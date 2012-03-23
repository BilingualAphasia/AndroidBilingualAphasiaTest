package ca.ilanguage.bilingualaphasiatest.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.ilanguage.bilingualaphasiatest.R;
import ca.ilanguage.bilingualaphasiatest.content.BilingualAphasiaTest;
import ca.ilanguage.oprime.storybook.StoryBookSubExperiment;
import ca.ilanguage.oprime.content.OPrime;
import ca.ilanguage.oprime.content.Stimulus;
import ca.ilanguage.oprime.datacollection.VideoRecorderSubExperiment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class BilingualAphasiaTestHome extends Activity {
	protected static final String TAG = "BilingualAphasiaTest";
	public static final boolean D = true;
	private WebView mWebView;
	private Handler mHandlerDelayStimuli = new Handler();
	
	private int mCurrentSubex = 0;
	private BilingualAphasiaTest app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_webview);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		mWebView.setWebChromeClient(new MyWebChromeClient());
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setUserAgentString(webSettings.getUserAgentString() + " "
				+ getString(R.string.user_agent_suffix));
		mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
		app = (BilingualAphasiaTest) getApplication();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//stopVideoRecorder();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	public class JavaScriptInterface {

		Context mContext;

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;

		}
		public void launchSubExperimentJS(final int subex){
			mCurrentSubex = subex;
			startVideoRecorder();
			/*
			 * Wait two seconds so that the video activity has time to load the
			 * camera. It will continue recording until you exit the video
			 * activity.
			 */
			mHandlerDelayStimuli.postDelayed(new Runnable() {
				public void run() {
					Toast.makeText(mContext, "Launching subexperiment "+mCurrentSubex, Toast.LENGTH_LONG).show();
					Intent intent;
					intent = new Intent(getApplicationContext(), StoryBookSubExperiment.class);
					ArrayList<Stimulus> stimuli = ((BilingualAphasiaTest) getApplication()).subExperiments.get(mCurrentSubex).getStimuli();
					intent.putExtra(OPrime.EXTRA_STIMULI_IMAGE_ID, stimuli);
					intent.putExtra(OPrime.EXTRA_LANGUAGE, ((BilingualAphasiaTest) getApplication()).getLanguage().getLanguage());

					startActivityForResult(intent, OPrime.EXPERIMENT_COMPLETED);
				}
			}, 2000);
		}
		public String fetchSubExperimentsArrayJS(){
			return ((BilingualAphasiaTest) getApplication()).getSubExperimentTitles().toString();
		}
		public String fetchParticipantCodesJS(){
			return "[the,codes]";
		}
		public String fetchExperimentTitleJS(){
			return ((BilingualAphasiaTest) getApplication()).getExperiment().getTitle();// ((RoogleTankApp) getApplication()).getLastMessage(); 
		}
		public void setAutoAdvanceJS(String autoadvance){
			Toast.makeText(mContext, "Set autoadvance to "+autoadvance, Toast.LENGTH_LONG).show();
			
		}
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
		}

		public String getVersionJIS(){
			String versionName;
			try {
				versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				Log.d(TAG, "Exception trying to get app version");
				return "";
			}
			return versionName;
		}
		public int getDforDebuggingJIS(){
			if(D){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	class MyWebChromeClient extends WebChromeClient {
		public boolean onConsoleMessage(ConsoleMessage cm) {
			if(D) Log.d(TAG, cm.message() + " -- From line " + cm.lineNumber() + " of "
					+ cm.sourceId());
			return true;
		}
	}
	private void startVideoRecorder() {
		final boolean fileManagerAvailable = isIntentAvailable(this,
				OPrime.INTENT_START_VIDEO_RECORDING);
		if (!fileManagerAvailable) {
			Toast.makeText(
					getApplicationContext(),
					"To record participant video you can install the "
							+ "OPrime Android Experimentation App, it allows your tablet to record video "
							+ "in the background and save it to the SDCARD.",
					Toast.LENGTH_LONG).show();
			Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri
					.parse("market://details?id=ca.ilanguage.oprime.android"));
		} else {

			String outputDir = ((BilingualAphasiaTest) getApplication()).getOutputDir();
			new File(outputDir).mkdirs();

			Intent intent;
			intent = new Intent(OPrime.INTENT_START_VIDEO_RECORDING);
			intent.putExtra(VideoRecorderSubExperiment.EXTRA_VIDEO_QUALITY, VideoRecorderSubExperiment.DEFAULT_DEBUGGING_QUALITY);
			intent.putExtra(VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA, true);
			String mDateString = (String) android.text.format.DateFormat.format("yyyy-MM-dd_kk.mm", new java.util.Date());
		      mDateString = mDateString.replaceAll("/","-").replaceAll(" ","-");
		    
			String resultsFile = outputDir+mDateString+"_"+System.currentTimeMillis() ;
			intent.putExtra(OPrime.EXTRA_RESULT_FILENAME, resultsFile+ ".3gp");
			app.subExperiments.get(mCurrentSubex).setResultsFileWithoutSuffix(resultsFile);
			
			startActivity(intent);
		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case OPrime.EXPERIMENT_COMPLETED:
			if(data != null){
				ArrayList<Stimulus> s =  (ArrayList<Stimulus>) data.getExtras().getSerializable(OPrime.EXTRA_STIMULI);
				app.subExperiments.get(mCurrentSubex).setStimuli(s);
				Intent i = new Intent(OPrime.INTENT_SAVE_SUB_EXPERIMENT_JSON);
				i.putExtra(OPrime.EXTRA_SUB_EXPERIMENT, (Serializable) app.subExperiments.get(mCurrentSubex) );
				startService(i); 
			}
			
			stopVideoRecorder();
			break;
		default:
			break;
		}
	}
	private void stopVideoRecorder(){
		Intent i = new Intent(OPrime.INTENT_STOP_VIDEO_RECORDING);
        sendBroadcast(i);
        Toast.makeText(this, "Subexperiment complete. ", Toast.LENGTH_LONG).show();
	}
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
	        PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
}

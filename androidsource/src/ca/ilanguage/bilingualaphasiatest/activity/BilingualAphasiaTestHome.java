package ca.ilanguage.bilingualaphasiatest.activity;

import ca.ilanguage.bilingualaphasiatest.R;
import ca.ilanguage.bilingualaphasiatest.content.BilingualAphasiaTest;
import ca.ilanguage.oprime.storybook.StoryBookSubExperiment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BilingualAphasiaTestHome extends Activity {
	protected static final String TAG = "BilingualAphasiaTest";
	public static final boolean D = true;
	private WebView mWebView;
	
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
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		public void launchSubExperimentJS(int subex){
			Toast.makeText(mContext, "Launching subexperiment "+subex, Toast.LENGTH_LONG).show();
			Intent intent;
			intent = new Intent(getApplicationContext(), StoryBookSubExperiment.class);
			startActivity(intent);
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


	
	

}

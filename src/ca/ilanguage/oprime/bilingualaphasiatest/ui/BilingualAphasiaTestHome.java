package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import ca.ilanguage.oprime.bilingualaphasiatest.R;
import ca.ilanguage.oprime.bilingualaphasiatest.preferences.SetPreferencesActivity;

public class BilingualAphasiaTestHome extends Activity {
	private WebView mWebView;
	private int mSubExperiments = 5;
	private static final int ENGLISH = 0;
	private static final int FRENCH = 1;
	public static final String LANGUAGE ="language";
	public static final String OUTPUT_DIRECTORY = "/sdcard/OPrime/BAT/results/";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_webview);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.addJavascriptInterface(new JavaScriptInterface(this),
				"Android");

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUserAgentString(webSettings.getUserAgentString() + " "
				+ getString(R.string.user_agent_suffix));

		new File(OUTPUT_DIRECTORY).mkdirs();
		
		
		mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");

	}

	public class JavaScriptInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;

		}
		public void launchSubExperimentJS(int subExperimentId){
			//startActivity(new Intent(mContext, SynonymsSubExperiment.class));
			startActivity(new Intent(mContext, AccelerometerUIActivity.class));

			if(subExperimentId == 1){
				
			}else if(subExperimentId == 1){
				
			}else if(subExperimentId == 2){
				
			}else if(subExperimentId == 3){
				
			}else if(subExperimentId == 4){
				
			}else if(subExperimentId == 5){
				
			}else if(subExperimentId == 6){
				
			}else{
				
			}
			//startActivity(new Intent(mContext, BilingualAphasiaTestHome.class));
		}

		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}

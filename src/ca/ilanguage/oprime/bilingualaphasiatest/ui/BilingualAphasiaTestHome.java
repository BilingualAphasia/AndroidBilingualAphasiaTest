package ca.ilanguage.oprime.bilingualaphasiatest.ui;

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
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_webview);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.addJavascriptInterface(new JavaScriptInterface(this),
				"Android");
		//mWebView.loadUrl("file:///android_asset/google_lobster_font.woff");

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUserAgentString(webSettings.getUserAgentString() + " "
				+ getString(R.string.user_agent_suffix));

		
		
		mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");

	}

	public class JavaScriptInterface {
		Context mContext;

		/*
		 * TODO add some hooks in the javascript interface to the space tree to
		 * track user interaction with the tree, how often did they drag it,
		 * what is their prefered layout can set tehir prefered layout in the
		 * settings if that is a popular change in the draft tree.
		 */

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;

		}
		public void launchSubExperimentJS(int subExperimentId){
			startActivity(new Intent(mContext, SetPreferencesActivity.class));

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

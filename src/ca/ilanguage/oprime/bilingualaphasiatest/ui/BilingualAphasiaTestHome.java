package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
import ca.ilanguage.oprime.bilingualaphasiatest.service.AudioRecorderService;

public class BilingualAphasiaTestHome extends Activity {
	private WebView mWebView;
	//private int mSubExperiments = 5;
	public static final String ENGLISH = "en";
	public static final String FRENCH = "fr";
	public static final String EXTRA_LANGUAGE ="language";
	public static final String EXTRA_PARTICIPANT_ID ="participant";
	public static final String EXTRA_SUB_EXPERIMENT_TITLE = "subexperimenttitle";
	public static final String OUTPUT_DIRECTORY = "/sdcard/OPrime/BAT/video/";
	private ArrayList<String> mSubExperiments;
	private String mParticipantId = "0000en"; //day00,participantnumber00,firstlanguage
	private long mExperimentLaunch;
	private long mExperimentQuit;
	
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
		
		String subExperiments = "History of Bilingualism,English Background,Spontaneous Speech,Verbal Comprehension,Pointing,Simple and Semi-complex Commands,Complex Commands,Verbal Auditory Discrimination,Syntactic Comprehension,Semantic Categories,Synonyms,Antonyms,Grammaticality Judgement,Semantic Acceptability,Lexical Decision,Series,Verbal Fluency,Naming,Sentence Construction,Semantic Opposites,Derivational Morphology,Morphological Opposites,Description,Mental Arithmetic,Listening Comprehension,Reading,Copying,Dictation,Reading Comprehension for Words,Reading Comprehension for Sentences,Writing";
		mSubExperiments =  new ArrayList(Arrays.asList(subExperiments.split(",")));
		mExperimentLaunch = System.currentTimeMillis();
		mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");

	}

	public class JavaScriptInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;

		}
		public String fetchSubExperimentsArrayJS(){
			return mSubExperiments.toString();			
			
		}
		public void launchSubExperimentJS(int subExperimentId){
				Intent intent = new Intent(mContext, SubExperiment.class);
				// intent.setData(mUri);
				intent.putExtra(EXTRA_LANGUAGE,ENGLISH);
				intent.putExtra(EXTRA_PARTICIPANT_ID, mParticipantId);
				intent.putExtra(EXTRA_SUB_EXPERIMENT_TITLE, subExperimentId+mSubExperiments.get(subExperimentId));
				startActivity(intent);
				//startActivity(new Intent(mContext, AccelerometerUIActivity.class));
		}
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		mExperimentQuit=System.currentTimeMillis();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}

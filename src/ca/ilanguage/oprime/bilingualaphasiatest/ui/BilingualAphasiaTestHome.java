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
	private ArrayList<String> mSubExperimentTypes;
	private String mParticipantId = "0000en"; //day00,participantnumber00,firstlanguage
	private long mExperimentLaunch;
	private long mExperimentQuit;
	private int mCurrentSubExperiment = 0;
	private Boolean mAutoAdvance= false;
	private static final int AUTO_ADVANCE_NEXT_SUB_EXPERIMENT = 2;
	
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
		
		String subExperiments = 
			  "History of Bilingualism," +
				"English Background," +
				"Spontaneous Speech," +
				"Pointing," +
				"Simple and Semi-complex Commands," +
				"Complex Commands," +
				"Verbal Auditory Discrimination," +
				"Syntactic Comprehension," +
				"Semantic Categories," +
				"Synonyms," +
				"Antonyms," +
				"Grammaticality Judgement," +
				"Semantic Acceptability," +
				"Lexical Decision," +
				"Series," +
				"Verbal Fluency," +
				"Naming," +
				"Sentence Construction," +
				"Semantic Opposites," +
				"Derivational Morphology," +
				"Morphological Opposites," +
				"Description," +
				"Mental Arithmetic," +
				"Listening Comprehension," +
				"Reading," +
				"Copying," +
				"Dictation," +
				"Reading Comprehension for Words," +
				"Reading Comprehension for Sentences," +
				"Writing";
		/*
		 * frontvideo creates backup audio to be analysied with subtitles and annotations
		 * and records an array of touches and time touched. for scoring later video in video
		 */
		String subExperimentTypes = 
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"backvideo," +
			"frontvideo," +
			"frontvideo," +
			"5pictures:frontvideo," +
			"4pictures:frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"6pictures:frontvideo," +
			"frontvideo," +
			"frontvideo," +
			"readingpictures:frontvideo," +
			"writing:frontvideo:takepicture," +
			"writing:frontvideo:takepicture," +
			"picturepicture:frontvideo," +
			"picturepicture:frontvideo," +
			"writing:frontvideo:timer:takepicture";

		mSubExperiments =  new ArrayList(Arrays.asList(subExperiments.split(",")));
		mSubExperimentTypes =  new ArrayList(Arrays.asList(subExperimentTypes.split(",")));
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
			mCurrentSubExperiment=subExperimentId;
			if(subExperimentId==0){
				mAutoAdvance=true;
			}
			launchSubExperiment(subExperimentId);
		}
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
		}
	}
	public void launchSubExperiment(int subExperimentId){
		Intent intent;
		if(mSubExperimentTypes.get(subExperimentId).contains("frontvideo")){
			intent = new Intent(getApplicationContext(), VideoRecorderSubExperiment.class);
			intent.putExtra(VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA, true);
		}else if(mSubExperimentTypes.get(subExperimentId).contains("backvideo")){
			intent = new Intent(getApplicationContext(), VideoRecorderSubExperiment.class);
			intent.putExtra(VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA, false);
		}else if(mSubExperimentTypes.get(subExperimentId).contains("writing")){
			intent = new Intent(getApplicationContext(), AccelerometerUIActivity.class);
		}else if(mSubExperimentTypes.get(subExperimentId).contains("reading")){
			intent = new Intent(getApplicationContext(), AccelerometerUIActivity.class);
		}else{
			intent = new Intent(getApplicationContext(), AccelerometerUIActivity.class);
		}
		intent.putExtra(EXTRA_LANGUAGE,ENGLISH);
		intent.putExtra(EXTRA_PARTICIPANT_ID, mParticipantId);
		intent.putExtra(EXTRA_SUB_EXPERIMENT_TITLE, subExperimentId+mSubExperiments.get(subExperimentId));
		if(!mAutoAdvance){
			startActivity(intent);
		}else{
			startActivityForResult(intent, AUTO_ADVANCE_NEXT_SUB_EXPERIMENT);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AUTO_ADVANCE_NEXT_SUB_EXPERIMENT:
			mCurrentSubExperiment++;
			if (mCurrentSubExperiment >= mSubExperiments.size()){
				Toast.makeText(getApplicationContext(), "Experiment completed!", Toast.LENGTH_LONG).show();
			}else{
				//launchSubExperiment(mCurrentSubExperiment);
				mWebView.loadUrl("javascript:getPositionAsButton(0,0,"+mCurrentSubExperiment+")");
			}
			break;
		default:
			break;
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

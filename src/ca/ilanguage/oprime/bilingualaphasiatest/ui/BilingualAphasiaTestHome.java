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

public class BilingualAphasiaTestHome extends Activity {
	private WebView mWebView;
	//private int mSubExperiments = 5;
	public static final String ENGLISH = "en";
	public static final String FRENCH = "fr";
	public static final String EXTRA_LANGUAGE ="language";
	public static final String EXTRA_PARTICIPANT_ID ="participant";
	public static final String EXTRA_SUB_EXPERIMENT_TITLE = "subexperimenttitle";
	public static final String EXTRA_STIMULI = "stimuli";
	public static final String OUTPUT_DIRECTORY = "/sdcard/OPrime/BAT/video/";
	private ArrayList<String> mSubExperiments;
	private ArrayList<String> mSubExperimentTypes;
	private String mParticipantId = "0000en"; //day00,participantnumber00,firstlanguage
	private long mExperimentLaunch;
	private long mExperimentQuit;
	private int mCurrentSubExperiment = 0;
	private Boolean mAutoAdvance= false;
	private static final int AUTO_ADVANCE_NEXT_SUB_EXPERIMENT = 2;
	private Boolean devMode= true;
	
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
				"English Background," + //1
				"Spontaneous Speech," +
				"Pointing," + //3
				"Simple and Semi-complex Commands," +
				"Complex Commands," + //5
				"Verbal Auditory Discrimination," +
				"Syntactic Comprehension," + //7
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
		ArrayList<Integer> stimiliImages = new ArrayList<Integer>();
		if(subExperimentId == 6 ){
			stimiliImages.add(R.drawable.x);
			stimiliImages.add(R.drawable.e048_0);
			stimiliImages.add(R.drawable.androids_experimenter_kids);
		}else{
//			stimiliImages.add(R.drawable.e048);
//			stimiliImages.add(R.drawable.e048_0);
//			stimiliImages.add(R.drawable.e049);
//			stimiliImages.add(R.drawable.e050);
//			stimiliImages.add(R.drawable.e051);
//			stimiliImages.add(R.drawable.e052);
//			stimiliImages.add(R.drawable.e053);
//			stimiliImages.add(R.drawable.e054);
//			stimiliImages.add(R.drawable.e055);
//			stimiliImages.add(R.drawable.e056);
//			stimiliImages.add(R.drawable.e057);
//			stimiliImages.add(R.drawable.e058);
//			stimiliImages.add(R.drawable.e059);
//			stimiliImages.add(R.drawable.e060);
//			stimiliImages.add(R.drawable.e061);
//			stimiliImages.add(R.drawable.e062);
//			stimiliImages.add(R.drawable.e063);
//			stimiliImages.add(R.drawable.e064);
//			stimiliImages.add(R.drawable.e065);
//			stimiliImages.add(R.drawable.e066_0);
//			stimiliImages.add(R.drawable.e066);
//			stimiliImages.add(R.drawable.e071);
//			stimiliImages.add(R.drawable.e077);
//			stimiliImages.add(R.drawable.e081);
//			stimiliImages.add(R.drawable.e089);
//			stimiliImages.add(R.drawable.e097);
//			stimiliImages.add(R.drawable.e105);
//			stimiliImages.add(R.drawable.e111);
//			stimiliImages.add(R.drawable.e115);
//			stimiliImages.add(R.drawable.e121);
//			stimiliImages.add(R.drawable.e125);
//			stimiliImages.add(R.drawable.e129);
//			stimiliImages.add(R.drawable.e133);
//			stimiliImages.add(R.drawable.e137);
//			stimiliImages.add(R.drawable.e138);
//			stimiliImages.add(R.drawable.e139);
//			stimiliImages.add(R.drawable.e140);
//			stimiliImages.add(R.drawable.e141);
//			stimiliImages.add(R.drawable.e142);
//			stimiliImages.add(R.drawable.e143);
//			stimiliImages.add(R.drawable.e144);
//			stimiliImages.add(R.drawable.e145);
//			stimiliImages.add(R.drawable.e146);
//			stimiliImages.add(R.drawable.e147);
//			stimiliImages.add(R.drawable.e148);
//			stimiliImages.add(R.drawable.e149);
//			stimiliImages.add(R.drawable.e150);
//			stimiliImages.add(R.drawable.e151);
//			stimiliImages.add(R.drawable.e152);
//			stimiliImages.add(R.drawable.e344);
//			stimiliImages.add(R.drawable.e367);
//			stimiliImages.add(R.drawable.e372);
//			stimiliImages.add(R.drawable.e377);
//			stimiliImages.add(R.drawable.e379);
//			stimiliImages.add(R.drawable.e381);
//			stimiliImages.add(R.drawable.e383);
//			stimiliImages.add(R.drawable.e385);
//			stimiliImages.add(R.drawable.e387_0);
//			stimiliImages.add(R.drawable.e393);
//			stimiliImages.add(R.drawable.e408);
//			stimiliImages.add(R.drawable.e408_0);
//			stimiliImages.add(R.drawable.e409);
//			stimiliImages.add(R.drawable.e409_0);
//			stimiliImages.add(R.drawable.e410);
//			stimiliImages.add(R.drawable.e410_0);
//			stimiliImages.add(R.drawable.e411);
//			stimiliImages.add(R.drawable.e411_0);
//			stimiliImages.add(R.drawable.e412);
//			stimiliImages.add(R.drawable.e412_0);
//			stimiliImages.add(R.drawable.e413);
//			stimiliImages.add(R.drawable.e413_0);
//			stimiliImages.add(R.drawable.e414);
//			stimiliImages.add(R.drawable.e414_0);
//			stimiliImages.add(R.drawable.e415);
//			stimiliImages.add(R.drawable.e415_0);
//			stimiliImages.add(R.drawable.e416);
//			stimiliImages.add(R.drawable.e416_0);
//			stimiliImages.add(R.drawable.e417);
//			stimiliImages.add(R.drawable.e417_0);
//			stimiliImages.add(R.drawable.e418);
//			stimiliImages.add(R.drawable.e418_0);
//			stimiliImages.add(R.drawable.e419);
//			stimiliImages.add(R.drawable.e419_0);
//			stimiliImages.add(R.drawable.e420);
//			stimiliImages.add(R.drawable.e420_0);
//			stimiliImages.add(R.drawable.e421);
//			stimiliImages.add(R.drawable.e421_0);
//			stimiliImages.add(R.drawable.e422);
//			stimiliImages.add(R.drawable.e422_0);
//			stimiliImages.add(R.drawable.e423);
//			stimiliImages.add(R.drawable.e423_0);
//			stimiliImages.add(R.drawable.e424);
//			stimiliImages.add(R.drawable.e424_0);
//			stimiliImages.add(R.drawable.e425);
//			stimiliImages.add(R.drawable.e425_0);
//			stimiliImages.add(R.drawable.e426);
//			stimiliImages.add(R.drawable.e426_0);
//			stimiliImages.add(R.drawable.e427);
//			stimiliImages.add(R.drawable.e427_0);
		}
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
		intent.putExtra(EXTRA_STIMULI, stimiliImages);
		intent.putExtra(EXTRA_LANGUAGE,ENGLISH);
		intent.putExtra(EXTRA_PARTICIPANT_ID, mParticipantId);
		intent.putExtra(EXTRA_SUB_EXPERIMENT_TITLE, subExperimentId+" "+mSubExperiments.get(subExperimentId));
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
			if (mCurrentSubExperiment >= mSubExperiments.size() ){
				Toast.makeText(getApplicationContext(), "Experiment completed!", Toast.LENGTH_LONG).show();
			}else{
				if(!devMode){
					//launchSubExperiment(mCurrentSubExperiment);
					mWebView.loadUrl("javascript:getPositionAsButton(0,0,"+mCurrentSubExperiment+")");
				}
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

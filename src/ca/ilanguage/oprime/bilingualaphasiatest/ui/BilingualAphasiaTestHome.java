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
	public static final String PARTICIPANT_ID_DEFAULT = "0000en";
	public static final String EXTRA_LANGUAGE ="language";
	public static final String EXTRA_PARTICIPANT_ID ="participant";
	public static final String EXTRA_SUB_EXPERIMENT_TITLE = "subexperimenttitle";
	public static final String EXTRA_STIMULI = "stimuli";
	public static final String OUTPUT_DIRECTORY = "/sdcard/OPrime/BAT/video/";
	private ArrayList<String> mSubExperiments;
	private ArrayList<String> mSubExperimentTypes;
	private String mParticipantId = PARTICIPANT_ID_DEFAULT; //day00,participantnumber00,firstlanguage
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
		ArrayList<Integer> stimuliImages = new ArrayList<Integer>();
		//stimuliImages.add(R.drawable.androids_experimenter_kids);
		
			if ("History of Bilingualism".equals(mSubExperiments.get(subExperimentId))){}
			if ("English Background".equals(mSubExperiments.get(subExperimentId))){}
			if ("Spontaneous Speech".equals(mSubExperiments.get(subExperimentId))){}
			if ("Pointing".equals(mSubExperiments.get(subExperimentId))){}
			if ("Simple and Semi-complex Commands".equals(mSubExperiments.get(subExperimentId))){}
			if ("Complex Commands".equals(mSubExperiments.get(subExperimentId))){}
			if ("Verbal Auditory Discrimination".equals(mSubExperiments.get(subExperimentId))){
				stimuliImages.add(R.drawable.e048_0);
				stimuliImages.add(R.drawable.e048);
				stimuliImages.add(R.drawable.e049);
				stimuliImages.add(R.drawable.e050);
				stimuliImages.add(R.drawable.e051);
				stimuliImages.add(R.drawable.e052);
				stimuliImages.add(R.drawable.e053);
				stimuliImages.add(R.drawable.e054);
				stimuliImages.add(R.drawable.e055);
				stimuliImages.add(R.drawable.e056);
				stimuliImages.add(R.drawable.e057);
				stimuliImages.add(R.drawable.e058);
				stimuliImages.add(R.drawable.e059);
				stimuliImages.add(R.drawable.e060);
				stimuliImages.add(R.drawable.e061);
				stimuliImages.add(R.drawable.e062);
				stimuliImages.add(R.drawable.e063);
				stimuliImages.add(R.drawable.e064);
				stimuliImages.add(R.drawable.e065);
			}
			if ("Syntactic Comprehension".equals(mSubExperiments.get(subExperimentId))){
				stimuliImages.add(R.drawable.e066_0);
				stimuliImages.add(R.drawable.e066);
				stimuliImages.add(R.drawable.e071);
				stimuliImages.add(R.drawable.e077);
				stimuliImages.add(R.drawable.e081);
				stimuliImages.add(R.drawable.e089);
				stimuliImages.add(R.drawable.e097);
				stimuliImages.add(R.drawable.e105);
				stimuliImages.add(R.drawable.e111);
				stimuliImages.add(R.drawable.e115);
				stimuliImages.add(R.drawable.e121);
				stimuliImages.add(R.drawable.e125);
				stimuliImages.add(R.drawable.e129);
				stimuliImages.add(R.drawable.e133);
				stimuliImages.add(R.drawable.e137_e145);
				stimuliImages.add(R.drawable.e138_e146);
				stimuliImages.add(R.drawable.e139_e147);
				stimuliImages.add(R.drawable.e140_e148);
				stimuliImages.add(R.drawable.e141_e149);
				stimuliImages.add(R.drawable.e142_e150);
				stimuliImages.add(R.drawable.e143_e151);
				stimuliImages.add(R.drawable.e144_e152);
				
				stimuliImages.add(R.drawable.e137_e145);
				stimuliImages.add(R.drawable.e138_e146);
				stimuliImages.add(R.drawable.e139_e147);
				stimuliImages.add(R.drawable.e140_e148);
				stimuliImages.add(R.drawable.e140_e148);
				stimuliImages.add(R.drawable.e142_e150);
				stimuliImages.add(R.drawable.e143_e151);
				stimuliImages.add(R.drawable.e144_e152);
			}
			if ("Semantic Categories".equals(mSubExperiments.get(subExperimentId))){}
			if ("Synonyms".equals(mSubExperiments.get(subExperimentId))){}
			if ("Antonyms".equals(mSubExperiments.get(subExperimentId))){}
			if ("Grammaticality Judgement".equals(mSubExperiments.get(subExperimentId))){}
			if ("Semantic Acceptability".equals(mSubExperiments.get(subExperimentId))){}
			if ("Lexical Decision".equals(mSubExperiments.get(subExperimentId))){}
			if ("Series".equals(mSubExperiments.get(subExperimentId))){}
			if ("Verbal Fluency".equals(mSubExperiments.get(subExperimentId))){}
			if ("Naming".equals(mSubExperiments.get(subExperimentId))){}
			if ("Sentence Construction".equals(mSubExperiments.get(subExperimentId))){}
			if ("Semantic Opposites".equals(mSubExperiments.get(subExperimentId))){}
			if ("Derivational Morphology".equals(mSubExperiments.get(subExperimentId))){}
			if ("Morphological Opposites".equals(mSubExperiments.get(subExperimentId))){}
			if ("Description".equals(mSubExperiments.get(subExperimentId))){
				stimuliImages.add(R.drawable.e344);
			}
			if ("Mental Arithmetic".equals(mSubExperiments.get(subExperimentId))){}
			if ("Listening Comprehension".equals(mSubExperiments.get(subExperimentId))){}
			if ("Reading".equals(mSubExperiments.get(subExperimentId))){
				stimuliImages.add(R.drawable.e367);
				stimuliImages.add(R.drawable.e372);
				stimuliImages.add(R.drawable.androids_experimenter_kids);
				stimuliImages.add(R.drawable.e377);
				stimuliImages.add(R.drawable.e379);
				stimuliImages.add(R.drawable.e381);
				stimuliImages.add(R.drawable.e383);
				stimuliImages.add(R.drawable.e385);
				stimuliImages.add(R.drawable.androids_experimenter_kids);
				stimuliImages.add(R.drawable.e387_0);
			}
			if ("Copying".equals(mSubExperiments.get(subExperimentId))){
				stimuliImages.add(R.drawable.e393);
			}
			if ("Dictation".equals(mSubExperiments.get(subExperimentId))){}
			if ("Reading Comprehension for Words".equals(mSubExperiments.get(subExperimentId))){
				stimuliImages.add(R.drawable.e408_0);
				stimuliImages.add(R.drawable.e408);
				stimuliImages.add(R.drawable.e409_0);
				stimuliImages.add(R.drawable.e409);
				stimuliImages.add(R.drawable.e410_0);
				stimuliImages.add(R.drawable.e410);
				stimuliImages.add(R.drawable.e411_0);
				stimuliImages.add(R.drawable.e411);
				stimuliImages.add(R.drawable.e412_0);
				stimuliImages.add(R.drawable.e412);
				stimuliImages.add(R.drawable.e413_0);
				stimuliImages.add(R.drawable.e413);
				stimuliImages.add(R.drawable.e414_0);
				stimuliImages.add(R.drawable.e414);
				stimuliImages.add(R.drawable.e415_0);
				stimuliImages.add(R.drawable.e415);
				stimuliImages.add(R.drawable.e416_0);
				stimuliImages.add(R.drawable.e416);
				stimuliImages.add(R.drawable.e417_0);
				stimuliImages.add(R.drawable.e417);
			}
			if ("Reading Comprehension for Sentences".equals(mSubExperiments.get(subExperimentId))){
				stimuliImages.add(R.drawable.e418_0);
				stimuliImages.add(R.drawable.e418);
				stimuliImages.add(R.drawable.e419_0);
				stimuliImages.add(R.drawable.e419);
				stimuliImages.add(R.drawable.e420_0);
				stimuliImages.add(R.drawable.e420);
				stimuliImages.add(R.drawable.e421_0);
				stimuliImages.add(R.drawable.e421);
				stimuliImages.add(R.drawable.e422_0);
				stimuliImages.add(R.drawable.e422);
				stimuliImages.add(R.drawable.e423_0);
				stimuliImages.add(R.drawable.e423);
				stimuliImages.add(R.drawable.e424_0);
				stimuliImages.add(R.drawable.e424);
				stimuliImages.add(R.drawable.e425_0);
				stimuliImages.add(R.drawable.e425);
				stimuliImages.add(R.drawable.e426_0);
				stimuliImages.add(R.drawable.e426);
				stimuliImages.add(R.drawable.e427_0);
				stimuliImages.add(R.drawable.e427);
			}
			if ("Writing".equals(mSubExperiments.get(subExperimentId))){}

		Intent intent;
		if(mSubExperimentTypes.get(subExperimentId).contains("frontvideo")){
//			intent = new Intent(getApplicationContext(), PresentStimuliActivity.class);//no video
			intent = new Intent(getApplicationContext(), VideoRecorderSubExperiment.class); //yes video
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
		intent.putExtra(EXTRA_STIMULI, stimuliImages);
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

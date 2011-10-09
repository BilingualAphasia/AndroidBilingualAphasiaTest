package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import ca.ilanguage.oprime.bilingualaphasiatest.R;
import ca.ilanguage.oprime.bilingualaphasiatest.preferences.PreferenceConstants;
import ca.ilanguage.oprime.bilingualaphasiatest.preferences.SetPreferencesActivity;

public class BilingualAphasiaTestHome extends Activity {
	private Menu mMenu;
	private WebView mWebView;
	//private int mSubExperiments = 5;
	public static final String ENGLISH = "en";
	public static final String FRENCH = "fr";
	public static final String PARTICIPANT_ID_DEFAULT = "0000en";
	public static final String EXTRA_LANGUAGE ="language";
	public static final String EXTRA_PARTICIPANT_ID ="participant";
	public static final String EXTRA_X_IMAGE = "xiamge";
	public static final String EXTRA_SUB_EXPERIMENT_TITLE = "subexperimenttitle";
	public static final String EXTRA_EXPERIMENT_TRIAL_INFORMATION = "experimenttrialinfo";
	public static final String EXTRA_RESULT_FILENAME = "resultfilename";
	public static final String EXTRA_STIMULI = "stimuli";
	public static final String EXTRA_TAKE_PICTURE_AT_END = "takepictureatend";
	public static final String EXTRA_REPLAY_PARTICIPANT_CODE = "replayparticipantcode";
	public static final String OUTPUT_DIRECTORY = "/sdcard/OPrime/BAT/video/";
	private String mExperimentTitle="";
	private ArrayList<String> mSubExperiments= new ArrayList<String>();
	private ArrayList<String> mSubExperimentTypes= new ArrayList<String>();
	private ArrayList<String> mSubExperimentParticipantVideos = new ArrayList<String>();
	private ArrayList<String> mParticipantsCodesCompleted = new ArrayList<String>();
	private String mParticipantId = PARTICIPANT_ID_DEFAULT; //day00,participantnumber00,firstlanguage
	private String mReplayParticipantId = "";
	private String mCurrentSubExperimentLanguage = ENGLISH;
	String mTabletOrPaperFirst = "T";
	private String mExperimentTrialHeader = "";
	private long mExperimentLaunch;
	private long mExperimentQuit;
	private int mCurrentSubExperiment = 0;
	private Boolean mReplayMode = false;
	private Boolean mReplayBySubExperiments = false;
	private Boolean mAutoAdvance= false; //if user clicks on start or History then it will automatically go into auto advance mode, unless dev mode is on. 
	private static final int AUTO_ADVANCE_NEXT_SUB_EXPERIMENT = 2;
	private static final int PREPARE_TRIAL = 0;
	private static final int SWITCH_LANGUAGE = 1;
	private static final int REPLAY_RESULTS = 3;
	
	String subExperiments ="";
	String subExperimentsFrench ="";
	String subExperimentTypes ="";
	/*
	 * List of things to do to run in testing mode:
	 * *devmode false(will run through all experiments iwth out stopping, maybebetter to keep devmode always true to alow for experimenter control
	 * *mSkipStimuli = false
	 * *mAutoAdvance true
	 * *VideoRecorderSubExperiment:
	 * * uncomment mVideoRecorder.setVideoEncodingBitRate(3000000)
	 */
	private Boolean devMode= false;
	private Boolean mSkipStimuli = false;
	
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
		mExperimentTitle = "Bilingual Aphasia Test - English";
		 subExperiments = 
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
				"Dictation for Words," +
				"Dictation for Sentences," +
				"Reading Comprehension for Words," +
				"Reading Comprehension for Sentences," +
				"Writing";
		 subExperimentsFrench = 
			  "Histoire du bilinguisme," +
				"Contexte d'apprentissage et d'utilisation du franais," + //1
				"Langage spontane," +
				"DŽsignation," + //3
				"Ordres simple et semi-complexes," +
				"Ordres complexes," + //5
				"Discrimination auditive verbale," +
				"ComprŽhension de structures syntaxiques," + //7
				"CompatibilitŽ sŽmantique," +
				"Synonyms," +
				"Antonyms," +
				"Jugement d'acceptabilitŽ," +
				"AcceptabilitŽ sŽmantique," +
				"DŽcision lexicale," +
				"SŽries," +
				"Fluence verbale," +
				"DŽnomination," +
				"Constuction de phrases," +
				"Contraires sŽmantiques," +
				"Morphologie," +
				"Contraires morphologiques," +
				"Description," +
				"Calcul mental," +
				"ComprŽhension auditive," +
				"Lecture," +// ˆ haute voix
				"Copie," +
				"DictŽe mots," +
				"DictŽe phrases," +
				"Lecture silencieuse des mots," +
				"Lecture silencieuse des phrases," +
				"ƒcriture spontanŽe";
		
		/*
		 * frontvideo creates backup audio to be analysied with subtitles and annotations
		 * and records an array of touches and time touched. for scoring later video in video
		 */
		 subExperimentTypes = 
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
			"writing:frontvideo:takepicture," +
			"picturepicture:frontvideo," +
			"picturepicture:frontvideo," +
			"writing:frontvideo:timer:takepicture";

		
		
		/*
		 * TODO get values from settings
		 * start settings activity
		 * on result read settings
		 */
		Intent setupIntent = new Intent(getBaseContext(),
				SetPreferencesActivity.class);
		startActivityForResult(setupIntent, PREPARE_TRIAL);
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
		public String fetchParticipantCodesJS(){
			if(!(mReplayMode && mParticipantsCodesCompleted != null)){
				return "";
			}else{
				return mParticipantsCodesCompleted.toString();
			}
		}
		public void setReplayParticipantCodeJS(String replayParticipantCode){
			if("Play All".equals(replayParticipantCode)){
				mReplayBySubExperiments = true;
				findVideosWithSubstring("_");//will get all videos
			}else{
				mReplayBySubExperiments = false;
				mReplayParticipantId = replayParticipantCode;
				findVideosWithSubstring(replayParticipantCode);
			}
		}
		public void setAutoAdvanceJS(String autoAdvance){
			if("1".equals(autoAdvance)){
				mAutoAdvance = true;
			}else if("0".equals(autoAdvance)){
				mAutoAdvance = false;
			}
		}
		public String fetchExperimentTitleJS(){
			return mExperimentTitle;
		}
		public void launchSubExperimentJS(int subExperimentId){
			mCurrentSubExperiment=subExperimentId;
			launchSubExperiment(subExperimentId);
		}
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
		}
	}

	public void replaySubExperiment(int subExperimentId){
		Intent intent;
		intent = new Intent(Intent.ACTION_VIEW); //"org.openintents.action.PICK_FILE"
		String thisSubExperimentSuffix = subExperimentId+"_"+mSubExperiments.get(subExperimentId);
		//process subexperimentsuffix the same as the way that files are created
		thisSubExperimentSuffix = thisSubExperimentSuffix.replaceAll(
				"[^\\w\\.\\-\\_]", "_");
		if (thisSubExperimentSuffix.length() >= 50) {
			thisSubExperimentSuffix = thisSubExperimentSuffix
					.substring(0, 49);
		}
		//String videofile=thisSubExperimentSuffix;
		ArrayList<String> videoFiles = new ArrayList<String>();
		for (int video=0;video < mSubExperimentParticipantVideos.size(); video++){
			String filefound = mSubExperimentParticipantVideos.get(video);
			
			//look for the last video made for this participant for this subexperiment
			if(filefound.contains(thisSubExperimentSuffix)){
				//videofile = "file:///mnt"+filefound;
				//break;
				videoFiles.add("file:///mnt"+filefound);
			}	
		}
		for(int j =0; j< videoFiles.size(); j++){
			if(videoFiles.get(j).startsWith("file")){
				intent.setDataAndType(Uri.parse(videoFiles.get(j)),"video/*");
				Toast.makeText(getApplicationContext(), 
		        		"Playing result video: "+videoFiles.get(j), Toast.LENGTH_LONG).show();
				if (!mAutoAdvance) {
					startActivity(intent);
				} else {
					startActivityForResult(intent, AUTO_ADVANCE_NEXT_SUB_EXPERIMENT);
				}
			}else{
				Toast.makeText(getApplicationContext(), 
		        		"No result video: "+videoFiles.get(j), Toast.LENGTH_LONG).show();
			}
		}
		
	}
	public void launchSubExperiment(int subExperimentId){
		if(mReplayMode){
			replaySubExperiment(subExperimentId);
			return;
		}
		
		Intent intent;
		ArrayList<Integer> stimuliImages = new ArrayList<Integer>();
		stimuliImages.add(R.drawable.androids_experimenter_kids);

		if (mSubExperimentTypes.get(subExperimentId).contains("frontvideo")) {
			// intent = new Intent(getApplicationContext(),
			// PresentStimuliActivity.class);//no video
			intent = new Intent(getApplicationContext(),
					VideoRecorderSubExperiment.class); // yes video
			intent.putExtra(
					VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA,
					true);
		} else if (mSubExperimentTypes.get(subExperimentId).contains(
				"backvideo")) {
			intent = new Intent(getApplicationContext(),
					VideoRecorderSubExperiment.class);
			intent.putExtra(
					VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA,
					false);
		} else if (mSubExperimentTypes.get(subExperimentId).contains("writing")) {
			intent = new Intent(getApplicationContext(),
					AccelerometerUIActivity.class);
		} else if (mSubExperimentTypes.get(subExperimentId).contains("reading")) {
			intent = new Intent(getApplicationContext(),
					AccelerometerUIActivity.class);
		} else {
			intent = new Intent(getApplicationContext(),
					AccelerometerUIActivity.class);
		}
		

		/*
		 * English sub experiments
		 */
		if ("History of Bilingualism".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("English Background".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Spontaneous Speech".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Pointing".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Simple and Semi-complex Commands".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Complex Commands".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Verbal Auditory Discrimination".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.e048_0);
			stimuliImages.add(R.drawable.e048_e408);
			stimuliImages.add(R.drawable.e049_e409);
			stimuliImages.add(R.drawable.ef050);
			stimuliImages.add(R.drawable.e051);
			stimuliImages.add(R.drawable.e052_e410);
			stimuliImages.add(R.drawable.e053_e411);
			stimuliImages.add(R.drawable.e054_e412);
			stimuliImages.add(R.drawable.e055);
			stimuliImages.add(R.drawable.e056);
			stimuliImages.add(R.drawable.ef057);
			stimuliImages.add(R.drawable.e058_e413);
			stimuliImages.add(R.drawable.e059_e414);
			stimuliImages.add(R.drawable.e060_e415);
			stimuliImages.add(R.drawable.ef061);
			stimuliImages.add(R.drawable.e062_e416);
			stimuliImages.add(R.drawable.e063);
			stimuliImages.add(R.drawable.e064_e417);
			stimuliImages.add(R.drawable.e065);
			intent.putExtra(EXTRA_X_IMAGE, R.drawable.x);
		}
		if ("Syntactic Comprehension".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.ef066_0);
			
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);

			stimuliImages.add(R.drawable.e071_e419);
			stimuliImages.add(R.drawable.e071_e419);
			stimuliImages.add(R.drawable.e071_e419);
			stimuliImages.add(R.drawable.e071_e419);
			stimuliImages.add(R.drawable.e071_e419);
			stimuliImages.add(R.drawable.e071_e419);

			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			
			stimuliImages.add(R.drawable.e111_f0111_to_f0114);
			stimuliImages.add(R.drawable.e111_f0111_to_f0114);
			stimuliImages.add(R.drawable.e111_f0111_to_f0114);
			stimuliImages.add(R.drawable.e111_f0111_to_f0114);
			
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			
			stimuliImages.add(R.drawable.e121_f0121_a_f0124);
			stimuliImages.add(R.drawable.e121_f0121_a_f0124);
			stimuliImages.add(R.drawable.e121_f0121_a_f0124);
			stimuliImages.add(R.drawable.e121_f0121_a_f0124);
			
			stimuliImages.add(R.drawable.e125_f0125_to_f0128);
			stimuliImages.add(R.drawable.e125_f0125_to_f0128);
			stimuliImages.add(R.drawable.e125_f0125_to_f0128);
			stimuliImages.add(R.drawable.e125_f0125_to_f0128);
			
			stimuliImages.add(R.drawable.e129_f0129_a_f0132);
			stimuliImages.add(R.drawable.e129_f0129_a_f0132);
			stimuliImages.add(R.drawable.e129_f0129_a_f0132);
			stimuliImages.add(R.drawable.e129_f0129_a_f0132);
			
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			
			stimuliImages.add(R.drawable.e137_e145);
			stimuliImages.add(R.drawable.e138_e146);
			stimuliImages.add(R.drawable.e139_e147);
			stimuliImages.add(R.drawable.e140_e148_f0137_et_f0145);
			stimuliImages.add(R.drawable.e141_e149);
			stimuliImages.add(R.drawable.e142_e150);
			stimuliImages.add(R.drawable.e143_e151);
			stimuliImages.add(R.drawable.e144_e152_f0141_et_f0149);

			stimuliImages.add(R.drawable.e137_e145);
			stimuliImages.add(R.drawable.e138_e146);
			stimuliImages.add(R.drawable.e139_e147);
			stimuliImages.add(R.drawable.e140_e148_f0137_et_f0145);
			stimuliImages.add(R.drawable.e141_e149);
			stimuliImages.add(R.drawable.e142_e150);
			stimuliImages.add(R.drawable.e143_e151);
			stimuliImages.add(R.drawable.e144_e152_f0141_et_f0149);
		}
		if ("Semantic Categories".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Synonyms".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Antonyms".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Grammaticality Judgement".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Semantic Acceptability".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Lexical Decision".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Series".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Verbal Fluency".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Naming".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Sentence Construction"
				.equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Semantic Opposites".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Derivational Morphology".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Morphological Opposites".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Description".equals(mSubExperiments.get(subExperimentId))) {
			stimuliImages.add(R.drawable.e344_f0344);
		}
		if ("Mental Arithmetic".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Listening Comprehension".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Reading".equals(mSubExperiments.get(subExperimentId))) {
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
			stimuliImages.add(R.drawable.androids_experimenter_kids);
			
		}
		if ("Copying".equals(mSubExperiments.get(subExperimentId))) {
			stimuliImages.add(R.drawable.e393);
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}
		if ("Dictation for Words".equals(mSubExperiments.get(subExperimentId))) {
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}
		if ("Dictation for Sentences".equals(mSubExperiments.get(subExperimentId))) {
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}
		if ("Reading Comprehension for Words".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.e408_0);
			stimuliImages.add(R.drawable.e048_e408);
			stimuliImages.add(R.drawable.e409_0);
			stimuliImages.add(R.drawable.e049_e409);
			stimuliImages.add(R.drawable.e410_0);
			stimuliImages.add(R.drawable.e052_e410);
			stimuliImages.add(R.drawable.e411_0);
			stimuliImages.add(R.drawable.e053_e411);
			stimuliImages.add(R.drawable.e412_0);
			stimuliImages.add(R.drawable.e054_e412);
			stimuliImages.add(R.drawable.e413_0);
			stimuliImages.add(R.drawable.e058_e413);
			stimuliImages.add(R.drawable.e414_0);
			stimuliImages.add(R.drawable.e059_e414);
			stimuliImages.add(R.drawable.e415_0);
			stimuliImages.add(R.drawable.e060_e415);
			stimuliImages.add(R.drawable.e416_0);
			stimuliImages.add(R.drawable.e062_e416);
			stimuliImages.add(R.drawable.e417_0);
			stimuliImages.add(R.drawable.e064_e417);
		}
		if ("Reading Comprehension for Sentences".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.e418_0);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e419_0);
			stimuliImages.add(R.drawable.e071_e419);
			stimuliImages.add(R.drawable.e420_0);
			stimuliImages.add(R.drawable.ef420);
			stimuliImages.add(R.drawable.e421_0);
			stimuliImages.add(R.drawable.ef421);
			stimuliImages.add(R.drawable.e422_0);
			stimuliImages.add(R.drawable.ef422);
			stimuliImages.add(R.drawable.e423_0);
			stimuliImages.add(R.drawable.ef423);
			stimuliImages.add(R.drawable.e424_0);
			stimuliImages.add(R.drawable.ef424);
			stimuliImages.add(R.drawable.e425_0);
			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			stimuliImages.add(R.drawable.e426_0);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e427_0);
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			
		}
		if ("Writing".equals(mSubExperiments.get(subExperimentId))) {
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}

		/*
		 * French Sub Experiments
		 *  "Histoire du bilinguisme," +
				"Contexte d'apprentissage et d'utilisation du franais," + //1
				"Langage spontane," +
				"DŽsignation," + //3
				"Ordres simple et semi-complexes," +
				"Ordres complexes," + //5
				"Discrimination auditive verbale," +
				"ComprŽhension de structures syntaxiques," + //7
				"CompatibilitŽ sŽmantique," +
				"Synonyms," +
				"Antonyms," +
				"Jugement d'acceptabilitŽ," +
				"AcceptabilitŽ sŽmantique," +
				"DŽcision lexicale," +
				"SŽries," +
				"Fluence verbale," +
				"DŽnomination," +
				"Constuction de phrases," +
				"Contraires sŽmantiques," +
				"Morphologie," +
				"Contraires morphologiques," +
				"Description," +
				"Calcul mental," +
				"ComprŽhension auditive," +
				"Lecture," +// ˆ haute voix
				"Copie," +
				"DictŽe mots," +
				"DictŽe phrases," +
				"Lecture silencieuse des mots," +
				"Lecture silencieuse des phrases," +
				"ƒcriture spontanŽe";
		 */

		if ("Contexte d'apprentissage et d'utilisation du franais".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Contexte d'apprentissage et d'utilisation du franais".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Langage spontane".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("DŽsignation".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Ordres simple et semi-complexes".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Ordres complexes".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Discrimination auditive verbale".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.f048_0_and_f413);
			stimuliImages.add(R.drawable.f048);
			stimuliImages.add(R.drawable.f049);
			stimuliImages.add(R.drawable.ef050);
			stimuliImages.add(R.drawable.f051);
			stimuliImages.add(R.drawable.f052_f410);
			stimuliImages.add(R.drawable.e053_e411);
			stimuliImages.add(R.drawable.f054_f411);
			stimuliImages.add(R.drawable.f055_f408);
			stimuliImages.add(R.drawable.f056_f414);
			stimuliImages.add(R.drawable.ef057);
			stimuliImages.add(R.drawable.f058_f417);
			stimuliImages.add(R.drawable.f059_f0409);
			stimuliImages.add(R.drawable.f060_f0416);
			stimuliImages.add(R.drawable.ef061);
			stimuliImages.add(R.drawable.f062_f0415);
			stimuliImages.add(R.drawable.f063);
			stimuliImages.add(R.drawable.f064);
			stimuliImages.add(R.drawable.f065);
			intent.putExtra(EXTRA_X_IMAGE, R.drawable.x);
		}
		if ("ComprŽhension de structures syntaxiques".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.ef066_0);
			
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);

			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);
			stimuliImages.add(R.drawable.e097_and_f071_to_f076);

			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			stimuliImages.add(R.drawable.e089_f089_a_f096);
			
			stimuliImages.add(R.drawable.f097_a_f104);
			stimuliImages.add(R.drawable.f097_a_f104);
			stimuliImages.add(R.drawable.f097_a_f104);
			stimuliImages.add(R.drawable.f097_a_f104);
			stimuliImages.add(R.drawable.f097_a_f104);
			stimuliImages.add(R.drawable.f097_a_f104);
			stimuliImages.add(R.drawable.f097_a_f104);
			stimuliImages.add(R.drawable.f097_a_f104);
			
			stimuliImages.add(R.drawable.f105_a_f110);
			stimuliImages.add(R.drawable.f105_a_f110);
			stimuliImages.add(R.drawable.f105_a_f110);
			stimuliImages.add(R.drawable.f105_a_f110);
			stimuliImages.add(R.drawable.f105_a_f110);
			stimuliImages.add(R.drawable.f105_a_f110);
			
			stimuliImages.add(R.drawable.e111_f0111_to_f0114);
			stimuliImages.add(R.drawable.e111_f0111_to_f0114);
			stimuliImages.add(R.drawable.e111_f0111_to_f0114);
			stimuliImages.add(R.drawable.e111_f0111_to_f0114);
			
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			stimuliImages.add(R.drawable.e115_f0115_to_f0120);
			
			stimuliImages.add(R.drawable.e121_f0121_a_f0124);
			stimuliImages.add(R.drawable.e121_f0121_a_f0124);
			stimuliImages.add(R.drawable.e121_f0121_a_f0124);
			stimuliImages.add(R.drawable.e121_f0121_a_f0124);
			
			stimuliImages.add(R.drawable.e125_f0125_to_f0128);
			stimuliImages.add(R.drawable.e125_f0125_to_f0128);
			stimuliImages.add(R.drawable.e125_f0125_to_f0128);
			stimuliImages.add(R.drawable.e125_f0125_to_f0128);
			
			stimuliImages.add(R.drawable.e129_f0129_a_f0132);
			stimuliImages.add(R.drawable.e129_f0129_a_f0132);
			stimuliImages.add(R.drawable.e129_f0129_a_f0132);
			stimuliImages.add(R.drawable.e129_f0129_a_f0132);
			
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			stimuliImages.add(R.drawable.e427_ef0133_to_ef0136);
			
			stimuliImages.add(R.drawable.e140_e148_f0137_et_f0145);
			stimuliImages.add(R.drawable.f138_et_f146);
			stimuliImages.add(R.drawable.f139_et_f147);
			stimuliImages.add(R.drawable.f140_et_f148);
			stimuliImages.add(R.drawable.e144_e152_f0141_et_f0149);
			stimuliImages.add(R.drawable.f142_et_f150);
			stimuliImages.add(R.drawable.f143_et_f151);
			stimuliImages.add(R.drawable.f144_et_f152);
			
			stimuliImages.add(R.drawable.e140_e148_f0137_et_f0145);
			stimuliImages.add(R.drawable.f138_et_f146);
			stimuliImages.add(R.drawable.f139_et_f147);
			stimuliImages.add(R.drawable.f140_et_f148);
			stimuliImages.add(R.drawable.e144_e152_f0141_et_f0149);
			stimuliImages.add(R.drawable.f142_et_f150);
			stimuliImages.add(R.drawable.f143_et_f151);
			stimuliImages.add(R.drawable.f144_et_f152);

		}
		if ("CompatibilitŽ sŽmantique".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Synonyms".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Antonyms".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Jugement d'acceptabilitŽ".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("AcceptabilitŽ sŽmantique".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("DŽcision lexicale".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("SŽries".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Fluence verbale".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("DŽnomination".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Constuction de phrases"
				.equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Contraires sŽmantiques".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("Morphologie".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Contraires morphologiques".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Description".equals(mSubExperiments.get(subExperimentId))) {
			stimuliImages.add(R.drawable.e344_f0344);
		}
		if ("Calcul mental".equals(mSubExperiments.get(subExperimentId))) {
		}
		if ("ComprŽhension auditive".equals(mSubExperiments
				.get(subExperimentId))) {
		}
		if ("Lecture".equals(mSubExperiments.get(subExperimentId))) {
			stimuliImages.add(R.drawable.f367_a_f371);
			stimuliImages.add(R.drawable.f372_a_f376);
			stimuliImages.add(R.drawable.androids_experimenter_kids);
			stimuliImages.add(R.drawable.f377_f378);
			stimuliImages.add(R.drawable.f379_et_f380);
			stimuliImages.add(R.drawable.f381_f382);
			stimuliImages.add(R.drawable.f383_f384);
			stimuliImages.add(R.drawable.f385_f386);
			stimuliImages.add(R.drawable.androids_experimenter_kids);
			stimuliImages.add(R.drawable.f387_a_f392);
			stimuliImages.add(R.drawable.androids_experimenter_kids);
			
		}
		if ("Copie".equals(mSubExperiments.get(subExperimentId))) {
			stimuliImages.add(R.drawable.f393_a_f397);
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}
		if ("DictŽe mots".equals(mSubExperiments.get(subExperimentId))) {
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}
		if ("DictŽe phrases".equals(mSubExperiments.get(subExperimentId))) {
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}
		if ("Lecture silencieuse des mots".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.f408_0);
			stimuliImages.add(R.drawable.f055_f408);
			stimuliImages.add(R.drawable.f409_0);
			stimuliImages.add(R.drawable.f059_f0409);
			stimuliImages.add(R.drawable.f410_0);
			stimuliImages.add(R.drawable.f052_f410);
			stimuliImages.add(R.drawable.f411_0);
			stimuliImages.add(R.drawable.f054_f411);
			stimuliImages.add(R.drawable.f412_0);
			stimuliImages.add(R.drawable.f412);
			stimuliImages.add(R.drawable.f413_0);
			stimuliImages.add(R.drawable.f048_0_and_f413);
			stimuliImages.add(R.drawable.f414_0);
			stimuliImages.add(R.drawable.f056_f414);
			stimuliImages.add(R.drawable.f415_0);
			stimuliImages.add(R.drawable.f062_f0415);
			stimuliImages.add(R.drawable.f416_0);
			stimuliImages.add(R.drawable.f060_f0416);
			stimuliImages.add(R.drawable.f417_0);
			stimuliImages.add(R.drawable.f058_f417);
		}
		if ("Lecture silencieuse des phrases".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.f418_0);
			stimuliImages.add(R.drawable.e066_and_f066_to_f070_ef418);
			stimuliImages.add(R.drawable.f419_0);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.f420_0);
			stimuliImages.add(R.drawable.ef420);
			stimuliImages.add(R.drawable.f421_0);
			stimuliImages.add(R.drawable.ef421);
			stimuliImages.add(R.drawable.f422_0);
			stimuliImages.add(R.drawable.ef422);
			stimuliImages.add(R.drawable.f423_0);
			stimuliImages.add(R.drawable.ef423);
			stimuliImages.add(R.drawable.f424_0);
			stimuliImages.add(R.drawable.ef424);
			stimuliImages.add(R.drawable.f425_0);
			stimuliImages.add(R.drawable.e077_and_f077_to_f080_ef425);
			stimuliImages.add(R.drawable.f426_0);
			stimuliImages.add(R.drawable.e081_and_f081_to_f088_ef426_f0419);
			stimuliImages.add(R.drawable.f427_0);
			stimuliImages.add(R.drawable.f427);
			
		}
		if ("ƒcriture spontanŽe".equals(mSubExperiments.get(subExperimentId))) {
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}
		/*end French sub experiments*/
		
		
		if(!mSkipStimuli){
			intent.putExtra(EXTRA_STIMULI, stimuliImages);
		}
		intent.putExtra(EXTRA_LANGUAGE, ENGLISH);
		intent.putExtra(EXTRA_PARTICIPANT_ID, mParticipantId);
		intent.putExtra(EXTRA_EXPERIMENT_TRIAL_INFORMATION, mExperimentTrialHeader);
		intent.putExtra(EXTRA_SUB_EXPERIMENT_TITLE, subExperimentId + " "
				+ mSubExperiments.get(subExperimentId));
		if (!mAutoAdvance) {
			startActivity(intent);
		} else {
			startActivityForResult(intent, AUTO_ADVANCE_NEXT_SUB_EXPERIMENT);
		}
	}
	private void initExperiment(){
		SharedPreferences prefs = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
		String firstname = prefs.getString(PreferenceConstants.PREFERENCE_PARTICIPANT_FIRSTNAME, "none");
		String lastname = prefs.getString(PreferenceConstants.PREFERENCE_PARTICIPANT_LASTNAME, "nobody");
		String experimenter = prefs.getString(PreferenceConstants.PREFERENCE_EXPERIEMENTER_CODE,"AA");
		String testDayNumber = prefs.getString(PreferenceConstants.PREFERENCE_TESTING_DAY_NUMBER,"0");
		String participantNumberOnDay = prefs.getString(PreferenceConstants.PREFERENCE_PARTICIPANT_NUMBER_IN_DAY,"0");
		mReplayMode = prefs.getBoolean(PreferenceConstants.PREFERENCE_REPLAY_RESULTS_MODE, false);
		if(mReplayMode){
			//findVideosWithSubstring(prefs.getString(PreferenceConstants.PREFERENCE_REPLAY_PARTICIPANT_CODE, ""));
			mReplayBySubExperiments =true;
			findParticipantCodesWithResults();
			//mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
		}
		mTabletOrPaperFirst = "T";
		if(prefs.getBoolean(PreferenceConstants.PREFERENCE_TABLET_FIRST, true)){
			
		}else{
			mTabletOrPaperFirst = "P";
		}
		String participantGroup= "E"; //participants worst language is English, so they get english first.
		mCurrentSubExperimentLanguage = ENGLISH;
		if(prefs.getBoolean(PreferenceConstants.PREFERENCE_PARTICIPANT_WORSTLANGUAGE_IS_ENGLISH, true)){
		}else{
			participantGroup ="F";
			mCurrentSubExperimentLanguage = FRENCH;
		}
		/*
		 * Build the participant ID and save the start time to the preferences. 
		 */
		mParticipantId = participantGroup+mTabletOrPaperFirst+testDayNumber+experimenter+participantNumberOnDay+firstname.substring(0,1)+lastname.substring(0,1);
		mExperimentLaunch = System.currentTimeMillis();
		mExperimentTrialHeader = "ParticipantID,FirstName,LastName,WorstLanguage,FirstBat,StartTime,EndTime,ExperimenterID" +
				":::==="+mParticipantId+","
				+firstname+","
				+lastname+","
				+participantGroup+","
				+mTabletOrPaperFirst+","
				+mExperimentLaunch+","
				+mExperimentQuit	;
		SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_ID,mParticipantId);
        editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_STARTTIME, mExperimentLaunch+"");
        editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_ENDTIME, mExperimentLaunch+"");
        editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_GROUP, participantGroup+mTabletOrPaperFirst);
        editor.commit();
       
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences prefens = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
		switch (requestCode) {
		case SWITCH_LANGUAGE:
			if(prefens.getBoolean(PreferenceConstants.PREFERENCE_PARTICIPANT_WORSTLANGUAGE_IS_ENGLISH, true)){
				mCurrentSubExperimentLanguage = ENGLISH;
			}else{
				mCurrentSubExperimentLanguage = FRENCH;
			}
			if(mCurrentSubExperimentLanguage.equals(ENGLISH)){
				mSubExperiments =  new ArrayList(Arrays.asList(subExperiments.split(",")));
				mExperimentTitle = "Bilingual Aphasia Test - English";
			}else{
				mSubExperiments =  new ArrayList(Arrays.asList(subExperimentsFrench.split(",")));
				mExperimentTitle = "Test de l'aphasie chez les bilingues - franais";
			}
			mSubExperimentTypes =  new ArrayList(Arrays.asList(subExperimentTypes.split(",")));
			mExperimentLaunch = System.currentTimeMillis();
			mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
			break;
		case REPLAY_RESULTS:
			mReplayMode = prefens.getBoolean(PreferenceConstants.PREFERENCE_REPLAY_RESULTS_MODE, false);
			//mReplayParticipantId ="ET1AM4";
			if(mReplayMode){
				mReplayBySubExperiments = true;
				findParticipantCodesWithResults();
				mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
			}else{
				mReplayBySubExperiments = false;
				mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
			}
			
			break;
		case PREPARE_TRIAL:
			initExperiment();
			if (mCurrentSubExperimentLanguage.equals(ENGLISH)) {
				mSubExperiments = new ArrayList(Arrays.asList(subExperiments
						.split(",")));
				mExperimentTitle = "Bilingual Aphasia Test - English";
				Toast.makeText(getApplicationContext(), "Experiment Trial is ready:\n\n" +
		        		"ParticipantCode: "+mParticipantId+"\n"+
		        		//"Trial start timestamp: "+mExperimentLaunch+"\n\n" +
		        				"Touch Start to take Participants Background Info...", Toast.LENGTH_LONG).show();
			} else {
				mSubExperiments = new ArrayList(
						Arrays.asList(subExperimentsFrench.split(",")));
				mExperimentTitle = "Test de l'aphasie chez les bilingues - franais";
				Toast.makeText(getApplicationContext(), "L'expŽrience est prt:\n\n" +
		        		"ParticipantCode: "+mParticipantId+"\n"+
		        		//"Trial start timesamp: "+mExperimentLaunch+"\n\n" +
        				"Touchez Commencer pour prendre l'histoire du participant ...", Toast.LENGTH_LONG).show();
			}
			mSubExperimentTypes = new ArrayList(
					Arrays.asList(subExperimentTypes.split(",")));
			mExperimentLaunch = System.currentTimeMillis();
			mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
	        
			
			break;
		case AUTO_ADVANCE_NEXT_SUB_EXPERIMENT:
			mCurrentSubExperiment++;
			if (mCurrentSubExperiment == 1 && mTabletOrPaperFirst.equals("P")){
				/*TODO make experiment pause so that experimenter can do the paper BAT*/
			}
			
			if (mCurrentSubExperiment >= mSubExperiments.size() ){
				if(mTabletOrPaperFirst.equals("T")){
					Toast.makeText(getApplicationContext(), "Tablet Experiment completed!\n\n Time for the Paper version.", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), "Experiment completed!", Toast.LENGTH_LONG).show();
				}
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
		/*
		 * TODO save participant details to outfile
		 */
		
		/*
		 * Reset the participant details from the settings so that they wont be saved on the next participant. 
		 */
		SharedPreferences prefs = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
		String participantNumber = prefs.getString(PreferenceConstants.PREFERENCE_PARTICIPANT_NUMBER_IN_DAY, "0");
		int participantInt = Integer.parseInt(participantNumber);
		participantInt++;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_NUMBER_IN_DAY, participantInt+"");
		editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_FIRSTNAME, "reset");
		editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_LASTNAME, "reset");
		editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_ID, "");
		editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_STARTTIME, "00");
        editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_ENDTIME, "00");
        editor.putString(PreferenceConstants.PREFERENCE_PARTICIPANT_GROUP, "");
        
        editor.commit();
		
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

	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// For "Title only": Examples of matching an ID with one assigned in
		// the XML
		case R.id.open_settings:

			Intent i = new Intent(getBaseContext(),
					SetPreferencesActivity.class);
			startActivity(i);
			return true;
		case R.id.language_settings:
			Intent inte = new Intent(getBaseContext(),
					SetPreferencesActivity.class);
			startActivityForResult(inte, SWITCH_LANGUAGE);
			return true;
		case R.id.result_folder:
//			final boolean fileManagerAvailable = isIntentAvailable(this,
//					"org.openintents.action.PICK_FILE");
//			if (!fileManagerAvailable) {
//				Toast.makeText(
//						getApplicationContext(),
//						"To open and export recorded files or "
//								+ "draft data you can install the OI File Manager, "
//								+ "it allows you to browse your SDCARD directly on your mobile device.",
//						Toast.LENGTH_LONG).show();
//				Intent goToMarket = new Intent(Intent.ACTION_VIEW)
//						.setData(Uri
//								.parse("market://details?id=org.openintents.filemanager"));
//			} else {
//				Intent openResults = new Intent(
//						"org.openintents.action.PICK_FILE");
//				openResults.setData(Uri.parse("file://"+OUTPUT_DIRECTORY));
//				startActivity(openResults);
//			}
			Intent intentReplay = new Intent(getBaseContext(),
					SetPreferencesActivity.class);
			startActivityForResult(intentReplay, REPLAY_RESULTS);			
			return true;
		case R.id.backup_results:
			Intent backupIntent = new Intent(getBaseContext(),
					SetPreferencesActivity.class);
			startActivity(backupIntent);
			return true;
		case R.id.issue_tracker:

			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://code.google.com/p/aublog/issues/entry"));
			startActivity(browserIntent);
			return true;
		default:
			// Do nothing

			break;
		}

		return false;
	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	public int findVideosWithSubstring(final String substring){
		if(substring.length() < 1 ){
			return 0;
		}
		mSubExperimentParticipantVideos = new ArrayList<String>();
		File dir = new File(OUTPUT_DIRECTORY);
	    FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.contains(substring) && name.endsWith("3gp"));
            }
        };
        File[] files = dir.listFiles(filter);
        if (files == null) {
            // Either dir does not exist or is not a directory
        	return 0;
        } else {
            for (int k = files.length - 1; k >= 0; k--) {
            	mSubExperimentParticipantVideos.add(files[k].toString());
            }
        }
        
        Toast.makeText(
				getApplicationContext(),
				mSubExperimentParticipantVideos.toString().replaceAll(OUTPUT_DIRECTORY, ""),
				Toast.LENGTH_LONG).show();
        return mSubExperimentParticipantVideos.size();

	}
	public String findParticipantCodesWithResults() {
		mParticipantsCodesCompleted = null;
		mParticipantsCodesCompleted = new ArrayList<String>();
		File dir = new File(BilingualAphasiaTestHome.OUTPUT_DIRECTORY);

		File[] files = dir.listFiles();
		if (files == null) {
			// Either dir does not exist or is not a directory
			return "" ;
		} else {
			mParticipantsCodesCompleted.add("Play All");
			for (int k = files.length - 1; k >= 0; k--) {
				String file = files[k].toString();
				String[] pieces = file.split("_");
				if(pieces != null && pieces.length > 1){
					if(mParticipantsCodesCompleted.contains(pieces[1])){
						//dont add it
					}else{
						mParticipantsCodesCompleted.add(pieces[1]);
					}
				}
			}
		}
//		CharSequence[] temp = mParticipantsCodesCompleted.toArray(new CharSequence[mParticipantsCodesCompleted.size()]);
//		int tempsize = temp.length;
		
		return mParticipantsCodesCompleted.toString();

	}

}

package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.File;
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
	public static final String OUTPUT_DIRECTORY = "/sdcard/OPrime/BAT/video/";
	private ArrayList<String> mSubExperiments;
	private ArrayList<String> mSubExperimentTypes;
	private String mParticipantId = PARTICIPANT_ID_DEFAULT; //day00,participantnumber00,firstlanguage
	private String mCurrentSubExperimentLanguage = ENGLISH;
	String mTabletOrPaperFirst = "T";
	private String mExperimentTrialHeader = "";
	private long mExperimentLaunch;
	private long mExperimentQuit;
	private int mCurrentSubExperiment = 0;
	private Boolean mAutoAdvance= false; //if user clicks on start or History then it will automatically go into auto advance mode, unless dev mode is on. 
	private static final int AUTO_ADVANCE_NEXT_SUB_EXPERIMENT = 2;
	private static final int PREPARE_TRIAL = 0;
	
	/*
	 * List of things to do to run in testing mode:
	 * *devmode false
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
				"Dictation for Words," +
				"Dictation for Sentences," +
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
			"writing:frontvideo:takepicture," +
			"picturepicture:frontvideo," +
			"picturepicture:frontvideo," +
			"writing:frontvideo:timer:takepicture";

		mSubExperiments =  new ArrayList(Arrays.asList(subExperiments.split(",")));
		mSubExperimentTypes =  new ArrayList(Arrays.asList(subExperimentTypes.split(",")));
		mExperimentLaunch = System.currentTimeMillis();
		mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
		
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
			intent.putExtra(EXTRA_X_IMAGE, R.drawable.x);
		}
		if ("Syntactic Comprehension".equals(mSubExperiments
				.get(subExperimentId))) {
			stimuliImages.add(R.drawable.e066_0);
			
			stimuliImages.add(R.drawable.e066);
			stimuliImages.add(R.drawable.e066);
			stimuliImages.add(R.drawable.e066);
			stimuliImages.add(R.drawable.e066);
			stimuliImages.add(R.drawable.e066);

			stimuliImages.add(R.drawable.e071);
			stimuliImages.add(R.drawable.e071);
			stimuliImages.add(R.drawable.e071);
			stimuliImages.add(R.drawable.e071);
			stimuliImages.add(R.drawable.e071);
			stimuliImages.add(R.drawable.e071);

			stimuliImages.add(R.drawable.e077);
			stimuliImages.add(R.drawable.e077);
			stimuliImages.add(R.drawable.e077);
			stimuliImages.add(R.drawable.e077);
			
			stimuliImages.add(R.drawable.e081);
			stimuliImages.add(R.drawable.e081);
			stimuliImages.add(R.drawable.e081);
			stimuliImages.add(R.drawable.e081);
			stimuliImages.add(R.drawable.e081);
			stimuliImages.add(R.drawable.e081);
			stimuliImages.add(R.drawable.e081);
			stimuliImages.add(R.drawable.e081);
			
			stimuliImages.add(R.drawable.e089);
			stimuliImages.add(R.drawable.e089);
			stimuliImages.add(R.drawable.e089);
			stimuliImages.add(R.drawable.e089);
			stimuliImages.add(R.drawable.e089);
			stimuliImages.add(R.drawable.e089);
			stimuliImages.add(R.drawable.e089);
			stimuliImages.add(R.drawable.e089);
			
			stimuliImages.add(R.drawable.e097);
			stimuliImages.add(R.drawable.e097);
			stimuliImages.add(R.drawable.e097);
			stimuliImages.add(R.drawable.e097);
			stimuliImages.add(R.drawable.e097);
			stimuliImages.add(R.drawable.e097);
			stimuliImages.add(R.drawable.e097);
			stimuliImages.add(R.drawable.e097);
			
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			stimuliImages.add(R.drawable.e105);
			
			stimuliImages.add(R.drawable.e111);
			stimuliImages.add(R.drawable.e111);
			stimuliImages.add(R.drawable.e111);
			stimuliImages.add(R.drawable.e111);
			
			stimuliImages.add(R.drawable.e115);
			stimuliImages.add(R.drawable.e115);
			stimuliImages.add(R.drawable.e115);
			stimuliImages.add(R.drawable.e115);
			stimuliImages.add(R.drawable.e115);
			stimuliImages.add(R.drawable.e115);
			
			stimuliImages.add(R.drawable.e121);
			stimuliImages.add(R.drawable.e121);
			stimuliImages.add(R.drawable.e121);
			stimuliImages.add(R.drawable.e121);
			
			stimuliImages.add(R.drawable.e125);
			stimuliImages.add(R.drawable.e125);
			stimuliImages.add(R.drawable.e125);
			stimuliImages.add(R.drawable.e125);
			
			stimuliImages.add(R.drawable.e129);
			stimuliImages.add(R.drawable.e129);
			stimuliImages.add(R.drawable.e129);
			stimuliImages.add(R.drawable.e129);
			
			stimuliImages.add(R.drawable.e133);
			stimuliImages.add(R.drawable.e133);
			stimuliImages.add(R.drawable.e133);
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
			stimuliImages.add(R.drawable.e344);
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
		if ("Reading Comprehension for Sentences".equals(mSubExperiments
				.get(subExperimentId))) {
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
			stimuliImages.add(R.drawable.androids_experimenter_kids); //paragraph hidden for questions
		}
		if ("Writing".equals(mSubExperiments.get(subExperimentId))) {
			intent.putExtra(EXTRA_TAKE_PICTURE_AT_END, true);
		}

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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PREPARE_TRIAL:
			SharedPreferences prefs = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
			String firstname = prefs.getString(PreferenceConstants.PREFERENCE_PARTICIPANT_FIRSTNAME, "none");
			String lastname = prefs.getString(PreferenceConstants.PREFERENCE_PARTICIPANT_LASTNAME, "nobody");
			String experimenter = prefs.getString(PreferenceConstants.PREFERENCE_EXPERIEMENTER_CODE,"AA");
			String testDayNumber = prefs.getString(PreferenceConstants.PREFERENCE_TESTING_DAY_NUMBER,"0");
			String participantNumberOnDay = prefs.getString(PreferenceConstants.PREFERENCE_PARTICIPANT_NUMBER_IN_DAY,"0");
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
	        editor.commit();
	       
	        if(mCurrentSubExperimentLanguage.equals(ENGLISH)){
		        Toast.makeText(getApplicationContext(), "Experiment Trial is ready:\n\n" +
		        		"ParticipantCode: "+mParticipantId+"\n"+
		        		"Trial start timestamp: "+mExperimentLaunch+"\n\n" +
		        				"Touch Start to take Participants Background Info...", Toast.LENGTH_LONG).show();
	        }else{
	        	Toast.makeText(getApplicationContext(), "L'expŽrience est prt:\n\n" +
		        		"ParticipantCode: "+mParticipantId+"\n"+
		        		"Trial start timesamp: "+mExperimentLaunch+"\n\n" +
        				"Touchez Commencer pour apprendre l'histoire du participant ...", Toast.LENGTH_LONG).show();
	        
	        }
			
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
			startActivity(inte);
			return true;
		case R.id.result_folder:
			final boolean fileManagerAvailable = isIntentAvailable(this,
					"org.openintents.action.PICK_FILE");
			if (!fileManagerAvailable) {
				Toast.makeText(
						getApplicationContext(),
						"To open and export recorded files or "
								+ "draft data you can install the OI File Manager, "
								+ "it allows you to browse your SDCARD directly on your mobile device.",
						Toast.LENGTH_LONG).show();
				Intent goToMarket = new Intent(Intent.ACTION_VIEW)
						.setData(Uri
								.parse("market://details?id=org.openintents.filemanager"));
			} else {
				Intent openResults = new Intent(
						"org.openintents.action.PICK_FILE");
				openResults.setData(Uri.parse("file://"+OUTPUT_DIRECTORY));
				startActivity(openResults);
			}
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
		      List<ResolveInfo> list =
		              packageManager.queryIntentActivities(intent,
		                      PackageManager.MATCH_DEFAULT_ONLY);
		      return list.size() > 0;
		  }

}

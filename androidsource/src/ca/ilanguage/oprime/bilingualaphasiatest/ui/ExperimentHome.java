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
import android.widget.ArrayAdapter;
import android.widget.Toast;
import ca.ilanguage.oprime.bilingualaphasiatest.*;
import ca.ilanguage.oprime.bilingualaphasiatest.preferences.PreferenceConstants;
import ca.ilanguage.oprime.bilingualaphasiatest.preferences.SetPreferencesActivity;
import ca.ilanguage.oprime.bilingualaphasiatest.ui.AccelerometerUIActivity;
import ca.ilanguage.oprime.domain.OPrime;

public class ExperimentHome extends Activity {
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
	public static final String OUTPUT_DIRECTORY = OPrime.OUTPUT_DIRECTORY;
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
	 *  comment out extra for video quality
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

//		if (mSubExperimentTypes.get(subExperimentId).contains("frontvideo")) {
//			// intent = new Intent(getApplicationContext(),
//			// PresentStimuliActivity.class);//no video
//			intent = new Intent(getApplicationContext(),
//					VideoRecorderSubExperiment.class); // yes video
//			intent.putExtra(
//					VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA,
//					true);
//		} else if (mSubExperimentTypes.get(subExperimentId).contains(
//				"backvideo")) {
//			intent = new Intent(getApplicationContext(),
//					VideoRecorderSubExperiment.class);
//			intent.putExtra(
//					VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA,
//					false);
//		} else if (mSubExperimentTypes.get(subExperimentId).contains("writing")) {
//			intent = new Intent(getApplicationContext(),
//					AccelerometerUIActivity.class);
//		} else if (mSubExperimentTypes.get(subExperimentId).contains("reading")) {
//			intent = new Intent(getApplicationContext(),
//					AccelerometerUIActivity.class);
//		} else {
			intent = new Intent(getApplicationContext(),
					AccelerometerUIActivity.class);
//		}
		

		
		
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
			findVideosWithSubstring("_");
			//mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
		}else{
			mReplayBySubExperiments =false;
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
	private void getSubExperimentTitles(){
		String[] subexperimentarray;
		
		subexperimentarray = getResources().getStringArray(R.array.subexperiment_titles);
		mSubExperiments =  new ArrayList(Arrays.asList(subexperimentarray));
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences prefens = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
		getSubExperimentTitles();
		switch (requestCode) {
		case SWITCH_LANGUAGE:
			if(prefens.getBoolean(PreferenceConstants.PREFERENCE_PARTICIPANT_WORSTLANGUAGE_IS_ENGLISH, true)){
				mCurrentSubExperimentLanguage = ENGLISH;
			}else{
				mCurrentSubExperimentLanguage = FRENCH;
			}
			if(mCurrentSubExperimentLanguage.equals(ENGLISH)){
				
				mExperimentTitle = "Bilingual Aphasia Test - English";
			}else{
				
				mExperimentTitle = "Test de l'aphasie chez les bilingues - français";
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
				findVideosWithSubstring("_");
				mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
			}else{
				mReplayBySubExperiments = false;
				mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
			}
			
			break;
		case PREPARE_TRIAL:
			initExperiment();
			if (mCurrentSubExperimentLanguage.equals(ENGLISH)) {
				
				mExperimentTitle = "Bilingual Aphasia Test - English";
				Toast.makeText(getApplicationContext(), "Experiment Trial is ready:\n\n" +
		        		"ParticipantCode: "+mParticipantId+"\n"+
		        		//"Trial start timestamp: "+mExperimentLaunch+"\n\n" +
		        				"Touch Start to take Participants Background Info...", Toast.LENGTH_LONG).show();
			} else {
				
				mExperimentTitle = "Test de l'aphasie chez les bilingues - fran�ais";
				Toast.makeText(getApplicationContext(), "L'exp�rience est pr�t:\n\n" +
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
					Uri.parse("https://github.com/iLanguage/AndroidBilingualAphasiaTest/issues/"));
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
        
//        Toast.makeText(
//				getApplicationContext(),
//				mSubExperimentParticipantVideos.toString().replaceAll(OUTPUT_DIRECTORY, ""),
//				Toast.LENGTH_LONG).show();
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
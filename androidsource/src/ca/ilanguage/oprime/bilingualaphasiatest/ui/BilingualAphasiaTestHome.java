package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import ca.ilanguage.oprime.domain.OPrime;
import ca.ilanguage.oprime.bilingualaphasiatest.R;
import ca.ilanguage.oprime.bilingualaphasiatest.preferences.PreferenceConstants;
import ca.ilanguage.oprime.bilingualaphasiatest.preferences.SetPreferencesActivity;


public class BilingualAphasiaTestHome extends Activity {
	private String mParticipantId = OPrime.PARTICIPANT_ID_DEFAULT;
	private String mExperimentTrialHeader = "";
	private Handler mHandlerDelayStimuli = new Handler();
	private Boolean mReplayMode = false;
	private Boolean mReplayBySubExperiments = false;
	private Boolean mDisplayPreparedToast = false;
	private String mCurrentSubExperimentLanguage = OPrime.ENGLISH;
	public long mExperimentLaunch;
	public long mExperimentQuit;
	public static final String OUTPUT_DIRECTORY = "/sdcard/OPrime/BilingualAphasiaTest/video/";

	public ArrayList<String> mSubExperimentParticipantVideos = new ArrayList<String>();
	public ArrayList<String> mParticipantsCodesCompleted = new ArrayList<String>();

	private Menu mMenu;
	private WebView mWebView;
	//private int mSubExperiments = 5;
	private String mExperimentTitle="";
	private ArrayList<String> mSubExperiments= new ArrayList<String>();
	private ArrayList<String> mSubExperimentTypes= new ArrayList<String>();
	private String mReplayParticipantId = "";
	String mTabletOrPaperFirst = "T";
	private int mCurrentSubExperiment = 0;
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
	public void onCreate(Bundle savedInstanceState) {
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

		
		if(savedInstanceState == null){
			Intent setupIntent = new Intent(getBaseContext(),
					SetPreferencesActivity.class);
			startActivityForResult(setupIntent, OPrime.PREPARE_TRIAL);

		}

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
			Toast.makeText(mContext, "Launching a sub experiment "+subExperimentId, Toast.LENGTH_LONG).show();
			//launchSubExperiment(subExperimentId);
		}
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
		}
	}


	public void replaySubExperiment(int subExperimentId){
//		Intent intent;
//		intent = new Intent(Intent.ACTION_VIEW); //"org.openintents.action.PICK_FILE"
//		String thisSubExperimentSuffix = subExperimentId+"_"+mSubExperiments.get(subExperimentId);
//		//process subexperimentsuffix the same as the way that files are created
//		thisSubExperimentSuffix = thisSubExperimentSuffix.replaceAll(
//				"[^\\w\\.\\-\\_]", "_");
//		if (thisSubExperimentSuffix.length() >= 50) {
//			thisSubExperimentSuffix = thisSubExperimentSuffix
//					.substring(0, 49);
//		}
//		//String videofile=thisSubExperimentSuffix;
//		ArrayList<String> videoFiles = new ArrayList<String>();
//		for (int video=0;video < mSubExperimentParticipantVideos.size(); video++){
//			String filefound = mSubExperimentParticipantVideos.get(video);
//			
//			//look for the last video made for this participant for this subexperiment
//			if(filefound.contains(thisSubExperimentSuffix)){
//				//videofile = "file:///mnt"+filefound;
//				//break;
//				videoFiles.add("file:///mnt"+filefound);
//			}	
//		}
//		for(int j =0; j< videoFiles.size(); j++){
//			if(videoFiles.get(j).startsWith("file")){
//				intent.setDataAndType(Uri.parse(videoFiles.get(j)),"video/*");
//				Toast.makeText(getApplicationContext(), 
//		        		"Playing result video: "+videoFiles.get(j), Toast.LENGTH_LONG).show();
//				if (!mAutoAdvance) {
//					startActivity(intent);
//				} else {
//					startActivityForResult(intent, AUTO_ADVANCE_NEXT_SUB_EXPERIMENT);
//				}
//			}else{
//				Toast.makeText(getApplicationContext(), 
//		        		"No result video: "+videoFiles.get(j), Toast.LENGTH_LONG).show();
//			}
//		}
//		
	}
	
	public void launchSubExperiment(int subExperimentId){
//		if(mReplayMode){
//			replaySubExperiment(subExperimentId);
//			return;
//		}
//		
//		Intent intent;
//		ArrayList<Integer> stimuliImages = new ArrayList<Integer>();
//		stimuliImages.add(R.drawable.androids_experimenter_kids);
//
////		if (mSubExperimentTypes.get(subExperimentId).contains("frontvideo")) {
////			// intent = new Intent(getApplicationContext(),
////			// PresentStimuliActivity.class);//no video
////			intent = new Intent(getApplicationContext(),
////					VideoRecorderSubExperiment.class); // yes video
////			intent.putExtra(
////					VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA,
////					true);
////		} else if (mSubExperimentTypes.get(subExperimentId).contains(
////				"backvideo")) {
////			intent = new Intent(getApplicationContext(),
////					VideoRecorderSubExperiment.class);
////			intent.putExtra(
////					VideoRecorderSubExperiment.EXTRA_USE_FRONT_FACING_CAMERA,
////					false);
////		} else if (mSubExperimentTypes.get(subExperimentId).contains("writing")) {
////			intent = new Intent(getApplicationContext(),
////					AccelerometerUIActivity.class);
////		} else if (mSubExperimentTypes.get(subExperimentId).contains("reading")) {
////			intent = new Intent(getApplicationContext(),
////					AccelerometerUIActivity.class);
////		} else {
//			intent = new Intent(getApplicationContext(),
//					AccelerometerUIActivity.class);
////		}
//		
//
//		
//		
//		if(!mSkipStimuli){
//			intent.putExtra(EXTRA_STIMULI, stimuliImages);
//		}
//		intent.putExtra(EXTRA_LANGUAGE, ENGLISH);
//		intent.putExtra(EXTRA_PARTICIPANT_ID, mParticipantId);
//		intent.putExtra(EXTRA_EXPERIMENT_TRIAL_INFORMATION, mExperimentTrialHeader);
//		intent.putExtra(EXTRA_SUB_EXPERIMENT_TITLE, subExperimentId + " "
//				+ mSubExperiments.get(subExperimentId));
//		if (!mAutoAdvance) {
//			startActivity(intent);
//		} else {
//			startActivityForResult(intent, AUTO_ADVANCE_NEXT_SUB_EXPERIMENT);
//		}
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
		
		setLocaleToExperimentLangauge();
		String participantGroup= "E";
		/*
		 * Build the participant ID and save the start time to the preferences. 
		 */
		mParticipantId = participantGroup+mTabletOrPaperFirst+testDayNumber+experimenter+participantNumberOnDay+firstname.substring(0,1).toUpperCase()+lastname.substring(0,1).toUpperCase();
		mExperimentLaunch = System.currentTimeMillis();
		mExperimentTrialHeader = getString(R.string.experiment_trial_header) +
				":::==="+mParticipantId+","
				+firstname+","
				+lastname+","
				+participantGroup+","
				+mTabletOrPaperFirst+","
				+mExperimentLaunch+","
				+mExperimentQuit  ;
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
		
		mExperimentTitle = getString(R.string.experiment_title);
		
		if (mDisplayPreparedToast){
			Toast.makeText(getApplicationContext(), getString(R.string.experiment_ready_message)+":\n\n" +
	        		"ParticipantCode: "+mParticipantId+"\n"+
	        		//"Trial start timestamp: "+mExperimentLaunch+"\n\n" +
	        				getString(R.string.experiment_start_instructions)+"...", Toast.LENGTH_LONG).show();
			mDisplayPreparedToast = false;
		}
		setTitle(getString(R.string.app_name));
	
	}


	private void setLocaleToExperimentLangauge(){
		SharedPreferences prefs = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
		
		mCurrentSubExperimentLanguage = prefs.getString(PreferenceConstants.PREFERENCE_EXPERIMENT_LANGUAGE, "en");
		String participantGroup=mCurrentSubExperimentLanguage.toUpperCase(); //participants worst language is English, so they get english first.

		//http://www.tutorialforandroid.com/2009/01/force-localize-application-on-android.html
		//String languageToLoad  = "fr";
		Locale locale = new Locale(mCurrentSubExperimentLanguage); 
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		/* This is the core part, here we only update the configuration of our basecontext's resources. I might not be right here but according to the description of Context, Interface to global information about an application environment. Getting the baseContext should be application level.
		 * 
		 */
		getBaseContext().getResources().updateConfiguration(config, 
				getBaseContext().getResources().getDisplayMetrics());

	}



	private void startVideoRecorder() {
		final boolean fileManagerAvailable = isIntentAvailable(this,
				"ca.ilanguage.oprime.intent.action.START_VIDEO_RECORDER");
		if (!fileManagerAvailable) {
			Toast.makeText(
					getApplicationContext(),
					getString(R.string.install_oprime),
							Toast.LENGTH_LONG).show();
			Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri
					.parse("market://details?id=ca.ilanguage.oprime.android"));
		} else {
			new File(OUTPUT_DIRECTORY).mkdirs();

			Intent intent;
			intent = new Intent(
					"ca.ilanguage.oprime.intent.action.START_VIDEO_RECORDER");

			intent.putExtra(OPrime.EXTRA_USE_FRONT_FACING_CAMERA, true);
			intent.putExtra(OPrime.EXTRA_LANGUAGE, OPrime.ENGLISH);
			intent.putExtra(OPrime.EXTRA_VIDEO_QUALITY, OPrime.DEFAULT_DEBUGGING_QUALITY);
			intent.putExtra(OPrime.EXTRA_PARTICIPANT_ID, mParticipantId);
			intent.putExtra(OPrime.EXTRA_OUTPUT_DIR, OUTPUT_DIRECTORY);
			intent.putExtra(OPrime.EXTRA_EXPERIMENT_TRIAL_INFORMATION,
					mExperimentTrialHeader);

			//startActivityForResult(intent, OPrime.EXPERIMENT_COMPLETED);
		}
	}

	private void stopVideoRecorder(){
		Intent i = new Intent("ca.ilanguage.oprime.intent.action.BROADCAST_STOP_VIDEO_SERVICE");
		sendBroadcast(i);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences prefens = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
		switch (requestCode) {
		case OPrime.EXPERIMENT_COMPLETED:
			break;
		case SWITCH_LANGUAGE:
			
			setLocaleToExperimentLangauge();

			

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
		case OPrime.PREPARE_TRIAL:
			initExperiment();
			mDisplayPreparedToast= true;
			mSubExperimentTypes = new ArrayList(
					Arrays.asList(subExperimentTypes.split(",")));
			mExperimentLaunch = System.currentTimeMillis();
			mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
	        
			startVideoRecorder();

			/*
			 * Wait two seconds so that the video activity has time to load the
			 * camera. It will continue recording until you exit the video
			 * activity.
			 */
//			mHandlerDelayStimuli.postDelayed(new Runnable() {
//				public void run() {
//					//launchExperiment();
//				}
//			}, 2000);
			break;
		case AUTO_ADVANCE_NEXT_SUB_EXPERIMENT:
			mCurrentSubExperiment++;
			
			if (mCurrentSubExperiment >= mSubExperiments.size() ){
				Toast.makeText(getApplicationContext(), getString(R.string.experiment_completed_message), Toast.LENGTH_LONG).show();
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
		getSubExperimentTitles();
		
	}
	@Override



	public void onDestroy(){
		//this shouldn't be needed.
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


		stopVideoRecorder();
		super.onDestroy();

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
//								Toast.LENGTH_LONG).show();
//				Intent goToMarket = new Intent(Intent.ACTION_VIEW)
//				.setData(Uri
//						.parse("market://details?id=org.openintents.filemanager"));
//			} else {
//				Intent openResults = new Intent(
//						"org.openintents.action.PICK_FILE");
//				openResults.setData(Uri.parse("file://"
//						+ OPrime.OUTPUT_DIRECTORY));
//				startActivity(openResults);
//			}
			Intent intentReplay = new Intent(getBaseContext(),
					SetPreferencesActivity.class);
			startActivityForResult(intentReplay, OPrime.REPLAY_RESULTS);
			return true;
		case R.id.backup_results:
			Intent backupIntent = new Intent(getBaseContext(),
					SetPreferencesActivity.class);
			startActivity(backupIntent);
			return true;
		case R.id.issue_tracker:

			Intent browserIntent = new Intent(
					Intent.ACTION_VIEW,
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
		File dir = new File(OPrime.OUTPUT_DIRECTORY);
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

		//	        Toast.makeText(
		//	        getApplicationContext(),
		//	        mSubExperimentParticipantVideos.toString().replaceAll(OUTPUT_DIRECTORY, ""),
		//	        Toast.LENGTH_LONG).show();
		return mSubExperimentParticipantVideos.size();

	}

	public String findParticipantCodesWithResults() {
		mParticipantsCodesCompleted = null;
		mParticipantsCodesCompleted = new ArrayList<String>();
		File dir = new File(OUTPUT_DIRECTORY);

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
		//	    CharSequence[] temp = mParticipantsCodesCompleted.toArray(new CharSequence[mParticipantsCodesCompleted.size()]);
		//	    int tempsize = temp.length;

		return mParticipantsCodesCompleted.toString();

	}

}


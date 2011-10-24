package ca.ilanguage.oprime.domain;

import java.util.ArrayList;

import android.view.Menu;
import android.webkit.WebView;

public class OPrime {
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

	private long mExperimentLaunch;
	private long mExperimentQuit;
	
	

	private static final int AUTO_ADVANCE_NEXT_SUB_EXPERIMENT = 2;
	private static final int PREPARE_TRIAL = 0;
	private static final int SWITCH_LANGUAGE = 1;
	private static final int REPLAY_RESULTS = 3;
	public static final int EXPERIMENT_COMPLETED =4;
	
	private ArrayList<String> mSubExperimentParticipantVideos = new ArrayList<String>();
	private ArrayList<String> mParticipantsCodesCompleted = new ArrayList<String>();

}


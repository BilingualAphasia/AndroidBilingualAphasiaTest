package ca.ilanguage.oprime.domain;

import android.view.Menu;
import android.webkit.WebView;

public class OPrime {
	public Menu mMenu;
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
	public static final String EXTRA_OUTPUT_DIR = "outputdir";
	public static final String EXTRA_REPLAY_PARTICIPANT_CODE = "replayparticipantcode";
	public static final String EXTRA_USE_FRONT_FACING_CAMERA = "usefrontcamera";
	public static final String EXTRA_STIMULI_IMAGE_ID = "stimuliimageid";
	public static final String EXTRA_VIDEO_QUALITY = "videoQuality";
	
	public static final int DEFAULT_DEBUGGING_QUALITY = 500000; //.5 megapixel
	public static final int DEFAULT_HIGH_QUALITY = 3000000;// 3 megapixel,
	public static final String OUTPUT_DIRECTORY = "/sdcard/OPrime/Results/video/";

	public static final String INTENT_STOP_VIDEO_RECORDING = "ca.ilanguage.oprime.intent.action.BROADCAST_STOP_VIDEO_SERVICE";
	
	

	public static final int AUTO_ADVANCE_NEXT_SUB_EXPERIMENT = 2;
	public static final int PREPARE_TRIAL = 0;
	public static final int SWITCH_LANGUAGE = 1;
	public static final int REPLAY_RESULTS = 3;
	public static final int EXPERIMENT_COMPLETED =4;
	
}


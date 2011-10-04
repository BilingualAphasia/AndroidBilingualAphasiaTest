package ca.ilanguage.oprime.bilingualaphasiatest.ui;


import java.util.ArrayList;

import ca.ilanguage.oprime.bilingualaphasiatest.R;
import ca.ilanguage.oprime.bilingualaphasiatest.service.AudioRecorderService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PresentStimuliActivity extends Activity {

	/*
	 * Sub experiment variables
	 */
	String mLanguageOfSubExperiment = BilingualAphasiaTestHome.ENGLISH;
	public static final String EXTRA_STIMULI_IMAGE_ID = "stimuliimageid";
	private static final String TAG = "PresentStimuliActivity";
	private String mParticipantId = BilingualAphasiaTestHome.PARTICIPANT_ID_DEFAULT;
	// private int mSubExperimentId = 0;
	private String mSubExperimentShortTitle = "";
	private String mSubExperimentTitle = "";
	String mAudioResultsFile = "";
	private ArrayList<Integer> mStimuliImages = new ArrayList<Integer>();;
	private ArrayList<String> mStimuliResponses = new ArrayList<String>();
	private ArrayList<Long> mReactionTimes = new ArrayList<Long>();;
	private int mStimuliIndex = 0;
	private Long mStartTime = System.currentTimeMillis();
	private Long mEndTime = System.currentTimeMillis();
	private ImageView mImage;

	/*
	 * Stimuli flow variables
	 */
	private static final int REWIND = 3;
	private static final int ADVANCE = 4;
	private static final int STIMULI_RESULT = 0;
	private int nextAction = ADVANCE;
	private Handler mHandlerDelayStimuli = new Handler();
	private Boolean mTouched = false;
	private Boolean mListeningForTouch = true; // sub experiment starts by user
												// touch
	private Boolean mfirstResponse = true;
	private Boolean mRewind = false;
	private Boolean mRewindHandled = false;
	/*
	 * Change these to fine tune experiment (rewindable, auto advance, display
	 * time)
	 */
	private Boolean mRewindable = false;
	private static Boolean mAdvanceByTouchOnly = true;
	private static int mWaitBetweenStimuli = 5;// wait between stimuli, if 999
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView image = (ImageView) findViewById(R.id.mainimage);
		image.setImageResource(R.drawable.androids_experimenter_kids);
		/*
		 * Get extras from the Experiment Home screen
		 */
		mStimuliImages = getIntent().getExtras().getIntegerArrayList(
				BilingualAphasiaTestHome.EXTRA_STIMULI);
		mParticipantId = getIntent().getExtras().getString(
				BilingualAphasiaTestHome.EXTRA_PARTICIPANT_ID);
		mLanguageOfSubExperiment = getIntent().getExtras().getString(
				BilingualAphasiaTestHome.EXTRA_LANGUAGE);
		mSubExperimentTitle = getIntent().getExtras().getString(
				BilingualAphasiaTestHome.EXTRA_SUB_EXPERIMENT_TITLE);
		mSubExperimentShortTitle = mSubExperimentTitle.replaceAll(
				"[^\\w\\.\\-\\_]", "_");
		if (mSubExperimentShortTitle.length() >= 50) {
			mSubExperimentShortTitle = mSubExperimentShortTitle
					.substring(0, 49);
		}
		this.setTitle(mSubExperimentTitle + "-" + mStimuliImages.size());

		
	}

	public boolean onTouchEvent(MotionEvent event) {
		// can use the xy of the touch to start and stop recording
		float positionX = event.getX();
		float positionY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// Screen is pressed for the first time
			break;
		case MotionEvent.ACTION_MOVE:
			// Screen is still pressed, float have been updated
			break;
		case MotionEvent.ACTION_UP:
			presentStimuli();
			break;
		}
		return super.onTouchEvent(event);
	}


	public void presentStimuli() {
		Intent intent;
		intent = new Intent(getApplicationContext(),
				SeeStimuliAndSpeakActivity.class);
		intent.putExtra(EXTRA_STIMULI_IMAGE_ID,
				mStimuliImages.get(mStimuliIndex));
		intent.putExtra(BilingualAphasiaTestHome.EXTRA_SUB_EXPERIMENT_TITLE,
				mSubExperimentTitle + " " + mStimuliIndex + "/"
						+ mStimuliImages.size());
		startActivityForResult(intent, STIMULI_RESULT);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case STIMULI_RESULT:
			advanceStimuli();
			break;
		default:
			break;
		}
	}

	/**
	 * This method is called after the response has been handled
	 */
	public void advanceStimuli() {
		mStimuliIndex++;
		// if the index is outside of the array of stimuli
		if (mStimuliIndex >= 5 || mStimuliIndex < 0) {
			writeResultsTable();
			finish();// end the sub experiment
			return;
		}
		presentStimuli();
	}

	public void writeResultsTable() {

	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
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

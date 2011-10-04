package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.util.ArrayList;

import ca.ilanguage.oprime.bilingualaphasiatest.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SeeStimuliAndSpeakActivity extends Activity {
	private static final String TAG = "PresentAnImageStimuli";
	
	/*
	 * Sub experiment variables
	 */
	String mLanguageOfSubExperiment = BilingualAphasiaTestHome.ENGLISH;
	private String mParticipantId = BilingualAphasiaTestHome.PARTICIPANT_ID_DEFAULT;
	// private int mSubExperimentId = 0;
	private String mSubExperimentShortTitle = "";
	private String mSubExperimentTitle = "";
	String mAudioResultsFile = "";
	private ArrayList<Integer> mStimuliImages  = new ArrayList<Integer>();;
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
		private int nextAction = ADVANCE;
		private Handler mHandlerDelayStimuli = new Handler();
		private Boolean mTouched = false;
		private Boolean mListeningForTouch = true;	//sub experiment starts by user touch
		private Boolean mfirstResponse = true;
		private Boolean mRewind = false;
		private Boolean mRewindHandled = false;
	  /*
	   * Change these to fine tune experiment (rewindable, auto advance, display time)
	   */
		private Boolean mRewindable = false;
		private static Boolean mAdvanceByTouchOnly = true;
		private static int mWaitBetweenStimuli = 5;// wait between stimuli, if 999 then wait until user input.

	
    
    //to have participant draw on images look at APIDemo>graphics>fingerprint
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.video_recorder);
	    mImage = (ImageView) findViewById(R.id.mainimage);
	    int stimuli =getIntent().getExtras().getInt(
				VideoRecorderSubExperiment.EXTRA_STIMULI_IMAGE_ID);
	    mImage.setImageResource(stimuli);
		mSubExperimentTitle = getIntent().getExtras().getString(
				BilingualAphasiaTestHome.EXTRA_SUB_EXPERIMENT_TITLE);
		this.setTitle(mSubExperimentTitle +"-"+ mStimuliImages.size());
	    mStartTime = System.currentTimeMillis();
    }
    /*
	 * If not using auto-advance, wait until user touches the screen.
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
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
		// Screen is not anymore touched
			// If touch is used to advance, and the app is listening for a touch
			if (mListeningForTouch) {
					getStimulusResponse(positionX, positionY);
					mTouched = true;
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	
	public void getStimulusResponse(float x, float y) {
	
			mEndTime = System.currentTimeMillis();
			Long reactionTime = mEndTime - mStartTime;
			mReactionTimes.add(mStimuliIndex, reactionTime);
			mStimuliResponses.add(mStimuliIndex,x+":::"+y);
			//advanceStimuli();
			finish();
	}
	
    
}

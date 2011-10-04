package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.util.ArrayList;

import ca.ilanguage.oprime.bilingualaphasiatest.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Android video recorder with "no" preview (the preview is a 1x1 pixel which
 * simulates an unobtrusive recording led). Based on Pro Android 2 2010 (Hashimi
 * et al) source code in Listing 9-6.
 * 
 * Also demonstrates how to use the front-facing and back-facing cameras. A
 * calling Intent can pass an Extra to use the front facing camera if available.
 * 
 * Suitable use cases: A: eye gaze tracking library to let users use eyes as a
 * mouse to navigate a web page B: use tablet camera(s) to replace video camera
 * in lab experiments (psycholingusitics or other experiments)
 * 
 * Video is recording is controlled in two ways: 1. Video starts and stops with
 * the activity 2. Video starts and stops on any touch
 * 
 * To control recording in other ways see the try blocks of the onTouchEvent
 * 
 * To incorporate into project add these features and permissions to
 * manifest.xml:
 * 
 * <uses-feature android:name="android.hardware.camera"/> <uses-feature
 * android:name="android.hardware.camera.autofocus"/>
 * 
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.CAMERA" /> <uses-permission
 * android:name="android.permission.RECORD_AUDIO" />
 * 
 * Tested Date: October 2 2011 with manifest.xml <uses-sdk
 * android:minSdkVersion="8" android:targetSdkVersion="11"/>
 */
public class VideoRecorderSubExperiment extends Activity implements
		SurfaceHolder.Callback {
	/*
	 * Recording variables
	 */
	public static final String EXTRA_USE_FRONT_FACING_CAMERA = "frontcamera";
	public static final String EXTRA_STIMULI_IMAGE_ID = "stimuliimageid";
	private static final String TAG = "VideoRecorderSubExperiment";
	private Boolean mRecording = false;
	private Boolean mUseFrontFacingCamera = false;
	private VideoView mVideoView = null;
	private MediaRecorder mVideoRecorder = null;
	private Camera mCamera;
	Context mContext;

	/*
	 * Sub experiment variables
	 */
	String mLanguageOfSubExperiment = BilingualAphasiaTestHome.ENGLISH;
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
	private Boolean mAutoAdvanceAfterWait = false;
	private int mWaitBetweenStimuli = 1;// wait between stimuli, if 999
												// then wait until user input.
	private Boolean mUseExternalStimuliActivity = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_recorder);
		mVideoView = (VideoView) this.findViewById(R.id.videoView);
		mImage = (ImageView) findViewById(R.id.mainimage);
		//mImage.setImageResource(R.drawable.androids_experimenter_kids);

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

		mUseFrontFacingCamera = getIntent().getExtras().getBoolean(
				EXTRA_USE_FRONT_FACING_CAMERA, true);
		if (mUseFrontFacingCamera) {
			// If caller wants to use front facing camera, then make sure the
			// device has one...
			// Hard coded to only open front facing camera on Xoom (model MZ604)
			// For more universal solution try:
			// http://stackoverflow.com/questions/2779002/how-to-open-front-camera-on-android-platform
			String deviceModel = android.os.Build.MODEL;
			if (deviceModel.contains("MZ604")) {
				mUseFrontFacingCamera = true;
			} else {
				// Toast.makeText(
				// getApplicationContext(),
				// "The App isn't designed to use this Android's front facing camera.\n "
				// +
				// "The device model is : " + deviceModel,
				// Toast.LENGTH_LONG).show();
				mUseFrontFacingCamera = false;
			}

		}

		final SurfaceHolder holder = mVideoView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		if (! mUseExternalStimuliActivity){
			presentStimuli();
		}

	}

	public boolean onTouchEvent(MotionEvent event) {
		// can use the xy of the touch to start and stop recording
		float positionX = event.getX();
		float positionY = event.getY();

		if (mListeningForTouch) {
			// if the touches are too close together, ignore it
			mEndTime = System.currentTimeMillis();
			long timeImageWasDisplayed = mEndTime - mStartTime;
			if (timeImageWasDisplayed > 300) {
				mListeningForTouch = false;
				if (mUseExternalStimuliActivity) {
					advanceStimuli();
				} else {
					getStimulusResponse(positionX, positionY);
				}
			}
		}
		return super.onTouchEvent(event);
	}

	public void presentStimuli() {
		mListeningForTouch =false;
		if(mUseExternalStimuliActivity){
			Intent intent;
			intent = new Intent(getApplicationContext(),
					SeeStimuliAndSpeakActivity.class);
			intent.putExtra(EXTRA_STIMULI_IMAGE_ID,
					mStimuliImages.get(mStimuliIndex));
			intent.putExtra(BilingualAphasiaTestHome.EXTRA_SUB_EXPERIMENT_TITLE,
					mSubExperimentTitle + " " + mStimuliIndex + "/"
							+ mStimuliImages.size());
			startActivityForResult(intent, STIMULI_RESULT);
		}else{
			mImage.setImageResource(mStimuliImages.get(mStimuliIndex));
			mStartTime = System.currentTimeMillis();
			mListeningForTouch = true;
		}
	}

	public void getStimulusResponse(float x, float y) {
		//mEndTime = System.currentTimeMillis();
		Long reactionTime = mEndTime - mStartTime;
		mReactionTimes.add(mStimuliIndex, reactionTime);
		mStimuliResponses.add(mStimuliIndex,x+":::"+y);
		advanceStimuli();
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
		if (mStimuliIndex >= mStimuliImages.size() || mStimuliIndex < 0) {
			writeResultsTable();
			finish();// end the sub experiment
			return;
		}
		if (mAutoAdvanceAfterWait){
			mHandlerDelayStimuli.postDelayed(new Runnable() {
				public void run() {
					presentStimuli();
				}
			}, mWaitBetweenStimuli * 1000);
		}else{
			presentStimuli();
		}
	}

	public void writeResultsTable() {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (mRecording) {
			return;
		}
		try {
			beginRecording(holder);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v(TAG, "Width x Height = " + width + "x" + height);
	}

	private void stopRecording() throws Exception {
		mRecording = false;
		if (mVideoRecorder != null) {
			mVideoRecorder.stop();
			mVideoRecorder.release();
			mVideoRecorder = null;
		}
		if (mCamera != null) {
			mCamera.reconnect();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		try {
			stopRecording();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
		super.onDestroy();

	}

	/**
	 * Uses the surface defined in video_recorder.xml Tested using 2.2 (HTC
	 * Desire/Hero phone) -> Use all defaults works, records back facing camera
	 * with AMR_NB audio 3.0 (Motorola Xoom tablet) -> Use all defaults doesn't
	 * work, works with these specs, might work with others
	 * 
	 * @param holder
	 *            The surfaceholder from the videoview of the layout
	 * @throws Exception
	 */
	private void beginRecording(SurfaceHolder holder) throws Exception {
		if (mVideoRecorder != null) {
			mVideoRecorder.stop();
			mVideoRecorder.release();
			mVideoRecorder = null;
		}
		if (mCamera != null) {
			mCamera.reconnect();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		mAudioResultsFile = BilingualAphasiaTestHome.OUTPUT_DIRECTORY
				+ System.currentTimeMillis() + "_" + mParticipantId + "_"
				+ mLanguageOfSubExperiment + mSubExperimentShortTitle + ".3gp";

		try {
			if (mUseFrontFacingCamera) {
				// hard coded assuming 1 is the front facing camera
				mCamera = Camera.open(1);
			} else {
				mCamera = Camera.open();
			}

			// Camera setup is based on the API Camera Preview demo
			mCamera.setPreviewDisplay(holder);
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(640, 480);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
			mCamera.unlock();

			mVideoRecorder = new MediaRecorder();
			mVideoRecorder.setCamera(mCamera);

			// Media recorder setup is based on Listing 9-6, Hashimi et all 2010
			// values based on best practices and good quality,
			// tested via upload to YouTube and played in QuickTime on Mac Snow
			// Leopard
			mVideoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mVideoRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mVideoRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);// THREE_GPP
																			// is
																			// big-endian,
																			// storing
																			// and
																			// transferring
																			// the
																			// most
																			// significant
																			// bytes
																			// first.
																			// MPEG_4
																			// as
																			// another
																			// option
			mVideoRecorder.setVideoSize(640, 480);// YouTube recommended size:
													// 320x240,
													// OpenGazer eye tracker:
													// 640x480
													// YouTube HD: 1280x720
			mVideoRecorder.setVideoFrameRate(20); // might be auto-determined
													// due to lighting
			// mVideoRecorder.setVideoEncodingBitRate(3000000);// 3 megapixel,
			// or the max of
			// the camera
			mVideoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// MPEG_4_SP
																			// Simple
																			// Profile
																			// is
																			// for
																			// low
																			// bit
																			// rate
																			// and
																			// low
																			// resolution
																			// H264
																			// is
																			// MPEG-4
																			// Part
																			// 10
																			// is
																			// commonly
																			// referred
																			// to
																			// as
																			// H.264
																			// or
																			// AVC
			int sdk = android.os.Build.VERSION.SDK_INT;
			// Gingerbread and up can have wide band ie 16,000 hz recordings
			// (Okay quality for human voice)
			if (sdk >= 10) {
				mVideoRecorder
						.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
				mVideoRecorder.setAudioSamplingRate(16000);
			} else {
				// Other devices only have narrow band, ie 8,000 hz
				// (Same quality as a phone call, not really good quality for
				// any purpose.
				// For human voice 8,000 hz means /f/ and /th/ are
				// indistinguishable)
				mVideoRecorder
						.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			}
			mVideoRecorder.setMaxDuration(600000); // limite to 10min 600,000limit to 30 seconds 30,000
			mVideoRecorder.setPreviewDisplay(holder.getSurface());
			mVideoRecorder.setOutputFile(mAudioResultsFile);
			mVideoRecorder.prepare();
			mVideoRecorder.start();
			mRecording = true;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}
}

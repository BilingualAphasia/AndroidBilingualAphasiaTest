package ca.ilanguage.oprime.bilingualaphasiatest.ui;


import ca.ilanguage.oprime.bilingualaphasiatest.R;


import java.io.File;
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.VideoView;

/**
 * Android video recorder with "no" preview (the preview is a 1x1 pixel which 
 * simulates an unobtrusive recording led). Based on Pro Android 2 2010 (Hashimi et al) 
 * source code in Listing 9-6. 
 * 
 * Suitable use cases:
 *  A: eye gaze tracking to let users use eyes to navigate a page
 *  B: use tablet camera(s) to replace video camera in lab experiments 
 *     (psycholingusitics or other experiments)
 * 
 * Video is recording is controlled in two ways:
 * 1. Video starts and stops with the activity
 * 2. Video starts and stops on any touch
 * 
 * To control recording in other ways see the try blocks of the onTouchEvent 
 * 
 * @author cesine
 *
 */
public class VideoRecorderSubExperiment extends Activity implements
		SurfaceHolder.Callback {

	private MediaRecorder recorder = null;
	private static final String OUTPUT_FILE = "/sdcard/videooutput";
	private static final String TAG = "RecordVideo";
	private VideoView videoView = null;
	private Boolean mRecording = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_recorder);

		videoView = (VideoView) this.findViewById(R.id.videoView);

		final SurfaceHolder holder = videoView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

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
			// Screen is not touched anymore
			if (mRecording) {
				// To stop recording attach this try block to another event listener,
				// button etc
				try {
					stopRecording();
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
				}
			} else {
				// To begin recording attach this try block to another event listener,
				// button etc
				try {
					beginRecording(videoView.getHolder());
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			beginRecording(holder);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.v(TAG, "Width x Height = " + width + "x" + height);
	}

	private void stopRecording() throws Exception {
		mRecording = false;
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
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

	private void beginRecording(SurfaceHolder holder) throws Exception {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}

		String uniqueOutFile = OUTPUT_FILE + System.currentTimeMillis() + ".mp4";
		File outFile = new File(uniqueOutFile);
		if (outFile.exists()) {
			outFile.delete();
		}

		try {
			recorder = new MediaRecorder();
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setVideoSize(640, 480);// YouTube recommended size 320x240,
																			// OpenGazer eye tracker: 640x480 mode.
			recorder.setVideoFrameRate(15);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setMaxDuration(30000); // limit to 30 seconds
			recorder.setPreviewDisplay(holder.getSurface());
			recorder.setOutputFile(uniqueOutFile);
			recorder.prepare();
			recorder.start();
			mRecording = true;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}
}

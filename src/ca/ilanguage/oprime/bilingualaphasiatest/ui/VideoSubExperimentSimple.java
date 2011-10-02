package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.IOException;

import ca.ilanguage.oprime.bilingualaphasiatest.R;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class VideoSubExperimentSimple extends Activity implements
		SurfaceHolder.Callback {

	String mLanguageOfSubExperiment;
	private String mParticipantId = "0000en";
	// private int mSubExperimentId = 0;
	private String mSubExperimentShortTitle = "";
	private String mSubExperimentTitle = "";
	String mAudioResultsFile;
	
	private static final String TAG = "CAMERA_TUTORIAL";

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean previewRunning;
	private boolean mRecording = false;
	
	 private MediaRecorder mediaRecorder;
		private final int maxDurationInMs = 20000;
		private final long maxFileSizeInBytes = 500000;
		private final int videoFramesPerSecond = 20;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		mParticipantId = getIntent().getExtras().getString(
				BilingualAphasiaTestHome.EXTRA_PARTICIPANT_ID);
		mLanguageOfSubExperiment = getIntent().getExtras().getString(
				BilingualAphasiaTestHome.EXTRA_LANGUAGE);
		mSubExperimentTitle = getIntent().getExtras().getString(
				BilingualAphasiaTestHome.EXTRA_SUB_EXPERIMENT_TITLE);
		mSubExperimentShortTitle = mSubExperimentTitle.replaceAll(
				"[^\\w\\.\\-\\_]", "_");
		if (mSubExperimentShortTitle.length() >= 50) {
			mSubExperimentShortTitle = mSubExperimentShortTitle.substring(0, 49);
		}
		mAudioResultsFile = BilingualAphasiaTestHome.OUTPUT_DIRECTORY
				+ System.currentTimeMillis() + "_" + mParticipantId + "_"
				+ mLanguageOfSubExperiment + mSubExperimentShortTitle + ".mp4";

		this.setTitle(mSubExperimentTitle);
		
		setContentView(R.layout.camera_surface);
		surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		
	}

	public boolean onTouchEvent(MotionEvent event) {
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
			if (mRecording){
				stopRecording();
				finish();
			}else{
				startRecording();
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		if (camera != null) {
			Camera.Parameters params = camera.getParameters();
			camera.setParameters(params);
		} else {
			Toast.makeText(getApplicationContext(), "Camera not available!",
					Toast.LENGTH_LONG).show();
			finish();
		}
	}
	public void stopRecording(){
		if(mediaRecorder != null){
			mediaRecorder.stop();
		}
		camera.lock();
	}
	public boolean startRecording(){
		try {
			camera.unlock();

			mediaRecorder = new MediaRecorder();

			mediaRecorder.setCamera(camera);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

			mediaRecorder.setMaxDuration(maxDurationInMs);

			
			mediaRecorder.setOutputFile(mAudioResultsFile);

			mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
			mediaRecorder.setVideoSize(surfaceView.getWidth(), surfaceView.getHeight());

			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

			mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

			mediaRecorder.setMaxFileSize(maxFileSizeInBytes);

                        mediaRecorder.prepare();
			mediaRecorder.start();

			return true;
		} catch (IllegalStateException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (previewRunning) {
			camera.stopPreview();
		}
		Camera.Parameters p = camera.getParameters();
		p.setPreviewSize(width, height);
		p.setPreviewFormat(PixelFormat.JPEG);
		camera.setParameters(p);

		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			previewRunning = true;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		previewRunning = false;
		camera.release();
	}
}
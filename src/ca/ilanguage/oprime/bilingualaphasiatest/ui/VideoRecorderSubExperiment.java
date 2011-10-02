package ca.ilanguage.oprime.bilingualaphasiatest.ui;


import ca.ilanguage.oprime.bilingualaphasiatest.R;


import java.io.File;
import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;
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
 * 
 * To incorporate into project add these features and permissions to manifest.xml:
 * <uses-sdk android:minSdkVersion="8" android:targetSdkVersion="11"/>
    
	<uses-feature android:name="android.hardware.camera"/>
	<uses-feature android:name="android.hardware.camera.autofocus"/>
	
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.CAMERA" />
	<uses-permission android:name="android.permission.RECORD_AUDIO" />
 * 
 * @author cesine
 *
 */
public class VideoRecorderSubExperiment extends Activity implements
		SurfaceHolder.Callback {

	private MediaRecorder recorder = null;
	private Camera mCamera;
	private static final String OUTPUT_FILE = "/sdcard/videooutput";
	private static final String TAG = "RecordVideo";
	private VideoView videoView = null;
	private Boolean mRecording = false;
	private Boolean useFrontFacingCamera = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_recorder);

		videoView = (VideoView) this.findViewById(R.id.videoView);

		
//		int cameraCount = 0;
//		cameraCount = Camera.getNumberOfCameras();
//		//Hard coded to only open front facing camera on xoom is model MZ604
		String deviceModel = android.os.Build.MODEL;
		if(deviceModel.contains("MZ604")){
			useFrontFacingCamera = true;
		}else{
			Toast.makeText(getApplicationContext(), "The App can't use the Front facing camera.\n The device model is : "+deviceModel, Toast.LENGTH_LONG).show();
			useFrontFacingCamera=false;
		}
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
		if(mCamera != null){
			mCamera.reconnect();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
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

	/**
	 * Uses the surface from video_recorder.xml
	 * Tested using
	 *  2.2 (HTC Desire/Hero phone) -> Use all defaults works, records forward camera with AMR_NB audio
	 *  3.0 (Motorola Xoom tablet)  -> Use all defaults doesn't work.
	 * 
	 * @param holder
	 * @throws Exception
	 */
	private void beginRecording(SurfaceHolder holder) throws Exception {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		if(mCamera != null){
			mCamera.reconnect();
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		
		String uniqueOutFile = OUTPUT_FILE + System.currentTimeMillis() + ".mp4";
		File outFile = new File(uniqueOutFile);
		if (outFile.exists()) {
			outFile.delete();
		}

		try {
			if (useFrontFacingCamera){
				mCamera = Camera.open(1);
//			Based on http://stackoverflow.com/questions/2779002/how-to-open-front-camera-on-android-platform
//				Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//				for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
//					Camera.getCameraInfo( camIdx, cameraInfo );
//					if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
//						mCamera = Camera.open(camIdx);
//						frontFacingFound = true;
//						break;
//					}
//				}
			}else{
				mCamera = Camera.open();
			}
			
		//Based on API demos
			mCamera.setPreviewDisplay(holder);
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(640, 480);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
			mCamera.unlock();
			
			recorder = new MediaRecorder();
			recorder.setCamera(mCamera);
			
			
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//CAMERA
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);//MIC
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//MPEG_4
			recorder.setVideoSize(640, 480);// YouTube recommended size 320x240,
																			// OpenGazer eye tracker: 640x480 mode.
			recorder.setVideoFrameRate(15);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);//MPEG_4_SP
			int sdk = android.os.Build.VERSION.SDK_INT;
			// gingerbread and up can have wide band ie 16,000 hz recordings (much
			// better human voice)
			if (sdk >= 10) {
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
				recorder.setAudioSamplingRate(16000);
			} else {
				// other devices will have to use narrow band, ie 8,000 hz (same quality
				// as a phone call which means /f/ and /th/ are indistinguishable)
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			}
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

package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.File;

import ca.ilanguage.oprime.bilingualaphasiatest.R;
import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoRecorderSubExperiment extends Activity implements
		SurfaceHolder.Callback {

	private MediaRecorder recorder = null;
	private static final String OUTPUT_FILE = "/sdcard/videooutput.mp4";
	private static final String TAG = "RecordVideo";
	private VideoView videoView = null;
	private Button startBtn = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_recorder);

		startBtn = (Button) findViewById(R.id.bgnBtn);

		
		videoView = (VideoView) this.findViewById(R.id.videoView);

		final SurfaceHolder holder = videoView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				try {
					beginRecording(holder);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
				}
			}
		});

	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		startBtn.setEnabled(true);
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
		if (recorder != null) {
			recorder.stop();
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
		if (recorder != null) {
			recorder.release();
		}
	}

	private void beginRecording(SurfaceHolder holder) throws Exception {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
		}

		File outFile = new File(OUTPUT_FILE);
		if (outFile.exists()) {
			outFile.delete();
		}

		try {
			recorder = new MediaRecorder();
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			recorder.setVideoSize(640, 480);// youtube reccomended size 320x240,
																			// OpenGazer: 640x480 mode.
			recorder.setVideoFrameRate(15);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setMaxDuration(30000); // limit to 30 seconds
			recorder.setPreviewDisplay(holder.getSurface());
			recorder.setOutputFile(OUTPUT_FILE);
			recorder.prepare();
			recorder.start();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}
}

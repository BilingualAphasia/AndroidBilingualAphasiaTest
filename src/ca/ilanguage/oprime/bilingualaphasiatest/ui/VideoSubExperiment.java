/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

// ----------------------------------------------------------------------

public class VideoSubExperiment extends Activity {
	String mLanguageOfSubExperiment;
	private String mParticipantId = "0000en";
	// private int mSubExperimentId = 0;
	private String mSubExperimentShortTitle = "";
	private String mSubExperimentTitle = "";
	String mAudioResultsFile;

	private Preview mPreview;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		// Create our Preview view and set it as the content of our activity.
		mPreview = new Preview(this, mAudioResultsFile);
		setContentView(mPreview);
		//mPreview.startRecording();
			
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

// ----------------------------------------------------------------------
//http://www.integratingstuff.com/2010/10/18/writing-code-that-captures-videos-on-android/
class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = null;
	SurfaceHolder mHolder;
	Camera mCamera;
	String mAudioResultsFile;
	private MediaRecorder mediaRecorder;
	private final int maxDurationInMs = 20000;
	private final long maxFileSizeInBytes = 500000;
	private final int videoFramesPerSecond = 20;


	Preview(Context context, String audioResultsFile) {
		super(context);
		mAudioResultsFile = audioResultsFile;
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	 boolean startRecording(){
   		try {
   			mCamera.unlock();
	
				mediaRecorder = new MediaRecorder();
	
//				mediaRecorder.setCamera(mCamera);
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	
				mediaRecorder.setMaxDuration(maxDurationInMs);
				mediaRecorder.setOutputFile(mAudioResultsFile);
	
				mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
				mediaRecorder.setVideoSize(this.getWidth(), this.getHeight());
	
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
				
				mediaRecorder.setPreviewDisplay(mHolder.getSurface());
	
				mediaRecorder.setMaxFileSize(maxFileSizeInBytes);
	
	      mediaRecorder.prepare();
				mediaRecorder.start();
	
				return true;
			} catch (IllegalStateException e) {
				Log.e(TAG,e.getMessage());
	//			e.printStackTrace();
				return false;
			} catch (IOException e) {
				Log.e(TAG,e.getMessage());
	//			e.printStackTrace();
//				mCamera.release();
//				mCamera = null;
				return false;
			}
	 }
	 boolean stopRecording(){
		if(mediaRecorder != null){
			mediaRecorder.release();
			mediaRecorder = null;
		}
		try {
			if(mCamera != null){
				mCamera.reconnect();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return true;
	}
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		mCamera = Camera.open();
		try {
			mHolder=holder;
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		stopRecording();
		if(mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = mCamera.getParameters();

		List<Size> sizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, w, h);
		parameters.setPreviewSize(optimalSize.width, optimalSize.height);

		mCamera.setParameters(parameters);
		mCamera.startPreview();
		startRecording();
	}

}

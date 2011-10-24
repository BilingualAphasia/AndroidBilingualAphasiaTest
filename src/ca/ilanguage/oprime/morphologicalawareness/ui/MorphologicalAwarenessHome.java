package ca.ilanguage.oprime.morphologicalawareness.ui;

import java.util.ArrayList;

import ca.ilanguage.oprime.domain.OPrime;
import ca.ilanguage.oprime.morphologicalawareness.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class MorphologicalAwarenessHome extends Activity {
	private String mParticipantId = OPrime.PARTICIPANT_ID_DEFAULT;
	private String mExperimentTrialHeader = "";
	private Handler mHandlerDelayStimuli = new Handler();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.setContentView(R.layout.main);
    	//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	startVideoRecorder();
    	/*
    	 * Wait two seconds so that the video activity has time to load the camera.
    	 * It will continue recording until you exit the video activity.
    	 */
    	mHandlerDelayStimuli.postDelayed(new Runnable() {
			public void run() {
				launchExperiment();
			}
		}, 2000);
    	
    }
	private void launchExperiment(){
		Intent intent;
		intent = new Intent(getApplicationContext(), CurlActivity.class);
		startActivity(intent);
	
		
	}
	private void startVideoRecorder(){
		Intent intent;
		intent = new Intent("ca.ilanguage.oprime.intent.action.START_VIDEO_RECORDER");
	
		intent.putExtra(
				OPrime.EXTRA_USE_FRONT_FACING_CAMERA,
				true);
		
		intent.putExtra(OPrime.EXTRA_LANGUAGE, OPrime.ENGLISH);
		intent.putExtra(OPrime.EXTRA_PARTICIPANT_ID, mParticipantId);
		intent.putExtra(OPrime.EXTRA_EXPERIMENT_TRIAL_INFORMATION, mExperimentTrialHeader);
		
		startActivityForResult(intent, OPrime.EXPERIMENT_COMPLETED);
		
	}
	private void stopVideoRecorder(){
		
		
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case OPrime.EXPERIMENT_COMPLETED:
			break;
		default:
			break;
		}
	}
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	
    }
    
}

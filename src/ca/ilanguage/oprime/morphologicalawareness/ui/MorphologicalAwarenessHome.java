package ca.ilanguage.oprime.morphologicalawareness.ui;

import java.util.ArrayList;

import ca.ilanguage.oprime.domain.OPrime;
import ca.ilanguage.oprime.morphologicalawareness.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class MorphologicalAwarenessHome extends Activity {
	private String mParticipantId = OPrime.PARTICIPANT_ID_DEFAULT;
	private String mExperimentTrialHeader = "";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.setContentView(R.layout.page_curl);
    	//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
	public void launchExperiment(){
		Intent intent;
		intent = new Intent("ca.ilanguage.oprime.intent.action.START_VIDEO_RECORDER");
	
		intent.putExtra(
				OPrime.EXTRA_USE_FRONT_FACING_CAMERA,
				true);
		ArrayList<Integer> stimuliImages = new ArrayList<Integer>();
		stimuliImages.add(R.drawable.stimulus_1_nonpublic);
		stimuliImages.add(R.drawable.stimulus_2_nonpublic);
		
		intent.putExtra(OPrime.EXTRA_LANGUAGE, OPrime.ENGLISH);
		intent.putExtra(OPrime.EXTRA_PARTICIPANT_ID, mParticipantId);
		intent.putExtra(OPrime.EXTRA_EXPERIMENT_TRIAL_INFORMATION, mExperimentTrialHeader);
		
		startActivityForResult(intent, OPrime.EXPERIMENT_COMPLETED);
		
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

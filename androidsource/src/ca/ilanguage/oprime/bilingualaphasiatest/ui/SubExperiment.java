package ca.ilanguage.oprime.bilingualaphasiatest.ui;


import ca.ilanguage.oprime.bilingualaphasiatest.R;
import ca.ilanguage.oprime.bilingualaphasiatest.service.AudioRecorderService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class SubExperiment extends Activity {
	String mLanguageOfSubExperiment;
	private String mParticipantId = "0000en";
	//private int mSubExperimentId = 0;
	private String mSubExperimentShortTitle="";
	private String mSubExperimentTitle="";
	String mAudioResultsFile;
	
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParticipantId = getIntent().getExtras().getString(BilingualAphasiaTestHome.EXTRA_PARTICIPANT_ID);
		mLanguageOfSubExperiment = getIntent().getExtras().getString(BilingualAphasiaTestHome.EXTRA_LANGUAGE);
		mSubExperimentTitle = getIntent().getExtras().getString(BilingualAphasiaTestHome.EXTRA_SUB_EXPERIMENT_TITLE);
		mSubExperimentShortTitle  =  mSubExperimentTitle.replaceAll("[^\\w\\.\\-\\_]", "_");
		if(mSubExperimentShortTitle.length() >= 50){
			mSubExperimentShortTitle = mSubExperimentShortTitle.substring(0,49);
		}
		setContentView(R.layout.activity_one_image_one_button);
		this.setTitle(mSubExperimentTitle);
		
		ImageView image = (ImageView) findViewById(R.id.mainimage);
		image.setImageResource(R.drawable.androids_experimenter_kids);
		
		mAudioResultsFile = BilingualAphasiaTestHome.OUTPUT_DIRECTORY
				+ System.currentTimeMillis() + "_" + mParticipantId + "_" +mLanguageOfSubExperiment+ mSubExperimentShortTitle
				+ ".mp3";
		Intent intent = new Intent(this, AudioRecorderService.class);
		// intent.setData(mUri);
		intent.putExtra(AudioRecorderService.EXTRA_AUDIOFILE_FULL_PATH,
				mAudioResultsFile);
		startService(intent);
		
	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent(this, AudioRecorderService.class);
		stopService(intent);
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

package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.IOException;

import ca.ilanguage.oprime.bilingualaphasiatest.R;
import ca.ilanguage.oprime.bilingualaphasiatest.service.AudioRecorderService;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

public class SynonymsSubExperiment extends Activity {
	private static final int ENGLISH = 0;
	private static final int FRENCH = 1;
	public static final String LANGUAGE = "language";
	String mAudioResultsFile;
	MediaRecorder mRecorder;
	private String mParticipantId= "12";
	private String mStimuliId= "1";
	MediaPlayer mp;
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
        break;
        }
        return super.onTouchEvent(event);
    }
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_image_one_button);

		ImageView image = (ImageView) findViewById(R.id.mainimage);
		image.setImageResource(R.drawable.androids_experimenter_kids);
		
		
		
		
		mAudioResultsFile = BilingualAphasiaTestHome.OUTPUT_DIRECTORY
				+ System.currentTimeMillis() + "_" + mParticipantId + "_" + mStimuliId
				+ ".mp3";
		Intent intent = new Intent(this, AudioRecorderService.class);
		// intent.setData(mUri);
		intent.putExtra(AudioRecorderService.EXTRA_AUDIOFILE_FULL_PATH,
				mAudioResultsFile);
		startService(intent);
		

		mp = MediaPlayer.create(this, R.raw.e_synonyms_instructions);
		OnCompletionListener listener = new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp = MediaPlayer.create(SynonymsSubExperiment.this, R.raw.e166);
				mp.setLooping(true);
				try {
					mp.prepare();		
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mp.start();
			}
		};
		
    //mp.setLooping(true);
		try {
			mp.prepare();		
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.start();
		mp.setOnCompletionListener(listener);
	}

	@Override
	protected void onDestroy() {
		mp.stop();
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

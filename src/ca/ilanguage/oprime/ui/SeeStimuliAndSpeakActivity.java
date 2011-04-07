package ca.ilanguage.oprime.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ca.ilanguage.oprime.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;

public class SeeStimuliAndSpeakActivity extends Activity {
	private static final String TAG = "PresentAnImageStimuli";
	
	private Long mStartTime;
    private Long mEndTime;
    private Long mTimeImageWasDisplayed;
    private Long mTimeDisplayMask;
    private String mAudioResultsFile;
    private String mImageFile;
    private ImageView mImage;
    private String mParticipantId;
    private String mStimuliId;
    private MediaRecorder mRecorder;
    
    //to have participant draw on images look at APIDemo>graphics>fingerprint
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	   
	    Bundle bundle = getIntent().getExtras();
	    if (!(bundle.isEmpty())){
		    mParticipantId=bundle.getString("participantCode");
		    mStimuliId=bundle.getString("stimuliCode");
		    mImageFile=bundle.getString("imageFile");
	    }else{
	    	mParticipantId="noone";
	    	mStimuliId="error";
	    	mImageFile ="/sdcard/OPrime/MorphologicalAwareness/images/magasin.jpg";    
	    }
	    setContentView(R.layout.activity_one_image_one_button);
	    mImage = (ImageView) findViewById(R.id.mainimage);

	    FileInputStream in;
        BufferedInputStream buf;
        try {
       	    in = new FileInputStream(mImageFile);
            buf = new BufferedInputStream(in);
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            mImage.setImageBitmap(bMap);
            if (in != null) {
         	in.close();
            }
            if (buf != null) {
         	buf.close();
            }
        } catch (Exception e) {
            Log.e("Error reading image file", e.toString());
        }
 
        
	    mAudioResultsFile ="/sdcard/OPrime/MorphologicalAwareness/results/"+System.currentTimeMillis()+"_"+mParticipantId+"_"+mStimuliId+".mp3";    
	    mRecorder = new MediaRecorder();
//	    Environment.getExternalStorageDirectory().getAbsolutePath() + path;
	    String state = android.os.Environment.getExternalStorageState();
	    if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
	        try {
				throw new IOException("SD Card is not mounted.  It is " + state + ".");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Toast.makeText(SeeStimuliAndSpeakActivity.this, "The experiment cannot save audio, maybe the tablet is attached to a computer?", Toast.LENGTH_SHORT).show();
			}
	    }
	    
	    try {
	    	//http://www.benmccann.com/dev-blog/android-audio-recording-tutorial/
	    	mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		    mRecorder.setOutputFile(mAudioResultsFile);
		    mRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    mRecorder.start();
	 
	    
	    
    }
	public void onNextClick(View v){
    	mRecorder.stop();
    	mRecorder.release();
    	Toast.makeText(SeeStimuliAndSpeakActivity.this, "The audio might be recorded, check the Oprime folder.", Toast.LENGTH_SHORT).show();
		//return to runexperimentactivity
    	//startActivity(new Intent(this, RunExperimentActivity.class));
    }
    
}

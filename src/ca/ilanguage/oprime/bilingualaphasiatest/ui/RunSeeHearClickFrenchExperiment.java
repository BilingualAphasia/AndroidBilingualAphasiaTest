package ca.ilanguage.oprime.ui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import ca.ilanguage.oprime.R;
import ca.ilanguage.oprime.preferences.PreferenceConstants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RunSeeHearClickFrenchExperiment extends Activity implements TextToSpeech.OnInitListener {
	private static final String TAG = "RunExperimentActivity";
	/** Talk to the user */
    private TextToSpeech mTts;
    private ImageView mImage;
    private String mStimuliFile;
    private String mResultsFile;
    private String mStimuliArray[] = new String[1000];
    private int mStimuliPosition=1;
    private BufferedWriter mOut;
    
    private Long mStartTime;
    private Long mEndTime;
    private Long mTimeImageWasDisplayed;
    private String mDateString;
    private String mResponse;
    private String mAudioResultsFile;
    private String mImageFile;
    private String mParticipantId;
    private String mStimuliId;
    private MediaRecorder mRecorder;
    private String mBaseDir;
    
  //implement on Init for the text to speech
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will
			// indicate this.
			int result = mTts.setLanguage(Locale.US);
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Language data is missing or the language is not supported.
				Log.e(TAG, "Language is not available.");
			} else {
				//everything is working.
			}
		} else {
			// Initialization failed.
			Log.e(TAG, "Could not initialize TextToSpeech.");
		}
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_one_image_one_button);
//        mImage = (ImageView) findViewById(R.id.mainimage);
//        mImage.setImageResource(R.drawable.androids_experimenter_kids);
        
        mTts = new TextToSpeech(this, this);
//        mTts.speak("The experiment is running.",
//    	        TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
//    	        null);

        
        SharedPreferences prefs = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
		mBaseDir = prefs.getString(PreferenceConstants.PREFERENCE_EXPERIMENTS_DIR, "/sdcard/dropbox");
		
        
        mStimuliFile=mBaseDir+"/BilingualAphasiaTest/stimuli_french.csv";
        mResultsFile=mBaseDir+"/BilingualAphasiaTest/results/french_results.txt";
        readInStimuli();
        
        FileWriter fstream;
    	try {
    		fstream = new FileWriter(mResultsFile,true);
    		mOut = new BufferedWriter(fstream);

    		mOut.write("Date"+"\t"+"ParticipantID"+"\t"+"Stimuli"+"\t"+"ReactionTime\tResponse");
    		mOut.newLine();
    		
//    		for(int k= 0; k<mSimuliArray.length; k++){
//    			presentStimuli(k);
//    		}
    		//while(mStimuliPosition<mSimuliArray.length){
    			presentStimuli(mStimuliPosition);
    		//}
    		
    		
//    		out.flush();
//    		out.close();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		Toast.makeText(RunSeeHearClickFrenchExperiment.this, "Error "+e.toString(), Toast.LENGTH_LONG).show();

    	}
    	
        
    }
    
    private void readInStimuli(){
    		BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(mStimuliFile));
	    		String line;
	    		int count=0;
	    		try {
					while((line = in.readLine()) !=null){
						mStimuliArray[count]= line;
						count++;	
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(RunSeeHearClickFrenchExperiment.this, "Error "+e.toString(), Toast.LENGTH_LONG).show();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Toast.makeText(RunSeeHearClickFrenchExperiment.this, "Error "+e.toString(), Toast.LENGTH_LONG).show();
			}

   }
    
    private void presentStimuli(int number){
    	String columns[] = mStimuliArray[number].split("\",\"");
    	String experimentPath=mBaseDir+"/BilingualAphasiaTest/";
		int stimuliCode=0;
    	Bundle bundle= new Bundle();
		bundle.putString("participantCode", "noone");
		bundle.putInt("stimuliCode",stimuliCode);
		
		bundle.putString("imageFile", experimentPath+"images/"+columns[1].replaceAll("\"",""));
//		try {
//			out.write(bundle.toString()+"\n\n");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			Toast.makeText(RunExperimentActivity.this, "Error "+e.toString(), Toast.LENGTH_LONG).show();
//		}

		//send the bundle to the SeeStimuliAndSpeakActivity
		Intent i = new Intent(RunSeeHearClickFrenchExperiment.this, SeeStimuliAndSpeakActivity.class);
		i.putExtras(bundle);
		//startActivityForResult(i, 0);
		
		
		mParticipantId="TT";
		mStimuliId="stimuli"+mStimuliPosition;
		mImageFile=experimentPath+"images/"+columns[1].replaceAll("\"","");
		setContentView(R.layout.activity_one_image_one_button_no_back);
		
		

//		TextView imagenumber = (TextView) findViewById(R.id.imagenumber);
//		String imagefilename = columns[1].replaceAll("\"","");
//		imagenumber.setText(imagefilename);
		
		
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
            Toast.makeText(RunSeeHearClickFrenchExperiment.this, "Error reading image file "+mImageFile, Toast.LENGTH_SHORT).show();
			
        }
        
 
        mStartTime=System.currentTimeMillis();
        
      //MediaPlayer mp = MediaPlayer.create(this, R.raw.click_on_dog_coat);
	    MediaPlayer mp = new MediaPlayer();
	    String audioFile =experimentPath+"audio/"+columns[2].replaceAll("\"","");//"/sdcard/dropbox/BilingualAphasiaTest/audio/e66.mp3";    
	    try {
			mp.setDataSource(audioFile);
			mp.prepare();
	        mp.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Log.e("Error reading file", e.toString());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Log.e("Error reading file", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("Error reading file", e.toString());
		}
		
        
//        Time timestamp= new Time();//System.currentTimeMillis()
//        timestamp.setToNow();
        //Date date = new Date(location.getTime());
        Date date = new Date();
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG, Locale.US);
        
        mDateString=dateFormat.format(date).replaceAll("/","_");
        
        
//        mAudioResultsFile ="/sdcard/dropbox/MorphologicalAwareness/results/"+dateFormat.format(date).replaceAll("/","_")+"_"+System.currentTimeMillis()+"_"+mParticipantId+"_"+mStimuliId+".mp3";    
//	    mRecorder = new MediaRecorder();
////	    Environment.getExternalStorageDirectory().getAbsolutePath() + path;
//	    String state = android.os.Environment.getExternalStorageState();
//	    if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
//	        try {
//				throw new IOException("SD Card is not mounted.  It is " + state + ".");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				Toast.makeText(RunSeeHearClickExperiment.this, "The experiment cannot save audio, maybe the tablet is attached to a computer?", Toast.LENGTH_SHORT).show();
//			}
//	    }
	    
//	    try {
//	    	//http://www.benmccann.com/dev-blog/android-audio-recording-tutorial/
//	    	mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//		    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//		    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//		    mRecorder.setOutputFile(mAudioResultsFile);
//		    mRecorder.prepare();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    mRecorder.start();
	    
	    
	    
    }
    
   @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		try {
//			out.write("Came back from stimuli");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//Toast.makeText(RunSeeHearClickExperiment.this, "On resume, current stimuli item is is"+mStimuliArray[mStimuliPosition].toString(), Toast.LENGTH_LONG).show();
	}

   
   
   @Override
public boolean onTouchEvent(MotionEvent event) {
	   
	   mEndTime=System.currentTimeMillis();
	   mTimeImageWasDisplayed=mEndTime-mStartTime;
	   if (mTimeImageWasDisplayed >300){
		   
		   String touchPosition ="x="+event.getX()+",y="+event.getY();
		 recordReaction(touchPosition);
		   
//		   Canvas canvas = new Canvas();
//		   Paint paint = new Paint();
//		   paint.setColor(Color.BLUE);
//		   canvas.drawCircle(event.getX(), event.getY(), 40 * 1.0f, paint);
		   
	   }
	   
	   
	   
	return super.onTouchEvent(event);
	
}
   private void recordReaction(String response){
	   if ("next".equals(response)){
		   mResponse ="skipped";
			  
	   }else{
		   mResponse=response;
	   }
	   mEndTime=System.currentTimeMillis();
	   mTimeImageWasDisplayed=mEndTime-mStartTime;
	   try {
		   mOut.write(mDateString+"\t"+mParticipantId+"\t"+mStimuliId+"\t"+mTimeImageWasDisplayed+"\t"+mResponse);
		   
		mOut.newLine();
		
		mOut.flush();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		Toast.makeText(RunSeeHearClickFrenchExperiment.this, "Error saving to the results file", Toast.LENGTH_SHORT).show();
		
	}
//   	mRecorder.stop();
//   	mRecorder.release();
   	//Toast.makeText(RunSeeHearClickExperiment.this, "Going to the next stimuli", Toast.LENGTH_SHORT).show();
//   	try {
//   		mTimeImageWasDisplayed=mEndTime-mStartTime;
//		out.write(mParticipantId+"\t"+mStimuliId+"\t"+mStartTime+"\t"+mEndTime+"\t"+mTimeImageWasDisplayed);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		Toast.makeText(RunExperimentActivity.this, "On resume", Toast.LENGTH_LONG).show();
//	}
   	mStimuliPosition++;
   	if (mStimuliArray[mStimuliPosition] != null){
   		presentStimuli(mStimuliPosition);
   	}else{
   		//Toast.makeText(RunExperimentActivity.this, "Merci!", Toast.LENGTH_LONG).show();
   		
   		try {
			mOut.flush();
			mOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finish();
   		//startActivity(new Intent(this, ThankYouActivity.class));
   	}
		//return to runexperimentactivity
   	//startActivity(new Intent(this, RunExperimentActivity.class));
   }

public void onNextClick(View v){
	recordReaction("next");
   }
}

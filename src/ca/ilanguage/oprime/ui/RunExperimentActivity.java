package ca.ilanguage.oprime.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;


import ca.ilanguage.oprime.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;



public class RunExperimentActivity extends Activity implements TextToSpeech.OnInitListener {
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
    
    private String mDateString ="";
    private String mResponse ="";
    
    private String mAudioResultsFile;
    private String mImageFile;
    private String mParticipantId ="TT";
    private String mParticipantName="Testing";
    private String mParticipantAge="0";
    private String mStimuliId;
    private MediaRecorder mRecorder;
    private Boolean mPauseScreen=true;
    
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

        mParticipantAge=getIntent().getExtras().getString("participantAge");
        mParticipantName=getIntent().getExtras().getString("participantName");
        
        
        mStimuliFile="/sdcard/OPrime/MorphologicalAwareness/stimuli_april9.csv";
        mResultsFile="/sdcard/OPrime/MorphologicalAwareness/results/results.txt";
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
    			
    		//}
    		
    		
//    		out.flush();
//    		out.close();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		Toast.makeText(RunExperimentActivity.this, "Error "+e.toString(), Toast.LENGTH_LONG).show();

    	}
    	
    	presentStimuli(mStimuliPosition);
        
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
					Toast.makeText(RunExperimentActivity.this, "Error "+e.toString(), Toast.LENGTH_LONG).show();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Toast.makeText(RunExperimentActivity.this, "Error "+e.toString(), Toast.LENGTH_LONG).show();
			}

   }
    
    private void presentStimuli(int number){
    	//if its the first time in this experiment, display the logo screen until 
    	//experimenter is ready, ie until they click next.
    	if (mPauseScreen){
    		setContentView(R.layout.logo_copyright);
    	    mImage = (ImageView) findViewById(R.id.mainimage);
    	    mImage.setImageResource(R.drawable.androids_experimenter_kids);
    	    this.setTitle("Touch the arrows when you're ready.");
    	    //mParticipantName=getIntent().getExtras().getString("participantName");
    	    Toast.makeText(RunExperimentActivity.this, "Hi "+mParticipantName
    	    		+"!\n\nTouch the arrows when you're ready!", Toast.LENGTH_LONG).show();

    	    
    	    
    	}else if (mPauseScreen == false){
    	
	    	String columns[] = mStimuliArray[number].split("\",\"");
	    	String experimentPath="/sdcard/OPrime/MorphologicalAwareness/";
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
			Intent i = new Intent(RunExperimentActivity.this, SeeStimuliAndSpeakActivity.class);
			i.putExtras(bundle);
			//startActivityForResult(i, 0);
			
			
			
			mStimuliId="stimuli"+mStimuliPosition;
			mImageFile=experimentPath+"images/"+columns[1].replaceAll("\"","");
			setContentView(R.layout.activity_one_image_one_button);
			
			
	
			TextView imagenumber = (TextView) findViewById(R.id.imagenumber);
			String imagefilename = columns[1].replaceAll("\"","");
			imagenumber.setText(imagefilename);
			
			
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
	            Toast.makeText(RunExperimentActivity.this, "Error reading image file "+mImageFile, Toast.LENGTH_SHORT).show();
				
	        }
	        
	 
	        mStartTime=System.currentTimeMillis();
	        
	        
	        mDateString = (String) android.text.format.DateFormat.format("yyyy-MM-dd_hh.mm", new java.util.Date());
	       mDateString = mDateString.replaceAll("/","_").replaceAll(" ","_");
	        
	//        mDateString=dateFormat.format(date).replaceAll("/","_");
	        
	        mAudioResultsFile ="/sdcard/OPrime/MorphologicalAwareness/results/"+mDateString+"_"+System.currentTimeMillis()+"_"+mParticipantId+"_"+mStimuliId+".mp3";    
		    mRecorder = new MediaRecorder();
	//	    Environment.getExternalStorageDirectory().getAbsolutePath() + path;
		    String state = android.os.Environment.getExternalStorageState();
		    if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
		        try {
					throw new IOException("SD Card is not mounted.  It is " + state + ".");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Toast.makeText(RunExperimentActivity.this, "The experiment cannot save audio, maybe the tablet is attached to a computer?", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(RunExperimentActivity.this, "The experiment cannot save audio, maybe the tablet is attached to a computer?", Toast.LENGTH_SHORT).show();
	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(RunExperimentActivity.this, "The experiment cannot save audio, maybe the tablet is attached to a computer?", Toast.LENGTH_SHORT).show();
	
			}
		    mRecorder.start();
		    
    	}
	    
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
		//Toast.makeText(RunExperimentActivity.this, "On resume, stimuli lenght is"+mStimuliArray.length, Toast.LENGTH_LONG).show();
	}

   public void onNextClick(View v){
	   //if coming in from a paused screen, start displaying the stimuli again
	   if (mPauseScreen){
   	    	mPauseScreen = false;
   	    	presentStimuli(mStimuliPosition);
    	}else if (mPauseScreen == false){
   	
	    	
		   
		mEndTime=System.currentTimeMillis();
	   	mRecorder.stop();
	   	mRecorder.release();
	   	//Toast.makeText(RunExperimentActivity.this, "The audio was successfully recorded, check the Oprime folder.", Toast.LENGTH_SHORT).show();
	//   	try {
	//   		mTimeImageWasDisplayed=mEndTime-mStartTime;
	//		out.write(mParticipantId+"\t"+mStimuliId+"\t"+mStartTime+"\t"+mEndTime+"\t"+mTimeImageWasDisplayed);
	//	} catch (IOException e) {
	//		// TODO Auto-generated catch block
	//		Toast.makeText(RunExperimentActivity.this, "On resume", Toast.LENGTH_LONG).show();
	//	}
	   	
	   	mEndTime=System.currentTimeMillis();
		   mTimeImageWasDisplayed=mEndTime-mStartTime;
		   try {
			   mOut.write(mDateString+"\t"+mParticipantId+"\t"+mStimuliId+"\t\t"+mTimeImageWasDisplayed+"\t"+mResponse);
			   
			mOut.newLine();
			
			mOut.flush();
		   } catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(RunExperimentActivity.this, "Error saving to the results file", Toast.LENGTH_SHORT).show();
				
			}
			
	   	mStimuliPosition++;
	   	if (mStimuliArray[mStimuliPosition] != null){
	   		presentStimuli(mStimuliPosition);
	   	}else{
	   		//Toast.makeText(RunExperimentActivity.this, "Merci!", Toast.LENGTH_LONG).show();
	   		startActivity(new Intent(this, ThankYouActivity.class));
	   	}
   	}
		//return to runexperimentactivity
   	//startActivity(new Intent(this, RunExperimentActivity.class));
   }

   public void onBackClick(View v){
	   mEndTime=System.currentTimeMillis();
   	mRecorder.stop();
   	mRecorder.release();
   	//Toast.makeText(RunExperimentActivity.this, "The audio was successfully recorded, check the Oprime folder.", Toast.LENGTH_SHORT).show();
//   	try {
//   		mTimeImageWasDisplayed=mEndTime-mStartTime;
//		out.write(mParticipantId+"\t"+mStimuliId+"\t"+mStartTime+"\t"+mEndTime+"\t"+mTimeImageWasDisplayed);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		Toast.makeText(RunExperimentActivity.this, "On resume", Toast.LENGTH_LONG).show();
//	}
   	mStimuliPosition--;
   	if (mStimuliArray[mStimuliPosition] != null && mStimuliPosition != 0){
   		//do nothing
   	}else{
   		//Toast.makeText(RunExperimentActivity.this, "Merci!", Toast.LENGTH_LONG).show();
   		mStimuliPosition++;
   	}
   	presentStimuli(mStimuliPosition);
		//return to runexperimentactivity
   	//startActivity(new Intent(this, RunExperimentActivity.class));
   }

}

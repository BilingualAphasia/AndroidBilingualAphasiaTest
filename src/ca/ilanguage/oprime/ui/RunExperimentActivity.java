package ca.ilanguage.oprime.ui;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Locale;

import ca.ilanguage.oprime.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.format.Time;

public class RunExperimentActivity extends Activity implements TextToSpeech.OnInitListener {
	private static final String TAG = "RunExperimentActivity";
	/** Talk to the user */
    private TextToSpeech mTts;
    private ImageView mImage;
    private String mStimuliFile;
    private String mResultsFile;
    private String mSimuliArray[] = new String[1000];
    private int mStimuliPosition=0;
    private BufferedWriter out;
    

    
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
        setContentView(R.layout.activity_one_image_one_button);
        mImage = (ImageView) findViewById(R.id.mainimage);
        mImage.setImageResource(R.drawable.androids_experimenter_kids);
        
        mTts = new TextToSpeech(this, this);
        mTts.speak("The experiment is running.",
    	        TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
    	        null);

        mStimuliFile="/sdcard/OPrime/MorphologicalAwareness/stimuli_new.csv";
        mResultsFile="/sdcard/OPrime/MorphologicalAwareness/results/results.txt";
        readInStimuli();
        
        FileWriter fstream;
    	try {
    		fstream = new FileWriter(mResultsFile,true);
    		BufferedWriter out = new BufferedWriter(fstream);

    		
    		
    		
    		out.flush();
    		out.close();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		Toast.makeText(RunExperimentActivity.this, "Error "+e.toString(), Toast.LENGTH_LONG).show();

    	}
    	//for(int k= 0; k++; k<mStimuliArray.size()){
    		presentStimuli(0);
    	//}
        
    }
    
    private void readInStimuli(){
    		BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(mStimuliFile));
	    		String line;
	    		int count=0;
	    		try {
					while((line = in.readLine()) !=null){
						mSimuliArray[count]= line;
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
    	String columns[] = mSimuliArray[number].split(",");
    	String experimentPath="/sdcard/OPrime/MorphologicalAwareness/";
		int stimuliCode=0;
    	Bundle bundle= new Bundle();
		bundle.putString("participantCode", "noone");
		bundle.putInt("stimuliCode",stimuliCode);
		bundle.putString("imageFile", experimentPath+columns[1]);
		try {
			out.write(bundle.toString()+"\n\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//send the bundle to the SeeStimuliAndSpeakActivity
		Intent i = new Intent(RunExperimentActivity.this, SeeStimuliAndSpeakActivity.class);
		i.putExtras(bundle);
		startActivityForResult(i, 0);
    }
    
   @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			out.write("Came back from stimuli");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

public void onNextClick(View v){
   	//
   }
}

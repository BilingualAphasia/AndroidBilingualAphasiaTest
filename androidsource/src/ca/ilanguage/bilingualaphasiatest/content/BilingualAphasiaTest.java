package ca.ilanguage.bilingualaphasiatest.content;

import java.util.ArrayList;

import ca.ilanguage.oprime.content.Experiment;
import ca.ilanguage.oprime.content.SubExperimentBlock;
import android.app.Application;
import android.util.Log;
import ca.ilanguage.bilingualaphasiatest.R;

public class BilingualAphasiaTest extends Application {
	protected static final String TAG = "BilingualAphasiaTest";
	public static final boolean D = true;
	Experiment bat;
	ArrayList<SubExperimentBlock> subexperiments;
	int currentsubexperiment;
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		bat = new Experiment( getString(R.string.experiment_title) );
		subexperiments = new ArrayList<SubExperimentBlock>();
		subexperiments.add(new SubExperimentBlock("Hi There"));
		currentsubexperiment = 0;
		Log.d(TAG, "Oncreate of the application");
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}
	
	

}

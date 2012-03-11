package ca.ilanguage.bilingualaphasiatest.content;

import java.util.ArrayList;
import java.util.Locale;

import ca.ilanguage.oprime.content.Experiment;
import ca.ilanguage.oprime.content.SubExperimentBlock;
import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import ca.ilanguage.bilingualaphasiatest.R;

public class BilingualAphasiaTest extends Application {
	protected static final String TAG = "BilingualAphasiaTest";
	public static final boolean D = true;
	
	Experiment bat;
	String language;
	
	ArrayList<SubExperimentBlock> subExperiments;
	int currentSubExperiment;
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		language = Locale.getDefault().getLanguage();
		language = "fr";
		forceLocale(language);
		bat = new Experiment( getString(R.string.experiment_title) );
		subExperiments = new ArrayList<SubExperimentBlock>();
		String[] subextitles = getResources().getStringArray(R.array.subexperiment_titles);
		for(int i = 0; i < subextitles.length; i++){
			subExperiments.add(new SubExperimentBlock(subextitles[i]));
		}
		currentSubExperiment = 0;
		Log.d(TAG, "Oncreate of the application");
	}
	public ArrayList<String> getSubExperimentTitles() {
		ArrayList<String> titles = new ArrayList<String>();
		for( SubExperimentBlock subexperiment : subExperiments){
			titles.add(subexperiment.getTitle());
		}
		return titles;
	}	
	/**
	 * Forces the locale for the duration of the app to the language needed for that version of the Bilingual Aphasia Test
	 * @param lang
	 * @return
	 */
	public String forceLocale(String lang){
		if (lang.equals(Locale.getDefault().getLanguage())){
			return Locale.getDefault().getDisplayLanguage();
		}
		Configuration config = getBaseContext().getResources().getConfiguration();
		Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        language = Locale.getDefault().getLanguage();
		return Locale.getDefault().getDisplayLanguage();
    }
	public Experiment getExperiment() {
		return bat;
	}
	public void setExperiment(Experiment bat) {
		this.bat = bat;
	}
	public ArrayList<SubExperimentBlock> getSubExperiments() {
		return subExperiments;
	}	
	
	public void setSubExperiments(ArrayList<SubExperimentBlock> subExperiments) {
		this.subExperiments = subExperiments;
	}
	public int getCurrentSubExperiment() {
		return currentSubExperiment;
	}
	public void setCurrentSubExperiment(int currentSubExperiment) {
		this.currentSubExperiment = currentSubExperiment;
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

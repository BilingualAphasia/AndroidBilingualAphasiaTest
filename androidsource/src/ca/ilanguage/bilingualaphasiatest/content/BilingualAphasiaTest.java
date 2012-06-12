package ca.ilanguage.bilingualaphasiatest.content;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import ca.ilanguage.oprime.content.Experiment;
import ca.ilanguage.oprime.content.Participant;
import ca.ilanguage.oprime.content.Stimulus;
import ca.ilanguage.oprime.content.TwoImageStimulus;
import ca.ilanguage.oprime.content.SubExperimentBlock;
import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import ca.ilanguage.bilingualaphasiatest.R;

public class BilingualAphasiaTest extends Application {
	protected static final String TAG = "BilingualAphasiaTest";
	public static final boolean D = true;

	int bat;
	ArrayList<Experiment> experiments;
	Locale language;

	int currentSubExperiment;

	private String outputDir = "/sdcard/BilingualAphasiaTest/";
	public static final String PREFERENCE_NAME = "BilingualAphasiaPrefs";
	public static final String PREFERENCE_PARTICIPANT_ID = "participantId";
	public static final String PREFERENCE_PARTICIPANT_FIRSTNAME = "participantfirstname";
	public static final String PREFERENCE_PARTICIPANT_LASTNAME = "participantlastname";
	public static final String PREFERENCE_PARTICIPANT_GENDER = "participantgender";
	public static final String PREFERENCE_PARTICIPANT_BIRTHDATE = "participantbirthdate";
	public static final String PREFERENCE_PARTICIPANT_DETAILS = "participantdetails";
	public static final String PREFERENCE_PARTICIPANT_STARTTIME = "participantstarttime";
	public static final String PREFERENCE_PARTICIPANT_ENDTIME = "participantendtime";
	public static final String PREFERENCE_EXPERIEMENTER_CODE = "experimenterCode";
	public static final String PREFERENCE_EXPERIMENT_LANGUAGE = "experimentlanguage";
	public static final String PREFERENCE_PARTICIPANT_LANGUAGES = "participantlangs";
	public static final String PREFERENCE_TESTING_DAY_NUMBER = "testingdaynumber";
	public static final String PREFERENCE_PARTICIPANT_NUMBER_IN_DAY = "participantnumberinday";

	public static final String PREFERENCE_REPLAY_RESULTS_MODE = "replayresults";
	public static final String PREFERENCE_REPLAY_PARTICIPANT_CODE = "replayparticipantcode";

	@Override
	public void onCreate() {
		super.onCreate();

		language = Locale.getDefault();
		experiments = new ArrayList<Experiment>();

		Log.d(TAG, "Oncreate of the application");
	}

	public ArrayList<String> getSubExperimentTitles() {
		ArrayList<String> titles = new ArrayList<String>();
		for (SubExperimentBlock subexperiment : experiments.get(bat)
				.getSubExperiments()) {
			titles.add(subexperiment.getTitle());
		}
		return titles;
	}

	public void createNewExperiment(String languagecode) {
		forceLocale(languagecode);

		Experiment expLocalized = new Experiment(
				getString(R.string.experiment_title) + " - "
						+ language.getDisplayLanguage());
		experiments.add(expLocalized);
		bat = experiments.size() - 1;

		experiments.get(bat).setSubExperiments(
				new ArrayList<SubExperimentBlock>());
		String[] subextitles = getResources().getStringArray(
				R.array.subexperiment_titles);
		for (int i = 0; i < subextitles.length; i++) {
			experiments
					.get(bat)
					.getSubExperiments()
					.add(new SubExperimentBlock(subextitles[i], language
							.getLanguage(), subextitles[i], null, ""));
		}
		addStimuli();
		currentSubExperiment = 0;
		Log.d(TAG, "Created an experiment " + experiments.get(bat).getTitle());

	}

	public boolean isExperimentCompleted() {
		int completed = 0;
		for (SubExperimentBlock s : getExperiment().getSubExperiments()) {
			if (s.isExperimentProbablyComplete()) {
				completed++;
			}
		}
		if (completed == getExperiment().getSubExperiments().size()) {
			if (!getExperiment().getParticipant().getLanguageCodes()
					.contains(language.getLanguage())) {
				getExperiment().getParticipant().getLanguageCodes()
						.add(language.getLanguage());
				getExperiment().getParticipant().getLanguages()
						.add(language.getDisplayLanguage());
			}
			return true;
		} else {
			return false;
		}

	}

	public void writePrivateParticipantToFile() {
		String outfile = outputDir + "participants_private.csv";
		try {
			FileOutputStream out = new FileOutputStream(outfile, true);
			out.write(("\n"+getExperiment().getParticipant().toCSVPrivateString())
					.getBytes());
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Problem opening outfile.");

		} catch (IOException e) {
			Log.e(TAG, "Problem writing outfile.");
		}
	}

	/**
	 * Forces the locale for the duration of the app to the language needed for
	 * that version of the Bilingual Aphasia Test
	 * 
	 * @param lang
	 * @return
	 */
	public String forceLocale(String lang) {
		if (lang.equals(Locale.getDefault().getLanguage())) {
			return Locale.getDefault().getDisplayLanguage();
		}
		Configuration config = getBaseContext().getResources()
				.getConfiguration();
		Locale locale = new Locale(lang);
		Locale.setDefault(locale);
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
		language = Locale.getDefault();
		return Locale.getDefault().getDisplayLanguage();
	}

	public Locale getLanguage() {
		return language;
	}

	public void setLanguage(Locale language) {
		this.language = language;
	}

	public Experiment getExperiment() {
		if (experiments.size() > 0) {
			return experiments.get(bat);
		} else {
			return null;
		}
	}

	public void setExperiment(int bat) {
		this.bat = bat;
	}

	public ArrayList<SubExperimentBlock> getSubExperiments() {
		return experiments.get(bat).getSubExperiments();
	}

	public void setSubExperiments(ArrayList<SubExperimentBlock> subExperiments) {
		experiments.get(bat).setSubExperiments(subExperiments);
	}

	public int getCurrentSubExperiment() {
		return currentSubExperiment;
	}

	public void setCurrentSubExperiment(int currentSubExperiment) {
		this.currentSubExperiment = currentSubExperiment;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
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

	private void addStimuli() {

		/*
		 * SubExperiment 6: Verbal Auditory Comprehension uses 2 images per
		 * stimuli.
		 */
		ArrayList<TwoImageStimulus> twostimuli = new ArrayList<TwoImageStimulus>();

		twostimuli.add(new TwoImageStimulus(R.drawable.s073, R.drawable.s002));
		twostimuli.add(new TwoImageStimulus(R.drawable.s003, R.drawable.s004));
		twostimuli.add(new TwoImageStimulus(R.drawable.s005, R.drawable.s006));
		twostimuli.add(new TwoImageStimulus(R.drawable.s007, R.drawable.s008));
		twostimuli.add(new TwoImageStimulus(R.drawable.s009, R.drawable.s010));
		twostimuli.add(new TwoImageStimulus(R.drawable.s011, R.drawable.s012));
		twostimuli.add(new TwoImageStimulus(R.drawable.s013, R.drawable.s014));
		twostimuli.add(new TwoImageStimulus(R.drawable.s015, R.drawable.s016));
		twostimuli.add(new TwoImageStimulus(R.drawable.s017, R.drawable.s018));
		twostimuli.add(new TwoImageStimulus(R.drawable.s019, R.drawable.s020));
		twostimuli.add(new TwoImageStimulus(R.drawable.s021, R.drawable.s022));
		twostimuli.add(new TwoImageStimulus(R.drawable.s023, R.drawable.s024));
		twostimuli.add(new TwoImageStimulus(R.drawable.s025, R.drawable.s026));
		twostimuli.add(new TwoImageStimulus(R.drawable.s027, R.drawable.s028));
		twostimuli.add(new TwoImageStimulus(R.drawable.s029, R.drawable.s030));
		twostimuli.add(new TwoImageStimulus(R.drawable.s031, R.drawable.s032));
		twostimuli.add(new TwoImageStimulus(R.drawable.s033, R.drawable.s034));
		twostimuli.add(new TwoImageStimulus(R.drawable.s035, R.drawable.s036));
		twostimuli.add(new TwoImageStimulus(R.drawable.s037, R.drawable.s038));
		twostimuli.add(new TwoImageStimulus(R.drawable.s039, R.drawable.s040));
		twostimuli.add(new TwoImageStimulus(R.drawable.s041,
				R.drawable.androids_experimenter_kids));

		experiments.get(bat).getSubExperiments().get(6).setStimuli(twostimuli);
		twostimuli = null;

		/*
		 * SubExperiment 7: Syntactic Comprehension
		 */
		ArrayList<Stimulus> stimuli = new ArrayList<Stimulus>();

		stimuli.add(new Stimulus(R.drawable.s042));
		stimuli.add(new Stimulus(R.drawable.s043));
		stimuli.add(new Stimulus(R.drawable.s044));
		stimuli.add(new Stimulus(R.drawable.s045));
		stimuli.add(new Stimulus(R.drawable.s046));
		stimuli.add(new Stimulus(R.drawable.s047));
		stimuli.add(new Stimulus(R.drawable.s048));
		stimuli.add(new Stimulus(R.drawable.s049));
		stimuli.add(new Stimulus(R.drawable.s050));
		stimuli.add(new Stimulus(R.drawable.s051));
		stimuli.add(new Stimulus(R.drawable.s052));
		stimuli.add(new Stimulus(R.drawable.s053));
		stimuli.add(new Stimulus(R.drawable.s054));
		stimuli.add(new Stimulus(R.drawable.s055));
		stimuli.add(new Stimulus(R.drawable.s056));
		stimuli.add(new Stimulus(R.drawable.s057));
		stimuli.add(new Stimulus(R.drawable.s058));
		stimuli.add(new Stimulus(R.drawable.s059));
		stimuli.add(new Stimulus(R.drawable.s060));
		stimuli.add(new Stimulus(R.drawable.s061));
		stimuli.add(new Stimulus(R.drawable.s062));
		stimuli.add(new Stimulus(R.drawable.s063));
		stimuli.add(new Stimulus(R.drawable.s064));
		stimuli.add(new Stimulus(R.drawable.s065));
		stimuli.add(new Stimulus(R.drawable.s066));
		stimuli.add(new Stimulus(R.drawable.s067));
		stimuli.add(new Stimulus(R.drawable.s068));
		stimuli.add(new Stimulus(R.drawable.s069));
		stimuli.add(new Stimulus(R.drawable.s070));
		stimuli.add(new Stimulus(R.drawable.s071));
		stimuli.add(new Stimulus(R.drawable.s072));
		stimuli.add(new Stimulus(R.drawable.androids_experimenter_kids));

		experiments.get(bat).getSubExperiments().get(7).setStimuli(stimuli);
		stimuli = null;

		/*
		 * SubExperiment 21: Description of a cartoon
		 */
		stimuli = new ArrayList<Stimulus>();
		stimuli.add(new Stimulus(R.drawable.s073));
		stimuli.add(new Stimulus(R.drawable.s074));
		stimuli.add(new Stimulus(R.drawable.androids_experimenter_kids));
		experiments.get(bat).getSubExperiments().get(21).setStimuli(stimuli);
		stimuli = null;

		/*
		 * SubExperiment 24: Reading
		 */
		stimuli = new ArrayList<Stimulus>();
		stimuli.add(new Stimulus(R.drawable.s073));
		stimuli.add(new Stimulus(R.drawable.s075));
		stimuli.add(new Stimulus(R.drawable.s076));
		stimuli.add(new Stimulus(R.drawable.s077));
		stimuli.add(new Stimulus(R.drawable.s078));
		stimuli.add(new Stimulus(R.drawable.s079));
		stimuli.add(new Stimulus(R.drawable.s080));
		stimuli.add(new Stimulus(R.drawable.s081));
		stimuli.add(new Stimulus(R.drawable.s082));
		stimuli.add(new Stimulus(R.drawable.androids_experimenter_kids));
		experiments.get(bat).getSubExperiments().get(24).setStimuli(stimuli);
		stimuli = null;

		/*
		 * SubExperiment 25: Copying
		 */
		stimuli = new ArrayList<Stimulus>();
		stimuli.add(new Stimulus(R.drawable.s073));
		stimuli.add(new Stimulus(R.drawable.s083));
		stimuli.add(new Stimulus(R.drawable.androids_experimenter_kids));
		experiments.get(bat).getSubExperiments().get(25).setStimuli(stimuli);
		stimuli = null;

		/*
		 * SubExperiment 28: Reading words for comprehension uses 2 images per
		 * stimuli.
		 */

		twostimuli = new ArrayList<TwoImageStimulus>();

		twostimuli.add(new TwoImageStimulus(R.drawable.s073, R.drawable.s084));
		twostimuli.add(new TwoImageStimulus(R.drawable.s085, R.drawable.s086));
		twostimuli.add(new TwoImageStimulus(R.drawable.s087, R.drawable.s088));
		twostimuli.add(new TwoImageStimulus(R.drawable.s089, R.drawable.s090));
		twostimuli.add(new TwoImageStimulus(R.drawable.s091, R.drawable.s092));
		twostimuli.add(new TwoImageStimulus(R.drawable.s093, R.drawable.s094));
		twostimuli.add(new TwoImageStimulus(R.drawable.s095, R.drawable.s096));
		twostimuli.add(new TwoImageStimulus(R.drawable.s097, R.drawable.s098));
		twostimuli.add(new TwoImageStimulus(R.drawable.s099, R.drawable.s100));
		twostimuli.add(new TwoImageStimulus(R.drawable.s101, R.drawable.s102));
		twostimuli.add(new TwoImageStimulus(R.drawable.s103, R.drawable.s104));
		twostimuli.add(new TwoImageStimulus(R.drawable.s041,
				R.drawable.androids_experimenter_kids));

		experiments.get(bat).getSubExperiments().get(28).setStimuli(twostimuli);

		/*
		 * SubExperiment 29: Reading sentences for comprehension uses 2 images
		 * per stimuli.
		 */
		twostimuli = null;
		twostimuli = new ArrayList<TwoImageStimulus>();

		twostimuli.add(new TwoImageStimulus(R.drawable.s105, R.drawable.s106));
		twostimuli.add(new TwoImageStimulus(R.drawable.s107, R.drawable.s108));
		twostimuli.add(new TwoImageStimulus(R.drawable.s109, R.drawable.s110));
		twostimuli.add(new TwoImageStimulus(R.drawable.s111, R.drawable.s112));
		twostimuli.add(new TwoImageStimulus(R.drawable.s113, R.drawable.s114));
		twostimuli.add(new TwoImageStimulus(R.drawable.s115, R.drawable.s116));
		twostimuli.add(new TwoImageStimulus(R.drawable.s117, R.drawable.s118));
		twostimuli.add(new TwoImageStimulus(R.drawable.s119, R.drawable.s120));
		twostimuli.add(new TwoImageStimulus(R.drawable.s121, R.drawable.s122));
		twostimuli.add(new TwoImageStimulus(R.drawable.s123, R.drawable.s124));
		twostimuli.add(new TwoImageStimulus(R.drawable.s125, R.drawable.s126));
		twostimuli.add(new TwoImageStimulus(R.drawable.s041,
				R.drawable.androids_experimenter_kids));

		experiments.get(bat).getSubExperiments().get(29).setStimuli(twostimuli);

	}

}

package ca.ilanguage.bilingualaphasiatest.content;

import java.io.File;
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
/**
 * AndroidBAT2.0 is an interactive OpenSource application of the Bilingual Aphasia Test
(BAT). The BAT was designed to assess each of the languages of a bilingual or multilingual
individual with aphasia in an equivalent way. [inutile] AndroidBAT2.0 is a ‘virtual paper’
of the original BAT paper version. Unlike a computer application, the AndroidBAT2.0 is
designed to simulate the flexibility of the original paper BAT, with the added benefit of
allowing for a diversity of data collection integrated directly into the test. AndroidBAT2.0
allows recording of a patient’s interview, without visible external camera or microphone,
while providing more analyzable data (e.g., eye-gaze, touch, etc.) than the paper format. Data
can then be easily transferred on the internet using Dropbox for instance. AndroidBAT2.0
works on tablets and phones.

 * @author iLanguage Lab LTD
 *
 */
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

		new File(outputDir+"video/").mkdirs();
		new File(outputDir+"writing/").mkdirs();
		new File(outputDir+"touchdata/").mkdirs();
		
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

		twostimuli.add(new TwoImageStimulus(R.drawable.s073, R.drawable.s002, "Begin"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s003, R.drawable.s004, "Practice"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s005, R.drawable.s006, "48"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s007, R.drawable.s008, "49"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s009, R.drawable.s010, "50"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s011, R.drawable.s012, "51"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s013, R.drawable.s014, "52"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s015, R.drawable.s016, "53"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s017, R.drawable.s018, "54"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s019, R.drawable.s020, "55"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s021, R.drawable.s022, "56"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s023, R.drawable.s024, "57"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s025, R.drawable.s026, "58"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s027, R.drawable.s028, "59"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s029, R.drawable.s030, "60"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s031, R.drawable.s032, "61"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s033, R.drawable.s034, "62"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s035, R.drawable.s036, "63"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s037, R.drawable.s038, "64"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s039, R.drawable.s040, "65"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s041,
				R.drawable.end, "End"));

		experiments.get(bat).getSubExperiments().get(6).setStimuli(twostimuli);
		twostimuli = null;

		/*
		 * SubExperiment 7: Syntactic Comprehension
		 */
		ArrayList<Stimulus> stimuli = new ArrayList<Stimulus>();

		stimuli.add(new Stimulus(R.drawable.s042, "Begin"));
		stimuli.add(new Stimulus(R.drawable.s043, "Practice"));
		
		stimuli.add(new Stimulus(R.drawable.s044, "66"));
		stimuli.add(new Stimulus(R.drawable.s044, "67"));
		stimuli.add(new Stimulus(R.drawable.s044, "68"));
		stimuli.add(new Stimulus(R.drawable.s044, "69"));
		stimuli.add(new Stimulus(R.drawable.s044, "70"));
		
		stimuli.add(new Stimulus(R.drawable.s045, "71"));
		stimuli.add(new Stimulus(R.drawable.s045, "72"));
		stimuli.add(new Stimulus(R.drawable.s045, "73"));
		stimuli.add(new Stimulus(R.drawable.s045, "74"));
		stimuli.add(new Stimulus(R.drawable.s045, "75"));
		stimuli.add(new Stimulus(R.drawable.s045, "76"));
		
		stimuli.add(new Stimulus(R.drawable.s046, "77"));
		stimuli.add(new Stimulus(R.drawable.s046, "78"));
		stimuli.add(new Stimulus(R.drawable.s046, "79"));
		stimuli.add(new Stimulus(R.drawable.s046, "80"));
		
		stimuli.add(new Stimulus(R.drawable.s047, "81"));
		stimuli.add(new Stimulus(R.drawable.s047, "82"));
		stimuli.add(new Stimulus(R.drawable.s047, "83"));
		stimuli.add(new Stimulus(R.drawable.s047, "84"));
		stimuli.add(new Stimulus(R.drawable.s047, "85"));
		stimuli.add(new Stimulus(R.drawable.s047, "86"));
		stimuli.add(new Stimulus(R.drawable.s047, "87"));
		stimuli.add(new Stimulus(R.drawable.s047, "88"));
		
		stimuli.add(new Stimulus(R.drawable.s048, "89"));
		stimuli.add(new Stimulus(R.drawable.s048, "90"));
		stimuli.add(new Stimulus(R.drawable.s048, "91"));
		stimuli.add(new Stimulus(R.drawable.s048, "92"));
		stimuli.add(new Stimulus(R.drawable.s048, "93"));
		stimuli.add(new Stimulus(R.drawable.s048, "94"));
		stimuli.add(new Stimulus(R.drawable.s048, "95"));
		stimuli.add(new Stimulus(R.drawable.s048, "96"));
		
		stimuli.add(new Stimulus(R.drawable.s049, "97"));
		stimuli.add(new Stimulus(R.drawable.s049, "98"));
		stimuli.add(new Stimulus(R.drawable.s049, "99"));
		stimuli.add(new Stimulus(R.drawable.s049, "100"));
		stimuli.add(new Stimulus(R.drawable.s049, "101"));
		stimuli.add(new Stimulus(R.drawable.s049, "102"));
		stimuli.add(new Stimulus(R.drawable.s049, "103"));
		stimuli.add(new Stimulus(R.drawable.s049, "104"));
		
		stimuli.add(new Stimulus(R.drawable.s050, "105"));
		stimuli.add(new Stimulus(R.drawable.s050, "106"));
		stimuli.add(new Stimulus(R.drawable.s050, "107"));
		stimuli.add(new Stimulus(R.drawable.s050, "108"));
		stimuli.add(new Stimulus(R.drawable.s050, "109"));
		stimuli.add(new Stimulus(R.drawable.s050, "110"));
		
		stimuli.add(new Stimulus(R.drawable.s051, "111"));
		stimuli.add(new Stimulus(R.drawable.s051, "112"));
		stimuli.add(new Stimulus(R.drawable.s051, "113"));
		stimuli.add(new Stimulus(R.drawable.s051, "114"));
		
		stimuli.add(new Stimulus(R.drawable.s052, "115"));
		stimuli.add(new Stimulus(R.drawable.s052, "116"));
		stimuli.add(new Stimulus(R.drawable.s052, "117"));
		stimuli.add(new Stimulus(R.drawable.s052, "118"));
		stimuli.add(new Stimulus(R.drawable.s052, "119"));
		stimuli.add(new Stimulus(R.drawable.s052, "120"));
		
		stimuli.add(new Stimulus(R.drawable.s053, "121"));
		stimuli.add(new Stimulus(R.drawable.s053, "122"));
		stimuli.add(new Stimulus(R.drawable.s053, "123"));
		stimuli.add(new Stimulus(R.drawable.s053, "124"));
		
		stimuli.add(new Stimulus(R.drawable.s054, "125"));
		stimuli.add(new Stimulus(R.drawable.s054, "126"));
		stimuli.add(new Stimulus(R.drawable.s054, "127"));
		stimuli.add(new Stimulus(R.drawable.s054, "128"));
		
		stimuli.add(new Stimulus(R.drawable.s055, "129"));
		stimuli.add(new Stimulus(R.drawable.s055, "130"));
		stimuli.add(new Stimulus(R.drawable.s055, "131"));
		stimuli.add(new Stimulus(R.drawable.s055, "132"));
		
		stimuli.add(new Stimulus(R.drawable.s056, "133"));
		stimuli.add(new Stimulus(R.drawable.s056, "134"));
		stimuli.add(new Stimulus(R.drawable.s056, "135"));
		stimuli.add(new Stimulus(R.drawable.s056, "136"));
		
		stimuli.add(new Stimulus(R.drawable.s057, "137"));
		stimuli.add(new Stimulus(R.drawable.s058, "138"));
		stimuli.add(new Stimulus(R.drawable.s059, "139"));
		stimuli.add(new Stimulus(R.drawable.s060, "140"));
		stimuli.add(new Stimulus(R.drawable.s061, "141"));
		stimuli.add(new Stimulus(R.drawable.s062, "142"));
		stimuli.add(new Stimulus(R.drawable.s063, "143"));
		stimuli.add(new Stimulus(R.drawable.s064, "144"));
		stimuli.add(new Stimulus(R.drawable.s065, "145"));
		stimuli.add(new Stimulus(R.drawable.s066, "146"));
		stimuli.add(new Stimulus(R.drawable.s067, "147"));
		stimuli.add(new Stimulus(R.drawable.s068, "148"));
		stimuli.add(new Stimulus(R.drawable.s069, "149"));
		stimuli.add(new Stimulus(R.drawable.s070, "150"));
		stimuli.add(new Stimulus(R.drawable.s071, "151"));
		stimuli.add(new Stimulus(R.drawable.s072, "152"));
		stimuli.add(new Stimulus(R.drawable.end, "End"));

		experiments.get(bat).getSubExperiments().get(7).setStimuli(stimuli);
		stimuli = null;

		/*
		 * SubExperiment 21: Description of a cartoon
		 */
		stimuli = new ArrayList<Stimulus>();
//		stimuli.add(new Stimulus(R.drawable.s073, ""));
		stimuli.add(new Stimulus(R.drawable.s074, "344"));
		stimuli.add(new Stimulus(R.drawable.end, "End"));
		experiments.get(bat).getSubExperiments().get(21).setStimuli(stimuli);
		stimuli = null;

		/*
		 * SubExperiment 24: Reading
		 */
		stimuli = new ArrayList<Stimulus>();
//		stimuli.add(new Stimulus(R.drawable.s073, ""));
		stimuli.add(new Stimulus(R.drawable.s075, "367-371"));
		stimuli.add(new Stimulus(R.drawable.s076, "372-376"));
		stimuli.add(new Stimulus(R.drawable.s077, "377-378"));
		stimuli.add(new Stimulus(R.drawable.s078, "379-380"));
		stimuli.add(new Stimulus(R.drawable.s079, "381-382"));
		stimuli.add(new Stimulus(R.drawable.s080, "383-384"));
		stimuli.add(new Stimulus(R.drawable.s081, "385-386"));
		stimuli.add(new Stimulus(R.drawable.s082, "387"));
		stimuli.add(new Stimulus(R.drawable.end, "End"));
		experiments.get(bat).getSubExperiments().get(24).setStimuli(stimuli);
		stimuli = null;

		/*
		 * SubExperiment 25: Copying
		 */
		stimuli = new ArrayList<Stimulus>();
//		stimuli.add(new Stimulus(R.drawable.s073, ""));
		stimuli.add(new Stimulus(R.drawable.s083, "393-397"));
		stimuli.add(new Stimulus(R.drawable.end, "End"));
		experiments.get(bat).getSubExperiments().get(25).setStimuli(stimuli);
		stimuli = null;

		/*
		 * SubExperiment 28: Reading words for comprehension uses 2 images per
		 * stimuli.
		 */

		twostimuli = new ArrayList<TwoImageStimulus>();

		twostimuli.add(new TwoImageStimulus(R.drawable.s073, R.drawable.s084, "Begin"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s085, R.drawable.s086, "408"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s087, R.drawable.s088, "409"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s089, R.drawable.s090, "410"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s091, R.drawable.s092, "411"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s093, R.drawable.s094, "412"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s095, R.drawable.s096, "413"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s097, R.drawable.s098, "414"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s099, R.drawable.s100, "415"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s101, R.drawable.s102, "416"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s103, R.drawable.s104, "417"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s041,
				R.drawable.end, "End"));

		experiments.get(bat).getSubExperiments().get(28).setStimuli(twostimuli);

		/*
		 * SubExperiment 29: Reading sentences for comprehension uses 2 images
		 * per stimuli.
		 */
		twostimuli = null;
		twostimuli = new ArrayList<TwoImageStimulus>();

		twostimuli.add(new TwoImageStimulus(R.drawable.s105, R.drawable.s106, "Begin"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s107, R.drawable.s108, "418"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s109, R.drawable.s110, "419"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s111, R.drawable.s112, "420"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s113, R.drawable.s114, "421"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s115, R.drawable.s116, "422"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s117, R.drawable.s118, "423"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s119, R.drawable.s120, "424"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s121, R.drawable.s122, "425"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s123, R.drawable.s124, "426"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s125, R.drawable.s126, "427"));
		twostimuli.add(new TwoImageStimulus(R.drawable.s041,
				R.drawable.end, "End"));

		experiments.get(bat).getSubExperiments().get(29).setStimuli(twostimuli);

	}

}

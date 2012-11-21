package ca.ilanguage.bilingualaphasiatest.content;

import java.util.ArrayList;

import ca.ilanguage.oprime.content.OPrime;
import ca.ilanguage.oprime.content.OPrimeApp;
import ca.ilanguage.oprime.content.Stimulus;
import ca.ilanguage.oprime.content.TwoImageStimulus;
import ca.ilanguage.bilingualaphasiatest.R;

/**
 * AndroidBAT2.0 is an interactive OpenSource application of the Bilingual
 * Aphasia Test (BAT). The BAT was designed to assess each of the languages of a
 * bilingual or multilingual individual with aphasia in an equivalent way.
 * [inutile] AndroidBAT2.0 is a ‘virtual paper’ of the original BAT paper
 * version. Unlike a computer application, the AndroidBAT2.0 is designed to
 * simulate the flexibility of the original paper BAT, with the added benefit of
 * allowing for a diversity of data collection integrated directly into the
 * test. AndroidBAT2.0 allows recording of a patient’s interview, without
 * visible external camera or microphone, while providing more analyzable data
 * (e.g., eye-gaze, touch, etc.) than the paper format. Data can then be easily
 * transferred on the internet using Dropbox for instance. AndroidBAT2.0 works
 * on tablets and phones.
 * 
 * @author iLanguage Lab ltd
 * 
 */
public class BilingualAphasiaTest extends OPrimeApp {
  public static final String TAG = "BilingualAphasiaTest";
  public static final boolean D = false;
  public static final String DEFAULT_OUTPUT_DIRECTORY = "/sdcard/BilingualAphasiaTest/";
  public static final String PREFERENCE_NAME = "BilingualAphasiaPrefs";

  @Override
  public void onCreate() {
    mOutputDir = DEFAULT_OUTPUT_DIRECTORY;
    super.onCreate();
  }

  @Override
  protected void addStimuli() {
    /*
     * SubExperiment 2: Spontaneous speech is a timer task of 5 minutes
     */
    ArrayList<Stimulus> stimuli = new ArrayList<Stimulus>();
    stimuli
        .add(new Stimulus(R.drawable.androids_experimenter_kids, "5 minutes"));
    mExperiments.get(mCurrentExperiment).getSubExperiments().get(2)
        .setStimuli(stimuli);
    mExperiments
        .get(mCurrentExperiment)
        .getSubExperiments()
        .get(2)
        .setIntentToCallThisSubExperiment(
            OPrime.INTENT_START_STOP_WATCH_SUB_EXPERIMENT);
    stimuli = null;

    /*
     * SubExperiment 6: Verbal Auditory Comprehension uses 2 images per stimuli.
     */
    ArrayList<TwoImageStimulus> twostimuli = new ArrayList<TwoImageStimulus>();

    twostimuli.add(new TwoImageStimulus(R.drawable.s073, R.drawable.s002,
        "Begin"));
    twostimuli.add(new TwoImageStimulus(R.drawable.s003, R.drawable.s004,
        "Practice"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s005, R.drawable.s006, "48"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s007, R.drawable.s008, "49"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s009, R.drawable.s010, "50"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s011, R.drawable.s012, "51"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s013, R.drawable.s014, "52"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s015, R.drawable.s016, "53"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s017, R.drawable.s018, "54"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s019, R.drawable.s020, "55"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s021, R.drawable.s022, "56"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s023, R.drawable.s024, "57"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s025, R.drawable.s026, "58"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s027, R.drawable.s028, "59"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s029, R.drawable.s030, "60"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s031, R.drawable.s032, "61"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s033, R.drawable.s034, "62"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s035, R.drawable.s036, "63"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s037, R.drawable.s038, "64"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s039, R.drawable.s040, "65"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s041, R.drawable.end, "End"));

    mExperiments.get(mCurrentExperiment).getSubExperiments().get(6)
        .setStimuli(twostimuli);
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(6)
    .setIntentToCallThisSubExperiment(
        OPrime.INTENT_START_TWO_IMAGE_SUB_EXPERIMENT);
    twostimuli = null;

    /*
     * SubExperiment 7: Syntactic Comprehension
     */
    stimuli = new ArrayList<Stimulus>();

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

    mExperiments.get(mCurrentExperiment).getSubExperiments().get(7)
        .setStimuli(stimuli);
    stimuli = null;

    /*
     * SubExperiment 15: Verbal Fluency is a timer task of 1 minute
     */
    stimuli = new ArrayList<Stimulus>();
    stimuli
        .add(new Stimulus(R.drawable.androids_experimenter_kids, "1 minute"));
    mExperiments.get(mCurrentExperiment).getSubExperiments().get(15)
        .setStimuli(stimuli);
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(15)
    .setIntentToCallThisSubExperiment(
        OPrime.INTENT_START_STOP_WATCH_SUB_EXPERIMENT);
    stimuli = null;

    /*
     * SubExperiment 21: Description of a cartoon, could try putting a timer of
     * suggested 2 minutes on it but that would distract the patient.
     */
    stimuli = new ArrayList<Stimulus>();
    // stimuli.add(new Stimulus(R.drawable.s073, ""));
    stimuli.add(new Stimulus(R.drawable.s074, "344"));
    stimuli.add(new Stimulus(R.drawable.end, "End"));
    mExperiments.get(mCurrentExperiment).getSubExperiments().get(21)
        .setStimuli(stimuli);
    stimuli = null;

    /*
     * SubExperiment 24: Reading
     */
    stimuli = new ArrayList<Stimulus>();
    // stimuli.add(new Stimulus(R.drawable.s073, ""));
    stimuli.add(new Stimulus(R.drawable.s075, "367-371"));
    stimuli.add(new Stimulus(R.drawable.s076, "372-376"));
    stimuli.add(new Stimulus(R.drawable.s077, "377-378"));
    stimuli.add(new Stimulus(R.drawable.s078, "379-380"));
    stimuli.add(new Stimulus(R.drawable.s079, "381-382"));
    stimuli.add(new Stimulus(R.drawable.s080, "383-384"));
    stimuli.add(new Stimulus(R.drawable.s081, "385-386"));
    stimuli.add(new Stimulus(R.drawable.s082, "387"));
    stimuli.add(new Stimulus(R.drawable.end, "End"));
    mExperiments.get(mCurrentExperiment).getSubExperiments().get(24)
        .setStimuli(stimuli);
    stimuli = null;

    /*
     * SubExperiment 25: Copying
     */
    stimuli = new ArrayList<Stimulus>();
    // stimuli.add(new Stimulus(R.drawable.s073, ""));
    stimuli.add(new Stimulus(R.drawable.s083, "393-397"));
    stimuli.add(new Stimulus(R.drawable.end, "End"));
    mExperiments.get(mCurrentExperiment).getSubExperiments().get(25)
        .setStimuli(stimuli);
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(25)
    .setIntentToCallAfterThisSubExperiment(
        OPrime.INTENT_TAKE_PICTURE);
    
    stimuli = null;
    
    /*
     * SubExperiment 26: xxxx
     */
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(26)
    .setIntentToCallAfterThisSubExperiment(
        OPrime.INTENT_TAKE_PICTURE);
    
    /*
     * SubExperiment 27: xxxx
     */
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(27)
    .setIntentToCallAfterThisSubExperiment(
        OPrime.INTENT_TAKE_PICTURE);
    
    /*
     * SubExperiment 28: Reading words for comprehension uses 2 images per
     * stimuli.
     */

    twostimuli = new ArrayList<TwoImageStimulus>();

    twostimuli.add(new TwoImageStimulus(R.drawable.s073, R.drawable.s084,
        "Begin"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s085, R.drawable.s086, "408"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s087, R.drawable.s088, "409"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s089, R.drawable.s090, "410"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s091, R.drawable.s092, "411"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s093, R.drawable.s094, "412"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s095, R.drawable.s096, "413"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s097, R.drawable.s098, "414"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s099, R.drawable.s100, "415"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s101, R.drawable.s102, "416"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s103, R.drawable.s104, "417"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s041, R.drawable.end, "End"));

    mExperiments.get(mCurrentExperiment).getSubExperiments().get(28)
        .setStimuli(twostimuli);
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(28)
    .setIntentToCallThisSubExperiment(
        OPrime.INTENT_START_TWO_IMAGE_SUB_EXPERIMENT);

    /*
     * SubExperiment 29: Reading sentences for comprehension uses 2 images per
     * stimuli.
     */
    twostimuli = null;
    twostimuli = new ArrayList<TwoImageStimulus>();

    twostimuli.add(new TwoImageStimulus(R.drawable.s105, R.drawable.s106,
        "Begin"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s107, R.drawable.s108, "418"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s109, R.drawable.s110, "419"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s111, R.drawable.s112, "420"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s113, R.drawable.s114, "421"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s115, R.drawable.s116, "422"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s117, R.drawable.s118, "423"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s119, R.drawable.s120, "424"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s121, R.drawable.s122, "425"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s123, R.drawable.s124, "426"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s125, R.drawable.s126, "427"));
    twostimuli
        .add(new TwoImageStimulus(R.drawable.s041, R.drawable.end, "End"));

    mExperiments.get(mCurrentExperiment).getSubExperiments().get(29)
        .setStimuli(twostimuli);
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(29)
    .setIntentToCallThisSubExperiment(
        OPrime.INTENT_START_TWO_IMAGE_SUB_EXPERIMENT);

    /*
     * SubExperiment 30: Writing is a timer task of 5 minutes
     */
    stimuli = new ArrayList<Stimulus>();
    stimuli
        .add(new Stimulus(R.drawable.androids_experimenter_kids, "5 minutes"));
    mExperiments.get(mCurrentExperiment).getSubExperiments().get(30)
        .setStimuli(stimuli);
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(30)
    .setIntentToCallThisSubExperiment(
        OPrime.INTENT_START_STOP_WATCH_SUB_EXPERIMENT);
    mExperiments
    .get(mCurrentExperiment)
    .getSubExperiments()
    .get(30)
    .setIntentToCallAfterThisSubExperiment(
        OPrime.INTENT_TAKE_PICTURE);
    
    stimuli = null;
  }

}

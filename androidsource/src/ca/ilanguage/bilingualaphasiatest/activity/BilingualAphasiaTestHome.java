package ca.ilanguage.bilingualaphasiatest.activity;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import ca.ilanguage.bilingualaphasiatest.R;
import ca.ilanguage.bilingualaphasiatest.content.BilingualAphasiaTest;
import ca.ilanguage.oprime.content.OPrime;
import ca.ilanguage.oprime.content.Participant;
import ca.ilanguage.oprime.content.SubExperimentBlock;
import ca.ilanguage.oprime.datacollection.VideoRecorder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class BilingualAphasiaTestHome extends Activity {
	protected static final String TAG = "BilingualAphasiaTest";
	public static final boolean D = true;
	private WebView mWebView;
	private Handler mHandlerDelayStimuli = new Handler();

	private int mCurrentSubex = 0;
	private BilingualAphasiaTest app;

	private Menu mMenu;

	private Boolean mAutoAdvance = false;
	GoogleAnalyticsTracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracker = GoogleAnalyticsTracker.getInstance();
		// Start the tracker in 20 sec interval dispatch mode...
		tracker.start("UA-30895446-1", 20, this);

		setContentView(R.layout.main_webview);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.addJavascriptInterface(new JavaScriptInterface(this),
				"Android");
		mWebView.setWebChromeClient(new MyWebChromeClient());
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setUserAgentString(webSettings.getUserAgentString() + " "
				+ getString(R.string.user_agent_suffix));
		app = (BilingualAphasiaTest) getApplication();

		Intent setupIntent = new Intent(getBaseContext(),
				ParticipantDetails.class);
		startActivityForResult(setupIntent, OPrime.PREPARE_TRIAL);

	}

	public class JavaScriptInterface {

		Context mContext;

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;

		}

		public void launchSubExperimentJS(final int subex) {
			mCurrentSubex = subex;
			startVideoRecorder();
			/*
			 * Wait two seconds so that the video activity has time to load the
			 * camera. It will continue recording until you exit the video
			 * activity.
			 */
			mHandlerDelayStimuli.postDelayed(new Runnable() {
				public void run() {
					// Toast.makeText(mContext,
					// "Launching subexperiment "+mCurrentSubex,
					// Toast.LENGTH_LONG).show();
					Intent intent;
					if (mCurrentSubex == 2 || mCurrentSubex == 30) {
						intent = new Intent(
								OPrime.INTENT_START_STOP_WATCH_SUB_EXPERIMENT);
					} else if (mCurrentSubex == 6 || mCurrentSubex == 28
							|| mCurrentSubex == 29) {
						intent = new Intent(
								OPrime.INTENT_START_TWO_IMAGE_SUB_EXPERIMENT);
					} else {
						intent = new Intent(OPrime.INTENT_START_SUB_EXPERIMENT);
					}
					intent.putExtra(OPrime.EXTRA_SUB_EXPERIMENT,
							((BilingualAphasiaTest) getApplication())
									.getSubExperiments().get(mCurrentSubex));
					intent.putExtra(OPrime.EXTRA_LANGUAGE,
							((BilingualAphasiaTest) getApplication())
									.getLanguage().getLanguage());

					startActivityForResult(intent, OPrime.EXPERIMENT_COMPLETED);

				}
			}, 2000);
		}

		public String fetchSubExperimentsArrayJS() {
			return ((BilingualAphasiaTest) getApplication())
					.getSubExperimentTitles().toString();
		}

		public String fetchParticipantCodesJS() {
			return "[the,codes]";
		}

		public String fetchExperimentTitleJS() {
			return ((BilingualAphasiaTest) getApplication()).getExperiment()
					.getTitle();// ((RoogleTankApp)
								// getApplication()).getLastMessage();
		}

		public void setAutoAdvanceJS(String autoadvance) {
			if (autoadvance.equals("1")) {
				mAutoAdvance = true;
			} else {
				mAutoAdvance = false;
			}

		}

		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
		}

		public String getVersionJIS() {
			String versionName;
			try {
				versionName = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				Log.d(TAG, "Exception trying to get app version");
				return "";
			}
			return versionName;
		}

		public int getDforDebuggingJIS() {
			if (D) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	class MyWebChromeClient extends WebChromeClient {
		public boolean onConsoleMessage(ConsoleMessage cm) {
			if (D)
				Log.d(TAG, cm.message() + " -- From line " + cm.lineNumber()
						+ " of " + cm.sourceId());
			return true;
		}
	}

	private void initExperiment() {
		getParticipantDetails();
		mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");

	}

	private void getParticipantDetails() {
		Participant p;
		try {
			p = app.getExperiment().getParticipant();
		} catch (Exception e) {
			p = new Participant();
		}
		SharedPreferences prefs = getSharedPreferences(
				BilingualAphasiaTest.PREFERENCE_NAME, MODE_PRIVATE);
		String firstname = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_PARTICIPANT_FIRSTNAME, "");
		String lastname = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_PARTICIPANT_LASTNAME, "");
		String experimenter = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_EXPERIEMENTER_CODE, "NN");
		String details = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_PARTICIPANT_DETAILS, "");
		String gender = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_PARTICIPANT_GENDER, "");
		String birthdate = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_PARTICIPANT_BIRTHDATE, "");
		String lang = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_EXPERIMENT_LANGUAGE, "en");
		// String langs =
		// prefs.getString(BilingualAphasiaTest.PREFERENCE_PARTICIPANT_LANGUAGES,
		// "");
		String testDayNumber = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_TESTING_DAY_NUMBER, "1");
		String participantNumberOnDay = prefs.getString(
				BilingualAphasiaTest.PREFERENCE_PARTICIPANT_NUMBER_IN_DAY, "1");
		/*
		 * Build the participant ID and save the start time to the preferences.
		 */
		p.setCode(testDayNumber + experimenter + participantNumberOnDay
				+ firstname.substring(0, 1).toUpperCase()
				+ lastname.substring(0, 1).toUpperCase());
		p.setFirstname(firstname);
		p.setLastname(lastname);
		p.setExperimenterCode(experimenter);
		p.setGender(gender);
		p.setBirthdate(birthdate);
		p.setDetails(details);

		if (app.getExperiment() == null) {
			app.createNewExperiment(lang);
		}
		app.getExperiment().setParticipant(p);
		Toast.makeText(getApplicationContext(), p.toCSVPrivateString(),
				Toast.LENGTH_LONG).show();

	}

	private void startVideoRecorder() {
		String outputDir = ((BilingualAphasiaTest) getApplication())
				.getOutputDir() + "video/";
		new File(outputDir).mkdirs();

		Intent intent;
		intent = new Intent(OPrime.INTENT_START_VIDEO_RECORDING);
		intent.putExtra(VideoRecorder.EXTRA_VIDEO_QUALITY,
				VideoRecorder.DEFAULT_DEBUGGING_QUALITY);
		intent.putExtra(VideoRecorder.EXTRA_USE_FRONT_FACING_CAMERA, true);
		String mDateString = (String) android.text.format.DateFormat.format(
				"yyyy-MM-dd_kk_mm",
				new java.util.Date(System.currentTimeMillis()));
		mDateString = mDateString.replaceAll("/", "-").replaceAll(" ", "-");

		String resultsFile = outputDir
				+ app.getExperiment().getParticipant().getCode()
				+ "_"
				+ app.getLanguage()
				+ mCurrentSubex
				+ "_"
				+ app.getSubExperiments().get(mCurrentSubex).getTitle()
						.replaceAll(" ", "_") + "-" + mDateString;
		intent.putExtra(OPrime.EXTRA_RESULT_FILENAME, resultsFile + ".3gp");
		app.getSubExperiments().get(mCurrentSubex)
				.setResultsFileWithoutSuffix(resultsFile);

		startActivity(intent);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case OPrime.EXPERIMENT_COMPLETED:
			if (data != null) {
				SubExperimentBlock completedExp = (SubExperimentBlock) data
						.getExtras().getSerializable(
								OPrime.EXTRA_SUB_EXPERIMENT);
				app.getSubExperiments().set(mCurrentSubex, completedExp);
				Intent i = new Intent(OPrime.INTENT_SAVE_SUB_EXPERIMENT_JSON);
				i.putExtra(OPrime.EXTRA_SUB_EXPERIMENT, (Serializable) app
						.getSubExperiments().get(mCurrentSubex));
				startService(i);
				app.getExperiment()
						.getParticipant()
						.setStatus(
								app.getExperiment().getParticipant()
										.getStatus()
										+ ":::"
										+ completedExp.getTitle()
										+ " in "
										+ (new Locale(completedExp
												.getLanguage()))
												.getDisplayLanguage()
										+ " --- "
										+ completedExp.getDisplayedStimuli()
										+ "/"
										+ completedExp.getStimuli().size()
										+ " Completed ");
				tracker.trackEvent(app.getExperiment().getParticipant().getCode(), // Category
                        "SubExperiment", // Action
                        completedExp.getTitle()
						+ " in "
						+ (new Locale(completedExp
								.getLanguage()))
								.getDisplayLanguage()
						+ " --- "
						+ completedExp.getDisplayedStimuli()
						+ "/"
						+ completedExp.getStimuli().size()
						+ " Completed " + System.currentTimeMillis() + " : ", // Label
                        (int) System.currentTimeMillis()); // Value



				app.writePrivateParticipantToFile();
			}
			stopVideoRecorder();
			if (mAutoAdvance) {
				mCurrentSubex++;
				if (mCurrentSubex >= app.getExperiment().getSubExperiments()
						.size()) {
					Toast.makeText(getApplicationContext(),
							"BAT Part A and B completed!", Toast.LENGTH_LONG)
							.show();
				} else {
					mWebView.loadUrl("javascript:getPositionAsButton(0,0,"
							+ mCurrentSubex + ")");
				}
			}
			break;
		case OPrime.PREPARE_TRIAL:
			initExperiment();
			break;
		case OPrime.SWITCH_LANGUAGE:
			break;
		case OPrime.REPLAY_RESULTS:
			break;
		default:
			break;
		}
	}

	private void stopVideoRecorder() {
		Intent i = new Intent(OPrime.INTENT_STOP_VIDEO_RECORDING);
		sendBroadcast(i);
		Intent audio = new Intent(OPrime.INTENT_START_AUDIO_RECORDING);
		stopService(audio);
		// Toast.makeText(this, "Subexperiment complete. ",
		// Toast.LENGTH_LONG).show();
	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Hold on to this
		mMenu = menu;

		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.open_settings:

			Intent i = new Intent(getBaseContext(), ParticipantDetails.class);
			startActivity(i);
			return true;
		case R.id.language_settings:
			Intent inte = new Intent(getBaseContext(), ParticipantDetails.class);
			startActivityForResult(inte, OPrime.SWITCH_LANGUAGE);
			return true;
		case R.id.result_folder:
			final boolean fileManagerAvailable = isIntentAvailable(this,
					"org.openintents.action.PICK_FILE");
			if (!fileManagerAvailable) {
				Toast.makeText(
						getApplicationContext(),
						"To open and export recorded files or "
								+ "draft data you can install the OI File Manager, "
								+ "it allows you to browse your SDCARD directly on your mobile device.",
						Toast.LENGTH_LONG).show();
				Intent goToMarket = new Intent(Intent.ACTION_VIEW)
						.setData(Uri
								.parse("market://details?id=org.openintents.filemanager"));
				startActivity(goToMarket);
			} else {
				Intent openResults = new Intent(
						"org.openintents.action.PICK_FILE");
				openResults.setData(Uri.parse("file://"
						+ ((BilingualAphasiaTest) getApplication())
								.getOutputDir()));
				startActivity(openResults);
			}
			// Intent intentReplay = new Intent(getBaseContext(),
			// ParticipantDetails.class);
			// startActivityForResult(intentReplay, OPrime.REPLAY_RESULTS);
			return true;

		case R.id.issue_tracker:

			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://code.google.com/p/aublog/issues/entry"));
			startActivity(browserIntent);
			return true;
		default:
			break;
		}

		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		/*
		 * Doing nothing makes the current redraw properly
		 */
	}

	@Override
	protected void onDestroy() {
		tracker.trackEvent(app.getExperiment().getParticipant().getCode(), // Category
				"Exit", // Action
				"Exit : " + System.currentTimeMillis() + " : ", // Label
				(int) System.currentTimeMillis()); // Value
		tracker.stop();// Stop the tracker when it is no longer needed.

		super.onDestroy();
	}

}

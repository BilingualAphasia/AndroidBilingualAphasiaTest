package ca.ilanguage.bilingualaphasiatest.activity;

import java.util.Locale;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import ca.ilanguage.oprime.R;
import ca.ilanguage.oprime.content.ExperimentJavaScriptInterface;
import ca.ilanguage.oprime.content.OPrime;
import ca.ilanguage.oprime.content.OPrimeApp;
import ca.ilanguage.bilingualaphasiatest.content.BilingualAphasiaTest;
import ca.ilanguage.oprime.activity.HTML5GameActivity;
import ca.ilanguage.oprime.content.SubExperimentBlock;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class BilingualAphasiaTestHome extends HTML5GameActivity {
  protected BilingualAphasiaTest BATapp;
  protected String mOutputDir = BilingualAphasiaTest.DEFAULT_OUTPUT_DIRECTORY;

  GoogleAnalyticsTracker tracker;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    this.app = (BilingualAphasiaTest) getApplication();
    this.BATapp = (BilingualAphasiaTest) getApplication();
    ((BilingualAphasiaTest) getApplication()).setOutputDir(mOutputDir);
    ((OPrimeApp) getApplication()).setOutputDir(mOutputDir);
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void setUpVariables() {
    TAG = BilingualAphasiaTest.getTag();
    D = BilingualAphasiaTest.isD();
    mOutputDir = BilingualAphasiaTest.DEFAULT_OUTPUT_DIRECTORY;
    mInitialAppServerUrl = "file:///android_asset/bilingual_aphasia_test_home.html";
    mJavaScriptInterface = new ExperimentJavaScriptInterface(D, TAG,
        mOutputDir, getApplicationContext(), this, "");
    mJavaScriptInterface.setContext(this);

    this.BATapp = (BilingualAphasiaTest) getApplication();

    tracker = GoogleAnalyticsTracker.getInstance();
    // Start the tracker in 20 sec interval dispatch mode...
    tracker.start("UA-30895446-1", 20, this);
  }

  @Override
  protected void checkIfNeedToPrepareExperiment(
      boolean activtySaysToPrepareExperiment) {
    boolean prepareExperiment = getIntent().getExtras().getBoolean(
        OPrime.EXTRA_PLEASE_PREPARE_EXPERIMENT, false);
    if (prepareExperiment || activtySaysToPrepareExperiment) {
      if (D) {
        Log.d(TAG,
            "BilingualAphasiaTestHome was asked to prepare the experiment.");
      }
      SharedPreferences prefs = getSharedPreferences(OPrimeApp.PREFERENCE_NAME,
          MODE_PRIVATE);
      String lang = prefs.getString(OPrimeApp.PREFERENCE_EXPERIMENT_LANGUAGE,
          "");
      boolean autoAdvanceStimuliOnTouch = prefs.getBoolean(
          OPrimeApp.PREFERENCE_EXPERIMENT_AUTO_ADVANCE_ON_TOUCH, false);
      // ((OPrimeApp) this.getApplication())
      // .setAutoAdvanceStimuliOnTouch(autoAdvanceStimuliOnTouch);
      if (BATapp.getLanguage().getLanguage().equals(lang)
          && BATapp.getExperiment() != null) {
        // do nothing if they didn't change the language
        if (D) {
          Log.d(TAG,
              "The Language has not changed, not preparing the experiment for "
                  + lang);
        }
      } else {
        if (lang == null) {
          lang = BATapp.getLanguage().getLanguage();
          if (D) {
            Log.d(TAG,
                "The Language was null, setting it to the tablets default language "
                    + lang);
          }
        }
        if (D) {
          Log.d(TAG, "Preparing the experiment for " + lang);
        }

        /*
         * Let the user know if the language is not there.
         */
        String availibleLanguages = "en,el,es,es-ES,fr,iu,iw,kn,ru";
        if (availibleLanguages.contains(lang)) {
          // do nothing, this language is supported
        } else {
          Locale templocale = new Locale(lang);
          new AlertDialog.Builder(this)
              .setTitle(
                  templocale.getDisplayLanguage(new Locale(lang))
                      + " stimuli are not currently in this App")
              .setMessage(
                  " We have only put ~8 BAT languages in the app (English, French, Spanish, Inuktitut, Hebrew, Russian, Kannada, Greek). Please contact us to request "
                      + templocale.getDisplayLanguage(new Locale(lang))
                      + " if you need it. Click OK to contact us. \n\nClick Cancel to choose another language.")
              .setPositiveButton(android.R.string.ok,
                  new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      /*
                       * Open a browser to the contact us page.
                       */
                      Intent browserIntent = new Intent(
                          Intent.ACTION_VIEW,
                          Uri.parse("https://docs.google.com/spreadsheet/viewform?formkey=dGpiRDhreGpmTFBmQ2FUTVVjVlhESHc6MQ"));
                      startActivity(browserIntent);

                      return;
                    }
                  })
              .setNegativeButton(android.R.string.cancel,
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      Intent i = new Intent(getBaseContext(),
                          BATParticipantActivity.class);
                      i.putExtra(OPrime.EXTRA_PLEASE_PREPARE_EXPERIMENT, true);
                      startActivity(i);
                      dialog.cancel();
                    }
                  }).setCancelable(true).create().show();
          // dont create experiment until user decides what to do
          return;
        }
        Locale templocale = new Locale(lang);
        final String finallang = lang;
        final boolean finalautoAdvanceStimuliOnTouch = autoAdvanceStimuliOnTouch;
        new AlertDialog.Builder(this)
            .setTitle(
                "Load " + templocale.getDisplayLanguage(new Locale(finallang))
                    + "?")
            .setMessage(
                "Do you want to load the "
                    + templocale.getDisplayLanguage(new Locale(finallang))
                    + " stimuli - auto-advance: "
                    + finalautoAdvanceStimuliOnTouch
                    + " \n\n(Your previous sub experiments have all been saved to the SDCard, you may switch between languages at any time.)")
            .setPositiveButton(android.R.string.ok,
                new AlertDialog.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    BATapp.createNewExperiment(finallang,
                        finalautoAdvanceStimuliOnTouch);
                    initExperiment();
                    return;
                  }
                })
            .setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                  }
                }).setCancelable(true).create().show();
      }
    }
  }

  @Override
  protected void getParticipantDetails(OPrimeApp app) {
    super.getParticipantDetails(this.BATapp);
  }

  @Override
  protected void initExperiment() {
    getParticipantDetails(this.BATapp);
    mWebView.loadUrl("file:///android_asset/bilingual_aphasia_test_home.html");
  }

  @Override
  protected void onDestroy() {
    try {
      tracker.trackEvent(BATapp.getExperiment().getParticipant().getCode(), // Category
          "Exit", // Action
          "Exit : " + System.currentTimeMillis() + " : ", // Label
          (int) System.currentTimeMillis()); // Value
      tracker.stop();// Stop the tracker when it is no longer needed.
    } catch (Exception e) {
      Log.e(
          TAG,
          "There was an error trying to get participant codes from the experiment. "
              + "Perhaps the experiemnt never began. this error was first seen on "
              + "Android market June 23 2012 8:27 am.");
    }
    super.onDestroy();
  }

  @Override
  protected void trackCompletedExperiment(SubExperimentBlock completedExp) {
    tracker.trackEvent(
        BATapp.getExperiment().getParticipant().getCode(), // Category
        "SubExperiment", // Action
        completedExp.getTitle() + " in "
            + (new Locale(completedExp.getLanguage())).getDisplayLanguage()
            + " --- " + completedExp.getDisplayedStimuli() + "/"
            + completedExp.getStimuli().size() + " Completed "
            + System.currentTimeMillis() + " : ", // Label
        (int) System.currentTimeMillis()); // Value
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.open_settings) {
      Intent i = new Intent(getBaseContext(), BATParticipantActivity.class);
      i.putExtra(OPrime.EXTRA_PLEASE_PREPARE_EXPERIMENT, true);
      startActivityForResult(i, OPrime.SWITCH_LANGUAGE);
      return true;
    } else if (item.getItemId() == R.id.language_settings) {
      Intent inte = new Intent(getBaseContext(), BATParticipantActivity.class);
      inte.putExtra(OPrime.EXTRA_PLEASE_PREPARE_EXPERIMENT, true);
      startActivityForResult(inte, OPrime.SWITCH_LANGUAGE);
      return true;
    } else if (item.getItemId() == R.id.open_bat_pdf) {
      // Intent browserIntent = new Intent(Intent.ACTION_VIEW,
      // Uri.parse(getString(R.string.bat_test_download_url)));
      // startActivity(browserIntent);

      String googleDocsUrl = "http://docs.google.com/viewer?url=";

      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(
          Uri.parse(googleDocsUrl + getString(R.string.bat_test_download_url)),
          "text/html");
      startActivity(intent);
      return true;
    } else if (item.getItemId() == R.id.result_folder) {
      final boolean fileManagerAvailable = isIntentAvailable(this,
          "org.openintents.action.PICK_FILE");
      if (!fileManagerAvailable) {
        Toast
            .makeText(
                getApplicationContext(),
                "To open and export recorded files or "
                    + "draft data you can install the OI File Manager, "
                    + "it allows you to browse your SDCARD directly on your mobile device."
                    + " There are other apps which allow you to view the files, but OI is the one this app uses when you click on this button",
                Toast.LENGTH_LONG).show();
        Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri
            .parse("market://details?id=org.openintents.filemanager"));
        startActivity(goToMarket);
      } else {
        Intent openResults = new Intent("org.openintents.action.PICK_FILE");
        openResults.setData(Uri.parse("file://" + mOutputDir));
        startActivity(openResults);
      }
      // Intent intentReplay = new Intent(getBaseContext(),
      // ParticipantDetails.class);
      // startActivityForResult(intentReplay, OPrime.REPLAY_RESULTS);
      return true;
    } else if (item.getItemId() == R.id.issue_tracker) {
      Intent browserIntent = new Intent(
          Intent.ACTION_VIEW,
          Uri.parse("https://docs.google.com/spreadsheet/viewform?formkey=dGpiRDhreGpmTFBmQ2FUTVVjVlhESHc6MQ"));
      startActivity(browserIntent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public BilingualAphasiaTest getApp() {
    return BATapp;
  }

  public void setApp(BilingualAphasiaTest BATapp) {
    this.BATapp = BATapp;
  }

}

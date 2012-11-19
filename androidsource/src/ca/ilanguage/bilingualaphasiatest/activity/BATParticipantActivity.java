package ca.ilanguage.bilingualaphasiatest.activity;

import android.content.Intent;
import ca.ilanguage.bilingualaphasiatest.content.BilingualAphasiaTest;
import ca.ilanguage.oprime.activity.ParticipantDetails;
import ca.ilanguage.oprime.content.OPrime;

public class BATParticipantActivity extends ParticipantDetails {

  @Override
  public void onBackPressed() {
    Intent i = new Intent(getBaseContext(), BilingualAphasiaTestHome.class);
    i.putExtra(OPrime.EXTRA_PLEASE_PREPARE_EXPERIMENT, true);
    i.putExtra(OPrime.EXTRA_DEBUG_MODE, BilingualAphasiaTest.D);
    i.putExtra(OPrime.EXTRA_TAG, BilingualAphasiaTest.TAG);
    startActivity(i);
    
    finish();
  }

}

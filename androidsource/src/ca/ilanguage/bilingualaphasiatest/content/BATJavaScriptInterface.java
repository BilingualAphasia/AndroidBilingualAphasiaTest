package ca.ilanguage.bilingualaphasiatest.content;

import android.content.Context;
import ca.ilanguage.bilingualaphasiatest.activity.BilingualAphasiaTestHome;
import ca.ilanguage.oprime.content.ExperimentJavaScriptInterface;

public class BATJavaScriptInterface extends ExperimentJavaScriptInterface {

  private static final long serialVersionUID = -5750335562314965063L;

  public BATJavaScriptInterface(boolean d, String tag, String outputDir,
      Context context, BilingualAphasiaTestHome UIParent, String assetsPrefix) {
    super(d, tag, outputDir, context, UIParent, assetsPrefix);
  }

}

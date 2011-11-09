/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ilanguage.oprime.bilingualaphasiatest.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import ca.ilanguage.oprime.bilingualaphasiatest.R;

public class SetPreferencesActivity extends PreferenceActivity implements 
		YesNoDialogPreference.YesNoDialogListener {
	String mBloggerAccount;
	String mBloggerPassword;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        getPreferenceManager().setSharedPreferencesName(PreferenceConstants.PREFERENCE_NAME);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        
        
    }

	public void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			SharedPreferences prefs = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			//editor.remove(PreferenceConstants.PREFERENCE_LEVEL_ROW);


			editor.commit();
			Toast.makeText(this, "Dialog was closed",
                    Toast.LENGTH_SHORT).show();
		}
	}

	
}

/*
 * Copyright (C) 2007 The Android Open Source Project
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

package ca.ilanguage.oprime.bilingualaphasiatest.service;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * Controller to start and stop a service. 

Demonstrates how to pass information to the service via extras

Clicking on the notification brings user here, this is where user can do extra actions, like schedule uplaods for later, import transcriptions into aublog?
TODO button to stop recording. (sent from dictationrecorderservice)
add buttons
Turn on wifi
Open aublog settings
Retry xxx audio file (add files to cue)
 */
public class NotifyingController extends Activity {
	private Uri mUri;
	GoogleAnalyticsTracker tracker;
	private String mAuBlogInstallId;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.notifying_controller);
      	Intent intent = new Intent(NotifyingController.this, AudioRecorderService.class);
      	stopService(intent);

      	onDestroy();

    }

    @Override
	protected void onDestroy() {
    	
		super.onDestroy();
	}
}


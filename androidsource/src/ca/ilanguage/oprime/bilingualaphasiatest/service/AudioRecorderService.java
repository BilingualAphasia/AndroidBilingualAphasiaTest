package ca.ilanguage.oprime.bilingualaphasiatest.service;

import java.io.IOException;

import ca.ilanguage.oprime.bilingualaphasiatest.ui.BilingualAphasiaTestHome;
import ca.ilanguage.oprime.bilingualaphasiatest.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.IBinder;

/**
 * A service to record and save a backup audio to the SDCard
 * 
 * Can be called from anywhere in Oprime.
 * 
 * Notes: *the service is started with startForeground which means it will
 * display a notification in the notification area that the uesr can click on
 * while its running. *the service can be turned off by the operating system if
 * its low in memory or for other reasons. In these cases the service tries to
 * save the audio, but there is no guarantee that the entire process will be
 * carried out. Make sure the audio file exists before you play it.
 * 
 * Sample client code: //Start dictation service Intent intent = new
 * Intent(this, DictationRecorderService.class); intent.setData(mUri);
 * intent.putExtra(DictationRecorderService.EXTRA_AUDIOFILE_FULL_PATH,
 * mAudioResultsFile);
 * intent.putExtra(DictationRecorderService.EXTRA_AUDIOFILE_STATUS,
 * mAudioResultsFileStatus); startService(intent); mRecording = true;
 * 
 * //Stop dictation service if (mRecording == true){ Intent intent = new
 * Intent(this, DictationRecorderService.class); stopService(intent); }
 * 
 * @author gina
 * 
 */
public class AudioRecorderService extends Service {
	protected static String TAG = "DictationRecorderService";
	private NotificationManager mNM;
	private Notification mNotification;
	private int NOTIFICATION = 7059;
	private PendingIntent mContentIntent;
	private int mIconId = R.drawable.stat_aublog;

	private String mAudioResultsFile = "";
	private String mAudioResultsFileStatus = "";
	public static final String EXTRA_AUDIOFILE_FULL_PATH = "audioFilePath";
	public static final String EXTRA_AUDIOFILE_STATUS = "audioFileStatus";
	public static final String EXTRA_DELEGATE_KILL_OPrime_TO_YOU = "killOPrime";
	public static final String EXTRA_DEVICE_INFO = "deviceInfo";

	private Long mStartTime;
	private Long mEndTime;
	private Long mTimeAudioWasRecorded;
	private String mAudioSource = "internal mic";;// bluetooth(record,play),
																								// phone(recordmic, play
																								// earpiece) for privacy,
																								// speaker(record mic, play
																								// speaker)
	private String mDeviceInfo = "";

	private MediaRecorder mRecorder;
	private Boolean mRecordingNow = false;

	// uri of the entry being edited.
	//private Uri mUri;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// The PendingIntent to launch our activity if the user selects this
		// notification
		Intent i = new Intent(this, NotifyingController.class);
		//i.setData(mUri);
		mContentIntent = PendingIntent.getActivity(this, 0, i, 0);

		mNotification = new Notification(mIconId, "OPrime recording in progress",
				System.currentTimeMillis());
		mNotification.setLatestEventInfo(this, "OPrime ", "Recording...",
				mContentIntent);
		mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

	}

	@Override
	public void onDestroy() {
		saveRecording();
		mNM.cancel(NOTIFICATION);
		super.onDestroy();

	}

	@Override
	public void onLowMemory() {
		saveRecording();
		mNM.cancel(NOTIFICATION);
		super.onLowMemory();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		startForeground(startId, mNotification);
		//mUri = intent.getData();
		/*
		 * get data from extras bundle, store it in the member variables
		 */
		try {
			mAudioResultsFile = intent.getExtras().getString(
					EXTRA_AUDIOFILE_FULL_PATH);
			mAudioResultsFileStatus = "";
			mDeviceInfo = intent.getExtras().getString(EXTRA_DEVICE_INFO);
			// Discard status which was sent.
			mAudioResultsFileStatus = "Recording service running";
		} catch (Exception e) {
			// Toast.makeText(SRTGeneratorActivity.this,
			// "Error "+e,Toast.LENGTH_LONG).show();
		}
		if (mAudioResultsFile.length() > 0) {
			mAudioResultsFileStatus = mAudioResultsFileStatus
					+ ":::Audio file name: " + mAudioResultsFile;
		} else {
			mAudioResultsFile = "/sdcard/temp.mp3";
			mAudioResultsFileStatus = mAudioResultsFileStatus + ":::"
					+ "Audio file name: No file recieved from OPrime using: "
					+ mAudioResultsFile;
		}

		mRecordingNow = true;
		mStartTime = System.currentTimeMillis();

		mAudioResultsFileStatus = mAudioResultsFileStatus + ":::"
				+ "Recording started." + ":::Audio source: " + mAudioSource
				+ ":::Device info: " + mDeviceInfo + ":::Start time: " + mStartTime;

		mRecorder = new MediaRecorder();
		try {
			// http://www.benmccann.com/dev-blog/android-audio-recording-tutorial/
			mRecordingNow = true;
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setAudioChannels(1); // mono
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			int sdk = android.os.Build.VERSION.SDK_INT;
			// gingerbread and up can have wide band ie 16,000 hz recordings (much
			// better for transcription)
			if (sdk >= 10) {
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
				mRecorder.setAudioSamplingRate(16000);
			} else {
				// other devices will have to use narrow band, ie 8,000 hz (same quality
				// as a phone call)
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			}
			mRecorder.setOutputFile(mAudioResultsFile);
			mRecorder.prepare();
			mStartTime = System.currentTimeMillis();
			mRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block

		} catch (IOException e) {
			// TODO Auto-generated catch block

		}

		// autofilled by eclipsereturn super.onStartCommand(intent, flags, startId);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	private void saveRecording() {
		String appendToContent = "";
		if (mRecorder != null) {
			/*
			 * if the recorder is running, save everything essentially simulating a
			 * click on the save button in the UI
			 */
			if (mRecordingNow == true) {
				/*
				 * Save recording
				 */
				mAudioResultsFileStatus = mAudioResultsFileStatus + ":::"
						+ "Recording stopped.";
				mEndTime = System.currentTimeMillis();
				mRecordingNow = false;
				try {
					mRecorder.stop();
					mRecorder.release();
				} catch (Exception e) {
					mAudioResultsFileStatus = mAudioResultsFileStatus
							+ ":::"
							+ "Recording not saved, your device does not support 16,000 hz recording.: "
							+ e;
				}
				mRecorder = null;
				mTimeAudioWasRecorded = mEndTime - mStartTime;

				appendToContent = "Attached a " + mTimeAudioWasRecorded / 1000
						+ " second Recording.\n";
				mAudioResultsFileStatus = mAudioResultsFileStatus + ":::"
						+ appendToContent;
				mAudioResultsFileStatus = mAudioResultsFileStatus + ":::"
						+ "Recording flagged for transcription.";

			} else {
				// this should not run
				mRecorder.release(); // this is called in the stop save recording
				mRecorder = null;
			}
		}
	}
}

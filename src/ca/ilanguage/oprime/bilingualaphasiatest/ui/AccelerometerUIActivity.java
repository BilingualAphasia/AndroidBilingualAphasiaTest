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

package ca.ilanguage.oprime.bilingualaphasiatest.ui;

import java.io.IOException;
import java.util.ArrayList;

import ca.ilanguage.oprime.bilingualaphasiatest.R;
import ca.ilanguage.oprime.bilingualaphasiatest.service.AudioRecorderService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.BitmapFactory.Options;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

/**
 * This is an example of using the accelerometer to integrate the device's
 * acceleration to a position using the Verlet method. This is illustrated with
 * a very simple particle system comprised of a few iron balls freely moving on
 * an inclined wooden table. The inclination of the virtual table is controlled
 * by the device's accelerometer.
 * 
 * @see SensorManager
 * @see SensorEvent
 * @see Sensor
 */

public class AccelerometerUIActivity extends Activity {

	private SimulationView mSimulationView;
	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private WakeLock mWakeLock;

	private static final int ENGLISH = 0;
	private static final int FRENCH = 1;
	//wait between stimuli, if 999 then wait until user input.
	private static int waitBetweenStimuli = 5;
	public static final String LANGUAGE = "language";
	String mAudioResultsFile;
	MediaRecorder mRecorder;
	private String mParticipantId = "12";
	private String mStimuliId = "1";
	MediaPlayer mp;
	OnCompletionListener mAudioFinishedListener;
	private static final String TAG = "AccelerometerUIActivity";
	private ArrayList<Integer> mStimuliAudio;
	private int mStimuliIndex =0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get an instance of the SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get an instance of the PowerManager
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

		// Get an instance of the WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mDisplay = mWindowManager.getDefaultDisplay();

		// Create a bright wake lock
		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				getClass().getName());

		// instantiate our simulation view and set it as the activity's content
		mSimulationView = new SimulationView(this);
		setContentView(mSimulationView);

		mAudioResultsFile = BilingualAphasiaTestHome.OUTPUT_DIRECTORY
				+ System.currentTimeMillis() + "_" + mParticipantId + "_" + mStimuliId
				+ ".mp3";
		Intent intent = new Intent(this, AudioRecorderService.class);
		// intent.setData(mUri);
		intent.putExtra(AudioRecorderService.EXTRA_AUDIOFILE_FULL_PATH,
				mAudioResultsFile);
		startService(intent);
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * when the activity is resumed, we acquire a wake-lock so that the screen
		 * stays on, since the user will likely not be fiddling with the screen or
		 * buttons.
		 */
		mWakeLock.acquire();

		// Start the simulation
		mSimulationView.startSimulation();


		mStimuliAudio = new ArrayList<Integer>();
		mStimuliAudio.add(R.raw.e_synonyms_instructions);
		mStimuliAudio.add(R.raw.e165);
		mStimuliAudio.add(R.raw.e166);
		mStimuliAudio.add(R.raw.e167);
		mStimuliIndex=0;
		playNext();
		
		// on completion listener to play next audio
		mAudioFinishedListener = new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mStimuliIndex++;
				playNext();
			}
		};
	}

	
	/**
	 * Play a sample with the Android MediaPLayer.
	 * http://stackoverflow.com/questions/2969242/problems-with-mediaplayer-raw-resources-stop-and-start
	 * @param resid Resource ID of the sample to play.
	 */
	private void playSample() {
		// if the index is outside of the array of stimuli
		if (mStimuliIndex >= mStimuliAudio.size()) {
			return;
		}
		AssetFileDescriptor afd = this.getResources().openRawResourceFd(
				mStimuliAudio.get(mStimuliIndex));
		try {
			mp.reset();
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
					afd.getDeclaredLength());
			mp.prepare();
			mp.start();
			mp.setOnCompletionListener(mAudioFinishedListener);
			afd.close();
		} catch (IllegalArgumentException e) {
			Log.e(TAG,
					"Unable to play audio queue do to exception: " + e.getMessage(), e);
		} catch (IllegalStateException e) {
			Log.e(TAG,
					"Unable to play audio queue do to exception: " + e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG,
					"Unable to play audio queue do to exception: " + e.getMessage(), e);
		}
	}
	public void playNext(){
		playSample();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		/*
		 * When the activity is paused, we make sure to stop the simulation, release
		 * our sensor resources and wake locks
		 */

		// Stop the simulation
		mSimulationView.stopSimulation();

		// and release our wake-lock
		mWakeLock.release();
	}

	@Override
	protected void onDestroy() {
		/*if(mp.isPlaying()){
			mp.stop();
		}*/
		if (mp != null) {
			mp.release();
			mp = null;
		}
		Intent intent = new Intent(this, AudioRecorderService.class);
		stopService(intent);
		super.onDestroy();
	}

	class SimulationView extends View implements SensorEventListener {
		// diameter of the balls in meters
		private static final float sBallDiameter = 0.004f;
		private static final float sBallDiameter2 = sBallDiameter * sBallDiameter;

		// friction of the virtual table and air
		private static final float sFriction = 0.1f;

		private Sensor mAccelerometer;
		private long mLastT;
		private float mLastDeltaT;

		private float mXDpi;
		private float mYDpi;
		private float mMetersToPixelsX;
		private float mMetersToPixelsY;
		private Bitmap mBitmap;
		private Bitmap mWood;
		private float mXOrigin;
		private float mYOrigin;
		private float mSensorX;
		private float mSensorY;
		private long mSensorTimeStamp;
		private long mCpuTimeStamp;
		private float mHorizontalBound;
		private float mVerticalBound;
		private final ParticleSystem mParticleSystem = new ParticleSystem();

		/*
		 * Each of our particle holds its previous and current position, its
		 * acceleration. for added realism each particle has its own friction
		 * coefficient.
		 */
		class Particle {
			private float mPosX;
			private float mPosY;
			private float mAccelX;
			private float mAccelY;
			private float mLastPosX;
			private float mLastPosY;
			private float mOneMinusFriction;

			Particle() {
				// make each particle a bit different by randomizing its
				// coefficient of friction
				final float r = ((float) Math.random() - 0.5f) * 0.2f;
				mOneMinusFriction = 1.0f - sFriction + r;
			}

			public void computePhysics(float sx, float sy, float dT, float dTC) {
				// Force of gravity applied to our virtual object
				final float m = 1000.0f; // mass of our virtual object
				final float gx = -sx * m;
				final float gy = -sy * m;

				/*
				 * �F = mA <=> A = �F / m We could simplify the code by completely
				 * eliminating "m" (the mass) from all the equations, but it would hide
				 * the concepts from this sample code.
				 */
				final float invm = 1.0f / m;
				final float ax = gx * invm;
				final float ay = gy * invm;

				/*
				 * Time-corrected Verlet integration The position Verlet integrator is
				 * defined as x(t+�t) = x(t) + x(t) - x(t-�t) + a(t)�t�2 However, the
				 * above equation doesn't handle variable �t very well, a time-corrected
				 * version is needed: x(t+�t) = x(t) + (x(t) - x(t-�t)) * (�t/�t_prev) +
				 * a(t)�t�2 We also add a simple friction term (f) to the equation:
				 * x(t+�t) = x(t) + (1-f) * (x(t) - x(t-�t)) * (�t/�t_prev) + a(t)�t�2
				 */
				final float dTdT = dT * dT;
				final float x = mPosX + mOneMinusFriction * dTC * (mPosX - mLastPosX)
						+ mAccelX * dTdT;
				final float y = mPosY + mOneMinusFriction * dTC * (mPosY - mLastPosY)
						+ mAccelY * dTdT;
				mLastPosX = mPosX;
				mLastPosY = mPosY;
				mPosX = x;
				mPosY = y;
				mAccelX = ax;
				mAccelY = ay;
			}

			/*
			 * Resolving constraints and collisions with the Verlet integrator can be
			 * very simple, we simply need to move a colliding or constrained particle
			 * in such way that the constraint is satisfied.
			 */
			public void resolveCollisionWithBounds() {
				final float xmax = mHorizontalBound;
				final float ymax = mVerticalBound;
				final float x = mPosX;
				final float y = mPosY;
				if (x > xmax) {
					mPosX = xmax;
				} else if (x < -xmax) {
					mPosX = -xmax;
				}
				if (y > ymax) {
					mPosY = ymax;
				} else if (y < -ymax) {
					mPosY = -ymax;
				}
			}
		}

		/*
		 * A particle system is just a collection of particles
		 */
		class ParticleSystem {
			static final int NUM_PARTICLES = 15;
			private Particle mBalls[] = new Particle[NUM_PARTICLES];

			ParticleSystem() {
				/*
				 * Initially our particles have no speed or acceleration
				 */
				for (int i = 0; i < mBalls.length; i++) {
					mBalls[i] = new Particle();
				}
			}

			/*
			 * Update the position of each particle in the system using the Verlet
			 * integrator.
			 */
			private void updatePositions(float sx, float sy, long timestamp) {
				final long t = timestamp;
				if (mLastT != 0) {
					final float dT = (float) (t - mLastT) * (1.0f / 1000000000.0f);
					if (mLastDeltaT != 0) {
						final float dTC = dT / mLastDeltaT;
						final int count = mBalls.length;
						for (int i = 0; i < count; i++) {
							Particle ball = mBalls[i];
							ball.computePhysics(sx, sy, dT, dTC);
						}
					}
					mLastDeltaT = dT;
				}
				mLastT = t;
			}

			/*
			 * Performs one iteration of the simulation. First updating the position
			 * of all the particles and resolving the constraints and collisions.
			 */
			public void update(float sx, float sy, long now) {
				// update the system's positions
				updatePositions(sx, sy, now);

				// We do no more than a limited number of iterations
				final int NUM_MAX_ITERATIONS = 10;

				/*
				 * Resolve collisions, each particle is tested against every other
				 * particle for collision. If a collision is detected the particle is
				 * moved away using a virtual spring of infinite stiffness.
				 */
				boolean more = true;
				final int count = mBalls.length;
				for (int k = 0; k < NUM_MAX_ITERATIONS && more; k++) {
					more = false;
					for (int i = 0; i < count; i++) {
						Particle curr = mBalls[i];
						for (int j = i + 1; j < count; j++) {
							Particle ball = mBalls[j];
							float dx = ball.mPosX - curr.mPosX;
							float dy = ball.mPosY - curr.mPosY;
							float dd = dx * dx + dy * dy;
							// Check for collisions
							if (dd <= sBallDiameter2) {
								/*
								 * add a little bit of entropy, after nothing is perfect in the
								 * universe.
								 */
								dx += ((float) Math.random() - 0.5f) * 0.0001f;
								dy += ((float) Math.random() - 0.5f) * 0.0001f;
								dd = dx * dx + dy * dy;
								// simulate the spring
								final float d = (float) Math.sqrt(dd);
								final float c = (0.5f * (sBallDiameter - d)) / d;
								curr.mPosX -= dx * c;
								curr.mPosY -= dy * c;
								ball.mPosX += dx * c;
								ball.mPosY += dy * c;
								more = true;
							}
						}
						/*
						 * Finally make sure the particle doesn't intersects with the walls.
						 */
						curr.resolveCollisionWithBounds();
					}
				}
			}

			public int getParticleCount() {
				return mBalls.length;
			}

			public float getPosX(int i) {
				return mBalls[i].mPosX;
			}

			public float getPosY(int i) {
				return mBalls[i].mPosY;
			}
		}

		public void startSimulation() {
			/*
			 * It is not necessary to get accelerometer events at a very high rate, by
			 * using a slower rate (SENSOR_DELAY_UI), we get an automatic low-pass
			 * filter, which "extracts" the gravity component of the acceleration. As
			 * an added benefit, we use less power and CPU resources.
			 */
			mSensorManager.registerListener(this, mAccelerometer,
					SensorManager.SENSOR_DELAY_UI);
		}

		public void stopSimulation() {
			mSensorManager.unregisterListener(this);
		}

		public SimulationView(Context context) {
			super(context);
			mAccelerometer = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			mXDpi = metrics.xdpi;
			mYDpi = metrics.ydpi;
			mMetersToPixelsX = mXDpi / 0.0254f;
			mMetersToPixelsY = mYDpi / 0.0254f;

			// rescale the ball so it's about 0.5 cm on screen
			Bitmap ball = BitmapFactory.decodeResource(getResources(),
					R.drawable.ball);
			final int dstWidth = (int) (sBallDiameter * mMetersToPixelsX + 0.5f);
			final int dstHeight = (int) (sBallDiameter * mMetersToPixelsY + 0.5f);
			mBitmap = Bitmap.createScaledBitmap(ball, dstWidth, dstHeight, true);

			Options opts = new Options();
			opts.inDither = true;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			mWood = BitmapFactory.decodeResource(getResources(), R.drawable.wood,
					opts);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			// compute the origin of the screen relative to the origin of
			// the bitmap
			mXOrigin = (w - mBitmap.getWidth()) * 0.5f;
			mYOrigin = (h - mBitmap.getHeight()) * 0.5f;
			mHorizontalBound = ((w / mMetersToPixelsX - sBallDiameter) * 0.5f);
			mVerticalBound = ((h / mMetersToPixelsY - sBallDiameter) * 0.5f);
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
				return;
			/*
			 * record the accelerometer data, the event's timestamp as well as the
			 * current time. The latter is needed so we can calculate the "present"
			 * time during rendering. In this application, we need to take into
			 * account how the screen is rotated with respect to the sensors (which
			 * always return data in a coordinate space aligned to with the screen in
			 * its native orientation).
			 */

			switch (mDisplay.getRotation()) {
			case Surface.ROTATION_0:
				mSensorX = event.values[0];
				mSensorY = event.values[1];
				break;
			case Surface.ROTATION_90:
				mSensorX = -event.values[1];
				mSensorY = event.values[0];
				break;
			case Surface.ROTATION_180:
				mSensorX = -event.values[0];
				mSensorY = -event.values[1];
				break;
			case Surface.ROTATION_270:
				mSensorX = event.values[1];
				mSensorY = -event.values[0];
				break;
			}

			mSensorTimeStamp = event.timestamp;
			mCpuTimeStamp = System.nanoTime();
		}

		@Override
		protected void onDraw(Canvas canvas) {

			/*
			 * draw the background
			 */

			canvas.drawBitmap(mWood, 0, 0, null);

			/*
			 * compute the new position of our object, based on accelerometer data and
			 * present time.
			 */

			final ParticleSystem particleSystem = mParticleSystem;
			final long now = mSensorTimeStamp + (System.nanoTime() - mCpuTimeStamp);
			final float sx = mSensorX;
			final float sy = mSensorY;

			particleSystem.update(sx, sy, now);

			final float xc = mXOrigin;
			final float yc = mYOrigin;
			final float xs = mMetersToPixelsX;
			final float ys = mMetersToPixelsY;
			final Bitmap bitmap = mBitmap;
			final int count = particleSystem.getParticleCount();
			for (int i = 0; i < count; i++) {
				/*
				 * We transform the canvas so that the coordinate system matches the
				 * sensors coordinate system with the origin in the center of the screen
				 * and the unit is the meter.
				 */

				final float x = xc + particleSystem.getPosX(i) * xs;
				final float y = yc - particleSystem.getPosY(i) * ys;
				canvas.drawBitmap(bitmap, x, y, null);
			}

			// and make sure to redraw asap
			invalidate();
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}
}

package ca.ilanguage.oprime.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ca.ilanguage.oprime.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;

public class PresentAnImageStimuliActivity extends Activity {
	private static final String TAG = "PresentAnImageStimuli";
	
	private Long mStartTime;
    private Long mEndTime;
    private Long mReactionTime;
    private Long mTimeDisplayImage;
    private Long mTimeDisplayMask;
    private String mVideoResultsFile;
    private String mImageFile;
    private String mAudioFile;
    private ImageView mImage;
    private String mMaskImageFile;
    
    
    //to have participant draw on images look at APIDemo>graphics>fingerprint
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    setContentView(R.layout.activity_present_one_image);
	    mImage = (ImageView) findViewById(R.id.oneimage);
//	    mImage.setImageResource(R.drawable.sample_0);
	    mImageFile ="/sdcard/OPrime/BilingualAphasiaTest/images/BAT-E1.png";    

	    FileInputStream in;
        BufferedInputStream buf;
        try {
       	    in = new FileInputStream(mImageFile);
            buf = new BufferedInputStream(in);
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            mImage.setImageBitmap(bMap);
            if (in != null) {
         	in.close();
            }
            if (buf != null) {
         	buf.close();
            }
        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }
 
        //MediaPlayer mp = MediaPlayer.create(this, R.raw.click_on_dog_coat);
	    MediaPlayer mp = new MediaPlayer();
	    mAudioFile ="/sdcard/OPrime/BilingualAphasiaTest/audio/e66.mp3";    
	    try {
			mp.setDataSource(mAudioFile);
			mp.prepare();
	        mp.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Log.e("Error reading file", e.toString());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Log.e("Error reading file", e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("Error reading file", e.toString());
		}
		
	    
	    
    }
    
}

package ca.ilanguage.oprime.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ca.ilanguage.oprime.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;

public class ThankYouActivity extends Activity {
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
	    
	    setContentView(R.layout.logo_copyright);
	    mImage = (ImageView) findViewById(R.id.mainimage);
	    mImage.setImageResource(R.drawable.androids_experimenter_kids);
	    
 
        TextView thankyou = (TextView) findViewById(R.id.copyright);
        thankyou.setText("Merci!");
        
        ImageButton settings = (ImageButton) findViewById(R.id.nextButton);
        settings.setImageResource(R.drawable.ic_settings);
        
		
    }
    public void onNextClick(View v){
    	
    	//startActivity(new Intent(this, OPrimeHomeActivity.class));
    	startActivity(new Intent(this, ListofExperimentsActivity.class));
    }
    
}

package ca.ilanguage.oprime.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import ca.ilanguage.oprime.R;

public class TextEntryActivity extends Activity {
    private EditText et;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enter_participant_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        // title
        try {
            String s = getIntent().getExtras().getString("title");
            if (s.length() > 0) {
                this.setTitle(s);
            }
        } catch (Exception e) {
        }
        // value

        try {
            et = ((EditText) findViewById(R.id.participant_name_edit));
            et.setText(getIntent().getExtras().getString("value"));
        } catch (Exception e) {
        }
        
    }
    public void onOkayClick(View v){
    	executeDone();
		//startActivity(new Intent(this, OPrimeHomeActivity.class));
		//startActivity(new Intent(this, RunExperimentActivity.class));
	}
    public void onCancelClick(View v){

		//startActivity(new Intent(this, OPrimeHomeActivity.class));
		//startActivity(new Intent(this, RunExperimentActivity.class));
	}

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        executeDone();
        super.onBackPressed();
    }

    /**
     *
     */
    private void executeDone() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("value", TextEntryActivity.this.et.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


}
package ca.ilanguage.oprime.ui;
/*
 * http://stackoverflow.com/questions/3061249/opening-a-dialog-with-text-input-from-within-a-view-in-android
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import ca.ilanguage.oprime.R;

public class TextEntryActivity extends Activity {
    private EditText etName;
    private EditText etAge;

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
            etName = ((EditText) findViewById(R.id.participant_name_edit));
            etName.setText(getIntent().getExtras().getString("name"));
            etAge = ((EditText) findViewById(R.id.participant_age_edit));
            etAge.setText(getIntent().getExtras().getString("age"));
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
        resultIntent.putExtra("name", TextEntryActivity.this.etName.getText().toString());
        resultIntent.putExtra("age", TextEntryActivity.this.etAge.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


}
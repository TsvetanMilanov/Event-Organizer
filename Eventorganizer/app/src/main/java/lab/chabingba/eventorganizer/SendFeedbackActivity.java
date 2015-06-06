package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Visual.CustomFeedbackCategorySpinnerItem;

/**
 * Created by Tsvetan on 2015-06-06.
 */
public class SendFeedbackActivity extends Activity {
    Spinner spinnerCategory;
    EditText etContent;
    Button buttonSend;
    String category;
    String[] email;
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_feedback_activity_layout);

        spinnerCategory = (Spinner) findViewById(R.id.spinnerSelectFBCategory);
        etContent = (EditText) findViewById(R.id.etFBContent);
        buttonSend = (Button) findViewById(R.id.buttonSendFB);

        ArrayAdapter<String> arrayAdapter = new CustomFeedbackCategorySpinnerItem(SendFeedbackActivity.this, GlobalConstants.FEEDBACK_CATEGORIES);

        spinnerCategory.setAdapter(arrayAdapter);
    }

    public void onSendFeedbackClicked(View view) {
        category = spinnerCategory.getSelectedItem().toString();
        email = new String[]{GlobalConstants.EMAIL_FOR_FEEDBACK};
        content = category + ":\n" + etContent.getText().toString();

        Intent intentForEmail = new Intent(Intent.ACTION_SEND);

        intentForEmail.putExtra(Intent.EXTRA_EMAIL, email);
        intentForEmail.putExtra(Intent.EXTRA_SUBJECT, GlobalConstants.EMAIL_SUBJECT);
        intentForEmail.setType("plain/text");
        intentForEmail.putExtra(Intent.EXTRA_TEXT, content);
        
        startActivity(intentForEmail);
    }
}

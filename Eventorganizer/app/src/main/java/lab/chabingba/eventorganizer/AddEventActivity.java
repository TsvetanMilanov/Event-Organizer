package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.AbstractList;
import java.util.Calendar;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.Visual.CustomSpinnerItem;

/**
 * Created by Tsvetan on 2015-05-27.
 */
public class AddEventActivity extends Activity {
    private MyEvent eventToAdd;
    private DBHandler database;
    private Category category;
    private String tableToAddTo;
    private AbstractList<String> listOfEventTypes;

    String eventType;
    String eventDescription;
    String eventLocation;
    String eventDateAndHour;
    boolean isFinished;

    Spinner spinnerType;
    EditText etDescription;
    EditText etLocation;
    DatePicker datePicker;
    TimePicker timePicker;
    CheckBox cbIsFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_and_edit_event_activity_layout);

        int currentDatabaseVersion = GeneralHelpers.getCurrentDatabaseVersion(this);

        database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

        category = (Category) getIntent().getSerializableExtra(GlobalConstants.CATEGORY_WORD);

        this.tableToAddTo = category.getSQLName();
        this.listOfEventTypes = database.getEventTypesAsArray();

        spinnerType = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> arrayAdapter = new CustomSpinnerItem(AddEventActivity.this, this.listOfEventTypes);

        spinnerType.setAdapter(arrayAdapter);

        etDescription = (EditText) findViewById(R.id.etAddDescription);

        etLocation = (EditText) findViewById(R.id.etAddLocation);

        datePicker = (DatePicker) findViewById(R.id.datePicker);

        Calendar currentDate = Calendar.getInstance();

        datePicker.updateDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        timePicker.setCurrentHour(currentDate.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(currentDate.get(Calendar.MINUTE));

        cbIsFinished = (CheckBox) findViewById(R.id.cbAddIsFinished);
    }

    public void onCancelClicked(View v) {
        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(AddEventActivity.this, this.category, false);

        startActivity(intent);
        finish();
    }
    
    public void onSaveClicked(View v) {

        eventType = (String) spinnerType.getSelectedItem();

        eventLocation = etLocation.getText().toString();

        eventDescription = etDescription.getText().toString();

        eventDateAndHour = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + ":" + "00";

        isFinished = cbIsFinished.isChecked();

        this.eventToAdd = new MyEvent(eventType, eventDateAndHour, eventLocation, eventDescription, isFinished, isFinished, isFinished);

        this.database.addEvent(eventToAdd, this.tableToAddTo);

        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(AddEventActivity.this, this.category, false);

        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(AddEventActivity.this, this.category, false);

        startActivity(intent);
        finish();
    }
}

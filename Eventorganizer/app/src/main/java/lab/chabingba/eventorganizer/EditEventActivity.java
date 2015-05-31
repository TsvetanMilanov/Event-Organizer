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
public class EditEventActivity extends Activity {
    private MyEvent currentEvent;
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

        currentEvent = (MyEvent) getIntent().getSerializableExtra(GlobalConstants.EVENT_WORD);

        this.tableToAddTo = category.getSQLName();
        this.listOfEventTypes = database.getEventTypesAsArray();

        spinnerType = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> arrayAdapter = new CustomSpinnerItem(EditEventActivity.this, this.listOfEventTypes);

        spinnerType.setAdapter(arrayAdapter);

        int currentIndexOfSpinnerItem = this.listOfEventTypes.indexOf(currentEvent.getType());

        spinnerType.setSelection(currentIndexOfSpinnerItem);

        etDescription = (EditText) findViewById(R.id.etAddDescription);
        etDescription.setText(currentEvent.getDescription());

        etLocation = (EditText) findViewById(R.id.etAddLocation);
        etLocation.setText(currentEvent.getLocation());

        datePicker = (DatePicker) findViewById(R.id.datePicker);

        int year = currentEvent.getDate().get(Calendar.YEAR);
        int month = currentEvent.getDate().get(Calendar.MONTH);
        int day = currentEvent.getDate().get(Calendar.DAY_OF_MONTH);

        datePicker.updateDate(year, month, day);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        int hour = currentEvent.getDate().get(Calendar.HOUR_OF_DAY);
        int minute = currentEvent.getDate().get(Calendar.MINUTE);

        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        cbIsFinished = (CheckBox) findViewById(R.id.cbAddIsFinished);
        cbIsFinished.setChecked(currentEvent.getIsFinished());
    }

    public void onCancelClicked(View v) {
        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(EditEventActivity.this, this.category, false);

        startActivity(intent);
        finish();
    }

    public void onSaveClicked(View v) {

        eventType = (String) spinnerType.getSelectedItem();

        eventLocation = etLocation.getText().toString();

        eventDescription = etDescription.getText().toString();

        eventDateAndHour = datePicker.getYear() + "-" + datePicker.getMonth() + 1 + "-" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + ":" + "00";

        isFinished = cbIsFinished.isChecked();

        MyEvent editedEvent = new MyEvent(currentEvent.getId(), eventType, eventDateAndHour, eventLocation, eventDescription, isFinished, currentEvent.getHasNotification(), currentEvent.getIsOld());

        database.updateEvent(editedEvent, tableToAddTo);

        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(EditEventActivity.this, this.category, editedEvent.getIsOld());

        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(EditEventActivity.this, this.category, this.currentEvent.getIsOld());

        startActivity(intent);
        finish();
    }
}

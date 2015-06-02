package lab.chabingba.eventorganizer;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Visual.CustomExpandableListAdapters.CustomExpandableListAdapter;

/**
 * Created by Tsvetan on 2015-05-26.
 */
public class CurrentEventsActivity extends ExpandableListActivity {
    private ExpandableListView expandableListView;
    private ArrayList<MyEvent> listOfEvents;
    private DBHandler database;
    private String tableName = GlobalConstants.EMPTY_STRING;
    private Category category;
    private boolean loadOldEvents;
    private ArrayList<EventOfCategory> listOfEventsForNotification;
    private boolean loadTodayEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_events_activity_layout);

        int currentDatabaseVersion = GeneralHelpers.getCurrentDatabaseVersion(this);

        database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

        category = (Category) getIntent().getSerializableExtra(GlobalConstants.CATEGORY_WORD);

        tableName = category.getSQLName();

        TextView tvCategoryName = (TextView) findViewById(R.id.tvCurrentCategory);

        tvCategoryName.setText(category.getName());

        loadOldEvents = getIntent().getBooleanExtra(GlobalConstants.LOAD_OLD_EVENTS_TEXT, false);
        loadTodayEvents = getIntent().getBooleanExtra(GlobalConstants.LOAD_TODAYS_EVENTS_TEXT, false);

        listOfEventsForNotification = (ArrayList<EventOfCategory>) getIntent().getSerializableExtra(GlobalConstants.EVENTS_FOR_NOTIFICATION_TEXT);

        expandableListView = (ExpandableListView) findViewById(android.R.id.list);

        //region settings for which events to load
        if (loadOldEvents == true) {
            listOfEvents = GeneralHelpers.selectOldEvents(database.createListWithEventsFromTable(tableName));
            tvCategoryName.append(GlobalConstants.OLD_EVENTS_TEXT_TO_APPEND);
            ImageButton imageButtonAdd = (ImageButton) findViewById(R.id.imageButtonAddEvent);
            imageButtonAdd.setVisibility(View.GONE);
            ImageButton imageButtonOldEvents = (ImageButton) findViewById(R.id.imageButtonOldEvents);
            imageButtonOldEvents.setVisibility(View.GONE);
            tvCategoryName.setPadding(0, 0, GlobalConstants.CATEGORY_TEXT_VIEW_PADDING_RIGHT, 0);
        } else if (loadTodayEvents) {
            listOfEvents = GeneralHelpers.createListOfEventsFromEventOfCategoryArray(listOfEventsForNotification);
            tvCategoryName.setText("Events for today");
            ImageButton imageButtonAdd = (ImageButton) findViewById(R.id.imageButtonAddEvent);
            imageButtonAdd.setVisibility(View.GONE);
            ImageButton imageButtonOldEvents = (ImageButton) findViewById(R.id.imageButtonOldEvents);
            imageButtonOldEvents.setVisibility(View.GONE);
            ImageButton imageButtonRemoveEvents = (ImageButton) findViewById(R.id.imageButtonRemoveEvent);
            imageButtonRemoveEvents.setVisibility(View.GONE);
        } else {
            listOfEvents = GeneralHelpers.selectCurrentEvents(database.createListWithEventsFromTable(tableName));
        }
        //endregion

        if (listOfEventsForNotification.size() > 0) {
            expandableListView.setAdapter(new CustomExpandableListAdapter(this, listOfEventsForNotification, category));
        } else {
            ArrayList<EventOfCategory> listOfEventsOfOneCategory = GeneralHelpers.createListWithEventsOfCategory(listOfEvents, category);
            expandableListView.setAdapter(new CustomExpandableListAdapter(this, listOfEventsOfOneCategory, category));
        }
    }

    public void onAddEventClicked(View v) {
        Intent intent = new Intent(CurrentEventsActivity.this, AddEventActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(GlobalConstants.CATEGORY_WORD, this.category);

        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }

    public void onOldEventsClicked(View v) {
        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(this, this.category, true, new ArrayList<EventOfCategory>(0), false);

        startActivity(intent);
        finish();
    }

    public void onDeleteEventClicked(View v) {
        final String[] items = GeneralHelpers.createStringArrayWithEventTypesAndDates(listOfEvents);

        AlertDialog dialog;

        final ArrayList<MyEvent> selectedEvents = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        MyEvent clickedEvent = listOfEvents.get(indexSelected);
                        if (isChecked) {
                            selectedEvents.add(clickedEvent);
                        } else if (selectedEvents.contains(clickedEvent)) {
                            selectedEvents.remove(clickedEvent);
                        }
                    }
                })
                .setPositiveButton(GlobalConstants.DIALOG_DELETE_WORD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (int i = 0; i < selectedEvents.size(); i++) {
                            MyEvent currentEvent = selectedEvents.get(i);

                            CurrentEventsActivity.this.database.removeEvent(tableName, currentEvent.getId());
                        }

                        if (selectedEvents.size() > 0) {
                            Intent intent = GeneralHelpers
                                    .createIntentForCurrentEventsActivity(CurrentEventsActivity.this,
                                            CurrentEventsActivity.this.category, false, new ArrayList<EventOfCategory>(0), false);

                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .setNegativeButton(GlobalConstants.DIALOG_CANCEL_WORD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (this.loadOldEvents == true) {
            Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(CurrentEventsActivity.this, this.category, false, new ArrayList<EventOfCategory>(0), false);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

}

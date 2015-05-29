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
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.Visual.CustomExpandableListAdapter;

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

        if (loadOldEvents == true) {
            listOfEvents = GeneralHelpers.selectOldEvents(database.createListWithEventsFromTable(tableName));
            tvCategoryName.append(GlobalConstants.OLD_EVENTS_TEXT_TO_APPEND);
            ImageButton imageButtonAdd = (ImageButton) findViewById(R.id.imageButtonAddEvent);
            imageButtonAdd.setVisibility(View.GONE);
            ImageButton imageButtonOldEvents = (ImageButton) findViewById(R.id.imageButtonOldEvents);
            imageButtonOldEvents.setVisibility(View.GONE);
            tvCategoryName.setPadding(0, 0, GlobalConstants.CATEGORY_TEXT_VIEW_PADDING_RIGHT, 0);
        } else {
            listOfEvents = GeneralHelpers.selectCurrentEvents(database.createListWithEventsFromTable(tableName));
        }

        expandableListView = (ExpandableListView) findViewById(android.R.id.list);
        expandableListView.setAdapter(new CustomExpandableListAdapter(this, listOfEvents));

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(CurrentEventsActivity.this, EditEventActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GlobalConstants.EVENT_WORD, CurrentEventsActivity.this.listOfEvents);

                intent.putExtras(bundle);

                startActivity(intent);
                finish();
                return true;
            }
        });

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
        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(this, this.category, true);

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
                                            CurrentEventsActivity.this.category, false);

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
            Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(CurrentEventsActivity.this, this.category, false);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}

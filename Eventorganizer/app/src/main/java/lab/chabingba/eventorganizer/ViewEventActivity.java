package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.ValidatorHelpers;

/**
 * Created by Tsvetan on 2015-05-29.
 */
public class ViewEventActivity extends Activity {
    private DBHandler database;
    private MyEvent currentEvent;
    private Category category;
    ArrayList<EventOfCategory> listOfEvents;
    boolean backToTodayEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_activity_layout);

        listOfEvents = (ArrayList<EventOfCategory>) getIntent().getSerializableExtra(GlobalConstants.EVENTS_FOR_NOTIFICATION_TEXT);
        backToTodayEvents = getIntent().getBooleanExtra(GlobalConstants.BASE_RETURN, false);

        int currentDatabaseVersion = GeneralHelpers.getCurrentDatabaseVersion(this);
        this.database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

        EventOfCategory eventOfCategory = (EventOfCategory) getIntent().getSerializableExtra(GlobalConstants.EVENT_OF_CATEGORY_WORD);
        this.currentEvent = eventOfCategory.getEvent();
        this.category = eventOfCategory.getCategory();

        TextView tvCategory = (TextView) findViewById(R.id.tvCategoryInView);
        tvCategory.setText(category.getName());

        TextView tvType = (TextView) findViewById(R.id.tvEventTypeInView);
        tvType.setText(currentEvent.getType());

        TextView tvDate = (TextView) findViewById(R.id.tvEventDateInView);
        tvDate.setText(currentEvent.getEventDateAsString());

        TextView tvHour = (TextView) findViewById(R.id.tvEventHourInView);
        tvHour.setText(currentEvent.getEventHourAsString() + " h");

        TextView tvDescriptionText = (TextView) findViewById(R.id.tvDescriptionTextInView);

        TextView tvDescription = (TextView) findViewById(R.id.tvDescriptionInView);

        if (ValidatorHelpers.isNullOrEmpty(currentEvent.getDescription())) {
            TableRow trDescriptionText = (TableRow) findViewById(R.id.trDescriptionText);
            TableRow trDescription = (TableRow) findViewById(R.id.trDescription);

            trDescriptionText.setVisibility(View.GONE);
            trDescription.setVisibility(View.GONE);

            tvDescriptionText.setVisibility(View.GONE);
            tvDescription.setVisibility(View.GONE);
        } else {
            tvDescription.setText(currentEvent.getDescription());
        }

        TextView tvLocationText = (TextView) findViewById(R.id.tvLocationTextInView);

        TextView tvLocation = (TextView) findViewById(R.id.tvLocationInView);

        if (ValidatorHelpers.isNullOrEmpty(currentEvent.getLocation())) {
            TableRow trLocationText = (TableRow) findViewById(R.id.trLocationText);
            TableRow trLocation = (TableRow) findViewById(R.id.trLocation);

            trLocationText.setVisibility(View.GONE);
            trLocation.setVisibility(View.GONE);

            tvLocationText.setVisibility(View.GONE);
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setText(currentEvent.getLocation());
        }

        CheckBox cbIsFinished = (CheckBox) findViewById(R.id.cbIsFinishedInView);
        cbIsFinished.setChecked(currentEvent.getIsFinished());
        cbIsFinished.setEnabled(false);
    }

    public void onDeleteInViewClicked(View view) {
        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);

        deleteDialog.setMessage("Are you sure you want to delete this event?");

        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                database.removeEvent(category.getSQLName(), currentEvent.getId());

                Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(ViewEventActivity.this, category, false, new ArrayList<EventOfCategory>(0), false);

                startActivity(intent);

                finish();
            }
        });

        deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = deleteDialog.create();
        alertDialog.show();
    }

    public void onEditInViewClicked(View view) {
        Intent intent = new Intent(this, EditEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GlobalConstants.EVENT_WORD, currentEvent);
        bundle.putSerializable(GlobalConstants.CATEGORY_WORD, this.category);

        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }

    public void onMoveInViewClicked(View view) {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final ArrayList<Category> listOfCategories = database.createListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);
        final String[] allCategories = GeneralHelpers.createStringArrayWithCategoryNames(listOfCategories);

        builder.setItems(allCategories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.moveEvent(currentEvent, category, listOfCategories.get(which));
                Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(ViewEventActivity.this, category, false, new ArrayList<EventOfCategory>(0), false);

                startActivity(intent);
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(this, category, currentEvent.getIsOld(), listOfEvents, backToTodayEvents);

        startActivity(intent);
        finish();
    }
}

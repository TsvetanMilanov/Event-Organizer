package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.QueryHelpers;
import lab.chabingba.eventorganizer.Helpers.Visual.CustomExpandableListAdapter;

/**
 * Created by Tsvetan on 2015-05-26.
 */
public class CurrentEventsActivity extends Activity {
    private ExpandableListView expandableListView;
    private ArrayList<MyEvent> listOfEvents;
    private DBHandler database;
    private String tableName = "";
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_events_activity_layout);

        int currentDatabaseVersion = GeneralHelpers.getCurrentDatabaseVersion(this);

        database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

        category = (Category) getIntent().getSerializableExtra("Category");

        tableName = QueryHelpers.convertTableNameToSQLConvention(category.getName());

        TextView tvCategoryName = (TextView) findViewById(R.id.tvCurrentCategory);

        tvCategoryName.setText(category.getName());

        listOfEvents = database.createListWithEventsFromTable(tableName);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setAdapter(new CustomExpandableListAdapter(this, listOfEvents));

    }

    public void onAddEventClicked(View v) {
        for (int i = 0; i < 3; i++) {
            MyEvent testEvent = new MyEvent("Lecture", "2015-0" + i + "-28 18:00:00", "Telerik Academy", "Description of the test lection which is realy long to test the wrapping of the view.", false, false, false);
            this.database.addEvent(testEvent, this.tableName);
        }

        Intent intent = new Intent(CurrentEventsActivity.this, CurrentEventsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Category", this.category);

        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}

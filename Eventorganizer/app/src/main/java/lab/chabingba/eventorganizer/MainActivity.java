package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.Visual.CustomAdapterForCategories;


public class MainActivity extends Activity {
    private ArrayList<Category> listOfCategories;
    private DBHandler database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GeneralHelpers.firstAppRun(this);

        int currentDatabaseVersion = GeneralHelpers.getCurrentDatabaseVersion(this);

        database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

        listOfCategories = database.createListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);

        ListView listView = (ListView) findViewById(R.id.lvMainMenu);

        ListAdapter listAdapter = new CustomAdapterForCategories(this, listOfCategories);

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentForCurrentEventsActivity = new Intent(MainActivity.this, CurrentEventsActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Category", listOfCategories.get(position));

                intentForCurrentEventsActivity.putExtras(bundle);

                startActivity(intentForCurrentEventsActivity);
            }
        });

        ImageButton optionsButton = (ImageButton) findViewById(R.id.imageButtonOptionsMain);
    }

    private void onOptionsButtonClicked() {
        Intent intent = new Intent(this, OptionsActivity.class);
    }
}

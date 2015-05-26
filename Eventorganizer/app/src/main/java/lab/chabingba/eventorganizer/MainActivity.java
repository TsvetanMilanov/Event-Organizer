package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;


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


    }
}

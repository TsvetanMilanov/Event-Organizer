package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

        GeneralHelpers.FirstAppRun(this);

        int currentDatabaseVersion = GeneralHelpers.GetCurrentDatabaseVersion(this);

        database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

        listOfCategories = database.CreateListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);

        TextView tvTest = (TextView) findViewById(R.id.tvTest);

        tvTest.setText("");

        for (int i = 0; i < listOfCategories.size(); i++) {
            tvTest.append(listOfCategories.get(i).getName());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

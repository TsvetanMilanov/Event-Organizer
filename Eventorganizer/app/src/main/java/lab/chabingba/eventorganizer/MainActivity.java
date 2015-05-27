package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.ValidatorHelpers;
import lab.chabingba.eventorganizer.Helpers.Visual.CustomAdapterForCategories;
import lab.chabingba.eventorganizer.Helpers.Visual.CustomAdapterForOptionsMenu;
import lab.chabingba.eventorganizer.Helpers.Visual.FlyInMenuContainer;


public class MainActivity extends Activity {
    private ArrayList<Category> listOfCategories;
    private DBHandler database;
    private FlyInMenuContainer root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.root = (FlyInMenuContainer) this.getLayoutInflater().inflate(R.layout.activity_main_with_flyin_menu, null);
        setContentView(this.root);

        GeneralHelpers.firstAppRun(this);

        int currentDatabaseVersion = GeneralHelpers.getCurrentDatabaseVersion(this);

        database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

        //region Create listViewForMenu
        ListView listViewForMenu = (ListView) this.findViewById(R.id.lvOptions);

        ListAdapter arrayAdapter = new CustomAdapterForOptionsMenu(this, GlobalConstants.MENU_OPTIONS);

        listViewForMenu.setAdapter(arrayAdapter);

        listViewForMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;

                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, OptionsActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "Force notifications", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        finish();
                        break;
                }
            }
        });
        //endregion

        listOfCategories = database.createListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);

        //region Crete listView
        ListView listView = (ListView) findViewById(R.id.lvMainMenu);

        ListAdapter listAdapter = new CustomAdapterForCategories(this, listOfCategories);

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentForCurrentEventsActivity = GeneralHelpers.
                        createIntentForCurrentEventsActivity(MainActivity.this, MainActivity.this.listOfCategories.get(position), false);

                startActivity(intentForCurrentEventsActivity);
            }
        });
        //endregion
    }

    public void onOptionsButtonClicked(View v) {
        this.root.toggleMenu();
    }

    @Override
    public void onBackPressed() {
        if (this.root.getMenuState() == GlobalConstants.MenuState.OPEN) {
            this.root.toggleMenu();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            this.root.toggleMenu();
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    public void onAddCategoryClicked(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.category_dialog, null);
        promptsView.setBackgroundColor(Color.rgb(165, 165, 165));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.etDialogCategoryName);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                String categoryName = userInput.getText().toString();

                                if (ValidatorHelpers.isNullOrEmpty(categoryName)) {
                                    Toast.makeText(MainActivity.this, "The category name can't be empty.", Toast.LENGTH_LONG).show();
                                } else {
                                    MainActivity.this.database.addCategory(categoryName.trim());
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void onDeleteCategoryClicked(View v) {

        final String[] items = GeneralHelpers.createStringArrayWithCategoryNames(listOfCategories);

        AlertDialog dialog;

        final ArrayList seletedItems = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            // write your code when user Uchecked the checkbox
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (int i = 0; i < seletedItems.size(); i++) {
                            MainActivity.this.database.removeCategory(items[(int) seletedItems.get(i)]);
                        }

                        if (seletedItems.size() > 0) {
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog = builder.create();
        dialog.show();
    }
}

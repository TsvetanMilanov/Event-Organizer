package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.ValidatorHelpers;
import lab.chabingba.eventorganizer.Visual.CustomAdapterRows.CustomAdapterForCategories;
import lab.chabingba.eventorganizer.Visual.CustomAdapterRows.CustomAdapterForOptionsMenu;
import lab.chabingba.eventorganizer.Visual.FlyInMenuContainer;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean autoCleanOldEvents = preferences.getBoolean("cbpAutoRemoveOldEvents", true);

        if (autoCleanOldEvents) {
            GeneralHelpers.removeOldEvents(database);
        }

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
                        Intent broadcastForForceNotifications = new Intent();
                        broadcastForForceNotifications.setAction(GlobalConstants.BOOT_RECEIVER_ACTION_NAME);

                        MainActivity.this.sendBroadcast(broadcastForForceNotifications);
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
                        createIntentForCurrentEventsActivity(MainActivity.this, MainActivity.this.listOfCategories.get(position), false, new ArrayList<EventOfCategory>(0), false);

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
        //promptsView.setBackgroundColor(Color.rgb(165, 165, 165));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.etDialogCategoryName);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(GlobalConstants.DIALOG_SAVE_WORD,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                .setNegativeButton(GlobalConstants.DIALOG_CANCEL_WORD,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void onDeleteCategoryClicked(View v) {

        final String[] items = GeneralHelpers.createStringArrayWithCategoryNames(listOfCategories);

        AlertDialog dialog;

        final ArrayList selectedItems = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton(GlobalConstants.DIALOG_DELETE_WORD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (int i = 0; i < selectedItems.size(); i++) {
                            MainActivity.this.database.removeCategory(items[(int) selectedItems.get(i)]);
                        }

                        if (selectedItems.size() > 0) {
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
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
}

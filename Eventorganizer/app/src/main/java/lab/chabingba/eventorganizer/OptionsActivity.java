package lab.chabingba.eventorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Visual.CustomAdapterRows.CustomAdapterForOptions;

/**
 * Created by Tsvetan on 2015-05-26.
 */
public class OptionsActivity extends Activity {
    DBHandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity_layout);
        
        database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null);

        ListView listView = (ListView) findViewById(R.id.lvOptionsInOptions);

        String[] listOfOptions = {"Preferences", "Add new event type", "Remove event type", "Remove old events", "Clear events in category"};

        listView.setAdapter(new CustomAdapterForOptions(this, listOfOptions));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //Preferences
                        Intent intent = new Intent(OptionsActivity.this, PreferencesActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        GeneralHelpers.createAddNewEventTypeDialog(OptionsActivity.this);
                        break;
                    case 2:
                        //Remove event type
                        ArrayList<String> listOfTypes = database.getEventTypesAsArray();

                        final String[] items = GeneralHelpers.createStringArrayWithEventTypes(listOfTypes);

                        AlertDialog dialog;

                        final ArrayList selectedItems = new ArrayList();

                        AlertDialog.Builder builder = new AlertDialog.Builder(OptionsActivity.this);
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
                                            OptionsActivity.this.database.removeEventType(items[(int) selectedItems.get(i)]);
                                        }

                                        if (selectedItems.size() > 0) {
                                            Intent intent = new Intent(OptionsActivity.this, OptionsActivity.class);
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
                        break;
                    case 3:
                        //Remove old events
                        GeneralHelpers.removeOldEvents(database);
                        Toast.makeText(OptionsActivity.this, "Old events cleared", Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        //Clear events in category
                        AlertDialog alertDialog;
                        AlertDialog.Builder categorySelectBuilder = new AlertDialog.Builder(OptionsActivity.this);

                        final ArrayList<Category> listOfCategories = database.createListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);
                        final String[] allCategories = GeneralHelpers.createStringArrayWithCategoryNames(listOfCategories);

                        categorySelectBuilder.setItems(allCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog confirmClearEventsDialog;
                                AlertDialog.Builder confirmClearEventsDialogBuilder = new AlertDialog.Builder(OptionsActivity.this);
                                final String categoryName = allCategories[which];

                                confirmClearEventsDialogBuilder.setMessage("Are you sure you want to delete all events in " + categoryName + "? \n(This will delete the old events too)")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                database.deleteAllRowsInTable(categoryName);
                                                Toast.makeText(OptionsActivity.this, "Removed all events in: " + categoryName, Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                confirmClearEventsDialog = confirmClearEventsDialogBuilder.create();
                                confirmClearEventsDialog.show();
                            }
                        });

                        alertDialog = categorySelectBuilder.create();
                        alertDialog.show();
                        break;
                }
            }
        });
    }
}

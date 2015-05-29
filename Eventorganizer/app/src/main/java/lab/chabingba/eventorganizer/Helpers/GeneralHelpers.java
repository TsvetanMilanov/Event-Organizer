package lab.chabingba.eventorganizer.Helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;

import lab.chabingba.eventorganizer.CurrentEventsActivity;
import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class GeneralHelpers {
    public static void firstAppRun(Context context) {
        SharedPreferences settings = context.getSharedPreferences(GlobalConstants.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settings.edit();

        boolean isFirstRun = settings.getBoolean(GlobalConstants.FIRST_APP_RUN_VARIABLE, true);

        if (isFirstRun) {
            settingsEditor.putInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);
            settingsEditor.putBoolean(GlobalConstants.FIRST_APP_RUN_VARIABLE, false);
            settingsEditor.commit();
        }
    }

    public static int getCurrentDatabaseVersion(Context context) {
        SharedPreferences settings = context.getSharedPreferences(GlobalConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int currentDBVersion = settings.getInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);

        return currentDBVersion;
    }

    public static String[] createStringArrayWithCategoryNames(ArrayList<Category> listOfCategories) {
        String[] result = new String[listOfCategories.size()];

        for (int i = 0; i < listOfCategories.size(); i++) {
            result[i] = listOfCategories.get(i).getName();
        }

        return result;
    }

    public static String[] createStringArrayWithEventTypesAndDates(ArrayList<MyEvent> listOfEvents) {
        String[] result = new String[listOfEvents.size()];

        for (int i = 0; i < listOfEvents.size(); i++) {
            result[i] = listOfEvents.get(i).getType() + " " + listOfEvents.get(i).getEventDateAsString();
        }

        return result;
    }

    public static ArrayList<MyEvent> selectCurrentEvents(ArrayList<MyEvent> listWithEventsFromTable) {
        ArrayList<MyEvent> result = new ArrayList<>();

        for (int i = 0; i < listWithEventsFromTable.size(); i++) {
            MyEvent currentEvent = listWithEventsFromTable.get(i);

            if (currentEvent.getIsOld() == true) {
                continue;
            } else {
                result.add(currentEvent);
            }
        }

        Collections.sort(result);

        return result;
    }

    public static ArrayList<MyEvent> selectOldEvents(ArrayList<MyEvent> listWithEventsFromTable) {
        ArrayList<MyEvent> result = new ArrayList<>();

        for (int i = 0; i < listWithEventsFromTable.size(); i++) {
            MyEvent currentEvent = listWithEventsFromTable.get(i);

            if (currentEvent.getIsOld() == false) {
                continue;
            } else {
                result.add(currentEvent);
            }
        }

        Collections.sort(result);
        Collections.reverse(result);

        return result;
    }

    public static Intent createIntentForCurrentEventsActivity(Context context, Category category, boolean loadOldEvents) {
        Intent result = new Intent(context, CurrentEventsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(GlobalConstants.CATEGORY_WORD, category);
        bundle.putBoolean(GlobalConstants.LOAD_OLD_EVENTS_TEXT, loadOldEvents);

        result.putExtras(bundle);

        return result;
    }
}

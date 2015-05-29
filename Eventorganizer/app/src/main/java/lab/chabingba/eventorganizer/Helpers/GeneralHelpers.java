package lab.chabingba.eventorganizer.Helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import lab.chabingba.eventorganizer.CurrentEventsActivity;
import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Notifications.BootReceiver;
import lab.chabingba.eventorganizer.Notifications.NotificationService;
import lab.chabingba.eventorganizer.R;
import lab.chabingba.eventorganizer.ViewEventActivity;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class GeneralHelpers {
    private static final String TAG = "Helpers";

    public static void firstAppRun(Context context) {
        SharedPreferences settings = context.getSharedPreferences(GlobalConstants.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settings.edit();

        boolean isFirstRun = settings.getBoolean(GlobalConstants.FIRST_APP_RUN_VARIABLE, true);

        if (isFirstRun) {
            settingsEditor.putInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);
            settingsEditor.putBoolean(GlobalConstants.FIRST_APP_RUN_VARIABLE, false);

            Intent intent = new Intent(context, BootReceiver.class);

            context.startService(intent);

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

    public static void startServiceForNotifications(Context context) {
        Intent intent = new Intent(context, NotificationService.class);

        context.startService(intent);
    }

    public static void createNotification(Context context, Category category, MyEvent event) {
        Intent intentForEdit = new Intent(context, ViewEventActivity.class);

        Bundle bundle = new Bundle();

        bundle.putSerializable(GlobalConstants.CATEGORY_WORD, category);
        bundle.putSerializable(GlobalConstants.EVENT_WORD, event);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentForEdit, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.icon, category.getName(), event.getDate().getTimeInMillis());

        notification.setLatestEventInfo(context, category.getName() + " " + GlobalConstants.EVENT_WORD, event.getDescription(), pendingIntent);

        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    /*
                    //Uncomment for auto cancel the notification.
                    notification.flags = notification.flags | notification.FLAG_AUTO_CANCEL;
                    */

        notificationManager.notify(1, notification);

        Log.i(TAG, "Notification created");
    }

    public static EventOfCategory checkForEventForToday(DBHandler database, ArrayList<Category> listOfCategories) {
        Calendar currentDate = Calendar.getInstance();

        for (int i = 0; i < listOfCategories.size(); i++) {
            Category currentCategory = listOfCategories.get(i);
            String currentCategoryName = currentCategory.getSQLName();

            ArrayList<MyEvent> listOfEvents = database.createListWithEventsFromTable(currentCategoryName);

            for (int j = 0; j < listOfEvents.size(); j++) {
                MyEvent currentEvent = listOfEvents.get(j);

                if (currentEvent.getIsOld()) {
                    continue;
                }


                Calendar eventsDate = currentEvent.getDate();

                if (eventsDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
                        && eventsDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
                        && eventsDate.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)) {
                    Log.i(TAG, "Found event.");
                    return new EventOfCategory(currentCategory, currentEvent);
                }
            }
        }
        Log.i(TAG, "No event.");
        return null;
    }
}

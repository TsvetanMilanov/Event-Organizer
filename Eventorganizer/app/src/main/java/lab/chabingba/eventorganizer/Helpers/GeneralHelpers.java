package lab.chabingba.eventorganizer.Helpers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import lab.chabingba.eventorganizer.CurrentEventsActivity;
import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.EditEventActivity;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Notifications.MultiNotificationsService;
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
            Log.i(TAG, "App ran for the first time.");

            settingsEditor.putInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);
            settingsEditor.putBoolean(GlobalConstants.FIRST_APP_RUN_VARIABLE, false);

            Intent intent = new Intent();
            intent.setAction(GlobalConstants.BOOT_RECEIVER_ACTION_NAME);

            context.sendBroadcast(intent);

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

    public static String[] createStringArrayWithEventTypes(ArrayList<String> listOfTypes) {
        String[] result = new String[listOfTypes.size()];

        for (int i = 0; i < listOfTypes.size(); i++) {
            result[i] = listOfTypes.get(i);
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

    public static Intent createIntentForCurrentEventsActivity(Context context, Category category, boolean loadOldEvents, ArrayList<EventOfCategory> listOfEventsForNotification, boolean loadTodayEvents) {
        Intent result = new Intent(context, CurrentEventsActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(GlobalConstants.CATEGORY_WORD, category);
        bundle.putBoolean(GlobalConstants.LOAD_OLD_EVENTS_TEXT, loadOldEvents);
        bundle.putBoolean(GlobalConstants.LOAD_TODAYS_EVENTS_TEXT, loadTodayEvents);
        bundle.putSerializable(GlobalConstants.EVENTS_FOR_NOTIFICATION_TEXT, listOfEventsForNotification);

        result.putExtras(bundle);

        return result;
    }

    public static void createNotification(Context context, ArrayList<EventOfCategory> listOfEventsForNotification) {
        if (ValidatorHelpers.isNullOrEmpty(listOfEventsForNotification)) {
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean autoCancel = preferences.getBoolean("cbpAutoCancelNotifications", true);

        DBHandler database = new DBHandler(context, DatabaseConstants.DATABASE_NAME, null);

        ArrayList<EventOfCategory> listOfEventsForToday = checkForEventForToday(database);

        Intent intentForEdit = GeneralHelpers.createIntentForCurrentEventsActivity(context, listOfEventsForToday.get(0).getCategory(), false, listOfEventsForToday, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentForEdit, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.icon, "There are events for today", Calendar.getInstance().getTimeInMillis());

        notification.setLatestEventInfo(context, "There are events for today", "Click to see the events.", pendingIntent);

        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (autoCancel) {
            notification.flags = notification.flags | notification.FLAG_AUTO_CANCEL;
        }

        notificationManager.notify(0, notification);

        Log.i(TAG, "Notification created");
    }

    public static ArrayList<EventOfCategory> checkForEventForToday(DBHandler database) {
        ArrayList<Category> listOfCategories = database.createListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);

        ArrayList<EventOfCategory> result = new ArrayList<>();
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

                    result.add(new EventOfCategory(currentCategory, currentEvent));
                }
            }
        }
        Log.i(TAG, "No event.");
        return result;
    }

    public static ArrayList<EventOfCategory> removeEventsWithNotifications(ArrayList<EventOfCategory> listOfEventsForToday) {
        ArrayList<EventOfCategory> result = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            if (listOfEventsForToday.get(i).getEvent().getHasNotification() || listOfEventsForToday.get(i).getEvent().getIsOld() || listOfEventsForToday.get(i).getEvent().getIsFinished()) {
                continue;
            } else {
                result.add(listOfEventsForToday.get(i));
            }
        }

        return result;
    }

    public static void createAlarmManager(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        long notificationInterval = Long.valueOf(preferences.getString("notificationInterval", "86400000"));

        String hourAsString = preferences.getString("prefTimePicker", "10:00");

        int hour = Integer.parseInt(hourAsString.split(":")[0]);
        int minutes = Integer.parseInt(hourAsString.split(":")[1]);

        Intent myIntent = new Intent(context, NotificationService.class);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar timeForNotification = Calendar.getInstance();

        timeForNotification.set(Calendar.SECOND, 0);
        timeForNotification.set(Calendar.MINUTE, minutes);
        timeForNotification.set(Calendar.HOUR, hour);

        // TODO: check with hour > 12 to test the PM
        if (hour > 12) {
            timeForNotification.set(Calendar.AM_PM, Calendar.PM);
        } else {
            timeForNotification.set(Calendar.AM_PM, Calendar.AM);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeForNotification.getTimeInMillis(), notificationInterval, pendingIntent);
    }

    public static void createAddNewEventTypeDialog(final Context context, final EventOfCategory eventOfCategory) {
        final DBHandler database = new DBHandler(context, DatabaseConstants.DATABASE_NAME, null);

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.add_event_type_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.etDialogEventType);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(GlobalConstants.DIALOG_SAVE_WORD,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String eventTypeAsString = userInput.getText().toString();

                                if (ValidatorHelpers.isNullOrEmpty(eventTypeAsString)) {
                                    Toast.makeText(context, "The event type can't be empty.", Toast.LENGTH_LONG).show();
                                } else {
                                    database.addEventType(eventTypeAsString.trim());
                                    Toast.makeText(context, "Added event type: " + eventTypeAsString, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(context, context.getClass());
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(GlobalConstants.EVENT_WORD, eventOfCategory.getEvent());
                                    bundle.putSerializable(GlobalConstants.CATEGORY_WORD, eventOfCategory.getCategory());

                                    intent.putExtras(bundle);

                                    context.startActivity(intent);

                                    ((Activity) context).finish();
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

    public static void createAddNewEventTypeDialog(final Context context) {
        final DBHandler database = new DBHandler(context, DatabaseConstants.DATABASE_NAME, null);

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.add_event_type_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.etDialogEventType);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(GlobalConstants.DIALOG_SAVE_WORD,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String eventTypeAsString = userInput.getText().toString();

                                if (ValidatorHelpers.isNullOrEmpty(eventTypeAsString)) {
                                    Toast.makeText(context, "The event type can't be empty.", Toast.LENGTH_LONG).show();
                                } else {
                                    database.addEventType(eventTypeAsString.trim());

                                    Toast.makeText(context, "Added event type: " + eventTypeAsString, Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(context, context.getClass());

                                    context.startActivity(intent);

                                    ((Activity) context).finish();
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

    public static void removeOldEvents(DBHandler database) {
        ArrayList<Category> listOfCategories = database.createListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);
        Calendar currentDate = Calendar.getInstance();

        for (int i = 0; i < listOfCategories.size(); i++) {
            Category category = listOfCategories.get(i);

            ArrayList<MyEvent> listOfEvents = database.createListWithEventsFromTable(category.getSQLName());

            for (int j = 0; j < listOfEvents.size(); j++) {
                MyEvent currentEvent = listOfEvents.get(j);

                Calendar eventsDate = currentEvent.getDate();

                if (eventsDate.get(Calendar.YEAR) < currentDate.get(Calendar.YEAR) && !currentEvent.getIsOld()) {
                    currentEvent.setIsOld(true);
                    currentEvent.setHasNotification(true);
                    currentEvent.setIsFinished(true);
                    database.updateEvent(currentEvent, category.getSQLName());
                } else if (eventsDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
                    if (eventsDate.get(Calendar.MONTH) < currentDate.get(Calendar.MONTH) && !currentEvent.getIsOld()) {
                        currentEvent.setIsOld(true);
                        currentEvent.setHasNotification(true);
                        currentEvent.setIsFinished(true);
                        database.updateEvent(currentEvent, category.getSQLName());
                    } else if (eventsDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
                        if (eventsDate.get(Calendar.DAY_OF_MONTH) < currentDate.get(Calendar.DAY_OF_MONTH) && !currentEvent.getIsOld()) {
                            currentEvent.setIsOld(true);
                            currentEvent.setHasNotification(true);
                            currentEvent.setIsFinished(true);
                            database.updateEvent(currentEvent, category.getSQLName());
                        }
                    }
                }
            }
        }
    }

    public static ArrayList copyOfArray(ArrayList sourceArray) {
        ArrayList resultArray = new ArrayList();

        for (int i = 0; i < sourceArray.size(); i++) {
            resultArray.add(sourceArray.get(i));
        }

        return resultArray;
    }

    public static ArrayList<MyEvent> createListOfEventsFromEventOfCategoryArray(ArrayList<EventOfCategory> eventOfCategoryArrayList) {
        ArrayList<MyEvent> result = new ArrayList<>();

        for (int i = 0; i < eventOfCategoryArrayList.size(); i++) {
            result.add(eventOfCategoryArrayList.get(i).getEvent());
        }

        return result;
    }

    public static ArrayList<EventOfCategory> createListWithEventsOfCategory(ArrayList<MyEvent> myEventArrayList, Category category) {
        ArrayList<EventOfCategory> result = new ArrayList<>();

        for (int i = 0; i < myEventArrayList.size(); i++) {
            result.add(new EventOfCategory(category, myEventArrayList.get(i)));
        }

        return result;
    }

    public static boolean checkForDifferentCategories(ArrayList<EventOfCategory> eventOfCategoryArrayList) {
        String lastCategory = "";

        for (int i = 0; i < eventOfCategoryArrayList.size(); i++) {
            if (i == 0) {
                lastCategory = eventOfCategoryArrayList.get(i).getCategory().getName();
                continue;
            }

            lastCategory = eventOfCategoryArrayList.get(i - 1).getCategory().getName();
            String currentCategory = eventOfCategoryArrayList.get(i).getCategory().getName();

            if (!lastCategory.equals(currentCategory)) {
                return true;
            }
        }
        return false;
    }

    public static void moveEvent(final Context context, final DBHandler database, final MyEvent currentEvent, final Category category) {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final ArrayList<Category> listOfCategories = database.createListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);
        final String[] allCategories = GeneralHelpers.createStringArrayWithCategoryNames(listOfCategories);

        builder.setItems(allCategories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.moveEvent(currentEvent, category, listOfCategories.get(which));
                Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(context, category, false, new ArrayList<EventOfCategory>(0), false);

                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void viewEvent(Context context, int groupPosition, Category currentEventsCategory, ArrayList<EventOfCategory> listOfEvents) {
        Intent intent = new Intent(context, ViewEventActivity.class);
        Bundle bundle = new Bundle();
        EventOfCategory eventOfCategoryToPass = new EventOfCategory(currentEventsCategory, listOfEvents.get(groupPosition).getEvent());
        bundle.putSerializable(GlobalConstants.EVENTS_FOR_NOTIFICATION_TEXT, listOfEvents);
        bundle.putSerializable(GlobalConstants.EVENT_OF_CATEGORY_WORD, eventOfCategoryToPass);
        bundle.putBoolean(GlobalConstants.BASE_RETURN, GeneralHelpers.checkForDifferentCategories(listOfEvents));

        intent.putExtras(bundle);

        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void deleteEvent(final Context context, final DBHandler database, final MyEvent currentEvent, final Category currentEventsCategory) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);

        deleteDialog.setMessage("Are you sure you want to delete this event?");

        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                database.removeEvent(currentEventsCategory.getSQLName(), currentEvent.getId());

                Intent intent = GeneralHelpers.createIntentForCurrentEventsActivity(context, currentEventsCategory, false, new ArrayList<EventOfCategory>(0), false);

                context.startActivity(intent);

                ((Activity) context).finish();
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

    public static void editEvent(Context context, MyEvent currentEvent, Category currentEventsCategory) {
        Intent intent = new Intent(context, EditEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GlobalConstants.EVENT_WORD, currentEvent);
        bundle.putSerializable(GlobalConstants.CATEGORY_WORD, currentEventsCategory);

        intent.putExtras(bundle);

        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void createMultiAlarms(Context context) {
        DBHandler database = new DBHandler(context, DatabaseConstants.DATABASE_NAME, null);

        ArrayList<EventOfCategory> listOfEventsForToday = GeneralHelpers.checkForEventForToday(database);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < listOfEventsForToday.size(); i++) {
            EventOfCategory currentEvent = listOfEventsForToday.get(i);

            if (currentEvent.getEvent().getHasNotification()) {
                continue;
            }

            int requestCode = i + 1;

            GeneralHelpers.createAlarmForEvent(context, alarmManager, requestCode, database, listOfEventsForToday, currentEvent);
        }
    }

    public static void createAlarmForEvent(Context context, AlarmManager alarmManager, int requestCode, DBHandler database, ArrayList<EventOfCategory> listOfEventsForToday, EventOfCategory currentEvent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        long timeBeforeEvent = Long.valueOf(sharedPreferences.getString("timeBeforeEvent", "10800000"));

        Intent myIntent = new Intent(context, MultiNotificationsService.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable(GlobalConstants.EVENT_OF_CATEGORY_WORD, currentEvent);
        bundle.putInt(GlobalConstants.REQUEST_CODE_WORD, requestCode);

        myIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getService(context, requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar timeForNotification = currentEvent.getEvent().getDate();

        long timeAsLong = timeForNotification.getTimeInMillis() - timeBeforeEvent;

        if (timeAsLong < 0) {
            timeAsLong = 1;
        }

        database.setHasNotificationToEvents(listOfEventsForToday);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAsLong, pendingIntent);
        Log.i(TAG, "Created MultiAlarm.");
    }

    public static void createNotification(Context context, EventOfCategory currentEvent, int requestCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean autoCancel = preferences.getBoolean("cbpAutoCancelNotifications", true);

        DBHandler database = new DBHandler(context, DatabaseConstants.DATABASE_NAME, null);

        ArrayList<EventOfCategory> listOfEventsForToday = checkForEventForToday(database);

        Intent intentForView = new Intent(context, ViewEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GlobalConstants.EVENTS_FOR_NOTIFICATION_TEXT, listOfEventsForToday);
        bundle.putBoolean(GlobalConstants.BASE_RETURN, true);
        bundle.putSerializable(GlobalConstants.EVENT_OF_CATEGORY_WORD, currentEvent);
        intentForView.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intentForView, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.icon, currentEvent.getCategory().getName() + " " + GlobalConstants.EVENT_WORD, Calendar.getInstance().getTimeInMillis());

        notification.setLatestEventInfo(context, currentEvent.getEvent().getType(), currentEvent.getEvent().getDescription(), pendingIntent);

        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (autoCancel) {
            notification.flags = notification.flags | notification.FLAG_AUTO_CANCEL;
        }

        notificationManager.notify(0, notification);

        Log.i(TAG, "Notification created");
    }

    public static void forceNotifications(Context context) {
        DBHandler database = new DBHandler(context, DatabaseConstants.DATABASE_NAME, null);

        ArrayList<EventOfCategory> listOfEventsForToday = GeneralHelpers.checkForEventForToday(database);

        if (!ValidatorHelpers.isNullOrEmpty(listOfEventsForToday)) {
            Intent intentForTodayEvents = GeneralHelpers.createIntentForCurrentEventsActivity(context, listOfEventsForToday.get(0).getCategory(), false, listOfEventsForToday, true);

            context.startActivity(intentForTodayEvents);
        } else {
            Toast.makeText(context, "No events for today.", Toast.LENGTH_LONG).show();
        }
    }

    public static void startNotificationServices(Context context, SharedPreferences sharedPreferences) {
        boolean notificationsState = sharedPreferences.getBoolean("notificationsOnOff", true);

        int notificationsType = Integer.valueOf(sharedPreferences.getString("notificationType", "0"));

        if (notificationsState == true) {
            if (notificationsType == 0) {
                GeneralHelpers.createAlarmManager(context);
            }

            if (notificationsType == 1) {
                GeneralHelpers.createMultiAlarms(context);
            }

            if (notificationsType == 2) {
                GeneralHelpers.createAlarmManager(context);

                GeneralHelpers.createMultiAlarms(context);
            }
        }
    }
}

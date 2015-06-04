package lab.chabingba.eventorganizer.Notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;
import lab.chabingba.eventorganizer.Helpers.ValidatorHelpers;

/**
 * Created by Tsvetan on 2015-05-29.
 */
public class NotificationService extends Service {
    private static final String TAG = "NotificationService";

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "NotificationService started.");

        int currentDatabaseVersion = GeneralHelpers.getCurrentDatabaseVersion(this);

        DBHandler database = new DBHandler(this, DatabaseConstants.DATABASE_NAME, null, currentDatabaseVersion);

        ArrayList<EventOfCategory> listOfEventsForToday = GeneralHelpers.checkForEventForToday(database);

        ArrayList<EventOfCategory> listOfEventsForNotification = GeneralHelpers.removeEventsWithNotifications(listOfEventsForToday);

        if (!ValidatorHelpers.isNullOrEmpty(listOfEventsForNotification)) {
            Log.i(TAG, "Found event/s for today.");

            GeneralHelpers.createNotification(getBaseContext(), listOfEventsForNotification);

            database.setHasNotificationToEvents(listOfEventsForToday);

            Log.i(TAG, "Created notification");
        } else {
            Log.i(TAG, "No event for today.");
        }


        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

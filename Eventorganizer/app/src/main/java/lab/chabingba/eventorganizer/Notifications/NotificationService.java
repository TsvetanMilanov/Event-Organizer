package lab.chabingba.eventorganizer.Notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Database.Category;
import lab.chabingba.eventorganizer.Database.DBHandler;
import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Database.MyEvent;
import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;

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

        ArrayList<Category> listOfCategories = database.createListWithCategoriesFromTable(DatabaseConstants.CATEGORIES_TABLE_NAME);

        EventOfCategory eventForTodayWithCategory = GeneralHelpers.checkForEventForToday(database, listOfCategories);

        if (eventForTodayWithCategory != null) {
            Log.i(TAG, "Found event for today.");
            if (!eventForTodayWithCategory.getEvent().getIsOld() && !eventForTodayWithCategory.getEvent().getHasNotification()) {
                GeneralHelpers.createNotification(getBaseContext(), eventForTodayWithCategory.getCategory(), eventForTodayWithCategory.getEvent());
                MyEvent updatedEvent = eventForTodayWithCategory.getEvent();
                updatedEvent.setHasNotification(true);
                database.updateEvent(updatedEvent, eventForTodayWithCategory.getCategory().getSQLName());
            }
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

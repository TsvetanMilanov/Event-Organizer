package lab.chabingba.eventorganizer.Notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import lab.chabingba.eventorganizer.Database.EventOfCategory;
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;

/**
 * Created by Tsvetan on 2015-06-04.
 */
public class MultiNotificationsService extends Service {
    private static final String TAG = "MultiNotificationService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Multi notification service started.");

        EventOfCategory currentEvent = (EventOfCategory) intent.getSerializableExtra(GlobalConstants.EVENT_OF_CATEGORY_WORD);

        int requestCode = intent.getIntExtra(GlobalConstants.REQUEST_CODE_WORD, 0);

        GeneralHelpers.createNotification(getBaseContext(), currentEvent, requestCode);

        return 0;
    }
}

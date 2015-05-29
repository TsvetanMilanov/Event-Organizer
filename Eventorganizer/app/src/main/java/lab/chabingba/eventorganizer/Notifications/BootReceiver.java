package lab.chabingba.eventorganizer.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Tsvetan on 2015-05-29.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Receiver received on boot intent.");

        Intent myIntent = new Intent(context, NotificationService.class);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar timeForNotification = Calendar.getInstance();

        timeForNotification.set(Calendar.SECOND, 0);
        timeForNotification.set(Calendar.MINUTE, 0);
        timeForNotification.set(Calendar.HOUR, 10);
        timeForNotification.set(Calendar.AM_PM, Calendar.AM);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeForNotification.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
    }
}

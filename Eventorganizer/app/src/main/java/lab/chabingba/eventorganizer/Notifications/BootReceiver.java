package lab.chabingba.eventorganizer.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;

/**
 * Created by Tsvetan on 2015-05-29.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiver";
    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Receiver received on boot intent.");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean notificationsState = sharedPreferences.getBoolean("notificationsOnOff", true);

        int notificationsType = sharedPreferences.getInt("notificationType", 0);

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

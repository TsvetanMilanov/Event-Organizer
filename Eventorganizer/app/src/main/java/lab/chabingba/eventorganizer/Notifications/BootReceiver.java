package lab.chabingba.eventorganizer.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import lab.chabingba.eventorganizer.Helpers.GeneralHelpers;

/**
 * Created by Tsvetan on 2015-05-29.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Receiver received on boot intent.");

        GeneralHelpers.createAlarmManager(context);
    }
}

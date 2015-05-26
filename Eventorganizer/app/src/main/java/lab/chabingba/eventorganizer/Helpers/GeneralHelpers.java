package lab.chabingba.eventorganizer.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GeneralConstants;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class GeneralHelpers {
    public static void FirstAppRun(Context context) {
        SharedPreferences settings = context.getSharedPreferences(GeneralConstants.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settings.edit();

        boolean isFirstRun = settings.getBoolean(GeneralConstants.FIRST_APP_RUN_VARIABLE, true);

        if (isFirstRun) {
            settingsEditor.putInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);
            settingsEditor.putBoolean(GeneralConstants.FIRST_APP_RUN_VARIABLE, false);
            settingsEditor.commit();
        }
    }

    public static int GetCurrentDatabaseVersion(Context context) {
        SharedPreferences settings = context.getSharedPreferences(GeneralConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int currentDBVersion = settings.getInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);

        return currentDBVersion;
    }
}

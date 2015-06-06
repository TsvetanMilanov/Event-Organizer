package lab.chabingba.eventorganizer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Tsvetan on 2015-06-01.
 */
@SuppressWarnings("deprecation")
public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

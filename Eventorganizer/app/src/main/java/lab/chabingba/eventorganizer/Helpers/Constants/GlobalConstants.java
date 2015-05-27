package lab.chabingba.eventorganizer.Helpers.Constants;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class GlobalConstants {
    public static final String DATE_FORMATTER = "dd.MM.yyyy";
    public static final String DATE_FORMATTER_DAY_OF_WEEK = "EEEE";
    public static final String DATE_FORMATTER_HOUR = "HH:mm";
    public static final String DATE_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String EMPTY_STRING = "";

    public static final String FIRST_APP_RUN_VARIABLE = "FIRST_RUN";
    public static final String PREFS_NAME = "preferences";

    public enum MenuState {
        OPEN, CLOSED, OPENING, CLOSING
    }

    public static final String[] MENU_OPTIONS = {"Options", "Force notification", "About", "Exit"};
}

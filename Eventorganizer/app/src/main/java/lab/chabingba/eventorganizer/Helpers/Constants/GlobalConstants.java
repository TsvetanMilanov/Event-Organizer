package lab.chabingba.eventorganizer.Helpers.Constants;

import java.util.ArrayList;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class GlobalConstants {
    //region Date formatter strings
    public static final String DATE_FORMATTER = "dd.MM.yyyy";
    public static final String DATE_FORMATTER_DAY_OF_WEEK = "EEEE";
    public static final String DATE_FORMATTER_HOUR = "HH:mm";
    public static final String DATE_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //endregion

    public static final String NAME_OF_INTENT_SERVICE = "EventOrganizerIntentService";

    public static final String EMPTY_STRING = "";

    public static final String FIRST_APP_RUN_VARIABLE = "FIRST_RUN";
    public static final String PREFS_NAME = "preferences";
    public static final int CATEGORY_TEXT_VIEW_PADDING_RIGHT = 60;

    public static final String CATEGORY_WORD = "Category";
    public static final String LOAD_OLD_EVENTS_TEXT = "LoadOldEvents";
    public static final String LOAD_TODAY_EVENTS_TEXT = "LoadTodayEvents";
    public static final String EVENTS_FOR_NOTIFICATION_TEXT = "EventsForNotification";
    public static final String BASE_RETURN = "BaseReturn";
    public static final String REQUEST_CODE_WORD = "RequestCode";
    public static final String OLD_EVENTS_TEXT_TO_APPEND = " - Old Events";
    public static final String EVENT_WORD = "Event";
    public static final String DIALOG_CANCEL_WORD = "Cancel";
    public static final String DIALOG_SAVE_WORD = "Save";
    public static final String DIALOG_DELETE_WORD = "Delete";
    public static final String EVENT_OF_CATEGORY_WORD = "EventOfCategory";
    public static final String EMAIL_FOR_FEEDBACK = "tsvetan.milanov17@gmail.com";
    public static final String EMAIL_SUBJECT = "Event-Organizer app";

    public static final String BOOT_RECEIVER_ACTION_NAME = ".Notifications.BootReceiver";

    public enum MenuState {
        OPEN, CLOSED, OPENING, CLOSING
    }

    public static final String[] MENU_OPTIONS = {"Events for today", "Options", "Force notification", "Send Feedback", "Exit"};

    public static final ArrayList<String> FEEDBACK_CATEGORIES = new ArrayList<String>() {{
        add("Request");
        add("Recommendation");
        add("Bug");
        add("Force close");
    }};

    public static final String[] DEFAULT_EVENT_TYPES = {
            "Lecture",
            "Non-Technical Lecture",
            "Seminar",
            "Exam",
            "Workshop",
            "Deadline",
            "Other"
    };
}

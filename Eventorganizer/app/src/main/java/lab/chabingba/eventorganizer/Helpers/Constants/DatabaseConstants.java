package lab.chabingba.eventorganizer.Helpers.Constants;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class DatabaseConstants {

    public static final int DATABASE_DEFAULT_VERSION = 1;
    public static final String DATABASE_VERSION_VARIABLE = "DATABASE_VERSION";
    public static final String DATABASE_NAME = "database.db";

    public static final String DEFAULT_EVENT_TABLE_NAME = "General events";
    public static final String CREATE_EVENT_TABLE_QUERY_END = " (" +
            DatabaseConstants.EVENT_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DatabaseConstants.EVENT_FIELD_TYPE + " TEXT, " +
            DatabaseConstants.EVENT_FIELD_DATE + " TEXT, " +
            DatabaseConstants.EVENT_FIELD_LOCATION + " TEXT, " +
            DatabaseConstants.EVENT_FIELD_DESCRIPTION + " TEXT," +
            DatabaseConstants.EVENT_FIELD_IS_FINISHED + " BOOLEAN, " +
            DatabaseConstants.EVENT_FIELD_HAS_NOTIFICATION + " BOOLEAN, " +
            DatabaseConstants.EVENT_FIELD_IS_OLD + " BOOLEAN" +
            ");";

    public static final String CATEGORIES_TABLE_NAME = "Categories table";
    public static final String CATEGORY_FIELD_ID = "_id";
    public static final String CATEGORY_FIELD_NAME = "name";

    public static final String CREATE_CATEGORY_TABLE_QUERY_END = " (" +
            DatabaseConstants.CATEGORY_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DatabaseConstants.CATEGORY_FIELD_NAME + " TEXT" +
            ");";

    public static final String EVENT_FIELD_ID = "_id";
    public static final String EVENT_FIELD_TYPE = "eventType";
    public static final String EVENT_FIELD_DATE = "eventDate";
    public static final String EVENT_FIELD_LOCATION = "eventLocation";
    public static final String EVENT_FIELD_DESCRIPTION = "eventDescription";
    public static final String EVENT_FIELD_IS_FINISHED = "eventIsFinished";
    public static final String EVENT_FIELD_HAS_NOTIFICATION = "eventHasNotification";
    public static final String EVENT_FIELD_IS_OLD = "eventIsOld";

    public static final String CREATE_TABLE_QUERY_START = "CREATE TABLE ";
    public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    public static final String DELETE_ROW_QUERY_START = "DELETE FROM ";
    public static final String WHERE = " WHERE ";
    public static final String SELECT = "SELECT ";
    public static final String FROM = " FROM ";
    public static final String SELECT_ALL_TABLES_QUERY = "SELECT name FROM sqlite_master WHERE type='table'";

    public static final String CREATE_EVENT_TYPES_TABLE = "CREATE TABLE Event_types (_id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT);";

    public static final String TYPE_FIELD_TYPE = "type";

    public static final String EVENT_TYPE_TABLE_NAME = "Event types";

    public static final String MOVE_EVENT_QUERY_START = "INSERT INTO ";
    public static final String MOVE_EVENT_QUERY_MIDDLE = " SELECT * FROM ";
    public static final String MOVE_EVENT_QUERY_END = " WHERE _id = ";
    public static final String DELETE_ALL_FROM_TABLE_QUERY = "DELETE from ";
}

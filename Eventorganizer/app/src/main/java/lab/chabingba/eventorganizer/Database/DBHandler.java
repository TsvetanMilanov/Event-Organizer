package lab.chabingba.eventorganizer.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;
import lab.chabingba.eventorganizer.Helpers.Constants.GeneralConstants;
import lab.chabingba.eventorganizer.Helpers.QueryHelpers;

/**
 * Created by Tsvetan on 2015-05-25.
 */

//DON'T forget to convert the table names except when passing to methods!!!

public class DBHandler extends SQLiteOpenHelper {
    private Context receivedContext;

    private static int DATABASE_VERSION;
    private static String DATABASE_NAME;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        this.receivedContext = context;
        DATABASE_VERSION = version;
        DATABASE_NAME = DatabaseConstants.DATABASE_NAME;

        Log.i("CTR", "Constructor passed.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryForCategoryTable = QueryHelpers.CreateQueryForCategoryTable(DatabaseConstants.CATEGORIES_TABLE_NAME);
        db.execSQL(queryForCategoryTable);

        AddNewTable(db, DatabaseConstants.DEFAULT_EVENT_TABLE_NAME);

        Log.i("CREATE", "ONCREATE completed.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("SQL", "OnUpgrade method called");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.CATEGORIES_TABLE_NAME);
        onCreate(db);
    }

    private int UpdateDatabaseVersion(Context context) {
        SharedPreferences settings = context.getSharedPreferences(GeneralConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int currentDatabaseVersion = settings.getInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);

        int result = currentDatabaseVersion + 1;

        return result;
    }

    public void AddNewTable(SQLiteDatabase database, String tableName) {
        String query = QueryHelpers.CreateQueryForCategoryTable(tableName);

        database.execSQL(query);

        Log.i("TABLE", "Added new Table.");
    }

    public void RemoveTable(String tableName) {
        SQLiteDatabase database = getWritableDatabase();

        String query = QueryHelpers.CreateQueryForDeletingTable(tableName);

        database.execSQL(query);
    }

    public void AddCategory(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.CATEGORY_FIELD_NAME, category.getName());

        SQLiteDatabase database = getWritableDatabase();
        database.insert(QueryHelpers.ConvertTableNameToSQLConvention(DatabaseConstants.CATEGORIES_TABLE_NAME), null, contentValues);

        AddNewTable(database, category.getName());
    }

    public void AddCategory(String category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.CATEGORY_FIELD_NAME, category);

        SQLiteDatabase database = getWritableDatabase();
        database.insert(QueryHelpers.ConvertTableNameToSQLConvention(DatabaseConstants.CATEGORIES_TABLE_NAME), null, contentValues);

        AddNewTable(database, category);
    }

    public void RemoveCategory(String categoryName) {
        SQLiteDatabase database = getWritableDatabase();

        String query = QueryHelpers.CreateQueryForDeletingRow(DatabaseConstants.CATEGORIES_TABLE_NAME, DatabaseConstants.CATEGORY_FIELD_NAME, categoryName);

        database.execSQL(query);

        RemoveTable(categoryName);
    }

    public void AddEvent(MyEvent event, String tableName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.EVENT_FIELD_TYPE, event.getType());
        contentValues.put(DatabaseConstants.EVENT_FIELD_DATE, event.getDateAsStringWithDefaultFormat());
        contentValues.put(DatabaseConstants.EVENT_FIELD_LOCATION, event.getLocation());
        contentValues.put(DatabaseConstants.EVENT_FIELD_DESCRIPTION, event.getDescription());
        contentValues.put(DatabaseConstants.EVENT_FIELD_IS_FINISHED, event.getIsFinished());
        contentValues.put(DatabaseConstants.EVENT_FIELD_HAS_NOTIFICATION, event.getHasNotification());
        contentValues.put(DatabaseConstants.EVENT_FIELD_IS_OLD, event.getIsOld());

        SQLiteDatabase database = getWritableDatabase();
        database.insert(QueryHelpers.ConvertTableNameToSQLConvention(tableName), null, contentValues);

        database.close();
    }

    public void RemoveEvent(String table, int id) {
        SQLiteDatabase database = getWritableDatabase();

        String query = QueryHelpers.CreateQueryForDeletingRow(table, DatabaseConstants.EVENT_FIELD_ID, id);

        database.execSQL(query);

        database.close();
    }

    public ArrayList<MyEvent> CreateListWithEventsFromTable(String tableName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = QueryHelpers.CreateQueryForSelectingWholeTable(tableName);

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();

        ArrayList<MyEvent> result = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            MyEvent eventToAdd = ParseEventFromCursor(cursor);

            result.add(eventToAdd);

            cursor.moveToNext();
        }

        sqLiteDatabase.close();

        return result;
    }

    public ArrayList<Category> CreateListWithCategoriesFromTable(String tableName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = QueryHelpers.CreateQueryForSelectingWholeTable(tableName);

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();

        ArrayList<Category> result = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            Category categoryToAdd = ParseCategoryFromCursor(cursor);

            result.add(categoryToAdd);

            cursor.moveToNext();
        }

        sqLiteDatabase.close();

        return result;
    }

    private Category ParseCategoryFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.CATEGORY_FIELD_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CATEGORY_FIELD_NAME));

        Category categoryToAdd = new Category();

        if (id >= 0) {
            categoryToAdd.setId(id);
        } else {
            categoryToAdd.setId(777);
        }

        categoryToAdd.setName(name);

        return categoryToAdd;
    }

    private MyEvent ParseEventFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.EVENT_FIELD_ID));
        String type = cursor.getString(cursor.getColumnIndex(DatabaseConstants.EVENT_FIELD_TYPE));
        String date = cursor.getString(cursor.getColumnIndex(DatabaseConstants.EVENT_FIELD_DATE));
        String location = cursor.getString(cursor.getColumnIndex(DatabaseConstants.EVENT_FIELD_LOCATION));
        String description = cursor.getString(cursor.getColumnIndex(DatabaseConstants.EVENT_FIELD_DESCRIPTION));
        boolean isFinished = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.EVENT_FIELD_IS_FINISHED)) > 0;
        boolean hasNotification = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.EVENT_FIELD_HAS_NOTIFICATION)) > 0;
        boolean isOld = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.EVENT_FIELD_IS_OLD)) > 0;

        MyEvent eventToAdd = new MyEvent();

        if (id >= 0) {
            eventToAdd.setId(id);
        } else {
            eventToAdd.setId(777);
        }

        eventToAdd.setType(type);
        eventToAdd.setDate(date);
        eventToAdd.setLocation(location);
        eventToAdd.setIsFinished(isFinished);
        eventToAdd.setHasNotification(hasNotification);
        eventToAdd.setIsOld(isOld);

        return eventToAdd;
    }
}

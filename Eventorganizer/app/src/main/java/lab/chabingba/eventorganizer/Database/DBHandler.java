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

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i("CTR", "Constructor passed.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryForCategoryTable = QueryHelpers.createQueryForCategoryTable(DatabaseConstants.CATEGORIES_TABLE_NAME);
        Log.i("QUERY", queryForCategoryTable);

        db.execSQL(queryForCategoryTable);

        addCategory(db, DatabaseConstants.DEFAULT_EVENT_TABLE_NAME);

        Log.i("CREATE", "ONCREATE completed.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("SQL", "OnUpgrade method called");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.CATEGORIES_TABLE_NAME);
        onCreate(db);
    }

    private int updateDatabaseVersion(Context context) {
        SharedPreferences settings = context.getSharedPreferences(GeneralConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int currentDatabaseVersion = settings.getInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);

        int result = currentDatabaseVersion + 1;

        return result;
    }

    private void addNewCategoryTable(SQLiteDatabase database, String tableName) {
        String query = QueryHelpers.createQueryForCategoryTable(tableName);

        Log.i("QUERY", query);

        database.execSQL(query);

        Log.i("TABLE", "Added new Table.");
    }

    private void addNewEventTable(SQLiteDatabase database, String tableName) {
        String query = QueryHelpers.createQueryForEventTable(tableName);

        Log.i("QUERY", query);

        database.execSQL(query);

        Log.i("TABLE", "Added new Table.");
    }

    private void removeTable(SQLiteDatabase database, String tableName) {
        String query = QueryHelpers.createQueryForDeletingTable(tableName);

        database.execSQL(query);
    }

    public void removeTable(String tableName) {
        SQLiteDatabase database = getWritableDatabase();

        String query = QueryHelpers.createQueryForDeletingTable(tableName);

        database.execSQL(query);

        database.close();
    }

    private void addCategory(SQLiteDatabase database, Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.CATEGORY_FIELD_NAME, category.getName());

        database.insert(QueryHelpers.convertTableNameToSQLConvention(DatabaseConstants.CATEGORIES_TABLE_NAME), null, contentValues);

        addNewEventTable(database, category.getName());
    }

    private void addCategory(SQLiteDatabase database, String category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.CATEGORY_FIELD_NAME, category);

        database.insert(QueryHelpers.convertTableNameToSQLConvention(DatabaseConstants.CATEGORIES_TABLE_NAME), null, contentValues);

        addNewEventTable(database, category);
    }

    public void addCategory(String category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.CATEGORY_FIELD_NAME, category);

        SQLiteDatabase database = getWritableDatabase();

        database.insert(QueryHelpers.convertTableNameToSQLConvention(DatabaseConstants.CATEGORIES_TABLE_NAME), null, contentValues);

        addNewEventTable(database, category);

        database.close();
    }

    public void removeCategory(String categoryName) {
        SQLiteDatabase database = getWritableDatabase();

        String query = QueryHelpers.createQueryForDeletingRow(DatabaseConstants.CATEGORIES_TABLE_NAME, DatabaseConstants.CATEGORY_FIELD_NAME, categoryName);

        database.execSQL(query);

        removeTable(categoryName);
        database.close();
    }

    public void addEvent(MyEvent event, String tableName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.EVENT_FIELD_TYPE, event.getType());
        contentValues.put(DatabaseConstants.EVENT_FIELD_DATE, event.getDateAsStringWithDefaultFormat());
        contentValues.put(DatabaseConstants.EVENT_FIELD_LOCATION, event.getLocation());
        contentValues.put(DatabaseConstants.EVENT_FIELD_DESCRIPTION, event.getDescription());
        contentValues.put(DatabaseConstants.EVENT_FIELD_IS_FINISHED, event.getIsFinished());
        contentValues.put(DatabaseConstants.EVENT_FIELD_HAS_NOTIFICATION, event.getHasNotification());
        contentValues.put(DatabaseConstants.EVENT_FIELD_IS_OLD, event.getIsOld());

        SQLiteDatabase database = getWritableDatabase();
        database.insert(QueryHelpers.convertTableNameToSQLConvention(tableName), null, contentValues);

        database.close();
    }

    public void removeEvent(String table, int id) {
        SQLiteDatabase database = getWritableDatabase();

        String query = QueryHelpers.createQueryForDeletingRow(table, DatabaseConstants.EVENT_FIELD_ID, id);

        database.execSQL(query);

        database.close();
    }

    public ArrayList<MyEvent> createListWithEventsFromTable(String tableName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = QueryHelpers.createQueryForSelectingWholeTable(tableName);

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();

        ArrayList<MyEvent> result = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            MyEvent eventToAdd = parseEventFromCursor(cursor);

            result.add(eventToAdd);

            cursor.moveToNext();
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    public ArrayList<Category> createListWithCategoriesFromTable(String tableName) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //logAllTableNames(sqLiteDatabase);

        String query = QueryHelpers.createQueryForSelectingWholeTable(tableName);
        Log.i("QUERY", query);

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();

        ArrayList<Category> result = new ArrayList<>();

        while (!cursor.isAfterLast()) {
            Category categoryToAdd = parseCategoryFromCursor(cursor);

            result.add(categoryToAdd);

            cursor.moveToNext();
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    private void logAllTableNames(SQLiteDatabase sqLiteDatabase) {
        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor c = sqLiteDatabase.rawQuery(DatabaseConstants.SELECT_ALL_TABLES_QUERY, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                arrTblNames.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }

        for (int i = 0; i < arrTblNames.size(); i++) {
            Log.i("TABLE_NAME: ", arrTblNames.get(i));
        }
    }

    public void logAllTableNames() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ArrayList<String> arrTblNames = new ArrayList<String>();
        Cursor c = sqLiteDatabase.rawQuery(DatabaseConstants.SELECT_ALL_TABLES_QUERY, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                arrTblNames.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }

        for (int i = 0; i < arrTblNames.size(); i++) {
            Log.i("TABLE_NAME: ", arrTblNames.get(i));
        }

        sqLiteDatabase.close();
    }

    private Category parseCategoryFromCursor(Cursor cursor) {
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

    private MyEvent parseEventFromCursor(Cursor cursor) {
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

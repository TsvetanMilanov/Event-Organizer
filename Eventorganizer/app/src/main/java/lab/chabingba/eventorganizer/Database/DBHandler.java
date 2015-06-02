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
import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;
import lab.chabingba.eventorganizer.Helpers.QueryHelpers;

/**
 * Created by Tsvetan on 2015-05-25.
 */

//DON'T forget to convert the table names except when passing to methods!!!

public class DBHandler extends SQLiteOpenHelper {
    private static final String TAG = "eventorganizer.Database";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i(TAG, "Constructor passed.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryForCategoryTable = QueryHelpers.createQueryForCategoryTable(DatabaseConstants.CATEGORIES_TABLE_NAME);
        Log.i(TAG, queryForCategoryTable);

        db.execSQL(queryForCategoryTable);

        addCategory(db, DatabaseConstants.DEFAULT_EVENT_TABLE_NAME);

        Log.i(TAG, "ONCREATE completed.");

        Log.i(TAG, DatabaseConstants.CREATE_EVENT_TYPES_TABLE);

        db.execSQL(DatabaseConstants.CREATE_EVENT_TYPES_TABLE);

        AddDefaultTypesToDatabase(db);
    }

    private void AddDefaultTypesToDatabase(SQLiteDatabase db) {
        for (int i = 0; i < GlobalConstants.DEFAULT_EVENT_TYPES.length; i++) {
            this.addEventType(db, GlobalConstants.DEFAULT_EVENT_TYPES[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "OnUpgrade method called");
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.CATEGORIES_TABLE_NAME);
        onCreate(db);
    }

    private int updateDatabaseVersion(Context context) {
        SharedPreferences settings = context.getSharedPreferences(GlobalConstants.PREFS_NAME, Context.MODE_PRIVATE);
        int currentDatabaseVersion = settings.getInt(DatabaseConstants.DATABASE_VERSION_VARIABLE, DatabaseConstants.DATABASE_DEFAULT_VERSION);

        int result = currentDatabaseVersion + 1;

        return result;
    }

    private void addNewCategoryTable(SQLiteDatabase database, String tableName) {
        String query = QueryHelpers.createQueryForCategoryTable(tableName);

        Log.i(TAG, query);

        database.execSQL(query);

        Log.i(TAG, "Added new Table.");
    }

    private void addNewEventTable(SQLiteDatabase database, String tableName) {
        String query = QueryHelpers.createQueryForEventTable(tableName);

        Log.i(TAG, query);

        database.execSQL(query);

        Log.i(TAG, "Added new Table.");
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
        Log.i(TAG, query);

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
        ArrayList<String> arrTblNames = getAllTableNames(sqLiteDatabase);

        for (int i = 0; i < arrTblNames.size(); i++) {
            Log.i(TAG, arrTblNames.get(i));
        }
    }

    public ArrayList<String> getAllTableNames(SQLiteDatabase sqLiteDatabase) {
        ArrayList<String> arrTblNames = new ArrayList<>();
        Cursor c = sqLiteDatabase.rawQuery(DatabaseConstants.SELECT_ALL_TABLES_QUERY, null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                arrTblNames.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        c.close();

        return arrTblNames;
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
            Log.i(TAG, arrTblNames.get(i));
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

        MyEvent eventToAdd = new MyEvent(id, type, date, location, description, isFinished, hasNotification, isOld);

        /*
            //Manual set event properties:
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
        */

        return eventToAdd;
    }

    public void addEventType(String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.TYPE_FIELD_TYPE, type);

        SQLiteDatabase database = getWritableDatabase();

        database.insert(QueryHelpers.convertTableNameToSQLConvention(DatabaseConstants.EVENT_TYPE_TABLE_NAME), null, contentValues);

        database.close();
    }

    private void addEventType(SQLiteDatabase database, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.TYPE_FIELD_TYPE, type);

        database.insert(QueryHelpers.convertTableNameToSQLConvention(DatabaseConstants.EVENT_TYPE_TABLE_NAME), null, contentValues);
    }

    public void removeEventType(String type) {
        SQLiteDatabase database = getWritableDatabase();

        String query = QueryHelpers.createQueryForDeletingRow(DatabaseConstants.EVENT_TYPE_TABLE_NAME, DatabaseConstants.TYPE_FIELD_TYPE, type);

        database.execSQL(query);

        removeTable(type);
        database.close();
    }

    public ArrayList<String> getEventTypesAsArray() {
        ArrayList<String> result = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        String query = QueryHelpers.createQueryForSelectingWholeTable(DatabaseConstants.EVENT_TYPE_TABLE_NAME);

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String type = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TYPE_FIELD_TYPE));

            result.add(type);

            cursor.moveToNext();
        }

        cursor.close();
        sqLiteDatabase.close();

        return result;
    }

    public void updateEvent(MyEvent editedEvent, String tableToAddTo) {
        SQLiteDatabase database = getWritableDatabase();

        String where = DatabaseConstants.EVENT_FIELD_ID + " = " + editedEvent.getId();

        ContentValues eventAsContentValues = new ContentValues();

        eventAsContentValues.put(DatabaseConstants.EVENT_FIELD_TYPE, editedEvent.getType());
        eventAsContentValues.put(DatabaseConstants.EVENT_FIELD_DATE, editedEvent.getDateAsStringWithDefaultFormat());
        eventAsContentValues.put(DatabaseConstants.EVENT_FIELD_LOCATION, editedEvent.getLocation());
        eventAsContentValues.put(DatabaseConstants.EVENT_FIELD_DESCRIPTION, editedEvent.getDescription());
        eventAsContentValues.put(DatabaseConstants.EVENT_FIELD_IS_FINISHED, editedEvent.getIsFinished());
        eventAsContentValues.put(DatabaseConstants.EVENT_FIELD_HAS_NOTIFICATION, editedEvent.getHasNotification());
        eventAsContentValues.put(DatabaseConstants.EVENT_FIELD_IS_OLD, editedEvent.getIsOld());

        int result = database.update(tableToAddTo, eventAsContentValues, where, null);

        if (result == 0) {
            Log.e(TAG, "Error while updating event:\n" + editedEvent.toString());
        } else {
            Log.i(TAG, "Successfully updated event.");
        }
        database.close();
    }

    public void moveEvent(MyEvent eventToMove, Category oldCategory, Category newCategory) {
        String oldTableName = oldCategory.getSQLName();
        String newTableName = newCategory.getSQLName();

        if (oldTableName.equals(newTableName)) {
            return;
        }

        String moveQuery = QueryHelpers.createQueryForMovingEventToAnotherTable(oldTableName, newTableName, eventToMove.getId());
        Log.d(TAG, moveQuery);

        String removeQuery = QueryHelpers.createQueryForDeletingRow(oldTableName, DatabaseConstants.EVENT_FIELD_ID, eventToMove.getId());
        Log.d(TAG, removeQuery);

        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(moveQuery);
        database.execSQL(removeQuery);
        database.close();
    }
}
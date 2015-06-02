package lab.chabingba.eventorganizer.Helpers;

import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;

/**
 * Created by Tsvetan on 2015-05-26.
 */

//DON'T forget to convert table name before using it.

public final class QueryHelpers {

    public static String createQueryForEventTable(String tableName) {
        String result = DatabaseConstants.CREATE_TABLE_QUERY_START +
                convertTableNameToSQLConvention(tableName) +
                DatabaseConstants.CREATE_EVENT_TABLE_QUERY_END;

        return result;
    }

    public static String convertTableNameToSQLConvention(String name) {
        String result = name.trim();
        result = result.replace(' ', '_');

        return result;
    }

    public static String createQueryForCategoryTable(String name) {
        String result = DatabaseConstants.CREATE_TABLE_QUERY_START +
                convertTableNameToSQLConvention(name) +
                DatabaseConstants.CREATE_CATEGORY_TABLE_QUERY_END;

        return result;
    }

    public static String createQueryForDeletingRow(String tableName, String fieldName, int id) {
        String result = "";

        result += DatabaseConstants.DELETE_ROW_QUERY_START +
                convertTableNameToSQLConvention(tableName) +
                DatabaseConstants.WHERE +
                fieldName + " =\"" + id + "\";";

        return result;
    }

    public static String createQueryForDeletingRow(String tableName, String fieldName, String value) {
        String result = "";

        result += DatabaseConstants.DELETE_ROW_QUERY_START +
                convertTableNameToSQLConvention(tableName) +
                DatabaseConstants.WHERE +
                fieldName + " =\"" + value + "\";";

        return result;
    }

    public static String createQueryForDeletingTable(String tableName) {
        String result = "";

        result += DatabaseConstants.DROP_TABLE_IF_EXISTS + convertTableNameToSQLConvention(tableName);

        return result;
    }

    public static String createQueryForSelectingWholeTable(String tableName) {
        String result = "";

        result += DatabaseConstants.SELECT +
                "*" +
                DatabaseConstants.FROM +
                convertTableNameToSQLConvention(tableName) + ";";

        return result;
    }

    public static String createQueryForMovingEventToAnotherTable(String oldTableName, String newTableName, int eventID) {
        String result = "";

        result += DatabaseConstants.MOVE_EVENT_QUERY_START + newTableName + DatabaseConstants.MOVE_EVENT_QUERY_MIDDLE + oldTableName + DatabaseConstants.MOVE_EVENT_QUERY_END + String.valueOf(eventID) + ";";

        return result;
    }
}

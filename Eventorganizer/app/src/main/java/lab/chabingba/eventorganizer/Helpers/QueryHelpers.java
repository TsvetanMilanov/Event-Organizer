package lab.chabingba.eventorganizer.Helpers;

import lab.chabingba.eventorganizer.Helpers.Constants.DatabaseConstants;

/**
 * Created by Tsvetan on 2015-05-26.
 */

//DON'T forget to convert table name before using it.

public final class QueryHelpers {

    public static String CreateQueryForEventTable(String tableName) {
        String result = DatabaseConstants.CREATE_TABLE_QUERY_START +
                ConvertTableNameToSQLConvention(tableName) +
                DatabaseConstants.CREATE_EVENT_TABLE_QUERY_END;

        return result;
    }

    public static String ConvertTableNameToSQLConvention(String name) {
        String result = name.trim();
        result = result.replace(' ', '_');

        return result;
    }

    public static String CreateQueryForCategoryTable(String name) {
        String result = DatabaseConstants.CREATE_TABLE_QUERY_START +
                ConvertTableNameToSQLConvention(name) +
                DatabaseConstants.CREATE_CATEGORY_TABLE_QUERY_END;

        return result;
    }

    public static String CreateQueryForDeletingRow(String tableName, String fieldName, int id) {
        String result = "";

        result += DatabaseConstants.DELETE_ROW_QUERY_START +
                ConvertTableNameToSQLConvention(tableName) +
                DatabaseConstants.WHERE +
                fieldName + " =\"" + id + "\";";

        return result;
    }

    public static String CreateQueryForDeletingRow(String tableName, String fieldName, String value) {
        String result = "";

        result += DatabaseConstants.DELETE_ROW_QUERY_START +
                ConvertTableNameToSQLConvention(tableName) +
                DatabaseConstants.WHERE +
                fieldName + " =\"" + value + "\";";

        return result;
    }

    public static String CreateQueryForDeletingTable(String tableName) {
        String result = "";

        result += DatabaseConstants.DROP_TABLE_IF_EXISTS + ConvertTableNameToSQLConvention(tableName);

        return result;
    }

    public static String CreateQueryForSelectingWholeTable(String tableName) {
        String result = "";

        result += DatabaseConstants.SELECT +
                "*" +
                DatabaseConstants.FROM +
                ConvertTableNameToSQLConvention(tableName) + ";";

        return result;
    }
}

package lab.chabingba.eventorganizer.Helpers;

import java.util.ArrayList;

import lab.chabingba.eventorganizer.Helpers.Constants.GlobalConstants;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class ValidatorHelpers {
    public static String isNullOrEmptyConverter(String inputString) {
        if (inputString == null || inputString.length() <= 0) {
            return GlobalConstants.EMPTY_STRING;
        }

        return inputString;
    }

    public static boolean isNullOrEmpty(String inputString) {
        if (inputString == null || inputString.length() <= 0) {
            return true;
        }

        return false;
    }

    public static boolean isNullOrEmpty(ArrayList list) {
        if (list == null || list.size() <= 0) {
            return true;
        }

        return false;
    }
}

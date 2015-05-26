package lab.chabingba.eventorganizer.Helpers;

import lab.chabingba.eventorganizer.Helpers.Constants.GeneralConstants;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class ValidatorHelpers {
    public static String isNullOrEmpty(String inputString) {
        if (inputString == null || inputString.length() <= 0) {
            return GeneralConstants.EMPTY_STRING;
        }

        return inputString;
    }
}

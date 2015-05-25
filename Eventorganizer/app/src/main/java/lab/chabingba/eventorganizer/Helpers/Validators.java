package lab.chabingba.eventorganizer.Helpers;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public final class Validators {
    public static String isNullOrEmpty(String inputString) {
        if (inputString == null || inputString.length() <= 0) {
            return Constants.EMPTY_STRING;
        }

        return inputString;
    }
}

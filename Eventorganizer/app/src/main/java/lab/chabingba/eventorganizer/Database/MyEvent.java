package lab.chabingba.eventorganizer.Database;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import lab.chabingba.eventorganizer.Helpers.Constants.GeneralConstants;
import lab.chabingba.eventorganizer.Helpers.ValidatorHelpers;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public class MyEvent {
    private int id;
    private String type;
    private Calendar date;
    private String location;
    private String description;
    private boolean isFinished;
    private boolean hasNotification;
    private boolean isOld;

    public MyEvent() {
    }

    public MyEvent(String inputType, String inputDate, String inputLocation,
                   String inputDescription, boolean inputIsFinished, boolean inputHasNotification, boolean inputIsOld) {
        this.setType(inputType);
        this.setDate(inputDate);
        this.setLocation(inputLocation);
        this.setDescription(inputDescription);
        this.setIsFinished(inputIsFinished);
        this.setHasNotification(inputHasNotification);
        this.setIsOld(inputIsOld);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return ValidatorHelpers.isNullOrEmpty(this.type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public Calendar getDate() {
        if (this.date == null) {
            return Calendar.getInstance();
        }

        return this.date;
    }

    public String getDateAsStringWithDefaultFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GeneralConstants.DATE_DEFAULT_FORMAT);

        String result = simpleDateFormat.format(this.date.getTime());

        return result;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GeneralConstants.DATE_DEFAULT_FORMAT);

        try {
            this.date.setTime(simpleDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Date parse in setter.", e.getMessage());
        }
    }

    public String getLocation() {
        return ValidatorHelpers.isNullOrEmpty(this.location);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return ValidatorHelpers.isNullOrEmpty(this.description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsFinished() {
        return this.isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public boolean getHasNotification() {
        return this.hasNotification;
    }

    public void setHasNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
    }

    public boolean getIsOld() {
        return this.isOld;
    }

    public void setIsOld(boolean isOld) {
        this.isOld = isOld;
    }

    public String getEventDateAsString() {
        String result = "";

        Calendar date = this.getDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GeneralConstants.DATE_FORMATTER);

        result += simpleDateFormat.format(date.getTime());

        return result;
    }

    public String getEventDayOfWeekAsString() {
        String result = "";

        Calendar date = this.getDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GeneralConstants.DATE_FORMATTER_DAY_OF_WEEK);

        result += simpleDateFormat.format(date.getTime());

        return result;
    }

    public String getEventHourAsString() {
        String result = "";

        Calendar date = this.getDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GeneralConstants.DATE_FORMATTER_HOUR);

        result += simpleDateFormat.format(date.getTime());

        return result;
    }
}

package lab.chabingba.eventorganizer.Database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lab.chabingba.eventorganizer.Helpers.Constants;
import lab.chabingba.eventorganizer.Helpers.Validators;

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
        this.setDate(Calendar.getInstance());
        this.setIsFinished(true);
        this.setHasNotification(true);
        this.setIsOld(true);
    }

    public MyEvent(int inputId, String inputType, Calendar inputDate, String inputLocation, String inputDescription) {
        this();

        this.setId(inputId);
        this.setType(inputType);
        this.setDate(inputDate);
        this.setLocation(inputLocation);
        this.setDescription(inputDescription);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return Validators.isNullOrEmpty(this.type);
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

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getLocation() {
        return Validators.isNullOrEmpty(this.location);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return Validators.isNullOrEmpty(this.description);
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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMATTER);

        result += simpleDateFormat.format(date.getTime());

        return result;
    }

    public String getEventDayOfWeekAsString() {
        String result = "";

        Calendar date = this.getDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMATTER_DAY_OF_WEEK);

        result += simpleDateFormat.format(date.getTime());

        return result;
    }

    public String getEventHourAsString() {
        String result = "";

        Calendar date = this.getDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMATTER_HOUR);

        result += simpleDateFormat.format(date.getTime());

        return result;
    }
}

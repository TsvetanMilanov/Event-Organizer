package lab.chabingba.eventorganizer.Database;

/**
 * Created by Tsvetan on 2015-05-29.
 */
public class EventOfCategory {
    private MyEvent event;
    private Category category;

    public EventOfCategory(Category category, MyEvent event) {
        this.setCategory(category);
        this.setEvent(event);
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public MyEvent getEvent() {
        return this.event;
    }

    public void setEvent(MyEvent event) {
        this.event = event;
    }
}

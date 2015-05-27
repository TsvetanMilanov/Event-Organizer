package lab.chabingba.eventorganizer.Database;

import java.io.Serializable;

import lab.chabingba.eventorganizer.Helpers.ValidatorHelpers;

/**
 * Created by Tsvetan on 2015-05-25.
 */
public class Category implements Serializable {
    private int id;
    private String name;

    public Category() {

    }

    public Category(String inputName) {
        this.setName(inputName);
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = ValidatorHelpers.isNullOrEmptyConverter(name);
    }
}

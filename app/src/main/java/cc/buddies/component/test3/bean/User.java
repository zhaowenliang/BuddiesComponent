package cc.buddies.component.test3.bean;

import java.io.Serializable;

public class User implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}

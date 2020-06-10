package org.springframework.fu.jafu.redis;

import java.io.Serializable;

public class TestUser implements Serializable {

    private final String id;
    private final String name;

    public TestUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}

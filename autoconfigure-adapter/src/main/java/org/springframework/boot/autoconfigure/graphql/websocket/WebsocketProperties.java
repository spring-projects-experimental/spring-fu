package org.springframework.boot.autoconfigure.graphql.websocket;

import org.springframework.lang.NonNull;

public class WebsocketProperties {

    private String mapping = "/graphql";

    @NonNull
    public String getMapping() {
        return mapping;
    }

    public void setMapping(@NonNull String mapping) {
        this.mapping = mapping;
    }
}
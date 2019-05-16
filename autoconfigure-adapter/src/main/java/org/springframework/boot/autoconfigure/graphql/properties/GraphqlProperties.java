package org.springframework.boot.autoconfigure.graphql.properties;

import org.springframework.lang.NonNull;

public class GraphqlProperties {

    private String mapping = "/graphql";

    @NonNull
    public String getMapping() {
        return mapping;
    }

    public void setMapping(@NonNull String mapping) {
        this.mapping = mapping;
    }
}
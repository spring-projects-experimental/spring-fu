package org.springframework.boot.autoconfigure.graphql.handler.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphql.Assert;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class GraphqlRequest {

    private final String query;

    private String operationName;

    private Map<String, Object> variables;

    private boolean readonly = false;

    @JsonCreator
    public GraphqlRequest(@NonNull @JsonProperty("query") String query) {
        this.query = Assert.assertNotNull(query, "query must be provided");
    }

    public GraphqlRequest(String query, String operationName) {
        this.query = query;
        this.operationName = operationName;
    }

    public GraphqlRequest(String query, String operationName, Map<String, Object> variables) {
        this.query = query;
        this.operationName = operationName;
        this.variables = variables != null ? variables : new HashMap<>();
    }

    @NonNull
    public String getQuery() {
        return query;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}
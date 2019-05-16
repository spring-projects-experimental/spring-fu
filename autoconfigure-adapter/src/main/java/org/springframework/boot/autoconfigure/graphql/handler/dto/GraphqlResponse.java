package org.springframework.boot.autoconfigure.graphql.handler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_ABSENT)
public class GraphqlResponse {

    private Object data;

    private Map<Object, Object> extensions = new HashMap<>();

    private List<Map<String, Object>> errors = new ArrayList<>();

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<Object, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<Object, Object> extensions) {
        this.extensions = extensions;
    }

    public List<Map<String, Object>> getErrors() {
        return errors;
    }

    public void setErrors(List<Map<String, Object>> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphqlResponse response = (GraphqlResponse) o;
        return Objects.equals(data, response.data) &&
                Objects.equals(extensions, response.extensions) &&
                Objects.equals(errors, response.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, extensions, errors);
    }
}
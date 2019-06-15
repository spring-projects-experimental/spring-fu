package org.springframework.boot.autoconfigure.graphql.handler.invocation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlRequest;
import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GraphqlInvocation {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphqlInvocation.class);

    private final GraphQL graphql;

    private final ObjectMapper objectMapper;

    public GraphqlInvocation(@NonNull GraphQL graphql, ObjectMapper objectMapper) {
        this.graphql = graphql;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<ExecutionResult> invokePublisher(@NonNull GraphqlRequest request) {
        Assert.notNull(request, "graphqlRequest must not be null");
        ExecutionInput executionInput = ExecutionInput.newExecutionInput(request.getQuery())
                .operationName(request.getOperationName())
                .variables(request.getVariables() != null ? request.getVariables() : new HashMap<>())
                .build();
        return graphql.executeAsync(executionInput);
    }

    public CompletableFuture<GraphqlResponse> invoke(@NonNull GraphqlRequest graphqlRequest) {
        Assert.notNull(graphqlRequest, "graphqlRequest must not be null");
        ExecutionInput executionInput = ExecutionInput.newExecutionInput(graphqlRequest.getQuery())
                .operationName(graphqlRequest.getOperationName())
                .variables(graphqlRequest.getVariables() != null ? graphqlRequest.getVariables() : new HashMap<>())
                .context(builder -> builder.of("readonly", graphqlRequest.isReadonly()))
                .build();
        return graphql.executeAsync(executionInput).thenApply(it -> objectMapper.convertValue(it.toSpecification(), GraphqlResponse.class));
    }

    public CompletableFuture<GraphqlResponse> invoke(@NonNull Map<String, String> requestMap) {
        GraphqlRequest graphqlRequest = new GraphqlRequest(requestMap.get("query"));
        graphqlRequest.setOperationName(requestMap.get("operationName"));
        if (requestMap.containsKey("variables")) {
            try {
                graphqlRequest.setVariables(objectMapper.readValue(requestMap.get("variables"), new MapTypeReference()));
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        graphqlRequest.setReadonly(true);
        return invoke(graphqlRequest);
    }

    private class MapTypeReference extends TypeReference<Map<String, Object>> {

    }
}
package org.springframework.boot.autoconfigure.graphql.handler;

import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlRequest;
import org.springframework.boot.autoconfigure.graphql.handler.invocation.GraphqlInvocation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public class GraphqlServletHandler {

    private final GraphqlInvocation graphqlInvocation;

    public GraphqlServletHandler(@NonNull GraphqlInvocation graphqlInvocation) {
        this.graphqlInvocation = graphqlInvocation;
    }

    @NonNull
    public ServerResponse graphqlGet(@NonNull ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(graphqlInvocation.invoke(serverRequest.params().toSingleValueMap()));
    }

    @NonNull
    public ServerResponse graphqlPost(@NonNull ServerRequest serverRequest) throws ServletException, IOException {
        MediaType contentType = serverRequest.headers().contentType().orElseThrow(() -> new ResponseStatusException(HttpStatus.ACCEPTED));
        if (contentType.includes(MediaType.APPLICATION_JSON)) {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(graphqlInvocation.invoke(serverRequest.body(GraphqlRequest.class)));
        }
        if (contentType.includes(new MediaType("application", "graphql"))) {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(graphqlInvocation.invoke(new GraphqlRequest(serverRequest.body(String.class))));
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
    }
}
package org.springframework.boot.autoconfigure.graphql.handler;

import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlRequest;
import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlResponse;
import org.springframework.boot.autoconfigure.graphql.handler.invocation.GraphqlInvocation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class GraphqlReactiveHandler {

    private final GraphqlInvocation graphqlInvocation;

    public GraphqlReactiveHandler(@NonNull GraphqlInvocation graphqlInvocation) {
        this.graphqlInvocation = graphqlInvocation;
    }

    @NonNull
    public Mono<ServerResponse> graphqlGet(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.fromCompletionStage(graphqlInvocation.invoke(serverRequest.queryParams().toSingleValueMap())), GraphqlResponse.class);
    }

    @NonNull
    public Mono<ServerResponse> graphqlPost(ServerRequest serverRequest) {
        MediaType contentType = serverRequest.headers().contentType().orElseThrow(() -> new ResponseStatusException(HttpStatus.ACCEPTED));
        if (contentType.includes(MediaType.APPLICATION_JSON)) {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(serverRequest.bodyToMono(GraphqlRequest.class).flatMap(it -> Mono.fromCompletionStage(graphqlInvocation.invoke(it))), GraphqlResponse.class);
        }
        if (contentType.includes(new MediaType("application", "graphql"))) {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(serverRequest.bodyToMono(String.class).map(GraphqlRequest::new).flatMap(it -> Mono.fromCompletionStage(graphqlInvocation.invoke(it))), GraphqlResponse.class);
        }
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
    }
}
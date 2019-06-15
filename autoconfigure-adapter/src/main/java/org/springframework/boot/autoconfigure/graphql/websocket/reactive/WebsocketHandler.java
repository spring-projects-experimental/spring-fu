package org.springframework.boot.autoconfigure.graphql.websocket.reactive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import org.springframework.boot.autoconfigure.graphql.handler.dto.GraphqlRequest;
import org.springframework.boot.autoconfigure.graphql.handler.invocation.GraphqlInvocation;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class WebsocketHandler implements WebSocketHandler {

    private final GraphqlInvocation invocation;

    private final ObjectMapper mapper;

    public WebsocketHandler(@NonNull GraphqlInvocation invocation, @NonNull ObjectMapper mapper) {
        this.invocation = invocation;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON);
        ResolvableType inputType = ResolvableType.forType(GraphqlRequest.class);
        Flux<WebSocketMessage> output = session.receive()
                .map(it -> decoder.decode(it.retain().getPayload(), inputType, MediaType.APPLICATION_JSON, Collections.emptyMap()))
                .flatMap(request -> Mono.fromCompletionStage(invocation.invokePublisher((GraphqlRequest) request))
                        .flatMapMany(it -> it.getData() != null ? it.getData() : Flux.just(it))
                        .map(ExecutionResult::toSpecification))
                .map(it -> mapToWebsocketMessage(session, it));
        return session.send(output);
    }

    private <T> WebSocketMessage mapToWebsocketMessage(WebSocketSession session, T object) {
        try {
            return session.textMessage(mapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
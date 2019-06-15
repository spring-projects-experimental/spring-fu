package org.springframework.boot.autoconfigure.graphql.websocket.reactive;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.graphql.handler.invocation.GraphqlInvocation;
import org.springframework.boot.autoconfigure.graphql.websocket.WebsocketProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;

public class WebsocketInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private final WebsocketProperties properties;

    public WebsocketInitializer(@NonNull WebsocketProperties properties) {
        this.properties = properties;
    }

    @Override
    public void initialize(@NonNull GenericApplicationContext ctx) {
        ctx.registerBean("gqlWebsocketHandler", WebSocketHandler.class, () -> {
            GraphqlInvocation invocation = ctx.getBean(GraphqlInvocation.class);
            ObjectMapper mapper = ctx.getBean(ObjectMapper.class);
            return new WebsocketHandler(invocation, mapper);
        });
        ctx.registerBean("websocketHandlerAdapter", WebSocketHandlerAdapter.class, () -> {
            RequestUpgradeStrategy strategy = ctx.getBeanProvider(RequestUpgradeStrategy.class).getIfAvailable();
            return new WebSocketHandlerAdapter(strategy != null ? new HandshakeWebSocketService(strategy) : new HandshakeWebSocketService());
        });
        ctx.registerBean("websocketHandlerMapping", HandlerMapping.class, () -> {
            SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
            Map<String, WebSocketHandler> urlMapping = new HashMap<>();
            urlMapping.put(properties.getMapping(), ctx.getBean("gqlWebsocketHandler", WebSocketHandler.class));
            handlerMapping.setUrlMap(urlMapping);
            handlerMapping.setOrder(1);
            return handlerMapping;
        });
    }
}
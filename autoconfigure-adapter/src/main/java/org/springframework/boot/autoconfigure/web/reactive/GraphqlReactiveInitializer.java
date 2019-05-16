package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.autoconfigure.graphql.handler.GraphqlReactiveHandler;
import org.springframework.boot.autoconfigure.graphql.handler.invocation.GraphqlInvocation;
import org.springframework.boot.autoconfigure.graphql.properties.GraphqlProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

public class GraphqlReactiveInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private final GraphqlProperties properties;

    public GraphqlReactiveInitializer(@NonNull GraphqlProperties properties) {
        this.properties = properties;
    }

    @Override
    public void initialize(@NonNull GenericApplicationContext ctx) {
        ctx.registerBean("gqlReactiveHandler", GraphqlReactiveHandler.class, () -> {
            GraphqlInvocation invocation = ctx.getBean("gqlInvocation", GraphqlInvocation.class);
            return new GraphqlReactiveHandler(invocation);
        });
        ctx.registerBean("gqlRouterFunction", RouterFunction.class, () -> {
            GraphqlReactiveHandler handler = ctx.getBean("gqlReactiveHandler", GraphqlReactiveHandler.class);
            return RouterFunctions.route()
                    .GET(properties.getMapping(), handler::graphqlGet)
                    .POST(properties.getMapping(), handler::graphqlPost)
                    .build();
        });
    }
}
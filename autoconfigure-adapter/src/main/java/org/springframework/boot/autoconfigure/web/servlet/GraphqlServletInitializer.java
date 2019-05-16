package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.boot.autoconfigure.graphql.handler.GraphqlServletHandler;
import org.springframework.boot.autoconfigure.graphql.handler.invocation.GraphqlInvocation;
import org.springframework.boot.autoconfigure.graphql.properties.GraphqlProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;

public class GraphqlServletInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private final GraphqlProperties properties;

    public GraphqlServletInitializer(@NonNull GraphqlProperties properties) {
        this.properties = properties;
    }

    @Override
    public void initialize(@NonNull GenericApplicationContext ctx) {
        ctx.registerBean("gqlServletHandler", GraphqlServletHandler.class, () -> {
            GraphqlInvocation invocation = ctx.getBean("gqlInvocation", GraphqlInvocation.class);
            return new GraphqlServletHandler(invocation);
        });
        ctx.registerBean("gqlRouterFunction", RouterFunction.class, () -> {
            GraphqlServletHandler handler = ctx.getBean("gqlServletHandler", GraphqlServletHandler.class);
            return RouterFunctions.route()
                    .GET(properties.getMapping(), handler::graphqlGet)
                    .POST(properties.getMapping(), handler::graphqlPost)
                    .build();
        });
    }
}
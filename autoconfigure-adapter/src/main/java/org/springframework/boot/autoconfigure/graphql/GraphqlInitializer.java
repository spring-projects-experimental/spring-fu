package org.springframework.boot.autoconfigure.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLSchema;
import org.springframework.boot.autoconfigure.graphql.handler.invocation.GraphqlInvocation;
import org.springframework.boot.autoconfigure.graphql.instrumentation.SchemaInstrumentation;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GraphqlInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(@NonNull GenericApplicationContext ctx) {
        ctx.registerBean("graphql", GraphQL.class, () -> {
            GraphQLSchema schema = ctx.getBean("gqlSchema", GraphQLSchema.class);
            List<Instrumentation> instrumentations = new ArrayList<>(ctx.getBeansOfType(Instrumentation.class).values());
            return GraphQL.newGraphQL(schema)
                    .instrumentation(new ChainedInstrumentation(instrumentations))
                    .build();
        });
        ctx.registerBean("gqlInvocation", GraphqlInvocation.class, () -> {
            GraphQL graphql = ctx.getBean("graphql", GraphQL.class);
            return new GraphqlInvocation(graphql, ctx.getBean(ObjectMapper.class));
        });
        ctx.registerBean("gqlSchemaInstrumentation", Instrumentation.class, SchemaInstrumentation::new);
    }
}
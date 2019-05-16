package org.springframework.boot.autoconfigure.graphql.instrumentation;

import graphql.GraphQLContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import graphql.schema.visibility.BlockedFields;

public class SchemaInstrumentation extends SimpleInstrumentation {


    @Override
    public GraphQLSchema instrumentSchema(GraphQLSchema schema, InstrumentationExecutionParameters parameters) {
        GraphQLContext context = parameters.getContext();
        if (context.getOrDefault("readonly", false)) {
            GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry(schema.getCodeRegistry())
                    .fieldVisibility(BlockedFields.newBlock()
                            .addPattern("Mutation.*")
                            .build())
                    .build();
            return GraphQLSchema.newSchema(schema)
                    .codeRegistry(codeRegistry)
                    .build();
        }
        return schema;
    }
}
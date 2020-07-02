package org.springframework.boot.autoconfigure.data.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryBuilder;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.r2dbc.EmbeddedDatabaseConnection;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.r2dbc.core.DatabaseClient;

import java.util.List;

public class R2dbcInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private final R2dbcProperties properties;
    private final List<ConnectionFactoryOptionsBuilderCustomizer> optionsCustomizers;

    public R2dbcInitializer(R2dbcProperties properties, List<ConnectionFactoryOptionsBuilderCustomizer> optionsCustomizers) {
        this.properties = properties;
        this.optionsCustomizers = optionsCustomizers;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        ConnectionFactory connectionFactory = ConnectionFactoryBuilder.of(properties, () -> EmbeddedDatabaseConnection.get(context.getClassLoader()))
                .configure((options) -> {
                    for (ConnectionFactoryOptionsBuilderCustomizer optionsCustomizer : optionsCustomizers) {
                        optionsCustomizer.customize(options);
                    }
                })
                .build();

        context.registerBean(DatabaseClient.class, () -> DatabaseClient.builder().connectionFactory(connectionFactory).build());
    }

}
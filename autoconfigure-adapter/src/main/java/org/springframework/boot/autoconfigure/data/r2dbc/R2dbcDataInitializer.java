package org.springframework.boot.autoconfigure.data.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryBuilder;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.r2dbc.EmbeddedDatabaseConnection;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerInitializer;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.function.Supplier;

public class R2dbcDataInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    public R2dbcDataInitializer() {
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        Supplier<R2dbcDataAutoConfiguration> r2dbcDataConfiguration = new Supplier<R2dbcDataAutoConfiguration>() {

            private R2dbcDataAutoConfiguration configuration;

            @Override
            public R2dbcDataAutoConfiguration get() {
                if (configuration == null) {
                    configuration = new R2dbcDataAutoConfiguration(context.getBean(DatabaseClient.class));
                }
                return configuration;
            }
        };

        context.registerBean(R2dbcEntityTemplate.class, () -> r2dbcDataConfiguration.get().r2dbcEntityTemplate(context.getBean(ReactiveDataAccessStrategy.class)));
        context.registerBean(R2dbcCustomConversions.class, () -> r2dbcDataConfiguration.get().r2dbcCustomConversions());
        context.registerBean(R2dbcMappingContext.class, () -> r2dbcDataConfiguration.get().r2dbcMappingContext(context.getBeanProvider(NamingStrategy.class), context.getBean(R2dbcCustomConversions.class)));
        context.registerBean(ReactiveDataAccessStrategy.class, () -> r2dbcDataConfiguration.get().reactiveDataAccessStrategy(context.getBean(R2dbcMappingContext.class), context.getBean(R2dbcCustomConversions.class)));
    }

}
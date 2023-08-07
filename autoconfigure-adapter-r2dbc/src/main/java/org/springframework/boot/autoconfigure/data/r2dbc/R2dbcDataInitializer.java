package org.springframework.boot.autoconfigure.data.r2dbc;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext;
import org.springframework.data.relational.RelationalManagedTypes;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.function.Supplier;

public class R2dbcDataInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    public R2dbcDataInitializer() {
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        Supplier<R2dbcDataAutoConfiguration> r2dbcDataConfiguration = new Supplier<>() {

            private R2dbcDataAutoConfiguration configuration;

            @Override
            public R2dbcDataAutoConfiguration get() {
                if (configuration == null) {
                    configuration = new R2dbcDataAutoConfiguration(context.getBean(DatabaseClient.class));
                }
                return configuration;
            }
        };

        context.registerBean(
            R2dbcEntityTemplate.class,
            () -> r2dbcDataConfiguration
                .get()
                .r2dbcEntityTemplate(context.getBean(R2dbcConverter.class))
        );

        context.registerBean(
            R2dbcCustomConversions.class,
            () -> r2dbcDataConfiguration
                .get()
                .r2dbcCustomConversions()
        );

        context.registerBean(
            RelationalManagedTypes.class,
            () -> {
                try {
                    return R2dbcDataAutoConfiguration.r2dbcManagedTypes(context);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        );

        context.registerBean(
            R2dbcMappingContext.class,
            () -> r2dbcDataConfiguration
                .get()
                .r2dbcMappingContext(
                    context.getBeanProvider(NamingStrategy.class),
                    context.getBean(R2dbcCustomConversions.class),
                    context.getBean(RelationalManagedTypes.class)
                )
        );

        context.registerBean(
            MappingR2dbcConverter.class,
            () -> r2dbcDataConfiguration
                .get()
                .r2dbcConverter(
                    context.getBean(R2dbcMappingContext.class),
                    context.getBean(R2dbcCustomConversions.class)
                )
        );
    }
}

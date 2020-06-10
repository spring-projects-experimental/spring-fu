package org.springframework.fu.jafu.cassandra;

import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Consumer;

/**
 * JoFu DSL for Reactive Cassandra configuration.
 *
 * @author Andreas Gebhardt
 */
public class ReactiveCassandraDsl extends CassandraDsl {

    ReactiveCassandraDsl(Consumer<CassandraDsl> dsl) {
        super(dsl);
    }

    public static ApplicationContextInitializer<GenericApplicationContext> reactiveCassandra() {
        return new ReactiveCassandraDsl(it -> {
        });
    }

    public static ApplicationContextInitializer<GenericApplicationContext> reactiveCassandra(Consumer<CassandraDsl> dsl) {
        return new ReactiveCassandraDsl(dsl);
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        new CassandraReactiveDataInitializer().initialize(context);
    }

}

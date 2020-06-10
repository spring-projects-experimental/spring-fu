package org.springframework.fu.jafu.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisReactiveInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Consumer;

/**
 * JaFu DSL for reactive Redis configuration.
 *
 * @author Andreas Gebhardt
 */
public class ReactiveRedisDsl extends AbstractRedisDsl<ReactiveRedisDsl> {

    public ReactiveRedisDsl(final Consumer<ReactiveRedisDsl> dsl) {
        super(dsl);
    }

    @Override
    protected ReactiveRedisDsl getSelf() {
        return this;
    }

    public static ApplicationContextInitializer<GenericApplicationContext> reactiveRedis() {
        return new ReactiveRedisDsl(dsl -> {
        });
    }

    public static ApplicationContextInitializer<GenericApplicationContext> reactiveRedis(final Consumer<ReactiveRedisDsl> dsl) {
        return new ReactiveRedisDsl(dsl);
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        new RedisReactiveInitializer().initialize(context);
    }

}

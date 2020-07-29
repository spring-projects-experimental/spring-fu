package org.springframework.fu.jafu.r2dbc;

import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcInitializer;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Jafu DSL for R2DBC (Reactive SQL) configuration.
 *
 * Enable and configure Reactive SQL support
 */
public class R2dbcDsl extends AbstractDsl {

    private final Consumer<R2dbcDsl> dsl;

    private final R2dbcProperties properties = new R2dbcProperties();

    private final List<ConnectionFactoryOptionsBuilderCustomizer> optionsCustomizers = new ArrayList<>();

    private boolean transactional = false;

    R2dbcDsl(Consumer<R2dbcDsl> dsl) {
        this.dsl = dsl;
    }

    public static ApplicationContextInitializer<GenericApplicationContext> r2dbc(Consumer<R2dbcDsl> dsl) {
        return new R2dbcDsl(dsl);
    }

    public R2dbcDsl url(String url){
        properties.setUrl(url);
        return this;
    }

    public R2dbcDsl name(String name){
        properties.setName(name);
        return this;
    }

    public R2dbcDsl username(String username){
        properties.setUsername(username);
        return this;
    }

    public R2dbcDsl password(String password){
        properties.setPassword(password);
        return this;
    }

    public R2dbcDsl generateUniqueName(Boolean generate){
        properties.setGenerateUniqueName(generate);
        return this;
    }

    public R2dbcDsl optionsCustomizer(ConnectionFactoryOptionsBuilderCustomizer customizer){
        optionsCustomizers.add(customizer);
        return this;
    }

    public R2dbcDsl transactional(boolean transactional) {
        this.transactional = transactional;
        return this;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        this.dsl.accept(this);
        new R2dbcInitializer(properties, optionsCustomizers, transactional).initialize(context);
    }
}

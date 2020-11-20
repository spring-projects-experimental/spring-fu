package org.springframework.fu.jafu.r2dbc;

import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

import java.util.function.Consumer;

public class DataR2dbcDsl extends AbstractDsl {

    private final Consumer<DataR2dbcDsl> dsl;

    public DataR2dbcDsl(Consumer<DataR2dbcDsl> dsl) {
        this.dsl = dsl;
    }

    public static ApplicationContextInitializer<GenericApplicationContext> dataR2dbc(Consumer<DataR2dbcDsl> dsl) {
        return new DataR2dbcDsl(dsl);
    }

    public DataR2dbcDsl r2dbc(Consumer<R2dbcDsl> r2dbcDsl) {
        new R2dbcDsl(r2dbcDsl).initialize(context);
        return this;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        dsl.accept(this);
        new R2dbcDataInitializer().initialize(context);
    }
}

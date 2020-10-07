package org.springframework.fu.jafu.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvokerInitializer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfigurationInitializer;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfigurationInitializer;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateConfigurationInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for JDBC configuration.
 */
public class JdbcDsl extends AbstractDsl {

    private final Consumer<JdbcDsl> dsl;

    private final JdbcProperties jdbcProperties = new JdbcProperties();

    private final DataSourceProperties dataSourceProperties = new DataSourceProperties();

    JdbcDsl(Consumer<JdbcDsl> dsl) {
        this.dsl = dsl;
    }

    public static ApplicationContextInitializer<GenericApplicationContext> jdbc() {
        return new JdbcDsl(dsl -> {});
    }

    public static ApplicationContextInitializer<GenericApplicationContext> jdbc(Consumer<JdbcDsl> dsl) {
        return new JdbcDsl(dsl);
    }

    public JdbcDsl url(String url){
        dataSourceProperties.setUrl(url);
        return this;
    }

    public JdbcDsl name(String name){
        dataSourceProperties.setName(name);
        return this;
    }

    public JdbcDsl username(String username){
        dataSourceProperties.setUsername(username);
        return this;
    }

    public JdbcDsl password(String password){
        dataSourceProperties.setPassword(password);
        return this;
    }

    public JdbcDsl generateUniqueName(Boolean generate){
        dataSourceProperties.setGenerateUniqueName(generate);
        return this;
    }

    public JdbcDsl schema(String schema){
        if (dataSourceProperties.getSchema() == null) {
            List<String> schemaList = new ArrayList<>();
            schemaList.add(schema);
            dataSourceProperties.setSchema(schemaList);
        }
        else {
            dataSourceProperties.getSchema().add(schema);
        }
        return this;
    }

    public JdbcDsl initializationMode(DataSourceInitializationMode initializationMode) {
        dataSourceProperties.setInitializationMode(initializationMode);
        return this;
    }

    public JdbcDsl data(String data){
        if (dataSourceProperties.getData() == null) {
            List<String> dataList = new ArrayList<>();
            dataList.add(data);
            dataSourceProperties.setData(dataList);
        }
        else {
            dataSourceProperties.getData().add(data);
        }
        return this;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        this.dsl.accept(this);
        new EmbeddedDataSourceConfigurationInitializer(dataSourceProperties).initialize(context);
        new JdbcTemplateConfigurationInitializer(jdbcProperties).initialize(context);
        new DataSourceTransactionManagerAutoConfigurationInitializer().initialize(context);
        new DataSourceInitializerInvokerInitializer(dataSourceProperties).initialize(context);
    }
}

package org.springframework.fu.jafu.elasticsearch;

import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticSearchDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.fu.jafu.AbstractDsl;

import java.util.Optional;
import java.util.function.Consumer;

public class ElasticSearchDsl extends AbstractElasticSearchDsl<ElasticSearchDsl> {

    public ElasticSearchDsl(Consumer<ElasticSearchDsl> dsl) {
        super(dsl);
    }

    /**
     * Configure Spring-data ElasticSearch support with default properties.
     * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
     * @see org.springframework.fu.jafu.elasticsearch.ElasticSearchDsl
     */
    public static ApplicationContextInitializer<GenericApplicationContext> elasticSearch() {
        return new ElasticSearchDsl(mongoDsl -> {});
    }

    /**
     * Configure Spring-data ElasticSearch with customized properties.
     * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
     * @see org.springframework.fu.jafu.elasticsearch.ElasticSearchDsl
     */
    public static ApplicationContextInitializer<GenericApplicationContext> elasticSearch(Consumer<ElasticSearchDsl> dsl) {
        return new ElasticSearchDsl(dsl);
    }

    @Override
    protected ElasticSearchDsl getSelf() {
        return this;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        new ElasticSearchDataInitializer(createClientConfiguration()).initialize(context);
    }
}

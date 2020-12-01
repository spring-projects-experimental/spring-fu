package org.springframework.fu.jafu.elasticsearch;

import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticSearchDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Consumer;

public class ReactiveElasticSearchDsl extends AbstractElasticSearchDsl<ReactiveElasticSearchDsl> {

    public ReactiveElasticSearchDsl(Consumer<ReactiveElasticSearchDsl> dsl) {
        super(dsl);
    }

    /**
     * Configure Spring-data ElasticSearch support with default properties.
     * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
     * @see org.springframework.fu.jafu.elasticsearch.ReactiveElasticSearchDsl
     */
    public static ApplicationContextInitializer<GenericApplicationContext> reactiveElasticSearch() {
        return new ReactiveElasticSearchDsl(mongoDsl -> {});
    }

    /**
     * Configure Spring-data ElasticSearch with customized properties.
     * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
     * @see org.springframework.fu.jafu.elasticsearch.ReactiveElasticSearchDsl
     */
    public static ApplicationContextInitializer<GenericApplicationContext> reactiveElasticSearch(Consumer<ReactiveElasticSearchDsl> dsl) {
        return new ReactiveElasticSearchDsl(dsl);
    }

    @Override
    protected ReactiveElasticSearchDsl getSelf() {
        return this;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        new ReactiveElasticSearchDataInitializer(createClientConfiguration()).initialize(context);
    }
}

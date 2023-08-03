package org.springframework.boot.autoconfigure.data.elasticsearch;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;

public class ReactiveElasticSearchDataInitializer implements ApplicationContextInitializer<GenericApplicationContext>  {

    private final ClientConfiguration clientConfiguration;

    public ReactiveElasticSearchDataInitializer(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(ReactiveElasticsearchClient.class, () -> ReactiveRestClients.create(clientConfiguration));
    }
}

package org.springframework.boot.autoconfigure.data.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

public class ElasticSearchDataInitializer implements ApplicationContextInitializer<GenericApplicationContext>  {

    private final ClientConfiguration clientConfiguration;

    public ElasticSearchDataInitializer(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean(RestHighLevelClient.class, () -> RestClients.create(clientConfiguration).rest());
    }
}

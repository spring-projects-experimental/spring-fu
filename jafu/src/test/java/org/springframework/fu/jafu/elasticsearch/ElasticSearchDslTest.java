package org.springframework.fu.jafu.elasticsearch;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.fu.jafu.Jafu;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.fu.jafu.elasticsearch.ElasticSearchDsl.elasticSearch;

class ElasticSearchDslTest {

    @Test
    public void enableElasticSearch() throws IOException {
        var es = new GenericContainer("elasticsearch:7.9.3")
                .withExposedPorts(9200)
                .withEnv("discovery.type", "single-node");

        es.start();
        var app = Jafu.application(a ->
                a.enable(elasticSearch(esDsl ->
                        esDsl.hostAndPort("localhost:" + es.getFirstMappedPort()))));

        var context = app.run();
        var client = context.getBean(RestHighLevelClient.class);
        assertNotNull(client);

        var request =  new IndexRequest("spring-data")
                .source(Collections.singletonMap("feature", "high-level-rest-client"));
        var response = client.index(request, RequestOptions.DEFAULT);
        assertEquals(201, response.status().getStatus());

        es.stop();
    }

}
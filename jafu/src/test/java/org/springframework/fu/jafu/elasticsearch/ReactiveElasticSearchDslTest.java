package org.springframework.fu.jafu.elasticsearch;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.rest.RestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.fu.jafu.Jafu;
import org.testcontainers.containers.GenericContainer;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.fu.jafu.elasticsearch.ReactiveElasticSearchDsl.reactiveElasticSearch;

class ReactiveElasticSearchDslTest {

    @Test
    public void enableReactiveElasticSearch() throws IOException {
        var es = new GenericContainer("elasticsearch:7.9.3")
                .withExposedPorts(9200)
                .withEnv("discovery.type", "single-node");

        es.start();
        var app = Jafu.application(a ->
                a.enable(reactiveElasticSearch(esDsl ->
                        esDsl.hostAndPort("localhost:" + es.getFirstMappedPort()))));

        var context = app.run();
        var reactiveClient = context.getBean(ReactiveElasticsearchClient.class);
        assertNotNull(reactiveClient);

        var request =  new IndexRequest("spring-data")
                .source(Collections.singletonMap("feature", "reactive-client"));
        StepVerifier
                .create(reactiveClient.index(request))
                .assertNext(next -> assertEquals(RestStatus.CREATED, next.status()))
                .verifyComplete();
        es.stop();
    }

}
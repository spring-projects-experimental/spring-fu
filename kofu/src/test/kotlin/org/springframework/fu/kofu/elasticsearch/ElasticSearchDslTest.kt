package org.springframework.fu.kofu.elasticsearch

import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer
import org.xnio.XnioExecutor.Key.IMMEDIATE
import java.util.Collections.singletonMap


class ElasticSearchDslTest {

    @Test
    fun `enable spring data elasticsearch`() {
        val es = object : GenericContainer<Nothing>("elasticsearch:7.9.3") {
            init {
                withExposedPorts(9200)
                withEnv("discovery.type", "single-node")
            }
        }
        es.start()

        val app = application {
            elasticSearch {
                hostAndPort = "localhost:${es.firstMappedPort}"
            }
        }
        with(app.run()) {
            val restClient = getBean<RestHighLevelClient>()
            Assertions.assertNotNull(restClient)

            val request: IndexRequest = IndexRequest("spring-data")
                    .source(singletonMap("feature", "high-level-rest-client"))

            val response = restClient.index(request, RequestOptions.DEFAULT)
            Assertions.assertEquals(201, response.status().status)
        }
        es.stop()
    }
}
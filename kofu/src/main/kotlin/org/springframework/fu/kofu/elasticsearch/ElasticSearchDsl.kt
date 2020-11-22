package org.springframework.fu.kofu.elasticsearch

import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticSearchDataInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

class ElasticSearchDsl(private val dsl: ElasticSearchDsl.() -> Unit): AbstractElasticSearchDsl() {

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        apply(dsl)
        ElasticSearchDataInitializer(createClientConfiguration()).initialize(context)
    }
}

fun ConfigurationDsl.elasticSearch(dsl: ElasticSearchDsl.() -> Unit) {
    ElasticSearchDsl(dsl).initialize(context)
}
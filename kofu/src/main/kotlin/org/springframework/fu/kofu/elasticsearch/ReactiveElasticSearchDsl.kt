package org.springframework.fu.kofu.elasticsearch

import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticSearchDataInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.ConfigurationDsl

class ReactiveElasticSearchDsl(private val dsl: ReactiveElasticSearchDsl.() -> Unit): AbstractElasticSearchDsl() {
    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        apply(dsl)
        ReactiveElasticSearchDataInitializer(createClientConfiguration()).initialize(context)
    }
}

fun ConfigurationDsl.reactiveElasticSearch(dsl: ReactiveElasticSearchDsl.() -> Unit) {
    ReactiveElasticSearchDsl(dsl).initialize(context)
}
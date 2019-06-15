package org.springframework.fu.kofu.graphql

import org.springframework.boot.autoconfigure.graphql.GraphqlInitializer
import org.springframework.boot.autoconfigure.graphql.properties.GraphqlProperties
import org.springframework.boot.autoconfigure.web.reactive.GraphqlReactiveInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.webflux.WebFluxServerDsl

class GraphqlReactiveDsl(private val init: GraphqlReactiveDsl.() -> Unit) : AbstractDsl() {

    private val properties = GraphqlProperties()

    var mapping: String
        get() = properties.mapping
        set(value) {
            properties.mapping = value
        }

    fun schema(init: GqlSchemaDsl.() -> Unit) {
        context.registerBean("gqlSchema") {
            val schemaDsl = GqlSchemaDsl(context)
            init(schemaDsl)
            schemaDsl.build()
        }
    }

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()
        GraphqlInitializer().initialize(context)
        GraphqlReactiveInitializer(properties).initialize(context)
    }
}

fun WebFluxServerDsl.graphql(dsl: GraphqlReactiveDsl.() -> Unit = {}) {
    GraphqlReactiveDsl(dsl).initialize(context)
}
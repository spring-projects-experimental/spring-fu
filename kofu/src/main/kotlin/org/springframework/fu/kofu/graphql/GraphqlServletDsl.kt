package org.springframework.fu.kofu.graphql

import org.springframework.boot.autoconfigure.graphql.GraphqlInitializer
import org.springframework.boot.autoconfigure.graphql.properties.GraphqlProperties
import org.springframework.boot.autoconfigure.web.servlet.GraphqlServletInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.webmvc.WebMvcServerDsl

class GraphqlServletDsl(private val init: GraphqlServletDsl.() -> Unit) : AbstractDsl() {

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
        GraphqlServletInitializer(properties).initialize(context)
    }
}

fun WebMvcServerDsl.graphql(dsl: GraphqlServletDsl.() -> Unit = {}) {
    GraphqlServletDsl(dsl).initialize(context)
}
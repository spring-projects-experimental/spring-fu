package org.springframework.fu.kofu.graphql.reactive

import org.springframework.boot.autoconfigure.graphql.websocket.WebsocketProperties
import org.springframework.boot.autoconfigure.graphql.websocket.reactive.WebsocketInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.graphql.GraphqlReactiveDsl

class GqlWebsocketDsl(private val init: GqlWebsocketDsl.() -> Unit) : AbstractDsl() {

    private val properties = WebsocketProperties()

    var mapping: String
        get() = properties.mapping
        set(value) {
            properties.mapping = value
        }

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        WebsocketInitializer(properties).initialize(context)
    }
}

fun GraphqlReactiveDsl.websocket(init: GqlWebsocketDsl.() -> Unit = {}) {
    GqlWebsocketDsl(init).initialize(context)
}
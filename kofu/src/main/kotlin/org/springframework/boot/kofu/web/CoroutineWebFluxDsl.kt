package org.springframework.boot.kofu.web

import org.springframework.boot.kofu.AbstractDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.web.function.client.CoroutinesWebClient
import org.springframework.fu.web.function.server.CoroutinesRouterFunctionDsl

class CoroutinesWebFluxClientDsl(private val clientModule: WebFluxClientDsl) : AbstractDsl() {
	override fun register(context: GenericApplicationContext) {
		context.registerBean {
			if (clientModule.baseUrl != null) {
				CoroutinesWebClient.create(clientModule.baseUrl)
			} else {
				CoroutinesWebClient.create()
			}
		}
	}
}

fun WebFluxClientDsl.coroutines()  {
	initializers.add(CoroutinesWebFluxClientDsl(this))
}

fun WebFluxServerDsl.coRouter(routes: (CoroutinesRouterFunctionDsl.() -> Unit)) {
	this.include { CoroutinesRouterFunctionDsl(routes).invoke() }
}


package org.springframework.boot.autoconfigure.web.reactive.coroutines

import org.springframework.boot.AbstractModule
import org.springframework.boot.autoconfigure.web.reactive.WebFluxClientModule
import org.springframework.boot.autoconfigure.web.reactive.WebFluxServerModule
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.web.function.client.CoroutinesWebClient
import org.springframework.fu.web.function.server.CoroutinesRouterFunctionDsl

class CoroutineWebFluxClientModule(private val clientModule: WebFluxClientModule) : AbstractModule() {
	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		context.registerBean {
			if (clientModule.baseUrl != null) {
				CoroutinesWebClient.create(clientModule.baseUrl)
			} else {
				CoroutinesWebClient.create()
			}
		}
		super.initialize(context)
	}
}

fun WebFluxClientModule.coroutines() : CoroutineWebFluxClientModule {
	val coroutinesModule = CoroutineWebFluxClientModule(this)
	initializers.add(coroutinesModule)
	return coroutinesModule
}

fun WebFluxServerModule.coRouter(routes: (CoroutinesRouterFunctionDsl.() -> Unit)) {
	this.include { CoroutinesRouterFunctionDsl(routes).invoke() }
}


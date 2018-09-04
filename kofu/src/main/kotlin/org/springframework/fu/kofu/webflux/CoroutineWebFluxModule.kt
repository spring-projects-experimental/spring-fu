package org.springframework.fu.kofu.webflux

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.kofu.AbstractModule
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


package org.springframework.fu.module.webflux.coroutine

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.webflux.WebFluxClientModule
import org.springframework.fu.module.webflux.WebFluxServerModule
import org.springframework.fu.module.webflux.coroutine.web.function.client.CoroutineWebClient
import org.springframework.fu.module.webflux.coroutine.web.function.server.CoroutineRouterFunctionDsl

class CoroutineWebFluxClientModule(private val clientModule: WebFluxClientModule) : AbstractModule() {
	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		context.registerBean {
			if (clientModule.baseUrl != null) {
				CoroutineWebClient.create(clientModule.baseUrl!!)
			} else {
				CoroutineWebClient.create()
			}
		}
		super.initialize(context)
	}
}

fun WebFluxClientModule.coroutine() : CoroutineWebFluxClientModule {
	val coroutinesModule = CoroutineWebFluxClientModule(this)
	initializers.add(coroutinesModule)
	return coroutinesModule
}

fun coRouter(routes: (CoroutineRouterFunctionDsl.() -> Unit)) =
		CoroutineRouterFunctionDsl(routes).invoke()

fun WebFluxServerModule.coRouter(routes: (CoroutineRouterFunctionDsl.() -> Unit)) {
	this.include { CoroutineRouterFunctionDsl(routes).invoke() }
}

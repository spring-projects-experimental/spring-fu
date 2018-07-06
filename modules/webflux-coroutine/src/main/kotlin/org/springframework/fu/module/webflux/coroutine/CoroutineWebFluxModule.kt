package org.springframework.fu.module.webflux.coroutine

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.fu.module.webflux.coroutine.web.function.client.CoroutineWebClient
import org.springframework.fu.module.webflux.coroutine.web.function.server.CoroutineRouterFunctionDsl

class CoroutineWebFluxClientModule(private val clientModule: WebFluxModule.WebFluxClientModule) : AbstractModule() {
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

fun WebFluxModule.WebFluxClientModule.coroutine() : CoroutineWebFluxClientModule {
	val coroutinesModule = CoroutineWebFluxClientModule(this)
	initializers.add(coroutinesModule)
	return coroutinesModule
}

fun coRouter(routes: (CoroutineRouterFunctionDsl.() -> Unit)) =
		CoroutineRouterFunctionDsl(routes)

fun WebFluxModule.WebFluxServerModule.coRouter(routes: (CoroutineRouterFunctionDsl.() -> Unit)) {
	this.router { CoroutineRouterFunctionDsl(routes) }
}
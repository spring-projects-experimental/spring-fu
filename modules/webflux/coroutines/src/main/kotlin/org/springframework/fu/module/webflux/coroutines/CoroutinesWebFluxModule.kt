package org.springframework.fu.module.webflux.coroutines

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.Module
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.fu.module.webflux.coroutines.web.function.client.CoroutineWebClient
import org.springframework.fu.module.webflux.coroutines.web.function.server.CoroutineRouterFunctionDsl

class CoroutineWebFluxClientModule(private val clientModule: WebFluxModule.WebFluxClientModule) : AbstractModule() {
	override fun initialize(context: GenericApplicationContext) {
		context.registerBean {
			if (clientModule.baseUrl != null) {
				CoroutineWebClient.create(clientModule.baseUrl!!)
			} else {
				CoroutineWebClient.create()
			}
		}
	}
}

fun WebFluxModule.WebFluxClientModule.coroutines() : CoroutineWebFluxClientModule {
	val coroutinesModule = CoroutineWebFluxClientModule(this)
	initializers.add(coroutinesModule)
	return coroutinesModule
}

open class CoroutineWebFluxRoutesModule(private val init: (CoroutineWebFluxRoutesModule.() -> Unit)) : CoroutineRouterFunctionDsl({}), Module {

	override lateinit var context: GenericApplicationContext

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		context.registerBean {
			init()
			invoke()
		}
	}

}

fun routes(routes: CoroutineWebFluxRoutesModule.() -> Unit) = CoroutineWebFluxRoutesModule(routes)

fun WebFluxModule.WebFluxServerModule.routes(routesModule: CoroutineWebFluxRoutesModule) =
		initializers.add(routesModule)

fun WebFluxModule.WebFluxServerModule.routes(routes: CoroutineWebFluxRoutesModule.() -> Unit) =
		initializers.add(CoroutineWebFluxRoutesModule(routes))
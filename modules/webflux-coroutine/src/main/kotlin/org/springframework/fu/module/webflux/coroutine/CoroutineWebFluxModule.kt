package org.springframework.fu.module.webflux.coroutine

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.Module
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.fu.module.webflux.coroutine.web.function.client.CoroutineWebClient
import org.springframework.fu.module.webflux.coroutine.web.function.server.CoroutineRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction

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

class CoroutineWebFluxRoutesModule(private val init: (CoroutineWebFluxRoutesModule.() -> Unit)) : CoroutineRouterFunctionDsl({}), Module {

	companion object {
		var count = 0
	}

	override lateinit var context: GenericApplicationContext

	override fun initialize(context: GenericApplicationContext) {
		this.context = context
		context.registerBean("${RouterFunction::class.qualifiedName}${count++}") {
			init()
			invoke()
		}
	}

}

fun coroutineRoutes(routes: CoroutineWebFluxRoutesModule.() -> Unit) =
		CoroutineWebFluxRoutesModule(routes)

fun WebFluxModule.WebFluxServerModule.coroutineRoutes(ref: (() -> CoroutineWebFluxRoutesModule)? = null, routes: (CoroutineWebFluxRoutesModule.() -> Unit)? = null) {
	if (routes == null && ref == null)
		throw IllegalArgumentException("No routes provided")

	routes?.let { initializers.add(CoroutineWebFluxRoutesModule(it)) }
	ref?.let { initializers.add(it.invoke()) }
}
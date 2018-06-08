package org.springframework.fu.module.webflux.coroutines

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.Module
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.fu.module.webflux.coroutines.web.function.client.CoroutineWebClient
import org.springframework.fu.module.webflux.coroutines.web.function.server.CoroutineRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction

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

fun routes(routes: CoroutineWebFluxRoutesModule.() -> Unit) = CoroutineWebFluxRoutesModule(routes)

fun WebFluxModule.WebFluxServerModule.routes(routes: (CoroutineWebFluxRoutesModule.() -> Unit)? = null, import: (() -> CoroutineWebFluxRoutesModule)? = null) {
	if (routes == null && import == null)
		throw IllegalArgumentException("No routes provided")

	routes?.let { initializers.add(CoroutineWebFluxRoutesModule(it)) }
	import?.let { initializers.add(it.invoke()) }
}
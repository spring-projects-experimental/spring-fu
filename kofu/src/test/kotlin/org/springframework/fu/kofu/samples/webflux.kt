@file:Suppress("UNUSED_PARAMETER")

package org.springframework.fu.kofu.samples

import org.springframework.boot.logging.LogLevel
import org.springframework.context.event.ContextStartedEvent
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.mongo.reactiveMongodb
import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.fu.kofu.webflux.cors
import org.springframework.fu.kofu.templating.mustache
import org.springframework.fu.kofu.webflux.webClient
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

private fun webFluxRouter() {
	reactiveWebApplication {
		webFlux {
			router {
				val htmlHandler = ref<ReactiveHtmlHandler>()
				val apiHandler = ref<ReactiveApiHandler>()
				GET("/", htmlHandler::blog)
				GET("/article/{id}", htmlHandler::article)
				"/api".nest {
					GET("/", apiHandler::list)
					POST("/", apiHandler::create)
					PUT("/{id}", apiHandler::update)
					DELETE("/{id}", apiHandler::delete)
				}
			}
		}
	}
}

private fun webFluxIncludeRouter() {
	fun routes(htmlHandler: ReactiveHtmlHandler, apiHandler: ReactiveApiHandler) = router {
		GET("/", htmlHandler::blog)
		GET("/article/{id}", htmlHandler::article)
		"/api".nest {
			GET("/", apiHandler::list)
			POST("/", apiHandler::create)
			PUT("/{id}", apiHandler::update)
			DELETE("/{id}", apiHandler::delete)
		}
	}
	reactiveWebApplication {
		beans {
			bean(::routes)
		}
	}
}

private fun webFluxCustomEngine() {
	fun routes(htmlHandler: ReactiveHtmlHandler, apiHandler: ReactiveApiHandler) = router {
		GET("/", htmlHandler::blog)
		GET("/article/{id}", htmlHandler::article)
		"/api".nest {
			GET("/", apiHandler::list)
			POST("/", apiHandler::create)
			PUT("/{id}", apiHandler::update)
			DELETE("/{id}", apiHandler::delete)
		}
	}
	reactiveWebApplication {
		beans {
			bean(::routes)
		}
		webFlux {
			engine = jetty()
		}
	}
}

private fun webFluxCoRouter() {
	reactiveWebApplication {
		beans {
			bean<CoHtmlHandler>()
			bean<CoApiHandler>()
		}
		webFlux {
			coRouter {
				val htmlHandler = ref<CoHtmlHandler>()
				val apiHandler = ref<CoApiHandler>()
				GET("/", htmlHandler::blog)
				GET("/article/{id}", htmlHandler::article)
				"/api".nest {
					GET("/", apiHandler::list)
					POST("/", apiHandler::create)
					PUT("/{id}", apiHandler::update)
					DELETE("/{id}", apiHandler::delete)
				}
			}
		}
	}
}

private fun webFluxIncludeCoRouter() {
	fun routes(htmlHandler: CoHtmlHandler, apiHandler: CoApiHandler) = coRouter {
		GET("/", htmlHandler::blog)
		GET("/article/{id}", htmlHandler::article)
		"/api".nest {
			GET("/", apiHandler::list)
			POST("/", apiHandler::create)
			PUT("/{id}", apiHandler::update)
			DELETE("/{id}", apiHandler::delete)
		}
	}
	reactiveWebApplication {
		beans {
			bean<CoHtmlHandler>()
			bean<CoApiHandler>()
			bean(::routes)
		}
		webFlux()
	}
}

private fun webFluxApplicationDsl() {
	fun routes(htmlHandler: CoHtmlHandler, apiHandler: CoApiHandler) = coRouter {
		GET("/", htmlHandler::blog)
		GET("/article/{id}", htmlHandler::article)
		"/api".nest {
			GET("/", apiHandler::list)
			POST("/", apiHandler::create)
			PUT("/{id}", apiHandler::update)
			DELETE("/{id}", apiHandler::delete)
		}
	}

	val dataConfiguration = configuration {
		beans {
			bean<UserRepository>()
			bean<ArticleRepository>()
		}
		reactiveMongodb {
			uri = "mongodb://myserver.com/foo"
		}
		listener<ContextStartedEvent> {
			ref<UserRepository>().init()
			ref<ArticleRepository>().init()
		}
	}

	val webConfiguration = configuration {
		beans {
			bean<ReactiveHtmlHandler>()
			bean<ReactiveApiHandler>()
			bean(::routes)
		}
		webFlux {
			port = if (profiles.contains("test")) 8181 else 8080
			cors {
				"example.com" { }
			}
			mustache()
			codecs {
				string()
				jackson()
			}
		}
		webClient {
			codecs {
				string()
				jackson()
			}
		}
	}

	val app = reactiveWebApplication {
		logging {
			level = LogLevel.INFO
			level("org.springframework", LogLevel.DEBUG)
		}
		configurationProperties<CityProperties>(prefix = "city")
		enable(dataConfiguration)
		enable(webConfiguration)
	}

	fun main() = app.run(profiles = "data, webflux")
}


private class ReactiveHtmlHandler(private val userRepository: UserRepository,
				  private val articleRepository: ArticleRepository) {

	fun blog(request: ServerRequest) = ServerResponse.ok().build()
	fun article(request: ServerRequest) = ServerResponse.ok().build()
}

interface ReactiveApiHandler {
	fun list(request: ServerRequest): Mono<ServerResponse>
	fun create(request: ServerRequest): Mono<ServerResponse>
	fun update(request: ServerRequest): Mono<ServerResponse>
	fun delete(request: ServerRequest): Mono<ServerResponse>
}

class CoHtmlHandler(
		private val userRepository: UserRepository,
		private val articleRepository: ArticleRepository) {

	suspend fun blog(request: ServerRequest) = ServerResponse.ok().buildAndAwait()
	suspend fun article(request: ServerRequest) = ServerResponse.ok().buildAndAwait()

}

class CoApiHandler {
	suspend fun list(request: ServerRequest) = ServerResponse.ok().buildAndAwait()
	suspend fun create(request: ServerRequest) = ServerResponse.ok().buildAndAwait()
	suspend fun update(request: ServerRequest) = ServerResponse.ok().buildAndAwait()
	suspend fun delete(request: ServerRequest) = ServerResponse.ok().buildAndAwait()

}

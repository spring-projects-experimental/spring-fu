/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UNUSED_PARAMETER")

package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.boot.logging.LogLevel
import org.springframework.context.event.ContextStartedEvent
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.mongo.reactiveMongodb
import org.springframework.fu.kofu.webflux.webClient
import org.springframework.fu.kofu.webflux.cors
import org.springframework.fu.kofu.webflux.mustache
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

private fun applicationDsl() {
	val app = application(WebApplicationType.NONE) {
		beans {
			bean<Foo>()
		}
		configurationProperties<City>("city")
	}

	fun main(args: Array<String>) = app.run()
}

private fun applicationDslWithConfiguration() {
	val conf = configuration {
		beans {
			bean<Foo>()
		}
	}
	val app = application(WebApplicationType.NONE) {
		enable(conf)
		configurationProperties<City>("city")
	}

	fun main(args: Array<String>) = app.run()
}

private fun webFluxApplicationDsl() {
	fun routes(htmlHandler: HtmlHandler, apiHandler: ApiHandler) = router {
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
			bean<HtmlHandler>()
			bean<ApiHandler>()
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

	val app = application(WebApplicationType.REACTIVE) {
		logging {
			level = LogLevel.INFO
			level("org.springframework", LogLevel.DEBUG)
		}
		configurationProperties<City>("city")
		enable(dataConfiguration)
		enable(webConfiguration)
	}

	fun main(args: Array<String>) = app.run(profiles = "data, webflux")
}

class Foo

class City(val name: String, val country: String)

interface UserRepository {
	fun init()
}
interface ArticleRepository {
	fun init()
}
class HtmlHandler(private val userRepository: UserRepository,
				  private val articleRepository: ArticleRepository) {

	fun blog(request: ServerRequest) = ServerResponse.ok().build()
	fun article(request: ServerRequest) = ServerResponse.ok().build()
}

interface ApiHandler {
	fun list(request: ServerRequest): Mono<ServerResponse>
	fun create(request: ServerRequest): Mono<ServerResponse>
	fun update(request: ServerRequest): Mono<ServerResponse>
	fun delete(request: ServerRequest): Mono<ServerResponse>
}

class HtmlCoroutinesHandler(
		private val userRepository: UserRepository,
		private val articleRepository: ArticleRepository) {

	suspend fun blog(request: ServerRequest) = ok().buildAndAwait()
	suspend fun article(request: ServerRequest) = ok().buildAndAwait()

}

class ApiCoroutinesHandler {
	suspend fun list(request: ServerRequest) = ok().buildAndAwait()
	suspend fun create(request: ServerRequest) = ok().buildAndAwait()
	suspend fun update(request: ServerRequest) = ok().buildAndAwait()
	suspend fun delete(request: ServerRequest) = ok().buildAndAwait()

}

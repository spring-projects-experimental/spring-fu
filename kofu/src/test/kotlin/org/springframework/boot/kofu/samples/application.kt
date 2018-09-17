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

package org.springframework.boot.kofu.samples

import org.springframework.web.function.server.coHandler
import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.mongo.mongodb
import org.springframework.boot.kofu.ref
import org.springframework.boot.kofu.web.*
import org.springframework.boot.logging.LogLevel
import org.springframework.context.event.ContextStartedEvent
import org.springframework.web.function.server.CoroutinesServerRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

private fun applicationDslWithCustomBeanApplication() {
	// ============================================================================================
	// This standalone application registers a custom bean `Foo` and a `City` properties properties
	// ============================================================================================
	val app = application(startServer = false) {
		beans {
			bean<Foo>()
		}
		properties<City>("city")
	}

	fun main(args: Array<String>) = app.run()
}

private fun applicationDslOverview() {
	// ============================================================================================
	// Overview of a more complete web application
	// ============================================================================================
	val app = application {
		logging {
			level(LogLevel.INFO)
			level("org.springframework", LogLevel.DEBUG)
		}
		beans {
			bean<UserRepository>()
			bean<ArticleRepository>()
			bean<HtmlHandler>()
			bean<ApiHandler>()
		}
		properties<City>("city")
		profile("data") {
			beans {
				bean<UserRepository>()
				bean<ArticleRepository>()
			}
			mongodb(uri = "mongodb://myserver.com/foo")
			listener<ContextStartedEvent> {
				ref<UserRepository>().init()
				ref<ArticleRepository>().init()
			}
		}
		profile("web") {
			beans {
				bean<HtmlHandler>()
				bean<ApiHandler>()
			}
			val port = if (profiles.contains("test")) 8181 else 8080
			server(netty(port)) {
				cors {
					"example.com" { }
				}
				mustache()
				codecs {
					string()
					jackson()
				}
				router {
					val htmlHandler = ref<HtmlHandler>()
					val apiHandler = ref<ApiHandler>()
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
			client {
				codecs {
					string()
					jackson()
				}
			}

		}
	}
	fun main(args: Array<String>) = app.run(profiles = "data, web")
}

class Foo

// Switch to data classes when https://github.com/spring-projects/spring-boot/issues/8762 will be fixed
class City {
	lateinit var name: String
	lateinit var country: String
}

interface UserRepository {
	fun init()
}
interface ArticleRepository {
	fun init()
}
class HtmlHandler(private val userRepository: UserRepository,
				  private val articleRepository: ArticleRepository) {

	fun blog(request: ServerRequest) = ServerResponse.ok().build().toMono()
	fun article(request: ServerRequest) = ServerResponse.ok().build().toMono()
}

interface ApiHandler {
	fun list(request: ServerRequest): Mono<ServerResponse>
	fun create(request: ServerRequest): Mono<ServerResponse>
	fun update(request: ServerRequest): Mono<ServerResponse>
	fun delete(request: ServerRequest): Mono<ServerResponse>
}

class HtmlCoroutinesHandler(private val userRepository: UserRepository,
				  private val articleRepository: ArticleRepository) {
	suspend fun blog(request: CoroutinesServerRequest) = coHandler {
		ok().build()
	}
	suspend fun article(request: CoroutinesServerRequest) = coHandler {
		ok().build()
	}
}

class ApiCoroutinesHandler {
	suspend fun list(request: CoroutinesServerRequest) = coHandler {
		ok().build()
	}
	suspend fun create(request: CoroutinesServerRequest) = coHandler {
		ok().build()
	}
	suspend fun update(request: CoroutinesServerRequest) = coHandler {
		ok().build()
	}
	suspend fun delete(request: CoroutinesServerRequest) = coHandler {
		ok().build()
	}
}

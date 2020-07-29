@file:Suppress("UNUSED_PARAMETER")

package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

private fun webMvcRouter() {
	webApplication {
		webMvc {
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
	}
}

private fun webmvcIncludeRouter() {
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
	webApplication {
		beans {
			bean(::routes)
		}
	}
}

class HtmlHandler(private val userRepository: UserRepository,
				  private val articleRepository: ArticleRepository) {

	fun blog(request: ServerRequest) = ServerResponse.ok().build()
	fun article(request: ServerRequest) = ServerResponse.ok().build()
}

interface ApiHandler {
	fun list(request: ServerRequest): ServerResponse
	fun create(request: ServerRequest): ServerResponse
	fun update(request: ServerRequest): ServerResponse
	fun delete(request: ServerRequest): ServerResponse
}
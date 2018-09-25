package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.ref
import org.springframework.boot.kofu.web.server
import org.springframework.web.reactive.function.server.router

private fun routerDsl() {
	application {
		beans {
			bean<HtmlHandler>()
			bean<ApiHandler>()
		}
		server {
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

private fun importRouter() {
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
	application {
		beans {
			bean {
				routes(ref(), ref())
			}
		}
		server()
	}
}

private fun coRouterDsl() {
	application {
		beans {
			bean<HtmlCoroutinesHandler>()
			bean<ApiCoroutinesHandler>()
		}
		server {
			coRouter {
				val htmlHandler = ref<HtmlCoroutinesHandler>()
				val apiHandler = ref<ApiCoroutinesHandler>()
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

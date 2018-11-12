package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.server
import org.springframework.web.function.server.coRouter
import org.springframework.web.reactive.function.server.router

private fun router() {
	application {
		server {
			val htmlHandler = ref<HtmlHandler>()
			val apiHandler = ref<ApiHandler>()
			router {
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
		server {
			import(::routes)
		}
	}
}

private fun customEngine() {
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
		server {
			engine = jetty()
			import(::routes)
		}
	}
}

private fun coRouter() {
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

private fun importCoRouter() {
	fun routes(htmlHandler: HtmlCoroutinesHandler, apiHandler: ApiCoroutinesHandler) = coRouter {
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
			bean<HtmlCoroutinesHandler>()
			bean<ApiCoroutinesHandler>()
		}
		server {
			import(::routes)
		}
	}
}

package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.context.support.beans
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.router

private fun router() {
	application(WebApplicationType.REACTIVE) {
		webFlux {
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

private fun includeRouter() {
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
	application(WebApplicationType.REACTIVE) {
		beans {
			bean(::routes)
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
	application(WebApplicationType.REACTIVE) {
		beans {
			bean(::routes)
		}
		webFlux {
			engine = jetty()
		}
	}
}

private fun coRouter() {
	application(WebApplicationType.REACTIVE) {
		beans {
			bean<HtmlCoroutinesHandler>()
			bean<ApiCoroutinesHandler>()
		}
		webFlux {
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

private fun includeCoRouter() {
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
	application(WebApplicationType.NONE) {
		beans {
			bean<HtmlCoroutinesHandler>()
			bean<ApiCoroutinesHandler>()
			bean(::routes)
		}
		webFlux()
	}
}

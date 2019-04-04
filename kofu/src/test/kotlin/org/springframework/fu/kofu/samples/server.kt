package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.router

private fun router() {
	reactiveWebApplication {
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
	reactiveWebApplication {
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
	reactiveWebApplication {
		beans {
			bean(::routes)
		}
		webFlux {
			engine = jetty()
		}
	}
}

private fun coRouter() {
	reactiveWebApplication {
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
	reactiveWebApplication {
		beans {
			bean<HtmlCoroutinesHandler>()
			bean<ApiCoroutinesHandler>()
			bean(::routes)
		}
		webFlux()
	}
}

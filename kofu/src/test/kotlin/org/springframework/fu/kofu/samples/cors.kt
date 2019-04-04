package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.webflux.cors
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.fu.kofu.reactiveWebApplication

fun corsDsl() {
	reactiveWebApplication {
		webFlux {
			cors {
				"/api/**" {
					allowedOrigins = "first.example.com, second.example.com"
					allowedMethods = "GET, PUT, POST, DELETE"
				}
				"/static/**" {
					allowedOrigins = "full.config.example.com"
					allowedMethods = "GET"
					allowedHeaders = "*"
					exposedHeaders = "Content-Location"
					allowCredentials = true
					maxAge = 3600
				}
				"/public/**"() // Enable CORS with permit default values
			}
		}
	}
}
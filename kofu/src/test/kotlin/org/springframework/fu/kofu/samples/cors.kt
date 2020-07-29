package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.cors
import org.springframework.fu.kofu.webflux.webFlux

fun corsDsl() {
	application {
		webFlux {
			cors {
				"/api/**" {
					allowedOrigins = listOf("first.example.com", "second.example.com")
					allowedMethods = listOf("GET", "PUT", "POST", "DELETE")
				}
				"/static/**" {
					allowedOrigins = listOf("full.config.example.com")
					allowedMethods = listOf("GET")
					allowedHeaders = listOf("*")
					exposedHeaders = listOf("Content-Location")
					allowCredentials = true
					maxAge = 3600
				}
				path("/public/**") // Enable CORS with permit default values
			}
		}
	}
}
package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.web.cors
import org.springframework.fu.kofu.web.server
import org.springframework.fu.kofu.webApplication

fun corsDsl() {
	webApplication {
		server {
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
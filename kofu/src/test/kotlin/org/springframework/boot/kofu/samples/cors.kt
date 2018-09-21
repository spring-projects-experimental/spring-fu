package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.cors
import org.springframework.boot.kofu.web.server

fun corsDsl() {
	application {
		server {
			cors {
				"/api/**" {
					allowedOrigins("first.example.com", "second.example.com")
					allowedMethods("GET", "PUT", "POST", "DELETE")
				}
				"/static/**" {
					allowedOrigins("full.config.example.com")
					allowedMethods("GET")
					allowedHeaders("*")
					exposedHeaders("Content-Location")
					allowCredentials = true
					maxAge = 3600
				}
				"/public/**"() // Enable CORS with permit default values
			}
		}
	}
}
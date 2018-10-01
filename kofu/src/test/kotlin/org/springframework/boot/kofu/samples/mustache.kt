package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.mustache
import org.springframework.boot.kofu.web.server

fun mustacheDsl() {
	application {
		server {
			mustache()
		}
	}
}
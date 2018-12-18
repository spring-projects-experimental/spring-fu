package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.web.mustache
import org.springframework.fu.kofu.web.server
import org.springframework.fu.kofu.webApplication

fun mustacheDsl() {
	webApplication {
		server {
			mustache()
		}
	}
}
package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.mustache
import org.springframework.fu.kofu.web.server

fun mustacheDsl() {
	application {
		server {
			mustache()
		}
	}
}
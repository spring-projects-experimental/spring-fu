package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.client
import org.springframework.boot.kofu.web.jackson
import org.springframework.boot.kofu.web.server

fun jacksonDsl() {
	application {
		server {
			codecs {
				jackson()
			}
		}
		client {
			codecs {
				jackson()
			}
		}
	}
}
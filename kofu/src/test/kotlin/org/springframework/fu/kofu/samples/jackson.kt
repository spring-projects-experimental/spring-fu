package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.client
import org.springframework.fu.kofu.web.jackson
import org.springframework.fu.kofu.web.server

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
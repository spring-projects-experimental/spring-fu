package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.web.client
import org.springframework.fu.kofu.web.server
import org.springframework.fu.kofu.webApplication

fun jacksonDsl() {
	webApplication {
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
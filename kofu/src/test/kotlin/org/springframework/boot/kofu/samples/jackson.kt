package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.client
import org.springframework.boot.kofu.web.netty
import org.springframework.boot.kofu.web.server

fun jackson() {
	application {
		server(netty()) {
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
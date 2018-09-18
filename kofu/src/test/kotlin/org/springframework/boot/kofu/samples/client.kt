package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.web.client
import org.springframework.boot.kofu.web.coroutines
import org.springframework.boot.kofu.web.jackson

fun clientDsl() {
	application {
		client(name = "client1") {
			codecs {
				string()
				jackson()
			}
		}
		client(name = "client2", baseUrl = "http://example.com")
	}
}

fun clientCoroutines() {
	application {
		client(name = "client1") {
			coroutines()
			codecs {
				string()
				jackson()
			}
		}
		client(name = "client2", baseUrl = "http://example.com") {
			coroutines()
		}
	}
}
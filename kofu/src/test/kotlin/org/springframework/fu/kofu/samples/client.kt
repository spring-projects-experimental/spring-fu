package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.webClient

fun clientDsl() {
	application(WebApplicationType.NONE) {
		webClient {
			codecs {
				string()
				jackson()
				resource()
				protobuf()
				form()
				multipart()
			}
		}
	}
}

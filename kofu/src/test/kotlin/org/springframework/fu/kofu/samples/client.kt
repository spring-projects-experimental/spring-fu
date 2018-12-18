package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.web.client
import org.springframework.fu.kofu.web.jackson

fun clientDsl() {
	application {
		client {
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

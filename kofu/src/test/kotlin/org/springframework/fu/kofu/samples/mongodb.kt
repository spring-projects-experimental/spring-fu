package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongo.reactiveMongodb

fun mongo() {
	application(WebApplicationType.NONE) {
		reactiveMongodb {
			uri = "mongodb://myserver.com/foo"
		}
	}
}

fun mongoEmbedded() {
	application(WebApplicationType.NONE) {
		reactiveMongodb {
			uri = "mongodb://myserver.com/foo"
			embedded()
		}
	}
}

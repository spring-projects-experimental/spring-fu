package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongo.reactiveMongodb

fun mongo() {
	application {
		reactiveMongodb {
			uri = "mongodb://myserver.com/foo"
		}
	}
}

fun mongoEmbedded() {
	application {
		reactiveMongodb {
			uri = "mongodb://myserver.com/foo"
			embedded()
		}
	}
}

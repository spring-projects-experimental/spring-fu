package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongo.mongodb

fun mongo() {
	application {
		mongodb {
			uri = "mongodb://myserver.com/foo"
		}
	}
}

fun mongoEmbedded() {
	application {
		mongodb {
			uri = "mongodb://myserver.com/foo"
			embedded()
		}
	}
}

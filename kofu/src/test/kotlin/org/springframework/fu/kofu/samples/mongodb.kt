package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongo.embedded
import org.springframework.fu.kofu.mongo.mongodb

fun mongo() {
	application(startServer = false) {
		mongodb {
			uri = "mongodb://myserver.com/foo"
		}
	}
}

fun mongoEmbedded() {
	application(startServer = false) {
		mongodb {
			uri = "mongodb://myserver.com/foo"
			embedded()
		}
	}
}

fun mongoCoroutines() {
	application(startServer = false) {
		mongodb {
			uri = "mongodb://myserver.com/foo"
			coroutines = true
		}
	}
}
package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.mongo.embedded
import org.springframework.boot.kofu.mongo.mongodb

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
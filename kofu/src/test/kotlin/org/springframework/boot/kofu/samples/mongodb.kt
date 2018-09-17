package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.mongo.coroutines
import org.springframework.boot.kofu.mongo.embedded
import org.springframework.boot.kofu.mongo.mongodb

fun mongo() {
	application {
		mongodb("mongodb://myserver.com/foo")
	}
}

fun mongoEmbedded() {
	application {
		mongodb("mongodb://myserver.com/foo") {
			embedded()
		}
	}
}

fun mongoCoroutines() {
	application {
		mongodb("mongodb://myserver.com/foo") {
			coroutines()
		}
	}
}
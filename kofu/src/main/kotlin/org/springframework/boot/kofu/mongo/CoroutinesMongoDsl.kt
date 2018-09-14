package org.springframework.boot.kofu.mongo

import org.springframework.boot.autoconfigure.mongo.coroutines.CoroutinesMongoInitializer

fun MongoDsl.coroutines() {
	initializers.add(CoroutinesMongoInitializer())
}
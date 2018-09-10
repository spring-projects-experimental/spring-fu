/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.mongo.embedded

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.springframework.boot.AbstractModule
import org.springframework.boot.autoconfigure.mongo.MongoModule
import org.springframework.context.support.GenericApplicationContext
import java.net.URI
import java.net.URISyntaxException

class EmbeddedMongoModule(
		private val connectionString: String,
		private val init: EmbeddedMongoModule.() -> Unit
) : AbstractModule() {

	private var version: Version.Main = Version.Main.PRODUCTION

	override fun initialize(context: GenericApplicationContext) {
		val connectionUri = try {
			URI(connectionString)
		} catch (e: URISyntaxException) {
			return
		}
		val bindIp = connectionUri.host
		val port = connectionUri.port.takeIf { it != -1 } ?: 27017

		init()

		val config = MongodConfigBuilder()
				.version(version)
				.net(Net(bindIp, port, Network.localhostIsIPv6()))
				.build()
		val runtime = MongodStarter.getDefaultInstance()
		val executable = runtime.prepare(config)
		executable.start()
	}

	fun developmentVersion() {
		version(Version.Main.DEVELOPMENT)
	}

	fun version(version: Version.Main) {
		this.version = version
	}
}

fun MongoModule.embedded(init: EmbeddedMongoModule.() -> Unit = {}): EmbeddedMongoModule {
	val embeddedMongoModule = EmbeddedMongoModule(connectionString, init)
	initializers.add(embeddedMongoModule)
	return embeddedMongoModule
}

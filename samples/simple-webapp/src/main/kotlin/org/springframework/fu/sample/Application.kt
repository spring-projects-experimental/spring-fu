/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.fu.sample

import kotlinx.coroutines.experimental.runBlocking
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.fu.application
import org.springframework.fu.module.data.mongodb.coroutines.coroutines
import org.springframework.fu.module.data.mongodb.mongodb
import org.springframework.fu.module.jackson.jackson
import org.springframework.fu.module.logging.*
import org.springframework.fu.module.logging.LogLevel.*
import org.springframework.fu.module.mustache.mustache
import org.springframework.fu.module.webflux.coroutines.routes
import org.springframework.fu.module.webflux.netty.netty
import org.springframework.fu.module.webflux.webflux
import java.io.File

val app = application {
	bean<UserRepository>()
	bean<UserHandler>()
	logging {
		level(INFO)
		level("org.springframework", DEBUG)
		level<DefaultListableBeanFactory>(WARN)

		logback {
			debug(true)
			consoleAppender()
			rollingFileAppender(File("/tmp/log.txt"))
		}
	}
	webflux {
		server(netty()) {
			mustache()
			codecs {
				jackson()
			}
			routes(routes)
		}
	}
	configuration(configuration)
	mongodb {
		coroutines()
	}
	if (env.activeProfiles.any { it.startsWith("foo") }) {

	}
}

data class SampleConfiguration(val property: String)

fun main(args: Array<String>) {
	app.run(await = true) {
		runBlocking {
			it.getBean<UserRepository>().init()
		}
	}
}
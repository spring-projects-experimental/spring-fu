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

@file:Suppress("UNUSED_PARAMETER")

package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.boot.logging.LogLevel
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.configuration

private fun applicationDsl() {
	val app = application(WebApplicationType.NONE) {
		logging {
			level = LogLevel.WARN
		}
		beans {
			bean<Foo>()
		}
		configurationProperties<City>("city")
	}

	fun main(args: Array<String>) = app.run()
}

private fun applicationDslWithConfiguration() {
	val conf = configuration {
		beans {
			bean<Foo>()
		}
	}
	val app = application(WebApplicationType.NONE) {
		logging {
			level = LogLevel.WARN
		}
		configurationProperties<City>("city")
		enable(conf)
	}

	fun main(args: Array<String>) = app.run()
}
class Foo

class City(val name: String, val country: String)

interface UserRepository {
	fun init()
}

interface ArticleRepository {
	fun init()
}
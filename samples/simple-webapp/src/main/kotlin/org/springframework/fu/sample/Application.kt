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

import org.springframework.http.HttpHeaders.*
import org.springframework.http.MediaType.*
import org.springframework.fu.application
import org.springframework.fu.configuration
import org.springframework.fu.get
import org.springframework.fu.module.data.mongodb.mongodb
import org.springframework.fu.module.jackson.jackson
import org.springframework.fu.module.mustache.mustache
import org.springframework.fu.module.webflux.Server
import org.springframework.fu.module.webflux.webflux
import org.springframework.web.reactive.function.server.ServerResponse.*

val app = application {
	webflux {
		server(Server.NETTY) {
			mustache()
			codecs {
				jackson()
			}
			routes {
				GET("/") {
					ok().syncBody("Hello")
				}
				GET("/user") {
					ok().header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE).syncBody(User("Brian"))
				}
				GET("/view") {
					ok().render("template", mapOf("name" to "world"))
				}
			}
		}
	}
	configuration { env ->
		SampleConfiguration(property = env["ENV_VARIABLE"] ?: "debugConf")
	}
	mongodb()
}

data class User(val name: String)

data class SampleConfiguration(val property: String)

fun main(args: Array<String>) {
	app.run(await = true)
}
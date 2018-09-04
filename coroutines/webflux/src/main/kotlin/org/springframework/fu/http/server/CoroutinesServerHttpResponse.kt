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

package org.springframework.fu.http.server

import org.springframework.fu.http.CoroutinesHttpOutputMessage
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse

interface CoroutinesServerHttpResponse : CoroutinesHttpOutputMessage {

	var statusCode: HttpStatus

	companion object {
		operator fun invoke(response: ServerHttpResponse): CoroutinesServerHttpResponse =
				DefaultCoroutineServerHttpResponse(response)
	}
}

open class DefaultCoroutineServerHttpResponse(val response: ServerHttpResponse) : CoroutinesServerHttpResponse {
	override var statusCode: HttpStatus
		get() = response.statusCode!!
		set(value) {
			response.statusCode = value
		}

	override fun getHeaders(): HttpHeaders = response.headers
}


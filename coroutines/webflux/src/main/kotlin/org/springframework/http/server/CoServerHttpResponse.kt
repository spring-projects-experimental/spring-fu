/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.http.server

import org.springframework.http.CoHttpOutputMessage
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse

interface CoServerHttpResponse : CoHttpOutputMessage {

	var statusCode: HttpStatus

	companion object {
		operator fun invoke(response: ServerHttpResponse): CoServerHttpResponse =
				DefaultCoServerHttpResponse(response)
	}
}

open class DefaultCoServerHttpResponse(val response: ServerHttpResponse) : CoServerHttpResponse {
	override var statusCode: HttpStatus
		get() = response.statusCode!!
		set(value) {
			response.statusCode = value
		}

	override fun getHeaders(): HttpHeaders = response.headers
}


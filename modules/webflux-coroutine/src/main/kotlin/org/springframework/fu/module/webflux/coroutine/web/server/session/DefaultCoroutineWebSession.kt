/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.fu.module.webflux.coroutine.web.server.session

import kotlinx.coroutines.experimental.reactive.awaitFirstOrDefault
import org.springframework.fu.module.webflux.coroutine.web.server.CoroutineWebSession
import org.springframework.web.server.WebSession

class DefaultCoroutineWebSession(val session: WebSession) : CoroutineWebSession {
	override val attributes: MutableMap<String, Any?>
		get() = session.attributes

	override fun <T> getAttribute(name: String): T? = session.getAttribute<T>(name)

	suspend override fun save(): Unit {
		session.save().awaitFirstOrDefault(null)
	}
}

internal fun WebSession.asCoroutineWebSession() = DefaultCoroutineWebSession(this)

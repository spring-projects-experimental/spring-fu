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

package org.springframework.fu.kofu.mongo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findById
import org.springframework.fu.kofu.application
import org.springframework.util.SocketUtils
import java.time.Duration

class EmbeddedMongoModuleTests {

	@Test
	fun `enable mongodb embedded module`() {
		val port = SocketUtils.findAvailableTcpPort()
		val app = application {
			beans {
				bean<TestRepository>()
			}
			reactiveMongodb {
				uri = "mongodb://localhost:$port/test"
				embedded()
			}
		}
		with(app.run()){
			val repository = getBean<TestRepository>()
			repository.save(TestUser("1", "foo")).block(Duration.ofSeconds(3))
			assertEquals("foo", repository.findById("1").block(Duration.ofSeconds(3))?.name)
			close()
		}
	}
}

class TestRepository(
		private val template: ReactiveMongoTemplate
) {
	fun findById(id: String) = template.findById<TestUser>(id)
	fun save(user: TestUser) = template.save(user)
}

data class TestUser(
		@Id val id: String,
		val name: String
)

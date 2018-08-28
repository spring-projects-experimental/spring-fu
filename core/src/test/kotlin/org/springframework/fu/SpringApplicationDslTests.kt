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

package org.springframework.fu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.env.get

/**
 * @author Sebastien Deleuze
 */
class SpringApplicationDslTests {

	@Test
	fun `Create an empty application`() {
		val app = application { }
		with(app) {
			run()
			context.getBean<ReloadableResourceBundleMessageSource>()
			stop()
		}
	}

	@Test
	fun `Create an application with a custom bean`() {
		val app = application {
			bean<Foo>()
		}
		with(app) {
			run()
			context.getBean<ReloadableResourceBundleMessageSource>()
			context.getBean<Foo>()
			stop()
		}
	}

	@Test
	fun `Application configuration with default Environment property`() {
		val app = application {
			configuration {
				TestConfiguration(name = env["NOT_EXIST"] ?: "default")
			}
		}
		with(app) {
			run()
			assertEquals(context.getBean<TestConfiguration>().name, "default")
			stop()
		}
	}

	@Test
	fun `Application configuration depending on profile`() {
		val app = application {
			profile("foo") {
				configuration {
					TestConfiguration(name = "foo")
				}
			}
			profile("bar") {
				configuration {
					TestConfiguration(name = "bar")
				}
			}
		}
		with(app) {
			run(profiles = "foo")
			assertEquals("foo", context.getBean<TestConfiguration>().name)
			stop()
		}
	}

	class Foo

	data class TestConfiguration(
		val name: String
	)
}


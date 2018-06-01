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
class ApplicationDslTests {

	@Test
	fun `Create an empty application`() {
		val context = GenericApplicationContext()
		val app = application { }
		app.run(context)
		context.getBean<ReloadableResourceBundleMessageSource>()
		context.close()
	}

	@Test
	fun `Create an application with a custom bean`() {
		val context = GenericApplicationContext()
		val app = application {
			beans {
				bean<Foo>()
			}
		}
		app.run(context)
		context.getBean<ReloadableResourceBundleMessageSource>()
		context.getBean<Foo>()
		context.close()
	}

	@Test
	fun `Application configuration with default Environment property`() {
		val context = GenericApplicationContext()
		val app = application {
			configuration {
				TestConfiguration(name = env["NOT_EXIST"] ?: "default")
			}
		}
		app.run(context)
		val testConfig = context.getBean<TestConfiguration>()
		assertEquals(testConfig.name, "default")
		context.close()
	}

	@Test
	fun `Application configuration depending on profile`() {
		val context = GenericApplicationContext()
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
		context.environment.addActiveProfile("foo")
		app.run(context)
		val testConfig = context.getBean<TestConfiguration>()
		assertEquals("foo", testConfig.name)
		context.close()
	}

	class Foo

	data class TestConfiguration(
			val name: String
	)
}


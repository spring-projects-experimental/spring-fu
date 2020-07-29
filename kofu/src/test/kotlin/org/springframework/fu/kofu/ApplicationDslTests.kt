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

package org.springframework.fu.kofu

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.context.MessageSource
import java.util.*

/**
 * @author Sebastien Deleuze
 */
class ApplicationDslTests {

	@Test
	fun `Create an empty application and check message source`() {
		val app = application { }
		with(app.run()) {
			assertFalse(this is ReactiveWebServerApplicationContext)
			val messageSource = getBean<MessageSource>()
			assertEquals("Spring Fu!", messageSource.getMessage("sample.message", null, Locale.getDefault()))
			close()
		}
	}

	@Test
	fun `Create an application with a custom bean`() {
		val app = application {
			beans {
				bean<Foo>(isPrimary = true)
			}
		}
		with(app.run()) {
			getBean<MessageSource>()
			getBean<Foo>()
			close()
		}
	}

	@Test
	fun `Create an application with a configuration import`() {
		val beanConfig = configuration {
			beans {
				bean<Foo>()
			}
		}
		val app = application {
			enable(beanConfig)
		}
		with(app.run()) {
			getBean<MessageSource>()
			getBean<Foo>()
			close()
		}
	}

	@Test
	fun `Mock a bean of an existing application`() {
		val app = application {
			beans {
				bean { Bar("original") }
			}
		}
		app.customize {
			beans {
				bean(isPrimary = true) {
					mockk<Bar> {
						every { value } returns "customized"
					}
				}
			}
		}
		with(app.run()) {
			assertEquals("customized", getBean<Bar>().value)
			close()
		}
	}


	class Foo
	class Bar(val value: String)

}

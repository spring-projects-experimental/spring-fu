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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.boot.logging.LoggingSystem


/**
 * @author Thomas Girard
 * @author Sebastien Deleuze
 */
internal class LoggingDslTests {

	@Test
	fun `Change default ROOT Log level`() {
		application {
			logging {
				level = LogLevel.DEBUG
			}
		}.run()

		val loggingSystem = LoggingSystem.get(LoggingDslTests::class.java.classLoader)
		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration("ROOT").effectiveLevel)
	}

	@Test
	fun `Change package Log level`() {
		val packageName = "org.springframework"
		application {
			logging {
				level(packageName, LogLevel.DEBUG)
			}
		}.run()

		val loggingSystem = LoggingSystem.get(LoggingDslTests::class.java.classLoader)
		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration(packageName).effectiveLevel)
	}

	@Test
	fun `Change class Log level`() {
		val loggingSystem = LoggingSystem.get(LoggingDslTests::class.java.classLoader)
		loggingSystem.setLogLevel("ROOT", LogLevel.INFO)
		application {
			logging {
				level<DefaultListableBeanFactory>(LogLevel.DEBUG)
			}
		}.run()

		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration("org.springframework.beans.factory.support.DefaultListableBeanFactory").effectiveLevel)
	}
}

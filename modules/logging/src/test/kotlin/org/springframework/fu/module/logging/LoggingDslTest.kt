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

package org.springframework.fu.module.logging

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.application
import org.springframework.fu.module.logging.LogLevel.*


/**
 * @author Thomas Girard
 */
internal class LoggingDslTest {

	@Test
	fun `Default LoggingConfiguration`() {
		val conf = LoggingConfiguration()
		assertEquals(INFO, conf.level)
		assertEquals(listOf<Pair<String, LogLevel>>(), conf.packagesLevels)
	}

	@Test
	fun `Default LoggingDsl Configuraton`() {
		lateinit var log: LoggingDsl
		application(false) {
			log = logging {
			}
		}.run()

		assertEquals(INFO, log.configuration.level)
		assertEquals(listOf<Pair<String, LogLevel>>(), log.configuration.packagesLevels)
	}

	@Test
	fun `Change default ROOT Log level`() {
		lateinit var log: LoggingDsl
		application(false) {
			log = logging {
				level(DEBUG)
			}
		}.run()

		assertEquals(DEBUG, log.configuration.level)
		assertEquals(listOf<Pair<String, LogLevel>>(), log.configuration.packagesLevels)
	}

	@Test
	fun `Change package Log level`() {
		lateinit var log: LoggingDsl
		application (false){
			log = logging {
				level("org.springframework", DEBUG)
			}
		}.run()

		assertEquals(INFO, log.configuration.level)
		assertEquals(
			listOf("org.springframework" to DEBUG),
			log.configuration.packagesLevels
		)
	}

	@Test
	fun `Change class Log level`() {
		lateinit var log: LoggingDsl
		application(false) {
			log = logging {
				level<DefaultListableBeanFactory>(DEBUG)
			}
		}.run()

		assertEquals(INFO, log.configuration.level)
		assertEquals(
			listOf("org.springframework.beans.factory.support.DefaultListableBeanFactory" to DEBUG),
			log.configuration.packagesLevels
		)
	}
}

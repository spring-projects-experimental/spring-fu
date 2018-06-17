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

package org.springframework.fu.module.logging.logback

import ch.qos.logback.classic.Level
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.fu.module.logging.LogLevel


/**
 * @author Thomas Girard
 */
internal class LogLevelTest {

	@Test
	fun `LogLevel to Logback Level`() {
		assertEquals(Level.TRACE, LogLevel.TRACE.logback)
		assertEquals(Level.DEBUG, LogLevel.DEBUG.logback)
		assertEquals(Level.INFO, LogLevel.INFO.logback)
		assertEquals(Level.WARN, LogLevel.WARN.logback)
		assertEquals(Level.ERROR, LogLevel.ERROR.logback)
		assertEquals(Level.ALL, LogLevel.FATAL.logback)
		assertEquals(Level.OFF, LogLevel.OFF.logback)
	}
}

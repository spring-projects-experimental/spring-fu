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

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.application
import java.io.File


/**
 * @author Thomas Girard
 */
internal class LogbackDslTest {

	private val tmp = System.getProperty("java.io.tmpdir").let(::File)

	@Test
	fun `Default Logback Configuration`() {
		lateinit var logback: LogbackDsl
		val context = GenericApplicationContext()
		application {
			logging {
				logback = logback { }
			}
		}.run(context)

		assertEquals(false, logback.debug)
		assertEquals(Level.INFO, logback.rootLogger.level)
		assertEquals(listOf<Appender<ILoggingEvent>>(), logback.appenders)
	}

	@Test
	fun `Debug Logback Configuration`() {
		lateinit var logback: LogbackDsl
		val context = GenericApplicationContext()
		application {
			logging {
				logback = logback {
					debug(true)
				}
			}
		}.run(context)

		assertEquals(true, logback.debug)
	}

	@Test
	fun `LogbackDsl rootLogger consoleAppender`() {
		lateinit var logback: LogbackDsl
		val context = GenericApplicationContext()
		application {
			logging {
				logback = logback {
					consoleAppender()
				}
			}
		}.run(context)

		val console = logback.rootLogger.getAppender("STDOUT")
		assertEquals(ConsoleAppender::class.java, console::class.java)
		assertEquals("STDOUT", console.name)
	}

	@Test
	fun `Logback consoleAppender custom`() {
		lateinit var logback: LogbackDsl
		val context = GenericApplicationContext()
		application {
			logging {
				logback = logback {
					consoleAppender(name = "MY_STDOUT")
				}
			}
		}.run(context)

		val console = logback.rootLogger.getAppender("MY_STDOUT")
		assertEquals(ConsoleAppender::class.java, console::class.java)
		assertEquals("MY_STDOUT", console.name)

		assertNull(logback.rootLogger.getAppender("STDOUT"))
	}

	@Test
	fun `Logback rollingFileAppender`() {
		lateinit var logback: LogbackDsl
		val context = GenericApplicationContext()
		val logFile = File(tmp, "log.txt")
		application {
			logging {
				logback = logback {
					rollingFileAppender(logFile)
				}
			}
		}.run(context)

		val rolling = logback.rootLogger.getAppender("ROLLING")
		assertEquals(RollingFileAppender::class.java, rolling::class.java)
		assertEquals("ROLLING", rolling.name)

		(rolling as RollingFileAppender).let {
			assertTrue(it.isAppend)

			(it.rollingPolicy as SizeAndTimeBasedRollingPolicy<*>).let {
				assertEquals(logFile.path, it.activeFileName)
				assertEquals("log.%d{yyyy-MM-dd}.%i.gz", it.fileNamePattern)
				assertEquals(30, it.maxHistory)
			}
		}
	}

	@Test
	fun `Logback rollingFileAppender custom`() {
		lateinit var logback: LogbackDsl
		val context = GenericApplicationContext()
		val logFile = File(tmp, "mylog.txt")
		application {
			logging {
				logback = logback {
					rollingFileAppender(
						file = logFile,
						name = "MY_ROLLING",
						pattern = "%d{yyyy-MM}",
						fileNamePattern = "%i.gz",
						maxFileSize = "2GB",
						maxHistory = 11,
						totalSizeCap = "1MB",
						append = false
					)
				}
			}
		}.run(context)

		val rolling = logback.rootLogger.getAppender("MY_ROLLING")
		assertEquals(RollingFileAppender::class.java, rolling::class.java)
		assertEquals("MY_ROLLING", rolling.name)

		assertNull(logback.rootLogger.getAppender("ROLLING"))

		(rolling as RollingFileAppender).let {
			assertFalse(it.isAppend)

			(it.rollingPolicy as SizeAndTimeBasedRollingPolicy<*>).let {
				assertEquals(logFile.path, it.activeFileName)
				assertEquals("%i.gz", it.fileNamePattern)
				assertEquals(11, it.maxHistory)
			}
		}
	}
}

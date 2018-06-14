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

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
import ch.qos.logback.core.status.OnConsoleStatusListener
import ch.qos.logback.core.util.FileSize
import ch.qos.logback.core.util.StatusListenerConfigHelper
import org.slf4j.LoggerFactory
import org.springframework.fu.module.logging.LoggingConfiguration
import org.springframework.fu.module.logging.LoggingDsl
import org.springframework.util.ClassUtils
import java.io.File


/**
 * @author Thomas Girard
 */
class LogbackDsl(configuration: LoggingConfiguration,
				 init: LogbackDsl.() -> Unit) {

	val loggerContext by lazy { LoggerFactory.getILoggerFactory() as LoggerContext }
	val rootLogger by lazy { loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) }

	internal var debug: Boolean = false
	internal val appenders = mutableListOf<Appender<ILoggingEvent>>()

	init {
		init()
		loggerContext.stop()
		loggerContext.reset()
		if (isBridgeHandlerAvailable()) {
			LevelChangePropagator().apply {
				setResetJUL(true)
				context = loggerContext
				loggerContext.addListener(this)
			}
		}
		loggerContext.start()

		if (debug)
			StatusListenerConfigHelper.addOnConsoleListenerInstance(loggerContext, OnConsoleStatusListener())

		rootLogger.level = configuration.level.logback
		appenders.forEach {
			rootLogger.addAppender(it)
		}

		configuration.packagesLevels.forEach {
			(LoggerFactory.getLogger(it.first) as Logger).level = it.second.logback
		}
	}

	private fun isBridgeHandlerAvailable(): Boolean {
		return ClassUtils.isPresent("org.slf4j.bridge.SLF4JBridgeHandler", ClassUtils.getDefaultClassLoader())
	}
}

/**
 * Adds a new OnConsoleStatusListener to the context passed as parameter.
 *
 * @author Thomas Girard
 */
fun LogbackDsl.debug(debug: Boolean) {
	this.debug = debug
}

/**
 * ```
 * <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
 *   <encoder>
 *     <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS}  %-5p --- [%15.15t] %-40.40logger{39} : %msg %n</pattern>
 *   </encoder>
 * </appender>
 * ```
 *
 * @author Thomas Girard
 */
fun LogbackDsl.consoleAppender(
		name: String = "STDOUT",
		pattern: String = "%d{yyyy-MM-dd HH:mm:ss.SSS}  %-5p --- [%15.15t] %-40.40logger{39} : %msg %n"
) = ConsoleAppender<ILoggingEvent>().apply {
	val encoder = PatternLayoutEncoder()
	encoder.context = loggerContext
	encoder.pattern = pattern
	encoder.start()

	this.context = loggerContext
	this.name = name
	this.encoder = encoder
	start()
	appenders.add(this)
}

/**
 * ```
 * <appender name="ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
 *   <file>file</file>
 *   <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
 *     <maxFileSize>100MB</maxFileSize>
 *     <maxHistory>30</maxHistory>
 *     <totalSizeCap>2GB</totalSizeCap>
 *     <fileNamePattern>file.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
 *   </rollingPolicy>
 *   <encoder>
 *     <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS}  %-5p --- [%t] %-40.40logger{39} : %m%n:</pattern>
 *   </encoder>
 * </appender>
 * ```
 *
 * @author Thomas Girard
 */
fun LogbackDsl.rollingFileAppender(
		file: File,
		name: String = "ROLLING",
		pattern: String = "%d{yyyy-MM-dd HH:mm:ss.SSS}  %-5p --- [%t] %-40.40logger{39} : %m%n:",
		fileNamePattern: String = "${file.nameWithoutExtension}.%d{yyyy-MM-dd}.%i.gz",
		maxFileSize: String = "100MB",
		maxHistory: Int = 30,
		totalSizeCap: String = "2GB",
		append: Boolean = true
) = RollingFileAppender<ILoggingEvent>().apply {
	val encoder = PatternLayoutEncoder()
	encoder.context = loggerContext
	encoder.pattern = pattern
	encoder.start()

	this.context = loggerContext
	this.name = name
	this.isAppend = append
	this.file = file.absolutePath
	this.rollingPolicy = SizeAndTimeBasedRollingPolicy<ILoggingEvent>()
			.also { srp ->
				srp.fileNamePattern = fileNamePattern
				srp.context = loggerContext
				srp.maxHistory = maxHistory
				srp.setMaxFileSize(FileSize.valueOf(maxFileSize))
				srp.setTotalSizeCap(FileSize.valueOf(totalSizeCap))
				srp.setParent(this)
				srp.start()
			}
	this.encoder = encoder
	this.start()
	appenders.add(this)
}

/**
 * Add custom appender
 *
 * @author Thomas Girard
 */
fun LogbackDsl.appender(init: () -> Appender<ILoggingEvent>) =
		init().let { appenders.add(it) }

/**
 * @author Thomas Girard
 */
fun LoggingDsl.logback(init: LogbackDsl.() -> Unit = {}) =
		LogbackDsl(configuration, init)

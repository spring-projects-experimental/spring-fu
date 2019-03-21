/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.fu.kofu

import org.springframework.boot.logging.LogLevel
import org.springframework.boot.logging.LoggingSystem

/**
 * Kofu DSL for logging configuration.
 *
 * @author Thomas Girard
 * @author Sebastien Deleuze
 */
@DslMarker
class LoggingDsl(init: LoggingDsl.() -> Unit) {

	@PublishedApi
	internal val loggingSystem: LoggingSystem = LoggingSystem.get(LoggingDsl::class.java.classLoader)

	init {
		init()
	}

	/**
	 * Set the default ROOT log level
	 */
	var level: LogLevel? = null
		set(value) {
			loggingSystem.setLogLevel("ROOT", value)
		}

	/**
	 * Customize the log level for a given package
	 * @param packageName the package for which the log level should be customized
	 * @param level the log level to use
	 */
	fun level(packageName: String, level: LogLevel) {
		loggingSystem.setLogLevel(packageName, level)
	}

	/**
	 * Customize the log level for a given class
	 * @param T the class for which the log level should be customized
	 * @param level the log level to use
	 *
	 */
	@JvmName("levelReified")
	inline fun <reified T> level(level: LogLevel) {
		T::class.qualifiedName?.let {
			loggingSystem.setLogLevel(it, level)
		}
	}
}

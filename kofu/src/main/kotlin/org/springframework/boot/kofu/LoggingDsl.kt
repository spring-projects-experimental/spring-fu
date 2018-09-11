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

package org.springframework.boot.kofu

import org.springframework.boot.logging.LogLevel
import org.springframework.boot.logging.LoggingSystem


/**
 * @author Thomas Girard
 * @author Sebastien Deleuze
 */
class LoggingDsl(init: LoggingDsl.() -> Unit) {

	val loggingSystem = LoggingSystem.get(LoggingDsl::class.java.classLoader)

	init {
		init()
	}

	/**
	 * Default ROOT Log level
	 */
	fun level(level: LogLevel) {
		loggingSystem.setLogLevel("ROOT", level)
	}

	/**
	 * Custom package Log level
	 */
	fun level(packageName: String, level: LogLevel) {
		loggingSystem.setLogLevel(packageName, level)
	}

	/**
	 * Custom Class Log level
	 */
	@JvmName("levelReified")
	inline fun <reified T> level(level: LogLevel) {
		T::class.qualifiedName?.let {
			loggingSystem.setLogLevel(it, level)
		}
	}
}

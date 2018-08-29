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

import org.springframework.fu.SpringApplicationDsl


/**
 * @author Thomas Girard
 */
class LoggingDsl(init: LoggingDsl.() -> Unit) {

	val configuration = LoggingConfiguration()

	init {
		init()
	}
}


/**
 * Default ROOT Log level
 *
 * @author Thomas Girard
 */
fun LoggingDsl.level(level: LogLevel) {
	this.configuration.level = level
}

/**
 * Custom package Log level
 *
 * @author Thomas Girard
 */
fun LoggingDsl.level(packageName: String, level: LogLevel) {
	configuration.packagesLevels.add(packageName to level)
}

/**
 * Custom Class Log level
 *
 * @author Thomas Girard
 */
@JvmName("levelReified")
inline fun <reified T> LoggingDsl.level(level: LogLevel) {
	T::class.qualifiedName?.let {
		this.configuration.packagesLevels.add(it to level)
	}
}

data class LoggingConfiguration(
	var level: LogLevel = LogLevel.INFO,
	var packagesLevels: MutableList<Pair<String, LogLevel>> = mutableListOf()
)

fun SpringApplicationDsl.logging(init: LoggingDsl.() -> Unit): LoggingDsl =
	LoggingDsl(init)

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

/**
 * @author Thomas Girard
 */
enum class LogLevel {
	TRACE {
		override val logback: Level = Level.TRACE
	},
	DEBUG {
		override val logback: Level = Level.DEBUG
	},
	INFO {
		override val logback: Level = Level.INFO
	},
	WARN {
		override val logback: Level = Level.WARN
	},
	ERROR {
		override val logback: Level = Level.ERROR
	},
	FATAL {
		override val logback: Level = Level.ALL
	},
	OFF {
		override val logback: Level = Level.OFF
	};

	abstract val logback: Level
}

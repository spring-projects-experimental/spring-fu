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

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.env.Environment

@DslMarker
internal annotation class KofuMarker

/**
 * Kofu DSL base class.
 *
 * Make sure to invoke `super.initialize(context)` in inherited classes to get the context initialized.
 *
 * @author Sebastien Deleuze
 */
@KofuMarker
abstract class AbstractDsl {

	@PublishedApi
	internal lateinit var context: GenericApplicationContext

	/**
	 * Shortcut the get the environment.
	 */
	val env: Environment
		get() = context.environment

	/**
	 * Shortcut the get the active profiles.
	 */
	val profiles: Array<String>
		get() = env.activeProfiles

	/**
	 * Take in account functional properties enclosed in the provided lambda only when the
	 * specified profile is active.
	 */
	fun profile(profile: String, block: () -> Unit) {
		if (env.activeProfiles.contains(profile)) {
			block()
		}
	}

	open fun initialize(context: GenericApplicationContext) {
		this.context = context
	}

	internal fun toInitializer() = ApplicationContextInitializer<GenericApplicationContext> { initialize(it) }
}
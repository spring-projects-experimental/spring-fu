/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.fu.module.dynconf

import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.ApplicationDsl
import java.io.InputStreamReader
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * @author Sebastien Deleuze
 */
class DynamicConfigurationModule(private val filename: String) : AbstractModule() {

	override fun initialize(context: GenericApplicationContext) {
		if (!context.containsBean("kotlinScriptEngine")) {
			context.registerBean("kotlinScriptEngine") {
				ScriptEngineManager(context.classLoader).getEngineByName("kotlin")
			}
		}
		context.registerBean {
			val engine = context.getBean<ScriptEngine>("kotlinScriptEngine")
			val resource = context.getResource(filename)
			engine.eval(InputStreamReader(resource.inputStream))
		}
	}
}

fun ApplicationDsl.configuration(filename: String = "application.kts"): DynamicConfigurationModule {
	val configuration = DynamicConfigurationModule(filename)
	initializers.add(configuration)
	return configuration
}

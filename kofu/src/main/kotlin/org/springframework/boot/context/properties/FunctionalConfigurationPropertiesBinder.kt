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

package org.springframework.boot.context.properties

import org.springframework.boot.context.properties.bind.BindResult
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler
import org.springframework.boot.context.properties.bind.handler.NoUnboundElementsBindHandler
import org.springframework.boot.context.properties.source.ConfigurationPropertySources
import org.springframework.boot.context.properties.source.UnboundElementsSourceFilter
import org.springframework.context.ConfigurableApplicationContext
import java.util.function.Consumer

/**
 * Allow to access to package private Boot classes like {@code PropertySourcesDeducer}.
 */
class FunctionalConfigurationPropertiesBinder(private var applicationContext: ConfigurableApplicationContext) {

	private val propertySources = PropertySourcesDeducer(applicationContext).propertySources

	private val binder: Binder

	init {
		binder = Binder(ConfigurationPropertySources.from(propertySources),
				PropertySourcesPlaceholdersResolver(this.propertySources),
				ConversionServiceDeducer(this.applicationContext).conversionService,
				Consumer { applicationContext.beanFactory.copyRegisteredEditorsTo(it) })
	}

	fun <T> bind(prefix: String, target: Bindable<T>): BindResult<T> {
		val filter = UnboundElementsSourceFilter()
		val handler = NoUnboundElementsBindHandler(IgnoreTopLevelConverterNotFoundBindHandler(), filter)
		return binder.bind(prefix, target, handler)
	}


}
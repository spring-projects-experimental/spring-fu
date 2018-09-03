package org.springframework.fu.kofu.boot


import org.apache.commons.logging.LogFactory

import org.springframework.context.ApplicationContext
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertySources

/**
 * Utility to deduce the [PropertySources] to use for configuration binding.
 *
 * @author Phillip Webb
 */
internal class PropertySourcesDeducer(private val applicationContext: ApplicationContext) {

	private val logger = LogFactory.getLog(PropertySourcesDeducer::class.java)

	val propertySources: PropertySources
		get() {
			val configurer = singlePropertySourcesPlaceholderConfigurer
			if (configurer != null) {
				return configurer.appliedPropertySources
			}
			val sources = extractEnvironmentPropertySources()
			if (sources != null) {
				return sources
			}
			throw IllegalStateException("Unable to obtain PropertySources from " + "PropertySourcesPlaceholderConfigurer or Environment")
		}

	private val singlePropertySourcesPlaceholderConfigurer: PropertySourcesPlaceholderConfigurer?
		get() {

			try {
				return this.applicationContext
						.getBean(PropertySourcesPlaceholderConfigurer::class.java)
			} catch (e: RuntimeException) { }
			return null
		}

	private fun extractEnvironmentPropertySources(): MutablePropertySources? {
		val environment = this.applicationContext.environment
		return if (environment is ConfigurableEnvironment) {
			environment.propertySources
		} else null
	}

}
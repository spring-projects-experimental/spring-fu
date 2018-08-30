package org.springframework.fu.properties

import java.util.Collections

import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.boot.convert.ApplicationConversionService
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.GenericConverter

/**
 * Utility to deduce the [ConversionService] to use for configuration properties
 * binding.
 *
 * @author Phillip Webb
 */
internal class ConversionServiceDeducer(private val applicationContext: ConfigurableApplicationContext) {

	val conversionService: ConversionService
		get() {
			try {
				return this.applicationContext.getBean(
						ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME,
						ConversionService::class.java)
			} catch (ex: NoSuchBeanDefinitionException) {
				return this.applicationContext.autowireCapableBeanFactory
						.createBean(Factory::class.java).create()
			}

		}

	private class Factory {

		private var converters = emptyList<Converter<*, *>>()

		private var genericConverters = emptyList<GenericConverter>()

		/**
		 * A list of custom converters (in addition to the defaults) to use when
		 * converting properties for binding.
		 * @param converters the converters to set
		 */
		@Autowired(required = false)
		@ConfigurationPropertiesBinding
		fun setConverters(converters: List<Converter<*, *>>) {
			this.converters = converters
		}

		/**
		 * A list of custom converters (in addition to the defaults) to use when
		 * converting properties for binding.
		 * @param converters the converters to set
		 */
		@Autowired(required = false)
		@ConfigurationPropertiesBinding
		fun setGenericConverters(converters: List<GenericConverter>) {
			this.genericConverters = converters
		}

		fun create(): ConversionService {
			if (this.converters.isEmpty() && this.genericConverters.isEmpty()) {
				return ApplicationConversionService.getSharedInstance()
			}
			val conversionService = ApplicationConversionService()
			for (converter in this.converters) {
				conversionService.addConverter(converter)
			}
			for (genericConverter in this.genericConverters) {
				conversionService.addConverter(genericConverter)
			}
			return conversionService
		}

	}

}
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
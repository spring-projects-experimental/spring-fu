package org.springframework.boot.context.properties

import org.springframework.boot.ApplicationDsl
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.context.support.registerBean

inline fun <reified T : Any> ApplicationDsl.configuration(prefix: String = "") {
	context.registerBean("${T::class.java.simpleName.toLowerCase()}ConfigurationProperties") {
		FunctionalConfigurationPropertiesBinder(context).bind(prefix, Bindable.of(T::class.java)).get()
	}
}


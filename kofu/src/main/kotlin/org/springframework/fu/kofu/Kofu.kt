package org.springframework.fu.kofu

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.context.support.GenericApplicationContext

/**
 * Declare an [application][ApplicationDsl] that allows to configure a Spring Boot
 * application using Kofu DSL and functional bean registration.
 *
 * @sample org.springframework.fu.kofu.samples.webFluxApplicationDsl
 * @param type The [WebApplicationType] of the application
 * @param dsl The `application { }` DSL
 * @see ApplicationDsl
 * @author Sebastien Deleuze
 */
fun application(type: WebApplicationType, dsl: ApplicationDsl.() -> Unit)
		= object: KofuApplication(ApplicationDsl(dsl)) {
	override fun initializeWebApplicationContext(app: SpringApplication) {
		app.webApplicationType = type
		app.setApplicationContextClass(when(type) {
			WebApplicationType.NONE -> GenericApplicationContext::class.java
			WebApplicationType.REACTIVE -> ReactiveWebServerApplicationContext::class.java
			WebApplicationType.SERVLET -> ServletWebServerApplicationContext::class.java
		})
	}
}

/**
 * Define a configuration that can be imported in an application or used in tests.
 * @see ConfigurationDsl.enable
 * @sample org.springframework.fu.kofu.samples.applicationDslWithConfiguration
 */
fun configuration(dsl: ConfigurationDsl.() -> Unit)
		= ConfigurationDsl(dsl)

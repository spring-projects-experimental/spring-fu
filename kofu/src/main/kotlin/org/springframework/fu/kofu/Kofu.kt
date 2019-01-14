package org.springframework.fu.kofu

import org.springframework.boot.SpringFuApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.boot.web.reactive.context.StandardReactiveWebEnvironment
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.env.StandardEnvironment

/**
 * Declare an [application][ApplicationDsl] that allows to configure a Spring Boot
 * application using Kofu DSL and functional bean registration. For web servers,
 * use [webApplication] instead.
 *
 * @sample org.springframework.fu.kofu.samples.applicationDsl
 * @param dsl The `application { }` DSL
 * @see ApplicationDsl.logging
 * @author Sebastien Deleuze
 */
fun application(dsl: ApplicationDsl.() -> Unit)
        = object: KofuApplication(ApplicationDsl(dsl)) {
    override fun getApplication() = SpringFuApplication(GenericApplicationContext(), StandardEnvironment())
}

/**
 * Declare a [web application][ApplicationDsl] that allows to configure a Spring Boot
 * application using Kofu DSL and functional bean registration. Requires a {@code server} child element.
 *
 * @sample org.springframework.fu.kofu.samples.webApplicationDsl
 * @param dsl The `application { }` DSL
 * @see ApplicationDsl.logging
 * @see org.springframework.fu.kofu.web.server
 */
fun webApplication(dsl: ApplicationDsl.() -> Unit)
        = object: KofuApplication(ApplicationDsl(dsl)) {
    override fun getApplication() = SpringFuApplication(ReactiveWebServerApplicationContext(), StandardReactiveWebEnvironment())
}

/**
 * Define a configuration that can be imported in an application or used in tests.
 * @see ConfigurationDsl.enable
 * @sample org.springframework.fu.kofu.samples.applicationDslWithConfiguration
 */
fun configuration(dsl: ConfigurationDsl.() -> Unit)
        = ConfigurationDsl(dsl)

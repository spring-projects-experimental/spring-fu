package org.springframework.fu.kofu

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.GenericApplicationContext

/**
 * Kofu application that can be run parameterized with Spring profiles and/or command line arguments.
 * @see application
 * @see webApplication
 */
abstract class KofuApplication(private val initializer: ApplicationContextInitializer<GenericApplicationContext>) {

    /**
     * Run the current application
     * @param profiles [ApplicationContext] profiles separated by commas.
     * @param args the application arguments (usually passed from a Java main method)
     * @return The application context of the application
     */
    fun run(args: Array<String> = emptyArray(), profiles: String = ""): ConfigurableApplicationContext {
        val app = object: SpringApplication(KofuApplication::class.java) {
            override fun load(context: ApplicationContext?, sources: Array<out Any>?) {
                // We don't want the annotation bean definition reader
            }
        }
        initializeWebApplicationContext(app)
        if (!profiles.isEmpty()) {
            app.setAdditionalProfiles(*profiles.split(",").map { it.trim() }.toTypedArray())
        }
        app.addInitializers(initializer)
        System.setProperty("spring.backgroundpreinitializer.ignore", "true")
        return app.run(*args)
    }

    protected abstract fun initializeWebApplicationContext(app: SpringApplication)

}

/**
 * Declare an [application][ApplicationDsl] that allows to configure a Spring Boot
 * application using Kofu DSL and functional bean registration. For web servers,
 * use [webApplication] instead.
 *
 * @sample org.springframework.fu.kofu.samples.applicationDslWithCustomBeanApplication
 * @sample org.springframework.fu.kofu.samples.applicationDslOverview
 * @param dsl The `application { }` DSL
 * @see ApplicationDsl.logging
 */
fun application(dsl: ApplicationDsl.() -> Unit)
        = object: KofuApplication(ApplicationDsl(dsl)) {
    override fun initializeWebApplicationContext(app: SpringApplication) {
        app.webApplicationType = WebApplicationType.NONE
        app.setApplicationContextClass(GenericApplicationContext::class.java)
    }
}

/**
 * Declare a [web application][ApplicationDsl] that allows to configure a Spring Boot
 * application using Kofu DSL and functional bean registration. Requires a {@code server} child element.
 *
 * @sample org.springframework.fu.kofu.samples.applicationDslWithCustomBeanApplication
 * @sample org.springframework.fu.kofu.samples.applicationDslOverview
 * @param dsl The `application { }` DSL
 * @see ApplicationDsl.logging
 * @see org.springframework.fu.kofu.web.server
 */
fun webApplication(dsl: ApplicationDsl.() -> Unit)
        = object: KofuApplication(ApplicationDsl(dsl)) {
    override fun initializeWebApplicationContext(app: SpringApplication) {
        app.webApplicationType = WebApplicationType.REACTIVE
        app.setApplicationContextClass(ReactiveWebServerApplicationContext::class.java)
    }
}

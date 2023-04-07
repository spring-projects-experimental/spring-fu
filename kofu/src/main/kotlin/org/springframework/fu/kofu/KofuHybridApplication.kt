package org.springframework.fu.kofu

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.Ordered


/**
 * Kofu application that supports annotation configuration as well
 * To use, declare a class that extends this class,
 * and register that class in your application's spring.factories file as a ApplicationContextInitializer.
 * This class may be your main Application class, but that is optional.
 * @author John Burns
 */
abstract class KofuHybridApplication : Ordered, ApplicationContextInitializer<GenericApplicationContext> {
    abstract fun dslConfigure(): ApplicationDsl.() -> Unit

    override fun getOrder(): Int {
        return Int.MIN_VALUE
    }

    override fun initialize(applicationContext: GenericApplicationContext) {
        val app = ApplicationDsl(dslConfigure())
        app.toInitializer().initialize(applicationContext)
    }
}

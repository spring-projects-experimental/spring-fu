package org.springframework.fu.kofu

import org.springframework.beans.factory.support.BeanDefinitionReaderUtils
import org.springframework.boot.context.properties.FunctionalConfigurationPropertiesBinder
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ApplicationEvent
import org.springframework.context.FunctionalClassPathScanningCandidateComponentProvider
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.util.ClassUtils

/**
 * Kofu DSL for a configuration that can be imported in an application or used in tests.
 *
 * @see configuration
 * @see enable
 * @sample org.springframework.fu.kofu.samples.applicationDslWithConfiguration
 * @author Sebastien Deleuze
 */
open class ConfigurationDsl(private val dsl: ConfigurationDsl.() -> Unit): AbstractDsl() {

	/**
	 * Configure beans via a [dedicated DSL][BeanDefinitionDsl].
	 *
	 * Since classes with a single constructor have their parameters automatically autowired,
	 * it is recommended to use constructor injection with `val` read-only
	 * (and non-nullable when possible) private [properties](https://kotlinlang.org/docs/reference/properties.html).
	 *
	 * @sample org.springframework.fu.kofu.samples.beansDsl
	 */
	fun beans(dsl: BeanDefinitionDsl.() -> Unit) {
		BeanDefinitionDsl(dsl).initialize(context)
	}

	/**
	 * Scan beans from the provided base package.
	 *
	 * The preferred constructor (Kotlin primary constructor and standard public constructors)
	 * are evaluated for autowiring before falling back to default instantiation.
	 *
	 * TODO Support generating component indexes
	 * TODO Support exclusion
	 *
	 * @param basePackage The base package to scan
	 * @sample org.springframework.fu.kofu.samples.beanScanDsl
	 */
	fun BeanDefinitionDsl.scan(basePackage: String) {
		val componentProvider = FunctionalClassPathScanningCandidateComponentProvider()
		for(metadata in componentProvider.findCandidateComponents(basePackage)) {
			val source = ClassUtils.resolveClassName(metadata.className, null)
			val beanName = BeanDefinitionReaderUtils.uniqueBeanName(source.name, context)
			context.registerBean(beanName, source)
		}
	}

	/**
	 * Enable the specified functional configuration.
	 * @see configuration
	 * @sample org.springframework.fu.kofu.samples.applicationDslWithConfiguration
	 */
	fun enable(configuration: ApplicationContextInitializer<GenericApplicationContext>) {
        configuration.initialize(context)
	}

	/**
	 * Specify the class and the optional prefix of configuration properties, which is the same mechanism than regular
	 * [Spring Boot configuration properties mechanism](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-typesafe-configuration-properties),
	 * without `@ConfigurationProperties` annotation.
	 *
	 * @sample org.springframework.fu.kofu.samples.configurationProperties
	 */
	inline fun <reified T : Any> configurationProperties(prefix: String = "") {
		context.registerBean("${T::class.java.simpleName.toLowerCase()}ConfigurationProperties") {
			FunctionalConfigurationPropertiesBinder(context).bind(prefix, Bindable.of(T::class.java)).get()
		}
	}

	/**
	 * Configure global, package or class logging via a [dedicated DSL][LoggingDsl].
	 * @sample org.springframework.fu.kofu.samples.loggingDsl
	 */
	fun logging(dsl: LoggingDsl.() -> Unit) {
		LoggingDsl().dsl()
	}

	/**
	 * Declare application event Listeners in order to run tasks when `ApplicationEvent`
	 * like `ApplicationReadyEvent` are emitted.
	 * @sample org.springframework.fu.kofu.samples.listener
	 */
	inline fun <reified E : ApplicationEvent>listener(crossinline listener: (E) -> Unit) {
		context.addApplicationListener {
			// TODO Leverage SPR-16872 when it will be fixed
			if (it is E) {
				listener.invoke(it)
			}
		}
	}

	/**
	 * Take in account functional properties enclosed in the provided lambda only when the
	 * specified profile is active.
	 */
	fun profile(profile: String, block: () -> Unit) {
		if (env.activeProfiles.contains(profile)) {
			block()
		}
	}

	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		dsl()
	}

}

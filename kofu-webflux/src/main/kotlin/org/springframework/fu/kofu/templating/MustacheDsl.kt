package org.springframework.fu.kofu.templating

import org.springframework.fu.kofu.webflux.WebFluxServerDsl


/**
 * Configure a [Mustache](https://github.com/samskivert/jmustache) view resolver.
 *
 * Require `org.springframework.boot:spring-boot-starter-mustache` dependency.
 *
 * @sample org.springframework.fu.kofu.samples.mustacheDsl
 * @author Sebastien Deleuze
 */
fun WebFluxServerDsl.mustache(dsl: MustacheDsl.() -> Unit = {}) {
    MustacheDsl(dsl).initializeReactive(context)
}

package org.springframework.fu.kofu.templating

import org.springframework.fu.kofu.webmvc.WebMvcServerDsl

/**
 * Configure a [Mustache](https://github.com/samskivert/jmustache) view resolver.
 *
 * Require `org.springframework.boot:spring-boot-starter-mustache` dependency.
 *
 * @author Sebastien Deleuze
 */
fun WebMvcServerDsl.mustache(dsl: MustacheDsl.() -> Unit = {}) {
    MustacheDsl(dsl).initializeServlet(context)
}

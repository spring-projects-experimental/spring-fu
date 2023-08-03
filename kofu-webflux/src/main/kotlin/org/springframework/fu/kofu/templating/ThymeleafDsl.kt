package org.springframework.fu.kofu.templating

import org.springframework.fu.kofu.webflux.WebFluxServerDsl


/**
 * Configure a [Thymeleaf](https://github.com/thymeleaf/thymeleaf) view resolver.
 *
 * Require `org.springframework.boot:spring-boot-starter-thymeleaf` dependency.
 *
 * @sample org.springframework.fu.kofu.samples.thymeleafDsl
 */
fun WebFluxServerDsl.thymeleaf(dsl: ThymeleafDsl.() -> Unit = {}) {
    ThymeleafDsl(dsl).initializeReactive(context)
}

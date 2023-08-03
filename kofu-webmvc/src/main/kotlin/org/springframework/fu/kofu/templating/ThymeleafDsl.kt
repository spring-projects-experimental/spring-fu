package org.springframework.fu.kofu.templating

import org.springframework.fu.kofu.webmvc.WebMvcServerDsl


/**
 * Configure a [Thymeleaf](https://github.com/thymeleaf/thymeleaf) view resolver.
 *
 * Require `org.springframework.boot:spring-boot-starter-thymeleaf` dependency.
 */
fun WebMvcServerDsl.thymeleaf(dsl: ThymeleafDsl.() -> Unit = {}) {
    ThymeleafDsl(dsl).initializeServlet(context)
}

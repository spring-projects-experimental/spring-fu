package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.thymeleaf
import org.springframework.fu.kofu.webflux.webFlux

fun thymeleafDsl() {
    application(WebApplicationType.REACTIVE) {
        webFlux {
            thymeleaf()
        }
    }
}
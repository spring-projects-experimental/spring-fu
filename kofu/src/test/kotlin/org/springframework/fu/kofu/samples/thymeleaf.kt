package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webflux.thymeleaf
import org.springframework.fu.kofu.webflux.webFlux

fun thymeleafDsl() {
    webApplication {
        webFlux {
            thymeleaf()
        }
    }
}
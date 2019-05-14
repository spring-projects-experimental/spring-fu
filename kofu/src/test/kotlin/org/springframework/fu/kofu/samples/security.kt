package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.security
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.fu.kofu.webflux.webFluxSecurity

fun securityDsl() {
    application(WebApplicationType.REACTIVE) {
        security()
        webFlux {
            webFluxSecurity()
        }
    }
}

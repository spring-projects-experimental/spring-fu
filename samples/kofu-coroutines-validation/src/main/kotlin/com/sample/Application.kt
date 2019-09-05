package com.sample

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.webFlux

@ExperimentalCoroutinesApi
val app = application(WebApplicationType.REACTIVE) {
    beans {
        bean<UserHandler>()
        bean(::routes)
    }
    webFlux {
        port = if (profiles.contains("test")) 8181 else 8080
        codecs {
            string()
            jackson()
        }
    }
}

@ExperimentalCoroutinesApi
fun main() {
    app.run()
}

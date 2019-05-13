package com.sample

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webmvc.webMvc

val app = application(WebApplicationType.SERVLET) {
    beans {
        bean<UserHandler>()
        bean(::routes)
    }
    webMvc {
        port = if (profiles.contains("test")) 8181 else 8080
		converters {
			jackson()
		}
    }
}

fun main() {
    app.run()
}
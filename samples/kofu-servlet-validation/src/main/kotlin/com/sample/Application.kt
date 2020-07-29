package com.sample

import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc

val app = webApplication {
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
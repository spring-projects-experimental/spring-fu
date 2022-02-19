package com.sample.kofututorial

import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc

val app = webApplication {
    webMvc {
    }
}

fun main(args: Array<String>) {
    app.run(args)
}

package com.sample

import org.springframework.fu.kofu.webApplication

val app = webApplication {
	configurationProperties<SampleProperties>(prefix = "sample")
	enable(dataConfig)
	enable(webConfig)
}

fun main() {
	app.run()
}

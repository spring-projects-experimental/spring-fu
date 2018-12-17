package org.springframework.fu.sample.coroutines

import org.springframework.fu.kofu.application

val app = application {
	enable(dataConfig)
	enable(webConfig)
	configurationProperties<SampleProperties>("sample")
}

fun main() {
	app.run()
}

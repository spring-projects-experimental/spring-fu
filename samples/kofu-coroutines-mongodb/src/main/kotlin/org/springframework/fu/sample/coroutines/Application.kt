package org.springframework.fu.sample.coroutines

import org.springframework.fu.kofu.reactiveWebApplication

val app = reactiveWebApplication {
	enable(dataConfig)
	enable(webConfig)
	configurationProperties<SampleProperties>("sample")
}

fun main() {
	app.run()
}

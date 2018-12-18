package org.springframework.fu.sample.coroutines

import org.springframework.fu.kofu.webApplication

val app = webApplication {
	enable(dataConfig)
	enable(webConfig)
	configurationProperties<SampleProperties>("sample")
}

fun main() {
	app.run()
}

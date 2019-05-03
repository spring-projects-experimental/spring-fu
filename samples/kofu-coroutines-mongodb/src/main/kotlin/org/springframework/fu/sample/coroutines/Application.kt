package org.springframework.fu.sample.coroutines

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application

val app = application(WebApplicationType.REACTIVE)  {
	enable(dataConfig)
	enable(webConfig)
	configurationProperties<SampleProperties>("sample")
}

fun main() {
	app.run()
}

package org.springframework.fu.sample.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application

@ExperimentalCoroutinesApi
val app = application(WebApplicationType.REACTIVE) {
	configurationProperties<SampleProperties>(prefix = "sample")
	enable(dataConfig)
	enable(webConfig)
}

@ExperimentalCoroutinesApi
fun main() {
	app.run()
}

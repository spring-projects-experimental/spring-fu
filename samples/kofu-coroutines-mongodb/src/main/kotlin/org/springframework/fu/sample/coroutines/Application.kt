package org.springframework.fu.sample.coroutines

import org.springframework.fu.kofu.application

val app = application {
	import(dataConfig)
	import(webConfig)
	properties<SampleProperties>("sample")
}

fun main() = app.run()

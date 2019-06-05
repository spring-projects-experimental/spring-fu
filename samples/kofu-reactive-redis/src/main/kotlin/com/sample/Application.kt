package com.sample

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer

val app = application(WebApplicationType.REACTIVE) {
	configurationProperties<SampleProperties>("sample")
	enable(dataConfig)
	enable(webConfig)
}

fun main() {
	val redisContainer = object : GenericContainer<Nothing>("redis:5") {}
	redisContainer.withExposedPorts(6379)
	redisContainer.start()
	app.run(args = arrayOf("--redis.port=${redisContainer.firstMappedPort}", "--redis.host=${redisContainer.containerIpAddress}"))
}

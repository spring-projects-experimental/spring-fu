package com.sample

import org.springframework.fu.kofu.reactiveWebApplication
import org.testcontainers.containers.GenericContainer

fun app(properties: ApplicationProperties) = reactiveWebApplication {
	with(configurationProperties(properties)) {
		enable(dataConfig(redisHost, redisPort))
		enable(webConfig(serverPort))
	}
}

fun main() {
	val redisContainer = object : GenericContainer<Nothing>("redis:5") {}
	redisContainer.withExposedPorts(6379)
	redisContainer.start()
	val properties = ApplicationProperties(
			redisHost = redisContainer.containerIpAddress,
			redisPort = redisContainer.firstMappedPort)
	app(properties).run()
}

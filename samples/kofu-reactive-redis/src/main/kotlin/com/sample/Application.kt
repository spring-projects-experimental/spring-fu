package com.sample

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer

fun app(properties: ApplicationProperties) = application(WebApplicationType.REACTIVE) {
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

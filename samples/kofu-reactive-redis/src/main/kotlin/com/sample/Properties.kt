package com.sample

class ApplicationProperties(
		val redisHost: String = "localhost",
		val redisPort: Int = 6379,
		val serverPort: Int = 8080,
		val message: String = "Default message")
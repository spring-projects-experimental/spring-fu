package com.sample

import com.datastax.driver.core.ProtocolOptions

class ApplicationProperties(
		val cassandraHost: String = "localhost",
		val cassandraPort: Int = ProtocolOptions.DEFAULT_PORT,
		val serverPort: Int = 8080,
		val message: String = "Default message")
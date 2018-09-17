package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.boot.logging.LogLevel

private fun loggingDsl() {
	application {
		logging {
			level(LogLevel.INFO)
			level("org.springframework", LogLevel.DEBUG)
			level<ApiHandler>(LogLevel.WARN)
		}
	}
}
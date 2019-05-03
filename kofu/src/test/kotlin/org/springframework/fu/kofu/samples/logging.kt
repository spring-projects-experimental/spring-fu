package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.boot.logging.LogLevel
import org.springframework.fu.kofu.application

private fun loggingDsl() {
	application(WebApplicationType.NONE) {
		logging {
			level = LogLevel.INFO
			level("org.springframework", LogLevel.DEBUG)
			level<ApiHandler>(LogLevel.WARN)
		}
	}
}
package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcH2
import org.springframework.fu.kofu.r2dbc.r2dbcPostgresql

fun r2dbcPostgresql() {
	application(WebApplicationType.NONE) {
		r2dbcPostgresql {
			host = "dbserver"
			port = 1234
		}
	}
}

fun r2dbcH2() {
	application(WebApplicationType.NONE) {
		r2dbcH2()
	}
}

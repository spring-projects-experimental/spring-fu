package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbc

fun r2dbc() {
	application(startServer = false) {
		r2dbc {
			host = "dbserver"
			port = 1234
		}
	}
}

fun r2dbcCoroutines() {
	application(startServer = false) {
		r2dbc {
			host = "dbserver"
			port = 1234
			coroutines = true
		}
	}
}
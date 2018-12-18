package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbcH2
import org.springframework.fu.kofu.r2dbc.r2dbcPostgresql

fun r2dbcPostgresql() {
	application {
		r2dbcPostgresql {
			host = "dbserver"
			port = 1234
		}
	}
}

fun r2dbcH2() {
	application {
		r2dbcH2()
	}
}

fun r2dbcPostgresqlCoroutines() {
	application {
		r2dbcPostgresql {
			host = "dbserver"
			port = 1234
			coroutines = true
		}
	}
}

fun r2dbcH2Coroutines() {
	application {
		r2dbcH2 {
			coroutines = true
		}
	}
}
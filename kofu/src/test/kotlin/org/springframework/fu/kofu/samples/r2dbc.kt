package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbc
import org.springframework.fu.kofu.r2dbc.r2dbcMssql
import org.springframework.fu.kofu.r2dbc.r2dbcMysql

fun r2dbcPostgresql() {
	application {
		r2dbc {
			url = "r2dbc:postgresql://dbserver:1234"
		}
	}
}

fun r2dbcH2() {
	application {
		r2dbcH2()
	}
}


fun r2dbcMssql() {
	application {
		r2dbcMssql {
			host = "dbserver"
			port = 1234
		}
	}
}


fun r2dbcMysql() {
	application {
		r2dbcMysql("localhost")
	}
}

fun r2dbc(){
	application {
		r2dbc {
			url = "r2dbc:postgresql://localhost/test"
		}
	}
}

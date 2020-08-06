package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.r2dbc

fun r2dbcPostgresql() {
	application {
		r2dbc {
			url = "r2dbc:postgresql://dbserver:1234"
		}
	}
}

fun r2dbc(){
	application {
		r2dbc {
			url = "r2dbc:postgresql://localhost/test"
		}
	}
}

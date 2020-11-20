package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.r2dbc.dataR2dbc
import org.springframework.fu.kofu.r2dbc.r2dbc

fun dataR2dbcPostgresql() {
    application {
        dataR2dbc {
            r2dbc {
                url = "r2dbc:postgresql://dbserver:1234"
            }
        }
    }
}

fun dataR2dbc() {
    application {
        dataR2dbc {
            r2dbc {
                url = "r2dbc:postgresql://localhost/test"
            }
        }
    }
}

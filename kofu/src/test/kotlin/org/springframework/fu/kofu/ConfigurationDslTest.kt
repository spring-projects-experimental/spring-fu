package org.springframework.fu.kofu

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.support.beans
import org.springframework.fu.kofu.r2dbc.R2dbcDsl
import org.springframework.r2dbc.core.DatabaseClient

class ConfigurationDslTest {

    @Test
    fun `enable should initialize other AbstractDsl`() {
        val r2dbc = R2dbcDsl {
            url = "r2dbc:h2:mem:///testdb"
        }

        val app = application {
            enable(r2dbc)
        }
        with(app.run()) {
            val client = getBean<DatabaseClient>()
            assertNotNull(client)
            close()
        }
    }


    @Test
    fun `enable should initialize ApplicationContextInitializer`() {
        val beans = beans {
            bean<TestService>()
            bean<TestHandler>()
        }

        val app = application {
            enable(beans)
        }
        with(app.run()) {
            getBean<TestService>()
            getBean<TestHandler>()
            close()
        }
    }

    class TestService{}
    class TestHandler(private val service: TestService)
}
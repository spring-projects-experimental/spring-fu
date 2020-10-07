package org.springframework.fu.kofu.jdbc

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.fu.kofu.application
import org.springframework.jdbc.core.JdbcTemplate

class JdbcDslTests {

    @Test
    fun `enable jdbc`() {
        val app = application {
            jdbc()
        }

        with(app.run()) {
            val jdbcTemplate = getBean<JdbcTemplate>()
            Assert.assertNotNull(jdbcTemplate)
            close()
        }
    }


}
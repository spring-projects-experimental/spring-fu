package org.springframework.fu.kofu

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.support.beans

class ConfigurationDslTest {

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

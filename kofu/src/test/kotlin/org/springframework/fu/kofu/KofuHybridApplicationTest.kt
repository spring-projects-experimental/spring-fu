package org.springframework.fu.kofu

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootTest(classes = [TestApplication::class])
@ContextConfiguration(initializers = [TestApplication::class])
class KofuHybridApplicationTest {
    @Autowired
    lateinit var configuredBean: ConfiguredBean

    @Autowired
    lateinit var dsl: DslBean

    @Test
    fun test() {
        assertEquals("test", configuredBean.value)
        assertEquals("value", dsl.value)
    }
}

@SpringBootConfiguration
@EnableAutoConfiguration(
    exclude = [
        CassandraDataAutoConfiguration::class,
        CassandraAutoConfiguration::class,
        MongoAutoConfiguration::class,
        EmbeddedMongoAutoConfiguration::class
    ]
)
@EnableWebMvc
open class TestApplication : KofuHybridApplication() {
    override fun dslConfigure(): ApplicationDsl.() -> Unit = {
        beans {
            bean<ConfiguredBean>("aBean") {
                ConfiguredBean("test")
            }
        }
        custom {
            enable()
        }
    }

}

@KofuMarker
fun ApplicationDsl.custom(config: CustomDsl.() -> Unit) {
    enable(CustomDsl().apply(config))
}

@KofuMarker
class CustomDsl : AbstractDsl() {
    private var enabled: Boolean = false
    fun enable() {
        enabled = true
    }

    override fun initialize(context: GenericApplicationContext) {
        context.registerBean<DslBean>("dsl", { }) {
            DslBean("value")
        }
    }
}

class DslBean(val value: String)
class ConfiguredBean(val value: String)
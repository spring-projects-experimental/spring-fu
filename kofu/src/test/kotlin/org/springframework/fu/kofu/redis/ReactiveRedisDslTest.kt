package org.springframework.fu.kofu.redis

import org.junit.Assert
import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.ResourceLoader
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer
import java.io.Serializable
import java.time.Duration

class ReactiveRedisDslTest {

	@ClassRule
	@JvmField
	val redis = object : GenericContainer<Nothing>("redis:5") {
		init {
			withExposedPorts(6379)
			start()
		}
	}

	@Test
	fun `enable reactive redis with default lettuce configuration`() {
		val app = application(WebApplicationType.NONE) {
			beans {
				bean<ReactiveTestRepository>()
				bean<ResourceLoader>(function = {
					DefaultResourceLoader()
				})
			}
			reactiveRedis {
				host = redis.containerIpAddress
				port = redis.firstMappedPort
				lettuce()
			}
		}

		with(app.run()) {
			val repository = getBean<ReactiveTestRepository>()
			repository.save(ReactiveTestUser("1", "foo")).block(Duration.ofSeconds(3))
			Assert.assertEquals("foo", repository.findById("1").block(Duration.ofSeconds(3))?.name)
			close()
		}
	}
}

class ReactiveTestRepository(
		private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>
) {
	fun findById(id: String) = reactiveRedisTemplate.opsForHash<String, ReactiveTestUser>().get("test", id)
	fun save(user: ReactiveTestUser) = reactiveRedisTemplate.opsForHash<String, ReactiveTestUser>().put("test", user.id, user)
}

data class ReactiveTestUser(val id: String, val name: String) : Serializable
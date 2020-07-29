package org.springframework.fu.kofu.redis

import org.junit.Assert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer
import java.io.Serializable
import java.time.Duration

class ReactiveRedisDslTests {

	private lateinit var redis: GenericContainer<Nothing>

	@BeforeAll
	fun setup() {
		redis = object : GenericContainer<Nothing>("redis:5") {
			init {
				withExposedPorts(6379)
			}
		}
		redis.start()
	}

	@Test
	fun `enable reactive redis with default lettuce configuration`() {
		val app = application {
			beans {
				bean<ReactiveTestRepository>()
			}
			reactiveRedis {
				host = redis.containerIpAddress
				port = redis.firstMappedPort
			}
		}

		with(app.run()) {
			val repository = getBean<ReactiveTestRepository>()
			repository.save(ReactiveTestUser("1", "foo")).block(Duration.ofSeconds(3))
			Assert.assertEquals("foo", repository.findById("1").block(Duration.ofSeconds(3))?.name)
			close()
		}
	}

	@AfterAll
	fun tearDown() {
		redis.stop()
	}
}

class ReactiveTestRepository(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ReactiveTestUser>) {
	fun findById(id: String) = reactiveRedisTemplate.opsForValue().get(id)
	fun save(user: ReactiveTestUser) = reactiveRedisTemplate.opsForValue().set(user.id, user)
}

data class ReactiveTestUser(val id: String, val name: String): Serializable
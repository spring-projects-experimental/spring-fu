package org.springframework.fu.kofu.redis

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer
import java.io.Serializable

class RedisDslTests {

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
	fun `enable redis`() {
		val app = application {
			beans {
				bean<TestRepository>()
			}
			redis {
				host = redis.containerIpAddress
				port = redis.firstMappedPort
			}
		}

		with(app.run()) {
			val repository = getBean<TestRepository>()
			repository.save(TestUser("1", "foo"))
			assertEquals("foo", repository.findById("1")?.name)
			close()
		}
	}

	@Test
	fun `enable redis with jedis`() {
		val app = application {
			beans {
				bean<TestRepository>()
			}
			redis {
				host = redis.containerIpAddress
				port = redis.firstMappedPort
				jedis()
			}
		}

		with(app.run()) {
			val repository = getBean<TestRepository>()
			repository.save(TestUser("1", "foo"))
			assertEquals("foo", repository.findById("1")?.name)
			close()
		}
	}

	@AfterAll
	fun tearDown() {
		redis.stop()
	}
}

class TestRepository(private val redisTemplate: RedisTemplate<String, TestUser>) {

	fun findById(id: String) = redisTemplate.opsForValue().get(id)
	fun save(user: TestUser) = redisTemplate.opsForValue().set(user.id, user)
}

data class TestUser(val id: String, val name: String): Serializable
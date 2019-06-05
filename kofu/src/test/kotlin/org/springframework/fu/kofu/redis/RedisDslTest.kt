package org.springframework.fu.kofu.redis

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.junit.Assert.assertEquals
import org.junit.ClassRule
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.WebApplicationType
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.fu.kofu.application
import org.testcontainers.containers.GenericContainer

class RedisDslTest {

	@ClassRule
	@JvmField
	val redis = object : GenericContainer<Nothing>("redis:5") {
		init {
			withExposedPorts(6379)
			start()
		}
	}

	@Test
	fun `enable redis with default jedis configuration`() {
		val app = application(WebApplicationType.NONE) {
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

	@Test
	fun `enable redis with default lettuce configuration`() {
		val app = application(WebApplicationType.NONE) {
			beans {
				bean<TestRepository>()
			}
			redis {
				host = redis.containerIpAddress
				port = redis.firstMappedPort
				lettuce()
			}
		}

		with(app.run()) {
			val repository = getBean<TestRepository>()
			repository.save(TestUser("1", "foo"))
			assertEquals("foo", repository.findById("1")?.name)
			close()
		}
	}
}

class TestRepository(
		private val redisTemplate: RedisTemplate<String, ByteArray>
) {
	init {
		redisTemplate.hashValueSerializer = Jackson2JsonRedisSerializer(TestUser::class.java)
	}

	fun findById(id: String) = redisTemplate.opsForHash<String, TestUser>().get("test", id)
	fun save(user: TestUser) = redisTemplate.opsForHash<String, TestUser>().put("test", user.id, user)
}

data class TestUser @JsonCreator constructor(
		@JsonProperty("id") val id: String,
		@JsonProperty("name") val name: String
)
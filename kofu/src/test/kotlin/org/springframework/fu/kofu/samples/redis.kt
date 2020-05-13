package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.redis.redis
import java.time.Duration

fun jedis() {
	application {
		redis {
			password = "password"
			jedis {
				maxActive = 7
				maxIdle = 7
				minIdle = 2
				maxWait = Duration.ofMillis(-1)
			}
			cluster {
				node("localhost", 1234)
				node("localhost", 1235)
				node("localhost", 1236)
				maxRedirects = 2
			}
		}
	}
}

fun lettuce() {
	application {
		redis {
			host = "localhost"
			port = 6789
			password = "password"
			timeout = Duration.ofMillis(100)
			ssl = true
			lettuce {
				shutdownTimeout = Duration.ofMillis(100)
				pool {
					maxActive = 7
					maxIdle = 7
					minIdle = 2
					maxWait = Duration.ofMillis(-1)
				}
			}
			sentinel {
				master = "mymaster"
				node("localhost", 1234)
				node("localhost", 1235)
				node("localhost", 1236)
			}
		}
	}
}
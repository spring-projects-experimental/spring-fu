package org.springframework.fu.kofu.samples

import com.datastax.oss.driver.api.core.ConsistencyLevel
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.cassandra.cassandra
import org.springframework.fu.kofu.cassandra.reactiveCassandra
import java.time.Duration

fun cassandra() {
	application {
		cassandra {
			localDatacenter = "datacenter1"
			keyspaceName = "keyspaceOne"
			sessionName = "cluster"
			username = "user"
			password = "password"
			ssl = false
			contactPoints = listOf("localhost")
			compression = CassandraProperties.Compression.NONE
			request {
				consistency = DefaultConsistencyLevel.ALL as DefaultConsistencyLevel // Casting due to https://youtrack.jetbrains.com/issue/KT-35021
				serialConsistency = ConsistencyLevel.ALL as DefaultConsistencyLevel // Casting due to https://youtrack.jetbrains.com/issue/KT-35021
				timeout = Duration.ofSeconds(2)
				pageSize = 5000
			}
			connection {
				connectTimeout = Duration.ofSeconds(1)
			}
		}
	}
}

fun reactiveCassandra() {
	application {
		reactiveCassandra {
			localDatacenter = "datacenter1"
			keyspaceName = "keyspaceOne"
			sessionName = "cluster"
			username = "user"
			password = "password"
			ssl = false
			contactPoints = listOf("localhost")
			compression = CassandraProperties.Compression.NONE
			request {
				consistency = DefaultConsistencyLevel.ALL as DefaultConsistencyLevel // Casting due to https://youtrack.jetbrains.com/issue/KT-35021
				serialConsistency = ConsistencyLevel.ALL as DefaultConsistencyLevel // Casting due to https://youtrack.jetbrains.com/issue/KT-35021
				timeout = Duration.ofSeconds(2)
				pageSize = 5000
			}
			connection {
				connectTimeout = Duration.ofSeconds(1)
			}
		}
	}
}

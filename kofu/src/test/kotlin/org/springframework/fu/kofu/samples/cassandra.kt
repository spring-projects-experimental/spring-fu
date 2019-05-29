package org.springframework.fu.kofu.samples

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.ProtocolOptions
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.cassandra.cassandra
import org.springframework.fu.kofu.cassandra.reactiveCassandra
import java.time.Duration

fun cassandra() {
	application(WebApplicationType.NONE) {
		cassandra {
			keyspaceName = "keyspaceOne"
			clusterName = "cluster"
			username = "user"
			password = "password"
			port = 9042
			ssl = false
			contactPoints = listOf("localhost")
			compression = ProtocolOptions.Compression.NONE
			consistencyLevel = ConsistencyLevel.ANY
			serialConsistencyLevel = ConsistencyLevel.SERIAL
			connectTimeout = Duration.ofSeconds(1)
			fetchSize = 5000
			readTimeout = Duration.ofSeconds(2)
			jmxEnabled = false
		}
	}
}

fun reactiveCassandra() {
	application(WebApplicationType.NONE) {
		reactiveCassandra {
			keyspaceName = "keyspaceOne"
			clusterName = "cluster"
			username = "user"
			password = "password"
			port = 9042
			ssl = false
			contactPoints = listOf("localhost")
			compression = ProtocolOptions.Compression.NONE
			consistencyLevel = ConsistencyLevel.ANY
			serialConsistencyLevel = ConsistencyLevel.SERIAL
			connectTimeout = Duration.ofSeconds(1)
			fetchSize = 5000
			readTimeout = Duration.ofSeconds(2)
			jmxEnabled = false
		}
	}
}

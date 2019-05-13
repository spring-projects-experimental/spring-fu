package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.cassandra.reactiveCassandra

fun cassandra() {
	application(WebApplicationType.NONE) {
		reactiveCassandra {
			keyspaceName = "keyspaceOne"
			clusterName = "cluster"
			username = "user"
			password = "password"
			port = 9042
			ssl = false
			contactPoints.add("localhost")
		}
	}
}


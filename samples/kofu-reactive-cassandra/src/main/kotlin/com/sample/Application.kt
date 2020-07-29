/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sample

import org.springframework.fu.kofu.reactiveWebApplication
import org.testcontainers.containers.CassandraContainer

fun app(properties: ApplicationProperties) = reactiveWebApplication {
	with(configurationProperties(properties, prefix = "sample")) {
		enable(dataConfig(cassandraHost, cassandraPort))
		enable(webConfig(serverPort))
	}
}

fun main() {
	class KCassandraContainer : CassandraContainer<KCassandraContainer>() // https://github.com/testcontainers/testcontainers-java/issues/318
	val cassandraContainer = KCassandraContainer().withInitScript("schema.cql")
	cassandraContainer.start()
	val properties = ApplicationProperties(
			cassandraHost = cassandraContainer.containerIpAddress,
			cassandraPort = cassandraContainer.firstMappedPort)
	app(properties).run()
}

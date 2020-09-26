package com.sample

import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.jdbc.DataSourceType
import org.springframework.fu.kofu.jooq.jooq
import org.springframework.fu.kofu.webmvc.webMvc

val dataConfig = configuration {
	beans {
		bean<UserRepository>()
	}
	jooq(DataSourceType.Generic) {
		url = "jdbc:h2:mem:vet-clinic-generation"
		schema = listOf("classpath:db/schema.sql")
		generateUniqueName = false
		name = "minimal-jooq"
	}
}

val webConfig = configuration {
	beans {
		bean<UserHandler>()
		bean(::routes)
	}
	webMvc {
		port = if (profiles.contains("test")) 8181 else 8080
		converters {
			string()
			jackson()
		}
	}
}

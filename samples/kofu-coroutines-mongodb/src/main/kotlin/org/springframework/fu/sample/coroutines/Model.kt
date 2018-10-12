package org.springframework.fu.sample.coroutines

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
	@Id val login: String,
	val firstname: String,
	val lastname: String
)
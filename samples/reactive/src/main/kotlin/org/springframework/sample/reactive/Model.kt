package org.springframework.sample.reactive

import org.springframework.data.annotation.Id

data class User(
	@Id val login: String,
	val firstname: String,
	val lastname: String
)
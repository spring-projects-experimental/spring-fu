package com.sample

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
		@Id
		val login: String,
		val firstname: String,
		val lastname: String
)
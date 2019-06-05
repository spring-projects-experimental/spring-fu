package com.sample

const val KEY = "users"

data class User(
		val login: String,
		val firstname: String,
		val lastname: String
)
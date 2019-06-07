package com.sample

import java.io.Serializable

const val KEY = "users"

data class User(
		val login: String,
		val firstname: String,
		val lastname: String
) : Serializable
package com.sample

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.function.CoDatabaseClient
import org.springframework.data.r2dbc.function.DatabaseClient

class UserRepository(
		private val client: CoDatabaseClient,
		private val objectMapper: ObjectMapper
) {

	suspend fun count() = client.execute().sql("SELECT COUNT(*) FROM users").fetch().one()

	suspend fun findAll() = client.select().from("users").asType(User::class).fetch().all()

	suspend fun findOne(id: String) = client.execute().sql("SELECT * FROM users WHERE login = \$1").bind(1, id).fetch().one()

	suspend fun deleteAll() = client.execute().sql("DELETE FROM users").fetch().one()

	suspend fun save(user: User) = client.insert().into(User::class).table("users").using(user).exchange()
			.extract { r, _ -> r.get("login", String::class.java) }.all()

	suspend fun init() {
		client.execute().sql("CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);").fetch().one()
		deleteAll()
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach {
			save(it)
		}
	}

}
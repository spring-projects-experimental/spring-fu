package com.sample

import org.springframework.data.r2dbc.function.DatabaseClient
import org.springframework.data.r2dbc.function.asType
import org.springframework.data.r2dbc.function.into

class UserRepository(private val client: DatabaseClient) {

	fun count() =
			client.execute().sql("SELECT COUNT(*) FROM users").asType<Long>().fetch().one()

	fun findAll() =
			client.select().from("users").asType<User>().fetch().all()

	fun findOne(id: String) =
			client.execute().sql("SELECT * FROM users WHERE login = :login").bind("login", id).asType<User>().fetch().one()

	fun deleteAll() =
			client.execute().sql("DELETE FROM users").fetch().one().then()

	fun save(user: User) =
			client.insert().into<User>().table("users").using(user).then()

	fun init() {
		client.execute().sql("CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);").then()
				.then(deleteAll())
				.then(save(User("smaldini", "Stéphane", "Maldini")))
				.then(save(User("sdeleuze", "Sébastien", "Deleuze")))
				.then(save(User("bclozel", "Brian", "Clozel")))
				.block()
	}

}
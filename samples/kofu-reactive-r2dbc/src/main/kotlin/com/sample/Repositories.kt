package com.sample

import org.springframework.data.r2dbc.function.DatabaseClient

class UserRepository(private val client: DatabaseClient) {

	fun count() = client.execute().sql("SELECT COUNT(*) FROM users").asType(Int::class).fetch().one()

	fun findAll() = client.select().from("users").asType(User::class).fetch().all()

	fun findOne(id: String) = client.execute().sql("SELECT * FROM users WHERE login = \$1").bind(1, id).asType(User::class).fetch().one()

	fun deleteAll() = client.execute().sql("DELETE FROM users").fetch().one().then()

	fun save(user: User) = client.insert().into(User::class).table("users").using(user)
			.map {r, _ -> r.get("login", String::class) }.one()

	fun init() {
		client.execute().sql("CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);").then()
				.then(deleteAll())
				.then(save(User("smaldini", "Stéphane", "Maldini")))
				.then(save(User("sdeleuze", "Sébastien", "Deleuze")))
				.then(save(User("bclozel", "Brian", "Clozel")))
				.block()
	}

}
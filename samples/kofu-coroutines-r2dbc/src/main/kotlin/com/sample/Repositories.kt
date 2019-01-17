package com.sample

import org.springframework.data.r2dbc.function.CoDatabaseClient

class UserRepository(private val client: CoDatabaseClient) {

	suspend fun count() =
			client.execute().sql("SELECT COUNT(*) FROM users").asType(Int::class).fetch().one()!!

	suspend fun findAll() = client.select().from("users").asType(User::class).fetch().all()

	suspend fun findOne(id: String) =
			client.execute().sql("SELECT * FROM users WHERE login = \$1").bind(1, id).asType(User::class).fetch().one()!!

	suspend fun deleteAll() {
		client.execute().sql("DELETE FROM users").execute()
	}

	suspend fun save(user: User) {
		client.insert().into(User::class).table("users").using(user).execute()
	}

	suspend fun init() {
		client.execute().sql("CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);").execute()
		deleteAll()
		save(User("smaldini", "Stéphane", "Maldini"))
		save(User("sdeleuze", "Sébastien", "Deleuze"))
		save(User("bclozel", "Brian", "Clozel"))
	}
}
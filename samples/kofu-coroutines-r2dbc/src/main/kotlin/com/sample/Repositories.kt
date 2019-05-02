package com.sample

import kotlinx.coroutines.FlowPreview
import org.springframework.data.r2dbc.function.DatabaseClient
import org.springframework.data.r2dbc.function.asType
import org.springframework.data.r2dbc.function.await
import org.springframework.data.r2dbc.function.awaitOne
import org.springframework.data.r2dbc.function.flow
import org.springframework.data.r2dbc.function.into

class UserRepository(private val client: DatabaseClient) {

	suspend fun count() =
			client.execute().sql("SELECT COUNT(*) FROM users").asType<Long>().fetch().awaitOne()

	@FlowPreview
	fun findAll() = client.select().from("users").asType<User>().fetch().flow()

	suspend fun findOne(id: String) =
			client.execute().sql("SELECT * FROM users WHERE login = :login").bind("login", id).asType<User>().fetch().awaitOne()

	suspend fun deleteAll() =
		client.execute().sql("DELETE FROM users").await()

	suspend fun save(user: User)=
		client.insert().into<User>().table("users").using(user).await()


	suspend fun init() {
		client.execute().sql("CREATE TABLE IF NOT EXISTS users (login varchar PRIMARY KEY, firstname varchar, lastname varchar);").await()
		deleteAll()
		save(User("smaldini", "Stéphane", "Maldini"))
		save(User("sdeleuze", "Sébastien", "Deleuze"))
		save(User("bclozel", "Brian", "Clozel"))
	}
}
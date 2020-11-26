package com.sample

import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono


class UserRepository(private val client: DatabaseClient) {

	suspend fun count() =
			client.sql("SELECT COUNT(login) FROM users")
					.map { row -> (row.get(0) as Long).toInt() }
					.first().awaitSingle()


	suspend fun findAll() =
			client.sql("SELECT login, firstname, lastname from users")
					.map { row ->
						User(
								login = row.get("login", String::class.java)!!,
								firstname = row.get("firstname", String::class.java)!!,
								lastname = row.get("lastname", String::class.java)!!
						)
					}
					.all()
					.asFlow()

	suspend fun findOne(id: String?) =
			id?.let {
				client.sql("SELECT login, firstname, lastname from users where login = :id")
						.bind("id", it)
						.map { row ->
							User(
									login = row.get("login", String::class.java)!!,
									firstname = row.get("firstname", String::class.java)!!,
									lastname = row.get("lastname", String::class.java)!!
							)
						}
						.first().awaitSingle()
			}


	suspend fun deleteAll() =
			client.sql("DELETE FROM users").then().awaitSingle()


	suspend fun insert(user: User) =
			client.sql("INSERT INTO users(login, firstname, lastname) values(:login, :firstname, :lastname)")
					.bind("login", user.login)
					.bind("firstname", user.firstname)
					.bind("lastname", user.lastname)
					.then()
					.awaitSingle()

}
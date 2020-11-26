package com.sample

import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono

class UserRepository(private val client: DatabaseClient) {

    fun count() =
            client.sql("SELECT COUNT(login) FROM users")
                    .map { row -> (row.get(0) as Long).toInt() }
                    .first()


    fun findAll() =
            client.sql("SELECT login, firstname, lastname from users")
                    .map { row ->
                        User(
                                login = row.get("login", String::class.java)!!,
                                firstname = row.get("firstname", String::class.java)!!,
                                lastname = row.get("lastname", String::class.java)!!
                        )
                    }
                    .all()

    fun findOne(id: String?) =
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
                    .first()
            } ?: Mono.empty()


    fun deleteAll() =
            client.sql("DELETE FROM users").then()


    fun insert(user: User) =
            client.sql("INSERT INTO users(login, firstname, lastname) values(:login, :firstname, :lastname)")
                    .bind("login", user.login)
                    .bind("firstname", user.firstname)
                    .bind("lastname", user.lastname)

}
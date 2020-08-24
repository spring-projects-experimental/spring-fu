package com.sample

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query

class UserRepository(private val operations: R2dbcEntityOperations) {

	fun count() =
			operations.count(Query.empty(), User::class.java)


	fun findAll() =
			operations.select(Query.empty(), User::class.java)


	fun findOne(id: String?) =
			operations.select(User::class.java).matching(Query.query(Criteria.where("login").`is`(id!!))).one()


	fun deleteAll() =
		operations.delete(User::class.java).all().then()


	fun insert(user: User) =
			operations.insert(User::class.java).using(user)

}
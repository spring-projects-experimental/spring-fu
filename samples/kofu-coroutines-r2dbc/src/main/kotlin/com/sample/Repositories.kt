package com.sample

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.empty
import org.springframework.data.relational.core.query.Query.query

class UserRepository(private val operations: R2dbcEntityOperations) {

	suspend fun count(): Long =
		operations.count(empty(), User::class.java).awaitSingle()


	fun findAll(): Flow<User> =
		operations.select(empty(), User::class.java).asFlow()


	suspend fun findOne(id: String?): User =
		operations.select(User::class.java).matching(query(where("login").`is`(id!!))).one().awaitSingle()


	suspend fun deleteAll() {
		operations.delete(User::class.java).all().awaitSingle()
	}

	suspend fun insert(user: User): User =
		operations.insert(User::class.java).using(user).awaitSingle()

}
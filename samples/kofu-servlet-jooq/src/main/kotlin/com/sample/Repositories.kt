package com.sample

import com.sample.Tables.USERS
import org.jooq.DSLContext
import org.jooq.impl.DSL

/**
 * @author Kevin Davin
 */
class UserRepository(private val query: DSLContext) {

	fun count(): Long {
		return query
				.selectCount()
				.from(USERS)
				.fetchOne(DSL.count())
				?.toLong() ?: 0L
	}

	fun findAll(): List<User> = query
			.select(USERS.LOGIN, USERS.FIRSTNAME, USERS.LASTNAME)
			.from(USERS)
			.fetch { (login, firstName, lastName) -> User(login, firstName, lastName) }

	fun findOne(login: String?): User? = query
			.select(USERS.LOGIN, USERS.FIRSTNAME, USERS.LASTNAME)
			.from(USERS)
			.where(USERS.LOGIN.eq(login))
			.fetchOne { (login, firstName, lastName) -> User(login, firstName, lastName) }

	fun deleteAll() {
		query
				.deleteFrom(USERS)
				.execute()
	}

	fun insert(user: User): User {
		query
				.insertInto(USERS)
				.set(USERS.LOGIN, user.login)
				.set(USERS.FIRSTNAME, user.firstname)
				.set(USERS.LASTNAME, user.lastname)
				.execute()

		return user
	}
}

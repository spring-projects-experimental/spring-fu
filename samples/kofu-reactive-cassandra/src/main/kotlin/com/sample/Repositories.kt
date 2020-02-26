package com.sample

import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.io.ClassPathResource
import org.springframework.data.cassandra.core.*
import org.springframework.data.cassandra.core.query.Query

class UserRepository(
		private val template: ReactiveCassandraTemplate,
		private val objectMapper: ObjectMapper) {

	fun count() = template.count<User>()

	fun findAll() = template.select<User>(Query.empty())

	fun findOne(id: String) = template.selectOne<User>(
			QueryBuilder.selectFrom("users").all().whereColumn("login").isEqualTo(QueryBuilder.literal(id)).build()
	)

	fun deleteAll() = template.delete<User>().inTable("users").matching(Query.empty()).all()

	fun save(user: User) = template.insert<User>().inTable("users").one(user).map { it.entity }

	fun init() {
		val eventsResource = ClassPathResource("data/users.json")
		val users: List<User> = objectMapper.readValue(eventsResource.inputStream)
		users.forEach {
			save(it).subscribe()
		}

	}

}

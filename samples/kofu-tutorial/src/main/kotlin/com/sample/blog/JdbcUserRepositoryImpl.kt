package com.sample.blog

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.sql.ResultSet
import javax.sql.DataSource

class JdbcUserRepositoryImpl(dataSource: DataSource): UserRepository {

    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)

    private val insertUser = SimpleJdbcInsert(dataSource)
        .withTableName("user")
        .usingGeneratedKeyColumns("id")

    override fun findByLogin(login: Login): UserEntity? = firstOrNull("login", login.value)
    override fun findById(id: Id<Long>): UserEntity? = firstOrNull("id", id.value)
    override fun findAll(): Collection<UserEntity> =
        jdbcTemplate.query("select * from user") { rs, _ -> toUser(rs) }

    override fun save(user: Entity<User>): UserEntity = when(user){
        is Entity.New ->{
            insertUser
                .executeAndReturnKey(getUserParameters(user.info))
                .let { id -> Entity.WithId(Id(id.toLong()), user.info) }
        }
        is Entity.WithId -> jdbcTemplate
            .update(
                "update user set login=:login, firstname=:firstname, lastname=:lastname where id=:id",
                getUserParameters(user.info).toMutableMap().also { it["id"] = "${user.id.value}" })
            .let { user }
    }

    private fun getUserParameters(user: User): Map<String, Any> = with(user) {
        mapOf(
            "login" to login.value,
            "firstname" to name.firstname,
            "lastname" to name.lastname
        )
    }

    private fun firstOrNull(paramName: String, value: Any) = jdbcTemplate
        .query("select * from user where $paramName=:$paramName", mapOf(paramName to value)) { rs, _ ->
            toUser(rs)
        }
        .firstOrNull()

    private fun toUser(rs: ResultSet) = Entity.WithId(
        Id(rs.getLong("id")),
        User.of(
            rs.getString("login"),
            rs.getString("firstname"),
            rs.getString("lastname")
        )
    )
}
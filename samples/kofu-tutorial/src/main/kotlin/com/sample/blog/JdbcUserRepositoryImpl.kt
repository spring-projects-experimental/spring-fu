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

    override fun save(user: Entity<User>): UserEntity {
        val parameters = with(user.info) {
            mapOf(
                "login" to login.value,
                "firstname" to name.firstname,
                "lastname" to name.lastname
            )
        }
        return when(user){
            is Entity.New ->{
                insertUser
                    .executeAndReturnKey(parameters)
                    .let { id -> Entity.WithId(Id(id.toLong()), user.info) }
            }
            is Entity.WithId -> jdbcTemplate
                .update(
                    "update user set login=:login, firstname=:firstname, lastname=:lastname where id=:id",
                    parameters.toMutableMap().also { it["id"] = "${user.id.value}" })
                .let { user }
        }
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
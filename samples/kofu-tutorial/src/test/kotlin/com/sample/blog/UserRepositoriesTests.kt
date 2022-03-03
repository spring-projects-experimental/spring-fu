package com.sample.blog

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class UserRepositoriesTests {
    private val dataSource: DataSource = JdbcTestsHelper.getDataSource()

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val repoHelper = JdbcTestsHelper(dataSource)

    private val userRepository: UserRepository = JdbcUserRepositoryImpl(dataSource)

    private val luca = User.of("springluca", "Luca", "Piccinelli")

    @BeforeEach
    internal fun setUp() {
        repoHelper.createUserTable()
    }

    @AfterEach
    internal fun tearDown() {
        repoHelper.dropDb()
    }

    @Test
    fun `When findByLogin then return User`() {
        repoHelper.insertUser(luca)
        val user = userRepository.findByLogin(luca.login)
        user?.info shouldBe luca
    }

    @Test
    fun `When findAll then return a collection of users`() {
        repoHelper.insertUser(luca)
        val users = userRepository.findAll()
        users.map { it.info }.toList() shouldBe listOf(luca)
    }

    @Test
    fun `When saving the user should exist`() {
        val user = userRepository.save(Entity.New(luca))
        val login = getLogin(user)
        user.info.login.value shouldBe login
    }

    @Test
    fun `When updating user its data should change`() {
        val userId = repoHelper.insertUser(luca)

        val newLogin = "banana"
        val user = userRepository.save(Entity.WithId(Id(userId.toLong()), luca.copy(login = Login.of(newLogin))))
        val login = getLogin(user)

        user.info.login.value shouldBe newLogin
        login shouldBe newLogin
    }

    private fun getLogin(user: UserEntity): String =
        jdbcTemplate
            .query("select * from user where id=${user.id.value}") { rs, _ -> rs.getString("login") }
            .first()
}


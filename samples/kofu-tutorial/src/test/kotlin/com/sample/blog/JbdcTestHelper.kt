package com.sample.blog

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import javax.sql.DataSource


class JdbcTestsHelper(private val dataSource: DataSource) {
    companion object{
        val h2Url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;"

        fun getDataSource(): DataSource {
            return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .username("sa")
                .password("")
                .url(h2Url)
                .build()
        }

        val luca = User.of("springluca", "Luca", "Piccinelli")
        val article1 = Article(
            "Spring Kotlin DSL is amazing",
            "Dear Spring community ...",
            "Lorem ipsum",
            { Entity.New(luca) })
    }

    private val jdbcTemplate = JdbcTemplate(dataSource)

    private val insertUser = SimpleJdbcInsert(dataSource)
        .withTableName("USER")
        .usingGeneratedKeyColumns("id")

    private val insertArticle = SimpleJdbcInsert(dataSource)
        .withTableName("ARTICLE")
        .usingGeneratedKeyColumns("id")

    fun insertUser(user: User): Number = insertUser.executeAndReturnKey(mapOf(
        "login" to user.login.value,
        "firstname" to user.name.firstname,
        "lastname" to user.name.lastname)
    )

    fun insertArticle(article: Article<Entity.New<User>>): Pair<Number, Number> {
        val userId = insertUser(article.author.info)

        return userId to insertArticle.executeAndReturnKey(mapOf(
            "title" to article.title,
            "headline" to article.headline,
            "content" to article.content,
            "slug" to article.slug,
            "added_at" to article.addedAt,
            "author_id" to userId)
        )
    }

    fun createUserTable() = jdbcTemplate.execute(
        """create table if not exists user(
                    id IDENTITY PRIMARY KEY, 
                    login VARCHAR NOT NULL,
                    firstname VARCHAR NOT NULL,
                    lastname VARCHAR NOT NULL,
                    description VARCHAR
                )"""
    )

    fun createArticleTable() = jdbcTemplate.execute(
        """create table if not exists article(
                    id IDENTITY PRIMARY KEY, 
                    title VARCHAR NOT NULL,
                    headline VARCHAR NOT NULL,
                    content VARCHAR NOT NULL,
                    slug VARCHAR NOT NULL,
                    added_at DATETIME,
                    author_id INT NOT NULL,
                    constraint FK_USER foreign key (author_id) references user(id)
                )"""
    )

    fun dropDb(){
        jdbcTemplate.execute("DROP ALL OBJECTS")
    }
}

package com.sample.blog

import org.springframework.dao.DataRetrievalFailureException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.sql.ResultSet
import javax.sql.DataSource

class JdbcArticleRepositoryImpl(dataSource: DataSource): ArticleRepository {
    private val jdbcTemplate = NamedParameterJdbcTemplate(dataSource)
    private val userRepository = JdbcUserRepositoryImpl(dataSource)

    private val insert = SimpleJdbcInsert(dataSource)
        .withTableName("article")
        .usingGeneratedKeyColumns("id")

    override fun findById(id: Id<Long>): ArticleEntity? = firstOrNull("id", id.value)
    override fun findBySlug(slug: String): ArticleEntity? = firstOrNull("slug", slug)
    override fun findAll(): Collection<ArticleEntity> =
        findAll("select * from article")

    override fun findAllByOrderByAddedAtDesc(): Collection<ArticleEntity> =
        findAll("select * from article order by added_at desc")

    override fun save(article: Entity<Article>): ArticleEntity = when (article) {
        is Entity.New -> {
            insert
                .executeAndReturnKey(getArticleParameters(article.info))
                .let { id -> Entity.WithId(Id(id.toLong()), article.info) }
        }
        is Entity.WithId -> jdbcTemplate
            .update(
                """update article set
                    |title=:title, 
                    |headline=:headline, 
                    |slug=:slug,
                    |added_at=:added_at, 
                    |content=:content,
                    |author_id=:author_id
                    |where id=:id""".trimMargin(),
                getArticleParameters(article.info).toMutableMap().also { it["id"] = "${article.id.value}" })
            .let { article }
    }

    private fun getArticleParameters(article: Article): Map<String, Any> = with(article) {
        mapOf(
            "title" to title,
            "headline" to headline,
            "slug" to slug,
            "added_at" to addedAt,
            "content" to content,
            "author_id" to author.id.value,
        )
    }

    private fun toArticle(rs: ResultSet): ArticleEntity {
        val userId = rs.getLong("author_id")
        val articleId = rs.getLong("id")
        return Entity.WithId(
            Id(articleId),
            Article(
                rs.getString("title"),
                rs.getString("headline"),
                rs.getString("content"),
                {
                    userRepository
                        .findById(userId.run(::Id))
                        ?: throw DataRetrievalFailureException("On article with id $articleId There is no user with id $userId")
                },
                rs.getString("slug"),
                rs.getTimestamp("added_at").toLocalDateTime()
            )
        )
    }

    private fun findAll(query: String) =
        jdbcTemplate.query(query) { rs, _ -> toArticle(rs) }

    private fun firstOrNull(parameterName: String, value: Any) = jdbcTemplate
        .query("select * from article where $parameterName=:$parameterName", mapOf(parameterName to value)) { rs, _ ->
            toArticle(rs)
        }
        .firstOrNull()
}
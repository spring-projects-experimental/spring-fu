package com.sample.blog

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

class ArticleRepositoriesTests {
    private val dataSource: DataSource = JdbcHelper.getDataSource()

    private val jdbcTemplate = JdbcTemplate(dataSource)
    private val repoHelper = JdbcHelper(dataSource)

    private val articleRepository = JdbcArticleRepositoryImpl(dataSource)

    private val luca = User.of("springluca", "Luca", "Piccinelli")
    private lateinit var article: Article

    @BeforeEach
    internal fun setUp() {
        repoHelper.createUserTable()
        repoHelper.createArticleTable()

        val userId = repoHelper.insertUser(luca)
        article = Article(
            "Spring Kotlin DSL is amazing",
            "Dear Spring community ...",
            "Lorem ipsum",
            { Entity.WithId(Id(userId.toLong()), luca) })
    }

    @AfterEach
    internal fun tearDown() {
        repoHelper.dropDb()
    }

    @Test
    fun `When findBySlug then return Article`() {
        val id = repoHelper.insertArticle(article)
        val articleEntity = articleRepository.findBySlug(article.slug)
        articleEntity?.id?.value shouldBe id.toLong()
    }

    @Test
    fun `When findByAll then return a collection of articles`() {
        val id = repoHelper.insertArticle(article)
        val articles = articleRepository.findAll()
        articles.map { it.id.value }.toList() shouldBe listOf(id.toLong())
    }

    @Test
    fun `When saving the user should exist`() {
        val article = articleRepository.save(Entity.New(article))
        val slug = get(article, "slug")
        article.info.slug shouldBe slug
    }

    @Test
    fun `When updating user its data should change`() {
        val articleId = repoHelper.insertArticle(article)

        val newTitle = "banana"
        val article = articleRepository.save(Entity.WithId(Id(articleId.toLong()), article.copy(title = newTitle)))
        val title = get(article, "title")

        article.info.title shouldBe newTitle
        title shouldBe newTitle
    }

    private fun get(article: ArticleEntity, name: String): String =
        jdbcTemplate
            .query("select * from article where id=${article.id.value}") { rs, _ -> rs.getString(name) }
            .first()
}

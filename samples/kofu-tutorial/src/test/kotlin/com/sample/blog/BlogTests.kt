package com.sample.blog

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import javax.sql.DataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogTests {
    private lateinit var context: ConfigurableApplicationContext
    private lateinit var client: MockMvc
    private lateinit var repoHelper: JdbcHelper

    private val author = UserEntity(Id(0), User.of("springluca", "Luca", "Piccinelli"))
    private val article = ArticleEntity(Id(0),
        Article(
            title = "Reactor Bismuth is out",
            headline = "Lorem ipsum",
            content = "dolor sit amet",
            authorFn = { author }
        )
    )

    private val app = webApplication {
        enable(blog)
        enable(datasource)
        enable(repositories)
        enable(liquibase)
        enable(populate)
        webMvc { port = (10000..10500).random() }
    }

    @BeforeAll
    internal fun setUp() {
        context = app.run()
        client = MockMvcBuilders
            .webAppContextSetup(context as WebApplicationContext)
            .build()

        repoHelper = context
            .getBean(DataSource::class.java)
            .run(::JdbcHelper)
    }

    @AfterAll
    internal fun tearDown() {
        repoHelper.dropDb()
        context.close()
    }

    @Test
    internal fun `Assert blog page title, content and status code`() {
        client.get("/")
            .andExpect {
                status { isOk() }
                content {
                    string(containsString("<h1>Blog</h1>"))
                    string(containsString("Reactor"))
                }
            }
    }

    @Test
    internal fun `Assert article page title, content and status code`() {
        val title = article.info.title

        client.get("/article/${title.toSlug()}")
            .andExpect {
                status { isOk() }
                content {
                    string(containsString(title))
                    string(containsString("Lorem ipsum"))
                    string(containsString("dolor sit amet"))
                }
            }
    }
}

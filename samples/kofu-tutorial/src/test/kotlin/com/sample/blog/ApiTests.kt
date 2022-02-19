package com.sample.blog

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.fu.kofu.KofuApplication
import org.springframework.fu.kofu.webApplication
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTests {
    lateinit var context: ConfigurableApplicationContext
    lateinit var client: MockMvc

    private val author = UserEntity(Id(0), User.of("springluca", "Luca", "Piccinelli"))
    private val article = ArticleEntity(Id(0),
        Article(
            title = "Reactor Bismuth is out",
            headline = "Lorem ipsum",
            content = "dolor sit amet",
            authorFn = { author }
        )
    )

    private val app: KofuApplication = webApplication {
        enable(blogApi)
        beans {
            bean {
                mockk<ArticleRepository> {
                    every { findAll() } returns listOf(article)
                }
            }
            bean {
                mockk<UserRepository> {
                    every { findAll() } returns listOf(author)
                }
            }
        }
        webMvc { port = (10000..10500).random() }
    }

    @BeforeAll
    internal fun setUp() {
        context = app.run()
        client = MockMvcBuilders
            .webAppContextSetup(context as WebApplicationContext)
            .build()
    }

    @AfterAll
    internal fun tearDown() {
        context.close()
    }

    @Test
    internal fun `List articles`() {
        client.get("/api/article")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("\$.[0].author.login", Matchers.equalTo(article.info.author.info.login.value))
                    jsonPath("\$.[0].slug", Matchers.equalTo(article.info.slug))
                }
            }
    }

    @Test
    internal fun `List users`() {
        client.get("/api/user")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("\$.[0].login", Matchers.equalTo(author.info.login.value))
                }
            }
    }
}
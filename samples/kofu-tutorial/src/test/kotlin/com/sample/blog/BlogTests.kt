package com.sample.blog

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogTests {
    lateinit var context: ConfigurableApplicationContext
    lateinit var client: MockMvc

    val app = webApplication {
        enable(blog)

        beans {
            bean {
                val author = UserEntity(Id(0), User.of("springluca", "Luca", "Piccinelli"))
                val article = ArticleEntity(Id(0),
                    Article(
                        title = "Reactor Bismuth is out",
                        headline = "Lorem ipsum",
                        content = "dolor sit amet",
                        authorFn = { author }
                    )
                )
                mockk<ArticleRepository> {
                    every { findAllByOrderByAddedAtDesc() } returns listOf(article)
                    every { findBySlug(article.info.slug) } returns article
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
    internal fun `Assert blog page title, content and status code`() {
        client.get("/")
            .andExpect {
                status { isOk() }
                content {
                    string(Matchers.containsString("<h1>Blog</h1>"))
                    string(Matchers.containsString("Reactor"))
                }
            }
    }

    @Test
    internal fun `Assert article page title, content and status code`() {
        val title = "Reactor Bismuth is out"

        client.get("/article/${title.toSlug()}")
            .andExpect {
                status { isOk() }
                content {
                    string(Matchers.containsString(title))
                    string(Matchers.containsString("Lorem ipsum"))
                    string(Matchers.containsString("dolor sit amet"))
                }
            }
    }
}

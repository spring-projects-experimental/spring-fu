package com.sample.blog

import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.templating.mustache
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

val blog = configuration {
    webMvc {
        mustache{
            prefix = "classpath:/views/"
        }

        router {
            val htmlHandler = HtmlHandler(ref())

            GET("/", htmlHandler::blog)
            GET("/article/{slug}", htmlHandler::article)
        }
    }
}

class HtmlHandler(
    private val articleRepository: ArticleRepository
) {
    fun blog(request: ServerRequest): ServerResponse =
        ServerResponse.ok().render(
            "blog", mapOf(
                "title" to "Blog",
                "articles" to articleRepository.findAllByOrderByAddedAtDesc().map { it.render() }
            ),
        )

    fun article(request: ServerRequest): ServerResponse = articleRepository
        .findBySlug(request.pathVariable("slug"))
        ?.render()
        ?.let { article ->
            ServerResponse.ok().render(
                "article", mapOf(
                    "title" to article.title,
                    "article" to article
                )
            )
        }
        ?: ServerResponse.notFound().build()
}

data class RenderedArticle(
    val id: Long,
    val slug: String,
    val title: String,
    val headline: String,
    val content: String,
    val author: RenderedUser,
    val addedAt: String)


fun ArticleEntity.render() = RenderedArticle(
    id.value,
    info.slug,
    info.title,
    info.headline,
    info.content,
    info.author.render(),
    info.addedAt.format()
)

data class RenderedUser(
    val id: Long,
    val login: String,
    val firstname: String,
    val lastname: String,
    val description: String?)

fun Entity.WithId<User>.render() = RenderedUser(
    id.value,
    info.login.value,
    info.name.firstname,
    info.name.lastname,
    info.description)
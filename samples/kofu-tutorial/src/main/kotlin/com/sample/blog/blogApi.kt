package com.sample.blog

import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.webmvc.webMvc
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

val blogApi = configuration {
    webMvc {
        converters {
            jackson()
        }

        router {
            val articleHandler = ArticleHandler(ref())
            val userHandler = UserHandler(ref())
            "/api".nest {
                "/article".nest {
                    GET("", articleHandler::findAll)
                    GET("/{slug}", articleHandler::findOne)
                }
                "/user".nest {
                    GET("", userHandler::findAll)
                    GET("/{login}", userHandler::findOne)
                }
            }
        }
    }
}

class UserHandler(private val userRepository: UserRepository) {
    fun findAll(serverRequest: ServerRequest): ServerResponse = userRepository.findAll()
        .map { it.render() }
        .run(::ok)

    fun findOne(serverRequest: ServerRequest): ServerResponse = userRepository
        .findByLogin(serverRequest.pathVariable("login").run(Login::of))
        ?.render()
        ?.run(::ok)
        ?: ServerResponse.notFound().build()
}

class ArticleHandler(private val articleRepository: ArticleRepository) {
    fun findAll(serverRequest: ServerRequest): ServerResponse = articleRepository.findAll()
        .map { it.render() }
        .run(::ok)

    fun findOne(serverRequest: ServerRequest): ServerResponse = articleRepository
        .findBySlug(serverRequest.pathVariable("slug"))
        ?.render()
        ?.run(::ok)
        ?: ServerResponse.notFound().build()
}

private fun ok(body: Any): ServerResponse = ServerResponse.ok().body(body)

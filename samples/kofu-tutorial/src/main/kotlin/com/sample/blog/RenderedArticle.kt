package com.sample.blog

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
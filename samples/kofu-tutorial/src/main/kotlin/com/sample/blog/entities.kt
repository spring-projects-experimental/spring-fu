package com.sample.blog

import java.time.LocalDateTime


sealed class Entity<out T>{
    data class WithId<out T>(val id: Id<Long>, val info: T) : Entity<T>()
    data class New<out T>(val info: T) : Entity<T>()
}

data class Id<T>(val value: T)

data class Name(
    val firstname: String,
    val lastname: String,
)

@JvmInline
value class Login private constructor(val value: String){
    companion object{
        fun of(value: String): Login{
            require(value.isNotEmpty()){ "can't accept an empty login value" }
            return Login(value)
        }
    }
}

data class User(
    val login: Login,
    val name: Name,
    val description: String? = null){

    companion object{
        fun of(
            login: String,
            firstname: String,
            lastname: String,
            description: String? = null) =
            User(Login.of(login), Name(firstname, lastname), description)
    }
}

data class Article(
    val title: String,
    val headline: String,
    val content: String,
    private val authorFn: () -> UserEntity,
    val slug: String = title.toSlug(),
    val addedAt: LocalDateTime = LocalDateTime.now().withNano(0)
){
    val author by lazy(authorFn)
}

typealias UserEntity = Entity.WithId<User>
typealias ArticleEntity = Entity.WithId<Article>
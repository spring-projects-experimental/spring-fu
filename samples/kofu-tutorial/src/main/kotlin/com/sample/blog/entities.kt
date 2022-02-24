package com.sample.blog

import java.time.LocalDateTime


sealed class Entity<out T>(open val info: T){
    data class Existing<out T>(val id: Id<Int>, override val info: T) : Entity<T>(info)
    data class New<out T>(override val info: T) : Entity<T>(info)
}

data class Id<T>(val value: T)

data class Name(
    val firstname: String,
    val lastname: String,
)

@JvmInline
value class Login(val value: String)

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
            User(Login(login), Name(firstname, lastname), description)
    }
}

data class Article<out T : Entity<User>>(
    val title: String,
    val headline: String,
    val content: String,
    private val userFn: () -> T,
    val slug: String = title.toSlug(),
    val addedAt: LocalDateTime = LocalDateTime.now().withNano(0)
){
    fun <U: Entity<User>> withUser(user: U): Article<U> =
        Article(title, headline, content, { user }, slug, addedAt)

    fun existing(id: Id<Int>) = Entity.Existing(id, Article(title, headline, content, userFn, slug, addedAt))

    val user by lazy(userFn)
}
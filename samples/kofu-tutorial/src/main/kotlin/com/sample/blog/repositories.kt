package com.sample.blog

interface UserRepository {
    fun save(user: Entity<User>): UserEntity
    fun findAll(): Collection<UserEntity>
    fun findById(id: Id<Long>): UserEntity?
    fun findByLogin(login: Login): UserEntity?
}

interface ArticleRepository {
    fun save(article: Entity<Article>): ArticleEntity
    fun findAll(): Collection<ArticleEntity>
    fun findAllByOrderByAddedAtDesc(): Collection<ArticleEntity>
    fun findById(id: Id<Long>): ArticleEntity?
    fun findBySlug(slug: String): ArticleEntity?
}

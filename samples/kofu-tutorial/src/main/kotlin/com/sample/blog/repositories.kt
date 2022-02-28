package com.sample.blog

interface UserRepository {
    fun save(entity: Entity<User>): Entity.WithId<User>
    fun findAll(): Collection<Entity.WithId<User>>
    fun findById(id: Id<Long>): Entity.WithId<User>?
    fun findByLogin(login: Login): Entity.WithId<User>?
}

interface ArticleRepository {
    fun save(entity: Entity<Article<Entity<User>>>): ArticleEntity
    fun findAll(): Collection<ArticleEntity>
    fun findAllByOrderByAddedAtDesc(): Collection<ArticleEntity>
    fun findById(id: Id<Long>): ArticleEntity?
    fun findBySlug(slug: String): ArticleEntity?
}

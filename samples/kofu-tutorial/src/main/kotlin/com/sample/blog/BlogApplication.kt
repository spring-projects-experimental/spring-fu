package com.sample.blog

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.core.env.get
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.jdbc.DataSourceType
import org.springframework.fu.kofu.jdbc.jdbc
import org.springframework.fu.kofu.webApplication

val datasource = configuration {
    jdbc(DataSourceType.Hikari){
        url = env["datasource.url"]
        driverClassName = env["datasource.driver"]
        username = env["datasource.username"]
        password = env["datasource.password"]
    }
}

val repositories = configuration {
    beans {
        bean { JdbcUserRepositoryImpl(ref()) }
        bean { JdbcArticleRepositoryImpl(ref()) }
    }
}

val createDb = configuration {
    listener<ApplicationReadyEvent> {
        JdbcHelper(ref()).apply {
            createUserTable()
            createArticleTable()
        }
    }
}

val populate = configuration {
    listener<ApplicationReadyEvent> {
        val userRepository: UserRepository = ref()
        val articleRepository: ArticleRepository = ref()

        val author = Entity.New(User.of("springluca", "Luca", "Piccinelli"))
        val persistedAuthor = userRepository.save(author)
        articleRepository.save(Entity.New(
            Article(
                title = "Reactor Bismuth is out",
                headline = "Lorem ipsum",
                content = "dolor sit amet",
                authorFn = { persistedAuthor }
            )
        ))
        articleRepository.save(
            Entity.New(
                Article(
                    title = "Reactor Aluminium has landed",
                    headline = "Lorem ipsum",
                    content = "dolor sit amet",
                    authorFn = { persistedAuthor }
                )
            )
        )
    }
}

val app = webApplication {
    enable(createDb)
    profile("dev"){
        enable(populate)
    }
    enable(repositories)
    enable(datasource)
    enable(blog)
    enable(blogApi)
}

fun main(args: Array<String>) {
    app.run(args, profiles = "dev")
}

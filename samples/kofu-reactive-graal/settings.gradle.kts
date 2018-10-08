pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.spring.io/milestone")
        maven("http://dl.bintray.com/kotlin/kotlin-eap")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.springframework.boot") {
                useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
            }
            if (requested.id.id == "org.jetbrains.kotlin.jvm") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}
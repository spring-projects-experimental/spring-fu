import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.31"
	id("io.spring.dependency-management") version "1.0.7.RELEASE"
	id("org.springframework.boot") version "2.2.0.M3"
}

dependencies {
	implementation("com.expedia:graphql-kotlin:0.4.1") {
		exclude("com.graphql-java", "graphql-java")
		exclude("org.jetbrains.kotlinx")
	}
	implementation("com.graphql-java:graphql-java:2019-06-12T04-51-56-e994f41")
	implementation("org.webjars:graphiql:0.11.11")
	implementation("org.springframework.fu:spring-fu-kofu:0.2.BUILD-SNAPSHOT")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

configurations.all {
	exclude(module = "jakarta.validation-api")
	exclude(module = "hibernate-validator")
}
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
	id("org.jetbrains.kotlin.jvm")
	id("io.spring.dependency-management")
	id("org.jetbrains.dokka")
	id("java-library")
}

dependencies {
	api("org.springframework.boot:spring-boot")
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation(project(":autoconfigure-adapter"))
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	compileOnly("org.springframework:spring-webmvc")
	compileOnly("org.springframework:spring-webflux")
	compileOnly("org.springframework.security:spring-security-web")
	compileOnly("org.springframework.security:spring-security-config")
	compileOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	compileOnly("org.springframework.data:spring-data-mongodb")
	compileOnly("org.springframework.data:spring-data-r2dbc")
	compileOnly("org.jooq:jooq")
	compileOnly("org.mongodb:mongodb-driver-reactivestreams")
	compileOnly("org.springframework.data:spring-data-cassandra")
	compileOnly("org.springframework.data:spring-data-redis")
	compileOnly("org.springframework.data:spring-data-elasticsearch")
	compileOnly("com.fasterxml.jackson.core:jackson-databind")
	compileOnly("com.samskivert:jmustache")
	compileOnly("io.projectreactor.kotlin:reactor-kotlin-extensions")
	compileOnly("javax.servlet:javax.servlet-api")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-undertow")
	testImplementation("org.springframework.boot:spring-boot-starter-jetty")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	testImplementation("org.springframework.boot:spring-boot-starter-mustache")
	testImplementation("org.springframework.boot:spring-boot-starter-json")
	testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	testImplementation("org.springframework.boot:spring-boot-starter-data-cassandra-reactive")
	testImplementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
	testImplementation("org.springframework.boot:spring-boot-starter-jooq")
	testImplementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	testImplementation("io.mockk:mockk:1.9")
	testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	testImplementation("javax.servlet:javax.servlet-api")
	testCompile("org.testcontainers:testcontainers")
	testImplementation("redis.clients:jedis")
	testImplementation("io.lettuce:lettuce-core")
	testImplementation("org.springframework:spring-r2dbc")
	testImplementation("org.springframework.data:spring-data-elasticsearch")
	testRuntimeOnly("io.r2dbc:r2dbc-h2")
	testRuntimeOnly("io.r2dbc:r2dbc-postgresql:0.8.4.RELEASE")
	testRuntimeOnly("org.postgresql:postgresql:42.2.18")
	testRuntimeOnly("mysql:mysql-connector-java:8.0.22")

	testImplementation("org.mongodb:mongodb-driver-legacy")
}

tasks.withType<Test> {
	if (project.hasProperty("isCI")) {
		exclude("org/springframework/fu/kofu/elasticsearch/ElasticSearchDslTest.class")
		exclude("org/springframework/fu/kofu/elasticsearch/ReactiveElasticSearchDslTest.class")
		exclude("org/springframework/fu/kofu/redis/ReactiveRedisDslTests.class")
		exclude("org/springframework/fu/kofu/redis/RedisDslTests.class")
		exclude("org/springframework/fu/kofu/r2dbc/DataR2dbcDslTest.class")
		exclude("org/springframework/fu/kofu/r2dbc/PostgreSqlR2dbcDslTests.class")
		exclude("org/springframework/fu/kofu/jdbc/JdbcDslTests.class")
		exclude("org/springframework/fu/kofu/jooq/JooqDslTest.class")
	}
}

tasks.withType<DokkaTask>().configureEach {
	dokkaSourceSets {
		named("main") {
			samples.from(fileTree("src/test/kotlin/org/springframework/fu/kofu/samples"))
			reportUndocumented.set(false)
			externalDocumentationLink {
				url.set(URL("https://docs.spring.io/spring-framework/docs/current/javadoc-api/"))
			}
			externalDocumentationLink {
				url.set(URL("https://docs.spring.io/spring-framework/docs/current/kdoc-api/spring-framework/"))
			}
			externalDocumentationLink {
				url.set(URL("https://docs.spring.io/spring-security/site/docs/current/api/"))
			}
			externalDocumentationLink {
				url.set(URL("https://fasterxml.github.io/jackson-core/javadoc/2.9/"))
			}
			externalDocumentationLink {
				url.set(URL("https://fasterxml.github.io/jackson-annotations/javadoc/2.9/"))
			}
			externalDocumentationLink {
				url.set(URL("https://fasterxml.github.io/jackson-databind/javadoc/2.9/"))
			}
			externalDocumentationLink {
				url.set(URL("https://docs.spring.io/spring-data/mongodb/docs/2.2.x/api/"))
			}
			externalDocumentationLink {
				url.set(URL("https://docs.oracle.com/javase/8/docs/api/"))
			}
			externalDocumentationLink {
				url.set(URL("https://docs.spring.io/spring-boot/docs/2.2.x/api/"))
			}
			externalDocumentationLink {
				url.set(URL("https://docs.spring.io/spring-data/r2dbc/docs/1.1.x/api/"))
			}
			externalDocumentationLink {
				url.set(URL("https://docs.spring.io/spring-data/cassandra/docs/current/api/"))
			}
		}
	}
}

publishing {
	publications {
		create<MavenPublication>(project.name) {
			from(components["java"])
			artifactId = "spring-fu-kofu"
			val sourcesJar by tasks.creating(Jar::class) {
				archiveClassifier.set("sources")
				from(sourceSets["main"].allSource)
				from(sourceSets["test"].allSource.apply {
					include("org/springframework/boot/kofu/samples/**")
				})
			}
			artifact(sourcesJar)
			val dokkaJar by tasks.creating(Jar::class) {
				dependsOn("dokkaHtml")
				archiveClassifier.set("javadoc")
				from(buildDir.resolve("dokka/html"))
			}
			artifact(dokkaJar)
			versionMapping {
				usage("java-api") {
					fromResolutionOf("runtimeClasspath")
				}
				usage("java-runtime") {
					fromResolutionResult()
				}
			}
		}
	}
}


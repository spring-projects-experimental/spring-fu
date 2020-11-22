plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

tasks.compileTestJava {
	sourceCompatibility = "11"
	targetCompatibility = "11"
}

tasks.withType<Javadoc> {
	with (options as StandardJavadocDocletOptions) {
		links = listOf(
				"https://docs.spring.io/spring-framework/docs/5.2.x/javadoc-api/",
				"https://docs.spring.io/spring-boot/docs/2.3.x/api/",
				"https://docs.spring.io/spring-data/r2dbc/docs/1.1.x/api/"
		)
		addStringOption("Xdoclint:none", "-quiet")
		memberLevel = org.gradle.external.javadoc.JavadocMemberLevel.PROTECTED
	}
}

dependencies {
	api("org.springframework.boot:spring-boot")
	implementation(project(":autoconfigure-adapter"))
	compileOnly("org.springframework:spring-webflux")
	compileOnly("org.springframework:spring-webmvc")
	compileOnly("com.fasterxml.jackson.core:jackson-databind")
	compileOnly("org.springframework.data:spring-data-mongodb")
	compileOnly("org.springframework.data:spring-data-elasticsearch")
	compileOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	compileOnly("org.springframework.data:spring-data-r2dbc")
	compileOnly("com.datastax.oss:java-driver-core")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-params")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-undertow")
	testImplementation("org.springframework.boot:spring-boot-starter-jetty")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	testImplementation("org.springframework.boot:spring-boot-starter-mustache")
	testImplementation("org.springframework.boot:spring-boot-starter-json")
	testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
	testImplementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	testImplementation("org.springframework.data:spring-data-elasticsearch")
	testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
	testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	testRuntimeOnly("com.h2database:h2")
	testRuntimeOnly("io.r2dbc:r2dbc-h2")
	testRuntimeOnly("io.r2dbc:r2dbc-postgresql:0.8.4.RELEASE")
	testImplementation("org.testcontainers:testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("redis.clients:jedis")
	testImplementation("io.lettuce:lettuce-core")
}

tasks.withType<Test> {
	if (project.hasProperty("isCI")) {
		exclude("org/springframework/fu/jafu/elasticsearch/ElasticSearchDslTest.class")
		exclude("org/springframework/fu/jafu/elasticsearch/ReactiveElasticSearchDslTest.class")
		exclude("org/springframework/fu/jafu/r2dbc/DataR2dbcDslTest.class")
		exclude("org/springframework/fu/jafu/r2dbc/R2dbcDslTest.class")
		exclude("org/springframework/fu/jafu/redis/ReactiveRedisDslTests.class")
		exclude("org/springframework/fu/jafu/redis/RedisDslTests.class")
	}
}

publishing {
	publications {
		create<MavenPublication>(project.name) {
			from(components["java"])
			artifactId = "spring-fu-jafu"
			val sourcesJar by tasks.creating(Jar::class) {
				archiveClassifier.set("sources")
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
			val javadocJar by tasks.creating(Jar::class) {
				dependsOn("javadoc")
				archiveClassifier.set("javadoc")
				from(buildDir.resolve("docs/javadoc"))
			}
			artifact(javadocJar)
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

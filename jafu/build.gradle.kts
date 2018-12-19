plugins {
	id("io.spring.dependency-management")
	id("java-library")
}

tasks.compileJava {
	sourceCompatibility = "1.8"
	targetCompatibility = "1.8"
}

tasks.withType<Javadoc> {
	with (options as StandardJavadocDocletOptions) {
		links = listOf(
				"https://docs.spring.io/spring-framework/docs/5.1.x/javadoc-api/",
				"https://docs.spring.io/spring-boot/docs/2.1.x/api/"
		)
		addStringOption("Xdoclint:none", "-quiet")
		memberLevel = org.gradle.external.javadoc.JavadocMemberLevel.PROTECTED
	}
}

dependencies {
	api("org.springframework.boot:spring-boot")
	implementation(project(":autoconfigure-adapter"))
	compileOnly("org.springframework:spring-webflux")
	compileOnly("com.fasterxml.jackson.core:jackson-databind")
	compileOnly("org.springframework.data:spring-data-mongodb")
	compileOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	compileOnly("org.springframework.data:spring-data-r2dbc:1.0.0.M1")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-undertow")
	testImplementation("org.springframework.boot:spring-boot-starter-jetty")
	testImplementation("org.springframework.boot:spring-boot-starter-mustache")
	testImplementation("org.springframework.boot:spring-boot-starter-json")
	testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu-jafu"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
			}
			artifact(sourcesJar)
			val javadocJar by tasks.creating(Jar::class) {
				dependsOn("javadoc")
				classifier = "javadoc"
				from(buildDir.resolve("docs/javadoc"))
			}
			artifact(javadocJar)
		}
	}
}
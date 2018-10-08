import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
	id("org.jetbrains.kotlin.jvm")
	id("io.spring.dependency-management")
	id("org.jetbrains.dokka")
}

dependencies {
	api("org.springframework.boot:spring-boot")
	implementation(project(":autoconfigure-adapter"))
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	compileOnly("org.springframework:spring-webflux")
	compileOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
	compileOnly("org.springframework.data:spring-data-mongodb")
	compileOnly("org.mongodb:mongodb-driver-reactivestreams")
	compileOnly("com.fasterxml.jackson.core:jackson-databind")
	compileOnly("com.samskivert:jmustache")
	compileOnly(project(":coroutines:data-mongodb"))
	compileOnly(project(":coroutines:webflux"))

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
	testImplementation(project(":coroutines:data-mongodb"))
	testImplementation(project(":coroutines:webflux"))
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

tasks.withType<DokkaTask> {
	//reportUndocumented = false
	outputFormat = "html"
	samples = listOf("src/test/kotlin")
	includes = listOf("README.md")
	externalDocumentationLink(delegateClosureOf<DokkaConfiguration.ExternalDocumentationLink.Builder> {
		url = URL("https://docs.spring.io/spring-framework/docs/5.1.0.RELEASE/javadoc-api/")
	})
	externalDocumentationLink(delegateClosureOf<DokkaConfiguration.ExternalDocumentationLink.Builder> {
		url = URL("https://docs.spring.io/spring-framework/docs/5.1.0.RELEASE/kdoc-api/spring-framework/")
	})
}

publishing {
	publications {
		create(project.name, MavenPublication::class.java) {
			from(components["java"])
			artifactId = "spring-fu-kofu"
			val sourcesJar by tasks.creating(Jar::class) {
				classifier = "sources"
				from(sourceSets["main"].allSource)
				from(sourceSets["test"].allSource.apply {
					include("org/springframework/boot/kofu/samples/**")
				})
			}
			artifact(sourcesJar)
			val dokkaJar by tasks.creating(Jar::class) {
				dependsOn("dokka")
				classifier = "javadoc"
				from(buildDir.resolve("dokka"))
			}
			artifact(dokkaJar)
		}
	}
}

repositories {
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("http://dl.bintray.com/kotlin/kotlin-eap")
	maven("https://jcenter.bintray.com")
}

dependencyManagement {
	val bootVersion: String by project
	val coroutinesVersion: String by project
	imports {
		mavenBom("org.springframework.boot:spring-boot-dependencies:$bootVersion")
	}
	dependencies {
		dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
		dependency("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
	}
}

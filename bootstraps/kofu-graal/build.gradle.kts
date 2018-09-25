import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.0-rc-57"
	id("io.spring.dependency-management") version "1.0.6.RELEASE"
	id("org.springframework.boot") version "2.1.0.M4"
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	//implementation("org.springframework.boot:spring-boot-starter-jetty")

	implementation("org.springframework.fu:spring-boot-kofu:0.0.1.BUILD-SNAPSHOT") {
		exclude(module = "kotlin-reflect")
	}

	// Workaround for https://github.com/oracle/graal/issues/655
	implementation("javax.servlet:javax.servlet-api")
	implementation("org.aspectj:aspectjweaver")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
	maven("http://dl.bintray.com/kotlin/kotlin-eap")
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
	exclude(module = "netty-transport-native-epoll")
	exclude(module = "netty-transport-native-unix-common")
	exclude(module = "netty-codec-http2")
	exclude(module = "hibernate-validator")
}

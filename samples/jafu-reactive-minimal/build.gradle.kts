plugins {
	id("io.spring.dependency-management") version "1.0.6.RELEASE"
	id("org.springframework.boot") version "2.1.2.RELEASE"
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8 // For GraalVM compat
	targetCompatibility = JavaVersion.VERSION_1_8 // For GraalVM compat
}

dependencies {
	implementation("org.springframework.fu:spring-fu-jafu:0.0.5-BUILD-SNAPSHOT")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
}

configurations.all {
	exclude(module = "javax.annotation-api")
	exclude(module = "hibernate-validator")
	if (project.hasProperty("graal")) {
		exclude(module = "netty-transport-native-epoll")
		exclude(module = "netty-transport-native-unix-common")
		exclude(module = "netty-codec-http2")
	}
}
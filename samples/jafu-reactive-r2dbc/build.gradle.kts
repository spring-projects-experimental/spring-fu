plugins {
	id("io.spring.dependency-management") version "1.0.6.RELEASE"
	id("org.springframework.boot") version "2.1.0.RELEASE"
	id("java")
}

dependencies {
	implementation("org.springframework.fu:spring-fu-jafu:0.0.3.BUILD-SNAPSHOT")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.data:spring-data-r2dbc:1.0.0.BUILD-SNAPSHOT")
	implementation("io.r2dbc:r2dbc-spi:1.0.0.M5")
	implementation("io.r2dbc:r2dbc-postgresql:1.0.0.M5")

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

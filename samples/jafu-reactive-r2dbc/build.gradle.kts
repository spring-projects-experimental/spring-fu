plugins {
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.springframework.boot") version "2.5.3"
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
	implementation("org.springframework.fu:spring-fu-jafu:0.4.5-SNAPSHOT")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework:spring-r2dbc")
	implementation("io.r2dbc:r2dbc-h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

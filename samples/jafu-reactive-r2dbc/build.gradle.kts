plugins {
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("org.springframework.boot") version "2.3.0.M4"
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
	implementation("org.springframework.fu:spring-fu-jafu:0.3.0.BUILD-SNAPSHOT")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.data:spring-data-r2dbc:1.1.0.RC1")
	implementation("io.r2dbc:r2dbc-h2")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

dependencyManagement {
	imports {
		mavenBom("io.r2dbc:r2dbc-bom:Arabba-SR3")
	}
}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://repo.spring.io/milestone")
	maven("https://repo.spring.io/snapshot")
}

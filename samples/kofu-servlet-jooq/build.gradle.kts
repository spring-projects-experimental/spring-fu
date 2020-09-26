import nu.studer.gradle.jooq.JooqEdition.OSS
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging.INFO

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.4.10"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	id("org.springframework.boot") version "2.4.0"
	id("nu.studer.jooq") version "5.0.1"
}

dependencies {
	implementation("org.springframework.fu:spring-fu-kofu:0.5.0-SNAPSHOT")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-mustache")

	runtimeOnly("com.h2database:h2")
	jooqGenerator("com.h2database:h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
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

jooq {

	version.set(dependencyManagement.importedProperties["jooq.version"])
	edition.set(OSS)

	configurations {
		create("main") {
			jooqConfiguration.apply {
				logging = INFO
				jdbc.apply {
					driver = "org.h2.Driver"
					url = "jdbc:h2:mem:vet-clinic-generation;INIT=RUNSCRIPT FROM 'src/main/resources/db/schema.sql'"
					user = "sa"
				}

				generator.apply {
					name = "org.jooq.codegen.DefaultGenerator"
					database.apply {
						name = "org.jooq.meta.h2.H2Database"
						inputSchema = "PUBLIC"

						isIncludeTables = true
						isIncludePackages = false
						isIncludeUDTs = true
						isIncludeSequences = true
						isIncludePrimaryKeys = true
						isIncludeUniqueKeys = true
						isIncludeForeignKeys = true
					}

					target.apply {
						packageName = "com.sample"
						directory = "${project.buildDir}/generated/jooq"
					}
				}
			}
		}
	}
}

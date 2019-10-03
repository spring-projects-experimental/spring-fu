import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.50" apply false
	id("org.springframework.boot") apply false
	id("org.jetbrains.dokka") version "0.9.18" apply false
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	id("maven-publish")
}

allprojects {
	apply {
		plugin("maven-publish")
		plugin("io.spring.dependency-management")
	}

	version = "0.3.BUILD-SNAPSHOT"
	group = "org.springframework.fu"

	dependencyManagement {
		val bootVersion: String by project
		val springDataR2dbcVersion: String by project
		val r2dbcVersion: String by project
		imports {
			mavenBom("org.springframework.boot:spring-boot-dependencies:$bootVersion")
			mavenBom("io.r2dbc:r2dbc-bom:$r2dbcVersion")
		}
		dependencies {
			dependency("org.springframework.data:spring-data-r2dbc:$springDataR2dbcVersion")
		}
	}

	publishing {
		repositories {
			val repoUsername: String? by project
			val repoPassword: String? by project
			maven {
				if (repoUsername != null && repoPassword != null) {
					credentials {
						username = repoUsername
						password = repoPassword
					}
					url = uri(
							if (version.toString().endsWith(".BUILD-SNAPSHOT")) "https://repo.spring.io/libs-snapshot-local/"
							else "https://repo.spring.io/libs-milestone-local/"
					)

				} else {
					url = uri("$buildDir/repo")
				}
			}
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = "1.8"
			freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
		}
	}

	repositories {
		mavenCentral()
		maven("https://repo.spring.io/milestone")
		maven("https://repo.spring.io/snapshot")
		jcenter()
	}
}

publishing {
	publications {
		create<MavenPublication>("kofu-coroutines-mongodb") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-coroutines-mongodb"
			artifact(task<Zip>("kofuCoroutinesMongodbSample") {
				from("samples/kofu-coroutines-mongodb") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-coroutines-mongodb")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-coroutines-r2dbc") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-coroutines-r2dbc"
			artifact(task<Zip>("kofuCoroutinesR2dbcSample") {
				from("samples/kofu-coroutines-r2dbc") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-coroutines-r2dbc")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-coroutines-validation") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-coroutines-validation"
			artifact(task<Zip>("kofuCoroutinesValidationSampleZip") {
				from("samples/kofu-coroutines-validation") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-coroutines-validation")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-reactive-minimal") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-minimal"
			artifact(task<Zip>("kofuReactiveMinimalSampleZip") {
				from("samples/kofu-reactive-minimal") {
					exclude("build", "com.sample.applicationkt", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-reactive-minimal")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-reactive-mongodb") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-mongodb"
			artifact(task<Zip>("kofuReactiveMongodbSampleZip") {
				from("samples/kofu-reactive-mongodb") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-reactive-mongodb")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-reactive-r2dbc") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-r2dbc"
			artifact(task<Zip>("kofuReactiveR2dbcSampleZip") {
				from("samples/kofu-reactive-r2dbc") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-reactive-r2dbc")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-reactive-redis") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-redis"
			artifact(task<Zip>("kofuReactiveRedisSampleZip") {
				from("samples/kofu-reactive-redis") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-reactive-redis")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-reactive-cassandra") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-cassandra"
			artifact(task<Zip>("kofuReactiveCassandraSampleZip") {
				from("samples/kofu-reactive-cassandra") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-reactive-cassandra")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-reactive-validation") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-validation"
			artifact(task<Zip>("kofuReactiveValidationSampleZip") {
				from("samples/kofu-reactive-validation") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-reactive-validation")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-servlet-minimal") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-servlet-minimal"
			artifact(task<Zip>("kofuServletMinimalSampleZip") {
				from("samples/kofu-servlet-minimal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-servlet-minimal")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-servlet-validation") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-servlet-validation"
			artifact(task<Zip>("kofuServletValidationSampleZip") {
				from("samples/kofu-servlet-validation") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-servlet-validation")
				setExecutablePermissions()
			})
		}
	}
}

fun CopySpec.setExecutablePermissions() {
	filesMatching("gradlew") { mode = 0b111101101 }
	filesMatching("gradlew.bat") { mode = 0b110100100 }
}

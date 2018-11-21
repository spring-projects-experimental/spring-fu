import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.10" apply false
	id("org.springframework.boot") version "2.1.0.RELEASE" apply false
	id("org.jetbrains.dokka") version "0.9.17" apply false
	id("io.spring.dependency-management") version "1.0.6.RELEASE"
	id("maven-publish")
}

allprojects {
	apply {
		plugin("maven-publish")
		plugin("io.spring.dependency-management")
	}

	version = "0.0.3.BUILD-SNAPSHOT"
	group = "org.springframework.fu"

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
	}
}

publishing {
	publications {
		create<MavenPublication>("jafu-reactive-minimal") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-jafu-reactive-minimal"
			artifact(task<Zip>("jafuReactiveMinimalSampleZip") {
				from("samples/jafu-reactive-minimal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("jafu-reactive-minimal")
				setExecutablePermissions()

			})
		}

		create<MavenPublication>("kofu-coroutines-minimal") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-coroutines-minimal"
			artifact(task<Zip>("kofuCoroutinesMinimalSampleZip") {
				from("samples/kofu-coroutines-minimal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-coroutines-minimal")
				setExecutablePermissions()
			})
		}

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

		create<MavenPublication>("kofu-reactive-graal") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-graal"
			artifact(task<Zip>("kofuReactiveGraalSampleZip") {
				from("samples/kofu-reactive-graal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml", "com.sample.applicationkt")
				}
				destinationDir = file("$buildDir/dist")
				into("kofu-reactive-graal")
				setExecutablePermissions()
			})
		}

		create<MavenPublication>("kofu-reactive-minimal") {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-minimal"
			artifact(task<Zip>("kofuReactiveMinimalSampleZip") {
				from("samples/kofu-reactive-minimal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
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
	}
}

fun CopySpec.setExecutablePermissions() {
	filesMatching("gradlew") { mode = 0b111101101 }
	filesMatching("gradlew.bat") { mode = 0b110100100 }
}

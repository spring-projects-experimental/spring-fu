import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.3.0-rc-146" apply false
	id("org.springframework.boot") version "2.1.0.BUILD-SNAPSHOT" apply false
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
		maven("https://repo.spring.io/milestone")
		maven("https://repo.spring.io/snapshot")
		maven("http://dl.bintray.com/kotlin/kotlin-eap")
		maven("https://jcenter.bintray.com")
	}
}

publishing {
	publications {
		create("jafu-reactive-minimal", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-jafu-reactive-minimal"
			val jafuReactiveMinimalSample by task<Zip> {
				from("samples/jafu-reactive-minimal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				into("jafu-reactive-minimal")
				setExecutablePermissions()
			}
			artifact(jafuReactiveMinimalSample)
		}

		create("kofu-coroutines-minimal", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-coroutines-minimal"
			val kofuCoroutinesMinimalSample by task<Zip> {
				from("samples/kofu-coroutines-minimal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				into("kofu-coroutines-minimal")
				setExecutablePermissions()
			}
			artifact(kofuCoroutinesMinimalSample)
		}

		create("kofu-coroutines-mongodb", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-coroutines-mongodb"
			val kofuCoroutinesMongodbSample by task<Zip> {
				from("samples/kofu-coroutines-mongodb") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				into("kofu-coroutines-mongodb")
				setExecutablePermissions()
			}
			artifact(kofuCoroutinesMongodbSample)
		}

		create("kofu-reactive-graal", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-graal"
			val kofuReactiveGraalSample by task<Zip> {
				from("samples/kofu-reactive-graal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml", "com.sample.applicationkt")
				}
				into("kofu-reactive-graal")
				setExecutablePermissions()
			}
			artifact(kofuReactiveGraalSample)
		}

		create("kofu-reactive-minimal", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-minimal"
			val kofuReactiveMinimalSample by task<Zip> {
				from("samples/kofu-reactive-minimal") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				into("kofu-reactive-minimal")
				setExecutablePermissions()
			}
			artifact(kofuReactiveMinimalSample)
		}

		create("kofu-reactive-mongodb", MavenPublication::class.java) {
			groupId = "org.springframework.fu"
			artifactId = "spring-fu-samples-kofu-reactive-mongodb"
			val kofuReactiveMongodbSample by task<Zip> {
				from("samples/kofu-reactive-mongodb") {
					exclude("build", ".gradle", ".idea", "out", "*.iml")
				}
				into("kofu-reactive-mongodb")
				setExecutablePermissions()
			}
			artifact(kofuReactiveMongodbSample)
		}
	}
}

fun CopySpec.setExecutablePermissions() {
	filesMatching("gradlew") { mode = 0b111101101 }
	filesMatching("gradlew.bat") { mode = 0b110100100 }
}

inline fun <reified T : Task> task(noinline configuration: T.() -> Unit) = tasks.creating(T::class, configuration)

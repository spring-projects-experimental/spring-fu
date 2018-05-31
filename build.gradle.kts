import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import proguard.gradle.ProGuardTask

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.2.41"
	id("io.spring.dependency-management") version "1.0.5.RELEASE"
	id ("com.github.johnrengelman.shadow") version "2.0.4" apply false
}

buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("net.sf.proguard:proguard-gradle:5.3.3")
	}
}

version = "1.0.0.BUILD-SNAPSHOT"

subprojects {
	apply {
		plugin("io.spring.dependency-management")
		plugin("org.jetbrains.kotlin.jvm")
	}
	tasks.withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = "1.8"
			freeCompilerArgs = listOf("-Xjsr305=strict")
		}
	}
	tasks.withType<Test> {
		useJUnitPlatform()
	}
	repositories {
		mavenCentral()
		maven("https://repo.spring.io/libs-release")
		maven("https://repo.spring.io/snapshot")
	}
	dependencyManagement {
		imports {
			mavenBom("org.springframework.boot:spring-boot-dependencies:2.0.2.RELEASE") {
				bomProperty("spring.version", "5.1.0.BUILD-SNAPSHOT")
				bomProperty("reactor-bom.version", "Californium-BUILD-SNAPSHOT")
			}
		}
	}

	task<ProGuardTask>("proguard") {
		val inputJar = File("$buildDir/libs/${project.name}-all.jar")
		val outputJar = File("$buildDir/libs/${project.name}-shrinked.jar")

		injars(inputJar)
		outjars(outputJar)

		dontobfuscate()
		dontoptimize()

		keepclasseswithmembers("public class * { public static void main(java.lang.String[]);}")
		keep("class kotlin.reflect.** { *; }")
		keep("class org.jetbrains.** { *; }")
		keep("class org.springframework.beans.* { *; }")
		keep("class org.springframework.http.codec.* { *; }")
		keep("class org.springframework.http.codec.support.* { *; }")
		keep("class com.fasterxml.jackson.**  { *; }")
		keep("class kotlin.Metadata { *; }")

		keepattributes("Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod")
		libraryjars(File("${System.getProperty("java.home")}/lib/rt.jar"))
		keepclassmembers("class * extends java.lang.Enum { <fields>; public static **[] values(); public static ** valueOf(java.lang.String); }")

		dontwarn()
	}
}


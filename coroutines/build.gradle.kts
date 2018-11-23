import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

allprojects {
	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("io.spring.dependency-management")
		plugin("java-library")
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = "1.8"
			freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable", "-Xuse-experimental=kotlin.Experimental")
		}
	}

}

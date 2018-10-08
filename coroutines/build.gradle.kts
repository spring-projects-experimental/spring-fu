allprojects {
	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("io.spring.dependency-management")
		plugin("java-library")
	}
}

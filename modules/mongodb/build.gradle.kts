dependencies {
	compile(project(":core"))
	compile("org.springframework.data:spring-data-mongodb") {
		exclude(module = "org.mongodb:mongo-java-driver")
		exclude(module = "org.slf4j:jcl-over-slf4j")
	}
	compile("org.mongodb:mongodb-driver")
	compile("org.mongodb:mongodb-driver-async")
	compile("org.mongodb:mongodb-driver-reactivestreams")
	compile("io.projectreactor:reactor-core")
}

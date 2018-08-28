import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

dependencies {
	implementation(project(":modules:webflux-netty"))
	testImplementation(project(":modules:test"))
}

/*tasks.withType<ShadowJar> {
	exclude("org/springframework/cglib/**",
			"org/springframework/beans/factory/groovy/**",
			"org/springframework/beans/factory/xml/**",
			"org/springframework/cache",
			"org/springframework/context/annotation",
			"org/springframework/context/support/*Xml*.class",
			"org/springframework/context/support/*Groovy*.class",
			"org/springframework/ejb/**",
			"org/springframework/instrument/classloading/**",
			"org/springframework/jmx/**",
			"org/springframework/jndi/**",
			"org/springframework/remoting/**",
			"org/springframework/scheduling/**",
			"org/springframework/scripting/**",
			"org/springframework/web/accept/**",
			"org/springframework/web/bind/**",
			"org/springframework/web/client/**",
			"org/springframework/web/context/**",
			"org/springframework/web/filter/**",
			"org/springframework/web/jsf/**",
			"org/springframework/web/method/**",
			"org/springframework/web/multipart/**",
			"org/springframework/util/xml/**")
}
*/
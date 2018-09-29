# Module kofu

Kofu (for **Ko**tlin and **fu**nctional) is an alternative way of configuring your Spring Boot application,
different from regular auto-configuration. It is based on Spring Boot infrastructure, but
[used in a functional way](https://github.com/spring-projects/spring-fu/tree/master/fuconfigure)
via lambdas instead of annotations. It has following characteristics:

 * Explicit configuration via a [Kotlin DSL](https://dzone.com/articles/kotlin-dsl-from-theory-to-practice) instead of annotation
 * Minimal set of features enabled by default
 * No classpath scanning, no feature enabled based on classpath detection
 * Both declarative (via the DSL) and programmatic (code auto-complete, allow any kind of `if`, `for` statements)
 * Functional configuration based on pure lambdas
 * Minimal reflection usage, no CGLIB proxy, no annotation processing
 * [Faster startup and lower memory consumption](https://github.com/spring-projects/spring-fu/blob/master/kofu/benchmarks.adoc)
 * [GraalVM native image](https://github.com/oracle/graal/tree/master/substratevm) friendly

A typical Kofu configuration look like the example bellow, and a comprehensive documentation is available for each DSL: [application][application],
[logging][ApplicationDsl.logging],
[beans](https://docs.spring.io/spring-framework/docs/current/kdoc-api/spring-framework/org.springframework.context.support/beans.html),
[server][org.springframework.boot.kofu.web.server],
[client][org.springframework.boot.kofu.web.client],
[router](https://docs.spring.io/spring-framework/docs/current/kdoc-api/spring-framework/org.springframework.web.reactive.function.server/router.html),
[cors][org.springframework.boot.kofu.web.cors].

```kotlin
application {
	logging { }
	beans { }
	server {
		router()
		cors { }
		codecs { }
	}
	client {
		codecs { }
	}
	mongo { }
	// TODO sql { }
	// TODO security { }
	// TODO cloud { }
}
```

The dependency to use is `org.springframework.fu:spring-boot-kofu`.


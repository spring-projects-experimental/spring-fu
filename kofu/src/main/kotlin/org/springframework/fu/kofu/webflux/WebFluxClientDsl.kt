package org.springframework.fu.kofu.webflux

import org.springframework.boot.autoconfigure.web.reactive.*
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientBuilderInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl
import org.springframework.fu.kofu.web.JacksonDsl
import org.springframework.web.reactive.function.client.WebClient

/**
 * Kofu DSL for WebFlux webClient.
 *
 * Register a [WebClient.Builder] bean via a [dedicated DSL][WebFluxClientDsl].
 *
 * When no codec is configured, `String` and `Resource` ones are configured by default.
 * When a `codecs { }` block is declared, the one specified are configured by default.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-webflux`.
 *
 * @sample org.springframework.fu.kofu.samples.clientDsl
 * @see WebFluxClientDsl.codecs
 * @author Sebastien Deleuze
 */
class WebFluxClientDsl(private val init: WebFluxClientDsl.() -> Unit) : AbstractDsl() {

	private var codecsConfigured: Boolean = false

	/**
	 * Configure a base URL for requests performed through the webClient.
	 */
	var baseUrl: String? = null


	override fun initialize(context: GenericApplicationContext) {
		super.initialize(context)
		init()
		if (!codecsConfigured) {
			StringCodecInitializer(true, false).initialize(context)
			ResourceCodecInitializer(true).initialize(context)
		}
		ReactiveWebClientBuilderInitializer(baseUrl).initialize(context)
	}

	/**
	 * Configure codecs via a [dedicated DSL][WebFluxClientCodecDsl].
	 */
	fun codecs(dsl: WebFluxClientCodecDsl.() -> Unit =  {}) {
		WebFluxClientCodecDsl(dsl).initialize(context)
		codecsConfigured = true
	}

	class WebFluxClientCodecDsl(private val init: WebFluxClientCodecDsl.() -> Unit) : AbstractDsl() {

		override fun initialize(context: GenericApplicationContext) {
			super.initialize(context)
			init()
		}

		/**
		 * Enable [org.springframework.core.codec.CharSequenceEncoder] and [org.springframework.core.codec.StringDecoder]
		 */
		fun string(textPlainOnly: Boolean = false) {
			StringCodecInitializer(true, textPlainOnly).initialize(context)
		}

		/**
		 * Enable [org.springframework.http.codec.ResourceHttpMessageWriter] and [org.springframework.core.codec.ResourceDecoder]
		 */
		fun resource() {
			ResourceCodecInitializer(true).initialize(context)
		}

		/**
		 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
		 * JSON codec on WebFlux webClient via a [dedicated DSL][JacksonDsl].
		 *
		 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-json`
		 * (included by default in `spring-boot-starter-webflux`).
		 *
		 * @sample org.springframework.fu.kofu.samples.jacksonDsl
		 */
		fun jackson(dsl: JacksonDsl.() -> Unit = {}) {
			JacksonDsl(dsl).initialize(context)
			JacksonJsonCodecInitializer(true).initialize(context)
		}

		/**
		 * Enable [org.springframework.http.codec.protobuf.ProtobufEncoder] and [org.springframework.http.codec.protobuf.ProtobufDecoder]
		 *
		 * This codec requires Protobuf 3 or higher with the official `com.google.protobuf:protobuf-java` dependency, and
		 * supports `application/x-protobuf` and `application/octet-stream`.
		 */
		fun protobuf() {
			ProtobufCodecInitializer(true).initialize(context)
		}

		/**
		 * Enable [org.springframework.http.codec.FormHttpMessageWriter] and [org.springframework.http.codec.FormHttpMessageReader]
		 */
		fun form() {
			FormCodecInitializer(true).initialize(context)
		}

		/**
		* Enable [org.springframework.http.codec.multipart.MultipartHttpMessageWriter] and
		* [org.springframework.http.codec.multipart.MultipartHttpMessageReader]
		*/
		fun multipart() {
			MultipartCodecInitializer(true).initialize(context)
		}

		/**
		 * Enable [org.springframework.http.codec.json.KotlinSerializationJsonDecoder] and
		 * [org.springframework.http.codec.json.KotlinSerializationJsonEncoder]
		 */
		fun kotlinSerialization() {
			KotlinSerializationCodecInitializer(true).initialize(context)
		}
	}
}

/**
 * Declare a WebFlux webClient.
 * @see WebFluxClientDsl
 */
fun ConfigurationDsl.webClient(dsl: WebFluxClientDsl.() -> Unit =  {}) {
	WebFluxClientDsl(dsl).initialize(context)
}

package org.springframework.fu.kofu

import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.charset.Charset
import java.util.*

const val usernameTest = "user"
const val passwordTest = "password"

// for CSRF
const val csrfHeaderTest = "CSRF_HEADER"
const val csrfTokenTest = "test_csrf_token"
const val csrfSessionAttribute = "CSRF_ATTRIBUTE"

/**
 * @author Jonas Bark, Ivan Skachkov, Fred Montariol
 */
internal fun commonTests(client: WebTestClient) = client.apply {
	get().uri("/public-view")
			.exchange()
			.expectStatus().is2xxSuccessful

	// Verify post fails without csrf header
	post().uri("/public-post")
			.exchange()
			.expectStatus().isForbidden

	get().uri("/view").exchange()
			.expectStatus().isUnauthorized

	val basicAuth =
			Base64.getEncoder().encode("$usernameTest:$passwordTest".toByteArray()).toString(Charset.defaultCharset())
	get().uri("/view").header("Authorization", "Basic $basicAuth").exchange()
			.expectStatus().is2xxSuccessful

	val basicAuthWrong =
			Base64.getEncoder().encode("user:pass".toByteArray()).toString(Charset.defaultCharset())
	get().uri("/view").header("Authorization", "Basic $basicAuthWrong").exchange()
			.expectStatus().isUnauthorized
}

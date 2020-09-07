package org.springframework.fu.kofu

import org.springframework.context.ApplicationContext
import org.springframework.fu.kofu.javautils.JavaKotlinUtils
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.charset.Charset
import java.time.Duration
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
fun testSecurityWebFlux(context: ApplicationContext) = context.apply {
	commonTests(webTestClient(context))

	val mockClient = JavaKotlinUtils.getMockWebTestClient(this)

	// Verify post fails without csrf header (mock client)
	mockClient
			.post().uri("/public-post")
			.exchange()
			.expectStatus().isForbidden

	// Verify post succeed with csrf header
	mockClient
			.mutateWith(SecurityMockServerConfigurers.csrf())
			.post().uri("/public-post")
			.exchange()
			.expectStatus().is2xxSuccessful
}

/**
 * @author Jonas Bark, Ivan Skachkov, Fred Montariol
 */
fun testSecurityWebMvc(context: ApplicationContext) {
	val client = webTestClient(context)
	commonTests(client)

	// Verify post succeed with csrf header
	client
			.post().uri("/public-post")
			.header(csrfHeaderTest, csrfTokenTest)
			.exchange()
			.expectStatus().is2xxSuccessful
}

private fun webTestClient(context: ApplicationContext) =
		WebTestClient.bindToServer()
				.baseUrl("http://127.0.0.1:${context.localServerPort}")
				.responseTimeout(Duration.ofMinutes(10)) // useful for debug
				.build()

private fun commonTests(client: WebTestClient) = client.apply {
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

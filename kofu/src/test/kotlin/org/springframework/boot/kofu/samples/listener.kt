package org.springframework.boot.kofu.samples

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.kofu.application
import org.springframework.boot.kofu.ref

fun listener() {
	application {
		listener<ApplicationReadyEvent> {
			ref<UserRepository>().init()
		}
	}
}
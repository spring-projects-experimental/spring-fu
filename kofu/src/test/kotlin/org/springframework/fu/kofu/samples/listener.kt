package org.springframework.fu.kofu.samples

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.application

fun listener() {
	application {
		listener<ApplicationReadyEvent> {
			ref<UserRepository>().init()
		}
	}
}
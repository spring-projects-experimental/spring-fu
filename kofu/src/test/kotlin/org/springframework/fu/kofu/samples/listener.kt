package org.springframework.fu.kofu.samples

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.ref

fun listener() {
	application {
		listener<ApplicationReadyEvent> {
			ref<UserRepository>().init()
		}
	}
}
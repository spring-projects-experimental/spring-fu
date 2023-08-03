package org.springframework.fu.kofu

import org.springframework.context.ApplicationContext
import org.springframework.core.env.get

/**
 * Shortcut for `environment["local.server.port"]`.
 */
val ApplicationContext.localServerPort: String
	get() = environment["local.server.port"]!!

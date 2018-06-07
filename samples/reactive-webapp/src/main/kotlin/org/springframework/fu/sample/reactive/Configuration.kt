package org.springframework.fu.sample.reactive

import org.springframework.core.env.get
import org.springframework.fu.configuration
import org.springframework.fu.sample.reactive.SampleConfiguration

val configuration = configuration {
	SampleConfiguration(property = env["ENV_VARIABLE"]
			?: "debugConf")
}
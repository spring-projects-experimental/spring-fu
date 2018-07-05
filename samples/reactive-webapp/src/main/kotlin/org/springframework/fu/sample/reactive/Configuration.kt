package org.springframework.fu.sample.reactive

import org.springframework.core.env.get
import org.springframework.fu.configuration

data class SampleConfiguration(val property: String)

val configuration = configuration {
	SampleConfiguration(
		property = env["ENV_VARIABLE"] ?: "debugConf"
	)
}
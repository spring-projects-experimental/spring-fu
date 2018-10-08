package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application

fun properties() {
	application {

		/** Will bind `sample.message` property typically defined in an `application.properties`
		 * or `application.yml` file to [SampleProperties.message]. Typically used by retrieving
		 * a [SampleProperties] bean.
		 */
		properties<SampleProperties>(prefix = "sample")
	}
}

/**
 * TODO Switch to data classes when https://github.com/spring-projects/spring-boot/issues/8762 will be fixed
 */
class SampleProperties {
	lateinit var message: String
}
package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application

fun configurationProperties() {
	application {

		/** Will bind `sample.message` property typically defined in an `application.configurationProperties`
		 * or `application.yml` file to [SampleProperties.message]. Typically used by retrieving
		 * a [SampleProperties] bean.
		 */
		configurationProperties<SampleProperties>(prefix = "sample")
	}
}

class SampleProperties(val message: String)
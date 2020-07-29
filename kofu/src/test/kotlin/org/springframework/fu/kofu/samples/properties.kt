@file:Suppress("UNUSED_VARIABLE")

package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application

fun configurationProperties() {
	application {

		/** Bind `sample.message` property typically defined in an `application.properties`
		 * or `application.yml` file to [SampleProperties.message]. Typically used by retrieving
		 * a [SampleProperties] bean.
		 */
		val properties = configurationProperties<SampleProperties>(prefix = "sample")

	}
}

class SampleProperties(val message: String)
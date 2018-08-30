/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.fu.module.dynconf

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.fu.application

/**
 * @author Sebastien Deleuze
 */
class DynamicConfigurationModuleTests {

	@Test
	fun `Use kts properties to generate a TestConfiguration bean`() {
		val app = application(false) {
			configuration("test.kts")
		}
		with(app) {
			run()
			context.getBean<TestConfiguration>()
			stop()
		}
	}

}

data class TestConfiguration(
	val name: String
)
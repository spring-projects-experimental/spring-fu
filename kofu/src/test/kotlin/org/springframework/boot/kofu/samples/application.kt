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

package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application

private fun applicationWithCustomBeanApplication() {
	/// This standalone application registers a custom bean `Foo` and a `City` configuration properties
	val app = application(startServer = false) {
		beans {
			bean<Foo>()
		}
		configuration<City>("city")
	}
	app.run()
}

class Foo

// Switch to data classes when https://github.com/spring-projects/spring-boot/issues/8762 will be fixed
class City {
	lateinit var name: String
	lateinit var country: String
}
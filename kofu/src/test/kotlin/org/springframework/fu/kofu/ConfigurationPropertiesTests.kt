package org.springframework.fu.kofu

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ConfigurationPropertiesTests {

	@Test
	fun `Configuration properties with binding`() {
		val app = application {
			val properties = configurationProperties<City>(prefix = "city")
			Assertions.assertEquals(properties.name, "San Francisco")
			Assertions.assertEquals(properties.country, "USA")
		}
		app.run().close()
	}

	@Test
	fun `Configuration properties with default value`() {
		val app = application {
			val properties = configurationProperties<City>(prefix = "withdefaultvalue")
			Assertions.assertEquals(properties.name, "Paris")
			Assertions.assertEquals(properties.country, "FR")
		}
		app.run().close()
	}

	@Test
	fun `Configuration properties with only default values`() {
		val app = application {
			val properties = configurationProperties<City>(prefix = "doesnotexists")
			Assertions.assertEquals(properties.name, "Paris")
			Assertions.assertEquals(properties.country, "FR")
		}
		app.run().close()
	}

	class City(val name: String = "Paris", val country: String = "FR")

}
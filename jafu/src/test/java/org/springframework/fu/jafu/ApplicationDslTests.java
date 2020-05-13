package org.springframework.fu.jafu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.fu.jafu.Jafu.application;

import java.util.Locale;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.MessageSource;

class ApplicationDslTests {

	@Test
	void createAnEmptyApplicationAndCheckMessageSource() {
		var app = application(a -> {});
		var context = app.run();
		assertFalse(context instanceof ReactiveWebServerApplicationContext);
		var messageSource = context.getBean(MessageSource.class);
		assertEquals("Spring Fu!", messageSource.getMessage("sample.message", null, Locale.getDefault()));
		context.close();
	}

	@Test
	void createAnApplicationWithACustomBean() {
		var app = application(a -> a.beans(b -> b.bean(Foo.class)));
		var context = app.run();
		context.getBean(MessageSource.class);
		context.getBean(Foo.class);
		context.close();
	}

	@Test
	void createAnApplicationWithAConfigurationImport() {
		Consumer<ConfigurationDsl> conf = c -> c.beans(b -> b.bean(Foo.class));
		var app = application(a -> a.enable(conf));
		var context = app.run();
		context.getBean(MessageSource.class);
		context.getBean(Foo.class);
		context.close();
	}

	@Test
	void applicationProperties() {
		var app = application(a -> a.configurationProperties(City.class, "city"));
		var context = app.run();
		assertEquals(context.getBean(City.class).name, "San Francisco");
		context.close();
	}

	@Test
	void replaceBeanOfExistingApplication() {
		var app = application(a -> a.beans(b -> b.bean(Bar.class, () -> new Bar("original"))));
		var context = app.customize(a -> a.beans(b -> b.bean(Bar.class, () -> new Bar("customized"), c -> c.setPrimary(true)))).run();
		assertEquals("customized", context.getBean(Bar.class).value);
		context.close();
	}


	static class Foo {}

	static class Bar {

		private final String value;

		public Bar(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	static class City {

		private String name;

		private String country;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}
	}

}

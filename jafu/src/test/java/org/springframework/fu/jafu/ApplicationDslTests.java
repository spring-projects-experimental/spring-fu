package org.springframework.fu.jafu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.fu.jafu.Jafu.*;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class ApplicationDslTests {

	@Test
	void createAnEmptyApplication() {
		SpringApplication app = application(false, it -> {});
		ConfigurableApplicationContext context = app.run();
		assertFalse(context instanceof ReactiveWebServerApplicationContext);
		context.getBean(ReloadableResourceBundleMessageSource.class);
		context.close();
	}

	@Test
	void createAnApplicationWithACustomBean() {
		SpringApplication app = application(false, a -> a.beans(b -> b.bean(Foo.class)));
		ConfigurableApplicationContext context = app.run();
		context.getBean(ReloadableResourceBundleMessageSource.class);
		context.getBean(Foo.class);
		context.close();
	}

	@Test
	void createAnApplicationWithAConfigurationImport() {
		Consumer<ConfigurationDsl> beansConfig = c -> c.beans(b -> b.bean(Foo.class));
		SpringApplication app = application(false, a -> a.importConfiguration(beansConfig));
		ConfigurableApplicationContext context = app.run();
		context.getBean(ReloadableResourceBundleMessageSource.class);
		context.getBean(Foo.class);
		context.close();
	}

	@Test
	void applicationProperties() {
		SpringApplication app = application(false, a -> a.properties(City.class, "city"));
		ConfigurableApplicationContext context = app.run();
		assertEquals(context.getBean(City.class).name, "San Francisco");
		context.close();
	}


	static class Foo {}

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

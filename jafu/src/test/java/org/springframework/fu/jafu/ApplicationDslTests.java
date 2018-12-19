package org.springframework.fu.jafu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.fu.jafu.Jafu.application;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.fu.jafu.beans.BeanWithDependency;
import org.springframework.fu.jafu.beans.SimpleBean;

class ApplicationDslTests {

	@Test
	void createAnEmptyApplication() {
		var app = application(it -> {});
		var context = app.run();
		assertFalse(context instanceof ReactiveWebServerApplicationContext);
		context.getBean(MessageSource.class);
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
	void createAnApplicationWithBeanScanning() {
		var app = application(a -> a.beans(beans -> beans.scan("org.springframework.fu.jafu.beans")));
		var context = app.run();
		context.getBean(SimpleBean.class);
		context.getBean(BeanWithDependency.class);
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

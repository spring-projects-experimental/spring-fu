package org.springframework.boot.context.properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;

/**
 * Similar to {@code PropertySourcesDeducer} without {@link PropertySourcesPlaceholderConfigurer}
 * bean retrieval since application context is not refreshed yet.
 * TODO Support property placeholder configuration
 */
public class FunctionalPropertySourcesDeducer {

	private final ApplicationContext applicationContext;

	FunctionalPropertySourcesDeducer(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public PropertySources getPropertySources() {
		MutablePropertySources sources = extractEnvironmentPropertySources();
		if (sources != null) {
			return sources;
		}
		throw new IllegalStateException("Unable to obtain PropertySources from "
				+ "PropertySourcesPlaceholderConfigurer or Environment");
	}

	private MutablePropertySources extractEnvironmentPropertySources() {
		Environment environment = this.applicationContext.getEnvironment();
		if (environment instanceof ConfigurableEnvironment) {
			return ((ConfigurableEnvironment) environment).getPropertySources();
		}
		return null;
	}
}

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

package org.springframework.boot.context.properties;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreTopLevelConverterNotFoundBindHandler;
import org.springframework.boot.context.properties.bind.handler.NoUnboundElementsBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.UnboundElementsSourceFilter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySources;

import java.util.function.Consumer;

/**
 * Allow to access to package private Boot classes like {@code PropertySourcesDeducer}.
 */
public class FunctionalConfigurationPropertiesBinder {

	private ConfigurableApplicationContext context;

	private final PropertySources propertySources;

	private final Binder binder;


	public FunctionalConfigurationPropertiesBinder(ConfigurableApplicationContext context) {
		this.context = context;
		this.propertySources = new PropertySourcesDeducer(context).getPropertySources();
		this.binder = new Binder(ConfigurationPropertySources.from(propertySources),
        				new PropertySourcesPlaceholdersResolver(this.propertySources),
        				new ConversionServiceDeducer(context).getConversionService(),
				(registry) -> context.getBeanFactory().copyRegisteredEditorsTo(registry));
	}


	public <T> BindResult<T> bind(String prefix, Bindable<T> target) {
		UnboundElementsSourceFilter filter = new UnboundElementsSourceFilter();
		NoUnboundElementsBindHandler handler = new NoUnboundElementsBindHandler(new IgnoreTopLevelConverterNotFoundBindHandler(), filter);
		return binder.bind(prefix, target, handler);
	}

}

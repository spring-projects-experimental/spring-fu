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

package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

/**
 * {@link ApplicationContextInitializer} adapter for {@link MustacheAutoConfiguration}.
 */
public class MustacheInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	private final MustacheProperties properties;

	public MustacheInitializer(MustacheProperties properties) {
		this.properties = properties;
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		MustacheAutoConfiguration configuration = new MustacheAutoConfiguration(this.properties, context);
		context.registerBean(MustacheResourceTemplateLoader.class, configuration::mustacheTemplateLoader);
		context.registerBean(Mustache.Compiler.class, () -> configuration.mustacheCompiler(context.getBean(Mustache.TemplateLoader.class), context.getEnvironment()));
	}
}

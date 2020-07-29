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

package org.springframework.boot.autoconfigure.thymeleaf;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * {@link ApplicationContextInitializer} adapter for {@link ThymeleafAutoConfiguration.ThymeleafWebMvcConfiguration.ThymeleafViewResolverConfiguration}.
 */
public class ThymeleafServletWebInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    private ThymeleafProperties properties;

    public ThymeleafServletWebInitializer(ThymeleafProperties properties) {
        this.properties = properties;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        ThymeleafAutoConfiguration.ThymeleafDefaultConfiguration defaultConfiguration = new ThymeleafAutoConfiguration.ThymeleafDefaultConfiguration();
        ThymeleafAutoConfiguration.ThymeleafWebMvcConfiguration.ThymeleafViewResolverConfiguration webMvcConfiguration = new ThymeleafAutoConfiguration.ThymeleafWebMvcConfiguration.ThymeleafViewResolverConfiguration();
        context.registerBean("thymeleafTemplateEngine", SpringTemplateEngine.class, () -> defaultConfiguration.templateEngine(this.properties, context.getBeanProvider(ITemplateResolver.class), context.getBeanProvider(IDialect.class)));
        context.registerBean("thymeleafViewResolver", ViewResolver.class, () -> webMvcConfiguration.thymeleafViewResolver(this.properties, context.getBean("thymeleafTemplateEngine", SpringTemplateEngine.class)));
    }
}

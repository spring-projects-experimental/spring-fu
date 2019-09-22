/*
 * Copyright 2012-2019 the original author or authors.
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

package org.springframework.boot.autoconfigure.freemarker;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

/**
 * {@link ApplicationContextInitializer} adapter for {@link FreeMarkerNonWebConfiguration}.
 * @author Ivan Skachkov
 */
public class FreeMarkerInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
    private FreeMarkerProperties freeMarkerProperties;

    public FreeMarkerInitializer(FreeMarkerProperties freeMarkerProperties) {
        this.freeMarkerProperties = freeMarkerProperties;
    }

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        applicationContext.registerBean(FreeMarkerConfigurationFactoryBean.class, () -> new FreeMarkerNonWebConfiguration(freeMarkerProperties).freeMarkerConfiguration());
    }
}

/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.fu.module.thymeleaf

import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.web.reactive.result.view.UrlBasedViewResolver
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine

/**
 * @author Artem Gavrilov
 */
class ThymeleafModule(private val prefix: String,
                      private val suffix: String) : AbstractModule() {

    override fun initialize(context: GenericApplicationContext) {
        context.registerBean { SpringWebFluxTemplateEngine() }
        context.registerBean {
            UrlBasedViewResolver().also {
                it.setPrefix(prefix)
                it.setSuffix(suffix)
                it.setViewClass(ThymeleafView::class.java)
            }
        }
    }
}


fun WebFluxModule.WebFluxServerModule.thymeleaf(prefix: String = "classpath:/templates/",
                                                suffix: String = ".html"): ThymeleafModule {
    val thymeleafDsl = ThymeleafModule(prefix, suffix)
    initializers.add(thymeleafDsl)
    return thymeleafDsl
}

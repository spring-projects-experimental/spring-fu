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

import org.slf4j.LoggerFactory
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.registerBean
import org.springframework.core.Ordered
import org.springframework.fu.AbstractModule
import org.springframework.fu.module.webflux.WebFluxModule
import org.springframework.util.MimeType
import org.thymeleaf.IEngineConfiguration
import org.thymeleaf.Thymeleaf
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.TemplateResolution
import java.nio.charset.Charset


/**
 * @author Artem Gavrilov
 */
class ThymeleafModule(private val customConfiguration: ThymeleafModule.() -> Unit) : AbstractModule() {

    private val logger by lazy { LoggerFactory.getLogger(ThymeleafModule::class.java) }

    private val engine = SpringWebFluxTemplateEngine()
    private val properties = ThymeleafModuleDefaultProperties()
    private val thymeleafUrlTemplateResolver = ThymeleafUrlTemplateResolver()
    private val springResourceTemplateResolver = SpringResourceTemplateResolver()
    // private val thymeleafViewResolver = ThymeleafViewResolver()
    // private val thymeleafReactiveViewResolver = ThymeleafReactiveViewResolver()


    override fun initialize(context: GenericApplicationContext) {
        this.customConfiguration()
        this.applyProperties(context)
        context.registerBean { engine }
        context.registerBean { thymeleafUrlTemplateResolver }
        context.registerBean { springResourceTemplateResolver }
        // context.registerBean { thymeleafViewResolver }
        // context.registerBean { thymeleafReactiveViewResolver }
    }

    fun cache(isCacheable: Boolean) {
        properties.cache = isCacheable
    }

    fun prefix(prefix: String) {
        properties.prefix = prefix
    }

    fun suffix(suffix: String) {
        properties.suffix = suffix
    }

    fun mode(mode: TemplateMode) {
        properties.mode = mode
    }

    fun encoding(encoding: Charset) {
        properties.encoding = encoding
    }

    fun checkExistence(flag: Boolean) {
        properties.checkTemplate = flag
    }


    // TODO look at https://github.com/spring-projects/spring-boot/issues/8124 carefully!
    private fun applyProperties(context: GenericApplicationContext) {

        thymeleafUrlTemplateResolver.applicationContext = context
        thymeleafUrlTemplateResolver.setSuffix(properties.suffix)
        thymeleafUrlTemplateResolver.setPrefix(properties.prefix)
        thymeleafUrlTemplateResolver.setViewClass(ThymeleafView::class.java)
        thymeleafUrlTemplateResolver.order = Ordered.HIGHEST_PRECEDENCE + 5

        springResourceTemplateResolver.setApplicationContext(context)
        springResourceTemplateResolver.characterEncoding = properties.encoding.name()
        springResourceTemplateResolver.isCacheable = properties.cache
        springResourceTemplateResolver.templateMode = properties.mode
        springResourceTemplateResolver.checkExistence = properties.checkTemplate
        // TODO ask someone about checkTemplateLocation existing in flux web
        if (properties.templateResolverOrder != null) {
            springResourceTemplateResolver.order = properties.templateResolverOrder
        }

        // TODO resolve AbstractCachingViewResolver missing for this resolver
//        thymeleafViewResolver.characterEncoding = properties.encoding.name()
//        thymeleafViewResolver.templateEngine = engine
//        thymeleafViewResolver.contentType = properties.servletContentType.let {
//            if (it.charset != null) it.toString()
//            else MimeType(it, it.parameters.plus("charset" to thymeleafViewResolver.characterEncoding)).toString()
//        }
//        thymeleafViewResolver.excludedViewNames = properties.excludeViewNames
//        thymeleafViewResolver.viewNames = properties.viewNames
//        thymeleafViewResolver.order = Ordered.LOWEST_PRECEDENCE - 5

        // TODO resolve conflict with thymeleafUrlTemplateResolver
//        thymeleafReactiveViewResolver.applicationContext = context
//        thymeleafReactiveViewResolver.templateEngine = engine
//        thymeleafReactiveViewResolver.supportedMediaTypes = properties.reactive.mediaTypes
//        thymeleafReactiveViewResolver.fullModeViewNames = properties.reactive.fullModeViewNames
//        thymeleafReactiveViewResolver.responseMaxChunkSizeBytes = properties.reactive.maxChunkSize
//        thymeleafReactiveViewResolver.chunkedModeViewNames = properties.reactive.chunkedModeViewNames


        engine.templateResolvers = setOf(thymeleafUrlTemplateResolver, springResourceTemplateResolver)
        engine.enableSpringELCompiler = properties.enableSpringElCompiler
    }
}

fun WebFluxModule.WebFluxServerModule.thymeleaf(f: ThymeleafModule.() -> Unit = {}): ThymeleafModule {
    val thymeleafDsl = ThymeleafModule(f)
    initializers.add(thymeleafDsl)
    return thymeleafDsl
}
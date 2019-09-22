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

package org.springframework.fu.kofu.freemarker

import org.springframework.boot.autoconfigure.freemarker.FreeMarkerInitializer
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ApplicationDsl

/**
 * Kofu DSL for FreeMarker configuration.
 *
 * Configure a [FreeMarker](https://freemarker.apache.org).
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-freemarker`.
 * @sample org.springframework.fu.kofu.samples.freeMarkerDsl
 * @author Ivan Skachkov
 */
class FreeMarkerDsl(private val init: FreeMarkerDsl.() -> Unit, private val freemarkerProperties: FreeMarkerProperties = FreeMarkerProperties()) : AbstractDsl() {
    /**
     * Comma-separated list of template paths.
     */
    var templateLoaderPath: Array<String>
        get() = freemarkerProperties.templateLoaderPath
        set(value) {
            freemarkerProperties.setTemplateLoaderPath(*value)
        }

    /**
     * Well-known FreeMarker keys which are passed to FreeMarker's Configuration.
     */
    val settings: MutableMap<String, String>
        get() = freemarkerProperties.settings

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()
        FreeMarkerInitializer(freemarkerProperties).initialize(context)
    }
}

/**
 * Configure a [FreeMarker](https://freemarker.apache.org).
 *
 * Configuration from properties / environment (with "spring.freemarker" prefix) is a starting point for further customisation if needed.
 *
 * Require `org.springframework.boot:spring-boot-starter-freemarker` dependency.
 *
 * @sample org.springframework.fu.kofu.samples.freeMarkerDsl
 */
fun ApplicationDsl.freeMarker(dsl: FreeMarkerDsl.() -> Unit = {}) {
    val freeMarkerProperties = configurationProperties(prefix = "spring.freemarker", defaultProperties = FreeMarkerProperties())
    FreeMarkerDsl(dsl, freeMarkerProperties).initialize(context)
}

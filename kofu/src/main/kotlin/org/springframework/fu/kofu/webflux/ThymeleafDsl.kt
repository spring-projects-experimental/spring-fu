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

package org.springframework.fu.kofu.webflux

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafInitializer
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafReactiveWebInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl

/**
 * Kofu DSL for Thymeleaf template engine.
 *
 * Configure a [Thymeleaf](https://github.com/thymeleaf/thymeleaf) view resolver.
 *
 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-thymeleaf`.
 */
class ThymeleafDsl(private val init: ThymeleafDsl.() -> Unit) : AbstractDsl() {

    private val properties = ThymeleafProperties()

    var prefix: String = ThymeleafProperties.DEFAULT_PREFIX
        set(value) {
            properties.prefix = value
        }

    var suffix: String = ThymeleafProperties.DEFAULT_SUFFIX
        set(value) {
            properties.suffix = value
        }

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()
        ThymeleafInitializer(properties).initialize(context)
        ThymeleafReactiveWebInitializer(properties).initialize(context)
    }
}

/**
 * Configure a [Thymeleaf](https://github.com/thymeleaf/thymeleaf) view resolver.
 *
 * Require `org.springframework.boot:spring-boot-starter-thymeleaf` dependency.
 *
 * @sample org.springframework.fu.kofu.samples.thymeleafDsl
 */
fun WebFluxServerDsl.thymeleaf(dsl: ThymeleafDsl.() -> Unit = {}) {
    ThymeleafDsl(dsl).initialize(context)
}

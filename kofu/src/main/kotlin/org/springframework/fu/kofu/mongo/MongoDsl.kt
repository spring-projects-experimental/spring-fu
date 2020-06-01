/*
 * Copyright 2012-2020 the original author or authors.
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

package org.springframework.fu.kofu.mongo

import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer
import org.springframework.boot.autoconfigure.mongo.MongoInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL for MongoDB configuration.
 *
 * Enable and configure Reactive MongoDB support by registering a [org.springframework.data.mongodb.core.MongoTemplate].
 *
 * @author Eddú Meléndez
 */
open class MongoDsl(private val init: MongoDsl.() -> Unit) : AbstractMongoDsl({}) {

    override fun initialize(context: GenericApplicationContext) {
        super.initialize(context)
        init()
        MongoInitializer(properties, embedded).initialize(context)
        MongoDataInitializer(properties).initialize(context)
    }

}

/**
 * Configure MongoDB support.
 * @see MongoDsl
 */
fun ConfigurationDsl.mongodb(dsl: MongoDsl.() -> Unit = {}) {
    MongoDsl(dsl).initialize(context)
}

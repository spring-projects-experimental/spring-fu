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

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion
import de.flapdoodle.embed.mongo.distribution.Version
import org.springframework.boot.autoconfigure.data.mongo.MongoDataInitializer
import org.springframework.boot.autoconfigure.mongo.MongoInitializer
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoInitializer
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties
import org.springframework.context.support.GenericApplicationContext
import org.springframework.fu.kofu.AbstractDsl
import org.springframework.fu.kofu.ConfigurationDsl

/**
 * Kofu DSL base MongoDB DSL
 *
 * @author Eddú Meléndez
 */
abstract class AbstractMongoDsl(private val init: MongoDsl.() -> Unit) : AbstractDsl() {

    protected val properties = MongoProperties()

    internal var embedded = false

    /**
     * Configure the database uri. By default set to `mongodb://localhost/test`.
     */
    var uri: String
        get() = MongoProperties.DEFAULT_URI
        set(value) {
            properties.uri = value
        }

    /**
     * Enable MongoDB embedded webFlux.
     *
     * Require `de.flapdoodle.embed:de.flapdoodle.embed.mongo` dependency.
     *
     * @sample org.springframework.fu.kofu.samples.mongoEmbedded
     */
    fun embedded(dsl: EmbeddedMongoDsl.() -> Unit = {}) {
        embedded = true
        EmbeddedMongoDsl(properties, dsl).initialize(context)
    }

    /**
     * Kofu DSL for embedded MongoDB configuration.
     */
    class EmbeddedMongoDsl(private val mongoProperties: MongoProperties, private val init: EmbeddedMongoDsl.() -> Unit) : AbstractDsl() {

        private val embeddedMongoProperties = EmbeddedMongoProperties()

        override fun initialize(context: GenericApplicationContext) {
            super.initialize(context)
            init()
            EmbeddedMongoInitializer(mongoProperties, embeddedMongoProperties).initialize(context)
        }

        /**
         * Version of Mongo to use
         */
        var version: IFeatureAwareVersion = Version.Main.PRODUCTION
            set(value) {
                embeddedMongoProperties.version = value.asInDownloadPath()
            }
    }
}

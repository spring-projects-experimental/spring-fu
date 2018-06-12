/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.fu.module.data.mongodb.coroutines.data.mongodb.core.mapping

import org.springframework.data.annotation.Persistent
import java.lang.annotation.Inherited

/**
 * Identifies a domain object to be persisted to MongoDB.
 *
 * @author Konrad Kamiński
 * @author Jon Brisbin <jbrisbin></jbrisbin>@vmware.com>
 * @author Oliver Gierke ogierke@vmware.com
 * @author Christoph Strobl
 */
@Persistent
@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class CoroutineDocument(
        val collection: String = "",
        /**
        * Defines the default language to be used with this document.
        *
        * @since 1.6
        * @return
        */
        val language: String = ""
)

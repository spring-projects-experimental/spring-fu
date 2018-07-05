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

package org.springframework.fu.module.data.mongodb.coroutine.data.mongodb.core

import org.springframework.fu.module.data.mongodb.coroutine.data.mongodb.core.index.CoroutineIndexOperations
import org.springframework.data.mongodb.core.index.IndexOperations
import org.springframework.data.mongodb.core.index.IndexOperationsAdapter
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations

open class DefaultCoroutineIndexOperations(
	private val reactiveIndexOperations: ReactiveIndexOperations
) : CoroutineIndexOperations {
	override fun blocking(): IndexOperations = IndexOperationsAdapter.blocking(reactiveIndexOperations)
}
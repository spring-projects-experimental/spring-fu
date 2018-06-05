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

package org.springframework.fu.module.webflux.coroutines.web.function.server

import org.springframework.web.reactive.function.server.RenderingResponse

interface CoroutineRenderingResponse: CoroutineServerResponse {
    companion object {
        fun from(other: CoroutineRenderingResponse): Builder =
                DefaultCoroutineRenderingResponseBuilder(RenderingResponse.from(other.extractServerResponse() as RenderingResponse))

        operator fun invoke(resp: RenderingResponse): CoroutineRenderingResponse = DefaultCoroutineRenderingResponse(resp)
    }

    interface Builder {
        suspend fun build(): CoroutineRenderingResponse

        fun modelAttributes(attributes: Map<String, *>): Builder
    }
}
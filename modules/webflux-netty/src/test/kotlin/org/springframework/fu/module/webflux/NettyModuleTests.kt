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

package org.springframework.fu.module.webflux

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.fu.module.webflux.netty.NettyModule

/**
 * @author Sebastien Deleuze
 */
class NettyModuleTests: AbstractWebServerModuleTests() {
	override fun getWebServerModule(port: Int, host: String): WebFluxModule.WebServerModule = NettyModule(port)

	// FIXME
	@Disabled
	@Test
	override fun `Declare 2 routes blocks`() {}
}
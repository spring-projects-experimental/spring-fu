/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.fu.kofu.javautils;

import org.springframework.context.ApplicationContext;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Fred Montariol
 */
public final class JavaKotlinUtils {
	// uninstantiable
	private JavaKotlinUtils() {
	}

	/**
	 * see https://github.com/spring-projects/spring-framework/issues/20606
	 */
	public static WebTestClient getMockWebTestClient(ApplicationContext ctx) {
		return WebTestClient.bindToApplicationContext(ctx)
				.apply(SecurityMockServerConfigurers.springSecurity())
				.build();
	}
}

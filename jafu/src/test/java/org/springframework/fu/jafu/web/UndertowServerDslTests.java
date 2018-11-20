package org.springframework.fu.jafu.web;

import org.springframework.boot.web.embedded.undertow.UndertowReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;

public class UndertowServerDslTests extends AbstractWebServerDslTests {

	@Override
	ConfigurableReactiveWebServerFactory getServerFactory() {
		return new UndertowReactiveWebServerFactory();
	}
}

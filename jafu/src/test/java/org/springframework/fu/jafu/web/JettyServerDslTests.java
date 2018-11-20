package org.springframework.fu.jafu.web;

import org.springframework.boot.web.embedded.jetty.JettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;

public class JettyServerDslTests extends AbstractWebServerDslTests {

	@Override
	ConfigurableReactiveWebServerFactory getServerFactory() {
		return new JettyReactiveWebServerFactory();
	}
}

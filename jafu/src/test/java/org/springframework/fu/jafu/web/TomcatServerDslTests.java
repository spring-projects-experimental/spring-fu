package org.springframework.fu.jafu.web;

import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;

public class TomcatServerDslTests extends AbstractWebServerDslTests {

	@Override
	ConfigurableReactiveWebServerFactory getServerFactory() {
		return new TomcatReactiveWebServerFactory();
	}
}

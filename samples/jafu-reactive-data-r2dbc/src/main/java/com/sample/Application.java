package com.sample;

import org.springframework.fu.jafu.JafuApplication;

import static org.springframework.fu.jafu.Jafu.reactiveWebApplication;

public abstract class Application {

	public static JafuApplication app = reactiveWebApplication(app -> app
				.enable(Configurations.dataConfig)
				.enable(Configurations.webConfig));

	public static void main (String[] args) {
		app.run(args);
	}
}

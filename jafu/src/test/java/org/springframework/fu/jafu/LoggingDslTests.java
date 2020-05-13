package org.springframework.fu.jafu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.fu.jafu.Jafu.application;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

public class LoggingDslTests {

	@Test
	void changeDefaultROOTLogLevel() {
		var app = application(it -> it.logging(log -> log.level(LogLevel.DEBUG)));
		app.run();
		var loggingSystem = LoggingSystem.get(LoggingDslTests.class.getClassLoader());
		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration("ROOT").getEffectiveLevel());
	}

	@Test
	void changePackageLogLevel() {
		var packageName = "org.springframework";
		var app = application(it -> it.logging(log -> log.level(packageName, LogLevel.DEBUG)));
		app.run();
		var loggingSystem = LoggingSystem.get(LoggingDslTests.class.getClassLoader());
		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration(packageName).getEffectiveLevel());
	}

	@Test
	void changeClassLogLevel() {
		var loggingSystem = LoggingSystem.get(LoggingDslTests.class.getClassLoader());
		loggingSystem.setLogLevel("ROOT", LogLevel.INFO);
		var app = application(it -> it.logging(log -> log.level(DefaultListableBeanFactory.class, LogLevel.DEBUG)));
		app.run();
		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration("org.springframework.beans.factory.support.DefaultListableBeanFactory").getEffectiveLevel());
	}

}

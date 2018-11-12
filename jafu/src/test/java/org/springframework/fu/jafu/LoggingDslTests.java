package org.springframework.fu.jafu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.fu.jafu.ApplicationDsl.application;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

public class LoggingDslTests {

	@Test
	void changeDefaultROOTLogLevel() {
		ApplicationDsl app = application(false, it -> it.logging(log -> log.level(LogLevel.DEBUG)));
		app.run();
		LoggingSystem loggingSystem = LoggingSystem.get(LoggingDslTests.class.getClassLoader());
		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration("ROOT").getEffectiveLevel());
	}

	@Test
	void changePackageLogLevel() {
		String packageName = "org.springframework";
		ApplicationDsl app = application(false, it -> it.logging(log -> log.level(packageName, LogLevel.DEBUG)));
		app.run();
		LoggingSystem loggingSystem = LoggingSystem.get(LoggingDslTests.class.getClassLoader());
		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration(packageName).getEffectiveLevel());
	}

	@Test
	void changeClassLogLevel() {
		LoggingSystem loggingSystem = LoggingSystem.get(LoggingDslTests.class.getClassLoader());
		loggingSystem.setLogLevel("ROOT", LogLevel.INFO);
		ApplicationDsl app = application(false, it -> it.logging(log -> log.level(DefaultListableBeanFactory.class, LogLevel.DEBUG)));
		app.run();
		assertEquals(LogLevel.DEBUG, loggingSystem.getLoggerConfiguration("org.springframework.beans.factory.support.DefaultListableBeanFactory").getEffectiveLevel());
	}

}

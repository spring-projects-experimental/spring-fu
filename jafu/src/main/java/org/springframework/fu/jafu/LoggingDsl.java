package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

/**
 * Jafu DSL for logging configuration.
 *
 * @author Sebastien Deleuze
 */
public class LoggingDsl {

	private LoggingSystem loggingSystem = LoggingSystem.get(LoggingDsl.class.getClassLoader());

	LoggingDsl(Consumer<LoggingDsl> dsl) {
		dsl.accept(this);
	}

	/**
	 * Set the default ROOT log level
	 */
	public LoggingDsl level(LogLevel level) {
		loggingSystem.setLogLevel("ROOT", level);
		return this;
	}

	/**
	 * Customize the log level for a given package
	 * @param packageName the package for which the log level should be customized
	 * @param level the log level to use
	 */
	public LoggingDsl level(String packageName, LogLevel level) {
		loggingSystem.setLogLevel(packageName, level);
		return this;
	}

	/**
	 * Customize the log level for a given class
	 * @param clazz the class for which the log level should be customized
	 * @param level the log level to use
	 *
	 */
	public <T> LoggingDsl level(Class<T> clazz, LogLevel level) {
		loggingSystem.setLogLevel(clazz.getName(), level);
		return this;
	}
}

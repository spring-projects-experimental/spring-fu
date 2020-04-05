package org.springframework.fu.jafu.web;

import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.boot.autoconfigure.jackson.JacksonInitializer;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;

/**
 * Jafu DSL for <a href="https://github.com/FasterXML/jackson">Jackson</a> serialization library.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-json}7444444444444444444444444444444444
 * (included by default in {@code spring-boot-starter-webflux}).
 *
 * @author Sebastien Deleuze
 */
public class JacksonDsl extends AbstractDsl {

	private final boolean isClientCodec;

	private final Consumer<JacksonDsl> dsl;

	private final JacksonProperties properties = new JacksonProperties();


	public JacksonDsl(boolean isClientCodec, Consumer<JacksonDsl> dsl) {
		this.isClientCodec = isClientCodec;
		this.dsl = dsl;
	}

	@Override
	public JacksonDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
		return (JacksonDsl) super.enable(dsl);
	}

	@Override
	public void initialize(GenericApplicationContext context) {
		super.initialize(context);
		this.dsl.accept(this);
		new JacksonInitializer(properties).initialize(context);
	}

	/**
	 * Date format string or a fully-qualified date format class name. For instance, {@code yyyy-MM-dd HH:mm:ss}
	 */
	public JacksonDsl dateFormat(String dateFormat) {
		this.properties.setDateFormat(dateFormat);
		return this;
	}

	/**
	 * {@code PropertyNamingStrategy} constant or subclass
	 */
	public JacksonDsl propertyNamingStrategy(Class<PropertyNamingStrategy> strategy) {
		this.properties.setPropertyNamingStrategy(strategy.getName());
		return this;
	}

	/**
	 * Set the default property inclusion
	 */
	public JacksonDsl defaultPropertyInclusion(JsonInclude.Include include) {
		this.properties.setDefaultPropertyInclusion(include);
		return this;
	}

	/**
	 * Set the timezone
	 */
	public JacksonDsl timeZone(TimeZone timeZone) {
		this.properties.setTimeZone(timeZone);
		return this;
	}

	/**
	 * Set the locale
	 */
	public JacksonDsl locale(Locale locale) {
		this.properties.setLocale(locale);
		return this;
	}

	/**
	 * Shortcut for {@link SerializationFeature#INDENT_OUTPUT} feature.
	 */
	public JacksonDsl indentOutput(Boolean indentOutput) {
		this.properties.getSerialization().put(SerializationFeature.INDENT_OUTPUT, indentOutput);
		return this;
	}

	/**
	 * Set the visibility for the specified {@link PropertyAccessor}
	 */
	public JacksonDsl visibility(PropertyAccessor propertyAccessor, JsonAutoDetect.Visibility visibility) {
		this.properties.getVisibility().put(propertyAccessor, visibility);
		return this;
	}

	/**
	 * Enable serialization feature
	 */
	public JacksonDsl enableSerializationFeature(SerializationFeature feature) {
		this.properties.getSerialization().put(feature, true);
		return this;
	}

	/**
	 * Disable serialization feature
	 */
	public JacksonDsl disableSerializationFeature(SerializationFeature feature) {
		this.properties.getSerialization().put(feature, false);
		return this;
	}

	/**
	 * Enable deserialization feature
	 */
	public JacksonDsl enableDeserializationFeature(DeserializationFeature feature) {
		this.properties.getDeserialization().put(feature, true);
		return this;
	}

	/**
	 * Disable deserialization feature
	 */
	public JacksonDsl disableDeserializationFeature(DeserializationFeature feature) {
		this.properties.getDeserialization().put(feature, false);
		return this;
	}

	/**
	 * Enable mapper feature
	 */
	public JacksonDsl enableMapperFeature(MapperFeature feature) {
		this.properties.getMapper().put(feature, true);
		return this;
	}

	/**
	 * Disable mapper feature
	 */
	public JacksonDsl disableMapperFeature(MapperFeature feature) {
		this.properties.getMapper().put(feature, false);
		return this;
	}

	/**
	 * Enable generator feature
	 */
	public JacksonDsl enableGeneratorFeature(JsonGenerator.Feature feature) {
		this.properties.getGenerator().put(feature, true);
		return this;
	}

	/**
	 * Disable generator feature
	 */
	public JacksonDsl disableGeneratorFeature(JsonGenerator.Feature feature) {
		properties.getGenerator().put(feature, false);
		return this;
	}

}

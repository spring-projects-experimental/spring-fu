/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.diagnostics.analyzer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.SpringBootExceptionReporter;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalysisReporter;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.boot.diagnostics.LoggingFailureAnalysisReporter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;

import org.springframework.util.Assert;

/**
 * Functional variant of {@code FailureAnalyzers}.
 *
 * @author Sébastien Deleuze
 */
public class FunctionalFailureAnalyzers implements SpringBootExceptionReporter {

	private static final Log logger = LogFactory.getLog(FunctionalFailureAnalyzers.class);

	private final List<FailureAnalyzer> analyzers = Arrays.asList(
			new BeanCurrentlyInCreationFailureAnalyzer(),
			new BeanDefinitionOverrideFailureAnalyzer(),
			new BeanNotOfRequiredTypeFailureAnalyzer(),
			new BindFailureAnalyzer(),
			new BindValidationFailureAnalyzer(),
			new UnboundConfigurationPropertyFailureAnalyzer(),
			new ConnectorStartFailureAnalyzer(),
			new NoSuchMethodFailureAnalyzer(),
			new NoUniqueBeanDefinitionFailureAnalyzer(),
			new PortInUseFailureAnalyzer(),
			//new ValidationExceptionFailureAnalyzer(),
			new InvalidConfigurationPropertyNameFailureAnalyzer(),
			new InvalidConfigurationPropertyValueFailureAnalyzer());

	private final List<FailureAnalysisReporter> reporters = Arrays.asList(
			new LoggingFailureAnalysisReporter()
	);

	public FunctionalFailureAnalyzers(ConfigurableApplicationContext context) {
		Assert.notNull(context, "Context must not be null");
		prepareFailureAnalyzers(this.analyzers, context);
	}

	private void prepareFailureAnalyzers(List<FailureAnalyzer> analyzers,
			ConfigurableApplicationContext context) {
		for (FailureAnalyzer analyzer : analyzers) {
			prepareAnalyzer(context, analyzer);
		}
	}

	private void prepareAnalyzer(ConfigurableApplicationContext context,
			FailureAnalyzer analyzer) {
		if (analyzer instanceof BeanFactoryAware) {
			((BeanFactoryAware) analyzer).setBeanFactory(context.getBeanFactory());
		}
		if (analyzer instanceof EnvironmentAware) {
			((EnvironmentAware) analyzer).setEnvironment(context.getEnvironment());
		}
	}

	@Override
	public boolean reportException(Throwable failure) {
		FailureAnalysis analysis = analyze(failure, this.analyzers);
		return report(analysis);
	}

	private FailureAnalysis analyze(Throwable failure, List<FailureAnalyzer> analyzers) {
		for (FailureAnalyzer analyzer : analyzers) {
			try {
				FailureAnalysis analysis = analyzer.analyze(failure);
				if (analysis != null) {
					return analysis;
				}
			}
			catch (Throwable ex) {
				logger.debug("FailureAnalyzer " + analyzer + " failed", ex);
			}
		}
		return null;
	}

	private boolean report(FailureAnalysis analysis) {
		if (analysis == null || reporters.isEmpty()) {
			return false;
		}
		for (FailureAnalysisReporter reporter : reporters) {
			reporter.report(analysis);
		}
		return true;
	}

}

package com.geodb.ite.iri.manager.services.providers.coordinator;

import java.util.Arrays;
import java.util.List;

import com.geodb.ite.iri.manager.services.configuration.CoordinatorServiceConfiguration;

public interface Configuration extends CoordinatorServiceConfiguration {
	public static final Integer INTERVAL_DEFAULT = 120;

	public static final List<String> KEYS = Arrays.asList(INTERVAL);
	public static final List<Object> VALUES = Arrays.asList(INTERVAL_DEFAULT);
}

package com.geodb.ite.iri.manager.services.providers.coordinator;

import java.util.Arrays;
import java.util.List;

public interface Configuration {	
	public static final String INTERVAL = "com.geodb.ite.iri.manager.services.coordinator.interval";
	
	public static final Integer INTERVAL_DEFAULT = 120;
	
	public static final List<String> KEYS = Arrays.asList(INTERVAL);
	public static final List<Object> VALUES = Arrays.asList(INTERVAL_DEFAULT);
}

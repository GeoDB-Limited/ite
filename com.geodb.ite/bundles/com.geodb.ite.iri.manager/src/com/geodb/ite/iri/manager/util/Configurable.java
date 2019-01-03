package com.geodb.ite.iri.manager.util;

import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;

public interface Configurable {

	MApplication getMApplication();
	
	default void configure(List<String> keys, List<Object> values) {
		IEclipseContext context = getMApplication().getContext();
		int size = keys.size();
		String key;
		for (int k = 0; k < size; k++) {
			key = keys.get(k);
			if (!context.containsKey(key)) {
				context.declareModifiable(key);
				context.set(key, values.get(k));
			}
		}
	}
}

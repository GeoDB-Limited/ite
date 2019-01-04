package com.geodb.ite.iri.manager.ui.parts.providers;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.viewers.IStructuredContentProvider;

@Singleton
@Creatable
public class ConfigContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IRIConfig) {
			return ((IRIConfig) inputElement).getElements().toArray();
		} else {
			return new Object[0];
		}
	}

}

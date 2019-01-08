package com.geodb.ite.iri.manager.ui.parts.providers;

import java.util.Map.Entry;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.viewers.ColumnLabelProvider;

@Singleton
@Creatable
public class FieldLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Entry<?, ?>) {
			return getText(((Entry<?, ?>) element).getKey());
		} else if (element instanceof String) {
			String v = ((String) element).trim();
			return v.isEmpty() ? "" : Character.toUpperCase(v.charAt(0)) + v.substring(1);
		}
		return null;
	}

}

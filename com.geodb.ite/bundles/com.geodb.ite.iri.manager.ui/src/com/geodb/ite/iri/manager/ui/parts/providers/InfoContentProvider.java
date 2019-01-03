package com.geodb.ite.iri.manager.ui.parts.providers;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.viewers.IStructuredContentProvider;

import jota.dto.response.GetNodeInfoResponse;

@Singleton
@Creatable
public class InfoContentProvider implements IStructuredContentProvider {
	
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof GetNodeInfoResponse) {
			GetNodeInfoResponse nodeInfoResponse = (GetNodeInfoResponse) inputElement;
			List<Entry<?, ?>> result = getResultsElements(nodeInfoResponse);
			return result.toArray();
		} else {
			return new Object[0];
		}
	}

	private List<Entry<?, ?>> getResultsElements(GetNodeInfoResponse nir) {
		List<Entry<?, ?>> result = new ArrayList<>();
		addField(result, "version", nir.getAppVersion());
		addField(result, "time", nir.getTime());
		addField(result, "milestone index", nir.getLatestMilestoneIndex());
		addField(result, "tips", nir.getTips());
		return result;
	}
	
	private static void addField(List<Entry<?, ?>> list, String field, Object value) {
		list.add(new AbstractMap.SimpleEntry<String, Object>(field, value));
	}

}

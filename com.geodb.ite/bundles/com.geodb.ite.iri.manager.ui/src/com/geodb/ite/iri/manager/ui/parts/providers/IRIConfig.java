package com.geodb.ite.iri.manager.ui.parts.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.Viewer;

@Singleton
@Creatable
public class IRIConfig {

	public interface Listener {
		Viewer getViewer();
		void layout();
	}

	private List<Listener> listeners;

	@Inject
	private void setPort(@Optional @Named("com.geodb.ite.iri.manager.services.iri.port") String value) {
		elements.put("port", value);
		notifyListeners();
	}

	@Inject
	private void setUdpPort(@Optional @Named("com.geodb.ite.iri.manager.services.iri.tcp.port") String value) {
		elements.put("TCP port", value);
		notifyListeners();
	}

	@Inject
	private void setTcpPort(@Optional @Named("com.geodb.ite.iri.manager.services.iri.udp.port") String value) {
		elements.put("UDP port", value);
		notifyListeners();
	}

	@Inject
	private void setMWM(@Optional @Named("com.geodb.ite.iri.manager.services.iri.mwm") String value) {
		elements.put("MWM", value);
		notifyListeners();
	}

	private Map<String, Object> elements;

	public IRIConfig() {
		elements = new HashMap<>();
		listeners = new ArrayList<>();
	}

	public List<Entry<?, ?>> getElements() {
		return new ArrayList<>(elements.entrySet());
	}

	public void register(Listener l) {
		listeners.add(l);
	}

	public void unregister(Listener l) {
		listeners.remove(l);
	}

	public void notifyListeners() {
		listeners.forEach(l -> {
			l.getViewer().setInput(this);
			l.layout();
		});
	}

	@PreDestroy
	void dispose() {
		listeners.clear();
	}
}

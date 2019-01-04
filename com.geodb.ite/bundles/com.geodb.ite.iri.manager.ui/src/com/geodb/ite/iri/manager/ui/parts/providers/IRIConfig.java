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

import com.geodb.ite.iri.manager.services.configuration.IRIServiceConfiguration;

@Singleton
@Creatable
public class IRIConfig {

	public interface Listener {
		Viewer getViewer();
		void layout();
	}

	private List<Listener> listeners;

	@Inject
	private void setPort(@Optional @Named(IRIServiceConfiguration.PORT) String value) {
		elements.put("port", value);
		notifyListeners();
	}

	@Inject
	private void setUdpPort(@Optional @Named(IRIServiceConfiguration.TCP_PORT) String value) {
		elements.put("TCP port", value);
		notifyListeners();
	}

	@Inject
	private void setTcpPort(@Optional @Named(IRIServiceConfiguration.UDP_PORT) String value) {
		elements.put("UDP port", value);
		notifyListeners();
	}

	@Inject
	private void setMWM(@Optional @Named(IRIServiceConfiguration.MWM) String value) {
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

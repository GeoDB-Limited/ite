package com.geodb.ite.iri.manager.services.providers.iri;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import jota.IotaAPI;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;

import com.geodb.ite.iri.manager.services.CoordinatorService;
import com.geodb.ite.iri.manager.services.IRIService;
import com.geodb.ite.iri.manager.services.events.iri.IRIServiceEvents;
import com.geodb.ite.iri.manager.util.Configurable;
import com.iota.iri.IRI;

@SuppressWarnings("restriction")
public class IRIServiceProvider implements IRIService, Configurable {

	protected boolean connected;
	protected ExecutorService iriWorker;
	protected Future<?> iriHandle;

	@Inject
	@Optional
	@Named(Configuration.ENABLE)
	protected Boolean enable;

	@Inject
	@Optional
	@Named(Configuration.PORT)
	protected String port;

	@Inject
	@Optional
	@Named(Configuration.UDP_PORT)
	protected String udpPort;

	@Inject
	@Optional
	@Named(Configuration.TCP_PORT)
	protected String tcpPort;

	@Inject
	@Optional
	@Named(Configuration.SEND_LIMIT)
	protected String sendLimit;

	@Inject
	@Optional
	@Named(Configuration.MAX_PEERS)
	protected String maxPeers;

	@Inject
	@Optional
	@Named(Configuration.MWM)
	protected String mwm;

	@Inject
	protected IEclipseContext context;

	@Inject
	private IEventBroker broker;

	@Inject
	protected CoordinatorService coordinator;

	@Inject
	protected MApplication application;

	protected Logger logger;

	@Inject
	public void instantiateLogger(ILoggerProvider loggerProvider) {
		logger = loggerProvider.getClassLogger(this.getClass());
	}

	@PostConstruct
	protected void initialize() {
		logger.info("Configuring service");
		configure(Configuration.KEYS, Configuration.VALUES);
	}

	protected void createAPI() {
		String host = "localhost";
		IotaAPI api = new IotaAPI.Builder()
				.host(host)
				.port(port)
				.build();

		IEclipseContext appContext = application.getContext();
		if (!appContext.containsKey(IotaAPI.class)) {
			appContext.declareModifiable(IotaAPI.class);
		}
		appContext.modify(IotaAPI.class, api);
	}

	@Override
	public MApplication getMApplication() {
		return application;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void start() {
		if (!connected)
			startWorkers();
	}

	protected void startWorkers() {
		if (enable) {
			iriWorker = Executors.newSingleThreadExecutor();
			iriHandle = iriWorker.submit(this::setupIRI);
			coordinator.start();
			broker.post(IRIServiceEvents.IRI_STARTED,
					createEventData(IRIServiceEvents.IRI_STARTED, IRIServiceEvents.FIELD_CONNECTED, true));
		}
	}

	protected void setupIRI() {
		try {
			connected = true;
			String[] args = new String[] {
					"--testnet",
					"--testnet-no-coo-validation",
					"--remote",
					"--mwm", mwm,
					"-p", port,
					"-u", udpPort,
					"-t", tcpPort,
					"--send-limit", sendLimit,
					"--max-peers", maxPeers
			};
			IRI.main(args);
			createAPI();
		} catch (Exception e) {
			connected = false;
			iriHandle = null;
			iriWorker.shutdown();
			iriWorker = null;
			logger.info("Fail IRI setup");
		}
	}

	private Map<String, Object> createEventData(String topic, String field, Object value) {
		Map<String, Object> map = new HashMap<>();
		map.put(IRIServiceEvents.TOPIC_BASE, topic);
		map.put(field, value);
		return map;
	}

	@Override
	public void stop() {
		logger.info("Deactivating service");
		shutdownWorkers();
	}

	protected void shutdownWorkers() {
		coordinator.stop();

		if (iriHandle != null) {
			IRI.stop();
			application.getContext().remove(IotaAPI.class);
			iriHandle.cancel(true);
			iriHandle = null;
		}

		if (iriWorker != null) {
			iriWorker.shutdown();
			iriWorker = null;
		}

		connected = false;
		broker.post(IRIServiceEvents.IRI_STOPED,
				createEventData(IRIServiceEvents.IRI_STOPED, IRIServiceEvents.FIELD_CONNECTED, false));
	}
}

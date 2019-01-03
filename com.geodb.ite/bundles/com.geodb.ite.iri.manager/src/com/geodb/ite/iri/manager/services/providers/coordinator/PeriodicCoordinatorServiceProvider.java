package com.geodb.ite.iri.manager.services.providers.coordinator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;

import com.geodb.ite.iri.manager.services.CoordinatorService;
import com.geodb.ite.iri.manager.util.Configurable;
import com.iota.iri.IRI;

import jota.IotaAPI;
import jota.dto.response.GetAttachToTangleResponse;
import jota.dto.response.GetNodeInfoResponse;
import jota.dto.response.GetTransactionsToApproveResponse;
import jota.model.Bundle;
import jota.model.Transaction;
import jota.utils.Converter;

/**
 * Copy & adapted from https://github.com/schierlm/private-iota-testnet
 */
@SuppressWarnings("restriction")
public class PeriodicCoordinatorServiceProvider implements CoordinatorService, Configurable {

	public static final String NULL_HASH = "999999999999999999999999999999999999999999999999999999999999999999999999999999999";
	public static final String TESTNET_COORDINATOR_ADDRESS = "EQQFCZBIHRHWPXKMTOLMYUYPCN9XLMJPYZVFJSAY9FQHCCLWTOLLUGKKMXYFDBOOYFBLBI9WUEILGECYM";
	public static final String NULL_ADDRESS = "999999999999999999999999999999999999999999999999999999999999999999999999999999999";
	public static final int TAG_TRINARY_SIZE = 81;

	@Inject
	@Optional
	@Named(Configuration.INTERVAL)
	private Integer interval;

	@Inject
	private MApplication application;

	private ScheduledExecutorService executor;
	private IotaAPI api;
	private boolean shutdown;

	private Logger logger;

	@Inject
	public void instantiateLogger(ILoggerProvider loggerProvider) {
		logger = loggerProvider.getClassLogger(this.getClass());
	}

	@Inject
	public void setAPI(@Optional IotaAPI api) {
		this.api = api;
	}

	@PostConstruct
	private void initialize() {
		configure(Configuration.KEYS, Configuration.VALUES);
	}

	@Override
	public MApplication getMApplication() {
		return application;
	}

	@Override
	public void start() {
		executor = Executors.newSingleThreadScheduledExecutor();
		generateMilestone();
	}

	protected void generateMilestone() {
		if (!shutdown) {
			if (IRI.isInitialized() && (api != null)) {
				try {
					GetNodeInfoResponse nodeInfo = api.getNodeInfo();
					int updatedMilestone = nodeInfo.getLatestMilestoneIndex();
					if (nodeInfo.getLatestMilestone().equals(NULL_HASH)) {
						createMilestone(api, NULL_HASH, NULL_HASH, updatedMilestone + 1);
						createMilestone(api, NULL_HASH, NULL_HASH, updatedMilestone + 2);
					} else if (nodeInfo.getLatestSolidSubtangleMilestone().equals(NULL_HASH)) {
						createMilestone(api, NULL_HASH, NULL_HASH, updatedMilestone + 1);
					} else {
						GetTransactionsToApproveResponse x = api.getTransactionsToApprove(10);
						createMilestone(api, x.getTrunkTransaction(), x.getBranchTransaction(), updatedMilestone + 1);
					}
				} catch (Exception e) {
					logger.info(e.getLocalizedMessage());
				}
			}

			executor.schedule(this::generateMilestone, interval, TimeUnit.SECONDS);
		}
	}

	protected void createMilestone(IotaAPI api, String tip1, String tip2, long index) throws Exception {
		newMilestone(api, tip1, tip2, index);
		logger.info("New milestone " + index + " created.");
	}

	static void newMilestone(IotaAPI api, String tip1, String tip2, long index) throws Exception {
		final Bundle bundle = new Bundle();
		String tag = Converter.trytes(Converter.trits(index, TAG_TRINARY_SIZE));
		long timestamp = System.currentTimeMillis() / 1000;
		bundle.addEntry(1, TESTNET_COORDINATOR_ADDRESS, 0, tag, timestamp);
		bundle.addEntry(1, NULL_ADDRESS, 0, tag, timestamp);
		bundle.finalize(null);
		bundle.addTrytes(Collections.<String>emptyList());
		List<String> trytes = new ArrayList<>();
		for (Transaction trx : bundle.getTransactions()) {
			trytes.add(trx.toTrytes());
		}
		Collections.reverse(trytes);
		GetAttachToTangleResponse rrr = api.attachToTangle(tip1, tip2, 13,
				(String[]) trytes.toArray(new String[trytes.size()]));
		api.storeTransactions(rrr.getTrytes());
		api.broadcastTransactions(rrr.getTrytes());
	}

	@Override
	public void stop() {
		if (executor != null) {
			shutdown = true;
			executor.shutdown();
			executor = null;
		}
	}
}

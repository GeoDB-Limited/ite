package com.geodb.ite.iri.manager.services.providers.iri;

import java.util.Arrays;
import java.util.List;

public interface Configuration {
	public static final String ENABLE = "com.geodb.ite.iri.manager.services.iri.enable";
	public static final String PORT = "com.geodb.ite.iri.manager.services.iri.port";
	public static final String UDP_PORT = "com.geodb.ite.iri.manager.services.iri.udp.port";
	public static final String TCP_PORT = "com.geodb.ite.iri.manager.services.iri.tcp.port";
	public static final String SEND_LIMIT = "com.geodb.ite.iri.manager.services.iri.send.limit";
	public static final String MAX_PEERS = "com.geodb.ite.iri.manager.services.iri.max.peers";
	public static final String MWM = "com.geodb.ite.iri.manager.services.iri.mwm";

	public static final boolean ENABLE_DEFAULT = true;
	public static final String PORT_DEFAULT = "14700";
	public static final String UDP_PORT_DEFAULT = "14600";
	public static final String TCP_PORT_DEFAULT = "14600";
	public static final String SEND_LIMIT_DEFAULT = "1.0";
	public static final String MAX_PEERS_DEFAULT = "8";
	public static final String MWM_DEFAULT = "1";

	public static final List<String> KEYS = Arrays.asList(
			ENABLE,
			PORT,
			UDP_PORT,
			TCP_PORT,
			SEND_LIMIT,
			MAX_PEERS,
			MWM);
	
	public static final List<Object> VALUES = Arrays.asList(
			ENABLE_DEFAULT,
			PORT_DEFAULT,
			UDP_PORT_DEFAULT,
			TCP_PORT_DEFAULT,
			SEND_LIMIT_DEFAULT,
			MAX_PEERS_DEFAULT,
			MWM_DEFAULT);
}

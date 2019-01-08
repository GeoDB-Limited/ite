package com.geodb.ite.iri.manager.services.providers.iri;

import java.util.Arrays;
import java.util.List;

import com.geodb.ite.iri.manager.services.configuration.IRIServiceConfiguration;

public interface Configuration extends IRIServiceConfiguration {
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

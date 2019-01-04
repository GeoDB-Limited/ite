package com.geodb.ite.iri.manager.services;

public interface IRIService {
	public static final String CONNECTED = "com.geodb.ite.iri.manager.services.iri.connected";
	
	void start();
	boolean isConnected();
	void stop();
}

package com.geodb.ite.iri.manager.services.events.iri;

public interface IRIServiceEvents {
	String TOPIC_BASE = "IRI";

	String ALL = TOPIC_BASE + "/*";

	String IRI_STARTED = TOPIC_BASE + "/STARTED";
	String IRI_STOPED = TOPIC_BASE + "/STOPED";
	String IRI_UPDATED = TOPIC_BASE + "/UPDATED";
	
	String FIELD_CONNECTED = "connected";
}

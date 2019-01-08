package com.geodb.ite.util.services;

import java.util.Map;

import com.geodb.ite.util.model.RunnableScript;

public interface JS {
	RunnableScript execute(String code);
	Object eval(String code);
	Map<Long, RunnableScript> getExecutions();
}

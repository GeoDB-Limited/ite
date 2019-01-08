package com.geodb.ite.util.services.providers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.geodb.ite.util.model.RunnableScript;
import com.geodb.ite.util.services.JS;
import com.geodb.ite.util.services.events.JSEvents;

public class JSProvider implements JS {

	private ScriptEngine engine;
	private Map<Long, RunnableScript> executions;

	@Inject
	private IEclipseContext context;

	@Inject
	private IEventBroker broker;

	@PostConstruct
	public void initialize() {
		createStructures();
		createScriptEngine();
	}

	private void createStructures() {
		executions = new HashMap<>();
	}

	private void createScriptEngine() {
		ScriptEngineManager factory = new ScriptEngineManager();
		engine = factory.getEngineByName("JavaScript");
		loadScripts();
	}

	private void loadScripts() {
		loadScript("platform:/plugin/com.geodb.ite.util/js/prepare/nashorn-polyfill.js");
		loadScript("platform:/plugin/com.geodb.ite.util/js/libs/iota.min.js");
	}

	private void loadScript(String script) {
		try {
			URL url = new URL(script);
			InputStream inputStream = url.openConnection().getInputStream();
		    Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(inputStream)));
			String code = scanner.useDelimiter("\\A").next();
			scanner.close();
			engine.eval(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public RunnableScript execute(String code) {
		RunnableScript result = null;
		result = ContextInjectionFactory.make(RunnableScript.class, context);
		result.setCode(code);
		result.run();
		executions.put(result.getTimestamp(), result);
		broker.post(JSEvents.EVAL_ADD, result);
		return result;
	}

	@Override
	public Object eval(String code) {
		Object result = null;
		try {
			result = engine.eval(code);
		} catch (Exception e) {
			result = e;
		}
		return result;
	}

	@Override
	public Map<Long, RunnableScript> getExecutions() {
		return executions;
	}

}

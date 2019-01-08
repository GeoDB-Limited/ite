package com.geodb.ite.util.services.contextfunctions;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;

import com.geodb.ite.util.services.JS;
import com.geodb.ite.util.services.providers.JSProvider;

public class JSContextFunction extends ContextFunction {
	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		JS service = ContextInjectionFactory.make(JSProvider.class, context);
		context = context.get(MApplication.class).getContext();
		context.set(JS.class, service);
		return service;
	}
}

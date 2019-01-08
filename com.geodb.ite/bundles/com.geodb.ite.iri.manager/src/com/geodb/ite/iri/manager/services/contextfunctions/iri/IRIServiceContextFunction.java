package com.geodb.ite.iri.manager.services.contextfunctions.iri;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;

import com.geodb.ite.iri.manager.services.IRIService;
import com.geodb.ite.iri.manager.services.providers.iri.IRIServiceProvider;

public class IRIServiceContextFunction extends ContextFunction {
	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		IRIService service = ContextInjectionFactory.make(IRIServiceProvider.class, context);
		context = context.get(MApplication.class).getContext();
		context.set(IRIService.class, service);
		return service;
	}
}

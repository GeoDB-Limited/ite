package com.geodb.ite.iri.manager.services.contextfunctions.coordinator;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;

import com.geodb.ite.iri.manager.services.CoordinatorService;
import com.geodb.ite.iri.manager.services.providers.coordinator.PeriodicCoordinatorServiceProvider;

public class CoordinatorServiceContextFunction extends ContextFunction {
	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		CoordinatorService service = ContextInjectionFactory.make(PeriodicCoordinatorServiceProvider.class, context);
		context = context.get(MApplication.class).getContext();
		context.set(CoordinatorService.class, service);
		return service;
	}
}

package com.geodb.ite.iri.manager.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.geodb.ite.iri.manager.services.IRIService;

public class IRIStopHandler {
	@Execute
	public void execute(IRIService iri) {
		iri.stop();
	}

	@CanExecute
	public boolean canExecute(IRIService iri) {
		return iri.isConnected();
	}
}

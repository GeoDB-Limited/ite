package com.geodb.ite.util.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import com.geodb.ite.util.model.RunnableScript;
import com.geodb.ite.util.services.JS;

public class EvalConsoleHandler {

	public static final String ID = "com.geodb.ite.util.eval.console.command";
	public static final String PARAMETER_PART_ID = "com.geodb.ite.util.console.partdescriptor";

	@Inject
	private JS js;

	@Inject
	private EModelService modelService;

	@Inject
	private MApplication application;

	@Execute
	void execute() {
		ReadablePart part = getPart(PARAMETER_PART_ID);
		String code = (part != null) ? part.read() : null;
		if (code != null)
			executeCode(code);
	}

	private ReadablePart getPart(String partId) {
		MUIElement muiElement = getPartMUIElement(partId);
		return (muiElement != null) ? (ReadablePart) ((MPart) muiElement).getObject() : null;
	}

	private MUIElement getPartMUIElement(String partId) {
		return modelService.find(partId, application);
	}

	private RunnableScript executeCode(String code) {
		return js.execute(code);
	}
}

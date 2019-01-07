package com.geodb.ite.rcp.layout.addon;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.geodb.ite.rcp.layout.ids.IDS;

public class LayoutAddon implements IDS {

	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private EPartService partService;
	
	@Inject
	private EModelService modelService;
	
	@Inject
	private MApplication application;
	
	@PostConstruct
	public void init() {

		EventHandler handler = new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				createPart(PARTSTACK_LEFT, PART_EXPLORER, true, false, PartState.ACTIVATE);
				createPart(PARTSTACK_LEFT, PART_CONSOLE, true, false, PartState.ACTIVATE);
				createPart(PARTSTACK_RIGHT, PART_IRI_INFO, true, false, PartState.ACTIVATE);
				createPart(PARTSTACK_BOTTOM, PART_CONVERTER, true, false, PartState.ACTIVATE);
			}
		};
		eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, handler);
	}
	
	private MPart createPart(String partStack, String part, boolean visible, boolean closeable, PartState state) {
		MPart result = partService.createPart(part);
		MPartStack stack = (MPartStack) modelService.find(partStack, application);
		stack.getChildren().add(result);
		result.setVisible(visible);
		result.setCloseable(closeable);
		partService.showPart(part, state);
		return result;
	}
}

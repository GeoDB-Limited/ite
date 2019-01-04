package com.geodb.ite.rcp.layout.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * {@lin khttp://stackoverflow.com/questions/15894393}
 */
public class MinSizeAddon {

	private static final int WIDTH = 300;
	private static final int HEIGHT = 400;

	@PostConstruct
	public void init(final IEventBroker eventBroker) {
		EventHandler handler = new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				if (!UIEvents.isSET(event))
					return;

				Object objElement = event.getProperty(UIEvents.EventTags.ELEMENT);
				if (!(objElement instanceof MWindow))
					return;

				MWindow windowModel = (MWindow) objElement;
				Shell theShell = (Shell) windowModel.getWidget();
				if (theShell == null)
					return;

				theShell.setMinimumSize(WIDTH, HEIGHT);
			}
		};
		eventBroker.subscribe(UIEvents.UIElement.TOPIC_WIDGET, handler);
	}
}

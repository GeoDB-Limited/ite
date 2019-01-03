package com.geodb.ite.iri.manager.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.geodb.ite.iri.manager.services.IRIService;

import jota.IotaAPI;

public class InfoPart {

	private Label label;

	@Inject
	private IRIService iri;

	@Inject
	private UISynchronize sync;

	private IotaAPI api;

	@Inject
	private void setAPI(@Optional IotaAPI api) {
		this.api = api;
		updateAPIInfo();
	}

	@PostConstruct
	public void createPartControl(Composite parent) {
		iri.start();
		GridLayoutFactory
				.fillDefaults()
				.numColumns(1)
				.spacing(0, 0)
				.applyTo(parent);

		Button button = new Button(parent, SWT.BORDER);
		button.setText("Update");
		button.addListener(SWT.Selection, event -> {
			updateAPIInfo();
		});
		GridDataFactory
				.swtDefaults()
				.align(SWT.FILL, SWT.TOP)
				.grab(true, false)
				.applyTo(button);

		label = new Label(parent, SWT.BORDER);
		GridDataFactory
				.swtDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, true)
				.applyTo(label);
	}

	private void updateAPIInfo() {
		Job job = Job.create("Update API info", (ICoreRunnable) monitor -> {
			sync.asyncExec(() -> {
				if (label != null && !label.isDisposed()) {
					String msg = "";
					try {
						msg = api.getNodeInfo().toString();
					} catch (Exception e) {
						msg = "Not connected yet";
					}
					label.setText(msg);
				}
			});
		});
		job.schedule();
	}

	@PreDestroy
	void dispose() {
		label.dispose();
		iri.stop();
	}
}

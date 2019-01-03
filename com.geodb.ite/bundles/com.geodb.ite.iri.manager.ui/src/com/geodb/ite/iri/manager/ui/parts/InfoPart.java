package com.geodb.ite.iri.manager.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.geodb.ite.iri.manager.services.IRIService;
import com.geodb.ite.iri.manager.ui.parts.providers.FieldLabelProvider;
import com.geodb.ite.iri.manager.ui.parts.providers.InfoContentProvider;
import com.geodb.ite.iri.manager.ui.parts.providers.ObjectLabelProvider;

import jota.IotaAPI;
import jota.dto.response.GetNodeInfoResponse;

public class InfoPart {

	public static final String ID = "com.geodb.ite.iri.manager.info.part";

	public static final String PLUGIN_ID = "com.geodb.ite.iri.manager.ui";

	private Composite parent;
	private Composite topComposite;
	private Composite bottomComposite;

	private TableViewer viewer;
	private TableColumnLayout tableLayout;

	private IotaAPI api;

	@Inject
	private void setAPI(@Optional IotaAPI api) {
		this.api = api;
		setInput();
	}

	@Inject
	private IEclipseContext context;

	@Inject
	private InfoContentProvider infoContentProvider;

	@Inject
	private FieldLabelProvider fieldLabelProvider;

	@Inject
	private ObjectLabelProvider valueLabelProvider;

	@Inject
	private UISynchronize sync;

	@Inject
	private IRIService iri;

	@PostConstruct
	public void createControls(Composite parent) {
		this.parent = parent;
		setLayout();
		createInfoViewer();
		layoutTableColumns();
		setInput();

		setButtons();
	}

	private void setLayout() {
		GridLayoutFactory
				.fillDefaults()
				.spacing(0, 0)
				.applyTo(parent);

		topComposite = addWidget(new Composite(parent, SWT.NONE));
		GridDataFactory
				.fillDefaults()
				.grab(true, true)
				.applyTo(topComposite);

		tableLayout = new TableColumnLayout();
		topComposite.setLayout(tableLayout);

		bottomComposite = addWidget(new Composite(parent, SWT.NONE));
		GridDataFactory
				.swtDefaults()
				.align(SWT.CENTER, SWT.CENTER)
				.grab(true, false)
				.applyTo(bottomComposite);

		GridLayoutFactory
				.fillDefaults()
				.numColumns(2)
				.applyTo(bottomComposite);
	}

	private <T> T addWidget(T widget) {
		ContextInjectionFactory.inject(widget, context);
		return widget;
	}

	private void createInfoViewer() {
		viewer = new TableViewer(topComposite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		createResultsViewerColumns();
		setResultsViewerDesign();
		viewer.setContentProvider(infoContentProvider);
	}

	private void createResultsViewerColumns() {
		createTableViewerColumn("Field", fieldLabelProvider);
		createTableViewerColumn("Value", valueLabelProvider);
	}

	private void createTableViewerColumn(String text, ColumnLabelProvider labelProvider) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.LEFT);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setResizable(true);
		tableColumn.setText(text);
		tableViewerColumn.setLabelProvider(labelProvider);
	}

	private void setResultsViewerDesign() {
		Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
	}

	private void setInput() {
		Job job = Job.create("Update API info", (ICoreRunnable) monitor -> {
			sync.asyncExec(() -> {
				if (availableViewer()) {
					GetNodeInfoResponse input = null;
					try {
						input = (api != null) ? api.getNodeInfo() : null;
					} catch (Exception e) {
					}
					viewer.setInput(input);
					layoutTableColumns();
				}
			});
		});
		job.schedule();
	}

	private boolean availableViewer() {
		return ((viewer != null) && !viewer.getTable().isDisposed());
	}

	private void layoutTableColumns() {
		TableColumn[] columns = viewer.getTable().getColumns();
		tableLayout.setColumnData(columns[0], new ColumnWeightData(100, 0));
		tableLayout.setColumnData(columns[1], new ColumnWeightData(100, 0));
		columns[0].pack();
	}

	private void setButtons() {
		addButton("&Start", e -> {
			Button b = ((Button) e.widget);
			String state = b.getText();
			if ("&Start".equals(state)) {
				b.setText("&Stop");
				iri.start();
			} else {
				b.setText("&Start");
				iri.stop();
				setInput();
			}
		});
		addButton("&Update", e -> setInput());
	}

	private Button addButton(String label, Listener l) {
		Button b = addWidget(new Button(bottomComposite, SWT.BORDER));
		GridDataFactory
				.swtDefaults()
				.align(SWT.CENTER, SWT.CENTER)
				.grab(false, false)
				.applyTo(b);
		b.setText(label);
		b.addListener(SWT.Selection, l);
		return b;
	}
}

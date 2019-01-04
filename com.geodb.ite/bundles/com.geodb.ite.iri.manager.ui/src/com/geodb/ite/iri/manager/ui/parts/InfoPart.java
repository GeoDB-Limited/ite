package com.geodb.ite.iri.manager.ui.parts;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.geodb.ite.iri.manager.services.IRIService;
import com.geodb.ite.iri.manager.services.events.iri.IRIServiceEvents;
import com.geodb.ite.iri.manager.ui.parts.providers.ConfigContentProvider;
import com.geodb.ite.iri.manager.ui.parts.providers.FieldLabelProvider;
import com.geodb.ite.iri.manager.ui.parts.providers.IRIConfig;
import com.geodb.ite.iri.manager.ui.parts.providers.InfoContentProvider;
import com.geodb.ite.iri.manager.ui.parts.providers.ObjectLabelProvider;

import jota.IotaAPI;
import jota.dto.response.GetNodeInfoResponse;

public class InfoPart {

	public static final String ID = "com.geodb.ite.iri.manager.info.part";

	public static final String PLUGIN_ID = "com.geodb.ite.iri.manager.ui";

	private static final String START_TEXT = "&Start";
	private static final String STOP_TEXT = "&Stop";
	private static final String UPDATE_TEXT = "&Update";

	private Composite parent;
	private Composite topComposite;
	private Composite centerComposite;
	private Composite bottomComposite;

	private Button buttonStatus;
	private Button buttonUpdate;

	private TableViewer configViewer, infoViewer;
	private TableColumnLayout tableLayoutConfig, tableLayoutInfo;

	private IotaAPI api;

	@Inject
	private void setAPI(@Optional IotaAPI api) {
		this.api = api;
		setInput();
	}

	@Inject
	private IEclipseContext context;

	@Inject
	private ConfigContentProvider configContentProvider;

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

	@Inject
	private IRIConfig iriConfig;

	@PostConstruct
	public void createControls(Composite parent) {
		this.parent = parent;
		setLayout();
		createConfigViewer();
		createInfoViewer();
		layoutTablesColumns();
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
				.minSize(300, 140)
				.applyTo(topComposite);

		tableLayoutConfig = new TableColumnLayout();
		topComposite.setLayout(tableLayoutConfig);

		centerComposite = addWidget(new Composite(parent, SWT.NONE));
		GridDataFactory
				.fillDefaults()
				.grab(true, true)
				.minSize(300, 140)
				.applyTo(centerComposite);

		tableLayoutInfo = new TableColumnLayout();
		centerComposite.setLayout(tableLayoutInfo);

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

	private void createConfigViewer() {
		configViewer = new TableViewer(topComposite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		createConfigViewerColumns();
		setTableViewerDesign(configViewer);
		configViewer.setContentProvider(configContentProvider);
	}

	private void createConfigViewerColumns() {
		createTableViewerColumn(configViewer, "Config", fieldLabelProvider);
		createTableViewerColumn(configViewer, "Value", valueLabelProvider);
	}

	private void createTableViewerColumn(TableViewer viewer, String text, ColumnLabelProvider labelProvider) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.LEFT);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setResizable(true);
		tableColumn.setText(text);
		tableViewerColumn.setLabelProvider(labelProvider);
	}

	private void setTableViewerDesign(TableViewer viewer) {
		Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
	}

	private void createInfoViewer() {
		infoViewer = new TableViewer(centerComposite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		createInfoViewerColumns();
		setTableViewerDesign(infoViewer);
		infoViewer.setContentProvider(infoContentProvider);
	}

	private void createInfoViewerColumns() {
		createTableViewerColumn(infoViewer, "Field", fieldLabelProvider);
		createTableViewerColumn(infoViewer, "Value", valueLabelProvider);
	}

	private void setInput() {
		setConfigInput();
		setInfoInput();
	}

	private void setConfigInput() {
		Job job = Job.create("Update API info", (ICoreRunnable) monitor -> {
			sync.asyncExec(() -> {
				if (availableViewer(configViewer)) {
					configViewer.setInput(iriConfig);
					iriConfig.register(new IRIConfig.Listener() {
						@Override
						public void layout() {
							layoutTableColumns(configViewer, tableLayoutConfig);
						}

						@Override
						public Viewer getViewer() {
							return configViewer;
						}
					});
					layoutTableColumns(configViewer, tableLayoutConfig);
				}
			});
		});
		job.schedule();
	}

	private void setInfoInput() {
		Job job = Job.create("Update API info", (ICoreRunnable) monitor -> {
			sync.asyncExec(() -> {
				if (availableViewer(infoViewer)) {
					GetNodeInfoResponse input = null;
					try {
						input = (iri.isConnected()) ? api.getNodeInfo() : null;
					} catch (Exception e) {
					}
					infoViewer.setInput(input);
					layoutTableColumns(infoViewer, tableLayoutInfo);
				}
			});
		});
		job.schedule();
	}

	private boolean availableViewer(TableViewer viewer) {
		return ((viewer != null) && !viewer.getTable().isDisposed());
	}

	private void layoutTablesColumns() {
		layoutTableColumns(configViewer, tableLayoutConfig);
		layoutTableColumns(infoViewer, tableLayoutInfo);
	}

	private void layoutTableColumns(TableViewer viewer, TableColumnLayout layout) {
		TableColumn[] columns = viewer.getTable().getColumns();
		layout.setColumnData(columns[0], new ColumnWeightData(50, 0));
		layout.setColumnData(columns[1], new ColumnWeightData(50, 0));
		columns[0].pack();
	}

	private void setButtons() {
		buttonStatus = addButton(computeStatusText(), e -> {
			buttonStatus.setEnabled(false);
			buttonUpdate.setEnabled(false);
			doIRIStatusAction();
		});
		buttonUpdate = addButton(UPDATE_TEXT, e -> setInfoInput());
		buttonUpdate.setEnabled(false);
	}

	private String computeStatusText() {
		return iri.isConnected() ? STOP_TEXT : START_TEXT;
	}

	private void doIRIStatusAction() {
		if (iri.isConnected())
			executeAction("Stop IRI", () -> iri.stop());
		else
			executeAction("Start IRI", () -> iri.start());

	}

	private void executeAction(String label, Runnable r) {
		Job job = Job.create(label, (ICoreRunnable) monitor -> {
			sync.asyncExec(r);
		});
		job.schedule();
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

	@PreDestroy
	private void dispose() {
		if (iri.isConnected()) {
			executeAction("Stop IRI", () -> iri.stop());
		}
	}

	@Inject
	@Optional
	private void subscribeTopicIRIStarted(@UIEventTopic(IRIServiceEvents.IRI_STARTED) Map<String, String> event) {
		buttonStatus.setEnabled(true);
		buttonUpdate.setEnabled(true);
		buttonStatus.setText(STOP_TEXT);
	}

	@Inject
	@Optional
	private void subscribeTopicIRIStoped(@UIEventTopic(IRIServiceEvents.IRI_STOPED) Map<String, String> event) {
		buttonStatus.setEnabled(true);
		buttonStatus.setText(START_TEXT);
	}
}

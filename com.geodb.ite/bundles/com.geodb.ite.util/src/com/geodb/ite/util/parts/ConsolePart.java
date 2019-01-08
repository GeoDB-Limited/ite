package com.geodb.ite.util.parts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

import com.geodb.ite.util.composites.OutputLineStyler;
import com.geodb.ite.util.composites.OutputStyledText;
import com.geodb.ite.util.composites.ScriptEditor;
import com.geodb.ite.util.handlers.ReadablePart;
import com.geodb.ite.util.model.RunnableScript;
import com.geodb.ite.util.services.JS;
import com.geodb.ite.util.services.events.JSEvents;

public class ConsolePart implements ReadablePart {

	private static final String CONSOLE_CLASS = "ConsoleClass";
	private static final String HOUR_FORMAT = "HH:mm:ss.SSS";
	private static SimpleDateFormat sdf = new SimpleDateFormat(HOUR_FORMAT);

	private SashForm container;
	private ScriptEditor editor;
	private OutputStyledText output;

	private Map<Long, Object> evals;

	@Inject
	private JS js;

	@Inject
	private IEclipseContext context;

	@Inject
	private IStylingEngine engine;

	@Inject
	public ConsolePart() {
		evals = new HashMap<>();
	}

	@PostConstruct
	private void createControls(Composite parent) {
		GridLayoutFactory
				.fillDefaults()
				.applyTo(parent);

		container = addWidget(new SashForm(parent, SWT.VERTICAL));
		GridDataFactory
				.fillDefaults()
				.grab(true, true)
				.align(SWT.FILL, SWT.FILL)
				.applyTo(container);

		styleComposite();
		createConsole();
		container.setWeights(new int[] { 2, 1 });
		setOutput();
	}

	private <T> T addWidget(T widget) {
		ContextInjectionFactory.inject(widget, context);
		return widget;
	}

	private void styleComposite() {
		engine.setClassname(container, CONSOLE_CLASS);
	}

	private void createConsole() {
		createScriptEditor();
		createOutputPanel();
	}

	private void createScriptEditor() {
		editor = addWidget(new ScriptEditor(container));
	}

	private void createOutputPanel() {
		output = addWidget(new OutputStyledText(container));
	}

	private void setOutput() {
		Map<Long, RunnableScript> serviceExecutions = js.getExecutions();
		for (long t : serviceExecutions.keySet()) {
			if (!evals.containsKey(t)) {
				addEval(serviceExecutions.get(t));
			}
		}
	}

	private void addEval(RunnableScript runnableScript) {
		StringBuilder output = new StringBuilder();
		output.append(generateOutput(runnableScript));
		output.append(this.output.getText());
		this.output.setText(output.toString());
		saveEval(runnableScript);
	}

	private StringBuilder generateOutput(RunnableScript runnableScript) {
		StringBuilder result = new StringBuilder();
		result.append(appendDate(runnableScript));
		result.append(appendValue(runnableScript));
		if (!this.output.getText().isEmpty())
			result.append("\n");
		return result;
	}

	private String appendDate(RunnableScript runnableScript) {
		return getDate(runnableScript) + "\n";
	}

	private String getDate(RunnableScript runnableScript) {
		return generateDateText((runnableScript != null) ? runnableScript.getTimestamp() : System.currentTimeMillis());
	}

	private String generateDateText(long time) {
		return OutputLineStyler.ITE_MARK + " " + date(time);
	}

	private String date(long time) {
		return sdf.format(new Date(time));
	}

	private String appendValue(RunnableScript runnableScript) {
		return adaptValue(getValue(runnableScript)) + "\n";
	}

	private String adaptValue(String value) {
		return (value.isEmpty()) ? "Empty output" : value;
	}

	private String getValue(RunnableScript runnableScript) {
		if (runnableScript != null) {
			if (runnableScript.getError() != null) {
				return runnableScript.getError().getLocalizedMessage();
			} else {
				if (runnableScript.getResult() != null) {
					return runnableScript.getResult().toString();
				}
			}
		}
		return "";
	}

	private void saveEval(RunnableScript runnableScript) {
		evals.put(runnableScript.getTimestamp(), runnableScript);
	}

	@Override
	public String read() {
		return (isAvailable()) ? editor.getText() : null;
	}

	private boolean isAvailable() {
		return (container != null) && !container.isDisposed();
	}

	public void clear() {
		if (isAvailable())
			editor.setText("");
	}

	public void paste(String text) {
		if (isAvailable()) {
			if (text != null) {
				int offset = editor.getCaretOffset();
				editor.insert(text);
				editor.setCaretOffset(offset + text.length());
			}
		}
	}

	@Inject
	@Optional
	private void subscribeTopicEvalAdd(@UIEventTopic(JSEvents.EVAL_ADD) RunnableScript script) {
		if (isAvailable())
			setOutput();
	}

}

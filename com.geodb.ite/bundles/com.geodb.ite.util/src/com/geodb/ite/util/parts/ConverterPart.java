package com.geodb.ite.util.parts;

import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import jota.IotaAPI;
import jota.utils.TrytesConverter;

public class ConverterPart {

	@SuppressWarnings("unused")
	private IotaAPI api;

	@Inject
	private IStylingEngine engine;

	private Composite parent;
	private Text trytesText, asciiText;

	private ModifyListener trytesListener, asciiListener;

	@Inject
	private void setAPI(@Optional IotaAPI api) {
		this.api = api;
	}

	@PostConstruct
	public void createControls(Composite parent) {
		this.parent = parent;
		setLayout();
		createInputs();
		hookListeners();
	}

	private void setLayout() {
		GridLayoutFactory
				.fillDefaults()
				.numColumns(2)
				.equalWidth(true)
				.spacing(4, 4)
				.margins(6, 6)
				.applyTo(parent);
	}

	private void createInputs() {
		Label label = new Label(parent, SWT.NONE);
		label.setText("ASCII");
		engine.setClassname(label, "TitleClass");

		GridDataFactory
				.swtDefaults()
				.align(SWT.CENTER, SWT.CENTER)
				.grab(true, false)
				.applyTo(label);

		label = new Label(parent, SWT.NONE);
		label.setText("TRYTES");
		engine.setClassname(label, "TitleClass");

		GridDataFactory
				.swtDefaults()
				.align(SWT.CENTER, SWT.CENTER)
				.grab(true, false)
				.applyTo(label);

		asciiText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridDataFactory
				.fillDefaults()
				.grab(true, true)
				.applyTo(asciiText);

		trytesText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridDataFactory
				.fillDefaults()
				.grab(true, true)
				.applyTo(trytesText);
	}

	private void hookListeners() {
		asciiText.addModifyListener(
				asciiListener = (e) -> processModification((t) -> TrytesConverter.toTrytes(t.getText()), asciiText,
						trytesText, trytesListener));
		trytesText.addModifyListener(
				trytesListener = (e) -> processModification((t) -> TrytesConverter.toString(t.getText()), trytesText,
						asciiText, asciiListener));
	}

	private void processModification(Function<Text, String> converter, Text input, Text output,
			ModifyListener outputListener) {

		String text = converter.apply(input);
		if (text == null)
			text = "";

		output.removeModifyListener(outputListener);
		output.setText(text);
		output.addModifyListener(outputListener);
	}
}

package com.geodb.ite.util.composites;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class OutputStyledText extends StyledText {

	private static final int DEFAULT_STYLE = SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL;
	private Font font;

	@Inject
	private OutputLineStyler styler;

	public OutputStyledText(Composite parent) {
		super(parent, DEFAULT_STYLE);
	}

	@PostConstruct
	private void createControl(Display display) {
		font = JFaceResources.getTextFont();
		setFont(font);
		addLineStyleListener(styler);
	}

	@Override
	public void dispose() {
		font.dispose();
		styler.disposeColors();
		removeLineStyleListener(styler);
	}

}

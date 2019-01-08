package com.geodb.ite.util.composites;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ScriptEditor extends StyledText {
	private static final int DEFAULT_STYLE = SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL;
	private Font font;

	private ScriptLineStyler styler;

	@Inject
	private IEclipseContext context;

	public ScriptEditor(Composite parent) {
		super(parent, DEFAULT_STYLE);
	}

	public ScriptEditor(Composite parent, int additionalStyle) {
		super(parent, DEFAULT_STYLE | additionalStyle);
	}

	@PostConstruct
	private void createControl(Display display) {
		setFont(font = new Font(display, "Monospaced", 10, SWT.NONE));
		styler = null;
		bindStyler();
	}

	private void bindStyler() {
		if (styler != null) {
			disposeStyler();
		}
		styler = ContextInjectionFactory.make(ScriptLineStyler.class, context);
		addLineStyleListener(styler);
		styler.bind(this);
		redraw();
	}

	private void disposeStyler() {
		styler.unbind();
		styler.disposeColors();
		this.removeLineStyleListener(styler);
	}

	@Override
	public void dispose() {
		font.dispose();
		disposeStyler();
	}
}

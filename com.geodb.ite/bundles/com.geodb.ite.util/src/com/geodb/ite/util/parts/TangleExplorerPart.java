package com.geodb.ite.util.parts;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

public class TangleExplorerPart {

	@PostConstruct
	public void createControls(Composite parent) {
		System.out.println("Explorer");
	}
}

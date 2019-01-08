package com.geodb.ite.util.composites;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

@Creatable
public class OutputLineStyler implements LineStyleListener {

	public static final String ITE_MARK = "#ITE#";

	private static final int EOF = -1;
	private static final int EOL = 10;

	private static final int COMMENT = 0;
	private static final int OTHER = 1;

	int[] tokenColors;
	List<int[]> blockComments = new ArrayList<>();
	private ITEScanner scanner;
	Color commentColor;

	@Inject
	public OutputLineStyler() {
		initializeColors();
	}

	@PostConstruct
	void createScanner() {
		scanner = new ITEScanner();
	}

	void initializeColors() {
		Display display = Display.getDefault();
		commentColor = new Color(display, new RGB(0, 0, 255));
	}

	void disposeColors() {
		commentColor.dispose();
	}

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		List<StyleRange> styles = new ArrayList<>();
		int token;
		scanner.setRange(event.lineText);
		token = scanner.nextToken();
		StyleRange style;
		while (token != EOF) {
			if (token == COMMENT) {
				style = new StyleRange(scanner.getStartOffset() + event.lineOffset, scanner.getLength(), commentColor,
						null);
				style.fontStyle = SWT.BOLD;
				styles.add(style);
			}
			token = scanner.nextToken();
		}
		event.styles = styles.toArray(new StyleRange[styles.size()]);
	}

	public class ITEScanner {

		protected StringBuffer fBuffer = new StringBuffer();
		protected String fDoc;
		protected int fPos;
		protected int fEnd;
		protected int fStartToken;
		protected boolean fEofSeen = false;

		/**
		 * Returns the ending location of the current token in the document.
		 */
		public final int getLength() {
			return fPos - fStartToken;
		}

		/**
		 * Returns the starting location of the current token in the document.
		 */
		public final int getStartOffset() {
			return fStartToken;
		}

		/**
		 * Returns the next lexical token in the document.
		 */
		public int nextToken() {
			int c;
			fStartToken = fPos;
			while (true) {
				switch (c = read()) {
				case EOF:
					return EOF;
				case '#': // MAISA Mark
					fBuffer.setLength(0);
					do {
						fBuffer.append((char) c);
						c = read();
					} while (!Character.isWhitespace((char) c));
					unread(c);
					if (ITE_MARK.equals(fBuffer.toString())) {
						while (true) {
							c = read();
							if ((c == EOF) || (c == EOL)) {
								unread(c);
								return COMMENT;
							}
						}
					}
					unread(c);
					return OTHER;
				default:
					return OTHER;
				}
			}
		}

		/**
		 * Returns next character.
		 */
		protected int read() {
			if (fPos <= fEnd) {
				return fDoc.charAt(fPos++);
			}
			return EOF;
		}

		public void setRange(String text) {
			fDoc = text;
			fPos = 0;
			fEnd = fDoc.length() - 1;
		}

		protected void unread(int c) {
			if (c != EOF)
				fPos--;
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.geodb.ite.util.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Adapted from JavaLineStyler in SWTExamples
 */
@Creatable
public class ScriptLineStyler implements LineStyleListener {

	public static final int EOF = -1;
	public static final int EOL = 10;

	public static final int WORD = 0;
	public static final int WHITE = 1;
	public static final int COMMENT = 2;
	public static final int STRING = 3;
	public static final int OTHER = 4;
	public static final int NUMBER = 5;
	public static final int KEY = 6;
	public static final int PROPERTY = 7;

	public static final int MAXIMUM_TOKEN = 8;

	int[] tokenColors;
	Color[] colors;
	List<int[]> blockComments = new ArrayList<>();
	private JavaScriptScanner scanner;
	private StyledText styledText = null;
	private PaintObjectListener paintObjectListener = null;
	private ModifyListener modifyListener = null;

	@Inject
	private Display display;

	@Inject
	public ScriptLineStyler() {
		initializeColors();
	}

	@PostConstruct
	void createScanner() {
		scanner = new JavaScriptScanner();
	}

	public boolean isBinded() {
		return styledText != null;
	}

	public void bind(StyledText styledText) {
		this.styledText = styledText;
		styledText.addPaintObjectListener(paintObjectListener = event -> drawBullet(
				event.bullet,
				event.gc,
				event.x,
				event.y,
				event.bulletIndex,
				event.ascent,
				event.descent));
		styledText.addModifyListener(modifyListener = event -> styledText.redraw());
	}

	public void unbind() {
		if (isBinded()) {
			if (paintObjectListener != null) {
				styledText.removePaintObjectListener(paintObjectListener);
			}
			if (modifyListener != null) {
				styledText.removeModifyListener(modifyListener);
			}
		}
	}

	Color getColor(int type) {
		if (type < 0 || type >= tokenColors.length) {
			return null;
		}
		return colors[tokenColors[type]];
	}

	boolean inBlockComment(int start, int end) {
		for (int i = 0; i < blockComments.size(); i++) {
			int[] offsets = blockComments.get(i);
			// start of comment in the line
			if ((offsets[0] >= start) && (offsets[0] <= end))
				return true;
			// end of comment in the line
			if ((offsets[1] >= start) && (offsets[1] <= end))
				return true;
			if ((offsets[0] <= start) && (offsets[1] >= end))
				return true;
		}
		return false;
	}

	void initializeColors() {
		Display display = Display.getDefault();
		colors = new Color[] {
				new Color(display, new RGB(0, 0, 0)), // black
				new Color(display, new RGB(210, 180, 140)), // tan
				new Color(display, new RGB(165, 42, 42)), // brown
				new Color(display, new RGB(128, 0, 0)), // maroon
				new Color(display, new RGB(255, 69, 0)), // orangered
				new Color(display, new RGB(136, 69, 19)), // saddlebrown
				new Color(display, new RGB(210, 105, 0)), // chocolate
				new Color(display, new RGB(235, 235, 235)), // light grey
				new Color(display, new RGB(169, 169, 169)) // dark gray
		};
		tokenColors = new int[MAXIMUM_TOKEN];
		tokenColors[WORD] = 0;
		tokenColors[WHITE] = 0;
		tokenColors[COMMENT] = 1;
		tokenColors[STRING] = 2;
		tokenColors[OTHER] = 0;
		tokenColors[NUMBER] = 0;
		tokenColors[KEY] = 3;
		tokenColors[PROPERTY] = 4;
	}

	void disposeColors() {
		for (int i = 0; i < colors.length; i++) {
			colors[i].dispose();
		}
	}

	// http://stackoverflow.com/questions/17072473/swt-bullet-change-entire-background
	void drawBullet(Bullet bullet, GC gc, int paintX, int paintY, int index, int lineAscent, int lineDescent) {
		StyleRange style = bullet.style;
		GlyphMetrics metrics = style.metrics;
		Color color = style.foreground;
		if (color != null)
			gc.setForeground(color);
		Font font = style.font;
		if (font != null)
			gc.setFont(font);
		String string = "";
		int type = bullet.type & (ST.BULLET_DOT | ST.BULLET_CUSTOM | ST.BULLET_NUMBER | ST.BULLET_LETTER_LOWER
				| ST.BULLET_LETTER_UPPER);
		switch (type) {
		case ST.BULLET_DOT:
			string = "\u2022";
			break;
		case ST.BULLET_CUSTOM:
			string = String.valueOf(index + 1);
			break;
		case ST.BULLET_NUMBER:
			string = String.valueOf(index + 1);
			break;
		case ST.BULLET_LETTER_LOWER:
			string = String.valueOf((char) (index % 26 + 97));
			break;
		case ST.BULLET_LETTER_UPPER:
			string = String.valueOf((char) (index % 26 + 65));
			break;
		}
		if ((bullet.type & ST.BULLET_TEXT) != 0)
			string += bullet.text;

		gc.setBackground(style.background);
		gc.fillRectangle(paintX, paintY, metrics.width - 5, styledText.getLineHeight());

		TextLayout layout = new TextLayout(display);
		layout.setText(string);
		layout.setAscent(lineAscent);
		layout.setDescent(lineDescent);
		style = (StyleRange) style.clone();
		style.metrics = null;
		if (style.font == null)
			style.font = styledText.getFont();
		layout.setStyle(style, 0, string.length());
		int x = paintX + Math.max(0, metrics.width - layout.getBounds().width - 10);
		layout.draw(gc, x, paintY);
		layout.dispose();
	}

	/**
	 * Event.detail line start offset (input) Event.text line text (input)
	 * LineStyleEvent.styles Enumeration of StyleRanges, need to be in order.
	 * (output) LineStyleEvent.background line background color (output)
	 */
	@Override
	public void lineGetStyle(LineStyleEvent event) {
		List<StyleRange> styles = new ArrayList<>();
		int token;
		StyleRange lastStyle;
		// Add line number

		// Set the line number
		StyledText text = (StyledText) event.widget;
		event.bulletIndex = text.getLineAtOffset(event.lineOffset);

		StyleRange style = new StyleRange(0, 0, colors[8], colors[7]);
		style.metrics = new GlyphMetrics(0, 0, 32);

		// Create and set the bullet
		event.bullet = new org.eclipse.swt.custom.Bullet(ST.BULLET_CUSTOM, style);

		// If the line is part of a block comment, create one style for the
		// entire line.
		if (inBlockComment(event.lineOffset, event.lineOffset + event.lineText.length())) {
			styles.add(new StyleRange(event.lineOffset, event.lineText.length(), getColor(COMMENT), null));
			event.styles = styles.toArray(new StyleRange[styles.size()]);
			return;
		}
		Color defaultFgColor = ((Control) event.widget).getForeground();
		scanner.setRange(event.lineText);
		token = scanner.nextToken();
		while (token != EOF) {
			if (token == OTHER) {
				// do nothing for non-colored tokens
			} else if (token != WHITE) {
				Color color = getColor(token);
				// Only create a style if the token color is different than the
				// widget's default foreground color and the token's style is
				// not
				// bold. Keywords are bolded.
				if ((!color.equals(defaultFgColor)) || (token == KEY)) {
					style = new StyleRange(scanner.getStartOffset() + event.lineOffset, scanner.getLength(),
							color, null);
					if ((token == KEY) || (token == PROPERTY)) {
						style.fontStyle = SWT.BOLD;
					}
					if (styles.isEmpty()) {
						styles.add(style);
					} else {
						// Merge similar styles. Doing so will improve
						// performance.
						lastStyle = styles.get(styles.size() - 1);
						if (lastStyle.similarTo(style) && (lastStyle.start + lastStyle.length == style.start)) {
							lastStyle.length += style.length;
						} else {
							styles.add(style);
						}
					}
				}
			} else if ((!styles.isEmpty()) && ((lastStyle = styles.get(styles.size() - 1)).fontStyle == SWT.BOLD)) {
				int start = scanner.getStartOffset() + event.lineOffset;
				lastStyle = styles.get(styles.size() - 1);
				// A font style of SWT.BOLD implies that the last style
				// represents a java keyword.
				if (lastStyle.start + lastStyle.length == start) {
					// Have the white space take on the style before it to
					// minimize the number of style ranges created and the
					// number of font style changes during rendering.
					lastStyle.length += scanner.getLength();
				}
			}
			token = scanner.nextToken();
		}
		event.styles = styles.toArray(new StyleRange[styles.size()]);
	}

	/**
	 * A simple fuzzy scanner for JavaScript
	 */
	public class JavaScriptScanner {

		protected Map<String, Integer> reservedKeywords = null;
		protected Map<String, Integer> reservedProperties = null;
		protected StringBuffer fBuffer = new StringBuffer();
		protected String fDoc;
		protected int fPos;
		protected int fEnd;
		protected int fStartToken;
		protected boolean fEofSeen = false;

		private String[] javaScriptReservedKeywords = {
				"abstract", "arguments", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
				"continue", "debugger", "default", "delete", "do", "double", "else", "enum", "eval", "export",
				"extends", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements",
				"import", "in", "instanceof", "int", "interface", "let", "long", "native", "new", "null", "package",
				"private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized",
				"this", "throw", "throws", "transient", "true", "try", "typeof", "var", "void", "volatile", "while",
				"with", "yield", "def", "println", "print"
		};

		private String[] javaScriptReservedProperties = {
				"Array", "Date", "eval", "function", "hasOwnProperty", "Infinity", "isFinite", "isNaN", "isPrototypeOf",
				"length", "Math", "NaN", "name", "Number", "Object", "prototype", "String", "toString", "undefined",
				"valueOf"
		};

		/**
		 * Returns the ending location of the current token in the document.
		 */
		public final int getLength() {
			return fPos - fStartToken;
		}

		public JavaScriptScanner() {
			initialize();
		}

		/**
		 * Initialize the lookup table.
		 */
		void initialize() {
			reservedKeywords = new HashMap<>();
			Integer k = Integer.valueOf(KEY);
			for (String word : javaScriptReservedKeywords) {
				reservedKeywords.put(word, k);
			}
			reservedProperties = new HashMap<>();
			k = Integer.valueOf(PROPERTY);
			for (String property : javaScriptReservedProperties) {
				reservedProperties.put(property, k);
			}
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
				case '/': // comment
					c = read();
					if (c == '/') {
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
				case '\'': // char const
					while (true) {
						c = read();
						switch (c) {
						case '\'':
							return STRING;
						case EOF:
							unread(c);
							return STRING;
						case '\\':
							c = read();
							break;
						}
					}

				case '"': // string
					while (true) {
						c = read();
						switch (c) {
						case '"':
							return STRING;
						case EOF:
							unread(c);
							return STRING;
						case '\\':
							c = read();
							break;
						}
					}

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					do {
						c = read();
					} while (Character.isDigit((char) c));
					unread(c);
					return NUMBER;
				default:
					if (Character.isWhitespace((char) c)) {
						do {
							c = read();
						} while (Character.isWhitespace((char) c));
						unread(c);
						return WHITE;
					}
					if (Character.isJavaIdentifierStart((char) c)) {
						fBuffer.setLength(0);
						do {
							fBuffer.append((char) c);
							c = read();
						} while (Character.isJavaIdentifierPart((char) c));
						unread(c);
						Integer i = reservedKeywords.get(fBuffer.toString());
						if (i != null)
							return i.intValue();
						i = reservedProperties.get(fBuffer.toString());
						if (i != null)
							return i.intValue();
						return WORD;
					}
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

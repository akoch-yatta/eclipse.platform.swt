package org.eclipse.swt.tests.win32.graphics;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

public class TextLayoutWin32Tests {
	private Display display;
	final static String text = "This is a text for testing.";

	@Before
	public void setUp() {
		display = Display.getDefault();
	}

	@Test
	public void getBoundPublicAPIshouldReturnTheSameValueRegardlessOfZoomLevel() {
		Shell shell1 = new Shell(display);
		Shell shell2 = new Shell(display);
		shell1.setText("Text Layout Win32 Test");
		shell2.setText("Text Layout Win32 Test Scaled");
		int scalingFactor = 2;
		int newZoom = shell1.zoom * scalingFactor;
		shell2.zoom = newZoom;
		final TextLayout layout = new TextLayout(display);
		layout.setText(text);
		shell1.addListener(SWT.Paint, event -> {
			layout.draw(event.gc, 10, 10);
		});
		shell2.addListener(SWT.Paint, event -> {
			layout.draw(event.gc, 10, 10);
		});
		shell1.open();
		Rectangle unscaledBounds = layout.getBounds();
		shell2.open();
		Rectangle scaledBounds = layout.getBounds();
		assertEquals("The public API for getBounds should give the same result for any zoom level", scaledBounds, unscaledBounds);
	}

}

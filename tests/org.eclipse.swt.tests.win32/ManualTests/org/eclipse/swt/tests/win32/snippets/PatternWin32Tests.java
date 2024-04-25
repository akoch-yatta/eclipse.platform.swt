package org.eclipse.swt.tests.win32.snippets;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

public class PatternWin32Tests {
	private Display display;

	@Before
	public void setUp() {
		display = Display.getDefault();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void patternMultiZoomLevelTest() {
		int zoom = DPIUtil.getDeviceZoom();
		int scaledZoom = zoom * 3;
		int width = 400;
		int height = 300;
		HashMap<Integer, Pattern> scaledPatterns = null;

		final Pattern pat = new Pattern(display, 0, 0, width, height, new Color(null, 200, 200, 200), 0, new Color(null, 255, 0, 0), 255);
		try {
			Field field = Pattern.class.getDeclaredField("scaledpattern");
			field.setAccessible(true);
			scaledPatterns = (HashMap<Integer, Pattern>) field.get(pat);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			assert(false);
		}

		Shell shell = new Shell(display);
		shell.setText("Unscaled shell");
		shell.setSize(width, height);

		shell.addPaintListener(e -> {
			e.gc.setBackground(new Color(null, 100, 200, 0));
			e.gc.fillRectangle(0, 0, shell.getBounds().width, shell.getBounds().height);
			e.gc.setBackground(new Color(null, 255, 0, 0));
			e.gc.setBackgroundPattern(pat);
			e.gc.fillRectangle(0, 0, shell.getBounds().width, shell.getBounds().height);
		});
		shell.open();
		assertTrue("Pattern with current zoom level should exist in the hashMap", scaledPatterns.containsKey(zoom));
		assertTrue("Pattern with scaled zoom level should not exist in the hashMap at the moment", !scaledPatterns.containsKey(scaledZoom));

		DPIUtil.setDeviceZoom(scaledZoom);
		Shell shell2 = new Shell(display);
		shell2.zoom = scaledZoom;
		shell2.setText("Scaled shell");
		shell2.setSize(width, height);
		shell2.addPaintListener(e -> {
			e.gc.setBackground(new Color(null, 100, 200, 0));
			e.gc.fillRectangle(0, 0, shell2.getBounds().width, shell2.getBounds().height);
			e.gc.setBackground(new Color(null, 255, 0, 0));
			e.gc.setBackgroundPattern(pat);
			e.gc.fillRectangle(0, 0, shell2.getBounds().width, shell2.getBounds().height);
		});

		shell2.open();
		assertTrue("Pattern with currentZoomLevel (scaled) should exist in the hashMap", scaledPatterns.containsKey(zoom * 3));

		while (!shell.isDisposed() || !shell2.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		pat.dispose();
		shell.dispose();
		shell2.dispose();
	}
}

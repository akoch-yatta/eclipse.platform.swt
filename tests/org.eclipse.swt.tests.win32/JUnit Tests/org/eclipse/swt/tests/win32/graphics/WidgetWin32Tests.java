package org.eclipse.swt.tests.win32.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WidgetWin32Tests {
	private Display display;
	private int initialZoom;

	@Before
	public void setUp() {
		initialZoom = DPIUtil.getDeviceZoom();
		changeAutoScaleOnRuntime(true);
		display = Display.getDefault();
		DPIUtil.setDeviceZoom(100);
	}

	@After
	public void tearDown() {
		DPIUtil.setDeviceZoom(initialZoom);
		changeAutoScaleOnRuntime(false);
	}

	@Test
	public void test() {
		int zoom = DPIUtil.getDeviceZoom();
		int scaledZoom = zoom * 2;
		Shell shell = new Shell(display);

		Button button = new Button(shell, SWT.PUSH);
		button.setBounds(0, 0, 200, 50);
		button.setText("Widget Test");
		button.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		shell.open();
		changeZoomLevel(scaledZoom, shell);
		assertEquals("The Zoom Level should be updated for button on zoom change event on its shell", scaledZoom, button.zoom);
	}

	public void changeZoomLevel(int zoomLevel, Control widget) {
		Event event = new Event();
		event.detail = zoomLevel;
		event.type = SWT.ZoomChanged;
		event.doit = true;
		event.widget = widget;
		DPIUtil.setDeviceZoom(zoomLevel);
		widget.notifyListeners(SWT.ZoomChanged, event);
	}

	private void changeAutoScaleOnRuntime(boolean value) {
		try {
			Field autoScaleOnRuntimeField = DPIUtil.class.getDeclaredField("autoScaleOnRuntime");
			autoScaleOnRuntimeField.setAccessible(true);
			autoScaleOnRuntimeField.setBoolean(null, value);
			autoScaleOnRuntimeField.setAccessible(false);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail("Value for DPI::autoScaleOnRuntime could not be changed");
		}
	}

}

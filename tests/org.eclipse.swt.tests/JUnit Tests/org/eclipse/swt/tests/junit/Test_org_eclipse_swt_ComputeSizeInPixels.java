/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.tests.junit;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.internal.SWTFontProvider;
import org.eclipse.swt.internal.win32.BITMAP;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Test_org_eclipse_swt_ComputeSizeInPixels {

	private Display display;
	private Shell shell;

	@Before
	public void setUp() {
		changeAutoScaleOnRuntime(true);
		display = new Display();
		SWTFontProvider.disposeFontRegistry(display);
	}

	@After
	public void tearDown() {
		display.dispose();
	}

	@Test
	public void testPointsAfterZooming()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		shell = new Shell(display);
		shell.setBounds(0, 0, 100, 160);
		shell.setLayout(new FillLayout());
		shell.pack();

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Button");
		button.setBounds(0, 0, 100, 200);
		Point sizeBeforeEvent = button.getSize();
		Point p1 = computeSizeInPixelsReflection(button, sizeBeforeEvent);

		System.out.println("Before Event");
		System.out.println(p1);

		// Zoom to 200
		changeZoomLevel(200, shell);

		Point sizeAfterEvent = button.getSize();
		Point p2 = computeSizeInPixelsReflection(button, sizeAfterEvent);

		System.out.println("After Event");
		System.out.println(p2);

		Assert.assertEquals("Width should be half in points after zooming to 200", sizeBeforeEvent.x,
				sizeAfterEvent.x * 2);
		Assert.assertEquals("Height should be half in points after zooming to 200", sizeBeforeEvent.y,
				sizeAfterEvent.y * 2);
	}

	@Test
	public void testImagePixelsAfterZooming() {
		shell = new Shell(display);
		shell.setBounds(0, 0, 100, 160);
		shell.setLayout(new FillLayout());
		shell.pack();

		Button button = new Button(shell, SWT.PUSH);
		InputStream inputStream = Test_org_eclipse_swt_ComputeSizeInPixels.class.getResourceAsStream("folder.png");
		Image image = new Image(display, inputStream);
		button.setText("Button");
		button.setBounds(0, 0, 100, 200);
		button.setImage(image);

		Point buttonImageSizeBeforeEvent = getImageDimension(image, 100);
		System.out.println("Button Size Before Event");
		System.out.println(getImageDimension(image, 100));

		Point buttonImageSizeAfterEvent = getImageDimension(image, 200);
		System.out.println("Button Size After Event");
		System.out.println(getImageDimension(image, 200));

		Assert.assertEquals("Width of a button image should be doubled after zooming to 200",
				buttonImageSizeBeforeEvent.x * 2, buttonImageSizeAfterEvent.x);
		Assert.assertEquals("Height of a button image should be doubled after zooming to 200",
				buttonImageSizeBeforeEvent.y * 2, buttonImageSizeAfterEvent.y);
	}

	@Test
	public void testButtonFontAfterZooming() {
		shell = new Shell(display);
		shell.setBounds(0, 0, 100, 160);
		shell.setLayout(new FillLayout());
		shell.pack();

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Button");
		button.setBounds(0, 0, 100, 200);
		Font font = new Font(display, "Arial", 12, SWT.BOLD);
		button.setFont(font);

		int heightBeforeZoom = button.getFont().getFontData()[0].data.lfHeight;
		System.out.println(heightBeforeZoom);

		// Zoom to 200
		changeZoomLevel(200, shell);

		int heightAfterZoom = button.getFont().getFontData()[0].data.lfHeight;
		System.out.println(heightAfterZoom);

		Assert.assertEquals("Height of a font of the button should be doubled after zooming to 200",
				heightBeforeZoom * 2, heightAfterZoom);
	}

	private Point computeSizeInPixelsReflection(Button button, Point point)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method method = Button.class.getDeclaredMethod("computeSizeInPixels", int.class, int.class, boolean.class);
		method.setAccessible(true);
		Point p = (Point) method.invoke(button, point.x, point.y, false);
		return p;
	}

	public void changeZoomLevel(int zoomLevel, Shell widget) {
		Event event = new Event();
		event.detail = zoomLevel;
		event.type = SWT.ZoomChanged;
		event.doit = true;
		event.widget = widget;
		widget.nativeZoom = zoomLevel;
		DPIUtil.setDeviceZoom(zoomLevel);
		widget.notifyListeners(SWT.ZoomChanged, event);

		RECT rect = new RECT();
		widget.setBounds(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
	}

	public Point getImageDimension(Image image, Integer zoomLevel) {
		BITMAP bm = new BITMAP();
		OS.GetObject(Image.win32_getHandle(image, zoomLevel), BITMAP.sizeof, bm);
		int imgWidth = bm.bmWidth;
		int imgHeight = bm.bmHeight;
		return new Point(imgWidth, imgHeight);
	}

	private void changeAutoScaleOnRuntime(boolean value) {
		try {
			Field autoScaleOnRuntimeField = DPIUtil.class.getDeclaredField("autoScaleOnRuntime");
			autoScaleOnRuntimeField.setAccessible(true);
			autoScaleOnRuntimeField.setBoolean(null, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			fail("Value for DPI::autoScaleOnRuntime could not be changed");
		}
	}
}

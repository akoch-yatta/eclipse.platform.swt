/*******************************************************************************
 * Copyright (c) 2024 Yatta Solutions
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yatta Solutions - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.graphics;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.awt.Point;
import java.lang.reflect.*;

import org.eclipse.swt.internal.win32.*;
import org.eclipse.swt.widgets.*;
import org.junit.*;
import org.mockito.*;

/**
 * Automated Tests for class org.eclipse.swt.graphics.Image
 * for Windows specific behavior
 *
 * @see org.eclipse.swt.graphics.Image
 */
public class ImageWin32Tests {
	private Display display;

	@Before
	public void setUp() {
		display = Display.getDefault();
	}

	@Test
	public void scalingOfImageInOnePlaceShouldNotAffectInOtherPlace() throws NoSuchMethodException, SecurityException {
		Image image = new Image(display, 10, 10);
		Method zoomMethod = GC.class.getDeclaredMethod("getDeviceZoomNullable");
		zoomMethod.setAccessible(true);

		Shell shell1 = new Shell(display);
		Shell shell2 = new Shell(display);
		shell2.zoom = shell2.zoom * 3;

		shell1.addPaintListener(e -> {
			GC gcSpy = spy(e.gc);
			e.gc = gcSpy;
			e.gc.drawImage(image, 10, 10);
			shell2.open();
			ArgumentCaptor<Image> imageCaptor = ArgumentCaptor.forClass(Image.class);
			verify(gcSpy).drawBitmap(imageCaptor.capture(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean());
			try {
				Integer gcZoom = (Integer) zoomMethod.invoke(e.gc);
				Point dimension = getImageDimension(imageCaptor.getValue(), gcZoom);
				assertEquals(dimension.x, 10);
				assertEquals(dimension.y, 10);
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				assert(false);
			}

		});
		shell2.addPaintListener(e -> {
			GC gcSpy = spy(e.gc);
			e.gc = gcSpy;
			e.gc.drawImage(image, 10, 10);
			ArgumentCaptor<Image> imageCaptor = ArgumentCaptor.forClass(Image.class);
			verify(gcSpy).drawBitmap(imageCaptor.capture(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean());
			try {
				Integer gcZoom = (Integer) zoomMethod.invoke(e.gc);
				Point dimension = getImageDimension(imageCaptor.getValue(), gcZoom);
				assertEquals(dimension.x, 30);
				assertEquals(dimension.y, 30);
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				assert(false);
			}
		});

		shell1.open();
		System.out.println(shell1);
	}

	public Point getImageDimension(Image image, Integer zoomLevel) {
		BITMAP bm = new BITMAP();
		OS.GetObject(Image.win32_getHandle(image, zoomLevel), BITMAP.sizeof, bm);
		int imgWidth = bm.bmWidth;
		int imgHeight = bm.bmHeight;
		return new Point(imgWidth, imgHeight);
	}
}

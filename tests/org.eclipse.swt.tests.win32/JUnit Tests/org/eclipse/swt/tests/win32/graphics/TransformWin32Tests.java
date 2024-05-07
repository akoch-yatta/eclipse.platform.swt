package org.eclipse.swt.tests.win32.graphics;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.internal.gdip.Gdip;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Test;

public class TransformWin32Tests {
	private Display display;

	@Before
	public void setUp() {
		display = Display.getDefault();
	}

	@Test
	public void shouldHaveDifferentHandlesAtDifferentZoomLevels() {
		int zoom = DPIUtil.getDeviceZoom();
		Transform transform = new Transform(display);
		long scaledHandle = Transform.win32_getHandle(transform, zoom * 2);
		assertNotEquals("There exist different handles for different zoom levels", scaledHandle, transform.handle);
		long scaledHandle2 = Transform.win32_getHandle(transform, zoom * 3);
		assertNotEquals("There exist different handles for different zoom levels", scaledHandle, scaledHandle2);
	}

	@Test
	public void scaledTrasformMustHaveScaledValues() {
		int zoom = DPIUtil.getDeviceZoom();
		Transform transform = new Transform(display, 0, 0, 0, 0, 4, 2);
		float[] elements = new float[6];
		transform.getElements(elements);
		long scaledHandle = Transform.win32_getHandle(transform, zoom * 2);
		float[] scaledElements = new float[6];
		Gdip.Matrix_GetElements(scaledHandle, scaledElements);
		assertEquals(elements[4] * 2, scaledElements[4], 0);
	}

}

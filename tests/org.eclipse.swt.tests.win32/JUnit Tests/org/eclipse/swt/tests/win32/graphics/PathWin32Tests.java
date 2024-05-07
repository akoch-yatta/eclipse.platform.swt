package org.eclipse.swt.tests.win32.graphics;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Test;

public class PathWin32Tests {
	private Display display;

	@Before
	public void setUp() {
		display = Display.getDefault();
	}

	@Test
	public void pathMustBeScaledOnZoomLevelChange() {
		int zoom = DPIUtil.getDeviceZoom();
		Path path = new Path(display);
		path.addArc(0, 0, 10, 10, 0, 90);
		long scaledHandle = Path.win32_getHandle(path, zoom * 2);
		PathData pathData = path.getPathData();
		path.handle = scaledHandle;
		PathData scaledPathData = path.getPathData();
		assertTrue("PathData types don't change on zoom level change", Arrays.equals(pathData.types, scaledPathData.types));
		for (int i = 0; i < pathData.points.length; i++) {
			pathData.points[i] = pathData.points[i] * 2;
		}
		assertTrue("PathData types don't change on zoom level change", Arrays.equals(pathData.points, scaledPathData.points));
	}
}

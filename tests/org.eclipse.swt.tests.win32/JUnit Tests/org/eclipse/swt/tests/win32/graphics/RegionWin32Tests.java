package org.eclipse.swt.tests.win32.graphics;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Test;

public class RegionWin32Tests {
	private Display display;

	@Before
	public void setUp() {
		display = Display.getDefault();
	}

	@Test
	public void regionMustBeScaledOnHandleOfScaledZoomLevel() {
		int zoom = DPIUtil.getDeviceZoom();
		Region region = new Region(display);
		region.add(0, 0, 5, 10);
		long scaledRegionHandle = Region.win32_getHandle(region, zoom * 2);
		Region scaledRegion = Region.win32_new(display, (int) scaledRegionHandle);
		Rectangle scaledBounds = scaledRegion.getBounds();
		Rectangle bounds = region.getBounds();
		assertEquals("scaled region's height should be double of unscaled region", bounds.height * 2, scaledBounds.height);
		assertEquals("scaled region's width should be double of unscaled region", bounds.width * 2, scaledBounds.width);
	}

}

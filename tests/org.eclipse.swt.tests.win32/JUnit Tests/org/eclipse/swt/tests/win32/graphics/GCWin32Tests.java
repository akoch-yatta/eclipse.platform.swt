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
package org.eclipse.swt.tests.win32.graphics;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GCWin32Tests {
	private Display display;
	private int initialZoom;

	@Before
	public void setUp() {
		initialZoom = DPIUtil.getDeviceZoom();
		display = Display.getDefault();
		DPIUtil.setDeviceZoom(100);
	}

	@After
	public void tearDown() {
		DPIUtil.setDeviceZoom(initialZoom);
	}

	@Test
	public void gcZoomLevelMustChangeOnShellZoomChange() {
		int zoom = DPIUtil.getDeviceZoom();
		Shell shell = new Shell(display);
		shell.addListener(SWT.Paint, event -> {
			assertEquals("GCData must have a zoom level equal to the actual zoom level of the widget/shell", shell.zoom, event.gc.getGCData().deviceZoom);
		});
		shell.addListener(SWT.ZoomChanged, event -> {
			assertEquals("GCData must have a zoom level equal to the actual zoom level of the widget/shell on zoomChanged event", shell.zoom, event.gc.getGCData().deviceZoom);
		});
		shell.open();
		int newSWTZoom = zoom * 2;
		Event swtEvent = new Event();
		swtEvent.type = SWT.ZoomChanged;
		swtEvent.gc = GC.win32_new(shell, new GCData());
		swtEvent.widget = shell;
		DPIUtil.setDeviceZoom(newSWTZoom);
		shell.zoom = newSWTZoom;
		shell.nativeZoom = DPIUtil.getZoomForAutoscaleProperty(newSWTZoom);
		shell.notifyListeners(SWT.ZoomChanged, swtEvent);
	}

	@Test
	public void drawnElementsShouldScaleUpToTheRightZoomLevel() {
		int zoom = DPIUtil.getDeviceZoom();
		int scalingFactor = 2;
		Shell shell = new Shell(display);
		GC gc = GC.win32_new(shell, new GCData());
		gc.getGCData().deviceZoom = zoom * scalingFactor;
		gc.getGCData().lineWidth = 10;
		assertEquals("DPIUtil calls with getDeviceZoom should scale to the right value", gc.getGCData().lineWidth, gc.getLineWidth() * scalingFactor, 0);
	}

}

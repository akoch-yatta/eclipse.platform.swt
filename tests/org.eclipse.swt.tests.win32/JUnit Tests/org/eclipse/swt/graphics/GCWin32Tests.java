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
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.internal.SWTFontProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GCWin32Tests {
	private Display display;

	@Before
	public void setUp() {
		changeAutoScaleOnRuntime(true);
		display = Display.getDefault();
	}

	@After
	public void tearDown() {
		changeAutoScaleOnRuntime(false);
	}

	@Test
	public void gcZoomLevelMustChangeOnShellZoomChange() {
		CompletableFuture<Integer> gcNativeZoom = new CompletableFuture<>();
		CompletableFuture<Integer> scaledGcNativeZoom = new CompletableFuture<>();
		int zoom = DPIUtil.getDeviceZoom();
		AtomicBoolean isScaled = new AtomicBoolean(false);
		Shell shell = new Shell(display);
		shell.addListener(SWT.Paint, event -> {
			if (isScaled.get()) {
				scaledGcNativeZoom.complete(event.gc.getGCData().nativeDeviceZoom);
			} else {
				gcNativeZoom.complete(event.gc.getGCData().nativeDeviceZoom);
			}
		});

		shell.open();
		assertEquals("GCData must have a zoom level equal to the actual zoom level of the widget/shell", DPIUtil.getNativeDeviceZoom(), (int) gcNativeZoom.join());

		int newSWTZoom = zoom * 2;
		Event swtEvent = new Event();
		swtEvent.type = SWT.ZoomChanged;
		swtEvent.widget = shell;
		swtEvent.detail = newSWTZoom;
		shell.notifyListeners(SWT.ZoomChanged, swtEvent);
		isScaled.set(true);
		shell.setVisible(false);
		shell.setVisible(true);

		assertEquals("GCData must have a zoom level equal to the actual zoom level of the widget/shell on zoomChanged event", newSWTZoom, (int) scaledGcNativeZoom.join());
	}

	@Test
	public void drawnElementsShouldScaleUpToTheRightZoomLevel() {
		int zoom = DPIUtil.getDeviceZoom();
		int scalingFactor = 2;
		Shell shell = new Shell(display);
		GC gc = GC.win32_new(shell, new GCData());
		gc.getGCData().nativeDeviceZoom = zoom * scalingFactor;
		gc.getGCData().lineWidth = 10;
		assertEquals("DPIUtil calls with getDeviceZoom should scale to the right value", gc.getGCData().lineWidth, gc.getLineWidth() * scalingFactor, 0);
	}

	void changeAutoScaleOnRuntime(boolean value) {
        try {
            Field autoScaleOnRuntimeField = DPIUtil.class.getDeclaredField("autoScaleOnRuntime");
            autoScaleOnRuntimeField.setAccessible(true);
            autoScaleOnRuntimeField.setBoolean(null, value);
            autoScaleOnRuntimeField.setAccessible(false);
            // dispose a probably existing font registry for the default display
            SWTFontProvider.disposeFontRegistry(display);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            fail("Value for DPI::autoScaleOnRuntime could not be changed");
        }
    }
}

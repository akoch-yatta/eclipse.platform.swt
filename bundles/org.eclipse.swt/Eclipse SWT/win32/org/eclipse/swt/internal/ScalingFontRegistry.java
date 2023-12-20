package org.eclipse.swt.internal;

import java.util.*;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.*;
import org.eclipse.swt.widgets.*;

public class ScalingFontRegistry implements SWTFontRegistry {
	private class ScaledFontContainer {
		private Font baseFont;
		private Map<Integer, Font> scaledFonts = new HashMap<>();

		ScaledFontContainer(Font baseFont) {
			this.baseFont = baseFont;
			System.out.println(
					"Create container with base font " + baseFont.getFontData()[0] + " -> " + baseFont.zoomLevel);
			scaledFonts.put(baseFont.zoomLevel, baseFont);
		}

		Font getScaledFont(int targetDeviceZoom) {
			return scaledFonts.get(targetDeviceZoom);
		}

		Font scaleFont(int targetDeviceZoom) {
			FontData[] fontData = baseFont.getFontData();
			Font scaledFont = Font.win32_new(baseFont.getDevice(), fontData[0], targetDeviceZoom);
			System.out.println("Create new font " + fontData[0] + " -> " + targetDeviceZoom);
			addScaledFont(targetDeviceZoom, scaledFont);
			return scaledFont;
		}

		void addScaledFont(int targetDeviceZoom, Font scaledFont) {
			scaledFonts.put(targetDeviceZoom, scaledFont);
		}
	}

	private static FontData KEY_SYSTEM_FONTS = new FontData();
	private Map<Long, ScaledFontContainer> fontHandleMap = new HashMap<>();
	private Map<FontData, ScaledFontContainer> fontKeyMap = new HashMap<>();

	@Override
	public Font getSystemFont(Shell shell) {
		Display display;
		int targetDeviceZoom;
		if (shell == null) {
			display = Display.getCurrent();
			targetDeviceZoom = display.getPrimaryMonitor().getZoom();
		} else {
			display = shell.getDisplay();
			targetDeviceZoom = shell.getCurrentDeviceZoom();
		}
		ScaledFontContainer container = getOrCreateBaseSystemFontContainer(display);

		Font systemFont = container.getScaledFont(targetDeviceZoom);
		if (systemFont != null) {
			return systemFont;
		}
		long systemFontHandle = createSystemFont(display, targetDeviceZoom);
		if (shell == null) {
			systemFont = Font.win32_new(display, systemFontHandle);
		} else {
			systemFont = Font.win32_new(shell, systemFontHandle);
		}
		container.addScaledFont(targetDeviceZoom, systemFont);
		return systemFont;
	}

	private ScaledFontContainer getOrCreateBaseSystemFontContainer(Display display) {
		ScaledFontContainer systemFontContainer = fontKeyMap.get(KEY_SYSTEM_FONTS);
		if (systemFontContainer == null) {
			int targetDeviceZoom = display.getPrimaryMonitor().getZoom();
			long systemFontHandle = createSystemFont(display, targetDeviceZoom);
			Font systemFont = Font.win32_new(display, systemFontHandle);
			systemFontContainer = new ScaledFontContainer(systemFont);
			fontHandleMap.put(systemFont.handle, systemFontContainer);
			fontKeyMap.put(KEY_SYSTEM_FONTS, systemFontContainer);
		}
		return systemFontContainer;
	}

	private long createSystemFont(Display display, int targetDeviceZoom) {
		long hFont = 0;
		NONCLIENTMETRICS info = new NONCLIENTMETRICS();
		info.cbSize = NONCLIENTMETRICS.sizeof;

		if (OS.SystemParametersInfoForDpi(OS.SPI_GETNONCLIENTMETRICS, NONCLIENTMETRICS.sizeof, info, 0,
				DPIUtil.mapZoomToDPI(targetDeviceZoom))) {
			LOGFONT logFont = info.lfMessageFont;
			System.out.println(String.format("Create system font with zoom %s: %s", targetDeviceZoom, new String(info.lfMessageFont.lfFaceName)));
			hFont = OS.CreateFontIndirect(logFont);
			// lfSystemFont = hFont != 0 ? logFont : null;
			// lfSystemFonts.put(dpiZoom, logFont);
		}

		if (hFont == 0)
			hFont = OS.GetStockObject(OS.DEFAULT_GUI_FONT);
		if (hFont == 0)
			hFont = OS.GetStockObject(OS.SYSTEM_FONT);
		return hFont;
	}

	@Override
	public Font getFont(FontData fontData, Shell shell) {
		if (shell == null || !DPIUtil.autoScaleOnRuntime) {
			return null;
		}
		ScaledFontContainer container;
		if (fontKeyMap.containsKey(fontData)) {
			container = fontKeyMap.get(fontData);
		} else {
			Font newFont = new Font(shell.getDisplay(), fontData);
			newFont.zoomLevel = shell.getCurrentDeviceZoom();
			container = new ScaledFontContainer(newFont);
			fontHandleMap.put(newFont.handle, container);
			fontKeyMap.put(fontData, container);
		}
		return getOrCreateFont(container, shell.getCurrentDeviceZoom());
	}

	@Override
	public void dispose() {

	}

	private Font getOrCreateFont(ScaledFontContainer container, int targetDeviceZoom) {
		Font scaledFont = container.getScaledFont(targetDeviceZoom);
		if (scaledFont == null) {
			scaledFont = container.scaleFont(targetDeviceZoom);
			fontHandleMap.put(scaledFont.handle, container);
			fontKeyMap.put(scaledFont.getFontData()[0], container);
		}
		return scaledFont;
	}
}

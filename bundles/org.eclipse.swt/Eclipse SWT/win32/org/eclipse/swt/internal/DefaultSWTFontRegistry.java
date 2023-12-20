package org.eclipse.swt.internal;

import java.util.*;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.*;
import org.eclipse.swt.widgets.*;

public class DefaultSWTFontRegistry implements SWTFontRegistry {
	private static String KEY_SYSTEM_FONTS = "SystemFont";
	private Map<String, Font> fontsMap = new HashMap<>();
	private Device device;

	public DefaultSWTFontRegistry(Device device) {
		this.device = device;
	}

	@Override
	public Font getSystemFont(Shell shell) {
		if (fontsMap.containsKey(KEY_SYSTEM_FONTS)) {
			return fontsMap.get(KEY_SYSTEM_FONTS);
		}

		long hFont = OS.GetStockObject(OS.SYSTEM_FONT);
		Device fontDevice;
		if (shell == null) {
			fontDevice = device;
		} else {
			fontDevice = shell.getDisplay();
		}
		Font font = Font.win32_new(fontDevice, hFont);
		registerFont(KEY_SYSTEM_FONTS, font, shell);
		return font;
	}

	@Override
	public Font getFont(FontData fontData, Shell shell) {
		String key = fontData.toString();
		if (fontsMap.containsKey(key)) {
			return fontsMap.get(key);
		}
		Font font = new Font(shell.getDisplay(), fontData);
		registerFont(key, font, shell);
		return font;
	}

	public Font registerFont(String key, Font font, Shell shell) {
		fontsMap.put(key, font);
		return font;
	}

	@Override
	public void dispose() {
		for (Font font : fontsMap.values()) {
			if (font != null) {
				font.dispose();
			}
		}
	}
}

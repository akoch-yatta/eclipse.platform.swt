package org.eclipse.swt.internal;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.*;
import org.eclipse.swt.widgets.*;

public class FontUtils {
	public static void printFontInfos(long handle) {
		long currentFontHandle = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
		if (currentFontHandle != 0) {
			LOGFONT logfont = new LOGFONT();
			OS.GetObject(currentFontHandle, LOGFONT.sizeof, logfont);
			System.out.println(String.format("Font: name %s height %s, handle %s", String.valueOf(logfont.lfFaceName), logfont.lfHeight, currentFontHandle));
		}
	}

	public static void resizeFont(long handle, int zoom) {
		long currentFontHandle = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
		if (currentFontHandle == 0) {
			//System.out.println("No new font neccessary handle for " + currentFontHandle);
		} else {
			Font newFont  = Display.getDefault().getSystemFont(zoom);
			long newFontHandle = newFont.handle;
			OS.SendMessage(handle, OS.WM_SETFONT, newFontHandle, 1);
		}
	}


	public static Font resizeFont(Font font, float scaleFactor) {
		/*	long currentFontHandle = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);

		LOGFONT logfont = new LOGFONT();
		OS.GetObject(currentFontHandle, LOGFONT.sizeof, logfont);
		int newHeight = Math.round(logfont.lfHeight / event.getScalingFactor());
		logfont.lfHeight = newHeight;
		long newFontHandle = OS.CreateFontIndirect(logfont);
		OS.SendMessage(handle, OS.WM_SETFONT, newFontHandle, 1);
*/
		Font oldFont = font;
		FontData[] fontData = oldFont.getFontData();
		for (int i = 0; i < fontData.length; ++i) {
			int currentHeight = fontData[i].getHeight();
			fontData[i].setHeight(Math.round(fontData[i].getHeight() * scaleFactor) );
	//		System.out.println("Font: set " + currentHeight + " -> " + fontData[i].getHeight());


		}
	//	System.out.println("Font: new " + font.getFontData()[0].getHeight());
		return new Font(font.getDevice(), fontData);
	}
}

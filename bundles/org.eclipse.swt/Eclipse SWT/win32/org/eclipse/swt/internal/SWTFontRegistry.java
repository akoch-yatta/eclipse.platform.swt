package org.eclipse.swt.internal;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public interface SWTFontRegistry {

	/**
	 * Returns a system font optimally suited for the specified shell.
	 *
     * @param shell the shell context used to determine the appropriate system font
     * @return the system font best suited for the specified shell
	 */
	Font getSystemFont(Shell shell);

	/**
     * Provides a font optimally suited for the specified shell. Fonts created in this manner
     * are managed by the font registry and should not be disposed of externally.
     *
     * @param fontData the data used to create the font
     * @param shell the shell context used to determine the appropriate font
     * @return the font best suited for the specified shell
     */
	Font getFont(FontData fontData, Shell shell);

	/**
     * Disposes of all fonts managed by the font registry.
	 */
	void dispose();
}

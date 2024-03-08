/*******************************************************************************
 * Copyright (c) 2000, 2024 Yatta Solutions and others.
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
package org.eclipse.swt.custom;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.*;
import org.eclipse.swt.widgets.*;

/**
 * This class is used in the win32 implementation only to support
 * adjusting widgets in the common package to DPI changes
 * <p>
 * <b>IMPORTANT:</b> This class is <em>not</em> part of the public
 * API for SWT. It is marked public only so that it can be shared
 * within the packages provided by SWT. It is not available on all
 * platforms, and should never be called from application code.
 * </p>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * @noreference This class is not intended to be referenced by clients
 */
public class CommonWidgetsDPIChangeHandlers {

	public static void registerCommonHandlers() {
		DPIZoomChangeRegistry.registerHandler(CommonWidgetsDPIChangeHandlers::handleCComboDPIChange, CCombo.class);
		DPIZoomChangeRegistry.registerHandler(CommonWidgetsDPIChangeHandlers::handleCTabFolderDPIChange, CTabFolder.class);
		DPIZoomChangeRegistry.registerHandler(CommonWidgetsDPIChangeHandlers::handleCTabItemDPIChange, CTabItem.class);
		DPIZoomChangeRegistry.registerHandler(CommonWidgetsDPIChangeHandlers::handleItemDPIChange, Item.class);
		DPIZoomChangeRegistry.registerHandler(CommonWidgetsDPIChangeHandlers::handleStyledTextDPIChange, StyledText.class);
	}

	private static void handleItemDPIChange(Widget widget, int newZoom, float scalingFactor) {
		if (!(widget instanceof Item)) {
			return;
		}
		Item item = (Item) widget;

		// Refresh the image
		Image image = item.getImage();
		if (image != null) {
			image.handleDPIChange(newZoom);
			item.setImage(image);
		}
	}


	private static void handleCComboDPIChange(Widget widget, int newZoom, float scalingFactor) {
		if (!(widget instanceof CCombo)) {
			return;
		}
		CCombo combo = (CCombo) widget;
		List list = combo.list;
		String [] items = list.getItems();
		list.dispose();
		combo.createPopup(items, -1);

		DPIZoomChangeRegistry.applyChange(combo.text, newZoom, scalingFactor);
		DPIZoomChangeRegistry.applyChange(combo.list, newZoom, scalingFactor);
		DPIZoomChangeRegistry.applyChange(combo.arrow, newZoom, scalingFactor);
	}

	private static void handleCTabFolderDPIChange(Widget widget, int newZoom, float scalingFactor) {
		if (!(widget instanceof CTabFolder)) {
			return;
		}
		CTabFolder cTabFolder = (CTabFolder) widget;

		for (CTabItem item : cTabFolder.getItems()) {
			DPIZoomChangeRegistry.applyChange(item, newZoom, scalingFactor);
		}
		cTabFolder.updateFolder(CTabFolder.UPDATE_TAB_HEIGHT | CTabFolder.REDRAW_TABS);
	}

	private static void handleCTabItemDPIChange(Widget widget, int newZoom, float scalingFactor) {
		if (!(widget instanceof CTabItem)) {
			return;
		}
		CTabItem item = (CTabItem) widget;
		Font itemFont = item.font;
		if (itemFont != null) {
			item.setFont(itemFont);
		}
		Image itemImage = item.getImage();
		if (itemImage != null) {
			itemImage.handleDPIChange(newZoom);

		}
		Image itemDisabledImage = item.getDisabledImage();
		if (itemDisabledImage != null) {
			itemDisabledImage.handleDPIChange(newZoom);
		}
	}

	private static void handleStyledTextDPIChange(Widget widget, int newZoom, float scalingFactor) {
		if (!(widget instanceof StyledText)) {
			return;
		}
		StyledText styledText = (StyledText) widget;

		DPIZoomChangeRegistry.applyChange(styledText.getCaret(), newZoom, scalingFactor);
		DPIZoomChangeRegistry.applyChange(styledText.defaultCaret, newZoom, scalingFactor);
		DPIZoomChangeRegistry.applyChange(styledText.ime, newZoom, scalingFactor);

		for (Caret caret : styledText.carets) {
			DPIZoomChangeRegistry.applyChange(caret, newZoom, scalingFactor);
		}

		styledText.updateCaretVisibility();

		styledText.renderer.setFont(styledText.getFont(), styledText.tabLength);
		styledText.setCaretLocations();
	}
}

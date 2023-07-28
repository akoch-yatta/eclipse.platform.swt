/*******************************************************************************
 * Copyright (c) 2008, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.examples.accessibility;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;

public class MinimalControlsWithLabelsExample {
	static Display display;
	static Shell shell;
	static Label label;
	static Button buttonPush;
	static Button buttonRadio;
	static Button buttonCheck;
	static Button buttonToggle;
	static Combo combo;
	static CCombo cCombo;
	static List list;
	static Spinner spinner;
	static Text textSingle;
	static Text textMulti;
	static StyledText styledText;
	static Table table;
	static Tree tree;
	static Tree treeTable;
	static ToolBar toolBar;
	static CoolBar coolBar;
	static Canvas canvas;
	static Group group;
	static TabFolder tabFolder;
	static CTabFolder cTabFolder;
	static CLabel cLabel;
	static Scale scale;
	static Slider slider;
	static ProgressBar progressBar;
	static Sash sash;

	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));
		shell.setText("All Controls Test");

		cTabFolder = new CTabFolder(shell, SWT.BORDER);
		for (int i = 0; i < 3; i++) {
			CTabItem item = new CTabItem(cTabFolder, SWT.NONE);
			item.setText("CTabItem &" + i);
			item.setToolTipText("CTabItem ToolTip" + i);
			Text itemText = new Text(cTabFolder, SWT.SINGLE | SWT.BORDER);
			itemText.setText("Text for CTabItem " + i);
			item.setControl(itemText);
		}
		cTabFolder.setSelection(cTabFolder.getItem(0));

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
}
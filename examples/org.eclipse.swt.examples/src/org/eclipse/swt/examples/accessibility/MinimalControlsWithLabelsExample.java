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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
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
import org.eclipse.swt.widgets.ToolItem;
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
	static ToolBar toolBar, overrideToolBar;
	static CoolBar coolBar, overrideCoolBar;
	static Canvas canvas;
	static Group group;
	static TabFolder tabFolder;
	static CTabFolder cTabFolder, cTabFolder2;
	static CLabel cLabel;
	static Scale scale;
	static Slider slider;
	static ProgressBar progressBar;
	static Sash sash;

	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display);
		shell.setLayout(new GridLayout(2, true));
		shell.setMinimumSize(200, 200);
		shell.setText("All Controls Test");


//		ToolBar toolBar, overrideToolBar;
		toolBar = new ToolBar(shell, SWT.FLAT);
		for (int i = 0; i < 3; i++) {
			ToolItem item = new ToolItem(toolBar, SWT.PUSH);
			item.setText("Item" + i);
			item.setToolTipText("ToolItem ToolTip" + i);
		}

		new Label(shell, SWT.NONE).setText("Label for CTabFolder");
		cTabFolder = new CTabFolder(shell, SWT.BORDER);
		cTabFolder.setFont(new Font(display, "Times New Roman", 12, SWT.NORMAL));
		for (int i = 0; i < 3; i++) {
			CTabItem item = new CTabItem(cTabFolder, SWT.NONE);
			item.setText("CTabItem &" + i);
			item.setToolTipText("CTabItem ToolTip" + i);
			Text itemText = new Text(cTabFolder, SWT.SINGLE | SWT.BORDER);
			itemText.setText("Text for CTabItem " + i);
			item.setControl(itemText);
		}
		cTabFolder.setSelection(cTabFolder.getItem(0));

/*
		overrideToolBar = new ToolBar(shell, SWT.FLAT);
		for (int i = 0; i < 3; i++) {
			ToolItem item = new ToolItem(overrideToolBar, SWT.PUSH);
			item.setText("Item" + i);
			item.setToolTipText("ToolItem ToolTip" + i);
		}
		overrideToolBar.setData("name", "ToolBar");
		overrideToolBar.setData("child", "ToolBar Item");

//		CoolBar coolBar, overrideCoolBar;
		coolBar = new CoolBar(shell, SWT.FLAT);
		for (int i = 0; i < 2; i++) {
			CoolItem coolItem = new CoolItem(coolBar, SWT.PUSH);
			ToolBar coolItemToolBar = new ToolBar(coolBar, SWT.FLAT);
			int toolItemWidth = 0;
			for (int j = 0; j < 2; j++) {
				ToolItem item = new ToolItem(coolItemToolBar, SWT.PUSH);
				item.setText("I" + i + j);
				item.setToolTipText("ToolItem ToolTip" + i + j);
				if (item.getWidth() > toolItemWidth) toolItemWidth = item.getWidth();
			}
			coolItem.setControl(coolItemToolBar);
			Point size = coolItemToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point coolSize = coolItem.computeSize (size.x, size.y);
			coolItem.setMinimumSize(toolItemWidth, coolSize.y);
			coolItem.setPreferredSize(coolSize);
			coolItem.setSize(coolSize);
		}

		overrideCoolBar = new CoolBar(shell, SWT.FLAT);
		for (int i = 0; i < 2; i++) {
			CoolItem coolItem = new CoolItem(overrideCoolBar, SWT.PUSH);
			ToolBar coolItemToolBar = new ToolBar(overrideCoolBar, SWT.FLAT);
			int toolItemWidth = 0;
			for (int j = 0; j < 2; j++) {
				ToolItem item = new ToolItem(coolItemToolBar, SWT.PUSH);
				item.setText("I" + i + j);
				item.setToolTipText("ToolItem ToolTip" + i + j);
				if (item.getWidth() > toolItemWidth) toolItemWidth = item.getWidth();
			}
			coolItem.setControl(coolItemToolBar);
			Point size = coolItemToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point coolSize = coolItem.computeSize (size.x, size.y);
			coolItem.setMinimumSize(toolItemWidth, coolSize.y);
			coolItem.setPreferredSize(coolSize);
			coolItem.setSize(coolSize);
		}
		overrideCoolBar.setData("name", "CoolBar");
		overrideCoolBar.setData("child", "CoolBar Item");
		*/
/*
		styledText = new StyledText(shell, SWT.SINGLE | SWT.BORDER);
		styledText.setText("Contents of single-line StyledText");
		*/

		Button button = new Button(shell, SWT.NONE);
		button.setText("Refresh");
		button.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				//ToolItem item = new ToolItem(toolBar, SWT.PUSH);
				//item.setText("Item " + e.time);
				toolBar.requestLayout();
				System.out.println("T " + toolBar.getItemCount());
			}

			@Override
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
}
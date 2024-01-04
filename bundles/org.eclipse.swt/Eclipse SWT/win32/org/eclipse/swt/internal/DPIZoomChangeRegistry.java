package org.eclipse.swt.internal;

import java.util.*;

import org.eclipse.swt.widgets.*;

public class DPIZoomChangeRegistry {
	private static Set<Class<? extends Widget>> blacklist = new HashSet<>();

	public static void unregisterForOnZoomChange(Class<? extends Widget> classToRegister) {
		blacklist.add(classToRegister);
	}

	public static boolean isDPIZoomChangeApplicable(Widget widget) {
		if (blacklist.contains(widget.getClass())) {
			System.out.println("Widget type " + widget.getClass() + " blacklisted");
			return false;
		}
		return true;
	}
}
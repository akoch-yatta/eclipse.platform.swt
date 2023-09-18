/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
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
 *     Daniel Kruegler - #420 - [High DPI] "swt.autoScale" should add new "half" option
 *******************************************************************************/
package org.eclipse.swt.internal;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * This class hold common constants and utility functions w.r.t. to SWT high DPI
 * functionality.
 * <p>
 * The {@code autoScaleUp(..)} methods convert from API coordinates (in
 * SWT points) to internal high DPI coordinates (in pixels) that interface with
 * native widgets.
 * </p>
 * <p>
 * The {@code autoScaleDown(..)} convert from high DPI pixels to API coordinates
 * (in SWT points).
 * </p>
 *
 * @since 3.105
 */
public class HighDPIUtil {

	private static enum AutoScaleMethod { AUTO, NEAREST, SMOOTH }
	private static AutoScaleMethod autoScaleMethod = AutoScaleMethod.NEAREST;
	private static boolean useCairoAutoScale = false;

/**
 * Auto-scale down ImageData
 */
public static ImageData autoScaleDown (Device device, final ImageData imageData, Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || imageData == null || (device != null && !device.isAutoScalable())) return imageData;
	float scaleFactor = 1.0f / getScalingFactor (deviceZoom);
	return autoScaleImageData(device, imageData, scaleFactor, monitor);
}

public static int[] autoScaleDown(int[] pointArray, Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || pointArray == null) return pointArray;
	float scaleFactor = getScalingFactor (deviceZoom);
	int [] returnArray = new int[pointArray.length];
	for (int i = 0; i < pointArray.length; i++) {
		returnArray [i] =  Math.round (pointArray [i] / scaleFactor);
	}
	return returnArray;
}

public static int[] autoScaleDown(Drawable drawable, int[] pointArray, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return pointArray;
	return autoScaleDown (pointArray, monitor);
}

/**
 * Auto-scale down float array dimensions.
 */
public static float[] autoScaleDown (float size[], Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || size == null) return size;
	float scaleFactor = getScalingFactor (deviceZoom);
	float scaledSize[] = new float[size.length];
	for (int i = 0; i < scaledSize.length; i++) {
		scaledSize[i] = size[i] / scaleFactor;
	}
	return scaledSize;
}

/**
 * Auto-scale down float array dimensions if enabled for Drawable class.
 */
public static float[] autoScaleDown (Drawable drawable, float size[], Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return size;
	return autoScaleDown (size, monitor);
}

/**
 * Auto-scale down int dimensions.
 */
public static int autoScaleDown (int size, Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || size == SWT.DEFAULT) return size;
	float scaleFactor = getScalingFactor (deviceZoom);
	return Math.round (size / scaleFactor);
}
/**
 * Auto-scale down int dimensions if enabled for Drawable class.
 */
public static int autoScaleDown (Drawable drawable, int size, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return size;
	return autoScaleDown (size, monitor);
}

/**
 * Auto-scale down float dimensions.
 */
public static float autoScaleDown (float size, Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || size == SWT.DEFAULT) return size;
	float scaleFactor = getScalingFactor (deviceZoom);
	return (size / scaleFactor);
}

/**
 * Auto-scale down float dimensions if enabled for Drawable class.
 */
public static float autoScaleDown (Drawable drawable, float size, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return size;
	return autoScaleDown (size, monitor);
}

/**
 * Returns a new scaled down Point.
 */
public static Point autoScaleDown (Point point, Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || point == null) return point;
	float scaleFactor = getScalingFactor (deviceZoom);
	Point scaledPoint = new Point (0,0);
	scaledPoint.x = Math.round (point.x / scaleFactor);
	scaledPoint.y = Math.round (point.y / scaleFactor);
	return scaledPoint;
}

/**
 * Returns a new scaled down Point if enabled for Drawable class.
 */
public static Point autoScaleDown (Drawable drawable, Point point, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return point;
	return autoScaleDown (point, monitor);
}

/**
 * Returns a new scaled down Rectangle.
 */
public static Rectangle autoScaleDown (Rectangle rect, Monitor monitor) {
	if (DPIUtil.deviceZoom == 100 || rect == null) return rect;
	Rectangle scaledRect = new Rectangle (0,0,0,0);
	Point scaledTopLeft = HighDPIUtil.autoScaleDown (new Point (rect.x, rect.y), monitor);
	Point scaledBottomRight = HighDPIUtil.autoScaleDown (new Point (rect.x + rect.width, rect.y + rect.height), monitor);

	scaledRect.x = scaledTopLeft.x;
	scaledRect.y = scaledTopLeft.y;
	scaledRect.width = scaledBottomRight.x - scaledTopLeft.x;
	scaledRect.height = scaledBottomRight.y - scaledTopLeft.y;
	return scaledRect;
}
/**
 * Returns a new scaled down Rectangle if enabled for Drawable class.
 */
public static Rectangle autoScaleDown (Drawable drawable, Rectangle rect, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return rect;
	return autoScaleDown (rect, monitor);
}

/**
 * Auto-scale image with ImageData
 */
public static ImageData autoScaleImageData (Device device, final ImageData imageData, int targetZoom, int currentZoom, Monitor monitor) {
	if (imageData == null || targetZoom == currentZoom || (device != null && !device.isAutoScalable())) return imageData;
	float scaleFactor = (float) targetZoom / (float) currentZoom;
	return autoScaleImageData(device, imageData, scaleFactor, monitor);
}

private static ImageData autoScaleImageData (Device device, final ImageData imageData, float scaleFactor, Monitor monitor) {
	// Guards are already implemented in callers: if (deviceZoom == 100 || imageData == null || scaleFactor == 1.0f) return imageData;
	int width = imageData.width;
	int height = imageData.height;
	int scaledWidth = Math.round ((float) width * scaleFactor);
	int scaledHeight = Math.round ((float) height * scaleFactor);
	switch (autoScaleMethod) {
	case SMOOTH:
		Image original = new Image (device, (ImageDataProvider) zoom -> imageData);

		/* Create a 24 bit image data with alpha channel */
		final ImageData resultData = new ImageData (scaledWidth, scaledHeight, 24, new PaletteData (0xFF, 0xFF00, 0xFF0000));
		resultData.alphaData = new byte [scaledWidth * scaledHeight];

		Image resultImage = new Image (device, (ImageDataProvider) zoom -> resultData);
		GC gc = new GC (resultImage);
		gc.setAntialias (SWT.ON);
		gc.drawImage (original, 0, 0, HighDPIUtil.autoScaleDown (width, monitor), HighDPIUtil.autoScaleDown (height, monitor),
				/* E.g. destWidth here is effectively DPIUtil.autoScaleDown (scaledWidth), but avoiding rounding errors.
				 * Nevertheless, we still have some rounding errors due to the point-based API GC#drawImage(..).
				 */
				0, 0, Math.round (HighDPIUtil.autoScaleDown ((float) width * scaleFactor, monitor)), Math.round (HighDPIUtil.autoScaleDown ((float) height * scaleFactor, monitor)));
		gc.dispose ();
		original.dispose ();
		ImageData result = resultImage.getImageData (DPIUtil.getDeviceZoom ());
		resultImage.dispose ();
		return result;
	case NEAREST:
	default:
		return imageData.scaledTo (scaledWidth, scaledHeight);
	}
}

/**
 * Returns a new rectangle as per the scaleFactor.
 */
public static Rectangle autoScaleBounds (Rectangle rect, int targetZoom, int currentZoom) {
	if ( rect == null || targetZoom == currentZoom) return rect;
	float scaleFactor = ((float)targetZoom) / (float)currentZoom;
	Rectangle returnRect = new Rectangle (0,0,0,0);
	returnRect.x = Math.round (rect.x * scaleFactor);
	returnRect.y = Math.round (rect.y * scaleFactor);
	returnRect.width = Math.round (rect.width * scaleFactor);
	returnRect.height = Math.round (rect.height * scaleFactor);
	return returnRect;
}

/**
 * Auto-scale up ImageData
 */
public static ImageData autoScaleUp (Device device, final ImageData imageData, Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || imageData == null || (device != null && !device.isAutoScalable())) return imageData;
	float scaleFactor = deviceZoom / 100f;
	return autoScaleImageData(device, imageData, scaleFactor, monitor);
}

public static int[] autoScaleUp(int[] pointArray, Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || pointArray == null) return pointArray;
	float scaleFactor = getScalingFactor (deviceZoom);
	int [] returnArray = new int[pointArray.length];
	for (int i = 0; i < pointArray.length; i++) {
		returnArray [i] =  Math.round (pointArray [i] * scaleFactor);
	}
	return returnArray;
}

public static int[] autoScaleUp(Drawable drawable, int[] pointArray, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return pointArray;
	return autoScaleUp (pointArray, monitor);
}

/**
 * Auto-scale up int dimensions.
 */
public static int autoScaleUp (int size, Monitor monitor) {
	int zoom = monitor.getZoom();
	if (zoom == 100 || size == SWT.DEFAULT) return size;
	float scaleFactor = getScalingFactor (zoom);
	return Math.round (size * scaleFactor);
}

/**
 * Auto-scale up int dimensions using Native DPI
 */
public static int autoScaleUpUsingNativeDPI (int size) {
	if (DPIUtil.nativeDeviceZoom == 100 || size == SWT.DEFAULT) return size;
	float nativeScaleFactor = DPIUtil.nativeDeviceZoom / 100f;
	return Math.round (size * nativeScaleFactor);
}

/**
 * Auto-scale up int dimensions if enabled for Drawable class.
 */
public static int autoScaleUp (Drawable drawable, int size, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return size;
	return autoScaleUp (size, monitor);
}

public static float autoScaleUp(float size) {
	if (DPIUtil.deviceZoom == 100 || size == SWT.DEFAULT) return size;
	float scaleFactor = getScalingFactor (DPIUtil.deviceZoom);
	return (size * scaleFactor);
}

public static float autoScaleUp(Drawable drawable, float size) {
	if (drawable != null && !drawable.isAutoScalable ()) return size;
	return autoScaleUp (size);
}

/**
 * Returns a new scaled up Point.
 */
public static Point autoScaleUp (Point point, Monitor monitor) {
	int deviceZoom = monitor.getZoom();
	if (deviceZoom == 100 || point == null) return point;
	float scaleFactor = getScalingFactor (deviceZoom);
	Point scaledPoint = new Point (0,0);
	scaledPoint.x = Math.round (point.x * scaleFactor);
	scaledPoint.y = Math.round (point.y * scaleFactor);
	return scaledPoint;
}

/**
 * Returns a new scaled up Point if enabled for Drawable class.
 */
public static Point autoScaleUp (Drawable drawable, Point point, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return point;
	return autoScaleUp (point, monitor);
}

/**
 * Returns a new scaled up Rectangle.
 */
public static Rectangle autoScaleUp (Rectangle rect, Monitor monitor) {
	if (DPIUtil.deviceZoom == 100 || rect == null) return rect;
	Rectangle scaledRect = new Rectangle (0,0,0,0);
	Point scaledTopLeft = HighDPIUtil.autoScaleUp (new Point (rect.x, rect.y), monitor);
	Point scaledBottomRight = HighDPIUtil.autoScaleUp (new Point (rect.x + rect.width, rect.y + rect.height), monitor);

	scaledRect.x = scaledTopLeft.x;
	scaledRect.y = scaledTopLeft.y;
	scaledRect.width = scaledBottomRight.x - scaledTopLeft.x;
	scaledRect.height = scaledBottomRight.y - scaledTopLeft.y;
	return scaledRect;
}

/**
 * Returns a new scaled up Rectangle if enabled for Drawable class.
 */
public static Rectangle autoScaleUp (Drawable drawable, Rectangle rect, Monitor monitor) {
	if (drawable != null && !drawable.isAutoScalable ()) return rect;
	return autoScaleUp (rect, monitor);
}

/**
 * Returns Scaling factor from the display
 * @return float scaling factor
 */
private static float getScalingFactor (int zoom) {
	if (useCairoAutoScale) {
		return 1;
	}
	return zoom / 100f;
}

/**
 * Gets Image data at specified zoom level, if image is missing then
 * fall-back to 100% image. If provider or fall-back image is not available,
 * throw error.
 */
public static ImageData validateAndGetImageDataAtZoom (ImageDataProvider provider, int zoom, boolean[] found) {
	if (provider == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	ImageData data = provider.getImageData (zoom);
	found [0] = (data != null);
	/* If image is null when (zoom != 100%), fall-back to image at 100% zoom */
	if (zoom != 100 && !found [0]) data = provider.getImageData (100);
	if (data == null) SWT.error (SWT.ERROR_INVALID_ARGUMENT, null, ": ImageDataProvider [" + provider + "] returns null ImageData at 100% zoom.");
	return data;
}

/**
 * Gets Image file path at specified zoom level, if image is missing then
 * fall-back to 100% image. If provider or fall-back image is not available,
 * throw error.
 */
public static String validateAndGetImagePathAtZoom (ImageFileNameProvider provider, int zoom, boolean[] found) {
	if (provider == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	String filename = provider.getImagePath (zoom);
	found [0] = (filename != null);
	/* If image is null when (zoom != 100%), fall-back to image at 100% zoom */
	if (zoom != 100 && !found [0]) filename = provider.getImagePath (100);
	if (filename == null) SWT.error (SWT.ERROR_INVALID_ARGUMENT, null, ": ImageFileNameProvider [" + provider + "] returns null filename at 100% zoom.");
	return filename;
}

public static void setUseCairoAutoScale (boolean cairoAutoScale) {
	useCairoAutoScale = cairoAutoScale;
}

public static boolean useCairoAutoScale() {
	return useCairoAutoScale;
}
}

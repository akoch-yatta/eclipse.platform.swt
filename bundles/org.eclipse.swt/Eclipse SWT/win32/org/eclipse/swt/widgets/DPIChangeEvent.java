package org.eclipse.swt.widgets;

/**
 * @since 3.125
 */
public record DPIChangeEvent(int oldZoom, int newZoom) {
	public float getScalingFactor() {
		return 1f * newZoom / oldZoom;
	}

	public boolean isDPIChange() {
		return oldZoom != newZoom;
	}

	@Override
	public String toString() {
		return "[oldZoom=" + oldZoom + ", newZoom=" + newZoom + "]";
	}
}

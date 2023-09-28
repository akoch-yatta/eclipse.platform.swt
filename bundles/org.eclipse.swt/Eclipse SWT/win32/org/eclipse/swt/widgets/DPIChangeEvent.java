package org.eclipse.swt.widgets;

/**
 * @since 3.125
 */
public class DPIChangeEvent {
	private int oldZoom;
	private int newZoom;

	public DPIChangeEvent(int oldZoom, int newZoom) {
		this.oldZoom = oldZoom;
		this.newZoom = newZoom;
	}

	public int newZoom() {
		return newZoom;
	}

	public int oldZoom() {
		return oldZoom;
	}

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

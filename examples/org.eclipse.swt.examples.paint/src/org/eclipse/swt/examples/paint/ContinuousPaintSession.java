package org.eclipse.swt.examples.paint;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved
 */

import org.eclipse.swt.events.*;import org.eclipse.swt.graphics.*;import org.eclipse.swt.widgets.*;

/**
 * The superclass for paint tools that draw continuously along the path
 * traced by the mouse's movement while the button is depressed
 */
public abstract class ContinuousPaintSession extends BasicPaintSession implements PaintRenderer {
	/**
	 * True if a click-drag is in progress.
	 */
	private boolean dragInProgress = false;
	
	/**
	 * A cached Point array for drawing.
	 */
	private Point[] cachedPointArray = new Point[] { new Point(-1, -1), new Point(-1, -1) };

	/**
	 * The time to wait between retriggers in milliseconds.
	 */
	private int retriggerInterval = 0;
	
	/**
	 * A unique identifier used to track RetriggerHandlers
	 */
	protected int retriggerId = 0;

	/**
	 * Constructs a ContinuousPaintSession.
	 * 
	 * @param paintSurface the drawing surface to use
	 */
	protected ContinuousPaintSession(PaintSurface paintSurface) {
		super(paintSurface);
	}

	/**
	 * Sets the retrigger timer.
	 * <p>
	 * After the timer elapses, if the mouse is still hovering over the same point with the
	 * drag button pressed, a new render order is issued and the timer is restarted.
	 * </p>
	 * @param interval the time in milliseconds to wait between retriggers, 0 to disable
	 */
	public void setRetriggerTimer(int interval) {
		retriggerInterval = interval;
	}

	/**
	 * Activates the tool.
	 */
	public void beginSession() {
		getPaintSurface().getPaintStatus().
			setMessage(PaintPlugin.getResourceString("session.ContinuousPaint.message"));
		dragInProgress = false;
	}
	
	/**
	 * Deactivates the tool.
     */
	public void endSession() {
		abortRetrigger();
	}
	
	/**
	 * Aborts the current operation.
	 */
	public void resetSession() {
		abortRetrigger();
	}

	/**
	 * Handles a mouseDown event.
	 * 
	 * @param event the mouse event detail information
	 */
	public final void mouseDown(MouseEvent event) {
		if (event.button != 1) return;
		if (dragInProgress) return; // spurious event
		dragInProgress = true;

		cachedPointArray[0].x = event.x;
		cachedPointArray[0].y = event.y;
		render(cachedPointArray, 1);
		prepareRetrigger();
	}

	/**
	 * Handles a mouseDoubleClick event.
	 * 
	 * @param event the mouse event detail information
	 */
	public final void mouseDoubleClick(MouseEvent event) {
	}

	/**
	 * Handles a mouseUp event.
	 * 
	 * @param event the mouse event detail information
	 */
	public final void mouseUp(MouseEvent event) {
		if (event.button != 1) return;
		if (! dragInProgress) return; // spurious event
		abortRetrigger();
		mouseSegmentFinished(event);
		dragInProgress = false;
	}
	
	/**
	 * Handles a mouseMove event.
	 * 
	 * @param event the mouse event detail information
	 */
	public final void mouseMove(MouseEvent event) {
		getPaintSurface().showCurrentPositionStatus();
		if (! dragInProgress) return;
		mouseSegmentFinished(event);
		prepareRetrigger();
	}
	
	/**
	 * Handle a rendering segment
	 * 
	 * @param event the mouse event detail information
	 */
	private final void mouseSegmentFinished(MouseEvent event) {
		if (cachedPointArray[0].x == -1) return; // spurious event
		if (cachedPointArray[0].x != event.x || cachedPointArray[0].y != event.y) {
			// draw new segment
			cachedPointArray[1].x = event.x;
			cachedPointArray[1].y = event.y;
			renderContinuousSegment();
		}
	}

	/**
	 * Draws a continuous segment from cachedPointArray[0] to cachedPointArray[1].
	 * Assumes cachedPointArray[0] has been drawn already.
	 * 
	 * @post cachedPointArray[0] will refer to the same point as cachedPointArray[1]
	 */
	protected void renderContinuousSegment() {
		/* A lazy but effective line drawing algorithm */
		final int dX = cachedPointArray[1].x - cachedPointArray[0].x;
		final int dY = cachedPointArray[1].y - cachedPointArray[0].y;
		int absdX = Math.abs(dX);
		int absdY = Math.abs(dY);

		if ((dX == 0) && (dY == 0)) return;
		
		if (absdY > absdX) {
			final int incfpX = (dX << 16) / absdY;
			final int incY = (dY > 0) ? 1 : -1;
			int fpX = cachedPointArray[0].x << 16; // X in fixedpoint format

			while (--absdY >= 0) {
				cachedPointArray[0].y += incY;
				cachedPointArray[0].x = (fpX += incfpX) >> 16;
				render(cachedPointArray, 1);
			}
			if (cachedPointArray[0].x == cachedPointArray[1].x) return;
			cachedPointArray[0].x = cachedPointArray[1].x;
		} else {
			final int incfpY = (dY << 16) / absdX;
			final int incX = (dX > 0) ? 1 : -1;
			int fpY = cachedPointArray[0].y << 16; // Y in fixedpoint format

			while (--absdX >= 0) {
				cachedPointArray[0].x += incX;
				cachedPointArray[0].y = (fpY += incfpY) >> 16;
				render(cachedPointArray, 1);
			}
			if (cachedPointArray[0].y == cachedPointArray[1].y) return;
			cachedPointArray[0].y = cachedPointArray[1].y;
		}
		render(cachedPointArray, 1);
	}		

	/**
	 * Prepare the retrigger timer
	 */
	private final void prepareRetrigger() {
		if (retriggerInterval > 0) {
			/*
			 * timerExec() provides a lightweight mechanism for running code at intervals from within
			 * the event loop when timing accuracy is not important.
			 *
			 * Since it is not possible to cancel a timerExec(), we tag the Runnable's with an
			 * identifier in order to distinguish the valid one from the stale ones.  In practice,
			 * if the interval is 1/100th of a second, then creating a few hundred new RetriggerHandlers
			 * each second will not cause a significant performance hit.
			 */
			Display display = getPaintSurface().getDisplay();
			display.timerExec(retriggerInterval, new RetriggerHandler(++retriggerId));
		}
	}

	/**
	 * Aborts the retrigger timer
	 */
	private final void abortRetrigger() {
		++retriggerId;
	}
	
	/**
	 * Handles possible retrigger events generated by timerExec().
	 */
	private class RetriggerHandler implements Runnable {
		int id;
		public RetriggerHandler(int id) {
			this.id = id;
		}
		public void run() {
			/*
			 * If the id's don't match, then we have cancelled the timed operation.
			 */
			if (retriggerId == id) {
				render(cachedPointArray, 1);
				prepareRetrigger();
			}
		}
	}
}

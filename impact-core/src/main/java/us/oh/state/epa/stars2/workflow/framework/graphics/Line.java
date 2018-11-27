package us.oh.state.epa.stars2.workflow.framework.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * <p>
 * Title: Line
 * </p>
 * 
 * <p>
 * Description: This is a simple line graphic. It goes from point "A" to point
 * "B".
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */
public class Line extends SelectableImage {
    public static final int SPACE = 7;
    private Point origDestination;
    private Point newDestination;

    /**
     * Constructor. Identifies the end points of the line and provides default
     * coloring for normal (black) and selected (red) states.
     * 
     * @param src
     *            One end point of the line.
     * @param dest
     *            The other end point of the line.
     */
    public Line(Point src, Point dest) {
        super(src);
        origDestination = dest;
        newDestination = null;
    }

    /**
     * Constructor. Identifies the end points of the line and specifies the
     * coloring for normal (non-selected) and selected states.
     * 
     * @param src
     *            One end point of the line.
     * @param dest
     *            The other end point of the line.
     * @param normal
     *            The color to use when not selected.
     * @param selected
     *            The color to use when selected.
     */
    public Line(Point src, Point dest, Color normal, Color selected) {
        super(src, normal, selected);
        origDestination = dest;
        newDestination = null;
    }

    /**
     * Returns the current "source" point. Note that this point may have moved
     * if the object was moved.
     * 
     * @return Point One end point of the line.
     */
    public final Point getSourcePoint() {
        return location;
    }

    /**
     * Returns the current "destination" point. Note that this point may have
     * moved if the object was moved.
     * 
     * @return Point The other end point of the line.
     */
    public final Point getDestinationPoint() {
        return origDestination;
    }

    /**
     * Sets the "destination" point to a new position. This is particularly
     * useful for "rubber banding" or re-drawing the line in response to a mouse
     * drag. In this situation, one end of the line is usually left alone. NOTE:
     * To change the "source" point, use the move methods on the
     * <code>SelectableImage</code> base class.
     * 
     * @param p
     *            New "destination end point.
     */
    public final void setDestinationPoint(Point p) {
        // Note that we are simply saving this point until we re-draw the
        // line.

        newDestination = p;
    }

    /**
     * Framework method. Draws the line in the graphics area.
     * 
     * @param g2d
     *            The object representing the graphics area.
     */
    protected final void drawImage(Graphics2D g2d) {
        // If the destination point was changed since the last time we drew
        // this line, erase the old line before drawing the new one.

        if (newDestination != null) {
            Point dest = newDestination;
            newDestination = null; // So we don't erase it again
            erase(g2d);
            origDestination = dest;
        }

        // Whatever happened before, this is now a simple "draw a line from
        // point A to point B" operation.

        int x1 = new Double(location.getX()).intValue();
        int y1 = new Double(location.getY()).intValue();

        int x2 = new Double(origDestination.getX()).intValue();
        int y2 = new Double(origDestination.getY()).intValue();

        drawLine(g2d, x1, y1, x2, y2);
    }

    /**
     * @param g2d
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public static void drawLine(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        if (x2 > (x1)) {
            g2d.drawLine(x1, y1, x2, y2);
        } else {
            int mid = (y2 - y1) / 2;
            g2d.drawLine(x1, y1, x1 + SPACE, y1);
            g2d.drawLine(x1 + SPACE, y1, x1 + SPACE, y1 + mid);
            g2d.drawLine(x1 + SPACE, y1 + mid, x2 - SPACE, y1 + mid);
            g2d.drawLine(x2 - (x2 - x1) / 2, y1 + mid, x2 - (x2 - x1) / 2 + 5,
                    y1 + mid + 3);
            g2d.drawLine(x2 - (x2 - x1) / 2, y1 + mid, x2 - (x2 - x1) / 2 + 5,
                    y1 + mid - 3);
            g2d.drawLine(x2 - SPACE, y1 + mid, x2 - SPACE, y2);
            g2d.drawLine(x2 - SPACE, y2, x2, y2);
        }
    }

    /**
     * Framework method. See if <code>Point</code> "p" lies with the area of
     * interest of this object. "P" is usually associated with a mouse
     * operation. The algorithm here is basically a check to see if "p" is
     * within three pixels of the line. If so, then "true" is returned.
     * Otherwise, "false" is returned.
     * 
     * @param p
     *            The Point of possible interest to this line.
     * 
     * @return boolean "true" if "p" is within three pixels of the line.
     */
    public final boolean intercepts(Point p) {
        // A bunch of convenience (??) local variables. Everything is done
        // in integer arithmetic.

        int x1 = new Double(location.getX()).intValue();
        int y1 = new Double(location.getY()).intValue();

        int x2 = new Double(origDestination.getX()).intValue();
        int y2 = new Double(origDestination.getY()).intValue();

        int xMin = Math.min(x1, x2);
        int xMax = Math.max(x1, x2);

        int yMin = Math.min(y1, y2);
        int yMax = Math.max(y1, y2);

        int px = new Double(p.getX()).intValue();
        int py = new Double(p.getY()).intValue();

        boolean intercepted = false;

        // We are going to use an intercept formula to see how close "p" is
        // to the "nominal" line representing the Connection (if the
        // "drawLine()" bent the line into some odd shape, the this won't
        // work all that well).
        //
        // If our line is essentially horizontal, then use a different
        // formula for calculating the distance.

        int deltay = Math.abs(y1 - y2);

        if (deltay < 3) {
            int avgY = (y1 + y2) / 2;
            int yDist = Math.abs(avgY - py);

            if ((yDist < 4) && (px >= xMin) && (px <= xMax)) {
                intercepted = true;
            }

            return intercepted;
        }

        // If our line is essentially vertical, then do something similar,
        // but "perpendicular", to what we did for the horizontal line.

        int deltax = Math.abs(x1 - x2);

        if (deltax < 3) {
            int avgX = (x1 + x2) / 2;
            int xDist = Math.abs(avgX - px);

            if ((xDist < 4) && (py >= yMin) && (py <= yMax)) {
                intercepted = true;
            }

            return intercepted;
        }

        // If we get here, our line has a definite slope. We will use a
        // point-intercept formula to figure out the distance between "p"
        // and our connection line. REMINDER: The coordinate system for
        // graphics is not the same as for normal mathematics, i.e., (0, 0)
        // is the upper left corner in graphical coordinates. This inverts
        // the sign on "Y" calculations.

        double dx1 = location.getX();
        double dy1 = location.getY();

        double dx2 = origDestination.getX();
        double dy2 = origDestination.getY();

        double slope = (dy1 - dy2) / (dx1 - dx2);
        double invSlope = -1.0 / slope;

        double b1 = dy1 - (dx1 * slope);
        double b2 = py - (px * invSlope);

        double xx = (b2 - b1) / (slope - invSlope);
        double yy = (slope * xx) + b1;

        double dd = ((xx - px) * (xx - px)) + ((yy - py) * (yy - py));
        double dist = Math.sqrt(dd);

        if ((dist < 4) && (px >= xMin) && (px <= xMax) && (py >= yMin)
                && (py <= yMax)) {
            intercepted = true;
        }

        return intercepted;
    }
}

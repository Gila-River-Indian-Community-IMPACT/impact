package us.oh.state.epa.stars2.workflow.framework.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * <p>
 * Title: OSSM
 * </p>
 * 
 * <p>
 * Description:
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
public class Connection extends SelectableImage {
    protected Connectable source;
    protected Connectable dest;

    public Connection(Connectable source, Connectable dest) {
        this(source, dest, Color.BLACK, Color.RED);
    }

    public Connection(Connectable source, Connectable dest, Color normal,
            Color selected) {
        super(source.getOutputPoint(), normal, selected);
        connect(source, dest);
    }

    /**
     * @param inSource
     * @param inDest
     */
    public final void connect(Connectable inSource, Connectable inDest) {
        this.source = inSource;
        this.source.addOutputConnection(this);

        this.dest = inDest;
        this.dest.addInputConnection(this);
    }

    /**
     * @see SelectableImage#move(Point)
     */
    public final void move(Point inDest) {
    }

    /**
     * @see SelectableImage#intercepts(Point)
     */
    public final boolean intercepts(Point p) {
        int x1 = new Double(source.getOutputPoint().getX()).intValue();
        int y1 = new Double(source.getOutputPoint().getY()).intValue();

        int x2 = new Double(dest.getInputPoint().getX()).intValue();
        int y2 = new Double(dest.getInputPoint().getY()).intValue();

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

        if (x2 < x1) {
            int mid = (y2 - y1) / 2;
            int space = Line.SPACE;
            if (py <= y1 + mid - 4) {
                int xDist = Math.abs(x1 + space - px);

                if ((xDist < 4) && (py <= y1 + mid) && (py >= y1)) {
                    intercepted = true;
                }
                return intercepted;
            }
            if (py >= y1 + mid + 4) {
                int xDist = Math.abs(x2 - space - px);

                if ((xDist < 4) && (py >= y1 + mid) && (py <= y2)) {
                    intercepted = true;
                }
                return intercepted;
            }
            int yDist = Math.abs(y1 + mid - py);

            if ((yDist < 4) && (px <= x1 + space) && (px >= x2 - space)) {
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

        double dx1 = source.getOutputPoint().getX();
        double dy1 = source.getOutputPoint().getY();

        double dx2 = dest.getInputPoint().getX();
        double dy2 = dest.getInputPoint().getY();

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

    /**
     * @see SelectableImage#drawImage(Graphics2D)
     */
    protected final void drawImage(Graphics2D g2d) {
        int x1 = new Double(source.getOutputPoint().getX()).intValue();
        int y1 = new Double(source.getOutputPoint().getY()).intValue();

        int x2 = new Double(dest.getInputPoint().getX()).intValue();
        int y2 = new Double(dest.getInputPoint().getY()).intValue();

        Line.drawLine(g2d, x1, y1, x2, y2);
    }
}

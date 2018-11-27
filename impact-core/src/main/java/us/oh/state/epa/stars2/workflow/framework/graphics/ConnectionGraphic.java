package us.oh.state.epa.stars2.workflow.framework.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * <p>
 * Title: CoonectionGraphic
 * </p>
 * 
 * <p>
 * Description: This is the graphic object that represents a connection point.
 * Currently, this is a little circle that the user can select or start a
 * mouse-drag in to create a Connection.
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

public class ConnectionGraphic extends SelectableImage {
    /**
     * This is the radius of the connection circle, in pixels.
     */
    public static final int RADIUS = 3;

    /**
     * Identifies a graphic as an input connection.
     */
    public static final int INPUT_CONNECTION = 1;

    /**
     * Identifies a graphic as an input connection.
     */
    public static final int OUTPUT_CONNECTION = 2;
    private Connectable connectable;
    private int connType;

    /**
     * Constructor. A <code>ConnectionGraphic</code> is always with the input
     * or output connection point on a <code>Connectable</code>. Default
     * normal (dark grey) and selected (red) coloring is provided.
     * 
     * @param connectable
     *            The Connectable this object is associated with.
     * @param connType
     *            An input/output connection point.
     */
    public ConnectionGraphic(Connectable connectable, int connType) {
        this(connectable, connType, Color.DARK_GRAY, Color.RED);
    }

    /**
     * Constructor. Same as previous constructor, except the normal and selected
     * colors are specified.
     * 
     * @param connectable
     *            The Connectable this object is associated with.
     * @param connType
     *            An input/output connection point.
     * @param normal
     *            The Color used for the non-selected state.
     * @param selected
     *            The Color used for the selected state.
     */
    public ConnectionGraphic(Connectable connectable, int connType,
            Color normal, Color selected) {
        super(new Point(0, 0), normal, selected);

        this.connectable = connectable;
        this.connType = connType;

        // Associate our location with either the Connectable's input
        // connection point or its output connection point.

        location = connectable.getInputPoint();

        if (connType == OUTPUT_CONNECTION) {
            location = connectable.getOutputPoint();
        }
    }

    /**
     * Returns the Connectable object associated with this graphic.
     * 
     * @return the Connectable.
     */
    public final Connectable getConnectable() {
        return connectable;
    }

    /**
     * Returns the Connection Type for this graphic.
     * 
     * @return
     */
    public final int getConnectionType() {
        return connType;
    }

    /**
     * Returns the center point of the connection graphic.
     * 
     * @return Point The center of the connection graphic.
     */
    public final Point getCenterPoint() {
        // Rather than use our location, get the center point from the
        // the Connectable we are associated with.

        Point p = connectable.getInputPoint();

        if (connType == OUTPUT_CONNECTION) {
            p = connectable.getOutputPoint();
        }

        return p;
    }

    /**
     * Framework method. Returns "true" if Point "p" lies within the area of
     * interest of this object. In this case, returns "true" if "p" is within
     * the radius of the center point. Point "p" is typically associated with a
     * mouse operation.
     * 
     * @param p
     *            The point being checked to see if it is of interest to this
     *            object.
     * 
     * @return boolean "true" if within the radius of the center point,
     *         otherwise "false".
     */
    public final boolean intercepts(Point p) {
        // This is just a simple application of the old "compute the
        // distance between point A and point B" formula.

        double px = p.getX();
        double py = p.getY();

        Point center = getCenterPoint();

        double lx = center.getX();
        double ly = center.getY();

        double deltax = px - lx;
        double deltay = py - ly;

        // Distance = Square Root ((x1 - x2)**2 + (y1 - y2)**2) ;

        double dd = (deltax * deltax) + (deltay * deltay);
        double dist = Math.sqrt(dd);
        double dradius = ConnectionGraphic.RADIUS + 1;

        return (dist <= dradius);
    }

    /**
     * Framework method. This is how we draw ourself.
     * 
     * @param g2d
     *            The object used to do the drawing.
     */
    protected final void drawImage(Graphics2D g2d) {
        Point center = getCenterPoint();

        int x = new Double(center.getX()).intValue();
        int y = new Double(center.getY()).intValue();

        int upperLeftX = x - ConnectionGraphic.RADIUS;
        int upperLeftY = y - ConnectionGraphic.RADIUS;
        int diameter = ConnectionGraphic.RADIUS * 2;

        g2d.drawOval(upperLeftX, upperLeftY, diameter, diameter);
    }
}

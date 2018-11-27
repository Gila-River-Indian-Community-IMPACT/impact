package us.oh.state.epa.stars2.workflow.framework.graphics;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

/**
 * <p>
 * Title: Connectable
 * </p>
 * 
 * <p>
 * Description: Abstract class that defines a "connectable" graphic object.
 * Connectable objects can be connected to other connectable objects via
 * Connection objects. If the Connectable moves, it automatically moves all of
 * its Connections.
 * </p>
 * 
 * <p>
 * The (x, y) coordinates of all <code>Point</code> objects are assumed to be
 * in pixels.
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
abstract public class Connectable extends SelectableImage {
    private ArrayList<Connection> inputs; // Input connections
    private ArrayList<Connection> outputs; // Output connections

    /**
     * Constructor. The "location" defines the location of the Connectable
     * graphic. Locations typically refer to the upper left corner of the
     * graphic. The (x, y) coordinates are assumed to be pixels. This
     * constructor uses the default "normal" (black) and "selected" (red)
     * colors.
     * 
     * @param location
     *            Location of this graphic in the image.
     */
    protected Connectable(Point location) {
        this(location, Color.BLACK, Color.RED);
    }

    /**
     * Constructor. The "location" defines the location of the Connectable
     * graphic. Locations typically refer to the upper left corner of the
     * graphic. The (x, y) coordinates are assumed to be pixels. This
     * constructor allows the "normal" and "selected" colors to be defined.
     * 
     * @param location
     *            Location of this graphic in the image.
     * @param normal
     *            The color to draw the Connectable in when not selected.
     * @param selected
     *            The color to draw the Connectable in when selected.
     */
    protected Connectable(Point location, Color normal, Color selected) {
        super(location, normal, selected);

        inputs = new ArrayList<Connection>();
        outputs = new ArrayList<Connection>();
    }

    /**
     * Defines the input connection point for this Connectable. Typically, the
     * output of a Connectable is connected to the input of another Connectable.
     * 
     * @return Input connection point.
     */
    abstract public Point getInputPoint();

    /**
     * Defines the output connection point for this Connectable. Typically, the
     * output of a Connectable is connected to the input of another Connectable.
     * 
     * @return Output connection point.
     */
    abstract public Point getOutputPoint();

    /**
     * Adds a Connection to the input connection point for this object. Multiple
     * input connections are permitted.
     * 
     * @param c
     *            Connection to add to the input connection point.
     */
    public final void addInputConnection(Connection c) {
        inputs.add(c);
    }

    /**
     * Removes a Connection from the input connection point for this object.
     * 
     * @param c
     *            Connection to remove from the input connection point.
     */
    public final void removeInputConnection(Connection c) {
        inputs.remove(c);
    }

    /**
     * Adds a Connection to the output connection point for this object.
     * Multiple output connections are permitted.
     * 
     * @param c
     *            Connection to add to the output connection point.
     */
    public final void addOutputConnection(Connection c) {
        outputs.add(c);
    }

    /**
     * Removes a Connection from the output connection point for this object.
     * 
     * @param c
     *            Connection to remove from the output connection point.
     */
    public final void removeOutputConnection(Connection c) {
        outputs.remove(c);
    }
}

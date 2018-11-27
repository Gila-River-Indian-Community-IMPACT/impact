package us.oh.state.epa.stars2.workflow.framework.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Action;
import javax.swing.JPopupMenu;

/**
 * <p>
 * Title: SelectableImage
 * </p>
 * 
 * <p>
 * Description: This is the base class for graphical objects in this paradigm.
 * To a large degree, this class serves as a "template" that defines how
 * graphical objects interact with their environment.
 * </p>
 * 
 * <p>
 * The <code>SelectableImage</code> is intended to be the graphical
 * representation of an application object. The key concept behind being
 * selectable is that a selected object is drawn in a different color than a
 * non-selected (or "normal") object. Usually this is associated with a mouse
 * event or other selection event, and may involve a state change in the
 * associated application object.
 * </p>
 * 
 * <p>
 * There are two key methods that derived classes must implement: "intercepts()"
 * and "drawImage()". These are discussed in more detail later in this file. By
 * implementing these two methods, this class can provide default
 * implementations for select color highlighting, moving an object to a new
 * location (erase the object at the old location, move location, redraw object
 * at new location). Some derived classes may need to overload the "move()"
 * method or "moveDraw()" method if the default behavior is not correcct.
 * </p>
 * 
 * <p>
 * Unless otherwise stated, the "x" and "y" values of Point refer to pixels.
 * Point (0, 0) is the upper left corner of the drawing area.
 * </p>
 * 
 * <p>
 * The <code>Graphic2D</code> object represents the drawing region. It is
 * usually associated with a <code>JPanel</code>. Most derived classes will
 * call methods on the Graphics2D object to draw themselves in this region.
 * </p>
 * 
 * <p>
 * Methods marked "Template method" are used by this object to perform specific
 * operations. These will usually call "Framework methods" provided by the
 * derived class, e.g., "drawImage()". Template methods should rarely need to be
 * overloaded by the derived class. Framework methods are either abstract or
 * likely to be overloaded by the derived class.
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

abstract public class SelectableImage {
    protected Color fgColor; // Normal drawing color
    protected Color selColor; // Selected drawing color
    protected boolean selected; // "Selected" state flag
    protected boolean moving; // "Moving" state flag
    protected Point location; // Current location of the object
    protected Point newLocation; // Next location of object when moving
    protected Action[] popupActions; // Actions to be displayed in popup

    /**
     * Constructor. The "location" can be object-specific. Polygons usually
     * interpret "location" at the upper left corner of the object. Circles
     * interpret "location" as the center of the circle. This constructor
     * assigns black as the normal (non-selected) color and red as the selected
     * color.
     * 
     * @param location
     *            Location within the graphics region where this object will be
     *            drawn.
     */
    protected SelectableImage(Point location) {
        this(location, Color.BLACK, Color.RED);
    }

    /**
     * Constructor. Like the previous constructor, except this one allows the
     * application to choose the colors representing "normal" and "selected"
     * states.
     * 
     * @param location
     *            Location within the graphics region where this object will be
     *            drawn.
     * @param normal
     *            The color for the non-selected object.
     * @param selected
     *            The color for the selected object.
     */
    protected SelectableImage(Point location, Color normal, Color selected) {
        this.location = location;
        fgColor = normal;
        selColor = selected;
        this.selected = false;
        popupActions = null;
    }

    /**
     * Framework method. This method allows a derived class to determine whether
     * or not a Point "p" lies within the area of interest of the current
     * graphic. Usually, the Point "p" corresponds to the location of a mouse
     * event, e.g., a mouse click, and this method is being called to determine
     * if the current SelectableImage is "interested" in the event. For example,
     * if the current image is a rectangle or a circle, does this point lies
     * within the border of the rectangle or circle. If so, then the derived
     * object "intercepts" the mouse event and the graphical area knows that it
     * need look no further.
     * 
     * @param p
     *            The Point of possible interest to the derived class object.
     * 
     * @return "true" if the derived class object is interested in this event,
     *         otherwise "false".
     */
    abstract public boolean intercepts(Point p);

    /**
     * Framework method. Allows the derived class to specify how to draw its
     * image. The derived class should not alter any of the characteristics of
     * "g2d", such as the foreground color. The foreground color will be set by
     * the framework so that the same method can be used to erase the object,
     * draw the object in the normal color, and draw the object in the
     * "selected" color. Most derived classes will call drawing methods on
     * "g2d".
     * 
     * @param g2d
     *            Object that corresponds to the graphical drawing area.
     */
    abstract protected void drawImage(Graphics2D g2d);

    /**
     * Allows the application to associate a set of popup actions with this
     * particular image. If "actions" is null or empty, no popup menu will be
     * display if the popup trigger occurs inside this object.
     * 
     * @param actions
     *            Array of popup actions.
     */
    public final void addPopupActions(Action[] actions) {
        popupActions = actions;
    }

    public final JPopupMenu createPopupMenu() {
        if ((popupActions == null) || (popupActions.length == 0)) {
            return null;
        }

        JPopupMenu menu = new JPopupMenu();

        int i;
        for (i = 0; i < popupActions.length; i++) {
            menu.add(popupActions[i]);
        }

        return menu;
    }

    public final Action getDefaultAction() {
        if ((popupActions == null) || (popupActions.length == 0)) {
            return null;
        }

        return popupActions[0];
    }

    /**
     * Framework method. The intent of the method is to support more
     * sophisticated repainting of objects. The "clip" rectangle represents an
     * area that is about to be repainted. If any of the "clipping" rectangle
     * overloads this object, then this object will need to be repainted and
     * should return "true" to caller. If there is no overlap, then "false"
     * should be returned. The default implementation always returns "true".
     * 
     * @param clip
     *            Area being repainted.
     * 
     * @return boolean "true" if the clipping rectangle intersects this image.
     */
    public final boolean intersects(Rectangle clip) {
        return true;
    }

    /**
     * Returns the current "x" location of the object. This value is originally
     * set in the constructor, but may have been moved at some later time.
     * 
     * @return int Current "x" location.
     */
    public final int getX() {
        int foo = new Double(location.getX()).intValue();
        return foo;
    }

    /**
     * Returns the current "y" location of the object. This value is originally
     * set in the constructor, but may have been moved at some later time.
     * 
     * @return int Current "y" location.
     */
    public final int getY() {
        int foo = new Double(location.getY()).intValue();
        return foo;
    }

    /**
     * Sets the internal state of this object to indicate that a move is in
     * progress. Later, when the object is being redrawn, if a move is in
     * progress the object will be erased at its old location and redrawn at its
     * new location. Note that this method does not force a repaint(), it simply
     * saves move information until the next paint event occurs.
     * 
     * @param dest
     *            The new location for this object.
     */
    public void move(Point dest) {
        moving = true;
        newLocation = dest;
    }

    /**
     * Sets the current "normal" (non-selected) color.
     * 
     * @param c
     *            Current normal color.
     */
    public final void setNormalColor(Color c) {
        fgColor = c;
    }

    /**
     * Gets the current "normal" (non-selected) color.
     * 
     * @return Color The current normal color.
     */
    public final Color getNormalColor() {
        return fgColor;
    }

    /**
     * Sets the current "selected" color.
     * 
     * @param c
     *            Current selected color.
     */
    public final void setSelectedColor(Color c) {
        selColor = c;
    }

    /**
     * Gets the current "selected" color.
     * 
     * @return Color The current selected color.
     */
    public final Color getSelectedColor() {
        return selColor;
    }

    /**
     * Template method. Examines the current state of the object and determines
     * the appropriate drawing strategy for that state. For example, if the
     * object is marked "selected", uses the "selected" color for drawing. If
     * the object is moving, erases the object at its old location and draws it
     * at its new location.
     * 
     * @param g2d
     *            The object representing the graphical region where the object
     *            should be drawn.
     */
    public final void draw(Graphics2D g2d) {
        // Start with the assumption that we are using normal coloring.

        Color c = fgColor;

        // If this object has been "selected", use the selection color.

        if (selected) {
            c = selColor;
        }

        // If this object is being moved, call "moveDraw()" to handle it.
        // Otherwise, just draw the object in its current location.

        if (moving) {
            moveDraw(g2d, c);
        } else {
            draw(g2d, c);
        }
    }

    /**
     * Template method. Arranges to draw the derived class object in the
     * graphics region using color "c". Ultimately, this method is going to call
     * the derived class's "drawImage()" method.
     * 
     * @param g2d
     *            Object representing the graphical region.
     * @param c
     *            The color to drawt this object in.
     */
    protected final void draw(Graphics2D g2d, Color c) {
        Color origColor = g2d.getColor();
        g2d.setColor(c);
        drawImage(g2d);
        g2d.setColor(origColor);
    }

    /**
     * Template method. Arranges to draw the derived class object in the
     * graphics region using color "c". Ultimately, this method is going to call
     * the derived class's "drawImage()" method.
     * 
     * @param g2d
     *            Object representing the graphical region.
     * @param c
     *            The color to drawt this object in.
     */
    protected final void moveDraw(Graphics2D g2d, Color c) {
        erase(g2d);
        location = newLocation;
        newLocation = null;
        moving = false;
        draw(g2d, c);
    }

    /**
     * Template method. Arranges to "erase" the derived class object in the
     * graphics region. The "erase" operation is accomplished by setting the
     * drawing color (foreground color) to the background color and then calling
     * the derived class's "drawImage()" method.
     * 
     * @param g2d
     *            Object representing the graphical region.
     */
    public final void erase(Graphics2D g2d) {
        Color origColor = g2d.getColor();
        Color bgColor = g2d.getBackground();
        g2d.setColor(bgColor);
        drawImage(g2d);
        g2d.setColor(origColor);
    }

    /**
     * Marks this object as "selected". On its next drawing opportunity, the
     * object will be redrawn in its "selected" color.
     */
    public final void select() {
        selected = true;
    }

    /**
     * Marks this object as "unselected". On its next drawing opportunity, the
     * object will be redrawn in its "normal" color.
     */
    public final void unselect() {
        selected = false;
    }

    /**
     * Returns "true" if this object is currently selected, otherwise returns
     * "false".
     * 
     * @return boolean "True" if this object is marked selected.
     */
    public final boolean isSelected() {
        return selected;
    }
}

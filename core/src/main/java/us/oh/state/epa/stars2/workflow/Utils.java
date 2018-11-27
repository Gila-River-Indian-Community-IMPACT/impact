package us.oh.state.epa.stars2.workflow;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;

/**
 * <p>
 * Title: Utils
 * </p>
 * 
 * <p>
 * Description: This is a collection of stuff that needs to be shared across
 * different components of the workflow designer.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author
 * @version 1.0
 */

public class Utils {
    /**
     * Identifies a key used by this with AbstractActions registered via the
     * "addActionButton()" method. The value of "requiresSelection" is stored in
     * the associated Action under this tag.
     */
    public static final String REQ_SELECTION = "requiresSelection";
    // The size of the ActivityTemplateDef selector list. This is on the
    // "Activities" tab in the "Available Components" area.
    public static int ATD_PREF_WIDTH = 200;
    public static int ATD_PREF_HEIGHT = 100;
    // The size of the Process Designer area. This is the large area where
    // the graphical process is edited.
    public static int PROC_DGN_WIDTH = 950;
    public static int PROC_DGN_HEIGHT = 600;
    // The size of the selection list in the ProcessTemplateControl (the
    // "Workflows" tab in the "Available Components" area. Note that most
    // selection lists use this as their preferred size.
    public static final int PTC_PREF_WIDTH = 200;
    public static final int PTC_PREF_HEIGHT = 120;
    // The starting location for an Activity that is being inserted into
    // a workflow. These coordinates are "normalized", i.e., scaled from
    // "0" to "1000". The process design panel will adjust the location to
    // fit its allocated area.
    public static int ACT_START_X = 50;
    public static int ACT_START_Y = 900;
    // These are the state colors used by the Activity legend.
    public static final Color C_INPROCESS = new Color(0xff6666);
    public static final Color C_READY = new Color(0x88ffff);
    public static final Color C_COMPLETED = new Color(0x66ff66);
    public static final Color C_BYPASSED = new Color(0xcccccc);
    public static final Color C_BLOCKED = new Color(0xffff66);
    public static final Color C_NOT_DONE = new Color(0xffffff);
    public static final Color C_AUTO_RETRY = new Color(0xff6633);
    // These are the state colors used by the SLA legend.
    public static final Color C_ONTIME = Color.GREEN;
    public static final Color C_JEOPARDY = new Color(0xffff66);
    public static final Color C_SLA_EXCEEDED = new Color(0xff6666);
    // These are the recognized, "universal" state for event handlers, e.g.,
    // "ActivityEvent", "TransitionEvent", etc. These states represent
    // common operations performed on these objects.
    public static final int SELECTED = 1;
    public static final int UNSELECTED = 2;
    public static final int INSERTED = 3;
    public static final int MODIFIED = 4;
    public static final int DELETED = 5;
    // I think I have the "sense" of these methods backwards, i.e.,
    // "normalize" is really "un-normalize" and vice versa.

    /**
     * Un-normalizes a width value from [0 .. 1000] to [0 .. width of editor] in
     * pixels.
     * 
     * @param w
     *            Normalized width value.
     * 
     * @return Un-normalized width value (pixels).
     */
    public static int unNormalizeWidth(int w) {
        int sw = (w * PROC_DGN_WIDTH) / 1000;
        return sw;
    }

    /**
     * Un-normalizes a height value from [0 .. 1000] to [0 .. height of editor]
     * in pixels.
     * 
     * @param h
     *            Normalized height value.
     * 
     * @return Un-normalized height value (pixels).
     */
    public static int unNormalizeHeight(int h) {
        int sh = (h * PROC_DGN_HEIGHT) / 1000;
        return sh;
    }

    /**
     * Normalizes a width value from [0 .. width of editor] to [0 .. 1000].
     * 
     * @param sw
     *            Un-normalized width value (pixels).
     * 
     * @return Normalized width value.
     */
    public static int normalizeWidth(int sw) {
        int w = (sw * 1000) / PROC_DGN_WIDTH;
        return w;
    }

    /**
     * Normalizes a height value from [0 .. height of editor] to [0 .. 1000].
     * 
     * @param sh
     *            Un-normalized height value (pixels).
     * 
     * @return Normalized height value.
     */
    public static int normalizeHeight(int sh) {
        int h = (sh * 1000) / PROC_DGN_HEIGHT;
        return h;
    }

    /**
     * Un-normalizes a Point "p" from a [0 .. 1000] coordinate value to actual
     * screen width and height.
     * 
     * @param p
     *            The point to be un-normalized.
     * 
     * @return The coordinates of "p" in screen pixels.
     */
    public static Point unNormalize(Point p) {
        int x = new Double(p.getX()).intValue();
        int y = new Double(p.getY()).intValue();

        int xx = unNormalizeWidth(x);
        int yy = unNormalizeHeight(y);

        Point pp = new Point(xx, yy);
        return pp;
    }

    /**
     * Normalizes a Point "p" from screen coordinates to a width and height of
     * [0 .. 1000].
     * 
     * @param p
     *            The point to be normalized.
     * 
     * @return The Normalized coordinates of "p".
     */
    public static Point normalize(Point p) {
        int x = new Double(p.getX()).intValue();
        int y = new Double(p.getY()).intValue();

        int xx = normalizeWidth(x);
        int yy = normalizeHeight(y);

        Point pp = new Point(xx, yy);
        return pp;
    }

    /**
     * Returns all of the Transitions in "tt" that are inbound connections to
     * Activity "a". Returns "null" or zero length array if no inbound
     * transitions are found.
     * 
     * @param a
     *            The Activity whose inbound transitions are needed.
     * @param tt
     *            An array of available transitions.
     * 
     * @return An array of inbound Transitions to "a".
     */
    public static Transition[] getInboundTransitions(Activity a, Transition[] tt) {
        ArrayList<Transition> temp = new ArrayList<Transition>();
        // If any of our input object are null or empty, then return null.

        if ((a != null) && (tt != null) && (tt.length > 0)) {
            Integer activityId = a.getActivityId();

            // Iterate over the array of Transitions. Add any transitions
            // inbound to "a" to our "temp" array list.
            for (Transition t : tt) {
                if (activityId.equals(t.getToId())) {
                    temp.add(t);
                }
            }
        }

        return temp.toArray(new Transition[0]);
    }

    /**
     * Returns all of the Transitions in "tt" that are outbound connections from
     * Activity "a". Returns "null" or zero length array if no outbound
     * transitions are found.
     * 
     * @param a
     *            The Activity whose outbound transitions are needed.
     * @param tt
     *            An array of available transitions.
     * 
     * @return An array of outbound Transitions to "a".
     */
    public static Transition[] getOutboundTransitions(Activity a,
            Transition[] tt) {
        ArrayList<Transition> temp = new ArrayList<Transition>();
        // If any of our input object are null or empty, then return null.

        if ((a != null) && (tt != null) && (tt.length > 0)) {
            Integer activityId = a.getActivityId();

            // Iterate over the array of Transitions. Add any transitions
            // outbound from "a" to our "temp" array list.
            for (Transition t : tt) {
                if (activityId.equals(t.getFromId())) {
                    temp.add(t);
                }
            }
        }

        return temp.toArray(new Transition[0]);
    }
}

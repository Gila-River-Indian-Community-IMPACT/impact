package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * <p>
 * Title: TransitionDef
 * </p>
 * 
 * <p>
 * Description: Represents a Transition definition object in the database. Once
 * constructed, this object is immutable.
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
 * @version 1.0
 */

public class TransitionDef extends BaseDB {
    private String code;
    private String description;
    private boolean isInputTrans;
    private boolean isOutputTrans;

    /**
     * Constructor. The "code" is the object key and is typically 1-4
     * characters. The "desc" is the user-friendly description and is typically
     * 1-40 characters. If this transition is available as an input transition,
     * then "inputTrans" should be set to "true". Similarly, if this transition
     * is available as an output transition, then "outputTrans" should be set to
     * "false". Note that a transition can be both an input and an output
     * transition.
     * 
     * @param code
     *            Transition code.
     * @param desc
     *            Transition description.
     * @param inputTrans
     *            "True" if this is an input transistion.
     * @param outputTrans
     *            "True" if this is an output transition.
     */
    public TransitionDef(String code, String desc, boolean inputTrans,
            boolean outputTrans) {
        this.code = code;
        this.description = desc;
        this.isInputTrans = inputTrans;
        this.isOutputTrans = outputTrans;
    }

    /**
     * Returns the current transition code.
     * 
     * @return String Transition code.
     */
    public final String getCode() {
        return code;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the current transition description.
     * 
     * @return String Transition description.
     */
    public final String getDescription() {
        return description;
    }

    public final void setDescription(String desc) {
        description = desc;
    }

    /**
     * Returns "true" if this transition is usable as an input transition.
     * 
     * @return boolean
     */
    public final boolean isInputTransition() {
        return this.isInputTrans;
    }

    /**
     * Returns "true" if this transition is usable as an output transition.
     * 
     * @return boolean
     */
    public final boolean isOutputTransition() {
        return this.isOutputTrans;
    }

    public final void populate(ResultSet rs) {
        try {
            setCode(rs.getString("transistion_def_cd"));
            setDescription(rs.getString("transistion_def_dsc"));
            setLastModified(rs.getInt("last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}

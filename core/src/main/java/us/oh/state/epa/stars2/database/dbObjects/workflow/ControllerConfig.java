package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * <p>
 * Title: OSSMDB
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */
public class ControllerConfig extends BaseDB {
    private String controlType;
    private String controlObject;
    private String controllerName;

    /**
     * @param old
     */
    public ControllerConfig(ControllerConfig old) {
        super(old);
        if (old != null) {
            setControllerClassName(old.getControllerClassName());
            setControlObject(old.getControlObject());
            setControlType(old.getControlType());
        }
    }

    public ControllerConfig() {
    }

    public final String getControlType() {
        return this.controlType;
    }

    public final void setControlType(String ct) {
        this.controlType = ct;
    }

    public final String getControlObject() {
        return this.controlObject;
    }

    public final void setControlObject(String cobj) {
        this.controlObject = cobj;
    }

    public final String getControllerClassName() {
        return this.controllerName;
    }

    public final void setControllerClassName(String cname) {
        this.controllerName = cname;
    }

    public final void populate(ResultSet rs) {
        try {
            setControlType(rs.getString("control_type"));
            setControlObject(rs.getString("object"));
            setControllerClassName(rs.getString("controller_class"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}

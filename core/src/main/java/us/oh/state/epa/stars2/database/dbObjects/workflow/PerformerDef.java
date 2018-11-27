package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author pyeh
 *
 */
public class PerformerDef extends BaseDB {
    private String code;
    private String desc;
    private String panel;

    /**
     * 
     */
    public PerformerDef() {
        super();
    }

    /**
     * @param old
     */
    public PerformerDef(PerformerDef old) {
        super(old);
        if (old != null) {
            setCode(old.getCode());
            setDesc(old.getDesc());
            setPanel(old.getPanel());
        }
    }

    public final String getCode() {
        return code;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public final String getDesc() {
        return desc;
    }

    public final void setDesc(String desc) {
        this.desc = desc;
    }

    public final String getPanel() {
        return panel;
    }

    public final void setPanel(String panel) {
        this.panel = panel;
    }

    public final void populate(ResultSet rs) {
        try {
            setCode(rs.getString("performer_type_cd"));
            setDesc(rs.getString("performer_type_dsc"));
            setPanel(rs.getString("performer_type_panel"));
            setLastModified(rs.getInt("last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}

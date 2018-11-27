package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
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
@SuppressWarnings("serial")
public class DataDetailBase extends BaseDB {
    private Integer dataDetailId;
    private String dataDetailLbl;

    /**
     * 
     */
    public DataDetailBase() {
        super();
    }

    /**
     * @param old
     */
    public DataDetailBase(DataDetailBase old) {
        super(old);
        if (old != null) {
            setDataDetailId(old.getDataDetailId());
            setDataDetailLbl(old.getDataDetailLbl());
        }
    }

    public final Integer getDataDetailId() {
        return dataDetailId;
    }

    public final void setDataDetailId(Integer dataDetailId) {
        this.dataDetailId = dataDetailId;
    }

    public final String getDataDetailLbl() {
        return dataDetailLbl;
    }

    public final void setDataDetailLbl(String dataDetailLbl) {
        this.dataDetailLbl = dataDetailLbl;
    }

    public final void populate(ResultSet rs) {
        try {
            setDataDetailId(AbstractDAO.getInteger(rs, "data_detail_id"));
            setDataDetailLbl(rs.getString("data_detail_lbl"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}

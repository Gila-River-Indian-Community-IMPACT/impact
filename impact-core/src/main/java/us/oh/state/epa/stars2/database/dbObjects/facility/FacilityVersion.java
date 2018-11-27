package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author Kbradley Note: (TO DO LATER) some attributes can go to base later.
 */
public class FacilityVersion extends BaseDB {
    private Integer fpId;
    private Integer versionId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String note;
	private boolean copyOnChange;

    public FacilityVersion() {
    }

    /**
     * @return
     */
    public final String getNote() {
        return note;
    }

    /**
     * @param cpId
     */
    public final void setNote(String note) {
        this.note = note;
    }

    /**
     * @return
     */
    public final Integer getFpId() {
        return fpId;
    }

    /**
     * @param fpId
     */
    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    /**
     * @return
     */
    public final Integer getVersionId() {
        return versionId;
    }

    /**
     * @param versionId
     */
    public final void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    /**
     * @return
     */
    public final Timestamp getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     */
    public final void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    /**
     * @return
     */
    public final Timestamp getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     */
    public final void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

	public final boolean isCopyOnChange() {
		return copyOnChange;
	}

	public final void setCopyOnChange(boolean copyOnChange) {
		this.copyOnChange = copyOnChange;
	}

    
    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setVersionId(AbstractDAO.getInteger(rs, "version_id"));
            setStartDate(rs.getTimestamp("start_dt"));
            setEndDate(rs.getTimestamp("end_dt"));
            if(this.endDate != null)
            {
            	setCopyOnChange(true);
            }else{
            setCopyOnChange(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("copy_on_change")));
            }
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
        
        try {
            setNote(rs.getString("note_txt"));
        } catch (SQLException sqle) {
        	logger.warn("Should happen only for Migration-- ");
        }
    }
}

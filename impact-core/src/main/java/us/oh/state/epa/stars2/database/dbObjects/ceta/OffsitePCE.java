package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class OffsitePCE extends BaseDB {
	private Integer offsitePceId;
	private String afsId;
	private Timestamp afsdate;
    // The following gotten by linking based upon Discovery type and Discovery ID.
    private transient Integer actionId;  // From Action
    private Timestamp actionDate;
    private transient Integer enforcementId;  // From Enforcement
    private Integer fpId;
    private transient String facilityId; // from facility specified by Enforcement
    private transient String scscId; // from facility specified by Enforcement
    private transient String facilityNm; // from facility specified by Enforcement
    private transient Integer versionId;  // from facility specified by Enforcement
	
	public void populate(ResultSet rs) throws SQLException {
		try {
			// required attributes
			setOffsitePceId(AbstractDAO.getInteger(rs, "offsite_pce_id"));
			setActionDate(rs.getTimestamp("action_date"));
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setAfsId(rs.getString("afs_id"));
			setAfsdate(rs.getTimestamp("afs_dt"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			
			// optional attributes
			setActionId(AbstractDAO.getInteger(rs, "action_id"));
            setEnforcementId(AbstractDAO.getInteger(rs, "enforcement_id"));          
            setVersionId(AbstractDAO.getInteger(rs, "version_id"));
            setFacilityId(rs.getString("facility_id"));
            setScscId(rs.getString("scsc_id"));
            setFacilityNm(rs.getString("facility_nm"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
	}
	public final Integer getOffsitePceId() {
		return offsitePceId;
	}
	public final void setOffsitePceId(Integer offsitePceId) {
		this.offsitePceId = offsitePceId;
	}
	public final String getAfsId() {
		return afsId;
	}
	public final void setAfsId(String afsId) {
		this.afsId = afsId;
	}
	public final Timestamp getAfsdate() {
		return afsdate;
	}
	public final void setAfsdate(Timestamp afsdate) {
		this.afsdate = afsdate;
	}
    public Integer getActionId() {
        return actionId;
    }
    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }
    public Integer getEnforcementId() {
        return enforcementId;
    }
    public void setEnforcementId(Integer enforcementId) {
        this.enforcementId = enforcementId;
    }
    public Integer getFpId() {
        return fpId;
    }
    public void setFpId(Integer fpId) {
        this.fpId = fpId;
    }
    public Timestamp getActionDate() {
        return actionDate;
    }
    public void setActionDate(Timestamp actionDate) {
        this.actionDate = actionDate;
    }
    public Integer getVersionId() {
        return versionId;
    }
    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }
    public String getFacilityId() {
        return facilityId;
    }
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    public String getFacilityNm() {
        return facilityNm;
    }
    public void setFacilityNm(String facilityNm) {
        this.facilityNm = facilityNm;
    }
    public String getScscId() {
        return scscId;
    }
    public void setScscId(String scscId) {
        this.scscId = scscId;
    }	
}

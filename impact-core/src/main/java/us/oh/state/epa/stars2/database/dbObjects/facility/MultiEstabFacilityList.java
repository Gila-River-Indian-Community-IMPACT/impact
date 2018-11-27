package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author RYamini
 * 
 */
@SuppressWarnings("serial")
public class MultiEstabFacilityList extends BaseDB {
    private Integer fpId;
    private Integer versionId;
    private String facilityId;
    private String name;

    public MultiEstabFacilityList() {
        super();
    }
    
    public MultiEstabFacilityList(MultiEstabFacilityList old) {
        super(old);
        setFpId(old.getFpId());
        setVersionId(old.getVersionId());
        setFacilityId(old.getFacilityId());
        setName(old.getName());
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
    public final String getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId
     */
    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * @return
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public void populate(ResultSet rs) {
        try {
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setFacilityId(rs.getString("facility_id"));
            setVersionId(AbstractDAO.getInteger(rs, "version_id"));
            setName(rs.getString("facility_nm"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}

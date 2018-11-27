package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;

/**
 * @author Kbradley Note: (TO DO LATER) some attributes can go to base later.
 */
public class FacilityHistList extends BaseDB {
    private Integer fpId;
    private Integer versionId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String facilityId;
    private String name;
    private String desc;
    private Timestamp revisedDate;
    private String addressLine1;
    private String cityName;
    private String countyCd;
    private String operatingStatusCd;
    private String permitClassCd;
	private String facilityTypeCd;
	private String companyName;
	private String cmpId;
    private Address phyAddr;

    public FacilityHistList() {
    }


    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public final Integer getVersionId() {
        return versionId;
    }

    public final void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public final Timestamp getStartDate() {
        return startDate;
    }

    public final void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public final Timestamp getEndDate() {
        return endDate;
    }

    public final void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public final Timestamp getRevisedDate() {
        return revisedDate;
    }

    public final void setRevisedDate(Timestamp revisedDate) {
        this.revisedDate = revisedDate;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getDesc() {
        return desc;
    }

    public final void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public void populate(ResultSet rs) {
        try {
        
            Address tempPhyAddr = new Address();
            tempPhyAddr.populate(rs);
            setPhyAddr(tempPhyAddr);
            
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setVersionId(AbstractDAO.getInteger(rs, "version_id"));
            setStartDate(rs.getTimestamp("start_dt"));
            setEndDate(rs.getTimestamp("end_dt"));
            setFacilityId(rs.getString("facility_id"));
            setName(rs.getString("facility_nm"));
            setDesc(rs.getString("facility_desc"));
            setOperatingStatusCd(rs.getString("operating_status_cd"));
            setPermitClassCd(rs.getString("permit_classification_cd"));
            setFacilityTypeCd(rs.getString("facility_type_cd"));
			setCompanyName(rs.getString("company_name"));
			setCmpId(rs.getString("cmp_id"));
			setCountyCd(rs.getString("county_cd"));

        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getAddressLine1() {
        return addressLine1;
    }

    public final void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public final String getCityName() {
        return cityName;
    }

    public final void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public final String getCountyCd() {
        return countyCd;
    }

    public final void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public String getCmpId() {
		return cmpId;
	}
	
	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}
	
    public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public final String getPermitClassCd() {
        return permitClassCd;
    }

    public final void setPermitClassCd(String permitClassCd) {
        this.permitClassCd = permitClassCd;
    }
	public final String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public final void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}
	
    public final String getOperatingStatusCd() {
        return operatingStatusCd;
    }

    public final void setOperatingStatusCd(String operatingStatusCd) {
        this.operatingStatusCd = operatingStatusCd;
    }
    
    public final void setPhyAddr(Address phyAddr) {
        this.phyAddr = phyAddr;
    }

    public final Address getPhyAddr() {
        return phyAddr;
    }
    
    public final String getGoogleMapsURL() {
    	return LocationCalculator.getGoogleMapsURL(phyAddr.getLatitude(), phyAddr.getLongitude(), this.getName());
    }
}

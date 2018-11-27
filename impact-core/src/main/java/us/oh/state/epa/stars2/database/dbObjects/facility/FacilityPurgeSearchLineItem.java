package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;



@SuppressWarnings("serial")
public class FacilityPurgeSearchLineItem extends BaseDB {
//  private Integer versionId;
    private Integer fpId;


	private String facilityId;
    private String facilityName;
	private String cmpId;	
	private String companyName;
	
	private String operatingStatusCd;
	private Timestamp shutdownDate;
	private String permitClassCd; //FacilityClass
	private String facilityTypeCd;   
    
	private Address phyAddr;

    public Integer getFpId() {
		return fpId;
	}
    
	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
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

	public String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public void setOperatingStatusCd(String operatingStatusCd) {
		this.operatingStatusCd = operatingStatusCd;
	}

	public Timestamp getShutdownDate() {
		return shutdownDate;
	}

	public void setShutdownDate(Timestamp shutdownDate) {
		this.shutdownDate = shutdownDate;
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public Address getPhyAddr() {
		return phyAddr;
	}

	public void setPhyAddr(Address phyAddr) {
		this.phyAddr = phyAddr;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			Address tempPhyAddr = new Address();
			tempPhyAddr.populate(rs);
			setPhyAddr(tempPhyAddr);
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_nm"));
			setCmpId(rs.getString("cmp_id"));
			setCompanyName(rs.getString("name"));          
			setOperatingStatusCd(rs.getString("operating_status_cd"));
			setShutdownDate(rs.getTimestamp("last_shutdown_date"));
			setPermitClassCd(rs.getString("permit_classification_cd"));
			setFacilityTypeCd(rs.getString("facility_type_cd"));

			setLastModified(AbstractDAO.getInteger(rs, "facility_lm"));

		} catch (SQLException sqle) {
			logger.error("Required field error");
		}
		
	}

	public final String getGoogleMapsURL() {
		return LocationCalculator.getGoogleMapsURL(phyAddr.getLatitude(),
				phyAddr.getLongitude(), this.facilityName);
	}

}

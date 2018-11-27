package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;
import us.wy.state.deq.impact.App;

/**
 * @author
 */
@SuppressWarnings("serial")
public class FacilityRequestList extends BaseDB {
    private String requestId;
    private String reqId;
    private String contactId;
    private String cntId;
    private String firstNm;
	private String lastNm;
	private String externalUsername;
    private String name;
	private Address phyAddr;
	private String doLaaCd;
	private String operatingStatusCd;
	private String requestStatusCd;
	private String reportingTypeCd;

	// used for Facility Type
	private String facilityTypeCd;
	private String facilityTypeDsc;

	private String companyName;
	private String cmpId;

	private transient Timestamp submitDate;
	
	private String truncatedMemo;
	private String memo;
	
	private InfrastructureService infrastructureService = App.getApplicationContext().getBean(InfrastructureService.class);

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}
	
	public FacilityRequestList() {
		super();
	}

	public FacilityRequestList(FacilityRequestList old) {
		super(old);
	    setRequestId(old.getRequestId());
	    setReqId(old.getReqId());
	    setContactId(old.getContactId());
	    setCntId(old.getCntId());
	    setFirstNm(old.getFirstNm());
		setLastNm(old.getLastNm());
		setExternalUsername(old.getExternalUsername());
	    setName(old.getName());
	    setPhyAddr(old.getPhyAddr());
		setDoLaaCd(old.getDoLaaCd());
		setOperatingStatusCd(old.getOperatingStatusCd());
		setRequestStatusCd(old.getRequestStatusCd());
		setReportingTypeCd(old.getReportingTypeCd());

		// used for Facility Type
		setFacilityTypeCd(old.getFacilityTypeCd());
		setFacilityTypeDsc(old.getFacilityTypeDsc());

		setCompanyName(old.getCompanyName());
		setCmpId(old.getCmpId());

		setSubmitDate(old.getSubmitDate());
		
		setTruncatedMemo(old.getTruncatedMemo());
		setMemo(old.getMemo());

	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public final void populate(ResultSet rs) {
		try {
			setRequestId(rs.getString("request_id"));
			setReqId(rs.getString("req_id"));
	        setName(rs.getString("facility_nm"));
	        setContactId(rs.getString("contact_id"));
	        setCntId(rs.getString("cnt_id"));
	        setExternalUsername(rs.getString("envite_username"));
			setFirstNm(rs.getString("first_nm"));
			setLastNm(rs.getString("last_nm"));
			
			if (!rs.wasNull()) {
			Address tempPhyAddr = new Address();
			tempPhyAddr.populate(rs);
			setPhyAddr(tempPhyAddr);
			}
			getPhyAddr().setAddressId(rs.getInt("address_id"));
			setOperatingStatusCd(rs.getString("operating_status_cd"));
			setRequestStatusCd(rs.getString("request_status_cd"));
			setFacilityTypeCd(rs.getString("facility_type_cd"));
			setFacilityTypeDsc(rs.getString("facility_type_dsc"));
			setCompanyName(rs.getString("name"));
			setCmpId(rs.getString("cmp_id"));
			setMemo(rs.getString("memo"));

			try {
				setSubmitDate(rs.getTimestamp("submit_dt"));
			} catch (SQLException e) {
				logger.error("bad submit date");
			}
			
			setLastModified(AbstractDAO.getInteger(rs, "facility_request_lm"));

		} catch (SQLException sqle) {
			logger.error("Required field error");
		}
	}
	
	

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getCntId() {
		return cntId;
	}

	public void setCntId(String cntId) {
		this.cntId = cntId;
	}

	public String getFirstNm() {
		return firstNm;
	}

	public void setFirstNm(String firstNm) {
		this.firstNm = firstNm;
	}

	public String getLastNm() {
		return lastNm;
	}

	public void setLastNm(String lastNm) {
		this.lastNm = lastNm;
	}

	public String getExternalUsername() {
		return externalUsername;
	}

	public void setExternalUsername(String externalUsername) {
		this.externalUsername = externalUsername;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getPhyAddr() {
		return phyAddr;
	}

	public void setPhyAddr(Address phyAddr) {
		this.phyAddr = phyAddr;
	}

	public String getDoLaaCd() {
		return doLaaCd;
	}

	public void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public void setOperatingStatusCd(String operatingStatusCd) {
		this.operatingStatusCd = operatingStatusCd;
	}

	public String getRequestStatusCd() {
		return requestStatusCd;
	}

	public void setRequestStatusCd(String requestStatusCd) {
		this.requestStatusCd = requestStatusCd;
	}

	public String getReportingTypeCd() {
		return reportingTypeCd;
	}

	public void setReportingTypeCd(String reportingTypeCd) {
		this.reportingTypeCd = reportingTypeCd;
	}

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public String getFacilityTypeDsc() {
		return facilityTypeDsc;
	}

	public void setFacilityTypeDsc(String facilityTypeDsc) {
		this.facilityTypeDsc = facilityTypeDsc;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public Timestamp getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Timestamp submitDate) {
		this.submitDate = submitDate;
	}

	public String getTruncatedMemo() {
		return truncatedMemo;
	}

	public void setTruncatedMemo(String truncatedMemo) {
		this.truncatedMemo = truncatedMemo;
	}

	public final String getGoogleMapsURL() {
		return LocationCalculator.getGoogleMapsURL(phyAddr.getLatitude(),
				phyAddr.getLongitude(), getName());
	}
	
	public String getMemo() {
        return memo;
    }

    public String getShortMemo() {
        String rtn = memo;
        if(memo != null && memo.length()> 75) {
            rtn = memo.substring(0, 75) + "...";
        }
        return rtn;
    }

    public void setMemo(String memo) {
        this.memo = memo;
        if(this.memo != null && this.memo.length() > 25) {
        	setTruncatedMemo(this.memo.substring(0, 25) + "...");
        } else {
        	setTruncatedMemo(this.memo);
        }
        setDirty(true);
    }
	
	public String getCountyNm(String countyCd) {
		String countyNm = "UNKNOWN";
		try {
			CountyDef cd = getInfrastructureService().retrieveCounty(countyCd);
			if (cd != null) {
				if (cd.getCountyNm() != null && cd.getCountyNm().length() > 0) {
					countyNm = cd.getCountyNm();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return countyNm;
	}
	
	public final String getCountyCd() {
		return phyAddr.getCountyCd();
	}
}

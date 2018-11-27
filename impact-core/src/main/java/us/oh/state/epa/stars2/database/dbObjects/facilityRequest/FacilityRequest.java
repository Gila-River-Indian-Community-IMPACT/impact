package us.oh.state.epa.stars2.database.dbObjects.facilityRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;
import us.wy.state.deq.impact.App;

/**
 * @author
 */
@SuppressWarnings("serial")
public class FacilityRequest extends BaseDB {
	
	private Integer requestId;
	private String reqId;
	private Integer contactId;
	private String firstNm;
	private String lastNm;
	private String externalUsername;
	private String cntId;
	private String name;
	private String desc;
	private String facilityTypeDsc;
	private Address phyAddr;
	private String operatingStatusCd;
	private String requestStatusCd;
	private String doLaaCd;
	private String facilityTypeCd;
	private String companyName;
	private String memo;
	private Integer companyId;
	private String companyIdString;
	private Integer currentUserId;
	private Timestamp submitDate;
	
	private String truncatedMemo;
	
	private InfrastructureService infrastructureService = App.getApplicationContext().getBean(InfrastructureService.class);

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityRequest() {
		super();
		this.setName("");
		this.setCompanyName("");
		this.setDoLaaCd("D9");
		this.setRequestId(null);
		this.setSubmitDate(new Timestamp(System.currentTimeMillis()));
		this.setCurrentUserId(null);
		phyAddr = new Address();
		requiredFields();
	}
	
	public FacilityRequest(FacilityRequest request) {
		
		this.setRequestId(request.getRequestId());
		this.setReqId(request.getReqId());
		this.setContactId(request.getContactId());
		this.setFirstNm(request.getFirstNm());
		this.setLastNm(request.getLastNm());
		this.setExternalUsername(request.getExternalUsername());
		this.setCntId(request.getCntId());
		this.setName(request.getName());
		this.setDesc(request.getDesc());
		this.setFacilityTypeDsc(request.getFacilityTypeDsc());
		this.setPhyAddr(new Address(request.getPhyAddr()));
		this.getPhyAddr().setAddressId(request.getPhyAddr().getAddressId());
		this.setOperatingStatusCd(request.getOperatingStatusCd());
		this.setRequestStatusCd(request.getRequestStatusCd());
		this.setDoLaaCd(request.getDoLaaCd());
		this.setFacilityTypeCd(request.getFacilityTypeCd());
		this.setCompanyName(request.getCompanyName());
		this.setMemo(request.getMemo());
		this.setCompanyId(request.getCompanyId());
		this.setCompanyIdString(request.getCompanyIdString());
		this.setCurrentUserId(request.getCurrentUserId());
		this.setSubmitDate(request.getSubmitDate());
		this.setTruncatedMemo(request.getTruncatedMemo());
	}
	
	public void requiredFields() {
		
		requiredField(name, "name", "Facility Name", "facility");
		//requiredField(companyName, "companyName", "Company Name", "facility");
		requiredField(facilityTypeCd, "facilityTypeCd", "Facility Type", "facility");
		requiredField(operatingStatusCd, "operatingStatus", "Operating Status", "facility");
		return;
	}
	
	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}
	
	public final String getReqId() {
		return reqId;
	}

	public final void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public final void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public final Integer getCompanyId() {
		return companyId;
	}
	
	public String getCompanyIdString() {
		return companyIdString;
	}

	public void setCompanyIdString(String companyIdString) {
		this.companyIdString = companyIdString;
	}

	public final String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public final void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public final void setOperatingStatusCd(String operatingStatusCd) {
		if (requiredField(operatingStatusCd, "facOperatingStatus",
				"Operating Status", "facility")) {}
		this.operatingStatusCd = operatingStatusCd;
	}
	
	public final String getRequestStatusCd() {
		return requestStatusCd;
	}

	public final void setRequestStatusCd(String requestStatusCd) {
		this.requestStatusCd = requestStatusCd;
	}
	
	public final String getFacilityTypeDsc() {
		return facilityTypeDsc;
	}

	public final void setFacilityTypeDsc(String facilityTypeDsc) {
		this.facilityTypeDsc = facilityTypeDsc;
	}

	public final void setPhyAddr(Address phyAddr) {
		this.phyAddr = phyAddr;
	}

	public final Address getPhyAddr() {
		return phyAddr;
	}

	public final String getDesc() {
		return desc;
	}

	public final void setDesc(String desc) {
		this.desc = desc;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		requiredField(name.trim(), "name", "Facility Name", "facility");
		this.name = name.trim();
	}
	
	public final Float getLatitudeDeg() {
		return Float.parseFloat(this.phyAddr.getLatitude());
	}

	public final Float getLongitude() {
		return Float.parseFloat(this.phyAddr.getLongitude());
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public final void populate(ResultSet rs) {
		try {
			setRequestId(AbstractDAO.getInteger(rs,"request_id"));
			setReqId(rs.getString("req_id"));
			setCompanyId(AbstractDAO.getInteger(rs,"company_id"));
			setCompanyIdString(rs.getString("cmp_id"));
			setCompanyName(rs.getString("name"));
			setName(rs.getString("facility_nm"));
			setDesc(rs.getString("facility_desc"));
			setContactId(AbstractDAO.getInteger(rs,"contact_id"));
			setCntId(rs.getString("cnt_id"));
			setExternalUsername(rs.getString("envite_username"));
			setFirstNm(rs.getString("first_nm"));
			setLastNm(rs.getString("last_nm"));
			setOperatingStatusCd(rs.getString("operating_status_cd"));
			setRequestStatusCd(rs.getString("request_status_cd"));
			setFacilityTypeCd(rs.getString("facility_type_cd"));
			setFacilityTypeDsc(rs.getString("facility_type_dsc"));
			setMemo(rs.getString("memo"));
			setSubmitDate(rs.getTimestamp("submit_dt"));
			getPhyAddr().setAddressId(AbstractDAO.getInteger(rs,"address_id"));
			if (!rs.wasNull()) {
				Address address = new Address();
				address.populate(rs);
				setPhyAddr(address);
			}
			
			try {
				setSubmitDate(rs.getTimestamp("submit_dt"));
			} catch (SQLException e) {
				logger.error("bad submit date");
			}
			setLastModified(AbstractDAO.getInteger(rs, "facility_request_lm"));
		} catch (SQLException sqle) {
			logger.error("Invalid column in populate", sqle);
		} finally {
			
		}
	}

	//public final ValidationMessage[] validateForCreateFacility() {
	//	this.clearValidationMessages();
	//	requiredField(companyName, "companyName", "Company Name", "facility");
	//	requiredFields();
	//	validationMessages.remove("facOperatingStatus");
	//
	//	return new ArrayList<ValidationMessage>(validationMessages.values())
	//			.toArray(new ValidationMessage[0]);
	//}

	private void basicValidations() {
		this.clearValidationMessages();
		requiredFields();
	}

	public final ValidationMessage[] validate(boolean internalApp) {
		return validate(internalApp, false);
	}

	public final ValidationMessage[] validate(boolean internalApp,
			boolean passRulesRegs) {
		basicValidations();
		ArrayList<ValidationMessage> msgs = new ArrayList<ValidationMessage>();

		int cnt = 0;
		for (ValidationMessage vm : msgs) {
			String cntStr = String.valueOf(cnt++);
			validationMessages.put(cntStr, vm);
		}

		// validate address
		int addMsgCnt = 0;
		for (ValidationMessage msg : phyAddr.validateFacilityLocation()) {
			validationMessages.put("address:" + addMsgCnt, msg);
			addMsgCnt++;
		}

		//checkOpStatus();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public void unionOperatingStatus(String opStat) {
		if (getOperatingStatusCd() == null) {
			setOperatingStatusCd(opStat);
			return;
		}
		if (OperatingStatusDef.OP.equals(opStat))
			setOperatingStatusCd(opStat);
	}

	public final ValidationMessage[] checkBasicFacility() {
		this.clearValidationMessages();
		requiredFields();
		ValidationMessage[] msgs = this.validate();
		return msgs;
	}

	public final String getGoogleMapsURL() {
		return LocationCalculator.getGoogleMapsURL(phyAddr.getLatitude(),
				phyAddr.getLongitude(), getName());
	}
	
	public final ValidationMessage[] validateForCreateFacilityRequest() {
		this.clearValidationMessages();
		
		requiredFields();
		validationMessages.remove("facOperatingStatus");

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
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
    
    public String getTruncatedMemo() {
		return truncatedMemo;
	}

	public void setTruncatedMemo(String truncatedMemo) {
		this.truncatedMemo = truncatedMemo;
	}
    
    public Integer getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}
	
	public  Timestamp getSubmitDate() {
		return submitDate;
	}

	public  void setSubmitDate(Timestamp submitDate) {
		this.submitDate = submitDate;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
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
}

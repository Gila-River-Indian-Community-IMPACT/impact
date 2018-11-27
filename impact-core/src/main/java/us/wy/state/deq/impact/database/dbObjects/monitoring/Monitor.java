package us.wy.state.deq.impact.database.dbObjects.monitoring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.MonitorParamTypeDef;
import us.oh.state.epa.stars2.def.MonitorStatusDef;
import us.oh.state.epa.stars2.def.MonitorTypeDef;
import us.oh.state.epa.stars2.def.WxParamDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class Monitor extends BaseDB {

	private Integer monitorId;
	
	private Integer groupId;
	
	private String mgrpId;
	
	private String groupName;
	
	private String groupDescription;
	
	private Integer siteId;
	
	private String mstId;
	
	private String siteName;
	
	private String type;
	
	private String name;
	
	private String parameter;   // ambient monitor parameter
	
	private String status;
	
	private Date startDate;
	
	private Date endDate;
	
	private Integer parameterCode;
	
	private Integer parameterOccurrenceCode;
	
	private Integer methodCode;
	
	private String durationCode;
	
	private Integer unitCode;
	
	private String frequencyCode;
	
	private String comments;
	
	private String mntrId;
	
	private String companyName;
	
	private List<MonitorNote> notes;
	
	private String parameterMet;  // meteorological monitor parameter
	
	private String parameterDesc;

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setMonitorId(AbstractDAO.getInteger(rs, "monitor_id"));
			setSiteId(AbstractDAO.getInteger(rs, "site_id"));
			setType(rs.getString("type_cd"));
			setParameter(rs.getString("parameter_cd"));
			setStatus(rs.getString("status_cd"));
			setName(rs.getString("name"));
			setParameterCode(AbstractDAO.getInteger(rs, "parameter2_cd"));
			setParameterOccurrenceCode(AbstractDAO.getInteger(rs, "param_occurrence_cd"));
			setMethodCode(AbstractDAO.getInteger(rs, "method_cd"));
			setUnitCode(AbstractDAO.getInteger(rs, "unit_cd"));
			setStartDate(rs.getDate("start_date"));
			setEndDate(rs.getDate("end_date"));
			setDurationCode(rs.getString("duration_cd"));
			setFrequencyCode(rs.getString("freq_cd"));
			setComments(rs.getString("comments"));
			setMntrId(rs.getString("mntr_id"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			setMstId(rs.getString("mst_id"));
			setMgrpId(rs.getString("mgrp_id"));
			
			try {
				setCompanyName(rs.getString("company_name"));
			} catch (SQLException e) {
				logger.warn("company_name");
			}
			
			setGroupDescription(rs.getString("group_description"));
			setGroupName(rs.getString("group_name"));
			setSiteName(rs.getString("site_name"));
			setParameterMet(rs.getString("met_parameter_cd"));

		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
		
	}




	public String getCompanyName() {
		return companyName;
	}




	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}




	public String getGroupName() {
		return groupName;
	}




	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}




	public String getMgrpId() {
		return mgrpId;
	}




	public void setMgrpId(String mgrpId) {
		this.mgrpId = mgrpId;
	}




	public String getMstId() {
		return mstId;
	}




	public void setMstId(String mstId) {
		this.mstId = mstId;
	}




	public Integer getParameterOccurrenceCode() {
		return parameterOccurrenceCode;
	}




	public void setParameterOccurrenceCode(Integer parameterOccurrenceCode) {
		this.parameterOccurrenceCode = parameterOccurrenceCode;
	}




	public String getMntrId() {
		return mntrId;
	}




	public void setMntrId(String mntrId) {
		this.mntrId = mntrId;
	}




	public Integer getMonitorId() {
		return monitorId;
	}




	public void setMonitorId(Integer monitorId) {
		this.monitorId = monitorId;
	}




	public Integer getGroupId() {
		return groupId;
	}




	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}




	public String getGroupDescription() {
		return groupDescription;
	}




	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}




	public Integer getSiteId() {
		return siteId;
	}




	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}




	public String getSiteName() {
		return siteName;
	}




	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}




	public String getType() {
		return type;
	}




	public void setType(String type) {
		this.type = type;
	}




	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public String getParameter() {
		return parameter;
	}




	public void setParameter(String parameter) {
		this.parameter = parameter;
	}




	public String getStatus() {
		return status;
	}




	public void setStatus(String status) {
		this.status = status;
	}




	public Date getStartDate() {
		return startDate;
	}




	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}




	public Date getEndDate() {
		return endDate;
	}




	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}




	public Integer getParameterCode() {
		return parameterCode;
	}




	public void setParameterCode(Integer parameterCode) {
		this.parameterCode = parameterCode;
	}




	public Integer getMethodCode() {
		return methodCode;
	}




	public void setMethodCode(Integer methodCode) {
		this.methodCode = methodCode;
	}




	public String getDurationCode() {
		return durationCode;
	}




	public void setDurationCode(String durationCode) {
		this.durationCode = durationCode;
	}




	public Integer getUnitCode() {
		return unitCode;
	}




	public void setUnitCode(Integer unitCode) {
		this.unitCode = unitCode;
	}




	public String getFrequencyCode() {
		return frequencyCode;
	}




	public void setFrequencyCode(String frequencyCode) {
		this.frequencyCode = frequencyCode;
	}




	public String getComments() {
		return comments;
	}




	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public ValidationMessage[] validate() {
		this.validationMessages.clear();
		
		if(Utility.isNullOrEmpty(this.getType())) {
			ValidationMessage valMsg = new ValidationMessage("type", "Set a monitor type.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("type", valMsg);
		}
		
		if(Utility.isNullOrEmpty(this.getName())) {
			ValidationMessage valMsg = new ValidationMessage("name", "Set a monitor name.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("name", valMsg);
		}
		
		if (this.isTypeMeteorological()) {
			if (Utility.isNullOrEmpty(this.getParameterMet())) {
				ValidationMessage valMsg = new ValidationMessage("parameterMet",
						"Set a meteorological monitor parameter.",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("parameterMet", valMsg);
			}
		}
		
		if (this.isTypeAmbient()) {
			if (Utility.isNullOrEmpty(this.getParameter())) {
				ValidationMessage valMsg = new ValidationMessage("parameter",
						"Set an ambient air monitor parameter.",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("parameter", valMsg);
			}
		}
        
        if (this.getStatus() == null) {
        	ValidationMessage valMsg = new ValidationMessage("status", "Set a status.", ValidationMessage.Severity.ERROR);
            this.validationMessages.put("status", valMsg);        	
        }
        
        String statusActiveCd = MonitorStatusDef.getActiveCode();
        if (null == statusActiveCd) {
        	throw new RuntimeException("Cannot find monitor status active def code, unable to validate monitor site.");
        }
        
        if(this.getStartDate() == null) {
        	ValidationMessage valMsg = new ValidationMessage("startDate", "Set a start date.", ValidationMessage.Severity.ERROR);
            this.validationMessages.put("startDate", valMsg);
        }
        
        if(!statusActiveCd.equals(this.getStatus()) && this.getEndDate() == null) {
        	ValidationMessage valMsg = new ValidationMessage("endDate", "Set an end date.", ValidationMessage.Severity.ERROR);
            this.validationMessages.put("endDate", valMsg);
        }

        if (this.getStartDate() != null && 
        		this.getEndDate() != null && 
        		!statusActiveCd.equals(this.getStatus()) &&
        		this.getEndDate().before(this.getStartDate())) {
        	ValidationMessage valMsg = new ValidationMessage("endDate", "End date is before start date.", ValidationMessage.Severity.ERROR);
            this.validationMessages.put("endDate", valMsg);
    	}
        
        if (null != this.getParameterOccurrenceCode()) {
            checkRangeValues(this.getParameterOccurrenceCode(), 1, 99, "parameterOccurrenceCd",
            		"Parameter Occurrence Code", "Monitor: " + getMonitorId());
        }
		
        if (null != this.getMethodCode()) {
            checkRangeValues(this.getMethodCode(), 1, 999, "methodCd",
            		"Method Code", "Monitor: " + getMonitorId());
        }

        if (null != this.getUnitCode()) {
            checkRangeValues(this.getUnitCode(), 1, 201, "unitCd",
            		"Unit Code", "Monitor: " + getMonitorId());
        }

        if (null != this.getDurationCode()) {
        	if (!this.getDurationCode().matches("[1-9,A,B,C,D,E,F,H,I,J,L,V,W,X,Y,Z]")) {
            	ValidationMessage valMsg = new ValidationMessage("durationCd", "Duration code must use one of the following characters: 1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,H,I,J,L,V,W,X,Y,Z.", ValidationMessage.Severity.ERROR);
                this.validationMessages.put("durationCd", valMsg);        	
        	}
        }

        if (null != this.getFrequencyCode()) {
        	if (!this.getFrequencyCode().matches("[1-9,10,11,90,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U]{1,2}")) {
            	ValidationMessage valMsg = new ValidationMessage("frequencyCd", "Frequency code must use only the following characters: 1,2,3,4,5,6,7,8,9,10,11,90,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U.", ValidationMessage.Severity.ERROR);
                this.validationMessages.put("frequencyCd", valMsg);        	
        	}
        }
        
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	public final List<MonitorNote> getNotes() {
		if (notes == null) {
			notes = new ArrayList<MonitorNote>();
		}
		return notes;
	}

	public final void setNotes(List<MonitorNote> notes) {
		this.notes = new ArrayList<MonitorNote>();
		if (notes != null) {
			this.notes.addAll(notes);
		}
	}

	public final void addNote(MonitorNote a) {
		if (notes == null) {
			notes = new ArrayList<MonitorNote>();
		}
		if (a != null) {
			notes.add(a);
		}
	}
	
	public final boolean isTypeMeteorological() {
		if (!Utility.isNullOrEmpty(this.getType())) {
			if (this.getType().equals(MonitorTypeDef.METEOROLOGICAL)) {
				return true;
			}

		}
		return false;
	}
	
	public final boolean isTypeAmbient() {
		if (!Utility.isNullOrEmpty(this.getType())) {
			if (this.getType().equals(MonitorTypeDef.AMBIENT_AIR)) {
				return true;
			}

		}
		return false;
	}
	
	public String getParameterMet() {
		return parameterMet;
	}

	public void setParameterMet(String parameterMet) {
		this.parameterMet = parameterMet;
	}
	
	public final String getParameterDesc() {
		this.parameterDesc = null;
		if (this.isTypeAmbient()) {
			this.parameterDesc = MonitorParamTypeDef.getData().getItems()
					.getItemDesc(getParameter());
		} else if (this.isTypeMeteorological()) {
			this.parameterDesc = WxParamDef.getData().getItems()
					.getItemDesc(getParameterMet());
		}
		return this.parameterDesc;
	}

}

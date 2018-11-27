package us.wy.state.deq.impact.database.dbObjects.monitoring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class MonitorGroup extends BaseDB {

	private String groupName;
	
	private Integer groupId;
	
	private String mgrpId;
	
	private String facilityId;
	
	private String facilityName;
	
	private String facilityType;
	
	private String facilityClass;
	
	private Integer fpId;
	
	private String cmpId;

	private String companyId;
	
	private String companyName;
	
	private String description;
	
	private boolean aqdOwned;
	
	private String contractor;
	
	private List<MonitorGroupNote> notes;

	private boolean facilityAssociatedWithOtherGroup;
	
	private Integer monitorReviewerId;
	
	public MonitorGroup() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MonitorGroup(MonitorGroup mg) {
		super();
		this.groupName = mg.groupName;
		this.groupId = mg.groupId;
		this.mgrpId = mg.mgrpId;
		this.facilityId = mg.facilityId;
		this.facilityName = mg.facilityName;
		this.facilityType = mg.facilityType;
		this.facilityClass = mg.facilityClass;
		this.fpId = mg.fpId;
		this.cmpId = mg.cmpId;
		this.companyId = mg.companyId;
		this.companyName = mg.companyName;
		this.description = mg.description;
		this.aqdOwned = mg.aqdOwned;
		this.notes = mg.notes;
		this.monitorReviewerId = mg.monitorReviewerId;
		this.contractor = mg.contractor;
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setCompanyId(rs.getString("company_id"));
			setCmpId(rs.getString("cmp_id"));
			setCompanyName(rs.getString("company_name"));
			setFacilityId(rs.getString("facility_id"));
			setFacilityName(rs.getString("facility_nm"));
			setFacilityClass(rs.getString("permit_classification_cd"));
			setFacilityType(rs.getString("facility_type_cd"));
			setFpId(rs.getInt("fp_id"));
			setMgrpId(rs.getString("mgrp_id"));
			setGroupId(rs.getInt("group_id"));
			setGroupName(rs.getString("group_name"));
			setDescription(rs.getString("description"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			setAqdOwned(AbstractDAO.translateIndicatorToBoolean(rs.getString("aqd_owned")));
			setMonitorReviewerId(AbstractDAO.getInteger(rs, "monitor_reviewer"));
			setContractor(rs.getString("contractor_cd"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}





	public Integer getMonitorReviewerId() {
		return monitorReviewerId;
	}

	public void setMonitorReviewerId(Integer monitorReviewerId) {
		this.monitorReviewerId = monitorReviewerId;
	}

	public boolean isFacilityAssociatedWithOtherGroup() {
		return facilityAssociatedWithOtherGroup;
	}

	public void setFacilityAssociatedWithOtherGroup(
			boolean facilityAssociatedWithOtherGroup) {
		this.facilityAssociatedWithOtherGroup = facilityAssociatedWithOtherGroup;
	}

	public String getContractor() {
		return contractor;
	}





	public void setContractor(String contractor) {
		this.contractor = contractor;
	}





	public Integer getFpId() {
		return fpId;
	}





	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}





	public boolean isAqdOwned() {
		return aqdOwned;
	}





	public void setAqdOwned(boolean aqdOwned) {
		this.aqdOwned = aqdOwned;
	}





	public String getFacilityType() {
		return facilityType;
	}





	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}





	public String getFacilityClass() {
		return facilityClass;
	}





	public void setFacilityClass(String facilityClass) {
		this.facilityClass = facilityClass;
	}





	public String getCmpId() {
		return cmpId;
	}





	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}





	public String getCompanyId() {
		return companyId;
	}




	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}




	public String getDescription() {
		return description;
	}




	public void setDescription(String description) {
		this.description = description;
	}




	public String getGroupName() {
		return groupName;
	}




	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}




	public Integer getGroupId() {
		return groupId;
	}




	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}




	public String getMgrpId() {
		return mgrpId;
	}




	public void setMgrpId(String mgrpId) {
		this.mgrpId = mgrpId;
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



	public String getCompanyName() {
		return companyName;
	}




	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}




	@Override
	public ValidationMessage[] validate() {
		this.validationMessages.clear();
		
		if(Utility.isNullOrEmpty(this.getGroupName())) {
			ValidationMessage valMsg = new ValidationMessage("groupName", "Set a group name.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("groupName", valMsg);
		}
		
		if(!this.isAqdOwned()) {
			if(Utility.isNullOrEmpty(this.getCmpId())) {
				ValidationMessage valMsg = new ValidationMessage("cmpId", "Set a company.", ValidationMessage.Severity.ERROR);
				this.validationMessages.put("cmpId", valMsg);
			}
		}
		
		if(Utility.isNullOrEmpty(this.getDescription())) {
			ValidationMessage valMsg = new ValidationMessage("description", "Set a description.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("description", valMsg);
		}
		
		if(null == this.getMonitorReviewerId()) {
			ValidationMessage valMsg = new ValidationMessage("monitorReviewerId", "Set a monitor reviewer.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("monitorReviewerId", valMsg);
		}
		
		if(isFacilityAssociatedWithOtherGroup()) {
			ValidationMessage valMsg = new ValidationMessage("facility", "Facility already has a monitor group.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("facility", valMsg);
		}
		
		
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	public final List<MonitorGroupNote> getNotes() {
		if (notes == null) {
			notes = new ArrayList<MonitorGroupNote>();
		}
		return notes;
	}

	public final void setNotes(List<MonitorGroupNote> notes) {
		this.notes = new ArrayList<MonitorGroupNote>();
		if (notes != null) {
			this.notes.addAll(notes);
		}
	}

	public final void addNote(MonitorGroupNote a) {
		if (notes == null) {
			notes = new ArrayList<MonitorGroupNote>();
		}
		if (a != null) {
			notes.add(a);
		}
	}
	
	// needed for jsp only so that the reviewer names in the
	// monitor group search results datagrid can be sorted
	// by name instead of id
	public final String getMonitorReviewerName() {
		String ret = null;
		if(null != this.monitorReviewerId) {
			ret = BasicUsersDef.getUserNm(this.monitorReviewerId);
		}
		return ret;
	}
}

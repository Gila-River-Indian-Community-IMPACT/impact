package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.def.PermitConditionCategoryDef;
import us.wy.state.deq.impact.def.PermitConditionStatusDef;

public class PermitCondition extends BaseDB {
	
	private static final long serialVersionUID = -5842946019808196484L;

	public static final String LIST_SEPARATOR = ", ";
	public static final String FACILITY_WIDE = "Facility Wide";

	private Integer permitConditionId;
	private String pcondId;
	private Integer permitId;
	private String permitConditionNumber;
	private String permitConditionStatusCd = PermitConditionStatusDef.ACTIVE;
	private List<String> permitConditionCategoryCds = new ArrayList<String>();
	private Integer lastUpdatedById;
	private Timestamp lastUpdatedDate;
	
	// If 'Y', condition applies to all EUs in the facility.
	private String facilityWide;
	
	private List<Integer> associatedCorrEuIds;
	private List<String> associatedFpEuEpaEmuIds;
	private List<ComplianceStatusEvent> complianceStatusEvents;
	// List of PermitConditions which were superseded by this PermitCondition.
	private List<PermitConditionSupersession> supersededByThis;
	// List of PermitConditions which superseded this PermitCondition.
	private List<PermitConditionSupersession> supersedesThis;
	
	private String conditionTextHTML;
	private String conditionTextPlain;

	public PermitCondition() {

		this.requiredField(lastUpdatedDate, "last_updated_dt");
		setDirty(false);

	}

	public PermitCondition(PermitCondition old) {

		super(old);

		setPermitConditionId(old.getPermitConditionId());
		setPcondId(old.getPcondId());
		setPermitId(old.getPermitId());
		setPermitConditionNumber(old.getPermitConditionNumber());
		setPermitConditionStatusCd(old.getPermitConditionStatusCd());
		setPermitConditionCategoryCds(old.getPermitConditionCategoryCds());
		// associated EUs
		setLastUpdatedById(old.getLastUpdatedById());
		setLastUpdatedDate(old.getLastUpdatedDate());
		setFacilityWide(old.getFacilityWide());
		setConditionTextHTML(old.getConditionTextHTML());
		setConditionTextPlain(old.getConditionTextPlain());
		setComplianceStatusEvents(old.getComplianceStatusEvents());
		setSupersededByThis(old.getSupersededByThis());
		setSupersedesThis(old.getSupersedesThis());
		setDirty(old.isDirty());
	}

	public void populate(ResultSet rs) throws SQLException {

		if(rs != null) {
			setPermitConditionId(AbstractDAO.getInteger(rs, "permit_condition_id"));
			setPcondId(rs.getString("pcond_id"));
			setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
			setPermitConditionNumber(rs.getString("permit_condition_number"));
			setPermitConditionStatusCd(rs.getString("condition_status_cd"));
			setLastUpdatedById(AbstractDAO.getInteger(rs, "last_updated_by_id"));
			setLastUpdatedDate(rs.getTimestamp("last_updated_dt"));
			setFacilityWide(rs.getString("facility_wide"));
			setConditionTextHTML(rs.getString("condition_text_html"));
			setConditionTextPlain(rs.getString("condition_text_plain"));
			setLastModified(AbstractDAO.getInteger(rs, "pc_lm"));
			setDirty(false);
		}

	}

	public final Integer getPermitConditionId() {
		return permitConditionId;
	}

	public final void setPermitConditionId(Integer permitConditionId) {
		this.permitConditionId = permitConditionId;
	}

	public String getPcondId() {
		return pcondId;
	}

	public void setPcondId(String pcondId) {
		this.pcondId = pcondId;
	}

	public final Integer getPermitId() {
		return permitId;
	}

	public final void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}

	public String getPermitConditionNumber() {
		return permitConditionNumber;
	}

	public void setPermitConditionNumber(String permitConditionNumber) {
		this.permitConditionNumber = permitConditionNumber;
	}
	
	public String getPermitConditionStatusCd() {
		return permitConditionStatusCd;
	}

	public void setPermitConditionStatusCd(String permitConditionStatusCd) {
		this.permitConditionStatusCd = permitConditionStatusCd;
	}

	public List<String> getPermitConditionCategoryCds() {
		if(null == permitConditionCategoryCds) {
			setPermitConditionCategoryCds(new ArrayList<String>());
		}
		return permitConditionCategoryCds;
	}

	public void setPermitConditionCategoryCds(List<String> permitConditionCategoryCds) {
		this.permitConditionCategoryCds = permitConditionCategoryCds;
	}

	// getter for the PermitConditions Grid.
	public String getCategoryCdsDescription() {
		String ret = null;
		if (null != this.permitConditionCategoryCds) {
			List<String> categoryDescList = new ArrayList<String>(this.permitConditionCategoryCds.size());

			for (String categoryCd : this.permitConditionCategoryCds) {
				categoryDescList.add(PermitConditionCategoryDef.getData().getItems().getItemDesc(categoryCd));
			}
			ret = StringUtils.join(categoryDescList.toArray(new String[0]), LIST_SEPARATOR);
		}
		return ret;

	}

	// associated EUs
	public Integer getLastUpdatedById() {
		return lastUpdatedById;
	}

	public void setLastUpdatedById(Integer lastUpdatedById) {
		this.lastUpdatedById = lastUpdatedById;
	}

	public final Timestamp getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public final void setLastUpdatedDate(Timestamp lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
		this.requiredField(lastUpdatedDate, "created_dt");
		setDirty(true);

	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((associatedCorrEuIds == null) ? 0 : associatedCorrEuIds.hashCode());
		result = prime * result + ((associatedFpEuEpaEmuIds == null) ? 0 : associatedFpEuEpaEmuIds.hashCode());
		result = prime * result + ((conditionTextHTML == null) ? 0 : conditionTextHTML.hashCode());
		result = prime * result + ((conditionTextPlain == null) ? 0 : conditionTextPlain.hashCode());
		result = prime * result + ((facilityWide == null) ? 0 : facilityWide.hashCode());
		result = prime * result + ((lastUpdatedById == null) ? 0 : lastUpdatedById.hashCode());
		result = prime * result + ((lastUpdatedDate == null) ? 0 : lastUpdatedDate.hashCode());
		result = prime * result + ((pcondId == null) ? 0 : pcondId.hashCode());
		result = prime * result + ((permitConditionCategoryCds == null) ? 0 : permitConditionCategoryCds.hashCode());
		result = prime * result + ((permitConditionId == null) ? 0 : permitConditionId.hashCode());
		result = prime * result + ((permitConditionNumber == null) ? 0 : permitConditionNumber.hashCode());
		result = prime * result + ((permitId == null) ? 0 : permitId.hashCode());
		result = prime * result + ((supersededByThis == null) ? 0 : supersededByThis.hashCode());
		result = prime * result + ((supersedesThis == null) ? 0 : supersedesThis.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PermitCondition other = (PermitCondition) obj;
		if (associatedCorrEuIds == null) {
			if (other.associatedCorrEuIds != null)
				return false;
		} else if (!associatedCorrEuIds.equals(other.associatedCorrEuIds))
			return false;
		if (associatedFpEuEpaEmuIds == null) {
			if (other.associatedFpEuEpaEmuIds != null)
				return false;
		} else if (!associatedFpEuEpaEmuIds.equals(other.associatedFpEuEpaEmuIds))
			return false;
		if (conditionTextHTML == null) {
			if (other.conditionTextHTML != null)
				return false;
		} else if (!conditionTextHTML.equals(other.conditionTextHTML))
			return false;
		if (conditionTextPlain == null) {
			if (other.conditionTextPlain != null)
				return false;
		} else if (!conditionTextPlain.equals(other.conditionTextPlain))
			return false;
		if (facilityWide == null) {
			if (other.facilityWide != null)
				return false;
		} else if (!facilityWide.equals(other.facilityWide))
			return false;
		if (lastUpdatedById == null) {
			if (other.lastUpdatedById != null)
				return false;
		} else if (!lastUpdatedById.equals(other.lastUpdatedById))
			return false;
		if (lastUpdatedDate == null) {
			if (other.lastUpdatedDate != null)
				return false;
		} else if (!lastUpdatedDate.equals(other.lastUpdatedDate))
			return false;
		if (pcondId == null) {
			if (other.pcondId != null)
				return false;
		} else if (!pcondId.equals(other.pcondId))
			return false;
		if (permitConditionCategoryCds == null) {
			if (other.permitConditionCategoryCds != null)
				return false;
		} else if (!permitConditionCategoryCds.equals(other.permitConditionCategoryCds))
			return false;
		if (permitConditionId == null) {
			if (other.permitConditionId != null)
				return false;
		} else if (!permitConditionId.equals(other.permitConditionId))
			return false;
		if (permitConditionNumber == null) {
			if (other.permitConditionNumber != null)
				return false;
		} else if (!permitConditionNumber.equals(other.permitConditionNumber))
			return false;
		if (permitId == null) {
			if (other.permitId != null)
				return false;
		} else if (!permitId.equals(other.permitId))
			return false;
		if (supersededByThis == null) {
			if (other.supersededByThis != null)
				return false;
		} else if (!supersededByThis.equals(other.supersededByThis))
			return false;
		if (supersedesThis == null) {
			if (other.supersedesThis != null)
				return false;
		} else if (!supersedesThis.equals(other.supersedesThis))
			return false;
		return true;
	}
	
	public List<Integer> getAssociatedCorrEuIds() {
		if (associatedCorrEuIds == null) {
			associatedCorrEuIds = new ArrayList<Integer>(0);
		}
		return associatedCorrEuIds;
	}

	public void setAssociatedCorrEuIds(List<Integer> assocFpEuIds) {
		this.associatedCorrEuIds = assocFpEuIds;
		if (this.associatedCorrEuIds == null) {
			this.associatedCorrEuIds = new ArrayList<Integer>(0);
		}
	}

	public List<String> getAssociatedFpEuEpaEmuIds() {
		if (associatedFpEuEpaEmuIds == null) {
			associatedFpEuEpaEmuIds = new ArrayList<String>(0);
		}
		return associatedFpEuEpaEmuIds;
	}

	public void setAssociatedFpEuEpaEmuIds(List<String> assocFpEuEpaEmuIds) {

		this.associatedFpEuEpaEmuIds = assocFpEuEpaEmuIds;

		if (this.associatedFpEuEpaEmuIds == null) {
			this.associatedFpEuEpaEmuIds = new ArrayList<String>(0);
		}

	}

	public String getFacilityWide() {
		return facilityWide;
	}

	public void setFacilityWide(String facWide) {
		this.facilityWide = facWide;
	}

	/**
	 * @return a comma separated list of associated objects i.e., eus and
	 * release points
	 * 
	 */
	public String getAssociatedObjects() {
		
		List<String> associatedObjects = new ArrayList<String>();

		if (!getAssociatedFpEuEpaEmuIds().isEmpty()) {
			associatedObjects.addAll(getAssociatedFpEuEpaEmuIds());
		}

		return StringUtils.join(associatedObjects.toArray(), LIST_SEPARATOR);
	}

	public String getAssociatedObjectsDisplay() {

		String ret = null;

		if (!Utility.isNullOrEmpty(this.getFacilityWide())) {

			if (this.getFacilityWide().equalsIgnoreCase("Y")) {
				ret = FACILITY_WIDE;
			} else {
				String associatedObjectsCSV = getAssociatedObjects();
				if(!Utility.isNullOrEmpty(associatedObjectsCSV)) {
					ret = associatedObjectsCSV;
				}
			}
		}

		return ret;
	}
	
	public List<ComplianceStatusEvent> getComplianceStatusEvents() {
		return complianceStatusEvents;
	}

	public void setComplianceStatusEvents(List<ComplianceStatusEvent> complianceStatusEvents) {
		this.complianceStatusEvents = complianceStatusEvents;
	}

	public Integer getComplianceStatusEventsCount() {
		return null != complianceStatusEvents ? complianceStatusEvents.size() : 0;
	}

	public String getConditionTextHTML() {
		return conditionTextHTML;
	}

	public void setConditionTextHTML(String conditionTextHTML) {
		this.conditionTextHTML = conditionTextHTML;
	}

	public String getConditionTextPlain() {
		return conditionTextPlain;
	}

	public void setConditionTextPlain(String conditionTextPlain) {
		this.conditionTextPlain = conditionTextPlain;
	}

	/**
	 * 
	 * @return a list of PermitConditions that were superseded by this PermitCondition
	 */
	public List<PermitConditionSupersession> getSupersededByThis() {

		if (supersededByThis == null) {
			supersededByThis = new ArrayList<PermitConditionSupersession>();
		}

		return supersededByThis;
	}

	public void setSupersededByThis(List<PermitConditionSupersession> supersededByThis) {
		this.supersededByThis = supersededByThis;
	}

	/**
	 * 
	 * @return a list of PermitConditions that superseded this PermitCondition
	 */
	public List<PermitConditionSupersession> getSupersedesThis() {

		if (supersedesThis == null) {
			supersedesThis = new ArrayList<PermitConditionSupersession>();
		}

		return supersedesThis;
	}

	public void setSupersedesThis(List<PermitConditionSupersession> supersedesThis) {
		this.supersedesThis = supersedesThis;
	}

	// Required fields in PermitConditionDetail pop-up.
	private void requiredFields() {
		requiredField(this.permitConditionNumber, "permitConditionNumber", "Condition Number", "permitConditionNumber");
		requiredField(this.permitConditionStatusCd, "permitConditionStatusCd", "Condition Status", "permitConditionStatusCd");
	}

	@Override
	public ValidationMessage[] validate() {
		this.validationMessages.clear();
		requiredFields();
		return new ArrayList<ValidationMessage>(this.validationMessages.values()).toArray(new ValidationMessage[0]);
	}

	// needed for jsp only so that the last updated names in the
	// datagrid can be sorted by name instead of id
	public final String getLastUpdatedByName() {
		String ret = null;
		if(null != this.lastUpdatedById) {
			ret = BasicUsersDef.getUserNm(this.lastUpdatedById);
		}
		return ret;
	}
	
	public boolean isConditionStatusActive() {
		return (null != this.permitConditionStatusCd
				&& this.permitConditionStatusCd.equals(PermitConditionStatusDef.ACTIVE)) ? true : false;
	}
}

package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.def.PermitConditionCategoryDef;

public class PermitConditionSearchLineItem extends BaseDB {

	private static final long serialVersionUID = 6825357564027814774L;

	private Integer permitConditionId;
	private String pcondId;
	private Integer permitId;
	private String permitNbr;
	private String legacyPermitNbr;
	private String permitTypeCd;
	private Timestamp finalIssuanceDate;
	private String permitConditionNumber;
	private Timestamp permitBasisDate;
	private String conditionTextPlain;
	private String facilityWide;
	private List<String> associatedFpEuEpaEmuIds;
	private List<String> permitConditionCategoryCds;
	private String permitConditionStatusCd;
	private String permitLevelStatusCd;
	private Integer lastUpdatedById;
	private Timestamp lastUpdatedDate;
	// List of PermitConditions which were superseded by this PermitCondition.
	private List<PermitConditionSupersession> supersededByThis;
	private List<ComplianceStatusEvent> complianceStatusEventList = new ArrayList<ComplianceStatusEvent>();
	
	public PermitConditionSearchLineItem() {
		super();
	}
	
	public PermitConditionSearchLineItem(PermitConditionSearchLineItem old) {
		super();
		if(null != old) {
			setPermitConditionId(old.getPermitConditionId());
			setPcondId(old.getPcondId());
			setPermitId(old.getPermitId());
			setPermitNbr(old.getPermitNbr());
			setLegacyPermitNbr(old.getLegacyPermitNbr());
			setPermitTypeCd(old.getPermitTypeCd());
			setFinalIssuanceDate(old.getFinalIssuanceDate());
			setPermitConditionNumber(old.getPermitConditionNumber());
			setPermitBasisDate(old.getPermitBasisDate());
			setConditionTextPlain(old.getConditionTextPlain());
			setFacilityWide(old.getFacilityWide());
			setAssociatedFpEuEpaEmuIds(old.getAssociatedFpEuEpaEmuIds());
			setPermitConditionCategoryCds(old.getPermitConditionCategoryCds());
			setPermitConditionStatusCd(old.getPermitConditionStatusCd());
			setPermitLevelStatusCd(old.getPermitLevelStatusCd());
			setSupersededByThis(old.getSupersededByThis());
			setLastUpdatedById(old.getLastUpdatedById());
			setLastUpdatedDate(old.getLastUpdatedDate());
		}
	}

	public Integer getPermitConditionId() {
		return permitConditionId;
	}

	public void setPermitConditionId(Integer permitConditionId) {
		this.permitConditionId = permitConditionId;
	}

	public String getPcondId() {
		return pcondId;
	}

	public void setPcondId(String pcondId) {
		this.pcondId = pcondId;
	}

	public Integer getPermitId() {
		return permitId;
	}

	public void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}

	public String getPermitNbr() {
		return permitNbr;
	}

	public void setPermitNbr(String permitNbr) {
		this.permitNbr = permitNbr;
	}
	
	public String getLegacyPermitNbr() {
		return legacyPermitNbr;
	}

	public void setLegacyPermitNbr(String legacyPermitNbr) {
		this.legacyPermitNbr = legacyPermitNbr;
	}

	public String getPermitTypeCd() {
		return permitTypeCd;
	}

	public void setPermitTypeCd(String permitTypeCd) {
		this.permitTypeCd = permitTypeCd;
	}

	public Timestamp getFinalIssuanceDate() {
		return finalIssuanceDate;
	}

	public void setFinalIssuanceDate(Timestamp finalIssuanceDate) {
		this.finalIssuanceDate = finalIssuanceDate;
	}

	public String getPermitConditionNumber() {
		return permitConditionNumber;
	}

	public void setPermitConditionNumber(String permitConditionNumber) {
		this.permitConditionNumber = permitConditionNumber;
	}

	public Timestamp getPermitBasisDate() {
		return permitBasisDate;
	}

	public void setPermitBasisDate(Timestamp permitBasisDate) {
		this.permitBasisDate = permitBasisDate;
	}

	public String getConditionTextPlain() {
		return conditionTextPlain;
	}

	public void setConditionTextPlain(String conditionTextPlain) {
		this.conditionTextPlain = conditionTextPlain;
	}

	public String getFacilityWide() {
		return facilityWide;
	}

	public void setFacilityWide(String facilityWide) {
		this.facilityWide = facilityWide;
	}

	public List<String> getAssociatedFpEuEpaEmuIds() {
		return associatedFpEuEpaEmuIds;
	}

	public void setAssociatedFpEuEpaEmuIds(List<String> associatedFpEuEpaEmuIds) {
		this.associatedFpEuEpaEmuIds = associatedFpEuEpaEmuIds;
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

	// getter for the PermitConditionsSearch Grid.
	public String getCategoryCdsDescription() {
		String ret = null;
		if (null != this.permitConditionCategoryCds) {
			List<String> categoryDescList = new ArrayList<String>(this.permitConditionCategoryCds.size());

			for (String categoryCd : this.permitConditionCategoryCds) {
				categoryDescList.add(PermitConditionCategoryDef.getData().getItems().getItemDesc(categoryCd));
			}
			ret = StringUtils.join(categoryDescList.toArray(new String[0]), PermitCondition.LIST_SEPARATOR);
		}
		return ret;

	}

	public String getPermitConditionStatusCd() {
		return permitConditionStatusCd;
	}

	public void setPermitConditionStatusCd(String permitConditionStatusCd) {
		this.permitConditionStatusCd = permitConditionStatusCd;
	}

	public String getPermitLevelStatusCd() {
		return permitLevelStatusCd;
	}

	public void setPermitLevelStatusCd(String permitLevelStatusCd) {
		this.permitLevelStatusCd = permitLevelStatusCd;
	}

	public Integer getLastUpdatedById() {
		return lastUpdatedById;
	}
	
	public void setLastUpdatedById(Integer lastUpdatedById) {
		this.lastUpdatedById = lastUpdatedById;
	}

	public Timestamp getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setPermitConditionId(AbstractDAO.getInteger(rs, "PERMIT_CONDITION_ID"));
			setPcondId(rs.getString("PCOND_ID"));
			setPermitId(AbstractDAO.getInteger(rs, "PERMIT_ID"));
			setPermitNbr(rs.getString("PERMIT_NBR"));
			setLegacyPermitNbr(rs.getString("LEGACY_PERMIT_NBR"));
			setPermitTypeCd(rs.getString("PERMIT_TYPE_CD"));
			setFinalIssuanceDate(rs.getTimestamp("ISSUANCE_DATE"));
			setPermitConditionNumber(rs.getString("PERMIT_CONDITION_NUMBER"));
			setPermitBasisDate(rs.getTimestamp("PERMIT_BASIS_DATE"));
			setConditionTextPlain(rs.getString("CONDITION_TEXT_PLAIN"));
			setFacilityWide(rs.getString("FACILITY_WIDE"));
			setPermitConditionStatusCd(rs.getString("CONDITION_STATUS_CD"));
			setPermitLevelStatusCd(rs.getString("PERMIT_LEVEL_STATUS_CD"));
			setLastUpdatedById(AbstractDAO.getInteger(rs, "LAST_UPDATED_BY_ID"));
			setLastUpdatedDate(rs.getTimestamp("LAST_UPDATED_DT"));
		}

	}
	
	public String getAssociatedEUsValue() {
		String ret = null;
		if(!Utility.isNullOrEmpty(this.facilityWide) && this.facilityWide.equalsIgnoreCase("Y")) {
			ret = PermitCondition.FACILITY_WIDE;
		} else if (null != this.associatedFpEuEpaEmuIds && this.associatedFpEuEpaEmuIds.size() > 0) {
			ret = StringUtils.join(this.associatedFpEuEpaEmuIds.toArray(new String[0]), PermitCondition.LIST_SEPARATOR);
		}
		return ret;
	}

	public Integer getAssociatedComplianceStatusEventsCount() {
		return getComplianceStatusEventList().size();
	}

	public List<PermitConditionSupersession> getSupersededByThis() {
		return supersededByThis;
	}

	public void setSupersededByThis(List<PermitConditionSupersession> supersededByThis) {
		this.supersededByThis = supersededByThis;
	}

	public List<ComplianceStatusEvent> getComplianceStatusEventList() {
		if(null == complianceStatusEventList) {
			return new ArrayList<ComplianceStatusEvent>();
		}
		
		return complianceStatusEventList;
	}

	public void setComplianceStatusEventList(List<ComplianceStatusEvent> complianceStatusEventList) {
		this.complianceStatusEventList = complianceStatusEventList;
	}
	
	

}

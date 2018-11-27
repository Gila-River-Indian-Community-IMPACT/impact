package us.wy.state.deq.impact.database.dbObjects.continuousMonitoring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class ContinuousMonitor extends BaseDB implements
		Comparable<ContinuousMonitor> {

	public static final String LIST_SEPARATOR = ",";
	
	private Integer createdById;
	private Timestamp createdDt;

	private Integer continuousMonitorId;
	private Integer fpId;
	private String monId;
	private String monitorDetails;
	private String addlInfo;
	private String facilityId;
	private String facilityNm;
	private String companyName;
	private String companyId;
	private String countyCd;
	private String countyNm;
	private String districtCd;
	private Integer creatorId;
	private Integer corrMonitorId;

	private Timestamp addDate;

	private String currentManufacturer;
	private String currentModelNumber;
	private String activeLimits;
	
	private Timestamp currentQAQCAcceptedDate;
	private String currentSerialNumber;

	// private List<ContinuousMonitorAttachment> attachments;
	private List<ContinuousMonitorNote> notes;

	private List<ContinuousMonitorEqt> _continuousMonitorEqtList;
	
	private List<FacilityCemComLimit> _facilityCemComLimitList;
	
	private List<Integer> associatedFpEuIds;
	private List<Integer> associatedFpEgressPointIds;
	
	// used for search only
	private List<String> associatedFpEuEpaEmuIds;
	private List<String> associatedFpEgressPointRPIds;

	public ContinuousMonitor() {
		super();
	}

	public ContinuousMonitor(ContinuousMonitor old) {
		super(old);
		if (old != null) {
			setCreatedById(old.getCreatedById());
			setCreatedDt(old.getCreatedDt());
			setContinuousMonitorId(old.getContinuousMonitorId());
			setFpId(old.getFpId());
			setMonId(old.getMonId());
			setMonitorDetails(old.getMonitorDetails());
			setAddlInfo(old.getAddlInfo());
			setFacilityId(old.getFacilityId());
			setFacilityNm(old.getFacilityNm());
			setCompanyName(old.getCompanyName());
			setCompanyId(old.getCompanyId());
			setCountyCd(old.getCountyCd());
			setCountyNm(old.getCountyNm());
			setDistrictCd(old.getDistrictCd());
			setCreatorId(old.getCreatorId());
			setCorrMonitorId(old.getCorrMonitorId());
			
			setAddDate(old.getAddDate());
			setCurrentManufacturer(old.getCurrentManufacturer());
			setCurrentModelNumber(old.getCurrentModelNumber());
			setActiveLimits(old.getActiveLimits());
			setCurrentQAQCAcceptedDate(old.getCurrentQAQCAcceptedDate());
			setCurrentSerialNumber(old.getCurrentSerialNumber());
			
			setNotes(old.getNotes());
			setContinuousMonitorEqtList(old.getContinuousMonitorEqtList());
			setFacilityCemComLimitList(old.getFacilityCemComLimitList());
			
			setAssociatedFpEuIds(old.getAssociatedFpEuIds());
			setAssociatedFpEgressPointIds(old.getAssociatedFpEgressPointIds());
			
			setAssociatedFpEuEpaEmuIds(old.getAssociatedFpEuEpaEmuIds());
			setAssociatedFpEgressPointRPIds(old.getAssociatedFpEgressPointRPIds());
		}
	}

	public void populate(ResultSet rs) throws SQLException {
		try {
			setContinuousMonitorId(AbstractDAO.getInteger(rs, "monitor_id"));
			setMonId(rs.getString("mon_id"));
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setMonitorDetails(rs.getString("monitor_desc"));
			setAddDate(rs.getTimestamp("add_date"));
			setAddlInfo(rs.getString("addl_info"));
			setCreatorId(AbstractDAO.getInteger(rs, "added_by"));
			setLastModified(AbstractDAO.getInteger(rs, "cm_lm"));
			setCorrMonitorId(AbstractDAO.getInteger(rs, "corr_monitor_id"));
			setCurrentQAQCAcceptedDate(rs.getTimestamp("qaqc_accepted_date"));
			setCurrentSerialNumber(rs.getString("serial_number"));
			try {
				setFacilityId(rs.getString("facility_id"));
				setFacilityNm(rs.getString("facility_nm"));
				setCompanyName(rs.getString("company_nm"));
				setCompanyId(rs.getString("cmp_id"));
				setDistrictCd(rs.getString("district_cd"));
				setCountyCd(rs.getString("county_cd"));
				setCountyNm(rs.getString("county_nm"));
			} catch (SQLException e) {
				logger.debug("caught/ignored several invalid columns");
			}

			try {
				setCurrentManufacturer(rs.getString("manufacturer_name"));
				setCurrentModelNumber(rs.getString("model_number"));
			} catch (SQLException e) {
				logger.debug("caught/ignored manufacturer_name is not valid");
			}
			setActiveLimits(""); // to be populated by SQLDAO method

		} catch (SQLException sqle) {
			logger.error(sqle.getMessage());
		} finally {

		}
	}

	@Override
	public int compareTo(ContinuousMonitor match) {
		int ret = -1;

		if (match != null) {
			if (match.getMonId() != null && this.getMonId() != null) {
				return this.getMonId().compareTo(match.getMonId());
			}
		}

		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((_continuousMonitorEqtList == null) ? 0
						: _continuousMonitorEqtList.hashCode());
		result = prime
				* result
				+ ((_facilityCemComLimitList == null) ? 0
						: _facilityCemComLimitList.hashCode());
		result = prime * result
				+ ((activeLimits == null) ? 0 : activeLimits.hashCode());
		result = prime * result + ((addDate == null) ? 0 : addDate.hashCode());
		result = prime * result
				+ ((addlInfo == null) ? 0 : addlInfo.hashCode());
		result = prime
				* result
				+ ((associatedFpEgressPointIds == null) ? 0
						: associatedFpEgressPointIds.hashCode());
		result = prime
				* result
				+ ((associatedFpEgressPointRPIds == null) ? 0
						: associatedFpEgressPointRPIds.hashCode());
		result = prime
				* result
				+ ((associatedFpEuEpaEmuIds == null) ? 0
						: associatedFpEuEpaEmuIds.hashCode());
		result = prime
				* result
				+ ((associatedFpEuIds == null) ? 0 : associatedFpEuIds
						.hashCode());
		result = prime * result
				+ ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result
				+ ((companyName == null) ? 0 : companyName.hashCode());
		result = prime
				* result
				+ ((continuousMonitorId == null) ? 0 : continuousMonitorId
						.hashCode());
		result = prime * result
				+ ((corrMonitorId == null) ? 0 : corrMonitorId.hashCode());
		result = prime * result
				+ ((countyCd == null) ? 0 : countyCd.hashCode());
		result = prime * result
				+ ((countyNm == null) ? 0 : countyNm.hashCode());
		result = prime * result
				+ ((createdById == null) ? 0 : createdById.hashCode());
		result = prime * result
				+ ((createdDt == null) ? 0 : createdDt.hashCode());
		result = prime * result
				+ ((creatorId == null) ? 0 : creatorId.hashCode());
		result = prime
				* result
				+ ((currentManufacturer == null) ? 0 : currentManufacturer
						.hashCode());
		result = prime
				* result
				+ ((currentModelNumber == null) ? 0 : currentModelNumber
						.hashCode());
		result = prime
				* result
				+ ((currentQAQCAcceptedDate == null) ? 0
						: currentQAQCAcceptedDate.hashCode());
		result = prime
				* result
				+ ((currentSerialNumber == null) ? 0 : currentSerialNumber
						.hashCode());
		result = prime * result
				+ ((districtCd == null) ? 0 : districtCd.hashCode());
		result = prime * result
				+ ((facilityId == null) ? 0 : facilityId.hashCode());
		result = prime * result
				+ ((facilityNm == null) ? 0 : facilityNm.hashCode());
		result = prime * result + ((fpId == null) ? 0 : fpId.hashCode());
		result = prime * result + ((monId == null) ? 0 : monId.hashCode());
		result = prime * result
				+ ((monitorDetails == null) ? 0 : monitorDetails.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ContinuousMonitor)) {
			return false;
		}
		ContinuousMonitor other = (ContinuousMonitor) obj;
		if (_continuousMonitorEqtList == null) {
			if (other._continuousMonitorEqtList != null) {
				return false;
			}
		} else if (!_continuousMonitorEqtList
				.equals(other._continuousMonitorEqtList)) {
			return false;
		}
		if (_facilityCemComLimitList == null) {
			if (other._facilityCemComLimitList != null) {
				return false;
			}
		} else if (!_facilityCemComLimitList
				.equals(other._facilityCemComLimitList)) {
			return false;
		}
		if (activeLimits == null) {
			if (other.activeLimits != null) {
				return false;
			}
		} else if (!activeLimits.equals(other.activeLimits)) {
			return false;
		}
		if (addDate == null) {
			if (other.addDate != null) {
				return false;
			}
		} else if (!addDate.equals(other.addDate)) {
			return false;
		}
		if (addlInfo == null) {
			if (other.addlInfo != null) {
				return false;
			}
		} else if (!addlInfo.equals(other.addlInfo)) {
			return false;
		}
		if (associatedFpEgressPointIds == null) {
			if (other.associatedFpEgressPointIds != null) {
				return false;
			}
		} else if (!associatedFpEgressPointIds
				.equals(other.associatedFpEgressPointIds)) {
			return false;
		}
		if (associatedFpEgressPointRPIds == null) {
			if (other.associatedFpEgressPointRPIds != null) {
				return false;
			}
		} else if (!associatedFpEgressPointRPIds
				.equals(other.associatedFpEgressPointRPIds)) {
			return false;
		}
		if (associatedFpEuEpaEmuIds == null) {
			if (other.associatedFpEuEpaEmuIds != null) {
				return false;
			}
		} else if (!associatedFpEuEpaEmuIds
				.equals(other.associatedFpEuEpaEmuIds)) {
			return false;
		}
		if (associatedFpEuIds == null) {
			if (other.associatedFpEuIds != null) {
				return false;
			}
		} else if (!associatedFpEuIds.equals(other.associatedFpEuIds)) {
			return false;
		}
		if (companyId == null) {
			if (other.companyId != null) {
				return false;
			}
		} else if (!companyId.equals(other.companyId)) {
			return false;
		}
		if (companyName == null) {
			if (other.companyName != null) {
				return false;
			}
		} else if (!companyName.equals(other.companyName)) {
			return false;
		}
		if (continuousMonitorId == null) {
			if (other.continuousMonitorId != null) {
				return false;
			}
		} else if (!continuousMonitorId.equals(other.continuousMonitorId)) {
			return false;
		}
		if (corrMonitorId == null) {
			if (other.corrMonitorId != null) {
				return false;
			}
		} else if (!corrMonitorId.equals(other.corrMonitorId)) {
			return false;
		}
		if (countyCd == null) {
			if (other.countyCd != null) {
				return false;
			}
		} else if (!countyCd.equals(other.countyCd)) {
			return false;
		}
		if (countyNm == null) {
			if (other.countyNm != null) {
				return false;
			}
		} else if (!countyNm.equals(other.countyNm)) {
			return false;
		}
		if (createdById == null) {
			if (other.createdById != null) {
				return false;
			}
		} else if (!createdById.equals(other.createdById)) {
			return false;
		}
		if (createdDt == null) {
			if (other.createdDt != null) {
				return false;
			}
		} else if (!createdDt.equals(other.createdDt)) {
			return false;
		}
		if (creatorId == null) {
			if (other.creatorId != null) {
				return false;
			}
		} else if (!creatorId.equals(other.creatorId)) {
			return false;
		}
		if (currentManufacturer == null) {
			if (other.currentManufacturer != null) {
				return false;
			}
		} else if (!currentManufacturer.equals(other.currentManufacturer)) {
			return false;
		}
		if (currentModelNumber == null) {
			if (other.currentModelNumber != null) {
				return false;
			}
		} else if (!currentModelNumber.equals(other.currentModelNumber)) {
			return false;
		}
		if (currentQAQCAcceptedDate == null) {
			if (other.currentQAQCAcceptedDate != null) {
				return false;
			}
		} else if (!currentQAQCAcceptedDate
				.equals(other.currentQAQCAcceptedDate)) {
			return false;
		}
		if (currentSerialNumber == null) {
			if (other.currentSerialNumber != null) {
				return false;
			}
		} else if (!currentSerialNumber.equals(other.currentSerialNumber)) {
			return false;
		}
		if (districtCd == null) {
			if (other.districtCd != null) {
				return false;
			}
		} else if (!districtCd.equals(other.districtCd)) {
			return false;
		}
		if (facilityId == null) {
			if (other.facilityId != null) {
				return false;
			}
		} else if (!facilityId.equals(other.facilityId)) {
			return false;
		}
		if (facilityNm == null) {
			if (other.facilityNm != null) {
				return false;
			}
		} else if (!facilityNm.equals(other.facilityNm)) {
			return false;
		}
		if (fpId == null) {
			if (other.fpId != null) {
				return false;
			}
		} else if (!fpId.equals(other.fpId)) {
			return false;
		}
		if (monId == null) {
			if (other.monId != null) {
				return false;
			}
		} else if (!monId.equals(other.monId)) {
			return false;
		}
		if (monitorDetails == null) {
			if (other.monitorDetails != null) {
				return false;
			}
		} else if (!monitorDetails.equals(other.monitorDetails)) {
			return false;
		}
		if (notes == null) {
			if (other.notes != null) {
				return false;
			}
		} else if (!notes.equals(other.notes)) {
			return false;
		}
		return true;
	}

	public final ValidationMessage[] validate() {

		validationMessages.clear();

		if (this.getMonitorDetails() == null
				|| Utility.isNullOrEmpty(this.getMonitorDetails().trim())) {
			ValidationMessage valMsg = new ValidationMessage("monitorDetails",
					"Monitor Details must be entered.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("monitorDetails", valMsg);
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public Integer getContinuousMonitorId() {
		return continuousMonitorId;
	}

	public String getMonId() {
		return monId;
	}

	public void setMonId(String monId) {
		this.monId = monId;
	}

	public String getMonitorDetails() {
		return monitorDetails;
	}

	public void setMonitorDetails(String monitorDetails) {
		this.monitorDetails = monitorDetails;
	}

	public String getAddlInfo() {
		return addlInfo;
	}

	public void setAddlInfo(String addlInfo) {
		this.addlInfo = addlInfo;
	}

	public void setContinuousMonitorId(Integer continuousMonitorId) {
		this.continuousMonitorId = continuousMonitorId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public Timestamp getAddDate() {
		return addDate;
	}

	public void setAddDate(Timestamp addDate) {
		this.addDate = addDate;
	}

	public final Integer getCreatedById() {
		return createdById;
	}

	public final void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}

	public final Timestamp getCreatedDt() {
		return createdDt;
	}

	public final void setCreatedDt(Timestamp createdDt) {
		this.createdDt = createdDt;
	}
	
	public Integer getCorrMonitorId() {
		return corrMonitorId;
	}

	public void setCorrMonitorId(Integer corrMonitorId) {
		this.corrMonitorId = corrMonitorId;
	}

	public final String getFacilityNm() {
		return facilityNm;
	}

	public final void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCountyCd() {
		return countyCd;
	}

	public void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}

	public String getCountyNm() {
		return countyNm;
	}

	public void setCountyNm(String countyNm) {
		this.countyNm = countyNm;
	}

	public String getDistrictCd() {
		return districtCd;
	}

	public void setDistrictCd(String districtCd) {
		this.districtCd = districtCd;
	}

	public String getCurrentManufacturer() {
		return currentManufacturer;
	}

	public void setCurrentManufacturer(String currentManufacturer) {
		this.currentManufacturer = currentManufacturer;
	}

	public String getCurrentModelNumber() {
		return currentModelNumber;
	}

	public void setCurrentModelNumber(String currentModelNumber) {
		this.currentModelNumber = currentModelNumber;
	}

	public String getActiveLimits() {
		return activeLimits;
	}

	public void setActiveLimits(String activeLimits) {
		this.activeLimits = activeLimits;
	}
	
	public Timestamp getCurrentQAQCAcceptedDate() {
		return currentQAQCAcceptedDate;
	}

	public void setCurrentQAQCAcceptedDate(Timestamp currentQAQCAcceptedDate) {
		this.currentQAQCAcceptedDate = currentQAQCAcceptedDate;
	}

	public String getCurrentSerialNumber() {
		return currentSerialNumber;
	}

	public void setCurrentSerialNumber(String currentSerialNumber) {
		this.currentSerialNumber = currentSerialNumber;
	}

	// TODO will probably want to get rid of this once testing is done
	public void copy(ContinuousMonitor action) {

		this.createdById = action.createdById;
		this.createdDt = action.createdDt;

	}

	/*
	 * public final List<ContinuousMonitorAttachment> getAttachments() { if
	 * (attachments == null) { attachments = new
	 * ArrayList<ContinuousMonitorAttachment>(); } return attachments; }
	 * 
	 * public final void setAttachments(List<ContinuousMonitorAttachment>
	 * attachments) { this.attachments = new
	 * ArrayList<ContinuousMonitorAttachment>(); if (attachments != null) {
	 * this.attachments.addAll(attachments); } }
	 * 
	 * public final void addAttachment(ContinuousMonitorAttachment a) { if
	 * (attachments == null) { attachments = new
	 * ArrayList<ContinuousMonitorAttachment>(); } if (a != null) {
	 * attachments.add(a); } }
	 * 
	 * public final boolean hasAttachments() { boolean rtn = false; if
	 * (attachments != null && attachments.size() > 0) { rtn = true; } return
	 * rtn; }
	 */

	public final List<ContinuousMonitorNote> getNotes() {
		if (notes == null) {
			notes = new ArrayList<ContinuousMonitorNote>();
		}
		return notes;
	}

	public final void setNotes(List<ContinuousMonitorNote> notes) {
		this.notes = new ArrayList<ContinuousMonitorNote>();
		if (notes != null) {
			this.notes.addAll(notes);
		}
	}

	public final void addNote(ContinuousMonitorNote a) {
		if (notes == null) {
			notes = new ArrayList<ContinuousMonitorNote>();
		}
		if (a != null) {
			notes.add(a);
		}
	}

	public final List<ContinuousMonitorEqt> getContinuousMonitorEqtList() {

		if (_continuousMonitorEqtList == null) {
			_continuousMonitorEqtList = new ArrayList<ContinuousMonitorEqt>(0);
		}
		return _continuousMonitorEqtList;
	}

	public final void setContinuousMonitorEqtList(
			List<ContinuousMonitorEqt> cmeList) {
		_continuousMonitorEqtList = cmeList;
		if (_continuousMonitorEqtList == null) {
			_continuousMonitorEqtList = new ArrayList<ContinuousMonitorEqt>(0);
		}
	}
	
	public final List<FacilityCemComLimit> getFacilityCemComLimitList() {
		if (_facilityCemComLimitList == null) {
			_facilityCemComLimitList = new ArrayList<FacilityCemComLimit>(0);
		}
		return _facilityCemComLimitList;
	}

	public final void setFacilityCemComLimitList(
			List<FacilityCemComLimit> fcclList) {
		_facilityCemComLimitList = fcclList;
		if (_facilityCemComLimitList == null) {
			_facilityCemComLimitList = new ArrayList<FacilityCemComLimit>(0);
		}
	}

	public List<Integer> getAssociatedFpEuIds() {
		if (associatedFpEuIds == null) {
			associatedFpEuIds = new ArrayList<Integer>(0);
		}
		return associatedFpEuIds;
	}

	public void setAssociatedFpEuIds(List<Integer> assocFpEuIds) {
		this.associatedFpEuIds = assocFpEuIds;
		if (this.associatedFpEuIds == null) {
			this.associatedFpEuIds = new ArrayList<Integer>(0);
		}
	}

	public List<Integer> getAssociatedFpEgressPointIds() {
		if (associatedFpEgressPointIds == null) {
			associatedFpEgressPointIds = new ArrayList<Integer>(0);
		}
		return associatedFpEgressPointIds;
	}

	public void setAssociatedFpEgressPointIds(
			List<Integer> assocFpEgressPointIds) {
		this.associatedFpEgressPointIds = assocFpEgressPointIds;
		if (this.associatedFpEgressPointIds == null) {
			this.associatedFpEgressPointIds = new ArrayList<Integer>(0);
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

	public List<String> getAssociatedFpEgressPointRPIds() {
		if (associatedFpEgressPointRPIds == null) {
			associatedFpEgressPointRPIds = new ArrayList<String>(0);
		}
		return associatedFpEgressPointRPIds;
	}

	public void setAssociatedFpEgressPointRPIds(
			List<String> assocFpEgressPointRPIds) {
		this.associatedFpEgressPointRPIds = assocFpEgressPointRPIds;
		if (this.associatedFpEgressPointRPIds == null) {
			this.associatedFpEgressPointRPIds = new ArrayList<String>(0);
		}
	}

	/**
	 * Returns a comma separated list of associated objects i.e., eus and release points
	 * 
	 */
	public String getAssociatedObjects() {
		List<String> associatedObjects = new ArrayList<String>();
		
		if(!getAssociatedFpEuEpaEmuIds().isEmpty()) {
			associatedObjects.addAll(getAssociatedFpEuEpaEmuIds());
		}
		
		if(!getAssociatedFpEgressPointRPIds().isEmpty()) {
			associatedObjects.addAll(getAssociatedFpEgressPointRPIds());
		}
		
		return StringUtils.join(associatedObjects.toArray(), LIST_SEPARATOR);
	}

	public void copyContinuousMonitorData(ContinuousMonitor dapcMon) {
		setCreatedById(dapcMon.getCreatedById());
		setCreatedDt(dapcMon.getCreatedDt());
		setContinuousMonitorId(dapcMon.getContinuousMonitorId());
		setFpId(dapcMon.getFpId());
		setMonId(dapcMon.getMonId());
		setMonitorDetails(dapcMon.getMonitorDetails());
		setAddlInfo(dapcMon.getAddlInfo());
		setFacilityId(dapcMon.getFacilityId());
		setFacilityNm(dapcMon.getFacilityNm());
		setCompanyName(dapcMon.getCompanyName());
		setCompanyId(dapcMon.getCompanyId());
		setCountyCd(dapcMon.getCountyCd());
		setCountyNm(dapcMon.getCountyNm());
		setDistrictCd(dapcMon.getDistrictCd());
		setCreatorId(dapcMon.getCreatorId());
		setCorrMonitorId(dapcMon.getCorrMonitorId());
		
		setAddDate(dapcMon.getAddDate());
		setCurrentManufacturer(dapcMon.getCurrentManufacturer());
		setCurrentModelNumber(dapcMon.getCurrentModelNumber());
		setActiveLimits(dapcMon.getActiveLimits());
		setCurrentQAQCAcceptedDate(dapcMon.getCurrentQAQCAcceptedDate());
		setCurrentSerialNumber(dapcMon.getCurrentSerialNumber());
		
		setNotes(dapcMon.getNotes());
		setContinuousMonitorEqtList(dapcMon.getContinuousMonitorEqtList());
		setFacilityCemComLimitList(dapcMon.getFacilityCemComLimitList());
		
		setAssociatedFpEuIds(dapcMon.getAssociatedFpEuIds());
		setAssociatedFpEgressPointIds(dapcMon.getAssociatedFpEgressPointIds());
		
		setAssociatedFpEuEpaEmuIds(dapcMon.getAssociatedFpEuEpaEmuIds());
		setAssociatedFpEgressPointRPIds(dapcMon.getAssociatedFpEgressPointRPIds());
	}
}

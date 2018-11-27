package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PermitDocIssuanceStageDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PublicNoticeTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class Permit extends BaseDB {

	private static final long serialVersionUID = -526220944892369657L;
	
	private Integer _permitID;
	private String _permitType;
	private String _permitGlobalStatusCD = PermitGlobalStatusDef.NONE;
	private String _legacyPermitNumber;
	private String _permitNumber;
	private String _description;
	private Timestamp _effectiveDate;
	private Timestamp _modEffectiveDate;
	private String _eracCaseNumber;
	private Timestamp _expirationDate;
	private String _facilityId;
	private boolean _variance;
	private boolean _mact;
	private List<String> _mactSubpartCDs;
	private boolean _neshaps;
	private List<String> _neshapsSubpartCDs;
	private boolean _nsps;
	private List<String> _nspsSubpartCDs;
	private boolean _part61NESHAP;
	private List<String> _part61NESHAPSubpartCDs;
	private boolean _part63NESHAP;
	private List<String> _part63NESHAPSubpartCDs;

	private boolean majorGHG;
	private boolean _issueDraft;
	private boolean _dapcHearingReqd;
	private boolean _rushFlag;
	private List<String> _permitReasonCDs;
	private boolean _earlyRenewalFlag;
	private String _primaryReasonCD;
	private List<PermitEUGroup> _euGroups;
	private List<PermitDocument> _documents;
	private List<PermitDocument> _fixedPotentialDocuments;
	private PermitDocument currentIssuanceDoc;
	private PermitDocument _currentCCDoc;
	private PermitDocument _faxCoverDoc;
	private PermitDocument invoiceDoc;
	private Timestamp issueDate;
	private Note[] _dapcComments;
	private List<Application> _applications;
	private LinkedHashMap<String, PermitIssuance> permitIssuances = new LinkedHashMap<String, PermitIssuance>(
			4);
	private List<Contact> _contacts;
	private String facilityNm;
	private String applicationNumbers;
	private int fpId;
	private Invoice _invoice;
	private String originalPermitNo;
	private List<PermitCC> _CCList;
	private String publicNoticeType;
	private String publicNoticeText;
	private boolean legacyPermit;
	transient private PermitNote noteErrMsg = null;
	private String cmpId;
	private String companyName;
	private String publicNoticeNewspaperCd;
	private boolean updateFacilityDtlPTETableComments;
	private boolean receiptLetterSent;
	private String receivedComments;
	
	// This is only used in permit search. yehp
	private Timestamp earliestSubmittedDate;

	// This is only used when permit is result of EU permit history search
	private PermitEU permitEU;
	
	// This is only used when NSR permit is result of Permit Search
	private Timestamp permitSentOutDate;
	
	// This is only used when TV or NSR permit is result of Permit Search
	private Timestamp recissionDate;
	
	private String actionType;
	
	private String truncatedDescription;
	
	//This is only used when NSR permit is result of Permit Search
	private Double initialInvoiceAmount;
	private Double finalInvoiceAmount;
	private Double totalCharges;
	private Double totalPayments;
	private Double currentBalance;
	
	private Timestamp lastInvoiceRefDate;
	
	// Permit Conditions
	private List<PermitCondition> _permitConditionList;
	
	private Timestamp permitBasisDt; // needed for permit search only
	
	private String permitLevelStatusCd;
	
	private List<PermitDocument> _commentsDocuments;

	/**
     * 
     */
	public Permit() {
		super();
		setPermitType("Not Defined");
		setDirty(false);
	}

	/**
	 * @param old
	 */
	public Permit(Permit old) {

		super(old);
		if (old != null) {
			setPermitID(old.getPermitID());
			setPermitType(old.getPermitType());
			setPermitGlobalStatusCD(old.getPermitGlobalStatusCD());
			setLegacyPermitNumber(old.getLegacyPermitNumber());
			setPermitNumber(old.getPermitNumber());
			setDescription(old.getDescription());
			setEffectiveDate(old.getEffectiveDate());
			setModEffectiveDate(old.getModEffectiveDate());
			setEracCaseNumber(old.getEracCaseNumber());
			setExpirationDate(old.getExpirationDate());
			setFacilityId(old.getFacilityId());
			setVariance(old.isVariance());
			setMact(old.isMact());
			setNeshaps(old.isNeshaps());
			setNsps(old.isNsps());
			setPart61NESHAP(old.isPart61NESHAP());
			setPart63NESHAP(old.isPart63NESHAP());
			setMajorGHG(old.isMajorGHG());
			setIssueDraft(old.isIssueDraft());
			setDapcHearingReqd(old.isDapcHearingReqd());
			setLegacyPermit(old.isLegacyPermit());
			setPrimaryReasonCD(old.getPrimaryReasonCD());
			setPermitReasonCDs(old.getPermitReasonCDs());
			setEuGroups(old.getEuGroups());
			setDocuments(old.getDocuments());
			setDapcComments(old.getDapcComments());

			setApplications(old.getApplications());
			setContacts(old.getContacts());
			setFacilityNm(old.getFacilityNm());
			setFpId(old.getFpId());
			setPermitIssuances(old.getPermitIssuances());
			setPermitCCList(old.getPermitCCList());

			setPublicNoticeType(old.getPublicNoticeType());
			setPublicNoticeText(old.getPublicNoticeText());
			setEarliestSubmittedDate(old.getEarliestSubmittedDate());

			setPublicNoticeNewspaperCd(old.getPublicNoticeNewspaperCd());
			setUpdateFacilityDtlPTETableComments(old
					.isUpdateFacilityDtlPTETableComments());
			setReceiptLetterSent(old.isReceiptLetterSent());
			setReceivedComments(old.getReceivedComments());
			setCompanyName(old.getCompanyName());
			setLastModified(old.getLastModified());
			setPermitConditionList(old.getPermitConditionList());
			setPermitLevelStatusCd(old.getPermitLevelStatusCd());
			setDirty(old.isDirty());
		}
	}

	public void populate(ResultSet rs) {

		try {
			setPermitType(rs.getString("permit_type_cd"));
			setPermitID(AbstractDAO.getInteger(rs, "permit_id"));
			setLegacyPermitNumber(rs.getString("legacy_permit_nbr"));
			setPermitNumber(rs.getString("permit_nbr"));
			setPermitGlobalStatusCD(rs.getString("permit_global_status_cd"));
			setFacilityId(rs.getString("facility_id"));
			setDescription(rs.getString("description"));
			setEffectiveDate(rs.getTimestamp("effective_date"));
			setModEffectiveDate(rs.getTimestamp("mod_effective_date"));
			setEracCaseNumber(rs.getString("erac_case_nbr"));
			setExpirationDate(rs.getTimestamp("expiration_date"));
			setVariance(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("variance")));
			setMact(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("mact_flag")));
			setNeshaps(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("neshaps_flag")));
			setNsps(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("nsps_flag")));
			setPart61NESHAP(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("part61NESHAP_flag")));
			setPart63NESHAP(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("part63NESHAP_flag")));
			setIssueDraft(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("issue_draft_flag")));
			setDapcHearingReqd(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("dapc_hearing_reqd_flag")));
/*			setRushFlag(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("rush_flag")));*/
			setLegacyPermit(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("legacy_flag")));
			// set the type to CW so we can have the text set into this permit.
			setPublicNoticeType(PublicNoticeTypeDef.CUSTOMIZED_WORDING);
			setPublicNoticeText(rs.getString("public_notice_text"));
			setPublicNoticeType(rs.getString("public_notice_type"));

			setLastModified(AbstractDAO.getInteger(rs, "pp_lm"));
			setDirty(false);

			setMajorGHG(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("major_ghg_flag")));
			setEarlyRenewalFlag(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("issue_date_chg")));

			setPublicNoticeNewspaperCd(rs
					.getString("public_notice_newspaper_cd"));
			setUpdateFacilityDtlPTETableComments(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("update_facility_detail_pte_table_comments_flag")));
			setReceiptLetterSent(AbstractDAO.translateIndicatorToBoolean(rs.getString("receipt_letter_sent")));
			setReceivedComments(rs.getString("received_comments"));
			setCompanyName(rs.getString("company_nm"));
			setPrimaryReasonCD(rs.getString("primary_reason_cd"));
			setPermitLevelStatusCd(rs.getString("permit_level_status_cd"));
		} catch (SQLException sqle) {
			logger.error("Required field error: " + sqle.getMessage(), sqle);
		} finally {
			newObject = false;
		}
		try {
			setFacilityNm(rs.getString("facility_nm"));
		} catch (SQLException sqle) {
			logger.debug("Optional field error: " + sqle.getMessage());
		}
	}

	public String getActionType() {
		return actionType;
	}
	
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	public boolean isWaiver() {
		return false;
	}
	
	public final Integer getPermitID() {
		return _permitID;
	}

	public final void setPermitID(Integer permitID) {
		_permitID = permitID;
	}

	public final String getPermitType() {
		return _permitType;
	}

	public final String getApplicationPermitType() {
		String applicationType = "";
		if (_permitType.equalsIgnoreCase(PermitTypeDef.NSR))
			applicationType = ApplicationTypeDef.PTIO_APPLICATION;
		else if (_permitType.equalsIgnoreCase(PermitTypeDef.TV_PTO))
			applicationType = ApplicationTypeDef.TITLE_V_APPLICATION;

		return applicationType;
	}

	public final String getPermitTypeDsc() {
		String permitTypeDsc = PermitTypeDef.getData().getItems()
				.getItemDesc(_permitType);
		return permitTypeDsc;
	}

	public final void setPermitType(String permitType) {

		_permitType = permitType;
		if (PermitTypeDef.isValid(_permitType)) {
			validationMessages.remove("permitType");
		} else {
			validationMessages.put("permitType", new ValidationMessage(
					"Permit Type", "Illegal value for permit type.",
					ValidationMessage.Severity.ERROR));
		}
		setDirty(true);
	}

	public final String getPermitGlobalStatusCD() {
		return _permitGlobalStatusCD;
	}

	public final void setPermitGlobalStatusCD(String permitGlobalStatusCD) {

		_permitGlobalStatusCD = permitGlobalStatusCD;
		if (PermitGlobalStatusDef.isValid(permitGlobalStatusCD)) {
			validationMessages.remove("permitGlobalStatusCD");
		} else {
			ValidationMessage msg = new ValidationMessage(
					"Permit Global Status Code",
					"Illegal value for global status code.",
					ValidationMessage.Severity.ERROR);
			validationMessages.put("permitGlobalStatusCD", msg);
		}
		setDirty(true);
	}

	public final String getLegacyPermitNumber() {
		return _legacyPermitNumber;
	}

	public final void setLegacyPermitNumber(String legacyPermitNumber) {
		checkDirty("lpnm", getPermitNumber(), getLegacyPermitNumber(), legacyPermitNumber);
		_legacyPermitNumber = legacyPermitNumber;
		setDirty(true);
	}

	public final String getPermitNumber() {
		return _permitNumber;
	}

	public final void setPermitNumber(String permitNumber) {
		checkDirty("pnum", getPermitNumber(), getPermitNumber(), permitNumber);
		_permitNumber = permitNumber;
		setDirty(true);
	}

	public final String getDescription() {
		return _description;
	}
	
	public final void setDescription(String description) {
		_description = description;
        if(_description != null && _description.length() > 500) {
        	setTruncatedDescription(_description.substring(0, 500) + "...");
        } else {
        	setTruncatedDescription(_description);
        }
        setDirty(true);
    }

	public final Timestamp getEffectiveDate() {
		return _effectiveDate;
	}

	public final void setEffectiveDate(Timestamp effectiveDate) {
		checkDirty("pdef", getPermitNumber(), getEffectiveDate(), effectiveDate);
		_effectiveDate = effectiveDate;
		setDirty(true);
	}

	public final Timestamp getModEffectiveDate() {
		return _modEffectiveDate;
	}

	public final void setModEffectiveDate(Timestamp modEffectiveDate) {
		_modEffectiveDate = modEffectiveDate;
	}

	public final String getEracCaseNumber() {
		return _eracCaseNumber;
	}

	public final void setEracCaseNumber(String eracCaseNumber) {
		_eracCaseNumber = eracCaseNumber;
	}

	public final Timestamp getExpirationDate() {
		return _expirationDate;
	}

	public final void setExpirationDate(Timestamp expirationDate) {
		_expirationDate = expirationDate;
		setDirty(true);
	}

	public final String getFacilityId() {
		return _facilityId;
	}

	public final void setFacilityId(String facilityId) {
		_facilityId = facilityId;
		setDirty(true);
	}
	

	public final boolean isVariance() {
		return _variance;
	}

	public final void setVariance(boolean variance) {
		_variance = variance;
		setDirty(true);
	}

	public final boolean isMact() {
		return _mact;
	}

	public final void setMact(boolean mact) {

		_mact = mact;
		if (_mact && getMactSubpartCDs().isEmpty()) {
			ValidationMessage msg = new ValidationMessage("MACT",
					"MACT is set, but there are no MACT subpart codes.",
					ValidationMessage.Severity.ERROR);
			validationMessages.put("mact", msg);
		} else {
			validationMessages.remove("mact");
		}
		setDirty(true);
	}

	public final List<String> getMactSubpartCDs() {
		if (_mactSubpartCDs == null) {
			_mactSubpartCDs = new ArrayList<String>(0);
		}
		return _mactSubpartCDs;
	}

	public final void setMactSubpartCDs(List<String> mactSubpartCDs) {

		_mactSubpartCDs = mactSubpartCDs;
		if (mactSubpartCDs == null) {
			_mactSubpartCDs = new ArrayList<String>(0);
			setMact(false);
		} else {
			setMact(true);
		}
	}

	public final boolean isNeshaps() {
		return _neshaps;
	}

	public final void setNeshaps(boolean neshaps) {

		_neshaps = neshaps;
		if (_neshaps && getNeshapsSubpartCDs().isEmpty()) {
			ValidationMessage msg = new ValidationMessage("NESHAPS",
					"NESHAPS is set, but there are no NESHAPS subpart codes.",
					ValidationMessage.Severity.ERROR);
			validationMessages.put("neshaps", msg);
		} else {
			validationMessages.remove("neshaps");
		}
		setDirty(true);
	}

	public final List<String> getNeshapsSubpartCDs() {
		if (_neshapsSubpartCDs == null) {
			_neshapsSubpartCDs = new ArrayList<String>(0);
		}
		return _neshapsSubpartCDs;
	}

	public final void setNeshapsSubpartCDs(List<String> neshapsSubpartCDs) {

		_neshapsSubpartCDs = neshapsSubpartCDs;
		if (neshapsSubpartCDs == null) {
			_neshapsSubpartCDs = new ArrayList<String>(0);
			setNeshaps(false);
		} else {
			setNeshaps(true);
		}
	}

	public final boolean isNsps() {
		return _nsps;
	}

	public final void setNsps(boolean nsps) {

		_nsps = nsps;
		if (_nsps && getNspsSubpartCDs().isEmpty()) {
			ValidationMessage msg = new ValidationMessage("NSPS",
					"NSPS is set, but there are no NSPS subpart codes.",
					ValidationMessage.Severity.ERROR);
			validationMessages.put("nsps", msg);
		} else {
			validationMessages.remove("nsps");
		}
		setDirty(true);
	}

	public final List<String> getNspsSubpartCDs() {
		if (_nspsSubpartCDs == null) {
			_nspsSubpartCDs = new ArrayList<String>(0);
		}
		return _nspsSubpartCDs;
	}

	public final void setNspsSubpartCDs(List<String> nspsSubpartCDs) {

		_nspsSubpartCDs = nspsSubpartCDs;
		if (_nspsSubpartCDs == null) {
			_nspsSubpartCDs = new ArrayList<String>(0);
			setNsps(false);
		} else {
			setNsps(true);
		}
		setDirty(true);
	}

	public final boolean isMajorGHG() {
		return majorGHG;
	}

	public final void setMajorGHG(boolean majorGHG) {
		this.majorGHG = majorGHG;
	}

	public final boolean isIssueDraft() {
		return _issueDraft;
	}

	public final void setIssueDraft(boolean issueDraft) {
		_issueDraft = issueDraft;
	}

	public final boolean isDapcHearingReqd() {
		return _dapcHearingReqd;
	}

	public final void setDapcHearingReqd(boolean dapcHearingReqd) {
		checkDirty("phrd", getPermitNumber(), new Boolean(isDapcHearingReqd()),
				new Boolean(dapcHearingReqd));
		_dapcHearingReqd = dapcHearingReqd;
		setDirty(true);
	}

	public final boolean isRushFlag() {
		return _rushFlag;
	}

	public final void setRushFlag(boolean rushFlag) {
		_rushFlag = rushFlag;
		setDirty(true);
	}

	public final List<String> getPermitReasonCDs() {
		if (_permitReasonCDs == null) {
			_permitReasonCDs = new ArrayList<String>(1);
			_permitReasonCDs.add(PermitReasonsDef.NOT_ASSIGNED);
		}
		return _permitReasonCDs;
	}

	public final boolean isRenewal() {
		boolean isR = false;
		for (String s : getPermitReasonCDs()) {
			if (PermitReasonsDef.RENEWAL.equals(s)) {
				isR = true;
				break;
			}
		}
		return isR;
	}

	public final String getPermitReasons() {
		StringBuffer reasons = new StringBuffer();
		for (int i = 0; i < getPermitReasonCDs().size(); i++) {
			reasons.append(PermitReasonsDef.getData().getItems()
					.getItemDesc(getPermitReasonCDs().get(i)));
			if (i < getPermitReasonCDs().size() - 1) {
				reasons.append(", ");
			}
		}
		return reasons.toString();
	}

	public final void setPermitReasonCDs(List<String> permitReasonCDs) {

		_permitReasonCDs = permitReasonCDs;
		if (_permitReasonCDs == null) {
			_permitReasonCDs = new ArrayList<String>(1);
			_permitReasonCDs.add(PermitReasonsDef.NOT_ASSIGNED);
			validationMessages.put("permitReasonCds", new ValidationMessage(
					"Permit Reason Codes", "Missing value.",
					ValidationMessage.Severity.ERROR));
		} else if (_permitType != null && PermitTypeDef.isValid(_permitType)) {
			StringBuffer badCodes = new StringBuffer();
			Iterator<String> it = getPermitReasonCDs().iterator();
			while (it.hasNext()) {
				String description = PermitReasonsDef.getInvalidDescription(
						it.next(), _permitType);
				if (description != null) {
					badCodes.append(description);
					badCodes.append(" ");
				}
			}
			if (badCodes.length() > 0) {
				badCodes.insert(0,
						"The following reason codes are not valid for this permit type: ");
				validationMessages.put(
						"permitReasonCds",
						new ValidationMessage("Permit Reason Codes", badCodes
								.toString(), ValidationMessage.Severity.ERROR));
			}
		} else {
			validationMessages.remove("permitReasonCds");
		}

		// Reset primary reason to the first one from selected reasons order by the PT_REASON_DEF.LINE_ORDER column.
		String oldPrimaryReason = _primaryReasonCD;
		
		if (getPermitReasonCDs().indexOf(_primaryReasonCD) < 0) {
			_primaryReasonCD = PermitReasonsDef.NOT_ASSIGNED;
		}
		for (String reasonCD : getPermitReasonCDs()) {
			if (PermitReasonsDef.checkPrimary(_primaryReasonCD, reasonCD)) {
				_primaryReasonCD = reasonCD;
			}
		}

		checkDirty("pres", getPermitNumber(), oldPrimaryReason,
				getPrimaryReasonCD());
		setDirty(true);
	}

	public String getPrimaryReasonCD() {
		return _primaryReasonCD;
	}

	public void setPrimaryReasonCD(String primaryReasonCD) {
		this._primaryReasonCD = primaryReasonCD;
	}
	
	public final List<PermitEUGroup> getEuGroups() {
		if (_euGroups == null) {
			_euGroups = new ArrayList<PermitEUGroup>(0);
		}
		return _euGroups;
	}

	/**
	 * get all EUs in all EU groups.
	 * 
	 * @return
	 */
	public final List<PermitEU> getEus() {

		List<PermitEU> ret = new ArrayList<PermitEU>();

		for (PermitEUGroup peg : getEuGroups()) {
			for (PermitEU pe : peg.getPermitEUs()) {
				ret.add(pe);
			}
		}

		return ret;
	}

	public final void setEuGroups(List<PermitEUGroup> euGroups) {

		_euGroups = euGroups;
		if (_euGroups == null) {
			_euGroups = new ArrayList<PermitEUGroup>(0);
		}
		if (getPermitNumber() != null) {
			for (PermitEUGroup group : _euGroups) {
				List<PermitEU> eus = group.getPermitEUs();
				for (PermitEU eu : eus) {
					if (eu.getPermitNumber() == null
							|| eu.getPermitNumber().length() < 1)
						eu.setPermitNumber(getPermitNumber());
				}
			}
		}
		setDirty(true);
	}

	public final void addEuGroup(PermitEUGroup euGroup) {

		if (_euGroups == null) {
			_euGroups = new ArrayList<PermitEUGroup>(1);
		}
		if (getPermitNumber() != null) {
			List<PermitEU> eus = euGroup.getPermitEUs();
			for (PermitEU eu : eus) {
				if (eu.getPermitNumber() == null
						|| eu.getPermitNumber().length() < 1)
					eu.setPermitNumber(getPermitNumber());
			}
		}
		_euGroups.add(euGroup);
		setDirty(true);
	}

	public final List<PermitDocument> getDocuments() {
		if (_documents == null) {
			_documents = new ArrayList<PermitDocument>(0);
		}
		return _documents;
	}

	public final void setDocuments(List<PermitDocument> documents) {

		_documents = documents;
		if (documents == null) {
			_documents = new ArrayList<PermitDocument>(0);
		}
		setDirty(true);
	}

	public final void addDocument(PermitDocument permitDoc) {

		if (_documents == null) {
			_documents = new ArrayList<PermitDocument>(1);
		}
		_documents.add(permitDoc);
		setDirty(true);
	}

	public final Note[] getDapcComments() {
		return _dapcComments;
	}

	public final void setDapcComments(Note[] dapcComments) {
		_dapcComments = dapcComments;
		setDirty(true);
	}

	public final List<Application> getApplications() {
		if (_applications == null) {
			_applications = new ArrayList<Application>(0);
		}
		return _applications;
	}

	public final void setApplications(List<Application> applications) {

		_applications = applications;
		if (_applications == null) {
			_applications = new ArrayList<Application>(0);
		}

		StringBuffer sb = new StringBuffer();
		boolean begin = true;
		for (Application app : _applications) {
			if (begin) {
				begin = false;
			} else {
				sb.append(", ");
			}
			sb.append(app.getApplicationNumber());
		}
		applicationNumbers = sb.toString();
		setDirty(true);
	}

	/**
	 * This method not add eus in app into permit
	 * 
	 * @param permitApp
	 */
	public final void addApplication(Application permitApp) {

		if (_applications == null) {
			_applications = new ArrayList<Application>(1);
		}
		if (permitApp != null) {
			_applications.add(permitApp);
		}
		if (applicationNumbers == null || applicationNumbers.length() < 1) {
			applicationNumbers = permitApp.getApplicationNumber();
		} else {
			applicationNumbers = applicationNumbers + ", "
					+ permitApp.getApplicationNumber();
		}
		setDirty(true);
	}

	public void removeApplication(Application permitApp) {

		if (permitApp != null) {
			getApplications().remove(permitApp);
			PermitEUGroup individualGroup = getIndividualEuGroup();
			for (ApplicationEU aeu : permitApp.getEus()) {
				for (PermitEU peu : individualGroup.getPermitEUs()) {
					if (peu.getFpEU().equals(aeu.getFpEU())) {
						individualGroup.getPermitEUs().remove(peu);
						break;
					}
				}
			}
		}
		setDirty(true);
	}

	/**
	 * This method added eus in app into permit
	 * 
	 * @param permitApp
	 */
	public final void addNewApplication(Application permitApp)
			throws DAOException {

		if (_applications == null) {
			_applications = new ArrayList<Application>(1);
		}
		if (permitApp != null) {
			_applications.add(permitApp);

			// 3/14/07 permit meeting update
			// Create individual EU group.
			PermitEUGroup individualGroup = getIndividualEuGroup();

			if (individualGroup == null) {
				individualGroup = new PermitEUGroup();
				individualGroup.setIndividualEUGroup();
				individualGroup.setPermitID(getPermitID());
				individualGroup.setPermitEUs(new ArrayList<PermitEU>());
				addEuGroup(individualGroup);
			}

			boolean isTVApp = false;
			if (permitApp instanceof TVApplication)
				isTVApp = true;
			else if (permitApp instanceof RPCRequest
					&& PermitTypeDef.TV_PTO.equals(_permitType))
				isTVApp = true;

			// IMPACT does not automatically create a group for Insignificant EUs
			// Fix for bug 186
			//PermitEUGroup insignificantGroup = getInsignificantEuGroup();
			//if (isTVApp && insignificantGroup == null) {
			//	insignificantGroup = new PermitEUGroup();
			//	insignificantGroup.setInsignificantEuGroup();
			//	insignificantGroup.setPermitID(getPermitID());
			//	insignificantGroup.setPermitEUs(new ArrayList<PermitEU>());
			//	addEuGroup(insignificantGroup);
			//}

			// Add all eus in app into permit if the eu is not already in.
			for (ApplicationEU aeu : permitApp.getIncludedEus()) {
				PermitEU te = new PermitEU();
				te.setPermitStatusCd(PermitStatusDef.NONE);
				te.setFpEU(aeu.getFpEU());
				if (!containsEU(te.getFpEU().getCorrEpaEmuId())) {
					// IMPACT does not support the TVClassification attribute
					//if (isTVApp
					//		&& aeu.getFpEU().getTvClassCd() != null
					//		&& aeu.getFpEU().getTvClassCd()
					//				.equals(TVClassification.INSIG)) {
					//	insignificantGroup.addPermitEU(te);
					//} else {
						individualGroup.addPermitEU(te);
					//}
				}
			}
			String failMsg = "Failed to find Superceeded Permit corresponding to application "
					+ permitApp.getApplicationNumber();
			// If TV Mod App then include the EUs that were included in the
			// Active Permit to be superceeded.
			if (this.getClass().equals(TVPermit.class)
					&& PermitReasonsDef.isModReason(_primaryReasonCD)) {
				// IMPACT does not automatically create a group for Insignificant EUs
				//if (insignificantGroup == null) {
					// should not have been null. Create group anyway but log an
					// error for the record
				//	logger.error(
				//			"InsignificantGroup should not have been null for application "
				//					+ permitApp.getApplicationNumber()
				//					+ " and permit " + _permitNumber
				//					+ ".  Create group and continue. ",
				//			new Exception());
				//	insignificantGroup = new PermitEUGroup();
				//	insignificantGroup.setInsignificantEuGroup();
				//	insignificantGroup.setPermitID(getPermitID());
				//	insignificantGroup.setPermitEUs(new ArrayList<PermitEU>());
				//	addEuGroup(insignificantGroup);
				//}
				try {
					PermitService pBO = ServiceFactory.getInstance()
							.getPermitService();
					SimpleIdDef[] defs = pBO
							.retrieveSupersedableTVPermits(permitApp
									.getFacility().getFacilityId());
					if (defs.length == 0) {
						// generate permit note
						noteErrMsg = new PermitNote();
						noteErrMsg.setDateEntered(new Timestamp(System
								.currentTimeMillis()));
						noteErrMsg.setNoteTypeCd(NoteType.DAPC);
						noteErrMsg.setUserId(CommonConst.ADMIN_USER_ID);
						String noteTxt = "There is no active Title V permit to retrieve a list of EUs from. Only EUs in the application are included in this permit.";
						noteErrMsg.setNoteTxt(noteTxt);
					} else if (defs.length > 1) {
						// generate perrmit note
						noteErrMsg = new PermitNote();
						noteErrMsg.setDateEntered(new Timestamp(System
								.currentTimeMillis()));
						noteErrMsg.setNoteTypeCd(NoteType.DAPC);
						noteErrMsg.setUserId(CommonConst.ADMIN_USER_ID);
						String noteTxt = "More than one active TV permit--should not be the case--only emission units in application are included.";
						noteErrMsg.setNoteTxt(noteTxt);
					} else {
						// found one permit; work with it
						PermitInfo supSededPermit = pBO.retrievePermit(defs[0]
								.getId());
						if (supSededPermit == null) {
							String s = "Failed to find permit id "
									+ defs[0].getId();
							DAOException e = new DAOException(s);
							logger.error(s, e);
							throw e;
						}
						List<PermitEUGroup> euGs = supSededPermit.getPermit()
								.getEuGroups();
						for (PermitEUGroup pEuG : euGs) {
							for (PermitEU pEu : pEuG.getPermitEUs()) {
								if (PermitStatusDef.ACTIVE.equals(pEu
										.getPermitStatusCd())) {
									if (!containsEU(pEu.getCorrEpaEMUID())) {
										pEu.setPermitStatusCd(PermitStatusDef.NONE);
										pEu.setPermitEUID(null); // need new
																	// database
																	// object.
										// IMPACT does not support the TVClassification attribute
										//if (pEu.getFpEU().getTvClassCd() != null
										//		&& pEu.getFpEU()
										//				.getTvClassCd()
										//				.equals(TVClassification.INSIG)) {
										//	insignificantGroup.addPermitEU(pEu);
										//} else {
											individualGroup.addPermitEU(pEu);
										//}
									}
								}
							}
						}
					}
				} catch (ServiceFactoryException sfe) {
					logger.error(failMsg, sfe);
					throw new DAOException(failMsg, sfe);
				} catch (RemoteException re) {
					logger.error(failMsg, re);
					throw new DAOException(failMsg, re);
				}
			}

			if (applicationNumbers == null || applicationNumbers.length() < 1) {
				applicationNumbers = permitApp.getApplicationNumber();
			} else {
				applicationNumbers = applicationNumbers + ", "
						+ permitApp.getApplicationNumber();
			}
			setDirty(true);
		}
	}

	private boolean containsEU(Integer corrEpaEmuId) {

		for (PermitEUGroup g : getEuGroups()) {
			for (PermitEU pe : g.getPermitEUs()) {
				if (pe.getFpEU().getCorrEpaEmuId().equals(corrEpaEmuId)) {
					return true;
				}
			}
		}
		return false;
	}

	public final List<Contact> getContacts() {
		if (_contacts == null) {
			_contacts = new ArrayList<Contact>(0);
		}
		return _contacts;
	}

	public final void setContacts(List<Contact> contacts) {

		if (contacts == null) {
			_contacts = new ArrayList<Contact>(0);
		}
		_contacts = contacts;
		setDirty(true);
	}

	public final void addContact(Contact contact) {

		if (_contacts == null) {
			_contacts = new ArrayList<Contact>(1);
		}
		_contacts.add(contact);
		setDirty(true);
	}

	public final String getFacilityNm() {
		return facilityNm;
	}

	public final void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
		setDirty(true);
	}

	public final LinkedHashMap<String, PermitIssuance> getPermitIssuances() {
		return permitIssuances;
	}

	public final void setPermitIssuances(
			LinkedHashMap<String, PermitIssuance> permitIssuances) {
		this.permitIssuances = permitIssuances;
		for (PermitIssuance pi : permitIssuances.values()) {
			pi.setPermitNumber(getPermitNumber());
		}
		setDirty(true);
	}

	public final PermitIssuance getDraftIssuance() {
		return getPermitIssuances(PermitIssuanceTypeDef.Draft);
	}

	public final PermitIssuance getFinalIssuance() {
		return getPermitIssuances(PermitIssuanceTypeDef.Final);
	}

	public final PermitIssuance getDeniedIssuance() {
		return getPermitIssuances(PermitIssuanceTypeDef.DENIED);
	}

	protected PermitIssuance getPermitIssuances(String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi == null) {
			int permitIdInt = 0;
			Integer permitId = getPermitID();
			if (permitId == null) {
				logger.error("Attempting to get permit Issuances for null permit id");
			} else {
				permitIdInt = permitId;
			}
			pi = new PermitIssuance(permitIdInt, getPermitNumber(), type,
					IssuanceStatusDef.NOT_READY);
			permitIssuances.put(type, pi);
		}
		return pi;
	}

	public final String getDraftIssuanceStatusCd() {
		return getIssuanceStatusCd(PermitIssuanceTypeDef.Draft);
	}

	public final String getFinalIssuanceStatusCd() {
		return getIssuanceStatusCd(PermitIssuanceTypeDef.Final);
	}

	public final String getDeniedIssuanceStatusCd() {
		return getIssuanceStatusCd(PermitIssuanceTypeDef.DENIED);
	}

	/**
	 * @param type
	 * @return
	 * 
	 */
	protected String getIssuanceStatusCd(String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			return pi.getIssuanceStatusCd();
		}
		return null;
	}

	public final Timestamp getDraftIssueDate() {
		return getIssueDate(PermitIssuanceTypeDef.Draft);
	}

	public final Timestamp getFinalIssueDate() {
		return getIssueDate(PermitIssuanceTypeDef.Final);
	}

	public final Timestamp getDeniedIssueDate() {
		return getIssueDate(PermitIssuanceTypeDef.DENIED);
	}

	public Timestamp getPpIssueDate() {
		return null;
	}

	public Timestamp getPppIssueDate() {
		return null;
	}
	
	public String getPpIssuanceStatusCd() {
        return null;
    }
	
	public final Timestamp getPublicNoticePublishDate() {
		return getPublicNoticePublishDate(PermitIssuanceTypeDef.Draft);
	}

	/**
	 * @param type
	 * @return
	 * 
	 */
	protected Timestamp getIssueDate(String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			return pi.getIssuanceDate();
		}
		return null;
	}
	
	protected Timestamp getPublicNoticePublishDate(String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			return pi.getPublicNoticePublishDate();
		}
		return null;
	}

	public final void setDraftIssuanceStatusCd(String issuanceStatusCd) {
		setIssuanceStatusCd(issuanceStatusCd, PermitIssuanceTypeDef.Draft);
		setDirty(true);
	}

	public final void setFinalIssuanceStatusCd(String issuanceStatusCd) {
		setIssuanceStatusCd(issuanceStatusCd, PermitIssuanceTypeDef.Final);
		setDirty(true);
	}

	public final void setDeniedIssuanceStatusCd(String issuanceStatusCd) {
		setIssuanceStatusCd(issuanceStatusCd, PermitIssuanceTypeDef.DENIED);
		setDirty(true);
	}

	/**
	 * @param issuanceStatusCd
	 * @param draft
	 * 
	 */
	protected void setIssuanceStatusCd(String issuanceStatusCd, String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null)
			pi.setIssuanceStatusCd(issuanceStatusCd);
		else {
			pi = new PermitIssuance(getPermitID(), getPermitNumber(), type,
					issuanceStatusCd);
			permitIssuances.put(type, pi);
		}
		setDirty(true);
	}

	public final void setDraftIssueDate(Timestamp issuanceDate) {
		setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.Draft);
		setDirty(true);
	}

	public final void setFinalIssueDate(Timestamp issuanceDate) {
		setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.Final);
		setDirty(true);
	}

	public final void setDeniedIssueDate(Timestamp issuanceDate) {
		setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.DENIED);
		setDirty(true);
	}

	/**
     * 
     * 
     */
	protected void setIssuanceDate(Timestamp issuanceDate, String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			pi.setIssuanceDate(issuanceDate);
		} else {
			pi = new PermitIssuance(getPermitID(), getPermitNumber(), type,
					IssuanceStatusDef.NOT_READY);
			pi.setIssuanceDate(issuanceDate);
			permitIssuances.put(type, pi);
		}
		setDirty(true);
	}
	
	public final void setPublicNoticePublishDate(Timestamp publishDate) {
		setPublicNoticeDate(publishDate, PermitIssuanceTypeDef.Draft);
		setDirty(true);
	}

	/**
     * 
     * 
     */
	protected void setPublicNoticeDate(Timestamp publishDate, String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			pi.setPublicNoticePublishDate(publishDate);
		} else {
			pi = new PermitIssuance(getPermitID(), getPermitNumber(), type,
					IssuanceStatusDef.NOT_READY);
			pi.setPublicNoticePublishDate(publishDate);
			permitIssuances.put(type, pi);
		}
		setDirty(true);
	}

	public final PermitDocument getIssuanceDoc(String issuanceType) {

		PermitDocument ret = null;
		for (PermitDocument tpd : getDocuments()) {
			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
					PermitDocTypeDef.ISSUANCE_DOCUMENT)
					&& tpd.getIssuanceStageFlag() != null
					&& tpd.getIssuanceStageFlag().equalsIgnoreCase(
							PermitDocIssuanceStageDef.getStage(issuanceType))) {
				ret = tpd;
				break;
			}
		}
		return ret;
	}

	public final PermitDocument getDraftIssuanceDoc() {

		PermitDocument ret = new PermitDocument();
		String docType = new String();
		if (_permitType.equalsIgnoreCase(PermitTypeDef.NSR)) {
			docType = PermitDocTypeDef.ANALYSIS_DOCUMENT;
		}
		else if (_permitType.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
			docType = PermitDocTypeDef.DRAFT_TV_PERMIT_DOCUMENT;
		}
		for (PermitDocument tpd : getDocuments()) {
			
			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
					docType)
					//&& tpd.getIssuanceStageFlag() != null
					//&& tpd.getIssuanceStageFlag().equalsIgnoreCase(
					//		PermitDocIssuanceStageDef
					//				.getStage(PermitIssuanceTypeDef.Draft))
									) {

				ret = tpd;
				break;
			}
		}
		return ret;
	}

	public final PermitDocument getPpIssuanceDoc() {

		PermitDocument ret = new PermitDocument();
		for (PermitDocument tpd : getDocuments()) {
			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
					PermitDocTypeDef.PROPOSED_TV_PERMIT_DOCUMENT)
					//&& tpd.getIssuanceStageFlag() != null
					//&& tpd.getIssuanceStageFlag().equalsIgnoreCase(
					//		PermitDocIssuanceStageDef
					//				.getStage(PermitIssuanceTypeDef.PP))
									) {

				ret = tpd;
				break;
			}
		}
		return ret;
	}

	/*
	 * public final PermitDocument getPppIssuanceDoc() {
	 * 
	 * PermitDocument ret = new PermitDocument(); for (PermitDocument tpd :
	 * getDocuments()) { if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
	 * PermitDocTypeDef.ISSUANCE_DOCUMENT) && tpd.getIssuanceStageFlag() != null
	 * && tpd.getIssuanceStageFlag().equalsIgnoreCase( PermitDocIssuanceStageDef
	 * .getStage(PermitIssuanceTypeDef.PPP))) {
	 * 
	 * ret = tpd; break; } } return ret;
	 */
	public final PermitDocument getFinalIssuanceDoc() {

		PermitDocument ret = null; // new PermitDocument();
		String docType = new String();
		if (_permitType.equalsIgnoreCase(PermitTypeDef.NSR)) {
			docType = PermitDocTypeDef.NSR_FINAL_PERMIT_WAIVER_PACKAGE;
		}
		else if (_permitType.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
			docType = PermitDocTypeDef.FINAL_TV_PERMIT_DOCUMENT;
		}
		for (PermitDocument tpd : getDocuments()) {
			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
					docType)
					//&& tpd.getIssuanceStageFlag() != null
					//&& tpd.getIssuanceStageFlag().equalsIgnoreCase(
					//		PermitDocIssuanceStageDef
					//				.getStage(PermitIssuanceTypeDef.Final))
									) {

				ret = tpd;
				break;
			}
		}
		return ret;
	}

	/**
	 * @param permitApps
	 * 
	 */
	public final void setApplicationNumbers(String[] permitApps) {
		StringBuffer sb = new StringBuffer();
		boolean begin = true;
		for (String s : permitApps) {
			if (begin) {
				begin = false;
			} else {
				sb.append(", ");
			}
			sb.append(s);
		}
		applicationNumbers = sb.toString();
		setDirty(true);
	}

	public final String getApplicationNumbers() {
		return applicationNumbers;
	}

	public final int getFpId() {
		return fpId;
	}

	/**
	 * @param fpId
	 * 
	 */
	public final void setFpId(int fpId) {
		this.fpId = fpId;
		setDirty(true);
	}

	public Invoice getInvoice() {
		return _invoice;
	}

	public void setInvoice(Invoice invoice) {

		_invoice = invoice;
		if (_invoice != null) {
			invoiceDoc = invoice.getPermitInvDocument();
		}
		setDirty(true);
	}

	public final String getOriginalPermitNo() {
		return originalPermitNo;
	}

	public final void setOriginalPermitNo(String originalPermitNo) {
		this.originalPermitNo = originalPermitNo;
		setDirty(true);
	}

	public final List<PermitCC> getPermitCCList() {
		if (_CCList == null) {
			_CCList = new ArrayList<PermitCC>(0);
		}
		return _CCList;
	}

	public final void setPermitCCList(List<PermitCC> ccList) {
		_CCList = ccList;
		if (_CCList == null) {
			_CCList = new ArrayList<PermitCC>(0);
		}
	}

	public final void addPermitCC(PermitCC carbonCopy) {

		if (_CCList == null) {
			_CCList = new ArrayList<PermitCC>(1);
		}
		if (carbonCopy != null) {
			_CCList.add(carbonCopy);
		}
	}

	public final String getPermitCCNames() {

		StringBuffer names = new StringBuffer();
		boolean first = true;
		for (PermitCC cc : getPermitCCList()) {
			if (!first) {
				names.append(", ");
			}
			names.append(cc.getName());
		}

		return names.toString();

	}

	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = super.hashCode();

		result = PRIME * result
				+ ((_permitID == null) ? 0 : _permitID.hashCode());
		result = PRIME * result
				+ ((_permitType == null) ? 0 : _permitType.hashCode());
		result = PRIME
				* result
				+ ((_permitGlobalStatusCD == null) ? 0 : _permitGlobalStatusCD
						.hashCode());
		result = PRIME
				* result
				+ ((publicNoticeNewspaperCd == null) ? 0
						: publicNoticeNewspaperCd.hashCode());
		result = PRIME * result
				+ ((_legacyPermitNumber == null) ? 0 : _legacyPermitNumber.hashCode());
		result = PRIME * result
				+ ((_permitNumber == null) ? 0 : _permitNumber.hashCode());
		result = PRIME * result
				+ ((_description == null) ? 0 : _description.hashCode());
		result = PRIME * result
				+ ((_effectiveDate == null) ? 0 : _effectiveDate.hashCode());
		result = PRIME
				* result
				+ ((_modEffectiveDate == null) ? 0 : _modEffectiveDate
						.hashCode());
		result = PRIME * result
				+ ((_eracCaseNumber == null) ? 0 : _eracCaseNumber.hashCode());
		result = PRIME * result
				+ ((_expirationDate == null) ? 0 : _expirationDate.hashCode());
		result = PRIME * result
				+ ((_facilityId == null) ? 0 : _facilityId.hashCode());
		result = PRIME * result + (_variance ? 1 : 0);

		result = PRIME * result + (_mact ? 1 : 0);
		for (String mact : getMactSubpartCDs()) {
			result = PRIME * result + mact.hashCode();
		}

		result = PRIME * result + (_neshaps ? 1 : 0);
		for (String neshaps : getNeshapsSubpartCDs()) {
			result = PRIME * result + neshaps.hashCode();
		}

		result = PRIME * result + (_nsps ? 1 : 0);
		for (String nsps : getNspsSubpartCDs()) {
			result = PRIME * result + nsps.hashCode();
		}

		result = PRIME * result + (_part61NESHAP ? 1 : 0);
		for (String part61NESHAP : getPart61NESHAPSubpartCDs()) {
			result = PRIME * result + part61NESHAP.hashCode();
		}

		result = PRIME * result + (_part63NESHAP ? 1 : 0);
		for (String part63NESHAP : getPart63NESHAPSubpartCDs()) {
			result = PRIME * result + part63NESHAP.hashCode();
		}

		result = PRIME * result + (majorGHG ? 1 : 0);

		result = PRIME * result + (_issueDraft ? 1 : 0);
		result = PRIME * result + (_dapcHearingReqd ? 1 : 0);
		result = PRIME * result + (_rushFlag ? 1 : 0);

		result = PRIME
				* result
				+ ((publicNoticeType == null) ? 0 : publicNoticeType.hashCode());
		result = PRIME
				* result
				+ ((publicNoticeText == null) ? 0 : publicNoticeText.hashCode());

		for (String permitReasonCD : getPermitReasonCDs()) {
			result = PRIME * result + permitReasonCD.hashCode();
		}

		for (PermitEUGroup euGroup : getEuGroups()) {
			result = PRIME * result + euGroup.hashCode();
		}

		for (PermitDocument document : getDocuments()) {
			result = PRIME * result + document.hashCode();
		}

		result = PRIME * result + (updateFacilityDtlPTETableComments ? 1 : 0);

		result = PRIME * result
				+ ((facilityNm == null) ? 0 : facilityNm.hashCode());
		result = PRIME
				* result
				+ ((applicationNumbers == null) ? 0 : applicationNumbers
						.hashCode());
		result = PRIME
				* result
				+ ((originalPermitNo == null) ? 0 : originalPermitNo.hashCode());
		result = PRIME * result
				+ ((_invoice == null) ? 0 : _invoice.hashCode());
		result = PRIME * result + fpId;

		for (PermitIssuance issuance : permitIssuances.values()) {
			result = PRIME * result + issuance.hashCode();
		}

		for (Application application : getApplications()) {
			result = PRIME * result + application.hashCode();
		}

		for (Contact contact : getContacts()) {
			result = PRIME * result + contact.hashCode();
		}

		if (_dapcComments != null) {
			for (Note note : _dapcComments) {
				result = PRIME * result + note.hashCode();
			}
		}

		result = PRIME * result+ (receiptLetterSent ? 1 : 0);
		result = PRIME
				* result
				+ ((receivedComments == null) ? 0 : receivedComments.hashCode());
		result = PRIME
				* result
				+ ((permitLevelStatusCd == null) ? 0 : permitLevelStatusCd.hashCode());


		return result;

	} // END: public int hashCode()

	@Override
	public boolean equals(Object obj) {

		if ((obj == null) || !(super.equals(obj))
				|| (getClass() != obj.getClass())) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final Permit other = (Permit) obj;

		if ((_permitID != other.getPermitID())
				|| (_variance != other.isVariance())
				|| (_mact != other.isMact()) || (_neshaps != other.isNeshaps())
				|| _nsps != other.isNsps() || majorGHG != other.isMajorGHG()
				|| (_issueDraft != other.isIssueDraft())
				|| (_dapcHearingReqd != other.isDapcHearingReqd())
				|| (_rushFlag != other.isRushFlag())
				|| (fpId != other.getFpId())) {
			return false;
		}

		// Either both null or equal values.
		if (((_permitID == null) && (other.getPermitID() != null))
				|| ((_permitID != null) && (other.getPermitID() == null))
				|| ((_permitID != null) && (other.getPermitID() != null) && !(_permitID
						.equals(other.getPermitID())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_permitType == null) && (other.getPermitType() != null))
				|| ((_permitType != null) && (other.getPermitType() == null))
				|| ((_permitType != null) && (other.getPermitType() != null) && !(_permitType
						.equals(other.getPermitType())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_permitGlobalStatusCD == null) && (other
				.getPermitGlobalStatusCD() != null))
				|| ((_permitGlobalStatusCD != null) && (other
						.getPermitGlobalStatusCD() == null))
				|| ((_permitGlobalStatusCD != null)
						&& (other.getPermitGlobalStatusCD() != null) && !(_permitGlobalStatusCD
							.equals(other.getPermitGlobalStatusCD())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_legacyPermitNumber == null) && (other.getLegacyPermitNumber() != null))
				|| ((_legacyPermitNumber != null) && (other.getLegacyPermitNumber() == null))
				|| ((_legacyPermitNumber != null)
						&& (other.getLegacyPermitNumber() != null) && !(_legacyPermitNumber
							.equals(other.getLegacyPermitNumber())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_permitNumber == null) && (other.getPermitNumber() != null))
				|| ((_permitNumber != null) && (other.getPermitNumber() == null))
				|| ((_permitNumber != null)
						&& (other.getPermitNumber() != null) && !(_permitNumber
							.equals(other.getPermitNumber())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_effectiveDate == null) && (other.getEffectiveDate() != null))
				|| ((_effectiveDate != null) && (other.getEffectiveDate() == null))
				|| ((_effectiveDate != null)
						&& (other.getEffectiveDate() != null) && !(_effectiveDate
							.equals(other.getEffectiveDate())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_modEffectiveDate == null) && (other.getModEffectiveDate() != null))
				|| ((_modEffectiveDate != null) && (other.getModEffectiveDate() == null))
				|| ((_modEffectiveDate != null)
						&& (other.getModEffectiveDate() != null) && !(_modEffectiveDate
							.equals(other.getModEffectiveDate())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_eracCaseNumber == null) && (other.getEracCaseNumber() != null))
				|| ((_eracCaseNumber != null) && (other.getEracCaseNumber() == null))
				|| ((_eracCaseNumber != null)
						&& (other.getEracCaseNumber() != null) && !(_eracCaseNumber
							.equals(other.getEracCaseNumber())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_expirationDate == null) && (other.getExpirationDate() != null))
				|| ((_expirationDate != null) && (other.getExpirationDate() == null))
				|| ((_expirationDate != null)
						&& (other.getExpirationDate() != null) && !(_expirationDate
							.equals(other.getExpirationDate())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_facilityId == null) && (other.getFacilityId() != null))
				|| ((_facilityId != null) && (other.getFacilityId() == null))
				|| ((_facilityId != null) && (other.getFacilityId() != null) && !(_facilityId
						.equals(other.getFacilityId())))) {
			return false;
		}

		// Either both null or equal values.
		if (((facilityNm == null) && (other.getFacilityNm() != null))
				|| ((facilityNm != null) && (other.getFacilityNm() == null))
				|| ((facilityNm != null) && (other.getFacilityNm() != null) && !(facilityNm
						.equals(other.getFacilityNm())))) {
			return false;
		}

		// Either both null or equal values.
		if (((applicationNumbers == null) && (other.getApplicationNumbers() != null))
				|| ((applicationNumbers != null) && (other
						.getApplicationNumbers() == null))
				|| ((applicationNumbers != null)
						&& (other.getApplicationNumbers() != null) && !(applicationNumbers
							.equals(other.getApplicationNumbers())))) {
			return false;
		}

		// Either both null or equal values.
		if (((originalPermitNo == null) && (other.getOriginalPermitNo() != null))
				|| ((originalPermitNo != null) && (other.getOriginalPermitNo() == null))
				|| ((originalPermitNo != null)
						&& (other.getOriginalPermitNo() != null) && !(originalPermitNo
							.equals(other.getOriginalPermitNo())))) {
			return false;
		}

		// Either both null or equal values.
		if (((_invoice == null) && (other.getInvoice() != null))
				|| ((_invoice != null) && (other.getInvoice() == null))
				|| ((_invoice != null) && (other.getInvoice() != null) && !(_invoice
						.equals(other.getInvoice())))) {
			return false;
		}

		// Either both null or equal values.
		if (((publicNoticeType == null) && (other.getPublicNoticeType() != null))
				|| ((publicNoticeType != null) && (other.getPublicNoticeType() == null))
				|| ((publicNoticeType != null)
						&& (other.getPublicNoticeType() != null) && !(publicNoticeType
							.equals(other.getPublicNoticeType())))) {
			return false;
		}

		// Either both null or equal values.
		if (((publicNoticeText == null) && (other.getPublicNoticeText() != null))
				|| ((publicNoticeText != null) && (other.getPublicNoticeText() == null))
				|| ((publicNoticeText != null)
						&& (other.getPublicNoticeText() != null) && !(publicNoticeText
							.equals(other.getPublicNoticeText())))) {
			return false;
		}

		// Either both null or equal values.
		if (((publicNoticeNewspaperCd == null) && (other
				.getPublicNoticeNewspaperCd() != null))
				|| ((publicNoticeNewspaperCd != null) && (other
						.getPublicNoticeNewspaperCd() == null))
				|| ((publicNoticeNewspaperCd != null)
						&& (other.getPublicNoticeNewspaperCd() != null) && !(publicNoticeNewspaperCd
							.equals(other.getPublicNoticeNewspaperCd())))) {
			return false;
		}

		// Each of our permit reason codes must have a corresponding code in
		// theirs and vice versa.
		String[] ourReasonCDs = getPermitReasonCDs().toArray(new String[0]);
		String[] theirReasonCDs = other.getPermitReasonCDs().toArray(
				new String[0]);

		for (int i = 0; i < ourReasonCDs.length; i++) {
			boolean found = false;
			for (int j = 0; j < theirReasonCDs.length; j++) {
				if (theirReasonCDs[j] == null) {
					continue;
				}
				if (ourReasonCDs[i].equals(theirReasonCDs[j])) {
					found = true;
					theirReasonCDs[j] = null;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		for (int j = 0; j < theirReasonCDs.length; j++) {
			if (theirReasonCDs[j] != null) {
				return false;
			}
		}

		// Each of our mact codes must have a corresponding code in theirs and
		// vice versa.
		if (isMact()) {
			String[] ourMactCDs = getMactSubpartCDs().toArray(new String[0]);
			String[] theirMactCDs = other.getMactSubpartCDs().toArray(
					new String[0]);

			// Either both null or equal values.
			if (((ourMactCDs == null) && (theirMactCDs != null))
					|| ((ourMactCDs != null) && (theirMactCDs == null))
					|| ((ourMactCDs != null) && (theirMactCDs != null) && !(ourMactCDs.length == theirMactCDs.length))) {
				return false;
			}
			if (ourMactCDs != null && theirMactCDs != null) {
				for (int i = 0; i < ourMactCDs.length; i++) {
					boolean found = false;
					for (int j = 0; j < theirMactCDs.length; j++) {
						if (theirMactCDs[j] == null) {
							continue;
						}
						if (ourMactCDs[i].equals(theirMactCDs[j])) {
							found = true;
							theirMactCDs[j] = null;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
				for (int j = 0; j < theirMactCDs.length; j++) {
					if (theirMactCDs[j] != null) {
						return false;
					}
				}
			}
		}

		// Each of our neshaps codes must have a corresponding code in theirs
		// and vice versa.
		if (isNeshaps()) {
			String[] ourNeshapsCDs = getNeshapsSubpartCDs().toArray(
					new String[0]);
			String[] theirNeshapsCDs = other.getNeshapsSubpartCDs().toArray(
					new String[0]);

			// Either both null or equal values.
			if (((ourNeshapsCDs == null) && (theirNeshapsCDs != null))
					|| ((ourNeshapsCDs != null) && (theirNeshapsCDs == null))
					|| ((ourNeshapsCDs != null) && (theirNeshapsCDs != null) && !(ourNeshapsCDs.length == theirNeshapsCDs.length))) {
				return false;
			}
			if (ourNeshapsCDs != null && theirNeshapsCDs != null) {
				for (int i = 0; i < ourNeshapsCDs.length; i++) {
					boolean found = false;
					for (int j = 0; j < theirNeshapsCDs.length; j++) {
						if (theirNeshapsCDs[j] == null) {
							continue;
						}
						if (ourNeshapsCDs[i].equals(theirNeshapsCDs[j])) {
							found = true;
							theirNeshapsCDs[j] = null;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
				for (int j = 0; j < theirNeshapsCDs.length; j++) {
					if (theirNeshapsCDs[j] != null) {
						return false;
					}
				}
			}
		}

		if (isNsps()) {
			// Each of our nsps codes must have a corresponding code in theirs
			// and vice versa.
			String[] ourNspsCDs = getNspsSubpartCDs().toArray(new String[0]);
			String[] theirNspsCDs = other.getNspsSubpartCDs().toArray(
					new String[0]);

			// Either both null or equal values.
			if (((ourNspsCDs == null) && (theirNspsCDs != null))
					|| ((ourNspsCDs != null) && (theirNspsCDs == null))
					|| ((ourNspsCDs != null) && (theirNspsCDs != null) && !(ourNspsCDs.length == theirNspsCDs.length))) {
				return false;
			}
			if (ourNspsCDs != null && theirNspsCDs != null) {
				for (int i = 0; i < ourNspsCDs.length; i++) {
					boolean found = false;
					for (int j = 0; j < theirNspsCDs.length; j++) {
						if (theirNspsCDs[j] == null) {
							continue;
						}
						if (ourNspsCDs[i].equals(theirNspsCDs[j])) {
							found = true;
							theirNspsCDs[j] = null;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
				for (int j = 0; j < theirNspsCDs.length; j++) {
					if (theirNspsCDs[j] != null) {
						return false;
					}
				}
			}
		}

		// Each of our part61NESAP codes must have a corresponding code in
		// theirs and vice versa.
		if (isPart61NESHAP()) {
			String[] ourPart61NESHAPCDs = getPart61NESHAPSubpartCDs().toArray(
					new String[0]);
			String[] theirPart61NESHAPCDs = other.getPart61NESHAPSubpartCDs()
					.toArray(new String[0]);

			// Either both null or equal values.
			if (((ourPart61NESHAPCDs == null) && (theirPart61NESHAPCDs != null))
					|| ((ourPart61NESHAPCDs != null) && (theirPart61NESHAPCDs == null))
					|| ((ourPart61NESHAPCDs != null)
							&& (theirPart61NESHAPCDs != null) && !(ourPart61NESHAPCDs.length == theirPart61NESHAPCDs.length))) {
				return false;
			}
			if (ourPart61NESHAPCDs != null && theirPart61NESHAPCDs != null) {
				for (int i = 0; i < ourPart61NESHAPCDs.length; i++) {
					boolean found = false;
					for (int j = 0; j < theirPart61NESHAPCDs.length; j++) {
						if (theirPart61NESHAPCDs[j] == null) {
							continue;
						}
						if (ourPart61NESHAPCDs[i]
								.equals(theirPart61NESHAPCDs[j])) {
							found = true;
							theirPart61NESHAPCDs[j] = null;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
				for (int j = 0; j < theirPart61NESHAPCDs.length; j++) {
					if (theirPart61NESHAPCDs[j] != null) {
						return false;
					}
				}
			}
		}

		// Each of our part63NESAP codes must have a corresponding code in
		// theirs and vice versa.
		if (isPart63NESHAP()) {
			String[] ourPart63NESHAPCDs = getPart63NESHAPSubpartCDs().toArray(
					new String[0]);
			String[] theirPart63NESHAPCDs = other.getPart63NESHAPSubpartCDs()
					.toArray(new String[0]);

			// Either both null or equal values.
			if (((ourPart63NESHAPCDs == null) && (theirPart63NESHAPCDs != null))
					|| ((ourPart63NESHAPCDs != null) && (theirPart63NESHAPCDs == null))
					|| ((ourPart63NESHAPCDs != null)
							&& (theirPart63NESHAPCDs != null) && !(ourPart63NESHAPCDs.length == theirPart63NESHAPCDs.length))) {
				return false;
			}
			if (ourPart63NESHAPCDs != null && theirPart63NESHAPCDs != null) {
				for (int i = 0; i < ourPart63NESHAPCDs.length; i++) {
					boolean found = false;
					for (int j = 0; j < theirPart63NESHAPCDs.length; j++) {
						if (theirPart63NESHAPCDs[j] == null) {
							continue;
						}
						if (ourPart63NESHAPCDs[i]
								.equals(theirPart63NESHAPCDs[j])) {
							found = true;
							theirPart63NESHAPCDs[j] = null;
							break;
						}
					}
					if (!found) {
						return false;
					}
				}
				for (int j = 0; j < theirPart63NESHAPCDs.length; j++) {
					if (theirPart63NESHAPCDs[j] != null) {
						return false;
					}
				}
			}
		}

		// Each of our permitEUGroups must have a corresponding permitEUGroup in
		// theirs and vice versa.
		PermitEUGroup[] ourPermitEUGroups = getEuGroups().toArray(
				new PermitEUGroup[0]);

		PermitEUGroup[] theirPermitEUGroups = other.getEuGroups().toArray(
				new PermitEUGroup[0]);

		for (int i = 0; i < ourPermitEUGroups.length; i++) {
			boolean found = false;
			for (int j = 0; j < theirPermitEUGroups.length; j++) {
				if (theirPermitEUGroups[j] == null) {
					continue;
				}
				if (ourPermitEUGroups[i].equals(theirPermitEUGroups[j])) {
					found = true;
					theirPermitEUGroups[j] = null;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		for (int j = 0; j < theirPermitEUGroups.length; j++) {
			if (theirPermitEUGroups[j] != null) {
				return false;
			}
		}

		// Each of our permit issuances must have a corresponding issuance in
		// theirs and vice versa.
		PermitIssuance[] ourIssuances = permitIssuances.values().toArray(
				new PermitIssuance[0]);
		PermitIssuance[] theirIssuances = other.getPermitIssuances().values()
				.toArray(new PermitIssuance[0]);

		for (int i = 0; i < ourIssuances.length; i++) {
			boolean found = false;
			for (int j = 0; j < theirIssuances.length; j++) {
				if (theirIssuances[j] == null) {
					continue;
				}
				if (ourIssuances[i].equals(theirIssuances[j])) {
					found = true;
					theirIssuances[j] = null;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		for (int j = 0; j < theirIssuances.length; j++) {
			if (theirIssuances[j] != null) {
				return false;
			}
		}

		// Each of our applications must have a corresponding application in
		// theirs and vice versa.
		Application[] ourApplications = getApplications().toArray(
				new Application[0]);
		Application[] theirApplications = other.getApplications().toArray(
				new Application[0]);

		for (int i = 0; i < ourApplications.length; i++) {
			boolean found = false;
			for (int j = 0; j < theirApplications.length; j++) {
				if (theirApplications[j] == null) {
					continue;
				}
				if (ourApplications[i].equals(theirApplications[j])) {
					found = true;
					theirApplications[j] = null;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		for (int j = 0; j < theirApplications.length; j++) {
			if (theirApplications[j] != null) {
				return false;
			}
		}

		// Each of our contacts must have a corresponding contact in theirs and
		// vice versa.
		Contact[] ourContacts = getContacts().toArray(new Contact[0]);
		Contact[] theirContacts = other.getContacts().toArray(new Contact[0]);
		for (int i = 0; i < ourContacts.length; i++) {
			boolean found = false;
			for (int j = 0; j < theirContacts.length; j++) {
				if (theirContacts[j] == null) {
					continue;
				}
				if (ourContacts[i].equals(theirContacts[j])) {
					found = true;
					theirContacts[j] = null;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		for (int j = 0; j < theirContacts.length; j++) {
			if (theirContacts[j] != null) {
				return false;
			}
		}

		// Each of our PermitDocuments must have a corresponding PermitDocument
		// in theirs and vice versa.
		PermitDocument[] ourPermitDocuments = getDocuments().toArray(
				new PermitDocument[0]);

		PermitDocument[] theirPermitDocuments = other.getDocuments().toArray(
				new PermitDocument[0]);

		for (int i = 0; i < ourPermitDocuments.length; i++) {
			boolean found = false;
			for (int j = 0; j < theirPermitDocuments.length; j++) {
				if (theirPermitDocuments[j] == null) {
					continue;
				}
				if (ourPermitDocuments[i].equals(theirPermitDocuments[j])) {
					found = true;
					theirPermitDocuments[j] = null;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		for (int j = 0; j < theirPermitDocuments.length; j++) {
			if (theirPermitDocuments[j] != null) {
				return false;
			}
		}

		// Each of our notes must have a corresponding note in theirs and vice
		// versa.
		Note[] ourNotes = _dapcComments;
		Note[] theirNotes = other.getDapcComments();
		// Either both null or equal values.
		if (((ourNotes == null) && (theirNotes != null))
				|| ((ourNotes != null) && (theirNotes == null))
				|| ((ourNotes != null) && (theirNotes != null) && !(ourNotes.length == theirNotes.length))) {
			return false;
		}
		if (ourNotes != null && theirNotes != null) {
			for (int i = 0; i < ourNotes.length; i++) {
				boolean found = false;
				for (int j = 0; j < theirNotes.length; j++) {
					if (theirNotes[j] == null) {
						continue;
					}
					if (ourNotes[i].equals(theirNotes[j])) {
						found = true;
						theirNotes[j] = null;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}
			for (int j = 0; j < theirNotes.length; j++) {
				if (theirNotes[j] != null) {
					return false;
				}
			}
		}
		
		if (((permitLevelStatusCd == null) && (other.getPermitLevelStatusCd() != null))
				|| ((permitLevelStatusCd != null) && (other.getPermitLevelStatusCd() == null))
				|| ((permitLevelStatusCd != null)
						&& (other.getPermitLevelStatusCd() != null) && !(permitLevelStatusCd
							.equals(other.getPermitLevelStatusCd())))) {
			return false;
		}

		return true;

	} // END: public final boolean equals(Object obj)

	public String toXML() {

		StringBuffer sb = new StringBuffer();
		sb.append("<basePermit>");

		sb.append("<permitID>" + _permitID + "</permitID>");
		sb.append("<permitType>" + _permitType + "</permitType>");
		sb.append("<permitGlobalStatusCD>" + _permitGlobalStatusCD
				+ "</permitGlobalStatusCD>");
		sb.append("<legacyPermitNumber>" + _legacyPermitNumber + "</legacyPermitNumber>");
		sb.append("<permitNumber>" + _permitNumber + "</permitNumber>");
		sb.append("<description>" + _description + "</description>");
		sb.append("<effectiveDate>" + _effectiveDate.toString()
				+ "</effectiveDate>");
		sb.append("<modEffectiveDate>" + _modEffectiveDate.toString()
				+ "</modEffectiveDate>");
		sb.append("<eracCaseNumber>" + _eracCaseNumber + "</eracCaseNumber>");
		sb.append("<expirationDate>" + _expirationDate.toString()
				+ "</expirationDate>");
		sb.append("<facilityID>" + _facilityId + "</facilityID>");
		sb.append("<variance>" + (_variance ? "Yes" : "No") + "</variance>");

		sb.append("<mact>" + (_mact ? "Yes" : "No") + "</mact>");
		if (_mact) {
			sb.append("<mactCDs>");
			Iterator<String> it = getMactSubpartCDs().iterator();
			while (it.hasNext()) {
				String code = it.next();
				sb.append(code);
				if (it.hasNext()) {
					sb.append(" ");
				}
			}
			sb.append("</mactCDs>");
		}

		sb.append("<neshaps>" + (_neshaps ? "Yes" : "No") + "</neshaps>");
		if (_neshaps) {
			sb.append("<neshapsCDs>");
			Iterator<String> it = getNeshapsSubpartCDs().iterator();
			while (it.hasNext()) {
				String code = it.next();
				sb.append(code);
				if (it.hasNext()) {
					sb.append(" ");
				}
			}
			sb.append("</neshapsCDs>");
		}

		sb.append("<nsps>" + (_nsps ? "Yes" : "No") + "</nsps>");
		if (_nsps) {
			sb.append("<nspsCDs>");
			Iterator<String> it = getNspsSubpartCDs().iterator();
			while (it.hasNext()) {
				String code = it.next();
				sb.append(code);
				if (it.hasNext()) {
					sb.append(" ");
				}
			}
			sb.append("</nspsCDs>");
		}

		sb.append("<majorGHG>" + (majorGHG ? "Yes" : "No") + "</majorGHG>");

		sb.append("<issueDraft>" + (_issueDraft ? "Yes" : "No")
				+ "</issueDraft>");
		sb.append("<dapcHearingReqd>" + (_dapcHearingReqd ? "Yes" : "No")
				+ "</dapcHearingReqd>");

		sb.append("<permitReasonCDs>");
		Iterator<String> it = getPermitReasonCDs().iterator();
		while (it.hasNext()) {
			String code = it.next();
			sb.append(code);
			if (it.hasNext()) {
				sb.append(" ");
			}
		}
		sb.append("</permitReasonCDs>");
		sb.append("<primaryReasonCD>" + _primaryReasonCD + "</primaryReasonCD>");

		sb.append("</basePermit>");
		return sb.toString();
	}

	/**
	 * currentIssuanceDoc is used to retrieve the current issuance document. It
	 * has to call after setCurrentIssuanceDoc. DAO is not setting up this
	 * field.
	 * 
	 * @return
	 */
	public final PermitDocument getCurrentIssuanceDoc() {
		return currentIssuanceDoc;
	}

	/**
	 * Use issuanceType to find the current issuance doc.
	 * 
	 * @param issuanceType
	 */
	public final void setCurrentIssuanceDoc(String issuanceType) {

		currentIssuanceDoc = null;
		for (PermitDocument tpd : getDocuments()) {
			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
					PermitDocTypeDef.ISSUANCE_DOCUMENT)
					&& PermitDocIssuanceStageDef.getStage(issuanceType)
							.equalsIgnoreCase(tpd.getIssuanceStageFlag())) {
				currentIssuanceDoc = tpd;
			} else if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
					PermitDocTypeDef.ADDRESS_LABELS)
					&& PermitDocIssuanceStageDef.getStage(issuanceType)
							.equalsIgnoreCase(tpd.getIssuanceStageFlag())) {
				_currentCCDoc = tpd;
			} else if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
					PermitDocTypeDef.FAX_COVER_SHEET)
					&& PermitDocIssuanceStageDef.getStage(issuanceType)
							.equalsIgnoreCase(tpd.getIssuanceStageFlag())) {
				_faxCoverDoc = tpd;
			}
		}
		issueDate = getIssueDate(issuanceType);
	}

	public final PermitDocument getCurrentCCDoc() {
		return _currentCCDoc;
	}

	public final PermitDocument getFaxCoverDoc() {
		return _faxCoverDoc;
	}

	/**
	 * Use issuanceType to find the current cc doc.
	 * 
	 * @param issuanceType
	 */
	public final void setCurrentCCDoc(String issuanceType) {

		_currentCCDoc = null;
		for (PermitDocument tpd : getDocuments()) {
			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(
					PermitDocTypeDef.ADDRESS_LABELS)
					&& tpd.getIssuanceStageFlag().equalsIgnoreCase(
							PermitDocIssuanceStageDef.getStage(issuanceType))) {
				_currentCCDoc = tpd;
				break;
			}
		}
	}

	public final Timestamp getIssueDate() {
		return issueDate;
	}

	public final String getPublicNoticeType() {
		return publicNoticeType;
	}

	public final void setPublicNoticeType(String publicNoticeType) {
		if (publicNoticeType == null) {
			publicNoticeType = PublicNoticeTypeDef.STANDARD_WORDING;
		}
		this.publicNoticeType = publicNoticeType;
	}

	public final String getPublicNoticeText() {
		return publicNoticeText;
	}

	public final void setPublicNoticeText(String publicNoticeText) {
		// Do this to make sure that if the type is SW, the text cannot be
		// changed.
		if (!PublicNoticeTypeDef.STANDARD_WORDING.equals(publicNoticeType)
				&& !PublicNoticeTypeDef.PSD_STANDARD_WORDING
						.equals(publicNoticeType))
			this.publicNoticeText = publicNoticeText;
	}

	/**
	 * This method is used to find the individual EU Group in this permit.
	 * 
	 * @return
	 */
	public PermitEUGroup getIndividualEuGroup() {
		for (PermitEUGroup euGroup : getEuGroups()) {
			if (euGroup.isIndividualEUGroup()) {
				return euGroup;
			}
		}
		return null;
	}

	/**
	 * This method is used to find the insignificant EU Group in this permit.
	 * 
	 * @return
	 */
	public PermitEUGroup getInsignificantEuGroup() {
		for (PermitEUGroup euGroup : getEuGroups()) {
			if (euGroup.isInsignificantEuGroup()) {
				return euGroup;
			}
		}
		return null;
	}

	public final PermitDocument getInvoiceDoc() {
		return invoiceDoc;
	}

	public final void setInvoiceDoc(PermitDocument invoiceDoc) {
		this.invoiceDoc = invoiceDoc;
	}

	public final boolean isLegacyPermit() {
		return legacyPermit;
	}

	public final void setLegacyPermit(boolean legacyPermit) {
		this.legacyPermit = legacyPermit;
	}

	public boolean isGeneralPermit() {
		return false;
	}

	public Double getTotalAmount() {
		return new Double(0.0);
	}

	/**
	 * @return the earliestSubmittedDate
	 */
	public final Timestamp getEarliestSubmittedDate() {
		return earliestSubmittedDate;
	}

	/**
	 * @param earliestSubmittedDate
	 *            the earliestSubmittedDate to set
	 */
	public final void setEarliestSubmittedDate(Timestamp earliestSubmittedDate) {
		this.earliestSubmittedDate = earliestSubmittedDate;
		setDirty(true);
	}

	public void setPermitEU(Integer corrEpaEmuId) {
		permitEU = null;
		outer: for (PermitEUGroup euGroup : _euGroups) {
			for (PermitEU eu : euGroup.getPermitEUs()) {
				if (eu.getFpEU().getCorrEpaEmuId().equals(corrEpaEmuId)) {
					permitEU = eu;
					break outer;
				}
			}
		}
	}

	public final PermitEU getPermitEU() {
		return permitEU;
	}

	public final void setPermitEU(PermitEU permitEU) {
		this.permitEU = permitEU;
	}

	public PermitNote getNoteErrMsg() {
		return noteErrMsg;
	}

	public boolean isEarlyRenewalFlag() {
		return _earlyRenewalFlag;
	}

	public void setEarlyRenewalFlag(boolean earlyRenewalFlag) {
		this._earlyRenewalFlag = earlyRenewalFlag;
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

	public String getPublicNoticeNewspaperCd() {
		return publicNoticeNewspaperCd;
	}

	public void setPublicNoticeNewspaperCd(String publicNoticeNewspaperCd) {
		this.publicNoticeNewspaperCd = publicNoticeNewspaperCd;
	}

	public boolean isUpdateFacilityDtlPTETableComments() {
		return updateFacilityDtlPTETableComments;
	}

	public void setUpdateFacilityDtlPTETableComments(
			boolean updateFacilityDtlPTETableComments) {
		this.updateFacilityDtlPTETableComments = updateFacilityDtlPTETableComments;
	}

	public void setReceiptLetterSent(boolean receiptLetterSent) {
		this.receiptLetterSent = receiptLetterSent;
	}

	public boolean isReceiptLetterSent() {
		return receiptLetterSent;
	}

	public String getReceivedComments() {
		return receivedComments;
	}

	public void setReceivedComments(String receivedComments) {
		this.receivedComments = receivedComments;
	}

	public final boolean isPart61NESHAP() {
		return _part61NESHAP;
	}

	public final void setPart61NESHAP(boolean part61NESHAP) {

		_part61NESHAP = part61NESHAP;
		if (_part61NESHAP && getPart61NESHAPSubpartCDs().isEmpty()) {
			ValidationMessage msg = new ValidationMessage("Part 61 NESHAP",
					"Part 61 NESHAP is set, but there are no subpart codes.",
					ValidationMessage.Severity.ERROR);
			validationMessages.put("part61NESHAP", msg);
		} else {
			validationMessages.remove("part61NESHAP");
		}
		setDirty(true);
	}

	public final List<String> getPart61NESHAPSubpartCDs() {
		if (_part61NESHAPSubpartCDs == null) {
			_part61NESHAPSubpartCDs = new ArrayList<String>(0);
		}
		return _part61NESHAPSubpartCDs;
	}

	public final void setPart61NESHAPSubpartCDs(
			List<String> part61NESHAPSubpartCDs) {

		_part61NESHAPSubpartCDs = part61NESHAPSubpartCDs;
		if (part61NESHAPSubpartCDs == null) {
			_part61NESHAPSubpartCDs = new ArrayList<String>(0);
			setPart61NESHAP(false);
		} else {
			setPart61NESHAP(true);
		}
	}

	public final boolean isPart63NESHAP() {
		return _part63NESHAP;
	}

	public final void setPart63NESHAP(boolean part63NESHAP) {

		_part63NESHAP = part63NESHAP;
		if (_part63NESHAP && getPart63NESHAPSubpartCDs().isEmpty()) {
			ValidationMessage msg = new ValidationMessage("Part 63 NESHAP",
					"Part 63 NESHAP is set, but there are no subpart codes.",
					ValidationMessage.Severity.ERROR);
			validationMessages.put("part63NESHAP", msg);
		} else {
			validationMessages.remove("part63NESHAP");
		}
		setDirty(true);
	}

	public final List<String> getPart63NESHAPSubpartCDs() {
		if (_part63NESHAPSubpartCDs == null) {
			_part63NESHAPSubpartCDs = new ArrayList<String>(0);
		}
		return _part63NESHAPSubpartCDs;
	}

	public final void setPart63NESHAPSubpartCDs(
			List<String> part63NESHAPSubpartCDs) {

		_part63NESHAPSubpartCDs = part63NESHAPSubpartCDs;
		if (part63NESHAPSubpartCDs == null) {
			_part63NESHAPSubpartCDs = new ArrayList<String>(0);
			setPart63NESHAP(false);
		} else {
			setPart63NESHAP(true);
		}
	}
	
	public final List<PermitDocument> getFixedPotentialDocuments() {
		List<String> attachedDocTypes = new ArrayList<String>(0);
		for (PermitDocument permitDoc: _documents)
		{
			attachedDocTypes.add(permitDoc.getPermitDocTypeCD());
		}
		if (_fixedPotentialDocuments == null) {
			_fixedPotentialDocuments = new ArrayList<PermitDocument>(0);
			DefSelectItems permitDocTypeDefs = PermitDocTypeDef.getData().getItems();
			for (SelectItem item : permitDocTypeDefs.getCurrentItems()) {
				PermitDocTypeDef permitDocTypeDef = (PermitDocTypeDef) permitDocTypeDefs
						.getItem(item.getValue().toString());
				if (permitDocTypeDef.isFixed()) {
					if (!attachedDocTypes.contains(permitDocTypeDef.getCode()))
					{
						PermitDocument document = new PermitDocument();
						document.setPermitDocTypeCD(permitDocTypeDef.getCode());
					}
				}
			}
		}
		return _fixedPotentialDocuments;
	}
	
	// NSR attribute needed here to support Permit Search page
	public Timestamp getPermitSentOutDate() {
		return permitSentOutDate;
	}

	public void setPermitSentOutDate(Timestamp permitSentOutDate) {
		this.permitSentOutDate = permitSentOutDate;
		setDirty(true);
	}

	// TV and NSR attribute needed here to support Permit Search page
	public Timestamp getRecissionDate() {
		return recissionDate;
	}

	public void setRecissionDate(Timestamp recissionDate) {
		this.recissionDate = recissionDate;
		setDirty(true);
	}
	
	public String getTruncatedDescription() {
		return truncatedDescription;
	}

	public void setTruncatedDescription(String truncatedDescription) {
		this.truncatedDescription = truncatedDescription;
	}
	
	public final PermitDocument getFinalInvoiceDocument() {

		PermitDocument ret = null;
		String docType = new String();
		if (_permitType.equalsIgnoreCase(PermitTypeDef.NSR)) {
			docType = PermitDocTypeDef.FINAL_INVOICE;
		} else if (_permitType.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
			// Not supported yet
		}
		for (PermitDocument tpd : getDocuments()) {
			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(docType)) {

				ret = tpd;
				break;
			}
		}
		return ret;
	}
	
	public final PermitDocument getInitialInvoiceDocument() {

		PermitDocument ret = null;
		String docType = new String();
		if (_permitType.equalsIgnoreCase(PermitTypeDef.NSR)) {
			docType = PermitDocTypeDef.INITIAL_INVOICE;
		} else if (_permitType.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
			// Not supported yet
		}
		for (PermitDocument tpd : getDocuments()) {
			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(docType)) {

				ret = tpd;
				break;
			}
		}
		return ret;
	}
	
	// whether to display the invoice column in the permit manifest jsp
	public final boolean isDisplayInvoiceColumn() {
		return _permitType.equalsIgnoreCase(PermitTypeDef.NSR)
					&& _permitGlobalStatusCD.equalsIgnoreCase(PermitGlobalStatusDef.ISSUED_DRAFT);
	}

	public Double getInitialInvoiceAmount() {
		return initialInvoiceAmount;
	}

	public void setInitialInvoiceAmount(Double initialInvoiceAmount) {
		this.initialInvoiceAmount = initialInvoiceAmount;
	}

	public Double getFinalInvoiceAmount() {
		return finalInvoiceAmount;
	}

	public void setFinalInvoiceAmount(Double finalInvoiceAmount) {
		this.finalInvoiceAmount = finalInvoiceAmount;
	}

	public Double getTotalCharges() {
		return totalCharges;
	}

	public void setTotalCharges(Double totalCharges) {
		this.totalCharges = totalCharges;
	}

	public Double getTotalPayments() {
		return totalPayments;
	}

	public void setTotalPayments(Double totalPayments) {
		this.totalPayments = totalPayments;
	}

	public Double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public Timestamp getLastInvoiceRefDate() {
		return lastInvoiceRefDate;
	}

	public void setLastInvoiceRefDate(Timestamp lastInvoiceRefDate) {
		this.lastInvoiceRefDate = lastInvoiceRefDate;
	}
	
	public final List<PermitCondition> getPermitConditionList() {

		if (_permitConditionList == null) {
			_permitConditionList = new ArrayList<PermitCondition>(0);
		}
		return _permitConditionList;
	}

	public final void setPermitConditionList(List<PermitCondition> pcList) {
		_permitConditionList = pcList;
		if (_permitConditionList == null) {
			_permitConditionList = new ArrayList<PermitCondition>(0);
		}
	}

	public Timestamp getPermitBasisDt() {
		return permitBasisDt;
	}

	public void setPermitBasisDt(Timestamp permitBasisDt) {
		this.permitBasisDt = permitBasisDt;
	}

	public String getPermitLevelStatusCd() {
		return permitLevelStatusCd;
	}

	public void setPermitLevelStatusCd(String permitLevelStatusCd) {
		this.permitLevelStatusCd = permitLevelStatusCd;
	}
	
	public final Timestamp getPublicCommentEndDate() {
		return getPublicCommentEndDate(PermitIssuanceTypeDef.Draft);
	}

	public final void setPublicCommentEndDate(Timestamp publicCommentEndDate) {
		setPublicCommentEndDate(publicCommentEndDate, PermitIssuanceTypeDef.Draft);
		setDirty(true);
	}

	protected Timestamp getPublicCommentEndDate(String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			return pi.getPublicCommentEndDate();
		}
		return null;
	}

	protected void setPublicCommentEndDate(Timestamp publicCommentEndDate, String type) {
		PermitIssuance pi = permitIssuances.get(type);
		if (pi != null) {
			pi.setPublicCommentEndDate(publicCommentEndDate);
		} else {
			pi = new PermitIssuance(getPermitID(), getPermitNumber(), type, IssuanceStatusDef.NOT_READY);
			pi.setPublicCommentEndDate(publicCommentEndDate);
			permitIssuances.put(type, pi);
		}
		setDirty(true);
	}

	public final PermitDocument getDraftTitleVPermitDoc() {

		PermitDocument ret = null;
		String docType = null;
		if (_permitType.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
			
			docType = PermitDocTypeDef.DRAFT_TV_PERMIT_DOCUMENT;
			

			if (docType != null) {
				for (PermitDocument tpd : getDocuments()) {

					if (tpd.getPermitDocTypeCD().equalsIgnoreCase(docType)) {

						ret = tpd;
						break;
					}
				}
			}
		}

		return ret;
	}

	public final PermitDocument getPublicNoticeDoc() {

		PermitDocument ret = new PermitDocument();
		String docType = new String();
		if (_permitType.equalsIgnoreCase(PermitTypeDef.NSR)) {
			docType = PermitDocTypeDef.NSR_PUBLIC_NOTICE_DOCUMENT;
		} else if (_permitType.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
			docType = PermitDocTypeDef.TV_PUBLIC_NOTICE_DOCUMENT;
		}
		for (PermitDocument tpd : getDocuments()) {

			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(docType)) {
				ret = tpd;
				break;
			}
		}
		return ret;
	}

	public final PermitDocument getDraftStatementOfBasisDoc() {

		PermitDocument ret = new PermitDocument();
		String docType = new String();

		docType = PermitDocTypeDef.DRAFT_STATEMENT_BASIS;

		for (PermitDocument tpd : getDocuments()) {

			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(docType)) {
				ret = tpd;
				break;
			}
		}
		return ret;
	}

	public final PermitDocument getProposedStatementOfBasisDoc() {

		PermitDocument ret = new PermitDocument();
		String docType = new String();

		docType = PermitDocTypeDef.PROPOSED_STATEMENT_BASIS;

		for (PermitDocument tpd : getDocuments()) {

			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(docType)) {
				ret = tpd;
				break;
			}
		}
		return ret;
	}

	public final PermitDocument getFinalStatementOfBasisDoc() {

		PermitDocument ret = new PermitDocument();
		String docType = new String();

		docType = PermitDocTypeDef.FINAL_STATEMENT_BASIS;

		for (PermitDocument tpd : getDocuments()) {

			if (tpd.getPermitDocTypeCD().equalsIgnoreCase(docType)) {
				ret = tpd;
				break;
			}
		}
		return ret;
	}
	
	public final List<PermitDocument> getCommentsDocuments() {
		if (_commentsDocuments == null) {
			_commentsDocuments = new ArrayList<PermitDocument>(0);
		}
		return _commentsDocuments;
	}

	public final void setCommentsDocuments(List<PermitDocument> documents) {

		_commentsDocuments = documents;
		if (documents == null) {
			_commentsDocuments = new ArrayList<PermitDocument>(0);
		}
		setDirty(true);
	}

	public final void addCommentsDocument(PermitDocument permitDoc) {

		if (_commentsDocuments == null) {
			_commentsDocuments = new ArrayList<PermitDocument>(1);
		}
		_commentsDocuments.add(permitDoc);
		setDirty(true);
	}
	
	public final boolean isShowPublicNoticeColumns() {
		boolean ret = false;
		if (getDraftIssuanceStatusCd().equals("I") || isLegacyPermit()) {
			ret = true;
		}
		return ret;
	}

	public final boolean isShowProposedColumns() {
		boolean ret = false;
		if (getPpIssuanceStatusCd().equals("I") || isLegacyPermit()) {
			ret = true;
		}
		return ret;
	}

	public final boolean isShowFinalColumns() {
		boolean ret = false;
		if (getFinalIssuanceStatusCd().equals("I") || isLegacyPermit()) {
			ret = true;
		}
		return ret;
	}
}

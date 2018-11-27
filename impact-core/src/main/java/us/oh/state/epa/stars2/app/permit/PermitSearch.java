package us.oh.state.epa.stars2.app.permit;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.app.tools.BulkOperationsCatalog;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dao.permit.PermitSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.PermitFeeBalanceTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.Pair;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.def.PermitLevelStatusDef;

/**
 * @author Pyeh
 * 
 */
@SuppressWarnings("serial")
public class PermitSearch extends AppBase {
	public static String STANDARD_LIST = "StandardList";
	public static String NSR_BILLING_LIST = "NSRBillingList";
	
	private String legacyPermitNumber;
	private String permitNumber;
	private String facilityID;
	private String facilityName;
	private String permitType;
	private String permitReason;
	private String permitLevelStatusCd;
	private String permitGlobalStatusCd;
	private String dateBy;
	private Timestamp beginDt;
	private Timestamp endDt;
	private String permitEUStatusCd;
	private String fromFacility;
	private boolean hasSearchResults;
	private String applicationNumber;
	private String newPermitNumber;
	private Pair<Class<? extends Permit>, Boolean> newPermitType;
	private boolean newPemitIssuedDraft;
	private Timestamp newPermitDraftIssueDate;
	private Timestamp newPermitFinalIssueDate;
	private Timestamp newPermitEffectiveDate;
	private Timestamp newPermitExpirationDate;
	protected UploadedFile draftFileToUpload;
	protected UploadedFileInfo draftFileInfo;
	protected UploadedFile finalFileToUpload;
	protected UploadedFileInfo finalFileInfo;
	protected UploadedFile sobFileToUpload;
	protected UploadedFileInfo sobFileInfo;
	private String cmpId;
	private String newPermitDescription;

	private LinkedHashMap<String, String> permitStatus;
	private List<Permit> permits;
	private String euId;
	private String popupRedirectOutcome;
	private PermitSearch backup;

	private static final String FEPTIO = "FEPTIO";
	private PermitService permitService;
	
	private boolean standardList;
	private boolean NSRBillingList; 
	
	private String permitFeeBalanceTypeCd;
	
	public PermitSearch() {
		super();
		reset();
		fromFacility = "false";
		cacheViewIDs.add("/permits/permitSearch.jsp");
		cacheViewIDs.add("/facilities/permits.jsp");
	}
	
	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public final boolean isAddPermitAble() {
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| InfrastructureDefs.getCurrentUserAttrs().isGeneralUser()
				|| InfrastructureDefs.getCurrentUserAttrs()
						.isCurrentUseCaseValid("permits.addOldPermit");
	}

	public final String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
			FacesUtil.setOutcome(null, popupRedirectOutcome);
			popupRedirectOutcome = null;
		}
		return null;
	}

	/**
	 * @return
	 */
	public final String search() {
		String ret = "failure";

		try {
			permits = null;
			hasSearchResults = false;
			permits = getPermitService().search(applicationNumber, euId, cmpId,
					permitType, permitReason, permitLevelStatusCd, legacyPermitNumber, permitNumber, facilityID,
					facilityName, permitGlobalStatusCd, dateBy, beginDt, endDt,
					permitEUStatusCd, unlimitedResults(), permitFeeBalanceTypeCd);
			if (permits.size() == 0 && fromFacility.equals("false")) {
				DisplayUtil.displayInfo("There are no Permits that match the search criteria.");
			}
			DisplayUtil.displayHitLimit(permits.size());
			hasSearchResults = true;
			ret = "permitSearch";
		} catch (RemoteException ex) {
			DisplayUtil.displayError("search failed");
			handleException(ex);
		}
		return ret;
	}

	/**
	 * @return
	 */
	public final String reset() {
		legacyPermitNumber = null;
		permitNumber = null;
		facilityID = null;
		facilityName = null;
		permits = null;
		permitType = null;
		permitReason = null;
		permitGlobalStatusCd = null; // PermitGlobalStatusDef.ISSUED_FINAL;
		permitLevelStatusCd = null;
		applicationNumber = null;
		dateBy = null; // PermitSQLDAO.EFFECTIVE_DATE;
		euId = null;
		permitEUStatusCd = null;
		// Calendar now = new GregorianCalendar();
		// now.setTimeInMillis(System.currentTimeMillis());
		// now.add(Calendar.YEAR, -1);
		beginDt = null; // new Timestamp(now.getTimeInMillis());
		endDt = null;
		newPemitIssuedDraft = false;
		newPermitDraftIssueDate = null;
		newPermitFinalIssueDate = null;
		newPermitEffectiveDate = null;
		newPermitExpirationDate = null;
		draftFileToUpload = null;
		draftFileInfo = null;
		finalFileToUpload = null;
		finalFileInfo = null;
		sobFileToUpload = null;
		sobFileInfo = null;
		cmpId = null;
		newPermitDescription = null;

		hasSearchResults = false;
		standardList = true;
		NSRBillingList = false;
		permitFeeBalanceTypeCd = PermitFeeBalanceTypeDef.ALL_BALANCES;
		
		return SUCCESS;
	}

	/**
	 * Copy input object into this object. 2598
	 */
	public final void copy(PermitSearch ps) {
		setLegacyPermitNumber(ps.getLegacyPermitNumber());
		setPermitNumber(ps.getPermitNumber());
		setFacilityID(ps.getFacilityID());
		setFacilityName(ps.getFacilityName());
		setPermitType(ps.getPermitType());
		setPermitReason(ps.getPermitReason());
		setPermitLevelStatusCd(ps.getPermitLevelStatusCd());
		setPermitGlobalStatusCd(ps.getPermitGlobalStatusCd());
		setApplicationNumber(ps.getApplicationNumber());
		setDateBy(ps.getDateBy());
		setEuId(ps.getEuId());
		setBeginDt(ps.getBeginDt());
		setEndDt(ps.getEndDt());
		setCmpId(ps.getCmpId());
	}

	/**
	 * @return
	 */
	public final void createNewPermit(ActionEvent event) {
		
		// copy file upload information so it isn't lost
		if (draftFileInfo == null && draftFileToUpload != null) {
			draftFileInfo = new UploadedFileInfo(draftFileToUpload);
		}
		if (finalFileInfo == null && finalFileToUpload != null) {
			finalFileInfo = new UploadedFileInfo(finalFileToUpload);
		}
		if (sobFileInfo == null && sobFileToUpload != null) {
			sobFileInfo = new UploadedFileInfo(sobFileToUpload);
		}

		if (newPermitType != null) {
			PermitDetail permitDetail = (PermitDetail) FacesUtil
					.getManagedBean("permitDetail");

			FacilityProfile facilityProfile = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");

			int ok = permitDetail.newPermit(newPermitType.getFirst(), legacyPermitNumber,
					newPermitNumber, newPermitType.getSecond(),
					newPemitIssuedDraft, facilityProfile.getFacility(),
					newPermitDraftIssueDate, newPermitFinalIssueDate,
					newPermitEffectiveDate, newPermitExpirationDate, newPermitDescription,
					draftFileInfo, finalFileInfo, sobFileInfo);
			
			if (ok < 0) {
				// temporary fix
				if (draftFileInfo != null && draftFileInfo.getFileName() == null){
					draftFileInfo = null;
				}
				if (finalFileInfo != null && finalFileInfo.getFileName() == null){
					finalFileInfo = null;
				}
				if (sobFileInfo != null && sobFileInfo.getFileName() == null){
					sobFileInfo = null;
				}
				DisplayUtil.displayError("Cannot create permit");
			} else {
				permitDetail.createNewLegacyPermit();
				SimpleMenuItem.setDisabled("menuItem_permitDetail", false);
				popupRedirectOutcome = PermitValidation.PERMIT_PROFILE_OUTCOME;
				newPermitType = null;
				newPermitNumber = null;
				newPemitIssuedDraft = false;
				newPermitDraftIssueDate = null;
				newPermitFinalIssueDate = null;
				newPermitEffectiveDate = null;
				newPermitExpirationDate = null;
				draftFileToUpload = null;
				draftFileInfo = null;
				finalFileToUpload = null;
				finalFileInfo = null;
				sobFileToUpload = null;
				sobFileInfo = null;
				newPermitDescription = null;
				FacesUtil.returnFromDialogAndRefresh();
			}
		} else {
			DisplayUtil.displayError("Please select a permit type");
		}
	}

	public final void cancelNewPermit(ActionEvent actionEvent) {
		newPermitType = null;
		newPermitNumber = null;
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public final List<SelectItem> getPermitGlobalStatusDefs() {
		return FindPermitGlobalStatusDefs(getParent().getValue(), permitType);
	}

	public static List<SelectItem> FindPermitGlobalStatusDefs(
			Object parentValue, String permitType) {
		// override dummy 'FEPTIO' entry with the real permit type
		// FEPTIO does not apply for IMPACT
		//if (FEPTIO.equals(permitType)) {
		//	permitType = PermitTypeDef.NSR;
		//}
		List<SelectItem> ret = PermitGlobalStatusDef.getData().getItems()
				.getItems(parentValue);
		
		
		// TO DO
		// Temporary fix until database can be updated ... skip PP and PPP if permit type is NSR.
		if (permitType != null
				&& (PermitTypeDef.NSR.equalsIgnoreCase(permitType))) {
			List<SelectItem> tr = new ArrayList<SelectItem>();
			for (SelectItem s : ret) {
				if (PermitGlobalStatusDef.ISSUED_PP.equals(s.getValue())
						//|| PermitGlobalStatusDef.ISSUED_PPP
						//		.equals(s.getValue())
				)
					continue;

				tr.add(s);
			}
			ret = tr;
		}
		
		// TO DO
		// Temporary fix ... skip PPP if permit type is not set OR if permit type is TV.
		/*
		if (permitType == null || (permitType != null
				&& (PermitTypeDef.TV_PTO.equalsIgnoreCase(permitType)))) {
			List<SelectItem> tr = new ArrayList<SelectItem>();
			for (SelectItem s : ret) {
				if (PermitGlobalStatusDef.ISSUED_PPP
								.equals(s.getValue())
				)
					continue;

				tr.add(s);
			}
			ret = tr;
		}
		*/
		return ret;
	}

	public final List<Permit> getPermits() {
		return permits;
	}

	public final String getFromFacility() {
		return fromFacility;
	}

	public final void setFromFacility(String fromFacility) {
		this.fromFacility = fromFacility;
	}
	
	public final String getPermitLevelStatusCd() {
		return permitLevelStatusCd;
	}
	
	public final void setPermitLevelStatusCd(String permitLevelStatusCd) {
		this.permitLevelStatusCd = permitLevelStatusCd;
	}
	
	/**
	 * @return
	 */
	public final String getPermitNumber() {
		return permitNumber;
	}

	/**
	 * @param permitNumber
	 */
	public final void setPermitNumber(String permitNumber) {
		if (permitNumber != null && permitNumber.equals("")) {
			permitNumber = null;
		}
		this.permitNumber = permitNumber;
	}

	/**
	 * @return
	 */
	public final String getLegacyPermitNumber() {
		return legacyPermitNumber;
	}

	/**
	 * @param permitNumber
	 */
	public final void setLegacyPermitNumber(String legacyPermitNumber) {
		if (legacyPermitNumber != null && legacyPermitNumber.equals("")) {
			legacyPermitNumber = null;
		}
		this.legacyPermitNumber = legacyPermitNumber;
	}

	/**
	 * @return
	 */
	public final String getFacilityID() {
		return facilityID;
	}

	/**
	 * @param facilityID
	 */
	public final void setFacilityID(String facilityID) {
		if (facilityID != null && facilityID.equals("")) {
			facilityID = null;
		}
		this.facilityID = facilityID;
	}

	/**
	 * @return
	 */
	public final String getFacilityName() {
		return facilityName;
	}

	/**
	 * @param facilityName
	 */
	public final void setFacilityName(String facilityName) {
		if (facilityName != null && facilityName.equals("")) {
			facilityName = null;
		}
		this.facilityName = facilityName;
	}

	/**
	 * @return
	 */
	public final boolean getHasSearchResults() {
		return hasSearchResults;
	}

	/**
	 * @return
	 */
	public final Pair<Class<? extends Permit>, Boolean> getNewPermitType() {
		return newPermitType;
	}

	/**
	 * @param newPermitType
	 */
	public final void setNewPermitType(
			Pair<Class<? extends Permit>, Boolean> newPermitType) {
		this.newPermitType = newPermitType;
	}

	/**
	 * @param applicationNumber
	 */
	public final void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	/**
	 * @return
	 */
	public final String getApplicationNumber() {
		return applicationNumber;
	}

	/**
	 * @return
	 */
	public final String getPermitType() {
		return permitType;
	}

	/**
	 * @param permitType
	 */
	public final void setPermitType(String permitType) {
		this.permitType = permitType;
	}

	/**
	 * @return
	 */
	public final String getPermitGlobalStatusCd() {
		return permitGlobalStatusCd;
	}

	/**
	 * @param permitStatusCd
	 */
	public final void setPermitGlobalStatusCd(String permitGlobalStatusCd) {
		this.permitGlobalStatusCd = permitGlobalStatusCd;
	}

	/**
	 * @return
	 */
	public final LinkedHashMap<String, String> getPermitStatus() {
		return permitStatus;
	}

	/**
	 * @param permitStatus
	 */
	public final void setPermitStatus(LinkedHashMap<String, String> permitStatus) {
		this.permitStatus = permitStatus;
	}

	/**
	 * @return
	 */
	public final String getPermitReason() {
		return permitReason;
	}

	/**
	 * @param permitReason
	 */
	public final void setPermitReason(String permitReason) {
		this.permitReason = permitReason;
	}

	/*
	 * To minimize session size, to be used in the future...
	 */
	/**
	 * 
	 * private void shrink() { if (hasSearchResults) { permits = null;
	 * resultsWrapper.setWrappedData(permits); } }
	 * 
	 * private void restore() { if (hasSearchResults) { search(); } }
	 */

	/**
	 * @return
	 */
	public final List<SelectItem> getPermitReasons() {
		// override dummy 'FEPTIO' entry with the real permit type
		//if (FEPTIO.equals(permitType)) {
		//	permitType = PermitTypeDef.NSR;
		//}
		List<SelectItem> ret = PermitReasonsDef.getPermitReasons(permitType,
				null);
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}

	/**
	 * @return
	 */
	public final List<SelectItem> getAllPermitReasons() {
		return PermitReasonsDef.getAllPermitReasons();
	}

	public final List<SelectItem> getPermitTypes() {
		List<SelectItem> permitTypes = new ArrayList<SelectItem>();
		//permitTypes.add(new SelectItem(FEPTIO, FEPTIO));
		for (SelectItem si : PermitTypeDef.getData().getItems()
				.getItems(permitType, true)) {
			//if (si.getValue().equals(PermitTypeDef.RPE)
			//		|| si.getValue().equals(PermitTypeDef.RPR))
			if (!si.getValue().equals(PermitTypeDef.NSR)
						&& !si.getValue().equals(PermitTypeDef.TV_PTO))
				continue;

			permitTypes.add(si);
		}

		return permitTypes;
	}

	/**
	 * @return
	 */
	public final LinkedHashMap<String, String> getPermitDateBy() {
		return buildPermitDateBy(permitType);
	}

	/**
	 * @param permitType
	 * @return
	 */
	public static LinkedHashMap<String, String> buildPermitDateBy(
			String permitType) {
		LinkedHashMap<String, String> permitDateBy = new LinkedHashMap<String, String>();
		
		if (Utility.isNullOrEmpty(permitType) || (!Utility.isNullOrEmpty(permitType)
				&& (PermitTypeDef.TV_PTO.equalsIgnoreCase(permitType)))) {
			permitDateBy.put("Effective Date", PermitSQLDAO.EFFECTIVE_DATE);
		}
		
		permitDateBy.put("Expiration Date", PermitSQLDAO.EXPIRATION_DATE);
		
		if (Utility.isNullOrEmpty(permitType) || (!Utility.isNullOrEmpty(permitType)
				&& (PermitTypeDef.TV_PTO.equalsIgnoreCase(permitType)))) {
			permitDateBy.put("Permit Basis Date", PermitSQLDAO.PERMIT_BASIS_DATE);
		}
		
		permitDateBy.put("Public Notice Date",
				PermitSQLDAO.PUBLIC_NOTICE_PUBLISH_DATE);
		
		if (Utility.isNullOrEmpty(permitType) || (!Utility.isNullOrEmpty(permitType)
				&& (PermitTypeDef.TV_PTO.equalsIgnoreCase(permitType)))) {
			// IMPACT does not support PPP Issuance/Publication
			//permitDateBy.put("PPP Issuance Date",
			//		PermitSQLDAO.PPP_ISSUANCE_DATE);
			permitDateBy.put("PP Publication Date", PermitSQLDAO.PP_ISSUANCE_DATE);
		}
		
		if (Utility.isNullOrEmpty(permitType) || (!Utility.isNullOrEmpty(permitType)
				&& (PermitTypeDef.NSR.equalsIgnoreCase(permitType)))) {
			permitDateBy.put("Permit Sent Out Date", PermitSQLDAO.PERMIT_SENT_OUT_DATE);
			permitDateBy.put("Last Invoice Ref. Date", PermitSQLDAO.NSR_PERMIT_LAST_INVOICE_REF_DATE);
		}
		
		permitDateBy.put("Final Issuance Date",
				PermitSQLDAO.FINAL_ISSUANCE_DATE);
		
		if (!Utility.isNullOrEmpty(permitType)
				&& (PermitTypeDef.TV_PTO.equalsIgnoreCase(permitType))) {
			permitDateBy.put("Rescission Date",
					PermitSQLDAO.PTV_PERMIT_RECISSION_DATE);
		} else if (!Utility.isNullOrEmpty(permitType)
				&& (PermitTypeDef.NSR.equalsIgnoreCase(permitType))) {
			permitDateBy.put("Rescission Date",
					PermitSQLDAO.NSR_PERMIT_RECISSION_DATE);
		}
		
		return permitDateBy;
	}

	public final String getDateBy() {
		return dateBy;
	}

	public final void setDateBy(String dateBy) {
		if (dateBy == null || (dateBy != null && dateBy.trim().length() == 0)) {
			beginDt = null;
			endDt = null;
			dateBy = null;
		}
		this.dateBy = dateBy;
	}

	public final Timestamp getBeginDt() {
		return beginDt;
	}

	public final void setBeginDt(Timestamp beginDt) {
		this.beginDt = beginDt;
	}

	public final Timestamp getEndDt() {
		return endDt;
	}

	public final void setEndDt(Timestamp endDt) {
		this.endDt = endDt;
	}

	public void restoreCache() {
		// submitSearch();
	}

	public void clearCache() {
		// if (permits != null) {
		// permits.clear();
		// permits = null;
		// }
		//
		// if (permitStatus != null) {
		// permitStatus.clear();
		// permitStatus = null;
		// }
		//
		// hasSearchResults = false;
	}

	public final String refresh() {
		String ret = "permitSearch";
		if (backup != null) {
			copy(backup);
			backup = null;
			search();
		}
		return ret;
	}
	
	public final String setBulkOpToGenerateNSRInvoice() {
		BulkOperationsCatalog bOpCat = (BulkOperationsCatalog)FacesUtil.getManagedBean("bulkOperationsCatalog");
	    // do same initialization that Tools-->DocumentGeneration would have done.
	    bOpCat.setFacilityId(null);
	    bOpCat.setGenerateNSRInvoice(true);
	    // navigate to jsp page for Tools-->DocumentGeneration
	    String rtn = bOpCat.setBulkOpToDGEN();
		
		return rtn;
	}

	/**
	 * @return the euId
	 */
	public final String getEuId() {
		return euId;
	}

	/**
	 * @param euId
	 *            the euId to set
	 */
	public final void setEuId(String euId) {
		this.euId = euId;
	}

	/**
	 * @return the newPermitNumber
	 */
	public final String getNewPermitNumber() {
		return newPermitNumber;
	}

	/**
	 * @param newPermitNumber
	 *            the newPermitNumber to set
	 */
	public final void setNewPermitNumber(String newPermitNumber) {
		this.newPermitNumber = newPermitNumber;
	}

	public final PermitSearch getBackup() {
		return backup;
	}

	public final void setBackup(PermitSearch backup) {
		this.backup = backup;
	}

	public final String getPermitEUStatusCd() {
		return permitEUStatusCd;
	}

	public final void setPermitEUStatusCd(String permitEUStatusCd) {
		this.permitEUStatusCd = permitEUStatusCd;
	}

	public final List<SelectItem> getPermitStatusCds() {
		return PermitStatusDef.getData().getItems()
				.getItems(permitEUStatusCd, true);
	}

	public final boolean isNewPemitIssuedDraft() {
		return newPemitIssuedDraft;
	}

	public final void setNewPemitIssuedDraft(boolean newPemitIssuedDraft) {
		this.newPemitIssuedDraft = newPemitIssuedDraft;
	}

	public final Timestamp getNewPermitDraftIssueDate() {
		return newPermitDraftIssueDate;
	}

	public final void setNewPermitDraftIssueDate(
			Timestamp newPermitDraftIssueDate) {
		this.newPermitDraftIssueDate = newPermitDraftIssueDate;
	}

	public final Timestamp getNewPermitFinalIssueDate() {
		return newPermitFinalIssueDate;
	}

	public final void setNewPermitFinalIssueDate(
			Timestamp newPermitFinalIssueDate) {
		this.newPermitFinalIssueDate = newPermitFinalIssueDate;
	}

	public final Timestamp getNewPermitEffectiveDate() {
		return newPermitEffectiveDate;
	}

	public final void setNewPermitEffectiveDate(Timestamp newPermitEffectiveDate) {
		this.newPermitEffectiveDate = newPermitEffectiveDate;
	}

	public final Timestamp getNewPermitExpirationDate() {
		return newPermitExpirationDate;
	}

	public final void setNewPermitExpirationDate(
			Timestamp newPermitExpirationDate) {
		this.newPermitExpirationDate = newPermitExpirationDate;
	}

	public final UploadedFile getDraftFileToUpload() {
		return draftFileToUpload;
	}

	public final void setDraftFileToUpload(UploadedFile draftFileToUpload) {
		this.draftFileToUpload = draftFileToUpload;
	}

	public final UploadedFileInfo getDraftFileInfo() {
		return draftFileInfo;
	}

	public final void setDraftFileInfo(UploadedFileInfo draftFileInfo) {
		this.draftFileInfo = draftFileInfo;
	}

	public final UploadedFile getFinalFileToUpload() {
		return finalFileToUpload;
	}

	public final void setFinalFileToUpload(UploadedFile finalFileToUpload) {
		this.finalFileToUpload = finalFileToUpload;
	}

	public final UploadedFileInfo getFinalFileInfo() {
		return finalFileInfo;
	}

	public final void setFinalFileInfo(UploadedFileInfo finalFileInfo) {
		this.finalFileInfo = finalFileInfo;
	}
	
	public UploadedFile getSobFileToUpload() {
		return sobFileToUpload;
	}

	public void setSobFileToUpload(UploadedFile sobFileToUpload) {
		this.sobFileToUpload = sobFileToUpload;
	}

	public UploadedFileInfo getSobFileInfo() {
		return sobFileInfo;
	}

	public void setSobFileInfo(UploadedFileInfo sobFileInfo) {
		this.sobFileInfo = sobFileInfo;
	}

	public final String getCmpId() {
		return cmpId;
	}

	public final void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public String getNewPermitDescription() {
		return newPermitDescription;
	}

	public void setNewPermitDescription(String newPermitDescription) {
		this.newPermitDescription = newPermitDescription;
	}
	
	public boolean isStandardList() {
		return standardList;
	}

	public void setStandardList(boolean standardList) {
		this.standardList = standardList;
	}

	public boolean isNSRBillingList() {
		return NSRBillingList;
	}

	public void setNSRBillingList(boolean NSRBillingList) {
		this.NSRBillingList = NSRBillingList;
	}
	
	public String getPermitListType() {
		String ret = null;
		if(isStandardList()) {
			ret = STANDARD_LIST;
		}else if(isNSRBillingList()) {
			ret = NSR_BILLING_LIST;
		}else {
			ret = "Unknown";
		}
		
		return ret;
	}
	
	public String getPermitFeeBalanceTypeCd() {
		return permitFeeBalanceTypeCd;
	}

	public void setPermitFeeBalanceTypeCd(String permitFeeBalanceTypeCd) {
		this.permitFeeBalanceTypeCd = permitFeeBalanceTypeCd;
	}

	public final List<SelectItem> getPermitFeeBalanceTypeDefs(){
		return PermitFeeBalanceTypeDef.getData().getItems().getAllItems();
	}

}

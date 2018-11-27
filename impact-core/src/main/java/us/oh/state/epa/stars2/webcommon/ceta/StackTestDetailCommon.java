package us.oh.state.epa.stars2.webcommon.ceta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.common.util.Logger;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.ceta.FceDetail;
import us.oh.state.epa.stars2.bo.EnforcementActionService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dao.ceta.FullComplianceEvalSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EuDetails;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestVisitDate;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedEmissionsUnit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedScc;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.StAttachmentTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/*
 * Scheme to handle multiple EUs and one Stack Test Method.
 * Not yet decided about SCC--whether the same for all EUs or different.
 * 
 * The database will contain only StackTestedPollutant rows which will repeat the EU and SCC ID 
 * for each pollutant row.
 * Internally in this bean, we maintain that table as well as the 
 * table of EUs (StackTest.testedEmissionsUnits) and table of
 * tested pollutants StackTest.testedPollutants.
 * 
 * Upon reading the stack test, both testedEmissionsUnits and testedPollutants (including
 * test results) are
 * created by processing the StackTestedPollutant rows.
 * 
 * Within the web interface, the user changes which EUs/SCC and which pollutants from the
 * displayed tables testedEmissionsUnits & testedPollutants and these changes make the
 * appropriate changes to the displayed pollutant table where the columns for EU/SCC and pollutant
 * are not updatable, deletable or addable.  The only things that can be done is enter/change the
 * permitted & tested rates and the AFS information.
 */

@SuppressWarnings("serial")
public abstract class StackTestDetailCommon extends TaskBase implements IAttachmentListener {

	protected Integer id;
	protected Facility facility;
	// private boolean facilityDetail = false;// have we performed
	// retrieveFacilityProfile()?
	private Facility curFacility;
	private String facilityIdForFceReassign;
	protected StackTest stackTest;
	private ArrayList<TestVisitDate> oldDates = new ArrayList<TestVisitDate>();
	EmissionProcess epNone = new EmissionProcess(); // used to avoid null
	protected boolean validatedSuccessfully = false;
	private boolean editable = false; // in edit mode or read mode
	private String providedScc; // Scc provided when profile does not have
								// correct one.
	private List<ControlEquipment> controlEquips = new ArrayList<ControlEquipment>();
	private ControlEquipment controlEquip;

	private boolean showMemoEditable = false; // true only when not in edit mode, user
										// allowed to edit but already submitted
	protected boolean memoEditable = false; // true when user can edit only Memo field.

	protected Timestamp reminderDate;
	private Timestamp tomorrowsDate;
	private boolean chgFceAssignFlag = false;
	private TableSorter allFCEs = new TableSorter();
	protected boolean error = false; // blank jsp page when an error occurs
	protected boolean newClone = false; // not yet saved.
	private Integer origStackTestId; // the test that was cloned.
	protected boolean newTest = false; // For new test or new clone--not yet saved
	private boolean disableDelete = false;
	private String disableDeleteMsg;
	private String afsWarning;
	protected ArrayList<EuDetails> euDetails;
	protected EmissionUnit selectedEu; // set by the jsp page to select what tested
	private String selectedScc; // To support EUs with no process
	protected EmissionProcess selectedProcess; // set by the jsp page to select what
										// tested
	// private List<TestedScc> testedSccs;
	private String selectedEpaEmuId; // Set from JSP to determine which EU
										// selected to edit SCC IDs
	private TestedEmissionsUnit selectedTestedEu; // the one located by
													// selectedEpaEmuId
	// private boolean selectEuOk; // determines whether can select EU or EU/SCC
	// combination.

	static public final String notInProfile = "(not-in-profile)";
	private HashMap<String, SccCode> sccCodes = new HashMap<String, SccCode>();

	protected List<Document> testDocuments; // used for print/download
	private List<Document> testAttachments; // used for print/download

	private EnforcementActionService enforcementActionService;
	private FacilityService facilityService;
	private FullComplianceEvalService fullComplianceEvalService;
	private InfrastructureService infrastructureService;
	private StackTestService stackTestService;
	private ReadWorkFlowService workFlowService;

	private boolean newNote;
	private StackTestNote tempComment;
	private StackTestNote modifyComment;
	private boolean noteReadOnly;
	private static final String COMMENT_DIALOG_OUTCOME = "dialog:stackTestNoteDetail";
	protected static final String STACK_TEST = "ceta.stackTestDetail";
	private boolean cloning = false;
	private boolean noEuDetailsToSelect = true; // used to display a message
												// when no Eu/SCC in
												// facility inventory.
	private boolean allEuDetailsSelected = false;
	private boolean staging = false;

	private boolean editMode = false;

	private Boolean hideTradeSecret;
	
	private boolean viewOnly = true;             // used to set Stack Test readOnly if
	                                             // accessed from the Stack Test List on
	                                             // the portal
	
	private String popupRedirectOutcome;
	
	/*
	 * The following ID's must match those used in StackTestBO.validateXXX()
	 */
	
	protected static final String STACKTEST_REFERENCE = "stacktest";
	
	//protected static final String STDETAIL_MENU_MANAGED_BEAN = "menuItem_stackTestDetail";

	public boolean isCloning() {
		return cloning;
	}

	public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}
	
	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}
	
	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(
			InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public EnforcementActionService getEnforcementActionService() {
		return enforcementActionService;
	}

	public void setEnforcementActionService(EnforcementActionService enforcementActionService) {
		this.enforcementActionService = enforcementActionService;
	}

	public String from2ndLevelMenu() {
		setFromTODOList(false);
		return submitStackTestInternal(schemaFlag());
	}

	/*
	 * OBSOLETE - STARS2 only public String editSccs() { if
	 * (!firstButtonClick()) { // protect from multiple clicks return null; } if
	 * (stackTest.getVisitDates() == null || stackTest.getVisitDates().size() ==
	 * 0 || stackTest.getVisitDates().get(0).getTestDate() == null) {
	 * DisplayUtil .displayError(
	 * "First provide Test Date(s)--needed to determine if SCCs were active for the year of the tests."
	 * ); return null; } // create testedSccs list testedSccs = new
	 * ArrayList<TestedScc>(); // Locate selected EU String ss = "";
	 * selectedTestedEu = locateTestedEu(selectedEpaEmuId,
	 * selectedProcess.getSccId()); if (selectedTestedEu != null) { ss =
	 * selectedTestedEu.getSccs(); }
	 * 
	 * testedSccs = TestedScc.buildTestedSccTable(ss); clearButtonClicked();
	 * return "dialog:changeEmissionTestedSccs"; }
	 * 
	 * public void sccChangesDone() { stackTest.reCalTests(); StackTests
	 * stackTests = (StackTests) FacesUtil .getManagedBean("stackTests");
	 * stackTests.setPopupRedirectOutcome("ceta.stackTestDetail");
	 * FacesUtil.returnFromDialogAndRefresh(); }
	 */

	private TestedEmissionsUnit locateTestedEu(String epaEmuId, String sccID) {
		for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
			if (teu.getEpaEmuId().equals(epaEmuId)
					&& teu.getSccs().equals(sccID)) {
				return teu;
			}
		}
		return null;
	}

	/*
	 * OBSOLETE for IMPACT // convert sccTable back to string String
	 * convertSccsToString() { Collections.sort(testedSccs); StringBuffer sb =
	 * new StringBuffer(); for (TestedScc tscc : testedSccs) { if (sb.length() >
	 * 0) sb.append(" "); sb.append(tscc.getSccId()); } return sb.toString(); }
	 * 
	 * 
	 * public List<TestedScc> getTestedSccs() { if (testedSccs == null)
	 * testedSccs = new ArrayList<TestedScc>(); return testedSccs; }
	 * 
	 * public void setTestedSccs(List<TestedScc> testedSccs) { this.testedSccs =
	 * testedSccs; }
	 * 
	 * 
	 * public boolean addTestedScc(TestedScc tscc) { // return true if not
	 * alreay included if (testedSccs == null) { testedSccs = new
	 * ArrayList<TestedScc>(); } boolean notAlreadyPresent = true; for
	 * (TestedScc u : testedSccs) { if
	 * (TestedScc.removeAllParens(u.getSccId()).equals(tscc.getSccId()))
	 * notAlreadyPresent = false; } if (notAlreadyPresent) testedSccs.add(tscc);
	 * return notAlreadyPresent; }
	 */

	/*
	 * OBSOLETE for IMPACT public void enterSccId() { Calendar cal =
	 * Calendar.getInstance();
	 * cal.setTimeInMillis(stackTest.getVisitDates().get(0).getTestDate()
	 * .getTime()); int testYear = cal.get(Calendar.YEAR); if (providedScc ==
	 * null || providedScc.trim().length() < 8) { DisplayUtil
	 * .displayError("The SCC Code entered must be 8 characters long."); return;
	 * } SccCode sccC = sccCodes.get(providedScc.trim()); if (sccC == null) {
	 * try { sccC = getInfrastructureService().retrieveSccCode(
	 * providedScc.trim()); if (sccC == null) {
	 * DisplayUtil.displayError("The SCC Code " + providedScc +
	 * " is not a valid code."); return; } sccCodes.put(sccC.getSccId(), sccC);
	 * } catch (RemoteException re) { handleException(re);
	 * DisplayUtil.displayError("Failed to find SCC " + providedScc.trim());
	 * return; } } String s = checkActive(sccC, testYear); if (s != null) {
	 * DisplayUtil.displayError("The SCC " + providedScc.trim() +
	 * " cannot be used because " + s); return; } boolean addedScc =
	 * addTestedScc(new TestedScc(providedScc.trim())); if (!addedScc) {
	 * DisplayUtil.displayWarning("The SCC ID " + providedScc +
	 * " is already included"); return; }
	 * 
	 * if (selectedTestedEu != null) {
	 * selectedTestedEu.setSccs(convertSccsToString()); } providedScc = null; }
	 * 
	 * public final void deleteSelectedSCCs() { if (testedSccs == null) return;
	 * Iterator<TestedScc> l = testedSccs.iterator(); StringBuffer sb = new
	 * StringBuffer(); while (l.hasNext()) { TestedScc tscc = l.next(); if (tscc
	 * == null) continue; if (!tscc.isSelected()) { if (sb.length() > 0)
	 * sb.append(" "); sb.append(tscc.getSccId()); } } if (selectedTestedEu !=
	 * null) { selectedTestedEu.setSccs(sb.toString()); } testedSccs =
	 * TestedScc.buildTestedSccTable(sb.toString()); }
	 */

	public String chgFceAssign() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		// Get all Inspections for facility
		try {
			FullComplianceEval[] all = getFullComplianceEvalService()
					.retrieveFceBySearch(facility.getFacilityId());
			if (all.length == 0) {
				DisplayUtil.displayInfo("Facility has no Inspections");
				return null;
			}
			List<FullComplianceEval> result = new ArrayList<FullComplianceEval>();
			if (getStackTest().getFceId() != null) {
				for (int i = 0; i < all.length; i++) {
					if (!getStackTest().getFceId().equals(all[i].getId())) {
						result.add(all[i]);
					}
				}
				allFCEs.setWrappedData(result);
			} else {
				allFCEs.setWrappedData(all);
			}
		} catch (RemoteException re) {
			handleException(re);
			error = true;
			setEditable(false);
			memoEditable = false;
		} finally {
			clearButtonClicked();
		}

		chgFceAssignFlag = true;
		return "dialog:fceReassignEmTst";
	}

	public String saveChgFceAssign() {
		Iterator<?> it = allFCEs.getTable().getSelectionState().getKeySet()
				.iterator();
		FullComplianceEval selectedRow = null;
		boolean ok = false;
		if (it.hasNext()) {
			Object obj = it.next();
			allFCEs.setRowKey(obj);
			selectedRow = (FullComplianceEval) allFCEs.getRowData();
			allFCEs.getTable().getSelectionState().clear();
			ok = true;
		}
		if (!ok) {
			DisplayUtil.displayError("Cancel or make an association selection");
			return null;
		}
		chgFceAssignFlag = false;
		stackTest.setFceId(selectedRow.getId());
		DisplayUtil.displayInfo("Stack Test associated with Inspection "
				+ selectedRow.getInspId());
		internalModifyStackTest(true, true);
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public void clearFceAssign() {
		chgFceAssignFlag = false;
		stackTest.setFceId(null);
		internalModifyStackTest(true, true);
		DisplayUtil
				.displayInfo("Stack Test not associated with any Inspection");
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void cancelChgFceAssign() {
		chgFceAssignFlag = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void cancel() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public StackTestDetailCommon() {
		super();
		this.setTag(ValidationBase.STACK_TEST_TAG);
	}

	public final String submitStackTest() {
		String rtn = null;
		if (!firstButtonClick()) { // protect from multiple clicks
			return rtn;
		}
		try {
			setFromTODOList(false);
			rtn = submitStackTestInternal(schemaFlag());
		} finally {
			clearButtonClicked();
		}
		return rtn;
	}

	public final void closePopup() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String requestDelete() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		disableDelete = false;
		try {
			//Integer[] enfIds = getEnforcementActionService()
			//		.getEnforcementWithDiscovery(
			//				AFSActionDiscoveryTypeDef.STACK_TEST,
			//				stackTest.getId());
			//if (enfIds.length > 0) {
			//	StringBuffer sb = new StringBuffer();
			//	for (Integer i : enfIds) {
			//		sb.append(i.toString() + " ");
			//	}
			//	disableDelete = true;
			//	disableDeleteMsg = "This stack test was not deleted because it is specified as the Discovery reason in the following Enforcemnt(s):  "
			//			+ sb.toString();
			//}
			afsWarning = null;
			for (StackTestedPollutant stp : stackTest.getTestedPollutants())
				if (stp.getAfsId() != null) {
					afsWarning = "If you delete this Stack Test, you will lose the fact that it is exported to AFS.";
					break;
				}
		//} catch (RemoteException re) {
		//	handleException(re);
			
			// Adding logic to prevent deletion of a stack test when a
			// compliance event is referencing it.
			if (stackTest.getAssocComplianceStatusEvents().size() > 0) {
				disableDelete = true;
				disableDeleteMsg = "You cannot delete this Stack Test while it has associated Compliance Status Event(s).";
			}
			if (stackTest.getInspectionsReferencedIn().size() > 0) {
				disableDelete = true;
				disableDeleteMsg = "You cannot delete this Stack Test while it is being referenced in other places.";
			}
		} finally {
			clearButtonClicked();
		}
		return "dialog:requestDelete";
	}

	public boolean getDisplayComplianceEventsTable() {
		return isDisableDelete() && !getStackTest().getAssocComplianceStatusEvents().isEmpty();
	}
	
	public boolean getDisplayReferencedInspectionsTable() {
		return isDisableDelete() && !getStackTest().getInspectionsReferencedIn().isEmpty();
	}
	
	public final String deleteEmissionsTest() {
		validatedSuccessfully = false;
		boolean ok = true;
		//try {
			//Integer[] enfIds = getEnforcementActionService()
			//		.getEnforcementWithDiscovery(
			//				AFSActionDiscoveryTypeDef.STACK_TEST,
			//				stackTest.getId());
			//if (enfIds.length > 0) {
			//	StringBuffer sb = new StringBuffer();
			//	for (Integer i : enfIds) {
			//		sb.append(i.toString() + " ");
			//	}
			//	DisplayUtil
			//			.displayError("This stack test was not deleted because it is specified as the Discovery reason in the following Enforcemnt(s):  "
			//					+ sb.toString());
			//	FacesUtil.returnFromDialogAndRefresh();
			//	return null;
			//}
		//} catch (RemoteException re) {
		//	handleException(re);
		//	ok = false;
		//}
		if (ok) {
			try {
				getStackTestService().deleteStackTest(facility, stackTest, schemaFlag(), true);
				DisplayUtil.displayInfo("Stack Test successfully deleted.");
				handleBadDetail();
				error = true; // There is no error but this blanks out the
								// screen
			} catch (DAOException e) {
				if (!e.prettyMsgIsNull()
						&& ((DAOException) e).getPrettyMsg().startsWith(
								FullComplianceEvalSQLDAO.visitsLockedMsg)) {
					DisplayUtil
							.displayError("Cannot Delete Stack Test Because "
									+ ((DAOException) e).getPrettyMsg());
					error = false;
					FacesUtil.returnFromDialogAndRefresh();
					return null;
				} else {
					handleException(e);

				}
				ok = false;
			} catch (RemoteException e) {
				handleException(e);

				ok = false;
			}
		}
		if (!ok) {
			DisplayUtil.displayInfo("Failed to delete Stack Test.");
			error = true;
		}
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public final String submitStackTestInternal(boolean readOnly) {
		newClone = false;
		newTest = false;
		cloning = false;
		validatedSuccessfully = false;
		error = false;
		// resetEuLookup();
		String ret = null;
		boolean ok = true;
		try {
			stackTest = null;
			testAttachments = null;
			testDocuments = null;
			stackTest = getStackTestService().retrieveStackTest(id, readOnly);
			if (stackTest == null) {
				ok = false;
				DisplayUtil.displayError("StackTest is not found with that number.");
				return null;
			}
			if (stackTest.getFpId() == null) {
				ok = false;
				DisplayUtil.displayError("Stack Test " + id
						+ " does  not specify an fpId.");
			}
			if (ok) {
				stackTest.reCalTests();
				facility = stackTest.getAssociatedFacility();
				initializeAttachmentBean();
				if (stackTest.getWitnesses().size() > 0)
					oldDates = copyDates(stackTest.getVisitDates()); // needed
																		// to
																		// compare
																		// against
																		// changes
				curFacility = facility; // first guess
				if (facility.getVersionId() != -1) {
					curFacility = getFacilityService().retrieveFacility(
							stackTest.getFpId());
					if (curFacility == null) {
						ok = false;
						DisplayUtil.displayError("FpId " + stackTest.getFpId()
								+ " for " + " Stack Test " + id
								+ " not located.");
					}
					if (ok) {
						curFacility = getFacilityService().retrieveFacility(
								curFacility.getFacilityId());
						if (curFacility == null) {
							ok = false;
							DisplayUtil.displayError("Current facility "
									+ curFacility.getFacilityId() + " for "
									+ " Stack Test " + id + " not located.");
						}
					}
				}
			}
			if (ok) {
				determineCeList();

				// populate pollutant rows with stack test

				if (stackTest != null
						&& stackTest.getAllMethodPollutants() != null) {
					ArrayList<StackTestedPollutant> newList = new ArrayList<StackTestedPollutant>();
					for (StackTestedPollutant stp : stackTest
							.getAllMethodPollutants()) {
						StackTestedPollutant newStp = new StackTestedPollutant(
								stackTest, stp.getEu(), stp);
						newStp.setSuperSelected(stp.isSelected());
						newList.add(newStp);
					}
					stackTest.setAllMethodPollutants(newList
							.toArray(new StackTestedPollutant[0]));
				}

				if (stackTest != null
						&& stackTest.getTestedPollutants() != null) {
					for (StackTestedPollutant stp : stackTest
							.getTestedPollutants()) {
						stp.setStackTest(stackTest);
					}
					TableSorter testedPollutantsWrapper = new TableSorter();
					testedPollutantsWrapper.setWrappedData(stackTest
							.getTestedPollutants());
					stackTest
							.setTestedPollutantsWrapper(testedPollutantsWrapper);
				}
			}
		} catch (RemoteException e) {
			ok = false;
			handleException(e);
		}
		if (ok) {
			if (!isPublicApp()) {
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_stackTestDetail"))
					.setDisabled(false);
			}
		        if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
		        	ret = "home.ceta.stackTestReportDetail";
		        }else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
		        	ret = "home.ceta.stackTestReportDetail"; 
		        }else if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
		        	ret = "ceta.stackTestDetail";
		        }
			
			validatedSuccessfully = stackTest.getValidated();
		}
		error = !ok;
		
		
		return ret;
	}

	public final String cloneEmissionsTest() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			validatedSuccessfully = false;
			setEditable(true);
			cloneStackTestInternal("cloned", EmissionsTestStateDef.DRAFT);
			return "ceta.stackTestDetail";
		} finally {
			clearButtonClicked();
		}
	}

	public final String newStackTest(Facility facility) {
		stackTest = new StackTest();
		error = false;
		stackTest.setFpId(facility.getFpId());
		try {
			// get complete profile
			facility = getFacilityService().retrieveFacilityProfile(
					facility.getFpId());
			if (facility == null) {
				DisplayUtil.displayError("Failed to read facility with FP ID "
						+ facility.getFpId());
				return null;
			}
			this.facility = facility; // TODO should check for null
			this.curFacility = facility;
			stackTest.setFacilityId(facility.getFacilityId()); // set Facility
																// Id to create
																// the
																// directories.
			initializeAttachmentBean();
			setEditable(true);
			newTest = true;
			populateCreatedBy(stackTest);
			return "ceta.stackTestDetail";
		} catch (RemoteException re) {
			DisplayUtil.displayError("Failed to read facility with FP ID "
					+ facility.getFpId());
			return null;
		}
	}

	public boolean isControlEquipsRows() {
		if (controlEquips != null
				&& stackTest.getTestedEmissionsUnits().size() > 0) {
			return controlEquips.size() > 0;
		}
		return true;
	}

	public boolean isControlEquipsMissing() {
		for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
			EmissionUnit eu = teu.getEu();
			if (eu != null) {
				ArrayList<TestedScc> sccList = TestedScc
						.buildTestedSccTable(teu.getSccs());
				for (TestedScc tScc : sccList) {
					EmissionProcess ep = eu.findProcess(tScc.getSccId());
					if (ep != null) {
						if (ep.getAllControlEquipments().size() > 0) {
							continue;
						} else {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	protected void determineCeList() {
		controlEquips.clear();
		for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
			EmissionUnit eu = teu.getEu();
			if (eu != null) {
				ArrayList<TestedScc> sccList = TestedScc
						.buildTestedSccTable(teu.getSccs());
				for (TestedScc tScc : sccList) {
					EmissionProcess ep = eu.findProcess(tScc.getSccId());
					if (ep != null) {
						for (ControlEquipment ce : ep.getAllControlEquipments()) {
							if (!controlEquips.contains(ce)) {
								controlEquips.add(ce);
							}
						}
					}
				}
			}
		}
	}

	public final void modifyStackTest() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			internalModifyStackTest(true, true);
		} finally {
			clearButtonClicked();
		}
	}

	protected final void internalModifyStackTest(boolean checkForDup,
			boolean reRead) {
		validatedSuccessfully = false; // require user to validate again before
										// submit
		boolean badValues = false;
		stackTest.upgradeToDraftBasedOnPollutants();
		ArrayList<ValidationMessage> vMsgs = new ArrayList<ValidationMessage>();
		HashSet<Timestamp> dates = new HashSet<Timestamp>();
		for (TestVisitDate tvd : stackTest.getVisitDates()) {
			if (tvd.getTestDate() == null) {
				vMsgs.add(new ValidationMessage("stDateTable",
						"Some Test Dates are blank.",
						ValidationMessage.Severity.ERROR, null));
				badValues = true;
				break;
			}
			if (!dates.add(tvd.getTestDate())) {
				vMsgs.add(new ValidationMessage("stDateTable",
						"There are duplicate Test Dates.",
						ValidationMessage.Severity.ERROR, null));
				badValues = true;
				break;
			}
		}

		HashSet<Integer> wits = new HashSet<Integer>();
		for (Evaluator e : stackTest.getWitnesses()) {
			if (!wits.add(e.getEvaluator())) {
				vMsgs.add(new ValidationMessage("stWitnessesTable",
						"There are duplicate Witnesses.",
						ValidationMessage.Severity.ERROR, null));
				badValues = true;
				break;
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Timestamp today = new Timestamp(cal.getTimeInMillis());

		if (stackTest.getDateReceived() != null) {
			if (today.before(stackTest.getDateReceived())) {
				vMsgs.add(new ValidationMessage("stRecDate",
						"Date Received cannot be in the future",
						ValidationMessage.Severity.ERROR, "dtRec"));
				badValues = true;

			}
		}
		if (stackTest.getDateEvaluated() != null) {
			if (today.before(stackTest.getDateEvaluated())) {
				vMsgs.add(new ValidationMessage("stEvalDate",
						"Date Reviewed cannot be in the future",
						ValidationMessage.Severity.ERROR, "dateEvaluatedDt"));
				badValues = true;

			}
		}
		

	
		if (stackTest.getDateReceived() != null
				&& stackTest.getDateEvaluated() != null) {
			Date recei=null;
			Date evaul=null;
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				recei = sdf.parse(stackTest.getDateReceived().toString());
				evaul = sdf.parse(stackTest.getDateEvaluated().toString());
			}catch(Exception e){
				Logger.fine("Exception: "+e.getMessage());
			}
			
			
			if((recei).compareTo(evaul)==0){
				Logger.fine("Both the Dates are same");
			}else{				
				if (stackTest.getDateEvaluated()
						.before(stackTest.getDateReceived())) {
					vMsgs.add(new ValidationMessage("stRecEvalDate",
							"Date Reviewed cannot be before Date Received",
							ValidationMessage.Severity.ERROR, "dateEvaluatedDt"));
					badValues = true;
				}
			}
		}

		if (stackTest.getDateReceived() != null
				&& stackTest.getVisitDates() != null) {
			for (TestVisitDate td : stackTest.getVisitDates()) {
				if (td.getTestDate() != null
						&& stackTest.getDateReceived().before(td.getTestDate())) {
					vMsgs.add(new ValidationMessage("stTestDate",
							"Date Received cannot be before Test Dates",
							ValidationMessage.Severity.ERROR, "dtRec"));
					badValues = true;
				}
			}
		}

		if (stackTest.getVisitDates() != null) {
			for (TestVisitDate td : stackTest.getVisitDates()) {
				if (td.getTestDate() != null && today.before(td.getTestDate())) {
					vMsgs.add(new ValidationMessage("stTestDate2",
							"Test Dates cannot be in the future",
							ValidationMessage.Severity.ERROR, "stDateTable"));
					badValues = true;
				}
			}
		}

		if (isInternalApp() && !isStars2Admin()) {
			// If date reviewed has a value, require a value for reviewer.
			// Require reviewer to be current user.
			if (stackTest.getDateEvaluated() != null && Utility.isNullOrZero(stackTest.getReviewer())
					&& !Utility.isNullOrEmpty(stackTest.getDateEvaluated()
							.toString())) {
				vMsgs.add(new ValidationMessage(
						"stReviewer1",
						"Since Date Results Reviewed has a value, Reviewer: value must be set.",
						ValidationMessage.Severity.ERROR, "reviewerChoice"));
				badValues = true;
			} else if (!Utility.isNullOrZero(stackTest.getReviewer())
					&& !stackTest.getReviewer().equals(
							InfrastructureDefs.getCurrentUserId())
							&& !stackTest.isLegacyFlag()) {
				vMsgs.add(new ValidationMessage("stReviewer2",
						"Reviewer: value must be set to the current user.",
						ValidationMessage.Severity.ERROR, "reviewerChoice"));
				badValues = true;
			}
			
			if ((stackTest.getDateEvaluated() == null || Utility
					.isNullOrEmpty(stackTest.getDateEvaluated().toString()))
					&& !Utility.isNullOrZero(stackTest.getReviewer())) {
				vMsgs.add(new ValidationMessage(
						"stRecEvalDate2",
						"Since Reviewer has a value, Date Results Reviewed must have a value.",
						ValidationMessage.Severity.ERROR, "dateEvaluatedDt"));
				badValues = true;
			}
		}
		
		// make sure this test is not duplicate with others
		Integer stId = null;
		String sameEu = null;
		String sameScc = null;
		String sameDate = null;
		if (stackTest.getStackTestMethodCd() != null
				&& stackTest.getVisitDates().size() > 0
				&& stackTest.getTestedEmissionsUnits().size() > 0) {
			// Just check if all values set
			StackTest[] otherTests = null;
			try {
				otherTests = getStackTestService()
						.retrieveStackTestsForValidate(
								facility.getFacilityId(),
								stackTest.getStackTestMethodCd());
			} catch (RemoteException re) {
				String s = "Failed on retrieveStackTestsForValidate("
						+ facility.getFacilityId() + ", "
						+ stackTest.getStackTestMethodCd() + ").";
				handleException(s, re);
			}
			if (otherTests != null) {
				for (StackTest st : otherTests) {
					if (st.getId().equals(stackTest.getId()))
						continue; // don't check against itself.
					if (st.getStackTestMethodCd() == null
							|| st.getVisitDates().size() == 0
							|| st.getTestedEmissionsUnits().size() == 0)
						continue;
					// See if test dates overlap
					boolean datesOverlap = false;
					dateLoop2: for (TestVisitDate tvd : stackTest
							.getVisitDates()) {
						if (tvd.getTestDate() == null)
							continue;
						for (TestVisitDate tvd2 : st.getVisitDates()) {
							if (tvd2.getTestDate() == null)
								continue;
							if (tvd.getTestDate().equals(tvd2.getTestDate())) {
								datesOverlap = true;
								sameDate = CetaBaseDB.getDateStr(tvd
										.getTestDate());
								break dateLoop2;
							}
						}
					}
					// See if EUs overlap
					boolean eusOverlap = false;
					euLoop2: for (TestedEmissionsUnit eu : stackTest
							.getTestedEmissionsUnits()) {
						if (eu.getEu() == null)
							continue;
						for (TestedEmissionsUnit eu2 : st
								.getTestedEmissionsUnits()) {
							if (eu2.getTestedEu() == null)
								continue;
							if (eu.getTestedEu().equals(eu2.getTestedEu())
									&& eu.getSccs().equals(eu2.getSccs())) {
								eusOverlap = true;
								sameEu = eu.getEpaEmuId();
								sameScc = eu.getSccs();
								break euLoop2;
							}
						}
					}
					if (datesOverlap && eusOverlap) {
						stId = st.getId();
						break;
					}
					// Start over since a single test must overlap this one.
					datesOverlap = false;
					eusOverlap = false;
				}
			}
		}
		if (stId != null) {
			vMsgs.add(new ValidationMessage(
					"testOverlap",
					"This Stack Test ovelapps with at least one other test ("
							+ stId
							+ ") by having the same test method, at least one EU/SCC in common ("
							+ sameEu + ")/(" + sameScc
							+ ") and at least one test date in common ("
							+ sameDate + ").",
					ValidationMessage.Severity.ERROR, "testOverlap"));
			badValues = true;
		}
		// check for deprecated test pollutants
		ValidationMessage.Severity sev = ValidationMessage.Severity.ERROR;
		if (isStars2Admin())
			sev = ValidationMessage.Severity.WARNING;
		for (StackTestedPollutant stp : stackTest.getAllMethodPollutants()) {
			if (!stp.isSelected())
				continue;
			if (stp.isDeprecated()) {
				badValues = true;
				vMsgs.add(new ValidationMessage(stp.getPollutantCd()
						+ "deprecated", "Test Method Pollutant "
						+ stp.getPollutantDsc() + " is no longer valid", sev,
						stp.getPollutantCd() + "deprecated"));
			}
		}

		if (badValues) {
			ValidationMessage[] vm = vMsgs.toArray(new ValidationMessage[0]);
			displayValidationMessages("", vm);
			return;
		}

		try {
			if (newTest) {
				stackTest = getStackTestService().createStackTest(facility,
						stackTest, schemaFlag());
				stackTest.reCalTests();
				stackTest.setValidated(false);
				initializeAttachmentBean(); // in case no Stack Test has yet
											// been visited.
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_stackTestDetail"))
						.setDisabled(false);
				if (newClone) {
					cloning = true;

					try {
						if (stackTest.getInspId() != null) {
							getStackTestService().createInspectionAssociation(
									stackTest.getId(), stackTest.getInspId());
						}

						for (StAttachment doc : stackTest.getAttachments()) {
							
							Attachment tsAttachment = null;
							FileInputStream tsFileInputStream = null;

							if (doc.getTradeSecretDocId() != null) {
								tsAttachment = new Attachment();
								String tsFilePath = doc
										.getTradeSecretDocURL()
										.replaceFirst(
												DocumentUtil
														.getFileStoreBaseURL(),
												DocumentUtil
														.getFileStoreRootPath());
								tsFileInputStream = new FileInputStream(
										tsFilePath);
								tsAttachment.setObjectId(stackTest.getId()
										.toString());
								tsAttachment.setFacilityID(stackTest
										.getFacilityId());
								tsAttachment.setSubPath("StackTest");
								tsAttachment.setLastModifiedBy(doc
										.getLastModifiedBy());
								tsAttachment.setLastModifiedTS(new Timestamp(
										System.currentTimeMillis()));
								tsAttachment.setUploadDate(doc
										.getLastModifiedTS());
								tsAttachment.setExtension(DocumentUtil
										.getFileExtension(tsFilePath));

							}
							FileInputStream docFileInputStream = new FileInputStream(
									DocumentUtil.getFileStoreRootPath()
											+ doc.getPath());
							doc.setObjectId(stackTest.getId().toString());
							doc.setSubPath("StackTest");
							doc.setAttachmentBasePath(null);
							doc.setDocumentID(null);
							getStackTestService().createStAttachment(stackTest,
									doc, docFileInputStream, tsAttachment,
									tsFileInputStream, schemaFlag());
						}

					} catch (Exception ioe) {
						DisplayUtil
								.displayError("A system error has occurred while cloning Stack Test. "
										+ "Please contact System Administrator.");
						logger.error(ioe.getMessage(), ioe);
					}

					Note[] stackTestNotes = stackTest.getStackTestNotes();

					for (Note note : stackTestNotes) {
						tempComment = new StackTestNote();
						tempComment.setUserId(getCurrentUserID());
						tempComment.setStackTestId(stackTest.getId());
						tempComment.setDateEntered(new Timestamp(System
								.currentTimeMillis()));
						tempComment.setNoteTypeCd(NoteType.DAPC);
						tempComment.setNoteTxt(note.getNoteTxt());
						tempComment.setNoteTypeCd(note.getNoteTypeCd());
						getStackTestService().createStackTestNote(tempComment);
					}

					DisplayUtil
							.displayInfo("Cloned Stack Test created successfully.");
				} else {
					DisplayUtil.displayInfo("Stack Test created successfully.");
				}

				setId(stackTest.getId());
				newClone = false;
				newTest = false;
			} else {
				Integer userId = InfrastructureDefs.getCurrentUserId();
				stackTest.setValidated(false);
				getStackTestService().modifyStackTest(facility, stackTest,
						oldDates, userId, schemaFlag());

				DisplayUtil.displayInfo("Stack Test updated successfully.");
			}
			if (reRead) {
				// note that the facility will not have changed.
				testDocuments = null;
				testAttachments = null;
				stackTest = getStackTestService().retrieveStackTest(
						stackTest.getId(), schemaFlag());
				stackTest.reCalTests();
			}
			if (stackTest.getWitnesses().size() > 0)
				oldDates = copyDates(stackTest.getVisitDates()); // needed to
																	// compare
																	// against
																	// future
																	// changes
		} catch (DAOException e) {
			String errPrefix;
			if (newTest && newClone) {
				errPrefix = "Failed to create cloned ";
			} else if (newTest) {
				errPrefix = "Failed to create ";
			} else {
				errPrefix = "Failed to update ";
			}
			if (!e.prettyMsgIsNull()
					&& ((DAOException) e).getPrettyMsg().startsWith(
							FullComplianceEvalSQLDAO.visitsLockedMsg)) {
				DisplayUtil.displayError(errPrefix + "Stack Test Because "
						+ ((DAOException) e).getPrettyMsg());
				return;
			} else {
				handleException(e);
			}
			error = true;
			if (!newTest && !newClone && error) {
				try {
					stackTest = getStackTestService().retrieveStackTest(
							stackTest.getId(), schemaFlag());
					if (stackTest == null) {
						DisplayUtil
								.displayError("Failed to update stack test. The stack test may no longer exist.");
						handleBadDetailAndRedirect();
						setEditable(false);
					} else {
						error = false;
						return;
					}
				} catch (DAOException de) {
					logger.error("Could not retrieve stack test : "
							+ stackTest.getStckId());
					DisplayUtil.displayError("Failed to update stack test "
							+ stackTest.getStckId()
							+ ". The stack test may no longer exist.");
					handleBadDetailAndRedirect();
					setEditable(false);
				} catch (RemoteException re) {
					logger.error("Could not retrieve stack test : "
							+ stackTest.getStckId());
					DisplayUtil.displayError("Failed to update stack test "
							+ stackTest.getStckId()
							+ ". The stack test may no longer exist.");
					handleBadDetailAndRedirect();
					setEditable(false);
				}
			} else {
				DisplayUtil.displayInfo(errPrefix + "Stack Test.");
				setEditable(false);
			}
			memoEditable = false;
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayInfo("Failed to update Stack Test.");
			error = true;
			setEditable(false);
			memoEditable = false;
		}
		setEditable(false);
		memoEditable = false;
		if (isInternalApp()) {
			updateLists();
		}
	}

	public String creacteBasePath(StackTest st) {
		String path = null;
		try {
			path = getStackTestDir(facility, st);
		} catch (Exception e) {
			logger.error("Exception creating file store directory " + path, e);
		}
		return path;
	}

	public String creacteDirectory(String path) {
		String newFilePath = null;
		try {
			newFilePath = File.separator + "Facilities" + File.separator
					+ facility.getFacilityId() + File.separator + path;
			DocumentUtil.mkDir(newFilePath);
		} catch (Exception e) {
			logger.error("Exception creating file store directory " + path, e);
		}
		return newFilePath;
	}

	private String getStackTestDir(Facility facility, StackTest st) {
		return File.separator + "StackTest" + File.separator + st.getId();
	}

	public static void populateCreatedBy(StackTest s) {
		Calendar cal = Calendar.getInstance();
		Timestamp today = new Timestamp(cal.getTimeInMillis());
		s.setCreatedDt(today);
		s.setCreatedById(InfrastructureDefs.getCurrentUserId());
	}

	protected final void cloneStackTestInternal(String msg, String newState) {
		origStackTestId = stackTest.getId();
		stackTest.clearFieldsAfterClone();
		stackTest.setEmissionTestState(newState);
		stackTest.setLegacyFlag(false);
		stackTest.setFceId(null); // Do not keep the Inspection association.
		stackTest.setId(null); // to create a new id
		try {
			// get complete profile
			if (facility.getVersionId() != -1) {
				String facilityId = facility.getFacilityId();
				facility = getFacilityService().retrieveFacility(facilityId);
				if (facility == null) {
					DisplayUtil.displayError("Failed to read facility "
							+ facilityId);
					return;
				}
				stackTest.setFpId(facility.getFpId());
				facility = getFacilityService().retrieveFacilityProfile(
						facility.getFpId());
				curFacility = facility;
			}
			setEditable(true);
			newClone = true; // true; No longer use this concept since created
								// here.
			newTest = true;
			populateCreatedBy(stackTest);
		} catch (RemoteException re) {
			DisplayUtil.displayError("Failed to read facility with FP ID "
					+ facility.getFpId());
		}
	}

	public final String newAssocStackTest(String facilityId, Integer fpId,
			Integer fceId) {
		stackTest = new StackTest();
		error = false;
		stackTest.setFpId(fpId);
		stackTest.setFacilityId(facilityId); // set facility Id to create
												// directories
		stackTest.setFceId(fceId);
		initializeAttachmentBean();
		try {
			// get complete profile
			facility = getFacilityService().retrieveFacility(facilityId);
			if (facility == null) {
				DisplayUtil.displayError("Failed to read facility "
						+ facilityId);
				return null;
			}
			facility = getFacilityService().retrieveFacilityProfile(
					facility.getFpId());
			curFacility = facility;
			stackTest.setFacilityId(facility.getFacilityId()); // set Facility
																// Id to create
																// the
																// directories.
			setEditable(true);
			newTest = true;
			populateCreatedBy(stackTest);
			return "ceta.stackTestDetail";
		} catch (RemoteException re) {
			DisplayUtil.displayError("Failed to read facility with FP ID "
					+ facility.getFpId());
			logger.error(
					"Failed to read facility with FP ID " + facility.getFpId(),
					re);
			return null;
		}
	}

	public final String toReminderState() {
		setTomorrowsDate();
		return "dialog:toMoveToReminderPopup";
	}

	public final String moveToReminderState() {
		if (stackTest.getReminderDate() == null
				|| stackTest.getReminderDate().before(tomorrowsDate)) {
			ValidationMessage[] vm = new ValidationMessage[1];
			vm[0] = new ValidationMessage("remDate",
					"Reminder Date must be in the future",
					ValidationMessage.Severity.ERROR, null);
			displayValidationMessages("", vm);
			return null;
		}
		stackTest.setEmissionTestState(EmissionsTestStateDef.REMINDER);
		stackTest.setLegacyFlag(false);
		stackTest.setVisitDates(new ArrayList<TestVisitDate>()); // clear test
																	// dates
		// update the stack test
		internalModifyStackTest(false, true);
		FacesUtil.returnFromDialogAndRefresh();
		return null;

	}

	public final String toSubmitState() {
		setTomorrowsDate();
		return "dialog:toStackTestSubmitPopup";
	}

	private void setTomorrowsDate() {
		// Want this to be today's date. Can specify a reminder for today.
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		tomorrowsDate = new Timestamp(cal.getTimeInMillis());
	}

	public final String finishSubmitState() {
		stackTest.setEmissionTestState(EmissionsTestStateDef.SUBMITTED);
		stackTest.setTestBeingSubmitted(true);
		stackTest.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
		internalModifyStackTest(true, true);
		DisplayUtil.displayInfo("Submit successful");

		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public List<ValidationMessage> validateStackTest() {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		ValidationMessage vm = null;
		if (stackTest.getVisitDates() != null
				&& stackTest.getVisitDates().size() > 0) {
			ArrayList<String> inactiveSccs = new ArrayList<String>();
			int testYear = 0;
			// if no test dates then validation will catch that.
			for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(stackTest.getVisitDates().get(0)
						.getTestDate().getTime());
				testYear = cal.get(Calendar.YEAR);
				ArrayList<TestedScc> sccList = TestedScc
						.buildTestedSccTable(teu.getSccs());
				Collections.sort(sccList);
				String scc = null;

				StringBuffer sb = new StringBuffer();
				for (TestedScc u : sccList) {
					if (sb.length() > 0)
						sb.append(" ");
					scc = TestedScc.removeAllParens(u.getSccId());
					sb.append(scc);

					SccCode sccC = sccCodes.get(scc);
					if (sccC == null) {
						try {
							sccC = getInfrastructureService().retrieveSccCode(
									scc);
							if (sccC == null) {
								String s = "The SCC Code " + scc
										+ " is not a valid code.";
								DisplayUtil.displayError(s);
								logger.error(s + " in Stack Test "
										+ stackTest.getId());
							} else {
								sccCodes.put(sccC.getSccId(), sccC);
							}
						} catch (RemoteException re) {
							handleException(re);
							DisplayUtil.displayError("Failed to find SCC "
									+ scc + " in Stack Test "
									+ stackTest.getId());

							messages.add(new ValidationMessage(
									"StackTest",
									"Unable to retrieve stack test. Please contact system administrator.",
									ValidationMessage.Severity.ERROR));
						}
					}
					String warning = checkActiveShort(sccC, testYear);
					if (warning != null) {
						sb.append(warning);
						if (!inactiveSccs.contains(scc))
							inactiveSccs.add(scc);
					}
				}
				teu.setSccs(sb.toString());
			}
			if (inactiveSccs.size() > 0) {
				StringBuffer sbInactive = new StringBuffer();
				for (String s : inactiveSccs) {
					sbInactive.append(s);
					sbInactive.append(" ");
				}
				vm = new ValidationMessage("stDateTable",
						"The following SCC IDs are inactive for the year of the test ("
								+ testYear + "): " + sbInactive.toString(),
						ValidationMessage.Severity.ERROR, null);

			}
		}
		
		boolean useReadonlyDB = false;
		if ((this.isViewOnly() && !isInternalApp()) || isInternalApp()) {
			useReadonlyDB = true;
		}
		try {
			 messages = getStackTestService()
					.validateStackTest(stackTest.getId(), vm, useReadonlyDB);
		} catch (RemoteException e) {
			handleException(
					"exception in stacktest "
							+ stackTest.getId(), e);
		} catch (ServiceFactoryException e) {
			handleException(new RemoteException(e.getMessage(), e));
		} finally {

		}

		boolean ok = true;

		List<ValidationMessage> msgs = new ArrayList<ValidationMessage>();
		for (ValidationMessage valMsg : messages) {
			if (valMsg.getReferenceID() == null) {
				valMsg.setReferenceID(ValidationBase.STACK_TEST_TAG + ":"
						+ stackTest.getId() + ":" + STACK_TEST);
			}

			msgs.add(valMsg);
		}

		return msgs;
	}

	public final void validate() {
		boolean ok = false;
		
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		try {
			// validate stack test
			List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
			messages = validateStackTest();
			
			ok = printValidationMessages(messages);

			if (ok) {
				validatedSuccessfully = true;
				Object close_validation_dialog = FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap()
						.get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
				if (close_validation_dialog != null
						&& (messages != null && messages.isEmpty())) {
					FacesUtil
							.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
					FacesContext.getCurrentInstance().getExternalContext()
							.getSessionMap()
							.remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
				}
			} else {
				validatedSuccessfully = false;
			}
			
			// update the stack test validated flag with validateSuccessfully
			// value.

			getStackTestService().setValidatedFlag(stackTest,
					validatedSuccessfully, schemaFlag());
		} catch (RemoteException e) {
			validatedSuccessfully = false;
			handleException(e);
		} finally {
			clearButtonClicked();
		}
	}
	
	private boolean printValidationMessages(
			List<ValidationMessage> validationMessages) {
		String refID;
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		for (ValidationMessage msg : validationMessages) {
			refID = msg.getReferenceID();
			if (!refID.startsWith(ValidationBase.FACILITY_TAG)) {
				if(msg.getProperty().equalsIgnoreCase("stackDetail")) {
					msg.setReferenceID(ValidationBase.STACK_TEST_TAG + ":"
							+ this.stackTest.getId().toString() + ":"
							+ msg.getProperty());
				} else {
					msg.setReferenceID(refID);
				}
				
			}
			valMessages.add(msg);
		}

		return AppValidationMsg.validate(valMessages, true);
	}

	static ArrayList<TestVisitDate> copyDates(List<TestVisitDate> dates) {
		ArrayList<TestVisitDate> copiedDates = new ArrayList<TestVisitDate>(
				dates.size());
		for (TestVisitDate tvd : dates) {
			copiedDates.add(new TestVisitDate(tvd.getTestDate()));
		}
		return copiedDates;
	}

	public final void doNothing() {
		// Just to have a button to pusth.
	}

	public String lookupFacility() {
		if (stackTest.getVisitDates() == null
				|| stackTest.getVisitDates().size() == 0
				|| stackTest.getVisitDates().get(0).getTestDate() == null) {
			DisplayUtil
					.displayError("First provide Test Dates (needed to determine if SCCs were active for the year of the tests)");
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(stackTest.getVisitDates().get(0).getTestDate()
				.getTime());
		int testYear = cal.get(Calendar.YEAR);
		// selectEuOk = stackTest.allSameSccs();
		euDetails = new ArrayList<EuDetails>();
		boolean included = false;
		noEuDetailsToSelect = true;
		try{			
			facility = getFacilityService().retrieveFacilityProfile(
				facility.getFpId(),isPortalApp());
		}catch(RemoteException re){
			Logger.fine(""+re.getStackTrace());
		}
		for (EmissionUnit eu : facility.getEmissionUnits()) {
			// boolean deadScc = false;
			boolean hasProcess = false;
			List<TestedEmissionsUnit> euIncluded = stackTest.locateTested(eu);
			for (EmissionProcess ep : eu.getEmissionProcesses()) {
				if (ep.getSccId() == null || ep.getSccId().length() == 0)
					continue;
				hasProcess = true;
				EuDetails details = new EuDetails(eu.getEpaEmuId(), eu,
						eu.getOperatingStatusCd(), eu.getEuShutdownDate(),
						eu.getAnyEuDesc(), ep.getSccId(), ep,
						ep.getEmissionProcessNm());
				// has it already been selected or cannot be selected?
				included = false;
				if (euIncluded != null) {
					for (TestedEmissionsUnit teu : euIncluded) {
						if (teu != null && teu.getSccs() != null
								&& teu.getSccs().contains(ep.getSccId())) {
							included = true;
						}
						// check SCC to see if currently valid.
					}
				}
				SccCode sccC = sccCodes.get(ep.getSccId());
				if (sccC == null) {
					try {
						sccC = getInfrastructureService().retrieveSccCode(
								ep.getSccId());
						sccCodes.put(sccC.getSccId(), sccC);
					} catch (RemoteException re) {
						handleException(re);
						DisplayUtil.displayError("Failed to find SCC "
								+ ep.getSccId() + " in EU " + eu.getEpaEmuId()
								+ " and process " + ep.getProcessId());
						return null;
					}
				}
				String s = checkActive(sccC, testYear);
				if (s != null) {
					details.setSccActiveInfo(s);
					// deadScc = true;
				}
				details.setAlreadySelected(included); // || deadScc);
				euDetails.add(details);
				setNoEuDetailsToSelect(false);
			}

			// If not included yet, must mean that there is no process for the
			// EU, so include a row for this EU and include text explaining
			// that it cannot be selected. In EuDetails.jsp, do not render the
			// checkbox based on the value of SCC id being equal to
			// "No Process".
			if (!hasProcess) {
				EuDetails details = new EuDetails(eu.getEpaEmuId(), eu,
						eu.getOperatingStatusCd(), eu.getEuShutdownDate(),
						eu.getAnyEuDesc(), "No Process", null,
						"Process must be added before EU can be selected.");
				euDetails.add(details);
			}
		}

		if (isNoEuDetailsToSelect()) {
			String infoMsg = "No EU/SCCs are available in the facility inventory to add to the Stack Test.";
			DisplayUtil.displayInfo(infoMsg);
			return null;
		} else if (isAllEuDetailsSelected()) {
			String infoMsg = "All available EU/SCCs are already included in the Stack Test.";
			DisplayUtil.displayInfo(infoMsg);
			return null;
		}

		return "dialog:selectEuScc";
	}

	private String checkActive(SccCode sccC, int testYear) {
		if (sccC.getCreatedYear() != null && testYear < sccC.getCreatedYear()) {
			return "Not Active until " + sccC.getCreatedYear();
		}
		if (sccC.getDeprecatedYear() != null
				&& testYear >= sccC.getDeprecatedYear()) {
			return "Became inactive in " + sccC.getDeprecatedYear();
		}
		return null;
	}

	private String checkActiveShort(SccCode sccC, int testYear) {
		if (sccC.getCreatedYear() != null && testYear < sccC.getCreatedYear()) {
			return "(valid-starting-" + sccC.getCreatedYear() + ")";
		}
		if (sccC.getDeprecatedYear() != null
				&& testYear >= sccC.getDeprecatedYear()) {
			return "(inactive-starting-" + sccC.getDeprecatedYear() + ")";
		}
		return null;
	}
	
	private void euSccChoiceMade() {
		if (selectedEu != null) {
			selectedTestedEu = locateTestedEu(selectedEu.getEpaEmuId(),
					selectedScc);

			if (selectedTestedEu == null) {
				selectedTestedEu = new TestedEmissionsUnit(selectedEu);
				selectedTestedEu.setSccs(selectedScc); // add
														// scc
														// id
				stackTest.addTestedEmissionsUnit(selectedTestedEu);
			}
			if (selectedProcess != null) {
				selectedTestedEu.setControlEquipment(selectedProcess
						.getControlEquipment()); // add control eqt
				selectedTestedEu.setProcessDescription(selectedProcess
						.getEmissionProcessNm()); // add process description
			}
			stackTest.reCalTests();
		}
		determineCeList();
		returnToDetail();
	}

	public void includeEuSccs() {
		for (EuDetails euds : euDetails) {
			if (!euds.isSelected())
				continue;
			selectedEu = euds.getEu();
			selectedProcess = euds.getEp();
			selectedScc = euds.getSccId();
			euSccChoiceMade();
		}
	}
	
	public boolean isAdminEditOnly() {
		boolean rtn = false;
		if (stackTest != null) {
			if (stackTest.isAfsLocked() && isStars2Admin())
				rtn = true;
		}
		return rtn;
	}

	public boolean isAllowEditOperations() {
		boolean rtn = false;
		if (isPublicApp()) {
			return rtn;
		}
		if (stackTest != null) {
			if ((isInternalApp() && !stackTest.isAfsLocked() && isCetaUpdate()) || isStars2Admin() || (!isInternalApp() && !isViewOnly()))
				rtn = true;
			rtn = rtn && !isReadOnlyUser();
		}
		return rtn;
	}

	public boolean isShowMemoEditable() {
		boolean rtn = showMemoEditable;
		if (stackTest != null) {
			if (stackTest.isAfsLocked() && isCetaUpdate() && !isStars2Admin())
				rtn = true;
			rtn = rtn && !isReadOnlyUser();
		}
		return rtn;
	}

	public final void edit() {
		setEditable(true);
	}

	public final void editMemo() {
		memoEditable = true; // true when user can edit only Memo field.
	}

	public final void addControlDesc() {
		if (stackTest.getControlEquipUsed() == null) {
			stackTest.setControlEquipUsed("");
		}
		if (controlEquip == null)
			return;
		String type = "";
		if (controlEquip.getEquipmentTypeCd() != null) {
			type = ContEquipTypeDef.getData().getItems()
					.getItemDesc(controlEquip.getEquipmentTypeCd());
		}
		String dapcDesc = controlEquip.getDapcDesc();
		if (dapcDesc == null)
			dapcDesc = "";
		String descript = controlEquip.getDapcDesc();
		if (descript == null || descript.trim().length() == 0)
			descript = controlEquip.getRegUserDesc();
		String stuff = controlEquip.getControlEquipmentId() + "(" + type
				+ "): " + descript;
		if (stackTest.getControlEquipUsed() == null
				|| stackTest.getControlEquipUsed().length() == 0) {
			stackTest.setControlEquipUsed(stuff);
		} else {
			stackTest.setControlEquipUsed(stackTest.getControlEquipUsed()
					+ "; " + stuff);
		}
	}

	public final String copyControlInfo() {
		return "dialog:copyControlInfo";
	}

	public void returnFromCeCopy() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String cancelEdit() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		setEditable(false);
		memoEditable = false;
		String rtn = null;
		try {
			if (newTest) {
				if (newClone) {
					error = true;
					DisplayUtil
							.displayInfo("Operation Cancelled.  Cloned Stack Test not created.");
					return null;
				} else {
					DisplayUtil
							.displayInfo("Operation Cancelled.  Stack Test not created.");
					return initStackTestsForCancelEdit();
				}
			} else {
				rtn = submitStackTestInternal(schemaFlag());
				DisplayUtil
						.displayInfo("Operation Cancelled.  Stack Test not updated.");
			}
		} finally {
			clearButtonClicked();
		}
		return rtn;
	}

	public final void addVisitEvaluator() {
		stackTest.addEvaluator();
	}

	public final void deleteEvaluators() {
		if (stackTest.getWitnesses() == null)
			return;
		Iterator<Evaluator> l = stackTest.getWitnesses().iterator();
		while (l.hasNext()) {
			Evaluator e = l.next();
			if (e == null)
				continue;
			if (e.isSelected())
				l.remove();
		}
	}

	public void addTestDate() {
		stackTest.addTestDate();
	}

	public final void deleteDates() {
		if (stackTest.getVisitDates() == null)
			return;
		Iterator<TestVisitDate> l = stackTest.getVisitDates().iterator();
		while (l.hasNext()) {
			TestVisitDate e = l.next();
			if (e == null)
				continue;
			if (e.isSelected())
				l.remove();
		}
	}

	public final void deleteTestedEmissionsUnits() {
		if (stackTest.getTestedEmissionsUnits() == null)
			return;
		if ((!isTestedEuSelected())) {
			DisplayUtil
					.displayError("No EU/SCC selected. Please select at least one EU/SCC to delete.");
			return;
		}
		Iterator<TestedEmissionsUnit> l = stackTest.getTestedEmissionsUnits()
				.iterator();
		while (l.hasNext()) {
			TestedEmissionsUnit e = l.next();
			if (e == null)
				continue;
			if (e.isSelected()) {
				ArrayList<StackTestedPollutant> activeList = new ArrayList<StackTestedPollutant>();
				for (StackTestedPollutant stp : stackTest.getTestedPollutants()) {
					StackTestedPollutant newStp = new StackTestedPollutant(
							stackTest, stp.getEu(), stp);
					if (stp.getTestedEu().equals(e.getTestedEu())
							&& stp.getSccId().equals(e.getSccs())) {
						newStp.setSuperSelected(false);
					} else {
						newStp.setSuperSelected(true);
					}
					activeList.add(newStp);
				}
				stackTest.setTestedPollutants(activeList
						.toArray(new StackTestedPollutant[0]));
				TableSorter testedPollutantsWrapper = new TableSorter();
				testedPollutantsWrapper.setWrappedData(activeList);
				stackTest.setTestedPollutantsWrapper(testedPollutantsWrapper);

				l.remove();

			}
		}
		determineCeList();
		ArrayList<StackTestedPollutant> activeList = new ArrayList<StackTestedPollutant>();
		for (StackTestedPollutant stp : stackTest.getTestedPollutants()) {
			if (stp.isSelected()) {
				activeList.add(stp);
			}
		}
		stackTest.setTestedPollutants(activeList
				.toArray(new StackTestedPollutant[0]));
		TableSorter testedPollutantsWrapper = new TableSorter();
		testedPollutantsWrapper.setWrappedData(activeList);
		stackTest.setTestedPollutantsWrapper(testedPollutantsWrapper);
		stackTest.reCalTests();
	}

	public void setStackTestMethodCd(String stackTestMethodCd) {
		stackTest.setStackTestMethodCd(stackTestMethodCd);
		try {
			getStackTestService().initializeStackTestPollInfo(stackTest);
			stackTest.reCalTests();
		} catch (RemoteException e) {
			handleException(e);
			error = true;
		}
	}
/* internal app only */
	private void updateLists() {
		FceDetail fceDetailBean = (FceDetail) FacesUtil
				.getManagedBean("fceDetail");
		fceDetailBean.submitSilently(facility.getFacilityId());
	}
	
	public void initializeAttachmentBean() {
		Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
		a.addAttachmentListener(this);
		if (isPublicApp()) {
			a.setStaging(false);
		} else if (isInternalApp()) {
			a.setStaging(false);
		} else if (isPortalApp()) {
			a.setStaging(true);
		}
		a.setNewPermitted(isCetaUpdate());
		a.setUpdatePermitted(isCetaUpdate());
		a.setFacilityId(stackTest.getFacilityId());
		a.setSubPath("StackTest");
		if (stackTest.getId() != null)
			a.setObjectId(Integer.toString(stackTest.getId()));
		else
			a.setObjectId("");
		a.setSubtitle(null);
		if (isPublicApp()) {
			a.setTradeSecretSupported(false);
		} else {
			a.setTradeSecretSupported(true);
		}
		a.setHasDocType(true);
		a.setAttachmentTypesDef(StAttachmentTypeDef.getData().getItems());
		a.setAttachmentList(stackTest.getAttachments().toArray(
				new Attachment[0]));
		a.cleanup();
	}

	public void cancelAttachment() {
		// also used for other cancels
		popupRedirectOutcome=null;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public AttachmentEvent createAttachment(Attachments attachment)
			throws AttachmentException {
		boolean ok = true;
		if (attachment.getDocument() == null) {
			// should never happen
			logger.error("Attempt to process null attachment");
			ok = false;
		} else {
			if (attachment.isTradeSecretAllowed()
					&& attachment.getTradeSecretAttachmentInfo() == null
					&& attachment.getTsFileToUpload() != null) {
				attachment.setTradeSecretAttachmentInfo(new UploadedFileInfo(
						attachment.getTsFileToUpload()));
			}
			if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getFileToUpload() != null) {
				attachment.setPublicAttachmentInfo(new UploadedFileInfo(
						attachment.getFileToUpload()));
			}

			// make sure document description and type are provided
			if (attachment.getDocument().getDescription() == null
					|| attachment.getDocument().getDescription().trim()
							.equals("")) {
				DisplayUtil
						.displayError("Please specify the description for this attachment");
				ok = false;
			}
			if (attachment.getDocument().getDocTypeCd() == null
					|| attachment.getDocument().getDocTypeCd().trim()
							.equals("")) {
				DisplayUtil
						.displayError("Please specify an attachment type for this attachment");
				ok = false;
			}

			// make sure document file is provided
			if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getTempDoc().getDocumentID() == null) {
				DisplayUtil
						.displayError("Please specify a public file to upload for this attachment");
				ok = false;
			}

			// make sure a justification is provided for trade secret document
			if (attachment.isTradeSecretAllowed()) {
				if (attachment.getTradeSecretAttachmentInfo() != null
						|| attachment.getTempDoc().getTradeSecretDocId() != null) {
					if (attachment.getTempDoc().getTradeSecretJustification() == null
							|| attachment.getTempDoc()
									.getTradeSecretJustification().trim()
									.equals("")) {
						DisplayUtil
								.displayError("Please specify the trade secret justification for this attachment");
						ok = false;
					}
				} else if (attachment.getTempDoc()
						.getTradeSecretJustification() != null
						&& attachment.getTempDoc()
								.getTradeSecretJustification().trim().length() > 0) {
					DisplayUtil
							.displayError("The Trade Secret Justification field should only be populated when a trade secret document is specified.");
					ok = false;
				}
			}
		}
		if (ok) {
			try {
				try {
					// process trade secret attachment (if there is one)
					Attachment tsAttachment = null;
					InputStream tsInputStream = null;
					if (attachment.isTradeSecretAllowed()
							&& attachment.getTradeSecretAttachmentInfo() != null) {
						tsAttachment = new Attachment();
						tsAttachment.setLastModifiedTS(new Timestamp(System
								.currentTimeMillis()));
						tsAttachment.setUploadDate(tsAttachment
								.getLastModifiedTS());
						tsAttachment.setExtension(DocumentUtil
								.getFileExtension(attachment
										.getTradeSecretAttachmentInfo()
										.getFileName()));
						tsAttachment.setLastModifiedBy(InfrastructureDefs
								.getCurrentUserId());
						// need object id to be set to put file in correct
						// directory
						tsAttachment.setObjectId(stackTest.getId().toString());
						tsAttachment.setFacilityID(stackTest.getFacilityId());
						tsAttachment.setSubPath("StackTest");
						tsInputStream = attachment
								.getTradeSecretAttachmentInfo()
								.getInputStream();
					}
					getStackTestService().createStAttachment(
							stackTest,
							attachment.getTempDoc(),
							attachment.getPublicAttachmentInfo()
									.getInputStream(), tsAttachment,
							tsInputStream, schemaFlag());
				} catch (IOException e) {
					throw new RemoteException(e.getMessage(), e);
				}

				stackTest.setAttachments(getStackTestService()
						.retrieveStAttachments(stackTest.getId(), schemaFlag()));
				Attachments a = (Attachments) FacesUtil
						.getManagedBean("attachments");
				a.setAttachmentList(stackTest.getAttachments().toArray(
						new Attachment[0]));

				// clean up temporary variables
				attachment.cleanup();
				attachment.setFileToUpload(null);
				attachment.setTsFileToUpload(null);
				attachment.setDocument(null);

				DisplayUtil
						.displayInfo("The attachment has been added to the stack test.");
				FacesUtil.returnFromDialogAndRefresh();
			} catch (RemoteException e) {
				error = true;
				setEditable(false);
				memoEditable = false;
				// turn it into an exception we can handle.
				handleException(e);
			}
		}
		return null;
	}

	public AttachmentEvent deleteAttachment(Attachments attachment) {
		boolean ok = true;
		try {
			Attachment doc = attachment.getTempDoc();
			StAttachment sa = new StAttachment(doc);
			sa.setTradeSecretDocId(doc.getTradeSecretDocId());
			getStackTestService().removeStAttachment(sa, schemaFlag());
		} catch (RemoteException e) {
			ok = false;
			handleException(e);
		}
		// reload attachments
		try {
			stackTest.setAttachments(getStackTestService()
					.retrieveStAttachments(stackTest.getId(), schemaFlag()));
			Attachments a = (Attachments) FacesUtil
					.getManagedBean("attachments");
			a.setAttachmentList(stackTest.getAttachments().toArray(
					new Attachment[0]));
			attachment.cleanup();
		} catch (RemoteException e) {
			error = true;
			setEditable(false);
			memoEditable = false;
			// turn it into an exception we can handle.
			ok = false;
			handleException(e);
		}
		if (ok) {
			DisplayUtil.displayInfo("The attachment has been removed.");
			FacesUtil.returnFromDialogAndRefresh();

		}
		return null;
	}

	public AttachmentEvent updateAttachment(Attachments attachment) {
		Attachment doc = attachment.getTempDoc();
		boolean ok = true;

		// make sure document description is provided
		if (doc.getDescription() == null
				|| doc.getDescription().trim().equals("")) {
			DisplayUtil
					.displayError("Please specify the description for this attachment");
			ok = false;
		}

		// make sure a justification is provided for trade secret document
		if (attachment.getTradeSecretAttachmentInfo() != null
				|| doc.getTradeSecretDocId() != null) {
			if (doc.getTradeSecretJustification() == null
					|| doc.getTradeSecretJustification().trim().equals("")) {
				DisplayUtil
						.displayError("Please specify the trade secret justification for this attachment");
				ok = false;
			}
		}

		if (ok) {
			try {
				StAttachment sa = new StAttachment(doc);
				sa.setTradeSecretDocId(doc.getTradeSecretDocId());
				sa.setTradeSecretJustification(doc
						.getTradeSecretJustification());
				getStackTestService().modifyStAttachment(sa, schemaFlag());
			} catch (RemoteException e) {
				ok = false;
				handleException(e);
			}
			// reload attachments
			try {
				stackTest.setAttachments(getStackTestService()
						.retrieveStAttachments(stackTest.getId(), schemaFlag()));
				Attachments a = (Attachments) FacesUtil
						.getManagedBean("attachments");
				a.setAttachmentList(stackTest.getAttachments().toArray(
						new Attachment[0]));
				attachment.cleanup();
			} catch (RemoteException e) {
				error = true;
				setEditable(false);
				memoEditable = false;
				// turn it into an exception we can handle.
				ok = false;
				handleException(e);
			}
			if (ok) {
				DisplayUtil
						.displayInfo("The attachment information has been updated.");
				FacesUtil.returnFromDialogAndRefresh();
			}
		}
		return null;
	}

	public String getStackTestMethodCd() {
		return stackTest.getStackTestMethodCd();
	}

	public final Integer getId() {
		return id;
	}

	public final void setId(Integer stackTestId) {
		this.id = stackTestId;
	}

	public final String getFacilityName() {
		String facilityName = "";
		if (facility != null) {
			facilityName = facility.getName();
		}
		return facilityName;
	}

	public final StackTest getStackTest() {
		return stackTest;
	}

	public final void setStackTest(StackTest stackTest) {
		this.stackTest = stackTest;
	}

	public final boolean isEditable() {
		return editable;
	}

	public final void setEditable(boolean editable) {
		this.editable = editable;
		setEditMode(editable);
		Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
		a.setNewPermitted(isCetaUpdate());
		a.setUpdatePermitted(isCetaUpdate());
	}

	public final Facility getFacility() {
		return facility;
	}

	public final void setFacility(Facility facility) {
		this.facility = facility;
	}

	public List<ControlEquipment> getControlEquips() {
		return controlEquips;
	}

	public ControlEquipment getControlEquip() {
		return controlEquip;
	}

	public void setControlEquip(ControlEquipment controlEquip) {
		this.controlEquip = controlEquip;
	}

	public String getProvidedScc() {
		return providedScc;
	}

	public void setProvidedScc(String providedScc) {
		this.providedScc = providedScc;
	}

	public TableSorter getAllFCEs() {
		return allFCEs;
	}

	public int getAllFCEsSize() {
		return getAllFCEs().getRowCount();
	}

	public void setAllFCEs(TableSorter allFCEs) {
		this.allFCEs = allFCEs;
	}

	public boolean isChgFceAssignFlag() {
		return chgFceAssignFlag;
	}

	public boolean isValidatedSuccessfully() {
		return validatedSuccessfully;
	}

	public Timestamp getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Timestamp reminderDate) {
		this.reminderDate = reminderDate;
	}

	public Timestamp getTomorrowsDate() {
		return tomorrowsDate;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getFacilityIdForFceReassign() {
		return facilityIdForFceReassign;
	}

	public void setFacilityIdForFceReassign(String facilityIdForFceReassign) {
		this.facilityIdForFceReassign = facilityIdForFceReassign;
	}

	public boolean isDisableDelete() {
		return disableDelete;
	}

	public String getDisableDeleteMsg() {
		return disableDeleteMsg;
	}

	public Facility getCurFacility() {
		return curFacility;
	}

	public String restoreOrigStackTest() {
		id = origStackTestId;
		setFromTODOList(false);
		return submitStackTestInternal(schemaFlag());
	}

	public String printEmissionsTest() {
		testAttachments = prepareTestAttachments();
		testDocuments = prepareTestDocuments();
		if(testAttachments==null){
			DisplayUtil
			.displayWarning("There is no trade secret information in the StackTest or file attachments.. Use Download/Print button instead.");	
			return null;
		}
		return "dialog:prtEmissionsTest";
	}

	private List<Document> prepareTestAttachments() {
		// Always get the documents again, in case user added/deleted/modified
		// attachments.
		boolean useReadonlyDB = false;
		if ((this.isViewOnly() && isPortalApp()) || isInternalApp() || isPublicApp()) {
			useReadonlyDB = true;
		}
		try {
			testAttachments = getStackTestService().getPrintableAttachmentList(
					stackTest, useReadonlyDB, hideTradeSecret);

		} catch (RemoteException re) {
			logger.error("getTestAttachments() failed for Stack Test "
					+ stackTest.getId(), re);
			DisplayUtil
					.displayError("Unable to generate stack test attachments");
		}		
		
		
		return testAttachments;
	}

	private List<Document> prepareTestDocuments() {
		boolean useReadonlyDB = false;
		if ((this.isViewOnly() && isPortalApp()) || isInternalApp() || isPublicApp()) {
			useReadonlyDB = true;
		}
		// Always get the documents again, in case user added/deleted/modified
		// attachments.
		String user = InfrastructureDefs.getCurrentUserAttrs().getUserName();
		testDocuments = new ArrayList<Document>();
		TmpDocument facilityDoc = null;

		try {
			facilityDoc = getFacilityService()
					.generateTempFacilityProfileReport(facility, null);
			facilityDoc.setDescription("Printable View of Facility Inventory");
		} catch (RemoteException re) {
			logger.error(
					"generateTempFacilityProfileReport() failed for report "
							+ stackTest.getId(), re);
			DisplayUtil
					.displayError("Unable to generate facility inventory document");
		}

		// Since the above code populated the attachment bean with facility
		// attachment info refresh the attachment bean.
		initializeAttachmentBean();

		try {

			testDocuments = getStackTestService().getPrintableDocumentList(
					facility, facilityDoc, stackTest, user, useReadonlyDB,
					hideTradeSecret);
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Unable to generate emissions inventory report documents");
			handleException("Stack Test " + stackTest.getId().toString(), re);
		}
		return testDocuments;
	}

	public boolean isNewClone() {
		return newClone;
	}

	public void setSelectedEu(EmissionUnit selectedEu) {
		this.selectedEu = selectedEu;
	}

	public void setSelectedProcess(EmissionProcess selectedProcess) {
		this.selectedProcess = selectedProcess;
	}

	public ArrayList<EuDetails> getEuDetails() {
		return euDetails;
	}

	public String getSelectedEpaEmuId() {
		return selectedEpaEmuId;
	}

	public void setSelectedEpaEmuId(String selectedEpaEmuId) {
		this.selectedEpaEmuId = selectedEpaEmuId;
	}

	public String getAfsWarning() {
		return afsWarning;
	}

	public boolean isMemoEditable() {
		return memoEditable;
	}

	public int getMemoRows() {
		int rtn = 3;
		int rows = 1;
		if (stackTest != null) {
			if (stackTest.getMemo() != null) {
				int charInRow = 0;
				// count number of rows by having either 170 characters or a new
				// line.
				for (int i = 0; i < stackTest.getMemo().length(); i++) {
					if ((int) (stackTest.getMemo().charAt(i)) == 10) {
						rows++;
						charInRow = 0;
					} else {
						charInRow++;
						if (charInRow > 170) {
							rows++;
							charInRow = 0;
						}
					}
				}
			}
		}
		if (rows > rtn)
			rtn = rows;
		return rtn;
	}

	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		// Validates stack test one more time before accepting task check in
		// Additional validations may be applied (i.e. reviewed?)
		messages = validateStackTest();

		if (stackTest.getDateEvaluated() == null
				|| stackTest.getDateEvaluated().equals("")) {
			messages.add(new ValidationMessage("dateEvaluated",
					"Date Results Reviewed should be set.",
					ValidationMessage.Severity.ERROR,
					ValidationBase.STACK_TEST_TAG + ":" + stackTest.getId()
							+ ":" + STACK_TEST));
		}

		if (stackTest.getReviewer() == null || stackTest.getReviewer() <= 0) {
			messages.add(new ValidationMessage("reviewerChoice",
					"A Reviewer should be set.",
					ValidationMessage.Severity.ERROR,
					ValidationBase.STACK_TEST_TAG + ":" + stackTest.getId()
							+ ":" + STACK_TEST));
		}

		return messages;
	}

	@Override
	public String findOutcome(String url, String ret) {
		String retVal = null;
		if(null != getStackTest()) {
			retVal = "ceta.stackTestDetail";
		}
		return retVal;
	}

	@Override
	public Integer getExternalId() {
		return stackTest.getId();
	}

	@Override
	public void setExternalId(Integer externalId) {
		setId(externalId);
		setFromTODOList(true);
		submitStackTestInternal(true);
	}

	@Override
	public String getExternalName(ProcessActivity activity) {
		String ret = super.getExternalName(activity);
		ret = "Stack Test";
		return ret;
	}

	@Override
	public String getExternalNum() {
		return stackTest.getStckId();
	}

	@Override
	public String toExternal() {
		setFromTODOList(true);
		return submitStackTestInternal(true);
	}
	
	private void loadNotes(int stackTestId) {
		try {
			Note[] stackTestNotes = getStackTestService()
					.retrieveStackTestNotes(stackTestId);
			stackTest.setStackTestNotes(stackTestNotes);
		} catch (RemoteException ex) {
			DisplayUtil.displayError("cannot load stack test comments");
			handleException(ex);
			return;
		}
	}

	public final String startAddComment() {
		tempComment = new StackTestNote();
		tempComment.setUserId(getCurrentUserID());
		tempComment.setStackTestId(stackTest.getId());
		tempComment.setDateEntered(new Timestamp(System.currentTimeMillis()));
		tempComment.setNoteTypeCd(NoteType.DAPC);
		newNote = true;
		noteReadOnly = false;

		return COMMENT_DIALOG_OUTCOME;
	}

	public final String startEditComment() {
		newNote = false;
		tempComment = new StackTestNote(modifyComment);
		if (tempComment.getUserId().equals(getCurrentUserID()))
			noteReadOnly = false;
		else
			noteReadOnly = true;

		return COMMENT_DIALOG_OUTCOME;
	}

	public final void saveComment(ActionEvent actionEvent) {

		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		// make sure note is provided
		if (tempComment.getNoteTxt() == null
				|| tempComment.getNoteTxt().trim().equals("")) {
			validationMessages.add(new ValidationMessage("noteTxt",
					"Attribute " + "Note" + " is not set.",
					ValidationMessage.Severity.ERROR, "noteTxt"));
		}

		if (validationMessages.size() > 0) {
			displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0]));
		} else {
			try {
				if (newNote) {
					getStackTestService().createStackTestNote(tempComment);
				} else {
					getStackTestService().modifyStackTestNote(tempComment);
				}
			} catch (RemoteException e) {
				DisplayUtil.displayError("cannot save comment");
				handleException(e);
				return;
			}

			tempComment = null;
			reloadNotes();
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public final void commentDialogDone(ReturnEvent returnEvent) {
		tempComment = null;
		reloadNotes();
	}

	public final StackTestNote getTempComment() {
		return tempComment;
	}

	public final void setTempComment(StackTestNote tempComment) {
		this.tempComment = tempComment;
	}

	public Integer getCurrentUserID() {
		return InfrastructureDefs.getCurrentUserId();
	}

	public final boolean isNoteReadOnly() {
		if (isReadOnlyUser()) {
			return true;
		}

		return noteReadOnly;
	}

	public final void setNoteReadOnly(boolean noteReadOnly) {
		this.noteReadOnly = noteReadOnly;
	}

	public final String reloadNotes() {
		loadNotes(stackTest.getId());
		return FacesUtil.getCurrentPage(); // stay on same page
	}

	public void closeViewDoc(ActionEvent actionEvent) {
		// reset this to true if it has been set to false
		// deleteDocAllowed = true;
		// viewDoc = false;
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public final StackTestNote getModifyComment() {
        return modifyComment;
    }

    public final void setModifyComment(StackTestNote modifyComment) {
        this.modifyComment = modifyComment;
    }
    
	public  String validationDlgAction() {
		String rtn = super.validationDlgAction();
		if (null != rtn)
			return rtn;
		
		if (newValidationDlgReference == null
				|| newValidationDlgReference
						.equals(getValidationDlgReference())) {
			//enterEditMode();
			return null; // stay on same page
		}

		StringTokenizer st = new StringTokenizer(newValidationDlgReference, ":");
		String subsystem = st.nextToken();
		if (!subsystem.equals(ValidationBase.STACK_TEST_TAG)) {
			logger.error("Stack Test reference is in error: "
					+ newValidationDlgReference);
			DisplayUtil.displayError("Error processing validation message");
			return null;
		}

		String valStacktId = st.nextToken();
		if (!valStacktId.equalsIgnoreCase(String.valueOf(id))) {
			DisplayUtil.displayError("Validation message is for Stack Test: "
					+ valStacktId);
			return null;
		}
		submitStackTestInternal(schemaFlag());
		// enter edit mode
		boolean isRefStDetail = newValidationDlgReference.contains("stackDetail");
		if(!isRefStDetail) {
			setEditable(true);
		}

		return st.nextToken(); // stay on same page
	}

	public final boolean isOkToUpdateFacilityProfile() {
		// facility inventory may be updated for non-submitted Stack Tests
		return (!isReadOnlyUser() && stackTest.getSubmittedDate() == null);
	}

	public boolean isAllEuDetailsSelected() {
		int cnt = 0;
		if (euDetails != null) {
			for (EuDetails euds : euDetails) {
				if (euds.isAlreadySelected()
						|| euds.getSccId() == euds.getNoProcess()) {
					cnt++;
				}
			}

			if (cnt != 0 && cnt == euDetails.size()) {
				allEuDetailsSelected = true;
				return true;
			} else {
				allEuDetailsSelected = false;
				return false;
			}
		}
		allEuDetailsSelected = true;
		return true;
	}

	public void setAllEuDetailsSelected(boolean allEuDetailsSelected) {
		this.allEuDetailsSelected = allEuDetailsSelected;
	}

	public boolean isNoEuDetailsToSelect() {
		return this.noEuDetailsToSelect;
	}

	public void setNoEuDetailsToSelect(boolean noEuDetailsToSelect) {
		this.noEuDetailsToSelect = noEuDetailsToSelect;
	}

	public boolean isLocked() {
		boolean ret = true;
		/*
		 * This returns true if the record has been reviewed. It ensures
		 * customer-facing fields are not editable by AQD unless they are Admin
		 * once the test has been reviewed
		 */
		if (isPublicApp()) {
			ret = true;
		} else if (isPortalApp() && isViewOnly()) {
			ret = true;
		} else if (isStaging() || isAdmin()) {
			ret = false;
		} else if ((EmissionsTestStateDef.DRAFT.equalsIgnoreCase(stackTest
				.getEmissionTestState()) || EmissionsTestStateDef.REMINDER
				.equalsIgnoreCase(stackTest.getEmissionTestState()))
				&& !isReadOnlyUser()) {
			// the test is being created internally - they must be able to
			// edit this
			ret = false;
		}

		return ret;
	}

	public boolean isAdmin() {
		boolean ret = false;

		if (!isStaging()) {
			ret = InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();
		}

		return ret;
	}

	public boolean isStaging() {
		return staging;
	}

	public void setStaging(boolean staging) {
		this.staging = staging;
	}

	public Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}

	public boolean getEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isTestedEuSelected() {
		boolean selected = false;
		for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
			if (teu.isSelected())
				return true;
		}
		return selected;
	}

	public final Boolean isHideTradeSecret() {
		return hideTradeSecret;
	}
	
	public boolean isActiveWorkflowProcess() {
		Integer processTemplateId = 8;
		Integer externalId = getId();
    	String errMsg = "Error occurred while checking if an active " +
    			"workflow process exists for this stack test; " +
    			"stack test deletion will not be allowed.";
		try {
			return null == workFlowService.findActiveProcessByExternalObject(
					processTemplateId, externalId) ? false : true;
		} catch (RemoteException e) {
			logger.error("error occurred checking for active wf process",e);
			DisplayUtil.displayError(errMsg);
			handleException(e);
			return true;
		}
	}

	public final void setHideTradeSecret(Boolean hideTradeSecret) {
		this.hideTradeSecret = hideTradeSecret;
	}

	public final boolean isTradeSecretVisible() {
		// Note that this Use Case is used both to protect Trade Secret
		// Application attachments and Compliance attachments.
		if (isPublicApp()) {
			return false;
		}
		return (isPortalApp() || InfrastructureDefs.getCurrentUserAttrs()
				.isCurrentUseCaseValid("applications.viewtradesecret"))
				&& !getEditMode();
	}

	public DefSelectItems getStBasicUsersDef() {
		DefSelectItems allUsers = BasicUsersDef.getData().getItems();
		DefSelectItems unselectedUsers = new DefSelectItems();
		List<Integer> witnesses = new ArrayList<Integer>();

		// create a list of already selected witnesses
		for (Evaluator witness : stackTest.getWitnesses()) {
			if (witness.getEvaluator() != null)
				witnesses.add(witness.getEvaluator());
		}

		// create a list of available witnesses i.e., allUsers minus already
		// selected witnesses
		for (SelectItem user : allUsers.getCurrentItems()) {
			if (!witnesses.contains((Integer) user.getValue()))
				unselectedUsers.add(user.getValue(), user.getLabel(), false);
		}
		return unselectedUsers;
	}

	public List<Document> getTestAttachments() {
		return testAttachments;
	}

	public List<Document> getTestDocuments() {
		return testDocuments;
	}
	
	public boolean isGeneral() {
		return InfrastructureDefs.getCurrentUserAttrs().isGeneralUser();
	}
    
    public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        
        return null;
    }
  
    public void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}
	
	protected boolean schemaFlag() {
		boolean useReadOnlySchema = true;

		if (isPortalApp()) {
			useReadOnlySchema = false;
		}

		return useReadOnlySchema;
	}
	
	public boolean isViewOnly() {
		return viewOnly;
	}

	public final void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}
	
	abstract public void returnToDetail();
	
	public abstract String initStackTestsForCancelEdit();
	
	abstract public void handleBadDetail();

	abstract public void handleBadDetailAndRedirect();
	
	public String getValidationDlgReference() {
		String ret = null;
		
		ret = STACKTEST_REFERENCE;

		ret += ":" + FacesUtil.getCurrentPage();

		return ret;
	}
	
	public void loadStackTest(int stackTestId, boolean schemaFlag) {
		Facility f = null;
		try {
			stackTest = getStackTestService().retrieveStackTest(stackTestId, schemaFlag);
			if (stackTest != null) {
				f = getFacilityService().retrieveFacility(
						stackTest.getFacilityId(), false);
				if (f != null) {
					setFacility(f);
				} else {
					DisplayUtil.displayWarning("Failed to find facility "
							+ stackTest.getFacilityId() + ", for stack test "
							+ stackTestId + " Refresh data and try again.");
				}
			}
			
		} catch (RemoteException re) {
			handleException(re);
		}
		
	}
	
}

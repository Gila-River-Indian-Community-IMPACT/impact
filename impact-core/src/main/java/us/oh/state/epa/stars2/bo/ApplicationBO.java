
package us.oh.state.epa.stars2.bo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.common.util.StringUtils;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUFugitiveLeaks;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUMaterialUsed;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationNote;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.FacilityWideRequirement;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationBACTEmission;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationLAEREmission;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotificationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequestDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TIVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVAltScenarioPteReq;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicableReq;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVCompliance;
import us.oh.state.epa.stars2.database.dbObjects.application.TVComplianceObligations;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperatingScenario;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperationalRestriction;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUPollutantLimit;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedAltLimits;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedExemptions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedTestChanges;
import us.oh.state.epa.stars2.database.dbObjects.application.TVPteAdjustment;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeCSH;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeEGU;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeENG;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeFUG;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeSEB;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeTNK;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantsControlled;
import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.RPRPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.AppEUFUGEmissionTypeDef;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.ApplicationEUEmissionTableDef;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.CeOperatingStatusDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.EventLogTypeDef;
import us.oh.state.epa.stars2.def.ExemptStatusDef;
import us.oh.state.epa.stars2.def.FacPermitStatusDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.MaterialUsedDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PAEuPteDeterminationBasisDef;
import us.oh.state.epa.stars2.def.PBRNotifDocTypeDef;
import us.oh.state.epa.stars2.def.PBRTypeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationEUPurposeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationPurposeDef;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl2Def;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.RPCRequestDocTypeDef;
import us.oh.state.epa.stars2.def.RPCTypeDef;
import us.oh.state.epa.stars2.def.RelocationTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.TVApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.TVApplicationPurposeDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.def.TVFacWideReqOptionDef;
import us.oh.state.epa.stars2.def.USEPAOutcomeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.NotFoundException;
import us.oh.state.epa.stars2.framework.exception.WorkflowRollbackException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;
import us.oh.state.epa.stars2.webcommon.pdf.application.ApplicationPdfGenerator;
import us.oh.state.epa.stars2.webcommon.reports.PollutantPartOf;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dao.ContactDAO;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

import com.lowagie.text.DocumentException;

@Transactional(rollbackFor = Exception.class)
@Service
@Scope("prototype")
public class ApplicationBO extends BaseBO implements ApplicationService {
	
	private static HashMap<String, String> ptioCapPollutantDefs = null;
	private static List<String> ptioCapPollutantCodesOrdered = null;
	private static List<String> ptioMaterialUsedCodesOrdered = null;
	private static List<String> tvCapPollutantCodes = null;
	private static long lastUpdate = 0;

	private static final String DFLT_CAP_EMISSIONS_VALUE_FORMAT = "###,###.##";
	private static final String DFLT_CAP_LB_HR_EMISSIONS_VALUE_FORMAT = "###,###.##";
	private static final String DFLT_HAP_EMISSIONS_VALUE_FORMAT = "###,###.######";
	private static final String DFLT_TV_CAP_EMISSIONS_VALUE_FORMAT = "###,###.##";
	private static final String DFLT_TV_HAP_EMISSIONS_VALUE_FORMAT = "###,###.######";
	private static final String DFLT_GHG_EMISSIONS_VALUE_FORMAT = "###,###.##";
	private static final String DFLT_OTH_EMISSIONS_VALUE_FORMAT = "###,###.##";
	private static final String DFLT_CMX_EMISSIONS_MATERIAL_USED_VALUE_FORMAT = "###,###,###,###";
	private static String capEmissionsValueFormat = null;
	private static String capLbHrEmissionsValueFormat = null;
	private static String hapEmissionsValueFormat = null;
	private static String tvCapEmissionsValueFormat = null;
	private static String tvHapEmissionsValueFormat = null;
	private static String ghgEmissionsValueFormat = null;
	private static String othEmissionsValueFormat = null;
	private static String cmxEmissionsMaterialUsedValueFormat = null;
	private HashMap<String, Float> egressPtThresholdMap;
	private static HashMap<String, String> ptioMaterialUsedDefs = null;

	// since this BO is session scoped, it is ok to have the instance variable
	// otherwise in a normal situation where the BO is a singleton, instance
	// variables should not be created for thread saftey
	private Application application = null;
	private Facility facility = null;
	
	public ApplicationEU replaceApplicationEU(Application application, 
			ApplicationEU appEU) 
					throws DAOException, RemoteException {

		// replace an existing application eu with a new one created from 
		// scratch
		
		removeApplicationEU(appEU,false);
		
		// create new pa_eu
		ApplicationEU newAppEU = getNewApplicationEU(application, appEU.getFpEU());
		newAppEU.setExcluded(true);
		modifyApplicationEU(newAppEU);
		return newAppEU;
	}
	
	public ApplicationEU getNewApplicationEU(Application application, EmissionUnit fpEU)
			throws DAOException, RemoteException {
		ApplicationEU appEU = null;
		boolean excluded = true;
		if (application instanceof PTIOApplication) {
			appEU = new PTIOApplicationEU();
		} else if (application instanceof TVApplication) {
			appEU = new TVApplicationEU();
			excluded = false;
		} else {
			appEU = new ApplicationEU();
		}
		appEU.setApplicationId(application.getApplicationID());
		appEU.setExcluded(excluded);
		appEU.setFpEU(fpEU);
		appEU = createApplicationEU(appEU);
		return appEU;
	}
	
	/**
	 * @param applicationId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Application retrieveApplication(Integer applicationId)
			throws DAOException, RemoteException {
		Application application = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			application = retrieveApplicationWithAllEUs(applicationId, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return application;
	}
/*
	public Application retrieveApplication(Integer applicationId,
			boolean staging) throws DAOException {
		Application application = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			application = retrieveApplication(applicationId, trans, staging);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return application;
	}
*/
	/**
	 * Synchronize <code>application</code> with the current view of the
	 * facility inventory.
	 * 
	 * @param application
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @return true if changes were made to synchronize the application with the
	 *         facility inventory, false otherwise
	 */
	public boolean synchAppWithCurrentFacilityProfile(Application application)
			throws DAOException {
		Transaction trans = null;
		boolean modified = false;

		FacilityDAO facDAO = facilityDAO(getSchema(CommonConst.STAGING_SCHEMA));
		Facility currentFacility = null;
		Facility appFacility = application.getFacility();
		currentFacility = facDAO.retrieveFacility(appFacility.getFacilityId());
		if (!currentFacility.getFpId().equals(appFacility.getFpId())
				|| !currentFacility.getLastModified().equals(
						appFacility.getLastModified())) {
			try {
				trans = TransactionFactory.createTransaction();
				synchAppWithCurrentFacilityProfile(application,
						currentFacility, trans);
				trans.complete();
				application = retrieveApplicationWithAllEUs(application
						.getApplicationID());
				appFacility = facDAO
						.retrieveFacilityData(appFacility.getFpId());
				ApplicationNote note = createFacilityChangedNote(application,
						appFacility, currentFacility);
				createApplicationNote(note);
				modified = true;
			} catch (DAOException de) {
				cancelTransaction(trans, de);
			} catch (RemoteException de) {
				cancelTransaction(trans, de);
			} finally {
				closeTransaction(trans);
			}
		}

		return modified;
	}

	private void deleteOrphanEUs(Application application, Transaction trans)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		// delete any "orphan" EUs that may exist in the application (Mantis
		// 2906)
		Integer[] orphanEuIds = appDAO.retrieveOrphanEus(application
				.getApplicationID());
		if (orphanEuIds.length > 0) {
			logger.debug("WARN] EUs exist in application "
					+ application.getApplicationNumber()
					+ " that do not exist in facility "
					+ application.getFacility().getFacilityId());
			for (Integer euId : orphanEuIds) {
				logger.debug("WARN] Deleting orphan application EU with id "
						+ euId);
				removeApplicationEU(euId, application, trans);
			}
		}
	}

	private ApplicationNote createFacilityChangedNote(Application application,
			Facility oldFacility, Facility newFacility) {
		ApplicationNote note = new ApplicationNote();
		String noteTxt = "The facility inventory for this application was updated to the current version on  "
				+ new Timestamp(System.currentTimeMillis()).toString();
		note.setNoteTxt(noteTxt);
		note.setApplicationId(application.getApplicationID());
		note.setDateEntered(new Timestamp(System.currentTimeMillis()));
		note.setNoteTypeCd(NoteType.DAPC);
		note.setUserId(InfrastructureDefs.getCurrentUserId());
		return note;
	}

	/**
	 * Synchronize gateway <code>application</code> with the latest view of the
	 * facility inventory.
	 * 
	 * @param application
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @return true if changes were made to synchronize the application with the
	 *         facility inventory, false otherwise
	 */
	public void synchGatewayAppWithCurrentFacilityProfile(
			Application application) throws DAOException {
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			FacilityDAO facDAO = facilityDAO(trans);
			Facility currentFacility = null;
			Facility appFacility = application.getFacility();

			currentFacility = ServiceFactory.getInstance().getFacilityService()
					.retrieveFacilityProfile((appFacility.getFpId()), true);

			synchGatewayAppWithCurrentFacilityProfile(application,
					currentFacility, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} catch (ServiceFactoryException sfe) {
			logger.error("Unable to load Facility Service: " + sfe.getMessage());
			throw new DAOException("Unable to load Facility Service");
		} finally {
			closeTransaction(trans);
		}
	}

	private void synchAppWithCurrentFacilityProfile(Application application,
			Facility currentFacility, Transaction trans) throws DAOException {
		// RPCRequest, RPERequest and RPRRequest handle update of EU information
		// in the modifyApplication
		// method. Other request types use the code below
		if (!(application instanceof RPCRequest
				|| application instanceof RPERequest || application instanceof RPRRequest)) {
			// update fpEU id's for app EUs
			List<ApplicationEU> appEUs = application.getEus();
			application.getEus().clear();
			List<EmissionUnit> fpEUs = new ArrayList<EmissionUnit>(
					currentFacility.getEmissionUnits());
			for (ApplicationEU appEU : appEUs) {
				// find matching EU in updated facility
				synchAppEuWithFacility(appEU,
						currentFacility.getEmissionUnits());
				fpEUs.remove(appEU.getFpEU());
			}
			// add any EUs that may have been added to the facility to the
			// application
			if (fpEUs.size() > 0) {
				addEusToApplication(application, fpEUs, trans);
			}
		}
		// update facility information
		application.setValidated(false);
		application.setFacility(currentFacility);
		
		boolean modifyEUs = true;
		modifyApplication(application, trans, modifyEUs);
		
		// make sure EUs deleted from the facility inventory are no longer part of
		// the application
		deleteOrphanEUs(application, trans);
	}

	private void synchGatewayAppWithCurrentFacilityProfile(
			Application application, Facility currentFacility, Transaction trans)
			throws DAOException {
		// RPCRequest, RPERequest and RPRRequest handle update of EU information
		// in the modifyApplication
		// method. Other request types use the code below
		if (!(application instanceof RPCRequest
				|| application instanceof RPERequest || application instanceof RPRRequest)) {
			// update fpEU id's for app EUs
			List<ApplicationEU> appEUs = application.getEus();
			application.getEus().clear();
			List<EmissionUnit> fpEUs = new ArrayList<EmissionUnit>(
					currentFacility.getEmissionUnits());
			for (ApplicationEU appEU : appEUs) {
				// find matching EU in updated facility
				appEU = retrieveApplicationEU(appEU.getApplicationEuId(), trans, true);
				if (synchGatewayAppEuWithFacility(appEU,
						currentFacility.getEmissionUnits())) {
					modifyApplicationEU(appEU, trans);
					appEU = retrieveApplicationEU(appEU.getApplicationEuId(), trans, true);
				}
				fpEUs.remove(appEU.getFpEU());
			}
			// add any EUs that may have been added to the facility to the
			// application
			if (fpEUs.size() > 0) {
				addEusToApplication(application, fpEUs, trans);
			}
		}
		// update facility information
		application.setFacility(currentFacility);
	}

	private void synchAppEuWithFacility(ApplicationEU appEU,
			List<EmissionUnit> fpEus) {
		for (EmissionUnit eu : fpEus) {
			if (eu.getCorrEpaEmuId().equals(appEU.getFpEU().getCorrEpaEmuId())) {
				appEU.setFpEU(eu);
				if (eu.getEuShutdownDate() != null
						|| EuOperatingStatusDef.IV.equals(eu
								.getOperatingStatusCd())
						|| EuOperatingStatusDef.SD.equals(eu
								.getOperatingStatusCd())
						|| TVClassification.TRIVIAL.equals(eu.getTvClassCd())
						|| TVClassification.INSIG_NO_APP_REQS.equals(eu
								.getTvClassCd())) {
					// force EU to be excluded from the application if it is
					// shutdown, invalid, or trivial
					appEU.setExcluded(true);
				}
				break;
			}
		}
	}

	private void synchPermitModRequestEuWithFacility(ApplicationEU appEU,
			List<EmissionUnit> fpEus) {
		for (EmissionUnit eu : fpEus) {
			if (eu.getCorrEpaEmuId().equals(appEU.getFpEU().getCorrEpaEmuId())) {
				appEU.setFpEU(eu);
				break;
			}
		}
	}

	private boolean synchGatewayAppEuWithFacility(ApplicationEU appEU,
			List<EmissionUnit> fpEus) {
		boolean modified = false;
		for (EmissionUnit eu : fpEus) {
			if (eu.getCorrEpaEmuId().equals(appEU.getFpEU().getCorrEpaEmuId())) {
				if (!appEU.getFpEU().getEmuId().equals(eu.getEmuId())) {
					logger.debug("Updating FP EU for appEU "
							+ appEU.getFpEU().getEpaEmuId());
					appEU.setFpEU(eu);
					modified = true;
				}
				if ((eu.getEuShutdownDate() != null
						|| EuOperatingStatusDef.IV.equals(eu
								.getOperatingStatusCd())
						|| EuOperatingStatusDef.SD.equals(eu
								.getOperatingStatusCd())
						|| TVClassification.TRIVIAL.equals(eu.getTvClassCd()) || TVClassification.INSIG_NO_APP_REQS
							.equals(eu.getTvClassCd()))
						&& !appEU.isExcluded()) {
					// force EU to be excluded from the application if it is
					// shutdown, invalid, or trivial
					logger.debug("Excluding appEU "
							+ appEU.getFpEU().getEpaEmuId());
					appEU.setExcluded(true);
					modified = true;
				}
				break;
			}
		}
		return modified;
	}

	public boolean isRelocationClass(Application application) {
		if (application == null) {
			logger.error("Unable to get application class");
			return false;
		}
		if (application.getApplicationTypeCD().equals(
				RelocationTypeDef.INTENT_TO_RELOCATE)
				|| application.getApplicationTypeCD().equals(
						RelocationTypeDef.SITE_PRE_APPROVAL)
				|| application.getApplicationTypeCD().equals(
						RelocationTypeDef.RELOCATE_TO_PREAPPROVED_SITE)) {
			return true;
		}
		return false;

	}

	public boolean isDelegationClass(Application application) {
		if (application.getApplicationTypeCD().equals(
				ApplicationTypeDef.DELEGATION_OF_RESPONSIBILITY)) {
			return true;
		}

		return false;
	}
/*	
	private void populateApplicationComponents(Application application,
			Transaction trans, boolean shallow, boolean staging)
			throws DAOException {
		int appId = application.getApplicationID();
		ApplicationEU[] appEUs = null;
		PTIOApplication ptioApp = null;
		TVApplication tvApp = null;

		ApplicationDAO appDAO = applicationDAO(trans);

		if (isRelocationClass(application)) {
			RelocateRequestDAO relocateDAO = relocateRequestDAO(trans);
			// retrieve additional addresses (if any)
			for (RelocationAddtlAddr addr : relocateDAO
					.retrieveRelocationAddtlAddrs(((RelocateRequest) application)
							.getRequestId())) {
				((RelocateRequest) application).addAdditionalAddress(addr);
			}
			((RelocateRequest) application).setAttachments(relocateDAO
					.retrieveAttachments(application.getFacilityId(),
							application.getApplicationID()));
		} else if (isDelegationClass(application)) {
			DelegationRequestDAO delegationDAO = delegationRequestDAO(trans);
			((DelegationRequest) application).setAttachments(delegationDAO
					.retrieveAttachments(application.getFacilityId(),
							application.getApplicationID()));
			logger.error("(" + application.getApplicationID() + ")"
					+ ((DelegationRequest) application).getAttachments().length);
		}
		if (!shallow) {
			// retrieve appropriate documents for the application
			if (application instanceof PBRNotification) {
				((PBRNotification) application).getPbrDocuments().clear();
				PBRNotificationDocument[] docs = appDAO
						.retrievePBRNotificationDocuments(appId);
				for (PBRNotificationDocument doc : docs) {
					((PBRNotification) application).getPbrDocuments().add(doc);
				}
			} else if (application instanceof RPCRequest) {
				((RPCRequest) application).getRpcDocuments().clear();
				RPCRequestDocument[] docs = appDAO
						.retrieveRPCRequestDocuments(appId);
				for (RPCRequestDocument doc : docs) {
					((RPCRequest) application).getRpcDocuments().add(doc);
				}
			} else {
				application.setDocuments(new ArrayList<ApplicationDocumentRef>());
				
				ApplicationDocumentRef[] appDocuments = null;
				if (ApplicationTypeDef.PTIO_APPLICATION.equals(application.getApplicationTypeCD())) {
					appDocuments = appDAO.retrieveNSRApplicationDocuments(appId);
				} else if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(application.getApplicationTypeCD())) {
					appDocuments = appDAO.retrieveTVApplicationDocuments(appId);
				} else {
					appDocuments = appDAO.retrieveApplicationDocuments(appId);
				}
				
				for (ApplicationDocumentRef doc : appDocuments) {
					// String s = "no public document";
					// if(doc.getPublicDoc() != null) s =
					// doc.getPublicDoc().getBasePath();
					// logger.error("Debug #2966: populateApplicationComponents: attachment description"
					// + doc.getDescription() +
					// ", basePath " + s);
					application.addDocument(doc);
				}
			}

			// retrieve contact information
			Integer contactId = application.getContactId();
			if (contactId != null) {
				ContactDAO cDAO = contactDAO(trans);

				if (staging) {
					String facilityId = application.getFacilityId();
					application.setContact(cDAO.retrieveStagingContact(
							contactId, facilityId));

				} else {
					application.setContact(cDAO.retrieveContact(contactId));
				}

			}
		}

		if (application.getClass() == PTIOApplication.class) {
			appEUs = appDAO.retrievePTIOApplicationEmissionUnits(appId);
			if (appEUs != null) {
				// populate Federal Rules Applicability, BACT, and LAER
				// information
				for (PTIOApplicationEU ptioAppEu : (PTIOApplicationEU[]) appEUs) {
					Integer appEuId = ptioAppEu.getApplicationEuId();

					// subparts
					if (ptioAppEu.getNspsApplicableFlag() != null
							&& (ptioAppEu.getNspsApplicableFlag().equals(
									PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || ptioAppEu
									.getNspsApplicableFlag()
									.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT))) {
						String[] subparts = appDAO
								.retrievePTIOAppNSPSSubpartCds(appEuId);
						ptioAppEu.setNspsSubpartCodes(Utility
								.createArrayList(subparts));
					}
					if (ptioAppEu.getNeshapApplicableFlag() != null
							&& (ptioAppEu.getNeshapApplicableFlag().equals(
									PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || (ptioAppEu
									.getNeshapApplicableFlag()
									.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)))) {
						String[] subparts = appDAO
								.retrievePTIOAppNESHAPSubpartCds(appEuId);
						ptioAppEu.setNeshapSubpartCodes(Utility
								.createArrayList(subparts));
					}
					if (ptioAppEu.getMactApplicableFlag() != null
							&& (ptioAppEu.getMactApplicableFlag().equals(
									PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || ptioAppEu
									.getMactApplicableFlag()
									.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT))) {
						String[] subparts = appDAO
								.retrievePTIOAppMACTSubpartCds(appEuId);
						ptioAppEu.setMactSubpartCodes(Utility
								.createArrayList(subparts));
					}

					// BACT
					if (ptioAppEu.isBactAnalysisCompleted()) {
						NSRApplicationBACTEmission[] bactEmissions = appDAO
								.retrieveNSRApplicationBactEmissions(appEuId);
						ptioAppEu.setBactEmissions(Utility
								.createArrayList(bactEmissions));
					}

					// LAER
					if (ptioAppEu.isLaerAnalysisCompleted()) {
						NSRApplicationLAEREmission[] laerEmissions = appDAO
								.retrieveNSRApplicationLaerEmissions(appEuId);
						ptioAppEu.setLaerEmissions(Utility
								.createArrayList(laerEmissions));
					}
				}
			}

			ptioApp = (PTIOApplication) application;
			if (!shallow) {
				String[] purposeCds = appDAO
						.retrievePTIOApplicationPurposeCds(appId);
				ptioApp.setApplicationPurposeCDs(Utility
						.createArrayList(purposeCds));

			}

		} else if (application.getClass() == TVApplication.class) {
			appEUs = appDAO.retrieveTVApplicationEmissionUnits(appId);
			if (!shallow) {
				tvApp = (TVApplication) application;
				for (TVEUGroup group : appDAO
						.retrieveTvEuGroupsForApplication(appId)) {
					tvApp.getEuGroups().add(group);
					for (TVApplicableReq req : appDAO
							.retrieveTVApplicableReqsForEUGroup(group
									.getTvEuGroupId())) {
						populateTVApplicableReqComponents(req, appDAO);

						if (!req.isStateOnly()) {
							group.getApplicableRequirements().add(req);
						} else {
							group.getStateOnlyRequirements().add(req);
						}
					}
				}

				TVApplicableReq[] applicableReqs = appDAO
						.retrieveTVApplicableReqs(appId);
				for (TVApplicableReq applicableReq : applicableReqs) {
					populateTVApplicableReqComponents(applicableReq, appDAO);
					if (!applicableReq.isStateOnly()) {
						tvApp.getApplicableRequirements().add(applicableReq);
					} else {
						tvApp.getStateOnlyRequirements().add(applicableReq);
					}
				}

				String[] reasonCds = appDAO
						.retrieveTVApplicationReasonCds(appId);

				if (reasonCds != null && reasonCds.length > 0) {
					tvApp.setPermitReasonCd(reasonCds[0]);
				}

				String facWideReqFlag = tvApp.getFacilityWideRequirementFlag();

				if (!Utility.isNullOrEmpty(facWideReqFlag)
						&& facWideReqFlag.equals(TVFacWideReqOptionDef.SUBJECT)) {
					List<FacilityWideRequirement> facWideReqs = appDAO
							.retrieveFacilityWideRequirements(tvApp
									.getApplicationID());
					tvApp.setFacilityWideRequirements(facWideReqs);
				} else {
					tvApp.setFacilityWideRequirements(new ArrayList<FacilityWideRequirement>());
				}

				// set TV Application subparts
				resetTVApplicationSubparts(tvApp, trans);
			}
		} else {
			appEUs = appDAO.retrieveApplicationEmissionUnits(appId);
		}
		for (ApplicationEU appEU : appEUs) {
			if (!shallow) {
				ApplicationEUTypeDAO appEUTypeDAO = null;
				if (appEU instanceof PTIOApplicationEU) {
					boolean stagedSchema = !isInternalApp() && trans != null;
					appEUTypeDAO = getApplicationEUTypeDAO(appEU.getFpEU()
							.getEmissionUnitTypeCd(), trans, stagedSchema);
				}

				populateApplicationEUComponents(appEU, appDAO, appEUTypeDAO,
						staging);
			}
			application.addEu(appEU);
		}

		// this must be done after EUs are retrieved and updated
		if (ptioApp != null) {
			// update purpose codes to reflect values in EUs
			updatePTIOApplicationPurposeCds(ptioApp);
		}
		if (tvApp != null && !shallow) {
			tvApp.setCapPteTotals(calculateTvCapPteTotals(tvApp, trans));
			tvApp.setHapPteTotals(calculateTvHapPteTotals(tvApp, trans));
			tvApp.setGhgPteTotals(calculateTvGhgPteTotals(tvApp, trans));
			tvApp.setOthPteTotals(calculateTvOthPteTotals(tvApp, trans));
		}
		if (!shallow) {
			// make sure all emission units in the application map to emission
			// units
			// in the facility (unless this is an RPE or RPR request).
			if (!application.getClass().equals(RPERequest.class)
					&& !application.getClass().equals(RPRRequest.class)) {
				// retrieve all Emission Units for the facility
				FacilityDAO facDAO = facilityDAO(trans);

				Map<Integer, EmissionUnit> fpEUs = new HashMap<Integer, EmissionUnit>();

				for (EmissionUnit fpEU : facDAO
						.retrieveFacilityEmissionUnits(application
								.getFacility().getFpId())) {
					fpEUs.put(fpEU.getCorrEpaEmuId(), fpEU);
					application.getFacility().addEmissionUnit(fpEU);
				}
				for (ApplicationEU eu : application.getIncludedEus()) {
					EmissionUnit fpEU = fpEUs.get(eu.getFpEU()
							.getCorrEpaEmuId());
					if (fpEU == null) {
						throw new DAOException(
								"internal error in retrieveApplication - "
										+ "emission unit with corrEpaEmuId="
										+ eu.getFpEU().getCorrEpaEmuId()
										+ " not found in facility (fpid="
										+ application.getFacility().getFpId()
										+ ")");
					}

					eu.setFpEU(fpEU);
				}
			}

			// notes only apply to internal AQD application
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				ApplicationNote[] notes = retrieveApplicationNotes(appId);
				for (ApplicationNote note : notes) {
					application.addApplicationNote(note);
				}
			}
		}
	}
	*/
	
	private void resetTVApplicationSubparts(TVApplication tvApp,
			Transaction trans) throws DAOException {
		int appId = tvApp.getApplicationID();
		ApplicationDAO appDAO = applicationDAO(trans);
		String nspsFlag = tvApp.getNspsApplicableFlag();
		String neshapFlag = tvApp.getNeshapApplicableFlag();
		String mactFlag = tvApp.getMactApplicableFlag();

		boolean isSujectNSPS = !Utility.isNullOrEmpty(nspsFlag)
				&& (nspsFlag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || nspsFlag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		boolean isSujectNESHAP = !Utility.isNullOrEmpty(neshapFlag)
				&& (neshapFlag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || neshapFlag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		boolean isSujectMACT = !Utility.isNullOrEmpty(mactFlag)
				&& (mactFlag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || mactFlag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		if (isSujectNSPS) {
			List<String> subparts = appDAO.retrieveTvAppNSPSSubpartCds(appId);
			tvApp.setNspsSubpartCodes(subparts);
		}

		if (isSujectNESHAP) {
			List<String> subparts = appDAO.retrieveTvAppNESHAPSubpartCds(appId);
			tvApp.setNeshapSubpartCodes(subparts);
		}

		if (isSujectMACT) {
			List<String> subparts = appDAO.retrieveTvAppMACTSubpartCds(appId);
			tvApp.setMactSubpartCodes(subparts);
		}
	}

	private void updateTVApplicationSubparts(ApplicationDAO appDAO,
			TVApplication tvApp) throws DAOException {
		int appId = tvApp.getApplicationID();

		String nspsFlag = tvApp.getNspsApplicableFlag();
		String neshapFlag = tvApp.getNeshapApplicableFlag();
		String mactFlag = tvApp.getMactApplicableFlag();

		boolean isSujectNSPS = !Utility.isNullOrEmpty(nspsFlag)
				&& (nspsFlag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || nspsFlag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		boolean isSujectNESHAP = !Utility.isNullOrEmpty(neshapFlag)
				&& (neshapFlag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || neshapFlag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		boolean isSujectMACT = !Utility.isNullOrEmpty(mactFlag)
				&& (mactFlag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || mactFlag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		appDAO.removeTvAppNSPSSubpartCds(appId);
		appDAO.removeTvAppNESHAPSubpartCds(appId);
		appDAO.removeTvAppMACTSubpartCds(appId);

		if (isSujectNSPS) {
			List<String> subparts = tvApp.getNspsSubpartCodes();
			for (String subpart : subparts) {
				appDAO.addTvAppNSPSSubpartCd(appId, subpart);
			}
		}

		if (isSujectNESHAP) {
			List<String> subparts = tvApp.getNeshapSubpartCodes();
			for (String subpart : subparts) {
				appDAO.addTvAppNESHAPSubpartCd(appId, subpart);
			}
		}

		if (isSujectMACT) {
			List<String> subparts = tvApp.getMactSubpartCodes();
			for (String subpart : subparts) {
				appDAO.addTvAppMACTSubpartCd(appId, subpart);
			}
		}
	}

	private void populateTVApplicableReqComponents(TVApplicableReq req,
			ApplicationDAO appDAO) throws DAOException {
		int reqId = req.getTvApplicableReqId();

		TVCompliance[] complianceReqs = appDAO
				.retrieveTvComplianceForAppReq(reqId);
		req.setComplianceReqs(Utility.createArrayList(complianceReqs));

		TVComplianceObligations[] complianceObligReqs = appDAO
				.retrieveTvComplianceObligationsForAppReq(reqId);
		req.setComplianceObligationsReqs(Utility
				.createArrayList(complianceObligReqs));

		TVProposedExemptions[] exemptionsReqs = appDAO
				.retrieveTvProposedExemptionsForAppReq(reqId);
		req.setProposedExemptionsReqs(Utility.createArrayList(exemptionsReqs));

		TVProposedAltLimits[] altLimitsReqs = appDAO
				.retrieveTvProposedAltLimitsForAppReq(reqId);
		req.setProposedAltLimitsReqs(Utility.createArrayList(altLimitsReqs));

		TVProposedTestChanges[] testChangesReqs = appDAO
				.retrieveTvProposedTestChangesForAppReq(reqId);
		req.setProposedTestChangesReqs(Utility.createArrayList(testChangesReqs));
	}

	/**
	 * Retrieve an application by its application number from the READ ONLY
	 * schema. This should only be used within the portal application to
	 * retrieve applications that have already been submitted and are no longer
	 * in the staging area.
	 * 
	 * @param applicationNumber
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
/*
	public Application retrieveApplication(String applicationNumber)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.READONLY_SCHEMA));

		Application application = appDAO.retrieveApplication(applicationNumber);

		if (application != null) {
			populateApplicationComponents(application, null, false, false);
		} else {
			logger.warn("No application found for application number: "
					+ applicationNumber);
		}
		return application;
	}
*/
	/**
	 * @param application
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public void setSubmitterUser(Application application) {
		GregorianCalendar defaultDeployDate = new GregorianCalendar(2008, 0, 1);
		Timestamp deployDate = new Timestamp(
				defaultDeployDate.getTimeInMillis());
		String deployDateStr = SystemPropertyDef.getSystemPropertyValue("STARS2_Deploy_Date", null);
		if (deployDateStr != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			try {
				java.util.Date date = dateFormat.parse(deployDateStr);
				deployDate = new Timestamp(date.getTime());
			} catch (ParseException e) {
				logger.error("Parameter 'STARS2_Deploy_Date' has an invalid format. "
						+ "Expected format is MM/dd/yyyy; parameter value is: '"
						+ deployDateStr
						+ " for "
						+ application.getApplicationNumber());
			}
		}
		String[] labelValue = new String[2];
		labelValue[0] = "Submitted: ";
		labelValue[1] = "No";
		if (application.getSubmittedDate() != null) {
			labelValue[1] = "Yes";
			if (!application.getSubmittedDate().before(deployDate)) {
				try {
					InfrastructureService infraBO = ServiceFactory
							.getInstance().getInfrastructureService();
					// Delegation of Responsibility
					labelValue = infraBO.retreiveSubmittingAppUser(application);
				} catch (ServiceFactoryException fse) {
					// do not generate an error
				} catch (RemoteException re) {
					// do not generate an error.
				}
			}
		}
		application.setSubmitLabel(labelValue[0]);
		application.setSubmitValue(labelValue[1]);
	}

	/**
	 * Retrieve a relocation application by its application number from the READ
	 * ONLY schema and provide the facility data for the related facility. This
	 * should only be used within the portal application to retrieve relocation
	 * requests that have already been submitted and are no longer in the
	 * staging area.
	 * 
	 * @param applicationNumber
	 * @param facility
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Application retrieveRelocationApplication(String applicationNumber,
			Facility facility) throws DAOException, RemoteException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.READONLY_SCHEMA));

		Application application = retrieveApplication(applicationNumber);
		application.setFacility(facility);
		if (application == null) {
			logger.warn("No application found for application number: "
					+ applicationNumber);
		}
		return application;
	}

	/**
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public SimpleDef[] retrieveApplicationTypes() throws DAOException {
		ApplicationDAO appDAO = applicationDAO();

		return appDAO.retrieveApplicationTypes();
	}

	/**
	 * @param appEUId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public ApplicationEU retrieveApplicationEU(Integer appEUId, Transaction trans, boolean staging)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		ApplicationEU appEU = appDAO.retrieveApplicationEmissionUnit(appEUId);

		if (appEU != null) {
			ApplicationEUTypeDAO appEUTypeDAO = null;
			EmissionUnitTypeDAO euTypeDAO = null;
			if (appEU instanceof PTIOApplicationEU) {
				appEUTypeDAO = getApplicationEUTypeDAO(appEU.getFpEU()
						.getEmissionUnitTypeCd(), trans, staging);
				euTypeDAO = getEUTypeDAO(
						appEU.getFpEU().getEmissionUnitTypeCd(), trans, staging);
			}
			populateApplicationEUComponents(appEU, appDAO, appEUTypeDAO, euTypeDAO, staging);
		}
		return appEU;
	}
	
	// Use this version when you want to operation on schema associated with IMPACT application type (dbo for internal and staging for portal).
	public ApplicationEU retrieveApplicationEU(Integer appEUId)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		ApplicationEU appEU = appDAO.retrieveApplicationEmissionUnit(appEUId);
		
		boolean staging = false;
		if (isPortalApp()) {
			staging = true;
		}

		if (appEU != null) {
			ApplicationEUTypeDAO appEUTypeDAO = null;
			EmissionUnitTypeDAO euTypeDAO = null;
			if (appEU instanceof PTIOApplicationEU) {
				appEUTypeDAO = getApplicationEUTypeDAO(appEU.getFpEU()
						.getEmissionUnitTypeCd(), null, staging);
				euTypeDAO = getEUTypeDAO(												
						appEU.getFpEU().getEmissionUnitTypeCd(), null, staging);			
			}
			populateApplicationEUComponents(appEU, appDAO, appEUTypeDAO, euTypeDAO, staging);
		}
		return appEU;
	}

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public ApplicationEU[] retrieveApplicationEUs(Integer appId)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));

		ApplicationEU[] appEUs = appDAO.retrieveApplicationEmissionUnits(appId);
		for (ApplicationEU appEU : appEUs) {
			ApplicationEUTypeDAO appEUTypeDAO = null;
			EmissionUnitTypeDAO euTypeDAO = null;
			if (appEU instanceof PTIOApplicationEU) {
				appEUTypeDAO = getApplicationEUTypeDAO(appEU.getFpEU()
						.getEmissionUnitTypeCd(), null, true);
				euTypeDAO = getEUTypeDAO(												
						appEU.getFpEU().getEmissionUnitTypeCd(), null, true);
			}
			populateApplicationEUComponents(appEU, appDAO, appEUTypeDAO, euTypeDAO, true);
		}

		return appEUs;
	}

	/**
	 * Perform additional database retrievals to retrieve components that are
	 * part of an application EU.
	 * 
	 * @param appEU
	 * @throws DAOException
	 */
	private void populateApplicationEUComponents(ApplicationEU appEU,
			ApplicationDAO appDAO, ApplicationEUTypeDAO appEUTypeDAO,
			EmissionUnitTypeDAO euTypeDAO,
			boolean staging) throws DAOException {

		ApplicationDocumentRef[] documents = null;
		Integer appEUId = appEU.getApplicationEuId();
		
		if (appEU.getClass().equals(PTIOApplicationEU.class)) {
			// populate PTIO-specific fields

			// add federal limit reason codes and EU purpose codes
			String[] reasonCds = appDAO
					.retrievePTIOEUFedLimitReasonCds(appEUId);
			((PTIOApplicationEU) appEU).setFederalLimitsReasonCDs(Utility
					.createArrayList(reasonCds));
			String[] purposeCds = appDAO.retrievePTIOEUPurposeCds(appEUId);
			((PTIOApplicationEU) appEU).setPtioEUPurposeCDs(Utility
					.createArrayList(purposeCds));

			// add emissions (pollutant) info
			ApplicationEUEmissions[] pollutants = appDAO
					.retrieveApplicationEUEmissions(appEUId);
			List<ApplicationEUEmissions> capList = new ArrayList<ApplicationEUEmissions>();
			List<ApplicationEUEmissions> ghgList = new ArrayList<ApplicationEUEmissions>();
			List<ApplicationEUEmissions> hapList = new ArrayList<ApplicationEUEmissions>();
			for (ApplicationEUEmissions pol : pollutants) {
				
				ApplicationEUEmissions pollutant = new ApplicationEUEmissions(pol);
				
				// add CAP pollutants to appropriate list
				if (pollutant.getEuEmissionTableCd().equals(
						ApplicationEUEmissionTableDef.CAP_TABLE_CD)) {
					
					capList.add(pollutant);
					
				} else if (pollutant.getEuEmissionTableCd().equals(
						ApplicationEUEmissionTableDef.GHG_TABLE_CD)) {
					BigDecimal co2e = new BigDecimal("0");
					try {
						co2e = PollutantDef.computeBigDecimalCO2Equivalent(
								pollutant.getPollutantCd(),
								pollutant.getPotentialToEmitTonYr());
					} catch (NumberFormatException e) {
						logger.error("Invalid value for CO2 Equivalent", e);
					}
					pollutant
							.setCo2Equivalent(getEmissionBigDecimalValueAsString(
									pollutant.getPollutantCd(),
									pollutant.getEuEmissionTableCd(), co2e));
					ghgList.add(pollutant);
				} else {
					hapList.add(pollutant);
				}
			}
			
			((PTIOApplicationEU) appEU).setCapEmissions(getSortedCapEmissions(capList));
			((PTIOApplicationEU) appEU).setGhgEmissions(ghgList);
			((PTIOApplicationEU) appEU).setHapTacEmissions(hapList);	
			
			if (((PTIOApplicationEU) appEU).getNspsApplicableFlag() != null
					&& (((PTIOApplicationEU) appEU).getNspsApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || ((PTIOApplicationEU) appEU)
							.getNspsApplicableFlag()
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT))) {
				String[] subparts = appDAO
						.retrievePTIOAppNSPSSubpartCds(appEUId);
				((PTIOApplicationEU) appEU).setNspsSubpartCodes(Utility
						.createArrayList(subparts));
			}
			if (((PTIOApplicationEU) appEU).getNeshapApplicableFlag() != null
					&& (((PTIOApplicationEU) appEU).getNeshapApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || (((PTIOApplicationEU) appEU)
							.getNeshapApplicableFlag()
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)))) {
				String[] subparts = appDAO
						.retrievePTIOAppNESHAPSubpartCds(appEUId);
				((PTIOApplicationEU) appEU).setNeshapSubpartCodes(Utility
						.createArrayList(subparts));
			}
			if (((PTIOApplicationEU) appEU).getMactApplicableFlag() != null
					&& (((PTIOApplicationEU) appEU).getMactApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || ((PTIOApplicationEU) appEU)
							.getMactApplicableFlag()
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT))) {
				String[] subparts = appDAO
						.retrievePTIOAppMACTSubpartCds(appEUId);
				((PTIOApplicationEU) appEU).setMactSubpartCodes(Utility
						.createArrayList(subparts));
			}
			

			// populate BACT emissions
			NSRApplicationBACTEmission[] bactEmissions = appDAO
					.retrieveNSRApplicationBactEmissions(appEUId);
			((PTIOApplicationEU) appEU).setBactEmissions(Utility
					.createArrayList(bactEmissions));

			// populate LAER emissions
			NSRApplicationLAEREmission[] laerEmissions = appDAO
					.retrieveNSRApplicationLaerEmissions(appEUId);
			((PTIOApplicationEU) appEU).setLaerEmissions(Utility
					.createArrayList(laerEmissions));

			// populate EU type
			if (appEUTypeDAO != null) { // TODO suspect this hides nasty errors,
										// should throw an NPE?
				NSRApplicationEUType appEuType = appEUTypeDAO
						.retrieveApplicationEUType(appEUId);

				if (appEuType.getApplicationEuId() == null) {
					// Application EU type is missing
					appEuType.setApplicationEuId(appEU.getApplicationEuId());
				}
				
				((PTIOApplicationEU) appEU).setEuType(appEuType); 
				if (appEuType.getEmissionUnitTypeCd() != null
						&& appEuType.getEmissionUnitTypeCd().equals("FUG")) {
					String fugTypeCd = ((NSRApplicationEUTypeFUG) appEuType)
							.getFugitiveEmissionTypeCd();
					if (fugTypeCd != null
							&& fugTypeCd
									.equals(AppEUFUGEmissionTypeDef.FIGUTIVE_LEAK_AT_OG))
						((PTIOApplicationEU) appEU)
								.setApplicationEUFugitiveLeaks(appDAO
										.retrieveApplicationEUFugitiveLeaks(appEU
												.getApplicationEuId()));
				}

				ApplicationEUMaterialUsed[] materialUsed = appDAO
						.retrieveApplicationEUMaterialUsed(appEUId);
				((PTIOApplicationEU) appEU).setMaterialUsed(Utility
						.createArrayList(materialUsed));

				// Set validation flags for EGU, CSH and TNK type emission
				// units.
				// The flags are set based on the value of Unit Type and type of
				// material stored attributes
				// in the facility inventory for these emission unit types.
				if (appEuType.getEmissionUnitTypeCd() != null) {
					if (euTypeDAO != null) {
						EmissionUnitType euType = euTypeDAO
								.retrieveEmissionUnitType(appEU
										.getFpEU().getEmuId());
						
						if (euType != null) {
							euType.setEmissionUnitTypeCd(appEU.getFpEU()
									.getEmissionUnitTypeCd());
						} else {
							euType = new EmissionUnitType();
							euType.setEmissionUnitTypeCd(appEU.getFpEU()
									.getEmissionUnitTypeCd());
						}
						appEU.getFpEU().setEmissionUnitType(euType);
						
						if (appEuType.getEmissionUnitTypeCd().equals(
								EmissionUnitTypeDef.EGU)) {
							((NSRApplicationEUTypeEGU) appEuType)
									.loadFpEuTypeData(euType);
						} else if (appEuType.getEmissionUnitTypeCd().equals(
								EmissionUnitTypeDef.CSH)) {
							((NSRApplicationEUTypeCSH) appEuType)
									.loadFpEuTypeData(euType);
						} else if (appEuType.getEmissionUnitTypeCd().equals(
								EmissionUnitTypeDef.TNK)) {
							((NSRApplicationEUTypeTNK) appEuType)
									.loadFpEuTypeData(euType);
						} else if (appEuType.getEmissionUnitTypeCd().equals(
								EmissionUnitTypeDef.ENG)) {
							((NSRApplicationEUTypeENG) appEuType)
									.loadFpEuTypeData(euType);
						} else if (appEuType.getEmissionUnitTypeCd().equals(
								EmissionUnitTypeDef.SEB)) {
							((NSRApplicationEUTypeSEB) appEuType)
									.loadFpEuTypeData(euType);
						}
					}
				}
			}

			// retrieve NSR EU documents
			documents = appDAO.retrieveNSRApplicationEUDocuments(appEUId);

		} else if (appEU.getClass().equals(TVApplicationEU.class)) {
			TVApplicationEU tvAppEU = (TVApplicationEU) appEU;
			Integer tvAppEUId = tvAppEU.getApplicationEuId();

			// subparts
			if (tvAppEU.getNspsApplicableFlag() != null
					&& (tvAppEU.getNspsApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || tvAppEU
							.getNspsApplicableFlag().equals(
									PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT))) {
				String[] subparts = appDAO
						.retrieveTvEuAppNSPSSubpartCds(tvAppEUId);
				tvAppEU.setNspsSubpartCodes(Utility.createArrayList(subparts));
			}
			if (tvAppEU.getNeshapApplicableFlag() != null
					&& (tvAppEU.getNeshapApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || (tvAppEU
							.getNeshapApplicableFlag()
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)))) {
				String[] subparts = appDAO
						.retrieveTvEuAppNESHAPSubpartCds(tvAppEUId);
				tvAppEU.setNeshapSubpartCodes(Utility.createArrayList(subparts));
			}
			if (tvAppEU.getMactApplicableFlag() != null
					&& (tvAppEU.getMactApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || tvAppEU
							.getMactApplicableFlag().equals(
									PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT))) {
				String[] subparts = appDAO
						.retrieveTvEuAppMACTSubpartCds(tvAppEUId);
				tvAppEU.setMactSubpartCodes(Utility.createArrayList(subparts));
			}

			// pollutant limits
			TVEUPollutantLimit[] pollutantLimits = appDAO
					.retrieveTVEUPollutantLimits(tvAppEU.getApplicationEuId());

			for (TVEUPollutantLimit pollutantLimit : pollutantLimits) {
				TVEUPollutantLimit pl = new TVEUPollutantLimit(pollutantLimit);
				tvAppEU.getPollutantLimits().add(pl);
			}

			// operational restrictions
			TVEUOperationalRestriction[] operationalRestrictions = appDAO
					.retrieveTVEUOperationalRestrictions(tvAppEU
							.getApplicationEuId());
			for (TVEUOperationalRestriction operationalRestriction : operationalRestrictions) {
				tvAppEU.getOperationalRestrictions()
						.add(operationalRestriction);
			}

			// operating scenarios
			TVEUOperatingScenario[] scenarios = appDAO
					.retrieveTVEUOperatingScenarios(appEU.getApplicationEuId());
			for (TVEUOperatingScenario scenario : scenarios) {
				TVApplicationEUEmissions[] emissions = appDAO
						.retrieveTVApplicationEUEmissions(
								appEU.getApplicationEuId(),
								scenario.getTvEuOperatingScenarioId());

				List<TVApplicationEUEmissions> capList = new ArrayList<TVApplicationEUEmissions>();
				List<TVApplicationEUEmissions> othList = new ArrayList<TVApplicationEUEmissions>();
				List<TVApplicationEUEmissions> ghgList = new ArrayList<TVApplicationEUEmissions>();
				List<TVApplicationEUEmissions> hapList = new ArrayList<TVApplicationEUEmissions>();
				for (TVApplicationEUEmissions emission : emissions) {
					// set major status based on PTE value. This is calculated
					// on
					// each read so it may be adjusted if threshold changes.
					emission.setMajorStatus(getTVPTEStatus(
							emission.getPollutantCd(),
							emission.getEuEmissionTableCd(),
							emission.getPteTonsYr(), new BigDecimal("0")));
					if (emission.getEuEmissionTableCd().equals(
							ApplicationEUEmissionTableDef.CAP_TABLE_CD)) {
						capList.add(emission);
					} else if (emission.getEuEmissionTableCd().equals(
							ApplicationEUEmissionTableDef.OTH_TABLE_CD)) {
						othList.add(emission);
					} else if (emission.getEuEmissionTableCd().equals(
							ApplicationEUEmissionTableDef.GHG_TABLE_CD)) {
						float co2e = 0f;
						try {
							co2e = PollutantDef.computeCO2Equivalent(
									emission.getPollutantCd(),
									emission.getPteTonsYr());
						} catch (ParseException e) {
							logger.error("Invalid value for CO2 Equivalent", e);
						}
						emission.setCo2Equivalent(getEmissionValueAsString(
								emission.getPollutantCd(),
								emission.getEuEmissionTableCd(),
								new Float(co2e)));
						ghgList.add(emission);
					} else {
						hapList.add(emission);
					}
				}
				
				scenario.setCapEmissions(getSortedTvCapEmissions(capList));
				scenario.setOthEmissions(othList);
				scenario.setGhgEmissions(ghgList);
				scenario.setHapEmissions(hapList);
				
				for (TVAltScenarioPteReq req : appDAO
						.retrieveTVAltScenarioPteReqsForScenario(
								appEU.getApplicationEuId(),
								scenario.getTvEuOperatingScenarioId())) {
					scenario.getPteRequirements().add(req);
				}
				// add CAP pollutants to appropriate list
				if (scenario.getTvEuOperatingScenarioId().intValue() == 0) {
					tvAppEU.setNormalOperatingScenario(scenario);
				} else {
					// all other pollutants go in the HAP table
					tvAppEU.getAlternateOperatingScenarios().add(scenario);
				}
			}
			TVApplicableReq[] applicableReqs = appDAO
					.retrieveTVApplicableReqsForEU(appEU.getApplicationEuId());
			for (TVApplicableReq applicableReq : applicableReqs) {
				populateTVApplicableReqComponents(applicableReq, appDAO);
				if (!applicableReq.isStateOnly()) {
					tvAppEU.getApplicableRequirements().add(applicableReq);
				} else {
					tvAppEU.getStateOnlyRequirements().add(applicableReq);
				}
			}
			
			// retrieve TV EU documents
			documents = appDAO.retrieveTVApplicationEUDocuments(appEUId);
		} else {
			// retrieve Other EU documents
			documents = appDAO.retrieveApplicationEUDocuments(appEUId);
		}

		appEU.setEuDocuments(Utility.createArrayList(documents));
		
		for (ApplicationDocumentRef docRef : appEU.getEuDocuments()) {
			loadDocuments(docRef, !staging);
		}
	}
	
	/**
	 * Return CAP emissions making sure they're in the correct order.
	 * 
	 * @return
	 */
	public List<ApplicationEUEmissions> getSortedCapEmissions(
			List<ApplicationEUEmissions> capList) {
		List<ApplicationEUEmissions> sortedCapEmissions = null;

		sortedCapEmissions = new ArrayList<ApplicationEUEmissions>();
		if (capList != null) {
			for (String pollutantCd : ApplicationBO
					.getPTIOCapPollutantCodesOrdered()) {
				for (ApplicationEUEmissions emissions : capList) {
					if (emissions.getPollutantCd().equals(pollutantCd)) {
						sortedCapEmissions.add(emissions);
						break;
					}
				}
			}
		}

		return sortedCapEmissions;
	}
	
	public List<TVApplicationEUEmissions> getSortedTvCapEmissions(
			List<TVApplicationEUEmissions> capList) {
		List<TVApplicationEUEmissions> sortedCapEmissions = new ArrayList<TVApplicationEUEmissions>();
		if (capList != null) {
			TreeMap<String, TVApplicationEUEmissions> sortedMap = new TreeMap<String, TVApplicationEUEmissions>();
			for (TVApplicationEUEmissions emission : capList) {
				String label = new String(PollutantDef.getData().getItems()
						.getItemDesc(emission.getPollutantCd()));
				TVApplicationEUEmissions em = new TVApplicationEUEmissions(
						emission);
				sortedMap.put(label, em);
			}

			for (String label : sortedMap.keySet()) {
				sortedCapEmissions.add(new TVApplicationEUEmissions(sortedMap
						.get(label)));
			}
		}
		return sortedCapEmissions;

	}

	/**
	 * Retrieve the application document identified by appDocId.
	 * 
	 * @param appDocId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public ApplicationDocumentRef retrieveApplicationDocument(String appTypeCD, Integer appDocId,
			boolean readOnly) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		if (ApplicationTypeDef.PTIO_APPLICATION.equals(appTypeCD)) {
			return appDAO.retrieveNSRApplicationDocument(appDocId);
		} else if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(appTypeCD)) {
			return appDAO.retrieveTVApplicationDocument(appDocId);
		} else {
			return appDAO.retrieveApplicationDocument(appDocId);
		}
	}
	
	/**
	 * Retrieve all application-wide documents related to the application
	 * identified by appTypeCD and appId.
	 * 
	 * @param appTypeCD
	 *            application type code.
	 *            
	 * @param appId
	 *            application identifier.
	 *            
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public ApplicationDocumentRef[] retrieveApplicationDocuments(String appTypeCD, Integer appId)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		if (ApplicationTypeDef.PTIO_APPLICATION.equals(appTypeCD)) {
			return appDAO.retrieveNSRApplicationDocuments(appId);
		} else if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(appTypeCD)) {
			return appDAO.retrieveTVApplicationDocuments(appId);
		} else {
			return appDAO.retrieveApplicationDocuments(appId);
		}
	}

	@Override
	public ApplicationDocumentRef[] retrieveApplicationEUDocuments(String appTypeCD,
			Integer appEuId) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		if (ApplicationTypeDef.PTIO_APPLICATION.equals(appTypeCD)) {
			return appDAO.retrieveNSRApplicationEUDocuments(appEuId);
		} else if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(appTypeCD)) {
			return appDAO.retrieveTVApplicationEUDocuments(appEuId);
		} else {
			return appDAO.retrieveApplicationEUDocuments(appEuId);
		}
	}

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Permit[] retrievePermitsForApplication(Integer appId)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.READONLY_SCHEMA));
		PermitDAO permitDAO = permitDAO(getSchema(CommonConst.READONLY_SCHEMA));
		Permit[] permits = appDAO.retrievePermitsForApplication(appId);
		for (int i = 0; i < permits.length; i++) {
			permits[i].setPermitIssuances(permitDAO.retrievePermitIssuances(permits[i].getPermitID()));
		}
		return permits;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public ApplicationSearchResult[] retrieveApplicationSearchResults(
			String applicationNumber, String euId, String facilityId,
			String facilityName, String doLaaCd, String countyCd,
			String applicationType, String ptioReasonCd,
			boolean legacyStatePTOFlag, String pbrTypeCd, String permitNumber,
			String companyName, boolean unlimitedResults) throws DAOException {

		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.READONLY_SCHEMA));

		return appDAO.retrieveApplicationSearchResults(applicationNumber, euId,
				facilityId, facilityName, doLaaCd, countyCd, applicationType,
				ptioReasonCd, legacyStatePTOFlag, pbrTypeCd, permitNumber,
				companyName, unlimitedResults);
	}

	/**
	 * @param newAppEu
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ApplicationEU createApplicationEU(ApplicationEU newAppEu)
			throws DAOException {

		Transaction trans = null;
		ApplicationEU ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ret = createApplicationEU(newAppEu, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param newAppEu
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private ApplicationEU createApplicationEU(ApplicationEU newAppEu,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		// create application EU
		ApplicationEU ret = appDAO.createApplicationEU(newAppEu);

		// Do type specfic creates...
		if (ret instanceof PTIOApplicationEU) {
			PTIOApplicationEU ptioAppEU = (PTIOApplicationEU) ret;
			ptioAppEU = appDAO.createPTIOApplicationEU(ptioAppEU);

			// pre-populate CAP portion of emissions table (for new EUs only)
			ptioAppEU.getCapEmissions().addAll(
					getPTIOCAPEmissions(ptioAppEU.getApplicationEuId()));
			for (ApplicationEUEmissions emissions : ptioAppEU.getCapEmissions()) {
				appDAO.addApplicationEUEmissions(emissions);
			}

			// populate federal rules
			Integer ptioAppEuId = ptioAppEU.getApplicationEuId();

			// remove old subparts
			appDAO.removePTIOAppNSPSSubpartCds(ptioAppEuId);
			appDAO.removePTIOAppNESHAPSubpartCds(ptioAppEuId);
			appDAO.removePTIOAppMACTSubpartCds(ptioAppEuId);

			// add new subparts
			for (String nspsSubpartCd : ptioAppEU.getNspsSubpartCodes()) {
				appDAO.addPTIOAppNSPSSubpartCd(ptioAppEuId, nspsSubpartCd);
			}
			for (String neshapsSubpartCd : ptioAppEU.getNeshapSubpartCodes()) {
				appDAO.addPTIOAppNESHAPSubpartCd(ptioAppEuId, neshapsSubpartCd);
			}
			for (String mactSubpartCd : ptioAppEU.getMactSubpartCodes()) {
				appDAO.addPTIOAppMACTSubpartCd(ptioAppEuId, mactSubpartCd);
			}

			// populate BACT emissions
			appDAO.removeBACTEmissions(ptioAppEU.getApplicationEuId());
			if (ptioAppEU.isBactAnalysisCompleted()) {
				for (NSRApplicationBACTEmission bactEmission : ptioAppEU
						.getBactEmissions()) {
					bactEmission.setApplicationEuId(ptioAppEU
							.getApplicationEuId());
					appDAO.addBACTEmission(bactEmission);
				}
			}

			// populate LAER emissions
			appDAO.removeLAEREmissions(ptioAppEU.getApplicationEuId());
			if (ptioAppEU.isLaerAnalysisCompleted()) {
				for (NSRApplicationLAEREmission laerEmission : ptioAppEU
						.getLaerEmissions()) {
					laerEmission.setApplicationEuId(ptioAppEU
							.getApplicationEuId());
					appDAO.addLAEREmission(laerEmission);
				}
			}
			if (ptioAppEU.getFpEU().getEmissionUnitTypeCd() != null
					&& ptioAppEU.getFpEU().getEmissionUnitTypeCd()
							.equals("CMX")) {
				ptioAppEU.getMaterialUsed().addAll(
						getPTIOMaterialUsed(ptioAppEU.getApplicationEuId()));
				for (ApplicationEUMaterialUsed materialUsed : ptioAppEU
						.getMaterialUsed()) {
					appDAO.addApplicationEUMaterialUsed(materialUsed);
				}
			}

		} else if (ret instanceof TVApplicationEU) {
			TVApplicationEU tvAppEU = (TVApplicationEU) ret;
			tvAppEU = appDAO.createTVApplicationEU(tvAppEU);

			// populate federal rules
			Integer tvAppEuId = tvAppEU.getApplicationEuId();

			// remove old subparts
			appDAO.removeTvEuAppNSPSSubpartCds(tvAppEuId);
			appDAO.removeTvEuAppNESHAPSubpartCds(tvAppEuId);
			appDAO.removeTvEuAppMACTSubpartCds(tvAppEuId);

			// add new subparts
			for (String nspsSubpartCd : tvAppEU.getNspsSubpartCodes()) {
				appDAO.addTvEuAppNSPSSubpartCd(tvAppEuId, nspsSubpartCd);
			}
			for (String neshapsSubpartCd : tvAppEU.getNeshapSubpartCodes()) {
				appDAO.addTvEuAppNESHAPSubpartCd(tvAppEuId, neshapsSubpartCd);
			}
			for (String mactSubpartCd : tvAppEU.getMactSubpartCodes()) {
				appDAO.addTvEuAppMACTSubpartCd(tvAppEuId, mactSubpartCd);
			}

			// create a default normal operating scenario
			// and populate its CAP emissions table with appropriate values
			TVEUOperatingScenario normalScenario = new TVEUOperatingScenario();
			normalScenario.setTvEuOperatingScenarioId(new Integer(0));
			normalScenario.setApplicationEuId(tvAppEU.getApplicationEuId());
			normalScenario.setCapEmissions(getTVCAPEmissions(
					tvAppEU.getApplicationEuId(),
					normalScenario.getTvEuOperatingScenarioId()));
			appDAO.createTVEUOperatingScenario(normalScenario);
			for (TVApplicationEUEmissions emissions : normalScenario
					.getCapEmissions()) {
				appDAO.addTVApplicationEUEmissions(emissions);
			}
			tvAppEU.setNormalOperatingScenario(normalScenario);
		}

		return ret;
	}

	private ApplicationEU createBaseApplicationEU(ApplicationEU newAppEu,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		// create application EU
		ApplicationEU ret = appDAO.createApplicationEU(newAppEu);

		// Do type specfic creates...
		if (ret instanceof PTIOApplicationEU) {
			PTIOApplicationEU ptioAppEU = (PTIOApplicationEU) ret;
			ptioAppEU = appDAO.createPTIOApplicationEU(ptioAppEU);
		} else if (ret instanceof TVApplicationEU) {
			TVApplicationEU tvAppEU = (TVApplicationEU) ret;
			tvAppEU = appDAO.createTVApplicationEU(tvAppEU);
		}

		return ret;
	}

	/**
	 * Create a copy of <code>appEU</code> and add it to <code>app</code>.
	 * 
	 * @param appEU
	 *            application EU to be copied.
	 * @param app
	 *            application to which EU will be added.
	 * @return reference to copied EU.
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ApplicationEU createApplicationEUCopy(ApplicationEU appEU,
			Application app) throws DAOException {
		Transaction trans = null;
		ApplicationEU copy = null;

		try {
			trans = TransactionFactory.createTransaction();
			copy = createApplicationEUCopy(appEU, app, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return copy;
	}

	private ApplicationEU createApplicationEUCopy(ApplicationEU origAppEU,
			Application app, Transaction trans) throws DAOException {
		ApplicationEU appEUCopy = null;

		if (origAppEU instanceof PTIOApplicationEU) {
			appEUCopy = new PTIOApplicationEU((PTIOApplicationEU) origAppEU);
		} else if (origAppEU instanceof TVApplicationEU) {
			appEUCopy = new TVApplicationEU((TVApplicationEU) origAppEU);
		} else {
			throw new DAOException("EUs of type " + origAppEU.getClass()
					+ " are not eligible for copy");
		}

		// change fields that should not be the same in the new copy
		appEUCopy.setApplicationEuId(null);
		appEUCopy.setValidated(false);
		appEUCopy.setApplicationId(app.getApplicationID());

		// remove documents from appEUCopy. Copies of documents from
		// origAppEU will be added after a new EU id is obtained.
		appEUCopy.setEuDocuments(new ArrayList<ApplicationDocumentRef>());

		// create application EU minus documents to get an EU id
		appEUCopy = createApplicationEUCopy(appEUCopy, trans);

		// add copies of documents from old EU, updating the application id
		// and application EU id to match the values for the new EU
		createApplicationEUDocumentCopies(origAppEU, appEUCopy, false, trans);

		return appEUCopy;
	}

	/**
	 * Create a new application EU object that contains the same data that is in
	 * appEU.
	 * 
	 * @param appEU
	 *            application EU to be copied.
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private ApplicationEU createApplicationEUCopy(ApplicationEU appEU,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		// create application EU
		ApplicationEU ret = appDAO.createApplicationEU(appEU);
		ret.setValidated(false);

		// Do type specific creates...
		Class<? extends ApplicationEU> incomingAppEuClass = appEU.getClass();
		if (incomingAppEuClass.equals(PTIOApplicationEU.class)) {
			PTIOApplicationEU ptioAppEU = (PTIOApplicationEU) appEU;
			ptioAppEU = appDAO.createPTIOApplicationEU(ptioAppEU);

			// set the application EU id of emissions to the new application EU
			// id
			for (ApplicationEUEmissions emissions : ptioAppEU.getCapEmissions()) {
				emissions.setApplicationEuId(ptioAppEU.getApplicationEuId());
			}
			for (ApplicationEUEmissions emissions : ptioAppEU
					.getHapTacEmissions()) {
				emissions.setApplicationEuId(ptioAppEU.getApplicationEuId());
			}
			for (ApplicationEUEmissions emissions : ptioAppEU.getGhgEmissions()) {
				emissions.setApplicationEuId(ptioAppEU.getApplicationEuId());
			}

			// setup federal rules
			Integer ptioAppEuId = ptioAppEU.getApplicationEuId();

			// remove old subparts
			appDAO.removePTIOAppNSPSSubpartCds(ptioAppEuId);
			appDAO.removePTIOAppNESHAPSubpartCds(ptioAppEuId);
			appDAO.removePTIOAppMACTSubpartCds(ptioAppEuId);

			// add new subparts
			for (String nspsSubpartCd : ptioAppEU.getNspsSubpartCodes()) {
				appDAO.addPTIOAppNSPSSubpartCd(ptioAppEuId, nspsSubpartCd);
			}
			for (String neshapsSubpartCd : ptioAppEU.getNeshapSubpartCodes()) {
				appDAO.addPTIOAppNESHAPSubpartCd(ptioAppEuId, neshapsSubpartCd);
			}
			for (String mactSubpartCd : ptioAppEU.getMactSubpartCodes()) {
				appDAO.addPTIOAppMACTSubpartCd(ptioAppEuId, mactSubpartCd);
			}

			// remove previous BACT emissions
			appDAO.removeBACTEmissions(ptioAppEU.getApplicationEuId());

			// add BACT emissions
			for (NSRApplicationBACTEmission bactEmission : ptioAppEU
					.getBactEmissions()) {
				bactEmission.setApplicationEuId(ptioAppEU.getApplicationEuId());
				appDAO.addBACTEmission(bactEmission);
			}

			// remove previous LAER emissions
			appDAO.removeLAEREmissions(ptioAppEU.getApplicationEuId());

			// add LAER emissions
			for (NSRApplicationLAEREmission laerEmission : ptioAppEU
					.getLaerEmissions()) {
				laerEmission.setApplicationEuId(ptioAppEU.getApplicationEuId());
				appDAO.addLAEREmission(laerEmission);
			}

			// add Material Used
			for (ApplicationEUMaterialUsed materialUsed : ptioAppEU
					.getMaterialUsed()) {
				materialUsed.setApplicationEuId(ptioAppEU.getApplicationEuId());
			}

			// update application eu id
			if (((PTIOApplicationEU) ret).getEuType() != null) {
				((PTIOApplicationEU) ret).getEuType().setApplicationEuId(
						ptioAppEuId);
			}
		} else if (incomingAppEuClass.equals(TVApplicationEU.class)) {
			TVApplicationEU tvAppEU = (TVApplicationEU) appEU;
			tvAppEU = appDAO.createTVApplicationEU(tvAppEU);

			// setup federal rules
			Integer tvAppEuId = tvAppEU.getApplicationEuId();

			// remove old subparts
			appDAO.removeTvEuAppNSPSSubpartCds(tvAppEuId);
			appDAO.removeTvEuAppNESHAPSubpartCds(tvAppEuId);
			appDAO.removeTvEuAppMACTSubpartCds(tvAppEuId);

			// add new subparts
			for (String nspsSubpartCd : tvAppEU.getNspsSubpartCodes()) {
				appDAO.addTvEuAppNSPSSubpartCd(tvAppEuId, nspsSubpartCd);
			}
			for (String neshapsSubpartCd : tvAppEU.getNeshapSubpartCodes()) {
				appDAO.addTvEuAppNESHAPSubpartCd(tvAppEuId, neshapsSubpartCd);
			}
			for (String mactSubpartCd : tvAppEU.getMactSubpartCodes()) {
				appDAO.addTvEuAppMACTSubpartCd(tvAppEuId, mactSubpartCd);
			}

			// set the application EU id of scenarios and emissions to
			// the new application EU id
			TVEUOperatingScenario scenario = tvAppEU
					.getNormalOperatingScenario();
			scenario.setApplicationEuId(tvAppEU.getApplicationEuId());
			for (TVApplicationEUEmissions emissions : scenario
					.getCapEmissions()) {
				emissions.setApplicationEuId(tvAppEU.getApplicationEuId());
			}
			for (TVApplicationEUEmissions emissions : scenario
					.getHapEmissions()) {
				emissions.setApplicationEuId(tvAppEU.getApplicationEuId());
			}
			for (TVApplicationEUEmissions emissions : scenario
					.getGhgEmissions()) {
				emissions.setApplicationEuId(tvAppEU.getApplicationEuId());
			}
			for (TVApplicationEUEmissions emissions : scenario
					.getOthEmissions()) {
				emissions.setApplicationEuId(tvAppEU.getApplicationEuId());
			}
			for (TVAltScenarioPteReq req : scenario.getPteRequirements()) {
				req.setApplicationEuId(tvAppEU.getApplicationEuId());
			}

			for (TVEUOperatingScenario altScenario : tvAppEU
					.getAlternateOperatingScenarios()) {
				altScenario.setApplicationEuId(tvAppEU.getApplicationEuId());
				for (TVApplicationEUEmissions emissions : altScenario
						.getCapEmissions()) {
					emissions.setApplicationEuId(tvAppEU.getApplicationEuId());
				}
				for (TVApplicationEUEmissions emissions : altScenario
						.getHapEmissions()) {
					emissions.setApplicationEuId(tvAppEU.getApplicationEuId());
				}
				for (TVApplicationEUEmissions emissions : altScenario
						.getGhgEmissions()) {
					emissions.setApplicationEuId(tvAppEU.getApplicationEuId());
				}
				for (TVAltScenarioPteReq req : altScenario.getPteRequirements()) {
					req.setApplicationEuId(tvAppEU.getApplicationEuId());
				}
			}
			// reset ids for applicable requirements
			for (TVApplicableReq req : tvAppEU.getApplicableRequirements()) {
				req.setApplicationId(tvAppEU.getApplicationId());
				req.setApplicationEuId(tvAppEU.getApplicationEuId());
				req.setTvApplicableReqId(appDAO
						.generateApplicableRequirementSeqNo());
			}
			// reset ids for state-only applicable requirements
			for (TVApplicableReq req : tvAppEU.getStateOnlyRequirements()) {
				req.setApplicationId(tvAppEU.getApplicationId());
				req.setApplicationEuId(tvAppEU.getApplicationEuId());
				req.setTvApplicableReqId(appDAO
						.generateApplicableRequirementSeqNo());
			}
		}
		ApplicationEUTypeDAO appEUTypeDAO = null;
		if (ret instanceof PTIOApplicationEU) {
			appEUTypeDAO = getApplicationEUTypeDAO(ret.getFpEU()
					.getEmissionUnitTypeCd(), trans, true);
		}

		updateApplicationEUComponents(ret, appDAO, appEUTypeDAO);

		return ret;
	}

	/**
	 * Create ApplicationDocumentRef objects in the database for all documents
	 * in fromAppEU and have the new objects refer to toAppEU.
	 * 
	 * @param fromAppEU
	 *            EU from which documents will be copied.
	 * @param toAppEU
	 *            EU to which documents will be copied.
	 * @param resetId
	 *            flag indicating whether a new document Id should be generated
	 *            for each document.
	 * @param trans
	 * @throws DAOException
	 */
	private void createApplicationEUDocumentCopies(ApplicationEU fromAppEU,
			ApplicationEU toAppEU, boolean resetIds, Transaction trans)
			throws DAOException {
		for (ApplicationDocumentRef docRef : fromAppEU.getEuDocuments()) {
			// logger.error("Debug #2966 Application document id = " +
			// docRef.getApplicationDocId()
			// + ", public document id = " + docRef.getDocumentId()
			// + ", document type cd " + docRef.getApplicationDocumentTypeCD());
			ApplicationDocumentRef docRefCopy = new ApplicationDocumentRef(
					docRef);
			docRefCopy.setApplicationId(toAppEU.getApplicationId());
			docRefCopy.setApplicationEUId(toAppEU.getApplicationEuId());
			if (copyApplicationDocumentRef(docRef, docRefCopy, trans) != null) {
				toAppEU.getEuDocuments().add(docRefCopy);
			}
		}
	}

	/**
	 * Copy application EU data from <code>source</code> to <code>dest</code>.
	 * It is assumed that <code>dest</code> already exists in the database.
	 * 
	 * @param source
	 *            Application EU from which data is to be copied.
	 * @param dest
	 *            Application EU to which data will be copied.
	 * @param app
	 *            Application to which both source and dest should belong.
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ApplicationEU copyApplicationEUData(ApplicationEU source,
			ApplicationEU dest) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			dest = copyApplicationEUData(source, dest, true, trans, false);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return dest;
	}

	private ApplicationEU copyApplicationEUData(ApplicationEU source,
			ApplicationEU dest, boolean resetIds, Transaction trans,
			boolean keepValidated) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		// only allow copy of like classes
		if (!source.getClass().equals(dest.getClass())) {
			throw new DAOException("Cannot copy EU of type"
					+ source.getClass().getName() + " to EU of type "
					+ dest.getClass().getName());
		}

		// maintain original App EU id and FP EU information
		Integer destAppEUId = dest.getApplicationEuId();
		EmissionUnit destFpEU = dest.getFpEU();
		Integer lastModified = dest.getLastModified();
		String destEmissionTypeCd = dest.getFpEU().getEmissionUnitTypeCd();

		// copy data from source to dest
		if (source.getClass().equals(PTIOApplicationEU.class)) {
			dest = new PTIOApplicationEU((PTIOApplicationEU) source);
			if (!destFpEU.getEmissionUnitTypeCd().equals(
					dest.getFpEU().getEmissionUnitTypeCd())) {
				((PTIOApplicationEU) dest).setEuType(null);
			} else {
				((PTIOApplicationEU) dest).getEuType().setApplicationEuId(
						destAppEUId);
			}

			if (destEmissionTypeCd.equals("CMX")) {
				if (!source.getFpEU().getEmissionUnitTypeCd().equals("CMX")) {
					((PTIOApplicationEU) dest).getMaterialUsed().addAll(
							getPTIOMaterialUsed(((PTIOApplicationEU) dest)
									.getApplicationEuId()));
				}
			} else {
				((PTIOApplicationEU) dest).setMaterialUsed(null);
			}
			if (!destEmissionTypeCd.equals("FUG")) {
				((PTIOApplicationEU) dest)
						.setApplicationEUFugitiveLeaks(new ArrayList<ApplicationEUFugitiveLeaks>(
								0));
			}

			// set application EU id to dest id where applicable
			for (ApplicationEUEmissions emissions : ((PTIOApplicationEU) dest)
					.getCapEmissions()) {
				emissions.setApplicationEuId(destAppEUId);
			}
			for (ApplicationEUEmissions emissions : ((PTIOApplicationEU) dest)
					.getHapTacEmissions()) {
				emissions.setApplicationEuId(destAppEUId);
			}
			for (ApplicationEUEmissions emissions : ((PTIOApplicationEU) dest)
					.getGhgEmissions()) {
				emissions.setApplicationEuId(destAppEUId);
			}
		} else if (source.getClass().equals(TVApplicationEU.class)) {
			dest = new TVApplicationEU((TVApplicationEU) source);
			// set application EU id to dest id where applicable
			TVEUOperatingScenario scenario = ((TVApplicationEU) dest)
					.getNormalOperatingScenario();
			scenario.setApplicationEuId(destAppEUId);
			for (TVApplicationEUEmissions emissions : scenario
					.getCapEmissions()) {
				emissions.setApplicationEuId(destAppEUId);
			}
			for (TVApplicationEUEmissions emissions : scenario
					.getHapEmissions()) {
				emissions.setApplicationEuId(destAppEUId);
			}
			for (TVApplicationEUEmissions emissions : scenario
					.getGhgEmissions()) {
				emissions.setApplicationEuId(destAppEUId);
			}
			for (TVApplicationEUEmissions emissions : scenario
					.getOthEmissions()) {
				emissions.setApplicationEuId(destAppEUId);
			}
			for (TVEUOperatingScenario altScenario : ((TVApplicationEU) dest)
					.getAlternateOperatingScenarios()) {
				altScenario.setApplicationEuId(destAppEUId);
				for (TVApplicationEUEmissions emissions : altScenario
						.getCapEmissions()) {
					emissions.setApplicationEuId(destAppEUId);
				}
				for (TVApplicationEUEmissions emissions : altScenario
						.getHapEmissions()) {
					emissions.setApplicationEuId(destAppEUId);
				}
				for (TVApplicationEUEmissions emissions : altScenario
						.getGhgEmissions()) {
					emissions.setApplicationEuId(destAppEUId);
				}
				for (TVApplicationEUEmissions emissions : altScenario
						.getOthEmissions()) {
					emissions.setApplicationEuId(destAppEUId);
				}
				for (TVAltScenarioPteReq req : altScenario.getPteRequirements()) {
					req.setApplicationEuId(destAppEUId);
				}
			}
			for (TVApplicableReq req : ((TVApplicationEU) dest)
					.getApplicableRequirements()) {
				if (resetIds) {
					req.setTvApplicableReqId(appDAO
							.generateApplicableRequirementSeqNo());
				}
				req.setApplicationEuId(destAppEUId);
				updateTVApplicableReqComponentIds(req);
			}
			for (TVApplicableReq req : ((TVApplicationEU) dest)
					.getStateOnlyRequirements()) {
				if (resetIds) {
					req.setTvApplicableReqId(appDAO
							.generateApplicableRequirementSeqNo());
				}
				req.setApplicationEuId(destAppEUId);
				updateTVApplicableReqComponentIds(req);
			}
		} else {
			dest = new ApplicationEU(source);
		}

		dest.setApplicationEuId(destAppEUId);
		dest.setFpEU(destFpEU);
		dest.setLastModified(lastModified);
		dest.setEuDocuments(new ArrayList<ApplicationDocumentRef>());
		dest.setValidated(keepValidated);
		// update application id and application eu id for documents
		// first, remove any existing documents for this EU
		appDAO.removeApplicationEUDocuments(dest.getApplicationEuId());
		createApplicationEUDocumentCopies(source, dest, resetIds, trans);

		modifyApplicationEU(dest, trans);

		return dest;
	}

	/**
	 * Set the applicableReqId value for components of <code>req</code> to req's
	 * applicableReqId. This is done when copying data from one entity
	 * (application, EU, EU group) to another.
	 * 
	 * @param req
	 */
	private void updateTVApplicableReqComponentIds(TVApplicableReq req) {
		int reqId = req.getTvApplicableReqId();

		for (TVCompliance compliance : req.getComplianceReqs()) {
			compliance.setTvApplicableReqId(reqId);
		}
		for (TVComplianceObligations obligations : req
				.getComplianceObligationsReqs()) {
			obligations.setTvApplicableReqId(reqId);
		}
		for (TVProposedExemptions exemptions : req.getProposedExemptionsReqs()) {
			exemptions.setTvApplicableReqId(reqId);
		}
		for (TVProposedAltLimits limits : req.getProposedAltLimitsReqs()) {
			limits.setTvApplicableReqId(reqId);
		}
		for (TVProposedTestChanges changes : req.getProposedTestChangesReqs()) {
			changes.setTvApplicableReqId(reqId);
		}
	}

	/**
	 * Update data for the components related to <code>appEU</code>.
	 * 
	 * @param appEU
	 * @param appDAO
	 * @throws DAOException
	 */
	private void updateApplicationEUComponents(ApplicationEU appEU,
			ApplicationDAO appDAO, ApplicationEUTypeDAO appEUTypeDAO)
			throws DAOException {

		int appEUId = appEU.getApplicationEuId();

		// Do type specfic modifications...
		if (appEU instanceof PTIOApplicationEU) {
			PTIOApplicationEU ptioAppEU = (PTIOApplicationEU) appEU;
			// add purpose codes
			appDAO.removePTIOEUPurposeCds(ptioAppEU.getApplicationEuId());
			for (String purposeCd : ptioAppEU.getPtioEUPurposeCDs()) {
				appDAO.createPTIOEUPurpose(appEUId, purposeCd);
			}
			// add federal limits reason codes
			appDAO.removePTIOEUFedLimitReasonCds(ptioAppEU.getApplicationEuId());
			for (String reasonCd : ptioAppEU.getFederalLimitsReasonCDs()) {
				appDAO.createPTIOEUFedLimitReason(appEUId, reasonCd);
			}

			// add CAP emissions (pollutant) info
			appDAO.removeApplicationEUEmissions(ptioAppEU.getApplicationEuId());
			for (ApplicationEUEmissions emissions : ptioAppEU.getCapEmissions()) {
				appDAO.addApplicationEUEmissions(emissions);
			}
			// add HAP/TAC emissions (pollutant) info
			for (ApplicationEUEmissions emissions : ptioAppEU
					.getHapTacEmissions()) {
				appDAO.addApplicationEUEmissions(emissions);
			}
			// add GHG emissions (pollutant) info
			for (ApplicationEUEmissions emissions : ptioAppEU.getGhgEmissions()) {
				appDAO.addApplicationEUEmissions(emissions);
			}

			// add BACT emissions
			appDAO.removeBACTEmissions(ptioAppEU.getApplicationEuId());
			if (ptioAppEU.isBactAnalysisCompleted()) {
				for (NSRApplicationBACTEmission bactEmission : ptioAppEU
						.getBactEmissions()) {
					bactEmission.setApplicationEuId(ptioAppEU
							.getApplicationEuId());
					appDAO.addBACTEmission(bactEmission);
				}
			}

			// add LAER emissions
			appDAO.removeLAEREmissions(ptioAppEU.getApplicationEuId());
			if (ptioAppEU.isLaerAnalysisCompleted()) {
				for (NSRApplicationLAEREmission laerEmission : ptioAppEU
						.getLaerEmissions()) {
					laerEmission.setApplicationEuId(ptioAppEU
							.getApplicationEuId());
					appDAO.addLAEREmission(laerEmission);
				}
			}

			// add EU type attributes
			if (appEUTypeDAO != null) {
				appEUTypeDAO.modifyApplicationEUType(ptioAppEU.getEuType());
			}

			// add EU type Fugitive -> Fugitive type Fugitive Leak at O&G
			appDAO.removeApplicationEUFugitiveLeaks(ptioAppEU
					.getApplicationEuId());
			for (ApplicationEUFugitiveLeaks applicationEUFugitiveLeaks : ptioAppEU
					.getApplicationEUFugitiveLeaks()) {
				applicationEUFugitiveLeaks.setApplicationEuId(ptioAppEU
						.getApplicationEuId());
				appDAO.createApplicationEUFugitiveLeaks(applicationEUFugitiveLeaks);
			}

			appDAO.removeApplicationEUMaterialUsed(ptioAppEU
					.getApplicationEuId());
			for (ApplicationEUMaterialUsed materialUsed : ptioAppEU
					.getMaterialUsed()) {
				materialUsed.setApplicationEuId(ptioAppEU.getApplicationEuId());
				appDAO.addApplicationEUMaterialUsed(materialUsed);
			}
		} else if (appEU instanceof TVApplicationEU) {
			TVApplicationEU tvAppEU = (TVApplicationEU) appEU;

			// remove scenarios and associated data
			appDAO.removeTVAltScenarioPteReqsForEu(tvAppEU.getApplicationEuId());
			appDAO.removeTVApplicationEUEmissions(tvAppEU.getApplicationEuId());
			appDAO.removeTVEUOperatingScenarios(tvAppEU.getApplicationEuId());

			// add data for normal scenario
			appDAO.createTVEUOperatingScenario(tvAppEU
					.getNormalOperatingScenario());
			for (TVApplicationEUEmissions emissions : tvAppEU
					.getNormalOperatingScenario().getCapEmissions()) {
				appDAO.addTVApplicationEUEmissions(emissions);
			}
			for (TVApplicationEUEmissions emissions : tvAppEU
					.getNormalOperatingScenario().getHapEmissions()) {
				appDAO.addTVApplicationEUEmissions(emissions);
			}
			for (TVApplicationEUEmissions emissions : tvAppEU
					.getNormalOperatingScenario().getGhgEmissions()) {
				appDAO.addTVApplicationEUEmissions(emissions);
			}
			for (TVApplicationEUEmissions emissions : tvAppEU
					.getNormalOperatingScenario().getOthEmissions()) {
				appDAO.addTVApplicationEUEmissions(emissions);
			}

			// add data for alternate scenarios
			for (TVEUOperatingScenario scenario : tvAppEU
					.getAlternateOperatingScenarios()) {
				appDAO.createTVEUOperatingScenario(scenario);
				for (TVApplicationEUEmissions emissions : scenario
						.getCapEmissions()) {
					appDAO.addTVApplicationEUEmissions(emissions);
				}
				for (TVApplicationEUEmissions emissions : scenario
						.getHapEmissions()) {
					appDAO.addTVApplicationEUEmissions(emissions);
				}
				for (TVApplicationEUEmissions emissions : scenario
						.getGhgEmissions()) {
					appDAO.addTVApplicationEUEmissions(emissions);
				}
				for (TVApplicationEUEmissions emissions : scenario
						.getOthEmissions()) {
					appDAO.addTVApplicationEUEmissions(emissions);
				}
				for (TVAltScenarioPteReq req : scenario.getPteRequirements()) {
					appDAO.addTVAltScenarioPteReq(req);
				}
			}

			// add data for applicable requirements
			removeTVApplicableReqsForEU(tvAppEU.getApplicationEuId(), appDAO);
			for (TVApplicableReq applicableReq : tvAppEU
					.getApplicableRequirements()) {
				applicableReq.setStateOnly(false);
				applicableReq = appDAO.addTVApplicableReq(applicableReq);
				addTVApplicableReqComponents(applicableReq, appDAO);
			}
			for (TVApplicableReq applicableReq : tvAppEU
					.getStateOnlyRequirements()) {
				applicableReq.setStateOnly(true);
				applicableReq = appDAO.addTVApplicableReq(applicableReq);
				addTVApplicableReqComponents(applicableReq, appDAO);
			}

			// add Pollutant Limits
			appDAO.removeTVEUPollutantLimits(tvAppEU.getApplicationEuId());
			for (TVEUPollutantLimit pollLimit : tvAppEU.getPollutantLimits()) {
				appDAO.addTVEUPollutantLimit(tvAppEU.getApplicationEuId(),
						pollLimit);
			}

			// add Operational Restrictions
			appDAO.removeTVEUOperationalRestrictions(tvAppEU
					.getApplicationEuId());
			for (TVEUOperationalRestriction opRestriction : tvAppEU
					.getOperationalRestrictions()) {
				appDAO.addTVEUOperationalRestriction(
						tvAppEU.getApplicationEuId(), opRestriction);
			}
		}
	}

	private void removeTVFacilityWideRequirements(TVApplication app,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		appDAO.removeFacilityWideRequirements(app.getApplicationID());
	}

	private void removeTVApplicableReqsForApp(Integer appId, Transaction trans)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		for (TVApplicableReq req : appDAO.retrieveTVApplicableReqs(appId)) {
			int reqId = req.getTvApplicableReqId();

			appDAO.removeTvComplianceForAppReq(reqId);
			appDAO.removeTvComplianceObligationsForAppReq(reqId);
			appDAO.removeTvProposedExemptionsForAppReq(reqId);
			appDAO.removeTvProposedAltLimitsForAppReq(reqId);
			appDAO.removeTvProposedTestChangesForAppReq(reqId);
		}
		appDAO.removeTVApplicableReqs(appId);
	}

	private void removeTVApplicableReqsForEU(Integer appEuId,
			ApplicationDAO appDAO) throws DAOException {
		for (TVApplicableReq req : appDAO
				.retrieveTVApplicableReqsForEU(appEuId)) {
			int reqId = req.getTvApplicableReqId();

			appDAO.removeTvComplianceForAppReq(reqId);
			appDAO.removeTvComplianceObligationsForAppReq(reqId);
			appDAO.removeTvProposedExemptionsForAppReq(reqId);
			appDAO.removeTvProposedAltLimitsForAppReq(reqId);
			appDAO.removeTvProposedTestChangesForAppReq(reqId);
		}
		appDAO.removeTVApplicableReqsForEU(appEuId);
	}

	private void removeTVApplicableReqsForEUGroup(Integer groupId,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		for (TVApplicableReq req : appDAO
				.retrieveTVApplicableReqsForEUGroup(groupId)) {
			int reqId = req.getTvApplicableReqId();

			appDAO.removeTvComplianceForAppReq(reqId);
			appDAO.removeTvComplianceObligationsForAppReq(reqId);
			appDAO.removeTvProposedExemptionsForAppReq(reqId);
			appDAO.removeTvProposedAltLimitsForAppReq(reqId);
			appDAO.removeTvProposedTestChangesForAppReq(reqId);
		}
		appDAO.removeTVApplicableReqsForEUGroup(groupId);
	}

	private void addTVApplicableReqComponents(TVApplicableReq req,
			ApplicationDAO appDAO) throws DAOException {
		Integer tvApplicableReqId = req.getTvApplicableReqId();

		// Note: the ids for the TVCompliance, TVComplianceObligations, etc.
		// records
		// only need to be unique for the same applicable requirement object, so
		// we'll
		// just generate ids each time they're added to the database.
		int id = 1;
		for (TVCompliance compliance : req.getComplianceReqs()) {
			compliance.setTvApplicableReqId(tvApplicableReqId);
			compliance.setComplianceId(id++);
			appDAO.addTvCompliance(compliance);
		}

		id = 1;
		for (TVComplianceObligations obligations : req
				.getComplianceObligationsReqs()) {
			obligations.setTvApplicableReqId(tvApplicableReqId);
			obligations.setComplianceObligationsId(id++);
			appDAO.addTvComplianceObligations(obligations);
		}

		id = 1;
		for (TVProposedExemptions exemptions : req.getProposedExemptionsReqs()) {
			exemptions.setTvApplicableReqId(tvApplicableReqId);
			exemptions.setProposedExemptionsId(id++);
			appDAO.addTvProposedExemptions(exemptions);
		}

		id = 1;
		for (TVProposedAltLimits altLimits : req.getProposedAltLimitsReqs()) {
			altLimits.setTvApplicableReqId(tvApplicableReqId);
			altLimits.setProposedAltLimitsId(id++);
			appDAO.addTvProposedAltLimits(altLimits);
		}

		id = 1;
		for (TVProposedTestChanges testChanges : req
				.getProposedTestChangesReqs()) {
			testChanges.setTvApplicableReqId(tvApplicableReqId);
			testChanges.setProposedTestChangesId(id++);
			appDAO.addTvProposedTestChanges(testChanges);
		}
	}

	/**
	 * @param appEU
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyApplicationEU(ApplicationEU appEU) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			modifyApplicationEU(appEU, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return true;
	}

	/**
	 * @param appEU
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private boolean modifyApplicationEU(ApplicationEU appEU, Transaction trans)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		// Update base class
		appDAO.modifyApplicationEU(appEU);

		Class<? extends ApplicationEU> incomingAppEUClass = appEU.getClass();
		if (incomingAppEUClass.equals(PTIOApplicationEU.class)) {
			PTIOApplicationEU ptioAppEU = (PTIOApplicationEU) appEU;
			Integer ptioAppEuId = ptioAppEU.getApplicationEuId();

			// remove old subparts
			appDAO.removePTIOAppNSPSSubpartCds(ptioAppEuId);
			appDAO.removePTIOAppNESHAPSubpartCds(ptioAppEuId);
			appDAO.removePTIOAppMACTSubpartCds(ptioAppEuId);

			// add new subparts
			for (String nspsSubpartCd : ptioAppEU.getNspsSubpartCodes()) {
				appDAO.addPTIOAppNSPSSubpartCd(ptioAppEuId, nspsSubpartCd);
			}
			for (String neshapsSubpartCd : ptioAppEU.getNeshapSubpartCodes()) {
				appDAO.addPTIOAppNESHAPSubpartCd(ptioAppEuId, neshapsSubpartCd);
			}
			for (String mactSubpartCd : ptioAppEU.getMactSubpartCodes()) {
				appDAO.addPTIOAppMACTSubpartCd(ptioAppEuId, mactSubpartCd);
			}

			appDAO.modifyPTIOApplicationEU(ptioAppEU);
		} else if (incomingAppEUClass.equals(TVApplicationEU.class)) {
			TVApplicationEU tvAppEU = (TVApplicationEU) appEU;

			Integer tvAppEuId = tvAppEU.getApplicationEuId();

			// remove old subparts
			appDAO.removeTvEuAppNSPSSubpartCds(tvAppEuId);
			appDAO.removeTvEuAppNESHAPSubpartCds(tvAppEuId);
			appDAO.removeTvEuAppMACTSubpartCds(tvAppEuId);

			// add new subparts
			for (String nspsSubpartCd : tvAppEU.getNspsSubpartCodes()) {
				appDAO.addTvEuAppNSPSSubpartCd(tvAppEuId, nspsSubpartCd);
			}
			for (String neshapsSubpartCd : tvAppEU.getNeshapSubpartCodes()) {
				appDAO.addTvEuAppNESHAPSubpartCd(tvAppEuId, neshapsSubpartCd);
			}
			for (String mactSubpartCd : tvAppEU.getMactSubpartCodes()) {
				appDAO.addTvEuAppMACTSubpartCd(tvAppEuId, mactSubpartCd);
			}
			appDAO.modifyTVApplicationEU(tvAppEU);
		}

		ApplicationEUTypeDAO appEUTypeDAO = null;
		if (appEU instanceof PTIOApplicationEU) {
			appEUTypeDAO = getApplicationEUTypeDAO(appEU.getFpEU()
					.getEmissionUnitTypeCd(), trans, true);
		}

		updateApplicationEUComponents(appEU, appDAO, appEUTypeDAO);

		return true;
	}

	/**
	 * @param scenario
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public TVEUOperatingScenario createTVEUOperatingScenario(
			TVEUOperatingScenario scenario) throws DAOException {
		Transaction trans = null;
		TVEUOperatingScenario ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);

			int euId = scenario.getApplicationEuId();
			int scenarioId = appDAO.getNextScenarioId(euId);
			scenario.setTvEuOperatingScenarioId(scenarioId);
			ret = appDAO.createTVEUOperatingScenario(scenario);

			// initialize CAP emissions values
			for (TVApplicationEUEmissions emissions : getTVCAPEmissions(euId,
					scenarioId)) {
				appDAO.addTVApplicationEUEmissions(emissions);
				scenario.getCapEmissions().add(emissions);
			}
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public TVAltScenarioPteReq createTVAltScenarioPteReq(TVAltScenarioPteReq req)
			throws DAOException {
		Transaction trans = null;
		TVAltScenarioPteReq ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);

			int appEuId = req.getApplicationEuId();
			int scenarioId = req.getTvEuOperatingScenarioId();
			int reqId = appDAO.getNextAltScenarioPteReqId(appEuId, scenarioId);
			req.setTvAltScenarioPteReqId(reqId);
			ret = appDAO.addTVAltScenarioPteReq(req);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeTVEUOperatingScenario(TVEUOperatingScenario scenario)
			throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			appDAO.removeTVApplicationEUEmissionsForScenario(
					scenario.getApplicationEuId(),
					scenario.getTvEuOperatingScenarioId());
			appDAO.removeTVAltScenarioPteReqsForScenario(
					scenario.getApplicationEuId(),
					scenario.getTvEuOperatingScenarioId());
			appDAO.removeTVEUOperatingScenario(scenario);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * Delete the TV EU group identified by groupId and all of the data
	 * associated with it.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeTVEUGroup(Integer groupId) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			appDAO.removeTvEusFromGroup(groupId);
			removeTVApplicableReqsForEUGroup(groupId, trans);
			appDAO.removeTvEuGroup(groupId);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * Create a new application.
	 * 
	 * @param newApp
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Application createApplication(Application newApp)
			throws DAOException {
		Transaction trans = null;
		Application result = null;

		try {
			trans = TransactionFactory.createTransaction();
			result = createApplication(newApp, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return result;
	}

	/**
	 * Create a new application.
	 * 
	 * @param newApp
	 * @param trans
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Application createApplication(Application newApp, Transaction trans)
			throws DAOException {
		Application result = null;

		result = createNewApplication(newApp, trans);
		boolean updateEUs = false;
		updateApplicationComponents(newApp, updateEUs, trans);
		initApplicationEUs(newApp, trans);
		if (newApp instanceof TVApplication) {
			((TVApplication) newApp).setCapPteTotals(calculateTvCapPteTotals(
					(TVApplication) newApp, trans));
			((TVApplication) newApp).setHapPteTotals(calculateTvHapPteTotals(
					(TVApplication) newApp, trans));
			((TVApplication) newApp).setGhgPteTotals(calculateTvGhgPteTotals(
					(TVApplication) newApp, trans));
			((TVApplication) newApp).setOthPteTotals(calculateTvOthPteTotals(
					(TVApplication) newApp, trans));
		}

		return result;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Application createStagingApplication(Application newApp,
			Integer fpId, Transaction trans) throws DAOException {
		logger.debug("Creating the staged application (createStagingApplication)");
		FacilityDAO facDAO = facilityDAO(trans);
		Facility facility = facDAO.retrieveFacility(fpId);
		newApp.setFacility(facility);
		newApp = createNewApplication(newApp, trans);
		updateApplicationComponents(newApp, false, trans);
		DisplayUtil.clearQueuedMessages();
		initApplicationEUs(newApp, trans);
		return newApp;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Application createApplicationFromGateway(Application app,
			Facility facility, Transaction trans) throws RemoteException {
		
		// check to see if application has already been created on the internal
		// system
		Application result = null;

		try {
			result = retrieveApplicationSummary(app.getApplicationID(), trans);
		} catch (DAOException e) {
			// "Invalid application id" error is thrown when id does not exist
			// in the
			// database. This is to be expected. Log a message if a different
			// error occurs.
			if (e.getMessage() == null
					|| !e.getMessage().startsWith("Invalid application id:")) {
				logger.error(
						"Exception while checking to see if gateway application ("
								+ app.getApplicationID() + ") exists", e);
			}
		}
		
		// only create application if it doesn't exist.
		if(null == result) {
			this.facility = facility;
			submitApplication(app, CommonConst.GATEWAY_USER_ID,
					app.getReceivedDate(), true, null, trans);
		} else {
			logger.debug("Application " + app.getApplicationNumber() + " already exists in the internal system");
			this.application = result;
		}
		return this.application;
	}

	private Application submitApplicationFromGateway(Application app,
			Facility facility, Transaction trans) throws DAOException,
			RemoteException {
		
		Application result = null;

		// make sure facility data is the same as the data submitted with
		// the
		// "change facility inventory" task
		if (facility != null) {
			app.setFacility(facility);
			// update fpEU id's for app EUs
			List<ApplicationEU> appEUs = app.getEus();
			for (ApplicationEU appEU : appEUs) {
				// find matching EU in updated facility
				synchAppEuWithFacility(appEU, facility.getEmissionUnits());
			}
		}

		// contact needs to be created separately from other application
		// data
		Contact contact = app.getContact();
		Contact existingContact = null;
		if (contact != null) {
			InfrastructureDAO infraDAO = infrastructureDAO(trans);
			ContactDAO contactDAO = contactDAO(trans);
			// application may share a contact with the facility inventory.
			// if this is the case, there is no need to create the contact
			// again
			// (in fact, it would cause an exception!)
			if (contact.getContactId() != null) {
				existingContact = contactDAO.retrieveContact(contact
						.getContactId());
			}
			if (existingContact == null) {
				if (contact.getAddress() != null) {
					infraDAO.createAddress(contact.getAddress());
				}
				contactDAO.createContact(contact);
			}
		}
		// set received date to current time (this will also be the submit
		// date)
		app.setReceivedDate(new Timestamp(System.currentTimeMillis()));

		// Need to create this app in the DB - use existing application
		// number
		// and application directory
		result = createApplicationDBInstance(app, trans);
		copyGatewayEus(result, app, CommonConst.GATEWAY_USER_ID, trans);
		if (result instanceof TVApplication) {
			for (TVEUGroup euGroup : ((TVApplication) result).getEuGroups()) {
				createTvEuGroup(euGroup, trans);
			}
		}
		copyGatewayApplicationDocuments(app, result, trans);
		updateApplicationComponents(result, true, trans);

		// retrieve the application to ensure all "last_modified" flags are
		// correct

		result = retrieveApplicationWithAllEUs(result.getApplicationID(), trans);
		
		return result;
	}
	
/**
	 * This method is to be used by methods that need to retrieve application
	 * data residing within a transaction.
	 * 
	 * @param applicationId
	 * @param trans
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
/*
	@Transactional(noRollbackFor = NotFoundException.class)
	public Application retrieveApplication(Integer applicationId,
			Transaction trans) throws DAOException {
		logger.debug("Retrieving application...");
		ApplicationDAO appDAO = applicationDAO(trans);

		Application application = appDAO.retrieveApplication(applicationId);

		if (application == null)
			throw new NotFoundException("Invalid application id:"
					+ applicationId);

		boolean staging = false;
		if (!isInternalApp() && trans != null) {
			staging = true;
		}

		populateApplicationComponents(application, trans, false, staging);

		logger.debug("Done retrieving application...");
		return application;
	}
*/
/*
	public Application retrieveApplication(Integer applicationId,
			Transaction trans, boolean staging) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		Application application = appDAO.retrieveApplication(applicationId);

		if (application == null)
			throw new DAOException("Invalid application id:" + applicationId);

		populateApplicationComponents(application, trans, false, staging);

		return application;
	}
*/
	private void copyGatewayApplicationDocuments(Application fromApp,
			Application toApp, Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		DocumentDAO docDAO = documentDAO(trans);

		if (fromApp instanceof PBRNotification) {
			for (PBRNotificationDocument doc : ((PBRNotification) fromApp)
					.getPbrDocuments()) {
				PBRNotificationDocument docCopy = new PBRNotificationDocument(
						doc);
				docCopy.setApplicationId(toApp.getApplicationID());
				docCopy.setLastModifiedBy(CommonConst.GATEWAY_USER_ID);
				// ensure timestamps are set
				if (docCopy.getLastModifiedTS() == null) {
					logger.warn("fixing LastModifiedTS for document: "
							+ docCopy.getDocumentID());
					docCopy.setLastModifiedTS(new Timestamp(System
							.currentTimeMillis()));
				}
				if (docCopy.getUploadDate() == null) {
					logger.warn("fixing UploadDate for document: "
							+ docCopy.getDocumentID());
					docCopy.setUploadDate(new Timestamp(System
							.currentTimeMillis()));
				}
				docDAO.createDocument(docCopy);
				appDAO.createPBRNotificationDocument(docCopy);
			}
		}
		if (toApp instanceof RPCRequest) {
			for (RPCRequestDocument doc : ((RPCRequest) fromApp)
					.getRpcDocuments()) {
				RPCRequestDocument docCopy = new RPCRequestDocument(doc);
				docCopy.setApplicationId(toApp.getApplicationID());
				docCopy.setLastModifiedBy(CommonConst.GATEWAY_USER_ID);
				// ensure timestamps are set
				if (docCopy.getLastModifiedTS() == null) {
					logger.warn("fixing LastModifiedTS for document: "
							+ docCopy.getDocumentID());
					docCopy.setLastModifiedTS(new Timestamp(System
							.currentTimeMillis()));
				}
				if (docCopy.getUploadDate() == null) {
					logger.warn("fixing UploadDate for document: "
							+ docCopy.getDocumentID());
					docCopy.setUploadDate(new Timestamp(System
							.currentTimeMillis()));
				}
				docDAO.createDocument(docCopy);
				appDAO.createRPCRequestDocument(docCopy);
			}
		} else {
			for (ApplicationDocumentRef docRef : fromApp.getDocuments()) {
				// logger.error("Debug #2966 Application document id = " +
				// docRef.getApplicationDocId()
				// + ", public document id = " + docRef.getDocumentId()
				// + ", document type cd " +
				// docRef.getApplicationDocumentTypeCD());
				logger.debug("Application document id = "
						+ docRef.getApplicationDocId()
						+ ", public document id = " + docRef.getDocumentId()
						+ ", document type cd "
						+ docRef.getApplicationDocumentTypeCD());
				ApplicationDocumentRef docRefCopy = new ApplicationDocumentRef(
						docRef);
				createGatewayApplicationDocumentRef(docRefCopy, trans);
			}
		}
	}

	private void copyGatewayEus(Application fromApp, Application toApp,
			Integer userId, Transaction trans) throws DAOException {
		for (ApplicationEU appEU : fromApp.getEus()) {
			ApplicationEU appEUCopy = createBaseApplicationEU(appEU, trans);
			// need to create instances of Document objects in internal DB
			for (ApplicationDocumentRef docRef : appEU.getEuDocuments()) {
				// logger.error("Debug #2966 Application document id = " +
				// docRef.getApplicationDocId()
				// + ", public document id = " + docRef.getDocumentId()
				// + ", document type cd " +
				// docRef.getApplicationDocumentTypeCD());
				ApplicationDocumentRef docRefCopy = new ApplicationDocumentRef(
						docRef);
				createGatewayApplicationDocumentRef(docRefCopy, trans);
			}
			copyApplicationEUData(appEU, appEUCopy, false, trans, true);
		}
	}

	private void createGatewayApplicationDocumentRef(
			ApplicationDocumentRef docRef, Transaction trans)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		DocumentDAO docDAO = documentDAO(trans);
		logger.debug("createGatewayApplicationDocumentRef");
		if (docRef.getPublicDoc() != null
				&& docRef.getPublicDoc().getDocumentID() != null) {
			logger.debug("public doc id = "
					+ docRef.getPublicDoc().getDocumentID());
			// two different doc ref object may point to the same physical
			// document,
			// so don't create the document record twice if this is the case
			Document matchDoc = docDAO.retrieveDocument(docRef.getPublicDoc()
					.getDocumentID());
			if (matchDoc == null) {
				docRef.getPublicDoc().setLastModifiedBy(
						CommonConst.GATEWAY_USER_ID);
				// ensure timestamp is set
				if (docRef.getPublicDoc().getLastModifiedTS() == null) {
					logger.warn("fixing LastModifiedTS for document: "
							+ docRef.getPublicDoc().getDocumentID());
					docRef.getPublicDoc().setLastModifiedTS(
							new Timestamp(System.currentTimeMillis()));
				}
				if (docRef.getPublicDoc().getUploadDate() == null) {
					logger.warn("fixing UploadDate for document: "
							+ docRef.getPublicDoc().getDocumentID());
					docRef.getPublicDoc().setUploadDate(
							new Timestamp(System.currentTimeMillis()));
				}
				// ensure that temp flag is set to false - this has been set to
				// true somehow in a few apps
				docRef.getPublicDoc().setTemporary(false);

				Document publicDoc = docDAO.createDocument(docRef
						.getPublicDoc());
				docRef.setDocumentId(publicDoc.getDocumentID());
				logger.debug("created new public doc with id: "
						+ publicDoc.getDocumentID());
			} else {
				logger.debug("unmark temp for doc with id: "
						+ docRef.getPublicDoc().getDocumentID());
				docDAO.unMarkTempDocument(docRef.getPublicDoc().getDocumentID());
			}
		}
		if (docRef.getTradeSecretDoc() != null
				&& docRef.getTradeSecretDoc().getDocumentID() != null) {
			logger.debug("trade secret doc id = "
					+ docRef.getTradeSecretDoc().getDocumentID());
			// two different doc ref object may point to the same physical
			// document,
			// so don't create the document record twice if this is the case
			Document matchDoc = docDAO.retrieveDocument(docRef
					.getTradeSecretDoc().getDocumentID());
			if (matchDoc == null) {
				docRef.getTradeSecretDoc().setLastModifiedBy(
						CommonConst.GATEWAY_USER_ID);
				// ensure timestamp is set
				if (docRef.getTradeSecretDoc().getLastModifiedTS() == null) {
					logger.warn("fixing LastModifiedTS for document: "
							+ docRef.getTradeSecretDoc().getDocumentID());
					docRef.getTradeSecretDoc().setLastModifiedTS(
							new Timestamp(System.currentTimeMillis()));
				}
				if (docRef.getTradeSecretDoc().getUploadDate() == null) {
					logger.warn("fixing UploadDate for document: "
							+ docRef.getTradeSecretDoc().getDocumentID());
					docRef.getTradeSecretDoc().setUploadDate(
							new Timestamp(System.currentTimeMillis()));
				}
				// ensure that temp flag is set to false - this has been set to
				// true somehow in a few apps
				docRef.getTradeSecretDoc().setTemporary(false);

				Document tsDoc = docDAO.createDocument(docRef
						.getTradeSecretDoc());
				docRef.setTradeSecretDocId(tsDoc.getDocumentID());
				logger.debug("created new trade secret doc with id: "
						+ tsDoc.getDocumentID());
			} else {
				logger.debug("unmark temp for doc with id: "
						+ docRef.getTradeSecretDoc().getDocumentID());
				docDAO.unMarkTempDocument(docRef.getTradeSecretDoc()
						.getDocumentID());
			}
		}
		logger.debug("create application document with id: "
				+ docRef.getApplicationDocId() + ", public doc id="
				+ docRef.getDocumentId() + ", TS doc id="
				+ docRef.getTradeSecretDocId());
		appDAO.createApplicationDocument(docRef);
	}

	/**
	 * 
	 * @param newApp
	 * @param trans
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Application createNewApplication(Application newApp)
			throws DAOException {
		Application result = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			result = createNewApplication(newApp, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return result;
	}

	/**
	 * 
	 * @param newApp
	 * @param trans
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Application createNewApplication(Application newApp,
			Transaction trans) throws DAOException {
		Application result = null;

		// generate a unique application number for this application
		newApp.setApplicationNumber(generateApplicationNumber(newApp, trans));

		// First create application, so we have an applicationId.
		Class<? extends Application> incomingAppClass = newApp.getClass();
		if (incomingAppClass.equals(PTIOApplication.class)) {
			newApp.setApplicationTypeCD("PTIO");
		} else if (incomingAppClass.equals(PBRNotification.class)) {
			newApp.setApplicationTypeCD("PBR");
		} else if (incomingAppClass.equals(TVApplication.class)) {
			newApp.setApplicationTypeCD("TV");
		} else if (incomingAppClass.equals(RPCRequest.class)) {
			newApp.setApplicationTypeCD("RPC");
		} else if (incomingAppClass.equals(RPERequest.class)) {
			newApp.setApplicationTypeCD("RPE");
		} else if (incomingAppClass.equals(TIVApplication.class)) {
			newApp.setApplicationTypeCD("TIV");
		} else if (incomingAppClass.equals(RelocateRequest.class)) {
			// the type cd should already be part of the newApp object
		}

		result = createApplicationDBInstance(newApp, trans);

		// create directory for application documents
		try {
			String path = getApplicationDir(result);
			DocumentUtil.mkDir(path);
		} catch (IOException e) {
			logger.error(
					"Exception creating file store directory for facility "
							+ newApp.getFacilityId(), e);
			throw new DAOException("Exception creating file store directory", e);
		}
		return result;
	}

	private Application createApplicationDBInstance(Application app,
			Transaction trans) throws DAOException {
		Application result = null;
		ApplicationDAO appDAO = applicationDAO(trans);
		result = appDAO.createApplication(app);

		// Do type specfic creates...
		if (result instanceof PTIOApplication) {
			result = appDAO.createPTIOApplication((PTIOApplication) result);
		} else if (result instanceof PBRNotification) {
			result = appDAO.createPBRNotification((PBRNotification) result);
		} else if (result instanceof TVApplication) {
			result = appDAO.createTVApplication((TVApplication) result);
		} else if (result instanceof RPCRequest) {
			result = appDAO.createRPCRequest((RPCRequest) result);
		} else if (result instanceof RPERequest) {
			result = appDAO.createRPERequest((RPERequest) result);
		} else if (result instanceof RPRRequest) {
			result = appDAO.createRPRRequest((RPRRequest) result);
		} else if (result instanceof TIVApplication) {
			result = appDAO.createTIVApplication((TIVApplication) result);
		}
		return result;
	}

	private String getApplicationDir(Application app) {
		return "/Facilities/" + app.getFacilityId() + "/Applications/"
				+ app.getApplicationID();
	}

	private void initTVApplicationComponents(TVApplication tvApp,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		// update ids for applicable requirements
		for (TVApplicableReq req : tvApp.getApplicableRequirements()) {
			req.setApplicationId(tvApp.getApplicationID());
			req.setTvApplicableReqId(appDAO
					.generateApplicableRequirementSeqNo());
		}
		for (TVApplicableReq req : tvApp.getStateOnlyRequirements()) {
			req.setApplicationId(tvApp.getApplicationID());
			req.setTvApplicableReqId(appDAO
					.generateApplicableRequirementSeqNo());
		}
		// update ids for EU groups
		for (TVEUGroup euGroup : tvApp.getEuGroups()) {
			euGroup.setApplicationId(tvApp.getApplicationID());
			euGroup.setTvEuGroupId(null);
			appDAO.createTvEuGroup(euGroup);
			for (TVApplicableReq req : euGroup.getApplicableRequirements()) {
				req.setApplicationId(tvApp.getApplicationID());
				req.setTvEuGroupId(euGroup.getTvEuGroupId());
				req.setTvApplicableReqId(appDAO
						.generateApplicableRequirementSeqNo());
			}
			for (TVApplicableReq req : euGroup.getStateOnlyRequirements()) {
				req.setApplicationId(tvApp.getApplicationID());
				req.setTvEuGroupId(euGroup.getTvEuGroupId());
				req.setTvApplicableReqId(appDAO
						.generateApplicableRequirementSeqNo());
			}
		}

		for (FacilityWideRequirement facWideReq : tvApp
				.getFacilityWideRequirements()) {
			facWideReq.setApplicationId(tvApp.getApplicationID());
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void deleteApplicationDir(Application app) throws IOException {
		DocumentUtil.rmDir(getApplicationDir(app));
	}

	private void updateApplicationComponents(Application application, boolean updateEUs,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		Integer appId = application.getApplicationID();

		// perform application type-specific updates
		if (application instanceof PTIOApplication) {
			// remove old data
			appDAO.removePTIOApplicationPurposeCds(appId);

			PTIOApplication ptioApp = (PTIOApplication) application;
			updatePTIOApplicationPurposeCds(ptioApp);
			
			try {
				if (!ptioApp.isLegacyStatePTOApp() && !ptioApp.isKnownIncompleteNSRApp()) {

					verifyNSRAppRequiredAttachments(trans, ptioApp);
					if (updateEUs) {
						for (ApplicationEU appEU : ptioApp.getEus()) {
							verifyNSRAppEURequiredAttachments(trans, ptioApp,
								(PTIOApplicationEU) appEU);
						}
					}
				}
				} catch (Exception e) {
					logger.error(e);
					DisplayUtil.displayError(e.getMessage());
				}

			for (String purposeCd : ptioApp.getApplicationPurposeCDs()) {
				appDAO.createPTIOApplicationPurpose(appId, purposeCd);
			}
		} else if (application instanceof TVApplication) {
			TVApplication tvApp = (TVApplication) application;
			
			try {
				if (!tvApp.isLegacyStateTVApp()) {
					verifyTVAppRequiredAttachments(trans, tvApp);
					if (updateEUs) {
						for (ApplicationEU appEU : tvApp.getEus()) {
							verifyTVAppEURequiredAttachments(trans, tvApp,
									(TVApplicationEU) appEU);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
				DisplayUtil.displayError(e.getMessage());
			}

			// update EU Groups
			for (TVEUGroup euGroup : tvApp.getEuGroups()) {
				modifyTvEuGroup(euGroup, trans);
			}

			// update reason codes
			appDAO.removeTVApplicationReasonCds(appId);
			String purposeCd = tvApp.getTvApplicationPurposeCd();
			String reasonCd = tvApp.getPermitReasonCd();
			if (purposeCd != null
					&& purposeCd
					.equals(TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING)
					&& !Utility.isNullOrEmpty(reasonCd)) {
				appDAO.createTVApplicationReason(appId, reasonCd);
			}
			// update applicable requirements
			removeTVApplicableReqsForApp(appId, trans);
			for (TVApplicableReq applicableReq : tvApp
					.getApplicableRequirements()) {
				applicableReq.setApplicationId(appId);
				applicableReq = appDAO.addTVApplicableReq(applicableReq);
				addTVApplicableReqComponents(applicableReq, appDAO);
			}
			for (TVApplicableReq applicableReq : tvApp
					.getStateOnlyRequirements()) {
				applicableReq.setApplicationId(appId);
				applicableReq = appDAO.addTVApplicableReq(applicableReq);
				addTVApplicableReqComponents(applicableReq, appDAO);
			}

			// update Facility-wide PTE data
			appDAO.removeTVPteAdjustmentForApplication(appId);
			for (TVPteAdjustment pte : tvApp.getCapPteTotals()) {
				pte.setApplicationId(appId);
				appDAO.addTVPteAdjustment(pte);
			}

			for (TVPteAdjustment pte : tvApp.getHapPteTotals()) {
				if (PollutantDef.HAPS_TOT_CD.equals(pte.getPollutantCd())) {
					// don't save totals row - its for display only
					continue;
				}

				if (pte.getPteEUTotal() != null
						&& pte.getPteAdjusted() != null
						&& pte.getPteEUTotal().length() != 0
						&& pte.getPteAdjusted().length() != 0
						&& getEmissionValueAsBigDecimal(pte.getPollutantCd(),
								ApplicationEUEmissionTableDef.HAP_TABLE_CD,
								pte.getPteEUTotal()).compareTo(
								new BigDecimal("0")) == 0
						&& getEmissionValueAsBigDecimal(pte.getPollutantCd(),
								ApplicationEUEmissionTableDef.HAP_TABLE_CD,
								pte.getPteAdjusted()).compareTo(
								new BigDecimal("0")) == 0) {
					continue; // do not write back to database if HAP and both
								// EU total and facility totals are zero.
				}
				pte.setApplicationId(appId);
				appDAO.addTVPteAdjustment(pte);
			}
			for (TVPteAdjustment pte : tvApp.getGhgPteTotals()) {
				if (PollutantDef.GHG_TOT_CD.equals(pte.getPollutantCd())) {
					// don't save totals row - its for display only
					continue;
				}
				if (pte.getPteEUTotal() != null
						&& pte.getPteAdjusted() != null
						&& pte.getPteEUTotal().length() != 0
						&& pte.getPteAdjusted().length() != 0
						&& getEmissionValueAsBigDecimal(pte.getPollutantCd(),
								ApplicationEUEmissionTableDef.GHG_TABLE_CD,
								pte.getPteEUTotal()).compareTo(
								new BigDecimal("0")) == 0
						&& getEmissionValueAsBigDecimal(pte.getPollutantCd(),
								ApplicationEUEmissionTableDef.GHG_TABLE_CD,
								pte.getPteAdjusted()).compareTo(
								new BigDecimal("0")) == 0) {
					continue; // do not write back to database if HAP and both
								// EU total and facility totals are zero.
				}
				pte.setApplicationId(appId);
				appDAO.addTVPteAdjustment(pte);
			}

			for (TVPteAdjustment pte : tvApp.getOthPteTotals()) {
				if (pte.getPteEUTotal() != null
						&& pte.getPteAdjusted() != null
						&& pte.getPteEUTotal().length() != 0
						&& pte.getPteAdjusted().length() != 0
						&& getEmissionValueAsBigDecimal(pte.getPollutantCd(),
								ApplicationEUEmissionTableDef.OTH_TABLE_CD,
								pte.getPteEUTotal()).compareTo(
								new BigDecimal("0")) == 0
						&& getEmissionValueAsBigDecimal(pte.getPollutantCd(),
								ApplicationEUEmissionTableDef.OTH_TABLE_CD,
								pte.getPteAdjusted()).compareTo(
								new BigDecimal("0")) == 0) {
					continue; // do not write back to database if HAP and both
								// EU total and facility totals are zero.
				}
				pte.setApplicationId(appId);
				appDAO.addTVPteAdjustment(pte);
			}

			// add Facility Wide Requirements
			updateTVApplicationSubparts(appDAO, tvApp);

			removeTVFacilityWideRequirements(tvApp, trans);
			for (FacilityWideRequirement facWideReq : tvApp
					.getFacilityWideRequirements()) {
				appDAO.createFacilityWideRequirement(facWideReq);
			}
		}
	}

	private void initApplicationEUs(Application application, Transaction trans)
			throws DAOException {
		if (application instanceof RPERequest
				|| application instanceof RPCRequest
				|| application instanceof RPRRequest) {
			createPermitModRequestEUs(application, trans);
		} else {
			// initialize app with EUs from facility
			addEusToApplication(application, application.getFacility()
					.getEmissionUnits(), trans);
		}
	}

	private void updateApplicationEUs(Application application, Transaction trans)
			throws DAOException {
		if (application instanceof RPCRequest
				|| application instanceof RPERequest
				|| application instanceof RPRRequest) {
			modifyPermitModRequestEUs(application, trans);
		} else {
			// need list of EU NSR reasons
			List<String> nsrEUReasons = new ArrayList<String>();

			// create or modify the EU's for this application
			for (ApplicationEU appEU : application.getEus()) {
				if (appEU.getApplicationEuId() == null) { // new EU
					appEU.setApplicationId(application.getApplicationID());
					createApplicationEU(appEU, trans);
				} else {
					modifyApplicationEU(appEU, trans);
				}

				if (appEU instanceof PTIOApplicationEU && !appEU.isExcluded()) {
					// add unique NSR reasons
					PTIOApplicationEU ptioAppEu = (PTIOApplicationEU) appEU;
					if (ptioAppEu.getPtioEUPurposeCD() != null
							&& !nsrEUReasons.contains(ptioAppEu
									.getPtioEUPurposeCD())) {
						nsrEUReasons.add(ptioAppEu.getPtioEUPurposeCD());
					}
				}
			}

			if (application instanceof PTIOApplication) {
				// save EU NSR reasons to application summary
				ApplicationDAO appDAO = applicationDAO(trans);
				PTIOApplication ptioApp = (PTIOApplication) application;
				updatePTIOApplicationPurposeCds(ptioApp);
				Integer appId = ptioApp.getApplicationID();

				appDAO.removePTIOApplicationPurposeCds(appId);
				for (String reasonCd : nsrEUReasons) {
					appDAO.createPTIOApplicationPurpose(appId, reasonCd);
				}
			}
		}
	}

	private void addEusToApplication(Application app, List<EmissionUnit> fpEus,
			Transaction trans) throws DAOException {
		// don't exclude EUs for legacy State applications
		boolean bypassEUExclusion = app instanceof PTIOApplication
				&& app.isLegacy();
		for (EmissionUnit fpEu : fpEus) {
			if (!bypassEUExclusion
					&& (fpEu.getEuShutdownDate() != null
							|| EuOperatingStatusDef.IV.equals(fpEu
									.getOperatingStatusCd())
							|| EuOperatingStatusDef.SD.equals(fpEu
									.getOperatingStatusCd())
							|| TVClassification.TRIVIAL.equals(fpEu
									.getTvClassCd()) || TVClassification.INSIG_NO_APP_REQS
								.equals(fpEu.getTvClassCd()))) {
				// don't process shutdown EUs, invalid EUs or Trivial EUs (for
				// Title V app only)
				continue;
			}
			ApplicationEU appEu = null;
			if (app.getClass().equals(TVApplication.class)) {
				appEu = new TVApplicationEU();
				appEu.setExcluded(false); // EU included in app by default
			} else if (app.getClass().equals(PTIOApplication.class)) {
				appEu = new PTIOApplicationEU();
				appEu.setExcluded(true); // EU excluded from app by default
			} else {
				appEu = new ApplicationEU();
				appEu.setExcluded(true); // EU excluded from app by default
			}
			appEu.setFpEU(fpEu);
			appEu.setApplicationId(app.getApplicationID());
			appEu = createApplicationEU(appEu, trans);
			app.addEu(appEu);
		}
	}

	@Override
	public void updateNSRPurposes(Application app) throws DAOException {
		if (app instanceof PTIOApplication) {
			PTIOApplication ptioApp = (PTIOApplication) app;
			updatePTIOApplicationPurposeCds(ptioApp);
			Transaction trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			Integer appId = ptioApp.getApplicationID();
			appDAO.removePTIOApplicationPurposeCds(appId);
			for (String reasonCd : ptioApp.getApplicationPurposeCDs()) {
				appDAO.createPTIOApplicationPurpose(appId, reasonCd);
			}
			trans.complete();
		}
	}

	private void updatePTIOApplicationPurposeCds(PTIOApplication app) throws DAOException {
		HashSet<String> updatedPurposeCds = new HashSet<String>();

		StringBuilder otherPurpose = new StringBuilder();

		app.getApplicationPurposeCDs().clear();
		
		// Retrieve purpose codes from database.
		Transaction trans = TransactionFactory.createTransaction();
		ApplicationDAO appDAO = applicationDAO(trans);
		String[] purposeCds = appDAO.retrieveUniquePTIOPurposeCds(app.getApplicationID());

		for (String purposeCd : purposeCds) {
			
			if (purposeCd != null) {
				if (purposeCd
						.equals(PTIOApplicationEUPurposeDef.CONSTRUCTION)) {
					updatedPurposeCds
							.add(PTIOApplicationPurposeDef.CONSTRUCTION);
				} else if (purposeCd
						.equals(PTIOApplicationEUPurposeDef.SYNTHETIC_MINOR)) {
					updatedPurposeCds
							.add(PTIOApplicationPurposeDef.SYNTHETIC_MINOR);
				} else if (purposeCd
						.equals(PTIOApplicationEUPurposeDef.TEMPORARY_PERMIT)) {
					updatedPurposeCds
							.add(PTIOApplicationPurposeDef.TEMPORARY_PERMIT);
				} else if (purposeCd
						.equals(PTIOApplicationEUPurposeDef.MODIFICATION)) {
					updatedPurposeCds
							.add(PTIOApplicationPurposeDef.MODIFICATION);
				} else if (purposeCd
						.equals(PTIOApplicationEUPurposeDef.RECONSTRUCTION)) {
					updatedPurposeCds
							.add(PTIOApplicationPurposeDef.RECONSTRUCTION);
				} else if (purposeCd
						.equals(PTIOApplicationEUPurposeDef.OTHER)) {
					updatedPurposeCds.add(PTIOApplicationPurposeDef.OTHER);
				}
			}

		}
		app.getApplicationPurposeCDs().addAll(updatedPurposeCds);
		
		// Retrieve and set the descriptions for EUs where purpose of application is 'Other'.
		String[] otherPurposeDesc = appDAO.retrieveModificationDescList(app.getApplicationID());
		for (String desc : otherPurposeDesc) {
			// make sure length of description does not exceed maximum
			// length of field
			if (desc != null) {
				if (desc.length() + otherPurpose.length() < 1000) {
					otherPurpose.append(desc + ";");
				} else if (otherPurpose.length() < 997) {
					int charsLeft = 1000 - otherPurpose.length() - 3;
					otherPurpose.append(desc.substring(0, charsLeft)
							+ "...");
				}
			}
		}
		
		if (otherPurpose.length() > 0) {
			otherPurpose.deleteCharAt(otherPurpose.length() - 1);
			app.setOtherPurposeDesc(otherPurpose.toString());
		}
	}

	/**
	 * Set the "validated" flag for <code>app</code> to <code>validated</code>.
	 * This method exists to avoid a full update of all application attributes
	 * when all that is needed is to set the validated flag.
	 * 
	 * @param app
	 *            the application
	 * @param validated
	 *            <code>true</code> or <code>false</code> to indicate whether
	 *            the application is validated.
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean setValidatedFlag(Application app, boolean validated)
			throws DAOException {
		Transaction trans = null;
		boolean ret = true;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			app.setValidated(validated);
			appDAO.setApplicationValidatedFlag(app.getApplicationID(),
					validated);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param app
	 * @param userId
	 * @return boolean
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyPbrNoApplicableApplication(Application app, int userId)
			throws DAOException {
		Transaction trans = null;
		PermitService pBO;
		try {
			pBO = ServiceFactory.getInstance().getPermitService();
			trans = TransactionFactory.createTransaction();
			modifyApplication(app, trans, true);
			Permit permit = pBO.retrievePermit(app.getApplicationNumber());
			/*
			 * Remove PBR references in permit applications if (permit != null
			 * && PermitTypeDef.PBR.equals(permit.getPermitType())) { for
			 * (PermitEU eu : permit.getEus()) {
			 * eu.setPermitStatusCd(PermitStatusDef.TERMINATED);
			 * pBO.modifyEU(eu, userId); } }
			 */
			trans.complete();
		} catch (ServiceFactoryException sfe) {
			trans.cancel();
			logger.error(sfe.getMessage(), sfe);
			throw new DAOException(sfe.getMessage());
		} catch (RemoteException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return true;
	}

	/**
	 * @param app
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyApplication(Application app, boolean modifyEUs) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			modifyApplication(app, trans, modifyEUs);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return true;
	}

	/**
	 * @param app
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * 
	 */
	public boolean modifyApplication(Application app, Transaction trans, boolean modifyEUs)
			throws DAOException {
		logger.debug("Modifying application...");
		
		ApplicationDAO appDAO = applicationDAO(trans);

		/*
		 * General Permit not valid for WY if (app instanceof PTIOApplication) {
		 * // update general permit string for PTIO applications
		 * updateGeneralPermitInfo((PTIOApplication) app); }
		 */

		// First modify application, so we have an applicationId.
		if (appDAO.modifyApplication(app)) {
			Class<? extends Application> incomingAppClass = app.getClass();

			// Do type specfic modifications...
			if (incomingAppClass.equals(PTIOApplication.class)) {
				// make radio button values consistent
				if (!((PTIOApplication) app).isQualifyExpress()) {
					((PTIOApplication) app).setRequestExpress(false);
				}
				appDAO.modifyPTIOApplication((PTIOApplication) app);
			} else if (incomingAppClass.equals(PBRNotification.class)) {
				appDAO.modifyPBRNotification((PBRNotification) app);
			} else if (incomingAppClass.equals(TVApplication.class)) {
				appDAO.modifyTVApplication((TVApplication) app);
			} else if (incomingAppClass.equals(RPCRequest.class)) {
				appDAO.modifyRPCRequest((RPCRequest) app);
				// EUs will be cleared if permit if changes.
				// If this is the case, we need to delete old EUs
				if (app.getEus().size() == 0) {
					appDAO.removeApplicationEUs(app.getApplicationID());
				}
			} else if (incomingAppClass.equals(RPERequest.class)) {
				appDAO.modifyRPERequest((RPERequest) app);
			} else if (incomingAppClass.equals(RPRRequest.class)) {
				appDAO.modifyRPRRequest((RPRRequest) app);
			} else if (incomingAppClass.equals(TIVApplication.class)) {
				appDAO.modifyTIVApplication((TIVApplication) app);
			}

			updateApplicationComponents(app, modifyEUs, trans);
			if (modifyEUs) {
				updateApplicationEUs(app, trans);
			}
		}

		logger.debug("Done modifying application...");
		return true;
	}

	/**
	 * 
	 * @param app
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public boolean isTradeSecret(Application app, boolean readOnly, boolean includeAllAttachments)
			throws DAOException {
		// set trade secret to true if there is at least one
		// trade secret attachment
		boolean isTradeSecret = false;
		ApplicationDAO appDAO = applicationDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		ApplicationDocumentRef[] appDocuments = null;
		if (ApplicationTypeDef.PTIO_APPLICATION.equals(app.getApplicationTypeCD())) {
			appDocuments = appDAO.retrieveNSRApplicationDocuments(app.getApplicationID());
		} else if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(app.getApplicationTypeCD())) {
			appDocuments = appDAO.retrieveTVApplicationDocuments(app.getApplicationID());
		} else {
			appDocuments = appDAO.retrieveApplicationDocuments(app.getApplicationID());
		}
		
		for (ApplicationDocumentRef docRef : appDocuments) {
			loadDocuments(docRef, readOnly);
			if (docRef.getTradeSecretDocId() != null && (includeAllAttachments || (app.getSubmittedDate() != null && docRef.getLastModifiedTS() != null
					&& docRef.getLastModifiedTS().before(app.getSubmittedDate())) || docRef.isTradeSecretAllowed())) {
				isTradeSecret = true;
				break;
			}
		}
		
		if (!isTradeSecret) {
			euloop: for (ApplicationEU appEU : app.getIncludedEus()) {
				ApplicationDocumentRef[] appEUDocuments = null;
				if (ApplicationTypeDef.PTIO_APPLICATION.equals(app.getApplicationTypeCD())) {
					appEUDocuments = appDAO.retrieveNSRApplicationEUDocuments(appEU.getApplicationEuId());
				} else if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(app.getApplicationTypeCD())) {
					appEUDocuments = appDAO.retrieveTVApplicationEUDocuments(appEU.getApplicationEuId());
				} else {
					appEUDocuments = appDAO.retrieveApplicationEUDocuments(appEU.getApplicationEuId());
				}
				
				for (ApplicationDocumentRef docRef : appEUDocuments) {
					loadDocuments(docRef, readOnly);
					if (docRef.getTradeSecretDocId() != null && (includeAllAttachments || (app.getSubmittedDate() != null && docRef.getLastModifiedTS() != null
							&& docRef.getLastModifiedTS().before(app.getSubmittedDate())) || docRef.isTradeSecretAllowed())) {
						isTradeSecret = true;
						break euloop;
					}
				}
			}
		}
		return isTradeSecret;
	}

	/*
	 * General Permit not valid for WY private void
	 * updateGeneralPermitInfo(PTIOApplication app) { StringBuilder
	 * generalPermit = new StringBuilder(); for (ApplicationEU appEU :
	 * app.getIncludedEus()) { if (((PTIOApplicationEU) appEU).isGeneralPermit()
	 * && ((PTIOApplicationEU) appEU).getGeneralPermitTypeCd() != null) {
	 * generalPermit.append(PTIOGeneralPermitTypeDef .getData() .getItems()
	 * .getItemDesc( ((PTIOApplicationEU) appEU) .getGeneralPermitTypeCd()) +
	 * ", "); } } if (generalPermit.length() > 0) { int subStrLength =
	 * generalPermit.length() - 2; if (subStrLength > 1024) {
	 * app.setGeneralPermit(generalPermit.substring(0, 1020) + "..."); } else {
	 * app.setGeneralPermit(generalPermit.substring(0, subStrLength)); } } else
	 * { app.setGeneralPermit(""); } }
	 */

	/**
	 * Create a copy of <code>origPbr</code> that will contain all the data
	 * specified in <code>origPbr</code>, but will exist as a separate
	 * application with its own application number. The copied application will
	 * be stored in the database, then returned by this method.
	 * 
	 * @param origPbr
	 *            PBRNotification to be copied.
	 * @return copied PBRNotification object.
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PBRNotification createPBRCopy(PBRNotification origPbr)
			throws DAOException {
		PBRNotification pbrCopy = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			pbrCopy = createPBRCopy(origPbr, trans);
			trans.complete();
		} catch (DAOException e) {
			logger.error(
					"Exception creating PBR copy using "
							+ origPbr.getApplicationNumber(), e);
			if (pbrCopy != null) {
				try {
					String path = getApplicationDir(pbrCopy);
					DocumentUtil.rmDir(path);
				} catch (IOException ioe) {
					logger.error("Exception deleting file store directory for "
							+ origPbr.getApplicationNumber(), ioe);
				}
			}
			cancelTransaction(trans, new DAOException(e.getMessage()));
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return pbrCopy;
	}

	private PBRNotification createPBRCopy(PBRNotification origPbr,
			Transaction trans) throws DAOException {
		PBRNotification pbrCopy = new PBRNotification(origPbr);
		pbrCopy.setApplicationID(null);
		pbrCopy.setApplicationNumber(null);
		pbrCopy.setDirty(false);
		pbrCopy.setSelected(false);

		// clear out documents and EUs - they will be copied later
		pbrCopy.setPbrDocuments(new ArrayList<PBRNotificationDocument>());
		pbrCopy.setEus(new ArrayList<ApplicationEU>());

		// notes should be cleared - they aren't copied to pbrCopy
		pbrCopy.setApplicationNotes(new ArrayList<ApplicationNote>());

		// create pbr copy, obtaining a new application id
		pbrCopy = (PBRNotification) createNewApplication(pbrCopy, trans);
		// copy documents from original PBR
		copyApplicationDocuments(origPbr, pbrCopy, trans);

		updateApplicationComponents(pbrCopy, true, trans);

		// copy EUs from original PBR
		for (ApplicationEU origEU : origPbr.getEus()) {
			ApplicationEU euCopy = new ApplicationEU(origEU);
			euCopy.setApplicationId(pbrCopy.getApplicationID());
			euCopy.setApplicationEuId(null);
			euCopy = createApplicationEU(euCopy, trans);
			pbrCopy.addEu(euCopy);
		}

		return pbrCopy;
	}

	/**
	 * Create a copy of <code>app</code> that will contain all the data
	 * specified in <code>app</code>, but will exist as a separate application
	 * with its own application number. The copied application will be stored in
	 * the database, then returned by this method.
	 * 
	 * @param origApp
	 *            Application to be copied.
	 * @param newApplicationClass
	 *            type of class which will be returned. Must be the same as
	 *            origApp's class except for PTIOApplication and TVApplication
	 *            which may copy from one another (e.g. an origApp of class
	 *            PTIOApplication may have newApplication class set to
	 *            TVApplication and vice versa).
	 * @return copied Application object.
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Application createApplicationCopy(Application origApp,
			boolean corrected, String correctedReason, boolean amended)
			throws DAOException, RemoteException {
		Application appCopy = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			appCopy = createApplicationCopy(origApp, corrected,
					correctedReason, amended, trans);
			trans.complete();
		} catch (DAOException e) {
			logger.error(
					"Exception creating application copy for "
							+ origApp.getApplicationNumber(), e);
			if (appCopy != null) {
				try {
					String path = getApplicationDir(appCopy);
					DocumentUtil.rmDir(path);
				} catch (IOException ioe) {
					logger.error("Exception deleting file store directory for "
							+ origApp.getApplicationNumber(), ioe);
				}
			}
			cancelTransaction(trans, new DAOException(e.getMessage()));
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return appCopy;
	}

	/**
	 * 
	 * @param origApp
	 * @param newApplicationClass
	 * @param corrected
	 * @param correctedReason
	 * @param amended
	 * @param trans
	 * @return
	 * @throws RemoteException
	 * @throws ServiceFactoryException
	 */
	public Application createApplicationCopy(Application origApp,
			boolean corrected, String correctedReason, boolean amended,
			Transaction trans) throws DAOException, RemoteException {
		Application appCopy = null;

		if (origApp instanceof PTIOApplication) {
			appCopy = new PTIOApplication((PTIOApplication) origApp);
			// Mantis 2604 - Legacy State PTO flag should not be copied to new
			// app
			((PTIOApplication) appCopy).setLegacyStatePTOApp(false);
			((PTIOApplication) appCopy).setKnownIncompleteNSRApp(false);
		} else if (origApp instanceof TVApplication) {
			appCopy = new TVApplication((TVApplication) origApp);
			((TVApplication) appCopy).setLegacyStateTVApp(false);
		} else {
			// only PTIO and TV applications may be copied
			throw new DAOException("Applications of type "
					+ origApp.getClass().getName()
					+ " are not eligible for copy operation");
		}

		// clear fields that must be cleared in order
		// to make appCopy an application distinct from origApp
		appCopy.setApplicationID(null);
		appCopy.setApplicationNumber(null);
		appCopy.setDirty(false);
		appCopy.setSelected(false);
		appCopy.setSubmittedDate(null);
		appCopy.setValidated(false);
		appCopy.setLegacy(false);
		appCopy.setKnownIncomplete(false);
		appCopy.setPreviousApplicationNumber(origApp.getApplicationNumber());

		// create new version of contact info (in case it needs to be modified)
		if (origApp.getContact() != null) {
			Contact contact = origApp.getContact();
			if (isPortalApp()) {
				contact.setFacilityId(appCopy.getFacilityId());
			}

			appCopy.setContact(contact);
			// appCopy.setContact(copyContact(contact, trans));
		}

		// update fields related to copying application
		appCopy.setApplicationAmended(amended);
		appCopy.setApplicationCorrected(corrected);
		appCopy.setApplicationCorrectedReason(correctedReason);

		// don't copy received date if this is a straight copy of an application
		// (not a correction or amendment)
		if (!appCopy.isApplicationCorrected()) {
			appCopy.setReceivedDate(null);
		} else {
			appCopy.setPreviousApplicationNumber(origApp.getApplicationNumber());
		}

		// clear out documents and EUs
		appCopy.setDocuments(new ArrayList<ApplicationDocumentRef>());
		appCopy.setEus(new ArrayList<ApplicationEU>());

		// notes should be cleared - they aren't copied to copyApp
		appCopy.setApplicationNotes(new ArrayList<ApplicationNote>());

		// update facility
		FacilityDAO facDAO = facilityDAO(trans);
		Facility currentFacility = facDAO.retrieveFacility(appCopy
				.getFacilityId());
		appCopy.setFacility(currentFacility);
		
		// create application copy, obtaining a new application id
		appCopy = createNewApplication(appCopy, trans);

		// now that we have an application id, we can copy the documents
		// removed prior to the call to createApplication
		logger.debug("Calling copyApplicationDocuments");
		copyApplicationDocuments(origApp, appCopy, trans);

		if (appCopy instanceof TVApplication) {
			// clear out EU groups since the application_eu_ids will change
			((TVApplication) appCopy).setEuGroups(new ArrayList<TVEUGroup>());
			initTVApplicationComponents((TVApplication) appCopy, trans);
		}

		boolean updateEUs = true;
		updateApplicationComponents(appCopy, updateEUs, trans);

		// restore application EU data
		List<EmissionUnit> fpEus = new ArrayList<EmissionUnit>();
		fpEus.addAll(currentFacility.getEmissionUnits());
		for (ApplicationEU appEu : origApp.getEus()) {
			// make sure EU data is up to date
			synchAppEuWithFacility(appEu, fpEus);
			ApplicationEU euCopy = createApplicationEUCopy(appEu, appCopy,
					trans);
			fpEus.remove(euCopy.getFpEU());
			appCopy.addEu(euCopy);
		}
		// add any "new" EUs that have been added to the facility inventory
		// since the original application was created
		addEusToApplication(appCopy, fpEus, trans);

		// update EU groups for Title V application
		if (appCopy instanceof TVApplication) {
			copyTVApplicationEUGroups(((TVApplication) origApp),
					((TVApplication) appCopy), trans);
			// need to re-retrieve application since it was modified by previous
			// method call
			appCopy = retrieveApplicationWithAllEUs(appCopy.getApplicationID(), trans);
		}

		return appCopy;
	}

	private void copyTVApplicationEUGroups(TVApplication origApp,
			TVApplication appCopy, Transaction trans) throws DAOException {
		for (TVEUGroup origEUGroup : origApp.getEuGroups()) {
			TVEUGroup copyEUGroup = new TVEUGroup();
			copyEUGroup.setTvEuGroupName(origEUGroup.getTvEuGroupName());
			copyEUGroup.setApplicationId(appCopy.getApplicationID());

			// get equivalent EUs from appCopy to add to EU group
			List<TVApplicationEU> copyEUs = new ArrayList<TVApplicationEU>();
			for (TVApplicationEU origEU : origEUGroup.getEus()) {
				for (ApplicationEU copyEU : appCopy.getEus()) {
					if (origEU.getFpEU().getCorrEpaEmuId()
							.equals(copyEU.getFpEU().getCorrEpaEmuId())) {
						copyEUs.add((TVApplicationEU) copyEU);
						break;
					}
				}
			}
			copyEUGroup.setEus(copyEUs);
			copyEUGroup = createTvEuGroup(copyEUGroup, trans);

			ApplicationDAO appDAO = applicationDAO(trans);
			// copy applicable and state applicable requirements
			List<TVApplicableReq> applicableReqs = new ArrayList<TVApplicableReq>();
			for (TVApplicableReq origAppReq : origEUGroup
					.getApplicableRequirements()) {
				TVApplicableReq copyAppReq = new TVApplicableReq(origAppReq);
				copyAppReq.setTvApplicableReqId(appDAO
						.generateApplicableRequirementSeqNo());
				copyAppReq.setApplicationId(appCopy.getApplicationID());
				copyAppReq.setTvEuGroupId(copyEUGroup.getTvEuGroupId());
				applicableReqs.add(copyAppReq);
			}
			copyEUGroup.setApplicableRequirements(applicableReqs);

			applicableReqs = new ArrayList<TVApplicableReq>();
			for (TVApplicableReq origAppReq : origEUGroup
					.getStateOnlyRequirements()) {
				TVApplicableReq copyAppReq = new TVApplicableReq(origAppReq);
				copyAppReq.setTvApplicableReqId(appDAO
						.generateApplicableRequirementSeqNo());
				copyAppReq.setApplicationId(appCopy.getApplicationID());
				copyAppReq.setTvEuGroupId(copyEUGroup.getTvEuGroupId());
				applicableReqs.add(copyAppReq);
			}
			copyEUGroup.setStateOnlyRequirements(applicableReqs);
			modifyTvEuGroup(copyEUGroup, trans);
		}
		boolean modifyEUs = true;
		modifyApplication(appCopy, trans, modifyEUs);
	}

	private Contact copyContact(Contact contact, Transaction trans)
			throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ContactDAO contactDAO = contactDAO(trans);
		Contact copy = null;
		Address address = contact.getAddress();
		if (address != null) {
			address.setAddressId(null);
			address = infraDAO.createAddress(address);
			contact.setAddressId(address.getAddressId());
		}

		contact.setContactId(null);

		if (isInternalApp()) {
			copy = contactDAO.createContact(contact);
		} else if (isPortalApp()) {
			copy = contactDAO.createStagingContact(contact);
		}

		return copy;
	}

	private void copyApplicationDocuments(Application fromApp,
			Application toApp, Transaction trans) throws DAOException {
		if (toApp instanceof PBRNotification) {
			for (PBRNotificationDocument doc : ((PBRNotification) fromApp)
					.getPbrDocuments()) {
				// don't copy attestation documents
				if (PBRNotifDocTypeDef.RO_SIGNATURE.equals(doc.getDocTypeCd())) {
					continue;
				}
				// don't copy documents added after application was submitted
				if (fromApp.getSubmittedDate() != null
						&& doc.getLastModifiedTS() != null
						&& doc.getLastModifiedTS().after(
								fromApp.getSubmittedDate())) {
					logger.debug("Not copying document " + doc.getDocumentID()
							+ ". Document last modified date ("
							+ doc.getLastModifiedTS()
							+ ") is after application submission date ("
							+ fromApp.getSubmittedDate() + ")");
					continue;
				}
				doc.setApplicationId(null);
				PBRNotificationDocument docCopy = new PBRNotificationDocument(
						doc);
				docCopy.setApplicationId(toApp.getApplicationID());
				docCopy.setAttachmentBasePath(null);
				docCopy.setSubPath("Applications");
				copyDocument(doc, docCopy, trans);
				((PBRNotification) toApp).getPbrDocuments().add(docCopy);
			}
		}
		if (toApp instanceof RPCRequest) {
			for (RPCRequestDocument doc : ((RPCRequest) fromApp)
					.getRpcDocuments()) {
				// don't copy attestation documents
				if (RPCRequestDocTypeDef.RO_SIGNATURE
						.equals(doc.getDocTypeCd())) {
					continue;
				}
				// don't copy documents added after application was submitted
				if (fromApp.getSubmittedDate() != null
						&& doc.getLastModifiedTS() != null
						&& doc.getLastModifiedTS().after(
								fromApp.getSubmittedDate())) {
					logger.debug("Not copying document " + doc.getDocumentID()
							+ ". Document last modified date ("
							+ doc.getLastModifiedTS()
							+ ") is after application submission date ("
							+ fromApp.getSubmittedDate() + ")");
					continue;
				}
				RPCRequestDocument docCopy = new RPCRequestDocument(doc);
				docCopy.setApplicationId(toApp.getApplicationID());
				copyDocument(doc, docCopy, trans);
				((RPCRequest) toApp).getRpcDocuments().add(doc);
			}
		} else {
			for (ApplicationDocumentRef docRef : fromApp.getDocuments()) {
				// don't copy attestation documents
				/*
				 * if (ApplicationDocumentTypeDef.RO_SIGNATURE.equals(docRef
				 * .getApplicationDocumentTypeCD())) { continue; }
				 */
				// don't copy documents added after application was submitted
				// (if copying to gateway)
				if (isPortalApp()
						&& fromApp.getSubmittedDate() != null
						&& docRef.getLastModifiedTS() != null
						&& docRef.getLastModifiedTS().after(
								fromApp.getSubmittedDate())) {
					logger.debug("Not copying document "
							+ docRef.getApplicationDocId()
							+ ". Document last modified date ("
							+ docRef.getLastModifiedTS()
							+ ") is after application submission date ("
							+ fromApp.getSubmittedDate() + ")");
					continue;
				}
				logger.debug("internal app = " + isInternalApp()
						+ " submitted date = " + fromApp.getSubmittedDate()
						+ " document last modified = "
						+ docRef.getLastModifiedTS());
				ApplicationDocumentRef docRefCopy = new ApplicationDocumentRef(
						docRef);
				docRefCopy.setApplicationId(toApp.getApplicationID());
				if ((docRefCopy = copyApplicationDocumentRef(docRef,
						docRefCopy, trans)) != null) {
					toApp.addDocument(docRefCopy);
				}
			}
		}
	}

	/*
	 * private boolean appHasAttestationAttachment(Application app) throws
	 * DAOException { boolean hasAttestationAttachment = false; if (app
	 * instanceof PBRNotification) { for (PBRNotificationDocument doc :
	 * ((PBRNotification) app) .getPbrDocuments()) { if
	 * (PBRNotifDocTypeDef.RO_SIGNATURE.equals(doc.getDocTypeCd())) {
	 * hasAttestationAttachment = true; break; } } } if (app instanceof
	 * RPCRequest) { for (RPCRequestDocument doc : ((RPCRequest)
	 * app).getRpcDocuments()) { if (RPCRequestDocTypeDef.RO_SIGNATURE
	 * .equals(doc.getDocTypeCd())) { hasAttestationAttachment = true; break; }
	 * } } else { for (ApplicationDocumentRef docRef : app.getDocuments()) { if
	 * (ApplicationDocumentTypeDef.RO_SIGNATURE.equals(docRef
	 * .getApplicationDocumentTypeCD())) { hasAttestationAttachment = true;
	 * break; } } } return hasAttestationAttachment; }
	 */

	private ApplicationDocumentRef copyApplicationDocumentRef(
			ApplicationDocumentRef fromDocRef, ApplicationDocumentRef toDocRef,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		DocumentDAO docDAO = documentDAO(trans);

		if (fromDocRef.getDocumentId() == null && fromDocRef.getTradeSecretDocId() != null) {
			logger.error("Application document ref "
					+ fromDocRef.getApplicationDocId()
					+ " has a trade secret document, but no public document");
			return null;
		}

		toDocRef.setApplicationDocId(null);
		// create copies of the actual document only if the document is copied
		// from one application to another.
		if (!fromDocRef.getApplicationId().equals(toDocRef.getApplicationId())) {
			if (fromDocRef.getDocumentId() != null) {
				Document publicDoc = docDAO.retrieveDocument(fromDocRef
						.getDocumentId());
				if (isPortalApp() && publicDoc == null) {
					// check internal database. This may be a copy from
					// internal to external
					DocumentDAO readDAO = documentDAO(getSchema(CommonConst.READONLY_SCHEMA));
					readDAO.setTransaction(trans);
					publicDoc = readDAO.retrieveDocument(fromDocRef
							.getDocumentId());
				}
				if (publicDoc != null) {
					ApplicationDocument docCopy = new ApplicationDocument(
							publicDoc);
					docCopy.setApplicationId(toDocRef.getApplicationId());
					docCopy.setTemporary(false);
					copyDocument(publicDoc, docCopy, trans);
					toDocRef.setDocumentId(docCopy.getDocumentID());
				} else {
					logger.error("Could not find public document with id: "
							+ fromDocRef.getDocumentId());
				}
			}
			if (fromDocRef.getTradeSecretDocId() != null) {
				Document tsDoc = docDAO.retrieveDocument(fromDocRef
						.getTradeSecretDocId());
				if (isPortalApp() && tsDoc == null) {
					// check internal database. This may be a copy from
					// internal to external
					DocumentDAO readDAO = documentDAO(getSchema(CommonConst.READONLY_SCHEMA));
					readDAO.setTransaction(trans);
					tsDoc = readDAO.retrieveDocument(fromDocRef
							.getTradeSecretDocId());
				}
				if (tsDoc != null) {
					ApplicationDocument docCopy = new ApplicationDocument(tsDoc);
					docCopy.setApplicationId(toDocRef.getApplicationId());
					docCopy.setTemporary(false);
					copyDocument(tsDoc, docCopy, trans);
					toDocRef.setTradeSecretDocId(docCopy.getDocumentID());
				} else {
					logger.error("Could not find trade secret document with id: "
							+ fromDocRef.getTradeSecretDocId());
				}
			}
		} else {
			// this is a copy of documents within the same application. Just
			// have
			// the copyDocRef "point to" the document rather than make a copy of
			// the
			// same document within the application.
			// No work needs to be done here, but we'll log a message for
			// the sake of debugging.
			logger.debug("logical copy of documents within application "
					+ fromDocRef.getApplicationId());
		}
		toDocRef = appDAO.createApplicationDocument(toDocRef);
		return toDocRef;
	}

	private void copyDocument(Document fromDoc, Document toDoc,
			Transaction trans) throws DAOException {
		DocumentDAO docDAO = documentDAO(trans);
		// first copy the actual file for this document
		try {
			toDoc.setDocumentID(null);
			docDAO.createDocument(toDoc);
			DocumentUtil.copyDocument(fromDoc.getPath(), toDoc.getPath());
		} catch (IOException e) {
			throw new DAOException("Exception copying document from "
					+ fromDoc.getPath() + " to " + toDoc.getPath(), e);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public List<ValidationMessage> validateApplication(int applicationID)
			throws RemoteException {
		Transaction trans = null;
		List<ValidationMessage> retVal = new ArrayList<ValidationMessage>();

		try {
			trans = TransactionFactory.createTransaction();
			retVal = validateApplication(applicationID, trans);
			trans.complete();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			closeTransaction(trans);
		}

		return retVal;
	}

	private List<ValidationMessage> validateApplication(int applicationID,
			Transaction trans) throws Exception, RemoteException, 
			ServiceFactoryException {
		logger.debug("Enter validateApplication(int applicationID, Transaction trans)" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
		
		// Application is valid unless an error is recorded
		boolean appIsValid = true;
		LinkedList<ValidationMessage> retVal = new LinkedList<ValidationMessage>();

		// Retrieve application summary and basic EU data from DB.
		Application app = null;
		boolean staging = false;
		if (isPortalApp()) {
			staging = true;
		}
		
		if (isInternalApp()) {
			app = retrieveApplicationWithAllEUs(applicationID, trans);
		} else {
			app = retrieveApplicationWithAllEUs(applicationID, trans, staging);
		}
		
		Facility appFacility = ServiceFactory.getInstance()
				.getFacilityService()
				.retrieveFacilityProfile(app.getFacility().getFpId(), true);
		app.setFacility(appFacility);

		// amended applications and known incomplete applications bypass QA/QC checks
		if (!app.isApplicationAmended() && !app.isKnownIncomplete()) {
			// get list of emission unit ids
			List<EmissionUnit> includedEUs = new ArrayList<EmissionUnit>();
			for (ApplicationEU appEu : app.getIncludedEus()) {
				// get facility inventory EU objects
				appEu.setFpEU(app.getFacility().getEmissionUnit(
						appEu.getFpEU().getEmuId()));

				includedEUs.add(appEu.getFpEU());
			}
			if (!app.isLegacy()) {
				// make sure attachments are intact
				try {
					if (app instanceof PTIOApplication) {
						verifyNSRAppRequiredAttachments((PTIOApplication)app);
					}
					validateApplicationAttachments(app, retVal, trans);
				} catch (IOException e) {
					throw new RemoteException(
							"Exception validating attachments for "
									+ app.getApplicationNumber(), e);
				}

				List<ValidationMessage> fpVal = null;
				// Perform application type-specific validation
				Class<? extends Application> appClass = app.getClass();
				if (appClass.equals(PTIOApplication.class)) {
					PTIOApplication ptioApp = (PTIOApplication) app;

					validateAppRequiredFields(app, retVal);
					validatePTIOApplication(ptioApp, retVal);
					
					// validate facility inventory
					fpVal = FacilityValidation
							.validatePTIandPTIOpermitApplication(
									app.getFacility(), includedEUs);
				} else if (appClass.equals(TVApplication.class)) {
					logger.debug("Validating application required fields...");
					if (TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING.equals(((TVApplication) app).getTvApplicationPurposeCd())) {
						((TVApplication) app).removeRevisionRequiredFields();
					}
					validateAppRequiredFields(app, retVal);
					
					logger.debug("Validating TV application...");
					validateTVApplication((TVApplication) app, retVal);
					
					logger.debug("Validating TV application facility...");
					fpVal = FacilityValidation.validateTVPTOpermitApplication(
							app.getFacility(), includedEUs);
					
					logger.debug("Done with application type-specific validations...");
				} else if (appClass.equals(PBRNotification.class)) {
					validatePBRNotification((PBRNotification) app, retVal);
					fpVal = FacilityValidation.validatePBRnotification(app
							.getFacility().getFpId(), includedEUs);
				} else if (appClass.equals(RPRRequest.class)) {
					validateRPRRequest((RPRRequest) app, retVal);
					fpVal = new ArrayList<ValidationMessage>();
				} else if (appClass.equals(RPERequest.class)) {
					validateRPERequest((RPERequest) app, retVal);
					fpVal = new ArrayList<ValidationMessage>();
				} else if (appClass.equals(RPCRequest.class)) {
					validateRPCRequest((RPCRequest) app, retVal);
					fpVal = new ArrayList<ValidationMessage>();
					fpVal = FacilityValidation.validateRAPM(app.getFacility()
							.getFpId());
				//} else if (appClass.equals(TIVApplication.class)) {
				//	validateTIVApplication((TIVApplication) app, retVal);
				//	fpVal = FacilityValidation.validateTIVAcidRainApplication(
				//			app.getFacility().getFpId(), includedEUs);
				}

				// make sure contact info is valid (if provided)
				if (app.getContact() != null) {
					logger.debug("Validating application contact...");
					List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
					if (!app.getClass().equals(TVApplication.class)) {
						app.getContact().requiredField(
								app.getContact().getFirstNm(),
								"contactFirstName", "First name",
								"application", valMessages);
						app.getContact().requiredField(
								app.getContact().getLastNm(),
								"contactLastName", "Last name", "application",
								valMessages);
					}
					app.getContact().requiredField(
							app.getContact().getAddress().getAddressLine1(),
							"addressLine1", "Address (line 1)", "application",
							valMessages);
					app.getContact()
							.requiredField(
									app.getContact().getAddress().getCityName(),
									"city", "City/Township", "application",
									valMessages);
					app.getContact().requiredField(
							app.getContact().getAddress().getState(), "state",
							"State", "application", valMessages);
					app.getContact().requiredField(
							app.getContact().getAddress().getZipCode5(),
							"zipCode", "Zip Code", "application", valMessages);
					if (valMessages.size() > 0) {
						for (ValidationMessage error : valMessages) {
							retVal.add(error);
						}
					}
				}

				// add facility validation messages to validation message popup
				boolean hasFpErrors = false;
				for (ValidationMessage fpMsg : fpVal) {
					retVal.add(fpMsg);
					if (fpMsg.getSeverity().equals(
							ValidationMessage.Severity.ERROR)) {
						hasFpErrors = true;
					}
				}
				if (hasFpErrors && isInternalApp()) {
					// add info message stating that app may need to be updated
					// to reflect changes to facility inventory.
					retVal.addFirst(new ValidationMessage(
							"appDetail",
							"If the facility inventory has recently been updated, "
									+ "you may need to click the 'Associate with Current Facility Inventory' "
									+ "button to avoid reporting errors that have already been corrected.",
							ValidationMessage.Severity.INFO, "application"));
				}

				for (ValidationMessage message : retVal) {
					if (message.getSeverity().equals(
							ValidationMessage.Severity.ERROR)) {
						appIsValid = false;
						break;
					}
				}
			} else {
				Class<? extends Application> appClass = app.getClass();
				if (appClass.equals(PTIOApplication.class)) {
					PTIOApplication ptioApp = (PTIOApplication) app;
					if (((PTIOApplication) app).isLegacyStatePTOApp()) {
						validateLegacyStatePTOApplication(ptioApp, retVal);
					} 
				} else if (appClass.equals(TVApplication.class)) {
					if (((TVApplication) app).isLegacyStateTVApp()) {
						validateLegacyStateTVApplication((TVApplication) app,
								retVal);
					}
				}
				
				// legacy app included EUs should validate successfully
				for (ApplicationEU appEu : app.getIncludedEus()) {
					appEu.setValidated(true);
				}
				
				for (ValidationMessage message : retVal) {
					if (message.getSeverity().equals(
							ValidationMessage.Severity.ERROR)) {
						appIsValid = false;
						break;
					}
				}
			}

		}

		// Save updated application data to DB
		app.setValidated(appIsValid);
		boolean modifyEUs = true;
		modifyApplication(app, trans, modifyEUs);

		logger.debug("Exit validateApplication(int applicationID, Transaction trans)" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());

		return retVal;
	}
/*
	private void validateTIVApplication(TIVApplication app,
			LinkedList<ValidationMessage> messages) {
		if (app.getAppPurposeCd() == null) {
			messages.add(new ValidationMessage("tivAppPurposeChoice",
					"The reason for this application is missing",
					ValidationMessage.Severity.ERROR, "application"));
		} else if (app.getAppPurposeCd().equals(
				TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING)
				&& app.getReasonCd() == null) {
			messages.add(new ValidationMessage("tivAppPurposeChoice",
					"The reason for this application is incomplete",
					ValidationMessage.Severity.ERROR, "application"));
		}
		if (app.getApplicationDesc() == null
				|| app.getApplicationDesc().length() == 0) {
			messages.add(new ValidationMessage(
					"tivPermitReasonText",
					"The reason for which a permit is being applied is missing",
					ValidationMessage.Severity.ERROR, "application"));
		}

		List<ApplicationEU> includedEus = app.getIncludedEus();
		if (includedEus.size() == 0) {
			messages.add(new ValidationMessage("applicationDetailTree",
					"At least one EU must be included in the application",
					ValidationMessage.Severity.ERROR, "application"));
		}

		// set EUs to valid if application is valid since no EU validation is
		// needed.
		for (ApplicationEU appEU : app.getIncludedEus()) {
			appEU.setValidated(true);
		}
	}
*/
	private void validateApplicationAttachments(Application app,
			List<ValidationMessage> retVal, Transaction trans)
			throws IOException {
		if (app instanceof PBRNotification) {
			for (PBRNotificationDocument doc : ((PBRNotification) app)
					.getPbrDocuments()) {
				if (!DocumentUtil.canRead(doc.getPath())) {
					retVal.add(new ValidationMessage(
							"DocTab",
							"Attachment file with description '"
									+ doc.getDescription()
									+ "' is missing (possibly due to a virus) and "
									+ "has been removed from the attachments table. Please check your "
									+ "copy for viruses and upload it again.",
							ValidationMessage.Severity.ERROR, "application"));
					removePBRNotificationDocument(doc, trans);
				}
			}
		} else if (app instanceof RPCRequest) {
			for (RPCRequestDocument doc : ((RPCRequest) app).getRpcDocuments()) {
				if (!DocumentUtil.canRead(doc.getPath())) {
					retVal.add(new ValidationMessage(
							"DocTab",
							"Attachment file with description '"
									+ doc.getDescription()
									+ "' is missing (possibly due to a virus) and "
									+ "has been removed from the attachments table. Please check your "
									+ "copy for viruses and upload it again.",
							ValidationMessage.Severity.ERROR, "application"));
					removeRPCRequestDocument(doc, trans);
				}
			}
		}

		// Validate application required attachment
		List<ValidationMessage> result = validateRequiredAttachments(app);

		if (result != null && result.size() > 0)
			retVal.addAll(result);

		validateDocumentList(app.getDocuments(), retVal, "application", trans);

		List<ApplicationEU> appEUs = app.getIncludedEus();

		for (ApplicationEU appEU : appEUs) {
			// Validate EU required attachment
			result = validateRequiredAttachments(appEU);

			if (result != null && result.size() > 0)
				retVal.addAll(result);

			validateDocumentList(appEU.getEuDocuments(), retVal,
					"eu:" + appEU.getApplicationEuId() + ":"
							+ appEU.getFpEU().getEpaEmuId(), trans);

		}
	}

	private List<ValidationMessage> validateRequiredAttachments(Application app) {
		List<ApplicationDocumentRef> docs = app.getDocuments();
		List<ValidationMessage> result = new ArrayList<ValidationMessage>();
		DefSelectItems requiredTypeItems = null;

		if (app instanceof TVApplication) {
			requiredTypeItems = TVApplicationDocumentTypeDef.getData()
					.getItems();
		} else {
			requiredTypeItems = ApplicationDocumentTypeDef.getData().getItems();
		}

		for (ApplicationDocumentRef docRef : docs) {
			boolean requiredAttachmentNotFound = docRef.isRequiredDoc()
					&& docRef.getDocumentId() == null;

			if (requiredAttachmentNotFound) {
				String typeDesc = (String) requiredTypeItems.getItemDesc().get(
						docRef.getApplicationDocumentTypeCD());

				String errorMsg = "A " + typeDesc + " attachment is required";
				String property = "DocTab";

				result.add(new ValidationMessage(property, errorMsg,
						ValidationMessage.Severity.ERROR, "application"));
			}
		}

		return result;
	}

	private List<ValidationMessage> validateRequiredAttachments(
			ApplicationEU appEU) {
		List<ApplicationDocumentRef> docs = appEU.getEuDocuments();
		List<ValidationMessage> result = new ArrayList<ValidationMessage>();
		DefSelectItems requiredTypeItems = null;

		if (appEU instanceof TVApplicationEU) {
			requiredTypeItems = TVApplicationDocumentTypeDef.getData()
					.getItems();
		} else {
			requiredTypeItems = ApplicationDocumentTypeDef.getData().getItems();
		}

		for (ApplicationDocumentRef docRef : docs) {
			boolean requiredAttachmentNotFound = docRef.isRequiredDoc()
					&& docRef.getDocumentId() == null;

			if (requiredAttachmentNotFound) {
				Integer docEuId = docRef.getApplicationEUId();
				String typeDesc = (String) requiredTypeItems.getItemDesc().get(
						docRef.getApplicationDocumentTypeCD());
				String errorMsg = "A " + typeDesc + " attachment is required";
				String property = "DocTab";
				String epaEmuId = appEU.getFpEU().getEpaEmuId();

				result.add(new ValidationMessage(property, errorMsg,
						ValidationMessage.Severity.ERROR, "eu:" + docEuId,
						epaEmuId));
			}
		}

		return result;
	}

	private void validateDocumentList(List<ApplicationDocumentRef> documents,
			List<ValidationMessage> retVal, String referenceID,
			Transaction trans) throws IOException {
		DocumentDAO docDAO = documentDAO(trans);
		for (ApplicationDocumentRef docRef : documents) {
			boolean removeDoc = false;
			Integer docId = docRef.getDocumentId();
			String tableLocStr = "application attachments table";
			if (referenceID != null && referenceID.startsWith("eu:")) {
				int lastColonIdx = referenceID.lastIndexOf(':');
				tableLocStr = "attachments table for EU: "
						+ referenceID.substring(lastColonIdx + 1);
				referenceID = referenceID.substring(0, lastColonIdx - 1);
			}
			if (docId != null) {
				Document doc = docDAO.retrieveDocument(docId);
				if (doc == null || !DocumentUtil.canRead(doc.getPath())) {
					retVal.add(new ValidationMessage(
							"DocTab",
							"Public attachment file with description '"
									+ docRef.getDescription()
									+ "' is missing (possibly due to a virus) and "
									+ "has been removed from the "
									+ tableLocStr + ". Please check your "
									+ "copy for viruses and upload it again.",
							ValidationMessage.Severity.ERROR, referenceID));
					removeDoc = true;
				}
			}
			docId = docRef.getTradeSecretDocId();
			if (docId != null) {
				Document doc = docDAO.retrieveDocument(docId);
				if (doc == null || !DocumentUtil.canRead(doc.getPath())) {
					retVal.add(new ValidationMessage(
							"DocTab",
							"Trade secret attachment file with description '"
									+ docRef.getDescription()
									+ "' is missing (possibly due to a virus) and "
									+ "has been removed from the "
									+ tableLocStr + ". Please check your "
									+ "copy for viruses and upload it again.",
							ValidationMessage.Severity.ERROR, referenceID));
					removeDoc = true;
				}
			}
			if (removeDoc) {
				removeApplicationDocument(docRef, trans, isPortalApp());
			}
		}
	}

	private void validateRPCRequest(RPCRequest request,
			List<ValidationMessage> retVal) {
		// received date is required (for internal app only. defaults to submit
		// date on portal)
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			if (request.getReceivedDate() == null) {
				retVal.add(new ValidationMessage("applicationReceivedDate",
						"Date request received must be specified",
						ValidationMessage.Severity.ERROR, "application"));
			}
		}

		// rpc type is required
		if (request.getRpcTypeCd() == null) {
			retVal.add(new ValidationMessage("rpcTypeChoice",
					"Modification Type must be specified",
					ValidationMessage.Severity.ERROR, "application"));
		}

		// permit id is required
		if (request.getPermitId() == null) {
			retVal.add(new ValidationMessage("permitIdChoice",
					"Permit to be Modified must be specified",
					ValidationMessage.Severity.ERROR, "application"));
		}

		// application description is required
		if (request.getApplicationDesc() == null) {
			retVal.add(new ValidationMessage("permitReasonText",
					"Reason permit is being modified must be specified",
					ValidationMessage.Severity.ERROR, "application"));
		}

		// contact is required
		if (request.getContact() == null) {
			retVal.add(new ValidationMessage("contactFirstName",
					"Modification Request Contact must be specified",
					ValidationMessage.Severity.ERROR, "application"));
		}

		// make sure at least one EU is included in the request
		if (request.getIncludedEus().size() == 0) {
			retVal.add(new ValidationMessage("applicationReceivedDate",
					"At least one EU must be included in the request",
					ValidationMessage.Severity.ERROR, "application"));
		} else {
			// no data is associated with these EUs, so mark them as validated
			for (ApplicationEU eu : request.getIncludedEus()) {
				eu.setValidated(true);
			}
		}
	}

	private void validateRPERequest(RPERequest request,
			List<ValidationMessage> retVal) {
		PermitService pBO;
		if (request.getTerminationDate() == null)
			retVal.add(new ValidationMessage("TerminationDate",
					"Termination Date is not set",
					ValidationMessage.Severity.ERROR));
		else
			try {
				// It should have a range check enforcing the value to be
				// between 18
				// months and 30 months from the permit's effective date.
				pBO = ServiceFactory.getInstance().getPermitService();
				Timestamp eDate = pBO.retrievePermit(request.getPermitId())
						.getPermit().getEffectiveDate();
				GregorianCalendar bdgc = new GregorianCalendar();
				bdgc.setTime(eDate);
				bdgc.add(Calendar.MONTH, 18);
				Timestamp m18 = new Timestamp(bdgc.getTimeInMillis());

				GregorianCalendar edgc = new GregorianCalendar();
				edgc.setTime(eDate);
				edgc.add(Calendar.MONTH, 30);
				Timestamp m30 = new Timestamp(edgc.getTimeInMillis());

				if (request.getTerminationDate().before(m18)
						|| request.getTerminationDate().after(m30))
					retVal.add(new ValidationMessage("TerminationDate",
							"Termination Date is not between " + m18 + " to "
									+ m30, ValidationMessage.Severity.ERROR));

			} catch (ServiceFactoryException e) {
				retVal.add(new ValidationMessage("validateRPERequest",
						"Cannot get instance of PermitService",
						ValidationMessage.Severity.ERROR));
			} catch (RemoteException e) {
				retVal.add(new ValidationMessage("retrievePermit",
						"Cannot retrievePermit " + request.getPermitId(),
						ValidationMessage.Severity.ERROR));
			}

	}

	private void validateRPRRequest(RPRRequest request,
			List<ValidationMessage> retVal) throws RemoteException {

		Integer permitId = request.getPermitId();
		Permit permit = permitDAO().retrievePermit(permitId);
		if (PermitTypeDef.TV_PTO.equalsIgnoreCase(permit.getPermitType())
				&& !request.isRevokeEntirePermit())
			retVal.add(new ValidationMessage(
					"revokeEntirePermit",
					"'Rescind entire permit' has to be checked in the application to revoke Title V Permit",
					ValidationMessage.Severity.ERROR, "application"));

		int includeableEUCount = 0;
		for (ApplicationEU appEU : request.getEus()) {
			if (!appEU.isNotIncludable()) {
				includeableEUCount++;
			}
		}
		if (request.isRevokeEntirePermit()
				&& request.getIncludedEus().size() != includeableEUCount)
			retVal.add(new ValidationMessage(
					"revokeEntirePermit",
					"All EUs must be included in the application if 'Rescind entire permit' is checked",
					ValidationMessage.Severity.ERROR, "application"));

		String bfr = request.getBasisForRevocation();
		if (request.isRevokeEntirePermit() && bfr == null
				|| (bfr != null && bfr.length() == 0))
			retVal.add(new ValidationMessage(
					"basisForRevoke",
					"Basis for Rescission is required if 'Rescind entire permit' is checked",
					ValidationMessage.Severity.ERROR, "application"));

		List<ApplicationEU> includedEus = request.getIncludedEus();
		if (includedEus.size() == 0) {
			retVal.add(new ValidationMessage("applicationReceivedDate",
					"At least one EU must be included in the application",
					ValidationMessage.Severity.ERROR, "application"));
		} else {
			for (ApplicationEU appEU : includedEus) {
				if (!request.isRevokeEntirePermit()
						&& (appEU.getEuText() == null || (appEU.getEuText() != null && appEU
								.getEuText().length() == 0))) {
					retVal.add(new ValidationMessage(
							"basisForRevoke",
							"EU Basis for Rescission of "
									+ appEU.getFpEU().getEpaEmuId()
									+ " is required if 'Rescind entire permit' is not checked",
							ValidationMessage.Severity.ERROR, "application"));
					appEU.setValidated(false);
				} else
					appEU.setValidated(true);
			}
		}
	}

	private void validatePBRNotification(PBRNotification notification,
			List<ValidationMessage> retVal) {
		if (notification.getPbrTypeCd() == null) {
			retVal.add(new ValidationMessage("pbrTypeChoice",
					"PBR Type must be specified",
					ValidationMessage.Severity.ERROR, "application"));
		}
		if (notification.getPbrReasonCd() == null) {
			retVal.add(new ValidationMessage("pbrReasonChoice",
					"PBR Reason must be specified",
					ValidationMessage.Severity.ERROR, "application"));
		}
		List<ApplicationEU> includedEus = notification.getIncludedEus();
		if (includedEus.size() == 0) {
			retVal.add(new ValidationMessage("applicationReceivedDate",
					"At least one EU must be included in the application",
					ValidationMessage.Severity.ERROR, "application"));
		} else {
			for (ApplicationEU appEU : includedEus) {
				// currently, there is no validation for an EU, so mark it as
				// valid
				appEU.setValidated(true);
			}
		}

		String longDescription = PBRTypeDef.getLongDscData().getItems()
				.getDescFromAllItem(notification.getPbrTypeCd());
		String errorMsg = "An attachment is required. " + longDescription;
		// try to create a shorter error message by extracting the document
		// type.
		// longDescription should be formatted as follows:
		// "In order for your notification to be complete, attach a completed "<document
		// type>". Etc."
		// ^--- we want to extract this text ---^
		int commaIdx = longDescription.indexOf(',');
		if (commaIdx > 0) {
			int periodIdx = longDescription.indexOf('.', commaIdx);
			if (periodIdx > 0) {
				errorMsg = "Please "
						+ longDescription
								.substring(commaIdx + 1, periodIdx + 1);
			}
		}
		if (notification.getPbrDocuments().size() == 0) {
			retVal.add(new ValidationMessage("DocTab", errorMsg,
					ValidationMessage.Severity.ERROR, "application"));
		}
	}

	private void validateLegacyStatePTOApplication(PTIOApplication app,
			List<ValidationMessage> messages) {
		Timestamp impactCutoffDate = getImpactLegacyCutoffDate();
		// application received date must be prior to IMPACT legacy cutoff date
		if (app.getReceivedDate() != null && impactCutoffDate != null
				&& !app.getReceivedDate().before(impactCutoffDate)) {
			SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
			messages.add(new ValidationMessage("applicationReceivedDate",
					"Date application received must be prior to IMPACT legacy cutoff date ("
							+ format.format(impactCutoffDate) + ")",
					ValidationMessage.Severity.ERROR, "application"));
			app.setValidated(false);
		}
		
		if (app.getReceivedDate() == null) {
			messages.add(new ValidationMessage("applicationReceivedDate",
					"Date application received is missing",
					ValidationMessage.Severity.ERROR, "application"));
			app.setValidated(false);
		}

		// make sure that at least one EU is included and all EUs have a
		// AQD description
		List<ApplicationEU> includedEus = app.getIncludedEus();
		if (includedEus.size() == 0) {
			messages.add(new ValidationMessage("legacyAppBox",
					"At least one EU must be included in the application",
					ValidationMessage.Severity.WARNING, "application"));
		} else {

			for (ApplicationEU appEU : includedEus) {
				/*
				 * General Permit not valid for WY
				 * app.setValidated(validateLegacyPTOAppEU((PTIOApplicationEU)
				 * appEU, messages));
				 */
				app.setValidated(true);
			}
		}
	}

	/*
	 * General Permit not valid for WY private boolean
	 * validateLegacyPTOAppEU(PTIOApplicationEU eu, List<ValidationMessage>
	 * messages) { boolean ok = true;
	 * 
	 * if (eu.isGeneralPermit()) { if (eu.getGeneralPermitTypeCd() == null) {
	 * messages.add(new ValidationMessage("generalPermitTypeChoice",
	 * "General Permit Category is missing", ValidationMessage.Severity.ERROR,
	 * "eu:" + eu.getApplicationEuId(), eu.getFpEU() .getEpaEmuId())); ok =
	 * false; } else if (eu.getModelGeneralPermitCd() == null) {
	 * messages.add(new ValidationMessage("modelGeneralPermitChoice",
	 * "General Permit Type is missing", ValidationMessage.Severity.ERROR, "eu:"
	 * + eu.getApplicationEuId(), eu.getFpEU() .getEpaEmuId())); ok = false; } }
	 * eu.setValidated(ok); return ok; }
	 */

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public List<ValidationMessage> submitApplication(Application app,
			int userId, Timestamp dfltReceivedDate) throws RemoteException {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		// don't allow submission of non-validated application
		if (!app.getValidated()) {
			throw new DAOException(
					"Cannot submit. Application is not validated");
		}
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			validationMessages = submitApplication(app, userId,
					dfltReceivedDate, true, null, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return validationMessages;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public List<ValidationMessage> submitApplication(Application app,
			int userId, Timestamp dfltReceivedDate, boolean createWorkFlow,
			String permitNumber) throws RemoteException {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			validationMessages = submitApplication(app, userId,
					dfltReceivedDate, createWorkFlow, permitNumber, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return validationMessages;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	// 	Run this as an independent transaction so that in case there is an exception
	// and workflow has to be rolledback, it can be done so without getting into a
	// deadlock situation
	@Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	private List<ValidationMessage> submitApplicationInternal(Application app,
			int userId, Timestamp dfltReceivedDate, boolean createWorkFlow,
			String permitNumber, Transaction trans) throws RemoteException {
		logger.debug("Trying to submit application.");
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		boolean ok = true;
		Permit permit = null;
		Integer externalId = null;
		Integer workflowId = null;
		
		if (CommonConst.GATEWAY_USER_ID == userId) {
			this.application = submitApplicationFromGateway(app, this.facility, trans);
			app = this.application;
		} 

		// if received date is not already set in application,
		// use dfltReceivedDate passed into this method
		if (app.getReceivedDate() == null) {
			app.setReceivedDate(dfltReceivedDate);
		}

		if (app instanceof PBRNotification) {
			if (((PBRNotification) app).getDispositionFlag() == null
					|| ((PBRNotification) app).getDispositionFlag().length() == 0) {
				((PBRNotification) app)
						.setDispositionFlag(PBRNotification.RECEIVED);
			}
		}

		if (!app.isLegacy() && app.isApplicationCorrected()) {
			// just create todo list item for corrected applications
			externalId = app.getApplicationID();
			workflowId = createCorrectedAppWorkflow(app, userId, trans);
			validationMessages.addAll(completeApplicationSubmission(app,
					userId, trans));
		} else {
			// create permit for non-corrected applications
			try {
				if (!app.isLegacy()) {
					// create a permit for this submitted application
					logger.debug("Trying to create permit for application.");
					permit = createPermitForApplication(app, userId,
							permitNumber, trans);

					logger.debug("Created permit object for application.");

					app.addReferencedPermit(permit);
					
					if (createWorkFlow) {
						logger.debug("Creating workflow");
						externalId = permit.getPermitID();
						workflowId = createPermitWorkflow(app, permit, userId,
								trans);
						logger.debug("Workflow has been created");
					} else {
						logger.debug("Permit workflow not being created for application "
								+ app.getApplicationNumber());
					}
					boolean facilityNeedsUpdate = false;
					FacilityDAO facDAO = facilityDAO(trans);
					Facility facility = facDAO.retrieveFacility(app
							.getFacility().getFpId());
					FacilityService facBO = ServiceFactory.getInstance()
							.getFacilityService();

					//
					// set flags on facility according to requirement
					// 2.2.2-PTI_PTO_PTIO-18-3
					// (not applicable to Legacy applications)
					//
					if (app.getClass().equals(TVApplication.class)) {
						// setting permit class code causes Emissions reporting
						// category to change. Note: Emissions reporting
						// category
						// cannot be set directly since it is overridden by
						// permit
						// class code.
						facility.setPermitClassCd(PermitClassDef.TV);
						if (facility.getPermitStatusCd() == null
								|| facility.getPermitStatusCd().equals(
										FacPermitStatusDef.NA)) {
							facility.setPermitStatusCd(FacPermitStatusDef.PENDING_INITIAL);
							facilityNeedsUpdate = true;
						}
					}

					// mark facility for copy on change unless it is a legacy
					// app
					// and
					// the copy on change flag is already set
					if (!facility.isCopyOnChange()) {
						facility.setCopyOnChange(true);
						facilityNeedsUpdate = true;
					}

					if (facilityNeedsUpdate) {
						facDAO.modifyFacility(facility);
						// facBO.modifyFacility(facility, userId);
					}
				}

				validationMessages.addAll(completeApplicationSubmission(app,
						userId, trans));

				logger.debug("Application has apparently been submitted");
				
			} catch (Exception e) {
				// Throw it all away if we have an exception
				if (permit != null) {
					try {
						// wipe our permit directory
						PermitService pBO = ServiceFactory.getInstance()
								.getPermitService();
						logger.warn("Removing directory for permit "
								+ permit.getPermitNumber());
						pBO.removePermitDir(permit);
						if (app != null) {
							app.removeReferencedPermit(permit);
						}
					} catch (IOException e1) {
						logger.error("Unable to delete permit directory for "
								+ permit.getPermitNumber(), e1);
					} catch (ServiceFactoryException e2) {
						logger.error("Unable to delete permit directory for "
								+ permit.getPermitNumber(), e2);
					}
				}
				
				// rollback workflow
				if (workflowId != null) {
					throw new WorkflowRollbackException(
							"Exception while submitting application "
									+ app.getApplicationNumber(), e, workflowId, externalId);
				}

				throw new RemoteException(
						"Exception while submitting application "
								+ app.getApplicationNumber(), e);
			}
		}
		if (!ok) {
			// not ok - something is wrong
			validationMessages.add(new ValidationMessage(
					"applicationReceivedDate", "Application submission failed",
					ValidationMessage.Severity.ERROR));
		}

		return validationMessages;
	}

	private List<ValidationMessage> completeApplicationSubmission(
			Application app, int userId, Transaction trans)
			throws RemoteException {
		logger.trace("DLTRACE --> completeApplicationSubmission");
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		// determine whether this application has already been submitted
		// and is being resubmitted
		boolean isResubmit = app.getSubmittedDate() != null;

		// update database to show application has been submitted
		app.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
		boolean modifyEUs = true;
		modifyApplication(app, trans, modifyEUs);

		// log event unless this is a AQD amendment or a resubmitted application
		if (!app.isApplicationAmended() && !app.isLegacy() && !isResubmit) {
			logger.debug("logging application received event...");
			logApplicationReceivedEvent(app, userId, trans);
			logger.debug("Done logging application received event.");
		} else {
			logger.debug("NOT logging application received event. appAmended="
					+ app.isApplicationAmended() + ", isLegacy="
					+ app.isLegacy() + ", isResubmit=" + isResubmit);
		}
		validationMessages.add(new ValidationMessage("applicationReceivedDate",
				"Application submission successful",
				ValidationMessage.Severity.INFO));

		return validationMessages;
	}

	private Permit createPermitForApplication(Application app, int userId,
			String permitNumber, Transaction trans) throws DAOException {

		Permit permit = null;
		boolean titleV = false;
		String type = app.getApplicationTypeCD();
		Facility fp = app.getFacility();
		PermitService pBO = null;

		if (type.equalsIgnoreCase(ApplicationTypeDef.TITLE_V_APPLICATION)) {
			TVPermit tvp = new TVPermit();
			tvp.setUsepaOutcomeCd(USEPAOutcomeDef.NO_RESPONSE);
			permit = tvp;
			if (permitNumber != null) {
				permit.setPermitNumber(permitNumber);
			}
			//permit.setPermitReasonCDs(findPTOPermitReasons(app));
			
			logger.debug("Initializing permit reason to Not yet assigned");
			List<String> rs = new ArrayList<String>();
			rs.add(PermitReasonsDef. NOT_ASSIGNED);
			permit.setPermitReasonCDs(rs);
		/*
		} else if (type
				.equalsIgnoreCase(ApplicationTypeDef.TITLE_IV_APPLICATION)) {
			TIVPermit p = new TIVPermit();
			p.setUsepaOutcomeCd(USEPAOutcomeDef.NO_RESPONSE);
			permit = p;
			if (permitNumber != null) {
				permit.setPermitNumber(permitNumber);
			}
			// When the permit detail is created it should populate the permit
			// reasons with the app reasons similar to Title V.
			permit.setPermitReasonCDs(findPTOPermitReasons(app));
		*/
		} else if (type.equalsIgnoreCase(ApplicationTypeDef.PTIO_APPLICATION)) {
			PTIOPermit pp = new PTIOPermit();
			FacilityDAO fd = facilityDAO(trans);
			//fp = fd.retrieveFacility(fp.getFpId());
			//if (fp.getPermitClassCd() != null
			//		&& fp.getPermitClassCd().equalsIgnoreCase("TV")) {
			//	titleV = true;
			//}
			pp.setTmpPERDueDateCD(((PTIOApplication) app)
					.getRequestedPERDueDateCD());
			permit = pp;
			if (permitNumber != null) {
				permit.setPermitNumber(permitNumber);
			}

			//logger.debug(" Finding and setting permit reasons");
			//permit.setPermitReasonCDs(findPTIOPermitReasons(app, titleV));
			
			logger.debug("Initializing permit reason to Not yet assigned");
			List<String> rs = new ArrayList<String>();
			rs.add(PermitReasonsDef. NOT_ASSIGNED);
			permit.setPermitReasonCDs(rs);
			
			additionalPopulate(permit, app);
		} /*
		 * Remove PBR references in permit applications else if
		 * (type.equalsIgnoreCase(ApplicationTypeDef.PBR_NOTIFICATION)) { permit
		 * = new PBRPermit();
		 * permit.setPermitNumber(app.getApplicationNumber()); }
		 */else if (type.equalsIgnoreCase(ApplicationTypeDef.RPR_REQUEST)) {
			permit = new RPRPermit();
			permit.setPermitNumber(app.getApplicationNumber());
		/*
		} else if (type.equalsIgnoreCase(ApplicationTypeDef.RPE_REQUEST)) {
			permit = new RPEPermit();
			permit.setPermitNumber(app.getApplicationNumber());
		} else if (type.equalsIgnoreCase(ApplicationTypeDef.RPC_REQUEST)) {
			RPCRequest rpc = (RPCRequest) app;
			PermitDAO permitDAO = permitDAO();
			Permit oldPermit = permitDAO.retrievePermit(rpc.getPermitId());
			if (oldPermit == null) {
				throw new DAOException("No permit found with id "
						+ rpc.getPermitId());
			}
			if (PermitTypeDef.TV_PTO.equals(oldPermit.getPermitType())) {
				TVPermit tvp = new TVPermit();
				tvp.setUsepaOutcomeCd(USEPAOutcomeDef.NO_RESPONSE);
				permit = tvp;
				try {
					pBO = ServiceFactory.getInstance().getPermitService();
					permit.setPermitNumber(pBO.generatePermitNumber());
				} catch (Exception e) {
					throw new DAOException("Could not generate permit number. "
							+ e.getMessage(), e);
				}
				permit.setPermitReasonCDs(findPTOPermitReasons(app));
			} else if (oldPermit instanceof PTIOPermit) {
				PTIOPermit pp = new PTIOPermit();
				FacilityDAO fd = facilityDAO();
				fp = fd.retrieveFacility(fp.getFacilityId());
				//
				// 2581 if
				// (PermitTypeDef.TVPTI.equals(oldPermit.getPermitType())) {
				// titleV = true; }
				//
				if (PermitClassDef.TV.equals(fp.getPermitClassCd())) {
					titleV = true;
				}
				pp.setTv(titleV);
				pp.setTmpPERDueDateCD(((PTIOPermit) oldPermit)
						.getTmpPERDueDateCD());
				permit = pp;
				try {
					pBO = ServiceFactory.getInstance().getPermitService();
					permit.setPermitNumber(pBO.generatePermitNumber());
				} catch (Exception e) {
					throw new DAOException("Could not generate permit number. "
							+ e.getMessage(), e);
				}

				permit.setPermitReasonCDs(findPTIOPermitReasons(app, titleV));
			}
		*/
		}

		if (permit != null) {
			permit.addNewApplication(app);
			permit.setFacilityId(fp.getFacilityId());

			try {
				pBO = ServiceFactory.getInstance().getPermitService();

				//if (permit instanceof RPEPermit) {
				//	setupRPEEUs(pBO, app, permit);
				//} else 
					if (permit instanceof PTIOPermit
						&& app instanceof PTIOApplication) {
					logger.debug("Setting up NSR EUs");
					setupPTIOEUs((PTIOApplication) app, (PTIOPermit) permit);
				}

				logger.debug("Creating the permit object");
				pBO.createPermit(permit, userId, trans);
				// add in error note--only created if TV Revision had trouble
				// finding 0 or more than 1 previous TV permits.
				if (permit.getNoteErrMsg() != null) {
					permit.getNoteErrMsg().setPermitId(permit.getPermitID());
					pBO.createPermitNote(permit.getNoteErrMsg(), trans);
				}
			} catch (Exception e) {
				// need to remove directory created for permit documents
				if (pBO != null) {
					try {
						pBO.removePermitDir(permit);
						permit = null;
					} catch (Exception e1) {
						logger.error(
								"Exception while attempting to delete permit directory for permit "
										+ permitNumber + " and application "
										+ app.getApplicationNumber(), e1);
					}
				}
				logger.error(
						"Exception creating permit workflow for permit "
								+ permitNumber + " and application "
								+ app.getApplicationNumber(), e);
				throw new DAOException("Could not create permit. "
						+ e.getMessage(), e);
			}
		}
		return permit;
	}

	private Integer createPermitWorkflow(Application app, Permit permit,
			Integer userId, Transaction trans) throws RemoteException {
		String type = app.getApplicationTypeCD();
		Facility fp = app.getFacility();

		ReadWorkFlowService wfBO = null;
		try {
			wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
		} catch (ServiceFactoryException e) {
			throw new RemoteException(
					"Exception getting workflow service for permit "
							+ permit.getPermitNumber() + " and application "
							+ app.getApplicationNumber(), e);
		}
		Integer workflowId = null;
		String ptName = null;
		if (type.equalsIgnoreCase(ApplicationTypeDef.TITLE_V_APPLICATION)
				//|| type.equalsIgnoreCase(ApplicationTypeDef.TITLE_IV_APPLICATION)
				|| type.equalsIgnoreCase(ApplicationTypeDef.PTIO_APPLICATION)
				|| type.equalsIgnoreCase(ApplicationTypeDef.RPC_REQUEST)) {
			ptName = WorkFlowProcess.PERMIT_WORKFLOW_NAME;
		} else if (type.equalsIgnoreCase(ApplicationTypeDef.RPR_REQUEST)) {
			ptName = WorkFlowProcess.REVOCATION;
		} else if (type.equalsIgnoreCase(ApplicationTypeDef.RPE_REQUEST)) {
			ptName = WorkFlowProcess.RPE;
		} else {
			ptName = type; // only if the app typeCd is same as process
		}
		// name
		workflowId = wfBO.retrieveWorkflowTempIdAndNm(trans).get(ptName);
		Timestamp dueDt = null;
		String rush = "N";

		WorkFlowManager wfm = new WorkFlowManager();
		
		HashMap<String,String> data = new HashMap<String,String>();
		data.put(WorkFlowManager.ROLE_DISCRIMINATOR, permit.getPermitType());
		
		//DONE 2475
		// pass in the permit type cd here (NSR or TV), using the data arg
		logger.debug("Submitting workflow process");
		WorkFlowResponse resp = wfm.submitProcess(workflowId,
				permit.getPermitID(), fp.getFpId(), userId, rush,
				app.getReceivedDate(), dueDt, data, trans);

		if (resp.hasError() || resp.hasFailed()) {
			String[] errorMsgs = resp.getErrorMessages();
			String[] recomMsgs = resp.getRecommendationMessages();
			String error = "Workflow response problem: ";
			for (String msg : errorMsgs) {
				error += msg + " ";
			}
			for (String msg : recomMsgs) {
				error += msg + " ";
			}
			logger.error("Error while creating permit workflow and application "
					+ app.getApplicationNumber() + ". " + error);
			throw new DAOException(error);
		}
		return workflowId;
	}
/*
	private void setupRPEEUs(PermitService pBO, Application app, Permit permit)
			throws DAOException, RemoteException {
		Permit orgPermit = pBO.retrievePermit(((RPERequest) app).getPermitId())
				.getPermit();

		PermitEU rpePermitEU = null;
		PermitEU orgPermitEU = null;

		for (PermitEUGroup peug : orgPermit.getEuGroups()) {
			for (PermitEU pe : peug.getPermitEUs()) {
				orgPermitEU = pe;
				break;
			}
			if (orgPermitEU != null)
				break;
		}

		for (PermitEUGroup peug : permit.getEuGroups()) {
			for (PermitEU pe : peug.getPermitEUs()) {
				rpePermitEU = pe;
				break;
			}
			if (rpePermitEU != null)
				break;
		}

		if (rpePermitEU == null && orgPermitEU != null) {
			rpePermitEU = new PermitEU();
			rpePermitEU.setPermitStatusCd(PermitStatusDef.NONE);
			rpePermitEU.setFpEU(orgPermitEU.getFpEU());
			permit.getIndividualEuGroup().addPermitEU(rpePermitEU);
		}

		if (rpePermitEU != null && orgPermitEU != null) {
			EUFee ef = rpePermitEU.getEuFee();
			EUFee oef = orgPermitEU.getEuFee();
			ef.setFeeCategoryId(oef.getFeeCategoryId());
			ef.setFee(oef.getFee());
			ef.setAdjustmentCd("O");

			Double fee = new Double(
					((PTIOPermit) orgPermit).getTotalAmount() * 0.5);
			ef.setAdjustedAmount(fee);
		}

	}
*/
	private void setupPTIOEUs(PTIOApplication app, PTIOPermit permit)
			throws DAOException, RemoteException {
		for (PermitEUGroup peug : permit.getEuGroups()) {
			for (PermitEU pe : peug.getPermitEUs()) {
				for (ApplicationEU appEU : app.getIncludedEus()) {
					if (appEU instanceof PTIOApplicationEU
							&& appEU.getFpEU().getCorrEpaEmuId()
									.equals(pe.getFpEU().getCorrEpaEmuId())) {
						PTIOApplicationEU ptioEU = (PTIOApplicationEU) appEU;
						pe.setGeneralPermitTypeCd(ptioEU
								.getGeneralPermitTypeCd());
						pe.setModelGeneralPermitCd(ptioEU
								.getModelGeneralPermitCd());
						break;
					}
				}
			}
		}

	}

	private List<String> findPTOPermitReasons(Application app) {
		List<String> rs = new ArrayList<String>();
		if (app instanceof TVApplication) {
			TVApplication ptoApp = (TVApplication) app;
			String pps = ptoApp.getTvApplicationPurposeCd();
			if (pps != null) {
				if (pps.contains(TVApplicationPurposeDef.INITIAL_APPLICATION))
					rs.add(PermitReasonsDef.INITIAL);
				else if (pps.contains(TVApplicationPurposeDef.RENEWAL))
					rs.add(PermitReasonsDef.RENEWAL);
				else if (pps
						.contains(TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING)) {
					// 2599
					//for (String r : ptoApp.getPermitReasonCds())
					//	rs.add(r);
					rs.add(ptoApp.getPermitReasonCd());
				}
			}
		} else if (app instanceof TIVApplication) {
			TIVApplication ptoApp = (TIVApplication) app;
			String pps = ptoApp.getAppPurposeCd();
			if (pps != null) {
				if (pps.contains(TVApplicationPurposeDef.INITIAL_APPLICATION))
					rs.add(PermitReasonsDef.INITIAL);
				else if (pps.contains(TVApplicationPurposeDef.RENEWAL))
					rs.add(PermitReasonsDef.RENEWAL);
				else if (pps
						.contains(TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING))
					rs.add(ptoApp.getReasonCd());
			}
		// IMPACT currently doesn't support RPCRequests
		//} else if (app instanceof RPCRequest) {
		//	RPCRequest request = (RPCRequest) app;
		//	String rpcTypeCd = request.getRpcTypeCd();
		//	if (RPCTypeDef.TV_ADMIN_PERMIT_AMENDMENT.equals(rpcTypeCd)) {
		//		rs.add(PermitReasonsDef.APA);
		//	} else if (RPCTypeDef.TV_OFF_PERMIT_CHANGE.equals(rpcTypeCd)) {
		//		rs.add(PermitReasonsDef.OFF_PERMIT_CHANGE);
		//	}
		}
		return rs;
	}

	private void additionalPopulate(Permit permit, Application app) {
		PTIOApplication ptioApp = (PTIOApplication) app;
		PTIOPermit ptioPermit = (PTIOPermit) permit;
		// If the applicant has selected General Permit then, in addition to
		// determining the permit reason to be Installation, Modification, or
		// Renewal as appropriate and based on the rules above, the permit
		// object should have general permit selected
		List<String> apprs = app.getApplicationPurposeCDs();
		/*
		 * General Permit not valid for impact if
		 * (apprs.contains(PTIOApplicationPurposeDef.GENERAL_PERMIT))
		 * ptioPermit.setGeneralPermit(true);
		 */

		// If the applicant has selected Request federally enforced
		// restrictions, then the permit object should have the 'Issue Draft?'
		// check box selected.
		if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getNeshapApplicableFlag()))
			ptioPermit.setIssueDraft(true);
		else if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getNspsApplicableFlag()))
			ptioPermit.setIssueDraft(true);
		else if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getMactApplicableFlag()))
			ptioPermit.setIssueDraft(true);
		else if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getPsdApplicableFlag()))
			ptioPermit.setIssueDraft(true);
		else if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getGhgApplicableFlag()))
			ptioPermit.setIssueDraft(true);
		else if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getNsrApplicableFlag()))
			ptioPermit.setIssueDraft(true);
		else if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getRiskManagementPlanFlag()))
			ptioPermit.setIssueDraft(true);
		else if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getTitleIVFlag()))
			ptioPermit.setIssueDraft(true);
		else if (!PTIOFedRuleAppl1Def.NOT_AFFECTED.equalsIgnoreCase(ptioApp
				.getMactApplicableFlag()))
			ptioPermit.setIssueDraft(true);
		
		// Check 'Public Notice Needed?' by default. If the user later 
		// marks this permit as waiver then 'Public Notice Needed?' is
		// unselected automatically
		ptioPermit.setIssueDraft(true);
	}

	private List<String> findPTIOPermitReasons(Application app, boolean titleV) {
		List<String> rs = new ArrayList<String>();

		if (app instanceof PTIOApplication) {
			List<String> apprs = app.getApplicationPurposeCDs();

			// If the applicant has selected Installation by itself or in
			// combination with any other option, then the permit reason should
			// be
			// installation.
			if (apprs.contains(PTIOApplicationPurposeDef.CONSTRUCTION))
				rs.add(PermitReasonsDef.CONSTRUCTION);

			// If the applicant has selected Modification by itself or in
			// combination with any other option other than Installation, then
			// the
			// permit reason should be Chapter 31 Modification.
			else if (apprs.contains(PTIOApplicationPurposeDef.MODIFICATION))
				rs.add(PermitReasonsDef.MODIFICATION);

			// If the applicant has selected Renewal by itself or in combination
			// with any other option other than Installation or Modification,
			// then
			// the permit reason should be Renewal.
			else if (apprs.contains(PTIOApplicationPurposeDef.RECONSTRUCTION))
				if (titleV)
					/*
					 * If a PTI/PTIO Application is submitted for a Title V
					 * facility, the system creates a PTI object (so far this is
					 * what it already does) then it could recognize that a PTI
					 * renewal is not an option and instead assign the reason as
					 * "initial installation". That way our statistics will
					 * count that permit and will show up on the radar sooner
					 * than if no reason was assigned at all.
					 */
					rs.add(PermitReasonsDef.RECONSTRUCTION);
				else
					rs.add(PermitReasonsDef.RECONSTRUCTION);
		// IMPACT currently doesn't support RCPRequests
		//} else if (app instanceof RPCRequest) {
		//	String rpcTypeCd = ((RPCRequest) app).getRpcTypeCd();
		//	if (RPCTypeDef.PTIO_ADMIN_MOD.equals(rpcTypeCd)) {
		//		rs.add(PermitReasonsDef.ADMIN_MOD);
		//	}
		}

		return rs;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeApplicationEU(ApplicationEU appEU) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			removeApplicationEU(appEU, true, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	public void removeApplicationEU(ApplicationEU appEU, 
			boolean removeDocumentReference) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			removeApplicationEU(appEU, removeDocumentReference, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	private void removeApplicationEU(ApplicationEU appEU, 
			boolean removeDocumentReference, Transaction trans)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		DocumentDAO documentDAO = documentDAO(trans);

		// Now remove associations...
		appDAO.markTempApplicationEUDocuments(appEU.getApplicationEuId());
		appDAO.removeApplicationEUDocuments(appEU.getApplicationEuId());
		// cleanup document in staging
		if (isPortalApp() && removeDocumentReference) {
			for (ApplicationDocumentRef euDocumentRef : appEU.getEuDocuments()) {
				if (null != euDocumentRef.getDocumentId()) {
					documentDAO.removeDocumentReference(euDocumentRef
							.getDocumentId());
				}
				if(null != euDocumentRef.getTradeSecretDocId()) {
					documentDAO.removeDocumentReference(euDocumentRef.getTradeSecretDocId());
				}
			}
		}

		// Remove derived class
		Class<? extends ApplicationEU> incomingAppEuClass = appEU.getClass();
		if (incomingAppEuClass.equals(PTIOApplicationEU.class)) {
			ApplicationEUTypeDAO euTypeDAO = getApplicationEUTypeDAO(appEU
					.getFpEU().getEmissionUnitTypeCd(), trans, true);

			appDAO.removeApplicationEUEmissions(appEU.getApplicationEuId());
			appDAO.removePTIOEUPurposeCds(appEU.getApplicationEuId());
			appDAO.removePTIOEUFedLimitReasonCds(appEU.getApplicationEuId());
			appDAO.removePTIOAppMACTSubpartCds(appEU.getApplicationEuId());
			appDAO.removePTIOAppNESHAPSubpartCds(appEU.getApplicationEuId());
			appDAO.removePTIOAppNSPSSubpartCds(appEU.getApplicationEuId());
			appDAO.removeBACTEmissions(appEU.getApplicationEuId());
			appDAO.removeLAEREmissions(appEU.getApplicationEuId());
			appDAO.removeApplicationEUMaterialUsed(appEU.getApplicationEuId());
			appDAO.removeApplicationEUFugitiveLeaks(appEU.getApplicationEuId());

			if (euTypeDAO != null) {
				euTypeDAO.removeApplicationEUType(appEU.getApplicationEuId());
			}

			appDAO.removePTIOApplicationEU(appEU.getApplicationEuId());
		} else if (incomingAppEuClass.equals(TVApplicationEU.class)) {
			appDAO.removeTVAltScenarioPteReqsForEu(appEU.getApplicationEuId());
			appDAO.removeTVApplicationEUEmissions(appEU.getApplicationEuId());
			appDAO.removeTVEUOperatingScenarios(appEU.getApplicationEuId());
			appDAO.removeTvEuAppMACTSubpartCds(appEU.getApplicationEuId());
			appDAO.removeTvEuAppNESHAPSubpartCds(appEU.getApplicationEuId());
			appDAO.removeTvEuAppNSPSSubpartCds(appEU.getApplicationEuId());
			removeTVApplicableReqsForEU(appEU.getApplicationEuId(), appDAO);
			appDAO.removeTVApplicationEU(appEU.getApplicationEuId());
		}

		// Remove base class
		appDAO.removeApplicationEU(appEU.getApplicationEuId());
	}

	private void removeApplicationEU(Integer applicationEuId, Application app,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		// Now remove associations...
		appDAO.markTempApplicationEUDocuments(applicationEuId);
		appDAO.removeApplicationEUDocuments(applicationEuId);

		// Remove derived class
		if (app instanceof PTIOApplication) {
			appDAO.removeApplicationEUEmissions(applicationEuId);
			appDAO.removePTIOEUPurposeCds(applicationEuId);
			appDAO.removePTIOEUFedLimitReasonCds(applicationEuId);
			appDAO.removePTIOApplicationEU(applicationEuId);
			appDAO.removeApplicationEUMaterialUsed(applicationEuId);
			appDAO.removeApplicationEUFugitiveLeaks(applicationEuId);
		} else if (app instanceof TVApplication) {
			appDAO.removeTVAltScenarioPteReqsForEu(applicationEuId);
			appDAO.removeTVApplicationEUEmissions(applicationEuId);
			appDAO.removeTVEUOperatingScenarios(applicationEuId);
			removeTVApplicableReqsForEU(applicationEuId, appDAO);
			appDAO.removeTVApplicationEU(applicationEuId);
		}

		// Remove base class
		appDAO.removeApplicationEU(applicationEuId);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeApplication(Application app) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			removeApplication(app, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeApplication(Application app, Transaction trans)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		int appId = app.getApplicationID();

		// make sure EUs deleted from the facility inventory are no longer part of
		// the application
		deleteOrphanEUs(app, trans);

		if (app.getClass().equals(TVApplication.class)) {
			for (TVEUGroup group : ((TVApplication) app).getEuGroups()) {
				appDAO.removeTvEusFromGroup(group.getTvEuGroupId());
				removeTVApplicableReqsForEUGroup(group.getTvEuGroupId(), trans);
				appDAO.removeTvEuGroup(group.getTvEuGroupId());
				appDAO.removeTVApplicationReasonCds(appId);
			}
		}

		// Remove EU's
		for (ApplicationEU appEU : app.getEus()) {
			removeApplicationEU(appEU, true, trans);
		}

		// remove documents
		if (app instanceof PBRNotification) {
			appDAO.markTempPBRNotificationDocuments(appId);
			appDAO.removePBRNotificationDocuments(appId);
		// IMPACT currently doesn't support RPCRequests
		//} else if (app instanceof RPCRequest) {
		//	appDAO.markTempRPCRequestDocuments(appId);
		//	appDAO.removeRPCRequestDocuments(appId);
		} else {
			appDAO.markTempApplicationDocuments(appId);
			appDAO.removeApplicationDocuments(appId);
		}
		
		// cleanup document in staging
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			DocumentDAO documentDAO = documentDAO(trans);
			for(ApplicationDocumentRef appDocRef : app.getDocuments()) {
				if(null != appDocRef.getDocumentId()) {
					documentDAO.removeDocumentReference(appDocRef.getDocumentId());
				}
				if(null != appDocRef.getTradeSecretDocId()) {
					documentDAO.removeDocumentReference(appDocRef.getTradeSecretDocId());
				}
			}
		}

		// remove notes (internal app only)
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			appDAO.removeApplicationNotes(appId);
			for (ApplicationNote note : app.getApplicationNotes()) {
				infraDAO.removeNote(note.getNoteId());
			}
		}

		// Do type specfic removes
		if (app.getClass().equals(PTIOApplication.class)) {
			appDAO.removePTIOApplicationPurposeCds(appId);
			// appDAO.removePTIOApplicationSageGrouseCds(appId);
			appDAO.removePTIOApplication(appId);
		} else if (app.getClass().equals(TVApplication.class)) {
			appDAO.removeTVPteAdjustmentForApplication(appId);
			removeTVApplicableReqsForApp(appId, trans);
			appDAO.removeTVApplicationReasonCds(appId);
			removeTVFacilityWideRequirements(((TVApplication) app), trans);
			appDAO.removeTVApplication(appId);
		// IMPACT currently doesn't support RPCRequests
		//} else if (app.getClass().equals(RPCRequest.class)) {
		//	appDAO.removeRPCRequest(appId);
		} else if (app.getClass().equals(RPERequest.class)) {
			appDAO.removeRPERequest(appId);
		} else if (app.getClass().equals(RPRRequest.class)) {
			appDAO.removeRPRRequest(appId);
		} else if (app.getClass().equals(PBRNotification.class)) {
			appDAO.removePBRNotification(appId);
		}

		// Remove base application
		appDAO.removeApplication(app.getApplicationID());

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String generateApplicationNumber(Application app)
			throws DAOException {
		String result = null;
		if (app != null) {
			ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
			result = appDAO.generateApplicationNumber(app.getClass());
		}

		return result;
	}

	private String generateApplicationNumber(Application app, Transaction trans)
			throws DAOException {
		String result = null;
		if (app != null) {
			ApplicationDAO appDAO = applicationDAO(trans);
			result = appDAO.generateApplicationNumber(app.getClass());
		}
		return result;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public TVEUGroup createTvEuGroup(TVEUGroup group) throws DAOException {
		Transaction trans = null;
		TVEUGroup ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			ret = appDAO.createTvEuGroup(group);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	private TVEUGroup createTvEuGroup(TVEUGroup group, Transaction trans)
			throws DAOException {
		TVEUGroup ret = null;

		ApplicationDAO appDAO = applicationDAO(trans);
		ret = appDAO.createTvEuGroup(group);
		for (TVApplicationEU eu : group.getEus()) {
			appDAO.addTvEuToGroup(eu.getApplicationEuId(),
					group.getTvEuGroupId());
		}
		for (TVApplicableReq req : group.getApplicableRequirements()) {
			req.setTvEuGroupId(group.getTvEuGroupId());
			req = appDAO.addTVApplicableReq(req);
			addTVApplicableReqComponents(req, appDAO);
		}
		for (TVApplicableReq req : group.getStateOnlyRequirements()) {
			req.setTvEuGroupId(group.getTvEuGroupId());
			req = appDAO.addTVApplicableReq(req);
			addTVApplicableReqComponents(req, appDAO);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public TVEUGroup retrieveTvEuGroup(Integer groupId) throws DAOException {
		TVEUGroup result = null;
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		result = appDAO.retrieveTvEuGroup(groupId);
		return result;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public List<TVEUGroup> retrieveTvEuGroups(Integer appId)
			throws DAOException {
		ArrayList<TVEUGroup> result = new ArrayList<TVEUGroup>();
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		for (TVEUGroup group : appDAO.retrieveTvEuGroupsForApplication(appId)) {
			result.add(group);
		}
		return result;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyTvEuGroup(TVEUGroup group) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			modifyTvEuGroup(group, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	private void modifyTvEuGroup(TVEUGroup group, Transaction trans)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		appDAO.modifyTvEuGroup(group);

		// clear list of Eus, then add entries for updated list
		appDAO.removeTvEusFromGroup(group.getTvEuGroupId());
		for (TVApplicationEU eu : group.getEus()) {
			appDAO.addTvEuToGroup(eu.getApplicationEuId(),
					group.getTvEuGroupId());
		}

		// clear applicable requirements, then add entries for updated lists
		removeTVApplicableReqsForEUGroup(group.getTvEuGroupId(), trans);
		for (TVApplicableReq req : group.getApplicableRequirements()) {
			req.setTvEuGroupId(group.getTvEuGroupId());
			req = appDAO.addTVApplicableReq(req);
			addTVApplicableReqComponents(req, appDAO);
		}
		for (TVApplicableReq req : group.getStateOnlyRequirements()) {
			req.setTvEuGroupId(group.getTvEuGroupId());
			req = appDAO.addTVApplicableReq(req);
			addTVApplicableReqComponents(req, appDAO);
		}
	}

	/**
	 * @param app
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ApplicationDocumentRef uploadApplicationDocument(Application app,
			ApplicationDocumentRef doc, UploadedFileInfo publicFile,
			UploadedFileInfo tsFile, Integer userId) throws DAOException {
		Transaction trans = null;
		ApplicationDocument publicDoc = null;
		ApplicationDocument tsDoc = null;
		try {
			trans = TransactionFactory.createTransaction();
			if (publicFile != null) {
				try {
					// upload public document and create record in dc_document
					publicDoc = uploadApplicationDocument(app, publicFile,
							doc.getDescription(), userId, trans);
					doc.setDocumentId(publicDoc.getDocumentID());
				} catch (IOException e) {
					logger.error(
							"Exception while uploading public application document for "
									+ app.getApplicationNumber(), e);
					throw new DAOException(
							"Exception while uploading public application document for "
									+ app.getApplicationNumber(), e);
				}
			}
			if (tsFile != null) {
				try {
					// upload trade secret document and create record in
					// dc_document
					tsDoc = uploadApplicationDocument(app, tsFile,
							doc.getDescription(), userId, trans);
					if (tsDoc != null) {
						doc.setTradeSecretDocId(tsDoc.getDocumentID());
					}
				} catch (IOException e) {
					logger.error(
							"Exception while uploading trade secret public application document for "
									+ app.getApplicationNumber(), e);
					throw new DAOException(
							"Exception while uploading trade secret application document for "
									+ app.getApplicationNumber(), e);
				}
			}
			// create record in pa_application_document table
			ApplicationDAO applicationDAO = applicationDAO(trans);
			doc = applicationDAO.createApplicationDocument(doc);

			trans.complete();
		} catch (DAOException de) {
			if (publicDoc != null && publicDoc.getPath() != null) {
				try {
					DocumentUtil.removeDocument(publicDoc.getPath());
				} catch (IOException e) {
					logger.warn("Exception deleting document "
							+ publicDoc.getPath());
				}
			}
			if (tsDoc != null && tsDoc.getPath() != null) {
				try {
					DocumentUtil.removeDocument(tsDoc.getPath());
				} catch (IOException e) {
					logger.warn("Exception deleting document "
							+ tsDoc.getPath());
				}
			}
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return doc;
	}

	/**
	 * 
	 * @param app
	 * @param file
	 * @param description
	 * @param userId
	 * @param trans
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ApplicationDocument uploadApplicationDocument(Application app,
			UploadedFileInfo file, String description, int userId)
			throws DAOException {
		ApplicationDocument appDoc = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			appDoc = uploadApplicationDocument(app, file, description, userId,
					trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return appDoc;
	}

	private ApplicationDocument uploadApplicationDocument(Application app,
			UploadedFileInfo file, String description, int userId,
			Transaction trans) throws DAOException {
		ApplicationDocument appDoc = null;
		if (file != null) {
			appDoc = new ApplicationDocument();
			appDoc.setFacilityID(app.getFacilityId());
			appDoc.setApplicationId(app.getApplicationID());
			appDoc.setLastModifiedBy(userId);
			appDoc.setDescription(description);
			try {
				// upload trade secret document and create record in dc_document
				appDoc = (ApplicationDocument) uploadDocument(appDoc,
						file.getFileName(), file.getInputStream(), trans);
			} catch (IOException e) {
				logger.error(
						"Exception while uploading trade secret public application document for "
								+ app.getApplicationNumber(), e);
				throw new DAOException(
						"Exception while uploading trade secret application document for "
								+ app.getApplicationNumber(), e);
			}
		}
		return appDoc;
	}

	/**
	 * 
	 * @param attachment
	 * @param application
	 * @return
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Attachment uploadApplicationAttachment(Attachments attachment,
			Application application) throws RemoteException {
		Attachment doc = attachment.getTempDoc();
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			try {
				uploadDocument(doc, attachment.getFileToUpload().getFilename(),
						attachment.getFileToUpload().getInputStream(), trans);
			} catch (IOException e) {
				throw new DAOException(
						"Exception uploading application attachment for "
								+ application.getApplicationNumber(), e);
			}
			if (application instanceof PBRNotification) {
				appDAO.createPBRNotificationDocument(new PBRNotificationDocument(
						doc));
			// IMPACT currently doesn't support RPCRequests
			//} else if (application instanceof RPCRequest) {
			//	appDAO.createRPCRequestDocument(new RPCRequestDocument(doc));
			} else {
				// this should never happen
				logger.error("Unexpected call to createAttachment from application of type "
						+ application.getClass().getName());
				doc = null;
			}

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
			throw de;
		} finally {
			closeTransaction(trans);
		}
		return doc;
	}

	/**
	 * 
	 * @param doc
	 * @param filename
	 * @param is
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Document uploadDocument(Document doc, String filename, InputStream is)
			throws DAOException {
		Transaction trans = null;
		Document result = null;

		try {
			trans = TransactionFactory.createTransaction();
			result = uploadDocument(doc, filename, is, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return result;
	}

	private Document uploadDocument(Document doc, String filename,
			InputStream is, Transaction trans) throws DAOException {
		if (is == null) {
			throw new DAOException("InputStream for uploaded document "
					+ filename + " is null");
		}
		DocumentDAO docDAO = documentDAO(trans);

		// Set the path elements of the temp doc.
		doc.setExtension(DocumentUtil.getFileExtension(filename));

		doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
		doc.setUploadDate(doc.getLastModifiedTS());

		// add document info to database
		// NOTE: This needs to be done before file is created in file store
		// since document id obtained from createDocument method is used as
		// the file name for the file store file
		docDAO.createDocument(doc);

		// copy document to file store
		try {
			DocumentUtil.createDocument(doc.getPath(), is);
		} catch (IOException e) {
			logger.error(
					"Exception creating document in file store: "
							+ doc.getPath(), e);
			throw new DAOException(
					"Exception creating document in file store: "
							+ doc.getPath(), e);
		}

		return doc;
	}

	/**
	 * Load Document objects into docRef to avoid multiple queries for this
	 * information.
	 * 
	 * @param docRef
	 * @param readOnly
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public void loadDocuments(ApplicationDocumentRef docRef, boolean readOnly)
			throws DAOException {
		DocumentDAO docDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		if (docRef != null) {
			if (docRef.getDocumentId() != null) {
				ApplicationDocument appDoc = new ApplicationDocument(
						docDAO.retrieveDocument(docRef.getDocumentId()));
				appDoc.setApplicationId(docRef.getApplicationId());
				appDoc.setTemporary(false); // make sure temporary is set to
											// false
				docRef.setPublicDoc(appDoc);
				logger.debug("Loaded application document with id: "
						+ appDoc.getDocumentID() + " last modified date = "
						+ appDoc.getLastModifiedTS());
			}
			if (docRef.getTradeSecretDocId() != null) {
				ApplicationDocument appDoc = new ApplicationDocument(
						docDAO.retrieveDocument(docRef.getTradeSecretDocId()));
				appDoc.setApplicationId(docRef.getApplicationId());
				appDoc.setTemporary(false); // make sure temporary is set to
											// false
				docRef.setTradeSecretDoc(appDoc);
			}
		}
	}

	/**
	 * Load Document objects into all ApplicationDocumentRef objects associated
	 * with application.
	 * 
	 * @param application
	 * @param readOnly
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public void loadAllDocuments(Application application, boolean readOnly)
			throws DAOException {
		logger.debug("Enter loadAllDocuments()" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
		for (ApplicationDocumentRef docRef : application.getDocuments()) {
			loadDocuments(docRef, readOnly);
		}
		
		for (ApplicationEU appEU : application.getEus()) {
			for (ApplicationDocumentRef docRef : appEU.getEuDocuments()) {
				loadDocuments(docRef, readOnly);
			}
		}
		logger.debug("Exit loadAllDocuments()" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
	}

	/**
	 * Get list of documents associated with the application.
	 * 
	 * @param app
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<Document> getPrintableDocumentList(Application app,
			Document facilityDocument, boolean readOnly, boolean hideTradeSecret, boolean includeAllAttachments)
			throws RemoteException {
		List<Document> docList = new ArrayList<Document>();
		Document doc = null;

		// use previously generated PDF file if application has been submitted
		if (app.getSubmittedDate() != null && !isPublicApp()) { // dennis marked this comment
			// get submitted pdf document
			doc = this.getApplicationReportDocument(app, hideTradeSecret);
			try {
				if (doc != null && DocumentUtil.canRead(doc.getPath())) {
					// use existing doc if already generated
					docList.add(doc);
				} else {
					// generate new doc if it does not already exist
					String userName = null;
					SubmissionLog log = getSubmissionLogForApplication(app);
					if (log != null) {
						userName = log.getGatewayUserName();
					}
					ApplicationDocument appDoc = createApplicationReportDocument(
							app, userName, hideTradeSecret, true, includeAllAttachments);
					if (appDoc != null
							&& DocumentUtil.canRead(appDoc.getPath())) {
						docList.add(appDoc);
					}
				}
			} catch (IOException e) {
				logger.error(
						"Exception checking on submitted PDF document for "
								+ app.getApplicationNumber(), e);
			}
		}

		// get public form
		TmpDocument appDoc = new TmpDocument();
		boolean hasTS = generateTempApplicationReport(app, hideTradeSecret,
				appDoc, false, includeAllAttachments);
		app.setContainsTS(hasTS);
		docList.add(appDoc);

		if (facilityDocument != null && !isPublicApp()) {
			docList.add(facilityDocument);
		}

		try {
			Document zipDoc = generateTempApplicationZipFile(app,
					facilityDocument, hideTradeSecret, includeAllAttachments, readOnly);
			if (zipDoc != null) {
				docList.add(zipDoc);
			}
		} catch (IOException e) {
			logger.error("Exception generating zip document for application "
					+ app.getApplicationNumber(), e);
		}

		return docList;
	}

	/**
	 * Get list of documents associated with the application.
	 * 
	 * @param app
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<Document> getPrintableAttachmentList(Application app,
			boolean readOnly, boolean hideTradeSecret, boolean includeAllAttachments) throws RemoteException {
		List<Document> docList = new ArrayList<Document>();
		Document doc = null;
		DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));

		if (app instanceof PBRNotification) {
			for (PBRNotificationDocument pbrDoc : ((PBRNotification) app)
					.getPbrDocuments()) {
				docList.add(pbrDoc);
			}
		// IMPACT currently doesn't support RPCRequests
		//} else if (app instanceof RPCRequest) {
		//	for (RPCRequestDocument rpcDoc : ((RPCRequest) app)
		//			.getRpcDocuments()) {
		//		docList.add(rpcDoc);
		//	}
		} else {
			// get application-wide attachments
			for (ApplicationDocumentRef appDoc : app.getDocuments()) {
				// don't include documents added after application was submitted
				if (app.getSubmittedDate() != null
						&& appDoc.getLastModifiedTS() != null
						&& appDoc.getLastModifiedTS().after(
								app.getSubmittedDate())) {
					logger.debug("Excluding document "
							+ appDoc.getApplicationDocId()
							+ " from application printable attachment list. Document last modified date ("
							+ appDoc.getLastModifiedTS()
							+ ") is after application submission date ("
							+ app.getSubmittedDate() + ")");
					continue;
				}

				if (appDoc.getDocumentId() != null) {
					doc = documentDAO.retrieveDocument(appDoc.getDocumentId());
					// document description may not be in synch with application
					// document
					doc.setDescription(appDoc.getDescription());
					if (!doc.isTemporary()) {
						docList.add(doc);
					}
				}
				if (!hideTradeSecret && appDoc.getTradeSecretDocId() != null && (includeAllAttachments || app.getSubmittedDate() != null || appDoc.isTradeSecretAllowed())) {
					doc = documentDAO.retrieveDocument(appDoc
							.getTradeSecretDocId());
					if (!doc.isTemporary()) {
						// document description may not be in synch with
						// application document
						doc.setDescription(appDoc.getDescription()
								+ " (trade secret)");
						docList.add(doc);
					}
				}
			}
			// get EU-specific attachments
			for (ApplicationEU appEU : app.getIncludedEus()) {
				String docPrefix = "EU " + appEU.getFpEU().getEpaEmuId() + ": ";
				for (ApplicationDocumentRef appDoc : appEU.getEuDocuments()) {
					// don't include documents added after application was
					// submitted
					if (app.getSubmittedDate() != null
							&& appDoc.getLastModifiedTS() != null
							&& appDoc.getLastModifiedTS().after(
									app.getSubmittedDate())) {
						logger.debug("Excluding EU document "
								+ appDoc.getApplicationDocId()
								+ " from application printable attachment list. Document last modified date ("
								+ appDoc.getLastModifiedTS()
								+ ") is after application submission date ("
								+ app.getSubmittedDate() + ")");
						continue;
					}
					if (appDoc.getDocumentId() != null) {
						doc = documentDAO.retrieveDocument(appDoc
								.getDocumentId());
						if (!doc.isTemporary()) {
							doc.setDescription(docPrefix
									+ appDoc.getDescription());
							docList.add(doc);
						}
					}
					if (!hideTradeSecret
							&& appDoc.getTradeSecretDocId() != null && (includeAllAttachments || app.getSubmittedDate() != null || appDoc.isTradeSecretAllowed())) {
						doc = documentDAO.retrieveDocument(appDoc
								.getTradeSecretDocId());
						if (!doc.isTemporary()) {
							doc.setDescription(docPrefix
									+ appDoc.getDescription()
									+ " (trade secret)");
							docList.add(doc);
						}
					}
				}
			}
		}
		return docList;

	}

	private SubmissionLog getSubmissionLogForApplication(Application app) {
		SubmissionLog log = null;
		SubmissionLog searchSubmissionLog = new SubmissionLog();
		Task t = new Task();
		HashMap<TaskType, String> taskTypeDescs = t.getTaskTypeDescs();
		searchSubmissionLog.setFacilityId(app.getFacilityId());
		// default to PTIO application
		searchSubmissionLog.setSubmissionType(taskTypeDescs.get(TaskType.PTPA));
		if (app instanceof PTIOApplication
				&& (app.isApplicationAmended() || app.isApplicationCorrected())) {
			searchSubmissionLog.setSubmissionType(taskTypeDescs
					.get(TaskType.COPY_PTPA));
		} else if (app instanceof TVApplication) {
			if (app.isApplicationAmended() || app.isApplicationCorrected()) {
				searchSubmissionLog.setSubmissionType(taskTypeDescs
						.get(TaskType.COPY_TVPA));
			} else {
				searchSubmissionLog.setSubmissionType(taskTypeDescs
						.get(TaskType.TVPA));
			}
		} else if (app instanceof TIVApplication) {
			searchSubmissionLog.setSubmissionType(taskTypeDescs
					.get(TaskType.TIVPA));
		} else if (app instanceof PBRNotification) {
			searchSubmissionLog.setSubmissionType(taskTypeDescs
					.get(TaskType.PBR));
		// IMPACT Currently doesn't support RPCRequests
		//} else if (app instanceof RPCRequest) {
		//	searchSubmissionLog.setSubmissionType(taskTypeDescs
		//			.get(TaskType.RPC));
		}
		try {
			int count = 0;
			for (SubmissionLog tmp : facilityDAO().searchSubmissionLog(
					searchSubmissionLog, app.getSubmittedDate(),
					app.getSubmittedDate())) {
				log = tmp;
				count++;
			}
			if (count > 0) {
				logger.error("Multiple submissions found for application: "
						+ app.getApplicationNumber() + ". Setting user id to "
						+ log.getGatewayUserName());
			} else if (count == 0) {
				// try search again for PTI and TV applications - may be a
				// "copy"
				if (!app.isApplicationAmended()
						&& ((app instanceof PTIOApplication) || (app instanceof TVApplication))) {
					app.setApplicationAmended(true); // force this flag to true
														// so "copy"
					// types will be searched
					log = getSubmissionLogForApplication(app);
				}
				if (log == null) {
					logger.error("No submissions found for application: "
							+ app.getApplicationNumber());
				}
			}
		} catch (DAOException e) {
			logger.error("Exception retrieving application from submission log"
					+ app.getApplicationNumber(), e);
		}
		return log;
	}

	/**
	 * Generate a pdf file containing data from the application and create a
	 * temporary Document object refrencing this file.
	 * 
	 * @param application
	 *            the application to be rendered in a PDF file.
	 * @param hideTradeSecret
	 *            flag indicating whether trade secret information should be
	 *            excluded from the generated file.
	 * @return Document object referencing pdf file.
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean generateTempApplicationReport(Application application,
			boolean hideTradeSecret, TmpDocument appDoc, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		boolean hasTS = false;
		try {
			// appDoc = new TmpDocument();
			// Set the path elements of the temp doc.
			String appName = ApplicationTypeDef.getData().getItems()
					.getItemDesc(application.getApplicationTypeCD());
			appDoc.setDescription("Printable View of "
					+ (hideTradeSecret ? appName : appName
							+ " with trade secret data"));
			appDoc.setFacilityID(application.getFacilityId());
			appDoc.setTemporary(true);
			appDoc.setTmpFileName("Application_" + application.getApplicationNumber()
					+ (hideTradeSecret ? "" : "_TS") + ".pdf");

			// make sure temporary directory exists
			DocumentUtil.mkDirs(appDoc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(appDoc
					.getPath());
			hasTS = writeApplicationReportToStream(application,
					hideTradeSecret, os, isSubmittedPDFDoc, includeAllAttachments);
			os.close();

			//if (!hideTradeSecret) {
			//	PdfGeneratorBase.addTradeSecretWatermark(appDoc.getPath());
			//}

		} catch (Exception ex) {
			logger.error("Cannot generate application report for "
					+ application.getApplicationNumber(), ex);
			throw new DAOException("Cannot generate application report", ex);
		}

		return hasTS;
	}

	/**
	 * Create a zip file containing application data and all its related
	 * attachments and download its contents.
	 * 
	 * @param app
	 * @throws IOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document generateTempApplicationZipFile(Application app,
			Document facilityDocument, boolean hideTradeSecret, boolean includeAllAttachments, boolean readOnly)
			throws FileNotFoundException, IOException {

		TmpDocument appDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		appDoc.setDescription(APPLICATION_ZIP_FILE);
		appDoc.setFacilityID(app.getFacilityId());
		appDoc.setTemporary(true);
		if(hideTradeSecret) {
			appDoc.setTmpFileName(app.getApplicationNumber() + ".zip");
		} else {
			appDoc.setTmpFileName(app.getApplicationNumber() + "_TS" + ".zip");
		}
		appDoc.setContentType(Document.CONTENT_TYPE_ZIP);

		// make sure temporary directory exists
		DocumentUtil.mkDirs(appDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(appDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		zipApplicationFiles(app, facilityDocument, zos, hideTradeSecret, includeAllAttachments, readOnly);
		zos.close();
		os.close();

		return appDoc;
	}

	/**
	 * Create a zip file containing application data and all its related
	 * attachments and download its contents.
	 * 
	 * @param app
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document generateTempApplicationAttachmentZipFile(Application app, boolean includeAllAttachments, boolean readOnly)
			throws FileNotFoundException, IOException {

		TmpDocument appDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		appDoc.setDescription("Application attachments zip file");
		appDoc.setFacilityID(app.getFacilityId());
		appDoc.setTemporary(true);
		appDoc.setTmpFileName(app.getApplicationNumber() + "Attachments.zip");

		// make sure temporary directory exists
		DocumentUtil.mkDirs(appDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(appDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		zipApplicationAttachments(app, true, zos, includeAllAttachments, readOnly);
		zos.close();
		os.close();

		return appDoc;
	}

	/**
	 * Create a zip file containing application pdf files.
	 * 
	 * @param app
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document generateTempApplicationPDFZipFile(Application app)
			throws FileNotFoundException, IOException {

		TmpDocument appDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		appDoc.setDescription("Application report (pdf) zip file");
		appDoc.setFacilityID(app.getFacilityId());
		appDoc.setTemporary(true);
		appDoc.setTmpFileName(app.getApplicationNumber() + "PDFReport.zip");

		// make sure temporary directory exists
		DocumentUtil.mkDirs(appDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(appDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		zipApplicationPDFReportFiles(app, zos);
		zos.close();
		os.close();

		return appDoc;
	}

	/**
	 * Validate the PTIO application app.
	 * 
	 * @param app
	 *            the application.
	 * @param messages
	 *            validation messages.
	 * @return true if release point emissions will trigger modelling for one of
	 *         the EUs included in the application. This is a kludge, but I
	 *         haven't thought of a better way to do this yet.
	 */
	private void validatePTIOApplication(PTIOApplication app,
			List<ValidationMessage> messages) {
		HashSet<String> generalPermitCds = getGeneralPermitCds(app);

		// received date must be specified on internal application, but defaults
		// to submit date for portal application
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)
				&& app.getReceivedDate() == null) {
			messages.add(new ValidationMessage("applicationReceivedDate",
					"Date application received is missing",
					ValidationMessage.Severity.ERROR, "application"));
		}

		if (app.getApplicationDesc() == null
				|| app.getApplicationDesc().length() == 0) {
			messages.add(new ValidationMessage(
					"PermitReasonText",
					"The reason for which a permit is being applied is missing",
					ValidationMessage.Severity.ERROR, "application"));
		}

		// Section 1, question 1: Application Purpose - required gor general
		// permit
		if (generalPermitCds.size() > 0
				&& app.getApplicationPurposeCDs().size() == 0) {
			messages.add(new ValidationMessage("AppPurposesCheckbox",
					"Application Purpose is missing",
					ValidationMessage.Severity.ERROR, "application"));
		}

		// Section 1, Question 4: Express Processing - not required

		// Section 1, Question 5: Air Contaminant Sources - always required
		List<ApplicationEU> includedEus = app.getIncludedEus();
		if (includedEus.size() == 0) {
			messages.add(new ValidationMessage("applicationDetailTree",
					"At least one EU must be included in the application",
					ValidationMessage.Severity.ERROR, "application"));
		} else {
			for (ApplicationEU appEU : includedEus) {
				validatePTIOApplicationEU((PTIOApplicationEU) appEU, app,
						messages);
			}
		}

		// Section 1, Question 6: Trade Secret Information -
		// This only applies to attachments and is handled by attachment logic

		// Section 1, Question 7: Contact - required
		if (app.getContact() == null) {
			messages.add(new ValidationMessage("contactFirstName",
					"Application Contact information is missing",
					ValidationMessage.Severity.ERROR, "application"));
		}
		if (app.getContainH2SFlag() != null
				&& app.getContainH2SFlag().equals("Y")) {
			if (app.getDivisionContacedFlag() != null
					&& app.getDivisionContacedFlag().equals("N")) {
				messages.add(new ValidationMessage(
						"divisionContacedFlag",
						"If the production at your facility contains H2S, then you must contact the Air Quality Division prior to submitting your NSR application",
						ValidationMessage.Severity.ERROR, "application"));
			}
		}

		// Section 1, Question 8: Authorized Signature - only needed for paper
		// application
	}

	private boolean isNeedSubpart(String flag) {
		return !Utility.isNullOrEmpty(flag)
				&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
	}

	private void validateTVApplication(TVApplication app,
			List<ValidationMessage> messages) {
		messages.add(new ValidationMessage(
				"DocTab",
				"Each flare or combustor must be entered as an emission unit and a control device in your Facility Inventory.",
				ValidationMessage.Severity.INFO, "application"));

		// received date must be specified on internal application, but defaults
		// to submit date for portal application
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)
				&& app.getReceivedDate() == null) {
			messages.add(new ValidationMessage("applicationReceivedDate",
					"Date application received is missing",
					ValidationMessage.Severity.ERROR, "application"));
		}
		
		if (app.getTvApplicationPurposeCd() == null) {
			messages.add(new ValidationMessage("tvApplicationPurposeCd",
					"The reason for this application is missing",
					ValidationMessage.Severity.ERROR, "application"));
		} else if (app.getTvApplicationPurposeCd().equals(
				TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING)
				&& Utility.isNullOrEmpty(app.getPermitReasonCd())) {
			messages.add(new ValidationMessage("tvApplicationPurposeCd",
					"The reason for this application is incomplete",
					ValidationMessage.Severity.ERROR, "application"));
		}
		
		if(app.getTvApplicationPurposeCd() != null){
			if((app.getTvApplicationPurposeCd().equals(TVApplicationPurposeDef.INITIAL_APPLICATION) 
					|| app.getTvApplicationPurposeCd().equals(TVApplicationPurposeDef.RENEWAL))
					&& app.getIncludedEus().size() == 0) {
				messages.add(new ValidationMessage("applicationDetailTree",
						"At least one Emission Unit must be included in the application",
						ValidationMessage.Severity.ERROR, "application"));
			}
		}
		
		if (app.getApplicationDesc() == null
				|| app.getApplicationDesc().length() == 0) {
			messages.add(new ValidationMessage(
					"tvPermitReasonText",
					"The reason for which a permit is being applied is missing",
					ValidationMessage.Severity.ERROR, "application"));
		}

		validateTVApplicationSubparts(app, messages);

		for (TVPteAdjustment tvA : app.getCapPteTotals()) {
			if (tooSmall(tvA.getPteEUTotal(), tvA.getPteAdjusted())) {
				messages.add(new ValidationMessage("tvAppCAPsmall "
						+ tvA.getPollutantCd(),
						"Facility PTE cannot be smaller than EU Total for CAP "
								+ PollutantDef.getData().getItems()
										.getItemDesc(tvA.getPollutantCd()),
						ValidationMessage.Severity.ERROR, "application"));
			}

		}

		for (TVPteAdjustment tvA : app.getHapPteTotals()) {

			if (tooSmall(tvA.getPteEUTotal(), tvA.getPteAdjusted())) {
				messages.add(new ValidationMessage("tvAppHAPsmall "
						+ tvA.getPollutantCd(),
						"Facility PTE cannot be smaller than EU Total for HAP "
								+ PollutantDef.getData().getItems()
										.getItemDesc(tvA.getPollutantCd()),
						ValidationMessage.Severity.ERROR, "application"));
			}

		}

		for (TVPteAdjustment tvA : app.getGhgPteTotals()) {
			if (tooSmall(tvA.getPteEUTotal(), tvA.getPteAdjusted())) {
				messages.add(new ValidationMessage("tvAppGHGsmall "
						+ tvA.getPollutantCd(),
						"Facility PTE cannot be smaller than EU Total for GHG "
								+ PollutantDef.getData().getItems()
										.getItemDesc(tvA.getPollutantCd()),
						ValidationMessage.Severity.ERROR, "application"));
			}

		}

		for (ApplicationEU appEU : app.getIncludedEus()) {
			validateTVApplicationEU((TVApplicationEU) appEU, app, messages);
		}
	}

	private void validateAppRequiredFields(Application app, List<ValidationMessage> messages) {

		List<ApplicationEU> aeus = app.getIncludedEus();
		for (ApplicationEU aeu : aeus) {
			if (aeu instanceof PTIOApplicationEU) {
				if (((PTIOApplicationEU) aeu).getEuType() instanceof NSRApplicationEUTypeENG) {
					NSRApplicationEUTypeENG eng = (NSRApplicationEUTypeENG) ((PTIOApplicationEU) aeu).getEuType();
					if ("Diesel".equals(eng.getFpEuPrimaryFuelType())
							|| "Diesel".equals(eng.getFpEuSecondaryFuelType())) {
						if (eng.getDieselEngineEpaTierCertifiedFlag() == null) {
							messages.add(new ValidationMessage("dieselEngineEpaTierCertifiedFlag", 
									"Attribute Diesel Engine EPA Tier Certified is not set.",
									ValidationMessage.Severity.ERROR, 
									"eu:" + aeu.getApplicationEuId(), 
									aeu.getFpEU().getEpaEmuId()));							
						}
					}
				}
			}
		}

		ValidationMessage[] validResults = app.validate();
		for (ValidationMessage validResult : validResults) {
			messages.add(new ValidationMessage(validResult.getProperty(), validResult.getMessage(),
					ValidationMessage.Severity.ERROR, "application"));
		}
	}

	private void validateTVApplicationSubparts(TVApplication app,
			List<ValidationMessage> messages) {
		boolean isNeedNspsSubpart = isNeedSubpart(app.getNspsApplicableFlag());
		boolean isNeedNeshapSubpart = isNeedSubpart(app
				.getNeshapApplicableFlag());
		boolean isNeedMactSubpart = isNeedSubpart(app.getMactApplicableFlag());

		List<String> nspsSubpartCds = app.getNspsSubpartCodes();
		List<String> neshapSubpartCds = app.getNeshapSubpartCodes();
		List<String> mactSubpartCds = app.getMactSubpartCodes();

		if (isNeedNspsSubpart
				&& (nspsSubpartCds == null || nspsSubpartCds.size() == 0)) {
			messages.add(new ValidationMessage("nspsApplicableFlag",
					"Application NSPS subparts missing",
					ValidationMessage.Severity.ERROR, "application"));
		}

		if (isNeedNeshapSubpart
				&& (neshapSubpartCds == null || neshapSubpartCds.size() == 0)) {
			messages.add(new ValidationMessage("neshapApplicableFlag",
					"Application Part 61 NESHAP subparts missing",
					ValidationMessage.Severity.ERROR, "application"));
		}

		if (isNeedMactSubpart
				&& (mactSubpartCds == null || mactSubpartCds.size() == 0)) {
			messages.add(new ValidationMessage("mactApplicableFlag",
					"Application Part 63 NESHAP subparts missing",
					ValidationMessage.Severity.ERROR, "application"));
		}
	}

	/**
	 * Validate an Emissions Unit included in the PTIO application app.
	 * 
	 * @param ptioAppEU
	 *            the emissions unit.
	 * @param app
	 *            the application.
	 * @param messages
	 *            validation messages.
	 * @return true if release point emissions will trigger modelling for this
	 *         EU. This is a kludge, but I haven't thought of a better way to do
	 *         this yet.
	 */
	private void validatePTIOApplicationEU(PTIOApplicationEU ptioAppEU,
			PTIOApplication app, List<ValidationMessage> messages) {
		List<ValidationMessage> euMessages = new ArrayList<ValidationMessage>();
		// get threshold definitions
		final float BAT_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
				"APP_EMISSIONS_BAT_Threshold", 10.0f);
		final float MAX_HAP_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
				"APP_EMISSIONS_Max_HAP_Threshold", 10.0f);
		final float TOTAL_HAP_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
				"APP_EMISSIONS_Total_HAP_Threshold", 25.0f);
		final float TOXIC_HAP_EGRESS_PT_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
				"APP_EMISSIONS_Toxic_Single_HAP_Egress_Pt_Threshold", 1.0f);

		// check for required attachments
		boolean demEgPntModeling = false;
		boolean isPsdSet = PTIOFedRuleAppl2Def.SUBJECT_TO_REG
				.equals(((PTIOApplication) app).getPsdApplicableFlag());

		// Section 2, Question 1: Installation or Modification Schedule -
		// always required
		String purposeCd = ptioAppEU.getPtioEUPurposeCD();
		if (purposeCd == null) {
			euMessages.add(new ValidationMessage("EUPurposesCheckbox",
					"Installation/Modification Schedule choice is missing",
					ValidationMessage.Severity.ERROR, "eu:"
							+ ptioAppEU.getApplicationEuId(), ptioAppEU
							.getFpEU().getEpaEmuId()));
		} else {
			// make sure required fields related to purpose code selected are
			// populated
			if (purposeCd.equals(PTIOApplicationEUPurposeDef.CONSTRUCTION)) {
				String facTypeCd = app.getFacility().getFacilityTypeCd();
				if (ptioAppEU.getWorkStartDate() == null
						&& !ptioAppEU.isWorkStartAfterPermit()
						&& FacilityTypeDef.isOilAndGas(facTypeCd)) {
					euMessages.add(new ValidationMessage("WorkStartDate",
							"Date production began information is missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			} else if (purposeCd
					.equals(PTIOApplicationEUPurposeDef.CONSTRUCTION)) {
				if (ptioAppEU.getWorkStartDate() == null) {
					euMessages.add(new ValidationMessage("WorkStartDate",
							"Construction Began Date is missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
				if (ptioAppEU.getOperationBeginDate() == null) {
					euMessages.add(new ValidationMessage("OperationBeganDate",
							"Operation Began Date is missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			} else if (purposeCd
					.equals(PTIOApplicationEUPurposeDef.MODIFICATION)) {
				if (ptioAppEU.getWorkStartDate() == null
						&& !ptioAppEU.isWorkStartAfterPermit()) {
					euMessages.add(new ValidationMessage("WorkStartDate",
							"Modification Date information is missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			} else if (purposeCd
					.equals(PTIOApplicationEUPurposeDef.MODIFICATION)) {
				if (ptioAppEU.getWorkStartDate() == null) {
					euMessages.add(new ValidationMessage("WorkStartDate",
							"Modification Began Date is missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
				if (ptioAppEU.getOperationBeginDate() == null) {
					euMessages.add(new ValidationMessage("OperationBeganDate",
							"Operation Began Date is missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			} else if (purposeCd
					.equals(PTIOApplicationEUPurposeDef.RECONSTRUCTION)) {
				if (ptioAppEU.getReconstructionDesc() == null) {
					euMessages.add(new ValidationMessage(
							"ModificationDescText",
							"Modification Description information is missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			} else if (purposeCd.equals(PTIOApplicationEUPurposeDef.OTHER)) {
				// NOTE: modificationDesc is overloaded to contain Other desc
				// as well
				if (ptioAppEU.getModificationDesc() == null) {
					euMessages.add(new ValidationMessage(
							"ModificationDescText",
							"Other application purpose is missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			}
		}

		/*
		 * General Permit not valid for WY if (ptioAppEU.isGeneralPermit()) { if
		 * (ptioAppEU.getGeneralPermitTypeCd() == null) { euMessages .add(new
		 * ValidationMessage( "generalPermitTypeChoice",
		 * "General Permit Category is required for General Permit applications"
		 * , ValidationMessage.Severity.ERROR, "eu:" +
		 * ptioAppEU.getApplicationEuId(), ptioAppEU.getFpEU().getEpaEmuId()));
		 * } if (ptioAppEU.getModelGeneralPermitCd() == null) { // make sure
		 * Model General Permit number is specified for // General Permit EU
		 * euMessages .add(new ValidationMessage( "modelGeneralPermitChoice",
		 * "General Permit Type is required for General Permit applications",
		 * ValidationMessage.Severity.ERROR, "eu:" +
		 * ptioAppEU.getApplicationEuId(), ptioAppEU.getFpEU().getEpaEmuId()));
		 * } }
		 */

		// Section 2, Question 2: SCC Codes - handled by Facility Inventory

		// Section 2, Question 3: Emissions Information - not required for
		// General Permit
		boolean emissionsValueSpecified = false;
		boolean batThresholdExceeded = false;
		boolean egressPointThresholdExceeded = false;
		HashMap<String, BigDecimal> reqAllowableMap = new HashMap<String, BigDecimal>();
		for (ApplicationEUEmissions emissions : ptioAppEU.getCapEmissions()) {
			BigDecimal potentialToEmitTonYr = getEmissionValueAsBigDecimal(
					emissions.getPollutantCd(),
					emissions.getEuEmissionTableCd(),
					emissions.getPotentialToEmitTonYr());
			
			if (emissions.isValueSpecified()) {
				emissionsValueSpecified = true;

				if (!emissions.getPollutantCd().equals(PollutantDef.HTOT_CD)) {
					if (potentialToEmitTonYr.compareTo(new BigDecimal(
							BAT_THRESHOLD)) == 1) {
						batThresholdExceeded = true;
					}

					Float egressPointThreshold = getEgressPtThreshold(emissions
							.getPollutantCd());
					if (egressPointThreshold != null
							&& potentialToEmitTonYr.compareTo(new BigDecimal(
									egressPointThreshold)) == 1) {
						egressPointThresholdExceeded = true;
					}
				}
			}
		}
		for (ApplicationEUEmissions emissions : ptioAppEU.getHapTacEmissions()) {
			BigDecimal potentialToEmitTonYr = getEmissionValueAsBigDecimal(
					emissions.getPollutantCd(),
					emissions.getEuEmissionTableCd(),
					emissions.getPotentialToEmitTonYr());
			
			if (emissions.isValueSpecified()) {
				emissionsValueSpecified = true;
				// note if any single emission exceeds 10 tons per year
				if (!emissions.getPollutantCd().equals(PollutantDef.HTOT_CD)
						&& potentialToEmitTonYr.compareTo(new BigDecimal(
								BAT_THRESHOLD)) == 1) {
					batThresholdExceeded = true;
				}
			}
		}

		/*
		 * boolean hapThresholdExceeded = (reqAllowableMap
		 * .get(PollutantDef.HMAX_CD) != null && reqAllowableMap
		 * .get(PollutantDef.HMAX_CD) > MAX_HAP_THRESHOLD) ||
		 * (reqAllowableMap.get(PollutantDef.HTOT_CD) != null && reqAllowableMap
		 * .get(PollutantDef.HTOT_CD) > TOTAL_HAP_THRESHOLD);
		 */
		/*
		 * General Permit not valid for WY if (!ptioAppEU.isGeneralPermit()) {
		 * if (!emissionsValueSpecified) { euMessages.add(new
		 * ValidationMessage("capTable", "No emissions information specified",
		 * ValidationMessage.Severity.ERROR, "eu:" +
		 * ptioAppEU.getApplicationEuId(), ptioAppEU .getFpEU().getEpaEmuId()));
		 * } else if (ptioAppEU.getHapTacEmissions().size() > 0 &&
		 * (reqAllowableMap.get(PollutantDef.HMAX_CD) == null || reqAllowableMap
		 * .get(PollutantDef.HTOT_CD) == null)) { euMessages .add(new
		 * ValidationMessage( "capTable",
		 * "The Requested Allowable (ton/year) column should be non-zero for " +
		 * PollutantDef.HMAX_DSC + " and " + PollutantDef.HTOT_DSC +
		 * " when values are specified in the HAP emissions table",
		 * ValidationMessage.Severity.WARNING, "eu:" +
		 * ptioAppEU.getApplicationEuId(), ptioAppEU.getFpEU().getEpaEmuId()));
		 * } }
		 */

		/*
		 * if (hapThresholdExceeded) { if (app.isRequestExpress()) { euMessages
		 * .add(new ValidationMessage( "RequestingExpressProcessingBox",
		 * "Express processing cannot be requested due to exceeded emissions threshold"
		 * , ValidationMessage.Severity.ERROR, "application")); }
		 * 
		 * if (!PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART.equals(app
		 * .getMactApplicableFlag())) { euMessages .add(new ValidationMessage(
		 * "MACTChoice",
		 * "MACT and subpart should be selected due to exceeded emissions threshold"
		 * , ValidationMessage.Severity.WARNING, "eu:" +
		 * ptioAppEU.getApplicationEuId(), ptioAppEU.getFpEU().getEpaEmuId()));
		 * }
		 * 
		 * 
		 * General Permit not valid for WY if (ptioAppEU.isGeneralPermit()) {
		 * euMessages .add(new ValidationMessage( "generalPermitBox",
		 * "General permit is not allowed due to exceeded emissions threshold" ,
		 * ValidationMessage.Severity.ERROR, "eu:" +
		 * ptioAppEU.getApplicationEuId(), ptioAppEU.getFpEU().getEpaEmuId()));
		 * }
		 * 
		 * }
		 */

		// Section BACT, Question: BACT Analysis PSD BACT
		boolean bactPSDRequired = (ptioAppEU.getPsdBACTFlag() == null)
				&& (app.isPsdSubjectToReg());
		if (bactPSDRequired) {
			euMessages.add(new ValidationMessage("isPsdBactAnalysis",
					"PSD BACT analysis information is incomplete",
					ValidationMessage.Severity.ERROR, "eu:"
							+ ptioAppEU.getApplicationEuId(), ptioAppEU
							.getFpEU().getEpaEmuId()));
		}

		// Section BACT, Question: BACT Analysis Completed
		boolean bactAnalysisRequired = ptioAppEU.getBactFlag() == null;
		if (bactAnalysisRequired) {
			euMessages.add(new ValidationMessage("bactAnalysisCompleted",
					"BACT analysis completion information is missing",
					ValidationMessage.Severity.ERROR, "eu:"
							+ ptioAppEU.getApplicationEuId(), ptioAppEU
							.getFpEU().getEpaEmuId()));
		}

		boolean bactAnalysisCompleted = ptioAppEU.isBactAnalysisCompleted();
		if (bactAnalysisCompleted) {
			if (ptioAppEU.getBactEmissions().size() == 0) {
				euMessages.add(new ValidationMessage("bactAnalysisCompleted",
						"Missing BACT pollutants and decision information",
						ValidationMessage.Severity.ERROR, "eu:"
								+ ptioAppEU.getApplicationEuId(), ptioAppEU
								.getFpEU().getEpaEmuId()));
			} else {
				if (!ptioAppEU.hasUniqueBACTPollutants()) {
					euMessages.add(new ValidationMessage(
							"bactAnalysisCompleted",
							"Cannot have duplicate BACT pollutants.",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			}
		}

		// Section LAER, Question: LAER Analysis NSR LAER
		boolean laerNSRRequired = (ptioAppEU.getNsrLAERFlag() == null)
				&& (app.isNsrSubjectToReg());
		if (laerNSRRequired) {
			euMessages.add(new ValidationMessage("isNsrLaerAnalysis",
					"PSD LAER analysis information is incomplete",
					ValidationMessage.Severity.ERROR, "eu:"
							+ ptioAppEU.getApplicationEuId(), ptioAppEU
							.getFpEU().getEpaEmuId()));
		}

		// Section LAER, Question: LAER Analysis Completed
		boolean laerAnalysisRequired = ptioAppEU.getLaerFlag() == null;
		if (laerAnalysisRequired) {
			euMessages.add(new ValidationMessage("laerAnalysisCompleted",
					"LAER analysis completion information is missing",
					ValidationMessage.Severity.ERROR, "eu:"
							+ ptioAppEU.getApplicationEuId(), ptioAppEU
							.getFpEU().getEpaEmuId()));
		}

		boolean laerAnalysisCompleted = ptioAppEU.isLaerAnalysisCompleted();

		if (laerAnalysisCompleted) {
			if (ptioAppEU.getLaerEmissions().size() == 0) {
				euMessages.add(new ValidationMessage("laerAnalysisCompleted",
						"Missing LAER pollutants and decision information",
						ValidationMessage.Severity.ERROR, "eu:"
								+ ptioAppEU.getApplicationEuId(), ptioAppEU
								.getFpEU().getEpaEmuId()));
			} else {
				if (!ptioAppEU.hasUniqueLAERPollutants()) {
					euMessages.add(new ValidationMessage(
							"laerAnalysisCompleted",
							"Cannot have duplicate LAER pollutants.",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			}

		}

		// Section Federal Rules Applicability -
		// not required for some general permit types

		boolean fedRulesRequired = true;

		if (fedRulesRequired) {
			if (ptioAppEU.getNspsApplicableFlag() == null) {
				euMessages.add(new ValidationMessage("NSPSChoice",
						"Part 60 NSPS applicability missing",
						ValidationMessage.Severity.ERROR, "eu:"
								+ ptioAppEU.getApplicationEuId(), ptioAppEU
								.getFpEU().getEpaEmuId()));
			} else if (ptioAppEU.getNspsApplicableFlag().equals(
					PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART)
					|| ptioAppEU.getNspsApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)) {
				if (ptioAppEU.getNspsSubpartCodes().size() == 0) {
					euMessages.add(new ValidationMessage("NSPSChoice",
							"Part 60 NSPS subparts missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			}
			if (ptioAppEU.getNeshapApplicableFlag() == null) {
				euMessages.add(new ValidationMessage("NESHAPChoice",
						"Part 61 NESHAP applicability missing",
						ValidationMessage.Severity.ERROR, "eu:"
								+ ptioAppEU.getApplicationEuId(), ptioAppEU
								.getFpEU().getEpaEmuId()));
			} else if (ptioAppEU.getNeshapApplicableFlag().equals(
					PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART)
					|| ptioAppEU.getNeshapApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)) {
				if (ptioAppEU.getNeshapSubpartCodes().size() == 0) {
					euMessages.add(new ValidationMessage("NESHAPChoice",
							"Part 61 NESHAP subparts missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			}
			if (ptioAppEU.getMactApplicableFlag() == null) {
				euMessages.add(new ValidationMessage("MACTChoice",
						"Part 63 NESHAP applicability missing",
						ValidationMessage.Severity.ERROR, "eu:"
								+ ptioAppEU.getApplicationEuId(), ptioAppEU
								.getFpEU().getEpaEmuId()));
				ptioAppEU.setValidated(false);
			} else if (ptioAppEU.getMactApplicableFlag().equals(
					PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART)
					|| ptioAppEU.getMactApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)) {
				if (ptioAppEU.getMactSubpartCodes().size() == 0) {
					euMessages.add(new ValidationMessage("MACTChoice",
							"Part 63 NESHAP subparts missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ ptioAppEU.getApplicationEuId(), ptioAppEU
									.getFpEU().getEpaEmuId()));
				}
			}

			if ((ptioAppEU.getFederalRuleApplicabilityExplanation() == null || ptioAppEU
					.getFederalRuleApplicabilityExplanation().trim().length() == 0)
					&& (PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT.equals(ptioAppEU
							.getNspsApplicableFlag())
							|| PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT
									.equals(ptioAppEU.getNeshapApplicableFlag()) || PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT
								.equals(ptioAppEU.getMactApplicableFlag()))) {
				euMessages
						.add(new ValidationMessage(
								"exemptionExplText",
								"An explanation must be provided for federal rules marked 'Subject, but Exempt'.",
								ValidationMessage.Severity.ERROR, "eu:"
										+ ptioAppEU.getApplicationEuId(),
								ptioAppEU.getFpEU().getEpaEmuId()));
			}
			if (ptioAppEU.getPsdApplicableFlag() == null) {
				euMessages.add(new ValidationMessage("PSDChoice",
						"PSD applicability missing",
						ValidationMessage.Severity.ERROR, "eu:"
								+ ptioAppEU.getApplicationEuId(), ptioAppEU
								.getFpEU().getEpaEmuId()));
			}
			if (ptioAppEU.getNsrApplicableFlag() == null) {
				euMessages.add(new ValidationMessage("NSRChoice",
						"NSR applicability missing",
						ValidationMessage.Severity.ERROR, "eu:"
								+ ptioAppEU.getApplicationEuId(), ptioAppEU
								.getFpEU().getEpaEmuId()));
			}
		}

		// Section 2, Question 7: Modeling Information - Handled in Facility
		// Profile
		// Mantis 1917 - don't set this for renewal applications
		if (egressPointThresholdExceeded
				&& !app.getApplicationPurposeCDs().contains(
						PTIOApplicationPurposeDef.RECONSTRUCTION)) {
			// set flag so facility validation will check release point
			// information
			demEgPntModeling = true;
		}

		// Section: EU Type Specific Attributes
		if (ptioAppEU.getEuType() == null) {
			euMessages.add(new ValidationMessage("",
					"Emissions unit type specific attributes are missing",
					ValidationMessage.Severity.ERROR, "eu:"
							+ ptioAppEU.getApplicationEuId(), ptioAppEU
							.getFpEU().getEpaEmuId()));
		} else {
			for (ValidationMessage euTypeValMsg : ptioAppEU.getEuType()
					.validate()) {
				euMessages.add(new ValidationMessage(
						euTypeValMsg.getProperty(), euTypeValMsg.getMessage(),
						euTypeValMsg.getSeverity(), "eu:"
								+ ptioAppEU.getApplicationEuId(), ptioAppEU
								.getFpEU().getEpaEmuId()));
			}
			if (ptioAppEU.getEuType() instanceof NSRApplicationEUTypeFUG) {
				NSRApplicationEUTypeFUG nsrApplicationEUTypeFUG = (NSRApplicationEUTypeFUG) ptioAppEU
						.getEuType();
				if (nsrApplicationEUTypeFUG.getFugitiveEmissionTypeCd() != null
						&& nsrApplicationEUTypeFUG
								.getFugitiveEmissionTypeCd()
								.equals(AppEUFUGEmissionTypeDef.FIGUTIVE_LEAK_AT_OG)) {
					if (!ptioAppEU.getFpEU().getEmissionUnitType().isFugitiveLeaks()) {
						euMessages
						.add(new ValidationMessage(
								"fugitiveEmissionTypeCd",
								"Fugitive Leaks at O&G selected in application but this does not match the choice made in the facility EU.",
								ValidationMessage.Severity.WARNING, "eu:"
										+ ptioAppEU
												.getApplicationEuId(),
								ptioAppEU.getFpEU().getEpaEmuId()));
					} 
					
					if (ptioAppEU.getApplicationEUFugitiveLeaks().size() == 0) {

						euMessages
								.add(new ValidationMessage(
										"applicationEUFugitiveLeaksTable",
										"Emission Unit Type Specific Information missing",
										ValidationMessage.Severity.ERROR, "eu:"
												+ ptioAppEU
														.getApplicationEuId(),
										ptioAppEU.getFpEU().getEpaEmuId()));

					}
				} else {
					if (ptioAppEU.getFpEU().getEmissionUnitType().isFugitiveLeaks()) {
						euMessages
						.add(new ValidationMessage(
								"fugitiveEmissionTypeCd",
								"Fugitive Leaks at O&G is not selected in application but this does not match the choice made in the facility EU.",
								ValidationMessage.Severity.WARNING, "eu:"
										+ ptioAppEU
												.getApplicationEuId(),
								ptioAppEU.getFpEU().getEpaEmuId()));
					}
				}
			}
		}

		// Application EU is valid unless an error is recorded
		boolean euIsValid = true;
		for (ValidationMessage message : euMessages) {
			if (message.getSeverity().equals(ValidationMessage.Severity.ERROR)) {
				euIsValid = false;
				break;
			}
		}

		// validate required EU attachments
		List<ValidationMessage> euAttachmentValMsgs = validateRequiredAttachments(ptioAppEU);
		for (ValidationMessage euAttachMsg : euAttachmentValMsgs) {
			if (euAttachMsg.getSeverity().equals(
					ValidationMessage.Severity.ERROR)) {
				euIsValid = false;
				break;
			}
		}

		ptioAppEU.getFpEU().setDemEgPntModeling(demEgPntModeling);
		ptioAppEU.getFpEU().setPsdOrScreenModeling(
				(isPsdSet && demEgPntModeling));

		// make sure EU has at least one release point
		/* IMPACT Task 5976 Disable per Adam's input
		int egpCount = 0;
		if (demEgPntModeling || ptioAppEU.getFpEU().isPsdOrScreenModeling()) {
			Facility facility = app.getFacility();
			EmissionUnit fpEU = facility.getEmissionUnit(ptioAppEU.getFpEU()
					.getEmuId());
			egpCount += fpEU.getAllEgressPoints().size();
			for (EmissionProcess ep : fpEU.getEmissionProcesses()) {
				egpCount += ep.getEgressPoints().size();
				for (ControlEquipment ce : ep.getControlEquipments()) {
					egpCount += ce.getEgressPoints().size();
				}
			}
			if (egpCount == 0) {
				euMessages
						.add(new ValidationMessage(
								"capTable",
								"There are no release points associated with this emission unit in the facility inventory. "
										+ "At least one release point must exist for an emision unit that "
										+ "requires screen modeling or PSD modeling.",
								ValidationMessage.Severity.ERROR, "eu:"
										+ ptioAppEU.getApplicationEuId(),
								ptioAppEU.getFpEU().getEpaEmuId()));
				euIsValid = false;
			}
		}
		*/
		/*/* IMPACT Task 5976 Disable per Adam's input
		if (isPsdSet) {
			// need to make sure that Pre-Controlled Potential Emissions (tons/yr) are provided for
			// all pollutants that have a Potential to Emit (PTE) value
			for (ApplicationEUEmissions e : ptioAppEU.getCapEmissions()) {
				if (!"0".equals(e.getPotentialToEmitLbHr())
						|| !"0".equals(e.getPotentialToEmitTonYr())) {
					if ("0".equals(e.getPreCtlPotentialEmissions())) {
						euMessages
								.add(new ValidationMessage(
										"capTable",
										"The Pre-Controlled Potential Emissions (tons/yr) column should be non-zero "
												+ "when values are specified in the Potential to Emit (PTE) column and the application "
												+ "requires screen modeling or PSD modeling.",
										ValidationMessage.Severity.ERROR, "eu:"
												+ ptioAppEU
														.getApplicationEuId(),
										ptioAppEU.getFpEU().getEpaEmuId()));
						euIsValid = false;
					}
				}
			}
		}
		*/

		// if (ptioAppEU.getFpEU().isPsdOrScreenModeling()) {
		// // turn off demEgPntModeling if Psd or Screen is true to avoid
		// duplicate messages.
		// ptioAppEU.getFpEU().setDemEgPntModeling(false);
		// }
		messages.addAll(euMessages);
		ptioAppEU.setValidated(euIsValid);
	}

	private void validateTVApplicationEU(TVApplicationEU appEU,
			TVApplication app, List<ValidationMessage> messages) {
		// check for required attachments
		List<ValidationMessage> euMessages = new ArrayList<ValidationMessage>();

		// EAC Document is required for EUs that are non-Insignificant or
		// for Insignificant EUs that have applicable requirements defined
		// LSRD Requirement: 2.3.3-PTI_PTO_PTIO-1.1
		String nodeType = "eu:";
		boolean nonInsignificantEU = true;

		if (appEU.getNormalOperatingScenario().getOpAosAutherized() == null) {
			euMessages
					.add(new ValidationMessage(
							"opAosAutherized",
							"Specify whether or not Alternate Operating Scenarios is authorized for this emission unit",
							ValidationMessage.Severity.ERROR, nodeType
									+ appEU.getApplicationEuId(), appEU
									.getFpEU().getEpaEmuId()));
		}

		validateScenario(appEU.getNormalOperatingScenario(),
				nodeType + appEU.getApplicationEuId(), appEU, euMessages);

		// display warning if no applicable requirements are defined for the
		// non-Insignificant EU
		/* not valid for WY 
		if (appEU.getApplicableRequirements().size() == 0
				&& appEU.getStateOnlyRequirements().size() == 0
				&& nonInsignificantEU) {
			euMessages.add(new ValidationMessage("applicableReqsTable",
					"No applicable requirements have been specified.",
					ValidationMessage.Severity.WARNING, nodeType
							+ appEU.getApplicationEuId(), appEU.getFpEU()
							.getEpaEmuId()));
		}*/		

		// validate Alternate Scenario information
		for (TVEUOperatingScenario altScenario : appEU
				.getAlternateOperatingScenarios()) {
			validateScenario(altScenario,
					"scenario:" + altScenario.getApplicationEuId() + ":"
							+ altScenario.getTvEuOperatingScenarioId(), appEU,
					euMessages);
		}

		// Section Federal Rules Applicability -
		// not required for some general permit types

		boolean fedRulesRequired = true;

		if (fedRulesRequired) {
			if (appEU.getNspsApplicableFlag() == null) {
				euMessages.add(new ValidationMessage("NSPSChoice",
						"Part 60 NSPS applicability missing",
						ValidationMessage.Severity.ERROR, "eu:"
								+ appEU.getApplicationEuId(), appEU.getFpEU()
								.getEpaEmuId()));
			} else if (appEU.getNspsApplicableFlag().equals(
					PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART)
					|| appEU.getNspsApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)) {
				if (appEU.getNspsSubpartCodes().size() == 0) {
					euMessages.add(new ValidationMessage("NSPSChoice",
							"Part 60 NSPS subparts missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ appEU.getApplicationEuId(), appEU
									.getFpEU().getEpaEmuId()));
				}
			}
			if (appEU.getNeshapApplicableFlag() == null) {
				euMessages.add(new ValidationMessage("NESHAPChoice",
						"Part 61 NESHAP applicability missing",
						ValidationMessage.Severity.ERROR, "eu:"
								+ appEU.getApplicationEuId(), appEU.getFpEU()
								.getEpaEmuId()));
			} else if (appEU.getNeshapApplicableFlag().equals(
					PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART)
					|| appEU.getNeshapApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)) {
				if (appEU.getNeshapSubpartCodes().size() == 0) {
					euMessages.add(new ValidationMessage("NESHAPChoice",
							"Part 61 NESHAP subparts missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ appEU.getApplicationEuId(), appEU
									.getFpEU().getEpaEmuId()));
				}
			}
			if (appEU.getMactApplicableFlag() == null) {
				euMessages.add(new ValidationMessage("MACTChoice",
						"Part 63 NESHAP applicability missing",
						ValidationMessage.Severity.ERROR, "eu:"
								+ appEU.getApplicationEuId(), appEU.getFpEU()
								.getEpaEmuId()));
				appEU.setValidated(false);
			} else if (appEU.getMactApplicableFlag().equals(
					PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART)
					|| appEU.getMactApplicableFlag().equals(
							PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)) {
				if (appEU.getMactSubpartCodes().size() == 0) {
					euMessages.add(new ValidationMessage("MACTChoice",
							"Part 63 NESHAP subparts missing",
							ValidationMessage.Severity.ERROR, "eu:"
									+ appEU.getApplicationEuId(), appEU
									.getFpEU().getEpaEmuId()));
				}
			}

			if ((appEU.getFederalRuleApplicabilityExplanation() == null || appEU
					.getFederalRuleApplicabilityExplanation().trim().length() == 0)
					&& (PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT.equals(appEU
							.getNspsApplicableFlag())
							|| PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT
									.equals(appEU.getNeshapApplicableFlag()) || PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT
								.equals(appEU.getMactApplicableFlag()))) {
				euMessages
						.add(new ValidationMessage(
								"exemptionExplText",
								"An explanation must be provided for federal rules marked 'Subject, but Exempt'.",
								ValidationMessage.Severity.ERROR, "eu:"
										+ appEU.getApplicationEuId(), appEU
										.getFpEU().getEpaEmuId()));
			}
		}

		// Pollutant Limits
		if (appEU.getPollutantLimits().size() > 0) {
			for (TVEUPollutantLimit pollutantLimit : appEU.getPollutantLimits()) {
				pollutantLimit.setControlled(isPollutantControlledForValidate(appEU,
						pollutantLimit.getPollutantCd()));

				for (ValidationMessage polLimitValMsg : pollutantLimit
						.validate()) {
					euMessages.add(new ValidationMessage(polLimitValMsg
							.getProperty(), "Pollutant Limit: "
							+ polLimitValMsg.getMessage(), polLimitValMsg
							.getSeverity(), "eu:" + appEU.getApplicationEuId(),
							appEU.getFpEU().getEpaEmuId()));
				}
			}

		}

		// Operational Restrictions
		for (TVEUOperationalRestriction operationalRestriction : appEU
				.getOperationalRestrictions()) {
			for (ValidationMessage orValMsg : operationalRestriction.validate()) {
				euMessages.add(new ValidationMessage(orValMsg.getProperty(),
						"Operational Restriction: " + orValMsg.getMessage(),
						orValMsg.getSeverity(), "eu:"
								+ appEU.getApplicationEuId(), appEU.getFpEU()
								.getEpaEmuId()));
			}
		}

		// Application EU is valid unless an error is recorded
		boolean euIsValid = true;
		for (ValidationMessage message : euMessages) {
			if (message.getSeverity().equals(ValidationMessage.Severity.ERROR)) {
				euIsValid = false;
				break;
			}
		}
		appEU.setValidated(euIsValid);
		messages.addAll(euMessages);
	}

	private void validateScenario(TVEUOperatingScenario scenario,
			String referenceID, ApplicationEU appEU,
			List<ValidationMessage> messages) {
		String msgPrefix = "";
		if (scenario.getTvEuOperatingScenarioId() != 0) {
			msgPrefix = "[" + scenario.getTvEuOperatingScenarioNm() + "] ";
		}
		if (!referenceID.startsWith("insignificantEU")) {
			if (scenario.getOpSchedHrsDay() == null
					|| scenario.getOpSchedHrsYr() == null) {
				messages.add(new ValidationMessage(
						"opSchedHrsDayText",
						msgPrefix + "Operating schedule information is missing",
						ValidationMessage.Severity.ERROR, referenceID, appEU
								.getFpEU().getEpaEmuId()));
			} else if (scenario.getOpSchedHrsDay() > scenario.getOpSchedHrsYr()) {
				messages.add(new ValidationMessage(
						"opSchedHrsDayText",
						msgPrefix
								+ "Operating schedule Hours/day must not exceed Hours/year",
						ValidationMessage.Severity.ERROR, referenceID, appEU
								.getFpEU().getEpaEmuId()));
			}

			if (scenario.isOpSchedTradeSecret()
					&& scenario.getOpSchedTradeSecretReason() == null) {
				messages.add(new ValidationMessage(
						"ReasonOpSchedTSText",
						msgPrefix
								+ "The reason why the Operating Schedule is a trade secret is missing",
						ValidationMessage.Severity.ERROR, referenceID, appEU
								.getFpEU().getEpaEmuId()));
			}
		}

		// at least one non-zero entry is required in the PTE table
		boolean emissionsSpecified = false;
		boolean detBasisMissing = false;
		for (TVApplicationEUEmissions emissions : scenario.getCapEmissions()) {
			BigDecimal pteTonsYr = getEmissionValueAsBigDecimal(
					emissions.getPollutantCd(),
					emissions.getEuEmissionTableCd(), emissions.getPteTonsYr());
			if (pteTonsYr.compareTo(new BigDecimal("0")) != 0) {
				emissionsSpecified = true;
				if (emissions.getPteDeterminationBasis() == null) {
					detBasisMissing = true;
					break;
				}
			}
		}
		// check HAPs if no values are specified in the CAPs
		if (!emissionsSpecified) {
			for (TVApplicationEUEmissions emissions : scenario
					.getHapEmissions()) {
				BigDecimal pteTonsYr = getEmissionValueAsBigDecimal(
						emissions.getPollutantCd(),
						emissions.getEuEmissionTableCd(),
						emissions.getPteTonsYr());
				if (pteTonsYr.compareTo(new BigDecimal("0")) != 0) {
					emissionsSpecified = true;
					if (emissions.getPteDeterminationBasis() == null) {
						detBasisMissing = true;
						break;
					}
				}
			}
		}

		// WY requested to allow possibility of zero potential emission in TV application
		// hence commenting below validation
		/*
		if (!emissionsSpecified) {
			if (!referenceID.startsWith("insignificantEU")) {
				messages.add(new ValidationMessage(
						"capTable",
						msgPrefix
								+ "A non-zero PTE value must be specified in the PTE table",
						ValidationMessage.Severity.ERROR, referenceID, appEU
								.getFpEU().getEpaEmuId()));
			} else {
				messages.add(new ValidationMessage(
						"capTable",
						msgPrefix
								+ "No PTE value(s) specified for IEU. "
								+ "If this unit has a PTE for the specified pollutants, "
								+ "you are required to identify those emissions per OAC 3745-77-03(a)",
						ValidationMessage.Severity.WARNING, referenceID, appEU
								.getFpEU().getEpaEmuId()));
			}
		} else if (detBasisMissing) {
			messages.add(new ValidationMessage(
					"capTable",
					msgPrefix
							+ "The PTE Determination Basis column must be populated for rows with a non-zero PTE value",
					ValidationMessage.Severity.ERROR, referenceID, appEU
							.getFpEU().getEpaEmuId()));
		}*/
		
		// added this below check after commenting out the above one
		if(emissionsSpecified && detBasisMissing) {
			messages.add(new ValidationMessage(
					"capTable",
					msgPrefix
							+ "The PTE Determination Basis column must be populated for rows with a non-zero PTE value",
					ValidationMessage.Severity.ERROR, referenceID, appEU
							.getFpEU().getEpaEmuId()));
		}
		
	}

	private HashSet<String> getGeneralPermitCds(PTIOApplication ptioApp) {
		HashSet<String> generalPermitCds = new HashSet<String>();
		for (ApplicationEU eu : ptioApp.getIncludedEus()) {
			if (((PTIOApplicationEU) eu).getGeneralPermitTypeCd() != null) {
				generalPermitCds.add(((PTIOApplicationEU) eu)
						.getGeneralPermitTypeCd());
			}
		}
		return generalPermitCds;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ApplicationNote createApplicationNote(ApplicationNote applicationNote)
			throws DAOException {
		Transaction trans = null;
		ApplicationNote ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ret = createApplicationNote(applicationNote, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param applicationNote
	 * @param trans
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ApplicationNote createApplicationNote(
			ApplicationNote applicationNote, Transaction trans)
			throws DAOException {
		ApplicationNote ret = null;

		try {
			InfrastructureDAO infraDAO = infrastructureDAO(trans);
			ApplicationDAO applicationDAO = applicationDAO(trans);

			Note tempNote = infraDAO.createNote(applicationNote);

			if (tempNote != null) {
				ret = applicationNote;
				ret.setNoteId(tempNote.getNoteId());

				applicationDAO.addApplicationNote(ret.getApplicationId(),
						ret.getNoteId());
			} else {
				logger.error("Failed to insert Application Note for application "
						+ applicationNote.getApplicationId());
				throw new DAOException(
						"Failed to insert Application Note for application "
								+ applicationNote.getApplicationId());
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			logger.error("Failed to insert Application Note for application "
					+ applicationNote.getApplicationId(), e);
			throw e;
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ApplicationDocumentRef createApplicationDocument(
			ApplicationDocumentRef doc) throws DAOException {
		Transaction trans = null;
		ApplicationDocumentRef ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO applicationDAO = applicationDAO(trans);
			ret = applicationDAO.createApplicationDocument(doc);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PBRNotificationDocument createPBRNotificationDocument(
			PBRNotificationDocument doc) throws DAOException {
		Transaction trans = null;
		PBRNotificationDocument ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			appDAO.createPBRNotificationDocument(doc);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PBRNotificationDocument modifyPBRNotificationDocument(
			PBRNotificationDocument doc) throws DAOException {
		Transaction trans = null;
		PBRNotificationDocument ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			appDAO.modifyPBRNotificationDocument(doc);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removePBRNotificationDocument(PBRNotificationDocument doc)
			throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			removePBRNotificationDocument(doc, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	private void removePBRNotificationDocument(PBRNotificationDocument doc,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		DocumentDAO docDAO = documentDAO(trans);
		markDocumentAsTemp(doc, docDAO);
		appDAO.removePBRNotificationDocument(doc);

		// attempt to delete document if removed from portal
		if (isPortalApp()) {
			removeDocument(doc, docDAO);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public RPCRequestDocument createRPCRequestDocument(RPCRequestDocument doc)
			throws DAOException {
		Transaction trans = null;
		RPCRequestDocument ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			appDAO.createRPCRequestDocument(doc);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public RPCRequestDocument modifyRPCRequestDocument(RPCRequestDocument doc)
			throws DAOException {
		Transaction trans = null;
		RPCRequestDocument ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO appDAO = applicationDAO(trans);
			appDAO.modifyRPCRequestDocument(doc);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeRPCRequestDocument(RPCRequestDocument doc)
			throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			removeRPCRequestDocument(doc, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	private void removeRPCRequestDocument(RPCRequestDocument doc,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		DocumentDAO docDAO = documentDAO(trans);
		markDocumentAsTemp(doc, docDAO);
		appDAO.removeRPCRequestDocument(doc);

		// attempt to delete document if removed from portal
		if (isPortalApp()) {
			removeDocument(doc, docDAO);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyApplicationDocument(ApplicationDocumentRef doc)
			throws DAOException {
		Transaction trans = null;
		boolean ret = false;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO applicationDAO = applicationDAO(trans);
			ret = applicationDAO.modifyApplicationDocument(doc);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeApplicationDocument(ApplicationDocumentRef appDoc)
			throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			removeApplicationDocument(appDoc, trans, isPortalApp());
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	private void removeAppRequiredAttachmnetDoc(ApplicationDAO appDAO,
			Application application, ApplicationEU applicationEU, String typeCd)
			throws DAOException {
		Integer applicationId = null == application? null : application.getApplicationID();
		Integer appEuId = null == applicationEU? null : applicationEU.getApplicationEuId();
		Integer applicationDocId = appDAO.retrieveRequiredApplicationDocId(
				applicationId, appEuId, typeCd);

		ApplicationDocumentRef docRef = appDAO
				.retrieveApplicationDocument(applicationDocId);

		if (docRef.getDocumentId() == null) {
			appDAO.removeApplicationDocumentDocId(applicationDocId);
			appDAO.removeReqruiedAttachmnetApplicationDoc(applicationId,
					appEuId, typeCd);
			if (null == applicationEU) {
				application.removeDocument(docRef);
			} else {
				applicationEU.getEuDocuments().remove(docRef);
			}
		} else {
			appDAO.changeReqruiedAttachmnetToOption(applicationId, appEuId,
					typeCd);
		}
	}

	@Override
	public void changeReqruiedAttachmnetToOption(Integer applicationId,
			Integer appEuId, String typeCd) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));

		appDAO.changeReqruiedAttachmnetToOption(applicationId, appEuId, typeCd);
	}

	private void removeApplicationDocument(ApplicationDocumentRef appDoc,
			Transaction trans, boolean removeFiles) throws DAOException {
		ApplicationDAO applicationDAO = applicationDAO(trans);
		DocumentDAO docDAO = documentDAO(trans);
		Document publicDoc = null;
		Document tsDoc = null;
		boolean isPublicDocMarkedTemp = false;
		boolean isTSDocMarkedTemp = false;
		
		// mark document record for deletion later
		if (appDoc.getDocumentId() != null) {
			publicDoc = docDAO.retrieveDocument(appDoc.getDocumentId());
			// document may be referenced by more than one entity (EU)
			// only mark document as temporary if no other entity is referencing
			// it
			if (applicationDAO.retrieveAttachmentReferenceCount(
					appDoc.getApplicationDocId(), appDoc.getDocumentId()) == 0) {
				isPublicDocMarkedTemp = true;
				markDocumentAsTemp(publicDoc, docDAO);
			}
		}
		if (appDoc.getTradeSecretDocId() != null) {
			tsDoc = docDAO.retrieveDocument(appDoc.getTradeSecretDocId());
			// document may be referenced by more than one entity (EU)
			// only mark document as temporary if no other entity is referencing
			// it
			if (applicationDAO.retrieveAttachmentReferenceCount(
					appDoc.getApplicationDocId(), appDoc.getTradeSecretDocId()) == 0) {
				isTSDocMarkedTemp = true;
				markDocumentAsTemp(tsDoc, docDAO);
			}
		}
		if (isPortalApp() && removeFiles) {
			if (isPublicDocMarkedTemp && publicDoc != null) {
				docDAO.removeDocument(publicDoc);
			}
			if (isTSDocMarkedTemp && tsDoc != null) {
				docDAO.removeDocument(tsDoc);
			}
		}
		
		applicationDAO.removeApplicationDocument(appDoc.getApplicationDocId());
	}

	private void removeApplicationDocument(ApplicationDocumentRef appDoc,
			Transaction trans) throws DAOException {
		removeApplicationDocument(appDoc, trans, false);
	}
	
	private void markDocumentAsTemp(Document doc, DocumentDAO docDAO)
			throws DAOException {
		if (doc != null) {
			doc.setTemporary(true);
			docDAO.modifyDocument(doc);
		}
	}

	// TODO fix calls to this method or remove it
	// the implementation is commented out here rather than commenting out
	// calls to the method for the sake of simplicity (and completeness).
	// deleting documents causes problems if the document is a copy of an
	// attachment whose original version exists in another EU
	private void removeDocument(Document doc, DocumentDAO docDAO) {
		// if (doc != null) {
		// try {
		// docDAO.removeDocument(doc);
		// } catch (IOException e) {
		// // if deleting the actual document fails, that's ok
		// // at least we tried!
		// logger.debug("Failed deleting document: " + doc.getPath());
		// }
		// }
	}

	/**
	 * Mark all documents associated with the application as temp and then
	 * attempt to remove the documents from the system. This is meant to be
	 * called when deleting an application from the portal since there is no way
	 * for the document clean up daemon to find documents that were once
	 * associated with a deleted application.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeApplicationDocuments(Application app, Transaction trans, boolean deleteAttachmentFiles)
			throws DAOException {
		if (app instanceof PBRNotification) {
			for (PBRNotificationDocument pbrDoc : ((PBRNotification) app).getPbrDocuments()) {
				removePBRNotificationDocument(pbrDoc, trans);
			}
		} else if (app instanceof RPCRequest) {
			for (RPCRequestDocument rpcDoc : ((RPCRequest) app).getRpcDocuments()) {
				removeRPCRequestDocument(rpcDoc, trans);
			}
		} else {
			for (ApplicationDocumentRef appDoc : app.getDocuments()) {
				removeApplicationDocument(appDoc, trans, deleteAttachmentFiles);
			}
			for (ApplicationEU appEU : app.getEus()) {
				for (ApplicationDocumentRef appDoc : appEU.getEuDocuments()) {
					removeApplicationDocument(appDoc, trans, deleteAttachmentFiles);
				}
			}
		}
	}

	@Override
	public void removeApplicationDocumentDoc(ApplicationDocumentRef appDoc)
			throws DAOException {
		
		Transaction trans = TransactionFactory.createTransaction();
		ApplicationDAO appDAO = applicationDAO(trans);

		try {
			
			appDAO.setApplicationValidatedFlag(appDoc.getApplicationId(), false);

			if (appDoc.getApplicationEUId() != null) {
				appDAO.setApplicationEUValidatedFlag(
						appDoc.getApplicationEUId(), false);
			}

			removeApplicationDocument(appDoc, trans, isPortalApp());

			trans.complete();

		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public ApplicationNote[] retrieveApplicationNotes(int applicationId)
			throws DAOException {
		ApplicationNote[] ret = null;

		ApplicationDAO facilityDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));

		ret = facilityDAO.retrieveApplicationNotes(applicationId);

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyApplicationNote(ApplicationNote applicationNote)
			throws DAOException {
		boolean ret = false;
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			ret = modifyApplicationNote(applicationNote, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyApplicationNote(ApplicationNote applicationNote,
			Transaction trans) throws DAOException {
		boolean ret = false;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ret = infraDAO.modifyNote(applicationNote);
		return ret;
	}

	/**
	 * Retrieve a "temporary" TVApplicableReq object. This object will not be
	 * stored in the database, but will have a valid, unique tvApplicableReqId
	 * value.
	 * 
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public TVApplicableReq getTempApplicableReq() throws DAOException {
		TVApplicableReq applicableReq = new TVApplicableReq();
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		applicableReq.setTvApplicableReqId(appDAO
				.generateApplicableRequirementSeqNo());
		return applicableReq;
	}

	/**
	 * Create a copy of a TVApplicableReq object. The new object will not be
	 * stored in the database, but will have a valid, unique tvApplicableReqId
	 * value.
	 * 
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public TVApplicableReq getApplicableReqCopy(TVApplicableReq src)
			throws DAOException {
		TVApplicableReq applicableReq = new TVApplicableReq(src);
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		applicableReq.setTvApplicableReqId(appDAO
				.generateApplicableRequirementSeqNo());

		for (TVCompliance compliance : src.getComplianceReqs()) {
			TVCompliance clone = new TVCompliance(compliance);
			clone.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
			applicableReq.getComplianceReqs().add(clone);
		}

		for (TVComplianceObligations obligations : src
				.getComplianceObligationsReqs()) {
			TVComplianceObligations clone = new TVComplianceObligations(
					obligations);
			clone.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
			applicableReq.getComplianceObligationsReqs().add(clone);
		}

		for (TVProposedExemptions exemptions : src.getProposedExemptionsReqs()) {
			TVProposedExemptions clone = new TVProposedExemptions(exemptions);
			clone.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
			applicableReq.getProposedExemptionsReqs().add(clone);
		}

		for (TVProposedAltLimits limits : src.getProposedAltLimitsReqs()) {
			TVProposedAltLimits clone = new TVProposedAltLimits(limits);
			clone.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
			applicableReq.getProposedAltLimitsReqs().add(clone);
		}

		for (TVProposedTestChanges changes : src.getProposedTestChangesReqs()) {
			TVProposedTestChanges clone = new TVProposedTestChanges(changes);
			clone.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
			applicableReq.getProposedTestChangesReqs().add(clone);
		}

		return applicableReq;
	}

	private Integer createCorrectedAppWorkflow(Application app, int userId,
			Transaction trans) throws RemoteException {
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
		String taskName = "Facility Corrected Application Received";
		if (app.isApplicationAmended()) {
			taskName = "AQD Amended Application";
		}
		data.put("Task Name", taskName);
		data.put(
				"Notes",
				"You MUST attach this corrected copy of the application "
						+ " (Application Number "
						+ app.getApplicationNumber()
						+ ") "
						+ "to the permit object it belongs with, then complete this task "
						+ "to remove it from your To-Do list. Attach the application in the "
						+ "Permit Detail page, under the section: Application/Request "
						+ "Administrative Permit Modifications. Once you open this section, "
						+ "enter this application number in the text box and clicking Add. ");
		
		if (ApplicationTypeDef.PTIO_APPLICATION.equals(app.getApplicationTypeCD())) {
			data.put(WorkFlowManager.ROLE_DISCRIMINATOR, PermitTypeDef.NSR);
		} else
		if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(app.getApplicationTypeCD())) {
			data.put(WorkFlowManager.ROLE_DISCRIMINATOR, PermitTypeDef.TV_PTO);			
		}
		
		Timestamp startDate = new Timestamp(System.currentTimeMillis());
		Timestamp dueDt = null;
		String rush = "N";
		ReadWorkFlowService wfBO = null;
		try {
			wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
		} catch (ServiceFactoryException e) {
			throw new RemoteException(
					"Exception getting workflow service for application "
							+ app.getApplicationNumber(), e);
		}
		Integer workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(
				WorkFlowProcess.CORRECTED_APPLICATION);

		WorkFlowManager wfm = new WorkFlowManager();
		WorkFlowResponse resp = wfm.submitProcess(workflowId,
				app.getApplicationID(), app.getFacility().getFpId(), userId,
				rush, startDate, dueDt, data, trans);

		if (resp.hasError() || resp.hasFailed()) {
			StringBuffer error = new StringBuffer(
					"Error creating workflow for corrected application "
							+ app.getApplicationNumber() + ": ");
			for (String msg : resp.getErrorMessages()) {
				error.append(msg + " ");
			}
			if (resp.getRecommendationMessages().length > 0) {
				error.append("; Recommendations: ");
				for (String recomm : resp.getRecommendationMessages()) {
					error.append(recomm + " ");
				}
			}
			throw new DAOException(
					"createCorrectedAppWorkflow FAILED. Unable to create workflow.");
		}

		return workflowId;
	}

	/**
	 * Define the collection of pollutants (and their labels) that can be
	 * displayed in the PTIO application's emissions information table.
	 * 
	 * @return
	 */
	public static HashMap<String, String> getPTIOCapPollutantDefs() {
		if (ptioCapPollutantDefs == null) {
			ptioCapPollutantDefs = new HashMap<String, String>();
			ptioCapPollutantDefs.put(PollutantDef.PE_CD, PollutantDef.PE_DSC);
			ptioCapPollutantDefs.put(PollutantDef.PM10_CD,
					PollutantDef.PM10_DSC);
			ptioCapPollutantDefs.put(PollutantDef.PM25_CD,
					PollutantDef.PM25_DSC);
			ptioCapPollutantDefs.put(PollutantDef.SO2_CD, PollutantDef.SO2_DSC);
			ptioCapPollutantDefs.put(PollutantDef.NOX_CD, PollutantDef.NOX_DSC);
			ptioCapPollutantDefs.put(PollutantDef.CO_CD, PollutantDef.CO_DSC);
			// ptioCapPollutantDefs.put(PollutantDef.OC_CD,
			// PollutantDef.OC_DSC);
			ptioCapPollutantDefs.put(PollutantDef.VOC_CD, PollutantDef.VOC_DSC);
			ptioCapPollutantDefs.put(PollutantDef.PB_CD, PollutantDef.PB_DSC);
			ptioCapPollutantDefs.put(PollutantDef.HTOT_CD,
					PollutantDef.HTOT_DSC);
			ptioCapPollutantDefs.put(PollutantDef.FL_CD, PollutantDef.FL_DSC);
			ptioCapPollutantDefs.put(PollutantDef.H2S_CD, PollutantDef.H2S_DSC);
			ptioCapPollutantDefs.put(PollutantDef.HG_CD, PollutantDef.HG_DSC);
			ptioCapPollutantDefs.put(PollutantDef.TRS_CD, PollutantDef.TRS_DSC);
			ptioCapPollutantDefs.put(PollutantDef.SAM_CD, PollutantDef.SAM_DSC);
			/*
			 * ptioCapPollutantDefs.put(PollutantDef.HMAX_CD,
			 * PollutantDef.HMAX_DSC);
			 */
		}
		return ptioCapPollutantDefs;
	}

	public static HashMap<String, String> getPTIOMaterialUsedDefs() {
		if (ptioMaterialUsedDefs == null) {
			ptioMaterialUsedDefs = new HashMap<String, String>();
			ptioMaterialUsedDefs.put(MaterialUsedDef.SAND_CD,
					MaterialUsedDef.SAND_DSC);
			ptioMaterialUsedDefs.put(MaterialUsedDef.POCE_CD,
					MaterialUsedDef.POCE_DSC);
			ptioMaterialUsedDefs.put(MaterialUsedDef.AGGR_CD,
					MaterialUsedDef.AGGR_DSC);
			ptioMaterialUsedDefs.put(MaterialUsedDef.FLYA_CD,
					MaterialUsedDef.FLYA_DSC);
			ptioMaterialUsedDefs.put(MaterialUsedDef.OTHE_CD,
					MaterialUsedDef.OTHE_DSC);
			ptioMaterialUsedDefs.put(MaterialUsedDef.LIME_CD,
					MaterialUsedDef.LIME_DSC);
		}
		return ptioMaterialUsedDefs;
	}

	public static List<String> getPTIOCapPollutantCodesOrdered() {
		if (ptioCapPollutantCodesOrdered == null) {
			ptioCapPollutantCodesOrdered = new ArrayList<String>();
			ptioCapPollutantCodesOrdered.add(PollutantDef.PE_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.PM10_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.PM25_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.SO2_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.NOX_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.CO_CD);
			// ptioCapPollutantCodesOrdered.add(PollutantDef.OC_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.VOC_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.PB_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.HTOT_CD);
			// ptioCapPollutantCodesOrdered.add(PollutantDef.HMAX_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.FL_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.H2S_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.HG_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.TRS_CD);
			ptioCapPollutantCodesOrdered.add(PollutantDef.SAM_CD);

		}
		return ptioCapPollutantCodesOrdered;
	}

	public static List<String> getPTIOMaterialUsedCodesOrdered() {
		if (ptioMaterialUsedCodesOrdered == null) {
			ptioMaterialUsedCodesOrdered = new ArrayList<String>();
			ptioMaterialUsedCodesOrdered.add(MaterialUsedDef.SAND_CD);
			ptioMaterialUsedCodesOrdered.add(MaterialUsedDef.POCE_CD);
			ptioMaterialUsedCodesOrdered.add(MaterialUsedDef.AGGR_CD);
			ptioMaterialUsedCodesOrdered.add(MaterialUsedDef.FLYA_CD);
			ptioMaterialUsedCodesOrdered.add(MaterialUsedDef.OTHE_CD);
			ptioMaterialUsedCodesOrdered.add(MaterialUsedDef.LIME_CD);
		}
		return ptioMaterialUsedCodesOrdered;
	}

	public static List<String> getTVCapPollutantCds() {
		long now = System.currentTimeMillis();
		if (tvCapPollutantCodes == null || (now - lastUpdate) > 60 * 60 * 1000) {
			// create select list of pollutants based on pollutantType
			DefSelectItems pollutantDefItems = PollutantDef.getData()
					.getItems();
			// Note: TreeMap is used to sort pollutants by label
			TreeMap<String, String> pollutantDefs = new TreeMap<String, String>();
			for (SelectItem item : pollutantDefItems.getCurrentItems()) {
				PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
						.getItem(item.getValue().toString());
				if (pollutantDef.isTvptoAppSec12()) {
					pollutantDefs.put(item.getLabel(), item.getValue()
							.toString());
				}
			}

			tvCapPollutantCodes = new ArrayList<String>();
			for (String pollutantName : pollutantDefs.keySet()) {
				tvCapPollutantCodes.add(pollutantDefs.get(pollutantName));
			}
			lastUpdate = now;
		}
		return tvCapPollutantCodes;
	}

	/**
	 * Get list of Emissions for the PTIO Application Emissions Information
	 * table.
	 * 
	 * @param appEUId
	 * @return
	 */
	private List<ApplicationEUEmissions> getPTIOCAPEmissions(Integer appEUId) {
		List<ApplicationEUEmissions> capEmissions = new ArrayList<ApplicationEUEmissions>();
		for (String pollutantCd : getPTIOCapPollutantDefs().keySet()) {
			capEmissions.add(new ApplicationEUEmissions(appEUId, pollutantCd,
					ApplicationEUEmissionTableDef.CAP_TABLE_CD));
		}
		return capEmissions;
	}

	private List<TVApplicationEUEmissions> getTVCAPEmissions(Integer appEUId,
			Integer opScenarioId) {
		List<TVApplicationEUEmissions> capEmissions = new ArrayList<TVApplicationEUEmissions>();
		for (String pollutantCd : getTVCapPollutantCds()) {
			capEmissions.add(new TVApplicationEUEmissions(appEUId,
					opScenarioId, pollutantCd,
					ApplicationEUEmissionTableDef.CAP_TABLE_CD));
		}
		return capEmissions;
	}

	public void zipApplicationFiles(Application app,
			Document facilityDocument, ZipOutputStream zos,
			boolean hideTradeSecret, boolean includeAllAttachments, boolean readOnly) throws IOException {

		// add attachments to zip output stream
		zipApplicationAttachments(app, !hideTradeSecret, zos, includeAllAttachments, readOnly);

		// generate file with application data
		String appName = app.getApplicationNumber() + ".pdf";
		TmpDocument appDoc = new TmpDocument();
		boolean hasTS = getApplicationReportAsStream(app, hideTradeSecret,
				appDoc, true, includeAllAttachments);
		InputStream appIS = DocumentUtil.getDocumentAsStream(appDoc.getPath());

		if (appIS != null) {
			addEntryToZip(appName, appIS, zos);
			appIS.close();
		} else {
			logger.error("Error generating application file for "
					+ app.getApplicationNumber());
		}

		// generate file with facility data
		String facName = app.getFacilityId() + ".pdf";
		InputStream facIS = DocumentUtil.getDocumentAsStream(facilityDocument
				.getPath());
		if (facIS != null) {
			addEntryToZip(facName, facIS, zos);
			facIS.close();
		} else {
			logger.error("Error generating application file for "
					+ app.getApplicationNumber());
		}
	}

	private void zipApplicationAttachments(Application app,
			boolean includeTradeSecretFiles, ZipOutputStream zos, boolean includeAllAttachments, boolean readOnly)
			throws FileNotFoundException, IOException {
		
		DocumentService docBO = null;
		try {
			docBO = ServiceFactory.getInstance().getDocumentService();
		} catch (ServiceFactoryException e) {
			logger.error(
					"Exception accessing DocumentService for "
							+ app.getApplicationNumber(), e);
			throw new IOException("Exception accessing DocumentService for "
					+ app.getApplicationNumber());
		}

		if (app instanceof PBRNotification) {
			for (PBRNotificationDocument doc : ((PBRNotification) app)
					.getPbrDocuments()) {
				addAttachmentoZipFile(doc, zos);
			}
		} else if (app instanceof RPCRequest) {
			for (RPCRequestDocument doc : ((RPCRequest) app).getRpcDocuments()) {
				addAttachmentoZipFile(doc, zos);
			}
		} else {
			List<ApplicationDocumentRef> attachmentList = new ArrayList<ApplicationDocumentRef>();
			for (ApplicationDocumentRef appDoc : app.getDocuments()) {
				// String s = "no public document";
				// if(appDoc.getPublicDoc() != null) s =
				// appDoc.getPublicDoc().getBasePath();
				// logger.error("Debug #2966: zipApplicationAttachments: attachment description "
				// + appDoc.getDescription() + ", attachment basePath " + s);
				// don't include documents added after application was submitted
				if (app.getSubmittedDate() != null
						&& appDoc.getLastModifiedTS() != null
						&& appDoc.getLastModifiedTS().after(
								app.getSubmittedDate())) {
					logger.debug("Excluding document "
							+ appDoc.getApplicationDocId()
							+ " from application zip file. Document last modified date ("
							+ appDoc.getLastModifiedTS()
							+ ") is after application submission date ("
							+ app.getSubmittedDate() + ")");
					continue;
				}
				attachmentList.add(appDoc);
			}

			HashMap<Integer, String> euMap = new HashMap<Integer, String>();
			for (ApplicationEU eu : app.getIncludedEus()) {
				for (ApplicationDocumentRef appDoc : eu.getEuDocuments()) {
					// don't include documents added after application was
					// submitted
					if (app.getSubmittedDate() != null
							&& appDoc.getLastModifiedTS() != null
							&& appDoc.getLastModifiedTS().after(
									app.getSubmittedDate())) {
						logger.debug("Excluding EU document "
								+ appDoc.getApplicationDocId()
								+ " from application zip file. Document last modified date ("
								+ appDoc.getLastModifiedTS()
								+ ") is after application submission date ("
								+ app.getSubmittedDate() + ")");
						continue;
					}
					attachmentList.add(appDoc);
				}
				euMap.put(eu.getApplicationEuId(), eu.getFpEU().getEpaEmuId());
			}

			// add attachments to zip file
			HashSet<Integer> docIdSet = new HashSet<Integer>();
			for (ApplicationDocumentRef attachment : attachmentList) {
				if (attachment.getDocumentId() != null
						&& !docIdSet.contains(attachment.getDocumentId())) {
					docIdSet.add(attachment.getDocumentId());
					Document doc = docBO.getDocumentByID(
							attachment.getDocumentId(), readOnly);
					addAttachmentoZipFile(attachment, doc, euMap, zos, app.getApplicationTypeCD(), false);
				}
				if (includeTradeSecretFiles
						&& attachment.getTradeSecretDocId() != null
						&& !docIdSet.contains(attachment.getTradeSecretDocId()) && (includeAllAttachments || app.getSubmittedDate() != null || attachment.isTradeSecretAllowed())) {
					docIdSet.add(attachment.getTradeSecretDocId());
					Document doc = docBO.getDocumentByID(
							attachment.getTradeSecretDocId(), readOnly);
					addAttachmentoZipFile(attachment, doc, euMap, zos, app.getApplicationTypeCD(), includeTradeSecretFiles);
				}
			}
		}
	}

	private void zipApplicationPDFReportFiles(Application app,
			ZipOutputStream zos) throws FileNotFoundException, IOException {
		ApplicationDocument nonTSDoc = getApplicationReportDocument(app, true);
		ApplicationDocument tsDoc = getApplicationReportDocument(app, false);
		List<ApplicationDocument> docList = new ArrayList<ApplicationDocument>();
		if (nonTSDoc != null && DocumentUtil.canRead(nonTSDoc.getPath())) {
			docList.add(nonTSDoc);
		}
		if (tsDoc != null && DocumentUtil.canRead(tsDoc.getPath())) {
			docList.add(tsDoc);
		}

		// add attachments to zip file
		for (ApplicationDocument doc : docList) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
			} catch (FileNotFoundException e) {
				String errorMsg = doc.getDescription();
				if (errorMsg.length() > 50) {
					errorMsg = errorMsg.substring(0, 47) + "...";
				}
				throw new FileNotFoundException(errorMsg);
			}
			addEntryToZip(doc.getDescription(), docIS, zos);

			if (docIS != null) {
				docIS.close();
			}
		}
	}

	private void addAttachmentoZipFile(Attachment doc, ZipOutputStream zos)
			throws IOException {
		if (doc != null && !doc.isTemporary()) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
			} catch (FileNotFoundException e) {
				logger.error("Cannot find file in path: " + doc.getPath());
				String errorMsg = doc.getDescription();
				if (errorMsg.length() > 50) {
					errorMsg = errorMsg.substring(0, 47) + "...";
				}
				throw new FileNotFoundException(errorMsg);
			}
			addEntryToZip(getNameForAttachment(doc), docIS, zos);

			if (docIS != null) {
				docIS.close();
			}
		} else if (doc == null) {
			logger.error("No document found");
		} else {
			logger.warn("Not zipping temporary document with id "
					+ doc.getDocumentID());
		}
	}

	private void addAttachmentoZipFile(ApplicationDocumentRef attachment,
			Document doc, HashMap<Integer, String> euMap, ZipOutputStream zos, String applicationTypeCd, boolean includeTradeSecretFiles)
			throws IOException {
		String epaEmuId = null;
		if (attachment.getApplicationEUId() == null) {
			epaEmuId = null;
		} else {
			epaEmuId = euMap.get(attachment.getApplicationEUId());
		}
		// got rid of check for "temporary" documents
		// if file gets here, it should not be temporary and should be ok to
		// include
		if (doc != null) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
			} catch (FileNotFoundException e) {
				String errorMsg = attachment.getDescription();
				if (errorMsg.length() > 50) {
					errorMsg = errorMsg.substring(0, 47) + "...";
				}
				if (attachment.getApplicationEUId() == null) {
					errorMsg = errorMsg + " in main application";
				} else {
					errorMsg = errorMsg + " in EU: "
							+ euMap.get(attachment.getApplicationEUId());
				}
				throw new FileNotFoundException(errorMsg);
			}
			addEntryToZip(
					getNameForDoc(attachment.getApplicationDocumentTypeCD(),
							doc.getDocumentID().toString(), includeTradeSecretFiles, doc.getExtension(), applicationTypeCd, epaEmuId),
					docIS, zos);

			if (docIS != null) {
				docIS.close();
			}
		} else if (doc == null) {
			logger.error("No document found with id "
					+ attachment.getDocumentId());
		} else {
			logger.warn("Not zipping temporary document with id "
					+ attachment.getDocumentId());
		}
	}

	/**
	 * Create a more descriptive name for attachments to be included in a zip
	 * file.
	 * 
	 * @param docTypeCd
	 *            document type code.
	 * @param doc
	 *            document record.
	 * @return
	 */
	private String getNameForDoc(String docTypeCd, String docId, boolean ts,
			String extension, String applicationTypeCd, String epaEmuId) {
		StringBuffer docName = new StringBuffer();
		if (epaEmuId != null) {
			docName.append(epaEmuId + "_");
		}
		
		if (docTypeCd != null) {
			if (applicationTypeCd.equalsIgnoreCase(ApplicationTypeDef.TITLE_V_APPLICATION)){
				docName.append(TVApplicationDocumentTypeDef.getData().getItems()
						.getItemDesc(docTypeCd)
						+ "_");
			} else {
				docName.append(ApplicationDocumentTypeDef.getData().getItems()
						.getItemDesc(docTypeCd)
						+ "_");
			}
		}
		docName.append(docId);
		if(ts) docName.append("_TS");
		if (extension != null && extension.length() > 0) {
			docName.append("." + extension);
		}
		return docName.toString();
	}

	private String getNameForAttachment(Attachment doc) {
		StringBuffer docName = new StringBuffer();
		if (doc.getDocTypeCd() != null) {
			docName.append(PBRNotifDocTypeDef.getData().getItems()
					.getItemDesc(doc.getDocTypeCd())
					+ "_");
		}
		docName.append(doc.getDocumentID());
		if (doc.getExtension() != null && doc.getExtension().length() > 0) {
			docName.append("." + doc.getExtension());
		}
		return docName.toString();
	}

	/**
	 * Return pdf version of application as an InputStream.
	 * 
	 * @param app
	 * @param hideTradeSecret
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ApplicationDocument createApplicationReportDocument(Application app,
			String userName, boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		ApplicationDocument appDoc = null;
		try {
			appDoc = getApplicationReportDocument(app, hideTradeSecret);
			OutputStream os = DocumentUtil.createDocumentStream(appDoc
					.getPath());
			try {
				ApplicationPdfGenerator generator = ApplicationPdfGenerator
						.getGeneratorForClass(app.getClass());
				generator.setHapValueFormat(new DecimalFormat(
						getHapEmissionsValueFormat()));
				generator.setUserName(userName);
				// generator.setAttestationAttached(userName != null
				// && appHasAttestationAttachment(app));
				generator.generatePdf(app, os, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);

				//if (!hideTradeSecret) {
				//	PdfGeneratorBase.addTradeSecretWatermark(appDoc.getPath());
				//}
			} catch (DocumentException e) {
				logger.error(
						"Exception writing application report to stream for "
								+ app.getApplicationNumber(), e);
				throw new IOException(
						"Exception writing application report to stream for "
								+ app.getApplicationNumber());
			}
			os.close();
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting application report as stream for "
							+ app.getApplicationNumber(), e);
		}
		return appDoc;
	}

	private ApplicationDocument getApplicationReportDocument(Application app,
			boolean hideTradeSecret) {
		ApplicationDocument appDoc = new ApplicationDocument();
		String submittedDocDesc = "Printable View of What Will Be Submitted from Data Entered";
		if (app.getSubmittedDate() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			submittedDocDesc = "Printable View of Application Data Submitted on "
					+ dateFormat.format(app.getSubmittedDate());
		}
		// Set the path elements of the temp doc.
		appDoc.setDescription(submittedDocDesc
				+ (hideTradeSecret ? "" : " with trade secret data"));
		appDoc.setFacilityID(app.getFacilityId());
		appDoc.setApplicationId(app.getApplicationID());
		appDoc.setTemporary(true);
		appDoc.setOverloadFileName("Application" + app.getApplicationNumber()
				+ (hideTradeSecret ? "" : "_TS") + ".pdf");
		return appDoc;
	}

	/**
	 * Return pdf file RO can sign as attestation document.
	 * 
	 * @param app
	 * @param hideTradeSecret
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ApplicationDocument createApplicationAttestationDocument(
			Application app) throws DAOException {
		ApplicationDocument appDoc = null;
		try {
			appDoc = getApplicationAttestationDocument(app);
			OutputStream os = DocumentUtil.createDocumentStream(appDoc
					.getPath());
			try {
				ApplicationPdfGenerator generator = ApplicationPdfGenerator
						.getGeneratorForClass(app.getClass());
				generator.setAttestationOnly(true);
				generator.generatePdf(app, os, true, false, true);
			} catch (DocumentException e) {
				logger.error(
						"Exception writing application report to stream for "
								+ app.getApplicationNumber(), e);
				throw new IOException(
						"Exception writing application report to stream for "
								+ app.getApplicationNumber());
			}
			os.close();
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting application report as stream for "
							+ app.getApplicationNumber(), e);
		}
		return appDoc;
	}

	private ApplicationDocument getApplicationAttestationDocument(
			Application app) {
		ApplicationDocument appDoc = new ApplicationDocument();
		appDoc.setFacilityID(app.getFacilityId());
		appDoc.setApplicationId(app.getApplicationID());
		appDoc.setTemporary(true);
		appDoc.setOverloadFileName("AttestationForApp"
				+ app.getApplicationNumber() + ".pdf");
		appDoc.setDescription("Attestation document for application "
				+ app.getApplicationNumber());
		return appDoc;
	}

	/**
	 * Generate a pdf file containing data from the application and create a
	 * temporary Document object refrencing this file.
	 * 
	 * @param app
	 * @param hideTradeSecret
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean getApplicationReportAsStream(Application app,
			boolean hideTradeSecret, TmpDocument appDoc, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		boolean hasTS = false;
		try {
			// Set the path elements of the temp doc.
			appDoc.setDescription("Current application pdf file");
			appDoc.setFacilityID(app.getFacilityId());
			appDoc.setTemporary(true);
			appDoc.setTmpFileName(app.getApplicationNumber() + ".pdf");

			// make sure temporary directory exists
			DocumentUtil.mkDirs(appDoc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(appDoc
					.getPath());
			hasTS = writeApplicationReportToStream(app, hideTradeSecret, os, isSubmittedPDFDoc, includeAllAttachments);
			os.close();
			//if (!hideTradeSecret) {
			//	PdfGeneratorBase.addTradeSecretWatermark(appDoc.getPath());
			//}
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting application report as stream for "
							+ appDoc.getPath(), e);
		} catch (Exception ex) {
			logger.error(
					"Cannot generate application report for "
							+ appDoc.getPath(), ex);
			throw new DAOException("Cannot generate application report", ex);
		}
		return hasTS;
	}

	/**
	 * Write pdf version of application to an output stream.
	 * 
	 * @param app
	 * @param hideTradeSecret
	 * @param os
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean writeApplicationReportToStream(Application app,
			boolean hideTradeSecret, OutputStream os, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws IOException {
		boolean hasTS = false;
		try {
			ApplicationPdfGenerator generator = ApplicationPdfGenerator
					.getGeneratorForClass(app.getClass());
			generator.setHapValueFormat(new DecimalFormat(
					getHapEmissionsValueFormat()));
			generator.generatePdf(app, os, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
			hasTS = generator.isContainsTradeSecret(); // DENNIS
		} catch (DocumentException e) {
			logger.error("Exception writing application report to stream for "
					+ app.getApplicationNumber(), e);
			throw new IOException(
					"Exception writing application report to stream for "
							+ app.getApplicationNumber());
		}
		return hasTS;
	}

	/**
	 * Retrieve list of permits available to be modified by <code>request</code>
	 * . The rpcTypCd field must be set prior to invoking this method.
	 * 
	 * @param request
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public List<Permit> retrievePermitsForRPCRequest(String rpcTypeCd,
			String facilityId) throws DAOException {
		ArrayList<Permit> permits = new ArrayList<Permit>();
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.READONLY_SCHEMA));
		PermitDAO permitDAO = permitDAO(getSchema(CommonConst.READONLY_SCHEMA));

		// find permits whose permit type code is applicable for the specified
		// rpc type code where the permit issuance is 'final' and at least one
		// permit eu is 'Active'
		if (rpcTypeCd != null && facilityId != null) {
			for (String permitTypeCd : appDAO
					.retrievePermitTypeForRPCType(rpcTypeCd)) {
				for (Permit permit : permitDAO.searchPermits(null, null, null,
						permitTypeCd, null, null, null, null, facilityId, null, null, null,
						null, null, null, true, null)) {
					// retrieve full set of permit data
					permit = permitDAO.retrievePermit(permit.getPermitID());
					if (!PermitIssuanceTypeDef.Final.equals(permit
							.getPermitGlobalStatusCD())) {
						// only include permits issued as 'Final'
						continue;
					}
					euGroupLoop: for (PermitEUGroup euGroup : permit
							.getEuGroups()) {
						for (PermitEU permitEU : euGroup.getPermitEUs()) {
							if (isPermitEUActive(permitEU)) {
								permits.add(permit);
								break euGroupLoop;
							}
						}
					}
				}
			}
		}
		return permits;
	}

	/**
	 * Check to see if permitEu is active. A permit EU is active if its status
	 * code is set to Active. It may seem like overkill to have a function to do
	 * such a simple thing, but the definition for 'acivtive' used to include
	 * Extended and Expired as well, so this method was created. It made more
	 * sense to keep the method rather tha delete it and make a bunch of code
	 * changes.
	 * 
	 * @param permitEU
	 * @return
	 */
	public static boolean isPermitEUActive(PermitEU permitEU) {
		return permitEU != null
				&& (PermitStatusDef.ACTIVE.equals(permitEU.getPermitStatusCd()));
	}

	/**
	 * Return the reason <code>appEU</code> cannot be included in
	 * <code>app</code>.
	 * 
	 * @param app
	 *            the application.
	 * @param appEU
	 *            the application EU.
	 * @return reason why appEU cannot be included in app.
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public String retrieveReasonEUNotIncludable(Application app,
			ApplicationEU appEU) throws DAOException {
		String reason = null;
		if (app instanceof RPCRequest || app instanceof RPERequest
				|| app instanceof RPRRequest) {
			if (!(app instanceof RPRRequest)
					&& appEU.getFpEU().getExemptStatusCd() != null
					&& !appEU.getFpEU().getExemptStatusCd()
							.equals(ExemptStatusDef.NA)) {
				reason = "EU exempt status is set to "
						+ ExemptStatusDef
								.getData()
								.getItems()
								.getItemDesc(
										appEU.getFpEU().getExemptStatusCd())
						+ " in the Facility inventory";
			} else if (app.getPermitId() != null) {
				PermitDAO permitDAO = permitDAO(getSchema(CommonConst.READONLY_SCHEMA));
				Permit permit = permitDAO.retrievePermit(app.getPermitId());
				euGroupLoop: for (PermitEUGroup permitEUGroup : permit
						.getEuGroups()) {
					for (PermitEU permitEU : permitEUGroup.getPermitEUs()) {
						if (permitEU.getFpEU().getCorrEpaEmuId()
								.equals(appEU.getFpEU().getCorrEpaEmuId())) {
							reason = "EU permit status is "
									+ PermitStatusDef
											.getData()
											.getItems()
											.getItemDesc(
													permitEU.getPermitStatusCd());
							break euGroupLoop;
						}
					}
				}
			}
		}
		return reason;
	}

	/**
	 * Special processing is needed to create the EUs for requests for
	 * modifications to permits. The set of EUs in the application is limited to
	 * the EUs in the affected permit.
	 * 
	 * @param request
	 * @param trans
	 * @throws DAOException
	 */
	private void createPermitModRequestEUs(Application request,
			Transaction trans) throws DAOException {
		Integer permitId = request.getPermitId();
		if (permitId != null && request.getEus().size() == 0) {
			PermitDAO permitDAO = permitDAO(getSchema(CommonConst.READONLY_SCHEMA));
			Permit permit = permitDAO.retrievePermit(permitId);
			boolean excludeEU = true; // exclude EUs from application by default
			if (request instanceof RPCRequest) {
				// exclude EUs for RPC requests unlete permit type is PTO
				excludeEU = !permit.getPermitType()
						.equals(PermitTypeDef.TV_PTO);
			} else if (request instanceof RPERequest) {
				excludeEU = false; // include all EUs for RPE requests
			}

			for (PermitEUGroup euGroup : permit.getEuGroups()) {
				for (PermitEU permitEU : euGroup.getPermitEUs()) {
					ApplicationEU appEU = createApplicationEUFromPermitEU(
							request, permitEU, excludeEU, trans);
					if (appEU != null) {
						request.addEu(appEU);
					}
				}
			}
			// for off-permit change requests, include all EUs in facility
			if (request instanceof RPCRequest
					&& RPCTypeDef.TV_OFF_PERMIT_CHANGE
							.equals(((RPCRequest) request).getRpcTypeCd())) {
				facility_eu_loop: for (EmissionUnit eu : request.getFacility()
						.getEmissionUnits()) {
					for (ApplicationEU appEU : request.getEus()) {
						if (appEU.getFpEU().getCorrEpaEmuId()
								.equals(eu.getCorrEpaEmuId())) {
							continue facility_eu_loop;
						}
					}
					ApplicationEU appEU = new ApplicationEU();
					appEU.setApplicationId(request.getApplicationID());
					appEU.setFpEU(eu);
					appEU.setExcluded(true);
					appEU = createApplicationEU(appEU, trans);
					request.addEu(appEU);
				}

			}
		}
	}

	/**
	 * Special processing is needed to modify EUs for requests for modifications
	 * to permits.
	 * 
	 * @param request
	 * @param trans
	 * @throws DAOException
	 */
	private void modifyPermitModRequestEUs(Application request,
			Transaction trans) throws DAOException {
		Integer permitId = request.getPermitId();
		if (permitId != null) {
			PermitDAO permitDAO = permitDAO(getSchema(CommonConst.READONLY_SCHEMA));
			permitDAO.setTransaction(trans);
			Permit permit = permitDAO.retrievePermit(permitId);
			boolean excludeEU = true;

			if (request instanceof RPCRequest) {
				// exclude EUs for RPC requests unless permit type is PTO
				excludeEU = !permit.getPermitType()
						.equals(PermitTypeDef.TV_PTO);
			} else if (request instanceof RPERequest) {
				excludeEU = false; // include all EUs for RPE Request
			}

			List<ApplicationEU> requestEUs = request.getEus();
			for (PermitEUGroup euGroup : permit.getEuGroups()) {
				for (PermitEU permitEU : euGroup.getPermitEUs()) {
					// find application EU matching permit EU
					ApplicationEU appEU = null;
					for (ApplicationEU matchEU : requestEUs) {
						if (matchEU.getFpEU().getCorrEpaEmuId()
								.equals(permitEU.getFpEU().getCorrEpaEmuId())) {
							appEU = matchEU;
							requestEUs.remove(appEU);
							break;
						}
					}
					if (appEU != null) {
						// make sure EU information is taken from current
						// facility inventory
						synchPermitModRequestEuWithFacility(appEU, request
								.getFacility().getEmissionUnits());

						if (request instanceof RPRRequest) {
							// non-Active EUs are not includeable for
							// RPRRequests
							appEU.setNotIncludable(!isPermitEUActive(permitEU));
						} else {
							if (request instanceof RPCRequest
									&& !RPCTypeDef.PTIO_ADMIN_MOD
											.equals(((RPCRequest) request)
													.getRpcTypeCd())) {
								// Mantis 2955 - change logic for TV RPC request
								appEU.setNotIncludable(TVClassification.TRIVIAL
										.equals(appEU.getFpEU().getTvClassCd())
										|| TVClassification.INSIG_NO_APP_REQS
												.equals(appEU.getFpEU()
														.getTvClassCd()));
							} else {
								// PTIO RPC and RPE requests mark EU as not
								// includable if the EU
								// is not active or if the EU is exempt from the
								// facility inventory
								appEU.setNotIncludable((appEU.getFpEU()
										.getExemptStatusCd() != null && !appEU
										.getFpEU().getExemptStatusCd()
										.equals(ExemptStatusDef.NA))
										|| !isPermitEUActive(permitEU));
							}

							// exlude shutdown or invalid EUs from RPR and RPC
							// requests
							if (appEU.getFpEU().getEuShutdownDate() != null
									|| appEU.getFpEU().getOperatingStatusCd()
											.equals(EuOperatingStatusDef.IV)
									|| appEU.getFpEU().getOperatingStatusCd()
											.equals(EuOperatingStatusDef.SD)) {
								// don't process shutdown or invalid EUs
								logger.warn("removing invalid or shutown application EU: "
										+ appEU.getFpEU().getEpaEmuId());
								request.removeEu(appEU);
								removeApplicationEU(appEU, true, trans);
								appEU = null;
							}
						}
						if (appEU != null) {
							modifyApplicationEU(appEU, trans);
						}
					} else {
						// EU may have been marked as shutdown and deleted and
						// is now being
						// restored. Create a new EU and add it to the
						// application
						createApplicationEUFromPermitEU(request, permitEU,
								excludeEU, trans);
					}
				}
			}

			// remove EUs that were once part of the permit but have since been
			// removed
			// unless this is an off permit change request
			if (!(request instanceof RPCRequest && RPCTypeDef.TV_OFF_PERMIT_CHANGE
					.equals(((RPCRequest) request).getRpcTypeCd()))) {
				for (ApplicationEU appEU : requestEUs) {
					logger.warn("removing EU no longer in permit: "
							+ appEU.getFpEU().getEpaEmuId());
					request.removeEu(appEU);
					removeApplicationEU(appEU, true, trans);
				}
			} else {
				for (ApplicationEU appEU : requestEUs) {
					modifyApplicationEU(appEU, trans);
				}
			}
		}
	}

	private ApplicationEU createApplicationEUFromPermitEU(Application request,
			PermitEU permitEU, boolean excludeEU, Transaction trans)
			throws DAOException {
		ApplicationEU appEU = new ApplicationEU();
		appEU.setApplicationId(request.getApplicationID());
		appEU.setFpEU(permitEU.getFpEU());
		appEU.setExcluded(excludeEU);

		// make sure EU information is taken from current facility inventory
		synchAppEuWithFacility(appEU, request.getFacility().getEmissionUnits());

		if (request instanceof RPRRequest) {
			// non-active EUs are not includeable in RPR Request
			appEU.setNotIncludable(!isPermitEUActive(permitEU));
		} else {
			if (request instanceof RPCRequest
					&& !RPCTypeDef.PTIO_ADMIN_MOD.equals(((RPCRequest) request)
							.getRpcTypeCd())) {
				// Mantis 2955 - change logic for TV RPC request
				appEU.setNotIncludable(TVClassification.TRIVIAL.equals(appEU
						.getFpEU().getTvClassCd())
						|| TVClassification.INSIG_NO_APP_REQS.equals(appEU
								.getFpEU().getTvClassCd()));
			} else {
				// non-active or Exempt EUs are not includable in RPC and RPE
				// Requests
				appEU.setNotIncludable((appEU.getFpEU().getExemptStatusCd() != null && !appEU
						.getFpEU().getExemptStatusCd()
						.equals(ExemptStatusDef.NA))
						|| !isPermitEUActive(permitEU));
			}

			// shutdown and invalid EUs are removed from PRC and RPE Requests
			if (appEU.getFpEU().getEuShutdownDate() != null
					|| appEU.getFpEU().getOperatingStatusCd()
							.equals(EuOperatingStatusDef.IV)
					|| appEU.getFpEU().getOperatingStatusCd()
							.equals(EuOperatingStatusDef.SD)) {
				// don't process shutdown or invalid EUs
				logger.debug("Skipping invalid or shutown EU: "
						+ appEU.getFpEU().getEpaEmuId());
				appEU = null;
			}
		}
		if (appEU != null) {
			createApplicationEU(appEU, trans);
		}
		return appEU;
	}

	private void logApplicationReceivedEvent(Application app, int userId,
			Transaction trans) throws RemoteException {
		if (app != null) {
			logger.debug("Creating EventLog...");
			EventLog el = new EventLog();
			StringBuffer note = new StringBuffer();
			note.append("Permit application number ");
			note.append(app.getApplicationNumber());
			note.append(" has been received.");
			logger.debug("EventLog note = " + note.toString());

			if (app.getFacility() != null) {
				el.setFpId(app.getFacility().getFpId());
				el.setFacilityId(app.getFacility().getFacilityId());
			}
			el.setUserId(userId);
			el.setDate(new Timestamp(System.currentTimeMillis()));
			el.setEventTypeDefCd(EventLogTypeDef.PA_RCVD);
			el.setNote(note.toString());

			logger.debug("Creating an event log");
			FacilityDAO facDAO = facilityDAO(trans);
			facDAO.createEventLog(el);

			// logger.debug("calling FacilityHelper.createEventLog...");
			// FacilityHelper facHelper = new FacilityHelper();
			// facHelper.createEventLog(el);
			// logger.debug("Done with FacilityHelper.createEventLog");

			logger.debug("Event log created");
		}
	}

	private final List<TVPteAdjustment> calculateTvCapPteTotals(
			TVApplication app, Transaction trans) throws DAOException {
		HashMap<String, TVPteAdjustment> pteAdjustedMap = new HashMap<String, TVPteAdjustment>();

		// initialize map with adjusted values from database
		ApplicationDAO appDAO = applicationDAO(trans);
		for (TVPteAdjustment pte : appDAO
				.retrieveTVPteAdjustmentForApplication(app.getApplicationID())) {
			if (pte.getEuEmissionTableCd().equals(
					ApplicationEUEmissionTableDef.CAP_TABLE_CD)) {
				pteAdjustedMap.put(pte.getPollutantCd(), pte);
			}
		}

		// calculate total pte value for each pollutant across included EUs
		HashMap<String, BigDecimal> pteValueMap = new HashMap<String, BigDecimal>();

		for (String pollutantCd : getTVCapPollutantCds()) {
			String emissionsSum = appDAO
					.retrieveTVApplicationEUEmissionsForPollutant(
							app.getApplicationID(),
							pollutantCd, ApplicationEUEmissionTableDef.CAP_TABLE_CD);
			if (emissionsSum == null || emissionsSum.trim().length() == 0) {
				emissionsSum = "0";
	        }
			BigDecimal capPte = getEmissionValueAsBigDecimal(
					pollutantCd,
					ApplicationEUEmissionTableDef.CAP_TABLE_CD,
					emissionsSum);
			
			if (pteValueMap.get(pollutantCd) == null) {
				pteValueMap.put(pollutantCd,
						capPte);
			}
		}

		for (String pollutantCd : pteValueMap.keySet()) {
			TVPteAdjustment adj = pteAdjustedMap.get(pollutantCd);
			if (adj == null) {
				adj = new TVPteAdjustment(app.getApplicationID(),
						ApplicationEUEmissionTableDef.CAP_TABLE_CD, pollutantCd);
				pteAdjustedMap.put(pollutantCd, adj);
			}
			adj.setPteEUTotal(getEmissionBigDecimalValueAsString(
					adj.getPollutantCd(), adj.getEuEmissionTableCd(),
					pteValueMap.get(pollutantCd)));
			adj.setMajorStatus(getTVPTEStatus(adj.getPollutantCd(),
					adj.getEuEmissionTableCd(), adj.getPteFinal(),
					new BigDecimal("0")));
		}

		List<TVPteAdjustment> result = new ArrayList<TVPteAdjustment>();
		for (String pollutantCd : getTVCapPollutantCds()) {
			TVPteAdjustment adj = pteAdjustedMap.get(pollutantCd);
			if (adj != null) {
				result.add(adj);
			}
		}
		return result;
	}

	private final List<TVPteAdjustment> calculateTvHapPteTotals(
			TVApplication app, Transaction trans) throws DAOException {
		TreeMap<String, TVPteAdjustment> pteAdjustedMap = new TreeMap<String, TVPteAdjustment>();
		TVPteAdjustment totalHAP = new TVPteAdjustment(app.getApplicationID(),
				ApplicationEUEmissionTableDef.HAP_TABLE_CD,
				PollutantDef.HAPS_TOT_CD);

		// initialize map with adjusted values from database
		ApplicationDAO appDAO = applicationDAO(trans);
		for (TVPteAdjustment pte : appDAO
				.retrieveTVPteAdjustmentForApplication(app.getApplicationID())) {
			if (pte.getEuEmissionTableCd().equals(
					ApplicationEUEmissionTableDef.HAP_TABLE_CD)) {
				pteAdjustedMap.put(PollutantDef.getData().getItems()
						.getItemDesc(pte.getPollutantCd()), pte);
			}
		}

		// calculate total pte value for each pollutant across included EUs
		HashMap<String, BigDecimal> pteValueMap = new HashMap<String, BigDecimal>();

		// Retrieve and set the descriptions for EUs where purpose of application is 'Other'.
		String[] hapPollutants = appDAO.retrieveApplicationHAPPollutantList(app.getApplicationID());
		for (String pollutantCd : hapPollutants) {
			String emissionsSum = appDAO
					.retrieveTVApplicationEUEmissionsForPollutant(
							app.getApplicationID(),
							pollutantCd, ApplicationEUEmissionTableDef.HAP_TABLE_CD);
			if (emissionsSum == null || emissionsSum.trim().length() == 0) {
				emissionsSum = "0";
	        }
			BigDecimal hapPte = getEmissionValueAsBigDecimal(
					pollutantCd,
					ApplicationEUEmissionTableDef.HAP_TABLE_CD,
					emissionsSum);
			
			if (pteValueMap.get(pollutantCd) == null) {
				pteValueMap.put(pollutantCd,
						hapPte);
			}
		}
		
		for (String pollutantCd : pteValueMap.keySet()) {
			String pollutantDsc = PollutantDef.getData().getItems()
					.getItemDesc(pollutantCd);
			TVPteAdjustment adj = pteAdjustedMap.get(pollutantDsc);

			if (adj == null) {
				adj = new TVPteAdjustment(app.getApplicationID(),
						ApplicationEUEmissionTableDef.HAP_TABLE_CD, pollutantCd);
				pteAdjustedMap.put(pollutantDsc, adj);
			}
			adj.setPteEUTotal(getEmissionBigDecimalValueAsString(
					adj.getPollutantCd(), adj.getEuEmissionTableCd(),
					pteValueMap.get(pollutantCd)));
			adj.setMajorStatus(getTVPTEStatus(adj.getPollutantCd(),
					adj.getEuEmissionTableCd(), adj.getPteFinal(),
					new BigDecimal("0")));
		}

		// add adjustment data to result list sorted by pollutant name
		List<TVPteAdjustment> result = new ArrayList<TVPteAdjustment>();
		BigDecimal sumTotal = new BigDecimal("0");
		for (String pollutantDsc : pteAdjustedMap.keySet()) {
			TVPteAdjustment adj = pteAdjustedMap.get(pollutantDsc);

			if (adj != null) {
				result.add(adj);
				BigDecimal pteAdjusted = new BigDecimal("0");
				totalHAP.setPteEUTotal(null);
				if (totalHAP.getPteAdjusted() != null) {
					pteAdjusted = getEmissionValueAsBigDecimal(
							adj.getPollutantCd(), adj.getEuEmissionTableCd(),
							totalHAP.getPteAdjusted()).add(
							getEmissionValueAsBigDecimal(adj.getPollutantCd(),
									adj.getEuEmissionTableCd(),
									adj.getPteFinal()));
				} else {
					pteAdjusted = getEmissionValueAsBigDecimal(
							adj.getPollutantCd(), adj.getEuEmissionTableCd(),
							adj.getPteFinal());
				}
				totalHAP.setPteAdjusted(getEmissionBigDecimalValueAsString(
						PollutantDef.HAPS_TOT_CD,
						totalHAP.getEuEmissionTableCd(), pteAdjusted));
				sumTotal = pteAdjusted;

			}
			adj.setMajorStatus(getTVPTEStatus(adj.getPollutantCd(),
					adj.getEuEmissionTableCd(), adj.getPteFinal(),
					new BigDecimal("0")));
		}

		// add totals row if there are one or more pollutants in the table
		if (result.size() > 0) {
			result.add(totalHAP);
			if (sumTotal.compareTo(new BigDecimal("0")) == 0)
				totalHAP.setMajorStatus("Not Applicable");
			if (sumTotal.compareTo(new BigDecimal("25")) >= 0)
				totalHAP.setMajorStatus("Major");
			else
				totalHAP.setMajorStatus("Non-Major");
		}
		return result;
	}

	private final List<TVPteAdjustment> calculateTvGhgPteTotals(
			TVApplication app, Transaction trans) throws DAOException {
		TreeMap<String, TVPteAdjustment> pteAdjustedMap = new TreeMap<String, TVPteAdjustment>();
		TVPteAdjustment totalGHG = new TVPteAdjustment(app.getApplicationID(),
				ApplicationEUEmissionTableDef.GHG_TABLE_CD,
				PollutantDef.GHG_TOT_CD);

		// initialize map with adjusted values from database
		ApplicationDAO appDAO = applicationDAO(trans);
		for (TVPteAdjustment pte : appDAO
				.retrieveTVPteAdjustmentForApplication(app.getApplicationID())) {
			if (pte.getEuEmissionTableCd().equals(
					ApplicationEUEmissionTableDef.GHG_TABLE_CD)) {
				pteAdjustedMap.put(PollutantDef.getData().getItems()
						.getItemDesc(pte.getPollutantCd()), pte);
			}
		}

		// calculate total pte value for each pollutant across included EUs
		HashMap<String, BigDecimal> pteValueMap = new HashMap<String, BigDecimal>();

		// Retrieve and set the descriptions for EUs where purpose of application is 'Other'.
				String[] ghgPollutants = appDAO.retrieveApplicationGHGPollutantList(app.getApplicationID());
				for (String pollutantCd : ghgPollutants) {
					String emissionsSum = appDAO
							.retrieveTVApplicationEUEmissionsForPollutant(
									app.getApplicationID(),
									pollutantCd,
									ApplicationEUEmissionTableDef.GHG_TABLE_CD);
					if (emissionsSum == null || emissionsSum.trim().length() == 0) {
						emissionsSum = "0";
			        }
					BigDecimal ghgPte = getEmissionValueAsBigDecimal(
							pollutantCd,
							ApplicationEUEmissionTableDef.GHG_TABLE_CD,
							emissionsSum);
					
					if (pteValueMap.get(pollutantCd) == null) {
						pteValueMap.put(pollutantCd,
								ghgPte);
					}
				}
		
		
		for (String pollutantCd : pteValueMap.keySet()) {
			String pollutantDsc = PollutantDef.getData().getItems()
					.getItemDesc(pollutantCd);
			TVPteAdjustment adj = pteAdjustedMap.get(pollutantDsc);
			if (adj == null) {
				adj = new TVPteAdjustment(app.getApplicationID(),
						ApplicationEUEmissionTableDef.GHG_TABLE_CD, pollutantCd);
				pteAdjustedMap.put(pollutantDsc, adj);
			}
			adj.setPteEUTotal(getEmissionBigDecimalValueAsString(
					adj.getPollutantCd(), adj.getEuEmissionTableCd(),
					pteValueMap.get(pollutantCd)));
			BigDecimal co2e = new BigDecimal("0");
			try {
				co2e = PollutantDef.computeBigDecimalCO2Equivalent(
						adj.getPollutantCd(), adj.getPteFinal());
			} catch (NumberFormatException e) {
				logger.error("Invalid value for CO2 Equivalent", e);
			}
			adj.setCo2Equivalent(getEmissionBigDecimalValueAsString(
					adj.getPollutantCd(), adj.getEuEmissionTableCd(), co2e));
			adj.setMajorStatus(getTVPTEStatus(adj.getPollutantCd(),
					adj.getEuEmissionTableCd(), adj.getPteFinal(), co2e));
		}

		// add adjustment data to result list sorted by pollutant name
		List<TVPteAdjustment> result = new ArrayList<TVPteAdjustment>();
		for (String pollutantDsc : pteAdjustedMap.keySet()) {
			TVPteAdjustment adj = pteAdjustedMap.get(pollutantDsc);
			if (adj != null) {
				result.add(adj);
				BigDecimal pteAdjusted = new BigDecimal("0");
				BigDecimal co2e = new BigDecimal("0");
				totalGHG.setPteEUTotal(null);
				if (totalGHG.getPteAdjusted() != null) {
					pteAdjusted = getEmissionValueAsBigDecimal(
							adj.getPollutantCd(), adj.getEuEmissionTableCd(),
							totalGHG.getPteAdjusted()).add(
							getEmissionValueAsBigDecimal(adj.getPollutantCd(),
									adj.getEuEmissionTableCd(),
									adj.getPteFinal()));
				} else {
					pteAdjusted = getEmissionValueAsBigDecimal(
							adj.getPollutantCd(), adj.getEuEmissionTableCd(),
							adj.getPteFinal());
				}
				totalGHG.setPteAdjusted(getEmissionBigDecimalValueAsString(
						PollutantDef.GHG_TOT_CD,
						totalGHG.getEuEmissionTableCd(), pteAdjusted));
				if (totalGHG.getCo2Equivalent() != null) {
					co2e = getEmissionValueAsBigDecimal(adj.getPollutantCd(),
							adj.getEuEmissionTableCd(),
							totalGHG.getCo2Equivalent()).add(
							getEmissionValueAsBigDecimal(adj.getPollutantCd(),
									adj.getEuEmissionTableCd(),
									adj.getCo2Equivalent()));
				} else {
					co2e = getEmissionValueAsBigDecimal(adj.getPollutantCd(),
							adj.getEuEmissionTableCd(), adj.getCo2Equivalent());
				}
				totalGHG.setCo2Equivalent(getEmissionBigDecimalValueAsString(
						PollutantDef.GHG_TOT_CD,
						totalGHG.getEuEmissionTableCd(), co2e));

			}
		}
		// ADQ asked us to remove the total GHG value
		// add totals row if there are one or more pollutants in the table
		//if (result.size() > 0) {
		//	result.add(totalGHG);
		//}

		return result;
	}

	private final List<TVPteAdjustment> calculateTvOthPteTotals(
			TVApplication app, Transaction trans) throws DAOException {
		TreeMap<String, TVPteAdjustment> pteAdjustedMap = new TreeMap<String, TVPteAdjustment>();

		// initialize map with adjusted values from database
		ApplicationDAO appDAO = applicationDAO(trans);
		for (TVPteAdjustment pte : appDAO
				.retrieveTVPteAdjustmentForApplication(app.getApplicationID())) {
			if (pte.getEuEmissionTableCd().equals(
					ApplicationEUEmissionTableDef.OTH_TABLE_CD)) {
				pteAdjustedMap.put(PollutantDef.getData().getItems()
						.getItemDesc(pte.getPollutantCd()), pte);
			}
		}

		// calculate total pte value for each pollutant across included EUs
		HashMap<String, BigDecimal> pteValueMap = new HashMap<String, BigDecimal>();
	
		// Retrieve and set the descriptions for EUs where purpose of application is 'Other'.
		String[] othPollutants = appDAO.retrieveApplicationOTHPollutantList(app.getApplicationID());
		for (String pollutantCd : othPollutants) {
			String emissionsSum = appDAO
					.retrieveTVApplicationEUEmissionsForPollutant(
							app.getApplicationID(),
							pollutantCd,
							ApplicationEUEmissionTableDef.OTH_TABLE_CD);
			if (emissionsSum == null || emissionsSum.trim().length() == 0) {
				emissionsSum = "0";
	        }
			BigDecimal othPte = getEmissionValueAsBigDecimal(
					pollutantCd,
					ApplicationEUEmissionTableDef.OTH_TABLE_CD,
					emissionsSum);
			
			if (pteValueMap.get(pollutantCd) == null) {
				pteValueMap.put(pollutantCd,
						othPte);
			}
		}

		for (String pollutantCd : pteValueMap.keySet()) {
			String pollutantDsc = PollutantDef.getData().getItems()
					.getItemDesc(pollutantCd);
			TVPteAdjustment adj = pteAdjustedMap.get(pollutantDsc);
			if (adj == null) {
				adj = new TVPteAdjustment(app.getApplicationID(),
						ApplicationEUEmissionTableDef.OTH_TABLE_CD, pollutantCd);
				pteAdjustedMap.put(pollutantDsc, adj);
			}
			adj.setPteEUTotal(getEmissionBigDecimalValueAsString(
					adj.getPollutantCd(), adj.getEuEmissionTableCd(),
					pteValueMap.get(pollutantCd)));
			BigDecimal co2e = new BigDecimal("0");
			try {
				co2e = PollutantDef.computeBigDecimalCO2Equivalent(
						adj.getPollutantCd(), adj.getPteFinal());
			} catch (NumberFormatException e) {
				logger.error("Invalid value for CO2 Equivalent", e);
			}
			adj.setCo2Equivalent(getEmissionBigDecimalValueAsString(
					adj.getPollutantCd(), adj.getEuEmissionTableCd(), co2e));
			adj.setMajorStatus(getTVPTEStatus(adj.getPollutantCd(),
					adj.getEuEmissionTableCd(), adj.getPteFinal(), co2e));
		}

		// add adjustment data to result list sorted by pollutant name
		List<TVPteAdjustment> result = new ArrayList<TVPteAdjustment>();
		TVPteAdjustment hapMaxAdjustment = null;

		for (String pollutantDsc : pteAdjustedMap.keySet()) {
			TVPteAdjustment adj = pteAdjustedMap.get(pollutantDsc);
			result.add(adj);
		}

		// add OTH Max to end of result list
		if (hapMaxAdjustment != null) {
			result.add(hapMaxAdjustment);
		}

		// add adjustment data to result list sorted by pollutant name
		/*
		 * List<TVPteAdjustment> result = new ArrayList<TVPteAdjustment>(); for
		 * (String pollutantDsc : pteAdjustedMap.keySet()) { TVPteAdjustment adj
		 * = pteAdjustedMap.get(pollutantDsc); if (adj != null) {
		 * result.add(adj); float pteAdjusted = 0f; float co2e = 0f;
		 * totalGHG.setPteEUTotal(null); if (totalGHG.getPteAdjusted() != null)
		 * { pteAdjusted = getEmissionValueAsFloat(adj.getPollutantCd(),
		 * adj.getEuEmissionTableCd(), totalGHG.getPteAdjusted()) +
		 * getEmissionValueAsFloat(adj.getPollutantCd(),
		 * adj.getEuEmissionTableCd(), adj.getPteFinal()); } else { pteAdjusted
		 * = getEmissionValueAsFloat(adj.getPollutantCd(),
		 * adj.getEuEmissionTableCd(), adj.getPteFinal()); }
		 * totalGHG.setPteAdjusted(getEmissionValueAsString(
		 * PollutantDef.GHG_TOT_CD, totalGHG.getEuEmissionTableCd(),
		 * pteAdjusted)); if (totalGHG.getCo2Equivalent() != null) { co2e =
		 * getEmissionValueAsFloat(adj.getPollutantCd(),
		 * adj.getEuEmissionTableCd(), totalGHG.getCo2Equivalent()) +
		 * getEmissionValueAsFloat(adj.getPollutantCd(),
		 * adj.getEuEmissionTableCd(), adj.getCo2Equivalent()); } else { co2e =
		 * getEmissionValueAsFloat(adj.getPollutantCd(),
		 * adj.getEuEmissionTableCd(), adj.getCo2Equivalent()); }
		 * totalGHG.setCo2Equivalent(getEmissionValueAsString(
		 * PollutantDef.GHG_TOT_CD, totalGHG.getEuEmissionTableCd(), co2e));
		 * 
		 * // do not put EU total into facility total -- ever Mantis 3916 // //
		 * display EU Total in PTE adjusted column if no adjusted // value is
		 * provided // if (adj.getPteAdjusted() == null) { //
		 * adj.setPteAdjusted(adj.getPteEUTotal()); // } } } // add totals row
		 * if there are one or more pollutants in the table if (result.size() >
		 * 0) { result.add(totalGHG); }
		 */

		return result;
	}

	// co2e only for GHG TABLE
	public String getTVPTEStatus(String pollutantCd, String euEmissionTableCd,
			String pte, float co2e) {
		String status = "Not Applicable";
		float pteFinal = getEmissionValueAsFloat(pollutantCd,
				euEmissionTableCd, pte);
		if (pollutantCd != null && euEmissionTableCd != null
				&& (pteFinal != 0 || co2e != 0)) {
			status = "Non-Major";
			final float CAP_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
					"TV_PTE_CAP_Threshold", 100f);
			final float SINGLE_HAP_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
					"TV_PTE_Single_HAP_Threshold", 10f);
			final float TOTAL_HAP_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
					"TV_PTE_Total_HAP_Threshold", 25f);
			final float TOTAL_CO2E_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
					"TV_GHG_Total_CO2E_Threshold", 100000f);
			if ((euEmissionTableCd
					.equals(ApplicationEUEmissionTableDef.CAP_TABLE_CD) && pteFinal >= CAP_THRESHOLD)
					|| (euEmissionTableCd
							.equals(ApplicationEUEmissionTableDef.HAP_TABLE_CD)
							&& !pollutantCd.equals(PollutantDef.HTOT_CD) && pteFinal >= SINGLE_HAP_THRESHOLD)
					|| (euEmissionTableCd
							.equals(ApplicationEUEmissionTableDef.HAP_TABLE_CD)
							&& pollutantCd.equals(PollutantDef.HTOT_CD) && pteFinal >= TOTAL_HAP_THRESHOLD)
					|| (euEmissionTableCd
							.equals(ApplicationEUEmissionTableDef.GHG_TABLE_CD) && co2e >= TOTAL_CO2E_THRESHOLD)) {
				status = "Major";
			}
		}
		return status;
	}

	public String getTVPTEStatus(String pollutantCd, String euEmissionTableCd,
			String pte, BigDecimal co2e) {
		String status = "Not Applicable";
		BigDecimal pteFinal = getEmissionValueAsBigDecimal(pollutantCd,
				euEmissionTableCd, pte);
		if (pollutantCd != null
				&& euEmissionTableCd != null
				&& (pteFinal.compareTo(new BigDecimal("0")) != 0 || co2e
						.compareTo(new BigDecimal("0")) != 0)) {
			status = "Non-Major";
			final float CAP_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
					"TV_PTE_CAP_Threshold", 100f);
			final float SINGLE_HAP_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
					"TV_PTE_Single_HAP_Threshold", 10f);
			final float TOTAL_HAP_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
					"TV_PTE_Total_HAP_Threshold", 25f);
			final float TOTAL_CO2E_THRESHOLD = SystemPropertyDef.getSystemPropertyValueAsFloat(
					"TV_GHG_Total_CO2E_Threshold", 100000f);
			if ((euEmissionTableCd
					.equals(ApplicationEUEmissionTableDef.CAP_TABLE_CD) && pteFinal
					.compareTo(new BigDecimal(CAP_THRESHOLD)) >= 0)
					|| (euEmissionTableCd
							.equals(ApplicationEUEmissionTableDef.HAP_TABLE_CD)
							&& !pollutantCd.equals(PollutantDef.HTOT_CD) && pteFinal
							.compareTo(new BigDecimal(SINGLE_HAP_THRESHOLD)) >= 0)
					|| (euEmissionTableCd
							.equals(ApplicationEUEmissionTableDef.HAP_TABLE_CD)
							&& pollutantCd.equals(PollutantDef.HTOT_CD) && pteFinal
							.compareTo(new BigDecimal(TOTAL_HAP_THRESHOLD)) >= 0)
					|| (euEmissionTableCd
							.equals(ApplicationEUEmissionTableDef.GHG_TABLE_CD) && co2e
							.compareTo(new BigDecimal(TOTAL_CO2E_THRESHOLD)) >= 0)) {
				status = "Major";
			}
		}
		return status;
	}

	Float getEgressPtThreshold(String pollutantCd) {
		if (egressPtThresholdMap == null) {
			egressPtThresholdMap = new HashMap<String, Float>();
			egressPtThresholdMap.put(
					PollutantDef.PE_CD,
					SystemPropertyDef.getSystemPropertyValueAsFloat("APP_EMISSIONS_PE_Egress_Pt_Threshold",
							10f));
			egressPtThresholdMap.put(
					PollutantDef.PM10_CD,
					SystemPropertyDef.getSystemPropertyValueAsFloat("APP_EMISSIONS_PM10_Egress_Pt_Threshold",
							10f));
			egressPtThresholdMap.put(
					PollutantDef.PM25_CD,
					SystemPropertyDef.getSystemPropertyValueAsFloat("APP_EMISSIONS_PM25_Egress_Pt_Threshold",
							10f));
			egressPtThresholdMap.put(
					PollutantDef.SO2_CD,
					SystemPropertyDef.getSystemPropertyValueAsFloat("APP_EMISSIONS_SO2_Egress_Pt_Threshold",
							25f));
			egressPtThresholdMap.put(
					PollutantDef.NOX_CD,
					SystemPropertyDef.getSystemPropertyValueAsFloat("APP_EMISSIONS_NOx_Egress_Pt_Threshold",
							25f));
			egressPtThresholdMap.put(
					PollutantDef.CO_CD,
					SystemPropertyDef.getSystemPropertyValueAsFloat("APP_EMISSIONS_CO_Egress_Pt_Threshold",
							100f));
			egressPtThresholdMap.put(
					PollutantDef.PB_CD,
					SystemPropertyDef.getSystemPropertyValueAsFloat("APP_EMISSIONS_PB_Egress_Pt_Threshold",
							0.6f));
		}
		return egressPtThresholdMap.get(pollutantCd);
	}

	private Timestamp getStars2DeployedDate() {
		
		GregorianCalendar defaultDeployDate = new GregorianCalendar(2008, 0, 1);
		Timestamp defaultDeployTS = new Timestamp(
				defaultDeployDate.getTimeInMillis());
		Date date = SystemPropertyDef.getSystemPropertyValueAsDate("STARS2_Deploy_Date", defaultDeployTS);
		Timestamp deployDateTS = new Timestamp(date.getTime());

		return deployDateTS;
	}
	
	private Timestamp getImpactLegacyCutoffDate() {
		
		GregorianCalendar defaultDeployDate = new GregorianCalendar(2008, 0, 1);
		Timestamp defaultDeployTS = new Timestamp(
				defaultDeployDate.getTimeInMillis());
		Date date = SystemPropertyDef.getSystemPropertyValueAsDate("IMPACT_Cutoff_Date", defaultDeployTS);
		Timestamp deployDateTS = new Timestamp(date.getTime());

		return deployDateTS;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getCapEmissionsValueFormat() {
		if (capEmissionsValueFormat == null) {
			capEmissionsValueFormat = SystemPropertyDef.getSystemPropertyValue("APP_EMISSIONS_VALUE_FORMAT_CAP", null);
			if (capEmissionsValueFormat == null) {
				capEmissionsValueFormat = DFLT_CAP_EMISSIONS_VALUE_FORMAT;
				logger.warn("No value specified for APP_EMISSIONS_VALUE_FORMAT_CAP in params.xml."
						+ "Defaulting to " + DFLT_CAP_EMISSIONS_VALUE_FORMAT);
			}
		}
		return capEmissionsValueFormat;
	}

	// DFLT_CAP_LB_HR_EMISSIONS_VALUE_FORMAT
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getCapLbHrEmissionsValueFormat() {
		if (capLbHrEmissionsValueFormat == null) {
			capLbHrEmissionsValueFormat = SystemPropertyDef.getSystemPropertyValue("APP_EMISSIONS_VALUE_FORMAT_CAP_LB_HR", null);
			if (capLbHrEmissionsValueFormat == null) {
				capLbHrEmissionsValueFormat = DFLT_CAP_LB_HR_EMISSIONS_VALUE_FORMAT;
				logger.warn("No value specified for APP_EMISSIONS_VALUE_FORMAT_CAP_LB_HR in params.xml."
						+ "Defaulting to "
						+ DFLT_CAP_LB_HR_EMISSIONS_VALUE_FORMAT);
			}
		}
		return capLbHrEmissionsValueFormat;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getHapEmissionsValueFormat() {
		if (hapEmissionsValueFormat == null) {
			hapEmissionsValueFormat = SystemPropertyDef.getSystemPropertyValue("APP_EMISSIONS_VALUE_FORMAT_HAP", null);
			if (hapEmissionsValueFormat == null) {
				hapEmissionsValueFormat = DFLT_HAP_EMISSIONS_VALUE_FORMAT;
				logger.warn("No value specified for APP_EMISSIONS_VALUE_FORMAT_HAP in params.xml."
						+ "Defaulting to " + DFLT_HAP_EMISSIONS_VALUE_FORMAT);
			}
		}
		return hapEmissionsValueFormat;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getGhgEmissionsValueFormat() {
		if (ghgEmissionsValueFormat == null) {
			ghgEmissionsValueFormat = SystemPropertyDef.getSystemPropertyValue("APP_EMISSIONS_VALUE_FORMAT_GHG", null);
			if (ghgEmissionsValueFormat == null) {
				ghgEmissionsValueFormat = DFLT_GHG_EMISSIONS_VALUE_FORMAT;
				logger.warn("No value specified for APP_EMISSIONS_VALUE_FORMAT_GHG in params.xml."
						+ "Defaulting to " + DFLT_GHG_EMISSIONS_VALUE_FORMAT);
			}
		}
		return ghgEmissionsValueFormat;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getOthEmissionsValueFormat() {
		if (othEmissionsValueFormat == null) {
			othEmissionsValueFormat = SystemPropertyDef.getSystemPropertyValue("APP_EMISSIONS_VALUE_FORMAT_OTH", null);
			if (othEmissionsValueFormat == null) {
				othEmissionsValueFormat = DFLT_OTH_EMISSIONS_VALUE_FORMAT;
				logger.warn("No value specified for APP_EMISSIONS_VALUE_FORMAT_OTH in params.xml."
						+ "Defaulting to " + DFLT_OTH_EMISSIONS_VALUE_FORMAT);
			}
		}
		return othEmissionsValueFormat;
	}

	/**
	 * Calculate the total and maximum Allowable Ton/Year values for eu and add
	 * them to the EU's list of CAP emissions.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void calculateHaps(PTIOApplicationEU eu) {
		BigDecimal hapMax = new BigDecimal("0");
		BigDecimal hapTotal = new BigDecimal("0");

		// determine highest value and total value for requested
		// allowable (ton/year)
		for (ApplicationEUEmissions emissions : eu.getHapTacEmissions()) {
			if (emissions.getPollutantCategory().matches(".*HAP.*")) {
				BigDecimal hapValue = getEmissionValueAsBigDecimal(
						emissions.getPollutantCd(),
						emissions.getEuEmissionTableCd(),
						emissions.getPotentialToEmitTonYr());

				if (hapMax.compareTo(hapValue) == 1) {
					hapMax = hapMax;
				} else {
					hapMax = hapValue;
				}
				hapTotal.add(hapValue);
			}
		}

		// update appropriate fields in CAP table
		for (ApplicationEUEmissions emissions : eu.getCapEmissions()) {
			if (emissions.getPollutantCd().equals(PollutantDef.HTOT_CD)) {
				emissions
						.setPotentialToEmitTonYr(getEmissionBigDecimalValueAsString(
								emissions.getPollutantCd(),
								emissions.getEuEmissionTableCd(), hapTotal));
			}
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getTvCapEmissionsValueFormat() {
		if (tvCapEmissionsValueFormat == null) {
			tvCapEmissionsValueFormat = SystemPropertyDef.getSystemPropertyValue("APP_EMISSIONS_VALUE_FORMAT_CAP", null);
			if (tvCapEmissionsValueFormat == null) {
				tvCapEmissionsValueFormat = DFLT_TV_CAP_EMISSIONS_VALUE_FORMAT;
				logger.warn("No value specified for APP_EMISSIONS_VALUE_FORMAT_CAP in params.xml."
						+ "Defaulting to " + DFLT_TV_CAP_EMISSIONS_VALUE_FORMAT);
			}
		}
		return tvCapEmissionsValueFormat;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getTvHapEmissionsValueFormat() {
		if (tvHapEmissionsValueFormat == null) {
			tvHapEmissionsValueFormat = SystemPropertyDef.getSystemPropertyValue("APP_EMISSIONS_VALUE_FORMAT_HAP", null);
			if (tvHapEmissionsValueFormat == null) {
				tvHapEmissionsValueFormat = DFLT_TV_HAP_EMISSIONS_VALUE_FORMAT;
				logger.warn("No value specified for APP_EMISSIONS_VALUE_FORMAT_HAP in params.xml."
						+ "Defaulting to " + DFLT_TV_HAP_EMISSIONS_VALUE_FORMAT);
			}
		}
		return tvHapEmissionsValueFormat;
	}

	private final void setPteTonsYrFloat(TVApplicationEUEmissions e, Float f) {
		String pteTonsYr = "0";
		if (f != null) {
			String format = getCapEmissionsValueFormat();
			if (ApplicationEUEmissionTableDef.HAP_TABLE_CD.equals(e
					.getEuEmissionTableCd())
					|| PollutantDef.PB_CD.equals(e.getPollutantCd())
					|| PollutantDef.HTOT_CD.equals(e.getPollutantCd())) {
				format = getHapEmissionsValueFormat();
			}
			DecimalFormat decFormat = new DecimalFormat(format);
			pteTonsYr = decFormat.format(f);
		}
		e.setPteTonsYr(pteTonsYr);
	}

	private final void setPteTonsYrBigDecimal(TVApplicationEUEmissions e,
			BigDecimal d) {
		String pteTonsYr = "0";
		if (d != null) {
			String format = getCapEmissionsValueFormat();
			if (ApplicationEUEmissionTableDef.HAP_TABLE_CD.equals(e
					.getEuEmissionTableCd())
					|| PollutantDef.PB_CD.equals(e.getPollutantCd())
					|| PollutantDef.HTOT_CD.equals(e.getPollutantCd())) {
				format = getHapEmissionsValueFormat();
			}
			DecimalFormat decFormat = new DecimalFormat(format);
			pteTonsYr = decFormat.format(d);
		}
		e.setPteTonsYr(pteTonsYr);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getEmissionValueAsString(String pollutantCd,
			String euEmissionTableCd, Float f) {
		String value = "0";
		if (f != null) {
			String format = getCapEmissionsValueFormat();
			if (ApplicationEUEmissionTableDef.HAP_TABLE_CD
					.equals(euEmissionTableCd)
					|| PollutantDef.PB_CD.equals(pollutantCd)
					|| PollutantDef.HTOT_CD.equals(pollutantCd)) {
				format = getHapEmissionsValueFormat();
			}
			DecimalFormat decFormat = new DecimalFormat(format);
			value = decFormat.format(f);
		}
		return value;
	}

	public String getEmissionBigDecimalValueAsString(String pollutantCd,
			String euEmissionTableCd, BigDecimal d) {
		String value = "0";
		if (d != null) {
			String format = getCapEmissionsValueFormat();
			if (ApplicationEUEmissionTableDef.HAP_TABLE_CD
					.equals(euEmissionTableCd)
					|| PollutantDef.PB_CD.equals(pollutantCd)
					|| PollutantDef.HTOT_CD.equals(pollutantCd)) {
				format = getHapEmissionsValueFormat();
			}
			DecimalFormat decFormat = new DecimalFormat(format);
			value = decFormat.format(d);
		}
		return value;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public float getEmissionValueAsFloat(String pollutantCd,
			String euEmissionTableCd, String value) {
		float result = 0f;
		if (value != null && value.length() != 0) {
			try {
				String format = getCapEmissionsValueFormat();
				if (ApplicationEUEmissionTableDef.HAP_TABLE_CD
						.equals(euEmissionTableCd)
						|| PollutantDef.PB_CD.equals(pollutantCd)
						|| PollutantDef.HTOT_CD.equals(pollutantCd)) {
					format = getHapEmissionsValueFormat();
				}
				DecimalFormat decFormat = new DecimalFormat(format);
				result = decFormat.parse(value).floatValue();
			} catch (ParseException e) {
				logger.error("Invalid emissions value: " + value);
			}
		}
		return result;
	}

	public BigDecimal getEmissionValueAsBigDecimal(String pollutantCd,
			String euEmissionTableCd, String value) {
		BigDecimal result = new BigDecimal("0");
		if (value != null && value.length() != 0) {
			String format = getCapEmissionsValueFormat();
			if (ApplicationEUEmissionTableDef.HAP_TABLE_CD
					.equals(euEmissionTableCd)
					|| PollutantDef.PB_CD.equals(pollutantCd)
					|| PollutantDef.HTOT_CD.equals(pollutantCd)) {
				format = getHapEmissionsValueFormat();
			}
			result = new BigDecimal(value.replaceAll(",", ""));
		}
		return result;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public BigDecimal calculateTVHaps(TVEUOperatingScenario selectedScenario) {
		BigDecimal hapMax = new BigDecimal("0");
		BigDecimal hapTotal = new BigDecimal("0");
		TVApplicationEUEmissions hapMaxEmissions = null;
		TVApplicationEUEmissions hapTotEmissions = null;

		// determine highest value and total value for requested
		// allowable (ton/year)
		for (TVApplicationEUEmissions emissions : selectedScenario
				.getHapEmissions()) {
			/*
			 * if (emissions.getPollutantCd().equals(PollutantDef.HMAX_CD)) {
			 * hapMaxEmissions = emissions; } else
			 */
			if (emissions.getPollutantCd().equals(PollutantDef.HTOT_CD)) {
				hapTotEmissions = emissions;
			} else {
				BigDecimal hapValue = getEmissionValueAsBigDecimal(
						emissions.getPollutantCd(),
						emissions.getEuEmissionTableCd(),
						emissions.getPteTonsYr());

				if (hapMax.compareTo(hapValue) == 1) {
					hapMax = hapMax;
				} else {
					hapMax = hapValue;
				}

				hapTotal.add(hapValue);
			}
		}

		/*
		 * if (hapMaxEmissions == null) { hapMaxEmissions = new
		 * TVApplicationEUEmissions( selectedScenario.getApplicationEuId(),
		 * selectedScenario.getTvEuOperatingScenarioId(), PollutantDef.HMAX_CD,
		 * ApplicationEUEmissionTableDef.HAP_TABLE_CD); } else {
		 */
		// remove from list so it can be added at the end of the list
		selectedScenario.getHapEmissions().remove(hapMaxEmissions);
		// }
		if (hapTotEmissions == null) {
			hapTotEmissions = new TVApplicationEUEmissions(
					selectedScenario.getApplicationEuId(),
					selectedScenario.getTvEuOperatingScenarioId(),
					PollutantDef.HTOT_CD,
					ApplicationEUEmissionTableDef.HAP_TABLE_CD);
		} else {
			// remove from list so it can be added at the end of the list
			selectedScenario.getHapEmissions().remove(hapTotEmissions);
		}

		// update appropriate fields in HAP table
		setPteTonsYrBigDecimal(hapMaxEmissions, hapMax);
		setPteTonsYrBigDecimal(hapTotEmissions, hapTotal);

		// add Max and Total to end of emissions list
		selectedScenario.getHapEmissions().add(hapMaxEmissions);
		selectedScenario.getHapEmissions().add(hapTotEmissions);

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@hapTotal: " + hapTotal);
		return hapTotal;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public List<String> verifySubpartTotals(
			List<ApplicationEUEmissions> appEUEmissions) {
		List<String> result = new ArrayList<String>();
		// translate list of applications emissions to map of pollutant code
		// to emission report emissions objects
		HashMap<String, Emissions> emissionsMap = new HashMap<String, Emissions>();
		List<Emissions> emissionsList = new ArrayList<Emissions>();
		for (ApplicationEUEmissions appEmission : appEUEmissions) {
			Emissions emissions = convertToEmissions(appEmission);
			emissionsMap.put(appEmission.getPollutantCd(), emissions);
			emissionsList.add(emissions);
		}

		PollutantPartOf partOf = new PollutantPartOf();
		for (Emissions emissions : emissionsList) {
			String msg = partOf.verifySubpartTotals(emissions,
					emissionsMap);
			if (msg != null) {
				// need to translate pollutant description from value in the
				// cm_pollutant_def table to the (non-standard) value used
				// in the PTI/PTIO Application
				int openBracketIdx = msg.indexOf('<');
				int closeBracketIdx = msg.lastIndexOf('>');
				String msgPrefix = msg.substring(0, openBracketIdx + 1);
				String msgSuffix = msg.substring(closeBracketIdx);
				String pollutantDsc = msg.substring(openBracketIdx + 1,
						closeBracketIdx).trim();
				for (String pollutantCd : ptioCapPollutantDefs.keySet()) {
					String realPolltantDsc = PollutantDef.getData().getItems()
							.getItemDesc(pollutantCd);
					if (realPolltantDsc.equals(pollutantDsc)) {
						pollutantDsc = ptioCapPollutantDefs.get(pollutantCd);
						break;
					}
				}
				result.add("The value of <"
						+ ptioCapPollutantDefs.get(emissions.getPollutantCd())
						+ "> " + msgPrefix + pollutantDsc + msgSuffix
						+ " in the Criteria Pollutants table.");
			}
		}
		return result;
	}

	private Emissions convertToEmissions(ApplicationEUEmissions appEmission) {
		Emissions emissions = new Emissions();
		emissions.setPollutantCd(appEmission.getPollutantCd());
		emissions.setStackEmissions(appEmission.getPotentialToEmitTonYr());
		emissions.setFugitiveEmissions("0");
		emissions.setEmissionsUnitNumerator(EmissionUnitReportingDef.TONS);
		return emissions;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public List<String> verifyTVSubpartTotals(
			List<TVApplicationEUEmissions> appEUEmissions) {
		List<String> result = new ArrayList<String>();
		// translate list of applications emissions to map of pollutant code
		// to emission report emissions objects
		HashMap<String, Emissions> emissionsMap = new HashMap<String, Emissions>();
		List<Emissions> emissionsList = new ArrayList<Emissions>();
		for (TVApplicationEUEmissions appEmission : appEUEmissions) {
			Emissions emissions = convertToEmissions(appEmission);
			emissionsMap.put(appEmission.getPollutantCd(), emissions);
			emissionsList.add(emissions);
		}

		PollutantPartOf partOf = new PollutantPartOf();
		for (Emissions emissions : emissionsList) {
			String msg = partOf.verifySubpartTotals(emissions,
					emissionsMap);
			if (msg != null) {
				result.add("The value of <"
						+ PollutantDef.getData().getItems()
								.getItemDesc(emissions.getPollutantCd()) + "> "
						+ msg + " in the Criteria Pollutants table.");
			}
		}
		return result;
	}

	private Emissions convertToEmissions(TVApplicationEUEmissions appEmission) {
		Emissions emissions = new Emissions();
		emissions.setPollutantCd(appEmission.getPollutantCd());
		emissions.setStackEmissions(appEmission.getPteTonsYr());
		emissions.setFugitiveEmissions("0");
		emissions.setEmissionsUnitNumerator(EmissionUnitReportingDef.TONS);
		return emissions;
	}

	/**
	 * Find applications associated with permit and invoke the modifyApplication
	 * method for RPC, RPE, and RPR requests. This is needed to reflect changes
	 * to permit EU data.
	 * 
	 * @param permit
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void updateApplicationAfterPermitUpdate(Permit permit)
			throws DAOException, RemoteException {
		Transaction trans = null;
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.READONLY_SCHEMA));

		ApplicationSearchResult[] results = appDAO
				.retrieveApplicationSearchResults(null, null,
						permit.getFacilityId(), null, null, null, null, null,
						false, null, permit.getPermitNumber(), null, false);

		for (ApplicationSearchResult result : results) {
			Application app = null;
			app = retrieveApplicationWithBasicEUs(result.getApplicationId());
			if (app instanceof RPCRequest || app instanceof RPERequest
					|| app instanceof RPRRequest) {
				try {
					trans = TransactionFactory.createTransaction();
					boolean modifyEUs = true;
					modifyApplication(app, trans, modifyEUs);
					trans.complete();
				} catch (DAOException de) {
					cancelTransaction(trans, de);
					break;
				} finally {
					closeTransaction(trans);
				}
			}
		}
	}

	/**
	 * Add attestation document to application
	 * 
	 * @param app
	 * @param attestationDoc
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void addAttestationDocument(Application app,
			ApplicationDocument attestationDoc, Transaction trans)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);
		if (app instanceof PBRNotification) {
			Attachment doc = new Attachment(attestationDoc);
			doc.setSubPath("Applications");
			doc.setObjectId(app.getApplicationID().toString());
			PBRNotificationDocument pbrDoc = new PBRNotificationDocument(doc);
			pbrDoc.setDocTypeCd(PBRNotifDocTypeDef.RO_SIGNATURE);
			appDAO.createPBRNotificationDocument(pbrDoc);
			((PBRNotification) app).getPbrDocuments().add(pbrDoc);
		} else if (app instanceof RPCRequest) {
			Attachment doc = new Attachment(attestationDoc);
			doc.setObjectId(app.getApplicationID().toString());
			doc.setSubPath("Applications");
			RPCRequestDocument rpcDoc = new RPCRequestDocument(doc);
			rpcDoc.setDocTypeCd(RPCRequestDocTypeDef.RO_SIGNATURE);
			appDAO.createRPCRequestDocument(rpcDoc);
			((RPCRequest) app).getRpcDocuments().add(rpcDoc);
		} else {
			ApplicationDocumentRef docRef = new ApplicationDocumentRef();
			docRef.setApplicationId(app.getApplicationID());
			docRef.setDocumentId(attestationDoc.getDocumentID());
			// docRef.setApplicationDocumentTypeCD(ApplicationDocumentTypeDef.RO_SIGNATURE);
			docRef.setDescription(attestationDoc.getDescription());
			appDAO.createApplicationDocument(docRef);
			// need to set publicDoc so document info is copied on internal
			// server
			ApplicationDocument appDoc = attestationDoc;
			appDoc.setApplicationId(app.getApplicationID());
			docRef.setPublicDoc(appDoc);
			app.addDocument(docRef);
		}
	}

	/**
	 * 
	 * @param app
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeAttestationDocument(Application app, Transaction trans)
			throws DAOException {
		if (app instanceof PBRNotification) {
			for (PBRNotificationDocument pbrDoc : ((PBRNotification) app)
					.getPbrDocuments()) {
				if (PBRNotifDocTypeDef.RO_SIGNATURE.equals(pbrDoc
						.getDocTypeCd())) {
					removePBRNotificationDocument(pbrDoc, trans);
				}
			}
		} else if (app instanceof RPCRequest) {
			for (RPCRequestDocument rpcDoc : ((RPCRequest) app)
					.getRpcDocuments()) {
				if (RPCRequestDocTypeDef.RO_SIGNATURE.equals(rpcDoc
						.getDocTypeCd())) {
					removeRPCRequestDocument(rpcDoc, trans);
				}
			}
		}/*
		 * else { for (ApplicationDocumentRef docRef : app.getDocuments()) { if
		 * (ApplicationDocumentTypeDef.RO_SIGNATURE.equals(docRef
		 * .getApplicationDocumentTypeCD())) { removeApplicationDocument(docRef,
		 * trans); } } }
		 */
	}

	/**
	 * 
	 * @param task
	 * @throws DAOException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void addSubmissionAttachments(Task task) throws DAOException,
			IOException, FileNotFoundException {
		Application application = task.getApplication();

		// don't want duplicate attachments
		task.getAttachments().clear();

		// add zip file with application attachments to task for submission
		if (application.hasAttachments()) {
			logger.debug("zipping application attachments...");
			Document zipDoc = generateTempApplicationAttachmentZipFile(application, true, false);
			us.oh.state.epa.portal.datasubmit.Attachment submitAttachment = new us.oh.state.epa.portal.datasubmit.Attachment(
					application.getApplicationID().toString(), "text/zip",
					DocumentUtil.getFileStoreRootPath() + zipDoc.getPath(),
					null, null);
			submitAttachment.setSystemFilename(DocumentUtil
					.getFileStoreRootPath() + zipDoc.getPath());
			task.getAttachments().add(submitAttachment);
			logger.debug("Done zipping application attachments.");
		}

		// generate pdf files for public and trade secret versions of document
		createApplicationReportDocument(application, task.getUserName(), true, false, true);
		if ((application instanceof PTIOApplication)
				|| (application instanceof TVApplication)) {
			// only generate trade secret report for PTIO and TV applications
			createApplicationReportDocument(application, task.getUserName(),
					false, false, true);
		}
		Document zipDoc = null;
		try {
			zipDoc = generateTempApplicationPDFZipFile(application);
			if (zipDoc != null) {
				us.oh.state.epa.portal.datasubmit.Attachment attachment = new us.oh.state.epa.portal.datasubmit.Attachment(
						zipDoc.getFileName(), "text/zip",
						DocumentUtil.getFileStoreRootPath() + zipDoc.getPath(),
						null, null);
				// logger.error("Debug #2966: generateTempApplicationPDFZipFile: attachment basePath "
				// + zipDoc.getPath() +
				// ", description " + zipDoc.getDescription());
				attachment.setSystemFilename(DocumentUtil
						.getFileStoreRootPath() + zipDoc.getPath());
				task.getAttachments().add(attachment);
			} // else {
				// logger.error("Debug #2966: generateTempApplicationPDFZipFile is null");
				// }
		} catch (Exception e) {
			logger.error(
					"Exception creating zip file for application PDF report files for "
							+ application.getApplicationNumber(), e);
		}
	}

	// This is the original version of isPollutantControlled().  It relies
	// on the appEu being fully populated. With Application performance
	// improvements, a second version is needed because, for performance
	// reasons, the appEu.getFpEU only has a subset of the data.
	@Override
	public boolean isPollutantControlledForValidate(ApplicationEU appEu, String pollutantCd) {
		boolean ret = false;
		// determine if pollutant is controlled
		for (EmissionProcess ep : appEu.getFpEU().getEmissionProcesses()) {
			for (ControlEquipment ce : ep.getAllControlEquipments()) {
				if (StringUtils.equalsIgnoreCase(ce.getOperatingStatusCd(), CeOperatingStatusDef.OP)
						&& ce.getPollCont(pollutantCd) != null) {
					ret = true;
				}
			}
		}
		return ret;
	}
	
	// This is a second version of isPollutantControlled().  It does not
	// rely on the appEu being fully populated. With Application performance
	// improvements, this second version is needed because, for performance
	// reasons, the appEu.getFpEU only has a subset of the data.
	@Override
	public boolean isPollutantControlled(ApplicationEU appEu, String pollutantCd)
			throws DAOException {
		boolean ret = false;

		FacilityDAO facDAO = facilityDAO(getSchema(CommonConst.STAGING_SCHEMA));

		List<Integer> ceFpnodeIds = facDAO
				.retrieveAllControlEquipIdsByEmuId(appEu.getFpEU().getEmuId());

		if (ceFpnodeIds != null && ceFpnodeIds.size() > 0) {
			for (Integer ceFpnodeId : ceFpnodeIds) {
				List<PollutantsControlled> pollutantsControlled = facDAO
						.retrievePollutantsControlled(ceFpnodeId);

				for (PollutantsControlled tempPoll : pollutantsControlled) {
					if (tempPoll.getPollutantCd().equals(pollutantCd)) {
						return true;
					}
				}
			}
		}
		return ret;
	}

	@Override
	public ValidationMessage[] validatePollutantLimits(
			List<TVEUPollutantLimit> pollutantLimits) {
		LinkedList<ValidationMessage> retVal = new LinkedList<ValidationMessage>();
		for (TVEUPollutantLimit pollutantLimit : pollutantLimits) {
			ValidationMessage[] valMsgs = pollutantLimit.validate();

			if (valMsgs != null && valMsgs.length > 0) {
				retVal.addAll(Arrays.asList(valMsgs));
			}

		}

		return retVal.toArray(new ValidationMessage[0]);
	}

	@Override
	public boolean modifyPollutantLimits(TVApplicationEU tvAppEu)
			throws DAOException {
		Transaction trans = null;
		boolean ret = false;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO applicationDAO = applicationDAO(trans);

			// lower validation flag
			applicationDAO.setApplicationValidatedFlag(
					tvAppEu.getApplicationId(), false);

			applicationDAO.setApplicationEUValidatedFlag(
					tvAppEu.getApplicationEuId(), false);

			// remove previous pollutant limits
			applicationDAO.removeTVEUPollutantLimits(tvAppEu
					.getApplicationEuId());

			// add
			for (TVEUPollutantLimit pollutantLimit : tvAppEu
					.getPollutantLimits()) {
				applicationDAO.addTVEUPollutantLimit(
						tvAppEu.getApplicationEuId(), pollutantLimit);
			}

			trans.complete();
			ret = true;
		} catch (DAOException de) {
			ret = false;
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	@Override
	public void createFacilityWideRequirement(FacilityWideRequirement facWideReq)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		appDAO.createFacilityWideRequirement(facWideReq);
	}

	@Override
	public void modifyFacilityWideRequirement(FacilityWideRequirement facWideReq)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		appDAO.modifyFacilityWideRequirement(facWideReq);
	}

	@Override
	public void removeFacilityWideRequirement(Integer requirementId)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		appDAO.removeFacilityWideRequirement(requirementId);
	}

	@Override
	public List<FacilityWideRequirement> retrieveFacilityWideRequirements(
			Integer applicationId) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.STAGING_SCHEMA));
		return appDAO.retrieveFacilityWideRequirements(applicationId);
	}

	@Override
	public ValidationMessage[] validateOperationalRestrictions(
			List<TVEUOperationalRestriction> operationalRestrictions) {
		LinkedList<ValidationMessage> retVal = new LinkedList<ValidationMessage>();
		for (TVEUOperationalRestriction operationalRestriction : operationalRestrictions) {
			ValidationMessage[] valMsgs = operationalRestriction.validate();

			if (valMsgs != null && valMsgs.length > 0) {
				retVal.addAll(Arrays.asList(valMsgs));
			}

		}

		return retVal.toArray(new ValidationMessage[0]);
	}

	@Override
	public boolean modifyOperationalRestrictions(TVApplicationEU tvAppEu)
			throws DAOException {
		Transaction trans = null;
		boolean ret = false;

		try {
			trans = TransactionFactory.createTransaction();
			ApplicationDAO applicationDAO = applicationDAO(trans);

			// lower validation flag
			applicationDAO.setApplicationValidatedFlag(
					tvAppEu.getApplicationId(), false);

			applicationDAO.setApplicationEUValidatedFlag(
					tvAppEu.getApplicationEuId(), false);

			// remove previous operational restrictions
			applicationDAO.removeTVEUOperationalRestrictions(tvAppEu
					.getApplicationEuId());

			// add
			for (TVEUOperationalRestriction operationalRestriction : tvAppEu
					.getOperationalRestrictions()) {
				applicationDAO.addTVEUOperationalRestriction(
						tvAppEu.getApplicationEuId(), operationalRestriction);
			}

			trans.complete();
			ret = true;
		} catch (DAOException de) {
			ret = false;
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		ret = true;

		return ret;
	}

	@Override
	public List<NSRApplicationLAEREmission> retrieveNSRApplicationLaerEmissions(
			Integer applicationEuId) throws DAOException {
		NSRApplicationLAEREmission[] laerEmissions = getApplicationDAO()
				.retrieveNSRApplicationLaerEmissions(applicationEuId);
		return Utility.createArrayList(laerEmissions);
	}

	@Override
	public void saveNSRApplicationLaerEmissions(
			List<NSRApplicationLAEREmission> laerEmissions,
			Integer applicationEuId) throws DAOException {
		ApplicationDAO appDAO = getApplicationDAO();
		// remove previous LAER emissions
		appDAO.removeLAEREmissions(applicationEuId);
		// add LAER emissions
		for (NSRApplicationLAEREmission laerEmission : laerEmissions) {
			laerEmission.setApplicationEuId(applicationEuId);
			appDAO.addLAEREmission(laerEmission);
		}
	}

	@Override
	public List<NSRApplicationBACTEmission> retrieveNSRApplicationBactEmissions(
			Integer applicationEuId) throws DAOException {
		NSRApplicationBACTEmission[] bactEmissions = getApplicationDAO()
				.retrieveNSRApplicationBactEmissions(applicationEuId);
		return Utility.createArrayList(bactEmissions);
	}

	@Override
	public void saveNSRApplicationBactEmissions(
			List<NSRApplicationBACTEmission> bactEmissions,
			Integer applicationEuId) throws DAOException {
		ApplicationDAO appDAO = getApplicationDAO();
		// remove previous BACT emissions
		appDAO.removeBACTEmissions(applicationEuId);
		// add BACT emissions
		for (NSRApplicationBACTEmission bactEmission : bactEmissions) {
			bactEmission.setApplicationEuId(applicationEuId);
			appDAO.addBACTEmission(bactEmission);
		}
	}

	@Override
	public void verifyNSRAppRequiredAttachments(PTIOApplication ptioApp)
			throws Exception {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			verifyNSRAppRequiredAttachments(trans, ptioApp);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
	}

	@Override
	public void verifyNSRAppEURequiredAttachments(PTIOApplication ptioApp,
			PTIOApplicationEU ptioAppEU) throws Exception {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			verifyNSRAppEURequiredAttachments(trans, ptioApp, ptioAppEU);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
	}

	@Override
	public void verifyTVAppRequiredAttachments(TVApplication tvApp)
			throws Exception {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			verifyTVAppRequiredAttachments(trans, tvApp);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
	}

	@Override
	public void verifyTVAppEURequiredAttachments(TVApplication tvApp,
			TVApplicationEU tvAppEU) throws Exception {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			verifyTVAppEURequiredAttachments(trans, tvApp, tvAppEU);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
	}

	@Override
	public void verifyNSRAppRequiredAttachments(Transaction trans,
			PTIOApplication ptioApp) throws Exception {
		if (ptioApp == null)
			throw new DAOException("NSR Applicatin cannot be null or empty.");

		ApplicationDAO appDAO = applicationDAO(
				getSchema(CommonConst.STAGING_SCHEMA), trans);

		verifyNSRAppLandUsePlanning(appDAO, ptioApp);
		verifyNSRAppFacilityMap(appDAO, ptioApp);
		verifyNSRAppProcessFlowDiagram(appDAO, ptioApp);
		verifyNSRAppModelingAnalysis(appDAO, ptioApp);
		verifyOtherTypeOfDemonstration(appDAO, ptioApp);
		verifyNSRAppEmissionsCalculations(appDAO, ptioApp);
		verifyNSRAppCoverLetterProjectDescription(appDAO, ptioApp);
		if (ptioApp.getFacility().getFacilityTypeCd().equalsIgnoreCase(FacilityTypeDef.PROD_SITE)) {
			verifyNSRAppEquipmentList(appDAO, ptioApp);
		}

	}

	@Override
	public void verifyNSRAppEURequiredAttachments(Transaction trans,
			PTIOApplication ptioApp, PTIOApplicationEU ptioAppEU)
			throws Exception {
		if (ptioApp == null)
			throw new Exception("NSR Applicatin cannot be null or empty.");

		if (ptioAppEU == null)
			throw new Exception("NSR Applicatin EU cannot be null or empty.");

		ApplicationDAO appDAO = applicationDAO(
				getSchema(CommonConst.STAGING_SCHEMA), trans);

		verifyNSRAppEUBACTAnalysis(appDAO, ptioApp, ptioAppEU);
		verifyNSRAppEULAERAnalysis(appDAO, ptioApp, ptioAppEU);
		// AQD does not want to require a Basis For Determination document
		//verifyNSRAppEUBasisForDetermination(appDAO, ptioApp, ptioAppEU);

		trans.complete();

	}

	@Override
	public void verifyTVAppRequiredAttachments(Transaction trans,
			TVApplication tvApp) throws Exception {
		if (tvApp == null)
			throw new Exception("Title V Applicatin cannot be null or empty.");

		ApplicationDAO appDAO = applicationDAO(
				getSchema(CommonConst.STAGING_SCHEMA), trans);

		verifyTVAppAlternateOperatingScenario(appDAO, tvApp);
		verifyTVAppDepressurization(appDAO, tvApp);
		verifyTVAppCompliancePlan(appDAO, tvApp);
		verifyTVAppCAMPlan(appDAO, tvApp);
		verifyTVAppProcessFlowDiagram(appDAO, tvApp);
		verifyTVAppSitePlotPlan(appDAO, tvApp);
		verifyTVAppAmbientMonitoringForm(appDAO, tvApp);
		verifyTVAppAcidRain(appDAO, tvApp);
		verifyTVAppInsignificantActivitiesForm(appDAO, tvApp);
	}

	@Override
	public void verifyTVAppEURequiredAttachments(Transaction trans,
			TVApplication tvApp, TVApplicationEU tvAppEU) throws Exception {
		if (tvApp == null)
			throw new Exception("Title V Applicatin cannot be null or empty.");

		if (tvAppEU == null)
			throw new Exception(
					"Title V Applicatin EU cannot be null or empty.");

		ApplicationDAO appDAO = applicationDAO(
				getSchema(CommonConst.STAGING_SCHEMA), trans);

		verifyTVAppEUAlternateOperatingScenario(appDAO, tvApp, tvAppEU);
		verifyTVAppEUPTECalculation(appDAO, tvApp, tvAppEU);
		verifyTVAppEUBasisForDetermination(appDAO, tvApp, tvAppEU);
		verifyTVAppEUPreControlPTECalculation(appDAO, tvApp, tvAppEU);
	}

	private final void createRequiredDoc(ApplicationDAO appDAO,
			Application application, String typeCd) {
		createRequiredDoc(appDAO, application, null, typeCd);
	}

	private final void createRequiredDoc(ApplicationDAO appDAO,
			Application application, ApplicationEU appplicationEU, String typeCd) {
		Integer appId = application.getApplicationID();
		Integer appEuId = null;

		if (appplicationEU != null) {
			appEuId = appplicationEU.getApplicationEuId();
		}

		try {
			if (appDAO
					.checkApplicationTypeIsInDb(appId, appEuId, typeCd, false)) {
				appDAO.changeOptionAttachmnetToRequired(appId, appEuId, typeCd);
				return;
			}

			ApplicationDocumentRef docRef = new ApplicationDocumentRef();
			docRef.setDescription("");
			docRef.setApplicationDocumentTypeCD(typeCd);
			docRef.setApplicationId(appId);
			docRef.setRequiredDoc(true);
			docRef.setApplicationEUId(appEuId);

			docRef = appDAO.createApplicationDocument(docRef);
			
			if (null == appplicationEU) {
				application.addDocument(docRef);
			} else {
				appplicationEU.getEuDocuments().add(docRef);
			}
			
		} catch (RemoteException e) {
			DisplayUtil
					.displayInfo("There is an error when the required attachments has been updated. Message: "
							+ e.getMessage());
			logger.error(e.getMessage());
		}
	}

	private void verifyNSRAppFacilityMap(ApplicationDAO appDAO,
			PTIOApplication ptioApp) {
		String changedLocationFlag = ptioApp.getFacilityChangedLocationFlag();

		Integer applicationId = ptioApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					ApplicationDocumentTypeDef.FACILITY_MAP, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		boolean isChangedLocation = !Utility.isNullOrEmpty(changedLocationFlag)
				&& changedLocationFlag.equalsIgnoreCase("Y");

		if (isChangedLocation && !isExisting) {
			createRequiredDoc(appDAO, ptioApp,
					ApplicationDocumentTypeDef.FACILITY_MAP);
			return;
		}

		if (!isChangedLocation && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, ptioApp, null,
						ApplicationDocumentTypeDef.FACILITY_MAP);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyNSRAppLandUsePlanning(ApplicationDAO appDAO,
			PTIOApplication ptioApp) {
		String changedLocationFlag = ptioApp.getFacilityChangedLocationFlag();
		String landUsePlanning = ptioApp.getLandUsePlanningFlag();

		Integer applicationId = ptioApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					ApplicationDocumentTypeDef.LAND_USE_PLANING, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		boolean isChangedLocation = !Utility.isNullOrEmpty(changedLocationFlag)
				&& changedLocationFlag.equalsIgnoreCase("Y");
		boolean isUsePalnning = !Utility.isNullOrEmpty(landUsePlanning)
				&& landUsePlanning.equalsIgnoreCase("Y");

		if (isChangedLocation && isUsePalnning && !isExisting) {
			createRequiredDoc(appDAO, ptioApp,
					ApplicationDocumentTypeDef.LAND_USE_PLANING);
			return;
		}

		if ((!isChangedLocation || !isUsePalnning) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, ptioApp, null,
						ApplicationDocumentTypeDef.LAND_USE_PLANING);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyNSRAppProcessFlowDiagram(ApplicationDAO appDAO,
			PTIOApplication ptioApp) {
		Integer applicationId = ptioApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					ApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		String psdFlag = ptioApp.getPsdApplicableFlag();
		boolean isSubject = !Utility.isNullOrEmpty(psdFlag)
				&& psdFlag.equalsIgnoreCase(PTIOFedRuleAppl2Def.SUBJECT_TO_REG);

		// When the facility type is production site or dehydration,
		// or facility class is Title V, the process flow diagram is required.
		Facility facility = ptioApp.getFacility();
		String facilityTypeCd = facility.getFacilityTypeCd();
		String facilityClassCd = facility.getPermitClassCd();

		boolean isFacilityRequired = facilityClassCd
				.equalsIgnoreCase(PermitClassDef.TV)
				|| (!Utility.isNullOrEmpty(facilityTypeCd) && (facilityTypeCd
						.equalsIgnoreCase(FacilityTypeDef.PRODUCTION_SITE) || facilityTypeCd
						.equalsIgnoreCase(FacilityTypeDef.DEHYDRATION)));

		if ((isSubject || isFacilityRequired) && !isExisting) {
			createRequiredDoc(appDAO, ptioApp,
					ApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM);
			return;
		}

		if (!(isSubject || isFacilityRequired) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, ptioApp, null,
						ApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyNSRAppModelingAnalysis(ApplicationDAO appDAO,
			PTIOApplication ptioApp) {
		Integer applicationId = ptioApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					ApplicationDocumentTypeDef.MODELING_ANALYSIS, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		String modelingAnalysisFlag = ptioApp.getModelingAnalysisFlag();

		boolean isModelingAnalysis = !Utility
				.isNullOrEmpty(modelingAnalysisFlag)
				&& modelingAnalysisFlag.equalsIgnoreCase("Y");

		if (isModelingAnalysis && !isExisting) {
			createRequiredDoc(appDAO, ptioApp,
					ApplicationDocumentTypeDef.MODELING_ANALYSIS);
			return;
		}

		if (!isModelingAnalysis && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, ptioApp, null,
						ApplicationDocumentTypeDef.MODELING_ANALYSIS);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyOtherTypeOfDemonstration(ApplicationDAO appDAO,
			PTIOApplication ptioApp) throws DAOException {
		Integer applicationId = ptioApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					ApplicationDocumentTypeDef.OTHER_TYPE_OF_DEMONSTRATION, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		
		boolean facilityInNonattainmentZone = 
				isFacilityInNonattainmentZone(ptioApp.getFacility().getFpId());

		if (facilityInNonattainmentZone && !isExisting) {
			createRequiredDoc(appDAO, ptioApp,
					ApplicationDocumentTypeDef.OTHER_TYPE_OF_DEMONSTRATION);
			return;
		}

		if (!facilityInNonattainmentZone && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, ptioApp, null,
						ApplicationDocumentTypeDef.OTHER_TYPE_OF_DEMONSTRATION);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyNSRAppEmissionsCalculations(ApplicationDAO appDAO,
			PTIOApplication ptioApp) {
		Integer applicationId = ptioApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					ApplicationDocumentTypeDef.EMISSIONS_CALCULATIONS, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		if (!isExisting) {
			createRequiredDoc(appDAO, ptioApp,
					ApplicationDocumentTypeDef.EMISSIONS_CALCULATIONS);
			return;
		}
	}
	
	private void verifyNSRAppCoverLetterProjectDescription(ApplicationDAO appDAO,
			PTIOApplication ptioApp) {
		Integer applicationId = ptioApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					ApplicationDocumentTypeDef.COVER_LETTER_PROJECT_DESCRIPTION, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		if (!isExisting) {
			createRequiredDoc(appDAO, ptioApp,
					ApplicationDocumentTypeDef.COVER_LETTER_PROJECT_DESCRIPTION);
			return;
		}
	}
	
	
	private void verifyNSRAppEquipmentList(ApplicationDAO appDAO,
			PTIOApplication ptioApp) {
		Integer applicationId = ptioApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					ApplicationDocumentTypeDef.EQUIPMENT_LIST, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		if (!isExisting) {
			createRequiredDoc(appDAO, ptioApp,
					ApplicationDocumentTypeDef.EQUIPMENT_LIST);
			return;
		}
	}

	private void verifyNSRAppEUBACTAnalysis(ApplicationDAO appDAO,
			PTIOApplication ptioApp, PTIOApplicationEU ptioAppEu) {

		Integer applicationId = ptioAppEu.getApplicationId();
		Integer appEuId = ptioAppEu.getApplicationEuId();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId,
					appEuId, ApplicationDocumentTypeDef.BACT_ANALYSIS, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		String bactFlag = ptioAppEu.getBactFlag();

		boolean isBactFlag = !Utility.isNullOrEmpty(bactFlag)
				&& bactFlag.equalsIgnoreCase("Y");

		if (isBactFlag && !isExisting) {
			createRequiredDoc(appDAO, ptioApp, ptioAppEu,
					ApplicationDocumentTypeDef.BACT_ANALYSIS);
			return;
		}

		if (!isBactFlag && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, ptioApp, ptioAppEu,
						ApplicationDocumentTypeDef.BACT_ANALYSIS);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyNSRAppEULAERAnalysis(ApplicationDAO appDAO,
			PTIOApplication ptioApp, PTIOApplicationEU ptioAppEU) {
		Integer applicationId = ptioAppEU.getApplicationId();
		Integer appEuId = ptioAppEU.getApplicationEuId();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId,
					appEuId, ApplicationDocumentTypeDef.LAER_ANALYSIS, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		String laerFlag = ptioAppEU.getLaerFlag();

		boolean isBactFlag = !Utility.isNullOrEmpty(laerFlag)
				&& laerFlag.equalsIgnoreCase("Y");

		if (isBactFlag && !isExisting) {
			createRequiredDoc(appDAO, ptioApp, ptioAppEU,
					ApplicationDocumentTypeDef.LAER_ANALYSIS);
			return;
		}

		if (!isBactFlag && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, ptioApp, ptioAppEU,
						ApplicationDocumentTypeDef.LAER_ANALYSIS);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyNSRAppEUBasisForDetermination(ApplicationDAO appDAO,
			PTIOApplication ptioApp, PTIOApplicationEU ptioAppEU) {

		Integer applicationId = ptioAppEU.getApplicationId();
		Integer appEuId = ptioAppEU.getApplicationEuId();
		boolean isOtherSelected = isOtherSelectedForBasisForDetermination(
				appDAO, ptioAppEU);
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId,
					appEuId,
					ApplicationDocumentTypeDef.BASIS_FOR_DETERMINATION, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isOtherSelected && !isExisting) {
			createRequiredDoc(appDAO, ptioApp, ptioAppEU,
					ApplicationDocumentTypeDef.BASIS_FOR_DETERMINATION);   
			return;
		}

		if (!isOtherSelected && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, ptioApp, ptioAppEU,
						ApplicationDocumentTypeDef.BASIS_FOR_DETERMINATION);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknown exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private boolean isOtherSelectedForBasisForDetermination(
			ApplicationDAO appDAO, PTIOApplicationEU ptioAppEu) {
		boolean isOtherSelected = false;
		List<ApplicationEUEmissions> capEmissions = ptioAppEu.getCapEmissions();
		if (capEmissions != null) {
			for (ApplicationEUEmissions emission : capEmissions) {
				if (emission.getPteDeterminationBasisCd() != null
						&& (emission.getPteDeterminationBasisCd()
								.equalsIgnoreCase(PAEuPteDeterminationBasisDef.OTHERS))) {
					isOtherSelected = true;
					break;
				}

			}
		}
		List<ApplicationEUEmissions> hapEmissions = ptioAppEu
				.getHapTacEmissions();
		if (hapEmissions != null) {
			for (ApplicationEUEmissions emission : hapEmissions) {
				if (emission.getPteDeterminationBasisCd() != null
						&& (emission.getPteDeterminationBasisCd()
								.equalsIgnoreCase(PAEuPteDeterminationBasisDef.OTHERS))) {
					isOtherSelected = true;
					break;
				}

			}
		}
		List<ApplicationEUEmissions> ghgEmissions = ptioAppEu.getGhgEmissions();
		if (ghgEmissions != null) {
			for (ApplicationEUEmissions emission : ghgEmissions) {
				if (emission.getPteDeterminationBasisCd() != null
						&& (emission.getPteDeterminationBasisCd()
								.equalsIgnoreCase(PAEuPteDeterminationBasisDef.OTHERS))) {
					isOtherSelected = true;
					break;
				}

			}
		}
		return isOtherSelected;
	}

	private void verifyTVAppAlternateOperatingScenario(ApplicationDAO appDAO,
			TVApplication tvApp) {
		boolean isAlternateOperatingScenario = tvApp
				.isAlternateOperatingScenariosAuthorized();

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.ALTERNATE_OPERATING_SCENARIO,
					true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		if (isAlternateOperatingScenario && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.ALTERNATE_OPERATING_SCENARIO);
			return;
		}

		if (!isAlternateOperatingScenario && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(
						appDAO,
						tvApp,
						null,
						TVApplicationDocumentTypeDef.ALTERNATE_OPERATING_SCENARIO);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppDepressurization(ApplicationDAO appDAO,
			TVApplication tvApp) {
		String purposeCd = tvApp.getTvApplicationPurposeCd();
		String facilityTypeCd = tvApp.getFacility().getFacilityTypeCd();
		boolean isOilAndGasType = FacilityTypeDef.isOilAndGas(facilityTypeCd);
		boolean isInitOrRenew = !Utility.isNullOrEmpty(purposeCd)
				&& (purposeCd
						.equals(TVApplicationPurposeDef.INITIAL_APPLICATION) || purposeCd
						.equals(TVApplicationPurposeDef.RENEWAL));

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.DEPRESSURIZATION_EMISSIONS,
					true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		if (isInitOrRenew && isOilAndGasType && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.DEPRESSURIZATION_EMISSIONS);
			return;
		}

		if (!(isInitOrRenew && isOilAndGasType) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, null,
						TVApplicationDocumentTypeDef.DEPRESSURIZATION_EMISSIONS);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppCompliancePlan(ApplicationDAO appDAO,
			TVApplication tvApp) {
		String purposeCd = tvApp.getTvApplicationPurposeCd();
		String permitReasonCd = tvApp.getPermitReasonCd();
		boolean isInitOrRenew = !Utility.isNullOrEmpty(purposeCd)
				&& (purposeCd
						.equals(TVApplicationPurposeDef.INITIAL_APPLICATION)
						|| purposeCd.equals(TVApplicationPurposeDef.RENEWAL));

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.COMPLIANCE_PLAN_FORM, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isInitOrRenew && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.COMPLIANCE_PLAN_FORM);
			return;
		}

		if (!(isInitOrRenew) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, null,
						TVApplicationDocumentTypeDef.COMPLIANCE_PLAN_FORM);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppCAMPlan(ApplicationDAO appDAO, TVApplication tvApp) {
		boolean isCAM = false;

		EU_LOOP: for (ApplicationEU appEu : tvApp.getIncludedEus()) {
			if (appEu instanceof TVApplicationEU) {
				TVApplicationEU tvAppEu = (TVApplicationEU) appEu;

				for (TVEUPollutantLimit pollutantLimit : tvAppEu
						.getPollutantLimits()) {
					isCAM = pollutantLimit.isCamCompliant();

					if (isCAM) {
						break EU_LOOP;
					}
				}
			}
		}

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.CAM_PLAN, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isCAM && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.CAM_PLAN);
			return;
		}

		if (!(isCAM) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, null,
						TVApplicationDocumentTypeDef.CAM_PLAN);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppProcessFlowDiagram(ApplicationDAO appDAO,
			TVApplication tvApp) {
		String purposeCd = tvApp.getTvApplicationPurposeCd();

		boolean isInitOrRenew = !Utility.isNullOrEmpty(purposeCd)
				&& (purposeCd
						.equals(TVApplicationPurposeDef.INITIAL_APPLICATION) || purposeCd
						.equals(TVApplicationPurposeDef.RENEWAL));

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isInitOrRenew && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM);
			return;
		}

		if (!isInitOrRenew && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, null,
						TVApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attachment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppSitePlotPlan(ApplicationDAO appDAO,
			TVApplication tvApp) {
		String purposeCd = tvApp.getTvApplicationPurposeCd();
		boolean isInitOrRenew = !Utility.isNullOrEmpty(purposeCd)
				&& (purposeCd
						.equals(TVApplicationPurposeDef.INITIAL_APPLICATION) || purposeCd
						.equals(TVApplicationPurposeDef.RENEWAL));

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.SITE_PLOT_PLAN, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isInitOrRenew && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.SITE_PLOT_PLAN);
			return;
		}

		if (!(isInitOrRenew) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, null,
						TVApplicationDocumentTypeDef.SITE_PLOT_PLAN);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppAmbientMonitoringForm(ApplicationDAO appDAO,
			TVApplication tvApp) {
		String purposeCd = tvApp.getTvApplicationPurposeCd();
		boolean isInitOrRenew = !Utility.isNullOrEmpty(purposeCd)
				&& (purposeCd
						.equals(TVApplicationPurposeDef.INITIAL_APPLICATION) || purposeCd
						.equals(TVApplicationPurposeDef.RENEWAL));
		boolean isAmbientMonitoring = tvApp.isAmbientMonitoringAllowed();

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.AMBIENT_MONITORING_FORM, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isInitOrRenew && isAmbientMonitoring && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.AMBIENT_MONITORING_FORM);
			return;
		}

		if (!(isInitOrRenew && isAmbientMonitoring) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, null,
						TVApplicationDocumentTypeDef.AMBIENT_MONITORING_FORM);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attachment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppAcidRain(ApplicationDAO appDAO, TVApplication tvApp) {
		String purposeCd = tvApp.getTvApplicationPurposeCd();
		boolean isInitOrRenew = !Utility.isNullOrEmpty(purposeCd)
				&& (purposeCd
						.equals(TVApplicationPurposeDef.INITIAL_APPLICATION) || purposeCd
						.equals(TVApplicationPurposeDef.RENEWAL));
		boolean isAcidRain = tvApp.isSubjectToTIVAct();

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.TIV_ACID_RAIN_APP, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isInitOrRenew && isAcidRain && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.TIV_ACID_RAIN_APP);
			return;
		}

		if (!(isInitOrRenew && isAcidRain) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, null,
						TVApplicationDocumentTypeDef.TIV_ACID_RAIN_APP);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppEUAlternateOperatingScenario(ApplicationDAO appDAO,
			TVApplication tvApp, TVApplicationEU tvAppEU) {
		boolean isAOSAutherized = tvAppEU.getNormalOperatingScenario()
				.isOpAosAutherizedAllowed();
		Integer appEuId = tvAppEU.getApplicationEuId();
		Integer applicationId = tvAppEU.getApplicationId();
		boolean isExisting = true;

		try {
			isExisting = appDAO
					.checkApplicationTypeIsInDb(
							applicationId,
							appEuId,
							TVApplicationDocumentTypeDef.EU_ALTERNATE_OPERATING_SCENARIO,
							true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isAOSAutherized && !isExisting) {
			createRequiredDoc(
					appDAO,
					tvApp,
					tvAppEU,
					TVApplicationDocumentTypeDef.EU_ALTERNATE_OPERATING_SCENARIO);
			return;
		}

		if (!(isAOSAutherized) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(
						appDAO,
						tvApp,
						tvAppEU,
						TVApplicationDocumentTypeDef.EU_ALTERNATE_OPERATING_SCENARIO);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppEUPTECalculation(ApplicationDAO appDAO,
			TVApplication tvApp, TVApplicationEU tvAppEU) {

		boolean isPTE = false;
		boolean nonZeroPTE = isNonZeroPTEForTVApplication(tvAppEU);

		if (tvApp.getTvApplicationPurposeCd() != null) {
			if ((tvApp.getTvApplicationPurposeCd().equals(
					TVApplicationPurposeDef.INITIAL_APPLICATION)
					|| tvApp.getTvApplicationPurposeCd().equals(
							TVApplicationPurposeDef.RENEWAL))
					&& nonZeroPTE)		
				isPTE = true;
			else if (tvApp.getTvApplicationPurposeCd().equals(
					TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING)) {
				if (tvApp.getPermitReasonCd() != null && tvApp.getPermitReasonCd().equals(PermitReasonsDef.SPM)
						&& nonZeroPTE)
					isPTE = true;
			}
		}
		Integer applicationId = tvApp.getApplicationID();
		Integer appEuId = tvAppEU.getApplicationEuId();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId,
					appEuId, TVApplicationDocumentTypeDef.PTE_CALCULATIONS,
					true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isPTE && !isExisting) {
			createRequiredDoc(appDAO, tvApp, tvAppEU,
					TVApplicationDocumentTypeDef.PTE_CALCULATIONS);
			return;
		}

		if (!(isPTE) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, tvAppEU,
						TVApplicationDocumentTypeDef.PTE_CALCULATIONS);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppEUPreControlPTECalculation(ApplicationDAO appDAO,
			TVApplication tvApp, TVApplicationEU tvAppEU) {

		boolean isPollutantControlled = false;
		for (TVEUPollutantLimit pollutantLimit : tvAppEU.getPollutantLimits()) {
			isPollutantControlled = pollutantLimit.isControlled();
			if (isPollutantControlled)
				break;
		}

		Integer applicationId = tvApp.getApplicationID();
		Integer appEuId = tvAppEU.getApplicationEuId();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId,
					appEuId,
					TVApplicationDocumentTypeDef.PRE_CONTROL_PTE_CALCULATIONS,
					true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isPollutantControlled && !isExisting) {
			createRequiredDoc(appDAO, tvApp, tvAppEU,
					TVApplicationDocumentTypeDef.PRE_CONTROL_PTE_CALCULATIONS);
			return;
		}

		if (!(isPollutantControlled) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(
						appDAO,
						tvApp,
						tvAppEU,
						TVApplicationDocumentTypeDef.PRE_CONTROL_PTE_CALCULATIONS);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppInsignificantActivitiesForm(ApplicationDAO appDAO,
			TVApplication tvApp) {

		String purposeCd = tvApp.getTvApplicationPurposeCd();
		boolean isInitOrRenew = !Utility.isNullOrEmpty(purposeCd)
				&& (purposeCd
						.equals(TVApplicationPurposeDef.INITIAL_APPLICATION) || purposeCd
						.equals(TVApplicationPurposeDef.RENEWAL));

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.INSIGNIFICANT_ACTIVITIES_FORM,
					true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isInitOrRenew && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.INSIGNIFICANT_ACTIVITIES_FORM);
			return;
		}

		if (!(isInitOrRenew) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(
						appDAO,
						tvApp,
						null,
						TVApplicationDocumentTypeDef.INSIGNIFICANT_ACTIVITIES_FORM);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private void verifyTVAppSpecialCircumstances(ApplicationDAO appDAO,
			TVApplication tvApp) {

		boolean isSC = false;

		EU_LOOP: for (ApplicationEU appEu : tvApp.getEus()) {
			if (appEu instanceof TVApplicationEU) {
				TVApplicationEU tvAppEu = (TVApplicationEU) appEu;

				for (TVEUPollutantLimit pollutantLimit : tvAppEu
						.getPollutantLimits()) {
					isSC = true;
					// !pollutantLimit.getPotentialEmissions().equals(PTE table)

					if (isSC) {
						break EU_LOOP;
					}
				}
			}
		}

		Integer applicationId = tvApp.getApplicationID();
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId, null,
					TVApplicationDocumentTypeDef.SPECIAL_CIRUMSTANCES, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknown error when the system generated the required attachment.");
		}

		if (isSC && !isExisting) {
			createRequiredDoc(appDAO, tvApp,
					TVApplicationDocumentTypeDef.SPECIAL_CIRUMSTANCES);
			return;
		}

		if (!(isSC) && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, null,
						TVApplicationDocumentTypeDef.SPECIAL_CIRUMSTANCES);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	public List<ApplicationEUFugitiveLeaks> retrieveApplicationEUFugitiveLeaks(
			Integer appEuId) throws DAOException {
		ApplicationDAO appDAO = getApplicationDAO();
		return appDAO.retrieveApplicationEUFugitiveLeaks(appEuId);
	}

	@Override
	public void saveApplicationEUFugitiveLeaks(
			List<ApplicationEUFugitiveLeaks> applicationEUFugitiveLeaks,
			Integer appEuId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			ApplicationDAO appDAO = applicationDAO(trans);
			appDAO.removeApplicationEUFugitiveLeaks(appEuId);
			for (ApplicationEUFugitiveLeaks applicationEUFugitiveLeak : applicationEUFugitiveLeaks) {
				applicationEUFugitiveLeak.setApplicationEuId(appEuId);
				appDAO.createApplicationEUFugitiveLeaks(applicationEUFugitiveLeak);
			}
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("appEuId=" + appEuId.toString(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

	}

	private List<ApplicationEUMaterialUsed> getPTIOMaterialUsed(Integer appEUId) {
		List<ApplicationEUMaterialUsed> materialUsed = new ArrayList<ApplicationEUMaterialUsed>();
		for (String materialUsedCd : getPTIOMaterialUsedDefs().keySet()) {
			materialUsed.add(new ApplicationEUMaterialUsed(appEUId,
					materialUsedCd));
		}
		return materialUsed;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @return
	 */
	public String getCmxEmissionsMaterialUsedValueFormat() {
		if (cmxEmissionsMaterialUsedValueFormat == null) {
			cmxEmissionsMaterialUsedValueFormat = SystemPropertyDef.getSystemPropertyValue("APP_CMX_EMISSIONS_MATERIAL_USED", null);
			if (cmxEmissionsMaterialUsedValueFormat == null) {
				cmxEmissionsMaterialUsedValueFormat = DFLT_CMX_EMISSIONS_MATERIAL_USED_VALUE_FORMAT;
				logger.warn("No value specified for APP_CMX_EMISSIONS_MATERIAL_USED in params.xml."
						+ "Defaulting to "
						+ DFLT_CMX_EMISSIONS_MATERIAL_USED_VALUE_FORMAT);
			}
		}
		return cmxEmissionsMaterialUsedValueFormat;
	}

	@Override
	public ArrayList<TVApplicationDocumentTypeDef> retrieveTvApplicationDocumentTypes()
			throws DAOException, RemoteException {
		ApplicationDAO applicationDao = (ApplicationDAO) getDAO(ApplicationDAO.class);
		return applicationDao.retrieveTvApplicationDocumentTypes();
	}

	@Override
	public ArrayList<ApplicationDocumentTypeDef> retrieveApplicationDocumentTypes()
			throws DAOException, RemoteException {
		ApplicationDAO applicationDao = (ApplicationDAO) getDAO(ApplicationDAO.class);
		return applicationDao.retrieveApplicationDocumentTypes();
	}

	@Override
	public ApplicationDocumentTypeDef retrieveApplicationDocTypeDef(
			String applicationDocumentTypeCD) throws DAOException,
			RemoteException {
		ApplicationDAO applicationDao = (ApplicationDAO) getDAO(ApplicationDAO.class);
		return applicationDao
				.retrieveApplicationDocTypeDef(applicationDocumentTypeCD);
	}

	@Override
	public ApplicationDocumentTypeDef retrieveTVApplicationDocTypeDef(
			String applicationDocumentTypeCD) throws DAOException,
			RemoteException {
		ApplicationDAO applicationDao = (ApplicationDAO) getDAO(ApplicationDAO.class);
		return applicationDao
				.retrieveTVApplicationDocTypeDef(applicationDocumentTypeCD);
	}

	private void verifyTVAppEUBasisForDetermination(ApplicationDAO appDAO,
			TVApplication tvApp, TVApplicationEU tvAppEU) {

		Integer applicationId = tvAppEU.getApplicationId();
		Integer appEuId = tvAppEU.getApplicationEuId();
		boolean isOtherSelected = isOtherSelectedForBasisForDeterminationForTVApplication(
				appDAO, tvAppEU);
		boolean isExisting = true;

		try {
			isExisting = appDAO.checkApplicationTypeIsInDb(applicationId,
					appEuId,
					TVApplicationDocumentTypeDef.BASIS_FOR_DETERMINATION, true);

		} catch (Exception e) {
			logger.error("applicationBO.checkRequiredApplicationTypeIsInDb ERROR: "
					+ e.getMessage());
			DisplayUtil
					.displayError("Unknow error when the system generated the required attachment.");
		}

		if (isOtherSelected && !isExisting) {
			createRequiredDoc(appDAO, tvApp, tvAppEU,
					TVApplicationDocumentTypeDef.BASIS_FOR_DETERMINATION);
			return;
		}

		if (!isOtherSelected && isExisting) {
			try {
				removeAppRequiredAttachmnetDoc(appDAO, tvApp, tvAppEU,
						TVApplicationDocumentTypeDef.BASIS_FOR_DETERMINATION);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in removing required attahcment");
				logger.error(e.getMessage());
			}
		}
	}

	private boolean isOtherSelectedForBasisForDeterminationForTVApplication(
			ApplicationDAO appDAO, TVApplicationEU tvAppEU) {
		boolean isOtherSelected = false;

		if (tvAppEU.getNormalOperatingScenario() != null) {
			TVEUOperatingScenario scenario = tvAppEU
					.getNormalOperatingScenario();
			for (TVApplicationEUEmissions emissions : scenario
					.getCapEmissions()) {
				if (emissions.getPteDeterminationBasis() != null
						&& (emissions.getPteDeterminationBasis()
								.equalsIgnoreCase(PAEuPteDeterminationBasisDef.OTHERS))) {
					isOtherSelected = true;
					break;
				}

			}
			for (TVApplicationEUEmissions emissions : scenario
					.getHapEmissions()) {
				if (emissions.getPteDeterminationBasis() != null
						&& (emissions.getPteDeterminationBasis()
								.equalsIgnoreCase(PAEuPteDeterminationBasisDef.OTHERS))) {
					isOtherSelected = true;
					break;
				}

			}
			for (TVApplicationEUEmissions emissions : scenario
					.getGhgEmissions()) {
				if (emissions.getPteDeterminationBasis() != null
						&& (emissions.getPteDeterminationBasis()
								.equalsIgnoreCase(PAEuPteDeterminationBasisDef.OTHERS))) {
					isOtherSelected = true;
					break;
				}

			}
			for (TVApplicationEUEmissions emissions : scenario
					.getOthEmissions()) {
				if (emissions.getPteDeterminationBasis() != null
						&& (emissions.getPteDeterminationBasis()
								.equalsIgnoreCase(PAEuPteDeterminationBasisDef.OTHERS))) {
					isOtherSelected = true;
					break;
				}

			}
		}

		return isOtherSelected;
	}

	private void validateLegacyStateTVApplication(TVApplication app,
			List<ValidationMessage> messages) {
		 Timestamp impactCutoffDate = getImpactLegacyCutoffDate();
		// application received date must be prior to IMPACT legacy cutoff date
		if (app.getReceivedDate() != null && impactCutoffDate != null
				&& !app.getReceivedDate().before(impactCutoffDate)) {
			SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
			messages.add(new ValidationMessage("applicationReceivedDate",
					"Date application received must be prior to IMPACT legacy cutoff date ("
							+ format.format(impactCutoffDate) + ")",
					ValidationMessage.Severity.ERROR, "application"));
			app.setValidated(false);
		}
		
		if (app.getReceivedDate() == null) {
			messages.add(new ValidationMessage("applicationReceivedDate",
					"Date application received is missing",
					ValidationMessage.Severity.ERROR, "application"));
			app.setValidated(false);
		}

		// make sure that at least one EU is included and all EUs have a
		// AQD description
		List<ApplicationEU> includedEus = app.getIncludedEus();
		if (includedEus.size() == 0) {
			messages.add(new ValidationMessage("legacyAppBox",
					"At least one EU must be included in the application",
					ValidationMessage.Severity.WARNING, "application"));
		} else {

			for (ApplicationEU appEU : includedEus) {
				/*
				 * General Permit not valid for WY
				 * app.setValidated(validateLegacyPTOAppEU((PTIOApplicationEU)
				 * appEU, messages));
				 */
				app.setValidated(true);
			}
		}
	}

	@Override
	public void checkAndRemoveCAMPlan(Application app, ApplicationEU appEU)
			throws DAOException {
		TVApplication tvApp = (TVApplication) app;
		TVApplicationEU tvAppEU = (TVApplicationEU) appEU;

		for (TVEUPollutantLimit tvEUPollutantLimit : tvAppEU
				.getPollutantLimits()) {
			if (AbstractDAO.translateIndicatorToBoolean(tvEUPollutantLimit
					.getCamFlag())) {
				Transaction trans = TransactionFactory.createTransaction();
				try {
					checkAndRemoveCAMPlan(trans, tvApp);
					trans.complete();
				} catch (DAOException e) {
					cancelTransaction(trans, e);
				} finally {
					closeTransaction(trans);
				}
			}
		}
	}

	@Override
	public void checkAndRemoveCAMPlan(Transaction trans, TVApplication tvApp) {
		// if any other included EUs in the application has CAM plan set then
		// do not remove the CAM attachment.
		EU_LOOP: for (ApplicationEU appEU : tvApp.getIncludedEus()) {
			TVApplicationEU tvAppEU = (TVApplicationEU) appEU;
			for (TVEUPollutantLimit tvEUPollutantLimit : tvAppEU
					.getPollutantLimits()) {
				if (AbstractDAO.translateIndicatorToBoolean(tvEUPollutantLimit
						.getCamFlag())) {
					return; // don't do anything just return
				}
			}
		}

		try {
			ApplicationDAO appDAO = applicationDAO(
					getSchema(CommonConst.STAGING_SCHEMA), trans);

			removeAppRequiredAttachmnetDoc(appDAO, tvApp,
					null, TVApplicationDocumentTypeDef.CAM_PLAN);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Unknow exception in removing required attachment");
			logger.error(e.getMessage());
		}
	}

	@Override
	public void checkAndAddCAMPlan(Application app, ApplicationEU appEU)
			throws DAOException {
		TVApplication tvApp = (TVApplication) app;
		TVApplicationEU tvAppEU = (TVApplicationEU) appEU;

		for (TVEUPollutantLimit tvEUPollutantLimit : tvAppEU
				.getPollutantLimits()) {
			if (AbstractDAO.translateIndicatorToBoolean(tvEUPollutantLimit
					.getCamFlag())) {
				Transaction trans = TransactionFactory.createTransaction();
				try {
					ApplicationDAO appDAO = applicationDAO(
							getSchema(CommonConst.STAGING_SCHEMA), trans);

					if (!appDAO.checkApplicationTypeIsInDb(
							tvApp.getApplicationID(), null,
							TVApplicationDocumentTypeDef.CAM_PLAN, true)) {
						createRequiredDoc(appDAO, tvApp,
								TVApplicationDocumentTypeDef.CAM_PLAN);
					}
					trans.complete();
				} catch (DAOException e) {
					DisplayUtil
							.displayError("Unknow exception in adding CAM attachment");
					logger.error(e.getMessage());
					cancelTransaction(trans, e);
				} finally {
					closeTransaction(trans);
				}
				break;
			}
		}
	}
	
	private boolean isNonZeroPTEForTVApplication(TVApplicationEU tvAppEU) {
		boolean nonZeroPTE = false;
		if (tvAppEU.getNormalOperatingScenario() != null) {
			TVEUOperatingScenario scenario = tvAppEU.getNormalOperatingScenario();
			try {
				for (TVApplicationEUEmissions emissions : scenario.getCapEmissions()) {
					if (!Utility.isNullOrEmpty(emissions.getPteTonsYr()) 
							&& Float.parseFloat(emissions.getPteTonsYr()) > 0) {
						nonZeroPTE = true;
						break;
					}	
				}
			} catch(NumberFormatException nfe) {
				DisplayUtil.displayError("Non numeric value for Potential to Emit(PTE) (tons/year) ");
				logger.error(nfe.getMessage());
			}
		}	
		return nonZeroPTE;
	}
	
	// wrapper for submitApplicationInternal so that removing the workflow
	// can be run as an independent transaction in case there is an exception
	// and workflow has to be rolledback
	public List<ValidationMessage> submitApplication(Application app,
			int userId, Timestamp dfltReceivedDate, boolean createWorkFlow,
			String permitNumber, Transaction trans) throws RemoteException {

		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		try {
			validationMessages = submitApplicationInternal(app, userId,
					dfltReceivedDate, createWorkFlow, permitNumber, trans);
		} catch (WorkflowRollbackException e) {
			logger.debug("Removing the workflow process");
			try {
				removeProcessFlows(e.getExtId(), e.getWfId(), userId);
			} catch (Exception e2) {
				logger.error("FAILED attempt to remove workflow for "
						+ e.getWfId(), e2);
			}
			throw e;
		}
		return validationMessages;
	}
	
	// Application Performance improvements

	/**
	 * @param applicationId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Application retrieveApplicationWithBasicEUs(Integer applicationId)
			throws DAOException, RemoteException {
		Application application = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			application = retrieveApplicationWithBasicEUs(applicationId, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return application;
	}

	public Application retrieveApplicationWithBasicEUs(Integer applicationId,
			boolean staging) throws DAOException {
		Application application = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			application = retrieveApplicationWithBasicEUs(applicationId, trans,
					staging);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return application;
	}

	public Application retrieveApplicationSummary(Integer applicationId)
			throws DAOException {
		Application application = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			application = retrieveApplicationSummary(applicationId, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return application;
	}

	public Application retrieveApplicationSummary(Integer applicationId,
			boolean staging) throws DAOException {
		Application application = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			application = retrieveApplicationSummary(applicationId, trans,
					staging);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return application;
	}

	public Application retrieveApplicationWithAllEUs(Integer applicationID)
			throws DAOException, RemoteException {
		Application app = null;
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();

			// Retrieve application with all EU data from DB.
			app = retrieveApplicationWithAllEUs(applicationID, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
			throw new DAOException("Unable to retrieveApplicationWithAllEUs");
		} finally {
			closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithAllEUs(Integer applicationID,
			boolean staging) throws DAOException, RemoteException {
		Application app = null;
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();

			// Retrieve application with all EU data from DB.
			app = retrieveApplicationWithAllEUs(applicationID, trans, staging);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
			throw new DAOException("Unable to retrieveApplicationWithAllEUs");
		} finally {
			closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithAllEUs(Integer applicationID,
			Transaction trans) throws DAOException, RemoteException {

		logger.debug("Retrieving application...");
		ApplicationDAO appDAO = applicationDAO(trans);

		Application app = null;

		try {

			// Retrieve application summary and basic EU data from DB.
			app = retrieveApplicationSummary(applicationID, trans);

			boolean staging = false;
			if (isPortalApp() && trans != null) {
				staging = true;
			}

			List<ApplicationEU> newList = new ArrayList<ApplicationEU>();
			for (ApplicationEU appEu : app.getEus()) {
				// retrieve an populate complete EU

				ApplicationEU applicationEu = retrieveApplicationEU(
						appEu.getApplicationEuId(), trans, staging);

				newList.add(applicationEu);
			}
			// Replace application EU list with new list that has
			// fully-populated EUs.
			app.setEus(newList);

		} catch (DAOException e) {
			cancelTransaction(trans, e);
			throw new DAOException("Unable to retrieveApplicationWithAllEUs");
		} finally {
			closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithAllEUs(Integer applicationID,
			Transaction trans, boolean staging) throws DAOException,
			RemoteException {

		logger.debug("Retrieving application...");
		ApplicationDAO appDAO = applicationDAO(trans);

		Application app = null;

		try {

			// Retrieve application summary and basic EU data from DB.
			app = retrieveApplicationSummary(applicationID, trans, staging);

			List<ApplicationEU> newList = new ArrayList<ApplicationEU>();
			for (ApplicationEU appEu : app.getEus()) {
				// retrieve an populate complete EU

				ApplicationEU applicationEu = retrieveApplicationEU(
						appEu.getApplicationEuId(), trans, staging);

				applicationEu.setFpEU(app.getFacility().getEmissionUnit(
						appEu.getFpEU().getEmuId()));

				newList.add(applicationEu);

			}
			// Replace application EU list with new list that has
			// fully-populated EUs.
			app.setEus(newList);

		} catch (DAOException e) {
			cancelTransaction(trans, e);
			throw new DAOException("Unable to retrieveApplicationWithAllEUs");
		} finally {
			closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithAllEUs(String applicationNum)
			throws DAOException, RemoteException {

		Transaction trans = null;

		Application app = null;

		try {

			// Retrieve application summary and basic EU data from DB.
			app = retrieveApplicationSummary(applicationNum);

			List<ApplicationEU> newList = new ArrayList<ApplicationEU>();
			for (ApplicationEU appEu : app.getEus()) {
				// retrieve an populate complete EU
				ApplicationEU applicationEu = retrieveApplicationEU(
						appEu.getApplicationEuId(), trans, false);

				newList.add(applicationEu);

			}
			// Replace application EU list with new list that has
			// fully-populated EUs.
			app.setEus(newList);

		} catch (DAOException e) {
			// cancelTransaction(trans, e);
			throw new DAOException("Unable to retrieveApplicationWithAllEUs");
		} finally {
			// closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithIncludedEUs(Integer applicationID)
			throws DAOException, RemoteException {
		Application app = null;
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();

			// Retrieve application with included EU data from DB.
			app = retrieveApplicationWithIncludedEUs(applicationID, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
			throw new DAOException(
					"Unable to retrieveApplicationWithIncludedEUs");
		} finally {
			closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithIncludedEUs(
			Integer applicationID, boolean staging) throws DAOException,
			RemoteException {
		Application app = null;
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();

			// Retrieve application with included EU data from DB.
			app = retrieveApplicationWithIncludedEUs(applicationID, trans,
					staging);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
			throw new DAOException(
					"Unable to retrieveApplicationWithIncludedEUs");
		} finally {
			closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithIncludedEUs(
			Integer applicationID, Transaction trans) throws DAOException,
			RemoteException {

		logger.debug("Retrieving application...");
		ApplicationDAO appDAO = applicationDAO(trans);

		Application app = null;

		try {

			// Retrieve application summary and basic EU data from DB.
			app = retrieveApplicationSummary(applicationID, trans);

			boolean staging = false;
			if (isPortalApp() && trans != null) {
				staging = true;
			}

			List<ApplicationEU> newList = new ArrayList<ApplicationEU>();
			for (ApplicationEU appEu : app.getEus()) {
				if (!appEu.isExcluded()) {
					// retrieve an populate complete EU
					ApplicationEU applicationEu = retrieveApplicationEU(
							appEu.getApplicationEuId(), trans, staging);

					newList.add(applicationEu);
				} else {
					newList.add(appEu);
				}

			}
			// Replace application EU list with new list that has
			// fully-populated EUs for included EUs.
			app.setEus(newList);

		} catch (DAOException e) {
			cancelTransaction(trans, e);
			throw new DAOException(
					"Unable to retrieveApplicationWithIncludedEUs");
		} finally {
			closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithIncludedEUs(
			Integer applicationID, Transaction trans, boolean staging)
			throws DAOException, RemoteException {

		logger.debug("Retrieving application...");
		ApplicationDAO appDAO = applicationDAO(trans);

		Application app = null;

		try {

			// Retrieve application summary and basic EU data from DB.
			app = retrieveApplicationSummary(applicationID, trans, staging);

			List<ApplicationEU> newList = new ArrayList<ApplicationEU>();
			for (ApplicationEU appEu : app.getEus()) {
				if (!appEu.isExcluded()) {
					// retrieve an populate complete EU
					ApplicationEU applicationEu = retrieveApplicationEU(
							appEu.getApplicationEuId(), trans, staging);

					newList.add(applicationEu);
				} else {
					newList.add(appEu);
				}
			}
			// Replace application EU list with new list that has
			// fully-populated EUs for included EUs.
			app.setEus(newList);

		} catch (DAOException e) {
			cancelTransaction(trans, e);
			throw new DAOException(
					"Unable to retrieveApplicationWithIncludedEUs");
		} finally {
			closeTransaction(trans);
		}
		return app;
	}

	public Application retrieveApplicationWithIncludedEUs(String applicationNum)
			throws DAOException, RemoteException {

		Application app = null;

		Transaction trans = null;

		try {

			app = retrieveApplicationSummary(applicationNum);

			List<ApplicationEU> newList = new ArrayList<ApplicationEU>();
			for (ApplicationEU appEu : app.getEus()) {
				if (!appEu.isExcluded()) {
					// retrieve an populate complete EU
					ApplicationEU applicationEu = retrieveApplicationEU(
							appEu.getApplicationEuId(), trans, false);

					newList.add(applicationEu);
				} else {
					newList.add(appEu);
				}

			}
			// Replace application EU list with new list that has
			// fully-populated EUs.
			app.setEus(newList);

		} catch (DAOException e) {
			throw new DAOException(
					"Unable to retrieveApplicationWithIncludedEUs");
		} finally {
		}
		return app;
	}

	private void populateApplicationComponentsOnly(Application application,
			Transaction trans, boolean staging) throws DAOException {
		Calendar now = new GregorianCalendar();
		logger.debug("Enter populateApplicationComponentsOnly "
				+ new Timestamp(now.getTimeInMillis()).toString());

		int appId = application.getApplicationID();
		PTIOApplication ptioApp = null;
		TVApplication tvApp = null;

		ApplicationDAO appDAO = applicationDAO(trans);

		FacilityDAO facDAO = facilityDAO(trans);

		// retrieve documents for the application

		application.setDocuments(new ArrayList<ApplicationDocumentRef>());

		ApplicationDocumentRef[] appDocuments = null;
		if (ApplicationTypeDef.PTIO_APPLICATION.equals(application
				.getApplicationTypeCD())) {
			appDocuments = appDAO.retrieveNSRApplicationDocuments(appId);
		} else if (ApplicationTypeDef.TITLE_V_APPLICATION.equals(application
				.getApplicationTypeCD())) {
			appDocuments = appDAO.retrieveTVApplicationDocuments(appId);
		} else {
			appDocuments = appDAO.retrieveApplicationDocuments(appId);
		}

		for (ApplicationDocumentRef doc : appDocuments) {
			loadDocuments(doc, !staging);
			application.addDocument(doc);
		}

		// retrieve contact information
		Integer contactId = application.getContactId();
		if (contactId != null) {
			ContactDAO cDAO = contactDAO(trans);

			if (staging) {
				String facilityId = application.getFacilityId();
				application.setContact(cDAO.retrieveStagingContact(contactId,
						facilityId));

			} else {
				application.setContact(cDAO.retrieveContact(contactId));
			}

		}

		if (application.getClass() == PTIOApplication.class) {

			ptioApp = (PTIOApplication) application;

			String[] purposeCds = appDAO
					.retrievePTIOApplicationPurposeCds(appId);
			ptioApp.setApplicationPurposeCDs(Utility
					.createArrayList(purposeCds));

		} else if (application.getClass() == TVApplication.class) {

			tvApp = (TVApplication) application;
			for (TVEUGroup group : appDAO
					.retrieveTvEuGroupsForApplication(appId)) {
				tvApp.getEuGroups().add(group);
				for (TVApplicableReq req : appDAO
						.retrieveTVApplicableReqsForEUGroup(group
								.getTvEuGroupId())) {
					populateTVApplicableReqComponents(req, appDAO);

					if (!req.isStateOnly()) {
						group.getApplicableRequirements().add(req);
					} else {
						group.getStateOnlyRequirements().add(req);
					}
				}
			}

			TVApplicableReq[] applicableReqs = appDAO
					.retrieveTVApplicableReqs(appId);
			for (TVApplicableReq applicableReq : applicableReqs) {
				populateTVApplicableReqComponents(applicableReq, appDAO);
				if (!applicableReq.isStateOnly()) {
					tvApp.getApplicableRequirements().add(applicableReq);
				} else {
					tvApp.getStateOnlyRequirements().add(applicableReq);
				}
			}

			String[] reasonCds = appDAO.retrieveTVApplicationReasonCds(appId);

			if (reasonCds != null && reasonCds.length > 0) {
				tvApp.setPermitReasonCd(reasonCds[0]);
			}

			String facWideReqFlag = tvApp.getFacilityWideRequirementFlag();

			if (!Utility.isNullOrEmpty(facWideReqFlag)
					&& facWideReqFlag.equals(TVFacWideReqOptionDef.SUBJECT)) {
				List<FacilityWideRequirement> facWideReqs = appDAO
						.retrieveFacilityWideRequirements(tvApp
								.getApplicationID());
				tvApp.setFacilityWideRequirements(facWideReqs);
			} else {
				tvApp.setFacilityWideRequirements(new ArrayList<FacilityWideRequirement>());
			}

			// set TV Application subparts
			resetTVApplicationSubparts(tvApp, trans);

		}

		// this must be done after EUs are retrieved and updated
		if (ptioApp != null) {
			// update purpose codes to reflect values in EUs
			updatePTIOApplicationPurposeCds(ptioApp);
		}
		if (tvApp != null) {
			tvApp.setCapPteTotals(calculateTvCapPteTotals(tvApp, trans));
			tvApp.setHapPteTotals(calculateTvHapPteTotals(tvApp, trans));
			tvApp.setGhgPteTotals(calculateTvGhgPteTotals(tvApp, trans));
			tvApp.setOthPteTotals(calculateTvOthPteTotals(tvApp, trans));
		}

		// notes only apply to internal AQD application
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			ApplicationNote[] notes = retrieveApplicationNotes(appId);
			for (ApplicationNote note : notes) {
				application.addApplicationNote(note);
			}
		}

		setSubmitterUser(application);

		application.getFacility().setOwner(
				facDAO.retrieveFacilityOwner(application.getFacilityId()));

		now = new GregorianCalendar();
		logger.debug("Exit populateApplicationComponentsOnly "
				+ new Timestamp(now.getTimeInMillis()).toString());
	}

	private void populateApplicationEUs(Application application,
			Transaction trans, boolean staging) throws DAOException {

		logger.debug("Enter populateApplicationEUs"
				+ new Timestamp(new GregorianCalendar().getTimeInMillis())
						.toString());

		int appId = application.getApplicationID();
		ApplicationEU[] appEUs = null;

		// ApplicationDAO appDAO = applicationDAO(schema);
		ApplicationDAO appDAO = applicationDAO(trans);

		if (application.getClass().equals(PTIOApplication.class)) {
			appEUs = appDAO.retrievePTIOApplicationEmissionUnits(appId);
		} else if (application.getClass().equals(TVApplication.class)) {
			appEUs = appDAO.retrieveTVApplicationEmissionUnits(appId);
		} else {
			appEUs = appDAO.retrieveApplicationEmissionUnits(appId);
		}
		logger.debug("1 populateApplicationEUs"
				+ new Timestamp(new GregorianCalendar().getTimeInMillis())
						.toString());
		for (ApplicationEU applicationEU : appEUs) {

			ApplicationEU appEu = null;
			if (application.getClass().equals(TVApplication.class)) {
				appEu = new TVApplicationEU((TVApplicationEU) applicationEU);
			} else if (application.getClass().equals(PTIOApplication.class)) {
				appEu = new PTIOApplicationEU((PTIOApplicationEU) applicationEU);
			} else {
				appEu = new ApplicationEU(applicationEU);

			}
			application.addEu(appEu);
		}
		logger.debug("2 populateApplicationEUs"
				+ new Timestamp(new GregorianCalendar().getTimeInMillis())
						.toString());
		// retrieve all Emission Units for the facility
		FacilityDAO facDAO = facilityDAO(trans);

		for (EmissionUnit fpEU : facDAO
				.retrieveFacilityEmissionUnits(application.getFacility()
						.getFpId())) {
			application.getFacility().addEmissionUnit(fpEU);
		}

		logger.debug("Exit populateApplicationEUs"
				+ new Timestamp(new GregorianCalendar().getTimeInMillis())
						.toString());
	}

	public Application retrieveApplication(String applicationNumber)
			throws DAOException, RemoteException {

		Application application = retrieveApplicationWithAllEUs(applicationNumber);

		if (application == null) {
			logger.warn("No application found for application number: "
					+ applicationNumber);
		}
		return application;
	}

	/**
	 * Retrieve an application by its application number from the READ ONLY
	 * schema. This should only be used within the portal application to
	 * retrieve applications that have already been submitted and are no longer
	 * in the staging area.
	 * 
	 * @param applicationNumber
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */

	public Application retrieveApplicationSummary(String applicationNumber)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.READONLY_SCHEMA));

		Application application = appDAO.retrieveApplication(applicationNumber);

		Transaction trans = null;
		if (application != null) {
			populateApplicationEUs(application, trans, false);
			populateApplicationComponentsOnly(application, trans, false);
			application.setInspectionsReferencedIn(fullComplianceEvalDAO().retrieveInspectionIdsForApplicationId(application.getApplicationID()));

		} else {
			logger.warn("No application found for application number: "
					+ applicationNumber);
		}
		return application;
	}

	public ApplicationEU retrieveApplicationEUReadOnly(Integer appEUId)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(getSchema(CommonConst.READONLY_SCHEMA));
		ApplicationEU appEU = appDAO.retrieveApplicationEmissionUnit(appEUId);

		if (appEU != null) {
			ApplicationEUTypeDAO appEUTypeDAO = null;
			EmissionUnitTypeDAO euTypeDAO = null;
			if (appEU instanceof PTIOApplicationEU) {
				appEUTypeDAO = getApplicationEUTypeDAO(appEU.getFpEU()
						.getEmissionUnitTypeCd(), null, false);
				euTypeDAO = getEUTypeDAO( 
						appEU.getFpEU().getEmissionUnitTypeCd(), null, false); 
			}
			populateApplicationEUComponents(appEU, appDAO, appEUTypeDAO,
					euTypeDAO, false);
		}
		return appEU;
	}

	@Transactional(noRollbackFor = NotFoundException.class)
	public Application retrieveApplicationSummary(Integer applicationId,
			Transaction trans) throws DAOException {
		logger.debug("Retrieving ... retrieveApplicationSummary ...");
		ApplicationDAO appDAO = applicationDAO(trans);

		Application application = appDAO.retrieveApplication(applicationId);

		if (application == null)
			throw new NotFoundException("Invalid application id:"
					+ applicationId);

		boolean staging = false;
		if (isPortalApp() && trans != null) {
			staging = true;
		}

		populateApplicationEUs(application, trans, staging);

		populateApplicationComponentsOnly(application, trans, staging);
		
		logger.debug("Done retrieving retrieveApplicationSummary...");
		return application;
	}

	public Application retrieveApplicationSummary(Integer applicationId,
			Transaction trans, boolean staging) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		Application application = appDAO.retrieveApplication(applicationId);

		if (application == null)
			throw new DAOException("Invalid application id:" + applicationId);

		populateApplicationEUs(application, trans, staging);
		populateApplicationComponentsOnly(application, trans, staging);

		return application;
	}

	@Transactional(noRollbackFor = NotFoundException.class)
	public Application retrieveApplicationWithBasicEUs(Integer applicationId,
			Transaction trans) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		Application application = appDAO.retrieveApplication(applicationId);

		if (application == null)
			throw new NotFoundException("Invalid application id:"
					+ applicationId);

		boolean staging = false;
		if (isPortalApp() && trans != null) {
			staging = true;
		}

		populateApplicationEUs(application, trans, staging);

		logger.debug("Done retrieving application...");
		return application;
	}

	public Application retrieveApplicationWithBasicEUs(Integer applicationId,
			Transaction trans, boolean staging) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(trans);

		Application application = appDAO.retrieveApplication(applicationId);

		if (application == null)
			throw new DAOException("Invalid application id:" + applicationId);

		populateApplicationEUs(application, trans, staging);

		return application;
	}

	public void loadApplicationDetailDocuments(Application application,
			boolean readOnly) throws DAOException {
		logger.debug("Enter loadApplicationDetailDocuments()"
				+ new Timestamp(new GregorianCalendar().getTimeInMillis())
						.toString());
		for (ApplicationDocumentRef docRef : application.getDocuments()) {
			loadDocuments(docRef, readOnly);
		}
		logger.debug("Exit loadApplicationDetailDocuments()"
				+ new Timestamp(new GregorianCalendar().getTimeInMillis())
						.toString());
	}

	/**
	 * Load Document objects into all ApplicationDocumentRef objects associated
	 * with application.
	 * 
	 * @param application
	 * @param readOnly
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public void loadEUDocuments(Application application, ApplicationEU selEU,
			boolean readOnly) throws DAOException {

		for (ApplicationEU appEU : application.getEus()) {
			if (appEU.getApplicationEuId().equals(selEU.getApplicationEuId())) {
				for (ApplicationDocumentRef docRef : appEU.getEuDocuments()) {
					loadDocuments(docRef, readOnly);
				}
				break;
			}
		}

	}
	
	@Override
	public boolean checkApplicationExistsInReadOnlySchema(Integer applicationId)
			throws DAOException {
		ApplicationDAO appDAO = applicationDAO(CommonConst.READONLY_SCHEMA);
		Application application = appDAO.retrieveApplication(applicationId);
		return (null != application) ? true : false;
	}

	@Override
	public ApplicationDocument retrieveApplicationDocumentByTradeSecrectDocId(Integer tradeSecretDocId)
			throws DAOException {
		return applicationDAO().retrieveApplicationDocumentByTradeSecrectDocId(tradeSecretDocId);
	}

	@Override
	public Timestamp retrieveApplicationSubmittedDate(Integer applicationId) throws DAOException {
		return applicationDAO().retrieveApplicationSubmittedDate(applicationId);
	}

	@Override
	public Application retrieveBasicApplicationById(Integer applicationId) throws DAOException {
		return applicationDAO().retrieveBasicApplicationById(applicationId);
	}

	@Override
	public Application retrieveBasicApplicationByNbr(String applicationNbr) throws DAOException {
		return applicationDAO().retrieveBasicApplicationByNbr(applicationNbr);
	}
	
	
	
}
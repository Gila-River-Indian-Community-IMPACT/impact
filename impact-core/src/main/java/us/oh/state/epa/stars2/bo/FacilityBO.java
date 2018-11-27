package us.oh.state.epa.stars2.bo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.DocumentException;

import au.com.bytecode.opencsv.CSVReader;
import us.oh.state.epa.place.facility.FacilitySummary;
import us.oh.state.epa.place.facility.SecondaryIdentifier;
import us.oh.state.epa.portal.base.Constants;
import us.oh.state.epa.portal.service.placeservice.PlaceService;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.tools.FacilityRoles;
import us.oh.state.epa.stars2.bo.event.FacilityOwnershipChangeEvent;
import us.oh.state.epa.stars2.bo.helpers.FacilityHelper;
import us.oh.state.epa.stars2.bo.helpers.InfrastructureHelper;
import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.ComplianceReportDAO;
import us.oh.state.epa.stars2.database.dao.DetailDataDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dao.EmissionsReportDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dao.WorkFlowDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.LimitTrendReportLineItem;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.AppPermitSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EuEmission;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityEmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityHistList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityIdRef;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNode;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityOwner;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityProfileDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRUM;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRelationship;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRequestList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRoleActivity;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityVersion;
import us.oh.state.epa.stars2.database.dbObjects.facility.HydrocarbonAnalysisPollutant;
import us.oh.state.epa.stars2.database.dbObjects.facility.ModelingExtractResult;
import us.oh.state.epa.stars2.database.dbObjects.facility.ModelingExtractResult.EmissionsLimitType;
import us.oh.state.epa.stars2.database.dbObjects.facility.MultiEstabFacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantsControlled;
import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.Component;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitReplacement;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.database.dbObjects.facilityRequest.FacilityRequest;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ApiGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FacilityRoleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessData;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.CeOperatingStatusDef;
import us.oh.state.epa.stars2.def.ComplianceStatusDef;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.DesignCapacityDef;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.EventLogTypeDef;
import us.oh.state.epa.stars2.def.ExemptStatusDef;
import us.oh.state.epa.stars2.def.FacilityAttachmentTypeDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.FieldAuditLogAttributeDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.SccCodesDef;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacilityEmissionFlow;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;
import us.oh.state.epa.stars2.webcommon.pdf.facility.FacilityPdfGenerator;
import us.oh.state.epa.stars2.workflow.Activity;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;
import us.oh.state.epa.stars2.workflow.util.WorkFlowUtils;
import us.wy.state.deq.impact.database.dao.CompanyDAO;
import us.wy.state.deq.impact.database.dao.ContactDAO;
import us.wy.state.deq.impact.database.dao.ContinuousMonitorDAO;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorEqt;
import us.wy.state.deq.impact.database.dbObjects.report.EiDataImportFacilityInformation;
import us.wy.state.deq.impact.database.dbObjects.tool.FixmeCompany;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;
import us.wy.state.deq.impact.def.EquipTypeDef;
import us.wy.state.deq.impact.def.FugitiveComponentDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeLiquidDef;
import us.wy.state.deq.impact.util.GeoTools;
import us.wy.state.deq.impact.util.GeoTools.UtmCoordinate;

/**
 * <p>
 * Title: FacilityBO
 * </p>
 * 
 * <p>
 * Description: This is the Business Object for Facilities.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author Kbradley
 * @version 1
 */
/**
 * @author ryamini
 * 
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class FacilityBO extends BaseBO implements FacilityService {
	
	public static final String MISSING_PERCENTS = "Some Flow Percentages are not set or all are zero";
	public static final String FLOW_TOTAL_WRONG1 = "Flow Percentages sum to ";
	public static final String FLOW_TOTAL_WRONG2 = "--they must sum to 100% or some values can be left empty to be provided later.";
	public static final float MISSING_FLOW_PERCENT = -1000f;

	private String emissionUnitCsvFilePath;
	private String emissionProcessCsvFilePath;
	private String controlEquipCsvFilePath;
	private String releasePointCsvFilePath;
	private String FacilityIdCsvFilePath;
	private PrintWriter printWriter;
	private int totalMigrationCount = 0;
	private int currentMigrationCount = 0;

	HashMap<String, Integer> releatedFacilityIds = null;
			
	@Autowired
	private InfrastructureHelper infrastructureHelper;
	@Autowired
	private FacilityHelper facilityHelper;
	private FacilityService facilityService;

	public class CopyOnChangeMaps {
		Integer fpId;
		HashMap<Integer, Integer> fpNodeIds;
		HashMap<Integer, Integer> euIds;

		public CopyOnChangeMaps() {
			fpId = 0;
			fpNodeIds = new HashMap<Integer, Integer>();
			euIds = new HashMap<Integer, Integer>();
		}
	}

	private InfrastructureDAO getInfraDAO() throws DAOException {
		String schema = null;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			schema = "Staging";
		}

		return infrastructureDAO(schema);
	}
	
	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public ModelingExtractResult[] modelingExtract(
			Boolean searchTypePolygon, Boolean searchTypeRadial, 
			Double latitudeDegrees, Double longitudeDegrees, 
			Integer distanceMeters, List<String> pollutants, 
			List<String> excludedFacilityTypes,
			Double latitudeSwDegrees, Double longitudeSwDegrees,
			Double latitudeSeDegrees, Double longitudeSeDegrees,
			Double latitudeNeDegrees, Double longitudeNeDegrees,
			Double latitudeNwDegrees, Double longitudeNwDegrees) 
					throws DAOException {
				
		FacilityDAO facilityDAO = getFacilityDAO();
		
		ModelingExtractResult[] results =  
				facilityDAO.modelingExtract(searchTypePolygon, 
				searchTypeRadial, latitudeDegrees, longitudeDegrees, 
				distanceMeters, pollutants, excludedFacilityTypes,
				 latitudeSwDegrees,  longitudeSwDegrees,
				 latitudeSeDegrees,  longitudeSeDegrees,
				 latitudeNeDegrees,  longitudeNeDegrees,
				 latitudeNwDegrees,  longitudeNwDegrees);

		Map<String,Set<ModelingExtractResult>> resultsByEmu = 
				new HashMap<String,Set<ModelingExtractResult>>();

		try {

			for (ModelingExtractResult r : results) {
				UtmCoordinate utm = GeoTools.latLonToUtm(
						r.getReleasePointLatitudeDegrees(), 
						r.getReleasePointLongitudeDegrees());
				r.setUtmDatum("WGS84/NAD83");
				r.setUtmZone(utm.getZone());
				r.setUtmEasting(utm.getEasting());
				r.setUtmNorthing(utm.getNorthing());
				
				Set<ModelingExtractResult> emuResults = 
						resultsByEmu.get(r.getModelingSourceId());
				if (null == emuResults) {
					emuResults = new HashSet<ModelingExtractResult>();
					resultsByEmu.put(r.getModelingSourceId(),emuResults);
				}
				emuResults.add(r);
			}
		} catch (Exception e) {
			throw new DAOException("Error converting lat/lon to utm",e);
		}
		results = setPollutantEmissionsValues(resultsByEmu);
		return results;
	}
	
	private ModelingExtractResult[] setPollutantEmissionsValues(
			Map<String,Set<ModelingExtractResult>> resultsByEmu) {
		
		Set<ModelingExtractResult> results = new HashSet<ModelingExtractResult>();
		
		for (String emuId : resultsByEmu.keySet()) {
			Set<ModelingExtractResult> emuResults = resultsByEmu.get(emuId);
			if (emuResults.size() > 0) {
				Map<String,Set<ModelingExtractResult>> emuResultsByPollutant = 
						new HashMap<String,Set<ModelingExtractResult>>();
				for (ModelingExtractResult r : emuResults) {
					String pollutantCd = r.getPollutantCd();
					if (null != pollutantCd) {
						Set<ModelingExtractResult> pollutantEmuResults = 
								emuResultsByPollutant.get(pollutantCd);
						if (null == pollutantEmuResults) {
							pollutantEmuResults = 
									new HashSet<ModelingExtractResult>();
							emuResultsByPollutant.put(pollutantCd,
									pollutantEmuResults);
						}
						pollutantEmuResults.add(r);
					}
				}
				for (String pollutantCd : emuResultsByPollutant.keySet()) {
					Set<ModelingExtractResult> pollutantEmuResults =
							emuResultsByPollutant.get(pollutantCd);
					BigDecimal prevShortTermValue = null;
					BigDecimal prevLongTermValue = null;
					for (ModelingExtractResult r : pollutantEmuResults) {
						BigDecimal shortTermValue = null;
						BigDecimal longTermValue = null;
						ModelingExtractResult rCopy = new ModelingExtractResult(r);
						
						// short term limits
						if (null != rCopy.getPollutantAllowableEmissionsLbsHour()) {
							shortTermValue = rCopy.getPollutantAllowableEmissionsLbsHour();
							if (null == prevShortTermValue || shortTermValue.equals(prevShortTermValue)) {
								rCopy.setPollutantAllowableEmissionsLbsHour(
										shortTermValue.divide(new BigDecimal(pollutantEmuResults.size()),
														6,RoundingMode.HALF_UP));
								rCopy.setPollutantShortTermEmissionsLimitType(EmissionsLimitType.ALLOWABLE);
								rCopy.setPollutantShortTermLimit(rCopy.getPollutantAllowableEmissionsLbsHour());
							} else 
							if (null != prevShortTermValue && !shortTermValue.equals(prevShortTermValue)) {
								rCopy.setPollutantAllowableEmissionsLbsHour(null);
								logger.error("Pollutant values expected to match for EU id ["+rCopy.getModelingSourceId()+"], Pollutant Code ["+rCopy.getPollutantCd()+"]");
							}
						}
						else
						if (null != rCopy.getPollutantPotentialEmissionsLbsHour()) {
							shortTermValue = rCopy.getPollutantPotentialEmissionsLbsHour();
							if (null == prevShortTermValue || shortTermValue.equals(prevShortTermValue)) {
								rCopy.setPollutantPotentialEmissionsLbsHour(
										shortTermValue.divide(new BigDecimal(pollutantEmuResults.size()),
														6,RoundingMode.HALF_UP));
								rCopy.setPollutantShortTermEmissionsLimitType(EmissionsLimitType.POTENTIAL);
								rCopy.setPollutantShortTermLimit(rCopy.getPollutantPotentialEmissionsLbsHour());
							} else 
							if (null != prevShortTermValue && !shortTermValue.equals(prevShortTermValue)) {
								rCopy.setPollutantPotentialEmissionsLbsHour(null);
								logger.error("Pollutant values expected to match for EU id ["+rCopy.getModelingSourceId()+"], Pollutant Code ["+rCopy.getPollutantCd()+"]");
							}
						}
						prevShortTermValue = shortTermValue;
						
						// long term limits
						if (null != rCopy.getPollutantAllowableEmissionsTonsYear()) {
							longTermValue = rCopy.getPollutantAllowableEmissionsTonsYear();
							if (null == prevLongTermValue || longTermValue.equals(prevLongTermValue)) {
								rCopy.setPollutantAllowableEmissionsTonsYear(
										longTermValue.divide(new BigDecimal(pollutantEmuResults.size()),
														6,RoundingMode.HALF_UP));
								rCopy.setPollutantLongTermEmissionsLimitType(EmissionsLimitType.ALLOWABLE);
								rCopy.setPollutantLongTermLimit(rCopy.getPollutantAllowableEmissionsTonsYear());
							} else 
							if (null != prevLongTermValue && !longTermValue.equals(prevLongTermValue)) {
								rCopy.setPollutantAllowableEmissionsTonsYear(null);
								logger.error("Pollutant values expected to match for EU id ["+rCopy.getModelingSourceId()+"], Pollutant Code ["+rCopy.getPollutantCd()+"]");
							}
						}
						else
						if (null != rCopy.getPollutantPotentialEmissionsTonsYear()) {
							longTermValue = rCopy.getPollutantPotentialEmissionsTonsYear();
							if (null == prevLongTermValue || longTermValue.equals(prevLongTermValue)) {
								rCopy.setPollutantPotentialEmissionsTonsYear(
										longTermValue.divide(new BigDecimal(pollutantEmuResults.size()),
														6,RoundingMode.HALF_UP));
								rCopy.setPollutantLongTermEmissionsLimitType(EmissionsLimitType.POTENTIAL);
								rCopy.setPollutantLongTermLimit(rCopy.getPollutantPotentialEmissionsTonsYear());
							} else 
							if (null != prevLongTermValue && !longTermValue.equals(prevLongTermValue)) {
								rCopy.setPollutantPotentialEmissionsTonsYear(null);
								logger.error("Pollutant values expected to match for EU id ["+rCopy.getModelingSourceId()+"], Pollutant Code ["+rCopy.getPollutantCd()+"]");
							}
						}
						prevLongTermValue = longTermValue;

						results.add(rCopy);
					}
					
				}
			}
		}
		return results.toArray(new ModelingExtractResult[0]);
	}

	private BigDecimal pphToTpy(BigDecimal pph) {
		BigDecimal tpy = null;
		if (null != pph) {
			BigDecimal ppy = pph.multiply(new BigDecimal(8760));
			tpy = ppy.divide(new BigDecimal(2000),6,RoundingMode.HALF_UP);
		}
		return tpy;
	}

	private BigDecimal tpyToPph(BigDecimal tpy) {
		BigDecimal pph = null;
		if (null != tpy) {
			BigDecimal ppy = tpy.multiply(new BigDecimal(2000));
			pph = ppy.divide(new BigDecimal(8760),6,RoundingMode.HALF_UP);
		}
		return pph;
	}



	/**
	 * validate contact
	 * 
	 * @param contact
	 *            contact
	 * @return Contact
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Contact retrieveFacilityContact(Contact contact) throws DAOException {
		Contact rtn = getFacilityDAO().retrieveFacilityContact(contact);
		if (rtn != null) {
			Address addr = infrastructureDAO().retrieveAddress(
					rtn.getAddressId());
			rtn.setAddress(addr);
		}
		return rtn;
	}

	/**
	 * validate contact
	 * 
	 * @param contact
	 *            contact
	 * @return Contact[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Contact[] retrieveDupFacilityContact(Contact contact,
			Transaction trans) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infrastructureDAO = infrastructureDAO(trans);
		Contact[] rtn = facilityDAO.retrieveDupFacilityContact(contact);
		// Just get the Address for the first Contact
		if (rtn != null && rtn.length > 0) {
			Address addr = infrastructureDAO.retrieveAddress(rtn[0]
					.getAddressId());
			rtn[0].setAddress(addr);
		}
		return rtn;
	}

	/**
	 * validate contact
	 * 
	 * @param contact
	 *            contact
	 * @return Contact
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Contact retrieveFacilityContact(Contact contact, Transaction trans)
			throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infrastructureDAO = infrastructureDAO(trans);
		Contact rtn = facilityDAO.retrieveFacilityContact(contact);
		if (rtn != null) {
			Address addr = infrastructureDAO
					.retrieveAddress(rtn.getAddressId());
			rtn.setAddress(addr);
		}
		return rtn;
	}

	/**
	 * validate contact
	 * 
	 * @param contact
	 *            contact
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ValidationMessage[] validateContact(Contact contact)
			throws DAOException {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>();
		ValidationMessage validationMessage;

		if (contact == null) {
			DAOException e = new DAOException("invalid contact (null)");
			throw e;
		}

		ValidationMessage[] validationMessages = contact.validate();
		for (ValidationMessage tempMsg : validationMessages) {
			validMessages.add(tempMsg);
		}

		FacilityDAO facilityDAO = getFacilityDAO();

		Contact tempContact = facilityDAO.retrieveFacilityContact(contact);
		if (tempContact != null
				&& (!tempContact.getContactId().equals(contact.getContactId()))) {
			validationMessage = new ValidationMessage("lastNm",
					"Duplicate contact", ValidationMessage.Severity.ERROR,
					"contact:" + contact.getContactId());
			validMessages.add(validationMessage);
			validationMessage = new ValidationMessage("firstNm",
					"Duplicate contact", ValidationMessage.Severity.ERROR,
					"contact:" + contact.getContactId());
			validMessages.add(validationMessage);
			validationMessage = new ValidationMessage("middleNm",
					"Duplicate contact", ValidationMessage.Severity.ERROR,
					"contact:" + contact.getContactId());
			validMessages.add(validationMessage);
			validationMessage = new ValidationMessage("companyName",
					"Duplicate contact", ValidationMessage.Severity.ERROR,
					"contact:" + contact.getContactId());
			validMessages.add(validationMessage);
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	/**
	 * validate of Emission Unit.
	 * 
	 * @param emissionUnit
	 *            EmissionUnit
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ValidationMessage[] validateEmissionUnit(EmissionUnit emissionUnit,
			Facility facility, boolean internalApp) throws DAOException {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>();
		ValidationMessage validationMessage;

		if (emissionUnit == null) {
			DAOException e = new DAOException("invalid emission unit (null)");
			throw e;
		}

		ValidationMessage[] validationMessages = emissionUnit.validate(
				facility.getPermitClassCd(),
				CompMgr.getAppName().equals(CommonConst.INTERNAL_APP));
		for (ValidationMessage tempMsg : validationMessages) {
			if ((!internalApp)
					&& (tempMsg.getProperty().equals("epaEmuId") || tempMsg
							.getProperty().equals(EmissionUnit.EPAEMUMSGID))) {
				continue;
			}

			validMessages.add(tempMsg);
		}

		String tempEpaId = emissionUnit.getEpaEmuId();
		EmissionUnit tempEU;

		if (tempEpaId != null) {
			validationMessage = new ValidationMessage("epaEmuId",
					"Duplicate Emission Unit ID",
					ValidationMessage.Severity.ERROR, "emissionUnit:"
							+ tempEpaId);
			FacilityDAO facilityDAO = getFacilityDAO();
			tempEU = facilityDAO.retrieveEmissionUnit(emissionUnit.getFpId(),
					tempEpaId);
			if (tempEU != null) {
				if (emissionUnit.getEmuId() != null) {
					if (!(emissionUnit.getEmuId().equals(tempEU.getEmuId()))) {
						validMessages.add(validationMessage);
					}
				} else {
					validMessages.add(validationMessage);
				}
			}
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	public ValidationMessage[] validateEuEmissions(EmissionUnit emissionUnit)
			throws DAOException {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>();

		if (emissionUnit == null) {
			DAOException e = new DAOException("invalid emission unit (null)");
			throw e;
		}

		ValidationMessage[] validationMessages = emissionUnit
				.validateOnlyEuEmissionsTable();
		for (ValidationMessage tempMsg : validationMessages) {
			validMessages.add(tempMsg);
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	/**
	 * @param sccId
	 * @return SccCode
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SccCode getFullSccCode(String sccId) throws DAOException {
		return infrastructureDAO().retrieveSccCode(sccId);
	}

	/**
	 * validate of Emission Process.
	 * 
	 * @param emissionProcess
	 *            EmissionProcess
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ValidationMessage[] validateEmissionProcess(
			EmissionProcess emissionProcess) throws DAOException {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>();
		ValidationMessage validationMessage;

		if (emissionProcess == null) {
			DAOException e = new DAOException("invalid emission process (null)");
			throw e;
		}

		ValidationMessage[] validationMessages = emissionProcess.validate();
		for (ValidationMessage tempMsg : validationMessages) {
			validMessages.add(tempMsg);
		}

		String processId = emissionProcess.getProcessId();
		EmissionProcess tempEP;
		FacilityDAO facilityDAO = getFacilityDAO();

		// validate scc code exists

		if (emissionProcess.getSccId() != null) {
			SccCode sccCode = infrastructureDAO().retrieveSccCode(
					emissionProcess.getSccId());
			if (sccCode == null) {
				validationMessage = new ValidationMessage("sccId",
						"Invalid SCC code; SCC code does not exist",
						ValidationMessage.Severity.ERROR, "emissionProcess:"
								+ processId);
				validMessages.add(validationMessage);
			} else {
				// replace with more complete SccCode
				emissionProcess.setSccCode(sccCode);
				
				// validate scc code exists in WebFIRE database (internal only)
				if(isInternalApp()
					&& !emissionsReportDAO().checkFireRowByScc(emissionProcess.getSccId())) {
					validMessages.add(new ValidationMessage("sccId",
							"SCC ID does not exist in the WebFIRE database",
							ValidationMessage.Severity.WARNING, "emissionProcess:" + processId));
				}
								
				validationMessage = new ValidationMessage("sccId",
						"Duplicate Emission SCC ID within emission unit",
						ValidationMessage.Severity.ERROR, "emissionProcess:"
								+ processId);

				tempEP = facilityDAO
						.retrieveEmissionProcess(emissionProcess.getFpId(),
								emissionProcess.getEmissionUnitId(),
								sccCode.getSccId());
				if (tempEP != null) {
					if (emissionProcess.getFpNodeId() != null) {
						if (!(emissionProcess.getFpNodeId().equals(tempEP
								.getFpNodeId()))) {
							validMessages.add(validationMessage);
						}
					} else {
						validMessages.add(validationMessage);
					}
				}
			}
		}

		double total;
		boolean notSet;
		if (emissionProcess.getEpEmissionFlows().size() == 0) {
			total = 100d;
			notSet = true;
		} else {
			total = 0d;
			notSet = false;
			String sp = null;
			boolean allSame = true;
			for (FacilityEmissionFlow fef : emissionProcess
					.getEpEmissionFlows()) {
				if (fef.getPercent() != null && fef.getPercent().length() != 0) {
					total += fef.getPercentValue();
					if (sp == null) {
						sp = fef.getPercent();
					} else {
						if (!sp.equals(fef.getPercent())) {
							allSame = false;
						}
					}
				} else {
					notSet = true;
				}
			}
			if (!notSet && allSame) {
				// Are they actually 100%
				float fract = 100f / emissionProcess.getEpEmissionFlows()
						.size();
				String str = BaseDB.numberToxxx_xx((double) fract);
				if (str.equals(sp)) {
					// they actually do add to 100%
					total = 100f; // make them look that way
				}
			}
		}
		if (!notSet && (total < 99.995 || total > 100.005)) {
			validationMessage = new ValidationMessage("flowTotal",
					FLOW_TOTAL_WRONG1 + BaseDB.numberToxxx_xx(total)
							+ FLOW_TOTAL_WRONG2,
					ValidationMessage.Severity.ERROR, "emissionProcess:"
							+ processId);
			validMessages.add(validationMessage);
		}
		return validMessages.toArray(new ValidationMessage[0]);
	}

	/**
	 * validate of Release Point.
	 * 
	 * @param egressPoint
	 *            EgressPoint
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ValidationMessage[] validateEgressPointJustLatLong(
			EgressPoint egressPoint, Facility fac) throws DAOException {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>();
		ValidationMessage validationMessage;
		if (egressPoint == null) {
			DAOException e = new DAOException("invalid release point (null)");
			throw e;
		}

		String tempEgrId = egressPoint.getReleasePointId();

		// calculation from
		// http://www.meridianworlddata.com/Distance-Calculation.asp:
		// Approximate distance in miles:
		// sqrt(x * x + y * y)
		// where x = 69.1 * (lat2 - lat1)
		// and y = 69.1 * (lon2 - lon1) * cos(lat1/57.3)
		double facLat = Math.abs(Float.parseFloat(fac.getPhyAddr()
				.getLatitude()));
		double facLon = Math.abs(Float.parseFloat(fac.getPhyAddr()
				.getLongitude()));
		double egpLat = Math.abs(egressPoint.getLatitudeNum());
		double egpLon = Math.abs(egressPoint.getLongitudeNum());
		double latDiff = Math.pow(Math.abs(egpLat - facLat) * 69.1, 2);
		double lonDiff = Math.pow(
				Math.abs(egpLon - facLon) * 69.1 * Math.cos(facLat / 57.3), 2);
		double distance = Math.sqrt(latDiff + lonDiff);
		Double maxFacilityToReleasePointDistance = new Double(
				SystemPropertyDef.getSystemPropertyValueAsFloat("max_Facility_To_ReleasePoint_Distance", null));

		if (distance > maxFacilityToReleasePointDistance) {
			validationMessage = new ValidationMessage(
					"XXX",
					"Invalid facility or release point latitude and longitude. Release point "
							+ " coordinates must be within " + maxFacilityToReleasePointDistance + " miles of facility coordinates ("
							+ facLat + "/-" + facLon + ").",
					ValidationMessage.Severity.ERROR, "releasePoint:"
							+ tempEgrId);
			validMessages.add(validationMessage);
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	/**
	 * validate of Release Point.
	 * 
	 * @param egressPoint
	 *            EgressPoint
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ValidationMessage[] validateEgressPointLatLong(
			EgressPoint egressPoint, Facility fac) throws DAOException {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>();
		ValidationMessage validationMessage;

		if (egressPoint == null) {
			DAOException e = new DAOException("invalid release point (null)");
			throw e;
		}

		String tempRpId = egressPoint.getReleasePointId();

		if (fac.getPermitClassCd() == null
				|| !fac.getPermitClassCd().equals(PermitClassDef.TV)) {
			return validMessages.toArray(new ValidationMessage[0]);
		} else {
			// calculation from
			// http://www.meridianworlddata.com/Distance-Calculation.asp:
			// Approximate distance in miles:
			// sqrt(x * x + y * y)
			// where x = 69.1 * (lat2 - lat1)
			// and y = 69.1 * (lon2 - lon1) * cos(lat1/57.3)
			double facLat = Math.abs(Float.parseFloat(fac.getPhyAddr()
					.getLatitude()));
			double facLon = Math.abs(Float.parseFloat(fac.getPhyAddr()
					.getLongitude()));
			double egpLat = Math.abs(egressPoint.getLatitudeNum());
			double egpLon = Math.abs(egressPoint.getLongitudeNum());
			double latDiff = Math.pow(Math.abs(egpLat - facLat) * 69.1, 2);
			double lonDiff = Math.pow(
					Math.abs(egpLon - facLon) * 69.1 * Math.cos(facLat / 57.3),
					2);
			double distance = Math.sqrt(latDiff + lonDiff);
			Double maxFacilityToReleasePointDistance = new Double(
					SystemPropertyDef.getSystemPropertyValueAsFloat("max_Facility_To_ReleasePoint_Distance", null));
			
			if (distance > maxFacilityToReleasePointDistance) {
				validationMessage = new ValidationMessage(
						"XXX",
						"Invalid facility or release point latitude and longitude. Release point "
								+ " coordinates must be within " + maxFacilityToReleasePointDistance + " miles of facility coordinates ("
								+ facLat + "/-" + facLon + ").",
						ValidationMessage.Severity.ERROR, "releasePoint:"
								+ tempRpId);
				validMessages.add(validationMessage);
			}
		}
		return validMessages.toArray(new ValidationMessage[0]);
	}

	/**
	 * validate of Control Equipment.
	 * 
	 * @param controlEquipment
	 *            ControlEquipment
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ValidationMessage[] validateControlEquipment(
			ControlEquipment controlEquipment) throws DAOException {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>();
		ValidationMessage validationMessage;

		if (controlEquipment == null) {
			DAOException e = new DAOException(
					"invalid control equipment (null)");
			throw e;
		}

		ValidationMessage[] validationMessages = controlEquipment.validate();
		for (ValidationMessage tempMsg : validationMessages) {
			validMessages.add(tempMsg);
		}

		String tempCeId = controlEquipment.getControlEquipmentId();
		double total;
		boolean notSet;
		String sp = null;
		boolean allSame = true;
		if (controlEquipment.getCeEmissionFlows().size() == 0) {
			total = 100d;
			notSet = true;
		} else {
			total = 0d;
			notSet = false;
			for (FacilityEmissionFlow fef : controlEquipment
					.getCeEmissionFlows()) {
				if (sp == null) {
					sp = fef.getPercent();
				} else {
					if (!sp.equals(fef.getPercent())) {
						allSame = false;
					}
				}
				if (fef.getPercent() != null && fef.getPercent().length() != 0) {
					total += fef.getPercentValue();
				} else {
					notSet = true;
				}
			}
			if (!notSet && allSame) {
				// Are they actually 100%
				float fract = 100f / controlEquipment.getCeEmissionFlows()
						.size();
				String str = BaseDB.numberToxxx_xx((double) fract);
				if (str.equals(sp)) {
					// they actually do add to 100%
					total = 100f; // make them look that way
				}
			}
		}
		if (!notSet && (total < 99.995 || total > 100.005)) {
			validationMessage = new ValidationMessage("flowTotal",
					FLOW_TOTAL_WRONG1 + BaseDB.numberToxxx_xx(total)
							+ FLOW_TOTAL_WRONG2,
					ValidationMessage.Severity.ERROR, "controlEquipment:"
							+ tempCeId);
			validMessages.add(validationMessage);
		}
		return validMessages.toArray(new ValidationMessage[0]);
	}

	/**
	 * Submit facility detail.
	 * 
	 * @param fpId
	 *            FP Id
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ValidationMessage[] submitFacilityProfile(Integer fpId) throws DAOException {

		ValidationMessage[] validationMessages = new ArrayList<ValidationMessage>().toArray(new ValidationMessage[0]);
		boolean hasError = false;
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		if (fpId == null) {
			DAOException e = new DAOException("invalid fpId (null)");
			throw e;
		}

		try {
			Facility facility = retrieveFacilityProfile(fpId, true);

			if (!facility.isValidated()) {
				// 2483
				Facility internalFacility = retrieveFacilityByCorePlaceId(facility.getCorePlaceId(), false);
				if (internalFacility == null) {
					internalFacility = retrieveFacility(facility.getFacilityId(), false);
				}
				boolean internalApp = CompMgr.getAppName().equals(CommonConst.INTERNAL_APP);
				List<ValidationMessage> msgs = FacilityValidation.validateFacility(facility, facility.getFpId(),
						facility.getEmissionUnits());
				//validationMessages = facility.validateFacility(internalApp, internalFacility);
				//if (validationMessages.length > 0) {
				//	for (int i = 0; i < validationMessages.length; i++) {
				//		msgs.add(validationMessages[i]);
				//	}
				//}
				validationMessages = msgs.toArray(new ValidationMessage[0]);
				//validationMessages = facility.validateFacility(internalApp, internalFacility);
				//if (validationMessages.length > 0) {
				//	for (int i = 0; i < validationMessages.length; i++) {
				//		if (validationMessages[i].getSeverity() == ValidationMessage.Severity.ERROR) {
				//			hasError = true;
				//		}
				//	}
				//}
			}

			if (!hasError) {
				facility.setValidated(true);
				// facility.setSubmitted(true);
			} else {
				facility.setValidated(false);
				facility.setSubmitted(false);
			}

			facilityDAO.modifyFacility(facility);

			trans.complete();

		} catch (DAOException e) {
			cancelTransaction("fpId=" + fpId, trans, e);
		} catch (ValidationException ve) {
			logger.error("ValidationException caught: " + ve.getMessage());
			;
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return validationMessages;
	}

	/**
	 * Validate facility detail.
	 * 
	 * @param fpId
	 *            FP Id
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ValidationMessage[] validateFacilityProfile(Integer fpId) throws DAOException {

		ValidationMessage[] validationMessages = null;
		boolean hasError = false;
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		if (fpId == null) {
			DAOException e = new DAOException("invalid fpId (null)");
			throw e;
		}

		try {

			Facility facility = retrieveFacilityProfile(fpId, true);
			// 2483
			Facility internalFacility = null;
			if (facility.getCorePlaceId() != null) {
				internalFacility = retrieveFacilityByCorePlaceId(facility.getCorePlaceId(), false);
			}

			if (internalFacility == null) {
				internalFacility = retrieveFacility(facility.getFacilityId(), false);
			}
			boolean internalApp = CompMgr.getAppName().equals(CommonConst.INTERNAL_APP);

			List<ValidationMessage> msgs = FacilityValidation.validateFacility(facility, facility.getFpId(),
					facility.getEmissionUnits());
			//validationMessages = facility.validateFacility(internalApp, internalFacility);
			//if (validationMessages.length > 0) {
			//	for (int i = 0; i < validationMessages.length; i++) {
			//		msgs.add(validationMessages[i]);
			//	}
			//}
			

			List<ValidationMessage> euEpMsgs = new ArrayList<ValidationMessage>();
			List<EmissionUnit> lEmissionUnits = facility.getEmissionUnits();
			if (lEmissionUnits != null & !lEmissionUnits.isEmpty()) {
				for (EmissionUnit tempEU : lEmissionUnits) {
					if (tempEU.getOperatingStatusCd().equals(EuOperatingStatusDef.SD)
							|| tempEU.getOperatingStatusCd().equals(EuOperatingStatusDef.IV)) {
						continue;
					}
					FacilityValidation.validateFacilityEmissionUnit(facility, tempEU, euEpMsgs);
					List<EmissionProcess> emissionProcesses = tempEU.getEmissionProcesses();
					for (EmissionProcess tempEP : emissionProcesses) {
						FacilityValidation.validateFacilityEmissionProcess(facility, tempEP, tempEU.getEpaEmuId(), euEpMsgs);
					}
				}
			}
			msgs.addAll(euEpMsgs);
			
			List<ValidationMessage> ceMsgs = new ArrayList<ValidationMessage>();
			HashMap<String, Integer> valObject = new HashMap<String, Integer>();
			ArrayList<ControlEquipment> controlEquips = (ArrayList<ControlEquipment>) facility.getControlEquips();
			if (controlEquips != null & !controlEquips.isEmpty()) {
				for (ControlEquipment tempCE : controlEquips) {
					if (!tempCE.isActive()) {
						logger.debug("Skipping validation for non-active control equipment " + tempCE.getControlEquipmentId());
						continue;
					}
					FacilityValidation.validateFacilityControlEquipment(internalFacility, tempCE, ceMsgs, valObject);
				}
			}
			msgs.addAll(ceMsgs);
			
			validationMessages = msgs.toArray(new ValidationMessage[0]);
			
			if (validationMessages.length > 0) {
				for (int i = 0; i < validationMessages.length; i++) {
					if (validationMessages[i].getSeverity() == ValidationMessage.Severity.ERROR) {
						hasError = true;
					}
				}
			}

			facility.setSubmitted(false);

			if (!hasError) {
				facility.setValidated(true);
			} else {
				facility.setValidated(false);
			}

			facilityDAO.modifyFacility(facility);

			trans.complete();

		} catch (ValidationException ve) {
			logger.error("ValidationException caught: " + ve.getMessage());
		} catch (DAOException e) {
			cancelTransaction("fpId=" + fpId, trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return validationMessages;
	}

	/**
	 * Validate create facility.
	 * 
	 * @param facility
	 *            facility
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ValidationMessage[] validateCreateFacility(Facility facility)
			throws DAOException {
		ValidationMessage[] validationMessages;

		if (facility == null) {
			DAOException e = new DAOException("invalid fpId (null)");
			throw e;
		}

		validationMessages = facility.validateForCreateFacility();
		return validationMessages;
	}

	private void createRoles(FacilityDAO facilityDAO,
			InfrastructureDAO infraDAO, String facilityId, String countyCd)
			throws DAOException {
		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				FacilityRoleDef[] facRoleDefs;
				ArrayList<FacilityRole> facRoles = new ArrayList<FacilityRole>();
				FacilityRole facRole;

				facRoleDefs = infraDAO.retrieveDefaultFacilityRoles(countyCd);

				if (facRoleDefs == null || facRoleDefs.length == 0) {
					throw new DAOException(
							"Default user roles is not defined for the county.");
				}

				for (FacilityRoleDef tempRole : facRoleDefs) {
					facRole = new FacilityRole(tempRole, facilityId);
					facRoles.add(facRole);
				}
				facilityDAO.createFacilityRoles(facRoles);
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	private void createWebDavs(String facilityId) throws DAOException {
		try {
			// Create all of the required WebDAV subdirectories.
			String path = File.separator + "Facilities" + File.separator
					+ facilityId;
			DocumentUtil.mkDir(path);
			path = File.separator + "Facilities" + File.separator + facilityId
					+ File.separator + "Attachments";
			DocumentUtil.mkDir(path);
			path = File.separator + "Facilities" + File.separator + facilityId
					+ File.separator + "Applications";
			DocumentUtil.mkDir(path);
			path = File.separator + "Facilities" + File.separator + facilityId
					+ File.separator + "Permits";
			DocumentUtil.mkDir(path);
			path = File.separator + "Facilities" + File.separator + facilityId
					+ File.separator + "Correspondence";
			DocumentUtil.mkDir(path);
			path = File.separator + "Facilities" + File.separator + facilityId
					+ File.separator + "EmissionsReports";
			DocumentUtil.mkDir(path);
			path = File.separator + "Facilities" + File.separator + facilityId
					+ File.separator + "ComplianceReports";
			DocumentUtil.mkDir(path);
			path = File.separator + "Facilities" + File.separator + facilityId
					+ File.separator + "tmp";
			DocumentUtil.mkDir(path);
		} catch (IOException ioe) {
			// Throw it all away if we have a problem with the WebDAV directory
			// creation.

			throw new DAOException(ioe.getMessage(), ioe);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	private void removeFacilityDir(String facilityId) {
		String path = File.separator + "Facilities" + File.separator
				+ facilityId;
		try {
			DocumentUtil.rmDir(path);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage() + " Path=" + path, ioe);
		}
	}

	/**
	 * Get a new facility ID.
	 * 
	 * @param facilityIdRef
	 *            Facility inventory to create
	 * @return Facility Id
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String getNewFacilityId() throws DAOException {
		String facilityId = null;
		Transaction trans = TransactionFactory.createTransaction();

		try {
			facilityId = getNewFacilityId(trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return facilityId;
	}

	private String getNewFacilityId(Transaction trans) throws DAOException {
		String facilityId = null;
		Integer nextFacIdSeqNum = null;
		String formatNextFacIdSeqNum;
		FacilityDAO facilityDAO = facilityDAO(trans);
		Facility fac;

		try {
			while (true) {
				nextFacIdSeqNum = facilityDAO.nextfacilityIdSeqNum();
				if (nextFacIdSeqNum.intValue() > 999999) {
					DAOException e = new DAOException(
							"Cannot get any more facility ID. Maximum Sequence ID number reached.");
					throw e;
				}

				formatNextFacIdSeqNum = String.format("%06d", nextFacIdSeqNum);
				facilityId = "F" + formatNextFacIdSeqNum;
				fac = facilityDAO.retrieveFacility(facilityId);
				if (fac == null) {
					break;
				}
			}
		} catch (DAOException e) {
			throw e;
		}

		return facilityId;
	}

	/**
	 * Create a new facility. This method is only intended to be used for
	 * creating facilities in Stars2.
	 * 
	 * @param Facility
	 *            Facility inventory to create
	 * @return Facility new facility detail complete with all id's for new
	 *         contained objects
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Facility createFacility(Facility newFacility) throws DAOException {
		Integer fpId = null;

		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		String facilityId = null;
		try {
			// newFacility.setDoLaaCd(facilityIdRef.getDolaCd());
			newFacility.setDoLaaCd(newFacility.getPhyAddr().getDistrictCd());
			newFacility.setVersionId(-1);
			// newFacility.setOperatingStatusCd(OperatingStatusDef.NI);
			newFacility.setStartDate(new Timestamp(System.currentTimeMillis()));
			newFacility.setLastSubmissionVersion(0);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				facilityId = getNewFacilityId(trans);
				newFacility.setFacilityId(facilityId);
				newFacility.setLastSubmissionType("I");
			} else if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
				// facilityId = getPortalNewFacilityId(facilityIdRef);
				newFacility.setFacilityId(facilityId);
				newFacility.setLastSubmissionType("E");
			}

			// First create the base Facility, so we have a fp_id to work with.
			newFacility.setFpId(null);
			newFacility.setSipCompCd(ComplianceStatusDef.YES);
			newFacility.setMactCompCd(ComplianceStatusDef.YES);
			Facility tempFac = facilityDAO.createFacility(newFacility);
			fpId = tempFac.getFpId();
			facilityId = tempFac.getFacilityId();
			Timestamp startDate = tempFac.getStartDate();
			String companyName = tempFac.getCompanyName();
			Address phyAddr = newFacility.getPhyAddr();
			phyAddr.setAddressId(null);
			
			if (SystemPropertyDef.getSystemPropertyValueAsBoolean("APP_PLSS_AUTO_REPLICATION", true)){
				autoFillNullLocationData(phyAddr);
			}
			
			Address newAddress = infraDAO.createAddress(phyAddr);
			newFacility.setPhyAddr(newAddress);
			facilityDAO.addFacilityAddress(fpId, newAddress.getAddressId());

			// getCompany id from Company Name
			int companyId = facilityDAO.findCompanyId(companyName);

			// We may need to change this value in future
			int lastmodified = 1;

			// creating relationship between company and facility
			if (companyId != 0) {
				facilityDAO.addFacilityCompany(facilityId, startDate,
						companyId, lastmodified);
			}

			createRoles(facilityDAO, infraDAO, facilityId, newFacility
					.getPhyAddr().getCountyCd());
			createWebDavs(facilityId);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				internalFacilitySubmit1(newFacility,
						"CF" + newFacility.getFacilityId());

				String audLogOrgVal = "Facility does not Exist.";
				String audLogNewVal = "Facility Created :"
						+ tempFac.getFacilityId();
				int currentUserId = InfrastructureDefs.getCurrentUserId();
				createFieldAudLog(tempFac, tempFac.getFacilityId(), "fnm",
						audLogOrgVal, audLogNewVal, currentUserId, trans);
			}
			// Create ce_next_afs_id row to initialize AFS Action ID.
			// We cannot do this here because the SCSC ID to be used by
			// this facility is not yet known. It will have to be done later.

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("facilityId=" + facilityId, trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		logger.debug("Facility: " + newFacility.getFacilityId()
				+ " with fpId = " + fpId + " created.");

		facilityDAO.setTransaction(null);

		return retrieveFacility(facilityDAO, fpId);
	}

	/**
	 * delete facility. This method is only intended to be used for deleting
	 * facility from staging area.
	 * 
	 * @param fpId
	 *            fpId of facility to remove
	 * @return
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeFacility(Integer fpId, Transaction trans)
			throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ContinuousMonitorDAO continuousMonitorDAO = continuousMonitorDAO(trans);

		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				throw new DAOException(
						"Cannot delete facility; Operation is not supported.",
						"Cannot delete facility; Operation is not supported.");
			}
			
			if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
				throw new DAOException(
						"Cannot delete facility; Operation is not supported.",
						"Cannot delete facility; Operation is not supported.");
			}
			
			boolean staging = false;
			if (isPortalApp()) {
				staging = true;
			}
			Facility currentFacility = retrieveFacilityProfile(fpId, staging,
					trans);

			for (ContinuousMonitor cm : currentFacility
					.getContinuousMonitorList()) {

				continuousMonitorDAO.removeContinuousMonitorEqtList(cm
						.getContinuousMonitorId());

				facilityDAO.removeFacilityCemComLimitList(cm
						.getContinuousMonitorId());

				// delete references to associated eus
				continuousMonitorDAO.deleteAssociatedFpEuIdsByMonitorId(cm
						.getContinuousMonitorId());

				// delete references to associated release points
				continuousMonitorDAO
						.deleteAssociatedFpEgressPointIdsByMonitorId(cm
								.getContinuousMonitorId());

				continuousMonitorDAO.deleteContinuousMonitor(cm);

			}
			
			Facility facility = facilityDAO.retrieveFacility(fpId);
			for (Address address : facility.getAddresses()) {
				infraDAO.removeAddress(address.getAddressId());
			}
			facilityDAO.removeFacility(fpId);
		} catch (DAOException e) {
			throw new DAOException(
					"Cannot delete facility; Operation unsuccessful.", e);
		}
	}

	/**
	 * Deletes facility Inventory. This method is only intended to be used for
	 * deleting facility inventory.
	 * 
	 * @param fpId
	 *            fpId of facility to remove
	 * 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean deleteFacilityInv(Integer fpId)throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);		

		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				//Facility facility = facilityDAO.retrieveFacility(fpId);
				Facility facility = null;
				try{
				facility = retrieveFacilityProfile(fpId);	
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					throw new DAOException("Unable to get Facility :"
							+ ex.getMessage(), ex);
				}						
				
				for(FacilityNode fn :facilityDAO.retrieveFacilityEgressPoints(fpId)){					
					for(FacilityRelationship fr: fn.getRelationships()){
						facilityDAO.removeRelationShip(fr.getFromNodeId(), fr.getToNodeId());
					}					
				}
				for(EgressPoint ep :facility.getEgressPoints()){
					facilityDAO.removeEgressPoint(ep);
				}
				
				
				for(ControlEquipment ce : facility.getControlEquips()){
					
					for(FacilityRelationship fr: ce.getRelationships()){
						facilityDAO.removeRelationShip(fr.getFromNodeId(), fr.getToNodeId());						
					}
					
					for (PollutantsControlled pc :ce.getPollutantsControlled()){
						
						facilityDAO.removePollutantsControlled(ce.getFpNodeId());
						
					}
					
					for (DataDetail dd :ce.getCeDataDetails()){
						facilityDAO.removeControlEquipmentData(ce.getFpNodeId());
					}
					
					facilityDAO.removeControlEquipment(ce);
				}
				
								
				for(EmissionUnit eu :facility.getEmissionUnits()){
					for (EmissionProcess emp : eu.getEmissionProcesses()) {
						facilityDAO.removeEmissionProcess(emp);
					}	
					
					for (EuEmission eue :eu.getEuEmissions()){
						facilityDAO.removeEuEmissions(eu.getEmuId());
					}
					facilityDAO.removeEmissionUnit(eu.getEmuId());
				}
			}
          
			
		} catch (DAOException e) {			
			e.getMessage();
			return false;				
		}	
		return true;
	}
	
	
	
	
	/**
	 * Preserve facility Inventory. This method is only intended to be used for
	 * preserve facility inventory.
	 * 
	 * @param fpId
	 *            fpId of facility to remove
	 * 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer preserveFacilityInv(Integer fpId)throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);	
		CopyOnChangeMaps copyOnChangeMaps;

		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				//Facility facility = facilityDAO.retrieveFacility(fpId);
				Facility facility = null;
				try{
				facility = retrieveFacilityProfile(fpId);	
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
					throw new DAOException("Unable to get Facility :"
							+ ex.getMessage(), ex);
				}		
								
				// "version" the current facility...
				facility.setVersionId(facility
						.getMaxVersion() + 1);

				facility.setEndDate(Utility.getToday());
				
				facilityDAO.modifyFacility(facility);
				
				
				facility.setStartDate(Utility.getToday());
				facility.setEndDate(null);
				facility.setVersionId(-1);
				facility.setFpId(null);
				
				copyOnChangeMaps = createFacilityProfile(facility,
						trans, false);
				fpId = copyOnChangeMaps.fpId;

				trans.complete();
				facilityDAO.setTransaction(null);	
				
				
			}
          
			
		} catch (DAOException e) {			
			e.getMessage();
			return null;				
		}	
		return fpId;
	}
	
	/**
	 * delete facilitycontacts. This method is only intended to be used for
	 * deleting facility contactsfrom staging area.
	 * 
	 * @param cpId
	 *            fpId of facility to remove
	 * @return
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeFacilityContacts(String facilityId, Transaction trans)
			throws DAOException {
		// FacilityDAO facilityDAO = facilityDAO(trans);
		// InfrastructureHelper infraHelper = new InfrastructureHelper();

		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				throw new DAOException(
						"Cannot delete facility; Operation is not supported.",
						"Cannot delete facility; Operation is not supported.");
			}
			
			if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
				throw new DAOException(
						"Cannot delete facility; Operation is not supported.",
						"Cannot delete facility; Operation is not supported.");
			}

			// List<Contact> contacts = facilityDAO
			// .retrieveStagedFacilityContacts(facilityId);
			List<Contact> contacts = retrieveStagedContactsByFacilityId(facilityId);
			for (Contact contact : contacts) {
				// facilityDAO.removeFacilityContact(facilityId, contact);
				infrastructureHelper.deleteStagingContact(contact, trans);
			}

		} catch (DAOException e) {
			throw new DAOException(
					"Cannot delete facility; Operation unsuccessful.", e);
		}
	}

	/**
	 * Delete the specified emission unit. An EU can only be deleted if it is a
	 * temporary EU (indicated by an epaEmuId value starting with "TMP") and
	 * there are no emission processes associated with the EU.
	 * 
	 * @param eu
	 *            the Emission Unit to be deleted.
	 * @return
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeEmissionUnit(EmissionUnit eu) throws DAOException {
		if (eu != null) {
			Transaction trans = TransactionFactory.createTransaction();
			FacilityDAO facilityDAO = facilityDAO(trans);
			try {
				facilityDAO.removeEmissionUnit(eu.getEmuId());
			} catch (DAOException e) {
				cancelTransaction("emuId=" + eu.getEmuId(), trans, e);
			} finally { // Clean up our transaction stuff
				closeTransaction(trans);
			}
		}
	}

	private void addFieldAudLogs(List<FieldAuditLog> auditLogs,
			FieldAuditLog[] fieldAudLogs) {
		for (FieldAuditLog audLog : fieldAudLogs) {
			auditLogs.add(audLog);
		}
	}

	private void createGateWayFacilityFAL(Facility dapcFac,
			Facility gateWayFacility, List<FieldAuditLog> auditLogs) {
		Facility facility = new Facility();
		facility.setNewObject(false);

		facility.checkDirty("fnm", gateWayFacility.getFacilityId(),
				dapcFac.getName(), gateWayFacility.getName());
		facility.checkDirty(
				"opst",
				gateWayFacility.getFacilityId(),
				OperatingStatusDef.getData().getItems()
						.getItemDesc(dapcFac.getOperatingStatusCd()),
				OperatingStatusDef.getData().getItems()
						.getItemDesc(gateWayFacility.getOperatingStatusCd()));

		// others are not needed

		addFieldAudLogs(auditLogs, facility.getFieldAuditLogs());
	}

	private void createGateWayEpFAL(EmissionProcess dapcEp,
			EmissionProcess gateWayEp, List<FieldAuditLog> auditLogs,
			String facilityId) {
		List<FieldAuditLog> assocAudLogs = new ArrayList<FieldAuditLog>();

		EgressPoint dapcEgp;
		String audLogOrigVal = null;
		String audLogNewVal = null;

		for (EgressPoint egp : gateWayEp.getEgressPoints()) {
			dapcEgp = dapcEp.getEgressPoint(egp);
			if (dapcEgp == null) {
				audLogOrigVal = "Release point: " + egp.getReleasePointId()
						+ " not associated to emission process: "
						+ gateWayEp.getProcessId();
				audLogNewVal = "Release point: " + egp.getReleasePointId()
						+ "  associated to emission process: "
						+ gateWayEp.getProcessId();
				assocAudLogs.add(new FieldAuditLog("asso", facilityId,
						audLogOrigVal, audLogNewVal));
			} else {
				// use dirty bit to indicate it is found in dapc
				dapcEgp.setDirty(true);
			}
		}

		for (EgressPoint egp : dapcEp.getEgressPoints()) {
			if (egp.isDirty()) {
				egp.setDirty(false);
			} else {
				audLogOrigVal = "Release point: " + egp.getReleasePointId()
						+ " associated to emission process: "
						+ dapcEp.getProcessId();
				audLogNewVal = "Release point: " + egp.getReleasePointId()
						+ " not associated to emission process: "
						+ dapcEp.getProcessId();
				assocAudLogs.add(new FieldAuditLog("asso", facilityId,
						audLogOrigVal, audLogNewVal));
			}
		}

		ControlEquipment dapcCe;

		for (ControlEquipment ce : gateWayEp.getControlEquipments()) {
			dapcCe = dapcEp.getControlEquipment(ce);
			if (dapcCe == null) {
				audLogOrigVal = "Control equipment: "
						+ ce.getControlEquipmentId()
						+ " not associated to emission process: "
						+ gateWayEp.getProcessId();
				audLogNewVal = "Control equipment: "
						+ ce.getControlEquipmentId()
						+ "  associated to emission process: "
						+ gateWayEp.getProcessId();
				assocAudLogs.add(new FieldAuditLog("asso", facilityId,
						audLogOrigVal, audLogNewVal));
			} else {
				// use dirty bit to indicate it is found in dapc
				dapcCe.setDirty(true);
			}
		}

		for (ControlEquipment ce : dapcEp.getControlEquipments()) {
			if (ce.isDirty()) {
				ce.setDirty(false);
			} else {
				audLogOrigVal = "Control equipment: "
						+ ce.getControlEquipmentId()
						+ " associated to emission process: "
						+ dapcEp.getProcessId();
				audLogNewVal = "Release point: " + ce.getControlEquipmentId()
						+ " not associated to emission process: "
						+ dapcEp.getProcessId();
				assocAudLogs.add(new FieldAuditLog("asso", facilityId,
						audLogOrigVal, audLogNewVal));
			}
		}

		addFieldAudLogs(auditLogs, assocAudLogs.toArray(new FieldAuditLog[0]));
	}

	private void createGateWayEuFAL(EmissionUnit dapcEu,
			EmissionUnit gateWayEu, List<FieldAuditLog> auditLogs,
			String facilityId) {
		EmissionUnit eu = new EmissionUnit();
		eu.setNewObject(false);

		eu.checkDirty("euci", dapcEu.getEpaEmuId(), dapcEu.getCompanyId(),
				gateWayEu.getCompanyId());
		eu.checkDirty("eucd", dapcEu.getEpaEmuId(),
				dapcEu.getRegulatedUserDsc(), gateWayEu.getRegulatedUserDsc());
		eu.checkDirty(
				"euos",
				dapcEu.getEpaEmuId(),
				EuOperatingStatusDef.getData().getItems()
						.getItemDesc(dapcEu.getOperatingStatusCd()),
				EuOperatingStatusDef.getData().getItems()
						.getItemDesc(gateWayEu.getOperatingStatusCd()));
		eu.checkDirty(
				"eutc",
				dapcEu.getEpaEmuId(),
				TVClassification.getData().getItems()
						.getItemDesc(dapcEu.getTvClassCd()),
				TVClassification.getData().getItems()
						.getItemDesc(gateWayEu.getTvClassCd()));
		eu.checkDirty(
				"eues",
				dapcEu.getEpaEmuId(),
				ExemptStatusDef.getData().getItems()
						.getItemDesc(dapcEu.getExemptStatusCd()),
				ExemptStatusDef.getData().getItems()
						.getItemDesc(gateWayEu.getExemptStatusCd()));

		if (dapcEu != null) {
			eu.checkDirty(
					"euty",
					gateWayEu.getEpaEmuId(),
					"["
							+ dapcEu.getEpaEmuId()
							+ "] "
							+ EmissionUnitTypeDef
									.getData()
									.getItems()
									.getItemDesc(dapcEu.getEmissionUnitTypeCd()),
					"["
							+ gateWayEu.getEpaEmuId()
							+ "] "
							+ EmissionUnitTypeDef
									.getData()
									.getItems()
									.getItemDesc(
											gateWayEu.getEmissionUnitTypeCd()));
		}

		eu.checkDirty("euid", gateWayEu.getEpaEmuId(), dapcEu.getEpaEmuId(),
				gateWayEu.getEpaEmuId());

		List<FieldAuditLog> falList = new ArrayList<FieldAuditLog>();
		for (FieldAuditLog fal : eu.getFieldAuditLogs()) {
			if (gateWayEu.getCorrEpaEmuId() != null) {
				fal.setCorrEmuId(gateWayEu.getCorrEpaEmuId());
			}
			falList.add(fal);
		}
		// others are not needed

		addFieldAudLogs(auditLogs, falList.toArray(new FieldAuditLog[0]));

		EmissionProcess dapcEp;
		for (EmissionProcess ep : gateWayEu.getEmissionProcesses()) {
			dapcEp = dapcEu.getEmissionProcess(ep);
			if (dapcEp != null) {
				createGateWayEpFAL(dapcEp, ep, auditLogs, facilityId);
			}
		}
	}

	private void createGateWayEgpFAL(EgressPoint dapcEgp,
			EgressPoint gateWayEgp, List<FieldAuditLog> auditLogs) {
		EgressPoint egp = new EgressPoint();
		egp.setNewObject(false);

		egp.checkDirty("egid", gateWayEgp.getReleasePointId(),
				dapcEgp.getReleasePointId(), gateWayEgp.getReleasePointId());

		// others are not needed

		addFieldAudLogs(auditLogs, egp.getFieldAuditLogs());
	}

	private void createGateWayCeFAL(ControlEquipment dapcCe,
			ControlEquipment gateWayCe, List<FieldAuditLog> auditLogs,
			String facilityId) {
		ControlEquipment ce = new ControlEquipment();
		ce.setNewObject(false);

		ce.checkDirty("ceid", gateWayCe.getControlEquipmentId(),
				dapcCe.getControlEquipmentId(),
				gateWayCe.getControlEquipmentId());
		ce.checkDirty(
				"cety",
				gateWayCe.getControlEquipmentId(),
				ContEquipTypeDef.getData().getItems()
						.getItemDesc(dapcCe.getEquipmentTypeCd()),
				ContEquipTypeDef.getData().getItems()
						.getItemDesc(gateWayCe.getEquipmentTypeCd()));
		ce.checkDirty(
				"ceos",
				gateWayCe.getControlEquipmentId(),
				CeOperatingStatusDef.getData().getItems()
						.getItemDesc(dapcCe.getOperatingStatusCd()),
				CeOperatingStatusDef.getData().getItems()
						.getItemDesc(gateWayCe.getOperatingStatusCd()));
		// others are not needed

		addFieldAudLogs(auditLogs, ce.getFieldAuditLogs());

		List<FieldAuditLog> pollAudLogs = new ArrayList<FieldAuditLog>();

		PollutantsControlled dapcPoll;
		for (PollutantsControlled pollCont : gateWayCe
				.getPollutantsControlled()) {
			dapcPoll = dapcCe.getPollCont(pollCont);
			if (dapcPoll == null) {
				pollAudLogs.add(new FieldAuditLog("cepa", gateWayCe
						.getControlEquipmentId(), BaseDB.FLD_AUD_LOG_NO_VALUE,
						PollutantDef.getData().getItems()
								.getItemDesc(pollCont.getPollutantCd())));
			} else {
				// use dirty bit to indicate it is found in dapc
				dapcPoll.setDirty(true);
			}
		}

		for (PollutantsControlled pollCont : dapcCe.getPollutantsControlled()) {
			if (pollCont.isDirty()) {
				pollCont.setDirty(false);
			} else {
				pollAudLogs.add(new FieldAuditLog("cepd", gateWayCe
						.getControlEquipmentId(), PollutantDef.getData()
						.getItems().getItemDesc(pollCont.getPollutantCd()),
						BaseDB.FLD_AUD_LOG_NO_VALUE));
			}
		}

		addFieldAudLogs(auditLogs, pollAudLogs.toArray(new FieldAuditLog[0]));

		List<FieldAuditLog> assocAudLogs = new ArrayList<FieldAuditLog>();
		EgressPoint dapcEgp;
		String audLogOrigVal = null;
		String audLogNewVal = null;

		for (EgressPoint egp : gateWayCe.getEgressPoints()) {
			dapcEgp = dapcCe.getEgressPoint(egp);
			if (dapcEgp == null) {
				audLogOrigVal = "Release point: " + egp.getReleasePointId()
						+ " not associated to control equipment: "
						+ gateWayCe.getControlEquipmentId();
				audLogNewVal = "Release point: " + egp.getReleasePointId()
						+ "  associated to control equipment: "
						+ gateWayCe.getControlEquipmentId();
				assocAudLogs.add(new FieldAuditLog("asso", facilityId,
						audLogOrigVal, audLogNewVal));
			} else {
				// use dirty bit to indicate it is found in dapc
				dapcEgp.setDirty(true);
			}
		}

		for (EgressPoint egp : dapcCe.getEgressPoints()) {
			if (egp.isDirty()) {
				egp.setDirty(false);
			} else {
				audLogOrigVal = "Release point: " + egp.getReleasePointId()
						+ " associated to control equipment: "
						+ dapcCe.getControlEquipmentId();
				audLogNewVal = "Release point: " + egp.getReleasePointId()
						+ " not associated to control equipment: "
						+ dapcCe.getControlEquipmentId();
				assocAudLogs.add(new FieldAuditLog("asso", facilityId,
						audLogOrigVal, audLogNewVal));
			}
		}

		ControlEquipment dapcCe1;

		for (ControlEquipment ce1 : gateWayCe.getControlEquips()) {
			dapcCe1 = dapcCe.getControlEquipment(ce);
			if (dapcCe1 == null) {
				audLogOrigVal = "Control equipment: "
						+ ce1.getControlEquipmentId()
						+ " not associated to control equipment: "
						+ gateWayCe.getControlEquipmentId();
				audLogNewVal = "Control equipment: "
						+ ce1.getControlEquipmentId()
						+ "  associated to control equipment: "
						+ gateWayCe.getControlEquipmentId();
				assocAudLogs.add(new FieldAuditLog("asso", facilityId,
						audLogOrigVal, audLogNewVal));
			} else {
				// use dirty bit to indicate it is found in dapc
				dapcCe.setDirty(true);
			}
		}

		for (ControlEquipment ce1 : dapcCe.getControlEquips()) {
			if (ce.isDirty()) {
				ce.setDirty(false);
			} else {
				audLogOrigVal = "Control equipment: "
						+ ce1.getControlEquipmentId()
						+ " associated to emission process: "
						+ dapcCe.getControlEquipmentId();
				audLogNewVal = "Release point: " + ce1.getControlEquipmentId()
						+ " not associated to emission process: "
						+ dapcCe.getControlEquipmentId();
				assocAudLogs.add(new FieldAuditLog("asso", facilityId,
						audLogOrigVal, audLogNewVal));
			}
		}

		addFieldAudLogs(auditLogs, assocAudLogs.toArray(new FieldAuditLog[0]));
	}

	private void createGateWayFacilityToDoLogs(Facility dapcFac,
			Facility gateWayFacility, List<FieldAuditLog> toDoLogs) {
		Facility facility = new Facility();
		facility.setNewObject(false);

		facility.fieldChangeEventLog("fnme", "N/A", dapcFac.getName(),
				gateWayFacility.getName(), true);
		facility.fieldChangeEventLog(
				"fops",
				"N/A",
				OperatingStatusDef.getData().getItems()
						.getItemDesc(dapcFac.getOperatingStatusCd()),
				OperatingStatusDef.getData().getItems()
						.getItemDesc(gateWayFacility.getOperatingStatusCd()));
		facility.fieldChangeEventLog("fstv", "N/A", dapcFac.isTivInd(),
				gateWayFacility.isTivInd());
		facility.fieldChangeEventLog("fstr", "N/A", dapcFac.isSec112(),
				gateWayFacility.isSec112());

		addFieldAudLogs(toDoLogs, facility.getFieldEventLogs());
	}

	private void createGateWayEuToDoLogs(EmissionUnit dapcEu,
			EmissionUnit gateWayEu, List<FieldAuditLog> toDoLogs,
			List<FieldAuditLog> eisToDoLogs) {
		EmissionUnit eu = new EmissionUnit();
		eu.setNewObject(false);

		eu.fieldChangeEventLog("feuc", dapcEu.getEpaEmuId(),
				dapcEu.getCompanyId(), gateWayEu.getCompanyId());
		eu.fieldChangeEventLog("feud", dapcEu.getEpaEmuId(),
				dapcEu.getRegulatedUserDsc(), gateWayEu.getRegulatedUserDsc());
		eu.fieldChangeEventLog(
				"feuo",
				dapcEu.getEpaEmuId(),
				EuOperatingStatusDef.getData().getItems()
						.getItemDesc(dapcEu.getOperatingStatusCd()),
				EuOperatingStatusDef.getData().getItems()
						.getItemDesc(gateWayEu.getOperatingStatusCd()));
		eu.fieldChangeEventLog(
				"feuv",
				dapcEu.getEpaEmuId(),
				TVClassification.getData().getItems()
						.getItemDesc(dapcEu.getTvClassCd()),
				TVClassification.getData().getItems()
						.getItemDesc(gateWayEu.getTvClassCd()), true);
		eu.fieldChangeEventLog(
				"feue",
				dapcEu.getEpaEmuId(),
				ExemptStatusDef.getData().getItems()
						.getItemDesc(dapcEu.getExemptStatusCd()),
				ExemptStatusDef.getData().getItems()
						.getItemDesc(gateWayEu.getExemptStatusCd()), true);

		addFieldAudLogs(toDoLogs, eu.getFieldEventLogs());

		EmissionProcess dapcEp;
		for (EmissionProcess ep : gateWayEu.getEmissionProcesses()) {
			dapcEp = dapcEu.getEmissionProcess(ep);
			if (dapcEp != null) {
				createGateWayEpToDoLogs(dapcEp, ep, eisToDoLogs);
			}
		}
	}

	private void createGateWayEpToDoLogs(EmissionProcess dapcEp,
			EmissionProcess gateWayEp, List<FieldAuditLog> eisToDoLogs) {
		EmissionProcess ep = new EmissionProcess();
		ep.setNewObject(false);

		ep.fieldChangeEventLog("feps", dapcEp.getProcessId(),
				dapcEp.getSccId(), gateWayEp.getSccId());

		addFieldAudLogs(eisToDoLogs, ep.getFieldEventLogs());
	}

	private void createGateWayCeToDoLogs(ControlEquipment dapcCe,
			ControlEquipment gateWayCe, List<FieldAuditLog> toDoLogs) {
		ControlEquipment ce = new ControlEquipment();
		ce.setNewObject(false);

		ce.fieldChangeEventLog(
				"fceo",
				dapcCe.getControlEquipmentId(),
				CeOperatingStatusDef.getData().getItems()
						.getItemDesc(dapcCe.getOperatingStatusCd()),
				CeOperatingStatusDef.getData().getItems()
						.getItemDesc(gateWayCe.getOperatingStatusCd()));

		addFieldAudLogs(toDoLogs, ce.getFieldEventLogs());
	}

	private void createGateWayEgpToDoLogs(EgressPoint dapcEgp,
			EgressPoint gateWayEgp, List<FieldAuditLog> toDoLogs,
			List<FieldAuditLog> eisToDoLogs) {
		EgressPoint egp = new EgressPoint();
		egp.setNewObject(false);
		egp.fieldChangeEventLog(
				"fego",
				dapcEgp.getReleasePointId(),
				EgOperatingStatusDef.getData().getItems()
						.getItemDesc(dapcEgp.getOperatingStatusCd()),
				EgOperatingStatusDef.getData().getItems()
						.getItemDesc(gateWayEgp.getOperatingStatusCd()));
		addFieldAudLogs(toDoLogs, egp.getFieldEventLogs());

		egp = new EgressPoint();
		egp.setNewObject(false);
		egp.fieldChangeEventLog(
				"fegt",
				dapcEgp.getReleasePointId(),
				EgrPointTypeDef.getData().getItems()
						.getItemDesc(dapcEgp.getEgressPointTypeCd()),
				EgrPointTypeDef.getData().getItems()
						.getItemDesc(gateWayEgp.getEgressPointTypeCd()));
		addFieldAudLogs(eisToDoLogs, egp.getFieldEventLogs());
	}

	private Facility combineFacilityProfile(Facility gateWayFacility,
			Facility dapcFac, List<FieldAuditLog> auditLogs,
			List<FieldAuditLog> toDoLogs, List<FieldAuditLog> eisToDoLogs,
			Transaction trans) throws DAOException {
		EmissionUnit dapcEmissionUnit;
		ControlEquipment dapcContEquip;
		EgressPoint dapcEgrPoint;
		Facility newFacility = gateWayFacility;

		// update Facility for attributes changable by AQD only

		newFacility.copyFacilityData(dapcFac);

		createGateWayFacilityFAL(dapcFac, newFacility, auditLogs);
		createGateWayFacilityToDoLogs(dapcFac, newFacility, toDoLogs);

		ArrayList<Integer> cmIds = new ArrayList<Integer>();
		Map<ContinuousMonitor,ContinuousMonitor> refreshMons = new HashMap<ContinuousMonitor,ContinuousMonitor>();
		for (ContinuousMonitor newFacMon : newFacility.getContinuousMonitorList()) {
			cmIds.add(newFacMon.getCorrMonitorId());
			for (ContinuousMonitor dapcFacMon : dapcFac.getContinuousMonitorList()) {
				if (newFacMon.getCorrMonitorId().equals(dapcFacMon.getCorrMonitorId())) {
					refreshMons.put(newFacMon, dapcFacMon);
				}
			}
		}
		for (ContinuousMonitor newFacMon : refreshMons.keySet()) {
			ContinuousMonitor dapcFacMon = refreshMons.get(newFacMon);
			
			// if the internal has a newer facility version than the portal,
			// then get the emuIds and fpNodeIds from the gateway facility so 
			// that the monitor and the associated object(s) relationship is 
			// not lost after the submission
			if (!newFacility.getFpId().equals(dapcFac.getFpId())) {
				dapcFacMon
						.setAssociatedFpEuIds(replaceAssociatedFpEuIds(dapcFac,
								newFacility, dapcFacMon.getAssociatedFpEuIds()));
				
				dapcFacMon
						.setAssociatedFpEgressPointIds(replaceAssociatedFpEgressPointIds(
								dapcFac, newFacility,
								dapcFacMon.getAssociatedFpEgressPointIds()));
			}
			
			// combine the cem/com limit list from both internal and portal facilities
			// so that any limits that were deleted internally can be added back
			dapcFacMon.setFacilityCemComLimitList(combineCemComLimitLists(
					dapcFacMon, newFacMon));
			
			newFacility.getContinuousMonitorList().remove(newFacMon);
			newFacility.getContinuousMonitorList().add(dapcFacMon);
		}

		ArrayList<Integer> limIds = new ArrayList<Integer>();
		Map<FacilityCemComLimit,FacilityCemComLimit> refreshLims =
				new HashMap<FacilityCemComLimit,FacilityCemComLimit>();
		for (FacilityCemComLimit newFacLim : newFacility.getFacilityCemComLimitList()) {
			limIds.add(newFacLim.getCorrLimitId());
			for (FacilityCemComLimit dapcFacLim : dapcFac.getFacilityCemComLimitList()) {
				if (newFacLim.getCorrLimitId().equals(dapcFacLim.getCorrLimitId())) {
					refreshLims.put(newFacLim, dapcFacLim);
				}
			}
		}
		for (FacilityCemComLimit newFacLim : refreshLims.keySet()) {
			newFacility.getFacilityCemComLimitList().remove(newFacLim);
			newFacility.getFacilityCemComLimitList().add(refreshLims.get(newFacLim));
		}
		
		ArrayList<Integer> euIds = new ArrayList<Integer>();
		ArrayList<Integer> ceIds = new ArrayList<Integer>();
		ArrayList<Integer> epIds = new ArrayList<Integer>();
		
		for (EmissionUnit tempEU : newFacility.getEmissionUnits()) {
			euIds.add(tempEU.getCorrEpaEmuId());
			dapcEmissionUnit = dapcFac.getGateWayEmissionUnit(tempEU);

			if (dapcEmissionUnit == null)
				continue;

			if (EuOperatingStatusDef.SD.equals(tempEU.getOperatingStatusCd())
					&& !EuOperatingStatusDef.SD.equals(dapcEmissionUnit
							.getOperatingStatusCd())) {
				// terminate permit EUs associated with newly shutdown EU
				// (mantis #3230)
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("Task Name", "Emissions Unit Shutdown");
				String notes;
				if (PermitClassDef.TV.equals(newFacility.getPermitClassCd())) {
					notes = "Emission Unit: "
							+ tempEU.getEpaEmuId()
							+ " marked shut down. "
							+ "If an EU is marked as permanently shutdown, "
							+ "the responsible party may have to reapply for a permit to install (PTI) "
							+ "for the EU prior to operating again.  Additionally, the responsible party "
							+ "must submit a notification of shutdown or request a permit modification.  "
							+ "Please contact your DO/LAA representative for assistance";
				} else {
					notes = "Emission Unit: "
							+ tempEU.getEpaEmuId()
							+ " marked shut down. "
							+ "Any EU marked as permanently shutdown may result in a revoked permit "
							+ "or portion of a permit.  Additionally, the responsible party may have to "
							+ "reapply for a permit to install (PTI) for the EU prior to operating again";
				}
				data.put("Notes", notes);
				newFacility.setFacilityRoles(dapcFac.getFacilityRoles()); // so
																			// newFacility
																			// has
																			// the
																			// roles
																			// available
				performShutdownToDo(newFacility, CommonConst.GATEWAY_USER_ID,
						data, trans);
				try {
					PermitService permitBO = ServiceFactory.getInstance()
							.getPermitService();
					permitBO.terminatePermitEUsForShutdownEU(newFacility,
							tempEU);
				} catch (ServiceFactoryException e) {
					logger.error(
							"Exception while attempting to mark permit EUs as terminated for "
									+ newFacility.getFacilityId(), e);
				} catch (RemoteException e) {
					logger.error(
							"Exception while attempting to mark permit EUs as terminated for "
									+ newFacility.getFacilityId(), e);
				}
			}

			tempEU.copyEmissionUnitData(dapcEmissionUnit);
			createGateWayEuFAL(dapcEmissionUnit, tempEU, auditLogs,
					newFacility.getFacilityId());
			createGateWayEuToDoLogs(dapcEmissionUnit, tempEU, toDoLogs,
					eisToDoLogs);

		}

		for (ControlEquipment tempCE : newFacility.getControlEquips()) {
			ceIds.add(tempCE.getCorrelationId());
			dapcContEquip = dapcFac.getControlEquipment(tempCE);
			if (dapcContEquip != null) {
				tempCE.copyControlEquipmentData(dapcContEquip);
				createGateWayCeFAL(dapcContEquip, tempCE, auditLogs,
						newFacility.getFacilityId());
				createGateWayCeToDoLogs(dapcContEquip, tempCE, toDoLogs);
			}
		}

		for (EgressPoint tempEGP : newFacility.getEgressPoints()) {
			epIds.add(tempEGP.getCorrelationId());
			dapcEgrPoint = dapcFac.getEgressPoint(tempEGP);
			if (dapcEgrPoint != null) {
				tempEGP.copyEgressPointData(dapcEgrPoint);
				createGateWayEgpFAL(dapcEgrPoint, tempEGP, auditLogs);
				createGateWayEgpToDoLogs(dapcEgrPoint, tempEGP, toDoLogs,
						eisToDoLogs);
			}
		}

		// 2161
		// In some cases (e.g. emissions inventory associated with a historical
		// facility version) a facility task comes in from the portal
		// with a version id other than -1 
		if (newFacility.getVersionId() == -1) {
			ArrayList<FacilityCemComLimit> candidateCL = new ArrayList<FacilityCemComLimit>();
			ArrayList<ContinuousMonitor> candidateCM = new ArrayList<ContinuousMonitor>();
			ArrayList<EmissionUnit> candidateEU = new ArrayList<EmissionUnit>();
			ArrayList<ControlEquipment> candidateCE = new ArrayList<ControlEquipment>();
			ArrayList<EmissionProcess> candidateEP = new ArrayList<EmissionProcess>();
			ArrayList<EgressPoint> candidateEGP = new ArrayList<EgressPoint>();

			for (ContinuousMonitor mon : dapcFac.getContinuousMonitorList()) {
				if (!cmIds.contains(mon.getCorrMonitorId())) {
					// if the internal has a newer facility version than the portal,
					// then get the emuIds and fpNodeIds from the gateway facility so 
					// that the monitor and the associated object(s) relationship is 
					// not lost after the submission
					if (!newFacility.getFpId().equals(dapcFac.getFpId())) {
						mon.setAssociatedFpEuIds(replaceAssociatedFpEuIds(
								dapcFac, newFacility,
								mon.getAssociatedFpEuIds()));
						
						mon.setAssociatedFpEgressPointIds(replaceAssociatedFpEgressPointIds(
								dapcFac, newFacility,
								mon.getAssociatedFpEgressPointIds()));
					}
					
					candidateCM.add(mon);
				}
			}
			for (FacilityCemComLimit lim : dapcFac.getFacilityCemComLimitList()) {
				if (!limIds.contains(lim.getCorrLimitId())) {
					candidateCL.add(lim);
				}
			}
			
			for (EmissionUnit tempEU : dapcFac.getEmissionUnits()) {
				if (!euIds.contains(tempEU.getCorrEpaEmuId())) {
					logger.debug(" EU "
							+ tempEU.getEpaEmuId()
							+ " ("
							+ tempEU.getCorrEpaEmuId()
							+ ") exists internally, but not externally. Will be kept in FP.");

					candidateEU.add(tempEU);
					for (EmissionProcess dapcEP : tempEU.getEmissionProcesses()) {
						logger.debug(" EP "
								+ dapcEP.getProcessId()
								+ " associated with EU "
								+ tempEU.getEpaEmuId()
								+ " ("
								+ tempEU.getCorrEpaEmuId()
								+ ") exists internally, but not externally. Will be kept in FP.");
						candidateEP.add(dapcEP);
						for (ControlEquipment dapcCE : dapcEP
								.getControlEquipments()) {
							logger.debug(" CE "
									+ dapcCE.getControlEquipmentId()
									+ " associated with EU "
									+ tempEU.getEpaEmuId()
									+ " ("
									+ tempEU.getCorrEpaEmuId()
									+ ") exists internally, but not externally. Will be kept in FP.");
							if (!candidateCE.contains(dapcCE)) {
								candidateCE.add(dapcCE);
								addSubCERP(dapcCE, candidateCE, candidateEGP);
							}
						}
						for (EgressPoint dapcEGP : dapcEP.getEgressPoints()) {
							logger.debug(" RP "
									+ dapcEGP.getReleasePointId()
									+ " associated with EU "
									+ tempEU.getEpaEmuId()
									+ " ("
									+ tempEU.getCorrEpaEmuId()
									+ ") exists internally, but not externally. Will be kept in FP.");
							if (!candidateEGP.contains(dapcEGP))
								candidateEGP.add(dapcEGP);
						}
					}
				}
			}

			Collections.sort(candidateCL);
			for (FacilityCemComLimit lim : candidateCL) {
				newFacility.addFacilityCemComLimit(lim);
			}
			
			Collections.sort(candidateCM);
			for (ContinuousMonitor mon : candidateCM) {
				newFacility.addContinuousMonitor(mon);
			}

			// Only rename other EP name, EP follow by EU(In other word, adding
			// EU will add EP at same time)
			Collections.sort(candidateEP);
			for (EmissionProcess tempEP : candidateEP) {
				logger.debug(" Adding EP " + tempEP.getProcessId());
				newFacility.insertEmissionProcess(tempEP);
			}

			Collections.sort(candidateEU);
			for (EmissionUnit tempEU : candidateEU) {
				logger.debug(" Adding EU " + tempEU.getEpaEmuId());
				newFacility.insertEmissionUnit(tempEU);
			}

			Collections.sort(candidateCE);
			for (ControlEquipment tempCE : candidateCE) {
				if (!ceIds.contains(tempCE.getCorrelationId())) {
					logger.debug(" Adding CE "
							+ tempCE.getControlEquipmentId());
					newFacility.insertControlEquip(tempCE);
				}
			}

			Collections.sort(candidateEGP);
			for (EgressPoint tempEGP : candidateEGP) {
				if (!epIds.contains(tempEGP.getCorrelationId())) {
					logger.debug(" Adding RP "
							+ tempEGP.getReleasePointId());
					newFacility.insertEgressPoint(tempEGP);
				}
			}
		}
		
		for (ContinuousMonitor newFacMon : newFacility.getContinuousMonitorList()) {
			for (ContinuousMonitor dapcMon : dapcFac.getContinuousMonitorList()) {
				if (!newFacMon.getCorrMonitorId().equals(dapcMon.getCorrMonitorId()) &&
						newFacMon.getMonId().equals(dapcMon.getMonId())) {
					newFacility.insertContinuousMonitor(newFacMon);
				}
			}
		}
		Collections.sort(newFacility.getContinuousMonitorList());
		Collections.sort(newFacility.getFacilityCemComLimitList());

		return newFacility;
	}

	private void addSubCERP(ControlEquipment dapcCE,
			ArrayList<ControlEquipment> candidateCE,
			ArrayList<EgressPoint> candidateEGP) {

		for (ControlEquipment subCE : dapcCE.getControlEquips()) {
			logger.debug(" CE "
					+ subCE.getControlEquipmentId()
					+ " associated with CE "
					+ dapcCE.getControlEquipmentId()
					+ " exists internally, but not externally. Will be kept in FP.");
			if (!candidateCE.contains(subCE)) {
				candidateCE.add(subCE);
				addSubCERP(subCE, candidateCE, candidateEGP);
			}
		}

		for (EgressPoint subRP : dapcCE.getEgressPoints()) {
			logger.debug(" RP "
					+ subRP.getReleasePointId()
					+ " associated with CE "
					+ dapcCE.getControlEquipmentId()
					+ " exists internally, but not externally. Will be kept in FP.");
			if (!candidateEGP.contains(subRP))
				candidateEGP.add(subRP);
		}
	}

	/**
	 * Create a new facility detail submitted from gateway.
	 * 
	 * @param Facility
	 *            Facility inventory to create from gateway.
	 * @parm trans transaction
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Facility createFacilityProfileFromGateWay(Facility gateWayFacility,
			Transaction trans) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		createFacilityProfileFromGateWay(gateWayFacility, null, trans);
		return facilityDAO.retrieveFacility(gateWayFacility.getFacilityId());
	}

	/**
	 * Create a new facility detail submitted from gateway.
	 * 
	 * @param Facility
	 *            Facility inventory to create from gateway.
	 * @param newFpId
	 *            FP iD to be used when creating a new Facility
	 * @parm trans transaction
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void createFacilityProfileFromGateWay(Facility gateWayFacility,
			Integer newFpId, Transaction trans) throws DAOException {
		boolean increaseVersions = true;
		boolean cl = false;
		Facility dapcFac = null;
		Integer dapcFpId = null;
		Facility newFacility;
		Timestamp revisedDate;
		List<FieldAuditLog> auditLogs = new ArrayList<FieldAuditLog>();
		List<FieldAuditLog> toDoLogs = new ArrayList<FieldAuditLog>(); // for
																		// DOLA
																		// Permit
																		// Writter
																		// Role
		List<FieldAuditLog> eisToDoLogs = new ArrayList<FieldAuditLog>(); // for
																			// CO
																			// EIS
																			// facility
																			// Role

		logger.debug("createFacilityProfileFromGateWay started; facility = "
				+ gateWayFacility.getFacilityId());

		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		logger.debug("Facility has addresses: "
				+ gateWayFacility.getAddresses() != null);

		try {

			// facility may have an attestation document attached to it
			// save it for now (in case gateWayFacility object gets changed) and
			// add document
			// near the end of this method
			if (gateWayFacility.getAttestationDocument() != null) {
				logger.debug(" Adding Attestation document with id = "
						+ gateWayFacility.getAttestationDocument()
								.getDocumentID());
				Attachment attestation = (Attachment) documentDAO(trans)
						.createDocument(
								gateWayFacility.getAttestationDocument());
				facilityDAO(trans).createFacilityAttachment(attestation);
			} else {
				logger.debug(" No Attestation document for facility submission for facility "
						+ gateWayFacility.getFacilityId());
			}

			if (gateWayFacility.getEndDate() == null) {
				dapcFac = facilityDAO
						.retrieveFacilityByFacilityId(gateWayFacility
								.getFacilityId());
				// TODO Fix this
				// if gateWayFacility has an old core place id, it will be
				// treated as a "new"
				// facility. Should we disable new facility concept entirely?
				if (dapcFac == null) {
					logger.error("AQD Facility not found; Facility ID = "
							+ gateWayFacility.getFacilityId());
					throw new DAOException(
							"AQD Facility not found; Facility ID = "
									+ gateWayFacility.getFacilityId());
				} else {
					dapcFpId = dapcFac.getFpId();
				}
			} else {
				dapcFpId = gateWayFacility.getFpId();
			}

			logger.debug("dapcFpId = " + dapcFpId);
			dapcFac = retrieveFacilityProfile(dapcFpId, false);
			if (gateWayFacility.getLastSubmissionType().equals(
					dapcFac.getLastSubmissionType())) {
				if (gateWayFacility.getLastSubmissionVersion().equals(
						dapcFac.getLastSubmissionVersion())) {
					logger.debug(" Identical facility with fp id "
							+ dapcFpId + "; No new version created.");
					return;
				}
			}

			if (!gateWayFacility.getFacilityId()
					.equals(dapcFac.getFacilityId())) {
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("Task Name",
						"Portal Submittal With Incorrect Facility ID");
				String notes = "A facility change is submitted by a Portal user that has different Facility ID ["
						+ gateWayFacility.getFacilityId()
						+ "]. The new facility ID is "
						+ dapcFac.getFacilityId();
				data.put("Notes", notes);
				Integer roleUserId = dapcFac
						.getFacilityRoles()
						.get(us.oh.state.epa.stars2.def.FacilityRoleDef.FACILITY_PROFILE_ADMIN)
						.getUserId();
				createToDoEntry(dapcFac.getFpId(), data, "N", new Timestamp(
						System.currentTimeMillis()), null, roleUserId, trans,
						CommonConst.GATEWAY_USER_ID);
			}

			newFacility = combineFacilityProfile(gateWayFacility, dapcFac,
					auditLogs, toDoLogs, eisToDoLogs, trans);
			try {
				updateFacilityFromCoreDB(dapcFac);
			} catch (Exception e) {
				logger.warn(
						"Accessing facility from COREDB failed; Continue updating IMPACT DB; facility ID = "
								+ dapcFac.getFacilityId() + e.getMessage(), e);
			}
			/*
			 * if (!newFacility.getName().equals(dapcFac.getName()) ||
			 * !newFacility.latitudeEquals(dapcFac) ||
			 * !newFacility.longitudeEquals(dapcFac)) {
			 */
			if (!newFacility.getName().equals(dapcFac.getName())) {
				cl = true;
			}

			Timestamp splitEndDate = dapcFac.getEndDate();
			Integer splitVersionId = dapcFac.getVersionId();
			Integer newVersionId;

			if (splitVersionId.equals(dapcFac.getMaxVersion())) {
				increaseVersions = false;
			}

			if (splitVersionId == -1) {
				// spliting the current vesrion of facility
				if (splitVersionId.equals(dapcFac.getMaxVersion())) {
					// only one version of facility
					splitVersionId = 0;
				} else {
					splitVersionId = dapcFac.getMaxVersion() + 1;
				}
				increaseVersions = false;
				newVersionId = -1;
			} else {
				newVersionId = splitVersionId + 1;
			}

			if (increaseVersions) {
				facilityDAO.increaseVersionIds(dapcFac.getFacilityId(),
						splitVersionId);
			}

			revisedDate = new Timestamp(System.currentTimeMillis());
			dapcFac.setVersionId(splitVersionId);
			dapcFac.setEndDate(revisedDate);
			dapcFac.setCopyOnChange(false);

			facilityDAO.modifyFacility(dapcFac);

			// Make a new "current" version of this facility...
			newFacility.setStartDate(revisedDate);
			newFacility.setEndDate(splitEndDate);
			newFacility.setVersionId(newVersionId);

			// create field audit logs
			infraDAO.createFieldAuditLogs(newFacility.getFacilityId(),
					newFacility.getName(), CommonConst.GATEWAY_USER_ID,
					auditLogs.toArray(new FieldAuditLog[0]));

			newFacility.setCopyOnChange(true);
			newFacility.setFpId(newFpId);

			// reconstruct the flow factors
			setRelationships(newFacility);

			createFacilityProfile(newFacility, trans, false);

			revisedDate = new Timestamp(System.currentTimeMillis());
			FacilityNote historyNote = new FacilityNote();
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			historyNote.setNoteTxt("Submit of Facility at : "
					+ dateFormat.format(revisedDate));
			historyNote.setDateEntered(revisedDate);
			historyNote.setUserId(1); // for now
			historyNote.setNoteTypeCd(NoteType.HIST);
			historyNote.setFpId(newFacility.getFpId());
			historyNote.setFacilityId(newFacility.getFacilityId());
			createFacilityNote(historyNote, trans);

			createFieldChangeToDoLog(
					toDoLogs.toArray(new FieldAuditLog[0]),
					newFacility.getFacilityId(),
					newFacility.getFpId(),
					trans,
					us.oh.state.epa.stars2.def.FacilityRoleDef.FACILITY_PROFILE_ADMIN,
					"Portal Facility Detail Change");
			createFieldChangeToDoLog(
					eisToDoLogs.toArray(new FieldAuditLog[0]),
					newFacility.getFacilityId(),
					newFacility.getFpId(),
					trans,
					us.oh.state.epa.stars2.def.FacilityRoleDef.FACILITY_PROFILE_ADMIN,
					"Portal Facility Detail Change");
			if (cl) {
				try {
					internalFacilitySubmit(newFacility,
							"GW" + newFacility.getFacilityId());
				} catch (Exception e) {
					logger.warn(
							"Updating COREDB failed; Continue updating STARS2 DB; facility ID = "
									+ dapcFac.getFacilityId() + e.getMessage(),
							e);
				}
			}
		} catch (DAOException e) {
			String error = "create facility detail from gateWay failed for facility submission for facility "
					+ gateWayFacility.getFacilityId() + ". " + e.getMessage();
			logger.error(e);
			throw new DAOException(error, e);
		} catch (Exception e) {
			String error = "create facility detail from gateWay failed for facility submission for facility "
					+ gateWayFacility.getFacilityId() + ". " + e.getMessage();
			logger.error(error, e);
			throw new DAOException(error, e);
		}
		logger.debug("createFacilityProfileFromGateWay Completed; facility = "
				+ gateWayFacility.getFacilityId() + " fpId = "
				+ newFacility.getFpId());
	}

	/**
	 * Clone a facility.
	 * 
	 * @param facility
	 *            Facility to create
	 * @return Facility new splitted facility inventory complete with all id's
	 *         for new contained objects
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */

	public Facility cloneFacility(Facility facility, String newFacilityName,
			int ownerCompanyId, Address phyAddr, int userId)
			throws DAOException {

		CopyOnChangeMaps copyOnChangeMaps;
		Integer newFpId = null;
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		ContactDAO contactDAO = contactDAO(trans);
		CompanyDAO companyDAO = companyDAO(trans);

		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		if (facility.getFpId() == null) {
			DAOException e = new DAOException("invalid fpId (null)");
			throw e;
		}

		try {
//			Facility facility = retrieveFacilityProfile(fpId);
			String facilityId = facility.getFacilityId();
			String facilityName = facility.getName();

			String newFacilityId = getNewFacilityId(trans);
			facility.setFacilityId(newFacilityId);
			facility.setName(newFacilityName);
			facility.setCorePlaceId(null);
			facility.setOwnerCompanyId(ownerCompanyId);
			facility.setLastSubmissionType("I");
			facility.setLastSubmissionVersion(0);
			facility.setStartDate(Utility.getToday());
			facility.setEndDateLong(null);
			facility.setFpId(null);

			if (phyAddr.getAddressLine1() == null) {
				String addr1 = "Section " + phyAddr.getSection() + ", "
						+ phyAddr.getTownship() + ", " + phyAddr.getRange();
				facility.getPhyAddr().setAddressLine1(addr1);
			} else {
				facility.getPhyAddr()
						.setAddressLine1(phyAddr.getAddressLine1());
			}
			facility.getPhyAddr().setAddressLine2(phyAddr.getAddressLine2());
			if (phyAddr.getCityName() == null) {
				String cityName = County.getData().getItems()
						.getItemDesc(phyAddr.getCountyCd())
						+ " County";
				facility.getPhyAddr().setCityName(cityName);
			} else {
				facility.getPhyAddr().setCityName(phyAddr.getCityName());
			}
			facility.getPhyAddr().setState(phyAddr.getState());
			facility.getPhyAddr().setZipCode(phyAddr.getZipCode());
			facility.getPhyAddr().setZipCode5(phyAddr.getZipCode5());
			facility.getPhyAddr().setZipCode4(phyAddr.getZipCode4());
			facility.getPhyAddr().setCountyCd(phyAddr.getCountyCd());
			facility.getPhyAddr().setDistrictCd(phyAddr.getDistrictCd());
			facility.getPhyAddr().setIndianReservationCd(phyAddr.getIndianReservationCd());
			facility.getPhyAddr().setBeginDate(phyAddr.getBeginDate());
			facility.getPhyAddr().setEndDate(phyAddr.getEndDate());
			facility.getPhyAddr().setLatitude(phyAddr.getLatitude());
			facility.getPhyAddr().setLongitude(phyAddr.getLongitude());
			facility.getPhyAddr().setLatlong(phyAddr.getLatlong());
			facility.getPhyAddr()
					.setQuarterQuarter(phyAddr.getQuarterQuarter());
			facility.getPhyAddr().setQuarter(phyAddr.getQuarter());
			facility.getPhyAddr().setSection(phyAddr.getSection());
			facility.getPhyAddr().setTownship(phyAddr.getTownship());
			facility.getPhyAddr().setRange(phyAddr.getRange());

			/*
			 * List<FacilityOwner> owners = facilityDAO
			 * .retrieveFacilityOwners(facilityId); for (FacilityOwner
			 * ownerHistory : owners) {
			 * ownerHistory.setFacilityId(newFacilityId); if
			 * (ownerHistory.getEndDate() == null)
			 * ownerHistory.setEndDate(Utility.getToday());
			 * 
			 * facilityDAO.addFacilityOwner(ownerHistory); }
			 */

			// clearing hydrocarbon analysis fields
			clearHydroCarbonAnalysisData(facility);
			facility.setAdministrativeHold(false);
			
			Company newOwnerCompany = companyDAO
					.retrieveCompany(ownerCompanyId);
			List<FacilityOwner> pastFacilityOwner = new ArrayList<FacilityOwner>();
			FacilityOwner newFacilityOwner = new FacilityOwner(
					Utility.getToday(), newOwnerCompany, newFacilityId, null);

			// selecting permits for EU use corrEpaEmuId. set it to null to have
			// a new one.
			for (EmissionUnit eu : facility.getEmissionUnits()) {
				// must set emission unit type to each EU
//				if (eu.getEmissionUnitTypeCd() != null
//						&& eu.getEmissionUnitTypeCd().length() > 0) {
//					EmissionUnitTypeDAO euTypeDAO = getEUTypeDAO(
//							eu.getEmissionUnitTypeCd(), trans, false);
//					if (euTypeDAO != null) {
//						eu.setEmissionUnitType(euTypeDAO
//								.retrieveEmissionUnitType(eu.getEmuId()));
//					}
//				}	
				EmissionUnitType euType = retrieveEmissionUnitType(eu,false);
				if (euType != null){
					euType.setEmissionUnitTypeCd(eu.getEmissionUnitTypeCd());
				}
				eu.setEmissionUnitType(euType);

				eu.setCorrEpaEmuId(null);
			}

			copyOnChangeMaps = createCloneFacilityProfile(facility, trans,
					false);
			newFpId = copyOnChangeMaps.fpId;

			addNewFacilityOwner(pastFacilityOwner, newFacilityOwner,
					newFacilityOwner.getStartDate(), facilityDAO);

			/*
			 * for (Contact contact : facility.getAllContacts()) { for
			 * (ContactType contactType : contact.getContactTypes()) { if
			 * (contactType.getFacilityId().equals(facilityId)) {
			 * contactType.setFacilityId(newFacilityId);
			 * contactDAO.addContactType(contactType.getContactId(),
			 * contactType); } } }
			 */

			createRoles(facilityDAO, infraDAO, facility.getFacilityId(),
					phyAddr.getCountyCd());
			// createWebDavs(facility.getFacilityId());

			internalFacilitySubmit1(facility, "SF" + facility.getFacilityId());

			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return retrieveFacility(facilityDAO, newFpId);
	}

	/**
	 * @param facility
	 *            Setting hydrocarbon analysis fields to null - not to be
	 *            copied over when cloning or splitting the facility
	 */
	private void clearHydroCarbonAnalysisData(Facility facility) {
		facility.setHydrocarbonPollutantList(null);
		facility.setAqdEmissionFactorGroupCd(null);
		facility.setHydrocarbonAnalysisSampleDetail(null);
		facility.setDecaneProperties(null);
	}

	/**
	 * Split a facility.
	 * 
	 * @param facility
	 *            Facility to create
	 * @return Facility new splitted facility detail complete with all id's for
	 *         new contained objects
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	@Override
	public Facility splitFacility(Integer fpId, String newFacilityName,
			Integer ownerCompanyId, int userId) throws DAOException {
		
		CopyOnChangeMaps copyOnChangeMaps;
		Integer newFpId = null;

		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		ContactDAO contactDAO = contactDAO(trans);
		CompanyDAO companyDAO = companyDAO(trans);

		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		if (fpId == null) {
			DAOException e = new DAOException("invalid fpId (null)");
			throw e;
		}

		try {
			Facility facility = retrieveFacilityProfile(fpId);
			String facilityId = facility.getFacilityId();
			String facilityName = facility.getName();

			String newFacilityId = getNewFacilityId(trans);
			facility.setFacilityId(newFacilityId);
			facility.setName(newFacilityName);
			facility.setCorePlaceId(null);
			facility.setOwnerCompanyId(ownerCompanyId);
			facility.setLastSubmissionType("I");
			facility.setLastSubmissionVersion(0);
			facility.setStartDate(Utility.getToday());
			facility.setEndDateLong(null);
			facility.setFpId(null);

			// clearing hydrocarbon analysis fields
			clearHydroCarbonAnalysisData(facility);
			facility.setAdministrativeHold(false);
			// selecting permits for EU use corrEpaEmuId. set it to null to have
			// a new one.
			for (EmissionUnit eu : facility.getEmissionUnits()) {
				// must set emission unit type to each EU
				if (eu.getEmissionUnitTypeCd() != null
						&& eu.getEmissionUnitTypeCd().length() > 0) {
					EmissionUnitTypeDAO euTypeDAO = getEUTypeDAO(
							eu.getEmissionUnitTypeCd(), trans, false);
					if (euTypeDAO != null) {
/*						eu.setEmissionUnitType(euTypeDAO
								.retrieveEmissionUnitType(eu.getEmuId()));*/
						EmissionUnitType euType = retrieveEmissionUnitType(eu.getEmuId(), euTypeDAO);
						eu.setEmissionUnitType(euType);
					};
				}

				eu.setCorrEpaEmuId(null);
			}
			
			// set correlated ids of continuous monitors and limits to null
			// so that new correlated ids are generated in the new facility
			for(ContinuousMonitor cm : facility.getContinuousMonitorList()) {
				cm.setCorrMonitorId(null);
				
				for (FacilityCemComLimit limit : cm.getFacilityCemComLimitList()) {
					limit.setCorrLimitId(null);
				}
			}

			copyOnChangeMaps = createFacilityProfile(facility, trans, false);
			newFpId = copyOnChangeMaps.fpId;

			List<FacilityOwner> owners = facilityDAO
					.retrieveFacilityOwners(facilityId);
			for (FacilityOwner ownerHistory : owners) {
				ownerHistory.setFacilityId(newFacilityId);
				if (ownerHistory.getEndDate() == null)
					ownerHistory.setEndDate(Utility.getToday());

				facilityDAO.addFacilityOwner(ownerHistory);
			}

			Company newOwnerCompany = companyDAO
					.retrieveCompany(ownerCompanyId);
			List<FacilityOwner> pastFacilityOwner = new ArrayList<FacilityOwner>();
			FacilityOwner newFacilityOwner = new FacilityOwner(
					Utility.getToday(), newOwnerCompany, newFacilityId, null);

			addNewFacilityOwner(pastFacilityOwner, newFacilityOwner,
					newFacilityOwner.getStartDate(), facilityDAO);

			for (Contact contact : facility.getAllContacts()) {
				for (ContactType contactType : contact.getContactTypes()) {
					if (contactType.getFacilityId().equals(facilityId)) {
						contactType.setFacilityId(newFacilityId);
						contactDAO.addContactType(contactType.getContactId(),
								contactType);
					}
				}
			}

			PermitDAO permitDAO = permitDAO(trans);
			ArrayList<Permit> activePermits = permitDAO
					.searchActivePermitsForFacility(facilityId);

			String note = "This facility is the outcome of the Facility Split operation on facility: "
					+ facilityName + " [" + facilityId + "] ";
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");

			if (activePermits != null && !activePermits.isEmpty()) {
				note += "The active permits [effective date, permit No., type] at the split time of facility ["
						+ facilityId + "] are:\n";
				String permitNo;
				String permitType;
				String effectDate;
				String permitInfo;

				for (Permit permit : activePermits) {
					permitNo = permit.getPermitNumber();
					permitType = PermitTypeDef.getData().getItems()
							.getItemDesc(permit.getPermitType());
					effectDate = permit.getEffectiveDate() == null ? "N/A"
							: dateFormat1.format(permit.getEffectiveDate());
					permitInfo = "[" + effectDate + "  " + permitNo + "  "
							+ permitType + "]\n";
					if ((note.length() + permitInfo.length()) > 3800) {
						note += " More ...";
						break;
					}

					note += permitInfo;
				}
			}

			ApplicationDAO applDAO = applicationDAO(trans);
			Application[] applications;

			applications = applDAO.searchApplications(null, facilityId, null,
					null, null, null);

			if (applications != null && applications.length > 0) {
				String applNo;
				String applType;
				String receivedDate;
				String applInfo;
				String permitInfo;
				String[] permitNums;
				Permit permit;

				note += "The applications [Application No., received date, type, (Permit Numbers)] at the split time of facility ["
						+ facilityId + "] are:\n";

				for (Application appl : applications) {
					permitNums = applDAO.retrieveApplicationPermitNumbers(appl
							.getApplicationID());
					applNo = appl.getApplicationNumber();
					applType = ApplicationTypeDef.getData().getItems()
							.getItemDesc(appl.getApplicationTypeCD());
					receivedDate = appl.getReceivedDate() == null ? "N/A"
							: dateFormat1.format(appl.getReceivedDate());
					applInfo = "[" + applNo + "  " + receivedDate + "  "
							+ applType + "]\n";
					if (permitNums.length > 0) {
						permitInfo = "(";
						for (String temp : permitNums) {
							permit = permitDAO
									.retrievePermit(new Integer(temp));
							permitInfo += " " + permit.getPermitNumber();
						}
						permitInfo += " )";
						applInfo = "[" + applNo + "  " + receivedDate + "  "
								+ applType + permitInfo + "]\n";
					} else {
						applInfo = "[" + applNo + "  " + receivedDate + "  "
								+ applType + "]\n";
					}
					if ((note.length() + applInfo.length()) > 3999) {
						note += " More ...";
						break;
					}

					note += applInfo;
				}
			}

			if (!note.equals("")) {
				FacilityNote facNote = new FacilityNote();
				facNote.setNoteTxt(note);
				facNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
				facNote.setUserId(userId);
				facNote.setNoteTypeCd(NoteType.DAPC);
				facNote.setFpId(newFpId);
				facNote.setFacilityId(newFacilityId);
				createFacilityNote(facNote, trans);
			}

			createRoles(facilityDAO, infraDAO, facility.getFacilityId(),
					facility.getPhyAddr().getCountyCd());
			createWebDavs(facility.getFacilityId());

			internalFacilitySubmit1(facility, "SF" + facility.getFacilityId());

			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return retrieveFacility(facilityDAO, newFpId);
	}

	/**
	 * Create a new facility detail. This method is only intended to be used by
	 * the Stars to Stars2 migration utility, since normally a "new" facility
	 * will be migrated from core_lite, i.e. facilities are not
	 * originated/created in Stars2.
	 * 
	 * @param Facility
	 *            Facility inventory to create
	 * @return Facility new facility detail complete with all id's for new
	 *         contained objects
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Facility createFacilityProfile(Facility newFacility, int userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		Facility ret = null;

		try {
			ret = createFacilityProfile(newFacility, userId, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * Create a new facility detail. This method is only intended to be used by
	 * the Stars to Stars2 migration utility, since normally a "new" facility
	 * will be migrated from core_lite, i.e. facilities are not
	 * originated/created in Stars2.
	 * 
	 * @param Facility
	 *            Facility inventory to create
	 * @return Facility new facility detail complete with all id's for new
	 *         contained objects
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Facility createFacilityProfile(Facility newFacility, int userId,
			Transaction trans) throws DAOException {
		Integer fpId = null;
		CopyOnChangeMaps copyOnChangeMaps;

		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			// set Addresses
			if ((newFacility.getAddresses() == null)
					|| (newFacility.getAddresses().toArray().length == 0)) {
				ArrayList<Address> addr = new ArrayList<Address>();
				addr.add(newFacility.getPhyAddr());
				newFacility.setAddresses(addr);
			}

			newFacility.setLastSubmissionType("I");
			newFacility.setLastSubmissionVersion(0);
			newFacility.setFpId(null);

			copyOnChangeMaps = createFacilityProfile(newFacility, trans, false);
			fpId = copyOnChangeMaps.fpId;

			if (newFacility.getVersionId().equals(new Integer(-1))) {
				InfrastructureDAO infraDAO = infrastructureDAO(trans);
				createRoles(facilityDAO, infraDAO, newFacility.getFacilityId(),
						newFacility.getPhyAddr().getCountyCd());
				createWebDavs(newFacility.getFacilityId());
			}
		} catch (DAOException e) {
			throw e;
		}

		return retrieveFacility(facilityDAO, fpId);
	}

	private void createContactChangeToDoLog(Facility facility,
			List<Contact> dapcContacts, List<Contact> gwContacts,
			String contactType, Transaction trans) throws DAOException {
		logger.trace("DLTRACE --> createContactChangeToDoLog");
		boolean found;
		boolean createToDo = false;
		Contact dapcContact = null;

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("Task Name", "Portal Facility Contact Change");
		String notes = null;
		String text = null;

		Integer roleUserId = facility
				.getFacilityRoles()
				.get(us.oh.state.epa.stars2.def.FacilityRoleDef.FACILITY_PROFILE_ADMIN)
				.getUserId();

		ContactType gwCurrContact;
		ContactType dapcCurrContact;

		for (Contact gwContact : gwContacts) {
			text = null;
			found = false;
			createToDo = false;
			for (Contact contact : dapcContacts) {
				if (gwContact.getContactId().equals(contact.getContactId())) {
					dapcContact = contact;
					found = true;
					break;
				}
			}
			gwCurrContact = gwContact.getCurrentContactType(contactType);
			if (found) {
				dapcCurrContact = dapcContact
						.getCurrentContactType(contactType);
				if (gwCurrContact != null) {
					if (dapcCurrContact == null) {
						
						text = "] is a new " 
								+ ContactTypeDef.getData().getItems().getDescFromAllItem(contactType) 
								+ " for facility as of: [";
						
						notes = "[" + gwContact.getName() + text
								+ gwCurrContact.getStartDate() + "]";

						createToDo = true;
					} else {
						if (!gwCurrContact.getStartDate().equals(
								dapcCurrContact.getStartDate())) {
							
							text = "Start date of " 
									+ ContactTypeDef.getData().getItems().getDescFromAllItem(contactType) 
									+ " [";
							
							notes = text + gwContact.getName()
									+ "] changed from: ["
									+ dapcCurrContact.getStartDate()
									+ "] to: [" + gwCurrContact.getStartDate()
									+ "]";

							createToDo = true;
						}
					}
				} else {
					if (dapcCurrContact != null) {
					
						text = "] is no longer the " 
								+ ContactTypeDef.getData().getItems().getDescFromAllItem(contactType) 
								+ " of this facility";
						
						notes = "[" + dapcContact.getName() + text;

						createToDo = true;
					} else {
						for (ContactType gwType : gwContact.getContactTypes()) {
							if (gwType.getContactTypeCd().equals(contactType)) {
								boolean matchFound = false;
								for (ContactType dapcType : dapcContact
										.getContactTypes()) {
									if (dapcType.getContactTypeCd().equals(
											contactType)
											&& (gwType.getStartDate().equals(
													dapcType.getStartDate()) && gwType
													.getEndDate()
													.equals(dapcType
															.getEndDate()))) {
										matchFound = true;
										break;
									}
								}
								
								// if matchFound is false it means that the existing contact dates
								// have been modified. So create a contact change task
								if (!matchFound) {
									createToDo = true;
								}
							}
							// if a Portal user adds an owner and then
							// terminates
							// the owner before submitting,
							// we do not need to generate to DO list.
						}
						if (createToDo) {
							
							text = "Start or End Date of " 
									+ ContactTypeDef.getData().getItems().getDescFromAllItem(contactType)
									+ " [";
									
							notes = text + dapcContact.getName()
									+ "] is changed";
						}
					}
				}
			} else {
				if (gwCurrContact != null) {
				
					text = "] is a new " 
							+ ContactTypeDef.getData().getItems().getDescFromAllItem(contactType) 
							+ " for facility as of: [";
					
					notes = "[" + gwContact.getName() + text
							+ gwCurrContact.getStartDate() + "]";

					createToDo = true;
				}

				// if a Portal user adds an owner and then terminates the owner
				// before submitting,
				// we do not need to generate to DO list.
			}
			if (createToDo) {
				logger.debug("Portal Facility Contact Change toDO for facility : "
						+ facility.getFacilityId() + " NOTE = " + notes);
				data.put("Notes", notes);
				createToDoEntry(facility.getFpId(), data, "N", new Timestamp(
						System.currentTimeMillis()), null, roleUserId, trans,
						CommonConst.GATEWAY_USER_ID);
			}
		}
	}

	private List<Contact> removeDuplicateContacts(Facility facility,
			List<Contact> gwContacts, Transaction trans) throws DAOException {
		logger.trace("DLTRACE --> removeDuplicateContacts");
		List<Contact> origContactList = gwContacts; // keep track of original
													// list in case we need to
													// restore
		List<Contact> cleanContactList = new ArrayList<Contact>();
		HashMap<String, Contact> contactMap = new HashMap<String, Contact>();
		FacilityDAO facilityDAO = facilityDAO(trans);
		// InfrastructureHelper infraHelper = new InfrastructureHelper();
		try {
			// put gateway contacts into a HashMap to get rid of duplicates
			for (Contact gwContact : gwContacts) {
				String key = (gwContact.getLastNm() == null ? "" : gwContact
						.getLastNm().toLowerCase())
						+ (gwContact.getFirstNm() == null ? "" : gwContact
								.getFirstNm().toLowerCase())
						+ (gwContact.getMiddleNm() == null ? "" : gwContact
								.getMiddleNm().toLowerCase())
						+ (gwContact.getEmailAddressTxt() == null ? "" : gwContact
								.getEmailAddressTxt().toLowerCase())
						+ (gwContact.getCompanyId() == null ? null
								: gwContact.getCompanyId());
				if (contactMap.get(key) == null) {
					contactMap.put(key, gwContact);
					cleanContactList.add(gwContact);
				} else {
					// handle duplicate
					boolean okToDeleteDup = true;
					Contact prevContact = contactMap.get(key);
					if (prevContact.getContactTypes().size() == 0
							&& gwContact.getContactTypes().size() > 0) {
						Contact tmp = prevContact;
						prevContact = gwContact;
						gwContact = tmp;
						loggerDebug("XXX duplicate contact "
								+ gwContact.getContactId()
								+ "has no contact type info and will be deleted");
					} else if (prevContact.getContactTypes().size() > 0
							&& gwContact.getContactTypes().size() == 0) {
						loggerDebug("XXX duplicate contact "
								+ gwContact.getContactId()
								+ "has no contact type info and will be deleted");
					} else {
						for (ContactType prevContactType : prevContact
								.getContactTypes()) {
							ContactType gwContactType = null;
							for (ContactType ct : gwContact.getContactTypes()) {
								if (ct.getContactTypeCd().equals(
										prevContactType.getContactTypeCd())) {
									gwContactType = ct;
									break;
								}
							}
							if (gwContactType != null) {
								if (gwContactType.getStartDate() == null
										|| prevContactType.getStartDate() == null
										|| !gwContactType.getStartDate()
												.equals(prevContactType
														.getStartDate())) {
									if ((gwContactType.getEndDate() == null && gwContactType
											.getEndDate() != null)
											|| (gwContactType.getEndDate() != null && gwContactType
													.getEndDate() == null)
											|| (gwContactType.getEndDate() != null
													&& gwContactType
															.getEndDate() != null && !gwContactType
													.getEndDate()
													.equals(gwContactType
															.getEndDate()))) {
										// contact types are different, need to
										// keep contact data
										okToDeleteDup = false;
									}
								}
							} else {
								okToDeleteDup = false;
							}
						}
					}
					if (okToDeleteDup) {
						loggerDebug("XXX Removing Dup Contact ID = "
								+ gwContact.getContactId());
						infrastructureHelper.deleteContact(gwContact, trans);
					} else {
						cleanContactList.add(gwContact);
					}
				}
			}
		} catch (DAOException dao) {
			// revert to original list if cleanup failed
			cleanContactList = origContactList;
			logger.error("removeDuplicateContacts failed for facility ID : "
					+ facility.getFacilityId(), dao);
		}
		return cleanContactList;
	}

	/**
	 * Update the facility contact as result of gateway submit.
	 * 
	 * @param Facility
	 *            Id Facility ID
	 * @param contacts
	 *            contacts
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */

	public void updateFacilityContactFromGateWay(String facilityId,
			Integer corePlaceId, List<Contact> contacts, Transaction trans)
			throws DAOException {
		logger.trace("DLTRACE --> updateFacilityContactFromGateWay");
		ContactDAO contactDAO = contactDAO(trans);
		FacilityDAO facilityDAO = facilityDAO(trans);

		List<Contact> currentContacts;
		try {
			// TODO: remove references of core place id
			// Facility facility = facilityDAO
			// .retrieveFacilityByCorePlaceId(corePlaceId);

			Facility facility = facilityDAO
					.retrieveFacilityByFacilityId(facilityId);
			// The following line is a STARS2 vestige.  It causes submission to fail when
			// trying to remove duplicate contact that is referenced by permit application.
			// For IMPACT, the best thing to do is skip trying to remove duplicates during
			// submission.  Instead, duplicates can be removed by AQD via the contact merge
			// capability.
			//contacts = removeDuplicateContacts(facility, contacts, trans);
			currentContacts = facilityDAO.retrieveFacilityContacts(facility
					.getFacilityId());
			
			for (SelectItem si : ContactTypeDef.getData().getItems().getAllItems()) {
				
				String contactTypeCd = (String) si.getValue();
				
				if (!Utility.isNullOrEmpty(contactTypeCd)) {
										
					createContactChangeToDoLog(facility, currentContacts, contacts, contactTypeCd, trans);							
				}
			}
			
			boolean found;

			logger.debug("Updating contact information...");

			// When this is done does not mater because the new contacts in
			// internal are not seen by
			// any of the other code in the routine.
			// Determine whether Billing(Primary) are assigned (end date null)
			// internally to a
			// contact that is not also in the gateway and the gateway also has
			// Billing(Primary) assigned.
			// If so, delete the internal one.
			// boolean newFound = false;
			// ContactType internalType = Contact.getCurrentContactType(
			// currentContacts, ContactTypeDef.PRIM);
			// ContactType gateWayType = null;
			// if (internalType != null) {
			// newFound = Contact.idBelongsTo(contacts,
			// internalType.getContactId());
			// }
			// if (!newFound) {
			// gateWayType = Contact.getCurrentContactType(contacts,
			// ContactTypeDef.PRIM);
			// if (gateWayType != null && internalType != null) {
			// infraDAO.deleteContactType(internalType);
			// }
			// }
			// String nullV = "null";
			// logger.error("DENNIS Debugging PRIM:  "
			// + (internalType != null ? internalType.getContactId()
			// .toString() : nullV)
			// + ", newFound="
			// + newFound
			// + ", "
			// + (gateWayType != null ? gateWayType.getContactId()
			// .toString() : nullV));
			//
			// internalType = Contact.getCurrentContactType(currentContacts,
			// ContactTypeDef.BILL);
			// gateWayType = null;
			// newFound = false;
			// if (internalType != null) {
			// newFound = Contact.idBelongsTo(contacts,
			// internalType.getContactId());
			// }
			// if (!newFound) {
			// gateWayType = Contact.getCurrentContactType(contacts,
			// ContactTypeDef.BILL);
			// if (gateWayType != null && internalType != null) {
			// infraDAO.deleteContactType(internalType);
			// }
			// }
			// logger.error("DENNIS Debugging BILL:  "
			// + (internalType != null ? internalType.getContactId()
			// .toString() : nullV)
			// + ", newFound="
			// + newFound
			// + ", "
			// + (gateWayType != null ? gateWayType.getContactId()
			// .toString() : nullV));

			for (Contact gwContact : contacts) {
				logger.debug("-- GW contact id = " + gwContact.getContactId());
				found = false;
				/*
				 * If you find the same contact, then replace the types with
				 * those from gateway. If you don't find the same contact, then
				 * add the contact. Recall that contacts are never deleted, just
				 * contact types so if the contact did exist, it will continue
				 * to exist--even when no types assigned to it.
				 * 
				 * We need to see if the internal and gateway have different
				 * contacts assigned as current for a specific type (that allows
				 * only one). If so, remove the type from the internal one.
				 * DENNIS
				 */
				for (Contact contact : currentContacts) {
					if (gwContact.getContactId().equals(contact.getContactId())) {
						logger.debug("--- Match found for GW contact id = "
								+ gwContact.getContactId());
						// Remove all the contact types
						contactDAO.removeContactTypes(contact.getContactId(),
								facilityId);
						gwContact.setLastModified(contact.getLastModified());
						// Update the address of the contact to that from
						// gateway
						gwContact.getAddress().setAddressId(
								contact.getAddress().getAddressId());
						gwContact.setAddressId(contact.getAddress()
								.getAddressId());
						gwContact.getAddress().setLastModified(
								contact.getAddress().getLastModified());

						// retain previous portal username
						gwContact
								.setExternalUser(contact.getExternalUser());

						found = true;
						break;
					}
				}
				if (!found) {

					// InfrastructureHelper infraHelper = new
					// InfrastructureHelper();
					Contact[] existingContacts = infrastructureHelper
							.retrieveContacts(gwContact, trans);
					if (existingContacts.length == 1) {
						found = true;
						gwContact.setLastModified(existingContacts[0]
								.getLastModified());
						// Update the address of the contact to that from
						// gateway
						gwContact.getAddress()
								.setAddressId(
										existingContacts[0].getAddress()
												.getAddressId());
						gwContact.setAddressId(existingContacts[0].getAddress()
								.getAddressId());
						gwContact.getAddress().setLastModified(
								existingContacts[0].getAddress()
										.getLastModified());

					}
				}
				String s = "";

				// InfrastructureHelper infraHelper = new
				// InfrastructureHelper();
				if (found) {
					s = "--- modifying data for GW contact id = "
							+ gwContact.getContactId();
					logger.debug(s);

					// Update contact data and address
					infrastructureHelper.modifyContactData(gwContact, trans);
					// Add all the contact types that were in gateway
					for (ContactType cType : gwContact.getContactTypes()) {
						contactDAO.addContactType(gwContact.getContactId(),
								cType);
					}
				} else {
					s = "--- creating new record for GW contact id = "
							+ gwContact.getContactId();
					logger.debug(s);
					gwContact.getAddress().setAddressId(null);
					infrastructureHelper.createContact(gwContact, trans);
				}

				// if (!facilityId.equals(facility.getFacilityId())) {
				// HashMap<String, String> data = new HashMap<String, String>();
				// data.put("Task Name",
				// "Facility Contact Submit with ID change");
				// String notes =
				// "A facility contact change is submitted by a Portal user that has different Facility ID ["
				// + facilityId + "].";
				// data.put("Notes", notes);
				// Integer roleUserId = facility.getFacilityRoles().
				// get(us.oh.state.epa.stars2.def.FacilityRoleDef.FACILITY_PROFILE_ADMIN).getUserId();
				// createToDoEntry(facility.getFpId(), data, "N",
				// new Timestamp(System.currentTimeMillis()), null,
				// roleUserId, trans, CommonConst.GATEWAY_USER_ID);
				// }
			}

		} catch (Exception ex) {
			DAOException daoe = new DAOException(
					"Could not use InfrastructureService for facilityId="
							+ facilityId, ex);
			throw daoe;
		}
	}

	public void migrationWriteEmissionProcess(EmissionProcess emissionProcess,
			Transaction trans) throws DAOException {
		try {
			EmissionProcess eP = createEmissionProcess(emissionProcess, trans);
			emissionProcess.setProcessId(eP.getProcessId());
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		}
	}

	/**
	 * This method is used to mark current facility as history. An update will
	 * cause copy this version.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void markProfileHistory(Integer fpId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		Facility facility;

		if (fpId != null) {
			try {
				facility = retrieveFacility(facilityDAO, fpId);
				facility.setCopyOnChange(true);

				facilityDAO.modifyFacility(facility);
				trans.complete();
			} catch (DAOException e) {
				cancelTransaction("fpId=" + fpId, trans, e);
			} finally { // Clean up our transaction stuff
				closeTransaction(trans);
			}
		}
	}

	/**
	 * This method is used to split an existing facility and increase the
	 * version number for all version greater than new one.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void splitFacilityProfile(Integer fpId, Timestamp revisedDate,
			FacilityNote historyNote, int userId) throws DAOException {
		Transaction trans = null;
		if (fpId != null) {
			try {
				trans = TransactionFactory.createTransaction();
				splitFacilityProfile(fpId, revisedDate, historyNote, userId,
						trans);
				trans.complete();

			} catch (DAOException e) {
				cancelTransaction("fpId=" + fpId, trans, e);
			} finally { // Clean up our transaction stuff
				closeTransaction(trans);
			}
		}

		return;
	}

	/**
	 * This internal method is used to split an existing facility and increase
	 * the version number for all version greater than new one.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	/**
	 * @param fpId
	 * @param revisedDate
	 *            *
	 * @param historyNote
	 * @param trans
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public CopyOnChangeMaps splitFacilityProfile(Integer fpId,
			Timestamp revisedDate, FacilityNote historyNote, int userId,
			Transaction trans) throws DAOException {
		boolean increaseVersions = true;
		if (fpId != null) {
			FacilityDAO facilityDAO = facilityDAO(trans);

			Facility splitFacility = retrieveFacilityProfile(fpId, true);

			Timestamp splitEndDate = splitFacility.getEndDate();
			Integer splitVersionId = splitFacility.getVersionId();
			Integer newVersionId;

			if (splitVersionId.equals(splitFacility.getMaxVersion())) {
				increaseVersions = false;
			}

			if (splitVersionId == -1) {
				// spliting the current vesrion of facility
				if (splitVersionId.equals(splitFacility.getMaxVersion())) {
					// only one version of facility
					splitVersionId = 0;
				} else {
					splitVersionId = splitFacility.getMaxVersion() + 1;
				}
				increaseVersions = false;
				newVersionId = -1;
			} else {
				newVersionId = splitVersionId + 1;
			}

			if (increaseVersions) {
				facilityDAO.increaseVersionIds(splitFacility.getFacilityId(),
						splitVersionId);
			}

			splitFacility.setVersionId(splitVersionId);
			splitFacility.setEndDate(revisedDate);
			splitFacility.setCopyOnChange(false);

			facilityDAO.modifyFacility(splitFacility);

			// Make a new "current" version of this facility...
			splitFacility.setStartDate(revisedDate);
			splitFacility.setEndDate(splitEndDate);
			splitFacility.setVersionId(newVersionId);
			splitFacility.setFpId(null);
			if (newVersionId != -1) {
				splitFacility.setCopyOnChange(true);
			}

			CopyOnChangeMaps copyOnChangeMaps = createFacilityProfile(
					splitFacility, trans, false);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				historyNote.setDateEntered(new Timestamp(System
						.currentTimeMillis()));
				historyNote.setUserId(userId);
				historyNote.setNoteTypeCd(NoteType.HIST);
				historyNote.setFpId(splitFacility.getFpId());
				historyNote.setFacilityId(splitFacility.getFacilityId());
				createFacilityNote(historyNote, trans);
			}
			return copyOnChangeMaps;
		}
		return null;
	}

	private Facility retrieveFacility(FacilityDAO facilityDAO, Integer fpId)
			throws DAOException {
		return facilityDAO.retrieveFacility(fpId);
	}

	/**
	 * This method is used to retrieve a facility after creating a new version
	 * if thefacility's "copy on change flag" is set and a change to the
	 * facility requires a "versioning" of the facility.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Facility retrieveFacilityEditable(Integer fpId, int userId)
			throws DAOException {
		Facility ret;
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			ret = copyFacilityProfile(fpId,
					new Timestamp(System.currentTimeMillis()), userId);
		} else {
			ret = retrieveFacilityProfile(fpId, true);
		}

		return ret;
	}

	public Facility retrieveFacilityEditable(Integer fpId, int userId,
			Transaction trans) throws DAOException {
		Facility ret;
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			ret = copyFacilityProfile(fpId,
					new Timestamp(System.currentTimeMillis()), userId, trans);
		} else {
			ret = retrieveFacilityProfile(fpId, true);
		}

		return ret;
	}

	/**
	 * This method is used to copy an existing facility to a new one. This will
	 * be used when a facility's "copy on change flag" is set and a change to
	 * the facility requires a "versioning" of the facility.
	 * 
	 * @param fpId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Facility copyFacilityProfile(Integer fpId, Timestamp dateOfChange,
			int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		Facility ret = null;
		CopyOnChangeMaps copyOnChangeMaps;

		if (fpId != null) {
			try {

				Facility currentFacility;
				if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
					currentFacility = retrieveFacilityProfile(fpId, true);
				} else {
					currentFacility = getFacilityDAO().retrieveFacilityData(
							fpId);
					return currentFacility;
				}

				// If the current facility isn't set to "copy on change" then do
				// nothing.
				if (currentFacility.isCopyOnChange()
						&& currentFacility.getVersionId() == -1) {

					currentFacility.setCopyOnChange(false);

					// "version" the current facility...
					currentFacility.setVersionId(currentFacility
							.getMaxVersion() + 1);

					currentFacility.setEndDate(dateOfChange);

					facilityDAO.modifyFacility(currentFacility);

					// Make a new "current" version of this facility...
					currentFacility.setStartDate(dateOfChange);
					currentFacility.setEndDate(null);
					currentFacility.setVersionId(-1);
					currentFacility.setFpId(null);

					copyOnChangeMaps = createFacilityProfile(currentFacility,
							trans, false);
					fpId = copyOnChangeMaps.fpId;

					trans.complete();
					facilityDAO.setTransaction(null);

					ret = retrieveFacility(facilityDAO, fpId);
					ret.setCopyOnChangeFpNodeIds(copyOnChangeMaps.fpNodeIds);
					ret.setCopyOnChangeEuIds(copyOnChangeMaps.euIds);

				} else {
					ret = currentFacility;
				}
			} catch (DAOException e) {
				cancelTransaction("fpId=" + fpId, trans, e);
			} finally { // Clean up our transaction stuff
				closeTransaction(trans);
			}
		}

		return ret;
	}

	/**
	 * This method is used to copy an existing facility to a new one. This will
	 * be used when a facility's "copy on change flag" is set and a change to
	 * the facility requires a "versioning" of the facility.
	 * 
	 * @param fpId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Facility copyFacilityProfile(Integer fpId, Timestamp dateOfChange,
			int userId, Transaction trans) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		Facility ret = null;
		CopyOnChangeMaps copyOnChangeMaps;

		if (fpId != null) {
			try {

				Facility currentFacility;
				if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
					currentFacility = retrieveFacilityProfile(fpId, true, trans);
				} else {
					currentFacility = getFacilityDAO().retrieveFacilityData(
							fpId);
					return currentFacility;
				}

				// If the current facility isn't set to "copy on change" then do
				// nothing.
				if (currentFacility.isCopyOnChange()
						&& currentFacility.getVersionId() == -1) {

					currentFacility.setCopyOnChange(false);

					// "version" the current facility...
					currentFacility.setVersionId(currentFacility
							.getMaxVersion() + 1);

					currentFacility.setEndDate(dateOfChange);

					facilityDAO.modifyFacility(currentFacility);

					// Make a new "current" version of this facility...
					currentFacility.setStartDate(dateOfChange);
					currentFacility.setEndDate(null);
					currentFacility.setVersionId(-1);
					currentFacility.setFpId(null);

					copyOnChangeMaps = createFacilityProfile(currentFacility,
							trans, false);
					fpId = copyOnChangeMaps.fpId;

					// facilityDAO.setTransaction(null);

					ret = retrieveFacility(facilityDAO, fpId);
					ret.setCopyOnChangeFpNodeIds(copyOnChangeMaps.fpNodeIds);
					ret.setCopyOnChangeEuIds(copyOnChangeMaps.euIds);

				} else {
					ret = currentFacility;
				}
			} catch (DAOException e) {
				cancelTransaction("fpId=" + fpId, trans, e);
			}
		}

		return ret;
	}

	/**
	 * This method is used to create a facility detail in the staging area
	 * 
	 * @param fpId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void createStagingFacilityProfile(Integer fpId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			// read it from readOnly profile
			Facility currentFacility = retrieveFacilityProfile(fpId, false);

			currentFacility.setLastSubmissionType("E");
			currentFacility.setCopyOnChange(false);
			createFacilityProfile(currentFacility, trans, true);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + fpId, trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * This method is used to create a facility detail in the staging area
	 * 
	 * @param fpId
	 * @return created fpId
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer createStagingFacilityProfile(Integer fpId, Transaction trans)
			throws DAOException {
		CopyOnChangeMaps copyOnChangeMaps;

		if (fpId == null) {
			DAOException e = new DAOException("invalid fpId (null)");
			throw e;
		}

		try {
			if (retrieveFacilityProfile(fpId, true) != null) {
				// facility already in staging area
				return fpId;
			}

			// read it from readOnly profile
			Facility currentFacility = retrieveFacilityProfile(fpId, false);

			currentFacility.setLastSubmissionType("E");
			currentFacility.setCopyOnChange(false);
			copyOnChangeMaps = createFacilityProfile(currentFacility, trans,
					true);

		} catch (DAOException e) {
			throw e;
		}

		return copyOnChangeMaps.fpId;
	}

	/**
	 * This method is used to create a facility contacts in the staging area
	 * 
	 * @param facilityId
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void createStagingFacilityContacts(String facilityId,
			Transaction trans) throws DAOException {
		try {
			List<Contact> allContacts = retrieveFacilityContacts(facilityId);
			for (Contact contact : allContacts) {
				contact.setFacilityId(facilityId);
				contact.getAddress().setAddressId(null);
				// InfrastructureHelper infraHelper = new
				// InfrastructureHelper();
				infrastructureHelper.createContact(contact, trans);
			}

		} catch (DAOException e) {
			throw e;
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Attachment createFacilityAttachment(Attachment facilityAttachment,
			InputStream fileStream) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			facilityAttachment = (Attachment) documentDAO().createDocument(
					facilityAttachment);
			facilityAttachment = facilityDAO
					.createFacilityAttachment(facilityAttachment);
			DocumentUtil.createDocument(facilityAttachment.getPath(),
					fileStream);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} catch (IOException ioe) {
			try {
				DocumentUtil.removeDocument(facilityAttachment.getPath());
			} catch (IOException ioex) {
			}
			cancelTransaction(trans, new RemoteException(ioe.getMessage(), ioe));
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return facilityAttachment;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Attachment updateFacilityAttachment(Attachment facilityAttachment)
			throws DAOException {
		Attachment ret = null;
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		DocumentDAO documentDAO = documentDAO(trans);
		try {
			facilityAttachment.setLastModifiedTS(new Timestamp(System
						.currentTimeMillis()));
			if(facilityAttachment.getUploadDate() == null) {
				facilityAttachment.setUploadDate(facilityAttachment
					.getLastModifiedTS());
			}
			documentDAO.modifyDocument(facilityAttachment);
			ret = facilityDAO.updateFacilityAttachment(facilityAttachment);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeFacilityAttachment(Attachment facilityAttachment)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			facilityDAO.removeFacilityAttachment(facilityAttachment);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public FacilityNote createFacilityNote(FacilityNote facilityNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityNote ret = null;

		try {
			ret = createFacilityNote(facilityNote, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public FacilityRUM createFacilityRUM(FacilityRUM facilityRUM)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityRUM ret = null;

		try {
			ret = createFacilityRUM(facilityRUM, trans);

			Integer workflowId = null;

			Timestamp dueDt = null;
			String rush = "N";

			FacilityDAO fd = facilityDAO();
			Facility fp = fd.retrieveFacility(facilityRUM.getFacilityId());

		} catch (RemoteException re) {
			logger.error("create facility RUM failed for facility "
					+ facilityRUM.getFacilityId());
			cancelTransaction(trans, re);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param facilityNote
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private FacilityRUM createFacilityRUM(FacilityRUM facilityRUM,
			Transaction trans) throws DAOException {
		FacilityRUM ret = null;
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			FacilityRUM tempRUM = facilityDAO.createFacilityRUM(facilityRUM);

			if (tempRUM != null) {
				ret = facilityRUM;
			} else {
				logger.error("Failed to insert Facility RUM for "
						+ facilityRUM.getFacilityId());
				throw new DAOException("Failed to insert Facility RUM for "
						+ facilityRUM.getFacilityId());
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			logger.error(
					"Failed to insert Facility RUM for "
							+ facilityRUM.getFacilityId(), e);
			throw e;
		}
		return ret;
	}

	/**
	 * @param facilityNote
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private FacilityNote createFacilityNote(FacilityNote facilityNote,
			Transaction trans) throws DAOException {
		FacilityNote ret = null;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			Note tempNote = infraDAO.createNote(facilityNote);

			if (tempNote != null) {
				ret = facilityNote;
				ret.setNoteId(tempNote.getNoteId());

				facilityDAO.addFacilityNote(ret.getFpId(), ret.getFacilityId(),
						ret.getNoteId());
			} else {
				logger.error("Failed to insert Facility Note for "
						+ facilityNote.getFacilityId());
				throw new DAOException("Failed to insert Facility Note for "
						+ facilityNote.getFacilityId());
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyFacilityNote(FacilityNote facilityNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		boolean ret = false;

		try {
			ret = infraDAO.modifyNote(facilityNote);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyFacilityRUM(FacilityRUM facilityRUM)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		boolean ret = false;

		try {
			ret = facilityDAO.modifyRUM(facilityRUM);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Attachment[] retrieveFacilityAttachments(String facilityId) {
		Attachment[] ret = null;

		try {
			ret = facilityDAO().retrieveFacilityAttachments(facilityId);
		} catch (DAOException de) {
			logger.error("retrieve facility attachments failed for "
					+ facilityId + ". " + de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityNote[] retrieveFacilityNotes(String facilityId)
			throws DAOException {
		FacilityNote[] ret = null;

		try {
			ret = facilityDAO().retrieveFacilityNotes(facilityId);
		} catch (DAOException de) {
			logger.error("retrieve facility notes failed for " + facilityId
					+ ". " + de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityRUM[] retrieveFacilityRUMs(String facilityId)
			throws DAOException {
		FacilityRUM[] ret = null;

		try {
			ret = facilityDAO().retrieveFacilityRUMs(facilityId);
		} catch (DAOException de) {
			logger.error("retrieve facility RUMs failed for " + facilityId
					+ ". " + de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityRUM retrieveFacilityRUM(int rumId) throws DAOException {
		FacilityRUM ret = null;

		try {
			ret = facilityDAO().retrieveFacilityRUM(rumId);
		} catch (DAOException de) {
			logger.error("retrieve facility RUM failed for " + rumId + ". "
					+ de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * @param roles
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean updateFacilityRoles(FacilityRole roles[], Facility facility,
			int userId) throws DAOException {

		boolean ret = false;
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			facilityDAO.removeFacilityRoles(roles[0].getFacilityId());

			facilityDAO.createFacilityRoles(roles);

			FieldAuditLog[] auditLog = new FieldAuditLog[1];
			auditLog[0] = new FieldAuditLog("frol", facility.getFacilityId(),
					"NO VALUE", "Some of the facility roles updated");

			infraDAO.createFieldAuditLogs(facility.getFacilityId(),
					facility.getName(), userId, auditLog);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param facility
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean updateFacilityRoles(Facility facility) throws DAOException {
		boolean ret = false;
		Transaction trans = TransactionFactory.createTransaction();
		try {
			FacilityRoleDef[] facRoleDefs;
			FacilityRole facRole;
			ArrayList<FacilityRole> facRoles = new ArrayList<FacilityRole>();

			FacilityDAO facilityDAO = facilityDAO(trans);
			InfrastructureDAO infraDAO = infrastructureDAO(trans);
			facRoleDefs = infraDAO.retrieveDefaultFacilityRoles(facility
					.getPhyAddr().getCountyCd());

			if (facRoleDefs == null || facRoleDefs.length == 0) {
				throw new DAOException(
						"Default user roles is not defined for the county.");
			}

			for (FacilityRoleDef tempRole : facRoleDefs) {
				facRole = new FacilityRole(tempRole, facility.getFacilityId());
				facRoles.add(facRole);
			}

			facilityDAO.removeFacilityRoles(facility.getFacilityId());

			facilityDAO.createFacilityRoles(facRoles
					.toArray(new FacilityRole[0]));

		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param roles
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean updateBulkFacilityRoles(String facilityIds[],
			FacilityRole roles[]) throws DAOException {

		boolean ret = false;
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		if (roles.length > 0 && facilityIds.length > 0) {
			try {
				for (String facilityId : facilityIds) {
					// if (roles.length != CommonConst.NUM_FACILITY_ROLES) {
					// throw new DAOException("Facility " + facilityId
					// +" Must have 16 roles, only received " + roles.length);
					// }
					for (FacilityRole role : roles) {
						role.setFacilityId(facilityId);
					}
					facilityDAO.removeFacilityRoles(facilityId);
					facilityDAO.createFacilityRoles(roles);
				}

				trans.complete();
			} catch (DAOException e) {
				cancelTransaction(trans, e);
			} finally { // Clean up our transaction stuff
				closeTransaction(trans);
			}
		}
		return ret;
	}

	/**
	 * This only retrieves Faciliy data and not any contained objects like
	 * Emission Units.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Facility retrieveFacilityData(String facilityId, Integer versionId)
			throws DAOException {
		Facility ret = null;
		FacilityDAO facilityDAO = facilityDAO();
		try {
			ret = facilityDAO.retrieveFacility(facilityId, versionId);
		} catch (DAOException de) {
			logger.error("retrieve facility data failed for " + facilityId
					+ ", versionId " + versionId + ". " + de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * This only retrieves Faciliy data and not any contained objects like
	 * Emission Units.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Facility retrieveFacilityByCorePlaceId(Integer corePlaceId)
			throws DAOException {
		Facility ret = null;
		FacilityDAO facilityDAO = facilityDAO();
		try {
			ret = facilityDAO.retrieveFacilityByCorePlaceId(corePlaceId);
		} catch (DAOException de) {
			logger.error("retrieve facility by core place ID failed or "
					+ corePlaceId + ". " + de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * This only retrieves Faciliy data and not any contained objects like
	 * Emission Units.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Facility retrieveFacilityByCorePlaceId(Integer corePlaceId,
			boolean staging) throws DAOException {
		Facility ret = null;
		FacilityDAO facilityDAO = getFacilityDAO(staging);
		try {
			ret = facilityDAO.retrieveFacilityByCorePlaceId(corePlaceId);
		} catch (DAOException de) {
			logger.error("retrieve facility by core place ID failed for "
					+ corePlaceId + ". " + de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * This only retrieves Faciliy data and not any contained objects like
	 * Emission Units.
	 * 
	 */
	public Facility retrieveFacilityByFacilityId(String facilityId,
			boolean staging) throws DAOException {
		Facility ret = null;
		FacilityDAO facilityDAO = getFacilityDAO(staging);
		try {
			ret = facilityDAO.retrieveFacilityByFacilityId(facilityId);
		} catch (DAOException de) {
			logger.error("retrieve facility by facility ID failed for "
					+ facilityId + ". " + de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Facility retrieveFacilityProfile(Integer fpId) throws DAOException {
		return retrieveFacilityProfile(fpId, false);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Facility retrieveFacilityProfile(Integer fpId, boolean staging)
			throws DAOException {
		return retrieveFacilityProfile(fpId, staging, null);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 * 
	 *                  staging only used when on portal side staging == true
	 *                  means use staging area staging == false means use
	 *                  readOnly database.
	 */
	public Facility retrieveFacilityProfile(Integer fpId, boolean staging,
			Transaction trans) throws DAOException {
		logger.debug("enter retrieveFacilityProfile" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
		Facility ret = null;
		FacilityDAO facilityDAO = getFacilityDAO(staging);
		ContinuousMonitorDAO continuousMonitorDAO = getContinuousMonitorDAO(staging);

		if (null != trans) {
			facilityDAO.setTransaction(trans);
			continuousMonitorDAO.setTransaction(trans);
		}

		if (fpId != null) {
			try {
				ret = retrieveFacility(facilityDAO, fpId);

				if (ret != null) {
					if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP) && staging) {
						// set contacts again in case of multiple facility versions
						ContactDAO contactDAO = getContactDAO(staging);
						ret.setAllContacts(contactDAO.retrieveStagedContactsByFacility(ret.getFacilityId()));
					}
					
					logger.debug("fpId = " + fpId);
					ret.setStaging(staging);
					List<FacilityNode> emissionProcesses = facilityDAO
							.retrieveFacilityEmissionProcesses(fpId);

					List<FacilityNode> controlEquips = facilityDAO
							.retrieveFacilityControlEquipment(fpId);

					List<FacilityNode> egressPoints = facilityDAO
							.retrieveFacilityEgressPoints(fpId);

					Iterator<FacilityNode> iter;

					iter = egressPoints.iterator();

					while (iter.hasNext()) {
						EgressPoint tempEGP = (EgressPoint) iter.next();
						ret.addEgressPoint(tempEGP);
						FacilityRelationship[] frList = tempEGP
								.getRelationships();
						if (frList.length == 0) {
							ret.addUnassignedEgrPoint(tempEGP);
						}
					}

					iter = controlEquips.iterator();
					while (iter.hasNext()) {
						ControlEquipment tempCE = (ControlEquipment) iter
								.next();
						ret.addControlEquip(tempCE);
						ret.addUnassignedCntEquip(tempCE);
					}

					/*
					 * Build relationship between ControlEquipment and
					 * ControlEquipment and EgressPoints
					 */

					iter = controlEquips.iterator();
					boolean homeFound = false;
					ControlEquipment ce;
					EgressPoint eg;
					float percent;
					boolean missingValues;

					while (iter.hasNext()) {
						percent = (float) 0.0;
						missingValues = false;
						ControlEquipment tempCE = (ControlEquipment) iter
								.next();
						FacilityRelationship[] frList = tempCE
								.getRelationships();

						for (FacilityRelationship tempFR : frList) {
							homeFound = false;
							ce = (ControlEquipment) FacilityNode.findFpNode(
									controlEquips, tempFR.getToNodeId());

							if (ce != null) {
								tempCE.addControlEquipment(ce);
								FacilityEmissionFlow emissionFlow = new FacilityEmissionFlow(
										FacilityEmissionFlow.CE_TYPE,
										ce.getControlEquipmentId(), tempFR);
								tempCE.addceEmissionFlow(emissionFlow);
								if (tempFR.getFlowFactor() > 0) {
									percent += tempFR.getFlowFactor();
								}
								if (tempFR.getFlowFactor() == MISSING_FLOW_PERCENT) {
									missingValues = true;
								}

								/*
								 * If an unassigned CE assigned to a control
								 * equipment, the CE is added to the unassigned
								 * hash map in the previous loop. This is
								 * because the relationship for CEs is based on
								 * fromFPNodeId. Remove the CE from unassigned
								 * CE list.
								 */
								ret.removeFromUnassignCntEquips(ce);

								homeFound = true;
							}

							eg = (EgressPoint) FacilityNode.findFpNode(
									egressPoints, tempFR.getToNodeId());
							if (eg != null) {
								tempCE.addEgressPoint(eg);
								if (!EgrPointTypeDef.isFugitive(eg)) {
									FacilityEmissionFlow emissionFlow = new FacilityEmissionFlow(
											FacilityEmissionFlow.STACK_TYPE,
											eg.getReleasePointId(), tempFR);
									tempCE.addceEmissionFlow(emissionFlow);
									if (tempFR.getFlowFactor() > 0f) {
										percent += tempFR.getFlowFactor();
									}
									if (tempFR.getFlowFactor() == MISSING_FLOW_PERCENT) {
										missingValues = true;
									}
								}
								homeFound = true;
							}

							if (!homeFound) {
								StringBuffer tempErrorMsg = new StringBuffer(
										"DB relationship problem, not a valid realtionship ");

								tempErrorMsg.append("FpId " + fpId.toString());
								tempErrorMsg.append(" FromNodeId "
										+ tempFR.getFromNodeId());
								tempErrorMsg.append(" ToNodeId "
										+ tempFR.getToNodeId());

								logger.error(tempErrorMsg.toString());
							}
						}

						if (tempCE.getCeEmissionFlows().size() > 0) {
							for (FacilityEmissionFlow emissionFlow : tempCE
									.getCeEmissionFlows()) {
								if (percent > (float) 0.0) {
									if (!missingValues) {
										if (emissionFlow.getRelationship()
												.getFlowFactor().floatValue() >= (float) 0.0) {
											Double percent1 = new Double(
													(emissionFlow
															.getRelationship()
															.getFlowFactor()
															.floatValue() / percent)
															* new Float(100.0));
											emissionFlow.setPercents(BaseDB
													.numberToxxx_xx(percent1),
													percent1.floatValue());
										} else {
											emissionFlow.setPercents(null, 0);
										}
									} else {
										if (emissionFlow.getRelationship()
												.getFlowFactor().floatValue() >= (float) 0.0) {
											Double percent2 = new Double(
													emissionFlow
															.getRelationship()
															.getFlowFactor()
															.floatValue());
											emissionFlow.setPercents(BaseDB
													.numberToxxx_xx(percent2),
													percent2.floatValue());
										} else {
											emissionFlow.setPercents(null, 0);
										}
									}
								} else if (emissionFlow.getRelationship()
										.getFlowFactor().floatValue() == (float) 0.0) {
									emissionFlow.setPercents("0", 0);
								} else {
									emissionFlow.setPercents(null, 0);
								}
								loggerDebug("CE = "
										+ tempCE.getControlEquipmentId()
										+ " Type = "
										+ emissionFlow.getType()
										+ " ID = "
										+ emissionFlow.getId()
										+ " Factor = "
										+ emissionFlow.getRelationship()
												.getFlowFactor()
										+ " Percent = "
										+ emissionFlow.getPercent());
							}
						}
					}

					/*
					 * Build relationship between EmissionProcesses and
					 * ControlEquipment and EgressPoints
					 */
					iter = emissionProcesses.iterator();
					// homeFound = false;

					while (iter.hasNext()) {
						percent = (float) 0.0;
						missingValues = false;
						EmissionProcess tempEP = (EmissionProcess) iter.next();

						FacilityRelationship[] frList = tempEP
								.getRelationships();

						for (FacilityRelationship tempFR : frList) {
							homeFound = false;

							ce = (ControlEquipment) FacilityNode.findFpNode(
									controlEquips, tempFR.getToNodeId());

							if (ce != null) {
								tempEP.addControlEquipment(ce);
								FacilityEmissionFlow emissionFlow = new FacilityEmissionFlow(
										FacilityEmissionFlow.CE_TYPE,
										ce.getControlEquipmentId(), tempFR);
								tempEP.addepEmissionFlow(emissionFlow);
								if (tempFR.getFlowFactor() > 0) {
									percent += tempFR.getFlowFactor();
								}
								if (tempFR.getFlowFactor() == MISSING_FLOW_PERCENT) {
									missingValues = true;
								}
								homeFound = true;
								/*
								 * If an unassigned CE assigned to a process,
								 * the CE is added to the unassigned hash map in
								 * the previous loop. This is because the
								 * relationship for CEs is based on
								 * fromFPNodeId. Remove the CE from unassigned
								 * CE list.
								 */
								ret.removeFromUnassignCntEquips(ce);
							}

							eg = (EgressPoint) FacilityNode.findFpNode(
									egressPoints, tempFR.getToNodeId());
							if (eg != null) {
								tempEP.addEgressPoint(eg);
								if (!EgrPointTypeDef.isFugitive(eg)) {
									FacilityEmissionFlow emissionFlow = new FacilityEmissionFlow(
											FacilityEmissionFlow.STACK_TYPE,
											eg.getReleasePointId(), tempFR);
									tempEP.addepEmissionFlow(emissionFlow);
									if (tempFR.getFlowFactor() > 0f) {
										percent += tempFR.getFlowFactor();
									}
									if (tempFR.getFlowFactor() == MISSING_FLOW_PERCENT) {
										missingValues = true;
									}
								}
								homeFound = true;
							}

							if (!homeFound) {
								StringBuffer tempErrorMsg = new StringBuffer(
										"DB relationship problem, not a valid realtionship ");

								tempErrorMsg.append("FpId " + fpId.toString());
								tempErrorMsg.append(" FromNodeId "
										+ tempFR.getFromNodeId());
								tempErrorMsg.append(" ToNodeId "
										+ tempFR.getToNodeId());

								logger.error(tempErrorMsg.toString());
							}
						}

						if (tempEP.getEpEmissionFlows().size() > 0) {
							for (FacilityEmissionFlow emissionFlow : tempEP
									.getEpEmissionFlows()) {
								if (percent > (float) 0.0) {
									if (!missingValues) {
										if (emissionFlow.getRelationship()
												.getFlowFactor().floatValue() >= (float) 0.0) {
											Double percent1 = new Double(
													(emissionFlow
															.getRelationship()
															.getFlowFactor()
															.floatValue() / percent)
															* new Float(100.0));
											emissionFlow.setPercents(BaseDB
													.numberToxxx_xx(percent1),
													percent1.floatValue());
										} else {
											emissionFlow.setPercents(null, 0);
										}
									} else {
										if (emissionFlow.getRelationship()
												.getFlowFactor().floatValue() >= (float) 0.0) {
											Double percent2 = new Double(
													emissionFlow
															.getRelationship()
															.getFlowFactor()
															.floatValue());
											emissionFlow.setPercents(BaseDB
													.numberToxxx_xx(percent2),
													percent2.floatValue());
										} else {
											emissionFlow.setPercents(null, 0);
										}
									}
								} else if (emissionFlow.getRelationship()
										.getFlowFactor().floatValue() == (float) 0.0) {
									emissionFlow.setPercents("0", 0);
								} else {
									emissionFlow.setPercents(null, 0);
								}
								loggerDebug("EP = "
										+ tempEP.getProcessId()
										+ " Type = "
										+ emissionFlow.getType()
										+ " ID = "
										+ emissionFlow.getId()
										+ " Factor = "
										+ emissionFlow.getRelationship()
												.getFlowFactor()
										+ " Percent = "
										+ emissionFlow.getPercent());
							}
						}
					}

					/*
					 * Build relationship between EmissionProcesses and
					 * EmissionUnits
					 */
					iter = emissionProcesses.iterator();

					while (iter.hasNext()) {
						EmissionProcess tempEP = (EmissionProcess) iter.next();

						Integer tempEUId = tempEP.getEmissionUnitId();

						if ((tempEUId != null)
								&& (ret.getEmissionUnit(tempEUId) != null)) {
							EmissionUnit tmpEU = ret.getEmissionUnit(tempEUId);
							tmpEU.addEmissionProcess(tempEP);
							// mark all processes, equipment and release points
							// as
							// active if this EU is not shut down or invalid
							if (!EuOperatingStatusDef.SD.equals(tmpEU
									.getOperatingStatusCd())
									&& !EuOperatingStatusDef.IV.equals(tmpEU
											.getOperatingStatusCd())) {
								loggerDebug(" EU is active: "
										+ tmpEU.getEpaEmuId());
								tempEP.setActive(true);
								loggerDebug("   EProc is active: "
										+ tempEP.getProcessId());
								for (EgressPoint tmpEP : tempEP
										.getEgressPoints()) {
									tmpEP.setActive(true);
									loggerDebug("     EP is active: "
											+ tmpEP.getReleasePointId());
								}
								for (ControlEquipment tmpCE : tempEP
										.getControlEquipments()) {
									tmpCE.setActive(true);
									loggerDebug("     CE is active: "
											+ tmpCE.getControlEquipmentId());
									for (EgressPoint tmpEP : tmpCE
											.getEgressPoints()) {
										tmpEP.setActive(true);
										loggerDebug("       EP is active: "
												+ tmpEP.getReleasePointId());
									}
								}
							}
						} else {
							StringBuffer tempErrorMsg = new StringBuffer(
									"EmissionProcess with no EmissionUnitId specified ");

							tempErrorMsg.append("FpId " + fpId.toString());
							tempErrorMsg.append(" Emission Process FPNode_id "
									+ tempEP.getFpNodeId());

							logger.error(tempErrorMsg.toString());
						}
					}

					boolean euBolier;
					boolean euTurbine;
					boolean euGenerator;

					for (EmissionUnit eu : ret.getEmissionUnits()) {
						euBolier = false;
						euTurbine = false;
						euGenerator = false;

						/*
						 * for (EmissionProcess ep : eu.getEmissionProcesses())
						 * { sccEuCapacityType = ep.getSccCode()
						 * .getEuCapacityTypeCd(); if
						 * (sccEuCapacityType.equals(DesignCapacityDef.BO)) {
						 * euBolier = true; break; } else if (sccEuCapacityType
						 * .equals(DesignCapacityDef.TU)) { euTurbine = true; }
						 * else if (sccEuCapacityType
						 * .equals(DesignCapacityDef.GE)) { euGenerator = true;
						 * } }
						 */
						if (euBolier) {
							eu.setDesignCapacityCd(DesignCapacityDef.BO);
						} else if (euTurbine) {
							eu.setDesignCapacityCd(DesignCapacityDef.TU);
						} else if (euGenerator) {
							eu.setDesignCapacityCd(DesignCapacityDef.GE);
						} else {
							eu.setDesignCapacityCd(DesignCapacityDef.NA);
							eu.setDesignCapacityUnitsCd(null);
							eu.setDesignCapacityUnitsVal(null);
						}

						// must set emission unit type to each EU
						if (eu.getEmissionUnitTypeCd() != null
								&& eu.getEmissionUnitTypeCd().length() > 0) {
							EmissionUnitTypeDAO euTypeDAO = getEUTypeDAO(
									eu.getEmissionUnitTypeCd(), trans, staging);
							if (euTypeDAO != null) {
								if (eu.getEmissionUnitType() == null) {
									EmissionUnitType euType = euTypeDAO
											.retrieveEmissionUnitType(eu
													.getEmuId());
									if (euType != null) {
										euType.setEmissionUnitTypeCd(eu
												.getEmissionUnitTypeCd());
									}
									eu.setEmissionUnitType(euType);
								}
								List<Component> components = euTypeDAO.retrieveComponentsByEmuId(eu.getEmuId());
								eu.getEmissionUnitType().setComponents(components);
							}
							if (eu.isReplacementType()) {
								eu.setEmissionUnitReplacements(facilityDAO
										.retrieveEmissionUnitReplacements(eu
												.getEmuId()));
							}
						}
					}
					
					// Retrieve Continuous Monitors

					List<ContinuousMonitor> continuousMonitors;
					continuousMonitors = new ArrayList<ContinuousMonitor>();

					List<ContinuousMonitor> completeContinuousMonitorsList = new ArrayList<ContinuousMonitor>();

					continuousMonitors = continuousMonitorDAO
							.searchContinuousMonitors(fpId, null, null, null,
									null, null, null, null, null);

					completeContinuousMonitorsList = retrieveCompleteContinuousMonitors(
							continuousMonitors, staging);

					ret.setContinuousMonitorList(completeContinuousMonitorsList);

					// Retrieve limits.

					List<FacilityCemComLimit> limits = new ArrayList<FacilityCemComLimit>();

					limits = facilityDAO
							.retrieveFacilityCemComLimitListByFpId(ret
									.getFpId());

					ret.setFacilityCemComLimitList(limits);
					
				}
			} catch (DAOException de) {
				String facId = (ret != null) ? ret.getFacilityId() : "UnKnown";
				logger.error(
						"retrieve facility detail failed; fp_id = [" + fpId
								+ "] facility ID = [" + facId + "]:"
								+ de.getMessage(), de);
				throw de;
			}
		}
		logger.debug("exit retrieveFacilityProfile" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
		return ret;
	}

	/**
	 * Modifies the supplied facility Rules.
	 * 
	 * @param Facility
	 *            Facility to modify
	 * @return boolean True = success, false = failure.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyFacilityFedRules(Facility facility, Integer userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		boolean ret = false;

		Facility newFacility = null;

		try {

			newFacility = copyFacilityProfile(facility.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to modify it.");
				throw e;
			}

			if (!newFacility.getFpId().equals(facility.getFpId())) {
				facility.setCopyOnChange(newFacility.isCopyOnChange());
				facility.setFpId(newFacility.getFpId());
				facility.setVersionId(newFacility.getVersionId());
				facility.setStartDate(newFacility.getStartDate());
				facility.setLastModified(newFacility.getLastModified());
				facility.getPhyAddr().setAddressId(
						newFacility.getPhyAddr().getAddressId());
			}

			facility.setValidated(false);
			facility.setSubmitted(false);
			setLastSubmission(facility);

			// FAL entries for air program changes
			InfrastructureDAO infraDAO = infrastructureDAO(trans);
			ArrayList<FieldAuditLog> falLogs = new ArrayList<FieldAuditLog>();
			if (isDifferent(facility.isMact(), facility.isOldMact())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to Part 63 NESHAP = "
								+ printableValue(facility.isOldMact()),
						printableValue(facility.isMact())));
			}
			if (isDifferent(facility.isNeshaps(), facility.isOldNeshaps())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to Part 61 NESHAP = "
								+ printableValue(facility.isOldNeshaps()),
						printableValue(facility.isNeshaps())));
			}
			if (isDifferent(facility.isNsps(), facility.isOldNsps())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to Part 60 NSPS = "
								+ printableValue(facility.isOldNsps()),
						printableValue(facility.isNsps())));
			}
			if (isDifferent(facility.isPsd(), facility.isOldPsd())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to PSD = "
								+ printableValue(facility.isOldPsd()),
						printableValue(facility.isPsd())));
			}
			if (isDifferent(facility.isNsrNonattainment(),
					facility.isOldNsrNonattainment())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to Non-attainment NSR = "
								+ printableValue(facility
										.isOldNsrNonattainment()),
						printableValue(facility.isNsrNonattainment())));
			}
			FieldAuditLog[] auditLog = falLogs.toArray(new FieldAuditLog[0]);
			if (userId != -1) {
				infraDAO.createFieldAuditLogs(facility.getFacilityId(),
						facility.getName(), userId, auditLog);
			}
			ret = facilityDAO.modifyFacility(facility);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				// update Part 63 NESHAP Subparts
				facilityDAO.removeFacilityMACTSubparts(facility.getFpId());
				if (facility.isMact()) {
					for (String mactSubpart : facility.getMactSubparts()) {
						facilityDAO.addFacilityMACTSubpart(facility.getFpId(),
								mactSubpart);
					}
				}

				// update Part 61 NESHAP Subparts
				facilityDAO.removeFacilityNeshapsSubparts(facility.getFpId());
				if (facility.isNeshaps()) {
					for (PollutantCompCode neshapsSubpart : facility
							.getNeshapsSubpartsCompCds()) {
						neshapsSubpart.setFpId(facility.getFpId());
						facilityDAO.addFacilityNeshapsSubpart(neshapsSubpart);
					}
				}

				// update Part 60 NSPS Subparts
				facilityDAO.removeFacilityNSPSSubparts(facility.getFpId());
				if (facility.isNsps()) {
					for (String nspsSubpart : facility.getNspsSubparts()) {
						facilityDAO.addFacilityNSPSSubpart(facility.getFpId(),
								nspsSubpart);
					}
				}

			}

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
			throw new DAOException(e.getMessage(), e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * Performs all modify functions of modifyFacilityInternal() except it
	 * returns data to perform the facility shutdown notification separately.
	 * 
	 * @param Facility
	 *            Facility to modify
	 * @return boolean True = success, false = failure.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public HashMap<String, String> modifyFacilityRtnToDo(Facility facility,
			int userId) throws DAOException {
		HashMap<String, String> data = null;
		data = modifyFacilityInternal(facility, userId, false);
		return data;
	}

	/**
	 * Performs facility shutdown notification using the data generated upon an
	 * earlier call to modifyFacility
	 * 
	 * @param Facility
	 *            Facility to modify
	 * @return boolean True = success, false = failure.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void performShutdownToDo(Facility facility, int userId,
			HashMap<String, String> data) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			performShutdownToDo(facility, userId, data, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	private void performShutdownToDo(Facility facility, int userId,
			HashMap<String, String> data, Transaction trans)
			throws DAOException {
		Integer roleUserId = facility
				.getFacilityRoles()
				.get(us.oh.state.epa.stars2.def.FacilityRoleDef.FACILITY_PROFILE_ADMIN)
				.getUserId();
		createToDoEntry(facility.getFpId(), data, "N",
				new Timestamp(System.currentTimeMillis()), null, roleUserId,
				trans, userId);
		// Mantis 2423: also generate To Do Entry for Emission Banking
		// Notification role
		// Integer ebiRoleUserId = facility
		// .getFacilityRoles()
		// .get(us.oh.state.epa.stars2.def.FacilityRoleDef.EMISSION_BANKING_NOTIFICATION)
		// .getUserId();
		// createToDoEntry(facility.getFpId(), data, "N",
		// new Timestamp(System.currentTimeMillis()), null, ebiRoleUserId,
		// trans, userId);
	}

	/**
	 * Performs all modify functions of modifyFacilityInternal() including the
	 * facility shutdown notification.
	 * 
	 * @param Facility
	 *            Facility to modify
	 * @return boolean True = success, false = failure.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyFacility(Facility facility, int userId)
			throws DAOException {
		HashMap<String, String> data = null;
		data = modifyFacilityInternal(facility, userId, true);
		return data != null;
	}

	/**
	 * Performs all modify functions of modifyFacilityInternal() including the
	 * facility shutdown notification.
	 * 
	 * @param Facility
	 *            Facility to modify
	 * @return boolean True = success, false = failure.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyFacility(Facility facility, int userId,
			Transaction trans) throws DAOException {
		HashMap<String, String> data = null;
		data = modifyFacilityInternal(facility, userId, true, trans);
		return data != null;
	}

	/**
	 * Modifies the supplied facility. This method only modifies the facility,
	 * none of the facilities contained objects are updated. To modify an entire
	 * Facility, including contained objects, the FacilityBO should be used.
	 * 
	 * @param Facility
	 *            Facility to modify
	 * @return boolean True = success, false = failure.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public HashMap<String, String> modifyFacilityInternal(Facility facility,
			int userId, boolean performShutdownToDo) throws DAOException {
		loggerDebug("modifyFacilityInternal started; facility = "
				+ facility.getFacilityId());

		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		HashMap<String, String> data = new HashMap<String, String>();
		boolean cl = false; // coreLite DB update flag
		Facility newFacility = null;

		try {

			newFacility = copyFacilityProfile(facility.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId, trans);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to modify it.");
				throw e;
			}

			if (!newFacility.getFpId().equals(facility.getFpId())) {
				facility.setCopyOnChange(newFacility.isCopyOnChange());
				facility.setFpId(newFacility.getFpId());
				facility.setVersionId(newFacility.getVersionId());
				facility.setStartDate(newFacility.getStartDate());
				facility.setLastModified(newFacility.getLastModified());
				facility.getPhyAddr().setAddressId(
						newFacility.getPhyAddr().getAddressId());
				if (facility.getHydrocarbonAnalysisSampleDetail() != null && newFacility.getHydrocarbonAnalysisSampleDetail() != null){
					facility.getHydrocarbonAnalysisSampleDetail().setLastModified(newFacility.getHydrocarbonAnalysisSampleDetail().getLastModified());
				}
				
				if (facility.getDecaneProperties() != null && newFacility.getDecaneProperties() != null){
					facility.getDecaneProperties().setLastModified(newFacility.getDecaneProperties().getLastModified());
				}
			}

			facility.setValidated(false);
			facility.setSubmitted(false);
			setLastSubmission(facility);

			ApplicationDAO applicationDAO = applicationDAO(trans);
			applicationDAO.setActiveApplicationsValidatedFlag(
					facility.getFpId(), false);
			EmissionsReportDAO reportDAO = emissionsReportDAO(trans);
			reportDAO.setActiveEmissionsReportsValidatedFlag(facility.getFpId(), false);
			ComplianceReportDAO crDao = complianceReportDAO(trans);
			crDao.setActiveComplianceReportsValidatedFlag(
					facility.getFpId(), false);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				if (!facility.getPhyAddr().equalsIgnoreCase(
						newFacility.getPhyAddr())) {
					cl = true;
				}

				if (!facility.getPhyAddr().equalsNotIgnoreCase(
						newFacility.getPhyAddr())) {
					if (facility.getPhyAddr()
							.equalsNotIgnoreCaseXcityIgnoreCase(
									newFacility.getPhyAddr())) {
						// only city capitalization changed. Update in place.
						facility.getPhyAddr().setEndDate(null); // don't set end
																// date.
						facility.getPhyAddr().setBeginDate(
								newFacility.getPhyAddr().getBeginDate()); // don't
																			// change
																			// effective
																			// date
						infraDAO.modifyAddress(facility.getPhyAddr());
					} else {
						Address oldAddr = newFacility.getPhyAddr();
						Address newAddr = facility.getPhyAddr();
						facility.fieldChangeEventLog("fal1", "N/A",
								oldAddr.getAddressLine1(),
								newAddr.getAddressLine1(), true);
						facility.fieldChangeEventLog("fal2", "N/A",
								oldAddr.getAddressLine2(),
								newAddr.getAddressLine2(), true);
						facility.fieldChangeEventLog("fcit", "N/A",
								oldAddr.getCityName(), newAddr.getCityName(),
								true);
						String oldState = State.getData().getItems()
								.getItemDesc(oldAddr.getState());
						String newState = State.getData().getItems()
								.getItemDesc(newAddr.getState());
						facility.fieldChangeEventLog("fsta", "N/A", oldState,
								newState, true);
						facility.fieldChangeEventLog("fzip", "N/A",
								oldAddr.getZipCode(), newAddr.getZipCode(),
								true);

						CountyDef oldCounty = infraDAO.retrieveCounty(oldAddr
								.getCountyCd());
						CountyDef newCounty = infraDAO.retrieveCounty(newAddr
								.getCountyCd());
						facility.fieldChangeEventLog("fcnt", "N/A",
								oldCounty.getCountyNm(),
								newCounty.getCountyNm(), true);

						// newFacility.getPhyAddr().setEndDate(
						// facility.getPhyAddr().getBeginDate());
						// infraDAO.modifyAddress(newFacility.getPhyAddr());
						//
						// Address newAddress;
						// // facility.getPhyAddr().setBeginDate(newTime); <--
						// now
						// // set in update web page
						// facility.getPhyAddr().setAddressId(null);
						// newAddress = infraDAO.createAddress(facility
						// .getPhyAddr());
						// facility.getPhyAddr().setAddressId(
						// newAddress.getAddressId());
						// facilityDAO.addFacilityAddress(facility.getFpId(),
						// newAddress.getAddressId());
					}
				}

				if (!facility.getName().equalsIgnoreCase(newFacility.getName())) {
					cl = true;
				}

				if (facility.getOperatingStatusCd().equals(
						OperatingStatusDef.SD)
						&& (!newFacility.getOperatingStatusCd().equals(
								OperatingStatusDef.SD))) {
					if (facility.getShutdownDate() == null) {
						facility.setShutdownDate(new Timestamp(System
								.currentTimeMillis()));
					}

					for (EmissionUnit tempEU : newFacility.getEmissionUnits()
							.toArray(new EmissionUnit[0])) {
						if (tempEU.getOperatingStatusCd().equals(
								EuOperatingStatusDef.OP)) {

							// No need to do any thing if the EmissionUnit Type
							// does not be changed
							boolean isTheSameEuType = isTheSameEmissionUnitTypeInDb(
									tempEU, trans);

							if (!isTheSameEuType) {
								String newEuId = facilityDAO
										.retrieveNextEmissionUnitId(tempEU);
								tempEU.setEpaEmuId(newEuId);

								String oldEuId = facilityDAO
										.retrieveEmissionUnitDisplayId(tempEU
												.getFpNodeId());

								// must create field audit log for each changed
								// EU id
								if (CompMgr.getAppName().equals(
										CommonConst.INTERNAL_APP)) {
									List<FieldAuditLog> falList = new ArrayList<FieldAuditLog>();

									FieldAuditLog fal = null;

									if (tempEU.getCorrEpaEmuId() != null) {
										fal = new FieldAuditLog(
												"euty",
												newEuId,
												"["
														+ oldEuId
														+ "] "
														+ EmissionUnitTypeDef
																.getData()
																.getItems()
																.getItemDesc(
																		facilityDAO
																				.retrieveEmissionUnitTypeCd(tempEU
																						.getEmuId())),
												"["
														+ newEuId
														+ "] "
														+ EmissionUnitTypeDef
																.getData()
																.getItems()
																.getItemDesc(
																		tempEU.getEmissionUnitTypeCd()),
												tempEU.getCorrEpaEmuId());

										falList.add(fal);

									}

									infraDAO.createFieldAuditLogs(newFacility
											.getFacilityId(), newFacility
											.getName(), userId, falList
											.toArray(new FieldAuditLog[0]));

								}
							}

							tempEU.setOperatingStatusCd(EuOperatingStatusDef.SD);
							tempEU.setEuShutdownDate(facility.getShutdownDate());
							tempEU.setEuShutdownNotificationDate(facility
									.getShutdownNotifDate());

							facilityDAO.modifyEmissionUnit(tempEU);
						}
						// terminate permit EUs associated with shutdown EU
						try {
							PermitService permitBO = ServiceFactory
									.getInstance().getPermitService();
							permitBO.terminatePermitEUsForShutdownEU(
									newFacility, tempEU);
						} catch (ServiceFactoryException e) {
							logger.error(
									"Exception while attempting to mark permit EUs as terminated for "
											+ facility.getFacilityId(), e);
						} catch (RemoteException e) {
							logger.error(
									"Exception while attempting to mark permit EUs as terminated for "
											+ facility.getFacilityId(), e);
						}
					}
				}
			}

			facilityDAO.modifyFacility(facility);

			// Modify SIC codes
			facilityDAO.removeFacilitySICs(facility.getFpId());
			for (String sicCd : facility.getSicCds()) {
				facilityDAO.addFacilitySIC(facility.getFpId(), sicCd);
			}

			// Modify NAICS codes
			facilityDAO.removeFacilityNAICSs(facility.getFpId());
			for (String naicsCd : facility.getNaicsCds()) {
				facilityDAO.addFacilityNAICS(facility.getFpId(), naicsCd);
			}

			//modify HC analysis tables
			facilityDAO.deleteFacExtendedHCAnalysisPollutant(facility.getFpId());
			
			if (FacilityTypeDef.hasHCAnalysis(facility.getFacilityTypeCd())){
				for (HydrocarbonAnalysisPollutant hcPollutant:facility.getHydrocarbonPollutantList()){
					if (!PollutantDef.TOTAL.equals(hcPollutant.getPollutantCd()) && 
							(hcPollutant.getGas() != null || hcPollutant.getOil() != null || hcPollutant.getProducedWater() != null)){
						hcPollutant.setFpId(facility.getFpId());
						facilityDAO.addFacExtendedHCAnalysisPollutant(hcPollutant);
					}
				}
				//modify hydrocarbon analysis Sample Detail
				if (null != facility.getHydrocarbonAnalysisSampleDetail()){
					facility.getHydrocarbonAnalysisSampleDetail().setFpId(facility.getFpId());
					
					if (facility.getHydrocarbonAnalysisSampleDetail().isNewObject()){
						facilityDAO.addFacHydrocarbonAnalysisSampleDetail(facility.getHydrocarbonAnalysisSampleDetail());
					} else {
						facilityDAO.modifyFacHydrocarbonAnalysisSampleDetail(facility.getHydrocarbonAnalysisSampleDetail());
					}

				}

				// modify hydrocarbon analysis decane properties
				if (null != facility.getDecaneProperties()) {

					// update fpId it was changed due to creation of a new
					// version of the facility inventory
					facility.getDecaneProperties().setFpId(facility.getFpId());

					if (facility.getDecaneProperties().isNewObject()) {
						facilityDAO.createDecaneProperties(facility.getDecaneProperties());
					} else {
						facilityDAO.modifyDecaneProperties(facility.getDecaneProperties());
					}
				}
			} else {
				facilityDAO.deleteFacHydrocarbonAnalysisSampleDetail(facility.getFpId());
				facilityDAO.deleteDecaneProperties(facility.getFpId());
			}
			

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				// update Notification Counties
				facilityDAO.removeCountyNotifications(facility.getFpId());
				for (String county : facility.getNotificationCounties()) {
					facilityDAO.addCountyNotification(facility.getFpId(),
							county);
				}

				if (facility.getOperatingStatusCd().equals(
						OperatingStatusDef.SD)
						&& (!newFacility.getOperatingStatusCd().equals(
								OperatingStatusDef.SD))) {
					data.put("Task Name", "Facility Shutdown");
					String notes;
					if (PermitClassDef.TV.equals(facility.getPermitClassCd())) {
						notes = "ALL active EUs are shutting down as result of shutting down facility. "
								+ "The responsible party may have to reapply for a permit to install (PTI) "
								+ "for the EU prior to operating again.  Additionally, the responsible party "
								+ "must submit a notification of shutdown or request a permit modification.  "
								+ "Please contact your DO/LAA representative for assistance";
					} else {
						notes = "ALL active EUs are shutting down as result of shutting down facility. "
								+ "Any EU marked as permanently shutdown may result in a revoked permit "
								+ "or portion of a permit.  Additionally, the responsible party may have to "
								+ "reapply for a permit to install (PTI) for the EU prior to operating again";
					}
					data.put("Notes", notes);
					if (performShutdownToDo) {
						performShutdownToDo(facility, userId, data, trans);
					}

					// Change emissions reporting state to report not needed
					// for existing rows in fp_yearly_reporting_category.
					facility.getShutdownDate();
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(facility.getShutdownDate().getTime());
					int sdYear = c.get(Calendar.YEAR);
					EmissionsReportService emissionsRptBO = null;

					emissionsRptBO = ServiceFactory.getInstance()
							.getEmissionsReportService();
					emissionsRptBO.updateYearlyReportingToShutdown(
							facility.getFacilityId(), sdYear, trans);
				}

				infraDAO.createFieldAuditLogs(facility.getFacilityId(),
						facility.getName(), userId,
						facility.getFieldAuditLogs());
				createFieldChangeEventLog(facility.getFieldEventLogs(),
						EventLogTypeDef.FAC_CHG, userId, facility.getFpId(),
						facility.getFacilityId(), trans);
				if (cl) {
					try {
						internalFacilitySubmit(facility,
								"UP" + facility.getFacilityId());
					} catch (Exception e) {
						logger.warn(
								"Updating COREDB failed; Continue updating STARS2 DB; facility ID = "
										+ facility.getFacilityId()
										+ e.getMessage(), e);
						data = null;
					}
				}
			}
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
		} catch (Exception e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans,
					new DAOException(e.getMessage(), e));
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		loggerDebug("modifyFacilityInternal completed; facility = "
				+ facility.getFacilityId());
		return data;
	}

	/**
	 * Modifies the supplied facility. This method only modifies the facility,
	 * none of the facilities contained objects are updated. To modify an entire
	 * Facility, including contained objects, the FacilityBO should be used.
	 * 
	 * @param Facility
	 *            Facility to modify
	 * @return boolean True = success, false = failure.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public HashMap<String, String> modifyFacilityInternal(Facility facility,
			int userId, boolean performShutdownToDo, Transaction trans)
			throws DAOException {
		loggerDebug("modifyFacilityInternal started; facility = "
				+ facility.getFacilityId());

		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		HashMap<String, String> data = new HashMap<String, String>();
		boolean cl = false; // coreLite DB update flag
		Facility newFacility = null;

		try {

			newFacility = copyFacilityProfile(facility.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId, trans);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to modify it.");
				throw e;
			}

			if (!newFacility.getFpId().equals(facility.getFpId())) {
				facility.setCopyOnChange(newFacility.isCopyOnChange());
				facility.setFpId(newFacility.getFpId());
				facility.setVersionId(newFacility.getVersionId());
				facility.setStartDate(newFacility.getStartDate());
				facility.setLastModified(newFacility.getLastModified());
				facility.getPhyAddr().setAddressId(
						newFacility.getPhyAddr().getAddressId());
				if (facility.getHydrocarbonAnalysisSampleDetail() != null && newFacility.getHydrocarbonAnalysisSampleDetail() != null){
					facility.getHydrocarbonAnalysisSampleDetail().setLastModified(newFacility.getHydrocarbonAnalysisSampleDetail().getLastModified());
				}
				
				if (facility.getDecaneProperties() != null && newFacility.getDecaneProperties() != null){
					facility.getDecaneProperties().setLastModified(newFacility.getDecaneProperties().getLastModified());
				}
			}

			facility.setValidated(false);
			facility.setSubmitted(false);
			setLastSubmission(facility);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				if (!facility.getPhyAddr().equalsIgnoreCase(
						newFacility.getPhyAddr())) {
					cl = true;
				}

				if (!facility.getPhyAddr().equalsNotIgnoreCase(
						newFacility.getPhyAddr())) {
					if (facility.getPhyAddr()
							.equalsNotIgnoreCaseXcityIgnoreCase(
									newFacility.getPhyAddr())) {
						// only city capitalization changed. Update in place.
						facility.getPhyAddr().setEndDate(null); // don't set end
																// date.
						facility.getPhyAddr().setBeginDate(
								newFacility.getPhyAddr().getBeginDate()); // don't
																			// change
																			// effective
																			// date
						infraDAO.modifyAddress(facility.getPhyAddr());
					} else {
						Address oldAddr = newFacility.getPhyAddr();
						Address newAddr = facility.getPhyAddr();
						facility.fieldChangeEventLog("fal1", "N/A",
								oldAddr.getAddressLine1(),
								newAddr.getAddressLine1(), true);
						facility.fieldChangeEventLog("fal2", "N/A",
								oldAddr.getAddressLine2(),
								newAddr.getAddressLine2(), true);
						facility.fieldChangeEventLog("fcit", "N/A",
								oldAddr.getCityName(), newAddr.getCityName(),
								true);
						String oldState = State.getData().getItems()
								.getItemDesc(oldAddr.getState());
						String newState = State.getData().getItems()
								.getItemDesc(newAddr.getState());
						facility.fieldChangeEventLog("fsta", "N/A", oldState,
								newState, true);
						facility.fieldChangeEventLog("fzip", "N/A",
								oldAddr.getZipCode(), newAddr.getZipCode(),
								true);

						CountyDef oldCounty = infraDAO.retrieveCounty(oldAddr
								.getCountyCd());
						CountyDef newCounty = infraDAO.retrieveCounty(newAddr
								.getCountyCd());
						facility.fieldChangeEventLog("fcnt", "N/A",
								oldCounty.getCountyNm(),
								newCounty.getCountyNm(), true);

						// newFacility.getPhyAddr().setEndDate(
						// facility.getPhyAddr().getBeginDate());
						// infraDAO.modifyAddress(newFacility.getPhyAddr());
						//
						// Address newAddress;
						// // facility.getPhyAddr().setBeginDate(newTime); <--
						// now
						// // set in update web page
						// facility.getPhyAddr().setAddressId(null);
						// newAddress = infraDAO.createAddress(facility
						// .getPhyAddr());
						// facility.getPhyAddr().setAddressId(
						// newAddress.getAddressId());
						// facilityDAO.addFacilityAddress(facility.getFpId(),
						// newAddress.getAddressId());
					}
				}

				if (!facility.getName().equalsIgnoreCase(newFacility.getName())) {
					cl = true;
				}

				if (facility.getOperatingStatusCd().equals(
						OperatingStatusDef.SD)
						&& (!newFacility.getOperatingStatusCd().equals(
								OperatingStatusDef.SD))) {
					if (facility.getShutdownDate() == null) {
						facility.setShutdownDate(new Timestamp(System
								.currentTimeMillis()));
					}

					for (EmissionUnit tempEU : newFacility.getEmissionUnits()
							.toArray(new EmissionUnit[0])) {
						if (tempEU.getOperatingStatusCd().equals(
								EuOperatingStatusDef.OP)) {

							// No need to do any thing if the EmissionUnit Type
							// does not be changed
							boolean isTheSameEuType = isTheSameEmissionUnitTypeInDb(
									tempEU, trans);

							if (!isTheSameEuType) {
								String newEuId = facilityDAO
										.retrieveNextEmissionUnitId(tempEU);
								tempEU.setEpaEmuId(newEuId);

								String oldEuId = facilityDAO
										.retrieveEmissionUnitDisplayId(tempEU
												.getFpNodeId());

								// must create field audit log for each changed
								// EU id
								if (CompMgr.getAppName().equals(
										CommonConst.INTERNAL_APP)) {
									List<FieldAuditLog> falList = new ArrayList<FieldAuditLog>();

									FieldAuditLog fal = null;

									if (tempEU.getCorrEpaEmuId() != null) {
										fal = new FieldAuditLog(
												"euty",
												newEuId,
												"["
														+ oldEuId
														+ "] "
														+ EmissionUnitTypeDef
																.getData()
																.getItems()
																.getItemDesc(
																		facilityDAO
																				.retrieveEmissionUnitTypeCd(tempEU
																						.getEmuId())),
												"["
														+ newEuId
														+ "] "
														+ EmissionUnitTypeDef
																.getData()
																.getItems()
																.getItemDesc(
																		tempEU.getEmissionUnitTypeCd()),
												tempEU.getCorrEpaEmuId());

										falList.add(fal);

									}

									infraDAO.createFieldAuditLogs(newFacility
											.getFacilityId(), newFacility
											.getName(), userId, falList
											.toArray(new FieldAuditLog[0]));

								}
							}

							tempEU.setOperatingStatusCd(EuOperatingStatusDef.SD);
							tempEU.setEuShutdownDate(facility.getShutdownDate());
							tempEU.setEuShutdownNotificationDate(facility
									.getShutdownNotifDate());

							facilityDAO.modifyEmissionUnit(tempEU);
						}
						// terminate permit EUs associated with shutdown EU
						try {
							PermitService permitBO = ServiceFactory
									.getInstance().getPermitService();
							permitBO.terminatePermitEUsForShutdownEU(
									newFacility, tempEU);
						} catch (ServiceFactoryException e) {
							logger.error(
									"Exception while attempting to mark permit EUs as terminated for "
											+ facility.getFacilityId(), e);
						} catch (RemoteException e) {
							logger.error(
									"Exception while attempting to mark permit EUs as terminated for "
											+ facility.getFacilityId(), e);
						}
					}
				}
			}

			facilityDAO.modifyFacility(facility);

			// Modify SIC codes
			facilityDAO.removeFacilitySICs(facility.getFpId());
			for (String sicCd : facility.getSicCds()) {
				facilityDAO.addFacilitySIC(facility.getFpId(), sicCd);
			}

			// Modify NAICS codes
			facilityDAO.removeFacilityNAICSs(facility.getFpId());
			for (String naicsCd : facility.getNaicsCds()) {
				facilityDAO.addFacilityNAICS(facility.getFpId(), naicsCd);
			}

			//modify HC analysis tables
			facilityDAO.deleteFacExtendedHCAnalysisPollutant(facility.getFpId());
			
			if (FacilityTypeDef.hasHCAnalysis(facility.getFacilityTypeCd())){
				for (HydrocarbonAnalysisPollutant hcPollutant:facility.getHydrocarbonPollutantList()){
					if (!PollutantDef.TOTAL.equals(hcPollutant.getPollutantCd()) && 
							(hcPollutant.getGas() != null || hcPollutant.getOil() != null || hcPollutant.getProducedWater() != null)){
						hcPollutant.setFpId(facility.getFpId());
						facilityDAO.addFacExtendedHCAnalysisPollutant(hcPollutant);
					}
				}

				if (null != facility.getHydrocarbonAnalysisSampleDetail()){
					facility.getHydrocarbonAnalysisSampleDetail().setFpId(facility.getFpId());
					
					if (facility.getHydrocarbonAnalysisSampleDetail().isNewObject()){
						facilityDAO.addFacHydrocarbonAnalysisSampleDetail(facility.getHydrocarbonAnalysisSampleDetail());
					} else {
						facilityDAO.modifyFacHydrocarbonAnalysisSampleDetail(facility.getHydrocarbonAnalysisSampleDetail());
					}
				}
				// modify hydrocarbon analysis decane properties
				if (null != facility.getDecaneProperties()) {

					// update fpId it was changed due to creation of a new
					// version of the facility inventory
					facility.getDecaneProperties().setFpId(facility.getFpId());

					if (facility.getDecaneProperties().isNewObject()) {
						facilityDAO.createDecaneProperties(facility.getDecaneProperties());
					} else {
						facilityDAO.modifyDecaneProperties(facility.getDecaneProperties());
					}
				}
			} else {
				facilityDAO.deleteFacHydrocarbonAnalysisSampleDetail(facility.getFpId());
				facilityDAO.deleteDecaneProperties(facility.getFpId());
			}

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				// update Notification Counties
				facilityDAO.removeCountyNotifications(facility.getFpId());
				for (String county : facility.getNotificationCounties()) {
					facilityDAO.addCountyNotification(facility.getFpId(),
							county);
				}

				if (facility.getOperatingStatusCd().equals(
						OperatingStatusDef.SD)
						&& (!newFacility.getOperatingStatusCd().equals(
								OperatingStatusDef.SD))) {
					data.put("Task Name", "Facility Shutdown");
					String notes;
					if (PermitClassDef.TV.equals(facility.getPermitClassCd())) {
						notes = "ALL active EUs are shutting down as result of shutting down facility. "
								+ "The responsible party may have to reapply for a permit to install (PTI) "
								+ "for the EU prior to operating again.  Additionally, the responsible party "
								+ "must submit a notification of shutdown or request a permit modification.  "
								+ "Please contact your DO/LAA representative for assistance";
					} else {
						notes = "ALL active EUs are shutting down as result of shutting down facility. "
								+ "Any EU marked as permanently shutdown may result in a revoked permit "
								+ "or portion of a permit.  Additionally, the responsible party may have to "
								+ "reapply for a permit to install (PTI) for the EU prior to operating again";
					}
					data.put("Notes", notes);
					if (performShutdownToDo) {
						performShutdownToDo(facility, userId, data, trans);
					}

					// Change emissions reporting state to report not needed
					// for existing rows in fp_yearly_reporting_category.
					facility.getShutdownDate();
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(facility.getShutdownDate().getTime());
					int sdYear = c.get(Calendar.YEAR);
					EmissionsReportService emissionsRptBO = null;

					emissionsRptBO = ServiceFactory.getInstance()
							.getEmissionsReportService();
					emissionsRptBO.updateYearlyReportingToShutdown(
							facility.getFacilityId(), sdYear, trans);
				}

				infraDAO.createFieldAuditLogs(facility.getFacilityId(),
						facility.getName(), userId,
						facility.getFieldAuditLogs());
				createFieldChangeEventLog(facility.getFieldEventLogs(),
						EventLogTypeDef.FAC_CHG, userId, facility.getFpId(),
						facility.getFacilityId(), trans);
				if (cl) {
					try {
						internalFacilitySubmit(facility,
								"UP" + facility.getFacilityId());
					} catch (Exception e) {
						logger.warn(
								"Updating COREDB failed; Continue updating STARS2 DB; facility ID = "
										+ facility.getFacilityId()
										+ e.getMessage(), e);
						data = null;
					}
				}
			}

		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
		} catch (Exception e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans,
					new DAOException(e.getMessage(), e));
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		loggerDebug("modifyFacilityInternal completed; facility = "
				+ facility.getFacilityId());
		return data;
	}

	/**
	 * Just modify data in the fp_facility table.
	 * 
	 * @param Facility
	 *            Facility to modify
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyFacilityTable(Facility facility) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		try {
			facilityDAO.modifyFacility(facility);
		} catch (DAOException e) {
			cancelTransaction("facility=" + facility.getFacilityId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * Retrieves the Facility for the given facility id complete with all of its
	 * EmissionUnits.
	 * 
	 * @param Integer
	 *            Id of the facility to be retrieved.
	 * @return Facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Facility retrieveFacility(Integer fpId) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();

		return retrieveFacility(facilityDAO, fpId);
	}

	/**
	 * Given a facility ID, retrieves the latest version of the Facility
	 * complete with all of its EmissionUnits.
	 * 
	 * @param String
	 *            Facility ID
	 * @return Facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Facility retrieveFacility(String facilityId) throws DAOException {
		return facilityDAO().retrieveFacility(facilityId);
	}

	public Facility retrieveFacility(String facilityId, Transaction trans)
			throws DAOException {
		return facilityDAO(trans).retrieveFacility(facilityId);
	}

	/**
	 * Given a facility ID, retrieves the latest version of the Facility
	 * complete with all of its EmissionUnits.
	 * 
	 * @param String
	 *            Facility ID
	 * @return Facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Facility retrieveFacility(String facilityId, boolean staging)
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(staging);
		return facilityDAO.retrieveFacility(facilityId);
	}

	/**
	 * Given a facility ID, retrieves only the data in the fp_facility table for
	 * the latest version of the facility.
	 * 
	 * @param String
	 *            Facility ID
	 * @return Facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Facility retrieveFacilityTable(String facilityId)
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO();
		return facilityDAO.retrieveFacilityTable(facilityId);
	}

	/**
	 * Retrieves all versions of the Facility for the given facility id
	 * 
	 * @param String
	 *            Id of the facility to be retrieved.
	 * @return Facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityVersion[] retrieveAllFacilityVersions(String facilityId)
			throws DAOException {
		return facilityDAO().retrieveAllFacilityVersions(facilityId);
	}

	/**
	 * Retrieves all versions of the Facility for the given facility id; This
	 * method is intended to be used by migration.
	 * 
	 * @param String
	 *            Id of the facility to be retrieved.
	 * @return Facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityVersion[] retrieveAllMigFacilityVersions(String facilityId)
			throws DAOException {
		return facilityDAO().retrieveAllMigFacilityVersions(facilityId);
	}

	private void setLastSubmission(Facility facility) {
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			facility.setLastSubmissionType("I");
		} else {
			facility.setLastSubmissionType("E");
			Integer nextVersion = (null == facility.getLastSubmissionVersion()) ? 1
						: facility.getLastSubmissionVersion() + 1;
			facility.setLastSubmissionVersion(nextVersion);
		}
	}

	private void resetFacilityValidAndSubmit(Facility facility,
			Transaction trans) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		try {
			facility.setValidated(false);
			facility.setSubmitted(false);
			setLastSubmission(facility);

			facilityDAO.modifyFacility(facility);

			ApplicationDAO applicationDAO = applicationDAO(trans);
			applicationDAO.setActiveApplicationsValidatedFlag(
					facility.getFpId(), false);
			EmissionsReportDAO reportDAO = emissionsReportDAO(trans);
			reportDAO.setActiveEmissionsReportsValidatedFlag(facility.getFpId(), false);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
	}

	private void createFieldAudLog(Facility facility, String fldAudLogId,
			String attrCd, String audLogOrigVal, String audLogNewVal,
			int userId, Transaction trans) throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			FieldAuditLog[] auditLog = new FieldAuditLog[1];
			auditLog[0] = new FieldAuditLog(attrCd, fldAudLogId, audLogOrigVal,
					audLogNewVal);

			infraDAO.createFieldAuditLogs(facility.getFacilityId(),
					facility.getName(), userId, auditLog);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
	}
	
	public void createFieldAuditLog(FieldAuditLog[] auditLog, int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			infraDAO.createFieldAuditLogs(auditLog[0].getFacilityId(), auditLog[0].getFacilityName(),
					userId, auditLog);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
	}

	private void createEventLog(Integer fpId, String facilityId,
			String eventType, int userId, String note, Transaction trans)
			throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);

		EventLog eventLog = new EventLog();
		eventLog.setFpId(fpId);
		eventLog.setFacilityId(facilityId);
		eventLog.setEventTypeDefCd(eventType);
		eventLog.setDate(new Timestamp(System.currentTimeMillis()));
		eventLog.setUserId(userId);
		eventLog.setNote(note);
		facilityDAO.createEventLog(eventLog);
		// FacilityHelper facHelper = new FacilityHelper();
		facilityHelper.createEventPortalAlert(eventLog);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer createRelationShip(Integer fromFpNodeId, Integer toFpNodeId,
			Integer fpId, String audLogOrigVal, String audLogNewVal, int userId)
			throws DAOException {
		return createRelationShip(fromFpNodeId, toFpNodeId, 1f, fpId,
				audLogOrigVal, audLogNewVal, userId);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer createRelationShip(Integer fromFpNodeId, Integer toFpNodeId,
			float flowFactor, Integer fpId, String audLogOrigVal,
			String audLogNewVal, int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		Integer tempFromId = fromFpNodeId;
		Integer tempToId = toFpNodeId;
		Integer ret = fpId;

		try {
			Facility newFacility = copyFacilityProfile(fpId, new Timestamp(
					System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to create relationship.");
				throw e;
			}

			if (!newFacility.getFpId().equals(fpId)) {
				ret = newFacility.getFpId();
				tempFromId = newFacility.getCopyOnChangeFpNodeIds().get(
						fromFpNodeId);
				tempToId = newFacility.getCopyOnChangeFpNodeIds().get(
						toFpNodeId);
			}

			createRelationShip(tempFromId, tempToId, flowFactor, trans);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				createFieldAudLog(newFacility, newFacility.getFacilityId(),
						"asso", audLogOrigVal, audLogNewVal, userId, trans);
			}

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + fpId, trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	private void createRelationShip(Integer fromFpNodeId, Integer toFpNodeId,
			Transaction trans) throws DAOException {

		FacilityDAO facilityDAO = facilityDAO(trans);
		try {
			facilityDAO.createRelationShip(fromFpNodeId, toFpNodeId);

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
	}

	public void createRelationShip(Integer fromFpNodeId, Integer toFpNodeId,
			Float flowFactor, Transaction trans) throws DAOException {

		FacilityDAO facilityDAO = facilityDAO(trans);
		try {
			facilityDAO
					.createRelationShip(fromFpNodeId, toFpNodeId, flowFactor);

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer removeRelationShip(Integer fromFpNodeId, Integer toFpNodeId,
			Integer fpId, String audLogOrigVal, String audLogNewVal, int userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		Integer tempFromId = fromFpNodeId;
		Integer tempToId = toFpNodeId;
		Integer ret = fpId;

		try {
			Facility newFacility = copyFacilityProfile(fpId, new Timestamp(
					System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to remove relationship.");
				throw e;
			}

			if (!newFacility.getFpId().equals(fpId)) {
				ret = newFacility.getFpId();
				tempFromId = newFacility.getCopyOnChangeFpNodeIds().get(
						fromFpNodeId);
				tempToId = newFacility.getCopyOnChangeFpNodeIds().get(
						toFpNodeId);
			}

			removeRelationShip(tempFromId, tempToId, trans);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				createFieldAudLog(newFacility, newFacility.getFacilityId(),
						"asso", audLogOrigVal, audLogNewVal, userId, trans);
			}

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + fpId, trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	private void removeRelationShip(Integer fromFpNodeId, Integer toFpNodeId,
			Transaction trans) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		try {
			facilityDAO.setTransaction(trans);

			facilityDAO.removeRelationShip(fromFpNodeId, toFpNodeId);

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public EmissionUnit createEmissionUnit(EmissionUnit emissionUnit, int userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		EmissionUnit ret = null;

		try {
			Facility newFacility = copyFacilityProfile(emissionUnit.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to create emission unit.");
				throw e;
			}

			if (!newFacility.getFpId().equals(emissionUnit.getFpId())) {
				emissionUnit.setFpId(newFacility.getFpId());
			}
			ret = createEmissionUnit(emissionUnit, trans);

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + emissionUnit.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	private EmissionUnit createEmissionUnit(EmissionUnit emissionUnit,
			Transaction trans) throws DAOException {
		EmissionUnit ret = null;
		EmissionUnitTypeDAO euTypeDAO = null;
		FacilityDAO facilityDAO = facilityDAO(trans);

		if (emissionUnit.getEmissionUnitTypeCd() != null
				&& emissionUnit.getEmissionUnitTypeCd().length() > 0) {
			euTypeDAO = getEUTypeDAO(emissionUnit.getEmissionUnitTypeCd(),
					trans, true);
		}
		try {
			/*
			 * FacilityNode facilityNode = facilityDAO.createFacilityNode(
			 * emissionUnit.getFpId(), emissionUnit.getFpNodeId(),
			 * emissionUnit.getCorrelationId());
			 * 
			 * emissionUnit.setFpNodeId(facilityNode.getFpNodeId());
			 */

			if (Utility.isNullOrEmpty(emissionUnit.getEpaEmuId())) {
				String newEuId = facilityDAO
						.retrieveNextEmissionUnitId(emissionUnit);
				emissionUnit.setEpaEmuId(newEuId);
			}
			ret = facilityDAO.createEmissionUnit(emissionUnit);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				for (EuEmission emission : ret.getEuEmissions()) {
					emission.setEmuId(ret.getEmuId());
					facilityDAO.createEuEmission(emission);
				}
			}

			// Get the EU again because the EU ID already change
			ret = facilityDAO.retrieveEmissionUnit(emissionUnit.getEmuId());

			// create emission unit type
			EmissionUnitType euType = emissionUnit.getEmissionUnitType();
			euType.setEmuId(ret.getEmuId());
			if (euTypeDAO != null) {
				euTypeDAO.createEmissionUnitType(euType);
				if (EmissionUnitTypeDef.FUG.equals(euType.getEmissionUnitTypeCd()) && euType.isFugitiveLeaks()){
					for (Component component : euType.getComponents()){
						component.setEmuId(ret.getEmuId());
						euTypeDAO.addComponentCount(component);
					}
				}
			}

			// create emissionUnitReplacement
			for (EmissionUnitReplacement emissionUnitReplacement : emissionUnit
					.getEmissionUnitReplacements()) {
				emissionUnitReplacement.setEmuId(ret.getEmuId());
				facilityDAO
						.createEmissionUnitReplacement(emissionUnitReplacement);
			}

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}
	
	private EmissionUnit createEmissionUnitBasedOnPreviousVersion(EmissionUnit emissionUnit,
			Integer oldFpId, Transaction trans) throws DAOException {
		EmissionUnit ret = null;
		EmissionUnitTypeDAO euTypeDAO = null;
		FacilityDAO facilityDAO = facilityDAO(trans);

		if (emissionUnit.getEmissionUnitTypeCd() != null
				&& emissionUnit.getEmissionUnitTypeCd().length() > 0) {
			euTypeDAO = getEUTypeDAO(emissionUnit.getEmissionUnitTypeCd(),
					trans, true);
		}
		try {
			/*
			 * FacilityNode facilityNode = facilityDAO.createFacilityNode(
			 * emissionUnit.getFpId(), emissionUnit.getFpNodeId(),
			 * emissionUnit.getCorrelationId());
			 * 
			 * emissionUnit.setFpNodeId(facilityNode.getFpNodeId());
			 */

			if (Utility.isNullOrEmpty(emissionUnit.getEpaEmuId())) {
				String newEuId = facilityDAO
						.retrieveNextEmissionUnitIdBasedOnPreviousVersion(emissionUnit, oldFpId);
				emissionUnit.setEpaEmuId(newEuId);
			}
			ret = facilityDAO.createEmissionUnit(emissionUnit);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				for (EuEmission emission : ret.getEuEmissions()) {
					emission.setEmuId(ret.getEmuId());
					facilityDAO.createEuEmission(emission);
				}
			}

			// Get the EU again because the EU ID already change
			ret = facilityDAO.retrieveEmissionUnit(emissionUnit.getEmuId());

			// create emission unit type
			EmissionUnitType euType = emissionUnit.getEmissionUnitType();
			euType.setEmuId(ret.getEmuId());
			if (euTypeDAO != null) {
				euTypeDAO.createEmissionUnitType(euType);
			}

			// create emissionUnitReplacement
			for (EmissionUnitReplacement emissionUnitReplacement : emissionUnit
					.getEmissionUnitReplacements()) {
				emissionUnitReplacement.setEmuId(ret.getEmuId());
				facilityDAO
						.createEmissionUnitReplacement(emissionUnitReplacement);
			}

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}


	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String modifyEmissionUnit(EmissionUnit emissionUnit, int userId)
			throws DAOException {
		String newEuDispalyId = "";

		Transaction trans = TransactionFactory.createTransaction();
		try {
			newEuDispalyId = modifyEmissionUnit(emissionUnit, userId, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("emuId=" + emissionUnit.getEmuId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return newEuDispalyId;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String modifyEmissionUnit(EmissionUnit emissionUnit, int userId,
			Transaction trans) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		EmissionUnitTypeDAO euTypeDAO = getEUTypeDAO(
				emissionUnit.getEmissionUnitTypeCd(), trans, true);

		String newEuDisplayId = "";

		// Get the current Emission Type first
		int emuId = emissionUnit.getEmuId();

		try {
			Facility newFacility = copyFacilityProfile(emissionUnit.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId, trans);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to modify Emission Unit "
								+ emissionUnit.getCompanyId());
				throw e;
			}

			if (!newFacility.getFpId().equals(emissionUnit.getFpId())) {
				emissionUnit.setFpId(newFacility.getFpId());
				Integer tempId = newFacility.getCopyOnChangeEuIds().get(
						emissionUnit.getEmuId());
				emissionUnit.setEmuId(tempId);
				tempId = newFacility.getCopyOnChangeFpNodeIds().get(
						emissionUnit.getFpNodeId());
				emissionUnit.setFpNodeId(tempId);
				emissionUnit.setLastModified(1);
			}

			// No need to do any thing if the EmissionUnit Type does not be
			// changed
			boolean isTheSameEuType = isTheSameEmissionUnitTypeInDb(
					emissionUnit, trans);

			if (!isTheSameEuType) {
				String newEuId = facilityDAO
						.retrieveNextEmissionUnitId(emissionUnit);
				emissionUnit.setEpaEmuId(newEuId);

				String oldEuId = facilityDAO
						.retrieveEmissionUnitDisplayId(emissionUnit.getEmuId());

				// must create field audit log for each changed EU id
				if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
					List<FieldAuditLog> falList = new ArrayList<FieldAuditLog>();

					FieldAuditLog fal = null;

					if (emissionUnit.getCorrEpaEmuId() != null) {
						String oldEuTypeCd = facilityDAO
								.retrieveEmissionUnitTypeCd(emissionUnit
										.getEmuId());

						fal = new FieldAuditLog(
								"euty",
								newEuId,
								"["
										+ oldEuId
										+ "] "
										+ EmissionUnitTypeDef.getData()
												.getItems()
												.getItemDesc(oldEuTypeCd),
								"["
										+ newEuId
										+ "] "
										+ EmissionUnitTypeDef
												.getData()
												.getItems()
												.getItemDesc(
														emissionUnit
																.getEmissionUnitTypeCd()),
								emissionUnit.getCorrEpaEmuId());
						falList.add(fal);
					}

					infraDAO.createFieldAuditLogs(newFacility.getFacilityId(),
							newFacility.getName(), userId,
							falList.toArray(new FieldAuditLog[0]));

				}
			}

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				if (emissionUnit.getOperatingStatusCd().equals(
						EuOperatingStatusDef.SD)) {
					boolean change = true;
					for (EmissionUnit tempEU : newFacility.getEmissionUnits()
							.toArray(new EmissionUnit[0])) {
						if (tempEU.getEmuId().equals(emissionUnit.getEmuId())) {
							if (tempEU.getOperatingStatusCd().equals(
									EuOperatingStatusDef.SD)) {
								change = false;
							}
							break;
						}
					}

					if (change) {
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("Task Name", "Emissions Unit Shutdown");
						String notes;
						if (PermitClassDef.TV.equals(newFacility
								.getPermitClassCd())) {
							notes = "Emission Unit: "
									+ emissionUnit.getEpaEmuId()
									+ " marked shut down. "
									+ "If an EU is marked as permanently shutdown, "
									+ "the responsible party may have to reapply for a permit to install (PTI) "
									+ "for the EU prior to operating again.  Additionally, the responsible party "
									+ "must submit a notification of shutdown or request a permit modification.  "
									+ "Please contact your DO/LAA representative for assistance";
						} else {
							notes = "Emission Unit: "
									+ emissionUnit.getEpaEmuId()
									+ " marked shut down. "
									+ "Any EU marked as permanently shutdown may result in a revoked permit "
									+ "or portion of a permit.  Additionally, the responsible party may have to "
									+ "reapply for a permit to install (PTI) for the EU prior to operating again";
						}
						data.put("Notes", notes);
						performShutdownToDo(newFacility, userId, data, trans);

						// mark permit EUs as terminated for shutdown EU
						try {
							PermitService permitBO = ServiceFactory
									.getInstance().getPermitService();
							permitBO.terminatePermitEUsForShutdownEU(
									newFacility, emissionUnit);
						} catch (ServiceFactoryException e) {
							logger.error(
									"Exception while attempting to mark permit EUs as terminated for EU  "
											+ emissionUnit.getCompanyId(), e);
						} catch (RemoteException e) {
							logger.error(
									"Exception while attempting to mark permit EUs as terminated for EU  "
											+ emissionUnit.getCompanyId(), e);
						}

						PermitDAO permitDAO = permitDAO();
						emissionUnit
								.setActivePermits(permitDAO
										.searchEuPermits(emissionUnit
												.getCorrEpaEmuId()));
					} else {
						emissionUnit.setActivePermits(new ArrayList<Permit>());
					}

				} else {
					if (emissionUnit.getOperatingStatusCd().equals(
							EuOperatingStatusDef.OP)) {
						if (newFacility.getOperatingStatusCd().equals(
								OperatingStatusDef.NI)
								|| newFacility.getOperatingStatusCd().equals(
										OperatingStatusDef.IA)) {
							newFacility
									.setOperatingStatusCd(OperatingStatusDef.OP);
							facilityDAO.modifyFacility(newFacility);
							// add the last modified since facility is updated
							// later in this function
							newFacility.setLastModified(newFacility
									.getLastModified() + 1);
						}
					}
				}

				facilityDAO.modifyEmissionUnit(emissionUnit);

				// update emissions
				facilityDAO.removeEuEmissions(emissionUnit.getEmuId());
				for (EuEmission emission : emissionUnit.getEuEmissions()) {
					emission.setEmuId(emissionUnit.getEmuId());
					facilityDAO.createEuEmission(emission);
				}

				List<FieldAuditLog> falList = new ArrayList<FieldAuditLog>();
				for (FieldAuditLog fal : emissionUnit.getFieldAuditLogs()) {
					fal.setCorrEmuId(emissionUnit.getCorrEpaEmuId());
					falList.add(fal);
				}

				infraDAO.createFieldAuditLogs(newFacility.getFacilityId(),
						newFacility.getName(), userId,
						falList.toArray(new FieldAuditLog[0]));
				createFieldChangeEventLog(emissionUnit.getFieldEventLogs(),
						EventLogTypeDef.FAC_CHG, userId, newFacility.getFpId(),
						newFacility.getFacilityId(), trans);
			} else {
				facilityDAO.modifyEmissionUnit(emissionUnit);
			}
			
			// modify the emission unit type in database
			if (euTypeDAO != null) {
				// reset emu id in case of change
				EmissionUnitType euType = emissionUnit.getEmissionUnitType();
				euType.setEmuId(emissionUnit.getEmuId());
				euType.setEmissionUnitTypeCd(emissionUnit.getEmissionUnitTypeCd());
//				euTypeDAO.modifyEmissionUnitType(emissionUnit.getEmissionUnitType());
				modifyEmissionUnitType(euType, euTypeDAO);
			}

			// update emissionUnitReplacement
			facilityDAO.removeEmissionUnitReplacements(emissionUnit.getEmuId());
			for (EmissionUnitReplacement emissionUnitReplacement : emissionUnit
					.getEmissionUnitReplacements()) {
				emissionUnitReplacement.setEmuId(emissionUnit.getEmuId());
				facilityDAO
						.createEmissionUnitReplacement(emissionUnitReplacement);
			}

			resetFacilityValidAndSubmit(newFacility, trans);
			newEuDisplayId = facilityDAO.retrieveEmissionUnitDisplayId(emuId);

		} catch (DAOException e) {
			throw e;
		}

		return newEuDisplayId;
	}

	/**
	 * Retrieves all EmissionUnits for the given facility id including its
	 * "relationships".
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested EmissionUnits
	 *            belong.
	 * @return EmissionUnit[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public EmissionUnit[] retrieveFacilityEmissionUnits(Integer facilityId)
			throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		return facilityDAO.retrieveFacilityEmissionUnits(facilityId);
	}

	/**
	 * Retrieve past versions of an emission unit (i.e. emission unit data for
	 * historical facility details, but not the current profile).
	 * 
	 * @param corrEpaEmuId
	 *            the id to uniquely identify this EU
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public EmissionUnit[] retrieveEmissionUnitsFromPastProfiles(
			int corrEpaEmuId, String facilityId) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		return facilityDAO.retrieveEmissionUnitsFromPastProfiles(corrEpaEmuId,
				facilityId);
	}
	
	@Override
	public EmissionUnit retrieveEmissionUnitFromCurrentProfile(
			int corrEpaEmuId, String facilityId) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		return facilityDAO.retrieveEmissionUnitFromCurrentProfile(corrEpaEmuId,
				facilityId);
	}
	

	private final void moveIdChangedLogToFacilityNote(Transaction trans,
			int fpId, String facilityId, String note) throws DAOException {
		Timestamp enteredDate = new Timestamp(System.currentTimeMillis());
		FacilityNote changedIdNote = new FacilityNote();
		changedIdNote.setNoteTxt(note);
		changedIdNote.setDateEntered(enteredDate);
		changedIdNote.setUserId(InfrastructureDefs.getCurrentUserId());
		changedIdNote.setNoteTypeCd(NoteType.IDCH);
		changedIdNote.setFpId(fpId);
		changedIdNote.setFacilityId(facilityId);
		createFacilityNote(changedIdNote, trans);
	}

	/**
	 * Retrieves only facility level data
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested EmissionUnits
	 *            belong.
	 * @return Facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Facility retrieveFacilityData(Integer fpId) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		return facilityDAO.retrieveFacilityData(fpId);
	}

	/**
	 * @param facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void retrieveFacilityAddresses(Facility facility)
			throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		facility.setAddresses(facilityDAO.retrieveFacilityAddresses(facility
				.getFpId()));
	}

	@Override
	public void retrieveFacilityAddressesFromStaging(Facility facility)
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO();
		facility.setAddresses(facilityDAO.retrieveFacilityAddresses(facility
				.getFpId()));
	}

	/**
	 * @param facility
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void updateFacilityAddresses(Integer fpId, Address address)
			throws DAOException {
		InfrastructureDAO infraDAO = getInfraDAO();
		FacilityDAO facilityDAO = getFacilityDAO();
		boolean flag = true;
		Address newAddr = null;

		if (SystemPropertyDef.getSystemPropertyValueAsBoolean("APP_PLSS_AUTO_REPLICATION", true)){
			autoFillNullLocationData(address);
		}

		if (address.isNewObject()) {
			try {
				newAddr = infraDAO.createAddress(address);
				facilityDAO.addFacilityAddress(fpId, newAddr.getAddressId());
				infraDAO.updateAddressEndDate(fpId);
			} catch (DAOException dex) {
				flag = false;
				logger.error(dex);
			}

		} else {
			address.setEndDate(null);
			flag = infraDAO.modifyAddress(address);
			infraDAO.updateAddressEndDate(fpId);
		}
		
		// update the facility district if this is the oldest address
		try {
			facilityDAO.updateFacilityDistrict(fpId, address);
		} catch (DAOException dex) {
			flag = false;
			logger.error(dex);
		}	

		if (!flag) {
			String s = "updateFacilityAddresses failed for fpId " + fpId
					+ " and AddressId " + address.getAddressId();
			logger.error(s);
			throw new DAOException(s);
		}

	}

	private void autoFillNullLocationData(Address address) {
		boolean isNullAddress1 = Utility.isNullOrEmpty(address
				.getAddressLine1()) || address.getAddressLine1().trim() == "";
		boolean isNullCity = Utility.isNullOrEmpty(address.getCityName())
				|| address.getCityName().trim() == "";
		boolean isNullZip = Utility.isNullOrEmpty(address.getZipCode())
				|| address.getZipCode().trim() == "";

		if (isNullAddress1) {
			fillAddress1WithPlss(address);
		}

		if (isNullCity) {
			fillCityWithCounty(address);
		}

		if (isNullZip) {
			fillZipFromCounty(address);
		}
	}

	private void fillAddress1WithPlss(Address address) {
		address.setAddressLine1(String.format("Section %s, %s, %s",
				address.getSection(), address.getTownship(), address.getRange()));
	}

	private void fillCityWithCounty(Address address) {
		String cityName = County.getData().getItems()
				.getItemDesc(address.getCountyCd())
				+ " County";
		address.setCityName(cityName);
	}

	private void fillZipFromCounty(Address address) {
		String zip = County.getMappingZip(address.getCountyCd());
		address.setZipCode(zip);
		address.setZipCode5(zip);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public EmissionProcess createEmissionProcess(
			EmissionProcess emissionProcess, int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		EmissionProcess ret = null;

		try {
			Facility newFacility = copyFacilityProfile(
					emissionProcess.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to create emission process.");
				throw e;
			}

			if (!newFacility.getFpId().equals(emissionProcess.getFpId())) {
				emissionProcess.setFpId(newFacility.getFpId());
				Integer newEmuId = newFacility.getCopyOnChangeEuIds().get(
						emissionProcess.getEmissionUnitId());
				emissionProcess.setEmissionUnitId(newEmuId);
			}
			ret = createEmissionProcess(emissionProcess, trans);

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + emissionProcess.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	private EmissionProcess createEmissionProcess(
			EmissionProcess emissionProcess, Transaction trans)
			throws DAOException {
		EmissionProcess ret = null;
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			FacilityNode facilityNode = facilityDAO.createFacilityNode(
					emissionProcess.getFpId(), emissionProcess.getFpNodeId(),
					emissionProcess.getCorrelationId());

			emissionProcess.setFpNodeId(facilityNode.getFpNodeId());

			if (Utility.isNullOrEmpty(emissionProcess.getProcessId())) {
				String newEpId = facilityDAO
						.retrieveNextEmissonProcessId(emissionProcess);
				emissionProcess.setProcessId(newEpId);
			}

			ret = facilityDAO.createEmissionProcess(emissionProcess);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}
	
	private EmissionProcess createEmissionProcessBasedOnOldVersion(
			EmissionProcess emissionProcess, Integer oldFpId, Transaction trans)
			throws DAOException {
		EmissionProcess ret = null;
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			FacilityNode facilityNode = facilityDAO.createFacilityNode(
					emissionProcess.getFpId(), emissionProcess.getFpNodeId(),
					emissionProcess.getCorrelationId());

			emissionProcess.setFpNodeId(facilityNode.getFpNodeId());

			if (Utility.isNullOrEmpty(emissionProcess.getProcessId())) {
				String newEpId = facilityDAO
						.retrieveNextEmissonProcessIdBasedOnPreviousVersion(emissionProcess, oldFpId);
				emissionProcess.setProcessId(newEpId);
			}

			ret = facilityDAO.createEmissionProcess(emissionProcess);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeEmissionProcess(EmissionProcess emissionProcess,
			int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			Facility newFacility = copyFacilityProfile(
					emissionProcess.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to remove emission process.");
				throw e;
			}

			if (!newFacility.getFpId().equals(emissionProcess.getFpId())) {
				emissionProcess.setFpId(newFacility.getFpId());
				Integer tempId = newFacility.getCopyOnChangeFpNodeIds().get(
						emissionProcess.getFpNodeId());
				emissionProcess.setFpNodeId(tempId);
			}

			facilityDAO.removeEmissionProcess(emissionProcess);
			facilityDAO.removeFacilityNode(emissionProcess.getFpNodeId());

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + emissionProcess.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyEmissionProcess(EmissionProcess emissionProcess,
			int userId) throws DAOException {
		boolean ret = false;
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			Facility newFacility = copyFacilityProfile(
					emissionProcess.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to modify emission process.");
				throw e;
			}

			if (!newFacility.getFpId().equals(emissionProcess.getFpId())) {
				emissionProcess.setFpId(newFacility.getFpId());
				Integer tempId = newFacility.getCopyOnChangeEuIds().get(
						emissionProcess.getEmissionUnitId());
				emissionProcess.setEmissionUnitId(tempId);
				tempId = newFacility.getCopyOnChangeFpNodeIds().get(
						emissionProcess.getFpNodeId());
				emissionProcess.setFpNodeId(tempId);
				emissionProcess.setLastModified(1);
			}

			if (Utility.isNullOrEmpty(emissionProcess.getProcessId())) {
				emissionProcess.setProcessId(facilityDAO
						.retrieveNextEmissonProcessId(emissionProcess));
			}

			ret = facilityDAO.modifyEmissionProcess(emissionProcess);

			for (FacilityEmissionFlow emFlow : emissionProcess
					.getEpEmissionFlows()) {
				facilityDAO.modifyRelationShip(emFlow.getRelationship());
			}

			resetFacilityValidAndSubmit(newFacility, trans);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				emissionProcess.fieldChangeEventLog("feps",
						emissionProcess.getProcessId(),
						emissionProcess.getOldSccId(),
						emissionProcess.getSccId());
				createFieldChangeEventLog(emissionProcess.getFieldEventLogs(),
						EventLogTypeDef.FAC_CHG, userId, newFacility.getFpId(),
						newFacility.getFacilityId(), trans);
			}

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + emissionProcess.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * Retrieves the Emission Unit for the given emission unit id
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested Emission Unit
	 *            belong
	 * @param Integer
	 *            Emission Unit Id of the Emission Unit
	 * @return EmissionUnit
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */

	public EmissionUnit retrieveEmissionUnit(Integer emuId) throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO();

		return facilityDAO.retrieveEmissionUnit(emuId);
	}

	/**
	 * Retrieves the Emission Unit for the given facility id and EPA emission
	 * unit id
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested Emission Unit
	 *            belong
	 * @param String
	 *            EPA Emission Unit Id of the Emission Unit
	 * @return EmissionUnit
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */

	public EmissionUnit retrieveEmissionUnit(Integer facilityId, String epaEmuId)
			throws DAOException {
		// This is because the interface is called only in update
		FacilityDAO facilityDAO = getFacilityDAO();

		return facilityDAO.retrieveEmissionUnit(facilityId, epaEmuId);
	}

	/**
	 * Retrieves the Emission Process for the given emission process fp node id
	 * 
	 * @param Integer
	 *            FP Node Id of the emission process
	 * @return EmissionProcess
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */

	public EmissionProcess retrieveEmissionProcess(
			Integer emissionProcessFpnodeId) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		return facilityDAO.retrieveEmissionProcess(emissionProcessFpnodeId);
	}

	/**
	 * Retrieves all EmissionProcesses for the given facility id including their
	 * "relationships"..
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested EmissionProcesses
	 *            belong.
	 * @return List<EmissionProcess> Keyed by the fp_nodeId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<FacilityNode> retrieveFacilityEmissionProcesses(
			Integer facilityId) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		return facilityDAO.retrieveFacilityEmissionProcesses(facilityId);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ControlEquipment cloneControlEquipment(
			ControlEquipment controlEquipment, int userId) throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		ControlEquipment ret = null;

		try {
			Facility newFacility = copyFacilityProfile(
					controlEquipment.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to create control equipment.");
				throw e;
			}

			if (!newFacility.getFpId().equals(controlEquipment.getFpId())) {
				controlEquipment.setFpId(newFacility.getFpId());
			}
			
			//Setting Ceid to 0 for cloned CE, so DAO generates a new ce id.
			controlEquipment.setCeId(0);
			ret = createControlEquipment(controlEquipment, trans);

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + controlEquipment.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	private ControlEquipment createControlEquipment(
			ControlEquipment controlEquipment, Transaction trans)
			throws DAOException {
		ControlEquipment ret = null;

		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			FacilityNode facilityNode = facilityDAO.createFacilityNode(
					controlEquipment.getFpId(), controlEquipment.getFpNodeId(),
					controlEquipment.getCorrelationId());

			controlEquipment.setFpNodeId(facilityNode.getFpNodeId());

			if (Utility.isNullOrEmpty(controlEquipment.getControlEquipmentId())) {
				String newCeId = facilityDAO
						.retrieveNextEquipmentId(controlEquipment);
				controlEquipment.setControlEquipmentId(newCeId);
			}

			ret = facilityDAO.createControlEquipment(controlEquipment);

			for (PollutantsControlled pollutant : ret.getPollutantsControlled()) {
				pollutant.setFpNodeId(ret.getFpNodeId());
				facilityDAO.createPollutantsControlled(pollutant);
			}

			for (DataDetail dataDetail : ret.getCeDataDetails().toArray(
					new DataDetail[0])) {
				facilityDAO.addControlEquipmentData(ret.getFpNodeId(),
						dataDetail);
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}
	
	
	
	private ControlEquipment createControlEquipmentBasedOnPreviousVersion(
			ControlEquipment controlEquipment, Integer oldFpId, Transaction trans)
			throws DAOException {
		ControlEquipment ret = null;

		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			FacilityNode facilityNode = facilityDAO.createFacilityNode(
					controlEquipment.getFpId(), controlEquipment.getFpNodeId(),
					controlEquipment.getCorrelationId());

			controlEquipment.setFpNodeId(facilityNode.getFpNodeId());

			if (Utility.isNullOrEmpty(controlEquipment.getControlEquipmentId())) {
				String newCeId = facilityDAO
						.retrieveNextEquipmentIdBasedOnPreviousVersion(controlEquipment, oldFpId);
				controlEquipment.setControlEquipmentId(newCeId);
			}

			ret = facilityDAO.createControlEquipment(controlEquipment);

			for (PollutantsControlled pollutant : ret.getPollutantsControlled()) {
				pollutant.setFpNodeId(ret.getFpNodeId());
				facilityDAO.createPollutantsControlled(pollutant);
			}

			for (DataDetail dataDetail : ret.getCeDataDetails().toArray(
					new DataDetail[0])) {
				facilityDAO.addControlEquipmentData(ret.getFpNodeId(),
						dataDetail);
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyControlEquipment(ControlEquipment controlEquipment,
			int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		boolean ret = false;

		try {
			Facility newFacility = copyFacilityProfile(
					controlEquipment.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to modify control equipment.");
				throw e;
			}

			if (!newFacility.getFpId().equals(controlEquipment.getFpId())) {
				controlEquipment.setFpId(newFacility.getFpId());
				Integer tempId = newFacility.getCopyOnChangeFpNodeIds().get(
						controlEquipment.getFpNodeId());
				controlEquipment.setFpNodeId(tempId);
				controlEquipment.setLastModified(1);
			}

			boolean isTheSameType = isTheSameEquipmentTypeInDb(controlEquipment);

			if (!isTheSameType) {
				String newEquId = facilityDAO
						.retrieveNextEquipmentId(controlEquipment);

				controlEquipment.setControlEquipmentId(newEquId);
				String desc = getControlEquipmentIdChangedLog(facilityDAO,
						trans, controlEquipment);

				controlEquipment.setDapcDesc(desc);
			}

			ret = facilityDAO.modifyControlEquipment(controlEquipment);

			facilityDAO.removePollutantsControlled(controlEquipment
					.getFpNodeId());

			for (PollutantsControlled pollutant : controlEquipment
					.getPollutantsControlled()) {
				pollutant.setFpNodeId(controlEquipment.getFpNodeId());
				facilityDAO.createPollutantsControlled(pollutant);
			}

			facilityDAO.removeControlEquipmentData(controlEquipment
					.getFpNodeId());
			for (DataDetail dataDetail : controlEquipment.getCeDataDetails()
					.toArray(new DataDetail[0])) {
				facilityDAO.addControlEquipmentData(
						controlEquipment.getFpNodeId(), dataDetail);
			}

			for (FacilityEmissionFlow emFlow : controlEquipment
					.getCeEmissionFlows()) {
				facilityDAO.modifyRelationShip(emFlow.getRelationship());
			}

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				infraDAO.createFieldAuditLogs(newFacility.getFacilityId(),
						newFacility.getName(), userId,
						controlEquipment.getFieldAuditLogs());
				List<FieldAuditLog> pollAudLogs = new ArrayList<FieldAuditLog>();
				for (PollutantsControlled pollutant : controlEquipment
						.getPollutantsControlled()) {
					/*
					 * We only audit log adding/deleting a pollutant for a
					 * Control equipment. If the user modify a pollutant, it is
					 * possible that user changes pollutant type of a pollutant.
					 * In this case, a pollutant is deleted and another one is
					 * added. The audit log length will be one snce we don't
					 * audit log any other attributes of pollutant.
					 */
					if (pollutant.isNewObject()) {
						pollAudLogs
								.add(new FieldAuditLog(
										"cepa",
										controlEquipment
												.getControlEquipmentId(),
										BaseDB.FLD_AUD_LOG_NO_VALUE,
										PollutantDef
												.getData()
												.getItems()
												.getItemDesc(
														pollutant
																.getPollutantCd())));
					} else if (pollutant.getFieldAuditLogs().length == 1) {
						FieldAuditLog fldAudLog = pollutant.getFieldAuditLogs()[0];
						if (!fldAudLog.getOriginalValue().equals(
								BaseDB.FLD_AUD_LOG_NO_VALUE)) {
							pollAudLogs.add(new FieldAuditLog("cepd",
									controlEquipment.getControlEquipmentId(),
									fldAudLog.getOriginalValue(),
									BaseDB.FLD_AUD_LOG_NO_VALUE));
							pollAudLogs.add(new FieldAuditLog("cepa",
									controlEquipment.getControlEquipmentId(),
									BaseDB.FLD_AUD_LOG_NO_VALUE, fldAudLog
											.getNewValue()));

						}
					}
				}
				infraDAO.createFieldAuditLogs(newFacility.getFacilityId(),
						newFacility.getName(), userId,
						pollAudLogs.toArray(new FieldAuditLog[0]));
				createFieldChangeEventLog(controlEquipment.getFieldEventLogs(),
						EventLogTypeDef.FAC_CHG, userId, newFacility.getFpId(),
						newFacility.getFacilityId(), trans);
			}

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + controlEquipment.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeControlEquipment(ControlEquipment controlEquipment,
			int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			Facility newFacility = copyFacilityProfile(
					controlEquipment.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to remove control equipment.");
				throw e;
			}

			if (!newFacility.getFpId().equals(controlEquipment.getFpId())) {
				controlEquipment.setFpId(newFacility.getFpId());
				Integer tempId = newFacility.getCopyOnChangeFpNodeIds().get(
						controlEquipment.getFpNodeId());
				controlEquipment.setFpNodeId(tempId);
			}

			facilityDAO.removePollutantsControlled(controlEquipment
					.getFpNodeId());
			facilityDAO.removeControlEquipmentData(controlEquipment
					.getFpNodeId());

			facilityDAO.removeControlEquipment(controlEquipment);

			int nodeId = controlEquipment.getFpNodeId();
			facilityDAO.removeRelationShip(nodeId, nodeId);
			facilityDAO.removeFacilityNode(nodeId);

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + controlEquipment.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public EgressPoint createEgressPoint(EgressPoint egressPoint, int userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		EgressPoint ret = null;

		try {
			Facility newFacility = copyFacilityProfile(egressPoint.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to create release point.");
				throw e;
			}

			if (!newFacility.getFpId().equals(egressPoint.getFpId())) {
				egressPoint.setFpId(newFacility.getFpId());
			}
			ret = createEgressPoint(egressPoint, trans);
			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + egressPoint.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	private EgressPoint createEgressPoint(EgressPoint egressPoint,
			Transaction trans) throws DAOException {
		EgressPoint ret = null;
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			FacilityNode facilityNode = facilityDAO.createFacilityNode(
					egressPoint.getFpId(), egressPoint.getFpNodeId(),
					egressPoint.getCorrelationId());

			egressPoint.setFpNodeId(facilityNode.getFpNodeId());

			if (Utility.isNullOrEmpty(egressPoint.getReleasePointId())) {
				String newRpId = facilityDAO
						.retrieveNextReleasePointId(egressPoint);
				egressPoint.setReleasePointId(newRpId);
			}

			ret = facilityDAO.createEgressPoint(egressPoint);


		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}
	
	private EgressPoint createEgressPointBasedOnPreviousVersion(EgressPoint egressPoint, Integer oldFpId,
			Transaction trans) throws DAOException {
		EgressPoint ret = null;
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			FacilityNode facilityNode = facilityDAO.createFacilityNode(
					egressPoint.getFpId(), egressPoint.getFpNodeId(),
					egressPoint.getCorrelationId());

			egressPoint.setFpNodeId(facilityNode.getFpNodeId());

			if (Utility.isNullOrEmpty(egressPoint.getReleasePointId())) {
				String newRpId = facilityDAO
						.retrieveNextReleasePointIdBasedOnPreviousVersion(egressPoint, oldFpId);
				egressPoint.setReleasePointId(newRpId);
			}

			ret = facilityDAO.createEgressPoint(egressPoint);

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyEgressPoint(EgressPoint egressPoint, int userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		boolean ret = false;

		try {
			Facility newFacility = copyFacilityProfile(egressPoint.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to modify release point.");
				throw e;
			}

			if (!newFacility.getFpId().equals(egressPoint.getFpId())) {
				egressPoint.setFpId(newFacility.getFpId());
				Integer tempId = newFacility.getCopyOnChangeFpNodeIds().get(
						egressPoint.getFpNodeId());
				egressPoint.setFpNodeId(tempId);
				egressPoint.setLastModified(1);
			}

			boolean isTheSameType = isTheSameReleasePointTypeInDb(egressPoint);

			if (!isTheSameType) {
				String newRpId = facilityDAO
						.retrieveNextReleasePointId(egressPoint);
				egressPoint.setReleasePointId(newRpId);

				String desc = getReleasePointIdChangedLog(facilityDAO, trans,
						egressPoint);
				egressPoint.setDapcDesc(desc);
			}

			ret = facilityDAO.modifyEgressPoint(egressPoint);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				infraDAO.createFieldAuditLogs(newFacility.getFacilityId(),
						newFacility.getName(), userId,
						egressPoint.getFieldAuditLogs());
				createFieldChangeEventLog(egressPoint.getFieldEventLogs(),
						EventLogTypeDef.FAC_CHG, userId, newFacility.getFpId(),
						newFacility.getFacilityId(), trans);
			}

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + egressPoint.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeEgressPoint(EgressPoint egressPoint, int userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {
			Facility newFacility = copyFacilityProfile(egressPoint.getFpId(),
					new Timestamp(System.currentTimeMillis()), userId);
			if (newFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to remove release point.");
				throw e;
			}

			if (!newFacility.getFpId().equals(egressPoint.getFpId())) {
				egressPoint.setFpId(newFacility.getFpId());
				Integer tempId = newFacility.getCopyOnChangeFpNodeIds().get(
						egressPoint.getFpNodeId());
				egressPoint.setFpNodeId(tempId);
			}

			int fpNodeId = egressPoint.getFpNodeId();

//			facilityDAO.removeEgressPointCems(fpNodeId);
			facilityDAO.removeEgressPoint(egressPoint);
			facilityDAO.removeFacilityNode(fpNodeId);

			resetFacilityValidAndSubmit(newFacility, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("fpId=" + egressPoint.getFpId(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * Retrieves all ControlEquipments for the given facility id including their
	 * "relationships".
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested ControlEquipments
	 *            belong.
	 * @return List<ControlEquipment> Keyed by fp_nodeId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<FacilityNode> retrieveFacilityControlEquipment(
			Integer facilityId) throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		return facilityDAO.retrieveFacilityControlEquipment(facilityId);
	}

	/**
	 * Retrieves all EgressPoints for the given facility id including their
	 * "relationships".
	 * 
	 * @param Integer
	 *            Id of the facility for which the requested EgressPoints
	 *            belong.
	 * @return List<FacilityNode> Keyed by fp_nodeId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<FacilityNode> retrieveFacilityEgressPoints(Integer facilityId)
			throws DAOException {
		FacilityDAO facilityDAO = facilityDAO();
		return facilityDAO.retrieveFacilityEgressPoints(facilityId);
	}

	// OWNERS
	public List<FacilityOwner> retrieveFacilityOwners(String facilityId)
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(false);

		return facilityDAO.retrieveFacilityOwners(facilityId);
	}

	public FacilityOwner retrieveFacilityOwner(String facilityId)
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(false);

		return facilityDAO.retrieveFacilityOwner(facilityId);
	}

	/**
	 * Modify a facility owner.
	 */
	public void modifyFacilityOwner(FacilityOwner oldOwner,
			FacilityOwner newOwner) throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = getFacilityDAO(false);
		try {
			facilityDAO.removeFacilityOwner(oldOwner);
			facilityDAO.addFacilityOwner(newOwner);

			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}

	}

	/**
	 * Add an owner to a facility.
	 */
	public void addFacilityOwner(FacilityOwner newOwner) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = getFacilityDAO(false);
		try {
			facilityDAO.addFacilityOwner(newOwner);

			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}

	}

	// CONTACTS
	/**
	 * Retrieves all facility contacts defined in the DB, for the given fp_id.
	 * 
	 * @return Contact[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public List<Contact> retrieveFacilityContacts(String facilityId)
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(false);

		return facilityDAO.retrieveFacilityContacts(facilityId);
	}

	public List<Contact> retrieveStagingFacilityContacts(String facilityId)
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(false);

		return facilityDAO.retrieveStagingFacilityContacts(facilityId);
	}

	@Override
	public List<Contact> retrieveAllContacts() throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ContactDAO contactDAO = contactDAO(trans);
		List<Contact> ret = new ArrayList<Contact>();
		try {
			ret = contactDAO.retrieveGlobalContacts();
			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			// Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	@Override
	public List<Contact> retrieveAllContacts(boolean staging)
			throws DAOException {
		ContactDAO contactDAO = getContactDAO(staging);
		List<Contact> ret = new ArrayList<Contact>();

		ret = contactDAO.retrieveGlobalContacts();

		return ret;
	}

	/**
	 * Retrieves active contacts or all contacts for the given facility id.
	 * 
	 * @param facilityId
	 * @param active
	 * @return
	 * @throws DAOException
	 */
	public List<Contact> retrieveFacilityActiveContacts(String facilityId,
			boolean active) throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(false);

		return facilityDAO.retrieveFacilityContacts(facilityId, active);
	}

	/**
	 * Retrieve active contacts defined in the DB for the given facility
	 */
	public List<Contact> retrieveActiveFacilityContacts(String facilityId,
			boolean staging) throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(staging);

		return facilityDAO.retrieveFacilityContacts(facilityId, true);
	}

	/**
	 * Retrieves all facility contacts defined in the DB, for the given facility
	 * ID.
	 * 
	 * @return Contact[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public List<Contact> retrieveFacilityContacts(String facilityId,
			boolean staging) throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(staging);
		if (!staging)
			return facilityDAO.retrieveFacilityContacts(facilityId);
		else
			return retrieveStagedContactsByFacilityId(facilityId);
	}

	@Override
	public List<Contact> retrieveStagedContactsByFacilityId(String facilityId)
			throws DAOException {
		ContactDAO contactDAO = getContactDAO(true);
		return contactDAO.retrieveStagedContactsByFacility(facilityId);
	}

	/**
	 * Retrieves all facility contacts defined in the DB, for the given facioity
	 * ID from staging if exists; otherwise from readonly DB.
	 * 
	 * @return Contact[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public List<Contact> retrieveAnyFacilityContacts(String facilityId)
			throws DAOException {
		List<Contact> ret;
		FacilityDAO facilityDAO = getFacilityDAO(true);
		ret = facilityDAO.retrieveFacilityContacts(facilityId);
		if (ret.size() == 0) {
			FacilityDAO readFacilityDAO = getFacilityDAO(false);
			ret = readFacilityDAO.retrieveFacilityContacts(facilityId);
		}

		return ret;
	}

	/**
	 * Retrieves all facilities matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facilities in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            facilityId either the exact ID, a wildcarded string or null.
	 * @param Integer
	 *            counyId the exact county ID or null.
	 * @param String
	 *            operatingStatusCd either the exact operating status CD, or
	 *            null.
	 * @param String
	 *            doLaaCd either the exact DO-LAA CD, or null.
	 * @param String
	 *            emissionReportCategoryCd either the exact
	 *            emissionReportCategoryCd, or null.
	 * @param String
	 *            permitClassCd either the exact permitClassCd, or null
	 * @param String
	 *            tvPermitStatusCd either the exact tvPermitStatusCd, or null
	 * 
	 * @return FacilityList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityList[] searchFacilities(String facilityName,
			String requestId, String companyName, Integer corePlaceId,
			String countyCd, String operatingStatusCd, String doLaaCd,
			String naicsCd, String permitClassCd, String tvPermitStatusCd,
			String address1, String city, String zip5, String portable,
			String portableGroupCd, boolean unlimitedSearch,
			String facilityTypeCd) throws DAOException {

		return facilityDAO().searchFacilities(facilityName, requestId,
				companyName, corePlaceId, countyCd, operatingStatusCd, doLaaCd,
				naicsCd, permitClassCd, tvPermitStatusCd, address1, city, zip5,
				portable, portableGroupCd, unlimitedSearch, facilityTypeCd);
	}

	/**
	 * Retrieves all facilities matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facilities in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            facilityId either the exact ID, a wildcarded string or null.
	 * @param String
	 *            operatingStatusCd either the exact operating status CD, or
	 *            null.
	 * @param String
	 *            permitClassCd either the exact permitClassCd, or null
	 * @return FacilityList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityEmissionUnit[] searchEuStatus(String facilityName,
			String facilityId, String facilityOperatingStatusCd,
			String euOperatingStatusCd, String doLaaCd, String permitClassCd,
			boolean unlimitedSearch) throws DAOException {

		return facilityDAO().searchEuStatus(facilityName, facilityId,
				facilityOperatingStatusCd, euOperatingStatusCd, doLaaCd,
				permitClassCd, unlimitedSearch);
	}

	/**
	 * Retrieves all facilities matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facilities in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            facilityId either the exact ID, a wildcarded string or null.
	 * @param Integer
	 *            counyId the exact county ID or null.
	 * @param String
	 *            doLaaCd either the exact DO-LAA CD, or null.
	 * 
	 * @return FacilityList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityList[] searchTvPermitFacilities(String facilityName,
			String facilityId, String countyCd, String doLaaCd,
			boolean unlimitedResults) throws DAOException {

		FacilityDAO facilityDAO = facilityDAO();

		Integer months = SystemPropertyDef.getSystemPropertyValueAsInteger("TvApplicationNoticeInterval", null);

		Calendar now = new GregorianCalendar();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);

		Calendar startDt = now;
		startDt.set(year, month + months, 1);

		Calendar endDt = now;
		endDt.set(year, month + months + 1, 1);
		Timestamp start = new Timestamp(startDt.getTimeInMillis());
		Timestamp end = new Timestamp(endDt.getTimeInMillis());

		return facilityDAO.searchTvPermitFacilities(facilityName, facilityId,
				countyCd, doLaaCd, start, end, unlimitedResults);
	}

	/**
	 * Retrieves all facilities matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facilities in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            facilityId either the exact ID, a wildcarded string or null.
	 * @param Integer
	 *            counyId the exact county ID or null.
	 * @param String
	 *            doLaaCd either the exact DO-LAA CD, or null.
	 * 
	 * @return FacilityList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityList[] searchTvExpFacilities(String facilityName,
			String facilityId, String countyCd, String doLaaCd,
			boolean unlimitedResults) throws DAOException {

		FacilityDAO facilityDAO = facilityDAO();

		Integer months = SystemPropertyDef.getSystemPropertyValueAsInteger("TvExpirationNoticeInterval", null);

		Calendar now = new GregorianCalendar();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);

		Calendar startDt = now;
		startDt.set(year, month + months, 1);
		Calendar endDt = now;
		endDt.set(year, month + months + 1, 1);

		Timestamp start = new Timestamp(startDt.getTimeInMillis());
		Timestamp end = new Timestamp(endDt.getTimeInMillis());

		return facilityDAO.searchTvPermitFacilities(facilityName, facilityId,
				countyCd, doLaaCd, start, end, unlimitedResults);
	}

	/**
	 * Retrieves all facilities matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facilities in the DB.
	 * 
	 * @param FacilityHistList
	 *            Filters: name, description
	 * 
	 * @return FacilityHistList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityHistList[] searchFacilitiesHist(
			FacilityHistList searchFacility) throws DAOException {
		return facilityDAO().searchFacilitiesHist(searchFacility);
	}

	/**
	 * Retrieves facility data for the facility matching the specified
	 * facilityId where the specified date is after the facility detail begin
	 * date and before the end date (if there is one). If no match is made by
	 * date, the current facility detail is returned. If no facility with the
	 * specified facilityId is found, null is returned.
	 * 
	 * @param facilityId
	 * @param date
	 * 
	 * @return FacilityHistList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityHistList searchFacilitiesHist(String facilityId,
			Timestamp date) throws DAOException {
		FacilityHistList searchFacility = new FacilityHistList();
		searchFacility.setFacilityId(facilityId);
		FacilityHistList result = null;
		FacilityHistList current = null;
		FacilityHistList[] matches = facilityDAO().searchFacilitiesHist(
				searchFacility);
		for (FacilityHistList match : matches) {
			if (date != null
					&& match.getStartDate() != null
					&& match.getStartDate().before(date)
					&& (match.getEndDate() == null || match.getEndDate().after(
							date))) {
				result = match;
				break;
			}
			if (match.getVersionId() != null && match.getVersionId() == -1) {
				current = match;
			}
		}
		if (result == null) {
			result = current;
		}
		return result;
	}

	/**
	 * Creates a new EventLog object in the database.
	 * 
	 * @param el
	 *            EventLog The object to be created.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public EventLog createEventLog(EventLog el) throws DAOException {
		// FacilityHelper facHelper = new FacilityHelper();
		return facilityHelper.createEventLog(el);
	}

	/**
	 * Creates attributes value change event logs.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public EventLog[] createFieldChangeEventLog(FieldAuditLog[] fieldChgLog,
			String eventTypeCd, Integer userId, Integer fpId,
			String facilityId, Transaction trans) throws DAOException {
		FacilityDAO facDAO = facilityDAO(trans);
		List<EventLog> eventLogs = new ArrayList<EventLog>();
		EventLog[] ret = null;
		EventLog eventLog;
		String attribute;

		for (FieldAuditLog log : fieldChgLog) {
			eventLog = new EventLog();
			eventLog.setFpId(fpId);
			eventLog.setFacilityId(facilityId);
			eventLog.setEventTypeDefCd(eventTypeCd);
			eventLog.setDate(new Timestamp(System.currentTimeMillis()));
			eventLog.setUserId(InfrastructureDefs.getCurrentUserId());
			attribute = FieldAuditLogAttributeDef.getData().getItems()
					.getItemDesc(log.getAttributeCd());
			if (log.getUniqueId().equals("N/A")) {
				eventLog.setNote(attribute + " changed from ["
						+ log.getOriginalValue() + "] to [" + log.getNewValue()
						+ "]");
			} else {
				eventLog.setNote(attribute + " [" + log.getUniqueId()
						+ "] changed from [" + log.getOriginalValue()
						+ "] to [" + log.getNewValue() + "]");
			}
			eventLogs.add(eventLog);
		}

		ret = facDAO.createEventLogs(eventLogs.toArray(new EventLog[0]));

		/*
		 * for (EventLog el : eventLogs.toArray(new EventLog[0])) {
		 * FacilityHelper facHelper = new FacilityHelper();
		 * facHelper.createEventPortalAlert(el); }
		 */

		return ret;
	}

	/**
	 * Creates attributes value change to Do logs.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void createFieldChangeToDoLog(FieldAuditLog[] fieldChgLog,
			String facilityId, Integer fpId, Transaction trans,
			String facRoleCd, String taskName) throws DAOException {
		if (fieldChgLog.length == 0) {
			return;
		}

		FacilityDAO facDAO = facilityDAO(trans);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("Task Name", taskName);
		String notes;
		String attribute;
		Integer roleUserId = null;

		FacilityRole[] facilityRoles = facDAO.retrieveFacilityRoles(facilityId);
		for (FacilityRole tempRole : facilityRoles) {
			if (tempRole.getFacilityRoleCd().equals(facRoleCd)) {
				roleUserId = tempRole.getUserId();
				break;
			}
		}

		for (FieldAuditLog log : fieldChgLog) {
			attribute = FieldAuditLogAttributeDef.getData().getItems()
					.getItemDesc(log.getAttributeCd());
			if (log.getUniqueId().equals("N/A")) {
				notes = attribute + " changed from [" + log.getOriginalValue()
						+ "] to [" + log.getNewValue() + "]";
			} else {
				notes = attribute + " [" + log.getUniqueId()
						+ "] changed from [" + log.getOriginalValue()
						+ "] to [" + log.getNewValue() + "]";
			}

			data.put("Notes", notes);

			createToDoEntry(fpId, data, "N",
					new Timestamp(System.currentTimeMillis()), null,
					roleUserId, trans, CommonConst.GATEWAY_USER_ID);
		}
	}

	/**
	 * Retrieve EventLog objects in the database.
	 * 
	 * @param el
	 *            EventLog The object to filter.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public EventLog[] retrieveEventLogs(EventLog el) throws DAOException {
		return facilityDAO().retrieveEventLogs(el);
	}

	/**
	 * Retrieve roles for specified facility
	 * 
	 * @param facilityId
	 *            identifier for facility.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityRole[] retrieveFacilityRoles(String facilityId)
			throws DAOException {
		return facilityDAO().retrieveFacilityRoles(facilityId);
	}

	/**
	 * Retrieve facility roles for specified user
	 * 
	 * @param userId
	 *            .
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityRole[] retrieveFacilityRolesByUserId(Integer userId)
			throws DAOException {
		return facilityDAO().retrieveFacilityRolesByUserId(userId);
	}

	/**
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public LinkedHashMap<String, String> retrieveEventTypeCdAndDesc()
			throws DAOException {

		InfrastructureDAO infrastructureDAO = infrastructureDAO();

		SimpleDef[] sid = infrastructureDAO
				.retrieveDescAndCd("InfrastructureSQL.retrieveEventTypeCdAndDesc");

		LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
		for (SimpleDef temp : sid) {
			ret.put(temp.getDescription(), temp.getCode());
		}

		return ret;
	}

	private CopyOnChangeMaps createFacilityProfile(Facility newFacility,
			Transaction trans, boolean useObjectId) throws DAOException {
		CopyOnChangeMaps ret = null;
		CopyOnChangeMaps copyOnChangeMaps = new CopyOnChangeMaps();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		
		loggerDebug("createFacilityProfile entered; fpId = "
				+ newFacility.getFpId());

		try {
			// First create the base Facility, so we have a fp_id to work with.
			Facility tempFac = facilityDAO.createFacility(newFacility);

			int fpId = tempFac.getFpId();
			copyOnChangeMaps.fpId = fpId;

			// create API
			for (ApiGroup api : newFacility.getApis()) {
				api.setFpId(fpId);
				facilityDAO.createFacilityApi(api);
			}

			// create SIC codes
			for (String sicCd : newFacility.getSicCds()) {
				facilityDAO.addFacilitySIC(fpId, sicCd);
			}

			// create NAICS codes
			for (String naicsCd : newFacility.getNaicsCds()) {
				facilityDAO.addFacilityNAICS(fpId, naicsCd);
			}
			
			if (FacilityTypeDef.hasHCAnalysis(newFacility.getFacilityTypeCd())) {
				if (newFacility.getHydrocarbonPollutantList() != null) {
					for (HydrocarbonAnalysisPollutant hcPollutant : newFacility.getHydrocarbonPollutantList()) {
						if (!PollutantDef.TOTAL.equals(hcPollutant.getPollutantCd()) && (hcPollutant.getGas() != null
								|| hcPollutant.getOil() != null || hcPollutant.getProducedWater() != null)) {
							hcPollutant.setFpId(fpId);
							facilityDAO.addFacExtendedHCAnalysisPollutant(hcPollutant);
						}
					}
				}

				if (newFacility.getHydrocarbonAnalysisSampleDetail() != null) {
					newFacility.getHydrocarbonAnalysisSampleDetail().setFpId(fpId);
					facilityDAO.addFacHydrocarbonAnalysisSampleDetail(newFacility.getHydrocarbonAnalysisSampleDetail());
				}

				// decane properties
				if (null != newFacility.getDecaneProperties()) {
					newFacility.getDecaneProperties().setFpId(fpId);
					facilityDAO.createDecaneProperties(newFacility.getDecaneProperties());
				}
			}

			Address newAddr = null;
			for (Address addr : newFacility.getAddresses()) {
				addr.setAddressId(null);
				newAddr = infraDAO.createAddress(addr);
				facilityDAO.addFacilityAddress(fpId, newAddr.getAddressId());
				infraDAO.updateAddressEndDate(fpId);
			}
			
			// create Emission Units, processes, control equipment, egress
			// points
			copyOnChangeMaps = createEUCERP(newFacility, fpId,
					copyOnChangeMaps, useObjectId, null, null, trans);
					
			// create continuous monitors
			createContinuousMonitors(newFacility.getContinuousMonitorList(), copyOnChangeMaps, fpId);
			
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				// Create Notification Counties
				if (newFacility.getSecondNotifCountyCd() != null) {
					facilityDAO.addCountyNotification(tempFac.getFpId(),
							newFacility.getSecondNotifCountyCd());
				}

				// create entries...
				for (String mactCd : newFacility.getMactSubparts()) {
					facilityDAO.addFacilityMACTSubpart(fpId, mactCd);
				}

				// create Neshaps subpart entries...
				for (PollutantCompCode neshaps : newFacility
						.getNeshapsSubpartsCompCds()) {
					neshaps.setFpId(fpId);
					facilityDAO.addFacilityNeshapsSubpart(neshaps);
				}

				// create NSPS subpart entries...
				for (String nspsCd : newFacility.getNspsSubparts()) {
					facilityDAO.addFacilityNSPSSubpart(fpId, nspsCd);
				}

			}
			
			
			ret = copyOnChangeMaps;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			String error = "create facility detail failed for "
					+ newFacility.getFacilityId() + ". " + e.getMessage();
			logger.error(error, e);
			throw new DAOException(error, e);
		}

		loggerDebug("createFacilityProfile completed; fpId = "
				+ newFacility.getFpId());

		return ret;
	}

	private CopyOnChangeMaps createCloneFacilityProfile(Facility newFacility,
			Transaction trans, boolean useObjectId) throws DAOException {
		CopyOnChangeMaps ret = null;
		CopyOnChangeMaps copyOnChangeMaps = new CopyOnChangeMaps();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		loggerDebug("createFacilityProfile entered; fpId = "
				+ newFacility.getFpId());

		try {
			// First create the base Facility, so we have a fp_id to work with.
			Facility tempFac = facilityDAO.createClonedFacility(newFacility);

			int fpId = tempFac.getFpId();
			copyOnChangeMaps.fpId = fpId;

			// create API
			/*
			 * for (ApiGroup api : newFacility.getApis()) { api.setFpId(fpId);
			 * facilityDAO.createFacilityApi(api); }
			 * 
			 * // create SIC codes for (String sicCd : newFacility.getSicCds())
			 * { facilityDAO.addFacilitySIC(fpId, sicCd); }
			 */

			// create NAICS codes
			for (String naicsCd : newFacility.getNaicsCds()) {
				facilityDAO.addFacilityNAICS(fpId, naicsCd);
			}

			Address newAddr = null;
			for (Address addr : newFacility.getAddresses()) {
				addr.setAddressId(null);
				newAddr = infraDAO.createAddress(addr);
				facilityDAO.addFacilityAddress(fpId, newAddr.getAddressId());
				infraDAO.updateAddressEndDate(fpId);
			}

			/*
			 * if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) { //
			 * Create Notification Counties if
			 * (newFacility.getSecondNotifCountyCd() != null) {
			 * facilityDAO.addCountyNotification(tempFac.getFpId(),
			 * newFacility.getSecondNotifCountyCd()); }
			 * 
			 * // create entries... for (String mactCd :
			 * newFacility.getMactSubparts()) {
			 * facilityDAO.addFacilityMACTSubpart(fpId, mactCd); }
			 * 
			 * // create Neshaps subpart entries... for (PollutantCompCode
			 * neshaps : newFacility .getNeshapsSubpartsCompCds()) {
			 * neshaps.setFpId(fpId);
			 * facilityDAO.addFacilityNeshapsSubpart(neshaps); }
			 * 
			 * // create NSPS subpart entries... for (String nspsCd :
			 * newFacility.getNspsSubparts()) {
			 * facilityDAO.addFacilityNSPSSubpart(fpId, nspsCd); }
			 * 
			 * }
			 */

			// create Emission Units, processes, control equipment, egress
			// points
			boolean useSelected = true;
			boolean useSourceFacilityLatLong = true;
			
    		///delete any EU Emission of selected EUs so that it will not be cloned.
    		for (EmissionUnit tempEU: newFacility.getEmissionUnits()){
    		    if (tempEU.isSelected()) {
    		    	tempEU.setEuEmissions(new ArrayList<EuEmission>(0));
        		} else {
            		continue; //skip this eu, check the next eu in the EmissionUnits list
        		}
    		}
			
			copyOnChangeMaps = createEUCERP(newFacility, fpId,
					copyOnChangeMaps, useObjectId, useSelected, useSourceFacilityLatLong, trans);

			ret = copyOnChangeMaps;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			String error = "create facility detail failed for "
					+ newFacility.getFacilityId() + ". " + e.getMessage();
			logger.error(error, e);
			throw new DAOException(error, e);
		}

		loggerDebug("createFacilityProfile completed; fpId = "
				+ newFacility.getFpId());

		return ret;
	}

	private void createContEquipComp(ControlEquipment contEquip,
			Float flowFactor, HashMap<String, Integer> newContEquips,
			HashMap<String, Integer> newEgressPoints,
			HashMap<String, String> createdRel, Integer fromFpNodeId,
			Transaction trans) throws DAOException {
		Integer toFpNodeId;
		Integer fromFpNodeId1;
		String rel;
		try {
			toFpNodeId = newContEquips.get(contEquip.getControlEquipmentId());
			rel = fromFpNodeId.toString() + '-' + toFpNodeId.toString();
			if (createdRel.get(rel) == null) {
				if (flowFactor == null) {
					// logger.error("DENNIS to controlEquipmentId=" +
					// contEquip.getControlEquipmentId()+ " " + fromFpNodeId +
					// ":" + toFpNodeId);
					createRelationShip(fromFpNodeId, toFpNodeId, trans);
				} else {
					createRelationShip(fromFpNodeId, toFpNodeId, flowFactor,
							trans);
				}
				createdRel.put(rel, "Y");
			}
			ControlEquipment[] controlEquips1 = contEquip.getControlEquips()
					.toArray(new ControlEquipment[0]);
			fromFpNodeId1 = newContEquips
					.get(contEquip.getControlEquipmentId());
			for (ControlEquipment tempCE : controlEquips1) {
				FacilityEmissionFlow fef;
				fef = FacilityEmissionFlow.getEmissionFlow(
						contEquip.getCeEmissionFlows(),
						FacilityEmissionFlow.CE_TYPE,
						tempCE.getControlEquipmentId());
				Float ff = null;
				if (fef == null) {
					// This should not happen
					logger.error("Should have had a flowfactor for control equipment: correlationId="
							+ tempCE.getCorrelationId()
							+ ", fromFpNodeId="
							+ fromFpNodeId);
				} else {
					ff = fef.getFlowFactor();
				}
				createContEquipComp(tempCE, ff, newContEquips, newEgressPoints,
						createdRel, fromFpNodeId1, trans);
			}

			EgressPoint[] egressPoints1 = contEquip.getEgressPoints().toArray(
					new EgressPoint[0]);
			for (EgressPoint tempEGP : egressPoints1) {
				toFpNodeId = newEgressPoints.get(tempEGP.getReleasePointId());
				rel = fromFpNodeId1.toString() + '-' + toFpNodeId.toString();
				if (createdRel.get(rel) == null) {
					if (tempEGP.isFugitive()) {
						// no flow factor
						// logger.error("controlEquipmentId=" +
						// contEquip.getControlEquipmentId()+ ",EgressPointId="
						// + tempEGP.getReleasePointId()+ " " + fromFpNodeId +
						// ":" + toFpNodeId);
						createRelationShip(fromFpNodeId1, toFpNodeId, trans);
					} else {
						FacilityEmissionFlow fef;
						fef = FacilityEmissionFlow.getEmissionFlow(
								contEquip.getCeEmissionFlows(),
								FacilityEmissionFlow.STACK_TYPE,
								tempEGP.getReleasePointId());
						if (fef != null && fef.getFlowFactor() != null) {
							createRelationShip(fromFpNodeId1, toFpNodeId,
									fef.getFlowFactor(), trans);
						} else {
							createRelationShip(fromFpNodeId1, toFpNodeId, trans);
							// this should not happen
							logger.error("Should have had a flowfactor: fromNodeId="
									+ fromFpNodeId
									+ " toFpNodeId="
									+ toFpNodeId);
						}
					}
					createdRel.put(rel, "Y");
				}
			}
		} catch (DAOException e) {
			throw e;
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public DataDetail[] retrieveContEquipDataDetail(String contEquipType)
			throws DAOException {
		DataDetail[] ret = null;
		FacilityDAO facilityDAO = getFacilityDAO(false);

		ret = facilityDAO.retrieveContEquipDataDetail(contEquipType);

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public MultiEstabFacilityList[] retrieveMutliEstabFacilities(
			Facility facility) throws DAOException {
		
		MultiEstabFacilityList[] ret = null;
		
		// Federal SCSC ID is only updated by DAPC.
		FacilityDAO facilityDAO = facilityDAO();

		if (facility != null && facility.getFederalSCSCId() != null) {
			try {
				ret = facilityDAO.retrieveMultiiEstabFacilities(facility
						.getFederalSCSCId());
				if (ret.length == 1) {
					ret = null;
				}
			} catch (DAOException de) {
				logger.error("retrieveMutliEstabFacilities failed for " 
						+ facility.getFacilityId() + ". " + de.getMessage());
				throw de;
			} catch (Exception e) {
				logger.error("retrieveMutliEstabFacilities failed for " 
						+ facility.getFacilityId() + ". " + e.getMessage());
				throw new DAOException("retrieveMutliEstabFacilities failed for " 
						+ facility.getFacilityId() + ". " + e.getMessage(), e);
			}
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Facility retrieveMutliEstabFacilityAirProgInfo(String scscId)
			throws DAOException {
		Facility ret = null;
		MultiEstabFacilityList[] list = null;
		FacilityDAO facilityDAO = facilityDAO();
		try {
			list = facilityDAO.retrieveMultiiEstabFacilities(scscId);
			if (list == null || list.length == 0) {
				return null;
			}
			for (MultiEstabFacilityList mefl : list) {
				Facility f = retrieveFacility(mefl.getFacilityId());
				if (ret == null) {
					ret = f;
				} else {
					unionAirProgInfo(ret, f);
				}
			}
		} catch (DAOException de) {
			logger.error("retrieve mutliEstab facilities failed for SCSC ID "
					+ scscId + ". " + de.getMessage(), de);
		}
		return ret;
	}

	/**
	 * Set due date and jeopardy date for Process p to the values defined by the
	 * SLA for the service identified by serviceId or the values defined in the
	 * ProcessTemplate object pt if no SLA values are defined.
	 * 
	 * @param pt
	 *            ProcessTemplate
	 * @param serviceId
	 *            Service Id
	 * @param p
	 *            Process being constructed
	 * 
	 * @throws DAOException
	 * 
	 */
	private void setProcessDates(ProcessTemplate pt, Integer serviceId,
			WorkFlowProcess p, Timestamp startDt, Timestamp dueDt) {
		Timestamp jeoDt;

		// set start date to current time
		if (startDt == null || startDt.getTime() == 0) {
			startDt = new Timestamp(System.currentTimeMillis());
		}

		long startTime = startDt.getTime();
		if (dueDt == null || dueDt.getTime() == 0) {
			dueDt = new Timestamp(startTime
					+ pt.getExpectedDuration().longValue() * 1000);
			jeoDt = new Timestamp(startTime
					+ pt.getJeopardyDuration().longValue() * 1000);
		} else {
			long dueTime = (long) ((dueDt.getTime() - startTime) * .80);
			if (dueTime > 0) {
				jeoDt = new Timestamp(dueTime + startTime);
			} else {
				jeoDt = dueDt;
			}
		}

		p.setDueDt(dueDt);
		p.setJeopardyDt(jeoDt);
		p.setStartDt(startDt);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void createToDoEntry(Integer fpId, HashMap<String, String> data,
			String expedite, Timestamp startDt, Timestamp dueDt,
			Integer userId, Transaction trans, Integer createUserId)
			throws DAOException {
		Integer processTemplateId = 0;
		Integer externalId = 0;
		Integer serviceId = 0;
		ProcessFlow parentProcess = null;
		Activity parentActivity = null;

		WorkFlowDAO wfDao = workFlowDAO(trans);
		ProcessTemplate pt = wfDao.retrieveProcessTemplate(processTemplateId);

		// If we didn't get a ProcessTemplate, then thrown an Exception.

		if (pt == null) {
			throw new DAOException("No ProcessTemplate found for process "
					+ "template Id = " + processTemplateId.toString() + ".");
		}

		DataField[] ptFields = wfDao
				.retrieveDataFieldsForProcessTemplate(processTemplateId);

		// Now, create the "Process" and fill in all of the information.

		WorkFlowProcess p = new WorkFlowProcess();

		p.setProcessTemplateId(processTemplateId);
		p.setProcessTemplateNm(pt.getProcessTemplateNm());

		setProcessDates(pt, serviceId, p, startDt, dueDt);

		p.setFacilityId(fpId);
		p.setExternalId(externalId);
		p.setExpedite(expedite);
		p.setServiceId(serviceId);
		p.setProcessCd(pt.getProcessCd());
		p.setUserId(createUserId);

		// What we are going to do next is save the Process to the
		// database. This will get us a "process Id" key which we need to
		// create the Activity objects for this process. When we create
		// the Activity components, we will need to save those components
		// to the database. We are going to write everything to the database
		// in a single Transaction so that clean up in the event of an error
		// is easy and leaves the database in a consistent and usable state.

		ProcessFlow pf = null;

		// Save the "Process" to the database to get us a key, then use
		// the "Process" to make a ProcessFlow.

		wfDao.createProcess(p);
		pf = new ProcessFlow(p);

		pf.setParentProcess(parentProcess);
		pf.setParentActivity(parentActivity);

		// Add the DataFields and Transitions to the ProcessFlow.

		DataField[] processDfs = WorkFlowUtils.loadDataFields(pf, ptFields);
		try {
			pf.addDataFields(processDfs);
		} catch (Exception e) {
			logger.error(
					"createToDoEntry failed for fpId " + fpId + ". "
							+ e.getMessage(), e);
		}

		ArrayList<ProcessData> als = new ArrayList<ProcessData>();
		boolean updated = false;
		for (DataField d : processDfs) {
			ProcessData pd = new ProcessData();
			pd.setDataDetailId(d.getCustomDetailTypeId());
			pd.setDataTemplateId(d.getDataTemplateId());
			pd.setDataValue(d.getDataValue());
			pd.setProcessId(d.getProcessId());
			als.add(pd);
			updated = true;
		}
		if (updated) {
			wfDao.createProcessDatas(als.toArray(new ProcessData[0]));
		}

		// Read in all of the ActivityTemplates for this Process Template.
		// We will use these to construct the Activity objects.

		ActivityTemplate[] ats = wfDao
				.retrieveActivityTemplatesForProcessTemplate(processTemplateId);

		// If we don't have Activity Templates, we don't have Activities.

		if ((ats != null) && (ats.length > 0)) {
			int i;
			ActivityTemplate at;

			// For each (Activity Template) create a ProcessActivity object
			// (which is the "guts" of the Activity object). Save the
			// ProcessActivity to the database and then create an Activity
			// from all of its parts. Add the Activity to the ProcessFlow.

			for (i = 0; i < ats.length; i++) {
				at = ats[i];
				Integer atdId = at.getActivityTemplateDefId();
				ActivityTemplateDef atd = wfDao
						.retrieveActivityTemplateDef(atdId);

				ProcessActivity pa = new ProcessActivity();

				pa.setProcessId(p.getProcessId());
				pa.setLoopCnt(new Integer(1));
				pa.setActivityTemplateId(at.getActivityTemplateId());
				pa.setCurrent("Y");
				pa.setAggregate("N");
				pa.setPerformerTypeCd(atd.getPerformerTypeCd());
				pa.setNumberOfRetries(atd.getNumberOfRetries());
				pa.setRetryInterval(atd.getRetryInterval());
				String as;
				if (at.isInitTask()) {
					pa.setReadyDt(startDt);
					as = Activity.IN_PROCESS;
				} else {
					as = Activity.NOT_DONE;
				}
				pa.setActivityStatusCd(as);

				wfDao.createProcessActivity(pa);
				pa = wfDao.retrieveProcessActivity(pa.getProcessId(),
						pa.getActivityTemplateId());
				Activity a = new Activity(pf, pa, at, atd);
				pf.addActivity(a);
			}
		}

		// modify process Data

		DataField[] tdata = wfDao.retrieveDataFieldsForProcess(pf
				.getProcessId());

		if (data != null) {
			try {
				for (DataField pdt : tdata) {
					String dataName = pdt.getDataName();
					String dataValue = data.get(dataName);

					if (dataValue != null
							&& !dataValue.equalsIgnoreCase(pdt.getDataValue())) {
						pdt.setDataValue(dataValue);
						// If we changed something, save it back to the
						// database.
						wfDao.modifyDataField(pdt);
					}
				}

				// update Activity

				Activity a = pf.getActivity(0);
				Timestamp now = new Timestamp(System.currentTimeMillis());
				a.setUserId(userId);
				a.setStatusCd(Activity.IN_PROCESS);
				a.setStartDt(now);
				a.setReadyDt(now);
				a.setDueDt(a.getContainer().getDueDt());
				a.setJeopardyDt(pf.getProcess().getJeopardyDt());
				a.setProcessActivity(wfDao.modifyProcessActivity(a
						.getProcessActivity()));
			} catch (DAOException e) {
				throw e;
			}
		}
	}

	/**
	 * Creates a new DataDetail object in the database.
	 * 
	 * @param data
	 *            DetailDef The object to be created.
	 * 
	 * @param equipmentTypeCd
	 *            Equipement Type Code.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void createEquipDetailDef(DataDetail data, String equipmentTypeCd)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		// Do not set trans with this; It should not use staging database
		DetailDataDAO ddDao = detailDataDAO();

		// This thing is actually made up of a couple of different parts.
		try {
			ddDao.setTransaction(trans);

			DataDetail dd = ddDao.createDataDetail(data);
			ddDao.createEquipmentDataXref(equipmentTypeCd, dd.getDataDetailId());

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * Creates a new DataDetail object in the database.
	 * 
	 * @param data
	 *            DetailDef The object to be created.
	 * 
	 * @param equipmentTypeCd
	 *            Equipement Type Code.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyEquipDetailDef(DataDetail data, String equipmentTypeCd)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		// Do not set trans with this; It should not use staging database
		DetailDataDAO ddDao = detailDataDAO();

		// This thing is actually made up of a couple of different parts.
		try {
			ddDao.setTransaction(trans);

			ddDao.modifyDataDetail(data);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * Retrieves facility summary from core DB
	 * 
	 * @param String
	 *            core place ID
	 * @return FacilitySummary
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilitySummary retrieveFacilitySummaryFromCoreDB(String corePlaceId)
			throws DAOException {
		FacilitySummary facSummary = null;

		if (corePlaceId == null) {
			return facSummary;
		}

		try {
			PlaceService placeService = ServiceFactory.getInstance()
					.getPlaceService();
			facSummary = placeService.retrieveFacilitySummaryByID(corePlaceId);
		} catch (ServiceFactoryException sfe) {
			String error = "retrieve facility summary from CoreDB failed for "
					+ corePlaceId + ". " + sfe.getMessage();
			logger.error(error, sfe);
			throw new DAOException(error, sfe);
		} catch (RemoteException re) {
			String error = "retrieve facility summary from CoreDB failed for "
					+ corePlaceId + ". " + re.getMessage();
			logger.error(error, re);
			throw new DAOException(error, re);
		}

		return facSummary;
	}

	/**
	 * @param currentFacilityId
	 * @param newFacilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean changeFacilityId(String currentFacilityId,
			FacilityIdRef facIdRef, Facility currFacility, int userId)
			throws DAOException {
		boolean ret = false;

		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDao = facilityDAO(trans);
		InfrastructureDAO infraDao = infrastructureDAO(trans);

		String newFacilityId = getNewFacilityId(trans);

		try {
			Address currAddress = currFacility.getPhyAddr();

			currFacility.fieldChangeEventLog("fcit", "N/A",
					currAddress.getCityName(), facIdRef.getCityName(), true);
			CountyDef oldCounty = infraDao.retrieveCounty(currAddress
					.getCountyCd());
			CountyDef newCounty = infraDao.retrieveCounty(facIdRef
					.getCountyCd());
			currFacility.fieldChangeEventLog("fcnt", "N/A",
					oldCounty.getCountyNm(), newCounty.getCountyNm(), true);

			createFieldChangeEventLog(currFacility.getFieldEventLogs(),
					EventLogTypeDef.FAC_CHG, userId, currFacility.getFpId(),
					currFacility.getFacilityId(), trans);

			Timestamp newTime = new Timestamp(System.currentTimeMillis());
			currAddress.setEndDate(newTime);
			ret = infraDao.modifyAddress(currAddress);

			Address newAddress;
			currAddress.setBeginDate(newTime);
			currAddress.setEndDate(null);
			currAddress.setAddressId(null);
			currAddress.setCityName(facIdRef.getCityName());
			currAddress.setCountyCd(facIdRef.getCountyCd());
			newAddress = infraDao.createAddress(currFacility.getPhyAddr());
			currFacility.getPhyAddr().setAddressId(newAddress.getAddressId());
			facilityDao.addFacilityAddress(currFacility.getFpId(),
					newAddress.getAddressId());

			facilityDao.changeFacilityId("fp_facility", currentFacilityId,
					newFacilityId);
			facilityDao.changeFacilityId("fp_note_facility_xref",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("fp_facility_role_xref",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("fp_event_log", currentFacilityId,
					newFacilityId);
			facilityDao.changeFacilityId("cm_contact_contact_type_xref",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("fp_yearly_reporting_category",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("fp_rum", currentFacilityId,
					newFacilityId);
			facilityDao.changeFacilityId("dc_document", currentFacilityId,
					newFacilityId);
			facilityDao.changeFacilityId("iv_invoice", currentFacilityId,
					newFacilityId);
			facilityDao.changeFacilityId("dc_correspondence",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("cr_compliance_report",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("is_generic_issuance",
					currentFacilityId, newFacilityId);

			PermitService permitBO = ServiceFactory.getInstance()
					.getPermitService();
			List<Permit> facPermits = permitBO.search(null, null, null, null, null,
					null, null, null, currentFacilityId, null, null, null,
					null, null, null, true, null);

			for (Permit permit : facPermits) {
				permit = permitBO.retrievePermit(permit.getPermitID())
						.getPermit();
				permit.setFacilityId(newFacilityId);
				permitBO.modifyPermit(permit, userId, trans);
			}

			// Field Audit logs are a special case and require a "two pass"
			// approach,
			// since the unique_id may contain a facility id, if the category_cd
			// is of type facility.
			infraDao.changeFacilityIdForFieldAuditLogs(currentFacilityId,
					newFacilityId);
			facilityDao.changeFacilityId("fl_field_audit_log",
					currentFacilityId, newFacilityId);

			String message = "Facility ID of facility [" + currentFacilityId
					+ "] changed to [" + newFacilityId + "]";

			createEventLog(currFacility.getFpId(),
					currFacility.getFacilityId(), EventLogTypeDef.FAC_CHG,
					userId, message, trans);

			FacilityNote note = new FacilityNote();
			note.setNoteTxt(message);
			note.setDateEntered(new Timestamp(System.currentTimeMillis()));
			note.setUserId(userId); // for now
			note.setNoteTypeCd(NoteType.DAPC);
			note.setFpId(currFacility.getFpId());
			note.setFacilityId(newFacilityId);
			createFacilityNote(note, trans);

			// Rename directories

			String currPath = File.separator + "Facilities" + File.separator
					+ currentFacilityId;
			String newPath = File.separator + "Facilities" + File.separator
					+ newFacilityId;
			DocumentUtil.renameDir(currPath, newPath);

			Facility newFac = facilityDao.retrieveFacility(newFacilityId);
			if (newFac == null) {
				throw new DAOException("New Facility with ID : "
						+ newFacilityId + " not found");
			}
			newFac.setDoLaaCd(facIdRef.getDolaCd());
			facilityDao.modifyFacility(newFac);

			/*
			 * Mantis 2422 Note 5408 : Please make sure that the facility ID
			 * changes are changing the DO/LAA setting in all versions of the
			 * profile so if someone submits something using a historical
			 * profile it won't recreate this problem.
			 */
			facilityDao.changeDolaCd(newFacilityId, facIdRef.getDolaCd());

			trans.complete();
		} catch (ServiceFactoryException sf) {
			cancelTransaction(trans, new DAOException(sf.getMessage(), sf));
		} catch (DAOException e) {
			cancelTransaction("currentFacilityId=" + currentFacilityId, trans,
					e);
		} catch (IOException ioe) {
			cancelTransaction("currentFacilityId=" + currentFacilityId, trans,
					new DAOException(ioe.getMessage(), ioe));
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param currentFacilityId
	 * @param newFacilityId
	 * @param dolaCd
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean changeGateWayFacilityId(String currentFacilityId,
			String newFacilityId, String dolaCd) throws DAOException {

		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			throw new DAOException(
					"This method should be used by gateway only.");
		}

		boolean ret = false;

		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDao = facilityDAO(trans);

		try {

			facilityDao.changeFacilityId("fp_facility", currentFacilityId,
					newFacilityId);
			facilityDao.changeFacilityId("dc_document", currentFacilityId,
					newFacilityId);
			facilityDao.changeFacilityId("cm_contact_contact_type_xref",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("FP_FACILITY_ATTACHMENTS",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("cr_compliance_report",
					currentFacilityId, newFacilityId);
			facilityDao.changeFacilityId("cm_task", currentFacilityId,
					newFacilityId);

			// Rename directories

			String currPath = File.separator + "Facilities" + File.separator
					+ currentFacilityId;
			String newPath = File.separator + "Facilities" + File.separator
					+ newFacilityId;
			DocumentUtil.renameDir(currPath, newPath);

			/*
			 * Mantis 2422 Note 5408 : Please make sure that the facility ID
			 * changes are changing the DO/LAA setting in all versions of the
			 * profile so if someone submits something using a historical
			 * profile it won't recreate this problem.
			 */
			facilityDao.changeDolaCd(newFacilityId, dolaCd);

			trans.complete();

		} catch (DAOException e) {
			cancelTransaction("currentFacilityId=" + currentFacilityId, trans,
					e);
		} catch (IOException ioe) {
			cancelTransaction(trans, new DAOException(ioe.getMessage(), ioe));
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	private boolean checkForFRsubmit(Facility facility) {
		
		Boolean parmVal = SystemPropertyDef.getSystemPropertyValueAsBoolean("migration", false);
		if (parmVal != null && parmVal) {
			return false;
		}

		parmVal = SystemPropertyDef.getSystemPropertyValueAsBoolean("GetDataFromCoreDB", true);
		if (parmVal != null && !parmVal) {
			return false;
		}

		if (facility.getOperatingStatusCd().equals(OperatingStatusDef.SD)) {
			logger.debug("Facility is in shutdown state; Facility is not sent for FR.");
			return false;
		}

		return true;
	}

	/*
	 * Used when createWeDave is called
	 */
	private void internalFacilitySubmit1(Facility facility, String dataSubmitId)
			throws DAOException {
		if (!checkForFRsubmit(facility)) {
			logger.debug("FR is not submitted for facility = "
					+ facility.getFacilityId());
			return;
		}

		try {
			frFacilitySubmit(facility, dataSubmitId);
		} catch (DAOException de) {
			removeFacilityDir(facility.getFacilityId());
			throw de;
		}
	}

	private void internalFacilitySubmit(Facility facility, String dataSubmitId)
			throws DAOException {
		if (!checkForFRsubmit(facility)) {
			logger.debug("FR is not submitted for facility = "
					+ facility.getFacilityId());
			return;
		}

		logger.debug("internalFacilitySubmit started for facility: "
				+ facility.getFacilityId());

		final Facility frFacility = facility;
		final String frDataSubmitId = dataSubmitId;

		Thread frThread = new Thread("Facility FR Thread") {

			public void run() {
				try {
					frFacilitySubmit(frFacility, frDataSubmitId);
				} catch (Exception e) {
					logger.error("Facility: " + frFacility.getFacilityId()
							+ " Thread problem: ", e);
				}
			}
		};

		try {
			frThread.setDaemon(true);
			frThread.start();
		} catch (Exception e) {
			logger.error("Start of Facility: " + frFacility.getFacilityId()
					+ " Thread problem: ", e);
		}

		logger.debug("internalFacilitySubmit completed for facility: "
				+ facility.getFacilityId());
	}

	protected void frFacilitySubmit(Facility facility, String dataSubmitId)
			throws DAOException {
		us.oh.state.epa.place.facility.Facility submitFac;
		us.oh.state.epa.place.facility.FacilityProfile submitFacProfile = new us.oh.state.epa.place.facility.FacilityProfile();
		us.oh.state.epa.portal.base.Address submitFacAddr;
		InfrastructureDAO infraDAO = infrastructureDAO();

		long surrSec = new Timestamp(System.currentTimeMillis()).getTime();
		String newDataSubmitId = dataSubmitId + surrSec;

		loggerDebug("Data Submit ID = " + newDataSubmitId);

		try {
			if (facility.getCorePlaceId() != null) {
				PlaceService placeService = ServiceFactory.getInstance()
						.getPlaceService();
				submitFac = placeService.retrieveFacilityByID("portal",
						facility.getCorePlaceId().toString());
				if (submitFac == null) {
					throw new DAOException(
							"Cannot access Facility from Portal for corePlaceId = "
									+ facility.getCorePlaceId());
				}
				submitFacAddr = submitFac.getAddress();
				submitFac.setCoreID(facility.getCorePlaceId().toString());
				loggerDebug("CORD DB - Facility Name = " + submitFac.getName());
				loggerDebug("CORD DB - Facility Description = "
						+ submitFac.getDescription());
				if (submitFacAddr != null) {
					loggerDebug("CORD DB - Facility Address Line1 = "
							+ submitFacAddr.getAddressLine1()
							+ " Address Line 2 = "
							+ submitFacAddr.getAddressLine2() + " City = "
							+ submitFacAddr.getCity() + " County = "
							+ submitFacAddr.getCounty() + " State = "
							+ submitFacAddr.getState() + " Zip Code = "
							+ submitFacAddr.getZip());
				}
			} else {
				submitFac = new us.oh.state.epa.place.facility.Facility();
				submitFac.setID(facility.getFacilityId());
				submitFac.setType(Constants.PLACE_TYPE_FACILITY);
				submitFac.setCoreID(null);
				submitFacAddr = new us.oh.state.epa.portal.base.Address();
				submitFacAddr.setType(Constants.ADDRESS_BSTMA);
			}

			submitFac.setDescription(facility.getDesc());
			submitFac.setName(facility.getName());
			if (facility.getPhyAddr().getAddressLine2() != null
					&& !facility.getPhyAddr().getAddressLine2().equals("")) {
				submitFac.setPhysicalDescription(facility.getPhyAddr()
						.getAddressLine1()
						+ ", "
						+ facility.getPhyAddr().getAddressLine2()
						+ ", "
						+ facility.getPhyAddr().getCityName());
			} else {
				submitFac.setPhysicalDescription(facility.getPhyAddr()
						.getAddressLine1()
						+ ", "
						+ facility.getPhyAddr().getCityName());
			}

			SecondaryIdentifier secFacIds[] = new SecondaryIdentifier[1];
			SecondaryIdentifier secId = new SecondaryIdentifier();
			secId.setDescription(facility.getFacilityId());
			secId.setType(Constants.SECONDARY_ID_TYPE_CODE_AIRFI);
			secFacIds[0] = secId;
			submitFac.setSecondaryIDs(secFacIds);

			submitFacAddr.setAddressLine1(facility.getPhyAddr()
					.getAddressLine1());
			submitFacAddr.setAddressLine2(facility.getPhyAddr()
					.getAddressLine2());
			submitFacAddr.setCity(facility.getPhyAddr().getCityName());
			CountyDef county = infraDAO.retrieveCounty(facility.getPhyAddr()
					.getCountyCd());
			submitFacAddr.setCounty(county.getCountyNm());
			submitFacAddr.setState(facility.getPhyAddr().getState());
			submitFacAddr.setZip(facility.getPhyAddr().getZipCode());

			submitFac.setAddress(submitFacAddr);

			submitFacProfile.setFacility(submitFac);
			// String xml =
			// FacilityProfileSubmitHelper.createFacilityProfileSubmitXML(submitFacProfile);
			//
			// DataSubmitService dataSubmitService =
			// ServiceFactory.getInstance().getDataSubmitService();
			//
			// DataSubmit dataSubmit = new DataSubmit("STARS2", // The submitter
			// name
			// user, // The user ID associated with the submission (must have
			// 'submit' privilege)
			// servName, // Service name the submission is for. Must be a valid
			// service name.
			// Constants.DATASUBMIT_FP_COMMAND_SUBMIT, // The command the
			// calling process wishes to execute
			// pin, // The PIN. Must be a valid PIN associated with the account
			// false, // the data is not confidential
			// xml.getBytes()); // The XML data structure to submit for
			// processing
			//
			// dataSubmit.setSubmissionID(newDataSubmitId);
			// dataSubmitService.submit(dataSubmit);
		} catch (ServiceFactoryException sfe) {
			throw new DAOException(sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			throw new DAOException(re.getMessage(), re);
		}

		logger.debug("FR FacilitySubmit completed for facility: "
				+ facility.getFacilityId());
	}

	/**
	 * @param reportYear
	 * @return FacilityList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityList[] retrieveTvCertLate(Integer reportYear,
			String facilityId, String facilityName, String operatingStatusCd)
			throws DAOException {

		Calendar cDate = new GregorianCalendar(reportYear, 0, 1, 0, 0, 0);
		Timestamp shutDownDateFrom = new Timestamp(cDate.getTimeInMillis());

		cDate = new GregorianCalendar(reportYear + 1, 0, 1, 0, 0, 0);
		Timestamp shutDownDateTo = new Timestamp(cDate.getTimeInMillis());

		return facilityDAO().retrieveTvCertLate(reportYear, facilityId,
				facilityName, operatingStatusCd, "0430", shutDownDateFrom,
				shutDownDateTo);

	}

	/**
	 * @param reportYear
	 * @return FacilityList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityList[] retrieveTvCertReminder(Integer reportYear,
			String facilityId, String facilityName) throws DAOException {

		Calendar cDate = new GregorianCalendar(reportYear + 1, 0, 1, 0, 0, 0);
		Timestamp effectiveDate = new Timestamp(cDate.getTimeInMillis());

		cDate = new GregorianCalendar(reportYear + 1, 0, 1, 0, 0, 0);
		Timestamp expirationDate = new Timestamp(cDate.getTimeInMillis());

		return facilityDAO().retrieveTvCertReminder(reportYear, facilityId,
				facilityName, "0430", effectiveDate, expirationDate);

	}

	private void updateFacilityFromCoreDB(Facility facility)
			throws DAOException {
		if (facility.getCorePlaceId() == null) {
			return;
		}

		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			return;
		}
		
		if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
			return;
		}

		Boolean parmVal = SystemPropertyDef.getSystemPropertyValueAsBoolean("migration", false);
		if (parmVal != null && parmVal) {
			return;
		}

		parmVal = SystemPropertyDef.getSystemPropertyValueAsBoolean("GetDataFromCoreDB", true);
		if (parmVal != null && !parmVal) {
			return;
		}

		parmVal = SystemPropertyDef.getSystemPropertyValueAsBoolean("testMode", false);
		if (parmVal != null && parmVal) {
			return;
		}

		us.oh.state.epa.place.facility.Facility fac = null;

		try {
			PlaceService placeService = ServiceFactory.getInstance()
					.getPlaceService();
			fac = placeService.retrieveFacilityByID("portal", facility
					.getCorePlaceId().toString());
			if (fac == null) {
				throw new DAOException(
						"Cannot access Facility from Portal for corePlaceId = "
								+ facility.getCorePlaceId());
			}
		} catch (ServiceFactoryException sfe) {
			String error = "update facility from CoreDB failed for facility "
					+ facility.getFacilityId() + ". " + sfe.getMessage();
			logger.error(error, sfe);
			throw new DAOException(error, sfe);
		} catch (RemoteException re) {
			String error = "update facility from CoreDB failed for facility "
					+ facility.getFacilityId() + ". " + re.getMessage();
			logger.error(error, re);
			throw new DAOException(error, re);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SubmissionLog[] searchSubmissionLog(
			SubmissionLog searchSubmissionLog, Timestamp beginDate,
			Timestamp endDate) throws DAOException {
		SubmissionLog[] results = facilityDAO().searchSubmissionLog(
				searchSubmissionLog, beginDate, endDate);
		for (SubmissionLog log : results) {
			if (log.isNonROSubmission() && log.getDocumentId() != null) {
				DocumentService documentBO;
				try {
					documentBO = ServiceFactory.getInstance()
							.getDocumentService();
					Document doc = documentBO.getDocumentByID(
							log.getDocumentId(), false);
					log.setAttestationDoc(doc);
				} catch (ServiceFactoryException e) {
					throw new DAOException(
							"Exception getting document service", e);
				} catch (RemoteException e) {
					throw new DAOException(
							"Exception retrieving document with ID="
									+ log.getDocumentId(), e);
				}
			}
		}
		return results;
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
	public List<Document> getPrintableDocumentList(Facility facility)
			throws RemoteException {
		List<Document> docList = new ArrayList<Document>();

		// if copy on change flag has been set, we can assume this facility is
		// "stable"
		// and no more changes will be made to it. Therefore, we will add a link
		// for
		// the submitted app to our printable document list
		if (facility.isCopyOnChange() && !isPublicApp()) {
			// get submitted pdf document
			Document rptDoc = this.getFacilityProfileReportDocument(facility);
			try {
				if (rptDoc != null && DocumentUtil.canRead(rptDoc.getPath())) {
					// use existing doc if already generated
					logger.debug(" Adding document to facility document list: "
							+ rptDoc.getPath());
					docList.add(rptDoc);
				} else if (rptDoc != null
						&& !DocumentUtil.canRead(rptDoc.getPath())) {
					// generate new doc if it does not already exist
					String userName = null;
					SubmissionLog log = getSubmissionLogForFacilityProfile(facility);
					if (log != null) {
						userName = log.getGatewayUserName();
					}
					FacilityProfileDocument cDoc = createFacilityProfilePDFDocument(
							facility, userName);
					if (cDoc != null && DocumentUtil.canRead(cDoc.getPath())) {
						logger.debug(" Adding document to facility document list: "
								+ cDoc.getPath());
						docList.add(cDoc);
					}
				}
			} catch (IOException e) {
				logger.error(
						"Exception checking on submitted PDF document for facility "
								+ facility.getFacilityId(), e);
			}
		}

		// get latest pdf document
		Document doc = generateTempFacilityProfileReport(facility, null);
		logger.debug(" Adding document to facility document list: "
				+ doc.getPath());
		docList.add(doc);

		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			// Show attachments only if internal.
			// No need to show attestation file (if there is one being submitted
			// from external) because
			// the user is "just now" attaching it and does not need to shown it
			// a second time.
			for (Attachment attachment : this
					.retrieveFacilityAttachments(facility.getFacilityId())) {
				logger.debug(" Adding attachment to facility document list: "
						+ attachment.getPath());
				docList.add(attachment);
			}
		}

		return docList;
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
	public FacilityProfileDocument createFacilityProfilePDFDocument(
			Facility facility, String userName) throws DAOException {
		FacilityProfileDocument doc = null;
		try {
			doc = getFacilityProfileReportDocument(facility);
			OutputStream os = DocumentUtil.createDocumentStream(doc.getPath());
			try {
				FacilityPdfGenerator generator = new FacilityPdfGenerator();
				generator.setUserName(userName);
				generator.setAttestationAttached(userName != null
						&& facilityHasAttestationDocument(facility));
				generator.generatePdf(facility, os, null);
			} catch (DocumentException e) {
				throw new IOException(
						"Exception writing faciltiy profile to stream for "
								+ facility.getFacilityId());
			} finally {
				if (os != null) {
					os.close();
				}
			}
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting faciltiy profile as stream for "
							+ facility.getFacilityId(), e);
		}
		return doc;
	}

	/**
	 * Generate a pdf file containing data from the facility and create a
	 * temporary Document object refrencing this file.
	 * 
	 * @param cpId
	 *            the facility FP ID to be rendered in a PDF file.
	 * @return Document object referencing pdf file.
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public TmpDocument generateTempFacilityProfileReport(Facility facility, String prefix)
			throws DAOException {
		TmpDocument profileDoc = null;

		try {
			profileDoc = new TmpDocument();

			// Set the path elements of the temp doc.
			profileDoc.setDescription("Facility Detail Data Report");
			profileDoc.setFacilityID(facility.getFacilityId());
			if(null != prefix) {
				profileDoc.setTmpFileName(prefix + "_FacProfileDoc_"
						+ facility.getFacilityId() + ".pdf");
			} else {
				profileDoc.setTmpFileName("FacProfileDoc_"
						+ facility.getFacilityId() + ".pdf");
			}
			profileDoc.setTemporary(true);
			DocumentUtil.mkDir(profileDoc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(profileDoc
					.getPath());
			if(null != prefix) {
				writeFacilityProfileToStream(facility, os, " - Emission Units Included in Application Only");
			} else {
				writeFacilityProfileToStream(facility, os, null);
			}
			os.close();

		} catch (Exception ex) {
			throw new DAOException("Cannot generate facility detail report", ex);
		}

		return profileDoc;
	}

	/**
	 * Write pdf version of facility detail to an output stream.
	 * 
	 * @param facility
	 * @param os
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void writeFacilityProfileToStream(Facility facility, OutputStream os, String titleSuffix )
			throws IOException {

		try {
			FacilityPdfGenerator generator = new FacilityPdfGenerator();
			generator.generatePdf(facility, os, titleSuffix);
		} catch (DocumentException e) {
			throw new IOException(
					"Exception writing facility detail to stream for "
							+ facility.getFacilityId());
		}
	}

	/**
	 * 
	 * @param facility
	 * @return
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityProfileDocument getFacilityProfileReportDocument(
			Facility facility) {
		FacilityProfileDocument doc = new FacilityProfileDocument();
		// Set the path elements of the temp doc.
		doc.setDescription("Submitted facility detail pdf file");
		doc.setFacilityID(facility.getFacilityId());
		doc.setTemporary(true);
		doc.setOverloadFileName("FacilityProfile" + facility.getFpId()
				+ ".pdf");
		return doc;
	}

	private SubmissionLog getSubmissionLogForFacilityProfile(Facility facility) {
		SubmissionLog log = null;
		SubmissionLog searchSubmissionLog = new SubmissionLog();
		Task t = new Task();
		HashMap<TaskType, String> taskTypeDescs = t.getTaskTypeDescs();
		searchSubmissionLog.setFacilityId(facility.getFacilityId());
		searchSubmissionLog.setSubmissionType(taskTypeDescs.get(TaskType.FC));
		try {
			int count = 0;
			for (SubmissionLog tmp : facilityDAO().searchSubmissionLog(
					searchSubmissionLog, facility.getStartDate(),
					facility.getStartDate())) {
				log = tmp;
				count++;
			}
			if (count > 0) {
				logger.error("Multiple submissions found for facility: "
						+ facility.getFacilityId() + ". Setting user id to "
						+ log.getGatewayUserName());
			} else if (count == 0) {
				logger.error("No submissions found for facility: "
						+ facility.getFacilityId());
			}
		} catch (DAOException e) {
			logger.error(
					"Exception retrieving facility from submission log for "
							+ facility.getFacilityId(), e);
		}
		return log;
	}

	/**
	 * Create a zip file containing facility pdf file.
	 * 
	 * @param app
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document generateTempFacilityProfilePDFZipFile(Facility facility)
			throws FileNotFoundException, IOException {

		TmpDocument appDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		appDoc.setDescription("Facility report (pdf) zip file");
		appDoc.setFacilityID(facility.getFacilityId());
		appDoc.setTemporary(true);
		appDoc.setTmpFileName("Facility" + facility.getFpId() + "PDFReport.zip");

		// make sure temporary directory exists
		DocumentUtil.mkDirs(appDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(appDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		zipFacilityProfilePDFReportFile(facility, zos);
		zos.close();
		os.close();

		return appDoc;
	}

	private void zipFacilityProfilePDFReportFile(Facility facility,
			ZipOutputStream zos) throws FileNotFoundException, IOException {
		FacilityProfileDocument doc = getFacilityProfileReportDocument(facility);
		if (doc != null && DocumentUtil.canRead(doc.getPath())) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
				addEntryToZip(doc.getDescription(), docIS, zos);
			} catch (FileNotFoundException e) {
				String errorMsg = doc.getDescription();
				if (errorMsg.length() > 50) {
					errorMsg = errorMsg.substring(0, 47) + "...";
				}
				throw new FileNotFoundException(errorMsg);
			}

			if (docIS != null) {
				docIS.close();
			}
		} else {
			logger.error("No pdf report file found for facility "
					+ facility.getFacilityId());
		}
	}

	/**
	 * Add attestation document to compliance report
	 * 
	 * @param facility
	 * @param attestationDoc
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void addAttestationDocument(Facility facility,
			Attachment attestationDoc, Transaction trans) throws DAOException {
		attestationDoc.setDocTypeCd(FacilityAttachmentTypeDef.RO_SIGNATURE);
		FacilityDAO facilityDAO = facilityDAO(trans);
		facilityDAO.createFacilityAttachment(attestationDoc);
		facility.setAttestationDocument(attestationDoc);
		logger.debug(" Added attestation document "
				+ attestationDoc.getDocumentID() + " to facility "
				+ facility.getFacilityId());
	}

	/**
	 * Remove attestation document to compliance report
	 * 
	 * @param facility
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeAttestationDocument(Facility facility, Transaction trans)
			throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		for (Attachment attachment : this.retrieveFacilityAttachments(facility
				.getFacilityId())) {
			if (FacilityAttachmentTypeDef.RO_SIGNATURE.equals(attachment
					.getDocTypeCd())) {
				facilityDAO.removeFacilityAttachment(attachment);
			}
		}
	}

	private boolean facilityHasAttestationDocument(Facility facility) {
		boolean hasAttestationDocument = false;
		for (Attachment attachment : this.retrieveFacilityAttachments(facility
				.getFacilityId())) {
			if (FacilityAttachmentTypeDef.RO_SIGNATURE.equals(attachment
					.getDocTypeCd())) {
				hasAttestationDocument = true;
				break;
			}
		}
		return hasAttestationDocument;
	}

	/**
	 * Return pdf file RO can sign as attestation document.
	 * 
	 * @param facility
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Attachment createFacilityAttestationDocument(Facility facility)
			throws DAOException {
		Attachment doc = null;
		// TODO portal filestore disabled
		/*
		 * 
		 * String path = null; try { doc =
		 * getFacilityAttestationDocument(facility); path = doc.getPath();
		 * OutputStream os = DocumentUtil.createDocumentStream(path); try {
		 * 
		 * FacilityPdfGenerator generator = new FacilityPdfGenerator();
		 * generator.setAttestationOnly(true); generator.generatePdf(facility,
		 * os); } catch (DocumentException e) { logger.error(e.getMessage(), e);
		 * throw new IOException(
		 * "Exception writing attestation document to stream for " +
		 * facility.getFacilityId()); } os.close(); } catch (IOException e) {
		 * throw new DAOException(
		 * "Error: The Facility Attachments are missing. Facility ID: " +
		 * facility.getFacilityId() + ", Path: " + path); }
		 */
		return doc;
	}

	// private Attachment getFacilityAttestationDocument(Facility facility) {
	// Attachment doc = new Attachment();
	// doc.setFacilityID(facility.getFacilityId());
	// doc.setSubPath("Attachments");
	// doc.setExtension("pdf");
	// doc.setDocumentID(facility.getFpId()); // just a temporary id until the
	// // file is uploaded
	// doc.setTemporary(true);
	// doc.setDescription("Attestation document for facility "
	// + facility.getFacilityId());
	// return doc;
	// }

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
		// generate Facility inventory PDF document and add it as an attachment
		task.getAttachments().clear();
		// TODO portal filestore disabled
		/*
		 * 
		 * createFacilityProfilePDFDocument(task.getFacility(),
		 * task.getUserName()); Document zipDoc =
		 * generateTempFacilityProfilePDFZipFile(task .getFacility()); if
		 * (zipDoc != null) { us.oh.state.epa.portal.datasubmit.Attachment
		 * attachment = new us.oh.state.epa.portal.datasubmit.Attachment(
		 * zipDoc.getFileName(), "text/zip", DocumentUtil.getFileStoreRootPath()
		 * + zipDoc.getPath(), null); task.getAttachments().add(attachment); }
		 */
	}

	/**
	 * This returns overall compliance status for a facility.
	 * 
	 * @param tring
	 *            curFacId
	 * @return boolean
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String getOverallComplianceStatus(String curFacId)
			throws DAOException {
		String rtn = ComplianceStatusDef.YES;
		Facility curFacility = this.retrieveFacilityData(curFacId, -1);
		if (ComplianceStatusDef.NO.equals(curFacility.getAirProgramCompCd())) {
			return ComplianceStatusDef.NO;
		} else if (ComplianceStatusDef.ON_SCHEDULE.equals(curFacility
				.getAirProgramCompCd())) {
			rtn = ComplianceStatusDef.ON_SCHEDULE;
		}
		if (ComplianceStatusDef.NO.equals(curFacility.getSipCompCd())) {
			return ComplianceStatusDef.NO;
		} else if (ComplianceStatusDef.ON_SCHEDULE.equals(curFacility
				.getSipCompCd())) {
			rtn = ComplianceStatusDef.ON_SCHEDULE;
		}
		if (ComplianceStatusDef.NO.equals(curFacility.getMactCompCd())) {
			return ComplianceStatusDef.NO;
		} else if (ComplianceStatusDef.ON_SCHEDULE.equals(curFacility
				.getMactCompCd())) {
			rtn = ComplianceStatusDef.ON_SCHEDULE;
		}
		if (curFacility.isNeshaps()) {
			for (PollutantCompCode neshapsSubpart : curFacility
					.getNeshapsSubpartsCompCds()) {
				if (ComplianceStatusDef.NO.equals(neshapsSubpart
						.getPollutantCompCd())) {
					return ComplianceStatusDef.NO;
				} else if (ComplianceStatusDef.ON_SCHEDULE
						.equals(neshapsSubpart.getPollutantCompCd())) {
					rtn = ComplianceStatusDef.ON_SCHEDULE;
				}
			}
		}

		return rtn;
	}

	void loggerDebug(String s) {
		// logger.debug(s);
	}

	/*
	 * To recreate the List<FacilityEmissionFlow> attributes for processes and
	 * control equipment from the relationships in FacilityNodes. This only
	 * preserves the flowFactor not percent. However, when the profile is read
	 * back in, the percents are re-computed.
	 */
	private void setRelationships(Facility f) {
		for (EmissionUnit eu : f.getEmissionUnits()) {
			for (EmissionProcess ep : eu.getEmissionProcesses()) {
				// FacilityRelationship[] fRels = ep.getRelationships();
				// dumpRels(eu.getEpaEmuId() + " " + ep.getProcessId(), fRels);

				List<FacilityEmissionFlow> epEmissionFlows = new ArrayList<FacilityEmissionFlow>(
						0);
				for (ControlEquipment ce2 : ep.getControlEquipments()) {
					FacilityRelationship rel = ep.findRelationship(ce2
							.getFpNodeId());
					if (rel == null) {
						logger.error("Did not find relation for control equip "
								+ ep.getProcessId() + " "
								+ ce2.getControlEquipmentId() + " "
								+ ce2.getFpNodeId());
					} else {
						FacilityEmissionFlow fef = new FacilityEmissionFlow(
								FacilityEmissionFlow.CE_TYPE,
								ce2.getControlEquipmentId(), rel);
						epEmissionFlows.add(fef);
					}
				}
				for (EgressPoint egp : ep.getEgressPoints()) {
					if (egp.isFugitive())
						continue;
					FacilityRelationship rel = ep.findRelationship(egp
							.getFpNodeId());
					if (rel == null) {
						logger.error("Did not find relation for release point "
								+ ep.getProcessId() + " "
								+ egp.getReleasePointId() + " "
								+ egp.getFpNodeId());
					} else {
						FacilityEmissionFlow fef = new FacilityEmissionFlow(
								FacilityEmissionFlow.STACK_TYPE,
								egp.getReleasePointId(), rel);
						epEmissionFlows.add(fef);
					}
				}
				ep.setEpEmissionFlows(epEmissionFlows);

				for (ControlEquipment ce : ep.getControlEquipments()) {
					setRelationships(
							eu.getEpaEmuId() + " " + ep.getProcessId(), ce);
				}
			}
		}
	}

	private void setRelationships(String from, ControlEquipment ce) {
		// logger.error("DENNIS " + from + " " + ce.getControlEquipmentId());
		// FacilityRelationship[] fRels = ce.getRelationships();
		// dumpRels(from + " " + ce.getControlEquipmentId(), fRels);

		List<FacilityEmissionFlow> ceEmissionFlows = new ArrayList<FacilityEmissionFlow>(
				0);
		for (ControlEquipment ce2 : ce.getControlEquips()) {
			FacilityRelationship rel = ce.findRelationship(ce2.getFpNodeId());
			if (rel == null) {
				logger.error("Did not find relation for control equip "
						+ ce.getControlEquipmentId() + " "
						+ ce2.getControlEquipmentId() + " " + ce2.getFpNodeId());
			} else {
				FacilityEmissionFlow fef = new FacilityEmissionFlow(
						FacilityEmissionFlow.CE_TYPE,
						ce2.getControlEquipmentId(), rel);
				ceEmissionFlows.add(fef);
			}
		}
		for (EgressPoint egp : ce.getEgressPoints()) {
			if (egp.isFugitive())
				continue;
			FacilityRelationship rel = ce.findRelationship(egp.getFpNodeId());
			if (rel == null) {
				logger.error("Did not find relation for release point "
						+ ce.getControlEquipmentId() + " "
						+ egp.getReleasePointId() + " " + egp.getFpNodeId());
			} else {
				FacilityEmissionFlow fef = new FacilityEmissionFlow(
						FacilityEmissionFlow.STACK_TYPE,
						egp.getReleasePointId(), rel);
				ceEmissionFlows.add(fef);
			}
		}
		ce.setCeEmissionFlows(ceEmissionFlows);

		for (ControlEquipment ce2 : ce.getControlEquips()) {
			setRelationships(from + " " + ce.getControlEquipmentId(), ce2);
		}
	}

	@Override
	public List<ApiGroup> retrieveFacilityApis(Integer fpId)
			throws DAOException {
		return getFacilityDAO().retrieveFacilityApis(fpId);
	}

	@Override
	public void createFacilityApi(ApiGroup api) throws DAOException {
		getFacilityDAO().createFacilityApi(api);
	}

	@Override
	public void updateFacilityApi(ApiGroup api) throws DAOException {
		getFacilityDAO().updateFacilityApi(api);
	}

	@Override
	public void deleteFacilityApi(Integer apiCd) throws DAOException {
		getFacilityDAO().deleteFacilityApi(apiCd);
	}

	@Override
	public void deleteFacilityAllApis(Integer fpId) throws DAOException {
		facilityDAO().deleteFacilityAllApis(fpId);
	}

	@Override
	public List<ValidationMessage> changeOwnership(FacilityList[] facilities,
			Integer environmentalContactId,
			Integer responsibleOfficialContactId, String facilityOwnerCmpId,
			Timestamp ownershipChangeDate) throws DAOException {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		Transaction trans = TransactionFactory.createTransaction();

		ContactDAO contactDAO = contactDAO(trans);
		FacilityDAO facilityDAO = facilityDAO(trans);
		CompanyDAO companyDAO = companyDAO(trans);

		boolean opOk = false;

		try {
			// Retrieve new owner's company
			Company newOwnerCompany = companyDAO
					.retrieveCompany(facilityOwnerCmpId);

			// Validate each facility, then proceed with actions
			for (FacilityList facility : facilities) {
				String facilityId = facility.getFacilityId();
				List<Contact> facilityContacts = new ArrayList<Contact>();
				FacilityOwner currentFacilityOwner = null;
				List<FacilityOwner> pastFacilityOwners = null;
				FacilityOwner newFacilityOwner = null;

				// retrieve necessary items
				facilityContacts = facilityDAO
						.retrieveFacilityContacts(facilityId);
				currentFacilityOwner = facilityDAO
						.retrieveFacilityOwner(facilityId);
				pastFacilityOwners = facilityDAO
						.retrieveFacilityOwners(facilityId);
				newFacilityOwner = new FacilityOwner(ownershipChangeDate,
						newOwnerCompany, facilityId, null);

				// do validation for each facility
				validationMessages.addAll(validateTimeOutContactTypes(facility,
						facilityContacts, ownershipChangeDate));
				validationMessages.addAll(validateTimeOutFacilityOwner(
						currentFacilityOwner, ownershipChangeDate, facilityId));

				if (validationMessages.size() == 0) {
					// time out contact types
					timeOutContactTypes(facility, facilityContacts,
							ownershipChangeDate, contactDAO);

					// time out owner
					timeOutFacilityOwner(currentFacilityOwner,
							ownershipChangeDate, facilityDAO);

					// refresh facility contacts
					facilityContacts = facilityDAO
							.retrieveFacilityContacts(facilityId);

					// add new contact types
					addEnvironmentalContact(facility, facilityContacts,
							ownershipChangeDate, environmentalContactId,
							contactDAO);

					addResponsibleOfficial(facility, facilityContacts,
							ownershipChangeDate, responsibleOfficialContactId,
							contactDAO);

					// refresh past facility owners
					pastFacilityOwners = facilityDAO
							.retrieveFacilityOwners(facilityId);

					// add owner
					addNewFacilityOwner(pastFacilityOwners, newFacilityOwner,
							ownershipChangeDate, facilityDAO);
					
					notifyListeners(new FacilityOwnershipChangeEvent(currentFacilityOwner,newFacilityOwner));

				}

			}

			if (validationMessages.size() == 0) {
				opOk = true;
			}

			if (opOk) {
				trans.complete();
				logger.debug("Change ownership transaction has been completed");
			} else {
				trans.cancel();
				if (trans.cancel()) {
					logger.debug("Change ownership transaction has been cancelled");
				}
			}

		} catch (RemoteException re) {
			cancelTransaction(trans, re);
			logger.error(re);
		} finally {
			closeTransaction(trans);
		}

		return validationMessages;

	}

	private void addNewFacilityOwner(List<FacilityOwner> pastFacilityOwners,
			FacilityOwner newFacilityOwner, Timestamp startDate,
			FacilityDAO facilityDAO) throws DAOException, RemoteException {
		boolean isAlreadyCurrentOwner = false;

		for (FacilityOwner pastOwner : pastFacilityOwners) {
			if (pastOwner.getCompany().getCmpId()
					.equals(newFacilityOwner.getCompany().getCmpId())) {
				if (pastOwner.getEndDate() != null) {
					if (pastOwner.getEndDate().equals(
							newFacilityOwner.getStartDate())) {
						// previous owner already exists
						newFacilityOwner = new FacilityOwner(pastOwner);
						newFacilityOwner.setEndDate(null);

						facilityDAO.removeFacilityOwner(pastOwner);
						facilityDAO.addFacilityOwner(newFacilityOwner);

						isAlreadyCurrentOwner = true;
					}
				} else {
					isAlreadyCurrentOwner = true;
				}
			}
		}

		if (!isAlreadyCurrentOwner) {
			// add as a new owner
			facilityDAO.addFacilityOwner(newFacilityOwner);
		}
	}

	private List<ValidationMessage> validateTimeOutFacilityOwner(
			FacilityOwner currentFacilityOwner, Timestamp endDate,
			String facilityId) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		if (currentFacilityOwner != null) {
			if (currentFacilityOwner.getCompany() != null) {
				FacilityOwner pastOwner = new FacilityOwner(
						currentFacilityOwner);
				pastOwner.setEndDate(endDate);
				validationMessages = FacilityValidation
						.validateFacilityOwnerTimeout(facilityId, pastOwner);
			}
		}

		return validationMessages;
	}

	private void timeOutFacilityOwner(FacilityOwner currentFacilityOwner,
			Timestamp endDate, FacilityDAO facilityDAO) throws DAOException,
			RemoteException {

		if (currentFacilityOwner != null) {
			if (currentFacilityOwner.getCompany() != null) {
				// time out old owner
				FacilityOwner pastOwner = new FacilityOwner(
						currentFacilityOwner);
				pastOwner.setEndDate(endDate);
				facilityDAO.removeFacilityOwner(currentFacilityOwner);
				facilityDAO.addFacilityOwner(pastOwner);
			}
		}

	}

	private List<ValidationMessage> validateTimeOutContactTypes(
			FacilityList facility, List<Contact> facilityContacts,
			Timestamp endDate) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		if (facility != null) {
			String facilityId = facility.getFacilityId();
			for (Contact contact : facilityContacts) {
				for (ContactType cType : contact.getContactTypes()) {
					if (cType.getFacilityId().equals(facilityId)) {
						ContactType newContactType = new ContactType(cType);
						newContactType.setEndDate(endDate);
						newContactType.setFacilityId(facilityId);
						validationMessages.addAll(FacilityValidation
								.validateContactTypeTimeOut(newContactType,
										contact));
					}
				}
			}
		}

		return validationMessages;
	}

	private void timeOutContactTypes(FacilityList facility,
			List<Contact> facilityContacts, Timestamp endDate,
			ContactDAO contactDAO) throws DAOException, RemoteException {
		if (facility != null) {
			String facilityId = facility.getFacilityId();
			for (Contact contact : facilityContacts) {
				for (ContactType cType : contact.getContactTypes()) {
					if (cType.getFacilityId().equals(facilityId)) {
						if (cType.getEndDate() == null) {
							updateContactType(cType, facilityId, endDate,
									contactDAO);
						}
					}
				}
			}
		}
	}

	private void addEnvironmentalContact(FacilityList facility,
			List<Contact> facilityContacts, Timestamp startDate,
			Integer environmentalContactId, ContactDAO contactDAO)
			throws DAOException, RemoteException {
		boolean isECSet = false;
		if (facility != null) {
			String facilityId = facility.getFacilityId();
			for (Contact contact : facilityContacts) {
				if (contact.getContactId().equals(environmentalContactId)) {
					for (ContactType cType : contact.getContactTypes()) {
						if (cType.getFacilityId().equals(facilityId)) {
							// does EC already exist?
							if (cType.getContactTypeCd().equals(
									ContactTypeDef.ENVI)) {
								if (cType.getEndDate() != null) {
									if (cType.getEndDate().equals(startDate)
											&& cType.getStartDate().equals(
													startDate)) {
										updateContactType(cType, facilityId,
												null, contactDAO);
										isECSet = true;
									}
								}
							}
						}
					}
				}
			}

			if (!isECSet) {
				ContactType newECContactType = new ContactType(
						environmentalContactId, ContactTypeDef.ENVI, startDate,
						null);

				contactDAO.addContactType(newECContactType.getContactId(),
						newECContactType.getContactTypeCd(),
						newECContactType.getStartDate(), facilityId);

			}
		}
	}

	private void addResponsibleOfficial(FacilityList facility,
			List<Contact> facilityContacts, Timestamp startDate,
			Integer responsibleOfficialContactId, ContactDAO contactDAO)
			throws DAOException, RemoteException {
		boolean isROSet = false;
		if (facility != null) {
			if (facility.getPermitClassCd().equals("tv")) {
				String facilityId = facility.getFacilityId();
				for (Contact contact : facilityContacts) {
					if (contact.getContactId().equals(
							responsibleOfficialContactId)) {
						for (ContactType cType : contact.getContactTypes()) {
							if (cType.getFacilityId().equals(facilityId)) {
								// does RO already exist?
								if (cType.getContactTypeCd().equals(
										ContactTypeDef.RSOF)) {
									if (cType.getEndDate() != null) {
										if (cType.getEndDate()
												.equals(startDate)
												&& cType.getStartDate().equals(
														startDate)) {
											updateContactType(cType,
													facilityId, null,
													contactDAO);
											isROSet = true;
										}
									}
								}
							}
						}
					}

				}

				if (!isROSet) {
					ContactType newROContactType = new ContactType(
							responsibleOfficialContactId, ContactTypeDef.RSOF,
							startDate, null);

					contactDAO.addContactType(newROContactType.getContactId(),
							newROContactType.getContactTypeCd(),
							newROContactType.getStartDate(), facilityId);
				}
			}
		}
	}

	private void updateContactType(ContactType cType, String facilityId,
			Timestamp endDate, ContactDAO contactDAO) throws DAOException {
		ContactType newContactType = new ContactType(cType);
		newContactType.setEndDate(endDate);
		newContactType.setFacilityId(facilityId);

		contactDAO.deleteContactType(cType);
		contactDAO.addContactType(cType.getContactId(), newContactType);
	}

	@Override
	public List<ValidationMessage> addNewContactType(FacilityList[] facilities,
			ContactType newContactType) throws DAOException {

		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		int added = 0;
		int failed = 0;

		// Validate each facility, then proceed with actions.
		for (FacilityList facility : facilities) {

			String facilityId = facility.getFacilityId();
			List<Contact> facilityContacts = new ArrayList<Contact>();

			// Retrieve necessary items.
			facilityContacts = facilityDAO().retrieveFacilityContacts(facilityId);
			newContactType.setFacilityId(facilityId);
			
			List<ValidationMessage> localMsgs 
				= FacilityValidation.validateAddContactType(newContactType, facilityContacts);

			if (localMsgs.size() == 0) {
				// Add contact type
				contactDAO().addContactType(newContactType.getContactId(), newContactType);
				added++;

			} else {
				validationMessages.addAll(localMsgs);
				failed++;
			}
			
		}

		if (failed > 0) {
			ValidationMessage fvm = new ValidationMessage("ContactsFailed", "Unable to add contact to " + failed + " facilities.", 
					ValidationMessage.Severity.WARNING);
			validationMessages.add(fvm);
		}
		
		if (added > 0) {
			ValidationMessage avm = new ValidationMessage("ContactsAdded", "Contact added to " + added + " facilities.", 
					ValidationMessage.Severity.INFO);
			validationMessages.add(avm);
			
		}

		return validationMessages;
	}

	@Override
	public List<Contact> retrieveAllReadOnlyContactsForStaging()
			throws DAOException {
		// Transaction trans = TransactionFactory.createTransaction();
		ContactDAO contactDAO = contactDAO();
		List<Contact> ret = new ArrayList<Contact>();
		try {
			ret = contactDAO.retrieveGlobalContacts();
			// trans.complete();
		} catch (RemoteException re) {
			// cancelTransaction(trans, re);
		} finally {
			// Clean up our transaction stuff
			// closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * This method is used to create a facility contacts with out Contact Type
	 * in the staging area
	 * 
	 * @param contact
	 * @throws DAOException
	 * 
	 */
	public void createStagingFacilityContactsWithOutAddType(Contact contact)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			// InfrastructureHelper infraHelper = new InfrastructureHelper();
			Contact contactInStaging = infrastructureHelper
					.retrieveStagingContact(contact, trans);
			if (contactInStaging == null)
				infrastructureHelper.createContactWithOutAddContactType(
						contact, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, new RemoteException(e.getMessage(), e));
			throw e;
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	private void unionAirProgInfo(Facility scscFacility, Facility f) {
		// Note that we are combining everything but AFS does not use
		// what we send to know what we are no longer subject to.
		// For that, they expect records to be sent up that specify
		// a compliance of "X" to mean no longer subject to that thing.
		scscFacility.unionOperatingStatus(f.getOperatingStatusCd());
		scscFacility.unionSipCompCd(f.getSipCompCd());
		scscFacility.unionMactCompCd(f.getMactCompCd());

		// NESHAPS
		if (f.getNeshapsSubpartsCompCds() != null
				&& f.getNeshapsSubpartsCompCds().size() > 0) {
			for (PollutantCompCode pcc : f.getNeshapsSubpartsCompCds()) {
				scscFacility.unionNeshapsSubpartsCompCd(pcc);
			}
		}

		// NSPS
		if (f.getNspsSubparts() != null && f.getNspsSubparts().size() > 0) {
			for (String s : f.getNspsSubparts()) {
				scscFacility.unionNspsSubpart(s);
			}
		}

		// MACT
		if (f.isMact()) {
			if (f.getMactSubparts() != null && f.getMactSubparts().size() > 0) {
				for (String s : f.getMactSubparts()) {
					scscFacility.unionMactSubpart(s);
				}
			}
		}

	}

	@Override
	public boolean modifyFacilityFedRules(Facility facility, int userId)
			throws DAOException, RemoteException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		boolean ret = false;

		try {
			facility.setValidated(false);
			facility.setSubmitted(false);
			setLastSubmission(facility);

			// FAL entries for air program changes
		/*	InfrastructureDAO infraDAO = infrastructureDAO(trans);
			ArrayList<FieldAuditLog> falLogs = new ArrayList<FieldAuditLog>();
			if (isDifferent(facility.isMact(), facility.isOldMact())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to Part 63 NESHAP = "
								+ printableValue(facility.isOldMact()),
						printableValue(facility.isMact())));
			}
			if (isDifferent(facility.isNeshaps(), facility.isOldNeshaps())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to Part 61 NESHAP = "
								+ printableValue(facility.isOldNeshaps()),
						printableValue(facility.isNeshaps())));
			}
			if (isDifferent(facility.isNsps(), facility.isOldNsps())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to Part 60 NSPS = "
								+ printableValue(facility.isOldNsps()),
						printableValue(facility.isNsps())));
			}
			if (isDifferent(facility.isPsd(), facility.isOldPsd())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to PSD = "
								+ printableValue(facility.isOldPsd()),
						printableValue(facility.isPsd())));
			}
			if (isDifferent(facility.isNsrNonattainment(),
					facility.isOldNsrNonattainment())) {
				falLogs.add(new FieldAuditLog("coms", facility.getFacilityId(),
						"Subject to Non-attainment NSR = "
								+ printableValue(facility
										.isOldNsrNonattainment()),
						printableValue(facility.isNsrNonattainment())));
			}
			FieldAuditLog[] auditLog = falLogs.toArray(new FieldAuditLog[0]);
			if (userId != -1) {
				infraDAO.createFieldAuditLogs(facility.getFacilityId(),
						facility.getName(), userId, auditLog);
			}*/

			ret = facilityDAO.modifyFacility(facility);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				// update MACT Subparts
				facilityDAO.removeFacilityMACTSubparts(facility.getFpId());
				if (facility.isMact()) {
					for (String mactSubpart : facility.getMactSubparts()) {
						facilityDAO.addFacilityMACTSubpart(facility.getFpId(),
								mactSubpart);
					}
				}

				// update neshaps Subparts
				facilityDAO.removeFacilityNeshapsSubparts(facility.getFpId());
				if (facility.isNeshaps()) {
					for (PollutantCompCode neshapsSubpart : facility
							.getNeshapsSubpartsCompCds()) {
						neshapsSubpart.setFpId(facility.getFpId());
						facilityDAO.addFacilityNeshapsSubpart(neshapsSubpart);
					}
				}

				// update nsps Subparts
				facilityDAO.removeFacilityNSPSSubparts(facility.getFpId());
				if (facility.isNsps()) {
					for (String nspsSubpart : facility.getNspsSubparts()) {
						facilityDAO.addFacilityNSPSSubpart(facility.getFpId(),
								nspsSubpart);
					}
				}

			}

			trans.complete();
		} catch (RemoteException e) {
			logger.error(
					e.getMessage() + " for facility="
							+ facility.getFacilityId(), e);
			throw new DAOException(e.getMessage(), e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	@Override
	public Facility transferEmissionUnit(Facility sourceFacility,
			Integer targetFpId, boolean changeStatus, Timestamp euShutdownDate,
			int userId) throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();

		if (targetFpId == null) {
			DAOException e = new DAOException("invalid fpId (null)");
			throw e;
		}
		Facility targetFacility = null;

		try {

			targetFacility = copyFacilityProfile(targetFpId, new Timestamp(
					System.currentTimeMillis()), userId);

			if (targetFacility == null) {
				DAOException e = new DAOException(
						"Cannot access facility to modify it.");
				throw e;
			}

			/*
			 * Change EU operating status to Shutdown at source facility
			 */
			if (changeStatus) {
				shutdownEUCERP(sourceFacility, euShutdownDate, userId, trans);
			}

			Integer fpId = targetFacility.getFpId();
			CopyOnChangeMaps copyOnChangeMaps = new CopyOnChangeMaps();
			boolean useObjectId = false;
			boolean useSelected = true;
			boolean useSourceFacilityLatLong = false;
			
			// Set Correlation ids to null to trigger getting a new ones.
			// This is necessary because the user may transfer the same
			// EU multiple times into the same target facility. The
			// Correlation ids must be refreshed for EU, Process, CE, and
			// RP to ensure that the Correlation ids for each unit are
			// unique within a facility.
			for (EmissionUnit eu : sourceFacility.getEmissionUnits()) {
				if (eu.isSelected()) {
					eu.setCorrEpaEmuId(null);
					eu.setEuDesc(null);
					EmissionProcess[] emissionProcesses = eu
							.getEmissionProcesses().toArray(
									new EmissionProcess[0]);
					for (EmissionProcess tempEP : emissionProcesses) {
						if (tempEP.isSelected()) {
							tempEP.setCorrelationId(null);
						} else {
							continue;
						}
					}
				} else {
					continue;
				}
			}

			EgressPoint[] egressPoints = sourceFacility.getEgressPoints();
			for (EgressPoint tempEGP : egressPoints) {
				if (tempEGP.isSelected()) {
					tempEGP.setCorrelationId(null);
				} else {
					continue;
				}
			}

			ControlEquipment[] controlEquips = sourceFacility
					.getControlEquips().toArray(new ControlEquipment[0]);
			for (ControlEquipment tempCE : controlEquips) {
				if (tempCE.isSelected()) {
					tempCE.setCorrelationId(null);
				} else {
					continue;
				}
			}

			/*
			 * Transfer selected EU, EP, CE, and RP
			 */
			copyOnChangeMaps = createEUCERP(sourceFacility, fpId,
					copyOnChangeMaps, useObjectId, useSelected, useSourceFacilityLatLong, trans);
			// Disabled creation of EU Transfer notes in IMPACT as requested by AQD. Task 4473
			// createTransferNotes(sourceFacility, targetFacility, userId, trans);
			
			

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("facility=" + targetFacility.getFacilityId(),
					trans, e);
			throw new DAOException(e.getMessage(), e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return targetFacility;

	}
	
	
	private CopyOnChangeMaps createEUCERP(Facility sourceFacility,
			Integer fpId, CopyOnChangeMaps copyOnChangeMaps,
			boolean useObjectId, Boolean useSelected, Boolean useSourceFacilityLatLong, Transaction trans)
			throws DAOException {
		EmissionUnit[] emissionUnits = sourceFacility.getEmissionUnits()
				.toArray(new EmissionUnit[0]);
		EmissionUnit newEU;
		EmissionProcess newEP;
		EgressPoint newEGP;
		ControlEquipment newCE;
		Integer fromFpNodeId;
		Integer toFpNodeId;
		Integer tempFpNodeId;

		EgressPoint[] egressPoints = sourceFacility.getEgressPoints();
		HashMap<String, Integer> newEgressPoints = new HashMap<String, Integer>();

		for (EgressPoint tempEGP : egressPoints) {
			if (Boolean.TRUE.equals(useSelected)) {
				if (tempEGP.isSelected()) {
					tempEGP.setReleasePointId(null);
					tempEGP.setAqdWiseEgressPointId(null);
					tempEGP.setDapcDesc(null);
				} else {
					continue;
				}
			}
			loggerDebug("EGP: " + tempEGP.getReleasePointId()
					+ ", old fpNodeId = " + tempEGP.getFpNodeId()
					+ " to newReleasePoints");
			tempFpNodeId = tempEGP.getFpNodeId();
			tempEGP.setFpId(fpId);
			
			if (Boolean.FALSE.equals(useSourceFacilityLatLong)) {
				Facility targetFacility = getFacilityDAO().retrieveFacility(
						fpId);
				tempEGP.setLatitudeDeg(Float.valueOf(targetFacility.getPhyAddr()
						.getLatitude()));
				tempEGP.setLongitudeDeg(Float.valueOf(targetFacility.getPhyAddr()
						.getLongitude()));
			} else if (Boolean.TRUE.equals(useSourceFacilityLatLong)) {
				tempEGP.setLatitudeDeg(Float.valueOf(sourceFacility.getPhyAddr()
						.getLatitude()));
				tempEGP.setLongitudeDeg(Float.valueOf(sourceFacility.getPhyAddr()
						.getLongitude()));
			}
			if (!useObjectId) {
				tempEGP.setFpNodeId(null);
			}
			newEGP = createEgressPoint(tempEGP, trans);
			newEgressPoints.put(newEGP.getReleasePointId(),
					newEGP.getFpNodeId());
			copyOnChangeMaps.fpNodeIds.put(tempFpNodeId, newEGP.getFpNodeId());
		}
		ControlEquipment[] controlEquips = sourceFacility.getControlEquips()
				.toArray(new ControlEquipment[0]);
		HashMap<String, Integer> newContEquips = new HashMap<String, Integer>();

		for (ControlEquipment tempCE : controlEquips) {
			if (Boolean.TRUE.equals(useSelected)) {
				if (tempCE.isSelected()) {
					tempCE.setControlEquipmentId(null);
					tempCE.setWiseViewId(null);
					tempCE.setDapcDesc(null);
					tempCE.setCeId(0); // setting to 0. dao method has logic to get a new id if this is 0.
				} else {
					continue;
				}
			}
			tempFpNodeId = tempCE.getFpNodeId();
			tempCE.setFpId(fpId);
			if (!useObjectId) {
				tempCE.setFpNodeId(null);
			}
			newCE = createControlEquipment(tempCE, trans);
			newContEquips.put(newCE.getControlEquipmentId(),
					newCE.getFpNodeId());
			copyOnChangeMaps.fpNodeIds.put(tempFpNodeId, newCE.getFpNodeId());
		}

		// already created relationship from Control to Egress
		HashMap<String, String> createdRel = new HashMap<String, String>();

		Integer tempEuId;

		for (EmissionUnit tempEU : emissionUnits) {
			if (Boolean.TRUE.equals(useSelected)) {
				if (tempEU.isSelected()) {
					tempEU.setEpaEmuId(null);
					tempEU.setWiseViewId(null);
					tempEU.setEuDesc(null);
				} else {
					continue;
				}
			}
			// loggerDebug("**** Processing EU: " + tempEU.getEpaEmuId()
			// + " *****");
			EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses()
					.toArray(new EmissionProcess[0]);

			tempEuId = tempEU.getEmuId();
			tempFpNodeId = tempEU.getFpNodeId();
			tempEU.setFpId(fpId);
			if (!useObjectId) {
				tempEU.setFpNodeId(null);
				tempEU.setEmuId(null);
			}
			newEU = createEmissionUnit(tempEU, trans);

			copyOnChangeMaps.euIds.put(tempEuId, newEU.getEmuId());
			copyOnChangeMaps.fpNodeIds.put(tempFpNodeId, newEU.getFpNodeId());

			// loggerDebug("**** Iterating over EU Emission Processes *****");
			for (EmissionProcess tempEP : emissionProcesses) {
				if (Boolean.TRUE.equals(useSelected)) {
					if (tempEP.isSelected()) {
						tempEP.setProcessId(null);
					} else {
						continue;
					}
				}
				// loggerDebug("tempEP process Id = " +
				// tempEP.getProcessId()
				// + ", fpNodeId = " + tempEP.getFpNodeId());
				tempFpNodeId = tempEP.getFpNodeId();
				tempEP.setFpId(fpId);
				tempEP.setEmissionUnitId(newEU.getEmuId());
				if (!useObjectId) {
					tempEP.setFpNodeId(null);
				}
				newEP = createEmissionProcess(tempEP, trans);
				// loggerDebug("newEP process Id = " + newEP.getProcessId()
				// + ", fpNodeId = " + newEP.getFpNodeId());
				fromFpNodeId = newEP.getFpNodeId();
				copyOnChangeMaps.fpNodeIds.put(tempFpNodeId,
						newEP.getFpNodeId());

				EgressPoint[] egressPoints1 = tempEP.getEgressPoints().toArray(
						new EgressPoint[0]);
				// loggerDebug("---- Iterating over EmissionProcess Release Points ----");
				for (EgressPoint tempEGP : egressPoints1) {
					toFpNodeId = newEgressPoints.get(tempEGP
							.getReleasePointId());
					// if release point is in internal copy, but not
					// external, drop it, but log an error.
					if (toFpNodeId == null) {
						logger.error("Release point '"
								+ tempEGP.getReleasePointId()
								+ "' exists in internal copy of profile, but not in copy submitted from gateway.");
						continue;
					}
					// get flow factor
					if (tempEGP.isFugitive()) {
						// no flow factor
						logger.error("DENNIS processNm="
								+ tempEP.getEmissionProcessNm()
								+ ",EgressPointId="
								+ tempEGP.getReleasePointId() + " "
								+ fromFpNodeId + ":" + toFpNodeId);
						createRelationShip(fromFpNodeId, toFpNodeId, trans);
					} else {
						FacilityEmissionFlow fef;
						fef = FacilityEmissionFlow.getEmissionFlow(
								tempEP.getEpEmissionFlows(),
								FacilityEmissionFlow.STACK_TYPE,
								tempEGP.getReleasePointId());
						if (fef != null && fef.getFlowFactor() != null) {
							createRelationShip(fromFpNodeId, toFpNodeId,
									fef.getFlowFactor(), trans);
						} else {
							// this should not happen
							logger.error("Should have had a flowfactor: fromNodeId="
									+ fromFpNodeId
									+ " toFpNodeId="
									+ toFpNodeId);
							createRelationShip(fromFpNodeId, toFpNodeId, trans);
						}
					}
				}

				ControlEquipment[] controlEquips1 = tempEP
						.getControlEquipments()
						.toArray(new ControlEquipment[0]);
				for (ControlEquipment tempCE : controlEquips1) {
					FacilityEmissionFlow fef;
					fef = FacilityEmissionFlow.getEmissionFlow(
							tempEP.getEpEmissionFlows(),
							FacilityEmissionFlow.CE_TYPE,
							tempCE.getControlEquipmentId());
					Float ff = null;
					if (fef == null) {
						// This should not happen
						logger.error("Should have had a flowfactor for control equipment: correlationId="
								+ tempCE.getCorrelationId()
								+ ", fromFpNodeId="
								+ fromFpNodeId);
					} else {
						ff = fef.getFlowFactor();
					}
					createContEquipComp(tempCE, ff, newContEquips,
							newEgressPoints, createdRel, fromFpNodeId, trans);
				}
			}
		}

		return copyOnChangeMaps;
	}

	private void shutdownEUCERP(Facility facility, Timestamp euShutdownDate,
			int userId, Transaction trans) throws DAOException {

		Facility newFacility = null;
		newFacility = copyFacilityProfile(facility.getFpId(), new Timestamp(
				System.currentTimeMillis()), userId);

		if (newFacility == null) {
			DAOException e = new DAOException(
					"Cannot access facility to modify it.");
			throw e;
		}

		boolean isNewFacility = !newFacility.getFpId().equals(
				facility.getFpId());

		for (EmissionUnit eu : facility.getEmissionUnits()) {
			if (eu.isSelected()) {
				// temp Status
				EmissionUnit tempEU = new EmissionUnit();
				tempEU.setFpId(eu.getFpId());
				tempEU.setEmuId(eu.getEmuId());
				tempEU.setFpNodeId(eu.getFpNodeId());
				tempEU.setLastModified(eu.getLastModified());
				tempEU.setOperatingStatusCd(eu.getOperatingStatusCd());
				tempEU.setEuShutdownDate(eu.getEuShutdownDate());

				if (isNewFacility) {
					eu.setFpId(newFacility.getFpId());
					Integer tempId = newFacility.getCopyOnChangeEuIds().get(
							tempEU.getEmuId());
					eu.setEmuId(tempId);
					tempId = newFacility.getCopyOnChangeFpNodeIds().get(
							tempEU.getFpNodeId());
					eu.setFpNodeId(tempId);
					eu.setLastModified(1);
				}

				// Shutdown EU
				eu.setOperatingStatusCd(EuOperatingStatusDef.SD);
				eu.setEuShutdownDate(euShutdownDate);
				this.modifyEmissionUnit(eu, userId, trans);

				// trun back Status for transfer
				eu.setFpId(tempEU.getFpId());
				eu.setEmuId(tempEU.getEmuId());
				eu.setFpNodeId(tempEU.getFpNodeId());
				eu.setLastModified(tempEU.getLastModified());
				eu.setOperatingStatusCd(tempEU.getOperatingStatusCd());
				eu.setEuShutdownDate(tempEU.getEuShutdownDate());

			}
		}

		for (ControlEquipment ce : facility.getControlEquips()) {
			if (ce.isSelected()) {
				// temp Status
				ControlEquipment tempCE = new ControlEquipment();
				tempCE.setFpId(ce.getFpId());
				tempCE.setFpNodeId(ce.getFpNodeId());
				tempCE.setLastModified(ce.getLastModified());
				tempCE.setOperatingStatusCd(ce.getOperatingStatusCd());

				if (isNewFacility) {
					ce.setFpId(newFacility.getFpId());
					Integer tempId = newFacility.getCopyOnChangeFpNodeIds()
							.get(ce.getFpNodeId());
					ce.setFpNodeId(tempId);
					ce.setLastModified(1);
				}

				// Shutdown CE
				ce.setOperatingStatusCd(CeOperatingStatusDef.NOP);
				this.modifyControlEquipment(ce, userId);

				// trun back Status for transfer
				ce.setFpId(tempCE.getFpId());
				ce.setFpNodeId(tempCE.getFpNodeId());
				ce.setLastModified(tempCE.getLastModified());
				ce.setOperatingStatusCd(tempCE.getOperatingStatusCd());
			}
		}

		for (EgressPoint rp : facility.getEgressPoints()) {
			if (rp.isSelected()) {
				// temp Status
				EgressPoint tempRP = new EgressPoint();
				tempRP.setFpId(rp.getFpId());
				tempRP.setFpNodeId(rp.getFpNodeId());
				tempRP.setLastModified(rp.getLastModified());
				tempRP.setOperatingStatusCd(rp.getOperatingStatusCd());

				if (isNewFacility) {
					rp.setFpId(newFacility.getFpId());
					Integer tempId = newFacility.getCopyOnChangeFpNodeIds()
							.get(rp.getFpNodeId());
					rp.setFpNodeId(tempId);
					rp.setLastModified(1);
				}

				// Shutdown RP
				rp.setOperatingStatusCd(EgOperatingStatusDef.NOP);
				this.modifyEgressPoint(rp, userId);

				// trun back Status for transfer
				rp.setFpId(tempRP.getFpId());
				rp.setFpNodeId(tempRP.getFpNodeId());
				rp.setLastModified(tempRP.getLastModified());
				rp.setOperatingStatusCd(tempRP.getOperatingStatusCd());
			}
		}

	}

	private final String retrieveEmissionUnitTypeCd(int emuId, Transaction trans)
			throws DAOException {
		return facilityDAO(trans).retrieveEmissionUnitTypeCd(emuId);
	}

	private final String retrieveControlEquipmentTypeCd(int equId)
			throws DAOException {
		return getFacilityDAO().retrieveControlEquipmentTypeCd(equId);
	}

	private final String retrieveReleasePointTypeCd(int fpNodeId)
			throws DAOException {
		return getFacilityDAO().retrieveReleasePointTypeCd(fpNodeId);
	}

	public final String getEmissionUnitIdChangedLog(FacilityDAO facilityDAO,
			Transaction trans, EmissionUnit emissionUnit) throws DAOException {
		String newEuId = emissionUnit.getEpaEmuId();

		if (Utility.isNullOrEmpty(newEuId))
			return "";

		int emuId = emissionUnit.getEmuId();
		String oldEuId = facilityDAO.retrieveEmissionUnitDisplayId(emuId);

		String changedNote = generateEuIdChangedNote(oldEuId, newEuId);
		String desc = emissionUnit.getEuDesc() + changedNote;

		if (desc.length() > 1000) {
			int fpId = emissionUnit.getFpId();
			String facilityId = facilityDAO.retrieveFacility(fpId)
					.getFacilityId();
			moveIdChangedLogToFacilityNote(trans, fpId, facilityId, desc);
			desc = " [EU ID Changed Log] The log was moved to the facility note. ";
		}

		return desc;
	}

	public final String getControlEquipmentIdChangedLog(
			FacilityDAO facilityDAO, Transaction trans,
			ControlEquipment controlEquipment) throws DAOException {
		String newEquId = controlEquipment.getControlEquipmentId();

		if (Utility.isNullOrEmpty(newEquId))
			return "";

		int fpNodeId = controlEquipment.getFpNodeId();
		String oldEquId = facilityDAO.retrieveControlEquipmentId(fpNodeId);

		String changedNote = generateCeIdChangedNote(oldEquId, newEquId);
		String desc = controlEquipment.getDapcDesc() + changedNote;

		if (desc.length() > 1000) {
			int fpId = controlEquipment.getFpId();
			String facilityId = facilityDAO.retrieveFacility(fpId)
					.getFacilityId();
			moveIdChangedLogToFacilityNote(trans, fpId, facilityId, desc);
			desc = " [CE ID Changed Log] The log was moved to the facility note. ";
		}

		return desc;
	}

	public final String getReleasePointIdChangedLog(FacilityDAO facilityDAO,
			Transaction trans, EgressPoint releasePoint) throws DAOException {
		String newRpId = releasePoint.getReleasePointId();

		if (Utility.isNullOrEmpty(newRpId))
			return "";

		int fpNodeId = releasePoint.getFpNodeId();
		String oldRpId = facilityDAO.retrieveReleasePointId(fpNodeId);
		String desc = releasePoint.getDapcDesc();
		String changedNote = generateRpIdChangedNote(oldRpId, newRpId);
		desc = desc + changedNote;

		if (desc.length() > 1000) {
			int fpId = releasePoint.getFpId();
			String facilityId = facilityDAO.retrieveFacility(fpId)
					.getFacilityId();
			moveIdChangedLogToFacilityNote(trans, fpId, facilityId, desc);
			desc = " [RP ID Changed Log] The log was moved to the facility note. ";
		}

		return desc;
	}

	private final String generateCeIdChangedNote(String oldCeId, String newCeId) {
		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		return " [CE ID Changed Log] " + dateFormat.format(datetime) + ", "
				+ oldCeId + " >> " + newCeId + ". ";
	}

	private final String generateEuIdChangedNote(String oldEuId, String newEuId) {
		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		return " [EU ID Changed Log] " + dateFormat.format(datetime) + ", "
				+ oldEuId + " >> " + newEuId + ". ";
	}

	private final String generateRpIdChangedNote(String oldRpId, String newRpId) {
		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		return " [RP ID Changed Log] " + dateFormat.format(datetime) + ", "
				+ oldRpId + " >> " + newRpId + ". ";
	}

	private final boolean isTheSameEquipmentTypeInDb(
			ControlEquipment controlEquipment) throws DAOException {
		int fpNodeId = controlEquipment.getFpNodeId();
		String newTypeCd = controlEquipment.getEquipmentTypeCd();
		String oldTypeCd = retrieveControlEquipmentTypeCd(fpNodeId);

		return newTypeCd.equals(oldTypeCd);
	}

	private final boolean isTheSameEmissionUnitTypeInDb(
			EmissionUnit emissionUnit, Transaction trans) throws DAOException {
		int emuId = emissionUnit.getEmuId();
		String newTypeCd = emissionUnit.getEmissionUnitTypeCd();
		String oldTypeCd = retrieveEmissionUnitTypeCd(emuId, trans);

		return newTypeCd.equals(oldTypeCd);
	}

	private final boolean isTheSameReleasePointTypeInDb(EgressPoint releasePoint)
			throws DAOException {
		int fpNodeId = releasePoint.getFpNodeId();
		String newTypeCd = releasePoint.getEgressPointTypeCd();
		String oldTypeCd = retrieveReleasePointTypeCd(fpNodeId);

		return newTypeCd.equals(oldTypeCd);
	}

	// *********** WISE View Migration Region ***********

	@Override
	public void migrateWiseViewData(boolean deleteExistingData, String migrationTempFolder, String logPath)
			throws DAOException {
		emissionUnitCsvFilePath = migrationTempFolder + "\\EmissionUnits.csv";
		emissionProcessCsvFilePath = migrationTempFolder + "\\EmissionProcesses.csv";
		controlEquipCsvFilePath = migrationTempFolder + "\\ControlEquipments.csv";
		releasePointCsvFilePath = migrationTempFolder + "\\ReleasePoints.csv";
		releatedFacilityIds = new HashMap<String, Integer>();
		this.currentMigrationCount = 0;

		try {
			printWriter = new PrintWriter(new File(logPath));

			wirteMigrateLog("The path of the log file: " + logPath);

			if (!validateMigrationFileNames())
				return;

			int euTotalCount = 0;
			int epTotalCount = 0;
			int ceTotalCount = 0;
			int rpTotalCount = 0;

			euTotalCount = getCsvDataTotalCount(emissionUnitCsvFilePath);
			epTotalCount = getCsvDataTotalCount(emissionProcessCsvFilePath);
			ceTotalCount = getCsvDataTotalCount(controlEquipCsvFilePath);
			rpTotalCount = getCsvDataTotalCount(releasePointCsvFilePath);
			totalMigrationCount = euTotalCount + epTotalCount + ceTotalCount + rpTotalCount;

			if (totalMigrationCount == 0) {
				wirteMigrateError("Migration data not found");
				return;
			}
			HashMap<String, Integer> wiseviewToEmuIdMap = addWiseViewEmissionUnits(deleteExistingData);

			if (releatedFacilityIds.size() == 0)
				return;

			HashMap<String, Integer> wiseviewToProcessIdMap = addWiseViewEmissionProcesses(wiseviewToEmuIdMap);
			HashMap<String, Integer> wiseviewToCEIdMap = addWiseViewControlEuqipments(wiseviewToProcessIdMap);
			addWiseViewReleasePoints(wiseviewToProcessIdMap, wiseviewToCEIdMap);

			// Set up the CopyOnChange flag
			turnOnFacilitiesCopyOnChange();

		} catch (FileNotFoundException e) {
			DisplayUtil.displayError(e.getMessage());

		} finally {
			if (printWriter != null) {
				printWriter.close();
				printWriter = null;
			}
		}
	}

	// *********** WISE View Migration Region ***********

	@Override
	public void deleteFacilityInventory(String migrationTempFolder, String logPath) throws DAOException {
		FacilityIdCsvFilePath = migrationTempFolder + "\\FacilityIds.csv";
		try {
			printWriter = new PrintWriter(new File(logPath));
			wirteMigrateLog("The path of the log file: " + logPath);
			CSVReader csvReader = null;
			String[] rawData;
			int facilityId;
			csvReader = new CSVReader(new FileReader(FacilityIdCsvFilePath));

			while ((rawData = csvReader.readNext()) != null) {
				facilityId = Integer.parseInt(rawData[0]);
				boolean isItDone = deleteFacilityInv(facilityId);
				if (isItDone) {
					wirteMigrateLog("FacilityId " + facilityId + " inventory deleted successfully");
				} else {
					wirteMigrateError("FacilityId " + facilityId + " inventory not deleted due to known reason");
				}
			}

		} catch (Exception e) {
			logger.error("Error during wiseview deleteFacilityInventory ", e);
		} finally {
			if (printWriter != null) {
				printWriter.close();
				printWriter = null;
			}
		}
	}

	public Integer preserveFacilityInventory(String migrationTempFolder, String logPath) throws DAOException {
		FacilityIdCsvFilePath = migrationTempFolder + "\\FacilityIds.csv";
		Integer fpId = 0;
		try {
			printWriter = new PrintWriter(new File(logPath));
			wirteMigrateLog("The path of the log file: " + logPath);
			CSVReader csvReader = null;
			String[] rawData;
			int facilityId;
			csvReader = new CSVReader(new FileReader(FacilityIdCsvFilePath));

			while ((rawData = csvReader.readNext()) != null) {
				facilityId = Integer.parseInt(rawData[0]);
				fpId = preserveFacilityInv(facilityId);
				if (fpId != null) {
					wirteMigrateLog("FacilityId " + facilityId + " inventory preserved successfully");
				} else {
					wirteMigrateError("FacilityId " + facilityId + " inventory not preserved due to known reason");
				}
			}

			DisplayUtil.displayInfo("Facility Inventory deletion completed");
		} catch (Exception e) {
			DisplayUtil.displayError(e.getMessage());
			return null;
		} finally {
			if (printWriter != null) {
				printWriter.close();
				printWriter = null;
			}

		}

		return fpId;
	}

	private boolean validateMigrationFileNames() {
		// Check the name of the files
		if (!new File(emissionUnitCsvFilePath).exists()) {
			wirteMigrateError("Emission Unit migration csv file not found. Please check the zip file again.");
			return false;
		}

		if (!new File(emissionProcessCsvFilePath).exists()) {
			wirteMigrateError("Emission Process migration csv file not found. Please check the zip file again.");
			return false;
		}

		if (!new File(controlEquipCsvFilePath).exists()) {
			wirteMigrateError("Control Equipment migration csv file not found. Please check the zip file again.");
			return false;
		}

		if (!new File(releasePointCsvFilePath).exists()) {
			wirteMigrateError("Release Point migration csv file not found. Please check the zip file again.");
			return false;
		}

		return true;
	}

	private void turnOnFacilitiesCopyOnChange() throws DAOException {
		for (String facId : releatedFacilityIds.keySet()) {
			setFacilityCopyOnChange(facId, true);
		}
	}

	public HashMap<String, Integer> addWiseViewEmissionUnits(
			boolean deleteExistingData) throws DAOException {
		HashMap<String, Integer> releatedFacIds = new HashMap<String, Integer>();
		HashMap<String, Integer> emuMap = new HashMap<String, Integer>();
		CSVReader csvReader = null;

		try {
			String[] rawData;

			csvReader = new CSVReader(new FileReader(emissionUnitCsvFilePath));
			
			boolean isFirstRow = true;
			
			while ((rawData = csvReader.readNext()) != null) {
				if (isFirstRow) {
					isFirstRow = false;
					if (StringUtils.equalsIgnoreCase(rawData[0], "facility_id")) {
						logger.info("addWiseViewEmissionUnits: skipping eu header row.");
						continue;
					}
				}
				if (rawData.length == 0 || (rawData.length == 1 && StringUtils.isEmpty(rawData[0]))) {
					wirteMigrateError("The Emission Unit csv row is empty.");
					continue;
				}
				
				if (rawData.length < 80) {
					wirteMigrateError(
							"The Emission Unit csv row has fewer columns than expected, csv raw data: ",
							Utility.toString(rawData, ", "));
					continue;
				}
				createWiseViewEmissionUnit(rawData, releatedFacIds, emuMap,
						deleteExistingData);

			}
		} catch (Exception efo) {
			wirteMigrateError(efo.getMessage());

		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException eio) {
					wirteMigrateError(eio.getMessage());
				}
			}
		}
		releatedFacilityIds.putAll(releatedFacIds);
		return emuMap;
	}
	
	

	public HashMap<String, Integer> addNewEmissionUnits(
			boolean deleteExistingData, Integer oldFpId) throws DAOException {
		HashMap<String, Integer> releatedFacilityIds = new HashMap<String, Integer>();
		CSVReader csvReader = null;

		try {
			String[] rawData;

			csvReader = new CSVReader(new FileReader(emissionUnitCsvFilePath));

			while ((rawData = csvReader.readNext()) != null) {
				if (rawData.length < 55) {
					wirteMigrateError(
							"The Emission Unit csv format is incorrect, csv raw data: ",
							Utility.toString(rawData, ", "));
					continue;
				}
				createWiseViewEmissionUnit(rawData, releatedFacilityIds,
						deleteExistingData, oldFpId);

			}
		} catch (Exception efo) {
			wirteMigrateError(efo.getMessage());

		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException eio) {
					wirteMigrateError(eio.getMessage());
				}
			}
		}

		return releatedFacilityIds;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void createWiseViewEmissionUnit(String[] rawData,
			HashMap<String, Integer> releatedFacilityIds,
			boolean deleteExistingData, Integer oldFpId) {

		String facilityId;
		Integer fpId = null;
		String wiseViewId;
		Transaction trans = TransactionFactory.createTransaction();

		try {

			EmissionUnit migrateEu = convertRawDataToEmissionUnit(rawData);
			wiseViewId = migrateEu.getWiseViewId();
			facilityId = migrateEu.getFacilityId();

			wirteMigrateLog("Start to create Emission Unit, WISEView ID is "
					+ wiseViewId);

			if (!releatedFacilityIds.containsKey(facilityId)) {
				fpId = facilityDAO().retrieveFpIdByFacilityId(facilityId);

				if (fpId == null) {
					wirteMigrateError(
							"The error is during the Emission Unit conversion, "
									+ "The Facility does not exist in the database, Facility ID: "
									+ facilityId, wiseViewId);
					return;
				}

				releatedFacilityIds.put(facilityId, fpId);

				// Turn Off CopyOnChagne
				setFacilityCopyOnChange(facilityId, false);

				if (deleteExistingData) {
					wirteMigrateLog("Delete Facility Delete All Sublayer Objects, Facility ID is "
							+ facilityId);
					deleteFaciltySublayerNodes(fpId, trans);
				}
			} else {
				fpId = releatedFacilityIds.get(facilityId);
			}

			migrateEu.setFpId(fpId);

			wirteMigrateLog("Start to create Emission Unit, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			createEmissionUnitBasedOnPreviousVersion(migrateEu, oldFpId, trans);

			wirteMigrateLog("End to create Emission Unit, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);
			trans.complete();
		} catch (Exception e) {
			wirteMigrateError(e.getMessage());
			if (trans != null)
				trans.cancel();

		} finally {
			// closeTransaction(trans);
			this.currentMigrationCount++;
//			refreshMigrationDisplayInfo();
		}

	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void createWiseViewEmissionUnit(String[] rawData,
			HashMap<String, Integer> releatedFacilityIds, HashMap<String, Integer> emuMap,
			boolean deleteExistingData) {

		String facilityId;
		Integer fpId = null;
		String wiseViewId;
		Transaction trans = TransactionFactory.createTransaction();

		try {

			EmissionUnit migrateEu = convertRawDataToEmissionUnit(rawData);
			wiseViewId = migrateEu.getWiseViewId();
			facilityId = migrateEu.getFacilityId();

			wirteMigrateLog("Start to create Emission Unit, WISEView ID is "
					+ wiseViewId);

			if (!releatedFacilityIds.containsKey(facilityId)) {
				fpId = facilityDAO().retrieveFpIdByFacilityId(facilityId);

				if (fpId == null) {
					wirteMigrateError(
							"The error is during the Emission Unit conversion, "
									+ "The Facility does not exist in the database, Facility ID: "
									+ facilityId, wiseViewId);
					return;
				}

				releatedFacilityIds.put(facilityId, fpId);

				// Turn Off CopyOnChagne
				setFacilityCopyOnChange(facilityId, false);

				if (deleteExistingData) {
					wirteMigrateLog("Delete Facility Delete All Sublayer Objects, Facility ID is "
							+ facilityId);
					deleteFaciltySublayerNodes(fpId, trans);
				}
			} else {
				fpId = releatedFacilityIds.get(facilityId);
			}

			migrateEu.setFpId(fpId);

			wirteMigrateLog("Start to create Emission Unit, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			migrateEu = createEmissionUnit(migrateEu, trans);
			emuMap.put(wiseViewId, migrateEu.getEmuId());
			
			wirteMigrateLog("End to create Emission Unit, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);
			trans.complete();
		} catch (Exception e) {
			wirteMigrateError(e.getMessage());
			if (trans != null)
				trans.cancel();

		} finally {
			// closeTransaction(trans);
			this.currentMigrationCount++;
//			refreshMigrationDisplayInfo();
		}

	}

	public HashMap<String, Integer> addWiseViewEmissionProcesses(HashMap<String, Integer> emuMap) throws DAOException {
		CSVReader csvReader = null;
		HashMap<String, Integer> wiseviewToProcessIdMap = new HashMap<String, Integer>();
		
		try {

			String[] rawData;

			csvReader = new CSVReader(
					new FileReader(emissionProcessCsvFilePath));

			while ((rawData = csvReader.readNext()) != null) {
				if (rawData.length == 0 || (rawData.length == 1 && StringUtils.isEmpty(rawData[0]))) {
					wirteMigrateError("The Emission Process csv row is empty.");
					continue;
				}
				if (rawData.length < 4) {
					wirteMigrateError("The Emission Process csv format is incorrect, csv raw data: "
							+ Utility.toString(rawData, ", "));
					continue;
				}

				createWiseViewEmissionProcess(rawData, releatedFacilityIds, emuMap, wiseviewToProcessIdMap);
			}
		} catch (Exception efo) {
			wirteMigrateError(efo.getMessage());

		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException eio) {
					wirteMigrateError(eio.getMessage());
				}
			}
		}
		return wiseviewToProcessIdMap;
	}
	
	public void addNewEmissionProcesses(
			HashMap<String, Integer> releatedFacilityIds, Integer oldFpId) throws DAOException {
		CSVReader csvReader = null;

		try {

			String[] rawData;

			csvReader = new CSVReader(
					new FileReader(emissionProcessCsvFilePath));

			while ((rawData = csvReader.readNext()) != null) {
				if (rawData.length < 4) {
					wirteMigrateError("The Emission Process csv format is incorrect, csv raw data: "
							+ Utility.toString(rawData, ", "));
					continue;
				}

				createWiseViewEmissionProcessBasedOnOldVesrion(rawData, releatedFacilityIds, oldFpId);
			}
		} catch (Exception efo) {
			wirteMigrateError(efo.getMessage());

		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException eio) {
					wirteMigrateError(eio.getMessage());
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void createWiseViewEmissionProcess(String[] rawData,
			HashMap<String, Integer> releatedFacilityIds, HashMap<String, Integer> emuMap, HashMap<String, Integer> wiseviewToProcessIdMap) {

		String facilityId;
		Integer fpId = null;
		String wiseViewId;
		Transaction trans = TransactionFactory.createTransaction();

		try {

			EmissionProcess migrateEp = convertRawDataToEmissionProcess(rawData, emuMap);

			if (migrateEp == null) {
				wirteMigrateError("The error is during the Emission Process conversion, "
						+ "The Emission Process csv file is incorrect, csv raw data: "
						+ Utility.toString(rawData, ", "));
				return;
			}
			wiseViewId = migrateEp.getWiseViewId();
			facilityId = migrateEp.getFacilityId();

			wirteMigrateLog("Start to create Emission Process, WISEView ID is "
					+ wiseViewId);

			if (releatedFacilityIds.containsKey(facilityId)) {
				fpId = releatedFacilityIds.get(facilityId);

			} else {
				wirteMigrateError(
						"The error is during the Emission Process conversion, "
								+ "The Emission Process csv file is incorrect "
								+ "The Facility ID is incorrect "
								+ "Facility ID: " + facilityId, wiseViewId);
				return;
			}

			migrateEp.setFpId(fpId);

			migrateEp = createEmissionProcess(migrateEp, trans);
			wiseviewToProcessIdMap.put(wiseViewId, migrateEp.getFpNodeId());
			
			wirteMigrateLog("End to create Emission Process, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);
			trans.complete();
		} catch (DAOException e) {
			wirteMigrateError(e.getMessage());
			if (trans != null)
				trans.cancel();

		} finally {
			// closeTransaction(trans);
			this.currentMigrationCount++;
//			refreshMigrationDisplayInfo();
		}

	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void createWiseViewEmissionProcessBasedOnOldVesrion(String[] rawData,
			HashMap<String, Integer> releatedFacilityIds, Integer oldFpId) {

		String facilityId;
		Integer fpId = null;
		String wiseViewId;
		Transaction trans = TransactionFactory.createTransaction();

		try {

			EmissionProcess migrateEp = convertRawDataToEmissionProcess(rawData, new HashMap<String, Integer>());

			if (migrateEp == null) {
				wirteMigrateError("The error is during the Emission Process conversion, "
						+ "The Emission Process csv file is incorrect, csv raw data: "
						+ Utility.toString(rawData, ", "));
				return;
			}
			wiseViewId = migrateEp.getWiseViewId();
			facilityId = migrateEp.getFacilityId();

			wirteMigrateLog("Start to create Emission Process, WISEView ID is "
					+ wiseViewId);

			if (releatedFacilityIds.containsKey(facilityId)) {
				fpId = releatedFacilityIds.get(facilityId);

			} else {
				wirteMigrateError(
						"The error is during the Emission Process conversion, "
								+ "The Emission Process csv file is incorrect "
								+ "The Facility ID is incorrect "
								+ "Facility ID: " + facilityId, wiseViewId);
				return;
			}

			migrateEp.setFpId(fpId);

			createEmissionProcessBasedOnOldVersion(migrateEp, oldFpId, trans);

			wirteMigrateLog("End to create Emission Process, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);
			trans.complete();
		} catch (DAOException e) {
			wirteMigrateError(e.getMessage());
			if (trans != null)
				trans.cancel();

		} finally {
			// closeTransaction(trans);
			this.currentMigrationCount++;
//			refreshMigrationDisplayInfo();
		}

	}

	private final HashMap<String, Integer> addWiseViewControlEuqipments(final HashMap<String, Integer> wiseviewToProcessIdMap) throws DAOException {
		CSVReader csvReader = null;
		HashMap<String, Integer> wiseviewToCEIdMap = new HashMap<String, Integer>();
		
		try {
			String[] rawData;
			ControlEquipment migrateCe = null;
			String previousParentWiseViewId = null;
			String parentWiseViewId = "";
			String previousWiseViewId = null;
			String wiseViewId = "";
			Integer previousParentEpFpnodeId = null;
			Integer epFpnodeId = null;
			String previousTypeCd = null;
			String typeCd = "";
			csvReader = new CSVReader(new FileReader(controlEquipCsvFilePath));

			
			while ((rawData = csvReader.readNext()) != null) {
				try {
					
					if (rawData.length == 0 || (rawData.length == 1 && StringUtils.isEmpty(rawData[0]))) {
						wirteMigrateError("The Control Equipment csv row is empty.");
						continue;
					}
					
					if (rawData.length < 133) {
						wirteMigrateError("The Control Equipment csv format is incorrect, csv raw data: "
								+ Utility.toString(rawData, ", "));
						continue;
					}

					wiseViewId = rawData[1];
					parentWiseViewId = rawData[2];
					ControlEquipment tempMigrateCe = convertRawDataToControlEquipment(rawData);

					if (Utility.isNullOrEmpty(parentWiseViewId)) {
						wirteMigrateError(
								"The Control Equipment Parent WISE View ID cannot be null or empty",
								rawData[1]);
						continue;
					}

					//check to see if the CE needs to be associated to a process
					epFpnodeId = wiseviewToProcessIdMap.get(parentWiseViewId);
/*					if (epFpnodeId == null) {
						//If a parent process id was not found, check to see if the CE needs to be associated to another CE
						epFpnodeId = wiseviewToCEIdMap.get(parentWiseViewId);
					}*/
					if (epFpnodeId == null) {
						wirteMigrateError(
								"The error is during the Control Equipment conversion, "
										+ "The related Emission Process is not found, Emission Process WISE View ID: "
										+ parentWiseViewId, rawData[1]);
						continue;
					}
					
					if (tempMigrateCe == null) {
						if (wiseviewToCEIdMap.get(migrateCe.getWiseViewId()) != null) {
							createRelationShip(previousParentEpFpnodeId, wiseviewToCEIdMap.get(migrateCe.getWiseViewId()), null);
							continue;
						}
						createMigrateControlEquip(releatedFacilityIds,
								previousParentEpFpnodeId, migrateCe);
						wiseviewToCEIdMap.put(migrateCe.getWiseViewId(), migrateCe.getFpNodeId());
						migrateCe = null;
						continue;
					}

					wirteMigrateLog("Start to create Control Equipment, WISEView ID is "
							+ tempMigrateCe.getWiseViewId());

					typeCd = tempMigrateCe.getEquipmentTypeCd();
					boolean isFirstRecord = migrateCe == null;

					if (isFirstRecord) {
						migrateCe = tempMigrateCe;

						continue;
					}

					boolean isNewCe = !previousWiseViewId.equalsIgnoreCase(wiseViewId)
							|| !previousParentWiseViewId.equalsIgnoreCase(parentWiseViewId)
							|| !previousTypeCd.equalsIgnoreCase(typeCd);

					if (!isNewCe) {
						appendPollutantControls(migrateCe, tempMigrateCe);
						continue;
					}

					if (wiseviewToCEIdMap.get(migrateCe.getWiseViewId()) != null) {
						createRelationShip(previousParentEpFpnodeId, wiseviewToCEIdMap.get(migrateCe.getWiseViewId()), null);
					} else {
						createMigrateControlEquip(releatedFacilityIds,
								previousParentEpFpnodeId, migrateCe);
						wiseviewToCEIdMap.put(migrateCe.getWiseViewId(), migrateCe.getFpNodeId());
					}
					
					migrateCe = tempMigrateCe;

				} catch (Exception e) {
					wirteMigrateError(e.getMessage());

				} finally {
					previousTypeCd = typeCd;
					previousParentWiseViewId = parentWiseViewId;
					previousWiseViewId = wiseViewId;
					previousParentEpFpnodeId = epFpnodeId;
					this.currentMigrationCount++;
//					refreshMigrationDisplayInfo();
				}
			}
			if (wiseviewToCEIdMap.get(migrateCe.getWiseViewId()) != null) {
				createRelationShip(epFpnodeId, wiseviewToCEIdMap.get(migrateCe.getWiseViewId()), null);
			} else {
				createMigrateControlEquip(releatedFacilityIds, epFpnodeId, migrateCe);
				wiseviewToCEIdMap.put(migrateCe.getWiseViewId(), migrateCe.getFpNodeId());
			}
		} catch (Exception efo) {
			wirteMigrateError(efo.getMessage());

		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException eio) {
					wirteMigrateError(eio.getMessage());
				}
			}
		}
		return wiseviewToCEIdMap;
	}

	
	
	private final void addNewControlEuqipments(
			HashMap<String, Integer> releatedFacilityIds,Integer oldFpId) throws DAOException {
		CSVReader csvReader = null;

		try {
			String[] rawData;
			ControlEquipment migrateCe = null;
			String previousParentWiseViewId = null;
			String parentWiseViewId = "";
			String previousTypeCd = null;
			String typeCd = "";
			csvReader = new CSVReader(new FileReader(controlEquipCsvFilePath));

			while ((rawData = csvReader.readNext()) != null) {
				try {
					if (rawData.length < 134) {
						wirteMigrateError("The Control Equipment csv format is incorrect, csv raw data: "
								+ Utility.toString(rawData, ", "));
						continue;
					}

					parentWiseViewId = rawData[2];
					ControlEquipment tempMigrateCe = convertRawDataToControlEquipment(rawData);

					if (tempMigrateCe == null) {
						createMigrateControlEquipBasedOnPreviousVersion(releatedFacilityIds,
								previousParentWiseViewId, migrateCe, oldFpId);
						migrateCe = null;
						continue;
					}

					wirteMigrateLog("Start to create Control Equipment, WISEView ID is "
							+ tempMigrateCe.getWiseViewId());

					typeCd = tempMigrateCe.getEquipmentTypeCd();
					boolean isFirstRecord = migrateCe == null;

					if (isFirstRecord) {
						migrateCe = tempMigrateCe;

						continue;
					}

					boolean isNewCe = !previousParentWiseViewId
							.equalsIgnoreCase(parentWiseViewId)
							|| !previousTypeCd.equalsIgnoreCase(typeCd);

					if (!isNewCe) {
						appendPollutantControls(migrateCe, tempMigrateCe);
						continue;
					}

					createMigrateControlEquipBasedOnPreviousVersion(releatedFacilityIds,
							previousParentWiseViewId, migrateCe, oldFpId);
					migrateCe = tempMigrateCe;

				} catch (Exception e) {
					wirteMigrateError(e.getMessage());

				} finally {
					previousTypeCd = typeCd;
					previousParentWiseViewId = parentWiseViewId;
					this.currentMigrationCount++;
//					refreshMigrationDisplayInfo();
				}
			}

			createMigrateControlEquipBasedOnPreviousVersion(releatedFacilityIds, parentWiseViewId,
					migrateCe, oldFpId);

		} catch (Exception efo) {
			wirteMigrateError(efo.getMessage());

		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException eio) {
					wirteMigrateError(eio.getMessage());
				}
			}
		}
	}
	
	public void addWiseViewReleasePoints(HashMap<String, Integer> wiseviewToProcessIdMap, HashMap<String, Integer> wiseviewToCEIdMap) throws DAOException {
		CSVReader csvReader = null;
		HashMap<String, Integer> wiseviewToRPIdMap = new HashMap<String, Integer>();
		try {

			String[] rawData;

			csvReader = new CSVReader(new FileReader(releasePointCsvFilePath));

			while ((rawData = csvReader.readNext()) != null) {
				createWiseViewReleasePoints(rawData, wiseviewToRPIdMap, wiseviewToProcessIdMap, wiseviewToCEIdMap);
			}
		} catch (Exception efo) {
			wirteMigrateError(efo.getMessage());

		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException eio) {
					wirteMigrateError(eio.getMessage());
				}
			}
		}
	}
	
	
	public void addNewReleasePoints(
			HashMap<String, Integer> releatedFacilityIds, Integer oldFpId) throws DAOException {
		CSVReader csvReader = null;

		try {

			String[] rawData;

			csvReader = new CSVReader(new FileReader(releasePointCsvFilePath));

			while ((rawData = csvReader.readNext()) != null) {
				createWiseViewReleasePointsBasedOnPreviousVersion(rawData, releatedFacilityIds, oldFpId);
			}
		} catch (Exception efo) {
			wirteMigrateError(efo.getMessage());

		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch (IOException eio) {
					wirteMigrateError(eio.getMessage());
				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void createWiseViewReleasePoints(String[] rawData, HashMap<String, Integer> wiseviewToRPIdMap,
			HashMap<String, Integer> wiseviewToProcessIdMap, HashMap<String, Integer> wiseviewToCEIdMap) {

		String facilityId;
		Integer fpId = null;
		String wiseViewId;

		Transaction trans = TransactionFactory.createTransaction();

		try {
			if (rawData.length == 0 || (rawData.length == 1 && StringUtils.isEmpty(rawData[0]))) {
				wirteMigrateError("The Release Point csv row is empty.");
				return;
			}
			
			if (rawData.length < 15) {
				wirteMigrateError("The Release Point csv format is incorrect, csv raw data: "
						+ Utility.toString(rawData, ", "));
				return;
			}

			String parentWiseViewId = rawData[2];

			if (Utility.isNullOrEmpty(parentWiseViewId)) {
				wirteMigrateError("The Release Point Parent WISE View ID cannot be null or empty");
				return;
			}

			EgressPoint migrateRp = convertRawDataToReleasePoint(rawData);

			if (migrateRp == null) {
				wirteMigrateError(
						"The error is during the Release Point conversion, "
								+ "The Release Point csv file is incorrect, csv raw data:  ",
						Utility.toString(rawData, ", "));
				return;
			}

			wiseViewId = migrateRp.getAqdWiseEgressPointId();
			facilityId = migrateRp.getFacilityId();

			wirteMigrateLog("Start to create Release Point, WISEView ID is "
					+ wiseViewId);

			if (releatedFacilityIds.containsKey(facilityId)) {
				fpId = releatedFacilityIds.get(facilityId);
			} else {
				wirteMigrateError(
						"The error is during the Release Point conversion, "
								+ "The Release Point csv file is incorrect "
								+ "The Facility ID is incorrect "
								+ "Facility ID: " + facilityId, wiseViewId);
				return;
			}

			migrateRp.setFpId(fpId);

			if (wiseviewToRPIdMap.get(wiseViewId) == null) {
				// RP is not in the map, so it is a new RP. create RP.
				migrateRp = createEgressPoint(migrateRp, trans);
				wiseviewToRPIdMap.put(wiseViewId, migrateRp.getFpNodeId());
			} else {
				//RP is in map, just get the fpnodeid
				migrateRp.setFpNodeId(wiseviewToRPIdMap.get(wiseViewId));
			}

			wirteMigrateLog("End to create Release Point, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);


			Integer ceFpNodeId = wiseviewToProcessIdMap.get(parentWiseViewId);
			if (ceFpNodeId != null) {
				createRelationShip(ceFpNodeId, migrateRp.getFpNodeId(), trans);
			} else {
				Integer epFpNodeId = wiseviewToCEIdMap.get(parentWiseViewId);
				if (epFpNodeId == null) {
					wirteMigrateError("The error is during the Release Point conversion, "
							+ "The related Emission Process is not found, Emission Process WISE View ID: "
							+ parentWiseViewId);
					return;
				}
				createRelationShip(epFpNodeId, migrateRp.getFpNodeId(), trans);
			}
			
/*			List<Integer> ceFpnodeIds = facilityDAO()
					.retrieveControlEquipIdsByProcessWiseId(parentWiseViewId);
			if (ceFpnodeIds != null && ceFpnodeIds.size() > 0) {
				for (Integer ceFpnodeId : ceFpnodeIds) {
					createRelationShip(ceFpnodeId, migrateRp.getFpNodeId(),
							trans);
				}

			} else {
				Integer epFpnodeId = facilityDAO().retrieveProcessIdByWiseId(
						parentWiseViewId);

				if (epFpnodeId == null) {
					wirteMigrateError("The error is during the Release Point conversion, "
							+ "The related Emission Process is not found, Emission Process WISE View ID: "
							+ parentWiseViewId);
					return;
				}

				createRelationShip(epFpnodeId, migrateRp.getFpNodeId(), trans);
			}*/

			wirteMigrateLog("End to create Release Point connection with Emission Process, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			trans.complete();
		} catch (Exception e) {
			wirteMigrateError(e.getMessage());
			if (trans != null)
				trans.cancel();

		} finally { // Clean up our transaction stuff
			// closeTransaction(trans);
			this.currentMigrationCount++;
//			refreshMigrationDisplayInfo();
		}

	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void createWiseViewReleasePointsBasedOnPreviousVersion(String[] rawData,
			HashMap<String, Integer> releatedFacilityIds, Integer oldFpId) {

		String facilityId;
		Integer fpId = null;
		String wiseViewId;
		EgressPoint previousMigrateRp = null;

		Transaction trans = TransactionFactory.createTransaction();

		try {
			if (rawData.length < 15) {
				wirteMigrateError("The Release Point csv format is incorrect, csv raw data: "
						+ Utility.toString(rawData, ", "));
				return;
			}

			String parentWiseViewId = rawData[2];

			if (Utility.isNullOrEmpty(parentWiseViewId)) {
				wirteMigrateError("The Release Point Parent WISE View ID cannot be null or empty");
				return;
			}

			EgressPoint migrateRp = convertRawDataToReleasePoint(rawData);

			if (migrateRp == null) {
				wirteMigrateError(
						"The error is during the Release Point conversion, "
								+ "The Release Point csv file is incorrect, csv raw data:  ",
						Utility.toString(rawData, ", "));
				return;
			}

			wiseViewId = migrateRp.getAqdWiseEgressPointId();
			facilityId = migrateRp.getFacilityId();

			wirteMigrateLog("Start to create Release Point, WISEView ID is "
					+ wiseViewId);

			if (releatedFacilityIds.containsKey(facilityId)) {
				fpId = releatedFacilityIds.get(facilityId);
			} else {
				wirteMigrateError(
						"The error is during the Release Point conversion, "
								+ "The Release Point csv file is incorrect "
								+ "The Facility ID is incorrect "
								+ "Facility ID: " + facilityId, wiseViewId);
				return;
			}

			migrateRp.setFpId(fpId);

			if (previousMigrateRp == null
					|| !(previousMigrateRp.getFpId().equals(fpId)
							&& previousMigrateRp.getAqdWiseEgressPointId()
									.equals(wiseViewId) && previousMigrateRp
							.getEgressPointTypeCd().equals(
									migrateRp.getEgressPointTypeCd()))) {
				migrateRp = createEgressPointBasedOnPreviousVersion(migrateRp, oldFpId, trans);
				previousMigrateRp = migrateRp;
			} else {
				migrateRp.setFpNodeId(previousMigrateRp.getFpNodeId());
			}

			wirteMigrateLog("End to create Release Point, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			List<Integer> ceFpnodeIds = facilityDAO()
					.retrieveControlEquipIdsByProcessWiseId(parentWiseViewId);

			if (ceFpnodeIds != null && ceFpnodeIds.size() > 0) {
				for (Integer ceFpnodeId : ceFpnodeIds) {
					createRelationShip(ceFpnodeId, migrateRp.getFpNodeId(),
							trans);
				}

			} else {
				Integer epFpnodeId = facilityDAO().retrieveProcessIdByWiseId(
						parentWiseViewId);

				if (epFpnodeId == null) {
					wirteMigrateError("The error is during the Release Point conversion, "
							+ "The related Emission Process is not found, Emission Process WISE View ID: "
							+ parentWiseViewId);
					return;
				}

				createRelationShip(epFpnodeId, migrateRp.getFpNodeId(), trans);
			}

			wirteMigrateLog("End to create Release Point connection with Emission Process, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			trans.complete();
		} catch (Exception e) {
			wirteMigrateError(e.getMessage());
			if (trans != null)
				trans.cancel();

		} finally { // Clean up our transaction stuff
			// closeTransaction(trans);
			this.currentMigrationCount++;
//			refreshMigrationDisplayInfo();
		}

	}

	private EmissionUnit convertRawDataToEmissionUnit(String[] rawData) {
		EmissionUnit eu = new EmissionUnit();
		EmissionUnitType euType = eu.getEmissionUnitType();

		for (int i = 0; i < rawData.length; i++) {
			rawData[i] = StringUtils.trimToNull(rawData[i]);
		}

		String facilityId = rawData[0];
		String wiseViewId = rawData[1];
		String euDesc = rawData[3];
		String companyId = rawData[4];
		String companyDesc = rawData[5];
		String operatingStatusCd = rawData[6];
		String eutypeCd = rawData[7];
		String euInitInstallDateText = rawData[8];
		String euInitStartupDateText = rawData[9];
		
		String namePlateRatingText = rawData[16];
		String namePlateRatingUnitCd = rawData[17];
		String primaryFuelType = rawData[18];
		String secondaryFuelType = rawData[19];
		String serialNumber = rawData[20];
		String serialNumberEffDateText = rawData[21];
		String manufacturerName = rawData[22];
		String modelNameNumber = rawData[23];
		String engineType = rawData[24];
		String heatInputRatingText = rawData[25];
		String heatInputRatingUnitCd = rawData[26];
		String plantType = rawData[27];
		String maxProductionRateText = rawData[28];
		String maxProductionRateUnitCd = rawData[29];
		String maxBurnerDesignRateText = rawData[30];
		String powerSource = rawData[31];
		String fuelType = rawData[32];
		String batchingType = rawData[33];
		String materialType = rawData[34];
		String materialDescription = rawData[35];
		String maxAnnualThroughputText = rawData[36];
		String maxAnnualThroughputUnitCd = rawData[37];
		String materialTypeStored = rawData[38];
		String liquidMaterialTypeStored = rawData[39];
		String materialTypeStoredDesc = rawData[40];
		String capacityText = rawData[41];
		String capacityUnit = rawData[42];
		String maxThroughputText = rawData[43];
		String maxThroughputUnitCd = rawData[44];
		String unitType = rawData[45];
		String maxDesignCapacityText = rawData[46];
		String maxDesignCapacityUnitCd = rawData[47];
		String minDesignCapacityText = rawData[48];
		String minDesignCapacityUnitCd = rawData[49];
		String pilotGasVolumeText = rawData[50];
		String incineratorType = rawData[51];
		String burnerSystem = rawData[52];
		String dehydrationType = rawData[53];
		String designCapacityText = rawData[54];
		String firingType = rawData[55];
		String equipmentType = rawData[56];
		String bleedRateText = rawData[57];
		String gasConsumptionRateText = rawData[58];
		String eventType = rawData[59];
		String producedMaterialType = rawData[60];
		String unitDesc = rawData[61];
		String vesselType = rawData[62];
		String vesselHeatedText = rawData[63];
		String siteRatingText = rawData[64];
		String siteRatingUnitCd = rawData[65];
		String eqptCountText = rawData[66];
		String fuelHeatContentText = rawData[67];
		String fuelHeatContentUnitCd = rawData[68];
		String typeOfUseCd = rawData[69];
	
		String fugitiveLeaksText = rawData[80];
		
		BigDecimal namePlateRating = null;
		BigDecimal heatInputRating = null;
		Integer maxProductionRate = null;
		BigDecimal maxBurnerDesignRate = null;
		Long maxAnnualThroughput = null;
		Integer capacity = null;
		BigDecimal maxThroughput = null;
		BigDecimal maxDesignCapacity = null;
		BigDecimal minDesignCapacity = null;
		BigDecimal designCapacity = null;
		BigDecimal bleedRate = null;
		BigDecimal gasConsumptionRate = null;
		BigDecimal siteRating = null;
		BigDecimal pilotGasVolume = null;
		Short eqptCount = null;
		Integer fuelHeatContent = null;
		boolean vesselHeated = false;
		Timestamp euInitInstallDate = null;
		Timestamp euInitStartupDate = null;
		Timestamp serialNumberEffDate = null;
		boolean fugitiveLeaks = false;
		
		if (!Utility.isNullOrEmpty(fugitiveLeaksText)) {
			try {
				if (StringUtils.indexOf(StringUtils.upperCase(fugitiveLeaksText), "Y") == 0)
					fugitiveLeaks = true;
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: fugitiveLeaks", wiseViewId);
			}
		}

		if (fugitiveLeaks) {
			// look for component table columns
			if (rawData.length < 105) {
				wirteMigrateError("The Emission Unit row doesnt have columns for Fugutive component table", wiseViewId);
				return null;
			}
			List<Component> euComponents = parseWiseViewFugutiveComponentColumns(rawData, wiseViewId);
			euType.setComponents(euComponents);
		}

		if (!Utility.isNullOrEmpty(namePlateRatingText)) {
			try {
				namePlateRating = new BigDecimal(namePlateRatingText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: namePlateRating", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(heatInputRatingText)) {
			try {
				heatInputRating = new BigDecimal(heatInputRatingText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: heatInputRating", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(siteRatingText)) {
			try {
				siteRating = new BigDecimal(siteRatingText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: siteRating", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(maxProductionRateText)) {
			try {
				maxProductionRate = Math.round(Float.valueOf(maxProductionRateText));
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: maxProductionRate", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(eqptCountText)) {
			try {
				eqptCount = Short.valueOf(eqptCountText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: eqptCount", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(fuelHeatContentText)) {
			try {
				fuelHeatContent = Math.round(Float.valueOf(fuelHeatContentText));
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: fuelHeatContent", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(maxBurnerDesignRateText)) {
			try {
				maxBurnerDesignRate = new BigDecimal(maxBurnerDesignRateText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: maxBurnerDesignRate", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(maxAnnualThroughputText)) {
			try {
				maxAnnualThroughput = Long.valueOf(maxAnnualThroughputText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: maxAnnualThroughput", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(capacityText)) {
			try {
				capacity = Math.round(Float.valueOf(capacityText));
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: capacity", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(maxThroughputText)) {
			try {
				maxThroughput = new BigDecimal(maxThroughputText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: maxThroughput", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(maxDesignCapacityText)) {
			try {
				maxDesignCapacity = new BigDecimal(maxDesignCapacityText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: maxDesignCapacity", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(minDesignCapacityText)) {
			try {
				minDesignCapacity = new BigDecimal(minDesignCapacityText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: minDesignCapacity", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(pilotGasVolumeText)) {
			try {
				pilotGasVolume = new BigDecimal(pilotGasVolumeText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: pilotGasVolume", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(designCapacityText)) {
			try {
				designCapacity = new BigDecimal(designCapacityText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: designCapacity", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(bleedRateText)) {
			try {
				bleedRate = new BigDecimal(bleedRateText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: bleedRate", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(gasConsumptionRateText)) {
			try {
				gasConsumptionRate = new BigDecimal(gasConsumptionRateText);
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: gasConsumptionRate", wiseViewId);
			}
		}

		if (Utility.isNullOrEmpty(facilityId)) {
			wirteMigrateError("The Emission Unit Facility ID cannot be null or empty", wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(wiseViewId)) {
			wirteMigrateError("The Emission Unit WISE View ID cannot be null or empty", wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(operatingStatusCd)) {
			wirteMigrateError("The Emission Unit Operating Status cannot be null or empty", wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(eutypeCd)) {
			wirteMigrateError("The Emission Unit Type cannot be null or empty", wiseViewId);
			return null;
		} else {
			DefSelectItems items = EmissionUnitTypeDef.getData().getItems();
			if (items.getItemDesc(eutypeCd) == null) {
				wirteMigrateError("The Emission Unit Type is invalid", wiseViewId);
				return null;
			}
		}

		if (!Utility.isNullOrEmpty(vesselHeatedText)) {
			try {
				if (StringUtils.indexOf(StringUtils.upperCase(vesselHeatedText), "Y") == 0)
					vesselHeated = true;
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: vesselHeated", wiseViewId);
			}
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		if (!Utility.isNullOrEmpty(euInitInstallDateText)) {
			try {
				Date parsedDate = dateFormat.parse(euInitInstallDateText);
				euInitInstallDate = new java.sql.Timestamp(parsedDate.getTime());
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: euInitInstallDate", wiseViewId);
			}
		}

		if (!Utility.isNullOrEmpty(euInitStartupDateText)) {
			try {
				Date parsedDate = dateFormat.parse(euInitStartupDateText);
				euInitStartupDate = new java.sql.Timestamp(parsedDate.getTime());
			} catch (Exception e) {
				wirteMigrateError("The Emission Unit convert error, field Name: euInitStartupDate", wiseViewId);
			}
		}

		eu.setFacilityId(facilityId);
		eu.setWiseViewId(wiseViewId);
		eu.setCompanyId(companyId);
		eu.setEuDesc(euDesc);
		eu.setCompanyId(companyId);
		eu.setRegulatedUserDsc(companyDesc);
		eu.setOperatingStatusCd(operatingStatusCd);
		eu.setEmissionUnitTypeCd(eutypeCd);
		eu.setEmissionUnitType(euType);
		eu.setEuInitInstallDate(euInitInstallDate);
		eu.setEuInitStartupDate(euInitStartupDate);

		if (!StringUtils.isEmpty(serialNumber)) {
			EmissionUnitReplacement euRep = new EmissionUnitReplacement();
			euRep.setSerialNumber(serialNumber);
			euRep.setSerialNumberEffectiveDate(serialNumberEffDate);
			eu.addEmissionUnitReplacement(euRep);
		}
		
		euType.setEmissionUnitTypeCd(eutypeCd);
		euType.setNamePlateRating(namePlateRating);
		if (!StringUtils.isEmpty(namePlateRatingUnitCd)) {
			euType.setUnitCd(namePlateRatingUnitCd);
		}
		euType.setPrimaryFuelType(primaryFuelType);
		euType.setSecondaryFuelType(secondaryFuelType);
		euType.setManufacturerName(manufacturerName);
		euType.setModelNameNumber(modelNameNumber);
		euType.setEngineType(engineType);
		euType.setHeatInputRating(heatInputRating);
		if (!StringUtils.isEmpty(heatInputRatingUnitCd)) {
			euType.setUnitCd(heatInputRatingUnitCd);
		}
		euType.setPlantType(plantType);
		euType.setMaxProductionRate(maxProductionRate);
		if (!StringUtils.isEmpty(maxProductionRateUnitCd)) {
			euType.setUnitCd(maxProductionRateUnitCd);
		}
		euType.setMaxBurnerDesignRate(maxBurnerDesignRate);
		euType.setPowerSource(powerSource);
		euType.setFuelType(fuelType);
		euType.setBatchingType(batchingType);
		euType.setMaterialType(materialType);
		euType.setMaterialDescription(materialDescription);
		euType.setMaxAnnualThroughput(maxAnnualThroughput);
		if (!StringUtils.isEmpty(maxAnnualThroughputUnitCd)) {
			euType.setUnitCd(maxAnnualThroughputUnitCd);
		}
		euType.setMaterialTypeStored(materialTypeStored);
		
		if (StringUtils.equalsIgnoreCase(materialTypeStored, TnkMaterialStoredTypeDef.SOLID)) {
			euType.setMaterialTypeStoredDesc(materialTypeStoredDesc);
		} else if (StringUtils.equalsIgnoreCase(materialTypeStored, TnkMaterialStoredTypeDef.LIQUID)) {
			euType.setLiquidMaterialTypeStored(liquidMaterialTypeStored);
			if (StringUtils.equalsIgnoreCase(TnkMaterialStoredTypeLiquidDef.OTHER, liquidMaterialTypeStored))
				euType.setMaterialTypeStoredDesc(materialTypeStoredDesc);
		}

		euType.setCapacity(capacity);
		if (!StringUtils.isEmpty(capacityUnit)) {
			euType.setCapacityUnit(capacityUnit);
		}
		euType.setMaxThroughput(maxThroughput);
		if (!StringUtils.isEmpty(maxThroughputUnitCd)) {
			euType.setUnitCd(maxThroughputUnitCd);
		}
		euType.setUnitType(unitType);
		euType.setMaxDesignCapacity(maxDesignCapacity);
		if (!StringUtils.isEmpty(maxDesignCapacityUnitCd)) {
			euType.setUnitCd(maxDesignCapacityUnitCd);
		}
		euType.setMinDesignCapacity(minDesignCapacity);
		if (!StringUtils.isEmpty(minDesignCapacityUnitCd)) {
			euType.setCapacityUnit(minDesignCapacityUnitCd);
		}
		euType.setPilotGasVolume(pilotGasVolume);
		euType.setIncineratorType(incineratorType);
		euType.setBurnerSystem(burnerSystem);
		euType.setDehydrationType(dehydrationType);
		euType.setDesignCapacity(designCapacity);
		euType.setFiringType(firingType);
		euType.setEquipmentType(equipmentType);
		if (StringUtils.equalsIgnoreCase(equipmentType, EquipTypeDef.PUMP)){
			euType.setGasConsumptionRate(gasConsumptionRate);
		} else if (StringUtils.equalsIgnoreCase(equipmentType, EquipTypeDef.INTERMITTENT)) {
			euType.setBleedRate(bleedRate);
		}
		euType.setEventType(eventType);
		euType.setProducedMaterialType(producedMaterialType);
		euType.setUnitDesc(unitDesc);
		euType.setVesselType(vesselType);
		euType.setVesselHeated(vesselHeated);
		euType.setSiteRating(siteRating);
		euType.setSiteRatingUnitCd(siteRatingUnitCd);
		euType.setEqptCount(eqptCount);
		euType.setFuelHeatContent(fuelHeatContent);
		euType.setFuelHeatContentUnitsCd(fuelHeatContentUnitCd);
		euType.setTypeOfUse(typeOfUseCd);
		euType.setFugitiveLeaks(fugitiveLeaks);
		
		return eu;

	}
	
	private List<Component> parseWiseViewFugutiveComponentColumns(String[] rawData, String wiseViewId) {
		List<Component> euComponents  = new ArrayList<Component>();
		
		try {
			Arrays.copyOfRange(rawData, 81, 84);
			Component cmp = parseWiseViewFugutiveComponent(FugitiveComponentDef.CNT,
					Arrays.copyOfRange(rawData, 81, 85));
			euComponents.add(cmp);
		} catch (NumberFormatException e) {
			wirteMigrateError("The Emission Unit convert error, field Name: Total Count of Component - Connectors",
					wiseViewId);
		}

		try {
			Component cmp = parseWiseViewFugutiveComponent(FugitiveComponentDef.FLG,
					Arrays.copyOfRange(rawData, 85, 89));
			euComponents.add(cmp);
		} catch (NumberFormatException e) {
			wirteMigrateError("The Emission Unit convert error, field Name: Total Count of Component - Flanges",
					wiseViewId);
		}

		try {
			Component cmp = parseWiseViewFugutiveComponent(FugitiveComponentDef.OEL,
					Arrays.copyOfRange(rawData, 89, 93));
			euComponents.add(cmp);
		} catch (NumberFormatException e) {
			wirteMigrateError(
					"The Emission Unit convert error, field Name: Total Count of Component - Open Ended Lines",
					wiseViewId);
		}

		try {
			Component cmp = parseWiseViewFugutiveComponent(FugitiveComponentDef.PPS,
					Arrays.copyOfRange(rawData, 93, 97));
			euComponents.add(cmp);
		} catch (NumberFormatException e) {
			wirteMigrateError("The Emission Unit convert error, field Name: Total Count of Component - Pump Seals",
					wiseViewId);
		}

		try {
			Component cmp = parseWiseViewFugutiveComponent(FugitiveComponentDef.VLV,
					Arrays.copyOfRange(rawData, 97, 101));
			euComponents.add(cmp);
		} catch (NumberFormatException e) {
			wirteMigrateError("The Emission Unit convert error, field Name: Total Count of Component - Valves",
					wiseViewId);
		}

		try {
			Component cmp = parseWiseViewFugutiveComponent(FugitiveComponentDef.OTH,
					Arrays.copyOfRange(rawData, 101, 105));
			euComponents.add(cmp);
		} catch (NumberFormatException e) {
			wirteMigrateError("The Emission Unit convert error, field Name: Total Count of Component - Others",
					wiseViewId);
		}
		
		return euComponents;
	}
	
	private Component parseWiseViewFugutiveComponent(String componentCd, String[] rowData)
			throws NumberFormatException {
		Component comp = new Component();

		Integer gas = Integer.parseInt(rowData[0]);
		Integer heavyOil = Integer.parseInt(rowData[1]);
		Integer lightOil = Integer.parseInt(rowData[2]);
		Integer water = Integer.parseInt(rowData[3]);

		comp.setComponentCd(componentCd);
		comp.setGas(gas);
		comp.setHeavyOil(heavyOil);
		comp.setLightOil(lightOil);
		comp.setWater(water);

		return comp;
	}

	private EmissionProcess convertRawDataToEmissionProcess(String[] rawData, HashMap<String, Integer> emuMap)
			throws DAOException {
		EmissionProcess ep = new EmissionProcess();

		for (int i = 0; i < rawData.length; i++) {
			rawData[i] = StringUtils.trimToNull(rawData[i]);
		}
		
		String facilityId = rawData[0];
		String wiseViewId = rawData[1];
		String parentWiseViewId = rawData[2];
		String scc = rawData[3];
		String epDesc = rawData[4];

		if (Utility.isNullOrEmpty(facilityId)) {
			wirteMigrateError(
					"The Emission Process Facility ID cannot be null or empty",
					wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(wiseViewId)) {
			wirteMigrateError(
					"The Emission Process WISE View ID cannot be null or empty",
					wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(parentWiseViewId)) {
			wirteMigrateError(
					"The Emission Process Parent WISE View ID cannot be null or empty",
					wiseViewId);
			return null;
		}
/*
		Integer emuId = facilityDAO().retrieveEmissionUnitIdByWiseId(
				parentWiseViewId);*/
		Integer emuId = emuMap.get(parentWiseViewId);

		if (emuId == null) {
			wirteMigrateError(
					"The Emission Process Parent WISE View ID incorrect or the related Emission Unit cannot be found",
					wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(scc)) {
			wirteMigrateError(
					"The Emission Process SCC Code cannot be null or empty",
					wiseViewId);
			return null;
		}

		SccCode tmpScc = SccCodesDef.getSccCode(scc);

		if (tmpScc == null) {
			wirteMigrateError("The Emission Process SCC Code, " + scc
					+ " is incorrect", wiseViewId);
			return null;
		}

		SccCode objScc = new SccCode();
		objScc.copySccCode(tmpScc);
		ep.setSccCode(objScc);

		ep.setFacilityId(facilityId);
		ep.setWiseViewId(wiseViewId);
		ep.setEmissionProcessNm(epDesc);
		ep.setEmissionUnitId(emuId);

		return ep;
	}

	private EgressPoint convertRawDataToReleasePoint(String[] rawData) {
		EgressPoint rp = new EgressPoint();

		for (int i = 0; i < rawData.length; i++) {
			rawData[i] = StringUtils.trimToNull(rawData[i]);
		}
		
		String facilityId = rawData[0];
		String wiseViewId = rawData[1];
		String operatingStatusCd = rawData[3];
		String rpType = rawData[4];
		String companyId = rawData[5];
		String companyDesc = rawData[6];
		String rpHeightText = rawData[7];
		String diameterText = rawData[8];
		String exitGasTempAvgText = rawData[9];
		String exitGasVelocityText = rawData[10];
		String exitGasFlowAvgText = rawData[11];
		String longitudeText = rawData[12];
		String latitudeText = rawData[13];
		String rpDesc = rawData[14];
		String baseElevationText = rawData[15];

		Float rpHeight = null;
		BigDecimal diameter = null;
		BigDecimal exitGasTempAvg = null;
		BigDecimal exitGasVelocity = null;
		BigDecimal exitGasFlowAvg = null;
		Float latitude = 0f;
		Float longitude = 0f;
		Float baseElevation = null;

		if (Utility.isNullOrEmpty(facilityId)) {
			wirteMigrateError(
					"The Release Point Facility ID cannot be null or empty",
					wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(wiseViewId)) {
			wirteMigrateError(
					"The Release Point WISE View ID cannot be null or empty",
					wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(operatingStatusCd)) {
			wirteMigrateError(
					"The Release Point Operation Status cannot be null or empty",
					wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(rpType)) {
			wirteMigrateError("The Release Point Type cannot be null or empty",
					wiseViewId);
			return null;
		}

		if (!Utility.isNullOrEmpty(rpHeightText)) {
			try {
				rpHeight = Float.valueOf(rpHeightText);
			} catch (Exception e) {
			}
		}

		if (!Utility.isNullOrEmpty(diameterText)) {
			try {
				diameter = new BigDecimal(diameterText);
			} catch (Exception e) {
			}
		}

		if (!Utility.isNullOrEmpty(exitGasTempAvgText)) {
			try {
				exitGasTempAvg = new BigDecimal(exitGasTempAvgText);
			} catch (Exception e) {
			}
		}

		if (!Utility.isNullOrEmpty(exitGasVelocityText)) {
			try {
				exitGasVelocity = new BigDecimal(exitGasVelocityText);
			} catch (Exception e) {
			}
		}

		if (!Utility.isNullOrEmpty(exitGasFlowAvgText)) {
			try {
				exitGasFlowAvg = new BigDecimal(exitGasFlowAvgText);
			} catch (Exception e) {
			}
		}

		boolean oneOfLatLongIsEmpty = Utility.isNullOrEmpty(latitudeText)
				|| Utility.isNullOrEmpty(longitudeText)
				|| latitudeText.equals("0") || latitudeText.equals("0");

		if (!oneOfLatLongIsEmpty) {
			try {
				latitude = Float.valueOf(latitudeText);
				longitude = Float.valueOf(longitudeText);
			} catch (Exception e) {
			}
		}

		boolean latLongIsOutOffRange = latitude == null || latitude == null
				|| latitude == 0f || latitude == 0f || latitude > 45
				|| latitude < 41 || longitude > -104.05 || longitude < -111.06;

		if (latLongIsOutOffRange) {
			try {
				Facility facility = facilityDAO().retrieveFacility(facilityId);
				if (facility == null) {
					wirteMigrateError(
							"The error is during the Release Point conversion, "
									+ "The Release Point csv file is incorrect "
									+ "The Facility ID is incorrect "
									+ "Facility ID: " + facilityId, wiseViewId);

					return null;
				}
				Address address = facility.getPhyAddr();

				latitudeText = address.getLatitude();
				longitudeText = address.getLongitude();

				if (!Utility.isNullOrEmpty(latitudeText)
						&& !Utility.isNullOrEmpty(longitudeText)) {
					latitude = Float.valueOf(latitudeText);
					longitude = Float.valueOf(longitudeText);
				}
			} catch (Exception e) {
				wirteMigrateError(e.getMessage());
				return null;
			}
		}

		if (!Utility.isNullOrEmpty(baseElevationText)) {
			try {
				baseElevation = Float.valueOf(baseElevationText);
			} catch (Exception e) {
			}
		}

		rp.setFacilityId(facilityId);
		rp.setAqdWiseEgressPointId(wiseViewId);
		rp.setEgressPointTypeCd(rpType);
		rp.setEgressPointId(companyId);
		rp.setRegulatedUserDsc(companyDesc);
		rp.setExitGasTempAvg(exitGasTempAvg);
		rp.setDiameter(diameter);
		rp.setReleaseHeight(rpHeight);
		rp.setExitGasVelocity(exitGasVelocity);
		rp.setExitGasFlowAvg(exitGasFlowAvg);
		rp.setDapcDesc(rpDesc);
		rp.setLatitudeDeg(latitude);
		rp.setLongitudeDeg(longitude);
		rp.setOperatingStatusCd(operatingStatusCd);
		rp.setBaseElevation(baseElevation);

		return rp;
	}

	private ControlEquipment convertRawDataToControlEquipment(String[] rawData) {
		ControlEquipment ce = new ControlEquipment();

		for (int i = 0; i < rawData.length; i++) {
			rawData[i] = StringUtils.trimToNull(rawData[i]);
		}
		
		String facilityId = rawData[0];
		String wiseViewId = rawData[1];
		String ceType = rawData[3];
		String companyId = rawData[4];
		String companyDesc = rawData[5];
		String ceDesc = rawData[6];
		String operatingStatusCd = rawData[7];
		String pollutantCd = rawData[136];
		String designContEff = rawData[137];
		String operContEff = rawData[138];
		String captureEff = rawData[139];

		if (Utility.isNullOrEmpty(facilityId)) {
			wirteMigrateError(
					"The Control Equipment Facility ID cannot be null or empty",
					wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(operatingStatusCd)) {
			wirteMigrateError(
					"The Control Equipment  Operation Status cannot be null or empty",
					wiseViewId);
			return null;
		}

		if (Utility.isNullOrEmpty(ceType)) {
			wirteMigrateError("The Control Equipment Type cannot be null or empty", wiseViewId);
			return null;
		} else {
			DefSelectItems items = ContEquipTypeDef.getData().getItems();
			if (items.getItemDesc(ceType) == null) {
				wirteMigrateError("The Control Equipment Type is invalid", wiseViewId);
				return null;
			}
		}
		
		ce.setFacilityId(facilityId);
		ce.setWiseViewId(wiseViewId);
		ce.setCompanyId(companyId);
		ce.setRegUserDesc(companyDesc);
		ce.setEquipmentTypeCd(ceType);
		ce.setDapcDesc(ceDesc);
		ce.setOperatingStatusCd(operatingStatusCd);

		PollutantsControlled polluant = new PollutantsControlled();
		polluant.setPollutantCd(pollutantCd);
		polluant.setDesignContEff(designContEff);
		polluant.setOperContEff(operContEff);
		polluant.setCaptureEff(captureEff);

		List<PollutantsControlled> pollutants = new ArrayList<PollutantsControlled>();
		pollutants.add(polluant);

		ce.setPollutantsControlled(pollutants);

		try {
			List<DataDetail> dataDetails = parseWiseViewCEDataDetail(rawData, wiseViewId);
			ce.setCeDataDetails(dataDetails);
		} catch (Exception e) {
			return null;
		}

		return ce;
	}

	private List<DataDetail> parseWiseViewCEDataDetail(String[] rawData, String wiseViewId) {
		List<DataDetail> dataDetails = new ArrayList<DataDetail>();
		String ceType = rawData[3];

		if (StringUtils.equalsIgnoreCase(ceType, ContEquipTypeDef.CNC)) {
			String catalyticReductionType = rawData[30];
			String reagentType = rawData[31];
			String reagentInjectionRatespecifyunits = rawData[32];
			String reagentSlipConcentration = rawData[33];
			String reagentSlipConcentrationO2 = rawData[34];
			
			String inletGasFlowRate = rawData[37];
			String inletGasTemp = rawData[35];
			String outletGasTemp = rawData[36];
			
			String airFuelRatioController = rawData[38];
			
			convertStringToFloat(reagentSlipConcentration, "reagentSlipConcentration", wiseViewId);
			convertStringToFloat(reagentSlipConcentrationO2, "reagentSlipConcentrationO2", wiseViewId);
			convertStringToFloat(inletGasFlowRate, "inletGasFlowRate", wiseViewId);
			convertStringToInteger(inletGasTemp, "inletGasTemp", wiseViewId);
			convertStringToInteger(outletGasTemp, "outletGasTemp", wiseViewId);

			wiseviewCEAddToDataDetail(dataDetails, 330, catalyticReductionType);
			wiseviewCEAddToDataDetail(dataDetails, 331, reagentType);
			wiseviewCEAddToDataDetail(dataDetails, 332, reagentInjectionRatespecifyunits);
			wiseviewCEAddToDataDetail(dataDetails, 333, reagentSlipConcentration);
			wiseviewCEAddToDataDetail(dataDetails, 334, reagentSlipConcentrationO2);
			wiseviewCEAddToDataDetail(dataDetails, 390, inletGasFlowRate);
			wiseviewCEAddToDataDetail(dataDetails, 392, inletGasTemp);
			wiseviewCEAddToDataDetail(dataDetails, 393, outletGasTemp);
			wiseviewCEAddToDataDetail(dataDetails, 1514, airFuelRatioController);

		}
		
		if (StringUtils.equalsIgnoreCase(ceType, ContEquipTypeDef.CON)) {
			String condenserType = rawData[41];
			String coolantType = rawData[42];
			String designCoolantTempRange = rawData[43];
			String designCoolantFlowRate = rawData[44];
			String maxExhaustGasTemp = rawData[45];
			String operatingPressure = rawData[46];
			String inletGasTemp = rawData[47];
			String outletGasTemp = rawData[48];
			String inletGasFlowRate = rawData[49];
			String outletGasFlowRate = rawData[50];

			convertStringToInteger(designCoolantTempRange, "designCoolantTempRange", wiseViewId);
			convertStringToFloat(designCoolantFlowRate, "designCoolantFlowRate", wiseViewId);
			convertStringToInteger(maxExhaustGasTemp, "maxExhaustGasTemp", wiseViewId);
			convertStringToFloat(inletGasFlowRate, "inletGasFlowRate", wiseViewId);
			convertStringToFloat(outletGasFlowRate, "outletGasFlowRate", wiseViewId);
			convertStringToInteger(inletGasTemp, "inletGasTemp", wiseViewId);
			convertStringToInteger(outletGasTemp, "outletGasTemp", wiseViewId);
			convertStringToFloat(operatingPressure, "operatingPressure", wiseViewId);

			wiseviewCEAddToDataDetail(dataDetails, 279, condenserType);
			wiseviewCEAddToDataDetail(dataDetails, 281, coolantType);
			wiseviewCEAddToDataDetail(dataDetails, 282, designCoolantTempRange);
			wiseviewCEAddToDataDetail(dataDetails, 283, designCoolantFlowRate);
			wiseviewCEAddToDataDetail(dataDetails, 284, maxExhaustGasTemp);
			wiseviewCEAddToDataDetail(dataDetails, 390, inletGasFlowRate);
			wiseviewCEAddToDataDetail(dataDetails, 391, outletGasFlowRate);
			wiseviewCEAddToDataDetail(dataDetails, 392, inletGasTemp);
			wiseviewCEAddToDataDetail(dataDetails, 416, outletGasTemp);
			wiseviewCEAddToDataDetail(dataDetails, 401, operatingPressure);
		}
		
		if (StringUtils.equalsIgnoreCase(ceType, ContEquipTypeDef.FLA)) {
			String flareType = rawData[53];
			String elevatedFlareType = rawData[54];
			String ignitionDevice = rawData[55];
			String flamePresenceSensor = rawData[56];
			String flamePresenceType = rawData[57];
			String inletGasTemp = rawData[58];
			String gasFlowRate = rawData[59];
			String secOutletGasTemp = rawData[60];
			
			convertStringToInteger(inletGasTemp, "inletGasTemp", wiseViewId);
			convertStringToFloat(gasFlowRate, "gasFlowRate", wiseViewId);
			convertStringToInteger(secOutletGasTemp, "secOutletGasTemp", wiseViewId);
			
			wiseviewCEAddToDataDetail(dataDetails, 269, flareType);
			wiseviewCEAddToDataDetail(dataDetails, 270, elevatedFlareType);
			wiseviewCEAddToDataDetail(dataDetails, 272, ignitionDevice);
			wiseviewCEAddToDataDetail(dataDetails, 273, flamePresenceSensor);
			wiseviewCEAddToDataDetail(dataDetails, 392, inletGasTemp);
			wiseviewCEAddToDataDetail(dataDetails, 404, flamePresenceType);
			wiseviewCEAddToDataDetail(dataDetails, 417, gasFlowRate);
			wiseviewCEAddToDataDetail(dataDetails, 436, secOutletGasTemp);
		}
		
		if (StringUtils.equalsIgnoreCase(ceType, ContEquipTypeDef.FDS)) {
			String suppressantAgentType = rawData[63];
			String methodofApplication = rawData[64];
			String applicationRatespecifyunits = rawData[65];
			String applicationFrequencyspecifyunits = rawData[66];
			
			wiseviewCEAddToDataDetail(dataDetails, 319, suppressantAgentType);
			wiseviewCEAddToDataDetail(dataDetails, 321, methodofApplication);
			wiseviewCEAddToDataDetail(dataDetails, 322, applicationRatespecifyunits);
			wiseviewCEAddToDataDetail(dataDetails, 323, applicationFrequencyspecifyunits);
		}
		
		if (StringUtils.equalsIgnoreCase(ceType, ContEquipTypeDef.OXI)) {
			String catalystType = rawData[69];
			String airFuelRatioController = rawData[70];
			
			wiseviewCEAddToDataDetail(dataDetails, 407, catalystType);
			wiseviewCEAddToDataDetail(dataDetails, 408, airFuelRatioController);
		}
		
		return dataDetails;
	}

	private void wiseviewCEAddToDataDetail(List<DataDetail> dataDetails, Integer id, String value) {
		if (!StringUtils.isEmpty(value)) {
			DataDetail dd = new DataDetail();
			dd.setDataDetailId(id);
			dd.setDataDetailVal(value);
			dataDetails.add(dd);
		}
	}

	private void convertStringToFloat(String valTxt, String labelNm, String wiseViewId) {
		if (!Utility.isNullOrEmpty(valTxt)) {
			try {
				Float.valueOf(valTxt);
			} catch (NumberFormatException e) {
				wirteMigrateError("The Control Equipment had a conversion error, field Name: " + labelNm, wiseViewId);
				throw e;
			}
		}
	}
	
	private void convertStringToInteger(String valTxt, String labelNm, String wiseViewId) {
		if (!Utility.isNullOrEmpty(valTxt)) {
			try {
				Integer.valueOf(valTxt);
			} catch (NumberFormatException e) {
				wirteMigrateError("The Control Equipment had a conversion error, field Name: " + labelNm, wiseViewId);
				throw e;
			}
		}
	}
	
	private Facility setFacilityCopyOnChange(String facilityId,
			boolean copyOnChange) throws DAOException {

		FacilityDAO facilityDAO = facilityDAO();
		Facility facility = facilityDAO.retrieveFacility(facilityId);
		facility.setCopyOnChange(copyOnChange);
		facilityDAO.modifyFacility(facility);

		return facility;
	}

	private final void wirteMigrateError(String message) {
		if (printWriter == null)
			return;

		String msg = "Migration Error:" + message;

		printWriter.println(msg);
//		DisplayUtil.displayError(msg);

		SimpleDateFormat ft = new SimpleDateFormat(
				"yyyy.MM.dd hh:mm:ss.SSS zzz");

		logger.error("****** Time: " + ft.format(new Date()) + ", " + msg);
	}

	private final void wirteMigrateError(String message, String wiseViewId) {
		wirteMigrateError(message + ", WISE View ID: " + wiseViewId);
	}

	private final void wirteMigrateLog(String message) {
		if (printWriter == null)
			return;

		SimpleDateFormat ft = new SimpleDateFormat(
				"yyyy.MM.dd hh:mm:ss.SSS zzz");

		String msg = "****** Time: " + ft.format(new Date()) + ", " + message;

		printWriter.println(msg);
//		DisplayUtil.displayInfo(msg);
		// logger.debug(msg);
	}

	private void deleteFaciltySublayerNodes(Integer fpId, Transaction trans)
			throws DAOException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		facilityDAO.removeFacilitySubObject(fpId);
	}

	@SuppressWarnings("resource")
	private int getCsvDataTotalCount(String filePath)
			throws FileNotFoundException {
		int count = 0;

		BufferedReader br = new BufferedReader(new FileReader(filePath));

		try {

			String line = br.readLine();

			while (line != null) {
				count++;
				line = br.readLine();
			}
		} catch (IOException e) {
			return 0;
		}

		return count;
	}

	private final void refreshMigrationDisplayInfo1() {
		String message = "Migration Operation is Processing "
				+ this.currentMigrationCount + " of "
				+ this.totalMigrationCount;

		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");

		String msg = "***** Time: " + ft.format(new Date()) + ", " + message;

//		DisplayUtil.clearQueuedMessages();
//		DisplayUtil.displayInfo(msg);

		// logger.debug(msg);

		if (printWriter == null)
			return;

		printWriter.println(msg);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	private boolean createMigrateControlEquip(
			HashMap<String, Integer> releatedFacilityIds,
			Integer epFpnodeId, ControlEquipment migrateCe)
			throws DAOException {
		String wiseViewId = migrateCe.getWiseViewId();
		Transaction trans = TransactionFactory.createTransaction();

		try {
			String facilityId = migrateCe.getFacilityId();
			Integer fpId = null;

			if (releatedFacilityIds.containsKey(facilityId)) {
				fpId = releatedFacilityIds.get(facilityId);

			} else {
				wirteMigrateError("The error is during the Control Equipment conversion, "
						+ "The Control Equipment csv file is incorrect "
						+ "The Facility ID is incorrect "
						+ "Facility ID: "
						+ facilityId);
				return false;
			}

			migrateCe.setFpId(fpId);
			migrateCe = createControlEquipment(migrateCe, trans);

			wirteMigrateLog("End to create Control Equipment, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			createRelationShip(epFpnodeId, migrateCe.getFpNodeId(), trans);

			wirteMigrateLog("End to create Control Equipment connection with Emission Process, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			trans.complete();
		} catch (DAOException e) {
			wirteMigrateError(e.getMessage());
			if (trans != null)
				trans.cancel();

		} finally {
			closeTransaction(trans);
		}

		return true;
	}
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	private boolean createMigrateControlEquipBasedOnPreviousVersion(
			HashMap<String, Integer> releatedFacilityIds,
			String parentWiseViewId, ControlEquipment migrateCe, Integer oldFpId)
			throws DAOException {
		String wiseViewId = migrateCe.getWiseViewId();
		Transaction trans = TransactionFactory.createTransaction();

		try {
			String facilityId = migrateCe.getFacilityId();
			Integer fpId = null;

			if (releatedFacilityIds.containsKey(facilityId)) {
				fpId = releatedFacilityIds.get(facilityId);

			} else {
				wirteMigrateError("The error is during the Control Equipment conversion, "
						+ "The Control Equipment csv file is incorrect "
						+ "The Facility ID is incorrect "
						+ "Facility ID: "
						+ facilityId);
				return false;
			}

			migrateCe.setFpId(fpId);
			migrateCe = createControlEquipmentBasedOnPreviousVersion(migrateCe, oldFpId, trans);

			wirteMigrateLog("End to create Control Equipment, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			Integer epFpnodeId = null;

			try {
				if (Utility.isNullOrEmpty(parentWiseViewId)) {
					wirteMigrateError(
							"The Control Equipment Parent WISE View ID cannot be null or empty",
							wiseViewId);
					return false;
				}

				epFpnodeId = facilityDAO().retrieveProcessIdByWiseId(
						parentWiseViewId);
			} catch (DAOException e) {
				wirteMigrateError(e.getMessage());
			}

			if (epFpnodeId == null) {
				wirteMigrateError(
						"The error is during the Control Equipment conversion, "
								+ "The related Emission Process is not found, Emission Process WISE View ID: "
								+ parentWiseViewId, wiseViewId);
				return false;
			}

			createRelationShip(epFpnodeId, migrateCe.getFpNodeId(), trans);

			wirteMigrateLog("End to create Control Equipment connection with Emission Process, Facility ID is "
					+ facilityId + ", WISEView ID is " + wiseViewId);

			trans.complete();
		} catch (DAOException e) {
			wirteMigrateError(e.getMessage());
			if (trans != null)
				trans.cancel();

		} finally {
			closeTransaction(trans);
		}

		return true;
	}

	private void appendPollutantControls(ControlEquipment tergetCe,
			ControlEquipment sourceCe) {
		List<PollutantsControlled> pollConts = sourceCe
				.getPollutantsControlled();

		for (PollutantsControlled pollutant : pollConts) {
			boolean isContain = tergetCe
					.getPollCont(pollutant.getPollutantCd()) != null;

			if (!isContain)
				tergetCe.addPollutantsControlled(pollutant);
		}
	}

	public void retrieveEuEmissions(EmissionUnit emissionUnit)
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO();
		facilityDAO.retrieveEuEmissions(emissionUnit);
	}

	@Override
	public List<EmissionUnitReplacement> retrieveEmissionUnitReplacements(
			Integer emuId) throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO();
		return facilityDAO.retrieveEmissionUnitReplacements(emuId);
	}

	@Override
	public void saveEmissionUnitReplacements(
			List<EmissionUnitReplacement> emissionUnitReplacements,
			Integer emuId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			FacilityDAO facilityDAO = facilityDAO(trans);
			facilityDAO.removeEmissionUnitReplacements(emuId);
			for (EmissionUnitReplacement emissionUnitReplacement : emissionUnitReplacements) {
				emissionUnitReplacement.setEmuId(emuId);
				facilityDAO
						.createEmissionUnitReplacement(emissionUnitReplacement);
			}
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("emuId=" + emuId.toString(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

	}

	@Override
	public List<FixmeCompany> retrieveFixmeCompanies() throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		List<FixmeCompany> ret = new ArrayList<FixmeCompany>(0);
		try {
			FacilityDAO facilityDAO = facilityDAO(trans);
			ret = facilityDAO.retrieveFixmeCompanies();
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("retrieveFixmeCompanies", trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	@Override
	public boolean removeFixmeCompanies() throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		try {
			FacilityDAO facilityDAO = facilityDAO(trans);
			ret = facilityDAO.removeFixmeCompanies();
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("removeFixmeCompanies", trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	@Override
	public boolean modifyFacilityFedRulesAndRegsSubparts(Facility facility,
			Transaction trans) throws DAOException, RemoteException {
		FacilityDAO facilityDAO = facilityDAO(trans);
		boolean ret = false;

		try {
			// update Part 61 NESHAP Subparts
			if (facility.isNeshaps()) {
				facilityDAO.removeFacilityNeshapsSubparts(facility.getFpId());
				for (PollutantCompCode pcc : facility
						.getNeshapsSubpartsCompCds()) {
					facilityDAO.addFacilityNeshapsSubpart(pcc);
				}
			}

			// update Part 63 NESHAP (MACT) Subparts
			if (facility.isMact()) {
				facilityDAO.removeFacilityMACTSubparts(facility.getFpId());
				for (String mactSubpart : facility.getMactSubparts()) {
					facilityDAO.addFacilityMACTSubpart(facility.getFpId(),
							mactSubpart);
				}
			}

			// update nsps Subparts
			if (facility.isNsps()) {
				facilityDAO.removeFacilityNSPSSubparts(facility.getFpId());
				for (String nspsSubpart : facility.getNspsSubparts()) {
					facilityDAO.addFacilityNSPSSubpart(facility.getFpId(),
							nspsSubpart);
				}
			}

			trans.complete();
		} catch (DAOException e) {
			logger.error(
					e.getMessage() + " for facility="
							+ facility.getFacilityId(), e);
			cancelTransaction("modifyFacilityFedRulesAndRegsSubparts", trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * Retrieves all facility ID reference Data for a given county
	 * 
	 * @param String
	 *            county
	 * @return FacilityIdRef
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityIdRef[] retrieveFacilityIdRefs(String countyCd)
			throws DAOException {
		// This is def table; read from read only database
		FacilityDAO facilityDAO = facilityDAO();

		return facilityDAO.retrieveFacilityIdRefs(countyCd);
	}

	/**
	 * Get a new facility ID.
	 * 
	 * @param facilityIdRef
	 *            Facility inventory to create
	 * @return Facility Id
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String getNewFacilityId(FacilityIdRef facilityIdRef)
			throws DAOException {
		String facilityId = null;
		Transaction trans = TransactionFactory.createTransaction();

		try {
			facilityId = getNewFacilityId(facilityIdRef, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return facilityId;
	}

	private String getNewFacilityId(FacilityIdRef facilityIdRef,
			Transaction trans) throws DAOException {
		String facilityId = null;
		Integer nextFacIdSeqNum = null;
		String formatNextFacIdSeqNum;
		FacilityDAO facilityDAO = facilityDAO(trans);
		Facility fac;

		try {
			while (true) {
				nextFacIdSeqNum = facilityDAO
						.nextfacilityIdSeqNum(facilityIdRef);
				if (nextFacIdSeqNum.intValue() > 9999) {
					DAOException e = new DAOException(
							"Cannot get any more facility ID. Maximum Sequence ID number readhed");
					throw e;
				}

				formatNextFacIdSeqNum = String.format("%04d", nextFacIdSeqNum);
				facilityId = facilityIdRef.getDolaId()
						+ facilityIdRef.getCountyCd()
						+ facilityIdRef.getCityCd() + formatNextFacIdSeqNum;
				fac = facilityDAO.retrieveFacility(facilityId);
				if (fac == null) {
					break;
				}
			}
		} catch (DAOException e) {
			throw e;
		}

		return facilityId;
	}

	// create Emission Units, processes, control equipment, egress points
	private CopyOnChangeMaps createCopyOnChangeMaps(int fpId,
			Facility newFacility, Transaction trans, boolean useObjectId,
			boolean allowEmissions) throws DAOException {
		CopyOnChangeMaps copyOnChangeMaps = new CopyOnChangeMaps();
		copyOnChangeMaps.fpId = fpId;

		EmissionUnit[] emissionUnits = newFacility.getEmissionUnits().toArray(
				new EmissionUnit[0]);
		EmissionUnit newEU;
		EmissionProcess newEP;
		EgressPoint newEGP;
		ControlEquipment newCE;
		Integer fromFpNodeId;
		Integer toFpNodeId;
		Integer tempFpNodeId;

		EgressPoint[] egressPoints = newFacility.getEgressPoints();
		HashMap<String, Integer> newEgressPoints = new HashMap<String, Integer>();

		loggerDebug("**** Iterating over Facility Egress Points *****");
		for (EgressPoint tempEGP : egressPoints) {
			logger.debug("EGP: " + tempEGP.getReleasePointId()
					+ ", old fpNodeId = " + tempEGP.getFpNodeId()
					+ " to newEgressPoints");
			tempFpNodeId = tempEGP.getFpNodeId();
			tempEGP.setFpId(fpId);
			if (!useObjectId) {
				tempEGP.setFpNodeId(null);
			}
			newEGP = createEgressPoint(tempEGP, trans);
			logger.debug("Adding EGP: " + newEGP.getReleasePointId()
					+ ", new fpNodeId = " + newEGP.getFpNodeId()
					+ " to newEgressPoints");
			newEgressPoints.put(newEGP.getReleasePointId(),
					newEGP.getFpNodeId());
			copyOnChangeMaps.fpNodeIds.put(tempFpNodeId, newEGP.getFpNodeId());
		}
		ControlEquipment[] controlEquips = newFacility.getControlEquips()
				.toArray(new ControlEquipment[0]);
		HashMap<String, Integer> newContEquips = new HashMap<String, Integer>();

		for (ControlEquipment tempCE : controlEquips) {
			tempFpNodeId = tempCE.getFpNodeId();
			tempCE.setFpId(fpId);
			if (!useObjectId) {
				tempCE.setFpNodeId(null);
			}
			newCE = createControlEquipment(tempCE, trans);
			newContEquips.put(newCE.getControlEquipmentId(),
					newCE.getFpNodeId());
			copyOnChangeMaps.fpNodeIds.put(tempFpNodeId, newCE.getFpNodeId());
		}

		// already created relationship from Control to Egress
		HashMap<String, String> createdRel = new HashMap<String, String>();

		Integer tempEuId;

		for (EmissionUnit tempEU : emissionUnits) {
			loggerDebug("**** Processing EU: " + tempEU.getEpaEmuId()
					+ " *****");
			EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses()
					.toArray(new EmissionProcess[0]);

			tempEuId = tempEU.getEmuId();
			tempFpNodeId = tempEU.getFpNodeId();
			tempEU.setFpId(fpId);
			if (!useObjectId) {
				tempEU.setFpNodeId(null);
				tempEU.setEmuId(null);
			}
			newEU = createEmissionUnit(tempEU, trans, allowEmissions);

			copyOnChangeMaps.euIds.put(tempEuId, newEU.getEmuId());
			copyOnChangeMaps.fpNodeIds.put(tempFpNodeId, newEU.getFpNodeId());

			loggerDebug("**** Iterating over EU Emission Processes *****");
			for (EmissionProcess tempEP : emissionProcesses) {
				loggerDebug("tempEP process Id = " + tempEP.getProcessId()
						+ ", fpNodeId = " + tempEP.getFpNodeId());
				tempFpNodeId = tempEP.getFpNodeId();
				tempEP.setFpId(fpId);
				tempEP.setEmissionUnitId(newEU.getEmuId());
				if (!useObjectId) {
					tempEP.setFpNodeId(null);
				}
				newEP = createEmissionProcess(tempEP, trans);
				loggerDebug("newEP process Id = " + newEP.getProcessId()
						+ ", fpNodeId = " + newEP.getFpNodeId());
				fromFpNodeId = newEP.getFpNodeId();
				copyOnChangeMaps.fpNodeIds.put(tempFpNodeId,
						newEP.getFpNodeId());

				EgressPoint[] egressPoints1 = tempEP.getEgressPoints().toArray(
						new EgressPoint[0]);
				loggerDebug("---- Iterating over EmissionProcess Egress Points ----");
				for (EgressPoint tempEGP : egressPoints1) {
					toFpNodeId = newEgressPoints.get(tempEGP
							.getReleasePointId());
					// if egress point is in internal copy, but not external,
					// drop it, but log an error.
					if (toFpNodeId == null) {
						logger.error("Egress point '"
								+ tempEGP.getReleasePointId()
								+ "' exists in internal copy of profile, but not in copy submitted from gateway.");
						continue;
					}
					// get flow factor
					if (tempEGP.isFugitive()) {
						// no flow factor
						// logger.error("INFO: processNm=" +
						// tempEP.getEmissionProcessNm() + ",EgressPointId=" +
						// tempEGP.getEgressPointId()+ " " + fromFpNodeId + ":"
						// + toFpNodeId);
						createRelationShip(fromFpNodeId, toFpNodeId, trans);
					} else {
						FacilityEmissionFlow fef;
						fef = FacilityEmissionFlow.getEmissionFlow(
								tempEP.getEpEmissionFlows(),
								FacilityEmissionFlow.STACK_TYPE,
								tempEGP.getReleasePointId());
						if (fef != null && fef.getFlowFactor() != null) {
							createRelationShip(fromFpNodeId, toFpNodeId,
									fef.getFlowFactor(), trans);
						} else {
							// this should not happen
							logger.error("Should have had a flowfactor: fromNodeId="
									+ fromFpNodeId
									+ " toFpNodeId="
									+ toFpNodeId);
							createRelationShip(fromFpNodeId, toFpNodeId, trans);
						}
					}
				}

				ControlEquipment[] controlEquips1 = tempEP
						.getControlEquipments()
						.toArray(new ControlEquipment[0]);
				for (ControlEquipment tempCE : controlEquips1) {
					FacilityEmissionFlow fef;
					fef = FacilityEmissionFlow.getEmissionFlow(
							tempEP.getEpEmissionFlows(),
							FacilityEmissionFlow.CE_TYPE,
							tempCE.getControlEquipmentId());
					Float ff = null;
					if (fef == null) {
						// This should not happen
						logger.error("Should have had a flowfactor for control equipment: correlationId="
								+ tempCE.getCorrelationId()
								+ ", fromFpNodeId="
								+ fromFpNodeId);
					} else {
						ff = fef.getFlowFactor();
					}
					createContEquipComp(tempCE, ff, newContEquips,
							newEgressPoints, createdRel, fromFpNodeId, trans);
				}
			}
		}

		return copyOnChangeMaps;
	}

	private void createRoles(FacilityDAO facilityDAO,
			InfrastructureDAO infraDAO, String facilityId) throws DAOException {
		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				FacilityRoleDef[] facRoleDefs;
				ArrayList<FacilityRole> facRoles = new ArrayList<FacilityRole>();
				FacilityRole facRole;

				facRoleDefs = infraDAO.retrieveDefaultFacilityRoles(facilityId
						.substring(2, 4));

				if (facRoleDefs == null || facRoleDefs.length == 0) {
					throw new DAOException(
							"Default user roles is not defined for the county.");
				}

				for (FacilityRoleDef tempRole : facRoleDefs) {
					facRole = new FacilityRole(tempRole, facilityId);
					facRoles.add(facRole);
				}

				// if (facRoles.size() != CommonConst.NUM_FACILITY_ROLES) {
				// throw new DAOException("Facility " + facilityId
				// +" Must have 16 roles, only received " + facRoles.size());
				// }

				facilityDAO.createFacilityRoles(facRoles
						.toArray(new FacilityRole[0]));
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	private EmissionUnit createEmissionUnit(EmissionUnit emissionUnit,
			Transaction trans, boolean allowEmissions) throws DAOException {
		EmissionUnit ret = null;
		FacilityDAO facilityDAO = facilityDAO(trans);

		try {

			FacilityNode facilityNode = facilityDAO.createFacilityNode(
					emissionUnit.getFpId(), emissionUnit.getFpNodeId(),
					emissionUnit.getCorrelationId());
			logger.debug("emissionUnit.getEmissionUnitTypeCd(): "
					+ emissionUnit.getEmissionUnitTypeCd());
			if (emissionUnit.getEmissionUnitTypeCd() != null
					&& emissionUnit.getEmissionUnitTypeCd().length() > 0) {
				EmissionUnitTypeDAO euTypeDAO = getEUTypeDAO(
						emissionUnit.getEmissionUnitTypeCd(), trans, false);
				logger.debug("emissionUnit.getEmuId():" + emissionUnit.getEmuId());
				if (euTypeDAO != null) {
//					emissionUnit.setEmissionUnitType(euTypeDAO
//							.retrieveEmissionUnitType(emissionUnit.getEmuId()));				
					EmissionUnitType euType = retrieveEmissionUnitType(emissionUnit.getEmuId(), euTypeDAO);
					emissionUnit.setEmissionUnitType(euType);			
				}
			}

			emissionUnit.setFpNodeId(facilityNode.getFpNodeId());

			ret = facilityDAO.createEmissionUnit(emissionUnit);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)
					&& allowEmissions) {
				for (EuEmission emission : ret.getEuEmissions()) {
					emission.setEmuId(ret.getEmuId());
					facilityDAO.createEuEmission(emission);
				}
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}

	@Override
	public Map<String, ArrayList<DataDetail>> retrieveControlEquipmentDataDetails()
			throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO(false);
		return facilityDAO.retrieveCeDataDetails();
	}

	@Override
	public void saveEmissionUnitPTE(EmissionUnit eu) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			FacilityDAO facilityDAO = facilityDAO(trans);

			// update emissions
			facilityDAO.removeEuEmissions(eu.getEmuId());
			for (EuEmission emission : eu.getEuEmissions()) {
				emission.setEmuId(eu.getEmuId());
				facilityDAO.createEuEmission(emission);
			}

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("emuId=" + eu.getEmuId().toString(), trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

	}
	
	public void createTransferNotes(Facility sourceFacility, Facility targetFacility, int userId, Transaction trans) throws DAOException
	{
		String transferFacilityNote = "Transfer EU: Emission units ";
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
		String emissionUnitInfo;
		EmissionUnit[] emissionUnits = sourceFacility.getEmissionUnits()
				.toArray(new EmissionUnit[0]);
		for (EmissionUnit tempEU : emissionUnits) {
			if (tempEU.isSelected()) {
				emissionUnitInfo = "[" + tempEU.getEpaEmuId() + "]";
				} else {
					continue;
				}
			if ((transferFacilityNote.length() + emissionUnitInfo.length()) > 3800) {
				transferFacilityNote += " More ...";
				break;
			}
			transferFacilityNote += emissionUnitInfo;
		}
		transferFacilityNote = transferFacilityNote + " transferred from facility "+ sourceFacility.getFacilityId() +" (source) to "  + targetFacility.getFacilityId()+ " (destination)";

		
		if (!transferFacilityNote.equals("")) {
			FacilityNote facNote = new FacilityNote();
			facNote.setNoteTxt(transferFacilityNote);
			facNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
			facNote.setUserId(userId);
			facNote.setNoteTypeCd(NoteType.DAPC);
			facNote.setFpId(sourceFacility.getFpId());
			facNote.setFacilityId(sourceFacility.getFacilityId());
			createFacilityNote(facNote, trans);
			facNote.setFpId(targetFacility.getFpId());
			facNote.setFacilityId(targetFacility.getFacilityId());
			createFacilityNote(facNote, trans);
		}
	}
	
	public EmissionUnit retrieveEmissionUnitByCorrEpaEmuId(Integer fpId, Integer corrEpaEmuId) throws DAOException {
		FacilityDAO facilityDAO = getFacilityDAO();

		return facilityDAO.retrieveEmissionUnitByCorrEpaEmuId(fpId, corrEpaEmuId);
	}
	
	public ValidationMessage[] validateNewFacilityRequest(Facility facility)
			throws DAOException {
		ValidationMessage[] validationMessages;

		if (facility == null) {
			DAOException e = new DAOException("invalid fpId (null)");
			throw e;
		}

		validationMessages = facility.validateForCreateFacility();
		return validationMessages;
	}

	/**
	 * Create a new facility request submitted from the portal.
	 * 
	 * @param facilityRequest
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @return String 
	 * 			new facility request id
	 */
	public String createNewFacilityRequestFromGateWay( 
			FacilityRequest facilityRequest)
			throws DAOException {
		String requestId = null;
		// The incoming objects are expected to be fully valid and complete.
		
		// Insert Facility Request into db 
		FacilityDAO facDAO = facilityDAO();
		InfrastructureDAO infraDAO = infrastructureDAO();
		
		try {
			
			Address phyAddr = facilityRequest.getPhyAddr();
			phyAddr.setAddressId(null);

			if (SystemPropertyDef.getSystemPropertyValueAsBoolean("APP_PLSS_AUTO_REPLICATION", true)){
				autoFillNullLocationData(phyAddr);
			}

			Address newAddress = infraDAO.createAddress(phyAddr);
			facilityRequest.setPhyAddr(newAddress);
			facilityRequest.getPhyAddr().setAddressId(newAddress.getAddressId());
		
			FacilityRequest tempFacilityRequest = facDAO
					.createFacilityRequest(facilityRequest);

			requestId = tempFacilityRequest.getReqId();
			
			

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
		return requestId;
	}

	public Facility cloneFacility(Facility facility, Facility targetFacility, 
			Integer currentUserId) throws DAOException {
		List<FacilityNode> nodes = new ArrayList<FacilityNode>();
		List<EmissionUnit> eus = facility.getEmissionUnits();
		for (EmissionUnit eu : eus) {
			if (eu.isSelected()) {
				logger.debug(" ==> eu selected: " + eu.getEpaEmuId());
				List<EmissionProcess> eps = eu.getEmissionProcesses();
				for (EmissionProcess ep : eps) {
					if (ep.isSelected()) {
						logger.debug(" ====> ep selected: " + ep.getProcessId());
						logger.debug(" ======> ep fpNodeId: " + ep.getFpNodeId());
						nodes.add(ep);
						FacilityRelationship[] epfrs = ep.getRelationships();
						for (FacilityRelationship epfr : epfrs) {
							logger.debug(" ======> ep node: " + epfr.getFromNodeId() + " -> " + epfr.getToNodeId());
						}
						if (epfrs.length > 0) {
							Set<ControlEquipment> ces = ep.getAllControlEquipments();
							for (ControlEquipment ce : ces) {
								if (ce.isSelected()) {
									logger.debug(" ======> ce selected: " + ce.getControlEquipmentId());
									nodes.add(ce);
								} else {
									logger.debug(" ======> ce not selected: " + ce.getControlEquipmentId());
								}
							}
							Set<EgressPoint> rps = ep.getAllEgressPoints();
							for (EgressPoint rp : rps) {
								if (rp.isSelected()) {
									logger.debug(" ======> rp selected: " + rp.getReleasePointId());										
									nodes.add(rp);
								} else {
									logger.debug(" ======> rp not selected: " + rp.getReleasePointId());										
								}
							}
						}
					} else {
						logger.debug(" ====> ep not selected: " + ep.getProcessId());					
					}
				}
			} else {
				logger.debug(" ==> eu not selected: " + eu.getEpaEmuId());				
			}
		}
		logger.debug("nodes.size() = " + nodes.size());
		return null;
	}
	/**
	 * Retrieves all facility requests matching the criteria parameters supplied.
	 * Suppling all "nulls" will return all facility requests in the DB.
	 * 
	 * @param String
	 *            facilityName either the exact name of the facility, a
	 *            wildcarded string or null.
	 * @param String
	 *            requestId either the exact ID, a wildcarded string or null.
	 * @param Integer
	 *            counyId the exact county ID or null.
	 * @param String
	 *            operatingStatusCd either the exact operating status CD, or
	 *            null.
	 * @param String
	 *            doLaaCd either the exact DO-LAA CD, or null.
	 * @param String
	 *            emissionReportCategoryCd either the exact
	 *            emissionReportCategoryCd, or null.
	 * @param String
	 *            permitClassCd either the exact permitClassCd, or null
	 * @param String
	 *            tvPermitStatusCd either the exact tvPermitStatusCd, or null
	 * 
	 * @return FacilityRequestList[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityRequestList[] searchFacilityRequests(String facilityName,
			String requestId, String companyName,
			String countyCd, String operatingStatusCd, String doLaaCd,
			String firstName, String lastName, String externalUsername,
			String address1, String cntId, String phone, String email,
			String requestState, boolean unlimitedSearch,
			String facilityTypeCd,
			Integer companyId) throws DAOException {

		return facilityDAO().searchFacilityRequests(facilityName, requestId,
				companyName, countyCd, operatingStatusCd, doLaaCd,
				firstName, lastName, externalUsername, address1, cntId, phone,
				email, requestState, unlimitedSearch, facilityTypeCd, companyId);
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public FacilityRequest retrieveFacilityRequest(Integer requestId) throws DAOException {
		FacilityRequest ret = null;
		FacilityDAO facilityDAO = getFacilityDAO();

		if (requestId != null) {
			try {
				ret = facilityDAO.retrieveFacilityRequest(requestId);
			} catch (DAOException de) {
				logger.error(
						"retrieve facility request detail failed; request_id = [" + requestId
								+ "] :"
								+ de.getMessage(), de);
				throw de;
			}
		}
		return ret;
	}

	/**
		 * Performs all modify functions of modifyFacilityRequestInternal().
		 * 
		 * @param FacilityRequest
		 *            FacilityRequest to modify
		 * @return boolean True = success, false = failure.
		 * 
		 * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="Required"
		 */
		public boolean modifyFacilityRequest(FacilityRequest facilityRequest, int userId)
				throws DAOException {
			HashMap<String, String> data = null;
			data = modifyFacilityRequestInternal(facilityRequest, userId);
			return data != null;
		}
		
		/**
		 * Modifies the supplied facility. This method only modifies the facility,
		 * none of the facilities contained objects are updated. To modify an entire
		 * Facility, including contained objects, the FacilityBO should be used.
		 * 
		 * @param Facility
		 *            Facility to modify
		 * @return boolean True = success, false = failure.
		 * 
		 * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="Required"
		 */
		public HashMap<String, String> modifyFacilityRequestInternal(FacilityRequest facilityRequest,
				int userId) throws DAOException {
			loggerDebug("modifyFacilityRequestInternal started; facilityRequest = "
					+ facilityRequest.getRequestId());

			FacilityDAO facilityDAO = facilityDAO();
			InfrastructureDAO infraDAO = infrastructureDAO();
			HashMap<String, String> data = new HashMap<String, String>();
			FacilityRequest newFacility = null;
			
			// check to be sure the Facility request still exists
			FacilityRequest facilityRequestToModify = null;
			try {
				facilityRequestToModify = this.retrieveFacilityRequest(facilityRequest.getRequestId());
				if (facilityRequestToModify == null) {
					logger.debug("Cannot retrieve facility request to be modified; Operation unsuccessful.");
					return null;
				}
			} catch (DAOException e) {
				logger.debug("modifyFacilityRequestInternal retrieveFacilityRequest DAOException");
				throw new DAOException(
						"Cannot retrieve facility request to modify; Operation unsuccessful.", e);
			}
			
			try {

				facilityDAO.modifyFacilityRequest(facilityRequest);

			} catch (DataStoreConcurrencyException e) {
				logger.debug("modifyFacilityRequestInternal DataStoreConcurrencyException");
				throw new DataStoreConcurrencyException(
						"Cannot modify facility request; Operation unsuccessful.", e);
			} catch (DAOException e) {
			
				logger.debug("modifyFacilityRequestInternal DAOException");
				throw new DAOException(
						"Cannot modify facility request; Operation unsuccessful.", e);
				
			} catch (Exception e) {
				logger.debug("modifyFacilityRequestInternal Exception");
				throw new DAOException(
						"Cannot modify facility request; Operation unsuccessful.", e);
			} finally {
				logger.debug("modifyFacilityRequestInternal finally");
			}

			logger.debug("modifyFacilityInternal completed; facility request = "
					+ facilityRequest.getRequestId());
			return data;
		}
		
		/**
		 * @param facilityId
		 * @param stackTest
		 * @return void
		 * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="Required"
		 */
		public void deleteFacilityRequest(FacilityRequest facilityRequest)
				throws DAOException {
			
			try {
				FacilityDAO facilityDAO = facilityDAO();
				facilityDAO.removeFacilityRequest(facilityRequest.getRequestId());
				
				InfrastructureDAO infraDAO = infrastructureDAO();
				// This (addressId == null) should not happen in the real world.
				if (facilityRequest.getPhyAddr().getAddressId() != null) {
					infraDAO.removeAddress(facilityRequest.getPhyAddr().getAddressId());
				}
			} catch (DAOException de) {
				logger.error(
						"deleteFacilityRequest failed for facility request id = " + facilityRequest.getRequestId() + "; "
								+ de.getMessage(), de);
				throw new DAOException(
						"Cannot delete facility request; Operation unsuccessful.", de);
				
			} finally {
				logger.error(
						"deleteFacilityRequest succeeded for facility request id = " + facilityRequest.getRequestId());
			}
		}
		
		/**
		 * @ejb.interface-method view-type="remote"
		 * @ejb.transaction type="Supports"
		 */
		public Integer retrieveFacilityRequestCount(Integer companyId) throws DAOException {
			Integer ret = 0;
			FacilityDAO facilityDAO = getFacilityDAO();

			if (companyId != null) {
				try {
					ret = facilityDAO.retrieveFacilityRequestCount(companyId);

				} catch (DAOException de) {
					logger.error(
							"retrieve facility request count failed; request_id = [" + companyId
									+ "] :"
									+ de.getMessage(), de);
					throw de;
				}
			}
			return ret;
		}

	private boolean updateFacilityRolesTransactional(List<FacilityRole> facilityRoles, String facilityRoleCd,
			int userId) throws DAOException {

		boolean ret = false;
		FacilityDAO facilityDAO = facilityDAO();
		InfrastructureDAO infraDAO = infrastructureDAO();

		Map<String, FieldAuditLog[]> facilityAuditLogMap = new HashMap<String, FieldAuditLog[]>();
		for (FacilityRole facilityRole : facilityRoles) {
			String facilityId = facilityRole.getFacilityId();
			FieldAuditLog[] auditLog = new FieldAuditLog[1];
			auditLog[0] = new FieldAuditLog("frol", facilityId, "NO VALUE", "Some of the facility roles updated");
			auditLog[0].setFacilityId(facilityId);
			facilityAuditLogMap.put(facilityId, auditLog);
		}

		facilityDAO.removeFacilityRoles(facilityRoles, facilityRoleCd, userId);
		facilityDAO.createFacilityRoles(facilityRoles);
		infraDAO.createFieldAuditLogs(facilityAuditLogMap, userId);

		return ret;
	}

	@Override
	public boolean updateFacilityRoles(FacilityRole[] facilityRoles, int userId, String facilityRoleCd)
			throws DAOException {

		int tot = 0;
		for (int i = 0; i < facilityRoles.length; i++) {
			if (facilityRoles[i] != null) {
				tot++;
			}
		}

		ArrayList<FacilityRole> facRolesList = new ArrayList<FacilityRole>();
		Collections.addAll(facRolesList, facilityRoles);
		updateFacilityRolesTransactional(facRolesList, facilityRoleCd, userId);

		return false;
	}

	@Override
	public FacilityRole[] retrieveFacilityRoles(FacilityList[] selectedFacilities, Integer userId) throws DAOException {

		FacilityRole[] ret = null;
		FacilityDAO facilityDAO = facilityDAO();

		ret = facilityDAO.retrieveFacilityRolesByFacilities(selectedFacilities, userId);
		return ret;
	}

	@Override
	public FacilityRole[] retrieveFacilityRoles(FacilityList[] selectedFacilities, String facilityRole, int userId)
			throws DAOException {

		FacilityRole[] ret = null;
		FacilityDAO facilityDAO = facilityDAO();

		ret = facilityDAO.retrieveFacilityRolesByFacilitiesByRole(selectedFacilities, facilityRole, userId);
		return null == ret ? new FacilityRole[0] : ret;
	}

		@Override
		public AppPermitSearchResult[] appPermitSearch(String searchCmpId,
				String searchFacilityId, Integer searchType, Integer appPermitType) throws DAOException {
			return getFacilityDAO().appPermitSearch(searchCmpId, searchFacilityId, searchType, appPermitType);
		}
		
		// Facility CEM/COM/CMS Limits
		
		public List<FacilityCemComLimit> retrieveFacilityCemComLimitListByFpId(
				Integer fpId, boolean staging) throws DAOException {

			List<FacilityCemComLimit> fcclList = null;

			try {

				fcclList = getFacilityDAO(staging)
						.retrieveFacilityCemComLimitListByFpId(fpId);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Error while retrieving Facility CEM/COM/CMS Limits");
				logger.error(e.getMessage());
			} catch (Exception e) {
				DisplayUtil
						.displayError("Error while retrieving Facility CEM/COM/CMS Limits");
				logger.error(e.getMessage());
			} finally {

			}

			return fcclList;

		}
		
		public List<FacilityCemComLimit> retrieveFacilityCemComLimitListByMonitorId(
				Integer monitorId, boolean staging) throws DAOException {

			List<FacilityCemComLimit> fcclList = null;

			try {

				fcclList = getFacilityDAO(staging)
						.retrieveFacilityCemComLimitListByMonitorId(monitorId);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Error while retrieving Facility CEM/COM/CMS Limits");
				logger.error(e.getMessage());
			} catch (Exception e) {
				DisplayUtil
						.displayError("Error while retrieving Facility CEM/COM/CMS Limits");
				logger.error(e.getMessage());
			} finally {

			}

			return fcclList;

		}

		@Override
		public FacilityCemComLimit createFacilityCemComLimit(FacilityCemComLimit fccl)
				throws DAOException {
			
			// get the CEM/COM/CMS monitor with which this equipment is associated
			ContinuousMonitor continuousMonitor =  
					continuousMonitorDAO().retrieveContinuousMonitor(fccl.getMonitorId());
			
			// create a new version of the facility if the current version is preserved
			Facility newFacility = copyFacilityProfile(
					continuousMonitor.getFpId(),
					new Timestamp(System.currentTimeMillis()),
					InfrastructureDefs.getCurrentUserId());
			
			if (!newFacility.getFpId().equals(continuousMonitor.getFpId())) {
				// if a new facility version was created, then create the
				// cem/com limit in the new current version of the facility
				ContinuousMonitor cm = continuousMonitorDAO()
						.retrieveContinuousMonitorByFpIdAndCorrId(
								newFacility.getFpId(),
								continuousMonitor.getCorrMonitorId());
				fccl.setMonitorId(cm.getContinuousMonitorId());
			}
			
			resetFacilityValidAndSubmit(newFacility, null);
			
			// invalidate any in-progress quarterly or RATA compliance reports 
			// associated with this facility
			complianceReportDAO().setActiveComplianceReportsValidatedFlag(
					newFacility.getFpId(), false);
			
			return facilityDAO().createFacilityCemComLimit(fccl);
		}

		public FacilityCemComLimit modifyFacilityCemComLimit(FacilityCemComLimit fccl)
				throws DAOException {

			// get the CEM/COM/CMS monitor with which this equipment is associated
			ContinuousMonitor continuousMonitor = continuousMonitorDAO()
					.retrieveContinuousMonitor(fccl.getMonitorId());
	
			// create a new version of the facility if the current version is
			// preserved
			Facility newFacility = copyFacilityProfile(continuousMonitor.getFpId(),
					new Timestamp(System.currentTimeMillis()),
					InfrastructureDefs.getCurrentUserId());
	
			if (!newFacility.getFpId().equals(continuousMonitor.getFpId())) {
				// if a new facility version was created, then modify the
				// cem/com limit in the new current version of the facility
				ContinuousMonitor cm = continuousMonitorDAO()
						.retrieveContinuousMonitorByFpIdAndCorrId(
								newFacility.getFpId(),
								continuousMonitor.getCorrMonitorId());
				
				FacilityCemComLimit limit = retrieveFacilityCemComLimitByMonitorIdAndCorrId(
						cm.getContinuousMonitorId(), fccl.getCorrLimitId());
				
				fccl.setLimitId(limit.getLimitId());
				fccl.setMonitorId(limit.getMonitorId());
				fccl.setLastModified(limit.getLastModified());
			}
			
			facilityDAO().modifyFacilityCemComLimit(fccl);
			
			resetFacilityValidAndSubmit(newFacility, null);
			
			// invalidate any in-progress quarterly or RATA compliance reports 
			// associated with this facility
			complianceReportDAO().setActiveComplianceReportsValidatedFlag(
					newFacility.getFpId(), false);
			
			return fccl;
		}

		@Override
		public final Integer removeFacilityCemComLimit(FacilityCemComLimit fccl)
				throws DAOException {
			
			// get the CEM/COM/CMS monitor with which this equipment is associated
			ContinuousMonitor continuousMonitor = continuousMonitorDAO()
					.retrieveContinuousMonitor(fccl.getMonitorId());
			
			// TFS: 5829 - preserve the facility version so that the deletion
			// shall trigger the creation of new version of the facility inventory
			markProfileHistory(continuousMonitor.getFpId());
			
			// create a new version of the facility if the current version is preserved
			Facility newFacility = copyFacilityProfile(continuousMonitor.getFpId(),
					new Timestamp(System.currentTimeMillis()),
					InfrastructureDefs.getCurrentUserId());
	
			if (!newFacility.getFpId().equals(continuousMonitor.getFpId())) {
				// if a new facility version was created, then delete the
				// cem/com limit in the new current version of the facility
				ContinuousMonitor cm = continuousMonitorDAO()
						.retrieveContinuousMonitorByFpIdAndCorrId(
								newFacility.getFpId(),
								continuousMonitor.getCorrMonitorId());
				
				fccl = retrieveFacilityCemComLimitByMonitorIdAndCorrId(
						cm.getContinuousMonitorId(), fccl.getCorrLimitId());
			}
	
			facilityDAO().removeFacilityCemComLimit(fccl);
			
			resetFacilityValidAndSubmit(newFacility, null);
			
			// invalidate any in-progress quarterly or RATA compliance reports 
			// associated with this facility
			complianceReportDAO().setActiveComplianceReportsValidatedFlag(
					newFacility.getFpId(), false);
			
			return newFacility.getFpId();
		}
		
		private void createContinuousMonitors(List<ContinuousMonitor> continuousMonitorList, 
				CopyOnChangeMaps copyOnChangeMaps, Integer fpId)
				throws DAOException {
			
			for(ContinuousMonitor old : continuousMonitorList) {
				if (isInternalApp()) {
					old.setContinuousMonitorId(null);
					old.setLastModified(null);
					old.setFpId(fpId);
				}
					
				// first create the continuous monitor and then the associated data
				ContinuousMonitor newcm = getContinuousMonitorDAO().createContinuousMonitor(old);
				Integer monitorId = newcm.getContinuousMonitorId();
				
				// create associated cem/com/limits with this monitor
				createFacilityCemComLimits(old.getFacilityCemComLimitList(), monitorId);
				
				// create associated equipments with this monitor
				createMonitorEquipments(old.getContinuousMonitorEqtList(), monitorId);
				
				// create associated emissions units with this monitor
				createMonitorAssociatedFpEuIds(old.getAssociatedFpEuIds(), copyOnChangeMaps, monitorId);
				
				// create associated release points with this monitor
				createMonitorAssociatedFpEgressPointIds(old.getAssociatedFpEgressPointIds(), copyOnChangeMaps, monitorId);
				
				// create associated notes with this monitor
				// createMonitorNotes(old.getNotes(), monitorId); 
			}
 		}

	public ContinuousMonitor retrieveCompleteContinuousMonitor(ContinuousMonitor cm, boolean staging)
		throws DAOException {
		ContinuousMonitorDAO continuousMonitorDAO = getContinuousMonitorDAO(staging);
		FacilityDAO facilityDAO = getFacilityDAO(staging);
		
		ContinuousMonitor ret = new ContinuousMonitor(cm);
		Integer monitorId = ret.getContinuousMonitorId();
		Integer corrMonitorId = ret.getCorrMonitorId(); 
		
		// retrieve the associated monitor data
		ret.setAssociatedFpEuIds(continuousMonitorDAO.retrieveAssociatedFpEuIdsByMonitorId(monitorId));
		ret.setAssociatedFpEuEpaEmuIds(continuousMonitorDAO.retrieveAssociatedFpEuEpaEmuIdsByMonitorId(monitorId));
		ret.setAssociatedFpEgressPointIds(continuousMonitorDAO.retrieveAssociatedFpEgressPointIdsByMonitorId(monitorId));
		ret.setAssociatedFpEgressPointRPIds(continuousMonitorDAO.retrieveAssociatedFpReleasePointIdsByMonitorId(monitorId));
		
		ret.setContinuousMonitorEqtList(continuousMonitorDAO.retrieveContinuousMonitorEqtList(monitorId));
		
		if (isInternalApp()) {
			if (null != corrMonitorId) {
				ret.setNotes(continuousMonitorDAO
						.retrieveContinuousMonitorNotes(corrMonitorId));
			}
		}
		
		ret.setFacilityCemComLimitList(facilityDAO.retrieveFacilityCemComLimitListByMonitorId(monitorId));
	
		return ret;
	}
		
	private List<ContinuousMonitor> retrieveCompleteContinuousMonitors(List<ContinuousMonitor> continuousMonitorList, boolean staging)
		throws DAOException {
		List<ContinuousMonitor> ret = new ArrayList<ContinuousMonitor>();
		for(ContinuousMonitor cm : continuousMonitorList) {
			ContinuousMonitor continuousMonitor = new ContinuousMonitor();
			continuousMonitor = retrieveCompleteContinuousMonitor(cm, staging);
			if(null != continuousMonitor) {
				ret.add(continuousMonitor);
			}
		}
		
		return ret;
	}
	
	private void createFacilityCemComLimits(List<FacilityCemComLimit> cemComLimits, Integer monitorId)
		throws DAOException {
		for(FacilityCemComLimit limit : cemComLimits) {
			if (isInternalApp()) {
				limit.setLastModified(null);
				limit.setLimitId(null);
			}
			limit.setMonitorId(monitorId);
			getFacilityDAO().createFacilityCemComLimit(limit);
		}
	}
	
	private void createMonitorEquipments(List<ContinuousMonitorEqt> equipments,
			Integer monitorId) throws DAOException {
		for (ContinuousMonitorEqt eqp : equipments) {
			if (isInternalApp()) {
				eqp.setMonitorEqtId(null);
				eqp.setLastModified(null);
			}
			eqp.setContinuousMonitorId(monitorId);
			getContinuousMonitorDAO().createContinuousMonitorEqtWithCreatedDt(
					eqp);
		}
	}
	
	private void createMonitorAssociatedFpEuIds(List<Integer> fpEuIds, CopyOnChangeMaps copyOnChangeMaps, 
			Integer monitorId) throws DAOException {
		for(Integer emuId : fpEuIds) {
			Integer newEmuId = copyOnChangeMaps.euIds.get(emuId);
			if(null != newEmuId) {
				getContinuousMonitorDAO().createAssociatedFpEuIdRef(monitorId, newEmuId);
			}
		}
	}
	
	private void createMonitorAssociatedFpEgressPointIds(List<Integer> fpEgressPointIds, CopyOnChangeMaps copyOnChangeMaps, 
			Integer monitorId) throws DAOException {
		for(Integer fpNodeId : fpEgressPointIds) {
			Integer newFpNodeId = copyOnChangeMaps.fpNodeIds.get(fpNodeId);
			if(null != newFpNodeId) {
				getContinuousMonitorDAO().createAssociatedFpEgressPointIdRef(monitorId, newFpNodeId);
			}
		}
	}
	
	/*private void createMonitorNotes(List<ContinuousMonitorNote> cmNotes, Integer monitorId)
		throws DAOException {
		for(ContinuousMonitorNote note : cmNotes) {
			note.setContinuousMonitorId(monitorId);
			note.setNoteId(null);
			note.setLastModified(null);
			
			Note tempNote = infrastructureDAO().createNote(note);
			if (tempNote != null) {
				continuousMonitorDAO().addContinuousMonitorNote(monitorId, tempNote.getNoteId());
			}
		}
	}*/

	@Override
	public FacilityCemComLimit retrieveFacilityCemComLimitByMonitorIdAndCorrId(
			Integer monitorId, Integer corrLimitId) throws DAOException {
		return facilityDAO().retrieveFacilityCemComLimitByMonitorIdAndCorrId(monitorId, corrLimitId);
	}

	@Override
	public ArrayList<LimitTrendReportLineItem> retrieveLimitTrendData(
			Integer corrLimitId, String facilityId) throws DAOException {
		return complianceReportDAO().retrieveLimitTrendData(corrLimitId, facilityId);
	}
	
	@Override
	public ArrayList<LimitTrendReportLineItem> retrievePeriodicLimitTrendData(
			Integer corrLimitId, String facilityId) throws DAOException {
		return complianceReportDAO().retrievePeriodicLimitTrendData(corrLimitId, facilityId);
	}
	
	public Integer retrieveCemsComplianceReportCountWithMonitor(
			Integer corrMonitorId) throws DAOException {
		Integer ret = 0;
		FacilityDAO facilityDAO = getFacilityDAO();

		try {
			ret = facilityDAO().retrieveCemsComplianceReportCountWithMonitor(
					corrMonitorId);

		} catch (DAOException de) {
			logger.error(
					"retrieve CEM Compliance Report count failed for correlated monitor id = ["
							+ corrMonitorId + "] : " + de.getMessage(), de);
			throw de;
		}

		return ret;
	}

	public Integer retrieveCemsComplianceReportCountWithLimit(
			Integer corrLimitId) throws DAOException {
		Integer ret = 0;
		FacilityDAO facilityDAO = getFacilityDAO();

		try {
			ret = facilityDAO().retrieveCemsComplianceReportCountWithLimit(
					corrLimitId);

		} catch (DAOException de) {
			logger.error(
					"retrieve CEM Compliance Report count failed for  correlated limit id = ["
							+ corrLimitId + "] :" + de.getMessage(), de);
			throw de;
		}

		return ret;
	}
	
	/**
	 * Retrieves facility profile with continuous monitors and cem/com limits
	 */
	public Facility retrieveFacilityWithMonitorsAndLimits(String facilityId) throws DAOException {
		Facility facility =  null;
		
		facility = facilityDAO().retrieveFacility(facilityId);

		if(null != facility) {
			// Retrieve Continuous Monitors
			List<ContinuousMonitor> continuousMonitors = new ArrayList<ContinuousMonitor>();
			List<ContinuousMonitor> completeContinuousMonitorsList = new ArrayList<ContinuousMonitor>();
			
			// get the monitors with basic data first
			continuousMonitors = Arrays.asList(continuousMonitorDAO()
					.retrieveContinuousMonitorByFpId(facility.getFpId()));
			// get the rest of the monitors data i.e., limits, equipment etc
			completeContinuousMonitorsList = retrieveCompleteContinuousMonitors(
					continuousMonitors, false);
			facility.setContinuousMonitorList(completeContinuousMonitorsList);

			// Retrieve cem/com limits.
			List<FacilityCemComLimit> limits = new ArrayList<FacilityCemComLimit>();
			limits = facilityDAO().retrieveFacilityCemComLimitListByFpId(
					facility.getFpId());
			facility.setFacilityCemComLimitList(limits);
		}
		
		return facility;
	}
	
	/**
	 * For a given emuId in the internal version of the facility, returns the emuId from
	 * the corresponding emission unit in the facility that was submited from the portal
	 * @param dapcFacility
	 * @param gatewayFacility
	 * @param emuId
	 * @return emuId of the corresponding eu in the gateway facility
	 */
	private Integer getEmuIdFromGatewayFacility(Facility dapcFacility,
			Facility gatewayFacility, Integer emuId) {
		Integer corrEpaEmuId = null;
		Integer gwFacEuEmuId = null;

		// first get the correlated id of the eu in the internal facility
		for (EmissionUnit newFacEU : dapcFacility.getEmissionUnits()) {
			if (newFacEU.getEmuId().equals(emuId)) {
				corrEpaEmuId = newFacEU.getCorrEpaEmuId();
				break;
			}
		}

		// next try to find the corresponding eu in the gateway facility
		if (null != corrEpaEmuId) {
			for (EmissionUnit gwFacEU : gatewayFacility.getEmissionUnits()) {
				if (gwFacEU.getCorrEpaEmuId().equals(corrEpaEmuId)) {
					gwFacEuEmuId = gwFacEU.getEmuId();
					break;
				}
			}
		}

		return gwFacEuEmuId;
	}
	
	/**
	 * For a given fpNodeId in the internal version of the facility, returns the fpNodeId from
	 * the corresponding release point in the facility that was submited from the portal
	 * @param dapcFacility
	 * @param gatewayFacility
	 * @param emuId
	 * @return fpNodeId of the corresponding release point in the gateway facility
	 */
	private Integer getFpNodeIdFromGatewayFacility(Facility dapcFacility,
			Facility gatewayFacility, Integer fpNodeId) {
		Integer correlationId = null;
		Integer gwFacRpFpNodeId = null;

		// first get the correlated id of the release point in the internal facility
		for (EgressPoint newFacEP : dapcFacility.getEgressPoints()) {
			if (newFacEP.getFpNodeId().equals(fpNodeId)) {
				correlationId = newFacEP.getCorrelationId();
				break;
			}
		}

		// next try to find the corresponding release point in the gateway facility
		if (null != correlationId) {
			for (EgressPoint gwFacEP : gatewayFacility.getEgressPoints()) {
				if (gwFacEP.getCorrelationId().equals(correlationId)) {
					gwFacRpFpNodeId = gwFacEP.getFpNodeId();
					break;
				}
			}
		}

		return gwFacRpFpNodeId;
	}
	
	/**
	 * Replaces the emuIds in the associatedFpEuIds list with the
	 * emuIds from the corresponding emissions units in the facility 
	 * that was submitted from the portal
	 * @param dapcFacility
	 * @param gatewayFacility
	 * @param associatedFpEuIds
	 * @return list of replaced emuIds from portal facility
	 */
	private List<Integer> replaceAssociatedFpEuIds(Facility dapcFacility,
			Facility gatewayFacility, List<Integer> associatedFpEuIds) {
		
		List<Integer> replaceIdList = new ArrayList<Integer>();
		
		for(Integer emuId : associatedFpEuIds) {
			Integer gwFacFpNodeId = getEmuIdFromGatewayFacility(dapcFacility,
				gatewayFacility, emuId);
			if (null != gwFacFpNodeId) {
				replaceIdList.add(gwFacFpNodeId);
			} else {
				// release point exists in the internal facility only
				replaceIdList.add(emuId);
			}
		}
		
		return replaceIdList;
	}
	
	/**
	 * Replaces the fpNodeIds in the associatedFpEgressPointIds list with the
	 * fpNodeIds from the corresponding release points in the facility 
	 * that was submitted from the portal
	 * @param newFacility
	 * @param gatewayFacility
	 * @param associatedFpEuIds
	 * @return list of replaced fpNodeIds from portal facility
	 */
	private List<Integer> replaceAssociatedFpEgressPointIds(Facility dapcFacility,
			Facility gatewayFacility, List<Integer> associatedFpEgressPointIds) {
		
		List<Integer> replaceIdList = new ArrayList<Integer>();
		
		for (Integer fpNodeId : associatedFpEgressPointIds) {
			Integer gwFacFpNodeId = getFpNodeIdFromGatewayFacility(dapcFacility,
					gatewayFacility, fpNodeId);
			if (null != gwFacFpNodeId) {
				replaceIdList.add(gwFacFpNodeId);
			} else {
				// release point exists in the internal facility only
				replaceIdList.add(fpNodeId);
			}
		}
			
		return replaceIdList;
	}
	
	/**
	 * Combines the cem/com limits list from internal facility and the portal
	 * facility
	 * @param newFacContiunousMonitor
	 * @param gwFacContinuousMonitor
	 * @return cobmibned cem/com limit list
	 */
	private List<FacilityCemComLimit> combineCemComLimitLists(
			ContinuousMonitor dapcFacMonitor, ContinuousMonitor gwFacMon) {
		List<FacilityCemComLimit> cemComLimitList = new ArrayList<FacilityCemComLimit>();

		cemComLimitList.addAll(dapcFacMonitor.getFacilityCemComLimitList());

		for (FacilityCemComLimit i : gwFacMon.getFacilityCemComLimitList()) {
			boolean found = false;
			for (FacilityCemComLimit j : dapcFacMonitor.getFacilityCemComLimitList()) {
				if (i.getCorrLimitId().equals(j.getCorrLimitId())) {
					found = true;
					break;
				}
			}

			if (!found) {
				i.setLimId(null);
				cemComLimitList.add(i);
			}
		}
		
		return cemComLimitList;
	}
	
	@Override
	public List<String> retrieveFacilitiesWithMatchingApiNumber(String apiNo,
			String facilityId) throws DAOException {
		
		// always check in the readOnly db
		return facilityDAO().retrieveFacilitiesWithMatchingApiNumber(apiNo, facilityId);
	}

	public Integer retrieveAddressIdForFacilityOnLastDOP(
			String facilityId, String lastDayOfPeriod) throws DAOException {
		Integer ret = 0;
		FacilityDAO facilityDAO = getFacilityDAO();

		try {
			ret = facilityDAO().retrieveAddressIdForFacilityOnLastDOP(
					facilityId, lastDayOfPeriod);

		} catch (DAOException de) {
			logger.error(
					"retrieve Address Id failed for facility id = ["
							+ facilityId + "] :" + de.getMessage(), de);
			throw de;
		}

		return ret;
	}
	
	public final String bulkUpdateFacilityRoles(String roleCd, FacilityList[] selectedFacilities, Integer currentUserId, Integer userIdForRole)
			throws DAOException {

		FacilityRole[] facilityRoles = retrieveFacilityRoles(selectedFacilities, roleCd, currentUserId);

		for (FacilityList facilityL : selectedFacilities) {

			List<FacilityRole> roles = new ArrayList<FacilityRole>();
			FacilityRole role = null;

			for (int i = 0; i < facilityRoles.length; i++) {
				if (facilityRoles[i].getFacilityId().equals(facilityL.getFacilityId())) {
					roles.add(facilityRoles[i]);
					if (facilityRoles[i].getFacilityRoleCd().equals(roleCd)) {
						role = facilityRoles[i];
					}
				}
			}

			// handle case where this role is not yet assigned
			if (role == null) {
				role = new FacilityRole();
				role.setFacilityRoleCd(roleCd);
				role.setFacilityId(facilityL.getFacilityId());
				roles.add(role);
			}
			role.setUserId(userIdForRole);
		}
		
		updateFacilityRoles(facilityRoles, currentUserId, roleCd);

		return FacilityRoles.SUCCESS;

	}
	
	@Override
	public ArrayList<EiDataImportFacilityInformation> retrieveEiDataImportFacilityInformation()
			throws DAOException {
		return getFacilityDAO().retrieveEiDataImportFacilityInformation();

	}
	
	@Override
	public Facility updateLatSubmissionType(Facility facility) throws DAOException {
		resetFacilityValidAndSubmit(facility, null);
		return facilityDAO().retrieveFacility(facility.getFpId());
	}

	@Override
	public List<String> retrieveInspectionIdsForCemComId(Integer limitId) throws DAOException {
		return fullComplianceEvalDAO().retrieveInspectionIdsForCemComId(limitId);
	}
	
	@Override
	public boolean isMonitorLimitsIncludedInInspectionReport(List<Integer> limitIds) throws DAOException {
		// If monitor has no limits, it cannot be part of Inspection report cem/com snapshot xref
		if (limitIds == null || limitIds.isEmpty()) {
			return false;
		}
		Integer inspectionCount = fullComplianceEvalDAO().getMonitorLimitsIncludedInInspectionReportCount(limitIds);
		if (inspectionCount != null && inspectionCount > 0)
			return true;
		else 
			return false;
	}
	
	
	public EmissionUnitType retrieveEmissionUnitType(EmissionUnit eu, boolean staging) throws DAOException{
		EmissionUnitType euType = null;
		if (eu.getEmissionUnitTypeCd() != null && eu.getEmissionUnitTypeCd().length() > 0) {
			EmissionUnitTypeDAO euTypeDAO = getEUTypeDAO(eu.getEmissionUnitTypeCd(), null, staging);
			if (euTypeDAO != null) {
				euType = euTypeDAO.retrieveEmissionUnitType(eu.getEmuId());
				if (euType!=null){
					List<Component> components = euTypeDAO.retrieveComponentsByEmuId(eu.getEmuId());
					euType.setComponents(components);
				}
			}
		}
		return euType;
	}
	
	
	public EmissionUnitType retrieveEmissionUnitType(Integer emuId, EmissionUnitTypeDAO euTypeDAO) throws DAOException{
		EmissionUnitType euType = null;
		if (euTypeDAO != null) {
			euType = euTypeDAO.retrieveEmissionUnitType(emuId);
			if (euType!=null){
				List<Component> components = euTypeDAO.retrieveComponentsByEmuId(emuId);
				euType.setComponents(components);
			}
		}
		return euType;
	}
	
	
	
	//euTypeDAO will contain schema information
	public void modifyEmissionUnitType(EmissionUnitType euType, EmissionUnitTypeDAO euTypeDAO) throws DAOException{
		euTypeDAO.modifyEmissionUnitType(euType);
		euTypeDAO.deleteComponentCountByEmuId(euType.getEmuId());
		if (EmissionUnitTypeDef.FUG.equals(euType.getEmissionUnitTypeCd()) && euType.isFugitiveLeaks()){
			for (Component component : euType.getComponents()){
				component.setEmuId(euType.getEmuId());
				euTypeDAO.addComponentCount(component);
			}
		}
	}
	
	@Override
	public List<FacilityRoleActivity> retrieveActivitiesByFacilityRole(String facilityRoleCd) throws DAOException {
		return facilityDAO().retrieveFacilityUserRoleActivities(facilityRoleCd);
	}

	@Override
	public List<FacilityPurgeSearchLineItem> retrieveFacilityListForPurging(Integer retentionPolicyActiveRecordMonths) throws DAOException {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MONTH, -retentionPolicyActiveRecordMonths);
		Timestamp purdgeDate = new Timestamp(cal.getTimeInMillis());
		return facilityDAO().retrieveFacilityListForPurging(purdgeDate);
	}
	
	@Override
	public List<FacilityPurgeLog> retrieveFacilityPurgeLogs() throws DAOException{
		return facilityDAO().retrieveFacilityPurgeLogs();
	}
	
	@Override
	public FacilityPurgeLog createFacilityPurgeLog(FacilityPurgeLog facilityPurgeLog) throws DAOException {
		return facilityDAO().createFacilityPurgeLog(facilityPurgeLog);
	}

	@Override
	public void deleteFacilityFieldAuditLog(String facilityId) throws DAOException {
		infrastructureDAO().deleteFacilityFieldAuditLogs(facilityId);
		
	}

	@Override
	public void deleteFacilityEventLogs(Integer fpId) throws DAOException {
		
		facilityDAO().deleteFacilityEventLogs(fpId);
		
	}

	
	//is used to delete facility inventory from internal
	@Override
	public void deleteFacilityInventory(Integer fpId) throws DAOException {
		
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ContinuousMonitorDAO continuousMonitorDAO = continuousMonitorDAO(trans);

		Facility currentFacility = retrieveFacilityProfile(fpId);

		for (ContinuousMonitor cm : currentFacility.getContinuousMonitorList()) {

			continuousMonitorDAO.removeContinuousMonitorEqtList(cm.getContinuousMonitorId());

			facilityDAO.removeFacilityCemComLimitList(cm.getContinuousMonitorId());

			// delete references to associated eus
			continuousMonitorDAO.deleteAssociatedFpEuIdsByMonitorId(cm.getContinuousMonitorId());

			// delete references to associated release points
			continuousMonitorDAO.deleteAssociatedFpEgressPointIdsByMonitorId(cm.getContinuousMonitorId());

			continuousMonitorDAO.deleteContinuousMonitor(cm);

		}

		Facility facility = facilityDAO.retrieveFacility(fpId);
		for (Address address : facility.getAddresses()) {
			infraDAO.removeAddressReference(facility.getFpId());
			infraDAO.removeAddress(address.getAddressId());
		}

		facilityDAO.removeFacilityNode(facility.getFpNodeId());
		//remove All Emission Unit, Emission Process, Control Equipment, and Release Point
		deleteFaciltySublayerNodes(fpId, null);
		//API
		deleteFacilityAllApis(fpId);
		//NAICS 
		facilityDAO.removeFacilityNAICSs(fpId);
		//delete Hydrocarbon Analysis Section
		facilityDAO.deleteFacExtendedHCAnalysisPollutant(facility.getFpId());
		facilityDAO.deleteFacHydrocarbonAnalysisSampleDetail(facility.getFpId());
		facilityDAO.deleteDecaneProperties(facility.getFpId());	
		
		//delete Rules and Regulations
		facilityDAO.removeFacilityMACTSubparts(facility.getFpId());
		facilityDAO.removeFacilityNeshapsSubparts(facility.getFpId());
		facilityDAO.removeFacilityNSPSSubparts(facility.getFpId());		
		

		
		facilityDAO.removeFacility(fpId);
		
	}
	
	@Override
	public void deleteFacility(String facilityId) throws DAOException{
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facilityDAO = facilityDAO(trans);
		
		//Facility roles
		facilityDAO.removeFacilityRoles(facilityId);
		facilityDAO.deleteFacilityCompanyRelationship(facilityId);
		//Facility Contacts
		facilityDAO.deleteFacilityContactRelationship(facilityId);

		deleteFacilitySubmissiontLogs(facilityId);
		facilityDAO.deleteFacilityNotes(facilityId);
		facilityDAO.deleteFacilityRUM(facilityId);
	}

	@Override
	public boolean deleteFacilityAttachments(Attachment attachment) throws DAOException {
		return facilityDAO().deleteFacilityAttachments(attachment.getFacilityID());

	}

	@Override
	public boolean deleteFacilitySubmissiontLogs(String facilityId) throws DAOException {
		return facilityDAO().deleteFacilitySubmissionLogs(facilityId);

	}


	
	
}
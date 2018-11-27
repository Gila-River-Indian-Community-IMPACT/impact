package us.oh.state.epa.stars2.bo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aspose.words.Cell;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.Row;
import com.aspose.words.Table;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.facility.PermitConditionSearchCriteria;
import us.oh.state.epa.stars2.bo.helpers.InfrastructureHelper;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dao.WorkFlowDAO;
import us.oh.state.epa.stars2.database.dao.permit.PermitSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.permit.EmissionsOffset;
import us.oh.state.epa.stars2.database.dbObjects.permit.NSRFixedCharge;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitActivitySearch;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCC;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitChargePayment;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSupersession;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitNote;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitWorkflowSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.permit.RPRPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.FacPermitStatusDef;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.NSRBillingBillableRateDef;
import us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef;
import us.oh.state.epa.stars2.def.NSRBillingFeeTypeDef;
import us.oh.state.epa.stars2.def.NSRBillingStandardFeesDef;
import us.oh.state.epa.stars2.def.OffsetTrackingContributingPollutantDef;
import us.oh.state.epa.stars2.def.OffsetTrackingDefaultMultiplierDef;
import us.oh.state.epa.stars2.def.OffsetTrackingNonAttainmentAreaDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitDocIssuanceStageDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PermitWorkflowActivityBenchmarkDef;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.fileConverter.FileConverter;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.InvoiceGenerationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.def.PermitConditionStatusDef;
import us.wy.state.deq.impact.def.PermitConditionSupersedenceStatusDef;
import us.wy.state.deq.impact.def.PermitLevelStatusDef;

@Transactional(rollbackFor = Exception.class)
@Service
public class PermitBO extends BaseBO implements PermitService {

	@Autowired
	InfrastructureHelper infraHelper;
	@Autowired
	PermitConditionService permitConditionService;
	

	public PermitConditionService getPermitConditionService() {
		return permitConditionService;
	}

	public void setPermitConditionService(PermitConditionService permitConditionService) {
		this.permitConditionService = permitConditionService;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String generatePermitNumber() throws DAOException {
		StringBuffer seqNoStr = new StringBuffer();
		seqNoStr.append(permitDAO().generatePermitSeqNo());
		while (seqNoStr.length() < 7) {
			seqNoStr.insert(0, "0");
		}
		return "P" + seqNoStr;
	}

	public String generatePermitNumber(PermitDAO permitDAO) throws DAOException {
		StringBuffer seqNoStr = new StringBuffer();
		seqNoStr.append(permitDAO.generatePermitSeqNo());
		while (seqNoStr.length() < 7) {
			seqNoStr.insert(0, "0");
		}
		return "P" + seqNoStr;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PermitDocument uploadTempDocument(PermitDocument doc, InputStream is)
			throws DAOException {
		if (doc == null || !doc.isValid()) {
			throw new DAOException("PermitDocument is not valid.");
		}

		try {
			doc.setTemporary(true);
			if (doc.getLastModifiedTS() == null) {
				doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
			}
			doc.setUploadDate(doc.getLastModifiedTS());
			documentDAO().createDocument(doc);
			DocumentUtil.createDocument(doc.getPath(), is);
		} catch (IOException ioe) {
			try {
				DocumentUtil.removeDocument(doc.getPath());
			} catch (IOException ioex) {
			}
			throw new DAOException("Unable to create document.", ioe);
		}

		return doc;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PermitDocument uploadDocument(PermitDocument doc, InputStream is,
			Transaction trans) throws DAOException {
		if (doc == null || !doc.isValid()) {
			throw new DAOException("PermitDocument is not valid.");
		}

		try {
			doc.setTemporary(false);
			if (doc.getLastModifiedTS() == null) {
				doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
			}
			doc.setUploadDate(doc.getLastModifiedTS());
			documentDAO(trans).createDocument(doc);
			DocumentUtil.createDocument(doc.getPath(), is);
		} catch (IOException ioe) {
			try {
				DocumentUtil.removeDocument(doc.getPath());
			} catch (IOException ioex) {
			}
			throw new DAOException("Unable to create document.", ioe);
		}

		return doc;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitDocument cloneTempDocument(PermitDocument docToClone,
			PermitDocument doc) throws DAOException {
		if (doc == null || !doc.isValid()) {
			throw new DAOException("PermitDocument is not valid.");
		}

		if (docToClone == null || !docToClone.isValid()) {
			throw new DAOException("PermitDocument to clone is not valid.");
		}

		try {
			doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
			doc.setUploadDate(doc.getLastModifiedTS());
			doc.setTemporary(true);
			documentDAO().createDocument(doc);
			DocumentUtil.copyDocument(docToClone.getPath(), doc.getPath());
		} catch (IOException ioe) {
			try {
				DocumentUtil.removeDocument(doc.getPath());
			} catch (IOException ioex) {
			}
			throw new DAOException("Unable to clone document.", ioe);
		}

		return doc;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public PermitDocument generateTempDocument(Permit permit,
			TemplateDocument templateDoc, PermitDocument doc)
			throws DAOException {

		String facilityID = permit.getFacilityId();
		Facility facility = facilityDAO().retrieveFacility(facilityID);
		
		if (templateDoc.getTemplateDocTypeCD().equals(
				TemplateDocTypeDef.PTI_T_AND_C)
				|| templateDoc.getTemplateDocTypeCD().equals(
						TemplateDocTypeDef.PTIO_T_AND_C)
				// || templateDoc.getTemplateDocTypeCD().equals(
				// TemplateDocTypeDef.TIVPTO_T_AND_C)
				|| templateDoc.getTemplateDocTypeCD().equals(
						TemplateDocTypeDef.PTI_PID)
						|| templateDoc.getTemplateDocTypeCD().equals(
								TemplateDocTypeDef.PTI_CL)
							|| templateDoc.getTemplateDocTypeCD().equals(
							TemplateDocTypeDef.PTI_PND)
							|| templateDoc.getTemplateDocTypeCD().equals(
							TemplateDocTypeDef.PTI_RL)
							|| templateDoc.getTemplateDocTypeCD().equals(
							TemplateDocTypeDef.PTI_CNL)) {
			

			// Check the EUs for AQD description and company equipment ID.
			boolean hasEUs = false;
			boolean missingStuffA = false;
			boolean missingStuffB = false;
			String missingErrorA = "One or more emissions units are missing "
					+ "a AQD Description and/or a Company ID: ";
			String missingErrorB = "One or more emissions units has a tempoary "
					+ "AQD Emissions Unit ID: ";
			String missingEUsA = "";
			String missingEUsB = "";
			String missingError = null;
			for (PermitEUGroup eug : permit.getEuGroups()) {
				if (permit instanceof TVPermit && eug.isInsignificantEuGroup()) {
					if (!eug.getPermitEUs().isEmpty()) {
						hasEUs = true;
					}
					continue;
				}
				for (PermitEU peu : eug.getPermitEUs()) {
					hasEUs = true;
					if (peu.getDapcDescription() == null
							|| peu.getDapcDescription().length() == 0
							|| peu.getCompanyId() == null
							|| peu.getCompanyId().length() == 0) {
						if (missingEUsA.length() == 0) {
							missingEUsA += peu.getFpEU().getEpaEmuId();
						} else {
							missingEUsA += ", " + peu.getFpEU().getEpaEmuId();
						}
						missingStuffA = true;
					}
					if (peu.getFpEU().getEpaEmuId().startsWith("TMP")
							|| peu.getFpEU().getEpaEmuId().startsWith("Z")) {
						if (missingEUsB.length() == 0) {
							missingEUsB += peu.getFpEU().getEpaEmuId();
						} else {
							missingEUsB += ", " + peu.getFpEU().getEpaEmuId();
						}
						missingStuffB = true;
					}
				}
			}
			/*if (!hasEUs) {
				throw new DAOException(
						"The permit has no emissions units. "
								+ "document cannot be generated.");
			}
			if (missingStuffA) {
				missingError = missingErrorA + missingEUsA + ".";
			}
			if (missingStuffB) {
				if (missingError == null) {
					missingError = missingErrorB + missingEUsB
							+ ". Go to the Permit Detail page "
							+ "and push the \"Sync EUs\" button.";
				} else {
					missingError = " " + missingErrorB + missingEUsB
							+ ". Go to the Permit Detail page "
							+ "and push the \"Sync EUs\" button.";
				}
			}*/
			//if (missingError != null) {
			//	throw new DAOException(missingError);
			//}

		}

		// Set the path elements of the generated doc.
		//doc.setFacilityID(permit.getFacilityId());
		//doc.setTemporary(true);
		
		//doc.setExtension(DocumentUtil.getFileExtension(templateDoc.getPath()));
		//doc.setPermitId(permit.getPermitID());
		

		try {
			//doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
			//doc.setUploadDate(doc.getLastModifiedTS());
			//doc = (PermitDocument) documentDAO().createDocument(doc);
			//doc.setExtension("docx");  //Task 5525
			DocumentGenerationBean dgb = new DocumentGenerationBean();
			dgb.setPermit(permit);
			dgb.setFacility(facility);
			
			Timestamp tempTime=null; //new Timestamp(Calendar.getInstance().getTime().getTime());
			Application tempApp=null;
			
		   List<Application> apps = permit.getApplications();
		   	for(Application permitApps : apps) {				
		   		Timestamp appSubmitTime= permitApps.getSubmittedDate();
		   		//System.out.println("tempTime: "+tempTime);
		   		//System.out.println("appSubmitTime: "+appSubmitTime);
		   		if(tempTime==null){
		   			tempTime=appSubmitTime;
		   			tempApp=permitApps;
		   		}else if(appSubmitTime.after(tempTime)){
		   			tempTime=appSubmitTime;
		   			tempApp=permitApps;
		   		}
		   		
		   		//System.out.println("tempApp.getApplicationID(): "+tempApp.getApplicationID());
			}
		   	if(tempApp!=null){
		   		dgb.setApplication(tempApp);
		   	}

			//DocumentUtil.generateDocument(templateDoc.getPath(), dgb,
				//	doc.getPath());
			String desFilePath=null;
			if(!doc.getPath().endsWith(".docx")){
				//desFilePath = doc.getPath() + templateDoc.getTemplateDocTypeDsc() +"."+ doc.getExtension(); //Task 5525
				desFilePath = doc.getPath() + templateDoc.getTemplateDocTypeDsc() +"."+ "docx";
			}else{
				//desFilePath = doc.getPath().substring(0,doc.getPath().lastIndexOf("\\")+1) + templateDoc.getTemplateDocTypeDsc() +"."+ doc.getExtension();  //Task 5525
				desFilePath = doc.getPath().substring(0,doc.getPath().lastIndexOf("\\")+1) + templateDoc.getTemplateDocTypeDsc() +"."+ "docx";
			}
			String docURL = DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dgb, desFilePath);	
			
			doc.setGeneratedDocumentPath(docURL);
			

		} catch (IOException iex) {
			String error = "Caught an IOException. Message = "
					+ iex.getMessage();
			logger.error(error, iex);
			throw new DAOException(error, iex);
		} catch (DocumentGenerationException dex) {
			String error = "Caught a DocumentGenerationException. Message = "
					+ dex.getMessage();
			logger.error(error, dex);
			throw new DAOException(error, dex);
		} catch (Exception e) {
			String error = "Caught an Exception. Message = " + e.getMessage();
			logger.error(error, e);
			throw new DAOException(error, e);
		}

		return doc;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<Permit> search(String applicationNumber, String euId,
			String cmpId, String permitType, String permitReason, String permitLevelStatusCd,
			String legacyPermitNumber, String permitNumber, String facilityID, String facilityName,
			String permitStatusCd, String dateBy, Timestamp beginDate,
			Timestamp endDate, String permitEUStatusCd, boolean unlimitedResults, String permitFeeBalanceTypeCd)
			throws DAOException {

		return permitDAO().searchPermits(applicationNumber, euId, cmpId,
				permitType, permitReason, permitLevelStatusCd, legacyPermitNumber, permitNumber, facilityID,
				facilityName, permitStatusCd, dateBy, beginDate, endDate,
				permitEUStatusCd, unlimitedResults, permitFeeBalanceTypeCd);
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	@Override
	public void markInactiveNSRPermitsToExpired() throws DAOException {
		permitDAO().markInactiveNSRPermitsToExpired();
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<PTIOPermit> searchPermitsForFinalInvoice(String cmpId,
			String facilityID, String facilityName, boolean unlimitedResults)
			throws DAOException {

		return permitDAO().searchPermitsForFinalInvoice(cmpId, facilityID,
				facilityName, unlimitedResults);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<Permit> searchPERs(String facilityID, Timestamp beginDate,
			Timestamp endDate) throws DAOException {

		List<Permit> sList = search(null, null, null, PermitTypeDef.NSR, null, null,
				null, null, facilityID, null, null, "effective_date", null, endDate,
				null, true, null);

		Facility facility = facilityDAO().retrieveFacility(facilityID);

		ArrayList<Permit> fullList = new ArrayList<Permit>();
		for (Permit permit : sList) {
			PermitInfo pi = retrievePermit(permit.getPermitID());
			permit = pi.getPermit();

			boolean hasValidEUs = false;
			ArrayList<PermitEUGroup> validEUGroups = new ArrayList<PermitEUGroup>();

			for (PermitEUGroup euGroup : permit.getEuGroups()) {

				boolean isValidEUGroup = false;
				ArrayList<PermitEU> validEUs = new ArrayList<PermitEU>();

				for (PermitEU peu : euGroup.getPermitEUs()) {

					// ...fetch the facility emission unit.
					EmissionUnit fpEu = peu.getFpEU();
					if (fpEu == null) {
						// ERROR!!!
						continue;
					}
					fpEu = facility.getMatchingEmissionUnit(fpEu
							.getCorrEpaEmuId());
					if (fpEu == null) {
						// ERROR!!!
						continue;
					}

					// Ignore EU if revoked, terminated, superseded, or shutdown
					// before start date of PER.
					if (beginDate != null) {
						Timestamp revokedDate = peu.getRevocationDate();
						if (revokedDate != null
								&& revokedDate.compareTo(beginDate) < 0) {
							continue;
						}
						Timestamp termDate = peu.getTerminatedDate();
						if (termDate != null
								&& termDate.compareTo(beginDate) < 0) {
							continue;
						}
						Timestamp supersedeDate = peu.getSupersededDate();
						if (supersedeDate != null
								&& supersedeDate.compareTo(beginDate) < 0) {
							continue;
						}
					}

					if (fpEu.getEpaEmuId() != null
							&& fpEu.getEpaEmuId().length() > 0) {
						Timestamp shutdownDate = fpEu.getEuShutdownDate();
						if (shutdownDate != null && beginDate != null
								&& shutdownDate.compareTo(beginDate) < 0) {
							continue;
						}
					}

					isValidEUGroup = true;
					hasValidEUs = true;
					peu.setPermitNumber(permit.getPermitNumber());
					peu.setEffectiveDate(permit.getEffectiveDate());
					validEUs.add(peu);

				}

				if (isValidEUGroup) {
					euGroup.setPermitEUs(validEUs);
					validEUGroups.add(euGroup);
				}

			}

			if (hasValidEUs) {
				permit.setEuGroups(validEUGroups);
				fullList.add(permit);
			}

		} // END: for (Permit permit : sList)

		return fullList;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitInfo retrievePermit(int permitID) throws DAOException {

		try {
			PermitDAO permitDAO = permitDAO();
			ApplicationService appBO = ServiceFactory.getInstance()
					.getApplicationService();

			FacilityService facBO = ServiceFactory.getInstance()
					.getFacilityService();

			PermitInfo ret = new PermitInfo();
			Permit p = permitDAO.retrievePermit(permitID);
			permitID = p.getPermitID();
			p.setPermitIssuances(permitDAO.retrievePermitIssuances(permitID));
			p.setDapcComments(permitDAO.retrieveDapcComments(permitID));
			p.setContacts(permitDAO.retrievePermitContacts(permitID));
			p.setInvoice(invoiceDAO().retrieveInvoiceByPermitID(permitID));
			p.setPermitCCList(permitDAO.retrievePermitCCList(permitID));
			
			ret.setCurrentFacility(facilityDAO().retrieveFacility(
					p.getFacilityId()));
			p.setFacilityNm(ret.getCurrentFacility().getName());
			p.setFpId(ret.getCurrentFacility().getFpId());
			ret.setPermit(p);
			if (!p.getApplications().isEmpty()) {
				List<Application> apps = p.getApplications();
				List<Application> tas = new ArrayList<Application>();
				// Here to retrieve the complete application.
				for (Application a : apps) {
					a = appBO.retrieveApplication(a.getApplicationNumber());
					tas.add(a);
				}
				p.setApplications(tas);
			}
			mergePermittableEUs(null, ret);

			if (p instanceof PTIOPermit) {
				((PTIOPermit) p).createEUFees();
				if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
					
					TimeSheetRow[] tsRows = this.retrieveTimeSheetHours(p);
					if(null != tsRows) {
						((PTIOPermit) p).setNSRTimeSheetRowList(Arrays.asList(tsRows));
					}

				}
				// } else if (p instanceof RPEPermit) {
				// String maxRPEFee = getParameter("MaxRPEFee");
				// ((RPEPermit) p).createEUFees(maxRPEFee);
			}
			
			return ret;
		} catch (Exception e) {
			throw new DAOException("Could not retrieve permit. "
					+ e.getMessage(), e);
		}
	}
	
	public Permit retrievePermitLight(int permitID) throws DAOException {

		try {
			PermitDAO permitDAO = permitDAO();
			
			Permit p = permitDAO.retrievePermit(permitID);
			
			return p;
		} catch (Exception e) {
			throw new DAOException("Could not retrieve permit. "
					+ e.getMessage(), e);
		}
	}

	/**
	 * Retrieves all active permits for a given EU.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<Permit> searchEuPermits(Integer corrEuId) throws DAOException {
		return permitDAO().searchEuPermits(corrEuId);
	}

	/**
	 * Retrieves all permits for a given EU.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<Permit> searchAllEuPermits(Integer corrEuId)
			throws DAOException {
		return permitDAO().searchAllEuPermits(corrEuId);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Permit retrievePermit(String permitNumber) throws DAOException {

		try {

			Permit p = permitDAO().retrievePermit(permitNumber);

			FacilityService facBO = ServiceFactory.getInstance()
					.getFacilityService();

			if (p instanceof PTIOPermit) {
				((PTIOPermit) p).createEUFees();
			//} else if (p instanceof RPEPermit) {
			//	String maxRPEFee = getParameter("MaxRPEFee");
			//	((RPEPermit) p).createEUFees(maxRPEFee);
			}

			return p;
		} catch (Exception e) {
			throw new DAOException("Cannot retrieve Permit Number "
					+ permitNumber, e);
		}

	}

	/**
	 * This won't create permit document in the permit object.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PermitInfo createPermit(Permit permit, int userId)
			throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		Permit ret = null;
		try {
			ret = createPermit(permit, userId, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
			throw de;
		} finally {
			closeTransaction(trans);
		}

		return retrievePermit(ret.getPermitID());
	}

	/**
	 * This won't create permit document in the permit object. reasonCDs,
	 * SupersededPermits, Applications, EuGroups
	 * 
	 * @param permit
	 * @param trans
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Permit createPermit(Permit permit, int userId, Transaction trans)
			throws DAOException {

		PermitDAO permitDAO = permitDAO(trans);

		logger.debug(" Generating permit number");
		if (permit.getPermitNumber() == null) {
			permit.setPermitNumber(generatePermitNumber(permitDAO));
		}
		List<Application> apps = permit.getApplications();

		try {
			// 2.3.3-PTI_PTO_PTIO-1-18
			if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
				// bug 1816 to find if the apps has RAPM request, not set permit
				// description with facility inventory description.
				boolean isRapm = false;
				for (Application ta : apps)
					if (ta instanceof RPCRequest)
						isRapm = true;

				if (!isRapm) {
					logger.debug(" Retrieving facility for permit description");
					Facility fp = facilityDAO(trans).retrieveFacility(
							permit.getFacilityId());
					if(!permit.isLegacyPermit())
						permit.setDescription(fp.getDesc());
				}

			}
		} catch (Exception e) {
			throw new DAOException("Could not retrieve facility. "
					+ e.getMessage(), e);
		}

		logger.debug(" Creating permit object");
		Permit ret = permitDAO.createPermit(permit);

		// Create mact, neshaps, and nsps subpart codes.
		logger.debug(" Creating subparts");
		permitDAO.createSubpartCDs(ret);

		int permitId = ret.getPermitID();

		// Create Draft and Final Issuances
		String key = PermitIssuanceTypeDef.Draft;
		PermitIssuance pi = permit.getPermitIssuances().get(key);
		if(pi == null) {
			pi = new PermitIssuance();
			pi.setIssuanceTypeCd(PermitIssuanceTypeDef.Draft);
			pi.setIssuanceStatusCd(IssuanceStatusDef.NOT_READY);
		}
		if (pi != null) {
			pi.setPermitId(permitId);
			ret.getPermitIssuances().put(key,
					permitDAO.createPermitIssuance(pi));
		} else if (permit.getPermitReasonCDs().size() == 0) {
			// Note that the NOT_ASSIGNED reason is assigned for initial shell
			// permits (i.e. permits created by applications w/out issuances.
			// NOT_ASSIGNED is not a valid reason otherwise.
			ArrayList<String> reasonCds = new ArrayList<String>();
			reasonCds.add(PermitReasonsDef.NOT_ASSIGNED);
			permit.setPermitReasonCDs(reasonCds);
		}

		key = PermitIssuanceTypeDef.Final;
		pi = permit.getPermitIssuances().get(key);
		if(pi == null) {
			pi = new PermitIssuance();
			pi.setIssuanceTypeCd(PermitIssuanceTypeDef.Final);
			pi.setIssuanceStatusCd(IssuanceStatusDef.NOT_READY);
		}
		if (pi != null) {
			pi.setPermitId(permitId);
			ret.getPermitIssuances().put(key,
					permitDAO.createPermitIssuance(pi));
		}

		logger.debug(" Adding permit reasons");
		permitDAO.addPermitReasons(permitId, permit.getPermitReasonCDs());

		logger.debug(" Creating NSR permit");
		// Create the specific type of permit and their reason codes.
		if (permit.getClass() == PTIOPermit.class) {
			ret = permitDAO.createPTIOPermit(permit);
		}
		// else if (permit.getClass() == RegPermit.class) {
		// ret = permitDAO.createRegPermit(permit);
		// }
		else if (PermitTypeDef.TV_PTO.equals(permit.getPermitType())
				//|| PermitTypeDef.TIV_PTO.equals(permit.getPermitType())
				) {
			ret = permitDAO.createTVPermit(permit);
			// Create PP and PPP Issuances
			key = PermitIssuanceTypeDef.PP;
			pi = permit.getPermitIssuances().get(key);
			if(pi == null) {
				pi = new PermitIssuance();
				pi.setIssuanceTypeCd(PermitIssuanceTypeDef.PP);
				pi.setIssuanceStatusCd(IssuanceStatusDef.NOT_READY);
			}
			if (pi != null) {
				pi.setPermitId(permitId);
				ret.getPermitIssuances().put(key,
						permitDAO.createPermitIssuance(pi));
			}
			/*
			key = PermitIssuanceTypeDef.PPP;
			pi = permit.getPermitIssuances().get(key);
			if (pi != null) {
				pi.setPermitId(permitId);
				ret.getPermitIssuances().put(key,
						permitDAO.createPermitIssuance(pi));
			}
			*/
		}

		// Create any application xrefs associated with this permit.
		permitDAO.addPermitApplications(permitId, apps);

		// Make sure we have an individual EUGroup for EUs not associated
		// with a group.
		if (permit.getIndividualEuGroup() == null) {
			PermitEUGroup individualGroup = new PermitEUGroup();
			individualGroup.setIndividualEUGroup();
			individualGroup.setPermitID(permit.getPermitID());
			individualGroup.setPermitEUs(new ArrayList<PermitEU>());
			permit.addEuGroup(individualGroup);
		}

		// Create any EU Groups, along with EUs.
		for (PermitEUGroup permitEUGroup : permit.getEuGroups()) {
			logger.debug(" Creating permit EU groups (specific EUs)");
			permitEUGroup.setPermitID(permitId);
			permitEUGroup = permitDAO.createPermitEUGroup(permitEUGroup);

		}

		// Create any PermitCC list entries.
		for (PermitCC cc : permit.getPermitCCList()) {
			cc.setPermitId(permitId);
			permitDAO.createPermitCC(cc);
		}
		
		if (permit instanceof PTIOPermit) {
			PTIOPermit ptioPermit = (PTIOPermit) permit;
			if (!ptioPermit.isLegacyPermit()) {
				// NSR Billing
				ptioPermit.setNSRFixedChargeList(retrieveFixedCharges(permit));
				// Create any Fixed Charges list entries.
				for (NSRFixedCharge fc : ptioPermit.getNSRFixedChargeList())
				{
				   fc.setPermitId(permitId);
				   permitDAO.createNSRFixedCharge(fc);
				}
	
				// Create any PermitChargePayment list entries.
				for (PermitChargePayment cp : ptioPermit.getPermitChargePaymentList()) {
					cp.setPermitId(permitId);
					permitDAO.createPermitChargePayment(cp);
				}
			
				createOffsetTrackingEntries(ptioPermit);
			}
			
		}
		
		// Create any Permit Condition list entries.
		for (PermitCondition pc : permit.getPermitConditionList())
		{
		   pc.setPermitId(permitId);
		   permitConditionDAO().createPermitCondition(pc);
		}

		// Create the permit's file system subdirectory.
		try {
			createPermitDir(ret);
		} catch (IOException ioe) {
			logger.error(
					"Permit directory creation problem: " + ioe.getMessage(),
					ioe);
			throw new DAOException(ioe.getMessage(), ioe);
		}

		// TODO disable multimedia letter for wyoming
		// String pType = permit.getPermitType();
		// if (!permit.isLegacyPermit()
		// && (pType.equals(PermitTypeDef.TVPTI) ||
		// pType.equals(PermitTypeDef.PTIO) ||
		// pType.equals(PermitTypeDef.TV_PTO) ||
		// pType.equals(PermitTypeDef.TIV_PTO))) {
		//
		// try {
		// prepareMultiMediaLetter(ret, userId, trans);
		// } catch (Exception e) {
		// // Need to rollback creation of the directory.
		// String message = "MultiMedia letter problem: " + e.getMessage();
		// try {
		// removePermitDir(ret);
		// } catch (IOException ioe) {
		// message += ", Directory removal problem: "
		// + ioe.getMessage();
		// }
		// logger.error(message, e);
		// throw new DAOException(message, e);
		// }
		// }

		return ret;

	}

	private void createOffsetTrackingEntries(PTIOPermit permit) 
			throws DAOException {
		String facilityId = permit.getFacilityId();
		Integer fpId = facilityDAO().retrieveFpIdByFacilityId(facilityId);
		List<OffsetTrackingNonAttainmentAreaDef> nonattainmentAreas =
				getFacilityNonattainmentAreas(fpId);
		if (nonattainmentAreas.size() > 0) {
			for (OffsetTrackingNonAttainmentAreaDef area : nonattainmentAreas) {
				OffsetTrackingContributingPollutantDef[] pollutants = 
						infrastructureDAO().findContributingPollutantsByNonattainmentArea(area.getCode());
				for (OffsetTrackingContributingPollutantDef pollutant : pollutants) {
					OffsetTrackingDefaultMultiplierDef defaultMultiplier =
							getDefaultMultiplier(area, pollutant);
					Double multiplier = 
							null != defaultMultiplier? 
									(double)defaultMultiplier.getMultiplier() : 1.0d;
					EmissionsOffset offset = new EmissionsOffset();
					offset.setPermitId(permit.getPermitID());
					offset.setNonAttainmentAreaCd(area.getCode());
					offset.setAttainmentStandardCd(area.getAttainmentStandardCd());
					offset.setPollutantCd(pollutant.getPollutantCd());
					offset.setOffsetMultiple(multiplier);
					permitDAO().createPermitEmissionsOffset(offset);
				}
			}
		}
	}

	private OffsetTrackingDefaultMultiplierDef getDefaultMultiplier(
			OffsetTrackingNonAttainmentAreaDef area, 
			OffsetTrackingContributingPollutantDef pollutant) {

		OffsetTrackingDefaultMultiplierDef defaultMultiplier = null;
		
		List<OffsetTrackingDefaultMultiplierDef> multipliers = 
				OffsetTrackingDefaultMultiplierDef.getDefListItems();
		
		Map<Timestamp,OffsetTrackingDefaultMultiplierDef> defaultMultipliers =
				new HashMap<Timestamp,OffsetTrackingDefaultMultiplierDef>();
		
		for (OffsetTrackingDefaultMultiplierDef multiplier : multipliers) {
			if (area.getCode().equals(multiplier.getAreaCd()) &&
					pollutant.getPollutantCd().equals(multiplier.getPollutantCd())) {
				defaultMultipliers.put(multiplier.getEffectiveDate(),multiplier);
			}
		}
		if (!defaultMultipliers.isEmpty()) {
			Timestamp latest = null;
			for (Timestamp ts : defaultMultipliers.keySet()) {
				if (null == latest || ts.after(latest)) {
					latest = ts;
				}
			}
			defaultMultiplier = defaultMultipliers.get(latest);
		}
		return defaultMultiplier;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyPermit(Permit permit, int userId) throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();

		try {
			modifyPermit(permit, userId, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return true;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyPermitDocuments(Permit permit) throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();

		try {
			PermitDAO permitDAO = permitDAO(trans);
			DocumentDAO documentDAO = documentDAO(trans);

			Integer pID = permit.getPermitID();
			permitDAO.markTempPermitDocuments(pID);
			permitDAO.removePermitDocuments(pID);
			//System.out.println("permit.getDocuments():" +permit.getDocuments().size());
			for (PermitDocument doc : permit.getDocuments()) {
				//System.out.println("permit.getFacilityId(): "+permit.getFacilityId());
				doc.setFacilityID(permit.getFacilityId());
				doc.setTemporary(false);
				if (doc.getDocumentID() != null) {
					documentDAO.modifyDocument(doc);
				} else {
					doc = (PermitDocument) documentDAO.createDocument(doc);
				}
				doc.setPermitId(pID);
				permitDAO.createPermitDocument(doc);

				// if this is an updated issuance doc and the permit has already
				// been issued,
				// we need to generate a PDF version of the document
				if (PermitDocTypeDef.ISSUANCE_DOCUMENT.equals(doc
						.getPermitDocTypeCD())
						&& ((PermitGlobalStatusDef.ISSUED_DRAFT.equals(permit
								.getPermitGlobalStatusCD()) && "D".equals(doc
								.getIssuanceStageFlag()))
								|| (PermitGlobalStatusDef.ISSUED_FINAL
										.equals(permit.getPermitGlobalStatusCD()) && "F"
										.equals(doc.getIssuanceStageFlag()))
								|| (PermitGlobalStatusDef.ISSUED_PP.equals(permit
										.getPermitGlobalStatusCD()) && "P"
										.equals(doc.getIssuanceStageFlag())) 
								//|| (PermitGlobalStatusDef.ISSUED_PPP
								//.equals(permit.getPermitGlobalStatusCD()) && "2"
								//.equals(doc.getIssuanceStageFlag()))
								)) {
					try {
						generatePDFIssuanceDoc(doc);
					} catch (IOException e) {
						logger.error(
								"Exception while attempting to generate PDF version of document "
										+ doc.getDocumentID(), e);
					}
				}
			}

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return true;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	/*
	public boolean convertToPTI(Permit permit, int userId) throws DAOException {

		FacilityService facBO = null;
		try {
			facBO = ServiceFactory.getInstance().getFacilityService();
		} catch (ServiceFactoryException e) {
			throw new DAOException(
					"Cannot convertToPTI Permit Number (FacilityService problem)"
							+ permit.getPermitNumber(), e);
		}

		Transaction trans = TransactionFactory.createTransaction();

		try {
			if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
				PTIOPermit tp = (PTIOPermit) permit;
				tp.setTv(true);
				tp.setFeptio(false);
				tp.setFePtioTvAvoid(false);
				tp.setConvertedToPTI(true);
				tp.setConvertedToPTIDate(new Timestamp(System
						.currentTimeMillis()));

				//
				// * Mantis 154 : For each 'applicable' EU in the PTIO permit,
				// * update the corresponding EU's PTI status to 'Active' in the
				// * facility inventory. The term 'applicable' in this context means
				// * the permit EU status is any value other than 'Revoked' or
				// * 'Terminated'.
				//

				// Fetch the latest version of the facility inventory.
				Facility facility = getEditableCurrent(facBO,
						permit.getFacilityId(), userId);
				if (facility == null) {
					throw new NullPointerException(
							"Could not locate facility for " + "Facility ID = "
									+ permit.getFacilityId()
									+ ", FacilityID = "
									+ permit.getFacilityId());
				}

				for (PermitEU peu : permit.getEus()) {
					if (PermitStatusDef.REVOKED.equalsIgnoreCase(peu
							.getPermitStatusCd())
							|| PermitStatusDef.TERMINATED.equalsIgnoreCase(peu
									.getPermitStatusCd())) {
						continue;
					}

					EmissionUnit eu = facility.getMatchingEmissionUnit(peu
							.getCorrEpaEMUID());
					if (eu == null) {
						throw new DAOException(
								"Could not locate Emission Unit for "
										+ "EU ID = "
										+ peu.getFpEU().getEpaEmuId());
					}

					eu.setPtiStatusCd(PTIRegulatoryStatus.ACTIVE);

					//
					// * Mantis 154 note : The EU facility inventory PTI status
					// * should become "Active PTI".
					// * 
					// * The EU FP Permit Status only change if the current status
					// * of that field is relating to a PTIO (Active PTIO,
					// * Extended PTIO, Expired PTIO, Active FEPTIO, Extended
					// * FEPTIO, Expired FEPTIO). IF the value is one of those 6,
					// * then change it to "None".
					//
					if (PTIORegulatoryStatus.ACTIVE_PTIO.equalsIgnoreCase(eu
							.getPtioStatusCd())
							|| PTIORegulatoryStatus.EXPIRED_PTIO
									.equalsIgnoreCase(eu.getPtioStatusCd())
							|| PTIORegulatoryStatus.EXTENDED_PTIO
									.equalsIgnoreCase(eu.getPtioStatusCd())
							|| PTIORegulatoryStatus.ACTIVE_FEPTIO
									.equalsIgnoreCase(eu.getPtioStatusCd())
							|| PTIORegulatoryStatus.EXTENDED_FEPTIO
									.equalsIgnoreCase(eu.getPtioStatusCd())
							|| PTIORegulatoryStatus.EXPIRED_FEPTIO
									.equalsIgnoreCase(eu.getPtioStatusCd())) {
						eu.setPtioStatusCd(PTIORegulatoryStatus.NONE);
					}

					facBO.modifyEmissionUnit(eu, userId, trans);
				}

				modifyPermit(permit, userId, trans);
				trans.complete();
			}

		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}
		return true;
	}
	*/

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removePermit(Permit permit) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);

		try {
			Integer permitId = permit.getPermitID();

			permitDAO.markTempPermitDocuments(permitId);
			permitDAO.removePermitDocuments(permitId);

			permitDAO.removePermitApplications(permitId);

			permitDAO.removePermitCCList(permitId);
			
			if (permit.getClass() == PTIOPermit.class) {
				
				permitDAO.removeNSRFixedChargeList(permitId);
				permitDAO.removePermitChargePaymentList(permitId);
			}
			
			removePermitConditionList(permit);

			// Delete the specific type of permit and it's reason codes...
			if (permit.getClass() == PTIOPermit.class) {
				permitDAO.removePTIOPermit(permitId);
			} else {
				permitDAO.removeTVPermit(permitId);
			}

			permitDAO.removePermit(permitId);

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	private void removePermitConditionList(Permit permit) throws DAOException {
		for (PermitCondition pc : permit.getPermitConditionList()){
			permitConditionDAO().removeComplianceStatusEventList(pc.getPermitConditionId());
		}
		permitConditionDAO().removePermitConditionList(permit.getPermitID());
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitEUGroup retrieveEUGroup(int euGroupID) throws DAOException {
		return permitDAO().retrievePermitEUGroup(euGroupID);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PermitEUGroup createEUGroup(PermitEUGroup euGroup)
			throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		PermitEUGroup ret = null;

		try {
			ret = createEUGroup(euGroup, trans);
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
	public PermitEUGroup createEUGroup(PermitEUGroup euGroup, Transaction trans)
			throws DAOException {
		PermitDAO permitDAO = permitDAO(trans);
		return permitDAO.createPermitEUGroup(euGroup);
	}

	private PermitEU createPermitEU(PermitEU permitEu, String permitType,
			PermitDAO permitDAO) throws DAOException {

		if (permitEu.getSupersededPermitID() == null) {
			SimpleIdDef[] sps = retrieveSupersedablePermits(
					permitEu.getCorrEpaEMUID(), permitType);
			if (sps.length != 0)
				permitEu.setSupersededPermitID(sps[0].getId());
		}

		return permitDAO.createPermitEU(permitEu);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyEUGroup(PermitEUGroup euGroup) throws DAOException {

		if (euGroup != null) {

			Transaction trans = TransactionFactory.createTransaction();
			PermitDAO permitDAO = permitDAO(trans);

			try {
				permitDAO.modifyPermitEUGroup(euGroup);
				trans.complete();
			} catch (DAOException de) {
				cancelTransaction(trans, de);
			} finally {
				closeTransaction(trans);
			}
		}

		return true;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeEUGroup(PermitEUGroup euGroup) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);

		try {
			permitDAO.removePermitEUGroup(euGroup);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitEU retrievePermitEU(int permitEUID) throws DAOException {
		return permitDAO().retrievePermitEU(permitEUID);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PermitEU createEU(PermitEU eu, String permitType)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);
		PermitEU ret = null;

		try {
			ret = createPermitEU(eu, permitType, permitDAO);
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
	public boolean modifyEU(PermitEU eu, int userId) throws DAOException {

		if (eu != null) {

			Transaction trans = TransactionFactory.createTransaction();
			PermitDAO permitDAO = permitDAO(trans);
			InfrastructureDAO infraDAO = infrastructureDAO(trans);

			try {
				permitDAO.modifyPermitEU(eu);
				// eu = permitDAO.retrievePermitEU(eu.getPermitEUID());

				ArrayList<String> facilityData = permitDAO
						.retrieveFacilityNameForEU(eu);

				String facilityId = facilityData.get(0);
				String facilityName = facilityData.get(1);

				infraDAO.createFieldAuditLogs(facilityId, facilityName, userId,
						eu.getFieldAuditLogs());

				trans.complete();
			} catch (DAOException de) {
				cancelTransaction(trans, de);
			} finally {
				closeTransaction(trans);
			}
		}

		return true;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeEU(PermitEU eu) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);

		try {
			int euId = eu.getPermitEUID();

			permitDAO.removePermitEU(euId);

			if (eu.getEuFee().getEUFeeId() != null)
				permitDAO.removePermitEUFee(eu.getEuFee().getEUFeeId());

			PermitEUGroup eg = eu.getEuGroup();
			if (eg != null) {
				List<PermitEU> eus = eg.getPermitEUs();
				if (eus.size() == 1)
					permitDAO.removePermitEUGroup(eg);
			}
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
	public ValidationMessage[] synchPermitEUs(Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		boolean hasError = false;

		try {

			String faciltiyID = permit.getFacilityId();
			Facility facility = facilityDAO().retrieveFacility(faciltiyID);

			for (PermitEUGroup eug : permit.getEuGroups()) {

				ArrayList<PermitEU> newEUs = new ArrayList<PermitEU>();

				for (PermitEU peu : eug.getPermitEUs()) {
					EmissionUnit oldEu = peu.getFpEU();
					if (oldEu == null) {
						String msg = "Could not locate a facility Emission Unit for "
								+ "EU ID = " + peu.getPermitEUID() + ".";
						messages.add(new ValidationMessage("Permit", msg,
								ValidationMessage.Severity.ERROR));
						logger.error(msg);
						hasError = true;
						continue;
					}

					EmissionUnit eu = facility.getMatchingEmissionUnit(oldEu
							.getCorrEpaEmuId());

					if (eu == null) {
						String msg = "Could not locate a current Emission Unit for "
								+ "EU ID = "
								+ peu.getPermitEUID()
								+ ", Constant EU ID = "
								+ oldEu.getCorrEpaEmuId() + ".";
						messages.add(new ValidationMessage("Permit", msg,
								ValidationMessage.Severity.ERROR));
						logger.error(msg);
						hasError = true;
						continue;
					}

					// Make sure the permitEU points to the matching latest EU.
					String oldEmuId = oldEu.getEpaEmuId();
					String newEmuId = eu.getEpaEmuId();
					if (!oldEmuId.equals(newEmuId)) {
						String msg = "Old EU ID " + oldEmuId
								+ " has been changed to " + newEmuId + ".";
						messages.add(new ValidationMessage("Permit", msg,
								ValidationMessage.Severity.INFO));
					}
					peu.setFpEU(eu);

					// 2725
					peu.setDapcDescription(eu.getEuDesc());

					newEUs.add(peu);
				}
				if (!hasError) {
					eug.setPermitEUs(newEUs);
				}
			}

			if (!hasError) {

				ArrayList<PermitEUGroup> newGroups = new ArrayList<PermitEUGroup>();

				for (PermitEUGroup eug : permit.getEuGroups()) {
					permitDAO().modifyPermitEUGroup(eug);
					PermitEUGroup newEug = permitDAO().retrievePermitEUGroup(
							eug.getPermitEUGroupID());
					if (newEug == null) {
						logger.error("Could not retrieve EU Group "
								+ eug.getPermitEUGroupID() + ".");
					} else {
						newGroups.add(newEug);
					}
				}
				permit.setEuGroups(newGroups);
			}

		} catch (Exception e) {
			messages.add(new ValidationMessage("Permit",
					"Exception caught while synching emissions units: "
							+ e.getMessage(), ValidationMessage.Severity.ERROR));
			logger.error("Exception caught while synching emissions units: "
					+ e.getMessage(), e);
			hasError = true;
		}

		if (!hasError) {
			messages.add(new ValidationMessage("Permit",
					"EU IDs have been synched.",
					ValidationMessage.Severity.INFO));
		}

		return messages.toArray(new ValidationMessage[0]);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void prepareIssuance(int permitID, int userID, String issuanceType)
			throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);

		try {
			Permit permit = permitDAO.retrievePermit(permitID);

			if (permit instanceof PTIOPermit) {
				((PTIOPermit) permit).createEUFees();
			//} else if (permit instanceof RPEPermit) {
			//	String maxRPEFee = getParameter("MaxRPEFee");
			//	((RPEPermit) permit).createEUFees(maxRPEFee);
			}

			/*
			 * Prelimary checks common to all permit types
			 */
			if (permit.getApplications().isEmpty()) {
				throw new DAOException(
						"Permit references no application, cannot issue draft");
			}

			if (PermitIssuanceTypeDef.isValid(issuanceType)) {
				PermitIssuanceTypeDef.getData().getItems()
						.getItemDesc(issuanceType);
			} else {
				throw new DAOException("Issuance type " + issuanceType
						+ " is incorrect type!!");
			}

			PermitIssuance pi = permit.getPermitIssuances().get(issuanceType);
			// Set issuance status to not ready.
			if (pi == null) {
				pi = new PermitIssuance(permit.getPermitID(),
						permit.getPermitNumber(), issuanceType,
						IssuanceStatusDef.NOT_READY);
				permit.getPermitIssuances().put(issuanceType, pi);
			} else {
				pi.setIssuanceStatusCd(IssuanceStatusDef.NOT_READY);
			}

			if (pi.getIssuanceDate() == null) {
				throw new DAOException("Issuance Date is needed.");
			}

			if (PermitIssuanceTypeDef.Final.equalsIgnoreCase(issuanceType)) {
				if (permit.getEffectiveDate() == null) {
					throw new DAOException("EffectiveDate is needed.");
				}
				if (!(permit instanceof PTIOPermit && ((PTIOPermit) permit)
						.isTv()) && permit.getExpirationDate() == null) {
					throw new DAOException("Expiration is needed.");
				}
			}

			// Check the EUs for AQD description and company equipment ID.
			boolean missingStuff = false;
			String missingError = "One or more emissions units are missing "
					+ "a AQD Description and/or a Company ID: ";
			String missingEUs = "";
			for (PermitEUGroup eug : permit.getEuGroups()) {
				if (permit instanceof TVPermit && eug.isInsignificantEuGroup()) {
					continue;
				}
				for (PermitEU peu : eug.getPermitEUs()) {
					if (peu.getDapcDescription() == null
							|| peu.getDapcDescription().length() == 0
							|| peu.getCompanyId() == null
							|| peu.getCompanyId().length() == 0) {
						if (missingEUs.length() == 0) {
							missingEUs += peu.getFpEU().getEpaEmuId();
						} else {
							missingEUs += ", " + peu.getFpEU().getEpaEmuId();
						}
						missingStuff = true;
					}
				}
			}
			if (missingStuff) {
				throw new DAOException(missingError + missingEUs + ".");
			}

			// Now do permit type-specific work.
			if (permit instanceof PTIOPermit) {
				preparePTIOIssuance((PTIOPermit) permit, userID, issuanceType);
			//} else if (PermitTypeDef.TIV_PTO.equals(permit.getPermitType())) {
			//	prepareTIVPTOIssuance(permit, userID, issuanceType);
			} else if (permit instanceof TVPermit) {
				prepareTVPTOIssuance((TVPermit) permit, userID, issuanceType);
			}

			// Update DB.
			modifyPermit(permit, userID, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage(), e);
		} finally {
			closeTransaction(trans);
		}

	}

	/**
	 * @param permit
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * 
	 */
	public void modifyPermit(Permit permit, int userId, Transaction trans)
			throws DAOException {

		PermitDAO permitDAO = permitDAO(trans);
		DocumentDAO documentDAO = documentDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		if (permit instanceof PTIOPermit) {
			// if it is a waiver then reset PSD, NANSR, received comments,
			// public notice needed and hearing required fields 
			if(!((PTIOPermit)permit).isActionTypePermit()) {
				((PTIOPermit) permit).setSubjectToNANSR(null);
				((PTIOPermit) permit).setSubjectToPSD(null);
				permit.setReceivedComments(null);
				permit.setIssueDraft(false);
				permit.setDapcHearingReqd(false);
			}
			
			if (permit.getFinalIssueDate() == null) {
				permit.setRecissionDate(null);
				((PTIOPermit) permit).setRecissionDate(null);
			}
			
			// if modeling required is no then reset modeling submitted date
			if(!AbstractDAO.translateIndicatorToBoolean(((PTIOPermit) permit).getModelingRequired()))
				((PTIOPermit) permit).setModelingCompletedDate(null);
				
			permitDAO.modifyPTIOPermit(permit);
		} else if (permit instanceof TVPermit) {
			if (permit.getFinalIssueDate() == null) {
				permit.setRecissionDate(null);
				((TVPermit) permit).setRecissionDate(null);
			}
			permitDAO.modifyTVPermit(permit);
		//} else if (permit instanceof RPEPermit) {
		} else if (permit instanceof RPRPermit) {
		}

		// else if (permit instanceof RegPermit) {
		// }
		else {
			String error = "Modify permit was asked to modify an unrecognized "
					+ "class of permit object. Permit Number = "
					+ permit.getPermitNumber() + ", PermitType = "
					+ permit.getPermitType();
			throw new DAOException(error);
		}
		permitDAO.modifyPermit(permit);

		// Create mact, neshaps, and nsps subpart codes.
		permitDAO.removeSubpartCDs(permit);
		permitDAO.createSubpartCDs(permit);

		Integer pID = permit.getPermitID();

		permitDAO.markTempPermitDocuments(pID);
		permitDAO.removePermitDocuments(pID);
		//System.out.println("permit.getDocuments():" +permit.getDocuments().size());
		for (PermitDocument doc : permit.getDocuments()) {
			//System.out.println("permit.getFacilityId(): "+permit.getFacilityId());
			doc.setFacilityID(permit.getFacilityId());
			doc.setTemporary(false);
			if (doc.getDocumentID() != null) {
				documentDAO.modifyDocument(doc);
			} else {
				doc = (PermitDocument) documentDAO.createDocument(doc);
			}
			doc.setPermitId(pID);
			permitDAO.createPermitDocument(doc);

			// if this is an updated issuance doc and the permit has already
			// been issued,
			// we need to generate a PDF version of the document
			if (PermitDocTypeDef.ISSUANCE_DOCUMENT.equals(doc
					.getPermitDocTypeCD())
					&& ((PermitGlobalStatusDef.ISSUED_DRAFT.equals(permit
							.getPermitGlobalStatusCD()) && "D".equals(doc
							.getIssuanceStageFlag()))
							|| (PermitGlobalStatusDef.ISSUED_FINAL
									.equals(permit.getPermitGlobalStatusCD()) && "F"
									.equals(doc.getIssuanceStageFlag()))
							|| (PermitGlobalStatusDef.ISSUED_PP.equals(permit
									.getPermitGlobalStatusCD()) && "P"
									.equals(doc.getIssuanceStageFlag())) 
							//|| (PermitGlobalStatusDef.ISSUED_PPP
							//.equals(permit.getPermitGlobalStatusCD()) && "2"
							//.equals(doc.getIssuanceStageFlag()))
							)) {
				try {
					generatePDFIssuanceDoc(doc);
				} catch (IOException e) {
					logger.error(
							"Exception while attempting to generate PDF version of document "
									+ doc.getDocumentID(), e);
				}
			}
		}

		for (PermitEUGroup group : permit.getEuGroups()) {

			if (group.getPermitEUGroupID() == null) {
				permitDAO.createPermitEUGroup(group);
			} else {
				permitDAO.modifyPermitEUGroup(group);
			}

			List<PermitEU> eus = group.getPermitEUs();
			for (PermitEU eu : eus) {
				infraDAO.createFieldAuditLogs(permit.getFacilityId(),
						permit.getFacilityNm(), userId, eu.getFieldAuditLogs());
			}
		}

		// Now remove associations... TODO remove associations
		permitDAO.removePermitApplications(pID);
		permitDAO.removePermitReasons(pID);

		// Create any reasons...
		permitDAO.addPermitReasons(pID, permit.getPermitReasonCDs());

		// Modify permit issuances
		LinkedHashMap<String, PermitIssuance> pis = permit.getPermitIssuances();
		LinkedHashMap<String, PermitIssuance> dpis = permitDAO
				.retrievePermitIssuances(pID);

		for (String key : pis.keySet()) {
			if (dpis.get(key) != null) {
				permitDAO.modifyPermitIssuance(pis.get(key));
				infraDAO.createFieldAuditLogs(permit.getFacilityId(), permit
						.getFacilityNm(), userId, pis.get(key)
						.getFieldAuditLogs());
			} else {
				permitDAO.createPermitIssuance(pis.get(key));
			}
		}
		
		permitDAO.removePermitCCList(pID);
		for (PermitCC cc : permit.getPermitCCList()) {
			permitDAO.createPermitCC(cc);
		}
		
		if (permit.getClass() == PTIOPermit.class) {

			permitDAO.removeNSRFixedChargeList(pID);
			for (NSRFixedCharge cp : ((PTIOPermit) permit)
					.getNSRFixedChargeList()) {
				permitDAO.createNSRFixedCharge(cp);
			}

			permitDAO.removePermitChargePaymentList(pID);
			for (PermitChargePayment cp : ((PTIOPermit) permit)
					.getPermitChargePaymentList()) {
				permitDAO.createPermitChargePayment(cp);
			}
		}
		
		permit.setPermitConditionList(getPermitConditionService().retrievePermitConditionList(pID));
	
		// Recreate contacts
		List<Contact> ncs = permit.getContacts();
		ArrayList<Contact> ocs = permitDAO.retrievePermitContacts(pID);
		for (Contact c : ncs) {
			if (!ocs.contains(c)) {
				permitDAO.createPermitContact(pID, c.getContactId());
			} else {
				ocs.remove(c);
			}
		}
		for (Contact c : ocs) {
			permitDAO.removePermitContact(pID, c.getContactId());
		}

		permitDAO.addPermitApplications(permit.getPermitID(),
				permit.getApplications());

		infraDAO.createFieldAuditLogs(permit.getFacilityId(),
				permit.getFacilityNm(), userId, permit.getFieldAuditLogs());

	}

	/**
	 * @param permit
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Timestamp findExpirationDate(Permit permit) throws DAOException {

		Timestamp ret = new Timestamp(System.currentTimeMillis());

		GregorianCalendar fid = new GregorianCalendar();
		fid.setTimeInMillis(permit.getEffectiveDate().getTime());

		// Retrieve the most recently issued active permit by permit type
		List<Permit> aps = retrieveRecentlyIssuedActivePermits(permit);
		String primaryReasonCD = permit.getPrimaryReasonCD();

		// PTIO case:
		if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {
			PTIOPermit p = (PTIOPermit) permit;
			// permit type is either Initial or Renewal
			// 2195: PTIO Chapter 31 Mod Allow to be issued with 10 year renewal
			// cycle
			fid = null;
//			if (!PermitReasonsDef.isModReason(primaryReasonCD)
//					|| permit.getPermitReasonCDs().contains(
//							PermitReasonsDef.CHAPTER_31_MOD)) {
//				// there are not existing, active PTIOs for this facility
//				if (aps.size() == 0) {
//					// PTIO is an FEPTIO
//					if ((p.isFeptio() || p.isNonFeptio5YrRenewal())) {
//						// FEPTIO expiration date is 5 years.
//						fid.add(Calendar.YEAR, 5);
//					} else {
//						// Other permit expiration date is 10 years.
//						fid.add(Calendar.YEAR, 10);
//					}
//				} else {
//					// expdate = expiration date from most recently
//					// issued PTIO permit
//					Timestamp tped = aps.get(0).getExpirationDate();
//					fid.add(Calendar.YEAR, 5);
//
//					// PTIO is an FEPTIO and expdate > 5 years from effective
//					// date
//					if (!((p.isFeptio() || p.isNonFeptio5YrRenewal()) && tped
//							.after(fid.getTime()))) {
//						
//						 /* FEPTIOs can't go longer than 5 years*/
//						 
//						fid.setTimeInMillis(tped.getTime());
//					}
//				}
//			} else {
//				fid = null;
//				/* permit type must be either Ch 31 mod or Admin mod */
//				// there are not existing, active PTIOs for this facility
//				if (aps.size() == 0) {
//					// expdate = blank
//					fid = null;
//
//				} else {
//					// expdate = expiration date from most recently
//					// issued PTIO permit
//					Timestamp tped = aps.get(0).getExpirationDate();
//					fid.add(Calendar.YEAR, 5);
//
//					// PTIO is an FEPTIO and expdate > 5 years from effective
//					// date
//					if (!((p.isFeptio() || p.isNonFeptio5YrRenewal()) && tped
//							.after(fid.getTime()))) {
//						/*
//						 * FEPTIOs can't go longer than 5 years
//						 */
//						fid.setTimeInMillis(tped.getTime());
//					}
//				}
//			}
		} else if (permit.getPermitType()
				.equalsIgnoreCase(PermitTypeDef.TV_PTO)
				//|| permit.getPermitType().equalsIgnoreCase(
				//		PermitTypeDef.TIV_PTO)
						) {
			// Title V Permit case:
			// permit type is either Initial or Renewal
			// For now set the permit expiration date to 5 years from the effective date
			// regardless of the permit reason.
			fid.add(Calendar.YEAR, 5);
			//if (!PermitReasonsDef.isModReason(primaryReasonCD)
			//		&& !PermitReasonsDef.RENEWAL.equals(primaryReasonCD)) {
				// expdate = 5 years from effective date
			//	fid.add(Calendar.YEAR, 5);
			//} else {
				/* permit type must be some type of mod */
				// there is at least one existing, active Title V Permit for
				// this
				// facility
			//	if (aps.size() != 0) {
					// expdate = expiration date from most recently issued
					// Title V Permit
			//		Timestamp tped = aps.get(0).getExpirationDate();
			//		fid.setTimeInMillis(tped.getTime());
			//	} else {
					/*
					 * Probably an error case, should always be an existing TV
					 * PTO if we are processing a modification. Leave exp date
					 * blank
					 */
			//		fid = null;
			//	}
			//}
		//} else {
		//	// all other permit type case
		//	fid = null;
		}

		if (fid == null)
			ret = null;
		else
			ret = new Timestamp(fid.getTimeInMillis());

		return ret;
	}

	/**
	 * @param permit
	 * @return A list of active permits sorted from oldest to newest.
	 * @throws DAOException
	 * 
	 */
	private List<Permit> retrieveRecentlyIssuedActivePermits(Permit permit)
			throws DAOException {

		List<Permit> candidates = search(null, null, null,
				permit.getPermitType(), null, null, null, null, permit.getFacilityId(),
				null, PermitGlobalStatusDef.ISSUED_FINAL,
				PermitSQLDAO.EXPIRATION_DATE, permit.getFinalIssueDate(), null,
				null, true, null);

		TreeMap<Timestamp, Permit> sorted = new TreeMap<Timestamp, Permit>();

		for (Permit candidate : candidates) {
			candidate = permitDAO().retrievePermit(candidate.getPermitID());
			boolean isActive = false;
			for (PermitEUGroup peug : candidate.getEuGroups()) {
				for (PermitEU peu : peug.getPermitEUs()) {
					if (peu.getPermitStatusCd() != null
							&& (peu.getPermitStatusCd().equals(
									PermitStatusDef.ACTIVE) || peu
									.getPermitStatusCd().equals(
											PermitStatusDef.EXTENDED))) {
						isActive = true;
						break;
					}
				}
				if (isActive) {
					break;
				}
			}
			if (isActive) {
				sorted.put(candidate.getExpirationDate(), candidate);
			}
		}

		ArrayList<Permit> activePermits = new ArrayList<Permit>(sorted.values());

		return activePermits;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void markReadyIssuance(int permitID, String issuanceType, int userId)
			throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);

		try {
			Permit permit = permitDAO.retrievePermit(permitID);

			if (permit instanceof PTIOPermit) {
				((PTIOPermit) permit).createEUFees();
			//} else if (permit instanceof RPEPermit) {
			//	String maxRPEFee = getParameter("MaxRPEFee");
			//	((RPEPermit) permit).createEUFees(maxRPEFee);
			}

			/*
			 * Prelimary checks common to all permit types
			 */
			String iDesc;
			if (PermitIssuanceTypeDef.isValid(issuanceType))
				iDesc = PermitIssuanceTypeDef.getData().getItems()
						.getItemDesc(issuanceType);
			else
				throw new DAOException("Issuance type " + issuanceType
						+ " is incorrect type!!");
			PermitIssuance pi = permit.getPermitIssuances().get(issuanceType);
			if (pi == null) {
				throw new DAOException(iDesc + " issuance is not set");
			}
			if (pi.getIssuanceStatusCd().equals(IssuanceStatusDef.ISSUED)) {
				throw new DAOException("Permit " + iDesc + " already issued");
			}

			/*
			 * TODO - for now, we'll add some meat later...
			 */
			pi.setIssuanceStatusCd(IssuanceStatusDef.READY);

			/*
			 * Update DB
			 */
			modifyPermit(permit, userId, trans);

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage(), e);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void unprepareDraftIssuance(int permitID, String issuanceType,
			int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);

		try {
			Permit permit = permitDAO.retrievePermit(permitID);

			/*
			 * Prelimary checks common to all permit types
			 */
			String iDesc;
			if (PermitIssuanceTypeDef.isValid(issuanceType)) {
				iDesc = PermitIssuanceTypeDef.getData().getItems()
						.getItemDesc(issuanceType);
			} else {
				throw new DAOException("Issuance type " + issuanceType
						+ " is incorrect type!!");
			}
			PermitIssuance pi = permit.getPermitIssuances().get(issuanceType);
			if (pi == null) {
				throw new DAOException(iDesc + " issuance is not set");
			}
			if (pi.getIssuanceStatusCd().equals(IssuanceStatusDef.ISSUED)) {
				throw new DAOException("Permit " + iDesc + " already issued");
			}

			/*
			 * TODO - for now, we'll add some meat later...
			 */
			pi.setIssuanceStatusCd(IssuanceStatusDef.NOT_READY);

			/*
			 * Update DB
			 */
			modifyPermit(permit, userId, trans);

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="RequiresNew"
	 */
	public ValidationMessage finalizeIssuance(int permitID,
			String issuanceType, boolean updatesOnFinal, int userId, User user)

	throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);
		Permit permit = null;
		Invoice newInv = null;
		ValidationMessage wrapnError = null;
//		String corrTypeCD = null;
//		Correspondence correspondence = null;

		try {
			InfrastructureService infBO = ServiceFactory.getInstance()
					.getInfrastructureService();

			FacilityService facBO = ServiceFactory.getInstance()
					.getFacilityService();

			permit = permitDAO.retrievePermit(permitID);
			if(!PermitGlobalStatusDef.DENIAL_PENDING.equals(issuanceType)) {
//				if (permit instanceof PTIOPermit) {
//					((PTIOPermit) permit).createEUFees();
//				//} else if (permit instanceof RPEPermit) {
//				//	String maxRPEFee = getParameter("MaxRPEFee");
//				//	((RPEPermit) permit).createEUFees(maxRPEFee);
//				}
	
				// Check for null or invalid issuanceType string.
				String iDesc;
				if (PermitIssuanceTypeDef.isValid(issuanceType)) {
					iDesc = PermitIssuanceTypeDef.getData().getItems()
							.getItemDesc(issuanceType);
				} else {
					throw new DAOException("Issuance type " + issuanceType
							+ "  is null or is invalid for permit "
							+ permit.getPermitNumber() + ".");
				}
	
				// Prelimary checks common to all permit types
				PermitIssuance pi = permit.getPermitIssuances().get(issuanceType);
	
				if (pi == null) {
					throw new DAOException(iDesc + " is not set for permit "
							+ permit.getPermitNumber() + ".");
				}
				if (pi.getIssuanceStatusCd().equals(IssuanceStatusDef.ISSUED)) {
					return new ValidationMessage("Permit", "Permit Number "
							+ permit.getPermitNumber()
							+ " has already been issued.",
							ValidationMessage.Severity.INFO);
				}
				if (pi.getIssuanceDate() == null && permit instanceof PTIOPermit) {
					throw new DAOException(iDesc
							+ " issuance date is not set for permit "
							+ permit.getPermitNumber() + ".");
				}
				if (!pi.getIssuanceStatusCd().equals(IssuanceStatusDef.READY)) {
					throw new DAOException("Permit " + iDesc
							+ " is not ready for permit "
							+ permit.getPermitNumber() + ".");
				}
	
				pi.setIssuanceStatusCd(IssuanceStatusDef.ISSUED);
	
				// Create new correspondence history with Issuance document ID
				// and without USPS certified mail tracking ID and receipt date.
				// Also fetch the correct OIDs for noticeType and noticeRemark
				// for WRAPN.
				
//				correspondence = new Correspondence();
//				correspondence.setFacilityID(permit.getFacilityId());
//				// TODO replace correspondence codes with static defs.
//				//if (permit.getPermitType().equals(PermitTypeDef.TVPTI)) {
//				//	if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.Draft)) {
//				//		corrTypeCD = "70";
//				//	}
//					// Note: The public notice text is different depending on
//					// whether the permit
//					// is a draft or final. According to Erica, the PN text goes if
//					// it is a draft.
//					// Permit description goes if it is final.
//					//if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.Final)) {
//					//	corrTypeCD = "71";
//				//	}
//				//} else 
//				if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
//					if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.Draft)) {
//						corrTypeCD = "72";
//					}
//					if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.Final)) {
//						corrTypeCD = "73";
//					}
//				} else if (permit.getPermitType().equals(PermitTypeDef.TV_PTO)) {
//					if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.Draft)) {
//						corrTypeCD = "74";
//					}
//					// Note: there is no public notice associated with either PPP or
//					// PP issuances.
//					//if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.PPP)) {
//					//	corrTypeCD = "75";
//					//}
//					if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.PP)) {
//						corrTypeCD = "76";
//					}
//					if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.Final)) {
//						corrTypeCD = "77";
//					}
//				//} else if (permit.getPermitType().equals(PermitTypeDef.TIV_PTO)) {
//				//	if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.Draft)) {
//				//		corrTypeCD = "84";
//				//	}
//					// Note: there is no public notice associated with either PPP or
//					// PP issuances.
//					//if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.PPP)) {
//					//	corrTypeCD = "85";
//					//}
//				//	if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.PP)) {
//				//		corrTypeCD = "86";
//				//	}
//				//	if (pi.getIssuanceTypeCd().equals(PermitIssuanceTypeDef.Final)) {
//				//		corrTypeCD = "87";
//				//	}
//				}
//	
//				// Create a correspondence history record in DB.
//				correspondence.setCorrespondenceTypeCode(corrTypeCD);
//				correspondence.setDateGenerated(new Timestamp(Calendar
//						.getInstance().getTimeInMillis()));
//				correspondence.setAdditionalInfo(permit.getPermitNumber());
//				correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
//				createCorrespondence(correspondence, trans);
//	
//				PermitDocument pDoc = permit.getIssuanceDoc(issuanceType);
//				if (pDoc != null) {
//					String pDocDesc = pDoc.getDescription();
//					if (pDocDesc == null) {
//						pDoc.setDescription(issuanceType);
//					}
//	
//					// 2560
//					generatePDFIssuanceDoc(pDoc);
//				}
//	
				// We need to possibly update the FP and other permits and create an
				// invoice when the
				// permit is issued final.
				if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.Final)) {
	
//					if (permit instanceof PTIOPermit) {
//						((PTIOPermit) permit).createEUFees();
//						// Do invoice
//						newInv = setupInvoice(permit);
//	
//						if (newInv != null) {
//							// InfrastructureHelper infraHelper = new
//							// InfrastructureHelper();
//							newInv = infraHelper.createInvoice(newInv, trans);
//							logger.debug("New invoice id = " + newInv.getInvoiceId()
//									+ ", last_mod = " + newInv.getLastModified());
//						}
//					}
//	
					// Perform final facility updates based on permit type and
					// permit primary reason code.
					if (updatesOnFinal) {
	
						if (permit.getPrimaryReasonCD() == null) {
							throw new DAOException("Permit "
									+ permit.getPermitNumber()
									+ " has no primary reason code.");
						}
	
						if (permit instanceof PTIOPermit) {
	
							// isTv() == true means that the permit is a TV PTI, not
							// a PTIO.
							if (((PTIOPermit) permit).isTv()) {
								updateFinalPTI((PTIOPermit) permit, userId, trans);
							} else {
								// Otherwise it is a PTIO.
	
								/*
								 * if (dueDates != null && !dueDates.isEmpty() &&
								 * dueDates
								 * .get(0).getPERDueDateCd().equals(PTIOPERDueDateDef
								 * .NOT_APPLICABLE)) {
								 * 
								 * if (((PTIOPermit) permit).getTmpPERDueDateCD() ==
								 * null || ((PTIOPermit)
								 * permit).getTmpPERDueDateCD()
								 * .equals(PTIOPERDueDateDef.NOT_APPLICABLE)) {
								 * 
								 * throw new DAOException("PTIO Permit " +
								 * permit.getPermitNumber() +
								 * " has a null or invalid PER due date."); }
								 * 
								 * 
								 * // Find all of the other PTIOs and re-set their
								 * PER Due Date iff this is the // 1st PTIO issued
								 * for this facility. List<Permit> others =
								 * permitDAO.searchPermits(null, null,
								 * PermitTypeDef.PTIO, null, null,
								 * permit.getFacilityId(), null, null, null, null,
								 * null, null, true); for (Permit other : others) {
								 * Permit fetched =
								 * permitDAO.retrievePermit(other.getPermitID()); if
								 * (!(fetched instanceof PTIOPermit)) { continue; }
								 * ((PTIOPermit)
								 * fetched).setTmpPERDueDateCD(((PTIOPermit)
								 * permit).getTmpPERDueDateCD()); // We will update
								 * the permit being finalized later. if
								 * (fetched.getPermitID
								 * ().equals(permit.getPermitID())) { continue; }
								 * permitDAO.modifyPTIOPermit(fetched);
								 * permitDAO.modifyPermit(fetched); }
								 * 
								 * }
								 */
	
								// Currently, we perform the same set of updates
								// regardless
								// of permit reason, but in case this ever
								// changes...
								if (permit.getPrimaryReasonCD().equals(
										PermitReasonsDef.CONSTRUCTION)
										|| permit.getPrimaryReasonCD().equals(
												PermitReasonsDef.MODIFICATION)
										|| permit.getPrimaryReasonCD().equals(
												PermitReasonsDef.OTHER)
										|| permit.getPrimaryReasonCD().equals(
												PermitReasonsDef.RECONSTRUCTION)
										|| permit.getPrimaryReasonCD().equals(
												PermitReasonsDef.SYNTHETIC_MINOR)
										|| permit.getPrimaryReasonCD().equals(
												PermitReasonsDef.TEMPORARY_PERMIT)) {
								}
	
								updateFinalPTIO((PTIOPermit) permit, userId, trans);
							}
	
						//} else if (permit instanceof TIVPermit) {
						//	updateFinalTIV((TIVPermit) permit, userId, trans);
						} else if (permit instanceof TVPermit) {
	
							// TODO - need to resolve open issue on how to deal with
							// revoke/reissue permit reason
							if (permit.getPrimaryReasonCD().equals(
									PermitReasonsDef.INITIAL)
									|| permit.getPrimaryReasonCD().equals(
											PermitReasonsDef.RENEWAL)) {
	
								updateFinalTV((TVPermit) permit, userId, trans);
							} else if (permit.getPrimaryReasonCD().equals(
									PermitReasonsDef.SPM)
									|| permit.getPrimaryReasonCD().equals(
											PermitReasonsDef.MPM)
									|| permit.getPrimaryReasonCD().equals(
											PermitReasonsDef.APA)
									|| permit.getPrimaryReasonCD().equals(
											PermitReasonsDef.REOPENING)
									// || permit.getPrimaryReasonCD().equals(
									//		PermitReasonsDef.RESCIND)
									|| permit.getPrimaryReasonCD().equals(
											PermitReasonsDef.CHANGE_502_B_10)) {
	
								updateFinalTVModification((TVPermit) permit,
										userId, trans);
							}
							// NOTE: No changes required for APA or Off Permit
							// Change Modifications.
						}
						
						// Update superseded conditions.
						List<PermitCondition> permCondList 
							= getPermitConditionService().retrievePermitConditionList(permit.getPermitID());
						HashMap<Integer, PermitCondition> permCondMap = new HashMap<Integer, PermitCondition>();
						for (PermitCondition cond : permCondList) {
							for (PermitConditionSupersession superseded : cond.getSupersededByThis()) {
								PermitCondition oldPc = permCondMap.get(superseded.getSupersededPermitConditionId());
								if (oldPc == null) {
									oldPc = getPermitConditionService().retrievePermitConditionById(superseded.getSupersededPermitConditionId());
									oldPc.setPermitConditionStatusCd(superseded.getSupersedingOption().equals(PermitConditionSupersedenceStatusDef.COMPLETE)
											? PermitConditionStatusDef.SUPERSEDED : PermitConditionStatusDef.PARTIALLY_SUPERSEDED);
									permCondMap.put(oldPc.getPermitConditionId(), oldPc);
								}
								if (superseded.getSupersedingOption().equals(PermitConditionSupersedenceStatusDef.COMPLETE)) {
									oldPc.setPermitConditionStatusCd(PermitConditionStatusDef.SUPERSEDED);
								}
							}
						}
						for (PermitCondition cond : permCondMap.values()) {
							getPermitConditionService().modifyPermitCondition(cond);
						}
						
					}
					
					permit.setPermitLevelStatusCd(PermitLevelStatusDef.ACTIVE);
					
				}
			} else {
				// finalize inactive/withdrawn
				if(permit.getPermitGlobalStatusCD().equalsIgnoreCase(PermitGlobalStatusDef.ISSUED_DENIAL)) {
					return new ValidationMessage("Permit", "Permit Number "
							+ permit.getPermitNumber()
							+ " has already been issued withdrawal.",
							ValidationMessage.Severity.INFO);
				}
				
				if(!permit.getPermitGlobalStatusCD().equalsIgnoreCase(PermitGlobalStatusDef.DENIAL_PENDING)) {
					return new ValidationMessage("Permit", "Permit Number "
							+ permit.getPermitNumber()
							+ " has not been marked for Withdrawal.",
							ValidationMessage.Severity.INFO);
				}
				
				// generate withdrawl correspondence
//				correspondence = new Correspondence();
//				correspondence.setFacilityID(permit.getFacilityId());
//				if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
//						corrTypeCD = "125";
//				} else if (permit.getPermitType().equals(PermitTypeDef.TV_PTO)) {
//					corrTypeCD = "133";
//				}
//				correspondence.setCorrespondenceTypeCode(corrTypeCD);
//				correspondence.setDateGenerated(new Timestamp(Calendar
//						.getInstance().getTimeInMillis()));
//				correspondence.setAdditionalInfo(permit.getPermitNumber());
//				correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
//				createCorrespondence(correspondence, trans);
				
				issuanceType = PermitGlobalStatusDef.ISSUED_DENIAL;
			}

			permit.setPermitGlobalStatusCD(PermitGlobalStatusDef
					.find(issuanceType));

			Facility facility = facBO.retrieveFacility(permit.getFacilityId());
			if (facility == null) {
				throw new DAOException("Could not locate facility for permit "
						+ permit.getPermitNumber() + "." + " Facility ID = "
						+ permit.getFacilityId() + ".");
			}

			// Update DB.
			modifyPermit(permit, userId, trans);

			/*
			 * Remove reference to Revenues. // Handles Post to Revenues. if
			 * (newInv != null) { try { infBO.preparePostToRevenue(user, newInv,
			 * newInv.getCreationDate(), true); } catch (RemoteException e) {
			 * logger.error("Exception thrown by preparePostToRevenue", e);
			 * throw new
			 * DAOException("Issuance did not complete because REVENUES failed. "
			 * +
			 * "Please (1) inform the System Administrator; and (2) loop this permit "
			 * +
			 * "back to \"Prepare Final\" to wait until the problem is fixed.",
			 * e); } } else if (newInv == null && (permit instanceof PTIOPermit)
			 * && ((PTIOPermit) permit).getTotalAmount() > 0 &&
			 * permit.getPermitGlobalStatusCD
			 * ().equalsIgnoreCase(PermitGlobalStatusDef.ISSUED_FINAL)) {
			 * 
			 * String message = "Unable to post to revenues for permit number "
			 * + permit.getPermitNumber() + ". Invoice is null.";
			 * logger.error(message); throw new DAOException(message); } else if
			 * (newInv != null && user == null &&
			 * permit.getPermitGlobalStatusCD(
			 * ).equalsIgnoreCase(PermitGlobalStatusDef.ISSUED_FINAL)){
			 * 
			 * String message = "Unable to post to revenues for permit number "
			 * + permit.getPermitNumber() + ". User is null.";
			 * logger.error(message); throw new DAOException(message); }
			 */

			trans.complete();

		}
		// Throw it all away if we have any Exception
		catch (Exception e) {
			if (trans != null) {
				trans.cancel();
			}
			StringBuffer sb = new StringBuffer();
			sb.append("An error occurred when finalizing permit number ");
			sb.append(permit.getPermitNumber());
			sb.append(". ");
			logger.error(sb.toString() + e.getMessage(), e);
			throw new DAOException(sb.toString() + e.getMessage());
		} finally {
			closeTransaction(trans);
		}

		return wrapnError;

	}
	

	/**
	 * Similar to a Title V permit the final issuance should mark all previous
	 * Title IV permits issued to the facility as superseded. It should mark the
	 * Title IV permit EUs as "active" when they are issued. There are no
	 * facility inventory fields to mark so any of those business rules that were
	 * present for Title IV permitting should be removed.
	 * 
	 * @param permit
	 * @param userId
	 * @param trans
	 * @throws DAOException
	 */
		/*
	private void updateFinalTIV(TIVPermit permit, int userId, Transaction trans)
			throws DAOException {
		try {

			// Fetch the latest version of the facility inventory.
			PermitDAO permitDAO = permitDAO(trans);

			for (PermitEUGroup eug : permit.getEuGroups()) {

				for (PermitEU peu : eug.getPermitEUs()) {

					peu.setPermitStatusCd(PermitStatusDef.ACTIVE);
				}
			}

			// When a Title V Permit is issued Final:
			// ALL EUs belonging to other Issued Final Title V Permits, Issued
			// Final
			// Legacy State PTOs, or Issued Final Legacy Registrations at the
			// facility
			// should be set to SUPERSEDED if its EU Permit Status is ACTIVE,
			// EXPIRED
			// or EXTENDED.
			List<Permit> otherPermits = permitDAO.searchPermits(null, null,
					null, null, null, null, permit.getFacilityId(), null,
					PermitGlobalStatusDef.ISSUED_FINAL, null, null, null, null,
					true);

			for (Permit other : otherPermits) {

				other = permitDAO.retrievePermit(other.getPermitID());

				// Don't supersede ourself!
				if (other.getPermitNumber().equals(permit.getPermitNumber())) {
					continue;
				}

				//if (other.getPermitType().equals(PermitTypeDef.TIV_PTO)) {
//
				//	for (PermitEU tpeu : other.getEus()) {
				//		tpeu = permitDAO.retrievePermitEU(tpeu.getPermitEUID());
				//		if (tpeu.getPermitStatusCd() == null
				//				|| tpeu.getPermitStatusCd().equals(
				//						PermitStatusDef.ACTIVE)
				//				|| tpeu.getPermitStatusCd().equals(
				//						PermitStatusDef.EXPIRED)
				//				|| tpeu.getPermitStatusCd().equals(
				//						PermitStatusDef.EXTENDED)) {
//
				//			tpeu.setPermitStatusCd(PermitStatusDef.SUPERSEDED);
							// The date should be the effective date of permit
							// (see email).
				//			tpeu.setSupersededDate(permit.getEffectiveDate());
				//			permitDAO.modifyPermitEU(tpeu);
				//		}
				//	}
				}
			}

		} catch (Exception e) {
			throw new DAOException("Problem updating final NSR.", e);
		}
	} */

	/**
	 * Handle facility, permit, and EU updates specific to PTIO permits
	 * including Initial, Chapter 31 Modification, or Administrative
	 * Modification. See LSRD Worksheet "tables" 5115D:5130G "Final PTIO
	 * Issuance (Initial, Chapter 31 Modification, Administrative Modification,
	 * or Renewal)". Note: renewal updates in LSRD are identical to these.
	 * 
	 * @param permit
	 *            PTIO permit to finalize.
	 * @param trans
	 *            Transaction for Tomcat only.
	 * 
	 * @throws DAOException
	 *             If anything goes wrong.
	 */
	private void updateFinalPTIO(PTIOPermit permit, int userId,
			Transaction trans) throws DAOException {
		try {
				PermitDAO permitDAO = permitDAO(trans);
				FacilityService facBO = ServiceFactory.getInstance()
						.getFacilityService();
	
				// Fetch the latest version of the facility inventory.
				Facility facility = getEditableCurrent(facBO,
						permit.getFacilityId(), userId, trans);
				if (facility == null) {
					throw new NullPointerException("Could not locate facility for "
							+ "Facility ID = " + permit.getFacilityId()
							+ ", FacilityID = " + permit.getFacilityId());
				}
				
				facility = facBO.retrieveFacility(permit.getFacilityId(), trans);
				
				// Update Federal rules and regulations in the facility 
				// inventory if they are set in the permit.
				// Set Part 61 NESHAP flag and SubParts
				if (permit.isPart61NESHAP()) {
					if (!facility.isNeshaps())
						facility.setNeshaps(true);
						// Update subparts
						List<String> permitPart61NESHAPSubparts = permit.getPart61NESHAPSubpartCDs();
						List<String> facilityNESHAPSubparts = facility.getNeshapsSubparts();
						List<PollutantCompCode> neshapPollutantCompCds = facility.getNeshapsSubpartsCompCds();
						for (String part61NESHAPSubpart : permitPart61NESHAPSubparts) {
							if (!facilityNESHAPSubparts.contains(part61NESHAPSubpart)) {
								PollutantCompCode pcc = new PollutantCompCode();
								pcc.setFpId(facility.getFpId());
								pcc.setPollutantCd(part61NESHAPSubpart);
								neshapPollutantCompCds.add(pcc);
							}
						}
					facility.setNeshapsSubpartsCompCds(neshapPollutantCompCds);
				}
	
				// Set Part 63 NESHAP (MACT) flag and SubParts
				if (permit.isPart63NESHAP()) {
					if (!facility.isMact())
						facility.setMact(true);
					// Update subparts
					List<String> permitPart63NESHAPSubparts = permit.getPart63NESHAPSubpartCDs();
					List<String> facilityMACTSubparts = facility.getMactSubparts();
					for (String part63NESHAPSubpart : permitPart63NESHAPSubparts) {
						if (!facilityMACTSubparts.contains(part63NESHAPSubpart))
							facilityMACTSubparts.add(part63NESHAPSubpart);
					}
					facility.setMactSubparts(facilityMACTSubparts);
				}
	
				// Set NSPS flag and SubParts
				if (permit.isNsps()) {
					if (!facility.isNsps())
						facility.setNsps(true);
					// Update subparts
					List<String> permitNSPSSubparts = permit.getNspsSubpartCDs();
					List<String> facilityNSPSSubparts = facility.getNspsSubparts();
					for (String nspsSubpart : permitNSPSSubparts) {
						if (!facilityNSPSSubparts.contains(nspsSubpart))
							facilityNSPSSubparts.add(nspsSubpart);
					}
					facility.setNspsSubparts(facilityNSPSSubparts);
				}
	
				// Set Subject to PSD flag
				if (AbstractDAO.translateIndicatorToBoolean(permit.getSubjectToPSD())
							&& !facility.isPsd())
						facility.setPsd(true);
							
				// Set Subject to Non-Attainment NSR flag
				if (AbstractDAO.translateIndicatorToBoolean(permit.getSubjectToNANSR())
							&& !facility.isNsrNonattainment())
						facility.setNsrNonattainment(true);
				
	            // A. If the emissions reporting category is "none" or "null" or "TitleV",
	            // set the emissions reporting category for that year to Non-Title V
	            // if final action is a PTIO or FEPTIO that has not been marked as
	            // avoiding Title V.
	            // B. If the emissions reporting category is "none" or "null" or "TitleV",
	            // set the emissions reporting category for that year to SMTV
	            // if final action is an FEPTIO to avoid Title V.
	            // C. If the emissions reporting category is "NTV", and permit is FEPTIO
	            // to avoid Title V, set the emissions reporting category for that year to SMTV.
//	            String rptCategory = null;
//	            Calendar cal = Calendar.getInstance();
//	            cal.setTimeInMillis(permit.getFinalIssueDate().getTime());
//	            String currentRptCat = getCurrentReportingCategory(facility.getFacilityId(), trans);
//	            logger.debug("DEBUG]: Current reporting category = " + currentRptCat);
//	            if (currentRptCat == null
//	                || currentRptCat.equals(EmissionReportsDef.NONE)
//	                || currentRptCat.equals(EmissionReportsDef.TV)
//	                || currentRptCat.equals(EmissionReportsDef.NTV)) {
//	                                                        
//	                if (permit.isFePtioTvAvoid()) {
//	                    rptCategory = EmissionReportsDef.SMTV;
//	                }
//	                else {
//	                    rptCategory = EmissionReportsDef.NTV;
//	                }
//	            }
//	            logger.debug("DEBUG] rptCategory = " + rptCategory);
//	            if (rptCategory != null) {
//	                EmissionsReportService emRptBO = ServiceFactory.getInstance().getEmissionsReportService();
//	                
//	                int year = cal.get(Calendar.YEAR);
//	                logger.debug("DEBUG]: Updating reporting info for year " + year + " to " + rptCategory);
//	                emRptBO.updateYearlyReportingInfo(facility.getFacilityId(), year,
//	                    trans, rptCategory, ReportReceivedStatusDef.REPORT_NOT_REQUESTED, null, null);
//	            }
				
							
				facBO.modifyFacility(facility, userId, trans);
							
				facBO.modifyFacilityFedRulesAndRegsSubparts(facility, trans);
	
		} catch (Exception e) {
			throw new DAOException("Problem updating final NSR.", e);
		}
	}

	/**
	 * Handle facility, permit, and EU updates specific to PTI permits. See LSRD
	 * Worksheet "tables" 5102D:5109G "Final PTI Issuance".
	 * 
	 * @param permit
	 *            PTI permit to finalize.
	 * @param trans
	 *            Transaction for Tomcat only.
	 * @param facility
	 *            Facility to be updated on finalization of permit.
	 * @param facBO
	 *            Facility business object.
	 * 
	 * @throws DAOException
	 *             If anything goes wrong.
	 */
	private void updateFinalPTI(PTIOPermit permit, int userId, Transaction trans)
			throws DAOException {

		try {

			PermitDAO permitDAO = permitDAO(trans);
			FacilityService facBO = ServiceFactory.getInstance()
					.getFacilityService();

			// Fetch the latest version of the facility inventory.
			Facility facility = getEditableCurrent(facBO,
					permit.getFacilityId(), userId);
			if (facility == null) {
				throw new NullPointerException("Could not locate facility for "
						+ "Facility ID = " + permit.getFacilityId()
						+ ", FacilityID = " + permit.getFacilityId());
			}
			/* Not valid for IMPACT
			for (PermitEUGroup eug : permit.getEuGroups()) {

				for (PermitEU peu : eug.getPermitEUs()) {

					peu.setPermitStatusCd(PermitStatusDef.ACTIVE);

					EmissionUnit eu = facility.getMatchingEmissionUnit(peu
							.getCorrEpaEMUID());
					if (eu == null) {
						throw new NullPointerException(
								"Could not locate Emission Unit for "
										+ "Permit EU ID = "
										+ peu.getPermitEUID());
					}

					// Current PTI regulatory Status' goes to "Active" for each
					// EU.
					eu.setPtiStatusCd(PTIRegulatoryStatus.ACTIVE);

					// Set 'Exempt Status' to "N/A".
					eu.setExemptStatusCd(ExemptStatusDef.NA);

					// Set all EU 'Title V Classification' to "Unknown" if null
					// and "eu operating status" is not "shut down".
					if (!EuOperatingStatusDef.SD.equalsIgnoreCase(eu
							.getOperatingStatusCd())
							&& eu.getTvClassCd() == null) {
						eu.setTvClassCd(TVClassification.NOT_APPLICABLE);
					}

					// When a PTI is issued Final:
					// All EUs included in that issued PTI which are present in
					// other Issued
					// Final PTIs or Issued Final PTIOs should be set to
					// SUPERSEDED if its EU
					// Permit Status is ACTIVE, EXPIRED or EXTENDED.

					// Mark all previously issued PTIs, PTIOs, or PBRs for that
					// EU as superseded
					// if the EU Permit Status is not 'revoked', 'terminated',
					// or 'superseded'.
					PermitEU[] eus = permitDAO().searchPermitEUsAcrossPermits(
							eu.getCorrEpaEmuId(), false, PermitTypeDef.TV_PTO);

					for (PermitEU tpeu : eus) {
						// Don't supersede ourself!
						if (tpeu.getPermitEUID().equals(peu.getPermitEUID())) {
							continue;
						}
						tpeu = permitDAO.retrievePermitEU(tpeu.getPermitEUID());
						if (tpeu.getPermitStatusCd() == null
								|| tpeu.getPermitStatusCd().equals(
										PermitStatusDef.ACTIVE)
								|| tpeu.getPermitStatusCd().equals(
										PermitStatusDef.EXPIRED)
								|| tpeu.getPermitStatusCd().equals(
										PermitStatusDef.EXTENDED)) {

							tpeu.setPermitStatusCd(PermitStatusDef.SUPERSEDED);
							// The date should be the effective date of permit
							// (see email).
							tpeu.setSupersededDate(permit.getEffectiveDate());
							permitDAO.modifyPermitEU(tpeu);
						}
					}

					facBO.modifyEmissionUnit(eu, userId);

				}
			}*/

			// Set the 'Emissions Reporting Category' for that year to Title V.
//			EmissionsReportService emRptBO = ServiceFactory.getInstance()
//					.getEmissionsReportService();
//
//			Calendar cal = Calendar.getInstance();
//			cal.setTimeInMillis(permit.getEffectiveDate().getTime());
//			int year = cal.get(Calendar.YEAR);
//			emRptBO.updateYearlyReportingInfo(facility.getFacilityId(), year,
//					trans, EmissionReportsDef.TV,
//					ReportReceivedStatusDef.REPORT_NOT_REQUESTED, null, null);

			facility = facBO.retrieveFacility(permit.getFacilityId());
			// Set the 'Current Permitting Classification' to Title V.
			facility.setPermitClassCd(PermitClassDef.TV);

			// Set the "Title V Facility Permit Status" to "Pending Initial" if
			// null.
			if (facility.getPermitStatusCd() == null) {
				facility.setPermitStatusCd(FacPermitStatusDef.PENDING_INITIAL);
			}

			facBO.modifyFacility(facility, userId);
		} catch (Exception e) {
			throw new DAOException("Problem updating final PTI. "
					+ e.getMessage(), e);
		}
	}

	/**
	 * Handle facility, permit, and EU updates specific to Title V permits. See
	 * LSRD Worksheet "tables" 5149D:5155G "Final Title V Issuance (Initial,
	 * Renewal)".
	 * 
	 * @param permit
	 *            Title V permit to finalize.
	 * @param trans
	 *            Transaction for Tomcat only.
	 * @param facility
	 *            Facility to be updated on finalization of permit.
	 * @param facBO
	 *            Facility business object.
	 * 
	 * @throws DAOException
	 *             If anything goes wrong.
	 */
	private void updateFinalTV(TVPermit permit, int userId, Transaction trans)
			throws DAOException {

		try {
				// Fetch the latest version of the facility inventory.
				PermitDAO permitDAO = permitDAO(trans);
				FacilityService facBO = ServiceFactory.getInstance()
						.getFacilityService();
				Facility facility = getEditableCurrent(facBO,
						permit.getFacilityId(), userId);
				if (facility == null) {
					throw new NullPointerException("Could not locate facility for "
							+ "Facility ID = " + permit.getFacilityId()
							+ ", FacilityID = " + permit.getFacilityId());
				}
			
				// Set the 'Emissions Reporting Category' for that year to Title V.
//				EmissionsReportService emRptBO = ServiceFactory.getInstance()
//						.getEmissionsReportService();
	
//				Calendar cal = Calendar.getInstance();
//				cal.setTimeInMillis(permit.getEffectiveDate().getTime());
//				int year = cal.get(Calendar.YEAR);
//				emRptBO.updateYearlyReportingInfo(facility.getFacilityId(), year,
//						trans, EmissionReportsDef.TV,
//						ReportReceivedStatusDef.REPORT_NOT_REQUESTED, null, null);
				
				facility = facBO.retrieveFacility(permit.getFacilityId());
				
				// Set the "Current Permitting Classification" to Title V.
				facility.setPermitClassCd(PermitClassDef.TV);
				
				// Set the "Title V Facility Permit Status" to "Active".
				facility.setPermitStatusCd(FacPermitStatusDef.ACTIVE);
	
				// Update Federal rules and regulations in the facility 
				// inventory if they are set in the permit.
				// Set Part 61 NESHAP flag and SubParts
				if (permit.isPart61NESHAP()) {
					if (!facility.isNeshaps())
						facility.setNeshaps(true);
					// Update subparts
					List<String> permitPart61NESHAPSubparts = permit.getPart61NESHAPSubpartCDs();
					List<String> facilityNESHAPSubparts = facility.getNeshapsSubparts();
					List<PollutantCompCode> neshapPollutantCompCds = facility.getNeshapsSubpartsCompCds();
					for (String part61NESHAPSubpart : permitPart61NESHAPSubparts) {
						if (!facilityNESHAPSubparts.contains(part61NESHAPSubpart)) {
							PollutantCompCode pcc = new PollutantCompCode();
							pcc.setFpId(facility.getFpId());
							pcc.setPollutantCd(part61NESHAPSubpart);
							neshapPollutantCompCds.add(pcc);
						}
					}
					facility.setNeshapsSubpartsCompCds(neshapPollutantCompCds);
				}
	
				// Set Part 63 NESHAP (MACT) flag and SubParts
				if (permit.isPart63NESHAP()) {
					if (!facility.isMact())
						facility.setMact(true);
					// Update subparts
					List<String> permitPart63NESHAPSubparts = permit.getPart63NESHAPSubpartCDs();
					List<String> facilityMACTSubparts = facility.getMactSubparts();
					for (String part63NESHAPSubpart : permitPart63NESHAPSubparts) {
						if (!facilityMACTSubparts.contains(part63NESHAPSubpart))
							facilityMACTSubparts.add(part63NESHAPSubpart);
					}
					facility.setMactSubparts(facilityMACTSubparts);
				}
	
				// Set NSPS flag and SubParts
				if (permit.isNsps()) {
					if (!facility.isNsps())
						facility.setNsps(true);
					// Update subparts
					List<String> permitNSPSSubparts = permit.getNspsSubpartCDs();
					List<String> facilityNSPSSubparts = facility.getNspsSubparts();
					for (String nspsSubpart : permitNSPSSubparts) {
						if (!facilityNSPSSubparts.contains(nspsSubpart))
							facilityNSPSSubparts.add(nspsSubpart);
					}
					facility.setNspsSubparts(facilityNSPSSubparts);
				}
							
				// Set TIV Acid Rain flag
				if(permit.isAcidRain() && !facility.isTivInd())
					facility.setTivInd(true);
	
				facBO.modifyFacility(facility, userId);
				facBO.modifyFacilityFedRulesAndRegsSubparts(facility, trans);

		} catch (Exception e) {
			throw new DAOException("Problem updating final Title V permit. "
					+ e.getMessage(), e);
		}
	}

	/**
	 * Handle facility, permit, and EU updates specific to Title V permits. See
	 * LSRD Worksheet "tables" 5158D:5158G "Final Title V Issuance (Sig, Minor,
	 * and Reopening Modifications)".
	 * 
	 * @param permit
	 *            Title V permit modification to finalize.
	 * @param trans
	 *            Transaction for Tomcat only.
	 * @param facility
	 *            Facility to be updated on finalization of permit.
	 * @param facBO
	 *            Facility business object.
	 * 
	 * @throws DAOException
	 *             If anything goes wrong.
	 */
	private void updateFinalTVModification(TVPermit permit, int userId,
			Transaction trans) throws DAOException {

		try {

				// Fetch the latest version of the facility inventory.
				PermitDAO permitDAO = permitDAO(trans);
				FacilityService facBO = ServiceFactory.getInstance()
						.getFacilityService();
				Facility facility = getEditableCurrent(facBO,
						permit.getFacilityId(), userId);
				if (facility == null) {
					throw new NullPointerException("Could not locate facility for "
							+ "Facility ID = " + permit.getFacilityId()
							+ ", FacilityID = " + permit.getFacilityId());
				}
				
				facility = facBO.retrieveFacility(permit.getFacilityId());
				
				// Update Federal rules and regulations in the facility 
				// inventory if they are set in the permit.
				// Set Part 61 NESHAP flag and SubParts
				if (permit.isPart61NESHAP()) {
					if (!facility.isNeshaps())
						facility.setNeshaps(true);
					// Update subparts
					List<String> permitPart61NESHAPSubparts = permit.getPart61NESHAPSubpartCDs();
					List<String> facilityNESHAPSubparts = facility.getNeshapsSubparts();
					List<PollutantCompCode> neshapPollutantCompCds = facility.getNeshapsSubpartsCompCds();
					for (String part61NESHAPSubpart : permitPart61NESHAPSubparts) {
						if (!facilityNESHAPSubparts.contains(part61NESHAPSubpart)) {
							PollutantCompCode pcc = new PollutantCompCode();
							pcc.setFpId(facility.getFpId());
							pcc.setPollutantCd(part61NESHAPSubpart);
							neshapPollutantCompCds.add(pcc);
						}
					}
					facility.setNeshapsSubpartsCompCds(neshapPollutantCompCds);
				}
	
				// Set Part 63 NESHAP (MACT) flag and SubParts
				if (permit.isPart63NESHAP()) {
					if (!facility.isMact())
						facility.setMact(true);
					// Update subparts
					List<String> permitPart63NESHAPSubparts = permit.getPart63NESHAPSubpartCDs();
					List<String> facilityMACTSubparts = facility.getMactSubparts();
					for (String part63NESHAPSubpart : permitPart63NESHAPSubparts) {
						if (!facilityMACTSubparts.contains(part63NESHAPSubpart))
							facilityMACTSubparts.add(part63NESHAPSubpart);
					}
					facility.setMactSubparts(facilityMACTSubparts);
				}
	
				// Set NSPS flag and SubParts
				if (permit.isNsps()) {
					if (!facility.isNsps())
						facility.setNsps(true);
					// Update subparts
					List<String> permitNSPSSubparts = permit.getNspsSubpartCDs();
					List<String> facilityNSPSSubparts = facility.getNspsSubparts();
					for (String nspsSubpart : permitNSPSSubparts) {
						if (!facilityNSPSSubparts.contains(nspsSubpart))
							facilityNSPSSubparts.add(nspsSubpart);
					}
					facility.setNspsSubparts(facilityNSPSSubparts);
				}
										
				// Set TIV Acid Rain flag
				if(permit.isAcidRain() && !facility.isTivInd())
					facility.setTivInd(true);
							
				facBO.modifyFacility(facility, userId);
				facBO.modifyFacilityFedRulesAndRegsSubparts(facility, trans);		
			
		} catch (Exception e) {
			throw new DAOException(
					"Problem updating final Title V permit modification. "
							+ e.getMessage(), e);
		}
	}

	/**
	 * Handle facility, permit, and EU updates specific to PBRs. See LSRD
	 * Worksheet "tables" 5163D:5166G "PBR Workflow Completes (assuming this
	 * includes a permit revocation if it was needed).
	 * 
	 * @param permit
	 *            Permit modification to finalize.
	 * @param facility
	 *            Facility to be updated on finalization of permit.
	 * @param facBO
	 *            Facility business object.
	 * 
	 * @throws DAOException
	 *             If anything goes wrong.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	/*
	public void updateFinalPBR(Permit permit, int userId) throws DAOException {

		try {
			PermitDAO permitDAO = permitDAO();
			Facility facility = null;
			FacilityService facBO = null;

			if (PermitGlobalStatusDef.ISSUED_FINAL.equals(permit
					.getPermitGlobalStatusCD())) {

				// Fetch the latest version of the facility inventory.
				facBO = ServiceFactory.getInstance().getFacilityService();
				facility = getEditableCurrent(facBO, permit.getFacilityId(),
						userId);
				if (facility == null) {
					throw new NullPointerException(
							"Could not locate facility for " + "Facility ID = "
									+ permit.getFacilityId()
									+ ", FacilityID = "
									+ permit.getFacilityId());
				}

				for (PermitEUGroup eug : permit.getEuGroups()) {

					for (PermitEU peu : eug.getPermitEUs()) {

						peu.setPermitStatusCd(PermitStatusDef.ACTIVE);

						EmissionUnit eu = facility.getMatchingEmissionUnit(peu
								.getCorrEpaEMUID());
						if (eu == null) {
							throw new NullPointerException(
									"Could not locate Emission Unit for "
											+ "Permit EU ID = "
											+ peu.getPermitEUID());
						}

						// 2457 Note 5579
						if (!PermitClassDef.TV.equals(facility
								.getPermitClassCd())
								|| facility.getPermitClassCd() == null) {
							// Current PTO/PTIO/Title V regulatory status'
							// should change
							// to Permit-By-Rule.
							eu.setPtioStatusCd(PTIORegulatoryStatus.PBR);
						}

						// Current PTI regulatory status' should change to PBR.
						eu.setPtiStatusCd(PTIRegulatoryStatus.PBR);

						// Set 'Exempt Status' to "N/A"
						eu.setExemptStatusCd(ExemptStatusDef.NA);

						facBO.modifyEmissionUnit(eu, userId);

						// Mantis 2981 - supersede PBR EUs and REG EUs that were
						// affected by a previous permit
						//
						// Remove PBR references in permit applications
						// PermitEU[] pbrEus =
						// permitDAO().searchPermitEUsAcrossPermits
						// (eu.getCorrEpaEmuId(), true, PermitTypeDef.PBR);
						//
						PermitEU[] regEus = permitDAO()
								.searchPermitEUsAcrossPermits(
										eu.getCorrEpaEmuId(), true,
										PermitTypeDef.REG);

						List<PermitEU> eus = new ArrayList<PermitEU>();
						//
						// Remove PBR references in permit applications for
						// (PermitEU pbrEU : pbrEus) { eus.add(pbrEU); }
						//
						for (PermitEU regEU : regEus) {
							eus.add(regEU);
						}

						for (PermitEU tpeu : eus) {
							// Don't supersede ourself!
							if (tpeu.getPermitEUID()
									.equals(peu.getPermitEUID())) {
								continue;
							}
							tpeu = permitDAO.retrievePermitEU(tpeu
									.getPermitEUID());
							if (tpeu.getPermitStatusCd() == null
									|| tpeu.getPermitStatusCd().equals(
											PermitStatusDef.ACTIVE)
									|| tpeu.getPermitStatusCd().equals(
											PermitStatusDef.EXPIRED)
									|| tpeu.getPermitStatusCd().equals(
											PermitStatusDef.EXTENDED)) {

								tpeu.setPermitStatusCd(PermitStatusDef.SUPERSEDED);
								// The date should be the effective date of
								// permit.
								tpeu.setSupersededDate(permit
										.getEffectiveDate());
								permitDAO.modifyPermitEU(tpeu);
							}
						}

						// DO the same for legacy registration permitS??
					}
				}
			}

			ApplicationService appBO = ServiceFactory.getInstance()
					.getApplicationService();

			for (Application permitApp : permit.getApplications()) {
				permitApp = appBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof PBRNotification) {
					if (PermitGlobalStatusDef.ISSUED_FINAL.equals(permit
							.getPermitGlobalStatusCD())) {
						((PBRNotification) permitApp).setDispositionFlag("a");
					} else if (PermitGlobalStatusDef.ISSUED_DENIAL
							.equals(permit.getPermitGlobalStatusCD())) {
						((PBRNotification) permitApp).setDispositionFlag("d");
					}
					appBO.modifyApplication(permitApp);
				}
			}

			// Chris agrees that PBR should not change any permit data.
			// Except that we do need to store the date the PBR was
			// accepted/denied.
			Timestamp now = new Timestamp(Calendar.getInstance()
					.getTimeInMillis());
			permit.setEffectiveDate(now);
			modifyPermit(permit, userId);

			if (PermitGlobalStatusDef.ISSUED_FINAL.equals(permit
					.getPermitGlobalStatusCD())) {
				boolean allPBR = true;
				for (EmissionUnit e : facility.getEmissionUnits()) {
					if (EuOperatingStatusDef.SD
							.equals(e.getOperatingStatusCd())) {
						continue;
					}
					if (PTIORegulatoryStatus.PBR.equals(e.getPtioStatusCd())
							|| PTIRegulatoryStatus.PBR.equals(e
									.getPtiStatusCd())) {
						continue;
					}
					if (ExemptStatusDef.DE_MINIMIS
							.equals(e.getExemptStatusCd())
							|| ExemptStatusDef.EXEMPT.equals(e
									.getExemptStatusCd())) {
						continue;
					}
					allPBR = false;
				}

				if (allPBR) {
					facility = facBO.retrieveFacility(permit.getFacilityId());
					//
					// bug 2241 The transition status remained "Going to PBR"
					// and should have been "None" (Bad) The Title V Permit
					// Classification remained "Extended" and should have been
					// "N/A" (Bad)
					//
					facility.setTransitStatusCd(TransitStatusDef.NONE);
					facility.setPermitStatusCd(FacPermitStatusDef.NA);
					//facility.setPermitClassCd(PermitClassDef.PBR);
					facBO.modifyFacility(facility, userId);

					EmissionsReportService emRptBO = ServiceFactory
							.getInstance().getEmissionsReportService();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(permit.getEffectiveDate().getTime());
					int year = cal.get(Calendar.YEAR);
					Transaction trans = TransactionFactory.createTransaction();
					emRptBO.updateYearlyReportingInfo(facility.getFacilityId(),
							year, trans, EmissionReportsDef.NONE,
							ReportReceivedStatusDef.NO_REPORT_NEEDED, null,
							null);
				}
			}
		} catch (Exception e) {
			throw new DAOException("Problem updating final PBR. "
					+ e.getMessage(), e);
		}
	}
*/
	private Invoice setupInvoice(Permit permit) {

		if (!(permit instanceof PTIOPermit)) {
			return null;
		}

		Invoice ret = null;
		PTIOPermit tp = (PTIOPermit) permit;

		if (tp.getTotalAmount() != 0) {
			ret = new Invoice();
			ret.setPermitId(permit.getPermitID());
			ret.setInvoiceStateCd(InvoiceState.READY_TO_INVOICE);
			ret.setRevenueTypeCd(RevenueTypeDef.PTI_FEE);
			ret.setCreationDate(permit.getFinalIssueDate());
			ret.setDueDate(Invoice.calculateDueDate(permit.getFinalIssueDate(),
					30));
			ret.setOrigAmount(new Double(tp.getTotalAmount()));
			ret.setFacilityId(tp.getFacilityId());
			ret.setContact(null);// Invoice will initialize this
		}

		return ret;
	}

	/*
	 * Returns the list of EmissionUnit's that can be included in a permit. The
	 * list is built by retrieving all EU's included in all permit applications
	 * referenced by the permit. When two (or more) applications reference
	 * different versions of the same EU (in this context, two EU's are
	 * considered to be the same if they have the same EPA EU ID), only the most
	 * recent version is kept.
	 */
	private Map<String, EmissionUnit> getPermittableEUs(Transaction tx,
			Permit permit) throws DAOException {
		ApplicationDAO appDAO = applicationDAO(tx);

		Map<String, EmissionUnit> euMap = new HashMap<String, EmissionUnit>();
		for (Application app : permit.getApplications()) {
			ApplicationEU[] appEUs = appDAO
					.retrieveApplicationEmissionUnits(app.getApplicationID());
			for (ApplicationEU appEU : appEUs) {
				if (appEU.isExcluded())
					continue;
				EmissionUnit fpEU = appEU.getFpEU();
				EmissionUnit otherFpEU = euMap.get(fpEU.getEpaEmuId());
				if (otherFpEU == null) {
					// make sure same EU is not present with a different name
					boolean matchFound = false;
					EmissionUnit matchFpEU = null;
					for (EmissionUnit tmpEU : euMap.values()) {
						if (tmpEU.getCorrEpaEmuId().equals(
								fpEU.getCorrEpaEmuId())) {
							matchFpEU = tmpEU;
							matchFound = true;
							break;
						}
					}
					if (!matchFound) {
						euMap.put(fpEU.getEpaEmuId(), fpEU);
					} else {
						// use most recent version if two EUs have same
						// corrEpaEmuId
						if (fpEU.getEmuId() > matchFpEU.getEmuId()) {
							euMap.remove(matchFpEU.getEpaEmuId());
							euMap.put(fpEU.getEpaEmuId(), fpEU);
						}
					}
				}
			}
		}
		return euMap;
	}

	/*
     * 
     */
	private void mergePermittableEUs(Transaction tx, PermitInfo retVal)
			throws DAOException {
		Map<String, EmissionUnit> euMap = getPermittableEUs(tx,
				retVal.getPermit());
		/*
		 * for (PermitEUGroup euGroup : retVal.getPermit().getEuGroups()) { for
		 * (PermitEU eu : euGroup.getPermitEUs()) { EmissionUnit fpEU =
		 * eu.getFpEU(); euMap.put(fpEU.getEpaEmuId(), fpEU); } }
		 */

		// Sort EUs.
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(euMap.keySet());
		Collections.sort(keys);
		for (String k : keys)
			retVal.getPermittableEUs().add(euMap.get(k));

	}

	/**
	 * Generate the Multi-media Notification Letter.
	 * 
	 */
	private void prepareMultiMediaLetter(Permit permit, int userId,
			Transaction trans) throws DAOException {

		PermitDocument multiMediaDoc = new PermitDocument();

		TemplateDocument multiMediaTemplate = TemplateDocTypeDef
				.getTemplate(TemplateDocTypeDef.MULTIMEDIA_LETTER);

		if (multiMediaTemplate == null) {
			throw new DAOException(
					"Cannot find template for muti-media letter.");
		}

		String facilityID = permit.getFacilityId();
		Facility facility = facilityDAO(trans).retrieveFacility(facilityID);

		// Set the path elements of the generated doc.
		multiMediaDoc.setFacilityID(permit.getFacilityId());
		multiMediaDoc.setTemporary(false);
		multiMediaDoc.setExtension(DocumentUtil
				.getFileExtension(multiMediaTemplate.getPath()));
		multiMediaDoc.setLastModifiedBy(userId);
		multiMediaDoc.setPermitDocTypeCD(PermitDocTypeDef.MULTIMEDIA_LETTER);
		multiMediaDoc.setDescription("Multi-Media Letter");
		multiMediaDoc.setPermitId(permit.getPermitID());

		try {
			multiMediaDoc.setLastModifiedTS(new Timestamp(System
					.currentTimeMillis()));

			multiMediaDoc.setUploadDate(multiMediaDoc.getLastModifiedTS());
			multiMediaDoc = (PermitDocument) documentDAO(trans).createDocument(
					multiMediaDoc);

			DocumentGenerationBean dgb = new DocumentGenerationBean();
			dgb.setFacility(facility);
			dgb.setPermit(permit);

			// Find the first app that has a description. We will use that for
			// the project
			// description field on the form.
			logger.debug("Looking for apps.");
			for (Application anApp : permit.getApplications()) {
				// anApp =
				// applicationDAO().retrieveApplication(anApp.getApplicationID());
				logger.debug("Found app. ID = " + anApp.getApplicationID()
						+ ", " + "Desc = " + anApp.getApplicationDesc());
				if (anApp != null && anApp.getApplicationDesc() != null
						&& anApp.getApplicationDesc().length() > 0) {
					logger.debug("This app is the one.");
					dgb.setApplication(anApp);
					break;
				}
			}

			/*DocumentUtil.generateDocument(multiMediaTemplate.getPath(), dgb,
					multiMediaDoc.getPath());*/
			DocumentUtil.generateAsposeDocument(multiMediaTemplate.getPath(), dgb, multiMediaDoc.getPath());
		} catch (IOException iex) {
			logger.error(iex.getMessage(), iex);
			throw new DAOException(iex.getMessage(), iex);
		} catch (DocumentGenerationException dex) {
			logger.error(dex.getMessage(), dex);
			throw new DAOException(dex.getMessage(), dex);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage(), e);
		}

		// If there is already an mulit-media letter, replace it with
		// the new document.
		Iterator<PermitDocument> it = permit.getDocuments().iterator();
		while (it.hasNext()) {
			PermitDocument doc = it.next();
			if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.MULTIMEDIA_LETTER)) {
				it.remove();
				break;
			}
		}
		permit.addDocument(multiMediaDoc);
		permitDAO(trans).createPermitDocument(multiMediaDoc);

	}

	/**
	 * Prepares PTIO documents for issuance
	 * 
	 * @deprecated No longer used within IMPACT
	 * @param permit
	 * @param userID
	 * @param issuanceType
	 * @throws DAOException
	 */
	@Deprecated
	private void preparePTIOIssuance(PTIOPermit permit, int userID,
			String issuanceType) throws DAOException {

		PermitDocument introPkgDoc = new PermitDocument();
		TemplateDocument introPkgTemplate = null;
		if (permit.isTv() && issuanceType.equals(PermitIssuanceTypeDef.Draft)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.PTI_DRAFT_ISSUANCE_PKG);
		} else if (permit.isTv()
				&& issuanceType.equals(PermitIssuanceTypeDef.Final)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.PTI_FINAL_ISSUANCE_PKG);
		} else if (!permit.isTv()
				&& issuanceType.equals(PermitIssuanceTypeDef.Draft)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.PTIO_DRAFT_ISSUANCE_PKG);
		} else if (!permit.isTv()
				&& issuanceType.equals(PermitIssuanceTypeDef.Final)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.PTIO_FINAL_ISSUANCE_PKG);
		}
		if (introPkgTemplate == null) {
			throw new DAOException(
					"Cannot find template document for PTI/PTIO issuance.");
		}

		PermitDocument addrLabelDoc = new PermitDocument();
		TemplateDocument addressLabelsTemplate = TemplateDocTypeDef
				.getTemplate(TemplateDocTypeDef.ADDRESS_LABELS);
		if (addressLabelsTemplate == null) {
			throw new DAOException("Cannot find template for address labels.");
		}

		// Generate the intro package.
		introPkgDoc.setLastModifiedBy(userID);
		introPkgDoc.setPermitDocTypeCD(PermitDocTypeDef.INTRO_PACKAGE);
		introPkgDoc.setIssuanceStageFlag(PermitDocIssuanceStageDef
				.getStage(issuanceType));
		introPkgDoc.setDescription(PermitIssuanceTypeDef.getData().getItems()
				.getItemDesc(issuanceType)
				+ " introductory package");
		introPkgDoc.setPermitId(permit.getPermitID());
		introPkgDoc = generateTempDocument(permit, introPkgTemplate,
				introPkgDoc);

		// If there is already an intro package document, replace it with
		// the new document.
		Iterator<PermitDocument> it = permit.getDocuments().iterator();
		while (it.hasNext()) {
			PermitDocument doc = it.next();
			if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.INTRO_PACKAGE)
					&& doc.getIssuanceStageFlag().equals(
							PermitDocIssuanceStageDef.getStage(issuanceType))) {
				it.remove();
				break;
			}
		}
		permit.addDocument(introPkgDoc);

		// Generate the address labels.
		addrLabelDoc.setLastModifiedBy(userID);
		addrLabelDoc.setPermitDocTypeCD(PermitDocTypeDef.ADDRESS_LABELS);
		addrLabelDoc.setIssuanceStageFlag(PermitDocIssuanceStageDef
				.getStage(issuanceType));
		addrLabelDoc.setDescription(PermitIssuanceTypeDef.getData().getItems()
				.getItemDesc(issuanceType)
				+ " address labels");
		addrLabelDoc.setPermitId(permit.getPermitID());
		addrLabelDoc = generateTempDocument(permit, addressLabelsTemplate,
				addrLabelDoc);

		// If there is already an address labels document, replace it with
		// the new document.
		it = permit.getDocuments().iterator();
		while (it.hasNext()) {
			PermitDocument doc = it.next();
			if (doc.getPermitDocTypeCD()
					.equals(PermitDocTypeDef.ADDRESS_LABELS)
					&& doc.getIssuanceStageFlag().equals(
							PermitDocIssuanceStageDef.getStage(issuanceType))) {
				it.remove();
				break;
			}
		}
		permit.addDocument(addrLabelDoc);

		// Generate the fax cover sheet if required.
		if (issuanceType.equals(PermitIssuanceTypeDef.Draft)) {
			PermitDocument faxCoverSheet = new PermitDocument();
			TemplateDocument faxCoverSheetTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.FAX_COVER_SHEET);
			if (faxCoverSheetTemplate == null) {
				throw new DAOException(
						"Cannot find template for fax cover sheet.");
			}
			faxCoverSheet.setLastModifiedBy(userID);
			faxCoverSheet.setPermitDocTypeCD(PermitDocTypeDef.FAX_COVER_SHEET);
			faxCoverSheet.setIssuanceStageFlag(PermitDocIssuanceStageDef
					.getStage(issuanceType));
			faxCoverSheet.setDescription(PermitIssuanceTypeDef.getData()
					.getItems().getItemDesc(issuanceType)
					+ " fax cover sheet");
			faxCoverSheet.setPermitId(permit.getPermitID());
			faxCoverSheet = generateTempDocument(permit, faxCoverSheetTemplate,
					faxCoverSheet);

			// If there is already a fax cover sheet, replace it with the new
			// document.
			it = permit.getDocuments().iterator();
			while (it.hasNext()) {
				PermitDocument doc = it.next();
				if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.FAX_COVER_SHEET)
						&& doc.getIssuanceStageFlag().equals(
								PermitDocIssuanceStageDef
										.getStage(issuanceType))) {
					it.remove();
					break;
				}
			}
			permit.addDocument(faxCoverSheet);
		}

	}

	/**
	 * Prepares TV PTO documents for issuance
	 * 
	 * @deprecated No longer used within IMPACT
	 * @param permit
	 * @param userID
	 * @param issuanceType
	 * @throws DAOException
	 */
	@Deprecated
	private void prepareTVPTOIssuance(TVPermit permit, int userID,
			String issuanceType) throws DAOException {

		PermitDocument introPkgDoc = new PermitDocument();
		TemplateDocument introPkgTemplate = null;
		if (issuanceType.equals(PermitIssuanceTypeDef.Draft)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.TVPTO_DRAFT_ISSUANCE_PKG);
		//} else if (issuanceType.equals(PermitIssuanceTypeDef.PPP)) {
		//	introPkgTemplate = TemplateDocTypeDef
		//			.getTemplate(TemplateDocTypeDef.TVPTO_PPP_ISSUANCE_PKG);
		} else if (issuanceType.equals(PermitIssuanceTypeDef.PP)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.TVPTO_PP_ISSUANCE_PKG);
		} else if (issuanceType.equals(PermitIssuanceTypeDef.Final)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.TVPTO_FINAL_ISSUANCE_PKG);
		}
		if (introPkgTemplate == null) {
			throw new DAOException(
					"Cannot find template document for Title V Permit issuance.");
		}
		PermitDocument addrLabelDoc = new PermitDocument();
		TemplateDocument addressLabelsTemplate = TemplateDocTypeDef
				.getTemplate(TemplateDocTypeDef.ADDRESS_LABELS);
		if (addressLabelsTemplate == null) {
			throw new DAOException("Cannot find template for address labels.");
		}

		// Generate the intro package.
		introPkgDoc.setLastModifiedBy(userID);
		introPkgDoc.setPermitDocTypeCD(PermitDocTypeDef.INTRO_PACKAGE);
		introPkgDoc.setIssuanceStageFlag(PermitDocIssuanceStageDef
				.getStage(issuanceType));
		introPkgDoc.setDescription(PermitIssuanceTypeDef.getData().getItems()
				.getItemDesc(issuanceType)
				+ " introductory package");
		introPkgDoc.setPermitId(permit.getPermitID());
		introPkgDoc = generateTempDocument(permit, introPkgTemplate,
				introPkgDoc);

		// If there is already an intro package document, replace it with the
		// new document.
		Iterator<PermitDocument> it = permit.getDocuments().iterator();
		while (it.hasNext()) {
			PermitDocument doc = it.next();
			if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.INTRO_PACKAGE)
					&& doc.getIssuanceStageFlag().equals(
							PermitDocIssuanceStageDef.getStage(issuanceType))) {
				it.remove();
				break;
			}
		}
		permit.addDocument(introPkgDoc);

		// Generate the address labels.
		addrLabelDoc.setLastModifiedBy(userID);
		addrLabelDoc.setPermitDocTypeCD(PermitDocTypeDef.ADDRESS_LABELS);
		addrLabelDoc.setIssuanceStageFlag(PermitDocIssuanceStageDef
				.getStage(issuanceType));
		addrLabelDoc.setDescription(PermitIssuanceTypeDef.getData().getItems()
				.getItemDesc(issuanceType)
				+ " address labels");
		addrLabelDoc.setPermitId(permit.getPermitID());
		addrLabelDoc = generateTempDocument(permit, addressLabelsTemplate,
				addrLabelDoc);

		// If there is already an address labels document, replace it with the
		// new document.
		it = permit.getDocuments().iterator();
		while (it.hasNext()) {
			PermitDocument doc = it.next();
			if (doc.getPermitDocTypeCD()
					.equals(PermitDocTypeDef.ADDRESS_LABELS)
					&& doc.getIssuanceStageFlag().equals(
							PermitDocIssuanceStageDef.getStage(issuanceType))) {
				it.remove();
				break;
			}
		}
		permit.addDocument(addrLabelDoc);

		// Generate the fax cover sheet if required.
		if (issuanceType.equals(PermitIssuanceTypeDef.Draft)) {
			PermitDocument faxCoverSheet = new PermitDocument();
			TemplateDocument faxCoverSheetTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.FAX_COVER_SHEET);
			if (faxCoverSheetTemplate == null) {
				throw new DAOException(
						"Cannot find template for fax cover sheet.");
			}
			faxCoverSheet.setLastModifiedBy(userID);
			faxCoverSheet.setPermitDocTypeCD(PermitDocTypeDef.FAX_COVER_SHEET);
			faxCoverSheet.setIssuanceStageFlag(PermitDocIssuanceStageDef
					.getStage(issuanceType));
			faxCoverSheet.setDescription(PermitIssuanceTypeDef.getData()
					.getItems().getItemDesc(issuanceType)
					+ " fax cover sheet");
			faxCoverSheet.setPermitId(permit.getPermitID());
			faxCoverSheet = generateTempDocument(permit, faxCoverSheetTemplate,
					faxCoverSheet);

			// If there is already a fax cover sheet, replace it with the new
			// document.
			it = permit.getDocuments().iterator();
			while (it.hasNext()) {
				PermitDocument doc = it.next();
				if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.FAX_COVER_SHEET)
						&& doc.getIssuanceStageFlag().equals(
								PermitDocIssuanceStageDef
										.getStage(issuanceType))) {
					it.remove();
					break;
				}
			}
			permit.addDocument(faxCoverSheet);
		}

	}
/*
	private void prepareTIVPTOIssuance(Permit permit, int userID,
			String issuanceType) throws DAOException {

		PermitDocument introPkgDoc = new PermitDocument();
		TemplateDocument introPkgTemplate = null;
		if (issuanceType.equals(PermitIssuanceTypeDef.Draft)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.TIVPTO_DRAFT_ISSUANCE_PKG);
		//} else if (issuanceType.equals(PermitIssuanceTypeDef.PPP)) {
		//	introPkgTemplate = TemplateDocTypeDef
		//			.getTemplate(TemplateDocTypeDef.TIVPTO_PPP_ISSUANCE_PKG);
		} else if (issuanceType.equals(PermitIssuanceTypeDef.PP)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.TIVPTO_PP_ISSUANCE_PKG);
		} else if (issuanceType.equals(PermitIssuanceTypeDef.Final)) {
			introPkgTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.TIVPTO_FINAL_ISSUANCE_PKG);
		}
		if (introPkgTemplate == null) {
			throw new DAOException(
					"Cannot find template document for Title IV Permit issuance.");
		}
		PermitDocument addrLabelDoc = new PermitDocument();
		TemplateDocument addressLabelsTemplate = TemplateDocTypeDef
				.getTemplate(TemplateDocTypeDef.ADDRESS_LABELS);
		if (addressLabelsTemplate == null) {
			throw new DAOException("Cannot find template for address labels.");
		}

		// Generate the intro package.
		introPkgDoc.setLastModifiedBy(userID);
		introPkgDoc.setPermitDocTypeCD(PermitDocTypeDef.INTRO_PACKAGE);
		introPkgDoc.setIssuanceStageFlag(PermitDocIssuanceStageDef
				.getStage(issuanceType));
		introPkgDoc.setDescription(PermitIssuanceTypeDef.getData().getItems()
				.getItemDesc(issuanceType)
				+ " introductory package");
		introPkgDoc.setPermitId(permit.getPermitID());
		introPkgDoc = generateTempDocument(permit, introPkgTemplate,
				introPkgDoc);

		// If there is already an intro package document, replace it with the
		// new document.
		Iterator<PermitDocument> it = permit.getDocuments().iterator();
		while (it.hasNext()) {
			PermitDocument doc = it.next();
			if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.INTRO_PACKAGE)
					&& doc.getIssuanceStageFlag().equals(
							PermitDocIssuanceStageDef.getStage(issuanceType))) {
				it.remove();
				break;
			}
		}
		permit.addDocument(introPkgDoc);

		// Generate the address labels.
		addrLabelDoc.setLastModifiedBy(userID);
		addrLabelDoc.setPermitDocTypeCD(PermitDocTypeDef.ADDRESS_LABELS);
		addrLabelDoc.setIssuanceStageFlag(PermitDocIssuanceStageDef
				.getStage(issuanceType));
		addrLabelDoc.setDescription(PermitIssuanceTypeDef.getData().getItems()
				.getItemDesc(issuanceType)
				+ " address labels");
		addrLabelDoc.setPermitId(permit.getPermitID());
		addrLabelDoc = generateTempDocument(permit, addressLabelsTemplate,
				addrLabelDoc);

		// If there is already an address labels document, replace it with the
		// new document.
		it = permit.getDocuments().iterator();
		while (it.hasNext()) {
			PermitDocument doc = it.next();
			if (doc.getPermitDocTypeCD()
					.equals(PermitDocTypeDef.ADDRESS_LABELS)
					&& doc.getIssuanceStageFlag().equals(
							PermitDocIssuanceStageDef.getStage(issuanceType))) {
				it.remove();
				break;
			}
		}
		permit.addDocument(addrLabelDoc);

		// Generate the fax cover sheet if required.
		if (issuanceType.equals(PermitIssuanceTypeDef.Draft)) {
			PermitDocument faxCoverSheet = new PermitDocument();
			TemplateDocument faxCoverSheetTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.FAX_COVER_SHEET);
			if (faxCoverSheetTemplate == null) {
				throw new DAOException(
						"Cannot find template for fax cover sheet.");
			}
			faxCoverSheet.setLastModifiedBy(userID);
			faxCoverSheet.setPermitDocTypeCD(PermitDocTypeDef.FAX_COVER_SHEET);
			faxCoverSheet.setIssuanceStageFlag(PermitDocIssuanceStageDef
					.getStage(issuanceType));
			faxCoverSheet.setDescription(PermitIssuanceTypeDef.getData()
					.getItems().getItemDesc(issuanceType)
					+ " fax cover sheet");
			faxCoverSheet.setPermitId(permit.getPermitID());
			faxCoverSheet = generateTempDocument(permit, faxCoverSheetTemplate,
					faxCoverSheet);

			// If there is already a fax cover sheet, replace it with the new
			// document.
			it = permit.getDocuments().iterator();
			while (it.hasNext()) {
				PermitDocument doc = it.next();
				if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.FAX_COVER_SHEET)
						&& doc.getIssuanceStageFlag().equals(
								PermitDocIssuanceStageDef
										.getStage(issuanceType))) {
					it.remove();
					break;
				}
			}
			permit.addDocument(faxCoverSheet);
		}

	}
	*/

	/**
	 * Returns all of the reasons currently defined in the system for the
	 * permitType.
	 * 
	 * @return SimpleDef[] All reasons of this type.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrievePermitReasons(String permitType)
			throws DAOException {
		return permitDAO().retrievePermitReasons(permitType);
	}

	/**
	 * Returns all of the reasons currently defined in the system.
	 * 
	 * @return SimpleDef[] All reasons of this type.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveAllPermitReasons() throws DAOException {
		return permitDAO().retrieveAllPermitReasons();
	}

	/**
	 * Returns all of the reasons currently defined in the system.
	 * 
	 * @return SimpleDef[] All reasons of this type.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveAllPermitTypes() throws DAOException {
		return permitDAO().retrieveAllPermitTypes();
	}
	
	/**
	 * Returns all of the permit comments by permit ID.
	 * 
	 * @param int The permit ID
	 * 
	 * @return Note[] All comments of this permit.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Note[] retrievePermitComments(int permitID) throws DAOException {
		return permitDAO().retrieveDapcComments(permitID);
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PermitNote createPermitNote(PermitNote permitNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		PermitNote ret = null;

		try {
			ret = createPermitNote(permitNote, trans);

			if (ret != null) {
				trans.complete();
			} else {
				trans.cancel();
				logger.error("Failed to insert Facility Note");
			}
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param permitNote
	 * @param trans
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public PermitNote createPermitNote(PermitNote permitNote, Transaction trans)
			throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		PermitDAO permitDAO = permitDAO(trans);

		Note tempNote = infraDAO.createNote(permitNote);

		if (tempNote != null) {
			permitNote.setNoteId(tempNote.getNoteId());

			permitDAO.createPermitNote(permitNote.getPermitId(),
					permitNote.getNoteId());
		} else
			permitNote = null;
		return permitNote;
	}

	/**
	 * @param permit
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void unDeadendPermit(Permit permit) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		WorkFlowDAO wfDAO = workFlowDAO(trans);
		PermitDAO permitDAO = permitDAO(trans);

		try {
			ProcessActivity[] acts = null;
			try {
				acts = wfDAO
						.retrieveActivitiesToUnDeadend(permit.getPermitID());
			} catch (DAOException e) {
				cancelTransaction(trans, e);
			}

			List<PermitIssuance> issues = null;
			try {
				issues = permitDAO.retrieveLatestPermitIssuanceFinal(permit
						.getPermitID());
			} catch (DAOException e) {
				cancelTransaction(trans, e);
			}

			WorkFlowProcess p = null;
			if (acts.length > 0) { // we are guaranteed to have some steps that
									// were cancelled
									// otherwise the workflow could not have
									// been cancelled.
				Integer processId = acts[0].getProcessId();
				p = wfDAO.retrieveProcess(processId);
				p.setEndDt(null);
				wfDAO.modifyProcess(p);
			}

			// first update workflow process activites in case of partial
			// failure.
			for (ProcessActivity pa : acts) {
				pa.setEndDt(null);
				if (pa.getStartDt() != null)
					pa.setActivityStatusCd(ActivityStatusDef.IN_PROCESS);
				else
					pa.setActivityStatusCd(ActivityStatusDef.NOT_COMPLETED);
				try {
					wfDAO.modifyProcessActivity(pa);
				} catch (DAOException e) {
					cancelTransaction(trans, e);
				}
			}

			permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.NONE);
			if (issues != null && issues.size() >= 1) {
				permit.setPermitGlobalStatusCD(issues.get(0)
						.getIssuanceTypeCd());
			}
			permitDAO.modifyMakePermitUndead(permit);
		} finally {
			closeTransaction(trans);
		}
		trans.complete();
	}

	/**
	 * 
	 * @return boolean <tt>true</tt> if a record was updated.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyPermitNote(PermitNote permitNote) throws DAOException {
		return infrastructureDAO().modifyNote(permitNote);
	}

	/**
	 * @param ps
	 * @param issuanceType
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public List<ValidationMessage> finalizeIssuances(List<Integer> pIDs,
			String issuanceType, boolean updateProfile, int userId, User user)

	throws DAOException {

		List<ValidationMessage> retVal = new ArrayList<ValidationMessage>();
		List<Integer> failedPIDs = new ArrayList<Integer>();

		for (Integer pID : pIDs) {
			try {
				PermitService permitBO = ServiceFactory.getInstance()
						.getPermitService();

				ValidationMessage vm = permitBO.finalizeIssuance(pID,
						issuanceType, updateProfile, userId, user);
				if (vm != null) {
					retVal.add(vm);
				}
			} catch (Exception e) {
				ValidationMessage vm = new ValidationMessage("Permit",
						e.getMessage(), ValidationMessage.Severity.ERROR, null);
				retVal.add(vm);
				failedPIDs.add(pID);
				logger.error("Failed to finalize permit " + pID + ".", e);
			}
		}
		for (Integer pID : failedPIDs) {
			pIDs.remove(pID);
		}

		return retVal;
	}

	/**
	 * 
	 * 
	 * @return SimpleDef[] .
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveModelGeneralPermitDefs(String generalPermitTypeCd)
			throws DAOException {
		return permitDAO().retrieveModelGeneralPermitDefs(generalPermitTypeCd);
	}

	/**
	 * @param facilityEUID
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	/*
	public SimpleIdDef[] retrieveSupersedablePermits(Integer facilityEUID,
			String permitType) throws DAOException {

		SimpleIdDef[] ret = permitDAO().retrieveSupersedablePermits(
				facilityEUID);
		List<SimpleIdDef> si = new ArrayList<SimpleIdDef>();
		// bug 2032
		for (SimpleIdDef s : ret)
			if (PermitTypeDef.NSR.equals(permitType)
					&& (s.getDescription().contains(PermitTypeDef.TVPTI)
							|| s.getDescription().contains(PermitTypeDef.NSR)
							|| s.getDescription().contains(PermitTypeDef.REG) || s
							.getDescription().contains(PermitTypeDef.SPTO)))
				// All EUs included in that issued PTIO which are present in
				// other Issued Final PTIs, Issued Final Legacy State PTOs,
				// Issued Final PTIOs or Issued Final Legacy Registrations
				si.add(s);
			else if (PermitTypeDef.TVPTI.equals(permitType)
					&& (s.getDescription().contains(PermitTypeDef.TVPTI) || s
							.getDescription().contains(PermitTypeDef.NSR)))
				// All EUs included in that issued PTI which are present in
				// other Issued Final PTIs or Issued Final PTIOs
				si.add(s);
			else if (PermitTypeDef.TV_PTO.equals(permitType)
					&& (s.getDescription().contains(PermitTypeDef.SPTO)
							|| s.getDescription().contains(PermitTypeDef.REG) || s
							.getDescription().contains(PermitTypeDef.TV_PTO)))
				// ALL EUs belonging to other Issued Final Title V Permits,
				// Issued
				// Final Legacy State PTOs, or Issued Final Legacy Registrations
				si.add(s);

		ret = si.toArray(new SimpleIdDef[0]);
		return ret;
	}
	*/

	public SimpleIdDef[] retrieveSupersedablePermits(Integer facilityEUID,
			String permitType) throws DAOException {

		SimpleIdDef[] ret = permitDAO().retrieveSupersedablePermits(
				facilityEUID);
		List<SimpleIdDef> si = new ArrayList<SimpleIdDef>();
		// bug 2032
		for (SimpleIdDef s : ret)
			if (PermitTypeDef.NSR.equals(permitType)
					&& (s.getDescription().contains(PermitTypeDef.NSR))) {
				// All EUs included in that issued NSR which are present in
				// Issued Final NSRs
				si.add(s);
			} else if (PermitTypeDef.TV_PTO.equals(permitType)
					&& (s.getDescription().contains(PermitTypeDef.TV_PTO))) {
				// ALL EUs belonging to other Issued Final Title V Permits,
				si.add(s);
			}
		ret = si.toArray(new SimpleIdDef[0]);
		return ret;
	}
	/**
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveSupersedableTVPermits(String facilityId)
			throws DAOException {
		return permitDAO().retrieveSupersedableTVPermits(facilityId);
	}

//	private Correspondence createCorrespondence(Correspondence correspondence,
//			Transaction trans) throws DAOException, ValidationException {
//
//		// Check internal validation of all of the pieces and parts.
//		ArrayList<ValidationMessage> validMsgs = new ArrayList<ValidationMessage>();
//		boolean failsBORules = false;
//		if (!correspondence.isValid()) {
//			validMsgs.addAll(correspondence.getValidationMessages().values());
//			failsBORules = true;
//		}
//
//		for (Note note : correspondence.getNotes()) {
//			if (!note.isValid()) {
//				validMsgs.addAll(note.getValidationMessages().values());
//				failsBORules = true;
//			}
//		}
//
//		if (failsBORules) {
//			throw new ValidationException(
//					"Cannot create new correspondence. Correspondence is not valid",
//					null, validMsgs.toArray(new ValidationMessage[0]));
//		}
//
//		// Objects are valid so go ahead and try to create in db.
//		CorrespondenceDAO cDao = correspondenceDAO(trans);
//		InfrastructureDAO iDao = infrastructureDAO(trans);
//		Correspondence ret = null;
//
//		ret = cDao.createCorrespondence(correspondence);
//		ret.setDirty(false);
//		for (Note note : correspondence.getNotes()) {
//			note = iDao.createNote(note);
//			cDao.createNoteXref(ret.getCorrespondenceID(), note.getNoteId());
//		}
//
//		return ret;
//	}

	/**
	 * 
	 * Create the file system subdirectory for a permit.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void createPermitDir(Permit permit) throws IOException {

		String path = File.separator + "Facilities" + File.separator
				+ permit.getFacilityId() + File.separator + "Permits"
				+ File.separator + permit.getPermitID().toString();
		DocumentUtil.mkDir(path);

	}

	/**
	 * Remove the file system subdirectory for a permit.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void removePermitDir(Permit permit) throws IOException {

		String path = File.separator + "Facilities" + File.separator
				+ permit.getFacilityId() + File.separator + "Permits"
				+ File.separator + permit.getPermitID().toString();
		DocumentUtil.rmDir(path);

	}

	/**
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveRPRPermitList(String facilityId)
			throws DAOException {
		return permitDAO().retrieveRPRPermitList(facilityId);
	}

	/**
	 * @param permitDocuents
	 * @param permitNbr
	 * @param userId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String zipPermitDocs(PermitDocument[] docs, String permitNbr,
			String stage, int userId, Document appDoc) throws DAOException {

		String urlPath = null;

		try {
			UserDef currentUser = infrastructureDAO().retrieveUserDef(userId);
			String userName = currentUser.getNetworkLoginNm();

			DocumentService docBO = ServiceFactory.getInstance()
					.getDocumentService();

			String tmpDirName = docBO.createTmpDir(userName);
			String zipFilePath = File.separator + "tmp" + File.separator
					+ userName + File.separator + permitNbr + "-" + stage
					+ ".zip";
			urlPath = zipFilePath.replace('\\', '/');

			for (PermitDocument doc : docs) {
				String newName = "unknownType";
				if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.INTRO_PACKAGE)) {
					newName = "1 Cover Letter to Section A";
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.TERMS_CONDITIONS)) {
					newName = "2 Section B and C";
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.DRAFT_STATEMENT_BASIS)) {
					newName = "SOB";
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.NSR_RESPONSE_TO_COMMENTS) || doc.getPermitDocTypeCD().equals(PermitDocTypeDef.TV_RESPONSE_TO_COMMENTS)) {
					newName = "Response to Comments";
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.PERMIT_STRATEGY_SUMMARY_WRITE_UP)) {
					newName = "Permit Strategy Write-Up";
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.ADDRESS_LABELS)) {
					newName = "Mailing Sheets";
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.INVOICE)) {
					newName = "Invoice";
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.FAX_COVER_SHEET)) {
					newName = "Fax Cover PN";
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.PUBLIC_NOTICE)) {
					newName = "Public Notice";
				} else {
					continue;
				}
				DocumentUtil.copyDocument(doc.getPath(), tmpDirName
						+ File.separator + newName + "." + doc.getExtension());
			}

			// 2456 Zip Documents Button on Title IV Acid Rain Permit Issuance
			// Screens
			if (appDoc != null)
				DocumentUtil.copyDocument(appDoc.getPath(), tmpDirName
						+ File.separator + "Title IV Application" + "."
						+ appDoc.getExtension());

			DocumentUtil.createZipFile(tmpDirName, zipFilePath, permitNbr + "-"
					+ stage);
			DocumentUtil.rmDir(tmpDirName);
		} catch (Exception e) {
			logger.error("Unexpected exception: " + e.getMessage(), e);
			throw new DAOException("Unexpected exception: " + e.getMessage(), e);
		}
		return urlPath;
	}

	/**
	 * 
	 * @param pa
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */

	public PermitActivitySearch[] searchPermitActivity(PermitActivitySearch pa)
			throws DAOException {
		return permitDAO().searchPermitActivity(pa);
	}

	private Facility getEditableCurrent(FacilityService facBO,
			String facilityId, int userId) throws DAOException {
		Facility f = null;
		try {
			Facility f1 = facBO.retrieveFacility(facilityId);
			if (f1 != null) {
				f = facBO.retrieveFacilityEditable(f1.getFpId(), userId);
			}
		} catch (RemoteException re) {
			logger.error("Remote exception: " + re.getMessage(), re);
			throw new DAOException("Remote exception: " + re.getMessage(), re);
		}
		return f;
	}

	private Facility getEditableCurrent(FacilityService facBO,
			String facilityId, int userId, Transaction trans)
			throws DAOException {
		Facility f = null;
		try {
			Facility f1 = facBO.retrieveFacility(facilityId);
			if (f1 != null) {
				f = facBO.retrieveFacilityEditable(f1.getFpId(), userId, trans);
			}
		} catch (RemoteException re) {
			logger.error("Remote exception: " + re.getMessage(), re);
			throw new DAOException("Remote exception: " + re.getMessage(), re);
		}
		return f;
	}

	/**
	 * Retrieve permit documents by the retrievePermitIssuanceDocuments SQL in
	 * PermitSQL.xml
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document[] retrievePermitIssuanceDocuments() throws DAOException {
		return permitDAO().retrievePermitIssuanceDocuments();
	}

	/**
	 * This method should be called whenever an EU is shutdown. It will mark the
	 * associated permit EU records as terminated.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void terminatePermitEUsForShutdownEU(Facility facility,
			EmissionUnit eu) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			PermitDAO permitDAO = permitDAO(trans);
			List<Permit> permits = permitDAO.searchAllEuPermits(eu
					.getCorrEpaEmuId());
			for (Permit permit : permits) {
				// permit EU termiation date is set to the EU shutdown
				// notification date
				// if the permit is a TVPTO or if the permit is a TVPTI and the
				// facility is tv
				// termination date is set to EU shutdown date if neither of
				// these conditions
				// is true or if the EU shutdown notification date is null
				Timestamp terminationDate = eu.getEuShutdownDate();
				if (PermitTypeDef.TV_PTO.equals(permit.getPermitType())
						//|| (PermitTypeDef.TVPTI.equals(permit.getPermitType()) && PermitClassDef.TV
						//		.equals(facility.getPermitClassCd())
								) {
					if (eu.getEuShutdownNotificationDate() != null) {
						terminationDate = eu.getEuShutdownNotificationDate();
					}
				}

				// find permit EUs that match the shutdown EU and mark them as
				// terminated
				for (PermitEUGroup euGroup : permit.getEuGroups()) {
					for (PermitEU permitEU : euGroup.getPermitEUs()) {
						if (permitEU.getCorrEpaEMUID().equals(
								eu.getCorrEpaEmuId())
								&& (PermitStatusDef.ACTIVE.equals(permitEU
										.getPermitStatusCd())
										|| PermitStatusDef.EXPIRED
												.equals(permitEU
														.getPermitStatusCd()) || PermitStatusDef.EXTENDED
											.equals(permitEU
													.getPermitStatusCd()))) {
							permitEU.setPermitStatusCd(PermitStatusDef.TERMINATED);
							permitEU.setTerminatedDate(terminationDate);
							permitDAO.modifyPermitEU(permitEU);
						}
					}
				}
			}
			trans.complete();
		} catch (RemoteException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	private void generatePDFIssuanceDoc(Document pDoc) throws IOException {

		FileConverter fc = new FileConverter();
		if (fc.init()) {
			if (!fc.convertFile(pDoc, true)) {
				logger.error("Issuance document is not converted to PDF.  DOC ID : "
						+ pDoc.getDocumentID());
			}
		} else {
			logger.error("Failed initializing FileConverter");
		}
	}
	
	public void removePermitDocument(PermitDocument permitDoc) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
			
		try {
				PermitDAO permitDAO = permitDAO(trans);
				permitDAO.removePermitDocument(permitDoc);
				DocumentDAO docDAO = documentDAO(trans);
				permitDoc.setTemporary(true);
			    docDAO.modifyDocument(permitDoc);
				
				trans.complete();
		} catch (DAOException de) {
			DisplayUtil.displayError("Error while removing permit document");
			logger.error(de.getMessage());
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}
	
	public void savePermitApplications (Permit permit) throws DAOException
	{
		Transaction trans = TransactionFactory.createTransaction();
		try	{
			PermitDAO permitDAO = permitDAO(trans);
			permitDAO.removePermitApplications(permit.getPermitID());
			permitDAO.addPermitApplications(permit.getPermitID(),
			permit.getApplications());
		} catch (DAOException de) {
			DisplayUtil.displayError("Error while removing permit application");
			logger.error(de.getMessage());
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}
	
	public void removePermitApplication(Application permitApp)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		PermitDAO permitDAO = permitDAO(trans);

		try {
			Integer applicationId = permitApp.getApplicationID();
			permitDAO.removePermitApplication(applicationId);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean isDuplicatePermitNumber(String permitNumber) throws DAOException {
		return permitDAO().isDuplicatePermitNumber(permitNumber);
	}
	public boolean isDuplicateLegacyPermitNumber(String legacyPermitNumber, String permitNumber) throws DAOException {
		return permitDAO().isDuplicateLegacyPermitNumber(legacyPermitNumber,permitNumber);
	}
	
	private String getNewPermitNumber(Transaction trans) throws DAOException {
		String permitNumber = null;
		int nextIdSeqNum = -1;
		String formatNextIdSeqNum = null;
		PermitDAO permitDAOObj = permitDAO(trans);

		try {
			while (true) {
				nextIdSeqNum = permitDAOObj.generatePermitSeqNo();
				if (nextIdSeqNum > 9999999) {
					DAOException e = new DAOException("Cannot get any more Permit Number. Maximum Sequence ID number reached.");
					throw e;
				}

				formatNextIdSeqNum = String.format("%07d", Integer.valueOf(nextIdSeqNum));
				permitNumber = "P" + formatNextIdSeqNum;
				if (!permitDAOObj.isDuplicatePermitNumber(permitNumber)) {
					break;
				}
			}
		} catch (DAOException e) {
			throw e;
		}

		return permitNumber;
	}
	
	/**
	 * Get a new Permit Number.
	 * 
	 * @return Permit Number
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String getNewPermitNumber() throws DAOException {
		String permitNumber = null;
		Transaction trans = TransactionFactory.createTransaction();

		try {
			permitNumber = getNewPermitNumber(trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return permitNumber;
	}

	@Override
	public List<PermitWorkflowSearchResult> searchPermitWorkflows(String facilityId, String facilityNm,
			String permitNumber, String applicationNumber, Integer userId,
			String permitType, String activityNm, String activityStatusCd,
			Date startDateFrom, Date startDateTo, Date endDateFrom,
			Date endDateTo, Integer processId, boolean filterSkipped, 
			boolean filterNonStarted) throws DAOException, RemoteException {

		return permitDAO().searchPermitWorkflows(facilityId, facilityNm, 
				permitNumber, applicationNumber, userId, permitType, 
				activityNm, activityStatusCd, startDateFrom, startDateTo, 
				endDateFrom, endDateTo, processId, filterSkipped, filterNonStarted);
	}
	
	
	public PermitWorkflowActivityBenchmarkDef[] retrievePermitWorkflowActivityBenchmarkDefs()
			throws DAOException {
		return permitDAO().retrievePermitWorkflowActivityBenchmarkDefs();
	}

	public List<PermitChargePayment> retrievePermitChargePaymentList(
			Integer permitID) throws DAOException {

		List<PermitChargePayment> pcpList = null;

		try {

			pcpList = permitDAO().retrievePermitChargePaymentList(permitID);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while retrieving Permit Charge Payments");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while retrieving Permit Charge Payments");
			logger.error(e.getMessage());
		} finally {

		}

		return pcpList;

	}

	public BigDecimal retrievePermitChargePaymentTotal(
			Integer permitID) throws DAOException {

		BigDecimal amount = BigDecimal.ZERO;

		try {

			amount = permitDAO().retrievePermitChargePaymentTotal(permitID);
		
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while retrieving Permit Charge Payment Total");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while retrieving Permit Charge Payment Total");
			logger.error(e.getMessage());
		} finally {

		}

		return amount;

	}

	public void createPermitChargePayment(PermitChargePayment pcp)
			throws DAOException {
		try {

			permitDAO().createPermitChargePayment(pcp);
			
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while creating Permit Charge Payment");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while creating Permit Charge Payments");
			logger.error(e.getMessage());
		} finally {

		}

	}

	public void modifyPermitChargePayment(PermitChargePayment pcp)
			throws DAOException {
		try {

			permitDAO().modifyPermitChargePayment(pcp);
			
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while modifying Permit Charge Payment");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while modifying Permit Charge Payments");
			logger.error(e.getMessage());
		} finally {

		}

	}

	public final void removePermitChargePayment(PermitChargePayment pcc)
			throws DAOException {
		try {

			permitDAO().removePermitChargePayment(pcc);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while removing Permit Charge Payments");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while removing Permit Charge Payments");
			logger.error(e.getMessage());
		} finally {

		}
	}
	
	public TimeSheetRow[] retrieveTimeSheetHours(Permit permit) {
		String permitApplicationNumbers = permit.getApplicationNumbers();
		String permitNumber = permit.getPermitNumber();
		TimeSheetRow[] timeSheetRows = null;
		try {
			// get the time sheet rows from AQDS OR IMPACT
			if(isTimesheetEntryEnabled()) {
				timeSheetRows = permitDAO().retrieveTimeSheetHoursFromImpact(permitApplicationNumbers, permitNumber);
			} else {
				timeSheetRows = permitDAO().retrieveTimeSheetHoursFromAqds(permitApplicationNumbers, permitNumber);
			}
			
			// get the billable rates from IMPACT definition list
			NSRBillingBillableRateDef[] billableRates = permitDAO().retrieveBillableRatesDef();
			
			// update the time sheet rows with hourly rate information retrieved from IMPACT definition list
			for(TimeSheetRow tsr : timeSheetRows) {
				// get the hourly rate effective on the date this time sheet was enterted into AQDS
				Timestamp tsDate = tsr.getDate();
				if(null != tsDate) {
					float hourlyRate = getBillableHourlyRate(tsDate, billableRates);
					tsr.setHourlyRate(hourlyRate);
					tsr.setAmount(hourlyRate * tsr.getHours());
				} else {
					DisplayUtil.displayWarning("No date was entered for following time sheet record ID in AQDS: " + tsr.getHoursEntryId());
					tsr.setHourlyRate(0.0f);
					tsr.setAmount(0.0f);
				}
			}
			((PTIOPermit)permit).setTimeCardInfoRetrieved(true);
		}catch(DAOException daoe) {
			logger.error(daoe.getMessage(), daoe);
			DisplayUtil.displayError("Data access error occured while retrieving time sheet hours. "
										+ daoe.getMessage());
			((PTIOPermit)permit).setTimeCardInfoRetrieved(false);
			timeSheetRows = null;
		}catch(CannotGetJdbcConnectionException jdbce) {
			logger.error(jdbce.getMessage(), jdbce);
			String errorMessage = jdbce.getMessage();
			String displayMessage = "";
			if(!Utility.isNullOrEmpty(errorMessage)) {
				if(errorMessage.contains("Login failed")) {
					displayMessage = "Invalid credentials. Check login name and password.";
				} else if(errorMessage.contains("connect timed out")) {
					displayMessage = "Please ensure the AQDS database is online and is reachable from IMPACT.";
				} else if(errorMessage.contains("Cannot open database")) {
					displayMessage = "Please ensure the AQDS database name is correct and the database is accessible.";
				} else if(errorMessage.contains("Permission denied")) {
					displayMessage = "Please ensure the connection to the AQDS database is not blocked by a firewall.";
				}
				else {
					displayMessage = errorMessage;
				}
			}
			DisplayUtil.displayError("IMPACT could not connect to the AQDS database. " + displayMessage); 

			((PTIOPermit)permit).setTimeCardInfoRetrieved(false);
			timeSheetRows = null;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			DisplayUtil.displayError("An unknown exception occured while connecting to the AQDS database. " + e.getMessage()); 
			((PTIOPermit)permit).setTimeCardInfoRetrieved(false);
			timeSheetRows = null;
		}
		
		return timeSheetRows;
	}
	
	public final float getBillableHourlyRate(Timestamp tsDate, NSRBillingBillableRateDef[] billableRates) {
		Timestamp currentEffectiveDate = null;
		float hourlyRate = 0.0f;
				
		for(NSRBillingBillableRateDef br : billableRates) {
			if(!br.isDeprecated()) {
				Timestamp effectiveDate = br.getEffectiveDate();
				
				if(tsDate.after(effectiveDate)) {
					if(null != currentEffectiveDate) {
						 if(effectiveDate.after(currentEffectiveDate)) {
							// make the effective date as the current effective date
							 currentEffectiveDate = effectiveDate;
							hourlyRate = br.getHourlyRate();
						 }
					} else {
						// make the effective date as the current effective date
						currentEffectiveDate = effectiveDate;
						hourlyRate = br.getHourlyRate();
					}
				}
			}

		}
		
		return hourlyRate;
	}
	
	public boolean generateNSRPermitInvoice(Permit permit, String invoiceType, Timestamp invoiceRefDate) 
			throws DAOException, InvoiceGenerationException {
		boolean ret = false;
		double totalFees = 0.0;
		double billableStandardFees = 0.0;
		
		TimeSheetRow[] tsRows = retrieveTimeSheetHours(permit);
		if( null == tsRows) {
			throw new InvoiceGenerationException("Failed to retrieve time card information from AQDS");
		}
		
		// hours from the time card table that are billable in this invoice
		List<TimeSheetRow> billableTSRows = getBillableTimeSheetRows(permit, tsRows, invoiceRefDate);
		for(TimeSheetRow tsr : billableTSRows) {
			totalFees += tsr.getAmount();	
		}
			
		// add standard fees to total fees
		billableStandardFees = getBillableStandardFees(permit, invoiceRefDate);
		totalFees += billableStandardFees;
			
		// create a charge/payment record
		PermitChargePayment pcp = new PermitChargePayment();
		pcp.setPermitId(permit.getPermitID());
		pcp.setTransactionDate(new Timestamp(System.currentTimeMillis()));
		pcp.setTransactionAmount(new Double(totalFees));
		
		if(!Utility.isNullOrEmpty(invoiceType)) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String invoiceDate = sdf.format(invoiceRefDate);
			if(invoiceType.equalsIgnoreCase(NSRBillingFeeTypeDef.INITIAL_INVOICE)) {
				pcp.setTransactionType(NSRBillingChargePaymentTypeDef.INITIAL_INVOICE);
				pcp.setNoteTxt("Initial invoice for permit   " + permit.getPermitNumber()
						+ " (Invoice Reference Date: " + invoiceDate  + ")");
			}else if(invoiceType.equalsIgnoreCase(NSRBillingFeeTypeDef.FINAL_INVOICE)) {
				pcp.setTransactionType(NSRBillingChargePaymentTypeDef.FINAL_INVOICE);
				pcp.setNoteTxt("Final invoice for permit   " + permit.getPermitNumber()
						+ " (Invoice Reference Date: " + invoiceDate + ")");
			}else if(invoiceType.equalsIgnoreCase(NSRBillingFeeTypeDef.SPECIAL_INVOICE)) {
				pcp.setTransactionType(NSRBillingChargePaymentTypeDef.SPECIAL_INVOICE);
				pcp.setNoteTxt("Special invoice for permit   " + permit.getPermitNumber()
						+ " (Invoice Reference Date: " + invoiceDate + ")");
			}else {
				// unknown invoice type
				logger.debug("Invalid invoice type: " + invoiceType);
				throw new InvoiceGenerationException("Invalid invoice type " + invoiceType);
			}
		} else {
			logger.debug("Invoice type is not set");
			throw new InvoiceGenerationException("Invoice type is not selected");
		}
		
		createPermitChargePayment(pcp);
		
		BigDecimal totalAmountDue = retrievePermitChargePaymentTotal(permit.getPermitID());
		
		generateInvoiceDocument(invoiceType, permit, invoiceRefDate, tsRows, totalAmountDue);
		
		// update the last invoice reference date
		((PTIOPermit)permit).setLastInvoiceRefDate(invoiceRefDate);
		((PTIOPermit)permit).setPermitChargePaymentList(permitDAO().
								retrievePermitChargePaymentList(permit.getPermitID()));
		modifyPermit(permit, InfrastructureDefs.getCurrentUserId());
		
		// if you come here then no errors have occured
		ret = true;
	
		return ret;
	}
	
	/**
	 * Returns an array of time sheet rows whose date is between the last invoice reference date
	 * and the current invoice reference date (inclusive).
	 * 
	 * @return An array of time sheet rows
	 */
	public List<TimeSheetRow> getBillableTimeSheetRows(Permit permit, 
									TimeSheetRow[] tsRows, Timestamp invoiceRefDate) {
		
		List<TimeSheetRow> billableRows = new ArrayList<TimeSheetRow>();
		if(isTimesheetEntryEnabled()) {
			// IMPACT timesheet entry feature is enabled - use new logic based on invoiced flag
			for(TimeSheetRow tsr : tsRows) {
				Timestamp tsrDate = tsr.getDate();
				if(null != tsrDate) {
					if(!tsr.isInvoiced() && 
							(tsrDate.before(invoiceRefDate) || tsrDate.equals(invoiceRefDate))) {
						billableRows.add(tsr);
					}
				}
			}
		} else {
			// system is still using AQDS to retrieve timesheet data - use old logic based on last invoice reference date
			Timestamp lastInvoiceRefDate;
					
			// if last invoice reference date is null, then we will use epoch as the reference
			lastInvoiceRefDate = ((PTIOPermit)permit).getLastInvoiceRefDate();
			if(null == lastInvoiceRefDate) {
				// set last invoice ref date to epoch
				lastInvoiceRefDate = new Timestamp(0);
			}
			
			// determine which time sheet rows to be included in this invoice
			// for that we need to exclude the rows that were billed in earlier invoice(s)
			// (i.e., the date on which the time sheet is entered is before the the last invoice ref date)
			// and we need to skip those records whose date is later than the invoice ref date.
	
			// determine billable rows
			for(TimeSheetRow tsr : tsRows) {
				Timestamp tsrDate = tsr.getDate();
				if(null != tsrDate) {
					if(tsrDate.after(lastInvoiceRefDate) 
							&& (tsrDate.before(invoiceRefDate) || tsrDate.equals(invoiceRefDate))) {
						billableRows.add(tsr);
					}
				}
			}
		}

		return billableRows;
	}
	
	public List<NSRFixedCharge> retrieveNSRFixedChargeList(
			Integer permitID) throws DAOException {

		List<NSRFixedCharge> pcpList = null;

		try {

			pcpList = permitDAO().retrieveNSRFixedChargeList(permitID);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while retrieving Fixed Charges");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while retrieving Fixed Charges");
			logger.error(e.getMessage());
		} finally {

		}

		return pcpList;

	}

	public void createNSRFixedCharge(NSRFixedCharge pcp)
			throws DAOException {
		try {

			permitDAO().createNSRFixedCharge(pcp);

		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while creating Fixed Charges");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while creating Fixed Charges");
			logger.error(e.getMessage());
		} finally {

		}

	}

	public void modifyNSRFixedCharge(NSRFixedCharge pcp)
			throws DAOException {
		try {

			permitDAO().modifyNSRFixedCharge(pcp);

		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while modifying Fixed Charges");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while modifying Fixed Charges");
			logger.error(e.getMessage());
		} finally {

		}

	}

	public final void removeNSRFixedCharge(NSRFixedCharge pcc)
			throws DAOException {
		try {

			permitDAO().removeNSRFixedCharge(pcc);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while removing Fixed Charges");
			logger.error(e.getMessage());
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while removing Fixed Charges");
			logger.error(e.getMessage());
		} finally {

		}
	}
	
	public NSRBillingStandardFeesDef[] retrieveStandardFeeDef() throws DAOException {
		return permitDAO().retrieveStandardFeeDef();
	}
	
	/**
	 * Retrieve time sheet information from AQDS related to a permit and associated applications
	 */
	public List<NSRFixedCharge> retrieveFixedCharges(Permit permit) throws DAOException {
		String permitNumber = permit.getPermitNumber();
		
		// get the standard fees (fixed charges) from IMPACT definition list
		NSRBillingStandardFeesDef[] standardFees = permitDAO().retrieveStandardFeeDef();
		List<NSRFixedCharge> activeFeeList = getActiveFees(standardFees);
		
		return activeFeeList;
	}
	
	public final List<NSRFixedCharge> getActiveFees(NSRBillingStandardFeesDef[] standardFees) {
		
		ArrayList<NSRFixedCharge> feeList = new ArrayList<NSRFixedCharge>();
		for(NSRBillingStandardFeesDef sf : standardFees) {
			if(!sf.isDeprecated()) {
				NSRFixedCharge fc = new NSRFixedCharge();
				fc.setAmount(sf.getFee().doubleValue());
				fc.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				fc.setDescription(sf.getDescription());
				feeList.add(fc);
			}
		}		
		return feeList;
	}
	
	/**
	 * Returns total billable standard fees which is sum of all fixed/other charges
	 * whose date is between the last invoice reference date and the current invoice
	 * reference date (inclusive).
	 * 
	 * @param permit
	 * @param invoiceRefDate - current invoice reference date
	 * 
	 * @return Total billable standard fees
	 * 
	 * @throws none
	 */
	public double getBillableStandardFees(Permit permit, Timestamp invoiceRefDate) {
		double billableStandardFees = 0.0;
		if(isTimesheetEntryEnabled()) {
			// IMPACT timesheet entry feature is enabled - use new logic based on invoiced flag
			if(null != invoiceRefDate) {
				List<NSRFixedCharge> fcs = ((PTIOPermit)permit).getNSRFixedChargeList();
				for(NSRFixedCharge afc : fcs) {
					if(!afc.isInvoiced()
							&& (afc.getCreatedDate().before(invoiceRefDate)	|| afc.getCreatedDate().equals(invoiceRefDate))) {
						billableStandardFees += afc.getAmount();
					}	
				}
			}
		} else {
			// system is still using AQDS to retrieve timesheet data - use old logic based on last invoice reference date
			Timestamp lastInvoiceRefDate;
			
			// if last invoice reference date is null, then we will use epoch as the reference
			lastInvoiceRefDate = ((PTIOPermit)permit).getLastInvoiceRefDate();
			if(null == lastInvoiceRefDate) {
				// set last invoice ref date to epoch
				lastInvoiceRefDate = new Timestamp(0);
			}
			if(null != invoiceRefDate) {
				List<NSRFixedCharge> fcs = ((PTIOPermit)permit).getNSRFixedChargeList();
				for(NSRFixedCharge afc : fcs) {
					if(afc.getCreatedDate().after(lastInvoiceRefDate)
							&& (afc.getCreatedDate().before(invoiceRefDate)
							|| afc.getCreatedDate().equals(invoiceRefDate))) {
						billableStandardFees += afc.getAmount();
					}	
				}
			}
		}
		return billableStandardFees;
	}
	
	public boolean generateInvoiceDocument(String invoiceType, Permit permit, Timestamp invoiceRefDate,	
											TimeSheetRow[] tsRows, BigDecimal totalAmountDue)
														throws DAOException, InvoiceGenerationException {	
		boolean ret = false;
		TemplateDocument template = null;
		String invoiceDesc = null;
		String invoiceDocDesc = null;
		String permitDocTypeCD = null;
		
		// if it is initial invoice then we will use the template for initial invoice, otherwise
		// we will use the template for final invoice
		if(invoiceType.equalsIgnoreCase(NSRBillingFeeTypeDef.INITIAL_INVOICE)) {
			template = TemplateDocTypeDef.getTemplate(TemplateDocTypeDef.NSR_BILLING_INITIAL_INVOICE);
			invoiceDesc = "Initial Invoice";
			invoiceDocDesc = "Initial invoice for permit " + permit.getPermitNumber();
			permitDocTypeCD = PermitDocTypeDef.INITIAL_INVOICE;
		} else if(invoiceType.equalsIgnoreCase(NSRBillingFeeTypeDef.FINAL_INVOICE)){
			template = TemplateDocTypeDef.getTemplate(TemplateDocTypeDef.NSR_BILLING_FINAL_INVOICE);
			invoiceDesc = "Final Invoice";
			invoiceDocDesc = "Final invoice for permit " + permit.getPermitNumber();
			permitDocTypeCD = PermitDocTypeDef.FINAL_INVOICE;
		} else if(invoiceType.equalsIgnoreCase(NSRBillingFeeTypeDef.SPECIAL_INVOICE)){
			template = TemplateDocTypeDef.getTemplate(TemplateDocTypeDef.NSR_BILLING_FINAL_INVOICE);
			invoiceDesc = "Special Invoice";
			invoiceDocDesc = "Special invoice for permit " + permit.getPermitNumber();
			permitDocTypeCD = PermitDocTypeDef.SPECIAL_INVOICE;
		} else {
			throw new InvoiceGenerationException("Invalid invoice type " + invoiceType);
		}
		
		if(null != template) {
			try {
					Contact nsrBillingContact = null;
					Facility facility = facilityDAO().retrieveFacility(permit.getFacilityId());
					if(null != facility) {
						nsrBillingContact = facility.getNSRBillingContact();
						if(null == nsrBillingContact) {
							throw new InvoiceGenerationException("Facility does not have an active NSR Billing Contact");
						}
					}
					
					NSRBillingBillableRateDef[] brd = permitDAO().retrieveBillableRatesDef();
					PermitDocument pDoc = new PermitDocument();
					Document document = new Document();
					
					// setup the document
					pDoc.setPermitId(permit.getPermitID());
					pDoc.setPermitDocTypeCD(permitDocTypeCD);
					pDoc.setDescription(invoiceDocDesc);
					pDoc.setLastModified(1);
					pDoc.setRequiredDoc(true);
					pDoc.setFacilityID(permit.getFacilityId());
					pDoc.setLastModifiedBy(InfrastructureDefs.getCurrentUserId());
					pDoc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
					pDoc.setUploadDate(new Timestamp(System.currentTimeMillis()));
					pDoc.setTemporary(false);
					pDoc.setExtension("docx");
					
					// create a place holder for the document so that when the actual document
					// is generated, we can use the document id as the name for the generated document.
					// This is necessary in order to correctly access/download the document from the 
					// attachments datagrid.
					document = documentDAO().createDocument(pDoc);
					pDoc.setDocumentID(document.getDocumentID());
					
					// set up the document generation bean
					DocumentGenerationBean dgb = new DocumentGenerationBean();
					dgb.setNSRInvoice(permit, invoiceDesc);
					dgb.setFacility(facility);
					dgb.setContact(nsrBillingContact);
					
					// generate invoice
					String documentPath = pDoc.getDirName() + pDoc.getBasePath();
					DocumentUtil.generateAsposeDocument(template.getTemplateDocPath(), dgb, documentPath);
					if(!updateInvoiceDocument(permit, invoiceRefDate, tsRows, totalAmountDue, documentPath)) {
						throw new InvoiceGenerationException("An error occured during document generation");
					}
					
			        // update permit documents
					permit.getDocuments().add(pDoc);
			        modifyPermitDocuments(permit);
			        
			        // mark billed timesheet rows as invoiced
			        if(isTimesheetEntryEnabled()) {
			        	for(TimeSheetRow tsr : getBillableTimeSheetRows(permit, tsRows, invoiceRefDate)) {
			        		tsr.setInvoiced(true);
			        		infrastructureDAO().modifyTimesheetEntry(tsr);
			        	}
			        	
			        	for(NSRFixedCharge nfc : getBillableFixedCharges(permit, invoiceRefDate)) {
			        		nfc.setInvoiced(true);
			        		permitDAO().modifyNSRFixedCharge(nfc);
			        	}
			        }
			        
					// if you come here then the invoice generation was successful
			        ret = true;
			}catch(IOException ioe){
				logger.debug("An I/O error occured while generating the invoice" + ioe.getMessage());
				throw new InvoiceGenerationException(ioe.getMessage());
			}catch(DocumentGenerationException dge) {
				logger.debug("Error occured in document generation bean" + dge.getMessage());
				throw new InvoiceGenerationException(dge.getMessage());
			}
		} else {
			logger.debug("Invoice template for invoice type " + invoiceType + "is not found");
			throw new InvoiceGenerationException("Template for invoice type " + invoiceType + "is not found");
		}
			
		return ret;
	}

/**
 * Returns an array of time sheet rows that were previoulsy billed
 * i.e., whose date is on or before the last invoice reference date
   
 * @return An array of time sheet rows
 */
public List<TimeSheetRow> getPreviouslyBilledTSRows(Permit permit, TimeSheetRow[] tsRows, Timestamp invoiceRefDate) {
	
		List<TimeSheetRow> previouslyBilledTSRows = new ArrayList<TimeSheetRow>();
		Timestamp lastInvoiceRefDate;
		
		if(isTimesheetEntryEnabled()) {
			// IMPACT timesheet entry feature is enabled - use new logic based on invoiced flag
			for(TimeSheetRow tsr : tsRows) {
				Timestamp trsDate = tsr.getDate();
				if(null != trsDate 
						&& tsr.isInvoiced()
						&& (trsDate.before(invoiceRefDate) || trsDate.equals(invoiceRefDate))) {
					previouslyBilledTSRows.add(tsr);
				}
			}
		} else {
			// system is still using AQDS to retrieve timesheet data - use old logic based on last invoice reference date	
			lastInvoiceRefDate = ((PTIOPermit)permit).getLastInvoiceRefDate();
			((PTIOPermit)permit).getNSRTimeSheetRowList();
			
			// get rows whose date is on or before the last invoice reference date
			if(null != lastInvoiceRefDate) {
				for(TimeSheetRow tsr : tsRows) {
					Timestamp trsDate = tsr.getDate();
					if(null != trsDate 
							&& (trsDate.before(lastInvoiceRefDate) || trsDate.equals(lastInvoiceRefDate))) {
						previouslyBilledTSRows.add(tsr);
					}
				}
			}	
		}
		
		return previouslyBilledTSRows;
	}

/**
 * Returns an array of fixed/other charges that were previously billed
 * i.e., whose date is on or before the last invoice reference date
   
 * @return An array of time sheet rows
 */
public List<NSRFixedCharge> getPreviouslyBilledFixedCharges(Permit permit, Timestamp invoiceRefDate) {
		List<NSRFixedCharge> fixedChargeList = ((PTIOPermit)permit).getNSRFixedChargeList();
		List<NSRFixedCharge> previouslyBilledFixedCharges = new ArrayList<NSRFixedCharge>();
		Timestamp lastInvoiceRefDate = null;

		if(isTimesheetEntryEnabled()) {
			// IMPACT timesheet entry feature is enabled - use new logic based on invoiced flag
			for(NSRFixedCharge fc : fixedChargeList) {
				Timestamp fcDate = fc.getCreatedDate();
				if(null != fcDate
						&& fc.isInvoiced()
						&& (fcDate.before(invoiceRefDate) || fcDate.equals(invoiceRefDate))) {
					previouslyBilledFixedCharges.add(fc);
				}
			}
		} else {
			// system is still using AQDS to retrieve timesheet data - use old logic based on last invoice reference date

			lastInvoiceRefDate = ((PTIOPermit)permit).getLastInvoiceRefDate();
			
			// get rows whose date is on or before the last invoice reference date
			if(null != lastInvoiceRefDate) {
				// determine previously billed charges
				for(NSRFixedCharge fc : fixedChargeList) {
					Timestamp fcDate = fc.getCreatedDate();
					if(null != fcDate 
							&& (fcDate.before(lastInvoiceRefDate) || fcDate.equals(lastInvoiceRefDate))) {
						previouslyBilledFixedCharges.add(fc);
					}
				}
			}
		}
		
		return previouslyBilledFixedCharges;
	}

/**
 * Returns an array of fixed/other charges that are billable
 * i.e., whose date is after the last invoice reference date and on or before the
 * current invoice reference date
   
 * @return An array of fixed charge
 */
public List<NSRFixedCharge> getBillableFixedCharges(Permit permit, Timestamp invoiceRefDate) {
		List<NSRFixedCharge> billableFixedCharges = new ArrayList<NSRFixedCharge>();
		List<NSRFixedCharge> fixedChargeList = ((PTIOPermit)permit).getNSRFixedChargeList();
		Timestamp lastInvoiceRefDate = null;
				
		if(isTimesheetEntryEnabled()) {
			// IMPACT timesheet entry feature is enabled - use new logic based on invoiced flag
			for(NSRFixedCharge fc : fixedChargeList) {
				if(!fc.isInvoiced()
						&& (fc.getCreatedDate().before(invoiceRefDate) || fc.getCreatedDate().equals(invoiceRefDate))) {
					billableFixedCharges.add(fc);
				}	
			}
		} else {
			// system is still using AQDS to retrieve timesheet data - use old logic based on last invoice reference date
			
			// if last invoice reference date is null, then we will use epoch as the reference
			lastInvoiceRefDate = ((PTIOPermit)permit).getLastInvoiceRefDate();
			if(null == lastInvoiceRefDate) {
				// set last invoice ref date to epoch
				lastInvoiceRefDate = new Timestamp(0);
			}
			
			
			// determine which time sheet rows to be included in this invoice
			// for that we need to exclude the rows that were billed in earlier invoice(s)
			// (i.e., the date on which fixed charge is created is before the the last invoice ref date)
			// and we need to skip those records whose created date is later than the invoice ref date.
			for(NSRFixedCharge fc : fixedChargeList) {
				if(fc.getCreatedDate().after(lastInvoiceRefDate)
						&& (fc.getCreatedDate().before(invoiceRefDate)
						|| fc.getCreatedDate().equals(invoiceRefDate))) {
					billableFixedCharges.add(fc);
				}	
			}
		}
				
		return billableFixedCharges;
	}

	public boolean updateInvoiceDocument(Permit permit, Timestamp invoiceRefDate, TimeSheetRow[] tsRows, 
											BigDecimal totalAmountDue, String documentPath) throws DAOException {	

		boolean ret = false;
		int tableIndex = 1; // index of the invoice table in the tempalte
		com.aspose.words.Document invoiceDoc = DocumentUtil.getDocument(documentPath);
		if(null != invoiceDoc) {
			// second table in the invoice template
			Table invoiceTable = DocumentUtil.getTable(invoiceDoc, tableIndex);
			if(null != invoiceTable) {
				// add previously billed fixed charges
				invoiceTable = addFixedChargesToInvoice(invoiceDoc, invoiceTable, permit, invoiceRefDate, false);
				
				// add previously billed time sheet hours
				invoiceTable = addTSRowsToInvoice(invoiceDoc, invoiceTable, permit, invoiceRefDate, tsRows, false);
				
				// add current fixed charges
				invoiceTable = addFixedChargesToInvoice(invoiceDoc, invoiceTable, permit, invoiceRefDate, true);
				
				// add current time sheet hours
				invoiceTable = addTSRowsToInvoice(invoiceDoc, invoiceTable, permit, invoiceRefDate, tsRows, true);
				
				// add payments
				invoiceTable = addPaymentsToInvoice(invoiceDoc, invoiceTable, permit);
				
				// add total amount due
				invoiceTable = addTotalDueToInvoice(invoiceDoc, invoiceTable, totalAmountDue);
				
				if(DocumentUtil.saveDocument(invoiceDoc, documentPath)) {
					ret = true;
				}			
			}
		}	
		
		return ret;
	}

	public Table addFixedChargesToInvoice(com.aspose.words.Document invoiceDoc, Table invoiceTable, Permit permit,
											Timestamp invoiceRefDate, boolean isCurrent) {
		List<NSRFixedCharge> fixedChargeRows;
		String heading;
		
		// if isCurrent is true then retrieve billable fixed charges, othewise
		// retrieve fixed charges there were billed in previous invoice(s)
		if(isCurrent) {
			fixedChargeRows = getBillableFixedCharges(permit, invoiceRefDate);
			heading = "\tCurrent Fixed Charges";
		} else {
			fixedChargeRows = getPreviouslyBilledFixedCharges(permit, invoiceRefDate);
			heading = "\tPreviously Billed Fixed Charges";
		}
		
		if (null != fixedChargeRows && fixedChargeRows.size() > 0) {
			Cell cell1;
			Cell cell2;

			cell1 = DocumentUtil.createNewCell(invoiceDoc, heading, 
												DocumentUtil.boldFont, ParagraphAlignment.LEFT, DocumentUtil.CELL1_PER_WIDTH);
			cell2 = DocumentUtil.createNewCell(invoiceDoc, "", 
												DocumentUtil.boldFont, ParagraphAlignment.LEFT, DocumentUtil.CELL2_PER_WIDTH);
			
			Row sectionHeading = DocumentUtil.createNewTwoCellRow(invoiceDoc, cell1, cell2, false);
			invoiceTable.appendChild(sectionHeading);

			// previously billed fixed/other charges
			for (NSRFixedCharge fc : fixedChargeRows) {
				String cell1Text = "\t\t" + fc.getDescription();
				String cell2Text = fc.getAmountString();
				
				cell1 = DocumentUtil.createNewCell(invoiceDoc, cell1Text, DocumentUtil.normalFont, 
													ParagraphAlignment.LEFT, DocumentUtil.CELL1_PER_WIDTH);
				cell2 = DocumentUtil.createNewCell(invoiceDoc, cell2Text, DocumentUtil.normalFont,
													ParagraphAlignment.RIGHT, DocumentUtil.CELL2_PER_WIDTH);
				
				Row row = DocumentUtil.createNewTwoCellRow(invoiceDoc, cell1, cell2, false);
				
				invoiceTable.appendChild(row);
			}
		}
		return invoiceTable;
	}
	
	public Table addTSRowsToInvoice(com.aspose.words.Document invoiceDoc, Table invoiceTable, Permit permit, 
										Timestamp invoiceRefDate, TimeSheetRow[] tsRows, boolean isCurrent) throws DAOException {
		if(null != tsRows && tsRows.length > 0) {
			Locale locale = new Locale("en", "US");
        	NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        	
        	NSRBillingBillableRateDef[] brd = permitDAO().retrieveBillableRatesDef();
        	List<TimeSheetRow> invoiceTSRows = new ArrayList<TimeSheetRow>();
        	
    		float[] hours = new float[brd.length];
    		String heading;
    		
    		// if isCurrent is true then retrieve billable time sheet rows, othewise
    		// retrieve time sheet rows there were billed in previous invoice(s)
    		if(isCurrent) {
    			invoiceTSRows = getBillableTimeSheetRows(permit, tsRows, invoiceRefDate);
    			heading = "\tCurrent Staff Review Charges";
    		} else {
    			invoiceTSRows = getPreviouslyBilledTSRows(permit, tsRows, invoiceRefDate);
    			heading = "\tPreviously Billed Staff Review Charges";
    		}
    		
    		if(null != invoiceTSRows && invoiceTSRows.size() > 0) {
	    		Cell cell1;
				Cell cell2;
	
				cell1 = DocumentUtil.createNewCell(invoiceDoc, heading, 
													DocumentUtil.boldFont, ParagraphAlignment.LEFT, DocumentUtil.CELL1_PER_WIDTH);
				cell2 = DocumentUtil.createNewCell(invoiceDoc, "", 
													DocumentUtil.boldFont, ParagraphAlignment.LEFT, DocumentUtil.CELL2_PER_WIDTH);
				
				Row sectionHeading = DocumentUtil.createNewTwoCellRow(invoiceDoc, cell1, cell2, false);
				invoiceTable.appendChild(sectionHeading);
				
				for(int i = 0; i < hours.length; ++i) {
		    		hours[i] = 0.0f;
		    	}
		    	// compute total hours for each row in the Billable Rate definition list
		    	for(TimeSheetRow tsr : invoiceTSRows) {
		    		float hrs = 0.0f;
		    		int j = 0;
		    		for(int i = 0; i < brd.length; ++i) {
		    			if(tsr.getDate().equals(brd[i].getEffectiveDate())
		    					|| tsr.getDate().after(brd[i].getEffectiveDate())) {
		    				hrs = tsr.getHours();
		    				j = i;
		    			}
		    		}
		    		hours[j] += hrs;
		    	}
			 
				// current charges
				for(int i = 0; i< brd.length; ++i) {
					if(hours[i] > 0) {
						String cell1Txt = "\t\t" + hours[i] + " hour(s) at " + nf.format(brd[i].getHourlyRate()) 
								+ " (" + brd[i].getDescription() + ")";   
						String cell2Txt = nf.format(hours[i] * brd[i].getHourlyRate());
						cell1 = DocumentUtil.createNewCell(invoiceDoc, cell1Txt, DocumentUtil.normalFont,
															ParagraphAlignment.LEFT, DocumentUtil.CELL1_PER_WIDTH);
						cell2 = DocumentUtil.createNewCell(invoiceDoc, cell2Txt, DocumentUtil.normalFont,
															ParagraphAlignment.RIGHT, DocumentUtil.CELL2_PER_WIDTH);
						
						Row row = DocumentUtil.createNewTwoCellRow(invoiceDoc, cell1, cell2, false);
				
						invoiceTable.appendChild(row);
					}
				}
    		}
		}	
		
		return invoiceTable;
	}
	
	public Table addPaymentsToInvoice(com.aspose.words.Document invoiceDoc, Table invoiceTable,  Permit permit) {
		
		List<PermitChargePayment> chargePayments = ((PTIOPermit)permit).getPermitChargePaymentList();
		SimpleDateFormat sdf =  new SimpleDateFormat("MM/dd/yyyy");
		
		if(null != chargePayments) {
			Cell cell1;
			Cell cell2;

			cell1 = DocumentUtil.createNewCell(invoiceDoc, "\tPayments and Credits", 
												DocumentUtil.boldFont, ParagraphAlignment.LEFT, DocumentUtil.CELL1_PER_WIDTH);
			cell2 = DocumentUtil.createNewCell(invoiceDoc, "", 
												DocumentUtil.boldFont, ParagraphAlignment.LEFT, DocumentUtil.CELL2_PER_WIDTH);
			
			Row sectionHeading = DocumentUtil.createNewTwoCellRow(invoiceDoc, cell1, cell2, false);
						invoiceTable.appendChild(sectionHeading);
			
			// payments and other credits
			for(PermitChargePayment pcp: chargePayments) {
				if(pcp.getTransactionType().equalsIgnoreCase(NSRBillingChargePaymentTypeDef.PAYMENT)
						|| pcp.getTransactionType().equalsIgnoreCase(NSRBillingChargePaymentTypeDef.OTHER_CREDIT)) {
					String notes = null != pcp.getNoteTxt() ? pcp.getNoteTxt() : "";
					String cell1Txt = "\t\tDate: "
							+sdf.format(pcp.getTransactionDate()) 
							+ ". " 
							+ notes;
					String cell2Txt = pcp.getTransactionAmountString();
					cell1 = DocumentUtil.createNewCell(invoiceDoc, cell1Txt, DocumentUtil.normalFont,
														ParagraphAlignment.LEFT, DocumentUtil.CELL1_PER_WIDTH);
					cell2 = DocumentUtil.createNewCell(invoiceDoc, cell2Txt, DocumentUtil.normalFont,
														ParagraphAlignment.RIGHT, DocumentUtil.CELL2_PER_WIDTH);
							
					Row row = DocumentUtil.createNewTwoCellRow(invoiceDoc, cell1, cell2, false);
					
					invoiceTable.appendChild(row);
				}
			}
		}	
		return invoiceTable;
	}
	
	public Table addTotalDueToInvoice(com.aspose.words.Document invoiceDoc, Table invoiceTable, 
										BigDecimal totalAmountDue) {
		
		Locale locale = new Locale("en", "US");
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		String totalAmountDueStr;
	
		totalAmountDueStr = nf.format(totalAmountDue);
		
		Cell cell1;
		Cell cell2;

		cell1 = DocumentUtil.createNewCell(invoiceDoc, "\tTOTAL AMOUNT DUE", 
											DocumentUtil.boldFont, ParagraphAlignment.RIGHT, DocumentUtil.CELL1_PER_WIDTH);
		cell2 = DocumentUtil.createNewCell(invoiceDoc, totalAmountDueStr, 
											DocumentUtil.boldFont, ParagraphAlignment.RIGHT, DocumentUtil.CELL2_PER_WIDTH);
		
		Row totalDueRow = DocumentUtil.createNewTwoCellRow(invoiceDoc, cell1, cell2, false);
		invoiceTable.appendChild(totalDueRow);
					
		return invoiceTable;
	}
	
	public final boolean isTimesheetEntryEnabled() {
		boolean ret = false;
		InfrastructureDefs infraDefs = (InfrastructureDefs)FacesUtil.getManagedBean("infraDefs");
		if(null != infraDefs) {
			ret = infraDefs.isTimesheetEntryEnabled();
		}
		
		return ret;
	}
	
	private final Double getInvoicedFixedChargesAmt(Permit permit, Timestamp invoiceRefDate) {
		Double invoicedFixedChargesAmt = 0.0;
		
		List<NSRFixedCharge> invoicedFixedCharges = new ArrayList<NSRFixedCharge>();
		invoicedFixedCharges = getPreviouslyBilledFixedCharges(permit, invoiceRefDate);
		
		for(NSRFixedCharge fc : invoicedFixedCharges) {
			invoicedFixedChargesAmt += fc.getAmount();
		}
		
		return invoicedFixedChargesAmt;
	}
	
	private final Double getInvoicedTimesheetHoursAmt(Permit permit, TimeSheetRow[] tsRows, Timestamp invoiceRefDate) {
		Float invoicedTimesheetHoursAmt = 0.0f;
		
		List<TimeSheetRow> invoicedTimesheetRows = new ArrayList<TimeSheetRow>();
		invoicedTimesheetRows = getPreviouslyBilledTSRows(permit, tsRows, invoiceRefDate);
		
		for(TimeSheetRow tsr : invoicedTimesheetRows) {
			invoicedTimesheetHoursAmt += tsr.getAmount();
		}
		
		return new Double(invoicedTimesheetHoursAmt);
	}
	
	private final Double getTotalInvoicedAmt(Permit permit, TimeSheetRow[] tsRows, Timestamp invoiceRefDate) {
		Double totalInvoicedAmt = 0.0;
		
		Double invoicedFixedChargesAmt = getInvoicedFixedChargesAmt(permit, invoiceRefDate);
		Double invoicedTimesheetHoursAmt = getInvoicedTimesheetHoursAmt(permit, tsRows, invoiceRefDate);
		
		totalInvoicedAmt = invoicedFixedChargesAmt + invoicedTimesheetHoursAmt;
		
		return totalInvoicedAmt;
	}
	
	public final Double getTotalPaymentAmt(Permit permit) throws DAOException {
		Double paymentAmt = 0.0;
		
		List<PermitChargePayment> chargePayments = new ArrayList<PermitChargePayment>();
		chargePayments = retrievePermitChargePaymentList(permit.getPermitID());
		
		for(PermitChargePayment cp : chargePayments) {
			if(cp.isPayment() || cp.isOtherCredit()) {
				paymentAmt += cp.getTransactionAmount();
			}
		}
		
		return paymentAmt;
	}
	
	public final boolean isCurrentBalanceValid(Permit permit, Timestamp invoiceRefDate) 
			throws DAOException {
		boolean ret = false;
	
		TimeSheetRow[] tsRows = retrieveTimeSheetHours(permit);
		BigDecimal totalAmountDue = retrievePermitChargePaymentTotal(permit.getPermitID());
		
		Double totalInvoicedAmt = getTotalInvoicedAmt(permit, tsRows, invoiceRefDate);
		Double paymentAmt = getTotalPaymentAmt(permit);

		Double actualAmountDue = Double.valueOf(String.format("%.2f", (totalInvoicedAmt - paymentAmt)));
		
		logger.debug("Total Invoiced Amount minus Received Payments = " + actualAmountDue);
		logger.debug("Current Balance Due (from charge/payment history) = " + totalAmountDue);
		
		return (totalAmountDue.doubleValue() == actualAmountDue) ? true : false;
	}
	
	@Override
	public EmissionsOffset createPermitEmissionsOffset(EmissionsOffset emissionsOffset)
			throws DAOException {
		return permitDAO().createPermitEmissionsOffset(emissionsOffset);
	}
	
	@Override
	public EmissionsOffset retrievePermitEmissionsOffset(Integer emissionsOffsetId)
			throws DAOException {
		return permitDAO().retrievePermitEmissionsOffset(emissionsOffsetId);
	}
	
	@Override
	public boolean modifyPermitEmissionsOffset(EmissionsOffset emissionsOffset)
			throws DAOException {
		return permitDAO().modifyPermitEmissionsOffset(emissionsOffset);
	}
	
	@Override
	public void deletePermitEmissionsOffset(Integer emissonsOffsetId)
			throws DAOException {
		permitDAO().deletePermitEmissionsOffset(emissonsOffsetId);
	}
	
	@Override
	public EmissionsOffset[] retrievePermitEmissionsOffsetByPermitId(Integer permitId)
			throws DAOException {
		return permitDAO().retrievePermitEmissionsOffsetByPermitId(permitId);
	}
	
	@Override
	public void regenerateOffsetTrackingEntries(PTIOPermit permit)
			throws DAOException {
		EmissionsOffset[] emissionsOffsets = 
				permitDAO().retrievePermitEmissionsOffsetByPermitId(permit.getPermitID());
		// delete exisiting (if any) entries first
		for(EmissionsOffset eo: emissionsOffsets) {
			permitDAO().deletePermitEmissionsOffset(eo.getEmissionsOffsetId());
		}
		// create new entries
		createOffsetTrackingEntries(permit);
	}
	
	/**
	 * Delete a legacy permit if following conditions are satisified: <br>
	 *  <li> permit is not associated with any applications
	 *  <li> permit is not associated with timesheet entries
	 *  <li> permit does not have any NSR fees or charges
	 *  <li> permit does not have any NSR invoices
	 *  <li> permit does not have any offset tracking information
	 *  
	 *  @param permit the permit object to delete
	 *  @return returns true if the permit was deleted, otherwise false
	 *  @throws DAOException  
	 * 
	 */
	@Override
	public boolean deleteLegacyPermit(Permit permit) throws DAOException {
		
		boolean allowedToDelete = true;
		boolean isDeleted = false;
		
		List<String> warnings = new ArrayList<String>();
		
		if(!permit.isLegacyPermit()) {
			DisplayUtil.displayError("Cannot delete a non-legacy permit.");
			return isDeleted;
		}
					
		// check if the permit can be deleted
		if(permit.getApplications().size() > 0) {
			warnings.add("Permit is associated with following application(s): " + permit.getApplicationNumbers() + ".");
			allowedToDelete = false;
		}
		
		if(permit instanceof PTIOPermit) {
			if(((PTIOPermit) permit).getEmissionsOffsetList().size() > 0) {
				warnings.add("Permit has one or more offset tracking entries.");
				allowedToDelete = false;
			}
			
			if(((PTIOPermit) permit).getNSRFixedChargeList().size() > 0
					|| ((PTIOPermit) permit).getPermitChargePaymentList().size() > 0) {
				warnings.add("Permit has one or more NSR charges and/or payments.");
				allowedToDelete = false;
			}
			
			if(((PTIOPermit)permit).getNSRTimeSheetRowList().size() > 0) {
				warnings.add("Permit has one or more billed and/or billable timesheet entries.");
				allowedToDelete = false;
			}
			
			for(PermitDocument doc: permit.getDocuments()) {
				if(doc.getPermitDocTypeCD().equals(PermitDocTypeDef.FINAL_INVOICE) 
						|| doc.getPermitDocTypeCD().equals(PermitDocTypeDef.SPECIAL_INVOICE)
						|| doc.getPermitDocTypeCD().equals(PermitDocTypeDef.INITIAL_INVOICE)) {
					warnings.add("Permit has one or more invoice attachments.");
					break;
				}
			}
		}
		
		if (permit.getPermitConditionList().size() > 0 || permit.getPermitConditionList().size() > 0) {
			warnings.add("Permit has one or more permit conditions.");
			allowedToDelete = false;
		}
		
		if(!allowedToDelete) {
			for(String msg: warnings) {
				DisplayUtil.displayWarning(msg);
			}
			DisplayUtil.displayError("Cannot delete permit " + permit.getPermitNumber() + ".");
		} else {
			// ok to remove the permit
			// first remove a bunch of associated data before removing the
			// permit itself
			
			// remove subparts
			permitDAO().removeSubpartCDs(permit);
			
			// remove reasons
			permitDAO().removePermitReasons(permit.getPermitID());
			
			// remove issuances
			permitDAO().removePermitIssuances(permit.getPermitID());
			
			// remove cc list
			permitDAO().removePermitCCList(permit.getPermitID());
			
			// remove notes
			permitDAO().removePermitNotes(permit.getPermitID());
			
			// remove documents
			permitDAO().markTempPermitDocuments(permit.getPermitID());
			permitDAO().removePermitDocuments(permit.getPermitID());
			
			// remove notes
			permitDAO().removePermitNotes(permit.getPermitID());
			for(Note note : permit.getDapcComments()) {
				infrastructureDAO().removeNote(note.getNoteId());
			}
			
			// remove PTIO/TV permit
			if(permit instanceof PTIOPermit) {
				permitDAO().removePTIOPermit(permit.getPermitID());
			} else if(permit instanceof TVPermit) {
				permitDAO().removeTVPermit(permit.getPermitID());
			}
			
			// remove eus
			for(PermitEUGroup euGroup : permit.getEuGroups()) {
				permitDAO().removeEUGroupEUs(euGroup.getPermitEUGroupID());
			}
			permitDAO().removeAllEUGroups(permit.getPermitID());
			
			// finally remove the permit
			permitDAO().deletePermit(permit.getPermitID());
			
			// if you come here then delete must have succeeded
			isDeleted = true;
		}
		
		return isDeleted;
	}

	@Override
	public PermitDocument retrievePermitDocumentById(Integer documentId) throws DAOException {
		return permitDAO().retrievePermitDocumentById(documentId);
	}
	
	@Override
	public Integer retrievePermitWorkflowProcessId(String permitNumber) throws DAOException {
		return permitDAO().retrievePermitWorkflowProcessId(permitNumber);
	}
	
	/**
	 * Deletes a permit as long as there is no reference to this permit in an inspection
	 * Deletes a permit as long as there is no reference to this permit in an inspection
	 * and the permit has not permit conditions
	 */
	@Override
	public void deletePermit(final Integer permitId) throws DAOException {

		if (null != permitId) {

			// the list of applications associated with this pemitId
			Integer[] permitApplications = permitDAO().retrievePermitApplicationIds(permitId);

			// non type specific
			// permit reason xref
			permitDAO().removePermitReasons(permitId);

			// fed rules and regs subparts - nsps, and neshap
			permitDAO().removeSubpartCDs(permitId);

			// permit application xref
			permitDAO().removePermitApplications(permitId);

			// permit eus and eu groups
			for (PermitEUGroup euGroup : permitDAO().retrievePermitEUGroups(permitId)) {
				if (null != euGroup) {
					permitDAO().removeEUGroupEUs(euGroup.getPermitEUGroupID());
				}
			}
			permitDAO().removeAllEUGroups(permitId);

			// notes
			permitDAO().removePermitNotes(permitId);
			for (Note note : permitDAO().retrieveDapcComments(permitId)) {
				if (null != note) {
					infrastructureDAO().removeNote(note.getNoteId());
				}
			}

			// permit issuances
			permitDAO().removePermitIssuances(permitId);

			// cc list
			permitDAO().removePermitCCList(permitId);
			if (null != permitId) {

				Permit permit = permitDAO().retrievePermitBasic(permitId);

				// permit reason xref
				permitDAO().removePermitReasons(permitId);

				// fed rules and regs subparts - nsps, and neshap
				permitDAO().removeSubpartCDs(permitId);

				// permit eus and eu groups
				for (PermitEUGroup euGroup : permitDAO().retrievePermitEUGroups(permitId)) {
					if (null != euGroup) {
						permitDAO().removeEUGroupEUs(euGroup.getPermitEUGroupID());
					}
				}
				permitDAO().removeAllEUGroups(permitId);

				// notes
				permitDAO().removePermitNotes(permitId);
				for (Note note : permitDAO().retrieveDapcComments(permitId)) {
					if (null != note) {
						infrastructureDAO().removeNote(note.getNoteId());
					}
				}

				// permit issuances
				permitDAO().removePermitIssuances(permitId);

				// cc list
				permitDAO().removePermitCCList(permitId);

				// nsr billing data
				permitDAO().removeNSRFixedChargeList(permitId);
				permitDAO().removePermitChargePaymentList(permitId);
				// TODO remove timesheet entries
				String permitApplicationsCSV = null;
				if (null != permitApplications) {
					permitApplicationsCSV = StringUtils.join(permitApplications, ",");
				}
				// permitDAO().retrieveTimeSheetHoursFromImpact(permitApplicationsCSV,
				// permitNumber);

				// offset tracking data
				for (EmissionsOffset eo : permitDAO().retrievePermitEmissionsOffsetByPermitId(permitId)) {
					if (null != eo) {
						permitDAO().deletePermitEmissionsOffset(eo.getEmissionsOffsetId());
					}
				}

				// attachments
				permitDAO().markTempPermitDocuments(permitId);
				permitDAO().removePermitDocuments(permitId);

				// type specific
				// TODO - can it be done in a better way?
				permitDAO().removePTIOPermit(permitId);
				permitDAO().removeTVPermit(permitId);

				// finally delete the permit itself
				permitDAO().deletePermit(permitId);

				// timesheet entries
				String[] applicationNumbers = permitDAO().retrievePermitApplicationNumbers(permitId);
				String appNumbersToCSV = null;

				if (null != applicationNumbers) {
					appNumbersToCSV = StringUtils.join(applicationNumbers, ",");
				}

				TimeSheetRow[] timesheetRows = permitDAO().retrieveTimeSheetHoursFromImpact(appNumbersToCSV,
						permit.getPermitNumber());

				for (TimeSheetRow row : timesheetRows) {
					infrastructureDAO().removeTimesheetEntry(row);
				}

				// permit application xref
				permitDAO().removePermitApplications(permitId);

				// offset tracking data
				for (EmissionsOffset eo : permitDAO().retrievePermitEmissionsOffsetByPermitId(permitId)) {
					if (null != eo) {
						permitDAO().deletePermitEmissionsOffset(eo.getEmissionsOffsetId());
					}
				}

				// attachments
				permitDAO().markTempPermitDocuments(permitId);
				permitDAO().removePermitDocuments(permitId);

				// type specific
				if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
					permitDAO().removePTIOPermit(permitId);
				} else if (permit.getPermitType().equals(PermitTypeDef.TV_PTO)) {
					permitDAO().removeTVPermit(permitId);
				}

				// finally delete the permit itself
				permitDAO().deletePermit(permitId);
			}

		}
	}

//	@Override
//	public void removePermitConditionCategoryXref(Permit permit) throws DAOException {
//		 permitDAO().removePermitConditionCategoryXref(permit.getPermitID());
//		
//	}
//
////	@Override
//	public void removePermitConditionEUXref(Permit permit) throws DAOException {
//		 permitDAO().removePermitConditionEUXref(permit.getPermitID());
//		
//	}

	
}

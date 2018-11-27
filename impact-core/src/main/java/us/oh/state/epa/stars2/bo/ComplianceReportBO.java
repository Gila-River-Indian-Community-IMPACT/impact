package us.oh.state.epa.stars2.bo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.DocumentException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.ComplianceReportDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.StackTestDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceDeviation;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceDocument;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportAttachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportCategoryInfo;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportLimit;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportMonitor;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportNote;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.framework.exception.WorkflowRollbackException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.compliance.PerReportingPeriod;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;
import us.oh.state.epa.stars2.webcommon.pdf.compliance.ComplianceReportPdfGenerator;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dao.ContinuousMonitorDAO;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorEqt;

//import us.oh.state.epa.stars2.migration.MigUtil;

/** Business object for store and load of compliance reports. */
@Transactional(rollbackFor = Exception.class)
@Service
public class ComplianceReportBO extends BaseBO implements
		ComplianceReportService {
	
	public static final String RO_SIGNATURE_DOC_TYPE = "10";

	/**
	 * Create a new row in the CR_COMPLIANCE_REPORT table.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	@Override
	public ComplianceReport createComplianceReport(
			ComplianceReport complianceReport, Facility facility)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ComplianceReport ret = null;
		try {
			ret = createComplianceReport(complianceReport, facility, trans,
					false);
			ret.setDirty(false);

			trans.complete();
		} catch (DAOException e) {
			logger.error("Compliance Report DAO Exception for facility "
					+ facility.getFacilityId(), e);
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			logger.warn("closing transaction");
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param complianceReport
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private ComplianceReport createComplianceReport(
			ComplianceReport complianceReport, Facility facility,
			Transaction trans, boolean fromPortalSubmit) throws DAOException {
		ComplianceReport ret = null;
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
		FacilityDAO facDAO = facilityDAO(trans);
		EmissionUnit eu;
		String path = "";
		
		try {
			// set the fpId for the report if this is a 
			// quarterly cem/com monitoring or annual RATA report
			if(complianceReport.isCemsComsRataRpt()) {
				if(null == facility) {
					throw new DAOException("Facility is null");
				}	
				logger.debug("Setting the fpId in the compliance report to " + facility.getFpId());
				complianceReport.setFpId(facility.getFpId());
			}
			ComplianceReport tempComplianceReport = complianceDAO
					.createComplianceReport(complianceReport);
			tempComplianceReport.refreshCompReportMonitorList();
			
			//logger.warn("Staging in BO is: " + complianceReport.isStaging());
			if (complianceReport.isCreateFileDir()) {
				// create the folder for attachments
				path = File.separator + "Facilities" + File.separator
						+ complianceReport.getFacilityId() + File.separator
						+ "ComplianceReports" + File.separator
						+ complianceReport.getReportId();
				DocumentUtil.mkDir(path);
			}
			
			if (complianceReport.isCemsComsRataRpt() && !complianceReport.isLegacyFlag()) {
				if (fromPortalSubmit) {
					createMonitorLimitEntriesFromPortal(tempComplianceReport);
					
					// sync the montiors and limits in the compliance report with the facility
					syncPortalComplianceReportWithCurrentFacility(tempComplianceReport, facility);
				} else {
					createMonitorLimitEntries(tempComplianceReport);
				}
			}

			ret = tempComplianceReport;

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
			// }catch (ServiceFactoryException se) {
			// throw new
			// DAOException("Failed to get Service Factory for FacilityBO", se);
		} catch (IOException ioe) {
			logger.error(
					"Failed to make directory for attachments. Create FileDir = "
							+ complianceReport.isCreateFileDir()
							//+ " and Staging var = "
							//+ complianceReport.isStaging()
							+ " and isInternalApp = " + isInternalApp()
							+ ".  Path is " + path, ioe);
			throw new DAOException("Failed to make directory for attachments",
					ioe);
		}
		return ret;
	}

	/**
	 * Copy data from fromReport to toReport and save changes to the database
	 * 
	 * @param fromReport
	 * @param toReport
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void copyComplianceReportData(ComplianceReport fromReport,
			ComplianceReport toReport) throws DAOException {
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			copyComplianceReportData(fromReport, toReport, trans);
			trans.complete();
		} catch (DAOException e) {
			logger.error("Exception creating compliance report copy from "
					+ fromReport.getReportId());
			cancelTransaction(trans, new DAOException(e.getMessage()));
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/**
	 * Copy data from fromReport to toReport and save changes to the database
	 * 
	 * @param fromReport
	 * @param toReport
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private void copyComplianceReportData(ComplianceReport fromReport,
			ComplianceReport toReport, Transaction trans) throws DAOException {
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
		// DocumentDAO docDAO = documentDAO(trans);

		toReport.setPerDueDateCd(fromReport.getPerDueDateCd());
		toReport.setOtherCategoryCd(fromReport.getOtherCategoryCd());
		toReport.setReportType(fromReport.getReportType());
		toReport.setUserId(fromReport.getUserId());
		toReport.setFacilityId(fromReport.getFacilityId());

		// Don't copy comments for TVCC reports (Mantis 2896)
		if (!toReport.getReportType().equals("tvcc")) {
			toReport.setComments(fromReport.getComments());
		}
		// Mantis 2811 - don't overwrite reporting year when data is copied
		// toReport.setTvccReportingYear(fromReport.getTvccReportingYear());
		toReport.setPerStartDate(fromReport.getPerStartDate());
		toReport.setPerEndDate(fromReport.getPerEndDate());
		toReport.setPerDueDate(fromReport.getPerDueDate());
		toReport.setReportTypeDesc(fromReport.getReportTypeDesc());

		// exclude some fields from copying (Mantis 2896)
		// toReport.setDapcDeviationsReported(fromReport.getDapcDeviationsReported());
		// toReport.setDapcReviewComments(fromReport.getDapcReviewComments());
		// toReport.setTvccComplianceStatusCd(fromReport.getTvccComplianceStatusCd());

		complianceDAO.modifyComplianceReport(toReport);

		// copy deviations
		for (ComplianceDeviation dev : fromReport.getDeviationReports()) {
			ComplianceDeviation devCopy = new ComplianceDeviation(dev);
			devCopy.setReportId(toReport.getReportId());
			createComplianceDeviation(devCopy, trans);
		}

		// Copying of attachments has been disabled for Mantis 2896
		// code has been left here in case that decision is reversed
		// copy attachments
		// if (fromReport.getAttachments() != null &&
		// fromReport.getAttachments().length > 0) {
		// Attachment[] attachmentsCopy = new
		// Attachment[fromReport.getAttachments().length];
		// for (int i=0; i < fromReport.getAttachments().length; i++) {
		// Attachment attCopy =
		// createAttachmentCopy(fromReport.getAttachments()[i],
		// toReport.getReportId());
		// copyDocument(fromReport.getAttachments()[i], attCopy, docDAO);
		// attachmentsCopy[i] = attCopy;
		// complianceDAO.createComplianceAttachment(toReport,attCopy);
		// // handle trade secret documents
		// if (fromReport.getAttachments()[i].getTradeSecretDocId() != null) {
		// Document tsDoc =
		// docDAO.retrieveDocument(fromReport.getAttachments()[i].getTradeSecretDocId());
		// Attachment tsCopy = createAttachmentCopy(tsDoc,
		// toReport.getReportId());
		// tsCopy.setReportId(toReport.getReportId());
		// copyDocument(fromReport.getAttachments()[i], tsCopy, docDAO);
		// attCopy.setTradeSecretDocId(tsCopy.getDocumentID());
		// complianceDAO.createCRTradeSecretAttachment(attCopy);
		// }
		// }
		// toReport.setAttachments(attachmentsCopy);
		// }
	}

	// private Attachment createAttachmentCopy(Document orig, int reportId) {
	// Attachment attCopy = new Attachment(orig);
	// attCopy.setReportId(reportId);
	// attCopy.setObjectId(String.valueOf(reportId));
	// attCopy.setAttachmentBasePath(null);
	// attCopy.setSubPath("ComplianceReports");
	// return attCopy;
	// }

	// private void copyDocument(Document fromDoc, Document toDoc, DocumentDAO
	// docDAO) throws DAOException {
	// // first copy the actual file for this document
	// try {
	// toDoc.setDocumentID(null);
	// // clear out last modified by if document came from migration (Mantis
	// 2065)
	// if (new
	// Integer(MigUtil.MIG_LEGACY_USER_ID).equals(toDoc.getLastModifiedBy())) {
	// toDoc.setLastModifiedBy(CommonConst.ADMIN_USER_ID);
	// }
	// docDAO.createDocument(toDoc);
	// DocumentUtil.copyDocument(fromDoc.getPath(), toDoc.getPath());
	// } catch (IOException e) {
	// throw new DAOException("Exception copying document from " +
	// fromDoc.getPath() +
	// " to " + toDoc.getPath(), e);
	// }
	// }

	/**
	 * Create compliance report submitted from the portal.
	 * 
	 * @param complianceReport
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ComplianceReport createComplianceReportFromPortal(
			ComplianceReport complianceReport, Facility facility, 
			String[] targetFacilityIds, Transaction trans)
			throws DAOException {

		complianceReport.setCreateFileDir(false);
		ComplianceReport created = null;
		for (ComplianceReportAttachment a : complianceReport.getAttachments()) {
			a.setOriginalDocId(a.getDocumentID());
			a.setOriginalTradeSecretDocId(a.getTradeSecretDocId());
		}
		try {
			created = createComplianceReportFromPortal(complianceReport,
					facility, facility.getFacilityId(), trans);

			// create compliance reports for additional facilities, using
			// the given compliance report as a template
			for (String facilityId : targetFacilityIds) {
				prepareForCopy(complianceReport, facilityId);
				ComplianceReport createdCopy = 
						createComplianceReportFromPortal(complianceReport, null, 
								facilityId, trans);
				ComplianceReportNote bulkOpNote = new ComplianceReportNote();
				bulkOpNote.setReportId(createdCopy.getReportId());
				bulkOpNote.setNoteTxt("[Compliance_Memo] This report was created" + 
						" during a bulk submission.  The originating compliance" + 
						" report ID is: " +	complianceReport.getReportCRPTId());
				bulkOpNote.setDateEntered(new Timestamp(new java.util.Date().getTime()));
				bulkOpNote.setNoteTypeCd(NoteType.DAPC);
				bulkOpNote.setUserId(CommonConst.GATEWAY_USER_ID);
				createNote(bulkOpNote);
				
				ComplianceReport retrievedCopy = 
						retrieveComplianceReport(createdCopy.getReportId());
				
				// Version without Trade Secret attachments listed.
				ComplianceDocument rpt1 = new ComplianceDocument();
				createComplianceReportPDFDocument(retrievedCopy, rpt1,
						null, true, false, true);

				// Trade secret version.
				ComplianceDocument rpt2 = new ComplianceDocument();
				createComplianceReportPDFDocument(retrievedCopy, rpt2,
						null, false, false, true);

			}
		} catch (IOException e) {
			logger.error("IOException occurred while creating compliance report from portal: " + e.getMessage(),e);
			throw new DAOException(e.getLocalizedMessage(), e);
		}
		return created;
	}

	private void prepareForCopy(ComplianceReport complianceReport,
			String facilityId) {
		complianceReport.setReportId(null);
		complianceReport.setFacilityId(facilityId);
		complianceReport.setFacilityNm(null);
		complianceReport.setFacilityTypeCd(null);
		complianceReport.setFpId(null);
	}

	private ComplianceReport createComplianceReportFromPortal(
			ComplianceReport complianceReport, Facility facility, String facilityId, 
			Transaction trans) throws IOException {
		
		// original report, not one of the copied reports in a bulk submission
		boolean originalReport = null != facility; 

		ComplianceReport created = 
				createComplianceReport(complianceReport, facility, trans, true);

		// Attachments
		ComplianceReportAttachment att[] = created.getAttachments();
		if (att != null) {
			for (int i = 0; i < att.length; i++) {
				try {
					InputStream publicDocInputStream = null;
					InputStream originalTsDocInputStream = null;
					if (!originalReport) {
						File publicDocFile = 
								new File(DocumentUtil.getFileStoreRootPath() + att[i].getPath());
						publicDocInputStream = 
								FileUtils.openInputStream(publicDocFile);
						
						if (att[i].getTradeSecretDocId() != null) {
							Document originalTsDoc = 
									documentDAO().retrieveDocument(att[i].getTradeSecretDocId());
							String originalTsDocFilePath = 
									DocumentUtil.getFileStoreRootPath() + 
									originalTsDoc.getPath().replace(
											att[i].getTradeSecretDocId().toString(),
											att[i].getOriginalTradeSecretDocId().toString()
											);
							File originalTsDocFile = 
									new File(originalTsDocFilePath);
							if (!originalTsDocFile.exists()) {
								originalTsDocFile = new File(DocumentUtil.getFileStoreRootPath() + 
										originalTsDoc.getPath());
							}
							originalTsDocInputStream = 
									FileUtils.openInputStream(originalTsDocFile);
						}						
						att[i].setDocumentID(null);
						att[i].setFacilityID(facilityId);
						att[i].setReportId(created.getReportId());
						att[i].setObjectId(created.getReportId().toString());
						att[i].setAttachmentBasePath(null);
						att[i].setSubPath("ComplianceReports");
					}
					
					createComplianceAttachmentFromPortal(created, att[i], trans);

					if (originalReport) {
						if (att[i].getTradeSecretDocId() != null) {
							Document tsDoc = 
									documentDAO().retrieveDocument(att[i].getTradeSecretDocId());
							String tsDocFilePath = 
									DocumentUtil.getFileStoreRootPath() + 
									tsDoc.getPath().replace(
											att[i].getTradeSecretDocId().toString(),
											att[i].getOriginalTradeSecretDocId().toString()
											);
							File tsDocFile = 
									new File(tsDocFilePath);
							if (!tsDocFile.exists()) {
								tsDocFile = new File(DocumentUtil.getFileStoreRootPath() + 
										tsDoc.getPath());
							}
							
							InputStream tsDocInputStream = 
									FileUtils.openInputStream(tsDocFile);
							DocumentUtil.createDocument(tsDoc.getPath(), 
									tsDocInputStream);
						}
					} else {
						DocumentUtil.createDocument(att[i].getPath(), 
								publicDocInputStream);
						if (att[i].getTradeSecretDocId() != null) {
							Document tsDoc = 
									documentDAO().retrieveDocument(att[i].getTradeSecretDocId());
							DocumentUtil.createDocument(tsDoc.getPath(), 
									originalTsDocInputStream);
						}						
					}
				} catch (ValidationException ve) {
					throw new DAOException("Failed to create new Attachment");
				}
			}
		} else {
			logger.error("Null response from getAttachments() for Compliance Report "
					+ created.getReportId());
		}

		Integer fpId = facilityDAO().retrieveFpIdByFacilityId(facilityId);
		
		try {
			createWorkflow(created, fpId);
		} catch (RemoteException re) {
			logger.error(
					"Unable to create workflow for submitted Compliance Report "
							+ created.getReportId(), re);
			throw new DAOException(re.getLocalizedMessage(), re);
		} catch (ServiceFactoryException sfe) {
			logger.error(
					"Unable to create workflow for submitted Compliance Report "
							+ created.getReportId(), sfe);
			throw new DAOException(sfe.getLocalizedMessage(), sfe);
		}
		return created;
	}

	/**
	 * Delete row in the DC_CORRESPONDENCE table.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void deleteComplianceReport(ComplianceReport complianceReport)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			// Delete all attachments
			ComplianceReportAttachment[] attachments = complianceReport
					.getAttachments();
			if (attachments != null) {
				for (ComplianceReportAttachment attachment : attachments) {
					deleteComplianceAttachment(complianceReport, attachment,
							trans);
				}
			}

			// Delete all notes
			Note[] notes = complianceReport.getNotes();
			if (notes != null) {
				for (Note note : notes) {
					removeNote((ComplianceReportNote) note, trans);
				}
			}

			// Delete all compliance report limits and monitors
			// for this compliance report.
			deleteAllCrMonitorsAndLimits(complianceReport);

			ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
			complianceDAO.deleteComplianceReport(complianceReport);
			trans.complete();
		} catch (DAOException e) { // Throw it all away if we have an Exception
			logger.error("ComplianceReport Deletion error for "
					+ complianceReport.getReportId());
			cancelTransaction(trans, e);
			throw e;
		} finally {
			closeTransaction(trans);
		}

		return;

	}

	/**
	 * Delete row in the DC_CORRESPONDENCE table.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void deleteComplianceReport(ComplianceReport complianceReport,
			Transaction trans) throws DAOException {
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);

		try {
			complianceDAO.deleteComplianceReport(complianceReport);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			logger.error("ComplianceReport Deletion error for "
					+ complianceReport.getReportId(), e);
			throw e;
		}
		return;

	}

	/**
	 * Delete row in the DC_CORRESPONDENCE table.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void deleteComplianceReport(ComplianceReport complianceReport,
			Transaction trans, boolean removeFiles) throws DAOException {
		try {
			DocumentDAO documentDAO = documentDAO(trans);
			// Delete all attachments
			ComplianceReportAttachment[] attachments = complianceReport
					.getAttachments();
			if (attachments != null) {
				for (ComplianceReportAttachment attachment : attachments) {
					deleteComplianceAttachment(complianceReport, attachment,
							trans, removeFiles);
					// cleanup document in staging
					if (isPortalApp()) {
						if (null != attachment.getDocumentID()) {
							documentDAO.removeDocumentReference(attachment
									.getDocumentID());
						}

						if (null != attachment.getTradeSecretDocId()) {
							documentDAO.removeDocumentReference(attachment
									.getTradeSecretDocId());
						}
					}
				}
			}

			// Delete all notes
			Note[] notes = complianceReport.getNotes();
			if (notes != null) {
				for (Note note : notes) {
					removeNote((ComplianceReportNote) note, trans);
				}
			}
			
			// Delete all compliance report limits and monitors
			// for this compliance report.
			deleteAllCrMonitorsAndLimits(complianceReport);

			ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
			complianceDAO.deleteComplianceReport(complianceReport, removeFiles);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			logger.error(
					"ComplianceReport Deletion error "
							+ complianceReport.getReportId(), e);
			throw e;
		}
		return;

	}

	/**
	 * Delete row in the CR_COMPLIANCE_DEVIATION table.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean deleteComplianceDeviation(
			ComplianceDeviation complianceDeviation) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean result;
		try {
			deleteComplianceDeviation(complianceDeviation, trans);
			trans.complete();
			result = true;
		} catch (DAOException e) {
			cancelTransaction(trans, e);
			result = false;
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return result;

	}

	/**
	 * Delete row in the DC_CORRESPONDENCE table.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */

	public boolean deleteComplianceAttachment(
			ComplianceReport complianceReport,
			ComplianceReportAttachment attachment) throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		try {
			deleteComplianceAttachment(complianceReport, attachment, trans, isPortalApp());
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return true;
	}

	private void deleteComplianceAttachment(ComplianceReport complianceReport,
			ComplianceReportAttachment attachment, Transaction trans, boolean removeAttachments)
			throws DAOException {
		DocumentDAO documentDAO = documentDAO(trans);
		ComplianceReportDAO compReportDAO = complianceReportDAO(trans);
		Document tsDocument = null;
		
		// first, flag document as temporary so it will be cleaned up later
		attachment.setTemporary(true);
		documentDAO.modifyDocument(attachment);
					
		// then delete trade secret attachment (if any)
		if (attachment.getTradeSecretDocId() != null) {
			tsDocument = documentDAO.retrieveDocument(attachment
					.getTradeSecretDocId());
			if (tsDocument != null) {
				tsDocument.setTemporary(true);
				documentDAO.modifyDocument(tsDocument);
			} else {
				logger.error("No document found in compliance report "
						+ complianceReport.getReportId()
						+ " for trade secret document: "
						+ attachment.getTradeSecretDocId());
			}
			compReportDAO.deleteCRTradeSecretAttachment(attachment);
		}

		if(isPortalApp() && removeAttachments){
			documentDAO.removeDocument(attachment);
			if (tsDocument != null) {
				documentDAO.removeDocument(tsDocument);
			}
		}
		
		// then, delete record from compliance report attachment table
		compReportDAO.deleteComplianceAttachment(complianceReport, attachment);
		compReportDAO.setComplianceReportValidatedFlag(complianceReport.getReportId(), false);
	}
	
	private void deleteComplianceAttachment(ComplianceReport complianceReport,
			ComplianceReportAttachment attachment, Transaction trans)
			throws DAOException {
		deleteComplianceAttachment(complianceReport, attachment, trans, false);
	}

	/**
	 * @param complianceReportList
	 * 
	 * @param String
	 *            facilityId either the exact ID, a wildcarded string or null.
	 * @param String
	 *            facilityName either the exact Name, a wildcarded string or
	 *            null.
	 * @param String
	 *            reportType either the exact report CD or null.
	 * @param String
	 *            reportStatus either the exact report Status CD or null.
	 * @param String
	 *            reportPeriod or null
	 * @param String
	 *            deviations either the exact deviations CD, or null
	 * @return ComplianceReport[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ComplianceReportList[] searchComplianceReports(String reportCRPTId,
			String facilityID, String facilityName, String doLaaCd,
			String reportType, String reportStatus, String reportYear,
			String deviationsReported, String dateBy, Date dtBegin, Date dtEnd,
			String reportAccepted, String otherTypeCd, boolean readOnlySchema,
			boolean unlimitedResults, String cmpId,
			String permitClassCd, String facilityTypeCd, String dapcReviewComments) throws DAOException {

		String schema = getSchema(readOnlySchema ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA);

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(schema);

		return complianceReportDAO.searchComplianceReport(reportCRPTId,
				facilityID, facilityName, doLaaCd, reportType, reportStatus,
				reportYear, deviationsReported, dateBy, dtBegin, dtEnd,
				reportAccepted, otherTypeCd, cmpId, permitClassCd, facilityTypeCd,
				unlimitedResults, dapcReviewComments);
	}

	/**
	 * @return List<ComplianceReport>
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<ComplianceReportList> newAfsTvCc() throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ComplianceReportDAO complianceReportDAO = complianceReportDAO(trans);
		return complianceReportDAO.newAfsTvCc();
	}

	/**
	 * @param complianceReportList
	 * 
	 * @param String
	 *            facilityId either the exact ID, a wildcarded string or null.
	 * @param String
	 *            facilityName either the exact Name, a wildcarded string or
	 *            null.
	 * @param String
	 *            reportType either the exact report CD or null.
	 * @param String
	 *            reportStatus either the exact report Status CD or null.
	 * @param String
	 *            reportPeriod or null
	 * @param String
	 *            deviations either the exact deviations CD, or null
	 * @return ComplianceReport[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ComplianceReportList[] searchComplianceReports(String reportCRPTId,
			String facilityID, String facilityName, String doLaaCd,
			String reportType, String reportStatus, String reportYear,
			String deviationsReported, String dateBy, Date dtBegin, Date dtEnd,
			String reportAccepted, String otherTypeCd,
			boolean unlimitedResults, String cmpId,
			String permitClassCd, String facilityTypeCd, String dapcReviewComments)
					throws DAOException {
		return searchComplianceReports(reportCRPTId, facilityID, facilityName,
				doLaaCd, reportType, reportStatus, reportYear,
				deviationsReported, dateBy, dtBegin, dtEnd, reportAccepted,
				otherTypeCd, false, unlimitedResults, cmpId,
				permitClassCd, facilityTypeCd, dapcReviewComments);
	}

	public ComplianceReportList[] searchComplianceReportByFacilityAndStatus(String facilityID, String reportStatus)
					throws DAOException {
		return searchComplianceReports(null, facilityID, null,
				null, null, reportStatus, null,
				null, null, null, null, null,
				null, false, true, null,
				null, null, null);
	}

	/**
	 * Locate all of the compliance reports for a given facility.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ComplianceReportList[] searchComplianceReportByFacility(
			String facilityID, boolean readOnlySchema) throws DAOException {
		String schema = getSchema(readOnlySchema ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA);
		return complianceReportDAO(schema).searchComplianceReportByFacility(
				facilityID);

	}

	/**
	 * Locate all of the compliance reports for a given facility.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ComplianceReportList[] searchComplianceReportByFacility(
			String facilityID) throws DAOException {
		return searchComplianceReportByFacility(facilityID, true);

	}

	/**
	 * @param complianceReport
	 * @param trans
	 * @return
	 * @throws DAOException
	 * 
	 *             Should only be used if we're not in the portal
	 */
	public ComplianceReport modifyComplianceReport(
			ComplianceReport complianceReport, int userId, Transaction trans)
			throws RemoteException {
		ComplianceReport ret = null;

		try {
			ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
			InfrastructureDAO infraDAO = infrastructureDAO(trans);
			
			if (complianceDAO.modifyComplianceReport(complianceReport)) {

				logger.debug("logging changes for compliance report to FAL"); 
				// we only update this if we aren't in staging
				
				if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
					infraDAO.createFieldAuditLogs(
							complianceReport.getFacilityId(),
							complianceReport.getFacilityNm(), userId,
							complianceReport.getFieldAuditLogs());
				}
				// Update all compliance report monitors and associated
				// compliance report limits
				// for this compliance report.

				List<ComplianceReportMonitor> monitors = complianceReport
						.getCompReportMonitorList();

				for (ComplianceReportMonitor cm : monitors) {
					modifyComplianceReportMonitor(cm);

					for (ComplianceReportLimit cl : cm
							.getComplianceReportLimitList()) {
						modifyComplianceReportLimit(cl);
					}
				}
				
				ret = complianceReport;

			} else {
				logger.error("error logging changes to FAL for compliance report "
						+ complianceReport.getReportId());
				ret = null;
			}
		} catch (RemoteException e) { // Throw it all away if we have an Exception
			logger.error("Error modifying compliance report "
					+ complianceReport.getReportId(), e);
			throw e;
		} 
		return ret;
	}

	/**
	 * @param complianceDeviation
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private ComplianceDeviation modifyComplianceDeviation(
			ComplianceDeviation complianceDeviation, Transaction trans)
			throws DAOException {
		ComplianceDeviation ret = null;
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
		try {
			complianceDAO.modifyComplianceDeviation(complianceDeviation);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
		return ret;
	}

	/**
	 * @param complianceReport
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private void deleteComplianceDeviation(
			ComplianceDeviation complianceDeviation, Transaction trans)
			throws DAOException {
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);

		try {
			complianceDAO.deleteComplianceDeviation(complianceDeviation);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

	}

	public boolean modifyComplianceReport(ComplianceReport complianceReport,
			int userId) throws DAOException {
		boolean ret;
		try {
			ret = modifyComplianceReportInternal(complianceReport, userId, 
					complianceReport.getTargetFacilityIds());
		} catch (WorkflowRollbackException e) {
			logger.debug("Removing the workflow process");
			try {
				removeProcessFlows(e.getExtId(), e.getWfId(), userId);
			} catch (Exception e2) {
				logger.error("FAILED attempt to remove workflow for "
						+ e.getWfId(), e2);
			}
			throw new DAOException("modifyComplianceReport failed");
		}
		return ret;
	}
	
	/**
	 * @param complianceReport
	 * @throws WorkflowRollbackException 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	private boolean modifyComplianceReportInternal(ComplianceReport complianceReport,
			int userId, String[] targetFacilityIds) throws DAOException, WorkflowRollbackException {

		boolean ret = modifyComplianceReportInternal(complianceReport, userId);
		String reportId = String.valueOf(complianceReport.getReportId());
		String crptId =  String.valueOf(complianceReport.getReportCRPTId());

		// create compliance reports for additional facilities, using
		// the given compliance report as a template
		for (String facilityId : targetFacilityIds) {
			prepareForCopy(complianceReport, facilityId);
			complianceReport = createComplianceReport(complianceReport, null);
			String newReportId = String.valueOf(complianceReport.getReportId());
			modifyComplianceReportInternal(complianceReport, userId);

			ComplianceReportAttachment[] attachments = complianceReport.getAttachments();
			try {
				for (ComplianceReportAttachment attachment: attachments) {

					ComplianceReportAttachment publicCopy = new ComplianceReportAttachment(attachment);
					publicCopy.setDocumentID(null);
					publicCopy.setFacilityID(facilityId);
					publicCopy.setReportId(complianceReport.getReportId());
					publicCopy.setAttachmentBasePath(publicCopy.getAttachmentBasePath().replace(reportId,newReportId));
					publicCopy.setBasePath(publicCopy.getBasePath().replace(reportId,newReportId));

					File publicDocFile = new File(DocumentUtil.getFileStoreRootPath() + attachment.getPath());
					InputStream publicDocInputStream = FileUtils.openInputStream(publicDocFile);
	
					// get ts document, create copy of file
					attachment.getTradeSecretDocId();

					// process trade secret attachment (if there is one)
					ComplianceReportAttachment tsCopy = null;
					InputStream tsDocInputStream = null;
					if (attachment.getTradeSecretDocId() != null) {
						
						Document tsDoc = documentDAO().retrieveDocument(attachment.getTradeSecretDocId());
						
						tsCopy = new ComplianceReportAttachment();
						tsCopy.setLastModifiedTS(new Timestamp(System
								.currentTimeMillis()));
						tsCopy.setUploadDate(tsCopy
								.getLastModifiedTS());
						tsCopy.setExtension(DocumentUtil
								.getFileExtension(attachment.getTradeSecretDocURL()));
						if (isInternalApp()) {
							tsCopy.setLastModifiedBy(InfrastructureDefs
									.getCurrentUserId());
						} else {
							tsCopy
									.setLastModifiedBy(CommonConst.GATEWAY_USER_ID);
						}
						tsCopy.setReportId(complianceReport.getReportId());
						// need object id to be set to put file in correct
						// directory
						tsCopy.setObjectId(complianceReport.getReportId()
								.toString());
						tsCopy.setFacilityID(complianceReport
								.getFacilityId());
						tsCopy.setSubPath("ComplianceReports");
						tsCopy.setAttachmentBasePath(tsDoc.getBasePath().replace(reportId,newReportId));
						tsCopy.setBasePath(tsDoc.getBasePath().replace(reportId,newReportId));
						tsCopy.setTradeSecretJustification(attachment.getTradeSecretJustification());

						// copy file
						File tsDocFile = new File(DocumentUtil.getFileStoreRootPath() + tsDoc.getPath());
						tsDocInputStream = FileUtils.openInputStream(tsDocFile);
					}
					createComplianceAttachment(
							complianceReport,
							publicCopy,
							publicDocInputStream, 
							tsCopy,
							tsDocInputStream);
				}
				
				Note[] notes = complianceReport.getNotes();
				for (Note n : notes) {
					ComplianceReportNote note = new ComplianceReportNote();
					note.setReportId(complianceReport.getReportId());
					note.setNoteTxt(n.getNoteTxt());
					note.setDateEntered(n.getDateEntered());
					note.setNoteTypeCd(n.getNoteTypeCd());
					note.setUserId(n.getUserId());
					createNote(note);
				}
				
				ComplianceReportNote bulkOpNote = new ComplianceReportNote();
				bulkOpNote.setReportId(complianceReport.getReportId());
				bulkOpNote.setNoteTxt("[Compliance_Memo] This report was created during a bulk submission.  The originating compliance report ID is: " + crptId);
				bulkOpNote.setDateEntered(new Timestamp(new java.util.Date().getTime()));
				bulkOpNote.setNoteTypeCd(NoteType.DAPC);
				bulkOpNote.setUserId(userId);
				createNote(bulkOpNote);
				
				ComplianceReport retrievedCopy = 
						retrieveComplianceReport(complianceReport.getReportId());
				String userName = 
						InfrastructureDefs.getCurrentUserAttrs().getUserName();
				
				// Version without Trade Secret attachments listed.
				ComplianceDocument rpt1 = new ComplianceDocument();
				createComplianceReportPDFDocument(retrievedCopy, rpt1,
						userName, true, false, true);

				// Trade secret version.
				ComplianceDocument rpt2 = new ComplianceDocument();
				createComplianceReportPDFDocument(retrievedCopy, rpt2,
						userName, false, false, true);

				
			} catch (IOException e) {
				throw new DAOException("File IO error while processing attachments",e);
			}
			

		}
		return ret;
	}
	
	private boolean modifyComplianceReportInternal(ComplianceReport complianceReport,
			int userId) throws DAOException, WorkflowRollbackException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		Integer workflowId = null;
		try {
		
			// null out the fpId if this is a legacy compliance report
			if(complianceReport.isLegacyFlag() 
					&& complianceReport.getFpId() != null) {
				complianceReport.setFpId(null);
			}
			
			modifyComplianceReport(complianceReport, userId, trans);
			if (complianceReport.isReportBeingSubmitted()) {
				
				logger.debug("Being submitted; createing workflow task");
				// createToDo(complianceReport, trans);
				if (!complianceReport.isLegacyFlag()) {
					workflowId = createWorkflow(complianceReport, null);
				}

				if (complianceReport.isSecondGenerationCemComRataRpt()) {
					FacilityDAO facDAO = facilityDAO(trans);
					Facility facility = facDAO.retrieveFacility(complianceReport
							.getFpId());
					if (!facility.isCopyOnChange()) {
						facility.setCopyOnChange(true);
						facDAO.modifyFacility(facility);
					}
				}
				
			}
			
			// If the Compliance Report is marked as Legacy, remove the monitors and limits
			// from CEMS/COMS/RATA reports.
			if (complianceReport.isCemsComsRataRpt() && complianceReport.isLegacyFlag()) {
				deleteAllCrMonitorsAndLimits(complianceReport);
			}

			ret = true;
		} catch (Exception e) {
			logger.error("Unable to submit Compliance Report " + 
					complianceReport.getReportId(), e);
			
			if (complianceReport.isReportBeingSubmitted() && 
					complianceReport.isCemsComsRataRpt() && null != workflowId) {
				throw new WorkflowRollbackException(
						"Exception while submitting compliance report "
								+ complianceReport.getReportId(), e, workflowId, 
								complianceReport.getReportId());
			}
			
			throw new DAOException(e.getLocalizedMessage(), e);
		}
		return ret;
	}

	/**
	 * @param complianceReport
	 * @param task
	 * @param userid
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyComplianceReport(ComplianceReport complianceReport,
			Task task, int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		boolean ret = false;
		try {
			modifyComplianceReport(complianceReport, userId, trans);
			/*
			if (complianceReport.getComments() != null) {
				if (complianceReport.getComments().length() > 40) {
					task.setTaskDescription(complianceReport.getComments()
							.substring(0, 37) + "...");
				} else {
					task.setTaskDescription(complianceReport.getComments());
				}
			}
			*/
			trans.complete();
			ret = true;
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} catch (Exception ex) {
			// let the record go through but log any to-do list errors
			String s = "Exception modifying compliance report "
					+ complianceReport.getReportId();
			logger.error(s, ex);
			throw new DAOException(s, ex);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param complianceDeviation
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */

	public boolean modifyComplianceDeviation(
			ComplianceDeviation complianceDeviation) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		try {
			modifyComplianceDeviation(complianceDeviation, trans);
			trans.complete();
			ret = true;
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param attachment
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */

	public boolean modifyComplianceAttachment(ComplianceReport compReport,
			ComplianceReportAttachment complianceAttachment)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = true;
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
		DocumentDAO documentDAO = documentDAO(trans);
		try {
			complianceAttachment.setLastModifiedTS(new Timestamp(System
						.currentTimeMillis()));
			if(complianceAttachment.getUploadDate() == null) {
				complianceAttachment.setUploadDate(complianceAttachment
					.getLastModifiedTS());
			}
			documentDAO.modifyDocument(complianceAttachment);
			complianceDAO.modifyComplianceAttachment(compReport,
					complianceAttachment);
			if (complianceAttachment.getTradeSecretDocId() != null) {
				complianceDAO
						.modifyCRTradeSecretAttachment(complianceAttachment);
			}
			trans.complete();
		} catch (DAOException e) {
			ret = false;
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param complianceReportId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ComplianceReport retrieveComplianceReport(int complianceReportId) throws DAOException {
		boolean readOnlySchema = true;
		if (isPortalApp()) {
			readOnlySchema = false;
		}
		return retrieveComplianceReport(complianceReportId, readOnlySchema);
	}

	/**
	 * @param complianceReportId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ComplianceReport retrieveComplianceReport(int complianceReportId,
			 boolean readOnlySchema) throws DAOException {
		String schema = getSchema(readOnlySchema ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA);
		ComplianceReport report = complianceReportDAO(schema)
				.retrieveComplianceReport(complianceReportId);

		if (report == null) {
			return null;
		}
		String[] array = new String[2];
		array[0] = "Submitted by: ";
		array[1] = "";

		try {
			InfrastructureDAO infraDAO = infrastructureDAO();
			if (report.getUserId() == CommonConst.GATEWAY_USER_ID) {
				UserDef c = infraDAO
						.retrieveContactByUserId(report.getUserId());
				if (c == null) {
					// don't generate any errors
				} else {
					array[1] = c.getNameOnly();
				}
			} else {
				array[0] = SystemPropertyDef.getSystemPropertyValue("EnteredByDapcLabel", null);
				if (!isInternalApp()) {
					array[1] = SystemPropertyDef.getSystemPropertyValue("EnteredByDapcValue", null);
				} else {
					UserDef c = infraDAO.retrieveContactByUserId(report
							.getUserId());
					if (c == null) {
						// don't generate any errors
					} else {
						array[1] = c.getNameOnly();
					}
				}
			}
		} catch (DAOException e) {
			// don't generate any errors
		}
		report.setSubmitLabel(array[0]);
		report.setSubmitValue(array[1]);

		// retrieve attachments and any trade secret attachments that may exist
		ComplianceReportAttachment[] attachments = complianceReportDAO(schema)
				.retrieveAttachments(report.getReportType(),
						report.getFacilityId(), report.getReportId());
		for (ComplianceReportAttachment attachment : attachments) {
			
				attachment.setReportId(report.getReportId());
				complianceReportDAO(schema)
						.retrieveCRTradeSecretAttachmentInfo(attachment);
				// need to retrieve trade secret document to build URL
				if (attachment.getTradeSecretDocId() != null) {
					Document tsDoc = documentDAO(schema).retrieveDocument(
							attachment.getTradeSecretDocId());
					if (tsDoc != null) {
						attachment.setTradeSecretDocURL(tsDoc.getDocURL());
					} else {
						// should never happen
						logger.error("Unable to retrieve Trade Secret Document: "
								+ attachment.getTradeSecretDocId()
								+ " for report " + complianceReportId);
					}
				}
		}
		report.setAttachments(attachments);
		report.setNotes(complianceReportDAO().retrieveNotes(
				report.getReportId()));
		
		report.setMonitors(complianceReportDAO(schema).retrieveComplianceReportMonitorListByReportId(report.getReportId()));
		/* TFS: 5767 Moved this logic to SQL  
		for (ComplianceReportMonitor monitor : report.getMonitors()) {
			monitor.setCurrentSerialNumber(null);
			monitor.setCurrentQAQCAcceptedDate(null);
			List<ContinuousMonitorEqt> activeEqtList = continuousMonitorDAO()
					.retrieveActiveContinuousMonitorEqtList(
							monitor.getContinuousMonitorId());
			if (activeEqtList != null && activeEqtList.size() == 1) {
				// get the current active
				ContinuousMonitorEqt cme = activeEqtList.get(0);
				monitor.setCurrentSerialNumber(cme.getSerialNumber());
				monitor.setCurrentQAQCAcceptedDate(cme.getQAQCAcceptedDate());
			} else {
				// get the most recent
				List<ContinuousMonitorEqt> allEqtList = continuousMonitorDAO()
						.retrieveContinuousMonitorEqtListNewestFirst(
								monitor.getContinuousMonitorId());
				if (allEqtList != null && allEqtList.size() > 0) {
					ContinuousMonitorEqt cme = allEqtList.get(0);
					monitor.setCurrentSerialNumber(cme.getSerialNumber());
					monitor.setCurrentQAQCAcceptedDate(cme
							.getQAQCAcceptedDate());
				}
			}
		}*/
		
		report.setLimits(complianceReportDAO(schema).retrieveComplianceReportLimitListByReportId(report.getReportId()));
		report.setAssocComplianceStatusEvents(
				permitConditionDAO().retrieveComplianceStatusEventListByReferencedComplianceReport(complianceReportId));
		report.setInspectionsReferencedIn(fullComplianceEvalDAO().retrieveInspectionIdsForComplianceRptId(complianceReportId));
		return report;
	}

	/**
	 * @param complianceReportId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ComplianceReport retrieveComplianceReportOnly(
			int complianceReportId, boolean readOnlySchema) throws DAOException {
		String schema = getSchema(readOnlySchema ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA);
		ComplianceReport report = complianceReportDAO(schema)
				.retrieveComplianceReport(complianceReportId);
		return report;
	}

	/**
	 * @param complianceReportId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public int retrievePerReportCount(String facilityID, String reportStatus,
			Timestamp startDate, Timestamp endDate) throws DAOException {
		// modified to always use readonly Schema on 3/25/2008
		return retrievePerReportCount(facilityID, reportStatus, startDate,
				endDate, true);
	}

	/**
	 * @param complianceReportId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public int retrievePerReportCount(String facilityID, String reportStatus,
			Timestamp startDate, Timestamp endDate, boolean readOnlySchema)
			throws DAOException {
		String schema = getSchema(readOnlySchema ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA);
		return complianceReportDAO(schema).retrievePerReportCount(facilityID,
				reportStatus, startDate, endDate);
	}

	/**
	 * Create a new row in the appropriate Compliance Attachment table.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Attachment createComplianceAttachment(ComplianceReport compReport,
			Attachment attachment, InputStream fileStream,
			ComplianceReportAttachment tsAttachment, InputStream tsInputStream)
			throws DAOException, ValidationException {
		Transaction trans = TransactionFactory.createTransaction();
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);

		try {
			attachment = (Attachment) documentDAO(trans).createDocument(
					attachment);
			complianceDAO.createComplianceAttachment(compReport, attachment);
			DocumentUtil.createDocument(attachment.getPath(), fileStream);
			if (tsAttachment != null) {
				// need to set reportId here because it defaults to 0
				attachment.setReportId(compReport.getReportId());
				createComplianceTradeSecretAttachment(attachment, tsAttachment,
						tsInputStream, trans);
			}
			trans.complete();

		} catch (DAOException e) {
			try {
				// need to delete public document if adding trade secret
				// document fails
				DocumentUtil.removeDocument(attachment.getPath());
			} catch (IOException ioex) {
				logger.error("Exception while attempting to delete document: "
						+ attachment.getPath(), ioex);
			}
			cancelTransaction(trans, e);
		} catch (IOException e) {
			cancelTransaction(trans, new RemoteException(e.getMessage(), e));
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return attachment;
	}

	private Attachment createComplianceTradeSecretAttachment(
			Attachment publicAttachment,
			ComplianceReportAttachment tsAttachment, InputStream fileStream,
			Transaction trans) throws DAOException {
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);

		try {
			tsAttachment = (ComplianceReportAttachment) documentDAO(trans)
					.createDocument(tsAttachment);
			publicAttachment.setTradeSecretDocId(tsAttachment.getDocumentID());
			ComplianceReportAttachment pAttachment = new ComplianceReportAttachment(
					publicAttachment);
			pAttachment.setTradeSecretDocId(publicAttachment
					.getTradeSecretDocId());
			pAttachment.setTradeSecretJustification(publicAttachment
					.getTradeSecretJustification());
			complianceDAO.createCRTradeSecretAttachment(pAttachment);
			DocumentUtil.createDocument(tsAttachment.getPath(), fileStream);
			publicAttachment.setTradeSecretDocURL(tsAttachment.getDocURL());
			trans.complete();
		} catch (IOException ioe) {
			try {
				DocumentUtil.removeDocument(tsAttachment.getPath());
			} catch (IOException ioex) {
				logger.error("Exception while attempting to delete document: "
						+ tsAttachment.getPath(), ioex);
			}
			throw new DAOException(ioe.getMessage(), ioe);
		}

		return publicAttachment;
	}

	/**
	 * Create a new row in the appropriate Compliance Attachment table. ASSUMES
	 * the actual attachment is already in file system.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ComplianceReportAttachment createComplianceAttachmentFromPortal(
			ComplianceReport compReport, ComplianceReportAttachment attachment,
			Transaction trans) throws DAOException, ValidationException {
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
		try {
			attachment = (ComplianceReportAttachment) documentDAO(trans)
					.createDocument(attachment);
			complianceDAO.createComplianceAttachment(compReport, attachment);
			if (attachment.getTradeSecretDocId() != null) {
				ComplianceReportAttachment tsAttachment = new ComplianceReportAttachment();
				tsAttachment.setLastModifiedTS(attachment.getLastModifiedTS());
				tsAttachment.setUploadDate(tsAttachment.getLastModifiedTS());
				tsAttachment.setExtension(DocumentUtil
						.getFileExtension(attachment.getTradeSecretDocURL()));
				tsAttachment.setLastModifiedBy(attachment.getLastModifiedBy());
				tsAttachment.setReportId(attachment.getReportId());
				// need object id to be set to put file in correct directory
				tsAttachment.setObjectId(String.valueOf(attachment
						.getReportId()));
				tsAttachment.setFacilityID(attachment.getFacilityID());
				tsAttachment.setSubPath("ComplianceReports");
				tsAttachment = (ComplianceReportAttachment) documentDAO(trans)
						.createDocument(tsAttachment);
				attachment.setTradeSecretDocId(tsAttachment.getDocumentID());
				complianceDAO.createCRTradeSecretAttachment(attachment);
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
		return attachment;
	}

	/**
	 * Create a new row in the CR_COMPLIANCE_DEVIATION table.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ComplianceDeviation createComplianceDeviation(
			ComplianceDeviation complianceDeviation) throws DAOException,
			ValidationException {
		Transaction trans = TransactionFactory.createTransaction();
		ComplianceDeviation ret = null;

		try {
			ret = createComplianceDeviation(complianceDeviation, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param complianceDeviation
	 * @param trans
	 * @return
	 * @throws DAOException
	 */
	private ComplianceDeviation createComplianceDeviation(
			ComplianceDeviation complianceDeviation, Transaction trans)
			throws DAOException {
		ComplianceDeviation ret = null;
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
		try {
			ComplianceDeviation tempComplianceDeviation = complianceDAO
					.createComplianceDeviation(complianceDeviation);

			if (tempComplianceDeviation != null) {
				ret = tempComplianceDeviation;
			} else {
				String s = "Failed to create Compliance Report Deviation for compliance report "
						+ complianceDeviation.getReportId();
				logger.error(s);
				throw new DAOException(s);
			}

		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
		return ret;
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
	private boolean writeComplianceReportToStream(
			ComplianceReport complianceReport, TmpDocument doc,
			OutputStream os, boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws IOException {
		boolean rtn = false;
		try {
			ComplianceReportPdfGenerator generator = new ComplianceReportPdfGenerator();
			generator.generatePdf(complianceReport, os, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
			rtn = generator.isHasTS();
			// There is no trade secret information in the Compliance Report
			// itself.
			// Only attachments can be designated as trade secret.
			if(rtn)
			PdfGeneratorBase.addTradeSecretWatermarkHorizontal(doc.getPath());
		} catch (DocumentException e) {
			throw new IOException(
					"Exception writing compliance report to stream");
		} catch (ServiceFactoryException see) {
			logger.error("Error getting InfraBO for ComplianceReport BO", see);
		}
		return rtn;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ComplianceDocument getComplianceReportDocument(
			ComplianceReport complianceReport) {
		ComplianceDocument appDoc = new ComplianceDocument();
		// Set the path elements of the temp doc.
		appDoc.setDescription("Submitted report pdf file");
		appDoc.setFacilityID(complianceReport.getFacilityId());
		appDoc.setOverloadFileName("SubmittedReport"
				+ complianceReport.getReportId());
		return appDoc;
	}

	private Integer createWorkflow(ComplianceReport cr, Integer fpId)
			throws DAOException, ServiceFactoryException, RemoteException {
		// create workflow
		ReadWorkFlowService wfBO = ServiceFactory.getInstance()
				.getReadWorkFlowService();
		Integer workflowId = null;
		Timestamp dueDt = null;

		logger.debug("Creating compliance report workflow");
		String ptName = WorkFlowProcess.COMPLIANCE_REPORTS;

		if (ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS.equals(cr
				.getReportType())) {
			ptName = "CEMS/COMS/RATA Reporting";
		}

		if (ComplianceReportTypeDef.COMPLIANCE_TYPE_ONE.equals(cr
				.getReportType())) {
			ptName = "One Time Reports";
		}

		if (ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER.equals(cr
				.getReportType())) {
			ptName = "Other Compliance Reports";
		}
		
		if(ptName.equalsIgnoreCase(WorkFlowProcess.COMPLIANCE_REPORTS)) {
			ptName="Generic Compliance Report";
		}

		/*
		 * // Mantis 2644 else if
		 * (ComplianceReportTypeDef.COMPLIANCE_TYPE_SMBR.equals
		 * (cr.getReportType())) { ptName = "Scheduled Maint. Bypass Request";
		 * // set due date to 5 days after start date Calendar dueDateCal =
		 * Calendar.getInstance(); dueDateCal.setTime(cr.getSubmittedDate());
		 * dueDateCal.add(Calendar.DAY_OF_MONTH, 5); dueDt = new
		 * Timestamp(dueDateCal.getTimeInMillis()); }
		 */
		workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
		if (workflowId == null) {
			String s = "Failed to find workflow for \"" + ptName
					+ "\" for Compliance Report " + cr.getReportId();
			logger.error(s);
			throw new DAOException(s);
		}

		String rush = "N";

		if (fpId == null) {
			FacilityDAO fd = facilityDAO();
			Facility fp = fd.retrieveFacility(cr.getFacilityId());
			fpId = fp.getFpId();
		}

		logger.debug("creating Compliance Report workflow with workflowId: "
				+ workflowId
				+ ", ReportID "
				+ cr.getReportId()
				+ ", FPid: "
				+ fpId
				+ ", facility Id: "
				+ cr.getFacilityId()
				+ ", UID: "
				+ cr.getUserId() + ", Due Date: " + dueDt);
		WorkFlowManager wfm = new WorkFlowManager();
		WorkFlowResponse resp = wfm.submitProcess(workflowId, cr.getReportId(),
				fpId, new Integer(cr.getUserId()), rush, new Timestamp(cr
						.getSubmittedDate().getTime()), dueDt, null);

		if (resp.hasError() || resp.hasFailed()) {
			StringBuffer errMsg = new StringBuffer();
			StringBuffer recomMsg = new StringBuffer();
			String[] errorMsgs = resp.getErrorMessages();
			String[] recomMsgs = resp.getRecommendationMessages();
			for (String msg : errorMsgs) {
				errMsg.append(msg + " ");
			}
			for (String msg : recomMsgs) {
				recomMsg.append(msg + " ");
			}
			String s = "Error encountered trying to create workflow for compliance report "
					+ cr.getReportId() + ": " + errMsg.toString();
			logger.error(s);
			throw new DAOException(s + " " + recomMsg);
		}
		return workflowId;
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
	public Document generateTempComplianceReportAttachmentZipFile(
			ComplianceReport complianceReport, ComplianceDocument submittedRpt,
			Document facilityDoc, TmpDocument rpt, boolean hideTradeSecret, boolean readOnly)
			throws FileNotFoundException, IOException {

		TmpDocument tmpDoc;
		tmpDoc = new TmpDocument();
		tmpDoc.setDescription("Compliance Report ZIP file");
		tmpDoc.setFacilityID(complianceReport.getFacilityId());
		tmpDoc.setTemporary(true);
		if(hideTradeSecret) {
			tmpDoc.setTmpFileName(complianceReport.getReportCRPTId() + ".zip");
		} else {
			tmpDoc.setTmpFileName(complianceReport.getReportCRPTId() + "_TS" + ".zip");
		}
		tmpDoc.setContentType(Document.CONTENT_TYPE_ZIP);
		DocumentUtil.mkDirs(tmpDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(tmpDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		boolean hasFiles = zipAttachments(complianceReport, submittedRpt, facilityDoc, rpt,
				hideTradeSecret, zos, readOnly);
		if (!hasFiles)
			return null;
		else {
			zos.close();
			os.close();
			return tmpDoc;
		}
	}

	private boolean zipAttachments(ComplianceReport complianceReport,
			ComplianceDocument submittedRpt, Document facilityDoc, TmpDocument rpt,
			boolean hideTradeSecret, ZipOutputStream zos, boolean readOnly)
			throws FileNotFoundException, IOException {
		DocumentService docBO = null;
		int attachCnt = 0;
		try {
			docBO = ServiceFactory.getInstance().getDocumentService();
		} catch (ServiceFactoryException e) {
			logger.error("Exception accessing DocumentService", e);
			throw new IOException("Exception accessing DocumentService");
		}

		// add attachments to zip file
		HashSet<Integer> docIdSet = new HashSet<Integer>();
		ComplianceReportAttachment[] a = complianceReport.getAttachments();

		if (submittedRpt != null) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(submittedRpt.getPath());
				attachCnt++;
			} catch (FileNotFoundException e) {
				throw new RemoteException("submitted compliance report " +
						complianceReport.getReportId() + " pdf not found", e);
			}
			addEntryToZip(submittedRpt.getOverloadFileName(), docIS, zos);
			docIS.close();
		}

		if (rpt != null) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(rpt.getPath());
				attachCnt++;
			} catch (FileNotFoundException e) {
				throw new RemoteException("compliance report "
						+ complianceReport.getReportId() + " pdf not found", e);
			}
			addEntryToZip(rpt.getTmpFileName(), docIS, zos);
			docIS.close();
		}
		
		// generate file with facility data
		if(null != facilityDoc) {
			InputStream facIS = DocumentUtil.getDocumentAsStream(facilityDoc
					.getPath());
			attachCnt++;
			if (facIS != null) {
				String facName = complianceReport.getFacilityId() + ".pdf";
				addEntryToZip(facName, facIS, zos);
				facIS.close();
			} else {
				logger.error("Error generating facility inventory file for "
						+ complianceReport.getReportCRPTId());
			}
		}

		for (int i = 0; i < a.length; i++) {
			// skip attachments that were added by DAPC
			// don't include documents added after compliance report was submitted
			if (DefData.isDapcAttachmentOnly(a[i].getAttachmentType()) || (complianceReport.getSubmittedDate() != null
					&& a[i].getLastModifiedTS() != null
					&& a[i].getLastModifiedTS().after(
							complianceReport.getSubmittedDate()))) {
				logger.debug("Excluding document "
						+ a[i].getDocumentID()
						+ " from compliance report zip file. Document last modified date ("
						+ a[i].getLastModifiedTS()
						+ ") is after compliance report submission date ("
						+ complianceReport.getSubmittedDate() + ")");
				continue;
			}
			
			if (a[i].getDocumentID() != null
					&& !docIdSet.contains(a[i].getDocumentID())) {
				docIdSet.add(a[i].getDocumentID());
				Document doc = docBO.getDocumentByID(a[i].getDocumentID(),
						readOnly);
				if (doc != null && !doc.isTemporary()) {
					InputStream docIS = null;
					try {
						docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
						attachCnt++;
					} catch (FileNotFoundException e) {
						throw new FileNotFoundException(a[i].getDescription());
					}
					addEntryToZip(getNameForAttachment(a[i], "", false), docIS, zos);
					docIS.close();
				} else if (doc == null) {
					logger.error("No document found with id "
							+ a[i].getDocumentID() + " for compliance report "
							+ complianceReport.getReportId());
				}
			}
			if (!hideTradeSecret) {
				if (a[i].getTradeSecretDocId() != null
						&& !docIdSet.contains(a[i].getTradeSecretDocId()) && (complianceReport.getSubmittedDate() != null || a[i].isTradeSecretAllowed())) {
					docIdSet.add(a[i].getTradeSecretDocId());
					Document doc = docBO.getDocumentByID(
							a[i].getTradeSecretDocId(), readOnly);
					if (doc != null && !doc.isTemporary()) {
						InputStream docIS = null;
						try {
							docIS = DocumentUtil.getDocumentAsStream(doc
									.getPath());
							attachCnt++;
						} catch (FileNotFoundException e) {
							throw new FileNotFoundException(
									a[i].getDescription());
						}
						addEntryToZip(getNameForAttachment(a[i], "_TS", true), docIS,
								zos);
						docIS.close();
					} else if (doc == null) {
						logger.error("No document found with id "
								+ a[i].getDocumentID()
								+ " for compliance report "
								+ complianceReport.getReportId());
					}
				}
			}
		}
		return attachCnt > 0;
	}

	private String getNameForAttachment(ComplianceReportAttachment doc,
			String suffix, boolean tradeSecret) {
		StringBuffer docName = new StringBuffer();
		// add description here?
		docName.append(doc.getDocumentID());
		docName.append(suffix);
		if (!tradeSecret) {
			if (doc.getExtension() != null && doc.getExtension().length() > 0) {
				docName.append("." + doc.getExtension());
			}
		} else {
			if (doc.getTradeSecretDocURL() != null) {
				int pos = doc.getTradeSecretDocURL().lastIndexOf(".");
				if (pos >= 0) {
					docName.append("." + doc.getTradeSecretDocURL().substring(pos + 1));
				}
			}
		}
		return docName.toString();
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
	public List<Document> getPrintableDocumentList(
			ComplianceReport complianceReport, Document facilityDoc, List<Document> rptAttachments,
			boolean readOnly, Boolean hideTradeSecret) throws RemoteException {
		// if hideTradeSecret is null; that means this is requested during
		// submit and return trade secret version if exists otherwise public
		// version.
		List<Document> docList = new ArrayList<Document>();
		ComplianceDocument submittedRpt = null;
		TmpDocument rpt = new TmpDocument();
		DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));

		// use previously generated PDF file if application has been submitted
		if (!isPublicApp()) {
			if (complianceReport.getSubmittedDate() != null) {
				// get submitted pdf document
				submittedRpt = new ComplianceDocument();
				getComplianceReportPDFDocument(complianceReport, submittedRpt, hideTradeSecret);
				try {
					if (submittedRpt != null && DocumentUtil.canRead(submittedRpt.getPath())) {
						// use existing doc if already generated
						// DON'T MAKE THIS AVAILABLE: docList.add(submittedRpt);
						docList.add(submittedRpt);
					} else if (submittedRpt != null && !DocumentUtil.canRead(submittedRpt.getPath())) {
						// generate new doc if it does not already exist
						String userName = null;
						SubmissionLog log = getSubmissionLogForComplianceReport(complianceReport);
						if (log != null) {
							userName = log.getGatewayUserName();
						}
						createComplianceReportPDFDocument(complianceReport, submittedRpt, userName, hideTradeSecret,
								true, false);
						if (submittedRpt != null && DocumentUtil.canRead(submittedRpt.getPath())) {
							// DON'T MAKE THIS AVAILABLE:
							// docList.add(submittedRpt);
							docList.add(submittedRpt);
						}
					}
				} catch (IOException e) {
					logger.error("Exception checking on submitted PDF document for compliance report "
							+ complianceReport.getReportId(), e);
				}
			}
		}

		// get version currently being edited
		boolean hTS;
		if (isPublicApp()) {
			hTS = true;
		} else if (hideTradeSecret != null) {
			hTS = hideTradeSecret;
		} else {
			hTS = false;
		}
		
		generateTempComplianceReportPDF(complianceReport, rpt,
				hTS, false, false);
		
		docList.add(rpt);
		
		if(null != facilityDoc && !isPublicApp()) {
			docList.add(facilityDoc);
		}
		
		try {
			if (complianceReport.getAttachments().length > 0) {
				Document zipDoc = null;
				if (complianceReport.getSubmittedDate() != null && !isPublicApp()) {
					zipDoc = generateTempComplianceReportAttachmentZipFile(
							complianceReport, submittedRpt, facilityDoc, null, hTS, readOnly);
				} else {
					zipDoc = generateTempComplianceReportAttachmentZipFile(
							complianceReport, null, facilityDoc, rpt, hTS, readOnly);
				}
				if (zipDoc != null) {
					docList.add(zipDoc);
				}
			}
		} catch (IOException e) {
			logger.error(
					"Exception generating zip document for complianc report "
							+ complianceReport.getReportId(), e);
		}

		if (!isPublicApp()) {
			for (ComplianceReportAttachment attachment : complianceReport.getAttachments()) {
				// skip attachments that were added by DAPC
				// don't include documents added after compliance report was
				// submitted
				if (((CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)
						|| CompMgr.getAppName().equals(CommonConst.PUBLIC_APP))
						&& DefData.isDapcAttachmentOnly(attachment.getAttachmentType()))
						|| (complianceReport.getSubmittedDate() != null && attachment.getLastModifiedTS() != null
								&& attachment.getLastModifiedTS().after(complianceReport.getSubmittedDate()))) {
					logger.debug("Excluding document " + attachment.getDocumentID()
							+ " from compliance report printable attachment list. Document last modified date ("
							+ attachment.getLastModifiedTS() + ") is after compliance report submission date ("
							+ complianceReport.getSubmittedDate() + ")");
					continue;
				}

				Document doc = documentDAO.retrieveDocument(attachment.getDocumentID());
				if (!doc.isTemporary()) {
					rptAttachments.add(doc);
				}

				if (!hTS && attachment.getTradeSecretDocId() != null
						&& (complianceReport.getSubmittedDate() != null || attachment.isTradeSecretAllowed())) {
					doc = documentDAO.retrieveDocument(attachment.getTradeSecretDocId());
					if (doc != null) {
						// document description may not be in synch with
						// compliance
						// report document
						doc.setDescription(attachment.getDescription() + " - Trade Secret version");
						if (!doc.isTemporary()) {
							rptAttachments.add(doc);
						}
					}
				}
			}
		}

		return docList;
	}

	private SubmissionLog getSubmissionLogForComplianceReport(
			ComplianceReport complianceReport) {
		SubmissionLog log = null;
		SubmissionLog searchSubmissionLog = new SubmissionLog();
		Task t = new Task();
		HashMap<TaskType, String> taskTypeDescs = t.getTaskTypeDescs();
		searchSubmissionLog.setFacilityId(complianceReport.getFacilityId());
		searchSubmissionLog.setSubmissionType(taskTypeDescs
				.get(TaskType.CR_OTHR));
		/*
		 * if
		 * (ComplianceReportTypeDef.COMPLIANCE_TYPE_TVCC.equals(complianceReport
		 * .getReportType())) {
		 * searchSubmissionLog.setSubmissionType(taskTypeDescs
		 * .get(TaskType.CR_TVCC)); } else if
		 * (ComplianceReportTypeDef.COMPLIANCE_TYPE_PER
		 * .equals(complianceReport.getReportType())) {
		 * searchSubmissionLog.setSubmissionType
		 * (taskTypeDescs.get(TaskType.CR_PER)); }
		 */
		try {
			int count = 0;
			for (SubmissionLog tmp : facilityDAO().searchSubmissionLog(
					searchSubmissionLog, complianceReport.getSubmittedDate(),
					complianceReport.getSubmittedDate())) {
				log = tmp;
				count++;
			}
			if (count > 0) {
				logger.error("Multiple submissions found for compliance report: "
						+ complianceReport.getReportId()
						+ ". Setting user id to " + log.getGatewayUserName());
			} else if (count == 0) {
				logger.error("No submissions found for compliance report: "
						+ complianceReport.getReportId());
			}
		} catch (DAOException e) {
			logger.error("Exception retrieving compliance report "
					+ complianceReport.getReportId() + " from submission log",
					e);
		}
		return log;
	}

	public void getComplianceReportPDFDocument(
			ComplianceReport complianceReport, ComplianceDocument doc, boolean hideTradeSecret) {
		// Set the path elements of the temp doc.
		String submittedDocDesc = "Printable View of What Will Be Submitted from Data Entered";
		if (complianceReport.getSubmittedDate() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			submittedDocDesc = "Printable View of Compliance Report Data Submitted on "
					+ dateFormat.format(complianceReport.getSubmittedDate());
		}
		// Set the path elements of the temp doc.
		doc.setDescription(submittedDocDesc
				+ (hideTradeSecret ? "" : " with trade secret data"));
		doc.setFacilityID(complianceReport.getFacilityId());
		doc.setTemporary(true);
		doc.setReportId(complianceReport.getReportId());
		doc.setOverloadFileName("ComplianceReport"
				+ complianceReport.getReportId() 
				+ (hideTradeSecret ? "" : "_TS") + ".pdf"); // Dennis just removed added facility id on front
		return;
	}

	/**
	 * Generate a pdf file containing data from the compliance report and create
	 * a temporary Document object refrencing this file.
	 * 
	 * @param complianceReport
	 *            the compliance report to be rendered in a PDF file.
	 * @return void.
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	private boolean generateTempComplianceReportPDF(
			ComplianceReport complianceReport, TmpDocument doc,
			boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		boolean rtn = false;
		try {
			// Set the path elements of the temp doc.
			String reportType = "Generic Compliance Report";

			if (complianceReport.getReportType().equalsIgnoreCase(
					ComplianceReportTypeDef.COMPLIANCE_TYPE_ONE)) {
				reportType = "One Time Reports";
			} else if (complianceReport.getReportType().equalsIgnoreCase(
					ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS)) {
				reportType = "CEMS/COMS/RATA Reporting";
			} else if (complianceReport.getReportType().equalsIgnoreCase(
					ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER)) {
				reportType = "Other Compliance Reports";
			}	

			doc.setDescription(reportType);
			doc.setFacilityID(complianceReport.getFacilityId());
			doc.setTemporary(true);
			doc.setTmpFileName("complianceReport_"
					+ complianceReport.getReportId()
					+ (hideTradeSecret ? "" : "_TS") + ".pdf");

			// make sure temporary directory exists
			DocumentUtil.mkDirs(doc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(doc.getPath());
			rtn = writeComplianceReportToStream(complianceReport, doc, os,
					hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
			os.close();

		} catch (Exception ex) {
			String s = "Cannot generate pdf for compliance report "
					+ complianceReport.getReportId();
			logger.error(s, ex);
			throw new DAOException(s, ex);
		}
		return rtn;
	}

	/**
	 * Return pdf version of application as an InputStream.
	 * 
	 * @param app
	 * @param hideTradeSecret
	 * @return void
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	private boolean createComplianceReportPDFDocument(
			ComplianceReport complianceReport, ComplianceDocument rpt,
			String userName, boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		boolean rtn = false;
		try {
			getComplianceReportPDFDocument(complianceReport, rpt, hideTradeSecret);
			OutputStream os = DocumentUtil.createDocumentStream(rpt.getPath());
			try {
				ComplianceReportPdfGenerator generator = new ComplianceReportPdfGenerator();
				generator.setUserName(userName);
				generator.setAttestationAttached(userName != null
						&& reportHasAttestationDocument(complianceReport));
				generator.generatePdf(complianceReport, os, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
				rtn = generator.isHasTS();
				// There is no trade secret information in the Compliance Report
				// itself.
				// Only attachments can be designated as trade secret.
				if(rtn)
				PdfGeneratorBase.addTradeSecretWatermarkHorizontal(rpt.getPath());
			} catch (DocumentException e) {
				throw new IOException(
						"Exception writing compliance report to stream");
			} catch (ServiceFactoryException e) {
				throw new DAOException("Exception creating PDF Generator", e);
			} finally {
				if (os != null) {
					os.close();
				}
			}
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting application report as stream", e);
		}
		return rtn;
	}

	/**
	 * Create a zip file containing application pdf files.
	 * 
	 * @param app
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document generateTempComplianceReportPDFZipFile(
			ComplianceReport complianceReport) throws FileNotFoundException,
			IOException {

		TmpDocument appDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		appDoc.setDescription("Compliance report (pdf) zip file");
		appDoc.setFacilityID(complianceReport.getFacilityId());
		appDoc.setTemporary(true);
		appDoc.setTmpFileName(complianceReport.getReportId() + "PDFReport.zip");

		// make sure temporary directory exists
		DocumentUtil.mkDirs(appDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(appDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		zipComplianceReportPDFReportFiles(complianceReport, zos);
		zos.close();
		os.close();

		return appDoc;
	}

	private void zipComplianceReportPDFReportFiles(
			ComplianceReport complianceReport, ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		ComplianceDocument nonTSDoc = new ComplianceDocument();
		ComplianceDocument tsDoc = new ComplianceDocument();
		List<ComplianceDocument> attachmentList = new ArrayList<ComplianceDocument>();
		getComplianceReportPDFDocument(complianceReport, nonTSDoc, true);
		getComplianceReportPDFDocument(complianceReport, tsDoc, false);
		if (nonTSDoc != null && DocumentUtil.canRead(nonTSDoc.getPath())) {
			attachmentList.add(nonTSDoc);
		}
		if (tsDoc != null && DocumentUtil.canRead(tsDoc.getPath())) {
			attachmentList.add(tsDoc);
		}

		// add attachments to zip file
		for (ComplianceDocument attachment : attachmentList) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(attachment.getPath());
			} catch (FileNotFoundException e) {
				String errorMsg = attachment.getDescription();
				if (errorMsg.length() > 50) {
					errorMsg = errorMsg.substring(0, 47) + "...";
				}
				throw new FileNotFoundException(errorMsg);
			}
			addEntryToZip(attachment.getDescription(), docIS, zos);

			if (docIS != null) {
				docIS.close();
			}
		}
	}

	/**
	 * Add attestation document to compliance report
	 * 
	 * @param complianceReport
	 * @param attestationDoc
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void addAttestationDocument(ComplianceReport complianceReport,
			Document attestationDoc, Transaction trans) throws DAOException {
		ComplianceReportAttachment doc = (ComplianceReportAttachment) attestationDoc;
		doc.setDocTypeCd(RO_SIGNATURE_DOC_TYPE);
		ComplianceReportDAO complianceDAO = complianceReportDAO(trans);
		complianceDAO.createComplianceAttachment(complianceReport, doc);

		// add attestation document to list of documents in compliance report.
		ComplianceReportAttachment[] attachmentList = new ComplianceReportAttachment[complianceReport
				.getAttachments().length + 1];
		int i = 0;
		for (i = 0; i < complianceReport.getAttachments().length; i++) {
			attachmentList[i] = complianceReport.getAttachments()[i];
		}
		attachmentList[i] = doc;
		complianceReport.setAttachments(attachmentList);
	}

	/**
	 * 
	 * @param complianceReport
	 * @param trans
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeAttestationDocument(ComplianceReport complianceReport,
			Transaction trans) throws DAOException {
		for (ComplianceReportAttachment doc : complianceReport.getAttachments()) {
			if (RO_SIGNATURE_DOC_TYPE.equals(equals(doc.getDocTypeCd()))) {
				deleteComplianceAttachment(complianceReport, doc, trans, isPortalApp());
			}
		}
	}

	private boolean reportHasAttestationDocument(
			ComplianceReport complianceReport) {
		boolean hasAttestationDocument = false;
		for (ComplianceReportAttachment doc : complianceReport.getAttachments()) {
			if (RO_SIGNATURE_DOC_TYPE.equals(doc.getDocTypeCd())) {
				hasAttestationDocument = true;
				break;
			}
		}
		return hasAttestationDocument;
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
		ComplianceReport complianceReport = task.getComplianceReport();

		// don't want duplicate attachments
		task.getAttachments().clear();

		// add zip file with application attachments to task for submission
		if (complianceReport.hasNonXAttachments()) {
			logger.debug("zipping compliance report attachments...");
			Document zipDoc = generateTempComplianceReportAttachmentZipFile(
					complianceReport, null, null, null, false, false);
			us.oh.state.epa.portal.datasubmit.Attachment submitAttachment = new us.oh.state.epa.portal.datasubmit.Attachment(
					Integer.toString(complianceReport.getReportId()),
					"text/zip", DocumentUtil.getFileStoreRootPath()
							+ zipDoc.getPath(), null, null);
			submitAttachment.setSystemFilename(DocumentUtil
					.getFileStoreRootPath() + zipDoc.getPath());
			task.getAttachments().add(submitAttachment);
			logger.debug("Done zipping compliance report attachments.");
		}

		// generate PDF files related to this submission...generate pdf files
		// for public and trade secret versions of document

		// Version without Trade Secret attachments listed.
		ComplianceDocument rpt1 = new ComplianceDocument();
		createComplianceReportPDFDocument(complianceReport, rpt1,
				task.getUserName(), true, false, true);

		// Trade secret version.
		ComplianceDocument rpt2 = new ComplianceDocument();
		createComplianceReportPDFDocument(complianceReport, rpt2,
				task.getUserName(), false, false, true);
		
		
		Document zipDoc = generateTempComplianceReportPDFZipFile(complianceReport);
		if (zipDoc != null) {
			// logger.error("Debug #2966: generateTempComplianceReportPDFZipFile: attachment description"
			// + zipDoc.getDescription() +
			// ", basePath " + zipDoc.getBasePath());
			us.oh.state.epa.portal.datasubmit.Attachment submitAttachment = new us.oh.state.epa.portal.datasubmit.Attachment(
					Integer.toString(complianceReport.getReportId()),
					"text/zip", DocumentUtil.getFileStoreRootPath()
							+ zipDoc.getPath(), null, null);
			submitAttachment.setSystemFilename(DocumentUtil
					.getFileStoreRootPath() + zipDoc.getPath());
			task.getAttachments().add(submitAttachment);
		} // else {
		// logger.error("Debug #2966: generateTempComplianceReportPDFZipFile is null");
		// }
	}

	/**
	 * Return pdf file RO can sign as attestation document.
	 * 
	 * @param cr
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ComplianceReportAttachment createCRAttestationDocument(
			ComplianceReport cr) throws DAOException {
		ComplianceReportAttachment doc = null;
		try {
			doc = getCRAttestationDocument(cr);
			OutputStream os = DocumentUtil.createDocumentStream(doc.getPath());
			try {
				ComplianceReportPdfGenerator generator = new ComplianceReportPdfGenerator();
				generator.setAttestationOnly(true);
				// For now, set hideTradeSecret to true, until portal
				// requirements are established.
				generator.generatePdf(cr, os, true, false, true);
			} catch (DocumentException e) {
				logger.error(
						"createCRAttestationDocument failed for Compliance Report "
								+ cr.getReportId() + ". " + e.getMessage(), e);
				throw new IOException(
						"Exception writing attestation document to stream");
			} catch (ServiceFactoryException e) {
				logger.error(
						"createCRAttestationDocument failed for Compliance Report "
								+ cr.getReportId() + ". " + e.getMessage(), e);
				throw new DAOException("ServiceFactoryException", e);
			}
			os.close();
		} catch (IOException e) {
			logger.error(
					"createCRAttestationDocument failed for Compliance Report "
							+ cr.getReportId() + ". " + e.getMessage(), e);
			throw new DAOException(
					"Exception getting attestation document as stream", e);
		} catch (Exception e) {
			logger.error(
					"createCRAttestationDocument failed for Compliance Report "
							+ cr.getReportId() + ". " + e.getMessage(), e);
			throw new DAOException(
					"Exception getting attestation document as stream", e);
		}
		return doc;
	}

	private ComplianceReportAttachment getCRAttestationDocument(
			ComplianceReport cr) {
		ComplianceReportAttachment doc = new ComplianceReportAttachment();
		doc.setFacilityID(cr.getFacilityId());
		doc.setSubPath("ComplianceReports");
		doc.setExtension("pdf");
		doc.setObjectId(cr.getReportId().toString());
		doc.setDocumentID(cr.getReportId()); // just a temporary id until the
												// file is uploaded
		doc.setTemporary(true);
		doc.setDescription("Attestation document for compliance report "
				+ cr.getReportId());
		return doc;
	}

	/**
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String getDAPCAttestationMessage(ComplianceReport report,
			String permitClassCd) throws RemoteException {
		String complianceReportMsg = SystemPropertyDef.getSystemPropertyValue("ComplianceReportMessage", null);
		if (complianceReportMsg == null) {
			complianceReportMsg = "";
		}

		return complianceReportMsg;
	}

	private Timestamp getPerEffectiveDate(String facilityId)
			throws RemoteException {
		Timestamp effectiveDate = null;
		PermitService permitBO = null;
		try {
			permitBO = ServiceFactory.getInstance().getPermitService();
			List<Permit> permits = permitBO.searchPERs(facilityId, null, null);

			for (int i = 0; i < permits.size(); i++) {
				Permit p = permits.get(i);
				// use the earliest effective date from PTIOs issued for this
				// facility
				if (p.getEffectiveDate() != null
						&& (effectiveDate == null || effectiveDate.after(p
								.getEffectiveDate()))) {
					effectiveDate = p.getEffectiveDate();
				}
			}
		} catch (ServiceFactoryException e) {
			throw new RemoteException(e.getMessage(), e);
		}
		return effectiveDate;
	}

	/**
	 * @throws RemoteException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String retrievePerReportingPeriods(String facilityId,
			List<PerReportingPeriod> perList) throws RemoteException {
		String lastPeriodCd = null;
		// construct the def list from the perduedates
		// List<PERDueDate> perDueDates = null;
		// perList.clear(); // make sure list is empty
		//
		// try {
		// FacilityService facilityBO =
		// ServiceFactory.getInstance().getFacilityService();
		// perDueDates = facilityBO.retrievePERDueDates(facilityId);
		//
		// if (perDueDates.size()==0) {
		// return null;
		// }
		//
		// // need effective due date to determine the applicable year(s) for
		// the periods
		// Timestamp effectiveDate = getPerEffectiveDate(facilityId);
		// if (effectiveDate == null) {
		// return null;
		// }
		//
		// // keep track of most recent PER effective date if multiple PER dates
		// // apply to this facility
		// Timestamp latestPerEffectiveDate = null;
		// for (PERDueDate pdd : perDueDates) {
		// if (!pdd.getPERDueDateCd().equals("0")) { // "0" is None
		// DefSelectItems dStartPeriods =
		// PTIOPERDueDateDef.getStartOfPeriod().getItems();
		// DefSelectItems dEndPeriods =
		// PTIOPERDueDateDef.getEndOfPeriod().getItems();
		//
		// String code = pdd.getPERDueDateCd();
		//
		// int iStartMonth =
		// Integer.parseInt(dStartPeriods.getItemDesc(code).substring(0, 2))-1;
		// int iStartDay =
		// Integer.parseInt(dStartPeriods.getItemDesc(code).substring(2));
		//
		// int iEndMonth =
		// Integer.parseInt(dEndPeriods.getItemDesc(code).substring(0, 2))-1;
		// int iEndDay =
		// Integer.parseInt(dEndPeriods.getItemDesc(code).substring(2));
		//
		// Calendar nowCal = Calendar.getInstance();
		// int endYear = nowCal.get(Calendar.YEAR);
		//
		// boolean switchDates = false;
		// Calendar startDateCal = Calendar.getInstance();
		// startDateCal.set(endYear, iStartMonth, iStartDay, 12, 0, 0);
		//
		// // increment end year of latest interval if current date is
		// // after beginning of interval
		// if (nowCal.after(startDateCal)) {
		// endYear++;
		// Calendar midPointCal = (Calendar)startDateCal.clone();
		// midPointCal.add(Calendar.MONTH, 6);
		// switchDates = nowCal.before(midPointCal);
		// }
		//
		// // extract year from the effective date
		// Calendar perEffectiveCal = Calendar.getInstance();
		// perEffectiveCal.setTime(pdd.getEffectiveDate()); // effectiveDate);
		// int perEffectiveYear = perEffectiveCal.get(Calendar.YEAR);
		// if (latestPerEffectiveDate == null) {
		// latestPerEffectiveDate = pdd.getEffectiveDate();
		// }
		//
		// // start year is typically the year prior to the effective date
		// int startYear = perEffectiveYear - 1;
		//
		// // move forward one year if due date is for following year
		// int endDateYearIncrement = 1;
		// if (iEndMonth >= 9) {
		// startYear++;
		// endDateYearIncrement = 0;
		// }
		//
		// // use the end date of the period as a reference
		// // if the effective date is after the period end date,
		// // the report should be run for the following year
		// Calendar referenceCal = Calendar.getInstance();
		// referenceCal.set(perEffectiveYear, iEndMonth, iEndDay, 12, 0, 0);
		// if (perEffectiveCal.after(referenceCal)) {
		// startYear++;
		// }
		//
		// boolean perOverridden = false;
		// if (pdd.getEffectiveDate().before(latestPerEffectiveDate)) {
		// // adjust endYear if this PER period has been replaced by another
		// Calendar tmpCal = Calendar.getInstance();
		// tmpCal.setTime(latestPerEffectiveDate);
		// endYear = tmpCal.get(Calendar.YEAR);
		// // reset most recent PER date in case there are more PERs
		// latestPerEffectiveDate = pdd.getEffectiveDate();
		// perOverridden = true;
		// }
		//
		// for (int year=endYear-1;year>=startYear;year--) {//Mantis #2121,
		// changed the condition to <= from <
		// Calendar cStartDt = Calendar.getInstance();
		// cStartDt.set(year, iStartMonth,iStartDay,12,0,0);
		// Timestamp startDt = new Timestamp(cStartDt.getTimeInMillis());
		//
		// Calendar cDueDt = Calendar.getInstance();
		// cDueDt.set(year+1, iEndMonth,iEndDay,12,0,0);//ii+1
		// Timestamp dueDt = new Timestamp(cDueDt.getTimeInMillis());
		//
		// Calendar cEndDt = Calendar.getInstance();
		// cEndDt.set(year+endDateYearIncrement,
		// iEndMonth,iEndDay,12,0,0);//ii+1
		// Timestamp endDt = new Timestamp(cEndDt.getTimeInMillis());
		//
		// PerReportingPeriod prp = new
		// PerReportingPeriod(facilityId,startDt,endDt,code, dueDt);
		// // select first item in the list
		// if (!perOverridden) {
		// if (lastPeriodCd == null) {
		// lastPeriodCd = prp.getFullDueDateCd();
		// } else if (switchDates) {
		// // set date to second item in list if switchDates is true.
		// lastPeriodCd = prp.getFullDueDateCd();
		// switchDates = false;
		// }
		// }
		// perList.add(prp);
		// }
		//
		// } else {
		// logger.warn("Invalid report period - code is " +
		// pdd.getPERDueDateCd());
		// }
		// }
		// } catch (ServiceFactoryException e) {
		// throw new RemoteException(e.getMessage(), e);
		// }
		return lastPeriodCd;
	}

	/**
	 * 
	 * @param facilityId
	 * @param sv
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer afsLockTvCc(String scscId, ComplianceReportList crl)
			throws DAOException {
		Transaction trans = null;
		Integer id = null;
		try {
			trans = TransactionFactory.createTransaction();
			ComplianceReportDAO compDAO = complianceReportDAO(trans);
			StackTestDAO stackTestDAO = stackTestDAO(trans);
			id = stackTestDAO.getAfsId(scscId);
			id++; // Get two adjacent values.
			stackTestDAO.updateDoubleAfsId(scscId, id);
			compDAO.afsLockTvCc(crl, id);
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return id;
	}

	/**
	 * 
	 * @param scscId
	 * @param afsId
	 * @return List<ComplianceReportList>
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public List<ComplianceReportList> retrieveTvCcByAfsId(String scscId,
			String afsId) throws DAOException {
		Transaction trans = null;
		List<ComplianceReportList> rtn = null;
		checkNull(scscId);
		checkNull(afsId);
		try {
			rtn = complianceReportDAO().retrieveTvCcByAfsId(scscId, afsId);
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return rtn;
	}

	/**
	 * @param fce
	 * @return boolean
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean afsSetDateTvCc(ComplianceReportList tvCc)
			throws DAOException {
		Transaction trans = null;
		boolean rtn = false;
		try {
			trans = TransactionFactory.createTransaction();
			rtn = complianceReportDAO(trans).afsSetDateTvCc(tvCc);
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return rtn;
	}

	/**
	 * Returns all of the Compliance Report notes by Report ID.
	 * 
	 * @param int The Report ID
	 * 
	 * @return Note[] All notes of this Compliance Report.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Note[] retrieveNotes(int reportId) throws DAOException {
		return complianceReportDAO().retrieveNotes(reportId);
	}

	public ComplianceReportNote createNote(ComplianceReportNote note)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ComplianceReportNote ret = null;

		try {
			ret = createNote(note, trans);

			if (ret != null) {
				trans.complete();
			} else {
				trans.cancel();
				logger.error("Failed to insert Compliance Report Note");
			}
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param note
	 * @param trans
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private ComplianceReportNote createNote(ComplianceReportNote note,
			Transaction trans) throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ComplianceReportDAO crDAO = complianceReportDAO(trans);

		Note tempNote = infraDAO.createNote(note);
		if (tempNote != null) {
			note.setNoteId(tempNote.getNoteId());
			crDAO.createNote(note.getReportId(), note.getNoteId());
		} else {
			note = null;
		}
		return note;
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
	public boolean modifyNote(ComplianceReportNote note) throws DAOException {
		return infrastructureDAO().modifyNote(note);
	}

	public boolean removeNote(ComplianceReportNote note) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;

		try {
			ret = removeNote(note, trans);
			if (ret) {
				trans.complete();
			} else {
				trans.cancel();
				logger.error("Failed to remove Compliance Report Note");
			}
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param note
	 * @param trans
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private boolean removeNote(ComplianceReportNote note, Transaction trans)
			throws DAOException {
		boolean ret = false;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ComplianceReportDAO crDAO = complianceReportDAO(trans);

		ret = crDAO.removeNote(note.getReportId(), note.getNoteId());
		if (ret) {
			ret = infraDAO.removeNote(note.getNoteId());
		}

		return ret;
	}
	
	
	
	
	/**
	 * Set the "validated" flag for <code>st</code> to <code>validated</code>.
	 * This method exists to avoid a full update of all stack test attributes
	 * when all that is needed is to set the validated flag.
	 * 
	 * @param st
	 *            the stack test
	 * @param validated
	 *            <code>true</code> or <code>false</code> to indicate whether
	 *            the stack test is validated.
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean setValidatedFlag(ComplianceReport report, boolean validated, boolean readOnly)
			throws DAOException {
		Transaction trans = null;
		boolean ret = true;

		try {
			trans = TransactionFactory.createTransaction();
			
			ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
					: CommonConst.STAGING_SCHEMA));
			report.setValidated(validated);
			complianceReportDAO.setComplianceReportValidatedFlag(report.getReportId(),
					validated);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}
	
	public ComplianceReportCategoryInfo[] retrieveComplianceReportCategoryInfo()
			throws DAOException {
		return complianceReportDAO().retrieveComplianceReportCategoryInfo();
	}
	
	// retrieve attachments and any trade secret attachments that may exist
	public ComplianceReportAttachment[] retrieveCrAttachments(
			ComplianceReport report, boolean readOnlySchema)
			throws DAOException {

		ComplianceReportAttachment[] attachments = null;

		String schema = getSchema(readOnlySchema ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA);

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(schema);

		DocumentDAO documentDAO = documentDAO(schema);

		try {
			attachments = complianceReportDAO.retrieveAttachments(
					report.getReportType(), report.getFacilityId(),
					report.getReportId());
			for (ComplianceReportAttachment attachment : attachments) {
				// need to set report id here - it's not set from the DAO
				// method.
				attachment.setReportId(report.getReportId());
				complianceReportDAO
						.retrieveCRTradeSecretAttachmentInfo(attachment);
				// need to retrieve trade secret document to build URL
				if (attachment.getTradeSecretDocId() != null) {
					Document tsDoc = documentDAO.retrieveDocument(attachment
							.getTradeSecretDocId());
					if (tsDoc != null) {
						attachment.setTradeSecretDocURL(tsDoc.getDocURL());
					} else {
						// should never happen
						logger.error("Unable to retrieve Trade Secret Document: "
								+ attachment.getTradeSecretDocId()
								+ " for Compliance Report "
								+ report.getReportId());
					}
				}
			}
		} catch (RemoteException de) {
			logger.error(
					"Report ID =" + report.getReportId() + " "
							+ de.getMessage(), de);
			throw new DAOException(de.getMessage(), de);
		}
		return attachments;
	}
	
	public List<ValidationMessage> validateComplianceReport(int reportId,
			boolean useReadOnlyDB) throws RemoteException,
			ServiceFactoryException {
		// ComplianceReport is valid unless an error is recorded
		boolean crIsValid = true;
		LinkedList<ValidationMessage> retVal = new LinkedList<ValidationMessage>();

		// Retrieve compliance report data from DB
		ComplianceReport complianceReport = retrieveComplianceReport(reportId);
		complianceReport.refreshCompReportMonitorList();

		// Perform compliance report validation
		validateCrRequiredFields(complianceReport, retVal);

		if (!complianceReport.isLegacyFlag()) {

			for (ValidationMessage message : retVal) {
				if (message.getSeverity().equals(
						ValidationMessage.Severity.ERROR)) {
					crIsValid = false;
					break;
				}
			}
		}

		// Save updated compliance report data to DB
		setValidatedFlag(complianceReport, crIsValid, useReadOnlyDB);

		return retVal;
	}

	private void validateCrRequiredFields(ComplianceReport cr,
			List<ValidationMessage> messages) {
		ValidationMessage[] validResults = cr.validate();
		for (ValidationMessage validResult : validResults) {
			messages.add(new ValidationMessage(validResult.getProperty(),
					validResult.getMessage(), validResult.getSeverity(),
					validResult.getReferenceID()));
		}
	}
	
	/** Associates the compliance report with the current version of the
	 * facility inventory and refreshes the monitors and limits in the 
	 * report if necessary.
	 * 
	 * @param  complianceReport
	 * @param facility
	 * @param userId
	 * @throws DAOException
	 */
	@Override
	public void associateRptWithCurrentFacility(
			ComplianceReport complianceReport, Facility facility, Integer userId)
			throws DAOException {
		// set the fpId in the compliance report to the current version 
		// of the facility inventory
		complianceReport.setFpId(facility.getFpId());
		complianceReport.setValidated(false);
		modifyComplianceReport(complianceReport, userId);
		
		// get the updated compliance report
		ComplianceReport cr = retrieveComplianceReport(complianceReport.getReportId());
		cr.refreshCompReportMonitorList();
		logger.debug("Compliance report id " + cr.getReportCRPTId() + " is now associated with fpId " + cr.getFpId());
		
		// sync monitors and limits information in the report with the facility
		syncMonitorsAndLimitstWithFacility(cr, facility);
		
		// add a note to the report to indicate that it is now associated with the 
		// current version of the facility
		ComplianceReportNote note = createFacilityChangedNote(cr);
		createNote(note);
		
	}
	
	private ComplianceReportNote createFacilityChangedNote(
			ComplianceReport complianceReport) {

		ComplianceReportNote note = new ComplianceReportNote();
		String noteTxt = "The facility inventory for this compliance report was updated to the current version on  "
				+ new Timestamp(System.currentTimeMillis()).toString();

		note.setReportId(complianceReport.getReportId());
		note.setNoteTxt(noteTxt);
		note.setDateEntered(new Timestamp(System.currentTimeMillis()));
		note.setNoteTypeCd(NoteType.DAPC);
		note.setUserId(InfrastructureDefs.getCurrentUserId());

		return note;
	}

	// Compliance Report Limits

	public List<ComplianceReportLimit> retrieveComplianceReportLimitListByFpId(
			Integer fpId) throws DAOException {

		List<ComplianceReportLimit> crlList = null;

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		try {

			crlList = complianceReportDAO
					.retrieveComplianceReportLimitListByFpId(fpId);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while retrieving Compliance Report Limits");
			logger.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while retrieving Compliance Report Limits");
			logger.error(e.getMessage());
			throw e;
		} finally {

		}

		return crlList;

	}

	public List<ComplianceReportLimit> retrieveComplianceReportLimitListByMonitorId(
			Integer monitorId) throws DAOException {

		List<ComplianceReportLimit> crlList = null;

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		try {

			crlList = complianceReportDAO
					.retrieveComplianceReportLimitListByMonitorId(monitorId);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while retrieving Compliance Report Limits");
			logger.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while retrieving Compliance Report Limits");
			logger.error(e.getMessage());
			throw e;
		} finally {

		}

		return crlList;

	}

	@Override
	public ComplianceReportLimit createComplianceReportLimit(
			ComplianceReportLimit crl) throws DAOException {

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		return complianceReportDAO.createComplianceReportLimit(crl);
	}

	public ComplianceReportLimit modifyComplianceReportLimit(
			ComplianceReportLimit crl) throws DAOException {

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		complianceReportDAO.modifyComplianceReportLimit(crl);

		return crl;
	}

	@Override
	public final void removeComplianceReportLimit(ComplianceReportLimit crl)
			throws DAOException {

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		complianceReportDAO.removeComplianceReportLimit(crl);

		return;
	}

	@Override
	public ComplianceReportLimit retrieveComplianceReportLimitByMonitorIdAndCorrId(
			Integer monitorId, Integer corrLimitId) throws DAOException {

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());
		return complianceReportDAO
				.retrieveComplianceReportLimitByMonitorIdAndCorrId(monitorId,
						corrLimitId);
	}

	/**
	 * 
	 * @param complianceReportMonitor
	 * @throws DAOException
	 * 
	 */
	@Override
	public ComplianceReportMonitor createComplianceReportMonitor(
			ComplianceReportMonitor complianceReportMonitor)
			throws DAOException, RemoteException, ServiceFactoryException {

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		complianceReportMonitor = complianceReportDAO
				.createComplianceReportMonitor(complianceReportMonitor);

		return complianceReportMonitor;
	}

	/**
	 * 
	 * @param action
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	@Override
	public void deleteComplianceReportMonitor(ComplianceReportMonitor monitor)
			throws DAOException, RemoteException {

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		complianceReportDAO.removeComplianceReportLimitList(monitor
				.getCrMonitorId());

		complianceReportDAO.deleteComplianceReportMonitor(monitor);

		return;
	}

	/**
	 * 
	 * @param complianceReportMonitor
	 * @throws DAOException
	 * 
	 */
	@Override
	public void modifyComplianceReportMonitor(
			ComplianceReportMonitor complianceReportMonitor)
			throws DAOException, RemoteException {
		
		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		complianceReportDAO
				.modifyComplianceReportMonitor(complianceReportMonitor);

		return;
	}

	/**
	 * 
	 * @param complianceReportMonitorId
	 * @throws DAOException
	 * 
	 */
	public ComplianceReportMonitor retrieveComplianceReportMonitor(
			Integer complianceReportMonitorId) throws DAOException {

		ComplianceReportMonitor ret = null;

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		try {
			ret = complianceReportDAO
					.retrieveComplianceReportMonitor(complianceReportMonitorId);
			populateComplianceReportMonitor(ret);
		} catch (DAOException de) {
			DisplayUtil.displayError("Failed to retrieve compliance report monitor");
			logger.error(de.getMessage(), de);
			throw de;
		}
		return ret;
	}

	public ComplianceReportMonitor[] retrieveComplianceReportMonitorListByReportId(
			Integer reportId) throws DAOException {

		ComplianceReportMonitor[] crmList = null;

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		try {

			crmList = complianceReportDAO
					.retrieveComplianceReportMonitorListByReportId(reportId);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while retrieving Compliance Report Monitors");
			logger.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while retrieving Compliance Report Monitors");
			logger.error(e.getMessage());
			throw e;
		} finally {

		}

		return crmList;

	}

	public ComplianceReportLimit[] retrieveComplianceReportLimitListByReportId(
			Integer reportId) throws DAOException {

		ComplianceReportLimit[] crlList = null;

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		try {

			crlList = complianceReportDAO
					.retrieveComplianceReportLimitListByReportId(reportId);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Error while retrieving Compliance Report Limits");
			logger.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			DisplayUtil
					.displayError("Error while retrieving Compliance Report Limits");
			logger.error(e.getMessage());
			throw e;
		} finally {

		}

		return crlList;

	}

	public void populateComplianceReportMonitor(ComplianceReportMonitor cm)
			throws DAOException {
		if (cm == null) {
			return;
		}

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		List<ComplianceReportLimit> limits = complianceReportDAO
				.retrieveComplianceReportLimitListByMonitorId(
						cm.getCrMonitorId());
		cm.setComplianceReportLimitList(limits);

	}

	private void createMonitorLimitEntries(ComplianceReport report)
			throws DAOException {
		Integer fpId = report.getFpId();

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());
		ContinuousMonitorDAO continuousMonitorDAO = continuousMonitorDAO(getSchema());

		// get monitors for the current version of the facility
		ContinuousMonitor[] monitors = continuousMonitorDAO
				.retrieveContinuousMonitorByFpId(fpId);
		if (monitors.length > 0) {
			for (ContinuousMonitor monitor : monitors) {
				addContinuousMonitorToReport(monitor, report);
			}
		}
	}

	private void createMonitorLimitEntriesFromPortal(ComplianceReport report)
			throws DAOException {
		Integer fpId = report.getFpId();

		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());

		List<ComplianceReportMonitor> monitors = report
				.getCompReportMonitorList();

		for (ComplianceReportMonitor cm : monitors) {
			ComplianceReportMonitor crMonitor = new ComplianceReportMonitor(cm);
			crMonitor.setReportId(report.getReportId());
			complianceReportDAO.createComplianceReportMonitor(crMonitor);

			for (ComplianceReportLimit cl : cm.getComplianceReportLimitList()) {
				ComplianceReportLimit crLimit = new ComplianceReportLimit(cl);
				crLimit.setCrMonitorId(crMonitor.getCrMonitorId());
				complianceReportDAO.createComplianceReportLimit(crLimit);
			}
		}
	}

	private void deleteAllCrMonitorsAndLimits(
			ComplianceReport complianceReport)
			throws DAOException {
		ComplianceReportDAO complianceDAO = complianceReportDAO(getSchema());

		try {

			ComplianceReportLimit limits[] = complianceReport.getLimits();
			if (limits != null) {
				for (ComplianceReportLimit cl : limits) {
					complianceDAO.removeComplianceReportLimit(cl);
				}
			}

			ComplianceReportMonitor monitors[] = complianceReport.getMonitors();
			if (monitors != null) {

				for (ComplianceReportMonitor cm : monitors) {
					complianceDAO.deleteComplianceReportMonitor(cm);
				}
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			logger.error("ComplianceReportBO.deleteAllCrMonitorsAndLimits Deletion error for "
					+ complianceReport.getReportId());
			throw e;
		} finally {
		}
	}

	private String getSchema() {
		boolean readOnlySchema = true;
		if (isPortalApp()) {
			readOnlySchema = false;
		}

		String schema = getSchema(readOnlySchema ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA);
		return schema;
	}
	
	/**
	 * For a given compliance report, syncs the monitor and limits with the 
	 * given version of the facility
	 * 
	 * @param complianceReport
	 * @param facility
	 * @throws DAOException
	 */
	private void syncMonitorsAndLimitstWithFacility(
			ComplianceReport complianceReport, Facility facility)
			throws DAOException {
		if (null != complianceReport && null != facility) {
			// list of monitors currently present in the compliance report
			List<ComplianceReportMonitor> complianceReportMonitors = complianceReport
					.getCompReportMonitorList();

			// list of monitors in the facility inventory
			List<ContinuousMonitor> facilityMonitors = facility
					.getContinuousMonitorList();
			
			for (ContinuousMonitor facMonitor : facilityMonitors) {
				boolean found = false;
				for (ComplianceReportMonitor crMonitor : complianceReportMonitors) {
					if (facMonitor.getCorrMonitorId().equals(
							crMonitor.getCorrMonitorId())) {
						found = true;
						
						// sync the limits
						syncLimitsWithFacility(crMonitor, facMonitor, complianceReport);
						
						if (!facMonitor.getContinuousMonitorId().equals(
								crMonitor.getContinuousMonitorId())) {
							// sync the compliance report monitor with the
							// continuous monitor in the facility
							// i.e., update complinace report monitor id in the
							// report with the continuois monitor id from the facility
							logger.debug("Syncing compliance report monitor id "
									+ crMonitor.getCrMonitorId()
									+ " with facility");
							logger.debug("Updating associated continuous monitor id to "
									+ facMonitor.getContinuousMonitorId());
							crMonitor.setContinuousMonitorId(facMonitor
									.getContinuousMonitorId());
							complianceReportDAO()
									.modifyComplianceReportMonitor(crMonitor);
						}
					}
				}
				
				if (!found) {
					// continuous monitor in the facility is not found in the
					// report, which means this must be a new monitor added to
					// facility and hence has to be added to the compliance report
					addContinuousMonitorToReport(facMonitor, complianceReport);
				}
			}
		}
	}
	
	/**
	 * Syncs limits in the given compliance report monitor with the
	 * limits in the given facility continuous monitor.
	 *  
	 * @param crMonitor
	 * @param monitor
	 * @throws DAOException
	 */
	private void syncLimitsWithFacility(ComplianceReportMonitor crMonitor,
			ContinuousMonitor facMonitor, ComplianceReport complianceReport)
			throws DAOException {

		if (null != facMonitor && null != crMonitor) {
			List<FacilityCemComLimit> cemComLimits = facilityDAO()
					.retrieveFacilityCemComLimitListByMonitorId(
							facMonitor.getContinuousMonitorId());

			List<ComplianceReportLimit> crMonitorLimits = crMonitor
					.getComplianceReportLimitList();
			
			for (FacilityCemComLimit cemComLimit : cemComLimits) {
				boolean found = false;
				for (ComplianceReportLimit crMonitorLimit : crMonitorLimits) {
					if (cemComLimit.getCorrLimitId().equals(
							crMonitorLimit.getCorrLimitId())) {
						found = true; // existing limit
						if (!cemComLimit.getLimitId().equals(
								crMonitorLimit.getLimitId())) {
							// sync the compliance report limit with the
							// cem/com limit in the facility
							// i.e., update complinace report limit id in the
							// report with the ccem/com limit id from the facility
							logger.debug("Syncing compliance report limit id "
									+ crMonitorLimit.getCrLimitId()
									+ " with facility");
							logger.debug("Updating associated cem/com limit id to "
									+ cemComLimit.getLimitId());
							crMonitorLimit.setLimitId(cemComLimit.getLimitId());
							complianceReportDAO().modifyComplianceReportLimit(
									crMonitorLimit);
						}
						break;
					}
				}

				if (!found) {
					// new limit...add it to the compliance report monitor
					logger.debug("Creating new compliance report limit for cem/com limit id "
							+ cemComLimit.getLimitId());
					ComplianceReportLimit crl = new ComplianceReportLimit(
							cemComLimit);
					crl.setCrMonitorId(crMonitor.getCrMonitorId());
					if (complianceReport.isSecondGenerationQuarterlyCemComRpt()
							&& null == crl.getEndDate()) {
						crl.setIncludedFlag(true);
					}
					complianceReportDAO().createComplianceReportLimit(crl);
				}
			}
		}
	}	
	
	/**
	 * Adds the given facility continuous monitor and the associated
	 * limits to the compliance report.
	 * 
	 * @param continuousMonitor
	 * @param complianceReport
	 * @return continuousMonitor
	 * @throws DAOException
	 */
	private ContinuousMonitor addContinuousMonitorToReport(
			ContinuousMonitor continuousMonitor,
			ComplianceReport complianceReport) throws DAOException {
		
		ComplianceReportDAO complianceReportDAO = complianceReportDAO(getSchema());
		ContinuousMonitorDAO continuousMonitorDAO = continuousMonitorDAO(getSchema());
		FacilityDAO facilityDAO = facilityDAO(getSchema());
		
		//add the monitor to the report only if there is atleast one equipment
		List<ContinuousMonitorEqt> equipmentList = continuousMonitorDAO.
				retrieveContinuousMonitorEqtList(
				continuousMonitor.getContinuousMonitorId());
		if(equipmentList.isEmpty()) {
			logger.debug("Skipping continuous monitor " + continuousMonitor.getContinuousMonitorId()
					+ " because it does not have any equipment. "
					+ continuousMonitor.getMonId()
					+ " not added to the report.");
			return continuousMonitor;
		}
		
		logger.debug("Creating new compliance report monitor for continuous monitor id "
				+ continuousMonitor.getContinuousMonitorId());
		// first create the compliance report monitor
		ComplianceReportMonitor crMonitor = new ComplianceReportMonitor(
				continuousMonitor);
		crMonitor.setReportId(complianceReport.getReportId());
		ComplianceReportMonitor crm = complianceReportDAO
				.createComplianceReportMonitor(crMonitor);

		// create the compliance report limits associated with the monitor
		List<FacilityCemComLimit> limits = facilityDAO
				.retrieveFacilityCemComLimitListByMonitorId(
						continuousMonitor.getContinuousMonitorId());
		
		for (FacilityCemComLimit limit : limits) {
			logger.debug("Creating new compliance report limit for cem/com limit id "
					+ limit.getLimitId());
			ComplianceReportLimit crLimit = new ComplianceReportLimit(limit);
			crLimit.setCrMonitorId(crm.getCrMonitorId());
			
			if (complianceReport.isSecondGenerationQuarterlyCemComRpt()
					&& null == crLimit.getEndDate()) {
				crLimit.setIncludedFlag(true); 
			}
			
			complianceReportDAO.createComplianceReportLimit(crLimit);
		}

		return crMonitor;
	}
	
	@Override
	public ComplianceReport refreshMonitorsAndLimits(
			ComplianceReport complianceReport, Facility facility)
			throws DAOException {

		ComplianceReport cr;

		syncMonitorsAndLimitstWithFacility(complianceReport, facility);

		cr = retrieveComplianceReport(
				complianceReport.getReportId());
		cr.refreshCompReportMonitorList();

		return cr;
	}
	
	private void syncPortalComplianceReportWithCurrentFacility(
			ComplianceReport complianceReport, Facility facility)
			throws DAOException {

		logger.debug("Syncing compliance repport monitors and limits with the current version of the facility (fpId: "
				+ facility.getFpId() + ")");
		// first, set the last_modified to 1 to avoid data concurrency errors
		// during the sync
		for (ComplianceReportMonitor cm : complianceReport
				.getCompReportMonitorList()) {
			cm.setLastModified(1);

			for (ComplianceReportLimit cl : cm.getComplianceReportLimitList()) {
				cl.setLastModified(1);
			}
		}

		// sync the compliance report monitors and limits
		syncMonitorsAndLimitstWithFacility(complianceReport, facility);

		// Unselect any limits that were added to the report during the
		// synchronization
		List<ComplianceReportLimit> crlList = new ArrayList<ComplianceReportLimit>();
		crlList = Arrays.asList(complianceReportDAO()
				.retrieveComplianceReportLimitListByReportId(
						complianceReport.getReportId()));

		for (ComplianceReportLimit i : crlList) {
			boolean found = false;
			for (ComplianceReportLimit j : complianceReport.getLimits()) {
				if (i.getCorrLimitId().equals(j.getCorrLimitId())) {
					// limit was added to report as part of the submission from the portal
					found = true;
					break;
				}
			}
			if (!found) {
				// limit was added to report as part of the synchronization
				i.setIncludedFlag(false);
				complianceReportDAO().modifyComplianceReportLimit(i);
			}
		}
	}

	@Override
	public ComplianceReportAttachment retrieveCRTradeSecretAttachmentInfoById(Integer tradeSecretDocId)
			throws DAOException {
		return complianceReportDAO().retrieveCRTradeSecretAttachmentInfoById(tradeSecretDocId);
	}
}



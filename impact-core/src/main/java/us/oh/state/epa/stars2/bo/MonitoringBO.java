package us.oh.state.epa.stars2.bo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.DocumentException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.monitoring.MonitorReportDocument;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.pdf.monitoring.MonitorReportPdfGenerator;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dbObjects.monitoring.Monitor;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroupNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSiteNote;

@Transactional(rollbackFor=Exception.class)
@Service
public class MonitoringBO extends BaseBO implements MonitoringService {

	@Override
	public List<Document> getPrintableDocumentList(MonitorReport monitorReport,
			String user, boolean readOnly) throws DAOException {
		
		List<Document> docList = new ArrayList<Document>();

		MonitorReportDocument submittedMonitorReportDoc = null;
		// use previously generated PDF file if monitor report has been submitted
		if (monitorReport.getSubmittedDate() != null) { 
			// get submitted pdf document
			submittedMonitorReportDoc = getMonitorReportDocument(monitorReport);
			try {
				if (submittedMonitorReportDoc != null && DocumentUtil.canRead(submittedMonitorReportDoc.getPath())) {
					// use existing doc if already generated
					docList.add(submittedMonitorReportDoc);
				} else {
					// generate new doc if it does not already exist
					String userName = null;
					SubmissionLog log = getSubmissionLogForMonitorReport(monitorReport);
					if (log != null) {
						userName = log.getGatewayUserName();
					}
					submittedMonitorReportDoc = createMonitorReportDocument(
							monitorReport, userName, true, false);
					if (submittedMonitorReportDoc != null
							&& DocumentUtil.canRead(submittedMonitorReportDoc.getPath())) {
						docList.add(submittedMonitorReportDoc);
					}
				}
			} catch (IOException e) {
				logger.error(
						"Exception checking on submitted PDF document for "
								+ monitorReport.getMrptId(), e);
			}
		}
		
		TmpDocument sumRpt = new TmpDocument();
		generateTempMonitorReport(monitorReport, user, sumRpt, false, false);
		docList.add(sumRpt);

		Document zipDoc = null;
		if (monitorReport.getSubmittedDate() != null) { 
			zipDoc = generateTempAttachmentZipFile(monitorReport,
				null, submittedMonitorReportDoc, readOnly);
		} else {
			zipDoc = generateTempAttachmentZipFile(monitorReport,
				 sumRpt, null, readOnly);
		}
		if (zipDoc != null) {
			docList.add(zipDoc);
		}
		
		return docList;
	}
	

	public Document generateTempAttachmentZipFile(MonitorReport monitorReport,
			TmpDocument monitorReportPdf, MonitorReportDocument 
			submittedMonitorReportDoc, boolean readOnly) {

		TmpDocument erDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		erDoc.setDescription("Monitor Report Package zip file");
		erDoc.setTemporary(true);
		erDoc.setTmpFileName(monitorReport.getMrptId() + ".zip");
		erDoc.setContentType(Document.CONTENT_TYPE_ZIP);

		// make sure temporary directory exists
		OutputStream os = null;
		try {
			DocumentUtil.mkDirs(erDoc.getDirName());
			os = DocumentUtil.createDocumentStream(erDoc.getPath());
		} catch (IOException e) {
			String s = "Failed to createDocumentStream, path="
					+ erDoc.getPath() + ", os is "
					+ ((os != null) ? "not null" : "null") + " for monitor report "
					+ monitorReport.getMrptId();
			logger.error(s, e);
			erDoc = null;
		}
		ZipOutputStream zos = new ZipOutputStream(os);
		boolean ok = false;
		try {
			ok = zipAttachments(monitorReport, zos, monitorReportPdf, submittedMonitorReportDoc,
					readOnly);
		} catch (IOException e) {
			String s = "Failed to zip attachments, path=" + erDoc.getPath()
					+ ", os is " + ((os != null) ? "not null" : "null")
					+ " for monitor report " + monitorReport.getMrptId();
			logger.error(s, e);
			erDoc = null;
		} finally {
			try {
				zos.close();
				os.close();
			} catch (FileNotFoundException e) {
				String s = "Failed to zip attachments, path=" + erDoc.getPath()
						+ ", os is " + ((os != null) ? "not null" : "null")
						+ " for monitor report " + monitorReport.getMrptId();
				logger.error(s, e);
				erDoc = null;
			} catch (IOException e) {
				String s = "Failed to zip attachments, path=" + erDoc.getPath()
						+ ", os is " + ((os != null) ? "not null" : "null")
						+ " for monitor report " + monitorReport.getMrptId();
				logger.error(s, e);
				erDoc = null;
			}
		}
		if (!ok)
			erDoc = null;
		return erDoc;
	}

	private boolean zipAttachments(MonitorReport monitorReport, ZipOutputStream zos,
			TmpDocument facilityPdf, TmpDocument monitorReportPdf, MonitorReportDocument submittedMonitorReportDoc,
			Boolean hideTradeSecret, boolean readOnly) throws DAOException {
		DocumentService docBO = null;
		// int attachCnt = 0;
		try {
			docBO = ServiceFactory.getInstance().getDocumentService();
		} catch (ServiceFactoryException e) {
			logger.error("Exception accessing DocumentService for monitor report "
					+ monitorReport.getMrptId(), e);
			throw new DAOException(
					"Exception accessing DocumentService for monitor report "
							+ monitorReport.getMrptId(), e);
		}

		InputStream docIS = null;
		if (submittedMonitorReportDoc != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(submittedMonitorReportDoc
						.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ submittedMonitorReportDoc.getPath()
						+ ") failed for submitted monitor report pdf " + monitorReport.getMrptId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ submittedMonitorReportDoc.getPath()
						+ ") failed for submitted monitor report pdf " + monitorReport.getMrptId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(submittedMonitorReportDoc.getOverloadFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error("addEntryToZip) failed for submitted monitor report pdf "
							+ monitorReport.getMrptId(), ioe);
					return false;
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}
		}
		
		docIS = null;
		if (monitorReportPdf != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(monitorReportPdf
						.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ monitorReportPdf.getPath()
						+ ") failed for monitor report pdf " + monitorReport.getMrptId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ monitorReportPdf.getPath()
						+ ") failed for monitor report pdf " + monitorReport.getMrptId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(monitorReportPdf.getTmpFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error("addEntryToZip) failed for monitor report pfd "
							+ monitorReport.getMrptId(), ioe);
					return false;
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}
		}

		docIS = null;
		if (facilityPdf != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(facilityPdf.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error(
						"DocumentUtil.getDocumentAsStream("
								+ facilityPdf.getPath()
								+ ") failed for facility pdf for monitor report "
								+ monitorReport.getMrptId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error(
						"DocumentUtil.getDocumentAsStream("
								+ facilityPdf.getPath()
								+ ") failed for facility pdf for monitor report "
								+ monitorReport.getMrptId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(facilityPdf.getTmpFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error(
							"addEntryToZip) failed for facility pdf for monitor report "
									+ monitorReport.getMrptId(), ioe);
					return false;
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}
		}

		// add attachments to zip file
		List<Attachment> attachmentList = new ArrayList<Attachment>();
		attachmentList.addAll(Arrays.asList(monitorReport.getAttachments()));
		HashSet<Integer> docIdSet = new HashSet<Integer>();
		for (Attachment attachment : attachmentList) {
			// don't include documents added after monitor report was submitted
			if (monitorReport.getSubmittedDate() != null
					&& attachment.getLastModifiedTS() != null
					&& attachment.getLastModifiedTS().after(
							monitorReport.getSubmittedDate())) {
				logger.debug("Excluding document "
						+ attachment.getDocumentID()
						+ " from monitor report zip file. Document last modified date ("
						+ attachment.getLastModifiedTS()
						+ ") is after monitor report submission date ("
						+ monitorReport.getSubmittedDate() + ")");
				continue;
			}
						
			if (attachment.getDocumentID() != null
					&& !docIdSet.contains(attachment.getDocumentID())) {
				docIdSet.add(attachment.getDocumentID());
				Document doc = null;
				try {
					doc = docBO.getDocumentByID(attachment.getDocumentID(),
							readOnly);
				} catch (RemoteException re) {
					logger.error(
							"docBO.getDocumentByID("
									+ attachment.getDocumentID()
									+ ") failed for monitor report " + monitorReport.getMrptId(),
							re);
					return false;
				}
				docIS = null;
				if (doc != null && !doc.isTemporary()) {
					try {
						docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
						// attachCnt++;
					} catch (FileNotFoundException e) {
						docIS = null;
						logger.error(
								"DocumentUtil.getDocumentAsStream("
										+ doc.getPath()
										+ ") failed for monitor report "
										+ monitorReport.getMrptId(), e);
						return false;
					} catch (IOException e) {
						docIS = null;
						logger.error(
								"DocumentUtil.getDocumentAsStream("
										+ doc.getPath()
										+ ") failed for monitor report "
										+ monitorReport.getMrptId(), e);
						return false;
					}
					if (docIS != null) {
						try {
							addEntryToZip(
									getNameForDoc(attachment.getDocTypeCd(),
											doc.getDocumentID().toString(),
											null, doc.getExtension()), docIS,
									zos);
						} catch (IOException ioe) {
							logger.error(
									"addEntryToZip(" + doc.getDescription()
											+ ") failed for monitor report "
											+ monitorReport.getMrptId(), ioe);
							return false;
						} finally {
							try {
								docIS.close();
							} catch (IOException e) {
								;
							}
						}
					}
				} else {
					String msg = "No document found with id "
							+ attachment.getDocumentID();
					if (doc != null) {
						msg = "Document " + attachment.getDocumentID()
								+ " is temporary";
					}
					logger.error(msg, new Exception());
					return false;
				}
			}

			if (!hideTradeSecret) {
				Document doc = null;
				if (attachment.getTradeSecretDocId() != null
						&& !docIdSet.contains(attachment.getTradeSecretDocId()) && (monitorReport.getSubmittedDate() != null || attachment.isTradeSecretAllowed())) {
					docIdSet.add(attachment.getTradeSecretDocId());
					try {
						doc = docBO.getDocumentByID(
								attachment.getTradeSecretDocId(), readOnly);

					} catch (RemoteException re) {
						logger.error(
								"docBO.getDocumentByID("
										+ attachment.getDocumentID()
										+ ") failed for monitor report "
										+ monitorReport.getMrptId(), re);
						return false;
					}
					if (doc != null && !doc.isTemporary()) {
						docIS = null;
						try {
							docIS = DocumentUtil.getDocumentAsStream(doc
									.getPath());
							// attachCnt++;
						} catch (FileNotFoundException e) {
							docIS = null;
							logger.error(
									"DocumentUtil.getDocumentAsStream("
											+ doc.getPath()
											+ ") failed for monitor report "
											+ monitorReport.getMrptId(), e);
							return false;
						} catch (IOException e) {
							docIS = null;
							logger.error(
									"DocumentUtil.getDocumentAsStream("
											+ doc.getPath()
											+ ") failed for monitor report "
											+ monitorReport.getMrptId(), e);
							return false;
						}

						if (docIS != null) {
							try {
								addEntryToZip(
										getNameForDoc(
												attachment.getDocTypeCd(), doc
														.getDocumentID()
														.toString(), "_TS",
												doc.getExtension()), docIS, zos);

							} catch (IOException ioe) {
								logger.error(
										"addEntryToZip(" + doc.getDescription()
												+ ") failed for monitor report "
												+ monitorReport.getMrptId(), ioe);
								return false;
							} finally {
								try {
									docIS.close();
								} catch (IOException e) {
									;
								}
							}
						}

					} else if (doc == null) {
						logger.error("No document found with id "
								+ attachment.getDocumentID()
								+ " for monitor report " + monitorReport.getMrptId());
					}
				}
			}
		}
		return true;
	}

	private String getNameForDoc(String docTypeCd, String docId, String suffix,
			String extension) {
		StringBuffer docName = new StringBuffer();
		if (docTypeCd != null) {
//			docName.append(StAttachmentTypeDef.getData().getItems()
//					.getItemDesc(docTypeCd)
//					+ "_");
			docName.append(docTypeCd
					+ "_");
		}
		docName.append(docId);
		if (suffix != null) {
			docName.append(suffix);
		}
		if (extension != null && extension.length() > 0) {
			docName.append("." + extension);
		}
		return docName.toString();
	}



	private boolean generateTempMonitorReport(MonitorReport monitorReport, 
			String user, TmpDocument rptDoc, boolean isSubmittedPDFDoc, 
			boolean includeAllAttachments) throws DAOException {
		boolean rtn = false;
		try {
			// Set the path elements of the temp doc.
			rptDoc.setDescription("Monitor Report");
			rptDoc.setFacilityID(monitorReport.getFacilityId());
			rptDoc.setTemporary(true);
			rptDoc.setTmpFileName("monitorReport_" + monitorReport.getMrptId()
					+ ".pdf");
			// the items below are not needed since this document data is not
			// stored in the database
			// appDoc.setLastModifiedBy();
			// appDoc.setLastModifiedTS(new
			// Timestamp(System.currentTimeMillis()));
			// appDoc.setUploadDate(appDoc.getLastModifiedTS());
			DocumentUtil.mkDir(rptDoc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(rptDoc
					.getPath());
			rtn = writeMonitorReportToStream(monitorReport, os, rptDoc,
					isSubmittedPDFDoc, includeAllAttachments);
			os.close();

		} catch (Exception ex) {
			logger.error("Cannot generate monitor report ", ex);
			throw new DAOException("Cannot generate monitor report", ex);
		}
		return rtn;
	}

	private boolean writeMonitorReportToStream(
			MonitorReport monitorReport, OutputStream os, TmpDocument doc,
			boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws IOException {
		boolean rtn = false;
		try {
			// make sure we have all Facility information
			MonitorReportPdfGenerator generator = new MonitorReportPdfGenerator();
			generator.generatePdf(monitorReport, os, isSubmittedPDFDoc, includeAllAttachments);
		} catch (DocumentException e) {
			logger.error(
					"Exception writing emission report to stream for monitor report "
							+ monitorReport.getMrptId(), e);
			throw new IOException(
					"Exception writing emission report to stream for report "
							+ monitorReport.getMrptId());
		}
		return rtn;
	}

	public Document generateTempMonitorReportAttachmentZipFile(
			MonitorReport monitorReport, MonitorReportDocument submittedRpt,
			TmpDocument rpt, boolean hideTradeSecret, boolean readOnly)
			throws FileNotFoundException, IOException {

		TmpDocument tmpDoc;
		tmpDoc = new TmpDocument();
		tmpDoc.setDescription("Monitor Report ZIP file");
		tmpDoc.setFacilityID(monitorReport.getFacilityId());
		tmpDoc.setTemporary(true);
		tmpDoc.setTmpFileName("monitorReport_"
				+ monitorReport.getMrptId() + ".zip");
		DocumentUtil.mkDirs(tmpDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(tmpDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		boolean hasFiles = zipAttachments(monitorReport, submittedRpt, rpt,
				zos, readOnly);
		if (!hasFiles)
			return null;
		else {
			zos.close();
			os.close();
			return tmpDoc;
		}
	}
	private boolean zipAttachments(MonitorReport monitorReport, ZipOutputStream zos,
			TmpDocument monitorReportPdf, MonitorReportDocument submittedMonitorReportDoc,
			boolean readOnly) throws DAOException {
		DocumentService docBO = null;
		// int attachCnt = 0;
		try {
			docBO = ServiceFactory.getInstance().getDocumentService();
		} catch (ServiceFactoryException e) {
			logger.error("Exception accessing DocumentService for monitor reports "
					+ monitorReport.getMrptId(), e);
			throw new DAOException(
					"Exception accessing DocumentService for monitor report "
							+ monitorReport.getMrptId(), e);
		}

		InputStream docIS = null;
		if (submittedMonitorReportDoc != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(submittedMonitorReportDoc
						.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ submittedMonitorReportDoc.getPath()
						+ ") failed for submitted monitor report pdf " + monitorReport.getMrptId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ submittedMonitorReportDoc.getPath()
						+ ") failed for submitted monitor report pdf " + monitorReport.getMrptId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(submittedMonitorReportDoc.getOverloadFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error("addEntryToZip) failed for submitted monitor report pdf "
							+ monitorReport.getMrptId(), ioe);
					return false;
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}
		}
		
		docIS = null;
		if (monitorReportPdf != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(monitorReportPdf
						.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ monitorReportPdf.getPath()
						+ ") failed for monitor report pdf " + monitorReport.getMrptId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ monitorReportPdf.getPath()
						+ ") failed for monitor report pdf " + monitorReport.getMrptId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(monitorReportPdf.getTmpFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error("addEntryToZip) failed for monitor report pfd "
							+ monitorReport.getMrptId(), ioe);
					return false;
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}
		}

		docIS = null;

		// add attachments to zip file
		List<Attachment> attachmentList = new ArrayList<Attachment>();
		attachmentList.addAll(Arrays.asList(monitorReport.getAttachments()));
		HashSet<Integer> docIdSet = new HashSet<Integer>();
		for (Attachment attachment : attachmentList) {
			// don't include documents added after monitor report was submitted
			if (monitorReport.getSubmittedDate() != null
					&& attachment.getLastModifiedTS() != null
					&& attachment.getLastModifiedTS().after(
							monitorReport.getSubmittedDate())) {
				logger.debug("Excluding document "
						+ attachment.getDocumentID()
						+ " from monitor report zip file. Document last modified date ("
						+ attachment.getLastModifiedTS()
						+ ") is after monitor report submission date ("
						+ monitorReport.getSubmittedDate() + ")");
				continue;
			}
						
			if (attachment.getDocumentID() != null
					&& !docIdSet.contains(attachment.getDocumentID())) {
				docIdSet.add(attachment.getDocumentID());
				Document doc = null;
				try {
					doc = docBO.getDocumentByID(attachment.getDocumentID(),
							readOnly);
				} catch (RemoteException re) {
					logger.error(
							"docBO.getDocumentByID("
									+ attachment.getDocumentID()
									+ ") failed for monitor report " + monitorReport.getMrptId(),
							re);
					return false;
				}
				docIS = null;
				if (doc != null && !doc.isTemporary()) {
					try {
						docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
						// attachCnt++;
					} catch (FileNotFoundException e) {
						docIS = null;
						logger.error(
								"DocumentUtil.getDocumentAsStream("
										+ doc.getPath()
										+ ") failed for monitor report "
										+ monitorReport.getMrptId(), e);
						return false;
					} catch (IOException e) {
						docIS = null;
						logger.error(
								"DocumentUtil.getDocumentAsStream("
										+ doc.getPath()
										+ ") failed for monitor report "
										+ monitorReport.getMrptId(), e);
						return false;
					}
					if (docIS != null) {
						try {
							addEntryToZip(
									getNameForDoc(attachment.getDocTypeCd(),
											doc.getDocumentID().toString(),
											null, doc.getExtension()), docIS,
									zos);
						} catch (IOException ioe) {
							logger.error(
									"addEntryToZip(" + doc.getDescription()
											+ ") failed for monitor report "
											+ monitorReport.getMrptId(), ioe);
							return false;
						} finally {
							try {
								docIS.close();
							} catch (IOException e) {
								;
							}
						}
					}
				} else {
					String msg = "No document found with id "
							+ attachment.getDocumentID();
					if (doc != null) {
						msg = "Document " + attachment.getDocumentID()
								+ " is temporary";
					}
					logger.error(msg, new Exception());
					return false;
				}
			}

//			if (!hideTradeSecret) {
//				Document doc = null;
//				if (attachment.getTradeSecretDocId() != null
//						&& !docIdSet.contains(attachment.getTradeSecretDocId()) && (monitorReport.getSubmittedDate() != null || attachment.isTradeSecretAllowed())) {
//					docIdSet.add(attachment.getTradeSecretDocId());
//					try {
//						doc = docBO.getDocumentByID(
//								attachment.getTradeSecretDocId(), readOnly);
//
//					} catch (RemoteException re) {
//						logger.error(
//								"docBO.getDocumentByID("
//										+ attachment.getDocumentID()
//										+ ") failed for monitor report "
//										+ monitorReport.getMrptId(), re);
//						return false;
//					}
//					if (doc != null && !doc.isTemporary()) {
//						docIS = null;
//						try {
//							docIS = DocumentUtil.getDocumentAsStream(doc
//									.getPath());
//							// attachCnt++;
//						} catch (FileNotFoundException e) {
//							docIS = null;
//							logger.error(
//									"DocumentUtil.getDocumentAsStream("
//											+ doc.getPath()
//											+ ") failed for monitor report "
//											+ monitorReport.getMrptId(), e);
//							return false;
//						} catch (IOException e) {
//							docIS = null;
//							logger.error(
//									"DocumentUtil.getDocumentAsStream("
//											+ doc.getPath()
//											+ ") failed for monitor report "
//											+ monitorReport.getMrptId(), e);
//							return false;
//						}
//
//						if (docIS != null) {
//							try {
//								addEntryToZip(
//										getNameForDoc(
//												attachment.getDocTypeCd(), doc
//														.getDocumentID()
//														.toString(), "_TS",
//												doc.getExtension()), docIS, zos);
//
//							} catch (IOException ioe) {
//								logger.error(
//										"addEntryToZip(" + doc.getDescription()
//												+ ") failed for monitor report "
//												+ monitorReport.getMrptId(), ioe);
//								return false;
//							} finally {
//								try {
//									docIS.close();
//								} catch (IOException e) {
//									;
//								}
//							}
//						}
//
//					} else if (doc == null) {
//						logger.error("No document found with id "
//								+ attachment.getDocumentID()
//								+ " for monitor report " + monitorReport.getMrptId());
//					}
//				}
//			}
		}
		return true;
	}


	private boolean zipAttachments(MonitorReport monitorReport,
			MonitorReportDocument submittedRpt, TmpDocument rpt,
			ZipOutputStream zos, boolean readOnly)
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
		List<Attachment> a = Arrays.asList(monitorReport.getAttachments());

		if (submittedRpt != null) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(submittedRpt.getPath());
				attachCnt++;
			} catch (FileNotFoundException e) {
				throw new RemoteException("submitted monitor report " +
						monitorReport.getMrptId() + " pdf not found", e);
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
				throw new RemoteException("monitor report "
						+ monitorReport.getMrptId() + " pdf not found", e);
			}
			addEntryToZip(rpt.getTmpFileName(), docIS, zos);
			docIS.close();
		}

		//for (int i = 0; i < a.size(); i++) {
		for (Attachment att : monitorReport.getAttachments()) {
			// skip attachments that were added by DAPC
			// don't include documents added after monitor report was submitted
			if (DefData.isDapcAttachmentOnly(att.getAttachmentType()) || (monitorReport.getSubmittedDate() != null
					&& att.getLastModifiedTS() != null
					&& att.getLastModifiedTS().after(
							monitorReport.getSubmittedDate()))) {
				logger.debug("Excluding document "
						+ att.getDocumentID()
						+ " from monitor report zip file. Document last modified date ("
						+ att.getLastModifiedTS()
						+ ") is after monitor report submission date ("
						+ monitorReport.getSubmittedDate() + ")");
				continue;
			}
			
			if (att.getDocumentID() != null
					&& !docIdSet.contains(att.getDocumentID())) {
				docIdSet.add(att.getDocumentID());
				Document doc = docBO.getDocumentByID(att.getDocumentID(),
						readOnly);
				if (doc != null && !doc.isTemporary()) {
					InputStream docIS = null;
					try {
						docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
						attachCnt++;
					} catch (FileNotFoundException e) {
						throw new FileNotFoundException(att.getDescription());
					}
					addEntryToZip(getNameForAttachment(att, ""), docIS, zos);
					docIS.close();
				} else if (doc == null) {
					logger.error("No document found with id "
							+ att.getDocumentID() + " for monitor report "
							+ monitorReport.getMrptId());
				}
			}
//			if (!hideTradeSecret) {
//				if (att.getTradeSecretDocId() != null
//						&& !docIdSet.contains(att.getTradeSecretDocId()) && (monitorReport.getSubmittedDate() != null || att.isTradeSecretAllowed())) {
//					docIdSet.add(att.getTradeSecretDocId());
//					Document doc = docBO.getDocumentByID(
//							att.getTradeSecretDocId(), readOnly);
//					if (doc != null && !doc.isTemporary()) {
//						InputStream docIS = null;
//						try {
//							docIS = DocumentUtil.getDocumentAsStream(doc
//									.getPath());
//							attachCnt++;
//						} catch (FileNotFoundException e) {
//							throw new FileNotFoundException(
//									att.getDescription());
//						}
//						addEntryToZip(getNameForAttachment(att, "_TS"), docIS,
//								zos);
//						docIS.close();
//					} else if (doc == null) {
//						logger.error("No document found with id "
//								+ att.getDocumentID()
//								+ " for monitor report "
//								+ monitorReport.getMrptId());
//					}
//				}
//			}
		}
		return attachCnt > 0;
	}
	
	private String getNameForAttachment(Attachment doc,
			String suffix) {
		StringBuffer docName = new StringBuffer();
		// add description here?
		docName.append(doc.getDocumentID());
		docName.append(suffix);
		if (doc.getExtension() != null && doc.getExtension().length() > 0) {
			docName.append("." + doc.getExtension());
		}
		return docName.toString();
	}
	


	private MonitorReportDocument getMonitorReportDocument(MonitorReport monitorReport) {
		MonitorReportDocument doc = new MonitorReportDocument();
		String submittedDocDesc = "Printable View of What Will Be Submitted from Data Entered";
		if (monitorReport.getSubmittedDate() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			submittedDocDesc = "Printable View of Monitor Report Data Submitted on "
					+ dateFormat.format(monitorReport.getSubmittedDate());
		}
		// Set the path elements of the temp doc.
		doc.setDescription(submittedDocDesc);
		doc.setFacilityID(monitorReport.getFacilityId());
		doc.setMonitorReportId(monitorReport.getReportId());
		doc.setTemporary(true);
		doc.setOverloadFileName("MonitorReport" + monitorReport.getMrptId()
				+ ".pdf");
		return doc;
	}
	
	private SubmissionLog getSubmissionLogForMonitorReport(MonitorReport monitorReport) {
		SubmissionLog log = null;
		SubmissionLog searchSubmissionLog = new SubmissionLog();
		Task t = new Task();
		HashMap<TaskType, String> taskTypeDescs = t.getTaskTypeDescs();
		searchSubmissionLog.setFacilityId(monitorReport.getFacilityId());
		searchSubmissionLog.setSubmissionType(taskTypeDescs.get(TaskType.MRPT));
		
		try {
			int count = 0;
			for (SubmissionLog tmp : facilityDAO().searchSubmissionLog(
					searchSubmissionLog, monitorReport.getSubmittedDate(),
					monitorReport.getSubmittedDate())) {
				log = tmp;
				count++;
			}
			if (count > 0) {
				logger.error("Multiple submissions found for monitor report: "
						+ monitorReport.getMrptId() + ". Setting user id to "
						+ log.getGatewayUserName());
			} else if (count == 0) {
				logger.error("No submissions found for monitor report: "
							+ monitorReport.getMrptId());
			}
		} catch (DAOException e) {
			logger.error("Exception retrieving monitor report from submission log"
					+ monitorReport.getMrptId(), e);
		}
		return log;
	}
	
	private MonitorReportDocument createMonitorReportDocument(MonitorReport monitorReport,
			String userName, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		MonitorReportDocument monitorReportDoc = null;
		try {
			monitorReportDoc = getMonitorReportDocument(monitorReport);
			OutputStream os = DocumentUtil.createDocumentStream(monitorReportDoc
					.getPath());
			try {
				MonitorReportPdfGenerator generator = new MonitorReportPdfGenerator();
				generator.setUserName(userName);
				generator.generatePdf(monitorReport, os, isSubmittedPDFDoc, includeAllAttachments);

			} catch (DocumentException e) {
				logger.error(
						"Exception writing monitor report to stream for "
								+ monitorReportDoc.getMonitorReportId(), e);
				throw new IOException(
						"Exception writing monitor report to stream for "
								+ monitorReportDoc.getMonitorReportId());
			}
			os.close();
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting monitor report as stream for "
							+ monitorReport.getMrptId(), e);
		}
		return monitorReportDoc;
	}
	
	public List<Document> getPrintableAttachmentList(MonitorReport monitorReport,
			boolean readOnly) throws DAOException {
		List<Document> docList = new ArrayList<Document>();

		// if hideTradeSecret is null; that means this is requested during
		// submit and return trade secret version if exists otherwise public
		// version.

		// add attachments
		Attachment attachments[] = monitorReport.getAttachments();

		boolean hasTS = generateAttachments(docList, attachments, readOnly, monitorReport.getSubmittedDate());
//		if (hideTradeSecret != null && !hideTradeSecret && !hasTS) {
//			// indicate that trade secret version should not have been
//			// requested.
//			return null;
//		}

		return docList;
	}

	private boolean generateAttachments(List<Document> docList,
			Attachment attachments[], boolean readOnly, Timestamp submittedDate)
			throws DAOException {
		Document doc;
		boolean hasTS = false;
		// DocumentDAO documentDAO =
		// documentDAO(getSchema(CommonConst.READONLY_SCHEMA));
		DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));

		for (Attachment attach : attachments) {
			// skip attachments that were added by AQD
			// don't include documents added after monitor report was submitted
			if (((CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP) || CompMgr.getAppName().equals(CommonConst.PUBLIC_APP))
					&& DefData.isDapcAttachmentOnly(attach.getAttachmentType())) || (submittedDate != null
							&& attach.getLastModifiedTS() != null
							&& attach.getLastModifiedTS().after(submittedDate))) {
				logger.debug("Excluding document "
						+ attach.getDocumentID()
						+ " from monitor report printable attachment list. Document last modified date ("
						+ attach.getLastModifiedTS()
						+ ") is after monitor report submission date ("
						+ submittedDate + ")");
				
				if (attach.getTradeSecretDocId() != null && attach.isTradeSecretAllowed()) {
					hasTS = true;
				}
				continue;
			}

			logger.debug(" Monitor Report Attachment; description = "
					+ attach.getDescription());
			if (attach.getDocumentID() != null) {
				doc = documentDAO.retrieveDocument(attach.getDocumentID());
				// document description may not be in synch with application
				// document
				doc.setDescription(attach.getDescription());
				if (!doc.isTemporary()) {
					docList.add(doc);
				}

//				if (!hTS && attach.getTradeSecretDocId() != null && (submittedDate != null || attach.isTradeSecretAllowed())) {
//					doc = documentDAO.retrieveDocument(attach
//							.getTradeSecretDocId());
//					if (doc != null) {
//						// document description may not be in synch with
//						// monitor report document
//						doc.setDescription(attach.getDescription()
//								+ " - Trade Secret version");
//						if (!doc.isTemporary()) {
//							docList.add(doc);
//							hasTS = true;
//						}
//					}
//				}
			}
		}
		return hasTS;
	}


	@Override
	public MonitorSite[] searchMonitorSites(MonitorSite searchObj)
			throws DAOException {
		return monitoringDAO().searchMonitorSites(searchObj);
	}
	
	@Override
	public MonitorSite[] searchMonitorSites(MonitorSite searchObj,
			boolean excludeFacilityOwned) throws DAOException {
		return monitoringDAO().searchMonitorSites(searchObj,
				excludeFacilityOwned);
	}

	@Override
	public MonitorSite retrieveMonitorSite(Integer siteId) throws DAOException {

		MonitorSite monitorSite = monitoringDAO().retrieveMonitorSite(siteId);

		MonitorSiteNote[] monitorSiteNotes;
		monitorSiteNotes = monitoringDAO().retrieveMonitorSiteNotes(siteId);

		monitorSite.setNotes(new ArrayList<MonitorSiteNote>(Arrays.asList(monitorSiteNotes)));
		monitorSite
				.setInspectionsReferencedIn(fullComplianceEvalDAO().retrieveInspectionIdsForAmbientMonSiteId(siteId));

		return monitorSite;
	}

	@Override
	public boolean modifyMonitorSite(MonitorSite monitorSite)
			throws DAOException {
		return monitoringDAO().modifyMonitorSite(monitorSite);
	}

	@Override
	public void deleteMonitorSite(MonitorSite monitorSite) throws DAOException {
		if (isInternalApp()) {
			// Removing notes
			monitoringDAO().removeMonitorSiteNotes(monitorSite.getSiteId());
			for (Note msNote : monitorSite.getNotes())
				infrastructureDAO().removeNote(msNote.getNoteId());
		}
		
		monitoringDAO().deleteMonitorSite(monitorSite);
	}

	@Override
	public MonitorGroup createMonitorGroup(MonitorGroup monitorGroup)
			throws DAOException {
		return monitoringDAO().createMonitorGroup(monitorGroup);
	}

	@Override
	public MonitorGroup retrieveMonitorGroup(Integer groupId)
			throws DAOException {
		MonitorGroup monitorGroup = monitoringDAO().retrieveMonitorGroup(groupId);
		
		MonitorGroupNote[] monitorGroupNotes;
		monitorGroupNotes = monitoringDAO().retrieveMonitorGroupNotes(
				groupId);
		
		monitorGroup.setNotes(new ArrayList<MonitorGroupNote>(Arrays
				.asList(monitorGroupNotes)));
		
		return monitorGroup;
	}

	@Override
	public boolean modifyMonitorGroup(MonitorGroup monitorGroup)
			throws DAOException {
		return monitoringDAO().modifyMonitorGroup(monitorGroup);
	}

	@Override
	public void deleteMonitorGroup(MonitorGroup monitorGroup)
			throws DAOException {
			
		// delete if there are any attachments
		Attachment[] attachments = monitoringDAO().retrieveMonitorGroupAttachments(monitorGroup.getGroupId());
		if(null != attachments && attachments.length > 0) {
			for(Attachment attachment : attachments) {
				removeMonitorGroupAttachment(attachment);
			}
		}
		
		if (isInternalApp()) {
			// Removing notes
			monitoringDAO().removeMonitorGroupNotes(monitorGroup.getGroupId());
			for (Note mgNote : monitorGroup.getNotes())
				infrastructureDAO().removeNote(mgNote.getNoteId());
		}
		monitoringDAO().deleteMonitorGroup(monitorGroup);
	}

	@Override
	public MonitorSite createMonitorSite(MonitorSite monitorSite)
			throws DAOException {
		return monitoringDAO().createMonitorSite(monitorSite);
	}

	@Override
	public MonitorGroup[] searchMonitorGroups(MonitorGroup searchObj)
			throws DAOException {
		return monitoringDAO().searchMonitorGroups(searchObj);
	}
	
	@Override
	public MonitorGroup[] searchMonitorGroups(MonitorGroup searchObj,
			boolean excludeFacilityOwned) throws DAOException {
		return monitoringDAO().searchMonitorGroups(searchObj,
				excludeFacilityOwned);
	}

	@Override
	public Monitor createMonitor(Monitor monitor) throws DAOException {
		return monitoringDAO().createMonitor(monitor);
	}

	@Override
	public Monitor[] searchMonitors(Monitor searchObj) throws DAOException {
		return monitoringDAO().searchMonitors(searchObj);
	}

	@Override
	public Monitor retrieveMonitor(Integer monitorId) throws DAOException {
		
		Monitor monitor = monitoringDAO().retrieveMonitor(monitorId);
		
		MonitorNote[] monitorNotes;
		monitorNotes = monitoringDAO().retrieveMonitorNotes(
				monitorId);
		
		monitor.setNotes(new ArrayList<MonitorNote>(Arrays
				.asList(monitorNotes)));
		
		return monitor;
	}

	@Override
	public boolean modifyMonitor(Monitor monitor) throws DAOException {
		return monitoringDAO().modifyMonitor(monitor);
	}

	@Override
	public void deleteMonitor(Monitor monitor) throws DAOException {
		if (isInternalApp()) {
			// Removing notes
			monitoringDAO().removeMonitorNotes(monitor.getMonitorId());
			for (Note mNote : monitor.getNotes())
				infrastructureDAO().removeNote(mNote.getNoteId());
		}
		monitoringDAO().deleteMonitor(monitor);
	}

	@Override
	public Monitor[] searchMonitorsByAqsId(String aqsSiteId, Integer siteId)
			throws DAOException {
		return monitoringDAO().searchMonitorsByAqsId(aqsSiteId,siteId);
	}
	
	@Override
	public Attachment createMonitorGroupAttachment(Integer groupId, Attachment attachment, InputStream fileStream)
			throws DAOException {
		attachment = (Attachment) documentDAO().createDocument(attachment);
		attachment = monitoringDAO().createMonitorGroupAttachment(groupId, attachment);
		try {
			DocumentUtil.createDocument(attachment.getPath(), fileStream);
		}catch (IOException ioe) {
			throw new DAOException(ioe.getMessage(), ioe);
		}
		
		return attachment;
	}
	
	@Override
	public Attachment[] retrieveMonitorGroupAttachments(Integer groupId)
			throws DAOException {
		return monitoringDAO().retrieveMonitorGroupAttachments(groupId);
	}
	
	@Override
	public Attachment updateMonitorGroupAttachment(Attachment attachment) throws DAOException {
		attachment.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
		if(null == attachment.getUploadDate()) {
			attachment.setUploadDate(attachment.getLastModifiedTS());
		}
		documentDAO().modifyDocument(attachment);
		return monitoringDAO().updateMonitorGroupAttachment(attachment);
	}
	
	@Override
	public void removeMonitorGroupAttachment(Attachment attachment) throws DAOException {
		monitoringDAO().removeMonitorGroupAttachment(attachment);
		attachment.setTemporary(true);
		documentDAO().modifyDocument(attachment);
	}
	
	// Monitor Group Notes
	
	@Override
	public MonitorGroupNote[] retrieveMonitorGroupNotes(int monitorGroupId)
			throws DAOException {
		return monitoringDAO().retrieveMonitorGroupNotes(monitorGroupId);
	}

	@Override
	public MonitorGroupNote createMonitorGroupNote(
			MonitorGroupNote monitorGroupNote) throws DAOException {
		MonitorGroupNote ret = null;

		ret = createMonitorGroupNoteInternal(monitorGroupNote);

		if (ret != null) {
			logger.info("Successfully inserted Monitor Group Note");
		} else {

			logger.error("Failed to insert Monitor Group Note");
		}

		return ret;
	}

	public MonitorGroupNote createMonitorGroupNoteInternal(
			MonitorGroupNote monitorGroupNote) throws DAOException {

		Note tempNote = infrastructureDAO().createNote(monitorGroupNote);

		if (tempNote != null) {
			monitorGroupNote.setNoteId(tempNote.getNoteId());

			monitoringDAO().createMonitorGroupNote(
					monitorGroupNote.getMonitorGroupId(),
					monitorGroupNote.getNoteId());
		} else
			monitorGroupNote = null;
		return monitorGroupNote;
	}

	@Override
	public boolean modifyMonitorGroupNote(MonitorGroupNote monitorGroupNote)
			throws DAOException {
		return infrastructureDAO().modifyNote(monitorGroupNote);
	}
	
	// Monitor Site Notes
	
	@Override
	public MonitorSiteNote[] retrieveMonitorSiteNotes(int monitorSiteId)
			throws DAOException {
		return monitoringDAO().retrieveMonitorSiteNotes(monitorSiteId);
	}

	@Override
	public MonitorSiteNote createMonitorSiteNote(
			MonitorSiteNote monitorSiteNote) throws DAOException {
		MonitorSiteNote ret = null;

		ret = createMonitorSiteNoteInternal(monitorSiteNote);

		if (ret != null) {
			logger.error("Successfully inserted Monitor Site Note");
		} else {

			logger.error("Failed to insert Monitor Site Note");
		}

		return ret;
	}

	public MonitorSiteNote createMonitorSiteNoteInternal(
			MonitorSiteNote monitorSiteNote) throws DAOException {

		Note tempNote = infrastructureDAO().createNote(monitorSiteNote);

		if (tempNote != null) {
			monitorSiteNote.setNoteId(tempNote.getNoteId());

			monitoringDAO().createMonitorSiteNote(
					monitorSiteNote.getMonitorSiteId(),
					monitorSiteNote.getNoteId());
		} else
			monitorSiteNote = null;
		return monitorSiteNote;
	}

	@Override
	public boolean modifyMonitorSiteNote(MonitorSiteNote monitorSiteNote)
			throws DAOException {
		return infrastructureDAO().modifyNote(monitorSiteNote);
	}
	
	// Monitor Notes
	
		@Override
		public MonitorNote[] retrieveMonitorNotes(int monitorId)
				throws DAOException {
			return monitoringDAO().retrieveMonitorNotes(monitorId);
		}

		@Override
		public MonitorNote createMonitorNote(
				MonitorNote monitorNote) throws DAOException {
			MonitorNote ret = null;

			ret = createMonitorNoteInternal(monitorNote);

			if (ret != null) {
				logger.error("Successfully inserted Monitor Note");
			} else {

				logger.error("Failed to insert Monitor Note");
			}

			return ret;
		}

		public MonitorNote createMonitorNoteInternal(
				MonitorNote monitorNote) throws DAOException {

			Note tempNote = infrastructureDAO().createNote(monitorNote);

			if (tempNote != null) {
				monitorNote.setNoteId(tempNote.getNoteId());

				monitoringDAO().createMonitorNote(
						monitorNote.getMonitorId(),
						monitorNote.getNoteId());
			} else
				monitorNote = null;
			return monitorNote;
		}

		@Override
		public boolean modifyMonitorNote(MonitorNote monitorNote)
				throws DAOException {
			return infrastructureDAO().modifyNote(monitorNote);
		}
		
		private int getUserID() {
			return InfrastructureDefs.getCurrentUserId();
		}


		private Integer createMonitoringWorkflow(MonitorReport report, Integer assignedUserId,
				Integer userId) throws RemoteException {
//			String type = app.getApplicationTypeCD();
//			Facility fp = app.getFacility();

			ReadWorkFlowService wfBO = null;
			try {
				wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
			} catch (ServiceFactoryException e) {
				throw new RemoteException(
						"Exception getting workflow service for monitoring", e);
			}
			Integer workflowId = null;
			String ptName = "Ambient Monitoring";
//			if (type.equalsIgnoreCase(ApplicationTypeDef.TITLE_V_APPLICATION)
//					//|| type.equalsIgnoreCase(ApplicationTypeDef.TITLE_IV_APPLICATION)
//					|| type.equalsIgnoreCase(ApplicationTypeDef.PTIO_APPLICATION)
//					|| type.equalsIgnoreCase(ApplicationTypeDef.RPC_REQUEST)) {
//				ptName = WorkFlowProcess.PERMIT_WORKFLOW_NAME;
//			} else if (type.equalsIgnoreCase(ApplicationTypeDef.RPR_REQUEST)) {
//				ptName = WorkFlowProcess.REVOCATION;
//			} else if (type.equalsIgnoreCase(ApplicationTypeDef.RPE_REQUEST)) {
//				ptName = WorkFlowProcess.RPE;
//			} else {
//				ptName = type; // only if the app typeCd is same as process
//			}
			// name
			workflowId = wfBO.retrieveWorkflowTempIdAndNm(null).get(ptName);
			Timestamp dueDt = null;
			String rush = "N";

			WorkFlowManager wfm = new WorkFlowManager();
			
			HashMap<String,String> data = new HashMap<String,String>();
//			data.put(WorkFlowManager.ROLE_DISCRIMINATOR, permit.getPermitType());
			data.put(WorkFlowManager.ASSIGNED_USER, String.valueOf(assignedUserId));
			
			//DONE 2475
			// pass in the permit type cd here (NSR or TV), using the data arg
			logger.debug(" Submitting workflow process");
			logger.debug("report.getCurrentFpId() = " + report.getCurrentFpId());
			WorkFlowResponse resp = wfm.submitProcess(workflowId,
					report.getReportId(), report.getCurrentFpId(), userId, rush,
					new Timestamp((new Date()).getTime()), dueDt, data, null);

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
				logger.error("Error while creating monitoring workflow: " + error);
				throw new DAOException(error);
			}
			return workflowId;
		}

		
		
		
		
		
		
		
		
		@Override
		public MonitorReport createMonitorReport(MonitorReport monitorReport) throws DAOException {
			String schema = isInternalApp()? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA;
			return monitoringDAO(schema).createMonitorReport(monitorReport);
		}
		
		@Override
		public MonitorReport[] retrieveMonitorReports(Integer monitorGroupId)
				throws DAOException {
			return retrieveMonitorReports(monitorGroupId,false);
		}
		
		@Override
		public MonitorReport[] retrieveMonitorReports(Integer monitorGroupId, boolean includeStaging)
				throws DAOException {
			ArrayList<MonitorReport> mrpts = new ArrayList<MonitorReport>();
			MonitorReport[] internalMrpts = monitoringDAO().retrieveMonitorReports(monitorGroupId);
			for (MonitorReport imrpt : internalMrpts) {
				//TODO is this unneccesary overhead?
				Attachment[] attachments =
						monitoringDAO().retrieveMonitorReportAttachments(imrpt.getReportId());
				imrpt.setAttachments(attachments);
				mrpts.add(imrpt);					
			}
			if (includeStaging) {
				MonitorReport[] stagingMrpts = 
						monitoringDAO(CommonConst.STAGING_SCHEMA).retrieveMonitorReports(monitorGroupId);
				for (MonitorReport smrpt : stagingMrpts) {
					Attachment[] attachments =
							monitoringDAO(CommonConst.STAGING_SCHEMA).retrieveMonitorReportAttachments(smrpt.getReportId());
					smrpt.setAttachments(attachments);
					mrpts.add(smrpt);
				}
			}
			return mrpts.toArray(new MonitorReport[0]);
		}
		
		@Override
		public MonitorReport retrieveMonitorReport(Integer reportId) throws DAOException {
			return retrieveMonitorReport(reportId, false);
		}
		
		@Override
		public MonitorReport retrieveMonitorReport(Integer reportId, boolean staging) throws DAOException {
			String schema = staging? CommonConst.STAGING_SCHEMA : CommonConst.READONLY_SCHEMA;
			MonitorReport ret = monitoringDAO(schema).retrieveMonitorReport(reportId);
			Attachment[] attachments = monitoringDAO(schema).retrieveMonitorReportAttachments(reportId);
			if (ret == null) {
				logger.debug("Report doesn't exist ... reportId = " + reportId);
			}
			if (ret != null) {
				ret.setAttachments(attachments);
			}
			return ret;
		}
		
		@Override
		public MonitorReport submitReport(MonitorReport monitorReport, Integer assignedUserId) throws DAOException {
			boolean ok = false;
			MonitorReport ret = null;
			Integer workFlowId = null;
			
			if(!monitorReport.isLegacyReport()) {
				try {
					// create a workflow only if it is not a legacy report
					workFlowId = createMonitoringWorkflow(monitorReport, assignedUserId, getUserID());
				} catch (RemoteException e) {
					throw new DAOException(e.getMessage(), e);
				}
			}	
			
			if(null != workFlowId || monitorReport.isLegacyReport()) {
				// set submitted status and date
				monitorReport.setSubmitted(true);
				monitorReport.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
				ok = monitoringDAO().modifyMonitorReport(monitorReport);
				if(ok) {
					ret = monitoringDAO().retrieveMonitorReport(monitorReport.getReportId());
				} else {
					throw new DAOException("Failed to update monitor report");
				}
			}
			
			return ret;
		}
		
		@Override
		public boolean modifyMonitorReport(MonitorReport monitorReport) throws DAOException {
			String schema = isInternalApp()? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA;
			return monitoringDAO(schema).modifyMonitorReport(monitorReport);
		}
		
		@Override
		public void deleteMonitorReport(MonitorReport monitorReport) throws DAOException {
			deleteMonitorReport(monitorReport, false);
		}
		
		@Override
		public void deleteMonitorReport(MonitorReport monitorReport, boolean deleteAttachmentFiles) throws DAOException {
			// delete if there are any attachments
			String schema = isInternalApp()? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA;
			Attachment[] attachments = monitoringDAO(schema).retrieveMonitorReportAttachments(monitorReport.getReportId());
			if(null != attachments && attachments.length > 0) {
				for(Attachment attachment : attachments) {
					removeMonitorReportAttachment(attachment, deleteAttachmentFiles);
				}
			}
			monitoringDAO(schema).deleteMonitorReport(monitorReport);
		}
		
		@Override
		public Attachment createMonitorReportAttachment(Integer reportId, Attachment attachment, InputStream fileStream)
				throws DAOException {
			String schema = isInternalApp()? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA;
			attachment = (Attachment) documentDAO(schema).createDocument(attachment);
			attachment = monitoringDAO(schema).createMonitorReportAttachment(reportId, attachment);
			try {
				DocumentUtil.createDocument(attachment.getPath(), fileStream);
			}catch (IOException ioe) {
				throw new DAOException(ioe.getMessage(), ioe);
			}
			
			return attachment;
		}
		
		@Override
		public Attachment[] retrieveMonitorReportAttachments(Integer reportId)
				throws DAOException {
			return retrieveMonitorReportAttachments(reportId, false);
		}
		
		@Override
		public Attachment[] retrieveMonitorReportAttachments(Integer reportId, boolean staging)
				throws DAOException {
			String schema = staging? CommonConst.STAGING_SCHEMA : CommonConst.READONLY_SCHEMA;
			return monitoringDAO(schema).retrieveMonitorReportAttachments(reportId);
		}
		
		@Override
		public Attachment updateMonitorReportAttachment(Attachment attachment) throws DAOException {
			String schema = isInternalApp()? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA;
			attachment.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
			if(null == attachment.getUploadDate()) {
				attachment.setUploadDate(attachment.getLastModifiedTS());
			}
			documentDAO(schema).modifyDocument(attachment);
			return monitoringDAO(schema).updateMonitorReportAttachment(attachment);
		}
		
		@Override
		public void removeMonitorReportAttachment(Attachment attachment) throws DAOException {
			removeMonitorReportAttachment(attachment, false);
		}

		@Override
		public void removeMonitorReportAttachment(Attachment attachment, boolean deleteAttachments) throws DAOException {
			String schema = isInternalApp() ? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA;
			monitoringDAO(schema).removeMonitorReportAttachment(attachment);
			attachment.setTemporary(true);
			documentDAO(schema).modifyDocument(attachment);
			if (isPortalApp()) {
				if(deleteAttachments) {
					documentDAO(schema).removeDocument(attachment);
				} else {
					documentDAO(schema).removeDocumentReference(attachment.getDocumentID());
				}
			}
		}
		
		@Override
		public boolean checkMonitorReportsValid(Integer groupId)
				throws DAOException {
			String schema = isInternalApp()? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA;
			return monitoringDAO(schema).checkMonitorReportsValid(groupId);
		}

		@Override
		public void createMonitorReportsFromGateway(List<MonitorReport> monitorReports)
				throws DAOException {

			MonitorGroup monitorGroup = null;
			Integer assignedUserId = null;

			for(MonitorReport monitorReport : monitorReports) {
				if(null == assignedUserId) {
					// get the associated monitor group reviewer id so that the workflow task
					// can be assigned to the correct internal user
					monitorGroup = monitoringDAO().retrieveMonitorGroup(monitorReport.getMonitorGroupId());
					assignedUserId = monitorGroup.getMonitorReviewerId();
				}
				
				// determine if the report already exists in the internal system, if it does then error out
				MonitorReport ret = monitoringDAO().retrieveMonitorReport(monitorReport.getReportId());
				if(null != ret) {
					// expecting ret to be null
					String errorMsg = "Monitor report with id " 
										+ monitorReport.getMrptId()
										+ " already exists in the internal system"; 
					logger.error(errorMsg, new Exception(errorMsg));
					throw(new DAOException(errorMsg));
				} else {
					// create the report
					MonitorReport newMonitorReport = null;
					monitorReport.setSubmittedDate(new Timestamp(System.currentTimeMillis()));
					monitorReport.setSubmitted(true);
					logger.debug("Creating monitoring report " + monitorReport.getReportId() + " from portal");
					newMonitorReport = monitoringDAO().createMonitorReport(monitorReport);
					if(null != newMonitorReport) {
						// create attachments
						if(null != monitorReport.getAttachments() && monitorReport.hasAttachments()) {
							for(Attachment attachment : monitorReport.getAttachments()) {
								createMonitorReportAttachmentFromPortal(newMonitorReport.getReportId(), attachment, false);
							}
						}
						
					// Populate the curent fpId in the MonitorReport so that it
					// can be used
					// when workflow is created.
					FacilityService facBO = null;
					try {
						facBO = ServiceFactory.getInstance()
								.getFacilityService();
					} catch (ServiceFactoryException e) {
						logger.error(
								"Exception accessing FacilityService for monitor report "
										+ monitorReport.getMrptId(), e);
						throw new DAOException(
								"Exception accessing FacilityService for monitor report "
										+ monitorReport.getMrptId(), e);
					}

					String facilityId = null;
					monitorGroup = monitoringDAO().retrieveMonitorGroup(
							monitorReport.getMonitorGroupId());

					// Set current fpId in monitorReport so it can be used in
					// workflow createProcess.
					facilityId = monitorGroup.getFacilityId();
					if (!Utility.isNullOrEmpty(facilityId)) {
						// logger.debug("facilityId = " + facilityId);
						Facility fp = null;
						try {
							fp = facBO.retrieveFacility(facilityId);
						} catch (Exception ex) {
							DisplayUtil
									.displayError("Failed to retrieve facility.");
						}
						Integer currentFpId = fp.getFpId();
						// logger.debug("currentFpId = " + currentFpId);
						newMonitorReport.setCurrentFpId(currentFpId);
					} else {
						newMonitorReport.setCurrentFpId(null);
					}
					// END Populate the curent fpId in the MonitorReport
						
						// create workflow
						try {
							createMonitoringWorkflow(newMonitorReport, assignedUserId, CommonConst.GATEWAY_USER_ID);
						} catch (RemoteException re) {
							logger.error("Unable to create workflow for submitted monitor report "
											+ newMonitorReport.getMrptId(), re);
							throw new DAOException(re.getMessage(), re);
						}
					}
				}
			}
			
			return;
		}
		
	@Override
	public Attachment createMonitorReportAttachmentFromPortal(Integer reportId, Attachment attachment, boolean staging)
			throws DAOException {
		String schema = staging ? CommonConst.READONLY_SCHEMA : CommonConst.STAGING_SCHEMA;
		attachment = (Attachment) documentDAO(schema).createDocument(attachment);
		return monitoringDAO(schema).createMonitorReportAttachment(reportId, attachment);
	}

	@Override
	public List<FceAmbientMonitorLineItem> searchFacilityMonitorsByDate(String facilityId, Timestamp startDate, Timestamp endDate)
			throws DAOException {
		List<FceAmbientMonitorLineItem> ret = monitoringDAO().searchFacilityMonitorsByDate(facilityId, startDate, endDate);
		return ret;
	}
}

package us.oh.state.epa.stars2.bo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.AgencyDef;
import us.oh.state.epa.stars2.def.ProjectAttachmentTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;
import us.oh.state.epa.stars2.webcommon.pdf.project.ProjectTrackingPdfGenerator;
import us.wy.state.deq.impact.database.dao.ProjectTrackingDAO;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Budget;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Contract;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.GrantProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.LetterProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.NEPAProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Project;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectAttachment;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectDocument;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectNote;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectSearchResult;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectTrackingEvent;
import us.wy.state.deq.impact.webcommon.projectTracking.ProjectTrackingSearchCriteria;

import com.lowagie.text.DocumentException;

@Transactional(rollbackFor=Exception.class)
@Service
public class ProjectTrackingBO extends BaseBO implements ProjectTrackingService {


	@Override
	public List<Document> getPrintableDocumentList(Project project, String user) 
			throws DAOException {
		
		List<Document> docList = new ArrayList<Document>();

		ProjectDocument projectDoc = null;
		
		TmpDocument tempProjectDoc = new TmpDocument();
		generateTempEmissionsTest(project, user, tempProjectDoc, false);
		docList.add(tempProjectDoc);

		Document zipDoc = null;
			zipDoc = generateTempAttachmentZipFile(project,
				 tempProjectDoc);
		if (zipDoc != null) {
			docList.add(zipDoc);
		}
		
		return docList;
	}
	
	
	
	public Document generateTempAttachmentZipFile(Project project,
			TmpDocument tempProjectDoc) {

		TmpDocument erDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		erDoc.setDescription("Project Tracking Detail zip file");
		erDoc.setTemporary(true);
		erDoc.setTmpFileName(project.getProjectId() + ".zip");
		erDoc.setContentType(Document.CONTENT_TYPE_ZIP);

		// make sure temporary directory exists
		OutputStream os = null;
		try {
			DocumentUtil.mkDirs(erDoc.getDirName());
			os = DocumentUtil.createDocumentStream(erDoc.getPath());
		} catch (IOException e) {
			String s = "Failed to createDocumentStream, path="
					+ erDoc.getPath() + ", os is "
					+ ((os != null) ? "not null" : "null") + " for stack test "
					+ project.getProjectId();
			logger.error(s, e);
			erDoc = null;
		}
		ZipOutputStream zos = new ZipOutputStream(os);
		boolean ok = false;
		try {
			ok = zipAttachments(project, zos, tempProjectDoc);
		} catch (IOException e) {
			String s = "Failed to zip attachments, path=" + erDoc.getPath()
					+ ", os is " + ((os != null) ? "not null" : "null")
					+ " for stack test " + project.getProjectId();
			logger.error(s, e);
			erDoc = null;
		} finally {
			try {
				zos.close();
				os.close();
			} catch (FileNotFoundException e) {
				String s = "Failed to zip attachments, path=" + erDoc.getPath()
						+ ", os is " + ((os != null) ? "not null" : "null")
						+ " for stack test " + project.getProjectId();
				logger.error(s, e);
				erDoc = null;
			} catch (IOException e) {
				String s = "Failed to zip attachments, path=" + erDoc.getPath()
						+ ", os is " + ((os != null) ? "not null" : "null")
						+ " for stack test " + project.getProjectId();
				logger.error(s, e);
				erDoc = null;
			}
		}
		if (!ok)
			erDoc = null;
		return erDoc;
	}

	private boolean zipAttachments(Project project, ZipOutputStream zos,
			TmpDocument tempProjectDoc) throws DAOException {
		DocumentService docBO = null;
		// int attachCnt = 0;
		try {
			docBO = ServiceFactory.getInstance().getDocumentService();
		} catch (ServiceFactoryException e) {
			logger.error("Exception accessing DocumentService for stack test "
					+ project.getProjectId(), e);
			throw new DAOException(
					"Exception accessing DocumentService for stack test "
							+ project.getProjectId(), e);
		}

		InputStream docIS = null;

		if (tempProjectDoc != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(tempProjectDoc
						.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ tempProjectDoc.getPath()
						+ ") failed for stack test pdf " + project.getProjectId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ tempProjectDoc.getPath()
						+ ") failed for stack test pdf " + project.getProjectId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(tempProjectDoc.getTmpFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error("addEntryToZip) failed for stack test pfd "
							+ project.getProjectId(), ioe);
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
		List<ProjectAttachment> attachmentList = new ArrayList<ProjectAttachment>();
		attachmentList.addAll(project.getAttachments());
		HashSet<Integer> docIdSet = new HashSet<Integer>();
		for (ProjectAttachment attachment : attachmentList) {
						
			if (attachment.getDocumentID() != null
					&& !docIdSet.contains(attachment.getDocumentID())) {
				docIdSet.add(attachment.getDocumentID());
				Document doc = null;
				try {
					doc = docBO.getDocumentByID(attachment.getDocumentID(),
							true);
				} catch (RemoteException re) {
					logger.error(
							"docBO.getDocumentByID("
									+ attachment.getDocumentID()
									+ ") failed for stack test " + project.getProjectId(),
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
										+ ") failed for stack test "
										+ project.getProjectId(), e);
						return false;
					} catch (IOException e) {
						docIS = null;
						logger.error(
								"DocumentUtil.getDocumentAsStream("
										+ doc.getPath()
										+ ") failed for stack test "
										+ project.getProjectId(), e);
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
											+ ") failed for stack test "
											+ project.getProjectId(), ioe);
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
		}
		return true;
	}

	private String getNameForDoc(String docTypeCd, String docId, String suffix,
			String extension) {
		StringBuffer docName = new StringBuffer();
		if (docTypeCd != null) {
			docName.append(ProjectAttachmentTypeDef.getData().getItems()
					.getItemDesc(docTypeCd)
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

	
	
	private boolean generateTempEmissionsTest(Project project, String user, 
			TmpDocument tempProjectDoc, boolean includeAllAttachments) throws DAOException {
		boolean rtn = false;
		try {
			// Set the path elements of the temp doc.
			tempProjectDoc.setDescription("Project Tracking Detail");
//			rptDoc.setFacilityID(facility.getFacilityId()); //TODO is facility optional??
			tempProjectDoc.setTemporary(true);
			tempProjectDoc.setTmpFileName("projectTrackingDetail_" + project.getProjectId()
					+ ".pdf");
			// the items below are not needed since this document data is not
			// stored in the database
			// appDoc.setLastModifiedBy();
			// appDoc.setLastModifiedTS(new
			// Timestamp(System.currentTimeMillis()));
			// appDoc.setUploadDate(appDoc.getLastModifiedTS());
			DocumentUtil.mkDir(tempProjectDoc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(tempProjectDoc
					.getPath());
			rtn = writeProjectTrackingDetailToStream(project, os, tempProjectDoc,
					includeAllAttachments);
			os.close();

		} catch (Exception ex) {
			logger.error("Cannot generate project tracking detail ", ex);
			throw new DAOException("Cannot generate project tracking detail", ex);
		}
		return rtn;
	}

	private boolean writeProjectTrackingDetailToStream(Project project, 
			OutputStream os, TmpDocument doc, boolean includeAllAttachments) throws IOException {
		boolean rtn = false;
		try {
			// make sure we have all Facility information
			ProjectTrackingPdfGenerator generator = new ProjectTrackingPdfGenerator();
			generator.generatePdf(project, os, includeAllAttachments);
			// rtn = generator.isHasTS();
			// if (rtn)
			// PdfGeneratorBase.addTradeSecretWatermarkHorizontal(doc
			// .getPath());
		} catch (DocumentException e) {
			logger.error(
					"Exception writing emission report to stream for stack test "
							+ project.getProjectId(), e);
			throw new IOException(
					"Exception writing emission report to stream for report "
							+ project.getProjectId());
		}
		return rtn;
	}


	
	@Override
	public Project createProject(Project project) throws DAOException {
		Project ret = projectTrackingDAO().createProject(project);
		
		if( project instanceof NEPAProject) {
			ret = createNEPAProject((NEPAProject)ret);
		} else if (project instanceof GrantProject) {
			ret = createGrantProject((GrantProject)ret);
		} else if (project instanceof LetterProject) {
			ret = createLetterProject((LetterProject)ret);
		} else if (project instanceof Contract) {
			ret = createContract((Contract)ret);
		}
		
		return ret;
	}

	@Override
	public Project retrieveProject(Integer projectId) throws DAOException {
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
		
		Project project = projectTrackingDAO.retrieveProject(projectId);
		if(null != project) {
			// retrieve other project related data
			project.setProjectDivisionCds(projectTrackingDAO
					.retrieveProjectDivisionCds(projectId));
			
			project.setProjectLeadUserIds(Stars2Object
					.toStar2IntObject(projectTrackingDAO
							.retrieveProjectLeadUserIds(projectId)));
			
			project.setTrackingEvents(retrieveProjectTrackingEventsByProjectId(projectId));
			
			project.setNotes(projectTrackingDAO
					.retrieveProjectNotesByProjectId(projectId));
			
			project.setAttachments(projectTrackingDAO()
					.retrieveProjectTrackingAttachments(projectId));
			
			if(project instanceof NEPAProject) {
				project = retrieveNEPAProjectData((NEPAProject) project);
			} else if(project instanceof GrantProject) {
				project = retrieveGrantProjectData((GrantProject)project);
			} else if(project instanceof Contract) {
				project = retrieveContractData((Contract)project);
			}
		}
		
		return project;
	}

	@Override
	public boolean updateProject(Project project) throws DAOException {
		boolean ret = false;
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
		Integer projectId = project.getProjectId();
		
		projectTrackingDAO.updateProject(project);
		
		// update other project related data
		// divisions
		projectTrackingDAO.deleteProjectDivisions(projectId);
		projectTrackingDAO.createProjectDivisions(projectId, project.getProjectDivisionCds());
		
		// project lead(s)
		projectTrackingDAO.deleteProjectLeadXrefs(projectId);
		projectTrackingDAO.createProjectLeadXrefs(projectId, Stars2Object
				.fromStar2IntObject(project.getProjectLeadUserIds()));
		
		// if you come here then basic update has succeeded
		ret = true;
		
		// update type specific data
		if(project instanceof NEPAProject) {
			ret = updateNEPAProject((NEPAProject)project);
		} else if (project instanceof GrantProject) {
			ret = updateGrantProject((GrantProject)project);
		} else if (project instanceof LetterProject) {
			ret = updateLetterProject((LetterProject)project);
		} else if (project instanceof Contract) {
			ret = updateContract((Contract)project);
		}
		
		return ret;
	}

	// deletes only basic project - do not use it directly instead 
	// use deleteProject(Project project)
	private void deleteProject(Integer projectId) throws DAOException {
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
		
		// first delete dependent data
		// divisions
		projectTrackingDAO.deleteProjectDivisions(projectId);
		
		// project lead(s)
		projectTrackingDAO.deleteProjectLeadXrefs(projectId);
		
		// tracking events
		projectTrackingDAO.deleteProjectTrackingEventsByProjectId(projectId);
		
		// project
		projectTrackingDAO.deleteProject(projectId);
	}

	
	private NEPAProject createNEPAProject(NEPAProject nepaProject)
			throws DAOException {
		return projectTrackingDAO().createNEPAProject(nepaProject);
	}

	private boolean updateNEPAProject(NEPAProject nepaProject)
			throws DAOException {
		boolean ret = false;
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
	
		if(null != nepaProject) {
			Integer projectId = nepaProject.getProjectId();
	
			projectTrackingDAO.updateNEPAProject(nepaProject);
	
			// update other NEPA data
			// nepa level(s)
			projectTrackingDAO.deleteNEPAProjectLevelXrefs(projectId);
			projectTrackingDAO.createNEPAProjectLevelXrefs(projectId,
					nepaProject.getLevelCds());
	
			// lead agenci(es)
			projectTrackingDAO.deleteNEPAProjectLeadAgencyXrefs(projectId);
			projectTrackingDAO.createNEPAProjectLeadAgencyXrefs(projectId,
					nepaProject.getLeadAgencyCds());
	
			// BLM field office(s)
			projectTrackingDAO.deleteNEPAProjectBLMFieldOfficeXrefs(projectId);
			if (nepaProject.getLeadAgencyCds().contains(AgencyDef.BLM)) {
				projectTrackingDAO.createNEPAProjectBLMFieldOfficeXrefs(projectId,
						nepaProject.getBLMFieldOfficeCds());
			}
	
			// national forest(s)
			projectTrackingDAO.deleteNEPAProjectNationalForestXrefs(projectId);
			if (nepaProject.getLeadAgencyCds().contains(
					AgencyDef.US_FOREST_SERVICE)) {
				projectTrackingDAO.createNEPAProjectNationalForestXrefs(projectId,
						nepaProject.getNationalForestCds());
			}
	
			// national park(s)
			projectTrackingDAO.deleteNEPAProjectNationalParkXrefs(projectId);
			if (nepaProject.getLeadAgencyCds().contains(
					AgencyDef.NATIONAL_PARK_SERVICE)) {
				projectTrackingDAO.createNEPAProjectNationalParkXrefs(projectId,
						nepaProject.getNationalParkCds());
			}
	
			// if you come here then update has succeeded
			ret = true;
		}

		return ret;
	}
	
	private NEPAProject retrieveNEPAProjectData(NEPAProject nepaProject)
			throws DAOException {
		
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
		
		NEPAProject ret = nepaProject;

		if(null != ret) {
			Integer projectId = ret.getProjectId();
			
			// NEPA levels
			ret.setLevelCds(projectTrackingDAO
					.retrieveNEPAProjectLevelCds(projectId));
			
			// retrieve lead agencies
			ret.setLeadAgencyCds(projectTrackingDAO
					.retrieveNEPAProjectLeadAgencyCds(projectId));
			
			// retrieve BLM field offices
			ret.setBLMFieldOfficeCds(projectTrackingDAO
					.retrieveNEPAProjectBLMFieldOfficeCds(projectId));
			
			// retrieve national forests
			ret.setNationalForestCds(projectTrackingDAO
					.retrieveNEPAProjectNationalForestCds(projectId));
			
			// retrieve national parks
			ret.setNationalParkCds(projectTrackingDAO
					.retrieveNEPAProjectNationalParkCds(projectId));
		}
		
		return ret;
	}
	
	private void deleteNEPAProject(Integer projectId) throws DAOException {
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
		
		// first delete dependent data
		projectTrackingDAO.deleteNEPAProjectLevelXrefs(projectId);
		projectTrackingDAO.deleteNEPAProjectBLMFieldOfficeXrefs(projectId);
		projectTrackingDAO.deleteNEPAProjectNationalForestXrefs(projectId);
		projectTrackingDAO.deleteNEPAProjectNationalParkXrefs(projectId);
		projectTrackingDAO.deleteNEPAProjectLeadAgencyXrefs(projectId);
		
		// delete NEPA project
		projectTrackingDAO.deleteNEPAProject(projectId);
	}

	@Override
	public void deleteProject(Project project) throws DAOException {
		if(null != project) {
			Integer projectId = project.getProjectId();
			
			if(project instanceof NEPAProject) {
				deleteNEPAProject(projectId);
			} else if (project instanceof GrantProject) {
				deleteGrantProject(projectId);
			} else if (project instanceof LetterProject) {
				deleteLetterProject(projectId);
			} else if (project instanceof Contract) {
				deleteContract(projectId);
			}
			
			// delete project notes
			projectTrackingDAO().deleteProjectNotesByProjectId(projectId);
			for(ProjectNote note: project.getNotes()) {
				infrastructureDAO().removeNote(note.getNoteId());
			}
			
			// delete attachments
			for (ProjectAttachment pa : project.getAttachments()) {
				pa.getAttachment().setTemporary(true);
				documentDAO().modifyDocument(pa.getAttachment());
			}
			projectTrackingDAO().deleteProjectTrackingAttachments(projectId);
				
			
			deleteProject(projectId);
		} else {
			throw new DAOException("project is null");
		}
	}
	
	private GrantProject createGrantProject(GrantProject grantProject)
			throws DAOException {
		return projectTrackingDAO().createGrantProject(grantProject);
	}

	private boolean updateGrantProject(GrantProject grantProject)
			throws DAOException {
		boolean ret = false;
		if (null != grantProject) {
			ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
			Integer projectId = grantProject.getProjectId();
			ret = projectTrackingDAO.updateGrantProject(grantProject);
			
			//update accountant contacts
			projectTrackingDAO.deleteGrantProjectAccountantXrefs(projectId);
			projectTrackingDAO.createGrantProjectAccountantXrefs(projectId,
					Stars2Object.fromStar2IntObject(grantProject
							.getAccountantUserIds()));
		}

		return ret;
	}

	private void deleteGrantProject(Integer projectId) throws DAOException {
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
		
		projectTrackingDAO.deleteGrantProjectAccountantXrefs(projectId);
		projectTrackingDAO.deleteGrantProject(projectId);
	}
	
	private LetterProject createLetterProject(LetterProject letterProject)
			throws DAOException {
		return projectTrackingDAO().createLetterProject(letterProject);
	}

	private boolean updateLetterProject(LetterProject letterProject)
			throws DAOException {
		return projectTrackingDAO().updateLetterProject(letterProject);
	}

	private void deleteLetterProject(Integer projectId) throws DAOException {
		projectTrackingDAO().deleteLetterProject(projectId);
	}
	
	private GrantProject retrieveGrantProjectData(GrantProject grantProject)
			throws DAOException {
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();

		GrantProject ret = grantProject;
		
		if (null != ret) {
			Integer projectId = grantProject.getProjectId();
			// accountant contacts
			ret.setAccountantUserIds(Stars2Object
					.toStar2IntObject(projectTrackingDAO
							.retrieveGrantProjectAccountantUserIds(projectId)));
		}

		return ret;
	}

	@Override
	public ProjectTrackingEvent createProjectTrackingEvent(
			ProjectTrackingEvent event) throws DAOException {
		return projectTrackingDAO().createProjectTrackingEvent(event);
	}

	@Override
	public ProjectTrackingEvent retrieveProjectTrackingEvent(Integer eventId)
			throws DAOException {
		ProjectTrackingEvent ret = projectTrackingDAO()
				.retrieveProjectTrackingEvent(eventId);
		// retrieve basic info of any associated attachment(s)
		if (null != ret) {
			ret.setAssociatedAttachmentsInfo(projectTrackingDAO()
					.retrieveProjectTrackingEventAttachmentInfo(ret.getEventId()));
		}
		
		return ret;
	}
	
	@Override
	public List<ProjectTrackingEvent> retrieveProjectTrackingEventsByProjectId(
			Integer projectId) throws DAOException {
		
		List<ProjectTrackingEvent> projectTrackingEventList = projectTrackingDAO()
				.retrieveProjectTrackingEventsByProjectId(projectId);
		
		// retrieve basic info of any associated attachment(s)
		for (ProjectTrackingEvent event : projectTrackingEventList) {
			event.setAssociatedAttachmentsInfo(projectTrackingDAO()
					.retrieveProjectTrackingEventAttachmentInfo(event.getEventId()));
		}
		
		return projectTrackingEventList;
	}

	@Override
	public boolean updateProjectTrackingEvent(ProjectTrackingEvent event)
			throws DAOException {
		return projectTrackingDAO().updateProjectTrackingEvent(event);
	}

	@Override
	public void deleteProjectTrackingEvent(Integer eventId) throws DAOException {
		projectTrackingDAO().deleteProjectTrackingEvent(eventId);
		
	}
	
	@Override
	public void deleteProjectTrackingEventsByProjectId(Integer projectId)
			throws DAOException {
		projectTrackingDAO().deleteProjectTrackingEventsByProjectId(projectId);

	}

	@Override
	public ProjectSearchResult[] searchProjects(
			ProjectTrackingSearchCriteria criteria) throws DAOException {
		return projectTrackingDAO().searchProjects(criteria);
	}

	@Override
	public ProjectNote createProjectNote(ProjectNote note) throws DAOException {
		ProjectNote projectNote = note;
		Note ret = infrastructureDAO().createNote(note);
		projectNote.setNoteId(ret.getNoteId());
		return projectTrackingDAO().createProjectNote(projectNote);
	}

	@Override
	public List<ProjectNote> retrieveProjectNotesByProjectId(Integer projectId)
			throws DAOException {
		return projectTrackingDAO().retrieveProjectNotesByProjectId(projectId);
		
	}

	@Override
	public boolean modifyProjectNote(ProjectNote note) throws DAOException {
		return infrastructureDAO().modifyNote(note);
	}

	@Override
	public ProjectAttachment createProjectTrackingAttachment(
			ProjectAttachment projectAttachment, InputStream fileStream)
			throws DAOException {
		ProjectAttachment ret;

		Document doc = documentDAO().createDocument(
				projectAttachment.getAttachment());

		projectAttachment.getAttachment().setDocumentID(doc.getDocumentID());
		ret = projectTrackingDAO().createProjectTrackingAttachment(
				projectAttachment);

		try {
			DocumentUtil.createDocument(projectAttachment.getAttachment()
					.getPath(), fileStream);
		} catch (IOException ioe) {
			throw new DAOException(ioe.getMessage(), ioe);
		}

		return ret;
	}

	@Override
	public List<ProjectAttachment> retrieveProjectTrackingAttachments(
			Integer projectId) throws DAOException {
		return projectTrackingDAO().retrieveProjectTrackingAttachments(
				projectId);
	}
	
	@Override
	public boolean updateProjectAttachment(ProjectAttachment projectAttachment)
			throws DAOException {
		documentDAO().modifyDocument(projectAttachment.getAttachment());
		return projectTrackingDAO().updateProjectTrackingAttachment(
				projectAttachment);
	}
	
	@Override
	public void deleteProjectAttachment(ProjectAttachment projectAttachment)
			throws DAOException {
		projectAttachment.getAttachment().setTemporary(true);
		documentDAO().modifyDocument(projectAttachment.getAttachment());
		projectTrackingDAO().deleteProjectTrackingAttachment(
				projectAttachment.getAttachment().getDocumentID());
	}
	
	private Contract createContract(Contract contract) throws DAOException {
		return projectTrackingDAO().createContract(contract);
	}

	private boolean updateContract(Contract contract) throws DAOException {
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
		Integer projectId = contract.getProjectId();
		
		//update accountant contacts
		projectTrackingDAO.deleteContractAccountantXrefs(projectId);
		projectTrackingDAO.createContractAccountantXrefs(projectId,
				Stars2Object.fromStar2IntObject(contract
						.getAccountantUserIds()));
		
		return projectTrackingDAO.updateContract(contract);
	}

	private void deleteContract(Integer projectId) throws DAOException {
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();
		projectTrackingDAO.deleteContractAccountantXrefs(projectId);
		projectTrackingDAO.deleteBudgetByProjectId(projectId);
		projectTrackingDAO.deleteContract(projectId);
	}
	
	private Contract retrieveContractData(Contract contract)
			throws DAOException {
		ProjectTrackingDAO projectTrackingDAO = projectTrackingDAO();

		Contract ret = contract;
		
		if (null != ret) {
			Integer projectId = contract.getProjectId();
			// accountant contacts
			ret.setAccountantUserIds(Stars2Object
					.toStar2IntObject(projectTrackingDAO
							.retrieveContractAccountantUserIds(projectId)));
			
			// budget
			ret.setBudgetList(retrieveBudgetByProjectId(projectId));
		}

		return ret;
	}

	@Override
	public Budget createBudget(Budget budget) throws DAOException {
		return projectTrackingDAO().createBudget(budget);
	}

	@Override
	public boolean updateBudget(Budget budget) throws DAOException {
		return projectTrackingDAO().updateBudget(budget);
	}

	@Override
	public Budget retrieveBudget(Integer budgetId) throws DAOException {
		return projectTrackingDAO().retrieveBudget(budgetId);
	}

	@Override
	public List<Budget> retrieveBudgetByProjectId(Integer projectId)
			throws DAOException {
		return projectTrackingDAO().retrieveBudgetByProjectId(projectId);
	}

	@Override
	public void deleteBudget(Integer budgetId) throws DAOException {
		projectTrackingDAO().deleteBudget(budgetId);
	}
}

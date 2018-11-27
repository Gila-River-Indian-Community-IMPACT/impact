package us.wy.state.deq.impact.bo;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.DocumentException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.BaseBO;
import us.oh.state.epa.stars2.bo.helpers.InfrastructureHelper;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.contact.ContactNote;
import us.oh.state.epa.stars2.database.dbObjects.contact.FacilityContactsDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.pdf.contact.FacilityContactsPdfGenerator;
import us.oh.state.epa.stars2.webcommon.pdf.project.ProjectTrackingPdfGenerator;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;
import us.wy.state.deq.impact.database.dao.ContactDAO;
import us.wy.state.deq.impact.database.dao.EnviteDAO;
import us.wy.state.deq.impact.database.dao.envite.EnviteRestDAO;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Project;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectDocument;

@Transactional(rollbackFor=Exception.class)
@Service
public class ContactBO extends BaseBO implements ContactService {

	@Autowired
	InfrastructureHelper infraHelper;
	private ContactDAO contactDao;
	private InfrastructureDAO infraDao;

	/**
	 * Constructor that allows DAO initialization override. Caller is
	 * responsible for any necessary DAO initialization.
	 * 
	 * @param contactDao
	 * @param infraDao
	 */
	public ContactBO(ContactDAO contactDao, InfrastructureDAO infraDao) {
		this();
		this.contactDao = contactDao;
		this.infraDao = infraDao;
	}

	public ContactBO() {
		super();
	}

	@Override
	public ValidationMessage[] validateCreateContact(Contact contact)
			throws DAOException {
		ValidationMessage[] validationMessages;

		if (contact == null) {
			DAOException e = new DAOException("invalid contact");
			throw e;
		}

		validationMessages = contact.validate();

		return validationMessages;
	}

	@Override
	public ValidationMessage[] validateContact(Contact contact)
			throws DAOException {
		ValidationMessage[] validationMessages;

		if (contact == null) {
			DAOException e = new DAOException("invalid contact");
			throw e;
		}

		validationMessages = contact.validate();

		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		valMessages.addAll(Arrays.asList(validationMessages));

		if (contact.getExternalUser() != null) {
			Contact externalContact = contactDAO()
					.retrieveContactByExternalUsername(
							contact.getExternalUser().getUserName());
			if (externalContact != null) {
				if (contact.getContactId() == null
						|| !contact.getContactId().equals(
								externalContact.getContactId())) {
					valMessages
							.add(new ValidationMessage(
									"portalUsername",
									"Contact ID="
											+ externalContact.getCntId()
											+ " has already been assigned CROMERR Username: "
											+ externalContact.getExternalUser().getUserName()
											+ ". You cannot assign the same CROMERR Username to more than one Contact.",
									ValidationMessage.Severity.ERROR, null));
				}
			}
		}

		validationMessages = valMessages.toArray(new ValidationMessage[0]);

		return validationMessages;
	}

	@Override
	public ValidationMessage[] validateContactPortalDetail(Contact contact)
			throws DAOException {
		ValidationMessage[] validationMessages;

		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();

		if (contact.getExternalUser() != null) {
			String userName=contact.getExternalUser().getUserName();
			if(userName.isEmpty()){
				contact.getExternalUser().setUserName(null);
			}
			Contact externalContact = contactDAO()
					.retrieveContactByExternalUsername(
							contact.getExternalUser().getUserName());
			if (externalContact != null) {
				if (contact.getContactId() == null
						|| !contact.getContactId().equals(
								externalContact.getContactId())) {
					valMessages
							.add(new ValidationMessage(
									"portalUsername",
									"Contact ID="
											+ externalContact.getCntId()
											+ " has already been assigned CROMERR Username: "
											+ externalContact.getExternalUser().getUserName()
											+ ". You cannot assign the same CROMERR Username to more than one Contact.",
									ValidationMessage.Severity.ERROR, null));
				}
			}
		}

		validationMessages = valMessages.toArray(new ValidationMessage[0]);

		return validationMessages;
	}

	@Override
	public Contact createContact(Contact contact) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		initDaos(trans);

		try {
			if (contact.getAddress() != null) {
				Address newAddress = infraDao.createAddress(contact
						.getAddress());
				contact.setAddress(newAddress);
			}
			contact = contactDao.createContact(contact);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("contactId=" + contact.getContactId(), trans, e);
		} finally {
			closeTransaction(trans);
		}

		logger.debug("contact with contact Id = " + contact.getContactId()
				+ " created.");

		return contact;
	}

	@Override
	public Contact createContactForStaging(Contact contact) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		initDaos(trans);

		try {
			if (contact.getAddress() != null) {
				Address newAddress = infraDao.createAddress(contact
						.getAddress());
				contact.setAddress(newAddress);
			}
			contact = contactDao.createStagingContact(contact);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("contactId=" + contact.getContactId(), trans, e);
		} finally {
			closeTransaction(trans);
		}

		logger.debug("contact with contact Id = " + contact.getContactId()
				+ " created.");

		return contact;
	}

	private void initDaos(Transaction trans) throws DAOException {
		if (null == contactDao && null == infraDao) {
			contactDao = contactDAO(trans);
			infraDao = infrastructureDAO(trans);
		}
	}

	@Override
	public Contact[] searchContacts(Map<String, String> params,
			boolean unlimitedSearch) throws DAOException {
		return contactDAO().searchContacts(params, unlimitedSearch);
	}

	@Override
	public Contact[] searchForDuplicateContacts(Map<String, String> params,
			boolean unlimitedSearch, Contact baseContact) throws DAOException {
		if (params == null) {
			params = new HashMap<String, String>();
			if (baseContact != null) {
				params.put("firstName",
						prepareAttribute(baseContact.getFirstNm()));
				params.put("lastName",
						prepareAttribute(baseContact.getLastNm()));
			} else {
				return null;
			}
		}

		Contact[] contacts = contactDAO().searchForDuplicateContacts(params,
				unlimitedSearch);
		List<Contact> contactList = new ArrayList<Contact>();

		if (contacts == null || contacts.length == 0) {
			return contactList.toArray(new Contact[0]);
		}

		for (Contact contact : contacts) {
			if (contact.getContactId().equals(baseContact.getContactId())) {
				continue;
			} else {
				contactList.add(contact);
			}
		}

		return contactList.toArray(new Contact[0]);
	}

	private String prepareAttribute(String attribute) {
		String newAttribute = attribute;
		if (newAttribute == null) {
			return newAttribute;
		}

		newAttribute = newAttribute.trim();

		if (newAttribute.length() > 1 && newAttribute.contains(" ")) {
			newAttribute = newAttribute.substring(0, newAttribute.indexOf(" "));
		}

		newAttribute = "%" + newAttribute + "%";

		return newAttribute;
	}

	@Override
	public boolean modifyContact(Contact contact) throws DAOException {
		boolean operationOk = false;
		Transaction trans = TransactionFactory.createTransaction();
		initDaos(trans);

		try {
			if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
				// place contact in staging
				if (retrieveStagingContactDetail(contact.getContactId(),
						contact.getFacilityId(), true) == null) {
					contact.setAddressId(null);
					createStagingFacilityContact(contact.getFacilityId(),
							contact, trans);
				}
			}
			if (contact.getAddress() != null) {
				infraDao.modifyAddress(contact.getAddress());
			}
			if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
				operationOk = contactDao.modifyStagingContact(contact);
			} else {
				operationOk = contactDao.modifyContact(contact);
			}

			trans.complete();
		} catch (DAOException e) {
			operationOk = false;
			cancelTransaction("contactId = " + contact.getContactId(), trans, e);
		} finally {
			operationOk = true;
			closeTransaction(trans);
		}

		return operationOk;
	}

	/**
	 * Creates a facility contact in the staging area.
	 * 
	 * @param facilityId
	 * @param contact
	 * @param trans
	 * @throws DAOException
	 */
	public void createStagingFacilityContact(String facilityId,
			Contact contact, Transaction trans) throws DAOException {
		try {
			logger.debug("Creating Contact in Staging...");
			contact.setFacilityId(facilityId);
			contact.getAddress().setAddressId(null);
			infraHelper.createContact(contact, trans);

		} catch (DAOException e) {
			throw e;
		}
	}

	@Override
	public Contact retrieveContactDetail(Integer contactId) throws DAOException {
		return retrieveContactDetail(contactId, null);
	}
	
	@Override
	public Contact retrieveContactDetail(String cntId) throws DAOException {
		return retrieveContactDetail(cntId, null);
	}

	@Override
	public Contact retrieveContactDetail(Integer contactId, boolean staging)
			throws DAOException {
		return retrieveContactDetail(contactId, staging, null);
	}

	@Override
	public Contact retrieveStagingContactDetail(Integer contactId,
			String facilityId, boolean staging) throws DAOException {
		Contact ret = null;
		ContactDAO contactDAO = getContactDAO(staging);
		if (contactId != null) {
			try {
				ret = contactDAO.retrieveStagingContact(contactId, facilityId);

			} catch (DAOException de) {
				logger.error("retrieve contact detail failed; contactId = ["
						+ contactId + "]:" + de.getMessage(), de);
				throw de;
			}
		}
		return ret;
	}

	public Contact retrieveContactDetail(Integer contactId, Transaction trans)
			throws DAOException {
		Contact ret = null;

		ContactDAO contactDAO = contactDAO();
		if (trans != null) {
			contactDAO = contactDAO(trans);
		}

		if (contactId != null) {
			try {
				ret = retrieveContact(contactDAO, contactId);

			} catch (DAOException de) {
				logger.error("retrieve contact detail failed; contactId = ["
						+ contactId + "]:" + de.getMessage(), de);
				throw de;
			}
		}
		return ret;
	}
	
	public Contact retrieveContactDetail(String cntId, Transaction trans)
			throws DAOException {
		Contact ret = null;

		ContactDAO contactDAO = contactDAO();
		if (trans != null) {
			contactDAO = contactDAO(trans);
		}

		if (cntId != null) {
			try {
				ret = retrieveContact(contactDAO, cntId);

			} catch (DAOException de) {
				logger.error("retrieve contact detail failed; contactId = ["
						+ cntId + "]:" + de.getMessage(), de);
				throw de;
			}
		}
		return ret;
	}

	public Contact retrieveContactDetail(Integer contactId, boolean staging,
			Transaction trans) throws DAOException {
		Contact ret = null;

		ContactDAO contactDAO = getContactDAO(staging);
		if (trans != null) {
			contactDAO.setTransaction(trans);
		}

		if (contactId != null) {
			try {
				ret = retrieveContact(contactDAO, contactId);

			} catch (DAOException de) {
				logger.error("retrieve contact detail failed; contactId = ["
						+ contactId + "]:" + de.getMessage(), de);
				throw de;
			}
		}
		return ret;
	}

	private Contact retrieveContact(ContactDAO contactDAO, int contactId)
			throws DAOException {
		// TODO Auto-generated method stub
		return contactDAO.retrieveContact(contactId);
	}
	
	private Contact retrieveContact(ContactDAO contactDAO, String cntId)
			throws DAOException {
		// TODO Auto-generated method stub
		return contactDAO.retrieveContact(cntId);
	}

	@Override
	public ContactNote createContactNote(ContactNote contactNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ContactNote ret = null;

		try {
			ret = createContactNote(contactNote, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	private ContactNote createContactNote(ContactNote contactNote,
			Transaction trans) throws DAOException {
		ContactNote ret = null;
		initDaos(trans);

		try {
			Note tempNote = infraDao.createNote(contactNote);

			if (tempNote != null) {
				ret = contactNote;
				ret.setNoteId(tempNote.getNoteId());

				contactDao.addContactNote(ret.getContactId(), ret.getNoteId());
			} else {
				logger.error("Failed to insert contact Note for "
						+ contactNote.getContactId());
				throw new DAOException("Failed to insert contact Note for "
						+ contactNote.getContactId());
			}
		} catch (DAOException e) {
			throw e;
		}

		return ret;
	}

	@Override
	public boolean modifyContactNote(ContactNote contactNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		boolean ret = false;

		try {
			ret = infraDAO.modifyNote(contactNote);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;

	}

	@Override
	public ContactNote[] retrieveContactNotes(Integer contactId) {
		ContactNote[] ret = null;

		try {
			ret = contactDAO().retrieveContactNotes(contactId);
		} catch (DAOException de) {
			logger.error("retrieve contact notes failed for " + contactId
					+ ". " + de.getMessage(), de);
		}

		return ret;
	}

	@Override
	public void deleteContact(Contact contact) throws DAOException {
		contactDAO().deleteContact(contact);
	}

	@Override
	public void deleteContactType(ContactType[] activeContactTypes,
			Timestamp endDate) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();

		ContactDAO contactDAO = contactDAO(trans);

		try {
			for (ContactType cType : activeContactTypes) {
				ContactType newContactType = new ContactType(cType);
				newContactType.setEndDate(endDate);
				newContactType.setFacilityId(cType.getFacilityId());

				contactDAO.deleteContactType(cType);
				contactDAO.addContactType(cType.getContactId(), newContactType);

				trans.complete();
			}
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
			logger.error(re);
		} finally {
			closeTransaction(trans);
		}

	}

//	private ContactDAO getContactDAO(boolean staging) throws DAOException {
//		String schema = null;
//		if (!CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
//			if (staging) {
//				schema = "Staging";
//			} else {
//				schema = "ReadOnly";
//			}
//		}
//
//		return contactDAO(schema);
//	}

	public ExternalOrganization[] retrieveExternalOrgs(Contact contact,
			FacilityList[] excludedFacilities) throws DAOException {
		EnviteDAO enviteDao = new EnviteRestDAO();
		ExternalOrganization[] externalOrgs = enviteDao.retrieveEnviteOrgs(contact
				.getExternalUser().getUserName());
		Company[] authorizedCompanies = companyDAO()
				.retrieveAuthorizedCompanies(contact.getExternalUser().getUserName());
		List<Integer> authorizedCompanyIds = new ArrayList<Integer>();
		for (Company company : authorizedCompanies) {
			authorizedCompanyIds.add(company.getCompanyId());
		}

		
		
		//TODO redoing this
		
		for (ExternalOrganization externalOrg : externalOrgs) {
			Company company = companyDAO().retrieveCompanyByExternalCompanyId(
					externalOrg.getOrganizationId());

			externalOrg.setCompany(company);

			//TODO set the information about facilities and authorization on
			// the ContactRole instead of ExternalOrganization
			
//			if (null != company) {
//				int totalExcludedFacilities = 0;
//				if (excludedFacilities != null && excludedFacilities.length > 0) {
//					for (FacilityList excludedFacility : excludedFacilities) {
//						if (excludedFacility.getCmpId().equals(
//								company.getCmpId())) {
//							totalExcludedFacilities++;
//						}
//					}
//				}
//
//				FacilityList[] companyFacilities = company.getFacilities();
//				int totalFacilitiesOwned = 0;
//				if (companyFacilities != null && companyFacilities.length > 0) {
//					for (FacilityList ownedFacility : companyFacilities) {
//						if (ownedFacility.getEndDate() == null) {
//							totalFacilitiesOwned++;
//						}
//					}
//				}
//				externalOrg.setTotalFacilitiesOwned(totalFacilitiesOwned); //TODO
//				externalOrg.setTotalExcludedFacilities(totalExcludedFacilities); //TODO
				
//				externalOrg.setContactAuthorized(authorizedCompanyIds
//						.contains(company.getCompanyId()));
//			}

		}
		return externalOrgs;
	}

	@Override
	public void savePortalDetail(Contact contact, ContactRole[] contactRoles)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ContactDAO contactDao = contactDAO(trans);
		try {
			contactDao.modifyExternalUsername(contact);
			contactDao.removeCompanyAuth(contact);
			for (ContactRole contactRole : contactRoles) {
				if (contactRole.isActive()) {
					contactDao.addCompanyAuth(contact, contactRole.getCompany());
				}
			}
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	@Override
	public FacilityList[] retrieveExcludedFacilities(Contact contact) {
		FacilityList[] excludedFacilities = null;

		if (contact == null) {
			return null;
		}

		try {
			excludedFacilities = contactDAO().retrieveExcludedFacilities(
					contact);
		} catch (DAOException de) {
			logger.error(
					"Couldn't retrieve excluded facilities for "
							+ contact.getCntId() + ". " + de.getMessage(), de);
		}

		return excludedFacilities;
	}

	@Override
	public void modifyFacilityExclusionList(Contact contact,
			FacilityList[] excludedFacilities,
			FacilityList[] authorizedFacilities) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ContactDAO contactDao = contactDAO(trans);
		try {
			for (FacilityList authorizedFacility : authorizedFacilities) {
				contactDao.removeFacilityFromFacilityNoAuth(contact,
						authorizedFacility);
			}

			// first remove excluded facilities
			for (FacilityList excludedFacility : excludedFacilities) {
				contactDao.removeFacilityFromFacilityNoAuth(contact,
						excludedFacility);
			}

			for (FacilityList excludedFacility : excludedFacilities) {
				contactDao.addFacilityNoAuth(contact, excludedFacility);
			}
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	@Override
	public void mergeContact(Contact baseContact,
			Contact[] selectedTargetContacts) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ContactDAO contactDao = contactDAO(trans);

		try {
			ContactNote baseContactNote = new ContactNote();

			baseContactNote.setContactId(baseContact.getContactId());
			baseContactNote.setUserId(InfrastructureDefs.getCurrentUserId());
			baseContactNote.setNoteTypeCd(NoteType.DAPC);
			baseContactNote.setDateEntered(new Timestamp(System
					.currentTimeMillis()));

			StringBuffer baseNoteTxt = new StringBuffer();

			baseNoteTxt
					.append("This contact ["
							+ baseContact.getCntId()
							+ "] has facility, permit application, and note associations merged from the following contacts: \n");

			// copy unique contact types
			List<ContactType> copyContactTypes = new ArrayList<ContactType>();
			copyContactTypes.addAll(baseContact.getContactTypes());
			for (Contact targetContact : selectedTargetContacts) {
				List<ContactType> targetContactTypes = new ArrayList<ContactType>();
				targetContactTypes.addAll(targetContact.getContactTypes());
				for (ContactType mergeContactType : targetContactTypes) {

					if (copyContactTypes.isEmpty()) {
						copyContactTypes.addAll(targetContactTypes);

						continue;
					}

					boolean isDuplicateContactType = false;
					for (ContactType baseContactType : copyContactTypes) {
						if (mergeContactType.getContactTypeCd().equals(
								baseContactType.getContactTypeCd())
								&& mergeContactType.getFacilityId().equals(
										baseContactType.getFacilityId())) {
							isDuplicateContactType = true;
						}
					}

					if (!isDuplicateContactType) {
						copyContactTypes.add(mergeContactType);
					}
				}
			}

			for (Contact targetContact : selectedTargetContacts) {
				// remove target contact's contact types
				contactDao.removeContactTypes(targetContact.getContactId());

				// modify notes to make them belong to base contact
				contactDao.modifyContactNotesAffiliation(targetContact,
						baseContact);

				// move any permit applications to base contact
				contactDao.modifyPermitApplicationsAffilation(targetContact,
						baseContact);

				// make inactive
				targetContact.setActive(false);
				contactDao.modifyContact(targetContact);

				// add to base contact note record
				baseNoteTxt.append("\t" + targetContact.getCntId() + "\n");

			}

			// add new contact types to base contact
			contactDao.removeContactTypes(baseContact.getContactId());
			for (ContactType baseContactType : copyContactTypes) {
				contactDao.addContactType(baseContact.getContactId(),
						baseContactType);
			}

			// add note to base contact
			baseContactNote.setNoteTxt(baseNoteTxt.toString());
			createContactNote(baseContactNote, trans);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

	}
	
	@Override
	public Contact retrieveContactByExternalUsername(String externalUsername)
			throws DAOException {

		Contact externalContact = null;
		if (externalUsername != null) {
			externalContact = contactDAO()
					.retrieveContactByExternalUsername(
							externalUsername);
			
		}

		return externalContact;
	}

	@Override
	public FacilityContactsDocument createFacilityContactsPDFDocument(
			Facility facility, String userName) throws DAOException {
		FacilityContactsDocument doc = null;
		try {
			doc = getFacilityContactsDocument(facility);
			OutputStream os = DocumentUtil.createDocumentStream(doc.getPath());
			try {
				FacilityContactsPdfGenerator generator = new FacilityContactsPdfGenerator();
				generator.setUserName(userName);
//				generator.setAttestationAttached(userName != null
//						&& facilityHasAttestationDocument(facility));
				generator.generatePdf(facility, os, null);
			} catch (DocumentException e) {
				throw new IOException(
						"Exception writing faciltiy contacts to stream for "
								+ facility.getFacilityId());
			} finally {
				if (os != null) {
					os.close();
				}
			}
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting faciltiy contacts as stream for "
							+ facility.getFacilityId(), e);
		}
		return doc;
	}
	
	@Override
	public FacilityContactsDocument getFacilityContactsDocument(
			Facility facility) {
		FacilityContactsDocument doc = new FacilityContactsDocument();
		// Set the path elements of the temp doc.
		doc.setDescription("Submitted facility contacts pdf file");
		doc.setFacilityID(facility.getFacilityId());
		doc.setTemporary(true);
		doc.setOverloadFileName("FacilityContacts" + facility.getFpId()
				+ ".pdf");
		return doc;
	}
	
	@Override
	public List<Document> getPrintableDocumentList(Facility facility, String user) 
			throws DAOException {
		
		List<Document> docList = new ArrayList<Document>();

		TmpDocument tempContactsDoc = new TmpDocument();
		generateTempContacts(facility, user, tempContactsDoc);
		docList.add(tempContactsDoc);
		
		return docList;
	}

	private boolean generateTempContacts(Facility facility, String user, 
			TmpDocument tempContactsDoc) throws DAOException {
		boolean rtn = false;
		try {
			// Set the path elements of the temp doc.
			tempContactsDoc.setDescription("Facility Contacts");
//			rptDoc.setFacilityID(facility.getFacilityId()); //TODO is facility optional??
			tempContactsDoc.setTemporary(true);
			tempContactsDoc.setTmpFileName("facilityContacts_" + facility.getFacilityId()
					+ ".pdf");
			// the items below are not needed since this document data is not
			// stored in the database
			// appDoc.setLastModifiedBy();
			// appDoc.setLastModifiedTS(new
			// Timestamp(System.currentTimeMillis()));
			// appDoc.setUploadDate(appDoc.getLastModifiedTS());
			DocumentUtil.mkDir(tempContactsDoc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(tempContactsDoc
					.getPath());
			rtn = writeFacilityContactsToStream(facility, os, tempContactsDoc);
			os.close();

		} catch (Exception ex) {
			logger.error("Cannot generate facility contacts pdf ", ex);
			throw new DAOException("Cannot generate project tracking detail", ex);
		}
		return rtn;
	}


	private boolean writeFacilityContactsToStream(Facility facility, 
			OutputStream os, TmpDocument doc) throws IOException {
		boolean rtn = false;
		try {
			// make sure we have all Facility information
			FacilityContactsPdfGenerator generator = new FacilityContactsPdfGenerator();
			generator.generatePdf(facility, os, facility.getFacilityId());
			// rtn = generator.isHasTS();
			// if (rtn)
			// PdfGeneratorBase.addTradeSecretWatermarkHorizontal(doc
			// .getPath());
		} catch (DocumentException e) {
			logger.error(
					"Exception writing facility contacts to stream for stack test "
							+ facility.getFacilityId(), e);
			throw new IOException(
					"Exception writing facility contacts to stream for report "
							+ facility.getFacilityId());
		}
		return rtn;
	}


}

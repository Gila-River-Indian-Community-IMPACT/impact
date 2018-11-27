package us.wy.state.deq.impact.bo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.contact.ContactNote;
import us.oh.state.epa.stars2.database.dbObjects.contact.FacilityContactsDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;

public interface ContactService {

	ValidationMessage[] validateCreateContact(Contact contact)
			throws DAOException;

	Contact createContact(Contact contact) throws DAOException;

	Contact createContactForStaging(Contact contact) throws DAOException;

	Contact[] searchContacts(Map<String, String> params, boolean unlimitedSearch)
			throws DAOException;

	boolean modifyContact(Contact contact) throws DAOException;

	Contact retrieveContactDetail(Integer contactId) throws DAOException;
	
	Contact retrieveContactDetail(String cntId) throws DAOException;

	ContactNote createContactNote(ContactNote contactNote) throws DAOException;

	boolean modifyContactNote(ContactNote contactNote) throws DAOException;

	ContactNote[] retrieveContactNotes(Integer contactId);

	void deleteContact(Contact contact) throws DAOException;

	void deleteContactType(ContactType[] activeContactTypes, Timestamp endDate)
			throws DAOException;

	Contact retrieveContactDetail(Integer contactId, boolean staging)
			throws DAOException;

	Contact retrieveStagingContactDetail(Integer contactId, String facilityId,
			boolean staging) throws DAOException;

	ExternalOrganization[] retrieveExternalOrgs(Contact contact,
			FacilityList[] excludedFacilities) throws DAOException;

	ValidationMessage[] validateContact(Contact contact) throws DAOException;

	void savePortalDetail(Contact contact, ContactRole[] contactRoles)
			throws DAOException;

	FacilityList[] retrieveExcludedFacilities(Contact contact);

	ValidationMessage[] validateContactPortalDetail(Contact contact)
			throws DAOException;

	void modifyFacilityExclusionList(Contact contact,
			FacilityList[] excludedFacilities,
			FacilityList[] authorizedFacilities) throws DAOException;

	/**
	 * Returns an array of suspected duplicate contacts given certain parameters
	 * and the base contact. The base contact will not be included in the
	 * results.
	 * 
	 * @param params
	 * @param unlimitedSearch
	 * @param baseContact
	 * @return
	 * @throws DAOException
	 */
	Contact[] searchForDuplicateContacts(Map<String, String> params,
			boolean unlimitedSearch, Contact baseContact) throws DAOException;

	/**
	 * Transfers all contact types and notes from the selected target contacts
	 * to the given base contact. Any duplicate contact type (same type, same
	 * facility) will be thrown out. All selected target contacts will be marked
	 * inactive and should have the capability to be deleted.
	 * 
	 * @param baseContact
	 * @param selectedTargetContacts
	 * @throws DAOException
	 */
	void mergeContact(Contact baseContact, Contact[] selectedTargetContacts)
			throws DAOException;
	
	Contact retrieveContactByExternalUsername(String externalUsername)
			throws DAOException;

	List<Document> getPrintableDocumentList(Facility facility, String user) throws DAOException;

	FacilityContactsDocument getFacilityContactsDocument(Facility facility);

	FacilityContactsDocument createFacilityContactsPDFDocument(Facility facility, String userName) throws DAOException;

}

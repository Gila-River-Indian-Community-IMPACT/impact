package us.wy.state.deq.impact.database.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import us.oh.state.epa.stars2.database.dao.TransactableDAO;
import us.oh.state.epa.stars2.database.dbObjects.contact.ContactNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

public interface ContactDAO extends TransactableDAO {

	ContactNote[] retrieveContactNotes(Integer contactId) throws DAOException;

	void addContactNote(Integer companyId, Integer noteId) throws DAOException;

	/**
	 * Retrieves the contact for the given contactId.
	 * 
	 * @param contactId
	 * @return
	 * @throws DAOException
	 */
	Contact retrieveContact(int contactId) throws DAOException;
	
	/**
	 * Retrieves the contact for the given contactId.
	 * 
	 * @param cntId
	 * @return
	 * @throws DAOException
	 */
	Contact retrieveContact(String cntId) throws DAOException;

	/**
	 * Creates the given Contact
	 * 
	 * @param contact
	 *            contact to create
	 * @return Contact contact create, complete with contactId
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Contact createContact(Contact contact) throws DAOException;

	/**
	 * Creates the given Contact
	 * 
	 * @param contact
	 *            contact to create
	 * @return Contact contact create, complete with contactId
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	Contact createStagingContact(Contact contact) throws DAOException;

	/**
	 * Modifies the given Contact
	 * 
	 * @param contact
	 *            contact to create
	 * @return boolean
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyContact(Contact contact) throws DAOException;

	/**
	 * Modifies the given Contact
	 * 
	 * @param contact
	 *            contact to create
	 * @return boolean
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	boolean modifyStagingContact(Contact contact) throws DAOException;

	/**
	 * Deletes the given Contact
	 * 
	 * @param contact
	 *            contact to delete
	 * @return void
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void deleteContact(Contact c) throws DAOException;

	/**
	 * Removes the given Contact
	 * 
	 * @param contactId
	 *            contactId of contact to delete
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void removeContact(Integer contactId) throws DAOException;

	/**
	 * @param userId
	 * @return Integer
	 * @throws DAOException
	 */
	Contact retrieveContactByUserId(Integer userId) throws DAOException;

	// TODO don't need this?
	Integer retrieveLastContactId();

	Contact[] searchContacts(Map<String, String> params, boolean unlimitedSearch)
			throws DAOException;

	/**
	 * Returns a list of all contacts within IMPACT.
	 * 
	 * @return
	 * @throws DAOException
	 */
	List<Contact> retrieveGlobalContacts() throws DAOException;

	/**
	 * Adds a contactType to the given contactId.
	 * 
	 * @param contactId
	 * @param contactTypeCd
	 * @param startDate
	 * @param facilityId
	 * @throws us.oh.state.epa.stars2.framework.exception.DAOException
	 */
	void addContactType(Integer contactId, String contactTypeCd,
			Timestamp startDate, String facilityId) throws DAOException;

	/**
	 * Adds a contactType given another contact type.
	 * 
	 * @param contactId
	 * @param contactType
	 * @throws DAOException
	 */
	void addContactType(Integer contactId, ContactType contactType)
			throws DAOException;

	/**
	 * deletes the contactType for the contact supplied in the contactType
	 * object.
	 * 
	 * @param contactType
	 * @throws DAOException
	 */
	void deleteContactType(ContactType contactType) throws DAOException;

	ContactType[] retrieveContactTypes(Integer contactId) throws DAOException;

	List<Contact> retrieveStagedContactsByFacility(String facilityId)
			throws DAOException;

	/**
	 * Retrieves the contact for the given contactId.
	 * 
	 * @param contactId
	 * @return
	 * @throws DAOException
	 */
	Contact retrieveStagingContact(int contactId, String faciltiyId)
			throws DAOException;

	ContactType[] retrieveStagingContactTypes(Integer contactId,
			String facilityId) throws DAOException;

	/**
	 * Removes all contact types associated with the given contact id and
	 * facility id.
	 * 
	 * @param contactId
	 * @param facilityId
	 * @throws DAOException
	 */
	void removeContactTypes(Integer contactId, String facilityId)
			throws DAOException;

	/**
	 * Removes all contact types associated with the given contact id
	 * 
	 * @param contactId
	 * @param facilityId
	 * @throws DAOException
	 */
	void removeContactTypes(Integer contactId) throws DAOException;

	Contact retrieveContactByExternalUsername(String externalUsername)
			throws DAOException;

	/**
	 * Remove authorization for all companies for the given contact.
	 * 
	 * @param contact
	 * @throws DAOException
	 */
	void removeCompanyAuth(Contact contact) throws DAOException;

	/**
	 * Add authorization for the given company for the given contact.
	 * 
	 * @param contact
	 * @param company
	 * @throws DAOException
	 */
	void addCompanyAuth(Contact contact, Company company) throws DAOException;

	/**
	 * Retrieves the facility exclusion list for the given contact.
	 * 
	 */
	FacilityList[] retrieveExcludedFacilities(Contact contact)
			throws DAOException;

	/**
	 * Adds a newly excluded facility to the facility exclusion list for the
	 * given facility and for the given contact.
	 * 
	 * @param contact
	 * @param excludedFacility
	 * @throws DAOException
	 */
	void addFacilityNoAuth(Contact contact, FacilityList excludedFacility)
			throws DAOException;

	/**
	 * Removes a newly authorized facility from the facility exclusion list for
	 * the given facility and for the given contact.
	 * 
	 * @param contact
	 * @param authorizedFacility
	 * @throws DAOException
	 */
	void removeFacilityFromFacilityNoAuth(Contact contact,
			FacilityList authorizedFacility) throws DAOException;

	void modifyExternalUsername(Contact contact) throws DAOException;

	Contact[] searchForDuplicateContacts(Map<String, String> params,
			boolean unlimitedSearch) throws DAOException;

	/**
	 * Changes the affiliation of all notes from one contact (oldContact) to
	 * another contact (newContact).
	 * 
	 * @param oldContact
	 *            The contact that originally has the notes
	 * @param newContact
	 *            The new contact that will be given the notes
	 * @return
	 * @throws DAOException
	 */
	boolean modifyContactNotesAffiliation(Contact oldContact, Contact newContact)
			throws DAOException;

	/**
	 * Changes the affiliation of all permit applications from one contact to
	 * another.
	 * 
	 * @param oldContact
	 *            The contact that originally associated to permit application
	 * @param newContact
	 *            The contact that will given permit application associations
	 * @return
	 * @throws DAOException
	 */
	boolean modifyPermitApplicationsAffilation(Contact oldContact,
			Contact newContact) throws DAOException;

}

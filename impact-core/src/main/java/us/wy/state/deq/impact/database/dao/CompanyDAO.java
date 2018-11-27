package us.wy.state.deq.impact.database.dao;

import java.util.Map;

import us.oh.state.epa.stars2.app.reports.OffsetSummaryReport;
import us.oh.state.epa.stars2.database.dao.DataAccessObject;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyEmissionsOffsetRow;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyNote;
import us.wy.state.deq.impact.database.dbObjects.company.EmissionsOffsetAdjustment;
import us.wy.state.deq.impact.database.dbObjects.company.OffsetSummaryLineItem;

public interface CompanyDAO extends DataAccessObject {
	/**
	 * Adds company to database -- assigns company id value.
	 * 
	 * @param company
	 * @return company object with added database properties
	 * @throws DAOException
	 */
	public abstract Company createCompany(Company company) throws DAOException;

	public abstract Company[] searchCompanies(Map<String, String> params,
			boolean unlimitedResults) throws DAOException;

	/**
	 * Adds company's to database.
	 * 
	 * @param company
	 * @return company object with added database properties
	 * @throws DAOException
	 */
	public abstract void addCompanyAddress(Integer cpId, Integer addressId)
			throws DAOException;

	/**
	 * Retrieves a company by its given company id.
	 * 
	 * @param companyId
	 * @return
	 * @throws DAOException
	 */
	Company retrieveCompany(Integer companyId) throws DAOException;

	public abstract boolean modifyCompany(Company company) throws DAOException;

	public abstract CompanyNote[] retrieveCompanyNotes(String cmpId)
			throws DAOException;

	public abstract void addCompanyNote(Integer companyId, String cmpId,
			Integer noteId) throws DAOException;

	public abstract Company retrieveCompany(String cmpId) throws DAOException;

	public abstract String retrieveCmpId(Integer companyId) throws DAOException;

	public abstract Company[] retrieveAllCompanies() throws DAOException;

	/**
	 * Retrieve a company's owned facilities.
	 * 
	 * @param cmpId
	 * @return
	 * @throws DAOException
	 */
	public abstract FacilityList[] retrieveOwnedFacilities(String cmpId)
			throws DAOException;

	public abstract FacilityList[] retrieveAuthorizedFacilities(String cmpId,
			String externalUsername) throws DAOException;

	/**
	 * Retrieves all of a company's employed contacts (inactive/active)
	 * 
	 * @param companyId
	 * @return
	 * @throws DAOException
	 */
	public abstract Contact[] retrieveCompanyContacts(Integer companyId)
			throws DAOException;

	/**
	 * Retrieves a company by its CROMERR company ID
	 * 
	 * @param externalCompanyId
	 * @return
	 * @throws DAOException
	 */
	public abstract Company retrieveCompanyByExternalCompanyId(
			Long externalCompanyId) throws DAOException;

	/**
	 * Retrieve a given user's authorized companies.
	 * 
	 * @param externalUsername
	 * @return
	 * @throws DAOException
	 */
	public abstract Company[] retrieveAuthorizedCompanies(String externalUsername)
			throws DAOException;

	/**
	 * Retrieve a company by its CROMERR organization id.
	 * 
	 * @param externalId
	 * @return
	 * @throws DAOException
	 */
	Company retrieveCompanyByExternalId(int externalId) throws DAOException;

	Integer retrieveTotalFacilitiesOwned(String cmpId) throws DAOException;

	/**
	 * Validate the company name is duplicate or not.
	 * 
	 * @param company
	 *            ID, company name
	 * @return
	 * @throws DAOException
	 */
	boolean isDuplicateCompanyName(Integer companyId, String companyName)
			throws DAOException;

	void deleteCompany(Company company) throws DAOException;

	/**
	 * Transfers all facilities owned by sourceCompany to destinationCompany.
	 * 
	 * @param sourceCompany
	 *            Company with or without facilities
	 * @param destinationCompany
	 *            Company that will own sourceCompany's facilities
	 * @throws DAOException
	 */
	public void modifyCompanyFacilitiesAffiliation(Company sourceCompany,
			Company destinationCompany) throws DAOException;

	/**
	 * Transfers all contacts employed by sourceCompany to destinationCompany.
	 * 
	 * @param sourceCompany
	 *            Company with or without contacts
	 * @param destinationCompany
	 *            Company that will own sourceCompany's contacts
	 * @throws DAOException
	 */
	public void modifyCompanyContactsAffiliation(Company sourceCompany,
			Company destinationCompany) throws DAOException;

	/**
	 * Transfers all notes created under sourceCompany to destinationCompany.
	 * 
	 * @param sourceCompany
	 *            Company with or without notes
	 * @param destinationCompany
	 *            Company that will contain sourceCompany's notes
	 * @throws DAOException
	 */
	public void modifyCompanyNotesAffiliation(Company sourceCompany,
			Company destinationCompany) throws DAOException;

	/**
	 * Removes all of the company note associations for the given company.
	 * 
	 * @param company
	 *            Company that will have its note affiliations removed.
	 * @throws DAOException
	 */
	void removeCompanyNotes(Company company) throws DAOException;
	
	public CompanyEmissionsOffsetRow[] retrieveEmissionsOffsetsByCompanyId(Integer cmpId) throws DAOException;
	
	EmissionsOffsetAdjustment createEmissionsOffsetAdjustment(EmissionsOffsetAdjustment emissionsOffsetAdjustment) 
			throws DAOException;	
	
	EmissionsOffsetAdjustment retrieveEmissionsOffsetAdjustment(Integer emissionsOffsetAdjustmentId)
			throws DAOException;
	
	EmissionsOffsetAdjustment[] retrieveEmissionsOffsetAdjustmentsByCompanyId(Integer cmpId)
			throws DAOException;
	
	boolean modifyEmissionsOffsetAdjustment(EmissionsOffsetAdjustment emissionsOffsetAdjustment)
			throws DAOException;
	
	void deleteEmissionsOffsetAdjustment(Integer emissionsOffsetAdjustmentId)
			throws DAOException;
	
	void deleteEmissionsOffsetAdjustmentsByCompanyId(Integer cmpId)
			throws DAOException;
	
	OffsetSummaryLineItem[] retrieveOffsetSummaryLineItems(OffsetSummaryReport report) throws DAOException;
	
}
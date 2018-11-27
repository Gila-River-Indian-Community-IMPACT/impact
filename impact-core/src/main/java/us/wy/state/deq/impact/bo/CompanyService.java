package us.wy.state.deq.impact.bo;

import java.util.Map;

import us.oh.state.epa.stars2.app.reports.OffsetSummaryReport;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyEmissionsOffsetRow;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyNote;
import us.wy.state.deq.impact.database.dbObjects.company.EmissionsOffsetAdjustment;
import us.wy.state.deq.impact.database.dbObjects.company.OffsetSummaryLineItem;

public interface CompanyService {

	/**
	 * Validate create company.
	 * 
	 * @param company
	 *            company
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	ValidationMessage[] validateCompany(Company company) throws DAOException;

	/**
	 * Create a new company.
	 * 
	 * @param Company
	 *            Company profile to create
	 * @return Company new company detail complete with all id's for new
	 *         contained objects
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	Company createCompany(Company newCompany) throws DAOException;

	Company[] searchCompanies(Map<String, String> params,
			boolean unlimitedSearch) throws DAOException;

	Company retrieveCompanyProfile(Integer companyId) throws DAOException;

	boolean modifyCompany(Company company) throws DAOException;

	CompanyNote[] retrieveCompanyNotes(String cmpId) throws DAOException;

	CompanyNote createCompanyNote(CompanyNote companyNote) throws DAOException;

	boolean modifyCompanyNote(CompanyNote companyNote) throws DAOException;

	Company retrieveCompanyProfile(String cmpId) throws DAOException;

	Company[] retrieveCompanies() throws DAOException;

	Company retrieveCompany(String cmpId) throws DAOException;

	Company retrieveCompany(Integer companyId) throws DAOException;

	Company retrieveCompanyByExternalCompanyId(Object externalCompanyId,
			boolean staging) throws DAOException;

	FacilityList[] retrieveAuthorizedFacilities(String cmpId,
			String externalUsername);

	ContactRole[] retrieveActiveContactRolesFromSession(String externalUsername);

	Company[] retrieveAuthorizedCompanies(String externalUsername);

	void deleteCompany(Company company) throws DAOException;

	/**
	 * Transfers all of the contact, facility, and note records from
	 * sourceCompany to destinationCompany. Additionally a note is added to
	 * destinationCompany detailing the transfer.
	 * 
	 * @param sourceCompany
	 *            Company that may or may not contain contact, facility, and
	 *            note records
	 * @param destinationCompany
	 *            Company that will contain sourceCompany's contact, facility,
	 *            and note records
	 * @throws DAOException
	 */
	void mergeCompanies(Company sourceCompany, Company destinationCompany)
			throws DAOException;
	
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
	
	PollutantDef[] findPollutantsByNonattainmentArea(String areaCd) throws DAOException;
	
	OffsetSummaryLineItem[] retrieveOffsetSummaryLineItems(OffsetSummaryReport report) throws DAOException;
		
}
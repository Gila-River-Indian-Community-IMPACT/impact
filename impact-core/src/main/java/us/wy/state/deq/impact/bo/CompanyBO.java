package us.wy.state.deq.impact.bo;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.gricdeq.impact.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.reports.OffsetSummaryReport;
import us.oh.state.epa.stars2.bo.BaseBO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;
import us.wy.state.deq.impact.database.dao.CompanyDAO;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyEmissionsOffsetRow;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyNote;
import us.wy.state.deq.impact.database.dbObjects.company.EmissionsOffsetAdjustment;
import us.wy.state.deq.impact.database.dbObjects.company.OffsetSummaryLineItem;

@Transactional(rollbackFor = Exception.class)
@Service
public class CompanyBO extends BaseBO implements CompanyService {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.wy.state.deq.impact.bo.CompanyService#validateCreateCompany(us.wy.
	 * state.deq.impact.database.dbObjects.company.Company)
	 */
	@Override
	public ValidationMessage[] validateCompany(Company company)
			throws DAOException {
		if (company == null) {
			DAOException e = new DAOException("invalid company");
			throw e;
		}

		HashMap<String, ValidationMessage> validationMessages = company
				.validationMessagesForCompany();
		Integer companyId = company.getCompanyId();
		if (company.getName() != null && isDuplicateCompanyName(companyId, company.getName())) {
			validationMessages.put("name", new ValidationMessage("name",
					"The company name already exists in the system",
					ValidationMessage.Severity.ERROR, null));
		}

		ValidationMessage validateExternalCompanyIdMsg = validateExternalCompanyId(company);

		if (validateExternalCompanyIdMsg != null) {
			validationMessages.put(validateExternalCompanyIdMsg.getProperty(),
					validateExternalCompanyIdMsg);
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	private boolean isDuplicateCompanyName(Integer companyId, String companyName) {
		try {
			CompanyDAO companyDAO = companyDAO();

			return companyDAO.isDuplicateCompanyName(companyId, companyName);
		} catch (DAOException e) {
			logger.debug("isDuplicateCompanyName Error: Company ID: "
					+ companyId + " Company Name: " + companyName);

			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.wy.state.deq.impact.bo.CompanyService#createCompany(us.wy.state.deq
	 * .impact.database.dbObjects.company.Company)
	 */
	@Override
	public Company createCompany(Company newCompany) throws DAOException {
		String cmpId = null;

		Transaction trans = TransactionFactory.createTransaction();
		CompanyDAO companyDAO = companyDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		Integer companyId = null;

		try {

			// saves address information
			newCompany.getAddress().setAddressId(null);

			// checks to see if address is even available
			if (newCompany.hasAddress()) {
				Address newAddress = infraDAO.createAddress(newCompany
						.getAddress());

				// give company assigned address id
				newCompany.getAddress().setAddressId(newAddress.getAddressId());
			}

			// create Company with given address id
			Company tempCompany = companyDAO.createCompany(newCompany);

			logger.debug("New Company ID: " + tempCompany.getCompanyId());

			// get company ID
			companyId = tempCompany.getCompanyId();
			tempCompany.setCompanyId(companyId);

			// get cmp Id
			cmpId = companyDAO.retrieveCmpId(companyId);
			tempCompany.setCmpId(cmpId);

			// Field audit log support
			String audLogOrgVal = "Company does not Exist.";
			String audLogNewVal = "Company Created: " + tempCompany.getCmpId();
			int currentUserId = InfrastructureDefs.getCurrentUserId();
			createFieldAudLog(tempCompany, tempCompany.getCmpId(), "cnm",
					audLogOrgVal, audLogNewVal, currentUserId, trans);

			trans.complete();

			newCompany = tempCompany;

		} catch (DAOException e) {
			cancelTransaction("companyId=" + companyId, trans, e);
		} finally {
			// End transaction (clean up)
			closeTransaction(trans);
		}

		logger.debug("Company: CMP" + newCompany.getCompanyId()
				+ " with Company Id = " + newCompany.getCompanyId()
				+ " created.");

		return retrieveCompany(companyDAO, companyId);
	}

	private Company retrieveCompany(CompanyDAO companyDAO, int companyId)
			throws DAOException {
		// TODO Auto-generated method stub
		return companyDAO.retrieveCompany(companyId);
	}

	private Company retrieveCompany(CompanyDAO companyDAO, String cmpId)
			throws DAOException {
		// TODO Auto-generated method stub
		return companyDAO.retrieveCompany(cmpId);
	}

	@Override
	public Company retrieveCompany(String cmpId) throws DAOException {
		CompanyDAO companyDAO = companyDAO();
		return retrieveCompany(companyDAO, cmpId);
	}

	@Override
	public Company retrieveCompany(Integer companyId) throws DAOException {
		CompanyDAO companyDAO = companyDAO();
		return retrieveCompany(companyDAO, companyId);
	}

	@Override
	public Company retrieveCompanyProfile(Integer companyId)
			throws DAOException {
		return retrieveCompanyProfile(companyId, null);
	}

	@Override
	public Company retrieveCompanyProfile(String cmpId) throws DAOException {
		return retrieveCompanyProfile(cmpId, null);
	}

	public Company retrieveCompanyProfile(Integer companyId, Transaction trans)
			throws DAOException {
		Company ret = null;
		CompanyDAO companyDAO = companyDAO();
		if (trans != null) {
			companyDAO = companyDAO(trans);
		}

		if (companyId != null) {
			try {
				ret = retrieveCompany(companyDAO, companyId);

			} catch (DAOException de) {
				logger.error("retrieve company detail failed; companyid = ["
						+ companyId + "]:" + de.getMessage(), de);
				throw de;
			}
		}
		return ret;
	}

	public Company retrieveCompanyProfile(String cmpId, Transaction trans)
			throws DAOException {
		Company ret = null;
		CompanyDAO companyDAO = companyDAO();
		if (trans != null) {
			companyDAO = companyDAO(trans);
		}

		if (cmpId != null) {
			try {
				ret = retrieveCompany(companyDAO, cmpId);

			} catch (DAOException de) {
				logger.error("retrieve company detail failed; cmpid = ["
						+ cmpId + "]:" + de.getMessage(), de);
				throw de;
			}
		}
		return ret;
	}

	private String getNewCompanyId(Transaction trans) {
		// TODO Might be used to reference companies
		return null;
	}

	@Override
	public Company[] searchCompanies(Map<String, String> params,
			boolean unlimitedSearch) throws DAOException {
		return companyDAO().searchCompanies(params, unlimitedSearch);
	}

	@Override
	public boolean modifyCompany(Company company) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		CompanyDAO companyDAO = companyDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		Integer companyId = company.getCompanyId();
		Integer addressId = company.getAddress().getAddressId();
		boolean operationOk = false;
		boolean removeAddress = false;

		if (companyId != null) {
			try {

				// check address information
				if (addressId != null) {
					if (company.hasAddress()) {
						// has a previous address, modify existing address
						infraDAO.modifyAddress(company.getAddress());
					} else {
						// does not have an address any longer
						removeAddress = true;
						company.getAddress().setAddressId(null);
					}
				} else {
					company.getAddress().setAddressId(null);

					// checks to see if address is even available
					if (company.hasAddress()) {
						Address newAddress = infraDAO.createAddress(company
								.getAddress());

						// give company assigned address id
						company.getAddress().setAddressId(
								newAddress.getAddressId());
					}
				}
				int userId = InfrastructureDefs.getCurrentUserId();
				infraDAO.createCompanyFieldAuditLogs(company.getCmpId(),
						userId, company.getFieldAuditLogs());

				operationOk = companyDAO.modifyCompany(company);

				if (removeAddress) {
					// clean up addresses
					infraDAO.removeAddress(addressId);
				}

				// everything appears to be successful
				trans.complete();

			} catch (DAOException e) {
				operationOk = false;
				cancelTransaction("companyId=" + companyId, trans, e);
			} finally {
				operationOk = true;
				// End transaction (clean up)
				closeTransaction(trans);
			}
		} else {
			// there is no company
			operationOk = false;
		}
		return operationOk;
	}

	public CompanyNote[] retrieveCompanyNotes(String cmpId) throws DAOException {
		CompanyNote[] ret = null;

		try {
			ret = companyDAO().retrieveCompanyNotes(cmpId);
		} catch (DAOException de) {
			logger.error("retrieve company notes failed for " + cmpId + ". "
					+ de.getMessage(), de);
		}

		return ret;
	}

	@Override
	public CompanyNote createCompanyNote(CompanyNote companyNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		CompanyNote ret = null;

		try {
			ret = createCompanyNote(companyNote, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	private CompanyNote createCompanyNote(CompanyNote companyNote,
			Transaction trans) throws DAOException {
		CompanyNote ret = null;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		CompanyDAO companyDAO = companyDAO(trans);

		try {
			Note tempNote = infraDAO.createNote(companyNote);

			if (tempNote != null) {
				ret = companyNote;
				ret.setNoteId(tempNote.getNoteId());

				companyDAO.addCompanyNote(ret.getCompanyId(), ret.getCmpId(),
						ret.getNoteId());
			} else {
				logger.error("Failed to insert Company Note for "
						+ companyNote.getCompanyId());
				throw new DAOException("Failed to insert Company Note for "
						+ companyNote.getCompanyId());
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}

		return ret;
	}

	@Override
	public boolean modifyCompanyNote(CompanyNote companyNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		boolean ret = false;

		try {
			ret = infraDAO.modifyNote(companyNote);

			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;

	}

	/**
	 * Creates a field audit log for a new company.
	 * 
	 * @param company
	 * @param fldAudLogId
	 * @param attrCd
	 * @param audLogOrigVal
	 * @param audLogNewVal
	 * @param userId
	 * @param trans
	 * @throws DAOException
	 */
	private void createFieldAudLog(Company company, String fldAudLogId,
			String attrCd, String audLogOrigVal, String audLogNewVal,
			int userId, Transaction trans) throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			FieldAuditLog[] auditLog = new FieldAuditLog[1];
			auditLog[0] = new FieldAuditLog(attrCd, fldAudLogId, audLogOrigVal,
					audLogNewVal);

			infraDAO.createCompanyFieldAuditLogs(company.getCmpId(), userId,
					auditLog);
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
	}

	@Override
	public Company[] retrieveCompanies() throws DAOException {
		return companyDAO().retrieveAllCompanies();
	}

	@Override
	public Company retrieveCompanyByExternalCompanyId(Object externalCompanyId,
			boolean staging) throws DAOException {
		Company ret = null;
		CompanyDAO companyDAO = getCompanyDAO(staging);
		try {
			if (externalCompanyId instanceof Long) {
				ret = companyDAO.retrieveCompanyByExternalCompanyId((Long)externalCompanyId);
			}
		} catch (DAOException de) {
			logger.error("retrieve company by external company id failed for "
					+ externalCompanyId + ". " + de.getMessage(), de);
		}

		return ret;
	}

	private CompanyDAO getCompanyDAO(boolean staging) throws DAOException {
		String schema = null;
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			if (staging) {
				schema = "Staging";
			} else {
				schema = "ReadOnly";
			}
		}

		return companyDAO(schema);
	}

	public ValidationMessage validateExternalCompanyId(Company validateCompany) {
		if (validateCompany.getExternalCompanyId() == null
				|| validateCompany.getExternalCompanyId().equals(""))
			return null;
		try {
			Long companyId = Utility.tryParseLong(validateCompany
					.getExternalCompanyId());

			if (companyId == null || companyId < Integer.MIN_VALUE || companyId > Integer.MAX_VALUE) {
				return new ValidationMessage("externalId",
						"CROMERR ID must be integer between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE,
						ValidationMessage.Severity.ERROR, null);
			}

			Company company = companyDAO().retrieveCompanyByExternalCompanyId(
					companyId);

			if (company != null) {
				if (validateCompany.getCompanyId() == null
						|| !company.getCompanyId().equals(
								validateCompany.getCompanyId())) {
					return new ValidationMessage(
							"externalId",
							"Company ID="
									+ company.getCmpId()
									+ " has already been assigned CROMERR ID="
									+ company.getExternalCompanyId()
									+ ". You cannot assign the same CROMERR ID to more than one Company.",
							ValidationMessage.Severity.ERROR, null);

				}
			}
		} catch (DAOException de) {
			logger.error(
					"Validate by external company id failed for "
							+ validateCompany.getExternalCompanyId() + ". "
							+ de.getMessage(), de);
		}
		return null;
	}

	@Override
	public FacilityList[] retrieveAuthorizedFacilities(String cmpId,
			String externalUsername) {
		FacilityList[] authorizedFacilities = new FacilityList[0];

		if (Utility.isNullOrEmpty(cmpId)) {
			return authorizedFacilities;
		}

		if (Utility.isNullOrEmpty(externalUsername)) {
			return authorizedFacilities;
		}

		try {
			authorizedFacilities = companyDAO().retrieveAuthorizedFacilities(
					cmpId, externalUsername);
		} catch (DAOException de) {
			logger.error("Couldn't retrieve authorized facilities for " + cmpId
					+ ". " + de.getMessage(), de);
		}

		return authorizedFacilities;
	}

	@Override
	public ContactRole[] retrieveActiveContactRolesFromSession(String externalUsername) {
		ContactRole[] activeContactRoles = null;

		if (externalUsername == null || externalUsername.equals("")) {
			return null;
		}

		try {
			List<ContactRole> activeContactRolesList = new ArrayList<ContactRole>();

			List<Company> impactAuthorizedCompanies = null;
			Company[] authorizedCompanies = companyDAO()
					.retrieveAuthorizedCompanies(externalUsername);
			if (authorizedCompanies != null) {
				impactAuthorizedCompanies = Arrays.asList(authorizedCompanies);
			}

			List<ContactRole> contactRoles = null;
			ExternalContext context = FacesContext.getCurrentInstance()
					.getExternalContext();
			if (context != null) {
				HttpServletRequest request = (HttpServletRequest) context.getRequest();
				Principal p = request.getUserPrincipal();
				if (p instanceof UserPrincipal) {
					UserPrincipal userPrincipal = (UserPrincipal)p;
					contactRoles = userPrincipal.getContact().getContactRoles();
				}
			}

			if (null != contactRoles) {
				for (ContactRole contactRole : contactRoles) {
					for (Company impactAuthorizedCompany : impactAuthorizedCompanies) {
					  ExternalOrganization organization = contactRole.getExternalRole().getOrganization();
						if (organization.getOrganizationId() != null && 
								  impactAuthorizedCompany.getExternalCompanyId() != null) {
							if (organization.getOrganizationId() == Long
									.parseLong(impactAuthorizedCompany
											.getExternalCompanyId())) {
								impactAuthorizedCompany.setExternalOrganization(organization);
								contactRole.setCompany(impactAuthorizedCompany);
								activeContactRolesList.add(contactRole);
								continue;
							}
						}
					}
				}
			}
			
			
			//TODO continue populating contactRole objects ...
			

			activeContactRoles = activeContactRolesList.toArray(new ContactRole[0]);

		} catch (DAOException de) {
			logger.error("Couldn't retrieve authorized companies for "
					+ externalUsername + ". " + de.getMessage(), de);
		}

		return null == activeContactRoles ? new ContactRole[0] : activeContactRoles;
	}

	@Override
	public Company[] retrieveAuthorizedCompanies(String externalUsername) {
		Company[] authorizedCompanies = null;

		if (Utility.isNullOrEmpty(externalUsername)) {
			return authorizedCompanies;
		}

		try {
			authorizedCompanies = companyDAO().retrieveAuthorizedCompanies(
					externalUsername);

		} catch (DAOException de) {
			logger.error("Couldn't retrieve authorized companies for "
					+ externalUsername + ". " + de.getMessage(), de);
		}

		return null == authorizedCompanies ? new Company[0]
				: authorizedCompanies;
	}

	@Override
	public void deleteCompany(Company company) throws DAOException {
		if (company.getNotes() != null) {
			companyDAO().removeCompanyNotes(company);
			for (CompanyNote note : company.getNotes()) {
				infrastructureDAO().removeNote(note.getNoteId());
			}
		}
		companyDAO().deleteCompany(company);
	}

	@Override
	public void mergeCompanies(Company sourceCompany, Company destinationCompany)
			throws DAOException {
		// merge operation
		companyDAO().modifyCompanyFacilitiesAffiliation(sourceCompany,
				destinationCompany);
		companyDAO().modifyCompanyContactsAffiliation(sourceCompany,
				destinationCompany);
		companyDAO().modifyCompanyNotesAffiliation(sourceCompany,
				destinationCompany);

		// create audit trail
		CompanyNote companyNote = new CompanyNote();
		companyNote.setCompanyId(destinationCompany.getCompanyId());
		companyNote.setCmpId(destinationCompany.getCmpId());
		companyNote.setUserId(InfrastructureDefs.getCurrentUserId());
		companyNote.setNoteTypeCd(NoteType.DAPC);
		companyNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
		StringBuffer companyNoteTxt = new StringBuffer();
		companyNoteTxt
				.append("This company ["
						+ destinationCompany.getCmpId()
						+ "] has facility, employee, and note associations merged from the company: "
						+ sourceCompany.getCmpId());
		companyNote.setNoteTxt(companyNoteTxt.toString());

		createCompanyNote(companyNote);
	}
	
	public CompanyEmissionsOffsetRow[] retrieveEmissionsOffsetsByCompanyId(Integer cmpId)
			throws DAOException {
		return companyDAO().retrieveEmissionsOffsetsByCompanyId(cmpId);
	}

	@Override
	public EmissionsOffsetAdjustment createEmissionsOffsetAdjustment(
			EmissionsOffsetAdjustment emissionsOffsetAdjustment)
			throws DAOException {
		return companyDAO().createEmissionsOffsetAdjustment(emissionsOffsetAdjustment);

	}

	@Override
	public EmissionsOffsetAdjustment retrieveEmissionsOffsetAdjustment(
			Integer emissionsOffsetAdjustmentId) throws DAOException {
		return companyDAO().retrieveEmissionsOffsetAdjustment(emissionsOffsetAdjustmentId);

	}

	@Override
	public EmissionsOffsetAdjustment[] retrieveEmissionsOffsetAdjustmentsByCompanyId(
			Integer cmpId) throws DAOException {
		return companyDAO().retrieveEmissionsOffsetAdjustmentsByCompanyId(cmpId);
	}

	@Override
	public boolean modifyEmissionsOffsetAdjustment(
			EmissionsOffsetAdjustment emissionsOffsetAdjustment)
			throws DAOException {
		return companyDAO().modifyEmissionsOffsetAdjustment(emissionsOffsetAdjustment);
	}

	@Override
	public void deleteEmissionsOffsetAdjustment(
			Integer emissionsOffsetAdjustmentId) throws DAOException {
		companyDAO().deleteEmissionsOffsetAdjustment(emissionsOffsetAdjustmentId);
	}

	@Override
	public void deleteEmissionsOffsetAdjustmentsByCompanyId(Integer cmpId)
			throws DAOException {
		companyDAO().deleteEmissionsOffsetAdjustmentsByCompanyId(cmpId);
	}
	
	@Override
	public PollutantDef[] findPollutantsByNonattainmentArea(String areaCd)
			throws DAOException {
		return infrastructureDAO().findPollutantsByNonattainmentArea(areaCd);
	}
	
	@Override
	public OffsetSummaryLineItem[] retrieveOffsetSummaryLineItems(OffsetSummaryReport report)
			throws DAOException {
		return companyDAO().retrieveOffsetSummaryLineItems(report);
	}
	
}

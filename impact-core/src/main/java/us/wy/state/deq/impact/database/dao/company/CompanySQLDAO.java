package us.wy.state.deq.impact.database.dao.company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.reports.OffsetSummaryReport;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.database.dao.CompanyDAO;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyEmissionsOffsetRow;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyNote;
import us.wy.state.deq.impact.database.dbObjects.company.EmissionsOffsetAdjustment;
import us.wy.state.deq.impact.database.dbObjects.company.OffsetSummaryLineItem;

@Repository
public class CompanySQLDAO extends AbstractDAO implements CompanyDAO {
	
	private Logger logger = Logger.getLogger(CompanySQLDAO.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.wy.state.deq.impact.database.dao.company.CompanyDAO#createCompany(
	 * us.wy.state.deq.impact.database.dbObjects.company.Company)
	 */
	@Override
	public final Company createCompany(Company company) throws DAOException {
		Company ret = company;
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.createCompany", false);

		int i = 1;

		// prepare SQL insert (primary table attributes)
		Integer id = nextSequenceVal("S_Company_Id", company.getCompanyId());
		connHandler.setInteger(i++, id);
		connHandler.setString(i++, company.getName());
		connHandler.setString(i++, company.getAlias());
		connHandler.setString(i++, company.getPhone());
		connHandler.setString(i++, company.getFax());

		// get the address ID from previously added address
		connHandler.setString(i++, company.getAddress().getAddressId());

		// CROMERR
		connHandler.setInteger(i++,
				Utility.tryParseInt(company.getExternalCompanyId()));
		
		// paykey and vendor number for NSR Billing
		connHandler.setString(i++, company.getPayKey());
		connHandler.setLong(i++, company.getVendorNumber());

		// execute
		connHandler.update();

		ret.setCompanyId(id);
		ret.setLastModified(1);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.wy.state.deq.impact.database.dao.company.CompanyDAO#searchCompanies()
	 */
	@Override
	public final Company[] searchCompanies(Map<String, String> params,
			boolean unlimitedResults) throws DAOException {
		String cmpId = params.get("id");

		if (!Utility.isNullOrEmpty(cmpId)) {
			String format = "CMP%06d";
			int tempId;
			try {
				tempId = Integer.parseInt(cmpId.trim());
				cmpId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		String statementSQL = loadSQL("CompanySQL.findCompanies");

		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}

		StringBuffer whereClause = new StringBuffer("");

		if (cmpId != null && cmpId.trim().length() > 0) {
			whereClause.append(" AND LOWER(ccm.cmp_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(cmpId.trim().replace("*", "%")));
			whereClause.append("')");
		}
		if (params.get("name") != null
				&& params.get("name").trim().length() > 0) {
			whereClause.append(" AND LOWER(ccm.name) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("name").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}
		if (params.get("alias") != null
				&& params.get("alias").trim().length() > 0) {
			whereClause.append(" AND LOWER(ccm.alias) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("alias").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}
		if (params.get("phone") != null
				&& params.get("phone").trim().length() > 0) {
			String phone = stripVisualPhoneNumberSeparators(params.get("phone"));
			whereClause.append(" AND ccm.phone LIKE ");
			whereClause.append("'");
			whereClause.append(SQLizeString(phone.trim().replace("*", "%")));
			whereClause.append("'");
		}
		if (params.get("enviteId") != null
				&& params.get("enviteId").trim().length() > 0) {
			whereClause.append(" AND LOWER(ccm.envite_company_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("enviteId").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}

		StringBuffer sortBy = new StringBuffer(" ORDER BY ccm.name ASC");
		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);

		ArrayList<Company> ret = connHandler.retrieveArray(Company.class,
				defaultSearchLimit);

		return ret.toArray(new Company[0]);
	}

	private String stripVisualPhoneNumberSeparators(String string) {
		String result = string.replaceAll("\\(|\\)|-", "");
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.wy.state.deq.impact.database.dao.company.CompanyDAO#retrieveAllCompanies
	 * ()
	 */
	@Override
	public final Company[] retrieveAllCompanies() throws DAOException {

		String statementSQL = loadSQL("CompanySQL.findCompanies");

		// unlimited results
		setDefaultSearchLimit(-1);

		StringBuffer sortBy = new StringBuffer(" ORDER BY ccm.name ASC");
		statementSQL += sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);

		ArrayList<Company> ret = connHandler.retrieveArray(Company.class,
				defaultSearchLimit);

		return ret.toArray(new Company[0]);
	}

	/**
	 * @throws DAOException
	 * @see CompanyDAO#addCompanyAddress(int cpId, int addressId)
	 */
	@Override
	public void addCompanyAddress(Integer cpId, Integer addressId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.addCompanyAddress", false);
		connHandler.setInteger(1, cpId);
		connHandler.setInteger(2, addressId);
		connHandler.update();
	}

	@Override
	public Company retrieveCompany(Integer companyId) throws DAOException {
		Company ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (companyId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("CompanySQL.retrieveCompany"));

				pStmt.setInt(1, companyId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = new Company();
					ret.populate(rs);

				}

				retrieveCompleteCompanyProfile(ret);

				rs.close();
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		}
		
		return ret;
	}

	@Override
	public Company retrieveCompany(String cmpId) throws DAOException {
		Company ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (cmpId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("CompanySQL.retrieveCompanyFromCmpId"));

				pStmt.setString(1, cmpId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = new Company();
					ret.populate(rs);

				}

				retrieveCompleteCompanyProfile(ret);

				rs.close();
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		}

		return ret;
	}

	private void retrieveCompleteCompanyProfile(Company company)
			throws DAOException {
		// retrieve the rest of a company's profile (address information, roles,
		// etc.)
		Company ret = company;
		
		if(ret == null){
			return;
		}

		if (ret.getCompanyId() == null || ret.getCmpId() == null) {
			return;
		}

		// get addresses
		ret.setAddresses(retrieveCompanyAddresses(ret.getCompanyId()));

		// get notes
		CompanyNote[] notes = retrieveCompanyNotes(ret.getCmpId());
		ArrayList<CompanyNote> hashNotes = new ArrayList<CompanyNote>();
		for (CompanyNote note : notes) {
			hashNotes.add(note);
		}
		ret.setNotes(hashNotes);

		// get facilities
		FacilityList[] facilities = retrieveOwnedFacilities(ret.getCmpId());
		ret.setFacilities(facilities);

		// get contacts
		Contact[] contacts = retrieveCompanyContacts(ret.getCompanyId());
		ret.setContacts(contacts);
		
		// get offset tracking info
		CompanyEmissionsOffsetRow[] emissionsOffsetRows
			= retrieveEmissionsOffsetsByCompanyId(ret.getCompanyId());
		ret.setEmissionsOffsetRows(emissionsOffsetRows);
		
		// get emissions offset adjustments
		EmissionsOffsetAdjustment[] emissionsOffsetAdjustments
			= retrieveEmissionsOffsetAdjustmentsByCompanyId(ret.getCompanyId());
		ret.setEmissionsOffsetAdjustments(emissionsOffsetAdjustments);

	}

	public final ArrayList<Address> retrieveCompanyAddresses(int companyId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.retrieveCompanyAddress", true);

		connHandler.setInteger(1, companyId);

		ArrayList<Address> ret = connHandler.retrieveArray(Address.class);

		return ret;
	}

	public final boolean modifyCompany(Company company) throws DAOException {
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.modifyCompany", false);

		int i = 1;
		connHandler.setString(i++, company.getName());
		connHandler.setString(i++, company.getAlias());
		connHandler.setString(i++, company.getPhone());
		connHandler.setString(i++, company.getFax());
		connHandler.setInteger(i++, company.getAddress().getAddressId());
		connHandler.setInteger(i++,
				Utility.tryParseInt(company.getExternalCompanyId()));
		connHandler.setString(i++,  company.getPayKey());
		connHandler.setLong(i++, company.getVendorNumber());
		connHandler.setInteger(i++, company.getLastModified() + 1);

		connHandler.setInteger(i++, company.getCompanyId());
		connHandler.setInteger(i++, company.getLastModified());

		connHandler.update();

		ret = true;
		return ret;

	}

	@Override
	public String retrieveCmpId(Integer companyId) throws DAOException {
		Connection conn = null;
		PreparedStatement pStmt = null;
		String cmpId = null;
		try {
			conn = getReadOnlyConnection();
			pStmt = conn.prepareStatement(loadSQL("CompanySQL.retrieveCmpId"));
			pStmt.setInt(1, companyId);
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) {
				cmpId = rs.getString("cmp_id");
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		logger.debug("Cmp Id: " + cmpId);

		return cmpId;
	}

	// ******************** \\
	// Company Note Methods \\
	// ******************** \\
	@Override
	public final CompanyNote[] retrieveCompanyNotes(String cmpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.retrieveCompanyNotes", true);

		connHandler.setString(1, cmpId);

		ArrayList<CompanyNote> ret = connHandler
				.retrieveArray(CompanyNote.class);

		return ret.toArray(new CompanyNote[0]);
	}

	@Override
	public void addCompanyNote(Integer companyId, String cmpId, Integer noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.addCompanyNote", false);
		logger.debug("cmp_id=" + cmpId);

		connHandler.setInteger(1, companyId);
		connHandler.setInteger(2, noteId);
		connHandler.setString(3, cmpId);

		// last modified hard coded to 1 ***
		connHandler.setString(4, 1);

		connHandler.update();

		return;

	}

	// ************************ \\
	// Company Facility Methods \\
	// ************************ \\
	@Override
	public final FacilityList[] retrieveOwnedFacilities(String cmpId)
			throws DAOException {
		String statementSQL = loadSQL("CompanySQL.retrieveOwnedFacilities");

		// unlimited results
		setDefaultSearchLimit(-1);

		StringBuffer sortBy = new StringBuffer(
				" ORDER BY isnull(fc.end_date,'9/9/9999') DESC, fc.start_date DESC");

		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			sortBy = new StringBuffer(" ORDER BY ff.facility_id ASC");
		}

		statementSQL += sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);
		connHandler.setString(1, cmpId);

		ArrayList<FacilityList> ret = connHandler.retrieveArray(
				FacilityList.class, defaultSearchLimit);

		return ret.toArray(new FacilityList[0]);
	}

	@Override
	public final Integer retrieveTotalFacilitiesOwned(String cmpId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLString("CompanySQL.retrieveTotalFacilitiesOwned");
		connHandler.setString(1, cmpId);

		Integer ret = (Integer) connHandler.retrieveJavaObject(Integer.class);

		return ret;
	}

	@Override
	public final FacilityList[] retrieveAuthorizedFacilities(String cmpId,
			String externalUsername) throws DAOException {
		String statementSQL = loadSQL("CompanySQL.retrieveAuthorizedFacilities");

		// unlimited results
		setDefaultSearchLimit(-1);

		StringBuffer sortBy = new StringBuffer(" ORDER BY ff.facility_id ASC");

		statementSQL += sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);
		connHandler.setString(1, cmpId);
		connHandler.setString(2, externalUsername);

		ArrayList<FacilityList> ret = connHandler.retrieveArray(
				FacilityList.class, defaultSearchLimit);

		return ret.toArray(new FacilityList[0]);
	}

	@Override
	public final Contact[] retrieveCompanyContacts(Integer companyId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.retrieveContactsByCompany", true);

		connHandler.setInteger(1, companyId);

		ArrayList<Contact> ret = connHandler.retrieveArray(Contact.class,
				defaultSearchLimit);

		return ret.toArray(new Contact[0]);
	}

	@Override
	public Company retrieveCompanyByExternalCompanyId(Long externalCompanyId)
			throws DAOException {
		Company ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		checkNull(externalCompanyId);

		if (externalCompanyId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("CompanySQL.retrieveCompanyByExternalCompanyId"));

				pStmt.setLong(1, externalCompanyId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = new Company();
					ret.populate(rs);

				}

				retrieveCompleteCompanyProfile(ret);

				rs.close();
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		}

		return ret;
	}

	@Override
	public Company[] retrieveAuthorizedCompanies(String externalUsername)
			throws DAOException {
		
		String statementSQL = loadSQL("CompanySQL.retrieveAuthorizedCompanies");

		// unlimited results
		setDefaultSearchLimit(-1);

		StringBuffer sortBy = new StringBuffer(" ORDER BY ccm.name ASC");
		statementSQL += sortBy.toString();
		
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);
		connHandler.setString(1, externalUsername);

		ArrayList<Company> ret = connHandler.retrieveArray(Company.class, defaultSearchLimit);

		return ret.toArray(new Company[0]);
	}

	@Override
	public Company retrieveCompanyByExternalId(int externalId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.retrieveCompanyByExternalId", true);

		connHandler.setInteger(1, externalId);

		return (Company) connHandler.retrieve(Company.class);
	}

	@Override
	public boolean isDuplicateCompanyName(Integer companyId, String companyName)
			throws DAOException {
		if (Utility.isNullOrEmpty(companyName)) {
			return true;
		}
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.isDuplicateCompanyName", false);
		connHandler.setString(1, companyName.trim());
		connHandler.setInteger(2, companyId);
		connHandler.setInteger(3, companyId);
		Integer count = (Integer) connHandler.retrieveJavaObject(Integer.class);
		return count > 0;
	}
	
	@Override
	public void deleteCompany(Company company) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		this.setTransaction(trans);
		// delete company
		try{
			ConnectionHandler companyHandler = new ConnectionHandler(
					"CompanySQL.removeCompany", false);
			companyHandler.setInteger(1, company.getCompanyId());
			companyHandler.remove();
			trans.complete();
		} catch (DAOException de) {
			if (trans != null) {
				trans.cancel();
			}
			logger.error(de.getMessage(), de);
			throw de;
		} finally {
			if (trans != null) {
				trans.close();
			}
		}
		return;
	}

	@Override
	public void modifyCompanyFacilitiesAffiliation(Company sourceCompany,
			Company destinationCompany) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.modifyFacilityCompanyXrefs", false);

		connHandler.setInteger(1, destinationCompany.getCompanyId());
		connHandler.setInteger(2, sourceCompany.getCompanyId());

		connHandler.updateNoCheck();

	}

	@Override
	public void modifyCompanyContactsAffiliation(Company sourceCompany,
			Company destinationCompany) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.modifyCompanyContacts", false);

		connHandler.setInteger(1, destinationCompany.getCompanyId());
		connHandler.setInteger(2, sourceCompany.getCompanyId());

		connHandler.updateNoCheck();

	}

	@Override
	public void modifyCompanyNotesAffiliation(Company sourceCompany,
			Company destinationCompany) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.modifyCompanyNoteXrefs", false);

		connHandler.setInteger(1, destinationCompany.getCompanyId());
		connHandler.setString(2, destinationCompany.getCmpId());
		connHandler.setInteger(3, sourceCompany.getCompanyId());
		connHandler.setString(4, sourceCompany.getCmpId());

		connHandler.updateNoCheck();

	}
	
	@Override
	public final void removeCompanyNotes(Company company)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.removeCompanyNotes", false);

		connHandler.setInteger(1, company.getCompanyId());
		connHandler.setString(2, company.getCmpId());

		connHandler.remove();
	}
	
	@Override
	public CompanyEmissionsOffsetRow[] retrieveEmissionsOffsetsByCompanyId(Integer cmpId)
			throws DAOException {
		checkNull(cmpId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.retrieveEmissionsOffsetsByCompanyId", true);
		
		connHandler.setInteger(1, cmpId);
		
		List<CompanyEmissionsOffsetRow> companyEmissionsOffsetRowList
			= new ArrayList<CompanyEmissionsOffsetRow>();
		
		companyEmissionsOffsetRowList = connHandler.retrieveArray(CompanyEmissionsOffsetRow.class);
		
		return companyEmissionsOffsetRowList.toArray(new CompanyEmissionsOffsetRow[0]);
	}

	@Override
	public EmissionsOffsetAdjustment createEmissionsOffsetAdjustment(
			EmissionsOffsetAdjustment emissionsOffsetAdjustment)
			throws DAOException {
		checkNull(emissionsOffsetAdjustment);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.createEmissionsOffsetAdjustment", true);
		int i = 1;
		int id = nextSequenceVal("S_Emissions_Offset_Adjustment_Id");
		
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, emissionsOffsetAdjustment.getCompanyId());
		connHandler.setString(i++, emissionsOffsetAdjustment.getNonAttainmentAreaCd());
		connHandler.setTimestamp(i++, emissionsOffsetAdjustment.getDate());
		connHandler.setString(i++, emissionsOffsetAdjustment.getComment());
		connHandler.setString(i++, emissionsOffsetAdjustment.getPollutantCd());
		connHandler.setDouble(i++, emissionsOffsetAdjustment.getAmount());
		connHandler.setString(i++, emissionsOffsetAdjustment.getIncludeInTotal());
		
		connHandler.update();
		
		emissionsOffsetAdjustment.setEmissionsOffsetAdjustmentId(id);
		emissionsOffsetAdjustment.setLastModified(1);
		
		return emissionsOffsetAdjustment;
	}

	@Override
	public EmissionsOffsetAdjustment retrieveEmissionsOffsetAdjustment(
			Integer emissionsOffsetAdjustmentId) throws DAOException {
		checkNull(emissionsOffsetAdjustmentId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.retrieveEmissionsOffsetAdjustment", true);
		
		connHandler.setInteger(1, emissionsOffsetAdjustmentId);
		
		return (EmissionsOffsetAdjustment)connHandler.retrieve(EmissionsOffsetAdjustment.class);
	}

	@Override
	public EmissionsOffsetAdjustment[] retrieveEmissionsOffsetAdjustmentsByCompanyId(
			Integer cmpId) throws DAOException {
		checkNull(cmpId);
		
		ConnectionHandler connHandler = new ConnectionHandler(true);
		
		StringBuffer whereClause = new StringBuffer();
		
		whereClause.append(" AND eoa.company_id = " + cmpId);
	
		// for external app retrieve only those adjustments that are 
		// not excluded from the total i.e., include_in_total = 'Y'
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			whereClause.append(" AND eoa.include_in_total = ");
			whereClause.append("'");
			whereClause.append("Y");
			whereClause.append("'");
		}
		
		String sqlStatement = loadSQL("CompanySQL.retrieveEmissionsOffsetAdjustmentsByCompanyId");
		sqlStatement += whereClause;
		
		connHandler.setSQLStringRaw(sqlStatement);
		
		List<EmissionsOffsetAdjustment> emissionsOffsetAdjustmentList =
				connHandler.retrieveArray(EmissionsOffsetAdjustment.class);
		
		return emissionsOffsetAdjustmentList.toArray(new EmissionsOffsetAdjustment[0]);
	}

	@Override
	public boolean modifyEmissionsOffsetAdjustment(
			EmissionsOffsetAdjustment emissionsOffsetAdjustment)
			throws DAOException {
		
		checkNull(emissionsOffsetAdjustment);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.modifyEmissionsOffsetAdjustment", true);
		
		int i = 1;
		
		connHandler.setInteger(i++, emissionsOffsetAdjustment.getCompanyId());
		connHandler.setString(i++, emissionsOffsetAdjustment.getNonAttainmentAreaCd());
		connHandler.setTimestamp(i++, emissionsOffsetAdjustment.getDate());
		connHandler.setString(i++, emissionsOffsetAdjustment.getComment());
		connHandler.setString(i++, emissionsOffsetAdjustment.getPollutantCd());
		connHandler.setDouble(i++, emissionsOffsetAdjustment.getAmount());
		connHandler.setString(i++, emissionsOffsetAdjustment.getIncludeInTotal());
		connHandler.setInteger(i++, emissionsOffsetAdjustment.getLastModified() + 1);
		
		connHandler.setInteger(i++, emissionsOffsetAdjustment.getEmissionsOffsetAdjustmentId());
		connHandler.setInteger(i++, emissionsOffsetAdjustment.getLastModified());
		
		return connHandler.update();
	}

	@Override
	public void deleteEmissionsOffsetAdjustment(
			Integer emissionsOffsetAdjustmentId) throws DAOException {
		checkNull(emissionsOffsetAdjustmentId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.deleteEmissionsOffsetAdjustment", true);
		connHandler.setInteger(1, emissionsOffsetAdjustmentId);
		connHandler.remove();
	}

	@Override
	public void deleteEmissionsOffsetAdjustmentsByCompanyId(Integer cmpId)
			throws DAOException {
		checkNull(cmpId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"CompanySQL.deleteEmissionsOffsetAdjustmentsByCompanyId", true);
		connHandler.setInteger(1, cmpId);
		connHandler.remove();
	}
	
	@Override
	public OffsetSummaryLineItem[] retrieveOffsetSummaryLineItems(OffsetSummaryReport report)
			throws DAOException {
		checkNull(report);
		
		StringBuffer whereClause = new StringBuffer("");
		if(!Utility.isNullOrEmpty(report.getNonAttainmentAreaCd())) {
			whereClause.append(" AND res.AREA_CD = ");
			whereClause.append("'");
			whereClause.append(SQLizeString(report.getNonAttainmentAreaCd().trim()));
			whereClause.append("'");
		}
		
		if(!Utility.isNullOrEmpty(report.getPollutantCd())) {
			whereClause.append(" AND res.POLLUTANT_CD = ");
			whereClause.append("'");
			whereClause.append(SQLizeString(report.getPollutantCd().trim()));
			whereClause.append("'");
		}
		
		if(!Utility.isNullOrEmpty(report.getCmpId())) {
			whereClause.append(" AND res.CMP_ID = ");
			whereClause.append("'");
			whereClause.append(SQLizeString(report.getCmpId().trim()));
			whereClause.append("'");
		}
		
		
		String statementSQL = loadSQL("CompanySQL.retrieveOffsetSummaryLineItems");
		statementSQL+= whereClause.toString();
		
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);
		
		List<OffsetSummaryLineItem> offsetSummaryLineItemList = 
				 connHandler.retrieveArray(OffsetSummaryLineItem.class);
		
		return offsetSummaryLineItemList.toArray(new OffsetSummaryLineItem[0]);
	}
}

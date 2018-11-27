package us.wy.state.deq.impact.database.dao.contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.contact.ContactNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.database.dao.ContactDAO;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

@Repository
public class ContactSQLDAO extends AbstractDAO implements ContactDAO {
	
	private Logger logger = Logger.getLogger(ContactSQLDAO.class);

	@Override
	public Integer retrieveLastContactId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact retrieveContact(int contactId) throws DAOException {
		Contact ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
			ret = new Contact();
			return ret;
		}
		
		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ContactSQL.retrieveContact"));

			pStmt.setInt(1, contactId);

			rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Contact();
				ret.populate(rs);

				if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
					retrieveStagingContactDetail(ret);
				} else {
					retrieveCompleteContactDetail(ret);
				}
			}

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
			} catch (SQLException e) {
				handleException(e, conn);
			}
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}
	
	@Override
	public Contact retrieveContact(String cntId) throws DAOException {
		Contact ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
			ret = new Contact();
			return ret;
		}
		
		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ContactSQL.retrieveContactDetail"));

			pStmt.setString(1, cntId);

			rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Contact();
				ret.populate(rs);

				if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
					retrieveStagingContactDetail(ret);
				} else {
					retrieveCompleteContactDetail(ret);
				}
			}

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
			} catch (SQLException e) {
				handleException(e, conn);
			}
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public Contact retrieveStagingContact(int contactId, String facilityId)
			throws DAOException {
		Contact ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ContactSQL.retrieveStagingContact"));

			pStmt.setInt(1, contactId);
			pStmt.setString(2, facilityId);

			rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Contact();
				ret.populate(rs);
				ret.setFacilityId(facilityId);
				retrieveStagingContactDetail(ret);
			}

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
			} catch (SQLException e) {
				handleException(e, conn);
			}
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	private void retrieveStagingContactDetail(Contact contact)
			throws DAOException {
		// retrieve the rest of a contact's profile (address information, roles,
		// etc.)
		Contact ret = contact;

		// get addresses
		ret.setAddress(retrieveContactAddress(ret.getAddress().getAddressId()));

		ContactType[] contactTypes = retrieveStagingContactTypes(
				ret.getContactId(), ret.getFacilityId());
		ArrayList<ContactType> hashContactTypes = new ArrayList<ContactType>();
		for (ContactType contactType : contactTypes) {
			hashContactTypes.add(contactType);
		}

		ret.setContactTypes(hashContactTypes);

	}

	private void retrieveCompleteContactDetail(Contact contact)
			throws DAOException {
		// retrieve the rest of a contact's profile (address information, roles,
		// etc.)
		Contact ret = contact;

		// get addresses
		ret.setAddress(retrieveContactAddress(ret.getAddress().getAddressId()));

		// get notes
		ContactNote[] notes = retrieveContactNotes(ret.getContactId());
		ArrayList<ContactNote> hashNotes = new ArrayList<ContactNote>();
		for (ContactNote note : notes) {
			hashNotes.add(note);
		}

		ContactType[] contactTypes = retrieveContactTypes(ret.getContactId());
		ArrayList<ContactType> hashContactTypes = new ArrayList<ContactType>();
		for (ContactType contactType : contactTypes) {
			hashContactTypes.add(contactType);
		}

		ret.setNotes(hashNotes);
		ret.setContactTypes(hashContactTypes);

	}

	@Override
	public Contact retrieveContactByExternalUsername(String externalUsername)
			throws DAOException {
		Contact ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ContactSQL.retrieveContactByExternalUsername"));

			pStmt.setString(1, externalUsername);

			rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new Contact();
				ret.populate(rs);
			}

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
			} catch (SQLException e) {
				handleException(e, conn);
			}
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	public final Address retrieveContactAddress(int addressId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveAddress", true);

		connHandler.setInteger(1, addressId);

		Address ret = (Address) connHandler.retrieve(Address.class);
		return ret;
	}

	@Override
	public boolean modifyContact(Contact contact) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler("ContactSQL.modifyContact", false);

		int i = 1;
		connHandler.setString(i++, contact.getTitleCd());
		connHandler.setInteger(i++, contact.getAddress().getAddressId());
		connHandler.setString(i++, contact.getFirstNm());
		connHandler.setString(i++, contact.getMiddleNm());
		connHandler.setString(i++, contact.getLastNm());
		connHandler.setString(i++, contact.getSuffixCd());
		connHandler.setString(i++, contact.getPhoneNo());
		connHandler.setString(i++, contact.getPhoneExtensionVal());
		connHandler.setString(i++, contact.getSecondaryPhoneNo());
		connHandler.setString(i++, contact.getSecondaryExtensionVal());
		connHandler.setString(i++, contact.getMobilePhoneNo());
		connHandler.setString(i++, contact.getFaxNo());
		connHandler.setString(i++, contact.getEmailAddressTxt());
		connHandler.setString(i++, contact.getLastModified());
		connHandler.setString(i++, contact.getPreferredName());
		connHandler.setInteger(i++, contact.getCompanyId());
		connHandler.setString(i++, contact.getCompanyTitle());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(contact.isActive()));
		connHandler.setString(i++, contact.getEmailAddressTxt2());
		connHandler.setString(i++, contact.getContactId());
		connHandler.setString(i++, contact.getLastModified());

		connHandler.update();

		return true;
	}

	@Override
	public boolean modifyStagingContact(Contact contact) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler("ContactSQL.modifyStagingContact", false);

		int i = 1;
		connHandler.setString(i++, contact.getTitleCd());
		connHandler.setInteger(i++, contact.getAddress().getAddressId());
		connHandler.setString(i++, contact.getFirstNm());
		connHandler.setString(i++, contact.getMiddleNm());
		connHandler.setString(i++, contact.getLastNm());
		connHandler.setString(i++, contact.getSuffixCd());
		connHandler.setString(i++, contact.getPhoneNo());
		connHandler.setString(i++, contact.getPhoneExtensionVal());
		connHandler.setString(i++, contact.getSecondaryPhoneNo());
		connHandler.setString(i++, contact.getSecondaryExtensionVal());
		connHandler.setString(i++, contact.getMobilePhoneNo());
		connHandler.setString(i++, contact.getFaxNo());
		connHandler.setString(i++, contact.getEmailAddressTxt());
		connHandler.setString(i++, contact.getLastModified());
		connHandler.setString(i++, contact.getPreferredName());
		connHandler.setInteger(i++, contact.getCompanyId());
		connHandler.setString(i++, contact.getCompanyTitle());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(contact.isActive()));
		connHandler.setString(i++, contact.getEmailAddressTxt2());
		connHandler.setInteger(i++, contact.getContactId());
		connHandler.setString(i++, contact.getFacilityId());
		connHandler.setString(i++, contact.getLastModified());

		connHandler.update();

		return true;
	}

	@Override
	public void deleteContact(Contact contact) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		this.setTransaction(trans);
		ContactNote[] notes = retrieveContactNotes(contact.getContactId());
		try {
			// delete xrefs
			ConnectionHandler xrefHandler = new ConnectionHandler(
					"ContactSQL.deleteContactNoteXrefs", false);
			xrefHandler.setInteger(1, contact.getContactId());
			xrefHandler.remove();

			// delete notes
			for (ContactNote note : notes) {
				ConnectionHandler noteHandler = new ConnectionHandler(
						"ContactSQL.deleteContactNote", false);
				noteHandler.setInteger(1, note.getNoteId());
				noteHandler.remove();
			}

			// delete contact
			ConnectionHandler contactHandler = new ConnectionHandler(
					"ContactSQL.removeContact", false);
			contactHandler.setInteger(1, contact.getContactId());
			contactHandler.remove();

			// delete address
			removeRows("cm_address", "address_id", contact.getAddress()
					.getAddressId());

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
	public void removeContact(Integer contactId) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Contact retrieveContactByUserId(Integer userId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see InfrastructureDAO#createContact(Contact)
	 */
	public final Contact createContact(Contact contact) throws DAOException {
		checkNull(contact);

		Contact ret = contact;
		ConnectionHandler connHandler = new ConnectionHandler("ContactSQL.createContact", false);
		int i = 1;
		Integer id = nextSequenceVal("S_Contact_Id", contact.getContactId());

		connHandler.setInteger(i++, id);
		connHandler.setString(i++, null); // obsolete column
		connHandler.setTimestamp(i++, contact.getStartDate());
		connHandler.setTimestamp(i++, contact.getEndDate());
		connHandler.setString(i++, contact.getTitleCd());
		connHandler.setInteger(i++, contact.getAddress().getAddressId());
		connHandler.setString(i++, contact.getFirstNm());
		connHandler.setString(i++, contact.getMiddleNm());
		connHandler.setString(i++, contact.getLastNm());
		connHandler.setString(i++, contact.getSuffixCd());
		connHandler.setString(i++, contact.getPhoneNo());
		connHandler.setString(i++, contact.getPhoneExtensionVal());
		connHandler.setString(i++, contact.getSecondaryPhoneNo());
		connHandler.setString(i++, contact.getSecondaryExtensionVal());
		connHandler.setString(i++, contact.getMobilePhoneNo());
		connHandler.setString(i++, contact.getFaxNo());
		connHandler.setString(i++, contact.getPagerNo());
		connHandler.setString(i++, contact.getPagerPinNo());
		connHandler.setString(i++, contact.getEmailAddressTxt());
		connHandler.setString(i++, contact.getEmailPagerAddress());
		connHandler.setInteger(i++, contact.getMaxEmailPagerCharsNum());
		connHandler.setString(i++, contact.getCompanyTitle());
		connHandler.setString(i++, contact.getPreferredName());
		connHandler.setInteger(i++, contact.getCompanyId());
		connHandler.setString(i++, contact.getEmailAddressTxt2());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setContactId(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#createContact(Contact)
	 */
	public final Contact createStagingContact(Contact contact) throws DAOException {
		checkNull(contact);

		Contact ret = contact;
		ConnectionHandler connHandler = new ConnectionHandler("ContactSQL.createStagingContact", false);
		int i = 1;
		Integer id = nextSequenceVal("S_Contact_Id", contact.getContactId());

		connHandler.setInteger(i++, id);
		connHandler.setString(i++, null); // obsolete column
		connHandler.setTimestamp(i++, contact.getStartDate());
		connHandler.setTimestamp(i++, contact.getEndDate());
		connHandler.setString(i++, contact.getTitleCd());
		connHandler.setInteger(i++, contact.getAddress().getAddressId());
		connHandler.setString(i++, contact.getFirstNm());
		connHandler.setString(i++, contact.getMiddleNm());
		connHandler.setString(i++, contact.getLastNm());
		connHandler.setString(i++, contact.getSuffixCd());
		connHandler.setString(i++, contact.getPhoneNo());
		connHandler.setString(i++, contact.getPhoneExtensionVal());
		connHandler.setString(i++, contact.getSecondaryPhoneNo());
		connHandler.setString(i++, contact.getSecondaryExtensionVal());
		connHandler.setString(i++, contact.getMobilePhoneNo());
		connHandler.setString(i++, contact.getFaxNo());
		connHandler.setString(i++, contact.getPagerNo());
		connHandler.setString(i++, contact.getPagerPinNo());
		connHandler.setString(i++, contact.getEmailAddressTxt());
		connHandler.setString(i++, contact.getEmailPagerAddress());
		connHandler.setInteger(i++, contact.getMaxEmailPagerCharsNum());
		connHandler.setString(i++, contact.getCompanyTitle());
		connHandler.setString(i++, contact.getPreferredName());
		connHandler.setInteger(i++, contact.getCompanyId());
		connHandler.setString(i++, contact.getFacilityId());
		connHandler.setString(i++, contact.getEmailAddressTxt2());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setContactId(id);
		ret.setLastModified(1);

		return ret;
	}

	@Override
	public Contact[] searchContacts(Map<String, String> params,
			boolean unlimitedSearch) throws DAOException {
		String cntId = params.get("cntId");

		if (!Utility.isNullOrEmpty(cntId)) {
			String format = "CNT%06d";
			int tempId;
			try {
				tempId = Integer.parseInt(cntId.trim());
				cntId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		String statementSQL = loadSQL("ContactSQL.findContacts");

		if (unlimitedSearch) {
			setDefaultSearchLimit(-1);
		}

		StringBuffer whereClause = new StringBuffer("");

		if (cntId != null && cntId.trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.cnt_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(cntId.trim().replace("*", "%")));
			whereClause.append("')");
		}
		if (params.get("firstName") != null
				&& params.get("firstName").trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.first_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("firstName").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}
		if (params.get("lastName") != null
				&& params.get("lastName").trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.last_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("lastName").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}

		if (params.get("preferredName") != null
				&& params.get("preferredName").trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.preferred_name) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("preferredName").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}

		if (params.get("phone") != null
				&& params.get("phone").trim().length() > 0) {
			StringBuffer phone = new StringBuffer(12);
			for (int i = 0; i < params.get("phone").length(); i++) {
				String c = params.get("phone").substring(i, i + 1);
				if ("%*0123456789".contains(c)) {
					phone.append(c);
				}
			}
			if (phone.length() > 0) {
				whereClause.append(" AND cc.phone_no like '");
				whereClause.append(SQLizeString(phone.toString().replace("*",
						"%")));
				whereClause.append("'");
			}
		}

		if (params.get("companyId") != null
				&& params.get("companyId").trim().length() > 0) {
			whereClause.append(" AND cc.company_id='");
			whereClause.append(params.get("companyId"));
			whereClause.append("'");
		}

		if (params.get("contactId") != null
				&& params.get("contactId").trim().length() > 0) {
			whereClause.append(" AND cc.contact_id='");
			whereClause.append(params.get("contactId"));
			whereClause.append("'");
		}

		if (params.get("active") != null
				&& params.get("active").trim().length() > 0) {
			whereClause.append(" AND cc.active='");
			whereClause.append(params.get("active"));
			whereClause.append("'");
		}

		if (params.get("enviteUsername") != null
				&& params.get("enviteUsername").trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.envite_username) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("enviteUsername").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}

		StringBuffer sortBy = new StringBuffer(" ORDER BY cc.last_nm, cc.first_nm");
		statementSQL += whereClause.toString() + " " + sortBy.toString();

		logger.debug("statementSQL = " + statementSQL);

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);

		ArrayList<Contact> ret = connHandler.retrieveArray(Contact.class,
				defaultSearchLimit);

		return ret.toArray(new Contact[0]);
	}

	@Override
	public Contact[] searchForDuplicateContacts(Map<String, String> params,
			boolean unlimitedSearch) throws DAOException {
		String statementSQL = loadSQL("ContactSQL.findContacts");

		if (unlimitedSearch) {
			setDefaultSearchLimit(-1);
		}

		StringBuffer whereClause = new StringBuffer("");

		if (params.get("firstName") != null
				&& params.get("firstName").trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.first_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("firstName").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}
		if (params.get("lastName") != null
				&& params.get("lastName").trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.last_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("lastName").trim()
					.replace("*", "%")));
			whereClause.append("')");
		}

		if (params.get("email") != null
				&& params.get("email").trim().length() > 0) {
			whereClause.append(" AND LOWER(cc.email_address_txt) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("email").trim()
					.replace("*", "%")));
			whereClause.append("')");
			//whereClause.append(" AND cc.address_id = ca.address_id");
		}

		if (params.get("firstName") != null
				&& params.get("firstName").trim().length() > 0) {
			whereClause.append(" OR LOWER(cc.first_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(params.get("firstName").trim()
					.replace("*", "%")));
			whereClause.append("')");
			whereClause.append(" AND cc.last_nm is null ");
			whereClause.append(" AND cc.address_id = ca.address_id");
		}

		StringBuffer sortBy = new StringBuffer(" ORDER BY cc.last_nm, cc.first_nm");
		statementSQL += whereClause.toString() + " " + sortBy.toString();

		logger.debug("statementSQL = " + statementSQL);

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);

		ArrayList<Contact> ret = connHandler.retrieveArray(Contact.class,
				defaultSearchLimit);

		return ret.toArray(new Contact[0]);
	}

	@Override
	public List<Contact> retrieveGlobalContacts() throws DAOException {

		List<Contact> ret = new ArrayList<Contact>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ContactSQL.retrieveAllContacts"));

			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				Integer contactId = AbstractDAO.getInteger(rs, "contact_id");
				Contact tempContact = null;

				for (Contact tempC : ret) {
					if (tempC.getContactId().equals(contactId)) {
						tempContact = tempC;
						break;
					}
				}
				if (tempContact == null) {
					tempContact = new Contact();

					tempContact.populate(rs);
					ret.add(tempContact);
				}
				
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public void addContactType(Integer contactId, String contactTypeCd,
			Timestamp startDate, String facilityId) throws DAOException {
		checkNull(contactId);
		checkNull(contactTypeCd);
		checkNull(facilityId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.addContactType", false);

		connHandler.setInteger(1, contactId);
		connHandler.setString(2, contactTypeCd);
		if (startDate == null) {
			startDate = new Timestamp(System.currentTimeMillis());
		}
		if (startDate != null)
			startDate.setNanos(0);
		connHandler.setTimestamp(3, startDate);
		connHandler.setString(4, facilityId);
		connHandler.update();

		return;
	}

	public final void addContactType(Integer contactId, ContactType contactType)
			throws DAOException {
		checkNull(contactId);
		checkNull(contactType);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.addContactType1", false);

		connHandler.setInteger(1, contactId);
		connHandler.setString(2, contactType.getContactTypeCd());

		if (contactType.getStartDate() != null) {
			contactType.getStartDate().setNanos(0);
		}
		connHandler.setTimestamp(3, contactType.getStartDate());

		if (contactType.getEndDate() != null) {
			contactType.getEndDate().setNanos(0);
		}
		connHandler.setTimestamp(4, contactType.getEndDate());

		connHandler.setString(5, contactType.getFacilityId());

		connHandler.update();

		return;
	}

	public final void deleteContactType(ContactType contactType)
			throws DAOException {
		checkNull(contactType);
		ConnectionHandler connHandler;
		if (contactType.getEndDate() != null) {
			connHandler = new ConnectionHandler("ContactSQL.deleteContactType",
					false);
		} else {
			connHandler = new ConnectionHandler(
					"ContactSQL.deleteContactType1", false);
		}
		connHandler.setInteger(1, contactType.getContactId());
		connHandler.setString(2, contactType.getContactTypeCd());
		connHandler.setTimestamp(3, contactType.getStartDate());
		if (contactType.getEndDate() != null) {
			connHandler.setTimestamp(4, contactType.getEndDate());
			connHandler.setString(5, contactType.getFacilityId());
		} else {
			connHandler.setString(4, contactType.getFacilityId());
		}

		connHandler.remove();

		return;
	}

	@Override
	public void addContactNote(Integer contactId, Integer noteId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.addContactNote", false);

		connHandler.setInteger(1, contactId);
		connHandler.setInteger(2, noteId);

		connHandler.update();

		return;
	}

	@Override
	public ContactNote[] retrieveContactNotes(Integer contactId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.retrieveContactNotes", true);

		connHandler.setString(1, contactId);

		ArrayList<ContactNote> ret = connHandler
				.retrieveArray(ContactNote.class);

		return ret.toArray(new ContactNote[0]);
	}

	@Override
	public ContactType[] retrieveContactTypes(Integer contactId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveContactTypes", true);

		connHandler.setString(1, contactId);

		ArrayList<ContactType> ret = connHandler
				.retrieveArray(ContactType.class);

		return ret.toArray(new ContactType[0]);
	}

	@Override
	public ContactType[] retrieveStagingContactTypes(Integer contactId,
			String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveStagingContactTypes", true);

		connHandler.setString(1, contactId);
		connHandler.setString(2, facilityId);

		ArrayList<ContactType> ret = connHandler
				.retrieveArray(ContactType.class);

		return ret.toArray(new ContactType[0]);
	}

	@Override
	public final List<Contact> retrieveStagedContactsByFacility(
			String facilityId) throws DAOException {
		List<Contact> ret = new ArrayList<Contact>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		logger.debug("Staged Facility: " + facilityId);
		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ContactSQL.retrieveStagedContactsByFacility"));

			pStmt.setString(1, facilityId);

			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				Integer contactId = AbstractDAO.getInteger(rs, "contact_id");
				Contact tempContact = null;

				for (Contact tempC : ret) {
					if (tempC.getContactId().equals(contactId)) {
						tempContact = tempC;
						break;
					}
				}
				if (tempContact == null) {
					tempContact = new Contact();

					tempContact.populate(rs);
					tempContact.setFacilityId(facilityId);
					ret.add(tempContact);
				}
				if (rs.getString("contact_type_cd") != null) {
					ContactType tempType = new ContactType();

					tempType.populate(rs);

					tempContact.addContactType(tempType);
				}
			}
			rs.close();

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	@Override
	public final void removeContactTypes(Integer contactId, String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.deleteContactTypesByFacilityId", false);

		logger.debug("Removing contact (" + contactId
				+ ")'s contact types from facility: " + facilityId);

		connHandler.setInteger(1, contactId);
		connHandler.setString(2, facilityId);

		connHandler.remove();
	}

	@Override
	public void removeCompanyAuth(Contact contact) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.removeCompanyAuth", false);
		connHandler.setInteger(1, contact.getContactId());
		connHandler.remove();
	}

	@Override
	public void addCompanyAuth(Contact contact, Company company)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.addCompanyAuth", false);
		connHandler.setInteger(1, contact.getContactId());
		connHandler.setInteger(2, company.getCompanyId());
		connHandler.update();
	}

	@Override
	public FacilityList[] retrieveExcludedFacilities(Contact contact)
			throws DAOException {
		String statementSQL = loadSQL("ContactSQL.retrieveExcludedFacilities");

		// unlimited results
		setDefaultSearchLimit(-1);

		StringBuffer sortBy = new StringBuffer(" ORDER BY ff.facility_id ASC");

		statementSQL += sortBy.toString();

		logger.debug("statementSQL = " + statementSQL);

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL);
		connHandler.setInteger(1, contact.getContactId());

		ArrayList<FacilityList> ret = connHandler.retrieveArray(
				FacilityList.class, defaultSearchLimit);

		return ret.toArray(new FacilityList[0]);
	}

	@Override
	public void addFacilityNoAuth(Contact contact, FacilityList excludedFacility)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.addFacilityNoAuth", false);
		connHandler.setInteger(1, contact.getContactId());
		connHandler.setString(2, excludedFacility.getFacilityId());
		connHandler.updateNoClose();

	}

	@Override
	public void removeFacilityFromFacilityNoAuth(Contact contact,
			FacilityList authorizedFacility) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.removeFacilityFromFacilityNoAuth", false);
		connHandler.setInteger(1, contact.getContactId());
		connHandler.setString(2, authorizedFacility.getFacilityId());
		connHandler.remove();
	}

	@Override
	public void modifyExternalUsername(Contact contact) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.modifyExternalUsername", false);
		connHandler.setString(1, contact.getExternalUser().getUserName());
		connHandler.setInteger(2, contact.getContactId());
		connHandler.update();
	}

	@Override
	public void removeContactTypes(Integer contactId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.deleteContactTypes", false);

		connHandler.setInteger(1, contactId);

		connHandler.remove();

		return;
	}

	@Override
	public boolean modifyContactNotesAffiliation(Contact oldContact,
			Contact newContact) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.modifyContactNoteXrefs", false);

		connHandler.setInteger(1, newContact.getContactId());
		connHandler.setInteger(2, oldContact.getContactId());

		connHandler.updateNoCheck();

		return true;
	}

	@Override
	public boolean modifyPermitApplicationsAffilation(Contact oldContact,
			Contact newContact) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ContactSQL.modifyApplicationContact", false);

		connHandler.setInteger(1, newContact.getContactId());
		connHandler.setInteger(2, oldContact.getContactId());

		connHandler.updateNoCheck();

		return true;
	}

}

package us.oh.state.epa.stars2.database.dao.infrastructure;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.adhoc.DataGridCellDef;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.facility.PlssConversion;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountryDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DaemonInfo;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FacilityRoleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ForeignKeyReference;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.NewspaperDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.PredicateDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ReportAttribute;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ReportDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccLevel;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SecurityGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Shape;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.StateDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDefBase;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.OffsetTrackingContributingPollutantDef;
import us.oh.state.epa.stars2.def.OffsetTrackingNonAttainmentAreaDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.WrapnDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.database.dbObjects.infrastructure.DistrictDef;

@Repository
public class InfrastructureSQLDAO extends AbstractDAO implements
		InfrastructureDAO {

	private Logger logger = Logger.getLogger(InfrastructureSQLDAO.class);
	
	@Autowired private ConfigManager cfgMgr;
	
	/**
	 * @see retrieveSccLevelCodes(int, String)
	 */
	public final SccLevel[] retrieveSccLevelCodes(int level, String level1Desc, String level2Desc, String level3Desc, String Level4Desc, String sccId)
			throws DAOException {
		String statementSQL = null;
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer whereClause = new StringBuffer("");
		if (level == 1) {
			statementSQL = "SELECT DISTINCT(level1_dsc) AS scc_level_dsc from "
					+ addSchemaToTable("cm_scc");
		} else if (level == 2) {
			statementSQL = "SELECT DISTINCT(level2_dsc) AS scc_level_dsc from "
					+ addSchemaToTable("cm_scc");
		} else if (level == 3) {
			statementSQL = "SELECT DISTINCT(level3_dsc) AS scc_level_dsc from "
					+ addSchemaToTable("cm_scc");
		} else if (level == 4) {
			statementSQL = "SELECT DISTINCT(level4_dsc) AS scc_level_dsc from "
					+ addSchemaToTable("cm_scc");
		}
		
				
		if (level == 1) {
			whereClause.append(" WHERE ");
			whereClause.append(" inactive='N' ");
			whereClause.append(" ORDER BY LEVEL1_DSC");
		}


		if (level == 2) {
			whereClause.append(" WHERE LEVEL1_DSC LIKE ");
			whereClause.append("('");
			whereClause.append(level1Desc);
			whereClause.append("')");
			whereClause.append(" and ");
			whereClause.append(" inactive='N' ");
			whereClause.append(" ORDER BY LEVEL2_DSC");
		}
		
		if (level == 3) {
	
			whereClause.append(" WHERE LEVEL1_DSC LIKE ");
			whereClause.append("('");
			whereClause.append(level1Desc);
			whereClause.append("')");
			whereClause.append(" AND LEVEL2_DSC LIKE ");
			whereClause.append("('");
			whereClause.append(level2Desc);
			whereClause.append("')");
			whereClause.append(" and ");
			whereClause.append(" inactive='N' ");
			whereClause.append(" ORDER BY LEVEL3_DSC");
		}
		
		if (level == 4) {
	
			whereClause.append(" WHERE LEVEL1_DSC LIKE ");
			whereClause.append("('");
			whereClause.append(level1Desc);
			whereClause.append("')");
			whereClause.append(" AND LEVEL2_DSC LIKE ");
			whereClause.append("('");
			whereClause.append(level2Desc);
			whereClause.append("')");
			whereClause.append(" AND LEVEL3_DSC LIKE ");
			whereClause.append("('");
			whereClause.append(level3Desc);
			whereClause.append("')");
			whereClause.append(" and ");
			whereClause.append(" inactive='N' ");
			whereClause.append(" ORDER BY LEVEL4_DSC");
		}
		
		statementSQL += " " + whereClause.toString();
		
		connHandler.setSQLStringRaw(statementSQL);

		ArrayList<SccLevel> tempArray = connHandler
				.retrieveArray(SccLevel.class);

		return tempArray.toArray(new SccLevel[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveAllUseCases()
	 */
	public final PredicateDef[] retrievePredicates() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrievePredicates", true);

		ArrayList<PredicateDef> tempArray = connHandler.retrieveArray(PredicateDef.class);

		return tempArray.toArray(new PredicateDef[0]);
	}	
	
	public final UseCase[] retrieveAllUseCases() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveAllUseCases", true);

		ArrayList<UseCase> tempArray = connHandler.retrieveArray(UseCase.class);

		return tempArray.toArray(new UseCase[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveUseCases(String, int)
	 */
	public final LinkedHashMap<String, UseCase> retrieveUseCases(int userId)
			throws DAOException {
		LinkedHashMap<String, UseCase> ret = new LinkedHashMap<String, UseCase>();
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUseCaseByAppUser", true);

		connHandler.setInteger(1, userId);

		ArrayList<UseCase> tempArray = connHandler.retrieveArray(UseCase.class);

		for (int i = 0; i < tempArray.size(); i++) {
			UseCase tempUseCase = tempArray.get(i);

			ret.put(tempUseCase.getUseCase(), tempUseCase);
		}
		return ret;
	}

	/**
	 * @see InfrastructureDAO#retrieveUseCases(String , Integer)
	 */
	public final UseCase[] retrieveUseCases(Integer securityGroupId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUseCasesBySecGrpId", true);

		connHandler.setInteger(1, securityGroupId);

		ArrayList<UseCase> ret = connHandler.retrieveArray(UseCase.class);

		return ret.toArray(new UseCase[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveUserDefByLoginAndPassword(String, String)
	 */
	public final UserDefBase retrieveUserDefByLoginAndPassword(String login,
			String password) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUserByLoginAndPassword", true);

		connHandler.setString(1, login);
		connHandler.setString(2, password);

		return (UserDefBase) connHandler.retrieve(UserDefBase.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveUserDefByLogin(String)
	 */
	public final UserDefBase retrieveUserDefByLogin(String login)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUserByLogin", true);

		connHandler.setString(1, login);

		return (UserDefBase) connHandler.retrieve(UserDefBase.class);
	}
	
	/**
	 * @see InfrastructureDAO#retrieveUserDefByLogin(String)
	 */
   public final boolean retrieveUserByLogin(String login)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUserByNetworkLogin", true);

		connHandler.setString(1, login);
		if((String) connHandler.retrieveJavaObject(String.class)!=null){
		 return true;	
		}else{
			return false;
		}		
	}

	/**
	 * @see InfrastructureDAO#createInternalUser(String)
	 */
	public final boolean createInternalUser(String login) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createInternalUserByLogin", true);
		Integer id = nextSequenceVal("S_User_Id");

		connHandler.setInteger(1, id);
		connHandler.setString(2, login);
		connHandler.setString(3, 'Y');
		connHandler.setString(4, login);

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#retrieveUser(Integer)
	 */
	public final UserDef retrieveUserDef(Integer userId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUserDef", true);

		connHandler.setInteger(1, userId);

		return (UserDef) connHandler.retrieve(UserDef.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveUserDefByIdAndPassword(String, String)
	 */
	public final UserDefBase retrieveUserDefByIdAndPassword(int userId,
			String password) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUserByIdAndPassword", true);

		connHandler.setInteger(1, userId);
		connHandler.setString(2, password);

		return (UserDefBase) connHandler.retrieve(UserDefBase.class);
	}

	/**
	 * @see InfrastructureDAO#createUserDef(UserDef)
	 */
	public final UserDef createUserDef(UserDef userDef) throws DAOException {
		checkNull(userDef);

		UserDef ret = userDef;

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createUserDef", false);

		int i = 1;
		connHandler.setString(i++, ret.getNetworkLoginNm());
		connHandler.setString(i++, ret.getActiveInd());
		connHandler.setString(i++, ret.getPasswordVal());
		connHandler.setTimestamp(i++, ret.getPasswordExpDt());
		connHandler.setInteger(i++, ret.getManagerId());
		connHandler.setInteger(i++, ret.getUserId());
		connHandler.setInteger(i++, ret.getPositionNumber());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#modifyUserDef(UserDef)
	 */
	public final boolean modifyUserDefActive(UserDef userDef)
			throws DAOException {
		checkNull(userDef);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyUserDef", false);

		int i = 1;
		connHandler.setString(i++, userDef.getActiveInd());
		connHandler.setInteger(i++, userDef.getLastModified() + 1);
		connHandler.setInteger(i++, userDef.getUserId());
		connHandler.setInteger(i++, userDef.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#modifyUser(UserDef)
	 */
	public final boolean modifyUser(UserDef userDef) throws DAOException {
		checkNull(userDef);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyUser", false);
		connHandler.setString(1, userDef.getUserFirstNm());
		connHandler.setString(2, userDef.getUserLastNm());
		connHandler.setInteger(3, userDef.getPositionNumber());
		connHandler.setString(4, userDef.getActiveInd());
		connHandler.setInteger(5, userDef.getLastModified() + 1);
		connHandler.setInteger(6, userDef.getUserId());
		connHandler.setInteger(7, userDef.getLastModified());
		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#retrieveUserName(Integer)
	 */
	public final String retrieveUserName(Integer userId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUserName", true);

		connHandler.setInteger(1, userId);

		return (String) connHandler.retrieveJavaObject(String.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveUsersWithUseCase(String)
	 */
	public final Integer[] retrieveUsersWithUseCase(String useCase)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUsersWithUseCase", true);

		connHandler.setString(1, useCase);

		ArrayList<Integer> ret = connHandler
				.retrieveJavaObjectArray(Integer.class);

		return ret.toArray(new Integer[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveUserList()
	 */
	public final SimpleIdDef[] retrieveUserList(boolean excludeInactiveUsers)
			throws DAOException {
		String sqlSource = "InfrastructureSQL.retrieveActiveUserList";

		if (!excludeInactiveUsers) {
			sqlSource = "InfrastructureSQL.retrieveUserList";
		}

		ConnectionHandler connHandler = new ConnectionHandler(sqlSource, true);

		ArrayList<SimpleIdDef> ret = connHandler
				.retrieveArray(SimpleIdDef.class);

		return ret.toArray(new SimpleIdDef[0]);
	}

	public UserDef[] retrieveInactiveUserDefs() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveInactiveUserDefs", true);
		ArrayList<UserDef> ret = connHandler.retrieveArray(UserDef.class);
		return ret.toArray(new UserDef[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveUseCase(Integer)
	 */
	public final UseCase retrieveUseCase(String useCase) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUseCase", true);

		connHandler.setString(1, useCase);

		return (UseCase) connHandler.retrieve(UseCase.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveSecurityGroup(Integer securityGroupId)
	 */
	public final SecurityGroup retrieveSecurityGroup(Integer securityGroupId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveSecurityGroup", true);

		connHandler.setInteger(1, securityGroupId);

		return (SecurityGroup) connHandler.retrieve(SecurityGroup.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveSecurityGroups()
	 */
	public final SecurityGroup[] retrieveSecurityGroups() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveSecurityGroups", true);

		ArrayList<SecurityGroup> ret = connHandler
				.retrieveArray(SecurityGroup.class);

		return ret.toArray(new SecurityGroup[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveContact(int contactId)
	 */
	public final Contact retrieveContact(int contactId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveContact", true);

		connHandler.setInteger(1, contactId);

		return (Contact) connHandler.retrieve(Contact.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveAddress(int)
	 */
	public final Address retrieveAddress(int id) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveAddress", true);

		connHandler.setInteger(1, id);

		return (Address) connHandler.retrieve(Address.class);
	}

	public final PlssConversion getPlssByLatLong(String latitude,
			String longitude) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.getPlssByLatLong", false);

		int index = 1;
		connHandler.setString(index++, latitude);
		connHandler.setString(index++, longitude);
		connHandler.setString(index++, latitude);
		connHandler.setString(index++, latitude);
		connHandler.setString(index++, longitude);
		connHandler.setString(index++, latitude);

		return (PlssConversion) connHandler.retrieve(PlssConversion.class);
	}

	public final PlssConversion getLatLongByPlss(String section,
			String township, String range) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.getLatLongByPlss", false);

		int index = 1;
		connHandler.setString(index++, section);
		connHandler.setString(index++, township);
		connHandler.setString(index++, range);

		return (PlssConversion) connHandler.retrieve(PlssConversion.class);
	}

	public final CountyDef getCountyByLatLong(String latitude, String longitude)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.getCountyByLatLong", false);

		int index = 1;
		connHandler.setString(index++, latitude);
		connHandler.setString(index++, longitude);

		return (CountyDef) connHandler.retrieve(CountyDef.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveCounty(String)
	 */
	public final CountyDef retrieveCounty(String countyCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveCounty", true);

		connHandler.setString(1, countyCd);

		return (CountyDef) connHandler.retrieve(CountyDef.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveNewspaperDef(String)
	 */
	public NewspaperDef retrieveNewspaper(String countyCd) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveNewspaper", true);

		connHandler.setString(1, countyCd);

		return (NewspaperDef) connHandler.retrieve(NewspaperDef.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveState(String)
	 */
	public final StateDef retrieveState(String stateCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveState", true);

		connHandler.setString(1, stateCd);

		return (StateDef) connHandler.retrieve(StateDef.class);
	}

	/**
	 * @see InfrastructureDAO#createContact(Contact)
	 */
	public final Contact createContact(Contact contact) throws DAOException {
		checkNull(contact);

		Contact ret = contact;
		/*
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createContact", false);

		Integer id = nextSequenceVal("S_Contact_Id", contact.getContactId());
		connHandler.setInteger(1, id);
		connHandler.setString(2, contact.getOperCompanyName());  // obsolete column
		connHandler.setTimestamp(3, contact.getStartDate());
		connHandler.setTimestamp(4, contact.getEndDate());
		connHandler.setString(5, contact.getTitleCd());
		connHandler.setInteger(6, contact.getAddressId());
		connHandler.setString(7, contact.getFirstNm());
		connHandler.setString(8, contact.getMiddleNm());
		connHandler.setString(9, contact.getLastNm());
		connHandler.setString(10, contact.getSuffixCd());
		connHandler.setString(11, contact.getPhoneNo());
		connHandler.setString(12, contact.getPhoneExtensionVal());
		connHandler.setString(13, contact.getSecondaryPhoneNo());
		connHandler.setString(14, contact.getSecondaryExtensionVal());
		connHandler.setString(15, contact.getMobilePhoneNo());
		connHandler.setString(16, contact.getFaxNo());
		connHandler.setString(17, contact.getPagerNo());
		connHandler.setString(18, contact.getPagerPinNo());
		connHandler.setString(19, contact.getEmailAddressTxt());
		connHandler.setString(20, contact.getEmailPagerAddress());
		connHandler.setInteger(21, contact.getMaxEmailPagerCharsNum());
		connHandler.setString(22, contact.getCompanyTitle());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setContactId(id);
		ret.setLastModified(1);
		 */
		return ret;
	}

	/**
	 * @see InfrastructureDAO#modifyContact(Contact)
	 */
	public final boolean modifyContact(Contact contact) throws DAOException {
		checkNull(contact);

		/*
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyContact", false);

		connHandler.setTimestamp(1, contact.getEndDate());
		connHandler.setString(2, contact.getTitleCd());
		connHandler.setInteger(3, contact.getAddressId());
		connHandler.setString(4, contact.getFirstNm());
		connHandler.setString(5, contact.getMiddleNm());
		connHandler.setString(6, contact.getLastNm());
		connHandler.setString(7, contact.getSuffixCd());
		connHandler.setString(8, contact.getPhoneNo());
		connHandler.setString(9, contact.getPhoneExtensionVal());
		connHandler.setString(10, contact.getSecondaryPhoneNo());
		connHandler.setString(11, contact.getSecondaryExtensionVal());
		connHandler.setString(12, contact.getMobilePhoneNo());
		connHandler.setString(13, contact.getFaxNo());
		connHandler.setString(14, contact.getPagerNo());
		connHandler.setString(15, contact.getPagerPinNo());
		connHandler.setString(16, contact.getEmailAddressTxt());
		connHandler.setString(17, contact.getEmailPagerAddress());
		connHandler.setInteger(18, contact.getMaxEmailPagerCharsNum());
		connHandler.setInteger(19, contact.getLastModified() + 1);
		connHandler.setString(20, contact.getCompanyTitle());
		connHandler.setString(21, contact.getOperCompanyName());   // obsolete column
		connHandler.setInteger(22, contact.getContactId());
		connHandler.setInteger(23, contact.getLastModified());

		return connHandler.update();
		*/
		return false;
	}

	/**
	 * @see InfrastructureDAO#deleteContact(Integer)
	 */
	public void deleteContact(Contact c) throws DAOException {
		if (c == null)
			return;
		removeRows("cm_contact", "contact_id", c.getContactId());
		removeRows("cm_address", "address_id", c.getAddress().getAddressId());
		return;
	}

	/**
	 * @see InfrastructureDAO#deleteContact(Integer)
	 */
	public void deleteStagingContact(Contact c) throws DAOException {
		if (c == null)
			return;
		ConnectionHandler contactHandler = new ConnectionHandler(
				"ContactSQL.removeStagingContact", false);
		contactHandler.setInteger(1, c.getContactId());
		contactHandler.setString(2, c.getFacilityId());
		contactHandler.remove();

		// delete address
		removeRows("cm_address", "address_id", c.getAddress().getAddressId());
		return;
	}

	/**
	 * @see InfrastructureDAO#addContactType(Integer, String, Timestamp)
	 */
	public final void addContactType(Integer contactId, String contactTypeCd,
			Timestamp startDate) throws DAOException {
		checkNull(contactId);
		checkNull(contactTypeCd);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.addContactType", false);

		connHandler.setInteger(1, contactId);
		connHandler.setString(2, contactTypeCd);
		if (startDate == null) {
			startDate = new Timestamp(System.currentTimeMillis());
		}
		if (startDate != null)
			startDate.setNanos(0);
		connHandler.setTimestamp(3, startDate);
		connHandler.update();

		return;
	}

	/**
	 * @see InfrastructureDAO#addContactType(Integer, ContactType)
	 */
	public final void addContactType(Integer contactId, ContactType contactType)
			throws DAOException {
		checkNull(contactId);
		checkNull(contactType);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.addContactType1", false);

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

		connHandler.update();

		return;
	}

	/**
	 * @see InfrastructureDAO#deprecateContactType(ContactType)
	 */
	public final void deprecateContactType(ContactType contactType)
			throws DAOException {
		checkNull(contactType);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.deprecateContactType", false);

		if (contactType.getEndDate() != null)
			contactType.getEndDate().setNanos(0);
		connHandler.setTimestamp(1, contactType.getEndDate());
		connHandler.setInteger(2, contactType.getContactId());
		connHandler.setString(3, contactType.getContactTypeCd());
		if (contactType.getStartDate() != null)
			contactType.getStartDate().setNanos(0);
		connHandler.setTimestamp(4, contactType.getStartDate());

		connHandler.update();

		return;
	}

	/**
	 * @see InfrastructureDAO#modifyContactType(ContactType)
	 */
	public final void modifyContactType(ContactType contactType)
			throws DAOException {
		checkNull(contactType);
		if (contactType.getStartDate() != null)
			contactType.getStartDate().setNanos(0);
		if (contactType.getEndDate() != null)
			contactType.getEndDate().setNanos(0);
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyContactType", false);
		connHandler.setTimestamp(1, contactType.getStartDate());
		connHandler.setTimestamp(2, contactType.getEndDate());
		connHandler.setInteger(3, contactType.getLastModified() + 1);
		connHandler.setInteger(4, contactType.getContactId());
		connHandler.setString(5, contactType.getContactTypeCd());
		connHandler.setInteger(6, contactType.getLastModified());

		connHandler.update();

		return;
	}

	/**
	 * @see InfrastructureDAO#deleteContactType(ContactType)
	 */
	public final void deleteContactType(ContactType contactType)
			throws DAOException {
		checkNull(contactType);
		ConnectionHandler connHandler;
		if (contactType.getEndDate() != null) {
			connHandler = new ConnectionHandler(
					"InfrastructureSQL.deleteContactType", false);
		} else {
			connHandler = new ConnectionHandler(
					"InfrastructureSQL.deleteContactType1", false);
		}
		connHandler.setInteger(1, contactType.getContactId());
		connHandler.setString(2, contactType.getContactTypeCd());
		connHandler.setTimestamp(3, contactType.getStartDate());
		if (contactType.getEndDate() != null) {
			connHandler.setTimestamp(4, contactType.getEndDate());
		}

		connHandler.remove();

		return;
	}

	/**
	 * @see InfrastructureDAO#retrieveContactTypes(int)
	 */
	public final List<ContactType> retrieveContactTypes(int contactId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveContactTypes", true);

		connHandler.setInteger(1, contactId);

		ArrayList<ContactType> temp = connHandler
				.retrieveArray(ContactType.class);

		ArrayList<ContactType> ret = new ArrayList<ContactType>();

		for (ContactType tempType : temp.toArray(new ContactType[0])) {
			ret.add(tempType);
		}

		return ret;
	}

	public final boolean updateAddressEndDate(int fp_id) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.updateAddressEndDates", false);

		connHandler.setString(1, fp_id);

		return connHandler.updateNoCheck();
	}

	/**
	 * @see InfrastructureDAO#createAddress(Address)
	 */
	public final Address createAddress(Address address) throws DAOException {
		checkNull(address);

		Address ret = address;
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createAddress", false);

		Integer id = nextSequenceVal("S_Address_Id", address.getAddressId());
		int i = 1;

		connHandler.setInteger(i++, id);
		connHandler.setString(i++, ret.getState());
		connHandler.setString(i++, ret.getCountryCd());
		connHandler.setString(i++, ret.getCountyCd());
		connHandler.setString(i++, ret.getAddressLine1());
		connHandler.setString(i++, ret.getAddressLine2());
		connHandler.setString(i++, ret.getCityName());
		connHandler.setString(i++, ret.getZipCode5());
		connHandler.setString(i++, ret.getZipCode4());
		connHandler.setTimestamp(i++, ret.getBeginDate());
		connHandler.setTimestamp(i++, ret.getEndDate());
		connHandler.setFloat(
				i++,
				(ret.getLatitude() == null) ? null : Float.parseFloat(ret
						.getLatitude()));
		connHandler.setFloat(
				i++,
				(ret.getLongitude() == null) ? null : Float.parseFloat(ret
						.getLongitude()));
		connHandler.setString(i++, ret.getQuarterQuarter());
		connHandler.setString(i++, ret.getQuarter());
		connHandler.setString(i++, ret.getSection());
		connHandler.setString(i++, ret.getTownship());
		connHandler.setString(i++, ret.getRange());
		connHandler.setString(i++, ret.getDistrictCd());
		connHandler.setString(i++, ret.getIndianReservationCd());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setAddressId(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#modifyAddress(Address)
	 */
	public final boolean modifyAddress(Address address) throws DAOException {
		checkNull(address);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyAddress", false);

		logger.debug("Address last modified: " + address.getLastModified());

		int i = 1;
		connHandler.setString(i++, address.getState());
		connHandler.setString(i++, address.getCountryCd());
		connHandler.setString(i++, address.getCountyCd());
		connHandler.setString(i++, address.getAddressLine1());
		connHandler.setString(i++, address.getAddressLine2());
		connHandler.setString(i++, address.getCityName());
		connHandler.setString(i++, address.getZipCode5());
		connHandler.setString(i++, address.getZipCode4());
		connHandler.setTimestamp(i++, address.getBeginDate());
		connHandler.setTimestamp(i++, address.getEndDate());
		connHandler.setInteger(i++, address.getLastModified() + 1);
		connHandler.setString(i++, address.getLatitude());
		connHandler.setString(i++, address.getLongitude());
		connHandler.setString(i++, address.getQuarterQuarter());
		connHandler.setString(i++, address.getQuarter());
		connHandler.setString(i++, address.getSection());
		connHandler.setString(i++, address.getTownship());
		connHandler.setString(i++, address.getRange());
		connHandler.setString(i++, address.getDistrictCd());
		connHandler.setString(i++, address.getIndianReservationCd());
		connHandler.setInteger(i++, address.getAddressId());
		connHandler.setInteger(i++, address.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#removeAddress(Integer)
	 */
	public final void removeAddress(Integer addressId) throws DAOException {
		checkNull(addressId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.removeAddress", false);

		connHandler.setInteger(1, addressId);

		connHandler.remove();
	}

	/**
	 * @see InfrastructureDAO#removeContact(Integer)
	 */
	public final void removeContact(Integer contactId) throws DAOException {
		checkNull(contactId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.removeContact", false);

		connHandler.setInteger(1, contactId);

		connHandler.remove();
	}

	/**
	 * @see InfrastructureDAO#modifyCounty (CountyDef)
	 */
	public final boolean modifyCounty(CountyDef countyDef) throws DAOException {
		checkNull(countyDef);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyCounty", false);

		int i = 0;
		connHandler.setString(++i, countyDef.getStateCd());
		connHandler.setString(++i, countyDef.getCountyNm());
		connHandler.setString(++i, countyDef.getDoLaaCd());
		connHandler.setInteger(++i, countyDef.getLastModified() + 1);
		connHandler.setString(++i, countyDef.getCountyCd());
		connHandler.setInteger(++i, countyDef.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#modifyCountry (CountryDef)
	 */
	public final boolean modifyCountry(CountryDef countryDef)
			throws DAOException {
		checkNull(countryDef);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyCountry", false);

		connHandler.setString(1, countryDef.getCountryNm());
		connHandler.setInteger(2, countryDef.getLastModified() + 1);
		connHandler.setString(3, countryDef.getCountryCd());
		connHandler.setInteger(4, countryDef.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#modifyState (StateDef)
	 */
	public final boolean modifyState(StateDef stateDef) throws DAOException {
		checkNull(stateDef);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyState", false);

		connHandler.setString(1, stateDef.getStateNm());
		connHandler.setString(2, stateDef.getCountryCd());
		connHandler.setInteger(3, stateDef.getLastModified() + 1);
		connHandler.setString(4, stateDef.getStateCd());
		connHandler.setInteger(5, stateDef.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#retrieveStates()
	 */
	public final StateDef[] retrieveStates() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveStates", true);

		ArrayList<StateDef> ret = connHandler.retrieveArray(StateDef.class);

		return ret.toArray(new StateDef[0]);
	}

	public final DistrictDef[] retrieveDistricts() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveDistricts", true);

		ArrayList<DistrictDef> ret = connHandler
				.retrieveArray(DistrictDef.class);

		return ret.toArray(new DistrictDef[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveCounties()
	 */
	public final CountyDef[] retrieveCounties() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveCounties", true);

		ArrayList<CountyDef> ret = connHandler.retrieveArray(CountyDef.class);

		return ret.toArray(new CountyDef[0]);
	}
	
//	/**
//	 * @see InfrastructureDAO#retrieveCities()
//	 */
//	public final CityDef[] retrieveCities() throws DAOException {
//		ConnectionHandler connHandler = new ConnectionHandler(
//				"InfrastructureSQL.retrieveCities", true);
//
//		ArrayList<CityDef> ret = connHandler.retrieveArray(CityDef.class);
//
//		return ret.toArray(new CityDef[0]);
//	}

	/**
	 * @see InfrastructureDAO#retrieveCountries()
	 */
	public final CountryDef[] retrieveCountries() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveCountries", true);

		ArrayList<CountryDef> ret = connHandler.retrieveArray(CountryDef.class);

		return ret.toArray(new CountryDef[0]);
	}


	/**
	 * @see InfrastructureDAO#retrieveDoLaas()
	 */
	public final DoLaaDef[] retrieveDoLaas() throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveDoLaas", true);

		ArrayList<DoLaaDef> ret = connHandler.retrieveArray(DoLaaDef.class);

		return ret.toArray(new DoLaaDef[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveDoLaa()
	 */
	public final DoLaaDef retrieveDoLaa(String doLaaCd) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveDoLaa", true);

		connHandler.setString(1, doLaaCd);

		return (DoLaaDef) connHandler.retrieve(DoLaaDef.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveCountry(String)
	 */
	public final CountryDef retrieveCountry(String countryCd)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveCountry", true);

		connHandler.setString(1, countryCd);

		return (CountryDef) connHandler.retrieve(CountryDef.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveNote(int)
	 */
	public final Note retrieveNote(int noteId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveNote", true);

		connHandler.setInteger(1, noteId);

		return (Note) connHandler.retrieve(Note.class);
	}

	/**
	 * @see InfrastructureDAO#createSecurityGroup(SecurityGroup)
	 */
	public final SecurityGroup createSecurityGroup(SecurityGroup newGroup)
			throws DAOException {

		checkNull(newGroup);

		SecurityGroup ret = newGroup;

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createSecurityGroup", false);

		Integer id = nextSequenceVal("S_Security_Group_Id");

		connHandler.setInteger(1, id);
		connHandler.setString(2, newGroup.getSecurityGroupName());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setSecurityGroupId(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#createUseCase(UseCase)
	 */
	public final UseCase createUseCase(UseCase newUseCase) throws DAOException {
		checkNull(newUseCase);

		UseCase ret = newUseCase;

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createUseCase", false);

		Integer id = nextSequenceVal("S_Security_Id");

		connHandler.setInteger(1, id);
		connHandler.setInteger(2, newUseCase.getParentId());
		connHandler.setString(3, newUseCase.getUseCase());
		connHandler.setString(4, newUseCase.getUseCaseName());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setSecurityId(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#createNote(Note newNote)
	 */
	public final Note createNote(Note newNote) throws DAOException {
		checkNull(newNote);

		Note ret = newNote;

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createNote", false);
		Integer id = nextSequenceVal("S_Note_Id");

		connHandler.setInteger(1, id);
		connHandler.setString(2, newNote.getNoteTypeCd());
		connHandler.setInteger(3, newNote.getUserId());
		connHandler.setString(4, newNote.getNoteTxt());
		connHandler.setTimestamp(5, newNote.getDateEntered());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setNoteId(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#modifyNote(Note note)
	 */
	public final boolean modifyNote(Note note) throws DAOException {
		checkNull(note);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyNote", false);

		connHandler.setString(1, note.getNoteTypeCd());
		connHandler.setString(2, note.getNoteTxt());
		connHandler.setInteger(3, note.getLastModified() + 1);
		connHandler.setInteger(4, note.getNoteId());
		connHandler.setInteger(5, note.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#removeNote(int)
	 */
	public final boolean removeNote(int noteId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.removeNote", false);
		connHandler.setInteger(1, noteId);
		return connHandler.remove();
	}

	/**
	 * @see InfrastructureDAO#retrieveDaemonInfo(String)
	 */
	public final DaemonInfo retrieveDaemonInfo(String daemonCode)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveDaemonInfoByCode", true);

		connHandler.setString(1, daemonCode);

		return (DaemonInfo) connHandler.retrieve(DaemonInfo.class);
	}

	/**
	 * @see InfrastructureDAO#createDaemonInfo(DaemonInfo)
	 */
	public final DaemonInfo createDaemonInfo(DaemonInfo info)
			throws DAOException {
		checkNull(info);

		DaemonInfo ret = info;
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createDaemonInfo", false);

		connHandler.setString(1, ret.getDaemonCode());
		connHandler.setString(2, ret.getHostName());
		connHandler.setInteger(3, ret.getPortNumber());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#modifyDaemonInfo(DaemonInfo)
	 */
	public final boolean modifyDaemonInfo(DaemonInfo info) throws DAOException {
		checkNull(info);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyDaemonInfo", false);

		
		connHandler.setInteger(1, info.getPortNumber());
		connHandler.setInteger(2, info.getLastModified() + 1);
		connHandler.setString(3, info.getDaemonCode());
		connHandler.setString(4, info.getHostName());
		connHandler.setInteger(5, info.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#removeSecurityGroup(SecurityGroup)
	 */
	public final void removeSecurityGroup(SecurityGroup group)
			throws DAOException {
		checkNull(group);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.removeSecurityGroup", false);

		connHandler.setInteger(1, group.getSecurityGroupId());
		connHandler.setInteger(2, group.getLastModified());

		connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#removeUsersFromGroup(Integer)
	 */
	public final void removeUsersFromGroup(Integer groupId) throws DAOException {
		removeRows("cm_user_group_xref", "security_group_id", groupId);
	}

	/**
	 * @see InfrastructureDAO#removeAllUserRoles(Integer)
	 */
	public final void removeAllUserRoles(Integer userId) throws DAOException {
		removeRows("cm_user_group_xref", "user_id", userId);
	}

	/**
	 * @see InfrastructureDAO#removeAllRoleUseCases(Integer)
	 */
	public final void removeAllRoleUseCases(Integer securityGroupId)
			throws DAOException {
		removeRows("cm_security_user_group_xref", "security_group_id",
				securityGroupId);
	}

	/**
	 * @see InfrastructureDAO#addUserRole(Integer, Integer)
	 */
	public final void addUserRole(Integer userId, Integer securityGroupId)
			throws DAOException {
		checkNull(userId);
		checkNull(securityGroupId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.addUserRole", false);

		connHandler.setInteger(1, userId);
		connHandler.setInteger(2, securityGroupId);

		connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#addUseCase(Integer, Integer)
	 */
	public final void addUseCase(Integer securityId, Integer securityGroupId)
			throws DAOException {
		checkNull(securityId);
		checkNull(securityGroupId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.addUseCase", false);

		connHandler.setInteger(1, securityGroupId);
		connHandler.setInteger(2, securityId);

		connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#retrieveDescAndId(String)
	 */
	public final SimpleIdDef[] retrieveDescAndId(String sqlLoadString)
			throws DAOException {

		checkNull(sqlLoadString);

		ConnectionHandler connHandler = new ConnectionHandler(sqlLoadString,
				true);

		ArrayList<SimpleIdDef> ret = connHandler
				.retrieveArray(SimpleIdDef.class);

		return ret.toArray(new SimpleIdDef[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveReportDefs()
	 */
	public final ReportDef[] retrieveReportDefs() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveReportDefs", true);

		ArrayList<ReportDef> ret = connHandler.retrieveArray(ReportDef.class);

		return ret.toArray(new ReportDef[0]);
	}

	/**
	 * @see InfrastructureDAO#createReport(ReportDef)
	 */
	public final ReportDef createReport(ReportDef report) throws DAOException {
		checkNull(report);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createReportDef", false);

		ReportDef ret = report;

		Integer reportId = nextSequenceVal("S_Report_Id");

		connHandler.setInteger(1, reportId);
		connHandler.setString(2, report.getName());
		connHandler.setString(3, report.getGroupNm());
		connHandler.setString(4, report.getJasperDefFile());
		connHandler.setInteger(5, report.getReportDocumentId());
		

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setId(reportId);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#addReportAttribute(int, String)
	 */
	public final void addReportAttribute(int reportId, ReportAttribute attr)
			throws DAOException {
		checkNull(attr);
		checkNull(attr.getCode());

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.addReportAttribute", false);

		int i = 1;

		connHandler.setInteger(i++, reportId);
		connHandler.setString(i++, attr.getCode());
		connHandler.setString(i++, attr.getDescription());
		connHandler.setString(i++, attr.getType());
		connHandler.setString(i++, attr.getValue());

		connHandler.update();

		return;
	}

	/**
	 * @see InfrastructureDAO#retrieveBulkDefs()
	 */
	public final BulkDef[] retrieveBulkDefs(String bulkMenu)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveBulkDefs", true);

		connHandler.setString(1, bulkMenu);

		ArrayList<BulkDef> ret = connHandler.retrieveArray(BulkDef.class);

		return ret.toArray(new BulkDef[0]);
	}

	/**
	 * @see InfrastructureDAO#createBulkOperation(BulkDef)
	 */
	public final BulkDef createBulkDef(BulkDef bulkOperation)
			throws DAOException {
		checkNull(bulkOperation);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createBulkDef", false);

		BulkDef ret = bulkOperation;

		Integer bulkId = nextSequenceVal("S_Bulk_Id");

		connHandler.setInteger(1, bulkId);
		connHandler.setString(2, bulkOperation.getName());
		connHandler.setString(3, bulkOperation.getGroupNm());
		connHandler.setString(4, bulkOperation.getMenu());
		connHandler.setString(5, bulkOperation.getDsc());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setBulkId(bulkId);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#addBulkAttribute(int, String)
	 */
	public final void addBulkAttribute(int bulkId, String attrCd)
			throws DAOException {
		checkNull(attrCd);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.addBulkAttribute", false);

		connHandler.setInteger(1, bulkId);
		connHandler.setString(2, attrCd);

		connHandler.update();

		return;
	}

	/**
	 * @see InfrastructureDAO#modifyBulkDef(BulkDef)
	 */
	public final boolean modifyBulkDef(BulkDef bulkdef) throws DAOException {
		checkNull(bulkdef);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyBulkDef", false);

		connHandler.setString(1, bulkdef.getName());
		connHandler.setString(2, bulkdef.getGroupNm());
		connHandler.setString(3, bulkdef.getMenu());
		connHandler.setString(4, bulkdef.getDsc());
		connHandler.setInteger(5, bulkdef.getLastModified() + 1);
		connHandler.setInteger(6, bulkdef.getBulkId());
		connHandler.setInteger(7, bulkdef.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#removeReportAttributes(int)
	 */
	public final void removeReportAttributes(int reportId) throws DAOException {
		super.removeRows("cm_report_attributes_xref", "report_id", reportId);
	}

	/**
	 * @see InfrastructureDAO#modifyReport(ReportDef)
	 */
	public final boolean modifyReport(ReportDef report) throws DAOException {

		checkNull(report);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyReportDef", false);

		int i = 1;
		connHandler.setString(i++, report.getName());
		connHandler.setString(i++, report.getGroupNm());
		connHandler.setString(i++, report.getJasperDefFile());
		connHandler.setString(i++, report.getReportDocumentId());
		connHandler.setInteger(i++, report.getLastModified() + 1);

		connHandler.setInteger(i++, report.getId());
		connHandler.setInteger(i++, report.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#removeReport(ReportDef)
	 */
	public final void removeReport(ReportDef report) throws DAOException {

		checkNull(report);
		removeRows("cm_report_def", "report_id", report.getId());

		return;
	}

	/**
	 * @see InfrastructureDAO#retrieveReport(String, Map)
	 */
	public final JasperPrint retrieveReport(String jasperReportFileNm,
			Map<String, Object> reportParms) throws DAOException {

		Connection conn = null;
		JasperPrint ret = null;
		JasperReport report = null;
		boolean preCompiled = true;

		if (jasperReportFileNm != null) {
			try {
				if (jasperReportFileNm.endsWith(".jrxml")) {
					JasperDesign design = JRXmlLoader.load(jasperReportFileNm);
					report = JasperCompileManager.compileReport(design);
					preCompiled = false;
				}

				conn = getConnection();

				if (reportParms != null) {
					if (preCompiled) {
						ret = JasperFillManager.fillReport(jasperReportFileNm,
								reportParms, conn);
					} else {
						ret = JasperFillManager.fillReport(report, reportParms,
								conn);
					}
				} else {
					logger.error("Report parms is null.");
				}
			} catch (JRException jre) {
				logger.error(jre.getMessage(), jre);
			} finally {
				handleClosing(conn);
			}

		}

		return ret;
	}

	/**
	 * @see InfrastructureDAO#retrieveReport(int)
	 */
	public final ReportDef retrieveReport(int reportId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveReportDef", false);

		connHandler.setInteger(1, reportId);

		return (ReportDef) connHandler.retrieve(ReportDef.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveBulkDef(int)
	 */
	public final BulkDef retrieveBulkDef(int reportId) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveBulkDef", false);

		connHandler.setInteger(1, reportId);

		return (BulkDef) connHandler.retrieve(BulkDef.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveSimpleDefs(String, String, String, String)
	 */
	public final SimpleDef[] retrieveSimpleDefs(String dbTable,
			String dbCodeColumn, String dbDescColumn,
			String dbDeprecatedColumn, String sortByColumn) throws DAOException {

		checkNull(dbTable);
		checkNull(dbCodeColumn);
		checkNull(dbDescColumn);

		String sql = "SELECT " + dbCodeColumn + " AS code, " + dbDescColumn
				+ " AS description, ";

		if (dbDeprecatedColumn != null) {
			sql += dbDeprecatedColumn + " AS deprecated, ";
		} else {
			sql += "'N' AS deprecated, ";
		}
		
		sql += " last_modified" + " FROM " + addSchemaToTable(dbTable)
				+ " ORDER BY ";

		if (sortByColumn != null) {
			sql += sortByColumn;
		} else {
			sql += dbDescColumn;
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);

		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveSimpleDefs(String, String, String, String)
	 */
	public final SimpleDef[] retrieveSimpleDefs(String dbTable,
			String dbCodeColumn, String dbDescColumn,
			String dbDeprecatedColumn, String sortByColumn, String whereColumn,
			String whereValue) throws DAOException {

		checkNull(dbTable);
		checkNull(dbCodeColumn);
		checkNull(dbDescColumn);
		

		String sql = "SELECT " + dbCodeColumn + " AS code, " + dbDescColumn
				+ " AS description, " + whereColumn + ", ";

		if (dbDeprecatedColumn != null) {
			sql += dbDeprecatedColumn + " AS deprecated, ";
		} else {
			sql += "'N' AS deprecated, ";
		}
		
		sql += " last_modified" + " FROM " + addSchemaToTable(dbTable);

		sql += " WHERE " + whereColumn + " = '" + whereValue + "'"
				+ " ORDER BY ";

		if (sortByColumn != null) {
			sql += sortByColumn;
		} else {
			sql += dbDescColumn;
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);

		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveSimpleIdDefs(String, String, String,
	 *      String)
	 */
	public final SimpleIdDef[] retrieveSimpleIdDefs(String dbTable,
			String dbIdColumn, String dbDescColumn, String dbDeprecatedColumn,
			String sortByColumn) throws DAOException {

		checkNull(dbTable);
		checkNull(dbIdColumn);
		checkNull(dbDescColumn);
		
		String sql = "SELECT " + dbIdColumn + " AS id, " + dbDescColumn
				+ " AS description, ";

		if (dbDeprecatedColumn != null) {
			sql += dbDeprecatedColumn + " AS deprecated, ";
		} else {
			sql += "'N' AS deprecated, ";
		}
		
		sql += " last_modified" + " FROM " + addSchemaToTable(dbTable)
				+ " ORDER BY ";

		if (sortByColumn != null) {
			sql += sortByColumn;
		} else {
			sql += dbDescColumn;
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);

		ArrayList<SimpleIdDef> ret = connHandler
				.retrieveArray(SimpleIdDef.class);

		return ret.toArray(new SimpleIdDef[0]);
	}
//
//	private String getSchema() {
//		return CommonConst.READONLY_SCHEMA; //default dbo schema
//	}

//	private String getSchema() {
//		String schema = "";
//		if (DAOFactory.isSchemaSubstitution()) {
//			try {
//				DAOFactory daoFactory = (DAOFactory) CompMgr
//						.newInstance("app.DAOFactory");
//				schema = daoFactory
//						.getSchemaQualifier(CommonConst.READONLY_SCHEMA);
//			} catch (UnableToStartException utse) {
//				logger.error(utse.getMessage(), utse);
//			}
//		}
//		return schema;
//	}

	/**
	 * @see InfrastructureDAO#modifyColumn(String, String, String, String,
	 *      String, String, String)
	 */
	public final boolean modifyColumn(String dbTable, String keyColumn,
			String keyColumnType, String keyValue, String column,
			String columnType, String columnValue) throws DAOException {
		logger.debug("updating: " + dbTable + "." + keyColumn);
		if (column == null) {
			logger.error("null column");
		}
		if (columnValue == null) {
			logger.error("null value");
		}
		logger.debug(column + " to " + columnValue);
		
		String sql = "UPDATE " + getSchemaQualifer() + dbTable + " SET " + column
				+ "=?" + " WHERE " + keyColumn + "=?";
		logger.debug(sql);
		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);
		connHandler.setString(1, columnValue);
		connHandler.setString(2, keyValue);
		boolean resp = connHandler.update();
		logger.debug(resp);
		return resp;
	}

	/**
	 * @see InfrastructureDAO#createSimpleDef(String, String , String, String,
	 *      SimpleDef)
	 */
	public final SimpleDef createSimpleDef(String dbTable, String dbCodeColumn,
			String dbDescColumn, SimpleDef sd) throws DAOException {
		checkNull(dbTable);
		checkNull(dbCodeColumn);
		checkNull(dbDescColumn);

		String sql = "INSERT INTO " + addSchemaToTable(dbTable) + " ("
				+ dbCodeColumn + "," + dbDescColumn + ") VALUES (?,?)";

		SimpleDef ret = new SimpleDef(sd);

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);
		connHandler.setString(1, sd.getCode());
		connHandler.setString(2, sd.getDescription());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#modifySimpleDef(String, String, String, String,
	 *      SimpleDef)
	 */
	public final boolean modifySimpleDef(String dbTable, String dbCodeColumn,
			String dbDescColumn, String dbDeprecatedColumn, SimpleDef sd)
			throws DAOException {
		checkNull(dbTable);
		checkNull(dbCodeColumn);
		checkNull(dbDescColumn);

		String sql = "UPDATE " + dbTable + " SET " + dbDescColumn + "=?,"
				+ dbDeprecatedColumn + "=? WHERE " + dbCodeColumn
				+ "=? and last_modified=?";

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(sql);
		connHandler.setString(1, sd.getDescription());
		connHandler.setBoolean(2, sd.isDeprecated());
		connHandler.setString(3, sd.getCode());
		connHandler.setInteger(4, sd.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see InfrastructureDAO#retrieveDescAndCd(String)
	 */
	public final SimpleDef[] retrieveDescAndCd(String sqlLoadString)
			throws DAOException {
		checkNull(sqlLoadString);

		ConnectionHandler connHandler = new ConnectionHandler(sqlLoadString,
				true);
		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);
		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveBaseDB(String, Class)
	 */
	public final <T extends BaseDB> BaseDef[] retrieveBaseDB(
			String sqlLoadString, Class<T> objectToRetrieve)
			throws DAOException {

		checkNull(sqlLoadString);
		checkNull(objectToRetrieve);
		ArrayList<T> ret = new ArrayList<T>();

		if (BaseDef.class.isAssignableFrom(objectToRetrieve)) {
			ConnectionHandler connHandler = new ConnectionHandler(
					sqlLoadString, true);
			ret = connHandler.retrieveArray(objectToRetrieve);
		}

		return ret.toArray(new BaseDef[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveBaseIdDB(String, Class) Added by Dennis to
	 *      allow using SQL to read in def table with Integer for code.
	 */
	public final <T extends BaseDB> SimpleIdDef[] retrieveBaseIdDB(
			String sqlLoadString, Class<T> objectToRetrieve)
			throws DAOException {

		checkNull(sqlLoadString);
		checkNull(objectToRetrieve);
		ArrayList<T> ret = new ArrayList<T>();

		if (BaseDef.class.isAssignableFrom(objectToRetrieve)) {
			ConnectionHandler connHandler = new ConnectionHandler(
					sqlLoadString, true);
			ret = connHandler.retrieveArray(objectToRetrieve);
		}

		return ret.toArray(new SimpleIdDef[0]);
	}

	/**
	 * @see InfrastructureDAO#removeGenericRow(String, String, String)
	 */
	public final void removeRows(String tableName, String columnName,
			Object columnValue) throws DAOException {
		super.removeRows(tableName, columnName, columnValue);
	}

	/**
	 * @see InfrastructureDAO#createFieldAuditLog(String, String, Integer,
	 *      FieldAuditLog)
	 */
	public final void createFieldAuditLogs(String facilityId,
			String facilityName, Integer userId, FieldAuditLog[] newLogs) //Not using facilityName passed in 
			throws DAOException {
		checkNull(facilityId);
		checkNull(facilityName);
		checkNull(userId);
		checkNull(newLogs);

//		Task 8088:Cannot get any FAL entries for EU or facility 		
//		boolean migration = SystemPropertyDef.getSystemPropertyValueAsBoolean("migration", false);
//		if (!migration) {
//			return;
//		}
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createFieldAuditLog", false);

		try {
			for (FieldAuditLog log : newLogs) {
				try {
					Integer logId = nextSequenceVal("S_Facility_Audit_Log_Id");
					connHandler.setInteger(1, logId);
					connHandler.setString(2, log.getAttributeCd());
					connHandler.setString(3, facilityId);
					connHandler.setString(4, log.getCorrEmuId());
					//need to delete facilityName and change the position number accordingly??
					connHandler.setString(5, log.getUniqueId());
					connHandler.setString(6, log.getOriginalValue());
					connHandler.setString(7, log.getNewValue());
					connHandler.setTimestamp(8,
							new Timestamp(System.currentTimeMillis()));
					connHandler.setInteger(9, userId);

					connHandler.updateNoClose();

					// If we get here the INSERT must have succeeded, so set the
					// important data and return the object.
					log.setFacilityId(facilityId);
					log.setFacilityName(facilityName);
					log.setUserId(userId);
					log.setFieldAuditLogId(logId);
					log.setLastModified(1);
				} catch (DAOException daoe) {
					logger.error(
							"createFieldAuditLogs failed on facility "
									+ facilityId + ", attributeCd "
									+ log.getAttributeCd() + ", uniqueId "
									+ log.getUniqueId() + ", originalValue "
									+ log.getOriginalValue() + ", newValue "
									+ log.getNewValue(), daoe);
					throw daoe;
				}
			}
		} finally {
			connHandler.close();
		}
	}

	/**
	 * @see InfrastructureDAO#createCompanyFieldAuditLogs(String, Integer,
	 *      FieldAuditLog)
	 */
	public final void createCompanyFieldAuditLogs(String cmpId, Integer userId,
			FieldAuditLog[] newLogs) throws DAOException {
		checkNull(cmpId);
		checkNull(userId);
		checkNull(newLogs);

//		
//		boolean migration = SystemPropertyDef.getSystemPropertyValueAsBoolean("migration", false);
//		if (!migration) {
//			return;
//		}
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createCompanyFieldAuditLog", false);

		try {
			for (FieldAuditLog log : newLogs) {
				Integer logId = nextSequenceVal("S_Facility_Audit_Log_Id");

				connHandler.setInteger(1, logId);
				connHandler.setString(2, log.getAttributeCd());
				connHandler.setString(3, cmpId);
				connHandler.setString(4, log.getUniqueId());
				connHandler.setString(5, log.getOriginalValue());
				connHandler.setString(6, log.getNewValue());
				connHandler.setTimestamp(7,
						new Timestamp(System.currentTimeMillis()));
				connHandler.setInteger(8, userId);

				connHandler.updateNoClose();

				// If we get here the INSERT must have succeeded, so set the
				// important data and return the object.
				log.setCmpId(cmpId);
				log.setUserId(userId);
				log.setFieldAuditLogId(logId);
				log.setLastModified(1);
			}
		} finally {
			connHandler.close();
		}
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.InfrastructureDAO#changeFacilityIdForFieldAuditLogs(java.lang.String,
	 *      java.lang.String)
	 */
	public void changeFacilityIdForFieldAuditLogs(String origFacId,
			String newFacId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.changeFacilityIdForFieldAuditLogs", false);

		connHandler.setString(1, newFacId);
		connHandler.setString(2, origFacId);

		connHandler.updateNoCheck();
	}

	/**
	 * @see InfrastructureDAO#searchFieldAuditLog(FieldAuditLog)
	 */
	public final FieldAuditLog[] searchFieldAuditLog(FieldAuditLog searchObj)
			throws DAOException {

		String facilityId = searchObj.getFacilityId();
		if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";

			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}

		StringBuffer statementSQL = null;
		if (searchObj.getCategoryCd() != null && searchObj.getCategoryCd().equals("cmp")) {
			statementSQL = new StringBuffer(
				loadSQL("InfrastructureSQL.retrieveFieldAuditLogsCompany"));
		} else {
			statementSQL = new StringBuffer(
				loadSQL("InfrastructureSQL.retrieveFieldAuditLogsNonCompany"));
		}

		if (searchObj.getFacilityName() != null
				&& searchObj.getFacilityName().trim().length() > 0) {
			statementSQL.append(" AND LOWER(ffal.facility_nm) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(searchObj.getFacilityName()
					.replace("*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getFacilityId() != null
				&& searchObj.getFacilityId().trim().length() > 0) {
			statementSQL.append(" AND LOWER(ffal.facility_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
			statementSQL.append("')");
		}

		if (searchObj.getCmpId() != null
				&& searchObj.getCmpId().trim().length() > 0) {
			String cmpId = searchObj.getCmpId();

			// take simple cmp id values and turn them into CMP0* format
			if ((cmpId.matches("(^CMP)([0-9]++)") || cmpId.matches("[0-9]++"))) {
				String newCmpId = cmpId.replaceFirst("^CMP", "");
				newCmpId = String.format("%06d", Integer.parseInt(newCmpId));
				newCmpId = "CMP" + newCmpId;
				cmpId = newCmpId;
				logger.debug("Modified CMPID:" + cmpId);
			}

			statementSQL.append(" AND LOWER(ccm.cmp_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(cmpId.replace("*", "%")));
			statementSQL.append("')");
		}

		if (searchObj.getCategoryCd() != null) {
			statementSQL.append(" AND facd.category_cd = '"
					+ searchObj.getCategoryCd() + "'");
		}
		if (searchObj.getAttributeCd() != null) {
			statementSQL.append(" AND ffal.attribute_cd = '"
					+ searchObj.getAttributeCd() + "'");
		}
		if (searchObj.getUniqueId() != null) {
			statementSQL.append(" AND LOWER(ffal.unique_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(searchObj.getUniqueId().replace(
					"*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getOriginalValue() != null) {
			statementSQL.append(" AND LOWER(ffal.old_value) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(searchObj.getOriginalValue()
					.replace("*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getNewValue() != null) {
			statementSQL.append(" AND LOWER(ffal.new_value) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(searchObj.getNewValue().replace(
					"*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getUserId() != null) {
			statementSQL.append(" AND ffal.user_id = " + searchObj.getUserId());
		}
		if (searchObj.getCorrEmuId() != null) {
			statementSQL.append(" AND ffal.corr_emu_id = "
					+ searchObj.getCorrEmuId());
		}
		// add beginDt & endDt into search criteria
		
		if (searchObj.getBeginDt() != null) {
			statementSQL.append(" AND ffal.date_entered >= ?");
		}
		
        if (searchObj.getEndDt() != null) {
        	statementSQL.append(" AND ffal.date_entered < ?");
        }
        
		statementSQL.append(" ORDER BY ffal.date_entered DESC");

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL.toString());
		
		int i = 1;
		if (searchObj.getBeginDt() != null) {
			connHandler.setTimestamp(i++, searchObj.getBeginDt());
		}
		
        if (searchObj.getEndDt() != null) {
        	connHandler.setTimestamp(i++, searchObj.getEndDt());
        }
		
		logger.debug("statementSQL.toString() = " + statementSQL.toString());

		ArrayList<FieldAuditLog> ret = connHandler
				.retrieveArray(FieldAuditLog.class, 1000);

		return ret.toArray(new FieldAuditLog[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveSccCode(sccCode)
	 */
	public final SccCode retrieveSccCode(String sccCode) throws DAOException {
		checkNull(sccCode);
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveSccCode", true);
		connHandler.setString(1, sccCode);
		SccCode ret = (SccCode) connHandler.retrieve(SccCode.class);

		return ret;

	}

	/**
	 * @see InfrastructureDAO#retrieveSccCodes(List<String>)
	 */
	public final SccCode[] retrieveSccCodes(List<String> searchSccList)
			throws DAOException {
		checkNull(searchSccList);
		String statementSQL = loadSQL("InfrastructureSQL.findSccCodes");

		StringBuffer whereClause = new StringBuffer("");

		int i = 0;
		for (String temp : searchSccList) {
			if (i == 0) {
				whereClause.append(" WHERE ");
			} else {
				whereClause.append(" AND ");
			}
			whereClause.append(" LOWER(scc.inactive) LIKE LOWER('%" + "N"
					+ "%') AND");
			whereClause.append(" ( LOWER(scc.level1_dsc) LIKE LOWER('%" + temp
					+ "%') OR");
			whereClause.append(" LOWER(scc.level2_dsc) LIKE LOWER('%" + temp
					+ "%') OR ");
			whereClause.append(" LOWER(scc.level3_dsc) LIKE LOWER('%" + temp
					+ "%') OR ");
			whereClause.append(" LOWER(scc.level4_dsc) LIKE LOWER('%" + temp
					+ "%')) ");
			i++;
		}
		StringBuffer sortBy = new StringBuffer(" ORDER BY scc.scc_id");
		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL);
		ArrayList<SccCode> ret = connHandler.retrieveArray(SccCode.class);

		return ret.toArray(new SccCode[0]);
	}

	/**
	 * @see InfrastructureDAO#retrieveDefaultFacilityRoles(String)
	 */
	public final FacilityRoleDef[] retrieveDefaultFacilityRoles(String countyCd)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveDefaultFacilityRoles", true);

		connHandler.setString(1, countyCd);

		ArrayList<FacilityRoleDef> ret = connHandler
				.retrieveArray(FacilityRoleDef.class);

		return ret.toArray(new FacilityRoleDef[0]);
	}

	/**
	 * @see InfrastructureDAO#nextSequenceIdValue(String)
	 */
	public Integer nextSequenceIdValue(String sequenceId) throws DAOException {

		return nextSequenceVal(sequenceId);

	}

	/**
	 * @param wrapnCD
	 * @return
	 * @throws DAOException
	 */
	public final WrapnDef retrieveWrapnDef(String wrapnCD) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveWRAPNDef", true);

		connHandler.setString(1, wrapnCD);
		return (WrapnDef) connHandler.retrieve(WrapnDef.class);
	}

	/**
	 * @see InfrastructureDAO#removeContactTypes(Integer)
	 */
	public final void removeContactTypes(Integer contactId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.deleteContactTypes", false);

		connHandler.setInteger(1, contactId);

		connHandler.remove();

		return;
	}

	/**
	 * @see InfrastructureDAO#removeContactTypes(Integer)
	 */
	public final void removeStagingContactTypes(Integer contactId,
			String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.deleteStagingContactTypes", false);

		connHandler.setInteger(1, contactId);
		connHandler.setString(2, facilityId);

		connHandler.remove();

		return;
	}

	/**
	 * @see InfrastructureDAO#getSimpleDefMetaData(SimpleDef simpleDef)
	 */
	public SimpleDef getSimpleDefMetaData(SimpleDef simpleDef)
			throws DAOException {
		// get the meta data for the simpleDef, populate the object and then
		// return it.
		Connection conn = getConnection();
		try {
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet rs = metadata.getColumns(null, "%", simpleDef.getTable(),
					"%");
			while (rs.next()) {
				if (rs.getString("COLUMN_NAME").equals(
						simpleDef.getDescriptionColumn())) {
					simpleDef
							.setDescriptionColumnSize(rs.getInt("COLUMN_SIZE"));
				} else if (rs.getString("COLUMN_NAME").equals(
						simpleDef.getCodeColumn())) {
					simpleDef.setCodeColumnSize(rs.getInt("COLUMN_SIZE"));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			handleClosing(conn);
		}
		return simpleDef;
	}

	/**
	 * @see InfrastructureDAO#createTask(Task newTask)
	 */
	public Task createTask(Task newTask) throws DAOException {
		checkNull(newTask);

		Task ret = newTask;

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.createTask", false);
		int i = 1;
		// TODO: Replace task id with task found from service
		Integer id = nextSequenceVal("S_Task_Id");
		ret.setTaskId(id.toString());
		connHandler.setString(i++, newTask.getTaskId());
		connHandler.setInteger(i++, newTask.getFpId());
		connHandler.setString(i++, newTask.getDependentTaskId());
		connHandler.setString(i++, newTask.getTaskType().toString());
		connHandler.setString(i++, newTask.getTaskName());
		connHandler.setString(i++, newTask.getTaskDescription());
		connHandler
				.setString(i++, AbstractDAO.translateBooleanToIndicator(newTask
						.getDependent()));
		connHandler.setTimestamp(i++, newTask.getCreateDate());
		connHandler.setString(i++, newTask.getUserName());
		connHandler.setString(i++, newTask.getVersion());
		connHandler.setInteger(i++, newTask.getTaskInternalId());
		connHandler.setString(i++, newTask.getFacilityId());
		connHandler.setInteger(i++, newTask.getReferenceCount());
		connHandler.setInteger(i++, newTask.getCorePlaceId());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newTask.isNonROSubmission()));
		connHandler.setInteger(i++, newTask.getDocumentId());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InfrastructureDAO#modifyTask(Task task)
	 */
	public boolean modifyTask(Task task) throws DAOException {
		checkNull(task);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.modifyTask", false);

		int i = 1;
		connHandler.setString(i++, task.getTaskName());
		connHandler.setString(i++, task.getDependentTaskId());
		connHandler.setString(i++, task.getTaskDescription());
		connHandler.setInteger(i++, task.getFpId());
		connHandler.setInteger(i++, task.getReferenceCount());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(task.getDependent()));
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(task
				.isNonROSubmission()));
		connHandler.setInteger(i++, task.getDocumentId());
		connHandler.setInteger(i++, task.getLastModified() + 1);

		connHandler.setString(i++, task.getTaskId());
		connHandler.setInteger(i++, task.getLastModified());

		boolean ok = connHandler.update();
		// make sure last modified is updated in object
		task.setLastModified(task.getLastModified() + 1);

		return ok;
	}

	/**
	 * @see InfrastructureDAO#deleteTask(String taskId)
	 */
	public void deleteTask(String taskId) throws DAOException {
		checkNull(taskId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.deleteTask", false);
		connHandler.setString(1, taskId);
		connHandler.remove();

		return;
	}

	/**
	 * @see InfrastructureDAO#updateTasksDepTaskId(String oldTaskId, String
	 *      newTaskId)
	 */
	public void updateTasksDepTaskId(String oldTaskId, String newTaskId)
			throws DAOException {
		checkNull(oldTaskId);
		checkNull(newTaskId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.updateTasksDepTaskId", false);
		connHandler.setString(1, newTaskId);
		connHandler.setString(2, oldTaskId);
		connHandler.update();

		return;
	}

	/**
	 * @see InfrastructureDAO#retrieveTask(String taskId)
	 */
	public Task retrieveTask(String taskId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveTask", true);

		connHandler.setString(1, taskId);
		return (Task) connHandler.retrieve(Task.class);
	}

	/**
	 * @see InfrastructureDAO#retrieveTasks(Task filterTask)
	 */
	public Task[] retrieveTasks(Task filterTask) throws DAOException {
		// TODO: completely remove core place id
		// StringBuffer statementSQL = new StringBuffer(
		// loadSQL("InfrastructureSQL.retrieveTasks"));

		StringBuffer statementSQL = new StringBuffer(
				loadSQL("InfrastructureSQL.retrieveTasksByFacId"));

		if (filterTask.getTaskType() != null) {
			statementSQL.append(" AND task_type = '" + filterTask.getTaskType()
					+ "'");
		}

		statementSQL.append(" ORDER BY create_date DESC");

		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLStringRaw(statementSQL.toString());

		// connHandler.setInteger(1, filterTask.getCorePlaceId());
		connHandler.setString(1, filterTask.getFacilityId());

		ArrayList<Task> ret = connHandler.retrieveArray(Task.class);

		return ret.toArray(new Task[0]);
	}

	public Hashtable<String, DataGridCellDef> getMetaData(String table)
			throws DAOException {
		Hashtable<String, DataGridCellDef> tableDefinition = new Hashtable<String, DataGridCellDef>(
				0);
		Connection conn = null;

		try {
			conn = getConnection();
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet rs = metadata.getColumns(null, "%", table, "%");
			while (rs.next()) {
				DataGridCellDef dgcd = new DataGridCellDef();
				dgcd.setField(rs.getString("COLUMN_NAME"));
				dgcd.setHeaderText(rs.getString("COLUMN_NAME"));
				dgcd.setMaximumLength(rs.getInt("COLUMN_SIZE"));
				dgcd.setDataType(rs.getString("TYPE_NAME"));
				dgcd.setPrimaryKey(false);
				if (rs.getInt("NULLABLE") == 1) {
					dgcd.setAllowNull(true);
				} else {
					dgcd.setAllowNull(false);
				}
				// int digits = rs.getInt("DECIMAL_DIGITS");
				tableDefinition.put(dgcd.getField(), dgcd);
			}
			ResultSet rs2 = metadata.getPrimaryKeys(null, "dbo", table);
			while (rs2.next()) {
				DataGridCellDef dgcd = tableDefinition.get(rs2
						.getString("COLUMN_NAME"));
				dgcd.setPrimaryKey(true);
				tableDefinition.put(dgcd.getField(), dgcd); // NO NEED TO PUT
															// BACK SINCE
															// ALREADY IN THE
															// Hashtable.
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			handleClosing(conn);
		}
		return tableDefinition;
	}

	// print out the result set for debugging
	public void writeQueryToStdOut(String sql) throws DAOException {
		Connection conn = null;
		PreparedStatement pStmt = null;
		try {
			conn = getConnection();
			pStmt = conn.prepareStatement(sql);
			ResultSet rs = pStmt.executeQuery();
			while (rs.next()) {
				for (int col = 1; col <= 6; col++) {
					String column = rs.getString(col);
					if (column != null) {
						logger.error(column.replaceAll("\n", " ") + "|");
					} else {
						logger.error("|");
					}
				}

			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			handleClosing(conn);
		}
	}

	/**
	 * @see InfrastructureDAO#retrieveRptUserId(Integer id)
	 */
	public Integer retrieveRptUserId(Integer id) throws DAOException {
		ConnectionHandler connHandler;
		connHandler = new ConnectionHandler(
				"InfrastructureSQL.emissionsReportUserId", true);
		connHandler.setInteger(1, id);
		DbInteger result = (DbInteger) connHandler.retrieve(DbInteger.class);
		if (result != null) {
			return result.getCnt();
		} else {
			return null;
		}
	}

	/**
	 * @see InfrastructureDAO#retrieveDorUserId(Integer id)
	 */
	public Integer retrieveDorUserId(Integer id) throws DAOException {
		// Delegation of Responsibility
		ConnectionHandler connHandler;
		connHandler = new ConnectionHandler(
				"InfrastructureSQL.dorApplicationUserId", true);
		connHandler.setInteger(1, id);
		DbInteger result = (DbInteger) connHandler.retrieve(DbInteger.class);
		if (result != null) {
			return result.getCnt();
		} else {
			return null;
		}
	}

	/**
	 * @see InfrastructureDAO#retrieveReqestIdFromRelocate(Integer id)
	 */
	public Integer retrieveReqestIdFromRelocate(Integer id) throws DAOException {
		// Intent to relocate to site not pre-approved
		// Relocate to pre-approved site
		// Relocation Site(s) Pre-Approval
		ConnectionHandler connHandler;
		connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveReqestIdFromRelocate", true);
		connHandler.setInteger(1, id);
		DbInteger result = (DbInteger) connHandler.retrieve(DbInteger.class);
		if (result != null) {
			return result.getCnt();
		} else {
			return null;
		}
	}

	/**
	 * @see InfrastructureDAO#applicationUserIdFromPermit(Integer id)
	 */
	public Integer applicationUserIdFromPermit(Integer id) throws DAOException {
		// Intent to relocate to site not pre-approved
		// Relocate to pre-approved site
		// Relocation Site(s) Pre-Approval
		ConnectionHandler connHandler;
		connHandler = new ConnectionHandler(
				"InfrastructureSQL.applicationUserIdFromPermit", true);
		connHandler.setInteger(1, id);
		DbInteger result = (DbInteger) connHandler.retrieve(DbInteger.class);
		if (result != null) {
			return result.getCnt();
		} else {
			return null;
		}
	}

	/**
	 * @see InfrastructureDAO#retrievePermitId(Integer id)
	 */
	public Integer retrievePermitId(Integer id) throws DAOException {
		ConnectionHandler connHandler;
		connHandler = new ConnectionHandler(
				"InfrastructureSQL.applicationPermitId", true);
		connHandler.setInteger(1, id);
		DbInteger result = (DbInteger) connHandler.retrieve(DbInteger.class);
		if (result != null) {
			return result.getCnt();
		} else {
			return null;
		}
	}

	/**
	 * @see InfrastructureDAO#retrievePermitId(Integer id)
	 */
	public Integer applicationUserIdFromRelocate(Integer id)
			throws DAOException {
		ConnectionHandler connHandler;
		connHandler = new ConnectionHandler(
				"InfrastructureSQL.applicationUserIdFromRelocate", true);
		connHandler.setInteger(1, id);
		DbInteger result = (DbInteger) connHandler.retrieve(DbInteger.class);
		if (result != null) {
			return result.getCnt();
		} else {
			return null;
		}
	}

	/**
	 * @see InfrastructureDAO#retrieveContactByUserId(Integer userId)
	 */
	public UserDef retrieveContactByUserId(Integer userId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveContactByUserId", true);

		connHandler.setInteger(1, userId);
		return (UserDef) connHandler.retrieve(UserDef.class);
	}
	
	public final int getNextSequenceNumber(String seqNumber) throws DAOException {
	    Integer seqNo = nextSequenceVal(seqNumber);
	    return seqNo.intValue();
	}
	
	public final DaemonInfo retrieveDaemonInfo(String daemonCode, String hostname)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveDaemonInfoByCodeAndHostname", true);

		connHandler.setString(1, daemonCode);
		connHandler.setString(2, hostname);

		return (DaemonInfo) connHandler.retrieve(DaemonInfo.class);
	}
	
	public final List<PollutantDef> retrievePollutantsByCategory(String category) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("InfrastructureSQL.retreivePollutantsByCategory", true);
        connHandler.setString(1, category);
        ArrayList<PollutantDef> tempArray = connHandler.retrieveArray(PollutantDef.class);
        return tempArray;
    }
	
	public final List<ForeignKeyReference> retrieveForeignKeyReferences(String tableName, String columnName, 
			String schema) throws DAOException{
		ConnectionHandler connHandler = new ConnectionHandler("InfrastructureSQL.retrieveForeignKeyReferences", true);
        connHandler.setString(1, columnName);
        connHandler.setString(2, tableName);
        connHandler.setString(3, schema);
        
        ArrayList<ForeignKeyReference> fkReferences = connHandler.retrieveArray(ForeignKeyReference.class);
        return fkReferences;
	}
	
	public final boolean checkForeignKeyReferencedData(String tableName, String columnName,	Integer fkValue) throws DAOException {
		
		boolean ret = false;
		StringBuffer sqlString = new StringBuffer("SELECT " + columnName + " FROM " + addSchemaToTable(tableName) + " WHERE " + columnName + " = " + fkValue);
		
		Connection con = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		
		// special case for excluded eu in the application
		// we don't want to prevent an eu from being deleted if it is 
		// excluded in the application
		if(tableName.equalsIgnoreCase("PA_EU")) {
			sqlString.append(" AND EXCLUDED_FLAG = 'N'");
		}
		
		try {
			con = getReadOnlyConnection();
			pStmt = con.prepareStatement(sqlString.toString());
			
			rs = pStmt.executeQuery();
			if(rs.next())
				ret = true; // at least one foreign key reference found
			
		} catch (Exception e) {
			handleException(e, con);
		} finally {
			closeStatement(pStmt);
			handleClosing(con);
		}	
		
		return ret;
	}
	
	/**
	 * @see InfrastructureDAO#retrieveUserStatusByLogin(String)
	 */
	public final boolean retrieveUserStatusByLogin(String login) throws DAOException {
		String ret = null;
		ConnectionHandler connectionHandler = new ConnectionHandler("InfrastructureSQL.retrieveUserStatusByLogin", true);
		connectionHandler.setString(1, login);
		ret = (String)connectionHandler.retrieveJavaObject(String.class);
		if(ret != null)
			return ret.equalsIgnoreCase("Y") ? true : false;
		else
			return false;
	}
	
	@Override
	public void createFieldAuditLogs(
			Map<String, FieldAuditLog[]> facilityAuditLogMap, int userId)
			throws DAOException {
		checkNull(userId);
		
		boolean migration = SystemPropertyDef.getSystemPropertyValueAsBoolean("migration", false);
		if (!migration) {
			return;
		}
		
		ArrayList<FieldAuditLog> allLogs = new ArrayList<FieldAuditLog>();
		for (FieldAuditLog[] log : facilityAuditLogMap.values()) {
			CollectionUtils.addAll(allLogs,log);
		}
		
		int remainder = allLogs.size();
		int startIndex = 0;
		while (remainder > 0) {
			int batchSize = calculateBatchSize(remainder);
			remainder = remainder - batchSize;
			
			ConnectionHandler connHandler = new ConnectionHandler(
					"InfrastructureSQL.createFieldAuditLog", false);
			
			int endIndex = startIndex + batchSize;
			for (int i = startIndex; i < endIndex; i++) {
				FieldAuditLog log = allLogs.get(i);

				Integer logId = nextSequenceVal("S_Facility_Audit_Log_Id");
				connHandler.setInteger(1, logId);
				connHandler.setString(2, log.getAttributeCd());
				connHandler.setString(3, log.getFacilityId());
				connHandler.setString(4, log.getCorrEmuId());	 			
				connHandler.setString(5, log.getUniqueId());
				connHandler.setString(6, log.getOriginalValue());
				connHandler.setString(7, log.getNewValue());
				connHandler.setTimestamp(8,
						new Timestamp(System.currentTimeMillis()));
				connHandler.setInteger(9, userId);

				connHandler.addBatch();
				startIndex++;

				// If we get here the INSERT must have succeeded, so set the
				// important data and return.
				log.setUserId(userId);
				log.setFieldAuditLogId(logId);
				log.setLastModified(1);
			}
			connHandler.executeBatchUpdate();
			logger.debug("batchSize: " + batchSize + "; total: " + allLogs.size() + "; processed: " + startIndex + "; remainder: " + remainder);
		}
	}
	
	/**
	 * @see InfrastructureDAO#retrieveDistrictCounties()
	 */
	public final CountyDef[] retrieveDistrictCounties(String districtCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveDistrictCounties", true);
		connHandler.setString(1, districtCd);

		ArrayList<CountyDef> ret = connHandler.retrieveArray(CountyDef.class);

		return ret.toArray(new CountyDef[0]);
	}

	@Override
	public TimeSheetRow createTimesheetEntry(TimeSheetRow row) throws DAOException {
		checkNull(row);
		ConnectionHandler connHandler = new ConnectionHandler(
				"NSRBillingSQL.createTimesheetEntry", true);
		Integer id = nextSequenceVal("S_TIMESHEET_ENTRY");
		row.setRowId(id);
		
		applyColumnValues(connHandler, row);
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		row.setLastModified(1);

		return row;
	}
	
	
	public TimeSheetRow[] retrieveTimeSheetEntries(Integer userId) throws DAOException {
		checkNull(userId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"NSRBillingSQL.retrieveTimeSheetEntries", true);
		
		connHandler.setInteger(1, userId);
		
		ArrayList<TimeSheetRow> ret = connHandler.retrieveArray(TimeSheetRow.class);
		
		TimeSheetRow[] rows = ret.toArray(new TimeSheetRow[0]);
		return rows;
	}

	@Override
	public boolean modifyTimesheetEntry(TimeSheetRow modifyRow)
			throws DAOException {

		checkNull(modifyRow);

		ConnectionHandler connHandler = new ConnectionHandler(
				"NSRBillingSQL.modifyTimesheetEntry", false);

		int i = 1;
		connHandler.setTimestamp(i++, modifyRow.getDate());
		connHandler.setString(i++, modifyRow.getFunction());
		connHandler.setString(i++, modifyRow.getSection());
		connHandler.setString(i++, modifyRow.getNsrId());
		connHandler.setInteger(i++, null); // TODO app id
		connHandler.setInteger(i++, null); // TODO permit id
		connHandler.setInteger(i++, null); // objective not used, legacy AQDS
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(modifyRow.isOvertime()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(modifyRow.isInvoiced()));
		connHandler.setFloat(i++, modifyRow.getHours());
		connHandler.setString(i++, modifyRow.getComments());
		connHandler.setInteger(i++, modifyRow.getLastModified() + 1);
		connHandler.setString(i++, modifyRow.getTvId());

		connHandler.setInteger(i++, modifyRow.getRowId());
		connHandler.setInteger(i++, modifyRow.getLastModified());

		return connHandler.update();
	}

	@Override
	public TimeSheetRow retrieveTimesheetEntry(Integer rowId)
			throws DAOException {
		checkNull(rowId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"NSRBillingSQL.retrieveTimeSheetEntry", true);
		
		connHandler.setInteger(1, rowId);
		
		TimeSheetRow row  = 
				(TimeSheetRow)connHandler.retrieve(TimeSheetRow.class);
		return row;
	}

	@Override
	public void removeTimesheetEntry(TimeSheetRow modifyRow)
			throws DAOException {
		checkNull(modifyRow);

		ConnectionHandler connHandler = new ConnectionHandler(
				"NSRBillingSQL.removeTimesheetEntry", false);

		connHandler.setInteger(1, modifyRow.getRowId());

		connHandler.update();
	}

	@Override
	public Integer countAqdsTimesheetEntries() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"NSRBillingSQL.countAqdsTimesheetEntries", true);
		Integer count = (Integer)connHandler.retrieveJavaObject(Integer.class);
		return count;
	}

	@Override
	public TimeSheetRow[] retrieveAqdsNsrTimesheetEntries()
			throws DAOException {
		
		ConnectionHandler conn = 
				new AQDSConnectionHandler("NSRBillingSQL.retrieveAqdsNsrTimesheetEntries").getAqdsConnection();
		
		ArrayList<TimeSheetRow> ret = conn.retrieveArray(TimeSheetRow.class);
		
		TimeSheetRow[] rows = ret.toArray(new TimeSheetRow[0]);
		return rows;
	}

	@Override
	public void createTimesheetEntries(TimeSheetRow[] timesheetEntries)
			throws DAOException {

		int remainder = timesheetEntries.length;
		int startIndex = 0;
		while (remainder > 0) {
			int batchSize = calculateBatchSize(remainder);
			remainder = remainder - batchSize;
			
			ConnectionHandler connHandler = new ConnectionHandler(
					"NSRBillingSQL.createTimesheetEntry", false);
			
			int endIndex = startIndex + batchSize;
			for (int i = startIndex; i < endIndex; i++) {
				TimeSheetRow row = timesheetEntries[i];
				Integer id = nextSequenceVal("S_TIMESHEET_ENTRY");
				row.setRowId(id);
				
				applyColumnValues(connHandler, row);

				connHandler.addBatch();
				startIndex++;

				// If we get here the INSERT must have succeeded, so set the important
				// data and return the object.
				row.setLastModified(1);
			}
			connHandler.executeBatchUpdate();
			logger.debug("timesheet batch size: " + batchSize + "; total: " + timesheetEntries.length + "; processed: " + startIndex + "; remainder: " + remainder);
		}
	}

	private void applyColumnValues(ConnectionHandler connHandler,
			TimeSheetRow row) throws DAOException {
		int ix = 1;
		connHandler.setInteger(ix++, row.getRowId());
		connHandler.setInteger(ix++, row.getUserId());
		connHandler.setTimestamp(ix++, row.getDate());
		connHandler.setString(ix++, row.getFunction());
		connHandler.setString(ix++, row.getSection());
		connHandler.setString(ix++, row.getNsrId());
		connHandler.setInteger(ix++, null); // app id
		connHandler.setInteger(ix++, null); // permit id
		connHandler.setInteger(ix++, null); // objective
		connHandler.setString(ix++, AbstractDAO
				.translateBooleanToIndicator(row.isOvertime()));
		connHandler.setString(ix++, AbstractDAO
				.translateBooleanToIndicator(row.isInvoiced()));
		connHandler.setFloat(ix++, row.getHours());
		connHandler.setString(ix++, row.getComments());
		connHandler.setInteger(ix++, row.getAqdsAppId());
		connHandler.setInteger(ix++, row.getHoursEntryId());
		connHandler.setString(ix++, row.getTvId());
	}

	@Override
	public void assignImpactUser(TimeSheetRow entry) throws DAOException {
		Integer userId = aqdsEmployeeCmUserMap.get(entry.getAqdsEmployeeId());
		if (null == userId) {
			ConnectionHandler connHandler = 
					new ConnectionHandler("NSRBillingSQL.retrieveImpactTimesheetUser",
							false);
			
			connHandler.setInteger(1, entry.getAqdsEmployeeId());
			userId = (Integer) connHandler.retrieveJavaObject(Integer.class);

			if (userId > 0) {
				aqdsEmployeeCmUserMap.put(entry.getAqdsEmployeeId(), userId);
			}
		}
		entry.setUserId(userId);
	}

	private HashMap<Integer,Integer> aqdsEmployeeCmUserMap = 
			new HashMap<Integer,Integer>();
	
	@Override
	public void deleteAllTimesheetEntries() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"NSRBillingSQL.deleteAllTimesheetEntries", true);

		connHandler.remove();
	}

	@Override
	public Map<Integer,Timestamp> retrieveLastInvoiceReferenceDate(String nsrAppId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveLastInvoiceReferenceDate", true);
		connHandler.setString(1, nsrAppId);
		Map<Integer,Timestamp> result = 
				connHandler.retrieveMap(Integer.class,Timestamp.class);
		return result;
	}

	@Override
	public String retrieveUserWithPositionNumber(Integer positionNumber)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveUserWithPositionNumber", true);

		connHandler.setInteger(1, positionNumber);

		String ret = (String)connHandler.retrieveJavaObject(String.class);
		return ret;
	}
	
	@Override
	public boolean addressIntersectsShape(Integer addressId, Integer shapeId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.addressIntersectsShape", true);

		connHandler.setInteger(1, shapeId);
		connHandler.setInteger(2, addressId);

		Integer ret = (Integer)connHandler.retrieveJavaObject(Integer.class);
		return null == ret? false : ret == 1;
	}
	
	@Override
	public Integer[] getShapeIds() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.getShapeIds", true);

		ArrayList<Integer> ret = connHandler.retrieveJavaObjectArray(Integer.class);
		return ret.toArray(new Integer[0]);
	}

	@Override
	public OffsetTrackingNonAttainmentAreaDef[] findNonattainmentAreasByAddress(Integer addressId)
			throws DAOException {
		
		ConnectionHandler connHandler = 
				new ConnectionHandler("InfrastructureSQL.findNonattainmentAreasByAddress",true);
		
		connHandler.setInteger(1, addressId);
		
		ArrayList<OffsetTrackingNonAttainmentAreaDef> ret = connHandler.retrieveArray(OffsetTrackingNonAttainmentAreaDef.class);
		
		OffsetTrackingNonAttainmentAreaDef[] rows = ret.toArray(new OffsetTrackingNonAttainmentAreaDef[0]);
		return rows;
	}

	@Override
	public PollutantDef[] findPollutantsByNonattainmentArea(String areaCd)
			throws DAOException {
		ConnectionHandler connHandler = 
				new ConnectionHandler("InfrastructureSQL.findPollutantsByNonattainmentArea",true);
		
		connHandler.setString(1, areaCd);
		
		ArrayList<PollutantDef> ret = connHandler.retrieveArray(PollutantDef.class);
		
		PollutantDef[] rows = ret.toArray(new PollutantDef[0]);
		return rows;
	}

	@Override
	public OffsetTrackingContributingPollutantDef[] findContributingPollutantsByNonattainmentArea(String areaCd)
			throws DAOException {
		ConnectionHandler connHandler = 
				new ConnectionHandler("InfrastructureSQL.findContributingPollutantsByNonattainmentArea",true);
		
		connHandler.setString(1, areaCd);
		
		ArrayList<OffsetTrackingContributingPollutantDef> ret = 
				connHandler.retrieveArray(OffsetTrackingContributingPollutantDef.class);
		
		OffsetTrackingContributingPollutantDef[] rows = 
				ret.toArray(new OffsetTrackingContributingPollutantDef[0]);
		return rows;
	}

	@Override
	public Shape[] retrieveShapes() throws DAOException {
		ConnectionHandler connHandler = 
				new ConnectionHandler("InfrastructureSQL.retrieveShapes",true);

		ArrayList<Shape> ret = connHandler.retrieveArray(Shape.class);
		
		Shape[] rows = ret.toArray(new Shape[0]);
		return rows;
	}

	@Override
	public boolean modifyShape(Shape modifyShape) throws DAOException {
			checkNull(modifyShape);

			ConnectionHandler connHandler = new ConnectionHandler(
					"InfrastructureSQL.modifyShape", false);

			int i = 1;
			connHandler.setString(i++, modifyShape.getLabel());
			connHandler.setString(i++, modifyShape.getDescription());
			connHandler.setInteger(i++, modifyShape.getLastModified() + 1);
			connHandler.setInteger(i++, modifyShape.getShapeId());

			boolean ok = connHandler.update();
			// make sure last modified is updated in object
			modifyShape.setLastModified(modifyShape.getLastModified() + 1);

			return ok;
		}

	@Override
	public Shape retrieveShape(Integer shapeId) throws DAOException {
		ConnectionHandler connHandler = 
				new ConnectionHandler("InfrastructureSQL.retrieveShape",true);
		
		connHandler.setInteger(1, shapeId);

		Shape ret = (Shape)connHandler.retrieve(Shape.class);
		return ret;
	}

	@Override
	public void removeShape(Shape shape) throws DAOException {
		ConnectionHandler connHandler = 
				new ConnectionHandler("InfrastructureSQL.removeShape",true);
		
		connHandler.setInteger(1, shape.getShapeId());

		connHandler.update();
	}

	@Override
	public List<String> retrieveProjectIdsAssociatedWithShape(Integer shapeId)
			throws DAOException {
		checkNull(shapeId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveProjectIdsAssociatedWithShape", true);
		connHandler.setInteger(1, shapeId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public List<SimpleDef> retrieveProjectTypesByAppUsr(Integer userId)
			throws DAOException {

		checkNull(userId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveProjectTypesByAppUsr", true);
		connHandler.setInteger(1, userId);

		return connHandler.retrieveArray(SimpleDef.class);
	}

	@Override
	public List<String> retrieveInactiveProjectIdsAssociatedWithShape(
			Integer shapeId) throws DAOException {
		checkNull(shapeId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveInactiveProjectIdsAssociatedWithShape",
				true);
		connHandler.setInteger(1, shapeId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}


	@Override
	public SystemPropertyDef retrieveSystemProperty(String systemPropCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveSystemProperty", true);
		connHandler.setString(1, systemPropCd);
		return (SystemPropertyDef) connHandler.retrieve(SystemPropertyDef.class);
	}
	
	@Override
	public  SystemPropertyDef[] retrieveSystemProperties()
			throws DAOException {
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.retrieveSystemProperties", true);
		ArrayList<SystemPropertyDef> ret= connHandler.retrieveArray(SystemPropertyDef.class);
		SystemPropertyDef[] rows=ret.toArray(new SystemPropertyDef[0]);
		return rows;
	}

	@Override
	public String getIndianReservationCdByLatLong(String latitude, String longitude)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler("InfrastructureSQL.getIndianReservationCdByLatLong", false);
		int index = 1;
		connHandler.setString(index++, latitude);
		connHandler.setString(index++, longitude);

		return (String) connHandler.retrieveJavaObject(String.class);
	}

	@Override
	public List<Integer> retrieveIndianReservationShapeIds() throws DAOException{
		ConnectionHandler connHandler = new ConnectionHandler("InfrastructureSQL.retrieveIndianReservationShapeIds", false);
		return connHandler.retrieveJavaObjectArray(Integer.class);
	}

	@Override
	public void deleteFacilityFieldAuditLogs(String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.deleteFacilityFieldAuditLog", true);
		connHandler.setString(1, facilityId);
		connHandler.remove();
	
	}

	
	@Override
	public  void removeAddressReference (Integer fpId) throws DAOException {
		checkNull(fpId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InfrastructureSQL.removeAddressXref", false);

		connHandler.setInteger(1, fpId);

		connHandler.remove();
	}


}

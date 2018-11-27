package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperPrint;
import us.oh.state.epa.stars2.database.adhoc.DataGridCellDef;
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
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.OffsetTrackingContributingPollutantDef;
import us.oh.state.epa.stars2.def.OffsetTrackingNonAttainmentAreaDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.WrapnDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;
import us.wy.state.deq.impact.database.dbObjects.infrastructure.DistrictDef;

public interface InfrastructureDAO extends TransactableDAO {

	public OffsetTrackingContributingPollutantDef[] findContributingPollutantsByNonattainmentArea(String areaCd)
			throws DAOException;

		public PollutantDef[] findPollutantsByNonattainmentArea(String areaCd) throws DAOException;
	
	public OffsetTrackingNonAttainmentAreaDef[] findNonattainmentAreasByAddress(Integer addressId) throws DAOException;
	
	public Integer[] getShapeIds() throws DAOException;
	
	public String retrieveUserWithPositionNumber(Integer positionNumber)
			throws DAOException;

	public TimeSheetRow[] retrieveTimeSheetEntries(Integer userId) throws DAOException;

	public PredicateDef[] retrievePredicates() throws DAOException;

	/**
     * Retrieves SCC "level" codes.
     * 
     * @param level
     * @parm sccId
     * @return
     * @throws DAOException
     */
    SccLevel[] retrieveSccLevelCodes(int level, String level1Desc, String level2Desc, String level3Desc, String Level4Desc, String sccId) throws DAOException;

    /**
     * Retrieves the address for the given addressId.
     * 
     * @param addressId
     * @return
     * @throws DAOException
     */
    Address retrieveAddress(int addressId) throws DAOException;
    
    /**
     * Get the PLSS by the latitude and longitude.
     * 
     * @param latitude
     * @param longitude
     * @return PLSS
     * @throws DAOException
     */
    PlssConversion getPlssByLatLong(String latitude, String longitude) throws DAOException;
    
    /**
     * Get the latitude and longitude by PLSS.
     * 
     * @param section
     * @param township
     * @param range
     * @return latitude and longitude
     * @throws DAOException
     */
    PlssConversion getLatLongByPlss(String section, String township, String range) throws DAOException;
    
    /**
     * Get the county by latitude and longitude.
     * 
     * @param latitude
     * @param longitude
     * @return County
     * @throws DAOException
     */
    CountyDef getCountyByLatLong(String latitude, String longitude) throws DAOException;
    
    /**
     * Retrieves the contact for the given contactId.
     * 
     * @param contactId
     * @return
     * @throws DAOException
     */
    Contact retrieveContact(int contactId) throws DAOException;

    /**
     * Retrieves the meta data for the given simpleDef.
     * 
     * @param simpleDef
     * @return
     * @throws DAOException
     */
    SimpleDef getSimpleDefMetaData(SimpleDef simpleDef) throws DAOException;
    
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
     * Modifies the given Contact
     * 
     * @param contact
     *            contact to create
     * @return boolean
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyContact(Contact contact) throws DAOException;
    
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
     * Deletes the given Contact
     * 
     * @param contact
     *            contact to delete
     * @return void
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    void deleteStagingContact(Contact c) throws DAOException;

    /**
     *
     * @return UseCase[]
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UseCase[] retrieveAllUseCases() throws DAOException;

    /**
     * Retrieves usecases for a given userId
     * 
     * @param appTypeCd
     * @param userId
     * @return UseCase[]
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    LinkedHashMap<String, UseCase> retrieveUseCases(int userId)
            throws DAOException;

    /**
     * Returns the requested UseCase identified by the useCase
     * 
     * @param useCaseId
     *            The use case requested.
     * 
     * @return UseCase.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UseCase retrieveUseCase(String useCase) throws DAOException;

    /**
     * Returns all of the use-cases currently associated with this "securityId".
     * The returned array of String represents values from
     * "Group_Feature_Xref.security_feature_cd" and are sorted in alphabetical
     * order.
     * 
     * @param userId
     *            The User Id of a system user.
     * 
     * @return String[] All of the use-cases this user may access.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UseCase[] retrieveUseCases(Integer securityGroupId)
            throws DAOException;

    /**
     * Retrieve the User Name identified by userId.
     * 
     * @param userId
     * @return String
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    String retrieveUserName(Integer userId) throws DAOException;

    /**
     * Retrieve the UserDef object identified by login and password.
     * 
     * @param login
     * @param password
     * @return
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UserDefBase retrieveUserDefByLoginAndPassword(String login, String password)
            throws DAOException;
    
    /**
     * Retrieve the UserDef object identified by login.
     * 
     * @param login
     * @return
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UserDefBase retrieveUserDefByLogin(String login)
            throws DAOException;
    
    /**
     * Retrieve the UserDef object identified by login.
     * 
     * @param login
     * @return
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean retrieveUserByLogin(String login)
            throws DAOException;
    
    /**
 	 * 
	 * @param login
     * @return boolean
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean createInternalUser(String login)
            throws DAOException;
    
    

    /**
     * Retrieve the UserDef object identified by login and password.
     * 
     * @param userId
     * @param password
     * @return
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UserDefBase retrieveUserDefByIdAndPassword(int userId, String password)
            throws DAOException;

    /**
     * Retrieve the UserDef object identified by id.
     * 
     * @param userId
     * @param password
     * @return
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UserDef retrieveUserDef(Integer userId) throws DAOException;

    /**
     * Create a new UserDef object.
     * 
     * @param userDef
     * @return
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UserDef createUserDef(UserDef userDef) throws DAOException;

    /**
     * Modify the active_ind field of an existing userDef object.
     * 
     * @param userDef
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyUserDefActive(UserDef userDef) throws DAOException;
    
    /**
     * Modifies the given User
     * 
     * @param userDef
     *            userDef to create
     * @return boolean
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyUser(UserDef userDef) throws DAOException;

    /**
     * Retrieve SecurityGroup for given security group id.
     * 
     * @param SecurityGroup
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    SecurityGroup retrieveSecurityGroup(Integer securityGroupId)
            throws DAOException;

    /**
     * Retrieve SecurityGroups for given application type.
     * 
     * @param String
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    SecurityGroup[] retrieveSecurityGroups() throws DAOException;

    /**
     * Retrieves County associated with given countyId
     * 
     * @param countyId
     * @return County county associated with given countyId
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    CountyDef retrieveCounty(String countyCd) throws DAOException;

    /**
     * Retrieves all defined Counties.
     * 
     * @return CountyDef[] an array of all defined counties
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    CountyDef[] retrieveCounties() throws DAOException;
    
    /**
     * Retrieves all Counties for given District.
     * 
     * @return CountyDef[] an array of all defined counties for given district
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    CountyDef[] retrieveDistrictCounties(String districtCd) throws DAOException;
    
//    /**
//     * Retrieves all defined Cities.
//     * 
//     * @return CityDef[] an array of all defined cities
//     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
//     */
//    CityDef[] retrieveCities() throws DAOException;
//    
//     /**
//     * Retrieves all Cities for given County.
//     * 
//     * @return CityDef[] an array of all defined cities for given county
//     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
//     */
//    CityDef[] retrieveCountyCities(String countyCd) throws DAOException;

    /**
     * Retrieves NewspaperDef associated with given countyId
     * 
     * @param countyId
     * @return NewspaperDef associated with given countyId
     */
    NewspaperDef retrieveNewspaper(String countyCd) throws DAOException;

    /**
     * Retrieves all defined DO-LAAs.
     * 
     * @return DoLaaDef[] an array of all defined DO-LAAs
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    DoLaaDef[] retrieveDoLaas() throws DAOException;
    
    /**
     *  Retrieve information for specified DO/LAA.
     * @param doLaaCd
     * @return
     * @throws DAOException
     */
    DoLaaDef retrieveDoLaa(String doLaaCd) throws DAOException;

    /**
     * Retrieves the note for the given noteId
     * 
     * @param noteId
     * @return
     * @throws DAOException
     */
    Note retrieveNote(int noteId) throws DAOException;

    /**
     * Create a new note in the datastore.
     * 
     * @param newNote
     * @return
     * @throws DAOException
     */
    Note createNote(Note newNote) throws DAOException;

    /**
     * Modifies the given note in the datastore.
     * 
     * @param note
     * @return
     * @throws DAOException
     */
    boolean modifyNote(Note note) throws DAOException;

    /**
     * Removes the note, determined but the given noteId from the datastore.
     * 
     * @param noteId
     * @return
     * @throws DAOException
     */
    boolean removeNote(int noteId) throws DAOException;

    /**
     * Retrieves State associated with given stateCd
     * 
     * @param stateCd
     * @return State state associated with given stateCd
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    StateDef retrieveState(String stateCd) throws DAOException;

    /**
     * Retrieves All defined States
     * 
     * @return StateDef[] an array of all defined states
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    StateDef[] retrieveStates() throws DAOException;

    /**
     * Retrieves All defined Districts
     * 
     * @return DistrictDef[] an array of all defined districts
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    DistrictDef[] retrieveDistricts() throws DAOException;
    
    /**
     * Returns an array of all countries contained in persistent storage. In the
     * event of a data access failure, the exception is logged and then rethrown
     * as a <tt>DAOException</tt>.
     * 
     * @return CountryDef[] An array of CountryDef objects.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    CountryDef[] retrieveCountries() throws DAOException;

    /**
     * Returns a single <tt>CountryDef</tt> object given its unique ID. If no
     * corresponding persistent object is found, returns <tt>null</tt>. In
     * the event of a data access failure, the exception is logged and then
     * rethrown as a <tt>DAOException</tt>.
     * 
     * @param countryCd
     *            Unique country identifier.
     * 
     * @return CountryDef The associated country definition.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    CountryDef retrieveCountry(String countryCd) throws DAOException;

    /**
     * Adds a contactType to the given contactId.
     * 
     * @param contactId
     * @param contactTypeCd
     * @param startDate
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    void addContactType(Integer contactId, String contactTypeCd,
            Timestamp startDate) throws DAOException;
    
    /**
     * Adds a contactType to the given contactId including end date.
     * 
     * @param contactId
     * @param contactType
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    void addContactType(Integer contactId, ContactType contactType) throws DAOException;

    /**
     * Deprecates the contactType for the contact supplied in the contactType
     * object.
     * 
     * @param contactType
     * @throws DAOException
     */
    void deprecateContactType(ContactType contactType) throws DAOException;

    /**
     * Modifies the contactType for the contact supplied in the contactType
     * object.
     * 
     * @param contactType
     * @throws DAOException
     */
    void modifyContactType(ContactType contactType) throws DAOException;

    /**
     * deletes the contactType for the contact supplied in the contactType
     * object.
     * 
     * @param contactType
     * @throws DAOException
     */
    void deleteContactType(ContactType contactType) throws DAOException;
    
    /**
     * Retrieves all contactTypes associated with the contactId supplied.
     * 
     * @param contactId
     * @return
     * @throws DAOException
     */
    List<ContactType> retrieveContactTypes(int contactId) throws DAOException;

    /**
     * Creates the given Address
     * 
     * @param address
     *            address to save
     * @return Address address saved, complete with addressId
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    Address createAddress(Address address) throws DAOException;

    /**
     * Modifies the given Address
     * 
     * @param address
     *            address to modify
     * @return boolean
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyAddress(Address address) throws DAOException;
    
    /**
     * update address end date field
     * 
     * @param fpid
     *            facility.fp_id to modify
     * @return boolean
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean updateAddressEndDate(int fpid) throws DAOException;
    
    /**
     * Removes the given Address
     * 
     * @param addressId
     *            addressId of address to delete
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    void removeAddress(Integer addressId) throws DAOException;
    
    /**
     * Removes the given Contact
     * 
     * @param contactId
     *            contactId of contact to delete
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    void removeContact(Integer contactId) throws DAOException;

    /**
     * Creates the given SecurityGroup
     * 
     * @param SecurityGroup
     *            to save
     * @return SecurityGroup new group saved, complete with securityGroupId
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    SecurityGroup createSecurityGroup(SecurityGroup newGroup)
            throws DAOException;

    /**
     * Creates the useCase supplied in the datastore.
     * 
     * @param newUseCase
     * @return
     * @throws DAOException
     */
    UseCase createUseCase(UseCase newUseCase) throws DAOException;

    /**
     * Modifies the given Country
     * 
     * @param country
     *            to update
     * @return boolean true = country saved, false = failed
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyCounty(CountyDef county) throws DAOException;

    /**
     * Modifies the given column in the given table
     * 
     * @return boolean true = column update saved, false = failed
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyColumn(String dbTable, String keyColumn,
            String keyColumnType, String keyValue, String column,
            String columnType, String columnValue) throws DAOException;

    /**
     * Modifies the given Country
     * 
     * @param country
     *            country to update
     * @return boolean true = country saved, false = failed
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyCountry(CountryDef country) throws DAOException;

    /**
     * Modifies the given State
     * 
     * @param state
     *            state to update
     * @return boolean true = state saved, false = failed
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyState(StateDef state) throws DAOException;

    /**
     * Returns a single DaemonInfo object given its <tt>daemonCode</tt>
     * identifier. If no corresponding object was found, returns <tt>null</tt>.
     * A valid code has a maximum of four characters.
     * 
     * @param daemonCode
     *            Daemon code.
     * 
     * @return DaemonInfo Corresponding info object.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    DaemonInfo retrieveDaemonInfo(String daemonCode) throws DAOException;

    /**
     * Creates a new Daemon object in the database from this <tt>info</tt>.
     * Note that the daemon code must be unique or the insertion attempt will
     * fail. The description is not inserted by this request.
     * 
     * @param info
     *            DaemonInfo to be inserted.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    DaemonInfo createDaemonInfo(DaemonInfo info) throws DAOException;

    /**
     * Updates a Daemon object in the database with the values contained in
     * <code>info</code>. Returns <tt>true</tt> if a record was actually
     * updated and <tt>false</tt> if no update occurred, i.e., no
     * corresponding "daemon code" was found in the database.
     * 
     * @param info
     *            Info object containing updated values.
     * 
     * @return boolean <tt>true</tt> if a record was updated.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyDaemonInfo(DaemonInfo info) throws DAOException;

    /**
     * Returns an array of UserDef objects for a given system wide useCase
     * 
     * @return UserDef[] Array of UserDef Objects
     * 
     * @throws DAOException
     */
    Integer[] retrieveUsersWithUseCase(String useCase) throws DAOException;

    /**
     * Returns an array of user names, formatted "lastName, firstName" and their
     * corresponding userId.
     * 
     * @return SimpleIdDef[] Array of userId's and user name formatted as
     *         "lastName, firstName"
     * 
     * @throws DAOException
     */
    SimpleIdDef[] retrieveUserList(boolean excludeInactiveUsers) throws DAOException;
    
    /**
     * Retrieve UserDefs objects for all users marked as Inactive.
     * @return
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    UserDef[] retrieveInactiveUserDefs() throws DAOException;

    /**
     * Removes all roles for the given userId. This method is meant to be used
     * when updating a users roles, by first deleting them and adding the
     * updated ones.
     * 
     * @throws DAOException
     */
    void removeAllUserRoles(Integer userId) throws DAOException;

    /**
     * Removes all usecases for the given securityGroupID. This method is meant
     * to be used when updating a roles usecases, or when deleting a role.
     * 
     * @throws DAOException
     */
    void removeAllRoleUseCases(Integer securityGroupId) throws DAOException;

    /**
     * Add the supplied role to the supplied user.
     * 
     * @throws DAOException
     */
    void addUserRole(Integer userId, Integer securityGroupId)
            throws DAOException;

    /**
     * Add the supplied usecase to the supplied role.
     * 
     * @throws DAOException
     */
    void addUseCase(Integer securityId, Integer securityGroupId)
            throws DAOException;

    /**
     * Remove the supplied SecurityGroup.
     * 
     * @throws DAOException
     */
    void removeSecurityGroup(SecurityGroup group) throws DAOException;

    /**
     * Remove all users from the given groupId.
     * 
     * @throws DAOException
     */
    void removeUsersFromGroup(Integer groupId) throws DAOException;

    /**
     * Return an array of Desc and Id.
     * 
     * @throws DAOException
     */
    SimpleIdDef[] retrieveDescAndId(String sqlLoadString) throws DAOException;

    /**
     * Return an array of ReportDefs.
     * 
     * @throws DAOException
     */
    ReportDef[] retrieveReportDefs() throws DAOException;

    /**
     * Creates a new report_def entry.
     * 
     * @throws DAOException
     */
    ReportDef createReport(ReportDef report) throws DAOException;

    /**
     * Adds the supplied attribute to the given reportId.
     * 
     * @param bulkId
     * @param attrCd
     * @throws DAOException
     */
    void addReportAttribute(int reportId, ReportAttribute attr) throws DAOException;
    /**
     * Return an array of BulkDefs.
     * 
     * @throws DAOException
     */
    BulkDef[] retrieveBulkDefs(String bulkMenu) throws DAOException;

    /**
     * Return a BulkDef.
     * 
     * @param bulkId
     * @throws DAOException
     */
    BulkDef retrieveBulkDef(int reportId) throws DAOException;

    /**
     * Creates a new report_def entry.
     * 
     * @throws DAOException
     */
    BulkDef createBulkDef(BulkDef bulkdef) throws DAOException;

    /**
     * Adds the supplied attribute to the bulkId given.
     * 
     * @param bulkId
     * @param attrCd
     * @throws DAOException
     */
    void addBulkAttribute(int bulkId, String attrCd) throws DAOException;

    /**
     * Modifies an existing Bulk_Def.
     * 
     * @throws DAOException
     */
    boolean modifyBulkDef(BulkDef bulkdef) throws DAOException;

    /**
     * Removes all attributes from the given reportId.
     * 
     * @param reportId
     * @throws DAOException
     */
    void removeReportAttributes(int reportId) throws DAOException;

    /**
     * Modifies an existing Report_Def.
     * 
     * @throws DAOException
     */
    boolean modifyReport(ReportDef report) throws DAOException;
    
    /**
     * Removes an existing Report_Def.
     * 
     * @throws DAOException
     */
    void removeReport(ReportDef report) throws DAOException;

    /**
     * Build and return the requested JasperPrint object, for the frontend to
     * export.
     * 
     * @throws DAOException
     */
    JasperPrint retrieveReport(String jasperReportFileNm, Map<String, Object> reportParms)
            throws DAOException;

    /**
     * Retrieve report for the given reportId.
     * 
     * @param reportId
     * @return
     * @throws DAOException
     */
    ReportDef retrieveReport(int reportId) throws DAOException;

    /**
     * Retrieves an array of SimpleIdDefs for the supplied table name, id
     * column, description column, deprecated column and sort by criteria.
     * 
     * @param dbTable
     * @param dbIdColumn
     * @param dbDescColumn
     * @param dbDeprecatedColumn
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveSimpleIdDefs(String dbTable, String dbIdColumn,
            String dbDescColumn, String dbDeprecatedColumn, String sortByColumn)
            throws DAOException;

    /**
     * Retrieves an array of SimpleDefs for the supplied table name, id column,
     * description column, deprecated column and sort by criteria.
     * 
     * @param dbTable
     * @param dbCodeColumn
     * @param dbDescColumn
     * @param dbDeprecatedColumn
     * @return
     * @throws DAOException
     */
    SimpleDef[] retrieveSimpleDefs(String dbTable, String dbCodeColumn,
            String dbDescColumn, String dbDeprecatedColumn, String sortByColumn)
            throws DAOException;

    /**
     * Retrieves an array of SimpleIdDefs for the supplied table name, id
     * column, description column, deprecated column and sort by criteria and a
     * customized where clause.
     * 
     * @param dbTable
     * @param dbCodeColumn
     * @param dbDescColumn
     * @param dbDeprecatedColumn
     * @param sortByColumn
     * @param whereColumn
     * @param whereVal
     * @return
     * @throws DAOException
     */
    SimpleDef[] retrieveSimpleDefs(String dbTable, String dbCodeColumn,
            String dbDescColumn, String dbDeprecatedColumn,
            String sortByColumn, String whereColumn, String whereValue)
            throws DAOException;

    /**
     * Creates a simpleDef entry in the datastore.
     * 
     * @param dbTable
     * @param dbCodeColumn
     * @param dbDescColumn
     * @param SimpleDef
     * @return
     * @throws DAOException
     */
    SimpleDef createSimpleDef(String dbTable, String dbCodeColumn,
            String dbDescColumn, SimpleDef sd) throws DAOException;

    /**
     * Modifies a simpleDef entry in the datastore.
     * 
     * @param dbTable
     * @param dbCodeColumn
     * @param dbDescColumn
     * @param dbDeprecatedColumn
     * @param SimpleDef
     * @return
     * @throws DAOException
     */
    boolean modifySimpleDef(String dbTable, String dbCodeColumn,
            String dbDescColumn, String dbDeprecatedColumn, SimpleDef sd)
            throws DAOException;

    /**
     * Retrieves an array of SimpleDefs given a complete SQL string.
     * 
     * @param sqlLoadString
     * @return
     * @throws DAOException
     */
    SimpleDef[] retrieveDescAndCd(String sqlLoadString) throws DAOException;

    /**
     * Retrieves an array of BaseDB's that are retrieved by the supplied
     * sqlLoadString and populated using reflection and the supplied class.
     * 
     * @param sqlLoadString
     * @param objectToRetrieve
     * @return
     * @throws DAOException
     */
    <T extends BaseDB>BaseDef[] retrieveBaseDB(String sqlLoadString, Class<T> objectToRetrieve)
            throws DAOException;

    /**
     * This method is used in unit testing, to "reset" the DB, so creation
     * methods will work correctly.
     * 
     * @param tableName
     * @param columnName
     * @param columnValue
     * @throws DAOException
     */
    void removeRows(String tableName, String columnName, Object columnValue)
            throws DAOException;

    /**
     * Creates new field audit logs for the supplied facility and user.
     * 
     * @param newLog
     * @return
     * @throws DAOException
     */
    void createFieldAuditLogs(String facilityId,
            String facilityName, Integer userId, FieldAuditLog[] newLogs)
            throws DAOException;

    /**
     * Creates new field audit logs for the supplied company and user.
     * 
     * @param newLog
     * @return
     * @throws DAOException
     */
    void createCompanyFieldAuditLogs(String cmpId, Integer userId, FieldAuditLog[] newLogs)
            throws DAOException;

    
    /**
     * This method ONLY changes field audit logs where category_cd code of the entry
     * is fac (Facility), and only changes the unique_id, which will be the orig facility id.
     * 
     * @param origFacId
     * @param newFacId
     * @throws DAOException
     */
    void changeFacilityIdForFieldAuditLogs(String origFacId, String newFacId) throws DAOException;
    
    /**
     * Searches the field audit logs.
     * 
     * @param searchObj
     * @return
     * @throws DAOException
     */
    FieldAuditLog[] searchFieldAuditLog(FieldAuditLog searchObj)
            throws DAOException;

    /**
     * Retrieve SCC code based on scc ID.
     * 
     * @param sccCode
     * @return
     * @throws DAOException
     */
    SccCode retrieveSccCode(String sccCode) throws DAOException;

    /**
     * Retrieves SCC codes where the descriptions contains the key words in the
     * search list.
     * 
     * @param searchSccList
     * @return
     * @throws DAOException
     */
    SccCode[] retrieveSccCodes(List<String> searchSccList) throws DAOException;

    /**
     * Retrieves the default facility roles for the county supplied.
     * 
     * @param countyCd
     * @return
     * @throws DAOException
     */
    FacilityRoleDef[] retrieveDefaultFacilityRoles(String countyCd)
            throws DAOException;
    
    /**
     * Generate the next seueunce Id value.
     * 
     * @param sequenceId
     * @return
     * @throws DAOException
     */
    Integer nextSequenceIdValue(String sequenceId)
            throws DAOException;
    
    /**
     * Remove all contact types for a contact.
     * 
     * @param contactId
     * @return
     * @throws DAOException
     */
    void removeContactTypes(Integer contactId) throws DAOException;

    /**
     * Remove all contact types for a contact in Staging.
     * 
     * @param contactId
     * @return
     * @throws DAOException
     */
    void removeStagingContactTypes(Integer contactId, String facilityId) throws DAOException;
    
    /**
     * @param wrapnCD
     * @return
     * @throws DAOException
     */
    WrapnDef retrieveWrapnDef(String wrapnCD) throws DAOException;
    
    /**
     * @param newTask
     * @return
     * @throws DAOException
     */
    Task createTask(Task newTask) throws DAOException;

    /**
     * @param task
     * @return
     * @throws DAOException
     */
    boolean modifyTask(Task task) throws DAOException;
    
    /**
     * @param taskId
     * @throws DAOException
     */
    void deleteTask(String taskId) throws DAOException;
    
    /**
     * @param oldTaskId
     * @param newTaskId
     * @throws DAOException
     */
    void updateTasksDepTaskId(String oldTaskId, String newTaskId) throws DAOException;
    
    /**
     * @param task
     * @return
     * @throws DAOException
     */
    Task retrieveTask(String taskId) throws DAOException;
    
    /**
     * @param task
     * @return
     * @throws DAOException
     */
    Task[] retrieveTasks(Task filterTask) throws DAOException;
    
    /**
     * @param table
     * @return
     * @throws DAOException
     */
    Hashtable<String, DataGridCellDef> getMetaData(String table) throws DAOException;
    
    /**
     * @param id
     * @return
     * @throws DAOException
     */
    Integer retrieveRptUserId(Integer id) throws DAOException;
    
    /**
     * @param id
     * @return
     * @throws DAOException
     */
    Integer retrieveDorUserId(Integer id) throws DAOException;
    
    /**
     * @param id
     * @return
     * @throws DAOException
     */
    Integer retrieveReqestIdFromRelocate(Integer id) throws DAOException;
    
    /**
     * @param id
     * @return
     * @throws DAOException
     */
    Integer retrievePermitId(Integer id) throws DAOException;
    
    /**
     * @param id
     * @return
     * @throws DAOException
     */
    Integer applicationUserIdFromPermit(Integer id) throws DAOException;
    
    /**
     * @param id
     * @return
     * @throws DAOException
     */
    Integer applicationUserIdFromRelocate(Integer id) throws DAOException;
    
    /**
     * @param userId
     * @return Integer
     * @throws DAOException
     */
    UserDef retrieveContactByUserId(Integer userId) throws DAOException;
    
    <T extends BaseDB>SimpleIdDef[] retrieveBaseIdDB(String sqlLoadString, Class<T> objectToRetrieve) throws DAOException ;
    
    int getNextSequenceNumber(String sequenceName) throws DAOException;

	DaemonInfo retrieveDaemonInfo(String daemonCode, String hostname) throws DAOException;
	
	List<PollutantDef> retrievePollutantsByCategory(String category) throws DAOException;
	
	List<ForeignKeyReference> retrieveForeignKeyReferences(String tableName, String columnName,	String schema) throws DAOException;
	
	boolean checkForeignKeyReferencedData(String tableName,	String columnName, Integer fkValue) throws DAOException;
	
	/**
	 * @param login
	 * @return boolean
	 * @throws DAOException
	 */
	boolean retrieveUserStatusByLogin(String login) throws DAOException;

	void createFieldAuditLogs(
			Map<String, FieldAuditLog[]> facilityAuditLogMap, int userId) throws DAOException;

	public TimeSheetRow createTimesheetEntry(TimeSheetRow row) throws DAOException;

	public boolean modifyTimesheetEntry(TimeSheetRow modifyRow) throws DAOException;

	public TimeSheetRow retrieveTimesheetEntry(Integer rowId) throws DAOException;

	public void removeTimesheetEntry(TimeSheetRow modifyRow) throws DAOException;

	public Integer countAqdsTimesheetEntries() throws DAOException;

	public TimeSheetRow[] retrieveAqdsNsrTimesheetEntries() throws DAOException;

	public void createTimesheetEntries(TimeSheetRow[] timesheetEntries) 
			throws DAOException;

	public void assignImpactUser(TimeSheetRow entry) throws DAOException;

	public void deleteAllTimesheetEntries() throws DAOException;

	public Map<Integer,Timestamp> retrieveLastInvoiceReferenceDate(String nsrId) throws DAOException;

	public boolean addressIntersectsShape(Integer addressId, Integer shapeId)
			throws DAOException;

	public Shape[] retrieveShapes()
			throws DAOException;

	public boolean modifyShape(Shape modifyShape)
			throws DAOException;

	public Shape retrieveShape(Integer shapeId)
			throws DAOException;

	public void removeShape(Shape shape)
			throws DAOException;
	
	List<String> retrieveProjectIdsAssociatedWithShape(Integer shapeId)
			throws DAOException;
	
	List<SimpleDef> retrieveProjectTypesByAppUsr(Integer userId)
			throws DAOException;
	
	List<String> retrieveInactiveProjectIdsAssociatedWithShape(Integer shapeId)
			throws DAOException;
	public SystemPropertyDef[] retrieveSystemProperties() throws DAOException;

	public SystemPropertyDef retrieveSystemProperty(String systemPropCd) throws DAOException;
	
	public int getDefaultSearchLimit();
	
	public String getIndianReservationCdByLatLong(String latitude, String longitude) throws DAOException;

	public List<Integer> retrieveIndianReservationShapeIds() throws DAOException;

    public void deleteFacilityFieldAuditLogs(String facilityId) throws DAOException;

	public void removeAddressReference(Integer fpId) throws DAOException;
}

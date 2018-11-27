/*
 * DetailDataDAO.java
 *
 * Created on March 5, 2004, 12:32 PM
 */

package us.oh.state.epa.stars2.database.dao;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataTypeDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.EnumDetail;
import us.oh.state.epa.stars2.framework.exception.DAOException;

/**
 * 
 * @author montotop
 */
public interface DetailDataDAO extends TransactableDAO {

    /**
     * Returns the <code>CustomDetailTypeDef</code> object currently
     * associated with this id. If no custom detail type is found for this Id,
     * then <tt>null</tt> is returned.
     * 
     * @param id
     *            The custom detail type Id.
     * 
     * @return CustomDetailTypeDef the custom detail type for this Id.
     * 
     * @throws DAOException
     *             Database access error.
     */
    DataTypeDef retrieveDataTypeDef(int id) throws DAOException;

    DataDetail retrieveDataDetail(int id) throws DAOException;

    DataDetail retrieveDataDetailForWorkFlow(int process_id, int id)
            throws DAOException;

    DataDetail[] retrieveDataDetails() throws DAOException;

    SimpleDef[] retrieveUnitDefs() throws DAOException;

    SimpleDef retrieveUnitDef(String unitDefCd) throws DAOException;

    /**
     * Returns the <code>CustomDetailTypeDef</code> objects currently
     * associated with this subsystem id. If no custom detail types are found,
     * then <tt>null</tt> is returned.
     * 
     * @param subsystemId
     *            The subsystem Id (0 will return all custom detail types).
     * 
     * @return CustomDetailTypeDef[] the custom detail types for this subsystem
     *         id.
     * 
     * @throws DAOException
     *             Database access error.
     */
    DataTypeDef[] retrieveDataTypeDefs() throws DAOException;

    /**
     * Inserts a record into the Custom_Detail_Def table of the database.
     * 
     * @param cdtd
     *            CustomDetailTypeDef object to be inserted into the database.
     * 
     * @throws DAOException
     *             Database access error.
     */
    DataTypeDef createDataTypeDef(DataTypeDef cdtd) throws DAOException;

    DataDetail createDataDetail(DataDetail cdtd) throws DAOException;

    /**
     * Modifies a record into the Custom_Detail_Def table of the database.
     * 
     * @param cdtd
     *            CustomDetailTypeDef object to be modified in the database.
     * 
     * @throws DAOException
     *             Database access error.
     */
    boolean modifyDataTypeDef(DataTypeDef cdtd) throws DAOException;

    boolean modifyDataDetail(DataDetail cdtd) throws DAOException;

    /**
     * Deletes the Custom_Detail_Type_Def record with Custom_Detail_Type_Id =
     * <tt>typeDefId</tt> from the database.
     * 
     * @param typeDefId
     *            Custom_Detail_Type_Id for the record to be deleted from the
     *            database.
     * 
     * @throws DAOException
     *             Database access error.
     */
    void removeDataTypeDefById(int typeDefId) throws DAOException;

    void removeDataDetail(int typeDefId) throws DAOException;

    /**
     * Returns the <code>EnumDetail</code> object currently associated with
     * this enum detail id. If no enum details are found for this enum detail
     * id, then <tt>null</tt> is returned.
     * 
     * @param enumDetailId
     *            the enum detail id of the object requested.
     * 
     * @return EnumDetail the enum detail object for this enum detail id.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    EnumDetail retrieveEnumDetail(int enumDetailId) throws DAOException;

    /**
     * Returns all of the <code>EnumDetail</code> objects currently associated
     * with this enum code. If no enum details are found for this enum code,
     * then <tt>null</tt> is returned.
     * 
     * @param enumCd
     *            The enum code of the object requested.
     * 
     * @return EnumDetail[] All enum details for this enum cd.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    EnumDetail[] retrieveEnumDetails(String enumCd) throws DAOException;

    /**
     * Returns all of the <code>EnumDetail</code> objects currently defined in
     * the system. If no enum details are found, then an empty array is
     * returned.
     * 
     * @return EnumDetail[] All enum details in the system.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    EnumDetail[] retrieveAllEnumDetails() throws DAOException;

    /**
     * Returns all of the <code>EnumDef</code> objects currently defined in
     * the system. If no enum defs are found, then an empty array is returned.
     * 
     * @return EnumDef[] All enum details in the system.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    SimpleDef[] retrieveAllEnumDefs() throws DAOException;

    /**
     * Inserts a record into the Enum_Detail_Def table of the database.
     * 
     * @param ed
     *            EnumDetail object to be inserted into the database.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    EnumDetail createEnumDetail(EnumDetail ed) throws DAOException;

    /**
     * Inserts a record into the Enum_Def table of the database.
     * 
     * @param enumCd
     *            enum code.
     * @param enumDsc
     *            enum description.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    SimpleDef createEnumDef(String enumCd, String enumDsc) throws DAOException;

    /**
     * Updates the corresponding Enum_Detail_Def record in the database to
     * correspond to the contents of <code>ed</code>. Returns <tt>true</tt>
     * if a record was updated and <tt>false</tt> if no actual database update
     * occurred, i.e., the enum Id was not found in the database.
     * 
     * @param ed
     *            EnumDetail that needs to be updated in the database.
     * 
     * @return <tt>true</tt> if a database record was updated, otherwise
     *         <tt>false</tt>.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.DAOException
     */
    boolean modifyEnumDetail(EnumDetail ed) throws DAOException;

    void removeEnumDetails(String enumCd) throws DAOException;

    void createEquipmentDataXref(String equipmentTypeCd, Integer id) throws DAOException;
}

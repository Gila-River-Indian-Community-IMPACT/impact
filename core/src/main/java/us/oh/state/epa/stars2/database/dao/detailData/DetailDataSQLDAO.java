package us.oh.state.epa.stars2.database.dao.detailData;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.DetailDataDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataTypeDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.EnumDetail;
import us.oh.state.epa.stars2.framework.exception.DAOException;

/**
 * Title: DetailDataSQLDAO.
 * 
 * <p>
 * Description: Accesses service Detail information for a given service order.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author S. A. Wooster
 * @version 1.0
 */

@Repository
public class DetailDataSQLDAO extends AbstractDAO implements DetailDataDAO {
    public final DataTypeDef retrieveDataTypeDef(int id) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveDataTypeDef", true);

        connHandler.setInteger(1, id);

        return (DataTypeDef) connHandler.retrieve(DataTypeDef.class);
    }

    public final DataDetail retrieveDataDetail(int id) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveDataDetail", true);

        connHandler.setInteger(1, id);
        DataDetail ret = (DataDetail) connHandler.retrieve(DataDetail.class);

        if (ret != null && ret.getEnumCd() != null) {
            populateEnums(ret);
        }

        return ret;
    }

    /**
     * @param ret
     * @throws DAOException
     * 
     */
    private void populateEnums(DataDetail ret) throws DAOException {
        ret.setEnumDetails(retrieveEnumDetails(ret.getEnumCd()));
        // setDataTypeNm(enumDetails.get(0).getEnumDsc());
        // Cannot set to other name. This is using in Workflow.
        // Yehpo
    }

    public final DataDetail retrieveDataDetailForWorkFlow(int process_id, int id)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveDataDetailForWorkFlow", true);

        connHandler.setInteger(1, process_id);
        connHandler.setInteger(2, id);

        DataDetail ret = (DataDetail) connHandler.retrieve(DataDetail.class);

        if (ret != null && ret.getEnumCd() != null) {
            populateEnums(ret);
        }

        return ret;
    }

    public final DataDetail[] retrieveDataDetails() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveDataDetails", true);

        ArrayList<DataDetail> ret = connHandler
                .retrieveArray(DataDetail.class);

        for (BaseDBObject dbo : ret) {
            DataDetail dd = (DataDetail) dbo;
            if (dd.getEnumCd() != null) {
                populateEnums(dd);
            }
        }
        return ret.toArray(new DataDetail[0]);
    }

    public final SimpleDef[] retrieveUnitDefs() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveUnitDefs", true);

        ArrayList<SimpleDef> ret = connHandler
                .retrieveArray(SimpleDef.class);

        return ret.toArray(new SimpleDef[0]);
    }

    public final SimpleDef retrieveUnitDef(String unitDefCd) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveUnitDef", true);

        connHandler.setString(1, unitDefCd);

        return (SimpleDef) connHandler.retrieve(SimpleDef.class);
    }

    public final DataTypeDef createDataTypeDef(DataTypeDef sdtd) throws DAOException {
        DataTypeDef ret = sdtd;

        Integer sdtdId = nextSequenceVal("S_Data_Type_Id");

        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.createDataTypeDef", false);

        connHandler.setInteger(1, sdtdId);
        connHandler.setString(2, sdtd.getDataTypeNm());

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setDataTypeId(sdtdId);
        ret.setLastModified(1);

        return ret;
    }

    public final DataDetail createDataDetail(DataDetail dd) throws DAOException {
        String enumCd = dd.getEnumCd();
        if ((enumCd != null) && (enumCd.length() == 0)) {
            enumCd = null;
        }

        String unitDefCd = dd.getUnitDefCd();

        if ((unitDefCd != null) && (unitDefCd.length() == 0)) {
            unitDefCd = null;
        }

        DataDetail ret = dd;

        Integer ddId = nextSequenceVal("S_Data_Detail_Id");

        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.createDataDetail", false);

        int i = 1;
        connHandler.setInteger(i++, ddId);
        connHandler.setString(i++, dd.getDataDetailDsc());
        connHandler.setString(i++, dd.getDataDetailLbl());
        connHandler.setString(i++, unitDefCd);
        connHandler.setString(i++, enumCd);
        connHandler.setInteger(i++, dd.getDataTypeId());
        connHandler.setString(i++, dd.getDataDetailVal());
        connHandler.setString(i++, dd.getMinVal());
        connHandler.setString(i++, dd.getMaxVal());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(dd
                .isReadOnly()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(dd
                .isRequired()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(dd
                .isVisible()));
        connHandler.setString(i++, dd.getFormatMask());

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setDataDetailId(ddId);
        ret.setLastModified(1);

        return ret;
    }

    public final boolean modifyDataTypeDef(DataTypeDef sdtd) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.modifyDataTypeDef", false);

        int i = 1;
        connHandler.setString(i++, sdtd.getDataTypeNm());
        connHandler.setInteger(i++, sdtd.getLastModified() + 1);
        connHandler.setInteger(i++, sdtd.getDataTypeId());
        connHandler.setInteger(i++, sdtd.getLastModified());

        return connHandler.update();
    }

    public final boolean modifyDataDetail(DataDetail dd) throws DAOException {
        String enumCd = dd.getEnumCd();

        if ((enumCd != null) && (enumCd.length() == 0)) {
            enumCd = null;
        }

        String unitDefCd = dd.getUnitDefCd();

        if ((unitDefCd != null) && (unitDefCd.length() == 0)) {
            unitDefCd = null;
        }

        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.modifyDataDetail", false);

        int i = 1;
        connHandler.setString(i++, dd.getDataDetailLbl());
        connHandler.setString(i++, dd.getDataDetailDsc());
        connHandler.setString(i++, unitDefCd);
        connHandler.setString(i++, enumCd);
        connHandler.setInteger(i++, dd.getDataTypeId());
        connHandler.setString(i++, dd.getDataDetailVal());
        connHandler.setString(i++, dd.getMinVal());
        connHandler.setString(i++, dd.getMaxVal());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(dd
                .isReadOnly()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(dd
                .isRequired()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(dd
                .isVisible()));
        connHandler.setString(i++, dd.getFormatMask());
        connHandler.setInteger(i++, dd.getLastModified() + 1);
        connHandler.setInteger(i++, dd.getDataDetailId());
        connHandler.setInteger(i++, dd.getLastModified());

        return connHandler.update();
    }

    public final void removeDataTypeDefById(int typeDefId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.removeDataTypeDef", false);

        connHandler.setInteger(1, typeDefId);

        connHandler.remove();
    }

    public final void removeDataDetail(int id) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.removeDataDetail", false);

        connHandler.setInteger(1, id);

        connHandler.remove();
    }

    public final DataTypeDef[] retrieveDataTypeDefs() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveAllDataTypes", true);

        ArrayList<DataTypeDef> ret = connHandler
                .retrieveArray(DataTypeDef.class);

        return ret.toArray(new DataTypeDef[0]);
    }

    /**
     * @see com.mentorgen.database.dao.InfrastructureDAO#retrieveEnumDetails(String)
     */
    public final EnumDetail[] retrieveEnumDetails(String enumCd) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveEnumDetails", true);

        connHandler.setString(1, enumCd);

        ArrayList<EnumDetail> ret = connHandler
                .retrieveArray(EnumDetail.class);

        return ret.toArray(new EnumDetail[0]);
    }

    /**
     * @see com.mentorgen.database.dao.InfrastructureDAO#retrieveAllEnumDefs()
     */
    public final SimpleDef[] retrieveAllEnumDefs() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveAllEnumDefs", true);

        ArrayList<SimpleDef> ret = connHandler
                .retrieveArray(SimpleDef.class);

        return ret.toArray(new SimpleDef[0]);
    }

    /**
     * @see com.mentorgen.database.dao.InfrastructureDAO#retrieveAllEnumDetails()
     */
    public final EnumDetail[] retrieveAllEnumDetails() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveAllEnumDetails", true);

        ArrayList<EnumDetail> ret = connHandler
                .retrieveArray(EnumDetail.class);

        return ret.toArray(new EnumDetail[0]);
    }

    /**
     * @see com.mentorgen.database.dao.InfrastructureDAO#modifyEnumDetail(EnumDetail)
     */
    public final boolean modifyEnumDetail(EnumDetail ed) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.modifyEnumDetail", false);

        int i = 1;
        connHandler.setInteger(i++, ed.getEnumDetailId());
        connHandler.setString(i++, ed.getEnumLabel());
        connHandler.setInteger(i++, ed.getLastModified() + 1);
        connHandler.setString(i++, ed.getEnumValue());
        connHandler.setInteger(i++, ed.getLastModified());

        return connHandler.update();
    }

    /**
     * @see com.mentorgen.database.dao.InfrastructureDAO#createEnumDef(String,
     *      String)
     */
    public final SimpleDef createEnumDef(String enumCd, String enumDsc)
            throws DAOException {
        SimpleDef ret = null;
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.createEnumDef", false);

        connHandler.setString(1, enumCd);
        connHandler.setString(2, enumDsc);

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret = new SimpleDef();
        ret.setCode(enumCd);
        ret.setDescription(enumDsc);
        ret.setLastModified(1);

        return ret;
    }

    /**
     * @see com.mentorgen.database.dao.InfrastructureDAO#createEnumDetail(EnumDetail)
     */
    public final EnumDetail createEnumDetail(EnumDetail ed) throws DAOException {
        EnumDetail ret = ed;

        Integer detailId = nextSequenceVal("S_Enum_Detail_Id");

        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.createEnumDetail", false);

        int i = 1;
        connHandler.setInteger(i++, detailId);
        connHandler.setString(i++, ed.getEnumCd());
        connHandler.setString(i++, ed.getEnumLabel());
        connHandler.setString(i++, ed.getEnumValue());

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setEnumDetailId(detailId);
        ret.setLastModified(1);

        return ret;
    }

    public final void removeEnumDetails(String enumCd) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.removeEnumDefByCd", false);

        connHandler.setString(1, enumCd);

        connHandler.remove();
    }

    /**
     * @see com.mentorgen.database.dao.InfrastructureDAO#retrieveEnumDetail(int)
     */
    public final EnumDetail retrieveEnumDetail(int enumDetailId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.retrieveEnumDetailById", true);

        connHandler.setInteger(1, enumDetailId);

        return (EnumDetail) connHandler.retrieve(EnumDetail.class);
    }

    /**
     * @see com.mentorgen.database.dao.InfrastructureDAO#createEquipmentDataXref(String,
     *      int)
     */
    public final void createEquipmentDataXref(String equipmentTypeCd, Integer id)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DetailDataSQL.addEquipmentDetailData", false);

        connHandler.setString(1, equipmentTypeCd);
        connHandler.setInteger(2, id);

        connHandler.update();
    }
}

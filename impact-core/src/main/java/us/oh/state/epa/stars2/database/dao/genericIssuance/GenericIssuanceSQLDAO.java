
package us.oh.state.epa.stars2.database.dao.genericIssuance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.GenericIssuanceDAO;
import us.oh.state.epa.stars2.database.dbObjects.genericIssuance.GenericIssuance;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class GenericIssuanceSQLDAO extends AbstractDAO implements GenericIssuanceDAO {

    /*
     * @param issuanceId Database ID of the GenericIssuance to be retrieved.
     * @return 
     * @throws DAOException
     */
    public GenericIssuance retrieveGenericIssuance(Integer issuanceId)
        throws DAOException {

        checkNull(issuanceId);
        ConnectionHandler connHandler 
            = new ConnectionHandler("GenericIssuanceSQL.retrieveGenericIssuance", true);
                                                              
        connHandler.setInteger(1, issuanceId.intValue());

        return (GenericIssuance) connHandler.retrieve(GenericIssuance.class);
    }

    /*
     * @param applicationId ID of an Application for which GenericIssuances
     *                      should be returned.
     * @return 
     * @throws DAOException
     */
    public List<GenericIssuance> searchGenericIssuances(String facilityId,
                                                        Integer applicationId,
                                                        Integer permitId,
                                                        String typeCd,
                                                        int maxNumberOfHits)
        throws DAOException {

        PreparedStatement ps = null;
        Connection conn = null;

        ArrayList<GenericIssuance> retList = new ArrayList<GenericIssuance>();

        try {
            conn = getReadOnlyConnection();
            StringBuffer sql 
                = new StringBuffer(loadSQL("GenericIssuanceSQL.searchGenericIssuances"));
                                                    
            String conjuct = " WHERE ";

            if (facilityId != null && facilityId.length() != 0) {
                if (facilityId.contains("*")) {
                    facilityId = facilityId.replace('*', '%');
                }
                sql.append(conjuct);
                conjuct = " AND ";
                if (facilityId.contains("%")) {
                    sql.append("LOWER(facility_id) like LOWER('" + facilityId + "')");
                } 
                else {
                    sql.append("LOWER(facility_id) = LOWER('" + facilityId + "')");
                }
            }

            if (applicationId != null) {
                sql.append(conjuct);
                conjuct = " AND ";
                sql.append("application_id = " + applicationId.intValue());
            }

            if (permitId != null) {
                sql.append(conjuct);
                conjuct = " AND ";
                sql.append("permit_id = " + permitId.intValue());
            }

            if (typeCd != null && typeCd.length() != 0) {
                sql.append(conjuct);
                conjuct = " AND ";
                sql.append("type_cd = '" + typeCd + "'");
            }

            ps = conn.prepareStatement(sql.toString());

            // maxNumberOfHits == 0 means ignore maxNumberOfHits.
            int numberOfHits = 0;
            if (maxNumberOfHits == 0) {
                numberOfHits = -1;
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next() && numberOfHits < maxNumberOfHits) {
                GenericIssuance gi = new GenericIssuance();
                gi.populate(rs);
                retList.add(gi);
                if (maxNumberOfHits > 0) {
                    numberOfHits++;
                }
            }

            rs.close();
        }
        catch (Exception ex) {
            handleException(ex, conn);
        } 
        finally {
            closeStatement(ps);
            handleClosing(conn);
        }
        return retList;

    }

    /*
     * @param GenericIssuance GenericIssuance to be created.
     * @return 
     * @throws DAOException
     */
    public GenericIssuance createGenericIssuance(GenericIssuance issuance)
        throws DAOException {

        checkNull(issuance);

        GenericIssuance retGI = issuance;

        ConnectionHandler connHandler 
            = new ConnectionHandler("GenericIssuanceSQL.createGenericIssuance", false);

        int id = nextSequenceVal("S_Generic_Issuance_Id");
        int i = 1;
        connHandler.setInteger(i++, id);
        connHandler.setString(i++, issuance.getIssuanceTypeCd());
        connHandler.setString(i++, issuance.getFacilityId());
        connHandler.setInteger(i++, issuance.getApplicationId());
        connHandler.setInteger(i++, issuance.getPermitId());
        connHandler.setTimestamp(i++, issuance.getIssuanceDate());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(issuance.isIssued()));
        connHandler.setString(i++, issuance.getPublicNoticeText());
        if (issuance.getIssuanceDoc() != null) {
            connHandler.setInteger(i++, issuance.getIssuanceDoc().getDocumentID());
        }
        else {
            connHandler.setInteger(i++, null);
        }
        if (issuance.getAddrLabelDoc() != null) {
            connHandler.setInteger(i++, issuance.getAddrLabelDoc().getDocumentID());
        }
        else {
            connHandler.setInteger(i++, null);
        }
        connHandler.setFloat(i++, issuance.getFeeAmount());

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        retGI.setIssuanceId(id);
        retGI.setLastModified(1);

        return null;
    }

    /*
     * @param GenericIssuance GenericIssuance to be modified.
     * @return 
     * @throws DAOException
     */
    public void modifyGenericIssuance(GenericIssuance issuance)
        throws DAOException {

        checkNull(issuance);
        ConnectionHandler connHandler 
            = new ConnectionHandler("GenericIssuanceSQL.modifyGenericIssuance", false);

        int i = 1;
        connHandler.setString(i++, issuance.getIssuanceTypeCd());
        connHandler.setString(i++, issuance.getFacilityId());
        connHandler.setInteger(i++, issuance.getApplicationId());
        connHandler.setInteger(i++, issuance.getPermitId());
        connHandler.setTimestamp(i++, issuance.getIssuanceDate());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(issuance.isIssued()));
        connHandler.setString(i++, issuance.getPublicNoticeText());
        if (issuance.getIssuanceDoc() != null) {
            connHandler.setInteger(i++, issuance.getIssuanceDoc().getDocumentID());
        }
        else {
            connHandler.setInteger(i++, null);
        }
        if (issuance.getAddrLabelDoc() != null) {
            connHandler.setInteger(i++, issuance.getAddrLabelDoc().getDocumentID());
        }
        else {
            connHandler.setInteger(i++, null);
        }
        connHandler.setFloat(i++, issuance.getFeeAmount());
        connHandler.setInteger(i++, issuance.getLastModified() + 1);

        connHandler.setInteger(i++, issuance.getIssuanceId());
        connHandler.setInteger(i++, issuance.getLastModified());

        connHandler.update();

        return;
    }

    public final void deleteGenericIssuance(GenericIssuance issuance) throws DAOException {
        checkNull(issuance);
        
        removeRows("is_generic_issuance", "issuance_id", issuance.getIssuanceId());

    }
}

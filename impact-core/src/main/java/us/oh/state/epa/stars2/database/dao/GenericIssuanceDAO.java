
package us.oh.state.epa.stars2.database.dao;

import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.genericIssuance.GenericIssuance;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface GenericIssuanceDAO extends TransactableDAO {

    /*
     * @param issuanceId Database ID of the GenericIssuance to be retrieved.
     * @return 
     * @throws DAOException
     */
    public GenericIssuance retrieveGenericIssuance(Integer issuanceId)
        throws DAOException;

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
        throws DAOException;

    /*
     * @param GenericIssuance GenericIssuance to be created.
     * @return 
     * @throws DAOException
     */
    public GenericIssuance createGenericIssuance(GenericIssuance issuance)
        throws DAOException;

    /*
     * @param GenericIssuance GenericIssuance to be modified.
     * @return 
     * @throws DAOException
     */
    public void modifyGenericIssuance(GenericIssuance issuance)
        throws DAOException;

    public void deleteGenericIssuance(GenericIssuance issuance) throws DAOException;

}

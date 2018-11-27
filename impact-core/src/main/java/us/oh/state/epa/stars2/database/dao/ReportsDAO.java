package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.report.ApplicationCount;
import us.oh.state.epa.stars2.database.dbObjects.report.FacilityPermitCount;
import us.oh.state.epa.stars2.database.dbObjects.report.GenericIssuanceCount;
import us.oh.state.epa.stars2.database.dbObjects.report.Issuance;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtioDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceTvDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuedMetricsData;
import us.oh.state.epa.stars2.database.dbObjects.report.PBRCount;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitCount;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitSOPData;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitSOPParams;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitTime;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitWorkers;
import us.oh.state.epa.stars2.database.dbObjects.report.SimpleIssuanceReason;
import us.oh.state.epa.stars2.database.dbObjects.report.SimplePermitId;
import us.oh.state.epa.stars2.database.dbObjects.report.TOPSData;
import us.oh.state.epa.stars2.database.dbObjects.report.WorkloadData;
import us.oh.state.epa.stars2.database.dbObjects.report.WorkloadTrend;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.framework.exception.DAOException;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePbrDetails;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtiDetails;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceSptoDetails;

public interface ReportsDAO extends TransactableDAO {
    /**
     * Returns all of the <tt>Issuance</tt> objects associated with the
     * specified time period.
     * 
     * @param startDt
     *            start timestamp for the given period
     * @param endDt
     *            end timestamp for the given period
     * 
     * @return Issuance[] All Issued permit data for this time period.
     * 
     * @throws DAOException
     *             Database access error.
     */
    Issuance[] retrieveIssuedPermits(Timestamp startDt, Timestamp endDt)
            throws DAOException;

    /**
     * @return
     * @throws DAOException
     */
    PermitSOPParams[] retrievePermitParams() throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveIssuedPbrsByDoLaa(Timestamp startDt,
            Timestamp endDt, String doLaaName) throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveIssuedPermitsByDoLaaByType(Timestamp startDt,
            Timestamp endDt, String permitType, String issuanceType,
            String doLaaName) throws DAOException;
    
    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveExpiredPermitsByDoLaaByType(Timestamp startDt,
            Timestamp endDt, String permitType, String issuanceType,
            String doLaaName) throws DAOException;

    /**
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @param userId
     * @param activityName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveActivePermitsByDoLaaByType(String permitType,
            String issuanceType, String doLaaName, Integer userId,
            String activityName, String activityStatusCd) throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveIssuedPermitsByIssuanceType(Timestamp startDt,
            Timestamp endDt, String permitType, String doLaaName)
            throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveExpiredPermitsByIssuanceType(Timestamp startDt,
            Timestamp endDt, String permitType, String doLaaName) 
            throws DAOException;

    /**
     * @param permitType
     * @param doLaaName
     * @param userId
     * @param activityName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveActivePermitsByIssuanceType(String permitType,
            String doLaaName, Integer userId, String activityName, 
            String activityStatusCd)
            throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveIssuedPermitsByGeneralByType(Timestamp startDt,
            Timestamp endDt, String permitType, String issuanceType,
            String doLaaName) throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveExpiredPermitsByGeneralByType(Timestamp startDt,
            Timestamp endDt, String permitType, String issuanceType,
            String doLaaName) throws DAOException;

    /**
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @param userId
     * @param activityName
     * @return
     * @throws DAOException
     */
    SimpleIdDef[] retrieveActivePermitsByGeneralByType(String permitType,
            String issuanceType, String doLaaName, Integer userId,
            String activityName, String activityStatusCd) throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @return
     * @throws DAOException
     */
    ApplicationCount[] retrieveApplicationCountByType(Timestamp startDt, Timestamp endDt)
        throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @return
     * @throws DAOException
     */
    PermitCount[] retrieveIssuedFinalPermitsCountByType(Timestamp startDt, Timestamp endDt) 
        throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @return
     * @throws DAOException
     */
    PBRCount[] retrievePbrCountsByType(Timestamp startDt, Timestamp endDt) 
        throws DAOException;

    /**
     *
     * @param endDt
     * @return
     * @throws DAOException
     */
    FacilityPermitCount[] retrieveFacilityCountsByPermitType(Timestamp endDt)
        throws DAOException;


    /**
     *
     * @return
     * @throws DAOException
     */
    PermitTime[] retrieveInProcessPermitTime() throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIssuanceReason[] retrieveIssuedPermitsByReasonByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimpleIssuanceReason[] retrieveExpiredPermitsByReasonByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException;

    /**
     * @param permitType
     * @param issuanceState
     * @param doLaaName
     * @param userId
     * @param activityName
     * @return
     * @throws DAOException
     */
    SimpleIssuanceReason[] retrieveActivePermitsByReasonByType(
            String permitType, String issuanceState, String doLaaName,
            Integer userId, String activityName, String activityStatusCd) 
            throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimplePermitId[] retrieveIssuedPermitsByFinalByType(Timestamp startDt,
            Timestamp endDt, String permitType, String issuanceType,
            String doLaaName) throws DAOException;
    
    /**
     * @param startDt
     * @param endDt
     * @param permitType
     * @param issuanceType
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    SimplePermitId[] retrieveExpiredPermitsByFinalByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException;

    /**
     * @param permitType
     * @return
     * @throws DAOException
     */
    SimplePermitId[] retrieveIssuedPermitsByDraftByType(String permitType)
            throws DAOException;
    
    /**
     * @param permitType
     * @param startDt
     * @param endDt
     * @return
     * @throws DAOException
     */
    SimplePermitId[] retrieveExpiredPermitsByDraftByType(String permitType,
            Timestamp startDt, Timestamp endDt)
            throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @param issuanceType
     * @return
     * @throws DAOException
     */
    IssuancePtioDetails[] retrieveIssuancePtioDetails(Timestamp startDt,
            Timestamp endDt, String doLaaName, String issuanceType)
            throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @param issuanceType
     * @return
     * @throws DAOException
     */
    IssuancePtioDetails[] retrieveExpiredPtioDetails(Timestamp startDt,
            Timestamp endDt, String doLaaName, boolean hideShutdownFacility,boolean hideExemptPBR,
            boolean hideEUPermitStatusTRS,boolean hideEUExemptionDmPe,boolean hideEUShutdownInvalid,
            boolean hidePtoPtioEuActivePBR, String issuanceType)
            throws DAOException;
    
    /**
     * 
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @param issuanceType
     * @return
     * @throws DAOException
     */
    /*
    IssuanceSptoDetails[] retrieveExpiredSptoDetails(Timestamp startDt, Timestamp endDt,
            String doLaaName, boolean hideShutdownFacility,boolean hideExemptPBR,
            boolean hideEUPermitStatusTRS,boolean hideEUExemptionDmPe,boolean hideEUShutdownInvalid,
            boolean hidePtoPtioEuActivePBR, String issuanceType) 
    		throws DAOException;
*/
    /**
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @param issuanceType
     * @return
     * @throws DAOException
     */
    /*
    IssuancePtiDetails[] retrieveIssuancePtiDetails(Timestamp startDt,
            Timestamp endDt, String doLaaName, String issuanceType)
            throws DAOException;
*/

    /**
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @param issuanceType
     * @return
     * @throws DAOException
     */
    /*
    IssuancePtiDetails[] retrieveExpiredPtiDetails(Timestamp startDt,
            Timestamp endDt, String doLaaName, String issuanceType)
            throws DAOException;
            */
    /**
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @param issuanceType
     * @return
     * @throws DAOException
     */
    IssuanceTvDetails[] retrieveIssuanceTvDetails(Timestamp startDt,
            Timestamp endDt, String doLaaName, String issuanceType)
            throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @param issuanceType
     * @return
     * @throws DAOException
     */
    IssuanceTvDetails[] retrieveExpiredTvDetails(Timestamp startDt,
            Timestamp endDt, String doLaaName, boolean hideShutdownFacility,boolean hideExemptPBR,
            boolean hideEUPermitStatusTRS,boolean hideEUExemptionDmPe,boolean hideEUShutdownInvalid,
            boolean hidePtoPtioEuActivePBR, String issuanceType)
            throws DAOException;

    /**
     * @param startDt
     * @param endDt
     * @param doLaaName
     * @return
     * @throws DAOException
     */
    /*
    IssuancePbrDetails[] retrieveIssuancePbrDetails(Timestamp startDt,
            Timestamp endDt, String doLaaName) throws DAOException;
*/
    /**
     * @param doLaas
     * @return
     * @throws DAOException
     */
    PermitWorkers[] retrievePermitWorkers(List<String> doLaas)
            throws DAOException;

    /**
     * @param doLaas
     * @return
     * @throws DAOException
     */
    WorkloadData[] retrievePermitWorkload(List<String> doLaas, String activityStatusCd)
            throws DAOException;

    /**
     * @param pa
     * @param doLaas
     * @return
     * @throws DAOException
     */
    ProcessActivity[] retrieveActivityList(ProcessActivity pa,
            String doLaas) throws DAOException;

    /**
     * @param doLaas
     * @param permitTypes
     * @param reasonCds
     * @param general
     * @param express
     * @return
     * @throws DAOException
     */
    PermitSOPData[] retrievePermitSOPData(List<String> doLaas,
            List<String> permitTypes, List<String> reasonCds, boolean general,
            boolean express,boolean hideShutdownFacility, boolean hideDeadEndedPermit) throws DAOException;
    
    /**
     * @param doLaas
     * @param permitTypes
     * @param reasonCds
     * @param general
     * @param express
     * @return
     * @throws DAOException
     */
    PermitSOPData[] retrieveLatePermitsData(List<String> doLaas,
            List<String> permitTypes, List<String> reasonCds, boolean general,
            boolean express) throws DAOException;


    /**
     * @param reportDate
     * @return
     * @throws DAOException
     */
    TOPSData retrieveTOPSData(Timestamp reportDate) throws DAOException;

    /**
     * @param doLaas
     * @param permitTypes
     * @param reasonCds
     * @param fromDate p.start_dt
     * @param toDate p.start_dt
     * @return
     * @throws DAOException
     */
    PermitSOPData[] retrievePreliminaryReviewCompletedData(List<String> doLaas,
            List<String> permitTypes, List<String> reasonCds,
            Timestamp fromDate, Timestamp toDate) throws DAOException;

    IssuedMetricsData[] retrieveIssuedMetricsData(List<String> doLaas,
            String permitType, List<String> reasonCds, List<String> countyCds, 
            List<String> selectedPermitActionTypes, String dateBy, Timestamp fromDate,
            Timestamp toDate) throws DAOException;
    
    IssuedMetricsData[] retrieveNotIssuedMetricsData(List<String> doLaas,
            String permitType, List<String> reasonCds, List<String> countyCds, 
            List<String> selectedPermitActionTypes, String dateBy, Timestamp fromDate,
            Timestamp toDate, boolean excludeDeadEnded, boolean excludeIssuedWithdrawal) throws DAOException;

    Issuance[] retrievePermitExpiration(Timestamp startDt, Timestamp endDt, 
    		boolean hideShutdownFacility, boolean hideExemptPBR, boolean hideEUPermitStatusTRS,
    	    boolean hideEUExemptionDmPe, boolean hideEUShutdownInvalid, boolean hidePtoPtioEuActivePBR) throws DAOException;
/*
    Issuance[] retrievePTIPermitExpiration(Timestamp startDt, Timestamp endDt) throws DAOException;
*/
    WorkloadTrend[] retrieveWorkloadTrendReport(String[] permitTypes, String[] permitReasons) throws DAOException;

    GenericIssuanceCount[] retrieveGenericIssuanceCount(String[] doLaaCds, String[] issuanceTypeCds, Timestamp startDt, Timestamp endDt) throws DAOException;
}

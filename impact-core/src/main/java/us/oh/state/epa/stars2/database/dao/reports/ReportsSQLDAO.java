package us.oh.state.epa.stars2.database.dao.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ReportsDAO;
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
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.DOLAA;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePbrDetails;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtiDetails;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceSptoDetails;

/**
 * <p>
 * Title: ReportsSQLDAO
 * </p>
 * 
 * <p>
 * Description: This is the SQL implementation of the ReportsDAO.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author S. A. Wooster
 */

@Repository
public class ReportsSQLDAO extends AbstractDAO implements ReportsDAO {

	private Logger logger = Logger.getLogger(ReportsSQLDAO.class);
	
	public static final String START_DATE = "p.start_dt";
	public static final String PUBLIC_NOTICE_PUBLISH_DATE = "pid.public_notice_publish_date";
	public static final String FINAL_ISSUANCE_DATE = "pif.issuance_date";
	public static final String END_DATE = "p.END_DT";

    /**
     * @see ReportDAO#retrieveIssuedPermits(Timestamp startDt, Timestamp endDt)
     */
    public final Issuance[] retrieveIssuedPermits(Timestamp startDt, Timestamp endDt)
        throws DAOException {

        ConnectionHandler connHandler 
            = new ConnectionHandler("ReportsSQL.retrieveIssuanceDetailsByDate", true);
        
        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));
        
        ArrayList<Issuance> ret = connHandler.retrieveArray(Issuance.class);

        return ret.toArray(new Issuance[0]);
    }

    /**
     * @see ReportDAO#retrievePermitParams()
     */
    public final PermitSOPParams[] retrievePermitParams() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ReportsSQL.retrievePermitParams", true);

        ArrayList<PermitSOPParams> ret = connHandler
                .retrieveArray(PermitSOPParams.class);

        return ret.toArray(new PermitSOPParams[0]);
    }
    
    /**
     * @see ReportDAO#retrieveIssuedPbrsByDoLaaBy(Timestamp startDt,
     *      Timestamp endDt, String doLaaName)
     */
    public final SimpleIdDef[] retrieveIssuedPbrsByDoLaa(Timestamp startDt,
            Timestamp endDt, String doLaaName) throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveIssuedPbrsByDoLaa"));

        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName + "'");

        query.append(" GROUP By do.do_laa_dsc");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }

    /**
     * @see ReportDAO#retrieveIssuedPermitsByDoLaaByType(Timestamp startDt,
     *      Timestamp endDt, String permitType, String issuanceType, String
     *      doLaaName)
     */
    public final SimpleIdDef[] retrieveIssuedPermitsByDoLaaByType(Timestamp startDt,
            Timestamp endDt, String permitType, String issuanceType,
            String doLaaName) throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveIssuedPermitsByDoLaaByType"));

        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName + "'");

        if (issuanceType != null)
            query.append(" and pi.issuance_type_cd='" + issuanceType + "'");

        query.append(" GROUP By do.do_laa_dsc");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, permitType);
        connHandler.setTimestamp(2, formatBeginOfDay(startDt));
        connHandler.setTimestamp(3, formatEndOfDay(endDt));

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }
    

    public final SimpleIdDef[] retrieveExpiredPermitsByDoLaaByType(Timestamp startDt,
            Timestamp endDt, String permitType, String issuanceType,
            String doLaaName) throws DAOException {
        
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveExpiredPermitsByDoLaaByType"));
        /*
        if (PermitTypeDef.TVPTI.equals(permitType)){
            query.append(" pt.effective_date >=? and pt.effective_date <=?");

            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(startDt.getTime());
            td.add(Calendar.MONTH, -18);
            startDt = new Timestamp(td.getTimeInMillis());
            
            td.setTimeInMillis(endDt.getTime());
            td.add(Calendar.MONTH, -18);
            endDt = new Timestamp(td.getTimeInMillis());
        }else{
        */
            query.append(" pt.expiration_date >=? and pt.expiration_date <=?");
        /*
        }
        */

        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName + "'");

        if (issuanceType != null)
            query.append(" and pi.issuance_type_cd='" + issuanceType + "'");

        query.append(" GROUP By do.do_laa_dsc");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, permitType);
        connHandler.setTimestamp(2, formatBeginOfDay(startDt));
        connHandler.setTimestamp(3, formatEndOfDay(endDt));

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }

    /**
     * @see ReportDAO#retrieveActivePermitsByDoLaaByType(String permitType,
     *      String issuanceType, String doLaaName, Integer userId,
     *      String activityName, String activityStatusCd)
     */
    public final SimpleIdDef[] retrieveActivePermitsByDoLaaByType(String permitType,
            String issuanceType, String doLaas, Integer userId,
            String activityName, String activityStatusCd) 
            throws DAOException 
   {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveActivePermitsByDoLaaByType"));

        /*if (doLaas != null) {
            String dolaas = "('" + doLaas + "')";
            query.append(" and do.do_laa_cd IN " + dolaas.replace(",", "','"));
        }*/

        if (issuanceType != null) {
            query.append(" and pt.permit_global_status_cd='" + issuanceType
                    + "'");
        } else {
            // don't want to include status code of 'E' or other code
            // that should not be in this report.
            query.append(" and pt.permit_global_status_cd IN ('N', 'D','PP', 'PPP')");
        }

        if (userId != null)
            query.append(" and pa.user_id=" + userId);

        if (permitType != null)
            query.append(" and pt.permit_type_cd='" + permitType + "'");
        
        if (activityStatusCd != null) {
            if (!activityStatusCd.contains(",")) {
                query.append(" and pa.activity_status_cd='" + activityStatusCd + "'");
            } else {
                query.append(" and pa.activity_status_cd in ('" + 
                        activityStatusCd.replaceAll("\\,", "','") + "')");
            }
        }

        if (activityName != null)
            query.append(" and wt.activity_template_nm='" + activityName + "'");
        else
            query.append(" and "
                    + "(wt.activity_template_nm='Completeness Review' or"
                    + " wt.activity_template_nm='Tech Review/Draft Permit/Waiver' or"
                    + " wt.activity_template_nm='Permit Peer Review' or"
                    + " wt.activity_template_nm='Manager/Supervisor Review') ");

        query.append(" GROUP By do.do_laa_dsc");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        // connHandler.setString(1, permitType);

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }

    /**
     * @see ReportDAO#retrieveIssuedPermitsByIssuanceType(Timestamp startDt,
     *      Timestamp endDt, String permitType, String doLaaName)
     */
    public final SimpleIdDef[] retrieveIssuedPermitsByIssuanceType(Timestamp startDt,
                                                                   Timestamp endDt, 
                                                                   String permitType, 
                                                                   String doLaaName)
            throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveIssuedPermitsByIssuanceType"));

        if (doLaaName != null) {
            query.append(" and do.do_laa_dsc='" + doLaaName + "'");
        }

        query.append(" GROUP By pi.issuance_type_cd");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        
        connHandler.setSQLStringRaw(query.toString());
        
        connHandler.setString(1, permitType);
        connHandler.setTimestamp(2, formatBeginOfDay(startDt));
        connHandler.setTimestamp(3, formatEndOfDay(endDt));
        
        ArrayList<SimpleIdDef> ret = connHandler.retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }
    

    public final SimpleIdDef[] retrieveExpiredPermitsByIssuanceType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String doLaaName) throws DAOException {

        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveExpiredPermitsByIssuanceType"));

        /*
        if (PermitTypeDef.TVPTI.equals(permitType)){
            query.append(" AND pt.effective_date >=? and pt.effective_date <=?");

            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(startDt.getTime());
            td.add(Calendar.MONTH, -18);
            startDt = new Timestamp(td.getTimeInMillis());
            
            td.setTimeInMillis(endDt.getTime());
            td.add(Calendar.MONTH, -18);
            endDt = new Timestamp(td.getTimeInMillis());
        }else{
        */
            query.append(" AND pt.expiration_date >=? and pt.expiration_date <=?");
        /*}*/
        
        if (doLaaName != null) {
            query.append(" and do.do_laa_dsc='" + doLaaName + "'");
        }

        query.append(" GROUP By pi.issuance_type_cd");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, permitType);
        connHandler.setTimestamp(2, formatBeginOfDay(startDt));
        connHandler.setTimestamp(3, formatEndOfDay(endDt));

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }

    /**
     * @see ReportDAO#retrieveActivePermitsByIssuanceType(String permitType,
     *      String doLaaName, Integer userId, String activityName, String
     *      activityStatusCd)
     */
    public final SimpleIdDef[] retrieveActivePermitsByIssuanceType(String permitType,
            String doLaas, Integer userId, String activityName, String activityStatusCd)
            throws DAOException 
    {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveActivePermitsByIssuanceType"));

        /*if (doLaas != null) {
            String dolaas = "('" + doLaas + "')";
            query.append(" AND do.do_laa_cd IN " + dolaas.replace(",", "','"));
        }*/
        
        if (permitType != null)
            query.append(" and pt.permit_type_cd='" + permitType + "'");

        if (userId != null)
            query.append(" and pa.user_id=" + userId);
        
        if (activityStatusCd != null) {
            if (!activityStatusCd.contains(",")) {
                query.append(" and pa.activity_status_cd='" + activityStatusCd + "'");
            } else {
                query.append(" and pa.activity_status_cd in ('" + 
                        activityStatusCd.replaceAll("\\,", "','") + "')");
            }
        }

        if (activityName != null)
            query.append(" and wt.activity_template_nm='" + activityName + "'");
        else
            //
            // It is possible to doudle count but the graphs will match the
        	// table.
            query
             .append(" and "
            		 + "(wt.activity_template_nm='Completeness Review' or"
                     + " wt.activity_template_nm='Tech Review/Draft Permit/Waiver' or"
                     + " wt.activity_template_nm='Permit Peer Review' or"
                     + " wt.activity_template_nm='Manager/Supervisor Review') ");

        query.append(" GROUP By pt.permit_global_status_cd ");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        String s = query.toString();
        connHandler.setSQLStringRaw(s);

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }

    /**
     * @see ReportDAO#retrieveIssuedPermitsByGeneralByType(Timestamp startDt,
     *      Timestamp endDt, String permitType, String issuanceType, String
     *      doLaaName)
     */
    public final SimpleIdDef[] retrieveIssuedPermitsByGeneralByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveIssuedPermitsByGeneralByType"));

        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName
                    + "' GROUP By ppp.general_permit_flag");
        else
            query.append(" GROUP By ppp.general_permit_flag");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, issuanceType);
        connHandler.setString(2, permitType);
        connHandler.setTimestamp(3, formatBeginOfDay(startDt));
        connHandler.setTimestamp(4, formatEndOfDay(endDt));

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }
    

    public final SimpleIdDef[] retrieveExpiredPermitsByGeneralByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveExpiredPermitsByGeneralByType"));

        /*
        if (PermitTypeDef.TVPTI.equals(permitType)){
            query.append(" pt.effective_date >=? and pt.effective_date <=?");

            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(startDt.getTime());
            td.add(Calendar.MONTH, -18);
            startDt = new Timestamp(td.getTimeInMillis());
            
            td.setTimeInMillis(endDt.getTime());
            td.add(Calendar.MONTH, -18);
            endDt = new Timestamp(td.getTimeInMillis());
        }else{
        */
            query.append(" pt.expiration_date >=? and pt.expiration_date <=?");
        /*}*/
        
        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName
                    + "' GROUP By ppp.general_permit_flag");
        else
            query.append(" GROUP By ppp.general_permit_flag");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, permitType);
        connHandler.setTimestamp(2, formatBeginOfDay(startDt));
        connHandler.setTimestamp(3, formatEndOfDay(endDt));

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }

    /**
     * @see ReportDAO#retrieveActivePermitsByGeneralByType(String permitType,
     *      String issuanceType, String doLaas, Integer userId, 
     *      String activityName, String activityStatusCd)
     */
    public final SimpleIdDef[] retrieveActivePermitsByGeneralByType(
            String permitType, String issuanceType, String doLaas,
            Integer userId, String activityName, String activityStatusCd) 
            throws DAOException 
    {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveActivePermitsByGeneralByType"));

        /*if (doLaas != null) {
            String dolaas = "('" + doLaas + "')";
            query.append(" AND do.do_laa_cd IN " + dolaas.replace(",", "','"));
        }*/
        
        if (activityStatusCd != null) {
            if (!activityStatusCd.contains(",")) {
                query.append(" and pa.activity_status_cd='" + activityStatusCd + "'");
            } else {
                query.append(" and pa.activity_status_cd in ('" +
                        activityStatusCd.replaceAll("\\,", "','") + "')");
            }
            
        }

        if (userId != null)
            query.append(" and pa.user_id=" + userId);
        
        if (issuanceType != null)
            query.append(" and pt.permit_global_status_cd='" + issuanceType
                    + "'");

        if (activityName != null)
            query.append(" and wt.activity_template_nm='" + activityName + "'");
        else
            //
            // It is possible to doudle count but the graphs will match the
        	// table.
            query
             .append(" and "
            		 + "(wt.activity_template_nm='Completeness Review' or"
                     + " wt.activity_template_nm='Tech Review/Draft Permit/Waiver' or"
                     + " wt.activity_template_nm='Permit Peer Review' or"
                     + " wt.activity_template_nm='Manager/Supervisor Review') ");

        query.append(" GROUP By ppp.general_permit_flag");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        String s = query.toString();

        connHandler.setSQLStringRaw(s);

        connHandler.setString(1, permitType);

        ArrayList<SimpleIdDef> ret = connHandler
                .retrieveArray(SimpleIdDef.class);

        return ret.toArray(new SimpleIdDef[0]);
    }

    /**
     * @see ReportDAO#retrieveIssuedPermitsByReasonByType(Timestamp startDt,
     *      Timestamp endDt, String permitType, String issuanceType, String
     *      doLaaName)
     */
    public final SimpleIssuanceReason[] retrieveIssuedPermitsByReasonByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveIssuedPermitsByReasonByType"));

        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName
                    + "' ORDER by pt.permit_id, prd.line_order");
        else
            query.append(" ORDER by pt.permit_id, prd.line_order");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, issuanceType);
        connHandler.setString(2, permitType);
        connHandler.setTimestamp(3, formatBeginOfDay(startDt));
        connHandler.setTimestamp(4, formatEndOfDay(endDt));

        ArrayList<SimpleIssuanceReason> ret = connHandler
                .retrieveArray(SimpleIssuanceReason.class);

        return ret.toArray(new SimpleIssuanceReason[0]);
    }
    
    public final SimpleIssuanceReason[] retrieveExpiredPermitsByReasonByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveExpiredPermitsByReasonByType"));

        /*
        if (PermitTypeDef.TVPTI.equals(permitType)){
            query.append(" AND pt.effective_date >=? and pt.effective_date <=?");

            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(startDt.getTime());
            td.add(Calendar.MONTH, -18);
            startDt = new Timestamp(td.getTimeInMillis());
            
            td.setTimeInMillis(endDt.getTime());
            td.add(Calendar.MONTH, -18);
            endDt = new Timestamp(td.getTimeInMillis());
        }else{
        */
            query.append(" AND pt.expiration_date >=? and pt.expiration_date <=?");
       /*}*/
        
        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName
                    + "' ORDER by pt.permit_id");
        else
            query.append(" ORDER by pt.permit_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, permitType);
        connHandler.setTimestamp(2, formatBeginOfDay(startDt));
        connHandler.setTimestamp(3, formatEndOfDay(endDt));
        
        ArrayList<SimpleIssuanceReason> ret = connHandler
                .retrieveArray(SimpleIssuanceReason.class);

        return ret.toArray(new SimpleIssuanceReason[0]);
    }

    /**
     * @see ReportDAO#retrieveActivePermitsByReasonByType(String permitType,
     *      String issuanceType, String doLaas, Integer userId, 
     *      String activityName, String activityStatusCd)
     */
    public final SimpleIssuanceReason[] retrieveActivePermitsByReasonByType(
            String permitType, String issuanceState, String doLaas,
            Integer userId, String activityName, String activityStatusCd) 
            throws DAOException 
    {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveActivePermitsByReasonByType"));


        if (activityStatusCd != null && !activityStatusCd.contains(",")) {
            query.append(" and pa.activity_status_cd='" + activityStatusCd + "'");
        } else if (activityStatusCd != null){
            query.append(" and pa.activity_status_cd in ('" +
                    activityStatusCd.replaceAll("\\,", "','") + "')");
        }
        
        if (userId != null)
            query.append(" and pa.user_id=" + userId);

        if (activityName != null)
            query.append(" and wt.activity_template_nm='" + activityName + "'");
        else
            //
            // It is possible to doudle count but the graphs will match the
        	// table.
            query
             .append(" and "
            		 + "(wt.activity_template_nm='Completeness Review' or"
                     + " wt.activity_template_nm='Tech Review/Draft Permit/Waiver' or"
                     + " wt.activity_template_nm='Permit Peer Review' or"
                     + " wt.activity_template_nm='Manager/Supervisor Review') ");

        if (issuanceState != null)
            query.append(" and pt.permit_global_status_cd='" + issuanceState
                    + "'");

        /*if (doLaas != null) {
            String dolaas = "('" + doLaas + "')";
            query.append(" AND do.do_laa_cd IN " + dolaas.replace(",", "','"));
        }*/
        query.append(" ORDER by pt.permit_id, prd.line_order");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        String s = query.toString();
        connHandler.setSQLStringRaw(s);

        connHandler.setString(1, permitType);

        ArrayList<SimpleIssuanceReason> ret = connHandler
                .retrieveArray(SimpleIssuanceReason.class);

        return ret.toArray(new SimpleIssuanceReason[0]);
    }

    /**
     * @see ReportDAO#retrieveIssuedPermitsByFinalByType(Timestamp startDt,
     *      Timestamp endDt, String permitType, String issuanceType, String
     *      doLaaName)
     */
    public final SimplePermitId[] retrieveIssuedPermitsByFinalByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveIssuedPermitsByFinalByType"));

        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName + "'");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, issuanceType);
        connHandler.setString(2, permitType);
        connHandler.setTimestamp(3, formatBeginOfDay(startDt));
        connHandler.setTimestamp(4, formatEndOfDay(endDt));

        ArrayList<SimplePermitId> ret = connHandler
                .retrieveArray(SimplePermitId.class);

        return ret.toArray(new SimplePermitId[0]);
    }
    
    public final SimplePermitId[] retrieveExpiredPermitsByFinalByType(
            Timestamp startDt, Timestamp endDt, String permitType,
            String issuanceType, String doLaaName) throws DAOException {
        
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveExpiredPermitsByFinalByType"));
        /*
        if (PermitTypeDef.TVPTI.equals(permitType)){
            query.append(" pt.effective_date >=? and pt.effective_date <=?");

            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(startDt.getTime());
            td.add(Calendar.MONTH, -18);
            startDt = new Timestamp(td.getTimeInMillis());
            
            td.setTimeInMillis(endDt.getTime());
            td.add(Calendar.MONTH, -18);
            endDt = new Timestamp(td.getTimeInMillis());
        }else{
        */
            query.append(" pt.expiration_date >=? and pt.expiration_date <=?");
        /*}*/
        
        if (doLaaName != null)
            query.append(" and do.do_laa_dsc='" + doLaaName + "'");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setString(1, permitType);
        connHandler.setTimestamp(2, formatBeginOfDay(startDt));
        connHandler.setTimestamp(3, formatEndOfDay(endDt));

        ArrayList<SimplePermitId> ret = connHandler
                .retrieveArray(SimplePermitId.class);

        return ret.toArray(new SimplePermitId[0]);
    }

    /**
     * @see ReportDAO#retrieveIssuedPermitsByDraftByType(Integer permitId)
     */
    public final SimplePermitId[] retrieveIssuedPermitsByDraftByType(String permitType)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ReportsSQL.retrieveIssuedPermitsByDraftByType", true);

        connHandler.setString(1, permitType);

        ArrayList<SimplePermitId> ret = connHandler
                .retrieveArray(SimplePermitId.class);

        return ret.toArray(new SimplePermitId[0]);
    }

    public final SimplePermitId[] retrieveExpiredPermitsByDraftByType(String permitType,
            Timestamp startDt, Timestamp endDt)
            throws DAOException {
        
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveExpiredPermitsByDraftByType"));
        /*
        if (PermitTypeDef.TVPTI.equals(permitType)){
            query.append(" pt.effective_date >=? and pt.effective_date <=?");

            Calendar td = new GregorianCalendar();
            td.setTimeInMillis(startDt.getTime());
            td.add(Calendar.MONTH, -18);
            startDt = new Timestamp(td.getTimeInMillis());
            
            td.setTimeInMillis(endDt.getTime());
            td.add(Calendar.MONTH, -18);
            endDt = new Timestamp(td.getTimeInMillis());
        }else{
        */
            query.append(" pt.expiration_date >=? and pt.expiration_date <=?");
        /*}*/
        
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());
        
        connHandler.setString(1, permitType);
        connHandler.setTimestamp(2, formatBeginOfDay(startDt));
        connHandler.setTimestamp(3, formatEndOfDay(endDt));

        ArrayList<SimplePermitId> ret = connHandler
                .retrieveArray(SimplePermitId.class);

        return ret.toArray(new SimplePermitId[0]);
    }

    /**
     * @see ReportDAO#retrieveApplicationCountByType(Timestamp startDt, Timestamp endDt)
     */
    public final ApplicationCount[] retrieveApplicationCountByType(Timestamp startDt, Timestamp endDt) 
        throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;

        HashMap<String, ApplicationCount> counts = new HashMap<String, ApplicationCount>();

        DefData doLaaDef = DOLAA.getData();
        if (doLaaDef == null) {
            throw new DAOException("Unable to fetch DO/LAA data.");
        }
        DefSelectItems dsi = doLaaDef.getItems();
        List<SelectItem> currentCodes = dsi.getAllItems();
        for (SelectItem si : currentCodes) {
            ApplicationCount appCount = new ApplicationCount(si.getLabel());
            counts.put((String) si.getValue(), appCount);
        }

        try {
            conn = getReadOnlyConnection();

            String sql = loadSQL("ReportsSQL.retrieveApplicationCountsByType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, formatBeginOfDay(startDt));
            ps.setTimestamp(2, formatEndOfDay(endDt));

            ResultSet res = ps.executeQuery();

            while (res.next()) {
                String doLaaCd = res.getString("do_laa_cd");
                ApplicationCount appCount = counts.get(doLaaCd);
                String typeCd = res.getString("application_type_cd");
                /*Remove PBR references in permit applications
				if (typeCd != null && typeCd.equals(ApplicationTypeDef.PBR_NOTIFICATION)) {
                    appCount.setPbr(res.getInt("app_count"));
                }*/
                if (typeCd != null && typeCd.equals(ApplicationTypeDef.PTIO_APPLICATION)) {
                    appCount.setPtio(res.getInt("app_count"));
                }
                else if (typeCd != null && typeCd.equals(ApplicationTypeDef.TITLE_V_APPLICATION)) {
                    appCount.setTvPto(res.getInt("app_count"));
                }
                else if (typeCd != null && typeCd.equals(ApplicationTypeDef.RPC_REQUEST)) {
                    appCount.setRpc(res.getInt("app_count"));
                }
                else if (typeCd != null && typeCd.equals(ApplicationTypeDef.RPE_REQUEST)) {
                    appCount.setRpe(res.getInt("app_count"));
                }
                else if (typeCd != null && typeCd.equals(ApplicationTypeDef.RPR_REQUEST)) {
                    appCount.setRpr(res.getInt("app_count"));
                }
                else {
                    throw new DAOException("Null or unknown application type code: " + typeCd + ".");
                }
            }

            res.close();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            handleException(ex, conn);
        }
        finally {
            closeStatement(ps);
            handleClosing(conn);
        }

        return counts.values().toArray(new ApplicationCount[0]);

    }

    /**
     * @see ReportDAO#retrieveIssuedFinalPermitsCountByType(Timestamp startDt, Timestamp endDt)
     */
    public final PermitCount[] retrieveIssuedFinalPermitsCountByType(Timestamp startDt, Timestamp endDt) 
        throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;

        HashMap<String, PermitCount> counts = new HashMap<String, PermitCount>();

        DefData doLaaDef = DOLAA.getData();
        if (doLaaDef == null) {
            throw new DAOException("Unable to fetch DO/LAA data.");
        }
        DefSelectItems dsi = doLaaDef.getItems();
        List<SelectItem> currentCodes = dsi.getAllItems();
        for (SelectItem si : currentCodes) {
            PermitCount permitCount = new PermitCount(si.getLabel());
            counts.put((String) si.getValue(), permitCount);
        }

        try {
            conn = getReadOnlyConnection();

            String sql = loadSQL("ReportsSQL.retrieveIssuedFinalPermitsCountByType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, formatBeginOfDay(startDt));
            ps.setTimestamp(2, formatEndOfDay(endDt));

            ResultSet res = ps.executeQuery();

            while (res.next()) {
                String doLaaCd = res.getString("do_laa_cd");
                PermitCount permitCount = counts.get(doLaaCd);
                String typeCd = res.getString("permit_type_cd");
               /* Remove PBR references in permit applications
				if (typeCd != null && typeCd.equals(PermitTypeDef.PBR)) {
                    permitCount.setFinalPbr(res.getInt("final_count"));
                }*/
                if (typeCd != null && typeCd.equals(PermitTypeDef.NSR)) {
                    permitCount.setFinalPtio(res.getInt("final_count"));
                }
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.TVPTI)) {
                //    permitCount.setFinalTvPti(res.getInt("final_count"));
                //}
                else if (typeCd != null && typeCd.equals(PermitTypeDef.TV_PTO)) {
                    permitCount.setFinalTvPto(res.getInt("final_count"));
                }
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.RPE)) {
                //    permitCount.setFinalRpe(res.getInt("final_count"));
                //}
                else if (typeCd != null && typeCd.equals(PermitTypeDef.RPR)) {
                    permitCount.setFinalRpr(res.getInt("final_count"));
                }
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.REG)) {
                //    permitCount.setFinalReg(res.getInt("final_count"));
               // }
               // else if (typeCd != null && typeCd.equals(PermitTypeDef.SPTO)) {
                //    permitCount.setFinalStatePto(res.getInt("final_count"));
                //}
                else {
                    throw new DAOException("Null or unknown permit type code: " + typeCd + ".");
                }

            }

            res.close();

            sql = loadSQL("ReportsSQL.retrieveIssuedDeniedPermitsCountByType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, formatBeginOfDay(startDt));
            ps.setTimestamp(2, formatEndOfDay(endDt));

            res = ps.executeQuery();

            while (res.next()) {
                String doLaaCd = res.getString("do_laa_cd");
                PermitCount permitCount = counts.get(doLaaCd);
                String typeCd = res.getString("permit_type_cd");
                /*Remove PBR references in permit applications
				if (typeCd != null && typeCd.equals(PermitTypeDef.PBR)) {
                    permitCount.setDeniedPbr(res.getInt("final_count"));
                }
                else */if (typeCd != null && typeCd.equals(PermitTypeDef.NSR)) {
                    permitCount.setDeniedPtio(res.getInt("final_count"));
                }
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.TVPTI)) {
                //    permitCount.setDeniedTvPti(res.getInt("final_count"));
                //}
                else if (typeCd != null && typeCd.equals(PermitTypeDef.TV_PTO)) {
                    permitCount.setDeniedTvPto(res.getInt("final_count"));
                }
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.RPE)) {
                 //   permitCount.setDeniedRpe(res.getInt("final_count"));
                //}
                else if (typeCd != null && typeCd.equals(PermitTypeDef.RPR)) {
                    permitCount.setDeniedRpr(res.getInt("final_count"));
                }
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.REG)) {
                //    permitCount.setDeniedReg(res.getInt("final_count"));
                //}
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.SPTO)) {
                //    permitCount.setDeniedStatePto(res.getInt("final_count"));
                //}
                else {
                    throw new DAOException("Null or unknown permit type code: " + typeCd + ".");
                }

            }

            res.close();

            sql = loadSQL("ReportsSQL.retrievePermitOnTimeMetrics");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, formatBeginOfDay(startDt));
            ps.setTimestamp(2, formatEndOfDay(endDt));

            res = ps.executeQuery();

            while (res.next()) {
                String doLaaCd = res.getString("do_laa_cd");
                PermitCount permitCount = counts.get(doLaaCd);
                String typeCd = res.getString("permit_type_cd");

                /*Remove PBR references in permit applications
				if (typeCd != null && typeCd.equals(PermitTypeDef.PBR)) {
                    permitCount.setPbrOnTimeCount(res.getInt("metric_count"));
                    permitCount.setPbrOnTimeAverage(res.getInt("avg_days"));
                }
                else*/if (typeCd != null && typeCd.equals(PermitTypeDef.NSR)) {
                    permitCount.setPtioOnTimeCount(res.getInt("metric_count"));
                    permitCount.setPtioOnTimeAverage(res.getInt("avg_days"));
                }
               //else if (typeCd != null && typeCd.equals(PermitTypeDef.TVPTI)) {
                //    permitCount.setTvPtiOnTimeCount(res.getInt("metric_count"));
                //    permitCount.setTvPtiOnTimeAverage(res.getInt("avg_days"));
                //}
                else if (typeCd != null && typeCd.equals(PermitTypeDef.TV_PTO)) {
                    permitCount.setTvPtoOnTimeCount(res.getInt("metric_count"));
                    permitCount.setTvPtoOnTimeAverage(res.getInt("avg_days"));
                }
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.RPE)) {
                //    permitCount.setRpeOnTimeCount(res.getInt("metric_count"));
                //    permitCount.setRpeOnTimeAverage(res.getInt("avg_days"));
                //}
                else if (typeCd != null && typeCd.equals(PermitTypeDef.RPR)) {
                    permitCount.setRprOnTimeCount(res.getInt("metric_count"));
                    permitCount.setRprOnTimeAverage(res.getInt("avg_days"));
                }
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.REG)) {
                //    permitCount.setRegOnTimeCount(res.getInt("metric_count"));
                //    permitCount.setRegOnTimeAverage(res.getInt("avg_days"));
                //}
                //else if (typeCd != null && typeCd.equals(PermitTypeDef.SPTO)) {
                //    permitCount.setStateOnTimeCount(res.getInt("metric_count"));
                //    permitCount.setStateOnTimeAverage(res.getInt("avg_days"));
                //}
                else {
                    throw new DAOException("Null or unknown permit type code: " + typeCd + ".");
                }

            }

            res.close();

            sql = loadSQL("ReportsSQL.retrievePermitOverTimeMetrics");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, formatBeginOfDay(startDt));
            ps.setTimestamp(2, formatEndOfDay(endDt));

            res = ps.executeQuery();

            while (res.next()) {
                String doLaaCd = res.getString("do_laa_cd");
                PermitCount permitCount = counts.get(doLaaCd);
                String typeCd = res.getString("permit_type_cd");

               /* Remove PBR references in permit applications
				if (typeCd != null && typeCd.equals(PermitTypeDef.PBR)) {
                    permitCount.setPbrOverTimeCount(res.getInt("metric_count"));
                    permitCount.setPbrOverTimeAverage(res.getInt("avg_days"));
                }
                else */if (typeCd != null && typeCd.equals(PermitTypeDef.NSR)) {
                    permitCount.setPtioOverTimeCount(res.getInt("metric_count"));
                    permitCount.setPtioOverTimeAverage(res.getInt("avg_days"));
                }
               // else if (typeCd != null && typeCd.equals(PermitTypeDef.TVPTI)) {
                //    permitCount.setTvPtiOverTimeCount(res.getInt("metric_count"));
                //    permitCount.setTvPtiOverTimeAverage(res.getInt("avg_days"));
                //}
                else if (typeCd != null && typeCd.equals(PermitTypeDef.TV_PTO)) {
                    permitCount.setTvPtoOverTimeCount(res.getInt("metric_count"));
                    permitCount.setTvPtoOverTimeAverage(res.getInt("avg_days"));
                }
               // else if (typeCd != null && typeCd.equals(PermitTypeDef.RPE)) {
               //     permitCount.setRpeOverTimeCount(res.getInt("metric_count"));
               //     permitCount.setRpeOverTimeAverage(res.getInt("avg_days"));
               // }
                else if (typeCd != null && typeCd.equals(PermitTypeDef.RPR)) {
                    permitCount.setRprOverTimeCount(res.getInt("metric_count"));
                    permitCount.setRprOverTimeAverage(res.getInt("avg_days"));
                }
               // else if (typeCd != null && typeCd.equals(PermitTypeDef.REG)) {
               //     permitCount.setRegOverTimeCount(res.getInt("metric_count"));
               //     permitCount.setRegOverTimeAverage(res.getInt("avg_days"));
               // }
               // else if (typeCd != null && typeCd.equals(PermitTypeDef.SPTO)) {
               //     permitCount.setStateOverTimeCount(res.getInt("metric_count"));
               //     permitCount.setStateOverTimeAverage(res.getInt("avg_days"));
               // }
                else {
                    throw new DAOException("Null or unknown permit type code: " + typeCd + ".");
                }

            }

            res.close();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            handleException(ex, conn);
        }
        finally {
            closeStatement(ps);
            handleClosing(conn);
        }

        return counts.values().toArray(new PermitCount[0]);

    }

    /**
     * @see ReportDAO#retrievePbrCountsByType(Timestamp startDt, Timestamp endDt)
     */
    public final PBRCount[] retrievePbrCountsByType(Timestamp startDt, Timestamp endDt) 
        throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;

        HashMap<String, PBRCount> counts = new HashMap<String, PBRCount>();

        DefData doLaaDef = DOLAA.getData();
        if (doLaaDef == null) {
            throw new DAOException("Unable to fetch DO/LAA data.");
        }
        DefSelectItems dsi = doLaaDef.getItems();
        List<SelectItem> currentCodes = dsi.getAllItems();
        for (SelectItem si : currentCodes) {
            PBRCount pbrCount = new PBRCount(si.getLabel());
            counts.put((String) si.getValue(), pbrCount);
        }

        try {
            conn = getReadOnlyConnection();

            String sql = loadSQL("ReportsSQL.retrievePbrCountsByType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, formatBeginOfDay(startDt));
            ps.setTimestamp(2, formatEndOfDay(endDt));

            ResultSet res = ps.executeQuery();

            while (res.next()) {
                String doLaaCd = res.getString("do_laa_cd");
                PBRCount pbrCount = counts.get(doLaaCd);
                pbrCount.setCount(res.getString("pbr_type_cd"),
                                  res.getString("disposition_flag"),
                                  res.getInt("pbr_by_type_and_disposition"),
                                  res.getFloat("avg_days_in_process"));
            }

            res.close();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            handleException(ex, conn);
        }
        finally {
            closeStatement(ps);
            handleClosing(conn);
        }

        return counts.values().toArray(new PBRCount[0]);

    }

    /**
     * @see ReportDAO#retrieveFacilityCountsByPermitType()
     */
    public final FacilityPermitCount[] retrieveFacilityCountsByPermitType(Timestamp endDt)
        throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;

        HashMap<String, FacilityPermitCount> counts = new HashMap<String, FacilityPermitCount>();

        DefData permitTypeDef = PermitTypeDef.getData();
        if (permitTypeDef == null) {
            throw new DAOException("Unable to fetch permit type data.");
        }
        DefSelectItems dsi = permitTypeDef.getItems();
        List<SelectItem> currentCodes = dsi.getAllItems();
        for (SelectItem si : currentCodes) {
            if ( 
            	//	si.getValue().equals(PermitTypeDef.REG) ||
                 si.getValue().equals(PermitTypeDef.RPR) 
                // || si.getValue().equals(PermitTypeDef.RPE)
                 ) {
                continue;
            }
            FacilityPermitCount facilityPermitCount = new FacilityPermitCount(si.getLabel());
            counts.put((String) si.getValue(), facilityPermitCount);
        }

        try {
            conn = getReadOnlyConnection();

            String sql = loadSQL("ReportsSQL.retrieveFacilityCountsByPermitType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, endDt);

            ResultSet res = ps.executeQuery();

            while (res.next()) {
                String permitTypeCd = res.getString("permit_type_cd");
                FacilityPermitCount facilityPermitCount = counts.get(permitTypeCd);
                if (facilityPermitCount == null) {
                    continue;
                }
                facilityPermitCount.setIssued(res.getInt("facility_count"));
            }
            res.close();

            sql = loadSQL("ReportsSQL.retrieveFacilityCountsByExpiredPermitType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, endDt);
            ps.setTimestamp(2, endDt);
            ps.setTimestamp(3, endDt);

            res = ps.executeQuery();

            while (res.next()) {
                String permitTypeCd = res.getString("permit_type_cd");
                FacilityPermitCount facilityPermitCount = counts.get(permitTypeCd);
                if (facilityPermitCount == null) {
                    continue;
                }
                facilityPermitCount.setExpired(res.getInt("facility_count"));
            }
            res.close();

            sql = loadSQL("ReportsSQL.retrieveFacilityCountsByActivePermitType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, endDt);
            ps.setTimestamp(2, endDt);

            res = ps.executeQuery();

            while (res.next()) {
                String permitTypeCd = res.getString("permit_type_cd");
                FacilityPermitCount facilityPermitCount = counts.get(permitTypeCd);
                if (facilityPermitCount == null) {
                    continue;
                }
                facilityPermitCount.setActive(res.getInt("facility_count"));
            }
            res.close();

            sql = loadSQL("ReportsSQL.retrieveFacilityCountsBySupersededPermitType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, endDt);

            res = ps.executeQuery();

            while (res.next()) {
                String permitTypeCd = res.getString("permit_type_cd");
                FacilityPermitCount facilityPermitCount = counts.get(permitTypeCd);
                if (facilityPermitCount == null) {
                    continue;
                }
                facilityPermitCount.setSuperseded(res.getInt("facility_count"));
            }
            res.close();

            sql = loadSQL("ReportsSQL.retrieveFacilityCountsByExtendedPermitType");
            ps = conn.prepareStatement(sql.toString());

            ps.setTimestamp(1, endDt);
            ps.setTimestamp(2, endDt);
            ps.setTimestamp(3, endDt);

            res = ps.executeQuery();

            while (res.next()) {
                String permitTypeCd = res.getString("permit_type_cd");
                FacilityPermitCount facilityPermitCount = counts.get(permitTypeCd);
                if (facilityPermitCount == null) {
                    continue;
                }
                facilityPermitCount.setExtended(res.getInt("facility_count"));
            }
            res.close();

        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            handleException(ex, conn);
        }
        finally {
            closeStatement(ps);
            handleClosing(conn);
        }

        return counts.values().toArray(new FacilityPermitCount[0]);
    }

    /**
     * @see ReportDAO#retrieveInProcessPermitTime()
     */
    public final PermitTime[] retrieveInProcessPermitTime() 
        throws DAOException {

        ArrayList<PermitTime> permitTimes = new ArrayList<PermitTime>();

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getReadOnlyConnection();
            
            String sql = loadSQL("ReportsSQL.retrievePermitProcessTime");
            ps = conn.prepareStatement(sql.toString());
            
            ResultSet res = ps.executeQuery();
            
            while (res.next()) {
                PermitTime pt = new PermitTime();

                pt.setPermitNbr(res.getString("permit_nbr"));
                pt.setPermitType(res.getString("permit_type_cd"));
                pt.setAgencyTime(res.getInt("agency_time"));
                pt.setApplicantTime(res.getInt("applicant_time"));
                pt.setOtherTime(res.getInt("other_time"));
                pt.setElapsedTime(res.getInt("elapsed_time"));

                permitTimes.add(pt);
            }
            res.close();
            
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            handleException(ex, conn);
        }
        finally {
            closeStatement(ps);
            handleClosing(conn);
        }
        
        return permitTimes.toArray(new PermitTime[0]);
            
    }

    /**
     *
     */
    /*
    public final IssuancePtiDetails[] retrieveIssuancePtiDetails(Timestamp startDt, Timestamp endDt, 
                                                                 String doLaaName, String issuanceType)
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveIssuancePtiDetailsByDate"));

        if (doLaaName != null) {
            query.append(" AND do.do_laa_dsc='" + doLaaName + "'");
        }
        if (issuanceType != null) {
            query.append(" AND pi.issuance_type_cd='" + issuanceType + "'");
        }
        query.append(" ORDER BY pt.permit_nbr, prd.line_order");
        String s = query.toString();

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(s);

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));

        ArrayList<IssuancePtiDetails> ret 
            = connHandler.retrieveArray(IssuancePtiDetails.class);

        return ret.toArray(new IssuancePtiDetails[0]);
    }
    */
/*
    public final IssuancePtiDetails[] retrieveExpiredPtiDetails(Timestamp startDt, Timestamp endDt, 
                                                                 String doLaaName, String issuanceType)
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveExpiredPtiDetailsByDate"));

        if (doLaaName != null) {
            query.append(" AND do.do_laa_dsc='" + doLaaName + "'");
        }
        if (issuanceType != null) {
            query.append(" AND pi.issuance_type_cd='" + issuanceType + "'");
        }
        query.append(" ORDER BY pt.permit_nbr, prd.line_order");
        String s = query.toString();

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(s);
        
        Calendar td = new GregorianCalendar();
        td.setTimeInMillis(startDt.getTime());
        td.add(Calendar.MONTH, -18);
        startDt = new Timestamp(td.getTimeInMillis());
        
        td.setTimeInMillis(endDt.getTime());
        td.add(Calendar.MONTH, -18);
        endDt = new Timestamp(td.getTimeInMillis());

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));

        ArrayList<IssuancePtiDetails> ret 
            = connHandler.retrieveArray(IssuancePtiDetails.class);

        return ret.toArray(new IssuancePtiDetails[0]);
    }
    */

    /**
     *
     */
    public final IssuancePtioDetails[] retrieveIssuancePtioDetails(Timestamp startDt, Timestamp endDt,
                                                                   String doLaaName, String issuanceType)
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveIssuancePtioDetailsByDate"));

        if (doLaaName != null) {
            query.append(" AND do.do_laa_dsc = '" + doLaaName + "'");
        }
        if (issuanceType != null) {
            query.append(" AND pi.issuance_type_cd = '" + issuanceType + "'");
        }
        query.append(" ORDER BY pt.permit_nbr, prd.line_order");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));
        
        ArrayList<IssuancePtioDetails> ret 
            = connHandler.retrieveArray(IssuancePtioDetails.class);

        return ret.toArray(new IssuancePtioDetails[0]);
    }

    public final IssuancePtioDetails[] retrieveExpiredPtioDetails(Timestamp startDt, Timestamp endDt,
                                                                   String doLaaName, boolean hideShutdownFacility,boolean hideExemptPBR,
                                                                   boolean hideEUPermitStatusTRS,boolean hideEUExemptionDmPe,boolean hideEUShutdownInvalid,
                                                                   boolean hidePtoPtioEuActivePBR, String issuanceType)
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveExpiredPtioDetailsByDate"));

        if (doLaaName != null) {
            query.append(" AND do.do_laa_dsc = '" + doLaaName + "'");
        }
        if (issuanceType != null) {
            query.append(" AND pi.issuance_type_cd = '" + issuanceType + "'");
        }
		if (hideShutdownFacility) {
			query.append("\nAND fp.operating_status_cd != 'sd'");
		}
		if (hideExemptPBR) {
			query.append("\nAND fp.permit_classification_cd NOT IN ('extm', 'pbr')");
		}
		if (hideEUPermitStatusTRS) {
			query.append("\nAND peu.permit_status_cd NOT IN ('T', 'R', 'S')");
		}
		if (hideEUExemptionDmPe) {
			query.append("\nAND feu.exempt_status_cd NOT IN ('exm', 'dem')");
		}
		if (hideEUShutdownInvalid) {
			query.append("\nAND feu.operating_status_cd NOT IN ('sd', 'iv')");
		}
		if (hidePtoPtioEuActivePBR) {
			query.append("\nAND (pt.permit_type_cd NOT IN ('SPTO', 'PTIO') OR\n" +
	                  "(SELECT COUNT(*) FROM stars2.pt_eu peu2\n" +
	                  "JOIN stars2.pt_eu_group eug2 ON (peu2.permit_eu_group_id = eug2.permit_eu_group_id)\n" +
	                  "JOIN stars2.pt_permit pt2 ON (eug2.permit_id = pt2.permit_id AND pt2.permit_type_cd = 'PBR')\n" +
	                  "WHERE peu2.permit_status_cd = 'A' AND peu2.corr_epa_emu_id = peu.corr_epa_emu_id\n" +
	                ") = 0)\n");
		}
        query.append(" ORDER BY pt.permit_nbr");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));
        
        ArrayList<IssuancePtioDetails> ret 
            = connHandler.retrieveArray(IssuancePtioDetails.class);

        return ret.toArray(new IssuancePtioDetails[0]);
    }
/*
    public final IssuanceSptoDetails[] retrieveExpiredSptoDetails(Timestamp startDt, Timestamp endDt,
                                                                   String doLaaName,  boolean hideShutdownFacility,boolean hideExemptPBR,
                                                                   boolean hideEUPermitStatusTRS,boolean hideEUExemptionDmPe,boolean hideEUShutdownInvalid,
                                                                   boolean hidePtoPtioEuActivePBR, String issuanceType)
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveExpiredSptoDetailsByDate"));

        if (doLaaName != null) {
            query.append(" AND do.do_laa_dsc = '" + doLaaName + "'");
        }
        if (issuanceType != null) {
            query.append(" AND pi.issuance_type_cd = '" + issuanceType + "'");
        }
		if (hideShutdownFacility) {
			query.append("\nAND fp.operating_status_cd != 'sd'");
		}
		if (hideExemptPBR) {
			query.append("\nAND fp.permit_classification_cd NOT IN ('extm', 'pbr')");
		}
		if (hideEUPermitStatusTRS) {
			query.append("\nAND peu.permit_status_cd NOT IN ('T', 'R', 'S')");
		}
		if (hideEUExemptionDmPe) {
			query.append("\nAND feu.exempt_status_cd NOT IN ('exm', 'dem')");
		}
		if (hideEUShutdownInvalid) {
			query.append("\nAND feu.operating_status_cd NOT IN ('sd', 'iv')");
		}
		if (hidePtoPtioEuActivePBR) {
			query.append("\nAND (pt.permit_type_cd NOT IN ('SPTO', 'PTIO') OR\n" +
	                  "(SELECT COUNT(*) FROM stars2.pt_eu peu2\n" +
	                  "JOIN stars2.pt_eu_group eug2 ON (peu2.permit_eu_group_id = eug2.permit_eu_group_id)\n" +
	                  "JOIN stars2.pt_permit pt2 ON (eug2.permit_id = pt2.permit_id AND pt2.permit_type_cd = 'PBR')\n" +
	                  "WHERE peu2.permit_status_cd = 'A' AND peu2.corr_epa_emu_id = peu.corr_epa_emu_id\n" +
	                ") = 0)\n");
		}
        query.append(" ORDER BY pt.permit_nbr");

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));
        
        ArrayList<IssuanceSptoDetails> ret 
            = connHandler.retrieveArray(IssuanceSptoDetails.class);

        return ret.toArray(new IssuanceSptoDetails[0]);
    }
*/
    /**
     *
     */
    public final IssuanceTvDetails[] retrieveIssuanceTvDetails(Timestamp startDt, Timestamp endDt, 
                                                               String doLaaName, String issuanceType)
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveIssuanceTvDetailsByDate"));

        if (doLaaName != null) {
            query.append(" AND do.do_laa_dsc='" + doLaaName + "'");
        }
        if (issuanceType != null) {
            query.append(" AND pi.issuance_type_cd = '" + issuanceType + "'");
        }
        query.append(" ORDER BY pt.permit_nbr, prd.line_order");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));

        ArrayList<IssuanceTvDetails> ret 
            = connHandler.retrieveArray(IssuanceTvDetails.class);

        return ret.toArray(new IssuanceTvDetails[0]);
    }

    public final IssuanceTvDetails[] retrieveExpiredTvDetails(Timestamp startDt, Timestamp endDt, 
                                                               String doLaaName,  boolean hideShutdownFacility,boolean hideExemptPBR,
                                                               boolean hideEUPermitStatusTRS,boolean hideEUExemptionDmPe,boolean hideEUShutdownInvalid,
                                                               boolean hidePtoPtioEuActivePBR, String issuanceType)
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveExpiredTvDetailsByDate"));

        if (doLaaName != null) {
            query.append(" AND do.do_laa_dsc='" + doLaaName + "'");
        }
        if (issuanceType != null) {
            query.append(" AND pi.issuance_type_cd = '" + issuanceType + "'");
        }
		if (hideShutdownFacility) {
			query.append("\nAND fp.operating_status_cd != 'sd'");
		}
		if (hideExemptPBR) {
			query.append("\nAND fp.permit_classification_cd NOT IN ('extm', 'pbr')");
		}
		if (hideEUPermitStatusTRS) {
			query.append("\nAND peu.permit_status_cd NOT IN ('T', 'R', 'S')");
		}
		if (hideEUExemptionDmPe) {
			query.append("\nAND feu.exempt_status_cd NOT IN ('exm', 'dem')");
		}
		if (hideEUShutdownInvalid) {
			query.append("\nAND feu.operating_status_cd NOT IN ('sd', 'iv')");
		}
		if (hidePtoPtioEuActivePBR) {
			query.append("\nAND (pt.permit_type_cd NOT IN ('SPTO', 'PTIO') OR\n" +
	                  "(SELECT COUNT(*) FROM stars2.pt_eu peu2\n" +
	                  "JOIN stars2.pt_eu_group eug2 ON (peu2.permit_eu_group_id = eug2.permit_eu_group_id)\n" +
	                  "JOIN stars2.pt_permit pt2 ON (eug2.permit_id = pt2.permit_id AND pt2.permit_type_cd = 'PBR')\n" +
	                  "WHERE peu2.permit_status_cd = 'A' AND peu2.corr_epa_emu_id = peu.corr_epa_emu_id\n" +
	                ") = 0)\n");
		}
        query.append(" ORDER BY pt.permit_nbr");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));
        
        ArrayList<IssuanceTvDetails> ret 
            = connHandler.retrieveArray(IssuanceTvDetails.class);

        return ret.toArray(new IssuanceTvDetails[0]);
    }
    /*
    public final IssuancePbrDetails[] retrieveIssuancePbrDetails(Timestamp startDt, Timestamp endDt, 
                                                                 String doLaaName)
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrieveIssuancePbrDetailsByDate"));

        if (doLaaName != null) {
            query.append(" AND do.do_laa_dsc = '" + doLaaName + "'");
        }

        query.append(" ORDER BY fp.facility_id, feu.epa_emu_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        String s = query.toString();
        connHandler.setSQLStringRaw(s);

        connHandler.setTimestamp(1, formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, formatEndOfDay(endDt));
        
        ArrayList<IssuancePbrDetails> ret 
            = connHandler.retrieveArray(IssuancePbrDetails.class);

        return ret.toArray(new IssuancePbrDetails[0]);
    }
*/
    public final PermitWorkers[] retrievePermitWorkers(List<String> doLaas)
            throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrievePermitWorkers"));

        if (doLaas != null) {
            StringBuffer dolaas = new StringBuffer("(");
            for (String doLaaCd : doLaas) {
                dolaas.append("'" + doLaaCd + "',");
            }
            dolaas.replace(dolaas.length() - 1, dolaas.length(), ")");

            query.append(" AND fp.do_laa_cd IN " + dolaas.toString());
            query.append(" order by wt.activity_template_nm, pa.user_id, do.do_laa_cd");
        }

        ConnectionHandler connHandler = new ConnectionHandler(true);
        String s = query.toString();

        connHandler.setSQLStringRaw(s);

        ArrayList<PermitWorkers> ret = connHandler
                .retrieveArray(PermitWorkers.class);

        return ret.toArray(new PermitWorkers[0]);

    }

    public final WorkloadData[] retrievePermitWorkload(List<String> doLaas, String activityStatusCd)
            throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveWorkloadDetails"));

        if (doLaas != null) {
            StringBuffer dolaas = new StringBuffer("(");
            for (String doLaaCd : doLaas) {
                dolaas.append("'" + doLaaCd + "',");
            }
            dolaas.replace(dolaas.length() - 1, dolaas.length(), ")");
            query.append(" AND fp.do_laa_cd IN " + dolaas.toString());
        }
        
        if (activityStatusCd != null) {
            if (activityStatusCd.contains(",")) {
                query.append(" AND pa.activity_status_cd IN ('"
                        + activityStatusCd.replaceAll("\\,", "','") + "')");
            } else {
                query.append(" AND pa.activity_status_cd = " + activityStatusCd);
            }
        }

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(query.toString());

        ArrayList<WorkloadData> ret = connHandler
                .retrieveArray(WorkloadData.class);

        return ret.toArray(new WorkloadData[0]);

    }

    /**
     * @see us.oh.state.epa.stars2.database.dao.ReportsDAO#retrieveActivityList(us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity)
     */
    public final ProcessActivity[] retrieveActivityList(ProcessActivity pa,
            String doLaaCds) throws DAOException {
        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveActivities"));

        String facilityId = pa.getFacilityId();
        if (pa.getFacilityId() != null && facilityId.trim().length() > 0) {
            query.append(" AND LOWER(A.FACILITY_ID) LIKE ");
            query.append("LOWER('");
            query.append(SQLizeString(facilityId.replace("*", "%")));
            query.append("')");
        }
        String facilityName = pa.getFacilityNm();
        if (facilityName != null && facilityName.trim().length() > 0) {
            query.append(" AND LOWER(A.facility_Nm) LIKE ");
            query.append("LOWER('");
            query.append(SQLizeString(facilityName.replace("*", "%")));
            query.append("')");
        }

        /*if (doLaaCds != null && doLaaCds.trim().length() > 0) {
            query.append(" AND A.DO_LAA_CD IN ('");
            query.append(doLaaCds.replace(",", "','"));
            query.append("')");
        }*/
        if (pa.getPermitType() != null
                && !"TOTAL".equalsIgnoreCase(pa.getPermitType())) {
            query.append(" AND PER.PERMIT_TYPE_CD = '");
            query.append(SQLizeString(pa.getPermitType()));
            query.append("'");
        }
        if (pa.getIssuanceType() != null
                && !"TOTAL".equalsIgnoreCase(pa.getIssuanceType())) {
            query.append(" AND PER.PERMIT_GLOBAL_STATUS_CD = '");
            query.append(SQLizeString(pa.getIssuanceType()));
            query.append("'");
        } else {
            query.append(" AND PER.PERMIT_GLOBAL_STATUS_CD IN ('N', 'D', 'PP', 'PPP')");
        }
        if (pa.getUserId() != null) {
            query.append(" AND PA.User_Id = ");
            query.append(pa.getUserId());
        }


        ArrayList<String> status = pa.getActivityStatusCds();
        if (status != null && status.size() > 0) {
            query.append(" AND ");
            query.append(formatQuery(status, "activity_status_cd", pa
                    .isInStatus()));
        } else if (pa.getActivityStatusCd() != null) {
            query.append(" AND PA.ACTIVITY_STATUS_CD = '");
            query.append(SQLizeString(pa.getActivityStatusCd()));
            query.append("'");
        }
        if (pa.getActivityTemplateNm() != null) {
            //
            // Special case for workload mgmt report
            //
            if ("Total".equalsIgnoreCase(pa.getActivityTemplateNm())) {
                query
                        .append(" and "
                        		+ "(ATD.activity_template_nm='Completeness Review' or"
                                + " ATD.activity_template_nm='Tech Review/Draft Permit/Waiver' or"
                                + " ATD.activity_template_nm='Permit Peer Review' or"
                                + " ATD.activity_template_nm='Manager/Supervisor Review') ");
            } else {
                query.append(" AND ATD.ACTIVITY_TEMPLATE_NM = '");
                query.append(SQLizeString(pa.getActivityTemplateNm()));
                query.append("'");
            }
        }
        if (pa.getProcessCd() != null) {
            query.append(" AND PT.PROCESS_CD = '");
            query.append(SQLizeString(pa.getProcessCd()));
            query.append("'");
        }
        if (pa.getProcessId() != null) {
            query.append(" AND pa.process_id = ");
            query.append(pa.getProcessId());
        }
        String processTemplateNm = pa.getProcessTemplateNm();
        if (processTemplateNm != null) {
            query.append(" AND LOWER(PT.PROCESS_TEMPLATE_NM) LIKE ");
            query.append("LOWER('");
            query.append(SQLizeString(processTemplateNm.replace("*", "%")));
            query.append("')");
        }
        if (pa.getActivityTemplateId() != null) {
            query.append(" AND PA.Activity_Template_Id = ");
            query.append(pa.getActivityTemplateId());
        }
        if (pa.getCurrent() != null) {
            query.append(" AND pa.is_current = '");
            query.append(SQLizeString(pa.getCurrent()));
            query.append("'");
        }
        if (pa.getAggregate() != null) {
            query.append(" AND AT.AGGREGATE = '");
            query.append(SQLizeString(pa.getAggregate()));
            query.append("'");
        }
        if (pa.getPerformerTypeCd() != null) { // A or M
            query.append(" AND PA.PERFORMER_TYPE_CD = '");
            query.append(SQLizeString(pa.getPerformerTypeCd()));
            query.append("'");
        }
        if (pa.getExternalId() != null) {
            query.append(" AND P.External_Id = ");
            query.append(pa.getExternalId());
        }
        if (pa.getLoopCnt() != null) {
            query.append(" AND PA.loop_cnt = ");
            query.append(pa.getLoopCnt());
        }

        query.append(" ORDER BY pa.END_DT, pa.start_dt ");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());

        ArrayList<ProcessActivity> ret = connHandler
                .retrieveArray(ProcessActivity.class);
        return ret.toArray(new ProcessActivity[0]);
    }

    public final PermitSOPData[] retrievePermitSOPData(List<String> doLaas,
                                                       List<String> permitTypes, List<String> reasonCds, 
                                                       boolean general, boolean express, boolean hideShutdownFacility, 
                                                       boolean hideDeadEndedPermit)
        
        throws DAOException {

        StringBuffer query 
            = new StringBuffer(loadSQL("ReportsSQL.retrievePermitSOPData"));
        
        if (doLaas != null) {
            StringBuffer dolaas = new StringBuffer("(");
            for (String doLaaCd : doLaas) {
                dolaas.append("'" + doLaaCd + "',");
            }
            dolaas.replace(dolaas.length() - 1, dolaas.length(), ")");
            query.append(" AND fp.do_laa_cd IN " + dolaas.toString());
        }
        
        if (permitTypes != null) {
            StringBuffer types = new StringBuffer("(");
            for (String permitTypesCd : permitTypes) {
                types.append("'" + permitTypesCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND pm.permit_type_cd IN " + types.toString());
        }
        
        if (reasonCds != null) {
            StringBuffer types = new StringBuffer("(");
            for (String reasonCd : reasonCds) {
                types.append("'" + reasonCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND pm.primary_reason_cd IN " + types.toString());
        }
        
        if (general) {
            query.append(" AND ptio.general_permit_flag = 'Y' ");
        }

        if (express) {
            query.append(" AND ptio.express_flag = 'Y' ");
        }
        
        if (hideShutdownFacility) {
            query.append(" AND fp.operating_status_cd != 'sd' ");
        }
        
        if (hideDeadEndedPermit) {
            query.append(" AND pm.permit_global_status_cd != 'E' ");
        }
        
        query.append("ORDER BY p.process_id, task_start_dt, pa.loop_cnt");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());
        
        ArrayList<PermitSOPData> ret 
            = connHandler.retrieveArray(PermitSOPData.class);

        return ret.toArray(new PermitSOPData[0]);
    }
    
    public final PermitSOPData[] retrieveLatePermitsData(List<String> doLaas,
            List<String> permitTypes, List<String> reasonCds, 
            boolean general, boolean express)

    throws DAOException {

        StringBuffer query 
        = new StringBuffer(loadSQL("ReportsSQL.retrievePermitSOPData"));

        if (doLaas != null) {
            StringBuffer dolaas = new StringBuffer("(");
            for (String doLaaCd : doLaas) {
                dolaas.append("'" + doLaaCd + "',");
            }
            dolaas.replace(dolaas.length() - 1, dolaas.length(), ")");
            query.append(" AND fp.do_laa_cd IN " + dolaas.toString());
        }

        if (permitTypes != null) {
            StringBuffer types = new StringBuffer("(");
            for (String permitTypesCd : permitTypes) {
                types.append("'" + permitTypesCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND pm.permit_type_cd IN " + types.toString());
        }

        if (reasonCds != null) {
            StringBuffer types = new StringBuffer("(");
            for (String reasonCd : reasonCds) {
                types.append("'" + reasonCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND pm.primary_reason_cd IN " + types.toString());
        }

        if (general) {
            query.append(" AND ptio.general_permit_flag = 'Y' ");
        }

        if (express) {
            query.append(" AND ptio.express_flag = 'Y' ");
        }

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());

        ArrayList<PermitSOPData> ret 
        = connHandler.retrieveArray(PermitSOPData.class);

        return ret.toArray(new PermitSOPData[0]);
    }

    /**
     * @see ReportDAO#retrieveTOPSDate(Timestamp reportDate)
     */
    public final TOPSData retrieveTOPSData(Timestamp reportDate) throws DAOException {

        checkNull(reportDate);

        TOPSData topsData = new TOPSData();

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getReadOnlyConnection();

            String sql = loadSQL("ReportsSQL.topsPartTwoA");
            ps = conn.prepareStatement(sql.toString());

            ResultSet res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvSCSCFacilityCount(res.getInt("tv_facility_count"));
            }
            closeStatement(ps);

            // 2.b is not applicable for WY. According to AQD, in WY TV facilities do not (or at least have not)
            // applied for synthetic minor restriction in lieu of Part 70 permit. So, default 2.b to 0.
            /*sql = loadSQL("ReportsSQL.topsPartTwoB");
            ps = conn.prepareStatement(sql.toString());

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setSmtvSCSCFacilityCount(res.getInt("smtv_facility_count"));
            }
            closeStatement(ps);*/
            topsData.setSmtvSCSCFacilityCount(0);

            sql = loadSQL("ReportsSQL.topsPartTwoD");
            ps = conn.prepareStatement(sql.toString());

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvFacilityCount(res.getInt("tv_permits_apps_cnt"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartThree");
            ps = conn.prepareStatement(sql.toString());
            //ps.setTimestamp(1, reportDate);    2559 0005684

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvActivePTOCount(res.getInt("tv_active_pto"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartFourA");
            ps = conn.prepareStatement(sql.toString());
            ps.setTimestamp(1, reportDate);
            ps.setTimestamp(2, reportDate);

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvInitialIssuedCount(res.getInt("tv_initial"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartFourB");
            ps = conn.prepareStatement(sql.toString());
            ps.setTimestamp(1, reportDate);
            ps.setTimestamp(2, reportDate);

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvOnTimeCount(res.getInt("tv_ontime"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartFive");
            ps = conn.prepareStatement(sql.toString());
            ps.setTimestamp(1, reportDate);

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvOverTimeCount(res.getInt("tv_overtime"));
            }
            closeStatement(ps);

            /*
            sql = loadSQL("ReportsSQL.topsPartSixA");
            ps = conn.prepareStatement(sql.toString());
            //ps.setTimestamp(1, reportDate);    2559 0005687

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvExpiredCount(res.getInt("tv_expired_count"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartSixB");
            ps = conn.prepareStatement(sql.toString());

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvExtendedCount(res.getInt("tv_extended_count"));
            }
            closeStatement(ps);*/

            sql = loadSQL("ReportsSQL.topsPartSevenA");
            ps = conn.prepareStatement(sql.toString());
            ps.setTimestamp(1, reportDate);
            ps.setTimestamp(2, reportDate);

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvSigModIssued(res.getInt("tv_spm"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartSevenB");
            ps = conn.prepareStatement(sql.toString());
            ps.setTimestamp(1, reportDate);
            ps.setTimestamp(2, reportDate);

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvSigModIssuedIn18Mo(res.getInt("tv_spm_ontime"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartSevenC");
            ps = conn.prepareStatement(sql.toString());
            ps.setTimestamp(1, reportDate);
            ps.setTimestamp(2, reportDate);

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvSigModIssuedIn9Mo(res.getInt("tv_spm_ontime"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartEight");
            ps = conn.prepareStatement(sql.toString());
            ps.setTimestamp(1, reportDate);

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvSigModOutstanding18Mo(res.getInt("tv_spm_overtime"));
            }
            closeStatement(ps);

            sql = loadSQL("ReportsSQL.topsPartTen");
            ps = conn.prepareStatement(sql.toString());
            ps.setTimestamp(1, reportDate);

            res = ps.executeQuery();
            while (res.next()) {
                topsData.setTvMinorModOutstanding3Mo(res.getInt("tv_mpm_overtime"));
            }
            closeStatement(ps);

        }
        catch (Exception ex) {
            logger.error("Unexpected exception: " + ex.getMessage(), ex);
            handleException(ex, conn);
        }
        finally {
            closeStatement(ps);
            handleClosing(conn);
        }


        return topsData;
    }
    
    /* (non-Javadoc)
     * @see us.oh.state.epa.stars2.database.dao.ReportsDAO#retrievePreliminaryReviewCompletedData(java.util.List, java.util.List, java.util.List, java.sql.Timestamp, java.sql.Timestamp)
     */
    public PermitSOPData[] retrievePreliminaryReviewCompletedData(
            List<String> doLaas, List<String> permitTypes,
            List<String> reasonCds, Timestamp fromDate, Timestamp toDate)
            throws DAOException {

        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrievePreliminaryReviewCompletedData"));

        if (doLaas != null) {
            StringBuffer dolaas = new StringBuffer("(");
            for (String doLaaCd : doLaas) {
                dolaas.append("'" + doLaaCd + "',");
            }
            dolaas.replace(dolaas.length() - 1, dolaas.length(), ")");
            query.append(" AND fp.do_laa_cd IN " + dolaas.toString());
        }

        if (permitTypes != null) {
            StringBuffer types = new StringBuffer("(");
            for (String permitTypesCd : permitTypes) {
                types.append("'" + permitTypesCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND pm.permit_type_cd IN " + types.toString());
        }

        if (reasonCds != null) {
            StringBuffer types = new StringBuffer("(");
            for (String reasonCd : reasonCds) {
                types.append("'" + reasonCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND pm.primary_reason_cd IN " + types.toString());
        }

        if ((fromDate != null || toDate != null)) {

            query.append(" AND ");
            query.append("(");
            String dateBy = "p.start_dt";
            if (fromDate != null) {
                query.append(dateBy);
                query.append(" >= ? ");
                if (toDate != null) {
                    query.append(" AND ");
                }
            }
            if (toDate != null) {
                query.append(dateBy);
                query.append(" <= ? ");
            }
            query.append(" ) ");
        }
        
        query.append("ORDER BY atd.activity_template_def_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());
        int i = 1;
        if (fromDate != null) {
            connHandler.setTimestamp(i, formatBeginOfDay(fromDate));
            i++;
        }
        if (toDate != null) {
            connHandler.setTimestamp(i, formatEndOfDay(toDate));
            i++;
        }

        ArrayList<PermitSOPData> ret = connHandler
                .retrieveArray(PermitSOPData.class);

        return ret.toArray(new PermitSOPData[0]);
    }
    
    /* (non-Javadoc)
     * @see us.oh.state.epa.stars2.database.dao.ReportsDAO#retrieveIssuedMetricsData(java.util.List, java.lang.String, java.util.List, java.sql.Timestamp, java.sql.Timestamp)
     */
    public IssuedMetricsData[] retrieveIssuedMetricsData(
            List<String> doLaas, String permitType,
            List<String> reasonCds,  List<String> countyCds, List<String> selectedPermitActionTypes, String dateBy, Timestamp fromDate, Timestamp toDate)
            throws DAOException {

        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveIssuedMetricsData"));

        if (doLaas != null) {
            StringBuffer dolaas = new StringBuffer("(");
            for (String doLaaCd : doLaas) {
                dolaas.append("'" + doLaaCd + "',");
            }
            dolaas.replace(dolaas.length() - 1, dolaas.length(), ")");
            query.append(" AND fp.do_laa_cd IN " + dolaas.toString());
        }
        
        if (permitType != null && permitType.length() != 0) {
			query.append(" AND pm.permit_type_cd = '");
			query.append(permitType);
			query.append("'");
		}

        if (reasonCds != null) {
            StringBuffer types = new StringBuffer("(");
            for (String reasonCd : reasonCds) {
                types.append("'" + reasonCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND pm.primary_reason_cd IN " + types.toString());
        }
        
        if (countyCds != null) {
            StringBuffer types = new StringBuffer("(");
            for (String countyCd : countyCds) {
                types.append("'" + countyCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND ca.county_cd IN " + types.toString());
        }
        
        if (selectedPermitActionTypes != null) {
            StringBuffer types = new StringBuffer("(");
            for (String actionType : selectedPermitActionTypes) {
                types.append("'" + actionType + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND ptio.action_type IN " + types.toString());
        }
      
        if (dateBy != null && dateBy.trim().length() != 0
				&& (fromDate != null || toDate != null)) {

        	query.append(" AND ");
			query.append("(");
			if (fromDate != null) {
				query.append(dateBy);
				query.append(" >= ? ");
				if (toDate != null) {
					query.append(" AND ");
				}
			}
			if (toDate != null) {
				query.append(dateBy);
				query.append(" <= ? ");
			}
			query.append(" ) ");
		}
        
        query.append("ORDER BY p.process_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());
        int i = 1;
        if (fromDate != null) {
            connHandler.setTimestamp(i, formatBeginOfDay(fromDate));
            i++;
        }
        if (toDate != null) {
            connHandler.setTimestamp(i, formatEndOfDay(toDate));
            i++;
        }

        ArrayList<IssuedMetricsData> ret = connHandler
                .retrieveArray(IssuedMetricsData.class);

        return ret.toArray(new IssuedMetricsData[0]);
    }
    
    /* (non-Javadoc)
     * @see us.oh.state.epa.stars2.database.dao.ReportsDAO#retrieveIssuedMetricsData(java.util.List, java.util.List, java.util.List, java.sql.Timestamp, java.sql.Timestamp)
     */
    public IssuedMetricsData[] retrieveNotIssuedMetricsData(
            List<String> doLaas, String permitType,
            List<String> reasonCds,  List<String> countyCds, List<String> selectedPermitActionTypes, String dateBy, Timestamp fromDate, Timestamp toDate,
            boolean excludeDeadEnded, boolean excludeIssuedWithdrawal)
            throws DAOException {

        StringBuffer query = new StringBuffer(
                loadSQL("ReportsSQL.retrieveNotIssuedMetricsData"));

        if (doLaas != null) {
            StringBuffer dolaas = new StringBuffer("(");
            for (String doLaaCd : doLaas) {
                dolaas.append("'" + doLaaCd + "',");
            }
            dolaas.replace(dolaas.length() - 1, dolaas.length(), ")");
            query.append(" AND fp.do_laa_cd IN " + dolaas.toString());
        }
        
        if (permitType != null && permitType.length() != 0) {
			query.append(" AND pm.permit_type_cd = '");
			query.append(permitType);
			query.append("'");
		}

        if (reasonCds != null) {
            StringBuffer types = new StringBuffer("(");
            for (String reasonCd : reasonCds) {
                types.append("'" + reasonCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND pm.primary_reason_cd IN " + types.toString());
        }
        
        if (countyCds != null) {
            StringBuffer types = new StringBuffer("(");
            for (String countyCd : countyCds) {
                types.append("'" + countyCd + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND ca.county_cd IN " + types.toString());
        }
        
        if (selectedPermitActionTypes != null) {
            StringBuffer types = new StringBuffer("(");
            for (String actionType : selectedPermitActionTypes) {
                types.append("'" + actionType + "',");
            }
            types.replace(types.length() - 1, types.length(), ")");
            query.append(" AND ptio.action_type IN " + types.toString());
        }
        
        if (excludeDeadEnded) {
        	query.append(" AND pm.permit_global_status_cd != 'E' ");
        }
        
        if (excludeIssuedWithdrawal) {
        	query.append(" AND pm.permit_global_status_cd != 'ID' ");
        }
      
        if (dateBy != null && dateBy.trim().length() != 0
				&& (fromDate != null || toDate != null)) {

        	query.append(" AND ");
			query.append("(");
			if (fromDate != null) {
				query.append(dateBy);
				query.append(" >= ? ");
				if (toDate != null) {
					query.append(" AND ");
				}
			}
			if (toDate != null) {
				query.append(dateBy);
				query.append(" <= ? ");
			}
			query.append(" ) ");
		}
        
        query.append("ORDER BY p.process_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(query.toString());
        int i = 1;
        if (fromDate != null) {
            connHandler.setTimestamp(i, formatBeginOfDay(fromDate));
            i++;
        }
        if (toDate != null) {
            connHandler.setTimestamp(i, formatEndOfDay(toDate));
            i++;
        }

        ArrayList<IssuedMetricsData> ret = connHandler
                .retrieveArray(IssuedMetricsData.class);

        return ret.toArray(new IssuedMetricsData[0]);
    }

    /**
     * This is not working for PTI because PTI has no expiration date in database.
     * 
     * @see ReportDAO#retrieveIssuedPermits(Timestamp startDt, Timestamp endDt)
     */
    public final Issuance[] retrievePermitExpiration(Timestamp startDt, Timestamp endDt, 
    		boolean hideShutdownFacility, boolean hideExemptPBR, boolean hideEUPermitStatusTRS,
    	    boolean hideEUExemptionDmPe, boolean hideEUShutdownInvalid, boolean hidePtoPtioEuActivePBR)
        throws DAOException {
    	List<Issuance> ret = new ArrayList<Issuance>();
    	Connection conn = null;
		PreparedStatement pStmt = null;
		StringBuilder statementSQL = new StringBuilder(loadSQL("ReportsSQL.retrievePermitExpirationByDate"));        
		StringBuilder andClause = new StringBuilder("");

		if (hideShutdownFacility) {
			andClause.append("\nAND fp.operating_status_cd != 'sd'");
		}
		if (hideExemptPBR) {
			andClause.append("\nAND fp.permit_classification_cd NOT IN ('extm', 'pbr')");
		}
		if (hideEUPermitStatusTRS) {
			andClause.append("\nAND peu.permit_status_cd NOT IN ('T', 'R', 'S')");
		}
		if (hideEUExemptionDmPe) {
			andClause.append("\nAND feu.exempt_status_cd NOT IN ('exm', 'dem')");
		}
		if (hideEUShutdownInvalid) {
			andClause.append("\nAND feu.operating_status_cd NOT IN ('sd', 'iv')");
		}
		if (hidePtoPtioEuActivePBR) {
			andClause.append("\nAND (pt.permit_type_cd NOT IN ('SPTO', 'PTIO') OR\n" +
	                  "(SELECT COUNT(*) FROM stars2.pt_eu peu2\n" +
	                  "JOIN stars2.pt_eu_group eug2 ON (peu2.permit_eu_group_id = eug2.permit_eu_group_id)\n" +
	                  "JOIN stars2.pt_permit pt2 ON (eug2.permit_id = pt2.permit_id AND pt2.permit_type_cd = 'PBR')\n" +
	                  "WHERE peu2.permit_status_cd = 'A' AND peu2.corr_epa_emu_id = peu.corr_epa_emu_id\n" +
	                ") = 0)\n");
		}

        StringBuilder sortBy = new StringBuilder("\nORDER BY pt.permit_id");
		statementSQL.append(andClause.toString() + " " + sortBy.toString());
		
		try {
			Integer lastPermitId = null;
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(replaceSchema(statementSQL.toString()));
        
            pStmt.setTimestamp(1, this.formatBeginOfDay(startDt));
            pStmt.setTimestamp(2, this.formatEndOfDay(endDt));
            ResultSet rs = pStmt.executeQuery();
            
			while (rs.next()) {
				Integer permitId = AbstractDAO.getInteger(rs, "permit_id");
				if (permitId == null) {
					// should never happen
					continue;
				}
				if (!permitId.equals(lastPermitId)) {
					Issuance issuance = new Issuance();
					issuance.populate(rs);
					ret.add(issuance);
				}
				lastPermitId = permitId;
			}
            rs.close();
            
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
        
        return ret.toArray(new Issuance[0]);
    }
    
    /**
     * @param startDt
     * @param endDt
     * @return
     * @throws DAOException
     */
    /*
    public final Issuance[] retrievePTIPermitExpiration(Timestamp startDt,
            Timestamp endDt) throws DAOException {

        ConnectionHandler connHandler = new ConnectionHandler(
                "ReportsSQL.retrievePTIPermitExpirationByDate", true);

        Calendar td = new GregorianCalendar();
        td.setTimeInMillis(startDt.getTime());
        td.add(Calendar.MONTH, -18);
        startDt = new Timestamp(td.getTimeInMillis());

        td.setTimeInMillis(endDt.getTime());
        td.add(Calendar.MONTH, -18);
        endDt = new Timestamp(td.getTimeInMillis());

        connHandler.setTimestamp(1, this.formatBeginOfDay(startDt));
        connHandler.setTimestamp(2, this.formatEndOfDay(endDt));

        ArrayList<Issuance> ret = connHandler.retrieveArray(Issuance.class);

        return ret.toArray(new Issuance[0]);
    }
    */
    public final WorkloadTrend[] retrieveWorkloadTrendReport(
            String permitTypes[], String permitReasons[]) throws DAOException {
        
        Map<Timestamp, WorkloadTrend> ret = new HashMap<Timestamp, WorkloadTrend>();
        
        String query = loadSQL("ReportsSQL.AppsReceivedMonthly");

        query = query.replace("%type%", convertCommaDelimited(permitTypes));
        query = query.replace("%reason%", convertCommaDelimited(permitReasons));
        
        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(query);

            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {

                Timestamp month = rs.getTimestamp("month");
                String doLaa = rs.getString("DO_LAA_CD");
                Integer appCount = AbstractDAO.getInteger(rs, "appCount");
                
                WorkloadTrend twt = ret.get(month);
                if (twt == null){
                    twt = new WorkloadTrend();
                    twt.setMonth(month);
                    ret.put(month, twt);
                }

                Map<String, Map<String, Integer>> counts = twt.getCounts();
                Map <String, Integer> byDoLaa = counts.get(doLaa);
                if (byDoLaa == null) {
                    byDoLaa = new HashMap<String, Integer>();
                    counts.put(doLaa, byDoLaa);
                }
                byDoLaa.put("appCount", appCount);

            }
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        
        query = loadSQL("ReportsSQL.PermitsIssuedMonthly");

        query = query.replace("%type%", convertCommaDelimited(permitTypes));
        query = query.replace("%reason%", convertCommaDelimited(permitReasons));
        
        try {
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(query);

            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {

                Timestamp month = rs.getTimestamp("month");
                String doLaa = rs.getString("DO_LAA_CD");
                Integer permitCount = AbstractDAO.getInteger(rs, "permitCount");
                
                WorkloadTrend twt = ret.get(month);
                if (twt == null){
                    twt = new WorkloadTrend();
                    twt.setMonth(month);
                    ret.put(month, twt);
                }

                Map<String, Map<String, Integer>> counts = twt.getCounts();
                Map <String, Integer> byDoLaa = counts.get(doLaa);
                if (byDoLaa == null) {
                    byDoLaa = new HashMap<String, Integer>();
                    counts.put(doLaa, byDoLaa);
                }
                byDoLaa.put("permitCount", permitCount);

            }
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        
        query = loadSQL("ReportsSQL.WorkflowOutstandingMonthly");

        query = query.replace("%type%", convertCommaDelimited(permitTypes));
        query = query.replace("%reason%", convertCommaDelimited(permitReasons));
        
        try {
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(query);

            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {

                Timestamp month = rs.getTimestamp("month");
                String doLaa = rs.getString("DO_LAA_CD");
                Integer workflowCount = AbstractDAO.getInteger(rs, "workflowCount");
                
                WorkloadTrend twt = ret.get(month);
                if (twt == null){
                    twt = new WorkloadTrend();
                    twt.setMonth(month);
                    ret.put(month, twt);
                }

                Map<String, Map<String, Integer>> counts = twt.getCounts();
                Map <String, Integer> byDoLaa = counts.get(doLaa);
                if (byDoLaa == null) {
                    byDoLaa = new HashMap<String, Integer>();
                    counts.put(doLaa, byDoLaa);
                }
                byDoLaa.put("workflowCount", workflowCount);
                
            }
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }

        // Sort the Colletion by month
        Iterator<Timestamp> it = ret.keySet().iterator();
        Vector<Timestamp> keys = new Vector<Timestamp>();
        while (it.hasNext()) {
            keys.add(it.next());
        }
        Collections.sort(keys);
        WorkloadTrend[] data = new WorkloadTrend[ret.size()];
        
        for (int ii = 0; ii < keys.size(); ii++) {
            WorkloadTrend td = ret.get(keys.elementAt(ii));
            Map<String, Map<String, Integer>> tc = td.getCounts();
            Map<String, Integer> total = new HashMap<String, Integer>();
            total.put("appCount", 0);
            total.put("permitCount", 0);
            total.put("workflowCount", 0);
            
            Iterator<Map<String, Integer>> dolaas = tc.values().iterator();
            while (dolaas.hasNext()){
                Map<String, Integer> tDolass = dolaas.next();
                total.put("appCount", total.get("appCount")+((tDolass.get("appCount") == null)? 0 : tDolass.get("appCount")));
                total.put("permitCount", total.get("permitCount")+((tDolass.get("permitCount") == null)? 0 : tDolass.get("permitCount")));
                total.put("workflowCount", total.get("workflowCount")+((tDolass.get("workflowCount") == null)? 0 : tDolass.get("workflowCount")));
            }

            tc.put("TOTAL", total);
            data[keys.size()-ii-1] = td;
        }
        
        return data;
    }
    
    public final GenericIssuanceCount[] retrieveGenericIssuanceCount(
            String doLaaCds[], String issuanceTypeCds[], 
            Timestamp startDt, Timestamp endDt) throws DAOException {
        
        Map<String, GenericIssuanceCount> ret = new HashMap<String, GenericIssuanceCount>();
        
        String query = loadSQL("ReportsSQL.retrieveGenericIssuanceCount");

        query = query.replace("%DO_LAA_CDs%", convertCommaDelimited(doLaaCds));
        query = query.replace("%ISSUANCE_TYPE_CDs%", convertCommaDelimited(issuanceTypeCds));
        
        Connection conn = null;
        PreparedStatement pStmt = null;

        try {
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(query);

            pStmt.setTimestamp(1, formatBeginOfDay(startDt));
            pStmt.setTimestamp(2, formatEndOfDay(endDt));
            
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {

                String issuanceType = rs.getString("ISSUANCE_TYPE_CD");
                String doLaa = rs.getString("DO_LAA_DSC");
                Integer count = AbstractDAO.getInteger(rs, "count");
                
                GenericIssuanceCount twt = ret.get(issuanceType);
                if (twt == null){
                    twt = new GenericIssuanceCount();
                    twt.setIssuanceTypeCd(issuanceType);
                    ret.put(issuanceType, twt);
                }

                Map<String, Integer> counts = twt.getCounts();
                counts.put(doLaa, count);

            }
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        
        for (GenericIssuanceCount td : ret.values()) {
            Map<String, Integer> tc = td.getCounts();
            Integer total = 0;
            
            Iterator<Integer> dolaas = tc.values().iterator();
            while (dolaas.hasNext()){
                Integer tDolass = dolaas.next();
                total = total + tDolass;
            }

            tc.put("TOTAL", total);
        }
        
        return ret.values().toArray(new GenericIssuanceCount[0]);
    }
    
}

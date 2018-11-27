package us.oh.state.epa.stars2.database.dao.emissionsReport;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.faces.context.FacesContext;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.emissionsReport.UsEpaEisReport;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.EmissionsReportDAO;
import us.oh.state.epa.stars2.database.dao.AbstractDAO.ConnectionHandler;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.DefaultStackParms;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionTotal;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportNote;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsVariable;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityAppInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityPermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityYearPair;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.IntegerPair;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.MultiEstablishment;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.PollutantPair;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.ReportEisStatusDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.reports.MissingFIREFactor;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;

@Repository
public class EmissionsReportSQLDAO extends AbstractDAO implements
        EmissionsReportDAO {
	
	private Logger logger = Logger.getLogger(EmissionsReportSQLDAO.class);
	
    /**
     * @see EmissionsReportDAO#createEmissionPeriod(EmissionPeriod newPeriod)
     */
    public EmissionsReportPeriod createEmissionPeriod(
            EmissionsReportPeriod newPeriod) throws DAOException {
    	
        checkNull(newPeriod);
        EmissionsReportPeriod ret = newPeriod;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createEmissionPeriod", false);

        int i = 1;
        Integer periodId = nextSequenceVal("S_Emission_Period_Id");

        connHandler.setInteger(i++, periodId);
        connHandler.setString(i++, newPeriod.getSccId());
        connHandler.setString(i++, AbstractDAO
                .translateBooleanToIndicator(newPeriod.isTradeSecretS()));
        connHandler.setString(i++, newPeriod.getTradeSecretSText());
        connHandler.setInteger(i++, newPeriod.getWinterThroughputPct());
        connHandler.setInteger(i++, newPeriod.getSpringThroughputPct());
        connHandler.setInteger(i++, newPeriod.getSummerThroughputPct());
        connHandler.setInteger(i++, newPeriod.getFallThroughputPct());
        connHandler.setInteger(i++, newPeriod.getDaysPerWeek());
        connHandler.setInteger(i++, newPeriod.getWeeksPerYear());
        connHandler.setInteger(i++, newPeriod.getHoursPerDay());
        connHandler.setDouble(i++, newPeriod.getHoursPerYear());
        connHandler.setInteger(i++, newPeriod.getFirstHalfHrsOfOperationPct());
        connHandler.setInteger(i++, newPeriod.getSecondHalfHrsOfOperationPct());
        

        connHandler.update();

        ret.setEmissionPeriodId(periodId);
        ret.setLastModified(1);

        return ret;
    }

    /**
     * @see EmissionsReportDAO#createEmissions(Emissions newEmissions)
     */
    public Emissions createEmissions(Emissions newEmissions)
            throws DAOException {
        checkNull(newEmissions);
        Emissions ret = newEmissions;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createEmissions", false);

        int i = 1;
        connHandler.setInteger(i++, newEmissions.getEmissionPeriodId());
        connHandler.setString(i++, newEmissions.getPollutantCd());
        connHandler.setString(i++, newEmissions.getFireRef());
        connHandler.setString(i++, newEmissions.getEmissionCalcMethodCd());
        connHandler.setString(i++, newEmissions.getFugitiveEmissions());
        connHandler.setString(i++, newEmissions.getStackEmissions());
        connHandler.setString(i++, newEmissions.getEmissionsUnitNumerator());
        connHandler.setString(i++, newEmissions.getTimeBasedFactorNumericValue());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(newEmissions.getFactorNumericValueOverride()));
        connHandler.setString(i++, newEmissions.getFactorNumericValue());
        connHandler.setString(i++, newEmissions.getFactorUnitNumerator());
        connHandler.setString(i++, newEmissions.getFactorUnitDenominator());
//        connHandler.setString(i++, AbstractDAO
//                .translateBooleanToIndicator(newEmissions.isTradeSecretF()));
//        connHandler.setString(i++, newEmissions.getTradeSecretFText());
        connHandler.setString(i++, newEmissions.getAnnualAdjust());
        connHandler.setString(i++, newEmissions.getExplanation());
//        connHandler.setString(i++, AbstractDAO
//                .translateBooleanToIndicator(newEmissions.isTradeSecretE()));
//        connHandler.setString(i++, newEmissions.getTradeSecretEText());
        if(newEmissions.getEmissionsUnitNumerator() == null) {
            logger.error("EmissionsUnitNumerator is null for pollutant "
                    + newEmissions.getPollutantCd() + " in period " + newEmissions.getEmissionPeriodId()
                    + ". This message is to help determine why it is null:  the only effect the user notices is that they may have to reset units even though it had earlier been set.");
        }
        connHandler.update();

        ret.setLastModified(1);

        return ret;
    }
    
    /**
     * @see EmissionsReportDAO#createEmissionTotal(EmissionTotal newEmissions)
     */
    public EmissionTotal createEmissionTotal(EmissionTotal newEmissions)
            throws DAOException {
        checkNull(newEmissions);
        EmissionTotal ret = newEmissions;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createEmissionTotal", false);

        connHandler.setInteger(1, newEmissions.getEmissionsRptId());
        connHandler.setString(2, newEmissions.getPollutantCd());
        connHandler.setString(3, newEmissions.getTotalEmissions());
        connHandler.update();

        ret.setLastModified(1);
        
        // Re-initialize reportPollutants refreshTime to 0 to trigger update from db in
        // ReportSearch.getReportPollutants()
        
		if (FacesContext.getCurrentInstance() != null) {
			ReportSearch rs = (ReportSearch) FacesUtil
					.getManagedBean("reportSearch");
			if (rs != null) {
				rs.setRefreshTime(0);
			}
		}

        return ret;
    }
    
    /**
     * @see EmissionsReportDAO#createVariable(EmissionsVariable
     *      var)
     */
    public EmissionsVariable createVariable(
            EmissionsVariable var) throws DAOException {
        checkNull(var);

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createVariable", false);

        int i = 1;
        connHandler.setInteger(i++, var.getEmissionPeriodId());
        connHandler.setString(i++, var.getVariable());
        connHandler.setString(i++, var.getValue());
//        connHandler.setString(i++, AbstractDAO
//                .translateBooleanToIndicator(var.isTradeSecret()));
//        connHandler.setString(i++, var.getTradeSecretText());
        connHandler.update();

        var.setLastModified(1);

        return var;
    }

    /**
     * @see EmissionsReportDAO#createMaterialActionUnits(EmissionsMaterialActionUnits
     *      newMAU)
     */
    public EmissionsMaterialActionUnits createMaterialActionUnits(
            EmissionsMaterialActionUnits newMAU) throws DAOException {
        checkNull(newMAU);
        EmissionsMaterialActionUnits ret = new EmissionsMaterialActionUnits();

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createMaterialActionUnits", false);

        int i = 1;
        connHandler.setInteger(i++, newMAU.getEmissionPeriodId());
        connHandler.setString(i++, newMAU.getMaterial());
        connHandler.setString(i++, newMAU.getAction());
        connHandler.setString(i++, newMAU.getMeasure());
//        connHandler.setString(i++, AbstractDAO
//                .translateBooleanToIndicator(newMAU.isTradeSecretM()));
//        connHandler.setString(i++, newMAU.getTradeSecretMText());
        connHandler.setString(i++, newMAU.getThroughput());
        connHandler.setString(i++, AbstractDAO
                .translateBooleanToIndicator(newMAU.isTradeSecretT()));
        connHandler.setString(i++, newMAU.getTradeSecretTText());

        connHandler.update();

        ret.setLastModified(1);

        return ret;
    }

    /**
     * @see EmissionsReportDAO#createEmissionsReport(EmissionsReport newReport)
     */
    public EmissionsReport createEmissionsReport(EmissionsReport newReport)
            throws DAOException {
        checkNull(newReport);
        EmissionsReport ret = newReport;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createEmissionsReport", false);
        
        if(newReport.getEmissionsRptId() == null) {
            // This is needed when submitting a report from portal
            // and creating existing report in AQD database
            newReport.setEmissionsRptId(nextSequenceVal("S_Emissions_Rpt_Id"));
        }
        
        int i = 1;
        connHandler.setInteger(i++, newReport.getEmissionsRptId());
        connHandler.setInteger(i++, newReport.getReportModified());
        connHandler.setInteger(i++, newReport.getFpId());
        connHandler.setString(i++, newReport.getRptReceivedStatusCd());
        connHandler.setTimestamp(i++, newReport.getRptReceivedStatusDate());
        connHandler.setString(i++,
                AbstractDAO.translateBooleanToIndicator(newReport.isLegacy()));
        connHandler.setString(i++,
                AbstractDAO.translateBooleanToIndicator(newReport.isAutoGenerated()));
        connHandler.setTimestamp(i++, newReport.getRptApprovedStatusDate());
        connHandler.setFloat(i++, newReport.getTotalEmissions());
        connHandler.setInteger(i++, newReport.getReportYear());
        connHandler.setInteger(i++, newReport.getFeeId());
        connHandler.setInteger(i++, newReport.getFeeId2());
        connHandler.setString(i++, newReport.getEisStatusCd());
        if(newReport.getTransferDate() != null) newReport.getTransferDate().setNanos(0);
        connHandler.setTimestamp(i++, newReport.getTransferDate());
        if(newReport.getShutdownDate() != null) newReport.getShutdownDate().setNanos(0);
        connHandler.setTimestamp(i++, newReport.getShutdownDate());
        connHandler.setString(i++,
                AbstractDAO.translateBooleanToIndicator(newReport.isNewOwner()));
        connHandler.setString(i++,
                AbstractDAO.translateBooleanToIndicator(newReport.isProvideBothYears()));
        Integer id = null;
        if(newReport.getPrevOwnerForwardingAddr() != null) {
            id = newReport.getPrevOwnerForwardingAddr().getContactId();
        }
        connHandler.setInteger(i++, id);
        id = null;
        if(newReport.getNewOwnerAddr() != null) {
            id = newReport.getNewOwnerAddr().getContactId();
        }
        connHandler.setInteger(i++, id);
        id = null;
        if(newReport.getBillingAddr() != null) {
            id = newReport.getBillingAddr().getContactId();
        }
        connHandler.setInteger(i++, id);
        id = null;
        if(newReport.getPrimaryAddr() != null) {
            id = newReport.getPrimaryAddr().getContactId();
        }
        connHandler.setInteger(i++, id);
        connHandler.setString(i++, newReport.getFacilityNm());
        connHandler.setInteger(i++, newReport.getCompanionReport());
        connHandler.setTimestamp(i++, newReport.getReceiveDate());
        connHandler.setString(i++, newReport.getRevisionReason());
        connHandler.setFloat(i++, newReport.getTotalReportedEmissions());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(newReport.isValidated()));
        connHandler.setInteger(i++,newReport.getSubmitterUser());
        connHandler.setInteger(i++,newReport.getSubmitterContact());
        connHandler.update();
        ret.setLastModified(1);
        return ret;
    }

    /**
     * @see EmissionsReportDAO#createReportEUGroup(EmissionsReportEUGroup)
     */
    public EmissionsReportEUGroup createReportEUGroup(
            EmissionsReportEUGroup newEUGroup) throws DAOException {
        checkNull(newEUGroup);
        EmissionsReportEUGroup ret = newEUGroup;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createReportEUGroup", false);

        Integer groupId = nextSequenceVal("S_Emissions_Rpt_EU_Group_Id");

        connHandler.setInteger(1, groupId);
        connHandler.setString(2, newEUGroup.getReportEuGroupName());
        connHandler.update();

        ret.setReportEuGroupID(groupId);
        ret.setLastModified(1);

        return ret;
    }

    /**
     * @see EmissionsReportDAO#createEmissionsDocument(EmissionsDocumentRef)
     */
    public EmissionsDocumentRef createEmissionsDocument(
            EmissionsDocumentRef newAttachment) throws DAOException {
        checkNull(newAttachment);
        EmissionsDocumentRef ret = newAttachment;

        newAttachment.setEmissionsDocId(nextSequenceVal("S_Emissions_Doc_Id", newAttachment.getEmissionsDocId()));
        
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createEmissionsDocument", false);
        int i=1;
        connHandler.setInteger(i++, newAttachment.getEmissionsDocId());
        connHandler.setInteger(i++, newAttachment.getDocumentId());
        connHandler.setInteger(i++, newAttachment.getEmissionsRptId());
        connHandler.setInteger(i++, newAttachment.getTradeSecretDocId());
        connHandler.setString(i++, newAttachment.getEmissionsDocumentTypeCD());
        connHandler.setString(i++, newAttachment.getTradeSecretReason());
        connHandler.setString(i++, newAttachment.getDescription());
        connHandler.update();

        ret.setLastModified(1);

        return ret;
    }
    
    /**
     * @see EmissionsReportDAO#modifyEmissionsDocument(EmissionsDocumentRef rptDoc)
     */
    public final boolean modifyEmissionsDocument(EmissionsDocumentRef rptDoc)
            throws DAOException {
        checkNull(rptDoc);
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.modifyEmissionsDocument", false);

        int i=1;
        connHandler.setString(i++, rptDoc.getEmissionsDocumentTypeCD());
        connHandler.setInteger(i++, rptDoc.getDocumentId());
        connHandler.setString(i++, rptDoc.getDescription());
        connHandler.setInteger(i++, rptDoc.getTradeSecretDocId());
        connHandler.setString(i++, rptDoc.getTradeSecretReason());
        connHandler.setInteger(i++, rptDoc.getLastModified() + 1);
        connHandler.setInteger(i++, rptDoc.getEmissionsDocId());
        connHandler.setInteger(i++, rptDoc.getLastModified());
        
        return connHandler.update();
    }

    /**
     * @see EmissionsReportDAO#createReportEU(EmissionsReportEU)
     */
    public EmissionsReportEU createReportEU(EmissionsReportEU newEu)
            throws DAOException {
        checkNull(newEu);
        EmissionsReportEU ret = newEu;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createReportEU", false);

        connHandler.setInteger(1, newEu.getCorrEpaEmuId());
        connHandler.setString(2, AbstractDAO.translateBooleanToIndicator(newEu
                .getExemptEG71()));
        connHandler.setString(3, AbstractDAO.translateBooleanToIndicator(newEu
                .isZeroEmissions()));
        connHandler.setString(4, AbstractDAO.translateBooleanToIndicator(newEu
                .isForceDetailedReporting()));
        connHandler.setInteger(5, newEu.getEmissionsRptId());

        connHandler.update();

        ret.setLastModified(1);

        return ret;
    }

    /**
     * @see EmissionsReportDAO#removeReportPeriods(int)
     */
    public void removeReportPeriods(int reportId) throws DAOException {
        removeRows("rp_report_period_xref", "emissions_rpt_id", reportId);

        return;
    }
    
    /**
     * @see EmissionsReportDAO#removeReportPeriod(int)
     */
    public void removeReportPeriod(int periodId) throws DAOException {
        removeRows("rp_report_period_xref", "emission_period_id", periodId);

        return;
    }

    /**
     * @see EmissionsReportDAO#removeEmissionsDocument(int)
     */
    public void removeEmissionsDocument(int docId) throws DAOException {
        removeRows("rp_emissions_document", "emissions_doc_id", docId);

        return;
    }
    
    /**
     * @see EmissionsReportDAO#removeEmissionsDocuments(int)
     */
    public void removeEmissionsDocuments(int reportId) throws DAOException {
        removeRows("rp_emissions_document", "emissions_rpt_id", reportId);

        return;
    }

    /**
     * @see EmissionsReportDAO#removeReportTypes(int)
     */
    public void removeReportTypes(int reportId) throws DAOException {
        removeRows("rp_report_report_type_xref", "emissions_rpt_id", reportId);

        return;
    }

    /**
     * @see EmissionsReportDAO#removeEmissionPeriod(int)
     */
    public void removeEmissionPeriod(int emissionPeriodId) throws DAOException {
        removeRows("rp_emission_period", "emission_period_id", emissionPeriodId);

        return;
    }
    
    /**
     * @see EmissionsReportDAO#removeEmissionsByReportId(int)
     */
    public void removeEmissionsByReportId(int emissionsRptId) throws DAOException {
        removeRows("rp_report_pollutant_totals", "emissions_rpt_id", emissionsRptId);

        return;
    }

    /**
     * @param emissionPeriodId
     * @throws DAOException
     */
    public void removeEmissionMaterialActionUnits(int emissionPeriodId)
            throws DAOException {
        removeRows("rp_material_actions", "emission_period_id",
                emissionPeriodId);

        return;
    }
    
    /**
     * @param emissionPeriodId
     * @throws DAOException
     */
    public void removeEmissionsVariables(int emissionPeriodId)
            throws DAOException {
        removeRows("rp_period_variables", "emission_period_id",
                emissionPeriodId);

        return;
    }

    /**
     * @param emissionPeriodId
     * @throws DAOException
     */
    public void removeEmissionsByPeriodId(int emissionPeriodId)
            throws DAOException {
        removeRows("rp_emissions", "emission_period_id", emissionPeriodId);

        return;
    }

    /**
     * @see EmissionsReportDAO#removeReportNotes(int)
     */
    public void removeReportNotes(int reportId) throws DAOException {
        removeRows("rp_emissions_rpt_note_xref", "emissions_rpt_id", reportId);

        return;
    }

    /**
     * @see EmissionsReportDAO#removeReportEUGroupsFromReport(int)
     */
    public void removeReportEUGroupsFromReport(int reportId)
            throws DAOException {
        removeRows("rp_report_eu_groups_xref", "emissions_rpt_id", reportId);

        return;
    }
    
    public void removeReportEUGroups(int groupId)
    		throws DAOException {
    	removeRows("rp_report_eu_groups", "report_eu_group_id", groupId);

    	return;
    }
    
    /**
     * @see EmissionsReportDAO#removeReportEUs(int)
     */
    public void removeReportEUs(int reportId)
            throws DAOException {
        removeRows("rp_report_eu", "emissions_rpt_id", reportId);

        return;
    }

    /**
     * @see EmissionsReportDAO#removeReportEUsFromGroups(int)
     */
    public void removeReportEUsFromGroups(int reportId) throws DAOException {
        removeRows("rp_report_eu_group_xref", "emissions_rpt_id", reportId);

        return;
    }

    /**
     * @see EmissionsReportDAO#addReportNote(EmissionsReportNote newReport)
     */
    public EmissionsReportNote addReportNote(EmissionsReportNote newReport)
            throws DAOException {
        checkNull(newReport);
        EmissionsReportNote ret = newReport;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.addReportNote", false);

        connHandler.setInteger(1, newReport.getEmissionsRptId());
        connHandler.setInteger(2, newReport.getNoteId());

        connHandler.update();

        ret.setLastModified(1);

        return ret;
    }

    /**
     * @see EmissionsReportDAO#addReportPeriod(int, int, int)
     */
    public void addReportPeriod(int emissionPeriodId, Integer emuId,
            Integer euGroupId, int emissionRptId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.addReportPeriod", false);

        int i = 1;
        connHandler.setInteger(i++, emissionPeriodId);
        connHandler.setInteger(i++, emuId);
        connHandler.setInteger(i++, euGroupId);
        connHandler.setInteger(i++, emissionRptId);

        connHandler.update();
    }

    /**
     * @see EmissionsReportDAO#addReportEUGroup(int, int, int)
     */
    public void addReportEUGroup(int euGroupId, int emuId, int emissionRptId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.addReportEUGroup", false);

        connHandler.setInteger(1, euGroupId);
        connHandler.setInteger(2, emuId);
        connHandler.setInteger(3, emissionRptId);

        connHandler.update();
    }

    /**
     * @see EmissionsReportDAO#addEUGroupToReport(int, int)
     */
    public void addEUGroupToReport(int euGroupId, int emissionRptId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.addEUGroupToReport", false);

        connHandler.setInteger(1, emissionRptId);
        connHandler.setInteger(2, euGroupId);

        connHandler.update();
    }

    /**
     * @see EmissionsReportDAO#addEUGroupToReport(int, int)
     */
    public void addReportType(int emissionRptId, String reportType)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.addReportType", false);

        connHandler.setInteger(1, emissionRptId);
        connHandler.setString(2, reportType);

        connHandler.update();
    }

    /**
     * @see EmissionsReportDAO#modifyEmissionPeriod(EmissionsReportPeriod)
     */
    public boolean modifyEmissionPeriod(EmissionsReportPeriod period)
            throws DAOException {
        checkNull(period);
        boolean ret = false;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.modifyEmissionPeriod", false);

        int i = 1;
        connHandler.setString(i++, AbstractDAO
                .translateBooleanToIndicator(period.isTradeSecretS()));
        connHandler.setString(i++, period.getTradeSecretSText());
        connHandler.setInteger(i++, period.getWinterThroughputPct());
        connHandler.setInteger(i++, period.getSpringThroughputPct());
        connHandler.setInteger(i++, period.getSummerThroughputPct());
        connHandler.setInteger(i++, period.getFallThroughputPct());
        connHandler.setInteger(i++, period.getDaysPerWeek());
        connHandler.setInteger(i++, period.getWeeksPerYear());
        connHandler.setInteger(i++, period.getHoursPerDay());
        connHandler.setDouble(i++, period.getHoursPerYear());
        connHandler.setInteger(i++, period.getFirstHalfHrsOfOperationPct());
        connHandler.setInteger(i++, period.getSecondHalfHrsOfOperationPct());
        if(null == period.getLastModified()) { // TODO Temp debuggging
            ret = false;
        }
        connHandler.setInteger(i++, period.getLastModified() + 1);
        connHandler.setInteger(i++, period.getEmissionPeriodId());
        connHandler.setInteger(i++, period.getLastModified());

        connHandler.update();

        ret = true;
        return ret;
    }

    /**
     * @see EmissionsReportDAO#modifyEmissionsReport(EmissionsReport report, Integer replaceId)
     */
    public boolean modifyEmissionsReport(EmissionsReport report, Integer replaceId)
            throws DAOException {
        checkNull(report);
        boolean ret = false;
        ConnectionHandler connHandler;
        if(replaceId != null) {
            connHandler = new ConnectionHandler(
                "EmissionsReportSQL.modifyEmissionsReport2", false);
        } else {
            connHandler = new ConnectionHandler(
                    "EmissionsReportSQL.modifyEmissionsReport", false);
        }

        int i = 1;
        connHandler.setTimestamp(i++, report.getRptReceivedStatusDate());
        connHandler.setString(i++,
                AbstractDAO.translateBooleanToIndicator(report.isLegacy()));
        connHandler.setString(i++,
                AbstractDAO.translateBooleanToIndicator(report.isAutoGenerated()));
        connHandler.setTimestamp(i++, report.getRptApprovedStatusDate());
        connHandler.setString(i++, report.getRptReceivedStatusCd());
        connHandler.setFloat(i++, report.getTotalEmissions());
        connHandler.setInteger(i++, report.getFeeId());
        connHandler.setInteger(i++, report.getFeeId2());
        connHandler.setString(i++, report.getEisStatusCd());
        connHandler.setInteger(i++, report.getFpId());
        if(report.getTransferDate() != null) report.getTransferDate().setNanos(0);
        connHandler.setTimestamp(i++, report.getTransferDate());
        if(report.getShutdownDate() != null) report.getShutdownDate().setNanos(0);
        connHandler.setTimestamp(i++, report.getShutdownDate());
        connHandler.setString(i++,
                AbstractDAO.translateBooleanToIndicator(report.isNewOwner()));
        connHandler.setString(i++,
                AbstractDAO.translateBooleanToIndicator(report.isProvideBothYears()));
        Integer id;
        if(report.getPrevOwnerForwardingAddr() != null) {
            id = report.getPrevOwnerForwardingAddr().getContactId();
        } else id = report.getPrevOwnerForwardingAddrInteger();
        connHandler.setInteger(i++, id);
        if(report.getNewOwnerAddr() != null) {
            id = report.getNewOwnerAddr().getContactId();
        } else id = report.getNewOwnerAddrInteger();
        connHandler.setInteger(i++, id);
        if(report.getBillingAddr() != null) {
            id = report.getBillingAddr().getContactId();
        } else id = report.getBillingAddrInteger();
        connHandler.setInteger(i++, id);
        if(report.getPrimaryAddr() != null) {
            id = report.getPrimaryAddr().getContactId();
        } else id = report.getPrimaryAddrInteger();
        connHandler.setInteger(i++, id);
        connHandler.setString(i++, report.getFacilityNm());
        connHandler.setInteger(i++, report.getCompanionReport());
        connHandler.setInteger(i++, report.getLastModified() + 1);
        connHandler.setTimestamp(i++, report.getReceiveDate());
        if(report.getInvoiceAmount()!=null){
        	connHandler.setDouble(i++, Double.parseDouble(report.getInvoiceAmount().replaceAll(",","").replaceAll("\\$","")));
        }else{
        	return false;
        }
        connHandler.setTimestamp(i++, report.getInvoiceDate());
        connHandler.setTimestamp(i++, report.getPaymentReceivedDate());
        connHandler.setString(i++, report.getRevisionReason());
        if(replaceId != null) {
            connHandler.setInteger(i++, replaceId);
        }
        connHandler.setFloat(i++, report.getTotalReportedEmissions());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(report.isValidated()));
        
        connHandler.setInteger(i++, report.getSubmitterUser());
        connHandler.setInteger(i++, report.getSubmitterContact());

        connHandler.setInteger(i++, report.getEmissionsRptId());
        connHandler.setInteger(i++, report.getLastModified());

                
        connHandler.update();
        if(replaceId != null) {
            // update report Id
            report.setEmissionsRptId(replaceId);
        }
        ret = true;
        return ret;
    }
    
    /**
     * @see EmissionsReportDAO#deleteEmissionsReport(EmissionsReport report)
     */
    public boolean deleteEmissionsReport(EmissionsReport report)
            throws DAOException {
        checkNull(report);
        // logger.error("deleteEmissionsReport() called on " + report.getEmissionsRptId(), new Exception());
        boolean ret = false;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.deleteEmissionsReport", false);

        connHandler.setInteger(1, report.getEmissionsRptId());
        connHandler.setInteger(2, report.getLastModified());
        connHandler.update();
        ret = true;
        return ret;
    }

    /**
     * @see EmissionsReportDAO#retrieveEmissionPeriod(int emissionPeriodId)
     */
    public EmissionsReportPeriod retrieveEmissionPeriod(int emissionPeriodId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionPeriod", true);

        connHandler.setInteger(1, emissionPeriodId);

        return (EmissionsReportPeriod) connHandler
                .retrieve(EmissionsReportPeriod.class);
    }

    /**
     * @see EmissionsReportDAO#periodMaterialExists(int, String)
     */
    public boolean periodMaterialExists(int emissionPeriodId, String materialCd)
            throws DAOException {
        boolean ret = false;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.periodMaterialExists", true);

        connHandler.setInteger(1, emissionPeriodId);
        connHandler.setString(2, materialCd);

        if (connHandler.retrieveJavaObject(Integer.class) != null) {
            ret = true;
        }

        return ret;
    }

    /**
     * @see EmissionsReportDAO#retrieveEmissionPeriods(int reportId)
     */
    public EmissionsReportPeriod[] retrieveEmissionPeriods(int reportId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionPeriods", true);

        connHandler.setInteger(1, reportId);

        ArrayList<EmissionsReportPeriod> ret = connHandler
                .retrieveArray(EmissionsReportPeriod.class);

        return ret.toArray(new EmissionsReportPeriod[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveFeeinfo(int feeId)
     */
    public Fee[] retrieveFeeinfo(int feeId)
            throws DAOException {
    	
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveFeeInfo", true);

        connHandler.setInteger(1, feeId);

        ArrayList<Fee> ret = connHandler.retrieveArray(Fee.class);
        
        return ret.toArray(new Fee[0]);               
      
     }
    
    /**
     * @see EmissionsReportDAO#retrieveFeeinfo(String feeId)
     */
    public String[] retrieveSuperCd(String pollutantCd)
            throws DAOException {
    	
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveSuperCd", true);

        connHandler.setString(1, pollutantCd);

        ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class);     
        
        return ret.toArray(new String[0]);               
      
     }
    
    
    /**
     * @see EmissionsReportDAO#retrieveEmissionPeriods(int reportId)
     */
    public FacilityYearPair[] retrieveStragglerNTV(int year1, int year2)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.searchForAutoGenNTV", true);

        connHandler.setInteger(1, year1);
        connHandler.setInteger(2, year2);

        ArrayList<FacilityYearPair> ret = connHandler
                .retrieveArray(FacilityYearPair.class);

        return ret.toArray(new FacilityYearPair[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveMaterialActionUnits(int
     *      emissionsPeriodId)
     */
    public EmissionsMaterialActionUnits[] retrieveMaterialActionUnits(
            int emissionsPeriodId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveMaterialActionUnits", true);

        connHandler.setInteger(1, emissionsPeriodId);

        ArrayList<EmissionsMaterialActionUnits> ret = connHandler
                .retrieveArray(EmissionsMaterialActionUnits.class);

        return ret.toArray(new EmissionsMaterialActionUnits[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveEmissionsVariables(int
     *      emissionsPeriodId)
     */
    public EmissionsVariable[] retrieveEmissionsVariables(
            int emissionsPeriodId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveVariables", true);

        connHandler.setInteger(1, emissionsPeriodId);

        ArrayList<EmissionsVariable> ret = connHandler
                .retrieveArray(EmissionsVariable.class);

        return ret.toArray(new EmissionsVariable[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveEmissionTotals(int reportId)
     */
    public EmissionTotal[] retrieveEmissionTotals(int reportId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionTotal", true);
        connHandler.setInteger(1, reportId);

        ArrayList<EmissionTotal> ret = connHandler
        .retrieveArray(EmissionTotal.class);

        return ret.toArray(new EmissionTotal[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveEmissions(EmissionsReportPeriod period)
     */
    public Emissions[] retrieveEmissions(EmissionsReportPeriod period)
    throws DAOException {
        int periodId = period.getEmissionPeriodId();
    	
    	ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissions", true);
        connHandler.setInteger(1, periodId);

        ArrayList<Emissions> ret = connHandler
        .retrieveArray(Emissions.class);

        return ret.toArray(new Emissions[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveEmissionsReport(int reportId)
     */
    public EmissionsReport retrieveEmissionsReport(int reportId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionsReport", true);

        connHandler.setInteger(1, reportId);

        return (EmissionsReport) connHandler.retrieve(EmissionsReport.class);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveLatestEmissionReport(Integer year, String facilityId)
     */
    public EmissionsReport retrieveLatestEmissionReport(Integer year,
            String facilityId) throws DAOException {
        EmissionsReport ret = null;
        Connection conn = getConnection();
        StringBuffer statementSQL = new StringBuffer(
                loadSQL("EmissionsReportSQL.retrieveLatestEmissionReport"));
        statementSQL.append(" AND rer.report_year = " + year.toString());
        statementSQL.append(" AND ff.facility_id = '" + facilityId + "'");
        statementSQL.append(") rer WHERE r = 1");

        try {
        	logger.debug("Rtrieve Latest Emission Report SQL = " + statementSQL.toString());
            ResultSet rs = conn.createStatement().executeQuery(
                    statementSQL.toString());
            if (rs.next()) {
                ret = new EmissionsReport();
                ret.populate(rs);
            }
        } catch (SQLException e) {
            handleException(e, conn);
        } finally {
            handleClosing(conn);
        }
        return ret;
    }
    
    /**
     * @see EmissionsReportDAO#retrieveLatestTvEmissionReport(Integer year, String facilityId)
     */
    public EmissionsReport retrieveLatestTvEmissionReport(Integer year,
            String facilityId) throws DAOException {
        EmissionsReport ret = null;
        Connection conn = getConnection();
        StringBuffer statementSQL = new StringBuffer(
                loadSQL("EmissionsReportSQL.retrieveLatestTvEmissionReport"));
        statementSQL.append(" AND rer.report_year = " + year.toString());
        statementSQL.append(" AND ff.facility_id = " + facilityId);
        statementSQL.append(") rer WHERE r = 1");

        try {
            ResultSet rs = conn.createStatement().executeQuery(
                    statementSQL.toString());
            if (rs.next()) {
                ret = new EmissionsReport();
                ret.populate(rs);
            }
        } catch (SQLException e) {
            handleException(e, conn);
        } finally {
            handleClosing(conn);
        }
        return ret;
    }
    
    /**
     * @see EmissionsReportDAO#retrieveLatestSubmittedEmissionReport(Integer year, String facilityId)
     */
    public EmissionsReport retrieveLatestSubmittedEmissionReport(Integer year,
            String facilityId) throws DAOException {
        EmissionsReport ret = null;
        Connection conn = getConnection();
        StringBuffer statementSQL = new StringBuffer(
                loadSQL("EmissionsReportSQL.retrieveLatestSubmittedEmissionReport"));
        statementSQL.append(" AND rer.report_year = " + year.toString());
        statementSQL.append(" AND ff.facility_id = " + facilityId);
        statementSQL.append(") rer WHERE r = 1");

        try {
            ResultSet rs = conn.createStatement().executeQuery(
                    statementSQL.toString());
            if (rs.next()) {
                ret = new EmissionsReport();
                ret.populate(rs);
            }
        } catch (SQLException e) {
            handleException(e, conn);
        } finally {
            handleClosing(conn);
        }
        return ret;
    }

    /**
     * @see EmissionsReportDAO#retrieveEmissionsReports(int fpId)
     */
    public EmissionsReport[] retrieveEmissionsReports(int fpId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionReports", true);

        connHandler.setInteger(1, fpId);

        ArrayList<EmissionsReport> ret = connHandler
                .retrieveArray(EmissionsReport.class);

        return ret.toArray(new EmissionsReport[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveReportNote(int noteId)
     */
    public EmissionsReportNote retrieveReportNote(int noteId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportNote", true);

        connHandler.setInteger(1, noteId);

        return (EmissionsReportNote) connHandler
                .retrieve(EmissionsReportNote.class);
    }

    /**
     * @see EmissionsReportDAO#retrieveReportNotes(int emissionsRptId)
     */
    public EmissionsReportNote[] retrieveReportNotes(int emissionsRptId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportNotes", true);

        connHandler.setInteger(1, emissionsRptId);

        ArrayList<EmissionsReportNote> ret = connHandler
                .retrieveArray(EmissionsReportNote.class);

        return ret.toArray(new EmissionsReportNote[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveReportEU(int emuId)
     */
    public EmissionsReportEU retrieveReportEU(int emissionsRptId, int emuId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportEU", true);

        connHandler.setInteger(1, emissionsRptId);
        connHandler.setInteger(2, emuId);

        return (EmissionsReportEU) connHandler
                .retrieve(EmissionsReportEU.class);
    }

    /**
     * @see EmissionsReportDAO#retrieveReportEUs(int emissionsRptId)
     */
    public EmissionsReportEU[] retrieveReportEUs(int emissionsRptId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportEUs", true);

        connHandler.setInteger(1, emissionsRptId);

        ArrayList<EmissionsReportEU> ret = connHandler
                .retrieveArray(EmissionsReportEU.class);

        return ret.toArray(new EmissionsReportEU[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveEmissionsDocument(int documentId)
     */
    public EmissionsDocumentRef retrieveEmissionsDocument(int documentId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionsDocument", true);

        connHandler.setInteger(1, documentId);

        return (EmissionsDocumentRef) connHandler
                .retrieve(EmissionsDocumentRef.class);
    }

    /**
     * @see EmissionsReportDAO#retrieveEmissionsDocuments(int emissionsRptId)
     */
    public EmissionsDocumentRef[] retrieveEmissionsDocuments(
            int emissionsRptId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportAttachments", true);

        connHandler.setInteger(1, emissionsRptId);

        ArrayList<EmissionsDocumentRef> ret = connHandler
                .retrieveArray(EmissionsDocumentRef.class);

        return ret.toArray(new EmissionsDocumentRef[0]);
    }

    /**
     * @see EmissionsReportDAO#modifyReportEUGroup(EmissionsReportEUGroup)
     */
    public boolean modifyReportEUGroup(EmissionsReportEUGroup euGroup)
            throws DAOException {
        checkNull(euGroup);
        boolean ret = false;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.modifyReportEUGroup", false);

        connHandler.setString(1, euGroup.getReportEuGroupName());
        connHandler.setInteger(2, euGroup.getLastModified() + 1);
        connHandler.setInteger(3, euGroup.getReportEuGroupID());
        connHandler.setInteger(4, euGroup.getLastModified());

        connHandler.update();

        ret = true;

        return ret;
    }

    /**
     * @see EmissionsReportDAO#retrieveReportEUGroup(int)
     */
    public EmissionsReportEUGroup retrieveReportEUGroup(int reportEUGroupId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportEUGroup", true);

        connHandler.setInteger(1, reportEUGroupId);

        return (EmissionsReportEUGroup) connHandler
                .retrieve(EmissionsReportEUGroup.class);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveGroupEmissions(int)
     */
    public Emissions[] retrieveGroupEmissions(int periodId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveGroupEmissions", true);
  
        connHandler.setInteger(1, periodId);
        ArrayList<Emissions> ret = connHandler.retrieveArray(Emissions.class);
        return ret.toArray(new Emissions[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveReportEUGroup(int, String)
     */
    public EmissionsReportEUGroup retrieveReportEUGroup(int reportId,
            String reportEUGroupName) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportEUGroupByName", true);

        connHandler.setInteger(1, reportId);
        connHandler.setString(2, reportEUGroupName);

        return (EmissionsReportEUGroup) connHandler
                .retrieve(EmissionsReportEUGroup.class);
    }

    public EmissionsReportEUGroup[] retrieveReportEUGroupRef(int reportId)
    		throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportEUGroupByRef", true);

        connHandler.setInteger(1, reportId);       

        ArrayList<EmissionsReportEUGroup> ret = connHandler
        		.retrieveArray(EmissionsReportEUGroup.class);
        
        return ret.toArray(new EmissionsReportEUGroup[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveReportEUGroups(int)
     */
    public EmissionsReportEUGroup[] retrieveReportEUGroups(int emissionsReportId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportEUGroups", true);

        connHandler.setInteger(1, emissionsReportId);

        ArrayList<EmissionsReportEUGroup> ret = connHandler
                .retrieveArray(EmissionsReportEUGroup.class);

        return ret.toArray(new EmissionsReportEUGroup[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveReportEUGroups(int, int)
     */
    public EmissionsReportEUGroup[] retrieveReportEUGroups(
            int emissionsReportId, int CorrEpaEmuId) throws DAOException {

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveReportEUGroupsByCorrId", true);

        connHandler.setInteger(1, emissionsReportId);
        connHandler.setInteger(2, CorrEpaEmuId);

        ArrayList<EmissionsReportEUGroup> ret = connHandler
                .retrieveArray(EmissionsReportEUGroup.class);

        return ret.toArray(new EmissionsReportEUGroup[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveEmissionsRptInfos(String facilityId)
     */
    public EmissionsRptInfo[] retrieveEmissionsRptInfos(String facilityId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionsRptInfos", true);

        connHandler.setString(1, facilityId);

        ArrayList<EmissionsRptInfo> ret = connHandler
                .retrieveArray(EmissionsRptInfo.class);

        return ret.toArray(new EmissionsRptInfo[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveEmissionsRptInfo(String facilityId,
     *      Integer year)
     */
    public EmissionsRptInfo retrieveEmissionsRptInfo(String facilityId,
            Integer year) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionsRptInfo", true);

        connHandler.setString(1, facilityId);
        connHandler.setInteger(2, year);

        return (EmissionsRptInfo) connHandler.retrieve(EmissionsRptInfo.class);
    }


    /**
     * @see EmissionsReportDAO#createEmissionsRptInfo(String, EmissionsRptInfo)
     */
    public EmissionsRptInfo createEmissionsRptInfo(String facilityId,
            EmissionsRptInfo info) throws DAOException {
        checkNull(info);
        EmissionsRptInfo ret = info;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createEmissionsRptInfo", false);

        int i = 1;
        connHandler.setString(i++, facilityId);
        connHandler.setString(i++, info.getState());
        connHandler.setString(i++, AbstractDAO
           .translateBooleanToIndicator(info.getReportingEnabled()));
        connHandler.setString(i++, info.getComment());
        connHandler.setInteger(i++, info.getScEmissionsReportId());
        connHandler.setInteger(i++, info.getYear());
        connHandler.update();

        return ret;
    }

    /**
     * @see EmissionsReportDAO#modifyEmissionsRptInfo(String, EmissionsRptInfo)
     */
    public boolean modifyEmissionsRptInfo(String facilityId,
            EmissionsRptInfo info) throws DAOException {
        checkNull(info);
        boolean ret = false;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.modifyEmissionsRptInfo", false);

        int i = 1;
        connHandler.setString(i++, info.getState());
        connHandler.setInteger(i++, info.getScEmissionsReportId());
        connHandler.setString(i++, AbstractDAO
                .translateBooleanToIndicator(info.getReportingEnabled()));
        connHandler.setString(i++, info.getComment());
        connHandler.setInteger(i++, info.getLastModified() + 1);
        connHandler.setString(i++, facilityId);
		connHandler.setInteger(i++, null == info.getPreviousScEmissionsReportId()? 
				info.getScEmissionsReportId() : info.getPreviousScEmissionsReportId());
        connHandler.setInteger(i++, info.getLastModified());

        connHandler.update();

        ret = true;

        return ret;
    }
    
	/**
	 * @see EmissionsReportDAO#deleteEmissionsRptInfo(String, EmissionsRptInfo)
	 */
	public void deleteEmissionsRptInfo(String facilityId, Integer id)
			throws DAOException {
		checkNull(facilityId);
		checkNull(id);

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionsReportSQL.deleteEmissionsRptInfo", false);

		connHandler.setString(1, facilityId);
		connHandler.setInteger(2, id);
		connHandler.remove();

		return;
	}

    /**
     * @see EmissionsReportDAO#missingEmissionsRptInfo(SCEmissionsReport scEmissionsReport)
     */
    public FacilityRptInfo[] missingEmissionsRptInfo(SCEmissionsReport scEmissionsReport)
            throws DAOException {
    	
    	// If the boolean flag is set to 'Y', get all facilities that match the facility class (e.g. "TV") at any time during the period.
    	boolean useLastDayOfPeriod = true;
    	if (scEmissionsReport.getTreatPartialAsFullPeriodFlag() != null && scEmissionsReport.getTreatPartialAsFullPeriodFlag().equals("Y")) {
    		useLastDayOfPeriod = false;
    	}
    	ConnectionHandler connHandler = null;
    	if (useLastDayOfPeriod) {
    		connHandler = new ConnectionHandler(
                "EmissionsReportSQL.missingEmissionsRptInfo1", true);
    	} else {
    		connHandler = new ConnectionHandler(
                    "EmissionsReportSQL.missingEmissionsRptInfo2", true);
    	}
        int i = 1;
        
        String firstDayOfPeriod = scEmissionsReport.getFirstDayOfPeriod();
        String lastDayOfPeriod = scEmissionsReport.getLastDayOfPeriod();
        
        connHandler.setInteger(i++, scEmissionsReport.getId());
        connHandler.setTimestamp(i++, Timestamp.valueOf(lastDayOfPeriod));
        connHandler.setTimestamp(i++, Timestamp.valueOf(lastDayOfPeriod));
        connHandler.setTimestamp(i++, Timestamp.valueOf(lastDayOfPeriod));
        connHandler.setTimestamp(i++, Timestamp.valueOf(firstDayOfPeriod));
        connHandler.setTimestamp(i++, Timestamp.valueOf(lastDayOfPeriod));
        connHandler.setInteger(i++, scEmissionsReport.getId());
        connHandler.setInteger(i++, scEmissionsReport.getId());
        connHandler.setInteger(i++, scEmissionsReport.getId());
        connHandler.setInteger(i++, scEmissionsReport.getId());
        
        if (useLastDayOfPeriod) {
        	connHandler.setInteger(i++, scEmissionsReport.getId());
        	connHandler.setInteger(i++, scEmissionsReport.getId());
        } else {
        	connHandler.setInteger(i++, scEmissionsReport.getId());
        	connHandler.setTimestamp(i++, Timestamp.valueOf(firstDayOfPeriod));
        	connHandler.setTimestamp(i++, Timestamp.valueOf(firstDayOfPeriod));
        	connHandler.setTimestamp(i++, Timestamp.valueOf(lastDayOfPeriod));
        }
        ArrayList<FacilityRptInfo> ret = connHandler.retrieveArray(FacilityRptInfo.class);

        return ret.toArray(new FacilityRptInfo[0]);
    }
    
    /**
     * @see EmissionsReportDAO#turnOnReporting(SCEmissionsReport scEmissionsReport)
     */
    public EmissionsRptInfo[] turnOnReporting(SCEmissionsReport scEmissionsReport)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionsRptInfo2", true);
        connHandler.setInteger(1, scEmissionsReport.getId());
        ArrayList<EmissionsRptInfo> ret = connHandler
            .retrieveArray(EmissionsRptInfo.class);
        return ret.toArray(new EmissionsRptInfo[0]);
    }
    
    /**
     * @see EmissionsReportDAO#turnOnReportingNtv(Integer year)
     */
    /*
    public EmissionsRptInfo[] turnOnReportingNtv(int year)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionsRptNtvInfo2", true);
        connHandler.setInteger(1, year - 1);
        ArrayList<EmissionsRptInfo> ret = connHandler
            .retrieveArray(EmissionsRptInfo.class);
        return ret.toArray(new EmissionsRptInfo[0]);
    }
    */

    /**
     * @see EmissionsReportDAO#findUnusedEmissionPeriods()
     */
    public Integer[] findUnusedEmissionPeriods() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.findUnusedEmissionPeriods", true);

        ArrayList<? extends Object> ret = connHandler
                .retrieveJavaObjectArray(Integer.class);

        return ret.toArray(new Integer[0]);
    }

    /**
     * @see EmissionsReportDAO#deleteEUs(EmissionsReport)
     */
    public void deleteEUs(EmissionsReport report) throws DAOException {

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.deleteReportEUs", false);

        connHandler.setInteger(1, report.getEmissionsRptId());

        connHandler.remove();
        return;
    }
    
    public Integer[] searchFacilities(List<String> counties, String doLaaCd, String facilityId, int firstYear, int lastYear) throws DAOException {
        StringBuffer statementSQL;
        ConnectionHandler connHandler = new ConnectionHandler(true);
        statementSQL = new StringBuffer(loadSQL("EmissionsReportSQL.searchFacilities"));
        statementSQL.append(" AND (operating_status_cd <> 'sd' OR last_shutdown_date is null OR (last_shutdown_date > ? AND last_shutdown_date < ?))");
        if (facilityId != null && facilityId.trim().length() > 0) {
            statementSQL.append(" AND LOWER(facility_id) LIKE ");
            statementSQL.append("LOWER('");
            statementSQL.append(SQLizeString(facilityId.trim().replace("*", "%")));
            statementSQL.append("')");
        }
        if (counties != null && counties.size() != 0) {
            int cnt = 1;
            if(!(counties.size() == 1 && "all".equals(counties.get(0)))) {
                statementSQL.append(" AND (");
                for(String countyCd : counties) {
                    statementSQL.append(" ca.county_cd = '");
                    statementSQL.append(countyCd);
                    statementSQL.append("'");
                    if(cnt != counties.size()) {
                        statementSQL.append(" OR ");
                        cnt++;
                    }
                }
                statementSQL.append(" )");
            }
        }
        if (doLaaCd != null) {
            statementSQL.append(" AND ff.do_laa_cd = '");
            statementSQL.append(doLaaCd);
            statementSQL.append("'");
        }
        statementSQL.append(" ORDER BY ff.facility_id");
        connHandler.setSQLStringRaw(statementSQL.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.YEAR, firstYear);
        int i = 1;
        connHandler.setTimestamp(i++, new Timestamp(cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, lastYear + 1);
        connHandler.setTimestamp(i++, new Timestamp(cal.getTimeInMillis()));
        
        ArrayList<Integer> ret = connHandler.retrieveJavaObjectArray(Integer.class);

        return ret.toArray(new Integer[0]);
    }
    
    public EmissionsReportSearch[] searchEmissionsReports(EmissionsReportSearch searchObj) throws DAOException {

        checkNull(searchObj);

        if (searchObj.isUnlimitedResults()) {
            setDefaultSearchLimit(-1);
        }

        StringBuffer statementSQL;
        if(!searchObj.isStagingDBQuery()) {
            statementSQL = new StringBuffer(loadSQL("EmissionsReportSQL.searchEmissionsReport"));
        } else {
            // query or reports from staging and facility inventories from readonly.
            statementSQL = new StringBuffer(loadSQL("EmissionsReportSQL.searchEmissionsReportStaging1"));
//            String statementSQL2 = DAOFactory.loadSQL("EmissionsReportSQL.searchEmissionsReportStaging2");
//            statementSQL.append(" ");
//
//            // Need read only schema for the facility inventory union.
//            AbstractDAO dao = null;
//            dao = DAOFactory.getDAO("EmissionsReportDAO", CommonConst.READONLY_SCHEMA);
//            statementSQL.append(statementSQL2.replace("%Schema%", dao.getSchemaQualifer()));  
        }
        String specificPollutant = "";
       
        if (searchObj.getPollutantCd() != null && searchObj.getPollutantCd() != " ") {
            specificPollutant = replaceSchema(", %Schema%rp_report_pollutant_totals rrpt ");
        }

        StringBuffer whereClause = new StringBuffer(specificPollutant + "WHERE 1=1");

        if (searchObj.getFacilityName() != null && searchObj.getFacilityName().trim().length() > 0) {
            whereClause.append(" AND LOWER(ff.facility_nm) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(searchObj.getFacilityName().replace("*", "%")));
            whereClause.append("')");
        }
        
        if (searchObj.getFacilityId() != null && searchObj.getFacilityId().trim().length() > 0) {
            whereClause.append(" AND LOWER(ff.facility_id) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(formatFacilityId(searchObj.getFacilityId()).replace("*", "%")));
            whereClause.append("')");
        }

        if (searchObj.getDolaaCd() != null) {
            whereClause.append(" AND ff.do_laa_cd = '");
            whereClause.append(searchObj.getDolaaCd());
            whereClause.append("'");
        }
        
        String emissions_inventory_id = searchObj.getEmissionsInventoryId();
        if (emissions_inventory_id != null && emissions_inventory_id.trim().length() > 0) {
        	emissions_inventory_id = formatId("EI", "%07d", emissions_inventory_id);
        	whereClause.append(" AND LOWER(rer.ei_id) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(emissions_inventory_id.replace("*", "%")));
            whereClause.append("')");
        }
        
        if (searchObj.getReportingState() != null) {
            whereClause.append(" AND rer.rpt_received_status_cd = '");
            whereClause.append(searchObj.getReportingState());
            whereClause.append("'");
        }
        if (searchObj.getYear() != null) {
            whereClause.append(" AND rer.report_year = ");
            whereClause.append(searchObj.getYear());
        }

		if (searchObj.getPollutantCd() != null
				&& searchObj.getPollutantCd() != " ") {
			// Search in emissionTotals not emissionsReport
			whereClause
					.append(" AND rer.emissions_rpt_id = rrpt.emissions_rpt_id AND rrpt.total_emissions IS NOT NULL AND rrpt.pollutant_cd = '");
			whereClause.append(searchObj.getPollutantCd());
			whereClause.append("'");
			if (searchObj.getMinEmissions() != null
					&& searchObj.getMaxEmissions() != null
					&& searchObj.getMinEmissions().equals(
							searchObj.getMaxEmissions())) {
				whereClause
						.append(" AND CONVERT(DECIMAL(20,8) , REPLACE(rrpt.total_emissions, ',', '')) = ");
				whereClause.append(searchObj.getMinEmissions());
			} else {
				if (searchObj.getMinEmissions() != null) {
					whereClause
							.append(" AND CONVERT(DECIMAL(20,8) , REPLACE(rrpt.total_emissions, ',', '')) >= ");
					whereClause.append(searchObj.getMinEmissions());
				}
				if (searchObj.getMaxEmissions() != null) {
					whereClause
							.append(" AND CONVERT(DECIMAL(20,8) , REPLACE(rrpt.total_emissions, ',', '')) < ");
					whereClause.append(searchObj.getMaxEmissions());
				}
			}
		}
		
        if (searchObj.getEisStatusCd() != null) {
            whereClause.append(" AND rer.eis_status_cd = '");
            whereClause.append(searchObj.getEisStatusCd());
            whereClause.append("'");
        }

        if (searchObj.getBeginDt() != null) {
            whereClause.append(" AND rer.received_date >= ?");
        }

        if (searchObj.getEndDt() != null) {
            whereClause.append(" AND rer.received_date < ?");
        }
        
        if (searchObj.getCmpId() != null && searchObj.getCmpId().length() != 0) {
			whereClause.append(" AND ccm.cmp_id = '");
			whereClause.append(searchObj.getCmpId());
			whereClause.append("'");
		}

        if (searchObj.getContentTypeCd() != null && searchObj.getContentTypeCd().length() != 0) {
			whereClause.append(" AND sc.content_type_cd = '");
			whereClause.append(searchObj.getContentTypeCd());
			whereClause.append("'");
		}

        if (searchObj.getRegulatoryRequirementCd() != null && searchObj.getRegulatoryRequirementCd().length() != 0) {
			whereClause.append(" AND sc.regulatory_requirement_cd = '");
			whereClause.append(searchObj.getRegulatoryRequirementCd());
			whereClause.append("'");
		}
        
        // In Public website, show EI's in Approved Reporting State only.
    	if(CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
    		whereClause.append(" AND rer.rpt_received_status_cd = '");
        	whereClause.append(ReportReceivedStatusDef.DOLAA_APPROVED);
        	whereClause.append("'");
    	}

        StringBuffer sortBy = new StringBuffer(" ORDER BY ff.facility_id, rer.report_year DESC, rer.emissions_rpt_id DESC");

        statementSQL.append(whereClause.toString() + " " + sortBy.toString());

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        int i = 1;

        if (searchObj.getBeginDt() != null) {
            connHandler.setTimestamp(i++, searchObj.getBeginDt());
        }
        
        if (searchObj.getEndDt() != null) {
            connHandler.setTimestamp(i++, searchObj.getEndDt());
        }

        ArrayList<EmissionsReportSearch> ret = connHandler.retrieveArray(EmissionsReportSearch.class, defaultSearchLimit);
        
        for (EmissionsReportSearch r : ret) {
        	List<SCEmissionsReport> scs = 
        			retrieveAssociatedSCEmissionsReports(r.getEmissionsRptId());
        	for (SCEmissionsReport sc : scs) {
        		r.getRegulatoryRequirementCds().add(sc.getRegulatoryRequirementCd());
        		if (null == r.getContentTypeCd()) {
        			r.setContentTypeCd(sc.getContentTypeCd());
        		}
        	}
        }
        
        return ret.toArray(new EmissionsReportSearch[0]);
    }
    
    public EmissionsReportSearch[] searchEmissionsReportForScore(EmissionsReportSearch searchObj) throws DAOException {

        checkNull(searchObj);

        if (searchObj.isUnlimitedResults()) {
            setDefaultSearchLimit(-1);
        }

        StringBuffer statementSQL = new StringBuffer(loadSQL("EmissionsReportSQL.searchEmissionsReportForScore"));
        StringBuffer whereClause = new StringBuffer();
 
        if (searchObj.getDolaaCd() != null) {
            whereClause.append(" AND ff.do_laa_cd = '");
            whereClause.append(searchObj.getDolaaCd());
            whereClause.append("'");
        }
        
        whereClause.append(" ORDER BY ff.do_laa_cd, ff.facility_id");
        statementSQL.append(whereClause);

        ConnectionHandler connHandler = new ConnectionHandler(true);


        connHandler.setSQLStringRaw(statementSQL.toString());

        int i = 1;
        connHandler.setTimestamp(i++, searchObj.getBeginDt());
        connHandler.setTimestamp(i++, searchObj.getEndDt());

        ArrayList<EmissionsReportSearch> ret = connHandler.retrieveArray(EmissionsReportSearch.class, defaultSearchLimit);

        return ret.toArray(new EmissionsReportSearch[0]);
    }

    /**
     * @see EmissionsReportDAO#createFireRow(FireRow row)
     */
    public FireRow createFireRow(FireRow row) throws DAOException {
        checkNull(row);
        FireRow ret = row;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.createFireRow", false);

        int i = 1;
        
        connHandler.setString(i++, row.getFactorId());
        connHandler.setString(i++, row.getSccId());
        
		if (row.getPollutantCd() != null && row.getPollutantCd().trim().isEmpty()) {
			// don't insert empty Strings into DB
			row.setPollutantCd(null);
		}
        connHandler.setString(i++, row.getPollutantCd());
        
		if (row.getFactor() != null && row.getFactor().trim().isEmpty()) {
			// don't insert empty Strings into DB
			row.setFactor(null);
		}
        connHandler.setString(i++, row.getFactor());
        
		if (row.getFormula() != null && row.getFormula().trim().isEmpty()) {
			// don't insert empty Strings into DB
			row.setFormula(null);
		}
        String f = row.getFormula();
        if(f != null) f = f.trim();
        connHandler.setString(i++, f);
        
        connHandler.setString(i++, row.getUnit());
        connHandler.setString(i++, row.getMeasure());
        connHandler.setString(i++, row.getMaterial());
        connHandler.setString(i++, row.getAction());
        connHandler.setString(i++, row.getNotes());
        connHandler.setString(i++, row.getQuality());
        connHandler.setString(i++, row.getCas());

		if (row.getPollutant() != null && row.getPollutant().trim().isEmpty()) {
			// don't insert empty Strings into DB
			row.setPollutant(null);
		}
        connHandler.setString(i++, row.getPollutant());
        
		if (row.getPollutantID() != null && row.getPollutantID().trim().isEmpty()) {
			// don't insert empty Strings into DB
			row.setPollutantID(null);
		}
        connHandler.setString(i++, row.getPollutantID());
        connHandler.setString(i++, row.getOrigFactor());
        connHandler.setString(i++, row.getOrigFormula());
        connHandler.setString(i++, row.getOrigUnit());
        connHandler.setString(i++, row.getOrigMeasure());
        connHandler.setString(i++, row.getOrigMaterial());
        connHandler.setString(i++, row.getOrigAction());
        connHandler.setString(i++, row.getAp42Section());
        connHandler.setString(i++, row.getRefDesc());
        connHandler.setString(i++, row.getOrigNotes());
        connHandler.setString(i++, row.getOrigQuality());
        connHandler.setString(i++, row.getOrigin());
        connHandler.setInteger(i++, row.getCreated());
        connHandler.setInteger(i++, row.getDeprecated());

        connHandler.update();

        ret.setLastModified(1);

        return ret;
    }

    /**
     * @see EmissionsReportDAO#modifyFireRow(FireRow row)
     */
    public boolean modifyFireRow(FireRow row) throws DAOException {
        checkNull(row);
        boolean ret = false;

        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.modifyFireRow", false);

        int i = 1;
        
        connHandler.setString(i++, row.getSccId());

        if (row.getPollutantCd() != null && row.getPollutantCd().trim().isEmpty()) {
			// don't insert empty Strings into DB
			row.setPollutantCd(null);
		}
        connHandler.setString(i++, row.getPollutantCd());
        
        if (row.getFactor() != null && row.getFactor().trim().isEmpty()) {
			// don't insert empty Strings into DB
			row.setFactor(null);
		}
        connHandler.setString(i++, row.getFactor());
        
        if (row.getFormula() != null && row.getFormula().trim().isEmpty()) {
			// don't insert empty Strings into DB
			row.setFormula(null);
		}
        String f = row.getFormula();
        if(f != null) f = f.trim();
        connHandler.setString(i++, f);
        
        connHandler.setString(i++, row.getUnit());
        connHandler.setString(i++, row.getMeasure());
        connHandler.setString(i++, row.getMaterial());
        connHandler.setString(i++, row.getAction());
        connHandler.setString(i++, row.getNotes());
        connHandler.setString(i++, row.getQuality());
        connHandler.setString(i++, row.getCas());
        connHandler.setString(i++, row.getPollutant());
        connHandler.setString(i++, row.getPollutantID());
        connHandler.setString(i++, row.getOrigFactor());
        connHandler.setString(i++, row.getOrigFormula());
        connHandler.setString(i++, row.getOrigUnit());
        connHandler.setString(i++, row.getOrigMeasure());
        connHandler.setString(i++, row.getOrigMaterial());
        connHandler.setString(i++, row.getOrigAction());
        connHandler.setString(i++, row.getAp42Section());
        connHandler.setString(i++, row.getRefDesc());
        connHandler.setString(i++, row.getOrigNotes());
        connHandler.setString(i++, row.getOrigQuality());
        connHandler.setString(i++, row.getOrigin());
        connHandler.setInteger(i++, row.getCreated());
        connHandler.setInteger(i++, row.getDeprecated());
        connHandler.setString(i++, row.getFactorId());
        connHandler.update();

        ret = true;

        return ret;
    }

    /**
     * @see EmissionsReportDAO#retrieveFireRows(EmissionsReportPeriod period)
     */
    public FireRow[] retrieveFireRows(Integer year, EmissionsReportPeriod period)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveFireRowsByScc", true);

        if (null == period.getSccId()) {
            return new FireRow[0];
        }
        connHandler.setString(1, period.getSccId());
        connHandler.setString(2, period.getSccId());

        ArrayList<FireRow> candidates = connHandler.retrieveArray(FireRow.class);
        ArrayList<FireRow> specificRows = new ArrayList<FireRow>(candidates.size());
        ArrayList<FireRow> keepGeneric = null;
        ArrayList<FireRow> keepDeprecated = new ArrayList<FireRow>();
        ListIterator<FireRow> it = candidates.listIterator();
        try {
            while(it.hasNext()) {
                FireRow r = it.next();
                if(!r.isActive(year)) {
                    // Determine whether used in period and if in table definition (meaning 
                    //   required in FER, EIS, ES as seen by having an order number) keep it.
                    for(Emissions e : period.getEmissions().values()) {
                        if(r.getFactorId().equals(e.getFireRef())) {
                            // keep it--but separate
                            keepDeprecated.add(r);
                            break;
                        }
                    }
                    it.remove();
                    continue;
                }
                if(r.getSccId() != null) {
                    specificRows.add(r);
                    it.remove();
                }
            }
            // Now all specific rows are kept and all generic rows are remaining in candidates.
            it = candidates.listIterator();
            // Keep generic emissions fire rows if this SCC has same material and action
            // and there is not an SCC specific fire row for the same pollutant.
            keepGeneric = new ArrayList<FireRow>(candidates.size());
            while(it.hasNext()) {
                FireRow r = it.next();  // next generic
                if(r.getSccId() != null) continue;
                String mat = r.getMaterial();
                String act = r.getAction();
                String pCd = r.getPollutantCd();
                // look at all specific ones
                ListIterator<FireRow> itSpec = specificRows.listIterator();
                boolean keep = false;
                while(itSpec.hasNext()) {
                    FireRow specRow = itSpec.next();
                    if(!mat.equals(specRow.getMaterial()) || !act.equals(specRow.getAction())) continue;
                    keep = true;  // we will keep it if the pollutants are different
                    if(pCd.equals(specRow.getPollutantCd())) {
                        keep = false;
                        break;
                    }
                }
                if(keep) {
                    keepGeneric.add(r);
                }
            }
        } catch(NoSuchElementException e) {
            logger.error("retrieveFireRows() failed", e);
            throw e;
        }
        specificRows.addAll(keepGeneric);
        specificRows.addAll(keepDeprecated);

        return specificRows.toArray(new FireRow[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveFireRows(String SccId, String MaterialCd)
     */
    public FireRow[] retrieveFireRows(String sccId, String materialCd)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveFireRowsBySccByMaterial", true);

        connHandler.setString(1, sccId);
        connHandler.setString(2, materialCd);

        ArrayList<FireRow> ret = connHandler.retrieveArray(FireRow.class);

        return ret.toArray(new FireRow[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveCompliance(Integer year, String countyCd)
     */
    public boolean retrieveCompliance(Integer year, String countyCd)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.inCompliance", true);

        connHandler.setInteger(1, year);
        connHandler.setString(2, countyCd);

        String rtn = (String)connHandler.retrieveJavaObject(String.class);
        if (null == rtn) {
            logger.error("Failed to determine attainment for year " +
                    year + " and countyCd " + countyCd  +  ", false assumed.");
            return false;  // Assume non-attainment if not found.
        }
        return translateIndicatorToBoolean(rtn);
    }
    
    /**
     * @see EmissionsReportDAO#retrievePartOf()
     */
    public PollutantPair[] retrievePartOf()
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrievePollutantPartOf", true);
        ArrayList<PollutantPair> ret =
            connHandler.retrieveArray(PollutantPair.class);
        return ret.toArray(new PollutantPair[0]);
    }
    
    public PollutantPair retrievePollutantParent(String pollutantCode) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrievePollutantParent", true);
        connHandler.setString(1, pollutantCode);
        PollutantPair ret = (PollutantPair) connHandler.retrieve(PollutantPair.class);
        return ret;
    }
    
    /**
     * @see EmissionsReportDAO#retrieveFireRow(String sccId, String materialCd, int year)
     */
    public FireRow retrieveFireRow(String sccId, String materialCd, int year) throws DAOException {
        /*
         * Used to get a single row from the table to ensure that action and material units
         * match for a given SCC and Material.
         */
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveFireRow2", true);

        connHandler.setString(1, sccId);
        connHandler.setString(2, materialCd);
        connHandler.setInteger(3, year);
        connHandler.setInteger(4, year);
        return (FireRow)connHandler.retrieve(FireRow.class);       
    }
    
    /**
     * @see EmissionsReportDAO#retrieveFireRow(String sccId, String materialCd, pollutantCd, String factor, String formula, int year)
     */
    public FireRow[] retrieveFireRow(String sccId, String materialCd,
            String pollutantCd, String factor, String formula, String measure,
            String action, int year) throws DAOException {
        /* 
         * Used to get the matching fire row that differs only in non-critical fields
         */
        StringBuffer statementSQL = new StringBuffer(
                loadSQL("EmissionsReportSQL.retrieveFireRow3"));
        if(sccId != null && sccId.length() > 0) {
            statementSQL.append(" WHERE rff.scc_id = '" + sccId + "'");
        } else {
            statementSQL.append(" WHERE rff.scc_id is null");
        }
        statementSQL.append(" AND rff.material_cd = '" + materialCd + "'");
        statementSQL.append(" AND rff.material_unit_cd = '" + measure + "'");
        statementSQL.append(" AND rff.action_cd = '" + action + "'");

        if(pollutantCd != null && pollutantCd.length() > 0) {
            statementSQL.append(" AND rff.pollutant_cd = '" + pollutantCd + "'");
        } else {
            statementSQL.append(" AND rff.pollutant_cd is null");
        }
        if(factor != null && factor.length() > 0) {
            statementSQL.append(" AND CAST(rff.factor AS FLOAT) = " + factor);
        } else {
            statementSQL.append(" AND rff.factor is null");
        }
        if(formula != null && formula.length() > 0) {
            statementSQL.append(" AND LTRIM(RTRIM(rff.formula)) = '" + formula.trim() + "'");
        } else {
            statementSQL.append(" AND rff.formula is null");
        }
        // Note we want those deprecated in year because that is a temporary condition
        statementSQL.append(" AND (rff.deprecated_year is null OR rff.deprecated_year >= " + year + ")");
        statementSQL.append(" AND (rff.created is null OR rff.created <= " + year + ")");
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());
        ArrayList<FireRow> rtn = connHandler.retrieveArray(FireRow.class);
        return rtn.toArray(new FireRow[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveFireRow(String sccId, String materialCd, pollutantCd, String measure,
            String action, int year)
     */
    public FireRow[] retrieveFireRow(String sccId, String materialCd,
            String pollutantCd, String measure,
            String action, int year) throws DAOException {
        /* 
         * Used to get the matching fire row that differs only in non-critical fields factor and formula
         */
        StringBuffer statementSQL = new StringBuffer(
                loadSQL("EmissionsReportSQL.retrieveFireRow3"));
        if(sccId != null && sccId.length() > 0) {
            statementSQL.append(" WHERE rff.scc_id = '" + sccId + "'");
        } else {
            statementSQL.append(" WHERE rff.scc_id is null");
        }
        statementSQL.append(" AND rff.material_cd = '" + materialCd + "'");
        statementSQL.append(" AND rff.material_unit_cd = '" + measure + "'");
        statementSQL.append(" AND rff.action_cd = '" + action + "'");

        if(pollutantCd != null && pollutantCd.length() > 0) {
            statementSQL.append(" AND rff.pollutant_cd = '" + pollutantCd + "'");
        } else {
            statementSQL.append(" AND rff.pollutant_cd is null");
        }
        
        // Note we want those deprecated in year because that is a temporary condition
        statementSQL.append(" AND (rff.deprecated_year is null OR rff.deprecated_year >= " + year + ")");
        statementSQL.append(" AND (rff.created is null OR rff.created <= " + year + ")");
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());
        ArrayList<FireRow> rtn = connHandler.retrieveArray(FireRow.class);
        return rtn.toArray(new FireRow[0]);
    }
    

    /**
     * @see EmissionsReportDAO#retrieveFireRow(String fireId)
     */
    public FireRow retrieveFireRow(String fireId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveFireRow", true);

        connHandler.setString(1, fireId);
        
        return (FireRow)connHandler.retrieve(FireRow.class);       
    }
    
    /**
     * @see EmissionsReportDAO#removeInvalidFireRows()
     */
    public void removeInvalidFireRows(int year) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.removeInvalidFireRows", false);
        connHandler.setInteger(1, year);
        connHandler.setInteger(2, year);
        connHandler.remove();        
    }
    
    /**
     * @see EmissionsReportDAO#deprecateFire()
     */
    public void deprecateFire(int date) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.deprecateFire", false);
        
        connHandler.setInteger(1, date);

        connHandler.remove();       
    }
    
    /**
     * @see EmissionsReportDAO#deprecateFire()
     */
    public void deprecateFire(int date, String sccId, List<String> materialCds, List<String> pollutantCds) throws DAOException {
        
    	
    	// convert the String array into a comma-delimited string list
        String tempMaterials = "";
        int i = 0;
        for (String s : materialCds) {
        	tempMaterials = tempMaterials + "'" + SQLizeString(s) + "'";
        	i++;
        	if (i < materialCds.size()) {
        		tempMaterials = tempMaterials + ",";
        	}
        }
        
        String tempPollutants = "";
        i = 0;
        for (String s : pollutantCds) {
        	tempPollutants = tempPollutants + "'" + SQLizeString(s) + "'";
        	i++;
        	if (i < pollutantCds.size()) {
        		tempPollutants = tempPollutants + ",";
        	}
        }
		
		Integer intValue = new Integer(date);
	    StringBuffer statementSQL = new StringBuffer(loadSQL("EmissionsReportSQL.updateFireRows"));
	    statementSQL.append(" SET deprecated_year = '" + SQLizeString(intValue.toString()) + "'");
	    statementSQL.append(" WHERE deprecated_year IS NULL");
	    statementSQL.append(" AND scc_id = '" + SQLizeString(sccId) + "'");
		statementSQL.append(" AND material_cd IN (" + tempMaterials + ")");
		statementSQL.append(" AND pollutant_cd IN (" + tempPollutants + ")");

		ConnectionHandler connHandler = new ConnectionHandler(true);
		
		//logger.info("sql = " + statementSQL.toString());
	    connHandler.setSQLStringRaw(statementSQL.toString());

        connHandler.remove();       
    }

    /**
     * @see EmissionsReportDAO#activeFireRows()
     */
    public DbInteger activeFireRows() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.activeFireRows", false);
        
        return (DbInteger)connHandler.retrieve(DbInteger.class);        
    }
    
    /**
     * @see EmissionsReportDAO#retrieveFireRows(String SccId, String MaterialCd)
     */
    public FireRow[] deprecatedFireRows(int year)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.deprecatedFireRows", true);

        connHandler.setInteger(1, year);
        ArrayList<FireRow> ret = connHandler.retrieveArray(FireRow.class);

        return ret.toArray(new FireRow[0]);
    }

    /**
     * @see EmissionsReportDAO#materialForScc()
     */
    public DbInteger materialForScc(String sccId, Integer year) {
        DbInteger rtn = null;
        try {
            ConnectionHandler connHandler = new ConnectionHandler(
                    "EmissionsReportSQL.materialForScc", true);
            connHandler.setString(1, sccId);
            connHandler.setInteger(2, year);
            connHandler.setInteger(3, year + 1);
            rtn =  (DbInteger)connHandler.retrieve(DbInteger.class);
        } catch (DAOException daoe) {
            logger.error("Failed on materialForScc(" + sccId + ", "  + year + ")--ignored", daoe);
        }
        return rtn;
    }

    /**
     * @see EmissionsReportDAO#retrieveDefaultStackParms(String fireId)
     */
    public DefaultStackParms retrieveDefaultStackParms(String sccId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveDefaultStackParms", true);

        connHandler.setString(1, sccId);

        return (DefaultStackParms)connHandler.retrieve(DefaultStackParms.class);       
    }

    /**
     * @see EmissionsReportDAO#facilitiesWithEisReports()
     */
    public MultiEstablishment[] facilitiesWithEisReports(int year) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.facilitiesWithEisReports", true);
        connHandler.setInteger(1, year);
        ArrayList<MultiEstablishment> ret = connHandler
        .retrieveArray(MultiEstablishment.class);

        return ret.toArray(new MultiEstablishment[0]);
    }

    /**
     * @see EmissionsReportDAO#activeTvFacilities()
     */
    public MultiEstablishment[] activeTvFacilities(int year) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.activeTvFacilities", true);
        connHandler.setInteger(1, year);
        ArrayList<MultiEstablishment> ret = connHandler
        .retrieveArray(MultiEstablishment.class);

        return ret.toArray(new MultiEstablishment[0]);
    }

    /**
     * @see EmissionsReportDAO#locateUsingOldEuName()
     */
    public DbInteger[] locateUsingOldEuName(String facilityId, int fpId, String oldName) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.locateUsingOldEuName", true);
        connHandler.setString(1, facilityId);
        connHandler.setInteger(2, fpId);
        connHandler.setString(3, oldName);

        ArrayList<DbInteger> ret = connHandler
        .retrieveArray(DbInteger.class);

        return ret.toArray(new DbInteger[0]);
    }

    /**
     * @see EmissionsReportDAO#retrieveReportInvoicePair()
     */
    public void retrieveReportInvoicePair(EmissionsReport r, EmissionsReport r2, Invoice i, Invoice i2, int rptId) throws DAOException {

        PreparedStatement ps = null;
        Connection conn = null;

        try {
            conn = getReadOnlyConnection();
            StringBuffer projSql = new StringBuffer(loadSQL("EmissionsReportSQL.retrieveReportInvoicePair"));
            ps = conn.prepareStatement(replaceSchema(projSql.toString()));
            ps.setInt(1, rptId);
            ResultSet res = ps.executeQuery();
            boolean hasNext = res.next(); 
            if(hasNext){
                // check for first emissions inventory
                AbstractDAO.getInteger(res, "emissions_rpt_id");
                if(!res.wasNull()) {
                    r.setEmissionsRptId(AbstractDAO.getInteger(res, "emissions_rpt_id"));
                    r.setRptReceivedStatusCd(res.getString("rpt_received_status_cd"));
                }
                AbstractDAO.getInteger(res, "invoice_id");
                // check to see if an invoice is included
                if(!res.wasNull()) {
                    i.setInvoiceId(AbstractDAO.getInteger(res, "invoice_id"));
                    i.setInvoiceStateCd(res.getString("invoice_state_cd"));
                    i.setFacilityId(res.getString("facility_id"));
                }
            }
            hasNext = res.next();
            if(hasNext){
                // check for a second emissions inventory
                AbstractDAO.getInteger(res, "emissions_rpt_id");
                if(!res.wasNull()) {
                    r2.setEmissionsRptId(AbstractDAO.getInteger(res, "emissions_rpt_id"));
                    r2.setRptReceivedStatusCd(res.getString("rpt_received_status_cd"));
                }
                AbstractDAO.getInteger(res, "invoice_id");
                // check to see if an invoice is included
                if(!res.wasNull()) {
                    i2.setInvoiceId(AbstractDAO.getInteger(res, "invoice_id"));
                    i2.setInvoiceStateCd(res.getString("invoice_state_cd"));
                    i2.setFacilityId(res.getString("facility_id"));
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
    }
    
    /**
     * @see EmissionsReportDAO#retrieveFacilityPermitInfo()
     */
    public FacilityPermitInfo[] retrieveFacilityPermitInfo(String facilityId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveFacilityPermitInfo", true);
        connHandler.setString(1, facilityId);
        ArrayList<FacilityPermitInfo> ret = connHandler.retrieveArray(FacilityPermitInfo.class);

        return ret.toArray(new FacilityPermitInfo[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retrieveFacilityPermitInfo()
     */
    public FacilityAppInfo[] retrieveFacilityAppInfo(String facilityId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveFacilityAppInfo", true);
        connHandler.setString(1, facilityId);
        ArrayList<FacilityAppInfo> ret = connHandler.retrieveArray(FacilityAppInfo.class);

        return ret.toArray(new FacilityAppInfo[0]);
    }
    
    /**
     * @see EmissionsReportDAO#retriveFacilityPermitAppInfo()
     */
    public IntegerPair[] retriveFacilityPermitAppInfo(String facilityId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retriveFacilityPermitAppInfo", true);
        connHandler.setString(1, facilityId);
        ArrayList<IntegerPair> ret = connHandler.retrieveArray(IntegerPair.class);

        return ret.toArray(new IntegerPair[0]);
    }
    
    /**
  	 * @see us.oh.state.epa.stars2.database.dao.EmissionsReportDAO#removeNote(java.lang.Integer, java.lang.Integer)
  	 */
  	public final boolean removeNote(Integer reportId, Integer noteId)
  			throws DAOException {
  		boolean ret = false;
  		
  		checkNull(reportId);
  		checkNull(noteId);
  		
  		ConnectionHandler connHandler = new ConnectionHandler(
  				"EmissionsReportSQL.removeNote", false);
  		if (connHandler != null) {
  			connHandler.setInteger(1, reportId);
  			connHandler.setInteger(2, noteId);
  			ret = connHandler.remove();
  		}
  		
  		return ret;
  	}    
  	
  	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.EmissionsReportDAO#
	 * setEmissionsReportValidatedFlag(java.lang.Integer, boolean)
	 */
	public final void setEmissionsReportValidatedFlag(Integer reportId,
			boolean validated) throws DAOException {
		checkNull(reportId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionsReportSQL.setEmissionsReportValidatedFlag", false);
		connHandler.setString(1, AbstractDAO.translateBooleanToIndicator(validated));
		connHandler.setInteger(2, reportId);
		connHandler.remove();
	}
	
	@Override
    public List<MissingFIREFactor> retrieveMissingFactors(Integer year, String facilityClass)
            throws DAOException {
    	Connection conn = null;
		PreparedStatement pStmt = null;
		List<MissingFIREFactor> ret = new ArrayList<MissingFIREFactor>();

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.YEAR, year + 1);
		
		// wildcard empty facility class
		if(Utility.isNullOrEmpty(facilityClass)) {
			facilityClass = "%";
		}

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("EmissionsReportSQL.retrieveMissingFactors"));

			pStmt.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));
			pStmt.setString(2, facilityClass);

			ResultSet rs = pStmt.executeQuery();
			
			while(rs.next()) {
				EmissionProcess ep = new EmissionProcess();
				ep.populate(rs);
				
				EmissionUnit eu = new EmissionUnit();
				eu.populate(rs);
				
				Facility facility = new Facility();
				facility.setMaxVersionFlag(false);
				facility.populate(rs);
				
				MissingFIREFactor factor = new MissingFIREFactor();
				factor.setEp(ep);
				factor.setEu(eu);
				factor.setFacility(facility);
				
				ret.add(factor);
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}
	
	 public boolean checkFireRowByScc(String sccId) throws DAOException {
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "EmissionsReportSQL.retrieveFireRow4", true);
	        FireRow fireRow = null;

	        connHandler.setString(1, sccId);
	        
	        fireRow = (FireRow)connHandler.retrieve(FireRow.class);
	        
	        return (null != fireRow ? true : false);
	    }
	 
	 /**
	     * @see EmissionsReportDAO#generateEmissionsInventoryXML(int year, String pointXmlFileName)
	     */
	    public boolean generateEmissionsInventoryXML(UsEpaEisReport report) throws DAOException {

	    	Connection conn = null;
	    	PreparedStatement pStmt = null;
	    	
	    	SQLXML xmlVal = null;   	
	    	SAXSource saxSource = null;
	    	
	    	Integer id = nextSequenceVal("S_EisXml_Doc_Id");
	    	int i = 1;
	    	
	    	boolean ret = false;

	    	try {
	    		
	    		conn = getReadOnlyConnection();
	    		xmlVal = conn.createSQLXML();
	    		Transformer aTransformer = TransformerFactory.newInstance().newTransformer();
	    		Result dest = new StreamResult(new File(report.getPointXmlFileName()));
	    		
	    		pStmt = conn.prepareStatement(replacePositionParamsInXMLQuery(
						"EmissionsInventoryXMLSQL.generateEmissionsInventoryXML",
						report));
	    		
	    		pStmt.setString(i++, "ID" + id.intValue());
	    		pStmt.setString(i++, report.getAuthorName());
	    		pStmt.setString(i++, report.getAqdContact());
	    		pStmt.setString(i++, report.getEISSubmissionTypeDesc(report.getSubmissionType()));
	    		pStmt.setString(i++, report.getEisLogin());
	    		pStmt.setInt(i++, report.getYear());
	    		pStmt.setString(i++,  report.getSubmittalComment());
	    		pStmt.setInt(i++, report.getYear());
	    		pStmt.setInt(i++, report.getYear());
	    		pStmt.setString(i++, report.getYear() + "-01-01" );
	    		pStmt.setString(i++, report.getYear() + "-12-31");
	    		pStmt.setInt(i++, report.getYear());
	    		pStmt.setInt(i++, report.getYear());
	    		pStmt.setInt(i++, report.getYear());
	    		
	    		for(String s : report.getFacilityClassList()) {
	    			pStmt.setString(i++, s);
	    		}
	    		
	    		for(String s : report.getFacilityTypeList()) {
	    			pStmt.setString(i++, s);
	    		}
	    		
	    		ResultSet  rs = pStmt.executeQuery();
	    		
	    		while(rs.next()) {
	    			xmlVal = rs.getSQLXML(1);
	    			saxSource = xmlVal.getSource(SAXSource.class);
	    			aTransformer.transform(saxSource, dest);
	    		}
	    		
	    		ret = true;
	    		
	    	} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
	    	
	    	return ret;
	    }
	    
		 /**
	     * @see EmissionsReportDAO#generateFacilityInventoryXML(int year, String facilityXmlFileName)
	     */
	    public boolean generateFacilityInventoryXML(UsEpaEisReport report) throws DAOException {

	    	Connection conn = null;
	    	PreparedStatement pStmt = null;
	    	
	    	SQLXML xmlVal = null ;   	
	    	SAXSource saxSource = null;
	    	
	    	Integer id = nextSequenceVal("S_EisXml_Doc_Id");
	    	int i = 1;
	    	
	    	boolean ret = false;
	    	
	    	try {
	    		
	    		conn = getReadOnlyConnection();
	    		xmlVal = conn.createSQLXML();
	    		Transformer aTransformer = TransformerFactory.newInstance().newTransformer();
	    		Result dest = new StreamResult(new File(report.getFacilityXmlFileName()));
	    		
	    		pStmt = conn.prepareStatement(replacePositionParamsInXMLQuery(
					"FacilityInventoryXMLSQL.generateFacilityInventoryXML",
					report));
	    		
	    		pStmt.setString(i++, "ID" + id.intValue());
	    		pStmt.setString(i++, report.getAuthorName());
	    		pStmt.setString(i++, report.getAqdContact());
	    		pStmt.setString(i++, report.getEISSubmissionTypeDesc(report.getSubmissionType()));
	    		pStmt.setString(i++, report.getEisLogin());
	    		pStmt.setInt(i++, report.getYear());
	    		pStmt.setString(i++,  report.getSubmittalComment());
	    		pStmt.setInt(i++, report.getYear());
	    		
	    		for(String s : report.getFacilityClassList()) {
	    			pStmt.setString(i++, s);
	    		}
	    		
	    		for(String s : report.getFacilityTypeList()) {
	    			pStmt.setString(i++, s);
	    		}
	    		
	    		ResultSet  rs = pStmt.executeQuery();
	    		
	    		while(rs.next()) {
	    			xmlVal = rs.getSQLXML(1);
	    			saxSource = xmlVal.getSource(SAXSource.class);
	    			aTransformer.transform(saxSource, dest);
	    		}
	    		
	    		ret = true;
	    		
	    	} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
	    	
	    	return ret;
	    }
	    
	    public boolean updateTotalEmissions(EmissionsReport report) throws DAOException {
	    	ConnectionHandler conn = new ConnectionHandler("EmissionsReportSQL.updateTotalEmissions", false);
	    	
	    	int i = 1;
	    	conn.setFloat(i++, report.getTotalEmissions());
	    	conn.setFloat(i++,  report.getTotalReportedEmissions());
	    	conn.setInteger(i++,  report.getEmissionsRptId());
	    	
	    	conn.update();
	    	
	    	return true;
	    	
	    }
	    
	    @Override
	    public List<SCEmissionsReport> retrieveAssociatedSCEmissionsReports(
				Integer rptId) throws DAOException {
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "EmissionsReportSQL.retrieveAssociatedSCEmissionsReports", true);

	        connHandler.setInteger(1, rptId);

	        ArrayList<SCEmissionsReport> ret = connHandler
	                .retrieveArray(SCEmissionsReport.class);
	        
	        return ret;
	    }

		@Override
		public List<SCEmissionsReport> retrieveAssociatedSCEmissionsReports(
				EmissionsReport report) throws DAOException {
			checkNull(report);
			return retrieveAssociatedSCEmissionsReports(report.getEmissionsRptId());
		}

	@Override
	public void disassociateSCEmissionsReports(EmissionsReport report) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler("EmissionsReportSQL.disassociateSCEmissionsReports",
				false);

		connHandler.setInteger(1, report.getEmissionsRptId());

		connHandler.remove();
		return;
	}

	@Override
	public void associateSCEmissionsReport(EmissionsReport report, SCEmissionsReport sc) throws DAOException {
		associateSCEmissionsReports(report, sc.getId());
	}

	private String replacePositionParamsInXMLQuery(String xmlQuery,
			UsEpaEisReport report) {

		String sql = loadSQL(xmlQuery);
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		map.put("@pos1", report.getFacilityClassList().size()); // facility class
		map.put("@pos2", report.getFacilityTypeList().size()); //facility type
		Iterator<String> iter = map.keySet().iterator();
		
		while (iter.hasNext()) {
			String key = (String)iter.next();
			int n = map.get(key);
			sql = sql.replace(key, Utility.nCopyAndJoin(n, "?", ','));
		}
		
		return sql;
	}

	@Override
	public void associateSCEmissionsReports(EmissionsReport report,
			Integer scEmissionsReportId) throws DAOException {
        checkNull(report, scEmissionsReportId);
        
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.associateSCEmissionsReport", false);

        connHandler.setInteger(1,report.getEmissionsRptId());
        connHandler.setInteger(2,scEmissionsReportId);
        connHandler.update();
	}
	
	/**
	 * @see EmissionsReportDAO#emissionsInventoriesForFacilityAndServiceCatalog()
	 */
	public DbInteger emissionsInventoriesForFacilityAndServiceCatalog(
			String facilityId, Integer serviceCatalogId) {
		DbInteger rtn = null;
		try {
			ConnectionHandler connHandler = new ConnectionHandler(
					"EmissionsReportSQL.emissionsInventoriesForFacilityAndServiceCatalog",
					true);
			connHandler.setString(1, facilityId);
			connHandler.setInteger(2, serviceCatalogId);
			rtn = (DbInteger) connHandler.retrieve(DbInteger.class);
		} catch (DAOException daoe) {
			logger.error(
					"Failed on emissionsInventoriesForFacilityAndServiceCatalog("
							+ facilityId + ", " + serviceCatalogId
							+ ")--ignored", daoe);
		}
		return rtn;
	}
	
	@Override
	public EmissionsRptInfo[] retrieveEmissionsRptInfos2(String facilityId, Integer emissionsReportId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EmissionsReportSQL.retrieveEmissionsRptInfos2", true);

        checkNull(facilityId);
        checkNull(emissionsReportId);
        
        connHandler.setString(1, facilityId);
        connHandler.setInteger(2, emissionsReportId);

        ArrayList<EmissionsRptInfo> ret = connHandler
                .retrieveArray(EmissionsRptInfo.class);

        return ret.toArray(new EmissionsRptInfo[0]);
    }
	
	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrieveSccMaterials(java.lang.String)
	 */
	public final SimpleDef[] retrieveSccMaterials(String sccId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionsReportSQL.retrieveSccMaterials", true);
		connHandler.setString(1, sccId);
		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}
	
	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrieveSccPollutants(java.lang.String)
	 */
	public final SimpleDef[] retrieveSccPollutants(String sccId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionsReportSQL.retrieveSccPollutants", true);
		connHandler.setString(1, sccId);
		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}
	
	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrieveSccPollutants(java.lang.String)
	 */
	public final SimpleDef[] retrieveSccPollutants(String sccId, List<String> materialCds)
			throws DAOException {
		
		ArrayList<SimpleDef> ret = null;
		
		// convert the String array into a comma-delimited string list
        String temp = "";
        int i = 0;
        for (String s : materialCds) {
        	temp = temp + "'" + SQLizeString(s) + "'";
        	i++;
        	if (i < materialCds.size()) {
        		temp = temp + ",";
        	}
        }
        //logger.info("temp = "  + temp);
		
		Connection conn = getConnection();
	    StringBuffer statementSQL = new StringBuffer(
	            loadSQL("EmissionsReportSQL.retrieveSccPollutantsForMaterials"));
	    statementSQL.append(" AND cpd.pollutant_cd IN (SELECT pollutant_cd FROM dbo.RP_FIRE_FACTOR WHERE scc_id = '" + SQLizeString(sccId) + "'");
		statementSQL.append(" AND rff.material_cd IN (" + temp + "))");
		statementSQL.append("ORDER BY cpd.pollutant_dsc");
		
		
		ConnectionHandler connHandler = new ConnectionHandler(true);
		
		//logger.info("sql = " + statementSQL.toString());
	    connHandler.setSQLStringRaw(statementSQL.toString());
	    ret = connHandler.retrieveArray(SimpleDef.class);
	    	
		return ret.toArray(new SimpleDef[0]);
	}

	@Override
	public EmissionsDocumentRef retrieveReportDocumentByTradeSecrectDocId(Integer tradeSecretDocId)
			throws DAOException {

		checkNull(tradeSecretDocId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionsReportSQL.retrieveReportDocumentByTradeSecrectDocId", true);

		connHandler.setInteger(1, tradeSecretDocId);

		return (EmissionsDocumentRef) connHandler.retrieve(EmissionsDocumentRef.class);
	}
	
	@Override
	public ArrayList<Integer> retrieveValidEmissionsReportIds(String facilityId, Integer rptYear, String contentTypeCd) throws DAOException {
		
		checkNull(facilityId);
		checkNull(rptYear);
		checkNull(contentTypeCd);

		StringBuffer statementSQL = new StringBuffer(
	            loadSQL("EmissionsReportSQL.retrieveValidEmissionsReportIds"));
		statementSQL.append(" AND fac.facility_id = '" + facilityId + "'");
		statementSQL.append(" AND sc.REPORTING_YR = " + rptYear);
		statementSQL.append(" AND sc.CONTENT_TYPE_CD = '" + contentTypeCd + "'");
        
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());
        ArrayList<Integer> ret = connHandler.retrieveJavaObjectArray(Integer.class);
		
        return ret;
	}
	
	@Override
	public void updatePriorEIsToInvalidAfterCsvImport(String facilityId, Integer rptYear, String contentTypeCd, Integer currentEmissionsRptId) throws DAOException {
		
		checkNull(facilityId);
		checkNull(currentEmissionsRptId);
		checkNull(rptYear);
		checkNull(contentTypeCd);
		
	    StringBuffer statementSQL = new StringBuffer(
	            loadSQL("EmissionsReportSQL.updatePriorEIsToInvalidAfterCsvImport"));
		
	    statementSQL.append(" AND fac.facility_id = '" + facilityId + "'");
		statementSQL.append(" AND sc.REPORTING_YR = " + rptYear);
		statementSQL.append(" AND sc.CONTENT_TYPE_CD = '" + contentTypeCd + "'");
		statementSQL.append(" AND rpt.EMISSIONS_RPT_ID <> " + currentEmissionsRptId);
		
        
		ConnectionHandler connHandler = new ConnectionHandler(true);
	    connHandler.setSQLStringRaw(statementSQL.toString());
	    connHandler.update();
	}
	
	@Override
	public Map<String, Integer> retrieveEnabledEmissionRptsForYearAndContentType(Integer year, String contentType)
			throws DAOException {
		Map<String, Integer> enabledReports = new HashMap<String, Integer>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		checkNull(year);
		checkNull(contentType);

		try {
			conn = getReadOnlyConnection();

			pStmt = conn
					.prepareStatement(loadSQL("EmissionsReportSQL.retrieveEnabledEmissionRptsForYearAndContentType"));
			pStmt.setInt(1, year);
			pStmt.setString(2, contentType);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				do {
					String facility_id = rs.getString("facility_id");
					Integer enabledRptsCount = AbstractDAO.getInteger(rs, "reports_count");

					if (facility_id != null) {
						enabledReports.put(facility_id, enabledRptsCount);
					}
				} while (rs.next());
			}
			rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return enabledReports;
	}

	@Override
	public final void setActiveEmissionsReportsValidatedFlag(Integer fpId, boolean validated) throws DAOException{
		checkNull(fpId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionsReportSQL.setActiveEmissionsReportsValidatedFlag", false);
		connHandler.setString(1,AbstractDAO.translateBooleanToIndicator(validated));
		connHandler.setInteger(2, fpId);
		connHandler.setString(3, ReportEisStatusDef.NOT_FILED);
		connHandler.updateNoCheck();
	}


	@Override
	public boolean deleteAssociatedInvoice(EmissionsReport emissionsReport) throws DAOException {
  		boolean ret = false;
  		checkNull(emissionsReport);
  		
  		ConnectionHandler connHandler = new ConnectionHandler(
  				"EmissionsReportSQL.removeAssociatedInvoice", false);
  		if (connHandler != null) {
  			connHandler.setInteger(1, emissionsReport.getEmissionsRptId());
  			ret = connHandler.remove();
  		}
  		
  		return ret;

	}

	
	@Override
	public boolean deleteEmissionsRptInvoiceNotes(EmissionsReport emissionsReport) throws DAOException {
		boolean ret = false;
  		checkNull(emissionsReport);
  		
  		ConnectionHandler connHandler = new ConnectionHandler(
  				"EmissionsReportSQL.deleteEmissionsRptInvoiceNotes", false);
  		if (connHandler != null) {
  			connHandler.setInteger(1, emissionsReport.getEmissionsRptId());
  			ret = connHandler.remove();
  		}
  		
  		return ret;

		
	}


	@Override
	public boolean deleteEmissionsRptInvoiceDetails(EmissionsReport emissionsReport) throws DAOException {
		boolean ret = false;
  		checkNull(emissionsReport);
  		
  		ConnectionHandler connHandler = new ConnectionHandler(
  				"EmissionsReportSQL.deleteEmissionsRptInvoiceDetails", false);
  		if (connHandler != null) {
  			connHandler.setInteger(1, emissionsReport.getEmissionsRptId());
  			ret = connHandler.remove();
  		}
  		
  		return ret;


		
	}

	
	@Override
	public boolean deleteReferences(String facilityId) throws DAOException {
		checkNull(facilityId);
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler("EmissionsReportSQL.deleteReferences", false);
		if (connHandler != null) {
	        connHandler.setString(1, facilityId);
	        connHandler.update();
			ret = true;
		}
		return ret;
	}
}

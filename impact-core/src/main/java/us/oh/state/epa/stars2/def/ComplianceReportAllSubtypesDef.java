package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.webcommon.reports.ReportBaseCommon;

@SuppressWarnings("serial")
public class ComplianceReportAllSubtypesDef extends SimpleDef {
    private String reportTypeCd;
    private boolean tvAttestationReq;
    private boolean bulkEnabled;
    
    private static final String defName = "ComplianceReportAllSubtypesDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ComplianceReportSQL.retrieveComplianceReportAllTypes",
                    ComplianceReportAllSubtypesDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public void populate(ResultSet rs) {

        super.populate(rs);

        try {
            setReportTypeCd(rs.getString("report_type_cd"));
            setTvAttestationReq(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("tv_attestation_req")));
            setBulkEnabled(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("bulk_enabled")));
        } catch (SQLException sqle) {
            logger.warn("Optional field: " + sqle.getMessage());
        }

    }
     
    public static boolean needTvAttestation(String cd, Logger logger) {
        ComplianceReportAllSubtypesDef bd = (ComplianceReportAllSubtypesDef)getData().getItems().getItem(cd);
        if(bd == null) {
            logger.error("Attestation value not found for type cd " + cd);
            return false;
        }
        return bd.tvAttestationReq;
    }

    public static boolean isBulkEnabled(String cd, Logger logger) {
        ComplianceReportAllSubtypesDef bd = 
        		(ComplianceReportAllSubtypesDef)getData().getItems().getItem(cd);
        if(bd == null) {
            logger.error("Value not found for type cd " + cd);
            return false;
        }
        return bd.isBulkEnabled();
    }

    public String getReportTypeCd() {
        return reportTypeCd;
    }

    public void setReportTypeCd(String reportTypeCd) {
        this.reportTypeCd = reportTypeCd;
    }

    public boolean isTvAttestationReq() {
        return tvAttestationReq;
    }

    public void setTvAttestationReq(boolean tvAttestationReq) {
        this.tvAttestationReq = tvAttestationReq;
    }

	public boolean isBulkEnabled() {
		return bulkEnabled;
	}

	public void setBulkEnabled(boolean bulkEnabled) {
		this.bulkEnabled = bulkEnabled;
	}
    
}

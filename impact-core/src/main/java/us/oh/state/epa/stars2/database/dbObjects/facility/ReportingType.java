package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * @author S. Wooster
 * 
 */
public class ReportingType extends BaseDB {
    private String emissionRptCd;
    private String emissionRptDsc;
    private String emissionRptSubcat;

    public ReportingType() {
    }

    /**
     * @return
     */
    public final String getEmissionRptCd() {
        return emissionRptCd;
    }

    /**
     * @param emissionRptCd
     */
    public final void setEmissionRptCd(String emissionRptCd) {
        this.emissionRptCd = emissionRptCd;
    }

    /**
     * @return
     */
    public final String getEmissionRptDsc() {
        return emissionRptDsc;
    }

    /**
     * @param emissionRptDsc
     */
    public final void setEmissionRptDsc(String emissionRptDsc) {
        this.emissionRptDsc = emissionRptDsc;
    }

    /**
     * @return
     */
    public final String getEmissionRptSubcat() {
        return emissionRptSubcat;
    }

    /**
     * @param emissionRptDsc
     */
    public final void setEmissionRptSubcat(String emissionRptSubcat) {
        this.emissionRptSubcat = emissionRptSubcat;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setEmissionRptCd(rs.getString("emissions_rpt_cd"));
            setEmissionRptDsc(rs.getString("emissions_rpt_dsc"));
            setEmissionRptSubcat(rs.getString("emissions_rpt_subcat"));

        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}

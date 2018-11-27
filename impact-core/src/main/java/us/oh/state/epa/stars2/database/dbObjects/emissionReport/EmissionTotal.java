package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/*  Meaning of emissions:
 *  On database:
 *   fugitive\total         !null                    null
 *      !null         stack = total-fugitive       stack = null
 *       null         stack = total               stack = null
 *       
 *    On Web Page:
 *    if  stack=null   then total=fugitive
 *    if fugitive=null then total=stack
 */

public class EmissionTotal extends BaseDB implements Comparable<EmissionTotal>, Serializable{
    private Integer emissionsRptId;
    private String pollutantCd;
    private String totalEmissions;  // always in TONs
    
    // NOT IN DATABASE
    private boolean ferPollutant;  // used for NTV reporting
    private boolean esPollutant;  // used for NTV reporting
    // Note:  initialize order if not yet known (when read from database).
    private int order = 100000;  // pollutant order--from report templates

    public EmissionTotal() {
        super();
//        StackTraceElement[] stk = Thread.currentThread().getStackTrace();
//        logger.error("Reached new EmissionTotal");
//        for(StackTraceElement e : stk) {
//            logger.error(e.getClassName() + " " + e.getMethodName()  + " " + e.getLineNumber());
//        }
    }

    public EmissionTotal(EmissionTotal old) {
        super(old);

        if (old != null) {
            setEmissionsRptId(old.getEmissionsRptId());
            setPollutantCd(old.getPollutantCd());
            setTotalEmissions(old.getTotalEmissions());
        }
    }
    
    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public final int compareTo(EmissionTotal compObj) {
        /* 
         * Compares this object with the specified object for order.
         * Returns a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         */
         return compObj.order - this.order;
    }
   
    public boolean equals(Object o) {
        return order == ((EmissionTotal)o).order;
    }
    
    public final void populate(ResultSet rs) {
        try {
            setEmissionsRptId(AbstractDAO
                    .getInteger(rs, "emissions_rpt_id"));
            setPollutantCd(rs.getString("pollutant_cd"));
            setTotalEmissions(rs.getString("total_emissions"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    public Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
    }

    public String getPollutantCd() {
        return pollutantCd;
    }

    public void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((emissionsRptId == null) ? 0 : emissionsRptId.hashCode());
        result = PRIME * result + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
        return result;
    }


    public boolean isEsPollutant() {
        return esPollutant;
    }

    public void setEsPollutant(boolean esPollutant) {
        this.esPollutant = esPollutant;
    }

    public boolean isFerPollutant() {
        return ferPollutant;
    }

    public void setFerPollutant(boolean ferPollutant) {
        this.ferPollutant = ferPollutant;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTotalEmissions() {
        return totalEmissions;
    }

    public void setTotalEmissions(String totalEmissions) {
        this.totalEmissions = totalEmissions;
    }
}

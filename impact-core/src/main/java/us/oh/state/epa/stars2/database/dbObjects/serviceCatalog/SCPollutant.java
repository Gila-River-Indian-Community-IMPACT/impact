package us.oh.state.epa.stars2.database.dbObjects.serviceCatalog;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class SCPollutant extends BaseDB {
	
	private static final long serialVersionUID = -2052254831129244305L;

	private Integer scReportId;
    private String pollutantCd;
    private String pollutantDsc;
    private Integer displayOrder;
    private boolean chargeable;
    private Float thresholdQa;
    private boolean deprecated;
    private boolean billedBasedOnPermitted;

    public SCPollutant() {
        super();
    }

    public SCPollutant(SCPollutant old) {
        super(old);

        if (old != null) {
            setSCReportId(old.scReportId);
            setPollutantCd(old.pollutantCd);
            setPollutantDsc(old.pollutantDsc);
            setDisplayOrder(old.displayOrder);
            setChargeable(old.chargeable);
            setThresholdQa(old.getThresholdQa());
            setDeprecated(old.deprecated);
            setBilledBasedOnPermitted(old.billedBasedOnPermitted);
        }
    }

    public final boolean isChargeable() {
        return chargeable;
    }

    public final void setChargeable(boolean chargeable) {
        this.chargeable = chargeable;
    }

    public final Integer getDisplayOrder() {
        return displayOrder;
    }

    public final void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public final String getPollutantCd() {
        return pollutantCd;
    }

    public final void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

    public final Integer getSCReportId() {
        return scReportId;
    }

    public final void setSCReportId(Integer scReportId) {
        this.scReportId = scReportId;
    }

    public final boolean isDeprecated() {
        return deprecated;
    }

    public final void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public final void populate(ResultSet rs) throws SQLException {
        try {
            setSCReportId(AbstractDAO.getInteger(rs, "sc_emissions_report_id"));
            setPollutantCd(rs.getString("pollutant_cd"));
            setPollutantDsc(rs.getString("pollutant_dsc"));
            setDisplayOrder(AbstractDAO.getInteger(rs, "display_order"));
            setChargeable(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("chargable")));
            setThresholdQa(AbstractDAO.getFloat(rs, "threshold_qa"));
            setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("deprecated")));
            setBilledBasedOnPermitted(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("billed_on_permitted")));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    @Override
    public final int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SCPollutant other = (SCPollutant) obj;
        if (!pollutantCd.equals(other.pollutantCd))
                return false;
        return true;
    }

    public final String getPollutantDsc() {
        return pollutantDsc;
    }

    public final void setPollutantDsc(String pollutantDsc) {
        this.pollutantDsc = pollutantDsc;
    }

    public Float getThresholdQa() {
        return thresholdQa;
    }

    public void setThresholdQa(Float thresholdQa) {
        this.thresholdQa = thresholdQa;
    }
    
    public final boolean isBilledBasedOnPermitted() {
        return billedBasedOnPermitted;
    }

    public final void setBilledBasedOnPermitted(boolean billedBasedOnPermitted) {
        this.billedBasedOnPermitted = billedBasedOnPermitted;
    }
}

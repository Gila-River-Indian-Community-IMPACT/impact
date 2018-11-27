package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;

public class DefaultStackParms extends BaseDB{

    private String sccId; 
    private Float avgStackHeight;
    private Float avgStackDiameter;
    private Float avgExitGasTemp;
    private Float avgExitGasVel;
    private Float calcFlowRate;
    private boolean fugitive;

    public DefaultStackParms() {
        super();
    }

    public final void populate(ResultSet rs)  {
        try {
            setSccId(rs.getString("scc_id"));
            setAvgStackHeight(AbstractDAO.getFloat(rs, "avg_stack_height"));
            setAvgStackDiameter(AbstractDAO.getFloat(rs, "avg_stack_diameter"));
            setAvgExitGasTemp(AbstractDAO.getFloat(rs, "avg_exit_gas_temp"));
            setAvgExitGasVel(AbstractDAO.getFloat(rs, "avg_exit_gas_vel"));
            setCalcFlowRate(AbstractDAO.getFloat(rs, "calc_flow_rate"));
            setFugitive(AbstractDAO.translateIndicatorToBoolean(rs.getString("fugitive")));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((avgExitGasTemp == null) ? 0 : avgExitGasTemp.hashCode());
        result = PRIME * result + ((avgExitGasVel == null) ? 0 : avgExitGasVel.hashCode());
        result = PRIME * result + ((avgStackDiameter == null) ? 0 : avgStackDiameter.hashCode());
        result = PRIME * result + ((avgStackHeight == null) ? 0 : avgStackHeight.hashCode());
        result = PRIME * result + ((calcFlowRate == null) ? 0 : calcFlowRate.hashCode());
        result = PRIME * result + (fugitive ? 1231 : 1237);
        result = PRIME * result + ((sccId == null) ? 0 : sccId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DefaultStackParms other = (DefaultStackParms) obj;
        if (avgExitGasTemp == null) {
            if (other.avgExitGasTemp != null)
                return false;
        } else if (!avgExitGasTemp.equals(other.avgExitGasTemp))
            return false;
        if (avgExitGasVel == null) {
            if (other.avgExitGasVel != null)
                return false;
        } else if (!avgExitGasVel.equals(other.avgExitGasVel))
            return false;
        if (avgStackDiameter == null) {
            if (other.avgStackDiameter != null)
                return false;
        } else if (!avgStackDiameter.equals(other.avgStackDiameter))
            return false;
        if (avgStackHeight == null) {
            if (other.avgStackHeight != null)
                return false;
        } else if (!avgStackHeight.equals(other.avgStackHeight))
            return false;
        if (calcFlowRate == null) {
            if (other.calcFlowRate != null)
                return false;
        } else if (!calcFlowRate.equals(other.calcFlowRate))
            return false;
        if (fugitive != other.fugitive)
            return false;
        if (sccId == null) {
            if (other.sccId != null)
                return false;
        } else if (!sccId.equals(other.sccId))
            return false;
        return true;
    }

    public Float getAvgExitGasTemp() {
        return avgExitGasTemp;
    }

    public void setAvgExitGasTemp(Float avgExitGasTemp) {
        this.avgExitGasTemp = avgExitGasTemp;
    }

    public Float getAvgExitGasVel() {
        return avgExitGasVel;
    }

    public void setAvgExitGasVel(Float avgExitGasVel) {
        this.avgExitGasVel = avgExitGasVel;
    }

    public Float getAvgStackDiameter() {
        return avgStackDiameter;
    }

    public void setAvgStackDiameter(Float avgStackDiameter) {
        this.avgStackDiameter = avgStackDiameter;
    }

    public Float getAvgStackHeight() {
        return avgStackHeight;
    }

    public void setAvgStackHeight(Float avgStackHeight) {
        this.avgStackHeight = avgStackHeight;
    }

    public Float getCalcFlowRate() {
        return calcFlowRate;
    }

    public void setCalcFlowRate(Float calcFlowRate) {
        this.calcFlowRate = calcFlowRate;
    }

    public boolean isFugitive() {
        return fugitive;
    }

    public void setFugitive(boolean fugitive) {
        this.fugitive = fugitive;
    }

    public String getSccId() {
        return sccId;
    }

    public void setSccId(String sccId) {
        this.sccId = sccId;
    }


}

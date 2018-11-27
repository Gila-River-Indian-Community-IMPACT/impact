package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.PollutantDef;

public class EgressPointCem extends BaseDB {
    private Integer fpNodeId;
    private Integer cemId;
    private String cemDsc;
    private boolean h2sFlag;
    private boolean so2Flag;
    private boolean noxFlag;
    private boolean coFlag;
    private boolean thcFlag;
    private boolean hclFlag;
    private boolean hflFlag;
    private boolean o1Flag;
    private boolean trsFlag;
    private boolean co2Flag;
    private boolean flowFlag;
    private boolean opacityFlag;
    private boolean pmFlag;

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setCemId(AbstractDAO.getInteger(rs, "cem_id"));
            setFpNodeId(AbstractDAO.getInteger(rs, "fpnode_id"));
            setCemDsc(rs.getString("cem_dsc"));
            setH2sFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("h2s_flag")));
            setSo2Flag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("so2_flag")));
            setNoxFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("nox_flag")));
            setCoFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("co_flag")));
            setThcFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("thc_flag")));
            setHclFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("hcl_flag")));
            setHflFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("hfl_flag")));
            setO1Flag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("o_flag")));
            setTrsFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("trs_flag")));
            setCo2Flag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("co2_flag")));
            setFlowFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("flow_flag")));
            setOpacityFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("opacity_flag")));
            setPmFlag(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("pm_flag")));
            setLastModified(AbstractDAO.getInteger(rs, "egressPointCem_lm"));

        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getCemDsc() {
        return cemDsc;
    }

    public final void setCemDsc(String cemDsc) {
        this.cemDsc = cemDsc;
    }

    public final boolean isCo2Flag() {
        return co2Flag;
    }

    public final void setCo2Flag(boolean co2Flag) {
        this.co2Flag = co2Flag;
    }

    public final boolean isCoFlag() {
        return coFlag;
    }

    public final void setCoFlag(boolean coFlag) {
        this.coFlag = coFlag;
    }

    public final boolean isFlowFlag() {
        return flowFlag;
    }

    public final void setFlowFlag(boolean flowFlag) {
        this.flowFlag = flowFlag;
    }

    public final boolean isH2sFlag() {
        return h2sFlag;
    }

    public final void setH2sFlag(boolean flag) {
        h2sFlag = flag;
    }

    public final boolean isHclFlag() {
        return hclFlag;
    }

    public final void setHclFlag(boolean hclFlag) {
        this.hclFlag = hclFlag;
    }

    public final boolean isHflFlag() {
        return hflFlag;
    }

    public final void setHflFlag(boolean hflFlag) {
        this.hflFlag = hflFlag;
    }

    public final boolean isNoxFlag() {
        return noxFlag;
    }

    public final void setNoxFlag(boolean noxFlag) {
        this.noxFlag = noxFlag;
    }

    public final boolean isO1Flag() {
        return o1Flag;
    }

    public final void setO1Flag(boolean flag) {
        o1Flag = flag;
    }

    public final boolean isOpacityFlag() {
        return opacityFlag;
    }

    public final void setOpacityFlag(boolean opacityFlag) {
        this.opacityFlag = opacityFlag;
    }

    public final boolean isSo2Flag() {
        return so2Flag;
    }

    public final void setSo2Flag(boolean so2Flag) {
        this.so2Flag = so2Flag;
    }

    public final boolean isThcFlag() {
        return thcFlag;
    }

    public final void setThcFlag(boolean thcFlag) {
        this.thcFlag = thcFlag;
    }

    public final boolean isTrsFlag() {
        return trsFlag;
    }

    public final void setTrsFlag(boolean trsFlag) {
        this.trsFlag = trsFlag;
    }

    public final Integer getFpNodeId() {
        return fpNodeId;
    }

    public final void setFpNodeId(Integer fpNodeId) {
        this.fpNodeId = fpNodeId;
    }

    public final Integer getCemId() {
        return cemId;
    }

    public final void setCemId(Integer cemId) {
        this.cemId = cemId;
    }

    public final boolean isPmFlag() {
        return pmFlag;
    }

    public final void setPmFlag(boolean pmFlag) {
        this.pmFlag = pmFlag;
    }
    
    @Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((cemDsc == null) ? 0 : cemDsc.hashCode());
		result = PRIME * result + ((cemId == null) ? 0 : cemId.hashCode());
		result = PRIME * result + (co2Flag ? 1231 : 1237);
		result = PRIME * result + (coFlag ? 1231 : 1237);
		result = PRIME * result + (flowFlag ? 1231 : 1237);
		result = PRIME * result + ((fpNodeId == null) ? 0 : fpNodeId.hashCode());
		result = PRIME * result + (h2sFlag ? 1231 : 1237);
		result = PRIME * result + (hclFlag ? 1231 : 1237);
		result = PRIME * result + (hflFlag ? 1231 : 1237);
		result = PRIME * result + (noxFlag ? 1231 : 1237);
		result = PRIME * result + (o1Flag ? 1231 : 1237);
		result = PRIME * result + (opacityFlag ? 1231 : 1237);
		result = PRIME * result + (pmFlag ? 1231 : 1237);
		result = PRIME * result + (so2Flag ? 1231 : 1237);
		result = PRIME * result + (thcFlag ? 1231 : 1237);
		result = PRIME * result + (trsFlag ? 1231 : 1237);
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
		final EgressPointCem other = (EgressPointCem) obj;
		if (cemDsc == null) {
			if (other.cemDsc != null)
				return false;
		} else if (!cemDsc.equals(other.cemDsc))
			return false;
		if (cemId == null) {
			if (other.cemId != null)
				return false;
		} else if (!cemId.equals(other.cemId))
			return false;
		if (co2Flag != other.co2Flag)
			return false;
		if (coFlag != other.coFlag)
			return false;
		if (flowFlag != other.flowFlag)
			return false;
		if (fpNodeId == null) {
			if (other.fpNodeId != null)
				return false;
		} else if (!fpNodeId.equals(other.fpNodeId))
			return false;
		if (h2sFlag != other.h2sFlag)
			return false;
		if (hclFlag != other.hclFlag)
			return false;
		if (hflFlag != other.hflFlag)
			return false;
		if (noxFlag != other.noxFlag)
			return false;
		if (o1Flag != other.o1Flag)
			return false;
		if (opacityFlag != other.opacityFlag)
			return false;
		if (pmFlag != other.pmFlag)
			return false;
		if (so2Flag != other.so2Flag)
			return false;
		if (thcFlag != other.thcFlag)
			return false;
		if (trsFlag != other.trsFlag)
			return false;
		return true;
	}
	
	public boolean isPollutantMonitored(String pollutantCd) {
		boolean ret = false;

		if (pollutantCd.equalsIgnoreCase(PollutantDef.H2S_CD)) {
			ret = isH2sFlag();
		} else if (pollutantCd.equalsIgnoreCase(PollutantDef.SO2_CD)) {
			ret = isSo2Flag();
		} else if (pollutantCd.equalsIgnoreCase(PollutantDef.NOX_CD)) {
			ret = isNoxFlag();
		} else if (pollutantCd.equalsIgnoreCase(PollutantDef.CO_CD)) {
			ret = isCoFlag();
		} else if (pollutantCd.equalsIgnoreCase(PollutantDef.THC_CD)) {
			ret = isThcFlag();
		} else if (pollutantCd.equalsIgnoreCase(PollutantDef.HCL_CD)) {
			ret = isHclFlag();
		} else if (pollutantCd.equalsIgnoreCase(PollutantDef.CO2_CD)) {
			ret = isCo2Flag();
		}

		return ret;
	}
}

package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.PollutantDef;

/**
 * @author
 * 
 */
public class PollutantsControlled extends BaseDB {
    private Integer fpNodeId;
    private String pollutantCd;
    private String designContEff;
    private String operContEff;
    private String captureEff;
    private String totCaptureEff;

    public PollutantsControlled() {
        pollutantCd = "";
        designContEff =null;
        operContEff = null;
        captureEff = null;
    }

    /**
     * @return
     */
    public final Integer getFpNodeId() {
        return fpNodeId;
    }

    /**
     * @param fpNodeId
     */
    public final void setFpNodeId(Integer fpNodeId) {
        this.fpNodeId = fpNodeId;
    }

    /**
     * @return
     */
    public final String getPollutantCd() {
        return pollutantCd;
    }

    /**
     * @param PollutantCd
     */
    public final void setPollutantCd(String pollutantCd) {
        checkDirty("cepa", pollutantCd, PollutantDef.getData().getItems()
                .getItemDesc(this.pollutantCd), PollutantDef.getData()
                .getItems().getItemDesc(pollutantCd));
        this.pollutantCd = pollutantCd;
    }

    /**
     * @return
     */
    public final String getDesignContEff() {
        return designContEff;
    }

    /**
     * @param
     */
    public final void setDesignContEff(String designContEff) {
        Float v = null;
        if(designContEff != null && designContEff.length() != 0) {
            v = new Float(designContEff);
        }
        checkRangeValues(v, new Float(0.0), new Float(100.0),
                "designContEff", "Design Control Efficiency");
        this.designContEff = designContEff;
    }

    /**
     * @return
     */
    public final String getOperContEff() {
        return operContEff;
    }

    /**
     * @param
     */
    public final void setOperContEff(String operContEff) {
        Float v = null;
        if(operContEff != null && operContEff.length() != 0) {
            v = new Float(operContEff);
        }
        checkRangeValues(v, new Float(0.0), new Float(100.0),
                "operContEff", "Operating Control Efficiency");
        this.operContEff = operContEff;
    }

    /**
     * @return
     */
    public final String getCaptureEff() {
        return captureEff;
    }
    
    /**
     * @return boolean
     */
    public final boolean isCaptureEffZero() {
        boolean rtn = false;
        if(captureEff != null && captureEff.length() > 0) {
            if(new Double(captureEff) == 0d) {
                rtn = true;
            }
        }
        return rtn;
    }
    
    /**
     * @return boolean
     */
    public final boolean isOperContEffZero() {
        boolean rtn = false;
        if(operContEff != null && operContEff.length() > 0) {
            if(new Double(operContEff) == 0d) {
                rtn = true;
            }
        }
        return rtn;
    }

    /**
     * @param
     */
    public final void setCaptureEff(String captureEff) {
        Float v = null;
        if(captureEff != null && captureEff.length() != 0) {
            v = new Float(captureEff);
        }
        checkRangeValues(v, new Float(0.0), new Float(100.0),
                "captureEff", "Capture Efficiency");
        this.captureEff = captureEff;
    }

    /**
     * @return
     */
    public final String getTotCaptureEff() {
        // total Capture will be calculated.
        Double temp = new Double(100.00);
        totCaptureEff = null;
        if (operContEff != null && operContEff.length() > 0 && captureEff != null && captureEff.length() > 0) {
            Double tempCapt = new Double(captureEff) / temp;
            Double tempOper = new Double(operContEff) / temp;
            Double res = new Double(tempCapt * tempOper) * temp;
            if (res.doubleValue() == 0d) {
            	totCaptureEff = "0";
            } else {
            	DecimalFormat decFormat = new DecimalFormat("##0.#####");
            	totCaptureEff = decFormat.format(res);
            }
        }
        return totCaptureEff;
    }

    /**
     * @param
     */
    public final void setTotCaptureEff(String totCaptureEff) {
        this.totCaptureEff = totCaptureEff;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            setPollutantCd(rs.getString("pollutant_cd"));
            setDesignContEff(rs.getString("design_control_eff"));
            setOperContEff(rs.getString("operating_control_eff"));
            setCaptureEff(rs.getString("capture_eff"));
            setLastModified(AbstractDAO.getInteger(rs, "cePollControlled_lm"));

        } catch (SQLException sqle) {
            logger.error("Required field error");
        } finally {
            newObject = false;
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((captureEff == null) ? 0 : captureEff.hashCode());
        result = PRIME * result
                + ((designContEff == null) ? 0 : designContEff.hashCode());
        result = PRIME * result
                + ((fpNodeId == null) ? 0 : fpNodeId.hashCode());
        result = PRIME * result
                + ((operContEff == null) ? 0 : operContEff.hashCode());
        result = PRIME * result
                + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
        result = PRIME * result
                + ((totCaptureEff == null) ? 0 : totCaptureEff.hashCode());
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
        final PollutantsControlled other = (PollutantsControlled) obj;
        if (captureEff == null) {
            if (other.captureEff != null)
                return false;
        } else if (!captureEff.equals(other.captureEff))
            return false;
        if (designContEff == null) {
            if (other.designContEff != null)
                return false;
        } else if (!designContEff.equals(other.designContEff))
            return false;
        if (fpNodeId == null) {
            if (other.fpNodeId != null)
                return false;
        } else if (!fpNodeId.equals(other.fpNodeId))
            return false;
        if (operContEff == null) {
            if (other.operContEff != null)
                return false;
        } else if (!operContEff.equals(other.operContEff))
            return false;
        if (pollutantCd == null) {
            if (other.pollutantCd != null)
                return false;
        } else if (!pollutantCd.equals(other.pollutantCd))
            return false;
        if (totCaptureEff == null) {
            if (other.totCaptureEff != null)
                return false;
        } else if (!totCaptureEff.equals(other.totCaptureEff))
            return false;
        return true;
    }
}

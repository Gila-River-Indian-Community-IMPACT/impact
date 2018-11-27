package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;

public class EUFee extends BaseDB {

    private Integer _euFeeId;
    private Fee _fee;
    private String _adjustmentCd;
    private Integer _feeCategoryId;
    private Double _adjustedAmount;
    private List<Fee> fees;

    public EUFee() {

        super();
        setAdjustmentCd("N");
        setFee(new Fee());

        requiredField(_adjustmentCd, "adjustmentCd");
        requiredField(_fee, "fee");
        requiredField(_feeCategoryId, "feeCategoryId");
        setDirty(false);
    }

    public EUFee(EUFee old) {
        super(old);
        if (old != null) {
            setEUFeeId(old.getEUFeeId());
            setAdjustedAmount(old.getAdjustedAmount());
            setAdjustmentCd(old.getAdjustmentCd());
            setFeeCategoryId(old.getFeeCategoryId());
            setFee(old.getFee());
            setLastModified(getLastModified());
            setDirty(old.isDirty());
        }
    }

    public void populate(ResultSet rs) throws SQLException {

        try {
            setEUFeeId(AbstractDAO.getInteger(rs, "EU_FEE_ID"));
            setFeeCategoryId(AbstractDAO.getInteger(rs, "EU_CATEGORY_ID"));
            setAdjustmentCd(rs.getString("ADJUSTMENT_CD"));
            setAdjustedAmount(AbstractDAO.getDouble(rs, "ADJUSTED_AMOUNT"));
            setLastModified(AbstractDAO.getInteger(rs, "eu_fee_lm"));

            Fee fee = new Fee();
            fee.populate(rs);
            setFee(fee);
            
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.warn("field warn: " + sqle.getMessage(), sqle);
        }

    }

    /**
     * Those two methods used to get the adjustment amount between base amount
     * and adjusted amount. Do not remove those two methods!!
     * 
     * Poshan
     * 
     * @return
     */
    public final Double getAdjustmentAmount() {
        Double ret = new Double(0);

        if (getAdjustedAmount() != null && getFee().getAmount() != null) {
            ret = getAdjustedAmount() - getFee().getAmount();
        }

        return ret;
    }

    public final void setAdjustmentAmount(Double adjustmentAmount) {
        if (getFee().getAmount() != null)
            setAdjustedAmount(getFee().getAmount() + adjustmentAmount);
        else
            setAdjustedAmount(getAdjustedAmount() + adjustmentAmount);
    }

    public final Double getAdjustedAmount() {
        String adjustmentCd = getAdjustmentCd();
        Double ret = null;

        if (_adjustedAmount == null) {
            ret = new Double(0.0);
        } else {
            ret = _adjustedAmount;
        }
        
        if (adjustmentCd != null) {
            if (getFee().getAmount() != null){
                if (adjustmentCd.equalsIgnoreCase("N")) {
                    ret = getFee().getAmount() * 1;
                } else if (adjustmentCd.equalsIgnoreCase("D")) {
                    ret = getFee().getAmount() * 2;
                } else if (adjustmentCd.equalsIgnoreCase("H")) {
                    ret = getFee().getAmount() * new Float(0.5);
                } else if (adjustmentCd.equalsIgnoreCase("NF")) {
                    ret = getFee().getAmount() * 0;
                }
                // else if (adjustmentCd.equalsIgnoreCase("O"))
                //
            }else{
                if (adjustmentCd.equalsIgnoreCase("NF")) {
                    ret = 0.0;
                }
            }
        } 

        return ret;
    }

    public final void setAdjustedAmount(Double adjustedAmount) {
        _adjustedAmount = adjustedAmount;
        setDirty(true);
    }

    public final String getAdjustmentCd() {
        return _adjustmentCd;
    }

    public final void setAdjustmentCd(String adjustmentCd) {
        _adjustmentCd = adjustmentCd;
        requiredField(_adjustmentCd, "adjustmentCd");
        setDirty(true);
    }

    public final Integer getFeeCategoryId() {
        return _feeCategoryId;
    }

    /**
     * This method has to be call before all others
     * 
     * @param feeCategoryId
     */
    public final void setFeeCategoryId(Integer feeCategoryId) {
        _feeCategoryId = feeCategoryId;
        setFeeId(null);
        fees = null;
        if (feeCategoryId == null)
            setAdjustedAmount(0.0);
        requiredField(_feeCategoryId, "feeCategoryId");
        setAdjustmentCd("N");
        setDirty(true);
    }

    public final Integer getFeeId(){
        if (_fee.getFeeId() == null && fees != null && fees.size() != 0)
            _fee = fees.get(0);
        return _fee.getFeeId();
    }
    
    /**
     * Only from JSP page.
     * 
     * @param feeId
     */
    public final void setFeeId(Integer feeId){
        if (feeId == null){
            _fee = new Fee();
        } else{
            for (Fee f:fees)
                if (feeId.equals(f.getFeeId())){
                    _fee = f;
                    /*
                     * When you switch the fee name and it indicates a new base
                     * amount could it make the adjusted amount the same as the
                     * base amount?  eengel-ishida (Mantis 0000987) 
                     */
                    _adjustedAmount = _fee.getAmount();
                    break;
                }
        }
    }
    
    public final Fee getFee() {
        return _fee;
    }

    public final void setFee(Fee fee) {

        _fee = fee;
        if (_fee == null) {
            _fee = new Fee();
        }
        requiredField(_fee, "fee");
        setDirty(true);
    }

    public final Integer getEUFeeId() {
        return _euFeeId;
    }

    public final void setEUFeeId(Integer euFeeId) {
        _euFeeId = euFeeId;
    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();

        result = PRIME * result
                + ((_euFeeId == null) ? 0 : _euFeeId.hashCode());
        result = PRIME * result + ((_fee == null) ? 0 : _fee.hashCode());
        result = PRIME * result
                + ((_feeCategoryId == null) ? 0 : _feeCategoryId.hashCode());
        result = PRIME * result
                + ((_adjustmentCd == null) ? 0 : _adjustmentCd.hashCode());
        result = PRIME * result
                + ((_adjustedAmount == null) ? 0 : _adjustedAmount.hashCode());

        return result;
    }

    @Override
    public final boolean equals(Object obj) {

        if ((obj == null) || !(super.equals(obj))
                || (getClass() != obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final EUFee other = (EUFee) obj;

        // Either both null or equal values.
        if (((_euFeeId == null) && (other.getEUFeeId() != null))
                || ((_euFeeId != null) && (other.getEUFeeId() == null))
                || ((_euFeeId != null) && (other.getEUFeeId() != null) && !(_euFeeId
                        .equals(other.getEUFeeId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_fee == null) && (other.getFee() != null))
                || ((_fee != null) && (other.getFee() == null))
                || ((_fee != null) && (other.getFee() != null) && !(_fee
                        .equals(other.getFee())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_feeCategoryId == null) && (other.getFeeCategoryId() != null))
                || ((_feeCategoryId != null) && (other.getFeeCategoryId() == null))
                || ((_feeCategoryId != null)
                        && (other.getFeeCategoryId() != null) && !(_feeCategoryId
                        .equals(other.getFeeCategoryId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_adjustmentCd == null) && (other.getAdjustmentCd() != null))
                || ((_adjustmentCd != null) && (other.getAdjustmentCd() == null))
                || ((_adjustmentCd != null)
                        && (other.getAdjustmentCd() != null) && !(_adjustmentCd
                        .equals(other.getAdjustmentCd())))) {
            return false;
        }

        // Either both null or equal values.
        if (((this.getAdjustedAmount() == null) && (other.getAdjustedAmount() != null))
                || ((this.getAdjustedAmount() != null) && (other
                        .getAdjustedAmount() == null))
                || ((this.getAdjustedAmount() != null)
                        && (other.getAdjustedAmount() != null) && !(this
                        .getAdjustedAmount().equals(other.getAdjustedAmount())))) {
            return false;
        }
        return true;
    }

    public void setFees(List<Fee> fs) {
        fees = fs;
    }
}

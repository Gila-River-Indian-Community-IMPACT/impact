package us.oh.state.epa.stars2.database.dbObjects.serviceCatalog;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;

public class Fee extends BaseDB {
    private Integer feeId;
    private String feeNm;
    private Double amount;
    private Integer lowRange;
    private Integer highRange;
    private transient Timestamp effectiveDate;
    private long longEffectiveDate;
    private transient Timestamp endDate;
    private long longEndDate;

    public Fee() {
        super();
        setDirty(false);
    }

    public Fee(Fee old) {
        super(old);

        if (old != null) {
            setFeeId(old.getFeeId());
            setFeeNm(old.getFeeNm());
            setAmount(old.getAmount());
            setLowRange(old.getLowRange());
            setHighRange(old.getHighRange());
            setEffectiveDate(old.getEffectiveDate());
            setEndDate(old.getEndDate());
            setDirty(old.isDirty());
        }
    }

    public final Double getAmount() {
        return amount;
    }

    public final void setAmount(Double amount) {
        this.amount = amount;
        setDirty(true);
    }

    public final Timestamp getEffectiveDate() {
        return effectiveDate;
    }

    public final void setEffectiveDate(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
        if (this.effectiveDate != null) {
            this.longEffectiveDate = this.effectiveDate.getTime();
        } else {
            this.longEffectiveDate = 0;
        }
        setDirty(true);
    }

    public final Timestamp getEndDate() {
        return endDate;
    }

    public final void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
        if (this.endDate != null) {
            this.longEndDate = this.endDate.getTime();
        } else {
            this.longEndDate = 0;
        }
        setDirty(true);
    }

    public final Integer getFeeId() {
        return feeId;
    }

    public final void setFeeId(Integer feeId) {
        this.feeId = feeId;
        setDirty(true);
    }

    public final String getFeeNm() {
        return feeNm;
    }

    public final void setFeeNm(String feeNm) {
        this.feeNm = feeNm;
        setDirty(true);
    }

    public final long getLongEffectiveDate() {
        long ret = 0;
        
        if (effectiveDate != null) {
            ret = effectiveDate.getTime();
        }
        
        return ret;
    }

    public final void setLongEffectiveDate(long longEffectiveDate) {
        effectiveDate = null;
        if (longEffectiveDate > 0) {
            effectiveDate = new Timestamp(longEffectiveDate);
        }
    }

    public final long getLongEndDate() {
        long ret = 0;
        
        if (endDate != null) {
            ret = endDate.getTime();
        }
        
        return ret;
    }

    public final void setLongEndDate(Long longEndDate) {
        endDate = null;
        if (longEndDate != null) {
            endDate = new Timestamp(longEndDate);
        }
    }

    public final Integer getHighRange() {
        return highRange;
    }

    public final void setHighRange(Integer highRange) {
        this.highRange = highRange;
        setDirty(true);
    }

    public final Integer getLowRange() {
        return lowRange;
    }

    public final void setLowRange(Integer lowRange) {
        this.lowRange = lowRange;
        setDirty(true);
    }
    
    public String getDescription(String rptType, int minNeedClause, String clause) {
        String dsc;
        Integer k = lowRange;
        if(lowRange != null) {
            if(highRange == null) {
                dsc = lowRange + " or more Tons per Year";
                k++;
            }
            else if(highRange.intValue() == 0) {
                dsc = "Zero Emissions";
                k = new Integer(-1);
            }
            else if(lowRange.intValue() == 0) {
                if(EmissionReportsRealDef.SMTV.equals(rptType)) {
                    dsc = " Less than " + highRange + " Tons per Year";
                } else {
                    dsc = "  More than 0 but less than " + highRange + " Tons per Year";
                }
            }
            else {
                dsc = lowRange + " or more but less than "+ highRange + " Tons per Year";
            }
            if(lowRange != null && lowRange.intValue() >= minNeedClause) {
                dsc = dsc + clause;
            }
        } else {
            dsc = "Priced Per Ton";
        }
        return dsc;
    }
    
    public static String getDescription(Integer low, Integer high) {
        String dsc;
        Integer k = low;
        if(low != null) {
            if(high == null) {
                dsc = "More than " + low;
                k++;
            } else if(high.intValue() == 0) {
                dsc = "Zero";
                k = new Integer(-1);
            } else if(low.intValue() == 0) {
                    dsc = "0 - " + high;
            } else {
                dsc = low + " - "+ high;
            }
        } else {
            dsc = "Per Ton";
        }
        return dsc;
    }
    
    public String getShortDescription() {
        // This is only used for NTV
        String dsc;
        Integer k = lowRange;
        if(lowRange != null) {
            if(highRange == null) {
                dsc = "More than " + lowRange;
                k++;
            }
            else if(highRange.intValue() == 0) {
                dsc = "Zero";
                k = new Integer(-1);
            }
            else if(lowRange.intValue() == 0) {
                dsc = "0 - " + highRange;
            }
            else {
                dsc = lowRange + " - "+ highRange;
            }
        } else {
            dsc = "Per Ton";
        }
        return dsc;
    }

    public final void populate(ResultSet rs) {

        try {
            setFeeId(AbstractDAO.getInteger(rs, "fee_id"));
            setFeeNm(rs.getString("fee_nm"));
            setAmount(AbstractDAO.getDouble(rs, "amount"));
            setLowRange(AbstractDAO.getInteger(rs, "low_range"));
            setHighRange(AbstractDAO.getInteger(rs, "high_range"));
            setEffectiveDate(rs.getTimestamp("fee_effective_dt"));
            setEndDate(rs.getTimestamp("fee_end_dt"));

            setLastModified(AbstractDAO.getInteger(rs, "fee_lm"));
            setDirty(false);
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    @Override
    public final int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((amount == null) ? 0 : amount.hashCode());
        result = PRIME * result
                + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
        result = PRIME * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = PRIME * result + ((feeId == null) ? 0 : feeId.hashCode());
        result = PRIME * result + ((feeNm == null) ? 0 : feeNm.hashCode());
        result = PRIME * result
                + ((highRange == null) ? 0 : highRange.hashCode());
        result = PRIME * result
                + ((lowRange == null) ? 0 : lowRange.hashCode());
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
        final Fee other = (Fee) obj;
        
        if (feeId != other.feeId) 
            return false;
        
        return true;
    }

    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // manually set transient date values since this does not appear to
        // work properly with persistence
        setLongEffectiveDate(this.longEffectiveDate);
        setLongEndDate(this.longEndDate);
    }
}

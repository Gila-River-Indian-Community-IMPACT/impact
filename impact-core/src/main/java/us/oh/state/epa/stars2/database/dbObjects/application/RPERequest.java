package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;

public class RPERequest extends Application {

    public static final String ISSUED = "i";
    public static final String DENIED = "d";
    public static final String DEAD_ENDED = "e";

    private Integer permitId;
    private Timestamp terminationDate;
    private String regulatedCmntyDsc;
    private String dispositionFlag;
    private Float otherAdjustment;

    public RPERequest() {
        super();
        setApplicationTypeCD("RPE");
        this.requiredField(permitId, "permitId");
        setDirty(false);
    }

    /**
     * Copy Constructor.
     * 
     * @param old an <code>RPERequest</code> object.
     */
    public RPERequest(RPERequest old) {

        super(old);

        if (old != null) {
            setPermitId(old.getPermitId());
            setTerminationDate(old.getTerminationDate());
            setRegulatedCmntyDsc(old.getRegulatedCmntyDsc());
            setDispositionFlag(old.getDispositionFlag());
            setOtherAdjustment(old.getOtherAdjustment());
            setDirty(old.isDirty());
        }
    }

    @Override
    public final void populate(java.sql.ResultSet rs) {

        try {
            super.populate(rs);

            setPermitId(AbstractDAO.getInteger(rs, "prer_permit_id"));
            setTerminationDate(rs.getTimestamp("termination_date"));
            setRegulatedCmntyDsc(rs.getString("prer_reg_cmnty_dsc"));
            setDispositionFlag(rs.getString("prer_disposition_flag"));
            setOtherAdjustment(AbstractDAO.getFloat(rs, "prer_other_adjustment"));
            setLastModified(AbstractDAO.getInteger(rs, "prer_lm"));

            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
        this.requiredField(permitId, "permitId");
        setDirty(true );
    }

    public final Timestamp getTerminationDate() {
        return terminationDate;
    }

    public final void setTerminationDate(Timestamp terminationDate) {
        this.terminationDate = terminationDate;
        setDirty(true);
    }

    public final String getRegulatedCmntyDsc() {
        return regulatedCmntyDsc;
    }

    public final void setRegulatedCmntyDsc(String regulatedCmntyDsc) {
        this.regulatedCmntyDsc = regulatedCmntyDsc;
        setDirty(true);
    }

    public final String getDispositionFlag() {
        return dispositionFlag;
    }

    public final void setDispositionFlag(String disposition) {
        dispositionFlag = disposition;
        setDirty(true);
    }

    public final Float getOtherAdjustment() {
        if (otherAdjustment == null)
            otherAdjustment = new Float(0.0);
        return otherAdjustment;
    }

    public final void setOtherAdjustment(Float otherAdjustment) {
        this.otherAdjustment = otherAdjustment;
    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();

        result = PRIME * result
            + ((permitId == null) ? 0 : permitId.hashCode());
        result = PRIME * result
            + ((terminationDate == null) ? 0 : terminationDate.hashCode());
        result = PRIME * result
            + ((regulatedCmntyDsc == null) ? 0 : regulatedCmntyDsc.hashCode());
        result = PRIME * result 
            + ((dispositionFlag == null) ? 0 : dispositionFlag.hashCode());
        result = PRIME * result 
            + ((otherAdjustment == null) ? 0 : otherAdjustment.hashCode());
                        
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if ((obj == null) || !(super.equals(obj))
            || (getClass() != obj.getClass())) {

            return false;
        }

        if (this == obj) {
            return true;
        }

        final RPERequest other = (RPERequest) obj;

        // Either both null or equal values.
        if (((permitId == null) && (other.getPermitId() != null))
            || ((permitId != null) && (other.getPermitId() == null))
            || ((permitId != null) && (other.getPermitId() != null) 
                && !(permitId.equals(other.getPermitId())))) {
            
            return false;
        }

        // Either both null or equal values.
        if (((terminationDate == null) && (other.getTerminationDate() != null))
            || ((terminationDate != null) 
                && (other.getTerminationDate() == null))
            || ((terminationDate != null) && (other.getTerminationDate() != null) 
                && !(terminationDate.equals(other.getTerminationDate())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((regulatedCmntyDsc == null) && (other.getRegulatedCmntyDsc() != null))
            || ((regulatedCmntyDsc != null) 
                && (other.getRegulatedCmntyDsc() == null))
            || ((regulatedCmntyDsc != null) && (other.getRegulatedCmntyDsc() != null) 
                && !(regulatedCmntyDsc.equals(other.getRegulatedCmntyDsc())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((dispositionFlag == null) 
             && (other.getDispositionFlag() != null))
            || ((dispositionFlag != null) 
                && (other.getDispositionFlag() == null))
            || ((dispositionFlag != null) 
                && (other.getDispositionFlag() != null) 
                && !(dispositionFlag.equals(other.getDispositionFlag())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((otherAdjustment == null) 
             && (other.getOtherAdjustment() != null))
            || ((otherAdjustment != null) 
                && (other.getOtherAdjustment() == null))
            || ((otherAdjustment != null) 
                && (other.getOtherAdjustment() != null) 
                && !(otherAdjustment.equals(other.getOtherAdjustment())))) {
                        
            return false;
        }

        return true;
    }

}

package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.RPRReasonDef;

@SuppressWarnings("serial")
public class RPRRequest extends Application {

    public static final String COMPLETED = "c";
    public static final String RETURNED  = "r";
    public static final String PENDING   = "p";

    public static final String ISSUED_PROPOSED       = "1";
    public static final String ISSUED_PROPOSED_FINAL = "2";
    public static final String ISSUED_FINAL          = "3";

    private Integer permitId;
    private String rprReasonCd;
    private String regulatedCmntyDsc;
    private String dispositionFlag;
    private boolean revokeEntirePermit;
    private String basisForRevoke;

    /** Constructor. */
    public RPRRequest() {
        super();
        setApplicationTypeCD("RPR");
        this.requiredField(permitId, "permitId");
        this.requiredField(rprReasonCd, "rprReasonCd");
    }

    /**
     * Copy Constructor.
     * 
     * @param old an <code>RPRRequest</code> object
     */
    public RPRRequest(RPRRequest old) {

        super(old);

        if (old != null) {
            setPermitId(old.getPermitId());
            setRprReasonCd(old.getRprReasonCd());
            setRegulatedCmntyDsc(old.getRegulatedCmntyDsc());
            setDispositionFlag(old.getDispositionFlag());
            setRevokeEntirePermit(old.isRevokeEntirePermit());
            setBasisForRevocation(old.getBasisForRevocation());
            setDirty(old.isDirty());
        }
    }

    @Override
    public final void populate(java.sql.ResultSet rs) {

        try {
            super.populate(rs);
            
            setPermitId(AbstractDAO.getInteger(rs, "prpr_permit_id"));
            setRprReasonCd(rs.getString("rpr_reason_cd"));
            setRegulatedCmntyDsc(rs.getString("prpr_reg_cmnty_dsc"));
            setDispositionFlag(rs.getString("prpr_disposition_flag"));
            setRevokeEntirePermit(AbstractDAO.translateIndicatorToBoolean(rs.getString("prpr_revoke_entire_permit")));
            setBasisForRevocation(rs.getString("prpr_basis_for_revoke"));
            setLastModified(AbstractDAO.getInteger(rs, "prpr_lm"));

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

    public final String getRprReasonCd() {
        return rprReasonCd;
    }

    public final void setRprReasonCd(String rprReasonCd) {
        this.rprReasonCd = rprReasonCd;
        this.requiredField(rprReasonCd, "rprReasonCd");
        setDirty(true );
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

    public final boolean isRevokeEntirePermit() {
        return revokeEntirePermit;
    }

    public final void setRevokeEntirePermit(boolean revokeItAll) {
        revokeEntirePermit = revokeItAll;
    }

    public final String getBasisForRevocation() {
        return basisForRevoke;
    }

    public final void setBasisForRevocation(String basisForRevocation) {
        this.basisForRevoke = basisForRevocation;
    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();

        result = PRIME * result
            + ((permitId == null) ? 0 : permitId.hashCode());
        result = PRIME * result
            + ((rprReasonCd == null) ? 0 : rprReasonCd.hashCode());
        result = PRIME * result
            + ((regulatedCmntyDsc == null) ? 0 : regulatedCmntyDsc.hashCode());
        result = PRIME * result 
            + ((dispositionFlag == null) ? 1 : 0);
        result = PRIME * result + (revokeEntirePermit ? 1 : 0);
        result = PRIME * result + ((basisForRevoke == null) ? 0 : basisForRevoke.hashCode());

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

        final RPRRequest other = (RPRRequest) obj;

        // Either both null or equal values.
        if (((permitId == null) && (other.getPermitId() != null))
            || ((permitId != null) && (other.getPermitId() == null))
            || ((permitId != null) && (other.getPermitId() != null) 
                && !(permitId.equals(other.getPermitId())))) {
            
            return false;
        }

        // Either both null or equal values.
        if (((rprReasonCd == null) && (other.getRprReasonCd() != null))
            || ((rprReasonCd != null) && (other.getRprReasonCd() == null))
            || ((rprReasonCd != null) && (other.getRprReasonCd() != null) 
                && !(rprReasonCd.equals(other.getRprReasonCd())))) {
            
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
        if (((dispositionFlag == null) && (other.getDispositionFlag() != null))
            || ((dispositionFlag != null) 
                && (other.getDispositionFlag() == null))
            || ((dispositionFlag != null) && (other.getDispositionFlag() != null) 
                && !(dispositionFlag.equals(other.getDispositionFlag())))) {
                        
            return false;
        }

        if (revokeEntirePermit != other.isRevokeEntirePermit()) {
            return false;
        }

        // Either both null or equal values.
        if (((basisForRevoke == null) && (other.getBasisForRevocation() != null))
            || ((basisForRevoke != null) 
                && (other.getBasisForRevocation() == null))
            || ((basisForRevoke != null) && (other.getBasisForRevocation() != null) 
                && !(basisForRevoke.equals(other.getBasisForRevocation())))) {
            
            return false;
        }

        return true;
    }

    @Override
    public String getApplicationPurposeDesc() {
        StringBuffer sb = new StringBuffer();
        if (rprReasonCd != null) {
            sb.append(RPRReasonDef.getData().getItems().getItemDesc(rprReasonCd));
        }
        return sb.toString();
    }

}

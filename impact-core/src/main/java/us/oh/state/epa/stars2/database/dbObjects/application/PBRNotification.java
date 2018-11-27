package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.PBRReasonDef;
import us.oh.state.epa.stars2.def.PBRTypeDef;

@SuppressWarnings("serial")
public class PBRNotification extends Application {

    public static final String RECEIVED ="r";
    public static final String ACCEPTED ="a";
    public static final String DENIED = "d";
    public static final String SUPERSEDED = "s";

    private String pbrReasonCd;
    private String pbrTypeCd;
    private boolean requestingRevocationFlag;
    private String regulatedCmntyDsc;
    private String dispositionFlag;
    private List<PBRNotificationDocument> pbrDocuments;

    public PBRNotification() {
        super();
        setApplicationTypeCD("PBR");
        this.requiredField(pbrReasonCd, "pbrReasonCd");
        this.requiredField(pbrTypeCd, "pbrTypeCd");
        setDirty(false);
    }

    /**
     * @param old
     */
    public PBRNotification(PBRNotification old) {

        super(old);

        if (old != null) {
            setPbrReasonCd(old.getPbrReasonCd());
            setPbrTypeCd(old.getPbrTypeCd());
            setRequestingRevocationFlag(old.isRequestingRevocationFlag());
            setRegulatedCmntyDsc(old.getRegulatedCmntyDsc());
            setDispositionFlag(old.getDispositionFlag());
            setDirty(old.isDirty());
        }
    }

    @Override
    public final void populate(java.sql.ResultSet rs) {

        try {
            super.populate(rs);

            setPbrReasonCd(rs.getString("pbr_reason_cd"));
            setPbrTypeCd(rs.getString("pbr_type_cd"));
            setRequestingRevocationFlag(AbstractDAO.translateIndicatorToBoolean(rs.getString("requesting_revocation_flag")));
            setRegulatedCmntyDsc(rs.getString("ppn_reg_cmnty_dsc"));
            setDispositionFlag(rs.getString("ppn_disposition_flag"));
            setLastModified(AbstractDAO.getInteger(rs, "ppn_lm"));

            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getPbrReasonCd() {
        return pbrReasonCd;
    }

    public final void setPbrReasonCd(String pbrReasonCd) {
        this.pbrReasonCd = pbrReasonCd;
        this.requiredField(pbrReasonCd, "pbrReasonCd");
        setDirty(true);
    }

    public final String getPbrTypeCd() {
        return pbrTypeCd;
    }

    public final void setPbrTypeCd(String pbrTypeCd) {
        this.pbrTypeCd = pbrTypeCd;
        this.requiredField(pbrTypeCd, "pbrTypeCd");
        setDirty(true);
    }

    public final boolean isRequestingRevocationFlag() {
        return requestingRevocationFlag;
    }

    public final void setRequestingRevocationFlag(boolean requestingRevocationFlag) {
        this.requestingRevocationFlag = requestingRevocationFlag;
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

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();

        result = PRIME * result
            + ((pbrReasonCd == null) ? 0 : pbrReasonCd.hashCode());
        result = PRIME * result
            + ((pbrTypeCd == null) ? 0 : pbrTypeCd.hashCode());
        result = PRIME * result + (requestingRevocationFlag ? 1 : 0);
        result = PRIME * result
            + ((regulatedCmntyDsc == null) ? 0 : regulatedCmntyDsc.hashCode());
        result = PRIME * result + ((dispositionFlag == null) ? 0 : dispositionFlag.hashCode());

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

        final PBRNotification other = (PBRNotification) obj;

        // Either both null or equal values.
        if (((pbrReasonCd == null) && (other.getPbrReasonCd() != null))
            || ((pbrReasonCd != null) && (other.getPbrReasonCd() == null))
            || ((pbrReasonCd != null) && (other.getPbrReasonCd() != null) 
                && !(pbrReasonCd.equals(other.getPbrReasonCd())))) {
            
            return false;
        }

        // Either both null or equal values.
        if (((pbrTypeCd == null) && (other.getPbrTypeCd() != null))
            || ((pbrTypeCd != null) && (other.getPbrTypeCd() == null))
            || ((pbrTypeCd != null) && (other.getPbrTypeCd() != null) 
                && !(pbrTypeCd.equals(other.getPbrTypeCd())))) {
            
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

        if (requestingRevocationFlag != other.isRequestingRevocationFlag()) {
            return false;
        }

        return true;
    }

    public final List<PBRNotificationDocument> getPbrDocuments() {
        if (pbrDocuments == null) {
            pbrDocuments = new ArrayList<PBRNotificationDocument>();
        }
        return pbrDocuments;
    }

    public final void setPbrDocuments(List<PBRNotificationDocument> pbrDocuments) {
        this.pbrDocuments = new ArrayList<PBRNotificationDocument>();
        if (pbrDocuments != null) {
            this.pbrDocuments.addAll(pbrDocuments);
        }
    }
    
    public boolean hasAttachments() {
        boolean hasAttachments = false;
        if (getPbrDocuments().size() > 0) {
            hasAttachments = true;
        }
        return hasAttachments;
    }

    @Override
    public String getApplicationPurposeDesc() {
        StringBuffer sb = new StringBuffer();
        if (pbrTypeCd != null) {
            sb.append(PBRTypeDef.getData().getItems().getItemDesc(pbrTypeCd));
        } if (pbrReasonCd != null) {
            if (sb.length() > 0) {
                sb.append(": ");
            }
            sb.append(PBRReasonDef.getData().getItems().getItemDesc(pbrReasonCd));
        }
        return sb.toString();
    }
}

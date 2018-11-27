package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.PermitTypeDef;
/*
public class RegPermit extends Permit {

    private Timestamp receivedDate;
    private String tc;
    private String eapStatus;
    private Timestamp processDate;
    private String withdrawalCode;
    private Timestamp withdrawalDate;
    private String adjudCode;
    private Timestamp renProcDate;
    private String appendixCode;
    private String renewalStatus;
    private String renewalType;
    private String stateTermsCond;
    private String facilityMajorPolluterCode;
    private String sicCode;

    public RegPermit() {
        super();
        setPermitType(PermitTypeDef.REG);
        setDirty(false);
    }
    */

    /**
     * @param old
     */
/*
    public RegPermit(RegPermit old) {

        super(old);
        if (old != null) {
            setReceivedDate(old.getReceivedDate());
            setTc(old.getTc());
            setEapStatus(old.getEapStatus());
            setProcessDate(old.getProcessDate());
            setWithdrawalCode(old.getWithdrawalCode());
            setWithdrawalDate(old.getWithdrawalDate());
            setAdjudCode(old.getAdjudCode());
            setRenProcDate(old.getRenProcDate());
            setAppendixCode(old.getAppendixCode());
            setRenewalStatus(old.getRenewalStatus());
            setRenewalType(old.getRenewalType());
            setStateTermsCond(old.getStateTermsCond());
            setFacilityMajorPolluterCode(old.getFacilityMajorPolluterCode());
            setSicCode(old.getSicCode());
            setDirty(old.isDirty());
        }
    }

    public final void populate(ResultSet rs) {

        try {
            super.populate(rs);

            setReceivedDate(rs.getTimestamp("received_date"));
            setTc(rs.getString("tc"));
            setEapStatus(rs.getString("eap_status"));
            setProcessDate(rs.getTimestamp("process_date"));
            setWithdrawalCode(rs.getString("withdrawal_code"));
            setWithdrawalDate(rs.getTimestamp("withdrawal_date"));
            setAdjudCode(rs.getString("adjud_code"));
            setRenProcDate(rs.getTimestamp("ren_proc_date"));
            setAppendixCode(rs.getString("appendix_code"));
            setRenewalStatus(rs.getString("renewal_status"));
            setRenewalType(rs.getString("renewal_type_cd"));
            setStateTermsCond(rs.getString("state_terms_cond"));
            setFacilityMajorPolluterCode(rs.getString("facility_major_polluter_code"));
            setSicCode(rs.getString("sic_cd"));

            setLastModified(AbstractDAO.getInteger(rs, "prp_lm"));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final boolean getDisplayWithdrawalDate() {
        return withdrawalDate != null;
    }

    public final Timestamp getReceivedDate() {
        return receivedDate;
    }

    public final void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
        setDirty(true);
    }

    public final String getTc() {
        return tc;
    }

    public final void setTc(String tc) {
        this.tc = tc;
        setDirty(true);
    }

    public final String getEapStatus() {
        return eapStatus;
    }

    public final String getDisplayEapStatus() {
        String ret = "not on file";

        if (eapStatus != null && eapStatus.length() > 0) {
            switch (eapStatus.charAt(0)) {
            case ' ':
                break;
            case '0':
                ret = "removed";
                break;
            case '1':
                ret = "in efffect";
                break;
            case '3':
                ret = "received and is being processed";
                break;
            default:
                ret = "codeValue=\"" + eapStatus + "\"";
                break;
            }
        }
        return ret;
    }

    public final void setEapStatus(String eapStatus) {
        this.eapStatus = eapStatus;
        setDirty(true);
    }

    public final Timestamp getProcessDate() {
        return processDate;
    }

    public final void setProcessDate(Timestamp processDate) {
        this.processDate = processDate;
        setDirty(true);
    }

    public final String getWithdrawalCode() {
        return withdrawalCode;
    }

    public final String getDisplayWithdrawalCode() {
        String ret = "";

        if (withdrawalCode != null && withdrawalCode.length() > 0) {
            switch (withdrawalCode.charAt(0)) {
            case '4':
                ret = "none";
                break;
            case '5':
                ret = "result of a state stipulation";
                break;
            case '6':
                ret = "result of a federal stipulation";
                break;
            default:
                ret = "codeValue=\"" + withdrawalCode + "\"";
                break;
            }
        }
        return ret;
    }

    public final void setWithdrawalCode(String withdrawalCode) {
        this.withdrawalCode = withdrawalCode;
        setDirty(true);
    }

    public final Timestamp getWithdrawalDate() {
        return withdrawalDate;
    }

    public final void setWithdrawalDate(Timestamp withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
        setDirty(true);
    }

    public final String getAdjudCode() {
        return adjudCode;
    }

    public final String getDisplayAdjudCode() {
        String ret = "no adjudication request or action";
        if (adjudCode != null && adjudCode.length() > 0) {
            switch (adjudCode.charAt(0)) {
            case ' ':
                break;
            case '0':
                ret = "adjudication has been satisfied";
                break;
            case '1':
                ret = "emission unit has a pending adjudication request";
                break;
            default:
                ret = "codeValue=\"" + adjudCode + "\"";
                break;
            }
        }
        return ret;
    }

    public final void setAdjudCode(String adjudCode) {
        this.adjudCode = adjudCode;
        setDirty(true);
    }

    public final Timestamp getRenProcDate() {
        return renProcDate;
    }

    public final void setRenProcDate(Timestamp renProcDate) {
        this.renProcDate = renProcDate;
        setDirty(true);
    }

    public final String getAppendixCode() {
        return appendixCode;
    }

    public final void setAppendixCode(String appendixCode) {
        this.appendixCode = appendixCode;
        setDirty(true);
    }

    public final String getRenewalStatus() {
        return renewalStatus;
    }

    public final String getDisplayRenewalStatus() {
        String ret = "not in renewal program";

        if (renewalStatus != null && renewalStatus.length() > 0) {
            switch (renewalStatus.charAt(0)) {
            case ' ':
                break;
            case '1':
                ret = "expiration notice has been sent to the facility";
                break;
            case '2':
                ret = "application has been received and advertised";
                break;
            case '3':
                ret = "permit is in process at state level";
                break;
            default:
                ret = "codeValue=\"" + renewalStatus + "\"";
                break;
            }
        }
        return ret;

    }

    public final void setRenewalStatus(String renewalStatus) {
        this.renewalStatus = renewalStatus;
        setDirty(true);
    }

    public final String getRenewalType() {
        return renewalType;
    }

    public final String getDisplayRenewalType() {
        String ret = "";
        
        if (renewalType != null && renewalType.length() > 0) {
            switch (renewalType.charAt(0)) {
            case '1':
                ret = "a PTO is being issued, a variance is bing issued or the emission unit is being withdrawn";
                break;
            case '2':
                ret = "a variance is being issued as a PTO, a variance is being issued or revised, or the emission unit is being withdrawn";
                break;
            default:
                ret = "codeValue=\"" + renewalType + "\"";
                break;
            }
        }
        return ret;
    }

    public final void setRenewalType(String renewalType) {
        this.renewalType = renewalType;
        setDirty(true);
    }

    public final String getStateTermsCond() {
        return stateTermsCond;
    }

    public final String getDisplayStateTermsCond() {
        String ret = "";

        if (stateTermsCond != null && stateTermsCond.length() > 0) {
            switch (stateTermsCond.charAt(0)) {
            case 'A':
                ret = "Special Terms and Conditions are attached to PTO";
                break;
            case 'B':
                ret = "Denial support data is attached to Denial";
                break;
            case 'C':
                ret = "Variance Special Terms and Conditions attached to variance";
                break;
            default:
                ret = "codeValue=\"" + stateTermsCond + "\"";
                break;
            }
        }
        return ret;
    }

    public final void setStateTermsCond(String stateTermsCond) {
        this.stateTermsCond = stateTermsCond;
        setDirty(true);
    }

    public final String getFacilityMajorPolluterCode() {
        return facilityMajorPolluterCode;
    }

    public final void setFacilityMajorPolluterCode(
                                                   String facilityMajorPolluterCode) {
        this.facilityMajorPolluterCode = facilityMajorPolluterCode;
        setDirty(true);
    }

    public final String getSicCode() {
        return sicCode;
    }

    public final void setSicCode(String sicCode) {
        this.sicCode = sicCode;
        setDirty(true);
    }

    @Override
        public final int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();

        result = PRIME * result
            + ((receivedDate == null) ? 0 : receivedDate.hashCode());
        result = PRIME * result + ((tc == null) ? 0 : tc.hashCode());
        result = PRIME * result
            + ((eapStatus == null) ? 0 : eapStatus.hashCode());
        result = PRIME * result
            + ((processDate == null) ? 0 : processDate.hashCode());
        result = PRIME * result
            + ((withdrawalCode == null) ? 0 : withdrawalCode.hashCode());
        result = PRIME * result
            + ((withdrawalDate == null) ? 0 : withdrawalDate.hashCode());
        result = PRIME * result
            + ((adjudCode == null) ? 0 : adjudCode.hashCode());
        result = PRIME * result
            + ((renProcDate == null) ? 0 : renProcDate.hashCode());
        result = PRIME * result
            + ((appendixCode == null) ? 0 : appendixCode.hashCode());
        result = PRIME * result
            + ((renewalStatus == null) ? 0 : renewalStatus.hashCode());
        result = PRIME * result
            + ((renewalType == null) ? 0 : renewalType.hashCode());
        result = PRIME * result
            + ((stateTermsCond == null) ? 0 : stateTermsCond.hashCode());
        result = PRIME * result
            + ((facilityMajorPolluterCode == null) ? 0 : facilityMajorPolluterCode.hashCode());
        result = PRIME * result + ((sicCode == null) ? 0 : sicCode.hashCode());

        return result;

    } // END: public int hashCode()

    @Override
        public final boolean equals(Object obj) {
        if ((obj == null) || !(super.equals(obj))
            || (getClass() != obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final RegPermit other = (RegPermit) obj;

        // Either both null or equal values.
        if (((receivedDate == null) && (other.getReceivedDate() != null))
            || ((receivedDate != null) && (other.getReceivedDate() == null))
            || ((receivedDate != null) && (other.getReceivedDate() != null) 
                && !(receivedDate.equals(other.getReceivedDate())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((tc == null) && (other.getTc() != null))
            || ((tc != null) && (other.getTc() == null))
            || ((tc != null) && (other.getTc() != null) 
                && !(tc.equals(other.getTc())))) {
                                                             
            return false;
        }

        // Either both null or equal values.
        if (((eapStatus == null) && (other.getEapStatus() != null))
            || ((eapStatus != null) && (other.getEapStatus() == null))
            || ((eapStatus != null) && (other.getEapStatus() != null) 
                && !(eapStatus.equals(other.getEapStatus())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((processDate == null) && (other.getProcessDate() != null))
            || ((processDate != null) && (other.getProcessDate() == null))
            || ((processDate != null) && (other.getProcessDate() != null) 
                && !(processDate.equals(other.getProcessDate())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((withdrawalCode == null) && (other.getWithdrawalCode() != null))
            || ((withdrawalCode != null) && (other.getWithdrawalCode() == null))
            || ((withdrawalCode != null)
                && (other.getWithdrawalCode() != null)
                && !(withdrawalCode.equals(other.getWithdrawalCode())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((withdrawalDate == null) && (other.getWithdrawalDate() != null))
            || ((withdrawalDate != null) && (other.getWithdrawalDate() == null))
            || ((withdrawalDate != null)
                && (other.getWithdrawalDate() != null) 
                && !(withdrawalDate.equals(other.getWithdrawalDate())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((adjudCode == null) && (other.getAdjudCode() != null))
            || ((adjudCode != null) && (other.getAdjudCode() == null))
            || ((adjudCode != null) && (other.getAdjudCode() != null) 
                && !(adjudCode.equals(other.getAdjudCode())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((renProcDate == null) && (other.getRenProcDate() != null))
            || ((renProcDate != null) && (other.getRenProcDate() == null))
            || ((renProcDate != null) && (other.getRenProcDate() != null) 
                && !(renProcDate.equals(other.getRenProcDate())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((appendixCode == null) && (other.getAppendixCode() != null))
            || ((appendixCode != null) && (other.getAppendixCode() == null))
            || ((appendixCode != null) && (other.getAppendixCode() != null)
                && !(appendixCode.equals(other.getAppendixCode())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((renewalStatus == null) && (other.getRenewalStatus() != null))
            || ((renewalStatus != null) && (other.getRenewalStatus() == null))
            || ((renewalStatus != null)
                && (other.getRenewalStatus() != null) 
                && !(renewalStatus.equals(other.getRenewalStatus())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((renewalType == null) && (other.getRenewalType() != null))
            || ((renewalType != null) && (other.getRenewalType() == null))
            || ((renewalType != null) && (other.getRenewalType() != null) 
                && !(renewalType.equals(other.getRenewalType())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((stateTermsCond == null) && (other.getStateTermsCond() != null))
            || ((stateTermsCond != null) && (other.getStateTermsCond() == null))
            || ((stateTermsCond != null)
                && (other.getStateTermsCond() != null) 
                && !(stateTermsCond.equals(other.getStateTermsCond())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((facilityMajorPolluterCode == null) 
             && (other.getFacilityMajorPolluterCode() != null))
            || ((facilityMajorPolluterCode != null) 
                && (other.getFacilityMajorPolluterCode() == null))
            || ((facilityMajorPolluterCode != null)
                && (other.getFacilityMajorPolluterCode() != null) 
                && !(facilityMajorPolluterCode.equals(other.getFacilityMajorPolluterCode())))) {
                        
            return false;
        }

        // Either both null or equal values.
        if (((sicCode == null) && (other.getSicCode() != null))
            || ((sicCode != null) && (other.getSicCode() == null))
            || ((sicCode != null) && (other.getSicCode() != null) 
                && !(sicCode.equals(other.getSicCode())))) {
                        
            return false;
        }
        return true;

    } // END: public final boolean equals(Object obj)

}
*/

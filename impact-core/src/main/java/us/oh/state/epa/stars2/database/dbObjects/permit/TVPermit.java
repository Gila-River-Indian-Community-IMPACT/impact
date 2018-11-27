package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.USEPAOutcomeDef;
import us.oh.state.epa.stars2.framework.util.Duration;

public class TVPermit extends Permit {
    //private boolean _pppReviewWaived;
	private boolean _usepaExpedited;
    private String _usepaOutcomeCd;
    private Timestamp _usepaCompleteDate;
    private Timestamp _recissionDate;
    private boolean _acidRain; 
    private String _acidDesc;
    private boolean _cam;
    private String _camDesc;
    private boolean _sec112;
    private String _sec112Desc;
    private Timestamp _usepaReceivedPermitDate;
    private Timestamp _usepaPermitSentDate;
    private Timestamp _permitBasisDate;
    
    // TODO: Missing db fields facility_dsc and compliance_due_date_cd
    
    public TVPermit() {
        super();
        setPermitType(PermitTypeDef.TV_PTO);
        this.requiredField(_usepaOutcomeCd, "usepaOutcomeCd");
        setDirty(false);
    }

    /**
     * @param old
     */
    public TVPermit(TVPermit old) {

        super(old);
        if (old != null) {
            ///setPppReviewWaived(old.isPppReviewWaived());
            setUsepaExpedited(old.isUsepaExpedited());
            setUsepaOutcomeCd(old.getUsepaOutcomeCd());
            setUsepaPermitSentDate(old.getUsepaPermitSentDate());
            // order of the next two are important ... must set received date before setting complete date
            // since the setter of the first calls the setter of the second
            setUsepaReceivedPermitDate(old.getUsepaReceivedPermitDate());
            setUsepaCompleteDate(old.getUsepaCompleteDate());
            setRecissionDate(old.getRecissionDate());
            setAcidRain(old.isAcidRain());
            setAcidDesc(old.getAcidDesc());
            setCam(old.isCam());
            setCamDesc(old.getCamDesc());
            setSec112(old.isSec112());
            setSec112Desc(old.getSec112Desc());
            setPermitBasisDate(old.getPermitBasisDate());
            
            setDirty(old.isDirty());
        }
    }

    public final void populate(ResultSet rs) {

        try {
            super.populate(rs);

            //setPppReviewWaived(AbstractDAO.translateIndicatorToBoolean(rs
            //        .getString("PPP_REVIEW_WAIVED")));
            setUsepaExpedited(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("USEPA_EXPEDITED_FLAG")));
            setUsepaOutcomeCd(rs.getString("USEPA_OUTCOME_CD"));
            setUsepaPermitSentDate(rs.getTimestamp("USEPA_PERMIT_SENT_DATE"));
            // order of the next two are important ... must set received date before setting complete date
            // since the setter of the first calls the setter of the second
            setUsepaReceivedPermitDate(rs.getTimestamp("USEPA_RECEIVED_PERMIT_DATE"));
            setUsepaCompleteDate(rs.getTimestamp("USEPA_COMPLETE_DATE"));
            setRecissionDate(rs.getTimestamp("RECISSION_DATE"));
            setAcidRain(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("ACID_RAIN_FLAG")));
            setAcidDesc(rs.getString("ACID_DESC"));
            setCam(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("CAM_FLAG")));
            setCamDesc(rs.getString("CAM_DESC"));
            setSec112(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("SEC_112_FLAG")));
            setSec112Desc(rs.getString("SEC_112_DESC"));
            setPermitBasisDate(rs.getTimestamp("PERMIT_BASIS_DATE"));

            setLastModified(AbstractDAO.getInteger(rs, "ptp_lm"));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public final String getAcidDesc() {
        return _acidDesc;
    }

    public final void setAcidDesc(String acidDesc) {
        _acidDesc = acidDesc;
        setDirty(true);
    }

    public final boolean isAcidRain() {
        return _acidRain;
    }

    public final void setAcidRain(boolean acidRain) {
        _acidRain = acidRain;
        setDirty(true);
    }

    public final boolean isCam() {
        return _cam;
    }

    public final void setCam(boolean cam) {
        _cam = cam;
        setDirty(true);
    }

    public final String getCamDesc() {
        return _camDesc;
    }

    public final void setCamDesc(String camDesc) {
        _camDesc = camDesc;
        setDirty(true);
    }

    //public final boolean isPppReviewWaived() {
    //    return _pppReviewWaived;
    //}

    //public final void setPppReviewWaived(boolean pppReviewWaived) {
    //    _pppReviewWaived = pppReviewWaived;
    //    setDirty(true);
    //}

    public final boolean isSec112() {
        return _sec112;
    }

    public final void setSec112(boolean sec112) {
        _sec112 = sec112;
        setDirty(true);
    }

    public final String getSec112Desc() {
        return _sec112Desc;
    }

    public final void setSec112Desc(String sec112Desc) {
        _sec112Desc = sec112Desc;
        setDirty(true);
    }

    public final Timestamp getUsepaCompleteDate() {
        return _usepaCompleteDate;
    }

    public final void setUsepaCompleteDate(Timestamp usepaCompleteDate) {
        _usepaCompleteDate = usepaCompleteDate;
        setDirty(true);
    }
    
    public final Timestamp getRecissionDate() {
        return _recissionDate;
    }

    public final void setRecissionDate(Timestamp recissionDate) {
        _recissionDate = recissionDate;
        setDirty(true);
    }

    public final Timestamp getUsepaReceivedPermitDate() {
        return _usepaReceivedPermitDate;
    }

    public final void setUsepaReceivedPermitDate(Timestamp usepaReceivedPermitDate) {
        
    	_usepaReceivedPermitDate = usepaReceivedPermitDate;
    	// set pp publication date to the date US EPA received proposed permit
    	setPpIssueDate(usepaReceivedPermitDate);
    	
    	if (usepaReceivedPermitDate != null) {
            Duration d = new Duration();
            d.setDays(45);
            try {
                Timestamp tPlusFortyFive = 
                    new Timestamp(d.addToDate(new Date(usepaReceivedPermitDate.getTime())).getTime());
                setUsepaCompleteDate(tPlusFortyFive);
            }
            catch (Exception e) {
                setUsepaCompleteDate(null);
                e.printStackTrace();
            }
        }
        setDirty(true);
    }
    
    public final Timestamp getUsepaPermitSentDate() {
        return _usepaPermitSentDate;
    }

    public final void setUsepaPermitSentDate(Timestamp usepaPermitSentDate) {
    	_usepaPermitSentDate = usepaPermitSentDate;
    }
    
    public final boolean isUsepaExpedited() {
        return _usepaExpedited;
    }

    public final void setUsepaExpedited(boolean usepaExpedited) {
        _usepaExpedited = usepaExpedited;
        setDirty(true);
    }

    public final String getUsepaOutcomeCd() {
        return _usepaOutcomeCd;
    }

    public final void setUsepaOutcomeCd(String usepaOutcomeCd) {
        _usepaOutcomeCd = usepaOutcomeCd;
        if (USEPAOutcomeDef.isValid(_usepaOutcomeCd)) {
            validationMessages.remove("usepaOutcomeCd");
        } else {
            ValidationMessage msg = new ValidationMessage("usepaOutcomeCd",
                    "Illegal value for US EPA outcome code.");
            validationMessages.put("usepaOutcomeCd", msg);
        }
        setDirty(true);
    }

    public final PermitIssuance getPpIssuance() {
        return getPermitIssuances(PermitIssuanceTypeDef.PP);
    }

    //public final PermitIssuance getPppIssuance() {
    //    return getPermitIssuances(PermitIssuanceTypeDef.PPP);
    //}

    //public final String getPppIssuanceStatusCd() {
    //    return getIssuanceStatusCd(PermitIssuanceTypeDef.PPP);
    //}

    //public final void setPppIssuanceStatusCd(String issuanceStatusCd) {
    //    setIssuanceStatusCd(issuanceStatusCd, PermitIssuanceTypeDef.PPP);
    //    setDirty(true);
    //}

    //public final Timestamp getPppIssueDate() {
    //    return getIssueDate(PermitIssuanceTypeDef.PPP);
    //}

    //public final void setPppIssueDate(Timestamp issuanceDate) {
    //    setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.PPP);
    //   setDirty(true);
    //}

    public final String getPpIssuanceStatusCd() {
        return getIssuanceStatusCd(PermitIssuanceTypeDef.PP);
    }

    public final void setPpIssuanceStatusCd(String issuanceStatusCd) {
        setIssuanceStatusCd(issuanceStatusCd, PermitIssuanceTypeDef.PP);
        setDirty(true);
    }

    public final Timestamp getPpIssueDate() {
        return getIssueDate(PermitIssuanceTypeDef.PP);
    }

    public final void setPpIssueDate(Timestamp issuanceDate) {
        setIssuanceDate(issuanceDate, PermitIssuanceTypeDef.PP);
        setDirty(true);
    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();

        //result = PRIME * result + (_pppReviewWaived ? 1 : 0);
        result = PRIME * result + (_usepaExpedited ? 1 : 0);
        result = PRIME * result
                + ((_usepaOutcomeCd == null) ? 0 : _usepaOutcomeCd.hashCode());
        result = PRIME
                * result
                + ((_usepaCompleteDate == null) ? 0 : _usepaCompleteDate
                        .hashCode());
        result = PRIME * result
                + ((_recissionDate == null) ? 0 : _recissionDate.hashCode());
        result = PRIME * result + (_acidRain ? 1 : 0);
        result = PRIME * result
                + ((_acidDesc == null) ? 0 : _acidDesc.hashCode());
        result = PRIME * result + (_cam ? 1 : 0);
        result = PRIME * result
                + ((_camDesc == null) ? 0 : _camDesc.hashCode());
        result = PRIME * result + (_sec112 ? 1 : 0);
        result = PRIME * result
                + ((_sec112Desc == null) ? 0 : _sec112Desc.hashCode());
        result = PRIME
                * result
                + ((_usepaReceivedPermitDate == null) ? 0 : _usepaReceivedPermitDate
                        .hashCode());
        result = PRIME * result
                + ((_usepaPermitSentDate == null) ? 0 : _usepaPermitSentDate
                        .hashCode());
        result = PRIME * result
                + ((_permitBasisDate == null) ? 0 : _permitBasisDate
                        .hashCode());
        
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

        final TVPermit other = (TVPermit) obj;

        //if ((_pppReviewWaived != other.isPppReviewWaived())
        //        || (_usepaExpedited != other.isUsepaExpedited())
        //        || (_acidRain != other.isAcidRain()) || (_cam != other.isCam())
        //        || (_sec112 != other.isSec112())) {
        //    return false;
        //}

        // Either both null or equal values.
        if (((_usepaOutcomeCd == null) && (other.getUsepaOutcomeCd() != null))
                || ((_usepaOutcomeCd != null) && (other.getUsepaOutcomeCd() == null))
                || ((_usepaOutcomeCd != null)
                        && (other.getUsepaOutcomeCd() != null) && !(_usepaOutcomeCd
                        .equals(other.getUsepaOutcomeCd())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_usepaCompleteDate == null) && (other.getUsepaCompleteDate() != null))
                || ((_usepaCompleteDate != null) && (other
                        .getUsepaCompleteDate() == null))
                || ((_usepaCompleteDate != null)
                        && (other.getUsepaCompleteDate() != null) && !(_usepaCompleteDate
                        .equals(other.getUsepaCompleteDate())))) {
            return false;
        }
        
        // Either both null or equal values.
        if (((_recissionDate == null) && (other.getRecissionDate() != null))
                || ((_recissionDate != null) && (other
                        .getRecissionDate() == null))
                || ((_recissionDate != null)
                        && (other.getRecissionDate() != null) && !(_recissionDate
                        .equals(other.getRecissionDate())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_acidDesc == null) && (other.getAcidDesc() != null))
                || ((_acidDesc != null) && (other.getAcidDesc() == null))
                || ((_acidDesc != null) && (other.getAcidDesc() != null) && !(_acidDesc
                        .equals(other.getAcidDesc())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_camDesc == null) && (other.getCamDesc() != null))
                || ((_camDesc != null) && (other.getCamDesc() == null))
                || ((_camDesc != null) && (other.getCamDesc() != null) && !(_camDesc
                        .equals(other.getCamDesc())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_sec112Desc == null) && (other.getSec112Desc() != null))
                || ((_sec112Desc != null) && (other.getSec112Desc() == null))
                || ((_sec112Desc != null) && (other.getSec112Desc() != null) && !(_sec112Desc
                        .equals(other.getSec112Desc())))) {
            return false;
        }
        
        // Either both null or equal values.
        if (((_usepaReceivedPermitDate == null) && (other.getUsepaReceivedPermitDate() != null))
                || ((_usepaReceivedPermitDate != null) && (other
                        .getUsepaReceivedPermitDate() == null))
                || ((_usepaReceivedPermitDate != null)
                        && (other.getUsepaReceivedPermitDate() != null) && !(_usepaReceivedPermitDate
                        .equals(other.getUsepaReceivedPermitDate())))) {
            return false;
        }
        
        // Either both null or equal values.
        if (((_usepaPermitSentDate == null) && (other.getUsepaPermitSentDate() != null))
                || ((_usepaPermitSentDate != null) && (other
                        .getUsepaPermitSentDate() == null))
                || ((_usepaPermitSentDate != null)
                        && (other.getUsepaPermitSentDate() != null) && !(_usepaPermitSentDate
                        .equals(other.getUsepaPermitSentDate())))) {
            return false;
        }
        
     // Either both null or equal values.
        if (((_permitBasisDate == null) && (other.getPermitBasisDate() != null))
                || ((_permitBasisDate != null) && (other
                        .getPermitBasisDate() == null))
                || ((_permitBasisDate != null)
                        && (other.getPermitBasisDate() != null) && !(_permitBasisDate
                        .equals(other.getPermitBasisDate())))) {
            return false;
        }

        return true;
    } // END: public final boolean equals(Object obj)

    public final Integer getRevocationAppIDtoEUs() {
        for (PermitEUGroup peg : getEuGroups())
            for (PermitEU pe : peg.getPermitEUs())
                if (pe != null)
                    return pe.getRevocationApplicationID();
        return null;
    }

    public final void setRevocationAppIDtoEUs(Integer revocationAppIDtoEUs) {
        for (PermitEUGroup eg : getEuGroups())
            for (PermitEU eu : eg.getPermitEUs())
                eu.setRevocationApplicationID(revocationAppIDtoEUs);
    }
    
    public final Timestamp getRevocationDate(){
        for (PermitEUGroup peg : getEuGroups())
            for (PermitEU pe : peg.getPermitEUs())
                if (pe != null)
                    return pe.getRevocationDate();
        return null;
    }
    
    public final void setRevocationDate(Timestamp revocationDate) {
        
        for (PermitEUGroup eg : getEuGroups())
            for (PermitEU eu : eg.getPermitEUs())
                eu.setRevocationDate(revocationDate);
    }
    
    public final Integer getSupersededPermitID() {
        for (PermitEUGroup peg : getEuGroups())
            for (PermitEU pe : peg.getPermitEUs())
                if (pe != null)
                    return pe.getSupersededPermitID();
        return null;
    }

    public final void setSupersededPermitID(Integer supersededPermitID) {
        
        for (PermitEUGroup eg : getEuGroups())
            for (PermitEU eu : eg.getPermitEUs())
                eu.setSupersededPermitID(supersededPermitID);
    }
    
    public final Timestamp getSupersededDate(){
        for (PermitEUGroup peg : getEuGroups())
            for (PermitEU pe : peg.getPermitEUs())
                if (pe != null)
                    return pe.getSupersededDate();
        return null;
    }
    
    public final void setSupersededDate(Timestamp supersededDate) {
        
        for (PermitEUGroup eg : getEuGroups())
            for (PermitEU eu : eg.getPermitEUs())
                eu.setSupersededDate(supersededDate);
    }

	public Timestamp getPermitBasisDate() {
		return _permitBasisDate;
	}

	public void setPermitBasisDate(Timestamp _permitBasisDate) {
		this._permitBasisDate = _permitBasisDate;
	}
}

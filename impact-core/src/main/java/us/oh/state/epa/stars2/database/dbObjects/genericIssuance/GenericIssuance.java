
package us.oh.state.epa.stars2.database.dbObjects.genericIssuance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.document.CorrespondenceDocument;

public class GenericIssuance extends BaseDB {
    private Integer   _issuanceId;
    private String    _issuanceTypeCd;
    private String    _facilityId;
    private Integer   _applicationId;
    private Integer   _permitId;
    private Timestamp _issuanceDate;
    private boolean   _issued;
    private String    _publicNoticeText;
    private CorrespondenceDocument _issuanceDoc;
    private Integer   _issuanceDocId;
    private CorrespondenceDocument _addrLabelDoc;
    private CorrespondenceDocument _faxDoc;
    private Integer   _addrLabelDocId;
    private Integer   _faxDocId;
    private Float     _feeAmount;
    
    public GenericIssuance() {
        super();
        requiredField(_issuanceTypeCd, "issuanceTypeCd", "Issuance Type");
        setDirty(false);
    }

    public GenericIssuance(GenericIssuance old) {

        super(old);
        setDirty(false);

        if (old != null) {
            setIssuanceId(old.getIssuanceId());
            setIssuanceTypeCd(old.getIssuanceTypeCd());
            setFacilityId(old.getFacilityId());
            setApplicationId(old.getApplicationId());
            setPermitId(old.getPermitId());
            setIssuanceDate(old.getIssuanceDate());
            setIssued(old.isIssued());
            setPublicNoticeText(old.getPublicNoticeText());
            setIssuanceDoc(old.getIssuanceDoc());
            setIssuanceDocId(old.getIssuanceDocId());
            setAddrLabelDoc(old.getAddrLabelDoc());
            setAddrLabelDocId(old.getAddrLabelDocId());
            setFeeAmount(old.getFeeAmount());
            setLastModified(old.getLastModified());
            setDirty(old.isDirty());
        }

    } // END: public GenericIssuance(GenericIssuance old)

    public void populate(ResultSet rs) {

        try {
            setIssuanceId(AbstractDAO.getInteger(rs, "issuance_id"));
            setIssuanceTypeCd(rs.getString("issuance_type_cd"));
            setFacilityId(rs.getString("facility_id"));
            setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setIssuanceDate(rs.getTimestamp("issuance_date"));
            setIssued(AbstractDAO.translateIndicatorToBoolean(rs.getString("issued_flag")));
            setPublicNoticeText(rs.getString("public_notice_text"));
            setIssuanceDocId(AbstractDAO.getInteger(rs, "document_id"));
            setAddrLabelDocId(AbstractDAO.getInteger(rs, "addr_doc_id"));
            setFeeAmount(AbstractDAO.getFloat(rs, "fee_amount"));

            setLastModified(AbstractDAO.getInteger(rs, "gi_lm"));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
        finally {
            newObject = false;
        }
        
    } // END: public void populate(ResultSet rs)

    public Integer getIssuanceId() {
        return _issuanceId;
    }

    public void setIssuanceId(Integer issuanceId) {
        _issuanceId = issuanceId;
        setDirty(true);
    }

    public String getIssuanceTypeCd() {
        return _issuanceTypeCd;
    }

    public void setIssuanceTypeCd(String issuanceTypeCd) {
        _issuanceTypeCd = issuanceTypeCd;
        requiredField(_issuanceTypeCd, "issuanceTypeCd", "Issuance Type");
        setDirty(true);
    }

    public String getFacilityId() {
        return _facilityId;
    }

    public void setFacilityId(String facilityId) {
        _facilityId = facilityId;
        setDirty(true);
    }

    public Integer getApplicationId() {
        return _applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        _applicationId = applicationId;
        setDirty(true);
    }

    public Integer getPermitId() {
        return _permitId;
    }

    public void setPermitId(Integer permitId) {
        _permitId = permitId;
        setDirty(true);
    }

    public Timestamp getIssuanceDate() {
        return _issuanceDate;
    }

    public void setIssuanceDate(Timestamp issuanceDate) {
        _issuanceDate = issuanceDate;
        setDirty(true);
    }

    public boolean isIssued() {
        return _issued;
    }

    public void setIssued(boolean isIssued) {
        _issued = isIssued;
        setDirty(true);
    }

    public final String getPublicNoticeText() {
        return _publicNoticeText;
    }

    public final void setPublicNoticeText(String publicNoticeText) {
        _publicNoticeText = publicNoticeText;
        setDirty(true);
    }

    public final CorrespondenceDocument getIssuanceDoc() {
        return _issuanceDoc;
    }
    
    public final String getIssuanceDocDesc() {
        String ret = "";
        if (_issuanceDoc != null){
            ret = _issuanceDoc.getDescription();
        }
        return ret;
    }

    /** Setting the Document will also set the IssuanceDocId. */
    public final void setIssuanceDoc(CorrespondenceDocument issuanceDoc) {
        _issuanceDoc = issuanceDoc;
        if (issuanceDoc == null) {
            _issuanceDocId = null;
        }
        setDirty(true);
    }

    public Integer getIssuanceDocId() {
        return _issuanceDocId;
    }

    /** Called only by populate(). */
    private void setIssuanceDocId(Integer issuanceDocId) {
        _issuanceDocId = issuanceDocId;
        setDirty(true);
    }

    public final CorrespondenceDocument getAddrLabelDoc() {
        return _addrLabelDoc;
    }
    
    /** Setting the Document will also set the AddrLabelDocId. */
    public final void setAddrLabelDoc(CorrespondenceDocument addrLabelDoc) {
        _addrLabelDoc = addrLabelDoc;
        if (addrLabelDoc == null) {
            _addrLabelDocId = null;
        }
        setDirty(true);
    }
    
    /**  Used for ITR only. Setting the Document will also set the AddrLabelDocId. */
    public final void setFaxDoc(CorrespondenceDocument faxDoc) {
        _faxDoc = faxDoc;
        if (faxDoc == null) {
            _faxDocId = null;
        }
        setDirty(true);
    }

    public Integer getAddrLabelDocId() {
        return _addrLabelDocId;
    }

    /** Called only by populate(). */
    private void setAddrLabelDocId(Integer addrLabelDocId) {
        _addrLabelDocId = addrLabelDocId;
        setDirty(true);
    }
    
    public Float getFeeAmount() {
        return _feeAmount;
    }

    public void setFeeAmount(Float feeAmount) {
        _feeAmount = feeAmount;
        setDirty(true);
    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();
        
        result = PRIME * result
            + ((_issuanceId == null) ? 0 : _issuanceId.hashCode());
        result = PRIME * result
            + ((_issuanceTypeCd == null) ? 0 : _issuanceTypeCd.hashCode());
        result = PRIME * result
            + ((_facilityId == null) ? 0 : _facilityId.hashCode());
        result = PRIME * result
            + ((_applicationId == null) ? 0 : _applicationId.hashCode());
        result = PRIME * result
            + ((_permitId == null) ? 0 : _permitId.hashCode());
        result = PRIME * result
            + ((_issuanceDate == null) ? 0 : _issuanceDate.hashCode());
        result = PRIME * result
            + (_issued ? 0 : 1);
        result = PRIME * result
            + ((_publicNoticeText == null) ? 0 : _publicNoticeText.hashCode());
        result = PRIME * result
            + ((_issuanceDoc == null) ? 0 : _issuanceDoc.hashCode());
        result = PRIME * result
            + ((_addrLabelDoc == null) ? 0 : _addrLabelDoc.hashCode());
        result = PRIME * result
            + ((_feeAmount == null) ? 0 : _feeAmount.hashCode());

        return result;

    } // END: public int hashCode()

    @Override
    public boolean equals(Object obj) {

        if ((obj == null) || !(super.equals(obj))
            || (getClass() != obj.getClass())) {
            return false;
        }
        
        if (this == obj) {
            return true;
        }
        
        final GenericIssuance other = (GenericIssuance) obj;
        
        if (_issued != other.isIssued()) {
            return false;
        }

        // Either both null or equal values.
        if (((_issuanceId == null) && (other.getIssuanceId() != null))
            || ((_issuanceId != null) && (other.getIssuanceId() == null))
            || ((_issuanceId != null) && (other.getIssuanceId() != null) 
                && !(_issuanceId.equals(other.getIssuanceId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_issuanceTypeCd == null) && (other.getIssuanceTypeCd() != null))
            || ((_issuanceTypeCd != null) && (other.getIssuanceTypeCd() == null))
            || ((_issuanceTypeCd != null) && (other.getIssuanceTypeCd() != null) 
                && !(_issuanceTypeCd.equals(other.getIssuanceTypeCd())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_facilityId == null) && (other.getFacilityId() != null))
            || ((_facilityId != null) && (other.getFacilityId() == null))
            || ((_facilityId != null) && (other.getFacilityId() != null) 
                && !(_facilityId.equals(other.getFacilityId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_applicationId == null) && (other.getApplicationId() != null))
            || ((_applicationId != null) && (other.getApplicationId() == null))
            || ((_applicationId != null) && (other.getApplicationId() != null) 
                && !(_applicationId.equals(other.getApplicationId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_permitId == null) && (other.getPermitId() != null))
            || ((_permitId != null) && (other.getPermitId() == null))
            || ((_permitId != null) && (other.getPermitId() != null) 
                && !(_permitId.equals(other.getPermitId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_issuanceDate == null) && (other.getIssuanceDate() != null))
            || ((_issuanceDate != null) && (other.getIssuanceDate() == null))
            || ((_issuanceDate != null) && (other.getIssuanceDate() != null) 
                && !(_issuanceDate.equals(other.getIssuanceDate())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_publicNoticeText == null) && (other.getPublicNoticeText() != null))
            || ((_publicNoticeText != null) && (other.getPublicNoticeText() == null))
            || ((_publicNoticeText != null) && (other.getPublicNoticeText() != null) 
                && !(_publicNoticeText.equals(other.getPublicNoticeText())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_issuanceDoc == null) && (other.getIssuanceDoc() != null))
            || ((_issuanceDoc != null) && (other.getIssuanceDoc() == null))
            || ((_issuanceDoc != null) && (other.getIssuanceDoc() != null) 
                && !(_issuanceDoc.equals(other.getIssuanceDoc())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_addrLabelDoc == null) && (other.getAddrLabelDoc() != null))
            || ((_addrLabelDoc != null) && (other.getAddrLabelDoc() == null))
            || ((_addrLabelDoc != null) && (other.getAddrLabelDoc() != null) 
                && !(_addrLabelDoc.equals(other.getAddrLabelDoc())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_feeAmount == null) && (other.getFeeAmount() != null))
            || ((_feeAmount != null) && (other.getFeeAmount() == null))
            || ((_feeAmount != null) && (other.getFeeAmount() != null) 
                && !(_feeAmount.equals(other.getFeeAmount())))) {
            return false;
        }

        return true;

    } // END: public final boolean equals(Object obj)

} // END: public class GenericIssuance extends BaseDB

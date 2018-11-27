package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.def.PermitStatusDef;

@SuppressWarnings("serial")
public class PermitEU extends BaseDB {

    private Integer permitEUID;
    private Integer permitEUGroupID;
    private Integer facilityEUID;
    private Integer corrEpaEMUID;
    private EmissionUnit fpEU;
    private String permitNumber;
    private Timestamp effectiveDate;
    private Integer batID;
    private Integer euFeeID;
    private List<EUFee> euFees;
    private String permitStatusCd;
    private String generalPermitTypeCd;
    private String modelGeneralPermitCd;
    private String dapcDescription;
    private String companyId;
    private Integer revocationApplicationID;
    private Timestamp revocationDate;
    private Integer extensionApplicationID;
    private Timestamp extensionDate;
    private Timestamp terminatedDate;
    private Integer supersededPermitID;
    private String supersededPermitNbr;
    private Timestamp supersededDate;
    private PermitEUGroup euGroup;
    private boolean toBeDeleted = false;
    
    private transient boolean activeWithoutPBR;  //  Used only in Permit.PermitValidation.validatePBRRevocation() to ignore EUs active because of PBRs. 

    /**
     * 
     */
    public PermitEU() {

        super();
        validationMessages.put("permitEUGroupID", 
                               new ValidationMessage("Permit EU Group ID", "Missing permit emission unit group id.",
                                                     ValidationMessage.Severity.WARNING));

        validationMessages.put("facilityEUID", 
                               new ValidationMessage("Facility EU ID", "Missing emission unit id.",
                                                     ValidationMessage.Severity.WARNING));

        validationMessages.put("corrEpaEMUID",
                               new ValidationMessage("Correlated EPA EU ID", "Missing emission unit id.",
                                                     ValidationMessage.Severity.WARNING));
        validationMessages.put("permitStatusCD", 
                               new ValidationMessage("Permit Status Code", "Missing permit status code.",
                                                     ValidationMessage.Severity.ERROR));
        setDirty(false);
    }

    /**
     * @param old
     */
    public PermitEU(PermitEU old) {

        super(old);
        if (old != null) {
            setPermitEUID(old.getPermitEUID());
            setPermitEUGroupID(old.getPermitEUGroupID());
            setEuGroup(old.getEuGroup());
            setFacilityEUID(old.getFacilityEUID());
            setCorrEpaEMUID(old.getCorrEpaEMUID());
            setFpEU(old.getFpEU());
            setPermitNumber(old.getPermitNumber());
            setBatID(old.getBatID());
            setEUFeeID(old.getEUFeeID());
            setEuFee(old.getEuFee());
            setPermitStatusCd(old.getPermitStatusCd());
            setGeneralPermitTypeCd(old.getGeneralPermitTypeCd());
            setModelGeneralPermitCd(old.getModelGeneralPermitCd());
            setDapcDescription(old.getDapcDescription());
            setCompanyId(old.getCompanyId());
            setRevocationApplicationID(old.getRevocationApplicationID());
            setRevocationDate(old.getRevocationDate());
            setExtensionApplicationID(old.getExtensionApplicationID());
            setExtensionDate(old.getExtensionDate());
            setTerminatedDate(old.getTerminatedDate());
            setSupersededPermitID(old.getSupersededPermitID());
            setSupersededPermitNbr(old.getSupersededPermitNbr());
            setSupersededDate(old.getSupersededDate());
            setLastModified(old.getLastModified());
            setDirty(old.isDirty());
        }
    }

    public void populate(ResultSet rs) {

        try {
            setPermitEUID(AbstractDAO.getInteger(rs, "permit_eu_id"));
            setPermitEUGroupID(AbstractDAO.getInteger(rs, "permit_eu_group_id"));
            setFacilityEUID(AbstractDAO.getInteger(rs, "facility_eu_id"));
            setCorrEpaEMUID(AbstractDAO.getInteger(rs, "corr_epa_emu_id"));
            setBatID(AbstractDAO.getInteger(rs, "bat_id"));
            setEUFeeID(AbstractDAO.getInteger(rs, "eu_fee_id"));
            setPermitStatusCd(rs.getString("permit_status_cd"));
            setGeneralPermitTypeCd(rs.getString("general_permit_type_cd"));
            setModelGeneralPermitCd(rs.getString("model_general_permit_cd"));
            setDapcDescription(rs.getString("eu_dapc_dsc"));
            setCompanyId(rs.getString("eu_company_id"));
            setRevocationApplicationID(AbstractDAO.getInteger(rs, "revocation_application_id"));
            setRevocationDate(rs.getTimestamp("revocation_dt"));
            setExtensionApplicationID(AbstractDAO.getInteger(rs, "extension_application_id"));
            setExtensionDate(rs.getTimestamp("extension_dt"));
            setTerminatedDate(rs.getTimestamp("terminated_dt"));
            setSupersededPermitID(AbstractDAO.getInteger(rs, "superseded_permit_id"));
            setSupersededDate(rs.getTimestamp("superseded_dt"));
            setLastModified(AbstractDAO.getInteger(rs, "pe_lm"));
            setSupersededPermitNbr(rs.getString("permit_nbr"));

            try {
                fpEU = new EmissionUnit();
                fpEU.populate(rs);
                
                euFees = new ArrayList<EUFee>(1);
                EUFee fee = new EUFee();
                fee.populate(rs);
                euFees.add(fee);
            }
            catch (SQLException sqle) {
                logger.debug("Optional field error: " + sqle.getMessage());
            }

            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
        finally {
            newObject = false;
        }

    }

    public final Integer getPermitEUID() {
        return permitEUID;
    }

    public final void setPermitEUID(Integer permitEUID) {
        this.permitEUID = permitEUID;
        setDirty(true);
    }

    public final Integer getPermitEUGroupID() {
        return permitEUGroupID;
    }

    public final void setPermitEUGroupID(Integer permitEUGroupID) {
        this.permitEUGroupID = permitEUGroupID;
        if (this.permitEUGroupID == null) {
            validationMessages.put("permitEUGroupID", 
                                   new ValidationMessage("Permit EU Group ID", "Missing value.",
                                                         ValidationMessage.Severity.WARNING));
        }
        else {
            validationMessages.remove("permitEUGroupID");
        }
        setDirty(true);
    }

    public final PermitEUGroup getEuGroup() {
        return euGroup;
    }

    public final void setEuGroup(PermitEUGroup euGroup) {
        this.euGroup = euGroup;
        setPermitEUGroupID(euGroup.getPermitEUGroupID());
    }

    public final Integer getFacilityEUID() {
        return facilityEUID;
    }

    public final void setFacilityEUID(Integer facilityEUID) {
        this.facilityEUID = facilityEUID;
        if (this.facilityEUID == null) {
            validationMessages.put("facilityEUID", 
                                   new ValidationMessage("Facility EU ID", "Missing value.",
                                                         ValidationMessage.Severity.WARNING));
        }
        else {
            validationMessages.remove("facilityEUID");
        }
        setDirty(true);
    }

    public final Integer getCorrEpaEMUID() {
        return corrEpaEMUID;
    }

    public final void setCorrEpaEMUID(Integer corrEpaEMUID) {

        this.corrEpaEMUID = corrEpaEMUID;
        if (this.corrEpaEMUID == null) {
            validationMessages.put("corrEpaEMUID",
                                   new ValidationMessage("Correlated EPA EU ID", "Missing value.",
                                                         ValidationMessage.Severity.WARNING));
        } 
        else {
            validationMessages.remove("corrEpaEMUID");
        }
        setDirty(true);
    }

    public final EmissionUnit getFpEU() {
        return fpEU;
    }

    public final void setFpEU(EmissionUnit fpEU) {
        this.fpEU = fpEU;
        setFacilityEUID(fpEU.getEmuId());
        setCorrEpaEMUID(fpEU.getCorrEpaEmuId());
        setDirty(true);
    }

    /**
     * Used strictly for field audit log purpose. Also required for PER forms.
     */
    public String getPermitNumber() {
        return this.permitNumber;
    }

    /**
     * Used strictly for field audit log purpose. Also required for PER forms. 
     */
    public void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public final String getAuditID() {
        String auditID = getPermitNumber();
        if (getFpEU() != null) {
            auditID += " : " + getFpEU().getEpaEmuId();
        }
        return auditID;
    }

    public final Integer getBatID() {
        return batID;
    }

    public final void setBatID(Integer batID) {
        this.batID = batID;
        setDirty(true);
    }

    public final Integer getEUFeeID() {
        return euFeeID;
    }

    public final EUFee getEuFee() {
        if (euFees == null) {
            this.euFees = new ArrayList<EUFee>(1);
            euFees.add(new EUFee());
        }
        return euFees.get(0);
    }

    public final void setEUFeeID(Integer euFeeID) {
        this.euFeeID = euFeeID;
        setDirty(true);
    }

    public final List<EUFee> getEuFees() {
        if (euFees == null) {
            this.euFees = new ArrayList<EUFee>(1);
            euFees.add(new EUFee());
        } 
        return euFees;
    }

    public final void setEuFee(EUFee euFee) {
        if (euFees == null) {
            this.euFees = new ArrayList<EUFee>(0);
        }
        this.euFees.clear();
        this.euFees.add(euFee);
        setDirty(true);
    }

    public final String getPermitStatusCd() {
        return permitStatusCd;
    }

    public final void setPermitStatusCd(String code) {

        checkDirty("pesc", getAuditID(), getPermitStatusCd(), code);
        permitStatusCd = code;
        if (permitStatusCd == null) {
            validationMessages.put("permitStatusCD",
                                   new ValidationMessage("Permit Status Code", 
                                                         "Missing value for permit status code.",
                                                         ValidationMessage.Severity.ERROR));
        }
        else if (!PermitStatusDef.isValid(permitStatusCd)) {
            validationMessages.put("permitStatusCD", 
                                   new ValidationMessage("Permit Status Code", 
                                                         "Illegal value for permit status code.",
                                                         ValidationMessage.Severity.ERROR));
        }
        else {
            validationMessages.remove("permitStatusCD");
        }

        setDirty(true);
    }

    public final String getGeneralPermitTypeCd() {
        return generalPermitTypeCd;
    }

    public final void setGeneralPermitTypeCd(String code) {
        generalPermitTypeCd = code;
        setDirty(true);
    }

    public final String getModelGeneralPermitCd() {
        return modelGeneralPermitCd;
    }

    public final void setModelGeneralPermitCd(String code) {
        modelGeneralPermitCd = code;
        setDirty(true);
    }

    public final String getDapcDescription() {
        return dapcDescription;
    }

    public final void setDapcDescription(String description) {
        dapcDescription = description;
        setDirty(true);
    }

    public final String getCompanyId() {
        return companyId;
    }

    public final void setCompanyId(String id) {
        companyId = id;
        setDirty(true);
    }

    public final Integer getRevocationApplicationID() {
        return revocationApplicationID;
    }

    public final void setRevocationApplicationID(Integer applicationId) {
        revocationApplicationID = applicationId;
        setDirty(true);
    }

    public final Timestamp getRevocationDate() {
        return revocationDate;
    }

    public final void setEffectiveDate(Timestamp date) {
        effectiveDate = date;
    }

    public final Timestamp getEffectiveDate() {
        return effectiveDate;
    }

    public final void setRevocationDate(Timestamp date) {
        checkDirty("prvd", getAuditID(), getRevocationDate(), date);
        revocationDate = date;
        setDirty(true);
    }

    public final Integer getExtensionApplicationID() {
        return extensionApplicationID;
    }

    public final void setExtensionApplicationID(Integer applicationId) {
        extensionApplicationID = applicationId;
        setDirty(true);
    }

    public final Timestamp getExtensionDate() {
        return extensionDate;
    }

    public final void setExtensionDate(Timestamp date) {
        extensionDate = date;
        setDirty(true);
    }

    public final Timestamp getTerminatedDate() {
        return terminatedDate;
    }

    public final void setTerminatedDate(Timestamp date) {
        checkDirty("ptmd", getAuditID(), getTerminatedDate(), date);
        terminatedDate = date;
        setDirty(true);
    }

    public final Integer getSupersededPermitID() {
        return supersededPermitID;
    }

    public final void setSupersededPermitID(Integer supersededPermitID) {
        this.supersededPermitID = supersededPermitID;
        setDirty(true);
    }

    public final String getSupersededPermitNbr() {
        return supersededPermitNbr;
    }

    public final void setSupersededPermitNbr(String supersededPermitNbr) {
        this.supersededPermitNbr = supersededPermitNbr;
        setDirty(true);
    }

    public final Timestamp getSupersededDate() {
        return supersededDate;
    }

    public final void setSupersededDate(Timestamp date) {
        supersededDate = date;
        setDirty(true);
    }

    public final boolean isToBeDeleted() {
        return toBeDeleted;
    }

    public final void setToBeDeleted(boolean delete) {
        toBeDeleted = delete;
    }

    @Override
        public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
            + ((permitEUID == null) ? 0 : permitEUID.hashCode());
        result = PRIME * result
            + ((permitEUGroupID == null) ? 0 : permitEUGroupID.hashCode());
        result = PRIME * result
            + ((facilityEUID == null) ? 0 : facilityEUID.hashCode());
        result = PRIME * result + ((fpEU == null) ? 0 : fpEU.hashCode());
        result = PRIME * result + ((batID == null) ? 0 : batID.hashCode());
        result = PRIME * result + ((euFeeID == null) ? 0 : euFeeID.hashCode());
        result = PRIME * result
            + ((permitStatusCd == null) ? 0 : permitStatusCd.hashCode());
        result = PRIME * result
            + ((generalPermitTypeCd == null) ? 0 : generalPermitTypeCd.hashCode());
        result = PRIME * result
            + ((modelGeneralPermitCd == null) ? 0 : modelGeneralPermitCd.hashCode());
        result = PRIME * result
            + ((dapcDescription == null) ? 0 : dapcDescription.hashCode());
        result = PRIME * result
            + ((companyId == null) ? 0 : companyId.hashCode());
        result = PRIME * result
            + ((revocationApplicationID == null) ? 0 : revocationApplicationID.hashCode());
        result = PRIME * result
            + ((revocationDate == null) ? 0 : revocationDate.hashCode());
        result = PRIME * result
            + ((extensionApplicationID == null) ? 0 : extensionApplicationID.hashCode());
        result = PRIME * result
            + ((extensionDate == null) ? 0 : extensionDate.hashCode());
        result = PRIME * result
            + ((terminatedDate == null) ? 0 : terminatedDate.hashCode());
        result = PRIME * result
            + ((supersededPermitID == null) ? 0 : supersededPermitID.hashCode());
        result = PRIME * result
            + ((supersededDate == null) ? 0 : supersededDate.hashCode());
        
        if (euFees != null) {
            for (EUFee fee : euFees) {
                result = PRIME * result + fee.hashCode();
            }
        }

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

        final PermitEU other = (PermitEU) obj;

        // Either both null or equal values.
        if (((permitEUID == null) && (other.getPermitEUID() != null))
            || ((permitEUID != null) && (other.getPermitEUID() == null))
            || ((permitEUID != null) && (other.getPermitEUID() != null)
                && !(permitEUID.equals(other.getPermitEUID())))) {
                                                                             
            return false;
        }

        // Either both null or equal values.
        if (((permitEUGroupID == null) && (other.getPermitEUGroupID() != null))
            || ((permitEUGroupID != null) && (other.getPermitEUGroupID() == null))
            || ((permitEUGroupID != null)
                && (other.getPermitEUGroupID() != null) 
                && !(permitEUGroupID.equals(other.getPermitEUGroupID())))) {
                                                             
            return false;
        }

        // Either both null or equal values.
        if (((facilityEUID == null) && (other.getFacilityEUID() != null))
            || ((facilityEUID != null) && (other.getFacilityEUID() == null))
            || ((facilityEUID != null) && (other.getFacilityEUID() != null) 
                && !(facilityEUID.equals(other.getFacilityEUID())))) {
                                                                                 
            return false;
        }

        // Either both null or equal values.
        if (((batID == null) && (other.getBatID() != null))
            || ((batID != null) && (other.getBatID() == null))
            || ((batID != null) && (other.getBatID() != null) 
                && !(batID.equals(other.getBatID())))) {
                                                                   
            return false;
        }

        // Either both null or equal values.
        if (((euFeeID == null) && (other.getEUFeeID() != null))
            || ((euFeeID != null) && (other.getEUFeeID() == null))
            || ((euFeeID != null) && (other.getEUFeeID() != null) 
                && !(euFeeID.equals(other.getEUFeeID())))) {
                                                                       
            return false;
        }

        // Either both null or equal values.
        if (((permitStatusCd == null) && (other.getPermitStatusCd() != null))
            || ((permitStatusCd != null) && (other.getPermitStatusCd() == null))
            || ((permitStatusCd != null)
                && (other.getPermitStatusCd() != null) 
                && !(permitStatusCd.equals(other.getPermitStatusCd())))) {
                                                            
            return false;
        }

        // Either both null or equal values.
        if (((generalPermitTypeCd == null) && (other.getGeneralPermitTypeCd() != null))
            || ((generalPermitTypeCd != null) 
                && (other.getGeneralPermitTypeCd() == null))
            || ((generalPermitTypeCd != null)
                && (other.getGeneralPermitTypeCd() != null) 
                && !(generalPermitTypeCd.equals(other.getGeneralPermitTypeCd())))) {
                                                                 
            return false;
        }

        // Either both null or equal values.
        if (((modelGeneralPermitCd == null) && (other.getModelGeneralPermitCd() != null))
            || ((modelGeneralPermitCd != null) 
                && (other.getModelGeneralPermitCd() == null))
            || ((modelGeneralPermitCd != null)
                && (other.getModelGeneralPermitCd() != null) 
                && !(modelGeneralPermitCd.equals(other.getModelGeneralPermitCd())))) {
                                                                  
            return false;
        }

        // Either both null or equal values.
        if (((dapcDescription == null) && (other.getDapcDescription() != null))
            || ((dapcDescription != null) && (other.getDapcDescription() == null))
            || ((dapcDescription != null)
                && (other.getDapcDescription() != null) 
                && !(dapcDescription.equals(other.getDapcDescription())))) {
                                                             
            return false;
        }

        // Either both null or equal values.
        if (((companyId == null) && (other.getCompanyId() != null))
            || ((companyId != null) && (other.getCompanyId() == null))
            || ((companyId != null) && (other.getCompanyId() != null) 
                && !(companyId.equals(other.getCompanyId())))) {
                                                                           
            return false;
        }

        // Either both null or equal values.
        if (((revocationApplicationID == null) 
             && (other.getRevocationApplicationID() != null))
            || ((revocationApplicationID != null) 
                && (other.getRevocationApplicationID() == null))
            || ((revocationApplicationID != null)
                && (other.getRevocationApplicationID() != null) 
                && !(revocationApplicationID.equals(other.getRevocationApplicationID())))) {
                                                                     
            return false;
        }

        // Either both null or equal values.
        if (((revocationDate == null) && (other.getRevocationDate() != null))
            || ((revocationDate != null) && (other.getRevocationDate() == null))
            || ((revocationDate != null)
                && (other.getRevocationDate() != null) 
                && !(revocationDate.equals(other.getRevocationDate())))) {
                                                            
            return false;
        }

        // Either both null or equal values.
        if (((extensionApplicationID == null) 
             && (other.getExtensionApplicationID() != null))
            || ((extensionApplicationID != null) 
                && (other.getExtensionApplicationID() == null))
            || ((extensionApplicationID != null)
                && (other.getExtensionApplicationID() != null)
                && !(extensionApplicationID.equals(other.getExtensionApplicationID())))) {
                                                                    
            return false;
        }

        // Either both null or equal values.
        if (((extensionDate == null) && (other.getExtensionDate() != null))
            || ((extensionDate != null) && (other.getExtensionDate() == null))
            || ((extensionDate != null)
                && (other.getExtensionDate() != null) 
                && !(extensionDate.equals(other.getExtensionDate())))) {
                                                           
            return false;
        }

        // Either both null or equal values.
        if (((terminatedDate == null) && (other.getTerminatedDate() != null))
            || ((terminatedDate != null) && (other.getTerminatedDate() == null))
            || ((terminatedDate != null)
                && (other.getTerminatedDate() != null) 
                && !(terminatedDate.equals(other.getTerminatedDate())))) {
                                                            
            return false;
        }

        // Either both null or equal values.
        if (((supersededDate == null) && (other.getSupersededDate() != null))
            || ((supersededDate != null) && (other.getSupersededDate() == null))
            || ((supersededDate != null)
                && (other.getSupersededDate() != null) 
                && !(supersededDate.equals(other.getSupersededDate())))) {
                                                            
            return false;
        }

        // Either both null or equal values.
        if (((supersededPermitID == null) && (other.getSupersededPermitID() != null))
            || ((supersededPermitID != null) 
                && (other.getSupersededPermitID() == null))
            || ((supersededPermitID != null)
                && (other.getSupersededPermitID() != null) 
                && !(supersededPermitID.equals(other.getSupersededPermitID())))) {
                                                                
            return false;
        }

        // Each of our EUFees must have a corresponding EUFee in theirs and vice
        // versa.
        EUFee[] ours = getEuFees().toArray(new EUFee[0]);
        EUFee[] theirs = other.getEuFees().toArray(new EUFee[0]);
        for (int i = 0; i < ours.length; i++) {
            boolean found = false;
            for (int j = 0; j < theirs.length; j++) {
                if (theirs[j] == null) {
                    continue;
                }
                if (ours[i].equals(theirs[j])) {
                    found = true;
                    theirs[j] = null;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        for (int j = 0; j < theirs.length; j++) {
            if (theirs[j] != null) {
                return false;
            }
        }
        return true;
    } // END: public final boolean equals(Object obj)

    public boolean isActiveWithoutPBR() {
        return activeWithoutPBR;
    }

    public void setActiveWithoutPBR(boolean activeWithoutPBR) {
        this.activeWithoutPBR = activeWithoutPBR;
    }
}

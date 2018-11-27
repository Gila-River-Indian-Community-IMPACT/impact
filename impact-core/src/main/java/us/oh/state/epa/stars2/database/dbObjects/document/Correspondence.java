package us.oh.state.epa.stars2.database.dbObjects.document;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.CityDef;
import us.oh.state.epa.stars2.def.CorrespondenceCategoryDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.ResultSetHelper;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.App;


/** Transfer object container for correspondence history event. */
public class Correspondence extends BaseDB {

	private static final long serialVersionUID = 5922247951424693397L;

	@Autowired
	private FacilityService facilityService;
	
    private Integer _correspondenceID;
    private String _correspondenceTypeCode;
    private Timestamp _dateGenerated;
    private String _facilityID;
    private Integer _rumProcessID;
    private String _certifiedMailTrackId;
    private Timestamp _certifiedMailRcptDate;
    private String _correspondenceTypeDescription;
    private boolean _savedDocReqd;
    private Document _document;
    private String _additionalInfo;
    private ArrayList<Note> _notes;
    private String facilityNm;
    private Integer fpId;
    private Integer linkedToId;
    private String linkedToTypeCd;
    
    private String directionCd;
    // This is same as dolaaCd variable from other objects.
    private String district;
    private String correspondenceCategoryCd;
    private String regarding;
    private String toPerson;
    private String fromPerson;
    private Integer reviewerId;
    private Timestamp receiptDate;
    private boolean followUpAction;
    private Timestamp followUpActionDate;
    private String followUpActionDescription;
    private boolean legacyFlag;
    
    private String truncatedAdditionalInfo;
    
    private String companyName;
    private String companyId;
    
    private String countyCd;
    private String countyNm;
    private String cityCd;
    private String cityNm;
    
    private boolean linkedtoEnfAction;
    private boolean hideAttachments;
    
    private String corId;
    
    private Integer attachmentCount;
    private List<String> inspectionsReferencedIn;


	/** No-arg constructor. */
    public Correspondence() {
        super();
        setDirty(false);
    }

    /** Copy constructor. */
    public Correspondence(Correspondence old) {

        super(old);

        if (old != null) {
            setCorrespondenceID(old.getCorrespondenceID());
            setFacilityID(old.getFacilityID());
            setCorrespondenceTypeCode(old.getCorrespondenceTypeCode());
            setDateGenerated(old.getDateGenerated());
            setRUMProcessID(old.getRUMProcessID());
            setLastModified(old.getLastModified());
            addNotes(old.getNotes());
            setCertifiedMailTrackId(old.getCertifiedMailTrackId());
            setCertifiedMailRcptDate(old.getCertifiedMailRcptDate());
            setDocument(old.getDocument());
            setAdditionalInfo(old.getAdditionalInfo());
            setFpId(old.getFpId());
            setFacilityNm(old.getFacilityNm());
            setLinkedToId(old.getLinkedToId());
            
            setDirectionCd(old.getDirectionCd());
            setDistrict(old.getDistrict());
            setCorrespondenceCategoryCd(old.getCorrespondenceCategoryCd());
            setRegarding(old.getRegarding());
            setToPerson(old.getToPerson());
            setFromPerson(old.getFromPerson());
            setReviewerId(old.getReviewerId());
            setReceiptDate(old.getReceiptDate());
            setFollowUpAction(old.isFollowUpAction());
            setFollowUpActionDate(old.getFollowUpActionDate());
            setFollowUpActionDescription(old.getFollowUpActionDescription());
            setLegacyFlag(old.isLegacyFlag());
            setCompanyId(old.getCompanyId());
            setCompanyName(old.getCompanyName());
            setCountyCd(old.getCountyCd());
            setCountyNm(old.getCountyNm());
            setCityCd(old.getCityCd());
            setCityNm(old.getCityNm());
            
            if (old.isDirty()) {
                setDirty(true);
            }
            else {
                setDirty(false);
            }
            setDirty(old.isDirty());
            
            setLinkedtoEnfAction(old.isLinkedtoEnfAction());
            setHideAttachments(old.isHideAttachments());
            setCorId(old.getCorId());
        }

    }

    /** Populate this instance from a database ResultSet. */
    public final void populate(java.sql.ResultSet rs) {

        try {
            setCorrespondenceID(AbstractDAO.getInteger(rs, "correspondence_id"));
            setFacilityID(rs.getString("facility_id"));
            setCorrespondenceTypeCode(rs.getString("correspondence_type_cd"));
            setDateGenerated(rs.getTimestamp("date_generated"));
            setRUMProcessID(AbstractDAO.getInteger(rs, "rum_process_id"));
            setCertifiedMailTrackId(rs.getString("certified_mail_track_id"));
            setCertifiedMailRcptDate(rs.getTimestamp("certified_mail_recpt_date"));
            setAdditionalInfo(rs.getString("additional_info"));
            setLastModified(AbstractDAO.getInteger(rs, "cs_lm"));
            setLinkedToId(AbstractDAO.getInteger(rs, "linked_to_id"));
            setDirectionCd(rs.getString("direction_cd"));
            setDistrict(rs.getString("district"));
            setCorrespondenceCategoryCd(rs.getString("correspondence_category_cd"));
            setRegarding(rs.getString("regarding"));
            setToPerson(rs.getString("to_person"));
            setFromPerson(rs.getString("from_person"));
            setReviewerId(AbstractDAO.getInteger(rs, "reviewer"));
            setReceiptDate(rs.getTimestamp("receipt_date"));
            setFollowUpAction(AbstractDAO.translateIndicatorToBoolean(rs.getString("follow_up_action")));
            setFollowUpActionDate(rs.getTimestamp("follow_up_action_date"));
            setFollowUpActionDescription(rs.getString("follow_up_action_dsc"));
            setLegacyFlag(AbstractDAO.translateIndicatorToBoolean(rs.getString("legacy_flag")));
            setCompanyId(rs.getString("company_id"));
            setCompanyName(rs.getString("company_name"));
            setCountyCd(rs.getString("county_cd"));
			if (ResultSetHelper.hasDataColumn(rs, "county_nm")) {
				setCountyNm(rs.getString("county_nm"));
			}
			setCityCd(rs.getString("city_cd"));
			if (ResultSetHelper.hasDataColumn(rs, "city_nm")) {
				setCityNm(rs.getString("city_nm"));
			}
			
			setLinkedtoEnfAction(AbstractDAO.translateIndicatorToBoolean(rs.getString("linked_to_enf_action")));
			setHideAttachments(AbstractDAO.translateIndicatorToBoolean(rs.getString("hide_attachments")));
			setCorId(rs.getString("cor_id"));
           
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
        try {
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setFacilityNm(rs.getString("facility_nm"));
        }
        catch (SQLException sqle) {
            logger.warn("Optional field not found.");
        }
    }

    public FacilityService getFacilityService() {
    	if (facilityService == null) {
    		facilityService = App.getApplicationContext().getBean(FacilityService.class);
    	}
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	/** Internal ID of the corresponding row in the DC_CORRESPONDENCE table. */
    public final Integer getCorrespondenceID() {
        return _correspondenceID;
    }

    /** Internal ID of the corresponding row in the DC_CORRESPONDENCE table. */
    public final void setCorrespondenceID(Integer correspondenceID) {
        _correspondenceID = correspondenceID;
    }

    /** Code representing the type of the corresponding correspondence. */
    public final String getCorrespondenceTypeCode() {
        return _correspondenceTypeCode;
    }

    /** Code representing the type of the corresponding correspondence. */
    public final void setCorrespondenceTypeCode(String correspondenceTypeCode) {

        if (correspondenceTypeCode == null) {

            if (_correspondenceTypeCode == null) {
                setDirty(true);
            }

            _correspondenceTypeDescription = null;
            _savedDocReqd = false;
            _correspondenceTypeCode = null;

            return;
        }

        DefData correspondenceTypeDescriptions 
            = CorrespondenceDef.getDescriptionData();

        DefSelectItems descriptionItems 
            = correspondenceTypeDescriptions.getItems();

        _correspondenceTypeDescription 
            = descriptionItems.getItemDesc(correspondenceTypeCode);
            
        DefData savedDocReqdData = CorrespondenceDef.getSavedDocReqdData();
        DefSelectItems savedDocReqdItems = savedDocReqdData.getItems();
        String mustSave = savedDocReqdItems.getItemDesc(correspondenceTypeCode);
        
        DefData linkedToTypeData = CorrespondenceDef.getLinkedToTypeData();
        DefSelectItems linkedToTypeItems = linkedToTypeData.getItems();
        linkedToTypeCd = linkedToTypeItems.getItemDesc(correspondenceTypeCode);

        if (mustSave.equalsIgnoreCase("Y")) {
            _savedDocReqd = true;
        }
        else {
            _savedDocReqd = false;
        }

        _correspondenceTypeCode = correspondenceTypeCode;
        setDirty(true);
    }

    /** Date that the correspondence was generated or sent. */
    public final Timestamp getDateGenerated() {
        return _dateGenerated;
    }

    /** Date that the correspondence was generated or sent. */
    public final void setDateGenerated(Timestamp dateGenerated) {

        _dateGenerated = dateGenerated;
        
        setDirty(true);

    }

    /** ID of the facility referenced by this correspondence. */
    public final String getFacilityID() {
        return _facilityID;
    }

    /** ID of the facility referenced by this correspondence. */
    public final void setFacilityID(String facilityID) {
        _facilityID = facilityID;
        setDirty(true);
    }

    public final String getFacilityNm() {
        return facilityNm;
    }

    public final void setFacilityNm(String facilityNm) {
        this.facilityNm = facilityNm;
    }

    public final int getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    /** Description matching the correspondence type code. */
    public final String getCorrespondenceTypeDescription() {
        return _correspondenceTypeDescription;
    }

    /**
     * Is an application required to save a document when adding a
     * correspondence entry?
     */
    public final boolean getSavedDocReqd() {
        return _savedDocReqd;
    }

    /** Process ID of an instance of the Record of Undeliverable Mail workflow. */
    public final Integer getRUMProcessID() {
        return _rumProcessID;
    }

    /** Process ID of an instance of the Record of Undeliverable Mail workflow. */
    public final void setRUMProcessID(Integer rumProcessID) {
        _rumProcessID = rumProcessID;
        setDirty(true);
    }

    /** Return any notes attached to this correspondence. */
    public final Note[] getNotes() {
        if (_notes == null) {
            _notes = new ArrayList<Note>(0);
        }
        return _notes.toArray(new Note[0]);
    }

    /** Add a note to this piece of correspondence. */
    public final void addNote(Note note) {

        if (_notes == null) {
            _notes = new ArrayList<Note>(1);
        }

        if (note != null) {
            _notes.add(note);
            if (note.isDirty()) {
                setDirty(true);
            }
        }
    }

    /** Add a set of notes to this piece of correspondence. */
    public final void addNotes(Note[] notes) {

        if (notes != null) {

            if (_notes == null) {
                _notes = new ArrayList<Note>(notes.length);
            }

            for (int i = 0; i < notes.length; i++) {
                if (notes[i] != null) {
                    _notes.add(notes[i]);
                    if (notes[i].isDirty()) {
                        setDirty(true);
                    }
                }
            }
        }

    }

    public final String getCertifiedMailTrackId() {
        return _certifiedMailTrackId;
    }

    public final void setCertifiedMailTrackId(String id) {
        _certifiedMailTrackId = id;
        setDirty(true);
    }

    public final Timestamp getCertifiedMailRcptDate() {
        return _certifiedMailRcptDate;
    }

    public final void setCertifiedMailRcptDate(Timestamp receiptDate) {
        _certifiedMailRcptDate = receiptDate;
        setDirty(true);
    }

    public final Document getDocument() {
        return _document;
    }

    public final void setDocument(Document document) {
        _document = document;

        setDirty(true);
    }

    public final String getDocumentUrl() {
        if (_document != null) {
            return _document.getDocURL();
        }
        return null;
    }

    public final String getAdditionalInfo() {
        return _additionalInfo;
    }

    public final void setAdditionalInfo(String info) {
        _additionalInfo = info;
        if (_additionalInfo != null && _additionalInfo.length() > 25) {
        	setTruncatedAdditionalInfo(_additionalInfo.substring(0, 25) + "...");
        } else {
        	setTruncatedAdditionalInfo(_additionalInfo);
        }
        setDirty(true);
    }

    public final Integer getLinkedToId() {
		return linkedToId;
	}

	public final void setLinkedToId(Integer linkedToId) {
		this.linkedToId = linkedToId;
	}

	public final String getLinkedToTypeCd() {
		return linkedToTypeCd;
	}

	@Override
        public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
            + ((_correspondenceID == null) ? 0 : _correspondenceID.hashCode());
        result = PRIME * result
            + ((_facilityID == null) ? 0 : _facilityID.hashCode());
        result = PRIME * result
            + ((_correspondenceTypeCode == null) ? 0 : _correspondenceTypeCode.hashCode());
        result = PRIME * result
            + ((_dateGenerated == null) ? 0 : _dateGenerated.hashCode());
        result = PRIME * result
            + ((_rumProcessID == null) ? 0 : _rumProcessID.hashCode());
        result = PRIME * result
            + ((_certifiedMailTrackId == null) ? 0 : _certifiedMailTrackId.hashCode());
        result = PRIME * result
            + ((_certifiedMailRcptDate == null) ? 0 : _certifiedMailRcptDate.hashCode());
        result = PRIME * result
            + ((_document == null) ? 0 : _document.hashCode());
        result = PRIME * result
            + ((_additionalInfo == null) ? 0 : _additionalInfo.hashCode());
        for (Note note : getNotes()) {
            result = PRIME * result + note.hashCode();
        }
        result = PRIME * result
        	+ ((linkedToId == null) ? 0 : linkedToId.hashCode());
        result = PRIME * result
            	+ ((directionCd == null) ? 0 : directionCd.hashCode());
        result = PRIME * result
            	+ ((district == null) ? 0 : district.hashCode());
        result = PRIME * result
            	+ ((correspondenceCategoryCd == null) ? 0 : correspondenceCategoryCd.hashCode());
        result = PRIME * result
            	+ ((regarding == null) ? 0 : regarding.hashCode());
        result = PRIME * result
            	+ ((toPerson == null) ? 0 : toPerson.hashCode());
        result = PRIME * result
            	+ ((fromPerson == null) ? 0 : fromPerson.hashCode());
        result = PRIME * result
            	+ ((reviewerId == null) ? 0 : reviewerId.hashCode());
        result = PRIME * result
            	+ ((receiptDate == null) ? 0 : receiptDate.hashCode());
        result = PRIME * result
            	+ ((followUpActionDate == null) ? 0 : followUpActionDate.hashCode());
        result = PRIME * result
            	+ ((followUpActionDescription == null) ? 0 : followUpActionDescription.hashCode());
        result = PRIME * result
        		+ ((companyName == null) ? 0 : companyName.hashCode());
        result = PRIME * result
        		+ ((companyId == null) ? 0 : companyId.hashCode());
        result = PRIME * result
        		+ ((countyCd == null) ? 0 : countyCd.hashCode());
        result = PRIME * result
        		+ ((countyNm == null) ? 0 : countyNm.hashCode());
        result = PRIME * result
        		+ ((cityCd == null) ? 0 : cityCd.hashCode());
        result = PRIME * result
        		+ ((cityNm == null) ? 0 : cityNm.hashCode());
        result = PRIME * result
        		+ ((corId == null) ? 0 : corId.hashCode());
        
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

        final Correspondence other = (Correspondence) obj;

        // Either both null or equal values.
        if (((_correspondenceID == null) && (other.getCorrespondenceID() != null))
            || ((_correspondenceID != null) && (other.getCorrespondenceID() == null))
            || ((_correspondenceID != null)
                && (other.getCorrespondenceID() != null) 
                && !(_correspondenceID.equals(other.getCorrespondenceID())))) {
                                                              
            return false;
        }

        // Either both null or equal values.
        if (((_rumProcessID == null) && (other.getRUMProcessID() != null))
            || ((_rumProcessID != null) && (other.getRUMProcessID() == null))
            || ((_rumProcessID != null)
                && (other.getRUMProcessID() != null) 
                && !(_rumProcessID.equals(other.getRUMProcessID())))) {
                                                          
            return false;
        }

        // Either both null or equal values. Note that we don't bother to test
        // for equality
        // of _correspondenceTypeDescription or _savedDocReqd since these must
        // be equal if the
        // _correspondenceTypeCode fields are equal.
        if (((_correspondenceTypeCode == null) 
             && (other.getCorrespondenceTypeCode() != null))
            || ((_correspondenceTypeCode != null) 
                && (other.getCorrespondenceTypeCode() == null))
            || ((_correspondenceTypeCode != null)
                && (other.getCorrespondenceTypeCode() != null) 
                && !(_correspondenceTypeCode.equals(other.getCorrespondenceTypeCode())))) {
                                                                    
            return false;
        }

        // Either both null or equal values.
        if (((_facilityID == null) && (other.getFacilityID() != null))
            || ((_facilityID != null) && (other.getFacilityID() == null))
            || ((_facilityID != null) && (other.getFacilityID() != null) 
                && !(_facilityID.equals(other.getFacilityID())))) {
                                                                              
            return false;
        }

        // Either both null or equal values.
        if (((_dateGenerated == null) && (other.getDateGenerated() != null))
            || ((_dateGenerated != null) && (other.getDateGenerated() == null))
            || ((_dateGenerated != null)
                && (other.getDateGenerated() != null) 
                && !(_dateGenerated.equals(other.getDateGenerated())))) {
                                                           
            return false;
        }

        // Either both null or equal values.
        if (((_certifiedMailTrackId == null) 
             && (other.getCertifiedMailTrackId() != null))
            || ((_certifiedMailTrackId != null) 
                && (other.getCertifiedMailTrackId() == null))
            || ((_certifiedMailTrackId != null)
                && (other.getCertifiedMailTrackId() != null)
                && !(_certifiedMailTrackId.equals(other.getCertifiedMailTrackId())))) {
                                                                  
            return false;
        }

        // Either both null or equal values.
        if (((_certifiedMailRcptDate == null) 
             && (other.getCertifiedMailRcptDate() != null))
            || ((_certifiedMailRcptDate != null) 
                && (other.getCertifiedMailRcptDate() == null))
            || ((_certifiedMailRcptDate != null)
                && (other.getCertifiedMailRcptDate() != null) 
                && !(_certifiedMailRcptDate.equals(other.getCertifiedMailRcptDate())))) {
                                                                   
            return false;
        }

        // Either both null or equal values.
        if (((_document == null) && (other.getDocument() != null))
            || ((_document != null) && (other.getDocument() == null))
            || ((_document != null) && (other.getDocument() != null) 
                && !(_document.equals(other.getDocument())))) {
                                                                          
            return false;
        }

        // Either both null or equal values.
        if (((_additionalInfo == null) && (other.getAdditionalInfo() != null))
            || ((_additionalInfo != null) && (other.getAdditionalInfo() == null))
            || ((_additionalInfo != null)
                && (other.getAdditionalInfo() != null) 
                && !(_additionalInfo.equals(other.getAdditionalInfo())))) {
                                                            
            return false;
        }

        // Either both null or equal values.
        if (((directionCd == null) && (other.getDirectionCd() != null))
            || ((directionCd != null) && (other.getDirectionCd() == null))
            || ((directionCd != null)
                && (other.getDirectionCd() != null) 
                && !(directionCd.equals(other.getDirectionCd())))) {
                                                            
            return false;
        }
        
        // Either both null or equal values.
        if (((district == null) && (other.getDistrict() != null))
            || ((district != null) && (other.getDistrict() == null))
            || ((district != null)
                && (other.getDistrict() != null) 
                && !(district.equals(other.getDistrict())))) {
                                                            
            return false;
        }
        
        // Either both null or equal values.
        if (((correspondenceCategoryCd == null) && (other.getCorrespondenceCategoryCd() != null))
            || ((correspondenceCategoryCd != null) && (other.getCorrespondenceCategoryCd() == null))
            || ((correspondenceCategoryCd != null)
                && (other.getCorrespondenceCategoryCd() != null) 
                && !(correspondenceCategoryCd.equals(other.getCorrespondenceCategoryCd())))) {
                                                            
            return false;
        }
        
        // Either both null or equal values.
        if (((regarding == null) && (other.getRegarding() != null))
            || ((regarding != null) && (other.getRegarding() == null))
            || ((regarding != null)
                && (other.getRegarding() != null) 
                && !(regarding.equals(other.getRegarding())))) {
                                                            
            return false;
        }
        
        // Either both null or equal values.
        if (((toPerson == null) && (other.getToPerson() != null))
            || ((toPerson != null) && (other.getToPerson() == null))
            || ((toPerson != null)
                && (other.getToPerson() != null) 
                && !(toPerson.equals(other.getToPerson())))) {
                                                            
            return false;
        }
        
        // Either both null or equal values.
        if (((fromPerson == null) && (other.getFromPerson() != null))
            || ((fromPerson != null) && (other.getFromPerson() == null))
            || ((fromPerson != null)
                && (other.getFromPerson() != null) 
                && !(fromPerson.equals(other.getFromPerson())))) {
                                                            
            return false;
        }
        
        // Either both null or equal values.
        if (((reviewerId == null) && (other.getReviewerId() != null))
            || ((reviewerId != null) && (other.getReviewerId() == null))
            || ((reviewerId != null)
                && (other.getReviewerId() != null) 
                && !(reviewerId.equals(other.getReviewerId())))) {
                                                            
            return false;
        }
        
        // Each of our notes must have a corresponding note in theirs and vice
        // versa.
        Note[] ours = getNotes();
        Note[] theirs = other.getNotes();
        for (int i = 0; i < ours.length; i++) {
            boolean found = false;
            for (int j = 0; (j < theirs.length) || !found; j++) {
                if (theirs[j] != null) {
                    if (ours[i].equals(theirs[j])) {
                        found = true;
                        theirs[j] = null;
                    }
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
        
        // Either both null or equal values.
        if (((receiptDate == null) && (other.getReceiptDate() != null))
            || ((receiptDate != null) && (other.getReceiptDate() == null))
            || ((receiptDate != null)
                && (other.getReceiptDate() != null) 
                && !(receiptDate.equals(other.getReceiptDate())))) {
                                                              
            return false;
        }
        
        if (followUpAction != other.isFollowUpAction()) {
        	return false;
        }
        
        // Either both null or equal values.
        if (((followUpActionDate == null) && (other.getFollowUpActionDate() != null))
            || ((followUpActionDate != null) && (other.getFollowUpActionDate() == null))
            || ((followUpActionDate != null)
                && (other.getFollowUpActionDate() != null) 
                && !(followUpActionDate.equals(other.getFollowUpActionDate())))) {
                                                              
            return false;
        }
        
        // Either both null or equal values.
        if (((followUpActionDescription == null) && (other.getFollowUpActionDescription() != null))
            || ((followUpActionDescription != null) && (other.getFollowUpActionDescription() == null))
            || ((followUpActionDescription != null)
                && (other.getFollowUpActionDescription() != null) 
                && !(followUpActionDescription.equals(other.getFollowUpActionDescription())))) {
                                                              
            return false;
        }
        
     // Either both null or equal values.
        if (((linkedToId == null) && (other.getLinkedToId() != null))
            || ((linkedToId != null) && (other.getLinkedToId() == null))
            || ((linkedToId != null)
                && (other.getLinkedToId() != null) 
                && !(linkedToId.equals(other.getLinkedToId())))) {
                                                              
            return false;
        }
        
        if (legacyFlag != other.isLegacyFlag()) {
        	return false;
        }
        
        //Either both null or equal values.
        if (((companyName == null) && (other.getCompanyName() != null))
            || ((companyName != null) && (other.getCompanyName() == null))
            || ((companyName != null)
                && (other.getCompanyName() != null) 
                && !(companyName.equals(other.getCompanyName())))) {
                                                            
            return false;
        }
        
        //Either both null or equal values.
        if (((companyId == null) && (other.getCompanyId() != null))
            || ((companyId != null) && (other.getCompanyId() == null))
            || ((companyId != null)
                && (other.getCompanyId() != null) 
                && !(companyId.equals(other.getCompanyId())))) {
                                                            
            return false;
        }
        
      //Either both null or equal values.
        if (((countyCd == null) && (other.getCountyCd() != null))
            || ((countyCd != null) && (other.getCountyCd() == null))
            || ((countyCd != null)
                && (other.getCountyCd() != null) 
                && !(countyCd.equals(other.getCountyCd())))) {
                                                            
            return false;
        }
        
      //Either both null or equal values.
        if (((countyNm == null) && (other.getCountyNm() != null))
            || ((countyNm != null) && (other.getCountyNm() == null))
            || ((countyNm != null)
                && (other.getCountyNm() != null) 
                && !(countyNm.equals(other.getCountyNm())))) {
                                                            
            return false;
        }
        
      //Either both null or equal values.
        if (((cityCd == null) && (other.getCityCd() != null))
            || ((cityCd != null) && (other.getCityCd() == null))
            || ((cityCd != null)
                && (other.getCityCd() != null) 
                && !(cityCd.equals(other.getCityCd())))) {
                                                            
            return false;
        }
        
      //Either both null or equal values.
        if (((cityNm == null) && (other.getCityNm() != null))
            || ((cityNm != null) && (other.getCityNm() == null))
            || ((cityNm != null)
                && (other.getCityNm() != null) 
                && !(cityNm.equals(other.getCityNm())))) {
                                                            
            return false;
        }
        
        if (linkedtoEnfAction != other.isLinkedtoEnfAction()) {
        	return false;
        }
        
        if (hideAttachments != other.isHideAttachments()) {
        	return false;
        }
        
      //Either both null or equal values.
        if (((corId == null) && (other.getCorId() != null))
            || ((corId != null) && (other.getCorId() == null))
            || ((corId != null)
                && (other.getCorId() != null) 
                && !(corId.equals(other.getCorId())))) {
                                                            
            return false;
        }
        
        return true;
    } // END: public boolean equals(Object obj)

	public String getDirectionCd() {
		return directionCd;
	}

	public void setDirectionCd(String directionCd) {
		this.directionCd = directionCd;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCorrespondenceCategoryCd() {
		return correspondenceCategoryCd;
	}

	public void setCorrespondenceCategoryCd(String correspondenceCategoryCd) {
		this.correspondenceCategoryCd = correspondenceCategoryCd;
	}

	public String getRegarding() {
		return regarding;
	}

	public void setRegarding(String regarding) {
		this.regarding = regarding;
	}

	public String getToPerson() {
		return toPerson;
	}

	public void setToPerson(String toPerson) {
		this.toPerson = toPerson;
	}

	public String getFromPerson() {
		return fromPerson;
	}

	public void setFromPerson(String fromPerson) {
		this.fromPerson = fromPerson;
	}

	public Integer getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(Integer reviewerId) {
		this.reviewerId = reviewerId;
	}

	public Timestamp getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Timestamp receiptDate) {
		this.receiptDate = receiptDate;
	}

	public boolean isFollowUpAction() {
		return followUpAction;
	}

	public void setFollowUpAction(boolean followUpAction) {
		this.followUpAction = followUpAction;
	}

	public Timestamp getFollowUpActionDate() {
		return followUpActionDate;
	}

	public void setFollowUpActionDate(Timestamp followUpActionDate) {
		this.followUpActionDate = followUpActionDate;
	}

	public String getFollowUpActionDescription() {
		return followUpActionDescription;
	}

	public void setFollowUpActionDescription(String followUpActionDescription) {
		this.followUpActionDescription = followUpActionDescription;
	}
	
	public boolean isOutgoing() {
		boolean ret = false;

		if (directionCd != null
				&& directionCd
						.equalsIgnoreCase(CorrespondenceDirectionDef.OUTGOING)) {
			ret = true;
		}

		return ret;
	}
	
	public boolean isIncoming() {
		boolean ret = false;
		
		if (directionCd != null
				&& directionCd
						.equalsIgnoreCase(CorrespondenceDirectionDef.INCOMING)) {
			ret = true;
		}
		
		return ret;
	} 
	
	@Override
	public ValidationMessage[] validate() {

		this.validationMessages.clear();

		Boolean hideDistrict = SystemPropertyDef.getSystemPropertyValueAsBoolean("hideDistrict", false);
		if (!hideDistrict) {

			if (Utility.isNullOrEmpty(this.getFacilityID()) && Utility.isNullOrEmpty(this.getDistrict())) {
				ValidationMessage valMsg = new ValidationMessage("associatedWithFacility",
						"Associate a facility or select a district.", ValidationMessage.Severity.ERROR);
				this.validationMessages.put("districtCd", valMsg);

			} else if (!Utility.isNullOrEmpty(this.getFacilityID()) && !Utility.isNullOrEmpty(this.getDistrict())) {
				ValidationMessage valMsg = new ValidationMessage("districtCd",
						"Correspondence cannot be associated with both a district and facility.",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("districtCd", valMsg);
			}
		}

		if (!Utility.isNullOrEmpty(this.getFacilityID())) {
			
			if (!this.getFacilityID().startsWith("F")) {
				String format = "F%06d";
				int tempId;
				try {
					tempId = Integer.parseInt(this.getFacilityID());
					setFacilityID(String.format(format, tempId));
				} catch (NumberFormatException nfe) {
					ValidationMessage valMsg = new ValidationMessage("facilityId",
							"Facility ID: " + this.getFacilityID() + " is not an integer.",
							ValidationMessage.Severity.ERROR);
					this.validationMessages.put("facilityIdEx", valMsg);
					logger.error("Error trying to understand facilityId " + this.getFacilityID() + ": " + nfe.getMessage());
				}
			}
			
			Facility facility = null;
			try {
				facility = getFacilityService().retrieveFacility(this.getFacilityID());
			} catch (DAOException e) {
				ValidationMessage valMsg = new ValidationMessage("facilityId",
						"Unable to retrieve facility with the given facility id: " + this.getFacilityID(),
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("facilityIdEx", valMsg);
				logger.error("Error trying to retrieve facility: " + e.getMessage(), e);
			} catch (RemoteException e) {
				ValidationMessage valMsg = new ValidationMessage("facilityId",
						"Unable to retrieve facility with the given facility id: " + this.getFacilityID(),
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("facilityIdEx", valMsg);
				logger.error("Error trying to retrieve facility: " + e.getMessage(), e);
			}

			if (facility == null) {
				ValidationMessage valMsg = new ValidationMessage("facilityId",
						"No facility exists with the given facility id: " + this.getFacilityID(),
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("facilityId", valMsg);
			}

		}

        if (this.getDirectionCd() == null){     
        	ValidationMessage valMsg = new ValidationMessage("correspondenceDirection", "Set a Correspondence direction.", ValidationMessage.Severity.ERROR);
            this.validationMessages.put("correspondenceDirection", valMsg);
        }
        
        if (this.getCorrespondenceTypeCode() == null){
        	ValidationMessage valMsg = new ValidationMessage("correspondenceType", "Set a Correspondence type.", ValidationMessage.Severity.ERROR);
            this.validationMessages.put("correspondenceType", valMsg);
        }
        
        if (this.isIncoming()) {
	        if (this.getCorrespondenceCategoryCd() == null){  
	        	ValidationMessage valMsg = new ValidationMessage("correspondenceCategory", "Set a Correspondence category.", ValidationMessage.Severity.ERROR);
	            this.validationMessages.put("correspondenceCategory", valMsg);
	        }
	        
	        if (this.getReceiptDate() == null) {
	        	ValidationMessage valMsg = new ValidationMessage("correspondenceReceiptDate", "The Receipt Date must be entered.", ValidationMessage.Severity.ERROR);
	            this.validationMessages.put("correspondenceReceiptDate", valMsg);
	        }
        }
        
        if (this.isOutgoing()) {
        	if (this.getDateGenerated() == null) {
	        	ValidationMessage valMsg = new ValidationMessage("correspondenceGeneratedDate", "The Date Generated must be entered.", ValidationMessage.Severity.ERROR);
	            this.validationMessages.put("correspondenceGeneratedDate", valMsg);
        	}
        }
        
        if (this.isFollowUpAction()) {
        	
        	if (null == getReviewerId()) {
	        	ValidationMessage valMsg = new ValidationMessage("divisionReviewer", "Division Reviewer is required.", ValidationMessage.Severity.ERROR);
	            this.validationMessages.put("divisionReviewer", valMsg);
        	}
        	
        	// follow-up action date must be in future
    		Calendar cal = Calendar.getInstance();
    		cal.set(Calendar.HOUR_OF_DAY, 23);
    		cal.set(Calendar.MINUTE, 59);
    		cal.set(Calendar.SECOND, 59);
    		Timestamp today = new Timestamp(cal.getTimeInMillis());
    		
    		if (this.getFollowUpActionDate() != null) {
	    		if (!this.getFollowUpActionDate().after(today)) {
	            	ValidationMessage valMsg = new ValidationMessage("followUpActionDate", "The Follow-up Action Date must be set in the future.", ValidationMessage.Severity.ERROR);
	                this.validationMessages.put("followUpActionDate", valMsg);
	    		}
    		} else {
            	ValidationMessage valMsg = new ValidationMessage("followUpActionDate", "The Follow-up Action Date must be set.", ValidationMessage.Severity.ERROR);
                this.validationMessages.put("followUpActionDate", valMsg);
    		}
        	
        	// follow-up action description must be filled
        	if (Utility.isNullOrEmpty(this.getFollowUpActionDescription())) {
            	ValidationMessage valMsg = new ValidationMessage("followUpActionDescription", "Follow-up Action Description must be entered.", ValidationMessage.Severity.ERROR);
                this.validationMessages.put("followUpActionDescription", valMsg);
        	}
        	
        	// follow-up action description cannot be too long
            if (this.getFollowUpActionDescription() != null && this.getFollowUpActionDescription().length() > 1000){  
            	ValidationMessage valMsg = new ValidationMessage("followUpActionDescription", "Follow-up Action Description longer than 1,000 characters.", ValidationMessage.Severity.ERROR);
                this.validationMessages.put("followUpActionDescription", valMsg);
            }
        }
        
        if (this.getDateGenerated() != null && this.getReceiptDate() != null) {
        	if ( this.getDateGenerated().after(this.receiptDate)) {
        		ValidationMessage valMsg = new ValidationMessage("correspondenceGeneratedDate", "The Date Generated must be on or before the Receipt Date.", ValidationMessage.Severity.ERROR);
	            this.validationMessages.put("correspondenceGeneratedDate", valMsg);
        	}
        }
        
        if (this.isLinkedtoEnfAction() && null == this.linkedToId) {
        	ValidationMessage valMsg = new ValidationMessage("linkedToId", "Enforcement Action must be selected.", ValidationMessage.Severity.ERROR);
            this.validationMessages.put("linkedToId", valMsg);
        }
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public boolean isLegacyFlag() {
		return legacyFlag;
	}

	public void setLegacyFlag(boolean legacyFlag) {
		this.legacyFlag = legacyFlag;
	}

	public String getTruncatedAdditionalInfo() {
		return truncatedAdditionalInfo;
	}

	public void setTruncatedAdditionalInfo(String truncatedAdditionalInfo) {
		this.truncatedAdditionalInfo = truncatedAdditionalInfo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCountyCd() {
		return countyCd;
	}

	public void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}
	
	public String getCountyNm() {
		return countyNm;
	}

	public void setCountyNm(String countyNm) {
		this.countyNm = countyNm;
	}

	public String getCityCd() {
		return cityCd;
	}

	public void setCityCd(String cityCd) {
		this.cityCd = cityCd;
	}
	
	public String getCityNm() {
		return cityNm;
	}

	public void setCityNm(String cityNm) {
		this.cityNm = cityNm;
	}

	public final List<SelectItem> getCounties() {
		List<SelectItem> ret = County
				.getDistrictCounties(district);
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}
	
	public final List<SelectItem> getCountiesAll() {
		List<SelectItem> ret = County
				.getDistrictCounties("ALL");
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}
	
	public final List<SelectItem> getCities() {
		List<SelectItem> ret = CityDef
				.getCountyCities(countyCd);
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}
	
	public final List<SelectItem> getCitiesAll() {
		List<SelectItem> ret = CityDef
				.getCountyCities("ALL");
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}

	public boolean isLinkedtoEnfAction() {
		return linkedtoEnfAction;
	}

	public void setLinkedtoEnfAction(boolean linkedtoEnfAction) {
		this.linkedtoEnfAction = linkedtoEnfAction;
	}
	
	public boolean isHideAttachments() {
		return hideAttachments;
	}

	public void setHideAttachments(boolean hideAttachments) {
		this.hideAttachments = hideAttachments;
	}

	public String getLinkedEnfActionId() {
		String ret = null;
		if (null != linkedToId) {
			ret = String.format("ENF%06d", linkedToId);
		}
		return ret;
	}

	public String getCorId() {
		return corId;
	}

	public void setCorId(String corId) {
		this.corId = corId;
	}
	
    public Integer getAttachmentCount() {
		return attachmentCount;
	}

	public void setAttachmentCount(Integer attachmentCount) {
		this.attachmentCount = attachmentCount;
	}

	public List<String> getInspectionsReferencedIn() {
		return inspectionsReferencedIn;
	}

	public void setInspectionsReferencedIn(List<String> inspectionsReferencedIn) {
		this.inspectionsReferencedIn = inspectionsReferencedIn;
	}


	// needed for jsp only so that the Correspondence Category column in the
	// Correspondence search results datagrid and Inpsection Correspondence Snapshot can be sorted by Category Desc instead of code
	public final String getCorrespondenceCategoryDesc() {
		String ret = null;
		if(null != this.correspondenceCategoryCd) {
			ret = CorrespondenceCategoryDef.getData().getItems().getItemDesc(correspondenceCategoryCd);
		}
		return ret;
	}

} // END: public class Correspondence extends BaseDB

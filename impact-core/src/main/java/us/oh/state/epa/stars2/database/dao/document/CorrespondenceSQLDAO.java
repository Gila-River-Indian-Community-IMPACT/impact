package us.oh.state.epa.stars2.database.dao.document;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.CorrespondenceDAO;
import us.oh.state.epa.stars2.database.dao.ceta.FullComplianceEvalSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;

/**
 * The DC_CORRESPONDENCE table tracks letters and other documents mailed to
 * facilities, the AG's office, etc.
 */
@Repository
public class CorrespondenceSQLDAO extends AbstractDAO 
    implements CorrespondenceDAO {
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public final static String DATE_GENERATED = "cs.date_generated";
    public final static String RECEIPT_DATE = "cs.receipt_date";
	private Logger logger = Logger.getLogger(CorrespondenceSQLDAO.class);                                                       
    /**
     * @see CorrespondenceDAO#searchCorrespondence(facilityID)
     */
    public final Correspondence[] searchCorrespondenceByFacility(String facilityID)
        throws DAOException {
                                                                 

        ConnectionHandler connHandler 
            = new ConnectionHandler("CorrespondenceSQL.searchFacilityCorrespondence", false);
                                                              
        connHandler.setString(1, facilityID);

        ArrayList<Correspondence> ret = connHandler.retrieveArray(Correspondence.class);

        Iterator<Correspondence> cit = ret.iterator();
        while (cit.hasNext()) {
            Correspondence corr = cit.next();
            connHandler = new ConnectionHandler("CorrespondenceSQL.searchCorrespondenceNotes", false);
            connHandler.setInteger(1, corr.getCorrespondenceID());
            ArrayList<Note> notes = connHandler.retrieveArray(Note.class);
            corr.addNotes(notes.toArray(new Note[0]));
        }

        return ret.toArray(new Correspondence[0]);
    }
    
    /**
     * @see CorrespondenceDAO#searchCorrespondence(linkedToID)
     */
    public final Correspondence[] searchCorrespondenceByLinkedToId(Integer linkedToId)
        throws DAOException {
                                                                 

        ConnectionHandler connHandler 
            = new ConnectionHandler("CorrespondenceSQL.searchLinkedToCorrespondence", false);
                                                              
        connHandler.setString(1, linkedToId);

        ArrayList<Correspondence> ret = connHandler.retrieveArray(Correspondence.class);

        Iterator<Correspondence> cit = ret.iterator();
        while (cit.hasNext()) {
            Correspondence corr = cit.next();
            connHandler = new ConnectionHandler("CorrespondenceSQL.searchCorrespondenceNotes", false);
            connHandler.setInteger(1, corr.getCorrespondenceID());
            ArrayList<Note> notes = connHandler.retrieveArray(Note.class);
            corr.addNotes(notes.toArray(new Note[0]));
        }

        return ret.toArray(new Correspondence[0]);
    }

    /**
     * @see CorrespondenceDAO#createCorrespondence(correspondence)
     */
    public final Correspondence createCorrespondence(Correspondence correspondence)
        throws DAOException {
                                                     

        Correspondence ret = correspondence;
        ConnectionHandler connHandler
            = new ConnectionHandler("CorrespondenceSQL.createCorrespondence", false);

        int i = 1;
        Integer correspondenceId = nextSequenceVal("S_Correspondence_Id");
        connHandler.setInteger(i++, correspondenceId);
        connHandler.setString(i++, correspondence.getCorrespondenceTypeCode());
        connHandler.setTimestamp(i++, correspondence.getDateGenerated());
        connHandler.setString(i++, correspondence.getFacilityID());
        connHandler.setInteger(i++, correspondence.getRUMProcessID());
        connHandler.setString(i++, correspondence.getCertifiedMailTrackId());
        connHandler.setTimestamp(i++, correspondence.getCertifiedMailRcptDate());
        connHandler.setString(i++, correspondence.getAdditionalInfo());
        connHandler.setInteger(i++, correspondence.getLinkedToId());
        
        connHandler.setString(i++, correspondence.getDirectionCd());
        connHandler.setString(i++, correspondence.getDistrict());
        connHandler.setString(i++, correspondence.getCorrespondenceCategoryCd());
        connHandler.setString(i++, correspondence.getRegarding());
        connHandler.setString(i++, correspondence.getToPerson());
        connHandler.setString(i++, correspondence.getFromPerson());
        connHandler.setInteger(i++, correspondence.getReviewerId());
        connHandler.setTimestamp(i++, correspondence.getReceiptDate());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isFollowUpAction()));
        connHandler.setTimestamp(i++, correspondence.getFollowUpActionDate());
        connHandler.setString(i++, correspondence.getFollowUpActionDescription());
        connHandler.setString(i++,  AbstractDAO.translateBooleanToIndicator(correspondence.isLegacyFlag()));
        connHandler.setString(i++, correspondence.getCountyCd());
        connHandler.setString(i++, correspondence.getCityCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isLinkedtoEnfAction()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isHideAttachments()));
        connHandler.update();

        ret.setCorrespondenceID(correspondenceId);
        ret.setLastModified(1);

        return ret;
    }

    /**
     * @see CorrespondenceDAO#updateCorrespondence(correspondence)
     */
    public final void updateCorrespondence(Correspondence correspondence)
        throws DAOException {

        ConnectionHandler connHandler = new ConnectionHandler("CorrespondenceSQL.updateCorrespondence", false);

        int i = 1;
        connHandler.setString(i++, correspondence.getCorrespondenceTypeCode());
        connHandler.setTimestamp(i++, correspondence.getDateGenerated());
        connHandler.setString(i++, correspondence.getFacilityID());
        connHandler.setInteger(i++, correspondence.getRUMProcessID());
        connHandler.setString(i++, correspondence.getCertifiedMailTrackId());
        connHandler.setTimestamp(i++, correspondence.getCertifiedMailRcptDate());
        connHandler.setString(i++, correspondence.getAdditionalInfo());
        connHandler.setInteger(i++, correspondence.getLinkedToId());
        
        connHandler.setString(i++, correspondence.getDirectionCd());
        connHandler.setString(i++, correspondence.getDistrict());
        connHandler.setString(i++, correspondence.getCorrespondenceCategoryCd());
        connHandler.setString(i++, correspondence.getRegarding());
        connHandler.setString(i++, correspondence.getToPerson());
        connHandler.setString(i++, correspondence.getFromPerson());
        connHandler.setInteger(i++, correspondence.getReviewerId());
        connHandler.setTimestamp(i++, correspondence.getReceiptDate());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isFollowUpAction()));
        connHandler.setTimestamp(i++, correspondence.getFollowUpActionDate());
        connHandler.setString(i++, correspondence.getFollowUpActionDescription());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isLegacyFlag()));
        connHandler.setString(i++, correspondence.getCountyCd());
        connHandler.setString(i++, correspondence.getCityCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isLinkedtoEnfAction()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isHideAttachments()));
        
        connHandler.setInteger(i++, correspondence.getLastModified() + 1);

        connHandler.setInteger(i++, correspondence.getCorrespondenceID());
        connHandler.setInteger(i++, correspondence.getLastModified());

        connHandler.update();

        return;
    }

    /**
     * @see CorrespondenceDAO#deleteCorrespondence(correspondence)
     */
    public final void deleteCorrespondence(Correspondence correspondence)
        throws DAOException {

        ConnectionHandler connHandler 
            = new ConnectionHandler("CorrespondenceSQL.deleteCorrespondence", false);

        connHandler.setInteger(1, correspondence.getCorrespondenceID());

        connHandler.remove();

        return;
    }

    /**
     * @see CorrespondenceDAO#createNoteXref(correspondenceID, noteID)
     */
    public final void createNoteXref(Integer correspondenceID, Integer noteID)
        throws DAOException {

        ConnectionHandler connHandler 
            = new ConnectionHandler("CorrespondenceSQL.createNoteXref", false);

        connHandler.setInteger(1, correspondenceID);
        connHandler.setInteger(2, noteID);

        connHandler.update();

        return;
    }

    /**
     * @see CorrespondenceDAO#deleteNoteXref(correspondenceID, noteID)
     */
    public final void deleteNoteXref(Integer correspondenceID, Integer noteID)
        throws DAOException {

        ConnectionHandler connHandler 
            = new ConnectionHandler("CorrespondenceSQL.deleteNoteXref", false);

        connHandler.setInteger(1, correspondenceID);
        connHandler.setInteger(2, noteID);

        connHandler.update();

        return;
    }

    // ************
    /**
     * @see CorrespondenceDAO#retrieveCorrespondence(CorrespondenceList)
     */
    public final Correspondence retrieveCorrespondence(int correspondenceId) // TODO
        throws DAOException {

        StringBuffer statementSQL 
            = new StringBuffer(loadSQL("CorrespondenceSQL.searchCorrespondence"));
                                                     
        statementSQL.append(" AND cs.correspondence_id = ?");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        connHandler.setInteger(1, correspondenceId);

        return (Correspondence) connHandler.retrieve(Correspondence.class);
    }

    /**
     * @see CorrespondenceDAO#modifyCorrespondence(CorrespondenceList)
     */
    public final boolean modifyCorrespondence(Correspondence correspondence)
        throws DAOException {
        checkNull(correspondence);

        ConnectionHandler connHandler 
            = new ConnectionHandler("CorrespondenceSQL.modifyCorrespondence", false);
                                                              
        int i = 1;
        connHandler.setInteger(i++, correspondence.getRUMProcessID());
        connHandler.setString(i++, correspondence.getCertifiedMailTrackId());
        connHandler.setTimestamp(i++, correspondence.getDateGenerated());
        connHandler.setString(i++, correspondence.getCorrespondenceTypeCode());
        connHandler.setTimestamp(i++, correspondence.getCertifiedMailRcptDate());
        connHandler.setString(i++, correspondence.getAdditionalInfo());
        connHandler.setInteger(i++, correspondence.getLinkedToId());
        connHandler.setString(i++, correspondence.getDirectionCd());
        connHandler.setString(i++, correspondence.getDistrict());
        connHandler.setString(i++, correspondence.getCorrespondenceCategoryCd());
        connHandler.setString(i++, correspondence.getRegarding());
        connHandler.setString(i++, correspondence.getToPerson());
        connHandler.setString(i++, correspondence.getFromPerson());
        connHandler.setInteger(i++, correspondence.getReviewerId());
        connHandler.setTimestamp(i++, correspondence.getReceiptDate());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isFollowUpAction()));
        connHandler.setTimestamp(i++, correspondence.getFollowUpActionDate());
        connHandler.setString(i++, correspondence.getFollowUpActionDescription());
        connHandler.setString(i++, correspondence.getFacilityID());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isLegacyFlag()));
        connHandler.setString(i++, correspondence.getCountyCd());
        connHandler.setString(i++, correspondence.getCityCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isLinkedtoEnfAction()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(correspondence.isHideAttachments()));
        
        connHandler.setInteger(i++, correspondence.getLastModified() + 1);
        connHandler.setInteger(i++, correspondence.getCorrespondenceID());
        connHandler.setInteger(i++, correspondence.getLastModified());

        return connHandler.update();
    }

    /**
     * @see CorrespondenceDAO#searchCorrespondence(CorrespondenceList)
     */
    public final Correspondence[] searchCorrespondence(Correspondence searchObj)
        throws DAOException {

        StringBuffer statementSQL = new StringBuffer(loadSQL("CorrespondenceSQL.searchCorrespondence"));

        String facilityId = searchObj.getFacilityID();
 		if (!Utility.isNullOrEmpty(facilityId)) {
 			String format = "F%06d";

 			int tempId;
 			try {
 				tempId = Integer.parseInt(facilityId);
 				facilityId = String.format(format, tempId);
 			} catch (NumberFormatException nfe) {
 			}
 		}
        
		if (!Utility.isNullOrEmpty(facilityId)) {
			statementSQL.append(" AND LOWER(cs.facility_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
			statementSQL.append("')");
        }
        if (searchObj.getFacilityNm() != null
            && searchObj.getFacilityNm().trim().length() > 0) {
            statementSQL.append(" AND LOWER(ff.facility_nm) LIKE ");
            statementSQL.append("LOWER('");
            statementSQL.append(SQLizeString(searchObj.getFacilityNm().replace(
                                                                               "*", "%")));
            statementSQL.append("')");
        }
        if (searchObj.getCompanyId() != null
        		&& searchObj.getCompanyId().trim().length() > 0) {
            statementSQL.append("AND cmp.cmp_id = '" + searchObj.getCompanyId() + "'");
        }
        if (searchObj.getCorrespondenceTypeCode() != null
            && searchObj.getCorrespondenceTypeCode().trim().length() > 0) {
            statementSQL.append(" AND cs.correspondence_type_cd = "
                                + searchObj.getCorrespondenceTypeCode());

        }
		if (searchObj.getDirectionCd() != null
				&& searchObj.getDirectionCd().trim().length() > 0) {
			statementSQL.append(" AND cs.direction_cd = '");
			statementSQL.append(searchObj.getDirectionCd());
			statementSQL.append("'");

		}
		if (searchObj.getCorrespondenceCategoryCd() != null
				&& searchObj.getCorrespondenceCategoryCd().trim().length() > 0) {
			statementSQL.append(" AND cs.correspondence_category_cd = '");
			statementSQL.append(searchObj.getCorrespondenceCategoryCd());
			statementSQL.append("'");
		}       
        if (searchObj.getAdditionalInfo() != null
            && searchObj.getAdditionalInfo().trim().length() > 0) {
            statementSQL.append(" AND LOWER(cs.additional_info) LIKE ");
            statementSQL.append("LOWER('");
            statementSQL.append(SQLizeString(searchObj.getAdditionalInfo()
                                             .replace("*", "%")));
            statementSQL.append("')");
        }

        statementSQL.append(" ORDER BY cs.facility_id");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        ArrayList<Correspondence> ret = connHandler.retrieveArray(Correspondence.class);

        return ret.toArray(new Correspondence[0]);
    }

    /**
     * @see CorrespondenceDAO#searchCorrespondence(CorrespondenceList)
     */
    public final Correspondence[] searchCorrespondenceByDate(Correspondence searchObj, String dateField,  
            Timestamp startDate, Timestamp endDate) throws DAOException {

        StringBuffer statementSQL = new StringBuffer(loadSQL("CorrespondenceSQL.searchCorrespondence"));
        
        String facilityId = searchObj.getFacilityID();
		if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";
			facilityId = facilityId.trim();
			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
        
		if (!Utility.isNullOrEmpty(facilityId)) {
			statementSQL.append(" AND LOWER(cs.facility_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
			statementSQL.append("')");
        }
        if (searchObj.getFacilityNm() != null
            && searchObj.getFacilityNm().trim().length() > 0) {
            statementSQL.append(" AND LOWER(ff.facility_nm) LIKE ");
            statementSQL.append("LOWER('");
            statementSQL.append(SQLizeString(searchObj.getFacilityNm().replace(
                                                                               "*", "%")));
            statementSQL.append("')");
        }
        if (searchObj.getCompanyId() != null
        		&& searchObj.getCompanyId().trim().length() > 0) {
            statementSQL.append("AND cmp.cmp_id = '" + searchObj.getCompanyId() + "'");
        }
        
		String corId = searchObj.getCorId();
		if (corId != null && corId.trim().length() > 0) {
			corId = formatId("COR", corId.trim());
			statementSQL.append(" AND LOWER(cs.cor_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(corId.replace("*", "%")));
			statementSQL.append("')");
		}
        
        if (searchObj.getCorrespondenceTypeCode() != null
            && searchObj.getCorrespondenceTypeCode().trim().length() > 0) {
            statementSQL.append(" AND cs.correspondence_type_cd = "
                                + searchObj.getCorrespondenceTypeCode());

        }
        if (searchObj.getAdditionalInfo() != null
            && searchObj.getAdditionalInfo().trim().length() > 0) {
            statementSQL.append(" AND LOWER(cs.additional_info) LIKE ");
            statementSQL.append("LOWER('");
            statementSQL.append(SQLizeString(searchObj.getAdditionalInfo()
                                             .replace("*", "%")));
            statementSQL.append("')");
        }
		if (searchObj.getDirectionCd() != null
				&& searchObj.getDirectionCd().trim().length() > 0) {
			statementSQL.append(" AND cs.direction_cd = '");
			statementSQL.append(searchObj.getDirectionCd());
			statementSQL.append("'");

		}
		
		if (searchObj.getDistrict() != null
				&& searchObj.getDistrict().trim().length() > 0) {
			statementSQL.append(" AND cs.district = '");
			statementSQL.append(searchObj.getDistrict());
			statementSQL.append("'");
		}
		
		if (searchObj.getCountyCd() != null
				&& searchObj.getCountyCd().trim().length() > 0) {
			statementSQL.append(" AND cs.county_cd = '");
			statementSQL.append(searchObj.getCountyCd());
			statementSQL.append("'");
		}
		
		if (searchObj.getCityCd() != null
				&& searchObj.getCityCd().trim().length() > 0) {
			statementSQL.append(" AND cs.city_cd = '");
			statementSQL.append(searchObj.getCityCd());
			statementSQL.append("'");
		}
		
		if (searchObj.getCorrespondenceCategoryCd() != null
				&& searchObj.getCorrespondenceCategoryCd().trim().length() > 0) {
			statementSQL.append(" AND cs.correspondence_category_cd = '");
			statementSQL.append(searchObj.getCorrespondenceCategoryCd());
			statementSQL.append("'");
		}
        
		if (!Utility.isNullOrEmpty(dateField)) {
			if (startDate != null) {
				statementSQL.append(" AND " + dateField.trim()
						+ " >= CONVERT(DATETIME2, '" + DATE_FORMAT.format(startDate)
						+ "')");
			}
			if (endDate != null) {
				statementSQL.append(" AND " + dateField.trim()
						+ " <= CONVERT(DATETIME2, '" + DATE_FORMAT.format(endDate)
						+ "')");
			}
		}

        statementSQL.append(" ORDER BY cs.facility_id, cs.date_generated DESC");

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        ArrayList<Correspondence> ret = connHandler.retrieveArray(Correspondence.class);

        return ret.toArray(new Correspondence[0]);
    }
    
    @Override
    public List<Correspondence> retrievePotentialFollowUpCorrespondence() throws DAOException {
    	ConnectionHandler connHandler = new ConnectionHandler("CorrespondenceSQL.retrieveFollowUpCorrespondence", true);

    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.HOUR_OF_DAY, 23);
    	cal.set(Calendar.MINUTE, 59);
    	cal.set(Calendar.SECOND, 59);
    	cal.set(Calendar.MILLISECOND, 999);
    	Timestamp ts = new Timestamp(cal.getTimeInMillis());
        connHandler.setTimestamp(1, ts);
        
        ArrayList<Correspondence> ret = connHandler.retrieveArray(Correspondence.class);
        return ret; 
    }    
    
   public Attachment createCorrespondenceAttachment(Integer correspondenceId, Attachment attachment)
			throws DAOException {
    	Attachment ret = attachment;

		ConnectionHandler connHandler = new ConnectionHandler(
				"CorrespondenceSQL.createCorrespondenceAttachment", false);

		connHandler.setInteger(1, attachment.getDocumentID());
		connHandler.setInteger(2, correspondenceId);
		connHandler.setString(3, attachment.getDocTypeCd());
		connHandler.update();

		ret.setLastModified(1);

		return ret;
    }
    
   public final Attachment[] retrieveCorrespondenceAttachments(Integer correspondenceId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CorrespondenceSQL.retrieveCorrespondenceAttachments", true);

		connHandler.setInteger(1, correspondenceId);

		ArrayList<Attachment> ret = connHandler.retrieveArray(Attachment.class);

		return ret.toArray(new Attachment[0]);
	}
   
	public final Attachment updateCorrespondenceAttachment(Attachment attachment)
			throws DAOException {
		Attachment ret = attachment;

		ConnectionHandler connHandler = new ConnectionHandler(
				"CorrespondenceSQL.updateCorrespondenceAttachment", false);

		connHandler.setInteger(1, attachment.getDocumentID());
		connHandler.setString(2, attachment.getDocTypeCd());
		connHandler.setInteger(3, attachment.getRefLastModified() + 1);
		connHandler.setInteger(4, attachment.getDocumentID());
		connHandler.setInteger(5, attachment.getRefLastModified());
		connHandler.update();
		ret.setLastModified(1);

		return ret;
	}


	public final void removeCorrespondenceAttachment(Attachment attachment)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CorrespondenceSQL.removeCorrespondenceAttachment", false);

		connHandler.setInteger(1, attachment.getDocumentID());
		connHandler.remove();

		removeRows("dc_document", "document_id", attachment.getDocumentID());
		try {
			DocumentUtil.removeDocument(attachment.getPath());
		} catch (IOException ioe) {
			throw new DAOException("Could not delete CORRESPONDENCE ATTACHMENT "
					+ attachment.getPath(), ioe);
		}
	}

	@Override
	public Correspondence retrieveCorrespondenceAttachmentCount(Correspondence correspondence) throws DAOException{
		checkNull(correspondence.getCorrespondenceID());
		Connection conn = null;
		PreparedStatement pStmt = null;
		
		try {
			conn = getConnection();
			pStmt = conn.prepareStatement(loadSQL("CorrespondenceSQL.retrieveCorrespondenceAttachmentCount"),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int i = 1;
			pStmt.setInt(i++, correspondence.getCorrespondenceID());
			ResultSet rs = pStmt.executeQuery();
			
			if (rs.next()) {
			    correspondence.setAttachmentCount(AbstractDAO.getInteger(rs, "attachment_count"));
			} else {
				correspondence.setAttachmentCount(0);
			}
			rs.close();
			
		}  catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			handleException(ex, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return correspondence;
		
	}

}

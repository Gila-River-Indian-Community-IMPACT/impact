package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.framework.util.Duration;

public class PermitIssuance extends BaseDB {

    private static String[][] _messages = {
        { "issuanceTypeCd", "IssuanceTypeCd is missing." },
        { "issuanceStatusCd", "IssuanceStatusCd is missing." },
        { "permitId", "PermitId is missing." } };
    private Integer issuanceId;
    private String issuanceTypeCd;
    private Integer permitId;
    private String permitNumber;
    private Timestamp issuanceDate;
    private String issuanceStatusCd;
    private Timestamp publicNoticeRequestDate;
    private Timestamp publicNoticePublishDate;
    private Timestamp publicCommentEndDate;
    private boolean hearingRequestedFlag;
    private Timestamp hearingRequestedDate;
    private Timestamp hearingNoticeRequestDate;
    private Timestamp hearingNoticePublishDate;
    private Timestamp hearingDate;
    private Timestamp newspaperAffidavitDate;

    public PermitIssuance() {

        super();

        for (int i = 0; i < _messages.length; i++) {
            ValidationMessage msg = new ValidationMessage(_messages[i][0],
                                                          _messages[i][1]);
            validationMessages.put(_messages[i][0], msg);
        }

        setDirty(false);
    }

    /**
     * @param permitID
     * @param type
     * @param not_ready
     */
    public PermitIssuance(Integer permitID, String permitNumber, String type, String statusCd) {

        super();

        for (int i = 0; i < _messages.length; i++) {
            ValidationMessage msg = new ValidationMessage(_messages[i][0],
                                                          _messages[i][1]);
            validationMessages.put(_messages[i][0], msg);
        }

        setPermitId(permitID);
        setPermitNumber(permitNumber);
        setIssuanceTypeCd(type);
        setIssuanceStatusCd(statusCd);
    }

    /**
     * @param old
     */
    public PermitIssuance(PermitIssuance old) {
        super(old);

        if (old != null) {
            setIssuanceId(old.getIssuanceId());
            setIssuanceTypeCd(old.getIssuanceTypeCd());
            setPermitId(old.getPermitId());
            setPermitNumber(old.getPermitNumber());
            setIssuanceDate(old.getIssuanceDate());
            setIssuanceStatusCd(old.getIssuanceStatusCd());
            setPublicNoticeRequestDate(old.getPublicNoticeRequestDate());
            setPublicNoticePublishDate(old.getPublicNoticePublishDate());
            setPublicCommentEndDate(old.getPublicCommentEndDate());
            setHearingRequested(old.isHearingRequested());
            setHearingRequestedDate(old.getHearingRequestedDate());
            setHearingNoticeRequestDate(old.getHearingNoticeRequestDate());
            setHearingNoticePublishDate(old.getHearingNoticePublishDate());
            setHearingDate(old.getHearingDate());
            setNewspaperAffidavitDate(old.getNewspaperAffidavitDate());
            setLastModified(old.getLastModified());
            setDirty(old.isDirty());
        }
    }

    public void populate(ResultSet rs) {
        try {
            setIssuanceId(AbstractDAO.getInteger(rs, "issuance_id"));
            setIssuanceTypeCd(rs.getString("issuance_type_cd"));
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setIssuanceDate(rs.getTimestamp("issuance_date"));
            setIssuanceStatusCd(rs.getString("issuance_status_cd"));
            setPublicNoticeRequestDate(rs.getTimestamp("public_notice_request_date"));
            setPublicNoticePublishDate(rs.getTimestamp("public_notice_publish_date"));
            setPublicCommentEndDate(rs.getTimestamp("public_comment_end_date"));
            setHearingRequested(AbstractDAO.translateIndicatorToBoolean(rs.getString("hearing_requested_flag")));
            setHearingRequestedDate(rs.getTimestamp("hearing_requested_date"));
            setHearingNoticeRequestDate(rs.getTimestamp("hearing_notice_request_date"));
            setHearingNoticePublishDate(rs.getTimestamp("hearing_notice_publish_date"));
            setHearingDate(rs.getTimestamp("hearing_date"));
            setNewspaperAffidavitDate(rs.getTimestamp("newspaper_affidavit_date"));

            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
        finally {
            newObject = false;
        }
    }

    public final Integer getIssuanceId() {
        return issuanceId;
    }

    public final void setIssuanceId(Integer issuanceId) {
        this.issuanceId = issuanceId;
        setDirty(true);
    }

    public final String getIssuanceTypeCd() {
        return issuanceTypeCd;
    }

    public final void setIssuanceTypeCd(String issuanceTypeCd) {

        this.issuanceTypeCd = issuanceTypeCd;
        this.requiredField(issuanceTypeCd, "issuanceTypeCd");

        setDirty(true);
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
        this.requiredField(permitId, "permitId");
        setDirty(true);
    }

    /**
     * Used strictly for field audit log purpose.
     */
    private String getPermitNumber() {
        return this.permitNumber;
    }

    /**
     * Used strictly for field audit log purpose.  Note 
     * that method has package level scope so that it 
     * can be set when a PermitIssuance is attached to a 
     * permit.
     */
    void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }

    public final Timestamp getIssuanceDate() {
        return issuanceDate;
    }

    public final void setIssuanceDate(Timestamp issuanceDate) {
        if (issuanceTypeCd != null) {
            if (issuanceTypeCd.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
                checkDirty("pdid", getPermitNumber(), getIssuanceDate(), issuanceDate);
            }
            else if (issuanceTypeCd.equalsIgnoreCase(PermitIssuanceTypeDef.Final)) {
                checkDirty("pfid", getPermitNumber(), getIssuanceDate(), issuanceDate);
            }
        }
        this.issuanceDate = issuanceDate;
        setDirty(true);
    }

    public final String getIssuanceStatusCd() {
        return issuanceStatusCd;
    }

    public final void setIssuanceStatusCd(String issuanceStatusCd) {
        this.issuanceStatusCd = issuanceStatusCd;
        this.requiredField(issuanceStatusCd, "issuanceStatusCd");
        setDirty(true);
    }

    public final Timestamp getPublicNoticeRequestDate() {
        return publicNoticeRequestDate;
    }

    public final void setPublicNoticeRequestDate(Timestamp publicNoticeRequestDate) {
        this.publicNoticeRequestDate = publicNoticeRequestDate;
        setDirty(true);
    }

    public final Timestamp getPublicNoticePublishDate() {
        return publicNoticePublishDate;
    }

    public final void setPublicNoticePublishDate(Timestamp publicNoticePublishDate) {
                                                 
        if (issuanceTypeCd != null 
            && issuanceTypeCd.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            checkDirty("pdpn", getPermitNumber(), getPublicNoticePublishDate(), publicNoticePublishDate);
        }

        this.publicNoticePublishDate = publicNoticePublishDate;
        if (publicNoticePublishDate != null) {
            Duration d = new Duration();
            d.setDays(30);
            try {
                Timestamp tPlusThirty = 
                    new Timestamp(d.addToDate(new Date(publicNoticePublishDate.getTime())).getTime());
                if (getPublicCommentEndDate() == null || tPlusThirty.compareTo(getPublicCommentEndDate()) > 0) {
                    setPublicCommentEndDate(tPlusThirty);
                }
            }
            catch (Exception e) {
                setPublicCommentEndDate(null);
                e.printStackTrace();
            }
        }
        setDirty(true);
    }

    public final Timestamp getPublicCommentEndDate() {
        return publicCommentEndDate;
    }

    public final void setPublicCommentEndDate(Timestamp commentEndDate) {

        if (issuanceTypeCd != null 
            && issuanceTypeCd.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            checkDirty("pepc", getPermitNumber(), getPublicCommentEndDate(), commentEndDate);
        }
        this.publicCommentEndDate = commentEndDate;
        setDirty(true);
    }

    public final boolean isHearingRequested() {
        return hearingRequestedFlag;
    }

    public final void setHearingRequested(boolean hearingRequested) {
        if (issuanceTypeCd != null 
            && issuanceTypeCd.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            checkDirty("pphr", getPermitNumber(), new Boolean(isHearingRequested()),
                       new Boolean(hearingRequested));
        }
        this.hearingRequestedFlag = hearingRequested;
        setDirty(true);
    }

    public final Timestamp getHearingRequestedDate() {
        return hearingRequestedDate;
    }

    public final void setHearingRequestedDate(Timestamp hearingRequestedDate) {
        this.hearingRequestedDate = hearingRequestedDate;
        setDirty(true);
    }

    public final Timestamp getHearingNoticeRequestDate() {
        return hearingNoticeRequestDate;
    }

    public final void setHearingNoticeRequestDate(Timestamp hearingNoticeRequestDate) {
        this.hearingNoticeRequestDate = hearingNoticeRequestDate;
        setDirty(true);
    }

    public final Timestamp getHearingNoticePublishDate() {
        return hearingNoticePublishDate;
    }

    public final void setHearingNoticePublishDate(Timestamp hearingNoticePublishDate) {
        if (issuanceTypeCd != null 
            && issuanceTypeCd.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            checkDirty("pphn", getPermitNumber(), getHearingNoticePublishDate(),
                       hearingNoticePublishDate);
        }
        this.hearingNoticePublishDate = hearingNoticePublishDate;
        setDirty(true);
    }

    public final Timestamp getHearingDate() {
        return hearingDate;
    }

    public final void setHearingDate(Timestamp hearingDate) {
        if (issuanceTypeCd != null 
            && issuanceTypeCd.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            checkDirty("pphd", getPermitNumber(), getHearingDate(), hearingDate);
        }
        this.hearingDate = hearingDate;
        setDirty(true);
    }

	public Timestamp getNewspaperAffidavitDate() {
		return newspaperAffidavitDate;
	}

	public void setNewspaperAffidavitDate(Timestamp newspaperAffidavitDate) {
		this.newspaperAffidavitDate = newspaperAffidavitDate;
		setDirty(true);
	}
}

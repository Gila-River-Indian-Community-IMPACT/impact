package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;

@SuppressWarnings("serial")
public class SubmissionLog extends BaseDB {
    private String facilityId;
    private String facilityName;
    private String submissionType;
    private Timestamp submissionDt;
    private String gatewayUserName;
    private String gatewaySubmissionId;
    private boolean nonROSubmission = false;
	private Integer documentId;
	private Document attestationDoc;

	public SubmissionLog() {
    }
    
    public SubmissionLog(Task task) {
        setFacilityId(task.getFacilityId());
        setSubmissionType(task.getTaskTypeDescription());
        setSubmissionDt(new Timestamp(System.currentTimeMillis()));
        setGatewayUserName(task.getGatewaySubmiterUserNm());
        setGatewaySubmissionId(task.getSubmissionId());
        setNonROSubmission(task.isNonROSubmission());
        setDocumentId(task.getDocumentId());
        setFacilityName("NA");
    }
    
    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final String getFacilityName() {
        return facilityName;
    }

    public final void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public final String getSubmissionType() {
        return submissionType;
    }

    public final void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public final Timestamp getSubmissionDt() {
        return submissionDt;
    }

    public final void setSubmissionDt(Timestamp submissionDt) {
        this.submissionDt = submissionDt;
    }

    public final String getGatewayUserName() {
        return gatewayUserName;
    }

    public final void setGatewayUserName(String gatewayUserName) {
        this.gatewayUserName = gatewayUserName;
    }

    public final String getGatewaySubmissionId() {
        return gatewaySubmissionId;
    }

    public final void setGatewaySubmissionId(String gatewaySubmissionId) {
        this.gatewaySubmissionId = gatewaySubmissionId;
    }
    public final boolean isNonROSubmission() {
		return nonROSubmission;
	}

	public final void setNonROSubmission(boolean nonROSubmission) {
		this.nonROSubmission = nonROSubmission;
	}

	public final Integer getDocumentId() {
		return documentId;
	}

	public final void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}
    
    public final Document getAttestationDoc() {
		return attestationDoc;
	}

	public final void setAttestationDoc(Document attestationDoc) {
		this.attestationDoc = attestationDoc;
	}

    public void populate(ResultSet rs) throws SQLException {
        try {
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_nm"));
            setSubmissionType(rs.getString("submission_type"));
            setSubmissionDt(rs.getTimestamp("submission_dt"));
            setGatewayUserName(rs.getString("gateway_user_nm"));
            setGatewaySubmissionId(rs.getString("gateway_submission_id"));
            setNonROSubmission(AbstractDAO.translateIndicatorToBoolean(rs.getString("non_ro_submission")));
            setDocumentId(AbstractDAO.getInteger(rs, "document_id"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}

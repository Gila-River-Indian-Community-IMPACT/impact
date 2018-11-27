package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;

@SuppressWarnings("serial")
public class ComplianceDocument extends Document {

    private Integer _reportId;
    private String _docTypeCD;
    private String _issuanceStageFlag;
    private String overloadFileName;

    /**
     * 
     */
    public ComplianceDocument() {
        super();
        this.requiredField(_reportId, "reportId");
        this.requiredField(_docTypeCD, "docTypeCD");
        setDirty(false);
    }

    /**
     * 
     */
    public ComplianceDocument(ComplianceDocument old) {

        super(old);
        if (old != null) {
            setReportId(old.getReportId());
            setDocTypeCD(old.getDocTypeCD());
            setLastModified(old.getLastModified());
            setDirty(old.isDirty());
        }
    }

    public void populate(ResultSet rs) {

        try {
            super.populate(rs);
            setReportId(AbstractDAO.getInteger(rs, "report_id"));
            setDocTypeCD(rs.getString("doc_type_cd"));
            setLastModified(AbstractDAO.getInteger(rs, "ppd_lm"));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getDocTypeCD() {
        return _docTypeCD;
    }

    public final void setDocTypeCD(String docTypeCD) {
        this._docTypeCD = docTypeCD;
        this.requiredField(_docTypeCD, "docTypeCD");
        setDirty(true);
    }

    public final Integer getReportId() {
        return _reportId;
    }

    public final void setReportId(Integer reportId) {
        this._reportId = reportId;
        this.requiredField(_reportId, "reportId");
        setDirty(true);
    }

    public final String getBasePath() {

        String ret = File.separator + "ComplianceReports" + File.separator;
        if (_reportId != null) {
            ret += _reportId + File.separator;
        }
        if (getOverloadFileName() == null) {
            ret += getFileName();
        } else {
            ret += getOverloadFileName();
        }
        return ret;
    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = super.hashCode();

        result = PRIME * result
            + ((_reportId == null) ? 0 : _reportId.hashCode());
        result = PRIME * result
            + ((_docTypeCD == null) ? 0 : _docTypeCD.hashCode());
        result = PRIME * result
            + ((_issuanceStageFlag == null) ? 0 : _issuanceStageFlag.hashCode());
               
        return result;
    }

    @Override
    public final boolean equals(Object obj) {

        if ((obj == null) || !(super.equals(obj))
            || (getClass() != obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final ComplianceDocument other = (ComplianceDocument) obj;

        // Either both null or equal values.
        if (((_reportId == null) && (other.getReportId() != null))
            || ((_reportId != null) && (other.getReportId() == null))
            || ((_reportId != null) && (other.getReportId() != null) 
                && !(_reportId.equals(other.getReportId())))) {
            return false;
        }

        // Either both null or equal values.
        if (((_docTypeCD == null) && (other.getDocTypeCD() != null))
            || ((_docTypeCD != null) && (other.getDocTypeCD() == null))
            || ((_docTypeCD != null)
                && (other.getDocTypeCD() != null) 
                && !(_docTypeCD.equals(other.getDocTypeCD())))) {
                                                       
            return false;
        }

        return true;
    }

    public final String getOverloadFileName() {
        return overloadFileName;
    }

    public final void setOverloadFileName(String overloadFileName) {
        this.overloadFileName = overloadFileName;
    }
}

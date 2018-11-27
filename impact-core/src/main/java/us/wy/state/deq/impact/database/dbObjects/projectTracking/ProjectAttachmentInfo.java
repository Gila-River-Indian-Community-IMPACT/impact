package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.util.DocumentUtil;

@SuppressWarnings("serial")
public class ProjectAttachmentInfo extends BaseDB {
	
	private Integer documentId;
	private String docURL; // path where the attachment is stored in the filesystem
	
	public Integer getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}
	
	public String getDocURL() {
		return docURL;
	}

	public void setDocURL(String docURL) {
		this.docURL = docURL;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setDocumentId(AbstractDAO.getInteger(rs, "document_id"));
			setDocURL(DocumentUtil.getFileStoreBaseURL() + rs.getString("path"));
		}
	}

}

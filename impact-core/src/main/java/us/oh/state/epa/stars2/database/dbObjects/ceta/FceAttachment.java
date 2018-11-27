package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;

@SuppressWarnings("serial")
public class FceAttachment extends Attachment {
	private Integer fceId;
	private String templateDocTypeCd;
	
	public FceAttachment() {
		super();
	}

	public FceAttachment(FceAttachment old) {
		super(old);
		if (old != null) {
			setFceId(old.getFceId());
			if (fceId != null) {
				setObjectId(fceId.toString());
			}
		}
	}
	
	public FceAttachment(Attachment doc) {
		super(doc);
		if (doc != null && doc.getObjectId() != null && doc.getObjectId().length() != 0) {
			setFceId(Integer.decode(doc.getObjectId()));
		}
	}

	public final Integer getFceId() {
		return fceId;
	}

	public final void setFceId(Integer fceId) {
		this.fceId = fceId;
		if (fceId != null) {
			setObjectId(fceId.toString());
		} else {
			setObjectId(null);
		}
	}
	
	public String getTemplateDocTypeCd() {
		return templateDocTypeCd;
	}

	public void setTemplateDocTypeCd(String templateDocTypeCd) {
		this.templateDocTypeCd = templateDocTypeCd;
	}

	@Override
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setFceId(AbstractDAO.getInteger(rs, "fce_id"));
			setTemplateDocTypeCd(rs.getString("template_doc_type_cd"));
		} catch (SQLException e) {
            logger.error("Required field error");
        }
	}
	
	
}

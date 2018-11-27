package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.StAttachmentTypeDef;

@SuppressWarnings("serial")
public class StAttachment extends Attachment {
	private Integer stackTestId;
	
	public StAttachment() {
		super();
	}

	public StAttachment(StAttachment old) {
		super(old);
		if (old != null) {
			setStackTestId(old.getStackTestId());
			if (stackTestId != null) {
				setObjectId(stackTestId.toString());
			}
		}
	}
	
	public StAttachment(Attachment doc) {
		super(doc);
		if (doc != null && doc.getObjectId() != null && doc.getObjectId().length() != 0) {
			setStackTestId(Integer.decode(doc.getObjectId()));
		}
	}

	public final Integer getStackTestId() {
		return stackTestId;
	}

	public final void setStackTestId(Integer stackTestId) {
		this.stackTestId = stackTestId;
		if (stackTestId != null) {
			setObjectId(stackTestId.toString());
		} else {
			setObjectId(null);
		}
	}

	@Override
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setStackTestId(AbstractDAO.getInteger(rs, "stack_test_id"));
		} catch (SQLException e) {
            logger.error("Required field error");
        }
	}
	
	// Use this function to check tradeSecretAllowed.
	@Override
	public boolean isTradeSecretAllowed() {
		boolean ret = false;

		if (getTradeSecretDocId() != null) {
			DefSelectItems tempAttachmentTypesDef = StAttachmentTypeDef.getData().getItems();
			if (tempAttachmentTypesDef != null) {
				BaseDef attachmentDef = tempAttachmentTypesDef.getItem(getDocTypeCd());
				if (attachmentDef != null) {
					StAttachmentTypeDef attachmentTypeDef = (StAttachmentTypeDef)attachmentDef;
					ret = attachmentTypeDef.isTradeSecretAllowed();
				}
			}
		}

		return ret;
	}
}

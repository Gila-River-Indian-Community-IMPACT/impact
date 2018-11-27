package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;

public class FcePermitCondition extends PermitConditionSearchLineItem {

	private static final long serialVersionUID = 1L;

	private Integer fceId;
	private String complianceStatusCd;
	private String comments;

	public FcePermitCondition() {
		super();
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		
		setFceId(AbstractDAO.getInteger(rs, "FCE_ID"));
		setComplianceStatusCd(rs.getString("COMPLIANCE_STATUS_CD"));
		setComments(rs.getString("COMMENTS"));
		setLastModified(AbstractDAO.getInteger(rs, "LAST_MODIFIED"));
	}

	public Integer getFceId() {
		return fceId;
	}

	public void setFceId(Integer fceId) {
		this.fceId = fceId;
	}

	public String getComplianceStatusCd() {
		return complianceStatusCd;
	}

	public void setComplianceStatusCd(String complianceStatusCd) {
		this.complianceStatusCd = complianceStatusCd;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}

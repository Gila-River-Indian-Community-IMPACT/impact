package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class EnforcementMilestone extends BaseDB {
	private Integer milestoneId;
	private Integer enforcementId;
	private String facilityId;
	private Timestamp orderDate;
	private String caseSettlementCd;
	private String milestoneOrRequirements;
	private String paymentAmount;
	private Timestamp deadlineDate;
	private Timestamp completionDate;
	private String memo;

	public void populate(ResultSet rs) throws SQLException {
		setMilestoneId(AbstractDAO.getInteger(rs, "milestone_id"));
		setEnforcementId(AbstractDAO.getInteger(rs, "enforcement_id"));
		setOrderDate(rs.getTimestamp("order_date"));
		setCaseSettlementCd(rs.getString("case_settlement_cd"));
		setMilestoneOrRequirements(rs.getString("milestone_or_requirements"));
		setPaymentAmount(rs.getString("pay_amount"));
		setDeadlineDate(rs.getTimestamp("deadline_date"));
		setCompletionDate(rs.getTimestamp("completion_date"));
		setMemo(rs.getString("memo"));
		setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
	}

	public final Integer getMilestoneId() {
		return milestoneId;
	}

	public final void setMilestoneId(Integer milestoneId) {
		this.milestoneId = milestoneId;
	}

	public final Integer getEnforcementId() {
		return enforcementId;
	}

	public final void setEnforcementId(Integer enforcementId) {
		this.enforcementId = enforcementId;
	}

	public final String getFacilityId() {
		return facilityId;
	}

	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final Timestamp getOrderDate() {
		return orderDate;
	}

	public final void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

	public final String getCaseSettlementCd() {
		return caseSettlementCd;
	}

	public final void setCaseSettlementCd(String caseSettlementCd) {
		this.caseSettlementCd = caseSettlementCd;
	}

	public final String getMilestoneOrRequirements() {
		return milestoneOrRequirements;
	}

	public final void setMilestoneOrRequirements(String milestoneOrRequirements) {
		this.milestoneOrRequirements = milestoneOrRequirements;
	}

	public final String getPaymentAmount() {
		return paymentAmount;
	}

	public final void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public final Timestamp getDeadlineDate() {
		return deadlineDate;
	}

	public final void setDeadlineDate(Timestamp deadlineDate) {
		this.deadlineDate = deadlineDate;
	}

	public final Timestamp getCompletionDate() {
		return completionDate;
	}

	public final void setCompletionDate(Timestamp completionDate) {
		this.completionDate = completionDate;
	}

	public final String getMemo() {
		return memo;
	}

	public final void setMemo(String memo) {
		this.memo = memo;
	}

	public void copy(EnforcementMilestone em) {
		this.milestoneId = em.milestoneId;
		this.facilityId = em.facilityId;
		this.orderDate = em.orderDate;
		this.caseSettlementCd = em.caseSettlementCd;
		this.milestoneOrRequirements = em.milestoneOrRequirements;
		this.paymentAmount = em.paymentAmount;
		this.deadlineDate = em.deadlineDate;
		this.completionDate = em.completionDate;
		this.memo = em.memo;
	}
	
    public final ValidationMessage[] validate() {
    	requiredField(orderDate, "orderDate", "Date of Order");
    	requiredField(caseSettlementCd, "caseSettlementsChoice", "Case Settlements");
    	requiredField(milestoneOrRequirements, "milestoneOrRequirementsChoice", 
    			"Milestone or Requirements");
    	
        return new ArrayList<ValidationMessage>(validationMessages.values())
                .toArray(new ValidationMessage[0]);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((caseSettlementCd == null) ? 0 : caseSettlementCd.hashCode());
		result = prime * result
				+ ((completionDate == null) ? 0 : completionDate.hashCode());
		result = prime * result
				+ ((deadlineDate == null) ? 0 : deadlineDate.hashCode());
		result = prime * result
				+ ((enforcementId == null) ? 0 : enforcementId.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result
				+ ((milestoneId == null) ? 0 : milestoneId.hashCode());
		result = prime
				* result
				+ ((milestoneOrRequirements == null) ? 0
						: milestoneOrRequirements.hashCode());
		result = prime * result
				+ ((orderDate == null) ? 0 : orderDate.hashCode());
		result = prime * result
				+ ((paymentAmount == null) ? 0 : paymentAmount.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnforcementMilestone other = (EnforcementMilestone) obj;
		if (caseSettlementCd == null) {
			if (other.caseSettlementCd != null)
				return false;
		} else if (!caseSettlementCd.equals(other.caseSettlementCd))
			return false;
		if (completionDate == null) {
			if (other.completionDate != null)
				return false;
		} else if (!completionDate.equals(other.completionDate))
			return false;
		if (deadlineDate == null) {
			if (other.deadlineDate != null)
				return false;
		} else if (!deadlineDate.equals(other.deadlineDate))
			return false;
		if (enforcementId == null) {
			if (other.enforcementId != null)
				return false;
		} else if (!enforcementId.equals(other.enforcementId))
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (milestoneId == null) {
			if (other.milestoneId != null)
				return false;
		} else if (!milestoneId.equals(other.milestoneId))
			return false;
		if (milestoneOrRequirements == null) {
			if (other.milestoneOrRequirements != null)
				return false;
		} else if (!milestoneOrRequirements
				.equals(other.milestoneOrRequirements))
			return false;
		if (orderDate == null) {
			if (other.orderDate != null)
				return false;
		} else if (!orderDate.equals(other.orderDate))
			return false;
		if (paymentAmount == null) {
			if (other.paymentAmount != null)
				return false;
		} else if (!paymentAmount.equals(other.paymentAmount))
			return false;
		return true;
	}
    
    

}

package us.wy.state.deq.impact.database.dbObjects.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;

public class EmissionsOffsetAdjustment extends BaseDB {
	
	private static final long serialVersionUID = -4605577435589744155L;

	public static final Double MIN_AMOUNT_VAL = -10000.0;
	public static final Double MAX_AMOUNT_VAL = 100000.0;
	
	private Integer emissionsOffsetAdjustmentId;
	private Integer companyId;
	private Timestamp date;
	private String nonAttainmentAreaCd;
	private String comment;
	private String pollutantCd;
	private Double amount;
	private String includeInTotal;
	
	public EmissionsOffsetAdjustment() {
		super();
	}
	
	public EmissionsOffsetAdjustment(EmissionsOffsetAdjustment old) {
		super(old);
		if(null != old) {
			setEmissionsOffsetAdjustmentId(old.getEmissionsOffsetAdjustmentId());
			setCompanyId(old.getCompanyId());
			setDate(old.getDate());
			setNonAttainmentAreaCd(old.getNonAttainmentAreaCd());
			setComment(old.getComment());
			setPollutantCd(old.getPollutantCd());
			setAmount(old.getAmount());
			setIncludeInTotal(old.getIncludeInTotal());
		}
	}
	
	public Integer getEmissionsOffsetAdjustmentId() {
		return emissionsOffsetAdjustmentId;
	}

	public void setEmissionsOffsetAdjustmentId(Integer emissionsOffsetAdjustmentId) {
		this.emissionsOffsetAdjustmentId = emissionsOffsetAdjustmentId;
	}

	public Integer getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	
	public Timestamp getDate() {
		return date;
	}
	
	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	public String getNonAttainmentAreaCd() {
		return nonAttainmentAreaCd;
	}
	
	public void setNonAttainmentAreaCd(String nonAttainmentAreaCd) {
		this.nonAttainmentAreaCd = nonAttainmentAreaCd;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getPollutantCd() {
		return pollutantCd;
	}
	
	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public String getIncludeInTotal() {
		return includeInTotal;
	}
	
	public void setIncludeInTotal(String includeInTotal) {
		this.includeInTotal = includeInTotal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result
				+ ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime
				* result
				+ ((emissionsOffsetAdjustmentId == null) ? 0
						: emissionsOffsetAdjustmentId.hashCode());
		result = prime * result
				+ ((includeInTotal == null) ? 0 : includeInTotal.hashCode());
		result = prime
				* result
				+ ((nonAttainmentAreaCd == null) ? 0 : nonAttainmentAreaCd
						.hashCode());
		result = prime * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
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
		EmissionsOffsetAdjustment other = (EmissionsOffsetAdjustment) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (emissionsOffsetAdjustmentId == null) {
			if (other.emissionsOffsetAdjustmentId != null)
				return false;
		} else if (!emissionsOffsetAdjustmentId
				.equals(other.emissionsOffsetAdjustmentId))
			return false;
		if (includeInTotal == null) {
			if (other.includeInTotal != null)
				return false;
		} else if (!includeInTotal.equals(other.includeInTotal))
			return false;
		if (nonAttainmentAreaCd == null) {
			if (other.nonAttainmentAreaCd != null)
				return false;
		} else if (!nonAttainmentAreaCd.equals(other.nonAttainmentAreaCd))
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		return true;
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setEmissionsOffsetAdjustmentId(AbstractDAO.getInteger(rs, "emissions_offset_adjustment_id"));
			setCompanyId(AbstractDAO.getInteger(rs, "company_id"));
			setNonAttainmentAreaCd(rs.getString("area_cd"));
			setDate(rs.getTimestamp("date"));
			setComment(rs.getString("comment"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setAmount(AbstractDAO.getDouble(rs, "amount"));
			setIncludeInTotal(rs.getString("include_in_total"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		}
	}
	
	@Override
	public ValidationMessage[] validate() {
		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();

		if(Utility.isNullOrEmpty(nonAttainmentAreaCd)) {
			valMsgs.add(new ValidationMessage(
							"nonAttainmentAreaCd", 
							"Attribute " + "Non-Attainment Area" + " is not set", 
							ValidationMessage.Severity.ERROR,
							"nonAttainmentAreaCd"));
		}
		
		if(Utility.isNullOrEmpty(pollutantCd)) {
			valMsgs.add(new ValidationMessage(
							"pollutantCd", 
							"Attribute " + "Pollutant" + " is not set", 
							ValidationMessage.Severity.ERROR,
							"pollutantCd"));
		}
		
		if(null == date) {
			valMsgs.add(new ValidationMessage(
					"date", 
					"Attribute " + "Date" + " is not set", 
					ValidationMessage.Severity.ERROR,
					"date"));
		}
		
		if(null == amount) {
			valMsgs.add(new ValidationMessage(
							"amount", 
							"Attribute " + "Amount" + " is not set", 
							ValidationMessage.Severity.ERROR,
							"amount"));
		}
		
		if(null != amount
				&& (amount < MIN_AMOUNT_VAL
						|| amount > MAX_AMOUNT_VAL)) {
			valMsgs.add(new ValidationMessage(
							"amount", 
							"Attribute " + "Amount" + " should be between " + MIN_AMOUNT_VAL + " and " + MAX_AMOUNT_VAL,
							ValidationMessage.Severity.ERROR,
							"amount"));
		}
		
		if(Utility.isNullOrEmpty(includeInTotal)) {
			valMsgs.add(new ValidationMessage(
							"includeInTotal", 
							"Attribute " + "Include in Total" + " is not set", 
							ValidationMessage.Severity.ERROR,
							"includeInTotal"));
		}
		
		if(Utility.isNullOrEmpty(comment)) {
			valMsgs.add(new ValidationMessage(
							"comment", 
							"Attribute " + "Comment" + " is not set", 
							ValidationMessage.Severity.ERROR,
							"comment"));
		}
				
		return valMsgs.toArray(new ValidationMessage[0]);
	}
}

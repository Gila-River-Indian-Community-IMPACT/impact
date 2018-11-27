package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef;
import us.oh.state.epa.stars2.def.TransactionTypeDef;

public class PermitChargePayment extends BaseDB {

	private Integer permitCPId;
	private Integer permitId;
	private Timestamp transactionDate;
	private String transactionType;
	private String transmittalNumber;
	private String checkNumber;
	private Double transactionAmount;
	private String noteTxt;

	public PermitChargePayment() {

		this.requiredField(transactionDate, "transaction_dt");
		this.requiredField(transactionType, "transactionType");
		this.requiredField(transactionAmount, "transactionAmount");
		setDirty(false);

	}

	public PermitChargePayment(PermitChargePayment old) {

		super(old);

		setPermitCPId(old.getPermitCPId());
		setPermitId(old.getPermitId());
		setTransactionDate(old.getTransactionDate());
		// setComment(old.getComment());
		setTransmittalNumber(old.getTransmittalNumber());
		setCheckNumber(old.getCheckNumber());
		setTransactionAmount(old.getTransactionAmount());
		setNoteTxt(old.getNoteTxt());

		setLastModified(old.getLastModified());
		setDirty(old.isDirty());
	}

	public void populate(ResultSet rs) {

		try {
			setPermitCPId(AbstractDAO.getInteger(rs, "permit_cp_id"));
			setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
			setTransactionDate(rs.getTimestamp("transaction_dt"));
			setTransactionType(rs.getString("transaction_type"));
			setNoteTxt(rs.getString("comment"));
			setTransmittalNumber(rs.getString("transmittal_num"));
			setCheckNumber(rs.getString("check_num"));
			setTransactionAmount(AbstractDAO.getDouble(rs, "transaction_amt"));

			setLastModified(AbstractDAO.getInteger(rs, "pcp_lm"));
			setDirty(false);
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}

	}

	public final Integer getPermitCPId() {
		return permitCPId;
	}

	public final void setPermitCPId(Integer permitCPId) {
		this.permitCPId = permitCPId;
	}

	public final Integer getPermitId() {
		return permitId;
	}

	public final void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}

	public final Timestamp getTransactionDate() {
		return transactionDate;
	}

	public final void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
		this.requiredField(transactionDate, "transaction_dt");
		setDirty(true);

	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
		this.requiredField(transactionType, "transactionType");
		setDirty(true);
	}

	public String getTransmittalNumber() {
		return transmittalNumber;
	}

	public void setTransmittalNumber(String transmittalNumber) {
		this.transmittalNumber = transmittalNumber;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public Double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(Double transactionAmount) {
		this.transactionAmount = transactionAmount;
		this.requiredField(transactionAmount, "transactionAmount");
		setDirty(true);
	}

	public final String getNoteTxt() {
		return this.noteTxt;
	}

	public final String getShortNoteTxt() {
		if (this.noteTxt == null || this.noteTxt.length() <= 92)
			return this.noteTxt;
		else
			return this.noteTxt.substring(0, 89) + "...";
	}

	public final void setNoteTxt(String noteTxt) {

		this.noteTxt = noteTxt;
		if (this.noteTxt != null && this.noteTxt.length() > 500) {
			String id = "noteId = null";
			if (permitCPId != null) {
				id = "permitCPId = " + permitCPId.toString();
			}
			validationMessages.put("noteTxt", new ValidationMessage("noteTxt",
					"Note is too long (> 500)",
					ValidationMessage.Severity.ERROR, id));
		} else {
			validationMessages.remove("noteTxt");
		}
		this.noteTxt = noteTxt;
		setDirty(true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((noteTxt == null) ? 0 : noteTxt.hashCode());
		result = prime * result
				+ ((checkNumber == null) ? 0 : checkNumber.hashCode());
		result = prime * result
				+ ((permitCPId == null) ? 0 : permitCPId.hashCode());
		result = prime * result
				+ ((permitId == null) ? 0 : permitId.hashCode());
		result = prime
				* result
				+ ((transactionAmount == null) ? 0 : transactionAmount
						.hashCode());
		result = prime * result
				+ ((transactionDate == null) ? 0 : transactionDate.hashCode());
		result = prime * result
				+ ((transactionType == null) ? 0 : transactionType.hashCode());
		result = prime
				* result
				+ ((transmittalNumber == null) ? 0 : transmittalNumber
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PermitChargePayment)) {
			return false;
		}
		PermitChargePayment other = (PermitChargePayment) obj;
		if (noteTxt == null) {
			if (other.noteTxt != null) {
				return false;
			}
		} else if (!noteTxt.equals(other.noteTxt)) {
			return false;
		}
		if (checkNumber == null) {
			if (other.checkNumber != null) {
				return false;
			}
		} else if (!checkNumber.equals(other.checkNumber)) {
			return false;
		}
		if (permitCPId == null) {
			if (other.permitCPId != null) {
				return false;
			}
		} else if (!permitCPId.equals(other.permitCPId)) {
			return false;
		}
		if (permitId == null) {
			if (other.permitId != null) {
				return false;
			}
		} else if (!permitId.equals(other.permitId)) {
			return false;
		}
		if (transactionAmount == null) {
			if (other.transactionAmount != null) {
				return false;
			}
		} else if (!transactionAmount.equals(other.transactionAmount)) {
			return false;
		}
		if (transactionDate == null) {
			if (other.transactionDate != null) {
				return false;
			}
		} else if (!transactionDate.equals(other.transactionDate)) {
			return false;
		}
		if (transactionType == null) {
			if (other.transactionType != null) {
				return false;
			}
		} else if (!transactionType.equals(other.transactionType)) {
			return false;
		}
		if (transmittalNumber == null) {
			if (other.transmittalNumber != null) {
				return false;
			}
		} else if (!transmittalNumber.equals(other.transmittalNumber)) {
			return false;
		}
		return true;
	}
	
	public final boolean isPayment() {

		if (transactionType != null
				&& transactionType.equalsIgnoreCase(NSRBillingChargePaymentTypeDef.PAYMENT)) {
			return true;

		}
		return false;
	}
		
	public String getTransactionAmountString() {
		// format the amount as USD
		Locale locale = new Locale("en", "US");
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		String stringAmount = nf.format(getTransactionAmount());
		
		List<SelectItem> transactionTypes = new ArrayList<SelectItem>();
		String ret = null;
		
		transactionTypes = NSRBillingChargePaymentTypeDef.getChargePaymentTypeData().getItems().getAllItems();
		for(SelectItem si : transactionTypes) {
			if(((String)si.getValue()).equalsIgnoreCase(transactionType)) {
				if(si.getLabel().equalsIgnoreCase(TransactionTypeDef.CREDIT)) {
					ret = "(" + stringAmount + ")";
				} else if(si.getLabel().equalsIgnoreCase(TransactionTypeDef.DEBIT)) {
					ret = stringAmount;
				} else {
					// unknown type
					ret = stringAmount;
				}
				break;
			}
		}
		
		return ret;
	}	
	
	public final boolean isOtherCredit() {

		if (transactionType != null
				&&  transactionType.equalsIgnoreCase(NSRBillingChargePaymentTypeDef.OTHER_CREDIT)) {
			return true;

		}
		return false;
	}
	
}

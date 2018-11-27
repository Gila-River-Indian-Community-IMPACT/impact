package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Locale;

import javax.faces.event.ValueChangeEvent;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

public class NSRFixedCharge extends BaseDB {

	private Integer permitFCId;
	private Integer permitId;
	private Timestamp createdDate;
	private Double amount;
	private String noteTxt;
	private String description;
	private boolean invoiced = false;
	
	private ValueChangeEvent invoicedValueChangeEvent;
	private ValueChangeEvent amountValueChangeEvent;
	
	public NSRFixedCharge() {

		this.requiredField(createdDate, "created_dt");
		this.requiredField(amount, "amount");
		setDirty(false);

	}

	public NSRFixedCharge(NSRFixedCharge old) {

		super(old);

		setPermitFCId(old.getPermitFCId());
		setPermitId(old.getPermitId());
		setCreatedDate(old.getCreatedDate());
		setAmount(old.getAmount());
		setNoteTxt(old.getNoteTxt());
		setDescription(old.getDescription());
		setInvoiced(old.isInvoiced());
		setInvoicedValueChangeEvent(old.getInvoicedValueChangeEvent());
		setAmountValueChangeEvent(old.getAmountValueChangeEvent());
	
		setLastModified(old.getLastModified());
		setDirty(old.isDirty());
	}

	public void populate(ResultSet rs) {

		try {
			setPermitFCId(AbstractDAO.getInteger(rs, "permit_fc_id"));
			setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
			setCreatedDate(rs.getTimestamp("created_dt"));
			setNoteTxt(rs.getString("comment"));
			setAmount(AbstractDAO.getDouble(rs, "amount"));
			setDescription(rs.getString("description"));
			setInvoiced(AbstractDAO.translateIndicatorToBoolean(rs.getString("invoiced")));
	
			setLastModified(AbstractDAO.getInteger(rs, "pcp_lm"));
			setDirty(false);
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}

	}

	public final Integer getPermitFCId() {
		return permitFCId;
	}

	public final void setPermitFCId(Integer permitFCId) {
		this.permitFCId = permitFCId;
	}

	public final Integer getPermitId() {
		return permitId;
	}

	public final void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}

	public final Timestamp getCreatedDate() {
		return createdDate;
	}

	public final void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
		this.requiredField(createdDate, "created_dt");
		setDirty(true);

	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
		this.requiredField(amount, "amount");
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
			if (permitFCId != null) {
				id = "permitFCId = " + permitFCId.toString();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isInvoiced() {
		return invoiced;
	}

	public void setInvoiced(boolean invoiced) {
		this.invoiced = invoiced;
	}

	public ValueChangeEvent getInvoicedValueChangeEvent() {
		return invoicedValueChangeEvent;
	}

	public void setInvoicedValueChangeEvent(
			ValueChangeEvent invoicedValueChangeEvent) {
		this.invoicedValueChangeEvent = invoicedValueChangeEvent;
	}

	public ValueChangeEvent getAmountValueChangeEvent() {
		return amountValueChangeEvent;
	}

	public void setAmountValueChangeEvent(ValueChangeEvent amountValueChangeEvent) {
		this.amountValueChangeEvent = amountValueChangeEvent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((noteTxt == null) ? 0 : noteTxt.hashCode());
		result = prime * result
				+ ((permitFCId == null) ? 0 : permitFCId.hashCode());
		result = prime * result
				+ ((permitId == null) ? 0 : permitId.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((invoiced == false) ? 0 : 1);
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
		if (!(obj instanceof NSRFixedCharge)) {
			return false;
		}
		NSRFixedCharge other = (NSRFixedCharge) obj;
		if (noteTxt == null) {
			if (other.noteTxt != null) {
				return false;
			}
		} else if (!noteTxt.equals(other.noteTxt)) {
			return false;
		}
		if (permitFCId == null) {
			if (other.permitFCId != null) {
				return false;
			}
		} else if (!permitFCId.equals(other.permitFCId)) {
			return false;
		}
		if (permitId == null) {
			if (other.permitId != null) {
				return false;
			}
		} else if (!permitId.equals(other.permitId)) {
			return false;
		}
		if (amount == null) {
			if (other.amount != null) {
				return false;
			}
		} else if (!amount.equals(other.amount)) {
			return false;
		}
		if (createdDate == null) {
			if (other.createdDate != null) {
				return false;
			}
		} else if (!createdDate.equals(other.createdDate)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		
		if(invoiced != other.invoiced) {
			return false;
		}
		
		return true;
	}

	public String getAmountString() {
		// format the amount as USD
		Locale locale = new Locale("en", "US");
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		String stringAmount = nf.format(getAmount());

		String ret = null;
		ret = stringAmount;
		return ret;
	}
	
	public void invoicedValueChanged(ValueChangeEvent vce) {
		setInvoicedValueChangeEvent(vce);
	}
	
	public void amountValueChanged(ValueChangeEvent vce) {
		setAmountValueChangeEvent(vce);
	}
	
	// need a getter/setter wrapper for invoiced field so that sorting on the UI can work correctly
	// this is due to the fact that another jsp file for rendering timesheet rows has
	// a invoiced column with same getter/setters - TFS task 5355
	public final boolean getInvoicedValue() {
		return isInvoiced();
	}
	
	public final void setInvoicedValue(boolean invoiced) {
		setInvoiced(invoiced);
	}

}

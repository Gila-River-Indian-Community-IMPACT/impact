package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;

@SuppressWarnings("serial")
public class GrantProject extends Project {
	
	public static final String GRANTS_ATTRIBUTES_PAGE_VIEW_ID = "grantAttributes:";
	
	private String grantId;
	private String outreachCategoryCd;
	private String grantStatusCd;
	private BigDecimal totalAmount;
	private List<Stars2Object> accountantUserIds = new ArrayList<Stars2Object>();
		
	public GrantProject() {
		super();
	}
	
	public GrantProject(GrantProject old) {
		super(old);
		if(null != old) {
			setGrantId(old.getGrantId());
			setOutreachCategoryCd(old.getOutreachCategoryCd());
			setGrantStatusCd(old.getGrantStatusCd());
			setTotalAmount(old.getTotalAmount());
			setAccountantUserIds(old.getAccountantUserIds());
		}
	}

	public String getGrantId() {
		return grantId;
	}

	public void setGrantId(String grantId) {
		this.grantId = grantId;
	}

	public String getOutreachCategoryCd() {
		return outreachCategoryCd;
	}


	public void setOutreachCategoryCd(String outreachCategoryCd) {
		this.outreachCategoryCd = outreachCategoryCd;
	}

	public String getGrantStatusCd() {
		return grantStatusCd;
	}

	public void setGrantStatusCd(String grantStatusCd) {
		this.grantStatusCd = grantStatusCd;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
	public List<Stars2Object> getAccountantUserIds() {
		return accountantUserIds;
	}

	public void setAccountantUserIds(List<Stars2Object> accountantUserIds) {
		this.accountantUserIds = accountantUserIds;
	}

	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			super.populate(rs);
			setGrantId(rs.getString("grant_id"));
			setOutreachCategoryCd(rs.getString("outreach_category_cd"));
			setGrantStatusCd(rs.getString("grant_status_cd"));
			setTotalAmount(rs.getBigDecimal("total_amount"));
		}
	}
	
	@Override
	public final ValidationMessage[] validate() {
		super.validate();
		
		if (null != this.totalAmount) {
			checkRangeValues(this.totalAmount, new BigDecimal(0),
					new BigDecimal(100000000), 
					GRANTS_ATTRIBUTES_PAGE_VIEW_ID + "totalAmount", 
					"Total Dollar Amount", "totalAmount");
		}
		
		// check for blank and duplicate values for accountant(s)
		Set<Integer> ids = new HashSet<Integer>();
		for(Integer obj : Stars2Object.fromStar2IntObject(this.accountantUserIds)) {
			String fieldName = GRANTS_ATTRIBUTES_PAGE_VIEW_ID + "accountantUserIds";
			if(null == obj || obj.intValue() == 0) {
				validationMessages
						.put(fieldName,
								new ValidationMessage(
										fieldName,
										"Empty value in a row of the Accountant Contact(s) table",
										ValidationMessage.Severity.ERROR,
										"accountantUserIds"));
			} else {
				if(ids.contains(obj)) {
					validationMessages
					.put(fieldName,
							new ValidationMessage(
									fieldName,
									"Duplicate value in Accountant Contact(s) table",
									ValidationMessage.Severity.ERROR,
									"accountantUserIds"));
				} else {
					ids.add(obj);
				}
			}
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	public void addAccountant() {
		if(null == accountantUserIds) {
			setAccountantUserIds(new ArrayList<Stars2Object>());
		}
		
		accountantUserIds.add(new Stars2Object());
	}
}

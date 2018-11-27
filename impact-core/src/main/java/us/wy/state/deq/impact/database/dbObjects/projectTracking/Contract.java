package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;

@SuppressWarnings("serial")
public class Contract extends Project {
	
	public static final String CONTRACT_ATTRIBUTES_PAGE_VIEW_ID = "contractAttributes:";
	
	private String contractId;
	private String contractTypeCd;
	private String contractStatusCd;
	private String contractNumber;
	private Timestamp contractExpirationDate;
	
	private String vendorName;
	private String vendorNumber;
	
	private Timestamp monitoringOperationsEndDate;
	
	private String MSANumber;
	private Long AGHeatTicketNumber;
	private String OCIOApproval;
	
	private BigDecimal totalAward;
	
	private List<Stars2Object> accountantUserIds = new ArrayList<Stars2Object>();
	private List<Budget> budgetList = new ArrayList<Budget>();
	
	public Contract() {
		super();
	}
	
	public Contract(Contract con) {
		super(con);
		if(null != con) {
			setContractId(con.getContractId());
			setContractTypeCd(con.getContractTypeCd());
			setContractStatusCd(con.getContractStatusCd());
			setContractNumber(con.getContractNumber());
			setContractExpirationDate(con.getContractExpirationDate());
			setVendorName(con.getVendorName());
			setVendorNumber(con.getVendorNumber());
			setMonitoringOperationsEndDate(con.getMonitoringOperationsEndDate());
			setMSANumber(con.getMSANumber());
			setAGHeatTicketNumber(con.getAGHeatTicketNumber());
			setOCIOApproval(con.getOCIOApproval());
			setTotalAward(con.getTotalAward());
			setAccountantUserIds(con.getAccountantUserIds());
			setBudgetList(con.getBudgetList());
		}
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getContractTypeCd() {
		return contractTypeCd;
	}

	public void setContractTypeCd(String contractTypeCd) {
		this.contractTypeCd = contractTypeCd;
	}

	public String getContractStatusCd() {
		return contractStatusCd;
	}

	public void setContractStatusCd(String contractStatusCd) {
		this.contractStatusCd = contractStatusCd;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public Timestamp getContractExpirationDate() {
		return contractExpirationDate;
	}

	public void setContractExpirationDate(Timestamp contractExpirationDate) {
		this.contractExpirationDate = contractExpirationDate;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorNumber() {
		return vendorNumber;
	}

	public void setVendorNumber(String vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

	public Timestamp getMonitoringOperationsEndDate() {
		return monitoringOperationsEndDate;
	}

	public void setMonitoringOperationsEndDate(Timestamp monitoringOperationsEndDate) {
		this.monitoringOperationsEndDate = monitoringOperationsEndDate;
	}

	public String getMSANumber() {
		return MSANumber;
	}

	public void setMSANumber(String MSANumber) {
		this.MSANumber = MSANumber;
	}

	public Long getAGHeatTicketNumber() {
		return AGHeatTicketNumber;
	}

	public void setAGHeatTicketNumber(Long AGHeatTicketNumber) {
		this.AGHeatTicketNumber = AGHeatTicketNumber;
	}
	
	public String getOCIOApproval() {
		return OCIOApproval;
	}

	public void setOCIOApproval(String OCIOApproval) {
		this.OCIOApproval = OCIOApproval;
	}

	public BigDecimal getTotalAward() {
		return totalAward;
	}

	public void setTotalAward(BigDecimal totalAward) {
		this.totalAward = totalAward;
	}

	public List<Stars2Object> getAccountantUserIds() {
		if(null == accountantUserIds) {
			setAccountantUserIds(new ArrayList<Stars2Object>());
		}
		return accountantUserIds;
	}

	public void setAccountantUserIds(List<Stars2Object> accountantUserIds) {
		this.accountantUserIds = accountantUserIds;
	}
	
	public List<Budget> getBudgetList() {
		if(null == budgetList) {
			setBudgetList(new ArrayList<Budget>());
		}
		return budgetList;
	}

	public void setBudgetList(List<Budget> budgetList) {
		this.budgetList = budgetList;
	}

	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			super.populate(rs);
			setContractId(rs.getString("contract_id"));
			setContractTypeCd(rs.getString("contract_type_cd"));
			setContractStatusCd(rs.getString("contract_status_cd"));
			setContractNumber(rs.getString("contract_number"));
			setContractExpirationDate(rs.getTimestamp("contract_expiration_date"));
			setVendorName(rs.getString("vendor_name"));
			setVendorNumber(rs.getString("vendor_number"));
			setMonitoringOperationsEndDate(rs.getTimestamp("monitoring_operations_end_date"));
			setMSANumber(rs.getString("msa_number"));
			setAGHeatTicketNumber(AbstractDAO.getLong(rs, "ag_heat_ticket_number"));
			setOCIOApproval(rs.getString("ocio_approval"));
			setTotalAward(rs.getBigDecimal("total_award"));
		}
	}
	
	@Override
	public final ValidationMessage[] validate() {
		super.validate();
		
		if (null != this.totalAward) {
			checkRangeValues(this.totalAward, new BigDecimal(0),
					new BigDecimal(100000000), 
					CONTRACT_ATTRIBUTES_PAGE_VIEW_ID + "totalAward", 
					"Total Award", "totalAward");
		}
		
		if(!Utility.isNullOrEmpty(this.vendorNumber)) {
			String pattern = "VC(\\d{1,16})";
			if(!this.vendorNumber.matches(pattern)) {
				String fieldName = CONTRACT_ATTRIBUTES_PAGE_VIEW_ID + "vendorNumber"; 
				validationMessages.put(fieldName,
						new ValidationMessage(
								fieldName,
								"Vendor number should be of the format VC followed by up to 16-digits",
								ValidationMessage.Severity.ERROR,
								"vendorNumber"));
			}
		}
		
		if(!Utility.isNullOrEmpty(this.MSANumber)) {
			String pattern = "([0-9a-zA-Z]{1,12})";
			if(!this.MSANumber.matches(pattern)) {
				String fieldName = CONTRACT_ATTRIBUTES_PAGE_VIEW_ID + "MSANumber"; 
				validationMessages.put(fieldName,
						new ValidationMessage(
								fieldName,
								"Not a valid value. MSA Number must be a alpha-numeric value with up to 12-characters.",
								ValidationMessage.Severity.ERROR,
								"MSANumber"));
			}
		}
		
		if(!Utility.isNullOrEmpty(this.contractNumber)) {
			String pattern = "CON-([0-9a-zA-Z]{4})-([0-9a-zA-Z]{4})";
			if(!this.contractNumber.matches(pattern)) {
				String fieldName = CONTRACT_ATTRIBUTES_PAGE_VIEW_ID + "contractNumber"; 
				validationMessages.put(fieldName,
						new ValidationMessage(
								fieldName,
								this.contractNumber + " does not match the CON-XXXX-XXXX pattern",
								ValidationMessage.Severity.ERROR,
								"contractNumber"));
			}
		}
		
		if(null != this.AGHeatTicketNumber) {
			String pattern = "(\\d{1,10})";
			if(!this.AGHeatTicketNumber.toString().matches(pattern)) {
				String fieldName = CONTRACT_ATTRIBUTES_PAGE_VIEW_ID + "AGHeatTicketNumber"; 
				validationMessages.put(fieldName,
						new ValidationMessage(
								fieldName,
								"Not a valid value. AG Heat Ticket Number must be a numeric value with up to 10-digits.",
								ValidationMessage.Severity.ERROR,
								"AGHeatTicketNumber"));
			}
		}
		
		
		// check for blank and duplicate values for accountant(s)
		Set<Integer> ids = new HashSet<Integer>();
		for(Integer obj : Stars2Object.fromStar2IntObject(this.accountantUserIds)) {
			String fieldName = CONTRACT_ATTRIBUTES_PAGE_VIEW_ID + "accountantUserIds";
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

package us.wy.state.deq.impact.database.dbObjects.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;

public class Company extends CompanyNode {
	
	private static final long serialVersionUID = -8661757300702523900L;

	// TODO Get correct company attributes for company details
	private Integer companyId;
	private String name;
	private String alias;
	private String phone;
	private String fax;
	private Address address;
	private String cmpId;
	private ExternalOrganization externalOrganization;

	// Note initialization
	private ArrayList<CompanyNote> notes = new ArrayList<CompanyNote>(0);

	// Facilities
	private FacilityList[] facilities;

	// Contacts
	private Contact[] contacts;

	private transient Timestamp startDate; // Used in memory not in database
	private transient Timestamp endDate; // Used in memory not in database

	// CROMERR
	private String externalCompanyId;
	
	// Paykey and Vendor# information needed in some cases for NSR Billing
	private String payKey;
	private Long vendorNumber;
	
	// Offset tracking info
	private CompanyEmissionsOffsetRow[] emissionsOffsetRows;
	private EmissionsOffsetAdjustment[] emissionsOffsetAdjustments;
	

	public Company() {
		super();
		address = new Address();
		requiredFields();
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCmpId() {
		return cmpId;
	}
	
	public int getFacilityCount() {
		return null != this.facilities? this.facilities.length : 0;
	}
	

	public ExternalOrganization getExternalOrganization() {
		return externalOrganization;
	}

	public void setExternalOrganization(ExternalOrganization externalOrganization) {
		this.externalOrganization = externalOrganization;
	}

	// TODO Correct getter and setter methods as attributes for company used
	public final String getName() {
		return name;
	}

	public final String getAlias() {
		return alias;
	}

	public final void setName(String name) {
		requiredField(name, "name", "Company Name", "company");
		checkDirty("cnm", getCmpId(), this.name, name);
		fieldChangeEventLog("cnme", "N/A", this.name, name, true);
		this.name = name;
	}

	public final void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Address.
	 * 
	 * @return Address
	 */
	public final Address getAddress() {
		return address;
	}

	/**
	 * Address.
	 * 
	 * @param address
	 */
	public final void setAddress(Address address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}
	
	public CompanyEmissionsOffsetRow[] getEmissionsOffsetRows() {
		return emissionsOffsetRows;
	}

	public void setEmissionsOffsetRows(
			CompanyEmissionsOffsetRow[] emissionsOffsetRows) {
		this.emissionsOffsetRows = emissionsOffsetRows;
	}
	
	public EmissionsOffsetAdjustment[] getEmissionsOffsetAdjustments() {
		return emissionsOffsetAdjustments;
	}

	public void setEmissionsOffsetAdjustments(
			EmissionsOffsetAdjustment[] emissionsOffsetAdjustments) {
		this.emissionsOffsetAdjustments = emissionsOffsetAdjustments;
	}

	public final ValidationMessage[] validate() {
		basicValidations();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public final HashMap<String, ValidationMessage> validationMessagesForCompany() {
		basicValidations();

		return validationMessages;
	}

	private void basicValidations() {
		this.clearValidationMessages();
		requiredFields();

		// validate given address -- first check to see if fields are empty
		if (this.hasAddress()) {
			if (!address.validateAddress()) {
				address.getValidationMessages().remove("countyCd");
				address.getValidationMessages().remove("latitude");
				address.getValidationMessages().remove("longitude");
				address.getValidationMessages().remove("section");
				address.getValidationMessages().remove("township");
				address.getValidationMessages().remove("range");
				address.getValidationMessages().remove("start_dt");
				address.getValidationMessages().remove("districtCd");
				address.getValidationMessages().remove("state");
				for (ValidationMessage temp : address.validate()) {
					validationMessages.put(temp.getProperty(), temp);
				}
			}
		}

	}

	public void requiredFields() {
		// required fields in order to pass validation
		requiredField(name, "name", "Company Name", "company");

		if (this.hasAddress()) {
			requiredField(this.address.getCityName(), "cityName", "City",
					"company");
			requiredField(this.address.getAddressLine1(), "addressLine1",
					"Address 1", "company");
			requiredField(this.address.getCountryCd(), "country", "Country",
					"company");
			requiredField(this.address.getZipCode(), "zipCode", "Zip Code",
					"company");
			requiredField(this.address.getState(), "state", "State", "company");
		}
		return;
	}

	/**
	 * Determines whether there is any part of any address.
	 * 
	 * @return true if there is part of an address available
	 */
	public boolean hasAddress() {
		boolean hasAddress = false;

		// check and see if any part of address has been filled
		if (this.address.getAddressLine1() != null
				&& this.address.getAddressLine1().length() > 0
				|| this.address.getCityName() != null
				&& this.address.getCityName().length() > 0
				|| this.address.getAddressLine2() != null
				&& this.address.getAddressLine2().length() > 0
				|| this.address.getZipCode() != null
				&& this.address.getZipCode().length() > 0
				|| this.address.getState() != null
				&& this.address.getState().length() > 0
				|| this.address.getCountryCd() != null
				&& this.address.getCountryCd().length() > 0) {

			hasAddress = true;
		}

		return hasAddress;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			rs.getInt("address_id");
			if (!rs.wasNull()) {
				Address address = new Address();
				address.populate(rs);
				setAddress(address);
			}

			setCompanyId(AbstractDAO.getInteger(rs, "company_id"));
			setCmpId(rs.getString("cmp_id"));
			setName(rs.getString("name"));
			setAlias(rs.getString("alias"));
			setPhone(rs.getString("phone"));
			setFax(rs.getString("fax"));
			setLastModified(rs.getInt("last_modified"));

			String externalId = rs.getString("envite_company_id");
			if (externalId == null) {
				setExternalCompanyId(null);
			} else {
				try {
					setExternalCompanyId(externalId);
				} catch (NumberFormatException nfex) {

				}
			}
			setPayKey(rs.getString("paykey"));
			setVendorNumber(AbstractDAO.getLong(rs, "vendor_number"));
		} catch (SQLException sqle) {
			logger.error("Invalid column in populate", sqle);
		} finally {
			newObject = false;
		}
	}

	/**
	 * Populates an owning company object.
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public void populateOwner(ResultSet rs) throws SQLException {
		try {
			rs.getInt("address_id");
			if (!rs.wasNull()) {
				Address address = new Address();
				address.populate(rs);
				setAddress(address);
			}

			setCompanyId(AbstractDAO.getInteger(rs, "company_id"));
			setCmpId(rs.getString("cmp_id"));
			setName(rs.getString("name"));
			setAlias(rs.getString("alias"));
			setPhone(rs.getString("phone"));
			setFax(rs.getString("fax"));
			setStartDate(rs.getTimestamp("start_date"));
			setEndDate(rs.getTimestamp("end_date"));
		} catch (SQLException sqle) {
			logger.error("Invalid column in populate", sqle);
		} finally {
			newObject = false;
		}
	}

	public final void setAddresses(List<Address> addresses) {
		// TODO: what is the purpose of this behavior?
		if (addresses != null) {
			for (Address tempAddr : addresses) {
				setAddress(tempAddr);
			}
		} else {
			this.address = null;
		}
	}

	// ******************** \\
	// Notes Method Section \\
	// ******************** \\

	public final ArrayList<CompanyNote> getNotes() {
		return notes;
	}

	public final void setNotes(ArrayList<CompanyNote> notes) {
		if (notes == null) {
			notes = new ArrayList<CompanyNote>(0);
		}
		this.notes = notes;
	}

	// ************************* \\
	// Facilities Method Section \\
	// ************************* \\

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public final FacilityList[] getFacilities() {
		return facilities;
	}

	public final void setFacilities(FacilityList[] facilities) {
		if (facilities == null) {
			facilities = new FacilityList[0];
		}
		this.facilities = facilities;
	}

	public Contact[] getContacts() {
		return contacts;
	}

	public void setContacts(Contact[] contacts) {
		this.contacts = contacts;
	}

	public String getExternalCompanyId() {
		return externalCompanyId;
	}

	public void setExternalCompanyId(String externalCompanyId) {
		this.externalCompanyId = externalCompanyId;
	}

	public String getPayKey() {
		return payKey;
	}

	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}

	public Long getVendorNumber() {
		return vendorNumber;
	}

	public void setVendorNumber(Long vendorNumber) {
		this.vendorNumber = vendorNumber;
	}

}

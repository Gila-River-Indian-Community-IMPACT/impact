package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.convert.ConverterException;

import org.gricdeq.impact.ExternalUser;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.contact.ContactNote;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.framework.util.PhoneNumberHandler;
import us.oh.state.epa.stars2.framework.util.ResultSetHelper;
import us.oh.state.epa.stars2.framework.util.ValidateEmailAddress;
import us.oh.state.epa.stars2.webcommon.converter.PhoneNumberConverter;

/**
 * Contact.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.0
 * @author Tom Dixon
 */
public class Contact extends BaseDB {
	
	private static final long serialVersionUID = -4925330237391502077L;

	private Integer contactId;
	private String facilityId;
	private String titleCd;
	private String firstNm;
	private String middleNm;
	private String lastNm;
	private String suffixCd;
	private String phoneNo;
	private String phoneExtensionVal;
	private String secondaryPhoneNo;
	private String secondaryExtensionVal;
	private String mobilePhoneNo;
	private String faxNo;
	private String pagerNo;
	private String pagerPinNo;
	private String emailAddressTxt;
	private String emailAddressTxt2;
	private String emailPagerAddress;
	private Integer maxEmailPagerCharsNum;
	private Address address;
	private String companyTitle;
	private List<ContactType> contactTypes = new ArrayList<ContactType>(0);
	private String preferredName;
	private String cntId;
	private Integer companyId;
	private boolean active;
	private String companyName;  // Contact's company name
	private String cmpId;
	private transient Timestamp startDate; // Used in memory not in database
	private transient Timestamp endDate; // Used in memory not in database
	private transient PhoneNumberConverter phoneConvert = new PhoneNumberConverter();
	private ExternalUser externalUser;
	private List<ContactRole> contactRoles = new ArrayList<ContactRole>(0);
	

	private static final String fieldIdPrefix = "contact:";
	private static final String firstNmFieldId = fieldIdPrefix + "firstNm";
	private static final String lastNmFieldId = fieldIdPrefix + "lastNm";
	private static final String emailFieldId = fieldIdPrefix + "emailAddressTxt";
	private static final String phoneNoFieldId = fieldIdPrefix + "phoneNo";
	private static final String companyFieldId = fieldIdPrefix + "contactCompany";
	private static final String cityFieldId = fieldIdPrefix + "cityName";
	private static final String addressFieldId = fieldIdPrefix + "addressLine1";
	private static final String zipFieldId = fieldIdPrefix + "zipCode";
	private static final String stateFieldId = fieldIdPrefix + "state";
	
	private ArrayList<ContactNote> notes = new ArrayList<ContactNote>(0);

	public Contact() {
		this.address = new Address();
		requiredFields();
	}

	public Contact(Contact old) {
		super(old);

		if (old != null) {
			setContactId(old.getContactId());
			setCntId(old.getCntId());
			setStartDate(old.getStartDate());
			setEndDate(old.getEndDate());
			setFacilityId(old.getFacilityId());
			setTitleCd(old.getTitleCd());
			setFirstNm(old.getFirstNm());
			setMiddleNm(old.getMiddleNm());
			setLastNm(old.getLastNm());
			setSuffixCd(old.getSuffixCd());
			setPhoneNo(old.getPhoneNo());
			setPhoneExtensionVal(old.getPhoneExtensionVal());
			setSecondaryPhoneNo(old.getSecondaryPhoneNo());
			setSecondaryExtensionVal(old.getSecondaryExtensionVal());
			setMobilePhoneNo(old.getMobilePhoneNo());
			setFaxNo(old.getFaxNo());
			setPagerNo(old.getPagerNo());
			setPagerPinNo(old.getPagerPinNo());
			setEmailAddressTxt(old.getEmailAddressTxt());
			setEmailAddressTxt2(old.getEmailAddressTxt2());
			setEmailPagerAddress(old.getEmailPagerAddress());
			setMaxEmailPagerCharsNum(old.getMaxEmailPagerCharsNum());
			setCompanyTitle(old.getCompanyTitle());
			setCompanyId(old.getCompanyId());
			setCmpId(old.getCmpId());
			setCompanyName(old.getCompanyName());
//			setExternalUser(new ExternalUser(old.getExternalUser()));
			setLastModified(old.getLastModified());

			if (old.getAddress() != null) {
				setAddress(new Address(old.getAddress()));
			}

			contactTypes = new ArrayList<ContactType>(old.getContactTypes()
					.size());
			for (ContactType tempType : old.getContactTypes()) {
				contactTypes.add(tempType);
			}
		}
		requiredFields();
	}
	
	public Contact(ExternalUser externalUser) {
		super();
		this.externalUser = externalUser;
	}

	private void requiredFields() {
		requiredField(firstNm, firstNmFieldId, "First Name");
		requiredField(lastNm, lastNmFieldId, "Last Name");
		requiredField(emailAddressTxt, emailFieldId, "Email");
		requiredField(phoneNo, phoneNoFieldId, "Primary Phone No");
		requiredField(companyId, companyFieldId, "Company Name");

		// addresses are always required for contacts
		if (this.address != null) {
			requiredField(this.address.getCityName(), cityFieldId, "City",
					"contact");
			requiredField(this.address.getAddressLine1(), addressFieldId,
					"Address 1", "contact");
			requiredField(this.address.getZipCode(), zipFieldId, "Zip Code",
					"contact");
			requiredField(this.address.getState(), stateFieldId, "State", "contact");
		}
	}

	
	public List<ContactRole> getContactRoles() {
		return contactRoles;
	}

	public void setContactRoles(List<ContactRole> contactRoles) {
		this.contactRoles = contactRoles;
		
		if (null != this.externalUser) {
			for (ContactRole contactRole : contactRoles) {
				this.externalUser.getRoles().add(contactRole.getExternalRole());
			}
		}
	}

	public ExternalUser getExternalUser() {
		return externalUser;
	}

	public void setExternalUser(ExternalUser externalUser) {
		this.externalUser = externalUser;
	}

	public String getCntId() {
		return cntId;
	}

	public void setCntId(String cntId) {
		this.cntId = cntId;
	}

	public ArrayList<ContactNote> getNotes() {
		return notes;
	}

	public void setNotes(ArrayList<ContactNote> notes) {
		this.notes = notes;
	}

	public final String getName() {
		if (lastNm == null || lastNm.equals("")) {
			return companyName == null ? "NO-NAME" : companyName;
		}
		String name = lastNm;
		if (firstNm != null) {
			name += ", " + firstNm;
			if (middleNm != null) {
				name += " " + middleNm;
			}
		}
		return name;
	}

	public final String getNameOnly() {
		if (lastNm == null || lastNm.equals("")) {
			return "";
		}
		String name = lastNm;
		if (firstNm != null) {
			name += ", " + firstNm;
			if (middleNm != null) {
				name += " " + middleNm;
			}
		}
		return name;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public String getCmpId() {
		return this.cmpId;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void appendContactInfo(StringBuffer buf) {
		appendLast(buf, "", getNameOnly());
		appendLast(buf, " ", companyTitle);
		appendLast(buf, " ", companyName);
		appendLast(buf, "", getAddress().getAddressLine1());
		appendLast(buf, "", getAddress().getAddressLine2());
		appendNext(buf, "", getAddress().getCityName());
		appendNext(buf, ", ", getAddress().getState());
		appendLast(buf, " ", getAddress().getZipCode());
		if (phoneNo != null) {
			try {
				appendNext(buf, "Phone Number: ",
						phoneConvert.formatPhoneNumber(phoneNo));
			} catch (ConverterException ce) {
				appendNext(buf, "Phone Number: ", phoneNo);
			}
		}
		appendLast(buf, "  EXT: ", phoneExtensionVal);
	}

	public boolean sameContact(Contact c) {
		return sameIgnoreCaseString(this.getNameOnly(), c.getNameOnly())
				&& sameInteger(this.getCompanyId(),
						c.getCompanyId());
	}

	public static boolean sameIgnoreCaseString(String s1, String s2) {
		if (s1 == null && s2 != null || s1 != null && s2 == null) {
			return false;
		}
		if (s1 == null && s2 == null) {
			return true;
		}
		return s1.equalsIgnoreCase(s2);
	}
	
	public static boolean sameInteger(Integer s1, Integer s2) {
		if (s1 == null && s2 != null || s1 != null && s2 == null) {
			return false;
		}
		if (s1 == null && s2 == null) {
			return true;
		}
		return s1.equals(s2);
	}

	private static void appendNext(StringBuffer buf, String before, String s) {
		if (s != null) {
			buf.append(before + s);
		}
	}

	private static void appendLast(StringBuffer buf, String before, String s) {
		if (s != null) {
			buf.append(before + s + "<br>");
		}
	}

	public final Timestamp getEndDate() {
		return endDate;
	}

	public final void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public final String getFacilityId() {
		return facilityId;
	}

	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final Timestamp getStartDate() {
		return startDate;
	}

	public final void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	/**
	 * ContactId.
	 * 
	 * @return Integer
	 */
	public final Integer getContactId() {
		return contactId;
	}

	/**
	 * ContactId.
	 * 
	 * @param contactId
	 */
	public final void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	/**
	 * AddressId.
	 * 
	 * @return Integer
	 */
	public final Integer getAddressId() {
		logger.debug("******** calling contact.getAddressId ********");
		return getAddress().getAddressId();
	}

	public void setAddressId(Integer id) {
		logger.debug("******** calling contact.setAddressId ********");
		getAddress().setAddressId(id);
	}

	/**
	 * TitleCd.
	 * 
	 * @return String
	 */
	public final String getTitleCd() {
		return titleCd;
	}

	/**
	 * TitleCd.
	 * 
	 * @param titleCd
	 */
	public final void setTitleCd(String titleCd) {
		this.titleCd = titleCd;
	}

	/**
	 * FirstNm.
	 * 
	 * @return String
	 */
	public final String getFirstNm() {
		return firstNm;
	}

	/**
	 * FirstNm.
	 * 
	 * @param firstNm
	 */
	public final void setFirstNm(String firstNm) {
		requiredField(firstNm, firstNmFieldId, "First Name");
		this.firstNm = firstNm;
	}

	/**
	 * MiddleNm.
	 * 
	 * @return String
	 */
	public final String getMiddleNm() {
		return middleNm;
	}

	/**
	 * MiddleNm.
	 * 
	 * @param middleNm
	 */
	public final void setMiddleNm(String middleNm) {
		this.middleNm = middleNm;
	}

	public final String getMiddleInitial() {
		String mi = null;
		if (middleNm != null && middleNm.length() >= 1) {
			mi = middleNm.substring(0, 1);
		}
		return mi;
	}

	/**
	 * LastNm.
	 * 
	 * @return String
	 */
	public final String getLastNm() {
		return lastNm;
	}

	/**
	 * LastNm.
	 * 
	 * @param lastNm
	 */
	public final void setLastNm(String lastNm) {
		requiredField(lastNm, lastNmFieldId, "Last Name");
		this.lastNm = lastNm;
	}

	/**
	 * SuffixCd.
	 * 
	 * @return String
	 */
	public final String getSuffixCd() {
		return suffixCd;
	}

	/**
	 * SuffixCd.
	 * 
	 * @param suffixCd
	 */
	public final void setSuffixCd(String suffixCd) {
		this.suffixCd = suffixCd;
	}

	/**
	 * PhoneNo.
	 * 
	 * @return String
	 */
	public final String getPhoneNo() {
		return phoneNo;
	}

	/**
	 * PhoneNo.
	 * 
	 * @param phoneNo
	 */
	public final void setPhoneNo(String phoneNo) {
		requiredField(phoneNo, "phoneNo", "Work Phone No");
		this.phoneNo = phoneNo;
	}

	/**
	 * Unformatted PhoneNo.
	 * 
	 * @return String
	 */
	public final String getUnformattedPhoneNo() {
		return phoneNo;
	}

	/**
	 * PhoneExtensionVal.
	 * 
	 * @return String
	 */
	public final String getPhoneExtensionVal() {
		return phoneExtensionVal;
	}

	/**
	 * PhoneExtensionVal.
	 * 
	 * @param phoneExtensionVal
	 */
	public final void setPhoneExtensionVal(String phoneExtensionVal) {
		this.phoneExtensionVal = phoneExtensionVal;
	}

	/**
	 * SecondaryPhoneNo.
	 * 
	 * @return String
	 */
	public final String getSecondaryPhoneNo() {
		return secondaryPhoneNo;
	}

	/**
	 * SecondaryPhoneNo.
	 * 
	 * @param secondaryPhoneNo
	 */
	public final void setSecondaryPhoneNo(String secondaryPhoneNo) {
		this.secondaryPhoneNo = secondaryPhoneNo;
	}

	/**
	 * SecondaryPhoneNo.
	 * 
	 * @return String
	 */
	public final String getUnformattedSecondaryPhoneNo() {
		return secondaryPhoneNo;
	}

	/**
	 * SecondaryExtensionVal.
	 * 
	 * @return String
	 */
	public final String getSecondaryExtensionVal() {
		return secondaryExtensionVal;
	}

	/**
	 * SecondaryExtensionVal.
	 * 
	 * @param secondaryExtensionVal
	 */
	public final void setSecondaryExtensionVal(String secondaryExtensionVal) {
		this.secondaryExtensionVal = secondaryExtensionVal;
	}

	/**
	 * MobilePhoneNo.
	 * 
	 * @return String
	 */
	public final String getMobilePhoneNo() {
		return mobilePhoneNo;
	}

	/**
	 * MobilePhoneNo.
	 * 
	 * @param mobilePhoneNo
	 */
	public final void setMobilePhoneNo(String mobilePhoneNo) {
		this.mobilePhoneNo = mobilePhoneNo;
	}

	/**
	 * MobilePhoneNo.
	 * 
	 * @return String
	 */
	public final String getUnformattedMobilePhoneNo() {
		return mobilePhoneNo;
	}

	/**
	 * FaxNo.
	 * 
	 * @return String
	 */
	public final String getFaxNo() {
		return faxNo;
	}

	/**
	 * FaxNo.
	 * 
	 * @param faxNo
	 */
	public final void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	/**
	 * FaxNo.
	 * 
	 * @return String
	 */
	public final String getUnformattedFaxNo() {
		return faxNo;
	}

	/**
	 * PagerNo.
	 * 
	 * @return String
	 */
	public final String getPagerNo() {
		return pagerNo;
	}

	/**
	 * PagerNo.
	 * 
	 * @param pagerNo
	 */
	public final void setPagerNo(String pagerNo) {
		this.pagerNo = pagerNo;
	}

	/**
	 * PagerNo.
	 * 
	 * @return String
	 */
	public final String getUnformattedPagerNo() {
		return pagerNo;
	}

	/**
	 * PagerPinNo.
	 * 
	 * @return String
	 */
	public final String getPagerPinNo() {
		return pagerPinNo;
	}

	/**
	 * PagerPinNo.
	 * 
	 * @param pagerPinNo
	 */
	public final void setPagerPinNo(String pagerPinNo) {
		this.pagerPinNo = pagerPinNo;
	}

	/**
	 * EmailAddressTxt.
	 * 
	 * @return String
	 */
	public final String getEmailAddressTxt() {
		return emailAddressTxt;
	}

	/**
	 * EmailAddressTxt.
	 * 
	 * @param emailAddressTxt
	 */
	public final void setEmailAddressTxt(String emailAddressTxt) {
		requiredField(emailAddressTxt, emailFieldId, "Email");
		this.emailAddressTxt = emailAddressTxt;
	}

	/**
	 * EmailPagerAddress.
	 * 
	 * @return String
	 */
	public final String getEmailPagerAddress() {
		return emailPagerAddress;
	}

	/**
	 * EmailPagerAddress.
	 * 
	 * @param emailPagerAddress
	 */
	public final void setEmailPagerAddress(String emailPagerAddress) {
		this.emailPagerAddress = emailPagerAddress;
	}

	/**
	 * MaxEmailPagerCharsNum.
	 * 
	 * @return Integer
	 */
	public final Integer getMaxEmailPagerCharsNum() {
		return maxEmailPagerCharsNum;
	}

	/**
	 * MaxEmailPagerCharsNum.
	 * 
	 * @param maxEmailPagerCharsNum
	 */
	public final void setMaxEmailPagerCharsNum(Integer maxEmailPagerCharsNum) {
		this.maxEmailPagerCharsNum = maxEmailPagerCharsNum;
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

	/**
	 * CompanyTitle.
	 * 
	 * @return String
	 */
	public final String getCompanyTitle() {
		return companyTitle;
	}

	/**
	 * CompanyTitle.
	 * 
	 * @param companyTitle
	 */
	public final void setCompanyTitle(String companyTitle) {
		this.companyTitle = companyTitle;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		requiredField(companyId, companyFieldId, "Company Name");
		this.companyId = companyId;
	}

	/*
	 * public final int compareTo(Object obj) { if (contactId != ((Contact)
	 * obj).getContactId()) { if ((firstNm != null) &&
	 * firstNm.compareTo(((Contact) obj).getFirstNm()) != 0) { return -1; } if
	 * ((lastNm != null) && lastNm.compareTo(((Contact) obj).getLastNm()) != 0)
	 * { return -1; } if ((middleNm != null) && middleNm.compareTo(((Contact)
	 * obj).getMiddleNm()) != 0) { return -1; } if ((firstNm != null) &&
	 * firstNm.compareTo(((Contact) obj).getFirstNm()) != 0) { return -1; } if
	 * ((phoneNo != null) && phoneNo.compareTo(((Contact) obj).getPhoneNo()) !=
	 * 0) { return -1; } if ((emailAddressTxt != null) &&
	 * emailAddressTxt.compareTo(((Contact) obj) .getEmailAddressTxt()) != 0) {
	 * return -1; } }
	 * 
	 * return 0; }
	 */

	public final boolean isResponsibleOfficial() {
		return isCurrentContactType(ContactTypeDef.RSOF);
	}

	public final boolean isOnSiteContact() {
		return isCurrentContactType(ContactTypeDef.ONST);
	}

	public final boolean isCurrentContactType(String contactTypeCd) {
		boolean ret = false;

		for (ContactType tempType : getContactType(contactTypeCd)) {
			if (tempType.getEndDate() == null) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	public final boolean isCurrentContactType(String contactTypeCd,
			String facilityId) {
		boolean ret = false;

		for (ContactType tempType : getContactType(contactTypeCd, facilityId)) {
			if (tempType.getEndDate() == null) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	public final boolean isContactType(String contactTypeCd) {
		return getContactType(contactTypeCd).length > 0;
	}

	public final ContactType[] getCurrentContactTypes() {
		ArrayList<ContactType> ret = new ArrayList<ContactType>(
				contactTypes.size());

		for (ContactType tempType : contactTypes) {
			if (tempType.getEndDate() == null) {
				ret.add(tempType);
			}
		}

		return ret.toArray(new ContactType[0]);
	}

	public final ContactType getCurrentContactType(String contactTypeCd) {
		ContactType ret = null;

		for (ContactType tempType : contactTypes) {
			if (contactTypeCd != null
					&& contactTypeCd.equals(tempType.getContactTypeCd())
					&& tempType.getEndDate() == null) {
				ret = tempType;
				break;
			}
		}

		return ret;
	}

	public static final ContactType getCurrentContactType(
			List<Contact> contacts, String contactTypeCd) {
		ContactType ret = null;
		for (Contact c : contacts) {
			ret = c.getCurrentContactType(contactTypeCd);
			if (ret != null)
				break;
		}
		return ret;
	}

	public static final boolean idBelongsTo(List<Contact> contacts, Integer id) {
		boolean ret = false;
		for (Contact c : contacts) {
			if (c.getContactId().equals(id)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	private ContactType[] getContactType(String contactTypeCd) {
		ArrayList<ContactType> ret = new ArrayList<ContactType>(
				contactTypes.size());

		if (contactTypeCd != null) {
			for (ContactType tempType : contactTypes) {
				if (tempType.getContactTypeCd().equals(contactTypeCd)) {
					ret.add(tempType);
				}
			}
		}

		return ret.toArray(new ContactType[0]);
	}

	private ContactType[] getContactType(String contactTypeCd, String facilityId) {
		ArrayList<ContactType> ret = new ArrayList<ContactType>(
				contactTypes.size());

		if (contactTypeCd != null) {
			for (ContactType tempType : contactTypes) {
				if (tempType.getContactTypeCd().equals(contactTypeCd)
						&& tempType.getFacilityId().equals(facilityId)) {
					ret.add(tempType);
				}
			}
		}

		return ret.toArray(new ContactType[0]);
	}
	
	public ContactType getCurrentContactType(String contactTypeCd, String facilityId) {
		ContactType ret = null;
		
		List<ContactType> currentContactTypes = Arrays.asList(getCurrentContactTypes());
		if (currentContactTypes != null) {
			for (ContactType currentContactType : currentContactTypes) {
				if (currentContactType.getFacilityId().equals(facilityId)
						&& currentContactType.getContactTypeCd().equals(
								contactTypeCd)) {
					ret = currentContactType;
				}
			}
		}
		
		return ret;
	}

	public final void addContactType(ContactType contactTypeCd) {
		if (contactTypes == null) {
			contactTypes = new ArrayList<ContactType>(1);
		}

		contactTypes.add(contactTypeCd);
	}

	public final void removeContactType(ContactType contactTypeCd) {
		if (contactTypes != null) {
			contactTypes.remove(contactTypeCd);
		}
	}

	public final List<ContactType> getContactTypes() {
		return contactTypes;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public final void populate(ResultSet rs) {
		try {
			Integer addressId = AbstractDAO.getInteger(rs, "address_id");
			if (null != addressId) {
				Address address = new Address();
				address.setAddressId(addressId);
				setAddress(address);
			}
			setContactId(AbstractDAO.getInteger(rs, "contact_id"));
			setStartDate(rs.getTimestamp("start_date"));
			setEndDate(rs.getTimestamp("end_date"));
			setEmailAddressTxt(rs.getString("email_address_txt"));
			setEmailAddressTxt2(rs.getString("secondary_email_address_txt"));
			setEmailPagerAddress(rs.getString("email_pager_address"));
			setFaxNo(rs.getString("fax_no"));
			setFirstNm(rs.getString("first_nm"));
			setLastNm(rs.getString("last_nm"));
			setMiddleNm(rs.getString("middle_nm"));
			setMaxEmailPagerCharsNum(AbstractDAO.getInteger(rs,
					"max_email_pager_chars_num"));
			setMobilePhoneNo(rs.getString("mobile_phone_no"));
			setPagerNo(rs.getString("pager_no"));
			setPagerPinNo(rs.getString("pager_pin_no"));
			setPhoneExtensionVal(rs.getString("phone_extension_val"));
			setPhoneNo(rs.getString("phone_no"));
			setSecondaryExtensionVal(rs.getString("secondary_extension_val"));
			setSecondaryPhoneNo(rs.getString("secondary_phone_no"));
			setSuffixCd(rs.getString("suffix"));
			setTitleCd(rs.getString("title_cd"));
			setCompanyTitle(rs.getString("company_title"));
			setCompanyId(rs.getInt("company_id"));

			if (ResultSetHelper.hasDataColumn(rs, "cmp_id")) {
				setCmpId(rs.getString("cmp_id"));
			}

			if (ResultSetHelper.hasDataColumn(rs, "company_name")) {
				setCompanyName(rs.getString("company_name"));
			}

			setPreferredName(rs.getString("preferred_name"));
			setCntId(rs.getString("cnt_id"));
			setActive(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("active")));
			
			setExternalUser(new ExternalUser(rs.getString("envite_username")));

			setLastModified(rs.getInt("contact_lm"));

			Address tempAddress = new Address();
			tempAddress.populate(rs);

			setAddress(tempAddress);
		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
	}


	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((address == null) ? 0 : address.hashCode());
		result = PRIME * result + ((companyTitle == null) ? 0 : companyTitle.hashCode());
		result = PRIME * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = PRIME * result + ((contactId == null) ? 0 : contactId.hashCode());
		result = PRIME * result + ((emailAddressTxt == null) ? 0 : emailAddressTxt.hashCode());
		result = PRIME * result + ((emailAddressTxt2 == null) ? 0 : emailAddressTxt2.hashCode());
		result = PRIME * result + ((emailPagerAddress == null) ? 0 : emailPagerAddress.hashCode());
		result = PRIME * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = PRIME * result + ((facilityId == null) ? 0 : facilityId.hashCode());
		result = PRIME * result + ((faxNo == null) ? 0 : faxNo.hashCode());
		result = PRIME * result + ((firstNm == null) ? 0 : firstNm.hashCode());
		result = PRIME * result + ((lastNm == null) ? 0 : lastNm.hashCode());
		result = PRIME * result + ((maxEmailPagerCharsNum == null) ? 0 : maxEmailPagerCharsNum.hashCode());
		result = PRIME * result + ((middleNm == null) ? 0 : middleNm.hashCode());
		result = PRIME * result + ((mobilePhoneNo == null) ? 0 : mobilePhoneNo.hashCode());
		result = PRIME * result + ((pagerNo == null) ? 0 : pagerNo.hashCode());
		result = PRIME * result + ((pagerPinNo == null) ? 0 : pagerPinNo.hashCode());
		result = PRIME * result + ((phoneExtensionVal == null) ? 0 : phoneExtensionVal.hashCode());
		result = PRIME * result + ((phoneNo == null) ? 0 : phoneNo.hashCode());
		result = PRIME * result + ((secondaryExtensionVal == null) ? 0 : secondaryExtensionVal.hashCode());
		result = PRIME * result + ((secondaryPhoneNo == null) ? 0 : secondaryPhoneNo.hashCode());
		result = PRIME * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = PRIME * result + ((suffixCd == null) ? 0 : suffixCd.hashCode());
		result = PRIME * result + ((titleCd == null) ? 0 : titleCd.hashCode());
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
		final Contact other = (Contact) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (companyTitle == null) {
			if (other.companyTitle != null)
				return false;
		} else if (!companyTitle.equals(other.companyTitle))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (contactId == null) {
			if (other.contactId != null)
				return false;
		} else if (!contactId.equals(other.contactId))
			return false;
		if (emailAddressTxt == null) {
			if (other.emailAddressTxt != null)
				return false;
		} else if (!emailAddressTxt.equals(other.emailAddressTxt))
			return false;
		if (emailAddressTxt2 == null) {
			if (other.emailAddressTxt2 != null)
				return false;
		} else if (!emailAddressTxt2.equals(other.emailAddressTxt2))
			return false;
		if (emailPagerAddress == null) {
			if (other.emailPagerAddress != null)
				return false;
		} else if (!emailPagerAddress.equals(other.emailPagerAddress))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (facilityId == null) {
			if (other.facilityId != null)
				return false;
		} else if (!facilityId.equals(other.facilityId))
			return false;
		if (faxNo == null) {
			if (other.faxNo != null)
				return false;
		} else if (!faxNo.equals(other.faxNo))
			return false;
		if (firstNm == null) {
			if (other.firstNm != null)
				return false;
		} else if (!firstNm.equals(other.firstNm))
			return false;
		if (lastNm == null) {
			if (other.lastNm != null)
				return false;
		} else if (!lastNm.equals(other.lastNm))
			return false;
		if (maxEmailPagerCharsNum == null) {
			if (other.maxEmailPagerCharsNum != null)
				return false;
		} else if (!maxEmailPagerCharsNum.equals(other.maxEmailPagerCharsNum))
			return false;
		if (middleNm == null) {
			if (other.middleNm != null)
				return false;
		} else if (!middleNm.equals(other.middleNm))
			return false;
		if (mobilePhoneNo == null) {
			if (other.mobilePhoneNo != null)
				return false;
		} else if (!mobilePhoneNo.equals(other.mobilePhoneNo))
			return false;
		if (pagerNo == null) {
			if (other.pagerNo != null)
				return false;
		} else if (!pagerNo.equals(other.pagerNo))
			return false;
		if (pagerPinNo == null) {
			if (other.pagerPinNo != null)
				return false;
		} else if (!pagerPinNo.equals(other.pagerPinNo))
			return false;
		if (phoneExtensionVal == null) {
			if (other.phoneExtensionVal != null)
				return false;
		} else if (!phoneExtensionVal.equals(other.phoneExtensionVal))
			return false;
		if (phoneNo == null) {
			if (other.phoneNo != null)
				return false;
		} else if (!phoneNo.equals(other.phoneNo))
			return false;
		if (secondaryExtensionVal == null) {
			if (other.secondaryExtensionVal != null)
				return false;
		} else if (!secondaryExtensionVal.equals(other.secondaryExtensionVal))
			return false;
		if (secondaryPhoneNo == null) {
			if (other.secondaryPhoneNo != null)
				return false;
		} else if (!secondaryPhoneNo.equals(other.secondaryPhoneNo))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (suffixCd == null) {
			if (other.suffixCd != null)
				return false;
		} else if (!suffixCd.equals(other.suffixCd))
			return false;
		if (titleCd == null) {
			if (other.titleCd != null)
				return false;
		} else if (!titleCd.equals(other.titleCd))
			return false;
		return true;
	}

	public boolean equalsIgnoreCase(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final Contact other = (Contact) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equalsIgnoreCase(other.address))
			return false;
		if (companyTitle == null) {
			if (other.companyTitle != null)
				return false;
		} else if (!companyTitle.equalsIgnoreCase(other.companyTitle))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (emailAddressTxt == null) {
			if (other.emailAddressTxt != null)
				return false;
		} else if (!emailAddressTxt.equalsIgnoreCase(other.emailAddressTxt))
			return false;
		if (emailAddressTxt2 == null) {
			if (other.emailAddressTxt2 != null)
				return false;
		} else if (!emailAddressTxt2.equalsIgnoreCase(other.emailAddressTxt2))
			return false;
		if (emailPagerAddress == null) {
			if (other.emailPagerAddress != null)
				return false;
		} else if (!emailPagerAddress.equalsIgnoreCase(other.emailPagerAddress))
			return false;
		if (faxNo == null) {
			if (other.faxNo != null)
				return false;
		} else if (!faxNo.equals(other.faxNo))
			return false;
		if (firstNm == null) {
			if (other.firstNm != null)
				return false;
		} else if (!firstNm.equalsIgnoreCase(other.firstNm))
			return false;
		if (lastNm == null) {
			if (other.lastNm != null)
				return false;
		} else if (!lastNm.equalsIgnoreCase(other.lastNm))
			return false;
		if (maxEmailPagerCharsNum == null) {
			if (other.maxEmailPagerCharsNum != null)
				return false;
		} else if (!maxEmailPagerCharsNum.equals(other.maxEmailPagerCharsNum))
			return false;
		if (middleNm == null) {
			if (other.middleNm != null)
				return false;
		} else if (!middleNm.equalsIgnoreCase(other.middleNm))
			return false;
		if (mobilePhoneNo == null) {
			if (other.mobilePhoneNo != null)
				return false;
		} else if (!mobilePhoneNo.equalsIgnoreCase(other.mobilePhoneNo))
			return false;
		if (pagerNo == null) {
			if (other.pagerNo != null)
				return false;
		} else if (!pagerNo.equalsIgnoreCase(other.pagerNo))
			return false;
		if (pagerPinNo == null) {
			if (other.pagerPinNo != null)
				return false;
		} else if (!pagerPinNo.equalsIgnoreCase(other.pagerPinNo))
			return false;
		if (phoneExtensionVal == null) {
			if (other.phoneExtensionVal != null)
				return false;
		} else if (!phoneExtensionVal.equals(other.phoneExtensionVal))
			return false;
		if (phoneNo == null) {
			if (other.phoneNo != null)
				return false;
		} else if (!phoneNo.equals(other.phoneNo))
			return false;
		if (secondaryExtensionVal == null) {
			if (other.secondaryExtensionVal != null)
				return false;
		} else if (!secondaryExtensionVal
				.equalsIgnoreCase(other.secondaryExtensionVal))
			return false;
		if (secondaryPhoneNo == null) {
			if (other.secondaryPhoneNo != null)
				return false;
		} else if (!secondaryPhoneNo.equalsIgnoreCase(other.secondaryPhoneNo))
			return false;
		if (suffixCd == null) {
			if (other.suffixCd != null)
				return false;
		} else if (!suffixCd.equalsIgnoreCase(other.suffixCd))
			return false;
		if (titleCd == null) {
			if (other.titleCd != null)
				return false;
		} else if (!titleCd.equals(other.titleCd))
			return false;
		return true;
	}

	/**
	 * Using in permitDetail.java
	 * 
	 * @return FirstNm LastNm, Address.toShortString()
	 */
	public final String toShortString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getFirstNm());
		sb.append(" ");
		sb.append(getLastNm());
		Address a = getAddress();
		if (a != null) {
			sb.append(", ");
			a.toShortString();
		}

		return sb.toString();
	}

	@Override
	public ValidationMessage[] validate() {
		StringBuffer errMsg = new StringBuffer();
		clearValidationMessages();

		requiredFields();

		if (phoneNo != null && !PhoneNumberHandler.validNumber(phoneNo, errMsg)) {
			this.validationMessages.put("phoneNo:1", new ValidationMessage(
					"phoneNo", "Primary phone number " + errMsg.toString(),
					ValidationMessage.Severity.ERROR, "contact:" + contactId));
		}
		if (secondaryPhoneNo != null
				&& !PhoneNumberHandler.validNumber(secondaryPhoneNo, errMsg)) {
			this.validationMessages.put("secondaryPhoneNo:1",
					new ValidationMessage("secondaryPhoneNo",
							"Secondary phone number " + errMsg.toString(),
							ValidationMessage.Severity.ERROR, "contact:"
									+ contactId));
		}
		if (mobilePhoneNo != null
				&& !PhoneNumberHandler.validNumber(mobilePhoneNo, errMsg)) {
			this.validationMessages.put("mobilePhoneNo:1",
					new ValidationMessage("mobilePhoneNo", "Mobile phone number " + errMsg.toString(),
							ValidationMessage.Severity.ERROR, "contact:"
									+ contactId));
		}
		if (faxNo != null && !PhoneNumberHandler.validNumber(faxNo, errMsg)) {
			this.validationMessages.put("faxNo:1", new ValidationMessage(
					"faxNo", "Fax number " + errMsg.toString(),
					ValidationMessage.Severity.ERROR, "contact:" + contactId));
		}
		if (pagerNo != null && !PhoneNumberHandler.validNumber(pagerNo, errMsg)) {
			this.validationMessages.put("pagerNo:1", new ValidationMessage(
					"pagerNo", "Pager number " + errMsg.toString(),
					ValidationMessage.Severity.ERROR, "contact:" + contactId));
		}
		if (emailAddressTxt != null && !emailAddressTxt.equals("")
				&& !ValidateEmailAddress.isValidEmail(emailAddressTxt)) {
			this.validationMessages.put("emailAddressTxt:1",
					new ValidationMessage("emailAddressTxt", "Invalid email.",
							ValidationMessage.Severity.ERROR, "contact:"
									+ contactId));
		}
		
		if (emailAddressTxt2 != null && !emailAddressTxt2.equals("")
				&& !ValidateEmailAddress.isValidEmail(emailAddressTxt2)) {
			this.validationMessages.put("emailAddressTxt:2",
					new ValidationMessage("emailAddressTxt2", "Invalid email.",
							ValidationMessage.Severity.ERROR, "contact:"
									+ contactId));
		}

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

		if (active == false && !validMakeInactive()) {
			this.validationMessages
					.put("statusFlag:1",
							new ValidationMessage(
									"statusFlag",
									"To mark contact as inactive, all contact types associated with contact must be inactive.",
									ValidationMessage.Severity.ERROR,
									"contact:" + contactId));
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	private boolean validMakeInactive() {
		boolean isValid = true;

		for (ContactType cType : contactTypes) {
			if (cType.getEndDate() == null) {
				isValid = false;
				break;
			}
		}

		return isValid;
	}

	public final void setContactTypes(List<ContactType> contactTypes) {
		this.contactTypes = contactTypes;
	}

	public final String getEmailAddressTxt2() {
		return emailAddressTxt2;
	}

	public final void setEmailAddressTxt2(String emailAddressTxt2) {
		this.emailAddressTxt2 = emailAddressTxt2;
	}

}

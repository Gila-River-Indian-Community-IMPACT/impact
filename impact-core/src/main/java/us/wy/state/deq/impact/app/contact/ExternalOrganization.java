package us.wy.state.deq.impact.app.contact;

import java.io.Serializable;

import javax.xml.datatype.XMLGregorianCalendar;

import org.gricdeq.scs.schema.sharedcromerr.portal._1.PartnerOrganizationMetadataType;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.PartnerOrganizationStatusType;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.UserOrganizationType;

import us.wy.state.deq.impact.database.dbObjects.company.Company;

@SuppressWarnings("serial")
public class ExternalOrganization implements Serializable {
	private Company company;
	
	private UserOrganizationType userOrganizationType;

	public ExternalOrganization() {
		super();
	}
	public ExternalOrganization(UserOrganizationType userOrganizationType) {
		super();
		this.userOrganizationType = userOrganizationType;
	}
	public UserOrganizationType getUserOrganizationType() {
		return userOrganizationType;
	}
	public void setUserOrganizationType(UserOrganizationType userOrganizationType) {
		this.userOrganizationType = userOrganizationType;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Long getUserOrganizationId() {
		return userOrganizationType.getUserOrganizationId();
	}
	public void setUserOrganizationId(Long value) {
		userOrganizationType.setUserOrganizationId(value);
	}
	public String getEmail() {
		return userOrganizationType.getEmail();
	}
	public void setEmail(String value) {
		userOrganizationType.setEmail(value);
	}
	public String getPhone() {
		return userOrganizationType.getPhone();
	}
	public Long getOrganizationId() {
		return userOrganizationType.getOrganizationId();
	}
	public void setPhone(String value) {
		userOrganizationType.setPhone(value);
	}
	public void setOrganizationId(Long value) {
		userOrganizationType.setOrganizationId(value);
	}
	public String getPhoneExtension() {
		return userOrganizationType.getPhoneExtension();
	}
	public String getOrganizationName() {
		return userOrganizationType.getOrganizationName();
	}
	public void setPhoneExtension(String value) {
		userOrganizationType.setPhoneExtension(value);
	}
	public void setOrganizationName(String value) {
		userOrganizationType.setOrganizationName(value);
	}
	public String getFax() {
		return userOrganizationType.getFax();
	}
	public String getMailingAddress1() {
		return userOrganizationType.getMailingAddress1();
	}
	public void setFax(String value) {
		userOrganizationType.setFax(value);
	}
	public void setMailingAddress1(String value) {
		userOrganizationType.setMailingAddress1(value);
	}
	public String getEsaStatus() {
		return userOrganizationType.getEsaStatus();
	}
	public String getMailingAddress2() {
		return userOrganizationType.getMailingAddress2();
	}
	public void setEsaStatus(String value) {
		userOrganizationType.setEsaStatus(value);
	}
	public void setMailingAddress2(String value) {
		userOrganizationType.setMailingAddress2(value);
	}
	public XMLGregorianCalendar getEsaApprovalDate() {
		return userOrganizationType.getEsaApprovalDate();
	}
	public String getMailingAddress3() {
		return userOrganizationType.getMailingAddress3();
	}
	public void setEsaApprovalDate(XMLGregorianCalendar value) {
		userOrganizationType.setEsaApprovalDate(value);
	}
	public void setMailingAddress3(String value) {
		userOrganizationType.setMailingAddress3(value);
	}
	public String getEsaApprovalMethod() {
		return userOrganizationType.getEsaApprovalMethod();
	}
	public String getMailingAddress4() {
		return userOrganizationType.getMailingAddress4();
	}
	public void setEsaApprovalMethod(String value) {
		userOrganizationType.setEsaApprovalMethod(value);
	}
	public void setMailingAddress4(String value) {
		userOrganizationType.setMailingAddress4(value);
	}
	public String getCity() {
		return userOrganizationType.getCity();
	}
	public void setCity(String value) {
		userOrganizationType.setCity(value);
	}
	public String getState() {
		return userOrganizationType.getState();
	}
	public void setState(String value) {
		userOrganizationType.setState(value);
	}
	public String getCountry() {
		return userOrganizationType.getCountry();
	}
	public void setCountry(String value) {
		userOrganizationType.setCountry(value);
	}
	public String getZip() {
		return userOrganizationType.getZip();
	}
	public void setZip(String value) {
		userOrganizationType.setZip(value);
	}
	public PartnerOrganizationStatusType getStatus() {
		return userOrganizationType.getStatus();
	}
	public void setStatus(PartnerOrganizationStatusType value) {
		userOrganizationType.setStatus(value);
	}
	public PartnerOrganizationMetadataType getMetadata() {
		return userOrganizationType.getMetadata();
	}
	public void setMetadata(PartnerOrganizationMetadataType value) {
		userOrganizationType.setMetadata(value);
	}
	public String getPartnerId() {
		return userOrganizationType.getPartnerId();
	}
	public void setPartnerId(String value) {
		userOrganizationType.setPartnerId(value);
	}
	
	
}
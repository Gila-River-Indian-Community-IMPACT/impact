package us.oh.state.epa.stars2.webcommon;

import java.sql.Timestamp;
import java.util.Calendar;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;

@SuppressWarnings("serial")
public class ContactUtil implements java.io.Serializable {
    private Contact contact;
    private ContactType contactType;
    private String message;

    public static String datePrtFormat(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        return (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) +  "/" + cal.get(Calendar.YEAR);
    }
    
    public ContactUtil() {
        contact = new Contact();
        contactType = new ContactType();
    }

    public ContactUtil(Contact contact, ContactType contactType) {
        this.contact = contact;
        this.contactType = contactType;
    }

    public ContactUtil(Contact contact) {
        this.contact = contact;
        contactType = new ContactType();
    }

    public final Contact getContact() {
        return contact;
    }

    public final void setContact(Contact contact) {
        this.contact = contact;
    }

    public final ContactType getContactType() {
        return contactType;
    }

    public final void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public final String getMessage() {
        return message;
    }

    public final void setMessage(String message) {
        this.message = message;
    }
    
    public Timestamp getStartDate() {
    	return null == getContactType()? null : getContactType().getStartDate();
    }
    
    public Timestamp getEndDate() {
    	return null == getContactType()? null : getContactType().getEndDate();
    }
}

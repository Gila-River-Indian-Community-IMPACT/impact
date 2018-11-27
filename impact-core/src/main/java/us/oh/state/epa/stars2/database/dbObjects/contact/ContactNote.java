package us.oh.state.epa.stars2.database.dbObjects.contact;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

@SuppressWarnings("serial")
public class ContactNote extends Note {

	Integer contactId;

	public ContactNote() {
        super();
    }

    public ContactNote(ContactNote old) {
        super(old);

        if (old != null) {
            setContactId(old.getContactId());
        }
    }
    
	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
	
	
	
}

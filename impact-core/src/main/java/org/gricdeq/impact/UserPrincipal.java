package org.gricdeq.impact;

import java.security.Principal;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;

public class UserPrincipal implements Principal {

	private Contact contact;
	private ContactRole initialRole;
	
	public UserPrincipal(Contact contact) {
		super();
		this.contact = contact;
	}

	

	public Contact getContact() {
		return contact;
	}



	public void setContact(Contact contact) {
		this.contact = contact;
	}



	public ContactRole getInitialRole() {
		return initialRole;
	}



	public void setInitialRole(ContactRole initialRole) {
		this.initialRole = initialRole;
	}



	@Override
	public String getName() {
		return this.contact.getExternalUser().getUserName();
	}

	
}

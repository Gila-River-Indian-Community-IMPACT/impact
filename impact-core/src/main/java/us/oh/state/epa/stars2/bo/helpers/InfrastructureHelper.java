package us.oh.state.epa.stars2.bo.helpers;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.BaseBO;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.InvoiceDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceDetail;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.EventLogTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.wy.state.deq.impact.database.dao.ContactDAO;

@Transactional(rollbackFor=Exception.class)
@Service
public class InfrastructureHelper extends BaseBO {
	private final static DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy hh:mm:ss");

	public Contact createContact(Contact contact, Transaction trans)
			throws DAOException {

		if (contact == null) {
			throw new DAOException("Contact is null.");
		}

		Contact ret = null;
		InfrastructureDAO infrastructureDAO = infrastructureDAO(trans);
		ContactDAO contactDAO = contactDAO(trans);

		Address contactAddress = contact.getAddress();

		if ((contactAddress != null) && (contactAddress.getAddressId() == null)) {

			contactAddress = infrastructureDAO.createAddress(contactAddress);
			contact.setAddressId(contactAddress.getAddressId());
		}
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			ret = contactDAO.createContact(contact);
		} else {
			ret = contactDAO.createStagingContact(contact);
		}

		if (contact.getContactTypes().size() > 0) {
			for (ContactType contactType : contact.getContactTypes()) {
				if (contactType.getEndDate() != null) {
					contactDAO.addContactType(ret.getContactId(), contactType);
				} else {
					contactDAO.addContactType(ret.getContactId(),
							contactType.getContactTypeCd(),
							contactType.getStartDate(),
							contactType.getFacilityId());
				}
			}
		}

		return ret;
	}

	public Contact createContactWithOutAddContactType(Contact contact,
			Transaction trans) throws DAOException {

		if (contact == null) {
			throw new DAOException("Contact is null.");
		}

		Contact ret = null;
		InfrastructureDAO infrastructureDAO = infrastructureDAO(trans);
		ContactDAO contactDAO = contactDAO(trans);

		Address contactAddress = contact.getAddress();

		if ((contactAddress != null) && (contactAddress.getAddressId() == null)) {

			contactAddress = infrastructureDAO.createAddress(contactAddress);
			contact.setAddressId(contactAddress.getAddressId());
		}
		ret = contactDAO.createStagingContact(contact);
		return ret;
	}

	public Contact modifyContactData(Contact contact, Transaction trans)
			throws DAOException {
		logger.trace("DLTRACE --> modifyContactData");
		Contact ret = null;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ContactDAO contactDAO = contactDAO(trans);

		Address contactAddress = contact.getAddress();
		infraDAO.modifyAddress(contactAddress);

		contactDAO.modifyContact(contact);
		ret = contact;

		return ret;
	}

	public void modifyContactType(ContactType oldContactType,
			ContactType contactType, Integer fpId, int userId, Transaction trans)
			throws DAOException {
		// TODO Clean up Infrastructure reference to contact-related DAO's
		ContactDAO contactDAO = contactDAO(trans);
		try {
			contactDAO.deleteContactType(oldContactType);
			contactDAO.addContactType(oldContactType.getContactId(),
					contactType);
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				/*if (contactType.getContactTypeCd().equals(ContactTypeDef.OWNR)) {
					Contact contact = contactDAO.retrieveContact(contactType
							.getContactId());

					List<EventLog> eventLogs = new ArrayList<EventLog>();
					EventLog eventLog = new EventLog();

					if (!oldContactType.getStartDate().equals(
							contactType.getStartDate())) {
						eventLog.setFpId(fpId);
						eventLog.setEventTypeDefCd(EventLogTypeDef.FAC_CHG);
						eventLog.setDate(new Timestamp(System
								.currentTimeMillis()));
						eventLog.setUserId(userId);
						eventLog.setNote("["
								+ contact.getName()
								+ "] acquired facility on: "
								+ dateFormat.format(contactType.getStartDate())
										.toString());
						eventLogs.add(eventLog);
					}

					if ((oldContactType.getEndDate() == null && contactType
							.getEndDate() != null)
							|| (oldContactType.getEndDate() != null
									&& contactType.getEndDate() != null && !oldContactType
									.getEndDate().equals(
											contactType.getEndDate()))) {
						eventLog.setFpId(fpId);
						eventLog.setEventTypeDefCd(EventLogTypeDef.FAC_CHG);
						eventLog.setDate(new Timestamp(System
								.currentTimeMillis()));
						eventLog.setUserId(userId);
						eventLog.setNote("["
								+ contact.getName()
								+ "] was no longer the owner of this facility as of: "
								+ dateFormat.format(contactType.getEndDate())
										.toString());
						eventLogs.add(eventLog);
					}
					FacilityHelper facHelper = new FacilityHelper();
					for (EventLog el : eventLogs.toArray(new EventLog[0])) {
						facHelper.createEventLog(el);
					}
				}*/
			}
			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}
	}

	public void deleteContactType(ContactType contactType, Integer fpId,
			int userId, Transaction trans) throws DAOException {
		// TODO Clean up Infrastructure reference to contact-related DAO's
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		ContactDAO contactDAO = contactDAO(trans);
		try {
			infraDAO.deleteContactType(contactType);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				/*if (contactType.getContactTypeCd().equals(ContactTypeDef.OWNR)
						&& contactType.getEndDate() == null) {
					Contact contact = contactDAO.retrieveContact(contactType
							.getContactId());

					EventLog eventLog = new EventLog();

					eventLog.setFpId(fpId);
					eventLog.setEventTypeDefCd(EventLogTypeDef.FAC_CHG);
					eventLog.setDate(new Timestamp(System.currentTimeMillis()));
					eventLog.setUserId(userId);
					eventLog.setNote("["
							+ contact.getName()
							+ "] was no longer the owner of this facility as of: "
							+ dateFormat.format(new Timestamp(System
									.currentTimeMillis())));

					FacilityHelper facHelper = new FacilityHelper();
					facHelper.createEventLog(eventLog);
				}*/
			}
			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}
	}

	public void deleteContact(Contact contact, Transaction trans)
			throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		infraDAO.removeContactTypes(contact.getContactId());
		infraDAO.deleteContact(contact);
	}

	public void deleteStagingContact(Contact contact, Transaction trans)
			throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		infraDAO.removeStagingContactTypes(contact.getContactId(),
				contact.getFacilityId());
		infraDAO.deleteStagingContact(contact);
	}

	public Invoice createInvoice(Invoice invoice, Transaction trans)
			throws DAOException {
		Invoice newInvoice = null;
		Integer invoiceId = null;
		Contact contact = null;
		InvoiceDAO invoiceDAO = invoiceDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		if (invoice.getContact() != null) {
			contact = invoice.getContact();
		} else {
			contact = retrieveBillingContact(invoice);
		}

		if (contact != null) {
			invoice.setContact(new Contact(contact));// Make a copy the contact,
														// which will
														// not have ids set

			/*
			 * Migrated invoices will already have their Revenue Id set. If this
			 * is Revised report's invoice then maintain same Revenue Id from
			 * its previous invoice. TODO DENNIS This is too early to set the
			 * Revenue Id because the previous report may not have been approved
			 * and invoiced yet.
			 */
			// if (invoice.getRevenueId() == null) {
			// if (invoice.getEmissionsRptId() != null) {
			// Invoice pInv;
			// try {
			// InfrastructureService infraBO =
			// ServiceFactory.getInstance().getInfrastructureService();
			// if ((pInv =
			// infraBO.retrievePreviousInvoice(invoice.getEmissionsRptId())) !=
			// null) {
			// if (pInv.getRevenueId() != null) {
			// invoice.setRevenueId(pInv.getRevenueId());
			// }
			// }
			// } catch (RemoteException re) {
			// logger.error(re.getMessage(), re);
			// throw new DAOException(re.getMessage());
			// } catch (ServiceFactoryException sfe) {
			// logger.error(sfe.getMessage(), sfe);
			// throw new DAOException(sfe.getMessage());
			// }
			// }
			// }

			if (invoice.getContact().getContactId() == null) {
				Address contactAddress = invoice.getContact().getAddress();
				contactAddress.setAddressId(null);
				if ((contactAddress != null)) {
					contactAddress = infraDAO.createAddress(contactAddress);

					invoice.getContact().setAddressId(
							contactAddress.getAddressId());
					invoice.getContact().setLastModified(1);
				}
				// newInv.getContact().setContactId(null);
				invoice.setContact(infraDAO.createContact(invoice.getContact()));
			}

			newInvoice = invoiceDAO.createInvoice(invoice);
			invoiceId = newInvoice.getInvoiceId();

			for (InvoiceDetail i : invoice.getInvoiceDetails()) {
				i.setInvoiceId(invoiceId);
				invoiceDAO.createInvoiceDetail(i);
			}
		} else {
			String s = "There is no active billing contact";
			throw new DAOException(s, s);
		}

		return newInvoice;
	}

	private Contact retrieveBillingContact(Invoice invoice) throws DAOException {
		Contact billingContact = null;

		try {
			FacilityService facilityBO = ServiceFactory.getInstance()
					.getFacilityService();

			Facility facility = facilityBO.retrieveFacility(invoice
					.getFacilityId());

			// Corresponding active contact of type specified. Function on
			// class Facility
			ContactUtil activeConU = facility.getActiveContact(
					ContactTypeDef.BILL, null);

			if (activeConU != null && activeConU.getContact() != null) {
				billingContact = activeConU.getContact();
			}

		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			throw new DAOException(re.getMessage());
		} catch (ServiceFactoryException sfe) {
			logger.error(sfe.getMessage(), sfe);
			throw new DAOException(sfe.getMessage());
		}
		return billingContact;
	}

	public Contact[] retrieveContacts(Contact contact) {
		Contact[] contacts = null;
		try {
			ContactDAO contactDAO = contactDAO();
			if (contact.getContactId() != null) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("contactId", contact.getContactId() == null ? ""
						: contact.getContactId().toString());
				contacts = contactDAO.searchContacts(params, false);
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contacts;

	}

	public Contact[] retrieveContacts(Contact contact, Transaction trans) {
		logger.trace("DLTRACE --> retrieveContacts");
		Contact[] contacts = null;
		try {
			ContactDAO contactDAO = contactDAO(trans);
			if (contact.getContactId() != null) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("contactId", contact.getContactId() == null ? ""
						: contact.getContactId().toString());
				contacts = contactDAO.searchContacts(params, false);
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contacts;

	}

	public Contact retrieveContact(Contact contact, Transaction trans) {
		Contact retContact = null;
		try {
			ContactDAO contactDAO = contactDAO(trans);

			retContact = contactDAO.retrieveContact(contact.getContactId());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retContact;
	}

	public Contact retrieveStagingContact(Contact contact, Transaction trans) {
		Contact retContact = null;
		try {
			ContactDAO contactDAO = contactDAO(trans);

			retContact = contactDAO.retrieveStagingContact(
					contact.getContactId(), contact.getFacilityId());
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retContact;
	}
}

package us.oh.state.epa.stars2.database.dbObjects.application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;

@SuppressWarnings("serial")
public class Application extends BaseDB {
	
	private Integer applicationID;
	private String previousApplicationNumber;
	private String applicationTypeCD = "";
	private String applicationNumber;
	private boolean applicationAmended;
	private boolean applicationCorrected;
	private String applicationCorrectedReason;
	private HashMap<Integer, Permit> referencedPermits;
	private Integer contactId;
	private Contact contact;
	private boolean validated;
	private String applicationDesc;
	private boolean legacy;
	private transient boolean containsTS; // only set when pdf generation done.

	private transient Timestamp receivedDate;
	// declare dummy version of receivedDate for XML serialization
	private long receivedDateLong;
	private transient Timestamp submittedDate;
	// declare dummy version of submittedDate for XML serialization
	private long submittedDateLong;

	private transient String submitLabel;
	private transient String submitValue;

	private transient Facility facility; // DENNIS trnsient because no need to
											// send/receive thru XML ?????
	protected HashMap<Integer, ApplicationDocumentRef> documents;
	private HashMap<Integer, ApplicationEU> eus;
	private HashMap<Integer, ApplicationNote> applicationNotes;
	private String generalPermit;
	
	private boolean knownIncomplete;
	
	private List<String> inspectionsReferencedIn;
	
	/**
	 * Copy Constructor
	 * 
	 * @param application
	 *            a <code>Application</code> object
	 */
	public Application(Application application) {
		super(application);

		if (application != null) {
			this.applicationID = application.applicationID;
			this.previousApplicationNumber = application.previousApplicationNumber;
			this.applicationTypeCD = application.applicationTypeCD;
			this.applicationAmended = application.applicationAmended;
			this.applicationCorrected = application.applicationCorrected;
			this.applicationCorrectedReason = application.applicationCorrectedReason;
			this.referencedPermits = application.referencedPermits;
			this.contactId = application.contactId;
			this.contact = application.contact;
			this.validated = application.validated;
			this.receivedDate = application.receivedDate;
			this.submittedDate = application.submittedDate;
			this.facility = application.facility;
			this.documents = application.documents;
			this.eus = application.eus;
			this.applicationNotes = application.applicationNotes;
			this.applicationDesc = application.applicationDesc;
			this.legacy = application.legacy;
			this.knownIncomplete = application.knownIncomplete;
		}
	}

	public Application() {
	}

	public void populate(ResultSet rs) {
		try {
			setApplicationID(AbstractDAO.getInteger(rs, "application_id"));
			setApplicationTypeCD(rs.getString("application_type_cd"));
			setApplicationAmended(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("app_amended_flag")));
			setApplicationCorrected(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("app_corrected_flag")));
			setApplicationCorrectedReason(rs.getString("app_corrected_reason"));
			setPreviousApplicationNumber(rs
					.getString("previous_application_nbr"));
			setApplicationNumber(rs.getString("application_nbr"));
			setValidated(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("validated_flag")));
			setReceivedDate(rs.getTimestamp("received_date"));
			setSubmittedDate(rs.getTimestamp("submitted_date"));
			setLastModified(AbstractDAO.getInteger(rs, "pa_lm"));
			setApplicationDesc(rs.getString("application_desc"));
			setContactId(AbstractDAO.getInteger(rs, "contact_id"));
			setLegacy(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("legacy_flag")));

			if (!"RPS".equals(getApplicationTypeCD())
					&& !"ITR".equals(getApplicationTypeCD())
					&& !"SPA".equals(getApplicationTypeCD())) {
				facility = new Facility();

				facility.setFpId(AbstractDAO.getInteger(rs, "fp_id"));
				facility.setFacilityId(rs.getString("facility_id"));
				facility.setName(rs.getString("facility_nm"));
				facility.setDoLaaCd(rs.getString("do_laa_cd"));
				facility.setLastModified(AbstractDAO.getInteger(rs, "ff_lm"));
				facility.setFacilityTypeCd(rs.getString("facility_type_cd"));
				facility.setPermitClassCd(rs.getString("permit_classification_cd"));
			}

		} catch (SQLException sqle) {
			logger.error(sqle.getMessage());
		}
	}

	public String getGeneralPermit() {
		return generalPermit;
	}

	public final void setGeneralPermit(String generalPermit) {
		this.generalPermit = generalPermit;
	}

	public String getFacilityId() {
		return facility.getFacilityId();
	}

	public final String getFacilityName() {
		return facility.getName();
	}

	public final String getDoLaaCd() {
		return facility.getDoLaaCd();
	}

	public final Integer getApplicationID() {
		return applicationID;
	}

	public final void setApplicationID(Integer id) {
		this.applicationID = id;
	}

	public final Facility getFacility() {
		return facility;
	}

	public final void setFacility(Facility facility) {
		this.facility = facility;
	}

	public final boolean getValidated() {
		return validated;
	}

	public final void setValidated(boolean validated) {
		this.validated = validated;
	}

	public final String getApplicationNumber() {
		return applicationNumber;
	}

	public final void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public final Contact getContact() {
		return contact;
	}

	public final void setContact(Contact contact) {
		this.contact = contact;
	}

	public final List<ApplicationDocumentRef> getDocuments() {
		Comparator<ApplicationDocumentRef> comparator = generateAppDocRefComparator();

		if (documents == null) {
			documents = new HashMap<Integer, ApplicationDocumentRef>();
		}

		List<ApplicationDocumentRef> docList = new ArrayList<ApplicationDocumentRef>(
				documents.values());
		Collections.sort(docList, comparator);

		return docList;
	}

	public final void setDocuments(
			List<ApplicationDocumentRef> facilityDocuments) {
		documents = new HashMap<Integer, ApplicationDocumentRef>();

		for (ApplicationDocumentRef tempDoc : facilityDocuments) {
			documents.put(tempDoc.getApplicationDocId(), tempDoc);
		}
	}

	public final void addDocument(ApplicationDocumentRef newDoc) {
		if (documents == null) {
			documents = new HashMap<Integer, ApplicationDocumentRef>();
		}

		documents.put(newDoc.getApplicationDocId(), newDoc);
	}

	public final void removeDocument(ApplicationDocumentRef doc) {
		if (documents != null) {
			documents.remove(doc.getApplicationDocId());
		}
	}

	public final String getRequestType() {
		String requestType = "Unknown";
		if (applicationTypeCD != null) {
			requestType = ApplicationTypeDef.getData().getItems()
					.getItemDesc(applicationTypeCD);
		}
		return requestType;
	}

	public final List<ApplicationEU> getEus() {
		if (eus == null) {
			eus = new HashMap<Integer, ApplicationEU>();
		}
		return new ArrayList<ApplicationEU>(eus.values());
	}

	public final List<ApplicationEU> getIncludedEus() {
		TreeMap<String, ApplicationEU> ieus = new TreeMap<String, ApplicationEU>();
		if (eus != null) {
			for (ApplicationEU teu : eus.values()) {
				if (!teu.isExcluded()) {
					ieus.put(teu.getFpEU().getEpaEmuId(), teu);
				}
			}
		}
		return new ArrayList<ApplicationEU>(ieus.values());
	}

	public final void setEus(List<ApplicationEU> eus) {
		this.eus = new HashMap<Integer, ApplicationEU>();

		for (ApplicationEU tempEu : eus) {
			this.eus.put(tempEu.getApplicationEuId(), tempEu);
		}
	}

	public final void addEu(ApplicationEU newEu) {
		if (eus == null) {
			eus = new HashMap<Integer, ApplicationEU>();
		}

		eus.put(newEu.getApplicationEuId(), newEu);
	}

	public final void removeEu(ApplicationEU eu) {
		if (eus != null) {
			eus.remove(eu.getApplicationEuId());
		}
	}

	public final void clearEus() {
		if (eus != null) {
			eus.clear();
		}
	}

	public final List<Permit> getReferencedPermits() {
		if (referencedPermits == null) {
			referencedPermits = new HashMap<Integer, Permit>();
		}
		return new ArrayList<Permit>(referencedPermits.values());
	}

	public final void setReferencedPermits(List<Permit> referencedPermits) {
		this.referencedPermits = new HashMap<Integer, Permit>();

		for (Permit tempPermit : referencedPermits) {
			this.referencedPermits.put(tempPermit.getPermitID(), tempPermit);
		}
	}

	public final void addReferencedPermit(Permit newPermit) {
		if (referencedPermits == null) {
			referencedPermits = new HashMap<Integer, Permit>();
		}

		referencedPermits.put(newPermit.getPermitID(), newPermit);
	}

	public final void removeReferencedPermit(Permit permit) {
		if (referencedPermits != null) {
			referencedPermits.remove(permit.getPermitID());
		}
	}

	public final String getApplicationTypeCD() {
		return applicationTypeCD;
	}

	public void setApplicationTypeCD(String applicationTypeCD) {
		this.applicationTypeCD = applicationTypeCD;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + (applicationAmended ? 1231 : 1237);
		result = PRIME * result + (applicationCorrected ? 1231 : 1237);
		result = PRIME
				* result
				+ ((applicationCorrectedReason == null) ? 0
						: applicationCorrectedReason.hashCode());
		result = PRIME * result
				+ ((applicationID == null) ? 0 : applicationID.hashCode());
		result = PRIME
				* result
				+ ((applicationNumber == null) ? 0 : applicationNumber
						.hashCode());
		result = PRIME
				* result
				+ ((applicationTypeCD == null) ? 0 : applicationTypeCD
						.hashCode());
		result = PRIME * result + ((contact == null) ? 0 : contact.hashCode());
		result = PRIME * result
				+ ((receivedDate == null) ? 0 : receivedDate.hashCode());
		result = PRIME * result
				+ ((submittedDate == null) ? 0 : receivedDate.hashCode());
		result = PRIME * result + (validated ? 1231 : 1237);
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
		final Application other = (Application) obj;
		if (applicationID == null) {
			if (other.applicationID != null)
				return false;
		} else if (!applicationID.equals(other.applicationID))
			return false;
		return true;
	}

	public final void addApplicationNote(ApplicationNote applicationNote) {
		if (applicationNote == null) {
			return;
		}
		if (applicationNotes == null) {
			applicationNotes = new HashMap<Integer, ApplicationNote>();
		}
		applicationNotes.put(applicationNote.getNoteId(), applicationNote);
	}

	@SuppressWarnings("unchecked")
	public final List<ApplicationNote> getApplicationNotes() {
		if (applicationNotes == null) {
			applicationNotes = new HashMap<Integer, ApplicationNote>();
		}
		
		List<ApplicationNote> sortedNotes = new ArrayList<ApplicationNote>(applicationNotes.values());
		
		Collections.sort(sortedNotes, new Comparator() {
			public int compare(Object o1, Object o2) {
				return (((ApplicationNote) o1).getNoteId()
						.compareTo(((ApplicationNote) o2).getNoteId()));
			}

		});
		return sortedNotes;
	}

	public final void setApplicationNotes(List<ApplicationNote> notes) {
		applicationNotes = new HashMap<Integer, ApplicationNote>();

		for (ApplicationNote note : notes) {
			applicationNotes.put(note.getNoteId(), note);
		}
	}

	public final void removeApplicationNote(ApplicationNote note) {
		if (applicationNotes != null) {
			applicationNotes.remove(note);
		}
	}

	public List<String> getApplicationPurposeCDs() {
		return new ArrayList<String>();
	}

	/**
	 * Comma-separated list of application purpose description values associated
	 * with this application.
	 * 
	 * @return
	 */
	public String getApplicationPurposeDesc() {
		return "";
	}

	public final boolean isApplicationAmended() {
		return applicationAmended;
	}

	public final void setApplicationAmended(boolean appAmended) {
		this.applicationAmended = appAmended;
	}

	public final boolean isApplicationCorrected() {
		return applicationCorrected;
	}

	public final void setApplicationCorrected(boolean applicationCorrected) {
		this.applicationCorrected = applicationCorrected;
	}

	public final String getApplicationCorrectedReason() {
		return applicationCorrectedReason;
	}

	public final void setApplicationCorrectedReason(
			String applicationCorrectedReason) {
		this.applicationCorrectedReason = applicationCorrectedReason;
	}

	public final Timestamp getReceivedDate() {
		return receivedDate;
	}

	public final void setReceivedDate(Timestamp receivedDate) {
		this.receivedDate = receivedDate;
		if (this.receivedDate != null) {
			this.receivedDateLong = this.receivedDate.getTime();
		} else {
			this.receivedDateLong = 0;
		}
	}

	public final long getReceivedDateLong() {
		long date = 0;
		if (receivedDate != null) {
			date = receivedDate.getTime();
		}
		return date;
	}

	public final void setReceivedDateLong(long receivedDateLong) {
		receivedDate = null;
		if (receivedDateLong > 0) {
			receivedDate = new Timestamp(receivedDateLong);
		}
	}

	public final Timestamp getSubmittedDate() {
		return submittedDate;
	}

	public final void setSubmittedDate(Timestamp submittedDate) {
		this.submittedDate = submittedDate;
		if (this.submittedDate != null) {
			this.submittedDateLong = this.submittedDate.getTime();
		} else {
			this.submittedDateLong = 0;
		}
	}

	public final long getSubmittedDateLong() {
		long date = 0;
		if (submittedDate != null) {
			date = submittedDate.getTime();
		}
		return date;
	}

	public final void setSubmittedDateLong(long submittedDateLong) {
		submittedDate = null;
		if (submittedDateLong > 0) {
			submittedDate = new Timestamp(submittedDateLong);
		}
	}

	public final String getApplicationDesc() {
		return applicationDesc;
	}

	public final void setApplicationDesc(String applicationDesc) {
		this.applicationDesc = applicationDesc;
	}

	public final String getPreviousApplicationNumber() {
		return previousApplicationNumber;
	}

	public final void setPreviousApplicationNumber(
			String previousApplicationNumber) {
		this.previousApplicationNumber = previousApplicationNumber;
	}

	public final Integer getContactId() {
		return contactId;
	}

	public final void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public final boolean isLegacy() {
		return legacy;
	}

	public final void setLegacy(boolean migrated) {
		this.legacy = migrated;
	}
	
	public final boolean isKnownIncomplete() {
		return knownIncomplete;
	}

	public final void setKnownIncomplete(boolean knownIncomplete) {
		this.knownIncomplete = knownIncomplete;
	}

	public Integer getPermitId() {
		return null;
	}

	public boolean hasAttachments() {
		boolean hasAttachments = false;
		if (getDocuments().size() > 0) {
			hasAttachments = true;
		} else {
			for (ApplicationEU appEU : getIncludedEus()) {
				if (appEU.getEuDocuments().size() > 0) {
					hasAttachments = true;
					break;
				}
			}
		}
		return hasAttachments;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		// manually set transient date values since this does not appear to
		// work properly with persistence
		setReceivedDateLong(this.receivedDateLong);
		setSubmittedDateLong(this.submittedDateLong);
	}

	public String getSubmitLabel() {
		return submitLabel;
	}

	public void setSubmitLabel(String submitLabel) {
		this.submitLabel = submitLabel;
	}

	public String getSubmitValue() {
		return submitValue;
	}

	public void setSubmitValue(String submitValue) {
		this.submitValue = submitValue;
	}

	public boolean isContainsTS() {
		return containsTS;
	}

	public void setContainsTS(boolean containsTS) {
		this.containsTS = containsTS;
	}

	public final ValidationMessage[] validateRecievedDt() {
		requiredField(receivedDate, "mailingDate", "Date Request Received");

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	private Comparator<ApplicationDocumentRef> generateAppDocRefComparator() {
		return new Comparator<ApplicationDocumentRef>() {
			public int compare(ApplicationDocumentRef s1,
					ApplicationDocumentRef s2) {
				if (s1.isRequiredDoc() && !s2.isRequiredDoc()) {
					return -1;

				} else if (!s1.isRequiredDoc() && s2.isRequiredDoc()) {
					return 1;

				}

				if (s1.getDocumentId() != null && s2.getDocumentId() == null) {
					return 1;
				} else if (s1.getDocumentId() == null
						&& s2.getDocumentId() != null) {
					return -1;
				}

				return s1.getApplicationDocId().compareTo(
						s2.getApplicationDocId());
			}
		};
	}
	
	public List<String> getInspectionsReferencedIn() {
		if (inspectionsReferencedIn == null) return new ArrayList<String>();
		return inspectionsReferencedIn; 
	}

	public void setInspectionsReferencedIn(List<String> inspectionsReferencedIn) {
		this.inspectionsReferencedIn = inspectionsReferencedIn;
	}
}

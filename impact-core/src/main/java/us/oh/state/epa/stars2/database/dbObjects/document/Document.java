package us.oh.state.epa.stars2.database.dbObjects.document;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class Document extends BaseDB {
	private Integer documentID;
	private Integer lastCheckoutBy;
	private Integer lastModifiedBy;
	private transient Timestamp lastModifiedTS;
	private long lastModifiedTSLong;
	private transient Timestamp uploadDate;
	private long uploadDateLong;
	private String basePath;
	private String extension;
	private String facilityID;
	private String description;
	private boolean temporary;
	private String lastModifiedUserName;
	private String docTypeCd;
	private String generatedDocumentPath;
	private String contentType;
	
	public static final String CONTENT_TYPE_ZIP = "CONTENT_TYPE_ZIP";

	public Document() {
		super();
		this.requiredField(this.lastModifiedBy, "lastModifiedBy");
		this.requiredField(this.facilityID, "facilityID");
		this.requiredField(this.extension, "extension");
		setDirty(false);
	}

	public Document(Document old) {
		super(old);

		if (old != null) {
			setDocumentID(old.getDocumentID());
			setLastCheckoutBy(old.getLastCheckoutBy());
			setLastModifiedBy(old.getLastModifiedBy());
			setLastModifiedUserName(old.getLastModifiedUserName());
			setLastModifiedTS(old.getLastModifiedTS());
			setUploadDate(old.getUploadDate());
			setBasePath(old.getBasePath());
			setFacilityID(old.getFacilityID());
			setDescription(old.getDescription());
			setTemporary(old.isTemporary());
			setDocTypeCd(old.getDocTypeCd());
			setDirty(old.isDirty());
		}
	}

	public void populate(java.sql.ResultSet rs) {

		try {
			setDocumentID(AbstractDAO.getInteger(rs, "document_id"));
			setFacilityID(rs.getString("facility_id"));
			setLastCheckoutBy(AbstractDAO.getInteger(rs, "last_checkout_by"));
			setLastModifiedBy(AbstractDAO.getInteger(rs, "last_modified_by"));
			setLastModifiedTS(rs.getTimestamp("last_modified_ts"));
			setUploadDate(rs.getTimestamp("upload_dt"));
			setBasePath(rs.getString("path"));
			setDescription(rs.getString("description"));
			setTemporary(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("temp_flag")));
			setLastModified(AbstractDAO.getInteger(rs, "dd_lm"));
			setDirty(false);
		} catch (SQLException sqle) {
			// logger.error("Required field error: " + sqle.getMessage(), sqle);
		}
		try {
			setLastModifiedUserName(rs.getString("contact_name"));
		} catch (SQLException sqle2) {
			logger.debug("Optional field 'contact_name' not found");
		}
		try {
			setDocTypeCd(rs.getString("attachment_type_cd"));
		} catch (SQLException sqle3) {
			setDocTypeCd("");
			logger.debug("Optional field 'attachment_type_cd' not found");
		}
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getGeneratedDocumentPath() {
		return generatedDocumentPath;
	}

	public void setGeneratedDocumentPath(String generatedDocumentPath) {
		this.generatedDocumentPath = generatedDocumentPath;
	}

	public final Integer getDocumentID() {
		return documentID;
	}

	public final void setDocumentID(Integer documentID) {
		this.documentID = documentID;
		if (this.documentID != null) {
			basePath = null;
			getBasePath();
		}
	}

	public final Integer getLastCheckoutBy() {
		return lastCheckoutBy;
	}

	public final void setLastCheckoutBy(Integer lastCheckoutBy) {
		this.lastCheckoutBy = lastCheckoutBy;
		setDirty(true);
	}

	public final Integer getLastModifiedBy() {
		return lastModifiedBy;
	}

	public final void setLastModifiedBy(Integer lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
		this.requiredField(this.lastModifiedBy, "lastModifiedBy");
		setDirty(true);
	}

	public final Timestamp getLastModifiedTS() {
		return lastModifiedTS;
	}

	public final void setLastModifiedTS(Timestamp lastModifiedTS) {
		this.lastModifiedTS = lastModifiedTS;
		if (this.lastModifiedTS != null) {
			this.lastModifiedTSLong = this.lastModifiedTS.getTime();
		} else {
			this.lastModifiedTSLong = 0;
		}
		setDirty(true);
	}

	public final Timestamp getUploadDate() {
		return uploadDate;
	}

	public final void setUploadDate(Timestamp uploadDate) {
		this.uploadDate = uploadDate;
		if (this.uploadDate != null) {
			this.uploadDateLong = this.uploadDate.getTime();
		} else {
			this.uploadDateLong = 0;
		}
		setDirty(true);
	}

	/** Input should match a fp_facility.facility_id field. */
	public final void setFacilityID(String facilityID) {
		this.facilityID = facilityID;
		this.requiredField(this.facilityID, "facilityID");
		setDirty(true);
	}

	/** Output should match a fp_facility.facility_id field. */
	public final String getFacilityID() {
		return facilityID;
	}

	public final String getPath() {
		String path = getDirName() + getBasePath();
		path = path.replace('\\', File.separatorChar);
		path = path.replace('/', File.separatorChar);
		return path;
	}
	
	

	/**
	 * A document directory is composed from the facility_id with subdirectories
	 * based on /application/application_id, /permit/permit_id or
	 * /correspondence. The specific file is then named by the document_id (for
	 * applications and permits) or the correspondence_id (for correspondence)
	 * with the mime type as the extension. Use setFacilityID(String),
	 * setApplicationID(Integer), setPermitID(Integer), or
	 * setCorrespondenceID(Integer) to create the appropriate path.
	 */
	public String getDirName() {

		String baseDir = File.separator;

		if (facilityID != null && facilityID.length() > 0) {
			baseDir = File.separator + "Facilities" + File.separator
					+ facilityID;
		}

		return baseDir;
	}

	/**
	 * Base path contains the part of the path AFTER the
	 * /Facilities/facility_id. The facility_id part of the path is stored in
	 * it's own db column in order to make it easy to modify facility ids.
	 */
	public String getBasePath() {

		if (basePath == null || basePath.length() < 1) {
			basePath = File.separator + getFileName();
		}

		return basePath;
	}

	/**
	 * A document directory is composed from the facility_id with subdirectories
	 * based on /application/application_id, /permit/permit_id or
	 * /correspondence. The specific file is then named by the document_id (for
	 * applications and permits) or the correspondence_id (for correspondence)
	 * with the mime type as the extension. Use setFacilityID(String),
	 * setApplicationID(Integer), setPermitID(Integer), or
	 * setCorrespondenceID(Integer) to create the appropriate path.
	 */
	public void setBasePath(String path) {
		this.basePath = path;
		if (basePath != null) {
			int pos = basePath.lastIndexOf(".");
			if (pos >= 0) {
				setExtension(basePath.substring(pos + 1));
			}
		}
		setDirty(true);
	}

	public final String getFileName() {
		String fileName = "";
		if (getDocumentID() != null) {
			if (getExtension() != null && getExtension().length() > 0) {
				fileName = getDocumentID().toString() + "." + getExtension();
			} else {
				fileName = getDocumentID().toString();
			}
		}
		return fileName;
	}

	/** Extension roughly corresponds to mime type. */
	public final String getExtension() {
		return extension;
	}

	/** Extension roughly corresponds to mime type. */
	public final void setExtension(String extension) {
		this.extension = extension;
		this.requiredField(this.extension, "extension");
		setDirty(true);
	}

	public final String getDocURL() {
		String wrongSep = DocumentUtil.getFileStoreBaseURL() + getDirName()
				+ getBasePath();
		
		return wrongSep.replace('\\', '/');
	}

	
	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
		setDirty(true);
	}

	public final boolean isTemporary() {
		return temporary;
	}

	public final void setTemporary(boolean temporary) {
		this.temporary = temporary;
		setDirty(true);
	}

	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = super.hashCode();

		result = PRIME * result
				+ ((documentID == null) ? 0 : documentID.hashCode());
		result = PRIME * result
				+ ((lastCheckoutBy == null) ? 0 : lastCheckoutBy.hashCode());
		result = PRIME * result
				+ ((lastModifiedBy == null) ? 0 : lastModifiedBy.hashCode());
		result = PRIME * result
				+ ((lastModifiedTS == null) ? 0 : lastModifiedTS.hashCode());
		result = PRIME * result
				+ ((uploadDate == null) ? 0 : uploadDate.hashCode());
		result = PRIME * result
				+ ((getPath() == null) ? 0 : getPath().hashCode());
		result = PRIME * result
				+ ((description == null) ? 0 : description.hashCode());
		result = PRIME * result + (temporary ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if ((obj == null) || !(super.equals(obj))
				|| (getClass() != obj.getClass())) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final Document other = (Document) obj;

		if (temporary != other.isTemporary()) {
			return false;
		}

		// Either both null or equal values.
		if (((documentID == null) && (other.getDocumentID() != null))
				|| ((documentID != null) && (other.getDocumentID() == null))
				|| ((documentID != null) && (other.getDocumentID() != null) && !(documentID
						.equals(other.getDocumentID())))) {

			return false;
		}

		// Either both null or equal values.
		if (((lastCheckoutBy == null) && (other.getLastCheckoutBy() != null))
				|| ((lastCheckoutBy != null) && (other.getLastCheckoutBy() == null))
				|| ((lastCheckoutBy != null)
						&& (other.getLastCheckoutBy() != null) && !(lastCheckoutBy
							.equals(other.getLastCheckoutBy())))) {

			return false;
		}

		// Either both null or equal values.
		if (((lastModifiedBy == null) && (other.getLastModifiedBy() != null))
				|| ((lastModifiedBy != null) && (other.getLastModifiedBy() == null))
				|| ((lastModifiedBy != null)
						&& (other.getLastModifiedBy() != null) && !(lastModifiedBy
							.equals(other.getLastModifiedBy())))) {

			return false;
		}

		// Either both null or equal values.
		if (((lastModifiedTS == null) && (other.getLastModifiedTS() != null))
				|| ((lastModifiedTS != null) && (other.getLastModifiedTS() == null))
				|| ((lastModifiedTS != null)
						&& (other.getLastModifiedTS() != null) && !(lastModifiedTS
							.equals(other.getLastModifiedTS())))) {

			return false;
		}

		// Either both null or equal values.
		if (((uploadDate == null) && (other.getUploadDate() != null))
				|| ((uploadDate != null) && (other.getUploadDate() == null))
				|| ((uploadDate != null) && (other.getUploadDate() != null) && !(uploadDate
						.equals(other.getUploadDate())))) {

			return false;
		}

		// Either both null or equal values.
		if (((getPath() == null) && (other.getPath() != null))
				|| ((getPath() != null) && (other.getPath() == null))
				|| ((getPath() != null) && (other.getPath() != null) && !(getPath()
						.equals(other.getPath())))) {

			return false;
		}

		// Either both null or equal values.
		if (((description == null) && (other.getDescription() != null))
				|| ((description != null) && (other.getDescription() == null))
				|| ((description != null) && (other.getDescription() != null) && !(description
						.equals(other.getDescription())))) {

			return false;
		}

		return true;
	}

	public final String getLastModifiedUserName() {
		return lastModifiedUserName;
	}

	public final void setLastModifiedUserName(String lastModifiedUserName) {
		this.lastModifiedUserName = lastModifiedUserName;
	}

	public String getDocTypeCd() {
		return docTypeCd;
	}

	public void setDocTypeCd(String docTypeCd) {
		this.docTypeCd = docTypeCd;
	}

	public final long getLastModifiedTSLong() {
		long ts = 0;
		if (lastModifiedTS != null) {
			ts = lastModifiedTS.getTime();
		}
		return ts;
	}

	public final void setLastModifiedTSLong(long lastModifiedTSLong) {
		lastModifiedTS = null;
		if (lastModifiedTSLong > 0) {
			lastModifiedTS = new Timestamp(lastModifiedTSLong);
		}
	}

	public final long getUploadDateLong() {
		long ts = 0;
		if (uploadDate != null) {
			ts = uploadDate.getTime();
		}
		return ts;
	}

	public final void setUploadDateLong(long uploadDateLong) {
		uploadDate = null;
		if (uploadDateLong > 0) {
			uploadDate = new Timestamp(uploadDateLong);
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		// manually set transient date values since this does not appear to
		// work properly with persistence
		setUploadDateLong(this.uploadDateLong);
		setLastModifiedTSLong(this.lastModifiedTSLong);
	}
}

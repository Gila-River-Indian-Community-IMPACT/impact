package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;

/**
 * This is an extension of the Document class that exists simply to provide a
 * means for defining the path attribute for application documents by allowing
 * an application id to be set and using this id in the getDirName method used
 * by Document.getPath() to determine the path of the document.
 *
 */

@SuppressWarnings("serial")
public class EmissionsDocument extends Document {

	// kludge to fix problem with converting this class to XML
	// Apparently, the XML conversion code processes attributes in
	// alphabetical order and this was causing basePath to be recorded
	// without the emissionsReportId. Creating the dummy element "aaaReportId"
	// fixes this problem.
	private Integer aaaReportId;

	private Integer emissionsRptId;
	private String overloadFileName;

	public EmissionsDocument() {
		super();
	}

	/**
	 * Copy Constructor
	 * 
	 * @param EmissionsDocument
	 *            a <code>EmissionsDocument</code> object
	 */
	public EmissionsDocument(EmissionsDocument eDoc) {
		super(eDoc);

		if (eDoc != null) {
			this.emissionsRptId = eDoc.emissionsRptId;
		}
	}

	/**
	 * Create ApplicationDocument from a Document.
	 * 
	 * @param doc
	 */
	public EmissionsDocument(Document doc) {
		super(doc);
	}

	@Override
	public final String getBasePath() {

		if (super.getBasePath() != null && !(super.getBasePath().equals(File.separator + getFileName())
				|| super.getBasePath().equals(File.separator + File.separator + getFileName()))) {
			return super.getBasePath();
		}

		String ret = File.separator + "EmissionsInventories" + File.separator;
		if (emissionsRptId != null) {
			ret += emissionsRptId + File.separator;
		}
		if (getOverloadFileName() == null) {
			ret += getFileName();
		} else {
			ret += getOverloadFileName();
		}
		return ret;
	}

	@Override
	public final void populate(ResultSet rs) {
		// This populate method is called only by Invoice object to set docURL
		// for Invoice document.
		try {
			setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));

			super.populate(rs);
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	public final Integer getEmissionRptId() {
		return emissionsRptId;
	}

	public final void setEmissionsRptId(Integer emissionRptId) {
		this.emissionsRptId = emissionRptId;
		this.aaaReportId = emissionRptId;
	}

	public final String getOverloadFileName() {
		return overloadFileName;
	}

	public final void setOverloadFileName(String overloadFileName) {
		this.overloadFileName = overloadFileName;
	}

	public final Integer getAaaReportId() {
		return aaaReportId;
	}

	public final void setAaaReportId(Integer aaaReportId) {
		if (aaaReportId != null) {
			this.emissionsRptId = aaaReportId;
		}
		this.aaaReportId = aaaReportId;
	}
}

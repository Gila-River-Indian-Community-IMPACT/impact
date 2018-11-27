package us.oh.state.epa.stars2.database.dbObjects.document;

import java.io.File;

/**
 * This is an extension of the Document class that exists simply to provide
 * a means for defining the path attribute for nei documents by allowing
 * an year to be set and using this year in the getDirName method
 * used by Document.getPath() to determine the path of the document.
 *
 */

public class NEIDocument extends Document {

	private Integer year;

	private String overLoadFileName;

	public NEIDocument() {
		super();
		this.requiredField(year, "year");
		setDirty(false);
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getOverLoadFileName() {
		return overLoadFileName;
	}

	public void setOverLoadFileName(String overLoadFileName) {
		this.overLoadFileName = overLoadFileName;
	}

	public final String getBasePath() {
		String ret = File.separator + "NEI" + File.separator;
		if (year != null) {
			ret += year + File.separator;
		}
		if (getOverLoadFileName() == null) {
			ret += getFileName();
		} else {
			ret += getOverLoadFileName() + "." + getExtension();
		}
		return ret;
	}

}

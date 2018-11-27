package us.wy.state.deq.impact.app.emissionsReport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;

public class EiDataImportCriteria {
	
	public static final Integer ANALYZE_IMPORT = 1;
	public static final Integer PERFORM_IMPORT = 2;
	
	private Integer eiImportYear;
	private String eiImportContentTypeCd;
	private String eiImportRegulatoryRequirement;
	private UploadedFile eiDataFileToUpload;
	private UploadedFileInfo eiDataInfo;
	private Integer importChoice = null;
	private String logFileString;
	private String errFileString;
	private Document logFileDoc = new Document();
	private Document errFileDoc = new Document();
	
	// Attachments
	protected List<Attachment> attachmentDocs = new ArrayList<Attachment>(0); // used for print/download

	public List<Attachment> getAttachmentDocs() {
		return attachmentDocs;
	}

	public void setAttachmentDocs(List<Attachment> attachmentDocs) {
		this.attachmentDocs = attachmentDocs;
	}

	/**
	 * validate importEiData input fields
	 */
	protected boolean isValidEiImportFields() {
		boolean ret = true;
		/*
		 * Inventory year cannot be null Content Type cannot be null Regulatory
		 * Requirement cannot be null eiDataFileToUpload cannot be null
		 */
		if (Utility.isNullOrZero(eiImportYear)) {
			DisplayUtil.displayError("Inventory year is required.");
			ret = false;
		}
		if (Utility.isNullOrEmpty(eiImportContentTypeCd)) {
			DisplayUtil.displayError("Content Type is required.");
			ret = false;
		}
		if (Utility.isNullOrEmpty(eiImportRegulatoryRequirement)) {
			DisplayUtil.displayError("Regulatory Requirement is required.");
			ret = false;
		}
		if (eiDataFileToUpload == null || eiDataFileToUpload.getFilename().length() == 0) {
			DisplayUtil.displayError("No Emissions Inventory Data File to Import has been chosen.");
			ret = false;
		} else {
			String fileName = eiDataFileToUpload.getFilename();
			if (!fileName.matches(".*\\.csv$")) {
				DisplayUtil.displayError("Data file " + fileName + " is not in csv format.");
				ret = false;
			}
		}
		if (importChoice == null) {
			DisplayUtil.displayError("No operation picked.");
			ret = false;
		}

		return ret;
	}

	public Integer getEiImportYear() {
		return eiImportYear;
	}

	public void setEiImportYear(Integer eiImportYear) {
		this.eiImportYear = eiImportYear;
	}

	public String getEiImportContentTypeCd() {
		return eiImportContentTypeCd;
	}

	public void setEiImportContentTypeCd(String eiImportContentTypeCd) {
		this.eiImportContentTypeCd = eiImportContentTypeCd;
	}

	public String getEiImportRegulatoryRequirement() {
		return eiImportRegulatoryRequirement;
	}

	public void setEiImportRegulatoryRequirement(String eiImportRegulatoryRequirement) {
		this.eiImportRegulatoryRequirement = eiImportRegulatoryRequirement;
	}

	public UploadedFile getEiDataFileToUpload() {
		return eiDataFileToUpload;
	}

	public void setEiDataFileToUpload(UploadedFile eiDataFileToUpload) {
		this.eiDataFileToUpload = eiDataFileToUpload;
	}

	public UploadedFileInfo getEiDataInfo() {
		return eiDataInfo;
	}

	public void setEiDataInfo(UploadedFileInfo eiDataInfo) {
		this.eiDataInfo = eiDataInfo;
	}

	public Integer getImportChoice() {
		return importChoice;
	}

	public void setImportChoice(Integer importChoice) {
		this.importChoice = importChoice;
	}

	public String getOperation() {
		String operation = "";
		if (this.importChoice == ANALYZE_IMPORT) {
			operation = "Analyze import file";
		} else if (this.importChoice == PERFORM_IMPORT) {
			operation = "Perform Data Import";
		}
		return operation;
	}

	public String getLogFileString() {
		return logFileString;
	}

	public void setLogFileString(String logFileString) {
		this.logFileString = logFileString;
		getLogFileDoc().setBasePath(EiDataImport.EI_DATA_IMPORT_PATH + File.separator + this.logFileString);
	}

	public String getErrFileString() {
		return errFileString;
	}

	public void setErrFileString(String errFileString) {
		this.errFileString = errFileString;
		getErrFileDoc().setBasePath(EiDataImport.EI_DATA_IMPORT_PATH + File.separator + this.errFileString);
	}
	
	public void setLogFileDoc(Document logFileDoc) {
		this.logFileDoc = logFileDoc;
	}

	public Document getLogFileDoc() {
		return logFileDoc;
	}

	public Document getErrFileDoc() {
		return errFileDoc;
	}

	public void setErrFileDoc(Document errFileDoc) {
		this.errFileDoc = errFileDoc;
	}
}

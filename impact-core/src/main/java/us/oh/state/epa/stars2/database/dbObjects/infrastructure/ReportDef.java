package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;

@SuppressWarnings("serial")
public class ReportDef extends ReportBase {

    private String jasperDefFile;
    
    private UploadedFile reportFile;

    private Integer reportDocumentId;
    
    private Document reportDocument;

    public ReportDef() {
        super();
    }

    public ReportDef(ReportDef old) {
        super(old);

        if (old != null) {
            setJasperDefFile(old.getJasperDefFile());
            setReportFile(old.reportFile);
            setReportDocumentId(old.getReportDocumentId());
            setReportDocument(old.getReportDocument());
        }
        
        
    }

    
    
    public Document getReportDocument() {
		return reportDocument;
	}

	public void setReportDocument(Document reportDocument) {
		this.reportDocument = reportDocument;
	}

	public Integer getReportDocumentId() {
		return reportDocumentId;
	}

	public void setReportDocumentId(Integer reportDocumentId) {
		this.reportDocumentId = reportDocumentId;
	}

	public String getReportFileName() {
		return null == reportFile? 
				(null == reportDocument? null : reportDocument.getFileName()) : 
				reportFile.getFilename();
	}

	public UploadedFile getReportFile() {
		return reportFile;
	}

	public void setReportFile(UploadedFile reportFile) {
		this.reportFile = reportFile;
	}

	public final String getJasperDefFile() {
        return jasperDefFile;
    }

    public final void setJasperDefFile(String jasperDefFile) {
        this.jasperDefFile = jasperDefFile;
    }

    public final void populate(ResultSet rs) {

        try {
            setJasperDefFile(rs.getString("jasper_report_file_nm"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
            setReportDocumentId(rs.getInt("report_document"));
            
            super.populate(rs);
        } 
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

	@Override
	public ValidationMessage[] validate() {
		super.validate();
		
		if(Utility.isNullOrEmpty(this.getReportFileName())) {
			ValidationMessage valMsg = new ValidationMessage("reportFileName", "Set a report file.", 
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("reportFileName", valMsg);
		} else{
			if (!DocumentUtil.isValidFileExtension(this.getReportFileName())){
				ValidationMessage valMsg = new ValidationMessage("reportFileName", DocumentUtil.invalidFileExtensionMessage("Report"), 
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("reportFileName", valMsg);
			}
		}
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
}

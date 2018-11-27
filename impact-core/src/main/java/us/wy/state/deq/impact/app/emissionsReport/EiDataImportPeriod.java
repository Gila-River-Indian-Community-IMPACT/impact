package us.wy.state.deq.impact.app.emissionsReport;

public class EiDataImportPeriod extends EiDataImportRow {

	private static final long serialVersionUID = 8165180046358008690L;

	private String sccId;
	private Integer corrEpaEmuId;

	public EiDataImportPeriod(EiDataImportRow dataImportRow) {
		super(dataImportRow);
	}
	
	public EiDataImportPeriod(EiDataImportRow dataImportRow, String sccId, Integer corrEpaEmuId) {
		super(dataImportRow);
		setSccId(sccId);
		setCorrEpaEmuId(corrEpaEmuId);
	}

	public String getSccId() {
		return sccId;
	}

	public void setSccId(String sccId) {
		this.sccId = sccId;
	}

	public Integer getCorrEpaEmuId() {
		return corrEpaEmuId;
	}

	public void setCorrEpaEmuId(Integer corrEpaEmuId) {
		this.corrEpaEmuId = corrEpaEmuId;
	}

}

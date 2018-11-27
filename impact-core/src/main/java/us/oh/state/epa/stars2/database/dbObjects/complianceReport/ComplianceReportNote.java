package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class ComplianceReportNote extends Note {

	private Integer _reportId;

	public ComplianceReportNote() {
		this.requiredField(_reportId, "reportId");
	}

	public ComplianceReportNote(ComplianceReportNote note) {
		super(note);
		setReportId(note.getReportId());
		setDirty(note.isDirty());
	}



	public final void populate(ResultSet rs) {

		try {
			super.populate(rs);
			setReportId(AbstractDAO.getInteger(rs, "REPORT_ID"));
			setDirty(false);
		} 
		catch (SQLException sqle) {
			logger.error("Required field error");
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((_reportId == null) ? 0 : _reportId.hashCode());
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

		final ComplianceReportNote other = (ComplianceReportNote) obj;
		// Either both null or equal values.
		if (((_reportId == null) && (other.getReportId() != null))
				|| ((_reportId != null) && (other.getReportId() == null))
				|| ((_reportId != null) && (other.getReportId() != null) 
						&& !(_reportId.equals(other.getReportId())))) {

			return false;
		}
		
		return true;
	}

	public Integer getReportId() {
		return _reportId;
	}

	public void setReportId(Integer reportId) {
		this._reportId = reportId;
		this.requiredField(_reportId, "reportId");
		setDirty(true);
	}
}

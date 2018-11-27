package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class EmissionsReportNote extends Note {
    private Integer emissionsRptId;

    public EmissionsReportNote() {
        super();
    }

    public EmissionsReportNote(EmissionsReportNote old) {
        super(old);

        if (old != null) {
            setEmissionsRptId(old.getEmissionsRptId());
        }
    }

    public final Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public final void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
    }

    @Override
    public final void populate(ResultSet rs) {
        try {
            setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));
            super.populate(rs);
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((emissionsRptId == null) ? 0 : emissionsRptId.hashCode());
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
        final EmissionsReportNote other = (EmissionsReportNote) obj;
        if (emissionsRptId == null) {
            if (other.emissionsRptId != null)
                return false;
        } else if (!emissionsRptId.equals(other.emissionsRptId))
            return false;
        return true;
    }
}

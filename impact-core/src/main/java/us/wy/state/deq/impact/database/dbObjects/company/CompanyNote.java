package us.wy.state.deq.impact.database.dbObjects.company;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class CompanyNote extends Note {

	private static final long serialVersionUID = 3759413533740029319L;

	private Integer companyId;
    private String cmpId;

    public CompanyNote() {

    }

    public CompanyNote(CompanyNote note) {
        super(note);
        this.companyId = note.companyId;
    }

    public final Integer getCompanyId() {
        return companyId;
    }

    public final void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public final String getCmpId() {
        return cmpId;
    }

    public final void setCmpId(String cmpId) {
        this.cmpId = cmpId;
    }

    public final void populate(ResultSet rs) {
        try {
        	setCompanyId(AbstractDAO.getInteger(rs, "company_id"));
            setCmpId(rs.getString("cmp_id"));

            super.populate(rs);
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}

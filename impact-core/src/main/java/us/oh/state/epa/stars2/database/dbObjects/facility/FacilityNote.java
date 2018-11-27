package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class FacilityNote extends Note {
    private Integer fpId;
    private String facilityId;

    public FacilityNote() {
    	super();
    }

    public FacilityNote(FacilityNote note) {
        super(note);
        this.fpId = note.fpId;
        this.facilityId = note.facilityId;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final void populate(ResultSet rs) {
        try {
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setFacilityId(rs.getString("facility_id"));

            super.populate(rs);
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
}

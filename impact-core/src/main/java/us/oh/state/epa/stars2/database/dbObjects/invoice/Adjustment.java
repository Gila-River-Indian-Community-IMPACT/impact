package us.oh.state.epa.stars2.database.dbObjects.invoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class Adjustment extends BaseDB {
    static final long serialVersionUID = 1L;
    private String type;
    private String createdBy;
    private String reason;
    private Timestamp dateApplied;
    private Float adjustment;

    public Adjustment() {
        super();
    }

    public Adjustment(Adjustment old) {
        super(old);

        if (old != null) {
            setType(old.getType());
            setCreatedBy(old.getCreatedBy());
            setReason(old.getReason());
            setDateApplied(old.getDateApplied());
            setAdjustment(old.getAdjustment());
        }
    }

    public final Float getAdjustment() {
        return adjustment;
    }

    public final void setAdjustment(Float adjustment) {
        this.adjustment = adjustment;
    }

    public final String getCreatedBy() {
        return createdBy;
    }

    public final void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public final Timestamp getDateApplied() {
        return dateApplied;
    }

    public final void setDateApplied(Timestamp dateApplied) {
        this.dateApplied = dateApplied;
    }

    public final String getReason() {
        return reason;
    }

    public final void setReason(String reason) {
        this.reason = reason;
    }

    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public final void populate(ResultSet rs) throws SQLException {
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((adjustment == null) ? 0 : adjustment.hashCode());
        result = PRIME * result
                + ((createdBy == null) ? 0 : createdBy.hashCode());
        result = PRIME * result
                + ((dateApplied == null) ? 0 : dateApplied.hashCode());
        result = PRIME * result + ((reason == null) ? 0 : reason.hashCode());
        result = PRIME * result + ((type == null) ? 0 : type.hashCode());
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
        final Adjustment other = (Adjustment) obj;
        if (adjustment == null) {
            if (other.adjustment != null)
                return false;
        } else if (!adjustment.equals(other.adjustment))
            return false;
        if (createdBy == null) {
            if (other.createdBy != null)
                return false;
        } else if (!createdBy.equals(other.createdBy))
            return false;
        if (dateApplied == null) {
            if (other.dateApplied != null)
                return false;
        } else if (!dateApplied.equals(other.dateApplied))
            return false;
        if (reason == null) {
            if (other.reason != null)
                return false;
        } else if (!reason.equals(other.reason))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}

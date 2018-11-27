package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class Correspondence extends BaseDB {
    private Integer fpId;
    private Integer correspondId;
    private String type;
    private Timestamp date;
    private Integer sentById;
    private String sentTo;
    private String note;

    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public final Integer getCorrespondId() {
        return correspondId;
    }

    public final void setCorrespondId(Integer correspondId) {
        this.correspondId = correspondId;
    }

    public final Integer getSentById() {
        return sentById;
    }

    public final void setsentById(Integer sentById) {
        this.sentById = sentById;
    }

    public final String getSentTo() {
        return sentTo;
    }

    public final void setSentTo(String sendTo) {
        this.sentTo = sendTo;
    }

    public final String getNote() {
        return note;
    }

    public final void setNote(String note) {
        this.note = note;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public final Timestamp getDate() {
        return date;
    }

    public final void setDate(Timestamp date) {
        this.date = date;
    }

    public final void populate(ResultSet rs) {
    }
}

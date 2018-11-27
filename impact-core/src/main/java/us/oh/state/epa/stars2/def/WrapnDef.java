package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;

/*
 * The codes declared in this class must match the content of the
 * CM_WRAPN_DEF table.
 */
@SuppressWarnings("serial")
public class WrapnDef extends SimpleDef {

    private Integer _noticeTypeID;
    private Integer _noticeRemarkID;
    private String  _defaultPublicNoticeText;
    private String  _defaultPSDPublicNoticeText;

    public WrapnDef() {
        super();
    }

    public WrapnDef(Integer noticeTypeID, Integer noticeRemarkID) {
        _noticeTypeID = noticeTypeID;
        _noticeRemarkID = noticeRemarkID;
    }

    public WrapnDef(WrapnDef old) {
        super(old);
        setNoticeTypeID(old.getNoticeTypeID());
        setNoticeRemarkID(old.getNoticeRemarkID());
        setDefaultPublicNoticeText(old.getDefaultPublicNoticeText());
    }

    public void populate(ResultSet rs) {
        try {
            setNoticeTypeID(AbstractDAO.getInteger(rs, "noticetypeid"));
            setNoticeRemarkID(AbstractDAO.getInteger(rs, "noticeremarkid"));
            setDefaultPublicNoticeText(rs.getString("default_public_notice_text"));
            setDefaultPublicNoticeText(rs.getString("default_psd_public_notice_text"));
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
        super.populate(rs);
    }

    public Integer getNoticeTypeID() {
        return _noticeTypeID;
    }

    public void setNoticeTypeID(Integer noticeTypeID) {
        _noticeTypeID = noticeTypeID;
    }

    public Integer getNoticeRemarkID() {
        return _noticeRemarkID;
    }

    public void setNoticeRemarkID(Integer noticeRemarkID) {
        _noticeRemarkID = noticeRemarkID;
    }

    public String getDefaultPublicNoticeText() {
        return _defaultPublicNoticeText;
    }

    public void setDefaultPublicNoticeText(String defaultPublicNoticeText) {
        _defaultPublicNoticeText = defaultPublicNoticeText;
    }

	public String get_defaultPSDPublicNoticeText() {
		return _defaultPSDPublicNoticeText;
	}

	public void set_defaultPSDPublicNoticeText(
			String _defaultPSDPublicNoticeText) {
		this._defaultPSDPublicNoticeText = _defaultPSDPublicNoticeText;
	}

}

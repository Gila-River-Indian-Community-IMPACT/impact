package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("serial")
public class LetterProject extends Project {
	
	private String letterId;
	private String letterTypeCd;
	
	public LetterProject() {
		super();
	}
	
	public LetterProject(LetterProject old) {
		super(old); 
		if(null != old) {
			setLetterId(old.getLetterId());
			setLetterTypeCd(old.getLetterTypeCd());
		}
	}

	public String getLetterId() {
		return letterId;
	}

	public void setLetterId(String letterId) {
		this.letterId = letterId;
	}

	public String getLetterTypeCd() {
		return letterTypeCd;
	}

	public void setLetterTypeCd(String letterTypeCd) {
		this.letterTypeCd = letterTypeCd;
	}
	
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			super.populate(rs);
			setLetterId(rs.getString("letter_id"));
			setLetterTypeCd(rs.getString("letter_type_cd"));
		}
	}
}

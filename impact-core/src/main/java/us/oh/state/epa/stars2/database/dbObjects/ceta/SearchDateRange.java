package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class SearchDateRange extends BaseDB{

	private Timestamp startDt;
	private Timestamp endDt;
	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		setStartDt(rs.getTimestamp("start_date"));
		setEndDt(rs.getTimestamp("end_date"));
		setNewObject(false);
		
	}
	
	public Timestamp getStartDt() {
		return startDt;
	}
	
	public void setStartDt(Timestamp startDt) {
		this.startDt = startDt;
	}
	
	public Timestamp getEndDt() {
		return endDt;
	}
	
	public void setEndDt(Timestamp endDt) {
		this.endDt = endDt;
	}
}

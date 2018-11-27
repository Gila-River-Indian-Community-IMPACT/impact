package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class RelocationAddtlAddr extends BaseDB {
	private Integer addtlAddrId;
	private Integer requestId;
	private String futureAddress;
	private String targetCountyCd;
	
	public RelocationAddtlAddr() {
		addtlAddrId = 0;
	}
	
	public void populate(ResultSet rs) throws SQLException {
		setAddtlAddrId(AbstractDAO.getInteger(rs, "addtl_addr_id"));
		setRequestId(AbstractDAO.getInteger(rs, "request_id"));
		setFutureAddress(rs.getString("future_address"));
		setTargetCountyCd(rs.getString("target_county_cd"));
	}

	public final Integer getAddtlAddrId() {
		return addtlAddrId;
	}

	public final void setAddtlAddrId(Integer addtlAddrId) {
		this.addtlAddrId = addtlAddrId;
	}

	public final Integer getRequestId() {
		return requestId;
	}

	public final void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public final String getFutureAddress() {
		return futureAddress;
	}

	public final void setFutureAddress(String futureAddress) {
		this.futureAddress = futureAddress;
	}

	public final String getTargetCountyCd() {
		return targetCountyCd;
	}

	public final void setTargetCountyCd(String targetCountyCd) {
		this.targetCountyCd = targetCountyCd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((futureAddress == null) ? 0 : futureAddress.hashCode());
		result = prime * result
				+ ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result
				+ ((targetCountyCd == null) ? 0 : targetCountyCd.hashCode());
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
		RelocationAddtlAddr other = (RelocationAddtlAddr) obj;
		if (futureAddress == null) {
			if (other.futureAddress != null)
				return false;
		} else if (!futureAddress.equals(other.futureAddress))
			return false;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
			return false;
		if (targetCountyCd == null) {
			if (other.targetCountyCd != null)
				return false;
		} else if (!targetCountyCd.equals(other.targetCountyCd))
			return false;
		return true;
	}

}

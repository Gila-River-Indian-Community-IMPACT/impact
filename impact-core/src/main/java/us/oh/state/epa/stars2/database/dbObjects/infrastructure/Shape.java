package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class Shape extends BaseDB {

	private Integer shapeId;
	
	private String label;
	
	private String description;
	
	private String geog;
	
	private String envelopeCenter;
	
	private BigDecimal area;
	
	private BigDecimal length;
	
	private Integer numPoints;
	
	public Shape() {
		super();
	}
	
	public Shape(Shape old) {
		super(old);
		if(null != old) {
			setShapeId(old.getShapeId());
			setLabel(old.getLabel());
			setDescription(old.getDescription());
			setGeog(old.getGeog());
			setEnvelopeCenter(old.getEnvelopeCenter());
			setArea(old.getArea());
			setLength(old.getLength());
			setNumPoints(old.getNumPoints());
			setLastModified(old.getLastModified());
		}
	}
	
	private String wkt2Json(String wkt, String regex) {
		String json = null;
		String pointStr = null;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(wkt);
		if (m.find()) {
			pointStr = m.group(1);
		}
		if (null != pointStr) {
			StringBuffer sb = new StringBuffer();
			String[] points = pointStr.split(",");
			for (String s : points) {
				s = s.trim();
				String [] point = s.split(" ");
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append('{').append("lat: ").append(point[1]).append(", ").append("lng: ").append(point[0]).append('}');
			}
			if (points.length > 1) {
				sb.insert(0,'[').append(']');
			}
			json = sb.toString();
		}
		return json;
	}
	
	public String getPathJson() {
		return wkt2Json(getGeog(), "POLYGON \\(\\((.*?)\\)\\)");
	}
	
	public String getCenterJson() {
		return wkt2Json(getEnvelopeCenter(), "POINT \\((.*?)\\)");
	}
	
	public BigDecimal getArea() {
		return area;
	}

	public void setArea(BigDecimal area) {
		this.area = area;
	}

	public BigDecimal getLength() {
		return length;
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public Integer getNumPoints() {
		return numPoints;
	}

	public void setNumPoints(Integer numPoints) {
		this.numPoints = numPoints;
	}

	public String getEnvelopeCenter() {
		return envelopeCenter;
	}

	public void setEnvelopeCenter(String envelopeCenter) {
		this.envelopeCenter = envelopeCenter;
	}
	
	public Integer getShapeId() {
		return shapeId;
	}

	public void setShapeId(Integer shapeId) {
		this.shapeId = shapeId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGeog() {
		return geog;
	}

	public void setGeog(String geog) {
		this.geog = geog;
	}



	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setShapeId(AbstractDAO.getInteger(rs, "shape_id"));
			setLabel(rs.getString("label"));
			setDescription(rs.getString("description"));
			setArea(rs.getBigDecimal("area"));
			setLength(rs.getBigDecimal("length"));
			setNumPoints(rs.getInt("num_points"));
			setGeog(rs.getString("geog"));
			setEnvelopeCenter(rs.getString("envelope_center"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		}		
	}

}

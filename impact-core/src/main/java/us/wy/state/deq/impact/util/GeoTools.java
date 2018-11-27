package us.wy.state.deq.impact.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.operation.DefiningConversion;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;

public class GeoTools {
	
	private static final String EAST = "E";
	private static final String WEST = "W";
	
	private static Map<Integer, Double> utmZoneCenterLongitudes = 
			new HashMap<Integer, Double>();
	private static Map<Integer, Map<String, Double>> utmZoneBoundaries = 
			new HashMap<Integer, Map<String, Double>>();
	
	static {
		/* Please Note: Wyoming spans UTM zones 12 and 13 only */
		
		utmZoneCenterLongitudes.put(12,-111.0);
		utmZoneCenterLongitudes.put(13,-105.0);
		
		Map<String,Double> zone12Bounderies = new HashMap<String,Double>();
		zone12Bounderies.put(EAST,-108.0);
		zone12Bounderies.put(WEST,-114.0);

		Map<String,Double> zone13Bounderies = new HashMap<String,Double>();
		zone13Bounderies.put(EAST,-102.0);
		zone13Bounderies.put(WEST,-108.0);

		utmZoneBoundaries.put(12, zone12Bounderies);
		utmZoneBoundaries.put(13, zone13Bounderies);
	}
	
	public static UtmCoordinate latLonToUtm(double latitude, double longitude) 
			throws Exception {
		int zoneNumber = getZone(longitude);
		double utmZoneCenterLongitude = utmZoneCenterLongitudes.get(zoneNumber);

		MathTransformFactory mtFactory = 
				ReferencingFactoryFinder.getMathTransformFactory(null);
	    CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);

		GeographicCRS geoCRS = 
				org.geotools.referencing.crs.DefaultGeographicCRS.WGS84;
		CartesianCS cartCS = 
				org.geotools.referencing.cs.DefaultCartesianCS.GENERIC_2D;

		ParameterValueGroup parameters = 
				mtFactory.getDefaultParameters("Transverse_Mercator");
		parameters.parameter("central_meridian").setValue(
				utmZoneCenterLongitude);
		parameters.parameter("latitude_of_origin").setValue(0.0);
		parameters.parameter("scale_factor").setValue(0.9996);
		parameters.parameter("false_easting").setValue(500000.0);
		parameters.parameter("false_northing").setValue(0.0);
	    Conversion conversion = 
	    		new DefiningConversion("Transverse_Mercator", parameters);

	    Map<String, ?> properties = 
	    		Collections.singletonMap("name", "WGS 84 / UTM Zone ");
	    ProjectedCRS projCRS = 
	    		crsFactory.createProjectedCRS(properties, geoCRS, conversion, 
	    				cartCS);
	    
		MathTransform transform = CRS.findMathTransform(geoCRS, projCRS);

		double[] dest = new double[2];
		transform.transform(new double[] {longitude, latitude}, 0, dest, 0, 1);

		int easting = (int)Math.round(dest[0]);
		int northing = (int)Math.round(dest[1]);
		
		UtmCoordinate utmCoordinate = new UtmCoordinate();
		utmCoordinate.setZone(zoneNumber);
		utmCoordinate.setEasting(easting);
		utmCoordinate.setNorthing(northing);
		
		return utmCoordinate;
	}
	
	private static int getZone(double longitude) throws Exception {
		for (int zone : utmZoneBoundaries.keySet()) {
			if (longitude <= utmZoneBoundaries.get(zone).get(EAST) &&
					longitude > utmZoneBoundaries.get(zone).get(WEST)) {
				return zone;
			}
		}
		throw new Exception("Longitude not in UTM zone 12 or 13: " + longitude);
	}

	public static class UtmCoordinate {
		private int zone;
		private int easting;
		private int northing;
		public int getZone() {
			return zone;
		}
		public void setZone(int zone) {
			this.zone = zone;
		}
		public int getEasting() {
			return easting;
		}
		public void setEasting(int easting) {
			this.easting = easting;
		}
		public int getNorthing() {
			return northing;
		}
		public void setNorthing(int northing) {
			this.northing = northing;
		}
	}
}

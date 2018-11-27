package us.wy.state.deq.impact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import us.wy.state.deq.impact.util.GeoTools;
import us.wy.state.deq.impact.util.GeoTools.UtmCoordinate;

public class GeoToolsTest {

	
	@Test
	public void testLatLonToUtm() throws Exception {
		double latitude = 44.555108;
		double longitude = -110.621790;
		
		UtmCoordinate utmc = GeoTools.latLonToUtm(latitude, longitude);
		
		System.out.println("Lat/Lon: " + latitude + " " + longitude);
		System.out.println("UTM " + utmc.getZone() + " " + 
				utmc.getEasting() + " " + utmc.getNorthing());
		
		assertEquals(12, utmc.getZone());
		assertEquals(530039, utmc.getEasting());
		assertEquals(4933600, utmc.getNorthing());
	}
	
	@Test
	public void testLatLonToUtmOnBoundary() throws Exception {
		double latitude = 44.555108;
		double longitude = -102.0;
		
		UtmCoordinate utmc = GeoTools.latLonToUtm(latitude, longitude);
		
		System.out.println("Lat/Lon: " + latitude + " " + longitude);
		System.out.println("UTM " + utmc.getZone() + " " + 
				utmc.getEasting() + " " + utmc.getNorthing());
		
		assertEquals(13, utmc.getZone());
	}
	
	@Test
	public void testLatLonToUtmOnBoundary2() throws Exception {
		double latitude = 44.555108;
		double longitude = -114.0;
		try {
			GeoTools.latLonToUtm(latitude, longitude);
			fail("expected an exception");
		} catch (Exception e) {
			// pass
			e.printStackTrace();
		}		
	}
	
	@Test
	public void testLatLonToUtmOnBoundary3() throws Exception {
		double latitude = 44.555108;
		double longitude = -108.0;
		
		UtmCoordinate utmc = GeoTools.latLonToUtm(latitude, longitude);
		
		System.out.println("Lat/Lon: " + latitude + " " + longitude);
		System.out.println("UTM " + utmc.getZone() + " " + 
				utmc.getEasting() + " " + utmc.getNorthing());
		
		assertEquals(12, utmc.getZone());
	}
	
	@Test
	public void testLatLonToUtmWithBogusLon() throws Exception {
		double latitude = 44.555108;
		double longitude = -115.0;

		try {
			GeoTools.latLonToUtm(latitude, longitude);
			fail("expected an exception");
		} catch (Exception e) {
			// pass
			e.printStackTrace();
		}
	}

	@Test
	public void testLatLonToUtmWithBogusLon2() throws Exception {
		double latitude = 44.555108;
		double longitude = -100.0;

		try {
			GeoTools.latLonToUtm(latitude, longitude);
			fail("expected an exception");
		} catch (Exception e) {
			// pass
			e.printStackTrace();
		}
	}
}

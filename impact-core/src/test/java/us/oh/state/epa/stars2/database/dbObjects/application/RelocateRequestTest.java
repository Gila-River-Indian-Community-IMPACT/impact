package us.oh.state.epa.stars2.database.dbObjects.application;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedOutputStream;
import java.io.IOException;

import org.junit.Test;

public class RelocateRequestTest {
	
	@Test
	public void testToXML() {
		RelocateRequest rr = new RelocateRequest();
		rr.setApplicationID(1);
		rr.setRequestId(1);
		rr.setApplicationAmended(false);
		rr.setApplicationCorrected(false);
		rr.setApplicationNumber("TEST");
		rr.setApplicationTypeCD("SPA");
		rr.setFutureAddress("test 1");
		rr.setTargetCountyCd("26");
		RelocationAddtlAddr aa = new RelocationAddtlAddr();
		aa.setAddtlAddrId(1);
		aa.setRequestId(1);
		aa.setFutureAddress("test 2");
		aa.setTargetCountyCd("27");
		rr.addAdditionalAddress(aa);
		aa = new RelocationAddtlAddr();
		aa.setAddtlAddrId(2);
		aa.setRequestId(1);
		aa.setFutureAddress("test 3");
		aa.setTargetCountyCd("28");
		rr.addAdditionalAddress(aa);
		byte[] xml = rr.toXMLStream();
		assertNotNull(xml);
		BufferedOutputStream os = new BufferedOutputStream(System.out);
		try {
			os.write(xml);
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

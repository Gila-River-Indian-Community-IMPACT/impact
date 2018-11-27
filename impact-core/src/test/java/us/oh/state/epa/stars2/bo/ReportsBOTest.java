package us.oh.state.epa.stars2.bo;

import java.text.SimpleDateFormat;

import org.junit.Ignore;
import org.junit.Test;
public class ReportsBOTest {
	private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy");
	// TODO: Enable after Reports has latest Stars2 updates (currently missing retrievePEROverdueDetails())
	@Test
	@Ignore
public void testPerOverdueReport() {
//		try {
//			ReportService reportBO = ServiceFactory.getInstance().getReportService();
//			List<PEROverdueDetails> perDetails = reportBO.retrievePEROverdueDetails("31", null);
//			Collections.sort(perDetails,
//					new Comparator<PEROverdueDetails>() {
//						public int compare(PEROverdueDetails a, PEROverdueDetails b) {
//							int retVal = 0;
//							if (a.getPerDueDate() != null) {
//								if (b.getPerDueDate() != null) {
//									retVal = -a.getPerDueDate().compareTo(b.getPerDueDate());
//									if (retVal == 0) {
//										if (a.getFacilityId() != null) {
//											if (b.getFacilityId() != null) {
//												retVal = a.getFacilityId().compareTo(b.getFacilityId());
//												if (retVal == 0) {
//													if (a.getEpaEmuId() != null) {
//														if (b.getEpaEmuId() != null) {
//															retVal = a.getEpaEmuId().compareTo(b.getEpaEmuId());
//														} else {
//															retVal = -1;
//														}
//													} else if (b.getEpaEmuId() != null) {
//														retVal = 1;
//													}
//												}
//											} else {
//												retVal = -1;
//											} 
//										} else if (b.getFacilityId() != null) {
//											retVal = 1;
//										}
//									}
//								} else {
//									retVal = 1;
//								}
//							} else if (b.getPerDueDate() != null) {
//								retVal = -1;
//							}
//							
//	                        return retVal;
//						}
//					}
//					);
//			for (PEROverdueDetails detail : perDetails) {
//				System.out.println(SDF.format(detail.getPerDueDate()) + ",'" + 
//						detail.getFacilityName() + "'," + 
//						detail.getFacilityId()  + "," + 
//						detail.getEpaEmuId()  + ",'" + 
//						detail.getEuDescription() + "'");
//			}
//		} catch (ServiceFactoryException e) {
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
}
	

}

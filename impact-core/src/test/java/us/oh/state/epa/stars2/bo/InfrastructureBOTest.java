package us.oh.state.epa.stars2.bo;

import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dao.DAOFactory;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

/**
 * Runs a junit test suite for the {@link PermitBO} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class InfrastructureBOTest {

    /**
     * 
     */

	
	// TODO: Prevent from referencing physical files and database
	@Test
	@Ignore
    public final void testKeywordSearch() {
    	
        try {
//        	File resFile1 = new File("C:\\work\\erica1.txt");
//        	String query1 = "\"storage pile\" OR \"paved road\"";
//        	File resFile2 = new File("C:\\work\\jenny2b.txt");
//        	String query2 = "\"21-07 Applicable Rules/Requirements\"~300";
//        	String query2 = "\"21-07\"";
//        	File resFile4 = new File("C:\\work\\mikeQuery4.txt");
//        	String query4 = "\"is less than ten tons per year\"";
        	File resFile5 = new File("C:\\work\\jenny5redo.txt");
//        	File resFile5b = new File("C:\\work\\jenny5b.txt");
//        	File resFile5c = new File("C:\\work\\jenny5c.txt");
//        	File resFile5d = new File("C:\\work\\jenny5d.txt");
        	String query5 = "\"3745-21-09 U\"";
//        	String query5b = "\"3745-21-09 U\" NOT \"3745-21-09 U 2\"";
//        	String query5c = "\"21\\-09 \\(U\\)\"";
        	
			InfrastructureService infBO = ServiceFactory.getInstance().getInfrastructureService();
			System.out.println("Search query = '" + query5 + "'");
			// Pelayo : Compiling problems
			//Document[] searchResults = infBO.keyWordSearch(query5);
			//System.out.println(searchResults.length + " documents retrieved");
			
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(resFile5))));
			System.out.println("Facility Name|Facility ID|County|Permit Type|Permit Number|Issuance Date|");
			String sql = 
"SELECT DISTINCT ff.facility_nm, pt.facility_id, cd.county_nm, ptd.permit_type_dsc, pt.permit_nbr, " +
"to_char(pi.issuance_date, 'MM/DD/YYYY')\n" +
"FROM stars2.PT_PERMIT pt\n" +
"INNER JOIN Stars2.PT_PERMIT_ISSUANCE pi\n" +
"      ON (pt.permit_id = pi.permit_id AND pi.issuance_type_cd = 'F' AND pi.issuance_status_cd = 'I')\n" +
"INNER JOIN Stars2.PT_PERMIT_DOCUMENT ppd\n" +
"      ON (ppd.PERMIT_ID = pt.PERMIT_ID AND ppd.PERMIT_DOC_TYPE_CD = 'D'\n" +
"          AND (PPD.ISSUANCE_STAGE_FLAG = 'F'))\n" +
"INNER JOIN Stars2.DC_DOCUMENT dd\n" +
"      ON (dd.DOCUMENT_ID = ppd.DOCUMENT_ID)\n" +
"INNER JOIN Stars2.FP_FACILITY ff\n" +
"      ON (ff.facility_id = pt.facility_id AND ff.version_id = -1)\n" +
"INNER JOIN Stars2.CM_COUNTY_DEF cd\n" +
"      ON (cd.county_cd = substr(ff.facility_id, 3, 2))\n" +
"INNER JOIN Stars2.PT_PERMIT_TYPE_DEF ptd\n" +
"          ON (pt.permit_type_cd = ptd.permit_type_cd)\n" +
"WHERE dd.document_id IN (DOC_ID_LIST)\n" +
"and (pt.facility_id like ('0204%') or pt.facility_id like ('1318%') or pt.facility_id like ('0228%') " +
"or pt.facility_id like ( '0243%') or pt.facility_id like ('0247%') or pt.facility_id like ('1652%') " +
"or pt.facility_id like ('1667%') or pt.facility_id like ('1677%'))" +
"ORDER BY facility_id, permit_nbr\n";
//			InfrastructureDAO iDAO = (InfrastructureDAO)DAOFactory.getDAO("InfrastructureDAO");
			int i = 0;
			String docIdList = "";
/*
			for (Document doc : searchResults) {
				if (i < 30) {
					docIdList += doc.getDocumentID() + ",";
					i++;
				} else {
					docIdList = docIdList.substring(0, docIdList.length()-1);
					// Pelayo : Compiling problems
					// iDAO.writeQueryToStdOut(sql.replace("DOC_ID_LIST", docIdList));
					i = 0;
					docIdList = "";
				}
			}
			if (i > 0) {
				docIdList = docIdList.substring(0, docIdList.length()-1);
				iDAO.writeQueryToStdOut(sql.replace("DOC_ID_LIST", docIdList));
			}

*/
		} catch (ServiceFactoryException e) {
			e.printStackTrace();
            fail("InfrastructureBOTest.testKeywordSearch() threw an unexpected exception. "
                    + e.getMessage());
//		} catch (RemoteException e) {
//			e.printStackTrace();
//            fail("InfrastructureBOTest.testKeywordSearch() threw an unexpected exception. "
//                   + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
            fail("InfrastructureBOTest.testKeywordSearch() threw an unexpected exception. "
                    + e.getMessage());
		}
   	
    }
   
//    public final void testPostToRevenues(){
//
//    	try{
//
//            PortalService portalService = ServiceFactory.getInstance().getPortalServiceService();
//            User user = portalService.authenticate("lours", "lours", "lours", "lours", "lours");
//
//            InfrastructureService infBO = ServiceFactory.getInstance().getInfrastructureService();
//    		
//            Invoice inv = new Invoice();
//            inv.setEmissionsRptId(12345);
//            inv.setRevenueTypeCd("TVE06");
//            inv.setOrigAmount(100.0);            
//            
//            Timestamp effectiveDate = new Timestamp(Calendar.getInstance().getTimeInMillis());                       
//            
//          	infBO.preparePostToRevenue(user, inv, effectiveDate, false);
//            
//    	}
//        catch(Exception e){
//
//            e.printStackTrace();
//            fail("InfrastructureBOTest.preparePostToRevenues() threw an unexpected exception. "
//                 + e.getMessage());
//    	}
//    }

    /**
     * This method is called before a test is executed and is used to setup any
     * needed preconditions for the test.
     */
    protected void setUp() {
    }

    /**
     * This method is called after a test is executed and is used to cleanup
     * after the test.
     */
    protected void tearDown() {
    }

    /**
     * Gathers up the individual test classes into a test suite. Add your test
     * class here.
     */
//    public static Test suite() {
//
//        TestSuite suite = new TestSuite("InfrastructureBO Test Suite");
//
//        suite.addTestSuite(us.oh.state.epa.stars2.bo.InfrastructureBOTest.class);
//
//        return suite;
//    }

}

            /*
            for (NoticeType type : noticeTypeArr) {
                Office office = type.getOffice();
                if (office == null) {
                    System.out.println("NOTICETYPE: ");
                    System.out.println("  Label       = " + type.getLabel());
                    System.out.println("  Description = " + type.getDescription());
                    System.out.println("  Comments    = " + type.getComments());
                    System.out.println("  Value       = " + type.getValue());
                    System.out.println("  OID         = " + type.getOID().longValue());
                    //System.out.println("  Office Label        = " + office.getLabel());
                    //System.out.println("  Office Name         = " + office.getName());
                    //System.out.println("  Office Abbreviation = " + office.getAbbreviation());
                    //System.out.println("  Office Value        = " + office.getValue());
                    //System.out.println("  Office OID          = " + type.getOID().longValue());
                }
            }
            */
            /*
            for (NoticeRemark remark : remarkArr) {
                System.out.println("REMARK:");
                System.out.println("  Label       = " + remark.getLabel());
                System.out.println("  Description = " + remark.getDescription());
                System.out.println("  Value       = " + remark.getValue());
                System.out.println("  OID         = " + remark.getOID().longValue());
            }
            */


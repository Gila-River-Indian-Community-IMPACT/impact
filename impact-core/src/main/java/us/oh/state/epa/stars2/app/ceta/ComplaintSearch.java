package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import us.oh.state.epa.stars2.bo.ComplaintsService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Complaint;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

@SuppressWarnings("serial")
public class ComplaintSearch extends AppBase {
	private String doLaaCd;
	private List<Complaint> complaints;
	private boolean hasSearchResults;

    private ComplaintsService complaintsService;

    
    public ComplaintsService getComplaintsService() {
		return complaintsService;
	}

	public void setComplaintsService(ComplaintsService complaintsService) {
		this.complaintsService = complaintsService;
	}

	public String initComplaint() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH) + 1;
        if(mon == 1) {
            mon = 12;
            y = y -1;
        } else {
            mon = mon - 1;
        }
        ComplaintDetail complaintD = (ComplaintDetail)FacesUtil.getManagedBean("complaintDetail");
        complaintD.setYear(Integer.toString(y));
        String m = Integer.toString(mon);
        if(m.length() == 1) m = "0" + m;
        complaintD.setMonth(m);
        return "complaints.search";
    }
    
    public final String reset() {
    	doLaaCd = null;
    	hasSearchResults = false;
    	return SUCCESS;
    }
    
    /*
     * Code to read in an XML submitted file in order to debug
     * problems that actually occurred that we could not reproduce
     * locally.
     */
//    public final String testSubmit() {
//    	byte[] xmlArray = new byte[200000];
//    	File f = new File("C:\\projects\\attachments\\xml.txt");
//    	try {
//    	FileInputStream fis = new FileInputStream(f);
//    	fis.read(xmlArray);
//    	} catch(FileNotFoundException fnfe) {
//    		logger.error("file not found", fnfe);
//    	} catch(IOException ioe) {
//    		logger.error("file not found", ioe);
//    	}
//    	
//    	try {
//            InfrastructureService infraBO = ServiceFactory.getInstance().getInfrastructureService();
//            infraBO.processSubmittedTask(xmlArray);
//        }
//        catch (ServiceFactoryException sfe) {
//            String error = "Caught a ServiceFactoryException: message = " + sfe.getMessage();
//            logger.error(error, sfe);
//        }
//        catch (DAOException daoe) {
//            String error = "Caught an DAOEException: message = " + daoe.getMessage();
//            logger.error(error, daoe);
//        }
//        catch (RemoteException re) {
//            String error = "Caught an RemoteException: message = " + re.getMessage();
//            logger.error(error, re);
//        }
//        catch (Exception e) {
//            String error = "Caught an Exception of type " + e.getClass().getName() 
//                + ": message = " + e.getMessage();
//            logger.error(error, e);
//        }
//    	return null;
//    }
	
	/**
	 * Invoked from Complaint search screen.
	 * @return
	 */
	public final String search() {
		try {
            hasSearchResults = false;
            complaints = getComplaintsService().retrieveComplaints(doLaaCd);
            DisplayUtil.displayHitLimit(complaints.size());
            if (complaints.size() == 0) {
            	DisplayUtil.displayInfo("There are no complaints for the selected DO/LAA.");
            } else {
            	// want results sorted in reverse order
            	Collections.sort(complaints);
            	Collections.reverse(complaints);
            }
            hasSearchResults = true;
		} catch (RemoteException e) {
			handleException(e);
		}
		return "complaints.search";
	}

    public final String refresh() {
        String ret = "complaints.search";
        search();
        return ret;
    }

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final List<Complaint> getComplaints() {
		return complaints;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}
}

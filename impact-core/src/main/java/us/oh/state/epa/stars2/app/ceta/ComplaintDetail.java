package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.ComplaintsService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage.Severity;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Complaint;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class ComplaintDetail extends ValidationBase {
	private String doLaaCd;
	private Integer complaintId;
	private Complaint complaint;
	private List<Complaint> complaintsForDoLaa;
    
    private String year;
    private String month;
	
    public boolean editMode = false;  // true means can make changes.
	
    private ComplaintsService complaintsService;

	public final void reset() {
		complaint = null;
	}
	
	public ComplaintsService getComplaintsService() {
		return complaintsService;
	}

	public void setComplaintsService(ComplaintsService complaintsService) {
		this.complaintsService = complaintsService;
	}

	public final void setComplaintId(Integer complaintId) {
		this.complaintId = complaintId;
	}
	
	public final Integer getComplaintId() {
		return complaintId;
	}
	
    public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}
	
	public final Complaint getComplaint() {
		return this.complaint;
	}	
	
	public final String startNewComplaint() {
		complaintId = null;
		complaint = new Complaint();
		complaint.setDoLaaCd(doLaaCd);
        complaint.setYear(year);
        complaint.setMonth(month);
        editMode = true;
		return "dialog:complaintDetail";
	}

	public final String startEditComplaint() {
		String ret = "dialog:complaintDetail";
		try {
			if (doLaaCd != null) {
				this.complaintsForDoLaa = getComplaintsService().retrieveComplaints(doLaaCd);
				for (Complaint match : complaintsForDoLaa) {
					if (match.getComplaintId().equals(complaintId)) {
						complaint = match;
					}
				}
			}
			if (complaint == null) {
				throw new RemoteException("No complaint found for doLaa: " + doLaaCd +
						" with complaintId: " + complaintId);
			}
		} catch (RemoteException e) {
			ret = null;
			handleException(e);
		}
		return ret;
	}
    
    public final void enterEditComplaint(ActionEvent actionEvent) {
        editMode = true;
    }
	
	public final void applyEditComplaint(ActionEvent actionEvent) {
		try {
			if (complaint != null) {
	            List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
				if (!complaint.isValid()) {
					for (ValidationMessage vm : complaint.getValidationMessages().values()) {
						validationMessages.add(vm);
					}
				}
				if (complaint.getComplaintId() == null) {
					Complaint match = getComplaintsService().retrieveComplaint(doLaaCd, complaint.getYear(), complaint.getMonth());
					if (match != null) {
						validationMessages.add(new ValidationMessage("Year", 
								"You may not create a new complaint for a year/month for which a complaint already exists",
								Severity.ERROR, "year"));
					}
				}
				if (validationMessages.size() == 0) {
					if (complaint.getComplaintId() == null) {
						getComplaintsService().createComplaint(complaint);
                        editMode = false;
                        year = complaint.getYear();
                        month = complaint.getMonth();
                        // up the date by one month
                        int y = Integer.parseInt(year);
                        int m = Integer.parseInt(month);
                        m++;
                        if(m == 13) {
                            m = 1;
                            y++;
                        }
                        year = Integer.toString(y);
                        month = Integer.toString(m);
                        if(month.length() == 1) month = "0" + month;
					} else {
						getComplaintsService().modifyComplaint(complaint);
                        editMode = false;
					}
					ComplaintSearch cs = (ComplaintSearch)FacesUtil.getManagedBean("complaintSearch");
					cs.search();
					FacesUtil.returnFromDialogAndRefresh();
				} else {
	                displayValidationMessages("", validationMessages.toArray(new ValidationMessage[0]));
				}
			}
		} catch (RemoteException e) {
			handleException(e);
		}
	}
	
	public final void applyRemoveComplaint(ActionEvent actionEvent) {
		try {
			if (complaint != null && complaint.getComplaintId() != null) {
				getComplaintsService().removeComplaint(complaint);
				complaint = null;
				complaintId = null;
				ComplaintSearch cs = (ComplaintSearch)FacesUtil.getManagedBean("complaintSearch");
				cs.search();
			}
			FacesUtil.returnFromDialogAndRefresh();
		} catch (RemoteException e) {
			handleException(e);
		}
	}	
	
	public final void cancelEditComplaint(ActionEvent actionEvent) {
		// clear out complaint data
		complaint = null;
		complaintId = null;
        editMode = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isEditMode() {
        return editMode;
    }
}

package us.wy.state.deq.impact.webcommon.company;

import java.rmi.RemoteException;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

@SuppressWarnings("serial")
public class CreateCompany extends AppBase {
	private Company company;

	private CompanyService companyService;
	
	private CompanyProfileBase companyProfile;
	
	private SimpleMenuItem menuItemCompanyProfile;

	public CreateCompany() {
		resetCreateCompany();
	}

	public SimpleMenuItem getMenuItemCompanyProfile() {
		return menuItemCompanyProfile;
	}

	public void setMenuItemCompanyProfile(SimpleMenuItem menuItemCompanyProfile) {
		this.menuItemCompanyProfile = menuItemCompanyProfile;
	}

	public CompanyProfileBase getCompanyProfile() {
		return companyProfile;
	}

	public void setCompanyProfile(CompanyProfileBase companyProfile) {
		this.companyProfile = companyProfile;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public final void resetCreateCompany() {
		company = new Company();
	}
	
	public final void resetCreateCompany(ActionEvent actionEvent) {
		resetCreateCompany();
	}
	
    public final String submitCreateCompany() {
        ValidationMessage[] validationMessages;
        try {
            validationMessages = getCompanyService().validateCompany(company);
            if (displayValidationMessages("createCompany:", validationMessages)) {
                return FAIL;
            }
            
            Company createdCompany = getCompanyService().createCompany(company);
            if (createdCompany == null) {
        		DisplayUtil.displayError("Create Company failed.");
        		return FAIL;
        	} else {
        		company = createdCompany;
        	}
            
            companyProfile.setCompanyId(company.getCompanyId());
            companyProfile.setCmpId(company.getCmpId());
        } catch (RemoteException re) {
        	handleException(re);
            DisplayUtil.displayError("Create Company failed");
            return FAIL;
        }
        
        DisplayUtil.displayInfo("company created successfully");        
        
		menuItemCompanyProfile.setDisabled(false);
        
        return "companyProfile";
    }

    public final void setCompany(Company company) {
        this.company = company;
    }
    
    public final Company getCompany() {
        return this.company;
    }
}

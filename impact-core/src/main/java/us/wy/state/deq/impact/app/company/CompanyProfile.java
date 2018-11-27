package us.wy.state.deq.impact.app.company;

import java.rmi.RemoteException;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.tree2.TreeNode;

import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.webcommon.company.CompanyProfileBase;

public class CompanyProfile extends CompanyProfileBase {

	private static final long serialVersionUID = 4966334482902613449L;

	private String mergeSourceCmpId;
	private String mergeDestinationCmpId;
	private boolean mergeEditable;
	private TableSorter sourceCmpFacWrapper;
	private TableSorter sourceCmpContactsWrapper;
	private Company sourceCompany;
	private Company destinationCompany;
	private boolean companiesLoading;

	public CompanyProfile() {
		super();
	}

	public final String refreshCompanyProfile() {
		if (companyId != null) {
			TreeNode tempSelectedTreeNode = selectedTreeNode;
			String tempCurrent = current;
			refreshCompany();
			selectedTreeNode = tempSelectedTreeNode;
			current = tempCurrent;
		}

		return "companyProfile";

	}
	

	public SelectItem[] getCompanyFacilitySelectItems() {
		FacilityList[] facilities = getCompany().getFacilities();
		SelectItem[] items = new SelectItem[facilities.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = new SelectItem(facilities[i].getFacilityId(),
					facilities[i].getName());
		}
		return items;
	}

	public boolean isCompanyMergeable() {
		boolean ret = false;

		ret = company != null
				&& (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin())
				&& isInternalApp() && !getEditable();

		return ret;
	}

	public final String mergeCompany() {
		setMergeEditable(true);
		setMergeSourceCmpId(null);

		return "dialog:mergeCompany";
	}

	public String getMergeSourceCmpId() {
		return mergeSourceCmpId;
	}

	public void setMergeSourceCmpId(String mergeSourceCmpId) {
		if (!Utility.isNullOrEmpty(mergeSourceCmpId)) {
			String format = "CMP%06d";
			int tempId;
			try {
				tempId = Integer.parseInt(mergeSourceCmpId.trim());
				mergeSourceCmpId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}

			try {
				sourceCompany = getCompanyService().retrieveCompanyProfile(
						mergeSourceCmpId);

				if (sourceCompany != null) {
					getSourceCmpFacWrapper().setWrappedData(
							sourceCompany.getFacilities());
					getSourceCmpContactsWrapper().setWrappedData(
							sourceCompany.getContacts());
				}

			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing company failed");
			}
		} else {
			sourceCompany = null;
		}

		this.mergeSourceCmpId = mergeSourceCmpId;
	}

	public String getMergeDestinationCmpId() {
		return mergeDestinationCmpId;
	}

	public void setMergeDestinationCmpId(String mergeDestinationCmpId) {
		if (!Utility.isNullOrEmpty(mergeDestinationCmpId)) {
			String format = "CMP%06d";
			int tempId;
			try {
				tempId = Integer.parseInt(mergeDestinationCmpId.trim());
				mergeDestinationCmpId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}

			try {
				destinationCompany = getCompanyService()
						.retrieveCompanyProfile(mergeDestinationCmpId);
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing company failed");
			}
		} else {
			destinationCompany = null;
		}

		this.mergeDestinationCmpId = mergeDestinationCmpId;
	}

	public boolean isMergeEditable() {
		return mergeEditable;
	}

	public void setMergeEditable(boolean mergeEditable) {
		this.mergeEditable = mergeEditable;
	}

	public Company getSourceCompany() {
		return sourceCompany;
	}

	public void setSourceCompany(Company sourceCompany) {
		this.sourceCompany = sourceCompany;
	}

	public Company getDestinationCompany() {
		return destinationCompany;
	}

	public void setDestinationCompany(Company destinationCompany) {
		this.destinationCompany = destinationCompany;
	}

	public TableSorter getSourceCmpFacWrapper() {
		if (sourceCmpFacWrapper == null) {
			sourceCmpFacWrapper = new TableSorter();
		}

		return sourceCmpFacWrapper;
	}

	public void setSourceCmpFacWrapper(TableSorter sourceCmpFacWrapper) {
		this.sourceCmpFacWrapper = sourceCmpFacWrapper;
	}

	public TableSorter getSourceCmpContactsWrapper() {
		if (sourceCmpContactsWrapper == null) {
			sourceCmpContactsWrapper = new TableSorter();
		}

		return sourceCmpContactsWrapper;
	}

	public void setSourceCmpContactsWrapper(TableSorter sourceCmpContactsWrapper) {
		this.sourceCmpContactsWrapper = sourceCmpContactsWrapper;
	}

	public String beginCompanyMerge() {
		setMergeEditable(false);

		return null;
	}

	public String stopCompanyMerge() {
		setMergeEditable(true);

		return null;
	}

	public String mergeCompanies() {
		boolean isOperationOk = true;

		// load companies
		setMergeSourceCmpId(this.mergeSourceCmpId);
		setMergeDestinationCmpId(this.mergeDestinationCmpId);

		isOperationOk = validateMergeCompanies(this.mergeSourceCmpId,
				this.sourceCompany, this.mergeDestinationCmpId,
				this.destinationCompany);

		if (isOperationOk) {
			try {
				getCompanyService().mergeCompanies(this.sourceCompany,
						this.destinationCompany);
			} catch (DAOException e) {
				logger.error("An exception was thrown during the company merge process between: Source Company ("
						+ this.sourceCompany.getCmpId()
						+ ") and Destination Company ("
						+ this.destinationCompany.getCmpId()
						+ ")"
						+ " Exception: " + e.getMessage());
				DisplayUtil
						.displayError("An error occurred during the company merge process. Please try again.");
				isOperationOk = false;
			}
		}

		if (isOperationOk) {
			DisplayUtil.displayInfo("Source company " + this.mergeSourceCmpId
					+ " was successfully merged into destination company "
					+ this.mergeDestinationCmpId);
			setCompanyId(this.destinationCompany.getCompanyId());
			setCmpId(this.destinationCompany.getCmpId());
			submitProfile();

			closeDialog();
		}

		return null;
	}

	private boolean validateMergeCompanies(String mergeSourceCmpId,
			Company sourceCompany, String mergeDestinationCmpId,
			Company destinationCompany) {
		boolean ret = true;
		if (sourceCompany == null) {
			ret = false;
			if (!Utility.isNullOrEmpty(mergeSourceCmpId)) {
				DisplayUtil
						.displayError("No source company could be found with the given company id: "
								+ mergeSourceCmpId);
			} else {
				DisplayUtil.displayError("Source company id is missing.");
			}
		}

		if (destinationCompany == null) {
			ret = false;
			if (!Utility.isNullOrEmpty(mergeDestinationCmpId)) {
				DisplayUtil
						.displayError("No destination company could be found with the given company id: "
								+ mergeDestinationCmpId);
			} else {
				DisplayUtil.displayError("Destination company id is missing.");
			}
		}

		if (sourceCompany != null && destinationCompany != null) {
			if (sourceCompany.getCompanyId().equals(
					destinationCompany.getCompanyId())) {
				ret = false;
				DisplayUtil.displayError("Cannot merge the same company.");
			}
		}

		return ret;
	}
	
	public final String refreshCompanyOffsetTracking() {
		return "companyOffsetTracking";

	}
}

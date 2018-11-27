package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;

@SuppressWarnings("serial")
public class ApplicationEU extends BaseDB {
	private Integer applicationEuId;
	private Integer applicationId;
	private boolean excluded;
	private boolean validated;
	private EmissionUnit fpEU;
	private List<ApplicationDocumentRef> euDocuments;
	private boolean notIncludable;
	private String euText;

	public ApplicationEU() {
		super();
	}

	/**
	 * @param old
	 */
	public ApplicationEU(ApplicationEU old) {
		super(old);
		if (old != null) {
			setApplicationEuId(old.getApplicationEuId());
			setApplicationId(old.getApplicationId());
			setEuDocuments(old.getEuDocuments());
			setFpEU(old.getFpEU());
			setExcluded(old.isExcluded());
			setNotIncludable(old.isNotIncludable());
			setValidated(old.getValidated());
			setEuText(old.getEuText());
		}
	}

	public void populate(java.sql.ResultSet rs) {
		try {
			setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
			setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
			setExcluded(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("excluded_flag")));
			setNotIncludable(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("not_includable_flag")));
			setValidated(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("validated_flag")));
			setEuText(rs.getString("eu_text"));
			setLastModified(AbstractDAO.getInteger(rs, "pe_lm"));

			fpEU = new EmissionUnit();

			fpEU.populate(rs);
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	public final Integer getApplicationEuId() {
		return applicationEuId;
	}

	public final void setApplicationEuId(Integer applicationEUID) {
		this.applicationEuId = applicationEUID;
	}

	public final EmissionUnit getFpEU() {
		return fpEU;
	}

	public final void setFpEU(EmissionUnit fpEU) {
		this.fpEU = fpEU;
	}

	public final boolean getValidated() {
		return validated;
	}

	public final void setValidated(boolean validated) {
		this.validated = validated;
	}
	
	public final List<ApplicationDocumentRef> getEuDocuments() {
		Comparator<ApplicationDocumentRef> comparator = generateAppDocRefComparator();

		if (euDocuments == null) {
			euDocuments = new ArrayList<ApplicationDocumentRef>();
		}

		Collections.sort(euDocuments, comparator);

		return euDocuments;
	}

	public final void setEuDocuments(List<ApplicationDocumentRef> euDocuments) {
		this.euDocuments = new ArrayList<ApplicationDocumentRef>();
		if (euDocuments != null) {
			this.euDocuments.addAll(euDocuments);
		}
	}

	public final Integer getApplicationId() {
		return applicationId;
	}

	public final void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((applicationEuId == null) ? 0 : applicationEuId.hashCode());
		result = prime * result
				+ ((applicationId == null) ? 0 : applicationId.hashCode());
		result = prime * result + ((euText == null) ? 0 : euText.hashCode());
		result = prime * result + (excluded ? 1231 : 1237);
		result = prime * result + (notIncludable ? 1231 : 1237);
		result = prime * result + (validated ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ApplicationEU other = (ApplicationEU) obj;
		if (applicationEuId == null) {
			if (other.applicationEuId != null)
				return false;
		} else if (!applicationEuId.equals(other.applicationEuId))
			return false;
		if (applicationId == null) {
			if (other.applicationId != null)
				return false;
		} else if (!applicationId.equals(other.applicationId))
			return false;
		if (euText == null) {
			if (other.euText != null)
				return false;
		} else if (!euText.equals(other.euText))
			return false;
		if (excluded != other.excluded)
			return false;
		if (notIncludable != other.notIncludable)
			return false;
		if (validated != other.validated)
			return false;
		return true;
	}

	public final boolean isExcluded() {
		return excluded;
	}

	public final void setExcluded(boolean excluded) {
		this.excluded = excluded;
	}

	public final boolean isNotIncludable() {
		return notIncludable;
	}

	public final void setNotIncludable(boolean notIncludable) {
		this.notIncludable = notIncludable;
		// if EU cannot be included, mark it as excluded
		if (this.notIncludable) {
			this.excluded = true;
		}
	}

	public final String getEuText() {
		return euText;
	}

	public final void setEuText(String euText) {
		this.euText = euText;
	}
	
	private Comparator<ApplicationDocumentRef> generateAppDocRefComparator() {
		return new Comparator<ApplicationDocumentRef>() {
			public int compare(ApplicationDocumentRef s1,
					ApplicationDocumentRef s2) {
				if (s1.isRequiredDoc() && !s2.isRequiredDoc()) {
					return -1;

				} else if (!s1.isRequiredDoc() && s2.isRequiredDoc()) {
					return 1;

				}

				if (s1.getDocumentId() != null && s2.getDocumentId() == null) {
					return 1;
				} else if (s1.getDocumentId() == null
						&& s2.getDocumentId() != null) {
					return -1;
				}

				return s1.getApplicationDocId().compareTo(
						s2.getApplicationDocId());
			}
		};
	}

	@Override
	public String toString() {
		return "ApplicationEU [applicationEuId=" + applicationEuId
				+ ", applicationId=" + applicationId + "]";
	}
	
}

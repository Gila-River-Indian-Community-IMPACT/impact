package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

/**
 * @author Kbradley
 * 
 */
public class SccCode extends BaseDB {

	private static final long serialVersionUID = 938669617719155689L;

	public static final String DUMMY_SCC_ID = "00000000";

	public static final short SCC_LEN = 8;
	private String sccId;
	private String fedSccId;
	private String sccIdL1Cd;
	private String sccIdL2Cd;
	private String sccIdL3Cd;
	private String sccIdL4Cd;
	private String sccLevel1Desc;
	private String sccLevel2Desc;
	private String sccLevel3Desc;
	private String sccLevel4Desc;
	private String category;
	private String inActive;;
	private String euCapacityTypeCd;
	private Integer createdYear;
	private Integer deprecatedYear;
	private boolean deprecated;
	private transient String createDeprecatePhrase;
	private transient String createDeprecateInfo = "";
	private Integer epaUnitTypeCd;

	public SccCode() {
		requiredFields();
	}

	public SccCode(SccCode old) {
		super(old);

		if (old != null) {
		}
		requiredFields();
	}

	private void requiredFields() {
		requiredField(sccId, "sccId", "Source Classification Code (SCC)");
	}

	public void copySccCode(SccCode scc) {
		setSccId(new String(scc.getSccId()));
	}

	public boolean determineDeprecated(int year) {

		deprecated = false;
		if (null != createdYear && year < createdYear.intValue()) {
			deprecated = true;
		}
		if (null != deprecatedYear && year >= deprecatedYear.intValue()) {
			deprecated = true;
		}
		return deprecated;
	}

	// Standard way of returning the scc value contained.
	public final static String getIdValue(SccCode scc) {

		if (null == scc)
			return null;
		if (null == scc.getSccId() || 0 == scc.getSccId().length())
			return null;
		return scc.getSccId();
	}

	// Standard way of comparing two SccCode values.
	public final static boolean comparIdValues(SccCode scc1, SccCode scc2) {

		String s1 = getIdValue(scc1);
		String s2 = getIdValue(scc2);
		if (null == s1 && null == s2)
			return true;
		if (null == s1 && null != s2 || null != s1 && null == s2)
			return false;
		if (s1.equals(s2))
			return true;
		return false;
	}

	/**
	 * @return
	 */
	public final String getSccIdL1Cd() {
		return sccIdL1Cd;
	}

	/**
	 * @param sccIdL1Cd
	 */
	public final void setSccIdL1Cd(String sccIdL1Cd) {

		setSccLevel1Desc(sccIdL1Cd);

		if (sccIdL1Cd != null) {
			int l1CdIndex = sccIdL1Cd.indexOf(":");
			if (l1CdIndex >= 0) {
				sccId = sccIdL1Cd.substring(0, l1CdIndex);
				this.sccIdL1Cd = sccIdL1Cd.substring(0, l1CdIndex);
			}
		}

	}

	/**
	 * @return
	 */
	public final String getSccIdL2Cd() {
		return sccIdL2Cd;
	}

	/**
	 * @param sccIdL2Cd
	 */
	public final void setSccIdL2Cd(String sccIdL2Cd) {

		setSccLevel2Desc(sccIdL2Cd);

		if (sccIdL2Cd != null) {
			int l2CdIndex = sccIdL2Cd.indexOf(":");
			if (sccIdL1Cd != null && l2CdIndex >= 0) {
				sccId = sccIdL1Cd + sccIdL2Cd.substring(0, l2CdIndex);
				this.sccIdL2Cd = sccIdL2Cd.substring(0, l2CdIndex);
			}
		}

	}

	/**
	 * @return
	 */
	public final String getSccIdL3Cd() {
		return sccIdL3Cd;
	}

	/**
	 * @param sccIdL3Cd
	 */
	public final void setSccIdL3Cd(String sccIdL3Cd) {

		setSccLevel3Desc(sccIdL3Cd);

		if (sccIdL3Cd != null) {
			int l3CdIndex = sccIdL3Cd.indexOf(":");
			if (sccIdL1Cd != null && sccIdL2Cd != null && l3CdIndex >= 0) {
				sccId = sccIdL1Cd + sccIdL2Cd + sccIdL3Cd.substring(0, l3CdIndex);
				this.sccIdL3Cd = sccIdL3Cd.substring(0, l3CdIndex);
			}
		}

	}

	/**
	 * @return
	 */
	public final String getSccIdL4Cd() {
		return sccIdL4Cd;
	}

	/**
	 * @param sccIdL4Cd
	 */
	public final void setSccIdL4Cd(String sccIdL4Cd) {

		setSccLevel4Desc(sccIdL4Cd);

		if (sccIdL4Cd != null) {
			int l4CdIndex = sccIdL4Cd.indexOf(":");
			if (sccIdL1Cd != null && sccIdL2Cd != null && sccIdL3Cd != null && l4CdIndex >= 0) {
				sccId = sccIdL1Cd + sccIdL2Cd + sccIdL3Cd + sccIdL4Cd.substring(0, l4CdIndex);
				this.sccIdL4Cd = sccIdL4Cd.substring(0, l4CdIndex);
			}
		}

	}

	public final String getSccId() {
		return sccId;
	}

	/**
	 * @param sccId
	 */
	public final void setSccId(String sccId) {
		//requiredField(sccId, "sccId", "Source Classification Code (SCC)");
		this.sccId = sccId;
		if (sccId != null && (!sccId.equals(""))) {
			this.sccId = sccId.replaceAll("-", "");
			sccIdL1Cd = this.sccId.substring(0, 1);
			sccIdL2Cd = this.sccId.substring(1, 3);
			sccIdL3Cd = this.sccId.substring(3, 6);
			sccIdL4Cd = this.sccId.substring(6);
		} else {
			this.sccId = sccId;
		}
	}

	public final String getSccLevel1Desc() {
		return sccLevel1Desc;
	}

	public final void setSccLevel1Desc(String sccLevel1Desc) {
		this.sccLevel1Desc = sccLevel1Desc;
	}

	public final String getSccLevel2Desc() {
		return sccLevel2Desc;
	}

	public final void setSccLevel2Desc(String sccLevel2Desc) {
		this.sccLevel2Desc = sccLevel2Desc;
	}

	public final String getSccLevel3Desc() {
		return sccLevel3Desc;
	}

	public final void setSccLevel3Desc(String sccLevel3Desc) {
		this.sccLevel3Desc = sccLevel3Desc;
	}

	public final String getSccLevel4Desc() {
		return sccLevel4Desc;
	}

	public final void setSccLevel4Desc(String sccLevel4Desc) {
		this.sccLevel4Desc = sccLevel4Desc;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getInActive() {
		return inActive;
	}

	public void setInActive(String inActive) {
		this.inActive = inActive;
	}

	public Integer getEpaUnitTypeCd() {
		return epaUnitTypeCd;
	}

	public void setEpaUnitTypeCd(Integer epaUnitTypeCd) {
		this.epaUnitTypeCd = epaUnitTypeCd;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public final void populate(ResultSet rs) {

		try {
			setSccId(rs.getString("scc_id"));
			this.setFedSccId(rs.getString("fed_scc_id"));
			setSccIdL1Cd(rs.getString("level1_dsc"));
			setSccIdL2Cd(rs.getString("level2_dsc"));
			setSccIdL3Cd(rs.getString("level3_dsc"));
			setSccIdL4Cd(rs.getString("level4_dsc"));
			// setSccLevel1Desc(rs.getString("level1_dsc"));
			// setSccLevel2Desc(rs.getString("level2_dsc"));
			// setSccLevel3Desc(rs.getString("level3_dsc"));
			// setSccLevel4Desc(rs.getString("level4_dsc"));
			setCategory(rs.getString("category"));
			setEpaUnitTypeCd(AbstractDAO.getInteger(rs, "epa_unit_type_cd"));
			setInActive(rs.getString("InActive"));
			setCreatedYear(AbstractDAO.getInteger(rs, "created"));
			// Determine phrase about create/deprecate
			// SccCode s = this;
			// s.setCreateDeprecatePhrase("");
			// if(s.getDeprecatedYear() == null && s.getCreatedYear() != null) {
			// s.setCreateDeprecatePhrase(" -- Can be used in emissions
			// reporting beginning " + s.getCreatedYear() + ".");
			// } else if(s.getDeprecatedYear() != null && s.getCreatedYear() ==
			// null) {
			// s.setCreateDeprecatePhrase(" -- No longer valid. Can only be used
			// in emissions reporting year " + (s.getDeprecatedYear() - 1) + "
			// or before.");
			// } else if (s.getDeprecatedYear() != null && s.getCreatedYear() !=
			// null) {
			// if(s.getCreatedYear().intValue() <=
			// s.getDeprecatedYear().intValue()) {
			// s.setCreateDeprecatePhrase(" -- Is not valid for use.");
			// } else {
			// s.setCreateDeprecatePhrase(" -- No longer valid. Can only be used
			// in emissions reporting years " + s.getCreatedYear() + " thru " +
			// (s.getDeprecatedYear() - 1) +".");
			// }
			// }
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	public final void clearInputSccValidMessages() {
		requiredField(sccId, "sccId", "Source Classification Code (SCC)");
		validationMessages.remove("sccIdL1");
		validationMessages.remove("sccIdL2");
		validationMessages.remove("sccIdL3");
		validationMessages.remove("sccIdL4");
	}

	public final void clearLevelSccValidMessages() {
		validationMessages.remove("sccId");
		requiredField(sccIdL1Cd, "sccIdL1", "SCC Level 1 Description");
		requiredField(sccIdL2Cd, "sccIdL2", "SCC Level 2 Description");
		requiredField(sccIdL3Cd, "sccIdL3", "SCC Level 3 Description");
		requiredField(sccIdL4Cd, "sccIdL4", "SCC Level 4 Description");
	}

	public final void clearSearchSccValidMessages() {
		clearInputSccValidMessages();
		validationMessages.remove("sccId");
	}

	public Integer getCreatedYear() {
		return createdYear;
	}

	public void setCreatedYear(Integer createdYear) {
		this.createdYear = createdYear;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public Integer getDeprecatedYear() {
		return deprecatedYear;
	}

	public void setDeprecatedYear(Integer deprecatedYear) {
		this.deprecatedYear = deprecatedYear;
	}

	public final boolean validate(String processId, String epaEmuId) {
		clearValidationMessages();

		if (sccId == null || sccId.equals("")) {
			validationMessages.put("sccId",
					new ValidationMessage("sccId",
							"Emission Process [" + processId
									+ "]: Attribute Source Classification Code (SCC) is not set.",
							ValidationMessage.Severity.ERROR, "emissionProcess:" + processId, epaEmuId));
			return false;
		}
		return true;
	}

	public String getEuCapacityTypeCd() {
		return euCapacityTypeCd;
	}

	public void setEuCapacityTypeCd(String euCapacityTypeCd) {
		this.euCapacityTypeCd = euCapacityTypeCd;
	}

	public String getCreateDeprecatePhrase() {
		return createDeprecatePhrase;
	}

	public void setCreateDeprecatePhrase(String createDeprecatePhrase) {
		this.createDeprecatePhrase = createDeprecatePhrase;
	}

	public String getCreateDeprecateInfo() {
		createDeprecateInfo = "";
		if (createdYear != null && deprecatedYear == null) {
			createDeprecateInfo = "The SCC is valid staring in " + createdYear + ".";
		} else if (createdYear == null && deprecatedYear != null) {
			createDeprecateInfo = "the SCC was valid thru " + (deprecatedYear - 1) + ".";
		} else if (createdYear != null && deprecatedYear != null) {
			createDeprecateInfo = "the SCC was valid from " + createdYear + " thru " + (deprecatedYear - 1) + ".";
		}
		return createDeprecateInfo;
	}

	public String getFedSccId() {
		return fedSccId;
	}

	public void setFedSccId(String fedSccId) {
		this.fedSccId = fedSccId;
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((createdYear == null) ? 0 : createdYear.hashCode());
		result = prime * result + (deprecated ? 1231 : 1237);
		result = prime * result + ((deprecatedYear == null) ? 0 : deprecatedYear.hashCode());
		result = prime * result + ((epaUnitTypeCd == null) ? 0 : epaUnitTypeCd.hashCode());
		result = prime * result + ((euCapacityTypeCd == null) ? 0 : euCapacityTypeCd.hashCode());
		result = prime * result + ((fedSccId == null) ? 0 : fedSccId.hashCode());
		result = prime * result + ((inActive == null) ? 0 : inActive.hashCode());
		result = prime * result + ((sccId == null) ? 0 : sccId.hashCode());
		result = prime * result + ((sccIdL1Cd == null) ? 0 : sccIdL1Cd.hashCode());
		result = prime * result + ((sccIdL2Cd == null) ? 0 : sccIdL2Cd.hashCode());
		result = prime * result + ((sccIdL3Cd == null) ? 0 : sccIdL3Cd.hashCode());
		result = prime * result + ((sccIdL4Cd == null) ? 0 : sccIdL4Cd.hashCode());
		result = prime * result + ((sccLevel1Desc == null) ? 0 : sccLevel1Desc.hashCode());
		result = prime * result + ((sccLevel2Desc == null) ? 0 : sccLevel2Desc.hashCode());
		result = prime * result + ((sccLevel3Desc == null) ? 0 : sccLevel3Desc.hashCode());
		result = prime * result + ((sccLevel4Desc == null) ? 0 : sccLevel4Desc.hashCode());
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
		SccCode other = (SccCode) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (createdYear == null) {
			if (other.createdYear != null)
				return false;
		} else if (!createdYear.equals(other.createdYear))
			return false;
		if (deprecated != other.deprecated)
			return false;
		if (deprecatedYear == null) {
			if (other.deprecatedYear != null)
				return false;
		} else if (!deprecatedYear.equals(other.deprecatedYear))
			return false;
		if (epaUnitTypeCd == null) {
			if (other.epaUnitTypeCd != null)
				return false;
		} else if (!epaUnitTypeCd.equals(other.epaUnitTypeCd))
			return false;
		if (euCapacityTypeCd == null) {
			if (other.euCapacityTypeCd != null)
				return false;
		} else if (!euCapacityTypeCd.equals(other.euCapacityTypeCd))
			return false;
		if (fedSccId == null) {
			if (other.fedSccId != null)
				return false;
		} else if (!fedSccId.equals(other.fedSccId))
			return false;
		if (inActive == null) {
			if (other.inActive != null)
				return false;
		} else if (!inActive.equals(other.inActive))
			return false;
		if (sccId == null) {
			if (other.sccId != null)
				return false;
		} else if (!sccId.equals(other.sccId))
			return false;
		if (sccIdL1Cd == null) {
			if (other.sccIdL1Cd != null)
				return false;
		} else if (!sccIdL1Cd.equals(other.sccIdL1Cd))
			return false;
		if (sccIdL2Cd == null) {
			if (other.sccIdL2Cd != null)
				return false;
		} else if (!sccIdL2Cd.equals(other.sccIdL2Cd))
			return false;
		if (sccIdL3Cd == null) {
			if (other.sccIdL3Cd != null)
				return false;
		} else if (!sccIdL3Cd.equals(other.sccIdL3Cd))
			return false;
		if (sccIdL4Cd == null) {
			if (other.sccIdL4Cd != null)
				return false;
		} else if (!sccIdL4Cd.equals(other.sccIdL4Cd))
			return false;
		if (sccLevel1Desc == null) {
			if (other.sccLevel1Desc != null)
				return false;
		} else if (!sccLevel1Desc.equals(other.sccLevel1Desc))
			return false;
		if (sccLevel2Desc == null) {
			if (other.sccLevel2Desc != null)
				return false;
		} else if (!sccLevel2Desc.equals(other.sccLevel2Desc))
			return false;
		if (sccLevel3Desc == null) {
			if (other.sccLevel3Desc != null)
				return false;
		} else if (!sccLevel3Desc.equals(other.sccLevel3Desc))
			return false;
		if (sccLevel4Desc == null) {
			if (other.sccLevel4Desc != null)
				return false;
		} else if (!sccLevel4Desc.equals(other.sccLevel4Desc))
			return false;
		return true;
	}

	public String getCompleteDescription() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("SCC Level 1: " + this.getSccLevel1Desc());
		sb.append("\r\n");
		sb.append("SCC Level 2: " + this.getSccLevel2Desc());
		sb.append("\r\n");
		sb.append("SCC Level 3: " + this.getSccLevel3Desc());
		sb.append("\r\n");
		sb.append("SCC Level 4: " + this.getSccLevel4Desc());

		return sb.toString();
	}
}

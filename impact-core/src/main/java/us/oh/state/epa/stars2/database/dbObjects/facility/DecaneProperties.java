package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;

public class DecaneProperties extends BaseDB {

	private static final long serialVersionUID = 5264194430714749623L;
	
	public static final Double AVG_MOLECULAR_WEIGHT_MIN_VAL = 100d;
	public static final Double AVG_MOLECULAR_WEIGHT_MAX_VAL = 1000d;
	public static final Double SPECIFIC_GRAVITY_MIN_VAL = 0d;
	public static final Double SPECIFIC_GRAVITY_MAX_VAL = 1.5d;
	
	public static final String MOLECULAR_WEIGHT_PROPERTY_LABEL = "Average Molecular Weight (lbmol) of Decanes+";
	public static final String SPECIFIC_GRAVITY_PROPERTY_LABEL = "Specific Gravity of Decanes+";

	private Integer fpId;
	
	// the following properties are represented as strings because AQD does not
	// want the system to add false precision to the value entered by the user
	// i.e., 
	// if the user had entered 25, the system should display 25, and not 25.00
	// if the user had entered 25.3, the system should display 25.3, and not 25.30
	// if the user had entered 25.0, the system should display 25.0, and not 25.00
	private String avgMolecularWtOfOil;
	private String avgMolecularWtOfProducedWater;
	private String specificGravityOfOil;
	private String specificGravityOfProducedWater;

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public String getAvgMolecularWtOfOil() {
		return avgMolecularWtOfOil;
	}

	public void setAvgMolecularWtOfOil(String avgMolecularWtOfOil) {
		this.avgMolecularWtOfOil = avgMolecularWtOfOil;
	}

	public String getAvgMolecularWtOfProducedWater() {
		return avgMolecularWtOfProducedWater;
	}

	public void setAvgMolecularWtOfProducedWater(String avgMolecularWtOfProducedWater) {
		this.avgMolecularWtOfProducedWater = avgMolecularWtOfProducedWater;
	}

	public String getSpecificGravityOfOil() {
		return specificGravityOfOil;
	}

	public void setSpecificGravityOfOil(String specificGravityOfOil) {
		this.specificGravityOfOil = specificGravityOfOil;
	}

	public String getSpecificGravityOfProducedWater() {
		return specificGravityOfProducedWater;
	}

	public void setSpecificGravityOfProducedWater(String specificGravityOfProducedWater) {
		this.specificGravityOfProducedWater = specificGravityOfProducedWater;
	}
	
	public void roundUpValues() {

		if (!Utility.isNullOrEmpty(avgMolecularWtOfOil)) {
			this.avgMolecularWtOfOil = Utility.roundUp(avgMolecularWtOfOil, 3);
		}

		if (!Utility.isNullOrEmpty(avgMolecularWtOfProducedWater)) {
			this.avgMolecularWtOfProducedWater = Utility.roundUp(avgMolecularWtOfProducedWater, 3);
		}

		if (!Utility.isNullOrEmpty(specificGravityOfOil)) {
			this.specificGravityOfOil = Utility.roundUp(specificGravityOfOil, 5);
		}

		if (!Utility.isNullOrEmpty(specificGravityOfProducedWater)) {
			this.specificGravityOfProducedWater = Utility.roundUp(specificGravityOfProducedWater, 5);
		}
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		setNewObject(false);
		setFpId(AbstractDAO.getInteger(rs, "fp_id"));
		setAvgMolecularWtOfOil(rs.getString("avg_molecular_wt_of_oil"));
		setAvgMolecularWtOfProducedWater(rs.getString("avg_molecular_wt_of_produced_water"));
		setSpecificGravityOfOil(rs.getString("specific_gravity_of_oil"));
		setSpecificGravityOfProducedWater(rs.getString("specific_gravity_of_produced_water"));
		setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
	}
	
	@Override
	public ValidationMessage[] validate() {
		
		this.clearValidationMessages();

		if (!Utility.isNullOrEmpty(avgMolecularWtOfOil)) {
			checkRangeValues(avgMolecularWtOfOil, AVG_MOLECULAR_WEIGHT_MIN_VAL, AVG_MOLECULAR_WEIGHT_MAX_VAL,
				"avgMolecularWtOfOil", "Average Molecular Weight (lbmol) of Decanes+ Oil/Condensate");
		}
		
		if (!Utility.isNullOrEmpty(avgMolecularWtOfProducedWater)) {
			checkRangeValues(avgMolecularWtOfProducedWater, AVG_MOLECULAR_WEIGHT_MIN_VAL, AVG_MOLECULAR_WEIGHT_MAX_VAL,
				"avgMolecularWtOfProducedWater", "Average Molecular Weight (lbmol) of Decanes+ Produced Water");
		}
		
		if (!Utility.isNullOrEmpty(specificGravityOfOil)) {
			checkRangeValues(specificGravityOfOil, SPECIFIC_GRAVITY_MIN_VAL, SPECIFIC_GRAVITY_MAX_VAL,
				"specificGravityOfOil", "Specific Gravity of Decanes+ Oil/Condensate");
		}
		
		if (!Utility.isNullOrEmpty(specificGravityOfProducedWater)) {
			checkRangeValues(specificGravityOfProducedWater, SPECIFIC_GRAVITY_MIN_VAL, SPECIFIC_GRAVITY_MAX_VAL,
				"specificGravityOfProducedWater", "Specific Gravity of Decanes+ Produced Water");
		}
		
		return validationMessages.values().toArray(new ValidationMessage[0]);
	}
}

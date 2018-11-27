package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oracle.adf.view.faces.model.SortCriterion;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.ModelingExtractResult;
import us.oh.state.epa.stars2.def.GeoPolygonDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.facility.FacilityReference;

@SuppressWarnings("serial")
public class ModelingExtract extends AppBase {

	private FacilityService facilityService;
	
	private FacilityReference facilityReference;

	private Boolean searchTypePolygon = false;

	private Boolean searchTypeRadial = true;
	
	private Double latitudeDegrees;
	
	private Double longitudeDegrees;
	
	private Double distanceKm;
	
	private String polygon;
	
	private List<String> pollutants;
	
	private List<String> excludedFacilityTypes;
	
	private TableSorter resultsWrapper = new TableSorter();

	private ModelingExtractResult[] results;
	
	private Boolean hasSearchResults = false;
	
	private List<SortCriterion> sortCriteria = initSortCriteria();
	
	protected HashMap<String, ValidationMessage> validationMessages = new HashMap<String, ValidationMessage>(
			1);
	
	public FacilityService getFacilityService() {
		return facilityService;
	}

	private List<SortCriterion> initSortCriteria() {
		List<SortCriterion> criteria = new ArrayList<SortCriterion>();

		criteria.add(new SortCriterion("distanceToFacility", true));
		criteria.add(new SortCriterion("distanceToReleasePoint", true));
		criteria.add(new SortCriterion("aqdSourceId", true));
		return criteria;
	}

	public FacilityReference getFacilityReference() {
		return facilityReference;
	}

	public void setFacilityReference(FacilityReference facilityReference) {
		this.facilityReference = facilityReference;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}


	public String reset() {
		setSearchTypePolygon(false);
		setSearchTypeRadial(true);
		setLatitudeDegrees(null);
		setLongitudeDegrees(null);
		setDistanceKm(null);
		setPolygon(null);
		setPollutants(null);
		setExcludedFacilityTypes(null);
		setResults(null);
		getResultsWrapper().clearWrappedData();
		setHasSearchResults(false);
		return SUCCESS;
	}
	
	public String submitSearch() {		
		results = null;
		hasSearchResults = false;
		if (!validateEmissionUnit()) {
			return FAIL;
		}
		Double latitudeSwDegrees = null;
		Double longitudeSwDegrees = null;
		Double latitudeSeDegrees = null;
		Double longitudeSeDegrees = null;
		Double latitudeNeDegrees = null;
		Double longitudeNeDegrees = null;
		Double latitudeNwDegrees = null;
		Double longitudeNwDegrees = null;
		
		if (searchTypePolygon && !searchTypeRadial && null != polygon) {
			GeoPolygonDef polygonDef = 
					(GeoPolygonDef)GeoPolygonDef.getData().getItem(polygon);
			
			 latitudeSwDegrees = polygonDef.getLatitudeSwDegrees();
			 longitudeSwDegrees = polygonDef.getLongitudeSwDegrees();
			 
			 latitudeSeDegrees = polygonDef.getLatitudeSeDegrees();
			 longitudeSeDegrees = polygonDef.getLongitudeSeDegrees();

			 latitudeNeDegrees = polygonDef.getLatitudeNeDegrees();
			 longitudeNeDegrees = polygonDef.getLongitudeNeDegrees();
			 
			 latitudeNwDegrees = polygonDef.getLatitudeNwDegrees();
			 longitudeNwDegrees = polygonDef.getLongitudeNwDegrees();
		}

		try {
			results = getFacilityService().modelingExtract(
					searchTypePolygon,searchTypeRadial,latitudeDegrees,
					longitudeDegrees,kmToMeters(distanceKm),pollutants,
					excludedFacilityTypes,
					 latitudeSwDegrees,  longitudeSwDegrees,
					 latitudeSeDegrees,  longitudeSeDegrees,
					 latitudeNeDegrees,  longitudeNeDegrees,
					 latitudeNwDegrees,  longitudeNwDegrees);
			DisplayUtil.displayHitLimit(results.length);
			resultsWrapper.setWrappedData(results);
			if (results.length == 0) {
				DisplayUtil
						.displayInfo("There are no results that match the search criteria");				
				return null;
			} else {
				hasSearchResults = true;
				resultsWrapper.setSortCriteria(getSortCriteria());
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search failed");
			results = new ModelingExtractResult[0];
			resultsWrapper.setWrappedData(results);
		}

		return SUCCESS;
	}

	private List<SortCriterion> getSortCriteria() {
		return sortCriteria;
	}
	
	public Boolean getSearchTypePolygon() {
		return searchTypePolygon;
	}

	public void setSearchTypePolygon(Boolean searchTypePolygon) {
		this.searchTypePolygon = searchTypePolygon;
	}

	public Boolean getSearchTypeRadial() {
		return searchTypeRadial;
	}

	public void setSearchTypeRadial(Boolean searchTypeRadial) {
		this.searchTypeRadial = searchTypeRadial;
	}

	public Double getLatitudeDegrees() {
		return latitudeDegrees;
	}

	public void setLatitudeDegrees(Double latitudeDegrees) {
		this.latitudeDegrees = latitudeDegrees;
	}

	public Double getLongitudeDegrees() {
		return longitudeDegrees;
	}

	public void setLongitudeDegrees(Double longitudeDegrees) {
		this.longitudeDegrees = longitudeDegrees;
	}

	public Double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(Double distanceKm) {
		this.distanceKm = distanceKm;
	}

	public String getPolygon() {
		return polygon;
	}

	public void setPolygon(String polygon) {
		this.polygon = polygon;
	}

	public List<String> getPollutants() {
		return pollutants;
	}

	public void setPollutants(List<String> pollutants) {
		this.pollutants = pollutants;
	}

	public List<String> getExcludedFacilityTypes() {
		return excludedFacilityTypes;
	}

	public void setExcludedFacilityTypes(List<String> excludedFacilityTypes) {
		this.excludedFacilityTypes = excludedFacilityTypes;
	}

	public TableSorter getResultsWrapper() {
		return resultsWrapper;
	}

	public void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}

	public ModelingExtractResult[] getResults() {
		return results;
	}

	public void setResults(ModelingExtractResult[] results) {
		this.results = results;
	}

	public Boolean getHasSearchResults() {
		return hasSearchResults;
	}

	public void setHasSearchResults(Boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	private Integer kmToMeters(Double km) {
		if (null != km) {
			Double meters = km * 1000;
			return (int) Math.round(meters);
		} else {
			return null;
		}
	}

	/**
	 * @param value
	 * @param fieldName
	 * @param fieldLabel
	 * @param referenceId
	 * 
	 */
	protected final boolean requiredField(Object value, String fieldName,
			String fieldLabel, String referenceId) {
		if (value == null || value.equals("")) {
			validationMessages.put(fieldName, new ValidationMessage(fieldName,
					"Attribute " + fieldLabel + " is not set.",
					ValidationMessage.Severity.ERROR, referenceId));
			return false;
		} else {
			validationMessages.remove(fieldName);
			return true;
		}
	}
	
	public final ValidationMessage[] validate() {
		
		validationMessages.clear();
		if (searchTypeRadial){
			
			Double minLatitude = SystemPropertyDef.getSystemPropertyValueAsDouble("MinLatitude", null);
			Double maxLatitude = SystemPropertyDef.getSystemPropertyValueAsDouble("MaxLatitude", null);
			Double minLongitude = SystemPropertyDef.getSystemPropertyValueAsDouble("MinLongitude", null);
			Double maxLongitude = SystemPropertyDef.getSystemPropertyValueAsDouble("MaxLongitude", null);
			
			requiredField(latitudeDegrees, "Latitude", "Latitude Value", "latitude:");
			requiredField(longitudeDegrees, "Longitude", "Longitude Value", "Longitude:");
			requiredField(distanceKm, "Distance", "Distance Value", "Distance:");	

			checkRangeValues(latitudeDegrees, minLatitude, maxLatitude,  "Latitude Value", "latitude:",
						null);
			checkRangeValues(longitudeDegrees, minLongitude, maxLongitude,  "Longitude Value", "Longitude:",
					null);
			checkRangeValues(distanceKm, 1.0d, 300.0d,  "Distance Value", "Distance:",
					null);

		} else if(searchTypePolygon){
			requiredField(polygon, "Polygon", "Polygon Value", "Polygon:");	
		}
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	protected final boolean validateEmissionUnit() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		validationMessages = validate();
		if (displayValidationMessages("tools.modelingData", validationMessages)) {
			isValid = false;
		}
		return isValid;
	}
	
	protected final void checkRangeValues(Object value, Object minValue,
			Object maxValue, String fieldName, String fieldLabel,
			String referenceId) {
		
		if (value instanceof Double) {
			if (minValue != null) {
				if ((Double) value < (Double) minValue) {
					validationMessages.put(fieldName + "min",
							new ValidationMessage(fieldName,
									"The minimum value for the attribute : "
											+ fieldLabel + " is " + minValue,
									ValidationMessage.Severity.ERROR,
									referenceId));
				} else {
					validationMessages.remove(fieldName + "min");
				}
			}
			if (maxValue != null) {
				if ((Double) value > (Double) maxValue) {
					validationMessages.put(fieldName + "max",
							new ValidationMessage(fieldName,
									"The maximum value for the attribute : "
											+ fieldLabel + " is " + maxValue,
									ValidationMessage.Severity.ERROR,
									referenceId));
				} else {
					validationMessages.remove(fieldName + "max");
				}
			}
		}else if (value instanceof Integer) {
			if (minValue != null) {
				if ((Integer) value < (Integer) minValue) {
					validationMessages.put(fieldName + "min",
							new ValidationMessage(fieldName,
									"The minimum value for the attribute : "
											+ fieldLabel + " is " + minValue,
									ValidationMessage.Severity.ERROR,
									referenceId));
				} else {
					validationMessages.remove(fieldName + "min");
				}
			}
			if (maxValue != null) {
				if ((Integer) value > (Integer) maxValue) {
					validationMessages.put(fieldName + "max",
							new ValidationMessage(fieldName,
									"The maximum value for the attribute : "
											+ fieldLabel + " is " + maxValue,
									ValidationMessage.Severity.ERROR,
									referenceId));
				} else {
					validationMessages.remove(fieldName + "max");
				}
			}
		}
		
	}
}

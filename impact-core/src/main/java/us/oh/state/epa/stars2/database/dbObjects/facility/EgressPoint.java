package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.EgrPointShapeDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.HortAccuracyMeasure;
import us.oh.state.epa.stars2.def.HortCollectionMethods;
import us.oh.state.epa.stars2.def.HortReferenceDatum;
import us.oh.state.epa.stars2.def.ReferencePoints;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;

public class EgressPoint extends FacilityNode  implements
		Comparable<EgressPoint>{

	private static final long serialVersionUID = -1279352117930789674L;

	private String egressPointNm;
    private String egressPointId; // Company ID
    private String releasePointId;
    private String dapcDesc;
    private String operatingStatusCd;
    private String egressPointTypeCd;
    private String egressPointShapeCd;
    private String fugitiveDimensionsUnit;
    private String emissionReleasePointType;
    private String coordDataSourceCd;
    private String hortCollectionMethodCd;
    private String hortReferenceDatumCd;
    private String referencePointCd;
    private String regulatedUserDsc;
    private String crossSectArea;
    private Float height;
    private BigDecimal diameter; ///
    private Float exitGasTempMax;
    private BigDecimal exitGasTempAvg; ////
    private Float exitGasFlowMax;
    private BigDecimal exitGasFlowAvg; ////

    private Float baseElevation;
    private Float releaseHeight;
    private Float plumeTemp;
    private Float areaOfEmissions;
    private String hortAccurancyMeasure;
    private Float stackFencelineDistance;
    private Float sourceMapScaleNumber;
    private Float latitudeDeg;
    private Float latitudeMin;
    private Float latitudeSec;
    private Float longitudeDeg;
    private Float longitudeMin;
    private Float longitudeSec;
    private Float buildingLength;
    private Float buildingWidth;
    private Float buildingHeight;
    private Float volumeLength;		// area length for fugitive-Area
    private Float volumeWidth;		// area width for fugitive-Area or volume width for fugitive-volume
    private Float volumeHeight;		// area Height for fugitive-Area or volume height for fugitive-volume
    private List<EgressPointCem> cems = new ArrayList<EgressPointCem>(0);
    private String associatedEpaEuIds; // Associated EU Ids; Not in database
    private transient String fedSCCId = null;  // keep track of the federal SCC for one of the processes which connects to this stack.
    private String aqdWiseEgressPointId = null;
    private BigDecimal exitGasVelocity; ////

	private Float basdElevationMax;
	private Float basdElevationMim;
	
	private static final BigDecimal MAX_VALUE = new BigDecimal("9999999999.99"); // this is the maximum value can be stored in DB - numeric(12,2)

    
    public EgressPoint() {
    	
        super();
        operatingStatusCd = EgOperatingStatusDef.OP;
        egressPointShapeCd = EgrPointShapeDef.OT;
        hortCollectionMethodCd = HortCollectionMethods.GPS;
        hortAccurancyMeasure = HortAccuracyMeasure.M100;
        hortReferenceDatumCd = HortReferenceDatum.W1984;
        referencePointCd = ReferencePoints.R106;
        coordDataSourceCd = "An Organization or individual that contracts to perform work";
        plumeTemp = new Float(60.0);
        basdElevationMax = SystemPropertyDef.getSystemPropertyValueAsFloat("RP_Max_BaseElevation", null);
        basdElevationMim = SystemPropertyDef.getSystemPropertyValueAsFloat("RP_Min_BaseElevation", null);
        
    }

    public void refresh(ValueChangeEvent ve) {
    	
    	this.baseElevation = null;
    	this.releaseHeight = null;
    	this.diameter = null;
    	this.exitGasVelocity = null;
    	this.exitGasFlowAvg = null;
    	this.exitGasTempAvg = null;

    }
    
    // This only copies from ep if the value is not already set.
    public void copy(EgressPoint ep) {
    	
        if(this.operatingStatusCd == null && ep.operatingStatusCd != null && ep.operatingStatusCd.length() > 0) this.operatingStatusCd = ep.operatingStatusCd;
        if(this.height == null && ep.height != null) this.height = ep.height;
        if(this.areaOfEmissions == null && ep.areaOfEmissions != null) this.areaOfEmissions = ep.areaOfEmissions;
        if(this.baseElevation == null && ep.baseElevation != null) this.baseElevation = ep.baseElevation;
        if(this.crossSectArea == null && ep.crossSectArea != null) this.crossSectArea = ep.crossSectArea;
        if(this.diameter == null && ep.diameter != null) this.diameter = ep.diameter;
        if(this.exitGasFlowAvg == null && ep.exitGasFlowAvg != null) this.exitGasFlowAvg = ep.exitGasFlowAvg;
        if(this.exitGasFlowMax == null && ep.exitGasFlowMax != null) this.exitGasFlowMax = ep.exitGasFlowMax;
        if(this.exitGasTempAvg == null && ep.exitGasTempAvg != null) this.exitGasTempAvg = ep.exitGasTempAvg;
        if(this.exitGasTempMax == null && ep.exitGasTempMax != null) this.exitGasTempMax = ep.exitGasTempMax;
        if(this.plumeTemp == null && ep.plumeTemp != null) this.plumeTemp = ep.plumeTemp;
        if(this.releaseHeight == null && ep.releaseHeight != null) this.releaseHeight = ep.releaseHeight;
        if(this.egressPointShapeCd == null && ep.egressPointShapeCd != null) this.egressPointShapeCd = ep.egressPointShapeCd;
        if(this.egressPointTypeCd == null && ep.egressPointTypeCd != null) this.egressPointTypeCd = ep.egressPointTypeCd;
        if(this.buildingHeight == null && ep.buildingHeight != null) this.buildingHeight = ep.buildingHeight;
        if(this.buildingLength == null && ep.buildingLength != null) this.buildingLength = ep.buildingLength;
        if(this.buildingWidth == null && ep.buildingWidth != null) this.buildingWidth = ep.buildingWidth;
        if(this.latitudeDeg == null && ep.latitudeDeg != null) this.latitudeDeg = ep.latitudeDeg;
        if(this.latitudeMin == null && ep.latitudeMin != null) this.latitudeMin = ep.latitudeMin;
        if(this.latitudeSec == null && ep.latitudeSec != null) this.latitudeSec = ep.latitudeSec;
        if(this.longitudeDeg == null && ep.longitudeDeg != null) this.longitudeDeg = ep.longitudeDeg;
        if(this.longitudeMin == null && ep.longitudeMin != null) this.longitudeMin = ep.longitudeMin;
        if(this.longitudeSec == null && ep.longitudeSec != null) this.longitudeSec = ep.longitudeSec;
        if(this.aqdWiseEgressPointId == null && ep.aqdWiseEgressPointId != null) this.aqdWiseEgressPointId = ep.aqdWiseEgressPointId;
        if(this.exitGasVelocity == null && ep.exitGasVelocity != null) this.exitGasVelocity = ep.exitGasVelocity;
        
    }
    
    public String getMouseOverTip() {
    	String mouseOverTip = dapcDesc;
		if (dapcDesc != null && !dapcDesc.equals("")) {
			if (dapcDesc.length() > 40) {
				mouseOverTip = dapcDesc.substring(0, 39) + "...";
			} else {
				mouseOverTip = dapcDesc;
			}
		}
    	return mouseOverTip;
    }

    public final Float getLatitudeNum() {
    	return latitudeDeg;
    }
    
    public final void setLatitudeNum(Float latitude) {
    	double remainder = 0f;
    	if (latitude != null) {
	    	if (latitude > 0) {
	    		latitudeDeg = (float)Math.floor(latitude);
	    		remainder = latitude - latitudeDeg;
	    	} else {
	    		latitudeDeg = (float)Math.ceil(latitude);
	    		remainder = Math.abs(latitude + latitudeDeg);
	    	}
	    	latitudeMin = (float)Math.floor(remainder * 60d);
	    	latitudeSec = (float)((remainder - (latitudeMin/60d)) * 3600d);
    	} else {
    		latitudeDeg = null;
    		latitudeMin = null;
    		latitudeSec = null;
    	}
    }
    
    public final Float getLongitudeNum() {
        return longitudeDeg;
    }
    
    public final void setLongitudeNum(Float longitude) {
    	if (longitude != null) {
	    	double remainder = 0f;
	    	if (longitude > 0) {
	    		longitudeDeg = (float)Math.floor(longitude);
	    		remainder = longitude - longitudeDeg;
	    	} else {
	    		longitudeDeg = (float)Math.ceil(longitude);
	    		remainder = Math.abs(longitude - longitudeDeg);
	    	}
	    	longitudeMin = (float)Math.floor(remainder * 60d);
	    	longitudeSec = (float)((remainder - (longitudeMin/60d)) * 3600d);
    	} else {
    		longitudeDeg = null;
    		longitudeMin = null;
    		longitudeSec = null;
    	}
    }

    public final String getLatitude() {
        if (latitudeDeg == null) {
            return null;
        }

        String latitude = "(" + latitudeDeg.toString() + ","
                + latitudeMin.toString() + "," + latitudeSec.toString() + ")";
        return latitude;
    }

    public final String getLongitude() {
        if (longitudeDeg == null) {
            return null;
        }

        String longitude = "(" + longitudeDeg.toString() + ","
                + longitudeMin.toString() + "," + longitudeSec.toString() + ")";
        return longitude;
    }
    
	public final String getGoogleMapsURL() {
		if (this.latitudeDeg == null || longitudeDeg == null)
			return null;
		
		return LocationCalculator.getGoogleMapsURL(this.latitudeDeg,
				this.longitudeDeg, this.releasePointId);
	}

    /**
     * @return
     */
    public final String getCoordDataSourceCd() {
        return coordDataSourceCd;
    }

    /**
     * @param
     */
    public final void setCoordDataSourceCd(String coordDataSourceCd) {
        this.coordDataSourceCd = coordDataSourceCd;
    }

    /**
     * @return
     */
    public final String getHortCollectionMethodCd() {
        return hortCollectionMethodCd;
    }

    /**
     * @param
     */
    public final void setHortCollectionMethodCd(String hortCollectionMethodCd) {
        this.hortCollectionMethodCd = hortCollectionMethodCd;
    }

    /**
     * @return
     */
    public final String getHortReferenceDatumCd() {
        return hortReferenceDatumCd;
    }

    /**
     * @param
     */
    public final void setHortReferenceDatumCd(String hortReferenceDatumCd) {
        this.hortReferenceDatumCd = hortReferenceDatumCd;
    }

    /**
     * @return
     */
    public final String getReferencePointCd() {
        return referencePointCd;
    }

    /**
     * @param
     */
    public final void setReferencePointCd(String referencePointCd) {
        this.referencePointCd = referencePointCd;
    }

    /**
     * @return
     */
    public final String getRegulatedUserDsc() {
        return regulatedUserDsc;
    }

    /**
     * @param
     */
    public final void setRegulatedUserDsc(String regulatedUserDsc) {
        this.regulatedUserDsc = regulatedUserDsc;
    }

    /**
     * @return
     */
    public final Float getStackFencelineDistance() {
        return stackFencelineDistance;
    }

    /**
     * @param
     */
    public final void setStackFencelineDistance(Float stackFencelineDistance) {
        checkRangeValues(stackFencelineDistance, new Float(0.1), new Float(5280.00),
                "stackFencelineDistance", "Fenceline Distance", "releasePoint:"+ releasePointId);
        this.stackFencelineDistance = stackFencelineDistance;
    }

    /**
     * @return
     */
    public final String getHortAccurancyMeasure() {
        return hortAccurancyMeasure;
    }

    /**
     * @param
     */
    public final void setHortAccurancyMeasure(String hortAccurancyMeasure) {
        this.hortAccurancyMeasure = hortAccurancyMeasure;
    }

    /**
     * @return
     */
    public final Float getSourceMapScaleNumber() {
        return sourceMapScaleNumber;
    }

    /**
     * @param
     */
    public final void setSourceMapScaleNumber(Float sourceMapScaleNumber) {
        checkRangeValues(sourceMapScaleNumber, new Float(0.0), new Float(99999999.00),
                "sourceMapScaleNumber", "Source Map Scale Number", "releasePoint:" + releasePointId);
        this.sourceMapScaleNumber = sourceMapScaleNumber;
    }

    /**
     * @return
     */
    public final Float getLatitudeDeg() {
        return latitudeDeg;
    }

    /**
     * @param latitudeDeg
     */
    public final void setLatitudeDeg(Float latitudeDeg) {
    	this.latitudeDeg = latitudeDeg;
    }

    /**
     * @return
     */
    public final Float getLatitudeMin() {
        return latitudeMin;
    }

    /**
     * @param latitudeMin
     */
    public final void setLatitudeMin(Float latitudeMin) {
    	if (latitudeMin != null) {
    		checkRangeValues(latitudeMin, new Float(0.0), new Float(59.0),
    				"latitudeMin", "Latitude Minute", "releasePoint:" + releasePointId);
    		this.latitudeMin = new Float(latitudeMin.intValue());
    	} else {
    		this.latitudeMin = latitudeMin;
    	}
    }

    /**
     * @return
     */
    public final Float getLatitudeSec() {
        return latitudeSec;
    }

    /**
     * @param latitudeSec
     */
    public final void setLatitudeSec(Float latitudeSec) {
    	if (latitudeSec != null) {
	        checkRangeValues(latitudeSec, new Float(0.0), new Float(59.99),
	                "latitudeSec", "Latitude Second", "releasePoint:" + releasePointId);
	        this.latitudeSec = latitudeSec;
    	} else {
    		this.latitudeSec = latitudeSec;
    	}
    }

    /**
     * @return
     */
    public final Float getLongitudeDeg() {
        return longitudeDeg;
    }

    /**
     * @param longitudeDeg
     */
    public final void setLongitudeDeg(Float longitudeDeg) {
		this.longitudeDeg = longitudeDeg;
    }

    /**
     * @return
     */
    public final Float getLongitudeMin() {
        return longitudeMin;
    }

    /**
     * @param longitudeMin
     */
    public final void setLongitudeMin(Float longitudeMin) {
    	if (longitudeMin != null) {
    		checkRangeValues(longitudeMin, new Float(0.0), new Float(59.0),
    				"longitudeMin", "Longitude Minute", "releasePoint:" + releasePointId);
    		this.longitudeMin = new Float(longitudeMin.intValue());
    	} else {
    		this.longitudeMin = longitudeMin;
    	}
    }

    /**
     * @return
     */
    public final Float getLongitudeSec() {
        return longitudeSec;
    }

    /**
     * @param latitudeSec
     */
    public final void setLongitudeSec(Float longitudeSec) {
    	if (longitudeSec != null) {
	        checkRangeValues(longitudeSec, new Float(0.0), new Float(59.99),
	                "longitudeSec", "Longitude Second", "releasePoint:" + releasePointId);
	        this.longitudeSec = longitudeSec;
    	} else {
    		this.longitudeSec = longitudeSec;
    	}
    }

    /**
     * @return
     */
    public final String getFugitiveDimensionsUnit() {
        return fugitiveDimensionsUnit;
    }

    /**
     * @param fugitiveDimensionsUnit
     */
    public final void setFugitiveDimensionsUnit(String fugitiveDimensionsUnit) {
        this.fugitiveDimensionsUnit = fugitiveDimensionsUnit;
    }

    /**
     * @return
     */
    public final String getEmissionReleasePointType() {
        return emissionReleasePointType;
    }

    /**
     * @param emissionReleasePointType
     */
    public final void setEmissionReleasePointType(
            String emissionReleasePointType) {
        this.emissionReleasePointType = emissionReleasePointType;
    }

    /**
     * @return
     */
    public final String getCrossSectArea() {
        return crossSectArea;
    }

    /**
     * @param crossSectArea
     */
    public final void setCrossSectArea(String crossSectArea) {
        this.crossSectArea = crossSectArea;
    }
    
    /**
     * @param crossSectArea
     */
    public final void setCrossSectArea(Float crossSectArea) {
        DecimalFormat decFormat = new DecimalFormat("##0.###");
        this.crossSectArea = decFormat.format(crossSectArea);
    }

    /**
     * @return
     */
    public final Float getHeight() {
        return height;
    }

    /**
     * @param height
     */
    public final void setHeight(Float height) {
        checkRangeValues(height, new Float(0.0), new Float(99999999.00), "height", "Height", "releasePoint:" + releasePointId);
        this.height = height;
    }

//    /**
//     * @return
//     */
//    public final Float getDiameter() {
//        return diameter;
//    }
//
//    /**
//     * @param diameter
//     */
//    public final void setDiameter(Float diameter) {
//        this.diameter = diameter;
//    }

    /**
     * @return
     */
    public final Float getExitGasTempMax() {
        return exitGasTempMax;
    }

    /**
     * @param exitGasTempMax
     */
    public final void setExitGasTempMax(Float exitGasTempMax) {
        this.exitGasTempMax = exitGasTempMax;
    }

    /**
     * @return
     */
    public final BigDecimal getExitGasTempAvg() {
        return exitGasTempAvg;
    }

    /**
     * @param exitGasTempAvg
     */
    public final void setExitGasTempAvg(BigDecimal exitGasTempAvg) {
        this.exitGasTempAvg = exitGasTempAvg;
    }

    /**
     * @return
     */
    public final Float getExitGasFlowMax() {
        return exitGasFlowMax;
    }

    /**
     * @param exitGasFlowMax
     */
    public final void setExitGasFlowMax(Float exitGasFlowMax) {
        this.exitGasFlowMax = exitGasFlowMax;
    }

//    /**
//     * @return
//     */
//    public final Float getExitGasFlowAvg() {
//        return exitGasFlowAvg;
//    }
//
//    /**
//     * @param exitGasFlowAvg
//     */
//    public final void setExitGasFlowAvg(Float exitGasFlowAvg) {
//        this.exitGasFlowAvg = exitGasFlowAvg;
//    }

    /**
     * @return
     */
    public final Float getBaseElevation() {
        return baseElevation;
    }

    /**
     * @param baseElevation
     */
    public final void setBaseElevation(Float baseElevation) {
        checkRangeValues(baseElevation, basdElevationMim, basdElevationMax, "baseElevation",
                "Base Elevation", "releasePoint:" + releasePointId);
        this.baseElevation = baseElevation;
    }

    /**
     * @return
     */
    public final Float getReleaseHeight() {
        return releaseHeight;
    }

    /**
     * @param releaseHeight
     */
    public final void setReleaseHeight(Float releaseHeight) {
        this.releaseHeight = releaseHeight;
    }

    /**
     * @return
     */
    public final Float getPlumeTemp() {
        return plumeTemp;
    }

    /**
     * @param plume
     */
    public final void setPlumeTemp(Float plumeTemp) {
        checkRangeValues(plumeTemp, new Float(0.0), null, "plumeTemp",
                "Plume Temp", "releasePoint:" + releasePointId);
        this.plumeTemp = plumeTemp;
    }

    /**
     * @return
     */
    public final Float getAreaOfEmissions() {
        return areaOfEmissions;
    }

    /**
     * @param
     */
    public final void setAreaOfEmissions(Float areaOfEmissions) {
        checkRangeValues(areaOfEmissions, new Float(0.0), new Float(99999999.00),
                "areaOfEmissions", "Area of Emissions", "releasePoint:" + releasePointId);
        this.areaOfEmissions = areaOfEmissions;
    }

    /**
     * @return
     */
    public final String getEgressPointNm() {
        return egressPointNm;
    }

    /**
     * @param egressPointNm
     */
    public final void setEgressPointNm(String egressPointNm) {
        this.egressPointNm = egressPointNm;
    }

    /**
     * @return
     */
    public final String getEgressPointId() {
        return egressPointId;
    }

    /**
     * @param egressPointId
     */
    public final void setEgressPointId(String egressPointId) {
        requiredField(egressPointId.trim(), "egressPointId", "Company Release Point ID", "releasePoint:" + releasePointId);
        checkDirty("egid", egressPointId.trim(), this.egressPointId, egressPointId.trim());

        this.egressPointId = egressPointId.trim();
    }
    
    public final String getReleasePointId() {
        return releasePointId;
    }

    public final void setReleasePointId(String releasePointId) {
        this.releasePointId = releasePointId;
    }

    /**
     * @return
     */
    public final String getDapcDesc() {
        return dapcDesc;
    }

    /**
     * @param regUserDesc
     */
    public final void setDapcDesc(String dapcDesc) {
        this.dapcDesc = dapcDesc;
    }

    /**
     * @return
     */
    public final String getOperatingStatusCd() {
        return operatingStatusCd;
    }

    /**
     * @param operatingStatusCd
     */
    public final void setOperatingStatusCd(String operatingStatusCd) {
        requiredField(operatingStatusCd, "egOperatingStatus",
                "Operating status", "releasePoint:" + releasePointId);
        fieldChangeEventLog("fego", this.egressPointId, EgOperatingStatusDef
                .getData().getItems().getItemDesc(this.operatingStatusCd),
                EgOperatingStatusDef.getData().getItems().getItemDesc(
                        operatingStatusCd));
        this.operatingStatusCd = operatingStatusCd;
    }

    /**
     * @return
     */
    public final String getEgressPointTypeCd() {
        return egressPointTypeCd;
    }

    /**
     * @param egressPointTypeCd
     */
    public final void setEgressPointTypeCd(String egressPointTypeCd) {
        requiredField(egressPointTypeCd, "egressPointTypeCd", "Release Type", "releasePoint:" + releasePointId);
        this.egressPointTypeCd = egressPointTypeCd;
        if (EgrPointTypeDef.isFugitive(this)) {
            cems.clear();
        }
    }

    /**
     * @return
     */
    public final String getEgressPointShapeCd() {
        return egressPointShapeCd;
    }

    /**
     * @param egressPointShapeCd
     */
    public final void setEgressPointShapeCd(String egressPointShapeCd) {
        requiredField(egressPointShapeCd, "egressPointShapeCd", "Shape", "releasePoint:" + releasePointId);
        this.egressPointShapeCd = egressPointShapeCd;
    }

    /**
     * @return
     */
    public final Float getBuildingLength() {
        return buildingLength;
    }

    /**
     * @param buildingLength
     */
    public final void setBuildingLength(Float buildingLength) {
        checkRangeValues(buildingLength, new Float(10.0), null,
                "buildingLength", "Building Length", "releasePoint:" + releasePointId);
        this.buildingLength = buildingLength;
    }

    /**
     * @return
     */
    public final Float getBuildingWidth() {
        return buildingWidth;
    }

    /**
     * @param buildingWidth
     */
    public final void setBuildingWidth(Float buildingWidth) {
        checkRangeValues(buildingWidth, new Float(10.0), null, "buildingWidth",
                "Building Width", "releasePoint:" + releasePointId);
        this.buildingWidth = buildingWidth;
    }

    /**
     * @return
     */
    public final Float getBuildingHeight() {
        return buildingHeight;
    }

    /**
     * @param buildingWidth
     */
    public final void setBuildingHeight(Float buildingHeight) {
        checkRangeValues(buildingHeight, new Float(10.0), null,
                "buildingHeight", "Building Height", "releasePoint:" + releasePointId);
        this.buildingHeight = buildingHeight;
    }

    /**
     * @return
     */
    public final Float getVolumeLength() {
        return volumeLength;
    }

    /**
     * @param VolumeLength
     */
    public final void setVolumeLength(Float volumeLength) {
        this.volumeLength = volumeLength;
    }

    /**
     * @return
     */
    public final Float getVolumeWidth() {
        return volumeWidth;
    }

    /**
     * @param volumeWidth
     */
    public final void setVolumeWidth(Float volumeWidth) {
        this.volumeWidth = volumeWidth;
    }

    /**
     * @return
     */
    public final Float getVolumeHeight() {
        return volumeHeight;
    }

    /**
     * @param volumeWidth
     */
    public final void setVolumeHeight(Float volumeHeight) {
        this.volumeHeight = volumeHeight;
    }

    public final List<EgressPointCem> getCems() {
        return cems;
    }

    public final void setCems(List<EgressPointCem> cems) {
        if (cems == null) {
            cems = new ArrayList<EgressPointCem>(0);
        }
        this.cems = cems;
    }

    public final void addCem(EgressPointCem cem) {
        cems.add(cem);
    }

    public final boolean isCemPresent() {
        boolean ret = false;
        if (cems != null && cems.size() > 0) {
            ret = true;
        }

        return ret;
    }

    /**
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
     */
    public final void populate(ResultSet rs) {
        try {
            super.populate(rs);

            aqdWiseEgressPointId = rs.getString("aqd_wise_egress_point_id");
            exitGasVelocity = rs.getBigDecimal("exit_gas_velocity");
            egressPointNm = rs.getString("egress_point_nm");
            egressPointId = rs.getString("egress_point_id");
            releasePointId = rs.getString("release_point_id");
            setFpNodeId(AbstractDAO.getInteger(rs, "egressPoint_nodeId"));
            setLastModified(AbstractDAO.getInteger(rs, "egressPoint_lm"));

            operatingStatusCd = rs.getString("operating_status_cd");
//            egressPointShapeCd = rs.getString("egress_point_shape_cd");
            egressPointTypeCd = rs.getString("egress_point_type_cd");
            if (EgrPointTypeDef.isFugitive(this)) {
                cems.clear();
            }

            dapcDesc = rs.getString("dapc_dsc");
//            fugitiveDimensionsUnit = rs.getString("fugitive_dimensions_unit");
            emissionReleasePointType = rs
                    .getString("emission_release_point_type");
//            hortCollectionMethodCd = rs.getString("hort_collection_method_cd");
//            hortReferenceDatumCd = rs.getString("hort_reference_datum_cd");
//            referencePointCd = rs.getString("reference_point_cd");
            regulatedUserDsc = rs.getString("regulated_user_dsc");
//            coordDataSourceCd = rs.getString("coordinate_data_source_cd");
//            crossSectArea = rs.getString("cross_sectional_area");
//            height = AbstractDAO.getFloat(rs, "height");
            diameter = rs.getBigDecimal("diameter");
//            exitGasTempMax = AbstractDAO.getFloat(rs, "exit_gas_temp_max");
            exitGasTempAvg = rs.getBigDecimal("exit_gas_temp_avg");
//            exitGasFlowMax = AbstractDAO.getFloat(rs, "exit_gas_flow_max");
            exitGasFlowAvg = rs.getBigDecimal("exit_gas_flow_avg");
            baseElevation = AbstractDAO.getFloat(rs, "base_elevation");
            releaseHeight = AbstractDAO.getFloat(rs, "release_height");
//            plumeTemp = AbstractDAO.getFloat(rs, "plume_temp");
//            areaOfEmissions = AbstractDAO.getFloat(rs, "area_of_emissions");
//            hortAccurancyMeasure = rs.getString("hort_accuracy_meas_cd");
//            stackFencelineDistance = AbstractDAO.getFloat(rs,
//                    "stack_fenceline_distance");
//            sourceMapScaleNumber= AbstractDAO.getFloat(rs,
//                    "source_map_scale_number");

            latitudeDeg= AbstractDAO.getFloat(rs, "lat_deg");
//            latitudeMin= AbstractDAO.getFloat(rs, "lat_min");
//            if (latitudeMin != null) {
//                this.latitudeMin = new Float(latitudeMin.intValue());
//            }
//            latitudeSec= AbstractDAO.getFloat(rs, "lat_sec");
            longitudeDeg= AbstractDAO.getFloat(rs, "long_deg");
//            longitudeMin= AbstractDAO.getFloat(rs, "long_min");
//            if (longitudeMin != null) {
//                this.longitudeMin = new Float(longitudeMin.intValue());
//            }
//            longitudeSec= AbstractDAO.getFloat(rs, "long_sec");

//            buildingLength= AbstractDAO.getFloat(rs, "building_length");
//            buildingWidth= AbstractDAO.getFloat(rs, "building_width");
//            buildingHeight= AbstractDAO.getFloat(rs, "building_height");
//            volumeLength= AbstractDAO.getFloat(rs, "volume_length");
//            volumeWidth= AbstractDAO.getFloat(rs, "volume_width");
//            volumeHeight= AbstractDAO.getFloat(rs, "volume_height");
            checkDirty("egid", egressPointId, this.egressPointId, egressPointId);
        } catch (SQLException sqle) {
            logger.error("Required field error");
            throw new RuntimeException(sqle);
        } finally {
            newObject = false;
        }
    }

    public final void validateCemTable() {
        HashMap<String, Integer> values = new HashMap<String, Integer>(0);
        ValidationMessage validationMessage;

        for (EgressPointCem tempCem : cems) {
            String temp = tempCem.getCemDsc();
            if (temp == null || temp.equals("")) {
                validationMessage = new ValidationMessage("cemDsc",
                        "null description in the CEM table",
                        ValidationMessage.Severity.ERROR, "releasePoint:"
                                + releasePointId);
                validationMessages.put("cemDsc", validationMessage);
                return;
            } else if (values.containsKey(temp)) {
                validationMessage = new ValidationMessage("cemDsc",
                        "Duplicate value in the CEM table",
                        ValidationMessage.Severity.ERROR, "releasePoint:"
                                + releasePointId);
                validationMessages.put("cemDsc", validationMessage);
                return;
            } else {
                values.put(temp, 1);
            }
        }
        validationMessages.remove("cemDsc");
    }
    
    private void validateRangeValues(String msgStr) {
    	
		Float minLatitude = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLatitude", null);
    	Float maxLatitude = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLatitude", null);
    	Float minLongitude = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLongitude", null);
    	Float maxLongitude = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLongitude", null); 

    	checkRangeValues(latitudeDeg, minLatitude, maxLatitude, "facLatitudeDec", 
				msgStr + "Latitude Degree", "releasePoint:" + releasePointId);
    	checkRangeValues(longitudeDeg, minLongitude, maxLongitude, "facLongitudeDec", 
    			msgStr + " Longitude Degree", "releasePoint:" + releasePointId);
        checkRangeValues(releaseHeight, new Float(0.0),null , "areaReleaseHeight", "Release Height", "releasePoint:" + releasePointId);
    }
    
    public final ValidationMessage[] validate() {
        return validate(true);
    }

    public final ValidationMessage[] validate(boolean mustHaveValue) {
        String msgStr;
        clearValidationMessages();
        if (releasePointId == null || releasePointId.equals("")) {
            String s = regulatedUserDsc;
            if(s == null) {
                s = dapcDesc;
            }
            if (s != null) {
            	msgStr = "Release Point with description [" + s + "]";
            } else {
                msgStr = "Release Point";            	
            }
        } else {
            msgStr = "Release Point[" + releasePointId + "]";
        }
        
        validateRangeValues(msgStr);
        
        requiredField(egressPointTypeCd, "egressPointTypeCd", msgStr + " Release Point Type", "releasePoint:" + releasePointId);
        
        if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
        	requiredField(dapcDesc, "egpDapcDesc", msgStr + " AQD Description", "releasePoint:" + releasePointId);
        }
        
        requiredField(egressPointId, "egressPointId", msgStr + " Company Release Point ID", "releasePoint:" + releasePointId);
        requiredField(operatingStatusCd, "operatingStatusCd", msgStr + " Operating Status", "releasePoint:" + releasePointId);
        requiredField(regulatedUserDsc, "egrPntegulatedUserDsc", msgStr + " Company Release Point Description", "releasePoint:" + releasePointId);
        if (egressPointTypeCd != null) {
    		if (egressPointTypeCd.equals(EgrPointTypeDef.FUGITIVE)) {

            } else {
    			requiredField(releaseHeight, "areaReleaseHeight", msgStr + " Stack Height (ft)", "releasePoint:" + releasePointId);
                requiredField(diameter, "diameter", msgStr + " Stack Diameter (ft)", "releasePoint:" + releasePointId);
                requiredField(exitGasTempAvg, "exitGasTempAvg", msgStr + " Exit Gas Temp (F)", "releasePoint:" + releasePointId);
                requiredField(exitGasVelocity, "exitGasVelocity", msgStr + " Exit Gas Velocity (ft/s)", "releasePoint:" + releasePointId);
                requiredField(baseElevation, "baseElevation", msgStr + " Base Elevation (ft)", "releasePoint:" + releasePointId);
                requiredField(latitudeDeg, "facLatitudeDec", msgStr + " Latitude", "releasePoint:" + releasePointId);
                requiredField(longitudeDeg, "facLongitudeDec", msgStr + " Longitude", "releasePoint:" + releasePointId);
                checkRangeValues(baseElevation, basdElevationMim, basdElevationMax, "baseElevation",
                		msgStr + " Base Elevation", "releasePoint:" + releasePointId);
                checkRangeValues(exitGasTempAvg, new BigDecimal("0"), EgressPoint.MAX_VALUE,
                        "exitGasTempAvg", msgStr + " Exit Gas Temp (F)", "releasePoint:" + releasePointId);
                checkRangeValues(exitGasFlowAvg, new BigDecimal("0"), EgressPoint.MAX_VALUE,
                        "exitGasFlowAvg", msgStr + " Exit Gas Flow Rate (acfm)", "releasePoint:" + releasePointId);
                checkRangeValues(exitGasVelocity, new BigDecimal("0"), EgressPoint.MAX_VALUE,
                        "exitGasVelocity", msgStr + " Exit Gas Velocity (ft/s)", "releasePoint:" + releasePointId);
                checkRangeValues(releaseHeight, new Float(0.0), new Float(1000.0), "areaReleaseHeight", "Release Height", "releasePoint:" + releasePointId);
                checkRangeValues(diameter, new BigDecimal("0"), EgressPoint.MAX_VALUE,
                        "diameter", "Stack Diameter (ft)", "releasePoint:" + releasePointId);
    		}
    	}

        validateCemTable();

        return new ArrayList<ValidationMessage>(validationMessages.values())
                .toArray(new ValidationMessage[0]);
    }
    
	public ValidationMessage[] checkLocation() {
		
		Float minLatitude = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLatitude", null);
    	Float maxLatitude = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLatitude", null);
    	Float minLongitude = SystemPropertyDef.getSystemPropertyValueAsFloat("MinLongitude", null);
    	Float maxLongitude = SystemPropertyDef.getSystemPropertyValueAsFloat("MaxLongitude", null); 
    	
		this.clearValidationMessages();
        requiredField(latitudeDeg, "facLatitudeDec", "Latitude", "releasePoint:" + releasePointId);
        requiredField(longitudeDeg, "facLongitudeDec", "Longitude", "releasePoint:" + releasePointId);
		
        if(latitudeDeg != null){
			checkRangeValues(latitudeDeg, minLatitude, maxLatitude,
					"facLatitudeDec", "Latitude Degree", "releasePoint:"
							+ releasePointId);
        }
        
        if(longitudeDeg != null){
			checkRangeValues(longitudeDeg, minLongitude, maxLongitude,
					"facLongitudeDec", "Longitude Degree", "releasePoint:"
							+ releasePointId);
        }
        
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	public ValidationMessage checkReleaseHeight() {
		ValidationMessage msg = null;
		if(releaseHeight != null){
			checkRangeValues(releaseHeight, new Float(0.0),null , "areaReleaseHeight", "Release Height", "releasePoint:" + releasePointId);
		}
		msg = validationMessages.get("areaReleaseHeight");
		return msg;
	}
	
	public ValidationMessage[] checkAttributes() {
		this.clearValidationMessages();
		requiredField(releaseHeight, "areaReleaseHeight", "Stack Height (ft)", "releasePoint:" + releasePointId);
        requiredField(diameter, "diameter", "Stack Diameter (ft)", "releasePoint:" + releasePointId);
        requiredField(exitGasTempAvg, "exitGasTempAvg", "Exit Gas Temp (F)", "releasePoint:" + releasePointId);
        requiredField(exitGasVelocity, "exitGasVelocity", "Exit Gas Velocity (ft/s)", "releasePoint:" + releasePointId);
        requiredField(baseElevation, "baseElevation", "Base Elevation (ft)", "releasePoint:" + releasePointId);
        checkRangeValues(exitGasTempAvg, new BigDecimal("0"), EgressPoint.MAX_VALUE,
                "exitGasTempAvg", "Exit Gas Temp (F)", "releasePoint:" + releasePointId);
        checkRangeValues(exitGasFlowAvg, new BigDecimal("0"), EgressPoint.MAX_VALUE,
                "exitGasFlowAvg", "Exit Gas Flow Rate (acfm)", "releasePoint:" + releasePointId);
        checkRangeValues(exitGasVelocity, new BigDecimal("0"), EgressPoint.MAX_VALUE,
                "exitGasVelocity", "Exit Gas Velocity (ft/s)", "releasePoint:" + releasePointId);
        checkRangeValues(releaseHeight, new Float(0.0), new Float(1000.0), "areaReleaseHeight", "Release Height", "releasePoint:" + releasePointId);
        checkRangeValues(baseElevation, basdElevationMim, basdElevationMax, "baseElevation", "Base Elevation", "releasePoint:" + releasePointId);
        checkRangeValues(diameter, new BigDecimal("0"), EgressPoint.MAX_VALUE,
                "diameter", "Stack Diameter (ft)", "releasePoint:" + releasePointId);
        
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
//	public ValidationMessage[] checkCemTable() {
//		this.clearValidationMessages();
//		validateCemTable();
//		return new ArrayList<ValidationMessage>(validationMessages.values())
//				.toArray(new ValidationMessage[0]);
//	}
    
    public void copyEgressPointData(EgressPoint dapcEgrPoint) {
    	setAqdWiseEgressPointId(dapcEgrPoint.getAqdWiseEgressPointId());
    	setDapcDesc(dapcEgrPoint.getDapcDesc());
    }
    
    public boolean isFugitive(){
    	return EgrPointTypeDef.isFugitive(this);
    }
    
    public String getAssociatedEpaEuIds() {
		return associatedEpaEuIds;
	}

	public void setAssociatedEpaEuIds(String epaEuId) {
		if (epaEuId != null) {
        	if (associatedEpaEuIds != null) {
        		if (!associatedEpaEuIds.contains(epaEuId)) {
        			associatedEpaEuIds += ", " + epaEuId;
        		}
        	} else {
        		associatedEpaEuIds = epaEuId;
        	}
        }
	}
	
	public boolean isRenderSourceMapScaleNumber() {
		boolean ret = false;
		int temp;

		if (hortCollectionMethodCd != null) {
			try {
				temp = new Integer(hortCollectionMethodCd).intValue();
				if ((temp >= 18 &&  temp <= 25) ||(temp >= 30 &&  temp <= 36)) {
					ret = true;
				}
			} catch (Exception e) {
				logger.error("Invalid value for attribute Horizontal Collection Method Code: " + hortCollectionMethodCd);
			}
		}
		return ret;
	}

    public String getFedSCCId() {
        return fedSCCId;
    }

    public void setFedSCCId(String fedSCCId) {
        // keep track of the first SCC code we find.
        if(this.fedSCCId != null) return;
        this.fedSCCId = fedSCCId;
    }
    

    public String getAqdWiseEgressPointId() {
		return aqdWiseEgressPointId;
	}

	public void setAqdWiseEgressPointId(String aqdWiseEgressPointId) {
		this.aqdWiseEgressPointId = aqdWiseEgressPointId;
	}

//	public Float getExitGasVelocity() {
//		return exitGasVelocity;
//	}
//
//	public void setExitGasVelocity(Float exitGasVelocity) {
//		this.exitGasVelocity = exitGasVelocity;
//	}

    @Override
    public int hashCode() {
        // DO NOT DELETE OR CHANGE THIS FUNCTION.  NEEDED IN UsEpaEisReport.java
        // We need to know when two release points differ in egressPointId or correlationId
        final int PRIME = 31;
        int result = ((getCorrelationId() == null) ? 0 : getCorrelationId().hashCode());
        result = PRIME * result + ((egressPointId == null) ? 0 : egressPointId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        // DO NOT DELETE OR CHANGE THIS FUNCTION.  NEEDED IN UsEpaEisReport.java
        // We need to know when two release points differ in egressPointId or correlationId
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final EgressPoint other = (EgressPoint) obj;
        if (egressPointId == null) {
            if (other.egressPointId != null)
                return false;
        } else if (!egressPointId.equals(other.egressPointId))
            return false;
        if (this.getCorrelationId() == null) {
            if (other.getCorrelationId() != null)
                return false;
        } else if (!this.getCorrelationId().equals(other.getCorrelationId()))
            return false;
        return true;
    }

	@Override
	public int compareTo(EgressPoint o) {
		return this.releasePointId.compareTo(o.getReleasePointId());
	}
	
	public BigDecimal getDiameter() {
		return diameter;
	}

	public void setDiameter(BigDecimal diameter) {
		this.diameter = diameter;
	}
	
	public BigDecimal getExitGasVelocity() {
		return exitGasVelocity;
	}

	public void setExitGasVelocity(BigDecimal exitGasVelocity) {
		this.exitGasVelocity = exitGasVelocity;
	}

	public BigDecimal getExitGasFlowAvg() {
		return exitGasFlowAvg;
	}

	public void setExitGasFlowAvg(BigDecimal exitGasFlowAvg) {
		this.exitGasFlowAvg = exitGasFlowAvg;
	}
	
	
	public void diameterValueChanged(ValueChangeEvent vce) {
		
		BigDecimal stackDiameter = null;
		
		if (null != vce) {
			
			stackDiameter = (BigDecimal)vce.getNewValue();
		}
		
		calculateExitGasFlowRate(stackDiameter, this.exitGasVelocity);
	}
	
	public void exitGasVelocityValueChanged(ValueChangeEvent vce) {
		
		BigDecimal exitGasVel = null;
		
		if (null != vce) {
			exitGasVel = (BigDecimal) vce.getNewValue();
		}

		calculateExitGasFlowRate(this.diameter, exitGasVel);
	}
	
	private void calculateExitGasFlowRate(final BigDecimal diameter, final BigDecimal exitGasVelocity) {
		
		
		logger.info("Automatically calculating the value of exit gas flow rate as ("
				+ diameter + "*" + diameter 
				+ ")"
				+ "*"
				+ exitGasVelocity
				+ "* 47.12389f)");
		BigDecimal c = new BigDecimal(47.12389);
		if (null != diameter && null != exitGasVelocity) {
			this.exitGasFlowAvg = c.multiply(diameter).multiply(diameter).multiply(exitGasVelocity);
			this.exitGasFlowAvg = this.exitGasFlowAvg.setScale(2, RoundingMode.HALF_UP);
//			this.exitGasFlowAvg = (diameter * diameter) * exitGasVelocity * 47.12389f;
		} else {
			this.exitGasFlowAvg = null;
		}
	}
}

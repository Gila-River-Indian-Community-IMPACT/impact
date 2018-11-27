package us.oh.state.epa.stars2.webcommon.reports;

import java.io.Serializable;
import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.UnitDef;

/*
 * Class PollutantRow is used to compare two hashmaps and generate a TreeSet of the results
 * The results is a TreeSet of PollutantRow objects that is compared based upon the name.
 * Each PollutantRow object contains the name and a reference to both the corresponding object
 * in the original set and the corresponding object in the compare set--if that name appears
 * in each.  Otherwise one or the other references are null to indicate that set does not
 * contain the value.
 * 
 * TreeSet is used so the names can be retrieved in sorted order.
 */

public class PollutantRow implements Comparable<PollutantRow>, Serializable {
    private static Logger logger = Logger.getLogger(PollutantRow.class);
    private String name; // EU name, Group name
    private String processName; //Process name
    private String fugitiveEmissions;
    private String stackEmissions;
    private String totalEmissions;
    private Double totalEmissionsV;
    private String emissionsUnitNumerator;
    private String link;
    private String clientID = "";  // to satisfy existing validation code
    private String showDetailClientID = ""; // to satisfy existing validation code
    // the following are just for the "Locate" command
    private String scc;
    private String sccDesc;
    private boolean tradeSecret;
    private String material;
    private String materialUnits;
    private String materialThrougput;
    private Double hoursOperation;

    public PollutantRow(String name, EmissionsReportPeriod erp, String fugitiveEmissions, String stackEmissions,
            String emissionsUnitNumerator, String emissionsUnitNumeratorCommon) {
        this.name = name;
        this.emissionsUnitNumerator = emissionsUnitNumeratorCommon;
        double t = 0;
        if(fugitiveEmissions != null) {
            double f = EmissionsReport.convertStringToNum(fugitiveEmissions, logger);
            double ff = EmissionUnitReportingDef.convert(
                    emissionsUnitNumerator, f,
                    emissionsUnitNumeratorCommon);
            this.fugitiveEmissions = EmissionsReport.numberToString(ff);
            t = ff;
        }
        if(stackEmissions != null) {
            double s = EmissionsReport.convertStringToNum(stackEmissions, logger);
            double ss = EmissionUnitReportingDef.convert(
                    emissionsUnitNumerator, s,
                    emissionsUnitNumeratorCommon);
            this.stackEmissions = EmissionsReport.numberToString(ss);
            t = t + ss;
        }
        totalEmissionsV = null;
        if(fugitiveEmissions != null || stackEmissions != null) {
            totalEmissions = EmissionsReport.numberToString(t);
            totalEmissionsV = new Double(t);
        }
        if(erp != null) {
            processName = erp.getTreeLabel();
            scc = erp.convertSCC();
            tradeSecret = erp.isTradeSecretS();
            SccCode sccCode = erp.getSccCode();
            sccDesc = null;
            if(sccCode != null) {
                sccDesc = sccCode.getSccLevel1Desc() + "; " +
                sccCode.getSccLevel2Desc() + "; " +
                sccCode.getSccLevel3Desc() + "; " +
                sccCode.getSccLevel4Desc();
            }
            hoursOperation = erp.getHoursPerYear();
            EmissionsMaterialActionUnits emau = erp.getCurrentMaus();
            if(emau != null) {
                String m = emau.getMaterial();
                if(m != null) {
                material = MaterialDef.getData().getItems().getItemDesc(m);
                }
                String units = emau.getMeasure();
                if(units != null) {
                    materialUnits = UnitDef.getData().getItems().getItemDesc(units);
                }
                materialThrougput = emau.getThroughput();
            }
        }
    }
    
    public PollutantRow(String name, Double stack, Double fugitive, String emissionsUnitNumeratorCommon) {
        this.name = name;
        double t = 0;
        this.emissionsUnitNumerator = emissionsUnitNumeratorCommon;
        if(fugitive != null) {
            this.fugitiveEmissions = EmissionsReport.numberToString(fugitive.doubleValue());
            t = fugitive.doubleValue();
        }
        if(stack != null) {
            this.stackEmissions = EmissionsReport.numberToString(stack.doubleValue());
            t = t + stack.doubleValue();
        }
        if(fugitive != null || stack != null) {
            totalEmissions = EmissionsReport.numberToString(t);
            totalEmissionsV = new Double(t);
        }
    }

    public final int compareTo(PollutantRow b) {
        // return -1 if less than argument, etc.
        if(totalEmissionsV != null && b.totalEmissionsV != null) {
            int cmp = b.totalEmissionsV.compareTo(totalEmissionsV);
            if(cmp != 0) {
                return cmp;
            } else {
                int cmp2 = name.compareTo(b.name); // do names in assending
                return cmp2;
            }
        } else if (totalEmissionsV == null && b.totalEmissionsV == null) {
            // if equal, then order by the name
            int cmp2 = name.compareTo(b.name); // do names in assending
            return cmp2;
        } else if(totalEmissionsV == null) {
            return 1;
        } else {
            return -1;
        }
    }

    public static Double add(Double value, String addOn, String emissionsUnitNumerator, String emissionsUnitNumeratorCommon) {
        Double result = value;
        if(addOn != null) {
            // convert addOn
            double d = EmissionsReport.convertStringToNum(addOn, logger);
            double dd = EmissionUnitReportingDef.convert(
                    emissionsUnitNumerator,
                    d,
                    emissionsUnitNumeratorCommon);
            if(result != null) {
                result = new Double(result + dd);
            } else {
                result = new Double(dd);
            }
        }
        return result;
    }

    public String getEmissionsUnitNumerator() {
        return emissionsUnitNumerator;
    }

    public String getFugitiveEmissions() {
        return fugitiveEmissions;
    }

    public String getName() {
        return name;
    }

    public String getStackEmissions() {
        return stackEmissions;
    }

    public String getTotalEmissions() {
        return totalEmissions;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getShowDetailClientID() {
        return showDetailClientID;
    }

    public void setShowDetailClientID(String showDetailClientID) {
        this.showDetailClientID = showDetailClientID;
    }

    public Double getHoursOperation() {
        return hoursOperation;
    }

    public void setHoursOperation(Double hoursOperation) {
        this.hoursOperation = hoursOperation;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialThrougput() {
        return materialThrougput;
    }

    public void setMaterialThrougput(String materialThrougput) {
        this.materialThrougput = materialThrougput;
    }

    public String getMaterialUnits() {
        return materialUnits;
    }

    public void setMaterialUnits(String materialUnits) {
        this.materialUnits = materialUnits;
    }

    public String getScc() {
        return scc;
    }

    public void setScc(String scc) {
        this.scc = scc;
    }

    public String getSccDesc() {
        return sccDesc;
    }

    public String getProcessName() {
        return processName;
    }

    public Double getTotalEmissionsV() {
        return totalEmissionsV;
    }

    public boolean isTradeSecret() {
        return tradeSecret;
    }

    public void setTradeSecret(boolean tradeSecret) {
        this.tradeSecret = tradeSecret;
    }
}

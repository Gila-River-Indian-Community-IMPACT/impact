package us.oh.state.epa.stars2.webcommon.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantsControlled;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.NonToxicPollutantDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.FacilityEmissionFlow;


public class ControlInfoRow  implements Serializable {

	private static final long serialVersionUID = -4544190622195308515L;

	private String streamLabel;
    private Float flowFactor;
    private String flowFactorStr;
    private Float percent;
    private String percentStr;
    private String type;
	private String deviceName;
    private EgressPoint stack;
	private double input;
    private String inputStr;
    private double output;
    private String outputC;
    private double captureEff;
    private String pollutantControlCd; // which pollutant control used.
    private String annualAdjust;
    private Double operContEff;
	private Double fugitiveFraction;
    private boolean noDownstreamEquip = false;
    private String fugitiveFractionC;
	private Double stackFraction;
    private String stackFractionC;
    private static StringBuffer problems = null;
    
    public final static double startingValue = 1000d;
    
    // What are the pollutants corresponding to VOC_HAP & PM_HAP
    private static String ER_VOC_Pollutant = "";
    private static String ER_PM_Pollutant1 = "";
    private static String ER_PM_Pollutant2 = "";
    
    private static Logger logger = Logger.getLogger(ControlInfoRow.class);

	public ControlInfoRow() {
	}

    private static void getParmInfo() {
    	ER_VOC_Pollutant = SystemPropertyDef.getSystemPropertyValue("ER_VOC_Pollutant", null);
    	ER_PM_Pollutant1 = SystemPropertyDef.getSystemPropertyValue("ER_PM_Pollutant1", null);
    	ER_PM_Pollutant2 = SystemPropertyDef.getSystemPropertyValue("ER_PM_Pollutant2", null);
    }
    
    public static ArrayList<ControlInfoRow> generateControlMatrix(Facility facility, EmissionProcess p,
            EmissionsReportPeriod period,  EmissionRow er, boolean forceFug, boolean forceStk) {
        ArrayList<ControlInfoRow> matrix = new ArrayList<ControlInfoRow>();
        ControlInfoRow iRow;
        String pollutantCd = er.getPollutantCd();
        getParmInfo();
        problems = new StringBuffer();
        int num = p.getControlEquipments().size();
        int numStack = 0;
        for(EgressPoint ep : p.getEgressPoints()) {
            // add in only stack EPs.
            if(!EgrPointTypeDef.isFugitive(ep)){
                numStack++;
            }
        }
        
        if(0 == num && numStack == 0){  // no control equipment.
            iRow = new ControlInfoRow();
            iRow.setStreamLabel("0");
            iRow.setDeviceName("");
            iRow.setInput(startingValue);
            iRow.setOutput(0d);
            iRow.setStackFraction(0d);
            matrix.add(iRow);
            iRow.setFugitiveFraction(startingValue);
            return matrix;
        }
        num += numStack;
        
        List<FacilityEmissionFlow> fefEpNormal = p.getEpEmissionFlows(facility, pollutantCd); 
        boolean allZero = FacilityEmissionFlow.normalize(fefEpNormal);
        if(allZero) {
            
            String allZeroCaptureError = "All the control equipment/stacks immediately downstream from process " + p.getProcessId()
                + " specify 0% capture efficiency in the facility inventory.  ";
            problems.append(allZeroCaptureError);
            iRow = new ControlInfoRow();
            iRow.setStreamLabel("0");
            iRow.setDeviceName("");
            iRow.setInput(startingValue);
            iRow.setOutput(0d);
            iRow.setStackFraction(0d);
            matrix.add(iRow);
            iRow.setFugitiveFraction(startingValue);
            return matrix;
        }
        
        int stackNum = 1;  // stream numbering
        double newFrac;
        if(0 != numStack) { // Process each stack
            for(EgressPoint eg2 : p.getEgressPoints()){
                if(EgrPointTypeDef.isFugitive(eg2)) continue;
                // Count as stack emissions
                iRow = new ControlInfoRow();
                iRow.setStreamLabel(Integer.toString(stackNum++));
                FacilityEmissionFlow fef;
                fef = FacilityEmissionFlow.getEmissionFlow(fefEpNormal,
                        FacilityEmissionFlow.STACK_TYPE, eg2.getReleasePointId());
                iRow.flowFactor = fef.getFlowFactor();
                iRow.flowFactorStr = fef.getFlowFactorStr();
                iRow.percent = fef.getPercentValue();
                iRow.percentStr = fef.getPercent();
                iRow.type = fef.getType();
                iRow.setDeviceName(eg2.getReleasePointId());
                iRow.stack = eg2;
                newFrac = startingValue * fef.getPercentValue()/100;
                iRow.setInput(newFrac);
                iRow.setOutput(newFrac);
                iRow.setFugitiveFraction(0d);
                iRow.setCaptureEff(100d);
                iRow.setStackFraction(newFrac);
                matrix.add(iRow);
            }
        }
        int ceNum = stackNum;
        for(ControlEquipment ce2 : p.getControlEquipments()){
            // Recurse for each ce under it
            FacilityEmissionFlow fef;
            fef = FacilityEmissionFlow.getEmissionFlow(fefEpNormal,
                    FacilityEmissionFlow.CE_TYPE, ce2.getControlEquipmentId());
            newFrac = startingValue * fef.getPercentValue()/100;
            ControlInfoRow.processNext(facility, 0, matrix, ce2, er.getAnnualAdjust(),
                    er.getAnnualAdjustV(),
                    period.getHoursPerYear(), pollutantCd,
                    Integer.toString(ceNum++), fef, newFrac, forceFug, forceStk);
        }
        return matrix;
    }
    
    public final static boolean addUpStackEmissions(List<ControlInfoRow> lst, HashMap<EgressPoint, Double> stacks, double realTotal) {
        // realTotal is the actual total emissions.
        // calcTotal is the total computed assuming emissions out of process is a fixed number.
        double calcTotal = ControlInfoRow.fugitiveTotal(lst) + ControlInfoRow.stackTotal(lst);
        double factor = 0d; // the factor makes the parts assigned to each EP to add up to the actual total of emissions reported.
        if(calcTotal != 0d) {
            factor = realTotal/calcTotal;
        }
        boolean noErrors = true;
        for(ControlInfoRow cir : lst) {
            if(cir.stackFraction != null && !cir.stackFraction.equals(new Double(0d))) {
                if(cir.stack == null) {
                    logger.error("Should have had a stack for release point " + cir.deviceName);
                    noErrors = false;
                } else {
                    Double dObj = stacks.get(cir.stack);
                    if(dObj == null) {
                        dObj = cir.stackFraction * factor;
                    } else {
                        dObj += cir.stackFraction * factor;
                    }
                    stacks.put(cir.stack, dObj);
                }
            }
            if(cir.fugitiveFraction != null && !cir.fugitiveFraction.equals(new Double(0d))) {
                Double dObj = stacks.get(null);
                if(dObj == null) {
                    dObj = cir.fugitiveFraction * factor;
                } else {
                    dObj += cir.fugitiveFraction * factor;
                }
                stacks.put(null, dObj);
            }
        }
        return noErrors;
    }
    
    public final static double afterControlTotal(List<ControlInfoRow> lst){
        // Add up all the emissions except those that never enter any control equipment
        // This is output from a stack that is first and non-captured emissions to
        // control equipment that is first.
        // then
        // divide that by the original total less the initial losses
        // to get
        // fraction remaining.  But return fraction controlled.
        boolean haveControlEquip = false;
        double afterControl = 0d;
        double initLoss = 0d;

        // Sum them starting with smallest to keep accuracy.
       for(int i = lst.size(); i > 0;) {
           --i;
           if(lst.get(i).pollutantControlCd != null && lst.get(i).pollutantControlCd.length() > 0) {
               haveControlEquip = true;
           }
           boolean skipStack = false;
           boolean subtractCeNonCapture = false;
           if(!lst.get(i).streamLabel.contains(".")) {
               // first level
               if(FacilityEmissionFlow.STACK_TYPE.equals(lst.get(i).type)) {
                   skipStack = true;
               } else { // is control equipment
                   if(lst.get(i).noDownstreamEquip) {
                       subtractCeNonCapture = true;
                   }
               }
           }
           if(!skipStack) {  // if stack is first, skip all its emissions
               Double fug = lst.get(i).getFugitiveFraction();
               if(null != fug){
                   afterControl += fug;
                   if(subtractCeNonCapture) { // if first ce with no downstream don't count its fugitive output
                       double d = lst.get(i).input * (100d - lst.get(i).getCaptureEff()) / 100d;
                       afterControl -= d;
                       initLoss += d;
                   }
               }
               Double stk = lst.get(i).getStackFraction();
               if(null != stk){
                   afterControl += stk.floatValue();
               }
           } else {
               Double fug = lst.get(i).getFugitiveFraction();
               if(null != fug){
                   initLoss += fug;
               }
               Double stk = lst.get(i).getStackFraction();
               if(null != stk){
                   initLoss += stk.floatValue();
               }
           }
       }
       if(!haveControlEquip) {
           return -1d; // indicate there are no controls
       }
       if(startingValue - initLoss == 0) {
           return 0d;
       } else {
           return 100d - (afterControl)*100d/(startingValue - initLoss);
       }
    }
    
    public final static double fugitiveTotal(List<ControlInfoRow> lst){
        double d = 0d;
        // Sum them starting with smallest to keep accuracy.
       for(int i = lst.size(); i > 0;) {
           Double fug = lst.get(--i).getFugitiveFraction();
           if(null != fug){
            d += fug;
           }
        }
        return d/startingValue;
    }
    
    public final static double stackTotal(List<ControlInfoRow> lst){
        double d = 0d;
        // Sum them starting with smallest to keep accuracy.
       for(int i = lst.size(); i > 0;) {
           Double stk = lst.get(--i).getStackFraction();
           if(null != stk){
            d += stk.floatValue();
           }
        }
        return d/startingValue;
    }
    
    private static boolean processNext(Facility facility, int depthCnt, ArrayList<ControlInfoRow> matrix,
            ControlEquipment ce, String annualAdjust, double annualAdjustV, Double hoursPerYear,
            String pollutantCd, String stream, FacilityEmissionFlow fef, double inputFrac,
            boolean forceFug, boolean forceStk) {
        if(depthCnt > 100) return false;
        // Generate Info row
        ControlInfoRow iRow = new ControlInfoRow();
        iRow.setStreamLabel(stream);
        double aa = 0d;
        double ab = 1d;
        if(null!=annualAdjust && hoursPerYear != null &&
                hoursPerYear >= annualAdjustV) {
            aa = annualAdjustV / hoursPerYear;
            ab = (hoursPerYear - annualAdjustV) / hoursPerYear;
        }

        iRow.setAnnualAdjust(annualAdjust);
        iRow.flowFactor = fef.getFlowFactor();
        iRow.flowFactorStr = fef.getFlowFactorStr();
        iRow.percent = fef.getPercentValue();
        iRow.percentStr = fef.getPercent();
        iRow.type = FacilityEmissionFlow.CE_TYPE;
        if(null != ce) iRow.setDeviceName(ce.getControlEquipmentId());
        iRow.setInput(inputFrac);
        
        // Locate pollutant control info
        PollutantsControlled pcExact = null;
        PollutantsControlled VOC_pollutantFound = null;
        PollutantsControlled PM_pollutant1Found = null;
        PollutantsControlled PM_pollutant2Found = null;
        float minCapture = 100.f; // Note, there will be at least one pollutant.
        boolean nonZeroCap = false;
        for(PollutantsControlled pc : ce.getPollutantsControlled()) {
        	if(null != pc.getCaptureEff()) {
        		Float temp = new Float(pc.getCaptureEff());
        		if(minCapture >= temp.floatValue() && temp.floatValue() > 0) {
        			minCapture = temp;
        			nonZeroCap = true;
        		}
        	}
            if(pollutantCd.equals(pc.getPollutantCd())){
                pcExact = pc;
                continue;
            }
            // Look for VOC
            if(ER_VOC_Pollutant.equals(pc.getPollutantCd())){
                VOC_pollutantFound = pc;
            }
            // Look for PM
            if(ER_PM_Pollutant1.equals(pc.getPollutantCd())){
                PM_pollutant1Found = pc;
            }
            if(ER_PM_Pollutant2.equals(pc.getPollutantCd())){
                PM_pollutant2Found = pc;
            }
        }
        if(!nonZeroCap) {
            // we did not find any capture %s
            minCapture = 0f;
        }
        
        /* Rules for Control Equipment:
         *   . Emissions not captured by the equipment is fugitive.
         *   . Capture % for a pollutant not controled is the lowest
         *     capture effeciency of the pollutants that are controlled
         *     (often the capture % is the same regardless of pollutant).
         *   . Annual adjustment does not effect capture %, it only
         *     effects control %.  For that fraction of time there was
         *     no control.
         *   . If a pollutant is not named to be controled but is a
         *     VOC-HAP, then if VOC is controlled, use those numbers.
         *   . If a pollutant is not named to be controled but is a PM-HAP,
         *     then use PM controls.
         */
        
        PollutantsControlled pollutantUsed = null;
        float pollutantControl = 0f;
        // Can we use a substitute
        if(null == pcExact){
            try {
                if(null != VOC_pollutantFound &&
                        NonToxicPollutantDef.getTheCategory(pollutantCd).toLowerCase().contains("voc")) {
                    pollutantUsed = VOC_pollutantFound;
                    if(!Utility.isNullOrEmpty(VOC_pollutantFound.getOperContEff()))
                    	pollutantControl = new Float(VOC_pollutantFound.getOperContEff());
                }
                if(NonToxicPollutantDef.getTheCategory(pollutantCd).toLowerCase().contains("pm")){
                    if(null != PM_pollutant1Found &&
                    		!Utility.isNullOrEmpty(PM_pollutant1Found.getOperContEff()) &&
                            (new Float(PM_pollutant1Found.getOperContEff())) > pollutantControl){
                        pollutantUsed = PM_pollutant1Found;
                        pollutantControl = new Float(PM_pollutant1Found.getOperContEff());
                    }
                    if(null != PM_pollutant2Found &&
                    		!Utility.isNullOrEmpty(PM_pollutant2Found.getOperContEff()) &&
                            (new Float(PM_pollutant2Found.getOperContEff())) > pollutantControl){
                        pollutantUsed = PM_pollutant2Found;
                        pollutantControl = new Float(PM_pollutant2Found.getOperContEff());
                    }
                }
            } catch(ApplicationException ae) {
                logger.error("Failed to get category for pollutantCd " + pollutantCd, ae);
            }
        } else {
            pollutantUsed = pcExact; 
            if (!Utility.isNullOrEmpty(pcExact.getOperContEff())) {
            	pollutantControl = new Float(pcExact.getOperContEff());
            }
        }
        if(null != pollutantUsed){
            iRow.setCaptureEff(EmissionsReport.convertStringToNum(pollutantUsed.getCaptureEff(), logger));
            iRow.setOperContEff(EmissionsReport.convertStringToNum(pollutantUsed.getOperContEff(), logger));
            iRow.setPollutantControlCd(pollutantUsed.getPollutantCd());
            iRow.setStackFraction(0d);
        } else {
            iRow.setCaptureEff(minCapture);
            iRow.setOperContEff(null);
            iRow.setStackFraction(0d);
        }
        // make adjustments if needed to "correct" efficencies
        if(forceFug && depthCnt == 0 && iRow.getCaptureEff() == 100.d) {
            // Change to generate fugitives
            iRow.setCaptureEff(90.d);  // arbitrarily set capture at 90%
        }
        if(forceStk) {
            // make minimal changes to make stack emissions happen
            if(iRow.getCaptureEff() == 0.d) {
                iRow.setCaptureEff(90.d);
            }
            if(iRow.getOperContEff() != null && iRow.getOperContEff() == 100.d) {
                iRow.setOperContEff(95.d);
            }
        }
        iRow.setFugitiveFraction((100.d - iRow.getCaptureEff())* inputFrac/100d);
        if(null != pollutantUsed) {
            iRow.setOutput(((100.d - iRow.getOperContEff())*ab/100d + aa) *
                    (inputFrac - iRow.getFugitiveFraction()));
        } else {
            iRow.setOutput(inputFrac - iRow.getFugitiveFraction());
        }
        matrix.add(iRow);
        int subLabel = 1;
        int num = ce.getControlEquips().size();
        // add in only stack EPs.
        for(EgressPoint ep : ce.getEgressPoints()) {
            if(!EgrPointTypeDef.isFugitive(ep)) num += 1;
        }
        if(0 == num){  // nothing downstream, then remaining emissions is fugitive.
            iRow.noDownstreamEquip = true;
            iRow.setFugitiveFraction(iRow.getFugitiveFraction() +  iRow.getOutput());
        }
        
        List<FacilityEmissionFlow> fefCeNormal = ce.getCeEmissionFlows(facility, pollutantCd); 
        boolean allZero = FacilityEmissionFlow.normalize(fefCeNormal);
        if(allZero) {
            
            String allZeroCaptureError = "All the control equipment immediately downstream from control equipment " + ce.getControlEquipmentId()
                + " specify 0% capture efficiency in the facility inventory.  ";
            problems.append(allZeroCaptureError);
            return true;
        }
        
        double newFrac;
        // Process each piece of equipment
        for(EgressPoint eg2 : ce.getEgressPoints()){
            if(EgrPointTypeDef.isFugitive(eg2)) continue;
            // Count as stack emissions
            ControlInfoRow jRow = new ControlInfoRow();
            jRow.setStreamLabel(stream + "." + subLabel);
            subLabel++;
            FacilityEmissionFlow fef3; 
            fef3 = FacilityEmissionFlow.getEmissionFlow(fefCeNormal,
                    FacilityEmissionFlow.STACK_TYPE, eg2.getReleasePointId());
            jRow.flowFactor = fef3.getFlowFactor();
            jRow.flowFactorStr = fef3.getFlowFactorStr();
            jRow.percent = fef3.getPercentValue();
            jRow.percentStr = fef3.getPercent();
            jRow.type = fef3.getType();
            jRow.setDeviceName(eg2.getReleasePointId());
            jRow.stack = eg2;
            newFrac = iRow.getOutput() * fef3.getPercentValue()/100;
            jRow.setInput(newFrac);
            jRow.setOutput(newFrac);
            jRow.setFugitiveFraction(0d);
            jRow.setCaptureEff(100d);
            jRow.setStackFraction(newFrac);
            matrix.add(jRow);
        }
        for(ControlEquipment ce2 : ce.getControlEquips()){
            // Recurse for each ce under it
            FacilityEmissionFlow fef2 = null;
            fef2 = FacilityEmissionFlow.getEmissionFlow(fefCeNormal,
                    FacilityEmissionFlow.CE_TYPE, ce2.getControlEquipmentId());
            newFrac = iRow.getOutput() * fef2.getPercentValue()/100;
            if(!ControlInfoRow.processNext(facility, depthCnt + 1, matrix, ce2,
                    annualAdjust, annualAdjustV, hoursPerYear, pollutantCd,  stream + "." + subLabel,
                    fef2, newFrac, forceFug, forceStk)) return false;
            subLabel++;
        }
        return true;
    }
    
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getStreamLabel() {
        return streamLabel;
    }

    public void setStreamLabel(String streamLabel) {
        this.streamLabel = streamLabel;
    }

    public String getPollutantControlCd() {
        return pollutantControlCd;
    }

    public void setPollutantControlCd(String pollutantControlCd) {
        this.pollutantControlCd = pollutantControlCd;
    }

    public String getAnnualAdjust() {
        return annualAdjust;
    }

    private void setAnnualAdjust(String annualAdjust) {
        this.annualAdjust = annualAdjust;
    }

    public double getCaptureEff() {
        return captureEff;
    }

    private void setCaptureEff(double captureEff) {
        this.captureEff = captureEff;
    }

    public double getInput() {
        return input;
    }

    public void setInput(double input) {
        this.input = input;
        inputStr = EmissionsReport.numberToString(input);
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
        outputC = EmissionsReport.numberToString(this.output);
    }

    public Double getFugitiveFraction() {
        return fugitiveFraction;
    }

    private void setFugitiveFraction(Double fugitiveFraction) {
        this.fugitiveFraction = fugitiveFraction;
        fugitiveFractionC = EmissionsReport.numberToString(this.fugitiveFraction);
    }

    public Double getStackFraction() {
        return stackFraction;
    }

    public void setStackFraction(Double stackFraction) {
        this.stackFraction = stackFraction;
        stackFractionC = EmissionsReport.numberToString(this.stackFraction);
    }

    public Double getOperContEff() {
        return operContEff;
    }

    private void setOperContEff(Double operContEff) {
        this.operContEff = operContEff;
    }

    public String getFugitiveFractionC() {
        return fugitiveFractionC;
    }

    public String getStackFractionC() {
        return stackFractionC;
    }

    public String getOutputC() {
        return outputC;
    }

    public Float getFlowFactor() {
        return flowFactor;
    }

    public Float getPercent() {
        return percent;
    }

    public String getType() {
        return type;
    }

    public String getInputStr() {
        return inputStr;
    }

    public String getFlowFactorStr() {
        return flowFactorStr;
    }

    public String getPercentStr() {
        return percentStr;
    }

    public EgressPoint getStack() {
        return stack;
    }

    public static StringBuffer getProblems() {
        return problems;
    }
    public static boolean isProblems() {
        return problems.length() > 0;
    }
}


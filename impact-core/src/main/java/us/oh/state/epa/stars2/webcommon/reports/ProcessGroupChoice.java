package us.oh.state.epa.stars2.webcommon.reports;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;

public class ProcessGroupChoice {
    private EmissionsReportPeriod period;
    private String processId;
    private String scc;
    private String epaEmuId;
    private String description;

    
    public ProcessGroupChoice(EmissionsReportPeriod p, EmissionsReportEU eu) {
        this.period = p;
        this.processId = p.getTreeLabel();
        this.scc = p.convertSCC();
        this.epaEmuId = eu.getEpaEmuId();
        this.description = p.getTreeLabelDesc();
    }

    public String getDescription() {
        return description;
    }

    public String getEpaEmuId() {
        return epaEmuId;
    }

    public String getProcessId() {
        return processId;
    }

    public String getScc() {
        return scc;
    }

    public EmissionsReportPeriod getPeriod() {
        return period;
    }    
}
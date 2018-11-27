package us.oh.state.epa.stars2.app.reports;

import java.text.DecimalFormat;
import us.oh.state.epa.stars2.def.DOLAA;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;

public class ReportReviewData {
    String doLaa;
    String rptCategory;
    int completionDays;
    String metric;
    int rptsCompleted;
    int totRpts;
    
    ReportReviewData(String doLaa, String rptCategory, int completionDays, int rptsLate, int totRpts) {
        this.doLaa = DOLAA.getData().getItems().getItemDesc(doLaa);
        this.rptCategory = EmissionReportsRealDef.getData().getItems().getItemDesc(rptCategory);
        this.completionDays = completionDays;
        this.rptsCompleted = totRpts - rptsLate;
        this.totRpts = totRpts;
        metric = null;
        if(this.totRpts > 0) {
            float floatMetric = 100f*this.rptsCompleted/this.totRpts;
            metric = measurementConvertToTenths(floatMetric) + "%";
        }
    }
    
    public String toString() {
        return rptCategory  + " " + completionDays + " Day Timely Completion = " + 
            (metric==null?"--%":metric) + " (" + rptsCompleted + "/" + totRpts + ")";
    }
    
    static String measurementConvertToTenths(float meas) {
        String format = "###0.0";
        DecimalFormat decFormat = new DecimalFormat(format);
        return decFormat.format(meas);
    }

    public int getCompletionDays() {
        return completionDays;
    }

    public String getDoLaa() {
        return doLaa;
    }

    public String getMetric() {
        return metric;
    }

    public String getRptCategory() {
        return rptCategory;
    }

    public int getRptsCompleted() {
        return rptsCompleted;
    }

    public int getTotRpts() {
        return totRpts;
    }
}

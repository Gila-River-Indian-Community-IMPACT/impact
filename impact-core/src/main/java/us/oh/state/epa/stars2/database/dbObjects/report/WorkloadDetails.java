package us.oh.state.epa.stars2.database.dbObjects.report;

@SuppressWarnings("serial")
public class WorkloadDetails implements java.io.Serializable {
    private String activityName;
    private Integer userId;
    private String doLaaName;
    private String doLaaCd;
    private String doLaaShortDsc;
    private String firstNm;
    private String lastNm;
    private Integer ptiWorking;
    private Integer ptiDraft;
    private Integer ptioWorking;
    private Integer ptioDraft;
    private Integer tivWorking;
    private Integer tivPpp;
    private Integer tivPp;
    private Integer tivDraft;
    private Integer tvWorking;
    private Integer tvPpp;
    private Integer tvPp;
    private Integer tvDraft;
    private Integer total;
    private Integer ptiWorkingNow;
    private Integer ptiDraftNow;
    private Integer ptioWorkingNow;
    private Integer ptioDraftNow;
    private Integer tivWorkingNow;
    private Integer tivPppNow;
    private Integer tivPpNow;
    private Integer tiivDraftNow;
    private Integer tivDraftNow;
    private Integer tvWorkingNow;
    private Integer tvPppNow;
    private Integer tvPpNow;
    private Integer tvDraftNow;
    private Integer totalNow;
    private Integer ptiWorkingPre;
    private Integer ptiDraftPre;
    private Integer ptioWorkingPre;
    private Integer ptioDraftPre;
    private Integer tivWorkingPre;
    private Integer tivPppPre;
    private Integer tivPpPre;
    private Integer tivDraftPre;
    private Integer tvWorkingPre;
    private Integer tvPppPre;
    private Integer tvPpPre;
    private Integer tvDraftPre;
    private Integer totalPre;

    public WorkloadDetails() {
        ptiDraft = new Integer(0);
        ptiWorking = new Integer(0);
        ptioDraft = new Integer(0);
        ptioWorking = new Integer(0);
        tivDraft = new Integer(0);
        tivPpp = new Integer(0);
        tivPp = new Integer(0);
        tivWorking = new Integer(0);
        tvDraft = new Integer(0);
        tvPpp = new Integer(0);
        tvPp = new Integer(0);
        tvWorking = new Integer(0);
        total = new Integer(0);

        ptiDraftNow = new Integer(0);
        ptiWorkingNow = new Integer(0);
        ptioDraftNow = new Integer(0);
        ptioWorkingNow = new Integer(0);
        tivDraftNow = new Integer(0);
        tivPppNow = new Integer(0);
        tivPpNow = new Integer(0);
        tivWorkingNow = new Integer(0);
        tvDraftNow = new Integer(0);
        tvPppNow = new Integer(0);
        tvPpNow = new Integer(0);
        tvWorkingNow = new Integer(0);
        totalNow = new Integer(0);

        ptiDraftPre = new Integer(0);
        ptiWorkingPre = new Integer(0);
        ptioDraftPre = new Integer(0);
        ptioWorkingPre = new Integer(0);
        tivDraftPre = new Integer(0);
        tivPppPre = new Integer(0);
        tivPpPre = new Integer(0);
        tivWorkingPre = new Integer(0);
        tvDraftPre = new Integer(0);
        tvPppPre = new Integer(0);
        tvPpPre = new Integer(0);
        tvWorkingPre = new Integer(0);
        totalPre = new Integer(0);
    }

    public final String getActivityName() {
        return activityName;
    }

    public final void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final Integer getPtiDraft() {
        return ptiDraft;
    }

    public final void setPtiDraft(Integer ptiDraft) {
        this.ptiDraft = ptiDraft;
    }

    public final Integer getPtioDraft() {
        return ptioDraft;
    }

    public final void setPtioDraft(Integer ptioDraft) {
        this.ptioDraft = ptioDraft;
    }

    public final Integer getTvDraft() {
        return tvDraft;
    }

    public final void setTvDraft(Integer tvDraft) {
        this.tvDraft = tvDraft;
    }

    public final Integer getTvPpp() {
        return tvPpp;
    }

    public final void setTvPpp(Integer tvPpp) {
        this.tvPpp = tvPpp;
    }

    public final Integer getTvPp() {
        return tvPp;
    }

    public final void setTvPp(Integer tvPp) {
        this.tvPp = tvPp;
    }

    public final Integer getPtioWorking() {
        return ptioWorking;
    }

    public final void setPtioWorking(Integer ptioWorking) {
        this.ptioWorking = ptioWorking;
    }

    public final Integer getPtiWorking() {
        return ptiWorking;
    }

    public final void setPtiWorking(Integer ptiWorking) {
        this.ptiWorking = ptiWorking;
    }

    public final Integer getTotal() {
        return total;
    }

    public final void setTotal(Integer total) {
        this.total = total;
    }

    public final Integer getTvWorking() {
        return tvWorking;
    }

    public final void setTvWorking(Integer tvWorking) {
        this.tvWorking = tvWorking;
    }

    public final String getFirstNm() {
        return firstNm;
    }

    public final void setFirstNm(String firstNm) {
        this.firstNm = firstNm;
    }

    public final String getLastNm() {
        return lastNm;
    }

    public final void setLastNm(String lastNm) {
        this.lastNm = lastNm;
    }

    public final String getUserName() {
        if (lastNm != null) {
            return lastNm + "," + firstNm;
        }

        return " ";
    }

    public final Integer getPtiDraftNow() {
        return ptiDraftNow;
    }

    public final void setPtiDraftNow(Integer ptiDraftNow) {
        this.ptiDraftNow = ptiDraftNow;
    }

    public final Integer getPtioDraftNow() {
        return ptioDraftNow;
    }

    public final void setPtioDraftNow(Integer ptioDraftNow) {
        this.ptioDraftNow = ptioDraftNow;
    }

    public final Integer getPtioWorkingNow() {
        return ptioWorkingNow;
    }

    public final void setPtioWorkingNow(Integer ptioWorkingNow) {
        this.ptioWorkingNow = ptioWorkingNow;
    }

    public final Integer getPtiWorkingNow() {
        return ptiWorkingNow;
    }

    public final void setPtiWorkingNow(Integer ptiWorkingNow) {
        this.ptiWorkingNow = ptiWorkingNow;
    }

    public final Integer getTotalNow() {
        return totalNow;
    }

    public final void setTotalNow(Integer totalNow) {
        this.totalNow = totalNow;
    }

    public final Integer getTvDraftNow() {
        return tvDraftNow;
    }

    public final void setTvDraftNow(Integer tvDraftNow) {
        this.tvDraftNow = tvDraftNow;
    }

    public final Integer getTvPpNow() {
        return tvPpNow;
    }

    public final void setTvPpNow(Integer tvPpNow) {
        this.tvPpNow = tvPpNow;
    }

    public final Integer getTvPppNow() {
        return tvPppNow;
    }

    public final void setTvPppNow(Integer tvPppNow) {
        this.tvPppNow = tvPppNow;
    }

    public final Integer getTvWorkingNow() {
        return tvWorkingNow;
    }

    public final void setTvWorkingNow(Integer tvWorkingNow) {
        this.tvWorkingNow = tvWorkingNow;
    }

    public final String getPtiWorkingDsp() {
        // return ptiWorkingPre + ":" + ptiWorkingNow + ":" + ptiWorking;
        return ptiWorkingPre + ":" + ptiWorkingNow;
    }

    public final String getPtiDraftDsp() {
        // return ptiDraftPre + ":" + ptiDraftNow + ":" + ptiDraft;
        return ptiDraftPre + ":" + ptiDraftNow;
    }

    public final String getPtioWorkingDsp() {
        // return ptioWorkingPre + ":" + ptioWorkingNow + ":" + ptioWorking;
        return ptioWorkingPre + ":" + ptioWorkingNow;
    }

    public final String getPtioDraftDsp() {
        // return ptioDraftPre + ":" + ptioDraftNow + ":" + ptioDraft;
        return ptioDraftPre + ":" + ptioDraftNow;
    }

    public final String getTvWorkingDsp() {
        // return tvWorkingPre + ":" + tvWorkingNow + ":" + tvWorking;
        return tvWorkingPre + ":" + tvWorkingNow;
    }

    public final String getTvDraftDsp() {
        // return tvDraftPre + ":" + tvDraftNow + ":" + tvDraft;
        return tvDraftPre + ":" + tvDraftNow;
    }

    public final String getTvPppDsp() {
        // return tvPppPre + ":" + tvPppNow + ":" + tvPpp;
        return tvPppPre + ":" + tvPppNow;
    }

    public final String getTvPpDsp() {
        // return tvPpPre + ":" + tvPpNow + ":" + tvPp;
        return tvPpPre + ":" + tvPpNow;
    }

    public final String getTotalDsp() {
        // return totalPre + ":" + totalNow + ":" + total;
        return totalPre + ":" + totalNow;
    }

    public final String getDoLaaName() {
        return doLaaName;
    }

    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    public final String getDoLaaCdDsp() {
        //
        // Somewhat of a kludge. If we are at the Row Total - do not
        // display a list of Do/LAAs
        //
        if (doLaaCd.indexOf(",") >= 0) {
            return "";
        }

        return doLaaShortDsc;
    }

    public final String getDoLaaCd() {
        return doLaaCd;
    }

    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public final Integer getPtiDraftPre() {
        return ptiDraftPre;
    }

    public final void setPtiDraftPre(Integer ptiDraftPre) {
        this.ptiDraftPre = ptiDraftPre;
    }

    public final Integer getPtioDraftPre() {
        return ptioDraftPre;
    }

    public final void setPtioDraftPre(Integer ptioDraftPre) {
        this.ptioDraftPre = ptioDraftPre;
    }

    public final Integer getPtioWorkingPre() {
        return ptioWorkingPre;
    }

    public final void setPtioWorkingPre(Integer ptioWorkingPre) {
        this.ptioWorkingPre = ptioWorkingPre;
    }

    public final Integer getPtiWorkingPre() {
        return ptiWorkingPre;
    }

    public final void setPtiWorkingPre(Integer ptiWorkingPre) {
        this.ptiWorkingPre = ptiWorkingPre;
    }

    public final Integer getTotalPre() {
        return totalPre;
    }

    public final void setTotalPre(Integer totalPre) {
        this.totalPre = totalPre;
    }

    public final Integer getTvDraftPre() {
        return tvDraftPre;
    }

    public final void setTvDraftPre(Integer tvDraftPre) {
        this.tvDraftPre = tvDraftPre;
    }

    public final Integer getTvPppPre() {
        return tvPppPre;
    }

    public final void setTvPppPre(Integer tvPppPre) {
        this.tvPppPre = tvPppPre;
    }

    public final Integer getTvPpPre() {
        return tvPpPre;
    }

    public final void setTvPpPre(Integer tvPpPre) {
        this.tvPpPre = tvPpPre;
    }

    public final Integer getTvWorkingPre() {
        return tvWorkingPre;
    }

    public final void setTvWorkingPre(Integer tvWorkingPre) {
        this.tvWorkingPre = tvWorkingPre;
    }

    public final Integer getTivWorking() {
        return tivWorking;
    }

    public final void setTivWorking(Integer tivWorking) {
        this.tivWorking = tivWorking;
    }

    public final Integer getTivPpp() {
        return tivPpp;
    }

    public final void setTivPpp(Integer tivPpp) {
        this.tivPpp = tivPpp;
    }

    public final Integer getTivPp() {
        return tivPp;
    }

    public final void setTivPp(Integer tivPp) {
        this.tivPp = tivPp;
    }

    public final Integer getTivDraft() {
        return tivDraft;
    }

    public final void setTivDraft(Integer tivDraft) {
        this.tivDraft = tivDraft;
    }

    public final Integer getTivWorkingNow() {
        return tivWorkingNow;
    }

    public final void setTivWorkingNow(Integer tivWorkingNow) {
        this.tivWorkingNow = tivWorkingNow;
    }

    public final Integer getTivDraftNow() {
        return tivDraftNow;
    }

    public final void setTivDraftNow(Integer tivDraftNow) {
        this.tivDraftNow = tivDraftNow;
    }

    public final Integer getTivWorkingPre() {
        return tivWorkingPre;
    }

    public final void setTivWorkingPre(Integer tivWorkingPre) {
        this.tivWorkingPre = tivWorkingPre;
    }

    public final Integer getTivPppPre() {
        return tivPppPre;
    }

    public final void setTivPppPre(Integer tivPppPre) {
        this.tivPppPre = tivPppPre;
    }

    public final Integer getTivPpPre() {
        return tivPpPre;
    }

    public final void setTivPpPre(Integer tivPpPre) {
        this.tivPpPre = tivPpPre;
    }

    public final Integer getTivDraftPre() {
        return tivDraftPre;
    }

    public final void setTivDraftPre(Integer tivDraftPre) {
        this.tivDraftPre = tivDraftPre;
    }

    public final Integer getTivPppNow() {
        return tivPppNow;
    }

    public final void setTivPppNow(Integer tivPppNow) {
        this.tivPppNow = tivPppNow;
    }

    public final Integer getTivPpNow() {
        return tivPpNow;
    }

    public final void setTivPpNow(Integer tivPpNow) {
        this.tivPpNow = tivPpNow;
    }

    public final Integer getTiivDraftNow() {
        return tiivDraftNow;
    }

    public final void setTiivDraftNow(Integer tiivDraftNow) {
        this.tiivDraftNow = tiivDraftNow;
    }

    public String getDoLaaShortDsc() {
        return doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        this.doLaaShortDsc = doLaaShortDsc;
    }

}

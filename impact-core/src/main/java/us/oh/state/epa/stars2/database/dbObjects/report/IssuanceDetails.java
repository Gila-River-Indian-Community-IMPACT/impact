package us.oh.state.epa.stars2.database.dbObjects.report;

public class IssuanceDetails implements java.io.Serializable {
    private String doLaaCd;
    private String doLaaShortDsc;
    private String doLaaName;
    //private Integer ptiDraft;
    //private Integer ptiFinal;
    private Integer ptioDraft;
    private Integer ptioFinal;
    private Integer tvDraft;
    //private Integer tvPpp;
    private Integer tvPp;
    private Integer tvFinal;
    //private Integer pbr;
    //private Integer totalPti;
    private Integer totalPtio;
    private Integer totalTv;
    //private Integer totalPbr;
    
    //private Integer spto;
    //private Integer totalSpto;
    //private Integer reg;
    //private Integer totalReg;

    public IssuanceDetails() {
        //ptiDraft = new Integer(0);
        //ptiFinal = new Integer(0);
        ptioDraft = new Integer(0);
        ptioFinal = new Integer(0);
        tvDraft = new Integer(0);
        //tvPpp = new Integer(0);
        tvPp = new Integer(0);
        tvFinal = new Integer(0);
        //pbr = new Integer(0);
        //totalPti = new Integer(0);
        totalPtio = new Integer(0);
        totalTv = new Integer(0);
        //totalPbr = new Integer(0);
        
       // spto = new Integer(0);
        //totalSpto = new Integer(0);
        //reg = new Integer(0);
        //totalReg = new Integer(0);
    }

    /**
     * Getter for property doLaaCd.
     * 
     * @return Value of property doLaaCd.
     * 
     */
    public final String getDoLaaCd() {
        return doLaaCd;
    }

    /**
     * Setter for property doLaaCd.
     * 
     * @param doLaaCd
     *            New value of property doLaaCd.
     * 
     */
    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    /**
     * Getter for property doLaaName.
     * 
     * @return Value of property doLaaName.
     * 
     */
    public final String getDoLaaName() {
        return doLaaName;
    }

    /**
     * Setter for property doLaaName.
     * 
     * @param doLaaName
     *            New value of property doLaaCd.
     * 
     */
    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    //public final Integer getPtiDraft() {
    //    return ptiDraft;
    //}

    //public final void setPtiDraft(Integer ptiDraft) {
    //    this.ptiDraft = ptiDraft;
    //}

    //public final Integer getPtiFinal() {
    //    return ptiFinal;
    //}

    //public final void setPtiFinal(Integer ptiFinal) {
    //    this.ptiFinal = ptiFinal;
   // }

    public final Integer getPtioDraft() {
        return ptioDraft;
    }

    public final void setPtioDraft(Integer ptioDraft) {
        this.ptioDraft = ptioDraft;
    }

    public final Integer getPtioFinal() {
        return ptioFinal;
    }

    public final void setPtioFinal(Integer ptioFinal) {
        this.ptioFinal = ptioFinal;
    }

    public final Integer getTvDraft() {
        return tvDraft;
    }

    public final void setTvDraft(Integer tvDraft) {
        this.tvDraft = tvDraft;
    }

    //public final Integer getTvPpp() {
    //    return tvPpp;
    //}

    //public final void setTvPpp(Integer tvPpp) {
    //    this.tvPpp = tvPpp;
    //}

    public final Integer getTvPp() {
        return tvPp;
    }

    public final void setTvPp(Integer tvPp) {
        this.tvPp = tvPp;
    }

    public final Integer getTvFinal() {
        return tvFinal;
    }

    public final void setTvFinal(Integer tvFinal) {
        this.tvFinal = tvFinal;
    }

    //public final Integer getPbr() {
    //    return pbr;
    //}

    //public final void setPbr(Integer pbr) {
    //    this.pbr = pbr;
    //}

    //public final Integer getTotalPti() {
    //    return totalPti;
   // }

    //public final void setTotalPti(Integer totalPti) {
    //    this.totalPti = totalPti;
    //}

    public final Integer getTotalPtio() {
        return totalPtio;
    }

    public final void setTotalPtio(Integer totalPtio) {
        this.totalPtio = totalPtio;
    }

    public final Integer getTotalTv() {
        return totalTv;
    }

    public final void setTotalTv(Integer totalTv) {
        this.totalTv = totalTv;
    }

    //public final Integer getTotalPbr() {
    //    return totalPbr;
    //}

    //public final void setTotalPbr(Integer totalPbr) {
    //    this.totalPbr = totalPbr;
    //}

    /**
     * @return the totalReg
     */
    //public final Integer getTotalReg() {
    //    return totalReg;
    //}

    /**
     * @param totalReg the totalReg to set
     */
    //public final void setTotalReg(Integer totalReg) {
    //    this.totalReg = totalReg;
    //}

    /**
     * @return the totalSpto
     */
    //public final Integer getTotalSpto() {
    //    return totalSpto;
    //}

    /**
     * @param totalSpto the totalSpto to set
     */
    //public final void setTotalSpto(Integer totalSpto) {
    //    this.totalSpto = totalSpto;
    //}

    /**
     * @return the reg
     */
    //public final Integer getReg() {
    //    return reg;
    //}

    /**
     * @param reg the reg to set
     */
    //public final void setReg(Integer reg) {
    //    this.reg = reg;
    //}

    /**
     * @return the spto
     */
    //public final Integer getSpto() {
    //    return spto;
    //}

    /**
     * @param spto the spto to set
     */
    //public final void setSpto(Integer spto) {
    //    this.spto = spto;
    //}

    public String getDoLaaShortDsc() {
        return doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        this.doLaaShortDsc = doLaaShortDsc;
    }
}

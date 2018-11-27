package us.oh.state.epa.stars2.database.dbObjects.report;

@SuppressWarnings("serial")
public class PermitExpirationDetails implements java.io.Serializable {
    private String doLaaCd;
    private String doLaaShortDsc;
    private String doLaaName;

    //private Integer spto;
    private Integer ptio;
    private Integer tv;

    public PermitExpirationDetails() {
    	//spto = new Integer(0);
        ptio = new Integer(0);
        tv = new Integer(0);
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

    /**
     * @return the pti
     */
    //public final Integer getSpto() {
    //    return spto;
   //}

    /**
     * @param pti the pti to set
     */
    //public final void setSpto(Integer spto) {
    //    this.spto = spto;
    //}

    /**
     * @return the ptio
     */
    public final Integer getPtio() {
        return ptio;
    }

    /**
     * @param ptio the ptio to set
     */
    public final void setPtio(Integer ptio) {
        this.ptio = ptio;
    }

    /**
     * @return the total
     */
    public final Integer getTotal() {
        //return spto+ptio+tv;
        return ptio+tv;
    }

    /**
     * @return the tv
     */
    public final Integer getTv() {
        return tv;
    }

    /**
     * @param tv the tv to set
     */
    public final void setTv(Integer tv) {
        this.tv = tv;
    }

    public String getDoLaaShortDsc() {
        return doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        this.doLaaShortDsc = doLaaShortDsc;
    }

}
